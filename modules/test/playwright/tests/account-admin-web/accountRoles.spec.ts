/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Page, expect, mergeTests} from '@playwright/test';

import {accountsPagesTest} from '../../fixtures/accountsPagesTest';
import {dataApiHelpersTest} from '../../fixtures/dataApiHelpersTest';
import {loginTest} from '../../fixtures/loginTest';
import {rolesPagesTest} from '../../fixtures/rolesPagesTest';
import {usersAndOrganizationsPagesTest} from '../../fixtures/usersAndOrganizationsPagesTest';
import {DataApiHelpers} from '../../helpers/ApiHelpers';
import {TRole} from '../../helpers/HeadlessAdminUserApiHelper';
import getRandomString from '../../utils/getRandomString';
import {
	performLoginViaApi,
	performLogout,
	userData,
} from '../../utils/performLogin';
import {waitForAlert} from '../../utils/waitForAlert';
import {addAccountRole, initAccountManager} from './utils/roles';

export const test = mergeTests(
	accountsPagesTest,
	dataApiHelpersTest,
	loginTest(),
	rolesPagesTest,
	usersAndOrganizationsPagesTest
);

const setupPermissionsTest = async (
	apiHelpers: DataApiHelpers,
	page: Page,
	accountEntyActionIds = ['UPDATE', 'VIEW_ACCOUNT_ROLES']
) => {
	const companyId = await page.evaluate(() => {
		return Liferay.ThemeDisplay.getCompanyId();
	});

	const account = await apiHelpers.headlessAdminUser.postAccount();

	apiHelpers.data.push({id: account.id, type: 'account'});

	const userAccount = await apiHelpers.headlessAdminUser.postUserAccount();

	userData[userAccount.alternateName] = {
		name: userAccount.givenName,
		password: 'test',
		surname: userAccount.familyName,
	};

	await apiHelpers.headlessAdminUser.assignUserToAccountByEmailAddress(
		account.id,
		[userAccount.emailAddress]
	);

	const regularRole = await apiHelpers.headlessAdminUser.postRole({
		name: getRandomString(),
		rolePermissions: [
			{
				actionIds: ['ACCESS_IN_CONTROL_PANEL'],
				primaryKey: companyId,
				resourceName:
					'com_liferay_account_admin_web_internal_portlet_AccountEntriesAdminPortlet',
				scope: 1,
			},
		],
		roleType: 'regular',
	});

	await apiHelpers.headlessAdminUser.assignUserToRole(
		regularRole.externalReferenceCode,
		userAccount.id
	);

	const {accountRole, role} = await addAccountRole(apiHelpers, account.id, [
		{
			actionIds: accountEntyActionIds,
			primaryKey: '0',
			resourceName: 'com.liferay.account.model.AccountEntry',
			scope: 3,
		},
		{
			actionIds: ['VIEW'],
			primaryKey: '0',
			resourceName: 'com.liferay.account.model.AccountRole',
			scope: 3,
		},
	]);

	await apiHelpers.headlessAdminUser.assignUserToAccountRole(
		account.id,
		accountRole.id,
		userAccount.id
	);

	const ownedAccountRole =
		await apiHelpers.headlessAdminUser.postAccountAccountRoles(account.id, {
			name: getRandomString(),
			roleType: 'account',
		});

	return {account, ownedAccountRole, role, userAccount};
};

test(
	'Can create an owned account role',
	{tag: ['@LPD-47225']},
	async ({
		accountRolesPage,
		accountsPage,
		apiHelpers,
		editAccountRolePage,
	}) => {
		const roleName = getRandomString();

		const account = await apiHelpers.headlessAdminUser.postAccount();

		apiHelpers.data.push({id: account.id, type: 'account'});

		await accountsPage.goto();

		await accountsPage.accountNameLink(account.name).click();
		await accountsPage.accountRolesTab.click();
		await accountRolesPage.rolesTable.newButton.click();
		await editAccountRolePage.addRole({name: roleName});
		await editAccountRolePage.backButton.click();

		await expect(await accountRolesPage.roleName(roleName)).toBeVisible();
		await expect(async () => {
			await expect(
				await accountRolesPage.rolesTable.rowCheckbox(roleName)
			).toBeEnabled();
			await expect(
				(
					await accountRolesPage.rolesTable.row(1, roleName)
				).row.getByText('Owned')
			).toBeVisible();
		}).toPass({timeout: 5000});
	}
);

test(
	'An owned account role is not shared between accounts',
	{tag: ['@LPD-47225']},
	async ({
		accountRolesPage,
		accountsPage,
		apiHelpers,
		editAccountRolePage,
	}) => {
		const roleName = getRandomString();

		const account1 = await apiHelpers.headlessAdminUser.postAccount();

		apiHelpers.data.push({id: account1.id, type: 'account'});

		const account2 = await apiHelpers.headlessAdminUser.postAccount();

		apiHelpers.data.push({id: account2.id, type: 'account'});

		await accountsPage.goto();

		await accountsPage.accountNameLink(account1.name).click();
		await accountsPage.accountRolesTab.click();
		await accountRolesPage.rolesTable.newButton.click();
		await editAccountRolePage.addRole({name: roleName});
		await editAccountRolePage.backButton.click();

		await expect(
			await accountRolesPage.roleName('Account Administrator')
		).toBeVisible();
		await expect(await accountRolesPage.roleName(roleName)).toBeVisible();

		await accountsPage.goto();

		await accountsPage.accountNameLink(account2.name).click();
		await accountsPage.accountRolesTab.click();

		await expect(
			await accountRolesPage.roleName('Account Administrator')
		).toBeVisible();
		await expect(await accountRolesPage.roleName(roleName)).toHaveCount(0);
	}
);

test(
	'A shared account role is visible in all the accounts',
	{tag: ['@LPD-47225']},
	async ({accountRolesPage, accountsPage, apiHelpers}) => {
		const account1 = await apiHelpers.headlessAdminUser.postAccount();

		apiHelpers.data.push({id: account1.id, type: 'account'});

		const account2 = await apiHelpers.headlessAdminUser.postAccount();

		apiHelpers.data.push({id: account2.id, type: 'account'});

		const role = await apiHelpers.headlessAdminUser.postRole({
			name: getRandomString(),
			roleType: 'account',
		});

		await accountsPage.goto();

		await accountsPage.accountNameLink(account1.name).click();
		await accountsPage.accountRolesTab.click();

		await expect(
			await accountRolesPage.roleName('Account Administrator')
		).toBeVisible();
		await expect(await accountRolesPage.roleName(role.name)).toBeVisible();

		await accountsPage.goto();

		await accountsPage.accountNameLink(account2.name).click();
		await accountsPage.accountRolesTab.click();

		await expect(
			await accountRolesPage.roleName('Account Administrator')
		).toBeVisible();
		await expect(await accountRolesPage.roleName(role.name)).toBeVisible();
	}
);

