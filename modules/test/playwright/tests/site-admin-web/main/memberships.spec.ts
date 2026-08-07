/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {apiHelpersTest} from '../../../fixtures/apiHelpersTest';
import {dataApiHelpersTest} from '../../../fixtures/dataApiHelpersTest';
import {isolatedSiteTest} from '../../../fixtures/isolatedSiteTest';
import {loginTest} from '../../../fixtures/loginTest';
import {pageEditorPagesTest} from '../../../fixtures/pageEditorPagesTest';
import {siteSettingsPagesTest} from '../../../fixtures/siteSettingsPagesTest';
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
	dataApiHelpersTest,
	isolatedSiteTest,
	loginTest(),
	membershipsPagesTest,
	pageEditorPagesTest,
	siteSettingsPagesTest
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

		const site2 = await apiHelpers.headlessAdminSite.postSite({
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

		await page.waitForTimeout(300);

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

		await page.reload();

		await page.waitForTimeout(300);

		await page.keyboard.press('Tab');

		await page.keyboard.press('Tab');

		await page.keyboard.press('Tab');

		await expect(
			page.getByRole('link', {name: 'Go to Memberships'})
		).toBeFocused();

		await expect(
			page.locator('.tooltip-inner', {hasText: 'Go to Memberships'})
		).toBeVisible();
	}
);

test(
	'Confirm that no pop up appears when select user card with XSS name in memberships',
	{
		tag: '@LPD-69499',
	},
	async ({apiHelpers, membershipsPage, page}) => {
		let dialogMessage: null | string = null;

		page.on('dialog', async (dialog) => {
			dialogMessage = dialog.message();

			await dialog.dismiss();
		});

		const user = await apiHelpers.headlessAdminUser.postUserAccount({
			familyName: `"><script>alert(2)</script>`,
			givenName: `"><script>alert(1)</script>`,
		});

		userData[user.alternateName] = {
			name: user.givenName,
			password: 'test',
			surname: user.familyName,
		};

		await membershipsPage.goto();

		await page.getByRole('heading', {name: 'Memberships'}).waitFor();

		await page.getByRole('button', {name: 'Add'}).click();

		await page
			.frameLocator('iframe[title="Assign Users to This Site"]')
			.getByLabel(user.givenName)
			.check();

		await page.getByRole('button', {name: 'Done'}).click();

		const userCard = page.locator(
			`[id="_com_liferay_site_memberships_web_portlet_SiteMembershipsPortlet_users_${user.alternateName}"]`
		);

		await userCard.waitFor();

		await userCard.click({force: true});

		await expect(userCard.locator('input[type="checkbox"]')).toBeChecked();

		expect(dialogMessage).toBeNull();
	}
);

