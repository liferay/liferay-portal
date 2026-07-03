/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';
import {createReadStream} from 'fs';
import path from 'path';

import {dataApiHelpersTest} from '../../../fixtures/dataApiHelpersTest';
import {displayPageTemplatesPagesTest} from '../../../fixtures/displayPageTemplatesPagesTest';
import {featureFlagsTest} from '../../../fixtures/featureFlagsTest';
import {isolatedSiteTest} from '../../../fixtures/isolatedSiteTest';
import {loginTest} from '../../../fixtures/loginTest';
import {pageEditorPagesTest} from '../../../fixtures/pageEditorPagesTest';
import {createCategories} from '../../../helpers/CreateCategories';
import getGlobalSiteId from '../../../utils/getGlobalSiteId';
import getRandomString from '../../../utils/getRandomString';
import {enableLocalStaging} from '../../../utils/staging';
import getBasicWebContentStructureId from '../../../utils/structured-content/getBasicWebContentStructureId';
import getPageDefinition from '../../layout-content-page-editor-web/main/utils/getPageDefinition';
import getWidgetDefinition from '../../layout-content-page-editor-web/main/utils/getWidgetDefinition';

const test = mergeTests(
	dataApiHelpersTest,
	displayPageTemplatesPagesTest,
	featureFlagsTest({
		'LPD-39304': {enabled: true},
		'LPS-178052': {enabled: true},
	}),
	isolatedSiteTest,
	loginTest(),
	pageEditorPagesTest
);

const ASSET_PUBLISHER_WIDGET =
	'com_liferay_asset_publisher_web_portlet_AssetPublisherPortlet';
const RELATED_ASSETS_WIDGET =
	'com_liferay_asset_publisher_web_portlet_RelatedAssetsPortlet';
const WEB_CONTENT_DISPLAY_WIDGET =
	'com_liferay_journal_content_web_portlet_JournalContentPortlet';

test(
	'Display all related assets on a content page',
	{tag: '@LPS-155247'},
	async ({apiHelpers, page, site}) => {

		// Create a blog and a web content that relates to it

		const blogHeadline = getRandomString();

		const blog = await apiHelpers.headlessDelivery.postBlog(site.id, {
			articleBody: getRandomString(),
			headline: blogHeadline,
		});

		const contentStructureId =
			await getBasicWebContentStructureId(apiHelpers);

		const webContent =
			await apiHelpers.headlessDelivery.postStructuredContent({
				contentStructureId,
				datePublished: null,
				relatedContents: [
					{
						contentType: 'BlogPosting',
						id: blog.id,
						title: blogHeadline,
					},
				],
				siteId: site.id,
				title: getRandomString(),
				viewableBy: 'Anyone',
			});

		// Create a content page with an Asset Publisher, a Web Content Display
		// bound to the web content, and a Related Assets widget

		const layout = await apiHelpers.headlessDelivery.createSitePage({
			pageDefinition: getPageDefinition([
				getWidgetDefinition({
					id: getRandomString(),
					widgetName: ASSET_PUBLISHER_WIDGET,
				}),
				getWidgetDefinition({
					id: getRandomString(),
					widgetConfig: {
						articleId: webContent.key,
						groupId: String(site.id),
					},
					widgetName: WEB_CONTENT_DISPLAY_WIDGET,
				}),
				getWidgetDefinition({
					id: getRandomString(),
					widgetName: RELATED_ASSETS_WIDGET,
				}),
			]),
			siteId: site.id,
			title: getRandomString(),
		});

		// View the page: the Related Assets widget shows the blog

		await page.goto(`/web${site.friendlyUrlPath}${layout.friendlyUrlPath}`);

		const relatedAssetsWidget = page.locator(
			`section[id^="portlet_${RELATED_ASSETS_WIDGET}"]`
		);

		await expect(
			relatedAssetsWidget.getByRole('link', {name: blogHeadline})
		).toBeVisible();

		// Viewing the blog, which has no related assets, hides the widget

		await relatedAssetsWidget
			.getByRole('link', {name: blogHeadline})
			.click();

		await expect(relatedAssetsWidget).toBeHidden();
	}
);

