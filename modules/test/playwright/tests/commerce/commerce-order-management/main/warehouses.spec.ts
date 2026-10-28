/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {commercePagesTest} from '../../../../fixtures/commercePagesTest';
import {dataApiHelpersTest} from '../../../../fixtures/dataApiHelpersTest';
import {loginTest} from '../../../../fixtures/loginTest';
import {DataApiHelpers} from '../../../../helpers/ApiHelpers';
import {CommerceAdminWarehouseDetailsPage} from '../../../../pages/commerce/commerce-warehouse-web/commerceAdminWarehouseDetailsPage';
import {CommerceAdminWarehousesPage} from '../../../../pages/commerce/commerce-warehouse-web/commerceAdminWarehousesPage';
import {getRandomInt} from '../../../../utils/getRandomInt';
import getRandomString from '../../../../utils/getRandomString';
import {performLoginViaApi} from '../../../../utils/performLogin';
import {waitForAlert} from '../../../../utils/waitForAlert';
import {activateChannelPaymentMethod, miniumSetUp} from '../../utils/commerce';

export const test = mergeTests(
	commercePagesTest,
	dataApiHelpersTest,
	loginTest()
);

let catalog: {id: number; name: string};
let channel: {id: number; name: string};
let setupData: Array<{id: number | string; type: string}>;
let site: Site;

test.beforeAll(async ({browser}) => {
	const page = await browser.newPage();

	await performLoginViaApi({page, screenName: 'test'});

	const apiHelpers = new DataApiHelpers(page);

	const miniumResult = await miniumSetUp(apiHelpers);

	await activateChannelPaymentMethod(
		apiHelpers,
		miniumResult.channel.id,
		'paypal'
	);

	catalog = miniumResult.catalog;
	channel = miniumResult.channel;
	site = miniumResult.site;
	setupData = [...apiHelpers.data];

	await page.close();
});

test.afterAll(async ({browser}) => {
	const page = await browser.newPage();

	await performLoginViaApi({page, screenName: 'test'});

	const apiHelpers = new DataApiHelpers(page);

	apiHelpers.setData(setupData);

	await apiHelpers.clearData();

	await page.close();
});

test.afterEach(async ({browser}) => {
	const page = await browser.newPage();

	await performLoginViaApi({page, screenName: 'test'});

	const apiHelpers = new DataApiHelpers(page);

	const baselineWarehouseIds = new Set(
		setupData
			.filter((entry) => entry.type === 'warehouse')
			.map((entry) => entry.id)
	);

	const warehousesPage =
		await apiHelpers.headlessCommerceAdminInventoryApiHelper.getWarehousesPage();

	await Promise.all(
		(warehousesPage.items ?? [])
			.filter((w: {id: number}) => !baselineWarehouseIds.has(w.id))
			.map((w: {id: number}) =>
				apiHelpers.headlessCommerceAdminInventoryApiHelper.deleteWarehouse(
					w.id
				)
			)
	);

	await page.close();
});

