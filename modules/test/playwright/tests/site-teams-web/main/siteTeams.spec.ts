/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {dataApiHelpersTest} from '../../../fixtures/dataApiHelpersTest';
import {featureFlagsTest} from '../../../fixtures/featureFlagsTest';
import {isolatedSiteTest} from '../../../fixtures/isolatedSiteTest';
import {loginTest} from '../../../fixtures/loginTest';
import {siteSettingsPagesTest} from '../../../fixtures/siteSettingsPagesTest';
import {usersAndOrganizationsPagesTest} from '../../../fixtures/usersAndOrganizationsPagesTest';
import getRandomString from '../../../utils/getRandomString';
import {performUserSwitch, userData} from '../../../utils/performLogin';
import {waitForAlert} from '../../../utils/waitForAlert';
import getPageDefinition from '../../layout-content-page-editor-web/main/utils/getPageDefinition';
import getWidgetDefinition from '../../layout-content-page-editor-web/main/utils/getWidgetDefinition';
import {siteTeamsPagesTest} from './fixtures/siteTeamsPagesTest';

const test = mergeTests(
	dataApiHelpersTest,
	featureFlagsTest({
		'LPD-35443': {enabled: true},
		'LPS-178052': {enabled: true},
	}),
	isolatedSiteTest,
	loginTest(),
	siteSettingsPagesTest,
	siteTeamsPagesTest,
	usersAndOrganizationsPagesTest
);

test(
	'Can Add / Update / Delete a site team',
	{tag: ['@LPD-71199']},
	async ({page, site, teamsPage}) => {
		page.on('dialog', (dialog) => dialog.accept());

		const team = {
			teamDescription: getRandomString(),
			teamName: getRandomString(),
		};

		await teamsPage.goTo(site.friendlyUrlPath);

		await teamsPage.newTeamButton.click();
		await teamsPage.newTeam(team);

		await expect(
			await teamsPage.teamsTable.cellLink(team.teamName)
		).toBeVisible();

		await expect(async () => {
			await (
				await teamsPage.teamsTable.rowActions(team.teamName)
			).click();

			await expect(teamsPage.editButton).toBeVisible({timeout: 500});
		}).toPass({timeout: 5000});

		await teamsPage.editButton.click();

		await expect(teamsPage.nameInput).toHaveValue(team.teamName);
		await expect(teamsPage.descriptionInput).toHaveValue(
			team.teamDescription
		);

		const newTeam = {
			teamDescription: getRandomString(),
			teamName: getRandomString(),
		};

		await teamsPage.nameInput.fill(newTeam.teamName);
		await teamsPage.descriptionInput.fill(newTeam.teamDescription);
		await teamsPage.saveButton.click();

		await waitForAlert(page);

		await expect(teamsPage.teamsTable.cell(team.teamName)).toHaveCount(0);
		await expect(
			await teamsPage.teamsTable.cellLink(newTeam.teamName)
		).toBeVisible();

		await expect(async () => {
			await (
				await teamsPage.teamsTable.rowActions(newTeam.teamName)
			).click();

			await expect(teamsPage.editButton).toBeVisible({timeout: 500});
		}).toPass({timeout: 5000});

		await teamsPage.editButton.click();

		await expect(teamsPage.nameInput).toHaveValue(newTeam.teamName);
		await expect(teamsPage.descriptionInput).toHaveValue(
			newTeam.teamDescription
		);

		await teamsPage.goTo(site.friendlyUrlPath);

		await expect(async () => {
			await (
				await teamsPage.teamsTable.rowActions(newTeam.teamName)
			).click();

			await expect(teamsPage.deleteButton).toBeVisible({timeout: 500});
		}).toPass({timeout: 5000});

		await teamsPage.deleteButton.click();

		await waitForAlert(page);

		await expect(teamsPage.teamsTable.cell(team.teamName)).toHaveCount(0);
		await expect(teamsPage.teamsTable.cell(newTeam.teamName)).toHaveCount(
			0
		);
	}
);

test(
	'Can add multiple teams to a site',
	{tag: ['@LPD-71199']},
	async ({page, site, teamsPage}) => {
		page.on('dialog', (dialog) => dialog.accept());

		const teams = [
			{
				teamDescription: getRandomString(),
				teamName: getRandomString(),
			},
			{
				teamDescription: getRandomString(),
				teamName: getRandomString(),
			},
			{
				teamDescription: getRandomString(),
				teamName: getRandomString(),
			},
		];

		await teamsPage.goTo(site.friendlyUrlPath);

		for (const team of teams) {
			await teamsPage.newTeamButton.click();
			await teamsPage.newTeam(team);

			await expect(
				await teamsPage.teamsTable.cellLink(team.teamName)
			).toBeVisible();
		}

		await teamsPage.teamsTable.selectAllItemsCheckbox.click();
		await teamsPage.deleteButton.click();

		for (const team of teams) {
			await expect(teamsPage.teamsTable.cell(team.teamName)).toHaveCount(
				0
			);
		}
	}
);

