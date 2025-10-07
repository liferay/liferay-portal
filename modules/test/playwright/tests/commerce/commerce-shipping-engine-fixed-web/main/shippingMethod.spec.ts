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
import {pageViewModePagesTest} from '../../../../fixtures/pageViewModePagesTest';
import {getRandomInt} from '../../../../utils/getRandomInt';
import getRandomString from '../../../../utils/getRandomString';
import {waitForAlert} from '../../../../utils/waitForAlert';

export const test = mergeTests(
	applicationsMenuPageTest,
	commercePagesTest,
	dataApiHelpersTest,
	isolatedSiteTest,
	loginTest(),
	pageViewModePagesTest
);

test('LPP-55641 Variable Shipping Rate is calculated based only on shippable products', async ({
	apiHelpers,
	checkoutPage,
	commerceAdminChannelDetailsPage,
	commerceAdminChannelsPage,
	page,
	site,
	widgetPagePage,
}) => {
	const layout = await apiHelpers.jsonWebServicesLayout.addLayout({
		groupId: site.id,
		title: getRandomString(),
	});

	const channel = await apiHelpers.headlessCommerceAdminChannel.postChannel({
		siteGroupId: site.id,
	});

	const catalog = await apiHelpers.headlessCommerceAdminCatalog.postCatalog({
		name: getRandomString(),
	});

	const product1 = await apiHelpers.headlessCommerceAdminCatalog.postProduct({
		catalogId: catalog.id,
		name: {en_US: 'Product1'},
		shippingConfiguration: {
			freeShipping: false,
			shippable: true,
		},
	});

	const product1Skus = await apiHelpers.headlessCommerceAdminCatalog
		.getProduct(product1.productId)
		.then((product) => {
			return product.skus;
		});

	const sku1 = product1Skus[0];

	const basePriceListId =
		await apiHelpers.headlessCommerceAdminPricing.getBasePriceListId(
			catalog.id
		);

	await apiHelpers.headlessCommerceAdminPricing.postPriceEntry({
		price: 15,
		priceListId: basePriceListId.items[0].id,
		skuId: sku1.id,
	});

	const product2 = await apiHelpers.headlessCommerceAdminCatalog.postProduct({
		catalogId: catalog.id,
		name: {en_US: 'Product2'},
		shippingConfiguration: {
			shippable: false,
		},
	});

	const product2Skus = await apiHelpers.headlessCommerceAdminCatalog
		.getProduct(product2.productId)
		.then((product) => {
			return product.skus;
		});

	const sku2 = product2Skus[0];

	await apiHelpers.headlessCommerceAdminPricing.postPriceEntry({
		price: 50,
		priceListId: basePriceListId.items[0].id,
		skuId: sku2.id,
	});

	const account = await apiHelpers.headlessAdminUser.postAccount({
		name: getRandomString(),
		type: 'business',
	});

	await apiHelpers.headlessAdminUser.assignUserToAccountByEmailAddress(
		account.id,
		['test@liferay.com']
	);

	await apiHelpers.headlessCommerceDeliveryCart.postCart(
		{
			accountId: account.id,
			cartItems: [
				{
					options: '[]',
					quantity: 1,
					replacedSkuId: 0,
					skuId: sku1.id,
				},
				{
					options: '[]',
					quantity: 1,
					replacedSkuId: 0,
					skuId: sku2.id,
				},
			],
		},
		channel.id
	);

	await commerceAdminChannelsPage.changeCommerceChannelSiteType(
		channel.name,
		'B2B'
	);

	await commerceAdminChannelDetailsPage.activateChannelConfiguration(
		'Variable Rate',
		'Shipping Methods'
	);
	await commerceAdminChannelDetailsPage.addVariableRateShippingOption(
		'variable rate'
	);
	await commerceAdminChannelDetailsPage.addVariableRateShippingOptionSetting(
		'variable rate',
		'10'
	);

	await page.goto(`/web${site.friendlyUrlPath}${layout.friendlyURL}`);

	await widgetPagePage.addPortlet('Checkout');

	await checkoutPage.addressInput.fill('123 Main St');
	await checkoutPage.cityInput.fill('Miami');
	await checkoutPage.countryInput.selectOption({label: 'United States'});
	await checkoutPage.nameInput.fill('John Doe');
	await checkoutPage.phoneNumberInput.fill('1234567890');
	await checkoutPage.regionInput.selectOption({label: 'Florida'});
	await checkoutPage.zipInput.fill('33101');

	await checkoutPage.continueButton.click();
	await expect(checkoutPage.shippingCost).toContainText('$ 1.50');
});