test(
	'Assert no pop up when viewing membership request detail',
	{
		tag: '@LPD-69499',
	},
	async ({
		apiHelpers,
		membershipsPage,
		page,
		pageEditorPage,
		site,
		siteSettingsPage,
	}) => {
		let dialogMessage: null | string = null;

		page.on('dialog', async (dialog) => {
			dialogMessage = dialog.message();

			await dialog.dismiss();
		});

		const site2 = await apiHelpers.headlessAdminSite.postSite({
			membershipType: 'restricted',
			name: getRandomString(),
		});

		const layout = await apiHelpers.jsonWebServicesLayout.addLayout({
			groupId: site.id,
			options: {type: 'content'},
			title: getRandomString(),
		});

		const user = await apiHelpers.headlessAdminUser.postUserAccount();

		userData[user.alternateName] = {
			name: user.givenName,
			password: 'test',
			surname: user.familyName,
		};

		await pageEditorPage.goto(layout, site.friendlyUrlPath);

		await pageEditorPage.addWidget('Community', 'My Sites');

		const widgetId = await pageEditorPage.getFragmentId('My Sites');

		await pageEditorPage.changeWidgetPermission(
			widgetId,
			'#user_ACTION_VIEW',
			true
		);

		await pageEditorPage.publishPage();

		await performLogout(page);

		await performLogin(page, user.alternateName);

		await page.goto(`/web${site.friendlyUrlPath}${layout.friendlyURL}`);

		await page.getByRole('link', {name: 'Available Sites'}).click();

		await clickAndExpectToBeVisible({
			autoClick: true,
			target: page.getByRole('menuitem', {name: 'Request Membership'}),
			trigger: page
				.locator(
					`[id="_com_liferay_site_my_sites_web_portlet_MySitesPortlet_ocerSearchContainer_-${site2.name}"]`
				)
				.getByLabel('Show Actions'),
		});

		await page
			.getByLabel('Characters Maximum')
			.fill(`<html><script>alert('test');</script></html>`);

		await page.getByRole('button', {name: 'Save'}).click();

		await performLogout(page);

		await performLogin(page, 'test');

		await siteSettingsPage.goto(site2.friendlyUrlPath);

		await membershipsPage.goto();

		await clickAndExpectToBeVisible({
			autoClick: true,
			target: page.getByRole('menuitem', {
				name: 'View Membership Requests',
			}),
			trigger: page.getByLabel('Options', {exact: true}),
		});

		await clickAndExpectToBeVisible({
			autoClick: true,
			target: page.getByRole('menuitem', {name: 'Reply'}),
			trigger: page.getByLabel('More actions'),
		});

		await page
			.getByLabel('Characters Maximum')
			.fill(`<html><script>alert('test');</script></html>`);

		await page.getByRole('button', {name: 'Save'}).click();

		await page.getByRole('link', {name: 'Approved'}).click();

		await page
			.getByRole('link', {
				name: `${user.givenName} ${user.familyName}`,
			})
			.click();

		await expect(page.locator('p.approved.status')).toBeVisible();

		expect(dialogMessage).toBeNull();
	}
);

test(
	'Ensure pagination functions properly in Unassign Roles modal',
	{
		tag: '@LPD-71299',
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

		for (let i = 1; i <= 21; i++) {
			const role = await apiHelpers.headlessAdminUser.postRole({
				name: getRandomString(),
				roleType: 'site',
			});

			await apiHelpers.headlessAdminUser.assignUserToSite(
				role.id,
				siteId,
				user.id
			);
		}

		await membershipsPage.goto();

		await clickAndExpectToBeVisible({
			autoClick: true,
			target: page.getByRole('menuitem', {name: 'Unassign Roles'}),
			timeout: 500,
			trigger: page
				.locator(
					'[id="_com_liferay_site_memberships_web_portlet_SiteMembershipsPortlet_users_' +
						user.alternateName +
						'"]'
				)
				.getByLabel('More actions'),
		});

		await page
			.frameLocator('iframe[title="Unassign Roles"]')
			.getByLabel('Page 2')
			.click();

		await expect(
			page
				.frameLocator('iframe[title="Unassign Roles"]')
				.getByText('Showing 21 to 21 of 21')
		).toBeVisible();

		await expect(
			page
				.frameLocator('iframe[title="Unassign Roles"]')
				.getByText('Site Administrator')
		).not.toBeVisible();
	}
);

test('Allow Manual Membership Management toggle controls product menu visibility', async ({
	membershipsPage,
	page,
	site,
	siteSettingsPage,
}) => {
	await page.goto(`/group${site.friendlyUrlPath}/~/control_panel/manage`);

	await membershipsPage.productMenuPage.openProductMenuIfClosed();
	await membershipsPage.productMenuPage.peopleButton.click();

	await expect(
		membershipsPage.productMenuPage.membershipsButton
	).toBeVisible();

	await siteSettingsPage.goToSiteSetting(
		'Site Configuration',
		null,
		site.friendlyUrlPath
	);

	await page.getByLabel('Allow Manual Membership Management').click();

	await siteSettingsPage.saveConfiguration();

	await page.goto(`/group${site.friendlyUrlPath}/~/control_panel/manage`);

	await membershipsPage.productMenuPage.openProductMenuIfClosed();
	await membershipsPage.productMenuPage.peopleButton.click();

	await expect(
		membershipsPage.productMenuPage.membershipsButton
	).not.toBeVisible();
});

