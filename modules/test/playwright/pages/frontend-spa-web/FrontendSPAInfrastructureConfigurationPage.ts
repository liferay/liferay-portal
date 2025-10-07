/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Locator, Page, expect} from '@playwright/test';

import {clickAndExpectToBeVisible} from '../../utils/clickAndExpectToBeVisible';
import isSPAEnabled from '../../utils/isSPAEnabled';
import {InstanceSettingsPage} from '../configuration-admin-web/InstanceSettingsPage';

export class FrontendSPAInfrastructureConfigurationPage {
	readonly actions: Locator;
	readonly enabledCheckBox: Locator;
	readonly instanceSettingsPage: InstanceSettingsPage;
	readonly page: Page;
	readonly resetDefaultValues: Locator;
	readonly saveButton: Locator;
	readonly updateButton: Locator;

	constructor(page: Page) {
		this.enabledCheckBox = page.getByRole('checkbox', {
			name: 'Enable Single Page Application',
		});
		this.instanceSettingsPage = new InstanceSettingsPage(page);
		this.page = page;
		this.resetDefaultValues = page.getByText('Reset Default Values');
		this.saveButton = page.getByRole('button', {name: 'Save'});
		this.updateButton = page.getByRole('button', {name: 'Update'});
	}

	async disableSPA() {
		await expect(this.enabledCheckBox).toBeVisible();

		await expect(async () => {
			await this.enabledCheckBox.uncheck();

			await expect(this.enabledCheckBox).not.toBeChecked();
		}).toPass();

		await this.saveConfiguration();

		await this.page.reload();

		expect(await isSPAEnabled({page: this.page})).toBeFalsy();
	}

	async enableSPA() {
		await expect(this.enabledCheckBox).toBeVisible();

		await expect(async () => {
			await this.enabledCheckBox.check();

			await expect(this.enabledCheckBox).toBeChecked();
		}).toPass();

		await this.saveConfiguration();

		await this.page.reload();

		expect(await isSPAEnabled({page: this.page})).toBeTruthy();
	}

	async goto() {
		await this.instanceSettingsPage.goToInstanceSetting(
			'Infrastructure',
			'Frontend SPA Infrastructure'
		);

		await expect(this.enabledCheckBox).toBeVisible();
	}

	async resetConfiguration() {
		if (await this.actions.isVisible()) {
			await clickAndExpectToBeVisible({
				autoClick: true,
				target: this.resetDefaultValues,
				trigger: this.actions,
			});

			await this.page
				.getByText('Success:Your request completed successfully.')
				.waitFor();
		}
	}

	async saveConfiguration() {
		if (await this.page.isVisible('button:has-text("Update")')) {
			await this.updateButton.click();
		}
		else {
			await this.saveButton.click();
		}

		await this.page
			.getByText('Success:Your request completed successfully.')
			.waitFor();
	}
}
