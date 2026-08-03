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
import {normalizeRestPath} from '../../../utils/normalizeRestPath';
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

test(
	'Can import custom object entries using a date filter',
	{tag: '@LPD-100261'},
	async ({
		apiHelpers,
		exportImportDataSelectionPage,
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

		const applicationName = normalizeRestPath(
			objectDefinition.restContextPath
		);

		const objectEntry = await apiHelpers.objectEntry.postObjectEntry(
			{externalReferenceCode: '', textField: getRandomString()},
			applicationName
		);

		await globalMenuPage.goToApplications('Export');

		await exportImportPage.clickNew();

		await test.step('Entries outside the date range are excluded', async () => {
			const toDateFilterDate = (date: Date) => {
				const pad = (value: number) => String(value).padStart(2, '0');

				return `${date.getFullYear()}-${pad(date.getMonth() + 1)}-${pad(date.getDate())} ${pad(date.getHours())}:${pad(date.getMinutes())}`;
			};

			const dayMilliseconds = 24 * 60 * 60 * 1000;

			const date = new Date();

			const oneDayAgoDate = new Date(date.getTime() - dayMilliseconds);
			const twoDaysAgoDate = new Date(
				date.getTime() - 2 * dayMilliseconds
			);

			await exportImportPage.filterByDateRange(
				toDateFilterDate(twoDaysAgoDate),
				toDateFilterDate(oneDayAgoDate)
			);

			await expect(
				exportImportPage.page.getByText('0 Results Found for:')
			).toBeVisible();
		});

		await test.step('Entries modified in the last hours are imported', async () => {
			const name = `MyExport-${getRandomString()}`;

			await exportImportPage.filterByModifiedLast();

			await exportImportDataSelectionPage.selectOnlyObjectDefinition(
				objectDefinition.name
			);

			await exportImportPage.nameInput.fill(name);

			await exportImportPage.exportButton.click();

			await expect(exportImportPage.taskStatusLabel(name)).toBeVisible();

			const folderPath = await exportImportPage.download(name);

			await apiHelpers.objectEntry.deleteObjectEntry(
				applicationName,
				String(objectEntry.id)
			);

			await globalMenuPage.goToApplications('Import');

			await exportImportPage.newButton.click();

			await exportImportPage.import({folderPath, name});

			expect(
				await apiHelpers.objectEntry.getObjectEntryByExternalReferenceCode(
					{
						applicationName,
						externalReferenceCode:
							objectEntry.externalReferenceCode,
					}
				)
			).toEqual(
				expect.objectContaining({
					externalReferenceCode: objectEntry.externalReferenceCode,
					textField: objectEntry.textField,
				})
			);
		});
	}
);

test(
	'Can import custom object entry deletions',
	{tag: '@LPD-100261'},
	async ({
		apiHelpers,
		exportImportDataSelectionPage,
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

		const applicationName = normalizeRestPath(
			objectDefinition.restContextPath
		);

		const objectEntry = await apiHelpers.objectEntry.postObjectEntry(
			{externalReferenceCode: '', textField: getRandomString()},
			applicationName
		);

		await apiHelpers.objectEntry.deleteObjectEntry(
			applicationName,
			String(objectEntry.id)
		);

		const name = `MyExport-${getRandomString()}`;

		await test.step('Export the deletions', async () => {
			await globalMenuPage.goToApplications('Export');

			await exportImportPage.clickNew();

			await exportImportDataSelectionPage.selectOnlyObjectDefinition(
				objectDefinition.name
			);

			await exportImportPage.nameInput.fill(name);

			await exportImportPage.exportIndividualDeletionsCheckbox.check();

			await exportImportPage.exportButton.click();

			await expect(exportImportPage.taskStatusLabel(name)).toBeVisible();
		});

		const folderPath = await exportImportPage.download(name);

		await apiHelpers.objectEntry.postObjectEntry(
			{
				externalReferenceCode: objectEntry.externalReferenceCode,
				textField: objectEntry.textField,
			},
			applicationName
		);

		await test.step('Import the deletions', async () => {
			await globalMenuPage.goToApplications('Import');

			await exportImportPage.newButton.click();

			await exportImportPage.import({
				folderPath,
				includeDeletions: true,
				name,
			});
		});

		expect(
			await apiHelpers.objectEntry.getObjectEntryByExternalReferenceCode({
				applicationName,
				externalReferenceCode: objectEntry.externalReferenceCode,
			})
		).toEqual({status: 'NOT_FOUND'});
	}
);
