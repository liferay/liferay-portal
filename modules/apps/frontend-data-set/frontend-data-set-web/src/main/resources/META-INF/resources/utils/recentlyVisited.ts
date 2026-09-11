/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import functionalStorage from './functionalStorage';

const DEFAULT_MAX_ENTRIES = 8;

const STORAGE_KEY_PREFIX = 'LFR_RECENTLY_VISITED_';

export interface IRecentlyVisitedItem {
	href: string;
	label: string;
}

/**
 * Stores an item the user navigated to, most recent first.
 *
 * Items are identified by their URL, so visiting one again moves it to the
 * front under whichever label it carries now, rather than storing it twice. An
 * item without a URL or without a label is ignored, because the list is read as
 * a set of labels that navigate somewhere, which lets a caller hand over
 * whatever a row holds without vetting it first.
 *
 * @param fdsName Name of the Data Set the item belongs to
 * @param item The visited item to store
 * @param options Caps the history, evicting the least recently visited items.
 * Defaults to 8 entries.
 */
function add(
	fdsName: string,
	{href, label}: Partial<IRecentlyVisitedItem> = {},
	{maxEntries = DEFAULT_MAX_ENTRIES}: {maxEntries?: number} = {}
): IRecentlyVisitedItem[] {
	const visitedHref = _getText(href);
	const visitedLabel = _getText(label);

	if (!visitedHref || !visitedLabel) {
		return get(fdsName);
	}

	return _setRecentlyVisitedItems(
		fdsName,
		[
			{href: visitedHref, label: visitedLabel},
			...get(fdsName).filter(
				(recentlyVisitedItem) =>
					recentlyVisitedItem.href !== visitedHref
			),
		].slice(0, maxEntries)
	);
}

/**
 * Removes every stored item for a Data Set and returns the items stored
 * afterwards.
 *
 * @param fdsName Name of the Data Set
 */
function clear(fdsName: string): IRecentlyVisitedItem[] {
	functionalStorage.remove(_getStorageKey(fdsName));

	return get(fdsName);
}

/**
 * Returns the stored items for a Data Set, most recent first, or an empty array
 * when there are none or the stored value cannot be read.
 *
 * @param fdsName Name of the Data Set
 */
function get(fdsName: string): IRecentlyVisitedItem[] {
	const storageKey = _getStorageKey(fdsName);

	const recentlyVisitedItems = functionalStorage.get(storageKey);

	if (recentlyVisitedItems === null) {
		return [];
	}

	if (
		!Array.isArray(recentlyVisitedItems) ||
		!recentlyVisitedItems.every(_isRecentlyVisitedItem)
	) {
		functionalStorage.logWarning(storageKey, 'malformed data');

		return [];
	}

	return recentlyVisitedItems;
}

/**
 * Removes a single stored item, matched by its URL.
 *
 * @param fdsName Name of the Data Set
 * @param href URL of the item to remove
 */
function remove(fdsName: string, href: string): IRecentlyVisitedItem[] {
	return _setRecentlyVisitedItems(
		fdsName,
		get(fdsName).filter(
			(recentlyVisitedItem) => recentlyVisitedItem.href !== href
		)
	);
}

/**
 * Returns the text a caller passed, or nothing at all when it passed something
 * that is not text. A caller hands over whatever a row holds, and a field the
 * server translated arrives as a map of locales, which is no more a label than
 * an absent one is.
 */
function _getText(value?: string): string {
	if (typeof value !== 'string') {
		return '';
	}

	return value.trim();
}

function _getStorageKey(fdsName: string): string {
	return `${STORAGE_KEY_PREFIX}${fdsName}`;
}

function _isRecentlyVisitedItem(value: unknown): value is IRecentlyVisitedItem {
	const {href, label} = (value ?? {}) as IRecentlyVisitedItem;

	return typeof href === 'string' && typeof label === 'string';
}

// The items are read back rather than returned as written, so a write that
// browser storage rejects cannot leave the caller showing a history the Data
// Set does not have

function _setRecentlyVisitedItems(
	fdsName: string,
	recentlyVisitedItems: IRecentlyVisitedItem[]
): IRecentlyVisitedItem[] {
	functionalStorage.set(_getStorageKey(fdsName), recentlyVisitedItems);

	return get(fdsName);
}

export default {
	add,
	clear,
	get,
	remove,
};
