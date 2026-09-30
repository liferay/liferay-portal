/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {dataApiHelpersTest} from '../../../fixtures/dataApiHelpersTest';
import {loginTest} from '../../../fixtures/loginTest';
import {userGroupsPageTest} from '../../../fixtures/userGroupsPageTest';
import {usersAndOrganizationsPagesTest} from '../../../fixtures/usersAndOrganizationsPagesTest';
import getRandomString from '../../../utils/getRandomString';
import {performUserSwitch, userData} from '../../../utils/performLogin';
import {waitForAlert} from '../../../utils/waitForAlert';

const test = mergeTests(dataApiHelpersTest, loginTest(), userGroupsPageTest);

const testWithPersonalSite = mergeTests(
	dataApiHelpersTest,
	loginTest(),
	userGroupsPageTest,
	usersAndOrganizationsPagesTest
);

test(
	'Can add, edit and delete user groups',
	{tag: '@LPD-57361'},
	async ({page, userGroupsPage}) => {
		page.on('dialog', async (dialog) => await dialog.accept());

		const userGroupName = getRandomString();

		await userGroupsPage.goto();

		await expect(async () => {
			await userGroupsPage.newUserGroupButton.click();

			await expect(userGroupsPage.nameInput).toBeVisible({timeout: 1000});
		}).toPass({timeout: 6000});

		await userGroupsPage.nameInput.fill(userGroupName);
		await userGroupsPage.saveButton.click();

		await waitForAlert(page);

		await expect(
			userGroupsPage.userGroupsTableCell(userGroupName)
		).toBeVisible();

		const userGroupRow = await userGroupsPage.userGroupsTableRow(
			1,
			userGroupName,
			true
		);

		await expect(userGroupRow.row.getByText('Approved')).toBeVisible();

		const newUserGroupName = getRandomString();

		await (
			await userGroupsPage.userGroupsTableRowActions(userGroupName)
		).click();
		await userGroupsPage.editUserGroupMenuItem.click();
		await userGroupsPage.nameInput.fill(newUserGroupName);
		await userGroupsPage.saveButton.click();

		await waitForAlert(page);

		await expect(
			userGroupsPage.userGroupsTableCell(newUserGroupName)
		).toBeVisible();

		await (
			await userGroupsPage.userGroupsTableCheckbox(newUserGroupName)
		).check();
		await userGroupsPage.deleteButton.click();

		await waitForAlert(page);

		await expect(
			userGroupsPage.userGroupsTableCell(newUserGroupName)
		).not.toBeVisible();
	}
);

test(
	'Can add and remove a user group member and delete the user group',
	{tag: ['@LPD-57361', '@LPD-107687']},
	async ({apiHelpers, page, userGroupsPage}) => {
		page.on('dialog', async (dialog) => await dialog.accept());

		const user = await apiHelpers.headlessAdminUser.postUserAccount();
		const userGroup = await apiHelpers.headlessAdminUser.postUserGroup();

		await userGroupsPage.goto();

		await expect(async () => {
			await (
				await userGroupsPage.userGroupsTableRowActions(userGroup.name)
			).click();
			await userGroupsPage.assignMembersMenuItem.click();

			await expect(userGroupsPage.noUsersMessage).toBeVisible({
				timeout: 500,
			});

			await userGroupsPage.newUserButton.click();
			await (
				await userGroupsPage.addUsersTable.rowCheckbox(user.name)
			).check();
			await userGroupsPage.addUsersIFrameAddButton.click();

			await waitForAlert(page);
		}).toPass({timeout: 5000});

		await expect(
			userGroupsPage.userGroupUsersTable.cell(user.name)
		).toBeVisible();

		await (
			await userGroupsPage.userGroupUsersTable.rowActions(user.name)
		).click();
		await userGroupsPage.removeUserMenuItem.click();

		await waitForAlert(page);

		await expect(userGroupsPage.noUsersMessage).toBeVisible();

		await userGroupsPage.backButton.click();

		await (
			await userGroupsPage.userGroupsTableCheckbox(userGroup.name)
		).check();

		const [getUsersCountResponse] = await Promise.all([
			page.waitForResponse((response) =>
				response.url().includes('get_users_count')
			),
			userGroupsPage.deleteButton.click(),
		]);

		expect(await getUsersCountResponse.text()).toBe('0');
		expect(getUsersCountResponse.url()).not.toContain('mvcPath');

		await waitForAlert(page);

		await expect(
			userGroupsPage.userGroupsTableCell(userGroup.name)
		).not.toBeVisible();
	}
);

