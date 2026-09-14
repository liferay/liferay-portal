/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Locator, Page} from '@playwright/test';

export class PlacedOrderPage {
	readonly orderItemLink: (productName: string) => Locator;
	readonly orderPricesPanel: Locator;
	readonly page: Page;
	readonly paginationText: (text: string) => Locator;
	readonly reorderButton: Locator;
	readonly retryPaymentButton: Locator;
	readonly shipmentStatusText: (status: string) => Locator;

	constructor(page: Page) {
		this.page = page;
		this.orderItemLink = (productName: string) =>
			page.getByRole('link', {name: productName});
		this.orderPricesPanel = page
			.locator('.commerce-panel')
			.filter({has: page.getByText('Subtotal', {exact: true})});
		this.paginationText = (text: string) => page.getByText(text);
		this.reorderButton = page.getByRole('button', {name: 'Reorder'});
		this.retryPaymentButton = page.getByRole('button', {
			name: 'Retry Payment',
		});
		this.shipmentStatusText = (status: string) => page.getByText(status);
	}

	async getOrderPrices(): Promise<Record<string, string>> {
		const labels = await this.orderPricesPanel
			.locator('dt')
			.allInnerTexts();
		const values = await this.orderPricesPanel
			.locator('dd')
			.allInnerTexts();

		return Object.fromEntries(
			labels.map((label, index) => [
				label.trim().toUpperCase(),
				(values[index] ?? '').trim(),
			])
		);
	}
}