test(
	'Can assign / unassing a shared and an owned role to an account user',
	{tag: ['@LPD-47225']},
	async ({
		accountRoleSelectorPage,
		accountRolesPage,
		accountUsersPage,
		accountsPage,
		apiHelpers,
		editAccountRolePage,
		page,
	}) => {
		const roles = ['Account Administrator', getRandomString()];

		const account = await apiHelpers.headlessAdminUser.postAccount();

		apiHelpers.data.push({id: account.id, type: 'account'});

		const user = await apiHelpers.headlessAdminUser.postUserAccount();

		await apiHelpers.headlessAdminUser.assignUserToAccountByEmailAddress(
			account.id,
			[user.emailAddress]
		);

		await accountsPage.goto();

		await accountsPage.accountNameLink(account.name).click();
		await accountsPage.accountRolesTab.click();
		await accountRolesPage.rolesTable.newButton.click();
		await editAccountRolePage.addRole({name: roles[1]});
		await editAccountRolePage.backButton.click();

		await accountsPage.usersTab.click();
		await (await accountUsersPage.usersTable.rowActions(user.name)).click();
		await accountUsersPage.assignRolesMenuItem.click();

		await accountRoleSelectorPage.selectRoles(roles);

		await expect(
			(await accountUsersPage.usersTable.firstRow()).getByText(roles[0])
		).toBeVisible();
		await expect(
			(await accountUsersPage.usersTable.firstRow()).getByText(roles[1])
		).toBeVisible();

		await page.reload();

		await expect(
			(await accountUsersPage.usersTable.firstRow()).getByText(roles[0])
		).toBeVisible();
		await expect(
			(await accountUsersPage.usersTable.firstRow()).getByText(roles[1])
		).toBeVisible();

		await (await accountUsersPage.usersTable.rowActions(user.name)).click();
		await accountUsersPage.assignRolesMenuItem.click();

		await accountRoleSelectorPage.selectRoles(roles, false);

		await expect(
			(await accountUsersPage.usersTable.firstRow()).getByText(roles[0])
		).toHaveCount(0);
		await expect(
			(await accountUsersPage.usersTable.firstRow()).getByText(roles[1])
		).toHaveCount(0);

		await page.reload();

		await expect(
			(await accountUsersPage.usersTable.firstRow()).getByText(roles[0])
		).toHaveCount(0);
		await expect(
			(await accountUsersPage.usersTable.firstRow()).getByText(roles[1])
		).toHaveCount(0);
	}
);

test(
	'Can assign / unassing a shared and an owned role to an account user from the role',
	{tag: ['@LPD-47225']},
	async ({
		accountRolesPage,
		accountUserSelectorPage,
		accountsPage,
		apiHelpers,
		editAccountRolePage,
		page,
	}) => {
		page.on('dialog', (dialog) => dialog.accept());

		const roles = ['Account Administrator', getRandomString()];

		const account = await apiHelpers.headlessAdminUser.postAccount();

		apiHelpers.data.push({id: account.id, type: 'account'});

		const user = await apiHelpers.headlessAdminUser.postUserAccount();

		await apiHelpers.headlessAdminUser.assignUserToAccountByEmailAddress(
			account.id,
			[user.emailAddress]
		);

		await accountsPage.goto();

		await accountsPage.accountNameLink(account.name).click();
		await accountsPage.accountRolesTab.click();
		await accountRolesPage.rolesTable.newButton.click();
		await editAccountRolePage.addRole({name: roles[1]});
		await editAccountRolePage.backButton.click();
		await accountsPage.accountRolesTab.click();

		for (const role of roles) {
			await expect(async () => {
				await expect(
					accountRolesPage.rolesTable.cell(role)
				).toBeVisible();

				await (
					await accountRolesPage.rolesTable.rowActions(role)
				).click();

				await expect(accountRolesPage.assignUsersButton).toBeVisible({
					timeout: 100,
				});
			}).toPass();

			await accountRolesPage.assignUsersButton.click();

			await expect(accountRolesPage.roleNameHeading(role)).toBeVisible();

			await accountRolesPage.assignUsersTable.newButton.click();

			await expect(
				accountUserSelectorPage.usersTable.searchInput
			).toBeEditable();

			await (
				await accountUserSelectorPage.usersTable.rowCheckbox(user.name)
			).check();

			await accountUserSelectorPage.assignButton.click();

			await waitForAlert(page);

			await expect(
				accountRolesPage.assignUsersTable.cell(user.name)
			).toBeVisible();

			await page.reload();

			await expect(
				accountRolesPage.assignUsersTable.cell(user.name)
			).toBeVisible();

			await (
				await accountRolesPage.assignUsersTable.rowCheckbox(user.name)
			).check();

			await accountRolesPage.removeButton.click();

			await expect(
				accountRolesPage.assignUsersTable.cell(user.name)
			).toHaveCount(0);

			await accountRolesPage.backButton.click();
		}
	}
);

test(
	'The account role is also visible on the user page',
	{tag: ['@LPD-47225']},
	async ({
		accountRoleSelectorPage,
		accountRolesPage,
		accountUsersPage,
		accountsPage,
		apiHelpers,
		editAccountRolePage,
		editUserPage,
		usersAndOrganizationsPage,
	}) => {
		const roles = ['Account Administrator', getRandomString()];

		const account = await apiHelpers.headlessAdminUser.postAccount();

		apiHelpers.data.push({id: account.id, type: 'account'});

		const user = await apiHelpers.headlessAdminUser.postUserAccount();

		await apiHelpers.headlessAdminUser.assignUserToAccountByEmailAddress(
			account.id,
			[user.emailAddress]
		);

		await accountsPage.goto();

		await accountsPage.accountNameLink(account.name).click();
		await accountsPage.accountRolesTab.click();
		await accountRolesPage.rolesTable.newButton.click();
		await editAccountRolePage.addRole({name: roles[1]});
		await editAccountRolePage.backButton.click();

		await accountsPage.usersTab.click();
		await (await accountUsersPage.usersTable.rowActions(user.name)).click();
		await accountUsersPage.assignRolesMenuItem.click();

		await accountRoleSelectorPage.selectRoles(roles);

		await usersAndOrganizationsPage.goToUsers();

		await (
			await usersAndOrganizationsPage.usersTableRowLink(
				user.alternateName
			)
		).click();

		await editUserPage.membershipsLink.click();

		await expect(
			(
				await editUserPage.membershipsAccountsTableRow(
					0,
					account.name,
					true
				)
			).row
		).toBeVisible();
		await expect(
			(
				await editUserPage.membershipsAccountsTableRow(
					0,
					account.name,
					true
				)
			).row
		).toContainText(roles[0]);
		await expect(
			(
				await editUserPage.membershipsAccountsTableRow(
					0,
					account.name,
					true
				)
			).row
		).toContainText(roles[1]);
	}
);

