/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {getItemLabel} from './getItemLabel';
import recentlyVisited from './recentlyVisited';

/**
 * Remembers where a row led, so that the search suggestions can offer it back.
 *
 * Every way a listing hands the user off to content comes through here, so that
 * a row is remembered under one name however the user left for it and a Data
 * Set that was never asked for suggestions remembers nothing at all.
 *
 * Only the caller knows whether a click leaves the page, so a click that opens
 * a modal or a side panel records nothing by not calling this.
 *
 * @param accessibleNameField Field the active view names its rows by
 * @param fdsName Name of the Data Set the row belongs to
 * @param href Where the row led
 * @param itemData The row the user is leaving for
 * @param label What to fall back on, typically the text the user clicked
 * @param searchSuggestionsEnabled Whether the Data Set offers suggestions
 */
export function recordVisit({
	accessibleNameField,
	fdsName,
	href,
	itemData,
	label,
	searchSuggestionsEnabled,
}: {
	accessibleNameField?: string;
	fdsName: string;
	href?: string;
	itemData?: any;
	label?: string;
	searchSuggestionsEnabled?: boolean;
}): void {
	if (!searchSuggestionsEnabled) {
		return;
	}

	recentlyVisited.add(fdsName, {
		href,
		label: getItemLabel(itemData, {accessibleNameField, fallback: label}),
	});
}
