/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Locator, Page} from '@playwright/test';

import {InstanceSettingsPage} from '../../../../pages/configuration-admin-web/InstanceSettingsPage';
import {waitForAlert} from '../../../../utils/waitForAlert';

export class LocalizationInstanceSettingsPage {
	readonly instanceSettingsPage: InstanceSettingsPage;
	readonly availableLanguages: Locator;
	readonly currentLanguages: Locator;
	readonly defaultLanguage: Locator;
	readonly moveToAvaiable: Locator;
	readonly moveToCurrent: Locator;
	readonly saveButton: Locator;
	readonly page: Page;

	constructor(page: Page) {
		this.instanceSettingsPage = new InstanceSettingsPage(page);
		this.availableLanguages = page.getByLabel('Available', {exact: true});
		this.currentLanguages = page.getByLabel('Current', {exact: true});
		this.defaultLanguage = page.getByRole('option', {selected: true});
		this.moveToAvaiable = page.getByLabel(
			'Move selected items from Current to Available.'
		);
		this.moveToCurrent = page.getByLabel(
			'Move selected items from Available to Current.'
		);
		this.saveButton = page.getByRole('button', {name: 'Save'});
		this.page = page;
	}

	async goto(configuration, forceReload = true) {
		await this.instanceSettingsPage.goToInstanceSetting(
			'Localization',
			configuration,
			forceReload
		);
	}

	async setLanguage(languages: string[]) {
		const allCurrentLanguages = await this.currentLanguages
			.locator('.reorder-option')
			.all();

		for (let i = allCurrentLanguages.length - 1; i >= 0; i--) {
			const value = await allCurrentLanguages[i].getAttribute('value');

			if (!languages.includes(value)) {
				await this.currentLanguages.selectOption(value);
				await this.moveToAvaiable.click();
			}
		}

		const allAvailableLanguages = await this.availableLanguages
			.locator('.reorder-option')
			.all();

		for (let i = allAvailableLanguages.length - 1; i >= 0; i--) {
			const value = await allAvailableLanguages[i].getAttribute('value');

			if (languages.includes(value)) {
				await this.availableLanguages.selectOption(value);
				await this.moveToCurrent.click();
			}
		}

		await this.saveSettings();
	}

	async saveSettings() {
		await this.saveButton.click();

		await waitForAlert(this.page);
	}
}
