/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {apiHelpersTest} from '../../../fixtures/apiHelpersTest';
import {loginTest} from '../../../fixtures/loginTest';
import {clickAndExpectToBeVisible} from '../../../utils/clickAndExpectToBeVisible';
import getRandomString from '../../../utils/getRandomString';
import performLogin, {
	performLogout,
	userData,
} from '../../../utils/performLogin';
import {waitForAlert} from '../../../utils/waitForAlert';
import {membershipsPagesTest} from './fixtures/membershipsPagesTest';

export const test = mergeTests(
	apiHelpersTest,
	loginTest(),
	membershipsPagesTest
);

test(
	'Confirm search bar does not display for membership requests',
	{
		tag: '@LPD-36275',
	},
	async ({membershipsPage, page}) => {
		await membershipsPage.goto();

		await clickAndExpectToBeVisible({
			autoClick: true,
			target: page.getByRole('menuitem', {
				name: 'View Membership Requests',
			}),
			trigger: page.getByLabel('Options', {exact: true}),
		});

		await expect(page.getByPlaceholder('Search for')).not.toBeVisible();
	}
);

test(
	'Bulk removal of roles from users',
	{
		tag: '@LPD-41737',
	},
	async ({apiHelpers, membershipsPage, page}) => {
		const user = await apiHelpers.headlessAdminUser.postUserAccount();

		const siteId = await page.evaluate(() => {
			return String(Liferay.ThemeDisplay.getSiteGroupId());
		});

		const siteRole =
			await apiHelpers.headlessAdminUser.getRoleByName('Site Member');

		await apiHelpers.headlessAdminUser.assignUserToSite(
			siteRole.id,
			siteId,
			user.id
		);

		await membershipsPage.goto();
		await membershipsPage.assignSiteAdministratorRole();
		await membershipsPage.filterBySiteAdministratorRole();
		await membershipsPage.removeSiteAdministratorRole();

		await expect(
			page.getByText(
				'No user was found that is a direct member of this site.'
			)
		).toBeVisible();

		await page.getByLabel('Remove Site Administrator').click();

		await expect(page.getByText(user.name)).toBeVisible();

		await apiHelpers.headlessAdminUser.deleteUserAccount(Number(user.id));
	}
);

test(
	'Bulk removal of roles from user groups',
	{
		tag: '@LPD-41737',
	},
	async ({apiHelpers, membershipsPage, page}) => {
		const userGroup1 = await apiHelpers.headlessAdminUser.postUserGroup();
		const userGroup2 = await apiHelpers.headlessAdminUser.postUserGroup();

		await membershipsPage.goto();

		await page.getByRole('link', {name: 'User Groups'}).click();

		await expect(
			page.getByText(
				' No user group was found that is a member of this site.'
			)
		).toBeVisible();

		await page.getByRole('button', {name: 'Add'}).click();

		await page.waitForTimeout(500);

		await page
			.frameLocator('iframe[title="Assign User Groups to This Site"]')
			.getByLabel('Select All Items on the Page')
			.click();

		await page.getByRole('button', {name: 'Done'}).click();

		await waitForAlert(page);

		await membershipsPage.assignSiteAdministratorRole();
		await membershipsPage.filterBySiteAdministratorRole();
		await membershipsPage.removeSiteAdministratorRole();

		await expect(
			page.getByText(
				' No user group was found that is a member of this site.'
			)
		).toBeVisible();

		await page.getByLabel('Remove Site Administrator').click();

		await expect(page.getByText(userGroup1.name)).toBeVisible();
		await expect(page.getByText(userGroup2.name)).toBeVisible();

		await apiHelpers.headlessAdminUser.deleteUserGroup(
			Number(userGroup1.id)
		);
		await apiHelpers.headlessAdminUser.deleteUserGroup(
			Number(userGroup2.id)
		);
	}
);

test(
	'Filter by roles shows cards as selectable',
	{
		tag: '@LPD-41741',
	},
	async ({membershipsPage, page}) => {
		await membershipsPage.goto();

		await clickAndExpectToBeVisible({
			autoClick: true,
			target: page.getByRole('menuitem', {name: 'Roles'}),
			timeout: 500,
			trigger: page.getByLabel('Filter'),
		});

		await expect(
			page
				.frameLocator('iframe[title="Select Role"]')
				.locator('.card-interactive')
				.first()
		).toBeVisible();
	}
);

