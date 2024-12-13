package it.unibo.backend;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentSkipListMap;

public class TemperatureSampler {
    private static final int MAX_READINGS = 500;
    private static final int MAX_HISTORY_LENGTH = 30;
    private static final long DEFAULT_HISTORY_INTERVAL = 10000; // Average temp is at intervals of 10s.
    
    // A thread safe variant of a TreeMap. A TreeMap stores values sorting them in the natural order of their key.
    private final ConcurrentSkipListMap<Long, Double> temperatureReadings;
    private final List<Double> averageHistory;
    private final int maxReadings;
    private final int maxAverageHistoryLength;
    private final long historyInterval;

    private long lastTime;

    public TemperatureSampler() {
        this.temperatureReadings = new ConcurrentSkipListMap<>();
        this.averageHistory = new LinkedList<>(); // Not thread safe;
        this.maxReadings = MAX_READINGS;
        this.maxAverageHistoryLength = MAX_HISTORY_LENGTH;
        this.historyInterval = DEFAULT_HISTORY_INTERVAL;
        this.lastTime = System.currentTimeMillis();
    }

    public TemperatureSampler(int maxReadings, int maxAverageHistoryLength ,long historyInterval) {
        this.temperatureReadings = new ConcurrentSkipListMap<>();
        this.averageHistory = new LinkedList<>(); // Not thread safe;
        this.maxReadings = maxReadings;
        this.maxAverageHistoryLength = maxAverageHistoryLength;
        this.historyInterval = historyInterval;
        this.lastTime = System.currentTimeMillis();
    }

    public void addReading(long timeStamp, double temperature) {
        temperatureReadings.put(timeStamp, temperature);

        if (temperatureReadings.size() > maxReadings) {
            temperatureReadings.pollFirstEntry(); // Removes the least recent timestamp and temp reading.
        }

        if (System.currentTimeMillis() - lastTime > historyInterval ) {
            calculateAverage();
        }
    }

    public double getTemperature() {
        return this.temperatureReadings.lastEntry().getValue();
    }

    private void calculateAverage() {
        double sum = 0;
        int count = 0;

        for (double temperature : temperatureReadings.tailMap(System.currentTimeMillis() - historyInterval).values()) {
            sum += temperature;
            count++;
        }

        if (count > 0) {
            averageHistory.add(sum / count);
        } else {
            // There were no readings during this interval
            averageHistory.add(null);
        }

        if (averageHistory.size() > maxAverageHistoryLength) {
            averageHistory.removeLast();
        }
        lastTime = System.currentTimeMillis();
    }
}
