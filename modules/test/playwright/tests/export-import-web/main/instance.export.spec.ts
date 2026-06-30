/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {
	ObjectRelationship,
	ObjectRelationshipAPI,
} from '@liferay/object-admin-rest-client-js';
import {expect, mergeTests} from '@playwright/test';

import {dataApiHelpersTest} from '../../../fixtures/dataApiHelpersTest';
import {featureFlagsTest} from '../../../fixtures/featureFlagsTest';
import {globalMenuPagesTest} from '../../../fixtures/globalMenuPagesTest';
import {loginTest} from '../../../fixtures/loginTest';
import {productMenuPageTest} from '../../../fixtures/productMenuPageTest';
import {uiElementsPageTest} from '../../../fixtures/uiElementsTest';
import {getRandomInt} from '../../../utils/getRandomInt';
import {normalizeRestPath} from '../../../utils/normalizeRestPath';
import performLogin, {
	performLogout,
	userData,
} from '../../../utils/performLogin';
import {getTempDir} from '../../../utils/temp';
import {checkInZip, readFileFromZip} from '../../../utils/zip';
import {companyExportImportPageTest} from './fixtures/companyExportImportPagesTest';
import {exportImportPagesTest} from './fixtures/exportImportPagesTest';
import {toDateRangeDate, toDateRangeTime} from './utils/dateRangeUtil';

export const test = mergeTests(
	companyExportImportPageTest,
	dataApiHelpersTest,
	exportImportPagesTest,
	featureFlagsTest({
		'LPD-57655': {enabled: false},
	}),
	globalMenuPagesTest,
	loginTest(),
	productMenuPageTest,
	uiElementsPageTest
);

const rootModelTest = mergeTests(
	test,
	featureFlagsTest({
		'LPD-34594': {enabled: true},
		'LPD-57655': {enabled: false},
	}),
	globalMenuPagesTest
);

