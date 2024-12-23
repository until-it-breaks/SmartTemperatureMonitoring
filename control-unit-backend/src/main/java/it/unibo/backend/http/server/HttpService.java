package it.unibo.backend.http.server;

import java.util.ArrayDeque;
import java.util.Deque;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import it.unibo.backend.Settings.Connectivity;
import it.unibo.backend.Settings.JsonUtility;
import it.unibo.backend.enums.OperatingMode;
import it.unibo.backend.enums.SystemState;
import it.unibo.backend.temperature.TemperatureReport;
import it.unibo.backend.temperature.TemperatureSample;

/**
 * {@code HttpService} is a Vert.x-based service that provides an HTTP server with multiple 
 * RESTful endpoints for handling temperature data, reports, and system configuration.
 * It uses Vert.x's event loop for processing HTTP requests and responses asynchronously.
 * 
 * <p>The service includes the following functionalities:</p>
 * <ul>
 *     <li>Adding and retrieving temperature samples</li>
 *     <li>Adding and retrieving periodic temperature reports</li>
 *     <li>Updating and retrieving the system's operating mode</li>
 *     <li>Updating and retrieving the need for operator intervention</li>
 *     <li>Updating and retrieving system configuration data (e.g., window level, frequency multiplier)</li>
 * </ul>
 * 
 * <p>Since Vert.x operates on a single event loop, the handlers for HTTP requests are not thread-safe.
 * All HTTP request handling happens sequentially on the event loop, and care should be taken
 * to avoid race conditions if accessing shared resources within the handlers.</p>
 * 
 * <p>The class is designed to be run as a Vert.x verticle and can handle multiple simultaneous
 * HTTP requests asynchronously, efficiently managing temperature data, reports, and configuration settings.</p>
 */
public class HttpService extends AbstractVerticle {
    private static final Logger logger = LoggerFactory.getLogger(HttpService.class);
    private static final int MAX_SAMPLES = 100;     // Max number of temperature samples
    private static final int MAX_REPORTS = 20;      // Max number of periodic reports (contains avg, min, max)

    private final String host;
    private final int port;

    // Deque are more efficient than LinkedList most of the time.
    private final Deque<TemperatureSample> samples; // Stores individual temperature samples
    private final Deque<TemperatureReport> reports; // Stores periodical average, min, max

    private Double freqMultiplier;                  // The sampling frequency multiplier
    private Double windowLevel;                     // The window opening leve (percentage). Ideally between 0.0 and 1.0

    private String operatingMode;                   // The window operatin mode (AUTO/MANUAL)
    private String state;                           // The system state (NORMAL, HOT, TOO_HOT, ALARM)
    private boolean interventionRequired;           // Flag indicating whether an operator's intervention is needed.

    public HttpService(final String host, final int port) {
        this.host = host;
        this.port = port;
        this.samples = new ArrayDeque<>();
        this.reports = new ArrayDeque<>();
        this.freqMultiplier = null;
        this.windowLevel = null;
        this.operatingMode = OperatingMode.AUTO.getName();
        this.state = SystemState.NORMAL.getName();
        this.interventionRequired = false;
    }

    @Override
    public void start() {
        logger.info("Starting Service on {}:{}", host, port);
        final Router router = Router.router(vertx);
        router.route().handler(BodyHandler.create());

        // Creating endpoints RESTful endpoints
        router.post(Connectivity.TEMPERATURE_PATH).handler(this::handleAddTemperatureSample);
        router.get(Connectivity.TEMPERATURE_PATH).handler(this::handleGetTemperatureSamples);

        router.post(Connectivity.REPORTS_PATH).handler(this::handleAddTemperatureReport);
        router.get(Connectivity.REPORTS_PATH).handler(this::handleGetTemperatureReports);

        router.post(Connectivity.OPERATING_MODE_PATH).handler(this::handleUpdateOperatingMode);
        router.get(Connectivity.OPERATING_MODE_PATH).handler(this::handleGetOperatingMode);

        router.post(Connectivity.INTERVENTION_PATH).handler(this::handleUpdateInterventionNeed);
        router.get(Connectivity.INTERVENTION_PATH).handler(this::handleGetInterventionNeed);

        router.post(Connectivity.CONFIG_PATH).handler(this::handleUpdateConfigData);
        router.get(Connectivity.CONFIG_PATH).handler(this::handleGetConfigData);

        vertx.createHttpServer().requestHandler(router).listen(port, host, res -> {
            if (res.succeeded()) {
                logger.info("Service started successfully");
            } else {
                logger.error("Failed to start service", res.cause().getMessage());
            }
        });
    }

    private void handleAddTemperatureSample(final RoutingContext routingContext) {
        logger.info("Request to add a temperature sample from {}", getHost(routingContext.request()));

        final HttpServerResponse response = routingContext.response();
        final JsonObject data = routingContext.body().asJsonObject();

        if (data == null) {
            logger.warn("Temperature sample body is null");
            response.setStatusCode(400).end();
        } else {
            if (samples.size() > MAX_SAMPLES) {
                logger.info("Sample list size exceeded {}. Removing oldest sample", MAX_REPORTS);
                samples.pollFirst();
            }
            samples.offerLast(new TemperatureSample(data.getDouble(JsonUtility.TEMPERATURE), data.getLong(JsonUtility.SAMPLE_TIME)));
            response.setStatusCode(200).end();
        }
    }

    private void handleGetTemperatureSamples(final RoutingContext routingContext) {
        logger.info("Request for temperature samples from {}", getHost(routingContext.request()));

        final JsonArray array = new JsonArray();
        for (final TemperatureSample value: samples) {
            final JsonObject data = new JsonObject();
            data.put(JsonUtility.TEMPERATURE, value.getTemperature());
            data.put(JsonUtility.SAMPLE_TIME, value.getTimeStamp());
            array.add(data);
        }
        routingContext.response().putHeader("Content-Type", "application/json").end(array.encodePrettily());
    }

