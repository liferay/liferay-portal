/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {FrameLocator, Locator, expect, mergeTests} from '@playwright/test';
import {createReadStream} from 'fs';
import path from 'path';

import {dataApiHelpersTest} from '../../../fixtures/dataApiHelpersTest';
import {featureFlagsTest} from '../../../fixtures/featureFlagsTest';
import {isolatedSiteTest} from '../../../fixtures/isolatedSiteTest';
import {loginTest} from '../../../fixtures/loginTest';
import {DataApiHelpers} from '../../../helpers/ApiHelpers';
import getRandomString from '../../../utils/getRandomString';
import {isImageLoaded} from '../../../utils/isImageLoaded';
import {openFieldset} from '../../../utils/openFieldset';
import {PORTLET_URLS} from '../../../utils/portletUrls';
import getBasicWebContentStructureId from '../../../utils/structured-content/getBasicWebContentStructureId';
import {blogsPagesTest} from '../../blogs-web/main/fixtures/blogsPagesTest';
import getPageDefinition from '../../layout-content-page-editor-web/main/utils/getPageDefinition';
import getWidgetDefinition from '../../layout-content-page-editor-web/main/utils/getWidgetDefinition';
import addConnectedDepot from './utils/addConnectedDepot';
import gotoDepotInItemSelector from './utils/gotoDepotInItemSelector';

const test = mergeTests(
	blogsPagesTest,
	dataApiHelpersTest,
	featureFlagsTest({
		'LPS-178052': {enabled: true},
	}),
	isolatedSiteTest,
	loginTest()
);

const BLOGS_ADMIN_NAMESPACE =
	'_com_liferay_blogs_web_portlet_BlogsAdminPortlet_';

const DOCUMENT_PATH = path.join(__dirname, 'dependencies/Document.jpg');

async function addBlogsWidgetPage(apiHelpers: DataApiHelpers, site: Site) {
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

	return `/web${site.friendlyUrlPath}${layout.friendlyUrlPath}`;
}

function getEditBlogEntryURL(site: Site, blogId: number) {
	return `/group${site.friendlyUrlPath}${PORTLET_URLS.blogs}&${BLOGS_ADMIN_NAMESPACE}mvcRenderCommandName=/blogs/edit_entry&${BLOGS_ADMIN_NAMESPACE}entryId=${blogId}`;
}

async function expectImageLoaded(image: Locator) {
	await expect(image).toBeVisible();

	expect(await isImageLoaded(image)).toBe(true);
}

async function selectDepotItem(
	itemSelector: FrameLocator,
	depotName: string,
	itemTitle: string
) {
	await itemSelector
		.getByRole('link', {exact: true, name: 'Documents and Media'})
		.click();

	await gotoDepotInItemSelector(itemSelector, depotName);

	await itemSelector.getByText(itemTitle, {exact: true}).click();
}

