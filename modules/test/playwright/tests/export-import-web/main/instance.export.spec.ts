/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {ObjectDefinitionAPI} from '@liferay/object-admin-rest-client-js';
import {expect, mergeTests} from '@playwright/test';

import {applicationsMenuPageTest} from '../../../fixtures/applicationsMenuPageTest';
import {dataApiHelpersTest} from '../../../fixtures/dataApiHelpersTest';
import {featureFlagsTest} from '../../../fixtures/featureFlagsTest';
import {loginTest} from '../../../fixtures/loginTest';
import {productMenuPageTest} from '../../../fixtures/productMenuPageTest';
import {uiElementsPageTest} from '../../../fixtures/uiElementsTest';
import {getRandomInt} from '../../../utils/getRandomInt';
import performLogin, {
	performLogout,
	userData,
} from '../../../utils/performLogin';
import {getTempDir} from '../../../utils/temp';
import {readFileFromZip} from '../../../utils/zip';
import {companyExportImportPageTest} from './fixtures/companyExportImportPagesTest';
import {toDateRangeDate, toDateRangeTime} from './utils/dateRangeUtil';
import {objectDefitionRequestData} from './utils/objectDefitionRequestData';

export const test = mergeTests(
	applicationsMenuPageTest,
	companyExportImportPageTest,
	dataApiHelpersTest,
	featureFlagsTest({
		'LPD-35914': {enabled: true},
	}),
	loginTest(),
	productMenuPageTest,
	uiElementsPageTest
);

test('cannot export site scoped custom object entries at instance level', async ({
	apiHelpers,
	applicationsMenuPage,
	page,
}) => {
	const objectActionAPIClient =
		await apiHelpers.buildRestClient(ObjectDefinitionAPI);

	const {body: objectDefinition} =
		await objectActionAPIClient.postObjectDefinition(
			objectDefitionRequestData({scope: 'site'})
		);

	apiHelpers.data.push({id: objectDefinition.id, type: 'objectDefinition'});

	await apiHelpers.objectEntry.postObjectEntry(
		{externalReferenceCode: '', name: 'test'},
		'c/tests/scopes/Guest'
	);

	await applicationsMenuPage.goToExport();

	await page.getByTestId('creationMenuNewButton').nth(1).click();

	await expect(page.getByLabel('Tests')).toBeHidden();
});

test('can export custom object entries at instance level with date filter', async ({
	apiHelpers,
	companyExportImportPage,
}) => {
	const objectActionAPIClient =
		await apiHelpers.buildRestClient(ObjectDefinitionAPI);

	const {body: objectDefinition} =
		await objectActionAPIClient.postObjectDefinition(
			objectDefitionRequestData()
		);

	apiHelpers.data.push({id: objectDefinition.id, type: 'objectDefinition'});

	await apiHelpers.objectEntry.postObjectEntry(
		{externalReferenceCode: '', name: 'test'},
		'c/tests'
	);

	const exportFilePath1 = await companyExportImportPage.export(
		['Tests 1 Items'],
		false
	);

	const content1 = await readFileFromZip('C_Test.json', exportFilePath1);

	const json1 = JSON.parse(content1);

	expect(json1.length).toBe(1);

	const endDate = new Date();

	endDate.setDate(endDate.getDate() - 1);

	const startDate = new Date();

	startDate.setDate(startDate.getDate() - 2);

	const exportFilePath2 = await companyExportImportPage.export(
		['Tests 1 Items'],
		false,
		{
			endDate: toDateRangeDate(endDate),
			endTime: toDateRangeTime(endDate),
			startDate: toDateRangeDate(startDate),
			startTime: toDateRangeTime(startDate),
		}
	);

	const content2 = await readFileFromZip('C_Test.json', exportFilePath2);

	const json2 = JSON.parse(content2);

	expect(json2.length).toBe(0);

	const exportFilePath3 = await companyExportImportPage.export(
		['Tests 1 Items'],
		false,
		{
			rangeLast: '12 Hours',
		}
	);

	const content3 = await readFileFromZip('C_Test.json', exportFilePath3);

	const json3 = JSON.parse(content3);

	expect(json3.length).toBe(1);
});

