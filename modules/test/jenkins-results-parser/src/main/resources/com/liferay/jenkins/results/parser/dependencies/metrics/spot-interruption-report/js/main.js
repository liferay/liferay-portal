const SPOT_ASG_TYPES = ['bundle-builder', 'io', 'mem', 'pco', 'slave', 'unknown'];

function createSpotInterruptionTable(table, tableElementID) {
	let tableElement = document.getElementById(tableElementID);

	tableElement.innerHTML = '';

	let tbodyElement = tableElement.createTBody();

	table.forEach((cellValues, index) => {
		if (index == 0) {
			let theadElement = tableElement.createTHead();

			theadElement.classList.add('thead-light');

			let rowElement = theadElement.insertRow();

			cellValues.forEach((cellValue) => {
				let thElement = document.createElement('th');

				thElement.appendChild(document.createTextNode(cellValue));

				rowElement.appendChild(thElement);
			});

			return;
		}

		let rowElement = tbodyElement.insertRow();

		cellValues.forEach((cellValue, columnIndex) => {
			let cellElement = rowElement.insertCell();

			cellElement.setAttribute(
				'data-value', (cellValue === null) ? -1 : cellValue);

			if (table[0][columnIndex].includes('%')) {
				cellValue = (cellValue === null) ? '-' : cellValue + '%';
			}

			cellElement.appendChild(document.createTextNode(cellValue));
		});
	});

	tableElement.removeAttribute('data-sortable-initialized');

	return tableElement;
}

function getGroupNames(entries, tallyNames) {
	let groupNames = new Set();

	entries.forEach((entry) => {
		tallyNames.forEach((tallyName) => {
			let tallyGroupNames = Object.keys(entry[tallyName]);

			tallyGroupNames.forEach((groupName) => {
				groupNames.add(groupName);
			});
		});
	});

	let groupNamesArray = Array.from(groupNames);

	groupNamesArray.sort();

	return groupNamesArray;
}

function getRate(evictionCount, launchCount) {
	if ((launchCount == 0) || (evictionCount > launchCount)) {
		return null;
	}

	return Number(((evictionCount / launchCount) * 100).toFixed(1));
}

function getSpotChartDatasets() {
	let datasets = [];

	SPOT_ASG_TYPES.forEach((asgType, index) => {
		let launchTotal = 0;

		let dataPoints = dailyTally.map((entry) => {
			let evictionCount = entry.evictionsByASGType[asgType] || 0;
			let launchCount = entry.launchesByASGType[asgType] || 0;

			launchTotal += launchCount;

			return {
				x: getSpotDateMillis(entry.date),
				y: getRate(evictionCount, launchCount)
			};
		});

		if (launchTotal > 0) {
			let color = getColor(index);

			datasets.push({
				backgroundColor: color,
				borderColor: color,
				data: dataPoints,
				label: asgType
			});
		}
	});

	return datasets;
}

function getSpotDateMillis(dateString) {
	let date = new Date(dateString.slice(0, 4), dateString.slice(4, 6) - 1, dateString.slice(6, 8));

	return date.getTime();
}

function getTallySum(entries, groupName, tallyName) {
	let sum = 0;

	entries.forEach((entry) => {
		let tally = entry[tallyName];

		if (groupName in tally) {
			sum += tally[groupName];
		}
	});

	return sum;
}

function newCrossTable(entries, evictionsTallyName, groupHeader1, groupHeader2, launchesTallyName) {
	let table = [[groupHeader1, groupHeader2, 'Launches', 'Evictions', 'Interruption Rate (%)']];

	let groupNames = getGroupNames(entries, [evictionsTallyName, launchesTallyName]);

	groupNames.forEach((groupName) => {
		let groupNameParts = groupName.split('/');

		let evictionCount = getTallySum(entries, groupName, evictionsTallyName);
		let launchCount = getTallySum(entries, groupName, launchesTallyName);

		table.push([groupNameParts[0], groupNameParts[1], launchCount, evictionCount, getRate(evictionCount, launchCount)]);
	});

	return table;
}

