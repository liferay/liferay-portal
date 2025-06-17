/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {FrameLocator, Locator, Page, expect} from '@playwright/test';

import {CommerceLayoutsPage} from '../commerce-order-content-web/commerceLayoutsPage';
import {
	CommerceDNDTablePage,
	searchTableRowByValue,
} from '../commerceDNDTablePage';

type TAddress = {
	asGuest?: boolean | false;
	city: string;
	countryLabel: string;
	name: string;
	phoneNumber?: string;
	regionLabel?: string;
	street: string;
	useAsBilling?: boolean | true;
	zip: string;
};

export class CheckoutPage extends CommerceDNDTablePage {
	readonly activeCheckoutStep: Locator;
	readonly assertDataDeliveryGroupModal: (data: string) => Locator;
	readonly addressInput: Locator;
	readonly cityInput: Locator;
	readonly commerceBillingAddress: Locator;
	readonly commerceShippingAddress: Locator;
	readonly configurationIFrame: FrameLocator;
	readonly configurationIFrameSaveButton: Locator;
	readonly configurationIFrameShowFullAddressToggle: Locator;
	readonly configurationIFrameShowPhoneNumberToggle: Locator;
	readonly configurationMenuItem: Locator;
	readonly continueButton: Locator;
	readonly countryInput: Locator;
	readonly emailInput: Locator;
	readonly goToOrderDetailsButton: Locator;
	readonly headingDeliveryGroupModal: (name: string) => Locator;
	readonly iframeOkButton: Locator;
	readonly layoutsPage: CommerceLayoutsPage;
	readonly multishippingTabLink: Locator;
	readonly multishippingTableLocator: Locator;
	readonly nameInput: Locator;
	readonly optionsButton: Locator;
	readonly orderItemsTabLink: Locator;
	readonly orderItemsTableLocator: Locator;
	readonly orderSuccessMessage: Locator;
	readonly orderSummaryTableRow: (
		colPosition: number,
		value: number | string,
		strictEqual?: boolean
	) => Promise<{column: Locator; row: Locator}>;
	readonly page: Page;
	readonly phoneNumberInput: Locator;
	readonly previousButton: Locator;
	readonly regionInput: Locator;
	readonly saveButton: Locator;
	readonly shippingAddressSelect: Locator;
	readonly shippingCost: Locator;
	readonly subtypeErrorMessage: Locator;
	readonly subtypeInput: Locator;
	readonly subtypeMenuItem: (name: string) => Locator;
	readonly subtypeResetModalTitle: Locator;
	readonly subtypeResetSubtypeInput: Locator;
	readonly orderSummaryShippingMethod: Locator;
	readonly useAsBillingCheckbox: Locator;
	readonly viewDeliveryGroupTableButton: Locator;
	readonly zipInput: Locator;