test(
	'Can search an account role',
	{tag: ['@LPD-47225']},
	async ({
		accountRolesPage,
		accountsPage,
		apiHelpers,
		editAccountRolePage,
	}) => {
		const roles = ['Account Administrator', getRandomString()];

		const account = await apiHelpers.headlessAdminUser.postAccount();

		apiHelpers.data.push({id: account.id, type: 'account'});

		await accountsPage.goto();

		await accountsPage.accountNameLink(account.name).click();
		await accountsPage.accountRolesTab.click();
		await accountRolesPage.rolesTable.newButton.click();
		await editAccountRolePage.addRole({name: roles[1]});
		await editAccountRolePage.backButton.click();

		await accountRolesPage.rolesTable.search(getRandomString());

		await expect(accountRolesPage.rolesTable.cell(roles[0])).toHaveCount(0);
		await expect(accountsPage.accountsTable.cell(roles[1])).toHaveCount(0);

		await accountsPage.accountsTable.search(roles[0]);

		await expect(accountsPage.accountsTable.cell(roles[0])).toBeVisible();
		await expect(accountsPage.accountsTable.cell(roles[1])).toHaveCount(0);

		await accountsPage.accountsTable.search(roles[1]);

		await expect(accountsPage.accountsTable.cell(roles[0])).toHaveCount(0);
		await expect(accountsPage.accountsTable.cell(roles[1])).toBeVisible();

		await accountsPage.accountsTable.search('');

		await expect(accountsPage.accountsTable.cell(roles[0])).toBeVisible();
		await expect(accountsPage.accountsTable.cell(roles[1])).toBeVisible();
		await expect(accountsPage.accountsTable.cell(roles[2])).toBeVisible();
	}
);

test(
	'Can search a user assigning account role',
	{tag: ['@LPD-47225']},
	async ({
		accountRolesPage,
		accountUserSelectorPage,
		accountsPage,
		apiHelpers,
	}) => {
		const roleName = 'Account Administrator';

		const account = await apiHelpers.headlessAdminUser.postAccount();

		apiHelpers.data.push({id: account.id, type: 'account'});

		const user1 = await apiHelpers.headlessAdminUser.postUserAccount();
		const user2 = await apiHelpers.headlessAdminUser.postUserAccount();
		const user3 = await apiHelpers.headlessAdminUser.postUserAccount();

		await apiHelpers.headlessAdminUser.assignUserToAccountByEmailAddress(
			account.id,
			[user1.emailAddress, user2.emailAddress]
		);

		await accountsPage.goto();

		await accountsPage.accountNameLink(account.name).click();
		await accountsPage.accountRolesTab.click();

		await expect(async () => {
			await expect(
				accountRolesPage.rolesTable.cell(roleName)
			).toBeVisible();
		}).toPass();

		await (await accountRolesPage.rolesTable.rowActions(roleName)).click();
		await accountRolesPage.assignUsersButton.click();

		await expect(accountRolesPage.roleNameHeading(roleName)).toBeVisible();

		await accountRolesPage.assignUsersTable.newButton.click();

		await expect(
			accountUserSelectorPage.usersTable.cell(user1.name)
		).toBeVisible();
		await expect(
			accountUserSelectorPage.usersTable.cell(user2.name)
		).toBeVisible();
		await expect(
			accountUserSelectorPage.usersTable.cell(user3.name)
		).toHaveCount(0);

		await accountUserSelectorPage.usersTable.search(getRandomString());

		await expect(
			accountUserSelectorPage.usersTable.cell(user1.name)
		).toHaveCount(0);
		await expect(
			accountUserSelectorPage.usersTable.cell(user2.name)
		).toHaveCount(0);
		await expect(
			accountUserSelectorPage.usersTable.cell(user3.name)
		).toHaveCount(0);

		await accountUserSelectorPage.usersTable.search(user1.name);

		await expect(
			accountUserSelectorPage.usersTable.cell(user1.name)
		).toBeVisible();
		await expect(
			accountUserSelectorPage.usersTable.cell(user2.name)
		).toHaveCount(0);
		await expect(
			accountUserSelectorPage.usersTable.cell(user3.name)
		).toHaveCount(0);

		await accountUserSelectorPage.usersTable.search(user2.name);

		await expect(
			accountUserSelectorPage.usersTable.cell(user1.name)
		).toHaveCount(0);
		await expect(
			accountUserSelectorPage.usersTable.cell(user2.name)
		).toBeVisible();
		await expect(
			accountUserSelectorPage.usersTable.cell(user3.name)
		).toHaveCount(0);

		await accountUserSelectorPage.usersTable.search(user3.name);

		await expect(
			accountUserSelectorPage.usersTable.cell(user1.name)
		).toHaveCount(0);
		await expect(
			accountUserSelectorPage.usersTable.cell(user2.name)
		).toHaveCount(0);
		await expect(
			accountUserSelectorPage.usersTable.cell(user3.name)
		).toHaveCount(0);

		await accountUserSelectorPage.usersTable.search('');

		await expect(
			accountUserSelectorPage.usersTable.cell(user1.name)
		).toBeVisible();
		await expect(
			accountUserSelectorPage.usersTable.cell(user2.name)
		).toBeVisible();
		await expect(
			accountUserSelectorPage.usersTable.cell(user3.name)
		).toHaveCount(0);
	}
);

test(
	'The default account roles are present in Roles Admin',
	{tag: ['@LPD-47225']},
	async ({rolesPage}) => {
		await rolesPage.goto();

		await rolesPage.accountRolesLink.click();
		await rolesPage.rolesTable.changeView('Table');

		await expect(
			rolesPage.rolesTable.cell('Account Administrator')
		).toBeVisible();
		await expect(
			await rolesPage.rolesTable.rowCheckbox('Account Administrator')
		).toBeDisabled();
		await expect(rolesPage.rolesTable.cell('Account Member')).toBeVisible();
		await expect(
			await rolesPage.rolesTable.rowCheckbox('Account Member')
		).toBeDisabled();
	}
);

