/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Locator, expect, mergeTests} from '@playwright/test';

import {apiHelpersTest} from '../../../fixtures/apiHelpersTest';
import {isolatedSiteTest} from '../../../fixtures/isolatedSiteTest';
import {loginTest} from '../../../fixtures/loginTest';
import {messageBoardsPagesTest} from '../../../fixtures/messageBoardsTest';
import {clickAndExpectToBeVisible} from '../../../utils/clickAndExpectToBeVisible';
import getRandomString from '../../../utils/getRandomString';

const test = mergeTests(
	apiHelpersTest,
	isolatedSiteTest,
	loginTest(),
	messageBoardsPagesTest
);

async function selectPagerDelta(pager: Locator, deltaLabel: string) {
	await clickAndExpectToBeVisible({
		autoClick: true,
		target: pager.getByRole('option', {exact: true, name: deltaLabel}),
		trigger: pager.locator('.pagination-items-per-page button'),
	});
}

test('Can view the remaining thread replies via More Messages', async ({
	apiHelpers,
	messageBoardsPage,
	page,
	site,
}) => {
	const headline = getRandomString();
	const replyBodies = Array.from({length: 7}, () => getRandomString());

	const thread = await apiHelpers.headlessDelivery.postMessageBoardThread({
		articleBody: getRandomString(),
		headline,
		siteId: site.id,
	});

	for (const articleBody of replyBodies) {
		await apiHelpers.headlessDelivery.postMessageBoardMessage({
			articleBody,
			messageBoardThreadId: String(thread.id),
		});
	}

	await messageBoardsPage.goToThread(headline, site.friendlyUrlPath);

	// With a delta of 5, the root message plus the first four replies show and
	// the rest sit behind the More Messages pager

	for (const replyBody of replyBodies.slice(0, 4)) {
		await expect(page.getByText(replyBody)).toBeVisible();
	}

	for (const replyBody of replyBodies.slice(4)) {
		await expect(page.getByText(replyBody)).toBeHidden();
	}

	// More Messages reveals the remaining replies

	await page.getByRole('link', {name: 'More Messages'}).click();

	for (const replyBody of replyBodies) {
		await expect(page.getByText(replyBody)).toBeVisible();
	}
});

test('Can page categories and threads with independent pagers', async ({
	apiHelpers,
	messageBoardsWidgetPage,
	page,
	site,
}) => {
	for (let count = 0; count < 6; count++) {
		await apiHelpers.headlessDelivery.postSiteMessageBoardSection({
			siteId: site.id,
			title: getRandomString(),
		});
	}

	for (let count = 0; count < 6; count++) {
		await apiHelpers.headlessDelivery.postMessageBoardThread({
			articleBody: getRandomString(),
			headline: getRandomString(),
			siteId: site.id,
		});
	}

	const layout = await messageBoardsWidgetPage.addMessageBoardsPortlet(site);

	await page.goto(`/web${site.friendlyUrlPath}${layout.friendlyURL}`);

	// The categories and threads groups are listed with their own pager

	const categoriesPager = page.locator('.pagination-bar').first();
	const threadsPager = page.locator('.pagination-bar').last();

	// The page delta is a persisted preference, so set each pager to four
	// entries explicitly rather than relying on the default

	await selectPagerDelta(categoriesPager, '4 Entries per Page');
	await selectPagerDelta(threadsPager, '4 Entries per Page');

	await expect(categoriesPager.locator('.pagination-results')).toHaveText(
		'Showing 1 to 4 of 6 entries.'
	);
	await expect(threadsPager.locator('.pagination-results')).toHaveText(
		'Showing 1 to 4 of 6 entries.'
	);

	// Raising the categories page delta pages that group only, leaving the
	// threads pager untouched

	await selectPagerDelta(categoriesPager, '8 Entries per Page');

	await expect(categoriesPager.locator('.pagination-results')).toHaveText(
		'Showing 1 to 6 of 6 entries.'
	);
	await expect(threadsPager.locator('.pagination-results')).toHaveText(
		'Showing 1 to 4 of 6 entries.'
	);
});
