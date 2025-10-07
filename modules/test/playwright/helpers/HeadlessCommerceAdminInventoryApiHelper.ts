/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {getRandomInt} from '../utils/getRandomInt';
import {ApiHelpers, DataApiHelpers} from './ApiHelpers';

type TWarehouse = {
	active: boolean;
	latitude: number;
	longitude: number;
	name?: {
		[key: string]: string;
	};
	warehouseItems?: TWarehouseItem[];
};

type TWarehouseItem = {
	quantity: number;
	sku: string;
	unitOfMeasureKey?: string;
	warehouseId?: number;
};

export class HeadlessCommerceAdminInventoryApiHelper {
	readonly apiHelpers: ApiHelpers;
	readonly basePath: string;
	constructor(apiHelpers) {
		this.apiHelpers = apiHelpers;
		this.basePath = 'headless-commerce-admin-inventory/v1.0';
	}

	async deleteWarehouse(warehouseId: number) {
		return this.apiHelpers.delete(
			`${this.apiHelpers.baseUrl}${this.basePath}/warehouses/${warehouseId}`
		);
	}

	async deleteWarehouseItem(warehouseItemId: number) {
		return this.apiHelpers.delete(
			`${this.apiHelpers.baseUrl}${this.basePath}/warehouseItems/${warehouseItemId}`
		);
	}

	async getWarehousesPage() {
		return this.apiHelpers.get(
			`${this.apiHelpers.baseUrl}${this.basePath}/warehouses`
		);
	}

	async getWarehouseIdWarehouseItemsPage(
		warehouseId: number,
		pageSize: number = 100
	) {
		return this.apiHelpers.get(
			`${this.apiHelpers.baseUrl}${this.basePath}/warehouses/${warehouseId}/warehouseItems?pageSize=${pageSize}`
		);
	}

	async patchWarehouseItem(
		warehouseItemId: number,
		warehouseItem: TWarehouseItem
	) {
		return this.apiHelpers.patch(
			`${this.apiHelpers.baseUrl}${this.basePath}/warehouseItems/${warehouseItemId}`,
			{
				...(warehouseItem || {}),
			}
		);
	}

	async postWarehouses(warehouse: TWarehouse) {
		const postWarehouse = await this.apiHelpers.post(
			`${this.apiHelpers.baseUrl}${this.basePath}/warehouses`,
			{
				data: {
					name: {
						en_US: 'Warehouse' + getRandomInt(),
					},
					...warehouse,
				},
			}
		);

		if (this.apiHelpers instanceof DataApiHelpers) {
			this.apiHelpers.data.push({
				id: postWarehouse.id,
				type: 'warehouse',
			});
		}

		return postWarehouse;
	}

	async postWarehousesChannels(warehouseId: number, channelId: number) {
		await this.apiHelpers.post(
			`${this.apiHelpers.baseUrl}${this.basePath}/warehouses/${warehouseId}/warehouse-channels`,
			{
				data: {
					channelId,
					warehouseId,
				},
			}
		);
	}

	async postWarehousesWarehouseItems(
		warehouseId: number,
		warehouseItem: TWarehouseItem
	) {
		const warehouseItems = await this.apiHelpers.post(
			`${this.apiHelpers.baseUrl}${this.basePath}/warehouses/${warehouseId}/warehouseItems`,
			{
				data: {
					...warehouseItem,
				},
			}
		);

		if (this.apiHelpers instanceof DataApiHelpers) {
			this.apiHelpers.data.push({
				id: warehouseItems.id,
				type: 'warehouse-item',
			});
		}

		return warehouseItems;
	}
}
