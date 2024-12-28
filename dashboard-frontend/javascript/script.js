const SERVER_HOST = "http://127.0.0.1:8080";

const TEMPERATURE_PATH = "/api/samples";
const REPORTS_PATH = "/api/reports";
const CONFIG_PATH = "/api/config";
const SWITCH_MODE_PATH = "/api/request_mode_switch";
const SWITCH_ALARM_PATH = "/api/request_alarm_switch";

let isManualMode = false;

async function fetchTemperatureData() {
    const response = await fetch(`${SERVER_HOST}${TEMPERATURE_PATH}`);
    const data = await response.json();
    console.log("Temperature Data:", data);
}

async function fetchConfigData() {
    const response = await fetch(`${SERVER_HOST}${CONFIG_PATH}`);
    const data = await response.json();
    console.log("Config Data:", data);

    // Update UI based on system state
    const systemState = document.getElementById('systemState');
    const windowLevel = document.getElementById('windowLevel');


    systemState.textContent = data.systemState;
    windowLevel.textContent = data.windowLevel;

    isManualMode = data.operatingMode === "manual";

    // Disable the range input if not in manual mode
    const windowControl = document.getElementById('windowControl');
    windowControl.disabled = !isManualMode;
}

const sendAlarmSwitchRequest = async (switchState) => {
    const modePayload = { switchOffAlarm: switchState };
    console.log("Sending mode payload:", modePayload);
    try {
        const response = await fetch(`${SERVER_HOST}${SWITCH_ALARM_PATH}`, {
            method: "POST",
            headers: {
                "Content-Type": "application/json",
            },
            body: JSON.stringify({
                switchOffAlarm: switchState,
            }),
        });
        const result = await response.json();
        console.log("Alarm Switch Response:", result);
    } catch (error) {
        console.error("Error sending alarm switch request:", error);
    }
};

const sendModeSwitchRequest = async (mode) => {
    const modePayload = { requestedMode: mode };
    console.log("Sending mode payload:", modePayload);
    try {
        const response = await fetch(`${SERVER_HOST}${SWITCH_MODE_PATH}`, {
            method: "POST",
            headers: {
                "Content-Type": "application/json",
            },
            body: JSON.stringify({
                requestedMode: mode,
            }),
        });
        const result = await response.json();
        console.log("Mode Switch Response:", result);
    } catch (error) {
        console.error("Error sending mode switch request:", error);
    }
};

document.getElementById('manualModeToggle').addEventListener('click', () => {
    const mode = isManualMode ? "auto" : "manual";
    sendModeSwitchRequest(mode);
});

document.getElementById('resolveAlarm').addEventListener('click', () => {
    sendAlarmSwitchRequest(true);
});

document.getElementById('windowControl').addEventListener('input', (event) => {
    if (!isManualMode) {
        alert("Cannot change window level in automatic mode.");
        event.target.value = 0; // Reset the range input value
    }
});

// Initial data fetch
fetchTemperatureData();
fetchConfigData();
