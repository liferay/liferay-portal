/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {accountsPagesTest} from '../../../fixtures/accountsPagesTest';
import {applicationsMenuPageTest} from '../../../fixtures/applicationsMenuPageTest';
import {commercePagesTest} from '../../../fixtures/commercePagesTest';
import {dataApiHelpersTest} from '../../../fixtures/dataApiHelpersTest';
import {loginTest} from '../../../fixtures/loginTest';
import {notificationPagesTest} from '../../../fixtures/notificationPagesTest';
import getRandomString from '../../../utils/getRandomString';
import {waitForAlert} from '../../../utils/waitForAlert';

export const test = mergeTests(
	applicationsMenuPageTest,
	accountsPagesTest,
	commercePagesTest,
	dataApiHelpersTest,
	loginTest(),
	notificationPagesTest
);

test('LPD-25860 Checkout widget configuration to display full addresses and phone number', async ({
	apiHelpers,
	applicationsMenuPage,
	checkoutPage,
	commerceLayoutsPage,
	page,
}) => {
	const site = await apiHelpers.headlessSite.createSite({
		name: getRandomString(),
	});

	apiHelpers.data.push({id: site.id, type: 'site'});

	const channel = await apiHelpers.headlessCommerceAdminChannel.postChannel({
		name: getRandomString(),
		siteGroupId: site.id,
	});

	const catalog = await apiHelpers.headlessCommerceAdminCatalog.postCatalog({
		name: getRandomString(),
	});

	const product = await apiHelpers.headlessCommerceAdminCatalog.postProduct({
		catalogId: catalog.id,
		name: {en_US: 'Product'},
	});

	const productSkus = await apiHelpers.headlessCommerceAdminCatalog
		.getProduct(product.productId)
		.then((product) => {
			return product.skus;
		});

	const sku = productSkus[0];

	const account = await apiHelpers.headlessAdminUser.postAccount({
		name: getRandomString(),
		type: 'person',
	});

	apiHelpers.data.push({id: account.id, type: 'account'});

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
					skuId: sku.id,
				},
			],
		},
		channel.id
	);

	await applicationsMenuPage.goToSite(site.name);
	await commerceLayoutsPage.goToPages(false);
	await commerceLayoutsPage.createWidgetPage(getRandomString());

	await page.goto(`/web/${site.name}`);

	await checkoutPage.addCheckoutWidget();

	const phoneNumber = '1234567890';
	const region = 'Florida';
	const zipCode = '33101';

	await checkoutPage.addressInput.fill('123 Main St');
	await checkoutPage.cityInput.fill('Miami');
	await checkoutPage.countryInput.selectOption({label: 'United States'});
	await checkoutPage.nameInput.fill('John Doe');
	await checkoutPage.phoneNumberInput.fill(phoneNumber);
	await checkoutPage.regionInput.selectOption({label: region});
	await checkoutPage.zipInput.fill(zipCode);

	await checkoutPage.continueButton.click();

	await expect(checkoutPage.commerceBillingAddress).not.toContainText(
		phoneNumber
	);
	await expect(checkoutPage.commerceBillingAddress).not.toContainText(region);
	await expect(checkoutPage.commerceBillingAddress).not.toContainText(
		zipCode
	);
	await expect(checkoutPage.commerceShippingAddress).not.toContainText(
		phoneNumber
	);
	await expect(checkoutPage.commerceShippingAddress).not.toContainText(
		region
	);
	await expect(checkoutPage.commerceShippingAddress).not.toContainText(
		zipCode
	);

	await checkoutPage.optionsButton.click();
	await checkoutPage.configurationMenuItem.click();

	await checkoutPage.configurationIFrameShowFullAddressToggle.check();
	await checkoutPage.configurationIFrameShowPhoneNumberToggle.check();
	await checkoutPage.configurationIFrameSaveButton.click();
	await waitForAlert(checkoutPage.configurationIFrame);

	await page.reload();

	await expect(checkoutPage.commerceBillingAddress).toContainText(
		phoneNumber
	);
	await expect(checkoutPage.commerceBillingAddress).toContainText(region);
	await expect(checkoutPage.commerceBillingAddress).toContainText(zipCode);
	await expect(checkoutPage.commerceShippingAddress).toContainText(
		phoneNumber
	);
	await expect(checkoutPage.commerceShippingAddress).toContainText(region);
	await expect(checkoutPage.commerceShippingAddress).toContainText(zipCode);
});

