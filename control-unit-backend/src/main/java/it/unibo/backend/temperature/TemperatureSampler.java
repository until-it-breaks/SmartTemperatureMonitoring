package it.unibo.backend.temperature;

import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.NavigableMap;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class TemperatureSampler {
    private static final int MAX_READINGS = 500;                    // We will store the 500 most recent temp readings.
    private static final int MAX_HISTORY_LENGTH = 30;               // We will store the 30 most recent averages.
    private static final long DEFAULT_HISTORY_INTERVAL = 5000;      // Average temperature are calculated at a period of 5s.
    
    private final NavigableMap<Long, Double> temperatureReadings;   // Thread safe variant of a TreeMap. A TreeMap stores key-values and sorts them according to their key in their natural order.
    private final Deque<TemperatureReport> history;                 // Thread safe version of a LinkedList

    private final ScheduledExecutorService scheduler;

    private volatile double maxTempRead;
    private volatile double minTempRead;

    private volatile double tempSum;
    private volatile int tempSampleCount;

    private volatile long lastTime;

    public TemperatureSampler() {
        this.temperatureReadings = new ConcurrentSkipListMap<>();
        this.history = new ConcurrentLinkedDeque<>();

        this.maxTempRead = Double.MIN_VALUE;
        this.minTempRead = Double.MAX_VALUE;
        this.tempSum = 0;
        this.tempSampleCount = 0;

        this.scheduler = Executors.newSingleThreadScheduledExecutor();
        this.scheduler.scheduleAtFixedRate(this::calculateAverage, DEFAULT_HISTORY_INTERVAL, DEFAULT_HISTORY_INTERVAL, TimeUnit.MILLISECONDS);
        this.lastTime = System.currentTimeMillis();
    }

    public void addReading(final long timeStamp, final double temperature) {
        if (timeStamp > System.currentTimeMillis()) {
            throw new IllegalArgumentException("Timestamp cannot be in the future");
        }

        temperatureReadings.put(timeStamp, temperature);

        if (temperatureReadings.size() > MAX_READINGS) {
            temperatureReadings.pollFirstEntry(); // Removes the least recent timestamp and temp reading.
        }

        // Critical section. Both the caller of this method and the scheduler worker can access these.
        synchronized (this) {
            if (temperature < minTempRead) {
                minTempRead = temperature;
            }
    
            if (temperature > maxTempRead) {
                maxTempRead = temperature;
            }
            tempSum += temperature;
            tempSampleCount++;
        }
    }

    private void calculateAverage() {
        double min;
        double max;
        double sum;
        int count;
    
        // Critical section. Both the caller of this method and the scheduler worker can access these. Therefore we save those values and operate on those.
        synchronized (this) {
            sum = tempSum;
            count = tempSampleCount;
            max = maxTempRead;
            min = minTempRead;
            minTempRead = Double.MAX_VALUE;
            maxTempRead = Double.MIN_VALUE;
            tempSum = 0;
            tempSampleCount = 0;
        }

        final long now = System.currentTimeMillis();
        if (count == 0) {
            history.add(new TemperatureReport(lastTime, now, Double.NaN, Double.NaN, Double.NaN));
        } else {
            history.add(new TemperatureReport(lastTime, now, sum / count, min, max));
        }

        if (history.size() > MAX_HISTORY_LENGTH) {
            history.removeFirst();
        }

        lastTime = System.currentTimeMillis();
    }

    public TemperatureSample getTemperature() {
        if (temperatureReadings.isEmpty()) {
            return null;
        }
        var lastEntry = this.temperatureReadings.lastEntry();
        return new TemperatureSample(lastEntry.getValue(), lastEntry.getKey());
    }

    public List<TemperatureReport> getHistory() {
        return new ArrayList<>(history);
    }

    // Stops the scheduler
    public void shutdown() {
        scheduler.shutdown();
        try {
            if (!scheduler.awaitTermination(60, TimeUnit.SECONDS)) {
                scheduler.shutdownNow();
            }
        } catch (final InterruptedException e) {
            scheduler.shutdownNow();
        }
    }
}
