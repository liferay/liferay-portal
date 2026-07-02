/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {
	ObjectDefinitionAPI,
	ObjectRelationshipAPI,
} from '@liferay/object-admin-rest-client-js';
import {expect, mergeTests} from '@playwright/test';

import {dataApiHelpersTest} from '../../../fixtures/dataApiHelpersTest';
import {depotAdminPageTest} from '../../../fixtures/depotAdminPageTest';
import {documentLibraryPagesTest} from '../../../fixtures/documentLibraryPages.fixtures';
import {featureFlagsTest} from '../../../fixtures/featureFlagsTest';
import {globalMenuPagesTest} from '../../../fixtures/globalMenuPagesTest';
import {isolatedSiteTest} from '../../../fixtures/isolatedSiteTest';
import {loginTest} from '../../../fixtures/loginTest';
import {objectPagesTest} from '../../../fixtures/objectPagesTest';
import {pageEditorPagesTest} from '../../../fixtures/pageEditorPagesTest';
import {pageTemplatesPagesTest} from '../../../fixtures/pageTemplatesPagesTest';
import {wikiPagesTest} from '../../../fixtures/wikiPagesTest';
import {getRandomInt} from '../../../utils/getRandomInt';
import {normalizeRestPath} from '../../../utils/normalizeRestPath';
import performLogin, {
	performLogout,
	performUserSwitch,
	userData,
} from '../../../utils/performLogin';
import {readFileFromZip} from '../../../utils/zip';
import {companyExportImportPageTest} from './fixtures/companyExportImportPagesTest';
import {exportImportPagesTest} from './fixtures/exportImportPagesTest';
import {portletExportImportPageTest} from './fixtures/portletExportImportPageTest';
import {stagingPageTest} from './fixtures/stagingPageTest';
import {createUserAssignRolesAndLogin} from './utils/createUserAssignRolesAndLogin';
import {toDateRangeDate, toDateRangeTime} from './utils/dateRangeUtil';

export const test = mergeTests(
	companyExportImportPageTest,
	dataApiHelpersTest,
	depotAdminPageTest,
	documentLibraryPagesTest,
	exportImportPagesTest,
	featureFlagsTest({
		'LPD-35013': {enabled: true},
		'LPD-57655': {enabled: false},
	}),
	globalMenuPagesTest,
	isolatedSiteTest,
	loginTest(),
	objectPagesTest,
	pageEditorPagesTest,
	pageTemplatesPagesTest,
	portletExportImportPageTest,
	stagingPageTest,
	wikiPagesTest
);

test('Can export and import custom object entries at instance level', async ({
	apiHelpers,
	companyExportImportPage,
	exportImportPage,
	globalMenuPage,
}) => {
	const objectDefinition =
		await apiHelpers.objectAdmin.postRandomObjectDefinition({
			status: {code: 0},
		});

	apiHelpers.data.push({
		id: objectDefinition.id,
		type: 'objectDefinition',
	});

	const objectEntry = await apiHelpers.objectEntry.postObjectEntry(
		{externalReferenceCode: '', textField: objectDefinition.name},
		`${normalizeRestPath(objectDefinition.restContextPath)}`
	);

	await globalMenuPage.goToApplications('Export');

	const exportFilePath = await exportImportPage.export({
		portletLabels: [`${objectDefinition.name} 1 Items`],
	});

	const content = await readFileFromZip(
		`${objectDefinition.externalReferenceCode}.json`,
		exportFilePath
	);

	const json = JSON.parse(content);

	expect(json.length).toBe(1);
	expect(json[0]).not.toHaveProperty('permissions');

	expect(
		await apiHelpers.delete(
			`${apiHelpers.baseUrl}${normalizeRestPath(objectDefinition.restContextPath)}/${objectEntry.id}`
		)
	).toBeOK();

	await companyExportImportPage.import({
		filePath: exportFilePath,
	});

	expect(
		await apiHelpers.get(
			`${apiHelpers.baseUrl}${normalizeRestPath(objectDefinition.restContextPath)}/by-external-reference-code/${objectEntry.externalReferenceCode}`
		)
	).toEqual(
		expect.objectContaining({
			externalReferenceCode: objectEntry.externalReferenceCode,
			textField: objectEntry.textField,
		})
	);
});

