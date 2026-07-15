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

const DOCUMENT_2 = path.join(__dirname, 'dependencies/Document_2.jpg');

const test = mergeTests(
	apiHelpersTest,
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
