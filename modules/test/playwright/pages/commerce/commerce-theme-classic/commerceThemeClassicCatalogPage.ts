/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Locator, Page} from '@playwright/test';

export class CommerceThemeClassicCatalogPage {
	readonly changeCurrencyModal: Locator;
	readonly changeCurrencyModalHeading: Locator;
	readonly changeCurrencyModalProceedButton: Locator;
	readonly currencyListItem: (currencyCode: string) => Locator;
	readonly currencySelectorButton: (
		currencyCode: string,
		currencySymbol: string
	) => Locator;
	readonly firstCardItem: Locator;
	readonly orderByButton: Locator;
	readonly ordersTab: (orderTabName: string) => Locator;
	readonly page: Page;
	readonly productCard: (productName: string) => Locator;
	readonly productCardImage: (productName: string) => Locator;
	readonly productCardPrice: (
		productName: string,
		productPrice: string
	) => Locator;
	readonly productCardSku: (
		productName: string,
		productSku: string
	) => Locator;
	readonly productCardAddToCartButton: (productName: string) => Locator;
	readonly productCardAddToWishListButton: (productName: string) => Locator;
	readonly productCardLink: (productName: string) => Locator;

	constructor(page: Page) {
		this.changeCurrencyModal = page.locator('.modal-content');
		this.changeCurrencyModalHeading = this.changeCurrencyModal.getByRole(
			'heading',
			{name: 'Change Active Currency'}
		);
		this.changeCurrencyModalProceedButton =
			this.changeCurrencyModal.getByRole('button', {
				exact: true,
				name: 'Proceed',
			});
		this.currencyListItem = (currencyCode) =>
			page.locator(`[data-testid*=${currencyCode}]`);
		this.currencySelectorButton = (currencyCode, currencySymbol) =>
			page.getByRole('button', {
				exact: true,
				name: `${currencySymbol} ${currencyCode}`,
			});
		this.firstCardItem = page.locator('.product-card').first();
		this.orderByButton = page.locator('#commerce-order-by');
		this.ordersTab = (orderTabName: string) =>
			page.getByRole('menuitem', {
				exact: true,
				name: orderTabName,
			});
		this.page = page;
		this.productCard = (productName: string) =>
			this.page.locator('.product-card').filter({hasText: productName});
		this.productCardImage = (productName: string) =>
			this.productCard(productName).locator('img.product-card-picture');
		this.productCardPrice = (productName, productPrice) =>
			this.productCard(productName).getByText(productPrice, {
				exact: true,
			});
		this.productCardSku = (productName: string, productSku: string) =>
			this.productCard(productName).getByText(productSku);
		this.productCardAddToCartButton = (productName: string) =>
			this.productCard(productName).getByRole('button', {
				exact: true,
				name: 'Add to Cart',
			});
		this.productCardAddToWishListButton = (productName: string) =>
			this.productCard(productName).getByRole('button', {
				exact: true,
				name: 'Add to List',
			});
		this.productCardLink = (productName: string) =>
			this.productCard(productName).getByRole('link', {
				name: productName,
			});
	}

	async selectSorting(orderByText: string) {
		await this.orderByButton.click();
		const orderByLink = this.page.getByText(orderByText);
		await orderByLink.click();
		await this.page.waitForLoadState('networkidle');
	}

	async goToOrderPages(orderTabName: string) {
		await this.ordersTab('Orders').click();
		await this.ordersTab(orderTabName).click();
	}
}
