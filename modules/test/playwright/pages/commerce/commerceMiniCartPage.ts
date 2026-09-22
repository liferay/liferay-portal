/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Locator, Page, expect} from '@playwright/test';

import {selectOptionContaining} from '../../tests/commerce/utils/selectOptionContaining';

export class CommerceMiniCartPage {
	readonly cartItemActionsButton: Locator;
	readonly editMenuItem: Locator;
	readonly editOptionsLabel: Locator;
	readonly editQuantityLabel: Locator;
	readonly editUnitOfMeasureLabel: Locator;
	readonly miniCartButton: Locator;
	readonly miniCartButtonClose: Locator;
	readonly miniCartOpenDrawer: Locator;
	readonly miniCartOverlay: Locator;
	readonly miniCartEditItemPanel: Locator;
	readonly miniCartEditItemInactivePrice: Locator;
	readonly miniCartEditItemOptionValues: (optionName: string) => Locator;
	readonly miniCartEditItemPrice: (priceName: string) => Locator;
	readonly miniCartItemDiscountLevels: (product: string | Locator) => Locator;
	readonly miniCartItemInactivePrice: (product: string | Locator) => Locator;
	readonly miniCartItemNetPrice: (product: string | Locator) => Locator;
	readonly miniCartInvalidQuantityMessage: Locator;
	readonly miniCartPriceOnApplicationInfoMessage: Locator;
	readonly miniCartItem: (productName: string) => Locator;
	readonly miniCartItemForSku: (skuName: string) => Locator;
	readonly miniCartItemForUnitOfMeasure: (
		product: string | Locator,
		unitOfMeasureKey: string
	) => Locator;
	readonly miniCartItemActionsButton: (product: string | Locator) => Locator;
	readonly miniCartItemBundledItem: (
		product: string | Locator,
		text: string
	) => Locator;
	readonly miniCartItemHideOptionsButton: (
		product: string | Locator
	) => Locator;
	readonly miniCartItemShowOptionsButton: (
		product: string | Locator
	) => Locator;
	readonly miniCartItemsContainer: Locator;
	readonly miniCartItemListPrice: (product: string | Locator) => Locator;
	readonly miniCartItemOption: (
		product: string | Locator,
		optionName: string
	) => Locator;
	readonly miniCartItemPrice: (text: RegExp, productName?: string) => Locator;
	readonly miniCartItemPriceOnApplication: (
		product: string | Locator
	) => Locator;
	readonly miniCartItemPromoPrice: (product: string | Locator) => Locator;
	readonly miniCartItemReplacementLabel: (productName: string) => Locator;
	readonly miniCartItemUnitOfMeasure: (product: string | Locator) => Locator;
	readonly miniCartReplacementInfoMessage: Locator;
	readonly miniCartResume: Locator;
	readonly miniCartSaveButton: Locator;
	readonly miniCartSku: (skuName: string) => Locator;
	readonly miniCartSummaryItem: (label: string) => Locator;
	readonly miniCartTotalPrice: Locator;
	readonly miniCartUnitOfMeasureSelector: Locator;
	readonly page: Page;
	readonly editQuantitySelector: Locator;
	readonly priceField: (
		price: string,
		container?: Locator | Page
	) => Promise<Locator>;
	readonly proceedAsGuest: Locator;
	readonly quickAddToCartButton: Locator;
	readonly quickAddToCartSku: (sku: string) => Locator;
	readonly requestAQuoteButton: Locator;
	readonly reviewOrderButton: Locator;
	readonly resubmitButton: Locator;
	readonly searchProductsInput: Locator;
	readonly selectOption: (
		optionLabel: string,
		optionName: string
	) => Promise<string[]>;
	readonly showOptionsButton: Locator;
	readonly signInToCheckoutButton: Locator;
	readonly removeAllItemsButton: Locator;
	readonly removeAllItemsConfirmButton: Locator;
	readonly submitButton: Locator;
	readonly unitOfMeasureTableLabel: Locator;
	readonly viewDetailsButton: Locator;

