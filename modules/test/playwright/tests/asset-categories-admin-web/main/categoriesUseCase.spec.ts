/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {FrameLocator, Page, expect, mergeTests} from '@playwright/test';
import {createReadStream} from 'fs';
import path from 'path';

import {collectionsPagesTest} from '../../../fixtures/collectionsPagesTest';
import {dataApiHelpersTest} from '../../../fixtures/dataApiHelpersTest';
import {documentLibraryPagesTest} from '../../../fixtures/documentLibraryPages.fixtures';
import {isolatedSiteTest} from '../../../fixtures/isolatedSiteTest';
import {loginTest} from '../../../fixtures/loginTest';
import {createCategories} from '../../../helpers/CreateCategories';
import addGlobalCategories from '../../../utils/addGlobalCategories';
import {clickAndExpectToBeVisible} from '../../../utils/clickAndExpectToBeVisible';
import getRandomString from '../../../utils/getRandomString';
import {openFieldset} from '../../../utils/openFieldset';
import addApprovedStructuredContent from '../../../utils/structured-content/addApprovedStructuredContent';
import getBasicWebContentStructureId from '../../../utils/structured-content/getBasicWebContentStructureId';
import {waitForAlert} from '../../../utils/waitForAlert';
import {blogsPagesTest} from '../../blogs-web/main/fixtures/blogsPagesTest';
import {contentDashboardPagesTest} from '../../content-dashboard-web/main/fixtures/contentDashboardPagesTest';
import {journalPagesTest} from '../../journal-web/main/fixtures/journalPagesTest';
import getDataStructureDefinition from '../../journal-web/main/utils/getDataStructureDefinition';
import getPageDefinition from '../../layout-content-page-editor-web/main/utils/getPageDefinition';
import getWidgetDefinition from '../../layout-content-page-editor-web/main/utils/getWidgetDefinition';
import {assetCategoriesPagesTest} from './fixtures/assetCategoriesAdminPagesTest';

const LEARN_HOW_URL = /defining-categories-and-vocabularies-for-content/;

const test = mergeTests(
	assetCategoriesPagesTest,
	blogsPagesTest,
	collectionsPagesTest,
	contentDashboardPagesTest,
	dataApiHelpersTest,
	documentLibraryPagesTest,
	isolatedSiteTest,
	journalPagesTest,
	loginTest()
);

async function selectCategory(
	page: Page,
	vocabularyName: string,
	categoryName: string
) {
	await page.getByRole('button', {name: `Select ${vocabularyName}`}).click();

	const selectCategoryModal = page.getByRole('dialog', {
		name: `Select ${vocabularyName}`,
	});

	await expect(async () => {
		await selectCategoryModal
			.locator('li')
			.filter({hasText: categoryName})
			.getByRole('checkbox')
			.check({timeout: 2000});
	}).toPass({timeout: 10_000});

	await selectCategoryModal.getByRole('button', {name: 'Done'}).click();
}

function getCategoryNames(asset: {
	taxonomyCategoryBriefs: {taxonomyCategoryName: string}[];
}) {
	return asset.taxonomyCategoryBriefs
		.map(({taxonomyCategoryName}) => taxonomyCategoryName)
		.sort();
}

