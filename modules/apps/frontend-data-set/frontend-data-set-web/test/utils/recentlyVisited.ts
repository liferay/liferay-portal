/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {localStorage} from 'frontend-js-web';

import recentlyVisited from '../../src/main/resources/META-INF/resources/utils/recentlyVisited';

const FDS_NAME = 'FDS_NAME';
const OTHER_FDS_NAME = 'OTHER_FDS_NAME';

const STORAGE_KEY = `LFR_RECENTLY_VISITED_${FDS_NAME}`;

const BLOGS = {href: '/blogs/1', label: 'Blogs'};
const DOCUMENTS = {href: '/documents/2', label: 'Documents'};

function setStoredValue(value: string) {
	localStorage.setItem(STORAGE_KEY, value, localStorage.TYPES.FUNCTIONAL);
}

describe('recentlyVisited', () => {
	afterEach(() => {
		recentlyVisited.clear(FDS_NAME);
		recentlyVisited.clear(OTHER_FDS_NAME);
	});

	describe('add', () => {
		it('stores an item', () => {
			recentlyVisited.add(FDS_NAME, BLOGS);

			expect(recentlyVisited.get(FDS_NAME)).toEqual([BLOGS]);
		});

		it('trims surrounding whitespace', () => {
			recentlyVisited.add(FDS_NAME, {
				href: '  /blogs/1  ',
				label: '  Blogs  ',
			});

			expect(recentlyVisited.get(FDS_NAME)).toEqual([BLOGS]);
		});

		it('ignores an item without a URL', () => {
			recentlyVisited.add(FDS_NAME, {href: '   ', label: 'Blogs'});

			expect(recentlyVisited.get(FDS_NAME)).toEqual([]);
		});

		it('ignores a label that is not text', () => {
			recentlyVisited.add(FDS_NAME, {
				href: '/blogs/1',
				label: {en_US: 'Blogs'} as any,
			});

			expect(recentlyVisited.get(FDS_NAME)).toEqual([]);
		});

		it('ignores an item without a label', () => {
			recentlyVisited.add(FDS_NAME, {href: '/blogs/1', label: '   '});

			expect(recentlyVisited.get(FDS_NAME)).toEqual([]);
		});

		it('returns unrelated items most recent first', () => {
			recentlyVisited.add(FDS_NAME, BLOGS);
			recentlyVisited.add(FDS_NAME, DOCUMENTS);

			expect(recentlyVisited.get(FDS_NAME)).toEqual([DOCUMENTS, BLOGS]);
		});

		it('moves a revisited item to the front without duplicating it', () => {
			recentlyVisited.add(FDS_NAME, BLOGS);
			recentlyVisited.add(FDS_NAME, DOCUMENTS);
			recentlyVisited.add(FDS_NAME, BLOGS);

			expect(recentlyVisited.get(FDS_NAME)).toEqual([BLOGS, DOCUMENTS]);
		});

		it('stores the label a revisited item carries now', () => {
			recentlyVisited.add(FDS_NAME, BLOGS);
			recentlyVisited.add(FDS_NAME, {...BLOGS, label: 'Renamed Blogs'});

			expect(recentlyVisited.get(FDS_NAME)).toEqual([
				{...BLOGS, label: 'Renamed Blogs'},
			]);
		});

		it('keeps items that share a label under different URLs', () => {
			recentlyVisited.add(FDS_NAME, BLOGS);
			recentlyVisited.add(FDS_NAME, {...BLOGS, href: '/blogs/2'});

			expect(recentlyVisited.get(FDS_NAME)).toEqual([
				{...BLOGS, href: '/blogs/2'},
				BLOGS,
			]);
		});

		it('evicts the least recently visited item beyond the given maximum', () => {
			recentlyVisited.add(FDS_NAME, BLOGS, {maxEntries: 2});
			recentlyVisited.add(FDS_NAME, DOCUMENTS, {maxEntries: 2});
			recentlyVisited.add(
				FDS_NAME,
				{href: '/pages/3', label: 'Pages'},
				{maxEntries: 2}
			);

			expect(recentlyVisited.get(FDS_NAME)).toEqual([
				{href: '/pages/3', label: 'Pages'},
				DOCUMENTS,
			]);
		});

		it('stores at most eight items by default', () => {
			const labels = Array.from({length: 9}, (value, index) =>
				String.fromCharCode(97 + index)
			);

			labels.forEach((label) =>
				recentlyVisited.add(FDS_NAME, {href: `/${label}`, label})
			);

			const recentlyVisitedItems = recentlyVisited.get(FDS_NAME);

			expect(recentlyVisitedItems).toHaveLength(8);
			expect(recentlyVisitedItems[0].label).toBe('i');
			expect(recentlyVisitedItems).not.toContainEqual({
				href: '/a',
				label: 'a',
			});
		});

		it('isolates items per Data Set', () => {
			recentlyVisited.add(FDS_NAME, BLOGS);
			recentlyVisited.add(OTHER_FDS_NAME, DOCUMENTS);

			expect(recentlyVisited.get(FDS_NAME)).toEqual([BLOGS]);
			expect(recentlyVisited.get(OTHER_FDS_NAME)).toEqual([DOCUMENTS]);
		});
	});

	describe('clear', () => {
		it('removes every item of a Data Set and leaves the other Data Sets alone', () => {
			recentlyVisited.add(FDS_NAME, BLOGS);
			recentlyVisited.add(OTHER_FDS_NAME, DOCUMENTS);

			recentlyVisited.clear(FDS_NAME);

			expect(recentlyVisited.get(FDS_NAME)).toEqual([]);
			expect(recentlyVisited.get(OTHER_FDS_NAME)).toEqual([DOCUMENTS]);
		});
	});

	describe('get', () => {
		it('scopes the stored value by the Data Set name', () => {
			recentlyVisited.add(FDS_NAME, BLOGS);

			expect(
				localStorage.getItem(STORAGE_KEY, localStorage.TYPES.FUNCTIONAL)
			).toBe(JSON.stringify([BLOGS]));
		});

		it('returns items stored before a page reload', () => {
			recentlyVisited.add(FDS_NAME, DOCUMENTS);
			recentlyVisited.add(FDS_NAME, BLOGS);

			// A reload leaves the module registry behind, so a freshly imported
			// API reads the items the way the next page load does

			let reloadedRecentlyVisited!: typeof recentlyVisited;

			jest.isolateModules(() => {
				reloadedRecentlyVisited =
					require('../../src/main/resources/META-INF/resources/utils/recentlyVisited').default;
			});

			expect(reloadedRecentlyVisited.get(FDS_NAME)).toEqual([
				BLOGS,
				DOCUMENTS,
			]);
		});

		it('returns an empty array for a Data Set without items', () => {
			expect(recentlyVisited.get(OTHER_FDS_NAME)).toEqual([]);
		});

		it('returns an empty array when the stored value is not valid JSON', () => {
			setStoredValue('Blogs');

			expect(recentlyVisited.get(FDS_NAME)).toEqual([]);
		});

		it('returns an empty array when the stored value is not an array', () => {
			setStoredValue(JSON.stringify(BLOGS));

			expect(recentlyVisited.get(FDS_NAME)).toEqual([]);
		});

		it('returns an empty array when the stored value holds anything but items', () => {
			setStoredValue(JSON.stringify([BLOGS, 7]));

			expect(recentlyVisited.get(FDS_NAME)).toEqual([]);
		});

		it('returns an empty array when a stored item has no label', () => {
			setStoredValue(JSON.stringify([{href: '/blogs/1'}]));

			expect(recentlyVisited.get(FDS_NAME)).toEqual([]);
		});
	});

	describe('remove', () => {
		it('removes a single item and keeps the rest', () => {
			recentlyVisited.add(FDS_NAME, BLOGS);
			recentlyVisited.add(FDS_NAME, DOCUMENTS);

			recentlyVisited.remove(FDS_NAME, BLOGS.href);

			expect(recentlyVisited.get(FDS_NAME)).toEqual([DOCUMENTS]);
		});

		it('keeps the stored items when the URL is not stored', () => {
			recentlyVisited.add(FDS_NAME, BLOGS);

			recentlyVisited.remove(FDS_NAME, DOCUMENTS.href);

			expect(recentlyVisited.get(FDS_NAME)).toEqual([BLOGS]);
		});
	});
});
