/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {FrameLocator, Locator, Page} from '@playwright/test';

import {CommerceDNDTablePage} from '../commerceDNDTablePage';

export class CommerceAdminOrderDetailsPage extends CommerceDNDTablePage {
	readonly cancelButton: Locator;
	readonly checkoutButton: Locator;
	readonly commerceOrderAccountEntryName: Locator;
	readonly editEntryActionLink: (
		labelName: string,
		action: string
	) => Promise<Locator>;
	readonly editPaymentMethodFrame: FrameLocator;
	readonly headerDetailsTitle: Locator;
	readonly orderDetailsEntryDescription: (
		infoName: string
	) => Promise<Locator>;
	readonly orderDetailsFrame: FrameLocator;
	readonly orderDetailsModalHeader: (headname: string) => Promise<Locator>;
	readonly orderDetailsModalField: (fieldName: string) => Promise<Locator>;
	readonly orderDetailsTab: (tabName: string) => Promise<Locator>;
	readonly orderId: Locator;
	readonly orderItemActions: Locator;
	readonly orderItemActionEdit: Locator;
	readonly orderItemFrame: FrameLocator;
	readonly orderItemFrameCloseButton: Locator;
	readonly orderItemDecimalQuantity: Locator;
	readonly orderItemQuantityColumn: (quantity: string) => Promise<Locator>;
	readonly orderItemSaveButton: Locator;
	readonly orderNote: (note: string) => Promise<Locator>;
	readonly orderNotesLink: Locator;
	readonly orderNotesTextArea: Locator;
	readonly orderSummaryLink: Locator;
	readonly orderSummaryFrame: FrameLocator;
	readonly orderSummarySubtotal: Locator;
	readonly orderSummarySubtotalInput: Locator;
	readonly orderSummarySaveButton: Locator;
	readonly page: Page;
	readonly paymentMethodRadioButton: (
		paymentMethod: string
	) => Promise<Locator>;
	readonly reorderButton: Locator;
	readonly saveButton: Locator;
	readonly selectDeliveryTerms: Locator;
	readonly selectPaymentTerms: Locator;
	readonly submitModalButton: Locator;
	readonly submitPaymentMethod: Locator;

	constructor(page: Page) {
		super(
			page,
			'#_com_liferay_commerce_order_web_internal_portlet_CommerceOrderPortlet_editOrderContainer .fds table'
		);
		this.cancelButton = page.getByRole('link', {
			exact: true,
			name: 'Cancel',
		});
		this.checkoutButton = page.getByRole('button', {
			exact: true,
			name: 'Checkout',
		});
		this.commerceOrderAccountEntryName = page.getByTestId(
			'commerceOrderAccountEntryName'
		);
		this.editEntryActionLink = async (
			labelName: string,
			action: string
		) => {
			return page
				.getByText(labelName)
				.getByRole('link', {exact: true, name: action});
		};
		this.editPaymentMethodFrame = page.frameLocator(
			'iframe[title="Edit Payment Method"]'
		);
		this.headerDetailsTitle = page.getByTestId('headerDetailsTitle');
		this.orderDetailsEntryDescription = async (infoName: string) => {
			return page.locator(
				`xpath=//header//div[contains(text(),'${infoName}')]/following::div[contains(@class, 'description')][1]`
			);
		};
		this.orderDetailsFrame = page.frameLocator('iframe >> nth=1');
		this.orderDetailsModalHeader = async (headName: string) => {
			return page.getByRole('heading', {name: headName});
		};
		this.orderDetailsModalField = async (fieldName: string) => {
			return this.orderDetailsFrame.getByLabel(fieldName);
		};
		this.orderDetailsTab = async (tabName: string) => {
			return page.getByRole('link', {exact: true, name: tabName});
		};
		this.orderId = page.locator('span:has-text("ID")+strong');
		this.orderItemActions = page.getByRole('button', {name: 'Actions'});
		this.orderItemActionEdit = page.getByRole('menuitem', {name: 'Edit'});
		this.orderItemFrame = page.frameLocator('iframe');
		this.orderItemFrameCloseButton = this.orderItemFrame
			.locator('button.side-panel-iframe-close')
			.first();
		this.orderItemDecimalQuantity =
			this.orderItemFrame.getByLabel('Decimal Quantity');
		this.orderItemSaveButton = this.orderItemFrame.getByRole('button', {
			name: 'Save',
		});
		this.orderItemQuantityColumn = async (quantity: string) => {
			return page.getByRole('gridcell', {exact: true, name: quantity});
		};
		this.orderNote = async (tabName: string) => {
			return page.getByText(tabName);
		};
		this.orderNotesLink = page.getByRole('link', {
			exact: true,
			name: 'Questions and Answers',
		});
		this.orderNotesTextArea = page.getByPlaceholder('Type your note here.');
		this.orderSummaryLink = page
			.getByText('Order Summary')
			.getByRole('link', {name: 'edit'});
		this.orderSummaryFrame = page.frameLocator(
			'iframe[title="Order Summary"]'
		);
		this.orderSummarySubtotal = page.locator(
			'div:nth-child(2) > .summary-table-item'
		);
		this.orderSummarySubtotalInput = this.orderSummaryFrame.getByLabel(
			'Subtotal',
			{exact: true}
		);
		this.orderSummarySaveButton = page
			.locator('.modal-item-last')
			.getByRole('button', {name: 'Submit'});
		this.page = page;
		this.paymentMethodRadioButton = async (paymentMethod: string) => {
			return this.editPaymentMethodFrame
				.locator('li')
				.filter({hasText: paymentMethod})
				.getByLabel('');
		};
		this.reorderButton = page.getByRole('button', {
			exact: true,
			name: 'Reorder',
		});
		this.saveButton = this.page.getByRole('button', {name: 'Save'});
		this.selectPaymentTerms = this.page
			.frameLocator('iframe[title="Payment Terms"]')
			.getByLabel('Title');
		this.selectDeliveryTerms = this.page
			.frameLocator('iframe[title="Delivery Terms"]')
			.getByLabel('Title');
		this.submitPaymentMethod = page
			.locator('.modal-item-last')
			.getByRole('button', {exact: true, name: 'Submit'});
		this.submitModalButton = this.page.getByRole('button', {
			name: 'Submit',
		});
	}

	async editAddress(
		city: string,
		country: string,
		region: string,
		street: string,
		zip: string
	) {
		await (
			await this.orderDetailsModalField('Street 1 Required')
		).fill(street);
		await (await this.orderDetailsModalField('Street 2')).fill(street);
		await (await this.orderDetailsModalField('Street 3')).fill(street);
		await (
			await this.orderDetailsModalField('Country')
		).selectOption(country);
		await (await this.orderDetailsModalField('Zip Required')).fill(zip);
		await (await this.orderDetailsModalField('City Required')).fill(city);
		await (
			await this.orderDetailsModalField('Region')
		).selectOption(region);
		await this.submitModalButton.click();
	}

	async reorder() {
		this.reorderButton.click();
		this.checkoutButton.click();
	}
}
