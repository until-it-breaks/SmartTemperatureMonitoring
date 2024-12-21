package it.unibo.backend.http;

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
import it.unibo.backend.ConnectivityConfig;
import it.unibo.backend.JsonUtility;
import it.unibo.backend.temperature.TemperatureReport;
import it.unibo.backend.temperature.TemperatureSample;

public class HttpService extends AbstractVerticle {
    private static final Logger logger = LoggerFactory.getLogger(HttpService.class);
    private static final int MAX_SAMPLES = 100;     // Max number of temperature samples
    private static final int MAX_REPORTS = 20;      // Max number of periodic reports (contains avg, min, max)

    private final String host;
    private final int port;

    private final Deque<TemperatureSample> samples;  // Stores individual temperature samples
    private final Deque<TemperatureReport> reports;  // Stores periodical average, min, max

    // Autoboxed variables
    private Double frequency;                       // The sampling frequency multiplier
    private Double windowLevel;                     // The window opening leve (percentage)

    private String operationMode;                   // The window operatin mode (AUTO/MANUAL)
    private String state;                           // The system state (NORMAL, HOT, TOO_HOT, ALARM)
    private boolean interventionRequired;           // Flag indicating whether an operator's intervention is needed.

    public HttpService(final String host, final int port) {
        this.host = host;
        this.port = port;
        this.samples = new ArrayDeque<>();
        this.reports = new ArrayDeque<>();
        this.frequency = 0.0;
        this.windowLevel = 0.0;
        this.operationMode = "";
        this.state = "";
        this.interventionRequired = false;
    }

    @Override
    public void start() {
        logger.info("Starting HttpService on {}:{}", host, port);
        final Router router = Router.router(vertx);
        router.route().handler(BodyHandler.create());

        // Creating endpoints RESTful endpoints
        router.post(ConnectivityConfig.TEMPERATURE_PATH).handler(this::handleAddTemperatureSample);
        router.get(ConnectivityConfig.TEMPERATURE_PATH).handler(this::handleGetTemperatureSamples);

        router.post(ConnectivityConfig.REPORTS_PATH).handler(this::handleAddTemperatureReport);
        router.get(ConnectivityConfig.REPORTS_PATH).handler(this::handleGetTemperatureReports);

        router.post(ConnectivityConfig.OPERATING_MODE_PATH).handler(this::handleUpdateOperatingMode);
        router.get(ConnectivityConfig.OPERATING_MODE_PATH).handler(this::handleGetOperatingMode);

        router.post(ConnectivityConfig.INTERVENTION_PATH).handler(this::handleUpdateInterventionNeed);
        router.get(ConnectivityConfig.INTERVENTION_PATH).handler(this::handleGetInterventionNeed);

        router.post(ConnectivityConfig.CONFIG_PATH).handler(this::handleUpdateConfigData);
        router.get(ConnectivityConfig.CONFIG_PATH).handler(this::handleGetConfigData);

        vertx.createHttpServer().requestHandler(router).listen(port, host, res -> {
            if (res.succeeded()) {
                logger.info("HttpService started successfully");
            } else {
                logger.error("Failed to start HttpService", res.cause());
            }
        });
    }

    private void handleAddTemperatureSample(final RoutingContext routingContext) {
        logger.info("Received request to add a temperature sample from {}", getHost(routingContext.request()));

        final HttpServerResponse response = routingContext.response();
        final JsonObject res = routingContext.body().asJsonObject();

        if (res == null) {
            logger.warn("Request body is null");
            response.setStatusCode(400).end();
        } else {
            if (samples.size() > MAX_SAMPLES) {
                logger.debug("Samples list exceeded max size. Removing oldest sample");
                samples.pollFirst();
            }
            samples.offerLast(new TemperatureSample(res.getDouble(JsonUtility.TEMPERATURE), res.getLong(JsonUtility.SAMPLE_TIME)));
            response.setStatusCode(200).end();
        }
    }

    private void handleGetTemperatureSamples(final RoutingContext routingContext) {
        logger.info("Received request for temperature samples from {}", getHost(routingContext.request()));

        final JsonArray array = new JsonArray();
        for (final TemperatureSample value: samples) {
            final JsonObject data = new JsonObject();
            data.put(JsonUtility.TEMPERATURE, value.getValue());
            data.put(JsonUtility.SAMPLE_TIME, value.getTime());
            array.add(data);
        }
        routingContext.response().putHeader("Content-Type", "application/json").end(array.encodePrettily());
    }