test(
	'Can assign / Unassing a user to a site team',
	{tag: ['@LPD-71199']},
	async ({apiHelpers, page, selectUserPage, site, teamsPage, usersPage}) => {
		page.on('dialog', (dialog) => dialog.accept());

		const user = await apiHelpers.headlessAdminUser.postUserAccount();

		apiHelpers.jsonWebServicesUser.assignUsersToSite(site.id, user.id);

		const team = {
			teamDescription: getRandomString(),
			teamName: getRandomString(),
		};

		await teamsPage.goTo(site.friendlyUrlPath);

		await teamsPage.newTeamButton.click();
		await teamsPage.newTeam(team);

		await expect(
			await teamsPage.teamsTable.cellLink(team.teamName)
		).toBeVisible();

		await (await teamsPage.teamsTable.cellLink(team.teamName)).click();

		await expect(usersPage.usersTable.searchInput).toBeEnabled();

		await usersPage.usersTable.changeView('Table');

		await expect(usersPage.noUsersMessage).toBeVisible();

		await expect(async () => {
			await usersPage.newButton.click();

			await expect(selectUserPage.addButton).toBeVisible({timeout: 2000});
		}).toPass({timeout: 5000});

		await (await selectUserPage.usersTable.rowCheckbox(user.name)).click();
		await selectUserPage.addButton.click();

		await expect(usersPage.usersTable.cell(user.name)).toBeVisible();

		await page.reload();

		await expect(usersPage.usersTable.cell(user.name)).toBeVisible();

		await expect(async () => {
			await (await usersPage.usersTable.rowActions(user.name)).click();

			await expect(usersPage.deleteButton).toBeVisible({timeout: 200});
		}).toPass({timeout: 1000});

		await usersPage.deleteButton.click();

		await expect(usersPage.usersTable.cell(user.name)).toHaveCount(0);

		await page.reload();

		await expect(usersPage.usersTable.cell(user.name)).toHaveCount(0);
	}
);

test(
	'Can search users connected to a site team',
	{tag: ['@LPD-71199']},
	async ({apiHelpers, page, selectUserPage, site, teamsPage, usersPage}) => {
		page.on('dialog', (dialog) => dialog.accept());

		const user1 = await apiHelpers.headlessAdminUser.postUserAccount();

		apiHelpers.jsonWebServicesUser.assignUsersToSite(site.id, user1.id);

		const user2 = await apiHelpers.headlessAdminUser.postUserAccount();

		apiHelpers.jsonWebServicesUser.assignUsersToSite(site.id, user2.id);

		const team = {
			teamDescription: getRandomString(),
			teamName: getRandomString(),
		};

		await teamsPage.goTo(site.friendlyUrlPath);

		await teamsPage.newTeamButton.click();
		await teamsPage.newTeam(team);

		await expect(
			await teamsPage.teamsTable.cellLink(team.teamName)
		).toBeVisible();

		await (await teamsPage.teamsTable.cellLink(team.teamName)).click();

		await expect(usersPage.usersTable.searchInput).toBeEnabled();

		await usersPage.usersTable.changeView('Table');

		await expect(usersPage.noUsersMessage).toBeVisible();

		await expect(async () => {
			await usersPage.newButton.click();

			await expect(selectUserPage.addButton).toBeVisible({timeout: 2000});
		}).toPass({timeout: 5000});

		await (await selectUserPage.usersTable.rowCheckbox(user1.name)).click();
		await (await selectUserPage.usersTable.rowCheckbox(user2.name)).click();
		await selectUserPage.addButton.click();

		await expect(usersPage.usersTable.cell(user1.name)).toBeVisible();
		await expect(usersPage.usersTable.cell(user2.name)).toBeVisible();

		await usersPage.usersTable.search(user1.name);

		await expect(usersPage.usersTable.cell(user1.name)).toBeVisible();
		await expect(usersPage.usersTable.cell(user2.name)).toHaveCount(0);

		await usersPage.usersTable.search(user2.name);

		await expect(usersPage.usersTable.cell(user1.name)).toHaveCount(0);
		await expect(usersPage.usersTable.cell(user2.name)).toBeVisible();

		await usersPage.usersTable.search(getRandomString());

		await expect(usersPage.usersTable.cell(user1.name)).toHaveCount(0);
		await expect(usersPage.usersTable.cell(user2.name)).toHaveCount(0);

		await usersPage.usersTable.search('');

		await expect(usersPage.usersTable.cell(user1.name)).toBeVisible();
		await expect(usersPage.usersTable.cell(user2.name)).toBeVisible();
	}
);

