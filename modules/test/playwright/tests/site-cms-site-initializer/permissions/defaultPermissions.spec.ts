/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {dataApiHelpersTest} from '../../../fixtures/dataApiHelpersTest';
import {featureFlagsTest} from '../../../fixtures/featureFlagsTest';
import {loginTest} from '../../../fixtures/loginTest';
import {getRandomInt} from '../../../utils/getRandomInt';
import {waitForAlert} from '../../../utils/waitForAlert';
import {cmsPagesTest} from './fixtures/cmsPagesTest';
import {DefaultPermissionsPage} from './pages/DefaultPermissionsPage';
import {PermissionsPage} from './pages/PermissionsPage';
import {
	clickMenuItem,
	createSpace,
	deleteSpace,
	getTableRowByText,
	goToAllSpaces,
	handleClickMenuItem,
	updateSpaceExternalReferenceCode,
} from './utils/permissions';

const test = mergeTests(
	cmsPagesTest,
	dataApiHelpersTest,
	featureFlagsTest({
		'LPD-11235': {enabled: false},
	}),
	loginTest()
);

type VerifyPermissionsOptions = {
	menuitem: string;
	objectName?: string;
	page: any;
	permissions: Array<{action: string; checked: boolean; role: string}>;
};

async function checkModalHeader(
	heading: string,
	menuitem: string,
	page,
	objectName?: string
) {
	await expect(async () => {
		await (await getTableRowByText(page, objectName))
			.getByRole('button', {name: 'Actions'})
			.click();

		await handleClickMenuItem(menuitem, page);

		await expect(page.getByRole('heading', {name: heading})).toBeVisible();
	}).toPass({timeout: 5000});

	await expect(async () => {
		await page.keyboard.press('Escape');

		await expect(
			page.getByRole('heading', {name: heading})
		).not.toBeVisible();
	}).toPass({timeout: 5000});
}

async function closeInfoAlert(page) {
	await page
		.locator('.alert-info')
		.getByRole('button', {name: 'Close'})
		.click();
}