test(
	'Inventory item safety stock can be edited and the item deleted via the admin UI',
	{tag: ['@COMMERCE-6207', '@LPD-88485']},
	async ({
		apiHelpers,
		commerceAdminInventoryItemPage,
		commerceAdminInventoryPage,
		page,
	}) => {
		const skuName = `TestSKU-${getRandomString()}`;
		const warehouseName = `TestWH-${getRandomString()}`;

		const warehouse =
			await apiHelpers.headlessCommerceAdminInventoryApiHelper.postWarehouses(
				{
					active: true,
					latitude: getRandomInt(),
					longitude: getRandomInt(),
					name: {en_US: warehouseName},
					warehouseItems: [{quantity: 50, sku: skuName}],
				}
			);

		await apiHelpers.headlessCommerceAdminInventoryApiHelper.postWarehousesChannels(
			warehouse.id,
			channel.id
		);

		await apiHelpers.headlessCommerceAdminCatalog.postProduct({
			catalogId: catalog.id,
			skus: [
				{
					cost: 0,
					price: 10,
					published: true,
					purchasable: true,
					sku: skuName,
				},
			],
		});

		await test.step('Open the inventory item detail and verify the warehouse row', async () => {
			await commerceAdminInventoryPage.goto();

			await commerceAdminInventoryPage.searchInput.fill(skuName);
			await commerceAdminInventoryPage.searchInput.press('Enter');

			await (
				await commerceAdminInventoryPage.tableRowLink({
					colIndex: 0,
					rowValue: skuName,
				})
			).click();

			await expect(
				commerceAdminInventoryItemPage.warehouseRowLink(warehouseName)
			).toBeVisible();
			await expect(
				commerceAdminInventoryItemPage.warehouseRowOnHandCell(
					warehouseName
				)
			).toContainText('50');
		});

		await test.step('Edit safety stock quantity in the side panel', async () => {
			await commerceAdminInventoryItemPage
				.warehouseRowLink(warehouseName)
				.click();

			await commerceAdminInventoryItemPage.sidePanelSafetyStockInput.fill(
				'20'
			);
			await commerceAdminInventoryItemPage.sidePanelSaveButton.click();

			await waitForAlert(
				commerceAdminInventoryItemPage.sidePanelFrameLocator
			);

			await page.reload();

			await expect(
				commerceAdminInventoryItemPage.warehouseRowSafetyStockCell(
					warehouseName
				)
			).toContainText('20');
			await expect(
				commerceAdminInventoryItemPage.warehouseRowAvailableCell(
					warehouseName
				)
			).toContainText('30');
		});

		await test.step('Delete the inventory item via the row actions menu', async () => {
			await commerceAdminInventoryItemPage
				.warehouseRowActionsButton(warehouseName)
				.click();
			await commerceAdminInventoryPage.deleteItemMenuItem.click();

			await commerceAdminInventoryPage.goto();

			await commerceAdminInventoryPage.searchInput.fill(skuName);
			await commerceAdminInventoryPage.searchInput.press('Enter');

			await expect(
				commerceAdminInventoryPage.skuLink(skuName)
			).toHaveCount(0);
		});
	}
);

test(
	'Incoming inventory items can be added, edited, and deleted via the admin UI',
	{tag: ['@COMMERCE-6206', '@LPD-88485']},
	async ({
		apiHelpers,
		commerceAdminInventoryItemPage,
		commerceAdminInventoryPage,
		page,
	}) => {
		const skuName = `TestSKU-${getRandomString()}`;
		const warehouseName = `TestWH-${getRandomString()}`;
		const initialDate = '06/11/2026';
		const updatedDate = '06/12/2026';

		const warehouse =
			await apiHelpers.headlessCommerceAdminInventoryApiHelper.postWarehouses(
				{
					active: true,
					latitude: getRandomInt(),
					longitude: getRandomInt(),
					name: {en_US: warehouseName},
					warehouseItems: [{quantity: 50, sku: skuName}],
				}
			);

		await apiHelpers.headlessCommerceAdminInventoryApiHelper.postWarehousesChannels(
			warehouse.id,
			channel.id
		);

		await apiHelpers.headlessCommerceAdminCatalog.postProduct({
			catalogId: catalog.id,
			skus: [
				{
					cost: 0,
					price: 10,
					published: true,
					purchasable: true,
					sku: skuName,
				},
			],
		});

		await test.step('Open the inventory item detail and switch to Incoming', async () => {
			await commerceAdminInventoryPage.goto();

			await commerceAdminInventoryPage.searchInput.fill(skuName);
			await commerceAdminInventoryPage.searchInput.press('Enter');

			await (
				await commerceAdminInventoryPage.tableRowLink({
					colIndex: 0,
					rowValue: skuName,
				})
			).click();

			await commerceAdminInventoryItemPage.incomingTab.click();
		});

		await test.step('Add incoming item via the Add modal', async () => {
			await commerceAdminInventoryItemPage.incomingAddButton.click();

			await commerceAdminInventoryItemPage.modalQuantityInput.fill('10');
			await commerceAdminInventoryItemPage.modalDestinationSelect.selectOption(
				{label: warehouseName}
			);
			await commerceAdminInventoryItemPage.modalDateInput.fill(
				initialDate
			);
			await commerceAdminInventoryItemPage.modalSubmitButton.click();

			await expect(
				commerceAdminInventoryItemPage.incomingRowLink(warehouseName)
			).toBeVisible();
			await expect(
				commerceAdminInventoryItemPage.incomingRowQuantityCell(
					warehouseName
				)
			).toContainText('10');
		});

		await test.step('Edit incoming quantity and date in the side panel', async () => {
			await commerceAdminInventoryItemPage
				.incomingRowLink(warehouseName)
				.click();

			await commerceAdminInventoryItemPage.sidePanelQuantityInput.fill(
				'15'
			);
			await commerceAdminInventoryItemPage.sidePanelDateInput.fill(
				updatedDate
			);
			await commerceAdminInventoryItemPage.sidePanelSaveButton.click();

			await waitForAlert(
				commerceAdminInventoryItemPage.sidePanelFrameLocator
			);

			await page.reload();

			await expect(
				commerceAdminInventoryItemPage.incomingRowQuantityCell(
					warehouseName
				)
			).toContainText('15');
		});

		await test.step('Delete the incoming item via the row actions menu', async () => {
			await commerceAdminInventoryItemPage
				.incomingRowActionsButton(warehouseName)
				.click();
			await commerceAdminInventoryPage.deleteItemMenuItem.click();

			await expect(
				commerceAdminInventoryItemPage.incomingRowLink(warehouseName)
			).toHaveCount(0);
		});
	}
);

