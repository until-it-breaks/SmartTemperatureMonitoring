const SERVER_HOST = "http://127.0.0.1:8080";

const TEMPERATURE_PATH = "/api/samples";
const REPORTS_PATH = "/api/reports";
const CONFIG_PATH = "/api/config";
const SWITCH_MODE_PATH = "/api/request_mode_switch";
const SWITCH_ALARM_PATH = "/api/request_alarm_switch";

let isManualMode = false;

async function fetchTemperatureData() {
    try {
        const response = await fetch(`${SERVER_HOST}${REPORTS_PATH}`);
        const data = await response.json();
        const avgTemp = document.getElementById("avgTemp");
        const maxTemp = document.getElementById("maxTemp");
        const minTemp = document.getElementById("minTemp");
        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }
        console.log("Temperature Data:", data);
        const reports = [];
        data.forEach(element => {
            reports.push(element);
        });
        avgTemp.innerText = reports[0]["averageTemp"];
        maxTemp.innerText = reports[0]["maximumTemp"];
        minTemp.innerText = reports[0]["minimumTemp"];
    } catch (error) {
        console.error(error);
    }
}

async function fetchSamples() {
    try {
        const response = await fetch(`${SERVER_HOST}${TEMPERATURE_PATH}`);
        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }
        const data = await response.json();
        data.forEach(sample => {
            addTemperature(sample.temperature);
        });
    } catch (error) {
        console.error(error);
    }
}

const N = 20; // Number of recent measurements to keep
let temperatureData = []; // Stores the last N temperatures
let labels = []; // X-axis labels (timestamps)

// Initialize Chart.js
const ctx = document.getElementById('temperatureGraph').getContext('2d');
const tempChart = new Chart(ctx, {
    type: 'line',
    data: {
        labels: labels,
        datasets: [{
            label: 'Temperature (°C)',
            data: temperatureData,
            borderColor: 'red',
            backgroundColor: 'rgba(255, 0, 0, 0.2)',
            fill: true
        }]
    },
    options: {
        scales: {
            x: { title: { display: true, text: 'Time' } },
            y: { title: { display: true, text: 'Temperature (°C)' } }
        }
    }
});

function addTemperature(value) {
    const now = new Date().toLocaleTimeString();
    
    if (temperatureData.length >= N) {
        temperatureData.shift();
        labels.shift();
    }

    temperatureData.push(value);
    labels.push(now);

    tempChart.update();
}

async function fetchConfigData() {
    try {
        const response = await fetch(`${SERVER_HOST}${CONFIG_PATH}`);
        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }
        const data = await response.json();
        console.log("Config Data:", data);
        
        // Update UI based on system state
        const systemState = document.getElementById('systemState');
        const windowLevel = document.getElementById('windowLevel');
    
    
        systemState.textContent = data.systemState;
        windowLevel.textContent = data.windowLevel;
    } catch (error) {
        console.error(error);
    }

}

document.getElementById('manualModeToggle').addEventListener('click', async () => {
    const modeElement = document.getElementById("manualModeToggle");
    const currentMode = modeElement.getAttribute("data-mode"); // Get the current mode from the button's data attribute

    const newMode = currentMode === "auto" ? "manual" : "auto";
    const modePayload = { requestedMode: newMode };

    console.log("Sending mode payload:", modePayload);

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
});

document.getElementById('resolveAlarm').addEventListener('click', async () => {
    const payload = { switchOffAlarm: true };
    console.log("Sending mode payload:", payload);
    try {
        const response = await fetch(`${SERVER_HOST}${SWITCH_ALARM_PATH}`, {
            method: "POST",
            headers: {
                "Content-Type": "application/json",
            },
            body: JSON.stringify(payload),
        });
        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }
    } catch (error) {
        console.error("Error sending alarm switch request:", error);
    }
});

// Initial data fetch
setInterval(fetchTemperatureData, 1000);
setInterval(fetchConfigData, 1000);
setInterval(fetchSamples, 2000);