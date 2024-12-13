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
            calculateAverage(timeStamp);
        }
    }

    public double getTemperature() {
        return this.temperatureReadings.lastEntry().getValue();
    }

    private void calculateAverage(long timeInterval) {
        double sum = 0;
        int count = 0;

        for (double temperature : temperatureReadings.tailMap(System.currentTimeMillis() - timeInterval).values()) {
            sum += temperature;
            count++;
        }

        if (count > 0) {
            averageHistory.add(sum / count);

            if (averageHistory.size() > maxAverageHistoryLength) {
                averageHistory.removeLast();
            }
        }
    }

    /*
    public static void main(String[] args) throws InterruptedException {
        TemperatureSampler sampler = new TemperatureSampler(5); // Keep the most recent 5 readings
        sampler.addReading(System.currentTimeMillis(), 22.5);
        Thread.sleep(1000);
        sampler.addReading(System.currentTimeMillis(), 23.0);
        Thread.sleep(1000);
        sampler.addReading(System.currentTimeMillis(), 23.5);
        Thread.sleep(1000);
        sampler.addReading(System.currentTimeMillis(), 24.0);
        Thread.sleep(1000);
        sampler.addReading(System.currentTimeMillis(), 24.5);
        Thread.sleep(1000);
        sampler.addReading(System.currentTimeMillis(), 25.0); // This will cause the oldest reading (22.5) to be removed

        // Calculate the average temperature over the last 5 minutes (300,000 ms)
        double avgTemperature = sampler.getAverage(1000000000);
        System.out.println("Average Temperature: " + avgTemperature);
    }
    */
}
