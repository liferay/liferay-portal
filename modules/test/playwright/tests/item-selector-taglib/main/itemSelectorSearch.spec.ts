/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {FrameLocator, expect, mergeTests} from '@playwright/test';
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

async function searchForSingleResult(iframe: FrameLocator, term: string) {
	const searchbox = iframe.getByRole('searchbox');

	// The search only fires once the keywords are submitted; retry the
	// submission until the single expected result surfaces.

	await expect(async () => {
		await searchbox.fill(term);

		await searchbox.press('Enter');

		await expect(
			iframe.getByText(`1 Result Found for "${term}"`)
		).toBeVisible({timeout: 5000});
	}).toPass({timeout: 30000});
}

test('Search finds a Documents and Media image, absent from Blog Images', async ({
	apiHelpers,
	blogsEditBlogEntryPage,
	page,
	site,
}) => {
	const fileName = `${getRandomString()}.png`;

	await apiHelpers.headlessDelivery.postDocument(
		site.id,
		createReadStream(SAMPLE_IMAGE),
		{
			documentFolderId: 0,
			fileName,
			title: fileName,
		}
	);

	// Open the blog cover image selector, which lands on the Blog Images tab

	await blogsEditBlogEntryPage.goto(site.friendlyUrlPath);

	await page.getByRole('button', {name: 'Select File'}).first().click();

	const iframe = page.frameLocator('iframe[title="Select File"]');

	// The Documents and Media image is not listed under Blog Images

	await expect(iframe.getByText(fileName)).toBeHidden();

	// Searching under Documents and Media finds the image

	await iframe.getByRole('link', {name: 'Documents and Media'}).click();

	await searchForSingleResult(iframe, fileName);

	await expect(iframe.getByTitle(fileName)).toBeVisible();
});

test('Search finds a Blog Images upload, absent from Documents and Media', async ({
	blogsEditBlogEntryPage,
	page,
	site,
}) => {

	// Open the blog cover image selector, which lands on the Blog Images tab

	await blogsEditBlogEntryPage.goto(site.friendlyUrlPath);

	await page.getByRole('button', {name: 'Select File'}).first().click();

	const iframe = page.frameLocator('iframe[title="Select File"]');

	// Upload an image through the Blog Images tab

	await iframe.locator('input[type="file"]').setInputFiles(SAMPLE_IMAGE);

	await iframe.getByRole('button', {name: 'Add'}).click();

	// Reopen the selector; the upload lives under Blog Images, not DM

	await page.locator('button.browse-image').click();

	await iframe.getByRole('link', {name: 'Documents and Media'}).click();

	await expect(iframe.getByTitle('sample_image.png')).toBeHidden();

	// Searching under Blog Images finds the upload

	await iframe.getByRole('link', {name: 'Blog Images'}).click();

	await searchForSingleResult(iframe, 'sample_image.png');

	await expect(iframe.getByTitle('sample_image.png')).toBeVisible();
});
