/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {dataApiHelpersTest} from '../../../fixtures/dataApiHelpersTest';
import {isolatedSiteTest} from '../../../fixtures/isolatedSiteTest';
import {loginTest} from '../../../fixtures/loginTest';
import addGlobalCategories from '../../../utils/addGlobalCategories';
import getRandomString from '../../../utils/getRandomString';
import {assetCategoriesPagesTest} from './fixtures/assetCategoriesAdminPagesTest';

const test = mergeTests(
	assetCategoriesPagesTest,
	dataApiHelpersTest,
	isolatedSiteTest,
	loginTest()
);

test(
	'The global Audience and Stage vocabularies are internal, allow multiple categories and are not required',
	{tag: '@LPD-112477'},
	async ({assetCategoriesAdminPage, page, vocabulariesEditPage}) => {
		for (const vocabularyName of ['Audience', 'Stage']) {
			await test.step(vocabularyName, async () => {
				await assetCategoriesAdminPage.goto('/global');

				await expect(
					page
						.getByRole('menuitem', {name: vocabularyName})
						.locator('.lexicon-icon-low-vision')
				).toBeVisible();

				await vocabulariesEditPage.goto(vocabularyName);

				await expect(
					page.getByLabel('Allow Multiple Categories')
				).toBeChecked();
				await expect(
					page.getByLabel('Internal', {exact: true})
				).toBeChecked();

				await vocabulariesEditPage.expandPanel(
					'Associated Asset Types'
				);

				await expect(
					vocabulariesEditPage.requiredToggle
				).not.toHaveCount(0);

				for (const requiredToggle of await vocabulariesEditPage.requiredToggle.all()) {
					await expect(requiredToggle).not.toBeChecked();
				}
			});
		}
	}
);

test(
	'A site views the changes of a global category but cannot edit its vocabulary',
	{tag: '@LPD-112477'},
	async ({apiHelpers, assetCategoriesAdminPage, page, site}) => {
		const [category] = await addGlobalCategories({
			apiHelpers,
			names: [getRandomString()],
			vocabularyName: 'Topic',
		});

		await test.step('View the global category in the site', async () => {
			await assetCategoriesAdminPage.goto(site.friendlyUrlPath);

			await assetCategoriesAdminPage.gotoVocabulary('Topic');

			await expect(
				page.getByRole('link', {exact: true, name: category.name})
			).toBeVisible();

			await expect(
				page
					.getByRole('menuitem', {name: 'Topic'})
					.locator('.lexicon-icon-lock')
			).toBeVisible();

			await expect(
				page.getByRole('link', {name: 'Add Category'})
			).toHaveCount(0);
		});

		await test.step('View the renamed global category in the site', async () => {
			const newName = getRandomString();

			await apiHelpers.headlessAdminTaxonomy.patchTaxonomyCategory({
				id: category.id,
				name: newName,
			});

			await page.reload();

			await expect(
				page.getByRole('link', {exact: true, name: newName})
			).toBeVisible();
			await expect(
				page.getByRole('link', {exact: true, name: category.name})
			).toHaveCount(0);
		});
	}
);
