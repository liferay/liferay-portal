/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {FrameLocator, Locator, Page} from '@playwright/test';

export class CommerceAdminProductConfigurationEntryPage {
	readonly allowedOrderQuantitiesInput: Locator;
	readonly backOrdersInput: Locator;
	readonly commerceAvailabilityEstimateIdInput: Locator;
	readonly CPDefinitionInventoryEngineInput: Locator;
	readonly CPTaxCategoryIdInput: Locator;
	readonly depthInput: Locator;
	readonly displayAvailabilityInput: Locator;
	readonly displayStockQuantityInput: Locator;
	readonly freeShippingInput: Locator;
	readonly heightInput: Locator;
	readonly iframe: FrameLocator;
	readonly lowStockActivityInput: Locator;
	readonly maxOrderQuantityInput: Locator;
	readonly minOrderQuantityInput: Locator;
	readonly minStockQuantityInput: Locator;
	readonly multipleOrderQuantityInput: Locator;
	readonly page: Page;
	readonly purchasableInput: Locator;
	readonly saveButton: Locator;
	readonly shippableInput: Locator;
	readonly shipSeparatelyInput: Locator;
	readonly sidePanelTitle: Locator;
	readonly taxExemptInput: Locator;
	readonly weightInput: Locator;
	readonly widthInput: Locator;

	constructor(page: Page) {
		this.iframe = page.frameLocator('iframe');
		this.page = page;

		this.allowedOrderQuantitiesInput = this.iframe.getByTestId(
			'allowedOrderQuantitiesInput'
		);
		this.backOrdersInput = this.iframe.getByTestId('backOrdersInput');
		this.commerceAvailabilityEstimateIdInput = this.iframe.getByTestId(
			'commerceAvailabilityEstimateIdInput'
		);
		this.CPDefinitionInventoryEngineInput = this.iframe.getByTestId(
			'CPDefinitionInventoryEngineInput'
		);
		this.CPTaxCategoryIdInput = this.iframe.getByTestId(
			'CPTaxCategoryIdInput'
		);
		this.depthInput = this.iframe.getByTestId('depthInput');
		this.displayAvailabilityInput = this.iframe.getByTestId(
			'displayAvailabilityInput'
		);
		this.displayStockQuantityInput = this.iframe.getByTestId(
			'displayStockQuantityInput'
		);
		this.freeShippingInput = this.iframe.getByTestId('freeShippingInput');
		this.heightInput = this.iframe.getByTestId('heightInput');
		this.lowStockActivityInput = this.iframe.getByTestId(
			'lowStockActivityInput'
		);
		this.maxOrderQuantityInput = this.iframe.getByTestId(
			'maxOrderQuantityInput'
		);
		this.minOrderQuantityInput = this.iframe.getByTestId(
			'minOrderQuantityInput'
		);
		this.minStockQuantityInput = this.iframe.getByTestId(
			'minStockQuantityInput'
		);
		this.multipleOrderQuantityInput = this.iframe.getByTestId(
			'multipleOrderQuantityInput'
		);
		this.purchasableInput = this.iframe.getByTestId('purchasableInput');
		this.saveButton = this.iframe.getByTestId('saveButton');
		this.shippableInput = this.iframe.getByTestId('shippableInput');
		this.shipSeparatelyInput = this.iframe.getByTestId(
			'shipSeparatelyInput'
		);
		this.sidePanelTitle = this.iframe.getByTestId('sidePanelTitle');
		this.taxExemptInput = this.iframe.getByTestId('taxExemptInput');
		this.weightInput = this.iframe.getByTestId('weightInput');
		this.widthInput = this.iframe.getByTestId('widthInput');
	}
}