test(
	'Group scope permissions can be defined for owned account roles',
	{tag: ['@LPD-47225']},
	async ({
		accountRolesPage,
		accountsPage,
		apiHelpers,
		editAccountRolePage,
		page,
		roleDefinePermissionsPage,
	}) => {
		page.on('dialog', (dialog) => dialog.accept());

		const roleName = getRandomString();

		const account = await apiHelpers.headlessAdminUser.postAccount();

		apiHelpers.data.push({id: account.id, type: 'account'});

		await accountsPage.goto();

		await accountsPage.accountNameLink(account.name).click();
		await accountsPage.accountRolesTab.click();
		await accountRolesPage.rolesTable.newButton.click();
		await editAccountRolePage.addRole({name: roleName});
		await editAccountRolePage.defineGroupScopePermissionsLink.click();
		await roleDefinePermissionsPage
			.menuItem('Site and Asset Library')
			.click();
		await roleDefinePermissionsPage.menuItem('Applications').click();
		await roleDefinePermissionsPage.menuItem('Account Management').click();
		await roleDefinePermissionsPage
			.permissionCheckbox('Add to Page')
			.check();
		await roleDefinePermissionsPage
			.permissionCheckbox('Configuration')
			.check();
		await roleDefinePermissionsPage.saveButton.click();

		await waitForAlert(page, 'The role permissions were updated');

		await expect(
			roleDefinePermissionsPage.resourceName(
				'Account Management: Add to Page'
			)
		).toBeVisible();
		await expect(
			roleDefinePermissionsPage.resourceName(
				'Account Management: Configuration'
			)
		).toBeVisible();

		await roleDefinePermissionsPage
			.resourceRemoveLink('Account Management: Add to Page')
			.click();

		await expect(
			roleDefinePermissionsPage.resourceName(
				'Account Management: Add to Page'
			)
		).toHaveCount(0);
		await expect(
			roleDefinePermissionsPage.resourceName(
				'Account Management: Configuration'
			)
		).toBeVisible();

		await page.reload();

		await expect(
			roleDefinePermissionsPage.resourceName(
				'Account Management: Add to Page'
			)
		).toHaveCount(0);
		await expect(
			roleDefinePermissionsPage.resourceName(
				'Account Management: Configuration'
			)
		).toBeVisible();
	}
);

test(
	'Group scope permissions can be defined for account roles from role pages',
	{tag: ['@LPD-47225']},
	async ({
		apiHelpers,
		editAccountRolePage,
		page,
		roleDefinePermissionsPage,
		rolesPage,
	}) => {
		page.on('dialog', (dialog) => dialog.accept());

		const role = await apiHelpers.headlessAdminUser.postRole({
			name: getRandomString(),
			roleType: 'account',
		});

		await rolesPage.goto();

		await rolesPage.accountRolesLink.click();
		await rolesPage.rolesTable.changeView('Table');

		await expect(rolesPage.rolesTable.cell(role.name)).toBeVisible();

		await (await rolesPage.rolesTable.cellLink(role.name)).click();
		await editAccountRolePage.defineGroupScopePermissionsLink.click();
		await roleDefinePermissionsPage
			.menuItem('Site and Asset Library')
			.click();
		await roleDefinePermissionsPage.menuItem('Applications').click();
		await roleDefinePermissionsPage.menuItem('Account Management').click();
		await roleDefinePermissionsPage
			.permissionCheckbox('Add to Page')
			.check();
		await roleDefinePermissionsPage
			.permissionCheckbox('Configuration')
			.check();
		await roleDefinePermissionsPage.saveButton.click();

		await waitForAlert(page, 'The role permissions were updated');

		await expect(
			roleDefinePermissionsPage.resourceName(
				'Account Management: Add to Page'
			)
		).toBeVisible();
		await expect(
			roleDefinePermissionsPage.resourceName(
				'Account Management: Configuration'
			)
		).toBeVisible();

		await roleDefinePermissionsPage
			.resourceRemoveLink('Account Management: Add to Page')
			.click();

		await expect(
			roleDefinePermissionsPage.resourceName(
				'Account Management: Add to Page'
			)
		).toHaveCount(0);
		await expect(
			roleDefinePermissionsPage.resourceName(
				'Account Management: Configuration'
			)
		).toBeVisible();

		await page.reload();

		await expect(
			roleDefinePermissionsPage.resourceName(
				'Account Management: Add to Page'
			)
		).toHaveCount(0);
		await expect(
			roleDefinePermissionsPage.resourceName(
				'Account Management: Configuration'
			)
		).toBeVisible();
	}
);

test(
	'Permissions can be defined for owned account roles',
	{tag: ['@LPD-47225']},
	async ({
		accountRolesPage,
		accountsPage,
		apiHelpers,
		editAccountRolePage,
		page,
		roleDefinePermissionsPage,
	}) => {
		page.on('dialog', (dialog) => dialog.accept());

		const roleName = getRandomString();

		const account = await apiHelpers.headlessAdminUser.postAccount();

		apiHelpers.data.push({id: account.id, type: 'account'});

		await accountsPage.goto();

		await accountsPage.accountNameLink(account.name).click();
		await accountsPage.accountRolesTab.click();
		await accountRolesPage.rolesTable.newButton.click();
		await editAccountRolePage.addRole({name: roleName});
		await editAccountRolePage.definePermissionsLink.click();
		await roleDefinePermissionsPage
			.menuItem('Site and Asset Library')
			.click();
		await roleDefinePermissionsPage.menuItem('Applications').click();
		await roleDefinePermissionsPage.menuItem('Account Management').click();
		await roleDefinePermissionsPage
			.permissionCheckbox('Add to Page')
			.check();
		await roleDefinePermissionsPage
			.permissionCheckbox('Configuration')
			.check();
		await roleDefinePermissionsPage.saveButton.click();

		await waitForAlert(page, 'The role permissions were updated');

		await expect(
			roleDefinePermissionsPage.resourceName(
				'Account Management: Add to Page'
			)
		).toBeVisible();
		await expect(
			roleDefinePermissionsPage.resourceName(
				'Account Management: Configuration'
			)
		).toBeVisible();

		await roleDefinePermissionsPage
			.resourceRemoveLink('Account Management: Add to Page')
			.click();

		await expect(
			roleDefinePermissionsPage.resourceName(
				'Account Management: Add to Page'
			)
		).toHaveCount(0);
		await expect(
			roleDefinePermissionsPage.resourceName(
				'Account Management: Configuration'
			)
		).toBeVisible();

		await page.reload();

		await expect(
			roleDefinePermissionsPage.resourceName(
				'Account Management: Add to Page'
			)
		).toHaveCount(0);
		await expect(
			roleDefinePermissionsPage.resourceName(
				'Account Management: Configuration'
			)
		).toBeVisible();
	}
);

