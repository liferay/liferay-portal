/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Page, expect, mergeTests} from '@playwright/test';

import {accountsPagesTest} from '../../../fixtures/accountsPagesTest';
import {apiHelpersTest} from '../../../fixtures/apiHelpersTest';
import {applicationsMenuPageTest} from '../../../fixtures/applicationsMenuPageTest';
import {dataApiHelpersTest} from '../../../fixtures/dataApiHelpersTest';
import {featureFlagsTest} from '../../../fixtures/featureFlagsTest';
import {loginTest} from '../../../fixtures/loginTest';
import {serverAdministrationPageTest} from '../../../fixtures/serverAdministrationPageTest';
import {usersAndOrganizationsPagesTest} from '../../../fixtures/usersAndOrganizationsPagesTest';
import {virtualInstancesPagesTest} from '../../../fixtures/virtualInstancesPagesTest';
import {AccountUserSelectorPage} from '../../../pages/account-admin-web/AccountUserSelectorPage';
import {AccountUsersPage} from '../../../pages/account-admin-web/AccountUsersPage';
import {AccountsPage} from '../../../pages/account-admin-web/AccountsPage';
import {EditAccountPage} from '../../../pages/account-admin-web/EditAccountPage';
import {EditUserPage} from '../../../pages/users-admin-web/EditUserPage';
import getRandomString from '../../../utils/getRandomString';
import {nextPage, setItemsPerPage} from '../../../utils/pagination';
import performLogin from '../../../utils/performLogin';
import {waitForAlert} from '../../../utils/waitForAlert';

export const test = mergeTests(
	accountsPagesTest,
	apiHelpersTest,
	applicationsMenuPageTest,
	dataApiHelpersTest,
	featureFlagsTest({
		'LPD-35914': {enabled: true},
	}),
	loginTest(),
	serverAdministrationPageTest,
	usersAndOrganizationsPagesTest,
	virtualInstancesPagesTest
);

test(
	'Can add and remove a user to an account',
	{tag: ['@LPD-47225', '@LPS-139430', '@LPS-149125']},
	async ({
		accountUserSelectorPage,
		accountUsersPage,
		accountsPage,
		apiHelpers,
		editUserPage,
		page,
		usersAndOrganizationsPage,
	}) => {
		page.on('dialog', (dialog) => dialog.accept());

		const account1 = await apiHelpers.headlessAdminUser.postAccount();
		const account2 = await apiHelpers.headlessAdminUser.postAccount();

		const user = await apiHelpers.headlessAdminUser.postUserAccount();

		await accountsPage.goto();

		await (
			await accountsPage.accountsTable.cellLink(account1.name)
		).click();

		await accountsPage.usersTab.click();
		await accountUsersPage.usersTable.newButton.click();
		await accountUsersPage.assignUserMenuItem.click();

		await accountUserSelectorPage.assignUsers([user.name]);

		await expect(accountUsersPage.usersTable.cell(user.name)).toBeVisible();

		await accountsPage.goto();

		await (
			await accountsPage.accountsTable.cellLink(account2.name)
		).click();

		await accountsPage.usersTab.click();
		await accountUsersPage.usersTable.newButton.click();
		await accountUsersPage.assignUserMenuItem.click();

		await accountUserSelectorPage.assignUsers([user.name]);

		await expect(accountUsersPage.usersTable.cell(user.name)).toBeVisible();

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
					account1.name,
					true
				)
			).row
		).toBeVisible();
		await expect(
			(
				await editUserPage.membershipsAccountsTableRow(
					0,
					account2.name,
					true
				)
			).row
		).toBeVisible();

		await accountsPage.goto();

		await (
			await accountsPage.accountsTable.cellLink(account1.name)
		).click();

		await accountsPage.usersTab.click();

		await (await accountUsersPage.usersTable.rowActions(user.name)).click();
		await accountUsersPage.removeButton.click();

		await expect(accountUsersPage.usersTable.cell(user.name)).toHaveCount(
			0
		);
	}
);

test(
	'Can add and remove users to an account in bulk',
	{tag: ['@LPD-47225', '@LPS-139430']},
	async ({
		accountUserSelectorPage,
		accountUsersPage,
		accountsPage,
		apiHelpers,
		page,
	}) => {
		page.on('dialog', (dialog) => dialog.accept());

		const account = await apiHelpers.headlessAdminUser.postAccount();

		const users = [];

		for (let i = 0; i < 5; i++) {
			users.push(await apiHelpers.headlessAdminUser.postUserAccount());
		}

		await accountsPage.goto();

		await (await accountsPage.accountsTable.cellLink(account.name)).click();

		await accountsPage.usersTab.click();
		await accountUsersPage.usersTable.newButton.click();
		await accountUsersPage.assignUserMenuItem.click();

		await accountUserSelectorPage.assignUsers(
			users.map((user) => user.name)
		);

		for (const user of users) {
			await expect(
				accountUsersPage.usersTable.cell(user.name)
			).toBeVisible();
		}

		for (const index of [1, 3]) {
			await (
				await accountUsersPage.usersTable.rowCheckbox(users[index].name)
			).check();
		}

		await accountUsersPage.removeButton.click();

		await waitForAlert(page);

		for (let i = 1; i < 5; i++) {
			if (i % 2 === 0) {
				await expect(
					accountUsersPage.usersTable.cell(users[i].name)
				).toBeVisible();
			}
			else {
				await expect(
					accountUsersPage.usersTable.cell(users[i].name)
				).toHaveCount(0);
			}
		}
	}
);

test(
	'Can search assigned users',
	{tag: ['@LPD-47225']},
	async ({
		accountUserSelectorPage,
		accountUsersPage,
		accountsPage,
		apiHelpers,
	}) => {
		const account = await apiHelpers.headlessAdminUser.postAccount();

		const user1 = await apiHelpers.headlessAdminUser.postUserAccount();
		const user2 = await apiHelpers.headlessAdminUser.postUserAccount();

		await accountsPage.goto();

		await (await accountsPage.accountsTable.cellLink(account.name)).click();

		await accountsPage.usersTab.click();
		await accountUsersPage.usersTable.newButton.click();
		await accountUsersPage.assignUserMenuItem.click();

		await accountUserSelectorPage.assignUsers([user1.name, user2.name]);

		await expect(
			accountUsersPage.usersTable.cell(user1.name)
		).toBeVisible();
		await expect(
			accountUsersPage.usersTable.cell(user2.name)
		).toBeVisible();

		await accountUsersPage.usersTable.search(getRandomString());

		await expect(accountUsersPage.usersTable.cell(user1.name)).toHaveCount(
			0
		);
		await expect(accountUsersPage.usersTable.cell(user2.name)).toHaveCount(
			0
		);

		await accountUsersPage.usersTable.search(user1.name);

		await expect(
			accountUsersPage.usersTable.cell(user1.name)
		).toBeVisible();
		await expect(accountUsersPage.usersTable.cell(user2.name)).toHaveCount(
			0
		);

		await accountUsersPage.usersTable.search(user2.name);

		await expect(accountUsersPage.usersTable.cell(user1.name)).toHaveCount(
			0
		);
		await expect(
			accountUsersPage.usersTable.cell(user2.name)
		).toBeVisible();

		await accountUsersPage.usersTable.search('');

		await expect(
			accountUsersPage.usersTable.cell(user1.name)
		).toBeVisible();
		await expect(
			accountUsersPage.usersTable.cell(user2.name)
		).toBeVisible();
	}
);

test(
	'Can search users during assignment',
	{tag: ['@LPD-47225']},
	async ({
		accountUserSelectorPage,
		accountUsersPage,
		accountsPage,
		apiHelpers,
	}) => {
		const account = await apiHelpers.headlessAdminUser.postAccount();

		const user1 = await apiHelpers.headlessAdminUser.postUserAccount();
		const user2 = await apiHelpers.headlessAdminUser.postUserAccount();

		await accountsPage.goto();

		await (await accountsPage.accountsTable.cellLink(account.name)).click();

		await accountsPage.usersTab.click();
		await accountUsersPage.usersTable.newButton.click();
		await accountUsersPage.assignUserMenuItem.click();

		await expect(
			accountUserSelectorPage.usersTable.cell(user1.name)
		).toBeVisible();
		await expect(
			accountUserSelectorPage.usersTable.cell(user2.name)
		).toBeVisible();

		await accountUserSelectorPage.usersTable.search(getRandomString());

		await expect(
			accountUserSelectorPage.usersTable.cell(user1.name)
		).toHaveCount(0);
		await expect(
			accountUserSelectorPage.usersTable.cell(user2.name)
		).toHaveCount(0);

		await accountUserSelectorPage.usersTable.search(user1.name);

		await expect(
			accountUserSelectorPage.usersTable.cell(user1.name)
		).toBeVisible();
		await expect(
			accountUserSelectorPage.usersTable.cell(user2.name)
		).toHaveCount(0);

		await accountUserSelectorPage.usersTable.search(user2.name);

		await expect(
			accountUserSelectorPage.usersTable.cell(user1.name)
		).toHaveCount(0);
		await expect(
			accountUserSelectorPage.usersTable.cell(user2.name)
		).toBeVisible();

		await accountUserSelectorPage.usersTable.search('');

		await expect(
			accountUserSelectorPage.usersTable.cell(user1.name)
		).toBeVisible();
		await expect(
			accountUserSelectorPage.usersTable.cell(user2.name)
		).toBeVisible();
	}
);

