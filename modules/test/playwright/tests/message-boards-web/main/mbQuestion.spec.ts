/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {apiHelpersTest} from '../../../fixtures/apiHelpersTest';
import {featureFlagsTest} from '../../../fixtures/featureFlagsTest';
import {isolatedSiteTest} from '../../../fixtures/isolatedSiteTest';
import {loginTest} from '../../../fixtures/loginTest';
import {messageBoardsPagesTest} from '../../../fixtures/messageBoardsTest';
import getRandomString from '../../../utils/getRandomString';

const test = mergeTests(
	apiHelpersTest,
	featureFlagsTest({
		'LPD-105225': {enabled: true},
	}),
	isolatedSiteTest,
	loginTest(),
	messageBoardsPagesTest
);

test('Can add a thread as a question in a category', async ({
	apiHelpers,
	messageBoardsEditThreadPage,
	messageBoardsPage,
	page,
	site,
}) => {
	const categoryName = getRandomString();
	const headline = getRandomString();

	// Seed a category through the API

	await apiHelpers.headlessDelivery.postSiteMessageBoardSection({
		siteId: site.id,
		title: categoryName,
	});

	// Create a thread in the category and mark it as a question

	await messageBoardsPage.goto(site.friendlyUrlPath);

	await page.getByRole('link', {name: categoryName}).click();

	await messageBoardsPage.goToCreateNewThread();

	await messageBoardsEditThreadPage.subjectSelector.fill(headline);
	await messageBoardsEditThreadPage.bodyTextBox.fill(getRandomString());

	// The "Mark as a Question" input is a zero-size native checkbox, so toggle
	// it with a DOM click rather than a pointer action

	await page
		.locator('input[name$="_question"]')
		.evaluate((element: HTMLInputElement) => element.click());

	await messageBoardsEditThreadPage.publishButton.click();

	// A reply to a question thread can be marked as an answer

	await messageBoardsEditThreadPage.publishReply(getRandomString());

	const messageActions = page.locator('.panel-heading .dropdown-toggle');

	await expect(messageActions).toHaveCount(2);

	const markAsAnswer = page.getByText('Mark as an Answer', {exact: true});

	await expect(async () => {
		await messageActions.last().click();

		await expect(markAsAnswer).toBeVisible({timeout: 3000});
	}).toPass();

	await markAsAnswer.click();

	await expect(page.getByText('(Answer)')).toBeVisible();
});

test('Can configure the widget to mark threads as questions by default', async ({
	messageBoardsEditThreadPage,
	messageBoardsPage,
	page,
	site,
}) => {

	// Enable "Thread as Question by Default" in the configuration

	await messageBoardsPage.goto(site.friendlyUrlPath);

	const configurationMenuItem = page.getByRole('menuitem', {
		name: 'Configuration',
	});

	await expect(async () => {
		await page.getByLabel('Options').click();

		await expect(configurationMenuItem).toBeVisible({timeout: 3000});
	}).toPass();

	await configurationMenuItem.click();

	await page
		.locator('input[name*="threadAsQuestionByDefault"]')
		.evaluate((element: HTMLInputElement) => element.click());

	await page.getByRole('button', {name: 'Save'}).click();

	// A new thread now has "Mark as a Question" checked by default

	await messageBoardsEditThreadPage.goto(site.friendlyUrlPath);

	await messageBoardsEditThreadPage.subjectSelector.waitFor();

	await expect(page.locator('input[name$="_question"]')).toBeChecked();
});
