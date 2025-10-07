/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Locator, Page} from '@playwright/test';

export class CommerceAdminProductConfigurationListPage {
	readonly accountElgibilityTitle: Locator;
	readonly allAccountsLabel: Locator;
	readonly allChannelsLabel: Locator;
	readonly allOrderTypesLabel: Locator;
	readonly allowedOrderQuantitiesInput: Locator;
	readonly backOrdersInput: Locator;
	readonly catalogNameInput: Locator;
	readonly channelElgibilityTitle: Locator;
	readonly commerceAvailabilityEstimateIdInput: Locator;
	readonly CPDefinitionInventoryEngineInput: Locator;
	readonly CPTaxCategoryIdInput: Locator;
	readonly customFieldInput: (name: string) => Locator;
	readonly depthInput: Locator;
	readonly detailsMenuItem: Locator;
	readonly displayAvailabilityInput: Locator;
	readonly displayDateInput: Locator;
	readonly displayStockQuantityInput: Locator;
	readonly eligibilitiesTab: Locator;
	readonly entriesMenuItem: Locator;
	readonly expirationDateInput: Locator;
	readonly freeShippingInput: Locator;
	readonly heightInput: Locator;
	readonly lowStockActivityInput: Locator;
	readonly maxOrderQuantityInput: Locator;
	readonly minOrderQuantityInput: Locator;
	readonly minStockQuantityInput: Locator;
	readonly multipleOrderQuantityInput: Locator;
	readonly nameInput: Locator;
	readonly neverExpireInput: Locator;
	readonly orderTypeElgibilityTitle: Locator;
	readonly page: Page;
	readonly parentCPConfigurationListNameInput: Locator;
	readonly priorityInput: Locator;
	readonly purchasableInput: Locator;
	readonly saveButton: Locator;
	readonly shippableInput: Locator;
	readonly shipSeparatelyInput: Locator;
	readonly taxExemptInput: Locator;
	readonly weightInput: Locator;
	readonly widthInput: Locator;

	constructor(page: Page) {
		this.accountElgibilityTitle = page.getByText('Account Eligibility');
		this.allAccountsLabel = page.getByLabel('All Accounts');
		this.allChannelsLabel = page.getByLabel('All Channels');
		this.allOrderTypesLabel = page.getByLabel('All Order Types');
		this.allowedOrderQuantitiesInput = page.getByTestId(
			'allowedOrderQuantitiesInput'
		);
		this.backOrdersInput = page.getByTestId('backOrdersInput');
		this.catalogNameInput = page.getByTestId('catalogNameInput');
		this.channelElgibilityTitle = page.getByText('Channel Eligibility');
		this.commerceAvailabilityEstimateIdInput = page.getByTestId(
			'commerceAvailabilityEstimateIdInput'
		);
		this.CPDefinitionInventoryEngineInput = page.getByTestId(
			'CPDefinitionInventoryEngineInput'
		);
		this.CPTaxCategoryIdInput = page.getByTestId('CPTaxCategoryIdInput');
		this.customFieldInput = (name) =>
			page.locator(`.field[name*="${name}"]`);
		this.depthInput = page.getByTestId('depthInput');
		this.detailsMenuItem = page.getByRole('link', {name: 'Details'});
		this.displayAvailabilityInput = page.getByTestId(
			'displayAvailabilityInput'
		);
		this.displayDateInput = page
			.getByTestId('displayDate')
			.getByLabel('Display Date');
		this.displayStockQuantityInput = page.getByTestId(
			'displayStockQuantityInput'
		);
		this.eligibilitiesTab = page.getByRole('link', {
			name: 'Eligibility',
		});
		this.entriesMenuItem = page.getByRole('link', {name: 'Entries'});
		this.expirationDateInput = page
			.getByTestId('expirationDate')
			.getByLabel('Expiration Date');
		this.freeShippingInput = page.getByTestId('freeShippingInput');
		this.heightInput = page.getByTestId('heightInput');
		this.lowStockActivityInput = page.getByTestId('lowStockActivityInput');
		this.maxOrderQuantityInput = page.getByTestId('maxOrderQuantityInput');
		this.minOrderQuantityInput = page.getByTestId('minOrderQuantityInput');
		this.minStockQuantityInput = page.getByTestId('minStockQuantityInput');
		this.multipleOrderQuantityInput = page.getByTestId(
			'multipleOrderQuantityInput'
		);
		this.nameInput = page.getByTestId('nameInput');
		this.neverExpireInput = page
			.getByTestId('expirationDate')
			.getByText('Never Expire');
		this.orderTypeElgibilityTitle = page.getByText(
			'Order Type Eligibility'
		);
		this.page = page;
		this.parentCPConfigurationListNameInput = page.getByTestId(
			'parentCPConfigurationListNameInput'
		);
		this.priorityInput = page.getByTestId('priorityInput');
		this.purchasableInput = page.getByTestId('purchasableInput');
		this.saveButton = page.getByRole('link', {exact: true, name: 'Save'});
		this.shippableInput = page.getByTestId('shippableInput');
		this.shipSeparatelyInput = page.getByTestId('shipSeparatelyInput');
		this.taxExemptInput = page.getByTestId('taxExemptInput');
		this.weightInput = page.getByTestId('weightInput');
		this.widthInput = page.getByTestId('widthInput');
	}
}
