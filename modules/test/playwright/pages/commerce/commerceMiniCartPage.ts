/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Locator, Page, expect} from '@playwright/test';

export class CommerceMiniCartPage {
	readonly cartItemActionsButton: Locator;
	readonly editMenuItem: Locator;
	readonly editOptionsLabel: Locator;
	readonly editQuantityLabel: Locator;
	readonly editUnitOfMeasureLabel: Locator;
	readonly miniCartButton: Locator;
	readonly miniCartButtonClose: Locator;
	readonly miniCartInvalidQuantityMessage: Locator;
	readonly miniCartItem: (productName: string) => Locator;
	readonly miniCartItemsContainer: Locator;
	readonly miniCartItemPrice: (text: RegExp) => Locator;
	readonly miniCartSaveButton: Locator;
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
	readonly reviewOrderButton: Locator;
	readonly searchProductsInput: Locator;
	readonly selectOption: (
		optionLabel: string,
		optionName: string
	) => Promise<string[]>;
	readonly showOptionsButton: Locator;
	readonly signInToCheckoutButton: Locator;
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
		this.miniCartInvalidQuantityMessage = page.getByText(
			'The product quantity is not valid.',
			{exact: true}
		);
		this.miniCartItemsContainer = page.locator('div.mini-cart-cart-items');
		this.miniCartItem = (productName: string) =>
			page.locator('div.mini-cart-item').filter({hasText: productName});
		this.miniCartItemPrice = (text: RegExp) =>
			page.locator('div').filter({hasText: text}).first();
		this.miniCartSaveButton = page
			.locator('.mini-cart-footer')
			.getByRole('button', {
				exact: true,
				name: 'Save',
			});
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
		this.submitButton = page.getByRole('button', {name: 'Submit'});
		this.unitOfMeasureTableLabel = page.getByText('Unit of Measure Table', {
			exact: true,
		});
		this.viewDetailsButton = page.getByRole('button', {
			exact: true,
			name: 'View Details',
		});
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
}
