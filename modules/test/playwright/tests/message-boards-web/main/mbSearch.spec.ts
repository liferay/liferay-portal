/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Page, expect, mergeTests} from '@playwright/test';

import {apiHelpersTest} from '../../../fixtures/apiHelpersTest';
import {isolatedSiteTest} from '../../../fixtures/isolatedSiteTest';
import {loginTest} from '../../../fixtures/loginTest';
import {messageBoardsPagesTest} from '../../../fixtures/messageBoardsTest';
import getRandomString from '../../../utils/getRandomString';

const test = mergeTests(
	apiHelpersTest,
	isolatedSiteTest,
	loginTest(),
	messageBoardsPagesTest
);

async function expectThreadFoundBySearch(
	page: Page,
	site: Site,
	layout: Layout,
	searchTerm: string,
	threadSubject: string
) {

	// Indexing is asynchronous, so retry the search until the thread surfaces

	await expect(async () => {
		await page.goto(`/web${site.friendlyUrlPath}${layout.friendlyURL}`);

		await page.getByTestId('searchInput').fill(searchTerm);
		await page.getByTestId('searchButton').click();

		await expect(page.getByRole('link', {name: threadSubject})).toBeVisible(
			{timeout: 5000}
		);
	}).toPass({timeout: 60000});
}

test('Can search for a thread by its category', async ({
	apiHelpers,
	messageBoardsWidgetPage,
	page,
	site,
}) => {
	const categoryName = getRandomString();
	const threadSubject = getRandomString();

	const vocabulary =
		await apiHelpers.headlessAdminTaxonomy.postSiteTaxonomyVocabulary({
			name: getRandomString(),
			siteId: site.id,
		});

	const category =
		await apiHelpers.headlessAdminTaxonomy.postTaxonomyVocabularyTaxonomyCategory(
			{
				name: categoryName,
				vocabularyId: vocabulary.id,
			}
		);

	await apiHelpers.headlessDelivery.postMessageBoardThread({
		articleBody: getRandomString(),
		headline: threadSubject,
		siteId: site.id,
		taxonomyCategoryIds: [category.id],
	});

	const layout = await messageBoardsWidgetPage.addMessageBoardsPortlet(site);

	await expectThreadFoundBySearch(
		page,
		site,
		layout,
		categoryName,
		threadSubject
	);
});
