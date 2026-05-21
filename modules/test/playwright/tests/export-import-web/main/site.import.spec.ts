/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {ObjectDefinitionAPI} from '@liferay/object-admin-rest-client-js';
import {expect, mergeTests} from '@playwright/test';
import * as path from 'path';

import {accountSettingsPagesTest} from '../../../fixtures/accountSettingsPagesTest';
import {accountsPagesTest} from '../../../fixtures/accountsPagesTest';
import {dataApiHelpersTest} from '../../../fixtures/dataApiHelpersTest';
import {depotAdminPageTest} from '../../../fixtures/depotAdminPageTest';
import {documentLibraryPagesTest} from '../../../fixtures/documentLibraryPages.fixtures';
import {featureFlagsTest} from '../../../fixtures/featureFlagsTest';
import {globalMenuPagesTest} from '../../../fixtures/globalMenuPagesTest';
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
import {DataApiHelpers} from '../../../helpers/ApiHelpers';
import {liferayConfig} from '../../../liferay.config';
import {HomePage} from '../../../pages/portal-web/HomePage';
import {getRandomInt} from '../../../utils/getRandomInt';
import getRandomString from '../../../utils/getRandomString';
import {normalizeRestPath} from '../../../utils/normalizeRestPath';
import {openFieldset} from '../../../utils/openFieldset';
import {performLoginViaApi} from '../../../utils/performLogin';
import {PORTLET_URLS} from '../../../utils/portletUrls';
import {readFileFromZip} from '../../../utils/zip';
import {companyExportImportPageTest} from './fixtures/companyExportImportPagesTest';
import {exportImportPagesTest} from './fixtures/exportImportPagesTest';
import {stagingPageTest} from './fixtures/stagingPageTest';
import {openImportFieldset} from './utils/openImportFieldset';

export const test = mergeTests(
	accountSettingsPagesTest,
	accountsPagesTest,
	companyExportImportPageTest,
	dataApiHelpersTest,
	depotAdminPageTest,
	documentLibraryPagesTest,
	exportImportPagesTest,
	featureFlagsTest({
		'LPD-17564': {enabled: true},
		'LPD-35013': {enabled: true},
		'LPD-35443': {enabled: false},
		'LPD-44307': {enabled: true},
		'LPD-44771': {enabled: true},
		'LPD-45276': {enabled: true},
		'LPD-76864': {enabled: true},
	}),
	globalMenuPagesTest,
	isolatedSiteTest,
	loginTest(),
	objectPagesTest,
	pageTemplatesPagesTest,
	pageViewModePagesTest,
	productMenuPageTest,
	stagingPageTest,
	styleBookPageTest,
	usersAndOrganizationsPagesTest,
	uiElementsPageTest,
	wikiPagesTest
);

const testWithDeprecationFFDisabled = mergeTests(
	exportImportPagesTest,
	dataApiHelpersTest,
	featureFlagsTest({
		'LPD-35443': {enabled: false},
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
		'LPD-35443': {enabled: false},
		'LPD-44307': {enabled: true},
		'LPD-44771': {enabled: true},
	}),
	loginTest(),
	uiElementsPageTest
);

test('Can export and import custom object entries at site level', async ({
	apiHelpers,
	exportImportPage,
}) => {
	const objectDefinition =
		await apiHelpers.objectAdmin.postRandomObjectDefinition({
			scope: 'site',
			status: {code: 0},
		});

	apiHelpers.data.push({
		id: objectDefinition.id,
		type: 'objectDefinition',
	});

	const objectEntry = await apiHelpers.objectEntry.postObjectEntry(
		{externalReferenceCode: '', textField: objectDefinition.name},
		`${normalizeRestPath(objectDefinition.restContextPath)}/scopes/Guest`
	);

	await exportImportPage.goToExport();

	const exportFilePath = await exportImportPage.export({
		portletLabels: [`${objectDefinition.name} 1 Items`],
	});

	const content = await readFileFromZip(
		`${objectDefinition.externalReferenceCode}.json`,
		exportFilePath
	);

	const json = JSON.parse(content);

	expect(json.length).toBe(1);
	expect(
		await apiHelpers.delete(
			`${apiHelpers.baseUrl}${normalizeRestPath(objectDefinition.restContextPath)}/${objectEntry.id}`
		)
	).toBeOK();

	await exportImportPage.goToImport();

	await exportImportPage.import({filePath: exportFilePath});

	expect(
		await apiHelpers.get(
			`${apiHelpers.baseUrl}${normalizeRestPath(objectDefinition.restContextPath)}/scopes/Guest/by-external-reference-code/${objectEntry.externalReferenceCode}`
		)
	).toEqual(
		expect.objectContaining({
			externalReferenceCode: objectEntry.externalReferenceCode,
			textField: objectEntry.textField,
		})
	);
});

