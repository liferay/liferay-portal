/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {accountsPagesTest} from '../../../../fixtures/accountsPagesTest';
import {commercePagesTest} from '../../../../fixtures/commercePagesTest';
import {dataApiHelpersTest} from '../../../../fixtures/dataApiHelpersTest';
import {loginTest} from '../../../../fixtures/loginTest';
import {DataApiHelpers} from '../../../../helpers/ApiHelpers';
import {OfflinePaymentMethodsSystemSettingPage} from '../../../../pages/commerce/commerce-payment-web/offlinePaymentMethodsSystemSettingPage';
import getRandomString from '../../../../utils/getRandomString';
import {
	performLoginViaApi,
	performUserSwitch,
} from '../../../../utils/performLogin';
import {createAccountWithBuyerUser, miniumSetUp} from '../../utils/commerce';

export const test = mergeTests(
	accountsPagesTest,
	commercePagesTest,
	dataApiHelpersTest,
	loginTest()
);

let channel: {id: number; name: string};
let setupData: Array<{id: number | string; type: string}>;
let site: Site;

const createdPaymentMethodKeys: string[] = [];

test.beforeAll(async ({browser}) => {
	const page = await browser.newPage();

	await performLoginViaApi({page, screenName: 'test'});

	const apiHelpers = new DataApiHelpers(page);

	const miniumResult = await miniumSetUp(apiHelpers);

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

	if (createdPaymentMethodKeys.length) {
		const offlinePaymentMethodsSystemSettingPage =
			new OfflinePaymentMethodsSystemSettingPage(page);

		await offlinePaymentMethodsSystemSettingPage.goto();

		for (const key of createdPaymentMethodKeys) {
			try {
				await offlinePaymentMethodsSystemSettingPage.deleteKey(key);
			}
			catch {
				continue;
			}
		}

		createdPaymentMethodKeys.length = 0;
	}

	await page.close();
});

test(
	'Can add, edit, and delete an offline payment method',
	{tag: ['@LPD-85008']},
	async ({offlinePaymentMethodsSystemSettingPage}) => {
		const key = `Test Offline Payment Method ${getRandomString()}`;
		const editedKey = `${key} Edited`;

		createdPaymentMethodKeys.push(key, editedKey);

		await offlinePaymentMethodsSystemSettingPage.goto();

		await offlinePaymentMethodsSystemSettingPage.addKey(key);

		await expect(
			offlinePaymentMethodsSystemSettingPage.configurationLink(key)
		).toBeVisible();

		await offlinePaymentMethodsSystemSettingPage.editKey(key, editedKey);

		await expect(
			offlinePaymentMethodsSystemSettingPage.configurationLink(editedKey)
		).toBeVisible();

		await offlinePaymentMethodsSystemSettingPage.deleteKey(editedKey);

		await expect(
			offlinePaymentMethodsSystemSettingPage.configurationLink(editedKey)
		).not.toBeVisible();
	}
);

test(
	'Can activate an offline payment method on a channel and see its description',
	{tag: ['@LPD-85008']},
	async ({
		apiHelpers,
		commerceAdminChannelDetailsPage,
		commerceAdminChannelsPage,
		offlinePaymentMethodsSystemSettingPage,
		page,
	}) => {
		const paymentMethodKey = `Test Offline Payment Method ${getRandomString()}`;

		createdPaymentMethodKeys.push(paymentMethodKey);

		const testChannelName = `Test Channel ${getRandomString()}`;

		const testChannel =
			await apiHelpers.headlessCommerceAdminChannel.postChannel({
				currencyCode: 'USD',
				name: testChannelName,
				siteGroupId: site.id,
				type: 'site',
			});

		await offlinePaymentMethodsSystemSettingPage.goto();

		await offlinePaymentMethodsSystemSettingPage.addKey(paymentMethodKey);

		await commerceAdminChannelsPage.goto();

		await (
			await commerceAdminChannelsPage.channelsTableRowLink(
				testChannel.name
			)
		).click();

		await commerceAdminChannelDetailsPage.activatePaymentMethod(
			paymentMethodKey,
			`Pay with ${paymentMethodKey}.`
		);

		await expect(
			page.getByText(`Pay with ${paymentMethodKey}.`).first()
		).toBeVisible();
		await expect(page.getByText('Active').first()).toBeVisible();
	}
);

