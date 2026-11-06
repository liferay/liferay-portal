/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {dataApiHelpersTest} from '../../../fixtures/dataApiHelpersTest';
import {isolatedSiteTest} from '../../../fixtures/isolatedSiteTest';
import {loginTest} from '../../../fixtures/loginTest';
import {rolesPagesTest} from '../../../fixtures/rolesPagesTest';
import {userGroupsPageTest} from '../../../fixtures/userGroupsPageTest';
import {usersAndOrganizationsPagesTest} from '../../../fixtures/usersAndOrganizationsPagesTest';
import {clickAndExpectToBeVisible} from '../../../utils/clickAndExpectToBeVisible';
import getRandomString from '../../../utils/getRandomString';
import {performUserSwitch, userData} from '../../../utils/performLogin';
import {waitForAlert} from '../../../utils/waitForAlert';

export const test = mergeTests(
	dataApiHelpersTest,
	isolatedSiteTest,
	loginTest(),
	rolesPagesTest,
	userGroupsPageTest,
	usersAndOrganizationsPagesTest
);

test(
	'Can assign role to users of different sites',
	{tag: '@LPD-55670'},
	async ({
		apiHelpers,
		blogsPage,
		page,
		rolesPage,
		site,
		usersAndOrganizationsPage,
	}) => {
		const organization =
			await apiHelpers.headlessAdminUser.postOrganization();

		const user1 = await apiHelpers.headlessAdminUser.postUserAccount();

		userData[user1.alternateName] = {
			name: user1.givenName,
			password: 'test',
			surname: user1.familyName,
		};

		const user2 = await apiHelpers.headlessAdminUser.postUserAccount();

		userData[user2.alternateName] = {
			name: user2.givenName,
			password: 'test',
			surname: user2.familyName,
		};

		const user3 = await apiHelpers.headlessAdminUser.postUserAccount();

		userData[user3.alternateName] = {
			name: user3.givenName,
			password: 'test',
			surname: user3.familyName,
		};

		const userGroup = await apiHelpers.headlessAdminUser.postUserGroup();

		await apiHelpers.headlessAdminUser.assignUsersToUserGroup(
			userGroup.id,
			[user1.id, user2.id, user3.id]
		);

		await apiHelpers.jsonWebServicesUser.assignUsersToSite(
			site.id,
			user1.id
		);
		await apiHelpers.jsonWebServicesUser.assignUsersToSite(
			site.id,
			user3.id
		);

		await apiHelpers.headlessAdminUser.assignUserToOrganizationByEmailAddress(
			organization.id,
			user2.emailAddress
		);
		await apiHelpers.headlessAdminUser.assignUserToOrganizationByEmailAddress(
			organization.id,
			user3.emailAddress
		);

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
					actionIds: ['ACCESS_IN_CONTROL_PANEL', 'VIEW'],
					primaryKey: companyId,
					resourceName:
						'com_liferay_users_admin_web_portlet_UsersAdminPortlet',
					scope: 1,
				},
				{
					actionIds: ['ACCESS_IN_CONTROL_PANEL', 'VIEW'],
					primaryKey: companyId,
					resourceName:
						'com_liferay_blogs_web_portlet_BlogsAdminPortlet',
					scope: 1,
				},
				{
					actionIds: ['ADD_ENTRY'],
					primaryKey: companyId,
					resourceName: 'com.liferay.blogs',
					scope: 1,
				},
				{
					actionIds: ['VIEW'],
					primaryKey: companyId,
					resourceName: 'com.liferay.blogs.model.BlogsEntry',
					scope: 1,
				},
			],
			roleType: 'regular',
		});

		await expect(async () => {
			await rolesPage.goto();
			await rolesPage.assignRoleToUserGroup(role.name, userGroup.name);
		}).toPass();

		await performUserSwitch(page, user1.alternateName);

		const blog = await apiHelpers.headlessDelivery.postBlog(site.id);

		await usersAndOrganizationsPage.goToOrganizationsWithLimitedAccess();

		await expect(
			usersAndOrganizationsPage.noPermissionMessage
		).toBeVisible();

		await performUserSwitch(page, user2.alternateName);

		await usersAndOrganizationsPage.goToOrganizationsWithLimitedAccess();

		await expect(
			usersAndOrganizationsPage.organizationsTable.cell(organization.name)
		).toBeVisible();
		await expect(
			await usersAndOrganizationsPage.organizationsTable.rowActions(
				organization.name
			)
		).not.toBeVisible();

		await blogsPage.goto(site.friendlyUrlPath);

		await expect(blogsPage.blogTitle(blog.headline)).toBeVisible();

		await performUserSwitch(page, user3.alternateName);

		await usersAndOrganizationsPage.goToOrganizationsWithLimitedAccess();

		await expect(
			usersAndOrganizationsPage.organizationsTable.cell(organization.name)
		).toBeVisible();
		await expect(
			await usersAndOrganizationsPage.organizationsTable.rowActions(
				organization.name
			)
		).not.toBeVisible();

		await blogsPage.goto(site.friendlyUrlPath);

		await expect(blogsPage.blogTitle(blog.headline)).toBeVisible();
	}
);

