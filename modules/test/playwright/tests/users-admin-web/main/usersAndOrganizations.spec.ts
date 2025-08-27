/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';
import {createReadStream} from 'fs';
import path from 'path';

import {accountSettingsPagesTest} from '../../../fixtures/accountSettingsPagesTest';
import {apiHelpersTest} from '../../../fixtures/apiHelpersTest';
import {dataApiHelpersTest} from '../../../fixtures/dataApiHelpersTest';
import {featureFlagsTest} from '../../../fixtures/featureFlagsTest';
import {loginTest} from '../../../fixtures/loginTest';
import {usersAndOrganizationsPagesTest} from '../../../fixtures/usersAndOrganizationsPagesTest';
import {getRandomInt} from '../../../utils/getRandomInt';
import getRandomString from '../../../utils/getRandomString';
import performLogin, {
	performLoginViaApi,
	performLogout,
	userData,
} from '../../../utils/performLogin';
import {waitForAlert} from '../../../utils/waitForAlert';

export const test = mergeTests(
	accountSettingsPagesTest,
	apiHelpersTest,
	dataApiHelpersTest,
	featureFlagsTest({
		'LPD-47858': {enabled: true},
	}),
	loginTest(),
	usersAndOrganizationsPagesTest
);

test(
	'Check export/import menu visibility',
	{tag: '@LPD-204541'},
	async ({usersAndOrganizationsPage}) => {
		await usersAndOrganizationsPage.goToUsers();
		await usersAndOrganizationsPage.openOptionsMenu();
		await expect(
			usersAndOrganizationsPage.exportImportOptionsMenuItem
		).toHaveCount(0);
		await expect(
			usersAndOrganizationsPage.exportUsersOptionsMenuItem
		).toBeVisible();
		await expect(
			usersAndOrganizationsPage.manageCustomFieldsOptionsMenuItem
		).toBeVisible();

		await usersAndOrganizationsPage.goToOrganizations();
		await usersAndOrganizationsPage.openOptionsMenu();
		await expect(
			usersAndOrganizationsPage.exportImportOptionsMenuItem
		).toBeVisible();
		await expect(
			usersAndOrganizationsPage.exportUsersOptionsMenuItem
		).toBeVisible();
		await expect(
			usersAndOrganizationsPage.manageCustomFieldsOptionsMenuItem
		).toHaveCount(0);
	}
);

