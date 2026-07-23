/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {dataApiHelpersTest} from '../../../fixtures/dataApiHelpersTest';
import {isolatedSiteTest} from '../../../fixtures/isolatedSiteTest';
import {loginTest} from '../../../fixtures/loginTest';
import {searchExperiencesPagesTest} from '../../../fixtures/searchExperiencesPageTest';
import {searchPageTest} from '../../../fixtures/searchPageTest';
import {getRandomInt} from '../../../utils/getRandomInt';
import getRandomString from '../../../utils/getRandomString';
import {hoverAndExpectToBeVisible} from '../../../utils/hoverAndExpectToBeVisible';
import getBasicWebContentStructureId from '../../../utils/structured-content/getBasicWebContentStructureId';

export const test = mergeTests(
	dataApiHelpersTest,
	isolatedSiteTest,
	loginTest(),
	searchExperiencesPagesTest,
	searchPageTest
);

test.describe('Reordering', () => {
	test('Add result and reorder rankings', async ({
		apiHelpers,
		editResultRankingPage,
		page,
		resultRankingsViewPage,
		searchPage,
		site,
	}) => {
		const searchQuery = 'Article';
		const webContentTitle1 = `Site Journal ${getRandomInt()}`;
		const webContentTitle2 = `Site Article ${getRandomInt()}`;
		const webContentTitle3 = `Site Article ${getRandomInt()}`;

		await test.step('Create web content for the new site', async () => {
			const basicWebContentStructureId =
				await getBasicWebContentStructureId(apiHelpers);

			await apiHelpers.jsonWebServicesJournal.addWebContent({
				ddmStructureId: basicWebContentStructureId,
				groupId: site.id,
				titleMap: {en_US: webContentTitle1},
			});

			await apiHelpers.jsonWebServicesJournal.addWebContent({
				ddmStructureId: basicWebContentStructureId,
				groupId: site.id,
				titleMap: {en_US: webContentTitle2},
			});

			await apiHelpers.jsonWebServicesJournal.addWebContent({
				ddmStructureId: basicWebContentStructureId,
				groupId: site.id,
				titleMap: {en_US: webContentTitle3},
			});
		});

		await test.step('Create result ranking for search query', async () => {
			await resultRankingsViewPage.goto();

			await resultRankingsViewPage.createResultRanking(searchQuery);
		});

		await test.step('Use the Add Results modal to add a web content', async () => {
			await editResultRankingPage.addResults({
				searchQuery: 'Site',
				titles: [webContentTitle1],
			});
		});

		const resultRankingItem1 =
			await editResultRankingPage.results.item(webContentTitle1);
		const resultRankingItem2 =
			await editResultRankingPage.results.item(webContentTitle2);
		const resultRankingItem3 =
			await editResultRankingPage.results.item(webContentTitle3);

		await test.step('View that the added result is pinned', async () => {
			await hoverAndExpectToBeVisible({
				autoClick: false,
				target: resultRankingItem1.getByLabel('Unpin Result'),
				trigger: resultRankingItem1,
			});
		});

		await test.step('Pin the recently created web content articles', async () => {
			await hoverAndExpectToBeVisible({
				autoClick: true,
				target: resultRankingItem2.getByLabel('Pin Result'),
				trigger: resultRankingItem2,
			});

			await hoverAndExpectToBeVisible({
				autoClick: true,
				target: resultRankingItem3.getByLabel('Pin Result'),
				trigger: resultRankingItem3,
			});
		});

		await test.step('Reorder first result ranking to third position', async () => {
			await editResultRankingPage.dragAndDropResultsItem({
				dragTarget: resultRankingItem1,
				dropTarget: resultRankingItem3,
			});

			await expect(
				page
					.locator('.result-rankings-container .list-group-item')
					.nth(2)
					.getByRole('link', {name: webContentTitle1})
			).toBeVisible();
		});

		await test.step('Save the result ranking', async () => {
			await editResultRankingPage.saveButton.click();
		});

		await test.step('Check the ranking results are being applied on a page', async () => {
			await test.step('Create a new page and navigate to it', async () => {
				const layout = await apiHelpers.jsonWebServicesLayout.addLayout(
					{
						groupId: site.id,
						title: `Page${getRandomInt()}`,
					}
				);

				await page.goto(
					'/web' + site.friendlyUrlPath + layout.friendlyURL
				);
			});

			await test.step('Add search bar and search results widgets to the page', async () => {
				await searchPage.addPortlet('Search Bar', 'Search');

				await searchPage.addPortlet('Search Results', 'Search');
			});

			await test.step('Configure the search bar scope to "Everything"', async () => {
				await searchPage.openSearchPortletConfiguration(
					'Search Bar',
					1
				);

				await searchPage.selectPortletConfigurationsSelect([
					{
						label: 'Scope',
						value: 'Everything',
					},
				]);

				await searchPage.savePortletConfiguration();
			});

			await test.step('Search for the query and check the rankings order', async () => {
				await searchPage.searchKeywordInMainContent(searchQuery);

				await expect(
					searchPage.searchResultsItems
						.nth(0)
						.getByRole('link', {name: webContentTitle2})
				).toBeVisible();

				await expect(
					searchPage.searchResultsItems
						.nth(1)
						.getByRole('link', {name: webContentTitle3})
				).toBeVisible();

				await expect(
					searchPage.searchResultsItems
						.nth(2)
						.getByRole('link', {name: webContentTitle1})
				).toBeVisible();
			});
		});

		await test.step('Delete the created result ranking', async () => {
			await resultRankingsViewPage.goto();

			await resultRankingsViewPage.deleteResultRanking(searchQuery);
		});
	});
});

