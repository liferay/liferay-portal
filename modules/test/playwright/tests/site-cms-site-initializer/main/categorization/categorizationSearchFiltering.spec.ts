/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {dataApiHelpersTest} from '../../../../fixtures/dataApiHelpersTest';
import {featureFlagsTest} from '../../../../fixtures/featureFlagsTest';
import {isolatedLayoutTest} from '../../../../fixtures/isolatedLayoutTest';
import {loginTest} from '../../../../fixtures/loginTest';
import {searchPageTest} from '../../../../fixtures/searchPageTest';
import getRandomString from '../../../../utils/getRandomString';

const test = mergeTests(
	dataApiHelpersTest,
	featureFlagsTest({
		'LPS-178052': {enabled: true},
	}),
	isolatedLayoutTest({type: 'portlet'}),
	loginTest(),
	searchPageTest
);

test(
	'Content tagged with a child category appears when filtering by the parent category in a search widget',
	{tag: ['@LPD-95543', '@LPD-95543/TC-20.d']},
	async ({apiHelpers, browser, layout, page, searchPage}) => {
		const space = await apiHelpers.headlessAssetLibrary.createAssetLibrary({
			name: `Space ${getRandomString()}`,
			type: 'Space',
		});

		const guestSite = await apiHelpers.headlessAdminSite.getSite('L_GUEST');

		await apiHelpers.headlessAssetLibrary.connectSite(
			space.externalReferenceCode,
			guestSite.externalReferenceCode,
			{searchable: true}
		);

		const siteId = await apiHelpers.headlessAdminUser
			.getSiteByFriendlyUrlPath('cms')
			.then((response) => response.id);

		const vocabularyName = getRandomString();

		const vocabularyId = await apiHelpers.headlessAdminTaxonomy
			.postSiteTaxonomyVocabulary({
				assetLibraries: [{id: -1}],
				assetTypes: [
					{
						required: false,
						subtype: 'AllAssetSubtypes',
						type: 'AllAssetTypes',
					},
				],
				name: vocabularyName,
				siteId,
				visibilityType: 'PUBLIC',
			})
			.then((response) => response.id);

		const parentCategoryName = getRandomString();

		const parentCategoryId = await apiHelpers.headlessAdminTaxonomy
			.postTaxonomyVocabularyTaxonomyCategory({
				name: parentCategoryName,
				vocabularyId,
			})
			.then((response) => response.id);

		const childCategoryName = getRandomString();

		const childCategoryId = await apiHelpers.headlessAdminTaxonomy
			.postTaxonomyCategoryTaxonomyCategory({
				name: childCategoryName,
				parentTaxonomyCategoryId: parentCategoryId,
			})
			.then((response) => response.id);

		const contentTitle = getRandomString();

		const objectEntry = await apiHelpers.objectEntry.postObjectEntry(
			{
				objectEntryFolderExternalReferenceCode: 'L_CONTENTS',
				taxonomyCategoryIds: [childCategoryId],
				title: contentTitle,
			},
			'cms/basic-web-contents',
			space.name
		);

		await apiHelpers.objectEntry.putObjectEntryPermissions(
			'cms/basic-web-contents',
			objectEntry.id,
			[
				{actionIds: ['VIEW'], roleName: 'Guest'},
				{actionIds: ['VIEW'], roleName: 'Site Member'},
			]
		);

		const viewPermissions = [
			{actionIds: ['VIEW'], roleName: 'Guest'},
			{actionIds: ['VIEW'], roleName: 'Site Member'},
		];

		await apiHelpers.put(
			`${apiHelpers.baseUrl}headless-admin-taxonomy/v1.0/taxonomy-vocabularies/${vocabularyId}/permissions`,
			{data: viewPermissions}
		);
		await apiHelpers.put(
			`${apiHelpers.baseUrl}headless-admin-taxonomy/v1.0/taxonomy-categories/${parentCategoryId}/permissions`,
			{data: viewPermissions}
		);
		await apiHelpers.put(
			`${apiHelpers.baseUrl}headless-admin-taxonomy/v1.0/taxonomy-categories/${childCategoryId}/permissions`,
			{data: viewPermissions}
		);

		await test.step('Add a search widget with a Category Facet to a site page', async () => {
			await page.goto('/web/guest' + layout.friendlyURL);

			await searchPage.addPortlet('Search Bar', 'Search');
			await searchPage.addPortlet('Category Facet', 'Search');
			await searchPage.addPortlet('Search Results', 'Search');
		});

		await test.step('Scope the Category Facet to all vocabularies', async () => {
			await searchPage.openSearchPortletConfiguration('Category Facet');

			await searchPage.modalIFrame
				.getByRole('radio', {name: 'All Vocabularies'})
				.check();

			await searchPage.savePortletConfiguration();
		});

		const layoutUrl = '/web/guest' + layout.friendlyURL;

		async function searchAndFilterByParentCategory(
			targetPage: typeof page
		) {
			await expect(async () => {
				await targetPage.goto(layoutUrl);

				const searchInput = targetPage
					.getByPlaceholder('Search...')
					.last();

				await searchInput.fill(contentTitle);
				await searchInput.press('Enter');

				await expect(
					targetPage
						.getByRole('link', {name: parentCategoryName})
						.last()
				).toBeVisible({timeout: 3000});
			}).toPass({timeout: 60000});

			await targetPage
				.getByRole('link', {name: parentCategoryName})
				.last()
				.click();

			await expect(
				targetPage.locator('.portlet-search-results').last()
			).toContainText(contentTitle);
		}

		await test.step('Search and filter by the parent category', async () => {
			await searchAndFilterByParentCategory(page);
		});

		await test.step('Verify the content appears for GUEST', async () => {
			const guestContext = await browser.newContext({
				storageState: {cookies: [], origins: []},
			});
			const guestPage = await guestContext.newPage();

			await searchAndFilterByParentCategory(guestPage);

			await guestContext.close();
		});
	}
);
