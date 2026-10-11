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
import {WebContentPage} from '../../../pages/journal-web/WebContentPage';
import {RecycleBinPage} from '../../../pages/trash-web/RecycleBinPage';
import getRandomString from '../../../utils/getRandomString';
import {enableLocalStaging} from '../../../utils/staging';
import addApprovedStructuredContent from '../../../utils/structured-content/addApprovedStructuredContent';
import getBasicWebContentStructureId from '../../../utils/structured-content/getBasicWebContentStructureId';
import {blogsPagesTest} from '../../blogs-web/main/fixtures/blogsPagesTest';
import {stagingPageTest} from '../../export-import-web/main/fixtures/stagingPageTest';
import {assetCategoriesPagesTest} from './fixtures/assetCategoriesAdminPagesTest';
import {AssetCategoriesAdminPage} from './pages/AssetCategoriesAdminPage';

const test = mergeTests(
	assetCategoriesPagesTest,
	blogsPagesTest,
	dataApiHelpersTest,
	documentLibraryPagesTest,
	isolatedSiteTest,
	loginTest(),
	stagingPageTest
);

function getAssertUsages(
	assetCategoriesAdminPage: AssetCategoriesAdminPage,
	page: Page
) {
	return async ({
		categoryName,
		siteUrl,
		usages,
		vocabularyName,
	}: {
		categoryName: string;
		siteUrl: string;
		usages: number;
		vocabularyName: string;
	}) => {
		await assetCategoriesAdminPage.goto(siteUrl);

		await assetCategoriesAdminPage.gotoVocabulary(vocabularyName);

		const row = page.getByRole('row').filter({hasText: categoryName});

		await expect(row).toBeVisible();

		const headers = await page.locator('thead th').allInnerTexts();

		const usagesIndex = headers.findIndex(
			(header) => header.trim() === 'Usages'
		);

		expect(usagesIndex).toBeGreaterThan(-1);

		await expect(row.getByRole('cell').nth(usagesIndex)).toHaveText(
			String(usages)
		);
	};
}

test(
	'View the usages of a site category and a global category',
	{tag: '@LPD-109100'},
	async ({apiHelpers, assetCategoriesAdminPage, blogsPage, page, site}) => {
		const recycleBinPage = new RecycleBinPage(page);
		const webContentPage = new WebContentPage(page);

		const assertUsages = getAssertUsages(assetCategoriesAdminPage, page);

		await test.step('View the usages of a site category used by a web content', async () => {
			const vocabularyName = getRandomString();

			const [category] = await createCategories({
				apiHelpers,
				categoryNames: [{name: getRandomString()}],
				siteId: site.id,
				vocabularyName,
			});

			const webContentTitle = getRandomString();

			await addApprovedStructuredContent({
				apiHelpers,
				categoryIds: [category.id],
				contentStructureId:
					await getBasicWebContentStructureId(apiHelpers),
				siteId: site.id,
				title: webContentTitle,
			});

			const siteCategoryUsages = {
				categoryName: category.name,
				siteUrl: site.friendlyUrlPath,
				vocabularyName,
			};

			await assertUsages({...siteCategoryUsages, usages: 1});

			await webContentPage.goto(site.friendlyUrlPath);

			await webContentPage.moveToRecycleBin(webContentTitle);

			await assertUsages({...siteCategoryUsages, usages: 1});

			await recycleBinPage.goto(site.friendlyUrlPath);

			await recycleBinPage.delete(webContentTitle);

			await assertUsages({...siteCategoryUsages, usages: 0});
		});

		await test.step('View the usages of a global category used by a blog entry', async () => {
			const company =
				await apiHelpers.jsonWebServicesCompany.getCompanyByWebId(
					'liferay.com'
				);

			const companyGroup =
				await apiHelpers.jsonWebServicesGroup.getCompanyGroup(
					company.companyId
				);

			const vocabularyName = getRandomString();

			const [category] = await createCategories({
				apiHelpers,
				categoryNames: [{name: getRandomString()}],
				siteId: companyGroup.groupId,
				vocabularyName,
			});

			const blogPosting = await apiHelpers.headlessDelivery.postBlog(
				site.id,
				{taxonomyCategoryIds: [category.id]}
			);

			const globalCategoryUsages = {
				categoryName: category.name,
				siteUrl: '/global',
				vocabularyName,
			};

			await assertUsages({...globalCategoryUsages, usages: 1});

			await blogsPage.goto(site.friendlyUrlPath);

			await blogsPage.moveEntryToRecycleBin(blogPosting.headline);

			await assertUsages({...globalCategoryUsages, usages: 1});

			await recycleBinPage.goto(site.friendlyUrlPath);

			await recycleBinPage.delete(blogPosting.headline);

			await assertUsages({...globalCategoryUsages, usages: 0});
		});
	}
);

test(
	'View the usages of a category in the staging and live sites',
	{tag: '@LPD-109100'},
	async ({
		apiHelpers,
		assetCategoriesAdminPage,
		documentLibraryPage,
		page,
		site,
		stagingPage,
	}) => {
		const assertUsages = getAssertUsages(assetCategoriesAdminPage, page);
		const recycleBinPage = new RecycleBinPage(page);

		await enableLocalStaging(apiHelpers, page, site);

		const company =
			await apiHelpers.jsonWebServicesCompany.getCompanyByWebId(
				'liferay.com'
			);

		const stagingGroup =
			await apiHelpers.jsonWebServicesGroup.getGroupByKey(
				company.companyId,
				`${site.name}-staging`
			);

		const vocabularyName = getRandomString();

		const [category] = await createCategories({
			apiHelpers,
			categoryNames: [{name: getRandomString()}],
			siteId: stagingGroup.groupId,
			vocabularyName,
		});

		const documentTitle = getRandomString();

		await apiHelpers.headlessDelivery.postDocument(
			stagingGroup.groupId,
			createReadStream(
				path.join(
					__dirname,
					'../../document-library-web/main/dependencies/image1.jpeg'
				)
			),
			{
				fileName: getRandomString(),
				taxonomyCategoryIds: [category.id],
				title: documentTitle,
			}
		);

		const liveCategoryUsages = {
			categoryName: category.name,
			siteUrl: site.friendlyUrlPath,
			vocabularyName,
		};
		const stagingCategoryUsages = {
			...liveCategoryUsages,
			siteUrl: `${site.friendlyUrlPath}-staging`,
		};

		await assertUsages({...stagingCategoryUsages, usages: 1});

		await test.step('Publish to live', async () => {
			await stagingPage.goto(
				`${site.friendlyUrlPath.substring(1)}-staging`
			);

			await stagingPage.publish();
		});

		await assertUsages({...liveCategoryUsages, usages: 1});

		await test.step('Move the document to the recycle bin and delete it in live', async () => {
			await documentLibraryPage.goto(site.friendlyUrlPath);

			await documentLibraryPage.moveToRecycleBin(documentTitle);

			await assertUsages({...liveCategoryUsages, usages: 1});

			await recycleBinPage.goto(site.friendlyUrlPath);

			await recycleBinPage.delete(documentTitle);
		});

		await assertUsages({...liveCategoryUsages, usages: 0});

		await assertUsages({...stagingCategoryUsages, usages: 1});
	}
);
