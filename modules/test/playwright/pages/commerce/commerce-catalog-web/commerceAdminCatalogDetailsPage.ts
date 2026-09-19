/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {FrameLocator, Locator, Page} from '@playwright/test';

export class CommerceAdminCatalogDetailsPage {
	readonly basePriceListAutocomplete: Locator;
	readonly basePriceListDropdownItem: (name: string) => Locator;
	readonly basePromotionAutocomplete: Locator;
	readonly basePromotionDropdownItem: (name: string) => Locator;
	readonly currencySelect: Locator;
	readonly defaultImage: Locator;
	readonly fileEntryIdInput: Locator;
	readonly itemSelectorAddButton: Locator;
	readonly itemSelectorFileInput: Locator;
	readonly itemSelectorFrameLocator: FrameLocator;
	readonly languageSelect: Locator;
	readonly linkSupplierAutocomplete: Locator;
	readonly linkSupplierDropdownItem: (name: string) => Locator;
	readonly linkSupplierReadOnlyInput: Locator;
	readonly nameInput: Locator;
	readonly page: Page;
	readonly removeImageButton: Locator;
	readonly saveButton: Locator;
	readonly selectFileButton: Locator;

	constructor(page: Page) {
		this.basePriceListAutocomplete = page
			.locator('#base-price-list-autocomplete-root input[type="text"]')
			.first();
		this.basePriceListDropdownItem = (name: string) =>
			page
				.locator('.autocomplete-dropdown-menu')
				.getByText(name, {exact: true});
		this.basePromotionAutocomplete = page
			.locator('#base-promotion-autocomplete-root input[type="text"]')
			.first();
		this.basePromotionDropdownItem = (name: string) =>
			page
				.locator('.autocomplete-dropdown-menu')
				.getByText(name, {exact: true});
		this.currencySelect = page.locator(
			'select[name$="commerceCurrencyCode"]'
		);
		this.defaultImage = page.locator('img.current-image');
		this.fileEntryIdInput = page.locator('input[name$="fileEntryId"]');
		this.itemSelectorFrameLocator = page.frameLocator(
			'iframe[title="Select File"]'
		);
		this.itemSelectorAddButton = this.itemSelectorFrameLocator.getByRole(
			'button',
			{exact: true, name: 'Add'}
		);
		this.itemSelectorFileInput =
			this.itemSelectorFrameLocator.locator('input[type="file"]');
		this.languageSelect = page.locator(
			'select[name$="catalogDefaultLanguageId"]'
		);
		this.linkSupplierAutocomplete = page
			.locator('#link-account-entry-autocomplete-root input[type="text"]')
			.first();
		this.linkSupplierDropdownItem = (name: string) =>
			page
				.locator('.autocomplete-dropdown-menu')
				.getByText(name, {exact: true});
		this.linkSupplierReadOnlyInput = page.getByLabel(
			'Link Catalog to a Supplier'
		);
		this.nameInput = page.locator('input[name$="_name"]').first();
		this.page = page;
		this.removeImageButton = page.getByRole('button', {
			exact: true,
			name: 'Remove Image',
		});
		this.selectFileButton = page.getByRole('button', {
			exact: true,
			name: 'Select File',
		});
		this.saveButton = page
			.getByRole('button', {exact: true, name: 'Save'})
			.or(page.getByRole('link', {exact: true, name: 'Save'}))
			.first();
	}

	async uploadDefaultImage(filePath: string) {
		await this.selectFileButton.click();
		await this.itemSelectorFileInput.setInputFiles(filePath);
		await this.itemSelectorAddButton.click();
	}
}
