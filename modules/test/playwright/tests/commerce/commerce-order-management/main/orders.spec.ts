/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {commercePagesTest} from '../../../../fixtures/commercePagesTest';
import {dataApiHelpersTest} from '../../../../fixtures/dataApiHelpersTest';
import {loginTest} from '../../../../fixtures/loginTest';
import {DataApiHelpers} from '../../../../helpers/ApiHelpers';
import getRandomString from '../../../../utils/getRandomString';
import {
	performLoginViaApi,
	performLogout,
	performUserSwitch,
	userData,
} from '../../../../utils/performLogin';
import {waitForAlert} from '../../../../utils/waitForAlert';
import {miniumSetUp} from '../../utils/commerce';

export const test = mergeTests(
	commercePagesTest,
	dataApiHelpersTest,
	loginTest()
);

const CHECKOUT_PERMISSIONS_WITHOUT_VIEW_BILLING_ADDRESS = (
	companyId: string
) => [
	{
		actionIds: ['VIEW'],
		primaryKey: companyId,
		resourceName: 'com.liferay.commerce.model.CommerceOrderType',
		scope: 1,
	},
	{
		actionIds: [
			'ADD_COMMERCE_ORDER',
			'CHECKOUT_OPEN_COMMERCE_ORDERS',
			'MANAGE_COMMERCE_ORDER_DELIVERY_TERMS',
			'MANAGE_COMMERCE_ORDER_PAYMENT_METHODS',
			'MANAGE_COMMERCE_ORDER_PAYMENT_TERMS',
			'MANAGE_COMMERCE_ORDER_SHIPPING_OPTIONS',
			'VIEW_COMMERCE_ORDERS',
			'VIEW_OPEN_COMMERCE_ORDERS',
		],
		primaryKey: '0',
		resourceName: 'com.liferay.commerce.order',
		scope: 3,
	},
	{
		actionIds: ['MANAGE_ADDRESSES', 'VIEW_ADDRESSES'],
		primaryKey: '0',
		resourceName: 'com.liferay.account.model.AccountEntry',
		scope: 3,
	},
];

let channel: {id: number; name: string};
let setupData: Array<{id: number | string; type: string}>;
let site: Site;

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

	await page.close();
});

test(
	'Buyer without VIEW_BILLING_ADDRESS permission sees an error at checkout when no default billing address is set',
	{tag: ['@LPD-89343']},
	async ({apiHelpers, checkoutPage, commerceMiniCartPage, page}) => {
		const account = await apiHelpers.headlessAdminUser.postAccount({
			name: 'Commerce Account ' + getRandomString(),
			type: 'business',
		});

		const role = await apiHelpers.headlessAdminUser.postRole({
			name: 'Test Buyer ' + getRandomString(),
			rolePermissions: CHECKOUT_PERMISSIONS_WITHOUT_VIEW_BILLING_ADDRESS(
				await page.evaluate(() => {
					return Liferay.ThemeDisplay.getCompanyId();
				})
			),
		});

		const buyerUser = await apiHelpers.headlessAdminUser.postUserAccount();

		userData[buyerUser.alternateName] = {
			name: buyerUser.givenName,
			password: 'test',
			surname: buyerUser.familyName,
		};

		await apiHelpers.headlessAdminUser.assignUserToAccountByEmailAddress(
			account.id,
			[buyerUser.emailAddress]
		);

		const siteMemberRole =
			await apiHelpers.headlessAdminUser.getRoleByName('Site Member');

		await apiHelpers.headlessAdminUser.assignUserToSite(
			siteMemberRole.id,
			site.id,
			buyerUser.id
		);

		await apiHelpers.headlessAdminUser.postRoleByExternalReferenceCodeUserAccountAssociation(
			role.externalReferenceCode,
			buyerUser.id
		);

		await performUserSwitch(page, buyerUser.alternateName);

		await page.goto(`/web/${site.name}/catalog`);

		await commerceMiniCartPage.quickAddToCart('MIN55861');
		await commerceMiniCartPage.submitButton.click();

		await page.waitForURL((url) => url.href.includes('shipping-address'));

		await checkoutPage.cityInput.fill('Test City');
		await checkoutPage.countryInput.selectOption({label: 'United States'});
		await checkoutPage.nameInput.fill('Address Name');
		await checkoutPage.regionInput.selectOption({label: 'California'});
		await checkoutPage.addressInput.fill('Test Address');
		await checkoutPage.zipInput.fill('12345');

		await checkoutPage.continueButton.click();

		await checkoutPage.shippingMethodRadio('Standard').check();

		await checkoutPage.continueButton.click();

		await expect(checkoutPage.noDefaultBillingAddressError).toBeVisible();
	}
);

