/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {applicationsMenuPageTest} from '../../../../fixtures/applicationsMenuPageTest';
import {commercePagesTest} from '../../../../fixtures/commercePagesTest';
import {dataApiHelpersTest} from '../../../../fixtures/dataApiHelpersTest';
import {isolatedSiteTest} from '../../../../fixtures/isolatedSiteTest';
import {loginTest} from '../../../../fixtures/loginTest';
import {getRandomInt} from '../../../../utils/getRandomInt';

export const test = mergeTests(
	applicationsMenuPageTest,
	commercePagesTest,
	dataApiHelpersTest,
	isolatedSiteTest,
	loginTest()
);

test(
	'Verify inventory changelog displays decimals with UOM and integer without',
	{tag: ['@LPD-59007']},
	async ({apiHelpers, commerceAdminInventoryPage, page, site}) => {
		const catalog =
			await apiHelpers.headlessCommerceAdminCatalog.postCatalog();

		const channel =
			await apiHelpers.headlessCommerceAdminChannel.postChannel({
				siteGroupId: site.id,
			});

		const product1 =
			await apiHelpers.headlessCommerceAdminCatalog.postProduct({
				catalogId: catalog.id,
				productConfiguration: {
					maxOrderQuantity: 10000,
					minOrderQuantity: 1,
					minStockQuantity: 5,
					multipleOrderQuantity: 1.5,
				},
				skus: [
					{
						cost: 0,
						price: 10,
						published: true,
						purchasable: true,
						sku: 'Sku' + getRandomInt(),
					},
				],
			});

		const productSkus1 = await apiHelpers.headlessCommerceAdminCatalog
			.getProduct(product1.productId)
			.then((product) => {
				return product.skus;
			});

		const sku1 = productSkus1[0];

		const skuUOM1 =
			await apiHelpers.headlessCommerceAdminCatalog.postSkuUnitOfMeasure(
				sku1.id,
				{
					basePrice: 10,
					incrementalOrderQuantity: 1.2345,
					precision: 4,
					priority: 0,
				}
			);

		const product2 =
			await apiHelpers.headlessCommerceAdminCatalog.postProduct({
				catalogId: catalog.id,
				productConfiguration: {
					maxOrderQuantity: 10000,
					minOrderQuantity: 1,
					minStockQuantity: 5,
					multipleOrderQuantity: 1,
				},
				skus: [
					{
						cost: 0,
						price: 15,
						published: true,
						purchasable: true,
						sku: 'Sku' + getRandomInt(),
					},
				],
			});

		const productSkus2 = await apiHelpers.headlessCommerceAdminCatalog
			.getProduct(product2.productId)
			.then((product) => {
				return product.skus;
			});

		const sku2 = productSkus2[0];

		const warehouse =
			await apiHelpers.headlessCommerceAdminInventoryApiHelper.postWarehouses(
				{
					active: true,
					latitude: getRandomInt(),
					longitude: getRandomInt(),
					warehouseItems: [
						{
							quantity: 100,
							sku: sku1.sku,
						},
						{
							quantity: 100,
							sku: sku2.sku,
						},
					],
				}
			);

		await apiHelpers.headlessCommerceAdminInventoryApiHelper.postWarehousesChannels(
			warehouse.id,
			channel.id
		);

		const warehouseItems =
			await apiHelpers.headlessCommerceAdminInventoryApiHelper.getWarehouseIdWarehouseItemsPage(
				warehouse.id
			);

		const warehouseItem1 = warehouseItems.items.find(
			(warehouseItem) =>
				warehouseItem.sku === sku1.sku &&
				warehouseItem.unitOfMeasureKey === skuUOM1.key
		);

		await apiHelpers.headlessCommerceAdminInventoryApiHelper.patchWarehouseItem(
			warehouseItem1.id,
			{
				quantity: 1000,
				sku: warehouseItem1.sku,
				unitOfMeasureKey: warehouseItem1.unitOfMeasureKey,
			}
		);

		const warehouseItem2 = warehouseItems.items.find(
			(warehouseItem) => warehouseItem.sku === sku2.sku
		);

		await apiHelpers.headlessCommerceAdminInventoryApiHelper.patchWarehouseItem(
			warehouseItem2.id,
			{
				quantity: 1000,
				sku: warehouseItem2.sku,
				unitOfMeasureKey: warehouseItem2.unitOfMeasureKey,
			}
		);

		await commerceAdminInventoryPage.goto();

		await expect(
			await commerceAdminInventoryPage.tableRowLink({
				colIndex: 0,
				rowValue: sku1.sku,
			})
		).toBeVisible();

		await (
			await commerceAdminInventoryPage.tableRowLink({
				colIndex: 0,
				rowValue: sku1.sku,
			})
		).click();

		await commerceAdminInventoryPage.changeLogLink.click();

		await expect(await page.getByText('1000.0000')).toBeVisible();

		await commerceAdminInventoryPage.backLink.click();

		await expect(
			await commerceAdminInventoryPage.tableRowLink({
				colIndex: 0,
				rowValue: sku2.sku,
			})
		).toBeVisible();

		await (
			await commerceAdminInventoryPage.tableRowLink({
				colIndex: 0,
				rowValue: sku2.sku,
			})
		).click();

		await commerceAdminInventoryPage.changeLogLink.click();

		await expect(await page.getByText('1000')).toBeVisible();
	}
);