test(
	'Warehouse channel eligibility can be switched between Specific Channels and No Channel',
	{tag: ['@COMMERCE-10640', '@LPD-88485']},
	async ({
		apiHelpers,
		commerceAdminWarehouseEligibilityPage,
		commerceAdminWarehousesPage,
	}) => {
		const warehouseName = `TestWH-${getRandomString()}`;

		const warehouse =
			await apiHelpers.headlessCommerceAdminInventoryApiHelper.postWarehouses(
				{
					active: true,
					latitude: getRandomInt(),
					longitude: getRandomInt(),
					name: {en_US: warehouseName},
				}
			);

		await apiHelpers.headlessCommerceAdminInventoryApiHelper.postWarehousesChannels(
			warehouse.id,
			channel.id
		);

		await commerceAdminWarehousesPage.goto();

		await commerceAdminWarehousesPage.warehouseLink(warehouseName).click();
		await commerceAdminWarehouseEligibilityPage.linkTab.click();

		await test.step('Specific Channels is selected by default and the linked channel is listed', async () => {
			await expect(
				commerceAdminWarehouseEligibilityPage.specificChannelsRadio
			).toBeChecked();
			await expect(
				commerceAdminWarehouseEligibilityPage.channelRow(channel.name)
			).toBeVisible();
		});

		await test.step('Switching to No Channel hides the Channels panel', async () => {
			await commerceAdminWarehouseEligibilityPage.noChannelRadio.check();

			await expect(
				commerceAdminWarehouseEligibilityPage.noChannelRadio
			).toBeChecked();
			await expect(
				commerceAdminWarehouseEligibilityPage.channelsHeading
			).toHaveCount(0);
		});

		await test.step('Switching back to Specific Channels restores the channel list', async () => {
			await commerceAdminWarehouseEligibilityPage.specificChannelsRadio.check();

			await expect(
				commerceAdminWarehouseEligibilityPage.specificChannelsRadio
			).toBeChecked();
			await expect(
				commerceAdminWarehouseEligibilityPage.channelRow(channel.name)
			).toBeVisible();
		});
	}
);

test(
	'Concurrent edits to the same warehouse surface a no-longer-valid error',
	{tag: ['@COMMERCE-6208', '@LPD-88485']},
	async ({apiHelpers, browser, page}) => {
		const warehouseName = `TestWH-${getRandomString()}`;
		const renamedFirst = `${warehouseName}-2`;
		const renamedSecond = `${warehouseName}-3`;

		await apiHelpers.headlessCommerceAdminInventoryApiHelper.postWarehouses(
			{
				active: true,
				latitude: getRandomInt(),
				longitude: getRandomInt(),
				name: {en_US: warehouseName},
			}
		);

		const warehousesPage1 = new CommerceAdminWarehousesPage(page);
		const detailsPage1 = new CommerceAdminWarehouseDetailsPage(page);

		await warehousesPage1.goto();

		await warehousesPage1.warehouseLink(warehouseName).click();

		const secondPage = await browser.newPage();

		await performLoginViaApi({page: secondPage, screenName: 'test'});

		const warehousesPage2 = new CommerceAdminWarehousesPage(secondPage);
		const detailsPage2 = new CommerceAdminWarehouseDetailsPage(secondPage);

		await warehousesPage2.goto();

		await warehousesPage2.warehouseLink(warehouseName).click();

		await test.step('Save the first rename in the second tab', async () => {
			await detailsPage2.nameInput.fill(renamedFirst);
			await detailsPage2.saveButton.click();

			await waitForAlert(secondPage);

			await secondPage.close();
		});

		await test.step('Saving in the original tab fails with the stale-state error', async () => {
			await detailsPage1.nameInput.fill(renamedSecond);
			await detailsPage1.saveButton.click();

			await expect(detailsPage1.itemNoLongerValidError).toBeVisible();
		});
	}
);

