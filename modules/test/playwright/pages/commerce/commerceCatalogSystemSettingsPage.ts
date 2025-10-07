/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Locator, Page} from '@playwright/test';

import {SystemSettingsPage} from '../configuration-admin-web/SystemSettingsPage';
import {UIElementsPage} from '../uielements/UIElementsPage';

export class CommerceCatalogSystemSettingsPage {
	readonly page: Page;
	readonly editConfigurationSubmitButton: Locator;
	readonly enabledButton: Locator;
	readonly systemSettingsPage: SystemSettingsPage;
	private uiElementsPage;

	constructor(page: Page) {
		this.page = page;
		this.editConfigurationSubmitButton = page
			.getByRole('button', {name: 'Save'})
			.or(page.getByRole('button', {name: 'Update'}));
		this.enabledButton = page.getByLabel('enabled');
		this.systemSettingsPage = new SystemSettingsPage(page);
		this.uiElementsPage = new UIElementsPage(page);
	}

	async toggleProductVersioning() {
		await this.systemSettingsPage.goToSystemSetting(
			'Catalog',
			'Product Versioning'
		);
		await this.enabledButton.click();
		await this.editConfigurationSubmitButton.click();
		await this.uiElementsPage.anySuccessAlert.waitFor({state: 'visible'});
	}
}
