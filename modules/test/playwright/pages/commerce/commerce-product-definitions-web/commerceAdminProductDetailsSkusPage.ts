/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {FrameLocator, Locator, Page} from '@playwright/test';

import {
	CommerceDNDTablePage,
	searchTableRowByValue,
} from '../commerceDNDTablePage';

export class CommerceAdminProductDetailsSkusPage extends CommerceDNDTablePage {
	readonly closeSidePanelFrame: (isNestedFrame: boolean) => Promise<Locator>;
	readonly inventoryTableRow: (
		colPosition: number,
		value: number | string,
		strictEqual?: boolean
	) => Promise<{column: Locator; row: Locator}>;
	readonly inventoryTableRowAction: (pageName: string) => Promise<Locator>;
	readonly page: Page;
	readonly pricinQuantity: Locator;
	readonly sidePanelDetailsSkuFieldName: Locator;
	readonly sidePanelDetailsSkuPublishButton: Locator;
	readonly sidePanelFrame: FrameLocator;
	readonly sidePanelInventoryTable: Locator;
	readonly sidePanelNestedFrame: FrameLocator;
	readonly sidePanelNestedPriceListPrice: Locator;
	readonly sidePanelNestedSaveButton: Locator;
	readonly sidePanelSkuInventoryQuantity: Locator;
	readonly sidePanelSaveButton: Locator;
	readonly sidePanelSkuPriceTableRowLink: (priceListName: string) => Locator;
	readonly skusLink: Locator;
	readonly skuPriceAddButton: Locator;
	readonly skuPriceAddModal: FrameLocator;
	readonly skuPriceFrame: FrameLocator;
	readonly skuPriceListSelect: Locator;
	readonly skuTab: (tabName: string) => Locator;
	readonly skusTable: Locator;
	readonly skusTableRowBasePrice: (price: string) => Promise<Locator>;
	readonly skusTableRow: (
		colPosition: number,
		value: number | string,
		strictEqual?: boolean
	) => Promise<{column: Locator; row: Locator}>;
	readonly skusTableRowLink: (skuName: string) => Locator;
	readonly skuUOMFrame: FrameLocator;
	readonly skuUOMTab: Locator;
	readonly skuUOMFrameCancelButton: Locator;
	readonly uomTableRowLink: (uomName: string) => Locator;

	constructor(page: Page) {
		super(
			page,
			'#_com_liferay_commerce_product_definitions_web_internal_portlet_CPDefinitionsPortlet_fm .fds table'
		);
		this.closeSidePanelFrame = async (isNestedFrame: boolean) => {
			if (isNestedFrame) {
				return this.sidePanelNestedFrame
					.locator('.side-panel-iframe-header')
					.getByRole('button');
			}

			return this.sidePanelFrame
				.locator('.side-panel-iframe-header')
				.getByRole('button');
		};
		this.inventoryTableRow = async (
			colPosition: number,
			value: number | string,
			strictEqual: boolean = false
		) => {
			return await searchTableRowByValue(
				this.sidePanelInventoryTable,
				colPosition,
				String(value),
				strictEqual
			);
		};
		this.inventoryTableRowAction = async (warehouseName: string) => {
			const inventoriesTableRow = await this.inventoryTableRow(
				0,
				warehouseName,
				true
			);

			if (inventoriesTableRow && inventoriesTableRow.column) {
				return inventoriesTableRow.row.getByRole('textbox', {
					name: 'commerce-inventory-warehouse-',
				});
			}

			throw new Error(
				`Cannot locate table row with name ${warehouseName}`
			);
		};
		this.page = page;
		this.pricinQuantity = page
			.frameLocator('iframe')
			.first()
			.frameLocator('iframe')
			.getByLabel('Pricing Quantity');
		this.sidePanelFrame = page.frameLocator('.is-visible iframe');
		this.sidePanelDetailsSkuFieldName =
			this.sidePanelFrame.getByLabel('SKU Required');
		this.sidePanelDetailsSkuPublishButton = this.sidePanelFrame.getByRole(
			'button',
			{exact: true, name: 'Publish'}
		);
		this.sidePanelInventoryTable = this.sidePanelFrame.locator(
			'#p_p_id_com_liferay_commerce_product_definitions_web_internal_portlet_CPDefinitionsPortlet_ table'
		);
		this.sidePanelNestedFrame =
			this.sidePanelFrame.frameLocator('.is-visible iframe');
		this.sidePanelNestedPriceListPrice =
			this.sidePanelNestedFrame.getByLabel('Price List');
		this.sidePanelNestedSaveButton = this.sidePanelNestedFrame.getByRole(
			'button',
			{exact: true, name: 'Save'}
		);
		this.sidePanelSaveButton = this.sidePanelFrame.getByRole('button', {
			exact: true,
			name: 'Save',
		});
		this.sidePanelSkuInventoryQuantity = this.sidePanelFrame.getByRole(
			'textbox',
			{name: 'commerce-inventory-warehouse-'}
		);
		this.sidePanelSkuPriceTableRowLink = (priceListName: string) =>
			this.sidePanelFrame.getByRole('link', {
				exact: true,
				name: priceListName,
			});
		this.skusLink = page.getByRole('link', {
			exact: true,
			name: 'SKUs',
		});
		this.skuPriceFrame = page.frameLocator('iframe').first();
		this.skuPriceAddButton = this.skuPriceFrame.locator(
			'[data-testid="fdsCreationActionButton"]'
		);
		this.skuPriceAddModal = page.frameLocator('iframe[title="Add Price"]');
		this.skuPriceListSelect =
			this.skuPriceAddModal.getByLabel('Price List');
		this.skuTab = (skuTab) =>
			this.skuPriceFrame.getByRole('link', {
				exact: true,
				name: skuTab,
			});
		this.skusTable = page.locator(
			'#_com_liferay_commerce_product_definitions_web_internal_portlet_CPDefinitionsPortlet_fm .fds table'
		);
		this.skusTableRow = async (
			colPosition: number,
			value: number | string,
			strictEqual: boolean = false
		) => {
			return await searchTableRowByValue(
				this.skusTable,
				colPosition,
				String(value),
				strictEqual
			);
		};
		this.skusTableRowBasePrice = async (price: string) => {
			const shipmentTableRow = await this.skusTableRow(2, price, true);

			if (shipmentTableRow && shipmentTableRow.column) {
				return shipmentTableRow.row.getByText(price, {exact: true});
			}

			throw new Error(`Cannot locate shipment row with value ${price}`);
		};
		this.skusTableRowLink = (sku: string) =>
			page.getByRole('link', {exact: true, name: sku});
		this.skuUOMFrame = page.frameLocator('iframe').first();
		this.skuUOMTab = this.skuUOMFrame.getByRole('link', {
			name: 'Units of Measure',
		});
		this.skuUOMFrameCancelButton = this.skuUOMFrame
			.frameLocator('iframe')
			.getByRole('button', {name: 'Cancel'});
		this.uomTableRowLink = (uom: string) =>
			this.skuUOMFrame.getByRole('link', {exact: true, name: uom});
	}

	async addWarehouseQuantity(quantity: string, warehouseName: string) {
		await (
			await this.inventoryTableRowAction(warehouseName)
		).fill(quantity);

		await (
			await this.inventoryTableRow(0, warehouseName, true)
		).row
			.getByRole('button', {
				exact: true,
				name: 'Save',
			})
			.click();
	}

	async goToSkuUOM() {
		await this.skuUOMTab.click();
	}

	async goToSkuTab(skuTab: string) {
		await this.skuTab(skuTab).click();
	}
}
