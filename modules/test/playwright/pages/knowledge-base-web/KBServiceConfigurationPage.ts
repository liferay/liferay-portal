/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Locator, Page} from '@playwright/test';

import {SystemSettingsPage} from '../configuration-admin-web/SystemSettingsPage';

export class KBServiceConfigurationPage {
	readonly checkIntervalInput: Locator;
	readonly expirationDateNotificationDateWeeksInput: Locator;
	readonly page: Page;

	private readonly systemSettingsPage: SystemSettingsPage;

	constructor(page: Page) {
		this.page = page;
		this.systemSettingsPage = new SystemSettingsPage(page);

		const systemSettingsPortletForm = page.locator(
			'[id="_com_liferay_configuration_admin_web_portlet_SystemSettingsPortlet_fm"]'
		);

		this.checkIntervalInput = systemSettingsPortletForm.locator(
			'input[name$="checkInterval"]'
		);
		this.expirationDateNotificationDateWeeksInput =
			systemSettingsPortletForm.locator(
				'input[name$="expirationDateNotificationDateWeeks"]'
			);
	}

	async goTo() {
		await this.systemSettingsPage.goToConfiguration(
			'com.liferay.knowledge.base.internal.configuration.KBServiceConfiguration'
		);
	}

	async resetToDefaultValues() {
		await this.systemSettingsPage.resetToDefaultValues();
	}

	async save() {
		await this.systemSettingsPage.saveAndWaitForAlert();
	}
}
