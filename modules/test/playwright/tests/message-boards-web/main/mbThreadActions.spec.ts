/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {apiHelpersTest} from '../../../fixtures/apiHelpersTest';
import {isolatedSiteTest} from '../../../fixtures/isolatedSiteTest';
import {loginTest} from '../../../fixtures/loginTest';
import {messageBoardsPagesTest} from '../../../fixtures/messageBoardsTest';
import {clickAndExpectToBeVisible} from '../../../utils/clickAndExpectToBeVisible';
import getRandomString from '../../../utils/getRandomString';
import {waitForAlert} from '../../../utils/waitForAlert';

const test = mergeTests(
	apiHelpersTest,
	isolatedSiteTest,
	loginTest(),
	messageBoardsPagesTest
);

function threadActionItem(page, name) {
	return page.locator('.dropdown-menu').getByText(name, {exact: true});
}

async function openThreadAction({headline, name, page}) {
	await page.getByRole('link', {name: headline}).click();

	await clickAndExpectToBeVisible({
		autoClick: true,
		target: threadActionItem(page, name),
		trigger: page.locator('.panel-heading .dropdown-toggle'),
	});
}

async function openThreadRowAction({name, page}) {
	const menuItem = page
		.locator('.dropdown-menu:visible')
		.getByText(name, {exact: true});

	await expect(async () => {
		await page
			.locator('a.component-action.dropdown-toggle')
			.first()
			.click();

		await expect(menuItem).toBeVisible({timeout: 3000});
	}).toPass();

	await menuItem.click();

	await page.waitForLoadState('networkidle');
}

test('Can edit a thread subject and body', async ({
	apiHelpers,
	messageBoardsEditThreadPage,
	messageBoardsPage,
	page,
	site,
}) => {
	const headline = getRandomString();

	await apiHelpers.headlessDelivery.postMessageBoardThread({
		articleBody: getRandomString(),
		headline,
		siteId: site.id,
	});

	await messageBoardsPage.goto(site.friendlyUrlPath);

	await openThreadAction({headline, name: 'Edit', page});

	const editedSubject = getRandomString();
	const editedBody = getRandomString();

	await messageBoardsEditThreadPage.subjectSelector.fill(editedSubject);
	await messageBoardsEditThreadPage.bodyTextBox.fill(editedBody);
	await messageBoardsEditThreadPage.publishButton.click();

	await expect(page.getByTestId('headerTitle')).toHaveText(editedSubject);
	await expect(page.getByText(editedBody)).toBeVisible();
});

test('Can lock and unlock a thread', async ({
	apiHelpers,
	messageBoardsPage,
	page,
	site,
}) => {
	const headline = getRandomString();
	const replyBody = getRandomString();

	const thread = await apiHelpers.headlessDelivery.postMessageBoardThread({
		articleBody: getRandomString(),
		headline,
		siteId: site.id,
	});

	await messageBoardsPage.goto(site.friendlyUrlPath);

	// Locking the thread surfaces a locked status

	await openThreadRowAction({name: 'Lock', page});

	await expect(page.getByText('Locked')).toBeVisible();

	// Unlocking restores replying

	await openThreadRowAction({name: 'Unlock', page});

	// The unlock is applied asynchronously, so retry the reply until it lands

	await expect(async () => {
		await apiHelpers.headlessDelivery.postMessageBoardMessage({
			articleBody: replyBody,
			messageBoardThreadId: String(thread.id),
		});
	}).toPass({timeout: 10000});

	await messageBoardsPage.goto(site.friendlyUrlPath);

	await page.getByRole('link', {name: headline}).click();

	await expect(page.getByText(replyBody)).toBeVisible();
});

test('Can view thread statistics post count', async ({
	apiHelpers,
	messageBoardsPage,
	page,
	site,
}) => {
	await apiHelpers.headlessDelivery.postMessageBoardThread({
		articleBody: getRandomString(),
		headline: getRandomString(),
		siteId: site.id,
	});

	await messageBoardsPage.goto(site.friendlyUrlPath);

	await page.getByRole('link', {name: 'Statistics'}).click();

	await page.waitForLoadState('networkidle');

	const postsCount = page
		.locator('.statistics-panel .overview-container', {hasText: 'Posts'})
		.locator('p')
		.first();

	await expect(postsCount).toHaveText('1');

	// A second thread bumps the post count

	await apiHelpers.headlessDelivery.postMessageBoardThread({
		articleBody: getRandomString(),
		headline: getRandomString(),
		siteId: site.id,
	});

	await messageBoardsPage.goto(site.friendlyUrlPath);

	await page.getByRole('link', {name: 'Statistics'}).click();

	await page.waitForLoadState('networkidle');

	await expect(postsCount).toHaveText('2');
});

test('Can cancel editing a thread', async ({
	apiHelpers,
	messageBoardsEditThreadPage,
	messageBoardsPage,
	page,
	site,
}) => {
	const headline = getRandomString();

	await apiHelpers.headlessDelivery.postMessageBoardThread({
		articleBody: getRandomString(),
		headline,
		siteId: site.id,
	});

	await messageBoardsPage.goto(site.friendlyUrlPath);

	await openThreadAction({headline, name: 'Edit', page});

	await messageBoardsEditThreadPage.subjectSelector.fill(getRandomString());

	await page.getByRole('button', {name: 'Cancel'}).click();

	// The original subject is preserved

	await expect(page.getByTestId('headerTitle')).toHaveText(headline);
});

test('Can move a thread from one category to another', async ({
	apiHelpers,
	messageBoardsPage,
	page,
	site,
}) => {
	const sourceCategory =
		await apiHelpers.headlessDelivery.postSiteMessageBoardSection({
			siteId: site.id,
			title: getRandomString(),
		});

	const targetCategory =
		await apiHelpers.headlessDelivery.postSiteMessageBoardSection({
			siteId: site.id,
			title: getRandomString(),
		});

	const headline = getRandomString();

	await apiHelpers.headlessDelivery.postMessageBoardSectionMessageBoardThread(
		{
			articleBody: getRandomString(),
			headline,
			messageBoardSectionId: String(sourceCategory.id),
		}
	);

	await messageBoardsPage.goto(site.friendlyUrlPath);

	// Open the source category and move its thread to the target category

	await page.getByRole('link', {name: sourceCategory.title}).click();

	await page.waitForLoadState('networkidle');

	await openThreadRowAction({name: 'Move', page});

	await page.getByRole('button', {exact: true, name: 'Select'}).click();

	await page
		.frameLocator('iframe[title="Select Category"]')
		.getByRole('row', {name: targetCategory.title})
		.getByRole('button', {name: 'Select'})
		.click();

	await page.getByRole('button', {exact: true, name: 'Move'}).click();

	await waitForAlert(page);

	// The thread now lives under the target category

	await messageBoardsPage.goto(site.friendlyUrlPath);

	await page.getByRole('link', {name: targetCategory.title}).click();

	await expect(page.getByRole('link', {name: headline})).toBeVisible();
});
