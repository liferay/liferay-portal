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

async function expectSubscribeActionVisible(
	trigger: Locator,
	visible: boolean
) {
	const page = trigger.page();

	const dropdownMenu = page.locator('.dropdown-menu:visible');

	// Open the entry's actions menu

	await clickAndExpectToBeVisible({
		target: dropdownMenu,
		trigger,
	});

	const subscribeToggle = dropdownMenu.getByText(/^(Subscribe|Unsubscribe)$/);

	if (visible) {
		await expect(subscribeToggle.first()).toBeVisible();
	}
	else {
		await expect(subscribeToggle).toHaveCount(0);
	}

	await page.keyboard.press('Escape');
}

test('Subscription actions are hidden when email notifications are disabled', async ({
	apiHelpers,
	messageBoardsPage,
	messageBoardsWidgetPage,
	page,
	site,
}) => {
	const categoryName = getRandomString();
	const threadSubject = getRandomString();

	await apiHelpers.headlessDelivery.postSiteMessageBoardSection({
		siteId: site.id,
		title: categoryName,
	});

	await apiHelpers.headlessDelivery.postMessageBoardThread({
		articleBody: getRandomString(),
		headline: threadSubject,
		siteId: site.id,
	});

	const layout = await messageBoardsWidgetPage.addMessageBoardsPortlet(site);

	// With notifications enabled, the category and thread both offer a
	// subscription action

	const messageBoards = page.locator(
		'.portlet-boundary_com_liferay_message_boards_web_portlet_MBPortlet_'
	);

	const categoryActions = messageBoards
		.locator('a.component-action.dropdown-toggle')
		.last();
	const threadActions = messageBoards
		.locator('a.component-action.dropdown-toggle')
		.first();

	await page.goto(`/web${site.friendlyUrlPath}${layout.friendlyURL}`);

	await page.getByRole('link', {name: categoryName}).click();

	await expectSubscribeActionVisible(categoryActions, true);

	await messageBoardsWidgetPage.goToThread(site, layout, threadSubject);

	await expectSubscribeActionVisible(threadActions, true);

	// Disabling both message notification emails removes the subscription
	// actions from the category and thread

	await messageBoardsPage.disableEmailNotifications(site.friendlyUrlPath);

	await page.goto(`/web${site.friendlyUrlPath}${layout.friendlyURL}`);

	await page.getByRole('link', {name: categoryName}).click();

	await expectSubscribeActionVisible(categoryActions, false);

	await messageBoardsWidgetPage.goToThread(site, layout, threadSubject);

	await expectSubscribeActionVisible(threadActions, false);
});