test(
	'Add public and internal categories to a web content, a blog entry and a document',
	{tag: '@LPD-109100'},
	async ({
		apiHelpers,
		blogsEditBlogEntryPage,
		blogsPage,
		documentLibraryEditFilePage,
		documentLibraryPage,
		journalEditArticlePage,
		journalPage,
		page,
		site,
	}) => {
		const internalVocabularyName = getRandomString();
		const publicVocabularyName = getRandomString();

		const [[internalCategory], [publicCategory]] = await Promise.all([
			createCategories({
				apiHelpers,
				categoryNames: [{name: getRandomString()}],
				siteId: site.id,
				vocabularyName: internalVocabularyName,
				vocabularyVisibility: true,
			}),
			createCategories({
				apiHelpers,
				categoryNames: [{name: getRandomString()}],
				siteId: site.id,
				vocabularyName: publicVocabularyName,
			}),
		]);

		const expectedCategoryNames = [
			internalCategory.name,
			publicCategory.name,
		].sort();

		const selectCategories = async () => {
			await selectCategory(
				page,
				publicVocabularyName,
				publicCategory.name
			);
			await selectCategory(
				page,
				internalVocabularyName,
				internalCategory.name
			);
		};

		await test.step('Add the categories to a web content', async () => {
			const webContentTitle = getRandomString();

			const structuredContent = await addApprovedStructuredContent({
				apiHelpers,
				contentStructureId:
					await getBasicWebContentStructureId(apiHelpers),
				siteId: site.id,
				title: webContentTitle,
			});

			await journalPage.goto(site.friendlyUrlPath);

			await journalEditArticlePage.editArticle(webContentTitle);

			await journalEditArticlePage.openFieldSet(
				'Categorization',
				'categorization'
			);

			await selectCategories();

			await journalEditArticlePage.publishArticle(true);

			await waitForAlert(
				page,
				`Success:${webContentTitle} was updated successfully.`
			);

			expect(
				getCategoryNames(
					await apiHelpers.get(
						`${apiHelpers.baseUrl}headless-delivery/v1.0/structured-contents/${structuredContent.id}`
					)
				)
			).toEqual(expectedCategoryNames);
		});

		await test.step('Add the categories to a blog entry', async () => {
			const blogPosting = await apiHelpers.headlessDelivery.postBlog(
				site.id
			);

			await blogsPage.goto(site.friendlyUrlPath);

			await blogsPage.goToBlogEntryAction('Edit', blogPosting.headline);

			await openFieldset(page, 'Categorization');

			await selectCategories();

			await blogsEditBlogEntryPage.publishBlogEntry();

			expect(
				getCategoryNames(
					await apiHelpers.get(
						`${apiHelpers.baseUrl}headless-delivery/v1.0/blog-postings/${blogPosting.id}`
					)
				)
			).toEqual(expectedCategoryNames);
		});

		await test.step('Add the categories to a document', async () => {
			const document = await apiHelpers.headlessDelivery.postDocument(
				site.id,
				createReadStream(
					path.join(
						__dirname,
						'../../document-library-web/main/dependencies/image1.jpeg'
					)
				),
				{fileName: getRandomString(), title: getRandomString()}
			);

			await documentLibraryPage.goto(site.friendlyUrlPath);

			await documentLibraryPage.goToEditFileEntry(document.title);

			await documentLibraryEditFilePage.openFieldset('Categorization');

			await selectCategories();

			await documentLibraryEditFilePage.publishFileEntry();

			expect(
				getCategoryNames(
					await apiHelpers.headlessDelivery.getDocument(document.id)
				)
			).toEqual(expectedCategoryNames);
		});
	}
);

test(
	'Add a web content with a category from the parent site',
	{tag: '@LPD-109100'},
	async ({apiHelpers, journalEditArticlePage, page}) => {
		const parentSite = await apiHelpers.headlessAdminSite.postSite({
			name: getRandomString(),
		});

		const childSite = await apiHelpers.headlessAdminSite.postSite({
			name: getRandomString(),
			parentSiteExternalReferenceCode: parentSite.externalReferenceCode,
		});

		const vocabularyName = getRandomString();

		const [category] = await createCategories({
			apiHelpers,
			categoryNames: [{name: getRandomString()}],
			siteId: parentSite.id,
			vocabularyName,
		});

		const title = getRandomString();

		await journalEditArticlePage.goto({siteUrl: childSite.friendlyUrlPath});

		await journalEditArticlePage.fillTitle(title);

		await journalEditArticlePage.selectCategories(vocabularyName, [
			category.name,
		]);

		await journalEditArticlePage.publishArticle();

		await journalEditArticlePage.editArticle(title);

		await journalEditArticlePage.openFieldSet(
			'Categorization',
			'categorization'
		);

		await expect(
			page
				.getByRole('group', {name: 'Categorization'})
				.getByText(category.name, {exact: true})
		).toBeVisible();
	}
);

