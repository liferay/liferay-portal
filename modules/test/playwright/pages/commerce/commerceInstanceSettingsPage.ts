/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Locator, Page} from '@playwright/test';

import {GlobalMenuPage} from '../product-navigation-applications-menu/GlobalMenuPage';
import {UIElementsPage} from '../uielements/UIElementsPage';

export class CommerceInstanceSettingsPage {
	readonly globalMenuPage: GlobalMenuPage;
	readonly catalogLink: Locator;
	readonly checkboxPlacedOrders: (checkboxName: string) => Locator;
	readonly editConfigurationSubmitButton: Locator;
	readonly enabledButton: Locator;
	readonly page: Page;
	readonly productOptionMenuItem: Locator;
	readonly showUnselectableOptionsCheckbox: Locator;
	readonly submitConfigurationButton: Locator;
	private uiElementsPage;

	constructor(page: Page) {
		this.globalMenuPage = new GlobalMenuPage(page);
		this.catalogLink = page.getByRole('link', {
			exact: true,
			name: 'Catalog',
		});
		this.checkboxPlacedOrders = (checkboxName) =>
			page.getByLabel(checkboxName, {exact: true});
		this.editConfigurationSubmitButton = page
			.getByRole('button', {name: 'Save'})
			.or(page.getByRole('button', {name: 'Update'}));
		this.enabledButton = page.getByLabel('enabled');
		this.page = page;
		this.productOptionMenuItem = page.getByRole('menuitem', {
			name: 'Product Options',
		});
		this.showUnselectableOptionsCheckbox = page.getByLabel(
			'Show Unselectable Options'
		);
		this.submitConfigurationButton = page.getByTestId(
			'submitConfiguration'
		);
		this.uiElementsPage = new UIElementsPage(page);
	}

	async goto() {
		await this.globalMenuPage.goToControlPanel('Instance Settings');
	}

	async goToInstanceSetting(categoryKey: string, configurationName: string) {
		await this.goto();
		await this.page
			.getByRole('link', {
				exact: true,
				name: categoryKey,
			})
			.click();
		await this.page
			.getByRole('menuitem', {
				exact: true,
				name: configurationName,
			})
			.click();
	}

	async toggleProductVersioning() {
		await this.globalMenuPage.goToHome();
		await this.goToInstanceSetting('Catalog', 'Product Versioning');
		await this.enabledButton.click();
		await this.editConfigurationSubmitButton.click();
		await this.uiElementsPage.anySuccessAlert.waitFor({state: 'visible'});
	}

	async toggleShowUnselectableOptions(check: boolean) {
		await this.goto();
		await this.catalogLink.click();
		await this.productOptionMenuItem.click();

		if (check) {
			await this.showUnselectableOptionsCheckbox.check();
		}
		else {
			await this.showUnselectableOptionsCheckbox.uncheck();
		}

		await this.submitConfigurationButton.click();
	}
}