test(
	'Permissions can be defined for account roles from role pages',
	{tag: ['@LPD-47225']},
	async ({
		apiHelpers,
		editAccountRolePage,
		page,
		roleDefinePermissionsPage,
		rolesPage,
	}) => {
		page.on('dialog', (dialog) => dialog.accept());

		const role = await apiHelpers.headlessAdminUser.postRole({
			name: getRandomString(),
			roleType: 'account',
		});

		await rolesPage.goto();

		await rolesPage.accountRolesLink.click();
		await rolesPage.rolesTable.changeView('Table');

		await expect(rolesPage.rolesTable.cell(role.name)).toBeVisible();

		await (await rolesPage.rolesTable.cellLink(role.name)).click();
		await editAccountRolePage.definePermissionsLink.click();
		await roleDefinePermissionsPage
			.menuItem('Site and Asset Library')
			.click();
		await roleDefinePermissionsPage.menuItem('Applications').click();
		await roleDefinePermissionsPage.menuItem('Account Management').click();
		await roleDefinePermissionsPage
			.permissionCheckbox('Add to Page')
			.check();
		await roleDefinePermissionsPage
			.permissionCheckbox('Configuration')
			.check();
		await roleDefinePermissionsPage.saveButton.click();

		await waitForAlert(page, 'The role permissions were updated');

		await expect(
			roleDefinePermissionsPage.resourceName(
				'Account Management: Add to Page'
			)
		).toBeVisible();
		await expect(
			roleDefinePermissionsPage.resourceName(
				'Account Management: Configuration'
			)
		).toBeVisible();

		await roleDefinePermissionsPage
			.resourceRemoveLink('Account Management: Add to Page')
			.click();

		await expect(
			roleDefinePermissionsPage.resourceName(
				'Account Management: Add to Page'
			)
		).toHaveCount(0);
		await expect(
			roleDefinePermissionsPage.resourceName(
				'Account Management: Configuration'
			)
		).toBeVisible();

		await page.reload();

		await expect(
			roleDefinePermissionsPage.resourceName(
				'Account Management: Add to Page'
			)
		).toHaveCount(0);
		await expect(
			roleDefinePermissionsPage.resourceName(
				'Account Management: Configuration'
			)
		).toBeVisible();
	}
);

test(
	'The account manager can only view account organizations by default and can not change organization associations',
	{tag: ['@LPD-49053', '@LPS-158344']},
	async ({accountOrganizationsPage, accountsPage, apiHelpers, page}) => {
		const {
			account,
			organization: organization1,
			userAccountManager,
		} = await initAccountManager(apiHelpers);

		const organization2 =
			await apiHelpers.headlessAdminUser.postOrganization();
		const organization3 =
			await apiHelpers.headlessAdminUser.postOrganization();

		await apiHelpers.headlessAdminUser.assignAccountToOrganization(
			account.id,
			organization2.id
		);

		await apiHelpers.headlessAdminUser.assignUserToOrganizationByEmailAddress(
			organization2.id,
			userAccountManager.emailAddress
		);

		await performLogout(page);
		await performLoginViaApi(page, userAccountManager.alternateName);

		await accountsPage.gotoAccountAdmin();

		await accountsPage.accountNameLink(account.name).click();
		await accountsPage.organizationsTab.click();

		await expect(
			accountOrganizationsPage.organizationsTable.cell(organization1.name)
		).toBeVisible();
		expect(
			await accountOrganizationsPage.organizationsTable.cellLink(
				organization1.name
			)
		).toBeNull();
		expect(
			await accountOrganizationsPage.organizationsTable.rowActions(
				organization1.name
			)
		).toBeNull();
		expect(
			await accountOrganizationsPage.organizationsTable.rowCheckbox(
				organization1.name
			)
		).toBeNull();
		await expect(
			accountOrganizationsPage.organizationsTable.cell(organization2.name)
		).toBeVisible();
		expect(
			await accountOrganizationsPage.organizationsTable.cellLink(
				organization2.name
			)
		).toBeNull();
		expect(
			await accountOrganizationsPage.organizationsTable.rowActions(
				organization2.name
			)
		).toBeNull();
		expect(
			await accountOrganizationsPage.organizationsTable.rowCheckbox(
				organization2.name
			)
		).toBeNull();
		await expect(
			accountOrganizationsPage.organizationsTable.cell(organization3.name)
		).toHaveCount(0);
		await expect(
			accountOrganizationsPage.organizationsTable.newButton
		).toHaveCount(0);
	}
);

test(
	'The account manager can view account users by default',
	{tag: ['@LPD-49053']},
	async ({accountUsersPage, accountsPage, apiHelpers, page}) => {
		const {account, userAccountManager} =
			await initAccountManager(apiHelpers);

		const userAccount1 =
			await apiHelpers.headlessAdminUser.postUserAccount();

		await apiHelpers.headlessAdminUser.assignUserToAccountByEmailAddress(
			account.id,
			[userAccount1.emailAddress]
		);

		const userAccount2 =
			await apiHelpers.headlessAdminUser.postUserAccount();

		await performLogout(page);
		await performLoginViaApi(page, userAccountManager.alternateName);

		await accountsPage.gotoAccountAdmin();

		await accountsPage.accountNameLink(account.name).click();
		await accountsPage.usersTab.click();

		await expect(
			accountUsersPage.usersTable.cell(userAccountManager.name)
		).toBeVisible();
		await expect(
			await accountUsersPage.usersTable.cellLink(userAccountManager.name)
		).not.toBeVisible();
		await expect(
			await accountUsersPage.usersTable.rowActions(
				userAccountManager.name
			)
		).toBeVisible();
		await expect(
			await accountUsersPage.usersTable.rowCheckbox(
				userAccountManager.name
			)
		).toBeVisible();
		await expect(
			accountUsersPage.usersTable.cell(userAccount1.name)
		).toBeVisible();
		await expect(
			await accountUsersPage.usersTable.cellLink(userAccount1.name)
		).not.toBeVisible();
		await expect(
			await accountUsersPage.usersTable.rowActions(userAccount1.name)
		).toBeVisible();
		await expect(
			await accountUsersPage.usersTable.rowCheckbox(userAccount1.name)
		).toBeVisible();
		await expect(
			accountUsersPage.usersTable.cell(userAccount2.name)
		).toHaveCount(0);
		await expect(accountUsersPage.usersTable.newButton).toBeVisible();
	}
);

