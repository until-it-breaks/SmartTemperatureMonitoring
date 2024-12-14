package it.unibo.backend.temperature;

import java.text.SimpleDateFormat;
import java.util.concurrent.ThreadLocalRandom;

import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;

public class TestTemperatureSampler {

    private static final Logger logger = LoggerFactory.getLogger(TestTemperatureSampler.class);

    public static void main(String[] args) {
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
        TemperatureSampler sampler = new TemperatureSampler();

        Thread producerThread = new Thread(() -> {
            try {
                while (true) {
                    long currentTimestamp = System.currentTimeMillis();
                    double randomTemperature = ThreadLocalRandom.current().nextDouble(-30.0, 50.0); // Random temp between -30 and 50 degrees

                    sampler.addReading(currentTimestamp, randomTemperature);

                    logger.info(String.format("Added temperature: %.2f at " + timeFormat.format(currentTimestamp), randomTemperature));

                    Thread.sleep(ThreadLocalRandom.current().nextInt(500, 1501));
                }
            } catch (InterruptedException e) {
                logger.warn("Producer thread interrupted.");
            }
        });

        producerThread.start();

        Thread consumerThread = new Thread(() -> {
            try {
                while (true) {
                    Thread.sleep(5000);
                    var history = sampler.getHistory();
                    if (!history.isEmpty()) {
                        logger.info(history.getLast().toStringSimple());
                    } else {
                        logger.info("No info available right now.");
                    }
                }
            } catch (InterruptedException e) {
                logger.warn("Consumer thread interrupted.");
            }
        });

        consumerThread.start();

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            logger.warn("Shutting down...");
            producerThread.interrupt();
            consumerThread.interrupt();
            sampler.shutdown();
        }));
    }
}