test(
	'Can see a payment integration description before and after activating it',
	{tag: ['@LPD-102685']},
	async ({
		apiHelpers,
		commerceAdminChannelDetailsPage,
		commerceAdminChannelsPage,
		page,
	}) => {
		const testChannel =
			await apiHelpers.headlessCommerceAdminChannel.postChannel({
				currencyCode: 'USD',
				name: `Test Channel ${getRandomString()}`,
				siteGroupId: site.id,
				type: 'site',
			});

		await commerceAdminChannelsPage.goto();

		await (
			await commerceAdminChannelsPage.channelsTableRowLink(
				testChannel.name
			)
		).click();

		const paymentMethodRow = page.getByRole('row').filter({
			has: page.getByRole('link', {exact: true, name: 'PayPal'}),
		});

		await test.step('See the description before the integration is configured', async () => {
			await expect(
				paymentMethodRow.getByText('Pay via PayPal.')
			).toBeVisible();
		});

		await test.step('See the description after activating without one', async () => {
			await commerceAdminChannelDetailsPage.activateChannelConfiguration(
				'PayPal',
				'Payment Methods'
			);

			await expect(
				paymentMethodRow.getByText('Pay via PayPal.')
			).toBeVisible();
		});
	}
);

test(
	'Offline payment method can be used to place an order',
	{tag: ['@LPD-85008']},
	async ({
		apiHelpers,
		checkoutPage,
		commerceAdminChannelDetailsPage,
		commerceAdminChannelsPage,
		commerceMiniCartPage,
		offlinePaymentMethodsSystemSettingPage,
		page,
	}) => {
		const paymentMethodKey = `Test Offline Payment Method ${getRandomString()}`;

		createdPaymentMethodKeys.push(paymentMethodKey);

		await offlinePaymentMethodsSystemSettingPage.goto();

		await offlinePaymentMethodsSystemSettingPage.addKey(paymentMethodKey);

		await commerceAdminChannelsPage.goto();

		await (
			await commerceAdminChannelsPage.channelsTableRowLink(channel.name)
		).click();
		await commerceAdminChannelDetailsPage.activateChannelConfiguration(
			paymentMethodKey,
			'Payment Methods'
		);

		const {buyerUser} = await createAccountWithBuyerUser(
			apiHelpers,
			site.id
		);

		await performUserSwitch(page, buyerUser.alternateName);

		await page.goto(`/web/${site.name}/catalog`);

		await commerceMiniCartPage.quickAddToCart('MIN55861');
		await commerceMiniCartPage.submitButton.click();

		await checkoutPage.addAddress({
			city: 'Test City',
			countryLabel: 'United States',
			name: 'Test Name',
			regionLabel: 'Florida',
			street: 'Test Street',
			zip: '12345',
		});
		await checkoutPage.continueButton.click();

		await page.waitForURL((url) => url.href.includes('shipping-method'));

		await checkoutPage.continueButton.click();

		await page.waitForURL((url) => url.href.includes('order-summary'));

		await expect(page.getByText(paymentMethodKey).first()).toBeVisible();
	}
);