	constructor(page: Page) {
		this.cartItemActionsButton = page.getByTestId('cartItemActions');
		this.editOptionsLabel = page.getByText('Edit Options', {exact: true});
		this.editQuantityLabel = page.getByText('Edit Quantity', {exact: true});
		this.editQuantitySelector = page.getByRole('spinbutton');
		this.editUnitOfMeasureLabel = page.getByText('Edit Unit of Measure', {
			exact: true,
		});
		this.editMenuItem = page.getByRole('menuitem', {
			exact: true,
			name: 'Edit',
		});
		this.miniCartButton = page.getByTestId('miniCartButton');
		this.miniCartButtonClose = page.locator('.mini-cart-close');
		this.miniCartOpenDrawer = page.locator('.mini-cart.is-open');
		this.miniCartOverlay = page.locator('.mini-cart-overlay');
		this.miniCartEditItemPanel = page.locator('.mini-cart-edit-item');
		this.miniCartEditItemInactivePrice = page
			.locator('.mini-cart-edit-item')
			.locator('.price-line-through');
		this.miniCartItemDiscountLevels = (product: string | Locator) =>
			this._cartItem(product).locator('.price-value-discount > span');
		this.miniCartItemInactivePrice = (product: string | Locator) =>
			this._cartItem(product).locator(
				'.price-value-inactive:not(.price-value-promo)'
			);
		this.miniCartItemNetPrice = (product: string | Locator) =>
			this._cartItem(product).locator('.price-value-final');
		this.miniCartEditItemOptionValues = (optionName: string) =>
			this.miniCartEditItemPanel.getByLabel(optionName).locator('option');
		this.miniCartEditItemPrice = (priceName: string) =>
			this.miniCartEditItemPanel
				.locator('.mini-cart-prices > div')
				.filter({hasText: priceName})
				.locator('span')
				.last();
		this.miniCartInvalidQuantityMessage = page.getByText(
			'The product quantity is not valid.',
			{exact: true}
		);
		this.miniCartPriceOnApplicationInfoMessage = page.getByText(
			'Your cart has products that require a quote to complete the checkout.'
		);
		this.miniCartItemsContainer = page.locator('div.mini-cart-cart-items');
		this.miniCartItem = (productName: string) =>
			page.locator('div.mini-cart-item').filter({hasText: productName});
		this.miniCartItemForSku = (skuName: string) =>
			page
				.locator('div.mini-cart-item')
				.filter({has: page.getByText(skuName, {exact: true})});
		this.miniCartItemForUnitOfMeasure = (
			product: string | Locator,
			unitOfMeasureKey: string
		) =>
			this._cartItem(product).filter({
				has: page
					.locator('.mini-cart-item-quantity')
					.getByText(unitOfMeasureKey, {exact: true}),
			});
		this.miniCartItemActionsButton = (product: string | Locator) =>
			this._cartItem(product).getByTestId('cartItemActions');
		this.miniCartItemBundledItem = (
			product: string | Locator,
			text: string
		) =>
			this._cartItem(product)
				.locator('.child-items')
				.locator('.child-item, .item-info-extra')
				.filter({hasText: text});
		this.miniCartItemHideOptionsButton = (product: string | Locator) =>
			this._cartItem(product).getByRole('button', {
				exact: true,
				name: 'Hide Options',
			});
		this.miniCartItemShowOptionsButton = (product: string | Locator) =>
			this._cartItem(product).getByRole('button', {
				exact: true,
				name: 'Show Options',
			});
		this.miniCartItemOption = (
			product: string | Locator,
			optionName: string
		) =>
			this._cartItem(product)
				.locator('.child-items .item-info-extra')
				.filter({
					has: page
						.locator('.item-name')
						.getByText(optionName, {exact: true}),
				})
				.locator('.item-sku');
		this.miniCartItemListPrice = (product: string | Locator) =>
			this._cartItem(product).locator(
				'.mini-cart-item-price .price-value:not(.price-value-promo)'
			);
		this.miniCartItemPriceOnApplication = (product: string | Locator) =>
			this._cartItem(product).locator(
				'.price-on-application.price-value'
			);
		this.miniCartItemPromoPrice = (product: string | Locator) =>
			this._cartItem(product).locator(
				'.mini-cart-item-price .price-value-promo'
			);
		this.miniCartItemPrice = (text: RegExp, productName?: string) =>
			(productName ? this.miniCartItem(productName) : page)
				.locator('div')
				.filter({hasText: text})
				.first();
		this.miniCartItemReplacementLabel = (productName: string) =>
			this.miniCartItem(productName).getByText('Replacement', {
				exact: true,
			});
		this.miniCartItemUnitOfMeasure = (product: string | Locator) =>
			this._cartItem(product).locator('.mini-cart-item-quantity .ml-2');
		this.miniCartReplacementInfoMessage = page.getByText(
			'There are replacement products in your cart.'
		);
		this.miniCartResume = page.locator('.mini-cart-header-resume');
		this.miniCartSaveButton = page
			.locator('.mini-cart-footer')
			.getByRole('button', {
				exact: true,
				name: 'Save',
			});
		this.miniCartSku = (skuName: string) =>
			this.miniCartItemsContainer.getByText(skuName, {exact: true});
		this.miniCartSummaryItem = (label: string) =>
			page
				.locator('.summary-table > div')
				.filter({hasText: new RegExp(`^${label}$`)})
				.locator('xpath=following-sibling::div[1]');
		this.miniCartTotalPrice = page.locator(
			`xpath=//div[text()='Total']/../following-sibling::div/div`
		);
		this.miniCartUnitOfMeasureSelector = page.locator(
			'select[name="minicart-uom-selector"]'
		);
		this.priceField = async (price: string, container = this.page) => {
			return container.getByText(price);
		};
		this.proceedAsGuest = page.getByRole('button', {
			name: 'Proceed as Guest',
		});
		this.requestAQuoteButton = page
			.locator('.mini-cart-wrapper')
			.getByRole('button', {
				name: 'Request A Quote',
			});
		this.resubmitButton = page.getByRole('button', {
			exact: true,
			name: 'Resubmit',
		});
		this.reviewOrderButton = page.getByRole('button', {
			exact: true,
			name: 'Review Order',
		});
		this.quickAddToCartButton = page.getByTestId('quickAddToCartButton');
		this.quickAddToCartSku = (sku) =>
			page.getByRole('menuitem', {name: sku});
		this.selectOption = (optionLabel: string, optionName: string) =>
			page.getByLabel(optionName).selectOption({label: optionLabel});
		this.searchProductsInput = page.getByPlaceholder('Search Products');
		this.showOptionsButton = page.getByRole('button', {
			exact: true,
			name: 'Show Options',
		});
		this.signInToCheckoutButton = page.getByRole('button', {
			name: 'Sign In to Checkout',
		});
		this.removeAllItemsButton = page.getByRole('button', {
			exact: true,
			name: 'Remove All Items',
		});
		this.removeAllItemsConfirmButton = page.getByRole('button', {
			exact: true,
			name: 'Yes',
		});
		this.submitButton = page.getByRole('button', {name: 'Submit'});
		this.unitOfMeasureTableLabel = page.getByText('Unit of Measure Table', {
			exact: true,
		});
		this.viewDetailsButton = page.getByRole('button', {
			exact: true,
			name: 'View Details',
		});
	}

