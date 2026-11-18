/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Locator, Page} from '@playwright/test';

export class CommerceShipmentContentPage {
	readonly page: Page;
	readonly shipmentAddress: Locator;
	readonly shipmentAddressImage: Locator;

	constructor(page: Page) {
		this.page = page;
		this.shipmentAddress = page.locator('.shipment-address');
		this.shipmentAddressImage = this.shipmentAddress.locator('img');
	}
}
