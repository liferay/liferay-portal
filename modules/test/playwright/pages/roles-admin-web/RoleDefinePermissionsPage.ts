/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {FrameLocator, Locator, Page, expect} from '@playwright/test';

import {waitForAlert} from '../../utils/waitForAlert';

export class RoleDefinePermissionsPage {
	readonly accessInControlPanel: Locator;
	readonly addToPage: Locator;
	readonly defineGroupScopePermissionsTab: Locator;
	readonly definePermissionsTab: Locator;
	readonly loading: Locator;
	readonly menuItem: (name: string, exact?: boolean) => Locator;
	readonly menuItemByTestId: (id: string) => Locator;
	readonly navigation: Locator;
	readonly noResultsMessage: Locator;
	readonly noRoleMessage: Locator;
	readonly page: Page;
	readonly permissionCheckbox: (
		permissionName: string,
		last?: boolean
	) => Locator;
	readonly permissionScopeChangeButton: (
		permissionName: string,
		last?: boolean
	) => Locator;
	readonly permissionScopeLabel: (
		permissionName: string,
		last?: boolean
	) => Locator;
	readonly permissionScopeRemoveButton: (
		permissionName: string,
		last?: boolean
	) => Locator;
	readonly permissionScopes: (permissionName: string) => Locator;
	readonly permissionScopeSiteLabel: (
		permissionName: string,
		siteName: string
	) => Locator;
	readonly portletResourceLabel: Locator;
	readonly resourceName: (resourceName: string) => Locator;
	readonly resourceRemoveLink: (resourceName: string) => Locator;
	readonly resourceSection: (title: string) => Locator;
	readonly saveButton: Locator;
	readonly searchInput: Locator;
	readonly selectAllCheckbox: (resourceName: string) => Locator;
	readonly siteSelectorFrame: FrameLocator;
	readonly siteSelectorMySitesLink: Locator;
	readonly siteSelectorSiteCard: (siteName: string) => Locator;
	readonly subMenuItem: (name: string, subMenuItemName: string) => Locator;
	readonly summaryPermissionCell: (permissionName: string) => Locator;
	readonly summaryPermissionScopeCell: (
		permissionName: string,
		scope: string
	) => Locator;

	constructor(page: Page) {
		this.accessInControlPanel = page.getByText('Access in Control Panel');
		this.addToPage = page.getByText('Add to Page');
		this.defineGroupScopePermissionsTab = page.getByRole('link', {
			name: 'Define Group Scope Permissions',
		});
		this.definePermissionsTab = page.getByRole('link', {
			name: 'Define Permissions',
		});
		this.loading = page.getByText('Loading');
		this.menuItem = (name: string, exact = false) => {
			return this.navigation.getByRole('menuitem', {exact, name}).first();
		};
		this.menuItemByTestId = (id: string) => {
			return page.getByTestId(id).getByRole('menuitem');
		};
		this.navigation = page.locator(
			'[data-qa-id="editRolePermissionsNavigation"]'
		);
		this.noResultsMessage = this.navigation.getByText(
			'There are no results.'
		);
		this.noRoleMessage = page.getByText(
			'This role does not have any permissions'
		);
		this.page = page;
		this.permissionCheckbox = (permissionName, last = false) => {
			if (last) {
				return page
					.getByLabel(permissionName)
					.and(page.getByRole('checkbox'))
					.last();
			}

			return page
				.getByLabel(permissionName)
				.and(page.getByRole('checkbox'));
		};
		this.permissionScopeChangeButton = (permissionName, last = false) =>
			this.permissionCheckbox(permissionName, last)
				.locator('../../..')
				.getByRole('button', {name: 'Change'});
		this.permissionScopeLabel = (permissionName, last = false) =>
			this.permissionCheckbox(permissionName, last)
				.locator('../../..')
				.getByText('All Sites and Asset Libraries');
		this.permissionScopeRemoveButton = (permissionName, last = false) =>
			this.permissionCheckbox(permissionName, last)
				.locator('../../..')
				.locator('.permission-scopes')
				.getByRole('button');
		this.permissionScopes = (permissionName, last = false) =>
			this.permissionCheckbox(permissionName, last)
				.locator('../../..')
				.locator('.permission-scopes .label');
		this.permissionScopeSiteLabel = (
			permissionName,
			siteName,
			last = false
		) =>
			this.permissionCheckbox(permissionName, last)
				.locator('../../..')
				.getByText(siteName);
		this.portletResourceLabel = page.getByTestId('portletResourceLabel');
		this.resourceName = (resourceName) =>
			page.getByRole('cell', {name: resourceName});
		this.resourceRemoveLink = (resourceName) =>
			this.resourceName(resourceName)
				.locator('..')
				.getByRole('link', {name: 'Remove'});
		this.resourceSection = (title: string) => {
			return page
				.locator('.sheet-tertiary-title')
				.filter({hasText: title});
		};
		this.saveButton = page.getByRole('button', {exact: true, name: 'Save'});
		this.searchInput = this.navigation.getByPlaceholder('Search');
		this.selectAllCheckbox = (resourceName) =>
			page
				.getByText(resourceName)
				.locator('..')
				.getByLabel('', {exact: true})
				.and(page.getByRole('checkbox'));
		this.siteSelectorFrame = page.frameLocator(
			'iframe[title="Select Site"]'
		);
		this.siteSelectorMySitesLink = this.siteSelectorFrame.getByRole(
			'link',
			{name: 'My Sites'}
		);
		this.siteSelectorSiteCard = (siteName) =>
			this.siteSelectorFrame.getByRole('link', {
				exact: true,
				name: siteName,
			});
		this.subMenuItem = (name: string, subMenuItemName: string) =>
			this.menuItem(name, true)
				.locator('..')
				.getByRole('menuitem', {exact: true, name: subMenuItemName});
		this.summaryPermissionCell = (permissionName) =>
			page.getByRole('cell', {name: permissionName});
		this.summaryPermissionScopeCell = (permissionName, scope) =>
			this.summaryPermissionCell(permissionName)
				.locator('..')
				.getByText(scope);
	}

	async changePermission(
		menuItemName: string,
		permissionName: string,
		check: boolean
	) {
		await expect(this.searchInput).toBeAttached();

		await this.searchInput.click();
		await this.searchInput.fill(menuItemName);

		await expect(this.menuItem(menuItemName)).toBeVisible();

		await this.menuItem(menuItemName).click();

		await this.page.waitForLoadState('domcontentloaded');

		if (check) {
			await this.permissionCheckbox(permissionName).check();
		}
		else {
			await this.permissionCheckbox(permissionName).uncheck();
		}

		await this.saveButton.click();

		await waitForAlert(
			this.page,
			'Success:The role permissions were updated.'
		);
	}
}
