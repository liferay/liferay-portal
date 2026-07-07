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

test(
	'Threads are sorted from newest to oldest by default',
	{tag: '@LPS-68939'},
	async ({apiHelpers, messageBoardsPage, page, site}) => {

		// Seed three threads oldest-to-newest via API

		const prefix = getRandomString();

		for (const index of [1, 2, 3]) {
			await apiHelpers.headlessDelivery.postMessageBoardThread({
				articleBody: getRandomString(),
				headline: `${prefix} ${index}`,
				siteId: site.id,
			});
		}

		await messageBoardsPage.goto(site.friendlyUrlPath);

		// The newest thread is listed first

		const threadLinks = page.getByRole('link', {
			name: new RegExp(prefix),
		});

		await expect(threadLinks).toHaveCount(3);

		const headlines = await threadLinks.allTextContents();

		expect(headlines[0]).toContain(`${prefix} 3`);
		expect(headlines[2]).toContain(`${prefix} 1`);
	}
);

test('Can add a thread with UTF-8 characters', async ({
	messageBoardsEditThreadPage,
	page,
	site,
}) => {
	const subject = 'MB Thrèad Mèssagè Subjèct';
	const body = 'MB Thrèad Mèssagè Body';

	await messageBoardsEditThreadPage.gotoAndPublishNewBasicThread(
		subject,
		body,
		site.friendlyUrlPath
	);

	await expect(page.getByTestId('headerTitle')).toHaveText(subject);
	await expect(page.getByText(body)).toBeVisible();
});

test(
	'Can add an urgent thread',
	{tag: '@LPS-136912'},
	async ({messageBoardsEditThreadPage, messageBoardsPage, page, site}) => {
		const subject = getRandomString();

		await messageBoardsEditThreadPage.goto(site.friendlyUrlPath);

		await messageBoardsEditThreadPage.subjectSelector.fill(subject);
		await messageBoardsEditThreadPage.bodyTextBox.fill(getRandomString());

		// Set the priority to Urgent under More Settings

		await page
			.getByRole('button', {exact: true, name: 'More Settings'})
			.click();

		await page
			.locator('select[id*="priority"]')
			.selectOption({label: 'Urgent'});

		await messageBoardsEditThreadPage.publishButton.click();

		await page.waitForLoadState('networkidle');

		// The thread is listed with an urgent priority marker

		await messageBoardsPage.goto(site.friendlyUrlPath);

		await expect(page.getByRole('link', {name: subject})).toBeVisible();

		await expect(page.locator('[title="Urgent"]').first()).toBeVisible();
	}
);

test(
	'Can publish a thread with a tag',
	{tag: '@LPS-87376'},
	async ({messageBoardsEditThreadPage, page, site}) => {
		const subject = getRandomString();
		const tagName = getRandomString().toLowerCase();

		await messageBoardsEditThreadPage.goto(site.friendlyUrlPath);

		await messageBoardsEditThreadPage.subjectSelector.fill(subject);
		await messageBoardsEditThreadPage.bodyTextBox.fill(getRandomString());

		// Add a tag under Categorization

		await page
			.getByRole('button', {exact: true, name: 'Categorization'})
			.click();

		const tagsField = page.locator(
			'[id*="assetTagsSelector"] input.form-control-inset'
		);

		await tagsField.fill(tagName);
		await tagsField.press('Enter');

		await expect(
			page.locator('[id*="assetTagsSelector"]').getByText(tagName).first()
		).toBeVisible();

		await page.keyboard.press('Escape');

		await messageBoardsEditThreadPage.publishButton.click();

		await page.waitForLoadState('networkidle');

		// The published thread shows the tag

		await expect(page.getByRole('link', {name: tagName})).toBeVisible();
	}
);

test('Can save a thread as a draft', async ({
	messageBoardsEditThreadPage,
	messageBoardsPage,
	page,
	site,
}) => {
	const subject = getRandomString();

	await messageBoardsEditThreadPage.goto(site.friendlyUrlPath);

	await messageBoardsEditThreadPage.subjectSelector.fill(subject);
	await messageBoardsEditThreadPage.bodyTextBox.fill(getRandomString());

	await page.getByRole('button', {name: 'Save as Draft'}).click();

	await page.waitForLoadState('networkidle');

	await messageBoardsPage.goto(site.friendlyUrlPath);

	// The thread is listed with a draft status

	await expect(page.getByRole('link', {name: subject})).toBeVisible();

	await expect(page.getByText('Draft', {exact: true}).first()).toBeVisible();
});

test('Can cancel adding a thread', async ({
	messageBoardsEditThreadPage,
	messageBoardsPage,
	page,
	site,
}) => {
	const subject = getRandomString();

	await messageBoardsEditThreadPage.goto(site.friendlyUrlPath);

	await messageBoardsEditThreadPage.subjectSelector.fill(subject);

	await page.getByRole('button', {name: 'Cancel'}).click();

	await messageBoardsPage.goto(site.friendlyUrlPath);

	await expect(page.getByRole('link', {name: subject})).toBeHidden();
});

test(
	'Can delete a thread',
	{tag: '@LPS-82675'},
	async ({apiHelpers, messageBoardsPage, page, site}) => {
		const headline = getRandomString();

		await apiHelpers.headlessDelivery.postMessageBoardThread({
			articleBody: getRandomString(),
			headline,
			siteId: site.id,
		});

		await messageBoardsPage.goto(site.friendlyUrlPath);

		await expect(page.getByRole('link', {name: headline})).toBeVisible();

		await messageBoardsPage.deleteAllMBEntries();

		await expect(page.getByRole('link', {name: headline})).toBeHidden();
	}
);

test('A thread title over 255 characters is trimmed to 255', async ({
	apiHelpers,
	messageBoardsPage,
	page,
	site,
}) => {
	const trimmedSubject = getRandomString()
		.replace(/-/g, '')
		.repeat(8)
		.slice(0, 255);
	const overflowSubject = `${trimmedSubject}OVERFLOW`;

	// Post a message whose subject exceeds 255 characters

	await apiHelpers.jsonWebServicesMBApiHelper.addMessage({
		groupId: site.id,
		subject: overflowSubject,
	});

	await messageBoardsPage.goToThread(trimmedSubject, site.friendlyUrlPath);

	// The stored title is trimmed to 255 characters

	await expect(page.getByTestId('headerTitle')).toHaveText(trimmedSubject);
});
