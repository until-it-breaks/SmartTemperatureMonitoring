package it.unibo.backend.http;

import java.util.LinkedList;
import java.util.List;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import it.unibo.backend.temperature.TemperatureReport;
import it.unibo.backend.temperature.TemperatureSample;

public class DataService extends AbstractVerticle {
    private static final int MAX_SAMPLES = 30;
    private static final int MAX_REPORTS = 15;

    private List<TemperatureSample> samples;    // Stores individual temperature samples
    private List<TemperatureReport> reports;    // Stores periodical average, min, max

    private int port;
    private double frequency;
    private double windowLevel;
    private String operationMode;
    private String state;
    private boolean interventionRequired;

    public DataService(int port) {
        this.samples = new LinkedList<>();
        this.reports = new LinkedList<>();
        this.port = port;

        this.frequency = Double.NaN;
        this.windowLevel = Double.NaN;
        this.operationMode = null;
        this.state = null;
        this.interventionRequired = false;
    }

    @Override
    public void start() {
        Router router = Router.router(vertx);
        router.route().handler(BodyHandler.create());
    
        router.post("/api/temperature").handler(this::handleAddTemperatureSample);
        router.get("/api/temperature").handler(this::handleGetTemperatureSamples);
    
        router.post("/api/temperature/history").handler(this::handleAddTemperatureReport);
        router.get("/api/temperature/history").handler(this::handleGetTemperatureReports);

        router.post("/api/operation").handler(this::handleUpdateOperatingMode);
        router.get("/api/operation").handler(this::handleGetOperatingMode);

        router.post("/api/intervention").handler(this::handleUpdateInterventioNeed);
        router.get("/api/intervention").handler(this::handleGetInterventionNeed);

        router.post("/api/config").handler(this::handleUpdateConfigData);
        router.get("/api/config").handler(this::handleGetConfigData);

        vertx.createHttpServer().requestHandler(router).listen(port);
    }

    private void handleUpdateInterventioNeed(RoutingContext routingContext) {
        HttpServerResponse response = routingContext.response();
        JsonObject res = routingContext.body().asJsonObject();

        if (res == null) {
            response.setStatusCode(400);
            response.end();
        } else {
            this.interventionRequired = res.getBoolean(JsonUtility.INTERVENTION_NEED);
            response.setStatusCode(200).end();
        }
    }

    private void handleGetInterventionNeed(RoutingContext routingContext) {
        JsonObject data = new JsonObject();
        data.put(JsonUtility.INTERVENTION_NEED, this.interventionRequired);
        routingContext.response().putHeader("Content-Type", "application.json").end(data.encodePrettily());
    }

    private void handleUpdateOperatingMode(RoutingContext routingContext) {
        HttpServerResponse response = routingContext.response();
        JsonObject res = routingContext.body().asJsonObject();

        if (res == null) {
            response.setStatusCode(400);
            response.end();
        } else {
            this.operationMode = res.getString(JsonUtility.OPERATING_MODE);
            response.setStatusCode(200).end();
        }
    }

    private void handleGetOperatingMode(RoutingContext routingContext) {
        JsonObject data = new JsonObject();
        data.put(JsonUtility.OPERATING_MODE, this.operationMode);
        routingContext.response().putHeader("Content-Type", "application.json").end(data.encodePrettily());
    }

    private void handleAddTemperatureSample(RoutingContext routingContext) {
        HttpServerResponse response = routingContext.response();
        JsonObject res = routingContext.body().asJsonObject();

        if (res == null) {
            response.setStatusCode(400);
            response.end();
        } else {
            if (samples.size() > MAX_SAMPLES) {
                samples.removeFirst();
            }
            samples.add(new TemperatureSample(res.getDouble(JsonUtility.TEMPERATURE), res.getLong(JsonUtility.SAMPLE_TIME)));
            response.setStatusCode(200).end();
        }
    }

    private void handleGetTemperatureSamples(RoutingContext routingContext) {
        JsonArray array = new JsonArray();
        for (TemperatureSample value: samples) {
            JsonObject data = new JsonObject();
            data.put(JsonUtility.TEMPERATURE, value.getValue());
            data.put(JsonUtility.SAMPLE_TIME, value.getTime());
            array.add(data);
        }
        routingContext.response().putHeader("Content-Type", "application.json").end(array.encodePrettily());
    }

    private void handleUpdateConfigData(RoutingContext routingContext) {
        HttpServerResponse response = routingContext.response();
        JsonObject res = routingContext.body().asJsonObject();

        if (res == null) {
            response.setStatusCode(400);
            response.end();
        } else {
            this.windowLevel = res.getDouble(JsonUtility.WINDOW_LEVEL);
            this.state = res.getString(JsonUtility.SYSTEM_STATE);
            this.frequency = res.getDouble(JsonUtility.SAMPLING_FREQ);
            response.setStatusCode(200).end();
        }
    }

    private void handleGetConfigData(RoutingContext routingContext) {
        JsonObject data = new JsonObject();
        data.put(JsonUtility.WINDOW_LEVEL, this.windowLevel);
        data.put(JsonUtility.SYSTEM_STATE, this.state);
        data.put(JsonUtility.SAMPLING_FREQ, this.frequency);
        routingContext.response().putHeader("Content-Type", "application.json").end(data.encodePrettily());
    }

    private void handleAddTemperatureReport(RoutingContext routingContext) {
        HttpServerResponse response = routingContext.response();
        JsonObject res = routingContext.body().asJsonObject();

        if (res == null) {
            response.setStatusCode(400);
            response.end();
        } else {
            TemperatureReport temperatureReport = new TemperatureReport();
            temperatureReport.setStartTime(res.getLong(JsonUtility.START_TIME));
            temperatureReport.setEndTime(res.getLong(JsonUtility.END_TIME));
            temperatureReport.setAverage(res.getDouble(JsonUtility.AVG_TEMP));
            temperatureReport.setMax(res.getDouble(JsonUtility.MAX_TEMP));
            temperatureReport.setMin(res.getDouble(JsonUtility.MIN_TEMP));
            if (reports.size() > MAX_REPORTS) {
                reports.removeFirst();
            }
            this.reports.add(temperatureReport);
            response.setStatusCode(200).end();
        }
    }

    private void handleGetTemperatureReports(RoutingContext routingContext) {
        JsonArray array = new JsonArray();
        for (TemperatureReport report: reports) {
            JsonObject data = new JsonObject();
            data.put(JsonUtility.START_TIME, report.getStartTime());
            data.put(JsonUtility.END_TIME, report.getEndTime());
            data.put(JsonUtility.AVG_TEMP, report.getAverage());
            data.put(JsonUtility.MAX_TEMP, report.getMax());
            data.put(JsonUtility.MIN_TEMP, report.getMin());
            array.add(data);
        }
        routingContext.response().putHeader("Content-Type", "application.json").end(array.encodePrettily());
    }
}