async function resetPermissions(page, folderName?: string) {
	await expect(async () => {
		await clickMenuItem('Reset to Default Permissions', page, folderName);

		await expect(page.locator('.liferay-modal .modal-dialog')).toHaveClass(
			/modal-dialog-centered/
		);

		await page.getByRole('button', {name: 'Confirm'}).click();
	}).toPass({timeout: 5000});

	if (folderName) {
		await waitForAlert(page, 'Permissions reset successfully.');
	}
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

			const folderName = 'Folder' + getRandomInt();

			await spaceSummaryPage.createContentFolder(folderName);

			await spaceSummaryPage.viewAllContentLink.click();

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

			await contentsPage.createContent('Basic Web Content');

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
			const folderName = 'Folder' + getRandomInt();

			await spaceSummaryPage.createContentFolder(folderName);

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

			const folderName = 'Folder' + getRandomInt();

			await spaceSummaryPage.createContentFolder(folderName);

			await spaceSummaryPage.viewAllContentLink.click();

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
	'Reset permissions in bulk to the default permissions of the parent',
	{tag: '@LPD-68735'},
	async ({
		contentsPage,
		defaultPermissionsPage,
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

			await clickMenuItem('Default Permissions', page, spaceName);

			await page.getByTestId('tab-L_CONTENTS').click();

			await defaultPermissionsPage.checkPermissionsAndSave(
				parentPermissions
			);

			await spaceSummaryPage.goto(spaceName);

			const folderName = 'Folder' + getRandomInt();

			await spaceSummaryPage.createContentFolder(folderName);

			await spaceSummaryPage.viewAllContentLink.click();

			await verifyPermissions({
				menuitem: 'Permissions',
				objectName: folderName,
				page,
				permissions: parentPermissions,
			});

			await clickMenuItem('Permissions', page, folderName);

			let childFolderPermissions = [
				{action: 'ADD_ENTRY', checked: true, role: 'Power User'},
				{action: 'UPDATE', checked: true, role: 'User'},
			];

			await permissionsPage.checkPermissionsAndSave(
				childFolderPermissions
			);

			await verifyPermissions({
				menuitem: 'Permissions',
				objectName: folderName,
				page,
				permissions: childFolderPermissions,
			});

			await contentsPage.createContent('Basic Web Content');

			const contentName = 'Content' + getRandomInt();

			await contentsPage.fillData([{label: 'Title', value: contentName}]);

			await contentsPage.saveContent();

			await waitForAlert(
				page,
				`Success:${contentName} was created successfully.`
			);

			await clickMenuItem('Permissions', page, contentName);

			let childContentPermissions = [
				{
					action: 'DELETE_DISCUSSION',
					checked: true,
					role: 'Power User',
				},
				{action: 'UPDATE_DISCUSSION', checked: true, role: 'User'},
			];

			await permissionsPage.checkPermissionsAndSave(
				childContentPermissions
			);

			await verifyPermissions({
				menuitem: 'Permissions',
				objectName: contentName,
				page,
				permissions: childContentPermissions,
			});

			await spaceSummaryPage.goto(spaceName);

			await spaceSummaryPage.viewAllContentLink.click();

			await tickCheckBoxes(page, [folderName, contentName]);

			await resetPermissions(page);

			await page
				.locator('.alert-info')
				.getByRole('button', {name: 'Close'})
				.click();

			childFolderPermissions = [
				{action: 'ADD_ENTRY', checked: false, role: 'Power User'},
				{action: 'UPDATE', checked: false, role: 'User'},
			];

			await verifyPermissions({
				menuitem: 'Permissions',
				objectName: folderName,
				page,
				permissions: childFolderPermissions,
			});

			childContentPermissions = [
				{
					action: 'DELETE_DISCUSSION',
					checked: false,
					role: 'Power User',
				},
				{action: 'UPDATE_DISCUSSION', checked: false, role: 'User'},
			];

			await verifyPermissions({
				menuitem: 'Permissions',
				objectName: contentName,
				page,
				permissions: childContentPermissions,
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

			await filesPage.changeVisualizationMode('Table');

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

			await page.getByRole('button', {name: 'Cancel'}).click();
			await page.getByRole('button', {name: 'Clear'}).click();
			await filesPage.changeVisualizationMode('Gallery');
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

			await checkModalHeader(
				'Permissions',
				'Permissions',
				page,
				folderName
			);
			await checkModalHeader(
				'Edit Default Permissions',
				'Default Permissions',
				page,
				folderName
			);
			await checkModalHeader(
				'Edit Default Permissions',
				'Edit and Propagate Default Permissions',
				page,
				folderName
			);
			await checkModalHeader(
				'Confirm Reset to Default Permissions',
				'Reset to Default Permissions',
				page,
				folderName
			);
		}
		finally {
			await goToAllSpaces(page);

			await deleteSpace(page, spaceName);
		}
	}
);

test(
	'Edit default permissions in bulk by role',
	{tag: '@LPD-67434'},
	async ({defaultPermissionsPage, page, spaceSummaryPage}) => {
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

			await clickMenuItem('Default Permissions', page, spaceName);

			await page.getByTestId('tab-L_CONTENTS').click();

			await defaultPermissionsPage.checkPermissionsAndSave(
				parentPermissions
			);

			await spaceSummaryPage.goto(spaceName);

			const folderName1 = 'Folder' + getRandomInt();

			await spaceSummaryPage.createContentFolder(folderName1);

			const folderName2 = 'Folder' + getRandomInt();

			await spaceSummaryPage.createContentFolder(folderName2);

			await spaceSummaryPage.viewAllContentLink.click();

			await verifyPermissions({
				menuitem: 'Default Permissions',
				objectName: folderName1,
				page,
				permissions: parentPermissions,
			});
			await verifyPermissions({
				menuitem: 'Default Permissions',
				objectName: folderName2,
				page,
				permissions: parentPermissions,
			});

			await spaceSummaryPage.goto(spaceName);

			await spaceSummaryPage.viewAllContentLink.click();

			await tickCheckBoxes(page, [folderName1, folderName2]);

			await clickMenuItem('Edit Default Permissions by Role', page);

			await expect(defaultPermissionsPage.permissionsModal).toBeVisible();

			await defaultPermissionsPage.permissionsModalSelectRole.selectOption(
				'Power User'
			);

			await defaultPermissionsPage.permissionsModal
				.getByTestId(`row-checkbox-Power User_UPDATE`)
				.check();

			await defaultPermissionsPage.permissionsModalSaveButton.click();

			await closeInfoAlert(page);

			await defaultPermissionsPage.permissionsModalSelectRole.selectOption(
				'Supplier'
			);

			await defaultPermissionsPage.permissionsModal
				.getByTestId(`row-checkbox-Supplier_VIEW`)
				.check();

			await defaultPermissionsPage.permissionsModalSaveButton.click();

			await closeInfoAlert(page);

			await defaultPermissionsPage.permissionsModalCancelButton.click();

			const permissionsByRole = [
				{action: 'UPDATE', checked: true, role: 'Power User'},
				{action: 'VIEW', checked: true, role: 'Supplier'},
			];

			await verifyPermissions({
				menuitem: 'Default Permissions',
				objectName: folderName1,
				page,
				permissions: permissionsByRole,
			});
			await verifyPermissions({
				menuitem: 'Default Permissions',
				objectName: folderName2,
				page,
				permissions: permissionsByRole,
			});
		}
		finally {
			await goToAllSpaces(page);

			await deleteSpace(page, spaceName);
		}
	}
);

test(
	'Edit permissions in bulk by role',
	{tag: '@LPD-67434'},
	async ({contentsPage, defaultPermissionsPage, page, spaceSummaryPage}) => {
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

			await clickMenuItem('Default Permissions', page, spaceName);

			await page.getByTestId('tab-L_CONTENTS').click();

			await defaultPermissionsPage.checkPermissionsAndSave(
				parentPermissions
			);

			await spaceSummaryPage.goto(spaceName);

			const folderName = 'Folder' + getRandomInt();

			await spaceSummaryPage.createContentFolder(folderName);

			await spaceSummaryPage.viewAllContentLink.click();

			await verifyPermissions({
				menuitem: 'Permissions',
				objectName: folderName,
				page,
				permissions: parentPermissions,
			});

			await contentsPage.createContent('Basic Web Content');

			const contentName = 'Content' + getRandomInt();

			await contentsPage.fillData([{label: 'Title', value: contentName}]);

			await contentsPage.saveContent();

			await spaceSummaryPage.goto(spaceName);

			await spaceSummaryPage.viewAllContentLink.click();

			await tickCheckBoxes(page, [folderName, contentName]);

			await clickMenuItem('Edit Permissions by Role', page);

			await defaultPermissionsPage.permissionsModalSelectRole.selectOption(
				'Power User'
			);

			await expect(defaultPermissionsPage.permissionsModal).toBeVisible();

			await defaultPermissionsPage.permissionsModal
				.getByTestId(`row-checkbox-Power User_VIEW`)
				.check();

			await page.getByTestId('tab-L_CONTENTS').click();

			await defaultPermissionsPage.permissionsModal
				.getByTestId(`row-checkbox-Power User_UPDATE`)
				.check();

			await defaultPermissionsPage.permissionsModalSaveButton.click();

			await closeInfoAlert(page);

			await defaultPermissionsPage.permissionsModalCancelButton.click();

			const permissionsByRole1 = [
				{action: 'VIEW', checked: true, role: 'Power User'},
				{action: 'PERMISSIONS', checked: true, role: 'User'},
			];

			await verifyPermissions({
				menuitem: 'Permissions',
				objectName: folderName,
				page,
				permissions: permissionsByRole1,
			});

			const permissionsByRole2 = [
				{action: 'UPDATE', checked: true, role: 'Power User'},
				{action: 'PERMISSIONS', checked: true, role: 'User'},
			];

			await verifyPermissions({
				menuitem: 'Permissions',
				objectName: contentName,
				page,
				permissions: permissionsByRole2,
			});
		}
		finally {
			await goToAllSpaces(page);

			await deleteSpace(page, spaceName);
		}
	}
);

