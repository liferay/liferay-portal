/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {FrameLocator, Locator, Page} from '@playwright/test';

import {CommerceDNDTablePage} from '../commerceDNDTablePage';

export class CommerceAdminOrderDetailsPage extends CommerceDNDTablePage {
	readonly acceptOrderButton: Locator;
	readonly addressFieldText: (value: string) => Locator;
	readonly cancelButton: Locator;
	readonly checkoutButton: Locator;
	readonly commerceOrderAccountEntryName: Locator;
	readonly completeOrderButton: Locator;
	readonly createShipmentButton: Locator;
	readonly editEntryActionLink: (
		labelName: string,
		action: string
	) => Promise<Locator>;
	readonly editPaymentMethodFrame: FrameLocator;
	readonly editPaymentStatusFrame: FrameLocator;
	readonly editPaymentStatusSelect: Locator;
	readonly editPaymentStatusSubmitButton: Locator;
	readonly expandProductButton: Locator;
	readonly headerDetailsTitle: Locator;
	readonly orderDetailsEntryDescription: (
		infoName: string
	) => Promise<Locator>;
	readonly orderDetailsFrame: FrameLocator;
	readonly orderDetailsModalField: (fieldName: string) => Promise<Locator>;
	readonly orderDetailsModalHeader: (headname: string) => Promise<Locator>;
	readonly orderDetailsTab: (tabName: string) => Promise<Locator>;
	readonly orderId: Locator;
	readonly orderItemActionEdit: Locator;
	readonly orderItemActions: Locator;
	readonly orderItemDecimalQuantity: Locator;
	readonly orderItemFrame: FrameLocator;
	readonly orderItemFrameCloseButton: Locator;
	readonly orderItemMeasurementUnits: Locator;
	readonly orderItemQuantityColumn: (quantity: string) => Promise<Locator>;
	readonly orderItemSaveButton: Locator;
	readonly orderNote: (note: string) => Promise<Locator>;
	readonly orderNotesLink: Locator;
	readonly orderNotesTextArea: Locator;
	readonly orderStatusProcessing: Locator;
	readonly orderSummaryFrame: FrameLocator;
	readonly orderSummaryLink: Locator;
	readonly orderSummarySaveButton: Locator;
	readonly orderSummarySubtotal: Locator;
	readonly orderSummarySubtotalInput: Locator;
	readonly firstTransactionTimestampCell: Locator;
	readonly page: Page;
	readonly paymentMethodName: Locator;
	readonly paymentMethodOption: (paymentMethod: string) => Locator;
	readonly paymentMethodRadioButton: (
		paymentMethod: string
	) => Promise<Locator>;
	readonly paymentStatusText: (status: string) => Locator;
	readonly transactionsTable: Locator;
	readonly recalculateButton: Locator;
	readonly recalculateOrderSummaryModalTitle: Locator;
	readonly recalculateOrderSummaryModalCancelButton: Locator;
	readonly recalculateOrderSummaryModalContinueButton: Locator;
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
		this.acceptOrderButton = page.getByRole('link', {
			exact: true,
			name: 'Accept Order',
		});
		this.addressFieldText = (value: string) => page.getByText(value);
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
		this.completeOrderButton = page
			.getByRole('link', {exact: true, name: 'Completed'})
			.or(page.getByRole('button', {exact: true, name: 'Completed'}));
		this.createShipmentButton = page.getByRole('link', {
			exact: true,
			name: 'Create Shipment',
		});
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
		this.editPaymentStatusFrame = page.frameLocator(
			'iframe[title="Edit Payment Status"]'
		);
		this.editPaymentStatusSelect =
			this.editPaymentStatusFrame.getByLabel('Payment Status');
		this.editPaymentStatusSubmitButton = page.getByRole('button', {
			name: 'Submit',
		});
		this.expandProductButton = page
			.locator('.autofit-col-toggle')
			.getByRole('button');
		this.headerDetailsTitle = page.getByTestId('headerDetailsTitle');
		this.orderDetailsEntryDescription = async (infoName: string) => {
			return page.locator(
				`xpath=//header//div[contains(text(),'${infoName}')]/following::div[contains(@class, 'description')][1]`
			);
		};
		this.orderDetailsFrame = page.frameLocator('iframe >> nth=1');
		this.orderDetailsModalField = async (fieldName: string) => {
			return this.orderDetailsFrame.getByLabel(fieldName);
		};
		this.orderDetailsModalHeader = async (headName: string) => {
			return page.getByRole('heading', {name: headName});
		};
		this.orderDetailsTab = async (tabName: string) => {
			return page.getByRole('link', {exact: true, name: tabName});
		};
		this.orderId = page.locator('span:has-text("ID")+strong');
		this.orderItemActionEdit = page.getByRole('menuitem', {name: 'Edit'});
		this.orderItemActions = page.getByRole('button', {name: 'Actions'});
		this.orderItemFrame = page.frameLocator('iframe');
		this.orderItemDecimalQuantity =
			this.orderItemFrame.getByLabel('Decimal Quantity');

