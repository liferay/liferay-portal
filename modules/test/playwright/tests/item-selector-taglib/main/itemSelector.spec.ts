/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';
import path from 'path';

import {apiHelpersTest} from '../../../fixtures/apiHelpersTest';
import {documentLibraryPagesTest} from '../../../fixtures/documentLibraryPages.fixtures';
import {isolatedSiteTest} from '../../../fixtures/isolatedSiteTest';
import {loginTest} from '../../../fixtures/loginTest';
import getRandomString from '../../../utils/getRandomString';
import {journalPagesTest} from '../../journal-web/main/fixtures/journalPagesTest';
import getDataStructureDefinition from '../../journal-web/main/utils/getDataStructureDefinition';

const baseTest = mergeTests(
	apiHelpersTest,
	documentLibraryPagesTest,
	isolatedSiteTest,
	journalPagesTest,
	loginTest()
);

baseTest(
	'Check if Item Selectors Browser Breadcrumb is updated after change folder',
	{
		tag: '@LPD-31633',
	},
	async ({documentLibraryPage, journalEditArticlePage, page, site}) => {
		await documentLibraryPage.goto(site.friendlyUrlPath);
		await documentLibraryPage.goToCreateNewFolder();
		const folderName = getRandomString();
		await page.getByLabel('Name Required').fill(folderName);
		await page.getByRole('button', {name: 'Save'}).click();

		await journalEditArticlePage.goto({siteUrl: site.friendlyUrlPath});

		await page.getByLabel('Image', {exact: true}).click();

		const iframeFolder = page
			.frameLocator('iframe[title="Select Item"]')
			.getByRole('link', {name: folderName});
		await iframeFolder.click();
		await expect(iframeFolder).toBeVisible();
	}
);

baseTest(
	'Item Selector preview shows the no-preview state for non-previewable documents',
	{
		tag: '@LPD-87398',
	},
	async ({apiHelpers, journalEditArticlePage, page, site}) => {
		const structureName = `Upload Structure ${getRandomString()}`;

		await apiHelpers.dataEngine.createStructure(
			site.id,
			getDataStructureDefinition({
				defaultLanguageId: 'en_US',
				fields: [
					{
						fieldType: 'document_library',
						name: 'Upload',
					},
				],
				name: structureName,
			})
		);

		await journalEditArticlePage.goto({
			siteUrl: site.friendlyUrlPath,
			structureName,
		});

		await page.getByLabel('File', {exact: true}).click();

		const iframe = page.frameLocator('iframe[title="Select Document"]');

		await iframe
			.locator('input[type="file"]')
			.setInputFiles(
				path.join(
					__dirname,
					'../../frontend-js-item-selector-web/main/dependencies/file.json'
				)
			);

		await expect(iframe.locator('.no-preview-image')).toBeVisible();
		await expect(iframe.locator('.no-preview-title')).toBeVisible();
		await expect(iframe.locator('.no-preview-description')).toBeVisible();
	}
);
