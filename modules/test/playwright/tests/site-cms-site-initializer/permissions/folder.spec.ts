/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {dataApiHelpersTest} from '../../../fixtures/dataApiHelpersTest';
import {featureFlagsTest} from '../../../fixtures/featureFlagsTest';
import {loginTest} from '../../../fixtures/loginTest';
import {addCMSAdministrator} from '../../../utils/addCMSAdministrator';
import getRandomString from '../../../utils/getRandomString';
import {performUserSwitch, userData} from '../../../utils/performLogin';
import {waitForAlert} from '../../../utils/waitForAlert';
import {cmsPagesTest} from './fixtures/cmsPagesTest';

const test = mergeTests(
	cmsPagesTest,
	dataApiHelpersTest,
	featureFlagsTest({
		'LPD-11235': {enabled: false},
		'LPD-17564': {enabled: true},
	}),
	loginTest()
);

test(
	'CMS Administrator can copy a folder within the same parent folder',
	{tag: '@LPD-83489'},
	async ({
		apiHelpers,
		assetsPage,
		copyFolderModalPage,
		page,
		spaceSummaryPage,
	}) => {
		const spaceName = `Space ${getRandomString()}`;
		const folder1Name = `Folder ${getRandomString()}`;
		const folder2Name = `Folder ${getRandomString()}`;

		let user;

		await test.step('Create a new Space', async () => {
			await apiHelpers.headlessAssetLibrary.createAssetLibrary({
				name: spaceName,
				settings: {},
				type: 'Space',
			});
		});

		await test.step('Create a user and assign CMS Administrator role and the space', async () => {
			user = await addCMSAdministrator(apiHelpers);

			await spaceSummaryPage.goto(spaceName);

			await spaceSummaryPage.addUserOrUserGroup(user.name, 'users');
		});

		await test.step('Create two folders inside the Space', async () => {
			await spaceSummaryPage.goto(spaceName);

			await spaceSummaryPage.createContentFolder(folder1Name);
			await spaceSummaryPage.createContentFolder(folder2Name);
		});

		await test.step('Login as CMS Administrator', async () => {
			await performUserSwitch(page, user.alternateName);
		});

		await test.step('Copy a folder within the same root parent folder', async () => {
			await assetsPage.gotoContents();

			await assetsPage.execItemAction({
				action: 'Copy To',
				filter: folder1Name,
				parentAction: 'Copy',
			});

			await copyFolderModalPage.space(spaceName).click();

			await expect(async () => {
				await copyFolderModalPage
					.folderRadio('Contents')
					.click({timeout: 500});

				await expect(copyFolderModalPage.selectButton).toBeEnabled({
					timeout: 500,
				});
			}).toPass({timeout: 5000});

			await copyFolderModalPage.selectButton.click();
			await copyFolderModalPage.duplicateDialog.waitFor();
			await copyFolderModalPage.keepBothOption.click();
			await copyFolderModalPage.continueButton.click();

			await waitForAlert(page, `was successfully copied to`);
		});

		await test.step('Copy a folder inside another non-root folder', async () => {
			await assetsPage.gotoContents();

			await assetsPage.execItemAction({
				action: 'Copy To',
				filter: folder1Name,
				parentAction: 'Copy',
			});

			await copyFolderModalPage.spaceInModal(spaceName).click();
			await copyFolderModalPage.navigableFolder('Contents').click();

			await expect(async () => {
				await copyFolderModalPage
					.folderRadio(folder2Name)
					.click({timeout: 500});

				await expect(copyFolderModalPage.selectButton).toBeEnabled({
					timeout: 500,
				});
			}).toPass({timeout: 5000});

			await copyFolderModalPage.selectButton.click();

			await waitForAlert(page, `was successfully copied to`);

			await assetsPage.gotoContents();

			await page.getByRole('link', {name: folder2Name}).click();

			await assetsPage.execItemAction({
				action: 'Copy To',
				filter: folder1Name,
				parentAction: 'Copy',
			});

			await copyFolderModalPage.spaceInModal(spaceName).click();
			await copyFolderModalPage.navigableFolder('Contents').click();

			await expect(async () => {
				await copyFolderModalPage
					.folderRadio(folder2Name)
					.click({timeout: 500});

				await expect(copyFolderModalPage.selectButton).toBeEnabled({
					timeout: 500,
				});
			}).toPass({timeout: 5000});

			await copyFolderModalPage.selectButton.click();
			await copyFolderModalPage.duplicateDialog.waitFor();
			await copyFolderModalPage.keepBothOption.click();
			await copyFolderModalPage.continueButton.click();

			await waitForAlert(page, `was successfully copied to`);
		});
	}
);