test(
	'Can assign / Unassing a user group to a site team',
	{tag: ['@LPD-71199']},
	async ({
		apiHelpers,
		page,
		selectUserGroupPage,
		site,
		teamsPage,
		userGroupsPage,
	}) => {
		page.on('dialog', (dialog) => dialog.accept());

		const userGroup = await apiHelpers.headlessAdminUser.postUserGroup();

		apiHelpers.jsonWebServicesUserGroup.assignUserGroupsToGroup(
			site.id,
			String(userGroup.id)
		);

		const team = {
			teamDescription: getRandomString(),
			teamName: getRandomString(),
		};

		await teamsPage.goTo(site.friendlyUrlPath);

		await teamsPage.newTeamButton.click();
		await teamsPage.newTeam(team);

		await expect(
			await teamsPage.teamsTable.cellLink(team.teamName)
		).toBeVisible();

		await (await teamsPage.teamsTable.cellLink(team.teamName)).click();

		await teamsPage.userGroupTab.click();

		await expect(userGroupsPage.userGroupsTable.searchInput).toBeEnabled();

		await userGroupsPage.userGroupsTable.changeView('Table');

		await expect(userGroupsPage.noUserGroupsMessage).toBeVisible();

		await expect(async () => {
			await userGroupsPage.newButton.click();

			await expect(selectUserGroupPage.addButton).toBeVisible({
				timeout: 2000,
			});
		}).toPass({timeout: 5000});

		await selectUserGroupPage.userGroupsTable.changeView('Table');

		await (
			await selectUserGroupPage.userGroupsTable.rowCheckbox(
				userGroup.name
			)
		).click();
		await selectUserGroupPage.addButton.click();

		await expect(
			userGroupsPage.userGroupsTable.cell(userGroup.name)
		).toBeVisible();

		await page.reload();

		await expect(
			userGroupsPage.userGroupsTable.cell(userGroup.name)
		).toBeVisible();

		await expect(userGroupsPage.deleteButton).toBeVisible({timeout: 200});

		await userGroupsPage.deleteButton.click();

		await expect(
			userGroupsPage.userGroupsTable.cell(userGroup.name)
		).toHaveCount(0);

		await page.reload();

		await expect(
			userGroupsPage.userGroupsTable.cell(userGroup.name)
		).toHaveCount(0);
	}
);

test(
	'Can search user groups connected to a site team',
	{tag: ['@LPD-71199']},
	async ({
		apiHelpers,
		selectUserGroupPage,
		site,
		teamsPage,
		userGroupsPage,
	}) => {
		const userGroup1 = await apiHelpers.headlessAdminUser.postUserGroup();

		apiHelpers.jsonWebServicesUserGroup.assignUserGroupsToGroup(
			site.id,
			String(userGroup1.id)
		);

		const userGroup2 = await apiHelpers.headlessAdminUser.postUserGroup();

		apiHelpers.jsonWebServicesUserGroup.assignUserGroupsToGroup(
			site.id,
			String(userGroup2.id)
		);

		const team = {
			teamDescription: getRandomString(),
			teamName: getRandomString(),
		};

		await teamsPage.goTo(site.friendlyUrlPath);

		await teamsPage.newTeamButton.click();
		await teamsPage.newTeam(team);

		await expect(
			await teamsPage.teamsTable.cellLink(team.teamName)
		).toBeVisible();

		await (await teamsPage.teamsTable.cellLink(team.teamName)).click();

		await teamsPage.userGroupTab.click();

		await expect(userGroupsPage.userGroupsTable.searchInput).toBeEnabled();

		await userGroupsPage.userGroupsTable.changeView('Table');

		await expect(userGroupsPage.noUserGroupsMessage).toBeVisible();

		await expect(async () => {
			await userGroupsPage.newButton.click();

			await expect(selectUserGroupPage.addButton).toBeVisible({
				timeout: 2000,
			});
		}).toPass({timeout: 5000});

		await selectUserGroupPage.userGroupsTable.changeView('Table');

		await (
			await selectUserGroupPage.userGroupsTable.rowCheckbox(
				userGroup1.name
			)
		).click();
		await (
			await selectUserGroupPage.userGroupsTable.rowCheckbox(
				userGroup2.name
			)
		).click();
		await selectUserGroupPage.addButton.click();

		await expect(
			userGroupsPage.userGroupsTable.cell(userGroup1.name)
		).toBeVisible();
		await expect(
			userGroupsPage.userGroupsTable.cell(userGroup2.name)
		).toBeVisible();

		await userGroupsPage.userGroupsTable.search(userGroup1.name);

		await expect(
			userGroupsPage.userGroupsTable.cell(userGroup1.name)
		).toBeVisible();
		await expect(
			userGroupsPage.userGroupsTable.cell(userGroup2.name)
		).toHaveCount(0);

		await userGroupsPage.userGroupsTable.search(userGroup2.name);

		await expect(
			userGroupsPage.userGroupsTable.cell(userGroup1.name)
		).toHaveCount(0);
		await expect(
			userGroupsPage.userGroupsTable.cell(userGroup2.name)
		).toBeVisible();

		await userGroupsPage.userGroupsTable.search(getRandomString());

		await expect(
			userGroupsPage.userGroupsTable.cell(userGroup1.name)
		).toHaveCount(0);
		await expect(
			userGroupsPage.userGroupsTable.cell(userGroup2.name)
		).toHaveCount(0);

		await userGroupsPage.userGroupsTable.search('');

		await expect(
			userGroupsPage.userGroupsTable.cell(userGroup1.name)
		).toBeVisible();
		await expect(
			userGroupsPage.userGroupsTable.cell(userGroup2.name)
		).toBeVisible();
	}
);