test('Can import account restricted entry when account does and does not exist in environment', async ({
	apiHelpers,
	companyExportImportPage,
	exportImportPage,
	globalMenuPage,
}) => {
	const account = await apiHelpers.headlessAdminUser.postAccount();

	apiHelpers.data.push({
		id: account.id,
		type: 'account',
	});

	const objectDefinition =
		await apiHelpers.objectAdmin.postRandomObjectDefinition({
			status: {code: 0},
		});

	const objectRelationshipAPIClient = await apiHelpers.buildRestClient(
		ObjectRelationshipAPI
	);

	const {body: objectRelationship} =
		await objectRelationshipAPIClient.postObjectDefinitionByExternalReferenceCodeObjectRelationship(
			'L_ACCOUNT',
			{
				label: {
					en_US: 'objectRelationshipLabel' + getRandomInt(),
				},
				name: 'objectRelationshipName' + Math.floor(Math.random() * 99),
				objectDefinitionExternalReferenceCode1: 'L_ACCOUNT',
				objectDefinitionExternalReferenceCode2:
					objectDefinition.externalReferenceCode,
				type: 'oneToMany',
			}
		);

	apiHelpers.data.push({
		id: objectRelationship.id,
		type: 'objectRelationship',
	});

	const accountEntryERC = `r_${objectRelationship.name}_accountEntryERC`;
	const accountEntryId = `r_${objectRelationship.name}_accountEntryId`;
	const applicationName = `${normalizeRestPath(objectDefinition.restContextPath)}`;

	const objectDefinitionAPIClient =
		await apiHelpers.buildRestClient(ObjectDefinitionAPI);

	await objectDefinitionAPIClient.patchObjectDefinition(objectDefinition.id, {
		accountEntryRestricted: true,
		accountEntryRestrictedObjectFieldName: accountEntryId,
	});

	const objectEntry = await apiHelpers.objectEntry.postObjectEntry(
		{
			[accountEntryERC]: account.externalReferenceCode.toString(),
			[accountEntryId]: account.id.toString(),
		},
		applicationName
	);

	await globalMenuPage.goToApplications('Export');

	const exportFilePath = await exportImportPage.export({
		portletLabels: [`${objectDefinition.name} 1 Items`],
	});

	await test.step('assert entry is imported with account relationship properties when it exists', async () => {
		await apiHelpers.delete(
			`${apiHelpers.baseUrl}${applicationName}/${objectEntry.id}`
		);

		expect(
			await apiHelpers.objectEntry.getObjectEntryByExternalReferenceCode({
				applicationName,
				externalReferenceCode: objectEntry.externalReferenceCode,
			})
		).toEqual({status: 'NOT_FOUND'});

		await companyExportImportPage.import({
			filePath: exportFilePath,
		});

		const importedObjectEntry = await apiHelpers.get(
			`${apiHelpers.baseUrl}${applicationName}/by-external-reference-code/${objectEntry.externalReferenceCode}`
		);

		expect(importedObjectEntry).toMatchObject({
			[accountEntryERC]: account.externalReferenceCode,
			[accountEntryId]: account.id,
		});

		await apiHelpers.delete(
			`${apiHelpers.baseUrl}${applicationName}/${importedObjectEntry.id}`
		);

		expect(
			await apiHelpers.objectEntry.getObjectEntryByExternalReferenceCode({
				applicationName,
				externalReferenceCode:
					importedObjectEntry.externalReferenceCode,
			})
		).toEqual({status: 'NOT_FOUND'});
	});

	await test.step('assert entry is imported with account relationship properties when it does not exist', async () => {
		await apiHelpers.headlessAdminUser.deleteAccount(account.id);

		expect(
			await apiHelpers.headlessAdminUser.getAccountByName(account.name)
		).toBe(undefined);

		await companyExportImportPage.import({
			filePath: exportFilePath,
		});

		const newImportedObjectEntry = await apiHelpers.get(
			`${apiHelpers.baseUrl}${applicationName}/by-external-reference-code/${objectEntry.externalReferenceCode}`
		);

		const importedAccount =
			await apiHelpers.headlessAdminUser.getAccountByName(account.name);

		expect(newImportedObjectEntry).toMatchObject({
			[accountEntryERC]: importedAccount.externalReferenceCode,
			[accountEntryId]: importedAccount.id,
		});
	});
});

