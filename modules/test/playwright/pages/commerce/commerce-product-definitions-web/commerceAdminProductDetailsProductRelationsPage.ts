/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {FrameLocator, Locator, Page} from '@playwright/test';

import {CommerceDNDTablePage} from '../commerceDNDTablePage';
import {CommerceAdminProductDetailsPage} from './commerceAdminProductDetailsPage';

const searchTableRowByValue = async function (
	tableLocator: Locator,
	colPosition: number,
	value: string,
	strictEqual: boolean = false
) {
	await tableLocator.elementHandle();

	const rows = await tableLocator.locator('tr').all();

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

export class CommerceAdminProductDetailsProductRelationsPage extends CommerceDNDTablePage {
	readonly addNewProductFrame: FrameLocator;
	readonly addNewProductFrameTable: Locator;
	readonly addNewProductFrameTableRow: (
		colPosition: number,
		value: number | string,
		strictEqual?: boolean
	) => Promise<{column: Locator; row: Locator}>;
	readonly addNewProductFrameTableRowModifiedDateField: (
		rowValue: string
	) => Promise<Locator>;
	readonly addProductRelationHeading: (
		productName: string
	) => Promise<Locator>;
	readonly addUpSellProductMenuButton: Locator;
	readonly commerceAdminProductDetailsPage: CommerceAdminProductDetailsPage;
	readonly creationMenuNewButton: Locator;
	readonly deleteBulkButton: Locator;
	readonly page: Page;
	readonly productRelationsLink: Locator;
	readonly selectItemsInput: Locator;
	readonly addIncompatibleInBundleProduct: Locator;
	readonly addRequiresInBundleProduct: Locator;
	readonly addSpareProductMenuButton: Locator;
	readonly tableRowCreateDateField: (rowValue: string) => Promise<Locator>;

	constructor(page: Page) {
		super(
			page,
			'#_com_liferay_commerce_product_definitions_web_internal_portlet_CPDefinitionsPortlet_fm .fds table'
		);

		this.addNewProductFrame = page.frameLocator('iframe[id="modalIframe"]');
		this.addNewProductFrameTable = this.addNewProductFrame.locator(
			'#_com_liferay_item_selector_web_portlet_ItemSelectorPortlet_cpDefinitionsSearchContainer'
		);
		this.addNewProductFrameTableRow = async (
			colPosition: number,
			value: number | string,
			strictEqual: boolean = false
		) => {
			return await searchTableRowByValue(
				this.addNewProductFrameTable,
				colPosition,
				String(value),
				strictEqual
			);
		};
		this.addNewProductFrameTableRowModifiedDateField = async (rowValue) => {
			const tableRow = await this.addNewProductFrameTableRow(
				2,
				rowValue,
				true
			);

			if (tableRow && tableRow.row) {
				return tableRow.row.locator('.lfr-modified-date-column');
			}

			throw new Error(`Cannot locate row with rowValue: ${rowValue}`);
		};
		this.addProductRelationHeading = async (productName: string) => {
			return page.getByRole('heading', {
				name: 'Add New Product to ' + productName,
			});
		};
		this.commerceAdminProductDetailsPage =
			new CommerceAdminProductDetailsPage(page);
		this.creationMenuNewButton = page
			.getByTestId('managementToolbar')
			.getByRole('button', {name: 'New'});
		this.deleteBulkButton = page
			.locator('nav')
			.locator('.bulk-actions')
			.getByRole('button');
		this.page = page;
		this.productRelationsLink = page.getByRole('link', {
			exact: true,
			name: 'Product Relations',
		});
		this.selectItemsInput = page.locator('input[title="Select Items"]');
		this.addIncompatibleInBundleProduct = page.getByRole('menuitem', {
			exact: true,
			name: 'Add Incompatible in Bundle Product',
		});
		this.addRequiresInBundleProduct = page.getByRole('menuitem', {
			exact: true,
			name: 'Add Requires in Bundle Product',
		});
		this.addSpareProductMenuButton = page.getByRole('menuitem', {
			exact: true,
			name: 'Add Spare Product',
		});
		this.addUpSellProductMenuButton = page.getByRole('menuitem', {
			exact: true,
			name: 'Add Up-Sell Product',
		});
		this.tableRowCreateDateField = async (rowValue) => {
			const tableRow = await this.tableRow(2, rowValue, true);

			if (tableRow && tableRow.row) {
				return tableRow.row.locator('.cell-createDateString');
			}

			throw new Error(`Cannot locate row with rowValue: ${rowValue}`);
		};
	}

	async addIncompatibleInBundleProductRelation() {
		await this.goto();
		await this.creationMenuNewButton.click();
		await this.addIncompatibleInBundleProduct.click();
	}

	async addRequiresInBundleProductRelation() {
		await this.goto();
		await this.creationMenuNewButton.click();
		await this.addRequiresInBundleProduct.click();
	}

	async addSpareProductRelation() {
		await this.goto();
		await this.creationMenuNewButton.click();
		await this.addSpareProductMenuButton.click();
	}

	async goto() {
		await Promise.all([
			this.commerceAdminProductDetailsPage.goToProductRelations(),
			this.page.waitForResponse(
				(resp) =>
					resp.status() === 200 &&
					resp
						.url()
						.includes(
							'screenNavigationCategoryKey=product-relations'
						)
			),
		]);
	}
}
