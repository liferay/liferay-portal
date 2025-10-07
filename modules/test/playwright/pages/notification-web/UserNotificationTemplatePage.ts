/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Locator, Page} from '@playwright/test';

import {NotificationTemplatesPage} from './NotificationTemplatesPage';

export class UserNotificationTemplatePage {
	readonly basicInfoName: Locator;
	readonly contentSubject: Locator;
	readonly descriptionInput: Locator;
	readonly notificationTemplatesPage: NotificationTemplatesPage;
	readonly page: Page;
	readonly saveButton: Locator;
	readonly toInput: Locator;

	constructor(page: Page) {
		this.basicInfoName = page.getByLabel('Name' + 'Mandatory').first();
		this.contentSubject = page.getByLabel('Subject' + 'Mandatory');
		this.descriptionInput = page.getByRole('textbox', {
			name: 'Description',
		});
		this.notificationTemplatesPage = new NotificationTemplatesPage(page);
		this.page = page;
		this.saveButton = page.getByText('Save', {exact: true});
		this.toInput = page.getByPlaceholder('Use terms to configure');
	}

	async goto() {
		await this.notificationTemplatesPage.goto();

		await this.notificationTemplatesPage.newNotificationTemplateButton.click();

		await this.notificationTemplatesPage.userNotificationDropdownItem.click();

		await this.notificationTemplatesPage.page
			.getByRole('heading', {exact: true, name: 'Notification Template'})
			.waitFor();
	}

	async selectNotificationRecipient(
		recipient: 'Definition of Terms' | 'Role' | 'User' | 'User Group'
	) {
		await this.page.getByRole('combobox').first().click();

		await this.page
			.getByRole('option')
			.filter({hasText: recipient})
			.click();
	}

	async selectRole(roleName: string) {
		await this.page.getByPlaceholder('Enter a role.').click();

		await this.page.getByLabel(roleName, {exact: true}).check();
	}
}