test(
	'View a vocabulary for a web content subtype only in the editor of that subtype',
	{tag: '@LPD-109100'},
	async ({apiHelpers, journalEditArticlePage, page, site}) => {
		const structureName = getRandomString();

		await apiHelpers.dataEngine.createStructure(
			site.id,
			getDataStructureDefinition({
				defaultLanguageId: 'en_US',
				fields: [{fieldType: 'text', name: 'Text'}],
				name: structureName,
			})
		);

		const vocabularyName = getRandomString();

		await createCategories({
			apiHelpers,
			assetTypes: [
				{
					required: false,
					subtype: 'Basic Web Content',
					type: 'StructuredContent',
				},
			],
			categoryNames: [{name: getRandomString()}],
			siteId: site.id,
			vocabularyName,
		});

		await test.step('View the vocabulary in the basic web content editor', async () => {
			await journalEditArticlePage.goto({siteUrl: site.friendlyUrlPath});

			await journalEditArticlePage.openFieldSet(
				'Categorization',
				'categorization'
			);

			await expect(
				page.getByRole('button', {name: `Select ${vocabularyName}`})
			).toBeVisible();
		});

		await test.step('Do not view the vocabulary in the editor of another structure', async () => {
			await journalEditArticlePage.goto({
				siteUrl: site.friendlyUrlPath,
				structureName,
			});

			await journalEditArticlePage.openFieldSet(
				'Categorization',
				'categorization'
			);

			await expect(
				page.getByRole('group', {name: 'Categorization'})
			).toBeVisible();

			await expect(page.getByText(vocabularyName)).toHaveCount(0);
		});
	}
);

test(
	'View the public and internal vocabularies and the Learn how links of categorization',
	{tag: ['@LPD-112477', '@LPS-117015']},
	async ({
		assetCategoriesAdminPage,
		contentDashboardPage,
		journalEditArticlePage,
		page,
		site,
	}) => {
		const learnHowLink = page.getByRole('link', {name: 'Learn how'});

		await test.step('View the vocabularies in the web content editor', async () => {
			await journalEditArticlePage.goto({siteUrl: site.friendlyUrlPath});

			await journalEditArticlePage.openFieldSet(
				'Categorization',
				'categorization'
			);

			const categorization = page.getByRole('group', {
				name: 'Categorization',
			});

			await expect(
				categorization.getByText('Public Categories', {exact: true})
			).toBeVisible();
			await expect(
				categorization.getByText('Internal Categories', {exact: true})
			).toBeVisible();

			for (const vocabularyName of ['Audience', 'Stage', 'Topic']) {
				await expect(
					page.getByRole('button', {name: `Select ${vocabularyName}`})
				).toBeVisible();
			}

			await expect(learnHowLink).toHaveAttribute('href', LEARN_HOW_URL);
		});

		await test.step('View the Learn how link in the global vocabularies', async () => {
			await assetCategoriesAdminPage.goto('/global');

			await expect(learnHowLink).toHaveAttribute('href', LEARN_HOW_URL);
		});

		await test.step('View the Learn how link in the empty chart of the content dashboard', async () => {
			await contentDashboardPage.goto(undefined, {scopeId: site.id});

			await expect(learnHowLink).toHaveAttribute('href', LEARN_HOW_URL);
		});
	}
);

test(
	'View a vocabulary for basic documents only in the editor of basic documents',
	{tag: '@LPD-112477'},
	async ({apiHelpers, documentLibraryEditFilePage, page, site}) => {
		const vocabularyName = getRandomString();

		await createCategories({
			apiHelpers,
			assetTypes: [
				{
					required: false,
					subtype: 'Basic Document',
					type: 'Document',
				},
			],
			categoryNames: [{name: getRandomString()}],
			siteId: site.id,
			vocabularyName,
		});

		await test.step('View the vocabulary in the basic document editor', async () => {
			await documentLibraryEditFilePage.goto(site.friendlyUrlPath);

			await documentLibraryEditFilePage.openFieldset('Categorization');

			for (const name of [vocabularyName, 'Audience', 'Stage', 'Topic']) {
				await expect(
					page.getByRole('button', {name: `Select ${name}`})
				).toBeVisible();
			}
		});

		await test.step('Do not view the vocabulary in the external video shortcut editor', async () => {
			await documentLibraryEditFilePage.goToNewFileDifferentType(
				'External Video Shortcut',
				site.friendlyUrlPath
			);

			await documentLibraryEditFilePage.openFieldset('Categorization');

			for (const name of ['Audience', 'Stage', 'Topic']) {
				await expect(
					page.getByRole('button', {name: `Select ${name}`})
				).toBeVisible();
			}

			await expect(page.getByText(vocabularyName)).toHaveCount(0);
		});
	}
);

