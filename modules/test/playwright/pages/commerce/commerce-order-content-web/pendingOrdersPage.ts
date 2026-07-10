/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Locator, Page} from '@playwright/test';

import {DataApiHelpers} from '../../../helpers/ApiHelpers';
import {
	CommerceDNDTablePage,
	searchTableRowByValue,
} from '../commerceDNDTablePage';
import {CommerceLayoutsPage} from './commerceLayoutsPage';

export class PendingOrdersPage extends CommerceDNDTablePage {
	readonly approveButton: Locator;
	readonly checkoutButton: Locator;
	readonly deleteMenuItem: Locator;
	readonly doneButton: Locator;
	readonly editMenuItem: Locator;
	readonly errorMessageCloseButton: Locator;
	readonly layoutsPage: CommerceLayoutsPage;
	readonly orderCell: (orderId: string) => Locator;
	readonly orderColumn: (rowIndex: number, rowColumn: number) => Locator;
	readonly orderItemActionsButton: Locator;
	readonly orderItemActionsButtonEdit: Locator;
	readonly orderItemExpandButton: (productName: string) => Locator;
	readonly orderItemsTable: Locator;
	readonly orderItemsTableRow: (
		colPosition: number,
		value: number | string,
		strictEqual?: boolean
	) => Promise<{column: Locator; row: Locator}>;
	readonly orderItemsTableRowLink: (productName: string) => Promise<Locator>;
	readonly orderType: Locator;
	readonly orderId: Locator;
	readonly page: Page;
	readonly pageLabel: Locator;
	readonly pageTitle: Locator;
	readonly panelList: Locator;
	readonly questionsAndAnswersLink: Locator;
	readonly questionAndAnswersText: Locator;
	readonly rejectButton: Locator;
	readonly saveButton: Locator;
	readonly skuLink: (sku: string) => Locator;
	readonly viewButton: Locator;

	constructor(page: Page) {
		super(
			page,
			'#portlet_com_liferay_commerce_order_content_web_internal_portlet_CommerceOpenOrderContentPortlet .fds table'
		);

		this.approveButton = page.getByRole('button', {
			exact: true,
			name: 'Approve',
		});
		this.checkoutButton = page.getByText('Checkout');
		this.doneButton = page.getByRole('button', {
			exact: true,
			name: 'Done',
		});
		this.deleteMenuItem = page.getByRole('menuitem', {
			exact: true,
			name: 'Delete',
		});
		this.editMenuItem = page.getByRole('menuitem', {
			exact: true,
			name: 'Edit',
		});
		this.errorMessageCloseButton = page.getByRole('button', {
			exact: true,
			name: 'Close',
		});
		this.layoutsPage = new CommerceLayoutsPage(page);
		this.orderCell = (orderId) => page.getByRole('cell', {name: orderId});
		this.orderColumn = (rowIndex, colIndex) =>
			page.getByRole('row').nth(rowIndex).locator('td').nth(colIndex);
		this.orderItemActionsButton = page.getByRole('button', {
			name: 'Actions',
		});
		this.orderItemActionsButtonEdit = page.getByRole('menuitem', {
			name: 'Edit',
		});
		this.orderItemExpandButton = (productName) =>
			page
				.getByRole('gridcell', {exact: true, name: productName})
				.getByRole('button');
		this.orderItemsTable = page.locator(
			'#portlet_com_liferay_commerce_order_content_web_internal_portlet_CommerceOpenOrderContentPortlet .fds table'
		);
		this.orderItemsTableRow = async (
			colPosition: number,
			value: number | string,
			strictEqual: boolean = false
		) => {
			return await searchTableRowByValue(
				this.orderItemsTable,
				colPosition,
				String(value),
				strictEqual
			);
		};
		this.orderItemsTableRowLink = async (productName: string) => {
			const orderItemsTableRow = await this.orderItemsTableRow(
				0,
				productName,
				true
			);

			if (orderItemsTableRow && orderItemsTableRow.column) {
				return orderItemsTableRow.row.getByRole('button', {
					exact: true,
					name: `${productName} Actions`,
				});
			}

			throw new Error(
				`Cannot locate order item row with productName ${productName}`
			);
		};
		this.orderType = page
			.locator('dl')
			.filter({hasText: 'Order Type'})
			.locator('dd');

		this.orderId = page
			.locator('dl')
			.filter({hasText: 'Order ID'})
			.locator('dd');

		this.page = page;
		this.pageLabel = page
			.getByTestId('layoutHref')
			.getByLabel('Pending Orders Page');
		this.pageTitle = page
			.getByTestId('headerTitle')
			.filter({hasText: 'Pending Orders Page'});
		this.panelList = page
			.getByTestId('specificationFacetPanel')
			.getByRole('button');
		this.questionsAndAnswersLink = page.getByRole('link', {
			name: 'Questions and Answers',
		});
		this.questionAndAnswersText = page
			.locator('dt')
			.filter({hasText: 'Questions and Answers'});
		this.rejectButton = page.getByRole('button', {name: 'Reject'});
		this.saveButton = page.getByRole('button', {name: 'Save'});
		this.skuLink = (sku) => page.getByRole('link', {name: sku});
		this.viewButton = page.getByLabel('View');
	}

	async addPendingOrdersWidget() {
		await this.layoutsPage.addWidgetToPage('Open Carts');
	}

	async addOrderRuleOrderType(apiHelpers: DataApiHelpers, amount: number) {
		const orderRule =
			await apiHelpers.headlessCommerceAdminOrder.postOrderRule({
				type: 'minimum-order-amount',
				typeSettings:
					`minimum-order-amount-field-amount=${amount}` +
					'\nminimum-order-amount-field-apply-to=' +
					'minimum-order-amount-field-apply-to-order-total' +
					'\nminimum-order-amount-field-currency-code=' +
					'USD\n',
			});

		const orderType =
			await apiHelpers.headlessCommerceAdminOrder.postOrderType({
				active: true,
			});

		await apiHelpers.headlessCommerceAdminOrder.postOrderRuleIdOrderRuleOrderType(
			{
				orderRuleId: orderRule.id,
				orderTypeId: orderType.id,
			}
		);

		return {orderRule, orderType};
	}

	async goto() {
		await this.layoutsPage.goto();
	}
}
