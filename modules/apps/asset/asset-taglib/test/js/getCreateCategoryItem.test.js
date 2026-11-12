/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {fetch} from 'frontend-js-web';

import getCreateCategoryItem from '../../src/main/resources/META-INF/resources/js/asset_categories_selector/getCreateCategoryItem';

jest.mock('frontend-js-web', () => ({
	...jest.requireActual('frontend-js-web'),
	fetch: jest.fn(),
}));

describe('getCreateCategoryItem', () => {
	afterEach(() => {
		jest.clearAllMocks();
	});

	it('returns nothing without the add category permission', () => {
		expect(
			getCreateCategoryItem({
				showAddCategoryButton: false,
				vocabularyIds: [1001],
			})
		).toBeUndefined();
	});

	it('returns nothing unless there is exactly one vocabulary', () => {
		expect(
			getCreateCategoryItem({
				showAddCategoryButton: true,
				vocabularyIds: [],
			})
		).toBeUndefined();
		expect(
			getCreateCategoryItem({
				showAddCategoryButton: true,
				vocabularyIds: [1001, 1002],
			})
		).toBeUndefined();
	});

	it('posts the category to the vocabulary', async () => {
		fetch.mockResolvedValue({
			json: () =>
				Promise.resolve({
					id: 5,
					name: 'Gamma',
					parentTaxonomyVocabulary: {id: 1001},
				}),
			ok: true,
		});

		const createItem = getCreateCategoryItem({
			showAddCategoryButton: true,
			vocabularyIds: [1001],
		});

		expect(createItem.label).toBe('add-category');

		await createItem.onCreate('Gamma');

		expect(fetch).toHaveBeenCalledWith(
			'/o/headless-admin-taxonomy/v1.0/taxonomy-vocabularies/1001/taxonomy-categories',
			expect.objectContaining({
				body: JSON.stringify({name: 'Gamma'}),
				method: 'POST',
			})
		);
	});

	it('rejects with the response title when the post fails', async () => {
		fetch.mockResolvedValue({
			json: () => Promise.resolve({title: 'Duplicate name'}),
			ok: false,
		});

		const createItem = getCreateCategoryItem({
			showAddCategoryButton: true,
			vocabularyIds: [1001],
		});

		await expect(createItem.onCreate('Gamma')).rejects.toThrow(
			'Duplicate name'
		);
	});
});
