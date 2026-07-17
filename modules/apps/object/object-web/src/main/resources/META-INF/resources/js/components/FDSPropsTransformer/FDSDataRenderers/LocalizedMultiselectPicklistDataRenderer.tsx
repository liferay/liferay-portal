/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import getLocalizedFieldValue from './getLocalizedFieldValue';

interface ListEntry {
	key: string;
	name: string;
}

interface LocalizedMultiselectPicklistDataRendererProps {
	itemData: {[key: string]: any};
	options: {fieldName: string};
	value?: any;
}

export default function LocalizedMultiselectPicklistDataRenderer({
	itemData,
	options,
	value,
}: LocalizedMultiselectPicklistDataRendererProps) {
	const localizedFieldValue = getLocalizedFieldValue(
		itemData,
		options?.fieldName
	);

	const listEntries =
		localizedFieldValue === undefined ? value : localizedFieldValue;

	if (Array.isArray(listEntries)) {
		return listEntries
			.map((listEntry: ListEntry) => listEntry.name)
			.join(', ');
	}

	return listEntries ?? '';
}
