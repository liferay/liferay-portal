/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {commercePagesTest} from '../../../../fixtures/commercePagesTest';
import {customFieldsPagesTest} from '../../../../fixtures/customFieldsPagesTest';
import {dataApiHelpersTest} from '../../../../fixtures/dataApiHelpersTest';
import {loginTest} from '../../../../fixtures/loginTest';
import {DataApiHelpers} from '../../../../helpers/ApiHelpers';
import {ViewAttributesPage} from '../../../../pages/expando-web/ViewAttributesPage';
import getRandomString from '../../../../utils/getRandomString';
import {
	performLoginViaApi,
	performUserSwitch,
	userData,
} from '../../../../utils/performLogin';
import {waitForAlert} from '../../../../utils/waitForAlert';
import {createAccountWithBuyerUser, miniumSetUp} from '../../utils/commerce';
import {
	acceptOrderAndCreateShipment,
	acceptOrderAndCreateShipmentWithItem,
	addShipmentItems,
	finishProcessingAndShip,
	setUpShipmentOrder,
} from '../../utils/shipments';

export const test = mergeTests(
	commercePagesTest,
	customFieldsPagesTest,
	dataApiHelpersTest,
	loginTest()
);

let miniumCatalog: {id: number; name: string};
let miniumChannel: {id: number; name: string};
let setupData: Array<{id: number | string; type: string}>;
let site: Site;