test(
	'The tooltip of the back button of a Team view or Team edit mode is Go to Teams',
	{tag: ['@LPD-71199', '@LPS-177717']},
	async ({page, site, teamsPage}) => {
		const team = {
			teamDescription: getRandomString(),
			teamName: getRandomString(),
		};

		await test.step('using mouse hover', async () => {
			await teamsPage.goTo(site.friendlyUrlPath);

			await teamsPage.newTeamButton.click();
			await teamsPage.backButton.hover();

			await expect(teamsPage.backTooltip).toBeVisible();

			await teamsPage.newTeam(team);

			await expect(
				await teamsPage.teamsTable.cellLink(team.teamName)
			).toBeVisible();

			await (await teamsPage.teamsTable.cellLink(team.teamName)).click();
			await teamsPage.backButton.hover();

			await expect(teamsPage.backTooltip).toBeVisible();

			await teamsPage.backButton.click();

			await expect(async () => {
				await (
					await teamsPage.teamsTable.rowActions(team.teamName)
				).click();

				await expect(teamsPage.editButton).toBeVisible({timeout: 200});
			}).toPass({timeout: 5000});

			await teamsPage.editButton.click();
			await teamsPage.backButton.hover();

			await expect(teamsPage.backTooltip).toBeVisible();
		});

		await test.step('using keyboard', async () => {
			await teamsPage.goTo(site.friendlyUrlPath);

			await teamsPage.newTeamButton.click();
			await page.getByTestId('header').click();

			await expect(async () => {
				await page.keyboard.press('Tab');

				await expect(teamsPage.backTooltip).toBeVisible({
					timeout: 1000,
				});
			}).toPass({timeout: 20000});

			await teamsPage.backButton.click();

			await (await teamsPage.teamsTable.cellLink(team.teamName)).click();
			await page.getByTestId('header').click();

			await expect(async () => {
				await page.keyboard.press('Tab');

				await expect(teamsPage.backTooltip).toBeVisible({
					timeout: 1000,
				});
			}).toPass({timeout: 20000});

			await expect(teamsPage.backTooltip).toBeVisible();

			await teamsPage.backButton.click();

			await expect(async () => {
				await (
					await teamsPage.teamsTable.rowActions(team.teamName)
				).click();

				await expect(teamsPage.editButton).toBeVisible({timeout: 200});
			}).toPass({timeout: 2000});

			await teamsPage.editButton.click();
			await page.getByTestId('header').click();

			await expect(async () => {
				await page.keyboard.press('Tab');

				await expect(teamsPage.backTooltip).toBeVisible({
					timeout: 1000,
				});
			}).toPass({timeout: 20000});

			await expect(teamsPage.backTooltip).toBeVisible();
		});
	}
);

test(
	'Test XSS vulnerability when adding site team with malicious name',
	{tag: ['@LPD-71199']},
	async ({page, site, siteSettingsPage, teamsPage}) => {
		page.on('dialog', async (dialog) => {
			if (dialog.type() === 'alert') {
				throw new Error('XSS');
			}
		});

		const team = {
			teamName: '<script>alert(123);</script>',
		};

		await teamsPage.goTo(site.friendlyUrlPath);

		await teamsPage.newTeamButton.click();
		await teamsPage.newTeam(team);

		await expect(teamsPage.teamsTable.cell(team.teamName)).toBeVisible();

		await siteSettingsPage.goToSiteSetting(
			'Users',
			'Default User Associations',
			site.friendlyUrlPath
		);

		await expect(async () => {
			await page
				.getByRole('heading', {name: 'Teams Select'})
				.getByLabel('Select')
				.click();

			await expect(
				page
					.frameLocator('iframe[title="Select Team"]')
					.getByRole('cell', {name: team.teamName})
			).toBeVisible({timeout: 1000});
		}).toPass({timeout: 5000});

		await expect(async () => {
			await page
				.frameLocator('iframe[title="Select Team"]')
				.getByRole('cell', {name: team.teamName})
				.click();

			await expect(
				page.getByRole('cell', {name: team.teamName})
			).toBeVisible({timeout: 1000});
		}).toPass({timeout: 5000});
	}
);

