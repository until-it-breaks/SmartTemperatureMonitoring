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
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.atomic.DoubleAdder;

public class TemperatureSampler {
    private static final int MAX_READINGS = 500;                    // We will store the 500 most recent temp readings.
    private static final int MAX_HISTORY_LENGTH = 30;               // We will store the 30 most recent averages.
    private static final long DEFAULT_HISTORY_INTERVAL = 5000;      // Average temperature are calculated at a period of 5s.
    
    private final NavigableMap<Long, Double> temperatureReadings;   // Thread safe variant of a TreeMap. A TreeMap stores key-values and sorts them according to their key in their natural order.
    private final Deque<TemperatureReport> history;                 // Thread safe version of a LinkedList

    private final ScheduledExecutorService scheduler;

    private final DoubleAdder tempSum;
    private final AtomicInteger tempSampleCount;
    private final AtomicReference<Double> maxTempRead;
    private final AtomicReference<Double> minTempRead;

    private volatile AtomicLong lastTime;

    public TemperatureSampler() {
        this.temperatureReadings = new ConcurrentSkipListMap<>();
        this.history = new ConcurrentLinkedDeque<>();

        this.tempSum = new DoubleAdder();
        this.maxTempRead = new AtomicReference<>(Double.MIN_VALUE);
        this.minTempRead = new AtomicReference<>(Double.MAX_VALUE);
        this.tempSampleCount = new AtomicInteger();
        this.lastTime = new AtomicLong(System.currentTimeMillis());

        this.scheduler = Executors.newSingleThreadScheduledExecutor();
        this.scheduler.scheduleAtFixedRate(this::calculateAverage, DEFAULT_HISTORY_INTERVAL, DEFAULT_HISTORY_INTERVAL, TimeUnit.MILLISECONDS);
    }

    public void addReading(final long timeStamp, final double temperature) {
        if (timeStamp > System.currentTimeMillis()) {
            throw new IllegalArgumentException("Timestamp cannot be in the future");
        }

        temperatureReadings.put(timeStamp, temperature);

        if (temperatureReadings.size() > MAX_READINGS) {
            temperatureReadings.pollFirstEntry(); // Removes the least recent timestamp and temp reading.
        }

        maxTempRead.updateAndGet(current -> Math.max(current, temperature));
        minTempRead.updateAndGet(current -> Math.min(current, temperature));
        tempSum.add(temperature);
        tempSampleCount.incrementAndGet();
    }

    private void calculateAverage() {
        double sum = tempSum.sumThenReset();
        int count = tempSampleCount.getAndSet(0);
        double max = maxTempRead.getAndSet(Double.MIN_VALUE);
        double min = minTempRead.getAndSet(Double.MAX_VALUE);

        final long now = System.currentTimeMillis();
        if (count == 0) {
            history.add(new TemperatureReport(lastTime.get(), now, null, null, null));
        } else {
            history.add(new TemperatureReport(lastTime.get(), now, sum / count, min, max));
        }

        if (history.size() > MAX_HISTORY_LENGTH) {
            history.removeFirst();
        }

        lastTime.set(System.currentTimeMillis());
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
}
