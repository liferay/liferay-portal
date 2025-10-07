/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {
	ObjectDefinitionAPI,
	ObjectRelationshipAPI,
} from '@liferay/object-admin-rest-client-js';
import {Page, expect, mergeTests} from '@playwright/test';
import fs from 'fs/promises';
import * as path from 'path';
import {getComparator} from 'playwright-core/lib/utils';

import {accountSettingsPagesTest} from '../../../fixtures/accountSettingsPagesTest';
import {accountsPagesTest} from '../../../fixtures/accountsPagesTest';
import {applicationsMenuPageTest} from '../../../fixtures/applicationsMenuPageTest';
import {dataApiHelpersTest} from '../../../fixtures/dataApiHelpersTest';
import {depotAdminPageTest} from '../../../fixtures/depotAdminPageTest';
import {documentLibraryPagesTest} from '../../../fixtures/documentLibraryPages.fixtures';
import {featureFlagsTest} from '../../../fixtures/featureFlagsTest';
import {isolatedSiteTest} from '../../../fixtures/isolatedSiteTest';
import {loginTest} from '../../../fixtures/loginTest';
import {objectPagesTest} from '../../../fixtures/objectPagesTest';
import {pageTemplatesPagesTest} from '../../../fixtures/pageTemplatesPagesTest';
import {pageViewModePagesTest} from '../../../fixtures/pageViewModePagesTest';
import {productMenuPageTest} from '../../../fixtures/productMenuPageTest';
import {styleBookPageTest} from '../../../fixtures/styleBookPageTest';
import {uiElementsPageTest} from '../../../fixtures/uiElementsTest';
import {usersAndOrganizationsPagesTest} from '../../../fixtures/usersAndOrganizationsPagesTest';
import {wikiPagesTest} from '../../../fixtures/wikiPagesTest';
import {HomePage} from '../../../pages/portal-web/HomePage';
import {getRandomInt} from '../../../utils/getRandomInt';
import getRandomString from '../../../utils/getRandomString';
import {openFieldset} from '../../../utils/openFieldset';
import {getTempDir} from '../../../utils/temp';
import {readFileFromZip} from '../../../utils/zip';
import {companyExportImportPageTest} from './fixtures/companyExportImportPagesTest';
import {exportImportPagesTest} from './fixtures/exportImportPagesTest';
import {stagingPageTest} from './fixtures/stagingPageTest';
import {objectDefitionRequestData} from './utils/objectDefitionRequestData';
import {openImportFieldset} from './utils/openImportFieldset';

export const test = mergeTests(
	accountSettingsPagesTest,
	accountsPagesTest,
	applicationsMenuPageTest,
	companyExportImportPageTest,
	dataApiHelpersTest,
	depotAdminPageTest,
	documentLibraryPagesTest,
	exportImportPagesTest,
	featureFlagsTest({
		'LPD-35013': {enabled: true},
		'LPD-35914': {enabled: false},
		'LPD-44307': {enabled: true},
		'LPD-44771': {enabled: true},
	}),
	isolatedSiteTest,
	loginTest(),
	objectPagesTest,
	pageViewModePagesTest,
	pageTemplatesPagesTest,
	productMenuPageTest,
	stagingPageTest,
	usersAndOrganizationsPagesTest,
	wikiPagesTest
);

export const testWithExportImportAtInstanceLevelFF = mergeTests(
	applicationsMenuPageTest,
	companyExportImportPageTest,
	exportImportPagesTest,
	dataApiHelpersTest,
	featureFlagsTest({
		'LPD-17564': {enabled: true},
		'LPD-35914': {enabled: true},
		'LPD-44307': {enabled: true},
		'LPD-44771': {enabled: true},
		'LPD-45276': {enabled: true},
	}),
	loginTest(),
	styleBookPageTest,
	uiElementsPageTest
);

const testWithDeprecationFFDisabled = mergeTests(
	exportImportPagesTest,
	dataApiHelpersTest,
	featureFlagsTest({
		'LPD-35914': {enabled: true},
		'LPD-44307': {enabled: false},
		'LPD-44771': {enabled: false},
	}),
	loginTest(),
	uiElementsPageTest
);

