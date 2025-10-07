/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {FrameLocator, Locator, Page} from '@playwright/test';

import {ApplicationsMenuPage} from '../../product-navigation-applications-menu/ApplicationsMenuPage';
import {searchTableRowByValue} from '../commerceDNDTablePage';
import {CommerceIframeDNDTablePage} from '../commerceIframeDNDTablePage';

export class CommerceAdminShipmentsPage extends CommerceIframeDNDTablePage {
	readonly addQuantityInShipment: Locator;
	readonly addProductsToShipment: Locator;
	readonly applicationsMenuPage: ApplicationsMenuPage;
	readonly backLink: Locator;
	readonly carrierDetailsEditLink: Locator;
	readonly carrierDetailsSubmitButton: Locator;
	readonly editProductCloseButton: Locator;
	readonly editProductMenuItem: Locator;
	readonly editProductSaveButton: Locator;
	readonly editProductTable: Locator;
	readonly editProductTableRow: (
		colPosition: number,
		value: number | string,
		strictEqual?: boolean
	) => Promise<{column: Locator; row: Locator}>;
	readonly editProductTableRowQuantitySelector: ({
		colIndex,
		rowValue,
	}) => Promise<Locator>;
	readonly editProductWarehouseAvailabilityTable: Locator;
	readonly editProductWarehouseAvailabilityTableRow: (
		colPosition: number,
		value: number | string,
		strictEqual?: boolean
	) => Promise<{column: Locator; row: Locator}>;
	readonly keyShipmentStatus: (orderStatus: string) => Locator;
	readonly page: Page;
	readonly productEllipsis: Locator;
	readonly productsSkuLink: (sku: string) => Locator;
	readonly shipmentIdLink: (shipmentId: string) => Locator;
	readonly shipmentsItemSubmitButton: Locator;
	readonly shipmentItemsTable: Locator;
	readonly shipmentItemsTableRow: (
		colPosition: number,
		value: number | string,
		strictEqual?: boolean
	) => Promise<{column: Locator; row: Locator}>;
	readonly shipmentItemsTableRows: () => Promise<Locator[]>;
	readonly shipmentItemsTableRowAction: (
		colposition: number,
		value: string
	) => Promise<Locator>;
	readonly shipmentItemFrame: FrameLocator;
	readonly shipmentStatusLink: (shipmentStatus: string) => Locator;
	readonly shippingMethodSelect: Locator;

	constructor(page: Page) {
		super(
			page,
			'iframe >> nth=1',
			'#_com_liferay_commerce_shipment_web_internal_portlet_CommerceShipmentPortlet_fm .fds table'
		);
		this.addQuantityInShipment = page
			.frameLocator('iframe')
			.getByRole('spinbutton');
		this.addProductsToShipment = page.getByText(
			'Add Products to This Shipment'
		);
		this.applicationsMenuPage = new ApplicationsMenuPage(page);
		this.carrierDetailsEditLink = page
			.getByText('Carrier Details Edit')
			.getByRole('link');
		this.carrierDetailsSubmitButton = page
			.locator('.modal-item-last')
			.getByRole('button', {exact: true, name: 'Submit'});
		this.backLink = page.locator('span[title="Back"]');
		this.editProductCloseButton = page
			.frameLocator('iframe')
			.getByRole('button')
			.first();
		this.editProductMenuItem = page.getByRole('menuitem', {
			exact: true,
			name: 'Edit',
		});
		this.editProductSaveButton = page
			.frameLocator('iframe')
			.getByRole('button', {exact: true, name: 'Save'});
		this.editProductTable = page.locator(
			'#_com_liferay_commerce_shipment_web_internal_portlet_CommerceShipmentPortlet_editShipmentContainer .fds table'
		);
		this.editProductTableRow = async (
			colPosition: number,
			value: number | string,
			strictEqual: boolean = false
		) => {
			return await searchTableRowByValue(
				this.editProductTable,
				colPosition,
				String(value),
				strictEqual
			);
		};
		this.editProductTableRowQuantitySelector = async ({
			colIndex = 0,
			rowValue,
		}: {
			colIndex: number;
			rowValue: number | string;
		}) => {
			const tableRow =
				await this.editProductWarehouseAvailabilityTableRow(
					colIndex,
					rowValue,
					true
				);

			if (tableRow && tableRow.column) {
				return tableRow.row.getByRole('spinbutton');
			}

			throw new Error(`Cannot locate row with rowValue: ${rowValue}`);
		};
		this.editProductWarehouseAvailabilityTable = page
			.frameLocator('iframe')
			.locator(
				'#_com_liferay_commerce_shipment_web_internal_portlet_CommerceShipmentPortlet_fm .fds table'
			);
		this.editProductWarehouseAvailabilityTableRow = async (
			colPosition: number,
			value: number | string,
			strictEqual: boolean = false
		) => {
			return await searchTableRowByValue(
				this.editProductWarehouseAvailabilityTable,
				colPosition,
				String(value),
				strictEqual
			);
		};
		this.keyShipmentStatus = (orderStatus: string) =>
			page.getByText(orderStatus);
		this.page = page;
		this.productEllipsis = page.getByRole('button', {
			exact: true,
			name: 'Actions',
		});
		this.productsSkuLink = (sku: string) =>
			page.getByRole('link', {exact: true, name: sku});
		this.shipmentIdLink = (shipmentId: string) =>
			page
				.locator('table')
				.getByRole('link', {exact: true, name: shipmentId});
		this.shipmentsItemSubmitButton = page
			.frameLocator('iframe >> nth=1')
			.getByRole('button', {exact: true, name: 'Submit'});
		this.shipmentItemsTable = this.table;
		this.shipmentItemsTableRow = this.tableRow;
		this.shipmentItemsTableRows = this.tableRows;
		this.shipmentItemsTableRowAction = async (
			colposition: number,
			value: string
		) => {
			const shipmentTableRow = await this.shipmentItemsTableRow(
				colposition,
				value,
				true
			);

			if (shipmentTableRow && shipmentTableRow.column) {
				return shipmentTableRow.row.getByLabel('', {exact: true});
			}

			throw new Error(`Cannot locate shipment row with value ${value}`);
		};
		this.shipmentItemFrame = page.frameLocator('iframe >> nth=1');
		this.shipmentStatusLink = (shipmentStatus: string) =>
			page.getByRole('link', {exact: true, name: shipmentStatus});
		this.shippingMethodSelect = page
			.frameLocator('iframe >> nth=1')
			.getByText('Shipping Method');
	}

	async goTo() {
		await this.applicationsMenuPage.goToCommerceShipments();
	}
}
