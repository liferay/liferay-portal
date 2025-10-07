/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Locator, Page, expect} from '@playwright/test';

import {DataApiHelpers} from '../../helpers/ApiHelpers';
import {waitForAlert} from '../../utils/waitForAlert';
import {InstanceSettingsPage} from '../configuration-admin-web/InstanceSettingsPage';

export class AccountInstanceSettingsAccountAddressSubtypePage {
	readonly billingAddressSubtypePicklistInput: Locator;
	readonly billingAndShippingAddressSubtypePicklistInput: Locator;
	readonly instanceSettingsPage: InstanceSettingsPage;
	readonly page: Page;
	readonly shippingAddressSubtypePicklistInput: Locator;
	readonly updateButton: Locator;

	constructor(page: Page) {
		this.billingAddressSubtypePicklistInput = page
			.getByTestId(
				'billingAddressSubtypeListTypeDefinitionExternalReferenceCode'
			)
			.getByLabel('Billing Address Subtype');
		this.billingAndShippingAddressSubtypePicklistInput = page
			.getByTestId(
				'billingAndShippingAddressSubtypeListTypeDefinitionExternalReferenceCode'
			)
			.getByLabel('Billing and Shipping Address Subtype');
		this.instanceSettingsPage = new InstanceSettingsPage(page);
		this.page = page;
		this.shippingAddressSubtypePicklistInput = page
			.getByTestId(
				'shippingAddressSubtypeListTypeDefinitionExternalReferenceCode'
			)
			.getByLabel('Shipping Address Subtype');
		this.updateButton = page.getByTestId('submitConfiguration');
	}

	async goto() {
		await this.instanceSettingsPage.goToInstanceSetting(
			'Accounts',
			'Account Address Subtype'
		);
	}

	async initAddressSubtypePicklists(apiHelpers: DataApiHelpers) {
		const billingListTypeDefinition =
			await apiHelpers.listTypeAdmin.postRandomListTypeDefinition();

		apiHelpers.data.push({
			id: billingListTypeDefinition.id,
			type: 'listTypeDefinition',
		});

		const billingListTypeEntry =
			await apiHelpers.listTypeAdmin.postListTypeEntry({
				key: 'billing1',
				listTypeDefinitionExternalReferenceCode:
					billingListTypeDefinition.externalReferenceCode,
				name_i18n: {en_US: 'Billing1'},
			});

		const billingAndShippingListTypeDefinition =
			await apiHelpers.listTypeAdmin.postRandomListTypeDefinition();

		apiHelpers.data.push({
			id: billingAndShippingListTypeDefinition.id,
			type: 'listTypeDefinition',
		});

		const billingAndShippingListTypeEntry =
			await apiHelpers.listTypeAdmin.postListTypeEntry({
				key: 'billingAndShipping1',
				listTypeDefinitionExternalReferenceCode:
					billingAndShippingListTypeDefinition.externalReferenceCode,
				name_i18n: {en_US: 'BillingAndShipping1'},
			});

		const shippingListTypeDefinition =
			await apiHelpers.listTypeAdmin.postRandomListTypeDefinition();

		apiHelpers.data.push({
			id: shippingListTypeDefinition.id,
			type: 'listTypeDefinition',
		});

		const shippingListTypeEntry =
			await apiHelpers.listTypeAdmin.postListTypeEntry({
				key: 'shipping1',
				listTypeDefinitionExternalReferenceCode:
					shippingListTypeDefinition.externalReferenceCode,
				name_i18n: {en_US: 'Shipping1'},
			});

		return {
			billingAndShippingListTypeDefinition,
			billingAndShippingListTypeEntry,
			billingListTypeDefinition,
			billingListTypeEntry,
			shippingListTypeDefinition,
			shippingListTypeEntry,
		};
	}

	async setAddressSubtypeExternalReferenceCodes(
		listTypeDefinitionBilling = '',
		listTypeDefinitionBillingAndShipping = '',
		listTypeDefinitionShipping = ''
	) {
		await this.goto();

		await expect(async () => {
			await expect(this.billingAddressSubtypePicklistInput).toBeEnabled();

			await this.billingAddressSubtypePicklistInput.fill(
				listTypeDefinitionBilling
			);

			await expect(
				this.billingAndShippingAddressSubtypePicklistInput
			).toBeEnabled();

			await this.billingAndShippingAddressSubtypePicklistInput.fill(
				listTypeDefinitionBillingAndShipping
			);

			await expect(
				this.shippingAddressSubtypePicklistInput
			).toBeEnabled();

			await this.shippingAddressSubtypePicklistInput.fill(
				listTypeDefinitionShipping
			);
			await this.updateButton.click();

			await waitForAlert(this.page);

			await expect(this.billingAddressSubtypePicklistInput).toHaveValue(
				listTypeDefinitionBilling
			);
			await expect(
				this.billingAndShippingAddressSubtypePicklistInput
			).toHaveValue(listTypeDefinitionBillingAndShipping);
			await expect(this.shippingAddressSubtypePicklistInput).toHaveValue(
				listTypeDefinitionShipping
			);
		}).toPass();
	}
}