		this.orderItemFrameCloseButton = this.orderItemFrame
			.locator('button.side-panel-iframe-close')
			.first();
		this.orderItemMeasurementUnits =
			this.orderItemFrame.getByLabel('Measurement Units');
		this.orderItemQuantityColumn = async (quantity: string) => {
			return page.getByRole('gridcell', {exact: true, name: quantity});
		};
		this.orderItemSaveButton = this.orderItemFrame.getByRole('button', {
			name: 'Save',
		});
		this.orderNote = async (tabName: string) => {
			return page.getByText(tabName);
		};
		this.orderNotesLink = page.getByRole('link', {
			exact: true,
			name: 'Questions and Answers',
		});
		this.orderNotesTextArea = page.getByPlaceholder('Type your note here.');
		this.orderStatusProcessing = page.getByText('Processing', {
			exact: true,
		});
		this.orderSummaryFrame = page.frameLocator(
			'iframe[title="Order Summary"]'
		);
		this.orderSummaryLink = page
			.getByText('Order Summary')
			.getByRole('link', {name: 'edit'});
		this.orderSummarySaveButton = page
			.locator('.modal-item-last')
			.getByRole('button', {name: 'Submit'});
		this.orderSummarySubtotal = page.locator(
			'div:nth-child(2) > .summary-table-item'
		);
		this.orderSummarySubtotalInput = this.orderSummaryFrame.getByLabel(
			'Subtotal',
			{exact: true}
		);
		this.page = page;
		this.paymentMethodName = page.locator('.payment-name');
		this.paymentMethodOption = (paymentMethod: string) =>
			this.editPaymentMethodFrame
				.locator('.list-group-title')
				.getByText(paymentMethod, {exact: true});
		this.paymentMethodRadioButton = async (paymentMethod: string) => {
			return this.editPaymentMethodFrame
				.locator('li')
				.filter({hasText: paymentMethod})
				.getByLabel('');
		};
		this.paymentStatusText = (status: string) =>
			page.getByText(status).first();
		this.transactionsTable = page.getByRole('table').filter({
			has: page.getByRole('columnheader', {
				exact: true,
				name: 'Timestamp',
			}),
		});
		this.firstTransactionTimestampCell = this.transactionsTable
			.locator('tbody')
			.getByRole('row')
			.first()
			.getByRole('cell')
			.nth(2);
		this.recalculateButton = page
			.getByText('Order Summary')
			.getByRole('link', {name: 'Recalculate'});
		this.recalculateOrderSummaryModalTitle = page
			.getByRole('heading', {
				name: 'Recalculate Order Summary',
			})
			.last();
		this.recalculateOrderSummaryModalCancelButton = page
			.locator('.modal-footer')
			.getByText('Cancel')
			.last();
		this.recalculateOrderSummaryModalContinueButton = page
			.locator('.modal-footer')
			.getByText('Continue')
			.last();
		this.reorderButton = page.getByRole('button', {
			exact: true,
			name: 'Reorder',
		});
		this.saveButton = page.getByRole('button', {name: 'Save'});
		this.selectDeliveryTerms = page
			.frameLocator('iframe[title="Delivery Terms"]')
			.getByLabel('Title');
		this.selectPaymentTerms = page
			.frameLocator('iframe[title="Payment Terms"]')
			.getByLabel('Title');
		this.submitModalButton = page.getByRole('button', {
			name: 'Submit',
		});
		this.submitPaymentMethod = page
			.locator('.modal-item-last')
			.getByRole('button', {exact: true, name: 'Submit'});
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
}
