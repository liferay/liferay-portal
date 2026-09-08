/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {FrameLocator, Locator, Page, expect} from '@playwright/test';

import {selectMaxItemsPerPage} from '../../../../utils/pagination';

export class PermissionsPage {
	readonly page: Page;
	readonly itemsPerPageButton: Locator;
	readonly maxItemsPerPageLink: Locator;
	readonly permissionsModal: FrameLocator;
	readonly permissionsModalCancelButton: Locator;
	readonly permissionsModalCloseButton: Locator;
	readonly permissionsModalSaveButton: Locator;
	readonly roleCell: (roleName: string) => Locator;

	constructor(page: Page) {
		this.page = page;
		this.permissionsModal = page.frameLocator(
			'iframe[title="Permissions"]'
		);
		this.itemsPerPageButton =
			this.permissionsModal.getByLabel('Items per Page');
		this.maxItemsPerPageLink = this.permissionsModal.locator(
			'a[href*="_delta=60"]'
		);
		this.permissionsModalCancelButton = this.permissionsModal.getByRole(
			'button',
			{name: 'Cancel'}
		);
		this.permissionsModalCloseButton = this.page
			.getByRole('dialog', {name: 'Permissions'})
			.getByLabel('Close');
		this.permissionsModalSaveButton = this.permissionsModal.getByRole(
			'button',
			{name: 'Save'}
		);
		this.roleCell = (roleName: string) => {
			return this.permissionsModal.getByRole('cell', {name: roleName});
		};
	}

	async checkPermissionsAndSave(
		permissions: Array<{action: string; role: string}>
	) {
		await expect(this.permissionsModalSaveButton).toBeVisible();

		await this.showAllRoles();

		for (const permission of permissions) {
			let role = permission.role.toLowerCase();
			role = role.replace(/ /g, '-');

			const action = permission.action.toUpperCase();

			await this.permissionsModal
				.locator(`#${role}_ACTION_${action}`)
				.check();
		}

		await this.permissionsModalSaveButton.click();
		await this.permissionsModalCloseButton.click();
	}

	async openFromActionsMenu() {
		const permissionsMenuItems = this.page.getByRole('menuitem', {
			exact: true,
			name: 'Permissions',
		});

		await permissionsMenuItems.first().click();

		await permissionsMenuItems.nth(1).click();
	}

	async showAllRoles() {
		await selectMaxItemsPerPage(
			this.itemsPerPageButton,
			this.maxItemsPerPageLink
		);
	}

	async verifyPermissions(
		permissions: Array<{action: string; checked: boolean; role: string}>
	) {
		await expect(this.permissionsModalSaveButton).toBeVisible();

		await this.showAllRoles();

		for (const permission of permissions) {
			let role = permission.role.toLowerCase();
			role = role.replace(/ /g, '-');

			const action = permission.action.toUpperCase();

			if (permission.checked) {
				await expect(
					this.permissionsModal.locator(`#${role}_ACTION_${action}`)
				).toBeChecked();
			}
			else {
				await expect(
					this.permissionsModal.locator(`#${role}_ACTION_${action}`)
				).not.toBeChecked();
			}
		}

		await this.permissionsModalCloseButton.click();
	}
}
