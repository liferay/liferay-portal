/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';
import {createReadStream} from 'fs';
import path from 'path';

import {dataApiHelpersTest} from '../../../fixtures/dataApiHelpersTest';
import {isolatedSiteTest} from '../../../fixtures/isolatedSiteTest';
import {loginTest} from '../../../fixtures/loginTest';
import {pageEditorPagesTest} from '../../../fixtures/pageEditorPagesTest';
import getRandomString from '../../../utils/getRandomString';
import addApprovedStructuredContent from '../../../utils/structured-content/addApprovedStructuredContent';
import getBasicWebContentStructureId from '../../../utils/structured-content/getBasicWebContentStructureId';
import {waitForPageToBeLoaded} from '../../../utils/waitForPageToBeLoaded';
import {journalPagesTest} from '../../journal-web/main/fixtures/journalPagesTest';
import getDataStructureDefinition from '../../journal-web/main/utils/getDataStructureDefinition';
import {contentDashboardPagesTest} from './fixtures/contentDashboardPagesTest';
import getDocumentFile from './utils/getDocumentFile';

export const test = mergeTests(
	contentDashboardPagesTest,
	dataApiHelpersTest,
	isolatedSiteTest,
	journalPagesTest,
	loginTest(),
	pageEditorPagesTest
);

test(
	'Info panel shows the details of a web content',
	{tag: ['@LPS-114912', '@LPS-116840']},
	async ({
		apiHelpers,
		contentDashboardPage,
		displayPageTemplatesPage,
		page,
		pageEditorPage,
		site,
	}) => {
		await displayPageTemplatesPage.createDefaultTemplate({
			className: 'com.liferay.journal.model.JournalArticle',
			classTypeKey: 'BASIC-WEB-CONTENT',
			pageEditorPage,
			site,
		});

		const basicWebContentTitle = getRandomString();

		await addApprovedStructuredContent({
			apiHelpers,
			contentStructureId: await getBasicWebContentStructureId(apiHelpers),
			siteId: site.id,
			title: basicWebContentTitle,
		});

		const structureName = getRandomString();

		const structure = await apiHelpers.dataEngine.createStructure(
			site.id,
			getDataStructureDefinition({
				defaultLanguageId: 'en_US',
				fields: [{fieldType: 'text', name: 'Text'}],
				name: structureName,
			})
		);

		const structureWebContentTitle = getRandomString();

		await addApprovedStructuredContent({
			apiHelpers,
			contentStructureId: Number(structure.id),
			siteId: site.id,
			title: structureWebContentTitle,
		});

		await contentDashboardPage.goto(undefined, {scopeId: site.id});

		await test.step('IconInfo, WebContentInfoPanel', async () => {
			await contentDashboardPage.getRow(basicWebContentTitle).hover();

			await expect(
				contentDashboardPage.getQuickAction(
					basicWebContentTitle,
					'View'
				)
			).toBeVisible();

			await contentDashboardPage.clickQuickAction(
				basicWebContentTitle,
				'Info'
			);

			await expect(contentDashboardPage.infoPanel).toBeVisible();

			await expect(contentDashboardPage.infoPanelTitle).toHaveText(
				basicWebContentTitle
			);

			await expect(
				page.getByRole('button', {exact: true, name: 'Share'})
			).toHaveCount(0);
		});

		await test.step('WebContentInfoPanel', async () => {
			await contentDashboardPage.goto(undefined, {scopeId: site.id});

			await contentDashboardPage.openInfoPanel(structureWebContentTitle);

			await expect(contentDashboardPage.infoPanelAssetType).toHaveText(
				`Web Content Article - ${structureName}`
			);
		});
	}
);

