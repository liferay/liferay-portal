/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Page, expect, mergeTests} from '@playwright/test';

import {dataApiHelpersTest} from '../../../fixtures/dataApiHelpersTest';
import {featureFlagsTest} from '../../../fixtures/featureFlagsTest';
import {loginTest} from '../../../fixtures/loginTest';
import {compareScreenshots} from '../../../utils/compareScreenshots';
import getRandomString from '../../../utils/getRandomString';
import {getSiteHomePageScreenshot} from '../../../utils/getSiteHomePageScreenshot';
import {pagesPagesTest} from '../../layout-admin-web/main/fixtures/pagesPagesTest';
import {exportImportPagesTest} from './fixtures/exportImportPagesTest';

const test = mergeTests(
	dataApiHelpersTest,
	exportImportPagesTest,
	featureFlagsTest({
		'LPD-35443': {enabled: false},
		'LPD-45276': {enabled: true},
		'LPD-57655': {enabled: true},
	}),
	loginTest(),
	pagesPagesTest
);

[
	{name: 'com.liferay.site.initializer.masterclass'},
	{
		mask: (page: Page) => page.locator('.user-personal-bar'),
		name: 'com.liferay.site.initializer.welcome',
	},
].forEach(({mask, name}) => {
	test(
		`Can export and import a site created with the ${name} site initializer`,
		{tag: '@LPD-90359'},
		async ({
			apiHelpers,
			exportImportDataSelectionPage,
			exportImportPage,
			page,
			utilityPagesPage,
		}) => {
			let exportableItems1: Map<string, number>;
			let exportableItems2: Map<string, number>;
			let folderPath: string;
			let site1: Site;
			let site2: Site;

			const exportName = `MyExport-${getRandomString()}`;

			await test.step('Create the site 1 from the template', async () => {
				site1 = await apiHelpers.headlessAdminSite.postSite({
					name: getRandomString(),
					templateKey: name,
					templateType: 'site-initializer',
				});
			});

			await test.step('Export the site 1', async () => {
				await exportImportPage.goToExport(site1.friendlyUrlPath);

				await exportImportPage.clickNew();

				exportableItems1 =
					await exportImportDataSelectionPage.getExportableItems();

				expect(exportableItems1.size).toBeGreaterThan(0);

				await exportImportPage.nameInput.fill(exportName);

				await exportImportPage.exportButton.click();

				await expect(
					exportImportPage.taskStatusLabel(exportName)
				).toBeVisible();

				folderPath = await exportImportPage.download(exportName);
			});

			await test.step('Create the site 2', async () => {
				site2 = await apiHelpers.headlessAdminSite.postSite({
					name: getRandomString(),
				});
			});

			await test.step('Delete the existing utility pages on site 2', async () => {
				await utilityPagesPage.goto(site2.friendlyUrlPath);

				await utilityPagesPage.deleteAllPages();
			});

			await test.step('Import the site 1 into site 2', async () => {
				await exportImportPage.goToImport(site2.friendlyUrlPath);

				await exportImportPage.newButton.click();

				await exportImportPage.import({
					folderPath,
					name: exportName,
					taskStatus: 'completedWithErrors',
				});
			});

			await test.step('Assert the import only reports missing references', async () => {
				await exportImportPage.goToImportDetails(exportName);

				await expect(
					page.getByRole('cell', {name: 'Missing Reference'}).first()
				).toBeVisible();

				await expect(
					page.getByRole('cell', {exact: true, name: 'Error'})
				).toHaveCount(0);
			});

			await test.step('Assert the exportable items from site 1 and site 2 are equal', async () => {
				await exportImportPage.goToExport(site2.friendlyUrlPath);

				await exportImportPage.clickNew();

				exportableItems2 =
					await exportImportDataSelectionPage.getExportableItems();

				expect(exportableItems1).toEqual(exportableItems2);
			});

			await test.step('Assert the home page screenshots from site 1 and site 2 are equal', async () => {
				compareScreenshots(
					await getSiteHomePageScreenshot(page, site1.name, {
						mask: mask?.(page),
					}),
					await getSiteHomePageScreenshot(page, site2.name, {
						mask: mask?.(page),
					}),
					{
						errorMessage:
							'The site 1 and site 2 home pages differ.',
						writeDiff: true,
					}
				);
			});
		}
	);
});
