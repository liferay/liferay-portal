const RANGE_SECONDS = [3600, 86400, 604800];

const STATUS_COLORS = {
	0: '#dcefe2',
	2: '#b7791f',
	3: '#c2362f',
	4: '#5b6b85'
};

const STATUS_NAMES = {
	0: 'OK',
	2: 'WARN',
	3: 'CRITICAL',
	4: 'STALE'
};

const UPDATE_INTERVAL_SECONDS = 15 * 60;

let allTimelineChart = null;
let currentRangeSeconds = 3600;
let durationChart = null;
let timelineChart = null;

function compareRows(row1, row2, key, direction) {
	let value1 = row1[key];
	let value2 = row2[key];

	if (value1 === value2) {
		return (row1.monitor < row2.monitor) ? -1 : 1;
	}

	if ((value1 === null) || (value1 === undefined)) {
		return 1;
	}

	if ((value2 === null) || (value2 === undefined)) {
		return -1;
	}

	return ((value1 < value2) ? -1 : 1) * direction;
}

function createMonitorTable(columns, emptyText, rows, tableElementID) {
	let tableElement = document.getElementById(tableElementID);

	tableElement.innerHTML = '';

	let theadElement = tableElement.createTHead();

	theadElement.classList.add('thead-light');

	let headerRowElement = theadElement.insertRow();

	columns.forEach((column) => {
		let thElement = document.createElement('th');

		thElement.appendChild(document.createTextNode(column.label));

		headerRowElement.appendChild(thElement);
	});

	let tbodyElement = tableElement.createTBody();

	if (rows.length === 0) {
		let rowElement = tbodyElement.insertRow();

		let cellElement = rowElement.insertCell();

		cellElement.colSpan = columns.length;

		cellElement.appendChild(document.createTextNode(emptyText));

		tableElement.removeAttribute('data-sortable-initialized');

		return;
	}

	rows.forEach((row) => {
		let rowElement = tbodyElement.insertRow();

		columns.forEach((column) => {
			let cellElement = rowElement.insertCell();

			let value = row[column.key];

			cellElement.setAttribute(
				'data-value', ((value === null) || (value === undefined)) ? -1 : value);

			let content = column.render(row);

			if (typeof content === 'string') {
				content = document.createTextNode(content);
			}

			cellElement.appendChild(content);
		});
	});

	tableElement.removeAttribute('data-sortable-initialized');
}

function formatClockTime(epochSeconds) {
	let date = new Date(epochSeconds * 1000);

	return date.toLocaleTimeString(undefined, {hour: 'numeric', minute: '2-digit'});
}

function formatDuration(seconds) {
	if ((seconds === null) || (seconds === undefined)) {
		return '-';
	}

	if (seconds < 60) {
		return Math.round(seconds) + 's';
	}

	let minutes = Math.round(seconds / 60);

	if (minutes < 60) {
		return minutes + 'm';
	}

	let hours = Math.floor(minutes / 60);

	if (hours < 24) {
		let remainingMinutes = minutes % 60;

		if (remainingMinutes === 0) {
			return hours + 'h';
		}

		return hours + 'h ' + remainingMinutes + 'm';
	}

	return Math.floor(hours / 24) + 'd ' + (hours % 24) + 'h';
}

function formatLongDuration(seconds) {
	if (seconds < 60) {
		return 'less than a minute';
	}

	let minutes = Math.round(seconds / 60);

	if (minutes < 60) {
		return minutes + ((minutes === 1) ? ' minute' : ' minutes');
	}

	let hours = Math.floor(minutes / 60);

	let text = hours + ((hours === 1) ? ' hour' : ' hours');

	let remainingMinutes = minutes % 60;

	if (remainingMinutes > 0) {
		text += ' ' + remainingMinutes + ((remainingMinutes === 1) ? ' minute' : ' minutes');
	}

	return text;
}

