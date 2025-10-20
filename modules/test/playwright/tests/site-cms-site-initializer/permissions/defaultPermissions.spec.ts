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
import {waitForAlert} from '../../../utils/waitForAlert';
import {cmsPagesTest} from './fixtures/cmsPagesTest';
import {DefaultPermissionsPage} from './pages/DefaultPermissionsPage';
import {PermissionsPage} from './pages/PermissionsPage';

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
	menuitem: string;
	objectName?: string;
	page: any;
	permissions: Array<{action: string; checked: boolean; role: string}>;
};

async function clickMenuItem(menuitem: string, page, objectName?: string) {
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
			.getByRole('menuitem', {
				exact: true,
				name: menuitem,
			})
			.click({timeout: 1000});
	}).toPass();
}

async function createSpace(page, spaceName: string) {
	await page.getByLabel('Add Space').first().click();
	await page.getByLabel('Space Name').fill(spaceName);
	await page.getByRole('button', {name: 'Continue'}).click();
	await page.getByRole('button', {name: 'Continue'}).click();
}

async function deleteSpace(page, spaceName: string) {
	await clickMenuItem('Delete', page, spaceName);

	await expect(async () => {
		await page.getByRole('button', {name: 'Delete'}).click();
	}).toPass();

	await waitForAlert(page, `${spaceName} was successfully deleted.`);
}

async function getTableRowByText(page, text: string) {
	return page.locator('table.table tbody tr', {hasText: text}).first();
}

async function resetPermissions(page, folderName: string) {
	await clickMenuItem('Reset to Default Permissions', page, folderName);

	await expect(async () => {
		await page.getByRole('button', {name: 'OK'}).click();
	}).toPass();

	await waitForAlert(page, 'Permissions reset successfully.');
}

async function tickCheckBoxes(page, names: string[]) {
	for (const name of names) {
		await (await getTableRowByText(page, name))
			.getByRole('checkbox')
			.check();
	}
}

