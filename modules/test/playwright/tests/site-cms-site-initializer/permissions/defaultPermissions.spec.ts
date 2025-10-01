/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {dataApiHelpersTest} from '../../../fixtures/dataApiHelpersTest';
import {featureFlagsTest} from '../../../fixtures/featureFlagsTest';
import {loginTest} from '../../../fixtures/loginTest';
import {getRandomInt} from '../../../utils/getRandomInt';
import {PORTLET_URLS} from '../../../utils/portletUrls';
import {cmsPagesTest} from './fixtures/cmsPagesTest';
import {DefaultPermissionsPage} from './pages/DefaultPermissionsPage';

const test = mergeTests(
	cmsPagesTest,
	dataApiHelpersTest,
	featureFlagsTest({
		'LPD-17564': {enabled: true},
		'LPD-32050': {enabled: true},
		'LPS-179669': {enabled: true},
	}),
	loginTest()
);

type VerifyPermissionsOptions = {
	defaultPermissionsPage: DefaultPermissionsPage;
	objectName?: string;
	page: any;
	permissions: Array<{action: string; checked: boolean; role: string}>;
};

async function createSpace(page, spaceName: string) {
	await page.getByTestId('fdsCreationActionButton').click();
	await page.getByLabel('Space Name').fill(spaceName);
	await page.getByRole('button', {name: 'Continue'}).click();
	await page.getByRole('button', {name: 'Continue'}).click();
}

async function deleteSpace(page, spaceName: string) {
	await expect(async () => {
		await (await getTableRowByText(page, spaceName))
			.getByRole('button', {name: 'Actions'})
			.click();
		await page.getByRole('menuitem', {name: 'Delete'}).click();
	}).toPass();

	await page.getByRole('button', {name: 'Delete'}).click();
}

async function getTableRowByText(page, text: string) {
	return page.locator('table.table tbody tr', {hasText: text}).first();
}

async function goToDefaultPermissions(page, objectName?: string) {
	await expect(async () => {
		if (!objectName) {
			await page.getByLabel('Actions').click();
		}
		else {
			await (await getTableRowByText(page, objectName))
				.getByRole('button', {name: 'Actions'})
				.click();
		}
		await page
			.getByRole('menuitem', {exact: true, name: 'Default Permissions'})
			.click();
	}).toPass();
}

async function tickCheckBoxes(page, names: string[]) {
	for (const name of names) {
		await (await getTableRowByText(page, name))
			.getByRole('checkbox')
			.check();
	}
}

async function verifyPermissions({
	defaultPermissionsPage,
	objectName,
	page,
	permissions,
}: VerifyPermissionsOptions) {
	await goToDefaultPermissions(page, objectName);

	await defaultPermissionsPage.verifyPermissions(permissions);

	await defaultPermissionsPage.permissionsModalCancelButton.click();
}

test(
	'Space and folder contents inherit parent default permissions',
	{tag: '@LPD-62475'},
	async ({defaultPermissionsPage, folderPage, page, spaceSummaryPage}) => {
		test.setTimeout(90000);

		await page.goto(PORTLET_URLS.cmsAllSpaces);

		const spaceName = 'Space' + getRandomInt();

		await createSpace(page, spaceName);

		try {
			await page.goto(PORTLET_URLS.cmsAllSpaces);

			await goToDefaultPermissions(page, spaceName);

			const permissions = [
				{action: 'DELETE', checked: true, role: 'Power User'},
				{action: 'PERMISSIONS', checked: true, role: 'User'},
			];

			await defaultPermissionsPage.checkPermissionsAndSave(permissions);

			await spaceSummaryPage.goto(spaceName);

			await spaceSummaryPage.viewAllContentLink.click();

			const folderName = 'Folder' + getRandomInt();

			await folderPage.createFolder(folderName);

			await verifyPermissions({
				defaultPermissionsPage,
				objectName: folderName,
				page,
				permissions,
			});

			await page.getByRole('link', {name: folderName}).click();

			const subFolderName = 'SubFolder' + getRandomInt();

			await folderPage.createFolder(subFolderName);

			await verifyPermissions({
				defaultPermissionsPage,
				objectName: subFolderName,
				page,
				permissions,
			});

			await page.getByTestId('fdsCreationActionButton').click();
			await page.getByRole('menuitem', {name: 'Basic Content'}).click();
			await page.getByRole('button', {name: 'Publish'}).click();

			await (await getTableRowByText(page, 'Basic Web Content'))
				.getByRole('button', {name: 'Actions'})
				.click();

			await expect(
				page.getByRole('menuitem', {name: 'Permissions'})
			).toBeVisible();
			await expect(
				page.getByRole('menuitem', {name: 'Default Permissions'})
			).not.toBeVisible();
		}
		finally {
			await page.goto(PORTLET_URLS.cmsAllSpaces);

			await deleteSpace(page, spaceName);
		}
	}
);

