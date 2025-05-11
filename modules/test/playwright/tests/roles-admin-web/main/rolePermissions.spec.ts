/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {accountsPagesTest} from '../../../fixtures/accountsPagesTest';
import {dataApiHelpersTest} from '../../../fixtures/dataApiHelpersTest';
import {featureFlagsTest} from '../../../fixtures/featureFlagsTest';
import {isolatedSiteTest} from '../../../fixtures/isolatedSiteTest';
import {loginTest} from '../../../fixtures/loginTest';
import {pageEditorPagesTest} from '../../../fixtures/pageEditorPagesTest';
import {portletConfigurationPermissionsPageTest} from '../../../fixtures/portletConfigurationPermissionsPagesTest';
import {rolesPagesTest} from '../../../fixtures/rolesPagesTest';
import {usersAndOrganizationsPagesTest} from '../../../fixtures/usersAndOrganizationsPagesTest';
import {HomePage} from '../../../pages/portal-web/HomePage';
import getRandomString from '../../../utils/getRandomString';
import {
	performLoginViaApi,
	performLogout,
	userData,
} from '../../../utils/performLogin';
import {waitForAlert} from '../../../utils/waitForAlert';
import getPageDefinition from '../../layout-content-page-editor-web/main/utils/getPageDefinition';
import getWidgetDefinition from '../../layout-content-page-editor-web/main/utils/getWidgetDefinition';
import {setupBookmark} from './utils/bookmarks';

export const test = mergeTests(
	accountsPagesTest,
	dataApiHelpersTest,
	featureFlagsTest({
		'LPS-178052': {enabled: true},
	}),
	isolatedSiteTest,
	loginTest(),
	pageEditorPagesTest,
	portletConfigurationPermissionsPageTest,
	rolesPagesTest,
	usersAndOrganizationsPagesTest
);

test(
	'A user with Preview in Device permission can view the simulation button',
	{tag: ['@LPD-50352']},
	async ({apiHelpers, page}) => {
		const user = await apiHelpers.headlessAdminUser.postUserAccount();

		userData[user.alternateName] = {
			name: user.givenName,
			password: 'test',
			surname: user.familyName,
		};

		await performLogout(page);
		await performLoginViaApi({page, screenName: user.alternateName});

		await expect(page.getByTestId('simulation')).toHaveCount(0);

		await performLogout(page);
		await performLoginViaApi({page, screenName: 'test'});

		const role = await apiHelpers.headlessAdminUser.postRole({
			name: getRandomString(),
			rolePermissions: [
				{
					actionIds: ['PREVIEW_IN_DEVICE'],
					primaryKey: await page.evaluate(() => {
						return Liferay.ThemeDisplay.getCompanyId();
					}),
					resourceName: 'com.liferay.portal.kernel.model.Group',
					scope: 1,
				},
			],
			roleType: 'regular',
		});

		await apiHelpers.headlessAdminUser.assignUserToRole(
			role.externalReferenceCode,
			user.id
		);

		await performLogout(page);
		await performLoginViaApi({page, screenName: user.alternateName});

		await expect(page.getByTestId('simulation')).toBeVisible();

		await page.getByTestId('simulation').click();

		await expect(page.getByLabel('Mobile')).toBeVisible();
	}
);

