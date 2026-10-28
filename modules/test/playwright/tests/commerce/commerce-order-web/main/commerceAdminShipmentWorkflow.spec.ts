/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {commercePagesTest} from '../../../../fixtures/commercePagesTest';
import {dataApiHelpersTest} from '../../../../fixtures/dataApiHelpersTest';
import {globalMenuPagesTest} from '../../../../fixtures/globalMenuPagesTest';
import {isolatedSiteTest} from '../../../../fixtures/isolatedSiteTest';
import {loginTest} from '../../../../fixtures/loginTest';
import {getRandomInt} from '../../../../utils/getRandomInt';
import getRandomString from '../../../../utils/getRandomString';
import {waitForAlert} from '../../../../utils/waitForAlert';
import {activateChannelPaymentMethod} from '../../utils/commerce';

export const test = mergeTests(
	commercePagesTest,
	dataApiHelpersTest,
	globalMenuPagesTest,
	isolatedSiteTest,
	loginTest()
);

test(
	'Execute the full shipment workflow from Accept Order to Delivered',
	{tag: ['@COMMERCE-6241', '@LPD-86571']},
	async ({
		apiHelpers,
		commerceAdminOrderDetailsPage,
		commerceAdminOrdersPage,
		commerceAdminShipmentsPage,
		page,
		site,
	}) => {
		const channel =
			await apiHelpers.headlessCommerceAdminChannel.postChannel({
				siteGroupId: site.id,
			});

		await activateChannelPaymentMethod(
			apiHelpers,
			channel.id,
			'money-order'
		);

		const catalog =
			await apiHelpers.headlessCommerceAdminCatalog.postCatalog();

		const product =
			await apiHelpers.headlessCommerceAdminCatalog.postProduct({
				catalogId: catalog.id,
				name: {en_US: getRandomString()},
			});

		const productSkus = await apiHelpers.headlessCommerceAdminCatalog
			.getProduct(product.productId)
			.then((product) => {
				return product.skus;
			});

		const sku = productSkus[0];

		const account = await apiHelpers.headlessAdminUser.postAccount({
			name: getRandomString(),
			type: 'business',
		});

		await apiHelpers.headlessAdminUser.assignUserToAccountByEmailAddress(
			account.id,
			['test@liferay.com']
		);

		const address =
			await apiHelpers.headlessCommerceAdminAccount.postAddress(
				account.id,
				{
					regionISOCode: 'LA',
				}
			);

		const warehouse =
			await apiHelpers.headlessCommerceAdminInventoryApiHelper.postWarehouses(
				{
					active: true,
					latitude: getRandomInt(),
					longitude: getRandomInt(),
					warehouseItems: [
						{
							quantity: 1,
							sku: sku.sku,
						},
					],
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
			orderItems: [
				{
					quantity: 1,
					skuId: sku.id,
				},
			],
			orderStatus: '1',
			paymentMethod: 'money-order',
			paymentStatus: '2',
			shippingAddressId: address.id,
			shippingMethod: 'by-weight',
			shippingOption: 'standard-option',
		});

		await test.step('Open the pending order', async () => {
			await commerceAdminOrdersPage.goto();

			await (
				await commerceAdminOrdersPage.tableRowLink({
					colIndex: 1,
					rowValue: order.id,
				})
			).click();

			await expect(
				commerceAdminOrderDetailsPage.headerDetailsTitle
			).toBeVisible();
		});

		await test.step('Accept the order and create a shipment', async () => {
			await commerceAdminOrderDetailsPage.acceptOrderButton.click();

			await waitForAlert(page);

			await commerceAdminOrderDetailsPage.createShipmentButton.click();

			await expect(
				commerceAdminOrderDetailsPage.orderStatusProcessing
			).toBeVisible();
		});

		await test.step('Add the product to the shipment with warehouse quantity', async () => {
			await commerceAdminShipmentsPage.addProductsToShipment.click();

			await (
				await commerceAdminShipmentsPage.shipmentItemsTableRowAction(
					1,
					sku.sku
				)
			).check();

			await commerceAdminShipmentsPage.shipmentsItemSubmitButton.click();
			await commerceAdminShipmentsPage.productsSkuLink(sku.sku).click();
			await commerceAdminShipmentsPage.addQuantityInShipment.fill('1');
			await commerceAdminShipmentsPage.editProductSaveButton.click();

			await waitForAlert(commerceAdminShipmentsPage.editProductFrame);

			await commerceAdminShipmentsPage.editProductCloseButton.click();
		});

		await test.step('Transition the shipment through Finish Processing, Ship and Deliver', async () => {
			await commerceAdminShipmentsPage
				.shipmentStatusLink('Finish Processing')
				.click();

			await waitForAlert(page);

			await commerceAdminShipmentsPage.shipmentStatusLink('Ship').click();

			await waitForAlert(page);

			await commerceAdminShipmentsPage
				.shipmentStatusLink('Deliver')
				.click();

			await waitForAlert(page);
		});

		await test.step('Verify that the order is completed', async () => {
			await commerceAdminOrdersPage.goto();

			await expect(
				(await commerceAdminOrdersPage.tableRow(7, 'Completed')).row
			).toBeVisible();
		});
	}
);