test(
	'Space default permissions remain available after changing the Space ERC',
	{tag: '@LPD-94507'},
	async ({apiHelpers, defaultPermissionsPage, page}) => {
		test.setTimeout(90000);

		const spaceName = 'Space' + getRandomInt();

		// Create a Space and configure its default permissions

		await goToAllSpaces(page);

		await createSpace(page, spaceName);

		try {
			await goToAllSpaces(page);

			await clickMenuItem('Default Permissions', page, spaceName);

			const permissions = [
				{action: 'DELETE', checked: true, role: 'Power User'},
				{action: 'PERMISSIONS', checked: true, role: 'User'},
			];

			await defaultPermissionsPage.checkPermissionsAndSave(permissions);

			// Change the Space ERC from Space Settings

			await updateSpaceExternalReferenceCode(
				apiHelpers,
				spaceName,
				'erc-' + getRandomInt()
			);

			// LPP-64409: the previously configured default permissions are
			// still visible after the change

			await goToAllSpaces(page);

			await verifyPermissions({
				menuitem: 'Default Permissions',
				objectName: spaceName,
				page,
				permissions,
			});

			// New default permissions can still be added without a system error

			await clickMenuItem('Default Permissions', page, spaceName);

			await defaultPermissionsPage.checkPermissionsAndSave([
				{action: 'VIEW', role: 'User'},
			]);
		}
		finally {
			await goToAllSpaces(page);

			await deleteSpace(page, spaceName);
		}
	}
);

