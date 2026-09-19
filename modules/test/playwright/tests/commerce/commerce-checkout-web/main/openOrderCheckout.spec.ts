/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {commercePagesTest} from '../../../../fixtures/commercePagesTest';
import {dataApiHelpersTest} from '../../../../fixtures/dataApiHelpersTest';
import {loginTest} from '../../../../fixtures/loginTest';
import {DataApiHelpers} from '../../../../helpers/ApiHelpers';
import {CheckoutPage} from '../../../../pages/commerce/commerce-checkout-web/checkoutPage';
import {
	performLoginViaApi,
	performUserSwitchViaApi,
} from '../../../../utils/performLogin';
import {
	createAccountWithBuyerUser,
	createBuyerUserForAccount,
	miniumSetUp,
	selectCurrentAccount,
} from '../../utils/commerce';

export const test = mergeTests(
	commercePagesTest,
	dataApiHelpersTest,
	loginTest()
);

let channel: {id: number; name: string};
let setupData: Array<{id: number | string; type: string}>;
let site: Site;
let skuId: number;

test.beforeAll(async ({browser}) => {
	const page = await browser.newPage();

	await performLoginViaApi({page, screenName: 'test'});

	const apiHelpers = new DataApiHelpers(page);

	const miniumResult = await miniumSetUp(apiHelpers);

	channel = miniumResult.channel;
	site = miniumResult.site;

	skuId = (
		await apiHelpers.headlessCommerceAdminCatalog.getProductByName(
			'U-Joint'
		)
	).skus[0].id;

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

async function checkoutOtherMembersOrder(
	{
		apiHelpers,
		checkoutPage,
		commerceMiniCartPage,
		commerceThemeMiniumCatalogPage,
		orderDetailsPage,
		page,
	},
	{
		checkoutAs = 'accountMember',
		entryPoint,
		orderHasItems,
	}: {
		checkoutAs?: 'accountMember' | 'admin';
		entryPoint: 'miniCart' | 'orderDetails';
		orderHasItems: boolean;
	}
) {
	const {account, buyerUser} = await createAccountWithBuyerUser(
		apiHelpers,
		site.id
	);

	const checkoutUserScreenName =
		checkoutAs === 'admin'
			? 'test'
			: (await createBuyerUserForAccount(account, apiHelpers, site.id))
					.alternateName;

	await performUserSwitchViaApi(page, buyerUser.alternateName);

	const cart = await apiHelpers.headlessCommerceDeliveryCart.postCart(
		{
			accountId: account.id,
			cartItems: orderHasItems
				? [{options: '[]', quantity: 1, skuId}]
				: [],
		},
		channel.id
	);

	await performUserSwitchViaApi(page, checkoutUserScreenName);

	if (checkoutAs === 'admin') {
		await selectCurrentAccount(account.id, apiHelpers, site.id);
	}

	await page.goto(`/web${site.friendlyUrlPath}`);

	await expect(
		commerceThemeMiniumCatalogPage.accountSelectorOrderId
	).toHaveText(String(cart.id));

	if (!orderHasItems) {
		await commerceThemeMiniumCatalogPage.addToCart('U-Joint');

		await expect(
			commerceThemeMiniumCatalogPage.accountSelectorOrderId
		).toHaveText(String(cart.id));
	}

	await commerceMiniCartPage.miniCartButton.click();

	await expect(commerceMiniCartPage.miniCartItem('U-Joint')).toBeVisible();

	if (entryPoint === 'miniCart') {
		await commerceMiniCartPage.submitButton.click();
	}
	else {
		await commerceMiniCartPage.viewDetailsButton.click();

		await orderDetailsPage.checkoutButton.click();
	}

	await completeCheckout(checkoutPage, checkoutUserScreenName, 1);
}

async function completeCheckout(
	checkoutPage: CheckoutPage,
	buyerName: string,
	quantity: number
) {
	const subtotal = 24 * quantity;

	await checkoutPage.addAddress({
		city: 'Test City',
		countryLabel: 'United States',
		name: buyerName,
		regionLabel: 'Florida',
		street: 'Test Address',
		zip: '12345',
	});

	await checkoutPage.continueButton.click();

	await checkoutPage.shippingMethodRadio('Standard').check();

	await checkoutPage.continueButton.click();

	await expect(checkoutPage.activeCheckoutStep).toHaveText('Order Summary');
	await expect(
		checkoutPage.orderSummaryItemCell('U-Joint', 'quantity')
	).toContainText(String(quantity));
	await expect(checkoutPage.orderSummaryItemListPrice('U-Joint')).toHaveText(
		formatPrice(24)
	);
	await expect(checkoutPage.orderSummarySubtotal).toHaveText(
		formatPrice(subtotal)
	);
	await expect(checkoutPage.orderSummaryDelivery).toHaveText(formatPrice(15));
	await expect(checkoutPage.orderSummaryTotal).toHaveText(
		formatPrice(subtotal + 15)
	);

	for (const address of [
		checkoutPage.commerceBillingAddress,
		checkoutPage.commerceShippingAddress,
	]) {
		for (const line of [
			buyerName,
			'Test Address',
			'Test City',
			'United States',
		]) {
			await expect(address).toContainText(line);
		}
	}

	await checkoutPage.continueButton.click();

	await expect(checkoutPage.orderSuccessMessage).toBeVisible();
}

function formatPrice(price: number) {
	return `$ ${price.toFixed(2)}`;
}

test(
	"A buyer can fill and check out another account member's empty order from the mini cart",
	{tag: ['@COMMERCE-9052', '@LPD-106360']},
	async ({
		apiHelpers,
		checkoutPage,
		commerceMiniCartPage,
		commerceThemeMiniumCatalogPage,
		orderDetailsPage,
		page,
	}) => {
		await checkoutOtherMembersOrder(
			{
				apiHelpers,
				checkoutPage,
				commerceMiniCartPage,
				commerceThemeMiniumCatalogPage,
				orderDetailsPage,
				page,
			},
			{entryPoint: 'miniCart', orderHasItems: false}
		);
	}
);

test(
	"A buyer can fill and check out another account member's empty order from the order details page",
	{tag: ['@COMMERCE-9052', '@LPD-106360']},
	async ({
		apiHelpers,
		checkoutPage,
		commerceMiniCartPage,
		commerceThemeMiniumCatalogPage,
		orderDetailsPage,
		page,
	}) => {
		await checkoutOtherMembersOrder(
			{
				apiHelpers,
				checkoutPage,
				commerceMiniCartPage,
				commerceThemeMiniumCatalogPage,
				orderDetailsPage,
				page,
			},
			{entryPoint: 'orderDetails', orderHasItems: false}
		);
	}
);

test(
	"A buyer can check out another account member's order from the mini cart",
	{tag: ['@COMMERCE-9052', '@LPD-106360']},
	async ({
		apiHelpers,
		checkoutPage,
		commerceMiniCartPage,
		commerceThemeMiniumCatalogPage,
		orderDetailsPage,
		page,
	}) => {
		await checkoutOtherMembersOrder(
			{
				apiHelpers,
				checkoutPage,
				commerceMiniCartPage,
				commerceThemeMiniumCatalogPage,
				orderDetailsPage,
				page,
			},
			{entryPoint: 'miniCart', orderHasItems: true}
		);
	}
);

test(
	"A buyer can check out another account member's order from the order details page",
	{tag: ['@COMMERCE-8143', '@COMMERCE-8350', '@LPD-106360']},
	async ({
		apiHelpers,
		checkoutPage,
		commerceMiniCartPage,
		commerceThemeMiniumCatalogPage,
		orderDetailsPage,
		page,
	}) => {
		await checkoutOtherMembersOrder(
			{
				apiHelpers,
				checkoutPage,
				commerceMiniCartPage,
				commerceThemeMiniumCatalogPage,
				orderDetailsPage,
				page,
			},
			{entryPoint: 'orderDetails', orderHasItems: true}
		);
	}
);

test(
	"An admin can check out a buyer's order after selecting the buyer's account",
	{tag: ['@COMMERCE-8143', '@COMMERCE-8350', '@LPD-106360']},
	async ({
		apiHelpers,
		checkoutPage,
		commerceMiniCartPage,
		commerceThemeMiniumCatalogPage,
		orderDetailsPage,
		page,
	}) => {
		await checkoutOtherMembersOrder(
			{
				apiHelpers,
				checkoutPage,
				commerceMiniCartPage,
				commerceThemeMiniumCatalogPage,
				orderDetailsPage,
				page,
			},
			{
				checkoutAs: 'admin',
				entryPoint: 'miniCart',
				orderHasItems: true,
			}
		);
	}
);

test(
	'A buyer can check out a new order after deleting the previous one',
	{tag: ['@COMMERCE-8127', '@COMMERCE-8168', '@LPD-106360']},
	async ({
		apiHelpers,
		checkoutPage,
		commerceMiniCartPage,
		commerceThemeMiniumCatalogPage,
		orderDetailsPage,
		page,
	}) => {
		const {account, buyerUser} = await createAccountWithBuyerUser(
			apiHelpers,
			site.id
		);

		await performUserSwitchViaApi(page, buyerUser.alternateName);

		const cart = await apiHelpers.headlessCommerceDeliveryCart.postCart(
			{
				accountId: account.id,
				cartItems: [{options: '[]', quantity: 1, skuId}],
			},
			channel.id
		);

		await page.goto(`/web${site.friendlyUrlPath}`);

		await test.step('Delete the order from its details page', async () => {
			await commerceMiniCartPage.miniCartButton.click();

			await commerceMiniCartPage.viewDetailsButton.click();

			await orderDetailsPage.deleteOrder();

			await page.goto(`/web${site.friendlyUrlPath}`);

			await expect(
				commerceThemeMiniumCatalogPage.accountSelectorOrderId
			).toBeHidden();
		});

		await test.step('Build and check out a new order', async () => {
			await commerceThemeMiniumCatalogPage.addToCart('U-Joint');

			await expect(
				commerceThemeMiniumCatalogPage.accountSelectorOrderId
			).not.toHaveText(String(cart.id));

			apiHelpers.data.push({
				id: Number(
					await commerceThemeMiniumCatalogPage.accountSelectorOrderId.innerText()
				),
				type: 'order',
			});

			await commerceMiniCartPage.miniCartButton.click();

			await commerceMiniCartPage.viewDetailsButton.click();

			await orderDetailsPage.checkoutButton.click();

			await completeCheckout(checkoutPage, buyerUser.alternateName, 1);
		});
	}
);
