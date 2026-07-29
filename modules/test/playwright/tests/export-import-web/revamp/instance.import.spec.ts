/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {dataApiHelpersTest} from '../../../fixtures/dataApiHelpersTest';
import {featureFlagsTest} from '../../../fixtures/featureFlagsTest';
import {globalMenuPagesTest} from '../../../fixtures/globalMenuPagesTest';
import {isolatedSiteTest} from '../../../fixtures/isolatedSiteTest';
import {loginTest} from '../../../fixtures/loginTest';
import {getRandomInt} from '../../../utils/getRandomInt';
import getRandomString from '../../../utils/getRandomString';
import performLogin, {
	performLogout,
	userData,
} from '../../../utils/performLogin';
import {exportImportPagesTest} from './fixtures/exportImportPagesTest';

export const test = mergeTests(
	dataApiHelpersTest,
	exportImportPagesTest,
	featureFlagsTest({
		'LPD-57655': {enabled: true},
	}),
	globalMenuPagesTest,
	isolatedSiteTest,
	loginTest()
);

test(
	'Can/not view Import menu item in Application menu depending on permissions',
	{tag: ['@LPD-99382', '@LPD-99799']},
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

		const importURL = await importMenuItem.getAttribute('href');

		await expect(importMenuItem).toBeVisible();

		await globalMenuPage.goToApplications('Import');

		await expect(exportImportPage.newButton).toBeVisible();

		await performLogout(page);

		await performLogin(page, user2.alternateName);

		await expect(globalMenuPage.globalMenuButton).toBeHidden();

		await page.goto(importURL);

		await expect(exportImportPage.newButton).toBeHidden();
	}
);

test(
	'Cannot import a site scoped lar file',
	{tag: '@LPD-99382'},
	async ({exportImportPage, globalMenuPage, site}) => {
		await exportImportPage.goToExport(site.friendlyUrlPath);

		const name = `MyExport-${getRandomString()}`;

		await exportImportPage.export(name);

		const folderPath = await exportImportPage.download(name);

		await globalMenuPage.goToApplications('Import');

		await exportImportPage.newButton.click();

		await exportImportPage.expectUploadError(
			folderPath,
			'The LAR file contains one or more entities with a different scope.'
		);
	}
);
