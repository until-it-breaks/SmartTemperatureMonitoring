package it.unibo.backend.temperature;

import java.text.SimpleDateFormat;
import java.util.concurrent.ThreadLocalRandom;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestTemperatureSampler {

    private static final Logger logger = LoggerFactory.getLogger(TestTemperatureSampler.class);

    public static void main(final String[] args) {
        final SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
        final TemperatureSampler sampler = new TemperatureSampler();

        final Thread producerThread = new Thread(() -> {
            try {
                while (true) {
                    final long currentTimestamp = System.currentTimeMillis();
                    final double randomTemperature = ThreadLocalRandom.current().nextDouble(-30.0, 50.0); // Random temp between -30 and 50 degrees

                    sampler.addReading(currentTimestamp, randomTemperature);

                    logger.info(String.format("Added temperature: %.2f at " + timeFormat.format(currentTimestamp), randomTemperature));

                    Thread.sleep(ThreadLocalRandom.current().nextInt(500, 1501));
                }
            } catch (final InterruptedException e) {
                logger.warn("Producer thread interrupted.");
            }
        });

        producerThread.start();

        final Thread consumerThread = new Thread(() -> {
            try {
                while (true) {
                    Thread.sleep(5000);
                    final var history = sampler.getHistory();
                    if (!history.isEmpty()) {
                        logger.info(history.getLast().toStringSimple());
                    } else {
                        logger.info("No info available right now.");
                    }
                }
            } catch (final InterruptedException e) {
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
