/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Page, expect, mergeTests} from '@playwright/test';
import {createReadStream} from 'fs';
import path from 'path';

import {apiHelpersTest} from '../../../fixtures/apiHelpersTest';
import {isolatedSiteTest} from '../../../fixtures/isolatedSiteTest';
import {loginTest} from '../../../fixtures/loginTest';
import {messageBoardsPagesTest} from '../../../fixtures/messageBoardsTest';
import {MessageBoardsEditThreadPage} from '../../../pages/message-boards/MessageBoardsEditThreadPage';
import getRandomString from '../../../utils/getRandomString';
import {PORTLET_URLS} from '../../../utils/portletUrls';

const test = mergeTests(
	apiHelpersTest,
	isolatedSiteTest,
	loginTest(),
	messageBoardsPagesTest
);

async function addAttachment(
	messageBoardsEditThreadPage: MessageBoardsEditThreadPage,
	name: string,
	page: Page
) {
	const expanded =
		await messageBoardsEditThreadPage.attachmentCollapse.evaluate(
			(element) => element.getAttribute('aria-expanded') === 'true'
		);

	if (!expanded) {
		await messageBoardsEditThreadPage.attachmentCollapse.click();
	}

	const fileChooserPromise = page.waitForEvent('filechooser');

	await messageBoardsEditThreadPage.fileSelector.click();

	const fileChooser = await fileChooserPromise;

	await fileChooser.setFiles({
		buffer: Buffer.from(getRandomString()),
		mimeType: 'text/plain',
		name,
	});

	await messageBoardsEditThreadPage.allFilesReadyToBeSavedMessage.waitFor();
}

async function openRecycleBinEntryAction(
	action: string,
	page: Page,
	siteUrl: Site['friendlyUrlPath']
) {
	await page.goto(`/group${siteUrl}${PORTLET_URLS.recycleBin}`);

	await page.getByRole('button', {name: 'Show Actions'}).click();

	await page.getByRole('menuitem', {name: action}).click();
}

test('Can add a thread with multiple attachments', async ({
	messageBoardsEditThreadPage,
	page,
	site,
}) => {
	const firstFileName = `${getRandomString()}.txt`;
	const secondFileName = `${getRandomString()}.txt`;

	await messageBoardsEditThreadPage.goto(site.friendlyUrlPath);

	await messageBoardsEditThreadPage.subjectSelector.fill(getRandomString());
	await messageBoardsEditThreadPage.bodyTextBox.fill(getRandomString());

	await addAttachment(messageBoardsEditThreadPage, firstFileName, page);
	await addAttachment(messageBoardsEditThreadPage, secondFileName, page);

	await messageBoardsEditThreadPage.publishButton.click();

	// Both attachments are listed on the published thread

	await expect(
		page.getByRole('link', {name: new RegExp(firstFileName)})
	).toBeVisible();
	await expect(
		page.getByRole('link', {name: new RegExp(secondFileName)})
	).toBeVisible();
});

test('Can reply after downloading a thread attachment twice', async ({
	messageBoardsEditThreadPage,
	page,
	site,
}) => {
	const fileName = `${getRandomString()}.txt`;
	const replyBody = getRandomString();

	await messageBoardsEditThreadPage.goto(site.friendlyUrlPath);

	await messageBoardsEditThreadPage.subjectSelector.fill(getRandomString());
	await messageBoardsEditThreadPage.bodyTextBox.fill(getRandomString());

	await addAttachment(messageBoardsEditThreadPage, fileName, page);

	await messageBoardsEditThreadPage.publishButton.click();

	// Download the attachment twice

	const attachmentLink = page.getByRole('link', {name: new RegExp(fileName)});

	for (let index = 0; index < 2; index++) {
		const downloadPromise = page.waitForEvent('download');

		await attachmentLink.click();

		await downloadPromise;
	}

	// Replying still succeeds

	await messageBoardsEditThreadPage.publishReply(replyBody);

	await expect(page.getByText(replyBody)).toBeVisible();
});

test(
	'Can download a thread attachment',
	{tag: '@LPS-136914'},
	async ({messageBoardsEditThreadPage, page, site}) => {
		const fileName = `${getRandomString()}.txt`;

		await messageBoardsEditThreadPage.goto(site.friendlyUrlPath);

		await messageBoardsEditThreadPage.subjectSelector.fill(
			getRandomString()
		);
		await messageBoardsEditThreadPage.bodyTextBox.fill(getRandomString());

		await addAttachment(messageBoardsEditThreadPage, fileName, page);

		await messageBoardsEditThreadPage.publishButton.click();

		// The attachment can be downloaded from the published thread

		const downloadPromise = page.waitForEvent('download');

		await page.getByRole('link', {name: new RegExp(fileName)}).click();

		const download = await downloadPromise;

		expect(download.suggestedFilename()).toBe(fileName);
	}
);

