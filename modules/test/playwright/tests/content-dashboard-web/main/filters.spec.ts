/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';
import {createReadStream} from 'fs';
import path from 'path';

import {dataApiHelpersTest} from '../../../fixtures/dataApiHelpersTest';
import {globalMenuPagesTest} from '../../../fixtures/globalMenuPagesTest';
import {isolatedSiteTest} from '../../../fixtures/isolatedSiteTest';
import {loginTest} from '../../../fixtures/loginTest';
import {ApiHelpers} from '../../../helpers/ApiHelpers';
import {BLOGS_WORKFLOW_ACTION} from '../../../helpers/json-web-services/JSONWebServicesBlogsApiHelper';
import {DLFILE_STATUS} from '../../../helpers/json-web-services/JSONWebServicesDocumentLibraryApiHelper';
import {getRandomInt} from '../../../utils/getRandomInt';
import getRandomString from '../../../utils/getRandomString';
import {performLoginViaApi} from '../../../utils/performLogin';
import addApprovedStructuredContent from '../../../utils/structured-content/addApprovedStructuredContent';
import addDraftStructuredContent from '../../../utils/structured-content/addDraftStructuredContent';
import addExpiredStructuredContent from '../../../utils/structured-content/addExpiredStructuredContent';
import addScheduledStructuredContent from '../../../utils/structured-content/addScheduledStructuredContent';
import getBasicWebContentStructureId from '../../../utils/structured-content/getBasicWebContentStructureId';
import getDataStructureDefinition from '../../journal-web/main/utils/getDataStructureDefinition';
import {contentDashboardPagesTest} from './fixtures/contentDashboardPagesTest';
import addGlobalCategories from './utils/addGlobalCategories';
import addSingleApproverWorkflow from './utils/addSingleApproverWorkflow';
import addSiteUser from './utils/addSiteUser';
import getDocumentFile from './utils/getDocumentFile';

export const test = mergeTests(
	contentDashboardPagesTest,
	dataApiHelpersTest,
	globalMenuPagesTest,
	isolatedSiteTest,
	loginTest()
);

test('displays second dropdown level for Size item with Small, Medium and Large options', async ({
	contentDashboardPage,
	page,
	site,
}) => {
	await contentDashboardPage.goto(site.friendlyUrlPath);

	await contentDashboardPage.openFilterDropdown();

	const sizeMenuItem = page.getByRole('menuitem', {name: 'Size'});

	await sizeMenuItem.hover();

	for (const size of [
		'Small (up to 150 kb)',
		'Medium (from 150 kb to 1 mb)',
		'Large (from 1 mb)',
	]) {
		await expect(page.getByText(size)).toBeVisible();
	}
});