function formatTime(epochSeconds) {
	if ((epochSeconds === null) || (epochSeconds === undefined)) {
		return '-';
	}

	let date = new Date(epochSeconds * 1000);

	return date.toLocaleString(undefined, {day: 'numeric', hour: '2-digit', minute: '2-digit', month: 'short'});
}

function getColumns() {
	return {
		badSeconds: {key: 'badSeconds', label: 'Status Duration', render: (row) => getStatusDurationsText(row)},
		links: {key: 'links', label: 'Links', render: (row) => getLinksElement(row.links)},
		message: {key: 'message', label: 'Details', render: (row) => getMessageElement(row.message)},
		monitor: {key: 'monitor', label: 'Monitor', render: (row) => row.monitor},
		status: {key: 'status', label: 'Status', render: (row) => getStatusElement(row.status)},
		type: {key: 'type', label: 'Type', render: (row) => row.type}
	};
}

function getDurationTimeUnit(rangeSeconds) {
	if (rangeSeconds <= 3600) {
		return 'minute';
	}

	if (rangeSeconds <= 86400) {
		return 'hour';
	}

	return 'day';
}

function getLinksElement(links) {
	let divElement = document.createElement('div');

	links.forEach((link) => {
		let linkElement = document.createElement('div');

		if (/^https?:\/\//.test(link)) {
			let anchorElement = document.createElement('a');

			anchorElement.href = link;
			anchorElement.rel = 'noopener noreferrer';
			anchorElement.target = '_blank';

			anchorElement.appendChild(document.createTextNode(link));

			linkElement.appendChild(anchorElement);
		}
		else {
			linkElement.appendChild(document.createTextNode(link));
		}

		divElement.appendChild(linkElement);
	});

	return divElement;
}

function getMessageElement(message) {
	let divElement = document.createElement('div');

	divElement.classList.add('message');

	divElement.appendChild(document.createTextNode(message));

	return divElement;
}

function getMonitorRows(rangeSeconds) {
	let rangeStart = getRangeStart(rangeSeconds);
	let rangeEnd = reportData.end + reportData.step;

	return reportData.monitors.map((monitor) => {
		let badSeconds = 0;
		let clippedStatuses = {};
		let runs = [];
		let statusSeconds = {};

		monitor.runs.forEach((run) => {
			let runStart = Math.max(run[0], rangeStart);
			let runEnd = Math.min(run[1], rangeEnd);

			if (runEnd <= runStart) {
				return;
			}

			runs.push([runStart, runEnd, run[2]]);

			if (run[0] < rangeStart) {
				clippedStatuses[run[2]] = true;
			}

			badSeconds += runEnd - runStart;
			statusSeconds[run[2]] = (statusSeconds[run[2]] || 0) + runEnd - runStart;
		});

		return {
			badSeconds: badSeconds,
			clippedStatuses: clippedStatuses,
			links: monitor.links || [],
			message: monitor.message || '',
			monitor: monitor.monitor,
			reportedEnd: Math.min(rangeEnd, monitor.lastSampleTime + reportData.step),
			reportedStart: Math.max(rangeStart, monitor.firstSampleTime),
			runs: runs,
			status: monitor.status,
			statusSeconds: statusSeconds,
			type: monitor.type
		};
	});
}

function getRangeLabel(rangeSeconds) {
	if (rangeSeconds <= 3600) {
		return 'the last hour';
	}

	if (rangeSeconds <= 86400) {
		return 'the last 24 hours';
	}

	return 'the last 7 days';
}

function getRangeStart(rangeSeconds) {
	return reportData.end + reportData.step - rangeSeconds;
}

function getSeriesPoints(rangeStart, series) {
	let points = [];

	series.values.forEach((value, index) => {
		let time = series.start + (index * series.step);

		if ((time < rangeStart) || (value === null)) {
			return;
		}

		points.push({x: time * 1000, y: value});
	});

	return points;
}