test(
	'Display all related assets on a display page template',
	{tag: '@LPS-155247'},
	async ({
		apiHelpers,
		displayPageTemplatesPage,
		page,
		pageEditorPage,
		site,
	}) => {
		test.setTimeout(180000);

		// Create a blog and a document, and a web content related to both

		const blogHeadline = getRandomString();

		const blog = await apiHelpers.headlessDelivery.postBlog(site.id, {
			articleBody: getRandomString(),
			headline: blogHeadline,
		});

		const documentTitle = getRandomString();

		const document = await apiHelpers.headlessDelivery.postDocument(
			site.id,
			createReadStream(path.join(__dirname, '/dependencies/image1.jpeg')),
			{title: documentTitle}
		);

		const contentStructureId =
			await getBasicWebContentStructureId(apiHelpers);

		const webContentTitle = getRandomString();

		await apiHelpers.headlessDelivery.postStructuredContent({
			contentStructureId,
			datePublished: null,
			relatedContents: [
				{contentType: 'BlogPosting', id: blog.id, title: blogHeadline},
				{
					contentType: 'Document',
					id: document.id,
					title: documentTitle,
				},
			],
			siteId: site.id,
			title: webContentTitle,
			viewableBy: 'Anyone',
		});

		// Create a default display page template for Basic Web Content holding a
		// Related Assets widget

		const displayPageTemplateName = getRandomString();

		await displayPageTemplatesPage.goto(site.friendlyUrlPath);

		await displayPageTemplatesPage.createTemplate({
			contentSubtype: 'Basic Web Content',
			contentType: 'Web Content Article',
			name: displayPageTemplateName,
		});

		await displayPageTemplatesPage.editTemplate(displayPageTemplateName);

		await pageEditorPage.addWidget('Content Management', 'Related Assets');

		await pageEditorPage.publishPage();

		await displayPageTemplatesPage.goto(site.friendlyUrlPath);

		await displayPageTemplatesPage.markAsDefault(displayPageTemplateName);

		// The Related Assets widget on the web content's display page shows both
		// assets related to the web content

		const relatedAssetsWidget = page.locator(
			`section[id^="portlet_${RELATED_ASSETS_WIDGET}"]`
		);

		await page.goto(`/web${site.friendlyUrlPath}/w/${webContentTitle}`);

		await expect(
			relatedAssetsWidget.getByRole('link', {name: blogHeadline})
		).toBeVisible();

		await expect(
			relatedAssetsWidget.getByRole('link', {name: documentTitle})
		).toBeVisible();
	}
);

test(
	'Do not show folders as related assets',
	{tag: '@LPD-96964'},
	async ({
		apiHelpers,
		displayPageTemplatesPage,
		page,
		pageEditorPage,
		site,
	}) => {
		test.setTimeout(180000);

		// Create a blog related to a web content, plus a document folder and a
		// structured content folder that are not related to anything

		const blogHeadline = getRandomString();

		const blog = await apiHelpers.headlessDelivery.postBlog(site.id, {
			articleBody: getRandomString(),
			headline: blogHeadline,
		});

		const documentFolderName = getRandomString();

		await apiHelpers.headlessDelivery.postDocumentFolder(site.id, {
			name: documentFolderName,
		});

		const structuredContentFolderName = getRandomString();

		await apiHelpers.headlessDelivery.postStructuredContentFolder(site.id, {
			name: structuredContentFolderName,
		});

		const contentStructureId =
			await getBasicWebContentStructureId(apiHelpers);

		const webContentTitle = getRandomString();

		await apiHelpers.headlessDelivery.postStructuredContent({
			contentStructureId,
			datePublished: null,
			relatedContents: [
				{contentType: 'BlogPosting', id: blog.id, title: blogHeadline},
			],
			siteId: site.id,
			title: webContentTitle,
			viewableBy: 'Anyone',
		});

		// Create a default display page template for Basic Web Content holding a
		// Related Assets widget

		const displayPageTemplateName = getRandomString();

		await displayPageTemplatesPage.goto(site.friendlyUrlPath);

		await displayPageTemplatesPage.createTemplate({
			contentSubtype: 'Basic Web Content',
			contentType: 'Web Content Article',
			name: displayPageTemplateName,
		});

		await displayPageTemplatesPage.editTemplate(displayPageTemplateName);

		await pageEditorPage.addWidget('Content Management', 'Related Assets');

		await pageEditorPage.publishPage();

		await displayPageTemplatesPage.goto(site.friendlyUrlPath);

		await displayPageTemplatesPage.markAsDefault(displayPageTemplateName);

		// The Related Assets widget on the web content's display page shows the
		// related blog but never the folders

		const relatedAssetsWidget = page.locator(
			`section[id^="portlet_${RELATED_ASSETS_WIDGET}"]`
		);

		await page.goto(`/web${site.friendlyUrlPath}/w/${webContentTitle}`);

		await expect(
			relatedAssetsWidget.getByRole('link', {name: blogHeadline})
		).toBeVisible();

		await expect(
			relatedAssetsWidget.getByText(documentFolderName)
		).toBeHidden();

		await expect(
			relatedAssetsWidget.getByText(structuredContentFolderName)
		).toBeHidden();
	}
);

