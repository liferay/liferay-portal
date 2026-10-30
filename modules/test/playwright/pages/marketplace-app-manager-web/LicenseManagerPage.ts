/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {FrameLocator, Locator, Page} from '@playwright/test';

import {GlobalMenuPage} from '../product-navigation-applications-menu/GlobalMenuPage';

export class LicenseManagerPage {
	readonly globalMenuPage: GlobalMenuPage;
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
		this.globalMenuPage = new GlobalMenuPage(page);
		this.frame = page.frameLocator(
			'[id="_com_liferay_license_manager_web_portlet_LicenseManagerPortlet_iframe"]'
		);
		this.frameHostNameCell = this.frame.getByRole('columnheader', {
			exact: true,
			name: 'Host Name',
		});
		this.frameIPAddressCell = this.frame.getByRole('columnheader', {
			exact: true,
			name: 'IP Addresses',
		});
		this.frameLicensesRegisteredCell = this.frame.getByRole(
			'columnheader',
			{
				exact: true,
				name: 'Licenses Registered',
			}
		);
		this.frameOwnerCell = this.frame.getByRole('columnheader', {
			exact: true,
			name: 'Owner',
		});
		this.frameServerInfoCell = this.frame.getByRole('columnheader', {
			exact: true,
			name: 'Server Info',
		});
		this.frameStartDateCell = this.frame.getByRole('columnheader', {
			exact: true,
			name: 'Start Date',
		});
		this.headerTitle = page.getByTestId('headerTitle');
		this.page = page;
	}

	async goto() {
		await this.globalMenuPage.goToControlPanel('License Manager');
	}
}
