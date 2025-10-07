/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {FrameLocator, Locator, Page} from '@playwright/test';

import {ApplicationsMenuPage} from '../product-navigation-applications-menu/ApplicationsMenuPage';

export class LicenseManagerPage {
	readonly applicationsMenuPage: ApplicationsMenuPage;
	readonly frame: FrameLocator;
	readonly frameHostNameCell: Locator;
	readonly frameIPAddressCell: Locator;
	readonly frameLicensesRegisteredCell: Locator;
	readonly frameOwnerCell: Locator;
	readonly frameServerInfoCell: Locator;
	readonly frameStartDateCell: Locator;
	readonly headerTitle: Locator;
	readonly page: Page;

	constructor(page: Page) {
		this.applicationsMenuPage = new ApplicationsMenuPage(page);
		this.frame = page.frameLocator(
			'[id="_com_liferay_license_manager_web_portlet_LicenseManagerPortlet_iframe"]'
		);
		this.frameHostNameCell = this.frame.getByRole('cell', {
			exact: true,
			name: 'Host Name',
		});
		this.frameIPAddressCell = this.frame.getByRole('cell', {
			exact: true,
			name: 'IP Addresses',
		});
		this.frameLicensesRegisteredCell = this.frame.getByRole('cell', {
			exact: true,
			name: 'Licenses Registered',
		});
		this.frameOwnerCell = this.frame.getByRole('cell', {
			exact: true,
			name: 'Owner',
		});
		this.frameServerInfoCell = this.frame.getByRole('cell', {
			exact: true,
			name: 'Server Info',
		});
		this.frameStartDateCell = this.frame.getByRole('cell', {
			exact: true,
			name: 'Start Date',
		});
		this.headerTitle = page.getByTestId('headerTitle');
		this.page = page;
	}

	async goto() {
		await this.applicationsMenuPage.goToLicenseManager();
	}
}