test(
	'User with VIEW_COMMERCE_ORDERS only cannot Add or Edit order fields on the order details page',
	{tag: ['@LPD-89343']},
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

		const companyId = await page.evaluate(() => {
			return Liferay.ThemeDisplay.getCompanyId();
		});

		const viewOrderRole = await apiHelpers.headlessAdminUser.postRole({
			name: 'View Order Role ' + getRandomString(),
			rolePermissions: [
				{
					actionIds: ['VIEW_COMMERCE_ORDERS'],
					primaryKey: companyId,
					resourceName: 'com.liferay.commerce.order',
					scope: 1,
				},
				{
					actionIds: ['VIEW'],
					primaryKey: companyId,
					resourceName:
						'com.liferay.commerce.payment.model.CommercePaymentMethodGroupRel',
					scope: 1,
				},
				{
					actionIds: ['VIEW'],
					primaryKey: companyId,
					resourceName: 'com.liferay.commerce.model.CommerceOrder',
					scope: 1,
				},
				{
					actionIds: ['VIEW'],
					primaryKey: companyId,
					resourceName:
						'com.liferay.commerce.model.CommerceOrderType',
					scope: 1,
				},
				{
					actionIds: ['VIEW'],
					primaryKey: companyId,
					resourceName:
						'com.liferay.commerce.product.model.CommerceChannel',
					scope: 1,
				},
				{
					actionIds: ['VIEW'],
					primaryKey: companyId,
					resourceName:
						'com.liferay.commerce.notification.model.CommerceNotificationTemplate',
					scope: 1,
				},
				{
					actionIds: ['VIEW_CONTROL_PANEL'],
					primaryKey: companyId,
					resourceName: '90',
					scope: 1,
				},
				{
					actionIds: ['ACCESS_IN_CONTROL_PANEL', 'VIEW'],
					primaryKey: companyId,
					resourceName:
						'com_liferay_commerce_order_web_internal_portlet_CommerceOrderPortlet',
					scope: 1,
				},
				{
					actionIds: ['VIEW_COMMERCE_CHANNELS'],
					primaryKey: companyId,
					resourceName: 'com.liferay.commerce.channel',
					scope: 1,
				},
			],
		});

		const viewUser = await apiHelpers.headlessAdminUser.postUserAccount();

		userData[viewUser.alternateName] = {
			name: viewUser.givenName,
			password: 'test',
			surname: viewUser.familyName,
		};

		await apiHelpers.headlessAdminUser.assignUserToRole(
			viewOrderRole.externalReferenceCode,
			viewUser.id
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

		const generalAddTitles = [
			'Purchase Order Number',
			'Payment Terms',
			'Delivery Terms',
			'Requested Delivery Date',
			'Printed Note',
		];
		const generalEditTitles = ['Billing Address', 'Shipping Address'];
		const paymentEditTitles = ['Payment Method', 'Payment Status'];

		await test.step('Admin can see Add/Edit buttons on the order details page', async () => {
			await commerceAdminOrdersPage.goto();

			await (
				await commerceAdminOrdersPage.tableRowLink({
					colIndex: 1,
					rowValue: order.id,
				})
			).click();

			for (const title of generalAddTitles) {
				await expect(
					await commerceAdminOrderDetailsPage.editEntryActionLink(
						`${title} Add`,
						'Add'
					)
				).toBeVisible();
			}

			for (const title of generalEditTitles) {
				await expect(
					await commerceAdminOrderDetailsPage.editEntryActionLink(
						`${title} Edit`,
						'Edit'
					)
				).toBeVisible();
			}

			await expect(
				commerceAdminOrderDetailsPage.orderSummaryLink
			).toBeVisible();
			await expect(
				commerceAdminOrderDetailsPage.orderItemActions
			).toBeVisible();

			await (
				await commerceAdminOrderDetailsPage.orderDetailsTab('Payments')
			).click();

			for (const title of paymentEditTitles) {
				await expect(
					await commerceAdminOrderDetailsPage.editEntryActionLink(
						`${title} Edit`,
						'Edit'
					)
				).toBeVisible();
			}
		});

		await test.step('User with view-only role cannot see Add/Edit buttons on the order details page', async () => {
			await performUserSwitch(page, viewUser.alternateName);

			await commerceAdminOrdersPage.goto();

			await (
				await commerceAdminOrdersPage.tableRowLink({
					colIndex: 1,
					rowValue: order.id,
				})
			).click();

			for (const title of generalAddTitles) {
				await expect(
					await commerceAdminOrderDetailsPage.editEntryActionLink(
						`${title} Add`,
						'Add'
					)
				).toHaveCount(0);
			}

			for (const title of generalEditTitles) {
				await expect(
					await commerceAdminOrderDetailsPage.editEntryActionLink(
						`${title} Edit`,
						'Edit'
					)
				).toHaveCount(0);
			}

			await expect(
				commerceAdminOrderDetailsPage.orderSummaryLink
			).toHaveCount(0);
			await expect(
				commerceAdminOrderDetailsPage.orderItemActions
			).toHaveCount(0);

			await (
				await commerceAdminOrderDetailsPage.orderDetailsTab('Payments')
			).click();

			for (const title of paymentEditTitles) {
				await expect(
					await commerceAdminOrderDetailsPage.editEntryActionLink(
						`${title} Edit`,
						'Edit'
					)
				).toHaveCount(0);
			}
		});
	}
);

test(
	'Admin can transition a Pending order to Processing via Accept Order',
	{tag: ['@COMMERCE-12716', '@LPD-89343']},
	async ({
		apiHelpers,
		commerceAdminChannelDetailsPage,
		commerceAdminChannelsPage,
		commerceAdminOrdersPage,
		page,
	}) => {
		const account = await apiHelpers.headlessAdminUser.postAccount({
			name: 'Commerce Account ' + getRandomString(),
			type: 'business',
		});

		await commerceAdminChannelsPage.goto();

		await (
			await commerceAdminChannelsPage.channelsTableRowLink(channel.name)
		).click();

		await commerceAdminChannelDetailsPage.activatePaymentMethod(
			'Money Order',
			'Money Order'
		);

		const product =
			await apiHelpers.headlessCommerceAdminCatalog.getProductByName(
				'U-Joint'
			);

		const order = await apiHelpers.headlessCommerceAdminOrder.postOrder({
			accountId: account.id,
			channelId: channel.id,
			orderItems: [{quantity: 1, skuId: String(product.skus[0].id)}],
			orderStatus: '1',
			paymentMethod: 'money-order',
			paymentStatus: '2',
		});

		await commerceAdminOrdersPage.goto();

		await (
			await commerceAdminOrdersPage.tableRowLink({
				colIndex: 1,
				rowValue: order.id,
			})
		).click();

		await commerceAdminOrdersPage.orderStatusLink('Accept Order').click();

		await waitForAlert(page);

		await commerceAdminOrdersPage.goto();

		await expect(
			commerceAdminOrdersPage.keyOrderStatus('Processing')
		).toBeVisible();
	}
);

