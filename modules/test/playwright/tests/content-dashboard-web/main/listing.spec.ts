/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {dataApiHelpersTest} from '../../../fixtures/dataApiHelpersTest';
import {isolatedSiteTest} from '../../../fixtures/isolatedSiteTest';
import {loginTest} from '../../../fixtures/loginTest';
import getGlobalSiteId from '../../../utils/getGlobalSiteId';
import getRandomString from '../../../utils/getRandomString';
import addApprovedStructuredContent from '../../../utils/structured-content/addApprovedStructuredContent';
import getBasicWebContentStructureId from '../../../utils/structured-content/getBasicWebContentStructureId';
import {contentDashboardPagesTest} from './fixtures/contentDashboardPagesTest';
import addGlobalCategories from './utils/addGlobalCategories';
import getDocumentFile from './utils/getDocumentFile';

export const test = mergeTests(
	contentDashboardPagesTest,
	dataApiHelpersTest,
	isolatedSiteTest,
	loginTest()
);

test(
	'Content Dashboard table shows the metadata columns, the type and the categories of each asset',
	{
		tag: ['@LPS-117341', '@LPS-131187', '@LPS-132509', '@LPS-133716'],
	},
	async ({apiHelpers, contentDashboardPage, page, site}) => {
		const categoryNamePrefix = getRandomString();

		const audienceCategories = await addGlobalCategories({
			apiHelpers,
			names: [1, 2, 3].map((index) => `${categoryNamePrefix} ${index}`),
			vocabularyName: 'Audience',
		});

		await addGlobalCategories({
			apiHelpers,
			names: [`${categoryNamePrefix} Stage`],
			vocabularyName: 'Stage',
		});

		const blogTitle = getRandomString();

		await apiHelpers.headlessDelivery.postBlog(site.id, {
			headline: blogTitle,
		});

		const documentTitle = getRandomString();

		await apiHelpers.headlessDelivery.postDocument(
			site.id,
			getDocumentFile(`${documentTitle}.txt`),
			{fileName: `${documentTitle}.txt`, title: documentTitle}
		);

		const webContentTitle = getRandomString();

		await addApprovedStructuredContent({
			apiHelpers,
			categoryIds: audienceCategories.map(({id}) => id),
			contentStructureId: await getBasicWebContentStructureId(apiHelpers),
			siteId: site.id,
			title: webContentTitle,
		});

		await contentDashboardPage.goto(undefined, {scopeId: site.id});

		await test.step('AssetMetadataColumns, CheckSiteAssertLibraryColumn', async () => {
			await expect(contentDashboardPage.getRow(blogTitle)).toBeVisible();

			for (const columnHeader of [
				'Author',
				'Audience',
				'Modified Date',
				'Site or Asset Library',
				'Stage',
				'Status',
				'Subtype',
				'Title',
			]) {
				await expect(
					page.locator('thead').getByRole('cell', {
						exact: true,
						name: columnHeader,
					})
				).toBeVisible();
			}
		});

		await test.step('CheckForMoreCategories', async () => {
			await expect(
				contentDashboardPage.getCell(webContentTitle, 'audience')
			).toHaveText(
				new RegExp(`${audienceCategories[0].name}\\s*\\+\\s*2`)
			);
		});

		await test.step('TypeColumnForDocument, TypeColumnForWebContent', async () => {
			await expect(
				contentDashboardPage.getCell(documentTitle, 'type')
			).toHaveText('Document');

			await expect(
				contentDashboardPage.getCell(webContentTitle, 'type')
			).toHaveText('Web Content Article');
		});
	}
);