rootModelTest.describe(
	'Manage export and import of root model object definitions',
	() => {
		rootModelTest(
			'can distinguish root model object definitions in export/import',
			async ({apiHelpers, exportImportPage, globalMenuPage, page}) => {
				const objectRelationships: ObjectRelationship[] = [];
				const objectRelationshipAPIClient =
					await apiHelpers.buildRestClient(ObjectRelationshipAPI);

				try {
					const objectDefinitionA =
						await apiHelpers.objectAdmin.postRandomObjectDefinition(
							{
								status: {code: 0},
							}
						);

					const objectDefinitionB =
						await apiHelpers.objectAdmin.postRandomObjectDefinition(
							{
								status: {code: 0},
							}
						);

					const objectDefinitionC =
						await apiHelpers.objectAdmin.postRandomObjectDefinition(
							{
								status: {code: 0},
							}
						);

					apiHelpers.data.push({
						id: objectDefinitionA.id,
						type: 'objectDefinition',
					});
					apiHelpers.data.push({
						id: objectDefinitionB.id,
						type: 'objectDefinition',
					});
					apiHelpers.data.push({
						id: objectDefinitionC.id,
						type: 'objectDefinition',
					});

					const {body: objectRelationshipAB} =
						await objectRelationshipAPIClient.postObjectDefinitionByExternalReferenceCodeObjectRelationship(
							objectDefinitionA.externalReferenceCode,
							{
								edge: true,
								label: {
									en_US:
										'objectRelationshipABLabel' +
										getRandomInt(),
								},
								name:
									'objectRelationshipABName' +
									Math.floor(Math.random() * 99),
								objectDefinitionExternalReferenceCode1:
									objectDefinitionA.externalReferenceCode,
								objectDefinitionExternalReferenceCode2:
									objectDefinitionB.externalReferenceCode,
								objectDefinitionId1: objectDefinitionA.id,
								objectDefinitionId2: objectDefinitionB.id,
								objectDefinitionName2: objectDefinitionB.name,
								type: 'oneToMany',
							}
						);

					const {body: objectRelationshipAC} =
						await objectRelationshipAPIClient.postObjectDefinitionByExternalReferenceCodeObjectRelationship(
							objectDefinitionA.externalReferenceCode,
							{
								edge: true,
								label: {
									en_US:
										'objectRelationshipBCLabel' +
										getRandomInt(),
								},
								name:
									'objectRelationshipBCName' +
									Math.floor(Math.random() * 99),
								objectDefinitionExternalReferenceCode1:
									objectDefinitionA.externalReferenceCode,
								objectDefinitionExternalReferenceCode2:
									objectDefinitionC.externalReferenceCode,
								objectDefinitionId1: objectDefinitionA.id,
								objectDefinitionId2: objectDefinitionC.id,
								objectDefinitionName2: objectDefinitionC.name,
								type: 'oneToMany',
							}
						);

					objectRelationships.push(
						objectRelationshipAB,
						objectRelationshipAC
					);

					apiHelpers.data.push({
						id: objectRelationshipAB.id,
						type: 'objectRelationship',
					});
					apiHelpers.data.push({
						id: objectRelationshipAC.id,
						type: 'objectRelationship',
					});

					const objectEntryA =
						await apiHelpers.objectEntry.postObjectEntry(
							{textField: 'entryA'},
							'c/' + objectDefinitionA.name.toLowerCase() + 's'
						);

					const objectEntryB =
						await apiHelpers.objectEntry.postObjectEntry(
							{textField: 'entryB'},
							'c/' + objectDefinitionB.name.toLowerCase() + 's'
						);

					await apiHelpers.objectEntry.postObjectEntry(
						{
							[`r_${objectRelationshipAB.name}_c_${objectDefinitionA.name[0].toLowerCase() + objectDefinitionA.name.substring(1)}Id`]:
								objectEntryA.id.toString(),
							[`r_${objectRelationshipAC.name}_c_${objectDefinitionB.name[0].toLowerCase() + objectDefinitionB.name.substring(1)}Id`]:
								objectEntryB.id.toString(),
							textField: 'entryC',
						},
						'c/' + objectDefinitionC.name.toLowerCase() + 's'
					);

					const objectDefinitionRootCheckbox = page.getByRole(
						'checkbox',
						{
							name: new RegExp(
								`^${objectDefinitionA.label.en_US}:?`
							),
						}
					);

					await globalMenuPage.goToApplications('Export');

					await exportImportPage.newExportButton.click();

					await expect(objectDefinitionRootCheckbox).toBeVisible();

					await expect(
						page.getByText(
							`${objectDefinitionB.label.en_US}, ${objectDefinitionC.label.en_US}`
						)
					).toBeVisible();

					await globalMenuPage.goToApplications('Export');

					const filePath = await exportImportPage.export({
						portletLabels: [
							`${objectDefinitionA.name}: Root Object 1 Items`,
						],
					});

					await globalMenuPage.goToApplications('Import');

					await exportImportPage.newImportButton.click();

					await page
						.locator('input[type="file"]')
						.setInputFiles(filePath);

					await exportImportPage.continueButton.click();

					await expect(objectDefinitionRootCheckbox).toBeChecked();

					await expect(
						page.getByText(
							`${objectDefinitionB.label.en_US}, ${objectDefinitionC.label.en_US}`
						)
					).toBeVisible();
				}
				finally {
					for (const objectRelationship of objectRelationships) {
						await objectRelationshipAPIClient.putObjectRelationship(
							objectRelationship.id,
							{
								...objectRelationship,
								edge: false,
							}
						);
					}
				}
			}
		);
	}
);

