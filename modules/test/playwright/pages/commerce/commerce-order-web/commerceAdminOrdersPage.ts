/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Locator, Page} from '@playwright/test';

import {GlobalMenuPage} from '../../product-navigation-applications-menu/GlobalMenuPage';
import {
	CommerceDNDTablePage,
	searchTableRowByValue,
} from '../commerceDNDTablePage';

export class CommerceAdminOrdersPage extends CommerceDNDTablePage {
	readonly addFilterButton: Locator;
	readonly globalMenuPage: GlobalMenuPage;
	readonly backLink: Locator;
	readonly deleteItemMenuItem: Locator;
	readonly editCommerceOrderTable: Locator;
	readonly editCommerceOrderTableRow: (
		colPosition: number,
		value: number | string,
		strictEqual?: boolean
	) => Promise<{column: Locator; row: Locator}>;
	readonly editCommerceOrderTableRows: () => Promise<Locator[]>;
	readonly filterButton: Locator;
	readonly editCommerceOrderTableRowLink: ({
		colIndex,
		rowValue,
	}) => Promise<Locator>;
	readonly itemsTableRow: (
		colPosition: number,
		value: number | string,
		strictEqual?: boolean
	) => Promise<{column: Locator; row: Locator}>;
	readonly itemsTableRows: () => Promise<Locator[]>;
	readonly itemsTableRowAction: (sku: string) => Promise<Locator>;
	readonly keyOrderStatus: (orderStatus: string) => Locator;
	readonly managementBarActionsButton: Locator;
	readonly managementBarCheckbox: Locator;
	readonly managementBarDeleteMenuItem: Locator;
	readonly menuActionButton: (accountName: string) => Locator;
	readonly menuItemAction: (action: string) => Locator;
	readonly orderActionsButton: Locator;
	readonly orderDate: Locator;
	readonly orderDateByOrderId: (orderId: string) => Locator;
	readonly orderId: Locator;
	readonly orderStatusLink: (orderStatus: string) => Locator;
	readonly page: Page;
	readonly quoteProcessedButton: Locator;
	readonly tableRowOrderIdLink: (orderId: number | string) => Locator;

	constructor(page: Page) {
		super(
			page,
			'#_com_liferay_commerce_order_web_internal_portlet_CommerceOrderPortlet_fm .fds table'
		);
		this.editCommerceOrderTable = page.locator(
			'#_com_liferay_commerce_order_web_internal_portlet_CommerceOrderPortlet_editOrderContainer .fds table'
		);
		this.editCommerceOrderTableRow = async (
			colPosition: number,
			value: number | string,
			strictEqual: boolean = false
		) => {
			return await searchTableRowByValue(
				this.editCommerceOrderTable,
				colPosition,
				String(value),
				strictEqual
			);
		};
		this.editCommerceOrderTableRows = async () => {
			await this.editCommerceOrderTable.elementHandle();

			return await this.editCommerceOrderTable.locator('tbody tr').all();
		};
		this.editCommerceOrderTableRowLink = async ({
			colIndex = 1,
			rowValue,
		}: {
			colIndex: number;
			rowValue: number | string;
		}) => {
			const tableRow = await this.editCommerceOrderTableRow(
				colIndex,
				rowValue,
				true
			);

			if (tableRow && tableRow.column) {
				return tableRow.column.getByRole('link', {
					name: String(rowValue),
				});
			}

			throw new Error(`Cannot locate row with rowValue: ${rowValue}`);
		};
		this.globalMenuPage = new GlobalMenuPage(page);
		this.backLink = page.locator('span[title="Back"]');
		this.deleteItemMenuItem = page.getByRole('menuitem', {
			exact: true,
			name: 'Delete',
		});
		this.itemsTableRow = this.editCommerceOrderTableRow;
		this.itemsTableRows = this.editCommerceOrderTableRows;
		this.itemsTableRowAction = async (sku: string) => {
			const itemsTableRow = await this.itemsTableRow(1, sku, true);

			if (itemsTableRow && itemsTableRow.column) {
				return itemsTableRow.row.getByRole('button', {
					exact: true,
					name: 'Item Actions',
				});
			}

			throw new Error(`Cannot locate order row with value ${sku}`);
		};
		this.keyOrderStatus = (orderStatus: string) =>
			page.locator('.fds table').getByText(orderStatus);
		this.managementBarActionsButton = page.getByLabel('Actions', {
			exact: true,
		});
		this.managementBarCheckbox = page.getByRole('checkbox').first();
		this.managementBarDeleteMenuItem = page.getByRole('menuitem', {
			exact: true,
			name: 'delete',
		});
		this.menuActionButton = (accountName) =>
			page.getByRole('row', {name: accountName}).getByRole('button');
		this.menuItemAction = (action) =>
			page.getByRole('menuitem', {exact: true, name: action});
		this.orderActionsButton = page.getByRole('button', {
			name: 'Actions',
		});
		this.orderDate = page.locator(
			'dl.commerce-list:has-text("Order Date") dd'
		);
		this.orderDateByOrderId = (orderId: string) =>
			page.locator(`tr:has-text("${orderId}") .cell-orderDate`);
		this.orderId = page.locator('dl.commerce-list:has-text("Order ID") dd');
		this.orderStatusLink = (orderStatus: string) =>
			page.getByRole('link', {exact: true, name: orderStatus});
		this.addFilterButton = page.getByRole('button', {
			exact: true,
			name: 'Add Filter',
		});
		this.filterButton = page.getByRole('button', {
			exact: true,
			name: 'Filter',
		});
		this.page = page;
		this.quoteProcessedButton = page.getByRole('link', {
			name: 'Quote Processed',
		});
		this.tableRowOrderIdLink = (orderId: number | string) =>
			this.table
				.locator('tbody')
				.getByRole('link', {exact: true, name: String(orderId)});
	}

	async applyFilter(category: string, valueLabel: string) {
		await this.filterButton.click();
		await this.page
			.getByRole('menuitem', {exact: true, name: category})
			.click();
		await this.page.getByLabel(valueLabel, {exact: true}).check();
		await this.addFilterButton.click();
	}

	async goto() {
		await this.globalMenuPage.goToCommerce('Orders');
	}

	async gotoOrder(orderId: number) {
		await this.goto();

		await (
			await this.tableRowLink({colIndex: 1, rowValue: orderId})
		).click();
	}
}
