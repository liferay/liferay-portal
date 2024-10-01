/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Locator, Page} from '@playwright/test';

import {waitForAlert} from '../../utils/waitForAlert';
import {InstanceSettingsPage} from '../configuration-admin-web/InstanceSettingsPage';

export class LoginInstanceSettingsPage {
	readonly instanceSettingsPage: InstanceSettingsPage;
	readonly page: Page;
	readonly saveButton: Locator;
	readonly updateButton: Locator;

	constructor(page: Page) {
		this.instanceSettingsPage = new InstanceSettingsPage(page);
		this.page = page;
		this.saveButton = page.getByRole('button', {name: 'Save'});
		this.updateButton = page.getByRole('button', {name: 'Update'});
	}

	async goto() {
		await this.instanceSettingsPage.goToInstanceSetting('Login', 'Login');
	}

	async enableLoginPrompt() {
		await this.page.getByLabel('Prompt Enabled').check();
		await this.saveConfiguration();
		await waitForAlert(this.page);
	}

	async disableLoginPrompt() {
		await this.page.getByLabel('Prompt Enabled').uncheck();
		await this.saveConfiguration();
		await waitForAlert(this.page);
	}

	async saveConfiguration() {
		if (await this.page.isVisible('button:has-text("Update")')) {
			this.updateButton.click();

			return;
		}

		this.saveButton.click();
	}
}