test(
	'Inventory admin On Hand and Available sum quantities across multiple warehouses for a SKU',
	{tag: ['@COMMERCE-6199', '@COMMERCE-6203', '@LPD-88485']},
	async ({
		apiHelpers,
		commerceAdminInventoryItemPage,
		commerceAdminInventoryPage,
	}) => {
		const skuName = `TestSKU-${getRandomString()}`;
		const warehouseNames = [
			`TestWH-${getRandomString()}`,
			`TestWH-${getRandomString()}`,
			`TestWH-${getRandomString()}`,
		];
		const quantities = [30, 40, 50];
		const expectedTotal = quantities.reduce((sum, q) => sum + q, 0);

		const warehouses = await Promise.all(
			quantities.map((quantity, index) =>
				apiHelpers.headlessCommerceAdminInventoryApiHelper.postWarehouses(
					{
						active: true,
						latitude: getRandomInt(),
						longitude: getRandomInt(),
						name: {en_US: warehouseNames[index]},
						warehouseItems: [{quantity, sku: skuName}],
					}
				)
			)
		);

		await Promise.all(
			warehouses.map((warehouse) =>
				apiHelpers.headlessCommerceAdminInventoryApiHelper.postWarehousesChannels(
					warehouse.id,
					channel.id
				)
			)
		);

		await apiHelpers.headlessCommerceAdminCatalog.postProduct({
			catalogId: catalog.id,
			skus: [
				{
					cost: 0,
					price: 10,
					published: true,
					purchasable: true,
					sku: skuName,
				},
			],
		});

		await commerceAdminInventoryPage.goto();

		await commerceAdminInventoryPage.searchInput.fill(skuName);
		await commerceAdminInventoryPage.searchInput.press('Enter');

		await (
			await commerceAdminInventoryPage.tableRowLink({
				colIndex: 0,
				rowValue: skuName,
			})
		).click();

		for (let index = 0; index < warehouseNames.length; index++) {
			await expect(
				commerceAdminInventoryItemPage.warehouseRowOnHandCell(
					warehouseNames[index]
				)
			).toContainText(String(quantities[index]));
		}

		const onHandTexts = await Promise.all(
			warehouseNames.map((name) =>
				commerceAdminInventoryItemPage
					.warehouseRowOnHandCell(name)
					.innerText()
			)
		);

		const totalOnHand = onHandTexts.reduce(
			(sum, text) => sum + Number(text.trim()),
			0
		);

		expect(totalOnHand).toBe(expectedTotal);
	}
);

test(
	'Channel-restricted warehouses contribute stock only to their eligible channels',
	{tag: ['@COMMERCE-6204', '@LPD-88485']},
	async ({apiHelpers}) => {
		const skuName = `TestSKU-${getRandomString()}`;

		const [channel1, channel2] = await Promise.all(
			[0, 1].map(() =>
				apiHelpers.headlessCommerceAdminChannel.postChannel({
					name: `TestChannel-${getRandomString()}`,
					siteGroupId: site.id,
				})
			)
		);

		const [warehouseChannel1, warehouseChannel2, warehouseShared] =
			await Promise.all(
				[20, 60, 60].map((quantity, index) =>
					apiHelpers.headlessCommerceAdminInventoryApiHelper.postWarehouses(
						{
							active: true,
							latitude: getRandomInt(),
							longitude: getRandomInt(),
							name: {
								en_US: `TestWH-${index}-${getRandomString()}`,
							},
							warehouseItems: [{quantity, sku: skuName}],
						}
					)
				)
			);

		await apiHelpers.headlessCommerceAdminInventoryApiHelper.postWarehousesChannels(
			warehouseChannel1.id,
			channel1.id
		);
		await apiHelpers.headlessCommerceAdminInventoryApiHelper.postWarehousesChannels(
			warehouseChannel2.id,
			channel2.id
		);
		await apiHelpers.headlessCommerceAdminInventoryApiHelper.postWarehousesChannels(
			warehouseShared.id,
			channel1.id
		);
		await apiHelpers.headlessCommerceAdminInventoryApiHelper.postWarehousesChannels(
			warehouseShared.id,
			channel2.id
		);

		const product =
			await apiHelpers.headlessCommerceAdminCatalog.postProduct({
				catalogId: catalog.id,
				productConfiguration: {
					allowBackOrder: false,
					displayStockQuantity: true,
					inventoryEngine: 'default',
				},
				skus: [
					{
						cost: 0,
						price: 10,
						published: true,
						purchasable: true,
						sku: skuName,
					},
				],
			});

		await expect(async () => {
			const channel1Skus =
				await apiHelpers.headlessCommerceDeliveryCatalog.getChannelProductSkusPage(
					channel1.id,
					product.productId
				);
			const channel2Skus =
				await apiHelpers.headlessCommerceDeliveryCatalog.getChannelProductSkusPage(
					channel2.id,
					product.productId
				);

			expect(channel1Skus.items?.[0]?.availability?.stockQuantity).toBe(
				80
			);
			expect(channel2Skus.items?.[0]?.availability?.stockQuantity).toBe(
				120
			);
		}).toPass({timeout: 15000});
	}
);

