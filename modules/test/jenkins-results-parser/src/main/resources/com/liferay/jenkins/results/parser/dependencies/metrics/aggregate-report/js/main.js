function createBuildCountLineChart(timelineData, elementID) {
	let datasets = getDatasets(timelineData, 'buildCounts');

	let lineChart = getLineChart('Build Count', datasets, elementID, 'Builds');

	lineChart.options.plugins.title = {
		display: false
	};

	lineChart.options.plugins.tooltip = {
		callbacks: {
		    label: function(context) {
		        let label = context.dataset.label || '';

		        if (label) {
		            label += ': ';
		        }

		        label += context.parsed.y;

		        label += ' (';

		        label += Math.round(context.parsed.y * 10000 / (maxWeeklyServerDurationMillis || MAX_WEEKLY_SERVER_DURATION_MILLIS)) / 100;

		        label += '%)'

		        return label;
		    }
		},
		mode: 'index'
	};

	lineChart.update();
}

function createDurationLineChart(chartTitle, datasets, elementID) {
	let lineChart = getLineChart(chartTitle, datasets, elementID, 'Duration (mins)');

	lineChart.options.scales.y.ticks = {
		callback: function(value) {
			return Math.round(value / 60000);
		}
	};

	lineChart.options.plugins.tooltip = {
		callbacks: {
			label: function(context) {
			    let label = context.dataset.label || '';

			    if (label) {
			        label += ': ';
			    }

			    label += Math.round(context.parsed.y / 60000);

			    label += ' mins';

			    return label;
			}
		},
		mode: 'index'
	};

	lineChart.update();
}

function createBuildDurationLineChart(timelineData, elementID) {
	let datasets = getDatasets(timelineData, 'averageBuildTime');

	createDurationLineChart('Average Build Duration', datasets, elementID);
}

function createQueueDurationLineChart(timelineData, elementID) {
	let datasets = getDatasets(timelineData, 'averageQueueTime');

	createDurationLineChart('Average Queue Duration', datasets, elementID);
}

function getDataPoints(xDataPoints, yDataPoints) {
	let dataPoints = [];

	for (let i = 0; i < xDataPoints.length; i++) {
		let dataPoint = {
			x: xDataPoints[i],
			y: yDataPoints[i]
		};

		dataPoints.push(dataPoint);
	}

	return dataPoints;
}

function getDatasets(timelineData, key) {
	let datasets = [];

	for (let i = 0; i < timelineData.jobTimelines.length; i++) {
		let color = getColor(datasets.length);

		let jobTimeline = timelineData.jobTimelines[i];

		let dataset = {
			backgroundColor: color,
			borderColor: color,
			data: getDataPoints(timelineData.time, jobTimeline[key]),
			label: timelineData.jobTimelines[i].name
		};

		datasets.push(dataset);
	}

	return datasets;
}

function getLineChart(chartTitle, datasets, elementID, yLabel) {
	return new Chart(document.getElementById(elementID), {
		data: {
			datasets: datasets
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
				title: {
					display: true,
					font: {
						size: 14
					},
					text: chartTitle
				}
			},
			responsive: true,
			scales: {
				x: {
					ticks: {
						autoSkipPadding: 50,
						sampleSize: 100
					},
					time: {
					    displayFormats: {
					        hour: 'EEE MMM dd ha'
					    },
					    unit: 'hour'
					},
					type: 'time'
				},
				y: {
					beginAtZero: true,
					stacked: true,
					title: {
						display: true,
						text: yLabel
					}
				}
			}
		},
		type: 'line'
	});
}

addDateText(document.getElementById("build-history-data-date"), dataGeneratedDate);

let buttonElements = document.getElementsByClassName('accordion');

for (let i = 0; i < buttonElements.length; i++) {
	buttonElements[i].addEventListener('click', function () {
		this.classList.toggle('active');

		let panelDivElement = this.nextElementSibling;

		if (panelDivElement.style.display === 'block') {
			panelDivElement.style.display = 'none';
		}
		else {
			panelDivElement.style.display = 'block';
		}
	});
}

window.onload = function () {
	for (let i = 0; i < buttonElements.length; i++) {
		buttonElements[i].classList.toggle('active');
		buttonElements[i].nextElementSibling.style.display = 'block';
	}
}

if ((typeof timelineData !== 'undefined') && timelineData) {
	createBuildCountLineChart(timelineData, 'build-history-canvas');

	createBuildDurationLineChart(timelineData, 'build-duration-canvas');

	createQueueDurationLineChart(timelineData, 'queue-duration-canvas');
}

if ((typeof tableData !== 'undefined') && tableData) {
	let tableElement = createTable(tableData, 'build-history-table');

	addTotalColumn(tableElement);

	Sortable.init();
}