test(
	'Show related assets bidirectionally in the Related Assets widget',
	{tag: '@LPD-96964'},
	async ({
		apiHelpers,
		displayPageTemplatesPage,
		page,
		pageEditorPage,
		site,
	}) => {
		test.setTimeout(240000);

		// Create a blog and a web content related to it (relations are
		// bidirectional)

		const blogHeadline = getRandomString();

		const blog = await apiHelpers.headlessDelivery.postBlog(site.id, {
			articleBody: getRandomString(),
			headline: blogHeadline,
		});

		const contentStructureId =
			await getBasicWebContentStructureId(apiHelpers);

		const webContentTitle = getRandomString();

		await apiHelpers.headlessDelivery.postStructuredContent({
			contentStructureId,
			datePublished: null,
			relatedContents: [
				{contentType: 'BlogPosting', id: blog.id, title: blogHeadline},
			],
			siteId: site.id,
			title: webContentTitle,
			viewableBy: 'Anyone',
		});

		// Create default display page templates holding a Related Assets widget
		// for both Basic Web Content and Blogs Entry

		const createDefaultRelatedAssetsTemplate = async (
			contentType: string,
			contentSubtype?: string
		) => {
			const displayPageTemplateName = getRandomString();

			await displayPageTemplatesPage.goto(site.friendlyUrlPath);

			await displayPageTemplatesPage.createTemplate({
				contentSubtype,
				contentType,
				name: displayPageTemplateName,
			});

			await displayPageTemplatesPage.editTemplate(
				displayPageTemplateName
			);

			await pageEditorPage.addWidget(
				'Content Management',
				'Related Assets'
			);

			await pageEditorPage.publishPage();

			await displayPageTemplatesPage.goto(site.friendlyUrlPath);

			await displayPageTemplatesPage.markAsDefault(
				displayPageTemplateName
			);
		};

		await createDefaultRelatedAssetsTemplate(
			'Web Content Article',
			'Basic Web Content'
		);

		await createDefaultRelatedAssetsTemplate('Blogs Entry');

		const relatedAssetsWidget = page.locator(
			`section[id^="portlet_${RELATED_ASSETS_WIDGET}"]`
		);

		// The web content's display page shows the blog as its related asset

		await page.goto(`/web${site.friendlyUrlPath}/w/${webContentTitle}`);

		await expect(
			relatedAssetsWidget.getByRole('link', {name: blogHeadline})
		).toBeVisible();

		// The blog's display page shows the web content as its related asset

		await page.goto(`/web${site.friendlyUrlPath}/b/${blogHeadline}`);

		await expect(
			relatedAssetsWidget.getByRole('link', {name: webContentTitle})
		).toBeVisible();
	}
);

test(
	'Show only related assets that match the Related Assets widget category filter',
	{tag: '@LPD-96964'},
	async ({apiHelpers, page, site}) => {
		test.setTimeout(180000);

		// Create a vocabulary with a category

		const categoryName = getRandomString();

		const [category] = await createCategories({
			apiHelpers,
			categoryNames: [{name: categoryName}],
			siteId: String(site.id),
			vocabularyName: getRandomString(),
		});

		const contentStructureId =
			await getBasicWebContentStructureId(apiHelpers);

		// Create a web content that carries the category and another that does
		// not

		const categorizedTitle = getRandomString();

		const categorizedWebContent =
			await apiHelpers.headlessDelivery.postStructuredContent({
				categoryIds: [category.id],
				contentStructureId,
				datePublished: null,
				siteId: site.id,
				title: categorizedTitle,
				viewableBy: 'Anyone',
			});

		const uncategorizedTitle = getRandomString();

		const uncategorizedWebContent =
			await apiHelpers.headlessDelivery.postStructuredContent({
				contentStructureId,
				datePublished: null,
				siteId: site.id,
				title: uncategorizedTitle,
				viewableBy: 'Anyone',
			});

		// Create a web content related to both

		const webContentTitle = getRandomString();

		const webContent =
			await apiHelpers.headlessDelivery.postStructuredContent({
				contentStructureId,
				datePublished: null,
				relatedContents: [
					{
						contentType: 'StructuredContent',
						id: categorizedWebContent.id,
						title: categorizedTitle,
					},
					{
						contentType: 'StructuredContent',
						id: uncategorizedWebContent.id,
						title: uncategorizedTitle,
					},
				],
				siteId: site.id,
				title: webContentTitle,
				viewableBy: 'Anyone',
			});

		// Create a content page with a Web Content Display bound to the web
		// content and a Related Assets widget filtered by the category

		const layout = await apiHelpers.headlessDelivery.createSitePage({
			pageDefinition: getPageDefinition([
				getWidgetDefinition({
					id: getRandomString(),
					widgetConfig: {
						articleId: webContent.key,
						groupId: String(site.id),
					},
					widgetName: WEB_CONTENT_DISPLAY_WIDGET,
				}),
				getWidgetDefinition({
					id: getRandomString(),
					widgetConfig: {
						queryAndOperator0: 'false',
						queryContains0: 'true',
						queryName0: 'assetCategories',
						queryValues0: [String(category.id)],
					},
					widgetName: RELATED_ASSETS_WIDGET,
				}),
			]),
			siteId: site.id,
			title: getRandomString(),
		});

		// The widget shows the categorized related asset but not the
		// uncategorized one

		await page.goto(`/web${site.friendlyUrlPath}${layout.friendlyUrlPath}`);

		const relatedAssetsWidget = page.locator(
			`section[id^="portlet_${RELATED_ASSETS_WIDGET}"]`
		);

		await expect(
			relatedAssetsWidget.getByRole('link', {name: categorizedTitle})
		).toBeVisible();

		await expect(
			relatedAssetsWidget.getByRole('link', {name: uncategorizedTitle})
		).toBeHidden();
	}
);

