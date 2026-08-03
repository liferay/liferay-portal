/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

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
	userData,
} from '../../../utils/performLogin';
import {companyExportImportPageTest} from './fixtures/companyExportImportPagesTest';
import {exportImportPagesTest} from './fixtures/exportImportPagesTest';
import {portletExportImportPageTest} from './fixtures/portletExportImportPageTest';
import {stagingPageTest} from './fixtures/stagingPageTest';

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

test(
	'Can/not view Import menu item in Application menu depending on permissions',
	{tag: '@LPD-99799'},
	async ({apiHelpers, exportImportPage, globalMenuPage, page}) => {
		const companyId = await page.evaluate(() => {
			return Liferay.ThemeDisplay.getCompanyId();
		});

		const roleWithPermissions = await apiHelpers.headlessAdminUser.postRole(
			{
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
			}
		);

		const roleWithoutPermissions =
			await apiHelpers.headlessAdminUser.postRole({
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

		await page.goto(importUrl);

		await expect(exportImportPage.newImportButton).toBeHidden();
	}
);