test('LPP-55128 Payment Term is reset correctly', async ({
	accountsPage,
	apiHelpers,
	applicationsMenuPage,
	checkoutPage,
	commerceAdminChannelDetailsPage,
	commerceAdminChannelsPage,
	commerceLayoutsPage,
	editAccountChannelDefaultsPage,
	editAccountPage,
	page,
}) => {
	const site = await apiHelpers.headlessSite.createSite({
		name: getRandomString(),
	});

	apiHelpers.data.push({id: site.id, type: 'site'});

	const channel = await apiHelpers.headlessCommerceAdminChannel.postChannel({
		name: getRandomString(),
		siteGroupId: site.id,
	});

	const paymentTerm1 = await apiHelpers.headlessCommerceAdminOrder.postTerms({
		label: {
			en_US: 'MoneyA',
		},
		name: 'moneya',
		priority: 0,
		type: 'payment-terms',
	});
	const paymentTerm2 = await apiHelpers.headlessCommerceAdminOrder.postTerms({
		label: {
			en_US: 'MoneyB',
		},
		name: 'moneyb',
		priority: 1,
		type: 'payment-terms',
	});
	const paymentTerm3 = await apiHelpers.headlessCommerceAdminOrder.postTerms({
		label: {
			en_US: 'PayPalA',
		},
		name: 'paypala',
		priority: 2,
		type: 'payment-terms',
	});
	const paymentTerm4 = await apiHelpers.headlessCommerceAdminOrder.postTerms({
		label: {
			en_US: 'MoneyC',
		},
		name: 'moneyc',
		priority: 3,
		type: 'payment-terms',
	});

	const catalog = await apiHelpers.headlessCommerceAdminCatalog.postCatalog({
		name: getRandomString(),
	});

	const product = await apiHelpers.headlessCommerceAdminCatalog.postProduct({
		catalogId: catalog.id,
		name: {en_US: 'Product'},
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
		price: 100,
		priceListId: basePriceListId.items[0].id,
		skuId: sku.id,
	});

	const account = await apiHelpers.headlessAdminUser.postAccount({
		name: getRandomString(),
		type: 'business',
	});

	apiHelpers.data.push({id: account.id, type: 'account'});

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
					skuId: sku.id,
				},
			],
		},
		channel.id
	);

	await accountsPage.goto();
	await (await accountsPage.accountsTableRowLink(account.name)).click();
	await editAccountPage.channelDefaultsLink.click();
	await editAccountChannelDefaultsPage.addDefaultPaymentTerm(paymentTerm2.id);

	await commerceAdminChannelsPage.changeCommerceChannelSiteType(
		channel.name,
		'B2B'
	);
	await commerceAdminChannelDetailsPage.activateChannelConfiguration(
		'Money Order',
		'Payment Methods'
	);
	await (
		await commerceAdminChannelDetailsPage.generalCommerceAdminChannelTableLink(
			'Money Order'
		)
	).click();
	await commerceAdminChannelDetailsPage.setEntryEligibility(
		'Specific Payment Terms',
		paymentTerm1.name,
		'Payment Methods'
	);
	await (
		await commerceAdminChannelDetailsPage.generalCommerceAdminChannelTableLink(
			'Money Order'
		)
	).click();
	await commerceAdminChannelDetailsPage.setEntryEligibility(
		'Specific Payment Terms',
		paymentTerm2.name,
		'Payment Methods'
	);
	await (
		await commerceAdminChannelDetailsPage.generalCommerceAdminChannelTableLink(
			'Money Order'
		)
	).click();
	await commerceAdminChannelDetailsPage.setEntryEligibility(
		'Specific Payment Terms',
		paymentTerm4.name,
		'Payment Methods'
	);

	await commerceAdminChannelDetailsPage.activateChannelConfiguration(
		'PayPal',
		'Payment Methods'
	);
	await (
		await commerceAdminChannelDetailsPage.generalCommerceAdminChannelTableLink(
			'PayPal'
		)
	).click();
	await commerceAdminChannelDetailsPage.setEntryEligibility(
		'Specific Payment Terms',
		paymentTerm3.name,
		'Payment Methods'
	);

	await applicationsMenuPage.goToSite(site.name);
	await commerceLayoutsPage.goToPages(false);
	await commerceLayoutsPage.createWidgetPage(getRandomString());

	await page.goto(`/web/${site.name}`);
	await checkoutPage.addCheckoutWidget();

	await checkoutPage.addressInput.fill('123 Main St');
	await checkoutPage.cityInput.fill('Miami');
	await checkoutPage.countryInput.selectOption({label: 'United States'});
	await checkoutPage.nameInput.fill('John Doe');
	await checkoutPage.phoneNumberInput.fill('1234567890');
	await checkoutPage.regionInput.selectOption({label: 'Florida'});
	await checkoutPage.zipInput.fill('33101');

	await checkoutPage.continueButton.click();

	expect(page.getByLabel('Money Order')).toBeChecked();
	await checkoutPage.continueButton.click();
	await page.waitForURL((url) => url.href.includes('payment-terms'));
	expect(page.getByLabel('MoneyB')).toBeChecked();
	await checkoutPage.continueButton.click();
	await page.waitForURL((url) => url.href.includes('order-summary'));
	await checkoutPage.previousButton.click();
	await page.waitForURL((url) => url.href.includes('payment-terms'));
	await checkoutPage.previousButton.click();
	await page.waitForURL((url) => url.href.includes('payment-method'));
	await page.getByLabel('PayPal').check();
	await checkoutPage.continueButton.click();
	await page.waitForURL((url) => url.href.includes('order-summary'));
	await checkoutPage.previousButton.click();
	await page.waitForURL((url) => url.href.includes('payment-method'));
	await page.getByLabel('Money Order').check();
	await checkoutPage.continueButton.click();
	await page.waitForURL((url) => url.href.includes('payment-terms'));
	expect(page.getByLabel('MoneyB')).toBeChecked();
});
