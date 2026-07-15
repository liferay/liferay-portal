/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Page, expect, mergeTests} from '@playwright/test';
import {createReadStream} from 'fs';
import path from 'path';

import {apiHelpersTest} from '../../../fixtures/apiHelpersTest';
import {documentLibraryPagesTest} from '../../../fixtures/documentLibraryPages.fixtures';
import {isolatedSiteTest} from '../../../fixtures/isolatedSiteTest';
import {loginTest} from '../../../fixtures/loginTest';
import {blogsPagesTest} from '../../blogs-web/main/fixtures/blogsPagesTest';

const DOCUMENT_2 = path.join(__dirname, 'dependencies/Document_2.jpg');

const test = mergeTests(
	apiHelpersTest,
	blogsPagesTest,
	documentLibraryPagesTest,
	isolatedSiteTest,
	loginTest()
);

async function getImageSize(
	page: Page,
	contentUrl: string
): Promise<{height: number; width: number}> {
	return page.evaluate(
		(url) =>
			new Promise((resolve) => {
				const image = new Image();

				image.onload = () =>
					resolve({
						height: image.naturalHeight,
						width: image.naturalWidth,
					});

				image.src = `${url}&imageEditorTest=${Date.now()}`;
			}),
		contentUrl
	);
}

async function openImageEditor(page: Page) {
	await page.getByRole('button', {name: 'Actions'}).first().click();

	await page.getByRole('menuitem', {name: 'Edit Image'}).click();

	await page.getByRole('dialog').waitFor();
}

test('Rotating an image is discarded on cancel and kept on save', async ({
	apiHelpers,
	documentLibraryPage,
	page,
	site,
}) => {
	const document = await apiHelpers.headlessDelivery.postDocument(
		site.id,
		createReadStream(DOCUMENT_2),
		{
			documentFolderId: 0,
			fileName: 'Document_2.jpg',
			title: 'Document_2.jpg',
		}
	);

	await documentLibraryPage.goto(site.friendlyUrlPath);

	// Rotate then cancel: the original image is preserved

	await openImageEditor(page);

	await page
		.getByRole('dialog')
		.getByRole('button', {name: 'Rotate'})
		.click();

	await page
		.getByRole('dialog')
		.getByRole('button', {name: 'Cancel'})
		.click();

	await expect(page.getByRole('dialog')).toBeHidden();

	const {contentUrl: originalUrl} =
		await apiHelpers.headlessDelivery.getDocument(document.id);

	expect(await getImageSize(page, originalUrl)).toEqual({
		height: 313,
		width: 500,
	});

	// Rotate then save: the rotated image is persisted

	await openImageEditor(page);

	await page
		.getByRole('dialog')
		.getByRole('button', {name: 'Rotate'})
		.click();

	await page.getByRole('dialog').getByRole('button', {name: 'Save'}).click();

	await expect(page.getByRole('dialog')).toBeHidden();

	const {contentUrl: rotatedUrl} =
		await apiHelpers.headlessDelivery.getDocument(document.id);

	expect(await getImageSize(page, rotatedUrl)).toEqual({
		height: 195,
		width: 313,
	});
});

test('Resizing an image saves the new dimensions', async ({
	apiHelpers,
	documentLibraryPage,
	page,
	site,
}) => {
	const document = await apiHelpers.headlessDelivery.postDocument(
		site.id,
		createReadStream(DOCUMENT_2),
		{
			documentFolderId: 0,
			fileName: 'Document_2.jpg',
			title: 'Document_2.jpg',
		}
	);

	await documentLibraryPage.goto(site.friendlyUrlPath);

	await openImageEditor(page);

	await page
		.getByRole('dialog')
		.getByRole('combobox')
		.selectOption({label: '16:10'});

	await page.getByRole('dialog').getByRole('button', {name: 'Save'}).click();

	await expect(page.getByRole('dialog')).toBeHidden();

	const {contentUrl} = await apiHelpers.headlessDelivery.getDocument(
		document.id
	);

	expect(await getImageSize(page, contentUrl)).toEqual({
		height: 312,
		width: 500,
	});
});

test('Resizing an image from the item selector preview saves a copy', async ({
	apiHelpers,
	blogsEditBlogEntryPage,
	page,
	site,
}) => {
	await apiHelpers.headlessDelivery.postDocument(
		site.id,
		createReadStream(DOCUMENT_2),
		{
			documentFolderId: 0,
			fileName: 'Document_2.jpg',
			title: 'Document_2.jpg',
		}
	);

	// Preview the image from the blog cover image selector

	await blogsEditBlogEntryPage.goto(site.friendlyUrlPath);

	await page.getByRole('button', {name: 'Select File'}).first().click();

	const itemSelector = page.frameLocator('iframe[title="Select File"]');

	await itemSelector.getByRole('link', {name: 'Documents and Media'}).click();

	await itemSelector.locator('.icon-view').first().click();

	// Edit the previewed image: resizing and saving creates a copy

	await itemSelector.getByRole('button', {name: 'Edit'}).click();

	await itemSelector.getByRole('combobox').selectOption({label: '16:10'});

	await itemSelector.getByRole('button', {name: 'Save'}).click();

	// The original is preserved and a resized copy is created

	let documentId: string | undefined;
	let copyId: string | undefined;

	await expect(async () => {
		const {items} = await apiHelpers.headlessDelivery.getSiteDocumentsPage(
			site.id
		);

		documentId = items.find((item) => item.title === 'Document_2.jpg')?.id;
		copyId = items.find((item) => item.title === 'Document_2 (1).jpg')?.id;

		expect(documentId).toBeTruthy();
		expect(copyId).toBeTruthy();
	}).toPass();

	const {contentUrl: originalUrl} =
		await apiHelpers.headlessDelivery.getDocument(documentId!);
	const {contentUrl: copyUrl} = await apiHelpers.headlessDelivery.getDocument(
		copyId!
	);

	expect(await getImageSize(page, originalUrl)).toEqual({
		height: 313,
		width: 500,
	});

	expect(await getImageSize(page, copyUrl)).toEqual({
		height: 312,
		width: 500,
	});
});