test(
	'Depot images and assets can be the cover image and related assets of a connected site blog entry',
	{tag: '@LPD-109885'},
	async ({apiHelpers, blogsEditBlogEntryPage, page, site}) => {
		const assetLibrary = await addConnectedDepot(apiHelpers, site);

		const documentTitle = getRandomString();

		await apiHelpers.headlessDelivery.postDocument(
			assetLibrary.siteId,
			createReadStream(DOCUMENT_PATH),
			{fileName: `${documentTitle}.jpg`, title: documentTitle}
		);

		const webContentTitle = getRandomString();

		await apiHelpers.headlessDelivery.postStructuredContent({
			contentStructureId: await getBasicWebContentStructureId(apiHelpers),
			datePublished: null,
			siteId: assetLibrary.siteId,
			title: webContentTitle,
			viewableBy: 'Anyone',
		});

		const blogTitle = getRandomString();

		const blog = await apiHelpers.headlessDelivery.postBlog(site.id, {
			headline: blogTitle,
		});

		await test.step('Select the depot cover image and related assets', async () => {
			await page.goto(getEditBlogEntryURL(site, blog.id));

			await page
				.getByRole('button', {name: 'Select File'})
				.first()
				.click();

			await selectDepotItem(
				page.frameLocator('iframe[title="Select File"]'),
				assetLibrary.name,
				documentTitle
			);

			await openFieldset(page, 'Related Assets');

			const modal = page.locator('.modal-dialog');

			await expect(async () => {
				await page.getByLabel('Select Items').click();

				await expect(
					modal.getByLabel(`Select ${webContentTitle}`)
				).toBeVisible({timeout: 3000});
			}).toPass();

			await modal.getByLabel(`Select ${webContentTitle}`).check();
			await modal.getByLabel(`Select ${documentTitle}`).check();

			await modal
				.getByRole('button', {exact: true, name: 'Select'})
				.click();

			await blogsEditBlogEntryPage.publishBlogEntry();
		});

		await page.goto(await addBlogsWidgetPage(apiHelpers, site));

		await page.getByRole('link', {exact: true, name: blogTitle}).click();

		const coverImage = page.locator('.cover-image');

		await expect(coverImage).toBeVisible();

		const coverImageURL = (await coverImage.getAttribute('style')).match(
			/url\(['"]?(.*?)['"]?\)/
		)[1];

		const coverImageResponse = await page.request.get(coverImageURL);

		expect(coverImageResponse.status()).toBe(200);
		expect(coverImageResponse.headers()['content-type']).toContain(
			'image/'
		);

		await expect(
			page.getByRole('link', {exact: true, name: webContentTitle})
		).toBeVisible();
		await expect(
			page.getByRole('link', {exact: true, name: documentTitle})
		).toBeVisible();
	}
);

test(
	'Depot images can be the inline and small images of a connected site blog entry after they move to a folder',
	{tag: '@LPD-109885'},
	async ({apiHelpers, blogsEditBlogEntryPage, page, site}) => {
		const assetLibrary = await addConnectedDepot(apiHelpers, site);

		const documentTitle = getRandomString();

		const document = await apiHelpers.headlessDelivery.postDocument(
			assetLibrary.siteId,
			createReadStream(DOCUMENT_PATH),
			{fileName: `${documentTitle}.jpg`, title: documentTitle}
		);

		const blogTitle = getRandomString();

		const blog = await apiHelpers.headlessDelivery.postBlog(site.id, {
			headline: blogTitle,
		});

		await test.step('Add the depot image inline and as the small image', async () => {
			await page.goto(getEditBlogEntryURL(site, blog.id));

			await blogsEditBlogEntryPage.contentEditor.click();

			await page.getByRole('button', {name: 'Add'}).click();

			await page.getByRole('button', {name: 'Insert Image'}).click();

			await selectDepotItem(
				page.frameLocator('iframe[title="Select Item"]'),
				assetLibrary.name,
				documentTitle
			);

			await expect(
				blogsEditBlogEntryPage.contentEditor.locator('img')
			).toBeVisible();

			const configurationPanel =
				await blogsEditBlogEntryPage.expandPanel('Configuration');

			await configurationPanel
				.getByRole('button', {name: 'Select File'})
				.click();

			await selectDepotItem(
				page.frameLocator('iframe[title="Select File"]'),
				assetLibrary.name,
				documentTitle
			);

			await blogsEditBlogEntryPage.publishBlogEntry();
		});

		const folder = await apiHelpers.headlessDelivery.postDocumentFolder(
			assetLibrary.siteId
		);

		await apiHelpers.jsonWebServicesDocumentLibrary.moveFileEntry(
			document.id,
			folder.id
		);

		await page.goto(await addBlogsWidgetPage(apiHelpers, site));

		await expectImageLoaded(page.locator('img.asset-small-image'));

		await page.getByRole('link', {exact: true, name: blogTitle}).click();

		await expectImageLoaded(page.locator('.widget-mode-detail-text img'));
	}
);