test(
	'Cannot delete user group with assigned members',
	{tag: '@LPD-57361'},
	async ({apiHelpers, page, userGroupsPage}) => {
		page.on('dialog', async (dialog) => await dialog.accept());

		const user = await apiHelpers.headlessAdminUser.postUserAccount();
		const userGroup = await apiHelpers.headlessAdminUser.postUserGroup();

		await apiHelpers.headlessAdminUser.assignUsersToUserGroup(
			userGroup.id,
			[user.id]
		);

		await userGroupsPage.goto();

		await (
			await userGroupsPage.userGroupsTableCheckbox(userGroup.name)
		).check();
		await userGroupsPage.deleteButton.click();

		await expect(
			userGroupsPage.deleteUserGroupWithUsersErrorMessage
		).toBeVisible();
		await expect(
			userGroupsPage.userGroupsTableCell(userGroup.name)
		).toBeVisible();
	}
);

test(
	'Can search current members assigned to a user group',
	{tag: '@LPD-57361'},
	async ({apiHelpers, userGroupsPage}) => {
		const user1 = await apiHelpers.headlessAdminUser.postUserAccount();
		const user2 = await apiHelpers.headlessAdminUser.postUserAccount();
		const userGroup = await apiHelpers.headlessAdminUser.postUserGroup();

		await apiHelpers.headlessAdminUser.assignUsersToUserGroup(
			userGroup.id,
			[user1.id, user2.id]
		);

		await userGroupsPage.goto();

		await (
			await userGroupsPage.userGroupsTableRowLink(userGroup.name)
		).click();

		await expect(
			userGroupsPage.userGroupUsersTable.cell(user2.alternateName)
		).toBeVisible();

		await userGroupsPage.userGroupUsersTable.search(user1.alternateName);

		await expect(
			userGroupsPage.userGroupUsersTable.cell(user1.alternateName)
		).toBeVisible();
		await expect(
			userGroupsPage.userGroupUsersTable.cell(user2.alternateName)
		).not.toBeVisible();
	}
);

test(
	'Predefined pages can be added to user groups',
	{tag: '@LPD-81993'},
	async ({apiHelpers, page, userGroupsPage}) => {
		const companyId = await page.evaluate(() =>
			Liferay.ThemeDisplay.getCompanyId()
		);

		const userGroup = await apiHelpers.headlessAdminUser.postUserGroup({
			name: `UGUserGroup${getRandomString()}`,
		});

		const group = await apiHelpers.jsonWebServicesGroup.getGroupByKey(
			companyId,
			String(userGroup.id)
		);

		await apiHelpers.jsonWebServicesLayout.addLayout({
			groupId: group.groupId,
			title: 'UG Public Page',
		});
		await apiHelpers.jsonWebServicesLayout.addLayout({
			groupId: group.groupId,
			privateLayout: 'true',
			title: 'UG Private Page',
		});

		await userGroupsPage.goto();

		await test.step('Verify Go to Profile Pages opens public page', async () => {
			await expect(async () => {
				await (
					await userGroupsPage.userGroupsTableRowActions(
						userGroup.name
					)
				).click();

				await expect(
					userGroupsPage.goToProfilePagesMenuItem
				).toBeVisible({timeout: 500});
			}).toPass({timeout: 5000});

			const [profilePage] = await Promise.all([
				page.context().waitForEvent('page'),
				userGroupsPage.goToProfilePagesMenuItem.click(),
			]);

			await profilePage.waitForLoadState('networkidle');

			await expect(profilePage).toHaveTitle(/UG Public Page/);

			await profilePage.close();
		});

		await test.step('Verify Go to Dashboard Pages opens private page', async () => {
			await userGroupsPage.goto();

			await expect(async () => {
				await (
					await userGroupsPage.userGroupsTableRowActions(
						userGroup.name
					)
				).click();

				await expect(
					userGroupsPage.goToDashboardPagesMenuItem
				).toBeVisible({timeout: 500});
			}).toPass({timeout: 5000});

			const [dashboardPage] = await Promise.all([
				page.context().waitForEvent('page'),
				userGroupsPage.goToDashboardPagesMenuItem.click(),
			]);

			await dashboardPage.waitForLoadState('networkidle');

			await expect(dashboardPage).toHaveTitle(/UG Private Page/);

			await dashboardPage.close();
		});
	}
);