test(
	'Confirm roles are unassign from users tab',
	{
		tag: '@LPD-42500',
	},
	async ({apiHelpers, membershipsPage, page}) => {
		const userAccount =
			await apiHelpers.headlessAdminUser.postUserAccount();

		await membershipsPage.goto();

		await page.waitForTimeout(500);

		await membershipsPage.assignAllUsersSiteMembership();

		await membershipsPage.assignAllRolesToUser(userAccount.alternateName);

		await membershipsPage.unassignAllRolesFromUser(
			userAccount.alternateName
		);

		await clickAndExpectToBeVisible({
			autoClick: true,
			target: page.getByRole('menuitem', {name: 'Unassign Roles'}),
			timeout: 500,
			trigger: page
				.locator(
					'[id="_com_liferay_site_memberships_web_portlet_SiteMembershipsPortlet_users_' +
						userAccount.alternateName +
						'"]'
				)
				.getByLabel('More actions'),
		});

		await expect(
			page
				.frameLocator('iframe[title="Unassign Roles"]')
				.locator(
					'[id="_com_liferay_site_memberships_web_portlet_SiteMembershipsPortlet_userGroupRoleRole_1"] label div'
				)
				.first()
		).not.toBeVisible();

		await expect(
			page
				.frameLocator('iframe[title="Unassign Roles"]')
				.locator(
					'[id="_com_liferay_site_memberships_web_portlet_SiteMembershipsPortlet_userGroupRoleRole_2"] label div'
				)
				.first()
		).not.toBeVisible();

		await expect(
			page
				.frameLocator('iframe[title="Unassign Roles"]')
				.locator(
					'[id="_com_liferay_site_memberships_web_portlet_SiteMembershipsPortlet_userGroupRoleRole_3"] label div'
				)
				.first()
		).not.toBeVisible();

		await apiHelpers.headlessAdminUser.deleteUserAccount(
			Number(userAccount.id)
		);
	}
);

test(
	'Able to remove membership after assigning role to user',
	{
		tag: '@LPD-50734',
	},
	async ({apiHelpers, membershipsPage, page}) => {
		const user = await apiHelpers.headlessAdminUser.postUserAccount();

		const siteId = await page.evaluate(() => {
			return String(Liferay.ThemeDisplay.getSiteGroupId());
		});

		const siteRole =
			await apiHelpers.headlessAdminUser.getRoleByName('Site Member');

		await apiHelpers.headlessAdminUser.assignUserToSite(
			siteRole.id,
			siteId,
			user.id
		);

		await membershipsPage.goto();
		await membershipsPage.assignAllRolesToUser(user.alternateName);
		await membershipsPage.removeSiteMembershipFromUser(user.alternateName);

		await expect(page.getByText(user.name)).not.toBeVisible();

		await apiHelpers.headlessAdminUser.deleteUserAccount(Number(user.id));
	}
);

test(
	'Filter roles that are assigned to the user based on the current group',
	{
		tag: '@LPD-53010',
	},
	async ({apiHelpers, membershipsPage, page}) => {
		const user = await apiHelpers.headlessAdminUser.postUserAccount();

		const currentSiteId = await page.evaluate(() => {
			return String(Liferay.ThemeDisplay.getSiteGroupId());
		});

		const site2 = await apiHelpers.headlessSite.createSite({
			name: getRandomString(),
		});

		const siteRole =
			await apiHelpers.headlessAdminUser.getRoleByName('Site Member');

		await apiHelpers.headlessAdminUser.assignUserToSite(
			siteRole.id,
			currentSiteId,
			user.id
		);

		await apiHelpers.headlessAdminUser.assignUserToSite(
			siteRole.id,
			site2.id,
			user.id
		);

		await membershipsPage.goto();
		await membershipsPage.assignSiteAdministratorRole();

		await page.goto(`/group/${site2.name}/~/control_panel/manage`);

		await membershipsPage.goto();
		await membershipsPage.openAssignRoles(user.alternateName);

		await expect(
			page
				.frameLocator('iframe[title="Assign Roles"]')
				.getByText('Site Administrator')
		).toBeVisible();

		await apiHelpers.headlessAdminUser.deleteUserAccount(Number(user.id));
		await apiHelpers.headlessSite.deleteSite(site2.id);
	}
);