test(
	'Add global categories to blog entries of several sites',
	{tag: '@LPD-112477'},
	async ({apiHelpers, blogsEditBlogEntryPage, blogsPage, page, site}) => {
		const [[audienceCategory], [stageCategory]] = await Promise.all([
			addGlobalCategories({
				apiHelpers,
				names: [getRandomString()],
				vocabularyName: 'Audience',
			}),
			addGlobalCategories({
				apiHelpers,
				names: [getRandomString()],
				vocabularyName: 'Stage',
			}),
		]);

		const secondSite = await apiHelpers.headlessAdminSite.postSite({
			name: getRandomString(),
		});

		for (const [index, currentSite] of [site, secondSite].entries()) {
			await test.step(`Add the categories to a blog entry of the ${index ? 'second' : 'first'} site`, async () => {
				const blogPosting = await apiHelpers.headlessDelivery.postBlog(
					currentSite.id
				);

				await blogsPage.goto(currentSite.friendlyUrlPath);

				await blogsPage.goToBlogEntryAction(
					'Edit',
					blogPosting.headline
				);

				await openFieldset(page, 'Categorization');

				await selectCategory(page, 'Audience', audienceCategory.name);
				await selectCategory(page, 'Stage', stageCategory.name);

				await blogsEditBlogEntryPage.publishBlogEntry();

				expect(
					getCategoryNames(
						await apiHelpers.get(
							`${apiHelpers.baseUrl}headless-delivery/v1.0/blog-postings/${blogPosting.id}`
						)
					)
				).toEqual([audienceCategory.name, stageCategory.name].sort());
			});
		}
	}
);

test(
	'The Category Filter widget does not show internal vocabularies',
	{tag: ['@LPD-112477', '@LPS-134682']},
	async ({apiHelpers, page, site}) => {
		const internalVocabularyName = getRandomString();
		const publicVocabularyName = getRandomString();

		const [[internalCategory], [publicCategory]] = await Promise.all([
			createCategories({
				apiHelpers,
				categoryNames: [{name: getRandomString()}],
				siteId: site.id,
				vocabularyName: internalVocabularyName,
				vocabularyVisibility: true,
			}),
			createCategories({
				apiHelpers,
				categoryNames: [{name: getRandomString()}],
				siteId: site.id,
				vocabularyName: publicVocabularyName,
			}),
		]);

		const layout = await apiHelpers.headlessDelivery.createSitePage({
			pageDefinition: getPageDefinition([
				getWidgetDefinition({
					id: getRandomString(),
					widgetName:
						'com_liferay_asset_categories_navigation_web_portlet_AssetCategoriesNavigationPortlet',
				}),
			]),
			siteId: site.id,
			title: getRandomString(),
		});

		await page.goto(`/web${site.friendlyUrlPath}${layout.friendlyUrlPath}`);

		await clickAndExpectToBeVisible({
			target: page.getByText(publicCategory.name, {exact: true}),
			trigger: page.getByText(publicVocabularyName, {exact: true}),
		});

		await expect(
			page.getByText(internalVocabularyName, {exact: true})
		).toHaveCount(0);
		await expect(
			page.getByText(internalCategory.name, {exact: true})
		).toHaveCount(0);
	}
);