test('Assign organization as site member and search', async ({
	apiHelpers,
	membershipsPage,
	page,
}) => {
	const organization1 = await apiHelpers.headlessAdminUser.postOrganization();
	const organization2 = await apiHelpers.headlessAdminUser.postOrganization();

	await membershipsPage.goto();

	await page.getByRole('link', {name: 'Organizations'}).click();

	await expect(
		page.getByText(
			'No organization was found that is a member of this site.'
		)
	).toBeVisible();

	await page.getByRole('button', {name: 'Add'}).click();

	await page.waitForTimeout(500);

	await page
		.frameLocator('iframe[title="Assign Organizations to This Site"]')
		.getByLabel(organization1.name)
		.check();

	await page.getByRole('button', {name: 'Done'}).click();

	await waitForAlert(page);

	const searchBox = page.getByPlaceholder('Search for');

	await expect(async () => {
		await searchBox.fill(organization1.name);
		await searchBox.press('Enter');

		await expect(
			page.getByRole('cell', {exact: true, name: organization1.name})
		).toBeVisible({timeout: 2000});
	}).toPass();

	await expect(async () => {
		await searchBox.fill(organization2.name);
		await searchBox.press('Enter');

		await expect(
			page.getByText(
				'No organization was found that is a member of this site.'
			)
		).toBeVisible({timeout: 2000});
	}).toPass();
});

test('Limit child site membership to parent site members', async ({
	apiHelpers,
	membershipsPage,
	page,
	siteSettingsPage,
}) => {
	const parentSite = await apiHelpers.headlessAdminSite.postSite({
		name: getRandomString(),
	});

	const childSite = await apiHelpers.headlessAdminSite.postSite({
		name: getRandomString(),
		parentSiteExternalReferenceCode: parentSite.externalReferenceCode,
	});

	const userInParent = await apiHelpers.headlessAdminUser.postUserAccount();
	const userNotInParent =
		await apiHelpers.headlessAdminUser.postUserAccount();

	const siteRole =
		await apiHelpers.headlessAdminUser.getRoleByName('Site Member');

	await apiHelpers.headlessAdminUser.assignUserToSite(
		siteRole.id,
		parentSite.id,
		userInParent.id
	);

	await siteSettingsPage.goToSiteSetting(
		'Site Configuration',
		null,
		childSite.friendlyUrlPath
	);

	await page
		.getByLabel('Limit membership to members of the parent site')
		.click();

	await siteSettingsPage.saveConfiguration();

	await membershipsPage.goto();

	await page.getByRole('button', {name: 'Add'}).click();

	await page.waitForTimeout(500);

	const usersFrame = page.frameLocator(
		'iframe[title="Assign Users to This Site"]'
	);

	await expect(usersFrame.getByLabel(userInParent.givenName)).toBeVisible();
	await expect(
		usersFrame.getByLabel(userNotInParent.givenName)
	).not.toBeVisible();
});

test('Search and paginate site members', async ({
	apiHelpers,
	membershipsPage,
	page,
	site,
}) => {
	await page.goto(`/group${site.friendlyUrlPath}/~/control_panel/manage`);

	const siteRole =
		await apiHelpers.headlessAdminUser.getRoleByName('Site Member');

	const users: TUserAccount[] = [];

	for (let i = 0; i < 20; i++) {
		const user = await apiHelpers.headlessAdminUser.postUserAccount();

		await apiHelpers.headlessAdminUser.assignUserToSite(
			siteRole.id,
			site.id,
			user.id
		);

		users.push(user);
	}

	await membershipsPage.goto();

	await expect(
		page.getByText('Showing 1 to 20 of 21 entries.')
	).toBeVisible();

	await page.getByLabel('Page 2').click();

	await expect(
		page.getByText('Showing 21 to 21 of 21 entries.')
	).toBeVisible();

	await page.getByLabel('Page 1').click();

	await expect(
		page.getByText('Showing 1 to 20 of 21 entries.')
	).toBeVisible();

	const searchBox = page.getByPlaceholder('Search for');

	const searchForMember = async (query: string, user: TUserAccount) => {
		await expect(async () => {
			await searchBox.fill(query);
			await searchBox.press('Enter');

			await expect(
				page.locator(
					`[id="_com_liferay_site_memberships_web_portlet_SiteMembershipsPortlet_users_${user.alternateName}"]`
				)
			).toBeVisible({timeout: 2000});
		}).toPass();
	};

	await searchForMember(users[0].givenName, users[0]);
	await searchForMember(users[1].familyName, users[1]);
	await searchForMember(users[2].alternateName, users[2]);

	await searchBox.fill('nonexistentmember');
	await searchBox.press('Enter');

	await expect(
		page.getByText(
			'No user was found that is a direct member of this site.'
		)
	).toBeVisible();
});