test(
	'Can paginate users during assignment',
	{tag: ['@LPD-47225']},
	async ({
		accountUserSelectorPage,
		accountUsersPage,
		accountsPage,
		apiHelpers,
	}) => {
		const account = await apiHelpers.headlessAdminUser.postAccount();

		const users = [];

		for (let i = 1; i <= 21; i++) {
			users.push(
				await apiHelpers.headlessAdminUser.postUserAccount({
					familyName: `A User ${String(i).padStart(2, '0')}`,
				})
			);
		}

		await accountsPage.goto();

		await (await accountsPage.accountsTable.cellLink(account.name)).click();

		await accountsPage.usersTab.click();
		await accountUsersPage.usersTable.newButton.click();
		await accountUsersPage.assignUserMenuItem.click();

		await setItemsPerPage(accountUserSelectorPage.frame, 20);

		await expect(
			accountUserSelectorPage.usersTable.cell(users[0].name)
		).toBeVisible();
		await expect(
			accountUserSelectorPage.usersTable.cell(
				users[users.length - 1].name
			)
		).toHaveCount(0);

		await nextPage(accountUserSelectorPage.frame);

		await expect(
			accountUserSelectorPage.usersTable.cell(users[0].name)
		).toHaveCount(0);
		await expect(
			accountUserSelectorPage.usersTable.cell(
				users[users.length - 1].name
			)
		).toBeVisible();
	}
);

test(
	'Can remove user from personal account',
	{tag: ['@LPD-47225']},
	async ({accountsPage, apiHelpers, editAccountPage, page}) => {
		const account = await apiHelpers.headlessAdminUser.postAccount({
			type: 'person',
		});

		const user = await apiHelpers.headlessAdminUser.postUserAccount();

		await apiHelpers.headlessAdminUser.assignUserToAccountByEmailAddress(
			account.id,
			[user.emailAddress]
		);

		await accountsPage.goto();

		await (await accountsPage.accountsTable.cellLink(account.name)).click();

		await expect(editAccountPage.assignUserMessage).not.toBeVisible();
		await expect(editAccountPage.personAccountUserContainer).toBeVisible();
		await expect(
			editAccountPage.personAccountUserName(user.name)
		).toBeVisible();
		await expect(accountsPage.usersTab).not.toBeVisible();

		await editAccountPage.personAccountUserRemoveButton.click();

		await expect(editAccountPage.assignUserMessage).toBeVisible();
		await expect(
			editAccountPage.personAccountUserName(user.name)
		).toHaveCount(0);

		await editAccountPage.saveButton.click();

		await waitForAlert(page);

		await editAccountPage.backButton.click();

		await (await accountsPage.accountsTable.cellLink(account.name)).click();

		await expect(editAccountPage.assignUserMessage).toBeVisible();
		await expect(
			editAccountPage.personAccountUserName(user.name)
		).toHaveCount(0);
	}
);

test(
	'Only one user can be assigned to a Person Account',
	{tag: ['@LPD-47225']},
	async ({
		accountPersonUserSelectorPage,
		accountsPage,
		apiHelpers,
		editAccountPage,
		editUserPage,
		page,
		usersAndOrganizationsPage,
	}) => {
		const account = await apiHelpers.headlessAdminUser.postAccount({
			type: 'person',
		});

		const user1 = await apiHelpers.headlessAdminUser.postUserAccount();
		const user2 = await apiHelpers.headlessAdminUser.postUserAccount();

		await apiHelpers.headlessAdminUser.assignUserToAccountByEmailAddress(
			account.id,
			[user1.emailAddress]
		);

		await accountsPage.goto();

		await (await accountsPage.accountsTable.cellLink(account.name)).click();

		await expect(editAccountPage.assignUserMessage).not.toBeVisible();
		await expect(editAccountPage.personAccountUserContainer).toBeVisible();
		await expect(
			editAccountPage.personAccountUserName(user1.name)
		).toBeVisible();
		await expect(
			editAccountPage.personAccountUserName(user2.name)
		).toHaveCount(0);

		await editAccountPage.personAccountUserSelectButton.click();

		await accountPersonUserSelectorPage.chooseUser(user2.name);

		await expect(editAccountPage.assignUserMessage).not.toBeVisible();
		await expect(editAccountPage.personAccountUserContainer).toBeVisible();
		await expect(
			editAccountPage.personAccountUserName(user1.name)
		).toHaveCount(0);
		await expect(
			editAccountPage.personAccountUserName(user2.name)
		).toBeVisible();

		await editAccountPage.saveButton.click();

		await waitForAlert(page);

		await usersAndOrganizationsPage.goToUsers();

		await (
			await usersAndOrganizationsPage.usersTableRowLink(
				user1.alternateName
			)
		).click();

		await editUserPage.membershipsLink.click();

		await expect(editUserPage.membershipsNoAccountsMessage).toBeVisible();

		await usersAndOrganizationsPage.goToUsers();

		await (
			await usersAndOrganizationsPage.usersTableRowLink(
				user2.alternateName
			)
		).click();

		await editUserPage.membershipsLink.click();

		await expect(
			editUserPage.membershipsNoAccountsMessage
		).not.toBeVisible();
	}
);

test(
	'Can search in person account user selector modal',
	{tag: ['@LPD-47225', '@LPS-117171']},
	async ({
		accountPersonUserSelectorPage,
		accountsPage,
		apiHelpers,
		editAccountPage,
	}) => {
		const account = await apiHelpers.headlessAdminUser.postAccount({
			type: 'person',
		});

		const user1 = await apiHelpers.headlessAdminUser.postUserAccount();
		const user2 = await apiHelpers.headlessAdminUser.postUserAccount();

		await accountsPage.goto();

		await (await accountsPage.accountsTable.cellLink(account.name)).click();

		await expect(editAccountPage.assignUserMessage).toBeVisible();

		await editAccountPage.personAccountUserSelectButton.click();

		await expect(
			accountPersonUserSelectorPage.usersTable.filterButton
		).toHaveCount(0);
		await expect(
			accountPersonUserSelectorPage.usersTable.orderButton
		).toBeVisible();

		await accountPersonUserSelectorPage.usersTable.search(
			getRandomString()
		);

		await expect(
			accountPersonUserSelectorPage.usersTable.cell(user1.name)
		).toHaveCount(0);
		await expect(
			accountPersonUserSelectorPage.usersTable.cell(user2.name)
		).toHaveCount(0);

		await accountPersonUserSelectorPage.usersTable.search(user1.name);

		await expect(
			accountPersonUserSelectorPage.usersTable.cell(user1.name)
		).toBeVisible();
		await expect(
			accountPersonUserSelectorPage.usersTable.cell(user2.name)
		).toHaveCount(0);

		await accountPersonUserSelectorPage.usersTable.search(user2.name);

		await expect(
			accountPersonUserSelectorPage.usersTable.cell(user1.name)
		).toHaveCount(0);
		await expect(
			accountPersonUserSelectorPage.usersTable.cell(user2.name)
		).toBeVisible();

		await accountPersonUserSelectorPage.usersTable.search('');

		await expect(
			accountPersonUserSelectorPage.usersTable.cell(user1.name)
		).toBeVisible();
		await expect(
			accountPersonUserSelectorPage.usersTable.cell(user2.name)
		).toBeVisible();
	}
);

test(
	'Can search, filter and sort in account user selector modal',
	{tag: ['@LPD-47225']},
	async ({
		accountUserSelectorPage,
		accountUsersPage,
		accountsPage,
		apiHelpers,
	}) => {
		const account1 = await apiHelpers.headlessAdminUser.postAccount();
		const account2 = await apiHelpers.headlessAdminUser.postAccount();

		const user1 = await apiHelpers.headlessAdminUser.postUserAccount({
			emailAddress: `A${getRandomString()}@test.com`,
			familyName: `Z${getRandomString()}`,
			givenName: `A${getRandomString()}`,
		});
		const user2 = await apiHelpers.headlessAdminUser.postUserAccount({
			emailAddress: `Z${getRandomString()}@test.com`,
			familyName: `A${getRandomString()}`,
			givenName: `Z${getRandomString()}`,
		});

		await apiHelpers.headlessAdminUser.assignUserToAccountByEmailAddress(
			account2.id,
			[user2.emailAddress]
		);

		await accountsPage.goto();

		await (
			await accountsPage.accountsTable.cellLink(account1.name)
		).click();

		await accountsPage.usersTab.click();
		await accountUsersPage.usersTable.newButton.click();
		await accountUsersPage.assignUserMenuItem.click();

		await accountUserSelectorPage.usersTable.search(getRandomString());

		await expect(
			accountUserSelectorPage.usersTable.cell(user1.name)
		).toHaveCount(0);
		await expect(
			accountUserSelectorPage.usersTable.cell(user2.name)
		).toHaveCount(0);

		await accountUserSelectorPage.usersTable.search(user1.name);

		await expect(
			accountUserSelectorPage.usersTable.cell(user1.name)
		).toBeVisible();
		await expect(
			accountUserSelectorPage.usersTable.cell(user2.name)
		).toHaveCount(0);

		await accountUserSelectorPage.usersTable.search(user2.name);

		await expect(
			accountUserSelectorPage.usersTable.cell(user1.name)
		).toHaveCount(0);
		await expect(
			accountUserSelectorPage.usersTable.cell(user2.name)
		).toBeVisible();

		await accountUserSelectorPage.usersTable.search('');

		await expect(
			accountUserSelectorPage.usersTable.cell(user1.name)
		).toBeVisible();
		await expect(
			accountUserSelectorPage.usersTable.cell(user2.name)
		).toBeVisible();

		await accountUserSelectorPage.usersTable.search('test.com');

		await expect(
			accountUserSelectorPage.usersTable.clearButton
		).toBeVisible();

		await expect(async () => {
			await accountUserSelectorPage.usersTable.orderButton.click();
			await accountUserSelectorPage.usersTable
				.orderMenuItem('First Name')
				.click({timeout: 1000});
		}).toPass();

		await expect(
			accountUserSelectorPage.usersTable.searchInput
		).toBeEditable();
		await expect(
			await accountUserSelectorPage.usersTable.firstRow()
		).toContainText(user1.name);
		await expect(
			await accountUserSelectorPage.usersTable.lastRow()
		).toContainText(user2.name);

		await expect(async () => {
			await accountUserSelectorPage.usersTable.orderButton.click();
			await accountUserSelectorPage.usersTable
				.orderMenuItem('Last Name')
				.click({timeout: 1000});
		}).toPass();

		await expect(
			accountUserSelectorPage.usersTable.searchInput
		).toBeEditable();
		await expect(
			await accountUserSelectorPage.usersTable.firstRow()
		).toContainText(user2.name);
		await expect(
			await accountUserSelectorPage.usersTable.lastRow()
		).toContainText(user1.name);

		await expect(async () => {
			await accountUserSelectorPage.usersTable.orderButton.click();
			await accountUserSelectorPage.usersTable
				.orderMenuItem('Email Address')
				.click({timeout: 1000});
		}).toPass();

		await expect(
			accountUserSelectorPage.usersTable.searchInput
		).toBeEditable();
		await expect(
			await accountUserSelectorPage.usersTable.firstRow()
		).toContainText(user1.name);
		await expect(
			await accountUserSelectorPage.usersTable.lastRow()
		).toContainText(user2.name);

		await expect(async () => {
			await accountUserSelectorPage.usersTable.orderButton.click();
			await accountUserSelectorPage.usersTable
				.orderMenuItem('Last Name')
				.click({timeout: 1000});
		}).toPass();

		await expect(
			accountUserSelectorPage.usersTable.searchInput
		).toBeEditable();

		await expect(async () => {
			await accountUserSelectorPage.usersTable.filterButton.click();
			await accountUserSelectorPage.usersTable
				.filterMenuItem('Account Users')
				.click({timeout: 1000});
		}).toPass();

		await expect(
			accountUserSelectorPage.usersTable.searchInput
		).toBeEditable();
		await expect(
			accountUserSelectorPage.usersTable.cell(user1.name)
		).toHaveCount(0);
		await expect(
			accountUserSelectorPage.usersTable.cell(user2.name)
		).toBeVisible();

		await expect(async () => {
			await accountUserSelectorPage.usersTable.filterButton.click();
			await accountUserSelectorPage.usersTable
				.filterMenuItem('No Assigned Account')
				.click({timeout: 1000});
		}).toPass();

		await expect(
			accountUserSelectorPage.usersTable.searchInput
		).toBeEditable();
		await expect(
			accountUserSelectorPage.usersTable.cell(user1.name)
		).toBeVisible();
		await expect(
			accountUserSelectorPage.usersTable.cell(user2.name)
		).toHaveCount(0);
	}
);

