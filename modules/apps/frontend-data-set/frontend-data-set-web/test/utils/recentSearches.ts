/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {localStorage} from 'frontend-js-web';

import recentSearches from '../../src/main/resources/META-INF/resources/utils/recentSearches';

const FDS_NAME = 'FDS_NAME';
const OTHER_FDS_NAME = 'OTHER_FDS_NAME';

const STORAGE_KEY = `LFR_RECENT_SEARCHES_${FDS_NAME}`;

function setStoredValue(value: string) {
	localStorage.setItem(STORAGE_KEY, value, localStorage.TYPES.FUNCTIONAL);
}

describe('recentSearches', () => {
	afterEach(() => {
		localStorage.clear();
	});

	describe('add', () => {
		it('stores a query', () => {
			recentSearches.add(FDS_NAME, 'pantalon');

			expect(recentSearches.get(FDS_NAME)).toEqual(['pantalon']);
		});

		it('trims surrounding whitespace', () => {
			recentSearches.add(FDS_NAME, '  pantalon  ');

			expect(recentSearches.get(FDS_NAME)).toEqual(['pantalon']);
		});

		it('ignores an empty or whitespace only query', () => {
			recentSearches.add(FDS_NAME, '');
			recentSearches.add(FDS_NAME, '   ');

			expect(recentSearches.get(FDS_NAME)).toEqual([]);
		});

		it('returns unrelated queries most recent first', () => {
			recentSearches.add(FDS_NAME, 'blogs');
			recentSearches.add(FDS_NAME, 'documents');

			expect(recentSearches.get(FDS_NAME)).toEqual([
				'documents',
				'blogs',
			]);
		});

		it('moves a repeated query to the front without duplicating it', () => {
			recentSearches.add(FDS_NAME, 'blogs');
			recentSearches.add(FDS_NAME, 'documents');
			recentSearches.add(FDS_NAME, 'blogs');

			expect(recentSearches.get(FDS_NAME)).toEqual([
				'blogs',
				'documents',
			]);
		});

		it('deduplicates a repeated query case insensitively', () => {
			recentSearches.add(FDS_NAME, 'Pantalon');
			recentSearches.add(FDS_NAME, 'pantalon');

			expect(recentSearches.get(FDS_NAME)).toEqual(['pantalon']);
		});

		it('replaces a stored query the new query continues within a word', () => {
			recentSearches.add(FDS_NAME, 'pant');
			recentSearches.add(FDS_NAME, 'pantalon');

			expect(recentSearches.get(FDS_NAME)).toEqual(['pantalon']);
		});

		it('replaces every stored query the new query continues within a word', () => {
			recentSearches.add(FDS_NAME, 'pa');
			recentSearches.add(FDS_NAME, 'pan');
			recentSearches.add(FDS_NAME, 'pant');
			recentSearches.add(FDS_NAME, 'pantalon');

			expect(recentSearches.get(FDS_NAME)).toEqual(['pantalon']);
		});

		it('discards a new query a stored query continues within a word', () => {
			recentSearches.add(FDS_NAME, 'pantalon');
			recentSearches.add(FDS_NAME, 'pant');

			expect(recentSearches.get(FDS_NAME)).toEqual(['pantalon']);
		});

		it('keeps both queries when the shorter one ends at a word boundary', () => {
			recentSearches.add(FDS_NAME, 'lego');
			recentSearches.add(FDS_NAME, 'lego star wars');

			expect(recentSearches.get(FDS_NAME)).toEqual([
				'lego star wars',
				'lego',
			]);
		});

		it('replaces a stored query continued within the last word of the new query', () => {
			recentSearches.add(FDS_NAME, 'lego s');
			recentSearches.add(FDS_NAME, 'lego star wars');

			expect(recentSearches.get(FDS_NAME)).toEqual(['lego star wars']);
		});

		it('evicts the oldest query beyond the given maximum', () => {
			recentSearches.add(FDS_NAME, 'a', {maxEntries: 2});
			recentSearches.add(FDS_NAME, 'b', {maxEntries: 2});
			recentSearches.add(FDS_NAME, 'c', {maxEntries: 2});

			expect(recentSearches.get(FDS_NAME)).toEqual(['c', 'b']);
		});

		it('stores at most twenty queries by default', () => {
			const queries = Array.from({length: 21}, (value, index) =>
				String.fromCharCode(97 + index)
			);

			queries.forEach((query) => recentSearches.add(FDS_NAME, query));

			const storedSearches = recentSearches.get(FDS_NAME);

			expect(storedSearches).toHaveLength(20);
			expect(storedSearches[0]).toBe('u');
			expect(storedSearches).not.toContain('a');
		});

		it('isolates queries per Data Set', () => {
			recentSearches.add(FDS_NAME, 'pantalon');
			recentSearches.add(OTHER_FDS_NAME, 'camiseta');

			expect(recentSearches.get(FDS_NAME)).toEqual(['pantalon']);
			expect(recentSearches.get(OTHER_FDS_NAME)).toEqual(['camiseta']);
		});
	});

	describe('clear', () => {
		it('removes every query of a Data Set and leaves the other Data Sets alone', () => {
			recentSearches.add(FDS_NAME, 'pantalon');
			recentSearches.add(OTHER_FDS_NAME, 'camiseta');

			recentSearches.clear(FDS_NAME);

			expect(recentSearches.get(FDS_NAME)).toEqual([]);
			expect(recentSearches.get(OTHER_FDS_NAME)).toEqual(['camiseta']);
		});
	});

	describe('get', () => {
		it('scopes the stored value by the Data Set name', () => {
			recentSearches.add(FDS_NAME, 'pantalon');

			expect(
				localStorage.getItem(STORAGE_KEY, localStorage.TYPES.FUNCTIONAL)
			).toBe(JSON.stringify(['pantalon']));
		});

		it('returns queries stored before a page reload', () => {
			setStoredValue(JSON.stringify(['pantalon', 'camiseta']));

			expect(recentSearches.get(FDS_NAME)).toEqual([
				'pantalon',
				'camiseta',
			]);
		});

		it('returns an empty array for a Data Set without queries', () => {
			expect(recentSearches.get(OTHER_FDS_NAME)).toEqual([]);
		});

		it('returns an empty array when the stored value is not valid JSON', () => {
			setStoredValue('pantalon');

			expect(recentSearches.get(FDS_NAME)).toEqual([]);
		});

		it('returns an empty array when the stored value is not an array', () => {
			setStoredValue(JSON.stringify({query: 'pantalon'}));

			expect(recentSearches.get(FDS_NAME)).toEqual([]);
		});

		it('returns an empty array when the stored value holds anything but strings', () => {
			setStoredValue(JSON.stringify(['pantalon', 7]));

			expect(recentSearches.get(FDS_NAME)).toEqual([]);
		});
	});

	describe('remove', () => {
		it('removes a single query and keeps the rest', () => {
			recentSearches.add(FDS_NAME, 'blogs');
			recentSearches.add(FDS_NAME, 'documents');

			recentSearches.remove(FDS_NAME, 'blogs');

			expect(recentSearches.get(FDS_NAME)).toEqual(['documents']);
		});

		it('removes a query case insensitively', () => {
			recentSearches.add(FDS_NAME, 'pantalon');

			recentSearches.remove(FDS_NAME, 'Pantalon');

			expect(recentSearches.get(FDS_NAME)).toEqual([]);
		});

		it('keeps the stored queries when the query is not stored', () => {
			recentSearches.add(FDS_NAME, 'pantalon');

			recentSearches.remove(FDS_NAME, 'camiseta');

			expect(recentSearches.get(FDS_NAME)).toEqual(['pantalon']);
		});
	});
});