test(
	'A dynamic collection filtered by global categories lists only the blog entries with all of them',
	{tag: '@LPD-112477'},
	async ({apiHelpers, collectionsPage, page, site}) => {
		const [[audienceCategory], [stageCategory]] = await Promise.all([
			addGlobalCategories({
				apiHelpers,
				names: [getRandomString()],
				vocabularyName: 'Audience',
			}),
			addGlobalCategories({
				apiHelpers,
				names: [getRandomString()],
				vocabularyName: 'Stage',
			}),
		]);

		const categorizedBlogPosting =
			await apiHelpers.headlessDelivery.postBlog(site.id, {
				taxonomyCategoryIds: [audienceCategory.id, stageCategory.id],
			});

		const partiallyCategorizedBlogPosting =
			await apiHelpers.headlessDelivery.postBlog(site.id, {
				taxonomyCategoryIds: [audienceCategory.id],
			});

		const blogsEntryClassName =
			await apiHelpers.jsonWebServicesClassName.fetchClassName(
				'com.liferay.blogs.model.BlogsEntry'
			);

		const assetListEntry =
			await apiHelpers.jsonWebServicesAssetListEntry.addDynamicAssetListEntry(
				{
					groupId: site.id,
					title: getRandomString(),
					typeSettings: [
						`anyAssetType=${blogsEntryClassName.classNameId}`,
						`filters=${JSON.stringify([
							{
								operatorName: 'contains',
								propertyName: 'assetCategories',
								quantifier: 'all',
								value: [audienceCategory, stageCategory].map(
									({id}) => ({value: String(id)})
								),
							},
						])}`,
					].join('\n'),
				}
			);

		const expectOnlyCategorizedBlogPosting = async (
			locator: Page | FrameLocator
		) => {
			await expect(
				locator.getByText(categorizedBlogPosting.headline)
			).toBeVisible();
			await expect(
				locator.getByText(partiallyCategorizedBlogPosting.headline)
			).toHaveCount(0);
		};

		await test.step('View the items of the collection', async () => {
			await collectionsPage.goto(site.friendlyUrlPath);

			await collectionsPage.openCollection(assetListEntry.title);

			const viewItemsFrame = await collectionsPage.openViewItems();

			await expectOnlyCategorizedBlogPosting(viewItemsFrame);
		});

		await test.step('View the collection in an asset publisher', async () => {
			const assetPublisherLayout =
				await apiHelpers.headlessDelivery.createSitePage({
					pageDefinition: getPageDefinition([
						getWidgetDefinition({
							id: getRandomString(),
							widgetConfig: {
								assetListEntryExternalReferenceCode:
									assetListEntry.externalReferenceCode,
								selectionStyle: 'asset-list',
							},
							widgetName:
								'com_liferay_asset_publisher_web_portlet_AssetPublisherPortlet',
						}),
					]),
					siteId: site.id,
					title: getRandomString(),
				});

			await page.goto(
				`/web${site.friendlyUrlPath}${assetPublisherLayout.friendlyUrlPath}`
			);

			await expectOnlyCategorizedBlogPosting(page);
		});
	}
);

test(
	'Search finds the web contents by their public categories but not by their internal ones',
	{tag: ['@LPD-112477', '@LPS-134875']},
	async ({apiHelpers, page, site}) => {
		const [[internalCategory], [publicCategory]] = await Promise.all([
			createCategories({
				apiHelpers,
				categoryNames: [{name: getRandomString()}],
				siteId: site.id,
				vocabularyName: getRandomString(),
				vocabularyVisibility: true,
			}),
			createCategories({
				apiHelpers,
				categoryNames: [{name: getRandomString()}],
				siteId: site.id,
				vocabularyName: getRandomString(),
			}),
		]);

		const contentStructureId =
			await getBasicWebContentStructureId(apiHelpers);

		const internalWebContentTitle = getRandomString();
		const publicWebContentTitle = getRandomString();

		await addApprovedStructuredContent({
			apiHelpers,
			categoryIds: [internalCategory.id],
			contentStructureId,
			siteId: site.id,
			title: internalWebContentTitle,
		});

		await addApprovedStructuredContent({
			apiHelpers,
			categoryIds: [publicCategory.id],
			contentStructureId,
			siteId: site.id,
			title: publicWebContentTitle,
		});

		const layout = await apiHelpers.headlessDelivery.createSitePage({
			pageDefinition: getPageDefinition(
				[
					'com_liferay_portal_search_web_search_bar_portlet_SearchBarPortlet',
					'com_liferay_portal_search_web_search_results_portlet_SearchResultsPortlet',
				].map((widgetName) =>
					getWidgetDefinition({id: getRandomString(), widgetName})
				)
			),
			siteId: site.id,
			title: getRandomString(),
		});

		const pageURL = `/web${site.friendlyUrlPath}${layout.friendlyUrlPath}`;

		await test.step('Find the web content by its public category', async () => {
			await expect(async () => {
				await page.goto(`${pageURL}?q=${publicCategory.name}`);

				await expect(
					page.getByRole('link', {
						exact: true,
						name: publicWebContentTitle,
					})
				).toBeVisible({timeout: 2000});
			}).toPass({timeout: 30_000});
		});

		await test.step('Do not find the web content by its internal category', async () => {
			await page.goto(`${pageURL}?q=${internalCategory.name}`);

			await expect(
				page.getByText('No results were found.')
			).toBeVisible();
			await expect(page.getByText(internalWebContentTitle)).toHaveCount(
				0
			);
		});
	}
);
