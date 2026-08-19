/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {MappingField, MappingFields} from '../../types/MappingField';

const cache: Record<string, MappingField> = {};

/**
 * Returns the selected field from the given key or return null.
 * This also check try to add a prefix to look for a specific field since older
 * Liferay versions didn't prefix ddm structure and vocabulary fields.
 */

export default function getSelectedField({
	fields,
	mappingFieldsKey,
	value,
}: {
	fields?: MappingFields;
	mappingFieldsKey?: string;
	value?: string;
}) {
	if (!value || !fields?.length) {
		return null;
	}

	const cacheKey = `${mappingFieldsKey}${value}`;

	if (mappingFieldsKey && cache[cacheKey]) {
		return cache[cacheKey];
	}

	const flattenFields = fields
		.flatMap((field) => ('fields' in field ? field.fields : [field]))
		.filter((field): field is MappingField => !('fields' in field));

	const selectedField =
		flattenFields.find((field) => field.externalKey === value) ||
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
