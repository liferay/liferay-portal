/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {FrameLocator, Locator, Page} from '@playwright/test';

export class NavigationMenuWidgetPage {
	readonly page: Page;

	readonly closeConfigurationModalButton: Locator;
	readonly menuDisplayModal: FrameLocator;
	readonly navigationSelector: Locator;
	readonly saveConfigurationModalButton: Locator;

	constructor(page: Page) {
		this.page = page;

		this.closeConfigurationModalButton = page
			.locator('.modal')
			.getByLabel('Close', {
				exact: true,
			});
		this.menuDisplayModal = page.frameLocator('#modalIframe');
		this.navigationSelector = this.menuDisplayModal.locator(
			'[id="_com_liferay_portlet_configuration_web_portlet_PortletConfigurationPortlet_selectSiteNavigationMenuType"]'
		);
		this.saveConfigurationModalButton = this.menuDisplayModal.getByRole(
			'button',
			{name: 'Save'}
		);
	}

	async saveAndCloseConfigurationModal() {
		await this.saveConfigurationModalButton.click();

		await this.page.waitForTimeout(500);

		await this.closeConfigurationModalButton.click();
	}

	async selectCustomNavigationMenu(navigationMenuName: string) {
		await this.menuDisplayModal.getByLabel('Choose Menu').check();

		await this.page.waitForTimeout(1500);

		await this.menuDisplayModal
			.getByRole('button', {name: 'Select'})
			.click();

		await this.page.waitForTimeout(1000);

		await this.menuDisplayModal
			.frameLocator('iframe[title="Select Site Navigation Menu"]')
			.getByRole('cell', {name: navigationMenuName})
			.click();
	}

	async selectDisplayTemplate(templateType: string) {
		await this.menuDisplayModal.getByLabel('Display Template').click();

		await this.menuDisplayModal
			.getByRole('option', {name: templateType})
			.click();
	}

	async openConfigurationModal(menuItemName: string) {
		await this.page.getByRole('menuitem', {name: menuItemName}).hover();

		await this.page
			.locator(
				'#portlet-topper-toolbar_com_liferay_site_navigation_menu_web_portlet_SiteNavigationMenuPortlet'
			)
			.getByLabel('Options')
			.click();

		await this.page
			.getByRole('menuitem', {exact: true, name: 'Configuration'})
			.click();

		await this.page.waitForTimeout(1500);
	}
}
