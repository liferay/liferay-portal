/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {getLocalizedValue} from './getLocalizedValue';

/**
 * Returns the name a row goes by, read the way the rest of the Data Set reads a
 * field, so that a title the server translated arrives as a string rather than
 * as the map of locales it is stored in.
 *
 * Every way a listing leads somewhere names its entry through here, so that the
 * same row is remembered under the same name whether the user followed the link
 * in a cell or the action behind the item actions menu.
 *
 * The field a view names its rows by for a screen reader is the one the Data
 * Set was configured with, so it is asked first. What that view falls back on
 * is not followed, because the first column and the identifier behind it read
 * as a name to somebody hearing one row at a time and as nothing at all in a
 * list of places to return to.
 *
 * @param itemData The row the user is leaving for
 * @param accessibleNameField Field the active view names its rows by
 * @param fallback What to fall back on for a row that goes by no name of its
 * own, typically the text the user clicked
 */
export function getItemLabel(
	itemData: any,
	{
		accessibleNameField,
		fallback,
	}: {accessibleNameField?: string; fallback?: string} = {}
): string {
	if (!itemData) {
		return _getText(fallback);
	}

	return (
		_getText(getLocalizedValue(itemData, accessibleNameField!)?.value) ||
		_getText(getLocalizedValue(itemData, 'title')?.value) ||
		_getText(getLocalizedValue(itemData, 'name')?.value) ||
		_getText(fallback)
	);
}

function _getText(value: unknown): string {
	if (typeof value !== 'string') {
		return '';
	}

	return value.trim();
}
