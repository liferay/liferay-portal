/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {dataApiHelpersTest} from '../../../fixtures/dataApiHelpersTest';
import {loginTest} from '../../../fixtures/loginTest';
import {usersAndOrganizationsPagesTest} from '../../../fixtures/usersAndOrganizationsPagesTest';
import {liferayConfig} from '../../../liferay.config';
import {OrganizationTypeSettingsPage} from '../../../pages/users-admin-web/OrganizationTypeSettingsPage';
import getRandomString from '../../../utils/getRandomString';
import {performUserSwitch, userData} from '../../../utils/performLogin';
import {waitForAlert} from '../../../utils/waitForAlert';

const test = mergeTests(
	dataApiHelpersTest,
	loginTest(),
	usersAndOrganizationsPagesTest
);

const baseUrl = liferayConfig.environment.baseUrl;

function toFriendlyURL(name: string) {
	return name.replace(/\s/g, '-').toLowerCase();
}

test(
	'View content in custom organization type site',
	{tag: '@LPD-81993'},
	async ({
		apiHelpers,
		editOrganizationPage,
		page,
		usersAndOrganizationsPage,
	}) => {
		const organizationRole1 = await apiHelpers.headlessAdminUser.postRole({
			name: `orgRole${getRandomString()}`,
			roleType: 'organization',
		});
		const organizationRole2 = await apiHelpers.headlessAdminUser.postRole({
			name: `orgRole${getRandomString()}`,
			roleType: 'organization',
		});

		const user1 = await apiHelpers.headlessAdminUser.postUserAccount();

		userData[user1.alternateName] = {
			name: user1.givenName,
			password: userData['test'].password,
			surname: user1.familyName,
		};

		const user2 = await apiHelpers.headlessAdminUser.postUserAccount();

		userData[user2.alternateName] = {
			name: user2.givenName,
			password: userData['test'].password,
			surname: user2.familyName,
		};

		const orgTypeName = `Location${getRandomString()}`;
		const orgTypeSettingsPage = new OrganizationTypeSettingsPage(page);

		await test.step('Configure custom organization type', async () => {
			await orgTypeSettingsPage.goto();

			await orgTypeSettingsPage.addButton.click();
			await page.waitForLoadState('networkidle');

			await orgTypeSettingsPage.childrenTypesInput.fill(orgTypeName);
			await orgTypeSettingsPage.nameInput.fill(orgTypeName);
			await orgTypeSettingsPage.countryEnabledCheckbox.check();
			await orgTypeSettingsPage.rootableCheckbox.check();

			await orgTypeSettingsPage.systemSettingsPage.saveAndWaitForAlert();
		});

		try {
			const organization1 =
				await apiHelpers.headlessAdminUser.postOrganization({
					name: `UnitedStates${getRandomString()}`,
				});
			const organization2 =
				await apiHelpers.headlessAdminUser.postOrganization({
					name: `Canada${getRandomString()}`,
				});

			const siteIds: Record<string, string> = {};

			for (const org of [organization1, organization2]) {
				await usersAndOrganizationsPage.goToOrganizations();

				await usersAndOrganizationsPage.organizationsTable.search(
					org.name
				);

				await expect(async () => {
					await (
						await usersAndOrganizationsPage.organizationsTable.rowActions(
							org.name
						)
					).click();

					await expect(
						usersAndOrganizationsPage.editOrganizationMenuItem
					).toBeVisible({timeout: 500});
				}).toPass({timeout: 5000});

				await usersAndOrganizationsPage.editOrganizationMenuItem.click();

				await editOrganizationPage.organizationSiteLink.click();
				await editOrganizationPage.createSiteToggle.check();
				await editOrganizationPage.saveButton.click();

				await waitForAlert(page);

				siteIds[org.name] =
					await editOrganizationPage.siteIdInput.inputValue();
			}

			const organization1SiteURL = toFriendlyURL(organization1.name);
			const organization2SiteURL = toFriendlyURL(organization2.name);

			await test.step('Add MB pages to org sites', async () => {
				for (const organization of [organization1, organization2]) {
					await apiHelpers.jsonWebServicesLayout.addLayout({
						groupId: siteIds[organization.name],
						title: 'MB Page',
					});

					const siteURL = toFriendlyURL(organization.name);

					await page.goto(`${baseUrl}/web/${siteURL}/mb-page`);
					await page.waitForLoadState('networkidle');

					await page.getByTestId('add').click();
					await page
						.getByRole('tabpanel')
						.getByPlaceholder('Search...')
						.fill('Message Boards');
					await page.keyboard.press('Enter');

					await page
						.getByTestId('addPanelTabItem')
						.filter({hasText: 'Message Boards'})
						.getByRole('button', {
							exact: true,
							name: 'Add Content',
						})
						.click();

					await expect(
						page.getByText(
							'Success:The application was added to the page.'
						)
					).toBeVisible({timeout: 10000});
				}
			});

			await test.step('Assign users to organizations and roles', async () => {
				await apiHelpers.headlessAdminUser.assignUserToOrganizationByEmailAddress(
					organization1.id,
					user2.emailAddress
				);
				await apiHelpers.headlessAdminUser.assignUserToOrganizationByEmailAddress(
					organization2.id,
					user1.emailAddress
				);
				await apiHelpers.headlessAdminUser.assignUserToOrganizationRole(
					organizationRole1.id,
					String(user1.id),
					organization2.id
				);
				await apiHelpers.headlessAdminUser.assignUserToOrganizationRole(
					organizationRole2.id,
					String(user2.id),
					organization1.id
				);
			});

			await test.step('Remove Guest and Site Member VIEW permissions on both MB sites', async () => {
				const companyId = String(
					await page.evaluate(() =>
						Liferay.ThemeDisplay.getCompanyId()
					)
				);

				const guestRole = await apiHelpers.jsonWebServicesRole.getRole(
					companyId,
					'Guest'
				);
				const siteMemberRole =
					await apiHelpers.jsonWebServicesRole.getRole(
						companyId,
						'Site Member'
					);

				for (const organization of [organization2, organization1]) {
					const groupId = siteIds[organization.name];

					for (const role of [guestRole, siteMemberRole]) {
						await apiHelpers.jsonWebServicesResourcePermissionApiHelper.removeResourcePermission(
							'VIEW',
							companyId,
							groupId,
							'com.liferay.message.boards',
							groupId,
							String(role.roleId),
							'4'
						);
					}
				}
			});

			await test.step('Verify user1 cannot see Organization2 Message Boards', async () => {
				await performUserSwitch(page, user1.alternateName);

				await page.goto(
					`${baseUrl}/web/${organization1SiteURL}/mb-page`
				);

				await expect(
					page.getByText('You do not have the required permissions.')
				).toBeVisible({timeout: 10000});
			});

			await test.step('Verify user2 cannot see Organization1 Message Boards', async () => {
				await performUserSwitch(page, user2.alternateName);

				await page.goto(
					`${baseUrl}/web/${organization2SiteURL}/mb-page`
				);

				await expect(
					page.getByText('You do not have the required permissions.')
				).toBeVisible({timeout: 10000});
			});
		}
		finally {
			await performUserSwitch(page, 'test');

			await orgTypeSettingsPage.deleteOrgType(orgTypeName);
		}
	}
);

