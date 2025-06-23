/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {dataApiHelpersTest} from '../../../fixtures/dataApiHelpersTest';
import {loginTest} from '../../../fixtures/loginTest';
import {userGroupsPageTest} from '../../../fixtures/userGroupsPageTest';
import {UsersAdminItemSelectorPageTest} from '../../../fixtures/usersAdminItemSelectorPagesTest';

export const test = mergeTests(
	dataApiHelpersTest,
	loginTest(),
	UsersAdminItemSelectorPageTest,
	userGroupsPageTest
);

test('LPD-1288 users item selector table clickable', async ({
	usersAdminItemSelectorPage,
}) => {
	const userName = 'Test Test';

	await usersAdminItemSelectorPage.goToOauth2Administration();
	await usersAdminItemSelectorPage.creationMenuNewButton.click();

	await expect(
		usersAdminItemSelectorPage.clientCredentialUserNameTextbox
	).toHaveValue('test');

	await usersAdminItemSelectorPage.selectUserButton.click();

	await expect(
		usersAdminItemSelectorPage.usersFrameSearchButton
	).toBeEnabled();

	await usersAdminItemSelectorPage.usersFrameTableRow(userName).click();

	await usersAdminItemSelectorPage
		.usersFrameTableRow(userName)
		.waitFor({state: 'detached'});

	await expect(
		usersAdminItemSelectorPage.clientCredentialUserNameTextbox
	).toHaveValue(userName);
});

test(
	'Searching by email works in users item selector',
	{tag: ['@LPD-57511']},
	async ({apiHelpers, page, userGroupsPage, usersAdminItemSelectorPage}) => {
		const user = await apiHelpers.headlessAdminUser.postUserAccount();
		const user2 = await apiHelpers.headlessAdminUser.postUserAccount();
		const userGroup = await apiHelpers.headlessAdminUser.postUserGroup();

		await userGroupsPage.goto(true);

		await userGroupsPage.userGroupsTableLink(userGroup.name, true).click();
		await page.waitForLoadState('networkidle');
		await userGroupsPage.creationMenuNewButton.click();

		await expect(
			usersAdminItemSelectorPage.assignUsersUserGroupsFrameTableRow(
				user.name,
				userGroup.name
			)
		).toBeVisible();
		await expect(
			usersAdminItemSelectorPage.assignUsersUserGroupsFrameTableRow(
				user2.name,
				userGroup.name
			)
		).toBeVisible();

		await usersAdminItemSelectorPage
			.assignUsersUserGroupsFrameSearchBar(userGroup.name)
			.fill(user.emailAddress);
		await usersAdminItemSelectorPage
			.assignUsersUserGroupsFrameSearchButton(userGroup.name)
			.click();

		await expect(
			usersAdminItemSelectorPage.assignUsersUserGroupsFrameTableRow(
				user.name,
				userGroup.name
			)
		).toBeVisible();
		await expect(
			usersAdminItemSelectorPage.assignUsersUserGroupsFrameTableRow(
				user2.name,
				userGroup.name
			)
		).toHaveCount(0);
	}
);