test(
	'Content Dashboard filters the assets by author',
	{
		tag: [
			'@LPS-114985',
			'@LPS-117489',
			'@LPS-120417',
			'@LPS-124960',
			'@LPS-146471',
		],
	},
	async ({
		apiHelpers,
		browser,
		contentDashboardPage,
		globalMenuPage,
		page,
		site,
	}) => {
		const [firstCategory, secondCategory] = await addGlobalCategories({
			apiHelpers,
			names: [getRandomString(), getRandomString()],
			vocabularyName: 'Audience',
		});

		const keyword = getRandomString().replace(/-/g, '');

		const adminWebContentTitle = `${keyword} ${getRandomString()}`;

		const basicWebContentStructureId =
			await getBasicWebContentStructureId(apiHelpers);

		await addApprovedStructuredContent({
			apiHelpers,
			categoryIds: [firstCategory.id, secondCategory.id],
			contentStructureId: basicWebContentStructureId,
			siteId: site.id,
			title: adminWebContentTitle,
		});

		const blogTitle = getRandomString();

		await apiHelpers.headlessDelivery.postBlog(site.id, {
			headline: blogTitle,
		});

		const author = await addSiteUser({
			apiHelpers,
			siteId: site.id,
			userAccount: {
				familyName: 'Test',
				givenName: `Author${getRandomInt()}`,
			},
		});

		const authorContext = await browser.newContext();

		const authorPage = await authorContext.newPage();

		await performLoginViaApi({
			page: authorPage,
			screenName: author.alternateName,
		});

		const authorWebContentTitle = `${keyword} ${getRandomString()}`;

		await addApprovedStructuredContent({
			apiHelpers: new ApiHelpers(authorPage),
			categoryIds: [firstCategory.id],
			contentStructureId: basicWebContentStructureId,
			siteId: site.id,
			title: authorWebContentTitle,
		});

		await authorContext.close();

		const authorName = `${author.givenName} ${author.familyName}`;

		await test.step('AssetNotFilteredByMe', async () => {
			await globalMenuPage.goToApplications('Content Dashboard');

			await contentDashboardPage.search(keyword);

			await expect(
				contentDashboardPage.getRow(adminWebContentTitle)
			).toBeVisible();

			await expect(
				contentDashboardPage.getRow(authorWebContentTitle)
			).toBeVisible();

			await expect(page.locator('.tbar-section .label-item')).toHaveCount(
				0
			);
		});

		await test.step('FilterBlogByAuthor', async () => {
			await contentDashboardPage.goto(undefined, {scopeId: site.id});

			await contentDashboardPage.filterBy('All');

			await expect(contentDashboardPage.getRow(blogTitle)).toBeVisible();

			await contentDashboardPage.filterBy('Me');

			await expect(contentDashboardPage.getRow(blogTitle)).toBeVisible();

			await expect(
				contentDashboardPage.getRow(authorWebContentTitle)
			).toHaveCount(0);

			await contentDashboardPage.goto(undefined, {scopeId: site.id});

			await contentDashboardPage.filterByAuthors({
				names: ['Test Test'],
				search: 'Test',
				view: 'Table',
			});

			await expect(contentDashboardPage.getRow(blogTitle)).toBeVisible();
		});

		await test.step('FilterByAuthor', async () => {
			for (const view of ['Table', 'List', 'Cards'] as const) {
				await contentDashboardPage.goto(undefined, {scopeId: site.id});

				await contentDashboardPage.filterByAuthors({
					names: ['Test Test'],
					search: 'Test',
					view,
				});

				await expect(
					contentDashboardPage.getAppliedFilter('Author: Test Test')
				).toBeVisible();

				await expect(
					contentDashboardPage.getRow(adminWebContentTitle)
				).toBeVisible();

				await expect(
					contentDashboardPage.getRow(authorWebContentTitle)
				).toHaveCount(0);
			}

			await contentDashboardPage.filterBy('All');

			await expect(
				contentDashboardPage.getRow(adminWebContentTitle)
			).toBeVisible();

			await expect(
				contentDashboardPage.getRow(authorWebContentTitle)
			).toBeVisible();
		});

		await test.step('ShowAllContentsCreatedByUsers', async () => {
			await contentDashboardPage.goto(undefined, {scopeId: site.id});

			await contentDashboardPage.filterByAuthors({
				names: ['Test Test', authorName],
				search: 'Test',
				view: 'Table',
			});

			await expect(
				contentDashboardPage.getRow(adminWebContentTitle)
			).toBeVisible();

			await expect(
				contentDashboardPage.getRow(authorWebContentTitle)
			).toBeVisible();

			await contentDashboardPage.goto(undefined, {scopeId: site.id});

			await contentDashboardPage.filterByAuthors({
				names: [authorName],
				search: author.givenName,
			});

			await expect(
				contentDashboardPage.getRow(authorWebContentTitle)
			).toBeVisible();

			await expect(
				contentDashboardPage.getRow(adminWebContentTitle)
			).toHaveCount(0);
		});

		await test.step('FilterByCategoryAndAuthors', async () => {
			const firstCategoryFilter = {
				categoryName: firstCategory.name,
				vocabularyLabel: 'Audience (Global)',
			};

			await contentDashboardPage.goto(undefined, {scopeId: site.id});

			await contentDashboardPage.filterByCategories([
				firstCategoryFilter,
			]);

			await contentDashboardPage.filterByAuthors({
				names: ['Test Test', authorName],
				search: 'Test',
			});

			await expect(
				contentDashboardPage.getRow(adminWebContentTitle)
			).toBeVisible();

			await expect(
				contentDashboardPage.getRow(authorWebContentTitle)
			).toBeVisible();

			for (const categoryFilters of [
				[firstCategoryFilter],
				[
					firstCategoryFilter,
					{
						categoryName: secondCategory.name,
						vocabularyLabel: 'Audience (Global)',
					},
				],
			]) {
				await contentDashboardPage.goto(undefined, {scopeId: site.id});

				await contentDashboardPage.filterByCategories(categoryFilters);

				await contentDashboardPage.filterByAuthors({
					names: ['Test Test'],
					search: 'Test',
				});

				await expect(
					contentDashboardPage.getRow(adminWebContentTitle)
				).toBeVisible();

				await expect(
					contentDashboardPage.getRow(authorWebContentTitle)
				).toHaveCount(0);
			}
		});
	}
);

