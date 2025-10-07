/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {dataApiHelpersTest} from '../../../fixtures/dataApiHelpersTest';
import {loginTest} from '../../../fixtures/loginTest';
import {usersAndOrganizationsPagesTest} from '../../../fixtures/usersAndOrganizationsPagesTest';
import {getRandomInt} from '../../../utils/getRandomInt';
import {waitForAlert} from '../../../utils/waitForAlert';

export const test = mergeTests(
	dataApiHelpersTest,
	loginTest(),
	usersAndOrganizationsPagesTest
);

test(
	'Password confirmation modal is not shown when saving user with empty email and email address is not required',
	{tag: '@LPD-3172'},
	async ({editUserPage, page, usersAndOrganizationsPage}) => {
		page.on('dialog', (dialog) => dialog.accept());

		await usersAndOrganizationsPage.goToUsers();

		const userName = 'user' + getRandomInt();

		const addUser = async () => {
			await usersAndOrganizationsPage.addUserButton.click();
			await editUserPage.screenNameInput.fill(userName);
			await editUserPage.firstNameInput.fill(userName);
			await editUserPage.lastNameInput.fill(userName);
			await editUserPage.saveButton.click();

			await waitForAlert(
				page,
				'Success:The user was created successfully.'
			);
		};

		const deleteUser = async () => {
			await usersAndOrganizationsPage.goToUsers();

			await usersAndOrganizationsPage.deActivateUsers([userName]);
			await usersAndOrganizationsPage.filterUsers('inactive');
			await usersAndOrganizationsPage.deleteUsers([userName]);
		};

		await addUser();

		const birthday = '01/01/2000';

		await editUserPage.birthdayInput.fill(birthday);
		await editUserPage.birthdayInput.blur();
		await editUserPage.saveButton.click();

		await usersAndOrganizationsPage.goToUsers(true);

		await (
			await usersAndOrganizationsPage.usersTableRowLink(userName)
		).click();

		await expect(editUserPage.birthdayInput).toHaveValue(birthday);

		await deleteUser();

		await addUser();

		await editUserPage.emailAddressInput.fill(
			'test' + getRandomInt() + '@liferay.com'
		);
		await editUserPage.saveButton.click();

		await expect(editUserPage.yourPasswordInput).toBeVisible();

		await deleteUser();

		await addUser();

		await editUserPage.screenNameInput.fill('user' + getRandomInt());
		await editUserPage.saveButton.click();

		await expect(editUserPage.yourPasswordInput).toBeVisible();

		await deleteUser();
	}
);
