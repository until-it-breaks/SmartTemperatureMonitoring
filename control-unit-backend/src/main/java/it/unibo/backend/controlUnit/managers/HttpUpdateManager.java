package it.unibo.backend.controlunit.managers;

import io.vertx.core.json.JsonObject;
import it.unibo.backend.ConnectivityConfig;
import it.unibo.backend.JsonUtility;
import it.unibo.backend.controlunit.ControlUnitData;
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
        sendOperatingMode(data);
        sendInterventionNeed(data);
        sendConfiguration(data);
    }

    private void sendSample(final ControlUnitData data) {
        final TemperatureSample sample = data.getSample();
        if (sample != null && !sample.equals(lastSample)) {
            httpClient.sendHttpData(ConnectivityConfig.TEMPERATURE_PATH, sample.asJson());
            lastSample = sample;
        }
    }

    private void sendReport(final ControlUnitData data) {
        final TemperatureReport report = data.getReport();
        if (report != null && !report.equals(lastReport)) {
            httpClient.sendHttpData(ConnectivityConfig.REPORTS_PATH, report.asJson());
            lastReport = report;
        }
    }

    private void sendOperatingMode(final ControlUnitData data) {
        if (!data.getMode().equals(lastMode)) {
            final JsonObject updateData = new JsonObject();
            updateData.put(JsonUtility.OPERATING_MODE, data.getMode().getName());
            httpClient.sendHttpData(ConnectivityConfig.OPERATING_MODE_PATH, updateData);
            lastMode = data.getMode();
        }
    }

    private void sendInterventionNeed(final ControlUnitData data) {
        if (data.isInterventionRequired() != lastInterventionNeed) {
            final JsonObject updateData = new JsonObject();
            updateData.put(JsonUtility.INTERVENTION_NEED, data.isInterventionRequired());
            httpClient.sendHttpData(ConnectivityConfig.INTERVENTION_PATH, updateData);
            lastInterventionNeed = data.isInterventionRequired();
        }
    }

    private void sendConfiguration(final ControlUnitData data) {
        if (data.getWindowLevel() != lastWindowLevel || 
            !data.getState().equals(lastStateAlias) || 
            data.getFreqMultiplier() != lastFrequencyMultiplier) {
            
            final JsonObject updateData = new JsonObject();
            updateData.put(JsonUtility.WINDOW_LEVEL, data.getWindowLevel());
            updateData.put(JsonUtility.SYSTEM_STATE, data.getState().getName());
            updateData.put(JsonUtility.FREQ_MULTIPLIER, data.getFreqMultiplier());

            httpClient.sendHttpData(ConnectivityConfig.CONFIG_PATH, updateData);
            lastWindowLevel = data.getWindowLevel();
            lastStateAlias = data.getState();
            lastFrequencyMultiplier = data.getFreqMultiplier();
        }
    }
}