test('Can import custom and system objects entries at instance level using date filter', async ({
	apiHelpers,
	companyExportImportPage,
	exportImportPage,
	globalMenuPage,
}) => {
	const objectDefinition =
		await apiHelpers.objectAdmin.postRandomObjectDefinition({
			status: {code: 0},
		});

	apiHelpers.data.push({
		id: objectDefinition.id,
		type: 'objectDefinition',
	});

	const applicationName = `${normalizeRestPath(objectDefinition.restContextPath)}`;

	const objectEntry = await apiHelpers.objectEntry.postObjectEntry(
		{externalReferenceCode: '', textField: objectDefinition.name},
		applicationName
	);

	const {
		items: cookiesObjectEntries,
		totalCount: cookiesObjectEntriesTotalCount,
	} = await apiHelpers.get(
		`${apiHelpers.baseUrl}functional-cookies-entries/`
	);

	const {
		dateCreated: cookiesObjectEntryCreationDate,
		id: cookiesObjectEntryId,
	} = cookiesObjectEntries[0];

	await test.step('export functional cookie entries using date range filter', async () => {
		const startDate = new Date(cookiesObjectEntryCreationDate);

		startDate.setUTCDate(startDate.getUTCDate() - 1);

		const endDate = new Date(cookiesObjectEntryCreationDate);

		endDate.setUTCMinutes(endDate.getUTCMinutes() + 1);

		await globalMenuPage.goToApplications('Export');

		const functionalCookieEntriesExportFilePath =
			await exportImportPage.export({
				dateFilter: {
					endDate: toDateRangeDate(endDate),
					endTime: toDateRangeTime(endDate),
					startDate: toDateRangeDate(startDate),
					startTime: toDateRangeTime(startDate),
				},
				portletLabels: [
					`Functional Cookie Entries ${cookiesObjectEntriesTotalCount} Items`,
					`${objectDefinition.name} 1 Items`,
				],
			});

		await apiHelpers.delete(
			`${apiHelpers.baseUrl}${applicationName}/${objectEntry.id}`
		);

		await apiHelpers.delete(
			`${apiHelpers.baseUrl}functional-cookies-entries/${cookiesObjectEntryId}`
		);

		await companyExportImportPage.import({
			filePath: functionalCookieEntriesExportFilePath,
		});

		const {totalCount: importedCookiesObjectEntriesTotalCount} =
			await apiHelpers.get(
				`${apiHelpers.baseUrl}functional-cookies-entries/`
			);

		const {totalCount: importedCustomObjectEntriesTotalCount} =
			await apiHelpers.get(
				`${apiHelpers.baseUrl}${normalizeRestPath(objectDefinition.restContextPath)}`
			);

		expect(importedCookiesObjectEntriesTotalCount).toBe(
			cookiesObjectEntriesTotalCount
		);

		expect(importedCustomObjectEntriesTotalCount).toBe(0);
	});

	await test.step('export all entries using last 12 hours filter', async () => {
		await apiHelpers.objectEntry.postObjectEntry(
			{externalReferenceCode: '', textField: objectDefinition.name},
			`${normalizeRestPath(objectDefinition.restContextPath)}`
		);

		await globalMenuPage.goToApplications('Export');

		const allEntriesExportFilePath = await exportImportPage.export({
			dateFilter: {rangeLast: '12 Hours'},
			portletLabels: [
				`Functional Cookie Entries ${cookiesObjectEntriesTotalCount} Items`,
				`${objectDefinition.name} 1 Items`,
			],
		});

		await apiHelpers.delete(
			`${apiHelpers.baseUrl}functional-cookies-entries/${cookiesObjectEntryId}`
		);

		await apiHelpers.delete(
			`${apiHelpers.baseUrl}${applicationName}/${objectEntry.id}`
		);
		await companyExportImportPage.import({
			filePath: allEntriesExportFilePath,
		});

		const {totalCount: importedCookiesObjectEntriesTotalCount} =
			await apiHelpers.get(
				`${apiHelpers.baseUrl}functional-cookies-entries/`
			);

		const {totalCount: importedCustomObjectEntriesTotalCount} =
			await apiHelpers.get(
				`${apiHelpers.baseUrl}${normalizeRestPath(objectDefinition.restContextPath)}`
			);

		expect(importedCookiesObjectEntriesTotalCount).toBe(
			cookiesObjectEntriesTotalCount
		);

		expect(importedCustomObjectEntriesTotalCount).toBe(1);
	});
});