	constructor(page: Page) {
		super(
			page,
			'#_com_liferay_commerce_checkout_web_internal_portlet_CommerceCheckoutPortlet_fm .fds table'
		);
		this.activeCheckoutStep = page.locator(
			'.multi-step-item.active .multi-step-indicator-label'
		);
		this.addressInput = page.getByPlaceholder('Address', {exact: true});
		this.cityInput = page.getByPlaceholder('City', {exact: true});
		this.commerceBillingAddress = page.getByTestId(
			'commerceBillingAddress'
		);
		this.configurationIFrame = page.frameLocator(
			'iframe[id="modalIframe"]'
		);
		this.continueButton = page.getByRole('button', {name: 'Continue'});
		this.configurationIFrameSaveButton = this.configurationIFrame.getByRole(
			'button',
			{name: 'Save'}
		);
		this.assertDataDeliveryGroupModal = (data: string) => {
			return this.configurationIFrame
				.locator('p')
				.filter({hasText: data});
		};
		this.configurationIFrameShowFullAddressToggle =
			this.configurationIFrame.getByLabel(
				'Order Summary Show Full Address'
			);
		this.configurationIFrameShowPhoneNumberToggle =
			this.configurationIFrame.getByLabel(
				'Order Summary Show Phone Number'
			);
		this.configurationMenuItem = page.getByRole('menuitem', {
			exact: true,
			name: 'Configuration',
		});
		this.countryInput = page.getByTitle('Country');
		this.emailInput = page.locator('input[id*="_email"]');
		this.headingDeliveryGroupModal = (name: string) => {
			return page.getByRole('heading', {exact: true, name});
		};
		this.iframeOkButton = page.getByLabel('close', {exact: true});
		this.goToOrderDetailsButton = page.getByRole('button', {
			name: 'Go to Order Details',
		});
		this.layoutsPage = new CommerceLayoutsPage(page);
		this.multishippingTabLink = page.getByRole('link', {
			exact: true,
			name: 'Multishipping',
		});
		this.multishippingTableLocator = page.locator(
			'div.multishipping-container'
		);
		this.nameInput = page.getByPlaceholder('Name', {exact: true});
		this.optionsButton = page
			.locator(
				'#portlet_com_liferay_commerce_checkout_web_internal_portlet_CommerceCheckoutPortlet'
			)
			.getByLabel('Options');
		this.orderItemsTabLink = page.getByRole('link', {
			exact: true,
			name: 'Order Items',
		});
		this.orderItemsTableLocator = page.locator(
			'#_com_liferay_commerce_checkout_web_internal_portlet_CommerceCheckoutPortlet_commerceOrderItems table'
		);
		this.orderSuccessMessage = page.getByText(
			'Success! Your order has been processed.'
		);
		this.orderSummaryTableRow = async (
			colPosition: number,
			value: number | string,
			strictEqual: boolean = false
		) => {
			return await searchTableRowByValue(
				this.orderItemsTableLocator,
				colPosition,
				String(value),
				strictEqual
			);
		};
		this.page = page;
		this.phoneNumberInput = page.getByPlaceholder('Phone Number', {
			exact: true,
		});
		this.previousButton = page.getByRole('button', {name: 'Previous'});
		this.regionInput = page.getByTitle('Region');
		this.commerceShippingAddress = page.getByTestId(
			'commerceShippingAddress'
		);
		this.saveButton = page.getByLabel('Save');
		this.shippingAddressSelect = page.getByText('Choose Shipping Address');
		this.shippingCost = page.locator('.shipping-cost');
		this.subtypeErrorMessage = page.getByText(
			'previous selection is not valid anymore'
		);
		this.subtypeInput = page.getByPlaceholder('Subtype');
		this.subtypeMenuItem = (name: string) =>
			page.getByRole('option', {name});
		this.subtypeResetModalTitle = page.getByRole('heading', {
			name: 'Reset Subtype',
		});
		this.subtypeResetSubtypeInput = page
			.getByLabel('Reset Subtype')
			.getByPlaceholder('Subtype');
		this.orderSummaryShippingMethod = page.locator('div.shipping-method');
		this.useAsBillingCheckbox = page.locator(
			'[id="_com_liferay_commerce_checkout_web_internal_portlet_CommerceCheckoutPortlet_use-as-billing"]'
		);
		this.viewDeliveryGroupTableButton = page.getByLabel('view');
		this.zipInput = page.getByPlaceholder('Zip', {exact: true});
	}

	async addAddress({
		asGuest = false,
		phoneNumber = '',
		regionLabel = '',
		useAsBilling = true,
		...address
	}: TAddress) {
		await this.cityInput.fill(address.city);
		await this.countryInput.selectOption({label: address.countryLabel});
		await this.nameInput.fill(address.name);
		await this.phoneNumberInput.fill(phoneNumber);
		await this.regionInput.selectOption({label: regionLabel});
		await this.addressInput.fill(address.street);
		await this.useAsBillingCheckbox.setChecked(useAsBilling);
		await this.zipInput.fill(address.zip);

		if (asGuest) {
			await this.emailInput.fill('guestemail@liferay.com');
		}
	}

	async addCheckoutWidget() {
		await this.layoutsPage.addWidgetToPage('Checkout');
	}

	async chooseShippingAddress(index) {
		this.shippingAddressSelect.selectOption(index);
		this.continueButton.click();
	}

	async performCheckout(
		{
			billingAddress,
			shippingAddress,
		}: {
			billingAddress?: TAddress;
			shippingAddress: TAddress;
		},
		callback: (activeStep: string) => Promise<void> = () =>
			Promise.resolve(),
		checkSuccess: boolean = true
	) {
		let currentStep = await this.activeCheckoutStep.textContent();

		if (currentStep.includes('Shipping Address')) {
			await this.addAddress(shippingAddress);

			await callback(currentStep);

			await this.continueButton.click();
		}

		currentStep = await this.activeCheckoutStep.textContent();

		if (currentStep.includes('Billing Address')) {
			await this.addAddress(billingAddress);

			await callback(currentStep);

			await this.continueButton.click();
		}

		currentStep = await this.activeCheckoutStep.textContent();

		if (currentStep.includes('Order Summary')) {
			await callback(currentStep);

			await this.continueButton.click();
		}

		currentStep = await this.activeCheckoutStep.textContent();

		if (currentStep.includes('Order Confirmation') && checkSuccess) {
			await callback(currentStep);

			await expect(this.orderSuccessMessage).toBeVisible();
		}
	}

	async performCheckoutUntilStep(stopAt: string) {
		let currentStep = await this.activeCheckoutStep.textContent();

		while (!currentStep.includes(stopAt)) {
			await this.continueButton.click();

			await expect(async () => {
				const nextStep = await this.activeCheckoutStep.textContent({
					timeout: 1000,
				});

				expect(currentStep !== nextStep).toBeTruthy();

				currentStep = nextStep;
			}).toPass();
		}

		return;
	}
}
