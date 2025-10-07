/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Locator, Page} from '@playwright/test';

import {clickAndExpectToBeVisible} from '../../utils/clickAndExpectToBeVisible';
import {SiteSettingsPage} from './SiteSettingsPage';

export class SiteSettingsLocalizationPage {
	readonly page: Page;

	readonly availableLanguages: Locator;
	readonly customDefaultLanguageOption: Locator;
	readonly defaultLanguageOption: Locator;
	readonly defaultLanguageSingleSelect: Locator;
	readonly siteSettingsPage: SiteSettingsPage;

	constructor(page: Page) {
		this.page = page;

		this.availableLanguages = page.locator(
			'[id="_com_liferay_site_admin_web_portlet_SiteSettingsPortlet_siteLanguageConfiguration"]'
		);
		this.customDefaultLanguageOption = page.getByLabel(
			'Define a custom default language and additional available languages for this site.'
		);
		this.defaultLanguageOption = page.getByLabel(
			'Use the default language options.'
		);
		this.defaultLanguageSingleSelect = page.locator(
			`select[name="_com_liferay_site_admin_web_portlet_SiteSettingsPortlet_TypeSettingsProperties--languageId--"]`
		);
		this.siteSettingsPage = new SiteSettingsPage(page);
	}

	async goto(siteUrl?: Site['friendlyUrlPath']) {
		await this.siteSettingsPage.goToSiteSetting(
			'Localization',
			'Languages',
			siteUrl
		);
	}

	async selectCustomDefaultLanguageOption() {
		await this.customDefaultLanguageOption.click();
	}

	async selectDefaultLanguageOption() {
		await this.defaultLanguageOption.click();
	}

	async setCustomDefaultLanguage(
		languageOption: string,
		siteUrl?: Site['friendlyUrlPath']
	) {
		await this.goto(siteUrl);

		await clickAndExpectToBeVisible({
			target: this.defaultLanguageSingleSelect,
			trigger: this.page.getByRole('radio', {
				name: 'Define a custom default language',
			}),
		});

		await this.defaultLanguageSingleSelect.selectOption(languageOption);

		await this.siteSettingsPage.saveConfiguration();

		await this.page.waitForLoadState();
	}

	async disableAllLanguagesExceptSp(siteUrl?: Site['friendlyUrlPath']) {
		await this.goto(siteUrl);

		await this.page.getByLabel('Transfer Item Right to Left').click();

		await this.page
			.getByRole('listbox')
			.nth(1)
			.selectOption([
				'en_US',
				'ar_SA',
				'ca_ES',
				'nl_NL',
				'zh_CN',
				'fi_FI',
				'fr_FR',
				'de_DE',
				'hu_HU',
				'ja_JP',
				'pt_BR',
				'sv_SE',
			]);

		await this.page.getByLabel('Transfer Item Right to Left').click();

		await this.siteSettingsPage.saveConfiguration();
	}
}
