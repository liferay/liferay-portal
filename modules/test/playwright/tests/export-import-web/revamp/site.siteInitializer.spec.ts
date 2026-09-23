/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {
	ObjectDefinition,
	ObjectRelationship,
	ObjectRelationshipAPI,
} from '@liferay/object-admin-rest-client-js';
import {Page, expect, mergeTests} from '@playwright/test';

import {dataApiHelpersTest} from '../../../fixtures/dataApiHelpersTest';
import {featureFlagsTest} from '../../../fixtures/featureFlagsTest';
import {loginTest} from '../../../fixtures/loginTest';
import {styleBookPageTest} from '../../../fixtures/styleBookPageTest';
import {compareScreenshots} from '../../../utils/compareScreenshots';
import {getRandomInt} from '../../../utils/getRandomInt';
import getRandomString from '../../../utils/getRandomString';
import {getSiteHomePageScreenshot} from '../../../utils/getSiteHomePageScreenshot';
import {waitForFDS} from '../../../utils/waitFor';
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
	pagesPagesTest,
	styleBookPageTest
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

				await exportImportPage.filterReportBy(
					'Type',
					'Missing Reference'
				);

				await waitForFDS({page});

				await expect(
					page
						.getByRole('cell', {
							exact: true,
							name: 'Missing Reference',
						})
						.first()
				).toBeVisible();

				await exportImportPage.excludeReportFilter();

				await waitForFDS({empty: true, page});

				await expect(page.getByText('No Results Found')).toBeVisible();

				await exportImportPage.removeReportFilter();
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

test(
	'Can export and import a site created with the Clarity site initializer including all exportable items',
	{tag: '@LPD-64056'},
	async ({
		apiHelpers,
		exportImportDataSelectionPage,
		exportImportPage,
		styleBooksPage,
		uploadServletRequestSystemSettingsPage,
		utilityPagesPage,
	}) => {
		test.setTimeout(300000);

		let exportableItems1: Map<string, number>;
		let exportableItems2: Map<string, number>;
		let folderPath: string;
		let objectDefinition1: ObjectDefinition;
		let objectDefinition2: ObjectDefinition;
		let objectRelationship: ObjectRelationship;
		let originalOverallMaximumUploadRequestSize: string;
		let site1: Site;
		let site2: Site;

		const exportName = `MyExport-${getRandomString()}`;

		await test.step('Increase the maximum upload request size', async () => {
			await uploadServletRequestSystemSettingsPage.goto();

			originalOverallMaximumUploadRequestSize =
				await uploadServletRequestSystemSettingsPage.getOverallMaximumUploadRequestSize();

			await uploadServletRequestSystemSettingsPage.setOverallMaximumUploadRequestSize(
				{
					size: '200000000',
				}
			);
		});

		await test.step('Create the Object definitions with 1-M relationship', async () => {
			const objectFolder =
				await apiHelpers.objectAdmin.postRandomObjectFolder();

			apiHelpers.data.push({
				id: objectFolder.id,
				type: 'objectFolder',
			});

			objectDefinition1 =
				await apiHelpers.objectAdmin.postRandomObjectDefinition({
					objectFolderExternalReferenceCode:
						objectFolder.externalReferenceCode,
					scope: 'site',
					status: {code: 0},
				});

			apiHelpers.data.push({
				id: objectDefinition1.id,
				type: 'objectDefinition',
			});

			objectDefinition2 =
				await apiHelpers.objectAdmin.postRandomObjectDefinition({
					objectFolderExternalReferenceCode:
						objectFolder.externalReferenceCode,
					scope: 'site',
					status: {code: 0},
				});

			apiHelpers.data.push({
				id: objectDefinition2.id,
				type: 'objectDefinition',
			});

			const objectRelationshipAPIClient =
				await apiHelpers.buildRestClient(ObjectRelationshipAPI);

			({body: objectRelationship} =
				await objectRelationshipAPIClient.postObjectDefinitionByExternalReferenceCodeObjectRelationship(
					objectDefinition1.externalReferenceCode,
					{
						label: {
							en_US: `objectRelationshipLabel${getRandomInt() % 100}`,
						},
						name: `objectRelationshipName${getRandomInt() % 100}`,
						objectDefinitionExternalReferenceCode1:
							objectDefinition1.externalReferenceCode,
						objectDefinitionExternalReferenceCode2:
							objectDefinition2.externalReferenceCode,
						objectDefinitionId1: objectDefinition1.id,
						objectDefinitionId2: objectDefinition2.id,
						objectDefinitionName2: objectDefinition2.name,
						type: 'oneToMany',
					}
				));
		});

		await test.step('Create the site 1 from the template', async () => {
			site1 = await apiHelpers.headlessAdminSite.postSite({
				name: getRandomString(),
				templateKey: 'com.liferay.site.initializer.teaser.showcase',
				templateType: 'site-initializer',
			});
		});

		await test.step('Add Object entry to the site 1', async () => {
			await apiHelpers.objectEntry.postObjectEntry(
				{
					textField: getRandomString(),
					[objectRelationship.name]: [
						{
							textField: getRandomString(),
						},
					],
				},
				`c/${objectDefinition1.name.toLowerCase()}s/scopes/${site1.name}`
			);
		});

		await test.step('Add a Style Book on the site 1', async () => {
			await styleBooksPage.goto(site1.friendlyUrlPath);

			await styleBooksPage.create(getRandomString());
		});

		await test.step('Export the site 1', async () => {
			await exportImportPage.goToExport(site1.friendlyUrlPath);

			await exportImportPage.clickNew();

			exportableItems1 =
				await exportImportDataSelectionPage.getExportableItems();

			expect(exportableItems1.has(objectDefinition1.name)).toBe(true);

			expect(exportableItems1.has(objectDefinition2.name)).toBe(true);

			expect(exportableItems1.has('Style Books')).toBe(true);

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

		await test.step('Assert the exportable items from site 1 and site 2 are equal', async () => {
			await exportImportPage.goToExport(site2.friendlyUrlPath);

			await exportImportPage.clickNew();

			exportableItems2 =
				await exportImportDataSelectionPage.getExportableItems();

			expect(exportableItems2.size).toEqual(exportableItems1.size);

			for (const [name, count] of exportableItems1.entries()) {
				expect(exportableItems2.get(name)).toBe(count);
			}
		});

		await test.step('Restore the initial maximum upload request size', async () => {
			await uploadServletRequestSystemSettingsPage.goto();

			await uploadServletRequestSystemSettingsPage.setOverallMaximumUploadRequestSize(
				{
					size: originalOverallMaximumUploadRequestSize,
				}
			);
		});
	}
);
