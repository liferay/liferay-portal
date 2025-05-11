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
import getRandomString from '../../../../utils/getRandomString';

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
		name: getRandomString(),
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
