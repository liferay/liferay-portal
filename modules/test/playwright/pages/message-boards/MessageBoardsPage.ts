/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {FrameLocator, Locator, Page} from '@playwright/test';

import {clickAndExpectToBeVisible} from '../../utils/clickAndExpectToBeVisible';
import {PORTLET_URLS} from '../../utils/portletUrls';

export class MessageBoardsPage {
	readonly actionAddMessage: Locator;
	readonly actionReplyMessage: Locator;
	readonly deleteAllMBEntriesButton: Locator;
	readonly homeCategoryPermissionsFrame: FrameLocator;
	readonly homeCategoryPermissionsMenuItem: Locator;
	readonly newThreadButton: Locator;
	readonly optionsMenu: Locator;
	readonly page: Page;
	readonly saveButton: Locator;
	readonly selectAllMBEntriesCheckBox: Locator;

	constructor(page: Page) {
		this.homeCategoryPermissionsFrame = page.frameLocator(
			'iframe[title="Home Category Permissions"]'
		);
		this.actionAddMessage = this.homeCategoryPermissionsFrame.locator(
			'#guest_ACTION_ADD_MESSAGE'
		);
		this.actionReplyMessage = this.homeCategoryPermissionsFrame.locator(
			'#guest_ACTION_REPLY_TO_MESSAGE'
		);
		this.deleteAllMBEntriesButton = page.getByRole('button', {
			name: 'Delete',
		});
		this.homeCategoryPermissionsMenuItem = page.getByRole('menuitem', {
			name: 'Home Category Permissions',
		});
		this.newThreadButton = page.getByRole('link', {name: 'New Thread'});
		this.optionsMenu = page.getByLabel('Options');
		this.page = page;
		this.saveButton = this.homeCategoryPermissionsFrame.getByRole(
			'button',
			{name: 'Save'}
		);
		this.selectAllMBEntriesCheckBox = page.getByLabel(
			'Select All Items on the Page'
		);
	}

	async deleteAllMBEntries() {
		await this.selectAllMBEntriesCheckBox.check();
		await this.deleteAllMBEntriesButton.click();
	}

	async goto(siteUrl?: Site['friendlyUrlPath']) {
		await this.page.goto(
			`/group${siteUrl || '/guest'}${PORTLET_URLS.messageBoardsAdmin}`
		);
	}

	async goToCreateNewThread() {
		await clickAndExpectToBeVisible({
			autoClick: true,
			target: this.page.getByRole('menuitem', {name: 'Thread'}),
			trigger: this.page.getByRole('button', {exact: true, name: 'New'}),
		});
	}

	async goToThreadPriorities(siteUrl?: Site['friendlyUrlPath']) {
		this.goto(siteUrl);

		await clickAndExpectToBeVisible({
			autoClick: true,
			target: this.page
				.locator('.dropdown-menu')
				.getByRole('menuitem', {name: 'Configuration'}),
			trigger: this.optionsMenu,
		});

		await this.page
			.getByRole('menuitem', {name: 'Thread Priorities'})
			.click();
	}

	async setGuestCategoryPermissions(siteUrl?: Site['friendlyUrlPath']) {
		await this.goto(siteUrl);

		await this.optionsMenu.click();
		await this.homeCategoryPermissionsMenuItem.click();

		await this.actionAddMessage.check();
		await this.actionReplyMessage.check();

		await this.saveButton.click();

		await this.page.getByLabel('Close', {exact: true}).click();
	}

	async setRoleCategoryPermissions(
		roleName: string,
		siteUrl?: Site['friendlyUrlPath']
	) {
		await this.goto(siteUrl);

		await this.optionsMenu.click();
		await this.homeCategoryPermissionsMenuItem.click();

		await this.homeCategoryPermissionsFrame
			.locator(`#${roleName}_ACTION_ADD_MESSAGE`)
			.first()
			.check();

		await this.homeCategoryPermissionsFrame
			.locator(`#${roleName}_ACTION_REPLY_TO_MESSAGE`)
			.first()
			.check();

		await this.homeCategoryPermissionsFrame
			.locator(`#${roleName}_ACTION_VIEW`)
			.first()
			.check();

		await this.saveButton.click();

		await this.page.getByLabel('Close', {exact: true}).click();
	}
}
