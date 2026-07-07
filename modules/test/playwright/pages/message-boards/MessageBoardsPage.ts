/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {FrameLocator, Locator, Page, expect} from '@playwright/test';

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

	async goToConfigurationTab(
		tabName: string,
		siteUrl?: Site['friendlyUrlPath']
	) {
		await this.goto(siteUrl);

		await clickAndExpectToBeVisible({
			autoClick: true,
			target: this.page
				.locator('.dropdown-menu')
				.getByRole('menuitem', {name: 'Configuration'}),
			trigger: this.optionsMenu,
		});

		await this.page.getByRole('menuitem', {name: tabName}).click();
	}

	async goToThreadPriorities(siteUrl?: Site['friendlyUrlPath']) {
		await this.goToConfigurationTab('Thread Priorities', siteUrl);
	}

	async disableEmailNotifications(siteUrl?: Site['friendlyUrlPath']) {
		for (const tabName of [
			'Message Added Email',
			'Message Updated Email',
		]) {
			await this.goToConfigurationTab(tabName, siteUrl);

			await this.page.getByLabel('Enabled').uncheck();

			await this.page.getByRole('button', {name: 'Save'}).click();

			// Saving submits the form and reloads the configuration screen;
			// wait for it before navigating away

			await this.page.waitForLoadState('networkidle');
		}
	}

	async banReplyAuthor() {
		await this._clickThreadMenuItem(
			this.page.locator('.panel-heading .dropdown-toggle').last(),
			'Ban This User'
		);
	}

	async goToBannedUsers(siteUrl?: Site['friendlyUrlPath']) {
		await this.goto(siteUrl);

		await this.page.getByRole('link', {name: 'Banned Users'}).click();

		await this.page.waitForLoadState('networkidle');
	}

	async goToThread(threadSubject: string, siteUrl?: Site['friendlyUrlPath']) {
		await this.goto(siteUrl);

		await this.page.waitForLoadState('networkidle');

		// The thread row link is covered by the card overlay, so read its href
		// and navigate directly

		const threadURL = await this.page.evaluate((subject) => {
			const anchors = Array.from(document.querySelectorAll('a'));

			const anchor = anchors.find(
				(element) =>
					element.textContent?.includes(subject) &&
					(element.getAttribute('href') || '').includes('message')
			);

			return anchor?.getAttribute('href') || '';
		}, threadSubject);

		await this.page.goto(threadURL);

		await this.page.waitForLoadState('networkidle');
	}

	async unbanUser() {
		await this._clickThreadMenuItem(
			this.page.locator('[data-qa-id="row"] .dropdown-toggle').last(),
			'Unban This User'
		);
	}

	async _clickThreadMenuItem(trigger: Locator, itemName: string) {
		const menuItem = this.page
			.locator('.dropdown-menu:visible')
			.getByText(itemName, {exact: true});

		await expect(async () => {
			await trigger.click();

			await expect(menuItem).toBeVisible({timeout: 3000});
		}).toPass();

		await menuItem.click();

		await this.page.waitForLoadState('networkidle');
	}

	async removeRoleReplyPermission(
		roleName: string,
		siteUrl?: Site['friendlyUrlPath']
	) {
		await this.goto(siteUrl);

		await this.optionsMenu.click();
		await this.homeCategoryPermissionsMenuItem.click();

		await this.homeCategoryPermissionsFrame
			.locator(`#${roleName}_ACTION_REPLY_TO_MESSAGE`)
			.uncheck();

		await this.saveButton.click();

		await this.page.getByLabel('Close', {exact: true}).click();
	}

	async setThreadRolePermissions(
		threadSubject: string,
		roleName: string,
		permissions: {subscribe?: boolean; view?: boolean},
		siteUrl?: Site['friendlyUrlPath']
	) {
		await this.goto(siteUrl);

		await this.page.waitForLoadState('networkidle');

		await this.page
			.getByRole('link', {name: threadSubject})
			.first()
			.click();

		await this.page.waitForLoadState('networkidle');

		await clickAndExpectToBeVisible({
			autoClick: true,
			target: this.page.getByRole('menuitem', {name: 'Permissions'}),
			trigger: this.optionsMenu,
		});

		const permissionsFrame = this.page.frameLocator(
			'iframe[title="Permissions"]'
		);

		if (permissions.view !== undefined) {
			await permissionsFrame
				.locator(`#${roleName}_ACTION_VIEW`)
				.setChecked(permissions.view);
		}

		if (permissions.subscribe !== undefined) {
			await permissionsFrame
				.locator(`#${roleName}_ACTION_SUBSCRIBE`)
				.setChecked(permissions.subscribe);
		}

		await permissionsFrame.getByRole('button', {name: 'Save'}).click();

		await this.page.getByLabel('Close', {exact: true}).click();
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
