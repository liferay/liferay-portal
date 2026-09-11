/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {FrameLocator, Locator, Page, expect} from '@playwright/test';

import {CommerceDNDTablePage} from '../commerceDNDTablePage';

export class CommerceAdminPriceListDetailsPage extends CommerceDNDTablePage {
	readonly addPriceModifierButton: Locator;
	readonly addPriceModifierModalFrame: FrameLocator;
	readonly addPriceModifierName: Locator;
	readonly addPriceModifierSaveButton: Locator;
	readonly addPriceModifierTarget: Locator;
	readonly addPriceModifierType: Locator;
	readonly addTierPriceButton: Locator;
	readonly addTierPriceEntryFrame: FrameLocator;
	readonly addTierPriceEntryPrice: Locator;
	readonly addTierPriceEntryQuantity: Locator;
	readonly addTierPriceEntryQuantityNotAllowedError: Locator;
	readonly addTierPriceEntrySaveButton: Locator;
	readonly catalogSelect: Locator;
	readonly currencySelect: Locator;
	readonly editPriceTierFrame: FrameLocator;
	readonly editPriceTierPrice: Locator;
	readonly eligibilityEntryCell: (name: string) => Locator;
	readonly eligibilityFindInput: (placeholder: string) => Locator;
	readonly eligibilityRowSelectButton: (entryName: string) => Locator;
	readonly eligibilityTab: Locator;
	readonly entriesTab: Locator;
	readonly errorAlert: (text: string) => Locator;
	readonly findSkuInput: Locator;
	readonly itemFinderRows: Locator;
	readonly nameInput: Locator;
	readonly page: Page;
	readonly parentAutocomplete: Locator;
	readonly parentDropdownItem: (name: string) => Locator;
	readonly priceModifierActiveToggle: Locator;
	readonly priceModifierAmountInput: Locator;
	readonly priceModifierLink: (title: string) => Locator;
	readonly priceModifierRowActions: (title: string) => Locator;
	readonly priceModifierRowDeleteMenuItem: Locator;
	readonly priceModifierSaveButton: Locator;
	readonly priceModifiersTab: Locator;
	readonly priceTypeSelect: Locator;
	readonly priorityInput: Locator;
	readonly publishButton: Locator;
	readonly scheduleLabel: Locator;
	readonly selectButton: Locator;
	readonly sidePanelDiscountLevel1Input: Locator;
	readonly sidePanelFrame: FrameLocator;
	readonly sidePanelOverrideDiscountToggle: Locator;
	readonly sidePanelPriceInput: Locator;
	readonly sidePanelSaveButton: Locator;
	readonly skuLink: (price: string) => Locator;
	readonly skuRowActionsButton: (sku: string) => Locator;
	readonly skuRowRemoveMenuItem: Locator;
	readonly skusTableRowLink: (skuName: string) => Locator;
	readonly specificAccountGroupsRadio: Locator;
	readonly specificAccountsRadio: Locator;
	readonly specificChannelsRadio: Locator;
	readonly specificOrderTypesRadio: Locator;