test(
	'Assign Permissions to user group pages',
	{tag: '@LPD-56222'},
	async ({apiHelpers, page, rolesPage, userGroupsPage}) => {
		const user = await apiHelpers.headlessAdminUser.postUserAccount();

		userData[user.alternateName] = {
			name: user.givenName,
			password: 'test',
			surname: user.familyName,
		};

		const userGroup = await apiHelpers.headlessAdminUser.postUserGroup();

		await apiHelpers.headlessAdminUser.assignUsersToUserGroup(
			userGroup.id,
			[user.id]
		);

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
					actionIds: ['ACCESS_IN_CONTROL_PANEL', 'VIEW'],
					primaryKey: companyId,
					resourceName:
						'com_liferay_user_groups_admin_web_portlet_UserGroupsAdminPortlet',
					scope: 1,
				},
				{
					actionIds: ['VIEW'],
					primaryKey: companyId,
					resourceName: 'com.liferay.portal.kernel.model.UserGroup',
					scope: 1,
				},
			],
			roleType: 'regular',
		});

		await rolesPage.goto();
		await rolesPage.assignRoleToUserGroup(role.name, userGroup.name);

		await performUserSwitch(page, user.alternateName);

		await userGroupsPage.goToWithLimitedAccess();

		await expect(
			await userGroupsPage.userGroupsTableRowActions(userGroup.name)
		).not.toBeVisible();

		await performUserSwitch(page, 'test');

		const group = await apiHelpers.jsonWebServicesGroup.getGroupByKey(
			companyId,
			String(userGroup.id)
		);

		const guestGroup = await apiHelpers.jsonWebServicesGroup.getGroupByKey(
			companyId,
			'Guest'
		);

		await apiHelpers.jsonWebServicesResourcePermissionApiHelper.setIndividualResourcePermissions(
			['MANAGE_LAYOUTS'],
			companyId,
			guestGroup.groupId,
			'com.liferay.portal.kernel.model.Group',
			String(group.groupId),
			String(role.id)
		);

		await performUserSwitch(page, user.alternateName);

		await userGroupsPage.goToWithLimitedAccess();

		await clickAndExpectToBeVisible({
			target: userGroupsPage.managePagesMenuItem,
			trigger: await userGroupsPage.userGroupsTableRowActions(
				userGroup.name
			),
		});
	}
);

