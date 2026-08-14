/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Locator, Page} from '@playwright/test';

import {DataTablePage} from '../account-admin-web/DataTablePage';
import {GlobalMenuPage} from '../product-navigation-applications-menu/GlobalMenuPage';

export class PushNotificationsPage {
	readonly devicesNavigationItem: Locator;
	readonly devicesTable: DataTablePage;
	readonly globalMenuPage: GlobalMenuPage;
	readonly page: Page;
	readonly testNavigationItem: Locator;

	constructor(page: Page) {
		this.devicesNavigationItem = page.getByRole('link', {name: 'Devices'});
		this.devicesTable = new DataTablePage(
			page,
			page.locator(
				'#portlet_com_liferay_push_notifications_web_portlet_PushNotificationsPortlet'
			)
		);
		this.globalMenuPage = new GlobalMenuPage(page);
		this.page = page;
		this.testNavigationItem = page.getByRole('link', {name: 'Test'});
	}

	async goto() {
		await this.globalMenuPage.goToControlPanel('Push Notifications');
	}
}
