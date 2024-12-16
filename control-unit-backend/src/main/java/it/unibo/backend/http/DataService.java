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

public class DataService extends AbstractVerticle {
    private int port;
    private static final int MAX_SIZE = 10;

    private List<DataPoint> values;
    private List<TemperatureReport> temperatureReports;

    private double frequency;
    private String operationMode;
    private int windowLevel;
    private String state;
    private float avgTemperature;
    private float minTemperature;
    private float maxTemperature;
    private boolean requireIntervention;

    public DataService(int port) {
        this.values = new LinkedList<>();
        this.temperatureReports = new LinkedList<>();
        this.port = port;
    }

    @Override
    public void start() {
        Router router = Router.router(vertx);
        router.route().handler(BodyHandler.create());
    
        router.post("/api/temperature").handler(this::handleAddTemperatureReading);
        router.get("/api/temperature").handler(this::handleGetTemperatureReading);
    
        router.post("/api/temperature/history").handler(this::handleAddTemperatureReports);
        router.get("/api/temperature/history").handler(this::handleGetTemperatureReports);

        router.post("/api/operation").handler(this::handleAddOperatingMode);
        router.get("/api/operation").handler(this::handleGetOperatingMode);

        router.post("/api/intervention").handler(this::handleAddRequireIntervention);
        router.get("/api/intervention").handler(this::handleGetRequireIntervention);

        router.post("/api/config").handler(this::handleAddConfigData);
        router.get("/api/config").handler(this::handleGetConfigData);

        vertx.createHttpServer().requestHandler(router).listen(port);
    }

    private void handleAddRequireIntervention(RoutingContext routingContext) {
        HttpServerResponse response = routingContext.response();
        JsonObject res = routingContext.body().asJsonObject();

        if (res == null) {
            response.setStatusCode(400);
            response.end();
        } else {
            this.requireIntervention = res.getBoolean("requireIntervention");
            response.setStatusCode(200).end();
        }
    }

    private void handleGetRequireIntervention(RoutingContext routingContext) {
        JsonObject data = new JsonObject();
        data.put("requireIntervention", this.operationMode);
        routingContext.response().putHeader("Content-Type", "application.json").end(data.encodePrettily());
    }

    private void handleAddOperatingMode(RoutingContext routingContext) {
        HttpServerResponse response = routingContext.response();
        JsonObject res = routingContext.body().asJsonObject();

        if (res == null) {
            response.setStatusCode(400);
            response.end();
        } else {
            this.operationMode = res.getString("operatingMode");
            response.setStatusCode(200).end();
        }
    }

    private void handleGetOperatingMode(RoutingContext routingContext) {
        JsonObject data = new JsonObject();
        data.put("operationMode", this.operationMode);
        routingContext.response().putHeader("Content-Type", "application.json").end(data.encodePrettily());
    }

    private void handleAddTemperatureReading(RoutingContext routingContext) {
        HttpServerResponse response = routingContext.response();
        JsonObject res = routingContext.body().asJsonObject();

        if (res == null) {
            response.setStatusCode(400);
            response.end();
        } else {
            if (values.size() > MAX_SIZE) {
                values.removeFirst();
            }
            values.add(new DataPoint(res.getDouble("temperature"), res.getLong("sampleTime")));
            response.setStatusCode(200).end();
        }
    }

    private void handleGetTemperatureReading(RoutingContext routingContext) {
        JsonArray array = new JsonArray();
        for (DataPoint value: values) {
            JsonObject data = new JsonObject();
            data.put("temperature", value.getValue());
            data.put("sampleTime", value.getTime());
            array.add(data);
        }
        routingContext.response().putHeader("Content-Type", "application.json").end(array.encodePrettily());
    }

    private void handleAddConfigData(RoutingContext routingContext) {
        HttpServerResponse response = routingContext.response();
        JsonObject res = routingContext.body().asJsonObject();

        if (res == null) {
            response.setStatusCode(400);
            response.end();
        } else {
            this.windowLevel = res.getInteger("windowLevel");
            this.state = res.getString("state");
            this.frequency = res.getDouble("frequency");
            response.setStatusCode(200).end();
        }
    }

    private void handleGetConfigData(RoutingContext routingContext) {
        JsonObject data = new JsonObject();
        data.put("windowLevel", this.windowLevel);
        data.put("state", this.state);
        data.put("frequency", this.frequency);
        routingContext.response().putHeader("Content-Type", "application.json").end(data.encodePrettily());
    }

    private void handleAddTemperatureReports(RoutingContext routingContext) {
        HttpServerResponse response = routingContext.response();
        JsonObject res = routingContext.body().asJsonObject();

        if (res == null) {
            response.setStatusCode(400);
            response.end();
        } else {
            TemperatureReport temperatureReport = new TemperatureReport();
            temperatureReport.setStartTime(res.getLong("startTime"));
            temperatureReport.setEndTime(res.getLong("endTime"));
            temperatureReport.setMax(res.getDouble("averageTemp"));
            temperatureReport.setMin(res.getDouble("minimumTemp"));
            temperatureReport.setAverage(res.getDouble("maximumTemp"));
            if (temperatureReports.size() > MAX_SIZE) {
                temperatureReports.removeFirst();
            }
            this.temperatureReports.add(temperatureReport);
            response.setStatusCode(200).end();
        }
    }

    private void handleGetTemperatureReports(RoutingContext routingContext) {
        JsonArray array = new JsonArray();
        for (TemperatureReport value: temperatureReports) {
            JsonObject data = new JsonObject();
            data.put("startTime", value.getStartTime());
            data.put("endTime", value.getEndTime());
            data.put("averageTemp", value.getAverage());
            data.put("minimumTemp", value.getMin());
            data.put("maximumTemp", value.getMax());
            array.add(data);
        }
        routingContext.response().putHeader("Content-Type", "application.json").end(array.encodePrettily());
    }
}
