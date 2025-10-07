/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Locator, Page} from '@playwright/test';

import {CommerceDNDTablePage} from '../commerceDNDTablePage';

export class CommerceThemeClassicOrdersPage extends CommerceDNDTablePage {
	readonly expandProductButton: Locator;
	readonly orderItemsTable: Locator;
	readonly orderItemsTableRow: (
		colPosition: number,
		value: number | string,
		strictEqual?: boolean
	) => Promise<{column: Locator; row: Locator}>;
	readonly orderTableItemsSelectorDropdown: Locator;
	readonly orderTableItemsSelector: Locator;
	readonly orderTableMenuItem: (value: string) => Locator;
	readonly orderTabs: (tabName: string) => Locator;

	readonly page: Page;

	constructor(page: Page) {
		super(
			page,
			'.lfr-layout-structure-item-com-liferay-commerce-order-content-web-internal-fragment-renderer-ordersdatasetfragmentrenderer .fds table'
		);

		this.expandProductButton = page
			.locator('.autofit-col-toggle')
			.getByRole('button');

		this.orderItemsTable = page.locator(
			'.lfr-layout-structure-item-com-liferay-commerce-order-content-web-internal-fragment-renderer-orderitemsdatasetfragmentrenderer .fds table'
		);
		this.orderItemsTableRow = async (
			colPosition: number,
			value: number | string,
			strictEqual: boolean = false
		) => {
			return await this.searchTableRowByValue(
				this.orderItemsTable,
				colPosition,
				String(value),
				strictEqual
			);
		};
		this.orderTableItemsSelectorDropdown = page.getByLabel('Actions');
		this.orderTableItemsSelector = page.locator(
			'#fnsd___table-id input[name="items-selector"]'
		);
		this.orderTableMenuItem = (value) =>
			page.getByRole('menuitem', {exact: true, name: value});
		this.orderTabs = (tabName) =>
			page.getByRole('tab', {exact: true, name: tabName});
	}

	searchTableRowByValue = async function (
		tableLocator: Locator,
		colPosition: number,
		value: string,
		strictEqual: boolean = false
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
}