test('Cannot import an instance scoped lar file', async ({
	apiHelpers,
	exportImportPage,
	globalMenuPage,
	page,
}) => {
	const objectDefinition =
		await apiHelpers.objectAdmin.postRandomObjectDefinition({
			status: {code: 0},
		});

	apiHelpers.data.push({
		id: objectDefinition.id,
		type: 'objectDefinition',
	});

	await apiHelpers.objectEntry.postObjectEntry(
		{externalReferenceCode: '', textField: objectDefinition.name},
		`${normalizeRestPath(objectDefinition.restContextPath)}`
	);

	const homePage = new HomePage(page);

	await globalMenuPage.goToApplications('Export');

	const exportFilePath = await exportImportPage.export({
		portletLabels: [`${objectDefinition.name} 1 Items`],
	});

	await homePage.goto();

	await exportImportPage.goToImport();

	await exportImportPage.import({
		expectedUploadErrorMessage:
			'The LAR file contains one or more entities with a different scope.',
		filePath: exportFilePath,
	});
});

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

		const exportFilePath = await exportImportPage.export();

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

	const exportFilePath = await exportImportPage.export();

	await exportImportPage.goToImport();

	await exportImportPage.import({filePath: exportFilePath});
});

test('Can only import site level custom object entries when their definitions are already in the system', async ({
	apiHelpers,
	exportImportPage,
}) => {
	const objectDefinitionExternalReferenceCode = `ObjectDefinition${getRandomInt()}`;

	const objectDefinition =
		await apiHelpers.objectAdmin.postRandomObjectDefinition({
			className: `com.liferay.object.model.ObjectDefinition#${objectDefinitionExternalReferenceCode}`,
			objectDefinitionExternalReferenceCode,
			scope: 'site',
			status: {code: 0},
		});

	const applicationName = `${normalizeRestPath(objectDefinition.restContextPath)}`;

	let objectEntry: ObjectEntry;

	try {
		objectEntry = await apiHelpers.objectEntry.postObjectEntry(
			{externalReferenceCode: 'testERC', textField: 'test'},
			`${applicationName}/scopes/Guest`
		);
	}
	catch {

		// Ensure cleanup if test execution stops before removing the object definition.

		apiHelpers.data.push({
			id: objectDefinition.id,
			type: 'objectDefinition',
		});
	}

	await exportImportPage.goToExport();

	const exportFilePath = await exportImportPage.export({
		portletLabels: [`${objectDefinitionExternalReferenceCode} 1 Items`],
	});

	const objectDefinitionAPIClient =
		await apiHelpers.buildRestClient(ObjectDefinitionAPI);

	await objectDefinitionAPIClient.deleteObjectDefinition(objectDefinition.id);

	await exportImportPage.goToImport();

	await exportImportPage.import({
		expectedUploadErrorMessage: `The Data Handler for the "${objectDefinitionExternalReferenceCode}" portlet is missing from the system.`,
		filePath: exportFilePath,
	});

	await test.step('Recreate the object definition', async () => {
		const objectDefinition2 =
			await apiHelpers.objectAdmin.postRandomObjectDefinition({
				className: `com.liferay.object.model.ObjectDefinition#${objectDefinitionExternalReferenceCode}`,
				objectDefinitionExternalReferenceCode,
				scope: 'site',
				status: {code: 0},
			});

		apiHelpers.data.push({
			id: objectDefinition2.id,
			type: 'objectDefinition',
		});
	});

	await exportImportPage.goToImport();

	await exportImportPage.import({
		filePath: exportFilePath,
	});

	expect(
		await apiHelpers.get(
			`${apiHelpers.baseUrl}${applicationName}/scopes/Guest/by-external-reference-code/${objectEntry.externalReferenceCode}`
		)
	).toEqual(
		expect.objectContaining({
			externalReferenceCode: objectEntry.externalReferenceCode,
			textField: objectEntry.textField,
		})
	);
});