test(
	'User group regular role permissions',
	{tag: '@LPD-56222'},
	async ({apiHelpers, blogsPage, page, rolesPage}) => {
		const user1 = await apiHelpers.headlessAdminUser.postUserAccount();

		userData[user1.alternateName] = {
			name: user1.givenName,
			password: 'test',
			surname: user1.familyName,
		};

		const user2 = await apiHelpers.headlessAdminUser.postUserAccount();

		userData[user2.alternateName] = {
			name: user2.givenName,
			password: 'test',
			surname: user2.familyName,
		};

		const userGroup = await apiHelpers.headlessAdminUser.postUserGroup();

		await apiHelpers.headlessAdminUser.assignUsersToUserGroup(
			userGroup.id,
			[user1.id]
		);

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
					actionIds: ['ACCESS_IN_CONTROL_PANEL'],
					primaryKey: companyId,
					resourceName:
						'com_liferay_blogs_web_portlet_BlogsAdminPortlet',
					scope: 1,
				},
				{
					actionIds: ['VIEW'],
					primaryKey: companyId,
					resourceName: 'com.liferay.blogs.model.BlogsEntry',
					scope: 1,
				},
			],
			roleType: 'regular',
		});

		await rolesPage.goto();
		await rolesPage.assignRoleToUserGroup(role.name, userGroup.name);

		const guestGroup = await apiHelpers.jsonWebServicesGroup.getGroupByKey(
			companyId,
			'Guest'
		);

		const blog = await apiHelpers.headlessDelivery.postBlog(
			guestGroup.groupId
		);

		await performUserSwitch(page, user1.alternateName);

		await blogsPage.goto(guestGroup.friendlyURL);

		await expect(blogsPage.blogTitle(blog.headline)).toBeVisible();

		await performUserSwitch(page, user2.alternateName);

		await blogsPage.goto(guestGroup.friendlyURL);

		await expect(rolesPage.noPermissionMessage).toBeVisible();
	}
);

test(
	'Add user group regular role',
	{tag: '@LPD-56222'},
	async ({apiHelpers, page, userGroupsPage}) => {
		page.on('dialog', async (dialog) => await dialog.accept());

		const user1 = await apiHelpers.headlessAdminUser.postUserAccount();

		userData[user1.alternateName] = {
			name: user1.givenName,
			password: 'test',
			surname: user1.familyName,
		};

		const user2 = await apiHelpers.headlessAdminUser.postUserAccount();

		userData[user2.alternateName] = {
			name: user2.givenName,
			password: 'test',
			surname: user2.familyName,
		};

		const companyId = await page.evaluate(() => {
			return Liferay.ThemeDisplay.getCompanyId();
		});

		const role1 = await apiHelpers.headlessAdminUser.postRole({
			name: getRandomString(),
			rolePermissions: [
				{
					actionIds: ['VIEW_CONTROL_PANEL'],
					primaryKey: companyId,
					resourceName: '90',
					scope: 1,
				},
				{
					actionIds: ['ACCESS_IN_CONTROL_PANEL', 'VIEW'],
					primaryKey: companyId,
					resourceName:
						'com_liferay_user_groups_admin_web_portlet_UserGroupsAdminPortlet',
					scope: 1,
				},
			],
			roleType: 'regular',
		});

		const role2 = await apiHelpers.headlessAdminUser.postRole({
			name: getRandomString(),
			rolePermissions: [
				{
					actionIds: ['VIEW_CONTROL_PANEL'],
					primaryKey: companyId,
					resourceName: '90',
					scope: 1,
				},
				{
					actionIds: ['ACCESS_IN_CONTROL_PANEL', 'VIEW'],
					primaryKey: companyId,
					resourceName:
						'com_liferay_user_groups_admin_web_portlet_UserGroupsAdminPortlet',
					scope: 1,
				},
				{
					actionIds: ['ADD_USER_GROUP'],
					primaryKey: companyId,
					resourceName: '90',
					scope: 1,
				},
				{
					actionIds: ['DELETE'],
					primaryKey: companyId,
					resourceName: 'com.liferay.portal.kernel.model.UserGroup',
					scope: 1,
				},
			],
			roleType: 'regular',
		});

		await apiHelpers.headlessAdminUser.assignUserToRole(
			role1.externalReferenceCode,
			user1.id
		);
		await apiHelpers.headlessAdminUser.assignUserToRole(
			role2.externalReferenceCode,
			user2.id
		);

		await performUserSwitch(page, user1.alternateName);

		await userGroupsPage.goToWithLimitedAccess();

		await expect(userGroupsPage.noUserGroupsMessage).toBeVisible();
		await expect(userGroupsPage.newUserGroupButton).not.toBeVisible();

		await performUserSwitch(page, user2.alternateName);

		await userGroupsPage.goToWithLimitedAccess();

		await expect(userGroupsPage.noUserGroupsMessage).toBeVisible();
		await expect(userGroupsPage.newUserGroupButton).toBeVisible();

		await userGroupsPage.newUserGroupButton.click();

		const userGroupName = getRandomString();

		await expect(async () => {
			await userGroupsPage.nameInput.fill(userGroupName);
			await userGroupsPage.saveButton.click();

			await waitForAlert(page);
		}).toPass();

		await expect(
			userGroupsPage.userGroupsTableCell(userGroupName)
		).toBeVisible();

		await (
			await userGroupsPage.userGroupsTableCheckbox(userGroupName)
		).check();

		await userGroupsPage.deleteButton.click();

		await waitForAlert(page);
	}
);