test(
	'A site team can have dedicated permissions',
	{tag: ['@LPD-71199']},
	async ({apiHelpers, page, site, teamsPage}) => {
		const createUserAndAssignToSite = async () => {
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

			return user;
		};

		const user1 = await createUserAndAssignToSite();
		const user2 = await createUserAndAssignToSite();

		const newTeam = {
			teamName: getRandomString(),
		};

		await teamsPage.goTo(site.friendlyUrlPath);

		await teamsPage.newTeamButton.click();
		await teamsPage.newTeam(newTeam);

		await expect(teamsPage.teamsTable.cell(newTeam.teamName)).toBeVisible();

		const team = await apiHelpers.jsonWebServicesTeam.getTeam(
			site.id,
			newTeam.teamName
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

		for (const roleName of ['Guest', 'Site Member']) {
			const role = await apiHelpers.headlessAdminUser.getRoleByName(
				roleName,
				'rolePermissions'
			);

			await apiHelpers.jsonWebServicesResourcePermissionApiHelper.setIndividualResourcePermissions(
				[],
				companyId,
				'0',
				'com.liferay.portal.kernel.model.Layout',
				String(layout.id),
				String(role.id)
			);
		}

		await apiHelpers.jsonWebServicesResourcePermissionApiHelper.setIndividualResourcePermissions(
			['VIEW'],
			companyId,
			String(team.groupId),
			'com.liferay.portal.kernel.model.Layout',
			String(layout.id),
			String(Number(team.teamId) + 1)
		);

		await performUserSwitch(page, user1.alternateName);

		await page.goto(`/web/${site.name}/${layout.friendlyUrlPath}`);

		await expect(
			page
				.getByRole('heading', {name: '404'})
				.or(page.getByText('requested resource could not be found'))
		).toBeVisible();

		await performUserSwitch(page, user2.alternateName);

		await page.goto(`/web/${site.name}/${layout.friendlyUrlPath}`);

		await expect(
			page
				.getByRole('heading', {name: '404'})
				.or(page.getByText('requested resource could not be found'))
		).toHaveCount(0);
	}
);

test(
	'Dropdown menu is hidden if user membership is due to inheritance',
	{tag: ['@LPD-80349']},
	async ({
		apiHelpers,
		page,
		selectUserGroupPage,
		site,
		teamsPage,
		userGroupsPage,
		usersPage,
	}) => {
		const userGroup = await apiHelpers.headlessAdminUser.postUserGroup();

		await apiHelpers.jsonWebServicesUserGroup.assignUserGroupsToGroup(
			site.id,
			String(userGroup.id)
		);

		const user1 = await apiHelpers.headlessAdminUser.postUserAccount();
		const user2 = await apiHelpers.headlessAdminUser.postUserAccount();

		await apiHelpers.headlessAdminUser.assignUsersToUserGroup(
			userGroup.id,
			[user1.id]
		);

		const newTeam = {
			teamDescription: getRandomString(),
			teamName: getRandomString(),
		};

		await teamsPage.goTo(site.friendlyUrlPath);

		await teamsPage.newTeamButton.click();
		await teamsPage.newTeam(newTeam);

		await expect(
			await teamsPage.teamsTable.cellLink(newTeam.teamName)
		).toBeVisible();

		const team = await apiHelpers.jsonWebServicesTeam.getTeam(
			site.id,
			newTeam.teamName
		);

		await apiHelpers.jsonWebServicesUser.addTeamUsers(team.teamId, [
			user2.id,
		]);

		await (await teamsPage.teamsTable.cellLink(newTeam.teamName)).click();
		await teamsPage.userGroupTab.click();

		await expect(userGroupsPage.userGroupsTable.searchInput).toBeEnabled();

		await userGroupsPage.userGroupsTable.changeView('Table');

		await expect(userGroupsPage.noUserGroupsMessage).toBeVisible();

		await expect(async () => {
			await userGroupsPage.newButton.click();

			await expect(selectUserGroupPage.addButton).toBeVisible({
				timeout: 2000,
			});
		}).toPass({timeout: 5000});

		await selectUserGroupPage.userGroupsTable.changeView('Table');

		await (
			await selectUserGroupPage.userGroupsTable.rowCheckbox(
				userGroup.name
			)
		).click();
		await selectUserGroupPage.addButton.click();

		await expect(
			userGroupsPage.userGroupsTable.cell(userGroup.name)
		).toBeVisible();

		await teamsPage.usersTab.click();

		await expect(usersPage.usersTable.searchInput).toBeEnabled();

		await usersPage.usersTable.changeView('Table');

		await expect(usersPage.usersTable.cell(user1.name)).toBeVisible();
		await expect(usersPage.usersTable.cell(user2.name)).toBeVisible();

		await expect(
			await usersPage.usersTable.rowActions(user1.name)
		).toHaveCount(0);

		await expect(async () => {
			await (await usersPage.usersTable.rowCheckbox(user2.name)).check();

			await expect(usersPage.deleteButton).toBeVisible({timeout: 200});
			await expect(usersPage.deleteButton).toBeEnabled({timeout: 200});
		}).toPass({timeout: 1000});

		await expect(
			await usersPage.usersTable.rowCheckbox(user1.name)
		).toBeDisabled();

		await page.reload();

		await expect(async () => {
			await (await usersPage.usersTable.rowActions(user2.name)).click();

			await expect(usersPage.deleteButton).toBeVisible({timeout: 200});
		}).toPass({timeout: 1000});
	}
);

test(
	'Team user is visible on staging site',
	{tag: ['@LPD-81993', '@LPS-115692']},
	async ({apiHelpers, page, selectUserPage, site, teamsPage, usersPage}) => {
		const user = await apiHelpers.headlessAdminUser.postUserAccount();

		await apiHelpers.jsonWebServicesUser.assignUsersToSite(
			site.id,
			String(user.id)
		);

		const teamName = `Team${getRandomString()}`;

		await teamsPage.goTo(site.friendlyUrlPath);

		await teamsPage.newTeamButton.click();
		await teamsPage.newTeam({teamName});

		await (await teamsPage.teamsTable.cellLink(teamName)).click();

		await expect(async () => {
			await usersPage.newButton.click();

			await expect(selectUserPage.addButton).toBeVisible({
				timeout: 2000,
			});
		}).toPass({timeout: 5000});

		await (await selectUserPage.usersTable.rowCheckbox(user.name)).click();
		await selectUserPage.addButton.click();

		await expect(page.getByText(user.name, {exact: false})).toBeVisible();

		await test.step('Verify team user is still visible after enabling staging', async () => {
			await apiHelpers.jsonWebServicesStaging.enableLocalStaging({
				groupId: site.id,
			});

			await teamsPage.goTo(site.friendlyUrlPath);

			await (await teamsPage.teamsTable.cellLink(teamName)).click();

			await expect(
				page.getByText(user.name, {exact: false})
			).toBeVisible();
		});

		// Wait for background tasks to complete before cleaning up.

		await page.waitForTimeout(2000);
	}
);

test(
	'Can search users who inherit membership based on user group membership',
	{tag: ['@LPD-82822']},
	async ({
		apiHelpers,
		page,
		selectUserGroupPage,
		selectUserPage,
		site,
		teamsPage,
		userGroupsPage,
		usersPage,
	}) => {
		page.on('dialog', (dialog) => dialog.accept());

		const user1 = await apiHelpers.headlessAdminUser.postUserAccount({
			familyName: 'Parker',
			givenName: 'Peter',
		});

		await apiHelpers.jsonWebServicesUser.assignUsersToSite(
			site.id,
			user1.id
		);

		const userGroup = await apiHelpers.headlessAdminUser.postUserGroup();

		await apiHelpers.jsonWebServicesUserGroup.assignUserGroupsToGroup(
			site.id,
			String(userGroup.id)
		);

		const user2 = await apiHelpers.headlessAdminUser.postUserAccount({
			familyName: 'Porker',
			givenName: 'Pete',
		});

		await apiHelpers.headlessAdminUser.assignUsersToUserGroup(
			userGroup.id,
			[user2.id]
		);

		const team = {
			teamName: getRandomString(),
		};

		await teamsPage.goTo(site.friendlyUrlPath);

		await teamsPage.newTeamButton.click();
		await teamsPage.newTeam(team);

		await expect(
			await teamsPage.teamsTable.cellLink(team.teamName)
		).toBeVisible();

		await (await teamsPage.teamsTable.cellLink(team.teamName)).click();

		await expect(usersPage.usersTable.searchInput).toBeEnabled();

		const newTeam = await apiHelpers.jsonWebServicesTeam.getTeam(
			site.id,
			team.teamName
		);

		await apiHelpers.jsonWebServicesUser.addTeamUsers(newTeam.teamId, [
			user1.id,
		]);

		await teamsPage.userGroupTab.click();

		await expect(userGroupsPage.userGroupsTable.searchInput).toBeEnabled();

		await userGroupsPage.userGroupsTable.changeView('Table');

		await expect(userGroupsPage.noUserGroupsMessage).toBeVisible();

		await expect(async () => {
			await userGroupsPage.newButton.click();

			await expect(selectUserGroupPage.addButton).toBeVisible({
				timeout: 2000,
			});
		}).toPass({timeout: 5000});

		await selectUserGroupPage.userGroupsTable.changeView('Table');

		await (
			await selectUserGroupPage.userGroupsTable.rowCheckbox(
				userGroup.name
			)
		).check();

		await selectUserGroupPage.addButton.click();

		await expect(
			userGroupsPage.userGroupsTable.cell(userGroup.name)
		).toBeVisible();

		await teamsPage.usersTab.click();

		await usersPage.usersTable.changeView('Table');

		await expect(usersPage.usersTable.cell(user1.name)).toBeVisible();
		await expect(usersPage.usersTable.cell(user2.name)).toBeVisible();

		await expect(async () => {
			await usersPage.newButton.click();

			await expect(selectUserPage.addButton).toBeVisible({timeout: 2000});
		}).toPass({timeout: 5000});

		await selectUserPage.usersTable.changeView('Table');

		await selectUserPage.usersTable.search(`"${user1.name}"`);

		await expect(selectUserPage.usersTable.cell(user1.name)).toBeVisible();
		await expect(selectUserPage.usersTable.cell(user2.name)).toHaveCount(0);

		await selectUserPage.usersTable.search(`"${user2.name}"`);

		await expect(selectUserPage.usersTable.cell(user1.name)).toHaveCount(0);
		await expect(selectUserPage.usersTable.cell(user2.name)).toBeVisible();

		await selectUserPage.usersTable.search(`"${user2.name}a"`);

		await expect(selectUserPage.usersTable.cell(user1.name)).toHaveCount(0);
		await expect(selectUserPage.usersTable.cell(user2.name)).toHaveCount(0);

		await selectUserPage.usersTable.search(`${user2.givenName}`);

		await expect(selectUserPage.usersTable.cell(user1.name)).toBeVisible();
		await expect(selectUserPage.usersTable.cell(user2.name)).toBeVisible();
	}
);

test(
	'Checkbox is hidden and user is not selected if user membership is due to inheritance',
	{tag: ['@LPD-82647']},
	async ({
		apiHelpers,
		page,
		selectUserGroupPage,
		site,
		teamsPage,
		userGroupsPage,
		usersPage,
	}) => {
		page.on('dialog', (dialog) => dialog.accept());

		const userGroup = await apiHelpers.headlessAdminUser.postUserGroup();

		await apiHelpers.jsonWebServicesUserGroup.assignUserGroupsToGroup(
			site.id,
			String(userGroup.id)
		);

		const user1 = await apiHelpers.headlessAdminUser.postUserAccount();
		const user2 = await apiHelpers.headlessAdminUser.postUserAccount();

		await apiHelpers.headlessAdminUser.assignUsersToUserGroup(
			userGroup.id,
			[user1.id]
		);

		const newTeam = {
			teamDescription: getRandomString(),
			teamName: getRandomString(),
		};

		await teamsPage.goTo(site.friendlyUrlPath);

		await teamsPage.newTeamButton.click();
		await teamsPage.newTeam(newTeam);

		await expect(
			await teamsPage.teamsTable.cellLink(newTeam.teamName)
		).toBeVisible();

		const team = await apiHelpers.jsonWebServicesTeam.getTeam(
			site.id,
			newTeam.teamName
		);

		await apiHelpers.jsonWebServicesUser.addTeamUsers(team.teamId, [
			user2.id,
		]);

		await (await teamsPage.teamsTable.cellLink(newTeam.teamName)).click();
		await teamsPage.userGroupTab.click();

		await expect(userGroupsPage.userGroupsTable.searchInput).toBeEnabled();

		await userGroupsPage.userGroupsTable.changeView('Table');

		await expect(userGroupsPage.noUserGroupsMessage).toBeVisible();

		await expect(async () => {
			await userGroupsPage.newButton.click();

			await expect(selectUserGroupPage.addButton).toBeVisible({
				timeout: 2000,
			});
		}).toPass({timeout: 5000});

		await selectUserGroupPage.userGroupsTable.changeView('Table');

		await (
			await selectUserGroupPage.userGroupsTable.rowCheckbox(
				userGroup.name
			)
		).click();
		await selectUserGroupPage.addButton.click();

		await expect(
			userGroupsPage.userGroupsTable.cell(userGroup.name)
		).toBeVisible();

		await teamsPage.usersTab.click();

		await expect(usersPage.usersTable.searchInput).toBeEnabled();

		await usersPage.usersTable.changeView('Table');

		await expect(usersPage.usersTable.cell(user1.name)).toBeVisible();
		await expect(usersPage.usersTable.cell(user2.name)).toBeVisible();

		await expect(
			await usersPage.usersTable.rowCheckbox(user1.name)
		).toBeDisabled();
		await expect(
			await usersPage.usersTable.rowCheckbox(user2.name)
		).toBeVisible();

		await usersPage.usersTable.selectAllItemsCheckbox.check();

		await expect(
			page.getByText('1 of 2 Items Selected', {exact: true})
		).toBeVisible();

		await expect(async () => {
			await expect(usersPage.deleteButton).toBeVisible({timeout: 200});
			await usersPage.deleteButton.click();
		}).toPass({timeout: 5000});

		await waitForAlert(page);

		await expect(usersPage.usersTable.cell(user1.name)).toBeVisible();
		await expect(usersPage.usersTable.cell(user2.name)).toHaveCount(0);
	}
);

test(
	'Inherited team members are labeled and removal affects only direct memberships',
	{tag: ['@LPD-87301', '@LPD-99754']},
	async ({
		apiHelpers,
		page,
		selectUserGroupPage,
		site,
		teamsPage,
		userGroupsPage,
		usersPage,
	}) => {
		const userGroup = await apiHelpers.headlessAdminUser.postUserGroup();

		await apiHelpers.jsonWebServicesUserGroup.assignUserGroupsToGroup(
			site.id,
			String(userGroup.id)
		);

		const directUser = await apiHelpers.headlessAdminUser.postUserAccount();
		const inheritedUser =
			await apiHelpers.headlessAdminUser.postUserAccount();

		await apiHelpers.headlessAdminUser.assignUsersToUserGroup(
			userGroup.id,
			[inheritedUser.id]
		);

		const newTeam = {
			teamDescription: getRandomString(),
			teamName: getRandomString(),
		};

		await teamsPage.goTo(site.friendlyUrlPath);

		await teamsPage.newTeamButton.click();
		await teamsPage.newTeam(newTeam);

		await expect(
			await teamsPage.teamsTable.cellLink(newTeam.teamName)
		).toBeVisible();

		const team = await apiHelpers.jsonWebServicesTeam.getTeam(
			site.id,
			newTeam.teamName
		);

		await apiHelpers.jsonWebServicesUser.addTeamUsers(team.teamId, [
			directUser.id,
		]);

		await (await teamsPage.teamsTable.cellLink(newTeam.teamName)).click();
		await teamsPage.userGroupTab.click();

		await expect(userGroupsPage.userGroupsTable.searchInput).toBeEnabled();

		await userGroupsPage.userGroupsTable.changeView('Table');

		await expect(userGroupsPage.noUserGroupsMessage).toBeVisible();

		await expect(async () => {
			await userGroupsPage.newButton.click();

			await expect(selectUserGroupPage.addButton).toBeVisible({
				timeout: 2000,
			});
		}).toPass({timeout: 5000});

		await selectUserGroupPage.userGroupsTable.changeView('Table');

		await (
			await selectUserGroupPage.userGroupsTable.rowCheckbox(
				userGroup.name
			)
		).click();
		await selectUserGroupPage.addButton.click();

		await expect(
			userGroupsPage.userGroupsTable.cell(userGroup.name)
		).toBeVisible();

		await teamsPage.usersTab.click();

		await expect(usersPage.usersTable.searchInput).toBeEnabled();

		await usersPage.usersTable.changeView('Table');

		await expect(usersPage.usersTable.cell(directUser.name)).toBeVisible();
		await expect(
			usersPage.usersTable.cell(inheritedUser.name)
		).toBeVisible();

		await expect(
			page.getByText(
				'Inherited memberships from an organization or a user ' +
					'group are managed at their source and cannot be ' +
					'removed here'
			)
		).toBeVisible();

		const directRow = await usersPage.usersTable.row(
			1,
			directUser.name,
			true
		);

		await expect(
			directRow.row.getByText('Direct', {exact: true})
		).toBeVisible();

		const inheritedRow = await usersPage.usersTable.row(
			1,
			inheritedUser.name,
			true
		);

		const inheritedLabel = inheritedRow.row.getByText('Inherited', {
			exact: true,
		});

		await expect(inheritedLabel).toBeVisible();
		await expect(inheritedLabel).not.toHaveCSS('text-align', 'center');

		let confirmationMessage = '';

		page.once('dialog', (dialog) => {
			confirmationMessage = dialog.message();

			dialog.accept();
		});

		await expect(async () => {
			await (
				await usersPage.usersTable.rowActions(directUser.name)
			).click();

			await expect(usersPage.deleteButton).toBeVisible({timeout: 500});
		}).toPass({timeout: 5000});

		await usersPage.deleteButton.click();

		await waitForAlert(page);

		expect(confirmationMessage).toContain(
			'Only direct memberships will be removed'
		);

		await expect(usersPage.usersTable.cell(directUser.name)).toHaveCount(0);
		await expect(
			usersPage.usersTable.cell(inheritedUser.name)
		).toBeVisible();
	}
);