test(
	'A user with View and Add Role permissions can add different kinds of roles',
	{tag: ['@LPD-50352']},
	async ({apiHelpers, page, rolePage, rolesPage}) => {
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
					actionIds: ['ACCESS_IN_CONTROL_PANEL'],
					primaryKey: companyId,
					resourceName:
						'com_liferay_roles_admin_web_portlet_RolesAdminPortlet',
					scope: 1,
				},
			],
			roleType: 'regular',
		});

		const user = await apiHelpers.headlessAdminUser.postUserAccount();

		userData[user.alternateName] = {
			name: user.givenName,
			password: 'test',
			surname: user.familyName,
		};

		await apiHelpers.headlessAdminUser.assignUserToRole(
			role1.externalReferenceCode,
			user.id
		);

		await performLogout(page);
		await performLoginViaApi({page, screenName: user.alternateName});

		await rolesPage.goto(false);

		await expect(async () => {
			await rolesPage.rolesTable.changeView('Table');

			await expect(rolesPage.rolesTable.cell('Title')).toBeVisible({
				timeout: 300,
			});
		}).toPass();

		await expect(rolesPage.rolesTable.searchInput).toBeEditable();
		await expect(rolesPage.rolesTable.newButton).toHaveCount(0);

		await rolesPage.organizationRolesLink.click();

		await expect(rolesPage.rolesTable.searchInput).toBeEditable();
		await expect(rolesPage.rolesTable.newButton).toHaveCount(0);

		await rolesPage.siteRolesLink.click();

		await expect(rolesPage.rolesTable.searchInput).toBeEditable();
		await expect(rolesPage.rolesTable.newButton).toHaveCount(0);

		await performLogout(page);
		await performLoginViaApi({page, screenName: 'test'});

		const role2 = await apiHelpers.headlessAdminUser.postRole({
			name: getRandomString(),
			rolePermissions: [
				{
					actionIds: ['ADD_ROLE'],
					primaryKey: companyId,
					resourceName: '90',
					scope: 1,
				},
				{
					actionIds: ['VIEW'],
					primaryKey: companyId,
					resourceName: 'com.liferay.portal.kernel.model.Role',
					scope: 1,
				},
			],
			roleType: 'regular',
		});

		await apiHelpers.headlessAdminUser.assignUserToRole(
			role2.externalReferenceCode,
			user.id
		);

		await performLogout(page);
		await performLoginViaApi({page, screenName: user.alternateName});

		await rolesPage.goto(false);

		await expect(rolesPage.rolesTable.newButton).toBeVisible();
		await expect(rolesPage.rolesTable.searchInput).toBeEditable();

		let newRoleName = getRandomString();

		await rolesPage.rolesTable.newButton.click();
		await rolePage.addRole(apiHelpers, {name: newRoleName});
		await rolePage.backButton.click();

		await expect(rolesPage.rolesTable.cell(newRoleName)).toBeVisible();

		await rolesPage.organizationRolesLink.click();

		await expect(rolesPage.rolesTable.cell(newRoleName)).toHaveCount(0);
		await expect(rolesPage.rolesTable.newButton).toBeVisible();
		await expect(rolesPage.rolesTable.searchInput).toBeEditable();

		newRoleName = getRandomString();

		await rolesPage.rolesTable.newButton.click();
		await rolePage.addRole(apiHelpers, {name: newRoleName});
		await rolePage.backButton.click();

		await expect(rolesPage.rolesTable.cell(newRoleName)).toBeVisible();

		await rolesPage.siteRolesLink.click();

		await expect(rolesPage.rolesTable.cell(newRoleName)).toHaveCount(0);
		await expect(rolesPage.rolesTable.newButton).toBeVisible();
		await expect(rolesPage.rolesTable.searchInput).toBeEditable();

		newRoleName = getRandomString();

		await rolesPage.rolesTable.newButton.click();
		await rolePage.addRole(apiHelpers, {name: newRoleName});
		await rolePage.backButton.click();

		await expect(rolesPage.rolesTable.cell(newRoleName)).toBeVisible();
	}
);

test(
	'Without the View permission on Administrator role you still can assign new roles',
	{tag: ['@LPD-50352', '@LPS-144566']},
	async ({apiHelpers, editUserPage, page, usersAndOrganizationsPage}) => {
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
						'com_liferay_users_admin_web_portlet_UsersAdminPortlet',
					scope: 1,
				},
				{
					actionIds: ['UPDATE', 'VIEW'],
					primaryKey: companyId,
					resourceName: 'com.liferay.portal.kernel.model.User',
					scope: 1,
				},
			],
			roleType: 'regular',
		});

		const role2 = await apiHelpers.headlessAdminUser.postRole({
			name: getRandomString(),
			roleType: 'regular',
		});

		await apiHelpers.jsonWebServicesResourcePermissionApiHelper.setIndividualResourcePermissions(
			['ASSIGN_MEMBERS', 'VIEW'],
			companyId,
			'0',
			'com.liferay.portal.kernel.model.Role',
			String(role2.id),
			String(role1.id)
		);

		const administratorRole =
			await apiHelpers.headlessAdminUser.getRoleByName('Administrator');
		const userRole =
			await apiHelpers.headlessAdminUser.getRoleByName('User');

		try {
			await apiHelpers.jsonWebServicesResourcePermissionApiHelper.setIndividualResourcePermissions(
				[],
				companyId,
				'0',
				'com.liferay.portal.kernel.model.Role',
				String(administratorRole.id),
				String(userRole.id)
			);

			const user1 = await apiHelpers.headlessAdminUser.postUserAccount();

			userData[user1.alternateName] = {
				name: user1.givenName,
				password: 'test',
				surname: user1.familyName,
			};

			await apiHelpers.headlessAdminUser.assignUserToRole(
				role1.externalReferenceCode,
				user1.id
			);

			const user2 = await apiHelpers.headlessAdminUser.postUserAccount();

			await performLogout(page);
			await performLoginViaApi({page, screenName: user1.alternateName});

			await usersAndOrganizationsPage.goToUsersWithLimitedAccess();

			await (
				await usersAndOrganizationsPage.usersTableRowLink(
					user2.alternateName
				)
			).click();

			await editUserPage.rolesLink.click();
			await editUserPage.selectRegularRolesButton.click();

			await expect(
				editUserPage.selectRegularRolesSearchInput
			).toBeEnabled();

			await editUserPage
				.selectRegularRolesChooseButton(role2.name)
				.click();

			await expect(
				editUserPage.regularRoleCell(role2.name)
			).toBeVisible();

			await editUserPage.saveButton.click();

			await waitForAlert(page);
		}
		finally {
			await performLogout(page);
			await performLoginViaApi({page, screenName: 'test'});

			await apiHelpers.jsonWebServicesResourcePermissionApiHelper.setIndividualResourcePermissions(
				['VIEW'],
				companyId,
				'0',
				'com.liferay.portal.kernel.model.Role',
				String(administratorRole.id),
				String(userRole.id)
			);
		}
	}
);

