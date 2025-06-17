/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {FrameLocator, Locator, Page, expect} from '@playwright/test';

export class CommerceThemeMiniumCatalogPage {
	readonly accountSelectorButton: Locator;
	readonly catalogSearch: Locator;
	readonly clearSearchButton: Locator;
	readonly configurationIFrame: FrameLocator;
	readonly configurationIFrameCloseButton: Locator;
	readonly configurationIFrameDefaultSortingDropdownMenu: Locator;
	readonly configurationIFrameSaveButton: Locator;
	readonly configurationMenuItem: Locator;
	readonly createNewOrderButton: Locator;
	readonly firstCardItem: Locator;
	readonly firstCardItemAddToCartButton: Locator;
	readonly globalSearchBarButton: Locator;
	readonly globalSearchBarInput: Locator;
	readonly globalSearchBarCommerceItemLink: (text: string) => Locator;
	readonly globalSearchBarCommerceOrderLink: (
		orderId: string,
		accountName: string
	) => Locator;
	readonly quantitySelector: (targetLocator: Locator) => Locator;
	readonly quantitySelectorErrorContainer: (
		targetLocator: Locator
	) => Locator;
	readonly optionsButton: Locator;
	readonly orderByButton: Locator;
	readonly page: Page;
	readonly popOverMessage: (popOverMessage: string) => Locator;
	readonly productCard: (productName: string) => Locator;
	readonly productCardPrice: (
		productName: string,
		productPrice: string
	) => Locator;
	readonly productCardAddToCartButton: (productName: string) => Locator;
	readonly productLink: (productName: string) => Locator;

	constructor(page: Page) {
		this.accountSelectorButton = page.locator('.account-selector-dropdown');
		this.catalogSearch = page.getByTestId('searchInput');
		this.clearSearchButton = page.getByRole('button', {
			name: 'Clear Search',
		});
		this.configurationIFrame = page.frameLocator(
			'iframe[id="modalIframe"]'
		);
		this.configurationIFrameCloseButton =
			this.configurationIFrame.getByRole('button', {name: 'Close'});
		this.configurationIFrameDefaultSortingDropdownMenu =
			this.configurationIFrame.getByLabel('Default Sort');
		this.configurationIFrameSaveButton = this.configurationIFrame.getByRole(
			'button',
			{name: 'Save'}
		);
		this.configurationMenuItem = page.getByRole('menuitem', {
			exact: true,
			name: 'Configuration',
		});
		this.createNewOrderButton = page.getByRole('button', {
			name: 'Create New Order',
		});
		this.firstCardItem = page.locator('.product-card').first();
		this.firstCardItemAddToCartButton = this.firstCardItem.getByRole(
			'button',
			{name: 'Add to Cart'}
		);
		this.globalSearchBarButton = page
			.locator('.commerce-topbar-button__icon')
			.first();
		this.globalSearchBarInput = page
			.locator('#search-bar')
			.getByPlaceholder('Search');
		this.globalSearchBarCommerceItemLink = (text) =>
			page.getByRole('link', {name: text});
		this.globalSearchBarCommerceOrderLink = (
			orderId: string,
			accountName: string
		) =>
			page
				.getByRole('link', {name: orderId})
				.filter({hasText: accountName});
		this.quantitySelector = (targetLocator: Locator) =>
			targetLocator.getByRole('spinbutton');
		this.quantitySelectorErrorContainer = (targetLocator: Locator) =>
			this.quantitySelector(targetLocator).locator('..');
		this.optionsButton = page
			.locator(
				'[id^="portlet_com_liferay_commerce_product_content_search_web_internal_portlet_CPSortPortlet"]'
			)
			.getByTitle('Options');
		this.orderByButton = page.locator('#commerce-order-by');
		this.page = page;
		this.popOverMessage = (popOverMessage: string) =>
			this.page
				.locator('.popover-body')
				.getByText(popOverMessage, {exact: true});
		this.productCard = (productName: string) =>
			this.page.locator('.product-card').filter({hasText: productName});
		this.productCardPrice = (productName, productPrice) =>
			this.productCard(productName).getByText(productPrice, {
				exact: true,
			});
		this.productCardAddToCartButton = (productName: string) =>
			this.productCard(productName).getByRole('button', {
				exact: true,
				name: 'Add to Cart',
			});
		this.productLink = (productName: string) =>
			this.page.getByRole('link', {
				exact: true,
				name: productName,
			});
	}

