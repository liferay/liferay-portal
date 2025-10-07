/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {FrameLocator, Locator, Page} from '@playwright/test';

import {CommerceDNDTablePage} from '../commerceDNDTablePage';
import {CommerceLayoutsPage} from './commerceLayoutsPage';

export class PlacedOrdersPage extends CommerceDNDTablePage {
	readonly commerceBillingAddress: Locator;
	readonly configurationIFrame: FrameLocator;
	readonly configurationIFrameSaveButton: Locator;
	readonly configurationIFrameShowFullAddressToggle: Locator;
	readonly configurationIFrameShowPhoneNumberToggle: Locator;
	readonly configurationMenuItem: Locator;
	readonly expandProductButton: Locator;
	readonly layoutsPage: CommerceLayoutsPage;
	readonly optionsButton: Locator;
	readonly orderAccountName: (accountName: string) => Locator;
	readonly orderCell: (orderId: string) => Locator;
	readonly orderColumn: (rowIndex: number, rowColumn: number) => Locator;
	readonly orderDateSortButton: Locator;
	readonly orderItemActionsButton: Locator;
	readonly orderItemActionsButtonEdit: Locator;
	readonly page: Page;
	readonly pageLabel: Locator;
	readonly pageTitle: Locator;
	readonly panelList: Locator;
	readonly placedOrderTableViewButton: Locator;
	readonly searchButton: Locator;
	readonly searchInput: Locator;
	readonly commerceShippingAddress: Locator;
	readonly viewButton: Locator;

	constructor(page: Page) {
		super(
			page,
			'#portlet_com_liferay_commerce_order_content_web_internal_portlet_CommerceOrderContentPortlet .fds table'
		);

		this.commerceBillingAddress = page.getByTestId(
			'commerceBillingAddress'
		);
		this.configurationIFrame = page.frameLocator(
			'iframe[id="modalIframe"]'
		);
		this.configurationIFrameSaveButton = this.configurationIFrame.getByRole(
			'button',
			{name: 'Save'}
		);
		this.configurationIFrameShowFullAddressToggle =
			this.configurationIFrame.getByLabel('Show Order Full Address');
		this.configurationIFrameShowPhoneNumberToggle =
			this.configurationIFrame.getByLabel('Show Order Phone Number');

		this.configurationMenuItem = page.getByRole('menuitem', {
			exact: true,
			name: 'Configuration',
		});
		this.expandProductButton = page
			.locator('.autofit-col-toggle')
			.getByRole('button');
		this.layoutsPage = new CommerceLayoutsPage(page);
		this.optionsButton = page
			.locator(
				'#portlet_com_liferay_commerce_order_content_web_internal_portlet_CommerceOrderContentPortlet'
			)
			.getByLabel('Options');
		this.orderAccountName = (accountName: string) =>
			page.getByText(accountName);
		this.orderCell = (orderId) => page.getByRole('cell', {name: orderId});
		this.orderColumn = (rowIndex, colIndex) =>
			page.getByRole('row').nth(rowIndex).locator('td').nth(colIndex);
		this.orderDateSortButton = page
			.getByRole('columnheader', {name: 'Order Date'})
			.getByRole('button');
		this.orderItemActionsButton = page.getByRole('button', {
			name: 'Actions',
		});
		this.orderItemActionsButtonEdit = page.getByRole('menuitem', {
			name: 'Edit',
		});
		this.page = page;
		this.pageLabel = page
			.getByTestId('layoutHref')
			.getByLabel('Placed Orders Page');
		this.pageTitle = page
			.getByTestId('headerTitle')
			.filter({hasText: 'Placed Orders Page'});
		this.panelList = page
			.getByTestId('specificationFacetPanel')
			.getByRole('button');
		this.placedOrderTableViewButton = this.table.getByLabel('View');
		this.searchButton = page.getByRole('button', {
			exact: true,
			name: 'Search',
		});
		this.searchInput = page.getByPlaceholder('Search');
		this.commerceShippingAddress = page.getByTestId(
			'commerceShippingAddress'
		);
		this.viewButton = page.getByLabel('View');
	}

	searchTableRowByValue = async function (
		colPosition: number,
		value: string,
		strictEqual: boolean = false,
		tableLocator: Locator = this.table
	) {
		await tableLocator.elementHandle();

		const rows = await tableLocator.locator('tbody tr').all();

		for await (const row of rows) {
			const column = row.locator('td').nth(colPosition).first();

			const colValue = (await column.allInnerTexts()).join('');

			if (
				(strictEqual && colValue === value) ||
				(!strictEqual &&
					colValue.toLowerCase().indexOf(value.toLowerCase()) >= 0)
			) {
				return {column, row};
			}
		}

		throw new Error(`Cannot locate table row with value ${value}`);
	};

	async goto() {
		await this.layoutsPage.goto();
	}
}
