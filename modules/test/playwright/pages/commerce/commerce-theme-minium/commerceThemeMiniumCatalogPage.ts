/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {FrameLocator, Locator, Page, expect} from '@playwright/test';

import {clickAndExpectToBeVisible} from '../../../utils/clickAndExpectToBeVisible';

export class CommerceThemeMiniumCatalogPage {
	readonly accountSelectorAccount: (accountName: string) => Locator;
	readonly accountSelectorBackButton: Locator;
	readonly accountSelectorButton: Locator;
	readonly accountSelectorDropdownMenu: Locator;
	readonly accountSelectorNoAccountsMessage: Locator;
	readonly accountSelectorNoOrderSelectedMessage: Locator;
	readonly accountSelectorOrderId: Locator;
	readonly accountSelectorOrderLink: (orderId: string) => Locator;
	readonly accountSelectorOrdersList: Locator;
	readonly accountSelectorOrderWorkflowStatus: Locator;
	readonly accountSelectorSearchAccountInput: Locator;
	readonly accountSelectorSearchOrderInput: Locator;
	readonly accountSelectorSelectedAccount: Locator;
	readonly addToCartFragment: Locator;
	readonly addToCartFragmentButton: Locator;
	readonly catalogSearch: Locator;
	readonly clearSearchButton: Locator;
	readonly configurationIFrame: FrameLocator;
	readonly configurationIFrameCloseButton: Locator;
	readonly configurationIFrameDefaultSortingDropdownMenu: Locator;
	readonly configurationIFrameSaveButton: Locator;
	readonly configurationMenuItem: Locator;
	readonly createNewAccountButton: Locator;
	readonly createNewAccountModal: Locator;
	readonly createNewAccountModalCancelButton: Locator;
	readonly createNewAccountModalNameInput: Locator;
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
	readonly quantitySelectorList: (targetLocator: Locator) => Locator;
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
	readonly productCardAddToWishListButton: (productName: string) => Locator;
	readonly productCardFragment: Locator;
	readonly productCardFragmentAddToCartButton: (
		targetLocator: Locator
	) => Locator;
	readonly productCardFragmentAddToWishListButton: (
		targetLocator: Locator
	) => Locator;
	readonly productCardFragmentAvailabilityLabel: (
		targetLocator: Locator
	) => Locator;
	readonly productCardFragmentCompareCheckbox: (
		targetLocator: Locator
	) => Locator;
	readonly productCardFragmentDescription: (
		targetLocator: Locator
	) => Locator;
	readonly productCardFragmentImage: (targetLocator: Locator) => Locator;
	readonly productCardFragmentInactivePrice: (
		targetLocator: Locator
	) => Locator;
	readonly productCardFragmentName: (
		targetLocator: Locator,
		productName: string
	) => Locator;
	readonly productCardFragmentNetPrice: (targetLocator: Locator) => Locator;
	readonly productCardFragmentPrice: (
		targetLocator: Locator,
		productPrice: string
	) => Locator;
	readonly productCardFragmentPriceOnApplicationLabel: (
		targetLocator: Locator
	) => Locator;
	readonly productCardFragmentPromoPrice: (targetLocator: Locator) => Locator;
	readonly productCardFragmentSku: (
		targetLocator: Locator,
		productSku: string
	) => Locator;
	readonly productCardFragmentViewButton: (targetLocator: Locator) => Locator;
	readonly productCardFragmentWishListFullIcon: (
		targetLocator: Locator
	) => Locator;
	readonly productCardFragmentWishListToggle: (
		targetLocator: Locator
	) => Locator;
	readonly productLink: (productName: string) => Locator;

