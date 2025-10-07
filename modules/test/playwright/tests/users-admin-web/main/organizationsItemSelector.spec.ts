/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {dataApiHelpersTest} from '../../../fixtures/dataApiHelpersTest';
import {loginTest} from '../../../fixtures/loginTest';
import {usersAndOrganizationsPagesTest} from '../../../fixtures/usersAndOrganizationsPagesTest';
import {EditUserPage} from '../../../pages/users-admin-web/EditUserPage';
import {UsersAndOrganizationsPage} from '../../../pages/users-admin-web/UsersAndOrganizationsPage';
import {getRandomInt} from '../../../utils/getRandomInt';

export const test = mergeTests(
	dataApiHelpersTest,
	loginTest(),
	usersAndOrganizationsPagesTest
);

test('LPD-24824 User only sees the organizations they have permission to view', async ({
	apiHelpers,
	context,
	usersAndOrganizationsPage,
}) => {
	const organization1 = await apiHelpers.headlessAdminUser.postOrganization({
		name: 'Organization' + getRandomInt(),
	});
	const organization2 = await apiHelpers.headlessAdminUser.postOrganization({
		name: 'Organization' + getRandomInt(),
	});
	const user = await apiHelpers.headlessAdminUser.postUserAccount();
	await apiHelpers.headlessAdminUser.assignUserToOrganizationByEmailAddress(
		organization1.id,
		user.emailAddress
	);
	const organizationOwnerRole =
		await apiHelpers.headlessAdminUser.getRoleByName('Organization Owner');
	await apiHelpers.headlessAdminUser.assignUserToOrganizationRole(
		String(organizationOwnerRole.id),
		user.id,
		organization1.id
	);

	await usersAndOrganizationsPage.goToUsers();
	await (
		await usersAndOrganizationsPage.usersTableRowActions(
			`${user.alternateName}`
		)
	).click();

	const pagePromise = context.waitForEvent('page');
	await usersAndOrganizationsPage.impersonateUserMenuItem.click();

	const newPage = await pagePromise;
	const newPageUsersAndOrganizationsPage = new UsersAndOrganizationsPage(
		newPage
	);
	await newPageUsersAndOrganizationsPage.goToMyOrganizations();
	await (
		await newPageUsersAndOrganizationsPage.myOrganizationsTableRowLink(
			organization1.name
		)
	).click();
	await (
		await newPageUsersAndOrganizationsPage.organizationUsersTableRowLink(
			user.name
		)
	).click();

	const newPageEditUserPage = new EditUserPage(newPage);
	await newPageEditUserPage.organizationsLink.click();
	await newPageEditUserPage.myOrganizationsSelectOrganizationButton.click();

	await expect(
		newPageEditUserPage.myOrganizationsSelectOrganizationsTable.getByText(
			`${organization2.name}`
		)
	).toBeHidden();
	await expect(
		(
			await newPageEditUserPage.myOrganizationsSelectOrganizationsTableRow(
				1,
				organization1.name,
				true
			)
		).row
	).toBeVisible();
});