test(
	'A user can only view the portlets he has permissions from applications menu',
	{tag: ['@LPD-50835', '@LPS-157219']},
	async ({apiHelpers, page}) => {
		const user = await apiHelpers.headlessAdminUser.postUserAccount();

		userData[user.alternateName] = {
			name: user.givenName,
			password: 'test',
			surname: user.familyName,
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
					actionIds: ['ACCESS_IN_CONTROL_PANEL'],
					primaryKey: companyId,
					resourceName:
						'com_liferay_site_admin_web_portlet_SiteAdminPortlet',
					scope: 1,
				},
				{
					actionIds: ['ACCESS_IN_CONTROL_PANEL'],
					primaryKey: companyId,
					resourceName:
						'com_liferay_layout_set_prototype_web_portlet_LayoutSetPrototypePortlet',
					scope: 1,
				},
				{
					actionIds: ['UPDATE'],
					primaryKey: companyId,
					resourceName:
						'com.liferay.portal.kernel.model.LayoutSetPrototype',
					scope: 1,
				},
			],
			roleType: 'regular',
		});

		await apiHelpers.headlessAdminUser.assignUserToRole(
			role.externalReferenceCode,
			user.id
		);

		await performLogout(page);
		await performLoginViaApi({page, screenName: user.alternateName});

		const homePage = new HomePage(page);

		await homePage.openApplicationMenu();

		await expect(
			page.getByRole('tab', {
				name: 'Applications',
			})
		).toHaveCount(0);
		await expect(
			page.getByRole('tab', {
				name: 'Commerce',
			})
		).toHaveCount(0);
		await expect(
			page.getByRole('tab', {
				name: 'Control Panel',
			})
		).toBeVisible();
		await expect(
			page.getByRole('menuitem', {
				exact: true,
				name: 'Users and Organizations',
			})
		).toHaveCount(0);
		await expect(
			page.getByRole('menuitem', {
				exact: true,
				name: 'App Manager',
			})
		).toHaveCount(0);
		await expect(
			page.getByRole('menuitem', {
				exact: true,
				name: 'Sites',
			})
		).toBeVisible();
		await expect(
			page.getByRole('menuitem', {
				exact: true,
				name: 'Site Templates',
			})
		).toBeVisible();
	}
);

test(
	'The default site role should not be inherited by Guest site',
	{tag: ['@LPD-50835', '@LPS-148855']},
	async ({accountsPage, apiHelpers, page, site}) => {
		const account = await apiHelpers.headlessAdminUser.postAccount();

		const companyId = await page.evaluate(() => {
			return Liferay.ThemeDisplay.getCompanyId();
		});

		const role = await apiHelpers.headlessAdminUser.postRole({
			name: getRandomString(),
			roleType: 'regular',
		});

		const layout = await apiHelpers.headlessDelivery.createSitePage({
			pageDefinition: getPageDefinition([
				getWidgetDefinition({
					id: getRandomString(),
					widgetName:
						'com_liferay_account_admin_web_internal_portlet_AccountEntriesManagementPortlet',
				}),
			]),
			siteId: site.id,
			title: getRandomString(),
		});

		const guestGroup = await apiHelpers.jsonWebServicesGroup.getGroupByKey(
			companyId,
			'Guest'
		);

		await apiHelpers.jsonWebServicesGroup.assignRoleToGroup(
			String(role.id),
			[site.id, guestGroup.groupId]
		);

		await apiHelpers.jsonWebServicesResourcePermissionApiHelper.setIndividualResourcePermissions(
			['VIEW'],
			companyId,
			'0',
			'com.liferay.account.model.AccountEntry',
			String(account.id),
			String(role.id)
		);

		const user = await apiHelpers.headlessAdminUser.postUserAccount();

		userData[user.alternateName] = {
			name: user.givenName,
			password: 'test',
			surname: user.familyName,
		};

		await performLogout(page);
		await performLoginViaApi({page, screenName: user.alternateName});

		await page.goto(`/web/${site.name}/${layout.friendlyUrlPath}`);

		await expect(accountsPage.accountsTable.cell(account.name)).toHaveCount(
			0
		);

		await performLogout(page);
		await performLoginViaApi({page, screenName: 'test'});

		await apiHelpers.jsonWebServicesUser.assignUsersToSite(
			site.id,
			user.id
		);

		await performLogout(page);
		await performLoginViaApi({page, screenName: user.alternateName});

		await page.goto(`/web/${site.name}/${layout.friendlyUrlPath}`);

		await expect(
			accountsPage.accountsTable.cell(account.name)
		).toBeVisible();

		await performLogout(page);
		await performLoginViaApi({page, screenName: 'test'});

		const guestRole = await apiHelpers.headlessAdminUser.getRoleByName(
			'Guest',
			'rolePermissions'
		);

		await apiHelpers.jsonWebServicesResourcePermissionApiHelper.setIndividualResourcePermissions(
			[],
			companyId,
			'0',
			'com.liferay.account.model.AccountEntry',
			String(account.id),
			String(guestRole.id)
		);

		await performLogout(page);

		await page.goto(`/web/${site.name}/${layout.friendlyUrlPath}`);

		await expect(accountsPage.accountsTable.cell(account.name)).toHaveCount(
			0
		);
	}
);