function getStatusDurationsText(row) {
	let statusDurations = [];

	[4, 3, 2].forEach((status) => {
		let seconds = row.statusSeconds[status];

		if (!seconds) {
			return;
		}

		let statusDuration = STATUS_NAMES[status] + ' for ' + formatDuration(seconds);

		if (row.clippedStatuses[status]) {
			statusDuration += '+';
		}

		statusDurations.push(statusDuration);
	});

	if (statusDurations.length === 0) {
		return '-';
	}

	return statusDurations.join(', ');
}

function getStatusElement(status) {
	let spanElement = document.createElement('span');

	spanElement.classList.add('status');
	spanElement.classList.add('status-' + status);

	spanElement.appendChild(document.createTextNode(STATUS_NAMES[status]));

	return spanElement;
}

function isMonitorFilterMatch(filterText, row) {
	let monitor = row.monitor.toLowerCase();
	let type = row.type.toLowerCase();

	return monitor.includes(filterText) || type.includes(filterText);
}

function renderAllTimeline(rangeSeconds, rows) {
	let reportedRows = rows.filter((row) => row.reportedEnd > row.reportedStart);

	allTimelineChart = renderFilteredTimeline(
		allTimelineChart, 'ci-monitoring-all-timeline', 'No monitor reported in ' + getRangeLabel(rangeSeconds) + '.', rangeSeconds, 20,
		reportedRows);
}

function renderAttention(rows) {
	let attentionRows = rows.filter((row) => row.status > 0);

	attentionRows.sort((row1, row2) => compareRows(row1, row2, 'status', -1));

	let emptyText = 'No monitor reported when this report was generated.';
	let reportingRows = rows.filter((row) => row.status !== null);

	if (reportingRows.length > 0) {
		emptyText = 'Nothing needs attention. All ' + reportingRows.length + ' monitors are OK.';
	}

	let columns = getColumns();

	createMonitorTable(
		[columns.monitor, columns.type, columns.status, columns.badSeconds, columns.message, columns.links], emptyText, attentionRows,
		'ci-monitoring-attention-table');
}

function renderChangedTimeline(rangeSeconds, rows) {
	let changedRows = rows.filter((row) => row.runs.length > 0);

	timelineChart = renderFilteredTimeline(
		timelineChart, 'ci-monitoring-timeline', 'No monitor was WARN, CRITICAL, or STALE in ' + getRangeLabel(rangeSeconds) + '.', rangeSeconds, 28, changedRows);
}

function renderDuration(rangeSeconds) {
	let rangeStart = getRangeStart(rangeSeconds);

	let types = Object.keys(reportData.durationsByType);

	types.sort();

	let datasets = types.map((type, index) => {
		let durationsByType = reportData.durationsByType[type];

		let series = durationsByType.week;

		if (rangeSeconds <= 86400) {
			series = durationsByType.day;
		}

		let color = getColor(index);

		return {
			backgroundColor: color,
			borderColor: color,
			borderWidth: 1.5,
			data: getSeriesPoints(rangeStart, series),
			label: type,
			pointRadius: 0
		};
	});

	if (durationChart) {
		durationChart.destroy();
	}

	durationChart = new Chart(document.getElementById('ci-monitoring-duration-canvas'), {
		data: {
			datasets: datasets
		},
		options: {
			animation: false,
			interaction: {
				intersect: false,
				mode: 'nearest'
			},
			plugins: {
				tooltip: {
					callbacks: {
						label: function(context) {
							return context.dataset.label + ': ' + context.parsed.y.toFixed(2) + 's';
						}
					}
				}
			},
			responsive: true,
			scales: {
				x: {
					max: reportData.end * 1000,
					min: rangeStart * 1000,
					time: {
						displayFormats: {
							day: 'MMM d',
							hour: 'HH:mm',
							minute: 'HH:mm'
						},
						unit: getDurationTimeUnit(rangeSeconds)
					},
					type: 'time'
				},
				y: {
					beginAtZero: true,
					title: {
						display: true,
						text: 'Longest Run Time (s)'
					}
				}
			}
		},
		type: 'line'
	});
}

