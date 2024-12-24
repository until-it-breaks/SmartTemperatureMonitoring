package it.unibo.backend.controlunit.handlers;

import io.vertx.core.json.JsonObject;
import it.unibo.backend.Settings.Connectivity;
import it.unibo.backend.Settings.JsonUtility;
import it.unibo.backend.controlunit.ControlUnit;
import it.unibo.backend.enums.OperatingMode;
import it.unibo.backend.http.client.HttpClient;

public class HttpMessageHandler implements MessageHandler {
    private final ControlUnit controlUnit;
    private final HttpClient httpClient;

    public HttpMessageHandler(final ControlUnit controlUnit, final HttpClient httpClient) {
        this.controlUnit = controlUnit;
        this.httpClient = httpClient;
    }

    @Override
    public void handleMessage(final JsonObject data) {
        if (data.containsKey(JsonUtility.REQUESTED_ALARM_SWITCH)) {
            if (controlUnit.needsIntervention() && data.getBoolean(JsonUtility.REQUESTED_ALARM_SWITCH)) {
                controlUnit.setNeedForIntervention(false);
            }
            // reset the variable now that we handled that
            httpClient.sendHttpData(Connectivity.SWITCH_ALARM_PATH, new JsonObject().put(JsonUtility.REQUESTED_ALARM_SWITCH, false));
        }
        if (data.containsKey(JsonUtility.REQUESTED_MODE)) {
            final String mode = data.getString(JsonUtility.REQUESTED_MODE);
            if (mode.equals(OperatingMode.AUTO.getName())) {
                controlUnit.setMode(OperatingMode.AUTO);
            } else if (mode.equals(OperatingMode.MANUAL.getName())) {
                controlUnit.setMode(OperatingMode.MANUAL);
            }
            // reset the variable now that we handled that
            httpClient.sendHttpData(Connectivity.SWITCH_MODE_PATH, new JsonObject().put(JsonUtility.REQUESTED_MODE, OperatingMode.NONE.getName()));
        }
    }
}
