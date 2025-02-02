package it.unibo.backend.http.server;

import java.util.ArrayDeque;
import java.util.Deque;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.CorsHandler;
import it.unibo.backend.Settings.Connectivity;
import it.unibo.backend.Settings.FreqMultiplier;
import it.unibo.backend.Settings.JsonUtility;
import it.unibo.backend.Settings.WindowLevel;
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
 *     <li>Updating and retrieving system configuration data (e.g., window level, frequency multiplier, operating mode and need for intervention)</li>
 *     <li>Updating and retrieving requests for alarm switch</li>
 *     <li>Updating and retrieving request for mode switch</li>
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
    private String operatingMode;                   // The current window operating mode (auto/manual)
    private String state;                           // The system state (normal, hot, too_hot, alarm)
    private boolean interventionRequired;           // Indicates whether an operator's intervention is needed.

    private String modeToSwitchTo;                  // Indicates the mode requested to switch to.
    private boolean switchOffAlarm;                        // Indicates a request to switch the alarm ON/OFF.

    public HttpService(final String host, final int port) {
        this.host = host;
        this.port = port;
        this.samples = new ArrayDeque<>();
        this.reports = new ArrayDeque<>();
        this.freqMultiplier = FreqMultiplier.NORMAL;
        this.windowLevel = WindowLevel.FULLY_CLOSED;
        this.operatingMode = OperatingMode.AUTO.getName();
        this.state = SystemState.NORMAL.getName();
        this.interventionRequired = false;
        this.modeToSwitchTo = OperatingMode.NONE.getName();
        this.switchOffAlarm = false;
    }

    @SuppressWarnings("deprecation")
    @Override
    public void start() {
        logger.info("Starting Service on {}:{}", host, port);
        final Router router = Router.router(vertx);
        router.route().handler(BodyHandler.create());

        router.route().handler(CorsHandler.create("*")
        .allowedMethod(HttpMethod.GET)
        .allowedMethod(HttpMethod.POST));

        // Creating endpoints RESTful endpoints
        router.post(Connectivity.TEMPERATURE_PATH).handler(this::handleAddTemperatureSample);
        router.get(Connectivity.TEMPERATURE_PATH).handler(this::handleGetTemperatureSamples);

        router.post(Connectivity.REPORTS_PATH).handler(this::handleAddTemperatureReport);
        router.get(Connectivity.REPORTS_PATH).handler(this::handleGetTemperatureReports);

        router.post(Connectivity.CONFIG_PATH).handler(this::handleUpdateConfigData);
        router.get(Connectivity.CONFIG_PATH).handler(this::handleGetConfigData);

        // Special endpoints for interacting with the backend
        router.post(Connectivity.SWITCH_MODE_PATH).handler(this::handleRequestToSwitchMode);
        router.get(Connectivity.SWITCH_MODE_PATH).handler(this::handleGetModeToSwitchTo);
        router.post(Connectivity.SWITCH_ALARM_PATH).handler(this::handleRequestToSwitchAlarm);
        router.get(Connectivity.SWITCH_ALARM_PATH).handler(this::handleGetAlarmRequest);

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
            this.operatingMode = data.getString(JsonUtility.OPERATING_MODE);
            this.interventionRequired = data.getBoolean(JsonUtility.NEEDS_INTERVENTION);
            logger.info("Updated config data: windowLevel = {}; state = {}; frequencyMultiplier = {}; mode = {}; interventionNeeded = {}", 
                this.windowLevel,this.state, this.freqMultiplier,
                this.operatingMode, this.interventionRequired);
            response.setStatusCode(200).end();
        }
    }

    private void handleGetConfigData(final RoutingContext routingContext) {
        logger.info("Request for configuration data from {}", getHost(routingContext.request()));

        final JsonObject data = new JsonObject();
        data.put(JsonUtility.WINDOW_LEVEL, this.windowLevel);
        data.put(JsonUtility.SYSTEM_STATE, this.state);
        data.put(JsonUtility.FREQ_MULTIPLIER, this.freqMultiplier);
        data.put(JsonUtility.OPERATING_MODE, this.operatingMode);
        data.put(JsonUtility.NEEDS_INTERVENTION, this.interventionRequired);
        routingContext.response().putHeader("Content-Type", "application/json").end(data.encodePrettily());
    }

    private void handleRequestToSwitchMode(final RoutingContext routingContext) {
        logger.info("Request to switch mode from {}", getHost(routingContext.request()));

        final HttpServerResponse response = routingContext.response();
        final JsonObject data = routingContext.body().asJsonObject();
        if (data == null) {
            logger.warn("Switch mode body is null");
            response.setStatusCode(400).end();
        } else {
            this.modeToSwitchTo = data.getString(JsonUtility.REQUESTED_MODE);
            logger.info("Mode to switch to has been set: {}", this.modeToSwitchTo);
            response.setStatusCode(200).end();
        }
    }

    private void handleGetModeToSwitchTo(final RoutingContext routingContext) {
        logger.info("Request for mode to switch to from {}", getHost(routingContext.request()));

        final JsonObject data = new JsonObject();
        data.put(JsonUtility.REQUESTED_MODE, this.modeToSwitchTo);
        routingContext.response().putHeader("Content-Type", "application/json").end(data.encodePrettily());
    }

    private void handleRequestToSwitchAlarm(final RoutingContext routingContext) {
        logger.info("Request to switch alarm from {}", getHost(routingContext.request()));

        final HttpServerResponse response = routingContext.response();
        final JsonObject data = routingContext.body().asJsonObject();
        if (data == null) {
            logger.warn("Alarm switch request body is null");
            response.setStatusCode(400).end();
        } else {
            this.switchOffAlarm = data.getBoolean(JsonUtility.REQUESTED_ALARM_SWITCH);
            logger.info("Updated alarm state request to: {}", this.switchOffAlarm);
            response.setStatusCode(200).end();
        }
    }

    private void handleGetAlarmRequest(final RoutingContext routingContext) {
        logger.info("Request for alarm switch status from {}", getHost(routingContext.request()));

        final JsonObject data = new JsonObject();
        data.put(JsonUtility.REQUESTED_ALARM_SWITCH, this.switchOffAlarm);
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