test(
	'CMS Administrator can bulk copy a folder within the same parent folder',
	{tag: '@LPD-83489'},
	async ({
		apiHelpers,
		assetsPage,
		copyFolderModalPage,
		page,
		spaceSummaryPage,
	}) => {
		const spaceName = `Space ${getRandomString()}`;
		const folder1Name = `Folder ${getRandomString()}`;
		const folder2Name = `Folder ${getRandomString()}`;

		let user;

		await test.step('Create a new Space', async () => {
			await apiHelpers.headlessAssetLibrary.createAssetLibrary({
				name: spaceName,
				settings: {},
				type: 'Space',
			});
		});

		await test.step('Create a user and assign CMS Administrator role and the space', async () => {
			user = await apiHelpers.headlessAdminUser.postUserAccount();

			userData[user.alternateName] = {
				name: user.givenName,
				password: 'test',
				surname: user.familyName,
			};

			const cmsAdminRole =
				await apiHelpers.headlessAdminUser.getRoleByName(
					'CMS Administrator'
				);

			await apiHelpers.headlessAdminUser.postRoleUserAccountAssociation(
				cmsAdminRole.id,
				Number(user.id)
			);

			await spaceSummaryPage.goto(spaceName);

			await spaceSummaryPage.addUserOrUserGroup(user.name, 'users');
		});

		await test.step('Create two folders inside the Space', async () => {
			await spaceSummaryPage.goto(spaceName);

			await spaceSummaryPage.createContentFolder(folder1Name);
			await spaceSummaryPage.createContentFolder(folder2Name);
		});

		await test.step('Login as CMS Administrator', async () => {
			await performUserSwitch(page, user.alternateName);
		});

		await test.step('Bulk copy a folder within the same root parent folder', async () => {
			await assetsPage.gotoContents();

			await assetsPage.selectItems([folder1Name]);

			await assetsPage.execBulkItemAction('Copy To');

			await copyFolderModalPage.space(spaceName).click();

			await expect(async () => {
				await copyFolderModalPage
					.folderRadio('Contents')
					.click({timeout: 500});

				await expect(copyFolderModalPage.selectButton).toBeEnabled({
					timeout: 500,
				});
			}).toPass({timeout: 5000});

			await copyFolderModalPage.selectButton.click();

			await waitForAlert(page, `was successfully copied to`);
		});

		await test.step('Bulk copy a folder inside another non-root folder', async () => {
			await assetsPage.gotoContents();

			await page
				.locator('tr', {hasText: folder1Name})
				.first()
				.getByRole('checkbox')
				.check();

			await assetsPage.execBulkItemAction('Copy To');

			await copyFolderModalPage.spaceInModal(spaceName).click();
			await copyFolderModalPage.navigableFolder('Contents').click();

			await expect(async () => {
				await copyFolderModalPage
					.folderRadio(folder2Name)
					.click({timeout: 500});

				await expect(copyFolderModalPage.selectButton).toBeEnabled({
					timeout: 500,
				});
			}).toPass({timeout: 5000});

			await copyFolderModalPage.selectButton.click();

			await waitForAlert(page, `was successfully copied to`);

			await assetsPage.gotoContents();

			await page.getByRole('link', {name: folder2Name}).click();

			await assetsPage.selectItems([folder1Name]);

			await assetsPage.execBulkItemAction('Copy To');

			await copyFolderModalPage.spaceInModal(spaceName).click();
			await copyFolderModalPage.navigableFolder('Contents').click();

			await expect(async () => {
				await copyFolderModalPage
					.folderRadio(folder2Name)
					.click({timeout: 500});

				await expect(copyFolderModalPage.selectButton).toBeEnabled({
					timeout: 500,
				});
			}).toPass({timeout: 5000});

			await copyFolderModalPage.selectButton.click();

			await waitForAlert(page, `was successfully copied to`);
		});
	}
);