test(
	'Account Manager cannot view or assign specific Account Roles without permissions',
	{tag: ['@LPD-49053', '@LPS-173628']},
	async ({
		accountRoleSelectorPage,
		accountUsersPage,
		accountsPage,
		apiHelpers,
		page,
	}) => {
		const companyId = await page.evaluate(() => {
			return Liferay.ThemeDisplay.getCompanyId();
		});

		const {
			account: account1,
			role: accountManagerRole,
			userAccountManager: userAccountManager1,
		} = await initAccountManager(apiHelpers);
		const {account: account2, userAccountManager: userAccountManager2} =
			await initAccountManager(apiHelpers);

		const regularRole = await apiHelpers.headlessAdminUser.postRole({
			name: getRandomString(),
			rolePermissions: [
				{
					actionIds: ['VIEW'],
					primaryKey: companyId,
					resourceName: 'com.liferay.account.model.AccountRole',
					scope: 1,
				},
			],
			roleType: 'regular',
		});

		await apiHelpers.headlessAdminUser.assignUserToRole(
			regularRole.externalReferenceCode,
			userAccountManager1.id
		);

		const accountRole = await apiHelpers.headlessAdminUser.postRole({
			name: getRandomString(),
			roleType: 'account',
		});

		try {
			await apiHelpers.jsonWebServicesResourcePermissionApiHelper.removeResourcePermission(
				'VIEW',
				companyId,
				'0',
				'com.liferay.account.model.AccountRole',
				'0',
				String(accountManagerRole.id),
				'3'
			);

			await performLogout(page);
			await performLoginViaApi(page, userAccountManager1.alternateName);

			await accountsPage.gotoAccountAdmin();

			await accountsPage.accountNameLink(account1.name).click();
			await accountsPage.usersTab.click();

			await expect(
				accountUsersPage.usersTable.cell(userAccountManager1.name)
			).toBeVisible();
			await (
				await accountUsersPage.usersTable.rowActions(
					userAccountManager1.name
				)
			).click();
			await accountUsersPage.assignRolesMenuItem.click();

			await expect(
				accountRoleSelectorPage.rolesTable.cell(accountRole.name)
			).toBeVisible();
			await expect(
				await accountRoleSelectorPage.rolesTable.rowCheckbox(
					accountRole.name
				)
			).toBeVisible();

			await performLogout(page);
			await performLoginViaApi(page, userAccountManager2.alternateName);

			await accountsPage.gotoAccountAdmin();

			await accountsPage.accountNameLink(account2.name).click();
			await accountsPage.usersTab.click();

			await expect(
				accountUsersPage.usersTable.cell(userAccountManager2.name)
			).toBeVisible();
			await (
				await accountUsersPage.usersTable.rowActions(
					userAccountManager2.name
				)
			).click();
			await accountUsersPage.assignRolesMenuItem.click();

			await expect(
				accountRoleSelectorPage.rolesTable.cell(accountRole.name)
			).toHaveCount(0);
		}
		finally {
			await performLogout(page);
			await performLoginViaApi(page, 'test');

			await apiHelpers.jsonWebServicesResourcePermissionApiHelper.addResourcePermission(
				'VIEW',
				companyId,
				'0',
				'com.liferay.account.model.AccountRole',
				'0',
				String(accountManagerRole.id),
				'3'
			);
		}
	}
);

test(
	'User with Add Account Role permissions for an account can add account roles',
	{tag: ['@LPD-49053', '@LPS-142654']},
	async ({
		accountRolesPage,
		accountsPage,
		apiHelpers,
		editAccountRolePage,
		page,
	}) => {
		const {
			account,
			role: role1,
			userAccount,
		} = await setupPermissionsTest(apiHelpers, page);

		await performLogout(page);
		await performLoginViaApi(page, userAccount.alternateName);

		await accountsPage.gotoAccountAdmin();
		await accountsPage.accountNameLink(account.name).click();
		await accountsPage.accountRolesTab.click();

		await expect(
			accountRolesPage.rolesTable.cell(role1.name)
		).toBeVisible();
		await expect(accountRolesPage.rolesTable.newButton).toHaveCount(0);

		await performLogout(page);
		await performLoginViaApi(page, 'test');

		const {accountRole: accountRole2, role: role2} = await addAccountRole(
			apiHelpers,
			account.id,
			[
				{
					actionIds: ['ADD_ACCOUNT_ROLE'],
					primaryKey: '0',
					resourceName: 'com.liferay.account.model.AccountEntry',
					scope: 3,
				},
			]
		);

		await apiHelpers.headlessAdminUser.assignUserToAccountRole(
			account.id,
			accountRole2.id,
			userAccount.id
		);

		await performLogout(page);
		await performLoginViaApi(page, userAccount.alternateName);

		await accountsPage.gotoAccountAdmin();
		await accountsPage.accountNameLink(account.name).click();
		await accountsPage.accountRolesTab.click();

		await expect(
			accountRolesPage.rolesTable.cell(role1.name)
		).toBeVisible();
		await expect(
			accountRolesPage.rolesTable.cell(role2.name)
		).toBeVisible();
		await expect(accountRolesPage.rolesTable.newButton).toBeVisible();

		const roleName = getRandomString();

		await accountRolesPage.rolesTable.newButton.click();
		await editAccountRolePage.addRole({name: roleName});
		await editAccountRolePage.backButton.click();

		await expect(await accountRolesPage.roleName(roleName)).toBeVisible();
		await expect(async () => {
			await expect(
				await accountRolesPage.rolesTable.rowCheckbox(roleName)
			).toBeEnabled();
			await expect(
				(
					await accountRolesPage.rolesTable.row(1, roleName)
				).row.getByText('Owned')
			).toBeVisible();
		}).toPass();
	}
);

test(
	'User with Assign Users permissions for an account can assign a user to an account role',
	{tag: ['@LPD-49053', '@LPS-142654']},
	async ({
		accountRolesPage,
		accountUserSelectorPage,
		accountsPage,
		apiHelpers,
		page,
	}) => {
		const {
			account,
			ownedAccountRole,
			userAccount: userAccount1,
		} = await setupPermissionsTest(apiHelpers, page, [
			'UPDATE',
			'VIEW_ACCOUNT_ROLES',
			'VIEW_USERS',
		]);

		const userAccount2 =
			await apiHelpers.headlessAdminUser.postUserAccount();

		await apiHelpers.headlessAdminUser.assignUserToAccountByEmailAddress(
			account.id,
			[userAccount2.emailAddress]
		);

		await performLogout(page);
		await performLoginViaApi(page, userAccount1.alternateName);

		await accountsPage.gotoAccountAdmin();
		await accountsPage.accountNameLink(account.name).click();
		await accountsPage.accountRolesTab.click();

		await expect(
			accountRolesPage.rolesTable.cell(ownedAccountRole.name)
		).toBeVisible();
		await expect(
			await accountRolesPage.rolesTable.rowActions(ownedAccountRole.name)
		).toHaveCount(0);

		await performLogout(page);
		await performLoginViaApi(page, 'test');

		const {accountRole: accountRole2} = await addAccountRole(
			apiHelpers,
			account.id,
			[
				{
					actionIds: ['ASSIGN_USERS'],
					primaryKey: '0',
					resourceName: 'com.liferay.account.model.AccountRole',
					scope: 3,
				},
			]
		);

		await apiHelpers.headlessAdminUser.assignUserToAccountRole(
			account.id,
			accountRole2.id,
			userAccount1.id
		);

		await performLogout(page);
		await performLoginViaApi(page, userAccount1.alternateName);

		await accountsPage.gotoAccountAdmin();
		await accountsPage.accountNameLink(account.name).click();
		await accountsPage.accountRolesTab.click();

		await expect(async () => {
			await (
				await accountRolesPage.rolesTable.rowActions(
					ownedAccountRole.name
				)
			).click();

			await expect(accountRolesPage.assignUsersButton).toBeVisible();
		}).toPass();

		await accountRolesPage.assignUsersButton.click();

		await expect(
			accountRolesPage.roleNameHeading(ownedAccountRole.name)
		).toBeVisible();

		await accountRolesPage.assignUsersTable.newButton.click();
		await accountUserSelectorPage.assignUsers([userAccount2.name]);

		await expect(
			accountRolesPage.assignUsersTable.cell(userAccount2.name)
		).toBeVisible();
	}
);