test(
	'A user with a blocked domain cannot be added to an account',
	{tag: ['@LPD-47225']},
	async ({
		accountUserSelectorPage,
		accountUsersPage,
		accountsPage,
		apiHelpers,
		editAccountPage,
		editUserPage,
		emailDomainsInstanceSettingsPage,
		page,
	}) => {
		const account = await apiHelpers.headlessAdminUser.postAccount();

		await emailDomainsInstanceSettingsPage.enableEmailDomainValidation(
			true,
			'yahoo.com,blocked.com'
		);

		try {
			await accountsPage.goto();

			await (
				await accountsPage.accountsTable.cellLink(account.name)
			).click();

			await editAccountPage.usersLink.click();
			await accountUsersPage.usersTable.newButton.click();
			await accountUsersPage.assignUserMenuItem.click();
			await accountUserSelectorPage.usersTable.newButton.click();

			const randomString = getRandomString();

			await editUserPage.emailAddressInput.fill(
				`${getRandomString()}@blocked.com`
			);
			await editUserPage.firstNameInput.fill(randomString);
			await editUserPage.lastNameInput.fill(randomString);
			await editUserPage.screenNameInput.fill(randomString);

			await expect(editUserPage.emailAddressError).toContainText(
				'is a blocked domain.'
			);

			await editUserPage.saveButton.click();

			await waitForAlert(page, 'Your request failed to complete', {
				type: 'danger',
			});

			await accountsPage.goto();

			await (
				await accountsPage.accountsTable.cellLink(account.name)
			).click();

			await editAccountPage.usersLink.click();

			await expect(
				accountUsersPage.usersTable.cell(randomString, false)
			).toHaveCount(0);
		}
		finally {
			await emailDomainsInstanceSettingsPage.enableEmailDomainValidation(
				false,
				''
			);
		}
	}
);

test(
	'A user with an invalid domain cannot be added to an account',
	{tag: ['@LPD-47225']},
	async ({
		accountUserSelectorPage,
		accountUsersPage,
		accountsPage,
		apiHelpers,
		editAccountPage,
		editUserPage,
		emailDomainsInstanceSettingsPage,
		page,
	}) => {
		const account = {
			domains: ['liferay.com', 'google.com', 'si-na.com', '9teen.com'],
			name: getRandomString(),
			type: 'business',
		};

		const {domains, name, type} = account;

		await emailDomainsInstanceSettingsPage.enableEmailDomainValidation();

		try {
			await accountsPage.goto();

			await accountsPage.accountsTable.newButton.click();

			await editAccountPage.createAccount(apiHelpers, {
				domains,
				name,
				type,
			});

			await editAccountPage.usersLink.click();
			await accountUsersPage.usersTable.newButton.click();
			await accountUsersPage.assignUserMenuItem.click();

			await expect(async () => {
				await accountUserSelectorPage.usersTable.newButton.click();

				await expect(editUserPage.emailAddressInput).toBeVisible();
			}).toPass();

			const randomString = getRandomString();

			await editUserPage.emailAddressInput.fill(
				`${getRandomString()}@invalid.com`
			);
			await editUserPage.firstNameInput.fill(randomString);
			await editUserPage.lastNameInput.fill(randomString);
			await editUserPage.screenNameInput.fill(randomString);

			await expect(editUserPage.emailAddressError).toContainText(
				'is not a valid domain for the following accounts'
			);

			await editUserPage.saveButton.click();

			await waitForAlert(page, 'Your request failed to complete', {
				type: 'danger',
			});

			await accountsPage.goto();

			await (
				await accountsPage.accountsTable.cellLink(account.name)
			).click();

			await editAccountPage.usersLink.click();

			await expect(
				accountUsersPage.usersTable.cell(randomString, false)
			).toHaveCount(0);
		}
		finally {
			await emailDomainsInstanceSettingsPage.enableEmailDomainValidation(
				false
			);
		}
	}
);

test(
	'A user with a valid domain can be added to an account without warnings',
	{tag: ['@LPD-47225']},
	async ({
		accountUserSelectorPage,
		accountUsersPage,
		accountsPage,
		apiHelpers,
		editAccountPage,
		editUserPage,
		emailDomainsInstanceSettingsPage,
		page,
	}) => {
		const account = {
			domains: ['liferay.com', 'google.com', 'si-na.com', '9teen.com'],
			name: getRandomString(),
			type: 'business',
		};

		const {domains, name, type} = account;

		await emailDomainsInstanceSettingsPage.enableEmailDomainValidation();

		try {
			await accountsPage.goto();

			await accountsPage.accountsTable.newButton.click();

			await editAccountPage.createAccount(apiHelpers, {
				domains,
				name,
				type,
			});

			await editAccountPage.usersLink.click();
			await accountUsersPage.usersTable.newButton.click();
			await accountUsersPage.assignUserMenuItem.click();

			const randomString = getRandomString();

			await expect(async () => {
				await accountUserSelectorPage.usersTable.newButton.click();

				await editUserPage.emailAddressInput.fill(
					`${randomString}@liferay.com`,
					{timeout: 500}
				);
			}).toPass();

			await editUserPage.firstNameInput.fill(randomString);
			await editUserPage.lastNameInput.fill(randomString);
			await editUserPage.screenNameInput.fill(randomString);

			await expect(editUserPage.emailAddressError).toHaveCount(0);

			await editUserPage.saveButton.click();

			await waitForAlert(page);

			await accountsPage.goto();

			await (
				await accountsPage.accountsTable.cellLink(account.name)
			).click();

			await editAccountPage.usersLink.click();

			await expect(
				accountUsersPage.usersTable.cell(randomString, false)
			).toBeVisible();
		}
		finally {
			await emailDomainsInstanceSettingsPage.enableEmailDomainValidation(
				false
			);
		}
	}
);

