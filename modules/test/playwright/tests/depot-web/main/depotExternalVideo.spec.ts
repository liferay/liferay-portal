/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Page, expect, mergeTests} from '@playwright/test';

import {dataApiHelpersTest} from '../../../fixtures/dataApiHelpersTest';
import {featureFlagsTest} from '../../../fixtures/featureFlagsTest';
import {isolatedSiteTest} from '../../../fixtures/isolatedSiteTest';
import {loginTest} from '../../../fixtures/loginTest';
import {pageEditorPagesTest} from '../../../fixtures/pageEditorPagesTest';
import {clickAndExpectToBeVisible} from '../../../utils/clickAndExpectToBeVisible';
import getRandomString from '../../../utils/getRandomString';
import {waitForAlert} from '../../../utils/waitForAlert';
import {BlogsEditBlogEntryPage} from '../../blogs-web/main/pages/BlogsEditBlogEntryPage';
import {JournalEditArticlePage} from '../../journal-web/main/pages/JournalEditArticlePage';
import getFragmentDefinition from '../../layout-content-page-editor-web/main/utils/getFragmentDefinition';
import getPageDefinition from '../../layout-content-page-editor-web/main/utils/getPageDefinition';
import getWidgetDefinition from '../../layout-content-page-editor-web/main/utils/getWidgetDefinition';
import addConnectedDepot from './utils/addConnectedDepot';

const test = mergeTests(
	dataApiHelpersTest,
	featureFlagsTest({
		'LPS-178052': {enabled: true},
	}),
	isolatedSiteTest,
	loginTest(),
	pageEditorPagesTest
);

const YOUTUBE_EMBED_URL = 'https://www.youtube.com/embed/HOdbzGCI5ME';

async function openVideoItemSelectorEverywhere(page: Page, site: Site) {
	const blogsEditBlogEntryPage = new BlogsEditBlogEntryPage(page);

	await blogsEditBlogEntryPage.goto(site.friendlyUrlPath);

	const itemSelector = await blogsEditBlogEntryPage.openVideoItemSelector();

	await clickAndExpectToBeVisible({
		autoClick: true,
		target: itemSelector.getByRole('menuitem', {name: 'Everywhere'}),
		trigger: itemSelector.getByRole('button', {name: 'Filter'}),
	});

	return {blogsEditBlogEntryPage, itemSelector};
}