test.describe('CMS Object Entries', () => {
	test('View object entries details created in CMS space @LPD-76602', async ({
		apiHelpers,
		editResultRankingPage,
		resultRankingsViewPage,
	}) => {
		const cmsId = `CMS_${getRandomString()}`;

		const cmsObjectEntry1 = {
			content: `Description ${cmsId}`,
			title: `${cmsId} Web Content`,
		};
		const cmsObjectEntry2 = {
			fileName: `File_${cmsId}.png`,
			title: `${cmsId} File`,
		};
		const spaceName = `Space ${cmsId}`;

		await test.step('Create a new Space', async () => {
			await apiHelpers.headlessAssetLibrary.createAssetLibrary({
				name: spaceName,
				settings: {},
				type: 'Space',
			});
		});

		await test.step('Create a basic web content for that space', async () => {
			await apiHelpers.objectEntry.postObjectEntry(
				{
					content: cmsObjectEntry1.content,
					objectEntryFolderExternalReferenceCode: 'L_CONTENTS',
					title: cmsObjectEntry1.title,
				},
				'cms/basic-web-contents',
				spaceName
			);
		});

		await test.step('Create a file for that space', async () => {
			await apiHelpers.objectEntry.postObjectEntry(
				{
					file: {
						fileBase64: 'R0lGODlhAQABAAAAACw=',
						name: cmsObjectEntry2.fileName,
					},
					objectEntryFolderExternalReferenceCode: 'L_FILES',
					title: cmsObjectEntry2.title,
				},
				'cms/basic-documents',
				spaceName
			);
		});

		await test.step('Create result ranking for search query', async () => {
			await resultRankingsViewPage.goto();

			await resultRankingsViewPage.createResultRanking(cmsId);
		});

		const resultRankingItem1 = await editResultRankingPage.results.item(
			cmsObjectEntry1.title
		);
		const resultRankingItem2 = await editResultRankingPage.results.item(
			cmsObjectEntry2.title
		);

		await test.step('View the results title, content, and icon', async () => {
			await expect(resultRankingItem1).toBeVisible();
			await expect(resultRankingItem2).toBeVisible();

			await expect(
				resultRankingItem1.locator('.list-item-description')
			).toHaveText(new RegExp(cmsObjectEntry1.content));

			await expect(
				resultRankingItem2.locator('.list-item-description')
			).toHaveText(new RegExp(cmsObjectEntry2.fileName));

			await expect(
				resultRankingItem1.locator('.lexicon-icon-forms')
			).toBeVisible();

			await expect(
				resultRankingItem2.locator('.lexicon-icon-documents-and-media')
			).toBeVisible();
		});

		await test.step('Delete the created result ranking', async () => {
			await resultRankingsViewPage.goto();

			await resultRankingsViewPage.deleteResultRanking(cmsId);
		});
	});
});
