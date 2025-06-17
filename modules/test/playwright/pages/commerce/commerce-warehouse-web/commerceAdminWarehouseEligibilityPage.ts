/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Locator, Page} from '@playwright/test';

export class CommerceAdminWarehouseEligibilityPage {
	readonly addChannels: Locator;
	readonly detailsActiveToggle: Locator;
	readonly linkTab: Locator;
	readonly page: Page;
	readonly specificChannelRadio: Locator;
	readonly selectButton: Locator;

	constructor(page: Page) {
		this.addChannels = page.getByPlaceholder('Find a Channel');
		this.detailsActiveToggle = page.getByLabel('Active');
		this.linkTab = page.getByRole('link', {
			exact: true,
			name: 'Eligibility',
		});
		this.page = page;
		this.specificChannelRadio = page.locator(
			'[id="_com_liferay_commerce_warehouse_web_internal_portlet_CommerceInventoryWarehousePortlet_channel_3"]'
		);
		this.selectButton = page.getByRole('button', {
			exact: true,
			name: 'Select',
		});
	}
}