test(
	'Can assign user groups to suborganization sites',
	{tag: '@LPD-81993'},
	async ({
		apiHelpers,
		editOrganizationPage,
		editUserPage,
		page,
		usersAndOrganizationsPage,
	}) => {
		const suffix = getRandomString();

		const userGroup1 = await apiHelpers.headlessAdminUser.postUserGroup({
			name: `UserGroup1${suffix}`,
		});
		const userGroup2 = await apiHelpers.headlessAdminUser.postUserGroup({
			name: `UserGroup2${suffix}`,
		});
		const userGroup3 = await apiHelpers.headlessAdminUser.postUserGroup({
			name: `UserGroup3${suffix}`,
		});

		const rootOrganization =
			await apiHelpers.headlessAdminUser.postOrganization({
				name: `RootOrg${suffix}`,
			});

		const organization1 =
			await apiHelpers.headlessAdminUser.postOrganization({
				name: `Organization1${suffix}`,
				parentOrganization: {id: rootOrganization.id},
			});
		const organization2 =
			await apiHelpers.headlessAdminUser.postOrganization({
				name: `Organization2${suffix}`,
				parentOrganization: {id: rootOrganization.id},
			});
		const organization3 =
			await apiHelpers.headlessAdminUser.postOrganization({
				name: `Organization3${suffix}`,
				parentOrganization: {id: organization2.id},
			});
		const organization4 =
			await apiHelpers.headlessAdminUser.postOrganization({
				name: `Organization4${suffix}`,
				parentOrganization: {id: organization2.id},
			});

		const subOrganizations = [
			organization1,
			organization2,
			organization3,
			organization4,
		];

		const siteIds: Record<string, string> = {};

		for (const organization of subOrganizations) {
			await usersAndOrganizationsPage.goToOrganizations();

			await usersAndOrganizationsPage.organizationsTable.search(
				organization.name
			);

			await expect(async () => {
				await (
					await usersAndOrganizationsPage.organizationsTable.rowActions(
						organization.name
					)
				).click();

				await expect(
					usersAndOrganizationsPage.editOrganizationMenuItem
				).toBeVisible({timeout: 500});
			}).toPass({timeout: 5000});

			await usersAndOrganizationsPage.editOrganizationMenuItem.click();

			await editOrganizationPage.organizationSiteLink.click();
			await editOrganizationPage.createSiteToggle.check();
			await editOrganizationPage.saveButton.click();

			await waitForAlert(page);

			siteIds[organization.name] =
				await editOrganizationPage.siteIdInput.inputValue();
		}

		await test.step('Assign user groups to organization sites via API', async () => {
			await apiHelpers.jsonWebServicesUserGroup.assignUserGroupsToGroup(
				siteIds[organization1.name],
				String(userGroup1.id)
			);
			await apiHelpers.jsonWebServicesUserGroup.assignUserGroupsToGroup(
				siteIds[organization2.name],
				String(userGroup2.id)
			);
			await apiHelpers.jsonWebServicesUserGroup.assignUserGroupsToGroup(
				siteIds[organization2.name],
				String(userGroup3.id)
			);
			await apiHelpers.jsonWebServicesUserGroup.assignUserGroupsToGroup(
				siteIds[organization3.name],
				String(userGroup2.id)
			);
			await apiHelpers.jsonWebServicesUserGroup.assignUserGroupsToGroup(
				siteIds[organization4.name],
				String(userGroup3.id)
			);
		});

		await test.step('Verify user in userGroup1 is only member of organization1 site', async () => {
			const user = await apiHelpers.headlessAdminUser.postUserAccount();

			await apiHelpers.headlessAdminUser.assignUsersToUserGroup(
				userGroup1.id,
				[String(user.id)]
			);

			await usersAndOrganizationsPage.goToUsers();

			await (
				await usersAndOrganizationsPage.usersTableRowLink(
					user.alternateName
				)
			).click();

			await editUserPage.membershipsLink.click();

			await expect(
				await editUserPage.membershipsSiteTableCell(organization1.name)
			).toBeVisible();
			await expect(
				editUserPage.membershipsSiteTable.getByRole('cell', {
					exact: true,
					name: organization2.name,
				})
			).not.toBeVisible();
			await expect(
				editUserPage.membershipsSiteTable.getByRole('cell', {
					exact: true,
					name: organization3.name,
				})
			).not.toBeVisible();
			await expect(
				editUserPage.membershipsSiteTable.getByRole('cell', {
					exact: true,
					name: organization4.name,
				})
			).not.toBeVisible();
		});

		await test.step('Verify user in userGroup2 is member of organization2 and organization3 sites', async () => {
			const user = await apiHelpers.headlessAdminUser.postUserAccount();

			await apiHelpers.headlessAdminUser.assignUsersToUserGroup(
				userGroup2.id,
				[String(user.id)]
			);

			await usersAndOrganizationsPage.goToUsers();

			await (
				await usersAndOrganizationsPage.usersTableRowLink(
					user.alternateName
				)
			).click();

			await editUserPage.membershipsLink.click();

			await expect(
				await editUserPage.membershipsSiteTableCell(organization2.name)
			).toBeVisible();
			await expect(
				await editUserPage.membershipsSiteTableCell(organization3.name)
			).toBeVisible();
			await expect(
				editUserPage.membershipsSiteTable.getByRole('cell', {
					exact: true,
					name: organization1.name,
				})
			).not.toBeVisible();
			await expect(
				editUserPage.membershipsSiteTable.getByRole('cell', {
					exact: true,
					name: organization4.name,
				})
			).not.toBeVisible();
		});
	}
);