test(
	'Can filter valid domain users and all users when assigning',
	{tag: ['@LPD-47225']},
	async ({
		accountUserSelectorPage,
		accountUsersPage,
		accountsPage,
		apiHelpers,
		editAccountPage,
		emailDomainsInstanceSettingsPage,
		page,
	}) => {
		const user1 = await apiHelpers.headlessAdminUser.postUserAccount({
			emailAddress: `${getRandomString()}@liferay.com`,
		});
		const user2 = await apiHelpers.headlessAdminUser.postUserAccount({
			emailAddress: `${getRandomString()}@invalid.com`,
		});

		await emailDomainsInstanceSettingsPage.enableEmailDomainValidation();

		try {
			await accountsPage.goto();

			await expect(async () => {
				await accountsPage.accountsTable.newButton.click();

				await expect(editAccountPage.accountNameInput).toBeVisible();
			}).toPass();

			await editAccountPage.createAccount(apiHelpers, {
				name: getRandomString(),
			});

			await editAccountPage.usersLink.click();
			await accountUsersPage.usersTable.newButton.click();
			await accountUsersPage.assignUserMenuItem.click();

			await expect(
				accountUserSelectorPage.usersTable.searchInput
			).toBeEditable();

			await accountUserSelectorPage.usersTable.filterButton.click();
			await accountUserSelectorPage.usersTable
				.filterMenuItem('Valid Domain Users')
				.click();

			await page.waitForTimeout(100);

			expect(
				await accountUserSelectorPage.usersTable.rowCheckbox(user1.name)
			).toBeNull();
			expect(
				await accountUserSelectorPage.usersTable.rowCheckbox(user2.name)
			).toBeNull();

			await accountUserSelectorPage.usersTable.filterButton.click();
			await accountUserSelectorPage.usersTable
				.filterMenuItem('All Users')
				.click();

			await page.waitForTimeout(100);

			await expect(
				await accountUserSelectorPage.usersTable.rowCheckbox(user1.name)
			).toBeVisible();
			await expect(
				await accountUserSelectorPage.usersTable.rowCheckbox(user1.name)
			).toBeEnabled();
			await expect(
				await accountUserSelectorPage.usersTable.rowCheckbox(user2.name)
			).toBeVisible();
			await expect(
				await accountUserSelectorPage.usersTable.rowCheckbox(user2.name)
			).toBeEnabled();

			await accountUserSelectorPage.usersTable.selectAllItemsCheckbox.check();
			await accountUserSelectorPage.assignButton.click();

			await waitForAlert(page);

			await expect(
				accountUsersPage.usersTable.cell(user1.name)
			).toBeVisible();
			await expect(
				accountUsersPage.usersTable.cell(user2.name)
			).toBeVisible();

			await accountsPage.goto();

			await accountsPage.accountsTable.newButton.click();

			await editAccountPage.createAccount(apiHelpers, {
				domains: [
					'liferay.com',
					'google.com',
					'si-na.com',
					'9teen.com',
				],
				name: getRandomString(),
			});

			await editAccountPage.usersLink.click();
			await accountUsersPage.usersTable.newButton.click();
			await accountUsersPage.assignUserMenuItem.click();

			await expect(
				accountUserSelectorPage.usersTable.searchInput
			).toBeEditable();

			await accountUserSelectorPage.usersTable.filterButton.click();
			await accountUserSelectorPage.usersTable
				.filterMenuItem('Valid Domain Users')
				.click();

			await page.waitForTimeout(100);

			await expect(
				await accountUserSelectorPage.usersTable.rowCheckbox(user1.name)
			).toBeVisible();
			await expect(
				await accountUserSelectorPage.usersTable.rowCheckbox(user1.name)
			).toBeEnabled();
			expect(
				await accountUserSelectorPage.usersTable.rowCheckbox(user2.name)
			).toBeNull();

			await accountUserSelectorPage.usersTable.filterButton.click();
			await accountUserSelectorPage.usersTable
				.filterMenuItem('All Users')
				.click();

			await page.waitForTimeout(100);

			await expect(
				await accountUserSelectorPage.usersTable.rowCheckbox(user1.name)
			).toBeVisible();
			await expect(
				await accountUserSelectorPage.usersTable.rowCheckbox(user1.name)
			).toBeEnabled();
			await expect(
				await accountUserSelectorPage.usersTable.rowCheckbox(user2.name)
			).toBeVisible();
			await expect(
				await accountUserSelectorPage.usersTable.rowCheckbox(user2.name)
			).toBeDisabled();

			await accountUserSelectorPage.usersTable.selectAllItemsCheckbox.check();
			await accountUserSelectorPage.assignButton.click();

			await waitForAlert(page);

			await expect(
				accountUsersPage.usersTable.cell(user1.name)
			).toBeVisible();
			await expect(
				accountUsersPage.usersTable.cell(user2.name)
			).toHaveCount(0);

			await accountUsersPage.usersTable.newButton.click();
			await accountUsersPage.assignUserMenuItem.click();

			await expect(
				accountUserSelectorPage.usersTable.searchInput
			).toBeEditable();

			await accountUserSelectorPage.usersTable.filterButton.click();
			await accountUserSelectorPage.usersTable
				.filterMenuItem('Valid Domain Users')
				.click();
		}
		finally {
			await emailDomainsInstanceSettingsPage.enableEmailDomainValidation(
				false
			);
		}
	}
);

test(
	'Cannot invite a user with a different domain',
	{tag: ['@LPD-47225']},
	async ({
		accountUserInvitePage,
		accountUsersPage,
		accountsPage,
		apiHelpers,
		editAccountPage,
		emailDomainsInstanceSettingsPage,
		page,
	}) => {
		await emailDomainsInstanceSettingsPage.enableEmailDomainValidation();

		try {
			await accountsPage.goto();

			await accountsPage.accountsTable.newButton.click();

			await editAccountPage.createAccount(apiHelpers, {
				domains: ['liferay.com'],
				name: getRandomString(),
			});

			await editAccountPage.usersLink.click();
			await accountUsersPage.usersTable.newButton.click();
			await accountUsersPage.inviteUserMenuItem.click();

			await accountUserInvitePage
				.emailAddressInput(accountUserInvitePage.firstEntry)
				.fill(`${getRandomString()}@invalid.com`);
			await accountUserInvitePage
				.emailAddressInput(accountUserInvitePage.firstEntry)
				.press('Enter');

			await expect(
				accountUserInvitePage.formError(
					accountUserInvitePage.firstEntry,
					'has an invalid email domain.'
				)
			).toBeVisible();

			await accountUserInvitePage.inviteButton.click();

			await expect(
				accountUserInvitePage.formError(
					accountUserInvitePage.firstEntry,
					'has an invalid email domain.'
				)
			).toBeVisible();

			await accountUserInvitePage
				.clearAllButton(accountUserInvitePage.firstEntry)
				.click();
			await accountUserInvitePage
				.emailAddressInput(accountUserInvitePage.firstEntry)
				.fill(`${getRandomString()}@liferay.com`);
			await accountUserInvitePage
				.emailAddressInput(accountUserInvitePage.firstEntry)
				.press('Enter');

			await expect(
				accountUserInvitePage.formError(
					accountUserInvitePage.firstEntry,
					'has an invalid email domain.'
				)
			).toHaveCount(0);

			await accountUserInvitePage.inviteButton.click();

			await waitForAlert(page);
		}
		finally {
			await emailDomainsInstanceSettingsPage.enableEmailDomainValidation(
				false
			);
		}
	}
);

test(
	'The user is able to add and remove entries when inviting users to an account',
	{tag: ['@LPD-47225', '@LPS-189434']},
	async ({
		accountUserInvitePage,
		accountUsersPage,
		accountsPage,
		apiHelpers,
		editAccountPage,
		emailDomainsInstanceSettingsPage,
		page,
	}) => {
		page.on('dialog', (dialog) => dialog.accept());

		await emailDomainsInstanceSettingsPage.enableEmailDomainValidation();

		try {
			await accountsPage.goto();

			await accountsPage.accountsTable.newButton.click();

			await editAccountPage.createAccount(apiHelpers, {
				domains: ['liferay.com'],
				name: getRandomString(),
			});

			await editAccountPage.usersLink.click();
			await accountUsersPage.usersTable.newButton.click();
			await accountUsersPage.inviteUserMenuItem.click();

			await expect(accountUserInvitePage.entries).toHaveCount(1);

			await accountUserInvitePage
				.emailAddressInput(accountUserInvitePage.firstEntry)
				.fill(`${getRandomString()}@liferay.com`);
			await accountUserInvitePage
				.emailAddressInput(accountUserInvitePage.firstEntry)
				.press('Enter');

			await accountUserInvitePage.addEntryButton.click();

			await expect(accountUserInvitePage.entries).toHaveCount(2);

			await accountUserInvitePage
				.emailAddressInput(accountUserInvitePage.lastEntry)
				.fill(`${getRandomString()}@invalid.com`);
			await accountUserInvitePage
				.emailAddressInput(accountUserInvitePage.lastEntry)
				.press('Enter');

			await expect(
				accountUserInvitePage.formError(
					accountUserInvitePage.lastEntry,
					'has an invalid email domain.'
				)
			).toBeVisible();

			await accountUserInvitePage.inviteButton.click();

			await expect(
				accountUserInvitePage.formError(
					accountUserInvitePage.lastEntry,
					'has an invalid email domain.'
				)
			).toBeVisible();

			await accountUserInvitePage
				.removeEntryButton(accountUserInvitePage.lastEntry)
				.click();

			await expect(accountUserInvitePage.entries).toHaveCount(1);

			await accountUserInvitePage.inviteButton.click();

			await waitForAlert(page);
		}
		finally {
			await emailDomainsInstanceSettingsPage.enableEmailDomainValidation(
				false
			);
		}
	}
);

test(
	'Removes domain set to an account',
	{tag: ['@LPD-47225']},
	async ({
		accountUserSelectorPage,
		accountUsersPage,
		accountsPage,
		apiHelpers,
		editAccountPage,
		editUserPage,
		emailDomainsInstanceSettingsPage,
		page,
	}) => {
		const account = {
			domains: ['liferay.com', 'google.com', 'si-na.com', '9teen.com'],
			name: getRandomString(),
			type: 'business',
		};

		await emailDomainsInstanceSettingsPage.enableEmailDomainValidation();

		try {
			await accountsPage.goto();

			await accountsPage.accountsTable.newButton.click();

			await editAccountPage.createAccount(apiHelpers, account);

			await editAccountPage.usersLink.click();
			await accountUsersPage.usersTable.newButton.click();
			await accountUsersPage.assignUserMenuItem.click();
			await accountUserSelectorPage.usersTable.newButton.click();

			const randomString1 = getRandomString();

			await editUserPage.emailAddressInput.fill(
				`${getRandomString()}@liferay.com`
			);
			await editUserPage.firstNameInput.fill(randomString1);
			await editUserPage.lastNameInput.fill(randomString1);
			await editUserPage.screenNameInput.fill(randomString1);

			await editUserPage.saveButton.click();

			await waitForAlert(page);

			await expect(
				accountUsersPage.usersTable.cell(randomString1, false)
			).toHaveCount(1);

			await emailDomainsInstanceSettingsPage.enableEmailDomainValidation(
				false
			);

			await accountsPage.goto();

			await (
				await accountsPage.accountsTable.cellLink(account.name)
			).click();

			await editAccountPage.usersLink.click();
			await accountUsersPage.usersTable.newButton.click();
			await accountUsersPage.assignUserMenuItem.click();
			await accountUserSelectorPage.usersTable.newButton.click();

			const randomString2 = getRandomString();

			await editUserPage.emailAddressInput.fill(
				`${getRandomString()}@invalid.com`
			);
			await editUserPage.firstNameInput.fill(randomString2);
			await editUserPage.lastNameInput.fill(randomString2);
			await editUserPage.screenNameInput.fill(randomString2);

			await editUserPage.saveButton.click();

			await waitForAlert(page);

			await expect(
				accountUsersPage.usersTable.cell(randomString1, false)
			).toHaveCount(1);
			await expect(
				accountUsersPage.usersTable.cell(randomString2, false)
			).toHaveCount(1);
		}
		finally {
			await emailDomainsInstanceSettingsPage.enableEmailDomainValidation(
				false
			);
		}
	}
);

