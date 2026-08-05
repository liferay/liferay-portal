/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

/**
 * Resolves the value that relates an item to a relationship field. The value
 * is read through valueKey before falling back to the DTO id because system
 * objects such as Commerce Products are related through an identifier
 * property that differs from the DTO id.
 */
export function getItemValue(
	item: {[key: string]: unknown} | undefined,
	valueKey: string
): number | string | undefined {
	const value = item?.[valueKey] ?? item?.id;

	if (typeof value === 'number' || typeof value === 'string') {
		return value;
	}

	return undefined;
}