test(
	'Inventory item quantity can be updated via the warehouse side panel',
	{tag: ['@COMMERCE-5708', '@LPD-88485']},
	async ({
		apiHelpers,
		commerceAdminInventoryItemPage,
		commerceAdminInventoryPage,
		page,
	}) => {
		const skuName = `TestSKU-${getRandomString()}`;
		const warehouseName = `TestWH-${getRandomString()}`;

		const warehouse =
			await apiHelpers.headlessCommerceAdminInventoryApiHelper.postWarehouses(
				{
					active: true,
					latitude: getRandomInt(),
					longitude: getRandomInt(),
					name: {en_US: warehouseName},
					warehouseItems: [{quantity: 50, sku: skuName}],
				}
			);

		await apiHelpers.headlessCommerceAdminInventoryApiHelper.postWarehousesChannels(
			warehouse.id,
			channel.id
		);

		await apiHelpers.headlessCommerceAdminCatalog.postProduct({
			catalogId: catalog.id,
			skus: [
				{
					cost: 0,
					price: 10,
					published: true,
					purchasable: true,
					sku: skuName,
				},
			],
		});

		await commerceAdminInventoryPage.goto();
		await commerceAdminInventoryPage.searchInput.fill(skuName);
		await commerceAdminInventoryPage.searchInput.press('Enter');

		await (
			await commerceAdminInventoryPage.tableRowLink({
				colIndex: 0,
				rowValue: skuName,
			})
		).click();

		await commerceAdminInventoryItemPage
			.warehouseRowLink(warehouseName)
			.click();

		await commerceAdminInventoryItemPage.sidePanelQuantityInput.fill('100');
		await commerceAdminInventoryItemPage.sidePanelSaveButton.click();

		await waitForAlert(
			commerceAdminInventoryItemPage.sidePanelFrameLocator
		);

		await page.reload();

		await expect(
			commerceAdminInventoryItemPage.warehouseRowOnHandCell(warehouseName)
		).toContainText('100');
	}
);

