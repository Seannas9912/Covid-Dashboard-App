document.addEventListener("DOMContentLoaded", () => {
    fetch('http://localhost:8080/api/covid-data')
        .then(response => response.json())
        .then(data => {
            renderPureHtmlChart(data);
        })
        .catch(error => console.error("Error fetching data payload:", error));
});

function renderPureHtmlChart(data) {
    const chartContainer = document.getElementById('customBarChart');
    chartContainer.innerHTML = '';

    const maxCases = Math.max(...data.map(item => item["Total Confirmed Cases"] || 1));

    data.forEach(item => {
        const rawCases = item["Total Confirmed Cases"] || 0;
        const heightPercentage = (rawCases / maxCases) * 100;

        const barWrapper = document.createElement('div');
        barWrapper.className = 'bar-wrapper';
        barWrapper.style.height = `${Math.max(heightPercentage, 2)}%`;
        barWrapper.title = `Date: ${item["Date"]}\nCases: ${rawCases}`;

        const barFill = document.createElement('div');
        barFill.className = 'bar-fill';

        barWrapper.appendChild(barFill);
        chartContainer.appendChild(barWrapper);
    });
}