test(
	'Check escape of memberships account name',
	{tag: '@LPD-15224'},
	async ({apiHelpers, editUserPage, page, usersAndOrganizationsPage}) => {
		await page.goto('/');

		const account = await apiHelpers.headlessAdminUser.postAccount({
			name: '<img src="x" onError="alert(document.location)">',
		});

		await apiHelpers.headlessAdminUser.assignUserToAccountByEmailAddress(
			account.id,
			['test@liferay.com']
		);

		try {
			await usersAndOrganizationsPage.goToUsers();

			await (
				await usersAndOrganizationsPage.usersTableRowLink('test')
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
		}
		finally {
			await apiHelpers.headlessAdminUser.deleteAccount(account.id);
		}
	}
);

test(
	'Check WebDAV password is generated',
	{tag: '@LPD-15423'},
	async ({editUserPage, page, usersAndOrganizationsPage}) => {
		await page.goto('/');

		await usersAndOrganizationsPage.goToUsers();
		await (
			await usersAndOrganizationsPage.usersTableRowLink('test')
		).click();

		await editUserPage.passwordLink.click();
		await editUserPage.generateWebDAVPasswordButton.click();

		await expect(editUserPage.webDAVPasswordLabel).toBeVisible();
	}
);

test(
	'Update user information',
	{tag: '@LPD-28908'},
	async ({apiHelpers, editUserPage, page, usersAndOrganizationsPage}) => {
		const user = await apiHelpers.headlessAdminUser.postUserAccount();

		await page.goto('/');

		await usersAndOrganizationsPage.goToUsers();
		await (
			await usersAndOrganizationsPage.usersTableRowLink(
				user.alternateName
			)
		).click();

		await editUserPage.screenNameInput.fill('User' + getRandomInt());
		await editUserPage.emailAddressInput.fill(
			'User' + getRandomInt() + '@liferay.com'
		);
		await editUserPage.saveButton.click();
		await editUserPage.yourPasswordInput.fill('test');
		await editUserPage.confirmButton.click();

		await expect(
			page
				.getByText('Success:Your request completed successfully.')
				.or(
					page.getByText(
						'Success:Your email verification code has been sent'
					)
				)
		).toBeVisible();
	}
);

test(
	'Add Organization Team',
	{tag: '@LPD-30589'},
	async ({
		apiHelpers,
		editOrganizationPage,
		page,
		siteConfigurationDetailsPage,
		siteSettingsPage,
		teamsPage,
		usersAndOrganizationsPage,
	}) => {
		const organization =
			await apiHelpers.headlessAdminUser.postOrganization();

		await apiHelpers.headlessAdminUser.assignUserToOrganizationByEmailAddress(
			organization.id,
			'test@liferay.com'
		);

		apiHelpers.data.push({
			id: `${organization.id}_test@liferay.com`,
			type: 'organizationUserAccountAssociation',
		});

		await usersAndOrganizationsPage.goToOrganizations();

		await (
			await usersAndOrganizationsPage.organizationsTable.rowActions(
				organization.name
			)
		).click();
		await editOrganizationPage.organizationEditMenuItem.click();
		await editOrganizationPage.organizationSiteLink.click();
		await editOrganizationPage.createSiteToggle.check();
		await editOrganizationPage.organizationSiteSaveButton.click();

		await waitForAlert(page);

		await siteSettingsPage.goToSiteSetting(
			'Site Configuration',
			null,
			'/' + organization.name
		);

		await siteConfigurationDetailsPage.allowManualMembershipManagementToggle.check();
		await siteConfigurationDetailsPage.saveButton.click();

		await waitForAlert(page);

		await teamsPage.goTo('/' + organization.name);

		const newTeamName = 'Team' + getRandomInt();

		await teamsPage.newTeamButton.click();
		await teamsPage.nameInput.fill(newTeamName);
		await teamsPage.saveButton.click();

		await waitForAlert(page);

		await expect(teamsPage.teamsTable.cell(newTeamName)).toBeVisible();
	}
);

test(
	'Check whether admin user is redirected to organization page after user to org assignment',
	{tag: '@LPD-31669'},
	async ({
		apiHelpers,
		assignUsersPage,
		organizationUsersPage,
		page,
		usersAndOrganizationsPage,
	}) => {
		const userName = 'Test Test';

		const organization =
			await apiHelpers.headlessAdminUser.postOrganization();

		await usersAndOrganizationsPage.goToOrganizations();

		await (
			await usersAndOrganizationsPage.organizationsTable.rowActions(
				organization.name
			)
		).click();
		await usersAndOrganizationsPage.assignUsersMenuItem.click();

		await (await assignUsersPage.usersTableRowCheckbox(userName)).check();
		await assignUsersPage.doneButton.click();

		await waitForAlert(page);

		await expect(
			await organizationUsersPage.usersTableRowLink(userName)
		).toBeVisible();
	}
);

test(
	'Remove member',
	{tag: '@LPD-31978'},
	async ({apiHelpers, organizationUsersPage, usersAndOrganizationsPage}) => {
		const userAccount =
			await apiHelpers.headlessAdminUser.postUserAccount();

		const organization =
			await apiHelpers.headlessAdminUser.postOrganization();

		await apiHelpers.headlessAdminUser.assignUserToOrganizationByEmailAddress(
			organization.id,
			userAccount.emailAddress
		);

		apiHelpers.data.push({
			id: `${organization.id}_test@liferay.com`,
			type: 'organizationUserAccountAssociation',
		});

		await usersAndOrganizationsPage.goToOrganizations();

		await usersAndOrganizationsPage.organizationsTable
			.valueLink(organization.name)
			.click();

		await expect(
			await organizationUsersPage.usersTableRowLink(
				userAccount.givenName + ' ' + userAccount.familyName
			)
		).toBeVisible();

		await (
			await organizationUsersPage.usersTableRowActions(
				userAccount.givenName + ' ' + userAccount.familyName
			)
		).click();
		await organizationUsersPage.removeMenuItem.click();

		await usersAndOrganizationsPage.goToOrganizations();

		await usersAndOrganizationsPage.organizationsTable
			.valueLink(organization.name)
			.click();

		await expect(organizationUsersPage.filterButton).toBeVisible();
		await expect(
			await organizationUsersPage.screenName(
				userAccount.givenName + ' ' + userAccount.familyName
			)
		).toHaveCount(0);
	}
);

test(
	'Assign User',
	{tag: '@LPD-31020'},
	async ({apiHelpers, usersAndOrganizationsPage}) => {
		const user = await apiHelpers.headlessAdminUser.postUserAccount();
		const organization =
			await apiHelpers.headlessAdminUser.postOrganization();

		await usersAndOrganizationsPage.goToOrganizations();

		await (
			await usersAndOrganizationsPage.organizationsTable.rowActions(
				organization.name
			)
		).click();

		await usersAndOrganizationsPage.assignUsersMenuItem.click();

		await (
			await usersAndOrganizationsPage.assignUsersCheckbox(user.name)
		).check();

		await usersAndOrganizationsPage.assignUsersDoneButton.click();

		apiHelpers.data.push({
			id: `${organization.id}_${user.emailAddress}`,
			type: 'organizationUserAccountAssociation',
		});

		await usersAndOrganizationsPage.goToOrganizations();

		await usersAndOrganizationsPage.organizationsTable
			.valueLink(organization.name)
			.click();

		await expect(
			(
				await usersAndOrganizationsPage.organizationUsersTableRow(
					1,
					user.name,
					true
				)
			).row
		).toBeVisible();
	}
);

test(
	'Search by Organizations when setting a users organization roles',
	{tag: '@LPD-31645'},
	async ({apiHelpers, editUserPage, page, usersAndOrganizationsPage}) => {
		const user = await apiHelpers.headlessAdminUser.postUserAccount();

		const organization1 =
			await apiHelpers.headlessAdminUser.postOrganization();

		await apiHelpers.headlessAdminUser.assignUserToOrganizationByEmailAddress(
			organization1.id,
			user.emailAddress
		);

		apiHelpers.data.push({
			id: `${organization1.id}_${user.emailAddress}`,
			type: 'organizationUserAccountAssociation',
		});

		const organization2 =
			await apiHelpers.headlessAdminUser.postOrganization();

		await apiHelpers.headlessAdminUser.assignUserToOrganizationByEmailAddress(
			organization2.id,
			user.emailAddress
		);

		apiHelpers.data.push({
			id: `${organization2.id}_${user.emailAddress}`,
			type: 'organizationUserAccountAssociation',
		});

		await usersAndOrganizationsPage.goToUsers();
		await (
			await usersAndOrganizationsPage.usersTableRowLink(
				user.alternateName
			)
		).click();

		await editUserPage.rolesLink.click();
		await editUserPage.selectOrganizationRolesButton.click();

		await expect(
			editUserPage.selectOrganizationRolesSearchBar
		).toBeEnabled();

		expect(
			(
				await editUserPage.selectOrganizationRolesTable
					.getByRole('row')
					.all()
			).length
		).toEqual(3);

		await editUserPage.selectOrganizationRolesSearchBar.fill(
			organization1.name
		);
		await editUserPage.selectOrganizationRolesSearchBarButton.click();

		await page.waitForTimeout(500);

		await expect(
			(
				await editUserPage.selectOrganizationRolesTableRow(
					0,
					organization1.name,
					true
				)
			).row
		).toBeVisible();
		expect(
			(
				await editUserPage.selectOrganizationRolesTable
					.getByRole('row')
					.all()
			).length
		).toEqual(2);
	}
);

test(
	'Last login visibility',
	{tag: '@LPD-33048'},
	async ({usersAndOrganizationsPage}) => {
		await usersAndOrganizationsPage.goToUsers();

		await expect(
			(
				await usersAndOrganizationsPage.usersTableRow(2, 'test', true)
			).row.getByText('ago')
		).toHaveCount(1);

		await usersAndOrganizationsPage.tableOrderMenu.click();

		await expect(
			usersAndOrganizationsPage.tableOrderMenuItem('Last Login Date')
		).toBeVisible();
	}
);

test(
	'Check custom field is escaped',
	{tag: '@LPD-29981'},
	async ({page, usersAndOrganizationsPage}) => {
		await page.goto('/');

		await usersAndOrganizationsPage.goToUsers();
		await usersAndOrganizationsPage.openOptionsMenu();

		await usersAndOrganizationsPage.manageCustomFieldsOptionsMenuItem.click();

		await page.getByRole('link', {name: 'Add Custom Field'}).click();

		const dropdownOptionButton = page.getByRole('link', {
			name: 'Dropdown Option',
		});

		await dropdownOptionButton.waitFor({state: 'visible'});
		await dropdownOptionButton.click();

		const customFieldLabel = page.getByLabel('Field Name Required');

		await customFieldLabel.waitFor({state: 'visible'});
		await customFieldLabel.click();
		await customFieldLabel.fill('fieldTest');

		const customFieldValue = page.getByLabel('Values Required');

		await customFieldValue.waitFor({state: 'visible'});
		await customFieldValue.click();
		await customFieldValue.fill('a & b');

		const saveButton = page.getByRole('button', {
			name: 'Save',
		});

		await saveButton.waitFor({state: 'visible'});
		await saveButton.click();

		await expect(
			page.getByText('Success:Your request completed successfully.')
		).toBeVisible();

		await usersAndOrganizationsPage.goToUsers();
		await (
			await usersAndOrganizationsPage.usersTableRowLink('test')
		).click();

		const customFieldDropDownLabel = page.getByLabel('Fieldtest', {
			exact: true,
		});

		await customFieldDropDownLabel.waitFor({state: 'visible'});

		const customFieldDropDownOptions = await page.evaluate(() => {
			const selection = document.querySelector('[title="field-test"]');

			// @ts-ignore

			return [...selection.options].some(
				(option) => option.text === 'a & b'
			);
		});

		expect(customFieldDropDownOptions).toBeTruthy();
	}
);

test(
	'Check toolbar select is not visible at all',
	{tag: '@LPD-41022'},
	async ({usersAndOrganizationsPage}) => {
		await usersAndOrganizationsPage.goToUsers();

		await expect(
			usersAndOrganizationsPage.selectAllUsersCheckBox
		).toBeVisible();

		await usersAndOrganizationsPage.filterUsers('all');

		await expect(
			usersAndOrganizationsPage.selectAllUsersCheckBox
		).not.toBeVisible();
	}
);

test(
	'Can Bulk Activate Users',
	{tag: '@LPD-42940'},
	async ({apiHelpers, page, usersAndOrganizationsPage}) => {
		page.on('dialog', async (dialog) => await dialog.accept());

		const user1 = await apiHelpers.headlessAdminUser.postUserAccount();
		const user2 = await apiHelpers.headlessAdminUser.postUserAccount();
		const user3 = await apiHelpers.headlessAdminUser.postUserAccount();
		const user4 = await apiHelpers.headlessAdminUser.postUserAccount();
		const user5 = await apiHelpers.headlessAdminUser.postUserAccount();

		await usersAndOrganizationsPage.goToUsers();

		const userNames: string[] = [user1.name, user2.name, user3.name];

		await usersAndOrganizationsPage.deActivateUsers(userNames);

		await usersAndOrganizationsPage.filterUsers('inactive');

		for (const userName of userNames) {
			await expect(
				(
					await usersAndOrganizationsPage.usersTableRow(
						1,
						userName,
						true
					)
				).row
			).toBeVisible();
		}

		await usersAndOrganizationsPage.activateUsers(userNames);

		for (const userName of userNames) {
			await usersAndOrganizationsPage.usersSearchBar.fill(userName);
			await usersAndOrganizationsPage.usersSearchBarButton.click();
			await page.waitForLoadState('domcontentloaded');
			await expect(
				usersAndOrganizationsPage.noUsersMessage
			).toBeVisible();
		}

		await usersAndOrganizationsPage.clearButton.click();
		await expect(usersAndOrganizationsPage.clearButton).not.toBeVisible();

		userNames.push(user4.name, user5.name);

		for (const userName of userNames) {
			await expect(
				(
					await usersAndOrganizationsPage.usersTableRow(
						1,
						userName,
						true
					)
				).row
			).toBeVisible();
		}
	}
);

test(
	'Organization Administrator can activate and deactivate users',
	{tag: '@LPD-35634'},
	async ({apiHelpers, page, usersAndOrganizationsPage}) => {
		page.on('dialog', (dialog) => dialog.accept());

		const organization =
			await apiHelpers.headlessAdminUser.postOrganization();

		const user = await apiHelpers.headlessAdminUser.postUserAccount();

		userData[user.alternateName] = {
			name: user.givenName,
			password: 'test',
			surname: user.familyName,
		};

		await apiHelpers.headlessAdminUser.assignUserToOrganizationByEmailAddress(
			organization.id,
			user.emailAddress
		);

		apiHelpers.data.push({
			id: `${organization.id}_${user.emailAddress}`,
			type: 'organizationUserAccountAssociation',
		});

		const organizationAdministratorRole =
			await apiHelpers.headlessAdminUser.getRoleByName(
				'Organization Administrator'
			);

		await apiHelpers.headlessAdminUser.assignUserToOrganizationRole(
			organizationAdministratorRole.id.toString(),
			user.id,
			organization.id
		);

		const user2 = await apiHelpers.headlessAdminUser.postUserAccount();

		await apiHelpers.headlessAdminUser.assignUserToOrganizationByEmailAddress(
			organization.id,
			user2.emailAddress
		);

		apiHelpers.data.push({
			id: `${organization.id}_${user2.emailAddress}`,
			type: 'organizationUserAccountAssociation',
		});

		await performLogout(page);
		await performLogin(page, user.alternateName);

		await usersAndOrganizationsPage.goToOrganizationsWithLimitedAccess();

		await usersAndOrganizationsPage.organizationsTable
			.valueLink(organization.name)
			.click();
		await (
			await usersAndOrganizationsPage.organizationUsersTableRowActions(
				`${user2.name}`
			)
		).click();

		await usersAndOrganizationsPage.deactivateUserMenuItem.click();

		await waitForAlert(page);

		await expect(
			await usersAndOrganizationsPage.organizationUsersTableRowStatusLink(
				user2.name,
				'Inactive'
			)
		).toBeVisible();

		await (
			await usersAndOrganizationsPage.organizationUsersTableRowActions(
				user2.name
			)
		).click();

		await usersAndOrganizationsPage.activateUserMenuItem.click();

		await waitForAlert(page);

		await expect(
			await usersAndOrganizationsPage.organizationUsersTableRowStatusLink(
				user2.name,
				'Active'
			)
		).toBeVisible();
	}
);

test(
	'Bulk delete users succeed',
	{tag: '@LPD-47050'},
	async ({apiHelpers, page, usersAndOrganizationsPage}) => {
		page.on('dialog', async (dialog) => await dialog.accept());

		const user1 = await apiHelpers.headlessAdminUser.postUserAccount();
		const user2 = await apiHelpers.headlessAdminUser.postUserAccount();
		const user3 = await apiHelpers.headlessAdminUser.postUserAccount();
		const user4 = await apiHelpers.headlessAdminUser.postUserAccount();
		const user5 = await apiHelpers.headlessAdminUser.postUserAccount();

		await usersAndOrganizationsPage.goToUsers();

		const userNames: string[] = [user1.name, user2.name, user3.name];

		await usersAndOrganizationsPage.deActivateUsers(userNames);

		await usersAndOrganizationsPage.filterUsers('inactive');

		for (const userName of userNames) {
			await expect(
				usersAndOrganizationsPage.usersTableCell(userName)
			).toBeVisible();
		}

		await usersAndOrganizationsPage.deleteUsers(userNames);

		for (const userName of userNames) {
			await expect(
				usersAndOrganizationsPage.usersTableCell(userName)
			).not.toBeVisible();
		}

		await usersAndOrganizationsPage.goToUsers();

		for (const userName of userNames) {
			await expect(
				usersAndOrganizationsPage.usersTableCell(userName)
			).not.toBeVisible();
		}

		for (const userName of [user4.name, user5.name]) {
			await expect(
				usersAndOrganizationsPage.usersTableCell(userName)
			).toBeVisible();
		}
	}
);

test(
	'User organizations list contains no duplicate',
	{tag: '@LPD-48741'},
	async ({apiHelpers, editUserPage, usersAndOrganizationsPage}) => {
		const parentOrganization =
			await apiHelpers.headlessAdminUser.postOrganization();
		const organization1 =
			await apiHelpers.headlessAdminUser.postOrganization({
				parentOrganization: {
					externalReferenceCode:
						parentOrganization.externalReferenceCode,
				},
			});
		const organization2 =
			await apiHelpers.headlessAdminUser.postOrganization({
				parentOrganization: {
					externalReferenceCode:
						parentOrganization.externalReferenceCode,
				},
			});

		await apiHelpers.headlessAdminUser.assignUserToOrganizationByEmailAddress(
			organization1.id,
			'test@liferay.com'
		);

		apiHelpers.data.push({
			id: `${organization1.id}_test@liferay.com`,
			type: 'organizationUserAccountAssociation',
		});

		await apiHelpers.headlessAdminUser.assignUserToOrganizationByEmailAddress(
			organization2.id,
			'test@liferay.com'
		);

		apiHelpers.data.push({
			id: `${organization2.id}_test@liferay.com`,
			type: 'organizationUserAccountAssociation',
		});

		await usersAndOrganizationsPage.goToUsers();

		await (
			await usersAndOrganizationsPage.usersTableRowLink('test')
		).click();

		await editUserPage.organizationsLink.click();

		await expect(
			editUserPage.organizationsTable.getByText(
				`${parentOrganization.name}`
			)
		).toHaveCount(1);
	}
);

test(
	'Bulk deactivate user succeed',
	{tag: '@LPD-48841'},
	async ({apiHelpers, page, usersAndOrganizationsPage}) => {
		page.on('dialog', async (dialog) => await dialog.accept());

		const user1 = await apiHelpers.headlessAdminUser.postUserAccount();
		const user2 = await apiHelpers.headlessAdminUser.postUserAccount();
		const user3 = await apiHelpers.headlessAdminUser.postUserAccount();
		const user4 = await apiHelpers.headlessAdminUser.postUserAccount();
		const user5 = await apiHelpers.headlessAdminUser.postUserAccount();

		await usersAndOrganizationsPage.goToUsers();

		const userNames: string[] = [user1.name, user2.name, user3.name];

		await usersAndOrganizationsPage.deActivateUsers(userNames);

		for (const userName of userNames) {
			await expect(
				usersAndOrganizationsPage.usersTableCell(userName)
			).not.toBeVisible();
		}

		for (const userName of [user4.name, user5.name]) {
			await expect(
				usersAndOrganizationsPage.usersTableCell(userName)
			).toBeVisible();
		}

		await usersAndOrganizationsPage.filterUsers('inactive');

		for (const userName of userNames) {
			await expect(
				usersAndOrganizationsPage.usersTableCell(userName)
			).toBeVisible();
		}
	}
);

test(
	'Can share document with site member user group user',
	{tag: '@LPD-48841'},
	async ({
		apiHelpers,
		notificationsPage,
		page,
		siteMembershipsPage,
		userDocumentLibraryPage,
	}) => {
		const userAccount1 =
			await apiHelpers.headlessAdminUser.postUserAccount();

		userData[userAccount1.alternateName] = {
			name: userAccount1.givenName,
			password: 'test',
			surname: userAccount1.familyName,
		};

		const userAccount2 =
			await apiHelpers.headlessAdminUser.postUserAccount();

		userData[userAccount2.alternateName] = {
			name: userAccount2.givenName,
			password: 'test',
			surname: userAccount2.familyName,
		};

		const userGroup = await apiHelpers.headlessAdminUser.postUserGroup();

		await apiHelpers.headlessAdminUser.assignUsersToUserGroup(
			userGroup.id,
			[userAccount1.id, userAccount2.id]
		);

		const site = await apiHelpers.headlessSite.createSite({
			name: getRandomString(),
		});

		apiHelpers.data.push({id: site.id, type: 'site'});

		const role =
			await apiHelpers.headlessAdminUser.getRoleByName(
				'Site Administrator'
			);

		await apiHelpers.headlessAdminUser.assignUserToSite(
			role.id,
			site.id,
			userAccount1.id
		);

		await siteMembershipsPage.goto(site.friendlyUrlPath);
		await siteMembershipsPage.userGroupsLink.click();
		await siteMembershipsPage.newUserGroupButton.click();

		await expect(
			siteMembershipsPage.assignUserGroupIFrameTitle
		).toBeVisible();

		await siteMembershipsPage.assignUserGroupTable.changeView('Table');

		await expect(
			siteMembershipsPage.assignUserGroupTable.cell(userGroup.name)
		).toBeVisible();

		await (
			await siteMembershipsPage.assignUserGroupTable.rowCheckbox(
				userGroup.name
			)
		).check();
		await siteMembershipsPage.userGroupSelectDoneButton.click();

		await waitForAlert(page);

		await performLogout(page);
		await performLoginViaApi({
			page,
			screenName: userAccount1.alternateName,
		});

		const document = await apiHelpers.headlessDelivery.postDocument(
			site.id,
			createReadStream(
				path.join(__dirname, '/dependencies/attachment.txt')
			),
			{title: 'Attachment'}
		);

		await userDocumentLibraryPage.goto(site.friendlyUrlPath);

		await userDocumentLibraryPage.documentsTable.changeView('Table');

		await expect(
			userDocumentLibraryPage.documentsTable.cell('Attachment')
		).toBeVisible();

		await (
			await userDocumentLibraryPage.documentsTable.rowActions(
				document.title
			)
		).click();
		await userDocumentLibraryPage.shareDocumentLink.click();

		await userDocumentLibraryPage.sharingDialogueInput.fill(
			userAccount2.emailAddress
		);
		await page.keyboard.press('Enter');
		await userDocumentLibraryPage.sharingDialogueShareButton.click();

		await waitForAlert(page, 'Success:The item was shared successfully.');

		await performLogout(page);
		await performLoginViaApi({
			page,
			screenName: userAccount2.alternateName,
		});

		await notificationsPage.goto(userAccount2.name);

		await expect(
			notificationsPage.sharingNotificationMessage(
				userAccount1.name,
				document.title
			)
		).toBeVisible();
	}
);

test(
	'Impersonating Administrator and Owner',
	{tag: '@50219'},
	async ({apiHelpers, context, page, usersAndOrganizationsPage}) => {
		const userAccount1 =
			await apiHelpers.headlessAdminUser.postUserAccount();

		userData[userAccount1.alternateName] = {
			name: userAccount1.givenName,
			password: 'test',
			surname: userAccount1.familyName,
		};

		const userAccount2 =
			await apiHelpers.headlessAdminUser.postUserAccount();

		const userAccount3 =
			await apiHelpers.headlessAdminUser.postUserAccount();

		userData[userAccount3.alternateName] = {
			name: userAccount3.givenName,
			password: 'test',
			surname: userAccount3.familyName,
		};

		const companyId = await page.evaluate(() => {
			return Liferay.ThemeDisplay.getCompanyId();
		});

		const role = await apiHelpers.headlessAdminUser.postRole({
			name: getRandomString(),
			rolePermissions: [
				{
					actionIds: ['VIEW_CONTROL_PANEL'],
					primaryKey: companyId,
					resourceName: '90',
					scope: 1,
				},
				{
					actionIds: ['VIEW'],
					primaryKey: companyId,
					resourceName: 'com.liferay.portal.kernel.model.User',
					scope: 1,
				},
				{
					actionIds: ['ACCESS_IN_CONTROL_PANEL', 'VIEW'],
					primaryKey: companyId,
					resourceName:
						'com_liferay_users_admin_web_portlet_UsersAdminPortlet',
					scope: 1,
				},
			],
			roleType: 'regular',
		});

		await apiHelpers.headlessAdminUser.postRoleByExternalReferenceCodeUserAccountAssociation(
			role.externalReferenceCode,
			userAccount1.id
		);

		const role2 = await apiHelpers.headlessAdminUser.postRole({
			name: getRandomString(),
			rolePermissions: [
				{
					actionIds: ['IMPERSONATE'],
					primaryKey: companyId,
					resourceName: 'com.liferay.portal.kernel.model.User',
					scope: 1,
				},
			],
			roleType: 'regular',
		});

		const administratorRole =
			await apiHelpers.headlessAdminUser.getRoleByName('Administrator');

		await apiHelpers.headlessAdminUser.postRoleByExternalReferenceCodeUserAccountAssociation(
			administratorRole.externalReferenceCode,
			userAccount3.id
		);

		await performLogout(page);
		await performLoginViaApi({
			page,
			screenName: userAccount1.alternateName,
		});

		await usersAndOrganizationsPage.goToUsersWithLimitedAccess();

		await expect(
			await usersAndOrganizationsPage.usersTableRowActions(
				userAccount2.alternateName
			)
		).not.toBeVisible();
		await expect(
			await usersAndOrganizationsPage.usersTableRowActions(
				userAccount3.alternateName
			)
		).not.toBeVisible();

		await performLogout(page);
		await performLoginViaApi({page, screenName: 'test'});

		await apiHelpers.headlessAdminUser.postRoleByExternalReferenceCodeUserAccountAssociation(
			role2.externalReferenceCode,
			userAccount1.id
		);

		await performLogout(page);
		await performLoginViaApi({
			page,
			screenName: userAccount1.alternateName,
		});

		await usersAndOrganizationsPage.goToUsersWithLimitedAccess();

		await expect(
			await usersAndOrganizationsPage.usersTableRowActions(
				userAccount2.alternateName
			)
		).toBeVisible();
		await expect(
			await usersAndOrganizationsPage.usersTableRowActions(
				userAccount3.alternateName
			)
		).not.toBeVisible();

		const pagePromise = context.waitForEvent('page');

		await (
			await usersAndOrganizationsPage.usersTableRowActions(
				userAccount2.alternateName
			)
		).click();
		await usersAndOrganizationsPage.impersonateUserMenuItem.click();

		const newPage = await pagePromise;

		await expect(newPage.getByTitle('User Profile Menu')).toBeVisible();

		await newPage.close();

		await performLogout(page);
		await performLoginViaApi({
			page,
			screenName: userAccount3.alternateName,
		});

		await usersAndOrganizationsPage.goToUsers();

		await (
			await usersAndOrganizationsPage.usersTableRowActions('test')
		).click();

		await expect(
			usersAndOrganizationsPage.impersonateUserMenuItem
		).toBeVisible();

		await usersAndOrganizationsPage.usersSearchBar.click();

		await (
			await usersAndOrganizationsPage.usersTableRowActions(
				userAccount1.alternateName
			)
		).click();

		await expect(
			usersAndOrganizationsPage.impersonateUserMenuItem
		).toBeVisible();

		await usersAndOrganizationsPage.usersSearchBar.click();

		await (
			await usersAndOrganizationsPage.usersTableRowActions(
				userAccount2.alternateName
			)
		).click();

		await expect(
			usersAndOrganizationsPage.impersonateUserMenuItem
		).toBeVisible();
	}
);

test(
	'Can edit organization site team',
	{tag: '@LPD-50685'},
	async ({
		apiHelpers,
		editOrganizationPage,
		page,
		siteConfigurationDetailsPage,
		siteSettingsPage,
		teamsPage,
		usersAndOrganizationsPage,
	}) => {
		const organization =
			await apiHelpers.headlessAdminUser.postOrganization();

		await usersAndOrganizationsPage.goToOrganizations();

		await (
			await usersAndOrganizationsPage.organizationsTable.rowActions(
				organization.name
			)
		).click();
		await editOrganizationPage.organizationEditMenuItem.click();
		await editOrganizationPage.organizationSiteLink.click();
		await editOrganizationPage.createSiteToggle.check();
		await editOrganizationPage.organizationSiteSaveButton.click();

		await waitForAlert(page);

		const siteUrl = `/${organization.name}`;

		await siteSettingsPage.goToSiteSetting(
			'Site Configuration',
			null,
			siteUrl
		);

		await siteConfigurationDetailsPage.allowManualMembershipManagementToggle.check();
		await siteConfigurationDetailsPage.saveButton.click();

		await waitForAlert(page);

		await teamsPage.goTo(siteUrl);

		const newTeamName = getRandomString();

		await teamsPage.newTeamButton.click();
		await teamsPage.nameInput.fill(newTeamName);
		await teamsPage.descriptionInput.fill(getRandomString());
		await teamsPage.saveButton.click();

		await waitForAlert(page);

		await (
			await teamsPage.teamsTable.rowActions(newTeamName, 1, true)
		).click();

		await teamsPage.editButton.click();

		const editedName = getRandomString();
		const editedDescription = getRandomString();

		await teamsPage.nameInput.fill(editedName);
		await teamsPage.descriptionInput.fill(editedDescription);
		await teamsPage.saveButton.click();

		await waitForAlert(page);

		await expect(teamsPage.teamsTable.cell(editedName, true)).toBeVisible();
		await expect(
			teamsPage.teamsTable.cell(editedDescription, true)
		).toBeVisible();
		await expect(
			teamsPage.teamsTable.cell(newTeamName, true)
		).not.toBeVisible();
	}
);

test(
	'Only global tag can be associated to user',
	{tag: ['@LPD-50770', '@LPS-111656']},
	async ({
		apiHelpers,
		editUserPage,
		page,
		tagsEditPage,
		usersAndOrganizationsPage,
	}) => {
		const tags = [
			{name: getRandomString(), siteUrl: '/global'},
			{name: getRandomString(), siteUrl: '/global'},
			{name: getRandomString(), siteUrl: '/guest'},
		];

		for (const {name, siteUrl} of tags) {
			await tagsEditPage.add(name, siteUrl);
		}

		const userAccount =
			await apiHelpers.headlessAdminUser.postUserAccount();

		await usersAndOrganizationsPage.goToUsers();
		await (
			await usersAndOrganizationsPage.usersTableRowLink(
				userAccount.alternateName
			)
		).click();

		await editUserPage.selectTagsButton.click();
		await editUserPage.selectTag([tags[0].name, tags[1].name]);

		await expect(editUserPage.tagInput(tags[0].name)).toBeVisible();
		await expect(editUserPage.tagInput(tags[1].name)).toBeVisible();
		await expect(editUserPage.tagInput(tags[2].name)).toHaveCount(0);

		await editUserPage.saveButton.click();

		await waitForAlert(page);

		await editUserPage.membershipsLink.click();
		await editUserPage.saveButton.click();

		await waitForAlert(page);

		await usersAndOrganizationsPage.goToUsers();
		await (
			await usersAndOrganizationsPage.usersTableRowLink(
				userAccount.alternateName
			)
		).click();

		await expect(editUserPage.tagInput(tags[0].name)).toBeVisible();
		await expect(editUserPage.tagInput(tags[1].name)).toBeVisible();
	}
);

test(
	'Can search users in organizations',
	{tag: '@LPD-50958'},
	async ({apiHelpers, usersAndOrganizationsPage}) => {
		const organization =
			await apiHelpers.headlessAdminUser.postOrganization();

		const userAccount =
			await apiHelpers.headlessAdminUser.postUserAccount();

		await apiHelpers.headlessAdminUser.assignUserToOrganizationByEmailAddress(
			organization.id,
			userAccount.emailAddress
		);

		apiHelpers.data.push({
			id: `${organization.id}_${userAccount.emailAddress}`,
			type: 'organizationUserAccountAssociation',
		});

		await usersAndOrganizationsPage.goToOrganizations();
		await usersAndOrganizationsPage.organizationsTable
			.valueLink(organization.name)
			.click();

		await expect(
			usersAndOrganizationsPage.organizationUsersTable
		).toBeVisible();

		await usersAndOrganizationsPage.usersSearchBar.fill(userAccount.name);
		await usersAndOrganizationsPage.usersSearchBarButton.click();

		await expect(
			(
				await usersAndOrganizationsPage.organizationUsersTableRow(
					1,
					userAccount.name,
					true
				)
			).row
		).toBeVisible();

		await usersAndOrganizationsPage.usersSearchBar.fill('test');
		await usersAndOrganizationsPage.usersSearchBarButton.click();

		await expect(usersAndOrganizationsPage.noResultsMessage).toBeVisible();
	}
);

test(
	'Can assign multiple users to an organization',
	{tag: '@LPD-50958'},
	async ({apiHelpers, page, usersAndOrganizationsPage}) => {
		const userAccounts: TUserAccount[] = [];

		for (let i = 0; i < 5; i++) {
			const user = await apiHelpers.headlessAdminUser.postUserAccount();
			userAccounts.push(user);
		}

		const organization =
			await apiHelpers.headlessAdminUser.postOrganization();

		await usersAndOrganizationsPage.goToOrganizations();

		await (
			await usersAndOrganizationsPage.organizationsTable.rowActions(
				organization.name
			)
		).click();
		await usersAndOrganizationsPage.assignUsersMenuItem.click();

		for (const user of userAccounts) {
			await (
				await usersAndOrganizationsPage.assignUsersCheckbox(user.name)
			).check();
		}

		await usersAndOrganizationsPage.assignUsersDoneButton.click();

		await waitForAlert(page);

		for (const user of userAccounts) {
			apiHelpers.data.push({
				id: `${organization.id}_${user.emailAddress}`,
				type: 'organizationUserAccountAssociation',
			});

			await expect(
				(
					await usersAndOrganizationsPage.organizationUsersTableRow(
						1,
						user.name,
						true
					)
				).row
			).toBeVisible();
		}
	}
);

test(
	'Can change user password',
	{tag: '@LPD-50958'},
	async ({accountSettingsPage, apiHelpers, page}) => {
		const user = await apiHelpers.headlessAdminUser.postUserAccount();

		userData[user.alternateName] = {
			name: user.givenName,
			password: 'test',
			surname: user.familyName,
		};

		await performLogout(page);
		await performLoginViaApi({page, screenName: user.alternateName});

		await accountSettingsPage.goToAccountSettings();
		await accountSettingsPage.passwordMenuItem.click();
		await accountSettingsPage.currentPasswordInput.fill('test');

		const newPassword = getRandomString();

		await accountSettingsPage.newPasswordInput.fill(newPassword);
		await accountSettingsPage.reenterPasswordInput.fill(newPassword);
		await accountSettingsPage.saveButton.click();

		await waitForAlert(page);

		userData[user.alternateName] = {
			name: user.givenName,
			password: newPassword,
			surname: user.familyName,
		};

		await performLogout(page);
		await performLoginViaApi({page, screenName: user.alternateName});

		await expect(accountSettingsPage.userPersonalMenuButton).toBeVisible();
	}
);

test(
	'Change user password invalid',
	{tag: '@LPD-50958'},
	async ({accountSettingsPage, apiHelpers, page}) => {
		const user = await apiHelpers.headlessAdminUser.postUserAccount();

		userData[user.alternateName] = {
			name: user.givenName,
			password: 'test',
			surname: user.familyName,
		};

		await performLogout(page);
		await performLoginViaApi({page, screenName: user.alternateName});

		await accountSettingsPage.goToAccountSettings();
		await accountSettingsPage.passwordMenuItem.click();

		await accountSettingsPage.newPasswordInput.fill('password');
		await accountSettingsPage.reenterPasswordInput.fill('password');
		await accountSettingsPage.saveButton.click();

		await expect(
			accountSettingsPage.passwordErrorMessage(
				'The Current Password field is required.'
			)
		).toBeVisible();

		await accountSettingsPage.currentPasswordInput.fill(getRandomString());
		await accountSettingsPage.saveButton.click();

		await expect(
			accountSettingsPage.passwordErrorMessage(
				'Error:The password you entered for the current password does not match your current password. Please try again.'
			)
		).toBeVisible();

		await performLogout(page);
		await performLoginViaApi({page, screenName: user.alternateName});

		await expect(accountSettingsPage.userPersonalMenuButton).toBeVisible();
	}
);

test(
	'Max fileSize limit is used uploading the user profile picture',
	{tag: '@LPD-52424'},
	async ({editUserPage, page, usersAndOrganizationsPage}) => {
		await usersAndOrganizationsPage.goToUsers();

		await (
			await usersAndOrganizationsPage.usersTableRowLink('test')
		).click();

		const fileChooserPromise = page.waitForEvent('filechooser');

		await editUserPage.changeImageButton.click();

		await expect(editUserPage.maxFileSizeText).toBeVisible();

		await editUserPage.uploadImageSelectImageButton.click();

		const fileChooser = await fileChooserPromise;

		await fileChooser.setFiles(
			path.join(__dirname, '/dependencies/liferay.png')
		);
		await editUserPage.uploadImageDoneButton.click();
		await editUserPage.changeImageButton.click();

		await expect(editUserPage.maxFileSizeText).toBeVisible();
	}
);

test(
	'Can search an organization and view its status',
	{tag: ['@LPD-55108']},
	async ({apiHelpers, usersAndOrganizationsPage}) => {
		const organization =
			await apiHelpers.headlessAdminUser.postOrganization();

		await usersAndOrganizationsPage.goToOrganizations();

		await usersAndOrganizationsPage.organizationsTable.changeView('Table');

		await usersAndOrganizationsPage.organizationsTable.search(
			organization.name
		);

		await expect(
			usersAndOrganizationsPage.organizationsTable.cell(organization.name)
		).toHaveCount(1);
		await expect(
			usersAndOrganizationsPage.organizationsTable.cell('Approved')
		).toBeVisible();

		await usersAndOrganizationsPage.organizationsTable.changeView('List');

		await expect(
			usersAndOrganizationsPage.organizationsTable.valueLink(
				organization.name
			)
		).toHaveCount(1);
		await expect(
			usersAndOrganizationsPage.statusText('Approved')
		).toBeVisible();

		await usersAndOrganizationsPage.organizationsTable.changeView('Cards');

		await expect(
			usersAndOrganizationsPage.organizationsTable.valueLink(
				organization.name
			)
		).toHaveCount(1);
		await expect(
			usersAndOrganizationsPage.statusText('Approved')
		).toBeVisible();
	}
);

test(
	'Organization Administrator can remove member from organization from User menu as well',
	{tag: ['@LPD-61092']},
	async ({apiHelpers, editUserPage, page, usersAndOrganizationsPage}) => {
		const organization =
			await apiHelpers.headlessAdminUser.postOrganization();

		const organizationAdministratorUser =
			await apiHelpers.headlessAdminUser.postUserAccount();

		userData[organizationAdministratorUser.alternateName] = {
			name: organizationAdministratorUser.givenName,
			password: 'test',
			surname: organizationAdministratorUser.familyName,
		};

		await apiHelpers.headlessAdminUser.assignUserToOrganizationByEmailAddress(
			organization.id,
			organizationAdministratorUser.emailAddress
		);

		const orgRole = await apiHelpers.headlessAdminUser.getRoleByName(
			'Organization Administrator'
		);

		await apiHelpers.headlessAdminUser.assignUserToOrganizationRole(
			orgRole.id,
			organizationAdministratorUser.id,
			organization.id
		);

		const companyId = await page.evaluate(() => {
			return Liferay.ThemeDisplay.getCompanyId();
		});

		const regularRole = await apiHelpers.headlessAdminUser.postRole({
			name: getRandomString(),
			rolePermissions: [
				{
					actionIds: ['ADD_USER'],
					primaryKey: companyId,
					resourceName: '90',
					scope: 1,
				},
			],
		});

		await apiHelpers.headlessAdminUser.assignUserToRole(
			regularRole.externalReferenceCode,
			organizationAdministratorUser.id
		);

		const memberUser = await apiHelpers.headlessAdminUser.postUserAccount();

		await apiHelpers.headlessAdminUser.assignUserToOrganizationByEmailAddress(
			organization.id,
			memberUser.emailAddress
		);

		await performLogout(page);
		await performLoginViaApi({
			page,
			screenName: organizationAdministratorUser.alternateName,
		});

		await usersAndOrganizationsPage.goToUsersWithLimitedAccess();
		await usersAndOrganizationsPage.goToUser(memberUser.alternateName);

		await expect(editUserPage.changeImageButton).toBeVisible();

		await editUserPage.organizationsLink.click();

		await expect(
			editUserPage.organizationsTable.getByText(organization.name)
		).toHaveCount(1);

		await Promise.all([
			editUserPage
				.organizationsTableRemoveButton(organization.name)
				.click(),
			page.waitForResponse((response: any) => response.status() === 200),
		]);

		await expect(usersAndOrganizationsPage.usersTable).toBeVisible();
	}
);
