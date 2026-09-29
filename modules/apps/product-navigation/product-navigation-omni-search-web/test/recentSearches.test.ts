/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {localStorage} from 'frontend-js-web';

import {
	deleteRecentSearch,
	getRecentSearches,
	saveRecentSearch,
} from '../src/main/resources/META-INF/resources/js/recentSearches';

const STORAGE_KEY = `liferay-omni-search-recent-${Liferay.ThemeDisplay.getUserId()}`;

describe('recentSearches', () => {
	afterEach(() => {
		localStorage.removeItem(STORAGE_KEY);
	});

	describe('getRecentSearches', () => {
		it('returns an empty array when nothing is stored', () => {
			expect(getRecentSearches()).toEqual([]);
		});

		it('returns an empty array when the stored value is not valid JSON', () => {
			localStorage.setItem(
				STORAGE_KEY,
				'invalid',
				localStorage.TYPES.FUNCTIONAL
			);

			expect(getRecentSearches()).toEqual([]);
		});
	});

	describe('saveRecentSearch', () => {
		it('stores a query and returns the list', () => {
			expect(saveRecentSearch('blogs')).toEqual(['blogs']);
		});

		it('adds new queries most recent first', () => {
			saveRecentSearch('blogs');

			expect(saveRecentSearch('documents')).toEqual([
				'documents',
				'blogs',
			]);
		});

		it('moves an existing query to the front without duplicating it', () => {
			saveRecentSearch('blogs');
			saveRecentSearch('documents');

			expect(saveRecentSearch('blogs')).toEqual(['blogs', 'documents']);
		});

		it('caps at 5 entries, evicting the oldest', () => {
			['a', 'b', 'c', 'd', 'e'].forEach((query) =>
				saveRecentSearch(query)
			);

			const result = saveRecentSearch('f');

			expect(result).toHaveLength(5);
			expect(result[0]).toBe('f');
			expect(result).not.toContain('a');
		});
	});

	describe('deleteRecentSearch', () => {
		it('removes a query and returns the remaining list', () => {
			saveRecentSearch('blogs');
			saveRecentSearch('documents');

			expect(deleteRecentSearch('blogs')).toEqual(['documents']);
		});

		it('returns the list unchanged when the query is not stored', () => {
			saveRecentSearch('blogs');

			expect(deleteRecentSearch('documents')).toEqual(['blogs']);
		});
	});
});
