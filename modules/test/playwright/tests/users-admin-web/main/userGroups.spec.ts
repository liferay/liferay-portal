/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {dataApiHelpersTest} from '../../../fixtures/dataApiHelpersTest';
import {loginTest} from '../../../fixtures/loginTest';
import {userGroupsPageTest} from '../../../fixtures/userGroupsPageTest';
import getRandomString from '../../../utils/getRandomString';
import {waitForAlert} from '../../../utils/waitForAlert';

export const test = mergeTests(
	dataApiHelpersTest,
	loginTest(),
	userGroupsPageTest
);

test(
	'Can add, edit and delete user groups',
	{tag: '@LPD-57361'},
	async ({page, userGroupsPage}) => {
		page.on('dialog', async (dialog) => await dialog.accept());

		const userGroupName = getRandomString();

		await userGroupsPage.goto();

		await userGroupsPage.newUserGroupButton.click();
		await userGroupsPage.nameInput.fill(userGroupName);
		await userGroupsPage.saveButton.click();

		await waitForAlert(page);

		await expect(
			userGroupsPage.userGroupsTableCell(userGroupName)
		).toBeVisible();

		const newUserGroupName = getRandomString();

		await (
			await userGroupsPage.userGroupsTableRowActions(userGroupName)
		).click();
		await userGroupsPage.editUserGroupMenuItem.click();
		await userGroupsPage.nameInput.fill(newUserGroupName);
		await userGroupsPage.saveButton.click();

		await waitForAlert(page);

		await expect(
			userGroupsPage.userGroupsTableCell(newUserGroupName)
		).toBeVisible();

		await (
			await userGroupsPage.userGroupsTableCheckbox(newUserGroupName)
		).check();
		await userGroupsPage.deleteButton.click();

		await waitForAlert(page);

		await expect(userGroupsPage.noUserGroupsMessage).toBeVisible();
	}
);

test(
	'Can add and remove user group member',
	{tag: '@LPD-57361'},
	async ({apiHelpers, page, userGroupsPage}) => {
		const user = await apiHelpers.headlessAdminUser.postUserAccount();
		const userGroup = await apiHelpers.headlessAdminUser.postUserGroup();

		await userGroupsPage.goto();

		await expect(async () => {
			await (
				await userGroupsPage.userGroupsTableRowActions(userGroup.name)
			).click();
			await userGroupsPage.assignMembersMenuItem.click();

			await expect(userGroupsPage.noUsersMessage).toBeVisible();

			await userGroupsPage.newUserButton.click();
			await (
				await userGroupsPage.addUsersTable.rowCheckbox(user.name)
			).check();
			await userGroupsPage.addUsersIFrameAddButton.click();

			await waitForAlert(page);
		}).toPass();

		await expect(
			userGroupsPage.userGroupUsersTable.cell(user.name)
		).toBeVisible();

		await (
			await userGroupsPage.userGroupUsersTable.rowActions(user.name)
		).click();
		await userGroupsPage.removeUserMenuItem.click();

		await waitForAlert(page);

		await expect(userGroupsPage.noUsersMessage).toBeVisible();
	}
);

test(
	'Cannot delete user group with assigned members',
	{tag: '@LPD-57361'},
	async ({apiHelpers, page, userGroupsPage}) => {
		page.on('dialog', async (dialog) => await dialog.accept());

		const user = await apiHelpers.headlessAdminUser.postUserAccount();
		const userGroup = await apiHelpers.headlessAdminUser.postUserGroup();

		await apiHelpers.headlessAdminUser.assignUsersToUserGroup(
			userGroup.id,
			[user.id]
		);

		await userGroupsPage.goto();

		await (
			await userGroupsPage.userGroupsTableCheckbox(userGroup.name)
		).check();
		await userGroupsPage.deleteButton.click();

		await expect(
			userGroupsPage.deleteUserGroupWithUsersErrorMessage
		).toBeVisible();
		await expect(
			userGroupsPage.userGroupsTableCell(userGroup.name)
		).toBeVisible();
	}
);

test(
	'Can search current members assigned to a user group',
	{tag: '@LPD-57361'},
	async ({apiHelpers, userGroupsPage}) => {
		const user1 = await apiHelpers.headlessAdminUser.postUserAccount();
		const user2 = await apiHelpers.headlessAdminUser.postUserAccount();
		const userGroup = await apiHelpers.headlessAdminUser.postUserGroup();

		await apiHelpers.headlessAdminUser.assignUsersToUserGroup(
			userGroup.id,
			[user1.id, user2.id]
		);

		await userGroupsPage.goto();

		await (
			await userGroupsPage.userGroupsTableRowLink(userGroup.name)
		).click();

		await expect(
			userGroupsPage.userGroupUsersTable.cell(user2.alternateName)
		).toBeVisible();

		await userGroupsPage.userGroupUsersTable.search(user1.alternateName);

		await expect(
			userGroupsPage.userGroupUsersTable.cell(user1.alternateName)
		).toBeVisible();
		await expect(
			userGroupsPage.userGroupUsersTable.cell(user2.alternateName)
		).not.toBeVisible();
	}
);
