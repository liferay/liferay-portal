/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Page, expect, mergeTests} from '@playwright/test';
import {createReadStream} from 'fs';

import {dataApiHelpersTest} from '../../../fixtures/dataApiHelpersTest';
import {featureFlagsTest} from '../../../fixtures/featureFlagsTest';
import {isolatedSiteTest} from '../../../fixtures/isolatedSiteTest';
import {loginTest} from '../../../fixtures/loginTest';
import {DataApiHelpers} from '../../../helpers/ApiHelpers';
import {DocumentLibraryEditFilePage} from '../../../pages/document-library-web/DocumentLibraryEditFilePage';
import {DocumentLibraryPage} from '../../../pages/document-library-web/DocumentLibraryPage';
import {DocumentLibraryViewFileEntryPage} from '../../../pages/document-library-web/DocumentLibraryViewFileEntryPage';
import {clickAndExpectToBeVisible} from '../../../utils/clickAndExpectToBeVisible';
import createTempFile from '../../../utils/createTempFile';
import getRandomString from '../../../utils/getRandomString';
import getBasicWebContentStructureId from '../../../utils/structured-content/getBasicWebContentStructureId';
import {JournalEditArticlePage} from '../../journal-web/main/pages/JournalEditArticlePage';
import getPageDefinition from '../../layout-content-page-editor-web/main/utils/getPageDefinition';
import getWidgetDefinition from '../../layout-content-page-editor-web/main/utils/getWidgetDefinition';

const test = mergeTests(
	dataApiHelpersTest,
	featureFlagsTest({
		'LPD-39304': {enabled: true},
		'LPS-178052': {enabled: true},
	}),
	isolatedSiteTest,
	loginTest()
);

async function addConnectedDepotCategory(
	apiHelpers: DataApiHelpers,
	site: Site
) {
	const assetLibrary =
		await apiHelpers.headlessAssetLibrary.createAssetLibrary({
			name: getRandomString(),
			type: 'AssetLibrary',
		});

	const vocabulary =
		await apiHelpers.headlessAdminTaxonomy.postAssetLibraryTaxonomyVocabulary(
			{assetLibraryId: assetLibrary.id, name: getRandomString()}
		);

	const categoryName = getRandomString();

	const category =
		await apiHelpers.headlessAdminTaxonomy.postTaxonomyVocabularyTaxonomyCategory(
			{name: categoryName, vocabularyId: vocabulary.id}
		);

	await apiHelpers.headlessAssetLibrary.connectSite(
		assetLibrary.externalReferenceCode,
		site.externalReferenceCode
	);

	return {assetLibrary, category, categoryName, vocabulary};
}

async function addDocument({
	apiHelpers,
	categoryId,
	site,
	title,
}: {
	apiHelpers: DataApiHelpers;
	categoryId: number;
	site: Site;
	title: string;
}) {
	await apiHelpers.headlessDelivery.postDocument(
		site.id,
		createReadStream(createTempFile(`${getRandomString()}.txt`, title)),
		{
			fileName: `${title}.txt`,
			taxonomyCategoryIds: [categoryId],
			title,
		}
	);
}

async function addWebContent({
	apiHelpers,
	categoryId,
	site,
	title,
}: {
	apiHelpers: DataApiHelpers;
	categoryId?: number;
	site: Site;
	title: string;
}) {
	await apiHelpers.headlessDelivery.postStructuredContent({
		categoryIds: categoryId ? [categoryId] : undefined,
		contentStructureId: await getBasicWebContentStructureId(apiHelpers),
		datePublished: null,
		siteId: site.id,
		title,
		viewableBy: 'Anyone',
	});
}

async function addWidgetPage({
	apiHelpers,
	site,
	widgets,
}: {
	apiHelpers: DataApiHelpers;
	site: Site;
	widgets: {widgetConfig?: Record<string, string>; widgetName: string}[];
}) {
	const layout = await apiHelpers.headlessDelivery.createSitePage({
		pageDefinition: getPageDefinition(
			widgets.map(({widgetConfig, widgetName}) =>
				getWidgetDefinition({
					id: getRandomString(),
					widgetConfig,
					widgetName,
				})
			)
		),
		siteId: site.id,
		title: getRandomString(),
	});

	return `/web${site.friendlyUrlPath}${layout.friendlyUrlPath}`;
}

async function expectSearchResults(
	page: Page,
	pageURL: string,
	keywords: string,
	titles: string[]
) {
	await expect(async () => {
		await page.goto(`${pageURL}?q=${keywords}`);

		for (const title of titles) {
			await expect(
				page.getByRole('link', {exact: true, name: title})
			).toBeVisible({timeout: 2000});
		}
	}).toPass({timeout: 30000});
}