test(
	'An account can be removed from a user through the user page',
	{tag: ['@LPD-47225', '@LPS-139430']},
	async ({apiHelpers, editUserPage, usersAndOrganizationsPage}) => {
		const account = await apiHelpers.headlessAdminUser.postAccount({
			type: 'business',
		});

		const user = await apiHelpers.headlessAdminUser.postUserAccount();

		await apiHelpers.headlessAdminUser.assignUserToAccountByEmailAddress(
			account.id,
			[user.emailAddress]
		);

		await usersAndOrganizationsPage.goToUsers();

		await (
			await usersAndOrganizationsPage.usersTableRowLink(
				user.alternateName
			)
		).click();
		await editUserPage.membershipsLink.click();

		await expect(async () => {
			await expect(editUserPage.selectAccountsButton).toBeVisible();
			await expect(
				editUserPage.membershipsNoAccountsMessage
			).not.toBeVisible();
			await expect(
				(
					await editUserPage.membershipsAccountsTableRow(
						0,
						account.name,
						true
					)
				).row
			).toBeVisible();
		}).toPass();

		await editUserPage
			.membershipsAccountsRemoveButton(account.name)
			.click();

		await expect(editUserPage.membershipsNoAccountsMessage).toBeVisible();
	}
);

test(
	'Can view account users from manage users link',
	{tag: ['@LPD-47225']},
	async ({
		accountUserSelectorPage,
		accountUsersPage,
		accountsPage,
		apiHelpers,
		editUserPage,
		page,
	}) => {
		const account = await apiHelpers.headlessAdminUser.postAccount({
			type: 'business',
		});

		const user = await apiHelpers.headlessAdminUser.postUserAccount();

		await apiHelpers.headlessAdminUser.assignUserToAccountByEmailAddress(
			account.id,
			[user.emailAddress]
		);

		await accountsPage.goto();

		await (
			await accountsPage.accountsTable.rowActions(account.name)
		).click();
		await accountsPage.manageUsersButton.click();

		await expect(accountUsersPage.usersTable.cell(user.name)).toBeVisible();

		await accountUsersPage.usersTable.newButton.click();
		await accountUsersPage.assignUserMenuItem.click();
		await accountUserSelectorPage.usersTable.newButton.click();

		const randomString = getRandomString();

		await editUserPage.emailAddressInput.fill(
			`${getRandomString()}@liferay.com`
		);
		await editUserPage.firstNameInput.fill(randomString);
		await editUserPage.lastNameInput.fill(randomString);
		await editUserPage.screenNameInput.fill(randomString);

		await editUserPage.saveButton.click();

		await waitForAlert(page);

		await expect(accountUsersPage.usersTable.cell(user.name)).toBeVisible();
		await expect(
			accountUsersPage.usersTable.cell(randomString, false)
		).toBeVisible();
	}
);

test(
	'Blocked domain is scoped to a virtual instance',
	{tag: ['@LPD-47225']},
	async ({
		accountUserSelectorPage,
		accountUsersPage,
		accountsPage,
		apiHelpers,
		browser,
		editAccountPage,
		editUserPage,
		emailDomainsInstanceSettingsPage,
		page,
		virtualInstancesPage,
	}) => {
		test.setTimeout(160000);

		const DEFAULT_VIRTUAL_INSTANCE_NAME = 'www.able.com';

		await emailDomainsInstanceSettingsPage.enableEmailDomainValidation(
			true,
			'yahoo.com,blocked.com'
		);

		let newPage: Page;

		try {
			await accountsPage.goto(false);

			await expect(async () => {
				await accountsPage.accountsTable.newButton.click();

				await expect(editAccountPage.accountNameInput).toBeVisible();
			}).toPass();

			await editAccountPage.createAccount(apiHelpers, {
				name: getRandomString(),
			});

			await editAccountPage.usersLink.click();
			await accountUsersPage.usersTable.newButton.click();
			await accountUsersPage.assignUserMenuItem.click();

			await expect(async () => {
				await accountUserSelectorPage.usersTable.newButton.click();

				await expect(editUserPage.emailAddressInput).toBeVisible();
			}).toPass();

			const randomString = getRandomString();

			await editUserPage.emailAddressInput.fill(
				`${getRandomString()}@blocked.com`
			);
			await editUserPage.firstNameInput.fill(randomString);
			await editUserPage.lastNameInput.fill(randomString);
			await editUserPage.screenNameInput.fill(randomString);

			await expect(editUserPage.emailAddressError).toContainText(
				'is a blocked domain.'
			);

			await editUserPage.saveButton.click();

			await waitForAlert(page, 'Your request failed to complete', {
				type: 'danger',
			});

			await virtualInstancesPage.addNewVirtualInstance(
				DEFAULT_VIRTUAL_INSTANCE_NAME
			);

			newPage = await browser.newPage({
				baseURL: `http://${DEFAULT_VIRTUAL_INSTANCE_NAME}:8080`,
			});

			accountUserSelectorPage = new AccountUserSelectorPage(newPage);
			accountUsersPage = new AccountUsersPage(newPage);
			accountsPage = new AccountsPage(newPage);
			editAccountPage = new EditAccountPage(newPage);
			editUserPage = new EditUserPage(newPage);

			await performLogin(
				newPage,
				'test',
				'',
				`@${DEFAULT_VIRTUAL_INSTANCE_NAME}.com`
			);

			await accountsPage.goto(false);

			await expect(async () => {
				await accountsPage.accountsTable.newButton.click();

				await expect(editAccountPage.accountNameInput).toBeVisible();
			}).toPass();

			await editAccountPage.createAccount(apiHelpers, {
				name: getRandomString(),
			});

			await editAccountPage.usersLink.click();
			await accountUsersPage.usersTable.newButton.click();
			await accountUsersPage.assignUserMenuItem.click();

			await expect(async () => {
				await accountUserSelectorPage.usersTable.newButton.click();

				await expect(editUserPage.emailAddressInput).toBeVisible();
			}).toPass();

			await editUserPage.emailAddressInput.fill(
				`${getRandomString()}@blocked.com`
			);
			await editUserPage.firstNameInput.fill(randomString);
			await editUserPage.lastNameInput.fill(randomString);
			await editUserPage.screenNameInput.fill(randomString);

			await expect(editUserPage.emailAddressError).toHaveCount(0);

			await editUserPage.saveButton.click();

			await waitForAlert(newPage);
		}
		finally {
			if (newPage) {
				await newPage.close();
			}

			await virtualInstancesPage.deleteVirtualInstance(
				DEFAULT_VIRTUAL_INSTANCE_NAME
			);
			await emailDomainsInstanceSettingsPage.enableEmailDomainValidation(
				false,
				''
			);
		}
	}
);

test(
	'Can add / edit an account user via Account Users portlet',
	{tag: ['@LPD-48750']},
	async ({
		accountUsersAccountSelectorPage,
		accountUsersPage,
		apiHelpers,
		editUserPage,
		page,
	}) => {
		const account = await apiHelpers.headlessAdminUser.postAccount({
			type: 'business',
		});

		await accountUsersPage.goto();

		await accountUsersPage.usersTable.newButton.click();

		await expect(
			accountUsersAccountSelectorPage.accountsTable.searchInput
		).toBeEditable();
		await expect(
			accountUsersAccountSelectorPage.accountsTable.cell(account.name)
		).toBeVisible();

		await accountUsersAccountSelectorPage
			.chooseButton(account.name)
			.click();

		const randomString1 = getRandomString();

		await editUserPage.emailAddressInput.fill(
			`${randomString1}@liferay.com`
		);
		await editUserPage.firstNameInput.fill(randomString1);
		await editUserPage.lastNameInput.fill(randomString1);
		await editUserPage.screenNameInput.fill(randomString1);
		await editUserPage.saveButton.click();

		await waitForAlert(page);

		await expect(
			accountUsersPage.usersTable.cell(`${randomString1}@liferay.com`)
		).toBeVisible();
		await expect(
			(
				await accountUsersPage.usersTable.row(
					2,
					`${randomString1}@liferay.com`
				)
			).row
		).toContainText(account.name);

		await (
			await accountUsersPage.usersTable.cellLink(
				`${randomString1}@liferay.com`,
				2
			)
		).click();

		await expect(editUserPage.emailAddressInput).toHaveValue(
			`${randomString1}@liferay.com`
		);
		await expect(editUserPage.firstNameInput).toHaveValue(randomString1);
		await expect(editUserPage.lastNameInput).toHaveValue(randomString1);
		await expect(editUserPage.screenNameInput).toHaveValue(randomString1);

		apiHelpers.data.push({
			id: await editUserPage.userIDInput.inputValue(),
			type: 'userAccount',
		});

		const randomString2 = getRandomString();

		await editUserPage.firstNameInput.fill(randomString2);
		await editUserPage.lastNameInput.fill(randomString2);
		await editUserPage.saveButton.click();

		await waitForAlert(page);

		await editUserPage.cancelButton.click();

		await expect(
			accountUsersPage.usersTable.cell(
				`${randomString2} ${randomString2}`
			)
		).toBeVisible();
		await expect(
			(
				await accountUsersPage.usersTable.row(
					2,
					`${randomString1}@liferay.com`
				)
			).row
		).toContainText(account.name);

		await (
			await accountUsersPage.usersTable.cellLink(
				`${randomString1}@liferay.com`,
				2
			)
		).click();

		await expect(editUserPage.firstNameInput).toHaveValue(randomString2);
		await expect(editUserPage.lastNameInput).toHaveValue(randomString2);
	}
);

