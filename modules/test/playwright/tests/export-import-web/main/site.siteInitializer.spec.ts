/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {
	ObjectDefinition,
	ObjectRelationship,
	ObjectRelationshipAPI,
} from '@liferay/object-admin-rest-client-js';
import {expect, mergeTests} from '@playwright/test';

import {dataApiHelpersTest} from '../../../fixtures/dataApiHelpersTest';
import {featureFlagsTest} from '../../../fixtures/featureFlagsTest';
import {loginTest} from '../../../fixtures/loginTest';
import {styleBookPageTest} from '../../../fixtures/styleBookPageTest';
import {uiElementsPageTest} from '../../../fixtures/uiElementsTest';
import {compareScreenshots} from '../../../utils/compareScreenshots';
import {getRandomInt} from '../../../utils/getRandomInt';
import getRandomString from '../../../utils/getRandomString';
import {getSiteHomePageScreenshot} from '../../../utils/getSiteHomePageScreenshot';
import {pagesPagesTest} from '../../layout-admin-web/main/fixtures/pagesPagesTest';
import {companyExportImportPageTest} from './fixtures/companyExportImportPagesTest';
import {exportImportPagesTest} from './fixtures/exportImportPagesTest';
import {stagingPageTest} from './fixtures/stagingPageTest';

const test = mergeTests(
	companyExportImportPageTest,
	dataApiHelpersTest,
	exportImportPagesTest,
	featureFlagsTest({
		'LPD-35443': {enabled: false},
		'LPD-45276': {enabled: true},
		'LPD-57655': {enabled: false},
	}),
	loginTest(),
	pagesPagesTest,
	stagingPageTest,
	styleBookPageTest,
	uiElementsPageTest
);

const testWithClaritySiteInitializerFF = mergeTests(
	test,
	featureFlagsTest({
		'LPD-35443': {enabled: false},
		'LPD-45276': {enabled: true},
		'LPD-57655': {enabled: false},
	})
);

[
	{name: 'com.liferay.site.initializer.masterclass'},
	{name: 'com.liferay.site.initializer.welcome'},
].forEach(({name}) => {
	test(`Local Staging can be enabled with site initializer ${name}`, async ({
		apiHelpers,
		page,
		stagingPage,
	}) => {
		const site = await apiHelpers.headlessAdminSite.postSite({
			name,
			templateKey: name,
			templateType: 'site-initializer',
		});

		expect(site.name).toBeDefined();

		await stagingPage.goto(site.name);

		await stagingPage.enableLocalStaging();

		compareScreenshots(
			await getSiteHomePageScreenshot(page, site.name, {
				mask: page.getByTestId('notificationsCount'),
				staging: false,
			}),
			await getSiteHomePageScreenshot(page, site.name, {
				mask: page.getByTestId('notificationsCount'),
				staging: true,
			}),
			{
				errorMessage: 'The live and staging pages differ.',
				writeDiff: true,
			}
		);
	});
});

