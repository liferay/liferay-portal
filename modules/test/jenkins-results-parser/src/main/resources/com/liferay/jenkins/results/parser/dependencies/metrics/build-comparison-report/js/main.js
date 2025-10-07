function addDateText(element, date) {
	let dateText = document.createTextNode("Generated " + timeago.format(date) + " on " + date)

	element.appendChild(dateText);
}

function createAnchorElement(link, text) {
	let anchorElement = document.createElement('a');

	anchorElement.href = link;
	anchorElement.appendChild(document.createTextNode(text));

	return anchorElement;
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

			let node = null;

			if ((typeof cellValue === 'string') || (cellValue instanceof String)) {
				if (cellValue.includes('http:') || cellValue.includes('https:')) {
					let hyperlinkText;

					if (table[0][columnIndex].includes('Build URL')) {
						hyperlinkText = "Build URL";
					}
					else if (table[0][columnIndex].includes('Comparison URL')){
						hyperlinkText = "Comparison URL"
					}
					else if (table[0][columnIndex].includes('Testray URL')) {
						hyperlinkText = "Testray URL";
					}

					let anchorElement = createAnchorElement(cellValue, hyperlinkText);

					node = anchorElement;
				}
				else {
					let divElement = document.createElement('div');
					let spanElement = document.createElement('span');

					spanElement.appendChild(document.createTextNode(cellValue));

					divElement.appendChild(spanElement);

					divElement.setAttribute('data-value', cellValue);

					node = divElement;
				}
			}
			else {
				cellElement.setAttribute('data-value', cellValue);

				let headerValue = table[0][columnIndex];

				if (headerValue.includes('%')) {
					cellValue = cellValue + '%';
				}
				else if (headerValue.includes('Duration')) {
					cellValue = getReadableDuration(cellValue);
				}
				else if (headerValue.includes('Start Time')) {
					let date = new Date(cellValue);

					cellValue = date.toLocaleString();
				}

				node = document.createTextNode(cellValue);
			}

			cellElement.appendChild(node);
		});
	});

	return tableElement;
}

window.onload = function () {
	var statusChangesRowHeader = getElementByXpath('//th[contains(.,"Top Level Start Time (DB)")]');

	triggerEvent(statusChangesRowHeader, 'click');
}

addReportName();

addDateText(document.getElementById("build-comparison-data-date"), dataGeneratedDate);

if ((typeof tableData !== 'undefined') && tableData) {
	let tableElement = createTable(tableData, 'build-comparison-data-table');

	Sortable.init();
}