test('Can view a thread body image after moving the document to a folder', async ({
	apiHelpers,
	messageBoardsEditThreadPage,
	page,
	site,
}) => {
	const documentTitle = getRandomString().replace(/-/g, '');
	const subject = getRandomString();

	// Seed an image in the Document Library

	const document = await apiHelpers.headlessDelivery.postDocument(
		site.id,
		createReadStream(path.join(__dirname, 'dependencies/image1.jpeg')),
		{
			fileName: `${documentTitle}.jpeg`,
			title: documentTitle,
		}
	);

	// Add a thread with the image inserted into the body

	await messageBoardsEditThreadPage.goto(site.friendlyUrlPath);

	await messageBoardsEditThreadPage.subjectSelector.fill(subject);

	await messageBoardsEditThreadPage.insertBodyImage(documentTitle);

	await messageBoardsEditThreadPage.publishButton.click();

	// Move the document into a folder

	const documentFolder = await apiHelpers.headlessDelivery.postDocumentFolder(
		site.id
	);

	await apiHelpers.headlessDelivery.patchDocument({
		document: {documentFolderId: documentFolder.id},
		documentId: document.id,
	});

	// The image still renders after the move

	await page.reload();

	const threadImage = page.locator('.message-content img');

	await expect(threadImage).toBeVisible();

	await expect
		.poll(() =>
			threadImage.evaluate(
				(image: HTMLImageElement) => image.naturalWidth
			)
		)
		.toBeGreaterThan(0);
});

test('Can permanently delete a trashed thread that has an attachment', async ({
	messageBoardsEditThreadPage,
	messageBoardsPage,
	page,
	site,
}) => {
	const subject = getRandomString();

	// Add a thread with an attachment

	await messageBoardsEditThreadPage.goto(site.friendlyUrlPath);

	await messageBoardsEditThreadPage.subjectSelector.fill(subject);
	await messageBoardsEditThreadPage.bodyTextBox.fill(getRandomString());

	await addAttachment(
		messageBoardsEditThreadPage,
		`${getRandomString()}.txt`,
		page
	);

	await messageBoardsEditThreadPage.publishButton.click();

	// Wait for the thread to be published before navigating away

	await expect(page.getByTestId('headerTitle')).toHaveText(subject);

	// Move the thread to the Recycle Bin

	await messageBoardsPage.goto(site.friendlyUrlPath);

	await messageBoardsPage.deleteAllMBEntries();

	await expect(page.getByRole('link', {name: subject})).toBeHidden();

	// Permanently delete the thread from the Recycle Bin

	await openRecycleBinEntryAction('Delete', page, site.friendlyUrlPath);

	await page
		.getByRole('dialog')
		.getByRole('button', {exact: true, name: 'Delete'})
		.click();

	await expect(page.getByRole('link', {name: subject})).toBeHidden();
});

test('Can restore a trashed category with a thread that has an attachment', async ({
	apiHelpers,
	messageBoardsEditThreadPage,
	messageBoardsPage,
	page,
	site,
}) => {
	const attachmentName = `${getRandomString()}.txt`;
	const categoryName = getRandomString();
	const replyBody = getRandomString();
	const subject = getRandomString();

	const category =
		await apiHelpers.headlessDelivery.postSiteMessageBoardSection({
			siteId: site.id,
			title: categoryName,
		});

	// Add a thread with an attachment and a reply inside the category

	await messageBoardsPage.goto(site.friendlyUrlPath);

	await page.getByRole('link', {name: category.title}).click();

	await messageBoardsPage.goToCreateNewThread();

	await messageBoardsEditThreadPage.subjectSelector.fill(subject);
	await messageBoardsEditThreadPage.bodyTextBox.fill(getRandomString());

	await addAttachment(messageBoardsEditThreadPage, attachmentName, page);

	await messageBoardsEditThreadPage.publishButton.click();

	await messageBoardsEditThreadPage.publishReply(replyBody);

	// Wait for the reply to be published before navigating away

	await expect(page.getByText(replyBody)).toBeVisible();

	// Move the category to the Recycle Bin

	await messageBoardsPage.goto(site.friendlyUrlPath);

	await messageBoardsPage.deleteAllMBEntries();

	await expect(page.getByRole('link', {name: category.title})).toBeHidden();

	// Restore the category from the Recycle Bin

	await openRecycleBinEntryAction('Restore', page, site.friendlyUrlPath);

	await expect(page.getByRole('link', {name: category.title})).toBeHidden();

	// The category, its thread, the attachment and the reply are restored

	await messageBoardsPage.goto(site.friendlyUrlPath);

	await page.getByRole('link', {name: category.title}).click();

	await page.getByRole('link', {name: subject}).click();

	await expect(
		page.getByRole('link', {name: new RegExp(attachmentName)})
	).toBeVisible();

	await expect(page.getByText(replyBody)).toBeVisible();
});
