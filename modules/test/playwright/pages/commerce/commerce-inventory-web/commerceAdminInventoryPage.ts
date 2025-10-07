/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Locator, Page} from '@playwright/test';

import {ApplicationsMenuPage} from '../../product-navigation-applications-menu/ApplicationsMenuPage';
import {CommerceDNDTablePage} from '../commerceDNDTablePage';

export class CommerceAdminInventoryPage extends CommerceDNDTablePage {
	readonly applicationsMenuPage: ApplicationsMenuPage;
	readonly backLink: Locator;
	readonly changeLogLink: Locator;
	readonly editCommerceInventoryTable: Locator;
	readonly page: Page;

	constructor(page: Page) {
		super(
			page,
			'#p_p_id_com_liferay_commerce_inventory_web_internal_portlet_CommerceInventoryPortlet_ .fds table'
		);
		this.editCommerceInventoryTable = page.locator(
			'#p_p_id_com_liferay_commerce_inventory_web_internal_portlet_CommerceInventoryPortlet_ .fds table'
		);
		this.applicationsMenuPage = new ApplicationsMenuPage(page);
		this.backLink = page.locator('span[title="Back"]');
		this.changeLogLink = page.getByRole('link', {name: 'Changelog'});
		this.page = page;
	}

	async goto() {
		await this.applicationsMenuPage.goToCommerceInventory(false);
	}
}