test(
	'Content Dashboard filters the assets by category and tag',
	{
		tag: [
			'@LPS-114911',
			'@LPS-115493',
			'@LPS-117419',
			'@LPS-124960',
			'@LPS-146471',
			'@LPS-155288',
		],
	},
	async ({apiHelpers, contentDashboardPage, page, site}) => {
		const vocabulary =
			await apiHelpers.headlessAdminTaxonomy.postSiteTaxonomyVocabulary({
				name: getRandomString(),
				siteId: site.id,
			});

		const siteCategories = [];

		for (let i = 0; i < 3; i++) {
			siteCategories.push(
				await apiHelpers.headlessAdminTaxonomy.postTaxonomyVocabularyTaxonomyCategory(
					{name: getRandomString(), vocabularyId: vocabulary.id}
				)
			);
		}

		const [firstAudienceCategory, secondAudienceCategory] =
			await addGlobalCategories({
				apiHelpers,
				names: [getRandomString(), getRandomString()],
				vocabularyName: 'Audience',
			});

		const firstTag = `tag${getRandomInt()}`;
		const secondTag = `tag${getRandomInt()}`;

		const keyword = getRandomString().replace(/-/g, '');

		const firstWebContentTitle = `${keyword} ${getRandomString()}`;

		const basicWebContentStructureId =
			await getBasicWebContentStructureId(apiHelpers);

		await addApprovedStructuredContent({
			apiHelpers,
			categoryIds: [
				siteCategories[0].id,
				firstAudienceCategory.id,
				secondAudienceCategory.id,
			],
			contentStructureId: basicWebContentStructureId,
			siteId: site.id,
			tags: [firstTag, secondTag],
			title: firstWebContentTitle,
		});

		const secondWebContentTitle = `${keyword} ${getRandomString()}`;

		await addApprovedStructuredContent({
			apiHelpers,
			categoryIds: [firstAudienceCategory.id],
			contentStructureId: basicWebContentStructureId,
			siteId: site.id,
			tags: [firstTag],
			title: secondWebContentTitle,
		});

		const blogTitle = getRandomString();

		await apiHelpers.headlessDelivery.postBlog(site.id, {
			headline: blogTitle,
			keywords: [firstTag],
			taxonomyCategoryIds: [siteCategories[0].id],
		});

		const secondSite = await apiHelpers.headlessAdminSite.postSite({
			name: getRandomString(),
		});

		const secondSiteWebContentTitle = `${keyword} ${getRandomString()}`;

		await addApprovedStructuredContent({
			apiHelpers,
			contentStructureId: basicWebContentStructureId,
			siteId: secondSite.id,
			tags: [firstTag],
			title: secondSiteWebContentTitle,
		});

		const vocabularyLabel = `${vocabulary.name} (${site.name})`;

		await test.step('CheckCategoriesResultsBarAtTreeviewComponent, CheckAllCheckboxesTurnsDeselectedWithClearButtonAtTreeviewComponent', async () => {
			await contentDashboardPage.goto(undefined, {scopeId: site.id});

			const frame = await contentDashboardPage.openFilterModal(
				'Categories',
				'Select Categories'
			);

			await frame.getByText(vocabularyLabel, {exact: true}).click();

			await frame
				.getByText(siteCategories[0].name, {exact: true})
				.click();

			await expect(
				frame.getByText('1 Item Selected', {exact: true})
			).toBeVisible();

			const clearAllButton = frame.getByText('Clear All', {exact: true});

			await expect(clearAllButton).toBeVisible();

			for (const siteCategory of siteCategories.slice(1)) {
				await frame.getByText(siteCategory.name, {exact: true}).click();
			}

			await expect(
				frame.getByText('3 Items Selected', {exact: true})
			).toBeVisible();

			await clearAllButton.click();

			await expect(clearAllButton).toHaveCount(0);

			await expect(frame.getByText(/Items? Selected/)).toHaveCount(0);

			for (const siteCategory of siteCategories) {
				await expect(
					contentDashboardPage
						.getTreeNode(frame, siteCategory.name)
						.getByRole('checkbox')
				).not.toBeChecked();
			}

			await page
				.getByRole('dialog')
				.getByRole('button', {exact: true, name: 'Cancel'})
				.click();
		});

		await test.step('CategorieShowInTable, FilterBlogByMetadata, FilterByCategory', async () => {
			await contentDashboardPage.filterByCategories([
				{categoryName: siteCategories[0].name, vocabularyLabel},
			]);

			await expect(
				contentDashboardPage.getAppliedFilter(
					`Category: ${siteCategories[0].name}`
				)
			).toBeVisible();

			await expect(
				contentDashboardPage.getRow(firstWebContentTitle)
			).toBeVisible();

			await expect(contentDashboardPage.getRow(blogTitle)).toBeVisible();

			await expect(
				contentDashboardPage.getRow(secondWebContentTitle)
			).toHaveCount(0);
		});

		await test.step('CheckCategoriesKeepResultsBarAtTreeviewComponent', async () => {
			const frame = await contentDashboardPage.openFilterModal(
				'Categories',
				'Select Categories'
			);

			await expect(
				frame.getByText('1 Item Selected', {exact: true})
			).toBeVisible();

			await page
				.getByRole('dialog')
				.getByRole('button', {exact: true, name: 'Cancel'})
				.click();
		});

		await test.step('FilterBlogByMetadata', async () => {
			await contentDashboardPage.goto();

			await contentDashboardPage.filterBySite(site.name);

			await expect(contentDashboardPage.getRow(blogTitle)).toBeVisible();

			await contentDashboardPage.goto(undefined, {scopeId: site.id});

			await contentDashboardPage.filterByTag(firstTag);

			await expect(contentDashboardPage.getRow(blogTitle)).toBeVisible();
		});

		await test.step('FilterByCategoryAndTag', async () => {
			await contentDashboardPage.goto(undefined, {scopeId: site.id});

			await contentDashboardPage.filterByCategories([
				{
					categoryName: firstAudienceCategory.name,
					vocabularyLabel: 'Audience (Global)',
				},
			]);

			await contentDashboardPage.filterByTag(secondTag);

			await expect(
				contentDashboardPage.getRow(firstWebContentTitle)
			).toBeVisible();

			await expect(
				contentDashboardPage.getRow(secondWebContentTitle)
			).toHaveCount(0);
		});

		await test.step('FilterByMultipleTags', async () => {
			await contentDashboardPage.goto(undefined, {scopeId: site.id});

			await contentDashboardPage.filterByTag(firstTag);

			await contentDashboardPage.filterByTag(secondTag);

			await expect(
				contentDashboardPage.getRow(firstWebContentTitle)
			).toBeVisible();

			await expect(
				contentDashboardPage.getRow(secondWebContentTitle)
			).toHaveCount(0);

			await contentDashboardPage.goto(undefined, {scopeId: site.id});

			await contentDashboardPage.filterByTag(secondTag);

			await expect(
				contentDashboardPage.getRow(firstWebContentTitle)
			).toBeVisible();

			await expect(
				contentDashboardPage.getRow(secondWebContentTitle)
			).toHaveCount(0);
		});

		await test.step('FilterTheListOfContentsByTag', async () => {
			await contentDashboardPage.goto();

			const frame = await contentDashboardPage.openFilterModal(
				'Tags',
				'Select Tags'
			);

			const searchInput = frame.getByPlaceholder('Search for');

			await searchInput.fill(firstTag);

			await searchInput.press('Enter');

			const tagRow = frame.locator('tr', {hasText: firstTag});

			await tagRow.getByRole('checkbox').first().check();

			await contentDashboardPage.selectModalItems();

			await expect(
				contentDashboardPage.getAppliedFilter('Tag:')
			).toHaveText([`Tag: ${firstTag}`]);

			await contentDashboardPage.search(keyword);

			await expect(
				contentDashboardPage.getRow(firstWebContentTitle)
			).toBeVisible();

			await expect(
				contentDashboardPage.getRow(secondSiteWebContentTitle)
			).toBeVisible();
		});
	}
);

