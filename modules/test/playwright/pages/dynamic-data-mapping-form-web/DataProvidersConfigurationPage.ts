/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Locator, Page, expect} from '@playwright/test';

import {waitForPageToBeLoaded} from '../../utils/waitForPageToBeLoaded';
import {SystemSettingsPage} from '../configuration-admin-web/SystemSettingsPage';

const ACCESS_LOCAL_NETWORK_LABEL = 'Access Local Network';

export class DataProvidersConfigurationPage {
	readonly accessLocalNetworkCheckbox: Locator;
	readonly page: Page;

	private readonly systemSettingsPage: SystemSettingsPage;

	constructor(page: Page) {
		this.page = page;
		this.systemSettingsPage = new SystemSettingsPage(page);

		this.accessLocalNetworkCheckbox = page.getByLabel(
			ACCESS_LOCAL_NETWORK_LABEL
		);
	}

	async goTo() {
		await this.systemSettingsPage.goToConfiguration(
			'com.liferay.dynamic.data.mapping.data.provider.configuration.DDMDataProviderConfiguration'
		);
	}

	async setAccessLocalNetwork(enabled: boolean) {
		await this.goTo();

		await this.systemSettingsPage.checkOption(
			ACCESS_LOCAL_NETWORK_LABEL,
			enabled
		);

		await this.systemSettingsPage.saveAndWaitForAlert();

		await waitForPageToBeLoaded(this.page);

		await expect(this.accessLocalNetworkCheckbox).toBeChecked({
			checked: enabled,
		});
	}
}
