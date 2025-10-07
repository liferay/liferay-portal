/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {fetch, getObjectValueFromPath} from 'frontend-js-web';

import {DEFAULT_FETCH_HEADERS} from '../constants';
import getValueFromItem from './getValueFromItem';

export function getData(apiURL, query) {
	const url = new URL(apiURL);

	if (query) {
		url.searchParams.append('search', query);
	}

	return fetch(url, {
		headers: DEFAULT_FETCH_HEADERS,
		method: 'GET',
	}).then((data) => data.json());
}

export function isValuesArrayChanged(prevValue = [], newValue = []) {
	if (prevValue.length !== newValue.length) {
		return true;
	}

	const prevValues = prevValue
		.map((element) => element.value || element)
		.sort();
	const newValues = newValue
		.map((element) => element.value || element)
		.sort();

	let changed = false;

	prevValues.forEach((element, i) => {
		if (element !== newValues[i]) {
			changed = true;
		}
	});

	return changed;
}

export function formatItemChanges(itemChanges) {
	const formattedChanges = Object.values(itemChanges).reduce(
		(changes, {value, valuePath}) => {
			const nestedValue = valuePath.reduceRight((acc, item) => {
				return {[item]: acc};
			}, value);

			return {
				...changes,
				...nestedValue,
			};
		},
		{}
	);

	return formattedChanges;
}

export function getCurrentItemUpdates(
	items,
	itemsChanges,
	selectedItemsKey,
	itemKey,
	property,
	value,
	valuePath
) {
	const itemChanged = items.find(
		(item) =>
			getObjectValueFromPath({object: item, path: selectedItemsKey}) ===
			itemKey
	);

	const itemChanges = itemsChanges[itemKey];

	if (!itemChanges) {
		return {
			[property]: {
				value,
				valuePath,
			},
		};
	}

	if (itemChanged && getValueFromItem(itemChanged, valuePath) === value) {
		const filteredProperties = Object.entries(itemChanges).reduce(
			(properties, [propertyKey, propertyValue]) => {
				return propertyKey !== property
					? {...properties, [propertyKey]: propertyValue}
					: properties;
			},
			{}
		);

		return filteredProperties;
	}

	return {
		...itemChanges,
		[property]: {
			value,
			valuePath,
		},
	};
}