test('Search user group site members', async ({
	apiHelpers,
	membershipsPage,
	page,
}) => {
	const userGroup1 = await apiHelpers.headlessAdminUser.postUserGroup();
	const userGroup2 = await apiHelpers.headlessAdminUser.postUserGroup();

	await membershipsPage.goto();

	await page.getByRole('link', {name: 'User Groups'}).click();

	await expect(
		page.getByText('No user group was found that is a member of this site.')
	).toBeVisible();

	await page.getByRole('button', {name: 'Add'}).click();

	await page.waitForTimeout(500);

	const userGroupsFrame = page.frameLocator(
		'iframe[title="Assign User Groups to This Site"]'
	);

	await userGroupsFrame.getByPlaceholder('Search for').fill(userGroup1.name);
	await userGroupsFrame.getByPlaceholder('Search for').press('Enter');

	await userGroupsFrame.getByLabel('Select All Items on the Page').click();

	await page.getByRole('button', {name: 'Done'}).click();

	await waitForAlert(page);

	const searchBox = page.getByPlaceholder('Search for');

	await expect(async () => {
		await searchBox.fill(userGroup1.name);
		await searchBox.press('Enter');

		await expect(
			page.getByRole('cell', {exact: true, name: userGroup1.name})
		).toBeVisible({timeout: 2000});
	}).toPass();

	await expect(async () => {
		await searchBox.fill(userGroup2.name);
		await searchBox.press('Enter');

		await expect(
			page.getByText(
				'No user group was found that is a member of this site.'
			)
		).toBeVisible({timeout: 2000});
	}).toPass();
});

test(
	'Inherited members are labeled and removal affects only direct memberships',
	{tag: '@LPD-87301'},
	async ({apiHelpers, membershipsPage, page}) => {
		const siteId = await page.evaluate(() => {
			return String(Liferay.ThemeDisplay.getSiteGroupId());
		});

		const explicitUser =
			await apiHelpers.headlessAdminUser.postUserAccount();

		await apiHelpers.jsonWebServicesUser.assignUsersToSite(
			siteId,
			explicitUser.id
		);

		const inheritedUser =
			await apiHelpers.headlessAdminUser.postUserAccount();
		const userGroup = await apiHelpers.headlessAdminUser.postUserGroup();

		await apiHelpers.headlessAdminUser.assignUsersToUserGroup(
			userGroup.id,
			[inheritedUser.id]
		);

		await apiHelpers.jsonWebServicesUserGroup.assignUserGroupsToGroup(
			siteId,
			String(userGroup.id)
		);

		await membershipsPage.goto();

		await expect(membershipsPage.inheritanceSourceLabel).toBeVisible();
		await expect(membershipsPage.inheritanceSourceLabel).toHaveCount(1);

		let confirmationMessage = '';

		page.once('dialog', (dialog) => {
			confirmationMessage = dialog.message();

			dialog.accept();
		});

		await membershipsPage.triggerRemoveMembership(
			explicitUser.alternateName
		);

		await waitForAlert(page);

		expect(confirmationMessage).toContain(
			'Only direct memberships will be removed'
		);
		await expect(page.getByText(explicitUser.name)).not.toBeVisible();
		await expect(membershipsPage.inheritanceSourceLabel).toBeVisible();
	}
);
