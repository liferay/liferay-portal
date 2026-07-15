/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';
import {createReadStream} from 'fs';
import path from 'path';

import {apiHelpersTest} from '../../../fixtures/apiHelpersTest';
import {isolatedSiteTest} from '../../../fixtures/isolatedSiteTest';
import {loginTest} from '../../../fixtures/loginTest';
import getRandomString from '../../../utils/getRandomString';
import {blogsPagesTest} from '../../blogs-web/main/fixtures/blogsPagesTest';

const SAMPLE_IMAGE = path.join(
	__dirname,
	'../../frontend-js-item-selector-web/main/dependencies/sample_image.png'
);

const test = mergeTests(
	apiHelpersTest,
	blogsPagesTest,
	isolatedSiteTest,
	loginTest()
);

test('Preview navigates through multiple images with the keyboard', async ({
	apiHelpers,
	blogsEditBlogEntryPage,
	page,
	site,
}) => {

	// Seed three images in the site's document library

	for (const title of ['Document 1', 'Document 2', 'Document 3']) {
		await apiHelpers.headlessDelivery.postDocument(
			site.id,
			createReadStream(SAMPLE_IMAGE),
			{
				documentFolderId: 0,
				fileName: `${getRandomString()}.png`,
				title,
			}
		);
	}

	// Open the blog cover image selector

	await blogsEditBlogEntryPage.goto(site.friendlyUrlPath);

	await page.getByRole('button', {name: 'Select File'}).first().click();

	const iframe = page.frameLocator('iframe[title="Select File"]');

	await iframe.getByRole('link', {name: 'Documents and Media'}).click();

	// Open the image preview on the first image

	await iframe.locator('.icon-view').first().click();

	const footer = iframe.locator('.footer');

	await expect(footer).toContainText('1 of 3');

	await expect(iframe.locator('.pull-left .icon-arrow')).toBeVisible();
	await expect(iframe.locator('.pull-right .icon-arrow')).toBeVisible();

	// Navigate with the keyboard, wrapping backwards to the last image

	await page.keyboard.press('ArrowRight');

	await expect(footer).toContainText('2 of 3');

	await page.keyboard.press('ArrowLeft');

	await expect(footer).toContainText('1 of 3');

	await page.keyboard.press('ArrowLeft');

	await expect(footer).toContainText('3 of 3');

	// Close the preview and the selector without choosing an image

	await page.keyboard.press('Escape');

	await page.getByRole('button', {name: 'Close'}).first().click();

	// The cover image must remain unset

	await expect(
		page.getByRole('button', {name: 'Select File'}).first()
	).toBeVisible();
});