test(
	'Can paginate account users',
	{tag: ['@LPD-48750']},
	async ({accountUsersPage, apiHelpers, page}) => {
		test.setTimeout(150000);

		const account = await apiHelpers.headlessAdminUser.postAccount({
			type: 'business',
		});

		const users: Array<TUserAccount> = [];

		for (let i = 1; i <= 21; i++) {
			const user = await apiHelpers.headlessAdminUser.postUserAccount({
				familyName: `${String(i).padStart(2, '0')} ${getRandomString()}`,
			});

			await apiHelpers.headlessAdminUser.assignUserToAccountByEmailAddress(
				account.id,
				[user.emailAddress]
			);

			users.push(user);
		}

		await accountUsersPage.goto();

		await setItemsPerPage(page, 20);

		for (const [index, user] of users.entries()) {
			if (index < 20) {
				await expect(
					accountUsersPage.usersTable.cell(user.emailAddress)
				).toBeVisible();
			}
			else {
				await expect(
					accountUsersPage.usersTable.cell(user.emailAddress)
				).toHaveCount(0);
			}
		}

		await nextPage(page);

		for (const [index, user] of users.entries()) {
			if (index < 20) {
				await expect(
					accountUsersPage.usersTable.cell(user.emailAddress)
				).toHaveCount(0);
			}
			else {
				await expect(
					accountUsersPage.usersTable.cell(user.emailAddress)
				).toBeVisible();
			}
		}

		await setItemsPerPage(page, 40);

		for (const user of users) {
			await expect(
				accountUsersPage.usersTable.cell(user.emailAddress)
			).toBeVisible();
		}
	}
);

test(
	'Can filter account users by any / selected / no assigned account',
	{tag: ['@LPD-48750']},
	async ({accountUsersAccountSelectorPage, accountUsersPage, apiHelpers}) => {
		const account1 = await apiHelpers.headlessAdminUser.postAccount({
			type: 'business',
		});

		const user1 = await apiHelpers.headlessAdminUser.postUserAccount();

		await apiHelpers.headlessAdminUser.assignUserToAccountByEmailAddress(
			account1.id,
			[user1.emailAddress]
		);

		const account2 = await apiHelpers.headlessAdminUser.postAccount({
			type: 'person',
		});

		const user2 = await apiHelpers.headlessAdminUser.postUserAccount();

		await apiHelpers.headlessAdminUser.assignUserToAccountByEmailAddress(
			account2.id,
			[user2.emailAddress]
		);

		const user3 = await apiHelpers.headlessAdminUser.postUserAccount();

		await accountUsersPage.goto();

		await expect(
			accountUsersPage.usersTable.cell(user1.emailAddress)
		).toBeVisible();
		await expect(
			accountUsersPage.usersTable.cell(user2.emailAddress)
		).toBeVisible();
		await expect(
			accountUsersPage.usersTable.cell(user3.emailAddress)
		).toHaveCount(0);

		await expect(async () => {
			await accountUsersPage.usersTable.filterButton.click();

			await expect(
				accountUsersPage.usersTable.filterMenuItem(
					'No Assigned Account'
				)
			).toBeVisible();
		}).toPass();

		await accountUsersPage.usersTable
			.filterMenuItem('No Assigned Account')
			.click();

		await expect(
			accountUsersPage.usersTable.cell(user1.emailAddress)
		).toHaveCount(0);
		await expect(
			accountUsersPage.usersTable.cell(user2.emailAddress)
		).toHaveCount(0);
		await expect(
			accountUsersPage.usersTable.cell(user3.emailAddress)
		).toBeVisible();

		await expect(async () => {
			await accountUsersPage.usersTable.filterButton.click();

			await expect(
				accountUsersPage.usersTable.filterMenuItem('Selected Accounts')
			).toBeVisible();
		}).toPass();

		await accountUsersPage.usersTable
			.filterMenuItem('Selected Accounts')
			.click();

		await expect(
			accountUsersAccountSelectorPage.accountsTable.searchInput
		).toBeEditable();
		await expect(
			accountUsersAccountSelectorPage.accountsTable.cell(account1.name)
		).toBeVisible();
		await expect(
			accountUsersAccountSelectorPage.accountsTable.cell(account2.name)
		).toBeVisible();

		await expect(async () => {
			await accountUsersAccountSelectorPage.accountsTable.filterButton.click();

			await expect(
				accountUsersAccountSelectorPage.accountsTable.filterMenuItem(
					'Person'
				)
			).toBeVisible();
		}).toPass();

		await accountUsersAccountSelectorPage.accountsTable
			.filterMenuItem('Person')
			.click();

		await expect(
			accountUsersAccountSelectorPage.accountsTable.cell(account1.name)
		).toHaveCount(0);
		await expect(
			accountUsersAccountSelectorPage.accountsTable.cell(account2.name)
		).toBeVisible();

		await (
			await accountUsersAccountSelectorPage.accountsTable.rowCheckbox(
				account2.name
			)
		).check();

		await accountUsersAccountSelectorPage.selectButton.click();

		await expect(
			accountUsersPage.usersTable.cell(user1.emailAddress)
		).toHaveCount(0);
		await expect(
			accountUsersPage.usersTable.cell(user2.emailAddress)
		).toBeVisible();
		await expect(
			accountUsersPage.usersTable.cell(user3.emailAddress)
		).toHaveCount(0);

		await expect(async () => {
			await accountUsersPage.usersTable.filterButton.click();

			await expect(
				accountUsersPage.usersTable.filterMenuItem('Selected Accounts')
			).toBeVisible();
		}).toPass();

		await accountUsersPage.usersTable
			.filterMenuItem('Selected Accounts')
			.click();

		await expect(
			accountUsersAccountSelectorPage.accountsTable.searchInput
		).toBeEditable();
		await expect(
			accountUsersAccountSelectorPage.accountsTable.cell(account1.name)
		).toBeVisible();
		await expect(
			accountUsersAccountSelectorPage.accountsTable.cell(account2.name)
		).toBeVisible();

		await expect(async () => {
			await accountUsersAccountSelectorPage.accountsTable.filterButton.click();

			await expect(
				accountUsersAccountSelectorPage.accountsTable.filterMenuItem(
					'Business'
				)
			).toBeVisible();
		}).toPass();

		await accountUsersAccountSelectorPage.accountsTable
			.filterMenuItem('Business')
			.click();

		await expect(
			accountUsersAccountSelectorPage.accountsTable.cell(account1.name)
		).toBeVisible();
		await expect(
			accountUsersAccountSelectorPage.accountsTable.cell(account2.name)
		).toHaveCount(0);

		await (
			await accountUsersAccountSelectorPage.accountsTable.rowCheckbox(
				account1.name
			)
		).check();

		await accountUsersAccountSelectorPage.selectButton.click();

		await expect(
			accountUsersPage.usersTable.cell(user1.emailAddress)
		).toBeVisible();
		await expect(
			accountUsersPage.usersTable.cell(user2.emailAddress)
		).toHaveCount(0);
		await expect(
			accountUsersPage.usersTable.cell(user3.emailAddress)
		).toHaveCount(0);
	}
);

test(
	'Can filter account users by status',
	{tag: ['@LPD-48750']},
	async ({accountUsersPage, apiHelpers, page}) => {
		page.on('dialog', (dialog) => dialog.accept());

		const account = await apiHelpers.headlessAdminUser.postAccount({
			type: 'business',
		});

		const user1 = await apiHelpers.headlessAdminUser.postUserAccount();

		await apiHelpers.headlessAdminUser.assignUserToAccountByEmailAddress(
			account.id,
			[user1.emailAddress]
		);

		const user2 = await apiHelpers.headlessAdminUser.postUserAccount();

		await apiHelpers.headlessAdminUser.assignUserToAccountByEmailAddress(
			account.id,
			[user2.emailAddress]
		);

		await accountUsersPage.goto();

		await expect(
			accountUsersPage.usersTable.cell(user1.name)
		).toBeVisible();
		await expect(
			accountUsersPage.usersTable.cell(user2.name)
		).toBeVisible();

		await (
			await accountUsersPage.usersTable.rowActions(user1.name)
		).click();
		await accountUsersPage.deactivateButton.click();

		await expect(accountUsersPage.usersTable.cell(user1.name)).toHaveCount(
			0
		);
		await expect(
			accountUsersPage.usersTable.cell(user2.name)
		).toBeVisible();

		await expect(async () => {
			await accountUsersPage.usersTable.filterButton.click();

			await expect(
				accountUsersPage.usersTable.filterMenuItem('Inactive')
			).toBeVisible();
		}).toPass();

		await accountUsersPage.usersTable.filterMenuItem('Inactive').click();

		await expect(
			accountUsersPage.usersTable.cell(user1.name)
		).toBeVisible();
		await expect(accountUsersPage.usersTable.cell(user2.name)).toHaveCount(
			0
		);

		await (
			await accountUsersPage.usersTable.rowActions(user1.name)
		).click();
		await accountUsersPage.activateButton.click();

		await expect(accountUsersPage.usersTable.cell(user1.name)).toHaveCount(
			0
		);
		await expect(accountUsersPage.usersTable.cell(user2.name)).toHaveCount(
			0
		);

		await expect(async () => {
			await accountUsersPage.usersTable.filterButton.click();

			await expect(
				accountUsersPage.usersTable.filterMenuItem('Active')
			).toBeVisible();
		}).toPass();

		await accountUsersPage.usersTable.filterMenuItem('Active').click();

		await expect(
			accountUsersPage.usersTable.cell(user1.name)
		).toBeVisible();
		await expect(
			accountUsersPage.usersTable.cell(user2.name)
		).toBeVisible();
	}
);

