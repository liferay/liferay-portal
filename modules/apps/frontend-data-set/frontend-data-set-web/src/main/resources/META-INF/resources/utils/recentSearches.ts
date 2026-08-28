/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {checkConsent, localStorage} from 'frontend-js-web';

const DEFAULT_MAX_ENTRIES = 20;

const STORAGE_KEY_PREFIX = 'LFR_RECENT_SEARCHES_';

/**
 * Stores a search query, most recent first.
 *
 * Queries are deduplicated case insensitively, and a query that continues a
 * stored query within the same word replaces it, so typing "pant" and then
 * "pantalon" leaves only "pantalon". A query that merely extends a stored one
 * at a word boundary is kept alongside it, because the two read as different
 * intents: "lego" and "lego star wars" are both stored.
 *
 * @param fdsName Name of the Data Set the query belongs to
 * @param query The search query to store
 * @param options Caps the history, evicting the oldest queries. Defaults to 20
 * entries.
 */
function add(
	fdsName: string,
	query: string,
	{maxEntries = DEFAULT_MAX_ENTRIES}: {maxEntries?: number} = {}
): void {
	const search = query.trim();

	if (!search) {
		return;
	}

	const recentSearches = get(fdsName);

	if (
		recentSearches.some((recentSearch) =>
			_continuesWord(search, recentSearch)
		)
	) {
		return;
	}

	_setRecentSearches(
		fdsName,
		[
			search,
			...recentSearches.filter(
				(recentSearch) =>
					!_isSameSearch(recentSearch, search) &&
					!_continuesWord(recentSearch, search)
			),
		].slice(0, maxEntries)
	);
}

/**
 * Removes every stored search query for a Data Set.
 *
 * @param fdsName Name of the Data Set
 */
function clear(fdsName: string): void {
	try {
		_checkConsentFunctionalCookies();

		localStorage.removeItem(_getStorageKey(fdsName));
	}
	catch (error) {
		_logStorageWarning(error);
	}
}

/**
 * Returns the stored search queries for a Data Set, most recent first, or an
 * empty array when there are none or the stored value cannot be read.
 *
 * @param fdsName Name of the Data Set
 */
function get(fdsName: string): string[] {
	let recentSearches;

	try {
		_checkConsentFunctionalCookies();

		recentSearches = JSON.parse(
			localStorage.getItem(
				_getStorageKey(fdsName),
				localStorage.TYPES.FUNCTIONAL
			) as string
		);
	}
	catch (error) {
		_logStorageWarning(error);

		return [];
	}

	if (
		!Array.isArray(recentSearches) ||
		recentSearches.some((recentSearch) => typeof recentSearch !== 'string')
	) {
		_logStorageWarning('malformed data');

		return [];
	}

	return recentSearches;
}

/**
 * Removes a single stored search query, matched case insensitively.
 *
 * @param fdsName Name of the Data Set
 * @param query The search query to remove
 */
function remove(fdsName: string, query: string): void {
	_setRecentSearches(
		fdsName,
		get(fdsName).filter(
			(recentSearch) => !_isSameSearch(recentSearch, query)
		)
	);
}

function _checkConsentFunctionalCookies() {
	if (!checkConsent(localStorage.TYPES.FUNCTIONAL)) {
		throw new Error('There is no consent for functional cookies');
	}
}

function _continuesWord(prefix: string, search: string): boolean {
	const normalizedPrefix = _normalize(prefix);
	const normalizedSearch = _normalize(search);

	return (
		normalizedSearch.length > normalizedPrefix.length &&
		normalizedSearch.startsWith(normalizedPrefix) &&
		normalizedSearch[normalizedPrefix.length] !== ' '
	);
}

function _getStorageKey(fdsName: string): string {
	return `${STORAGE_KEY_PREFIX}${fdsName}`;
}

function _isSameSearch(search: string, otherSearch: string): boolean {
	return _normalize(search) === _normalize(otherSearch);
}

function _normalize(search: string): string {
	return search.trim().toLowerCase();
}

function _logStorageWarning(error: unknown) {
	if (process.env.NODE_ENV === 'development') {
		console.warn(
			'Recent searches could not be accessed in browser storage',
			error
		);
	}
}

function _setRecentSearches(fdsName: string, recentSearches: string[]): void {
	try {
		_checkConsentFunctionalCookies();

		localStorage.setItem(
			_getStorageKey(fdsName),
			JSON.stringify(recentSearches),
			localStorage.TYPES.FUNCTIONAL
		);
	}
	catch (error) {
		_logStorageWarning(error);
	}
}

export default {
	add,
	clear,
	get,
	remove,
};