test(
	'Can view and change the payment method of an order from the orders admin',
	{tag: ['@LPD-85008']},
	async ({
		apiHelpers,
		checkoutPage,
		commerceAdminChannelDetailsPage,
		commerceAdminChannelsPage,
		commerceAdminOrderDetailsPage,
		commerceAdminOrdersPage,
		commerceMiniCartPage,
		offlinePaymentMethodsSystemSettingPage,
		page,
	}) => {
		const paymentMethodKey1 = `Test Offline Payment Method 1 ${getRandomString()}`;
		const paymentMethodKey2 = `Test Offline Payment Method 2 ${getRandomString()}`;

		createdPaymentMethodKeys.push(paymentMethodKey1, paymentMethodKey2);

		await offlinePaymentMethodsSystemSettingPage.goto();

		await offlinePaymentMethodsSystemSettingPage.addKey(paymentMethodKey1);
		await offlinePaymentMethodsSystemSettingPage.addKey(paymentMethodKey2);

		await commerceAdminChannelsPage.goto();

		await (
			await commerceAdminChannelsPage.channelsTableRowLink(channel.name)
		).click();
		await commerceAdminChannelDetailsPage.activateChannelConfiguration(
			paymentMethodKey1,
			'Payment Methods'
		);
		await commerceAdminChannelDetailsPage.activateChannelConfiguration(
			paymentMethodKey2,
			'Payment Methods'
		);

		const {buyerUser} = await createAccountWithBuyerUser(
			apiHelpers,
			site.id
		);

		await performUserSwitch(page, buyerUser.alternateName);

		await page.goto(`/web/${site.name}/catalog`);

		await commerceMiniCartPage.quickAddToCart('MIN55861');
		await commerceMiniCartPage.submitButton.click();

		await checkoutPage.addAddress({
			city: 'Test City',
			countryLabel: 'United States',
			name: 'Test Name',
			regionLabel: 'Florida',
			street: 'Test Street',
			zip: '12345',
		});
		await checkoutPage.continueButton.click();

		await page.waitForURL((url) => url.href.includes('shipping-method'));

		await checkoutPage.continueButton.click();

		await page.waitForURL((url) => url.href.includes('payment-method'));

		await checkoutPage.paymentMethodRadio(paymentMethodKey1).check();
		await checkoutPage.continueButton.click();

		await page.waitForURL((url) => url.href.includes('order-summary'));

		await checkoutPage.continueButton.click();

		await expect(checkoutPage.goToOrderDetailsButton).toBeVisible();

		await performUserSwitch(page, 'test');

		const ordersResponse =
			await apiHelpers.headlessCommerceAdminOrder.getOrdersPage();
		const order = ordersResponse.items[0];

		await commerceAdminOrdersPage.goto();

		await (
			await commerceAdminOrdersPage.tableRowLink({
				colIndex: 1,
				rowValue: order.id,
			})
		).click();

		await (
			await commerceAdminOrderDetailsPage.orderDetailsTab('Payments')
		).click();

		await (
			await commerceAdminOrderDetailsPage.editEntryActionLink(
				'Payment Method',
				'Edit'
			)
		).click();
		await (
			await commerceAdminOrderDetailsPage.paymentMethodRadioButton(
				paymentMethodKey2
			)
		).click();
		await commerceAdminOrderDetailsPage.submitPaymentMethod.click();

		await expect(
			commerceAdminOrderDetailsPage.page
				.getByText(paymentMethodKey2)
				.first()
		).toBeVisible();
	}
);

test(
	'Multiple active payment methods are selectable at checkout',
	{tag: ['@LPD-85008']},
	async ({
		apiHelpers,
		checkoutPage,
		commerceAdminChannelDetailsPage,
		commerceAdminChannelsPage,
		commerceMiniCartPage,
		offlinePaymentMethodsSystemSettingPage,
		page,
	}) => {
		const paymentMethodKey = `Test Offline Payment Method ${getRandomString()}`;

		createdPaymentMethodKeys.push(paymentMethodKey);

		await offlinePaymentMethodsSystemSettingPage.goto();

		await offlinePaymentMethodsSystemSettingPage.addKey(paymentMethodKey);

		await commerceAdminChannelsPage.goto();

		await (
			await commerceAdminChannelsPage.channelsTableRowLink(channel.name)
		).click();
		await commerceAdminChannelDetailsPage.activateChannelConfiguration(
			paymentMethodKey,
			'Payment Methods'
		);
		await commerceAdminChannelDetailsPage.activateChannelConfiguration(
			'PayPal',
			'Payment Methods'
		);

		const {buyerUser} = await createAccountWithBuyerUser(
			apiHelpers,
			site.id
		);

		await performUserSwitch(page, buyerUser.alternateName);

		await page.goto(`/web/${site.name}/catalog`);

		await commerceMiniCartPage.quickAddToCart('MIN55861');
		await commerceMiniCartPage.submitButton.click();

		await checkoutPage.addAddress({
			city: 'Test City',
			countryLabel: 'United States',
			name: 'Test Name',
			regionLabel: 'Florida',
			street: 'Test Street',
			zip: '12345',
		});
		await checkoutPage.continueButton.click();

		await page.waitForURL((url) => url.href.includes('shipping-method'));

		await checkoutPage.continueButton.click();

		await page.waitForURL((url) => url.href.includes('payment-method'));

		await expect(
			checkoutPage.paymentMethodRadio(paymentMethodKey)
		).toBeVisible();
		await expect(checkoutPage.paymentMethodRadio('PayPal')).toBeVisible();
	}
);
