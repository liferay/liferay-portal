/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {commercePagesTest} from '../../../../fixtures/commercePagesTest';
import {dataApiHelpersTest} from '../../../../fixtures/dataApiHelpersTest';
import {featureFlagsTest} from '../../../../fixtures/featureFlagsTest';
import {loginTest} from '../../../../fixtures/loginTest';
import {liferayConfig} from '../../../../liferay.config';
import getRandomString from '../../../../utils/getRandomString';
import {
	performLoginViaApi,
	performLogout,
} from '../../../../utils/performLogin';
import {ORDER_WORKFLOW_STATUS_CODE} from '../../../workspaces/liferay-workspace-marketplace/main/utils/constants';
import {
	classicCommerceSetUp,
	createAccountWithBuyerUser,
} from '../../utils/commerce';

export const test = mergeTests(
	commercePagesTest,
	dataApiHelpersTest,
	featureFlagsTest({
		'LPD-10562': {enabled: true},
		'LPS-178052': {enabled: true},
	}),
	loginTest()
);

test(
	'View shipments in the placed order details page',
	{tag: ['@COMMERCE-6382', '@LPD-95980']},
	async ({apiHelpers, commerceThemeClassicOrdersPage, page}) => {
		test.setTimeout(180000);

		const {channel, site} = await classicCommerceSetUp(apiHelpers);

		const {account, buyerUser} = await createAccountWithBuyerUser(
			apiHelpers,
			site.id
		);

		const address =
			await apiHelpers.headlessCommerceAdminAccount.postAddress(
				account.id,
				{phoneNumber: '323262', regionISOCode: 'AL'}
			);

		const product =
			await apiHelpers.headlessCommerceAdminCatalog.getProducts(
				new URLSearchParams({filter: `name eq 'U-Joint'`})
			);

		const sku = await apiHelpers.headlessCommerceAdminCatalog
			.getProduct(product.items[0].productId)
			.then((product) => product.skus[0]);

		const warehouse =
			await apiHelpers.headlessCommerceAdminInventoryApiHelper.postWarehouses(
				{
					active: true,
					latitude: 10,
					longitude: 0,
					warehouseItems: [{quantity: 100, sku: sku.sku}],
				}
			);

		await apiHelpers.headlessCommerceAdminInventoryApiHelper.postWarehousesChannels(
			warehouse.id,
			channel.id
		);

		await performLogout(page);
		await performLoginViaApi({page, screenName: buyerUser.alternateName});

		const postCart = await apiHelpers.headlessCommerceDeliveryCart.postCart(
			{
				accountId: account.id,
				cartItems: [{quantity: 1, replacedSkuId: 0, skuId: sku.id}],
			},
			channel.id
		);

		const checkoutCart =
			await apiHelpers.headlessCommerceDeliveryCart.checkoutCart(
				postCart.id
			);

		await performLogout(page);
		await performLoginViaApi({page, screenName: 'test'});

		await apiHelpers.headlessCommerceAdminOrder.patchOrder(postCart.id, {
			orderStatus: ORDER_WORKFLOW_STATUS_CODE.PROCESSING,
		});

		const carrier = 'Test Carrier';
		const trackingNumber = getRandomString();

		const shipment =
			await apiHelpers.headlessCommerceAdminShipment.postShipment({
				carrier,
				orderId: checkoutCart.id,
				shipmentItems: [
					{
						orderItemId: postCart.cartItems[0].id,
						quantity: 1,
						warehouseId: warehouse.id,
					},
				],
				shippingAddressId: address.id,
				trackingNumber,
			});

		await apiHelpers.headlessCommerceAdminShipment.postShipmentStatusDelivered(
			shipment.id
		);

		await apiHelpers.headlessCommerceAdminOrder.patchOrder(postCart.id, {
			orderStatus: ORDER_WORKFLOW_STATUS_CODE.COMPLETED,
		});

		await performLogout(page);
		await performLoginViaApi({page, screenName: buyerUser.alternateName});

		await page.goto(
			liferayConfig.environment.baseUrl +
				`/web/${site.name}/order/${checkoutCart.id}`
		);

		await (
			await commerceThemeClassicOrdersPage.orderItemsTableRow(2, sku.sku)
		).row
			.getByRole('button', {name: 'Actions'})
			.click();

		await expect(
			commerceThemeClassicOrdersPage.orderTableMenuItem('Shipments')
		).toBeVisible();

		await commerceThemeClassicOrdersPage
			.orderTableMenuItem('Shipments')
			.click();

		await expect(
			commerceThemeClassicOrdersPage.orderItemShipmentsIframe.getByText(
				'Delivered'
			)
		).toBeVisible();
		await expect(
			commerceThemeClassicOrdersPage.orderItemShipmentsIframe.getByText(
				carrier
			)
		).toBeVisible();
		await expect(
			commerceThemeClassicOrdersPage.orderItemShipmentsIframe.getByText(
				trackingNumber
			)
		).toBeVisible();

		await page.keyboard.press('Escape');

		await commerceThemeClassicOrdersPage.orderTabs('Shipments').click();

		await expect(page.getByText(shipment.id.toString())).toBeVisible();
	}
);