test(
	'Edit team permissions',
	{tag: ['@LPD-50835']},
	async ({
		apiHelpers,
		page,
		portletConfigurationPermissionsPage,
		site,
		teamsPage,
	}) => {
		test.setTimeout(120000);

		const companyId = await page.evaluate(() => {
			return Liferay.ThemeDisplay.getCompanyId();
		});

		const role = await apiHelpers.headlessAdminUser.postRole({
			name: getRandomString(),
			rolePermissions: [
				{
					actionIds: [
						'ACCESS_IN_CONTROL_PANEL',
						'ADD_TO_PAGE',
						'CONFIGURATION',
						'PERMISSIONS',
						'PREFERENCES',
						'VIEW',
					],
					primaryKey: '0',
					resourceName:
						'com_liferay_site_memberships_web_portlet_SiteMembershipsPortlet',
					scope: 3,
				},
				{
					actionIds: [
						'ADD_COMMUNITY',
						'ADD_LAYOUT',
						'ADD_LAYOUT_BRANCH',
						'ADD_LAYOUT_SET_BRANCH',
						'ADD_LAYOUT_UTILITY_PAGE_ENTRY',
						'ASSIGN_DEFAULT_LAYOUT_UTILITY_PAGE_ENTRY',
						'ASSIGN_MEMBERS',
						'ASSIGN_USER_ROLES',
						'CONFIGURE_PORTLETS',
						'DELETE',
						'EXPORT_IMPORT_LAYOUTS',
						'EXPORT_IMPORT_PORTLET_INFO',
						'MANAGE_ANNOUNCEMENTS',
						'MANAGE_ARCHIVED_SETUPS',
						'MANAGE_LAYOUTS',
						'MANAGE_STAGING',
						'MANAGE_SUBGROUPS',
						'MANAGE_TEAMS',
						'PERMISSIONS',
						'PREVIEW_IN_DEVICE',
						'PUBLISH_PORTLET_INFO',
						'PUBLISH_STAGING',
						'UPDATE',
						'VIEW',
						'VIEW_MEMBERS',
						'VIEW_SITE_ADMINISTRATION',
						'VIEW_STAGING',
					],
					primaryKey: '0',
					resourceName: 'com.liferay.portal.kernel.model.Group',
					scope: 3,
				},
				{
					actionIds: [
						'ASSIGN_MEMBERS',
						'DELETE',
						'PERMISSIONS',
						'UPDATE',
						'VIEW',
					],
					primaryKey: '0',
					resourceName: 'com.liferay.portal.kernel.model.Team',
					scope: 3,
				},
				{
					actionIds: [
						'ADD_TO_PAGE',
						'CONFIGURATION',
						'PERMISSIONS',
						'PREFERENCES',
						'VIEW',
					],
					primaryKey: '0',
					resourceName:
						'com_liferay_journal_content_web_portlet_JournalContentPortlet',
					scope: 3,
				},
			],
			roleType: 'site',
		});

		const layout = await apiHelpers.headlessDelivery.createSitePage({
			pageDefinition: getPageDefinition([
				getWidgetDefinition({
					id: getRandomString(),
					widgetName:
						'com_liferay_journal_content_web_portlet_JournalContentPortlet',
				}),
			]),
			siteId: site.id,
			title: getRandomString(),
		});

		const user = await apiHelpers.headlessAdminUser.postUserAccount();

		userData[user.alternateName] = {
			name: user.givenName,
			password: 'test',
			surname: user.familyName,
		};

		await apiHelpers.jsonWebServicesUser.assignUsersToSite(
			site.id,
			user.id
		);
		await apiHelpers.headlessAdminUser.assignUserToSite(
			role.id,
			site.id,
			user.id
		);

		await performLogout(page);
		await performLoginViaApi({page, screenName: user.alternateName});

		await teamsPage.goTo(site.friendlyUrlPath);

		const teamName = getRandomString();

		await teamsPage.newTeamButton.click();
		await teamsPage.nameInput.fill(teamName);
		await teamsPage.saveButton.click();

		await waitForAlert(page);

		await expect(teamsPage.teamsTable.cell(teamName)).toBeVisible();

		const team = await apiHelpers.jsonWebServicesTeam.getTeam(
			site.id,
			teamName
		);

		await page.goto(`/web/${site.name}/${layout.friendlyUrlPath}`);

		await page.getByRole('link', {name: 'Edit'}).click();

		await expect(async () => {
			const portlet = page.locator(
				"div[id*='p_p_id_com_liferay_journal_content_web_portlet_JournalContentPortlet_']"
			);

			await portlet.click();
			await page
				.locator('#wrapper')
				.getByRole('button', {name: 'Options'})
				.click();
			await page.getByRole('menuitem', {name: 'Permissions'}).click();

			await expect(
				portletConfigurationPermissionsPage.permissionsFrame.getByRole(
					'cell',
					{name: teamName}
				)
			).toBeVisible();
		}).toPass();

		await performLogout(page);
		await performLoginViaApi({page, screenName: 'test'});

		await page.goto(`/web/${site.name}/${layout.friendlyUrlPath}`);

		await page.getByRole('link', {name: 'Edit'}).click();

		await expect(async () => {
			const portlet = page.locator(
				"div[id*='p_p_id_com_liferay_journal_content_web_portlet_JournalContentPortlet_']"
			);

			await portlet.click();
			await page
				.locator('#wrapper')
				.getByRole('button', {name: 'Options'})
				.click();
			await page.getByRole('menuitem', {name: 'Permissions'}).click();

			await expect(
				portletConfigurationPermissionsPage.permissionsFrame.getByRole(
					'cell',
					{name: teamName}
				)
			).toBeVisible();
		}).toPass();

		await apiHelpers.jsonWebServicesResourcePermissionApiHelper.setIndividualResourcePermissions(
			['ASSIGN_MEMBERS', 'DELETE', 'PERMISSIONS', 'UPDATE', 'VIEW'],
			companyId,
			'0',
			'com.liferay.portal.kernel.model.Team',
			String(team.teamId),
			String(role.id)
		);

		await performLogout(page);
		await performLoginViaApi({page, screenName: user.alternateName});

		await teamsPage.goTo(site.friendlyUrlPath);

		const newTeamName = getRandomString();

		await expect(async () => {
			await (await teamsPage.teamsTable.rowActions(teamName)).click();

			await expect(teamsPage.editLink).toBeVisible({timeout: 300});
		}).toPass();

		await teamsPage.editLink.click();
		await teamsPage.nameInput.fill(newTeamName);
		await teamsPage.saveButton.click();

		await waitForAlert(page);

		await expect(teamsPage.teamsTable.cell(newTeamName)).toBeVisible();
		await expect(teamsPage.teamsTable.cell(teamName)).toHaveCount(0);

		await apiHelpers.jsonWebServicesTeam.deleteTeam(team.teamId);
	}
);

