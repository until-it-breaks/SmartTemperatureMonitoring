package it.unibo.backend;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.unibo.backend.controlunit.ControlUnit;
import it.unibo.backend.Settings.Temperature;

public class TemperatureTest implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(TemperatureTest.class);
    private static final List<Double> temperatureList = List.of(Temperature.NORMAL - 2, Temperature.NORMAL - 3, Temperature.NORMAL - 4, Temperature.NORMAL -5,
        Temperature.HOT + 1, Temperature.HOT + 3, Temperature.HOT + 4, Temperature.HOT + 4,
        Temperature.NORMAL + 1, Temperature.NORMAL + 2, Temperature.NORMAL + 3, Temperature.NORMAL + 4,
        Temperature.TOO_HOT + 2, Temperature.TOO_HOT + 1, Temperature.TOO_HOT + 4, Temperature.TOO_HOT - 10);

    private final ControlUnit controlUnit;
    private final long interval;
    private int index;

    public TemperatureTest(ControlUnit controlUnit, long interval) {
        this.controlUnit = controlUnit;
        this.interval = interval;
        this.index = 0;
    }

    @Override
    public void run() {
        while (true) {
            double temperature = pickTemperature();
            controlUnit.getSampler().addSample(System.currentTimeMillis(), temperature);
            logger.info("Adding test sample: {}", temperature);
            try {
                Thread.sleep(interval);
            } catch (InterruptedException e) {
                logger.error("TemperatureTest was interrupted", e);
                Thread.currentThread().interrupt();
            }
        }
    }

    private double pickTemperature() {
        if (index > temperatureList.size() - 1) {
            index = 0;
        }
        return temperatureList.get(index++);
    }
}