testWithPersonalSite(
	'User group pages are visible based on membership',
	{tag: '@LPD-81993'},
	async ({apiHelpers, page, userPersonalSitePage}) => {
		const companyId = await page.evaluate(() =>
			Liferay.ThemeDisplay.getCompanyId()
		);

		const user = await apiHelpers.headlessAdminUser.postUserAccount();

		userData[user.alternateName] = {
			name: user.givenName,
			password: userData['test'].password,
			surname: user.familyName,
		};

		const userGroup = await apiHelpers.headlessAdminUser.postUserGroup({
			name: `UGUserGroup${getRandomString()}`,
		});

		const group = await apiHelpers.jsonWebServicesGroup.getGroupByKey(
			companyId,
			String(userGroup.id)
		);

		await apiHelpers.jsonWebServicesLayout.addLayout({
			groupId: group.groupId,
			title: 'Home',
		});
		await apiHelpers.jsonWebServicesLayout.addLayout({
			groupId: group.groupId,
			title: 'Custom Page',
		});

		const adminUserId = await page.evaluate(() =>
			Liferay.ThemeDisplay.getUserId()
		);

		await apiHelpers.headlessAdminUser.assignUsersToUserGroup(
			userGroup.id,
			[adminUserId]
		);

		await test.step('Verify admin (in group) sees pages on profile', async () => {
			await expect(async () => {
				await userPersonalSitePage.userPersonalMenuButton.click();
				await userPersonalSitePage.myProfileMenuItem.click({
					timeout: 1000,
				});
			}).toPass({timeout: 5000});

			await expect(userPersonalSitePage.navItem('Home')).toBeVisible();
			await expect(
				userPersonalSitePage.navItem('Custom Page')
			).toBeVisible();
		});

		await test.step('Verify admin dashboard does NOT show group pages', async () => {
			await expect(async () => {
				await userPersonalSitePage.userPersonalMenuButton.click();
				await userPersonalSitePage.myDashboardMenuItem.click({
					timeout: 1000,
				});
			}).toPass({timeout: 5000});

			await expect(
				userPersonalSitePage.navItem('Custom Page')
			).not.toBeVisible();
		});

		await test.step('Verify non-member does NOT see group pages on profile', async () => {
			await performUserSwitch(page, user.alternateName);

			await expect(async () => {
				await userPersonalSitePage.userPersonalMenuButton.click();
				await userPersonalSitePage.myProfileMenuItem.click({
					timeout: 1000,
				});
			}).toPass({timeout: 5000});

			await expect(
				userPersonalSitePage.navItem('Custom Page')
			).not.toBeVisible();
		});

		await test.step('Verify non-member dashboard does NOT show group pages', async () => {
			await expect(async () => {
				await userPersonalSitePage.userPersonalMenuButton.click();
				await userPersonalSitePage.myDashboardMenuItem.click({
					timeout: 1000,
				});
			}).toPass({timeout: 5000});

			await expect(
				userPersonalSitePage.navItem('Custom Page')
			).not.toBeVisible();
		});

		await test.step('Add user to group and verify pages become visible on profile', async () => {
			await performUserSwitch(page, 'test');

			await apiHelpers.headlessAdminUser.assignUsersToUserGroup(
				userGroup.id,
				[String(user.id)]
			);

			await performUserSwitch(page, user.alternateName);

			await expect(async () => {
				await userPersonalSitePage.userPersonalMenuButton.click();
				await userPersonalSitePage.myProfileMenuItem.click({
					timeout: 1000,
				});
			}).toPass({timeout: 5000});

			await expect(userPersonalSitePage.navItem('Home')).toBeVisible();
			await expect(
				userPersonalSitePage.navItem('Custom Page')
			).toBeVisible();
		});

		await test.step('Verify dashboard still does NOT show group pages', async () => {
			await expect(async () => {
				await userPersonalSitePage.userPersonalMenuButton.click();
				await userPersonalSitePage.myDashboardMenuItem.click({
					timeout: 1000,
				});
			}).toPass({timeout: 5000});

			await expect(
				userPersonalSitePage.navItem('Custom Page')
			).not.toBeVisible();
		});
	}
);

test(
	'Can export user group members',
	{tag: '@LPD-81993'},
	async ({apiHelpers, userGroupsPage}) => {
		const userGroup = await apiHelpers.headlessAdminUser.postUserGroup({
			name: `UGUserGroup${getRandomString()}`,
		});

		const user1 = await apiHelpers.headlessAdminUser.postUserAccount();
		const user2 = await apiHelpers.headlessAdminUser.postUserAccount();

		await apiHelpers.headlessAdminUser.assignUsersToUserGroup(
			userGroup.id,
			[String(user1.id), String(user2.id)]
		);

		await userGroupsPage.goto();

		await expect(async () => {
			await userGroupsPage.optionsButton.click();

			await expect(userGroupsPage.exportImportMenuItem).toBeVisible({
				timeout: 500,
			});

			await userGroupsPage.exportImportMenuItem.click({timeout: 500});
		}).toPass({timeout: 5000});

		await expect(userGroupsPage.exportButton).toBeVisible({
			timeout: 10000,
		});

		await userGroupsPage.exportButton.click();

		await expect(
			userGroupsPage.exportImportFrame
				.getByText('successful', {exact: false})
				.first()
		).toBeVisible({timeout: 30000});
	}
);
