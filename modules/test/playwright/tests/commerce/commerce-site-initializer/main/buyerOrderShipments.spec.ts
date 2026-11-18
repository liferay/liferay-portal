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
import {getRandomInt} from '../../../../utils/getRandomInt';
import getRandomString from '../../../../utils/getRandomString';
import {
	performLoginViaApi,
	performLogout,
} from '../../../../utils/performLogin';
import getPageDefinition from '../../../layout-content-page-editor-web/main/utils/getPageDefinition';
import getWidgetDefinition from '../../../layout-content-page-editor-web/main/utils/getWidgetDefinition';
import {ORDER_WORKFLOW_STATUS_CODE} from '../../../workspaces/liferay-workspace-marketplace/main/utils/constants';
import {
	classicCommerceSetUp,
	createAccountWithBuyerUser,
} from '../../utils/commerce';

export const test = mergeTests(
	commercePagesTest,
	dataApiHelpersTest,
	featureFlagsTest({
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

test(
	'A shipping address city containing markup is escaped in the Shipments widget',
	{tag: '@LPD-116132'},
	async ({apiHelpers, commerceShipmentContentPage, page}) => {
		const {channel, site} = await classicCommerceSetUp(apiHelpers);

		const account = await apiHelpers.headlessAdminUser.postAccount({
			type: 'business',
		});

		const city = `<img src=x onerror="alert('x')">A${getRandomInt()}`;

		const address =
			await apiHelpers.headlessCommerceAdminAccount.postAddress(
				account.id,
				{city, regionISOCode: 'AL'}
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

		const order = await apiHelpers.headlessCommerceAdminOrder.postOrder({
			accountId: account.id,
			billingAddressId: address.id,
			channelId: channel.id,
			orderItems: [{quantity: 1, skuId: sku.id}],
			orderStatus: ORDER_WORKFLOW_STATUS_CODE.PROCESSING,
			shippingAddressId: address.id,
		});

		const shipment =
			await apiHelpers.headlessCommerceAdminShipment.postShipment({
				orderId: order.id,
				shipmentItems: [
					{
						orderItemId: order.orderItems[0].id,
						quantity: 1,
						warehouseId: warehouse.id,
					},
				],
				shippingAddressId: address.id,
			});

		const portletId =
			'com_liferay_commerce_shipment_content_web_internal_portlet_CommerceShipmentContentPortlet';

		const layout = await apiHelpers.headlessDelivery.createSitePage({
			pageDefinition: getPageDefinition([
				getWidgetDefinition({
					id: getRandomString(),
					widgetName: portletId,
				}),
			]),
			siteId: site.id,
			title: getRandomString(),
		});

		page.on('dialog', async (dialog) => {
			if (dialog.type() === 'alert') {
				throw new Error('XSS');
			}
		});

		const urlSearchParams = new URLSearchParams({
			[`_${portletId}_commerceShipmentId`]: String(shipment.id),
			[`_${portletId}_mvcRenderCommandName`]:
				'/commerce_shipment_content/view_commerce_shipment_items',
			p_p_id: portletId,
			p_p_lifecycle: '0',
		});

		await page.goto(
			`${liferayConfig.environment.baseUrl}/web${site.friendlyUrlPath}${layout.friendlyUrlPath}?${urlSearchParams}`
		);

		await expect(commerceShipmentContentPage.shipmentAddress).toContainText(
			city
		);
		await expect(
			commerceShipmentContentPage.shipmentAddressImage
		).toHaveCount(0);
	}
);