test(
	'Info panel shows the details, the categorization and the preview of a blog',
	{tag: ['@LPS-132510', '@LPS-152905']},
	async ({apiHelpers, contentDashboardPage, site}) => {
		const vocabularies = [];

		for (const visibilityType of ['INTERNAL', 'PUBLIC']) {
			const vocabulary =
				await apiHelpers.headlessAdminTaxonomy.postSiteTaxonomyVocabulary(
					{name: getRandomString(), siteId: site.id, visibilityType}
				);

			const categoryName = getRandomString();

			const category =
				await apiHelpers.headlessAdminTaxonomy.postTaxonomyVocabularyTaxonomyCategory(
					{name: categoryName, vocabularyId: vocabulary.id}
				);

			vocabularies.push({
				categoryId: category.id,
				categoryName,
				vocabularyName: vocabulary.name,
			});
		}

		const tag = getRandomString();

		const blogTitle = getRandomString();

		await apiHelpers.headlessDelivery.postBlog(site.id, {
			headline: blogTitle,
			keywords: [tag],
			taxonomyCategoryIds: vocabularies.map(({categoryId}) => categoryId),
		});

		const blogPostingImage =
			await apiHelpers.headlessDelivery.postSiteBlogPostingImage(
				site.id,
				createReadStream(
					path.join(__dirname, 'dependencies/image1.jpeg')
				),
				'image1.jpeg'
			);

		const imageBlogTitle = getRandomString();

		await apiHelpers.headlessDelivery.postBlog(site.id, {
			headline: imageBlogTitle,
			image: {imageId: blogPostingImage.id},
		});

		await contentDashboardPage.goto(undefined, {scopeId: site.id});

		await test.step('BlogInfoPanel', async () => {
			await contentDashboardPage.openInfoPanel(blogTitle);

			const infoPanelHeader =
				contentDashboardPage.infoPanel.locator('.sidebar-header');

			await expect(
				infoPanelHeader.getByText('Test Test', {exact: true})
			).toBeVisible();

			await expect(
				infoPanelHeader.locator('.sticker-circle')
			).toBeVisible();

			await expect(
				infoPanelHeader.getByText('Approved', {exact: true})
			).toBeVisible();

			await expect(contentDashboardPage.infoPanelAssetType).toHaveText(
				'Blogs Entry'
			);

			await expect(
				infoPanelHeader.getByText('Version 1.0', {exact: true})
			).toBeVisible();

			for (const label of [
				'Creation Date',
				'Display Date',
				'ID',
				'Modified Date',
			]) {
				await expect(
					contentDashboardPage.infoPanel.getByText(label, {
						exact: true,
					})
				).toBeVisible();
			}

			await expect(
				contentDashboardPage.infoPanel.getByRole('button', {
					exact: true,
					name: 'Share',
				})
			).toBeVisible();
		});

		await test.step('BlogCategorizationInInfoPanel', async () => {
			await contentDashboardPage.openInfoPanelTab('Categorization');

			for (const text of [
				'Internal Categories',
				'Public Categories',
				'Tags',
				tag,
				...vocabularies.flatMap(({categoryName, vocabularyName}) => [
					categoryName,
					`${vocabularyName} (${site.name})`,
				]),
			]) {
				await expect(
					contentDashboardPage.infoPanel.getByText(text, {
						exact: true,
					})
				).toBeVisible();
			}
		});

		await test.step('PreviewBlogsImage', async () => {
			await contentDashboardPage.goto(undefined, {scopeId: site.id});

			await contentDashboardPage.openInfoPanel(imageBlogTitle);

			await expect(
				contentDashboardPage.infoPanel.locator(
					'.document-preview-figure img'
				)
			).toHaveAttribute('src', /image1/);
		});
	}
);

test(
	'Info panel shows the details of a document',
	{tag: '@LPS-119966'},
	async ({apiHelpers, contentDashboardPage, site}) => {
		const documentTitle = getRandomString();

		await apiHelpers.headlessDelivery.postDocument(
			site.id,
			getDocumentFile(`${documentTitle}.txt`),
			{fileName: `${documentTitle}.txt`, title: documentTitle}
		);

		const documentTypeName = getRandomString();

		await apiHelpers.headlessDelivery.postSiteDocumentDataDefinitionType(
			site.id,
			documentTypeName
		);

		const typedDocumentTitle = getRandomString();

		await apiHelpers.headlessDelivery.postDocument(
			site.id,
			getDocumentFile(`${typedDocumentTitle}.txt`),
			{
				documentType: {name: documentTypeName},
				fileName: `${typedDocumentTitle}.txt`,
				title: typedDocumentTitle,
			}
		);

		await contentDashboardPage.goto(undefined, {scopeId: site.id});

		await test.step('DocumentInfoPanel', async () => {
			await contentDashboardPage.openInfoPanel(documentTitle);

			for (const label of ['Latest Version URL', 'WebDAV URL']) {
				await expect(
					contentDashboardPage.infoPanel.getByText(label, {
						exact: true,
					})
				).toBeVisible();
			}

			await contentDashboardPage.goto(undefined, {scopeId: site.id});

			await contentDashboardPage.openInfoPanel(typedDocumentTitle);

			await expect(contentDashboardPage.infoPanelAssetType).toHaveText(
				`Document - ${documentTypeName}`
			);
		});
	}
);