test(
	'Content Dashboard filters the assets by status',
	{
		tag: [
			'@LPS-114983',
			'@LPS-150553',
			'@LPS-155386',
			'@LPS-161330',
			'@LPS-162827',
			'@LPS-86164',
		],
	},
	async ({apiHelpers, contentDashboardPage, page, site}) => {
		const basicWebContentStructureId =
			await getBasicWebContentStructureId(apiHelpers);

		const approvedWebContentTitle = getRandomString();

		await addApprovedStructuredContent({
			apiHelpers,
			contentStructureId: basicWebContentStructureId,
			siteId: site.id,
			title: approvedWebContentTitle,
		});

		const draftWebContentTitle = getRandomString();

		await addDraftStructuredContent({
			apiHelpers,
			contentStructureId: basicWebContentStructureId,
			siteId: site.id,
			title: draftWebContentTitle,
		});

		const expiredWebContentTitle = getRandomString();

		await addExpiredStructuredContent(
			apiHelpers,
			site.id,
			basicWebContentStructureId,
			expiredWebContentTitle
		);

		const scheduledWebContentTitle = getRandomString();

		await addScheduledStructuredContent(
			apiHelpers,
			site.id,
			basicWebContentStructureId,
			scheduledWebContentTitle
		);

		const approvedBlogTitle = getRandomString();

		await apiHelpers.headlessDelivery.postBlog(site.id, {
			headline: approvedBlogTitle,
		});

		const draftBlogTitle = getRandomString();

		await apiHelpers.jsonWebServicesBlogs.addEntry({
			groupId: site.id,
			title: draftBlogTitle,
			workflowAction: BLOGS_WORKFLOW_ACTION.SAVE_DRAFT,
		});

		const nextYearDate = new Date();

		nextYearDate.setFullYear(nextYearDate.getFullYear() + 1);

		const scheduledBlogTitle = getRandomString();

		await apiHelpers.jsonWebServicesBlogs.addEntry({
			displayDate: nextYearDate,
			groupId: site.id,
			title: scheduledBlogTitle,
		});

		const expiredDocumentTitle = getRandomString();

		const expiredDocument = await apiHelpers.headlessDelivery.postDocument(
			site.id,
			getDocumentFile(`${expiredDocumentTitle}.txt`),
			{
				fileName: `${expiredDocumentTitle}.txt`,
				title: expiredDocumentTitle,
			}
		);

		const fileVersion =
			await apiHelpers.jsonWebServicesDocumentLibrary.getLastestFileVersion(
				String(expiredDocument.id)
			);

		await apiHelpers.jsonWebServicesDocumentLibrary.updateStatus(
			String(expiredDocument.creator.id),
			fileVersion.fileVersionId,
			DLFILE_STATUS.EXPIRED
		);

		for (const className of [
			'com.liferay.blogs.model.BlogsEntry',
			'com.liferay.journal.model.JournalArticle',
		] as const) {
			await addSingleApproverWorkflow({
				apiHelpers,
				className,
				siteId: site.id,
			});
		}

		const pendingBlogTitle = getRandomString();

		await apiHelpers.headlessDelivery.postBlog(site.id, {
			headline: pendingBlogTitle,
		});

		const pendingWebContentTitle = getRandomString();

		await addApprovedStructuredContent({
			apiHelpers,
			contentStructureId: basicWebContentStructureId,
			siteId: site.id,
			title: pendingWebContentTitle,
		});

		const pageErrors: string[] = [];

		page.on('pageerror', (error) => pageErrors.push(error.message));

		await contentDashboardPage.goto(undefined, {scopeId: site.id});

		await test.step('ExpiredWebContent', async () => {
			await expect(
				contentDashboardPage.getCell(expiredWebContentTitle, 'status')
			).toHaveText('Expired');
		});

		await test.step('FilterByStatusAny', async () => {
			await contentDashboardPage.filterBy('Any');

			await expect(
				contentDashboardPage.getRow(approvedWebContentTitle)
			).toBeVisible();

			await expect(
				contentDashboardPage.getRow(draftWebContentTitle)
			).toBeVisible();
		});

		for (const {hiddenTitles, status, stepName, visibleTitles} of [
			{
				hiddenTitles: [draftBlogTitle, draftWebContentTitle],
				status: 'Approved',
				stepName: 'FilterBlogByStatus, FilterByStatusAny',
				visibleTitles: [approvedBlogTitle, approvedWebContentTitle],
			},
			{
				hiddenTitles: [approvedBlogTitle, approvedWebContentTitle],
				status: 'Draft',
				stepName: 'FilterBlogByStatus, FilterByStatusAny',
				visibleTitles: [draftBlogTitle, draftWebContentTitle],
			},
			{
				hiddenTitles: [approvedBlogTitle, approvedWebContentTitle],
				status: 'Expired',
				stepName:
					'ExpiredWebContent, FilterByStatusExpiredDocument, FilterByStatusExpiredWebContent',
				visibleTitles: [expiredDocumentTitle, expiredWebContentTitle],
			},
			{
				hiddenTitles: [approvedBlogTitle, approvedWebContentTitle],
				status: 'Pending',
				stepName:
					'FilterByStatusPendingBlog, FilterByStatusPendingWebContent',
				visibleTitles: [pendingBlogTitle, pendingWebContentTitle],
			},
			{
				hiddenTitles: [approvedBlogTitle, approvedWebContentTitle],
				status: 'Scheduled',
				stepName: 'FilterBlogByStatus, FilterByStatusScheduled',
				visibleTitles: [scheduledBlogTitle, scheduledWebContentTitle],
			},
		]) {
			await test.step(stepName, async () => {
				await contentDashboardPage.filterBy(status);

				await expect(
					contentDashboardPage.getAppliedFilter(`Status: ${status}`)
				).toBeVisible();

				for (const title of visibleTitles) {
					await expect(
						contentDashboardPage.getRow(title)
					).toBeVisible();
				}

				for (const title of hiddenTitles) {
					await expect(
						contentDashboardPage.getRow(title)
					).toHaveCount(0);
				}
			});
		}

		await test.step('FilterBlogByStatus', async () => {
			await contentDashboardPage.goto(undefined, {scopeId: site.id});

			await contentDashboardPage.filterBy('Approved');

			await contentDashboardPage.filterBy('Me');

			await expect(
				contentDashboardPage.getAppliedFilter('Author: Test Test')
			).toBeVisible();

			await expect(
				contentDashboardPage.getRow(approvedBlogTitle)
			).toBeVisible();

			await expect(
				contentDashboardPage.getRow(draftBlogTitle)
			).toHaveCount(0);
		});

		await test.step('Status filters raise no page errors', async () => {
			expect(pageErrors).toEqual([]);
		});
	}
);

