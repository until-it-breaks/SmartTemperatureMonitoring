package it.unibo.backend.http;

import java.util.LinkedList;
import java.util.List;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import it.unibo.backend.temperature.TemperatureReport;
import it.unibo.backend.temperature.TemperatureSample;

public class HttpService extends AbstractVerticle {
    private static final Logger logger = LoggerFactory.getLogger(HttpService.class);
    private static final int MAX_SAMPLES = 100;     // Max number of temperature samples
    private static final int MAX_REPORTS = 20;      // Max number of periodic reports (contains avg, min, max)

    private final String host;
    private final int port;

    private final List<TemperatureSample> samples;  // Stores individual temperature samples
    private final List<TemperatureReport> reports;  // Stores periodical average, min, max

    // Autoboxed variables
    private Double frequency;                       // The sampling frequency multiplier
    private Double windowLevel;                     // The window opening leve (percentage)

    private String operationMode;                   // The window operatin mode (AUTO/MANUAL)
    private String state;                           // The system state (NORMAL, HOT, TOO_HOT, ALARM)
    private boolean interventionRequired;           // Flag indicating whether an operator's intervention is needed.

    public HttpService(final String host, final int port) {
        this.host = host;
        this.port = port;
        this.samples = new LinkedList<>();
        this.reports = new LinkedList<>();
        this.frequency = null;
        this.windowLevel = null;
        this.operationMode = null;
        this.state = null;
        this.interventionRequired = false;
    }

    @Override
    public void start() {
        logger.info("Starting DataService on {}:{}", host, port);
        final Router router = Router.router(vertx);
        router.route().handler(BodyHandler.create());

        // Creating endpoints RESTful endpoints
        router.post("/api/temperature").handler(this::handleAddTemperatureSample);
        router.get("/api/temperature").handler(this::handleGetTemperatureSamples);

        router.post("/api/report").handler(this::handleAddTemperatureReport);
        router.get("/api/report").handler(this::handleGetTemperatureReports);

        router.post("/api/operation").handler(this::handleUpdateOperatingMode);
        router.get("/api/operation").handler(this::handleGetOperatingMode);

        router.post("/api/alarm").handler(this::handleUpdateInterventioNeed);
        router.get("/api/alarm").handler(this::handleGetInterventionNeed);

        router.post("/api/data").handler(this::handleUpdateConfigData);
        router.get("/api/data").handler(this::handleGetConfigData);

        vertx.createHttpServer().requestHandler(router).listen(port, host, res -> {
            if (res.succeeded()) {
                logger.info("DataService started successfully");
            } else {
                logger.error("Failed to start DataService", res.cause());
            }
        });
    }

    private void handleAddTemperatureSample(final RoutingContext routingContext) {
        logger.info("Received request to add a temperature sample from {}", routingContext.request().remoteAddress().host());

        final HttpServerResponse response = routingContext.response();
        final JsonObject res = routingContext.body().asJsonObject();

        if (res == null) {
            logger.warn("Request body is null");
            response.setStatusCode(400).end();
        } else {
            if (samples.size() > MAX_SAMPLES) {
                logger.debug("Samples list exceeded max size. Removing oldest sample");
                samples.removeFirst();
            }
            samples.add(new TemperatureSample(res.getDouble(JsonUtility.TEMPERATURE), res.getLong(JsonUtility.SAMPLE_TIME)));
            response.setStatusCode(200).end();
        }
    }

    private void handleGetTemperatureSamples(final RoutingContext routingContext) {
        logger.info("Received request for temperature samples from {}", routingContext.request().remoteAddress().host());

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
        logger.info("Received request add temperature report from {}", routingContext.request().remoteAddress().host());

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
                reports.removeFirst();
            }
            this.reports.add(temperatureReport);
            response.setStatusCode(200).end();
        }
    }

    private void handleGetTemperatureReports(final RoutingContext routingContext) {
        logger.info("Received request for temperature reports from {}", routingContext.request().remoteAddress().host());

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
        logger.info("Received request to update operating mode from {}", routingContext.request().remoteAddress().host());

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
        logger.info("Received request for operating mode from {}", routingContext.request().remoteAddress().host());

        final JsonObject data = new JsonObject();
        data.put(JsonUtility.OPERATING_MODE, this.operationMode);
        routingContext.response().putHeader("Content-Type", "application/json").end(data.encodePrettily());
    }

    private void handleUpdateInterventioNeed(final RoutingContext routingContext) {
        logger.info("Received request to update [intervention need] from {}", routingContext.request().remoteAddress().host());

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
        logger.info("Received request for [intervention need] status from {}", routingContext.request().remoteAddress().host());

        final JsonObject data = new JsonObject();
        data.put(JsonUtility.INTERVENTION_NEED, this.interventionRequired);
        routingContext.response().putHeader("Content-Type", "application/json").end(data.encodePrettily());
    }

    private void handleUpdateConfigData(final RoutingContext routingContext) {
        logger.info("Received request to update configuration data from {}", routingContext.request().remoteAddress().host());

        final HttpServerResponse response = routingContext.response();
        final JsonObject res = routingContext.body().asJsonObject();

        if (res == null) {
            response.setStatusCode(400);
            logger.warn("Request body is null");
            response.end();
        } else {
            this.windowLevel = res.getDouble(JsonUtility.WINDOW_LEVEL);
            this.state = res.getString(JsonUtility.SYSTEM_STATE);
            this.frequency = res.getDouble(JsonUtility.SAMPLING_FREQ);
            response.setStatusCode(200).end();
        }
    }

    private void handleGetConfigData(final RoutingContext routingContext) {
        logger.info("Received request for config data from {}", routingContext.request().remoteAddress().host());

        final JsonObject data = new JsonObject();
        data.put(JsonUtility.WINDOW_LEVEL, this.windowLevel);
        data.put(JsonUtility.SYSTEM_STATE, this.state);
        data.put(JsonUtility.SAMPLING_FREQ, this.frequency);
        routingContext.response().putHeader("Content-Type", "application/json").end(data.encodePrettily());
    }
}
