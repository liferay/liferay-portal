/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import css from './styles/index.css';

import type {
	FDSVisualizationMode,
	FDSVisualizationModeArgs,
	FDSVisualizationModeItem,
	FDSVisualizationModeSchema,
	FDSVisualizationModeSelection,
} from '@liferay/js-api/data-set';

const STYLE_ELEMENT_ID = 'liferay-sample-fds-timeline-style';

function addStyle() {
	if (document.getElementById(STYLE_ELEMENT_ID)) {
		return;
	}

	const style = document.createElement('style');

	style.id = STYLE_ELEMENT_ID;
	style.textContent = css;

	document.head.append(style);
}

function getValue(
	item: FDSVisualizationModeItem,
	name: string,
	schema?: FDSVisualizationModeSchema
) {
	return item[schema?.[name] ?? name];
}

function createEntry(
	dateTimeFormat: Intl.DateTimeFormat,
	item: FDSVisualizationModeItem,
	schema?: FDSVisualizationModeSchema,
	selection?: FDSVisualizationModeSelection
) {
	const entry = document.createElement('li');

	entry.className = 'liferay-sample-fds-timeline-entry';

	const dateValue = getValue(item, 'date', schema);

	const date = typeof dateValue === 'string' ? new Date(dateValue) : null;

	if (date) {
		const time = document.createElement('time');

		time.className =
			'liferay-sample-fds-timeline-date small text-secondary';
		time.dateTime = date.toISOString();
		time.textContent = dateTimeFormat.format(date);

		entry.append(time);
	}

	const title = document.createElement(selection ? 'label' : 'div');

	title.className = 'd-flex font-weight-semi-bold mb-1';

	if (selection) {
		const checkbox = document.createElement('input');

		checkbox.checked = selection.selectedValues.includes(
			item[selection.itemsKey]
		);
		checkbox.className = 'mr-2';
		checkbox.type = 'checkbox';

		checkbox.addEventListener('change', () => selection.toggleItem(item));

		title.append(checkbox);
	}

	title.append(String(getValue(item, 'title', schema) ?? ''));

	entry.append(title);

	const descriptionValue = getValue(item, 'description', schema);

	if (descriptionValue) {
		const description = document.createElement('p');

		description.className = 'mb-0 text-secondary';
		description.textContent = String(descriptionValue);

		entry.append(description);
	}

	return entry;
}

const fdsVisualizationMode: FDSVisualizationMode = (container) => {
	const dateTimeFormat = new Intl.DateTimeFormat(
		document.documentElement.lang || undefined,
		{dateStyle: 'medium', timeZone: 'UTC'}
	);

	addStyle();

	const timeline = document.createElement('ol');

	timeline.ariaLabel = 'Timeline';
	timeline.className = 'liferay-sample-fds-timeline';

	container.append(timeline);

	const draw = ({items, schema, selection}: FDSVisualizationModeArgs) => {
		timeline.replaceChildren(
			...items.map((item) =>
				createEntry(dateTimeFormat, item, schema, selection)
			)
		);
	};

	return {
		destroy: () => {},
		update: draw,
	};
};

export default fdsVisualizationMode;