test.beforeAll(async ({browser}) => {
	const page = await browser.newPage();

	await performLoginViaApi({page, screenName: 'test'});

	const apiHelpers = new DataApiHelpers(page);

	const miniumResult = await miniumSetUp(apiHelpers);

	miniumCatalog = miniumResult.catalog;
	miniumChannel = miniumResult.channel;
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

test(
	'Add items to a shipment and verify the shipment item quantity',
	{tag: ['@COMMERCE-6238', '@LPD-93911']},
	async ({
		apiHelpers,
		commerceAdminOrderDetailsPage,
		commerceAdminOrdersPage,
		commerceAdminShipmentsPage,
		page,
	}) => {
		const {order, skus, warehouseName} = await setUpShipmentOrder(
			apiHelpers,
			{
				catalog: miniumCatalog,
				channel: miniumChannel,
			}
		);

		const sku = skus[0];

		await acceptOrderAndCreateShipmentWithItem(
			{
				commerceAdminOrderDetailsPage,
				commerceAdminOrdersPage,
				commerceAdminShipmentsPage,
				page,
			},
			{orderId: order.id, sku, warehouseName}
		);

		const shipmentItemRow = commerceAdminShipmentsPage.shipmentItemRow(
			sku.sku
		);

		await expect(shipmentItemRow).toBeVisible();
		await expect(shipmentItemRow).toContainText(warehouseName);
		await expect(
			commerceAdminShipmentsPage.shipmentItemCell(sku.sku, '0')
		).toBeVisible();
		await expect(
			commerceAdminShipmentsPage.shipmentItemCell(sku.sku, '1')
		).toBeVisible();
	}
);

test(
	'Shipment Base Tracking URL can be added, viewed and edited',
	{tag: ['@COMMERCE-9459', '@LPD-93911']},
	async ({
		apiHelpers,
		commerceAdminOrderDetailsPage,
		commerceAdminOrdersPage,
		commerceAdminShipmentsPage,
		page,
	}) => {
		const {order, skus, warehouseName} = await setUpShipmentOrder(
			apiHelpers,
			{
				catalog: miniumCatalog,
				channel: miniumChannel,
			}
		);

		await acceptOrderAndCreateShipmentWithItem(
			{
				commerceAdminOrderDetailsPage,
				commerceAdminOrdersPage,
				commerceAdminShipmentsPage,
				page,
			},
			{orderId: order.id, sku: skus[0], warehouseName}
		);

		const setTrackingURL = async (trackingURL: string) => {
			await commerceAdminShipmentsPage.carrierDetailsEditLink.click();

			await commerceAdminShipmentsPage.carrierDetailsTrackingURLInput.fill(
				trackingURL
			);

			await commerceAdminShipmentsPage.carrierDetailsSubmitButton.click();

			await expect(
				commerceAdminShipmentsPage.baseTrackingURLValue
			).toHaveText(trackingURL);
		};

		await setTrackingURL('www.carriersite.com/');
		await setTrackingURL('www.carriersite2.com/');
	}
);

test(
	'Cannot finish processing a shipment with no items',
	{tag: ['@COMMERCE-6243', '@LPD-93911']},
	async ({
		apiHelpers,
		commerceAdminOrderDetailsPage,
		commerceAdminOrdersPage,
		commerceAdminShipmentsPage,
		page,
	}) => {
		const {order} = await setUpShipmentOrder(apiHelpers, {
			catalog: miniumCatalog,
			channel: miniumChannel,
		});

		await acceptOrderAndCreateShipment(
			{commerceAdminOrderDetailsPage, commerceAdminOrdersPage, page},
			{orderId: order.id}
		);

		await commerceAdminShipmentsPage
			.shipmentStatusLink('Finish Processing')
			.click();

		await waitForAlert(
			page,
			'Please add at least one item to the shipment.',
			{autoClose: false, type: 'danger'}
		);
	}
);

test(
	'Delete a shipment from the shipments list',
	{tag: ['@COMMERCE-6250', '@LPD-93911']},
	async ({
		apiHelpers,
		commerceAdminOrderDetailsPage,
		commerceAdminOrdersPage,
		commerceAdminShipmentsPage,
		page,
	}) => {
		const {account, order, skus, warehouseName} = await setUpShipmentOrder(
			apiHelpers,
			{
				catalog: miniumCatalog,
				channel: miniumChannel,
			}
		);

		await acceptOrderAndCreateShipmentWithItem(
			{
				commerceAdminOrderDetailsPage,
				commerceAdminOrdersPage,
				commerceAdminShipmentsPage,
				page,
			},
			{orderId: order.id, sku: skus[0], warehouseName}
		);

		const shipments =
			await apiHelpers.headlessCommerceAdminShipment.getShipments();

		const shipment = (shipments.items ?? []).find(
			(item: {accountId: number}) => item.accountId === account.id
		);

		expect(shipment).toBeDefined();

		await commerceAdminShipmentsPage.goTo();

		await commerceAdminShipmentsPage
			.shipmentListRowActions(shipment.id)
			.click();
		await commerceAdminShipmentsPage.deleteMenuItem.click();
		await commerceAdminShipmentsPage.deleteModalSubmitButton.click();

		await expect(async () => {
			const remaining =
				await apiHelpers.headlessCommerceAdminShipment.getShipments();

			expect(
				(remaining.items ?? []).find(
					(item: {id: number}) => item.id === shipment.id
				)
			).toBeUndefined();
		}).toPass({timeout: 10000});
	}
);

test(
	'Remove an item from a shipment',
	{tag: ['@COMMERCE-6240', '@LPD-93911']},
	async ({
		apiHelpers,
		commerceAdminOrderDetailsPage,
		commerceAdminOrdersPage,
		commerceAdminShipmentsPage,
		page,
	}) => {
		const {order, skus, warehouseName} = await setUpShipmentOrder(
			apiHelpers,
			{catalog: miniumCatalog, channel: miniumChannel, skuCount: 2}
		);

		await acceptOrderAndCreateShipment(
			{commerceAdminOrderDetailsPage, commerceAdminOrdersPage, page},
			{orderId: order.id}
		);

		await addShipmentItems(commerceAdminShipmentsPage, skus, warehouseName);

		await commerceAdminShipmentsPage
			.shipmentItemRowActions(skus[1].sku)
			.click();
		await commerceAdminShipmentsPage.deleteMenuItem.click();
		await commerceAdminShipmentsPage.deleteModalSubmitButton.click();

		await expect(
			commerceAdminShipmentsPage.shipmentItemRow(skus[1].sku)
		).toHaveCount(0);
		await expect(
			commerceAdminShipmentsPage.shipmentItemRow(skus[0].sku)
		).toBeVisible();
	}
);

test(
	'Create multiple shipments for one order',
	{tag: ['@COMMERCE-6247', '@LPD-93911']},
	async ({
		apiHelpers,
		commerceAdminOrderDetailsPage,
		commerceAdminOrdersPage,
		commerceAdminShipmentsPage,
		page,
	}) => {
		const {order, skus, warehouseName} = await setUpShipmentOrder(
			apiHelpers,
			{catalog: miniumCatalog, channel: miniumChannel, skuCount: 5}
		);

		const expectOrderStatus = async (status: number) => {
			await expect(async () => {
				const updated =
					await apiHelpers.headlessCommerceAdminOrder.getOrder(
						order.id
					);

				expect(Number(updated.orderStatus)).toBe(status);
			}).toPass({timeout: 10000});
		};

		const openOrder = async () => {
			await commerceAdminOrdersPage.goto();

			await commerceAdminOrdersPage.tableRowOrderIdLink(order.id).click();

			await expect(
				commerceAdminOrderDetailsPage.headerDetailsTitle
			).toBeVisible();
		};

		await openOrder();

		await commerceAdminOrderDetailsPage.acceptOrderButton.click();

		await waitForAlert(page);

		await commerceAdminOrderDetailsPage.createShipmentButton.click();

		await expect(
			commerceAdminOrderDetailsPage.orderStatusProcessing
		).toBeVisible();

		await addShipmentItems(
			commerceAdminShipmentsPage,
			skus.slice(0, 3),
			warehouseName
		);

		await finishProcessingAndShip(commerceAdminShipmentsPage, page);

		await expectOrderStatus(14);

		await openOrder();

		await commerceAdminOrderDetailsPage.createShipmentButton.click();

		await addShipmentItems(
			commerceAdminShipmentsPage,
			skus.slice(3, 5),
			warehouseName
		);

		await finishProcessingAndShip(commerceAdminShipmentsPage, page);

		await expectOrderStatus(15);

		await openOrder();

		await commerceAdminOrderDetailsPage.completeOrderButton.click();

		await waitForAlert(page);

		await expectOrderStatus(0);
	}
);

test(
	'Search shipments by shipment ID',
	{tag: ['@COMMERCE-6256', '@LPD-93911']},
	async ({
		apiHelpers,
		commerceAdminOrderDetailsPage,
		commerceAdminOrdersPage,
		commerceAdminShipmentsPage,
		page,
	}) => {
		const pages = {
			commerceAdminOrderDetailsPage,
			commerceAdminOrdersPage,
			commerceAdminShipmentsPage,
			page,
		};

		const setup1 = await setUpShipmentOrder(apiHelpers, {
			catalog: miniumCatalog,
			channel: miniumChannel,
		});
		await acceptOrderAndCreateShipmentWithItem(pages, {
			orderId: setup1.order.id,
			sku: setup1.skus[0],
			warehouseName: setup1.warehouseName,
		});

		const setup2 = await setUpShipmentOrder(apiHelpers, {
			catalog: miniumCatalog,
			channel: miniumChannel,
		});
		await acceptOrderAndCreateShipmentWithItem(pages, {
			orderId: setup2.order.id,
			sku: setup2.skus[0],
			warehouseName: setup2.warehouseName,
		});

		const shipments =
			await apiHelpers.headlessCommerceAdminShipment.getShipments();

		const shipment1 = (shipments.items ?? []).find(
			(item: {accountId: number}) => item.accountId === setup1.account.id
		);
		const shipment2 = (shipments.items ?? []).find(
			(item: {accountId: number}) => item.accountId === setup2.account.id
		);

		expect(shipment1).toBeDefined();
		expect(shipment2).toBeDefined();

		await commerceAdminShipmentsPage.goTo();

		await commerceAdminShipmentsPage.search(shipment1.id);

		await expect(
			commerceAdminShipmentsPage.shipmentIdLink(String(shipment1.id))
		).toBeVisible();
		await expect(
			commerceAdminShipmentsPage.shipmentIdLink(String(shipment2.id))
		).toHaveCount(0);

		await commerceAdminShipmentsPage.search(shipment2.id);

		await expect(
			commerceAdminShipmentsPage.shipmentIdLink(String(shipment2.id))
		).toBeVisible();
		await expect(
			commerceAdminShipmentsPage.shipmentIdLink(String(shipment1.id))
		).toHaveCount(0);
	}
);

test(
	'Operations Manager can view shipments',
	{tag: ['@COMMERCE-8626', '@LPD-93911']},
	async ({
		apiHelpers,
		commerceAdminOrderDetailsPage,
		commerceAdminOrdersPage,
		commerceAdminShipmentsPage,
		page,
	}) => {
		const {account, order, skus, warehouseName} = await setUpShipmentOrder(
			apiHelpers,
			{
				catalog: miniumCatalog,
				channel: miniumChannel,
			}
		);

		await acceptOrderAndCreateShipmentWithItem(
			{
				commerceAdminOrderDetailsPage,
				commerceAdminOrdersPage,
				commerceAdminShipmentsPage,
				page,
			},
			{orderId: order.id, sku: skus[0], warehouseName}
		);

		const shipments =
			await apiHelpers.headlessCommerceAdminShipment.getShipments();

		const shipment = (shipments.items ?? []).find(
			(item: {accountId: number}) => item.accountId === account.id
		);

		expect(shipment).toBeDefined();

		const operationsManagerUser =
			await apiHelpers.headlessAdminUser.postUserAccount();

		userData[operationsManagerUser.alternateName] = {
			name: operationsManagerUser.givenName,
			password: 'test',
			surname: operationsManagerUser.familyName,
		};

		const operationsManagerRole =
			await apiHelpers.headlessAdminUser.getRoleByName(
				'Operations Manager'
			);

		await apiHelpers.headlessAdminUser.assignUserToRole(
			operationsManagerRole.externalReferenceCode,
			operationsManagerUser.id
		);

		await performUserSwitch(page, operationsManagerUser.alternateName);

		await commerceAdminShipmentsPage.goTo();

		await commerceAdminShipmentsPage.search(shipment.id);

		await expect(
			commerceAdminShipmentsPage.shipmentIdLink(String(shipment.id))
		).toBeVisible();
	}
);

test(
	'Shipment custom field value can be edited',
	{tag: ['@COMMERCE-9331', '@LPD-93911']},
	async ({
		addCustomFieldPage,
		apiHelpers,
		commerceAdminOrderDetailsPage,
		commerceAdminOrdersPage,
		commerceAdminShipmentsPage,
		page,
	}) => {
		const fieldName = `Shipment CF ${getRandomString().slice(0, 8)}`;

		try {
			await addCustomFieldPage.addCustomField({
				fieldName,
				fieldType: 'textArea',
				resource: 'Shipment',
			});

			const {account, order, skus, warehouseName} =
				await setUpShipmentOrder(apiHelpers, {
					catalog: miniumCatalog,
					channel: miniumChannel,
				});

			await acceptOrderAndCreateShipmentWithItem(
				{
					commerceAdminOrderDetailsPage,
					commerceAdminOrdersPage,
					commerceAdminShipmentsPage,
					page,
				},
				{orderId: order.id, sku: skus[0], warehouseName}
			);

			const editCustomField = async (value: string) => {
				await commerceAdminShipmentsPage.customFieldsTab.click();

				await commerceAdminShipmentsPage
					.customFieldInput(fieldName)
					.waitFor({state: 'visible', timeout: 15000});

				await expect(async () => {
					await commerceAdminShipmentsPage
						.customFieldInput(fieldName)
						.fill(value);

					await expect(
						commerceAdminShipmentsPage.customFieldInput(fieldName)
					).toHaveValue(value, {timeout: 2000});
				}).toPass({timeout: 15000});

				await commerceAdminShipmentsPage.customFieldsSaveButton.click();

				await waitForAlert(page);
			};

			await editCustomField('Test');

			await expect(async () => {
				const shipments =
					await apiHelpers.headlessCommerceAdminShipment.getShipments();

				const shipment = (shipments.items ?? []).find(
					(item: {accountId: number}) => item.accountId === account.id
				);

				expect(JSON.stringify(shipment?.customFields ?? [])).toContain(
					'Test'
				);
			}).toPass({timeout: 10000});

			const transitions: Array<[string, string]> = [
				['Finish Processing', 'Test 2'],
				['Ship', 'Test 3'],
				['Deliver', 'Test 4'],
			];

			for (const [status, value] of transitions) {
				await commerceAdminShipmentsPage
					.shipmentStatusLink(status)
					.click();

				await waitForAlert(page);

				await editCustomField(value);
			}
		}
		finally {
			await performLoginViaApi({page, screenName: 'test'});

			await new ViewAttributesPage(page).deleteCustomField(
				fieldName,
				'Shipment'
			);
		}
	}
);

test(
	'Deleting a Shipment custom field removes the Custom Fields tab',
	{tag: ['@COMMERCE-9331', '@LPD-93911']},
	async ({
		addCustomFieldPage,
		apiHelpers,
		commerceAdminOrderDetailsPage,
		commerceAdminOrdersPage,
		commerceAdminShipmentsPage,
		page,
	}) => {
		const fieldName = `Shipment CF ${getRandomString().slice(0, 8)}`;

		const viewAttributesPage = new ViewAttributesPage(page);

		await viewAttributesPage.goto('Shipment');

		await viewAttributesPage.addCustomFieldButton.waitFor();

		if (await viewAttributesPage.selectAllItemsCheckbox.isVisible()) {
			await viewAttributesPage.deleteCustomFields('Shipment');
		}

		await addCustomFieldPage.addCustomField({
			fieldName,
			fieldType: 'textArea',
			resource: 'Shipment',
		});

		const {account, order, skus, warehouseName} = await setUpShipmentOrder(
			apiHelpers,
			{
				catalog: miniumCatalog,
				channel: miniumChannel,
			}
		);

		await acceptOrderAndCreateShipmentWithItem(
			{
				commerceAdminOrderDetailsPage,
				commerceAdminOrdersPage,
				commerceAdminShipmentsPage,
				page,
			},
			{orderId: order.id, sku: skus[0], warehouseName}
		);

		const shipments =
			await apiHelpers.headlessCommerceAdminShipment.getShipments();

		const shipment = (shipments.items ?? []).find(
			(item: {accountId: number}) => item.accountId === account.id
		);

		expect(shipment).toBeDefined();

		await expect(commerceAdminShipmentsPage.customFieldsTab).toBeVisible();

		await viewAttributesPage.deleteCustomField(fieldName, 'Shipment');

		await commerceAdminShipmentsPage.goTo();

		await commerceAdminShipmentsPage.search(shipment.id);

		await commerceAdminShipmentsPage
			.shipmentIdLink(String(shipment.id))
			.click();

		await expect(commerceAdminShipmentsPage.customFieldsTab).toHaveCount(0);
	}
);

test(
	'A shipment combining items from orders with different shipping methods shows an info message',
	{tag: ['@COMMERCE-9917', '@LPD-93911']},
	async ({
		apiHelpers,
		commerceAdminChannelDetailsPage,
		commerceAdminChannelsPage,
		commerceAdminOrderDetailsPage,
		commerceAdminOrdersPage,
		commerceAdminShipmentsPage,
		page,
	}) => {
		const randomSuffix = getRandomString().slice(0, 8);
		const variableRateOption = `variable-option-${randomSuffix}`;

		const skus: Array<{id: number; sku: string}> = [];

		for (let i = 0; i < 2; i++) {
			const productStub =
				await apiHelpers.headlessCommerceAdminCatalog.postProduct({
					catalogId: miniumCatalog.id,
					name: {
						en_US: `Shipping Method Product ${randomSuffix}-${i}`,
					},
				});

			const product =
				await apiHelpers.headlessCommerceAdminCatalog.getProduct(
					productStub.productId
				);

			skus.push(product.skus[0]);
		}

		const warehouse =
			await apiHelpers.headlessCommerceAdminInventoryApiHelper.postWarehouses(
				{
					active: true,
					latitude: 10,
					longitude: 0,
					name: {en_US: `Test Warehouse ${randomSuffix}`},
					warehouseItems: skus.map((sku) => ({
						quantity: 100,
						sku: sku.sku,
					})),
				}
			);

		await apiHelpers.headlessCommerceAdminInventoryApiHelper.postWarehousesChannels(
			warehouse.id,
			miniumChannel.id
		);

		const account = await apiHelpers.headlessAdminUser.postAccount({
			name: `Commerce Account ${randomSuffix}`,
			type: 'business',
		});

		const address =
			await apiHelpers.headlessCommerceAdminAccount.postAddress(
				account.id,
				{
					city: 'Test City',
					countryISOCode: 'US',
					defaultBilling: true,
					defaultShipping: true,
					name: 'Test Address',
					regionISOCode: 'CA',
					street1: 'Test Street',
					zip: '12345',
				}
			);

		await commerceAdminChannelsPage.goto();

		await (
			await commerceAdminChannelsPage.channelsTableRowLink(
				miniumChannel.name
			)
		).click();

		await commerceAdminChannelDetailsPage.activateChannelConfiguration(
			'Variable Rate',
			'Shipping Methods'
		);
		await commerceAdminChannelDetailsPage.addVariableRateShippingOption(
			variableRateOption
		);
		await commerceAdminChannelDetailsPage.addVariableRateShippingOptionSetting(
			variableRateOption,
			undefined,
			'100',
			'100'
		);

		const order1 = await apiHelpers.headlessCommerceAdminOrder.postOrder({
			accountId: account.id,
			billingAddressId: address.id,
			channelId: miniumChannel.id,
			orderItems: [{quantity: 1, skuId: skus[0].id}],
			orderStatus: '10',
			paymentMethod: 'money-order',
			paymentStatus: '2',
			shippingAddressId: address.id,
			shippingMethod: 'fixed',
			shippingOption: 'standard-option',
		} as any);

		await apiHelpers.headlessCommerceAdminOrder.postOrder({
			accountId: account.id,
			billingAddressId: address.id,
			channelId: miniumChannel.id,
			orderItems: [{quantity: 1, skuId: skus[1].id}],
			orderStatus: '10',
			paymentMethod: 'money-order',
			paymentStatus: '2',
			shippingAddressId: address.id,
			shippingMethod: 'by-weight',
			shippingOption: variableRateOption,
		} as any);

		await commerceAdminOrdersPage.goto();

		await commerceAdminOrdersPage.tableRowOrderIdLink(order1.id).click();

		await expect(
			commerceAdminOrderDetailsPage.headerDetailsTitle
		).toBeVisible();

		await commerceAdminOrderDetailsPage.createShipmentButton.click();
		await commerceAdminShipmentsPage.addProductsToShipment.click();
		await (
			await commerceAdminShipmentsPage.shipmentItemsTableRowAction(
				1,
				skus[0].sku
			)
		).check();
		await (
			await commerceAdminShipmentsPage.shipmentItemsTableRowAction(
				1,
				skus[1].sku
			)
		).check();
		await commerceAdminShipmentsPage.shipmentsItemSubmitButton.click();

		await waitForAlert(
			page,
			'There are shipment items from orders with different shipping methods selected.',
			{autoClose: false, type: 'info'}
		);
	}
);

test(
	'Shipment Base Tracking URL is inherited from the shipping method',
	{tag: ['@COMMERCE-9459', '@LPD-93911']},
	async ({
		apiHelpers,
		checkoutPage,
		commerceAdminChannelDetailsPage,
		commerceAdminChannelsPage,
		commerceAdminOrderDetailsPage,
		commerceAdminOrdersPage,
		commerceAdminShipmentsPage,
		commerceMiniCartPage,
		page,
	}) => {
		const trackingURL = 'www.methodcarriersite.com/';

		const {account, buyerUser} = await createAccountWithBuyerUser(
			apiHelpers,
			site.id
		);

		try {
			await commerceAdminChannelsPage.goto();

			await (
				await commerceAdminChannelsPage.channelsTableRowLink(
					miniumChannel.name
				)
			).click();

			await commerceAdminChannelDetailsPage.setShippingMethodTrackingURL(
				'Flat Rate',
				trackingURL,
				'Shipping Methods'
			);

			await performUserSwitch(page, buyerUser.alternateName);

			await page.goto(`/web${site.friendlyUrlPath}/catalog`);

			await commerceMiniCartPage.quickAddToCart('MIN55861');
			await commerceMiniCartPage.submitButton.click();

			await checkoutPage.addAddress({
				city: 'Test City',
				countryLabel: 'United States',
				name: 'Address Name',
				regionLabel: 'Florida',
				street: 'Test Street',
				zip: '12345',
			});

			await checkoutPage.continueButton.click();
			await checkoutPage.shippingMethodRadio('Standard').check();
			await checkoutPage.continueButton.click();
			await checkoutPage.continueButton.click();

			await expect(checkoutPage.orderSuccessMessage).toBeVisible();

			await performLoginViaApi({page, screenName: 'test'});

			const orders =
				await apiHelpers.headlessCommerceAdminOrder.getOrdersPage();

			const order = (orders.items ?? []).find(
				(item: {accountId: number}) => item.accountId === account.id
			);

			expect(order).toBeDefined();

			await acceptOrderAndCreateShipmentWithItem(
				{
					commerceAdminOrderDetailsPage,
					commerceAdminOrdersPage,
					commerceAdminShipmentsPage,
					page,
				},
				{
					orderId: order.id,
					sku: {sku: 'MIN55861'},
					warehouseName: 'Italy',
				}
			);

			await commerceAdminShipmentsPage.carrierDetailsEditLink.click();
			await commerceAdminShipmentsPage.carrierDetailsShippingMethodSelect.selectOption(
				{label: 'Flat Rate'}
			);
			await commerceAdminShipmentsPage.carrierDetailsSubmitButton.click();

			await expect(
				commerceAdminShipmentsPage.baseTrackingURLValue
			).toHaveText(trackingURL);
		}
		finally {
			await performLoginViaApi({page, screenName: 'test'});

			const orders =
				await apiHelpers.headlessCommerceAdminOrder.getOrdersPage();

			for (const order of orders.items ?? []) {
				if (order.accountId === account.id) {
					apiHelpers.data.push({id: order.id, type: 'order'});
				}
			}

			const shipments =
				await apiHelpers.headlessCommerceAdminShipment.getShipments();

			for (const shipment of shipments.items ?? []) {
				if (shipment.accountId === account.id) {
					apiHelpers.data.push({id: shipment.id, type: 'shipment'});
				}
			}

			await commerceAdminChannelsPage.goto();

			await (
				await commerceAdminChannelsPage.channelsTableRowLink(
					miniumChannel.name
				)
			).click();

			await commerceAdminChannelDetailsPage.setShippingMethodTrackingURL(
				'Flat Rate',
				'',
				'Shipping Methods'
			);
		}
	}
);
