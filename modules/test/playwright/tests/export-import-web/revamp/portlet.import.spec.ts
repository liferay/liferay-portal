/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';
import * as path from 'path';

import {apiHelpersTest} from '../../../fixtures/apiHelpersTest';
import {documentLibraryPagesTest} from '../../../fixtures/documentLibraryPages.fixtures';
import {featureFlagsTest} from '../../../fixtures/featureFlagsTest';
import {isolatedSiteTest} from '../../../fixtures/isolatedSiteTest';
import {loginTest} from '../../../fixtures/loginTest';
import {pageViewModePagesTest} from '../../../fixtures/pageViewModePagesTest';
import {productMenuPageTest} from '../../../fixtures/productMenuPageTest';
import getRandomString from '../../../utils/getRandomString';
import {stagingPageTest} from '../main/fixtures/stagingPageTest';
import {exportImportPagesTest} from './fixtures/exportImportPagesTest';

export const test = mergeTests(
	documentLibraryPagesTest,
	productMenuPageTest,
	exportImportPagesTest,
	featureFlagsTest({
		'LPD-57655': {enabled: true},
	}),
	loginTest(),
	stagingPageTest
);

const portletTest = mergeTests(
	apiHelpersTest,
	exportImportPagesTest,
	featureFlagsTest({
		'LPD-57655': {enabled: true},
	}),
	isolatedSiteTest,
	loginTest(),
	pageViewModePagesTest
);

test('Can import using the new navigation buttons', async ({
	documentLibraryPage,
	exportImportPage,
	productMenuPage,
}) => {
	await documentLibraryPage.goto();
	await documentLibraryPage.openOptionsMenu();
	await exportImportPage.importMenuItem.click();

	await exportImportPage.newButton.click();

	await exportImportPage.import({
		folderPath: path.join(__dirname, 'dependencies', 'folder.portlet.lar'),
		name: `Test import-${getRandomString()}`,
	});

	await productMenuPage.backButton.click();

	await expect(
		documentLibraryPage.page.getByRole('link', {name: 'LPS-205933'})
	).toBeVisible();
});

portletTest(
	'Can import a portlet from its page topper menu',
	{tag: '@LPD-104237'},
	async ({apiHelpers, exportImportPage, page, site, widgetPagePage}) => {
		const layout = await apiHelpers.jsonWebServicesLayout.addLayout({
			groupId: site.id,
			title: getRandomString(),
			typeSettings:
				'layout-template-id=1_column\ncolumn-1=com_liferay_document_library_web_portlet_DLPortlet\n',
		});

		await page.goto('/web' + site.friendlyUrlPath + layout.friendlyURL);

		await widgetPagePage.clickOnAction('Documents and Media', 'Import');

		await expect(
			page.locator('.portlet-title-text', {
				hasText: 'Import Documents and Media',
			})
		).toBeVisible();

		await exportImportPage.newButton.click();

		await exportImportPage.import({
			folderPath: path.join(
				__dirname,
				'dependencies',
				'documents.portlet.lar'
			),
			name: getRandomString(),
		});

		await page.goto('/web' + site.friendlyUrlPath + layout.friendlyURL);

		await expect(
			page.getByRole('link', {name: 'LPD-104237'})
		).toBeVisible();
	}
);