test('Can see corresponding elements at site level', async ({
	apiHelpers,
	exportImportPage,
}) => {
	const objectDefinition =
		await apiHelpers.objectAdmin.postRandomObjectDefinition({
			status: {code: 0},
		});

	apiHelpers.data.push({
		id: objectDefinition.id,
		type: 'objectDefinition',
	});

	await exportImportPage.goToExport();

	const exportFilePath = await exportImportPage.export();

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
		const objectDefinition =
			await apiHelpers.objectAdmin.postRandomObjectDefinition({
				status: {code: 0},
			});

		apiHelpers.data.push({
			id: objectDefinition.id,
			type: 'objectDefinition',
		});

		await exportImportPage.goToExport();

		const exportFilePath = await exportImportPage.export();

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
		const objectDefinition =
			await apiHelpers.objectAdmin.postRandomObjectDefinition({
				scope: 'site',
				status: {code: 0},
			});

		apiHelpers.data.push({
			id: objectDefinition.id,
			type: 'objectDefinition',
		});

		await exportImportPage.goToExport();

		const exportName = 'MyExport-' + getRandomString();

		await apiHelpers.objectEntry.postObjectEntry(
			{externalReferenceCode: '', textField: objectDefinition.name},
			`${normalizeRestPath(objectDefinition.restContextPath)}/scopes/Guest`
		);

		const exportFilePath = await exportImportPage.export({
			portletLabels: [`${objectDefinition.name} 1 Items`],
			taskName: exportName,
		});

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

		await test.step('object entry selected and "Mirror with overwriting" checked', async () => {
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
		});

		await test.step('object entry selected and "Copy as new" checked', async () => {
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
		});

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
			await exportImportPage
				.taskStatusLabel(exportName, 'success')
				.waitFor();
		});
	}
);

testWithDeprecationFFDisabled(
	'Show modal warning at site level - FF disabled',
	{tag: ['@LPD-54835', '@LPD-54836']},
	async ({apiHelpers, exportImportPage, page, uiElementsPage}) => {
		const objectDefinition =
			await apiHelpers.objectAdmin.postRandomObjectDefinition({
				scope: 'site',
				status: {code: 0},
			});

		apiHelpers.data.push({
			id: objectDefinition.id,
			type: 'objectDefinition',
		});

		await exportImportPage.goToExport();

		await apiHelpers.objectEntry.postObjectEntry(
			{externalReferenceCode: '', textField: objectDefinition.name},
			`${normalizeRestPath(objectDefinition.restContextPath)}/scopes/Guest`
		);

		const exportFilePath = await exportImportPage.export({
			portletLabels: [`${objectDefinition.name} 1 Items`],
		});

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

testWithDeprecationFF(
	'Can import the default site on a new instance twice',
	async ({apiHelpers, exportImportPage, featureFlags, page}) => {
		test.slow();

		await exportImportPage.goToExport();

		const exportFilePath = await exportImportPage.export();

		const virtualInstance =
			await apiHelpers.headlessPortalInstance.addVirtualInstance({
				domain: 'liferay.com',
				portalInstanceId: 'www.able.com',
				virtualHost: 'www.able.com',
			});
		apiHelpers.data.push({
			id: virtualInstance.portalInstanceId,
			type: 'virtual-instance',
		});

		await performLoginViaApi({
			loginUrl: `http://www.able.com:${liferayConfig.environment.port}`,
			page,
			screenName: 'test',
		});

		const virtualInstanceApiHelpers = new DataApiHelpers(
			page,
			`http://www.able.com:${liferayConfig.environment.port}`
		);

		for (const featureFlag of featureFlags) {
			await virtualInstanceApiHelpers.featureFlag.updateFeatureFlag(
				featureFlag.key,
				featureFlag.enabled,
				`http://www.able.com:${liferayConfig.environment.port}`
			);
		}

		const site = await virtualInstanceApiHelpers.headlessAdminSite.postSite(
			{
				name: getRandomString(),
			}
		);

		await page.goto(
			`http://www.able.com:${liferayConfig.environment.port}/group${site.friendlyUrlPath}${PORTLET_URLS.import}`
		);
		await exportImportPage.importByDefault(exportFilePath);
		await exportImportPage.importByDefault(exportFilePath);
	}
);