test(
	'Editing remaining unshipped order item quantity completes a Partially Shipped order',
	{tag: ['@COMMERCE-10086', '@LPD-89343']},
	async ({
		apiHelpers,
		commerceAdminChannelDetailsPage,
		commerceAdminChannelsPage,
		commerceAdminOrderDetailsPage,
		commerceAdminOrdersPage,
		commerceAdminShipmentsPage,
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
					name: 'Test Name',
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

		const product =
			await apiHelpers.headlessCommerceAdminCatalog.getProductByName(
				'U-Joint'
			);

		const order = await apiHelpers.headlessCommerceAdminOrder.postOrder({
			accountId: account.id,
			billingAddressId: address.id,
			channelId: channel.id,
			orderItems: [{quantity: 5, skuId: String(product.skus[0].id)}],
			orderStatus: '1',
			paymentMethod: 'money-order',
			paymentStatus: '0',
			shippingAddressId: address.id,
		});

		await commerceAdminOrdersPage.goto();

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

		await waitForAlert(page);

		await commerceAdminShipmentsPage.addProductsToShipment.click();
		await (
			await commerceAdminShipmentsPage.shipmentItemsTableRowAction(
				1,
				'MIN55861'
			)
		).check();
		await commerceAdminShipmentsPage.shipmentsItemSubmitButton.click();
		await commerceAdminShipmentsPage.productEllipsis('MIN55861').click();
		await commerceAdminShipmentsPage.editProductMenuItem.click();
		await (
			await commerceAdminShipmentsPage.editProductTableRowQuantitySelector(
				{
					colIndex: 0,
					rowValue: 'Italy',
				}
			)
		).fill('1');
		await commerceAdminShipmentsPage.editProductSaveButton.click();
		await commerceAdminShipmentsPage.editProductCloseButton.click();
		await commerceAdminShipmentsPage
			.shipmentStatusLink('Finish Processing')
			.click();
		await commerceAdminShipmentsPage.shipmentStatusLink('Ship').click();

		await waitForAlert(page);

		await commerceAdminShipmentsPage.shipmentStatusLink('Deliver').click();

		await waitForAlert(page);

		await commerceAdminOrdersPage.goto();

		await expect(
			commerceAdminOrdersPage.keyOrderStatus('Partially Shipped')
		).toBeVisible();

		await (
			await commerceAdminOrdersPage.tableRowLink({
				colIndex: 1,
				rowValue: order.id,
			})
		).click();

		await (
			await commerceAdminOrdersPage.itemsTableRowAction('MIN55861')
		).click();
		await commerceAdminOrderDetailsPage.orderItemActionEdit.click();
		await commerceAdminOrderDetailsPage.orderItemDecimalQuantity.fill('1');
		await commerceAdminOrderDetailsPage.orderItemSaveButton.click();
		await commerceAdminOrderDetailsPage.orderItemFrameCloseButton.click();

		await commerceAdminOrdersPage.goto();

		await expect(
			commerceAdminOrdersPage.keyOrderStatus('Completed')
		).toBeVisible();

		await commerceAdminShipmentsPage.goTo();

		await expect(
			commerceAdminShipmentsPage.keyShipmentStatus('Delivered')
		).toBeVisible();
	}
);

test(
	'Admin can edit order item quantity from the order details page and view a non-empty payment transaction history timestamp',
	{tag: ['@COMMERCE-11392', '@LPD-89343']},
	async ({
		apiHelpers,
		checkoutPage,
		commerceAdminChannelDetailsPage,
		commerceAdminChannelsPage,
		commerceAdminOrderDetailsPage,
		commerceAdminOrdersPage,
		commerceMiniCartPage,
		page,
	}) => {
		const account = await apiHelpers.headlessAdminUser.postAccount({
			name: 'Commerce Account ' + getRandomString(),
			type: 'business',
		});

		await commerceAdminChannelsPage.goto();

		await (
			await commerceAdminChannelsPage.channelsTableRowLink(channel.name)
		).click();

		await commerceAdminChannelDetailsPage.activatePaymentMethod(
			'Money Order',
			'Money Order'
		);

		const companyId = await page.evaluate(() => {
			return Liferay.ThemeDisplay.getCompanyId();
		});

		const role = await apiHelpers.headlessAdminUser.postRole({
			name: 'Test Buyer ' + getRandomString(),
			rolePermissions: [
				{
					actionIds: ['VIEW'],
					primaryKey: companyId,
					resourceName:
						'com.liferay.commerce.model.CommerceOrderType',
					scope: 1,
				},
				{
					actionIds: [
						'ADD_COMMERCE_ORDER',
						'CHECKOUT_OPEN_COMMERCE_ORDERS',
						'MANAGE_COMMERCE_ORDER_DELIVERY_TERMS',
						'MANAGE_COMMERCE_ORDER_PAYMENT_METHODS',
						'MANAGE_COMMERCE_ORDER_PAYMENT_TERMS',
						'MANAGE_COMMERCE_ORDER_SHIPPING_OPTIONS',
						'VIEW_BILLING_ADDRESS',
						'VIEW_COMMERCE_ORDERS',
						'VIEW_OPEN_COMMERCE_ORDERS',
					],
					primaryKey: '0',
					resourceName: 'com.liferay.commerce.order',
					scope: 3,
				},
				{
					actionIds: ['MANAGE_ADDRESSES', 'VIEW_ADDRESSES'],
					primaryKey: '0',
					resourceName: 'com.liferay.account.model.AccountEntry',
					scope: 3,
				},
			],
		});

		const buyerUser = await apiHelpers.headlessAdminUser.postUserAccount();

		userData[buyerUser.alternateName] = {
			name: buyerUser.givenName,
			password: 'test',
			surname: buyerUser.familyName,
		};

		await apiHelpers.headlessAdminUser.assignUserToAccountByEmailAddress(
			account.id,
			[buyerUser.emailAddress]
		);

		const siteMemberRole =
			await apiHelpers.headlessAdminUser.getRoleByName('Site Member');

		await apiHelpers.headlessAdminUser.assignUserToSite(
			siteMemberRole.id,
			site.id,
			buyerUser.id
		);

		await apiHelpers.headlessAdminUser.postRoleByExternalReferenceCodeUserAccountAssociation(
			role.externalReferenceCode,
			buyerUser.id
		);

		await performUserSwitch(page, buyerUser.alternateName);

		await page.goto(`/web/${site.name}/catalog`);

		await commerceMiniCartPage.quickAddToCart('MIN93015');
		await commerceMiniCartPage.submitButton.click();

		await page.waitForURL((url) => url.href.includes('shipping-address'));

		await checkoutPage.addAddress({
			city: 'Test City',
			countryLabel: 'United States',
			name: 'Address Name',
			regionLabel: 'California',
			street: 'Test Address',
			zip: '12345',
		});

		await checkoutPage.continueButton.click();

		await checkoutPage.shippingMethodRadio('Standard').check();

		await checkoutPage.continueButton.click();

		await expect(checkoutPage.activeCheckoutStep).toContainText(
			'Order Summary'
		);

		await checkoutPage.continueButton.click();

		await expect(checkoutPage.orderConfirmationContainer).toBeVisible();

		await performLoginViaApi({page, screenName: 'test'});

		const orders =
			await apiHelpers.headlessCommerceAdminOrder.getOrdersPage();

		const order = orders.items.find(
			(o: {accountId: number}) => o.accountId === account.id
		);

		await commerceAdminOrdersPage.goto();

		await (
			await commerceAdminOrdersPage.tableRowLink({
				colIndex: 1,
				rowValue: order.id,
			})
		).click();

		await (
			await commerceAdminOrdersPage.itemsTableRowAction('MIN93015')
		).click();
		await commerceAdminOrderDetailsPage.orderItemActionEdit.click();
		await commerceAdminOrderDetailsPage.orderItemDecimalQuantity.fill('4');
		await commerceAdminOrderDetailsPage.orderItemSaveButton.click();
		await commerceAdminOrderDetailsPage.orderItemFrameCloseButton.click();

		await expect(
			await commerceAdminOrderDetailsPage.orderItemQuantityColumn('4')
		).toBeVisible();

		await expect(
			commerceAdminOrderDetailsPage.addressFieldText('Test Address')
		).toHaveCount(2);
		await expect(
			commerceAdminOrderDetailsPage.addressFieldText('Test City')
		).toHaveCount(2);
		await expect(
			commerceAdminOrderDetailsPage.addressFieldText('12345')
		).toHaveCount(2);
		await expect(
			commerceAdminOrderDetailsPage.orderSummarySubtotal
		).not.toBeEmpty();

		await (
			await commerceAdminOrderDetailsPage.orderDetailsTab('Payments')
		).click();

		await expect(
			commerceAdminOrderDetailsPage.paymentMethodName
		).toContainText('Money Order');

		await expect(
			commerceAdminOrderDetailsPage.firstTransactionTimestampCell
		).not.toBeEmpty();
	}
);

test(
	'Orders admin can be filtered by Account, Channel, and Order Status',
	{tag: ['@COMMERCE-5997', '@LPD-89343']},
	async ({apiHelpers, commerceAdminOrdersPage}) => {
		const account1Name = 'Commerce Account 1 ' + getRandomString();
		const account2Name = 'Commerce Account 2 ' + getRandomString();

		const account1 = await apiHelpers.headlessAdminUser.postAccount({
			name: account1Name,
			type: 'business',
		});
		const account2 = await apiHelpers.headlessAdminUser.postAccount({
			name: account2Name,
			type: 'business',
		});

		const otherSite = await apiHelpers.headlessAdminSite.postSite({
			name: 'Other ' + getRandomString(),
		});

		const otherChannel =
			await apiHelpers.headlessCommerceAdminChannel.postChannel({
				siteGroupId: otherSite.id,
			});

		const product =
			await apiHelpers.headlessCommerceAdminCatalog.getProductByName(
				'U-Joint'
			);

		const order1 = await apiHelpers.headlessCommerceAdminOrder.postOrder({
			accountId: account1.id,
			channelId: channel.id,
			orderItems: [{quantity: 1, skuId: String(product.skus[0].id)}],
			orderStatus: '1',
		});
		const order2 = await apiHelpers.headlessCommerceAdminOrder.postOrder({
			accountId: account2.id,
			channelId: otherChannel.id,
			orderItems: [{quantity: 1, skuId: String(product.skus[0].id)}],
			orderStatus: '2',
		});

		await commerceAdminOrdersPage.goto();

		await test.step('Filter by Account shows only matching orders', async () => {
			await commerceAdminOrdersPage.applyFilter('Account', account1Name);

			await expect(
				commerceAdminOrdersPage.tableRowOrderIdLink(order1.id)
			).toBeVisible();
			await expect(
				commerceAdminOrdersPage.tableRowOrderIdLink(order2.id)
			).toHaveCount(0);
		});

		await test.step('Filter by Channel shows only matching orders', async () => {
			await commerceAdminOrdersPage.goto();

			await commerceAdminOrdersPage.applyFilter('Channel', channel.name);

			await expect(
				commerceAdminOrdersPage.tableRowOrderIdLink(order1.id)
			).toBeVisible();
			await expect(
				commerceAdminOrdersPage.tableRowOrderIdLink(order2.id)
			).toHaveCount(0);
		});

		await test.step('Filter by Order Status shows only matching orders', async () => {
			await commerceAdminOrdersPage.goto();

			await commerceAdminOrdersPage.applyFilter('Order Status', 'Open');

			await expect(
				commerceAdminOrdersPage.tableRowOrderIdLink(order2.id)
			).toBeVisible();
			await expect(
				commerceAdminOrdersPage.tableRowOrderIdLink(order1.id)
			).toHaveCount(0);
		});
	}
);

test(
	'When multiple Minimum Order Amount rules are active, the highest-priority rule is enforced and the buyer cannot checkout',
	{tag: ['@LPD-89343']},
	async ({apiHelpers, commerceMiniCartPage, orderDetailsPage, page}) => {
		const account = await apiHelpers.headlessAdminUser.postAccount({
			name: 'Commerce Account ' + getRandomString(),
			type: 'business',
		});

		const role = await apiHelpers.headlessAdminUser.postRole({
			name: 'Test Buyer ' + getRandomString(),
			rolePermissions: CHECKOUT_PERMISSIONS_WITHOUT_VIEW_BILLING_ADDRESS(
				await page.evaluate(() => {
					return Liferay.ThemeDisplay.getCompanyId();
				})
			),
		});

		const buyerUser = await apiHelpers.headlessAdminUser.postUserAccount();

		userData[buyerUser.alternateName] = {
			name: buyerUser.givenName,
			password: 'test',
			surname: buyerUser.familyName,
		};

		await apiHelpers.headlessAdminUser.assignUserToAccountByEmailAddress(
			account.id,
			[buyerUser.emailAddress]
		);

		const siteMemberRole =
			await apiHelpers.headlessAdminUser.getRoleByName('Site Member');

		await apiHelpers.headlessAdminUser.assignUserToSite(
			siteMemberRole.id,
			site.id,
			buyerUser.id
		);

		await apiHelpers.headlessAdminUser.postRoleByExternalReferenceCodeUserAccountAssociation(
			role.externalReferenceCode,
			buyerUser.id
		);

		await apiHelpers.headlessCommerceAdminOrder.postOrderRule({
			active: true,
			name: 'Test Order Rule ' + getRandomString(),
			priority: 0,
			type: 'minimum-order-amount',
			typeSettings:
				'minimum-order-amount-field-amount=25.00\n' +
				'minimum-order-amount-field-apply-to=minimum-order-amount-field-apply-to-order-total\n' +
				'minimum-order-amount-field-currency-code=USD\n',
		});
		await apiHelpers.headlessCommerceAdminOrder.postOrderRule({
			active: true,
			name: 'Test Order Rule ' + getRandomString(),
			priority: 1,
			type: 'minimum-order-amount',
			typeSettings:
				'minimum-order-amount-field-amount=50.00\n' +
				'minimum-order-amount-field-apply-to=minimum-order-amount-field-apply-to-order-subtotal\n' +
				'minimum-order-amount-field-currency-code=USD\n',
		});
		await apiHelpers.headlessCommerceAdminOrder.postOrderRule({
			active: true,
			name: 'Test Order Rule ' + getRandomString(),
			priority: 2,
			type: 'minimum-order-amount',
			typeSettings:
				'minimum-order-amount-field-amount=75.00\n' +
				'minimum-order-amount-field-apply-to=minimum-order-amount-field-apply-to-order-subtotal\n' +
				'minimum-order-amount-field-currency-code=USD\n',
		});

		await performUserSwitch(page, buyerUser.alternateName);

		await page.goto(`/web/${site.name}/catalog`);

		await commerceMiniCartPage.quickAddToCart('MIN55861');
		await commerceMiniCartPage.viewDetailsButton.click();

		await expect(
			orderDetailsPage.minimumOrderAmountWarning('$ 75.00', '$ 51.00')
		).toBeVisible();

		await expect(orderDetailsPage.checkoutButton).toHaveCount(0);
	}
);

test(
	'Inventory On Order tab shows orders for a SKU and supports searching by Order ID and Account Name',
	{tag: ['@COMMERCE-10750', '@LPD-89343']},
	async ({
		apiHelpers,
		checkoutPage,
		commerceAdminInventoryItemPage,
		commerceAdminInventoryPage,
		commerceMiniCartPage,
		page,
	}) => {
		const accountName = 'Account Name ' + getRandomString();
		const otherAccountName = 'Commerce Account ' + getRandomString();

		const account = await apiHelpers.headlessAdminUser.postAccount({
			name: accountName,
			type: 'business',
		});
		await apiHelpers.headlessAdminUser.postAccount({
			name: otherAccountName,
			type: 'business',
		});

		const role = await apiHelpers.headlessAdminUser.postRole({
			name: 'Test Buyer ' + getRandomString(),
			rolePermissions: [
				{
					actionIds: ['VIEW'],
					primaryKey: await page.evaluate(() => {
						return Liferay.ThemeDisplay.getCompanyId();
					}),
					resourceName:
						'com.liferay.commerce.model.CommerceOrderType',
					scope: 1,
				},
				{
					actionIds: [
						'ADD_COMMERCE_ORDER',
						'CHECKOUT_OPEN_COMMERCE_ORDERS',
						'MANAGE_COMMERCE_ORDER_DELIVERY_TERMS',
						'MANAGE_COMMERCE_ORDER_PAYMENT_METHODS',
						'MANAGE_COMMERCE_ORDER_PAYMENT_TERMS',
						'MANAGE_COMMERCE_ORDER_SHIPPING_OPTIONS',
						'VIEW_BILLING_ADDRESS',
						'VIEW_COMMERCE_ORDERS',
						'VIEW_OPEN_COMMERCE_ORDERS',
					],
					primaryKey: '0',
					resourceName: 'com.liferay.commerce.order',
					scope: 3,
				},
				{
					actionIds: ['MANAGE_ADDRESSES', 'VIEW_ADDRESSES'],
					primaryKey: '0',
					resourceName: 'com.liferay.account.model.AccountEntry',
					scope: 3,
				},
			],
		});

		const buyerUser = await apiHelpers.headlessAdminUser.postUserAccount();

		userData[buyerUser.alternateName] = {
			name: buyerUser.givenName,
			password: 'test',
			surname: buyerUser.familyName,
		};

		await apiHelpers.headlessAdminUser.assignUserToAccountByEmailAddress(
			account.id,
			[buyerUser.emailAddress]
		);

		const siteMemberRole =
			await apiHelpers.headlessAdminUser.getRoleByName('Site Member');

		await apiHelpers.headlessAdminUser.assignUserToSite(
			siteMemberRole.id,
			site.id,
			buyerUser.id
		);

		await apiHelpers.headlessAdminUser.postRoleByExternalReferenceCodeUserAccountAssociation(
			role.externalReferenceCode,
			buyerUser.id
		);

		await performUserSwitch(page, buyerUser.alternateName);

		for (let i = 0; i < 2; i++) {
			await page.goto(`/web/${site.name}/catalog`);

			await commerceMiniCartPage.quickAddToCart('MIN55861');
			await commerceMiniCartPage.submitButton.click();

			await page.waitForURL((url) =>
				url.href.includes('shipping-address')
			);

			await checkoutPage.addAddress({
				city: 'Test City',
				countryLabel: 'United States',
				name: 'Address Name',
				regionLabel: 'California',
				street: 'Test Address',
				zip: '12345',
			});

			await checkoutPage.continueButton.click();

			await checkoutPage.shippingMethodRadio('Standard').check();

			await checkoutPage.continueButton.click();

			await expect(checkoutPage.activeCheckoutStep).toContainText(
				'Order Summary'
			);

			await checkoutPage.continueButton.click();

			await expect(checkoutPage.orderConfirmationContainer).toBeVisible();
		}

		await performLoginViaApi({page, screenName: 'test'});

		const orders =
			await apiHelpers.headlessCommerceAdminOrder.getOrdersPage();
		const accountOrders = orders.items.filter(
			(o: {accountId: number}) => o.accountId === account.id
		);

		await commerceAdminInventoryPage.goto();

		await commerceAdminInventoryPage.searchInput.fill('MIN55861');
		await commerceAdminInventoryPage.searchInput.press('Enter');

		await expect(
			commerceAdminInventoryPage.skuLink('MIN55861')
		).toBeVisible();

		await commerceAdminInventoryPage.skuLink('MIN55861').click();

		await commerceAdminInventoryItemPage.onOrderTab.click();

		await test.step('Search by Order ID returns only the matching order', async () => {
			await commerceAdminInventoryItemPage.searchInput.fill(
				String(accountOrders[0].id)
			);
			await commerceAdminInventoryItemPage.searchInput.press('Enter');

			await expect(
				commerceAdminInventoryItemPage.onOrderRowText(
					String(accountOrders[0].id)
				)
			).toBeVisible();
			await expect(
				commerceAdminInventoryItemPage.onOrderRowText(
					String(accountOrders[1].id)
				)
			).toHaveCount(0);

			await commerceAdminInventoryItemPage.searchInput.fill('');
			await commerceAdminInventoryItemPage.searchInput.press('Enter');
		});

		await test.step('Search by Account Name returns orders for that account only', async () => {
			await commerceAdminInventoryItemPage.searchInput.fill(accountName);
			await commerceAdminInventoryItemPage.searchInput.press('Enter');

			await expect(
				commerceAdminInventoryItemPage
					.onOrderRowText(accountName)
					.first()
			).toBeVisible();
			await expect(
				commerceAdminInventoryItemPage.onOrderRowText(otherAccountName)
			).toHaveCount(0);
		});
	}
);

test(
	'Buyer authenticated on the admin-order endpoint sees only orders they personally created',
	{tag: ['@COMMERCE-10895', '@LPD-89343']},
	async ({apiHelpers, page}) => {
		const accountAName = 'Commerce Account A ' + getRandomString();
		const accountBName = 'Commerce Account B ' + getRandomString();

		const accountA = await apiHelpers.headlessAdminUser.postAccount({
			name: accountAName,
			type: 'business',
		});
		const accountB = await apiHelpers.headlessAdminUser.postAccount({
			name: accountBName,
			type: 'business',
		});

		const companyId = await page.evaluate(() => {
			return Liferay.ThemeDisplay.getCompanyId();
		});

		const role = await apiHelpers.headlessAdminUser.postRole({
			name: 'Test Buyer ' + getRandomString(),
			rolePermissions: [
				{
					actionIds: ['VIEW'],
					primaryKey: companyId,
					resourceName:
						'com.liferay.commerce.model.CommerceOrderType',
					scope: 1,
				},
				{
					actionIds: [
						'ADD_COMMERCE_ORDER',
						'CHECKOUT_OPEN_COMMERCE_ORDERS',
						'MANAGE_COMMERCE_ORDER_DELIVERY_TERMS',
						'MANAGE_COMMERCE_ORDER_PAYMENT_METHODS',
						'MANAGE_COMMERCE_ORDER_PAYMENT_TERMS',
						'MANAGE_COMMERCE_ORDER_SHIPPING_OPTIONS',
						'VIEW_BILLING_ADDRESS',
						'VIEW_COMMERCE_ORDERS',
						'VIEW_OPEN_COMMERCE_ORDERS',
					],
					primaryKey: '0',
					resourceName: 'com.liferay.commerce.order',
					scope: 3,
				},
				{
					actionIds: ['MANAGE_ADDRESSES', 'VIEW_ADDRESSES'],
					primaryKey: '0',
					resourceName: 'com.liferay.account.model.AccountEntry',
					scope: 3,
				},
			],
		});

		const buyerUser = await apiHelpers.headlessAdminUser.postUserAccount();

		userData[buyerUser.alternateName] = {
			name: buyerUser.givenName,
			password: 'test',
			surname: buyerUser.familyName,
		};

		await apiHelpers.headlessAdminUser.assignUserToAccountByEmailAddress(
			accountA.id,
			[buyerUser.emailAddress]
		);

		const siteMemberRole =
			await apiHelpers.headlessAdminUser.getRoleByName('Site Member');

		await apiHelpers.headlessAdminUser.assignUserToSite(
			siteMemberRole.id,
			site.id,
			buyerUser.id
		);

		await apiHelpers.headlessAdminUser.postRoleByExternalReferenceCodeUserAccountAssociation(
			role.externalReferenceCode,
			buyerUser.id
		);

		const accountAAddress =
			await apiHelpers.headlessCommerceAdminAccount.postAddress(
				accountA.id,
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

		const product =
			await apiHelpers.headlessCommerceAdminCatalog.getProductByName(
				'U-Joint'
			);

		await performUserSwitch(page, buyerUser.alternateName);

		const buyerCart =
			await apiHelpers.headlessCommerceDeliveryCart.postCart(
				{
					accountId: accountA.id,
					billingAddressId: accountAAddress.id,
					cartItems: [
						{quantity: 1, skuId: Number(product.skus[0].id)},
					],
					shippingAddressId: accountAAddress.id,
				},
				channel.id
			);

		await apiHelpers.headlessCommerceDeliveryCart.checkoutCart(
			buyerCart.id
		);

		await test.step('After the buyer creates one order, GET /orders authenticated as buyer returns exactly that order', async () => {
			const ordersPage =
				await apiHelpers.headlessCommerceAdminOrder.getOrdersPage();

			expect(ordersPage.totalCount).toBe(1);
			expect(ordersPage.items.map((o: {id: number}) => o.id)).toEqual([
				buyerCart.id,
			]);
		});

		await performLoginViaApi({page, screenName: 'test'});

		const orderByAdmin =
			await apiHelpers.headlessCommerceAdminOrder.postOrder({
				accountId: accountB.id,
				channelId: channel.id,
				orderItems: [{quantity: 1, skuId: String(product.skus[0].id)}],
				orderStatus: '1',
			});

		await performUserSwitch(page, buyerUser.alternateName);

		await test.step('After admin creates an order on a different account, GET /orders authenticated as buyer still returns only the buyer-created order', async () => {
			const ordersPage =
				await apiHelpers.headlessCommerceAdminOrder.getOrdersPage();

			const orderIds = ordersPage.items.map((o: {id: number}) => o.id);

			expect(orderIds).toContain(buyerCart.id);
			expect(orderIds).not.toContain(orderByAdmin.id);
		});
	}
);

test(
	'Printed note set on a delivery cart survives checkout and is visible on the admin order details page',
	{tag: ['@COMMERCE-11737', '@LPD-89343']},
	async ({
		apiHelpers,
		commerceAdminChannelDetailsPage,
		commerceAdminChannelsPage,
		commerceAdminOrderDetailsPage,
		commerceAdminOrdersPage,
		page,
	}) => {
		const printedNote = 'Test Print Note Content ' + getRandomString();

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

		const product =
			await apiHelpers.headlessCommerceAdminCatalog.getProductByName(
				'U-Joint'
			);

		const cart = await apiHelpers.headlessCommerceDeliveryCart.postCart(
			{
				accountId: account.id,
				billingAddressId: address.id,
				cartItems: [{quantity: 10, skuId: Number(product.skus[0].id)}],
				printedNote,
				shippingAddressId: address.id,
			},
			channel.id
		);

		await test.step('GET order returns the printed note set on the cart (Open status)', async () => {
			const order = await apiHelpers.headlessCommerceAdminOrder.getOrder(
				cart.id
			);

			expect(order.printedNote).toBe(printedNote);
		});

		await test.step('Admin can view the printed note on the Open order details page', async () => {
			await commerceAdminOrdersPage.goto();

			await (
				await commerceAdminOrdersPage.tableRowLink({
					colIndex: 1,
					rowValue: cart.id,
				})
			).click();

			await expect(
				await commerceAdminOrderDetailsPage.editEntryActionLink(
					'Printed Note Edit',
					'Edit'
				)
			).toBeVisible();
			await expect(page.getByText(printedNote)).toBeVisible();
		});

		await apiHelpers.headlessCommerceDeliveryCart.checkoutCart(cart.id);

		await test.step('GET order after checkout still returns the printed note (Pending status)', async () => {
			const order = await apiHelpers.headlessCommerceAdminOrder.getOrder(
				cart.id
			);

			expect(order.printedNote).toBe(printedNote);
		});

		await test.step('Admin can view the printed note on the Pending order details page', async () => {
			await commerceAdminOrdersPage.goto();

			await (
				await commerceAdminOrdersPage.tableRowLink({
					colIndex: 1,
					rowValue: cart.id,
				})
			).click();

			await expect(
				await commerceAdminOrderDetailsPage.editEntryActionLink(
					'Printed Note Edit',
					'Edit'
				)
			).toBeVisible();
			await expect(page.getByText(printedNote)).toBeVisible();
		});
	}
);

test(
	'Removing the manage delivery terms permission hides only the delivery controls',
	{tag: ['@LPD-97544']},
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

		const companyId = await page.evaluate(() => {
			return Liferay.ThemeDisplay.getCompanyId();
		});

		const orderTermsRole = await apiHelpers.headlessAdminUser.postRole({
			name: 'Order Terms Role ' + getRandomString(),
			rolePermissions: [
				{
					actionIds: [
						'MANAGE_COMMERCE_ORDER_DELIVERY_TERMS',
						'MANAGE_COMMERCE_ORDER_PAYMENT_TERMS',
						'VIEW_COMMERCE_ORDERS',
					],
					primaryKey: companyId,
					resourceName: 'com.liferay.commerce.order',
					scope: 1,
				},
				{
					actionIds: ['VIEW'],
					primaryKey: companyId,
					resourceName:
						'com.liferay.commerce.payment.model.CommercePaymentMethodGroupRel',
					scope: 1,
				},
				{
					actionIds: ['VIEW'],
					primaryKey: companyId,
					resourceName: 'com.liferay.commerce.model.CommerceOrder',
					scope: 1,
				},
				{
					actionIds: ['VIEW'],
					primaryKey: companyId,
					resourceName:
						'com.liferay.commerce.model.CommerceOrderType',
					scope: 1,
				},
				{
					actionIds: ['VIEW'],
					primaryKey: companyId,
					resourceName:
						'com.liferay.commerce.product.model.CommerceChannel',
					scope: 1,
				},
				{
					actionIds: ['VIEW'],
					primaryKey: companyId,
					resourceName:
						'com.liferay.commerce.notification.model.CommerceNotificationTemplate',
					scope: 1,
				},
				{
					actionIds: ['VIEW_CONTROL_PANEL'],
					primaryKey: companyId,
					resourceName: '90',
					scope: 1,
				},
				{
					actionIds: ['ACCESS_IN_CONTROL_PANEL', 'VIEW'],
					primaryKey: companyId,
					resourceName:
						'com_liferay_commerce_order_web_internal_portlet_CommerceOrderPortlet',
					scope: 1,
				},
				{
					actionIds: ['VIEW_COMMERCE_CHANNELS'],
					primaryKey: companyId,
					resourceName: 'com.liferay.commerce.channel',
					scope: 1,
				},
			],
		});

		const buyerUser = await apiHelpers.headlessAdminUser.postUserAccount();

		userData[buyerUser.alternateName] = {
			name: buyerUser.givenName,
			password: 'test',
			surname: buyerUser.familyName,
		};

		await apiHelpers.headlessAdminUser.assignUserToRole(
			orderTermsRole.externalReferenceCode,
			buyerUser.id
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

		await performUserSwitch(page, buyerUser.alternateName);

		await commerceAdminOrdersPage.goto();

		await (
			await commerceAdminOrdersPage.tableRowLink({
				colIndex: 1,
				rowValue: order.id,
			})
		).click();

		await expect(
			await commerceAdminOrderDetailsPage.editEntryActionLink(
				'Payment Terms Add',
				'Add'
			)
		).toBeVisible();

		await expect(
			await commerceAdminOrderDetailsPage.editEntryActionLink(
				'Delivery Terms Add',
				'Add'
			)
		).toBeVisible();

		await performLogout(page);
		await performLoginViaApi({page, screenName: 'test'});

		await apiHelpers.jsonWebServicesResourcePermissionApiHelper.removeResourcePermission(
			'MANAGE_COMMERCE_ORDER_DELIVERY_TERMS',
			companyId,
			'0',
			'com.liferay.commerce.order',
			String(companyId),
			String(orderTermsRole.id),
			'1'
		);

		await performUserSwitch(page, buyerUser.alternateName);

		await commerceAdminOrdersPage.goto();

		await (
			await commerceAdminOrdersPage.tableRowLink({
				colIndex: 1,
				rowValue: order.id,
			})
		).click();

		await expect(
			await commerceAdminOrderDetailsPage.editEntryActionLink(
				'Payment Terms Add',
				'Add'
			)
		).toBeVisible();

		await expect(
			await commerceAdminOrderDetailsPage.editEntryActionLink(
				'Delivery Terms Add',
				'Add'
			)
		).toHaveCount(0);
	}
);