test(
	'View user groups with no permission as user',
	{tag: ['@LPD-56222', '@LPS-40580']},
	async ({apiHelpers, page, userGroupsPage}) => {
		const user = await apiHelpers.headlessAdminUser.postUserAccount();

		userData[user.alternateName] = {
			name: user.givenName,
			password: 'test',
			surname: user.familyName,
		};

		const userGroup = await apiHelpers.headlessAdminUser.postUserGroup();

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
					actionIds: ['ACCESS_IN_CONTROL_PANEL'],
					primaryKey: companyId,
					resourceName:
						'com_liferay_user_groups_admin_web_portlet_UserGroupsAdminPortlet',
					scope: 1,
				},
				{
					actionIds: ['VIEW'],
					primaryKey: companyId,
					resourceName: 'com.liferay.portal.kernel.model.UserGroup',
					scope: 1,
				},
			],
			roleType: 'regular',
		});

		await apiHelpers.headlessAdminUser.assignUserToRole(
			role.externalReferenceCode,
			user.id
		);

		await performUserSwitch(page, user.alternateName);

		await userGroupsPage.goToWithLimitedAccess();

		await expect(
			userGroupsPage.userGroupsTableCell(userGroup.name)
		).toBeVisible();

		await expect(
			await userGroupsPage.userGroupsTableRowActions(userGroup.name)
		).not.toBeVisible();
	}
);

