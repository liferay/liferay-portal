/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

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