test(
	'Site role bookmarks inline permissions',
	{tag: ['@LPD-50835']},
	async ({apiHelpers, bookmarksPage, page, site}) => {
		const role = await apiHelpers.headlessAdminUser.postRole({
			name: getRandomString(),
			rolePermissions: [
				{
					actionIds: ['VIEW'],
					primaryKey: '0',
					resourceName: 'com.liferay.bookmarks.model.BookmarksEntry',
					scope: 3,
				},
			],
			roleType: 'site',
		});

		const bookmarkName = getRandomString();

		const {layout} = await setupBookmark(
			apiHelpers,
			bookmarkName,
			bookmarksPage,
			page,
			site
		);

		const user = await apiHelpers.headlessAdminUser.postUserAccount();

		userData[user.alternateName] = {
			name: user.givenName,
			password: 'test',
			surname: user.familyName,
		};

		await apiHelpers.jsonWebServicesUser.assignUsersToSite(
			site.id,
			user.id
		);

		await performLogout(page);
		await performLoginViaApi({page, screenName: user.alternateName});

		await page.goto(`/web/${site.name}/${layout.friendlyUrlPath}`);

		await expect(bookmarksPage.bookmarkItem(bookmarkName)).toHaveCount(0);

		await performLogout(page);
		await performLoginViaApi({page, screenName: 'test'});

		await apiHelpers.headlessAdminUser.assignUserToSite(
			role.id,
			site.id,
			user.id
		);

		await performLogout(page);
		await performLoginViaApi({page, screenName: user.alternateName});

		await page.goto(`/web/${site.name}/${layout.friendlyUrlPath}`);

		await expect(bookmarksPage.bookmarkItem(bookmarkName)).toBeVisible();
	}
);