test('Can import custom object entries at instance level with or without permissions based on selection', async ({
	apiHelpers,
	companyExportImportPage,
	exportImportPage,
	globalMenuPage,
}) => {
	const objectDefinition =
		await apiHelpers.objectAdmin.postRandomObjectDefinition({
			status: {code: 0},
		});

	apiHelpers.data.push({
		id: objectDefinition.id,
		type: 'objectDefinition',
	});

	let objectEntry = await apiHelpers.objectEntry.postObjectEntry(
		{
			externalReferenceCode: '',
			permissions: [
				{
					actionIds: ['VIEW'],
					roleName: 'Guest',
				},
			],
			textField: 'test',
		},
		`${normalizeRestPath(objectDefinition.restContextPath)}`
	);

	// Export with permissions

	await globalMenuPage.goToApplications('Export');

	const exportFilePath = await exportImportPage.export({
		includePermissions: true,
		portletLabels: [`${objectDefinition.name} 1 Items`],
	});

	// Import with permissions

	await apiHelpers.delete(
		`${apiHelpers.baseUrl}${normalizeRestPath(objectDefinition.restContextPath)}/${objectEntry.id}`
	);

	expect(
		await apiHelpers.objectEntry.getObjectEntryByExternalReferenceCode({
			applicationName: `${normalizeRestPath(objectDefinition.restContextPath)}`,
			externalReferenceCode: objectEntry.externalReferenceCode,
		})
	).toEqual({status: 'NOT_FOUND'});

	await companyExportImportPage.import({
		filePath: exportFilePath,
		includePermissions: true,
	});

	objectEntry = await apiHelpers.get(
		`${apiHelpers.baseUrl}${normalizeRestPath(objectDefinition.restContextPath)}/by-external-reference-code/${objectEntry.externalReferenceCode}/?nestedFields=permissions`
	);

	expect(objectEntry).toEqual(
		expect.objectContaining({
			permissions: [
				{
					actionIds: ['VIEW'],
					roleExternalReferenceCode: expect.any(String),
					roleName: 'Guest',
					roleType: 'regular',
				},
			],
		})
	);

	// Import without permissions

	await apiHelpers.delete(
		`${apiHelpers.baseUrl}${normalizeRestPath(objectDefinition.restContextPath)}/${objectEntry.id}`
	);

	expect(
		await apiHelpers.objectEntry.getObjectEntryByExternalReferenceCode({
			applicationName: `${normalizeRestPath(objectDefinition.restContextPath)}`,
			externalReferenceCode: objectEntry.externalReferenceCode,
		})
	).toEqual({status: 'NOT_FOUND'});

	await companyExportImportPage.import({
		filePath: exportFilePath,
	});

	objectEntry = await apiHelpers.get(
		`${apiHelpers.baseUrl}${normalizeRestPath(objectDefinition.restContextPath)}/by-external-reference-code/${objectEntry.externalReferenceCode}/?nestedFields=permissions`
	);

	expect(objectEntry).not.toEqual(
		expect.objectContaining({
			permissions: [
				{
					actionIds: ['VIEW'],
					roleExternalReferenceCode: expect.any(String),
					roleName: 'Guest',
					roleType: 'regular',
				},
			],
		})
	);
});

