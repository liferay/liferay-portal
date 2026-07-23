/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {collectionsPagesTest} from '../../../../fixtures/collectionsPagesTest';
import {dataApiHelpersTest} from '../../../../fixtures/dataApiHelpersTest';
import {isolatedSiteTest} from '../../../../fixtures/isolatedSiteTest';
import {loginTest} from '../../../../fixtures/loginTest';
import {clickAndExpectToBeVisible} from '../../../../utils/clickAndExpectToBeVisible';
import getRandomString from '../../../../utils/getRandomString';
import {PORTLET_URLS} from '../../../../utils/portletUrls';
import {waitForAlert} from '../../../../utils/waitForAlert';
import {cmsPagesTest} from '../fixtures/cmsPagesTest';

const test = mergeTests(
	cmsPagesTest,
	collectionsPagesTest,
	dataApiHelpersTest,
	isolatedSiteTest,
	loginTest()
);

test(
	'Categories and tags from the CMS are available in the Collection filter selectors when a Space is in the scope',
	{tag: '@LPD-86263'},
	async ({apiHelpers, collectionsPage, page, site}) => {
		const categoryName = getRandomString();
		const collectionName = getRandomString();

		const siteId = await apiHelpers.headlessAdminUser
			.getSiteByFriendlyUrlPath('cms')
			.then((response) => response.id);

		const spaceName = getRandomString();
		const tagName = getRandomString();
		const vocabularyName = getRandomString();

		await test.step('Create a new space', async () => {
			await apiHelpers.headlessAssetLibrary.createAssetLibrary({
				name: spaceName,
				settings: {},
				type: 'Space',
			});
		});

		await test.step('Create a CMS vocabulary and category', async () => {
			const vocabulary =
				await apiHelpers.headlessAdminTaxonomy.postSiteTaxonomyVocabulary(
					{
						assetLibraries: [{id: -1}],
						assetTypes: [
							{
								required: true,
								subtype: 'AllAssetSubtypes',
								type: 'AllAssetTypes',
							},
						],
						name: vocabularyName,
						siteId,
						visibilityType: 'PUBLIC',
					}
				);

			await apiHelpers.headlessAdminTaxonomy.postTaxonomyVocabularyTaxonomyCategory(
				{
					name: categoryName,
					vocabularyId: vocabulary.id,
				}
			);
		});

		await test.step('Create a CMS tag', async () => {
			await apiHelpers.headlessAdminTaxonomy.postSiteKeyword({
				name: tagName,
				siteId,
			});
		});

		await test.step('Connect space to the site', async () => {
			await page.goto(PORTLET_URLS.cmsAllSpaces);

			await expect(
				page.getByRole('row', {name: spaceName})
			).toBeVisible();

			await page
				.getByRole('row', {name: spaceName})
				.getByRole('button', {name: 'Actions'})
				.click();
			await page
				.getByRole('menuitem', {name: 'View Connected Sites'})
				.click();
			await page.getByPlaceholder('Select a Site').fill(site.name);
			await page.getByRole('option', {name: site.name}).click();

			await expect(
				page.getByRole('button', {name: 'Connect'})
			).toBeEnabled();

			await page.getByRole('button', {name: 'Connect'}).click();

			await waitForAlert(
				page,
				`Success:Site ${site.name} was successfully connected to the space.`
			);
		});

		await test.step('Create a dynamic collection on the site', async () => {
			await collectionsPage.goto(site.friendlyUrlPath);
			await collectionsPage.addNewDynamicCollection(collectionName);

			await page
				.getByLabel('Item Type')
				.selectOption({label: 'Basic Document (CMS)'});
		});

		await test.step('Include the Space in the collection scope', async () => {
			await page.getByRole('button', {name: 'Scope'}).click();

			await page.getByRole('button', {name: 'Select Site'}).click();

			await page
				.getByRole('menuitem', {name: 'Other Site, Asset Library, or'})
				.click();

			await page
				.locator('iframe[title="Scope"]')
				.contentFrame()
				.getByRole('link', {name: 'Spaces'})
				.click();

			await page
				.locator('iframe[title="Scope"]')
				.contentFrame()
				.getByRole('link', {exact: true, name: spaceName})
				.click();
		});

		await test.step('CMS tag is available in the tag filter selector', async () => {
			await clickAndExpectToBeVisible({
				autoClick: true,
				target: page.getByRole('button', {name: 'Select Tags'}),
				trigger: page.getByRole('button', {name: 'Filter'}),
			});

			const tagFrame = page.frameLocator('iframe[title="Tags"]');

			await expect(tagFrame.getByText(tagName)).toBeVisible();

			await page.keyboard.press('Escape');
		});

		await test.step('CMS category is available in the category filter selector', async () => {
			const filterFieldSelect = page.getByLabel('of the following');

			await expect(async () => {
				if (!(await filterFieldSelect.isVisible())) {
					await page.getByRole('button', {name: 'Filter'}).click();
				}

				await expect(filterFieldSelect).toBeVisible({timeout: 2000});
			}).toPass({timeout: 2000});

			await filterFieldSelect.selectOption('assetCategories');

			await page
				.getByRole('button', {exact: true, name: 'Select'})
				.click();

			await expect(
				page.getByText(categoryName, {exact: true})
			).toBeVisible();
		});
	}
);