test(
	'Team permissions site',
	{tag: ['@LPD-50835']},
	async ({apiHelpers, page, site, teamsPage}) => {
		const user1 = await apiHelpers.headlessAdminUser.postUserAccount();

		userData[user1.alternateName] = {
			name: user1.givenName,
			password: 'test',
			surname: user1.familyName,
		};

		await apiHelpers.jsonWebServicesUser.assignUsersToSite(
			site.id,
			user1.id
		);

		const user2 = await apiHelpers.headlessAdminUser.postUserAccount();

		userData[user2.alternateName] = {
			name: user2.givenName,
			password: 'test',
			surname: user2.familyName,
		};

		await apiHelpers.jsonWebServicesUser.assignUsersToSite(
			site.id,
			user2.id
		);

		await teamsPage.goTo(site.friendlyUrlPath);

		const teamName = getRandomString();

		await teamsPage.newTeamButton.click();
		await teamsPage.nameInput.fill(teamName);
		await teamsPage.saveButton.click();

		await waitForAlert(page);

		await expect(teamsPage.teamsTable.cell(teamName)).toBeVisible();

		const team = await apiHelpers.jsonWebServicesTeam.getTeam(
			site.id,
			teamName
		);

		await apiHelpers.jsonWebServicesUser.addTeamUsers(team.teamId, [
			user2.id,
		]);

		const layout = await apiHelpers.headlessDelivery.createSitePage({
			pageDefinition: getPageDefinition([
				getWidgetDefinition({
					id: getRandomString(),
					widgetName:
						'com_liferay_journal_content_web_portlet_JournalContentPortlet',
				}),
			]),
			siteId: site.id,
			title: getRandomString(),
		});

		const companyId = await page.evaluate(() => {
			return Liferay.ThemeDisplay.getCompanyId();
		});

		const guestRole = await apiHelpers.headlessAdminUser.getRoleByName(
			'Guest',
			'rolePermissions'
		);

		await apiHelpers.jsonWebServicesResourcePermissionApiHelper.setIndividualResourcePermissions(
			[],
			companyId,
			'0',
			'com.liferay.portal.kernel.model.Layout',
			String(layout.id),
			String(guestRole.id)
		);

		const siteMemberRole = await apiHelpers.headlessAdminUser.getRoleByName(
			'Site Member',
			'rolePermissions'
		);

		await apiHelpers.jsonWebServicesResourcePermissionApiHelper.setIndividualResourcePermissions(
			[],
			companyId,
			'0',
			'com.liferay.portal.kernel.model.Layout',
			String(layout.id),
			String(siteMemberRole.id)
		);

		await apiHelpers.jsonWebServicesResourcePermissionApiHelper.setIndividualResourcePermissions(
			['VIEW'],
			companyId,
			String(team.groupId),
			'com.liferay.portal.kernel.model.Layout',
			String(layout.id),
			String(Number(team.teamId) + 1)
		);

		await performLogout(page);
		await performLoginViaApi({page, screenName: user1.alternateName});

		await page.goto(`/web/${site.name}/${layout.friendlyUrlPath}`);

		await expect(
			page
				.getByRole('heading', {name: '404'})
				.or(page.getByText('requested resource could not be found'))
		).toBeVisible();

		await performLogout(page);
		await performLoginViaApi({page, screenName: user2.alternateName});

		await page.goto(`/web/${site.name}/${layout.friendlyUrlPath}`);

		await expect(
			page
				.getByRole('heading', {name: '404'})
				.or(page.getByText('requested resource could not be found'))
		).toHaveCount(0);
	}
);