function renderFilteredTimeline(chart, elementIDPrefix, emptyText, rangeSeconds, rowHeight, rows) {
	let filterElement = document.getElementById(elementIDPrefix + '-filter');

	let filterValue = filterElement.value;

	let trimmedFilterValue = filterValue.trim();

	let filterText = trimmedFilterValue.toLowerCase();

	let filteredRows = rows.filter((row) => isMonitorFilterMatch(filterText, row));

	filteredRows.sort((row1, row2) => compareRows(row1, row2, 'monitor', 1));

	let containerElement = document.getElementById(elementIDPrefix + '-container');
	let emptyElement = document.getElementById(elementIDPrefix + '-empty');

	containerElement.hidden = filteredRows.length === 0;
	emptyElement.hidden = filteredRows.length > 0;

	if (filteredRows.length > 0) {
		return renderTimelineChart(elementIDPrefix + '-canvas', chart, elementIDPrefix + '-container', rangeSeconds, rowHeight, filteredRows);
	}

	if (filterText === '') {
		emptyElement.textContent = emptyText;
	}
	else {
		emptyElement.textContent = 'No monitors match the filter.';
	}

	return chart;
}

function renderGeneratedDate() {
	let dataDateElement = document.getElementById('ci-monitoring-data-date');

	dataDateElement.textContent = '';

	addDateText(dataDateElement, dataGeneratedDate);
}

function renderNextUpdate() {
	let nextUpdateSeconds = (dataGeneratedDate.getTime() / 1000) + UPDATE_INTERVAL_SECONDS;
	let nowSeconds = Date.now() / 1000;

	let text = 'This report updates every ' + (UPDATE_INTERVAL_SECONDS / 60) + ' minutes. ';

	if (nextUpdateSeconds > nowSeconds) {
		text += 'Next update around ' + formatTime(nextUpdateSeconds) + ' (in ' + formatLongDuration(nextUpdateSeconds - nowSeconds) + ').';
	}
	else {
		text += 'An update was expected around ' + formatTime(nextUpdateSeconds) + '. Reload the page to see it.';
	}

	let nextUpdateElement = document.getElementById('ci-monitoring-next-update');

	nextUpdateElement.textContent = text;
}

function renderNotes() {
	let text = 'A monitor is STALE when its last run is more than ' + formatLongDuration(reportData.staleSeconds) + ' old.';

	if (reportData.heartbeatAge !== null) {
		text = 'Monitor metrics were last written ' + formatLongDuration(reportData.heartbeatAge) + ' before this report was generated. ' + text;
	}

	let notesElement = document.getElementById('ci-monitoring-notes');

	notesElement.textContent = text;
}

function renderRange(rangeSeconds) {
	currentRangeSeconds = rangeSeconds;

	let rows = getMonitorRows(rangeSeconds);

	renderSectionTitles(rangeSeconds);
	renderSummary(rangeSeconds, rows);
	renderAttention(rows);
	renderTimelines(rangeSeconds, rows);
	renderDuration(rangeSeconds);

	RANGE_SECONDS.forEach((buttonRangeSeconds) => {
		let buttonElement = document.getElementById('range-' + buttonRangeSeconds);

		if (buttonRangeSeconds === rangeSeconds) {
			buttonElement.classList.add('active');
		}
		else {
			buttonElement.classList.remove('active');
		}
	});

	Sortable.init();
}

function renderSectionTitles(rangeSeconds) {
	let buttonElement = document.getElementById('range-' + rangeSeconds);

	let rangeText = 'the ' + buttonElement.textContent;

	let attentionTitleElement = document.getElementById('ci-monitoring-attention-title');

	attentionTitleElement.textContent = 'Monitors That Need Attention as of ' + formatTime(dataGeneratedDate.getTime() / 1000);

	let allTitleElement = document.getElementById('ci-monitoring-all-title');

	allTitleElement.textContent = 'All Monitors: Status History and Run Time in ' + rangeText;

	let timelineTitleElement = document.getElementById('ci-monitoring-timeline-title');

	timelineTitleElement.textContent = 'Monitors With WARN, CRITICAL, or STALE Status in ' + rangeText;
}