test('cannot export site scoped custom object entries at instance level', async ({
	apiHelpers,
	globalMenuPage,
	page,
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

	await apiHelpers.objectEntry.postObjectEntry(
		{externalReferenceCode: '', textField: objectDefinition.name},
		`${normalizeRestPath(objectDefinition.restContextPath)}/scopes/Guest`
	);

	await globalMenuPage.goToApplications('Export');

	await page.getByTestId('creationMenuNewButton').nth(1).click();

	await expect(page.getByLabel(`${objectDefinition.name}`)).toBeHidden();
});

test('can export custom object entries at instance level with date filter', async ({
	apiHelpers,
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

	await apiHelpers.objectEntry.postObjectEntry(
		{externalReferenceCode: '', textField: objectDefinition.name},
		`${normalizeRestPath(objectDefinition.restContextPath)}`
	);

	await globalMenuPage.goToApplications('Export');

	const exportFilePath1 = await exportImportPage.export({
		portletLabels: [`${objectDefinition.name} 1 Items`],
	});

	const content1 = await readFileFromZip(
		`${objectDefinition.externalReferenceCode}.json`,
		exportFilePath1
	);

	const json1 = JSON.parse(content1);

	expect(json1.length).toBe(1);

	const endDate = new Date();

	endDate.setDate(endDate.getDate() - 1);

	const startDate = new Date();

	startDate.setDate(startDate.getDate() - 2);

	await globalMenuPage.goToApplications('Export');

	const exportFilePath2 = await exportImportPage.export({
		dateFilter: {
			endDate: toDateRangeDate(endDate),
			endTime: toDateRangeTime(endDate),
			startDate: toDateRangeDate(startDate),
			startTime: toDateRangeTime(startDate),
		},
		portletLabels: [`${objectDefinition.name} 1 Items`],
	});

	await expect(
		checkInZip(
			exportFilePath2,
			`${objectDefinition.externalReferenceCode}.json`
		)
	).resolves.toBe(false);

	await globalMenuPage.goToApplications('Export');

	const exportFilePath3 = await exportImportPage.export({
		dateFilter: {rangeLast: '12 Hours'},
		portletLabels: [`${objectDefinition.name} 1 Items`],
	});

	const content3 = await readFileFromZip(
		`${objectDefinition.externalReferenceCode}.json`,
		exportFilePath3
	);

	const json3 = JSON.parse(content3);

	expect(json3.length).toBe(1);
});

test('can export new default and custom task name', async ({
	apiHelpers,
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

	await apiHelpers.objectEntry.postObjectEntry(
		{externalReferenceCode: '', textField: objectDefinition.name},
		`${normalizeRestPath(objectDefinition.restContextPath)}`
	);

	await globalMenuPage.goToApplications('Export');

	const defaultExportFilePath = await exportImportPage.export({
		portletLabels: [`${objectDefinition.name} 1 Items`],
	});

	expect(defaultExportFilePath).toMatch(
		new RegExp(`^${getTempDir()}Export-`)
	);

	const taskName = 'CustomTaskName';

	await globalMenuPage.goToApplications('Export');

	const customExportFilePath = await exportImportPage.export({
		portletLabels: [`${objectDefinition.name} 1 Items`],
		taskName,
	});

	expect(customExportFilePath).toMatch(
		new RegExp(`^${getTempDir()}${taskName}\\.lar$`)
	);
});

test('can export custom object entries at instance level with permissions', async ({
	apiHelpers,
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

	await apiHelpers.objectEntry.postObjectEntry(
		{externalReferenceCode: '', textField: objectDefinition.name},
		`${normalizeRestPath(objectDefinition.restContextPath)}`
	);

	await globalMenuPage.goToApplications('Export');

	const exportFilePath = await exportImportPage.export({
		includePermissions: true,
		portletLabels: [`${objectDefinition.name} 1 Items`],
	});

	const content = await readFileFromZip(
		`${objectDefinition.externalReferenceCode}.json`,
		exportFilePath
	);

	const json = JSON.parse(content);

	expect(json.length).toBe(1);
	expect(json[0]).toHaveProperty('permissions');
});

test('can see corresponding elements at instance level', async ({
	apiHelpers,
	companyExportImportPage,
	globalMenuPage,
	uiElementsPage,
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
	await uiElementsPage.clickNewButton();
	await expect(
		companyExportImportPage.page.getByText('Comments, Ratings')
	).not.toBeVisible();

	await companyExportImportPage.exportImportPage.expectPortletCounts(
		objectDefinition.name,
		{counts: {items: 1}}
	);

	await expect(
		companyExportImportPage.page.getByText(
			`${objectDefinition.externalReferenceCode} Change`
		)
	).not.toBeVisible();

	await expect(
		companyExportImportPage.page.getByRole('link', {name: 'Refresh Counts'})
	).toBeVisible();
});

test(
	'can see the Deletions label at the instance level',
	{tag: ['@LPD-37317']},
	async ({exportImportPage, globalMenuPage, uiElementsPage}) => {
		await globalMenuPage.goToApplications('Export');
		await uiElementsPage.clickNewButton();

		const deletionsLabelText =
			await exportImportPage.deletionsLabel.textContent();

		expect(deletionsLabelText?.replace(/\s+/g, ' ').trim()).toBe(
			'Export Individual Deletions: If this is checked, the delete operations performed will be exported in the LAR file.'
		);
	}
);

test('Can/not view Export menu item in Application menu depending on permissions', async ({
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
					'com_liferay_exportimport_web_portlet_CompanyExportPortlet',
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

	const exportMenuItem = page.getByRole('menuitem', {
		exact: true,
		name: 'Export',
	});

	const exportUrl = await exportMenuItem.getAttribute('href');

	await expect(exportMenuItem).toBeVisible();

	await globalMenuPage.goToApplications('Export');

	await expect(exportImportPage.newExportButton).toBeVisible();

	await performLogout(page);

	await performLogin(page, user2.alternateName);

	await expect(globalMenuPage.globalMenuButton).toBeHidden();

	// Try to access the Export page directly using the stored URL

	await page.goto(exportUrl);

	await expect(exportImportPage.newExportButton).toBeHidden();
});

test(
	'Reset date filters when exporting',
	{tag: '@LPD-78925'},
	async ({exportImportPage}) => {
		await exportImportPage.goToExport();

		await exportImportPage.newExportButton.click();

		await exportImportPage.rangeDateRangeRadioButton.click();

		const endDate = new Date('2026-01-02 08:00');

		await exportImportPage.rangeDateRangeEndDate.fill(
			toDateRangeDate(endDate)
		);
		await exportImportPage.rangeDateRangeEndTime.fill(
			toDateRangeTime(endDate)
		);

		const startDate = new Date('2026-01-01 08:00');

		await exportImportPage.rangeDateRangeStartDate.fill(
			toDateRangeDate(startDate)
		);
		await exportImportPage.rangeDateRangeStartTime.fill(
			toDateRangeTime(startDate)
		);

		await exportImportPage.refreshCountsLink.click();

		await expect(exportImportPage.rangeDateRangeEndDate).toBeEnabled();
		await expect(exportImportPage.rangeDateRangeEndDate).toHaveValue(
			toDateRangeDate(endDate)
		);
		await expect(exportImportPage.rangeDateRangeStartDate).toBeEnabled();
		await expect(exportImportPage.rangeDateRangeStartDate).toHaveValue(
			toDateRangeDate(startDate)
		);

		await exportImportPage.allRadioButton.click();

		await exportImportPage.refreshCountsLink.click();

		await expect(exportImportPage.rangeDateRangeEndDate).toBeEnabled();
		await expect(exportImportPage.rangeDateRangeEndDate).not.toHaveValue(
			toDateRangeDate(endDate)
		);
		await expect(exportImportPage.rangeDateRangeStartDate).toBeEnabled();
		await expect(exportImportPage.rangeDateRangeStartDate).not.toHaveValue(
			toDateRangeDate(endDate)
		);

		await exportImportPage.rangeDateRangeRadioButton.click();

		await expect(exportImportPage.rangeDateRangeEndDate).toBeEnabled();
		await expect(exportImportPage.rangeDateRangeEndDate).not.toHaveValue(
			toDateRangeDate(endDate)
		);
		await expect(exportImportPage.rangeDateRangeStartDate).toBeEnabled();
		await expect(exportImportPage.rangeDateRangeStartDate).not.toHaveValue(
			toDateRangeDate(endDate)
		);
	}
);

test('Can see deletion counts at instance level', async ({
	apiHelpers,
	companyExportImportPage,
	globalMenuPage,
	uiElementsPage,
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

	const objectEntry1 = await apiHelpers.objectEntry.postObjectEntry(
		{textField: objectDefinition.name},
		applicationName
	);

	const objectEntry2 = await apiHelpers.objectEntry.postObjectEntry(
		{textField: objectDefinition.name},
		applicationName
	);

	await globalMenuPage.goToApplications('Export');
	await uiElementsPage.clickNewButton();

	await companyExportImportPage.exportImportPage.deletionsLabel.check();

	await companyExportImportPage.exportImportPage.expectPortletCounts(
		objectDefinition.name,
		{counts: {items: 2}}
	);

	await apiHelpers.objectEntry.deleteObjectEntry(
		applicationName,
		String(objectEntry1.id)
	);

	await companyExportImportPage.exportImportPage.refreshCountsLink.click();

	await companyExportImportPage.exportImportPage.expectPortletCounts(
		objectDefinition.name,
		{counts: {deletions: 1, items: 1}}
	);

	await apiHelpers.objectEntry.deleteObjectEntry(
		applicationName,
		String(objectEntry2.id)
	);

	await companyExportImportPage.exportImportPage.refreshCountsLink.click();

	await companyExportImportPage.exportImportPage.expectPortletCounts(
		objectDefinition.name,
		{counts: {deletions: 2}}
	);

	await companyExportImportPage.exportImportPage.deletionsLabel.uncheck();

	await companyExportImportPage.exportImportPage.expectPortletDeletionsHidden(
		objectDefinition.name
	);
});