	getProductMinQuantity(
		minQuantity = 1,
		multipleQuantity = 1,
		precision = 0
	) {
		let result = multipleQuantity;

		while (result < minQuantity) {
			result += result;
		}

		return parseFloat(result.toFixed(precision));
	}

	getMultipleQuantity(
		incrementalOrderQuantity = 0,
		multipleQuantity = 1,
		precision = 0
	) {
		if (incrementalOrderQuantity === 0) {
			return multipleQuantity;
		}

		const scalingFactor = Math.pow(10, precision);

		const roundedValue =
			Math.round(
				(incrementalOrderQuantity + Number.EPSILON) * scalingFactor
			) / scalingFactor;

		let result = roundedValue % multipleQuantity;

		if (roundedValue < multipleQuantity) {
			result =
				incrementalOrderQuantity * scalingFactor * multipleQuantity;
			if (Number.isInteger(result / 2)) {
				return parseFloat((result / 2).toFixed(precision));
			}

			return parseFloat(
				(
					incrementalOrderQuantity *
					scalingFactor *
					multipleQuantity
				).toFixed(precision)
			);
		}

		if (result !== 0) {
			return parseFloat(
				(roundedValue - result + multipleQuantity).toFixed(precision)
			);
		}
	}

	getProductMaxQuantity(
		maxQuantity: number,
		multipleQuantity: number,
		precision = 0
	) {
		const maxDifference = maxQuantity % multipleQuantity;

		if (!maxDifference || maxQuantity < multipleQuantity) {
			return parseFloat(maxQuantity.toFixed(precision));
		}

		return parseFloat(
			Number(maxQuantity - maxDifference).toFixed(precision)
		);
	}

	async checkQuantitiesInPopOverMessages(
		maxQuantity: number,
		minQuantity: number,
		multipleQuantity: number,
		maxQuantityNotSatisfied = false,
		minQuantityNotSatisfied = false,
		multipleQuantityNotSatisfied = false
	) {
		if (multipleQuantityNotSatisfied) {
			await expect(
				this.popOverMessage(
					'Quantity must be a multiple of ' + multipleQuantity
				)
			).toHaveClass('text-danger');
		}
		else {
			await expect(
				this.popOverMessage(
					'Quantity must be a multiple of ' + multipleQuantity
				)
			).toBeVisible();
		}
		if (minQuantityNotSatisfied) {
			await expect(
				this.popOverMessage('Min quantity per order is ' + minQuantity)
			).toHaveClass('text-danger');
		}
		else {
			await expect(
				this.popOverMessage('Min quantity per order is ' + minQuantity)
			).toBeVisible();
		}
		if (maxQuantityNotSatisfied) {
			await expect(
				this.popOverMessage(
					'Maximum quantity per order is ' + maxQuantity + '.'
				)
			).toHaveClass('text-danger');
		}
		else {
			await expect(
				this.popOverMessage(
					'Maximum quantity per order is ' + maxQuantity + '.'
				)
			).toBeVisible();
		}
	}

	async selectSorting(orderByText: string) {
		await this.orderByButton.click();
		const orderByLink = this.page.getByText(orderByText);
		await orderByLink.click();
		await this.page.waitForLoadState('networkidle');
	}

	async search(query: string) {
		await this.globalSearchBarInput.waitFor({state: 'visible'});
		await this.globalSearchBarInput.fill(query);
	}

	async focusGlobalSearchBarInput() {
		await expect(this.globalSearchBarButton).toBeAttached();
		await this.globalSearchBarButton.click();
	}
}