test(
	'Shipment split across multiple warehouses consumes on-hand quantities per warehouse',
	{tag: ['@COMMERCE-6201', '@LPD-88485']},
	async ({apiHelpers}) => {
		const skuName = `TestSKU-${getRandomString()}`;
		const accountName = `TestAccount-${getRandomString()}`;

		const [warehouseItaly, warehouseSouthwest] = await Promise.all(
			[`Italy-${getRandomString()}`, `SW-${getRandomString()}`].map(
				(name) =>
					apiHelpers.headlessCommerceAdminInventoryApiHelper.postWarehouses(
						{
							active: true,
							latitude: getRandomInt(),
							longitude: getRandomInt(),
							name: {en_US: name},
							warehouseItems: [{quantity: 1, sku: skuName}],
						}
					)
			)
		);

		await apiHelpers.headlessCommerceAdminInventoryApiHelper.postWarehousesChannels(
			warehouseItaly.id,
			channel.id
		);
		await apiHelpers.headlessCommerceAdminInventoryApiHelper.postWarehousesChannels(
			warehouseSouthwest.id,
			channel.id
		);

		const product =
			await apiHelpers.headlessCommerceAdminCatalog.postProduct({
				catalogId: catalog.id,
				skus: [
					{
						cost: 0,
						price: 10,
						published: true,
						purchasable: true,
						sku: skuName,
					},
				],
			});

		const sku = product.skus[0];

		const account = await apiHelpers.headlessAdminUser.postAccount({
			name: accountName,
			type: 'business',
		});

		const address =
			await apiHelpers.headlessCommerceAdminAccount.postAddress(
				account.id,
				{
					phoneNumber: '1234567890',
					regionISOCode: 'AL',
				}
			);

		const order = await apiHelpers.headlessCommerceAdminOrder.postOrder({
			accountId: account.id,
			billingAddressId: address.id,
			channelId: channel.id,
			orderItems: [{quantity: 2, skuId: String(sku.id)}],
			paymentMethod: 'paypal',
			shippingAddressId: address.id,
			total: 20,
		});

		await apiHelpers.headlessCommerceAdminOrder.patchOrder(order.id, {
			orderStatus: '1',
		});

		const shipment =
			await apiHelpers.headlessCommerceAdminShipment.postShipment({
				orderId: order.id,
				shipmentItems: [
					{
						orderItemId: order.orderItems[0].id,
						quantity: 1,
						warehouseId: warehouseItaly.id,
					},
					{
						orderItemId: order.orderItems[0].id,
						quantity: 1,
						warehouseId: warehouseSouthwest.id,
					},
				],
			});

		await apiHelpers.headlessCommerceAdminShipment.postShipmentStatusDelivered(
			shipment.id
		);

		const italyItems =
			await apiHelpers.headlessCommerceAdminInventoryApiHelper.getWarehouseIdWarehouseItemsPage(
				warehouseItaly.id
			);
		const southwestItems =
			await apiHelpers.headlessCommerceAdminInventoryApiHelper.getWarehouseIdWarehouseItemsPage(
				warehouseSouthwest.id
			);

		expect(
			italyItems.items.find((item: {sku: string}) => item.sku === skuName)
				.quantity
		).toBe(0);
		expect(
			southwestItems.items.find(
				(item: {sku: string}) => item.sku === skuName
			).quantity
		).toBe(0);
	}
);

test(
	'Shipment item assigned to a specific warehouse only decrements that warehouse',
	{tag: ['@COMMERCE-6200', '@LPD-88485']},
	async ({apiHelpers}) => {
		const skuName = `TestSKU-${getRandomString()}`;
		const accountName = `TestAccount-${getRandomString()}`;

		const warehouses = await Promise.all(
			[20, 20, 20].map((quantity, index) =>
				apiHelpers.headlessCommerceAdminInventoryApiHelper.postWarehouses(
					{
						active: true,
						latitude: getRandomInt(),
						longitude: getRandomInt(),
						name: {
							en_US: `TestWH-${index}-${getRandomString()}`,
						},
						warehouseItems: [{quantity, sku: skuName}],
					}
				)
			)
		);

		await Promise.all(
			warehouses.map((warehouse) =>
				apiHelpers.headlessCommerceAdminInventoryApiHelper.postWarehousesChannels(
					warehouse.id,
					channel.id
				)
			)
		);

		const product =
			await apiHelpers.headlessCommerceAdminCatalog.postProduct({
				catalogId: catalog.id,
				skus: [
					{
						cost: 0,
						price: 10,
						published: true,
						purchasable: true,
						sku: skuName,
					},
				],
			});

		const sku = product.skus[0];

		const account = await apiHelpers.headlessAdminUser.postAccount({
			name: accountName,
			type: 'business',
		});

		const address =
			await apiHelpers.headlessCommerceAdminAccount.postAddress(
				account.id,
				{
					phoneNumber: '1234567890',
					regionISOCode: 'AL',
				}
			);

		const order = await apiHelpers.headlessCommerceAdminOrder.postOrder({
			accountId: account.id,
			billingAddressId: address.id,
			channelId: channel.id,
			orderItems: [{quantity: 1, skuId: String(sku.id)}],
			paymentMethod: 'paypal',
			shippingAddressId: address.id,
			total: 10,
		});

		await apiHelpers.headlessCommerceAdminOrder.patchOrder(order.id, {
			orderStatus: '1',
		});

		const [pickedWarehouse, ...otherWarehouses] = warehouses;

		const shipment =
			await apiHelpers.headlessCommerceAdminShipment.postShipment({
				orderId: order.id,
				shipmentItems: [
					{
						orderItemId: order.orderItems[0].id,
						quantity: 1,
						warehouseId: pickedWarehouse.id,
					},
				],
			});

		await apiHelpers.headlessCommerceAdminShipment.postShipmentStatusDelivered(
			shipment.id
		);

		const pickedItems =
			await apiHelpers.headlessCommerceAdminInventoryApiHelper.getWarehouseIdWarehouseItemsPage(
				pickedWarehouse.id
			);

		expect(
			pickedItems.items.find(
				(item: {sku: string}) => item.sku === skuName
			).quantity
		).toBe(19);

		for (const warehouse of otherWarehouses) {
			const items =
				await apiHelpers.headlessCommerceAdminInventoryApiHelper.getWarehouseIdWarehouseItemsPage(
					warehouse.id
				);

			expect(
				items.items.find((item: {sku: string}) => item.sku === skuName)
					.quantity
			).toBe(20);
		}
	}
);

