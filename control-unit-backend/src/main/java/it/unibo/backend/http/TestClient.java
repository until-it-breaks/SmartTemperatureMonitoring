package it.unibo.backend.http;

import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.client.WebClient;

public class TestClient {
    public static void main(String[] args) throws InterruptedException {
        String host = "localhost";
        int port = 8080;
        Vertx vertx = Vertx.vertx();

        JsonObject item = new JsonObject();
        item.put("value", 20);
        
        WebClient client = WebClient.create(vertx);

        System.out.println("Posting new data item...");
        client.post(port, host, "/api/data").sendJson(item).onSuccess(response -> {
            System.out.println("Posting - Received response with status code: " + response.statusCode());
        });

        Thread.sleep(2000);

        System.out.println("Getting data items...");

        client.get(port, host, "/api/data").send()
            .onSuccess(response -> {
                System.out.println("Getting - Received response with status code: " + response.statusCode());
                JsonArray output = response.bodyAsJsonArray();
                System.out.println(output.encodePrettily()); 
            })
            .onFailure(error -> {
                System.out.println("Something went wrong " + error.getMessage());
            });
    }
}