test(
	'Content Dashboard lists the assets of every site and asset library and filters them by site',
	{
		tag: ['@LPS-114982', '@LPS-119966', '@LPS-132509', '@LPS-133716'],
	},
	async ({apiHelpers, contentDashboardPage, site}) => {
		const secondSite = await apiHelpers.headlessAdminSite.postSite({
			name: getRandomString(),
		});

		const assetLibrary =
			await apiHelpers.headlessAssetLibrary.createAssetLibrary({
				name: getRandomString(),
				type: 'AssetLibrary',
			});

		const keyword = getRandomString().replace(/-/g, '');

		const assetLibraryDocumentTitle = `${keyword} ${getRandomString()}`;

		await apiHelpers.headlessDelivery.postAssetLibraryDocument(
			assetLibrary.id,
			getDocumentFile(`${assetLibraryDocumentTitle}.txt`),
			{
				fileName: `${assetLibraryDocumentTitle}.txt`,
				title: assetLibraryDocumentTitle,
			}
		);

		const basicWebContentStructureId =
			await getBasicWebContentStructureId(apiHelpers);

		const siteAssets = [];

		for (const curSite of [site, secondSite]) {
			const blogTitle = `${keyword} ${getRandomString()}`;

			await apiHelpers.headlessDelivery.postBlog(curSite.id, {
				headline: blogTitle,
			});

			const documentTitle = `${keyword} ${getRandomString()}`;

			await apiHelpers.headlessDelivery.postDocument(
				curSite.id,
				getDocumentFile(`${documentTitle}.txt`),
				{fileName: `${documentTitle}.txt`, title: documentTitle}
			);

			const webContentTitle = `${keyword} ${getRandomString()}`;

			await addApprovedStructuredContent({
				apiHelpers,
				contentStructureId: basicWebContentStructureId,
				siteId: curSite.id,
				title: webContentTitle,
			});

			siteAssets.push({
				blogTitle,
				documentTitle,
				siteName: curSite.name,
				webContentTitle,
			});
		}

		await contentDashboardPage.goto();

		await contentDashboardPage.search(keyword);

		await test.step('BlogsFromDifferentSitesAreListed, CheckSiteAssertLibraryOfContent, DocumentFromAssetLibraryIsListed, DocumentsFromDifferentSitesAreListed', async () => {
			for (const {
				blogTitle,
				documentTitle,
				siteName,
				webContentTitle,
			} of siteAssets) {
				await expect(
					contentDashboardPage.getRow(blogTitle)
				).toBeVisible();

				await expect(
					contentDashboardPage.getRow(documentTitle)
				).toBeVisible();

				await expect(
					contentDashboardPage.getCell(
						webContentTitle,
						'site-or-asset-library'
					)
				).toHaveText(siteName);
			}

			await expect(
				contentDashboardPage.getCell(
					assetLibraryDocumentTitle,
					'site-or-asset-library'
				)
			).toHaveText(assetLibrary.name);
		});

		await test.step('BlogsFromDifferentSitesAreListed, FilterBySite', async () => {
			for (const [index, {blogTitle, siteName}] of siteAssets.entries()) {
				await contentDashboardPage.filterBySite(siteName);

				await expect(
					contentDashboardPage.getAppliedFilter(
						`Site or Asset Library: ${siteName}`
					)
				).toBeVisible();

				await expect(
					contentDashboardPage.getRow(blogTitle)
				).toBeVisible();

				await expect(
					contentDashboardPage.getRow(siteAssets[1 - index].blogTitle)
				).toHaveCount(0);
			}
		});

		await test.step('FilterBySite', async () => {
			const globalSiteId = await getGlobalSiteId(apiHelpers);

			const globalWebContentTitle = `${keyword} ${getRandomString()}`;

			const globalWebContent = await addApprovedStructuredContent({
				apiHelpers,
				contentStructureId: basicWebContentStructureId,
				siteId: globalSiteId,
				title: globalWebContentTitle,
			});

			try {
				await contentDashboardPage.goto();

				await contentDashboardPage.filterBySite('Global');

				await contentDashboardPage.search(keyword);

				await expect(
					contentDashboardPage.getRow(globalWebContentTitle)
				).toBeVisible();

				await expect(
					contentDashboardPage.getRow(siteAssets[0].webContentTitle)
				).toHaveCount(0);
			}
			finally {
				await apiHelpers.jsonWebServicesJournal.deleteArticle(
					globalSiteId,
					globalWebContent.key
				);
			}
		});
	}
);
