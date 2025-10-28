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
	await expect(async () => {
		await clickMenuItem('Delete', page, spaceName);

		await page.getByRole('button', {name: 'Delete'}).click();
	}).toPass({timeout: 5000});

	await waitForAlert(page, `${spaceName} was successfully deleted.`);
}

async function getTableRowByText(page, text: string) {
	return page.locator('table.table tbody tr', {hasText: text}).first();
}

async function goToAllSpaces(page) {
	await expect(async () => {
		await page.goto(PORTLET_URLS.cmsAllSpaces);

		await expect(
			page.getByRole('heading', {exact: true, name: 'All Spaces'})
		).toBeVisible();
	}).toPass({timeout: 10000});
}

async function resetPermissions(page, folderName: string) {
	await expect(async () => {
		await clickMenuItem('Reset to Default Permissions', page, folderName);

		await page.getByRole('button', {name: 'OK'}).click();
	}).toPass({timeout: 5000});

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
	async ({
		contentsPage,
		defaultPermissionsPage,
		folderPage,
		page,
		spaceSummaryPage,
	}) => {
		test.setTimeout(90000);

		await goToAllSpaces(page);

		const spaceName = 'Space' + getRandomInt();

		await createSpace(page, spaceName);

		try {
			await goToAllSpaces(page);

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

			await contentsPage.createContent('Basic Content');

			await contentsPage.fillData([{label: 'Title', value: '1234'}]);

			await contentsPage.saveContent();

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
			await goToAllSpaces(page);

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

		await goToAllSpaces(page);

		await createSpace(page, spaceName1);

		await goToAllSpaces(page);

		await createSpace(page, spaceName2);

		await goToAllSpaces(page);

		await createSpace(page, spaceName3);

		try {
			await goToAllSpaces(page);

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
			await goToAllSpaces(page);

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

		await goToAllSpaces(page);

		await createSpace(page, spaceName1);

		await goToAllSpaces(page);

		await createSpace(page, spaceName2);

		try {
			await goToAllSpaces(page);

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

			await page.getByRole('button', {name: 'Clear'}).click();

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
			await goToAllSpaces(page);

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

		await goToAllSpaces(page);

		const spaceName = 'Space' + getRandomInt();

		await createSpace(page, spaceName);

		try {
			await spaceSummaryPage.viewAllContentLink.click();

			const folderName = 'Folder' + getRandomInt();

			await folderPage.createFolder(folderName);

			await page.getByRole('link', {name: folderName}).click();

			const subFolderName = 'SubFolder' + getRandomInt();

			await folderPage.createFolder(subFolderName);

			await goToAllSpaces(page);

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
			await goToAllSpaces(page);

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
		await goToAllSpaces(page);

		const spaceName = 'Space' + getRandomInt();

		await createSpace(page, spaceName);

		try {
			await goToAllSpaces(page);

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
				{action: 'ADD_ENTRY', checked: true, role: 'User'},
				{action: 'UPDATE', checked: true, role: 'Power User'},
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
				{action: 'ADD_ENTRY', checked: false, role: 'User'},
				{action: 'UPDATE', checked: false, role: 'Power User'},
			];

			await verifyPermissions({
				menuitem: 'Permissions',
				objectName: folderName,
				page,
				permissions: childPermissions,
			});
		}
		finally {
			await goToAllSpaces(page);

			await deleteSpace(page, spaceName);
		}
	}
);

test(
	'Display only relevant permission tabs based on the section',
	{tag: '@LPD-67530'},
	async ({contentsPage, defaultPermissionsPage, filesPage, page}) => {
		const spaceName = 'Space' + getRandomInt();

		await goToAllSpaces(page);

		await createSpace(page, spaceName);

		try {
			await contentsPage.goto();

			const folderName1 = 'Folder' + getRandomInt();
			const folderName2 = 'Folder' + getRandomInt();

			await contentsPage.createFolder(folderName1, spaceName);
			await contentsPage.createFolder(folderName2, spaceName);

			await tickCheckBoxes(page, [folderName1, folderName2]);

			await clickMenuItem('Default Permissions', page);

			await expect(page.getByTestId('tab-L_CONTENTS')).toBeVisible();
			await expect(page.getByTestId('tab-L_FILES')).not.toBeVisible();
			await expect(
				page.getByTestId('tab-OBJECT_ENTRY_FOLDERS')
			).toBeVisible();

			await defaultPermissionsPage.permissionsModalCancelButton.click();

			await clickMenuItem('Default Permissions', page, folderName1);

			await expect(page.getByTestId('tab-L_CONTENTS')).toBeVisible();
			await expect(page.getByTestId('tab-L_FILES')).not.toBeVisible();
			await expect(
				page.getByTestId('tab-OBJECT_ENTRY_FOLDERS')
			).toBeVisible();

			await defaultPermissionsPage.permissionsModalCancelButton.click();

			await filesPage.goto();

			const folderName3 = 'Folder' + getRandomInt();
			const folderName4 = 'Folder' + getRandomInt();

			await filesPage.createFolder(folderName3, spaceName);
			await filesPage.createFolder(folderName4, spaceName);

			await tickCheckBoxes(page, [folderName3, folderName4]);

			await clickMenuItem('Default Permissions', page);

			await expect(page.getByTestId('tab-L_CONTENTS')).not.toBeVisible();
			await expect(page.getByTestId('tab-L_FILES')).toBeVisible();
			await expect(
				page.getByTestId('tab-OBJECT_ENTRY_FOLDERS')
			).toBeVisible();

			await defaultPermissionsPage.permissionsModalCancelButton.click();

			await clickMenuItem('Default Permissions', page, folderName3);

			await expect(page.getByTestId('tab-L_CONTENTS')).not.toBeVisible();
			await expect(page.getByTestId('tab-L_FILES')).toBeVisible();
			await expect(
				page.getByTestId('tab-OBJECT_ENTRY_FOLDERS')
			).toBeVisible();
		}
		finally {
			await goToAllSpaces(page);

			await deleteSpace(page, spaceName);
		}
	}
);

test(
	'Action are working also in cms home page',
	{tag: '@LPD-67530'},
	async ({page}) => {
		const spaceName = 'Space' + getRandomInt();

		await goToAllSpaces(page);

		await createSpace(page, spaceName);

		try {
			await expect(async () => {
				await page
					.getByText('No Content Yet')
					.locator('../..')
					.getByTestId('fdsCreationActionButton')
					.click();
				await page.getByRole('menuitem', {name: 'Folder'}).click();

				await expect(page.getByLabel('Name')).toBeVisible();
			}).toPass({timeout: 5000});

			const folderName = String(getRandomInt());

			await page.getByLabel('Name').fill(folderName);
			await page.getByRole('button', {name: 'Save'}).click();

			await expect(
				page.getByRole('link', {name: folderName}).first()
			).toBeVisible();

			await expect(async () => {
				await page
					.getByRole('button', {exact: true, name: 'Actions'})
					.click();
				await page
					.getByRole('menuitem', {exact: true, name: 'Permissions'})
					.click();

				await expect(
					page.getByRole('heading', {name: 'Permissions'})
				).toBeVisible();
			}).toPass({timeout: 5000});

			await expect(async () => {
				await page
					.getByRole('button', {exact: true, name: 'Close'})
					.click();

				await expect(
					page.getByRole('heading', {name: 'Permissions'})
				).not.toBeVisible();
			}).toPass({timeout: 5000});

			await expect(async () => {
				await page
					.getByRole('button', {exact: true, name: 'Actions'})
					.click();
				await page
					.getByRole('menuitem', {
						exact: true,
						name: 'Default Permissions',
					})
					.click();

				await expect(
					page.getByRole('heading', {
						name: 'Edit Default Permissions',
					})
				).toBeVisible();
			}).toPass({timeout: 5000});

			await expect(async () => {
				await page
					.getByRole('button', {exact: true, name: 'Close'})
					.click();

				await expect(
					page.getByRole('heading', {
						name: 'Edit Default Permissions',
					})
				).not.toBeVisible();
			}).toPass({timeout: 5000});

			await expect(async () => {
				await page
					.getByRole('button', {exact: true, name: 'Actions'})
					.click();
				await page
					.getByRole('menuitem', {name: 'Edit and Propagate Default'})
					.click();

				await expect(
					page.getByRole('heading', {
						name: 'Edit Default Permissions',
					})
				).toBeVisible();
			}).toPass({timeout: 5000});

			await expect(async () => {
				await page
					.getByRole('button', {exact: true, name: 'Close'})
					.click();

				await expect(
					page.getByRole('heading', {
						name: 'Edit Default Permissions',
					})
				).not.toBeVisible();
			}).toPass({timeout: 5000});

			await expect(async () => {
				await page
					.getByRole('button', {exact: true, name: 'Actions'})
					.click();
				await page
					.getByRole('menuitem', {
						name: 'Reset to Default Permissions',
					})
					.click();

				await expect(
					page.getByRole('heading', {
						name: 'Confirm Reset Permissions -',
					})
				).toBeVisible();

				await page.getByRole('button', {name: 'Cancel'}).click();
			}).toPass({timeout: 5000});
		}
		finally {
			await goToAllSpaces(page);

			await deleteSpace(page, spaceName);
		}
	}
);
