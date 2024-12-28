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

// Function to toggle between auto and manual mode
async function toggleMode() {
    const modeElement = document.getElementById("manualModeToggle");
    const currentMode = modeElement.getAttribute("data-mode"); // Get the current mode from the button's data attribute

    const newMode = currentMode === "auto" ? "manual" : "auto";
    const modePayload = { requestedMode: newMode };

    console.log("Sending mode payload:", modePayload); // Debug log

    try {
        const response = await fetch(`${SERVER_HOST}${SWITCH_MODE_PATH}`, {
            method: "POST",
            headers: {
                "Content-Type": "application/json",
            },
            body: JSON.stringify(modePayload),
        });

        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }

        // Update the button text and data attribute based on the new mode
        modeElement.setAttribute("data-mode", newMode);
        modeElement.textContent = `Enter ${currentMode === "auto" ? "Manual" : "Auto"} Mode`;

        console.log("Mode toggled successfully to:", newMode);
    } catch (error) {
        console.error("Error toggling mode:", error);
    }

    isManualMode = currentMode === "manual";
    // Disable the range input if not in manual mode
    const windowControl = document.getElementById('windowControl');
    windowControl.disabled = !isManualMode;
}


document.getElementById('manualModeToggle').addEventListener('click', toggleMode);

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