test(
	'Cannot link the same channel eligibility entry twice to a warehouse',
	{tag: ['@COMMERCE-10053', '@LPD-88485']},
	async ({
		apiHelpers,
		commerceAdminWarehouseEligibilityPage,
		commerceAdminWarehousesPage,
	}) => {
		const warehouseName = `TestWH-${getRandomString()}`;

		const warehouse =
			await apiHelpers.headlessCommerceAdminInventoryApiHelper.postWarehouses(
				{
					active: true,
					latitude: getRandomInt(),
					longitude: getRandomInt(),
					name: {en_US: warehouseName},
				}
			);

		await apiHelpers.headlessCommerceAdminInventoryApiHelper.postWarehousesChannels(
			warehouse.id,
			channel.id
		);

		await commerceAdminWarehousesPage.goto();

		await commerceAdminWarehousesPage.warehouseLink(warehouseName).click();
		await commerceAdminWarehouseEligibilityPage.linkTab.click();

		await expect(async () => {
			await commerceAdminWarehouseEligibilityPage.addChannels.fill('');
			await commerceAdminWarehouseEligibilityPage.addChannels.fill(
				channel.name
			);

			await expect(
				commerceAdminWarehouseEligibilityPage.channelRowSelectButton(
					channel.name
				)
			).toHaveCount(0, {timeout: 2000});
		}).toPass({timeout: 15000});
	}
);

test(
	'Warehouse can be created, edited, and deleted via the admin UI',
	{tag: ['@COMMERCE-5706', '@COMMERCE-10308', '@LPD-88485']},
	async ({
		commerceAdminWarehouseDetailsPage,
		commerceAdminWarehousesPage,
		page,
	}) => {
		const warehouseName = `Test Warehouse ${getRandomString()}`;

		await test.step('Create the warehouse via the Add modal', async () => {
			await commerceAdminWarehousesPage.goto();

			await commerceAdminWarehousesPage.addButton.click();

			await commerceAdminWarehousesPage.modalFieldName.fill(
				warehouseName
			);
			await commerceAdminWarehousesPage.modalSubmitButton.click();
		});

		await test.step('Fill description, activate, set lat/long, and verify Geolocate is present', async () => {
			await commerceAdminWarehouseDetailsPage.descriptionTextarea.fill(
				'Test Warehouse Description'
			);

			await commerceAdminWarehouseDetailsPage.detailsActiveToggle.check();

			await commerceAdminWarehouseDetailsPage.latitudeInput.fill('12345');
			await commerceAdminWarehouseDetailsPage.longitudeInput.fill(
				'12345'
			);

			await expect(
				commerceAdminWarehouseDetailsPage.geolocateButton
			).toBeVisible();

			await commerceAdminWarehouseDetailsPage.saveButton.click();

			await waitForAlert(page);
		});

		await test.step('Add address and save', async () => {
			await commerceAdminWarehouseDetailsPage.street1Input.fill(
				'Test Address'
			);
			await commerceAdminWarehouseDetailsPage.street2Input.fill(
				'Test Street'
			);
			await commerceAdminWarehouseDetailsPage.countrySelect.selectOption({
				label: 'United States',
			});
			await commerceAdminWarehouseDetailsPage.regionSelect.selectOption({
				label: 'California',
			});
			await commerceAdminWarehouseDetailsPage.postalCodeInput.fill(
				'12345'
			);
			await commerceAdminWarehouseDetailsPage.cityInput.fill('Test City');

			await commerceAdminWarehouseDetailsPage.saveButton.click();

			await waitForAlert(page);
		});

		await test.step('Re-open the warehouse and verify all fields persisted', async () => {
			await commerceAdminWarehousesPage.goto();

			await commerceAdminWarehousesPage
				.warehouseLink(warehouseName)
				.click();

			await expect(
				commerceAdminWarehouseDetailsPage.nameInput
			).toHaveValue(warehouseName);
			await expect(
				commerceAdminWarehouseDetailsPage.descriptionTextarea
			).toHaveValue('Test Warehouse Description');
			await expect(
				commerceAdminWarehouseDetailsPage.detailsActiveToggle
			).toBeChecked();
			await expect(
				commerceAdminWarehouseDetailsPage.street1Input
			).toHaveValue('Test Address');
			await expect(
				commerceAdminWarehouseDetailsPage.street2Input
			).toHaveValue('Test Street');
			await expect(
				commerceAdminWarehouseDetailsPage.postalCodeInput
			).toHaveValue('12345');
			await expect(
				commerceAdminWarehouseDetailsPage.cityInput
			).toHaveValue('Test City');
			await expect(
				commerceAdminWarehouseDetailsPage.latitudeInput
			).toHaveValue(/12345/);
			await expect(
				commerceAdminWarehouseDetailsPage.longitudeInput
			).toHaveValue(/12345/);
		});

		await test.step('Delete the warehouse', async () => {
			await commerceAdminWarehousesPage.goto();

			await commerceAdminWarehousesPage
				.rowActionsButton(warehouseName)
				.click();
			await commerceAdminWarehousesPage.deleteMenuItem.click();

			await expect(
				commerceAdminWarehousesPage.warehouseLink(warehouseName)
			).toHaveCount(0);
		});
	}
);

