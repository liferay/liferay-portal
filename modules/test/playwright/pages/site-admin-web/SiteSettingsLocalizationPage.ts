/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Locator, Page} from '@playwright/test';

import {waitForAlert} from '../../utils/waitForAlert';
import {SiteSettingsPage} from './SiteSettingsPage';

export class SiteSettingsLocalizationPage {
	readonly page: Page;

	readonly customDefaultLanguageOption: Locator;
	readonly defaultLanguageOption: Locator;
	readonly defaultLanguageSingleSelect: Locator;
	readonly saveButton: Locator;
	readonly siteSettingsPage: SiteSettingsPage;

	constructor(page: Page) {
		this.page = page;

		this.customDefaultLanguageOption = page.getByLabel(
			'Define a custom default language and additional available languages for this site.'
		);
		this.defaultLanguageOption = page.getByLabel(
			'Use the default language options.'
		);
		this.defaultLanguageSingleSelect = page.locator(
			`select[name="_com_liferay_site_admin_web_portlet_SiteSettingsPortlet_TypeSettingsProperties--languageId--"]`
		);
		this.saveButton = page.getByRole('button', {name: 'Save'});
		this.siteSettingsPage = new SiteSettingsPage(page);
	}

	async goto() {
		await this.siteSettingsPage.goToSiteSetting('Localization');
	}

	async saveConfiguration() {
		this.saveButton.click();
		await waitForAlert(this.page);
	}

	async selectCustomDefaultLanguageOption() {
		await this.customDefaultLanguageOption.click();
	}

	async selectDefaultLanguageOption() {
		await this.defaultLanguageOption.click();
	}

	async setCustomDefaultLanguage(languageOption: string) {
		await this.defaultLanguageSingleSelect.click();
		await this.defaultLanguageSingleSelect.selectOption(languageOption);
		await this.saveConfiguration();
	}
}
