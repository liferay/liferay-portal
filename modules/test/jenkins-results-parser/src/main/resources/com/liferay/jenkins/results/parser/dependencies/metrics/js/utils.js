const COLORS = [
	'#59adf6',
	'#42d6a4',
	'#ff6961',
	'#ffb480',
	'#f8f38d',
	'#08cad1',
	'#9d94ff',
	'#c780e8',
	'#4472c4',
    '#ed7d31',
    '#70ad47',
    '#ffc000',
    '#a5a5a5',
    '#636363',
    '#ff85a1',
    '#4bacc6',
    '#8064a2',
    '#9bbb59'
];

const MAX_WEEKLY_SERVER_DURATION_MILLIS = 2370 * 7 * 24 * 60 * 60 * 1000;

function addDateText(element, date) {
	let dateText = document.createTextNode("Generated " + timeago.format(date) + " on " + date)

	element.appendChild(dateText);
}

function addReportName() {
	let headerElement = document.getElementById('report-name');

	headerElement.textContent = reportName;

	let titleElement = document.getElementById('title');

	titleElement.textContent = reportName;
}

function addTotalColumn(tableElement) {
	theadElement = tableElement.querySelector('thead tr');

	let totalHeaderElement = document.createElement('th');

	totalHeaderElement.textContent = 'Total';

	totalHeaderElement.classList.add('total-col');

	theadElement.appendChild(totalHeaderElement);

	let rowElements = tableElement.querySelectorAll('tbody tr');

	rowElements.forEach(rowElement => {
		let totalValue = 0;

		let cellElements = rowElement.querySelectorAll('td');

		for (let i = 2; i < cellElements.length; i++) {
			let value = parseFloat(cellElements[i].getAttribute('data-value'));

			if (!isNaN(value)) {
				totalValue += value;
			}
		}

		let totalCellElement = document.createElement('td');

		totalCellElement.setAttribute('data-value', totalValue);

		totalCellElement.classList.add('total-col');

		if (cellElements[1].textContent.includes('Duration')) {
			totalValue = getReadableDuration(totalValue);
		}

		totalCellElement.textContent = totalValue;

		rowElement.appendChild(totalCellElement);
	});
}

function getDynamicMax(datasets) {
    let indexTotals = [];

    datasets.forEach(dataset => {
        let data = dataset.data;

        data.forEach((dataValue, i) => {
            let indexTotal = parseFloat(dataValue) || 0;
            indexTotals[i] = (indexTotals[i] || 0) + indexTotal;
        });
    });

    let maxIndexTotal = Math.max(...indexTotals, 0);

    return Math.round(maxIndexTotal * 1.10);
}


function createBarChartFromTable(chartTitle, dataSuffix, elementID, metricName, tableElement) {
	headerElements = tableElement.querySelectorAll('thead tr th');

	let xLabels = [];

	let testSuiteReport = false;

	if (chartTitle == 'Daily Server Duration by Test Suite') {
		testSuiteReport = true;
	}

	headerElements.forEach(headerElement => {
		if (headerElement.classList.contains('col-1') || headerElement.classList.contains('col-2')) {
			return;
		}

		if (headerElement.textContent.trim() === 'Total' && testSuiteReport) {
			return;
		}

		xLabels.push(headerElement.textContent);
	});

	let datasets = [];
	let rowElements = tableElement.querySelectorAll('tbody tr');

	rowElements.forEach(rowElement => {
		let cellElements = rowElement.querySelectorAll('td');

		if ((cellElements[0].textContent === 'All') || (cellElements[0].textContent === '[Total]') || (cellElements[0].textContent === '[Unknown]')) {
			return;
		}

		if (cellElements[1].textContent !== metricName) {
			return;
		}

		let dataValues = [];

		cellElements.forEach(cellElement => {
			if (cellElement.classList.contains('col-1') || cellElement.classList.contains('col-2') || cellElement.classList.contains('total-col')) {
				return;
			}

			let dataValue = cellElement.getAttribute('data-value');

			if (testSuiteReport) {
				dataValue = dataValue / 3600000;
			}

			dataValues.push(Math.round(dataValue));
		});

		let color = getColor(datasets.length);

		let dataset = {
			backgroundColor: color,
			borderColor: color,
			data: dataValues,
			label: cellElements[0].textContent
		};

		datasets.push(dataset);
	});

	let yMax = 100;

	if (testSuiteReport) {
		yMax = getDynamicMax(datasets);
	}

	let barChart = new Chart(document.getElementById(elementID), {
		data: {
			datasets: datasets,
			labels: xLabels
		},
		options: {
			maintainAspectRatio: false,
			plugins: {
				title: {
					display: true,
					font: {
						size: 14
					},
					text: chartTitle
				},
				tooltip: {
					callbacks: {
						label: function(context) {
					        let label = context.dataset.label;
					        let dataDenomination = context.dataset.data[context.dataIndex];
					        let totaldataDenomination = 0;

					        for (let i = 0; i < context.chart.data.datasets.length; i++) {
					            totaldataDenomination += parseFloat(context.chart.data.datasets[i].data[context.dataIndex]);
					        }

					        if (context.datasetIndex != 0) {
					            return label + ' : ' + dataDenomination + dataSuffix;
					        }
					        else {
					            return [label + ' : ' + dataDenomination + dataSuffix, "Total : " + totaldataDenomination.toFixed(2) + dataSuffix];
					        }
						}
					},
					itemSort: function(a, b) {
						return b.datasetIndex - a.datasetIndex;
					},
					mode: 'index'
				}
			},
			responsive: true,
			scales: {
				x: {
					stacked: true
				},
				y: {
					beginAtZero: true,
					max: yMax,
					stacked: true,
					ticks: {
						callback: function(value) {
							return value + dataSuffix;
						}
					},
					title: {
						display: true,
						text: dataSuffix
					}
				}
			}
		},
		type: 'bar'
	});
}

