/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Locator, Page, expect} from '@playwright/test';

import {clickAndExpectToBeVisible} from '../../utils/clickAndExpectToBeVisible';

export class AccountNotificationsPage {
	readonly accountNotificationsMenuItem: Locator;
	readonly deleteButton: Locator;
	readonly notificationsCount: Locator;
	readonly optionsButton: Locator;
	readonly page: Page;
	readonly selectAllItemsCheckbox: Locator;
	readonly userPersonalMenuButton: Locator;

	constructor(page: Page) {
		this.accountNotificationsMenuItem = page.getByRole('menuitem', {
			name: 'Notifications',
		});
		this.deleteButton = page.getByRole('button', {name: 'Delete'});
		this.notificationsCount = page.locator('a.panel-notifications-count');
		this.optionsButton = page.getByTitle('Options', {exact: true});
		this.page = page;
		this.selectAllItemsCheckbox = page.getByLabel(
			'Select All Items on the Page'
		);
		this.userPersonalMenuButton = page.getByTitle('User Profile Menu');
	}

	async deleteEnabledNotifications() {
		if (
			!(await this.selectAllItemsCheckbox.isVisible()) ||
			!(await this.selectAllItemsCheckbox.isEnabled())
		) {
			return;
		}

		await this.selectAllItemsCheckbox.check();

		await expect(this.deleteButton).toBeVisible();

		await this.deleteButton.click();

		await expect(this.deleteButton).toBeVisible();
	}

	async disableWebsiteDeliveries(
		contentType: string,
		actionDescriptions: string[]
	) {
		await this.goToAccountNotifications();

		await clickAndExpectToBeVisible({
			autoClick: true,
			target: this.page.getByRole('menuitem', {name: 'Configuration'}),
			trigger: this.optionsButton,
		});

		const configurationFrame = this.page.frameLocator(
			'iframe[title*="Configuration"]'
		);

		const contentTypePanel = configurationFrame.locator(
			`[id$="${contentType.replace(/ /g, '-')}Content"]`
		);

		// The content type panel is collapsed by default; expand it to reveal
		// its delivery switches

		await clickAndExpectToBeVisible({
			target: contentTypePanel
				.locator('tr')
				.filter({hasText: actionDescriptions[0]}),
			trigger: configurationFrame
				.locator('.collapse-icon')
				.filter({hasText: contentType}),
		});

		for (const actionDescription of actionDescriptions) {
			await contentTypePanel
				.locator('tr')
				.filter({hasText: actionDescription})
				.getByTitle('Website')
				.uncheck();
		}

		await configurationFrame.getByRole('button', {name: 'Save'}).click();

		await this.page.waitForLoadState('networkidle');
	}

	async goToAccountNotifications() {
		await this.userPersonalMenuButton.click();

		await expect(this.accountNotificationsMenuItem).toBeVisible();

		await this.accountNotificationsMenuItem.click();
	}
}
