/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {FrameLocator, Locator, Page, expect} from '@playwright/test';

import {DataTablePage} from '../account-admin-web/DataTablePage';
import {ApplicationsMenuPage} from '../product-navigation-applications-menu/ApplicationsMenuPage';
import {RoleAssigneesPage} from './RoleAssigneesPage';
import {RolePage} from './RolePage';
import {RoleUserGroupSelectorPage} from './RoleUserGroupSelectorPage';

export class RolesPage {
	readonly accountRolesLink: Locator;
	readonly applicationsMenuButton: Locator;
	readonly applicationsMenuPage: ApplicationsMenuPage;
	readonly copyFrame: FrameLocator;
	readonly copyFrameEmptyErrorMessage: Locator;
	readonly copyFrameErrorMessage: Locator;
	readonly copyFrameNewRoleNameInput: Locator;
	readonly copyFrameSaveButton: Locator;
	readonly deleteButton: Locator;
	readonly duplicateMenuItem: Locator;
	readonly noPermissionMessage: Locator;
	readonly numberAssigneesCell: (
		roleName: string,
		value: string
	) => Promise<Locator>;
	readonly optionsButton: Locator;
	readonly organizationRolesLink: Locator;
	readonly page: Page;
	readonly roleAssigneesPage: RoleAssigneesPage;
	readonly roleCell: (value: string, exact?: boolean) => Locator;
	readonly rolePage: RolePage;
	readonly rolesLink: (name: string) => Locator;
	readonly roleUserGroupSelectorPage: RoleUserGroupSelectorPage;
	readonly rolesTable: DataTablePage;
	readonly siteRolesLink: Locator;
	readonly statusText: (value: string) => Locator;
	readonly userLink: Locator;

	constructor(page: Page) {
		this.accountRolesLink = page.getByRole('link', {
			exact: true,
			name: 'Account Roles',
		});
		this.applicationsMenuButton = page.getByLabel(
			'Open Applications MenuCtrl+'
		);
		this.applicationsMenuPage = new ApplicationsMenuPage(page);
		this.copyFrame = page.frameLocator('iframe[id="modalIframe"]');
		this.copyFrameEmptyErrorMessage = this.copyFrame.getByText(
			'Please enter a valid name'
		);
		this.copyFrameErrorMessage = this.copyFrame.getByText(
			'Please enter a unique name'
		);
		this.copyFrameNewRoleNameInput =
			this.copyFrame.getByLabel('New Role Name');
		this.copyFrameSaveButton = this.copyFrame.getByRole('button', {
			name: 'Save',
		});
		this.deleteButton = page
			.getByRole('menuitem', {name: 'Delete'})
			.or(page.getByRole('link', {name: 'Delete'}));
		this.duplicateMenuItem = page.getByRole('menuitem', {
			name: 'Duplicate',
		});
		this.noPermissionMessage = page
			.getByText(
				'You do not have the roles required to access this portlet.'
			)
			.first();
		this.numberAssigneesCell = async (roleName, value) =>
			(await this.rolesTable.row(1, roleName, true)).row.getByRole(
				'cell',
				{exact: true, name: value}
			);
		this.optionsButton = page.getByLabel('Options', {exact: true});
		this.organizationRolesLink = page.getByRole('link', {
			exact: true,
			name: 'Organization Roles',
		});
		this.page = page;
		this.roleAssigneesPage = new RoleAssigneesPage(page);
		this.roleCell = (value, exact = true) =>
			this.page.getByRole('cell', {
				exact,
				name: value,
			});
		this.rolePage = new RolePage(page);
		this.rolesLink = (name) =>
			page.getByRole('link', {
				exact: true,
				name: `${name} Roles`,
			});
		this.roleUserGroupSelectorPage = new RoleUserGroupSelectorPage(page);
		this.rolesTable = new DataTablePage(
			page,
			page
				.locator(
					'#portlet_com_liferay_roles_admin_web_portlet_RolesAdminPortlet div'
				)
				.first()
		);
		this.siteRolesLink = page.getByRole('link', {
			exact: true,
			name: 'Site Roles',
		});
		this.statusText = (value) => page.getByText(value, {exact: true});
		this.userLink = page.getByRole('link', {exact: true, name: 'User'});
	}

	async goto(checkTabVisibility = true) {
		await this.applicationsMenuPage.goToRoles(checkTabVisibility);
	}

	async selectRole(roleName: string) {
		await this.page.getByRole('link', {name: roleName}).click();
	}

	async assignRoleToUserGroup(roleName: string, userGroupName: string) {
		await this.rolesTable.changeView('Table');
		await this.rolesTable.search(roleName);
		await (await this.rolesTable.cellLink(roleName)).click();
		await this.rolePage.assigneesLink.click();

		await expect(
			this.roleAssigneesPage.assigneesTable.cell(userGroupName)
		).toHaveCount(0);

		await expect(this.roleAssigneesPage.userGroupsLink).toBeVisible();

		await this.roleAssigneesPage.userGroupsLink.click();

		await expect(
			this.roleAssigneesPage.noDataMessage('user groups')
		).toBeVisible();
		await expect(
			this.roleAssigneesPage.assigneesTable.cell(userGroupName)
		).toHaveCount(0);

		await this.roleAssigneesPage.assigneesTable.newButton.click();

		await expect(
			this.roleUserGroupSelectorPage.userGroupsTable.cell(userGroupName)
		).toBeVisible();

		await this.roleUserGroupSelectorPage.assignUserGroups([userGroupName]);

		await expect(
			this.roleAssigneesPage.assigneesTable.cell(userGroupName)
		).toBeVisible();
	}
}
