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
import {CommerceAdminChannelDetailsPage} from '../../../../pages/commerce/commerce-channel-web/commerceAdminChannelDetailsPage';
import {CommerceAdminChannelsPage} from '../../../../pages/commerce/commerce-channel-web/commerceAdminChannelsPage';
import {OfflinePaymentMethodsSystemSettingPage} from '../../../../pages/commerce/commerce-payment-web/offlinePaymentMethodsSystemSettingPage';
import getRandomString from '../../../../utils/getRandomString';
import {
	performLoginViaApi,
	performUserSwitch,
} from '../../../../utils/performLogin';
import {
	configureOperationsManagerUserForSite,
	createAccountWithBuyerUser,
	miniumSetUp,
} from '../../utils/commerce';

export const test = mergeTests(
	accountsPagesTest,
	commercePagesTest,
	dataApiHelpersTest,
	loginTest()
);

let channel: {id: number; name: string};
let setupData: Array<{id: number | string; type: string}>;
let site: Site;

const activatedPaymentMethods: string[] = [];
const createdPaymentMethodKeys: string[] = [];

let subscriptionProductId: number;

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

	if (subscriptionProductId) {
		await apiHelpers.headlessCommerceAdminCatalog.patchProductSubscriptionConfiguration(
			subscriptionProductId,
			{enable: false}
		);

		subscriptionProductId = undefined;
	}

	if (activatedPaymentMethods.length) {
		const commerceAdminChannelDetailsPage =
			new CommerceAdminChannelDetailsPage(page);
		const commerceAdminChannelsPage = new CommerceAdminChannelsPage(page);

		await commerceAdminChannelsPage.goto();

		await (
			await commerceAdminChannelsPage.channelsTableRowLink(channel.name)
		).click();

		for (const paymentMethod of activatedPaymentMethods) {
			await commerceAdminChannelDetailsPage.deactivateChannelConfiguration(
				paymentMethod,
				'Payment Methods'
			);
		}

		activatedPaymentMethods.length = 0;
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

test(
	'Users without the manage payment methods permission cannot edit the payment method of an order',
	{tag: ['@LPD-104219']},
	async ({
		apiHelpers,
		commerceAdminChannelDetailsPage,
		commerceAdminChannelsPage,
		commerceAdminOrderDetailsPage,
		commerceAdminOrdersPage,
		page,
	}) => {
		const account = await apiHelpers.headlessAdminUser.postAccount({
			name: 'Commerce Account ' + getRandomString(),
			type: 'business',
		});

		const address =
			await apiHelpers.headlessCommerceAdminAccount.postAddress(
				account.id,
				{
					city: 'Test City',
					countryISOCode: 'US',
					defaultBilling: true,
					defaultShipping: true,
					name: 'Test Address',
					regionISOCode: 'CA',
					street1: 'Test Street',
					zip: '12345',
				}
			);

		await commerceAdminChannelsPage.goto();

		await (
			await commerceAdminChannelsPage.channelsTableRowLink(channel.name)
		).click();

		await commerceAdminChannelDetailsPage.activatePaymentMethod(
			'Money Order',
			'Money Order'
		);
		await commerceAdminChannelDetailsPage.activateChannelConfiguration(
			'PayPal',
			'Payment Methods'
		);

		const product =
			await apiHelpers.headlessCommerceAdminCatalog.getProductByName(
				'U-Joint'
			);

		const order = await apiHelpers.headlessCommerceAdminOrder.postOrder({
			accountId: account.id,
			billingAddressId: address.id,
			channelId: channel.id,
			orderItems: [{quantity: 1, skuId: String(product.skus[0].id)}],
			orderStatus: '1',
			paymentMethod: 'money-order',
			paymentStatus: '2',
			shippingAddressId: address.id,
		});

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

		await expect(
			commerceAdminOrderDetailsPage.paymentMethodName
		).toContainText('Money Order');
		await expect(
			await commerceAdminOrderDetailsPage.editEntryActionLink(
				'Payment Method',
				'Edit'
			)
		).toBeVisible();

		const companyId = await page.evaluate(() => {
			return Liferay.ThemeDisplay.getCompanyId();
		});

		const operationsManagerUser =
			await configureOperationsManagerUserForSite(
				account,
				apiHelpers,
				companyId,
				site,
				[]
			);

		await performUserSwitch(page, operationsManagerUser.alternateName);

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

		await expect(
			commerceAdminOrderDetailsPage.paymentMethodName
		).toContainText('Money Order');
		await expect(
			await commerceAdminOrderDetailsPage.editEntryActionLink(
				'Payment Method',
				'Edit'
			)
		).toHaveCount(0);
		await expect(
			await commerceAdminOrderDetailsPage.editEntryActionLink(
				'Payment Status',
				'Edit'
			)
		).toBeVisible();
	}
);

test(
	'PayPal Subscriptions is offered only to orders with a subscription product',
	{tag: ['@COMMERCE-12920', '@LPD-106360']},
	async ({
		apiHelpers,
		checkoutPage,
		commerceAdminChannelDetailsPage,
		commerceAdminChannelsPage,
		commerceAdminOrderDetailsPage,
		commerceAdminOrdersPage,
		orderDetailsPage,
		page,
		pendingOrdersPage,
	}) => {
		test.setTimeout(180000);

		await commerceAdminChannelsPage.goto();

		await (
			await commerceAdminChannelsPage.channelsTableRowLink(channel.name)
		).click();

		for (const paymentMethod of [
			'Money Order',
			'PayPal',
			'PayPal Subscriptions',
		]) {
			await commerceAdminChannelDetailsPage.activateChannelConfiguration(
				paymentMethod,
				'Payment Methods'
			);

			activatedPaymentMethods.push(paymentMethod);
		}

		const subscriptionProduct =
			await apiHelpers.headlessCommerceAdminCatalog.getProductByName(
				'ABS Sensor'
			);

		await apiHelpers.headlessCommerceAdminCatalog.patchProductSubscriptionConfiguration(
			subscriptionProduct.productId,
			{
				enable: true,
				length: 2,
				numberOfLength: 0,
				subscriptionType: 'monthly',
				subscriptionTypeSettings: {monthDay: 1, monthlyMode: 0},
			}
		);

		const plainProduct =
			await apiHelpers.headlessCommerceAdminCatalog.getProductByName(
				'U-Joint'
			);

		const {account, buyerUser} = await createAccountWithBuyerUser(
			apiHelpers,
			site.id
		);

		await apiHelpers.headlessCommerceAdminAccount.postAddress(account.id, {
			city: 'Test City',
			countryISOCode: 'US',
			defaultBilling: true,
			defaultShipping: true,
			name: 'Test Address',
			regionISOCode: 'CA',
			street1: 'Test Street',
			zip: '12345',
		});

		const companyId = await page.evaluate(() => {
			return Liferay.ThemeDisplay.getCompanyId();
		});

		const operationsManagerUser =
			await configureOperationsManagerUserForSite(
				account,
				apiHelpers,
				companyId,
				site,
				[
					{
						actionIds: ['MANAGE_COMMERCE_ORDER_PAYMENT_METHODS'],
						primaryKey: companyId,
						resourceName: 'com.liferay.commerce.order',
						scope: 1,
					},
				]
			);

		subscriptionProductId = subscriptionProduct.productId;

		await performUserSwitch(page, buyerUser.alternateName);

		const goToPaymentStep = async (orderId: number) => {
			await pendingOrdersPage.gotoOrder(site.friendlyUrlPath, orderId);

			await orderDetailsPage.checkoutButton.click();

			await checkoutPage.continueButton.click();

			await page.waitForURL((url) =>
				url.href.includes('shipping-method')
			);

			await checkoutPage.shippingMethodRadio('Standard').check();

			await checkoutPage.continueButton.click();
		};

		const plainOrder =
			await apiHelpers.headlessCommerceDeliveryCart.postCart(
				{
					accountId: account.id,
					cartItems: [
						{
							options: '[]',
							quantity: 1,
							skuId: plainProduct.skus[0].id,
						},
					],
				},
				channel.id
			);

		await test.step('A non-subscription order cannot pay with PayPal Subscriptions', async () => {
			await goToPaymentStep(plainOrder.id);

			await page.waitForURL((url) => url.href.includes('payment-method'));

			await expect(
				checkoutPage.paymentMethodRadio('Money Order', true)
			).toBeVisible();
			await expect(
				checkoutPage.paymentMethodRadio('PayPal', true)
			).toBeVisible();
			await expect(
				checkoutPage.paymentMethodRadio('PayPal Subscriptions', true)
			).toHaveCount(0);
		});

		const subscriptionOrder =
			await apiHelpers.headlessCommerceDeliveryCart.postCart(
				{
					accountId: account.id,
					cartItems: [
						{
							options: '[]',
							quantity: 1,
							skuId: subscriptionProduct.skus[0].id,
						},
					],
				},
				channel.id
			);

		await test.step('A subscription order is placed on PayPal Subscriptions without a choice', async () => {
			await goToPaymentStep(subscriptionOrder.id);

			await page.waitForURL((url) => url.href.includes('order-summary'));

			await expect(checkoutPage.checkoutStepLabels).not.toContainText([
				'Payment Method',
			]);
			await expect(
				checkoutPage.paymentMethodRadio('Money Order', true)
			).toHaveCount(0);
			await expect(
				checkoutPage.paymentMethodRadio('PayPal', true)
			).toHaveCount(0);
			await expect(checkoutPage.orderSummaryPaymentMethod).toContainText(
				'PayPal Subscriptions'
			);
		});

		await performUserSwitch(page, operationsManagerUser.alternateName);

		for (const {eligible, notEligible, orderId} of [
			{
				eligible: ['Money Order', 'PayPal'],
				notEligible: ['PayPal Subscriptions'],
				orderId: plainOrder.id,
			},
			{
				eligible: ['PayPal Subscriptions'],
				notEligible: ['Money Order', 'PayPal'],
				orderId: subscriptionOrder.id,
			},
		]) {
			await test.step(`Order ${orderId} offers only its eligible payment methods in the orders admin`, async () => {
				await commerceAdminOrdersPage.goto();

				await (
					await commerceAdminOrdersPage.tableRowLink({
						colIndex: 1,
						rowValue: orderId,
					})
				).click();

				await (
					await commerceAdminOrderDetailsPage.orderDetailsTab(
						'Payments'
					)
				).click();

				await (
					await commerceAdminOrderDetailsPage.editEntryActionLink(
						'Payment Method',
						'Edit'
					)
				).click();

				for (const paymentMethod of eligible) {
					await expect(
						commerceAdminOrderDetailsPage.paymentMethodOption(
							paymentMethod
						)
					).toBeVisible();
				}

				for (const paymentMethod of notEligible) {
					await expect(
						commerceAdminOrderDetailsPage.paymentMethodOption(
							paymentMethod
						)
					).toHaveCount(0);
				}

				await (
					await commerceAdminOrderDetailsPage.paymentMethodRadioButton(
						eligible[0]
					)
				).click();

				await commerceAdminOrderDetailsPage.submitPaymentMethod.click();

				await expect(
					commerceAdminOrderDetailsPage.paymentMethodName
				).toContainText(eligible[0]);
			});
		}
	}
);