test(
	'A depot category finds the connected site assets in search, also after disconnecting',
	{tag: '@LPD-109572'},
	async ({apiHelpers, page, site}) => {
		const {assetLibrary, category, categoryName} =
			await addConnectedDepotCategory(apiHelpers, site);

		const categoryId = Number(category.id);

		const blogTitle = getRandomString();

		await apiHelpers.headlessDelivery.postBlog(site.id, {
			headline: blogTitle,
			taxonomyCategoryIds: [categoryId],
		});

		const documentTitle = getRandomString();

		await addDocument({apiHelpers, categoryId, site, title: documentTitle});

		const knowledgeBaseArticleTitle = getRandomString();

		await apiHelpers.headlessDelivery.postSiteKnowledgeBaseArticle({
			articleBody: getRandomString(),
			siteId: site.id,
			taxonomyCategoryIds: [categoryId],
			title: knowledgeBaseArticleTitle,
		});

		const webContentTitle = getRandomString();

		await addWebContent({
			apiHelpers,
			categoryId,
			site,
			title: webContentTitle,
		});

		const pageURL = await addWidgetPage({
			apiHelpers,
			site,
			widgets: [
				{
					widgetName:
						'com_liferay_portal_search_web_search_bar_portlet_SearchBarPortlet',
				},
				{
					widgetName:
						'com_liferay_portal_search_web_search_results_portlet_SearchResultsPortlet',
				},
			],
		});

		const titles = [
			blogTitle,
			documentTitle,
			knowledgeBaseArticleTitle,
			webContentTitle,
		];

		await expectSearchResults(page, pageURL, categoryName, titles);

		await apiHelpers.headlessAssetLibrary.disconnectSite(
			assetLibrary.externalReferenceCode,
			site.externalReferenceCode
		);

		await expectSearchResults(page, pageURL, categoryName, titles);
	}
);

test(
	'A depot category filters an Asset Publisher through Category Filter until the site is disconnected',
	{tag: '@LPD-109572'},
	async ({apiHelpers, page, site}) => {
		const {assetLibrary, category, categoryName, vocabulary} =
			await addConnectedDepotCategory(apiHelpers, site);

		const categorizedTitle = getRandomString();

		await addWebContent({
			apiHelpers,
			categoryId: Number(category.id),
			site,
			title: categorizedTitle,
		});

		const uncategorizedTitle = getRandomString();

		await addWebContent({apiHelpers, site, title: uncategorizedTitle});

		const pageURL = await addWidgetPage({
			apiHelpers,
			site,
			widgets: [
				{
					widgetConfig: {selectionStyle: 'dynamic'},
					widgetName:
						'com_liferay_asset_publisher_web_portlet_AssetPublisherPortlet',
				},
				{
					widgetName:
						'com_liferay_asset_categories_navigation_web_portlet_AssetCategoriesNavigationPortlet',
				},
			],
		});

		const categoryItem = page.getByText(categoryName, {exact: true});
		const vocabularyItem = page.getByText(vocabulary.name, {exact: true});

		await page.goto(pageURL);

		await expect(page.getByText(uncategorizedTitle)).toBeVisible();

		await clickAndExpectToBeVisible({
			target: categoryItem,
			trigger: vocabularyItem,
		});

		await categoryItem.click();

		await expect(page.getByText(categorizedTitle)).toBeVisible();
		await expect(page.getByText(uncategorizedTitle)).toHaveCount(0);

		await apiHelpers.headlessAssetLibrary.disconnectSite(
			assetLibrary.externalReferenceCode,
			site.externalReferenceCode
		);

		await page.goto(pageURL);

		await expect(page.getByText(uncategorizedTitle)).toBeVisible();
		await expect(vocabularyItem).toHaveCount(0);
	}
);

test(
	'A depot vocabulary is read only in a connected site and gone once disconnected',
	{tag: ['@LPD-109572', '@LPS-131774', '@LPS-131849']},
	async ({apiHelpers, page, site}) => {
		const {assetLibrary, category, categoryName, vocabulary} =
			await addConnectedDepotCategory(apiHelpers, site);

		const documentTitle = getRandomString();

		await addDocument({
			apiHelpers,
			categoryId: Number(category.id),
			site,
			title: documentTitle,
		});

		const documentLibraryEditFilePage = new DocumentLibraryEditFilePage(
			page
		);
		const documentLibraryPage = new DocumentLibraryPage(page);
		const documentLibraryViewFileEntryPage =
			new DocumentLibraryViewFileEntryPage(page);
		const journalEditArticlePage = new JournalEditArticlePage(page);

		await test.step('The document shows the depot category', async () => {
			await documentLibraryPage.goto(site.friendlyUrlPath);

			await documentLibraryPage.goToViewFileEntry(documentTitle);

			await documentLibraryViewFileEntryPage.openInfoPanel(
				documentTitle,
				'Details'
			);

			await expect(
				documentLibraryViewFileEntryPage.infoPanel.getByText(
					categoryName,
					{exact: true}
				)
			).toBeVisible();
		});

		await test.step('The depot vocabulary offers no new category from the site', async () => {
			await documentLibraryEditFilePage.goto(site.friendlyUrlPath);

			await page.getByRole('button', {name: 'Categorization'}).click();

			await page
				.locator('.form-group')
				.filter({hasText: vocabulary.name})
				.getByRole('button', {name: 'Select'})
				.click();

			const dialog = page.getByRole('dialog');

			await expect(dialog.getByText(categoryName)).toBeVisible();
			await expect(
				dialog.getByRole('button', {name: 'Add Category'})
			).toHaveCount(0);
		});

		await apiHelpers.headlessAssetLibrary.disconnectSite(
			assetLibrary.externalReferenceCode,
			site.externalReferenceCode
		);

		await test.step('The add forms show no depot vocabulary', async () => {
			await documentLibraryEditFilePage.goto(site.friendlyUrlPath);

			await page.getByRole('button', {name: 'Categorization'}).click();

			await expect(page.getByText(vocabulary.name)).toHaveCount(0);

			await journalEditArticlePage.goto({siteUrl: site.friendlyUrlPath});

			await expect(page.getByText(vocabulary.name)).toHaveCount(0);
		});
	}
);
