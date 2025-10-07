/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {FrameLocator, Locator, Page} from '@playwright/test';

export class CommerceChannelDefaultsPage {
	readonly defaultBillingCommerceAddresses: Locator;
	readonly defaultBillingCommerceAddressesActions: Locator;
	readonly defaultBillingCommerceAddressesButton: Locator;
	readonly defaultCommerceCurrencies: Locator;
	readonly defaultCommerceCurrenciesButton: Locator;
	readonly defaultCommerceDiscounts: Locator;
	readonly defaultCommerceDiscountsButton: Locator;
	readonly defaultCommercePaymentMethod: Locator;
	readonly defaultCommercePaymentMethodButton: Locator;
	readonly defaultCommercePriceLists: Locator;
	readonly defaultCommercePriceListsButton: Locator;
	readonly defaultCommerceShippingOption: Locator;
	readonly defaultCommerceShippingOptionButton: Locator;
	readonly defaultDeliveryCommerceTermEntries: Locator;
	readonly defaultDeliveryCommerceTermEntriesButton: Locator;
	readonly defaultPaymentCommerceTermEntries: Locator;
	readonly defaultPaymentCommerceTermEntriesButton: Locator;
	readonly defaultShippingCommerceAddresses: Locator;
	readonly defaultShippingCommerceAddressesActions: Locator;
	readonly defaultShippingCommerceAddressesButton: Locator;
	readonly defaultUsers: Locator;
	readonly defaultUsersButton: Locator;
	readonly editFrame: FrameLocator;
	readonly editFrameChannelSelect: Locator;
	readonly editFrameCurrencySelect: Locator;
	readonly editFramePriceListSelect: Locator;
	readonly editFrameSaveButton: Locator;
	readonly editMenuItem: Locator;
	readonly channelEntry: (channelEntryName: string) => Locator;
	readonly channelEntryAddButton: (channelEntryName: string) => Locator;
	readonly channelEntryHeader: (channelEntryHeaderName: string) => Locator;
	readonly page: Page;

	constructor(page: Page) {
		this.defaultBillingCommerceAddresses = page.getByTestId(
			'defaultBillingCommerceAddresses'
		);
		this.defaultBillingCommerceAddressesActions =
			this.defaultBillingCommerceAddresses.getByRole('button', {
				name: 'Actions',
			});
		this.defaultBillingCommerceAddressesButton =
			this.defaultBillingCommerceAddresses
				.getByTestId('managementToolbar')
				.locator('[data-testid="fdsCreationActionButton"]');
		this.defaultCommerceCurrencies = page.getByTestId(
			'defaultCommerceCurrencies'
		);
		this.defaultCommerceCurrenciesButton =
			this.defaultCommerceCurrencies.getByLabel('Add Default Currency');
		this.defaultCommerceDiscounts = page.getByTestId(
			'defaultCommerceDiscounts'
		);
		this.defaultCommerceDiscountsButton =
			this.defaultCommerceDiscounts.getByLabel('Add Default Discount');
		this.defaultCommercePaymentMethod = page.getByTestId(
			'defaultCommercePaymentMethod'
		);
		this.defaultCommercePaymentMethodButton =
			this.defaultCommercePaymentMethod.getByLabel('Edit').first();
		this.defaultCommercePriceLists = page.getByTestId(
			'defaultCommercePriceLists'
		);
		this.defaultCommercePriceListsButton =
			this.defaultCommercePriceLists.getByLabel('Add Default Price List');
		this.defaultCommerceShippingOption = page.getByTestId(
			'defaultCommerceShippingOption'
		);
		this.defaultCommerceShippingOptionButton =
			this.defaultCommerceShippingOption.getByLabel('Edit').first();
		this.defaultDeliveryCommerceTermEntries = page.getByTestId(
			'defaultDeliveryCommerceTermEntries'
		);
		this.defaultDeliveryCommerceTermEntriesButton =
			this.defaultDeliveryCommerceTermEntries.getByLabel(
				'Add Default Term'
			);
		this.defaultPaymentCommerceTermEntries = page.getByTestId(
			'defaultPaymentCommerceTermEntries'
		);
		this.defaultPaymentCommerceTermEntriesButton =
			this.defaultPaymentCommerceTermEntries.getByLabel(
				'Add Default Term'
			);
		this.defaultShippingCommerceAddresses = page.getByTestId(
			'defaultShippingCommerceAddresses'
		);
		this.defaultShippingCommerceAddressesActions =
			this.defaultShippingCommerceAddresses.getByRole('button', {
				name: 'Actions',
			});
		this.defaultShippingCommerceAddressesButton =
			this.defaultShippingCommerceAddresses
				.getByTestId('managementToolbar')
				.locator('[data-testid="fdsCreationActionButton"]');
		this.defaultUsers = page.getByTestId('defaultUsers');
		this.defaultUsersButton = this.defaultUsers.getByLabel('Add User');
		this.editFrame = page.frameLocator('.fds-modal-body > iframe');
		this.editMenuItem = page.getByRole('menuitem', {name: 'Edit'});
		this.page = page;

		this.editFrameChannelSelect = this.editFrame.getByLabel('Channel');
		this.editFrameCurrencySelect =
			this.editFrame.getByLabel('Currency Required');
		this.editFramePriceListSelect = this.editFrame.getByLabel('Price List');
		this.editFrameSaveButton = this.editFrame.getByRole('button', {
			exact: true,
			name: 'Save',
		});
		this.channelEntry = (channelEntryName: string) => {
			return page.getByTestId(new RegExp(`.*${channelEntryName}.*`, 'g'));
		};
		this.channelEntryAddButton = (channelEntryName: string) => {
			return this.channelEntry(channelEntryName).getByRole('button', {
				name: 'Add',
			});
		};
		this.channelEntryHeader = (channelEntryHeaderName: string) => {
			return page.getByRole('heading', {name: channelEntryHeaderName});
		};
	}
}
