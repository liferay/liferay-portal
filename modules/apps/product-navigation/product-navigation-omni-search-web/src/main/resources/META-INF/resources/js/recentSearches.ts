/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {localStorage} from 'frontend-js-web';

const _RECENT_MAX = 5;

function _getStorageKey(): string {
	return `liferay-omni-search-recent-${Liferay.ThemeDisplay.getUserId()}`;
}

function getRecentSearches(): string[] {
	try {
		return JSON.parse(
			localStorage.getItem(
				_getStorageKey(),
				localStorage.TYPES.FUNCTIONAL
			) ?? '[]'
		);
	}
	catch {
		return [];
	}
}

function saveRecentSearch(query: string): string[] {
	return _persistSearches(
		[
			query,
			...getRecentSearches().filter(
				(item) => !_isSameSearch(item, query)
			),
		].slice(0, _RECENT_MAX)
	);
}

function deleteRecentSearch(query: string): string[] {
	return _persistSearches(
		getRecentSearches().filter((item) => !_isSameSearch(item, query))
	);
}

function _isSameSearch(a: string, b: string): boolean {
	return a.trim().toLowerCase() === b.trim().toLowerCase();
}

function _persistSearches(searches: string[]): string[] {
	localStorage.setItem(
		_getStorageKey(),
		JSON.stringify(searches),
		localStorage.TYPES.FUNCTIONAL
	);

	return searches;
}

export {deleteRecentSearch, getRecentSearches, saveRecentSearch};