test(
	'User with Delete Account Role permissions for an account can delete account roles',
	{tag: ['@LPD-49053', '@LPS-142654']},
	async ({accountRolesPage, accountsPage, apiHelpers, page}) => {
		page.on('dialog', (dialog) => dialog.accept());

		const {
			account,
			ownedAccountRole,
			userAccount: userAccount1,
		} = await setupPermissionsTest(apiHelpers, page);

		await performLogout(page);
		await performLoginViaApi(page, userAccount1.alternateName);

		await accountsPage.gotoAccountAdmin();
		await accountsPage.accountNameLink(account.name).click();
		await accountsPage.accountRolesTab.click();

		await expect(
			accountRolesPage.rolesTable.cell(ownedAccountRole.name)
		).toBeVisible();
		await expect(
			await accountRolesPage.rolesTable.rowActions(ownedAccountRole.name)
		).toHaveCount(0);

		await performLogout(page);
		await performLoginViaApi(page, 'test');

		const {accountRole: accountRole2} = await addAccountRole(
			apiHelpers,
			account.id,
			[
				{
					actionIds: ['DELETE'],
					primaryKey: '0',
					resourceName: 'com.liferay.account.model.AccountRole',
					scope: 3,
				},
			]
		);

		await apiHelpers.headlessAdminUser.assignUserToAccountRole(
			account.id,
			accountRole2.id,
			userAccount1.id
		);

		await performLogout(page);
		await performLoginViaApi(page, userAccount1.alternateName);

		await accountsPage.gotoAccountAdmin();
		await accountsPage.accountNameLink(account.name).click();
		await accountsPage.accountRolesTab.click();

		await expect(async () => {
			await expect(
				accountRolesPage.rolesTable.searchInput
			).toBeEditable();

			await (
				await accountRolesPage.rolesTable.rowActions(
					ownedAccountRole.name
				)
			).click();

			await expect(accountRolesPage.deleteButton).toBeVisible();
		}).toPass();

		await accountRolesPage.deleteButton.click();

		await waitForAlert(page);

		await expect(
			accountRolesPage.rolesTable.cell(ownedAccountRole.name)
		).toHaveCount(0);
	}
);

test(
	'User with Update Account Role permissions for an account can update account roles',
	{tag: ['@LPD-49053', '@LPS-142654']},
	async ({
		accountRolesPage,
		accountsPage,
		apiHelpers,
		editAccountRolePage,
		page,
	}) => {
		const {
			account,
			ownedAccountRole,
			userAccount: userAccount1,
		} = await setupPermissionsTest(apiHelpers, page);

		await performLogout(page);
		await performLoginViaApi(page, userAccount1.alternateName);

		await accountsPage.gotoAccountAdmin();
		await accountsPage.accountNameLink(account.name).click();
		await accountsPage.accountRolesTab.click();

		await expect(
			accountRolesPage.rolesTable.cell(ownedAccountRole.name)
		).toBeVisible();
		await expect(
			await accountRolesPage.rolesTable.rowActions(ownedAccountRole.name)
		).toHaveCount(0);

		await performLogout(page);
		await performLoginViaApi(page, 'test');

		const {accountRole: accountRole2} = await addAccountRole(
			apiHelpers,
			account.id,
			[
				{
					actionIds: ['UPDATE'],
					primaryKey: '0',
					resourceName: 'com.liferay.account.model.AccountRole',
					scope: 3,
				},
			]
		);

		await apiHelpers.headlessAdminUser.assignUserToAccountRole(
			account.id,
			accountRole2.id,
			userAccount1.id
		);

		await performLogout(page);
		await performLoginViaApi(page, userAccount1.alternateName);

		await accountsPage.gotoAccountAdmin();
		await accountsPage.accountNameLink(account.name).click();
		await accountsPage.accountRolesTab.click();

		await expect(async () => {
			await expect(
				accountRolesPage.rolesTable.searchInput
			).toBeEditable();

			await (
				await accountRolesPage.rolesTable.rowActions(
					ownedAccountRole.name
				)
			).click();

			await expect(accountRolesPage.editButton).toBeVisible();
		}).toPass();

		await accountRolesPage.editButton.click();

		const roleName = getRandomString();

		await editAccountRolePage.nameInput.fill(roleName);
		await editAccountRolePage.saveButton.click();

		await waitForAlert(page);

		await editAccountRolePage.backButton.click();

		await expect(
			accountRolesPage.rolesTable.cell(ownedAccountRole.name)
		).toHaveCount(0);
		await expect(accountRolesPage.rolesTable.cell(roleName)).toBeVisible();
	}
);

test(
	'User with Define Permission can define owned account role permissions',
	{tag: ['@LPD-49053', '@LPS-142654']},
	async ({
		accountRolesPage,
		accountsPage,
		apiHelpers,
		page,
		roleDefinePermissionsPage,
	}) => {
		const {
			account,
			ownedAccountRole,
			userAccount: userAccount1,
		} = await setupPermissionsTest(apiHelpers, page);

		await performLogout(page);
		await performLoginViaApi(page, userAccount1.alternateName);

		await accountsPage.gotoAccountAdmin();
		await accountsPage.accountNameLink(account.name).click();
		await accountsPage.accountRolesTab.click();

		await expect(
			accountRolesPage.rolesTable.cell(ownedAccountRole.name)
		).toBeVisible();
		await expect(
			await accountRolesPage.rolesTable.rowActions(ownedAccountRole.name)
		).toHaveCount(0);

		await performLogout(page);
		await performLoginViaApi(page, 'test');

		const {accountRole: accountRole2} = await addAccountRole(
			apiHelpers,
			account.id,
			[
				{
					actionIds: ['DEFINE_PERMISSIONS'],
					primaryKey: '0',
					resourceName: 'com.liferay.account.model.AccountRole',
					scope: 3,
				},
			]
		);

		await apiHelpers.headlessAdminUser.assignUserToAccountRole(
			account.id,
			accountRole2.id,
			userAccount1.id
		);

		await performLogout(page);
		await performLoginViaApi(page, userAccount1.alternateName);

		await accountsPage.gotoAccountAdmin();
		await accountsPage.accountNameLink(account.name).click();
		await accountsPage.accountRolesTab.click();

		await expect(async () => {
			await expect(
				accountRolesPage.rolesTable.searchInput
			).toBeEditable();

			await (
				await accountRolesPage.rolesTable.rowActions(
					ownedAccountRole.name
				)
			).click();

			await expect(
				accountRolesPage.definePermissionsButton
			).toBeVisible();
		}).toPass();

		await accountRolesPage.definePermissionsButton.click();

		await expect(roleDefinePermissionsPage.noRoleMessage).toBeVisible();

		await roleDefinePermissionsPage.changePermission(
			'Account Users',
			'Assign Accounts',
			true
		);

		await expect(roleDefinePermissionsPage.noRoleMessage).not.toBeVisible();

		await roleDefinePermissionsPage.defineGroupScopePermissionsTab.click();

		await expect(roleDefinePermissionsPage.noRoleMessage).toBeVisible();

		await roleDefinePermissionsPage.changePermission(
			'Account Management',
			'Permissions',
			true
		);

		await expect(roleDefinePermissionsPage.noRoleMessage).not.toBeVisible();
	}
);