test(
	'Folder default permission management works after changing the Space ERC',
	{tag: '@LPD-94507'},
	async ({apiHelpers, defaultPermissionsPage, page, spaceSummaryPage}) => {
		test.setTimeout(120000);

		const spaceName = 'Space' + getRandomInt();

		await goToAllSpaces(page);

		await createSpace(page, spaceName);

		try {

			// Create a folder before the ERC change

			await spaceSummaryPage.goto(spaceName);

			const folderBefore = 'Folder' + getRandomInt();

			await spaceSummaryPage.createContentFolder(folderBefore);

			// Change the Space ERC from Space Settings

			await updateSpaceExternalReferenceCode(
				apiHelpers,
				spaceName,
				'erc-' + getRandomInt()
			);

			// Create a folder after the ERC change

			await spaceSummaryPage.goto(spaceName);

			const folderAfter = 'Folder' + getRandomInt();

			await spaceSummaryPage.createContentFolder(folderAfter);

			await spaceSummaryPage.viewAllContentLink.click();

			// LPP-64362: folder Default Permissions and Edit and Propagate
			// Default Permissions stay usable for folders created both before
			// and after the ERC change

			for (const folderName of [folderBefore, folderAfter]) {
				await clickMenuItem('Default Permissions', page, folderName);

				await defaultPermissionsPage.checkPermissionsAndSave([
					{action: 'VIEW', role: 'User'},
				]);

				await clickMenuItem(
					'Edit and Propagate Default Permissions',
					page,
					folderName
				);

				await defaultPermissionsPage.checkPermissionsAndSave(
					[{action: 'UPDATE', role: 'Power User'}],
					false,
					true
				);
			}
		}
		finally {
			await goToAllSpaces(page);

			await deleteSpace(page, spaceName);
		}
	}
);
