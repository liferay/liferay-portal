/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Page, expect, mergeTests} from '@playwright/test';
import {createReadStream} from 'fs';
import path from 'path';

import {dataApiHelpersTest} from '../../../fixtures/dataApiHelpersTest';
import {documentLibraryPagesTest} from '../../../fixtures/documentLibraryPages.fixtures';
import {isolatedSiteTest} from '../../../fixtures/isolatedSiteTest';
import {loginTest} from '../../../fixtures/loginTest';
import {createCategories} from '../../../helpers/CreateCategories';
import getRandomString from '../../../utils/getRandomString';
import {openFieldset} from '../../../utils/openFieldset';
import addApprovedStructuredContent from '../../../utils/structured-content/addApprovedStructuredContent';
import getBasicWebContentStructureId from '../../../utils/structured-content/getBasicWebContentStructureId';
import {waitForAlert} from '../../../utils/waitForAlert';
import {blogsPagesTest} from '../../blogs-web/main/fixtures/blogsPagesTest';
import {journalPagesTest} from '../../journal-web/main/fixtures/journalPagesTest';
import getDataStructureDefinition from '../../journal-web/main/utils/getDataStructureDefinition';

const test = mergeTests(
	blogsPagesTest,
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