test(
	'Confirm tooltip of back button of Membership Requests is correct',
	{
		tag: '@LPS-177717',
	},
	async ({apiHelpers, membershipsPage, page}) => {
		const user = await apiHelpers.headlessAdminUser.postUserAccount();

		userData[user.alternateName] = {
			name: user.givenName,
			password: 'test',
			surname: user.familyName,
		};

		await performLogout(page);

		await performLogin(page, user.alternateName);

		await page.getByTitle('User Profile Menu').click();

		await page
			.getByRole('menuitem', {
				name: 'My Dashboard',
			})
			.click();

		await page.getByRole('link', {name: 'Available Sites'}).click();

		await clickAndExpectToBeVisible({
			autoClick: true,
			target: page.getByRole('menuitem', {name: 'Request Membership'}),
			trigger: page
				.locator(
					'[id="_com_liferay_site_my_sites_web_portlet_MySitesPortlet_ocerSearchContainer_-guest"]'
				)
				.getByLabel('Show Actions'),
		});

		await page.locator('textarea[id$=comments]').fill('Test');

		await page.getByRole('button', {name: 'Save'}).click();

		await performLogout(page);

		await performLogin(page, 'test');

		await membershipsPage.goto();

		await clickAndExpectToBeVisible({
			autoClick: true,
			target: page.getByRole('menuitem', {
				name: 'View Membership Requests',
			}),
			trigger: page.getByLabel('Options', {exact: true}),
		});

		await expect(
			page
				.locator('.control-menu-nav-item')
				.getByTitle('Go to Memberships')
		).toBeVisible();

		await clickAndExpectToBeVisible({
			autoClick: true,
			target: page.getByRole('menuitem', {name: 'Reply'}),
			trigger: page
				.locator(
					'[id="_com_liferay_site_memberships_web_portlet_SiteMembershipsPortlet_membershipRequestsSearchContainer_1"]'
				)
				.getByLabel('More actions'),
		});

		await expect(
			page
				.locator('.control-menu-nav-item')
				.getByTitle('Go to Membership Requests')
		).toBeVisible();

		await page.locator('textarea[id$=replyComments]').fill('Test');

		await page.getByRole('button', {name: 'Save'}).click();

		await expect(
			page
				.locator('.control-menu-nav-item')
				.getByTitle('Go to Memberships')
		).toBeVisible();

		await apiHelpers.headlessAdminUser.deleteUserAccount(Number(user.id));
	}
);

test(
	'Confirm that, using Keyboard Navigation, it is possible to access the back button of Reply Membership, Membership Request, and Approved users',
	{
		tag: '@LPS-177717',
	},
	async ({apiHelpers, membershipsPage, page}) => {
		const user = await apiHelpers.headlessAdminUser.postUserAccount();

		userData[user.alternateName] = {
			name: user.givenName,
			password: 'test',
			surname: user.familyName,
		};

		await performLogout(page);

		await performLogin(page, user.alternateName);

		await page.getByTitle('User Profile Menu').click();

		await page
			.getByRole('menuitem', {
				name: 'My Dashboard',
			})
			.click();

		await page.getByRole('link', {name: 'Available Sites'}).click();

		await clickAndExpectToBeVisible({
			autoClick: true,
			target: page.getByRole('menuitem', {name: 'Request Membership'}),
			trigger: page
				.locator(
					'[id="_com_liferay_site_my_sites_web_portlet_MySitesPortlet_ocerSearchContainer_-guest"]'
				)
				.getByLabel('Show Actions'),
		});

		await page.locator('textarea[id$=comments]').fill('Test');

		await page.getByRole('button', {name: 'Save'}).click();

		await performLogout(page);

		await performLogin(page, 'test');

		await membershipsPage.goto();

		await clickAndExpectToBeVisible({
			autoClick: true,
			target: page.getByRole('menuitem', {
				name: 'View Membership Requests',
			}),
			trigger: page.getByLabel('Options', {exact: true}),
		});

		await expect(
			page
				.locator('.control-menu-nav-item')
				.getByTitle('Go to Memberships')
		).toBeVisible();

		await page.getByLabel('Close Product Menu').click();

		await page.waitForTimeout(300);

		await page.keyboard.press('Tab');

		await expect(
			page.getByRole('link', {name: 'Go to Memberships'})
		).toBeFocused();

		await expect(
			page.locator('.tooltip-inner', {hasText: 'Go to Memberships'})
		).toBeVisible();

		await clickAndExpectToBeVisible({
			autoClick: true,
			target: page.getByRole('menuitem', {name: 'Reply'}),
			trigger: page
				.locator(
					'[id="_com_liferay_site_memberships_web_portlet_SiteMembershipsPortlet_membershipRequestsSearchContainer_1"]'
				)
				.getByLabel('More actions'),
		});

		await expect(
			page
				.locator('.control-menu-nav-item')
				.getByTitle('Go to Membership Requests')
		).toBeVisible();

		await page.keyboard.press('Tab');

		await page.keyboard.press('Tab');

		await page.keyboard.press('Tab');

		await expect(
			page.getByRole('link', {name: 'Go to Membership Requests'})
		).toBeFocused();

		await expect(
			page.locator('.tooltip-inner', {
				hasText: 'Go to Membership Requests',
			})
		).toBeVisible();

		await page.locator('textarea[id$=replyComments]').fill('Test');

		await page.getByRole('button', {name: 'Save'}).click();

		await expect(
			page
				.locator('.control-menu-nav-item')
				.getByTitle('Go to Memberships')
		).toBeVisible();

		await page.keyboard.press('Tab');

		await page.keyboard.press('Tab');

		await page.keyboard.press('Tab');

		await expect(
			page.getByRole('link', {name: 'Go to Memberships'})
		).toBeFocused();

		await expect(
			page.locator('.tooltip-inner', {hasText: 'Go to Memberships'})
		).toBeVisible();

		await apiHelpers.headlessAdminUser.deleteUserAccount(Number(user.id));
	}
);