async function verifyPermissions({
	menuitem,
	objectName,
	page,
	permissions,
}: VerifyPermissionsOptions) {
	await clickMenuItem(menuitem, page, objectName);

	if (menuitem === 'Permissions') {
		page = new PermissionsPage(page);
	}
	else if (menuitem === 'Default Permissions') {
		page = new DefaultPermissionsPage(page);
	}

	await page.verifyPermissions(permissions);
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

			await clickMenuItem('Default Permissions', page, spaceName);

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
				menuitem: 'Default Permissions',
				objectName: folderName,
				page,
				permissions,
			});

			await page.getByRole('link', {name: folderName}).click();

			const subFolderName = 'SubFolder' + getRandomInt();

			await folderPage.createFolder(subFolderName);

			await verifyPermissions({
				menuitem: 'Default Permissions',
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
				page.getByRole('menuitem', {exact: true, name: 'Permissions'})
			).toBeVisible();
			await expect(
				page.getByRole('menuitem', {
					exact: true,
					name: 'Default Permissions',
				})
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
		const spaceName3 = 'Space' + getRandomInt();

		await page.goto(PORTLET_URLS.cmsAllSpaces);

		await createSpace(page, spaceName1);

		await page.goto(PORTLET_URLS.cmsAllSpaces);

		await createSpace(page, spaceName2);

		await page.goto(PORTLET_URLS.cmsAllSpaces);

		await createSpace(page, spaceName3);

		try {
			await page.goto(PORTLET_URLS.cmsAllSpaces);

			await tickCheckBoxes(page, [spaceName1, spaceName2]);
			await clickMenuItem('Default Permissions', page);

			const permissions1 = [
				{action: 'DELETE', checked: true, role: 'Power User'},
				{action: 'PERMISSIONS', checked: true, role: 'User'},
			];

			await defaultPermissionsPage.checkPermissionsAndSave(
				permissions1,
				true
			);

			await verifyPermissions({
				menuitem: 'Default Permissions',
				objectName: spaceName1,
				page,
				permissions: permissions1,
			});
			await verifyPermissions({
				menuitem: 'Default Permissions',
				objectName: spaceName2,
				page,
				permissions: permissions1,
			});

			const permissions2 = [
				{action: 'DELETE', checked: false, role: 'Power User'},
				{action: 'PERMISSIONS', checked: false, role: 'User'},
			];

			await verifyPermissions({
				menuitem: 'Default Permissions',
				objectName: spaceName3,
				page,
				permissions: permissions2,
			});
		}
		finally {
			await page.goto(PORTLET_URLS.cmsAllSpaces);

			await deleteSpace(page, spaceName1);
			await deleteSpace(page, spaceName2);
			await deleteSpace(page, spaceName3);
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

			await clickMenuItem('Default Permissions', page, spaceName1);

			const permissionsSpace1 = [
				{action: 'DELETE', checked: true, role: 'Power User'},
				{action: 'PERMISSIONS', checked: true, role: 'User'},
			];

			await defaultPermissionsPage.checkPermissionsAndSave(
				permissionsSpace1
			);

			await clickMenuItem('Default Permissions', page, spaceName2);

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

			await clickMenuItem('Default Permissions', page, folderName1);

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
				menuitem: 'Default Permissions',
				objectName: null,
				page,
				permissions: defaultPermissions,
			});

			await page.getByRole('link', {name: 'Clear'}).click();

			await tickCheckBoxes(page, [folderName2, folderName3]);
			await verifyPermissions({
				menuitem: 'Default Permissions',
				objectName: null,
				page,
				permissions: permissionsSpace2,
			});

			await verifyPermissions({
				menuitem: 'Default Permissions',
				objectName: folderName1,
				page,
				permissions: permissionsFolder1,
			});

			await page.getByRole('link', {name: folderName1}).click();

			const subFolderName1 = 'SubFolder' + getRandomInt();
			const subFolderName2 = 'SubFolder' + getRandomInt();

			await folderPage.createFolder(subFolderName1);
			await folderPage.createFolder(subFolderName2);

			await clickMenuItem('Default Permissions', page, subFolderName1);

			const permissionsSubFolder1 = [
				{action: 'ADD_ENTRY', checked: true, role: 'Power User'},
				{action: 'SUBSCRIBE', checked: true, role: 'User'},
			];

			await defaultPermissionsPage.checkPermissionsAndSave(
				permissionsSubFolder1
			);

			await tickCheckBoxes(page, [subFolderName1, subFolderName2]);
			await verifyPermissions({
				menuitem: 'Default Permissions',
				objectName: null,
				page,
				permissions: permissionsFolder1,
			});
			await verifyPermissions({
				menuitem: 'Default Permissions',
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

test(
	'Can propagate Default Permissions to existing assets',
	{tag: ['@LPD-67436']},
	async ({defaultPermissionsPage, folderPage, page, spaceSummaryPage}) => {
		test.setTimeout(90000);

		await page.goto(PORTLET_URLS.cmsAllSpaces);

		const spaceName = 'Space' + getRandomInt();

		await createSpace(page, spaceName);

		try {
			await spaceSummaryPage.viewAllContentLink.click();

			const folderName = 'Folder' + getRandomInt();

			await folderPage.createFolder(folderName);

			await page.getByRole('link', {name: folderName}).click();

			const subFolderName = 'SubFolder' + getRandomInt();

			await folderPage.createFolder(subFolderName);

			await page.goto(PORTLET_URLS.cmsAllSpaces);

			await clickMenuItem(
				'Edit and Propagate Default Permissions',
				page,
				spaceName
			);

			const permissions = [
				{action: 'DELETE', checked: true, role: 'Power User'},
				{action: 'PERMISSIONS', checked: true, role: 'User'},
			];

			await defaultPermissionsPage.checkPermissionsAndSave(
				permissions,
				false,
				true
			);

			await spaceSummaryPage.goto(spaceName);

			await spaceSummaryPage.viewAllContentLink.click();

			await verifyPermissions({
				menuitem: 'Default Permissions',
				objectName: folderName,
				page,
				permissions,
			});

			await clickMenuItem(
				'Edit and Propagate Default Permissions',
				page,
				folderName
			);

			const permissions2 = [
				{action: 'UPDATE', checked: true, role: 'Power User'},
				{action: 'VIEW', checked: true, role: 'User'},
			];

			await defaultPermissionsPage.checkPermissionsAndSave(
				permissions2,
				false,
				true
			);

			await page.getByRole('link', {name: folderName}).click();

			const allPermissions = permissions.concat(permissions2);

			await verifyPermissions({
				menuitem: 'Default Permissions',
				objectName: subFolderName,
				page,
				permissions: allPermissions,
			});
		}
		finally {
			await page.goto(PORTLET_URLS.cmsAllSpaces);

			await deleteSpace(page, spaceName);
		}
	}
);

test(
	'Reset permissions to the default permissions of the parent',
	{tag: '@LPD-62475'},
	async ({
		defaultPermissionsPage,
		folderPage,
		page,
		permissionsPage,
		spaceSummaryPage,
	}) => {
		await page.goto(PORTLET_URLS.cmsAllSpaces);

		const spaceName = 'Space' + getRandomInt();

		await createSpace(page, spaceName);

		try {
			await page.goto(PORTLET_URLS.cmsAllSpaces);

			await clickMenuItem('Default Permissions', page, spaceName);

			const parentPermissions = [
				{action: 'DELETE', checked: true, role: 'Power User'},
				{action: 'PERMISSIONS', checked: true, role: 'User'},
			];

			await defaultPermissionsPage.checkPermissionsAndSave(
				parentPermissions
			);

			await spaceSummaryPage.goto(spaceName);

			await spaceSummaryPage.viewAllContentLink.click();

			const folderName = 'Folder' + getRandomInt();

			await folderPage.createFolder(folderName);

			await verifyPermissions({
				menuitem: 'Permissions',
				objectName: folderName,
				page,
				permissions: parentPermissions,
			});

			await clickMenuItem('Permissions', page, folderName);

			let childPermissions = [
				{action: 'UPDATE', checked: true, role: 'Power User'},
				{action: 'VIEW', checked: true, role: 'User'},
			];

			await permissionsPage.checkPermissionsAndSave(childPermissions);

			await verifyPermissions({
				menuitem: 'Permissions',
				objectName: folderName,
				page,
				permissions: childPermissions,
			});

			await resetPermissions(page, folderName);

			childPermissions = [
				{action: 'UPDATE', checked: false, role: 'Power User'},
				{action: 'VIEW', checked: false, role: 'User'},
			];

			await verifyPermissions({
				menuitem: 'Permissions',
				objectName: folderName,
				page,
				permissions: childPermissions,
			});
		}
		finally {
			await page.goto(PORTLET_URLS.cmsAllSpaces);

			await deleteSpace(page, spaceName);
		}
	}
);
