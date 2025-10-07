/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {apiHelpersTest} from '../../../../fixtures/apiHelpersTest';
import {applicationsMenuPageTest} from '../../../../fixtures/applicationsMenuPageTest';
import {commercePagesTest} from '../../../../fixtures/commercePagesTest';
import {dataApiHelpersTest} from '../../../../fixtures/dataApiHelpersTest';
import {loginTest} from '../../../../fixtures/loginTest';
import {clickAndExpectToBeVisible} from '../../../../utils/clickAndExpectToBeVisible';
import {getRandomInt} from '../../../../utils/getRandomInt';
import getRandomString from '../../../../utils/getRandomString';
import {waitForAlert} from '../../../../utils/waitForAlert';

export const test = mergeTests(
	apiHelpersTest,
	applicationsMenuPageTest,
	commercePagesTest,
	dataApiHelpersTest,
	loginTest()
);

test(
	'Orders can be bulk deleted on admin orders page',
	{tag: '@LPD-55981'},
	async ({apiHelpers, commerceAdminOrdersPage, page}) => {
		const site = await apiHelpers.headlessSite.createSite({
			name: getRandomString(),
		});

		apiHelpers.data.push({id: site.id, type: 'site'});

		const channel =
			await apiHelpers.headlessCommerceAdminChannel.postChannel({
				siteGroupId: site.id,
			});

		const catalog =
			await apiHelpers.headlessCommerceAdminCatalog.postCatalog();

		const product1 =
			await apiHelpers.headlessCommerceAdminCatalog.postProduct({
				catalogId: catalog.id,
				name: {en_US: 'Product1'},
			});

		const productSkus1 = await apiHelpers.headlessCommerceAdminCatalog
			.getProduct(product1.productId)
			.then((product) => {
				return product.skus;
			});

		const sku1 = productSkus1[0];

		const product2 =
			await apiHelpers.headlessCommerceAdminCatalog.postProduct({
				catalogId: catalog.id,
				name: {en_US: 'Product2'},
			});

		const productSkus2 = await apiHelpers.headlessCommerceAdminCatalog
			.getProduct(product2.productId)
			.then((product) => {
				return product.skus;
			});

		const sku2 = productSkus2[0];

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
							sku: sku1.sku,
						},
						{
							quantity: 1,
							sku: sku2.sku,
						},
					],
				}
			);

		await apiHelpers.headlessCommerceAdminInventoryApiHelper.postWarehousesChannels(
			warehouse.id,
			channel.id
		);

		const order1 = await apiHelpers.headlessCommerceAdminOrder.postOrder({
			accountId: account.id,
			billingAddressId: address.id,
			channelId: channel.id,
			orderItems: [
				{
					quantity: 1,
					skuId: sku1.id,
				},
				{
					quantity: 1,
					skuId: sku2.id,
				},
			],
			orderStatus: '1',
			paymentMethod: 'money-order',
			paymentStatus: '0',
			shippingAddressId: address.id,
			shippingMethod: 'by-weight',
			shippingOption: 'standard-option',
		});

		const order2 = await apiHelpers.headlessCommerceAdminOrder.postOrder({
			accountId: account.id,
			billingAddressId: address.id,
			channelId: channel.id,
			orderItems: [
				{
					quantity: 1,
					skuId: sku1.id,
				},
				{
					quantity: 1,
					skuId: sku2.id,
				},
			],
			orderStatus: '1',
			paymentMethod: 'money-order',
			paymentStatus: '0',
			shippingAddressId: address.id,
			shippingMethod: 'by-weight',
			shippingOption: 'standard-option',
		});

		const order3 = await apiHelpers.headlessCommerceAdminOrder.postOrder({
			accountId: account.id,
			billingAddressId: address.id,
			channelId: channel.id,
			orderItems: [
				{
					quantity: 1,
					skuId: sku1.id,
				},
				{
					quantity: 1,
					skuId: sku2.id,
				},
			],
			orderStatus: '1',
			paymentMethod: 'money-order',
			paymentStatus: '0',
			shippingAddressId: address.id,
			shippingMethod: 'by-weight',
			shippingOption: 'standard-option',
		});

		const order4 = await apiHelpers.headlessCommerceAdminOrder.postOrder({
			accountId: account.id,
			billingAddressId: address.id,
			channelId: channel.id,
			orderItems: [
				{
					quantity: 1,
					skuId: sku1.id,
				},
				{
					quantity: 1,
					skuId: sku2.id,
				},
			],
			orderStatus: '1',
			paymentMethod: 'money-order',
			paymentStatus: '0',
			shippingAddressId: address.id,
			shippingMethod: 'by-weight',
			shippingOption: 'standard-option',
		});

		const order5 = await apiHelpers.headlessCommerceAdminOrder.postOrder({
			accountId: account.id,
			billingAddressId: address.id,
			channelId: channel.id,
			orderItems: [
				{
					quantity: 1,
					skuId: sku1.id,
				},
				{
					quantity: 1,
					skuId: sku2.id,
				},
			],
			orderStatus: '1',
			paymentMethod: 'money-order',
			paymentStatus: '0',
			shippingAddressId: address.id,
			shippingMethod: 'by-weight',
			shippingOption: 'standard-option',
		});

		await commerceAdminOrdersPage.goto();

		await expect(
			await commerceAdminOrdersPage.tableRowLink({
				colIndex: 1,
				rowValue: order1.id,
			})
		).toBeVisible();

		await expect(
			await commerceAdminOrdersPage.tableRowLink({
				colIndex: 1,
				rowValue: order2.id,
			})
		).toBeVisible();

		await expect(
			await commerceAdminOrdersPage.tableRowLink({
				colIndex: 1,
				rowValue: order3.id,
			})
		).toBeVisible();

		await expect(
			await commerceAdminOrdersPage.tableRowLink({
				colIndex: 1,
				rowValue: order4.id,
			})
		).toBeVisible();

		await expect(
			await commerceAdminOrdersPage.tableRowLink({
				colIndex: 1,
				rowValue: order5.id,
			})
		).toBeVisible();

		await (await commerceAdminOrdersPage.managementBarCheckbox).check();

		await expect(
			await commerceAdminOrdersPage.managementBarCheckbox
		).toBeChecked();

		await (
			await commerceAdminOrdersPage.managementBarActionsButton
		).click();

		await clickAndExpectToBeVisible({
			autoClick: false,
			target: commerceAdminOrdersPage.managementBarActionsButton,
			trigger: commerceAdminOrdersPage.managementBarDeleteMenuItem,
		});

		page.once('dialog', (dialog) => {
			dialog.accept();
		});

		await (
			await commerceAdminOrdersPage.managementBarDeleteMenuItem
		).click();

		await waitForAlert(page);

		await expect(await commerceAdminOrdersPage.table).toBeHidden();
	}
);
