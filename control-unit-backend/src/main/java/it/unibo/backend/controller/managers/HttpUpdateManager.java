package it.unibo.backend.controller.managers;

import io.vertx.core.json.JsonObject;
import it.unibo.backend.Settings.Connectivity;
import it.unibo.backend.Settings.JsonUtility;
import it.unibo.backend.controller.ControlUnitData;
import it.unibo.backend.enums.OperatingMode;
import it.unibo.backend.enums.SystemState;
import it.unibo.backend.http.client.HttpClient;
import it.unibo.backend.temperature.TemperatureReport;
import it.unibo.backend.temperature.TemperatureSample;

public class HttpUpdateManager implements UpdateManager {
    private final HttpClient httpClient;

    private TemperatureSample lastSample;
    private TemperatureReport lastReport;
    private OperatingMode lastMode;
    private boolean lastInterventionNeed;
    private double lastWindowLevel;
    private SystemState lastStateAlias;
    private double lastFrequencyMultiplier;

    public HttpUpdateManager(final HttpClient httpClient) {
        this.httpClient = httpClient;
    }
    
    @Override
    public void sendUpdate(final ControlUnitData data) {
        sendSample(data);
        sendReport(data);
        sendConfiguration(data);
    }

    private void sendSample(final ControlUnitData data) {
        final TemperatureSample sample = data.getSample();
        if (sample != null && !sample.equals(lastSample)) {
            httpClient.sendHttpData(Connectivity.TEMPERATURE_PATH, sample.asJson());
            lastSample = sample;
        }
    }

    private void sendReport(final ControlUnitData data) {
        final TemperatureReport report = data.getReport();
        if (report != null && !report.equals(lastReport)) {
            httpClient.sendHttpData(Connectivity.REPORTS_PATH, report.asJson());
            lastReport = report;
        }
    }

    private void sendConfiguration(final ControlUnitData data) {
        if (data.getWindowLevel() != lastWindowLevel || 
            !data.getState().equals(lastStateAlias) || 
            data.getFreqMultiplier() != lastFrequencyMultiplier ||
            data.getMode() != lastMode || data.isInterventionRequired() != lastInterventionNeed) {
            
            final JsonObject updateData = new JsonObject();
            updateData.put(JsonUtility.WINDOW_LEVEL, data.getWindowLevel());
            updateData.put(JsonUtility.SYSTEM_STATE, data.getState().getName());
            updateData.put(JsonUtility.FREQ_MULTIPLIER, data.getFreqMultiplier());
            updateData.put(JsonUtility.OPERATING_MODE, data.getMode().getName());
            updateData.put(JsonUtility.NEEDS_INTERVENTION, data.isInterventionRequired());

            httpClient.sendHttpData(Connectivity.CONFIG_PATH, updateData);
            lastWindowLevel = data.getWindowLevel();
            lastStateAlias = data.getState();
            lastFrequencyMultiplier = data.getFreqMultiplier();
            lastMode = data.getMode();
            lastInterventionNeed = data.isInterventionRequired();
        }
    }
}
