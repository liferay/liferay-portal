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
	'JavaScript in a thread subject and body is not executed',
	{tag: ['@LPS-137634', '@LPS-65365']},
	async ({messageBoardsEditThreadPage, page, site}) => {
		const dialogs: string[] = [];

		page.on('dialog', async (dialog) => {
			dialogs.push(dialog.message());

			await dialog.dismiss();
		});

		const script = "<script>alert('XSS');</script>";
		const subject = `${getRandomString()} ${script}`;

		await messageBoardsEditThreadPage.gotoAndPublishNewBasicThread(
			subject,
			script,
			site.friendlyUrlPath
		);

		// The script is rendered as text, not executed

		await expect(page.getByTestId('headerTitle')).toHaveText(subject);

		expect(dialogs).toHaveLength(0);
	}
);

test(
	'BBCode content posted through remote services does not execute scripts',
	{tag: '@LPS-65368'},
	async ({apiHelpers, messageBoardsPage, page, site}) => {
		const dialogs: string[] = [];

		page.on('dialog', async (dialog) => {
			dialogs.push(dialog.message());

			await dialog.dismiss();
		});

		const script = '<script>alert(123);</script>';
		const subject = getRandomString();

		// Post a BBCode message through the JSON web service

		await apiHelpers.jsonWebServicesMBApiHelper.addMessage({
			body: script,
			groupId: site.id,
			subject,
		});

		await messageBoardsPage.goToThread(subject, site.friendlyUrlPath);

		// The script is rendered as text, not executed

		await expect(page.getByTestId('headerTitle')).toHaveText(subject);

		expect(dialogs).toHaveLength(0);
	}
);

test(
	'A malformed attachment URL returns a bad request instead of executing',
	{tag: '@LPS-131529'},
	async ({messageBoardsEditThreadPage, page, site}) => {
		const subject = getRandomString();

		await messageBoardsEditThreadPage.goto(site.friendlyUrlPath);

		await messageBoardsEditThreadPage.subjectSelector.fill(subject);
		await messageBoardsEditThreadPage.bodyTextBox.fill(getRandomString());

		// Attach an HTML file to the thread

		const isExpanded =
			await messageBoardsEditThreadPage.attachmentCollapse.evaluate(
				(element) => element.getAttribute('aria-expanded') === 'true'
			);

		if (!isExpanded) {
			await messageBoardsEditThreadPage.attachmentCollapse.click();
		}

		const fileChooserPromise = page.waitForEvent('filechooser');

		await messageBoardsEditThreadPage.fileSelector.click();

		const fileChooser = await fileChooserPromise;

		await fileChooser.setFiles({
			buffer: Buffer.from('<html><body>XSS</body></html>'),
			mimeType: 'text/html',
			name: 'xss_test.html',
		});

		await messageBoardsEditThreadPage.allFilesReadyToBeSavedMessage.waitFor();

		await messageBoardsEditThreadPage.publishButton.click();

		// Requesting the attachment with a malformed URL is rejected

		const attachmentURL = await page
			.getByRole('link', {name: /xss_test\.html/})
			.getAttribute('href');

		const malformedURL = attachmentURL!.replace(
			'xss_test.html',
			'xss_test%.html'
		);

		const response = await page.request.get(malformedURL, {
			maxRedirects: 0,
		});

		expect(response.status()).toBe(400);
	}
);

test('JavaScript in a user name is not executed when viewing a thread', async ({
	apiHelpers,
	messageBoardsEditThreadPage,
	messageBoardsPage,
	page,
	site,
}) => {
	const dialogs: string[] = [];

	page.on('dialog', async (dialog) => {
		dialogs.push(dialog.message());

		await dialog.dismiss();
	});

	const subject = getRandomString();

	// Create a user whose last name carries a script payload

	const user = await apiHelpers.headlessAdminUser.postUserAccount({
		familyName: `${getRandomString()}<img src=x onerror=alert('XSS')>`,
	});

	const siteAdministratorRole =
		await apiHelpers.headlessAdminUser.getRoleByName('Site Administrator');

	await apiHelpers.headlessAdminUser.assignUserToSite(
		siteAdministratorRole.id,
		site.id,
		user.id
	);

	// The user posts a thread, so their name is the author shown on the thread

	await messageBoardsPage.goto(site.friendlyUrlPath);

	await page.waitForURL(/MBAdminPortlet/);

	const url = new URL(page.url());

	url.searchParams.set('doAsUserId', String(user.id));

	await page.goto(url.toString());

	await messageBoardsPage.goToCreateNewThread();

	await messageBoardsEditThreadPage.publishNewBasicThread(
		subject,
		getRandomString()
	);

	// The script in the author name is rendered as text, not executed

	await expect(page.getByTestId('headerTitle')).toHaveText(subject);

	expect(dialogs).toHaveLength(0);
});