test(
	'Show a related asset from the global site after publishing to live',
	{tag: '@LPS-72472'},
	async ({apiHelpers, page, site}) => {
		test.setTimeout(240000);

		// Enable local staging on the site

		await enableLocalStaging(apiHelpers, page, site);

		const stagingSite =
			await apiHelpers.headlessAdminUser.getSiteByFriendlyUrlPath(
				`${site.friendlyUrlPath}-staging`
			);

		const contentStructureId =
			await getBasicWebContentStructureId(apiHelpers);

		// Create a web content in the global site

		const globalSiteId = await getGlobalSiteId(apiHelpers);

		const globalWebContentTitle = getRandomString();

		const globalWebContent =
			await apiHelpers.headlessDelivery.postStructuredContent({
				contentStructureId,
				datePublished: null,
				siteId: globalSiteId,
				title: globalWebContentTitle,
				viewableBy: 'Anyone',
			});

		// The global web content is outside the isolated site, so track it for
		// cleanup

		apiHelpers.data.push({
			id: `${globalSiteId}_${globalWebContent.key}`,
			type: 'webContent',
		});

		// Create a web content in the staging site related to the global web
		// content

		const webContentTitle = getRandomString();

		const webContent =
			await apiHelpers.headlessDelivery.postStructuredContent({
				contentStructureId,
				datePublished: null,
				relatedContents: [
					{
						contentType: 'StructuredContent',
						id: globalWebContent.id,
						title: globalWebContentTitle,
					},
				],
				siteId: stagingSite.id,
				title: webContentTitle,
				viewableBy: 'Anyone',
			});

		// Create a content page in the staging site with a Web Content Display
		// bound to the web content and a Related Assets widget

		const layout = await apiHelpers.headlessDelivery.createSitePage({
			pageDefinition: getPageDefinition([
				getWidgetDefinition({
					id: getRandomString(),
					widgetConfig: {
						articleId: webContent.key,
						groupId: String(stagingSite.id),
					},
					widgetName: WEB_CONTENT_DISPLAY_WIDGET,
				}),
				getWidgetDefinition({
					id: getRandomString(),
					widgetConfig: {
						scopeIds: [
							`Group_${stagingSite.id}`,
							`Group_${globalSiteId}`,
						],
					},
					widgetName: RELATED_ASSETS_WIDGET,
				}),
			]),
			siteId: stagingSite.id,
			title: getRandomString(),
		});

		const relatedAssetsWidget = page.locator(
			`section[id^="portlet_${RELATED_ASSETS_WIDGET}"]`
		);

		// The staging page shows the global web content as a related asset

		await page.goto(
			`/web${stagingSite.friendlyUrlPath}${layout.friendlyUrlPath}`
		);

		await expect(
			relatedAssetsWidget.getByRole('link', {name: globalWebContentTitle})
		).toBeVisible();

		// Publish the staging site to live

		await page.goto(`/web${stagingSite.friendlyUrlPath}`);

		await page.getByText('Publish to Live').click();

		const publishToLiveFrame = page.frameLocator(
			'iframe[title="Publish to Live"]'
		);

		await publishToLiveFrame.getByText('Publish to Live').click();

		await expect(publishToLiveFrame.getByText('Successful')).toBeVisible({
			timeout: 120000,
		});

		// The live page still shows the global web content as a related asset

		await page.goto(`/web${site.friendlyUrlPath}${layout.friendlyUrlPath}`);

		await expect(
			relatedAssetsWidget.getByRole('link', {name: globalWebContentTitle})
		).toBeVisible();
	}
);