const testWithDeprecationFF = mergeTests(
	exportImportPagesTest,
	dataApiHelpersTest,
	featureFlagsTest({
		'LPD-35914': {enabled: true},
		'LPD-44307': {enabled: true},
		'LPD-44771': {enabled: true},
	}),
	loginTest(),
	uiElementsPageTest
);

async function getSiteHomePageScreenshot(
	page: Page,
	siteKey: string,
	{staging}: {staging: boolean}
) {
	await page.goto(`/web/${siteKey}${staging ? '-staging' : ''}`);

	const url = page.url();

	await page.goto(`${url}?p_l_mode=preview`, {waitUntil: 'load'});

	await page.waitForFunction(() => document.fonts.ready);

	const screenshot = await page.screenshot({
		fullPage: true,
		mask: [page.getByTestId('notificationsCount')],
		path: path.join(
			getTempDir(),
			`${siteKey}-${staging ? 'staging' : 'live'}.png`
		),
	});

	await page.goto(url);

	return screenshot;
}

testWithExportImportAtInstanceLevelFF(
	'Can export and import custom object entries at site level',
	async ({apiHelpers, exportImportPage}) => {
		const objectActionAPIClient =
			await apiHelpers.buildRestClient(ObjectDefinitionAPI);

		const {body: objectDefinition} =
			await objectActionAPIClient.postObjectDefinition(
				objectDefitionRequestData({scope: 'site'})
			);

		apiHelpers.data.push({
			id: objectDefinition.id,
			type: 'objectDefinition',
		});

		const objectEntry = await apiHelpers.objectEntry.postObjectEntry(
			{externalReferenceCode: '', name: 'test'},
			'c/tests/scopes/Guest'
		);

		await exportImportPage.goToExport();

		const exportName = 'MyExport-' + getRandomString();

		await exportImportPage.export(exportName, 'Tests 1 Items');

		await expect(
			exportImportPage.page
				.locator('//h2[span[normalize-space()="' + exportName + '"]]')
				.first()
				.locator('../..')
				.getByText('Successful')
		).toBeVisible();

		const exportFilePath =
			await exportImportPage.downloadExportProcess(exportName);

		const content = await readFileFromZip('C_Test.json', exportFilePath);

		const json = JSON.parse(content);

		expect(json.length).toBe(1);

		expect(
			await apiHelpers.delete(
				`${apiHelpers.baseUrl}c/tests/${objectEntry.id}`
			)
		).toBeOK();

		await exportImportPage.goToImport();

		await exportImportPage.import(exportFilePath);

		await expect(
			exportImportPage.page
				.getByText(exportName)
				.locator('../../..')
				.getByText('Successful')
		).toBeVisible();

		expect(
			await apiHelpers.get(
				`${apiHelpers.baseUrl}c/tests/scopes/Guest/by-external-reference-code/${objectEntry.externalReferenceCode}`
			)
		).toEqual(
			expect.objectContaining({
				externalReferenceCode: objectEntry.externalReferenceCode,
				name: objectEntry.name,
			})
		);
	}
);

testWithExportImportAtInstanceLevelFF(
	'Cannot import an instance scoped lar file',
	async ({apiHelpers, companyExportImportPage, exportImportPage, page}) => {
		const objectActionAPIClient =
			await apiHelpers.buildRestClient(ObjectDefinitionAPI);

		const {body: objectDefinition} =
			await objectActionAPIClient.postObjectDefinition(
				objectDefitionRequestData()
			);

		apiHelpers.data.push({
			id: objectDefinition.id,
			type: 'objectDefinition',
		});

		await apiHelpers.objectEntry.postObjectEntry(
			{externalReferenceCode: '', name: 'test'},
			'c/tests'
		);

		const homePage = new HomePage(page);

		const exportFilePath = await companyExportImportPage.export([
			'Tests 1 Items',
		]);

		await homePage.goto();

		await exportImportPage.goToImport();

		await exportImportPage.import(
			exportFilePath,
			'The LAR file contains one or more entities with a different scope.'
		);
	}
);