test(
	'Can import custom object entries with current user as creator',
	{tag: '@LPD-43217'},
	async ({
		apiHelpers,
		companyExportImportPage,
		exportImportPage,
		globalMenuPage,
		page,
	}) => {
		const objectDefinition =
			await apiHelpers.objectAdmin.postRandomObjectDefinition({
				panelCategoryKey: 'control_panel.object',
				status: {code: 0},
			});
		apiHelpers.data.push({
			id: objectDefinition.id,
			type: 'objectDefinition',
		});
		const user = await createUserAssignRolesAndLogin({apiHelpers, page});
		const textFieldContent = `${objectDefinition.name} entry by ${user.alternateName}`;
		const objectEntry = await apiHelpers.objectEntry.postObjectEntry(
			{
				externalReferenceCode: '',
				name: 'test',
				textField: textFieldContent,
			},
			`c/${objectDefinition.name.toLowerCase()}s`
		);

		await globalMenuPage.goToApplications('Export');

		const exportFilePath = await exportImportPage.export({
			portletLabels: [`${objectDefinition.name} 1 Items`],
		});

		const applicationName = `${normalizeRestPath(objectDefinition.restContextPath)}`;
		await apiHelpers.delete(
			`${apiHelpers.baseUrl}${applicationName}/${objectEntry.id}`
		);

		await performUserSwitch(page, 'test');

		await test.step('Import the file with useCurrentUser enabled and check the imported entry authorship', async () => {
			await companyExportImportPage.import({
				expectedUploadErrorMessage: null,
				filePath: exportFilePath,
				includePermissions: false,
				useCurrentUser: true,
			});
			await globalMenuPage.goToObjectDefinition(objectDefinition.name);

			const row = page.locator('tr', {hasText: textFieldContent});

			await expect(row).toContainText(
				`${userData.test.name} ${userData.test.surname}`
			);
		});
	}
);

test(
	'Can import custom object entries with original creator, and creator user exists in the current environment',
	{tag: '@LPD-43217'},
	async ({
		apiHelpers,
		companyExportImportPage,
		exportImportPage,
		globalMenuPage,
		page,
	}) => {
		const objectDefinition =
			await apiHelpers.objectAdmin.postRandomObjectDefinition({
				panelCategoryKey: 'control_panel.object',
				status: {code: 0},
			});
		apiHelpers.data.push({
			id: objectDefinition.id,
			type: 'objectDefinition',
		});
		const user = await createUserAssignRolesAndLogin({apiHelpers, page});
		const textFieldContent = `${objectDefinition.name} entry by ${user.alternateName}`;
		const objectEntry = await apiHelpers.objectEntry.postObjectEntry(
			{
				externalReferenceCode: '',
				name: 'test',
				textField: textFieldContent,
			},
			`${normalizeRestPath(objectDefinition.restContextPath)}`
		);

		await globalMenuPage.goToApplications('Export');

		const exportFilePath = await exportImportPage.export({
			portletLabels: [`${objectDefinition.name} 1 Items`],
		});

		const applicationName = `${normalizeRestPath(objectDefinition.restContextPath)}`;
		await apiHelpers.delete(
			`${apiHelpers.baseUrl}${applicationName}/${objectEntry.id}`
		);

		await performUserSwitch(page, 'test');

		await test.step('Import the file and check the imported entry authorship', async () => {
			await companyExportImportPage.import({
				filePath: exportFilePath,
			});
			await globalMenuPage.goToObjectDefinition(objectDefinition.name);

			const row = page.locator('tr', {hasText: textFieldContent});
			await expect(row).toContainText(
				`${user.givenName} ${user.familyName}`
			);
		});
	}
);