test(
	'Product is Unavailable on the channel when warehouses have No Channel eligibility',
	{tag: ['@COMMERCE-10640', '@LPD-88485']},
	async ({apiHelpers}) => {
		const skuName = `TestSKU-${getRandomString()}`;

		await apiHelpers.headlessCommerceAdminInventoryApiHelper.postWarehouses(
			{
				active: true,
				latitude: getRandomInt(),
				longitude: getRandomInt(),
				name: {en_US: `TestWH-${getRandomString()}`},
				warehouseItems: [{quantity: 100, sku: skuName}],
			}
		);

		const product =
			await apiHelpers.headlessCommerceAdminCatalog.postProduct({
				catalogId: catalog.id,
				productConfiguration: {
					allowBackOrder: false,
					displayStockQuantity: true,
					inventoryEngine: 'default',
				},
				skus: [
					{
						cost: 0,
						price: 10,
						published: true,
						purchasable: true,
						sku: skuName,
					},
				],
			});

		const channelSkus =
			await apiHelpers.headlessCommerceDeliveryCatalog.getChannelProductSkusPage(
				channel.id,
				product.productId
			);

		expect(channelSkus.items?.[0]?.availability?.stockQuantity ?? 0).toBe(
			0
		);
	}
);

test(
	'Minium seeds the Italy warehouse with the expected address and coordinates',
	{tag: ['@COMMERCE-10308', '@LPD-88485']},
	async ({
		commerceAdminWarehouseDetailsPage,
		commerceAdminWarehousesPage,
	}) => {
		await commerceAdminWarehousesPage.goto();

		await commerceAdminWarehousesPage.warehouseLink('Italy').click();

		await expect(
			commerceAdminWarehouseDetailsPage.geolocateButton
		).toBeVisible();
		await expect(
			commerceAdminWarehouseDetailsPage.latitudeInput
		).toHaveValue('42.214601');
		await expect(
			commerceAdminWarehouseDetailsPage.longitudeInput
		).toHaveValue('12.796434');
		await expect(
			commerceAdminWarehouseDetailsPage.street1Input
		).toHaveValue('Via delle Coste 24');
		await expect(commerceAdminWarehouseDetailsPage.cityInput).toHaveValue(
			'Borgorose'
		);
		await expect(
			commerceAdminWarehouseDetailsPage.postalCodeInput
		).toHaveValue('2021');
		await expect(
			commerceAdminWarehouseDetailsPage.countrySelect
		).toHaveText(/Italy/);
		await expect(commerceAdminWarehouseDetailsPage.regionSelect).toHaveText(
			/Rieti/
		);
	}
);