test(
	'Make sure we do not export-import wikiNodes if they are not selected in the export configuration screen',
	{tag: '@LPD-40988'},
	async ({
		exportImportPage,
		page,
		pageTemplatesPage,
		site,
		widgetPagePage,
		wikiPage,
	}) => {
		await wikiPage.goto(site.friendlyUrlPath);

		await wikiPage.createNewWikiNode('Wiki Node Title');

		await pageTemplatesPage.goto(site.friendlyUrlPath);

		// Create page template collection

		const pageTemplateCollectionName = getRandomString();

		await pageTemplatesPage.addPageTemplateCollection(
			pageTemplateCollectionName
		);

		await expect(
			page.getByRole('menuitem', {
				exact: true,
				name: pageTemplateCollectionName,
			})
		).toBeVisible();

		// Create widget page template

		const pageTemplateName = getRandomString();

		await pageTemplatesPage.addWidgetPageTemplate(pageTemplateName);

		await pageTemplatesPage.page.getByLabel('Add', {exact: true}).click();

		await widgetPagePage.addPortlet(
			'Web Content Display',
			'Content Management'
		);

		await wikiPage.goto(site.friendlyUrlPath);

		await exportImportPage.goToExport();

		const exportName = 'MyExport-' + getRandomString();

		await exportImportPage.export(exportName);

		await expect(
			exportImportPage.page
				.locator('//h2[span[normalize-space()="' + exportName + '"]]')
				.first()
				.locator('../..')
				.getByText('Successful')
		).toBeVisible();

		const exportFilePath =
			await exportImportPage.downloadExportProcess(exportName);

		await exportImportPage.goToImport();

		await exportImportPage.checkItemInNewlyCreatedImportProcess(
			exportFilePath,
			'Wiki'
		);
	}
);

test(
	'Can XSS with `searchContainerId` in Asset Libraries import',
	{tag: '@LPS-195766'},
	async ({apiHelpers, depotAdminPage, page}) => {
		const depotName = getRandomString();

		await apiHelpers.jsonWebServicesDepot.addDepotEntry(depotName);

		await depotAdminPage.goToDepotByName(depotName);

		await depotAdminPage.gotoImport();

		const paramName =
			'_com_liferay_exportimport_web_portlet_ImportPortlet_searchContainerId';

		const requestPromise = page.waitForRequest(
			(request) =>
				request.method() === 'GET' && request.url().includes(paramName)
		);

		const request = await requestPromise;

		const insertString = '%22%3E%3Cimg%20src=1%20onerror=alert(123)%3E';

		const [urlBase, urlParam] = request.url().split(`${paramName}=`);

		const newUrl = `${urlBase}${paramName}=${urlParam.replace(/([^&]+)/, `$1${insertString}`)}`;

		let alertTriggered = false;

		page.on('dialog', async (dialog) => {
			if (dialog.type() === 'alert') {
				alertTriggered = true;
				await dialog.dismiss();
			}
		});

		await page.goto(newUrl);

		expect(alertTriggered).toBe(false);
	}
);

test('Can import a folder with document type restrictions and workflow', async ({
	apiHelpers,
	documentLibraryEditFolderPage,
	documentLibraryPage,
	exportImportFramePage,
}) => {
	await documentLibraryPage.goto();
	await documentLibraryPage.openOptionsMenu();
	await documentLibraryPage.exportImportOptionsMenuItem.click();
	await exportImportFramePage.importLARFile(
		path.join(__dirname, 'dependencies', 'folder.portlet.lar')
	);
	await exportImportFramePage.close();
	await documentLibraryPage.goToEditFolder('LPS-205933');

	expect(
		await documentLibraryEditFolderPage.getSelectedWorkflowDefinition()
	).toBe('Single Approver@1');

	await apiHelpers.headlessDelivery.deleteSiteDocumentsFolderByExternalReferenceCode(
		'LPS-205933'
	);
});

test('Can import a lar file selecting some items to import', async ({
	exportImportPage,
}) => {
	await exportImportPage.goToExport();

	const exportName = 'MyExport-' + getRandomString();

	await exportImportPage.export(exportName);

	await expect(
		exportImportPage.page
			.locator('//h2[span[normalize-space()="' + exportName + '"]]')
			.first()
			.locator('../..')
			.getByText('Successful')
	).toBeVisible();

	const exportFilePath =
		await exportImportPage.downloadExportProcess(exportName);

	await exportImportPage.goToImport();

	await exportImportPage.import(exportFilePath);

	await expect(
		exportImportPage.page
			.getByText(exportName)
			.locator('../../..')
			.getByText('Successful')
	).toBeVisible();
});