function createTable(table, tableElementID) {
	let tableElement = document.getElementById(tableElementID);

	let tbodyElement = tableElement.createTBody();

	table.forEach((cellValues, index) => {
		if (index == 0) {
			let theadElement = tableElement.createTHead();

			theadElement.classList.add('thead-light');

			let rowElement = theadElement.insertRow();

			cellValues.forEach((cellValue, columnIndex) => {
				let thElement = document.createElement('th');

				if (columnIndex == 0) {
					thElement.classList.add('col-1');
				}
				else if (columnIndex == 1) {
					thElement.classList.add('col-2');
				}
				else {
					thElement.setAttribute('value', cellValue);

					let date = moment(cellValue, 'YYYYMMDD');

					cellValue = date.format('ddd MMM DD');
				}

				thElement.appendChild(document.createTextNode(cellValue));

				rowElement.appendChild(thElement);
			});

			return;
		}

		let rowElement = tbodyElement.insertRow();

		cellValues.forEach((cellValue, columnIndex) => {
			let cellElement = rowElement.insertCell();

			if (columnIndex == 0) {
				cellElement.classList.add('col-1');
				cellElement.classList.add('truncate');
			}

			if (columnIndex == 1) {
				cellElement.classList.add('col-2');
			}

			let node = null;

			if ((typeof cellValue === 'string') || (cellValue instanceof String)) {
				let divElement = document.createElement('div');
				let spanElement = document.createElement('span');

				spanElement.appendChild(document.createTextNode(cellValue));

				divElement.appendChild(spanElement);

				divElement.setAttribute('data-value', cellValue);

				node = divElement;
			}
			else {
				cellElement.setAttribute('data-value', cellValue);

				if (cellValues[1].includes('Duration')) {
					cellValue = getReadableDuration(cellValue);
				}

				node = document.createTextNode(cellValue);
			}

			cellElement.appendChild(node);

		});

	});

	return tableElement;
}

function getColor(index) {
	return COLORS[index % COLORS.length];
}

function getElementByXpath(path) {
	return document.evaluate(path, document, null, XPathResult.FIRST_ORDERED_NODE_TYPE, null).singleNodeValue;
}

function getReadableDuration(milliseconds) {
	if (milliseconds == 0) {
		return milliseconds;
	}

	let duration = moment.duration(milliseconds);

	if (duration.asHours() >= 10) {
		return Math.round(duration.asHours()) + ' hours';
	}

	if (duration.asHours() >= 1) {
		return Math.round(duration.asHours() * 10) / 10 + ' hours';
	}

	if (Math.round(duration.asMinutes()) >= 1) {
		return Math.round(duration.asMinutes()) + ' minutes';
	}

	return '< 1 minute';
}

function searchTable(inputID, tableID) {
	let inputElement = document.getElementById(inputID);
	let tableElement = document.getElementById(tableID);

	let inputValue = inputElement.value.toUpperCase();

	let rowElements = tableElement.getElementsByTagName('tr');

	for (let i = 0; i < rowElements.length; i++) {
		let cellElements = rowElements[i].getElementsByTagName('td');

		for (let j = 0; j < cellElements.length; j++) {
			if (j < 2) {
				let cellValue = cellElements[j].textContent || cellElements[j].innerText;

				cellValue = cellValue.toUpperCase();

				if (cellValue.indexOf(inputValue) > -1) {
					rowElements[i].style.display = '';

					break;
				}
				else {
					rowElements[i].style.display = 'none';
				}
			}
		}
	}
}

function triggerEvent(element, eventName) {
	element.dispatchEvent(new Event(eventName));
}