/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Locator, Page} from '@playwright/test';

import {waitForAlert} from '../../utils/waitForAlert';
import {SystemSettingsPage} from '../configuration-admin-web/SystemSettingsPage';

export class CommercePricingSystemSettingsPage {
	readonly displayDiscountLevelsCheckbox: Locator;
	readonly page: Page;
	readonly priceListDiscoveryMethodInput: Locator;
	readonly submitConfiguration: Locator;
	readonly systemSettingsPage: SystemSettingsPage;

	constructor(page: Page) {
		this.displayDiscountLevelsCheckbox = page.getByLabel(
			'Display Discount Levels'
		);
		this.page = page;
		this.priceListDiscoveryMethodInput = this.page.getByRole('textbox', {
			name: 'Price List Discovery Method',
		});
		this.submitConfiguration = page.getByTestId('submitConfiguration');
		this.systemSettingsPage = new SystemSettingsPage(page);
	}

	async setDisplayDiscountLevels(checked: boolean) {
		await this.systemSettingsPage.goToSystemSetting('Pricing', 'Price');

		await this.displayDiscountLevelsCheckbox.setChecked(checked);

		await this.submitConfiguration.click();

		await waitForAlert(this.page);
	}

	async setPriceListDiscoveryMethod(mode = 'hierarchy') {
		await this.systemSettingsPage.goToSystemSetting(
			'Pricing',
			'Commerce Pricing Configuration'
		);

		await this.priceListDiscoveryMethodInput.clear();
		await this.priceListDiscoveryMethodInput.fill(mode);
		await this.submitConfiguration.click();

		await waitForAlert(this.page);
	}
}