test(
	'User groups site owner permissions',
	{tag: '@LPD-56598'},
	async ({apiHelpers, page, site, siteMembershipsPage}) => {
		const user = await apiHelpers.headlessAdminUser.postUserAccount();

		userData[user.alternateName] = {
			name: user.givenName,
			password: 'test',
			surname: user.familyName,
		};

		const userGroup = await apiHelpers.headlessAdminUser.postUserGroup();

		await apiHelpers.headlessAdminUser.assignUsersToUserGroup(
			userGroup.id,
			[user.id]
		);

		await siteMembershipsPage.goto(site.friendlyUrlPath);

		await siteMembershipsPage.userGroupsLink.click();

		await expect(async () => {
			await siteMembershipsPage.newUserGroupButton.click();

			await siteMembershipsPage.assignUserGroupTable.changeView('Table');
		}).toPass();

		await (
			await siteMembershipsPage.assignUserGroupTable.rowCheckbox(
				userGroup.name
			)
		).check();
		await siteMembershipsPage.userGroupSelectDoneButton.click();

		await waitForAlert(page);

		await performUserSwitch(page, user.alternateName);

		await siteMembershipsPage.goto(site.friendlyUrlPath);

		await expect(siteMembershipsPage.noPermissionMessage).toBeVisible();

		await performUserSwitch(page, 'test');

		await siteMembershipsPage.goto(site.friendlyUrlPath);
		await siteMembershipsPage.userGroupsLink.click();

		await (
			await siteMembershipsPage.userGroupsTable.rowActions(userGroup.name)
		).click();
		await siteMembershipsPage.assignRolesButton.click();
		await siteMembershipsPage.assignRolesTable.changeView('Table');
		await (
			await siteMembershipsPage.assignRolesTable.rowCheckbox('Site Owner')
		).check();
		await siteMembershipsPage.assignRolesDoneButton.click();

		await waitForAlert(page);

		await (
			await siteMembershipsPage.userGroupsTable.rowActions(userGroup.name)
		).click();
		await siteMembershipsPage.assignRolesButton.click();
		await siteMembershipsPage.assignRolesTable.changeView('Cards');
		await siteMembershipsPage.assignRolesDoneButton.click();

		await waitForAlert(page);

		await performUserSwitch(page, user.alternateName);

		await siteMembershipsPage.goto(site.friendlyUrlPath);

		await siteMembershipsPage.usersTable.changeView('Table');

		await expect(
			siteMembershipsPage.usersTable.cell(user.name)
		).toBeVisible();

		await (
			await siteMembershipsPage.usersTable.rowActions(user.name)
		).click();

		await expect(siteMembershipsPage.assignRolesButton).toBeVisible();
	}
);

test(
	'View user group site role in line',
	{tag: '@LPD-56598'},
	async ({apiHelpers, userGroupsPage}) => {
		const role = await apiHelpers.headlessAdminUser.postRole({
			name: getRandomString(),
			roleType: 'site',
		});

		const userGroup = await apiHelpers.headlessAdminUser.postUserGroup();

		await userGroupsPage.goto();

		await (
			await userGroupsPage.userGroupsTableRowActions(userGroup.name)
		).click();

		await expect(
			userGroupsPage.userGroupPagesPermissionsMenuItem
		).toBeVisible();

		await userGroupsPage.userGroupPagesPermissionsMenuItem.click();

		await expect(
			userGroupsPage.userGroupPagesPermissionsTable.cell(role.name, false)
		).toBeVisible();
	}
);

test(
	'Assign site role to user group',
	{tag: '@LPD-56598'},
	async ({apiHelpers, page, site, siteMembershipsPage}) => {
		const userGroup = await apiHelpers.headlessAdminUser.postUserGroup();

		await siteMembershipsPage.goto(site.friendlyUrlPath);

		await siteMembershipsPage.userGroupsLink.click();
		await siteMembershipsPage.newUserGroupButton.click();
		await siteMembershipsPage.assignUserGroupTable.changeView('Table');
		await (
			await siteMembershipsPage.assignUserGroupTable.rowCheckbox(
				userGroup.name
			)
		).check();
		await siteMembershipsPage.userGroupSelectDoneButton.click();

		await waitForAlert(page);

		const role = await apiHelpers.headlessAdminUser.postRole({
			name: getRandomString(),
			roleType: 'site',
		});

		await (
			await siteMembershipsPage.userGroupsTable.rowActions(userGroup.name)
		).click();
		await siteMembershipsPage.assignRolesButton.click();

		await expect(async () => {
			await siteMembershipsPage.assignRolesTable.changeView('Table');
			await (
				await siteMembershipsPage.assignRolesTable.rowCheckbox(
					role.name
				)
			).check();
			await siteMembershipsPage.assignRolesDoneButton.click();

			await waitForAlert(page);

			await (
				await siteMembershipsPage.userGroupsTable.rowActions(
					userGroup.name
				)
			).click();
			await siteMembershipsPage.assignRolesButton.click();
			await siteMembershipsPage.assignRolesTable.changeView('Cards');
			await siteMembershipsPage.assignRolesDoneButton.click();

			await expect(
				siteMembershipsPage.userGroupsTable.cell(role.name)
			).toBeVisible();
		}).toPass();
	}
);