function renderSummary(rangeSeconds, rows) {
	let currentCounts = {0: 0, 2: 0, 3: 0, 4: 0};
	let rangeCounts = {0: 0, 2: 0, 3: 0, 4: 0};

	rows.forEach((row) => {
		if (row.status !== null) {
			currentCounts[row.status]++;
		}

		if (row.reportedEnd <= row.reportedStart) {
			return;
		}

		let seenStatuses = {};

		if (row.badSeconds < (row.reportedEnd - row.reportedStart)) {
			seenStatuses[0] = true;
		}

		row.runs.forEach((run) => {
			seenStatuses[run[2]] = true;
		});

		if (row.status !== null) {
			seenStatuses[row.status] = true;
		}

		let statuses = Object.keys(seenStatuses);

		statuses.forEach((status) => {
			rangeCounts[status]++;
		});
	});

	let summaryElement = document.getElementById('ci-monitoring-summary');

	summaryElement.innerHTML = '';

	let overallStatus = 0;

	rows.forEach((row) => {
		overallStatus = Math.max(overallStatus, row.status);
	});

	let reportingRows = rows.filter((row) => row.status !== null);

	let overallSubText = reportingRows.length + ' monitors';

	if (reportData.heartbeatAge === null) {
		overallStatus = 4;
		overallSubText = 'No monitor metrics found';
	}
	else if (reportData.heartbeatAge > reportData.staleSeconds) {
		overallStatus = 4;
		overallSubText = 'No new monitor metrics in ' + formatLongDuration(reportData.heartbeatAge);
	}

	let overallTileElement = document.createElement('div');

	overallTileElement.classList.add('summary-tile');
	overallTileElement.classList.add('summary-tile-' + overallStatus);

	let generatedTimeText = formatClockTime(dataGeneratedDate.getTime() / 1000);

	overallTileElement.appendChild(renderSummaryTilePart('summary-tile-label', 'Overall Status as of ' + generatedTimeText));

	let overallValueElement = renderSummaryTilePart('summary-tile-value', '');

	overallValueElement.appendChild(getStatusElement(overallStatus));

	overallTileElement.appendChild(overallValueElement);

	overallTileElement.appendChild(renderSummaryTilePart('summary-tile-sub', overallSubText));

	summaryElement.appendChild(overallTileElement);

	[0, 2, 3, 4].forEach((status) => {
		let tileElement = document.createElement('div');

		tileElement.classList.add('summary-tile');
		tileElement.classList.add('summary-tile-' + status);

		tileElement.appendChild(renderSummaryTilePart('summary-tile-label', STATUS_NAMES[status] + ' as of ' + generatedTimeText));
		tileElement.appendChild(renderSummaryTilePart('summary-tile-value', String(currentCounts[status])));
		tileElement.appendChild(
			renderSummaryTilePart(
				'summary-tile-sub',
				rangeCounts[status] + ((rangeCounts[status] === 1) ? ' monitor was ' : ' monitors were ') + STATUS_NAMES[status] +
					' at some point in ' + getRangeLabel(rangeSeconds)));

		summaryElement.appendChild(tileElement);
	});
}

function renderSummaryTilePart(className, text) {
	let divElement = document.createElement('div');

	divElement.classList.add(className);

	if (text) {
		divElement.appendChild(document.createTextNode(text));
	}

	return divElement;
}