test(
	'Info panel is closed after a navigation, a refresh or a back from the version screens',
	{tag: ['@LPS-160218', '@LPS-161544', '@LPS-162826']},
	async ({apiHelpers, contentDashboardPage, page, site}) => {
		const webContentTitle = getRandomString();

		let webContent = await apiHelpers.jsonWebServicesJournal.addWebContent({
			ddmStructureId: await getBasicWebContentStructureId(apiHelpers),
			groupId: site.id,
			titleMap: {en_US: webContentTitle},
		});

		for (let i = 0; i < 11; i++) {
			webContent = await apiHelpers.jsonWebServicesJournal.editWebContent(
				{content: getRandomString(), title: webContentTitle},
				site.id,
				webContent
			);
		}

		const documentTitle = getRandomString();

		const document = await apiHelpers.headlessDelivery.postDocument(
			site.id,
			getDocumentFile(`${documentTitle}.txt`),
			{fileName: `${documentTitle}.txt`, title: documentTitle}
		);

		for (let i = 0; i < 10; i++) {
			await apiHelpers.headlessDelivery.patchDocument({
				document: {description: String(i)},
				documentId: document.id,
			});
		}

		await test.step('InfoPanelClosedAfterNavigationOrRefresh', async () => {
			await contentDashboardPage.goto(undefined, {scopeId: site.id});

			await contentDashboardPage.openInfoPanel(webContentTitle);

			await page.goto(`/web${site.friendlyUrlPath}`);

			await contentDashboardPage.goto(undefined, {scopeId: site.id});

			await expect(
				contentDashboardPage.getRow(webContentTitle)
			).toBeVisible();

			await expect(contentDashboardPage.infoPanel).toHaveCount(0);

			await contentDashboardPage.openInfoPanel(webContentTitle);

			await page.reload();

			await expect(
				contentDashboardPage.getRow(webContentTitle)
			).toBeVisible();

			await expect(contentDashboardPage.infoPanel).toHaveCount(0);
		});

		await test.step('BackClosesInfoPanel', async () => {
			for (const {actionName, landing, title, version} of [
				{
					actionName: 'Compare to...',
					landing: page.getByText(
						'You are comparing these versions:'
					),
					title: webContentTitle,
					version: '2.0',
				},
				{
					actionName: 'View',
					landing: page.getByRole('heading', {
						name: `${documentTitle} (Version 1.10)`,
					}),
					title: documentTitle,
					version: '1.10',
				},
			]) {
				await contentDashboardPage.goto(undefined, {scopeId: site.id});

				await contentDashboardPage.openInfoPanel(title);

				await contentDashboardPage.openInfoPanelTab('Versions');

				await contentDashboardPage.clickVersionAction(
					version,
					actionName
				);

				await expect(landing).toBeVisible();

				await waitForPageToBeLoaded(page);

				await page.goBack();

				await expect(contentDashboardPage.getRow(title)).toBeVisible();

				await expect(contentDashboardPage.infoPanel).toHaveCount(0);
			}
		});

		await test.step('BackClosesInfoPanel, ViewMoreWCVersionsBackButton', async () => {
			for (const title of [documentTitle, webContentTitle]) {
				await contentDashboardPage.goto(undefined, {scopeId: site.id});

				await contentDashboardPage.openInfoPanel(title);

				await contentDashboardPage.openInfoPanelTab('Versions');

				await contentDashboardPage.viewMoreVersionsLink.click();

				await expect(
					page
						.getByLabel('Control Menu')
						.getByRole('heading', {name: title})
				).toBeVisible();

				await waitForPageToBeLoaded(page);

				await page.goBack();

				await expect(
					page
						.getByLabel('Control Menu')
						.getByRole('heading', {name: 'Content Dashboard'})
				).toBeVisible();

				await expect(contentDashboardPage.infoPanel).toHaveCount(0);
			}
		});
	}
);