test(
	'Shipment tracking URL for an order can be updated and viewed',
	{tag: ['@COMMERCE-9459', '@LPD-56179']},
	async ({
		apiHelpers,
		commerceAdminChannelDetailsPage,
		commerceAdminChannelsPage,
		commerceAdminOrdersPage,
		commerceAdminShipmentsPage,
		page,
		site,
	}) => {
		const channel =
			await apiHelpers.headlessCommerceAdminChannel.postChannel({
				siteGroupId: site.id,
			});

		const catalog =
			await apiHelpers.headlessCommerceAdminCatalog.postCatalog({
				name: getRandomString(),
			});

		const product =
			await apiHelpers.headlessCommerceAdminCatalog.postProduct({
				catalogId: catalog.id,
				name: {en_US: 'Product1'},
				shippingConfiguration: {
					freeShipping: false,
					shippable: true,
				},
			});

		const productSkus = await apiHelpers.headlessCommerceAdminCatalog
			.getProduct(product.productId)
			.then((product) => {
				return product.skus;
			});

		const sku = productSkus[0];

		const basePriceListId =
			await apiHelpers.headlessCommerceAdminPricing.getBasePriceListId(
				catalog.id
			);

		await apiHelpers.headlessCommerceAdminPricing.postPriceEntry({
			price: 15,
			priceListId: basePriceListId.items[0].id,
			skuId: sku.id,
		});

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

		await commerceAdminChannelsPage.changeCommerceChannelSiteType(
			channel.name,
			'B2B'
		);

		await commerceAdminChannelDetailsPage.activateChannelConfiguration(
			'Flat Rate',
			'Shipping Methods'
		);
		await (
			await commerceAdminChannelDetailsPage.generalCommerceAdminChannelTableLink(
				'Flat Rate'
			)
		).click();

		await (
			await commerceAdminChannelDetailsPage.sidePanelFrameInput(
				'Tracking URL',
				'Shipping Methods'
			)
		).fill('www.flatratecarriersite.com/');
		await (
			await commerceAdminChannelDetailsPage.frameSaveButton(
				false,
				'Shipping Methods'
			)
		).click();
		await waitForAlert(
			await commerceAdminChannelDetailsPage.sidePanelFrame(
				'Shipping Methods'
			)
		);
		await (
			await commerceAdminChannelDetailsPage.closeSidePanelFrame(
				false,
				'Shipping Methods'
			)
		).click();

		await commerceAdminChannelDetailsPage.activateChannelConfiguration(
			'Variable Rate',
			'Shipping Methods'
		);
		await (
			await commerceAdminChannelDetailsPage.generalCommerceAdminChannelTableLink(
				'Variable Rate'
			)
		).click();
		await (
			await commerceAdminChannelDetailsPage.sidePanelFrameInput(
				'Tracking URL',
				'Shipping Methods'
			)
		).fill('www.variableratecarriersite.com/');
		await (
			await commerceAdminChannelDetailsPage.frameSaveButton(
				false,
				'Shipping Methods'
			)
		).click();
		await waitForAlert(
			await commerceAdminChannelDetailsPage.sidePanelFrame(
				'Shipping Methods'
			)
		);
		await (
			await commerceAdminChannelDetailsPage.closeSidePanelFrame(
				false,
				'Shipping Methods'
			)
		).click();
		await commerceAdminChannelDetailsPage.addVariableRateShippingOption(
			'variable rate'
		);
		await commerceAdminChannelDetailsPage.addVariableRateShippingOptionSetting(
			'variable rate',
			'10'
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
			paymentStatus: '0',
			shippingAddressId: address.id,
			shippingMethod: 'fixed',
		});

		await commerceAdminOrdersPage.goto();

		await expect(
			await commerceAdminOrdersPage.tableRowLink({
				colIndex: 1,
				rowValue: order.id,
			})
		).toBeVisible();
		await (
			await commerceAdminOrdersPage.tableRowLink({
				colIndex: 1,
				rowValue: order.id,
			})
		).click();
		await commerceAdminOrdersPage.orderStatusLink('Accept Order').click();
		await commerceAdminOrdersPage
			.orderStatusLink('Create Shipment')
			.click();

		await expect(
			await page.getByText('www.flatratecarriersite.com/')
		).toBeVisible();
		await expect(
			await commerceAdminShipmentsPage.emptyTableMessage
		).toBeVisible();

		await commerceAdminShipmentsPage.carrierDetailsEditLink.click({
			trial: true,
		});
		await commerceAdminShipmentsPage.carrierDetailsEditLink.click();

		await page.waitForLoadState('networkidle');

		await commerceAdminShipmentsPage.shippingMethodSelect.selectOption(
			'Variable Rate'
		);
		await commerceAdminShipmentsPage.carrierDetailsSubmitButton.click();

		await expect(
			await page.getByText('www.variableratecarriersite.com/')
		).toBeVisible();
	}
);