test(
	'View asset via organization role',
	{tag: ['@LPD-50835']},
	async ({
		apiHelpers,
		blogsPage,
		editOrganizationPage,
		page,
		usersAndOrganizationsPage,
	}) => {
		const companyId = await page.evaluate(() => {
			return Liferay.ThemeDisplay.getCompanyId();
		});

		const organization =
			await apiHelpers.headlessAdminUser.postOrganization();

		await usersAndOrganizationsPage.goToOrganizations();

		await (
			await usersAndOrganizationsPage.organizationActionsMenu(
				organization.name
			)
		).click();
		await editOrganizationPage.organizationEditMenuItem.click();
		await editOrganizationPage.organizationSiteLink.click();
		await editOrganizationPage.createSiteToggle.check();
		await editOrganizationPage.organizationSiteSaveButton.click();

		await waitForAlert(page);

		const organizationSite =
			await apiHelpers.jsonWebServicesGroup.getGroupByKey(
				companyId,
				`${organization.name} LFR_ORGANIZATION`
			);

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

		await apiHelpers.headlessAdminUser.assignUserToOrganizationByEmailAddress(
			organization.id,
			user1.emailAddress
		);

		apiHelpers.data.push({
			id: `${organization.id}_${user1.emailAddress}`,
			type: 'organizationUserAccountAssociation',
		});

		await apiHelpers.headlessAdminUser.assignUserToOrganizationByEmailAddress(
			organization.id,
			user2.emailAddress
		);

		apiHelpers.data.push({
			id: `${organization.id}_${user2.emailAddress}`,
			type: 'organizationUserAccountAssociation',
		});

		const blog = await apiHelpers.headlessDelivery.postBlog(
			organizationSite.groupId,
			{
				headline: getRandomString(),
			}
		);

		const layout = await apiHelpers.headlessDelivery.createSitePage({
			pageDefinition: getPageDefinition([
				getWidgetDefinition({
					id: getRandomString(),
					widgetName: 'com_liferay_blogs_web_portlet_BlogsPortlet',
				}),
			]),
			siteId: organizationSite.groupId,
			title: getRandomString(),
		});

		const role = await apiHelpers.headlessAdminUser.postRole({
			name: getRandomString(),
			rolePermissions: [
				{
					actionIds: ['VIEW'],
					primaryKey: '0',
					resourceName: 'com.liferay.blogs.model.BlogsEntry',
					scope: 3,
				},
			],
			roleType: 'organization',
		});

		await apiHelpers.headlessAdminUser.assignUserToOrganizationRole(
			role.id,
			user1.id,
			organization.id
		);

		const guestRole = await apiHelpers.headlessAdminUser.getRoleByName(
			'Guest',
			'rolePermissions'
		);

		await apiHelpers.jsonWebServicesResourcePermissionApiHelper.setIndividualResourcePermissions(
			[],
			companyId,
			'0',
			'com.liferay.blogs.model.BlogsEntry',
			String(blog.id),
			String(guestRole.id)
		);

		const organizationUserRole =
			await apiHelpers.headlessAdminUser.getRoleByName(
				'Organization User',
				'rolePermissions'
			);

		await apiHelpers.jsonWebServicesResourcePermissionApiHelper.setIndividualResourcePermissions(
			[],
			companyId,
			'0',
			'com.liferay.blogs.model.BlogsEntry',
			String(blog.id),
			String(organizationUserRole.id)
		);

		const siteMemberRole = await apiHelpers.headlessAdminUser.getRoleByName(
			'Site Member',
			'rolePermissions'
		);

		await apiHelpers.jsonWebServicesResourcePermissionApiHelper.setIndividualResourcePermissions(
			[],
			companyId,
			'0',
			'com.liferay.blogs.model.BlogsEntry',
			String(blog.id),
			String(siteMemberRole.id)
		);

		await performLogout(page);
		await performLoginViaApi({page, screenName: user1.alternateName});

		await page.goto(`/web/${organization.name}/${layout.friendlyUrlPath}`);

		await expect(blogsPage.blogTitle(blog.headline)).toBeVisible();
		await expect(blogsPage.noEntriesMessage).toHaveCount(0);

		await performLogout(page);
		await performLoginViaApi({page, screenName: user2.alternateName});

		await page.goto(`/web/${organization.name}/${layout.friendlyUrlPath}`);

		await expect(blogsPage.blogTitle(blog.headline)).toHaveCount(0);
		await expect(blogsPage.noEntriesMessage).toBeVisible();
	}
);

