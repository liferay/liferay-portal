/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {FrameLocator, Locator, Page, expect} from '@playwright/test';

import {selectMaxItemsPerPage} from '../../../utils/pagination';

export class UserAssociatedDataMessageBoardPage {
	readonly actionButton: Locator;
	readonly editMenuItem: Locator;
	readonly itemsPerPageButton: Locator;
	readonly maxItemsPerPageLink: Locator;
	readonly newButton: Locator;
	readonly page: Page;
	readonly permissionsFrameLocator: FrameLocator;
	readonly threadMenuItem: Locator;
	readonly threadSubjectLink: (subject: string) => Locator;

	constructor(page: Page) {
		this.actionButton = page.getByRole('button', {name: 'Actions'});
		this.editMenuItem = page.getByRole('menuitem', {name: 'Edit'});
		this.newButton = page.getByRole('button', {name: 'New'});
		this.page = page;
		this.permissionsFrameLocator = page.frameLocator(
			'iframe[title="Permissions"]'
		);
		this.itemsPerPageButton =
			this.permissionsFrameLocator.getByLabel('Items per Page');
		this.maxItemsPerPageLink = this.permissionsFrameLocator.locator(
			'a[href*="_delta=60"]'
		);
		this.threadMenuItem = page.getByRole('menuitem', {name: 'Thread'});
		this.threadSubjectLink = (subject: string) =>
			page.getByRole('link', {name: subject});
	}

	async setPermissions(permissionLocators: string[]) {
		await expect(
			this.permissionsFrameLocator.getByRole('button', {name: 'Save'})
		).toBeVisible();

		await selectMaxItemsPerPage(
			this.itemsPerPageButton,
			this.maxItemsPerPageLink
		);

		for (const permissionsLocator of permissionLocators) {
			await this.permissionsFrameLocator
				.locator(permissionsLocator)
				.check();
		}

		await this.permissionsFrameLocator
			.getByRole('button', {name: 'Save'})
			.click();
		await this.permissionsFrameLocator
			.getByRole('button', {name: 'Cancel'})
			.click();
	}
}
