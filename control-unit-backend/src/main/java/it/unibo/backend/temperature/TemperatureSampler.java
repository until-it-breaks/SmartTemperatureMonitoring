package it.unibo.backend.temperature;

import java.util.Deque;
import java.util.List;
import java.util.NavigableMap;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class TemperatureSampler {
    private static final int MAX_READINGS = 500;                // We will store the 500 most recent temp readings.
    private static final int MAX_HISTORY_LENGTH = 30;           // We will store the 30 most recent averages.
    private static final long DEFAULT_HISTORY_INTERVAL = 5000; // Average temperature are calculated at a period of 5s.
    
    private final NavigableMap<Long, Double> temperatureReadings;   // Thread safe variant of a TreeMap. A TreeMap stores key-values and sorts them according to their key in their natural order.
    private final Deque<Double> averageHistory;                     // Thread safe version of a LinkedList

    private final ScheduledExecutorService scheduler;

    private volatile double temperatureSum;
    private volatile int summedTempReadingCount;

    public TemperatureSampler() {
        this.temperatureReadings = new ConcurrentSkipListMap<>();
        this.averageHistory = new ConcurrentLinkedDeque<>();
        this.temperatureSum = 0;
        this.summedTempReadingCount = 0;

        this.scheduler = Executors.newSingleThreadScheduledExecutor();
        this.scheduler.scheduleAtFixedRate(this::calculateAverage, DEFAULT_HISTORY_INTERVAL, DEFAULT_HISTORY_INTERVAL, TimeUnit.MILLISECONDS);
    }

    public void addReading(long timeStamp, double temperature) {
        if (timeStamp > System.currentTimeMillis()) {
            throw new IllegalArgumentException("Timestamp cannot be in the future");
        }

        temperatureReadings.put(timeStamp, temperature);

        if (temperatureReadings.size() > MAX_READINGS) {
            temperatureReadings.pollFirstEntry(); // Removes the least recent timestamp and temp reading.
        }

        // Critical section. Both the caller of this method and the scheduler worker can access these.
        synchronized (this) {
            temperatureSum += temperature;
            summedTempReadingCount++;
        }
    }

    private void calculateAverage() {

        double sum;
        int count;
    
        // Critical section. Both the caller of this method and the scheduler worker can access these. Therefore we save those values and operate on those.
        synchronized (this) {
            sum = temperatureSum;
            count = summedTempReadingCount;

            temperatureSum = 0;
            summedTempReadingCount = 0;
        }

        if (count == 0) {
            averageHistory.add(Double.NaN);
        } else {
            averageHistory.add(sum / count);
        }

        if (averageHistory.size() > MAX_HISTORY_LENGTH) {
            averageHistory.removeFirst();
        }
    }

    public double getTemperature() {
        return this.temperatureReadings.lastEntry().getValue();
    }

    public List<Double> getAverageHistory() {
        return List.copyOf(averageHistory);
    }

    // Stops the scheduler
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
