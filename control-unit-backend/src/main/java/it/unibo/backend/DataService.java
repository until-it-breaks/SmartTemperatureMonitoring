package it.unibo.backend;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;

public class DataService extends AbstractVerticle {
    private int port;
    private static final int MAX_SIZE = 10;
    private List<DataPoint> values;

    public DataService(int port) {
        values = new LinkedList<>();
        this.port = port;
    }

    @Override
    public void start() {
        Router router = Router.router(vertx);
        router.route().handler(BodyHandler.create());
        router.post("/api/data").handler(this::handleAddNewData);
        router.get("/api/data").handler(this::handleGetData);
        vertx.createHttpServer().requestHandler(router).listen(port);
        log("Service ready on port: " + port);
    }

    private void handleAddNewData(RoutingContext routingContext) {
        HttpServerResponse response = routingContext.response();
        log("new msg + " + routingContext.body().asString());
        JsonObject res = routingContext.body().asJsonObject();
        if (res == null) {
            sendError(400, response);
        } else {
            float value = res.getFloat("value");
            long time = System.currentTimeMillis();

            values.addFirst(new DataPoint(value, time));
            if (values.size() > MAX_SIZE) {
                values.removeLast();
            }

            log("New value: " + value + " on " + new Date(time));
            response.setStatusCode(200).end();
        }
    }

    private void handleGetData(RoutingContext routingContext) {
        JsonArray array = new JsonArray();
        for (DataPoint value: values) {
            JsonObject data = new JsonObject();
            data.put("time", value.getTime());
            data.put("value", value.getValue());
            array.add(data);
        }
        routingContext.response().putHeader("Content-Type", "application.json").end(array.encodePrettily());
    }

    private void sendError(int statusCode, HttpServerResponse response) {
        response.setStatusCode(statusCode);
        response.end();
    }

    private void log(String msg) {
        System.out.println("[DATA SERVICE] " + msg);
    }
}