function renderTimelineChart(canvasElementID, chart, containerElementID, rangeSeconds, rowHeight, rows) {
	let rangeStart = getRangeStart(rangeSeconds);
	let rangeEnd = reportData.end + reportData.step;

	let labels = rows.map((row) => row.monitor);

	let datasets = [
		{
			backgroundColor: STATUS_COLORS[0],
			barPercentage: 0.8,
			categoryPercentage: 1,
			data: rows.map((row, rowIndex) => ({x: [row.reportedStart * 1000, row.reportedEnd * 1000], y: labels[rowIndex]})),
			grouped: false,
			label: STATUS_NAMES[0],
			order: 4
		}
	];

	[2, 3, 4].forEach((status, index) => {
		let data = [];

		rows.forEach((row, rowIndex) => {
			row.runs.forEach((run) => {
				if (run[2] === status) {
					data.push({x: [run[0] * 1000, run[1] * 1000], y: labels[rowIndex]});
				}
			});
		});

		datasets.push({
			backgroundColor: STATUS_COLORS[status],
			barPercentage: 0.8,
			categoryPercentage: 1,
			data: data,
			grouped: false,
			label: STATUS_NAMES[status],
			minBarLength: 2,
			order: 3 - index
		});
	});

	let containerElement = document.getElementById(containerElementID);

	containerElement.style.height = Math.max(120, (rows.length * rowHeight) + 60) + 'px';

	if (chart) {
		chart.destroy();
	}

	return new Chart(document.getElementById(canvasElementID), {
		data: {
			datasets: datasets,
			labels: labels
		},
		options: {
			animation: false,
			indexAxis: 'y',
			maintainAspectRatio: false,
			plugins: {
				tooltip: {
					callbacks: {
						label: function(context) {
							let range = context.raw.x;

							return context.dataset.label + ': ' + formatTime(range[0] / 1000) + ' to ' + formatTime(range[1] / 1000) + ' (' +
								formatDuration((range[1] - range[0]) / 1000) + ')';
						}
					},
					filter: function(tooltipItem) {
						return tooltipItem.dataset.label !== STATUS_NAMES[0];
					}
				}
			},
			responsive: true,
			scales: {
				x: {
					max: rangeEnd * 1000,
					min: rangeStart * 1000,
					time: {
						displayFormats: {
							day: 'MMM d',
							hour: 'HH:mm',
							minute: 'HH:mm'
						},
						unit: getDurationTimeUnit(rangeSeconds)
					},
					type: 'time'
				},
				y: {
					stacked: true,
					ticks: {
						autoSkip: false
					}
				}
			}
		},
		type: 'bar'
	});
}

function renderTimelines(rangeSeconds, rows) {
	renderAllTimeline(rangeSeconds, rows);
	renderChangedTimeline(rangeSeconds, rows);
}

addReportName();

renderGeneratedDate();

RANGE_SECONDS.forEach((rangeSeconds) => {
	let buttonElement = document.getElementById('range-' + rangeSeconds);

	buttonElement.addEventListener('click', () => renderRange(rangeSeconds));
});

let allFilterElement = document.getElementById('ci-monitoring-all-timeline-filter');

allFilterElement.addEventListener('input', () => renderAllTimeline(currentRangeSeconds, getMonitorRows(currentRangeSeconds)));

let changedFilterElement = document.getElementById('ci-monitoring-timeline-filter');

changedFilterElement.addEventListener('input', () => renderChangedTimeline(currentRangeSeconds, getMonitorRows(currentRangeSeconds)));

let accordionButtonElements = document.getElementsByClassName('accordion');

for (let i = 0; i < accordionButtonElements.length; i++) {
	let accordionButtonElement = accordionButtonElements[i];

	let panelElement = accordionButtonElement.nextElementSibling;

	if (accordionButtonElement.classList.contains('collapsed')) {
		panelElement.style.display = 'none';
	}
	else {
		accordionButtonElement.classList.add('active');

		panelElement.style.display = 'block';
	}

	accordionButtonElement.addEventListener('click', function () {
		this.classList.toggle('active');

		if (panelElement.style.display === 'block') {
			panelElement.style.display = 'none';
		}
		else {
			panelElement.style.display = 'block';
		}
	});
}

if ((typeof reportData !== 'undefined') && reportData) {
	renderNextUpdate();
	renderNotes();

	renderRange(3600);

	setInterval(
		() => {
			renderGeneratedDate();
			renderNextUpdate();
		},
		30 * 1000);
}