	constructor(page: Page) {
		this.accountSelectorAccount = (accountName: string) =>
			page
				.locator('.dropdown-menu.show')
				.getByText(accountName, {exact: false});
		this.accountSelectorBackButton = page
			.locator('.dropdown-menu.show')
			.getByRole('button', {exact: true, name: 'Back to Accounts'});
		this.accountSelectorButton = page
			.locator('.account-selector-dropdown')
			.getByRole('button');
		this.accountSelectorDropdownMenu = page.locator(
			'.account-selector-dropdown-menu.show'
		);
		this.accountSelectorNoAccountsMessage =
			this.accountSelectorDropdownMenu.getByText(
				'No accounts were found.',
				{exact: true}
			);
		this.accountSelectorNoOrderSelectedMessage =
			this.accountSelectorButton.getByText(
				'There is no order selected.',
				{exact: true}
			);
		this.accountSelectorOrderId =
			this.accountSelectorButton.locator('.order-id');
		this.accountSelectorOrderLink = (orderId: string) =>
			page
				.locator('.orders-table')
				.getByRole('button', {exact: true, name: orderId});
		this.accountSelectorOrdersList = page.locator('.orders-list');
		this.accountSelectorOrderWorkflowStatus =
			this.accountSelectorButton.locator('.workflow-status');
		this.accountSelectorSearchAccountInput =
			this.accountSelectorDropdownMenu.getByPlaceholder('Search', {
				exact: true,
			});
		this.accountSelectorSearchOrderInput =
			this.accountSelectorDropdownMenu.getByPlaceholder('Search Order');
		this.accountSelectorSelectedAccount =
			this.accountSelectorButton.locator('.account-name');
		this.addToCartFragment = page.locator('.add-to-cart');
		this.addToCartFragmentButton = this.addToCartFragment.locator(
			'button.btn-add-to-cart:not(.skeleton)'
		);
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
		this.createNewAccountButton =
			this.accountSelectorDropdownMenu.getByRole('button', {
				name: 'Create New Account',
			});
		this.createNewAccountModal = page.locator('.modal-content');
		this.createNewAccountModalCancelButton =
			this.createNewAccountModal.getByRole('button', {name: 'Cancel'});
		this.createNewAccountModalNameInput =
			this.createNewAccountModal.locator('input[name="accountName"]');
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
		this.quantitySelectorList = (targetLocator: Locator) =>
			targetLocator.locator('select.quantity-selector');
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
		this.productCardAddToWishListButton = (productName: string) =>
			this.productCard(productName).getByRole('button', {
				exact: true,
				name: 'Add to List',
			});
		this.productCardFragment = page.locator('.cp-renderer .product-card');
		this.productCardFragmentAddToCartButton = (targetLocator: Locator) =>
			targetLocator.getByRole('button', {
				exact: true,
				name: 'Add to Cart',
			});
		this.productCardFragmentAddToWishListButton = (
			targetLocator: Locator
		) => targetLocator.locator('.add-to-wish-list');
		this.productCardFragmentAvailabilityLabel = (targetLocator: Locator) =>
			targetLocator.locator('[class*="availability-label"]');
		this.productCardFragmentCompareCheckbox = (targetLocator: Locator) =>
			targetLocator.locator('.compare-checkbox');
		this.productCardFragmentDescription = (targetLocator: Locator) =>
			targetLocator.locator('.two-lined-description');
		this.productCardFragmentImage = (targetLocator: Locator) =>
			targetLocator.locator('img.product-card-picture');
		this.productCardFragmentInactivePrice = (targetLocator: Locator) =>
			targetLocator.locator('.price-value-inactive');
		this.productCardFragmentName = (
			targetLocator: Locator,
			productName: string
		) =>
			targetLocator
				.locator('.card-title')
				.getByText(productName, {exact: true});
		this.productCardFragmentNetPrice = (targetLocator: Locator) =>
			targetLocator.locator('.price-value-final');
		this.productCardFragmentPrice = (
			targetLocator: Locator,
			productPrice: string
		) =>
			targetLocator
				.locator('.card-text')
				.getByText(productPrice, {exact: true});
		this.productCardFragmentPriceOnApplicationLabel = (
			targetLocator: Locator
		) =>
			targetLocator
				.locator('.card-text .price-value')
				.filter({hasText: 'Price on Application'});
		this.productCardFragmentPromoPrice = (targetLocator: Locator) =>
			targetLocator.locator('.price-value-promo');
		this.productCardFragmentSku = (
			targetLocator: Locator,
			productSku: string
		) =>
			targetLocator
				.locator('.card-subtitle')
				.getByText(productSku, {exact: true});
		this.productCardFragmentViewButton = (targetLocator: Locator) =>
			targetLocator.getByRole('button', {exact: true, name: 'View'});
		this.productCardFragmentWishListFullIcon = (targetLocator: Locator) =>
			targetLocator.locator(
				'.add-to-wish-list svg.lexicon-icon-heart-full'
			);
		this.productCardFragmentWishListToggle = (targetLocator: Locator) =>
			targetLocator.locator('.add-to-wish-list button:not(.skeleton)');
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

	async addToCart(productName: string) {
		await this.page.waitForLoadState('networkidle');

		await this.productCardAddToCartButton(productName).click();

		await this.page.waitForLoadState('networkidle');
	}

	async openAccountSelectorDropdown() {
		await clickAndExpectToBeVisible({
			target: this.accountSelectorDropdownMenu,
			trigger: this.accountSelectorButton,
		});

		await expect(async () => {
			if (await this.accountSelectorBackButton.isVisible()) {
				await this.accountSelectorBackButton.click({timeout: 500});
			}

			await expect(this.accountSelectorSearchAccountInput).toBeVisible({
				timeout: 500,
			});
		}).toPass({timeout: 5000});
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
		await this.page.waitForLoadState('networkidle');

		await expect(async () => {
			await this.globalSearchBarButton.click();

			await expect(this.globalSearchBarInput).toBeVisible();
		}).toPass();
	}
}
