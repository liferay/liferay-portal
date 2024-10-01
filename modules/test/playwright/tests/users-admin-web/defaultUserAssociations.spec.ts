/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {dataApiHelpersTest} from '../../fixtures/dataApiHelpersTest';
import {instanceSettingsPagesTest} from '../../fixtures/instanceSettingsPagesTest';
import {loginTest} from '../../fixtures/loginTest';
import {usersAndOrganizationsPagesTest} from '../../fixtures/usersAndOrganizationsPagesTest';
import {waitForAlert} from '../../utils/waitForAlert';

export const test = mergeTests(
	dataApiHelpersTest,
	loginTest(),
	instanceSettingsPagesTest,
	usersAndOrganizationsPagesTest
);

test('LPD-30006 Configure default userGroup associations', async ({
	apiHelpers,
	defaultUserAssociationsPage,
	editUserPage,
	instanceSettingsPage,
	page,
	usersAndOrganizationsPage,
}) => {
	const userGroup = await apiHelpers.headlessAdminUser.postUserGroup();

	await instanceSettingsPage.goToInstanceSetting(
		'Users',
		'Default User Associations'
	);

	await defaultUserAssociationsPage.userGroupsInput.fill(userGroup.name);
	await defaultUserAssociationsPage.saveButton.click();

	await waitForAlert(page);

	const user = await apiHelpers.headlessAdminUser.postUserAccount();

	await usersAndOrganizationsPage.goToUsers();

	await (
		await usersAndOrganizationsPage.usersTableRowLink(user.alternateName)
	).click();

	await editUserPage.membershipsLink.click();

	await expect(
		(
			await editUserPage.membershipsUserGroupsTableRow(
				0,
				userGroup.name,
				true
			)
		).row
	).toBeVisible();
});
