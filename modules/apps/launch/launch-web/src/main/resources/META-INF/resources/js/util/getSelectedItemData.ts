/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

export default function getSelectedItemData(
	selectedItem: unknown
): Record<string, string> {
	if (typeof selectedItem === 'string') {
		return _flatten(
			_parseJSONObject(selectedItem) || {value: selectedItem}
		);
	}

	if (!selectedItem || typeof selectedItem !== 'object') {
		return {};
	}

	const item = {...selectedItem} as Record<string, unknown>;

	if (typeof item.value === 'string') {
		const value = _parseJSONObject(item.value);

		if (value) {
			return _flatten({...value, ...item});
		}
	}

	return _flatten(item);
}

function _flatten(item: Record<string, unknown>): Record<string, string> {
	const selectedItemData: Record<string, string> = {};

	for (const [key, value] of Object.entries(item)) {
		if (value !== null && typeof value !== 'object') {
			selectedItemData[key] = String(value);
		}
	}

	return selectedItemData;
}

function _parseJSONObject(value: string): Record<string, unknown> | null {
	try {
		const parsed = JSON.parse(value);

		if (parsed && typeof parsed === 'object') {
			return parsed;
		}
	}
	catch (error) {
		return null;
	}

	return null;
}