test(
	'Saving edits in one permissions tab in Account Roles does not clear related permissions from the other tab',
	{tag: ['@LPD-49053', '@LPS-151552']},
	async ({
		accountRolesPage,
		accountsPage,
		apiHelpers,
		roleDefinePermissionsPage,
	}) => {
		const account = await apiHelpers.headlessAdminUser.postAccount();

		apiHelpers.data.push({id: account.id, type: 'account'});

		const ownedAccountRole =
			await apiHelpers.headlessAdminUser.postAccountAccountRoles(
				account.id,
				{
					name: getRandomString(),
					roleType: 'account',
				}
			);

		await accountsPage.gotoAccountAdmin();
		await accountsPage.accountNameLink(account.name).click();
		await accountsPage.accountRolesTab.click();

		await expect(async () => {
			await expect(
				accountRolesPage.rolesTable.searchInput
			).toBeEditable();

			await (
				await accountRolesPage.rolesTable.rowActions(
					ownedAccountRole.name
				)
			).click();

			await expect(
				accountRolesPage.definePermissionsButton
			).toBeVisible();
		}).toPass();

		await accountRolesPage.definePermissionsButton.click();

		await expect(roleDefinePermissionsPage.noRoleMessage).toBeVisible();

		await roleDefinePermissionsPage.changePermission(
			'Account Users',
			'Assign Accounts',
			true
		);

		await expect(roleDefinePermissionsPage.noRoleMessage).not.toBeVisible();

		await roleDefinePermissionsPage.defineGroupScopePermissionsTab.click();

		await expect(roleDefinePermissionsPage.noRoleMessage).toBeVisible();

		await roleDefinePermissionsPage.changePermission(
			'Account Management',
			'Permissions',
			true
		);

		await expect(roleDefinePermissionsPage.noRoleMessage).not.toBeVisible();
		await expect(
			roleDefinePermissionsPage.resourceName(
				'Account Management: Permissions'
			)
		).toBeVisible();

		await roleDefinePermissionsPage.definePermissionsTab.click();

		await expect(roleDefinePermissionsPage.noRoleMessage).not.toBeVisible();
		await expect(
			roleDefinePermissionsPage.resourceName(
				'Account Users: Assign Accounts'
			)
		).toBeVisible();
	}
);

test(
	'A user with an Account Administrator role can view and manage the Channel Defaults of the account',
	{tag: ['@COMMERCE-12695', '@LPD-49053']},
	async ({
		accountsPage,
		apiHelpers,
		editAccountChannelDefaultsPage,
		editAccountPage,
		page,
	}) => {
		const account1 = await apiHelpers.headlessAdminUser.postAccount({
			type: 'business',
		});

		apiHelpers.data.push({id: account1.id, type: 'account'});

		const account2 = await apiHelpers.headlessAdminUser.postAccount({
			type: 'business',
		});

		apiHelpers.data.push({id: account2.id, type: 'account'});

		const userAccountManager =
			await apiHelpers.headlessAdminUser.postUserAccount();

		userData[userAccountManager.alternateName] = {
			name: userAccountManager.givenName,
			password: 'test',
			surname: userAccountManager.familyName,
		};

		await apiHelpers.headlessAdminUser.assignUserToAccountByEmailAddress(
			account1.id,
			[userAccountManager.emailAddress]
		);

		const regularRole = await apiHelpers.headlessAdminUser.postRole({
			name: getRandomString(),
			rolePermissions: [
				{
					actionIds: ['ACCESS_IN_CONTROL_PANEL'],
					primaryKey: await page.evaluate(() => {
						return Liferay.ThemeDisplay.getCompanyId();
					}),
					resourceName:
						'com_liferay_account_admin_web_internal_portlet_AccountEntriesAdminPortlet',
					scope: 1,
				},
			],
			roleType: 'regular',
		});

		await apiHelpers.headlessAdminUser.assignUserToRole(
			regularRole.externalReferenceCode,
			userAccountManager.id
		);

		await performLogout(page);
		await performLoginViaApi(page, userAccountManager.alternateName);

		await accountsPage.gotoAccountAdmin();

		await expect(
			accountsPage.accountsTable.cell(account1.name)
		).toBeVisible();
		await expect(
			await accountsPage.accountsTable.cellLink(account1.name)
		).toHaveCount(0);
		await expect(
			await accountsPage.accountsTable.rowActions(account1.name)
		).toHaveCount(0);
		await expect(
			accountsPage.accountsTable.cell(account2.name)
		).toHaveCount(0);

		await performLogout(page);
		await performLoginViaApi(page, 'test');

		const role = await (
			await apiHelpers.headlessAdminUser.getAccountRoles(account1.id)
		).items.find((item: TRole) => item.name === 'Account Administrator');

		await apiHelpers.headlessAdminUser.assignUserToAccountRole(
			account1.id,
			role.id,
			userAccountManager.id
		);

		await performLogout(page);
		await performLoginViaApi(page, userAccountManager.alternateName);

		await accountsPage.gotoAccountAdmin();

		await expect(
			accountsPage.accountsTable.cell(account1.name)
		).toBeVisible();
		await expect(
			await accountsPage.accountsTable.cellLink(account1.name)
		).toBeVisible();
		await expect(
			await accountsPage.accountsTable.rowActions(account1.name)
		).toBeVisible();
		await expect(
			accountsPage.accountsTable.cell(account2.name)
		).toHaveCount(0);

		await (
			await accountsPage.accountsTable.cellLink(account1.name)
		).click();

		await expect(editAccountPage.channelDefaultsLink).toBeVisible();

		await editAccountPage.channelDefaultsLink.click();

		await expect(
			editAccountChannelDefaultsPage.addDefaultPaymentTermButton
		).toBeVisible();
	}
);