test(
	'Asset library external video can be embedded in a blog of a connected site until the site is disconnected',
	{tag: '@LPD-112510'},
	async ({apiHelpers, page, site}) => {
		const assetLibrary = await addConnectedDepot(apiHelpers, site);

		const siteVideoTitle = getRandomString();

		await apiHelpers.headlessDelivery.postExternalVideoShortcut(
			`sites/${site.id}`,
			{title: siteVideoTitle}
		);

		const videoTitle = getRandomString();

		await apiHelpers.headlessDelivery.postExternalVideoShortcut(
			`asset-libraries/${assetLibrary.id}`,
			{title: videoTitle}
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

		const blogTitle = getRandomString();

		await test.step('Embed the video from the asset library in a blog entry', async () => {
			const {blogsEditBlogEntryPage, itemSelector} =
				await openVideoItemSelectorEverywhere(page, site);

			await itemSelector.getByText(videoTitle, {exact: true}).click();

			await expect(
				blogsEditBlogEntryPage.contentEditor.locator(
					`[data-embed-url^="${YOUTUBE_EMBED_URL}"]`
				)
			).toBeVisible();

			await blogsEditBlogEntryPage.titleInput.fill(blogTitle);

			await blogsEditBlogEntryPage.publishButton.click();

			await waitForAlert(page);
		});

		await test.step('The blog entry shows the video', async () => {
			await page.goto(
				`/web${site.friendlyUrlPath}${layout.friendlyUrlPath}`
			);

			await page
				.getByRole('link', {exact: true, name: blogTitle})
				.click();

			await expect(
				page.locator(`iframe[src^="${YOUTUBE_EMBED_URL}"]`)
			).toBeVisible();
		});

		await test.step('The video is not offered once the site is disconnected', async () => {
			await apiHelpers.headlessAssetLibrary.disconnectSite(
				assetLibrary.externalReferenceCode,
				site.externalReferenceCode
			);

			const {itemSelector} = await openVideoItemSelectorEverywhere(
				page,
				site
			);

			await expect(
				itemSelector.getByText(siteVideoTitle, {exact: true})
			).toBeVisible();
			await expect(
				itemSelector.getByText(videoTitle, {exact: true})
			).toHaveCount(0);
		});
	}
);

test(
	'External Video fragment shows a video selected from an asset library',
	{tag: ['@LPD-112510', '@LPS-154383']},
	async ({apiHelpers, page, pageEditorPage, site}) => {
		const assetLibrary = await addConnectedDepot(apiHelpers, site);

		const videoTitle = getRandomString();

		await apiHelpers.headlessDelivery.postExternalVideoShortcut(
			`asset-libraries/${assetLibrary.id}`,
			{title: videoTitle}
		);

		const fragmentId = getRandomString();

		const layout = await apiHelpers.headlessDelivery.createSitePage({
			pageDefinition: getPageDefinition([
				getFragmentDefinition({
					id: fragmentId,
					key: 'BASIC_COMPONENT-external-video',
				}),
			]),
			siteId: site.id,
			title: getRandomString(),
		});

		// Select the asset library video in the fragment and publish the page

		await pageEditorPage.goto(layout, site.friendlyUrlPath);

		await pageEditorPage.selectFragment(fragmentId);

		await page.getByTitle('Select Video', {exact: true}).click();

		const itemSelector = page.frameLocator('iframe[title="Select"]');

		await itemSelector
			.getByRole('link', {exact: true, name: 'Documents and Media'})
			.click();
		await itemSelector
			.getByRole('link', {exact: true, name: 'Sites and Libraries'})
			.click();
		await itemSelector.getByText('Asset Library', {exact: true}).click();
		await itemSelector.getByText(assetLibrary.name, {exact: true}).click();
		await itemSelector.getByText(videoTitle, {exact: true}).click();

		await pageEditorPage.publishPage();

		// View the page

		await page.goto(`/web${site.friendlyUrlPath}${layout.friendlyUrlPath}`);

		await expect(
			page.locator(`.video-container iframe[src^="${YOUTUBE_EMBED_URL}"]`)
		).toBeVisible();
	}
);

test(
	'Asset library external video can be a related asset of a connected site web content',
	{tag: '@LPD-112510'},
	async ({apiHelpers, page, site}) => {
		const assetLibrary = await addConnectedDepot(apiHelpers, site);

		const videoTitle = getRandomString();

		await apiHelpers.headlessDelivery.postExternalVideoShortcut(
			`asset-libraries/${assetLibrary.id}`,
			{title: videoTitle}
		);

		// Relate the video to a web content from the Related Assets selector

		const journalEditArticlePage = new JournalEditArticlePage(page);

		await journalEditArticlePage.goto({siteUrl: site.friendlyUrlPath});

		const webContentTitle = getRandomString();

		await journalEditArticlePage.fillTitle(webContentTitle);
		await journalEditArticlePage.fillContent(getRandomString());

		await journalEditArticlePage.openRelatedAsset();

		const selectAssetDialog = page.getByRole('dialog', {
			name: 'Select Asset',
		});

		await selectAssetDialog
			.getByRole('row', {name: videoTitle})
			.getByRole('checkbox')
			.check();
		await selectAssetDialog
			.getByRole('button', {exact: true, name: 'Select'})
			.click();

		await journalEditArticlePage.publishArticle();

		await waitForAlert(
			page,
			`Success:${webContentTitle} was created successfully.`
		);

		// The Asset Publisher lists the video as a related asset and opens it

		const layout = await apiHelpers.headlessDelivery.createSitePage({
			pageDefinition: getPageDefinition([
				getWidgetDefinition({
					id: getRandomString(),
					widgetConfig: {
						displayStyle: 'full-content',
						enableRelatedAssets: 'true',
					},
					widgetName:
						'com_liferay_asset_publisher_web_portlet_AssetPublisherPortlet',
				}),
			]),
			siteId: site.id,
			title: getRandomString(),
		});

		await page.goto(`/web${site.friendlyUrlPath}${layout.friendlyUrlPath}`);

		await expect(
			page.getByText(webContentTitle, {exact: true})
		).toBeVisible();

		await page.getByRole('link', {exact: true, name: videoTitle}).click();

		await expect(
			page.locator(`iframe[src^="${YOUTUBE_EMBED_URL}"]`)
		).toBeVisible();
	}
);
