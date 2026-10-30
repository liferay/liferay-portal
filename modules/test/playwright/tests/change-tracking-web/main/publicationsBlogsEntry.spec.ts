/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';
import {createReadStream} from 'fs';
import path from 'path';

import {apiHelpersTest} from '../../../fixtures/apiHelpersTest';
import {changeTrackingPagesTest} from '../../../fixtures/changeTrackingPagesTest';
import {featureFlagsTest} from '../../../fixtures/featureFlagsTest';
import {isolatedSiteTest} from '../../../fixtures/isolatedSiteTest';
import {loginTest} from '../../../fixtures/loginTest';
import getRandomString from '../../../utils/getRandomString';
import {blogsPagesTest} from '../../blogs-web/main/fixtures/blogsPagesTest';
import getPageDefinition from '../../layout-content-page-editor-web/main/utils/getPageDefinition';
import getWidgetDefinition from '../../layout-content-page-editor-web/main/utils/getWidgetDefinition';

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
			path.join(
				__dirname,
				'/../../blogs-web/main/dependencies/image1.jpeg'
			)
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

test(
	'Can publish a Publication containing a Blog Entry that embeds an asset library video added in it',
	{tag: '@LPD-112510'},
	async ({
		apiHelpers,
		blogsEditBlogEntryPage,
		changeTrackingPage,
		ctCollection,
		page,
		site,
	}) => {
		const assetLibrary =
			await apiHelpers.headlessAssetLibrary.createAssetLibrary();

		await apiHelpers.headlessAssetLibrary.connectSite(
			assetLibrary.externalReferenceCode,
			site.externalReferenceCode
		);

		const layout = await apiHelpers.headlessDelivery.createSitePage({
			pageDefinition: getPageDefinition([
				getWidgetDefinition({
					id: getRandomString(),
					widgetName: 'com_liferay_blogs_web_portlet_BlogsPortlet',
				}),
			]),
			siteId: site.id,
			title: getRandomString(),
		});

		// Add the video and a blog entry that embeds it in the publication

		await changeTrackingPage.workOnPublication(ctCollection);

		const videoTitle = getRandomString();

		await apiHelpers.headlessDelivery.postExternalVideoShortcut(
			`asset-libraries/${assetLibrary.id}`,
			{title: videoTitle}
		);

		await blogsEditBlogEntryPage.goto(site.friendlyUrlPath);

		const itemSelector =
			await blogsEditBlogEntryPage.openVideoItemSelector();

		await itemSelector
			.getByRole('link', {exact: true, name: 'Sites and Libraries'})
			.click();
		await itemSelector.getByText('Asset Library', {exact: true}).click();
		await itemSelector.getByText(assetLibrary.name, {exact: true}).click();
		await itemSelector.getByText(videoTitle, {exact: true}).click();

		const blogTitle = getRandomString();

		await blogsEditBlogEntryPage.fillTitle(blogTitle);

		await blogsEditBlogEntryPage.publishBlogEntry();

		// Publish the publication and view the blog entry on production

		await apiHelpers.headlessChangeTracking.publishCTCollection(
			ctCollection.body.id
		);

		await expect(async () => {
			await page.goto(
				`/web${site.friendlyUrlPath}${layout.friendlyUrlPath}`
			);

			await page
				.getByRole('link', {exact: true, name: blogTitle})
				.click({timeout: 2000});
		}).toPass({timeout: 60000});

		await expect(
			page.locator(
				'iframe[src^="https://www.youtube.com/embed/HOdbzGCI5ME"]'
			)
		).toBeVisible();

		await apiHelpers.headlessAssetLibrary.deleteAssetLibrary(
			assetLibrary.externalReferenceCode
		);
	}
);