    private void handleAddTemperatureReport(final RoutingContext routingContext) {
        logger.info("Request to add temperature report from {}", getHost(routingContext.request()));

        final HttpServerResponse response = routingContext.response();
        final JsonObject data = routingContext.body().asJsonObject();

        if (data == null) {
            logger.warn("Temperature report body is null");
            response.setStatusCode(400).end();
        } else {
            if (reports.size() > MAX_REPORTS) {
                logger.info("Report list size exceeded {}. Removing oldest report", MAX_REPORTS);
                reports.pollFirst();
            }
            final TemperatureReport temperatureReport = new TemperatureReport();
            temperatureReport.setStartTime(data.getLong(JsonUtility.START_TIME));
            temperatureReport.setEndTime(data.getLong(JsonUtility.END_TIME));
            temperatureReport.setAverage(data.getDouble(JsonUtility.AVG_TEMP));
            temperatureReport.setMax(data.getDouble(JsonUtility.MAX_TEMP));
            temperatureReport.setMin(data.getDouble(JsonUtility.MIN_TEMP));
            reports.offerLast(temperatureReport);
            response.setStatusCode(200).end();
        }
    }

    private void handleGetTemperatureReports(final RoutingContext routingContext) {
        logger.info("Request for temperature reports from {}", getHost(routingContext.request()));

        final JsonArray array = new JsonArray();
        for (final TemperatureReport report: reports) {
            final JsonObject data = new JsonObject();
            data.put(JsonUtility.START_TIME, report.getStartTime());
            data.put(JsonUtility.END_TIME, report.getEndTime());
            data.put(JsonUtility.AVG_TEMP, report.getAverage());
            data.put(JsonUtility.MAX_TEMP, report.getMax());
            data.put(JsonUtility.MIN_TEMP, report.getMin());
            array.add(data);
        }
        routingContext.response().putHeader("Content-Type", "application/json").end(array.encodePrettily());
    }

    private void handleUpdateOperatingMode(final RoutingContext routingContext) {
        logger.info("Request to update operating mode from {}", getHost(routingContext.request()));

        final HttpServerResponse response = routingContext.response();
        final JsonObject data = routingContext.body().asJsonObject();
        if (data == null) {
            logger.warn("Operating mode body is null");
            response.setStatusCode(400).end();
        } else {
            this.operatingMode = data.getString(JsonUtility.OPERATING_MODE);
            logger.info("Updated operating mode to: {}", this.operatingMode);
            response.setStatusCode(200).end();
        }
    }

    private void handleGetOperatingMode(final RoutingContext routingContext) {
        logger.info("Request for operating mode from {}", getHost(routingContext.request()));

        final JsonObject data = new JsonObject();
        data.put(JsonUtility.OPERATING_MODE, this.operatingMode);
        routingContext.response().putHeader("Content-Type", "application/json").end(data.encodePrettily());
    }

    private void handleUpdateInterventionNeed(final RoutingContext routingContext) {
        logger.info("Request to update [intervention need] from {}", getHost(routingContext.request()));

        final HttpServerResponse response = routingContext.response();
        final JsonObject data = routingContext.body().asJsonObject();
        if (data == null) {
            logger.warn("Intervention body is null");
            response.setStatusCode(400).end();
        } else {
            this.interventionRequired = data.getBoolean(JsonUtility.INTERVENTION_NEED);
            logger.info("Updated [intervention need] to: {}", this.interventionRequired);
            response.setStatusCode(200).end();
        }
    }

    private void handleGetInterventionNeed(final RoutingContext routingContext) {
        logger.info("Request for [intervention need] status from {}", getHost(routingContext.request()));

        final JsonObject data = new JsonObject();
        data.put(JsonUtility.INTERVENTION_NEED, this.interventionRequired);
        routingContext.response().putHeader("Content-Type", "application/json").end(data.encodePrettily());
    }

    private void handleUpdateConfigData(final RoutingContext routingContext) {
        logger.info("Request to update configuration data from {}", getHost(routingContext.request()));

        final HttpServerResponse response = routingContext.response();
        final JsonObject data = routingContext.body().asJsonObject();
        if (data == null) {
            response.setStatusCode(400);
            logger.warn("Config body is null");
            response.end();
        } else {
            this.windowLevel = data.getDouble(JsonUtility.WINDOW_LEVEL);
            this.state = data.getString(JsonUtility.SYSTEM_STATE);
            this.freqMultiplier = data.getDouble(JsonUtility.FREQ_MULTIPLIER);
            logger.info("Updated config data: windowLevel = {}; state = {}; frequencyMultiplier = {}", this.windowLevel, this.state, this.freqMultiplier);
            response.setStatusCode(200).end();
        }
    }

    private void handleGetConfigData(final RoutingContext routingContext) {
        logger.info("Request for configuration data from {}", getHost(routingContext.request()));

        final JsonObject data = new JsonObject();
        data.put(JsonUtility.WINDOW_LEVEL, this.windowLevel);
        data.put(JsonUtility.SYSTEM_STATE, this.state);
        data.put(JsonUtility.FREQ_MULTIPLIER, this.freqMultiplier);
        routingContext.response().putHeader("Content-Type", "application/json").end(data.encodePrettily());
    }

    private String getHost(final HttpServerRequest request) {
        final String host = request.getHeader("X-Forwarded-For");
        if (host == null) {
            return request.remoteAddress().host();
        } else {
            return host;
        }
    }
}