	constructor(page: Page) {
		super(
			page,
			'#_com_liferay_commerce_pricing_web_internal_portlet_CommercePriceListPortlet_fm .fds table'
		);

		this.addPriceModifierButton = page
			.getByTestId('managementToolbar')
			.getByRole('button', {name: 'Add Price Modifier'});
		this.addPriceModifierModalFrame = page
			.locator('.modal-dialog')
			.frameLocator('iframe');
		this.addPriceModifierName =
			this.addPriceModifierModalFrame.getByLabel('Name');
		this.addPriceModifierSaveButton =
			this.addPriceModifierModalFrame.getByRole('button', {
				name: 'Submit',
			});
		this.addPriceModifierTarget =
			this.addPriceModifierModalFrame.getByLabel('Target');
		this.addPriceModifierType =
			this.addPriceModifierModalFrame.getByLabel('Modifier');
		this.addTierPriceButton = page
			.frameLocator('iframe')
			.getByTestId('managementToolbar')
			.locator('[data-testid="fdsCreationActionButton"]');
		this.addTierPriceEntryFrame = page.frameLocator('iframe >> nth=1');
		this.addTierPriceEntryPrice = this.addTierPriceEntryFrame.getByLabel(
			'Tier Price Required'
		);
		this.addTierPriceEntryQuantity =
			this.addTierPriceEntryFrame.getByLabel('Quantity Required');
		this.addTierPriceEntryQuantityNotAllowedError =
			this.addTierPriceEntryFrame.getByText(
				'The specified quantity is not allowed.',
				{exact: false}
			);
		this.addTierPriceEntrySaveButton =
			this.addTierPriceEntryFrame.getByRole('button', {name: 'Submit'});
		this.catalogSelect = page.locator(
			'select[name$="commerceCatalogGroupId"]'
		);
		this.currencySelect = page.locator(
			'select[name$="commerceCurrencyId"]'
		);
		this.editPriceTierFrame = page
			.frameLocator('iframe')
			.frameLocator('iframe');
		this.editPriceTierPrice = this.editPriceTierFrame.getByLabel(
			'Tier Price Required'
		);
		this.eligibilityEntryCell = (name: string) =>
			page.getByRole('cell', {name}).first();
		this.eligibilityFindInput = (placeholder: string) =>
			page.getByPlaceholder(placeholder);
		this.eligibilityRowSelectButton = (entryName: string) =>
			page
				.getByRole('row')
				.filter({hasText: entryName})
				.getByRole('button', {exact: true, name: 'Select'});
		this.eligibilityTab = page.getByRole('link', {
			exact: true,
			name: 'Eligibility',
		});
		this.entriesTab = page.getByRole('link', {name: 'Entries'});
		this.errorAlert = (text: string) =>
			page.locator('.alert-danger', {hasText: text});
		this.findSkuInput = page.getByPlaceholder('Find a SKU');
		this.itemFinderRows = page.locator('.add-or-create tbody tr');
		this.nameInput = page.locator('input[name$="_name"]').first();
		this.page = page;
		this.parentAutocomplete = page
			.locator('#autocomplete-root input[type="text"]')
			.first();
		this.parentDropdownItem = (name: string) =>
			page
				.locator('.autocomplete-dropdown-menu')
				.getByText(name, {exact: true});
		this.sidePanelFrame = page
			.locator('.fds-side-panel')
			.frameLocator('iframe');
		this.priceModifierActiveToggle =
			this.sidePanelFrame.getByLabel('Active');
		this.priceModifierAmountInput = this.sidePanelFrame.locator(
			'input[name$="_modifierAmount"]'
		);
		this.priceModifierLink = (title: string) =>
			page.getByRole('link', {exact: true, name: title});
		this.priceModifierRowActions = (title: string) =>
			page.getByRole('row').filter({hasText: title}).getByRole('button');
		this.priceModifierRowDeleteMenuItem = page.getByRole('menuitem', {
			exact: true,
			name: 'Delete',
		});
		this.priceModifierSaveButton = this.sidePanelFrame.getByRole('button', {
			exact: true,
			name: 'Save',
		});
		this.priceModifiersTab = page.getByRole('link', {
			exact: true,
			name: 'Price Modifiers',
		});
		this.priceTypeSelect = page.locator('select[name$="_netPrice"]');
		this.priorityInput = page.locator('input[name$="_priority"]').first();
		this.publishButton = page
			.getByRole('button', {exact: true, name: 'Publish'})
			.or(page.getByRole('link', {exact: true, name: 'Publish'}))
			.first();
		this.scheduleLabel = page.getByText('Schedule');
		this.selectButton = page.getByRole('button', {name: 'Select'});
		this.sidePanelDiscountLevel1Input = this.sidePanelFrame.locator(
			'input[name$="_discountLevel1"]'
		);
		this.sidePanelOverrideDiscountToggle =
			this.sidePanelFrame.getByLabel('Override Discount');
		this.sidePanelPriceInput = this.sidePanelFrame
			.getByLabel('Price List Price')
			.or(this.sidePanelFrame.getByLabel('Promotion Price'));
		this.sidePanelSaveButton = this.sidePanelFrame.getByRole('button', {
			name: 'Save',
		});
		this.skuLink = (price: string) =>
			page
				.frameLocator('iframe')
				.first()
				.getByRole('link', {name: price});
		this.skuRowActionsButton = (sku: string) =>
			page.getByRole('row').filter({hasText: sku}).getByRole('button');
		this.skuRowRemoveMenuItem = page.getByRole('menuitem', {
			exact: true,
			name: 'Remove',
		});
		this.skusTableRowLink = (sku: string) =>
			page.getByRole('link', {exact: true, name: sku});
		this.specificAccountGroupsRadio = page.getByRole('radio', {
			name: 'Specific Account Groups',
		});
		this.specificAccountsRadio = page.getByRole('radio', {
			name: 'Specific Accounts',
		});
		this.specificChannelsRadio = page.getByRole('radio', {
			name: 'Specific Channels',
		});
		this.specificOrderTypesRadio = page.getByRole('radio', {
			name: 'Specific Order Types',
		});
	}

	async assertUOMSelectedInSidePanel({
		linkName,
		rowText,
		scope,
		uomKeys = ['uomKey1', 'uomKey2'],
	}: {
		linkName: string;
		rowText: string;
		scope?: FrameLocator;
		uomKeys?: string[];
	}) {
		const container = scope ?? this.page;

		const sidePanelFrame = container.frameLocator('.is-visible iframe');

		const closeButton = sidePanelFrame
			.locator('.side-panel-iframe-header')
			.getByRole('button');

		const uomSelect = sidePanelFrame.getByLabel('Unit of Measure');

		for (const uomKey of uomKeys) {
			const row = container
				.getByRole('row')
				.filter({hasText: rowText})
				.filter({hasText: uomKey});

			await row.getByRole('link', {name: linkName}).click();

			await expect(uomSelect).toBeDisabled();
			await expect(uomSelect).toHaveValue(uomKey);

			await closeButton.click();
		}
	}

	async addEligibilityEntry(placeholder: string, entryName: string) {
		const findInput = this.page.getByPlaceholder(placeholder);

		await findInput.click();
		await findInput.fill(entryName);

		await this.eligibilityEntryCell(entryName).click();

		await this.eligibilityTab.click();
	}
}