testWithExportImportAtInstanceLevelFF(
	'Can export and import a site created with the Clarity site initializer including all exportable items',
	{tag: '@LPD-64056'},
	async ({
		apiHelpers,
		exportImportPage,
		styleBooksPage,
		uploadServletRequestSystemSettingsPage,
	}) => {
		test.setTimeout(300000);

		let exportFilePath: string;
		let exportName: string;
		let exportableItems1: Map<string, number>;
		let exportableItems2: Map<string, number>;
		let exportableItems3: Map<string, number>;
		let objectDefinition1;
		let objectDefinition2;
		let objectRelationship;
		let originalOverallMaximumUploadRequestSize: string;
		let site1: Site;
		let site2: Site;

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

		try {
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

				objectRelationship =
					await objectRelationshipAPIClient.postObjectDefinitionByExternalReferenceCodeObjectRelationship(
						objectDefinition1.externalReferenceCode,
						{
							label: {
								en_US: `objectRelationshipLabel${getRandomInt()}`,
							},
							name: `objectRelationshipName${Math.floor(Math.random() * 99)}`,
							objectDefinitionExternalReferenceCode1:
								objectDefinition1.externalReferenceCode,
							objectDefinitionExternalReferenceCode2:
								objectDefinition2.externalReferenceCode,
							objectDefinitionId1: objectDefinition1.id,
							objectDefinitionId2: objectDefinition2.id,
							objectDefinitionName2: objectDefinition2.name,
							type: 'oneToMany',
						}
					);
			});

			await test.step('Create the site 1 from the template', async () => {
				site1 = await apiHelpers.headlessSite.createSite({
					name: getRandomString(),
					templateKey: 'com.liferay.site.initializer.teaser.showcase',
					templateType: 'site-initializer',
				});

				apiHelpers.data.push({id: site1.id, type: 'site'});
			});

			await test.step('Add Object entry to the site 1', async () => {
				await apiHelpers.objectEntry.postObjectEntry(
					{
						textField: getRandomString(),
						[objectRelationship.body.name]: [
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

				exportableItems1 = await exportImportPage.getExportableItems();

				expect(exportableItems1.has(objectDefinition1.name)).toBe(true);

				expect(exportableItems1.has(objectDefinition2.name)).toBe(true);

				expect(exportableItems1.has('Style Books')).toBe(true);

				exportName = `MyExport-${getRandomString()}`;

				await exportImportPage.exportAll(exportName);

				await expect(
					exportImportPage.taskSuccessLabel(exportName)
				).toBeVisible({timeout: 60000});

				exportFilePath =
					await exportImportPage.downloadExportProcess(exportName);
			});

			await test.step('Create the site 2', async () => {
				site2 = await apiHelpers.headlessSite.createSite({
					name: getRandomString(),
				});

				apiHelpers.data.push({id: site2.id, type: 'site'});
			});

			await test.step('Import the site 1 into site 2', async () => {
				await exportImportPage.goToExport(site2.friendlyUrlPath);

				exportableItems2 = await exportImportPage.getExportableItems();

				await exportImportPage.goToImport(site2.friendlyUrlPath);

				await exportImportPage.import(exportFilePath);

				await expect(
					exportImportPage.taskSuccessLabel(exportName)
				).toBeVisible({timeout: 60000});
			});

			await test.step('Assert the exportable items from site 1 and site 2 are equal', async () => {
				await exportImportPage.goToExport(site2.friendlyUrlPath);

				exportableItems3 = await exportImportPage.getExportableItems();

				expect(exportableItems3.size).toEqual(exportableItems1.size);

				for (const [name, count] of exportableItems1.entries()) {
					if (name === 'Calendar' || name === 'Categories') {

						// TODO LPD-64899, LPD-65749

						continue;
					}
					else if (name === 'Pages') {
						expect(exportableItems3.get(name)).toBe(
							count + exportableItems2.get('Pages')
						);
					}
					else if (name === 'Style Books') {

						// TODO LPD-64905

						expect(
							exportableItems3.get(name)
						).toBeGreaterThanOrEqual(count);
					}
					else {
						expect(exportableItems3.get(name)).toBe(count);
					}
				}
			});

			// TODO LPD-65374

			/*
			await test.step('Assert the home page screenshots from site 1 and site 2 are equal', async () => {

				const comparator = getComparator('image/png');

				const buffer = comparator(
					await getSiteHomePageScreenshot(page, site1.name, {staging: false}),
					await getSiteHomePageScreenshot(page, site2.name, {staging: false})
				);

				if (buffer !== null && buffer.diff !== undefined) {
					const diffPath = path.join(getTempDir(), `${site1.name}-diff.png`);
					await fs.writeFile(diffPath, buffer.diff);
					throw new Error(
						`The site 1 and site 2 home pages differ. Check the screenshot diff at "${diffPath}".`
					);
				}
			});
			*/
		}
		finally {
			await test.step('Restore the initial maximum upload request size', async () => {
				await uploadServletRequestSystemSettingsPage.goto();

				await uploadServletRequestSystemSettingsPage.setOverallMaximumUploadRequestSize(
					{
						size: originalOverallMaximumUploadRequestSize,
					}
				);
			});
		}
	}
);

[
	{name: 'com.liferay.site.initializer.masterclass', shouldFail: true},
	{name: 'com.liferay.site.initializer.welcome'},
].forEach(({name, shouldFail}) => {
	test(`Site initializer ${name} can be exported and imported`, async ({
		apiHelpers,
		page,
		stagingPage,
	}, testInfo) => {
		testInfo.fail(shouldFail);

		const site = await apiHelpers.headlessSite.createSite({
			name,
			templateKey: name,
			templateType: 'site-initializer',
		});

		expect(site.name).toBeDefined();

		apiHelpers.data.push({id: site.id, type: 'site'});

		await stagingPage.goto(site.name);

		await stagingPage.enableLocalStaging();

		const comparator = getComparator('image/png');

		const buffer = comparator(
			await getSiteHomePageScreenshot(page, site.name, {staging: false}),
			await getSiteHomePageScreenshot(page, site.name, {staging: true})
		);

		if (buffer !== null && buffer.diff !== undefined) {
			const diffPath = path.join(getTempDir(), `${site.name}-diff.png`);
			await fs.writeFile(diffPath, buffer.diff);
			throw new Error(
				`The live and staging pages differ. Check the screenshot diff at "${diffPath}".`
			);
		}
	});
});

test('Can see corresponding elements at site level', async ({
	apiHelpers,
	exportImportPage,
}) => {
	const objectActionAPIClient =
		await apiHelpers.buildRestClient(ObjectDefinitionAPI);

	const {body: objectDefinition} =
		await objectActionAPIClient.postObjectDefinition(
			objectDefitionRequestData()
		);

	apiHelpers.data.push({id: objectDefinition.id, type: 'objectDefinition'});

	await exportImportPage.goToExport();

	const exportName = 'MyExport-' + getRandomString();

	await exportImportPage.export(exportName);

	await expect(
		exportImportPage.page
			.getByText(exportName)
			.locator('../..')
			.getByText('Successful')
	).toBeVisible();

	const exportFilePath =
		await exportImportPage.downloadExportProcess(exportName);

	await exportImportPage.goToImport();

	await exportImportPage.goToImportOptions(exportFilePath);

	await expect(
		exportImportPage.page.getByText('Comments, Ratings')
	).toBeVisible();

	await expect(
		exportImportPage.page.getByRole('group', {name: 'Pages'})
	).toBeVisible();

	await expect(exportImportPage.deleteApplicationDataCheckbox).toBeVisible();

	await openImportFieldset({
		name: 'Update Data',
		page: exportImportPage.page,
	});

	await expect(
		exportImportPage.page.getByText(
			'Mirror: All data and content inside the imported LAR is created as new the first time while maintaining a reference to the source. Subsequent imports from the same source update the entries instead of creating new entries.'
		)
	).toBeVisible();

	await expect(
		exportImportPage.page.getByText('Mirror with overwriting:')
	).toBeVisible();

	await expect(exportImportPage.page.getByText('Copy as New:')).toBeVisible();
});

testWithDeprecationFFDisabled(
	"Hide 'Delete Application Data' checkbox and 'Copy as New' radio button when deprecation FF is false",
	{tag: ['@LPD-44771', '@LPD-44307']},
	async ({apiHelpers, exportImportPage}) => {
		const objectActionAPIClient =
			await apiHelpers.buildRestClient(ObjectDefinitionAPI);

		const {body: objectDefinition} =
			await objectActionAPIClient.postObjectDefinition(
				objectDefitionRequestData()
			);

		apiHelpers.data.push({
			id: objectDefinition.id,
			type: 'objectDefinition',
		});

		await exportImportPage.goToExport();

		const exportName = 'MyExport-' + getRandomString();

		await exportImportPage.export(exportName);

		await expect(
			exportImportPage.page
				.getByText(exportName)
				.locator('../..')
				.getByText('Successful')
		).toBeVisible();

		const exportFilePath =
			await exportImportPage.downloadExportProcess(exportName);

		await exportImportPage.goToImportOptions(exportFilePath);

		await expect(
			exportImportPage.page.getByText('Copy as New:')
		).not.toBeVisible();

		await expect(
			exportImportPage.page.getByLabel('Delete Application Data')
		).not.toBeVisible();
	}
);

testWithDeprecationFF(
	'Show modal warning at site level',
	{tag: ['@LPD-54835', '@LPD-54836']},
	async ({apiHelpers, exportImportPage, page, uiElementsPage}) => {
		const objectActionAPIClient =
			await apiHelpers.buildRestClient(ObjectDefinitionAPI);

		const {body: objectDefinition} =
			await objectActionAPIClient.postObjectDefinition(
				objectDefitionRequestData({scope: 'site'})
			);

		apiHelpers.data.push({
			id: objectDefinition.id,
			type: 'objectDefinition',
		});

		await exportImportPage.goToExport();

		const exportName = 'MyExport-' + getRandomString();

		await apiHelpers.objectEntry.postObjectEntry(
			{externalReferenceCode: '', name: 'test'},
			'c/tests/scopes/Guest'
		);

		await exportImportPage.export(exportName, 'Tests 1 Items');

		await expect(
			page.getByText(exportName).locator('../..').getByText('Successful')
		).toBeVisible();

		const exportFilePath =
			await exportImportPage.downloadExportProcess(exportName);

		await exportImportPage.goToImport();

		await exportImportPage.goToImportOptions(exportFilePath);

		await openFieldset(page, 'Update Data');

		await testWithDeprecationFF.step(
			'object entry selected and "Delete Application Data Before Importing" checked',
			async () => {
				await expect(
					exportImportPage.deleteApplicationDataAlert
				).not.toBeVisible();
				await expect(
					exportImportPage.updateDataAlert
				).not.toBeVisible();

				await exportImportPage.deleteApplicationDataCheckbox.check();

				await expect(
					exportImportPage.deleteApplicationDataAlert
				).toBeVisible();

				await exportImportPage.importButton.click();

				await expect(exportImportPage.warningHeader).toBeVisible();
				await expect(
					exportImportPage.deleteApplicationDataBeforeImportingWarningLabel
				).toBeVisible();
				await expect(
					exportImportPage.updateDataMirrorWarningLabel
				).not.toBeVisible();

				await uiElementsPage.cancelButton.click();
			}
		);

		await testWithExportImportAtInstanceLevelFF.step(
			'object entry selected and "Mirror with overwriting" checked',
			async () => {
				await exportImportPage.deleteApplicationDataCheckbox.uncheck();
				await exportImportPage.mirrorWithOverwritingRadioButton.click();

				await expect(
					exportImportPage.deleteApplicationDataAlert
				).not.toBeVisible();
				await expect(exportImportPage.updateDataAlert).toBeVisible();

				await exportImportPage.importButton.click();

				await expect(
					exportImportPage.deleteApplicationDataBeforeImportingWarningLabel
				).not.toBeVisible();
				await expect(
					exportImportPage.updateDataMirrorWarningLabel
				).toBeVisible();

				await uiElementsPage.cancelButton.click();
			}
		);

		await testWithExportImportAtInstanceLevelFF.step(
			'object entry selected and "Copy as new" checked',
			async () => {
				await exportImportPage.copyAsNewRadioButton.click();

				await expect(
					exportImportPage.deleteApplicationDataAlert
				).not.toBeVisible();
				await expect(exportImportPage.updateDataAlert).toBeVisible();

				await exportImportPage.importButton.click();

				await expect(
					exportImportPage.deleteApplicationDataBeforeImportingWarningLabel
				).not.toBeVisible();
				await expect(
					exportImportPage.updateDataMirrorWarningLabel
				).toBeVisible();

				await uiElementsPage.cancelButton.click();
				await exportImportPage.copyAsNewRadioButton.click();
			}
		);

		await testWithDeprecationFF.step(
			'object entry is selected and "Delete Application Data Before Importing" and "Copy as new" checked',
			async () => {
				await exportImportPage.copyAsNewRadioButton.click();
				await exportImportPage.deleteApplicationDataCheckbox.check();

				await expect(exportImportPage.updateDataAlert).toBeVisible();
				await expect(
					exportImportPage.deleteApplicationDataAlert
				).toBeVisible();

				await exportImportPage.importButton.click();

				await expect(
					exportImportPage.deleteApplicationDataBeforeImportingWarningLabel
				).toBeVisible();
				await expect(
					exportImportPage.updateDataMirrorWarningLabel
				).toBeVisible();

				await uiElementsPage.cancelButton.click();
			}
		);

		await testWithDeprecationFF.step(
			'object entry is selected and "Delete Application Data Before Importing" and "Mirror with overwriting" checked',
			async () => {
				await exportImportPage.deleteApplicationDataCheckbox.check();
				await exportImportPage.mirrorWithOverwritingRadioButton.click();

				await expect(
					exportImportPage.deleteApplicationDataAlert
				).toBeVisible();
				await expect(exportImportPage.updateDataAlert).toBeVisible();

				await exportImportPage.importButton.click();

				await expect(
					exportImportPage.deleteApplicationDataBeforeImportingWarningLabel
				).toBeVisible();
				await expect(
					exportImportPage.updateDataMirrorWarningLabel
				).toBeVisible();

				await uiElementsPage.cancelButton.click();
			}
		);

		await testWithDeprecationFF.step('can import from modal', async () => {
			page.on('dialog', (dialog) => dialog.accept());

			await exportImportPage.deleteApplicationDataCheckbox.check();
			await exportImportPage.importButton.click();
			await exportImportPage.importModalButton.click();
			await expect(
				page
					.getByText(exportName)
					.locator('../../..')
					.getByText('Successful')
			).toBeVisible();
		});
	}
);

testWithDeprecationFFDisabled(
	'Show modal warning at site level - FF disabled',
	{tag: ['@LPD-54835', '@LPD-54836']},
	async ({apiHelpers, exportImportPage, page, uiElementsPage}) => {
		const objectActionAPIClient =
			await apiHelpers.buildRestClient(ObjectDefinitionAPI);

		const {body: objectDefinition} =
			await objectActionAPIClient.postObjectDefinition(
				objectDefitionRequestData({scope: 'site'})
			);

		apiHelpers.data.push({
			id: objectDefinition.id,
			type: 'objectDefinition',
		});

		await exportImportPage.goToExport();

		const exportName = 'MyExport-' + getRandomString();

		await apiHelpers.objectEntry.postObjectEntry(
			{externalReferenceCode: '', name: 'test'},
			'c/tests/scopes/Guest'
		);

		await exportImportPage.export(exportName, 'Tests 1 Items');

		await expect(
			page.getByText(exportName).locator('../..').getByText('Successful')
		).toBeVisible();

		const exportFilePath =
			await exportImportPage.downloadExportProcess(exportName);

		await exportImportPage.goToImport();

		await exportImportPage.goToImportOptions(exportFilePath);

		await openFieldset(page, 'Update Data');

		await testWithDeprecationFFDisabled.step(
			'object entry selected and “Mirror with overwriting” checked',
			async () => {
				await exportImportPage.mirrorWithOverwritingRadioButton.click();

				await expect(exportImportPage.updateDataAlert).toBeVisible();
				await expect(
					exportImportPage.deleteApplicationDataAlert
				).not.toBeVisible();

				await exportImportPage.importButton.click();

				await expect(exportImportPage.warningHeader).toBeVisible();
				await expect(
					exportImportPage.updateDataMirrorWarningLabel
				).toBeVisible();

				await uiElementsPage.cancelButton.click();
			}
		);
	}
);
