/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {commercePagesTest} from '../../../../fixtures/commercePagesTest';
import {dataApiHelpersTest} from '../../../../fixtures/dataApiHelpersTest';
import {featureFlagsTest} from '../../../../fixtures/featureFlagsTest';
import {globalMenuPagesTest} from '../../../../fixtures/globalMenuPagesTest';
import {loginTest} from '../../../../fixtures/loginTest';
import {DataApiHelpers} from '../../../../helpers/ApiHelpers';
import {getRandomInt} from '../../../../utils/getRandomInt';
import {
	performLoginViaApi,
	performLogout,
} from '../../../../utils/performLogin';
import {
	activateChannelPaymentMethod,
	createAccountWithBuyerUser,
	miniumSetUp,
} from '../../utils/commerce';

export const test = mergeTests(
	commercePagesTest,
	dataApiHelpersTest,
	featureFlagsTest({
		'LPS-178052': {enabled: true},
	}),
	globalMenuPagesTest,
	loginTest()
);

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
		'money-order'
	);

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

	const ordersPage =
		await apiHelpers.headlessCommerceAdminOrder.getOrdersPage();

	await Promise.all(
		(ordersPage.items ?? []).map((order) =>
			apiHelpers.headlessCommerceAdminOrder.deleteOrder(order.id)
		)
	);

	await page.close();
});

test(
	'Shipment delivered status is reflected on the storefront placed-order detail page',
	{tag: ['@COMMERCE-11444']},
	async ({apiHelpers, page, placedOrderPage, placedOrdersPage}) => {
		const {account, buyerUser} = await createAccountWithBuyerUser(
			apiHelpers,
			site.id
		);

		const sku =
			await apiHelpers.headlessCommerceAdminCatalog.getSkuByName(
				'MIN55861'
			);

		const warehouse =
			await apiHelpers.headlessCommerceAdminInventoryApiHelper.postWarehouses(
				{
					active: true,
					latitude: getRandomInt(),
					longitude: getRandomInt(),
					warehouseItems: [{quantity: 10, sku: sku.sku}],
				}
			);

		await apiHelpers.headlessCommerceAdminInventoryApiHelper.postWarehousesChannels(
			warehouse.id,
			channel.id
		);

		const address =
			await apiHelpers.headlessCommerceAdminAccount.postAddress(
				account.id,
				{regionISOCode: 'LA'}
			);

		const order = await apiHelpers.headlessCommerceAdminOrder.postOrder({
			accountId: account.id,
			billingAddressId: address.id,
			channelId: channel.id,
			orderItems: [{quantity: 1, skuId: sku.id}],
			orderStatus: '10',
			paymentMethod: 'money-order',
			paymentStatus: '2',
			shippingAddressId: address.id,
			shippingMethod: 'by-weight',
			shippingOption: 'standard-option',
		});

		const orderItem = order.orderItems[0];

		const shipment =
			await apiHelpers.headlessCommerceAdminShipment.postShipment({
				orderId: order.id,
				shipmentItems: [
					{
						orderItemId: orderItem.id,
						quantity: orderItem.quantity,
						warehouseId: warehouse.id,
					},
				],
			});

		await apiHelpers.headlessCommerceAdminShipment.postShipmentStatusDelivered(
			shipment.id
		);

		await test.step('Verify the delivered shipment is reflected on the storefront placed-order detail page', async () => {
			await performLogout(page);
			await performLoginViaApi({
				page,
				screenName: buyerUser.alternateName,
			});

			await page.goto(`/web/${site.name}/placed-orders`, {
				waitUntil: 'networkidle',
			});

			await placedOrdersPage.viewButton.first().click();

			await expect(
				placedOrderPage.shipmentStatusText('Completed').first()
			).toBeVisible();
		});
	}
);