test(
	'A person account cannot be selected when a user is already assigned to it',
	{tag: ['@LPD-48750']},
	async ({accountUsersAccountSelectorPage, accountUsersPage, apiHelpers}) => {
		const account1 = await apiHelpers.headlessAdminUser.postAccount({
			type: 'person',
		});

		const user = await apiHelpers.headlessAdminUser.postUserAccount();

		await apiHelpers.headlessAdminUser.assignUserToAccountByEmailAddress(
			account1.id,
			[user.emailAddress]
		);

		const account2 = await apiHelpers.headlessAdminUser.postAccount({
			type: 'person',
		});

		await accountUsersPage.goto();

		await accountUsersPage.usersTable.newButton.click();

		await expect(
			accountUsersAccountSelectorPage.accountsTable.cell(account1.name)
		).toHaveCount(0);
		await expect(
			accountUsersAccountSelectorPage.accountsTable.cell(account2.name)
		).toBeVisible();
	}
);

test(
	'Can search account users',
	{tag: ['@LPD-48750']},
	async ({accountUsersPage, apiHelpers}) => {
		const account = await apiHelpers.headlessAdminUser.postAccount({
			type: 'business',
		});

		const user1 = await apiHelpers.headlessAdminUser.postUserAccount();

		await apiHelpers.headlessAdminUser.assignUserToAccountByEmailAddress(
			account.id,
			[user1.emailAddress]
		);

		const user2 = await apiHelpers.headlessAdminUser.postUserAccount();

		await apiHelpers.headlessAdminUser.assignUserToAccountByEmailAddress(
			account.id,
			[user2.emailAddress]
		);

		await accountUsersPage.goto();

		await accountUsersPage.usersTable.search(getRandomString());

		await expect(accountUsersPage.usersTable.cell(user1.name)).toHaveCount(
			0
		);
		await expect(accountUsersPage.usersTable.cell(user2.name)).toHaveCount(
			0
		);

		await accountUsersPage.usersTable.search(user1.name);

		await expect(
			accountUsersPage.usersTable.cell(user1.name)
		).toBeVisible();
		await expect(accountUsersPage.usersTable.cell(user2.name)).toHaveCount(
			0
		);

		await accountUsersPage.usersTable.search(user2.name);

		await expect(accountUsersPage.usersTable.cell(user1.name)).toHaveCount(
			0
		);
		await expect(
			accountUsersPage.usersTable.cell(user2.name)
		).toBeVisible();

		await accountUsersPage.usersTable.search(user1.emailAddress);

		await expect(
			accountUsersPage.usersTable.cell(user1.name)
		).toBeVisible();
		await expect(accountUsersPage.usersTable.cell(user2.name)).toHaveCount(
			0
		);

		await accountUsersPage.usersTable.search(user2.emailAddress);

		await expect(accountUsersPage.usersTable.cell(user1.name)).toHaveCount(
			0
		);
		await expect(
			accountUsersPage.usersTable.cell(user2.name)
		).toBeVisible();

		await accountUsersPage.usersTable.search('');

		await expect(
			accountUsersPage.usersTable.cell(user1.name)
		).toBeVisible();
		await expect(
			accountUsersPage.usersTable.cell(user2.name)
		).toBeVisible();
	}
);

test(
	'Can deactivate/activate an account user',
	{tag: ['@LPD-48750']},
	async ({accountUsersPage, apiHelpers, page}) => {
		page.on('dialog', (dialog) => dialog.accept());

		const account = await apiHelpers.headlessAdminUser.postAccount({
			type: 'business',
		});

		const user1 = await apiHelpers.headlessAdminUser.postUserAccount();

		await apiHelpers.headlessAdminUser.assignUserToAccountByEmailAddress(
			account.id,
			[user1.emailAddress]
		);

		const user2 = await apiHelpers.headlessAdminUser.postUserAccount();

		await apiHelpers.headlessAdminUser.assignUserToAccountByEmailAddress(
			account.id,
			[user2.emailAddress]
		);

		await accountUsersPage.goto();

		await expect(
			accountUsersPage.usersTable.cell(user1.name)
		).toBeVisible();
		await expect(
			accountUsersPage.usersTable.cell(user2.name)
		).toBeVisible();

		await (
			await accountUsersPage.usersTable.rowActions(user1.name)
		).click();
		await accountUsersPage.deactivateButton.click();

		await expect(accountUsersPage.usersTable.cell(user1.name)).toHaveCount(
			0
		);
		await expect(
			accountUsersPage.usersTable.cell(user2.name)
		).toBeVisible();

		await expect(async () => {
			await accountUsersPage.usersTable.filterButton.click();

			await expect(
				accountUsersPage.usersTable.filterMenuItem('Inactive')
			).toBeVisible();
		}).toPass();

		await accountUsersPage.usersTable.filterMenuItem('Inactive').click();

		await expect(
			accountUsersPage.usersTable.cell(user1.name)
		).toBeVisible();
		await expect(accountUsersPage.usersTable.cell(user2.name)).toHaveCount(
			0
		);

		await (
			await accountUsersPage.usersTable.rowActions(user1.name)
		).click();
		await accountUsersPage.activateButton.click();

		await expect(accountUsersPage.usersTable.cell(user1.name)).toHaveCount(
			0
		);
		await expect(accountUsersPage.usersTable.cell(user2.name)).toHaveCount(
			0
		);

		await expect(async () => {
			await accountUsersPage.usersTable.filterButton.click();

			await expect(
				accountUsersPage.usersTable.filterMenuItem('Active')
			).toBeVisible();
		}).toPass();

		await accountUsersPage.usersTable.filterMenuItem('Active').click();

		await expect(
			accountUsersPage.usersTable.cell(user1.name)
		).toBeVisible();
		await expect(
			accountUsersPage.usersTable.cell(user2.name)
		).toBeVisible();
	}
);

test(
	'Can bulk deactivate/activate account users',
	{tag: ['@LPD-48750']},
	async ({accountUsersPage, apiHelpers, page}) => {
		page.on('dialog', (dialog) => dialog.accept());

		const account = await apiHelpers.headlessAdminUser.postAccount({
			type: 'business',
		});

		const user1 = await apiHelpers.headlessAdminUser.postUserAccount();

		await apiHelpers.headlessAdminUser.assignUserToAccountByEmailAddress(
			account.id,
			[user1.emailAddress]
		);

		const user2 = await apiHelpers.headlessAdminUser.postUserAccount();

		await apiHelpers.headlessAdminUser.assignUserToAccountByEmailAddress(
			account.id,
			[user2.emailAddress]
		);

		await accountUsersPage.goto();

		await expect(
			accountUsersPage.usersTable.cell(user1.name)
		).toBeVisible();
		await expect(
			accountUsersPage.usersTable.cell(user2.name)
		).toBeVisible();

		await (
			await accountUsersPage.usersTable.rowCheckbox(user1.name)
		).check();
		await (
			await accountUsersPage.usersTable.rowCheckbox(user2.name)
		).check();
		await accountUsersPage.deactivateButton.click();

		await expect(accountUsersPage.usersTable.cell(user1.name)).toHaveCount(
			0
		);
		await expect(accountUsersPage.usersTable.cell(user2.name)).toHaveCount(
			0
		);

		await expect(async () => {
			await accountUsersPage.usersTable.filterButton.click();

			await expect(
				accountUsersPage.usersTable.filterMenuItem('Inactive')
			).toBeVisible();
		}).toPass();

		await accountUsersPage.usersTable.filterMenuItem('Inactive').click();

		await expect(
			accountUsersPage.usersTable.cell(user1.name)
		).toBeVisible();
		await expect(
			accountUsersPage.usersTable.cell(user2.name)
		).toBeVisible();

		await (
			await accountUsersPage.usersTable.rowCheckbox(user1.name)
		).check();
		await (
			await accountUsersPage.usersTable.rowCheckbox(user2.name)
		).check();
		await accountUsersPage.activateButton.click();

		await expect(accountUsersPage.usersTable.cell(user1.name)).toHaveCount(
			0
		);
		await expect(accountUsersPage.usersTable.cell(user2.name)).toHaveCount(
			0
		);

		await expect(async () => {
			await accountUsersPage.usersTable.filterButton.click();

			await expect(
				accountUsersPage.usersTable.filterMenuItem('Active')
			).toBeVisible();
		}).toPass();

		await accountUsersPage.usersTable.filterMenuItem('Active').click();

		await expect(
			accountUsersPage.usersTable.cell(user1.name)
		).toBeVisible();
		await expect(
			accountUsersPage.usersTable.cell(user2.name)
		).toBeVisible();
	}
);