test(
	'Change space default permissions in bulk',
	{tag: '@LPD-62475'},
	async ({defaultPermissionsPage, page}) => {
		test.setTimeout(90000);

		const spaceName1 = 'Space' + getRandomInt();
		const spaceName2 = 'Space' + getRandomInt();

		await page.goto(PORTLET_URLS.cmsAllSpaces);

		await createSpace(page, spaceName1);

		await page.goto(PORTLET_URLS.cmsAllSpaces);

		await createSpace(page, spaceName2);

		try {
			await page.goto(PORTLET_URLS.cmsAllSpaces);

			await tickCheckBoxes(page, [spaceName1, spaceName2]);
			await goToDefaultPermissions(page);

			const permissions1 = [
				{action: 'DELETE', checked: true, role: 'Power User'},
				{action: 'PERMISSIONS', checked: true, role: 'User'},
			];

			await defaultPermissionsPage.checkPermissionsAndSave(
				permissions1,
				true
			);

			await verifyPermissions({
				defaultPermissionsPage,
				objectName: spaceName1,
				page,
				permissions: permissions1,
			});
			await verifyPermissions({
				defaultPermissionsPage,
				objectName: spaceName2,
				page,
				permissions: permissions1,
			});

			const permissions2 = [
				{action: 'DELETE', checked: false, role: 'Power User'},
				{action: 'PERMISSIONS', checked: false, role: 'User'},
			];

			await verifyPermissions({
				defaultPermissionsPage,
				objectName: 'Default',
				page,
				permissions: permissions2,
			});
		}
		finally {
			await page.goto(PORTLET_URLS.cmsAllSpaces);

			await deleteSpace(page, spaceName1);
			await deleteSpace(page, spaceName2);
		}
	}
);

test(
	'Change folder default permissions in bulk',
	{tag: '@LPD-62475'},
	async ({contentsPage, defaultPermissionsPage, folderPage, page}) => {
		test.setTimeout(90000);

		const spaceName1 = 'Space' + getRandomInt();
		const spaceName2 = 'Space' + getRandomInt();

		await page.goto(PORTLET_URLS.cmsAllSpaces);

		await createSpace(page, spaceName1);

		await page.goto(PORTLET_URLS.cmsAllSpaces);

		await createSpace(page, spaceName2);

		try {
			await page.goto(PORTLET_URLS.cmsAllSpaces);

			await goToDefaultPermissions(page, spaceName1);

			const permissionsSpace1 = [
				{action: 'DELETE', checked: true, role: 'Power User'},
				{action: 'PERMISSIONS', checked: true, role: 'User'},
			];

			await defaultPermissionsPage.checkPermissionsAndSave(
				permissionsSpace1
			);

			await goToDefaultPermissions(page, spaceName2);

			const permissionsSpace2 = [
				{action: 'UPDATE', checked: true, role: 'Power User'},
				{action: 'VIEW', checked: true, role: 'User'},
			];

			await defaultPermissionsPage.checkPermissionsAndSave(
				permissionsSpace2
			);

			await contentsPage.goto();

			const folderName1 = 'Folder' + getRandomInt();
			const folderName2 = 'Folder' + getRandomInt();
			const folderName3 = 'Folder' + getRandomInt();

			await contentsPage.createFolder(folderName1, spaceName1);
			await contentsPage.createFolder(folderName2, spaceName2);
			await contentsPage.createFolder(folderName3, spaceName2);

			await goToDefaultPermissions(page, folderName1);

			const permissionsFolder1 = [
				{action: 'UPDATE', checked: true, role: 'Supplier'},
				{action: 'VIEW', checked: true, role: 'Supplier'},
			];

			await defaultPermissionsPage.checkPermissionsAndSave(
				permissionsFolder1
			);

			const defaultPermissions = [
				{action: 'ADD_ENTRY', checked: true, role: 'CMS Administrator'},
				{action: 'DELETE', checked: true, role: 'CMS Administrator'},
				{
					action: 'PERMISSIONS',
					checked: true,
					role: 'CMS Administrator',
				},
				{action: 'UPDATE', checked: true, role: 'CMS Administrator'},
				{action: 'SUBSCRIBE', checked: true, role: 'CMS Administrator'},
				{action: 'VIEW', checked: true, role: 'CMS Administrator'},
			];

			await tickCheckBoxes(page, [folderName1, folderName2, folderName3]);
			await verifyPermissions({
				defaultPermissionsPage,
				objectName: null,
				page,
				permissions: defaultPermissions,
			});

			await page.getByRole('link', {name: 'Clear'}).click();

			await tickCheckBoxes(page, [folderName2, folderName3]);
			await verifyPermissions({
				defaultPermissionsPage,
				objectName: null,
				page,
				permissions: permissionsSpace2,
			});

			await verifyPermissions({
				defaultPermissionsPage,
				objectName: folderName1,
				page,
				permissions: permissionsFolder1,
			});

			await page.getByRole('link', {name: folderName1}).click();

			const subFolderName1 = 'SubFolder' + getRandomInt();
			const subFolderName2 = 'SubFolder' + getRandomInt();

			await folderPage.createFolder(subFolderName1);
			await folderPage.createFolder(subFolderName2);

			await goToDefaultPermissions(page, subFolderName1);

			const permissionsSubFolder1 = [
				{action: 'ADD_ENTRY', checked: true, role: 'Power User'},
				{action: 'SUBSCRIBE', checked: true, role: 'User'},
			];

			await defaultPermissionsPage.checkPermissionsAndSave(
				permissionsSubFolder1
			);

			await tickCheckBoxes(page, [subFolderName1, subFolderName2]);
			await verifyPermissions({
				defaultPermissionsPage,
				objectName: null,
				page,
				permissions: permissionsFolder1,
			});
			await verifyPermissions({
				defaultPermissionsPage,
				objectName: subFolderName1,
				page,
				permissions: permissionsSubFolder1,
			});
		}
		finally {
			await page.goto(PORTLET_URLS.cmsAllSpaces);

			await deleteSpace(page, spaceName1);
			await deleteSpace(page, spaceName2);
		}
	}
);
