/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

const cache = {};

/**
 * Returns the selected field from the given key or return null.
 * This also check try to add a prefix to look for a specific field since older
 * Liferay versions didn't prefix ddm structure and vocabulary fields.
 *
 * @param {object} data
 * @param {Array}: data.fields Array of available fields for a given info type.
 * @param {string}: [data.mappingFieldsKey] Key used to store the mapping fields.
 * @param {string}: data.value Field key.
 * @return {object}
 */
export default function getSelectedField({fields, mappingFieldsKey, value}) {
	if (!value || !fields?.length) {
		return null;
	}

	const cacheKey = `${mappingFieldsKey}${value}`;

	if (mappingFieldsKey && cache[cacheKey]) {
		return cache[cacheKey];
	}

	const flattenFields = fields.flatMap((field) =>
		'fields' in field ? field.fields : [field]
	);

	const selectedField =
		flattenFields.find((field) => field.key === value) ||
		flattenFields.find((field) => field.name === value);

	if (selectedField) {
		if (mappingFieldsKey) {
			cache[cacheKey] = selectedField;
		}

		return selectedField;
	}

	return null;
}