test(
	'Can impersonate an account user',
	{tag: ['@LPD-48750']},
	async ({accountUsersPage, apiHelpers, context}) => {
		const account = await apiHelpers.headlessAdminUser.postAccount({
			type: 'business',
		});

		const user = await apiHelpers.headlessAdminUser.postUserAccount();

		await apiHelpers.headlessAdminUser.assignUserToAccountByEmailAddress(
			account.id,
			[user.emailAddress]
		);

		await accountUsersPage.goto();

		await expect(accountUsersPage.usersTable.cell(user.name)).toBeVisible();

		const pagePromise = context.waitForEvent('page');

		await (await accountUsersPage.usersTable.rowActions(user.name)).click();
		await accountUsersPage.impersonateUserMenuItem.click();

		const newPage = await pagePromise;

		await expect(newPage.getByTitle('User Profile Menu')).toBeVisible();
	}
);

test(
	'Back or cancel button does not create an account user',
	{tag: ['@LPD-48750']},
	async ({
		accountUsersAccountSelectorPage,
		accountUsersPage,
		apiHelpers,
		editUserPage,
	}) => {
		const account = await apiHelpers.headlessAdminUser.postAccount({
			type: 'business',
		});

		await accountUsersPage.goto();

		await expect(
			accountUsersPage.usersTable.cell(account.name)
		).toHaveCount(0);

		await accountUsersPage.usersTable.newButton.click();

		await expect(
			accountUsersAccountSelectorPage.accountsTable.searchInput
		).toBeEditable();
		await expect(
			accountUsersAccountSelectorPage.accountsTable.cell(account.name)
		).toBeVisible();

		await accountUsersAccountSelectorPage
			.chooseButton(account.name)
			.click();

		await editUserPage.backLink.click();

		await expect(
			accountUsersPage.usersTable.cell(account.name)
		).toHaveCount(0);

		await accountUsersPage.usersTable.newButton.click();

		await expect(
			accountUsersAccountSelectorPage.accountsTable.searchInput
		).toBeEditable();
		await expect(
			accountUsersAccountSelectorPage.accountsTable.cell(account.name)
		).toBeVisible();

		await accountUsersAccountSelectorPage
			.chooseButton(account.name)
			.click();

		await editUserPage.cancelButton.click();

		await expect(
			accountUsersPage.usersTable.cell(account.name)
		).toHaveCount(0);
	}
);

test(
	'Can add an account to an account user',
	{tag: ['@LPD-48750', '@LPD-58550']},
	async ({
		accountUsersAccountSelectorPage,
		accountUsersPage,
		apiHelpers,
		editUserPage,
		page,
	}) => {
		const account1 = await apiHelpers.headlessAdminUser.postAccount({
			type: 'business',
		});

		const user = await apiHelpers.headlessAdminUser.postUserAccount();

		await apiHelpers.headlessAdminUser.assignUserToAccountByEmailAddress(
			account1.id,
			[user.emailAddress]
		);

		const account2 = await apiHelpers.headlessAdminUser.postAccount({
			type: 'business',
		});

		await accountUsersPage.goto();

		await expect(accountUsersPage.usersTable.cell(user.name)).toBeVisible();
		await expect(
			(await accountUsersPage.usersTable.row(1, user.name)).row
		).toContainText(account1.name);
		await expect(
			(await accountUsersPage.usersTable.row(1, user.name)).row
		).not.toContainText(account2.name);

		await (await accountUsersPage.usersTable.cellLink(user.name)).click();

		await editUserPage.accountsLink.click();
		await editUserPage.selectAccountsButton.click();

		await expect(
			accountUsersAccountSelectorPage.accountsTable.searchInput
		).toBeEditable();

		await expect(
			accountUsersAccountSelectorPage.accountsTable.cell('Active')
		).toBeVisible();

		await accountUsersAccountSelectorPage
			.chooseButton(account2.name)
			.click();
		await editUserPage.saveButton.click();

		await waitForAlert(page);

		await editUserPage.cancelButton.click();

		await expect(accountUsersPage.usersTable.cell(user.name)).toBeVisible();
		await expect(
			(await accountUsersPage.usersTable.row(1, user.name)).row
		).toContainText(account1.name);
		await expect(
			(await accountUsersPage.usersTable.row(1, user.name)).row
		).toContainText(account2.name);
		await expect(accountUsersPage.usersTable.cell('Active')).toBeVisible();
	}
);

test(
	'Can filter users by account association',
	{tag: ['@LPD-48750', '@LPS-107598', '@LPS-129713']},
	async ({apiHelpers, usersAndOrganizationsPage}) => {
		const account = await apiHelpers.headlessAdminUser.postAccount({
			type: 'business',
		});

		const user1 = await apiHelpers.headlessAdminUser.postUserAccount();

		await apiHelpers.headlessAdminUser.assignUserToAccountByEmailAddress(
			account.id,
			[user1.emailAddress]
		);

		const user2 = await apiHelpers.headlessAdminUser.postUserAccount();

		await usersAndOrganizationsPage.goToUsers();

		await expect(
			usersAndOrganizationsPage.usersTableCell(user1.name)
		).toBeVisible();
		await expect(
			usersAndOrganizationsPage.usersTableCell(user2.name)
		).toBeVisible();

		await expect(async () => {
			await usersAndOrganizationsPage.tableFilterMenu.click();

			await expect(
				usersAndOrganizationsPage.tableFilterMenuItem('Company Users')
			).toBeVisible();
		}).toPass();

		await usersAndOrganizationsPage
			.tableFilterMenuItem('Company Users')
			.click();

		await expect(
			usersAndOrganizationsPage.usersTableCell(user1.name)
		).toHaveCount(0);
		await expect(
			usersAndOrganizationsPage.usersTableCell(user2.name)
		).toBeVisible();

		await expect(async () => {
			await usersAndOrganizationsPage.tableFilterMenu.click();

			await expect(
				usersAndOrganizationsPage.tableFilterMenuItem('Account Users')
			).toBeVisible();
		}).toPass();

		await usersAndOrganizationsPage
			.tableFilterMenuItem('Account Users')
			.click();

		await expect(
			usersAndOrganizationsPage.usersTableCell(user1.name)
		).toBeVisible();
		await expect(
			usersAndOrganizationsPage.usersTableCell(user2.name)
		).toHaveCount(0);
	}
);

test(
	'An account is disabled if already associated to a user',
	{tag: ['@LPD-53780']},
	async ({
		accountUsersAccountSelectorPage,
		apiHelpers,
		editUserPage,
		usersAndOrganizationsPage,
	}) => {
		const user = await apiHelpers.headlessAdminUser.postUserAccount();

		const accounts = [];

		for (let i = 0; i < 25; i++) {
			const account = await apiHelpers.headlessAdminUser.postAccount({
				name: `${String(i).padStart(2, '0')}_${getRandomString()}`,
				type: 'business',
			});

			if (i < 23) {
				await apiHelpers.headlessAdminUser.assignUserToAccountByEmailAddress(
					account.id,
					[user.emailAddress]
				);
			}

			accounts.push(account);
		}

		await usersAndOrganizationsPage.goToUsers();

		await (
			await usersAndOrganizationsPage.usersTableRowLink(
				user.alternateName
			)
		).click();
		await editUserPage.membershipsLink.click();

		await expect(async () => {
			await expect(editUserPage.selectAccountsButton).toBeVisible();
			await expect(
				editUserPage.membershipsNoAccountsMessage
			).not.toBeVisible();
		}).toPass();

		await editUserPage.selectAccountsButton.click();

		await expect(
			accountUsersAccountSelectorPage.accountsTable.searchInput
		).toBeEditable();

		await expect(async () => {
			await accountUsersAccountSelectorPage.accountsTable.search(
				accounts[0].name
			);

			await expect(
				await accountUsersAccountSelectorPage.accountsTable.rowCheckbox(
					accounts[0].name
				)
			).toBeChecked();
			await expect(
				await accountUsersAccountSelectorPage.accountsTable.rowCheckbox(
					accounts[0].name
				)
			).toBeDisabled();
		}).toPass();

		await expect(async () => {
			await accountUsersAccountSelectorPage.accountsTable.search(
				accounts[21].name
			);

			await expect(
				await accountUsersAccountSelectorPage.accountsTable.rowCheckbox(
					accounts[21].name
				)
			).toBeChecked();
			await expect(
				await accountUsersAccountSelectorPage.accountsTable.rowCheckbox(
					accounts[21].name
				)
			).toBeDisabled();
		}).toPass();

		await expect(async () => {
			await accountUsersAccountSelectorPage.accountsTable.search(
				accounts[24].name
			);

			await expect(
				await accountUsersAccountSelectorPage.accountsTable.rowCheckbox(
					accounts[24].name
				)
			).not.toBeChecked();
			await expect(
				await accountUsersAccountSelectorPage.accountsTable.rowCheckbox(
					accounts[24].name
				)
			).toBeEnabled();
		}).toPass();
	}
);

test(
	'Clearing filter goes back to default Any Account filter',
	{tag: ['@LPD-54580']},
	async ({accountUsersPage, apiHelpers}) => {
		const account = await apiHelpers.headlessAdminUser.postAccount({
			type: 'business',
		});

		apiHelpers.data.push({id: account.id, type: 'account'});

		const user1 = await apiHelpers.headlessAdminUser.postUserAccount();

		await apiHelpers.headlessAdminUser.assignUserToAccountByEmailAddress(
			account.id,
			[user1.emailAddress]
		);

		await accountUsersPage.goto();

		await accountUsersPage.changeFilter('No Assigned Account');

		await expect(accountUsersPage.usersTable.clearButton).toBeVisible();

		await accountUsersPage.usersTable.filterButton.click();

		await expect(
			accountUsersPage.usersTable.filterMenuItem('No Assigned Account')
		).toHaveClass(/active/);

		await accountUsersPage.usersTable.filterButton.click();
		await accountUsersPage.usersTable.clearButton.click();

		await expect(accountUsersPage.usersTable.clearButton).not.toBeVisible();

		await accountUsersPage.usersTable.filterButton.click();

		await expect(
			accountUsersPage.usersTable.filterMenuItem('Any Account')
		).toHaveClass(/active/);
	}
);