test(
	'Can import custom object entries with original creator, but creator user does not exist in the current environment',
	{tag: '@LPD-43217'},
	async ({
		apiHelpers,
		companyExportImportPage,
		exportImportPage,
		globalMenuPage,
		page,
	}) => {
		const objectDefinition =
			await apiHelpers.objectAdmin.postRandomObjectDefinition({
				panelCategoryKey: 'control_panel.object',
				status: {code: 0},
			});
		apiHelpers.data.push({
			id: objectDefinition.id,
			type: 'objectDefinition',
		});
		const user = await createUserAssignRolesAndLogin({apiHelpers, page});
		const textFieldContent = `${objectDefinition.name} entry by ${user.alternateName}`;
		const objectEntry = await apiHelpers.objectEntry.postObjectEntry(
			{
				externalReferenceCode: '',
				name: 'test',
				textField: textFieldContent,
			},
			`${normalizeRestPath(objectDefinition.restContextPath)}`
		);

		await globalMenuPage.goToApplications('Export');

		const exportFilePath = await exportImportPage.export({
			portletLabels: [`${objectDefinition.name} 1 Items`],
		});

		const applicationName = `${normalizeRestPath(objectDefinition.restContextPath)}`;
		await apiHelpers.delete(
			`${apiHelpers.baseUrl}${applicationName}/${objectEntry.id}`
		);

		await performUserSwitch(page, 'test');

		await apiHelpers.headlessAdminUser.deleteUserAccount(Number(user.id));

		await test.step('Import the file and check the authorship fallback to the current user', async () => {
			await companyExportImportPage.import({
				filePath: exportFilePath,
			});
			await globalMenuPage.goToObjectDefinition(objectDefinition.name);

			const row = page.locator('tr', {hasText: textFieldContent});

			await expect(row).toContainText(
				`${userData.test.name} ${userData.test.surname}`
			);
		});
	}
);

