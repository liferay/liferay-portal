/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Locator, Page, expect} from '@playwright/test';

import {waitForAlert} from '../../../utils/waitForAlert';
import {SystemSettingsPage} from '../../configuration-admin-web/SystemSettingsPage';

export class RefundReasonsSystemSettingPage {
	readonly actionsButton: (key: string) => Locator;
	readonly addLink: Locator;
	readonly configurationLink: (key: string) => Locator;
	readonly deleteMenuItem: Locator;
	readonly editMenuItem: Locator;
	readonly fieldRequiredWarning: Locator;
	readonly keyInput: Locator;
	readonly nameInput: Locator;
	readonly page: Page;
	readonly priorityInput: Locator;
	readonly saveButton: Locator;
	readonly systemSettingsPage: SystemSettingsPage;
	readonly updateButton: Locator;

	constructor(page: Page) {
		this.actionsButton = (key: string) =>
			page
				.getByRole('row')
				.filter({
					has: page.getByRole('link', {exact: true, name: key}),
				})
				.getByRole('button', {name: 'Actions'});
		this.addLink = page.getByRole('link', {exact: true, name: 'Add'});
		this.configurationLink = (key: string) =>
			page.getByRole('link', {exact: true, name: key});
		this.deleteMenuItem = page
			.getByRole('menu')
			.getByRole('link', {exact: true, name: 'Delete'});
		this.editMenuItem = page
			.getByRole('menu')
			.getByRole('link', {exact: true, name: 'Edit'});
		this.fieldRequiredWarning = page.getByText('This field is required.');
		this.keyInput = page.getByRole('textbox', {exact: true, name: 'Key'});
		this.nameInput = page.getByRole('textbox', {
			exact: true,
			name: 'Name',
		});
		this.page = page;
		this.priorityInput = page.getByRole('textbox', {
			exact: true,
			name: 'Priority',
		});
		this.saveButton = page.getByRole('button', {name: 'Save'});
		this.systemSettingsPage = new SystemSettingsPage(page);
		this.updateButton = page.getByRole('button', {name: 'Update'});
	}

	async addReason({
		key,
		name,
		priority,
	}: {
		key: string;
		name?: string;
		priority?: number;
	}) {
		await this.addLink.click();
		await this.keyInput.fill(key);

		if (name !== undefined) {
			await this.nameInput.fill(name);
		}

		if (priority !== undefined) {
			await this.priorityInput.fill(String(priority));
		}

		await this.saveButton.click();

		await waitForAlert(this.page);
	}

	async deleteReason(key: string) {
		this.page.on('dialog', (dialog) => dialog.accept());

		await expect(async () => {
			await this.actionsButton(key).click();

			await expect(this.deleteMenuItem).toBeVisible({timeout: 500});
		}).toPass({timeout: 5000});

		await this.deleteMenuItem.click();

		await waitForAlert(this.page);
	}

	async goto() {
		await this.systemSettingsPage.goToSystemSetting(
			'Payment',
			'Refund Reasons',
			'System Scope'
		);
	}
}
