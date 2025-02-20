/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';
import {createReadStream} from 'fs';
import path from 'path';

import {apiHelpersTest} from '../../fixtures/apiHelpersTest';
import {changeTrackingPagesTest} from '../../fixtures/changeTrackingPagesTest';
import {featureFlagsTest} from '../../fixtures/featureFlagsTest';
import {isolatedSiteTest} from '../../fixtures/isolatedSiteTest';
import {loginTest} from '../../fixtures/loginTest';
import getRandomString from '../../utils/getRandomString';
import {blogsPagesTest} from '../blogs-web/fixtures/blogsPagesTest';

const test = mergeTests(
	apiHelpersTest,
	changeTrackingPagesTest,
	isolatedSiteTest,
	loginTest(),
	featureFlagsTest({
		'LPD-39304': {enabled: true},
		'LPS-178052': {enabled: true},
	}),
	blogsPagesTest
);

test('Can publish a Publication containing an added Blog Entry with cover image', async ({
	apiHelpers,
	blogsEditBlogEntryPage,
	blogsPage,
	changeTrackingPage,
	ctCollection,
	page,
	site,
}) => {
	await changeTrackingPage.workOnPublication(ctCollection);

	const coverImageTitle = 'image1.jpeg';

	await apiHelpers.headlessDelivery.postDocument(
		site.id,
		createReadStream(
			path.join(__dirname, '/../blogs-web/dependencies/image1.jpeg')
		),
		{
			description: getRandomString(),
			fileName: getRandomString(),
			title: coverImageTitle,
		}
	);

	await blogsEditBlogEntryPage.goto(site.friendlyUrlPath);

	const blogTitle = getRandomString();

	await blogsEditBlogEntryPage.editBlogEntry({
		content: getRandomString(),
		publish: false,
		title: blogTitle,
	});
	await blogsEditBlogEntryPage.selectCoverImage(coverImageTitle);

	const itemSelectorDialog = page.locator('iframe[title="Select File"]');

	if (await itemSelectorDialog.isVisible()) {
		await page.keyboard.press('Escape');
	}

	await blogsEditBlogEntryPage.publishBlogEntry();

	await apiHelpers.headlessChangeTracking.publishCTCollection(
		ctCollection.body.id
	);

	await page.reload();

	await blogsPage.goto(site.friendlyUrlPath);

	await expect(page.getByText(blogTitle)).toBeVisible();
});

test('Can publish a Publication containing an edited Blog Entry', async ({
	apiHelpers,
	blogsEditBlogEntryPage,
	blogsPage,
	changeTrackingPage,
	ctCollection,
	page,
	site,
}) => {
	await blogsEditBlogEntryPage.goto(site.friendlyUrlPath);
	await blogsEditBlogEntryPage.editBlogEntry({
		content: getRandomString(),
		publish: false,
		title: getRandomString(),
	});
	await blogsEditBlogEntryPage.publishBlogEntry();

	await changeTrackingPage.workOnPublication(ctCollection);

	const updatedBlogsTitle = getRandomString();

	await blogsEditBlogEntryPage.goto(site.friendlyUrlPath);

	await blogsEditBlogEntryPage.editBlogEntry({
		content: getRandomString(),
		publish: true,
		title: updatedBlogsTitle,
	});

	await apiHelpers.headlessChangeTracking.publishCTCollection(
		ctCollection.body.id
	);

	await page.reload();

	await blogsPage.goto(site.friendlyUrlPath);

	await expect(page.getByText(updatedBlogsTitle)).toBeVisible();
});