test(
	'View asset via site role',
	{tag: ['@LPD-50835']},
	async ({apiHelpers, blogsPage, page, site}) => {
		const user1 = await apiHelpers.headlessAdminUser.postUserAccount();

		userData[user1.alternateName] = {
			name: user1.givenName,
			password: 'test',
			surname: user1.familyName,
		};

		await apiHelpers.jsonWebServicesUser.assignUsersToSite(
			site.id,
			user1.id
		);

		const user2 = await apiHelpers.headlessAdminUser.postUserAccount();

		userData[user2.alternateName] = {
			name: user2.givenName,
			password: 'test',
			surname: user2.familyName,
		};

		await apiHelpers.jsonWebServicesUser.assignUsersToSite(
			site.id,
			user2.id
		);

		const blog = await apiHelpers.headlessDelivery.postBlog(site.id, {
			headline: getRandomString(),
		});

		const layout = await apiHelpers.headlessDelivery.createSitePage({
			pageDefinition: getPageDefinition([
				getWidgetDefinition({
					id: getRandomString(),
					widgetName: 'com_liferay_blogs_web_portlet_BlogsPortlet',
				}),
			]),
			siteId: site.id,
			title: getRandomString(),
		});

		const companyId = await page.evaluate(() => {
			return Liferay.ThemeDisplay.getCompanyId();
		});

		const role = await apiHelpers.headlessAdminUser.postRole({
			name: getRandomString(),
			rolePermissions: [
				{
					actionIds: ['VIEW'],
					primaryKey: '0',
					resourceName: 'com.liferay.blogs.model.BlogsEntry',
					scope: 3,
				},
			],
			roleType: 'site',
		});

		await apiHelpers.headlessAdminUser.assignUserToSite(
			role.id,
			site.id,
			user1.id
		);

		const guestRole = await apiHelpers.headlessAdminUser.getRoleByName(
			'Guest',
			'rolePermissions'
		);

		await apiHelpers.jsonWebServicesResourcePermissionApiHelper.setIndividualResourcePermissions(
			[],
			companyId,
			'0',
			'com.liferay.blogs.model.BlogsEntry',
			String(blog.id),
			String(guestRole.id)
		);

		const siteMemberRole = await apiHelpers.headlessAdminUser.getRoleByName(
			'Site Member',
			'rolePermissions'
		);

		await apiHelpers.jsonWebServicesResourcePermissionApiHelper.setIndividualResourcePermissions(
			[],
			companyId,
			'0',
			'com.liferay.blogs.model.BlogsEntry',
			String(blog.id),
			String(siteMemberRole.id)
		);

		await performLogout(page);
		await performLoginViaApi({page, screenName: user1.alternateName});

		await page.goto(`/web/${site.name}/${layout.friendlyUrlPath}`);

		await expect(blogsPage.blogTitle(blog.headline)).toBeVisible();
		await expect(blogsPage.noEntriesMessage).toHaveCount(0);

		await performLogout(page);
		await performLoginViaApi({page, screenName: user2.alternateName});

		await page.goto(`/web/${site.name}/${layout.friendlyUrlPath}`);

		await expect(blogsPage.blogTitle(blog.headline)).toHaveCount(0);
		await expect(blogsPage.noEntriesMessage).toBeVisible();
	}
);

test(
	'View private page site member',
	{tag: ['@LPD-50835']},
	async ({apiHelpers, page, site, siteMembershipsPage}) => {
		const user = await apiHelpers.headlessAdminUser.postUserAccount();

		userData[user.alternateName] = {
			name: user.givenName,
			password: 'test',
			surname: user.familyName,
		};

		const layout = await apiHelpers.headlessDelivery.createSitePage({
			siteId: site.id,
			title: getRandomString(),
		});

		const guestRole = await apiHelpers.headlessAdminUser.getRoleByName(
			'Guest',
			'rolePermissions'
		);

		await apiHelpers.jsonWebServicesResourcePermissionApiHelper.setIndividualResourcePermissions(
			[],
			await page.evaluate(() => {
				return Liferay.ThemeDisplay.getCompanyId();
			}),
			'0',
			'com.liferay.portal.kernel.model.Layout',
			String(layout.id),
			String(guestRole.id)
		);

		await performLogout(page);
		await performLoginViaApi({page, screenName: user.alternateName});

		await page.goto(`/web/${site.name}/${layout.friendlyUrlPath}`);

		await expect(
			page
				.getByRole('heading', {name: '404'})
				.or(page.getByText('requested resource could not be found'))
		).toBeVisible();

		await performLogout(page);
		await performLoginViaApi({page, screenName: 'test'});

		const userGroup = await apiHelpers.headlessAdminUser.postUserGroup();

		await apiHelpers.headlessAdminUser.assignUsersToUserGroup(
			userGroup.id,
			[user.id]
		);

		await siteMembershipsPage.goto(site.friendlyUrlPath);
		await siteMembershipsPage.userGroupsLink.click();

		await expect(siteMembershipsPage.noUserGroupMessage).toBeVisible();

		await siteMembershipsPage.newUserGroupButton.click();
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
		await performLoginViaApi({page, screenName: user.alternateName});

		await page.goto(`/web/${site.name}/${layout.friendlyUrlPath}`);

		await expect(
			page
				.getByRole('heading', {name: '404'})
				.or(page.getByText('requested resource could not be found'))
		).toHaveCount(0);
	}
);
