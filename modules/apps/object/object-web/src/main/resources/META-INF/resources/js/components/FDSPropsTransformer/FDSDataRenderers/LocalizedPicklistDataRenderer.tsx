/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import getLocalizedFieldValue from './getLocalizedFieldValue';

interface ListEntry {
	key: string;
	name: string;
}

interface LocalizedPicklistDataRendererProps {
	itemData: {[key: string]: any};
	options: {fieldName: string};
	value?: any;
}

export default function LocalizedPicklistDataRenderer({
	itemData,
	options,
	value,
}: LocalizedPicklistDataRendererProps) {
	const localizedFieldValue = getLocalizedFieldValue(
		itemData,
		options?.fieldName
	);

	if (localizedFieldValue === undefined) {
		return (typeof value === 'object' ? value?.name : value) ?? '';
	}

	return (localizedFieldValue as ListEntry)?.name ?? '';
}
