/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {dataApiHelpersTest} from '../../../fixtures/dataApiHelpersTest';
import {featureFlagsTest} from '../../../fixtures/featureFlagsTest';
import {globalMenuPagesTest} from '../../../fixtures/globalMenuPagesTest';
import {loginTest} from '../../../fixtures/loginTest';
import {getRandomInt} from '../../../utils/getRandomInt';
import getRandomString from '../../../utils/getRandomString';
import performLogin, {
	performLogout,
	userData,
} from '../../../utils/performLogin';
import {getTempDir} from '../../../utils/temp';
import {exportImportPagesTest} from './fixtures/exportImportPagesTest';

export const test = mergeTests(
	dataApiHelpersTest,
	exportImportPagesTest,
	featureFlagsTest({
		'LPD-57655': {enabled: true},
	}),
	globalMenuPagesTest,
	loginTest()
);

test(
	'Can export at instance level with a custom task name',
	{tag: '@LPD-57655'},
	async ({exportImportPage, globalMenuPage}) => {
		await globalMenuPage.goToApplications('Export');

		const name = `MyExport-${getRandomString()}`;

		await exportImportPage.export(name);

		await expect(exportImportPage.taskStatusLabel(name)).toBeVisible();

		expect(await exportImportPage.download(name)).toBe(
			`${getTempDir()}${name}.lar`
		);
	}
);

test(
	'Cannot select comments and ratings at instance level',
	{tag: '@LPD-57655'},
	async ({
		exportImportDataSelectionPage,
		exportImportPage,
		globalMenuPage,
		page,
	}) => {
		await globalMenuPage.goToApplications('Export');

		await exportImportPage.clickNew();

		await exportImportDataSelectionPage.expandSection('Content & Data');

		await expect(page.getByText('Comments and Ratings')).toBeHidden();
	}
);

test(
	'Can view the Export menu item only with permissions',
	{tag: '@LPD-57655'},
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
							'com_liferay_exportimport_web_portlet_CompanyExportPortlet',
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

		const exportMenuItem = page.getByRole('menuitem', {
			exact: true,
			name: 'Export',
		});

		const exportURL = await exportMenuItem.getAttribute('href');

		await expect(exportMenuItem).toBeVisible();

		await globalMenuPage.goToApplications('Export');

		await expect(exportImportPage.newButton).toBeVisible();

		await performLogout(page);

		await performLogin(page, user2.alternateName);

		await expect(globalMenuPage.globalMenuButton).toBeHidden();

		// Try to access the Export page directly using the stored URL

		await page.goto(exportURL);

		await expect(exportImportPage.newButton).toBeHidden();
	}
);