	_cartItem(product: string | Locator) {
		return typeof product === 'string'
			? this.miniCartItem(product)
			: product;
	}

	async close() {
		if (await this.miniCartOpenDrawer.count()) {
			await this.miniCartButtonClose.click();

			await expect(this.miniCartOpenDrawer).toHaveCount(0);
		}
	}

	async open() {
		if (!(await this.miniCartOpenDrawer.count())) {
			await this.miniCartButton.click();
		}

		await expect(this.miniCartOpenDrawer).toBeVisible();
	}

	async quickAddToCart(sku: string) {
		if (await this.searchProductsInput.isHidden()) {
			await this.miniCartButton.click();
		}

		await expect(this.miniCartButtonClose).toBeVisible();

		await expect(async () => {
			await this.searchProductsInput.fill(sku);
			await expect(this.quickAddToCartSku(sku)).toBeVisible();
		}).toPass();

		await this.quickAddToCartSku(sku).click();
		await this.quickAddToCartButton.click();
	}

	async selectEditItemOption(optionLabel: string, optionName: string) {
		await selectOptionContaining(
			this.miniCartEditItemPanel.getByLabel(optionName),
			optionLabel
		);
	}

	async showItemOptions(product: string | Locator) {
		const showOptionsButton = this.miniCartItemShowOptionsButton(product);

		if (await showOptionsButton.isVisible()) {
			await showOptionsButton.click();
		}

		await expect(this.miniCartItemHideOptionsButton(product)).toBeVisible();
	}
}