test(
	'Content Dashboard filters the assets by type, subtype and extension',
	{
		tag: ['@LPS-124960', '@LPS-133354', '@LPS-146471'],
	},
	async ({apiHelpers, contentDashboardPage, page, site}) => {
		const basicWebContentTitle = getRandomString();

		const basicWebContentStructureId =
			await getBasicWebContentStructureId(apiHelpers);

		await addApprovedStructuredContent({
			apiHelpers,
			contentStructureId: basicWebContentStructureId,
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

		const blogTitle = getRandomString();

		await apiHelpers.headlessDelivery.postBlog(site.id, {
			headline: blogTitle,
		});

		const docDocumentTitle = getRandomString();

		await apiHelpers.headlessDelivery.postDocument(
			site.id,
			getDocumentFile(`${docDocumentTitle}.doc`, 'application/msword'),
			{fileName: `${docDocumentTitle}.doc`, title: docDocumentTitle}
		);

		const imageDocumentTitle = getRandomString();

		await apiHelpers.headlessDelivery.postDocument(
			site.id,
			createReadStream(path.join(__dirname, 'dependencies/image1.jpeg')),
			{fileName: `${imageDocumentTitle}.jpeg`, title: imageDocumentTitle}
		);

		const audioDocumentTitle = getRandomString();

		await apiHelpers.headlessDelivery.postDocument(
			site.id,
			getDocumentFile(`${audioDocumentTitle}.mp3`, 'audio/mpeg'),
			{fileName: `${audioDocumentTitle}.mp3`, title: audioDocumentTitle}
		);

		await test.step('FilterBySubtypeLabel', async () => {
			for (const {nodeName, subtypeLabel, title} of [
				{
					nodeName: 'Basic Document',
					subtypeLabel: 'Basic Document',
					title: docDocumentTitle,
				},
				{
					nodeName: 'Basic Web Content (Global)',
					subtypeLabel: 'Basic Web Content (Global)',
					title: basicWebContentTitle,
				},
				{
					nodeName: /^Blogs Entry \(/,
					subtypeLabel: 'Blogs Entry',
					title: blogTitle,
				},
			]) {
				await contentDashboardPage.goto(undefined, {scopeId: site.id});

				await contentDashboardPage.filterByTreeNodes({
					dialogTitle: 'Filter by Type',
					menuItemName: 'Type',
					nodeNames: [nodeName],
				});

				await expect(
					contentDashboardPage.getAppliedFilter(
						`Subtype: ${subtypeLabel}`
					)
				).toBeVisible();

				await expect(contentDashboardPage.getRow(title)).toBeVisible();
			}
		});

		await test.step('FilterByTypes', async () => {
			for (const {nodeNames, titles} of [
				{
					nodeNames: [
						'Basic Web Content (Global)',
						/^Blogs Entry \(/,
					],
					titles: [basicWebContentTitle, blogTitle],
				},
				{
					nodeNames: [
						'Basic Web Content (Global)',
						`${structureName} (${site.name})`,
					],
					titles: [basicWebContentTitle, structureWebContentTitle],
				},
			]) {
				await contentDashboardPage.goto(undefined, {scopeId: site.id});

				await contentDashboardPage.filterByTreeNodes({
					dialogTitle: 'Filter by Type',
					menuItemName: 'Type',
					nodeNames,
				});

				for (const title of titles) {
					await expect(
						contentDashboardPage.getRow(title)
					).toBeVisible();
				}
			}
		});

		await test.step('FilterByExtensionLabel', async () => {
			await contentDashboardPage.goto(undefined, {scopeId: site.id});

			await contentDashboardPage.filterByTreeNodes({
				dialogTitle: 'Filter by Extension',
				menuItemName: 'Extension',
				nodeNames: ['doc'],
			});

			await expect(
				contentDashboardPage.getAppliedFilter('Extension: doc')
			).toBeVisible();

			await expect(
				contentDashboardPage.getRow(docDocumentTitle)
			).toBeVisible();
		});

		await test.step('SelectedParentAndChildExtensions', async () => {
			await contentDashboardPage.goto(undefined, {scopeId: site.id});

			const frame = await contentDashboardPage.openFilterModal(
				'Extension',
				'Filter by Extension'
			);

			await contentDashboardPage.selectTreeNode({
				frame,
				nodeName: /^Text \(/,
				search: 'doc',
			});

			await contentDashboardPage.selectModalItems();

			await expect(
				contentDashboardPage.getAppliedFilter('Extension: doc')
			).toBeVisible();
		});

		await test.step('SelectedExtensionNumbers', async () => {
			await contentDashboardPage.goto(undefined, {scopeId: site.id});

			const frame = await contentDashboardPage.openFilterModal(
				'Extension',
				'Filter by Extension'
			);

			for (const nodeName of [/^Image \(/, /^Text \(/, 'mp3']) {
				await contentDashboardPage.selectTreeNode({frame, nodeName});
			}

			await contentDashboardPage.selectModalItems();

			await expect(
				page.getByText('3 Results Found With Filters', {exact: true})
			).toBeVisible();
		});
	}
);

test(
	'Content Dashboard lists every asset again when the type filter is deselected',
	{tag: '@LPS-138908'},
	async ({apiHelpers, contentDashboardPage, site}) => {
		const webContentTitle = getRandomString();

		await addApprovedStructuredContent({
			apiHelpers,
			contentStructureId: await getBasicWebContentStructureId(apiHelpers),
			siteId: site.id,
			title: webContentTitle,
		});

		const documentTitle = getRandomString();

		await apiHelpers.headlessDelivery.postDocument(
			site.id,
			getDocumentFile(`${documentTitle}.txt`),
			{fileName: `${documentTitle}.txt`, title: documentTitle}
		);

		await contentDashboardPage.goto(undefined, {scopeId: site.id});

		const typeFilter =
			contentDashboardPage.getAppliedFilter(/^(Sub)?[Tt]ype:/);

		await contentDashboardPage.filterByTreeNodes({
			dialogTitle: 'Filter by Type',
			menuItemName: 'Type',
			nodeNames: [/^Web Content Article \(/],
		});

		await expect(typeFilter).not.toHaveCount(0);

		await expect(
			contentDashboardPage.getRow(webContentTitle)
		).toBeVisible();

		await expect(contentDashboardPage.getRow(documentTitle)).toHaveCount(0);

		const frame = await contentDashboardPage.openFilterModal(
			'Type',
			'Filter by Type'
		);

		const webContentArticleCheckbox = contentDashboardPage
			.getTreeNode(frame, /^Web Content Article \(/)
			.getByRole('checkbox');

		await expect(webContentArticleCheckbox).toBeChecked();

		await webContentArticleCheckbox.uncheck();

		await expect(frame.getByRole('checkbox', {checked: true})).toHaveCount(
			0
		);

		await contentDashboardPage.selectModalItems();

		await expect(typeFilter).toHaveCount(0);

		await expect(
			contentDashboardPage.getRow(webContentTitle)
		).toBeVisible();

		await expect(contentDashboardPage.getRow(documentTitle)).toBeVisible();
	}
);