test('can export new default and custom task name', async ({
	apiHelpers,
	companyExportImportPage,
}) => {
	const objectActionAPIClient =
		await apiHelpers.buildRestClient(ObjectDefinitionAPI);

	const {body: objectDefinition} =
		await objectActionAPIClient.postObjectDefinition(
			objectDefitionRequestData()
		);

	apiHelpers.data.push({id: objectDefinition.id, type: 'objectDefinition'});

	await apiHelpers.objectEntry.postObjectEntry(
		{externalReferenceCode: '', name: 'test'},
		'c/tests'
	);

	const defaultExportFilePath = await companyExportImportPage.export([
		'Tests 1 Items',
	]);

	expect(defaultExportFilePath).toMatch(
		new RegExp(`^${getTempDir()}Export-`)
	);

	const taskName = 'CustomTaskName';

	const customExportFilePath = await companyExportImportPage.export(
		['Tests 1 Items'],
		false,
		undefined,
		taskName
	);

	expect(customExportFilePath).toMatch(
		new RegExp(`^${getTempDir()}${taskName}-`)
	);
});

test('can export custom object entries at instance level with permissions', async ({
	apiHelpers,
	companyExportImportPage,
}) => {
	const objectActionAPIClient =
		await apiHelpers.buildRestClient(ObjectDefinitionAPI);

	const {body: objectDefinition} =
		await objectActionAPIClient.postObjectDefinition(
			objectDefitionRequestData()
		);

	apiHelpers.data.push({id: objectDefinition.id, type: 'objectDefinition'});

	await apiHelpers.objectEntry.postObjectEntry(
		{externalReferenceCode: '', name: 'test'},
		'c/tests'
	);

	const exportFilePath = await companyExportImportPage.export(
		['Tests 1 Items'],
		true
	);

	const content = await readFileFromZip('C_Test.json', exportFilePath);

	const json = JSON.parse(content);

	expect(json.length).toBe(1);
	expect(json[0]).toHaveProperty('permissions');
});

test('can see corresponding elements at instance level', async ({
	apiHelpers,
	companyExportImportPage,
	uiElementsPage,
}) => {
	const objectActionAPIClient =
		await apiHelpers.buildRestClient(ObjectDefinitionAPI);
	const {body: objectDefinition} =
		await objectActionAPIClient.postObjectDefinition(
			objectDefitionRequestData()
		);
	apiHelpers.data.push({id: objectDefinition.id, type: 'objectDefinition'});

	await apiHelpers.objectEntry.postObjectEntry({name: 'test'}, 'c/tests');

	await companyExportImportPage.applicationsMenuPage.goToExport();
	await uiElementsPage.clickNewButton();
	await expect(
		companyExportImportPage.page.getByText('Comments, Ratings')
	).not.toBeVisible();

	await expect(
		companyExportImportPage.page.getByText('Tests 1 Items')
	).not.toBeVisible();

	await companyExportImportPage.page.getByLabel('Tests').click();

	await expect(
		companyExportImportPage.page.getByText('C_Test Change')
	).not.toBeVisible();

	await expect(
		companyExportImportPage.page.getByRole('link', {name: 'Refresh Counts'})
	).toBeVisible();
});

test(
	'can see the Deletions label at the instance level',
	{tag: ['@LPD-37317']},
	async ({companyExportImportPage, uiElementsPage}) => {
		await companyExportImportPage.applicationsMenuPage.goToExport();
		await uiElementsPage.clickNewButton();

		const deletionsLabelText =
			await companyExportImportPage.deletionsLabel.textContent();

		expect(deletionsLabelText?.replace(/\s+/g, ' ').trim()).toBe(
			'Export Individual Deletions: If this is checked, the delete operations performed will be exported in the LAR file.'
		);
	}
);

test('Can/not view Export menu item in Application menu depending on permissions', async ({
	apiHelpers,
	applicationsMenuPage,
	companyExportImportPage,
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

	await applicationsMenuPage.goToApplicationsMenu();

	const exportUrl =
		await applicationsMenuPage.exportMenuItem.getAttribute('href');

	await expect(applicationsMenuPage.exportMenuItem).toBeVisible();

	await applicationsMenuPage.goToExport();

	await expect(
		companyExportImportPage.exportImportPage.newExportButton
	).toBeVisible();

	await performLogout(page);

	await performLogin(page, user2.alternateName);

	await expect(applicationsMenuPage.applicationsMenuTabButton).toBeHidden();

	// Try to access the Export page directly using the stored URL

	await page.goto(exportUrl);

	await expect(
		companyExportImportPage.exportImportPage.newExportButton
	).toBeHidden();
});