    private void handleAddTemperatureReport(final RoutingContext routingContext) {
        logger.info("Received request add temperature report from {}", getHost(routingContext.request()));

        final HttpServerResponse response = routingContext.response();
        final JsonObject res = routingContext.body().asJsonObject();

        if (res == null) {
            response.setStatusCode(400).end();
        } else {
            final TemperatureReport temperatureReport = new TemperatureReport();
            temperatureReport.setStartTime(res.getLong(JsonUtility.START_TIME));
            temperatureReport.setEndTime(res.getLong(JsonUtility.END_TIME));
            temperatureReport.setAverage(res.getDouble(JsonUtility.AVG_TEMP));
            temperatureReport.setMax(res.getDouble(JsonUtility.MAX_TEMP));
            temperatureReport.setMin(res.getDouble(JsonUtility.MIN_TEMP));
            if (reports.size() > MAX_REPORTS) {
                logger.debug("Report list exceeded max size. Removing oldest report");
                reports.pollFirst();
            }
            this.reports.offerLast(temperatureReport);
            response.setStatusCode(200).end();
        }
    }

    private void handleGetTemperatureReports(final RoutingContext routingContext) {
        logger.info("Received request for temperature reports from {}", getHost(routingContext.request()));

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
        logger.info("Received request to update operating mode from {}", getHost(routingContext.request()));

        final HttpServerResponse response = routingContext.response();
        final JsonObject res = routingContext.body().asJsonObject();

        if (res == null) {
            logger.warn("Request body is null");
            response.setStatusCode(400).end();
        } else {
            this.operationMode = res.getString(JsonUtility.OPERATING_MODE);
            response.setStatusCode(200).end();
        }
    }

    private void handleGetOperatingMode(final RoutingContext routingContext) {
        logger.info("Received request for operating mode from {}", getHost(routingContext.request()));

        final JsonObject data = new JsonObject();
        data.put(JsonUtility.OPERATING_MODE, this.operationMode);
        routingContext.response().putHeader("Content-Type", "application/json").end(data.encodePrettily());
    }

    private void handleUpdateInterventionNeed(final RoutingContext routingContext) {
        logger.info("Received request to update [intervention need] from {}", getHost(routingContext.request()));

        final HttpServerResponse response = routingContext.response();
        final JsonObject res = routingContext.body().asJsonObject();

        if (res == null) {
            logger.warn("Request body is null");
            response.setStatusCode(400).end();
        } else {
            this.interventionRequired = res.getBoolean(JsonUtility.INTERVENTION_NEED);
            response.setStatusCode(200).end();
        }
    }

    private void handleGetInterventionNeed(final RoutingContext routingContext) {
        logger.info("Received request for [intervention need] status from {}", getHost(routingContext.request()));

        final JsonObject data = new JsonObject();
        data.put(JsonUtility.INTERVENTION_NEED, this.interventionRequired);
        routingContext.response().putHeader("Content-Type", "application/json").end(data.encodePrettily());
    }

    private void handleUpdateConfigData(final RoutingContext routingContext) {
        logger.info("Received request to update configuration data from {}", getHost(routingContext.request()));

        final HttpServerResponse response = routingContext.response();
        final JsonObject res = routingContext.body().asJsonObject();

        if (res == null) {
            response.setStatusCode(400);
            logger.warn("Request body is null");
            response.end();
        } else {
            this.windowLevel = res.getDouble(JsonUtility.WINDOW_LEVEL);
            this.state = res.getString(JsonUtility.SYSTEM_STATE);
            this.frequency = res.getDouble(JsonUtility.FREQ_MULTIPLIER);
            response.setStatusCode(200).end();
        }
    }

    private void handleGetConfigData(final RoutingContext routingContext) {

        logger.info("Received request for configuration data from {}", getHost(routingContext.request()));
        final JsonObject data = new JsonObject();
        data.put(JsonUtility.WINDOW_LEVEL, this.windowLevel);
        data.put(JsonUtility.SYSTEM_STATE, this.state);
        data.put(JsonUtility.FREQ_MULTIPLIER, this.frequency);
        routingContext.response().putHeader("Content-Type", "application/json").end(data.encodePrettily());
    }

    private String getHost(HttpServerRequest request) {
        String host = request.getHeader("X-Forwarded-For");
        if (host == null) {
            return request.remoteAddress().host();
        } else {
            return host;
        }
    }
}
