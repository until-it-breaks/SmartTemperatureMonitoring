package it.unibo.backend;

import java.util.Deque;
import java.util.List;
import java.util.NavigableMap;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class TemperatureSampler {
    private static final int MAX_READINGS = 500;
    private static final int MAX_HISTORY_LENGTH = 30;
    private static final long DEFAULT_HISTORY_INTERVAL = 10000; // Average temp is at intervals of 10s.
    
    // A thread safe variant of a TreeMap. A TreeMap stores values sorting them in the natural order of their key.
    private final NavigableMap<Long, Double> temperatureReadings;
    private final Deque<Double> averageHistory;

    private final ScheduledExecutorService scheduler;
    private final Runnable averageTask;

    private volatile double currentIntervalSum;
    private volatile int currentIntervalCount;

    public TemperatureSampler() {
        this.temperatureReadings = new ConcurrentSkipListMap<>();
        this.averageHistory = new ConcurrentLinkedDeque<>();
        this.currentIntervalSum = 0;
        this.currentIntervalCount = 0;

        this.scheduler = Executors.newSingleThreadScheduledExecutor();
        this.averageTask = this::calculateAverage;

        scheduler.scheduleAtFixedRate(averageTask, DEFAULT_HISTORY_INTERVAL, DEFAULT_HISTORY_INTERVAL, TimeUnit.MILLISECONDS);
    }

    public void addReading(long timeStamp, double temperature) {
        if (timeStamp > System.currentTimeMillis()) {
            throw new IllegalArgumentException("Timestamp cannot be in the future");
        }

        temperatureReadings.put(timeStamp, temperature);

        if (temperatureReadings.size() > MAX_READINGS) {
            temperatureReadings.pollFirstEntry(); // Removes the least recent timestamp and temp reading.
        }
        
        synchronized (this) {
            currentIntervalSum += temperature;
            currentIntervalCount++;
        }
    }

    private void calculateAverage() {

        double sum;
        int count;

        synchronized (this) {
            sum = currentIntervalSum;
            count = currentIntervalCount;

            currentIntervalSum = 0;
            currentIntervalCount = 0;
        }

        if (count == 0) {
            averageHistory.add(Double.NaN);
        } else {
            averageHistory.add(sum / count);
        }

        if (averageHistory.size() > MAX_HISTORY_LENGTH) {
            averageHistory.removeLast();
        }
    }

    public double getTemperature() {
        return this.temperatureReadings.lastEntry().getValue();
    }

    public List<Double> getAverageHistory() {
        return List.copyOf(averageHistory);
    }

    public void shutdown() {
        scheduler.shutdown();
        try {
            if (!scheduler.awaitTermination(60, TimeUnit.SECONDS)) {
                scheduler.shutdownNow();
            }
        } catch (InterruptedException e) {
            scheduler.shutdownNow();
        }
    }
}