function newGroupTable(entries, evictionsTallyName, groupHeader, launchesTallyName) {
	let table = [[groupHeader, 'Launches', 'Evictions', 'Interruption Rate (%)']];

	let groupNames = getGroupNames(entries, [evictionsTallyName, launchesTallyName]);

	groupNames.forEach((groupName) => {
		let evictionCount = getTallySum(entries, groupName, evictionsTallyName);
		let launchCount = getTallySum(entries, groupName, launchesTallyName);

		table.push([groupName, launchCount, evictionCount, getRate(evictionCount, launchCount)]);
	});

	return table;
}

function renderChart() {
	return new Chart(document.getElementById('spot-interruption-canvas'), {
		data: {
			datasets: getSpotChartDatasets()
		},
		options: {
			elements: {
				point: {
					hitRadius: 10,
					hoverRadius: 4,
					radius: 0
				}
			},
			maintainAspectRatio: false,
			plugins: {
				tooltip: {
					callbacks: {
						label: function(context) {
							let rate = context.parsed.y;

							if ((rate === null) || isNaN(rate)) {
								return context.dataset.label + ': -';
							}

							return context.dataset.label + ': ' + rate + '%';
						}
					},
					mode: 'index'
				}
			},
			responsive: true,
			scales: {
				x: {
					time: {
						displayFormats: {
							day: 'MMM d'
						},
						unit: 'day'
					},
					type: 'time'
				},
				y: {
					beginAtZero: true,
					ticks: {
						callback: function(value) {
							return value + '%';
						}
					},
					title: {
						display: true,
						text: 'Interruption Rate (%)'
					}
				}
			}
		},
		type: 'line'
	});
}

function renderRange(rangeDays) {
	let entries = dailyTally;

	if (rangeDays > 0) {
		entries = dailyTally.slice(-rangeDays);
	}

	createSpotInterruptionTable(newGroupTable(entries, 'evictionsByASGType', 'ASG Type', 'launchesByASGType'), 'spot-interruption-asg-type-table');
	createSpotInterruptionTable(newGroupTable(entries, 'evictionsByInstanceType', 'Instance Type', 'launchesByInstanceType'), 'spot-interruption-instance-type-table');
	createSpotInterruptionTable(newGroupTable(entries, 'evictionsByMaster', 'Master', 'launchesByMaster'), 'spot-interruption-master-table');
	createSpotInterruptionTable(newCrossTable(entries, 'evictionsByMasterASGType', 'Master', 'ASG Type', 'launchesByMasterASGType'), 'spot-interruption-master-asg-type-table');

	searchTable('spot-interruption-master-asg-type-search', 'spot-interruption-master-asg-type-table');

	[1, 7, 0].forEach((buttonRangeDays) => {
		let buttonElement = document.getElementById('range-' + buttonRangeDays);

		if (buttonRangeDays == rangeDays) {
			buttonElement.classList.add('active');
		}
		else {
			buttonElement.classList.remove('active');
		}
	});

	Sortable.init();
}

addReportName();

addDateText(document.getElementById('spot-interruption-data-date'), dataGeneratedDate);

[1, 7, 0].forEach((rangeDays) => {
	let buttonElement = document.getElementById('range-' + rangeDays);

	buttonElement.addEventListener('click', () => renderRange(rangeDays));
});

let searchInputElement = document.getElementById('spot-interruption-master-asg-type-search');

searchInputElement.addEventListener('keyup', () => searchTable('spot-interruption-master-asg-type-search', 'spot-interruption-master-asg-type-table'));

let accordionButtonElements = document.getElementsByClassName('accordion');

for (let i = 0; i < accordionButtonElements.length; i++) {
	let accordionButtonElement = accordionButtonElements[i];

	accordionButtonElement.classList.add('active');

	accordionButtonElement.nextElementSibling.style.display = 'block';

	accordionButtonElement.addEventListener('click', function () {
		this.classList.toggle('active');

		let panelElement = this.nextElementSibling;

		if (panelElement.style.display === 'block') {
			panelElement.style.display = 'none';
		}
		else {
			panelElement.style.display = 'block';
		}
	});
}

if ((typeof dailyTally !== 'undefined') && dailyTally) {
	document.getElementById('range-0').textContent =
		'Last ' + dailyTally.length + ' Days';

	renderChart();

	renderRange(7);
}