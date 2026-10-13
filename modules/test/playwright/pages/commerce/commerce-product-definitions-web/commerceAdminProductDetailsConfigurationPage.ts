/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Locator, Page} from '@playwright/test';

export class CommerceAdminProductDetailsConfigurationPage {
	readonly minStockQuantityInput: Locator;
	readonly orderQuantityErrorMessage: (orderQuantityLabel: string) => Locator;
	readonly orderQuantityInput: (orderQuantityLabel: string) => Locator;
	readonly page: Page;
	readonly publishLink: Locator;
	readonly purchasableInput: Locator;

	constructor(page: Page) {
		this.minStockQuantityInput = page.getByTestId('minStockQuantityInput');
		this.orderQuantityErrorMessage = (orderQuantityLabel: string) =>
			page
				.locator('.form-group')
				.filter({has: this.orderQuantityInput(orderQuantityLabel)})
				.getByRole('alert');
		this.orderQuantityInput = (orderQuantityLabel: string) =>
			page.getByLabel(orderQuantityLabel);
		this.page = page;
		this.publishLink = page.getByRole('link', {name: 'Publish'});
		this.purchasableInput = page.getByTestId('purchasableInput');
	}
}