test('Can see corresponding elements at instance level', async ({
	apiHelpers,
	companyExportImportPage,
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

	await globalMenuPage.goToApplications('Export');

	const exportFilePath = await exportImportPage.export({
		portletLabels: [`${objectDefinition.name} 1 Items`],
	});

	await page.goto('/');

	await companyExportImportPage.goToImportOptions(exportFilePath);

	await expect(page.getByRole('group', {name: 'Pages'})).not.toBeVisible();

	await expect(page.getByText('Comments, Ratings')).not.toBeVisible();

	await expect(page.getByText(`${objectDefinition.name}`)).toBeVisible();

	await expect(
		page.getByText(`${objectDefinition.externalReferenceCode} Change`)
	).not.toBeVisible();

	await expect(page.getByLabel('Delete Application Data')).not.toBeVisible();

	await expect(
		page.getByText(
			'Mirror: All data and content inside the imported LAR is created as new the first time while maintaining a reference to the source. Subsequent imports from the same source update the entries instead of creating new entries.'
		)
	).toBeVisible();

	await expect(page.getByText('Mirror with overwriting:')).not.toBeVisible();

	await expect(page.getByText('Copy as New:')).not.toBeVisible();
});

test('Can/not view Import menu item in Application menu depending on permissions', async ({
	apiHelpers,
	exportImportPage,
	globalMenuPage,
	page,
}) => {
	const companyId = await page.evaluate(() => {
		return Liferay.ThemeDisplay.getCompanyId();
	});

	const roleWithPermissions = await apiHelpers.headlessAdminUser.postRole({
		name: 'role' + getRandomInt(),
		rolePermissions: [
			{
				actionIds: ['VIEW_CONTROL_PANEL'],
				primaryKey: companyId,
				resourceName: '90',
				scope: 1,
			},
			{
				actionIds: ['ACCESS_IN_CONTROL_PANEL'],
				primaryKey: companyId,
				resourceName:
					'com_liferay_exportimport_web_portlet_CompanyImportPortlet',
				scope: 1,
			},
		],
	});

	const roleWithoutPermissions = await apiHelpers.headlessAdminUser.postRole({
		name: 'role' + getRandomInt(),
		rolePermissions: [
			{
				actionIds: ['VIEW_CONTROL_PANEL'],
				primaryKey: companyId,
				resourceName: '90',
				scope: 1,
			},
		],
	});

	const user1 = await apiHelpers.headlessAdminUser.postUserAccount();

	userData[user1.alternateName] = {
		name: user1.givenName,
		password: 'test',
		surname: user1.familyName,
	};

	await apiHelpers.headlessAdminUser.assignUserToRole(
		roleWithPermissions.externalReferenceCode,
		user1.id
	);

	const user2 = await apiHelpers.headlessAdminUser.postUserAccount();

	userData[user2.alternateName] = {
		name: user2.givenName,
		password: 'test',
		surname: user2.familyName,
	};

	await apiHelpers.headlessAdminUser.assignUserToRole(
		roleWithoutPermissions.externalReferenceCode,
		user2.id
	);

	await performLogout(page);

	await performLogin(page, user1.alternateName);

	await globalMenuPage.goToApplications();

	const importMenuItem = page.getByRole('menuitem', {
		exact: true,
		name: 'Import',
	});

	const importUrl = await importMenuItem.getAttribute('href');

	await expect(importMenuItem).toBeVisible();

	await globalMenuPage.goToApplications('Import');

	await expect(exportImportPage.newImportButton).toBeVisible();

	await performLogout(page);

	await performLogin(page, user2.alternateName);

	await expect(globalMenuPage.globalMenuButton).toBeHidden();

	// Try to access the Import page directly using the stored URL

	await page.goto(importUrl);

	await expect(exportImportPage.newImportButton).toBeHidden();
});

test('Cannot import a site scoped lar file', async ({
	companyExportImportPage,
	exportImportPage,
}) => {
	await exportImportPage.goToExport();

	const exportFilePath = await exportImportPage.export();

	await companyExportImportPage.import({
		expectedUploadErrorMessage:
			'The LAR file contains one or more entities with a different scope.',
		filePath: exportFilePath,
		includePermissions: false,
	});
});

test('Can import at instance level when LAR contains custom objects without existing definitions', async ({
	apiHelpers,
	companyExportImportPage,
	exportImportPage,
	globalMenuPage,
}) => {
	const objectDefinitionExternalReferenceCode = `ObjectDefinition${getRandomInt()}`;

	const objectDefinition =
		await apiHelpers.objectAdmin.postRandomObjectDefinition({
			className: `com.liferay.object.model.ObjectDefinition#${objectDefinitionExternalReferenceCode}`,
			objectDefinitionExternalReferenceCode,
			status: {code: 0},
		});

	try {
		await apiHelpers.objectEntry.postObjectEntry(
			{externalReferenceCode: 'testERC', textField: 'test'},
			`${normalizeRestPath(objectDefinition.restContextPath)}`
		);
	}
	catch {

		// Ensure cleanup if test execution stops before removing the object definition.

		apiHelpers.data.push({
			id: objectDefinition.id,
			type: 'objectDefinition',
		});
	}

	await globalMenuPage.goToApplications('Export');

	const exportFilePath = await exportImportPage.export({
		portletLabels: [`${objectDefinitionExternalReferenceCode} 1 Items`],
	});

	const objectDefinitionAPIClient =
		await apiHelpers.buildRestClient(ObjectDefinitionAPI);

	await objectDefinitionAPIClient.deleteObjectDefinition(objectDefinition.id);

	await companyExportImportPage.import({
		filePath: exportFilePath,
		taskStatus: 'completedWithErrors',
	});
});