testWithClaritySiteInitializerFF(
	'Can export and import a site created with the Clarity site initializer including all exportable items',
	{tag: '@LPD-64056'},
	async ({
		apiHelpers,
		exportImportPage,
		page,
		styleBooksPage,
		uploadServletRequestSystemSettingsPage,
		utilityPagesPage,
	}) => {
		testWithClaritySiteInitializerFF.setTimeout(300000);

		let exportFilePath: string;
		let exportableItems1: Map<string, number>;
		let exportableItems2: Map<string, number>;
		let objectDefinition1: ObjectDefinition;
		let objectDefinition2: ObjectDefinition;
		let objectRelationship: ObjectRelationship;
		let originalOverallMaximumUploadRequestSize: string;
		let site1: Site;
		let site2: Site;

		await testWithClaritySiteInitializerFF.step(
			'Increase the maximum upload request size',
			async () => {
				await uploadServletRequestSystemSettingsPage.goto();

				originalOverallMaximumUploadRequestSize =
					await uploadServletRequestSystemSettingsPage.getOverallMaximumUploadRequestSize();

				await uploadServletRequestSystemSettingsPage.setOverallMaximumUploadRequestSize(
					{
						size: '200000000',
					}
				);
			}
		);

		try {
			await testWithClaritySiteInitializerFF.step(
				'Create the Object definitions with 1-M relationship',
				async () => {
					const objectFolder =
						await apiHelpers.objectAdmin.postRandomObjectFolder();

					apiHelpers.data.push({
						id: objectFolder.id,
						type: 'objectFolder',
					});

					objectDefinition1 =
						await apiHelpers.objectAdmin.postRandomObjectDefinition(
							{
								objectFolderExternalReferenceCode:
									objectFolder.externalReferenceCode,
								scope: 'site',
								status: {code: 0},
							}
						);

					apiHelpers.data.push({
						id: objectDefinition1.id,
						type: 'objectDefinition',
					});

					objectDefinition2 =
						await apiHelpers.objectAdmin.postRandomObjectDefinition(
							{
								objectFolderExternalReferenceCode:
									objectFolder.externalReferenceCode,
								scope: 'site',
								status: {code: 0},
							}
						);

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
				}
			);

			await testWithClaritySiteInitializerFF.step(
				'Create the site 1 from the template',
				async () => {
					site1 = await apiHelpers.headlessAdminSite.postSite({
						name: getRandomString(),
						templateKey:
							'com.liferay.site.initializer.teaser.showcase',
						templateType: 'site-initializer',
					});
				}
			);

			await testWithClaritySiteInitializerFF.step(
				'Add Object entry to the site 1',
				async () => {
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
				}
			);

			await testWithClaritySiteInitializerFF.step(
				'Add a Style Book on the site 1',
				async () => {
					await styleBooksPage.goto(site1.friendlyUrlPath);

					await styleBooksPage.create(getRandomString());
				}
			);

			await testWithClaritySiteInitializerFF.step(
				'Export the site 1',
				async () => {
					await exportImportPage.goToExport(site1.friendlyUrlPath);

					exportableItems1 =
						await exportImportPage.getExportableItems();

					expect(exportableItems1.has(objectDefinition1.name)).toBe(
						true
					);

					expect(exportableItems1.has(objectDefinition2.name)).toBe(
						true
					);

					expect(exportableItems1.has('Style Books')).toBe(true);

					exportFilePath = await exportImportPage.export({
						exportAllPortlets: true,
					});
				}
			);

			await testWithClaritySiteInitializerFF.step(
				'Create the site 2',
				async () => {
					site2 = await apiHelpers.headlessAdminSite.postSite({
						name: getRandomString(),
					});
				}
			);

			await test.step('Delete the existing utility pages on site 2', async () => {
				await utilityPagesPage.goto(site2.friendlyUrlPath);

				await utilityPagesPage.deleteAllPages();
			});

			await testWithClaritySiteInitializerFF.step(
				'Import the site 1 into site 2',
				async () => {
					await exportImportPage.goToImport(site2.friendlyUrlPath);

					await exportImportPage.import({
						filePath: exportFilePath,
						timeout: 60000,
					});
				}
			);

			await testWithClaritySiteInitializerFF.step(
				'Assert the exportable items from site 1 and site 2 are equal',
				async () => {
					await exportImportPage.goToExport(site2.friendlyUrlPath);

					exportableItems2 =
						await exportImportPage.getExportableItems();

					expect(exportableItems2.size).toEqual(
						exportableItems1.size
					);

					for (const [name, count] of exportableItems1.entries()) {
						expect(exportableItems2.get(name)).toBe(count);
					}
				}
			);

			await test.step('Assert the home page screenshots from site 1 and site 2 are equal', async () => {
				compareScreenshots(
					await getSiteHomePageScreenshot(page, site1.name),
					await getSiteHomePageScreenshot(page, site2.name),
					{
						errorMessage:
							'The site 1 and site 2 home pages differ.',
						writeDiff: true,
					}
				);
			});
		}
		finally {
			await testWithClaritySiteInitializerFF.step(
				'Restore the initial maximum upload request size',
				async () => {
					await uploadServletRequestSystemSettingsPage.goto();

					await uploadServletRequestSystemSettingsPage.setOverallMaximumUploadRequestSize(
						{
							size: originalOverallMaximumUploadRequestSize,
						}
					);
				}
			);
		}
	}
);
