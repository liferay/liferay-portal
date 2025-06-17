/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Locator, Page} from '@playwright/test';

export class EditAccountChannelDefaultsPage {
	readonly addDefaultBillingAddressButton: Locator;
	readonly addDefaultShippingAddressButton: Locator;
	readonly addDefaultPaymentTermButton: Locator;
	readonly addDefaultPaymentTermSelector: Locator;
	readonly addDefaultTermSaveButton: Locator;
	readonly addressTableRowColumn: (
		columnIndex: number,
		tableName: string,
		text: string
	) => Promise<Locator>;
	readonly billingAddressAllChannelsText: Locator;
	readonly billingAddressAllOtherChannelsText: Locator;
	readonly defaultBillingAddressesTable: Locator;
	readonly defaultShippingAddressesTable: Locator;
	readonly deleteMenuItem: Locator;
	readonly getRowByTextFromTable: (
		tableName: string,
		text: string
	) => Locator;
	readonly page: Page;
	readonly setDefaultBillingAddressFrameBillingAddressDropdownMenu: Locator;
	readonly setDefaultAddressFrameChannelDropdownMenu: Locator;
	readonly setDefaultAddressFrameSaveButton: Locator;
	readonly setDefaultShippingAddressFrameBillingAddressDropdownMenu: Locator;
	readonly shippingAddressAllChannelsText: Locator;
	readonly shippingAddressAllOtherChannelsText: Locator;

	constructor(page: Page) {
		this.addDefaultBillingAddressButton = page
			.getByTestId('defaultBillingCommerceAddresses')
			.getByRole('button', {name: 'Add Default Address'})
			.first();
		this.addDefaultPaymentTermButton = page
			.locator(
				"[id='_com_liferay_account_admin_web_internal_portlet_AccountEntriesAdminPortlet_defaultPaymentCommerceTermEntries']"
			)
			.getByRole('button', {name: 'Add Default Term'})
			.first();
		this.addDefaultPaymentTermSelector = page
			.frameLocator('.fds-modal-body > iframe')
			.getByLabel('Term');
		this.addDefaultTermSaveButton = page
			.frameLocator('.fds-modal-body > iframe')
			.getByRole('button', {name: 'Save'});
		this.addDefaultShippingAddressButton = page
			.getByTestId('defaultShippingCommerceAddresses')
			.getByRole('button', {name: 'Add Default Address'})
			.first();
		this.addressTableRowColumn = async (
			columnIndex: number,
			tableName: 'Billing' | 'Shipping',
			text: string
		) => {
			return this.getRowByTextFromTable(
				`default${tableName}CommerceAddresses`,
				text
			)
				.locator('td')
				.nth(columnIndex);
		};
		this.defaultBillingAddressesTable = page.getByTestId(
			'defaultBillingCommerceAddresses'
		);
		this.billingAddressAllChannelsText =
			this.defaultBillingAddressesTable.getByText('All Channels');
		this.billingAddressAllOtherChannelsText =
			this.defaultBillingAddressesTable.getByText('All Other Channels');
		this.defaultShippingAddressesTable = page.getByTestId(
			'defaultShippingCommerceAddresses'
		);
		this.deleteMenuItem = page.getByRole('menuitem', {name: 'Delete'});
		this.getRowByTextFromTable = (
			tableName: string,
			text: string
		): Locator => {
			return this.page
				.getByTestId(tableName)
				.locator('tbody')
				.locator('tr')
				.filter({
					has: this.page.getByText(text).first(),
				});
		};
		this.page = page;
		this.setDefaultBillingAddressFrameBillingAddressDropdownMenu = page
			.frameLocator('.fds-modal-body > iframe')
			.getByLabel('Billing Address');
		this.setDefaultAddressFrameChannelDropdownMenu = page
			.frameLocator('.fds-modal-body > iframe')
			.getByLabel('Channel');
		this.setDefaultAddressFrameSaveButton = page
			.frameLocator('.fds-modal-body > iframe')
			.getByRole('button', {name: 'Save'});
		this.setDefaultShippingAddressFrameBillingAddressDropdownMenu = page
			.frameLocator('.fds-modal-body > iframe')
			.getByLabel('Shipping Address');
		this.shippingAddressAllChannelsText =
			this.defaultShippingAddressesTable.getByText('All Channels');
		this.shippingAddressAllOtherChannelsText =
			this.defaultShippingAddressesTable.getByText('All Other Channels');
	}

	async addDefaultPaymentTerm(paymentTermId: number) {
		await this.addDefaultPaymentTermButton.click();
		await this.addDefaultPaymentTermSelector.selectOption(
			paymentTermId.toString()
		);
		await this.addDefaultTermSaveButton.click();
		await this.page.waitForTimeout(200);
	}
}
