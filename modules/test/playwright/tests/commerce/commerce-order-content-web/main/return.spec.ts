/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {applicationsMenuPageTest} from '../../../../fixtures/applicationsMenuPageTest';
import {commercePagesTest} from '../../../../fixtures/commercePagesTest';
import {dataApiHelpersTest} from '../../../../fixtures/dataApiHelpersTest';
import {featureFlagsTest} from '../../../../fixtures/featureFlagsTest';
import {loginTest} from '../../../../fixtures/loginTest';
import {pageViewModePagesTest} from '../../../../fixtures/pageViewModePagesTest';
import {getRandomInt} from '../../../../utils/getRandomInt';
import getRandomString from '../../../../utils/getRandomString';
import performLogin, {performLogout} from '../../../../utils/performLogin';
import {waitForAlert} from '../../../../utils/waitForAlert';
import {commerceReturnSetUp, miniumSetUp} from '../../utils/commerce';

export const test = mergeTests(
	applicationsMenuPageTest,
	commercePagesTest,
	dataApiHelpersTest,
	featureFlagsTest({
		'LPD-10562': {enabled: true},
	}),
	loginTest(),
	pageViewModePagesTest
);

test('LPD-21633 Returns widget to show return and refunds', async ({
	apiHelpers,
	page,
	returnDetailsPage,
	returnsPage,
	widgetPagePage,
}) => {
	const {commerceReturn, payment, site} =
		await commerceReturnSetUp(apiHelpers);

	const layout = await apiHelpers.jsonWebServicesLayout.addLayout({
		groupId: site.id,
		title: getRandomString(),
	});

	await apiHelpers.headlessCommerceAdminPaymentApiHelper.postPayment({
		amount: payment.amount,
		relatedItemId: payment.id,
		relatedItemName:
			'com.liferay.commerce.payment.model.CommercePaymentEntry',
		type: 1,
	});

	await page.goto(`/web${site.friendlyUrlPath}${layout.friendlyURL}`);

	await widgetPagePage.addPortlet('Returns');

	await (
		await returnsPage.tableRowLink({
			colIndex: 0,
			rowValue: commerceReturn.id,
		})
	).click();

	await returnDetailsPage.returnActionsButton.click();

	await expect(
		returnDetailsPage.returnActionsViewRefundsButton
	).toBeVisible();

	await returnDetailsPage.returnActionsViewRefundsButton.click();

	await expect(returnDetailsPage.viewRefundsTitle).toBeVisible();
	await expect(
		returnDetailsPage.viewRefundsFrame.getByText(
			'Showing 1 to 1 of 1 entries.'
		)
	).toBeVisible();
});

test('LPD-32515 Returns widget displays amount fields with correct currency pattern', async ({
	apiHelpers,
	page,
	returnDetailsPage,
	returnsPage,
	widgetPagePage,
}) => {
	const {commerceReturn, site, sku} = await commerceReturnSetUp(apiHelpers);

	const layout = await apiHelpers.jsonWebServicesLayout.addLayout({
		groupId: site.id,
		title: getRandomString(),
	});

	await page.goto(`/web${site.friendlyUrlPath}${layout.friendlyURL}`);

	await widgetPagePage.addPortlet('Returns');

	await expect(
		(await returnsPage.tableRow(1, '$ 0.00', true)).row
	).toBeVisible();

	await (
		await returnsPage.tableRowLink({
			colIndex: 0,
			rowValue: commerceReturn.id,
		})
	).click();

	await expect(
		(await returnDetailsPage.tableRow(0, sku.sku, true)).row
	).toBeVisible();

	for await (const currencyField of await returnDetailsPage.page
		.getByText('0.00')
		.all()) {
		await expect(currencyField.getByText('$')).toBeVisible();
	}
});

test('LPD-32522 Returns widget displays status field on return items table when return is submitted', async ({
	apiHelpers,
	page,
	returnDetailsPage,
	returnsPage,
	widgetPagePage,
}) => {
	const {commerceReturn, site} = await commerceReturnSetUp(
		apiHelpers,
		0,
		0,
		0,
		1,
		'draft'
	);

	const layout = await apiHelpers.jsonWebServicesLayout.addLayout({
		groupId: site.id,
		title: getRandomString(),
	});

	await page.goto(`/web${site.friendlyUrlPath}${layout.friendlyURL}`);

	await widgetPagePage.addPortlet('Returns');

	await (
		await returnsPage.tableRowLink({
			colIndex: 0,
			rowValue: commerceReturn.id,
		})
	).click();

	await expect(await returnDetailsPage.table).toBeVisible();

	await expect(
		await returnDetailsPage.table.getByText('Status')
	).toBeHidden();

	await returnDetailsPage.submitReturnRequestButton.click();

	await expect(
		await returnDetailsPage.table.getByText('Status')
	).toBeVisible();
});

test('LPD-32519 Warning message before submitting a return should not be shown once the return has been submitted', async ({
	apiHelpers,
	page,
	returnDetailsPage,
	returnsPage,
	widgetPagePage,
}) => {
	const {commerceReturn, site} = await commerceReturnSetUp(
		apiHelpers,
		10,
		1,
		1,
		1,
		'draft'
	);

	const layout = await apiHelpers.jsonWebServicesLayout.addLayout({
		groupId: site.id,
		title: getRandomString(),
	});

	await page.goto(`/web${site.friendlyUrlPath}${layout.friendlyURL}`);

	await widgetPagePage.addPortlet('Returns');

	await (
		await returnsPage.tableRowLink({
			colIndex: 0,
			rowValue: commerceReturn.id,
		})
	).click();

	await page.waitForLoadState('networkidle');

	await expect(
		page.getByText(
			'Warning:Please review the details of the returning items before submitting the request.'
		)
	).toBeVisible();

	await expect(returnDetailsPage.submitReturnRequestLink).toBeVisible();

	await returnDetailsPage.submitReturnRequestLink.click();

	await expect(
		page.getByText(
			'Warning:Please review the details of the returning items before submitting the request.'
		)
	).not.toBeVisible();
});

test('LPD-32514 Return external reference code can not be edited in returns widget', async ({
	apiHelpers,
	page,
	returnDetailsPage,
	returnsPage,
	widgetPagePage,
}) => {
	const {commerceReturn, site} = await commerceReturnSetUp(apiHelpers);

	const layout = await apiHelpers.jsonWebServicesLayout.addLayout({
		groupId: site.id,
		title: getRandomString(),
	});

	await page.goto(`/web${site.friendlyUrlPath}${layout.friendlyURL}`);

	await widgetPagePage.addPortlet('Returns');

	await (
		await returnsPage.tableRowLink({
			colIndex: 0,
			rowValue: commerceReturn.id,
		})
	).click();

	await expect(
		returnDetailsPage.page.locator('#erc-edit-modal-opener')
	).toBeHidden();
});

test('LPD-32521 Returns widget details page will only show returns status', async ({
	apiHelpers,
	applicationsMenuPage,
	page,
	returnDetailsPage,
	returnsPage,
	widgetPagePage,
}) => {
	const {commerceReturn, site} = await commerceReturnSetUp(apiHelpers);

	const layout = await apiHelpers.jsonWebServicesLayout.addLayout({
		groupId: site.id,
		title: getRandomString(),
	});

	await applicationsMenuPage.goToSite(site.name);

	await page.goto(`/web${site.friendlyUrlPath}${layout.friendlyURL}`);

	await widgetPagePage.addPortlet('Returns');

	await (
		await returnsPage.tableRowLink({
			colIndex: 0,
			rowValue: commerceReturn.id,
		})
	).click();

	await expect(
		returnDetailsPage.page.getByText(
			commerceReturn.id + ' APPROVED PROCESSING'
		)
	).toBeHidden();

	await expect(
		returnDetailsPage.page.getByText(commerceReturn.id + ' PROCESSING')
	).toBeVisible();
});

test('LPD-32524 Returns widget to show comments for return items', async ({
	apiHelpers,
	page,
	returnDetailsPage,
	returnsPage,
	widgetPagePage,
}) => {
	const {commerceReturn, site} = await commerceReturnSetUp(apiHelpers);

	const layout = await apiHelpers.jsonWebServicesLayout.addLayout({
		groupId: site.id,
		title: getRandomString(),
	});

	await page.goto(`/web${site.friendlyUrlPath}${layout.friendlyURL}`);

	await widgetPagePage.addPortlet('Returns');
	await (
		await returnsPage.tableRowLink({
			colIndex: 0,
			rowValue: commerceReturn.id,
		})
	).click();

	await returnDetailsPage.returnActionsButton.click();

	await expect(
		returnDetailsPage.returnActionsViewDetailsButton
	).toBeVisible();

	await returnDetailsPage.returnActionsViewDetailsButton.click();

	await expect(returnDetailsPage.viewDetailsTitle).toBeVisible();

	await returnDetailsPage.viewDetailsCommentInput.fill('This is a comment.');
	await returnDetailsPage.viewDetailsSubmitButton.click();

	await expect(
		returnDetailsPage.viewDetailsFrame.getByText('This is a comment.')
	).toBeVisible();
	await expect(returnDetailsPage.viewDetailsCommentInput).toBeVisible();
});

test('LPD-32523 Returns widget to show received quantity label localized', async ({
	apiHelpers,
	page,
	returnDetailsPage,
	returnsPage,
	widgetPagePage,
}) => {
	const {commerceReturn, site} = await commerceReturnSetUp(apiHelpers);

	const layout = await apiHelpers.jsonWebServicesLayout.addLayout({
		groupId: site.id,
		title: getRandomString(),
	});

	await page.goto(`/web${site.friendlyUrlPath}${layout.friendlyURL}`);

	await widgetPagePage.addPortlet('Returns');

	await (
		await returnsPage.tableRowLink({
			colIndex: 0,
			rowValue: commerceReturn.id,
		})
	).click();

	await expect(await returnDetailsPage.table).toBeVisible();

	await expect(
		await returnDetailsPage.table.getByText('Received Quantity')
	).toBeVisible();
});

test('LPD-41539 Buyer users are missing permissions to view refunds', async ({
	apiHelpers,
	page,
	returnDetailsPage,
}) => {
	test.setTimeout(180000);

	const siteName = 'minium-' + getRandomInt();

	const {channel, site} = await miniumSetUp(apiHelpers, siteName);

	const account = await apiHelpers.headlessAdminUser.postAccount({
		name: 'Account Business',
		type: 'business',
	});

	const user =
		await apiHelpers.headlessAdminUser.getUserAccountByEmailAddress(
			'demo.unprivileged@liferay.com'
		);

	await apiHelpers.headlessAdminUser.assignUserToAccountByEmailAddress(
		account.id,
		['demo.unprivileged@liferay.com']
	);

	const companyId = await page.evaluate(() => {
		return Liferay.ThemeDisplay.getCompanyId();
	});

	const role = await apiHelpers.headlessAdminUser.postRole({
		name: 'Buyer ' + getRandomString(),
		rolePermissions: [
			{
				actionIds: ['MANAGE_ADDRESSES', 'VIEW_ADDRESSES'],
				primaryKey: '0',
				resourceName: 'com.liferay.account.model.AccountEntry',
				scope: 3,
			},
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
					'VIEW_BILLING_ADDRESS',
					'VIEW_COMMERCE_ORDERS',
					'VIEW_OPEN_COMMERCE_ORDERS',
				],
				primaryKey: '0',
				resourceName: 'com.liferay.commerce.order',
				scope: 3,
			},
		],
	});

	await apiHelpers.headlessAdminUser.postRoleUserAccountAssociation(
		role.id,
		user.id
	);

	apiHelpers.data.push({
		id: `${role.id}_${user.id}`,
		type: 'roleUserAccountAssociation',
	});

	await apiHelpers.jsonWebServicesUser.addGroupUsers(site.id, [user.id]);

	const address = await apiHelpers.headlessCommerceAdminAccount.postAddress(
		account.id
	);

	const catalogs =
		await apiHelpers.headlessCommerceAdminCatalog.getCatalogsPage(siteName);

	const product = await apiHelpers.headlessCommerceAdminCatalog.postProduct({
		catalogId: catalogs.items[0].id,
		productType: 'virtual',
	});

	const productSkus = await apiHelpers.headlessCommerceAdminCatalog
		.getProduct(product.productId)
		.then((product) => {
			return product.skus;
		});

	const sku = productSkus[0];

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
		paymentMethod: 'paypal',
		shippingAddressId: address.id,
		total: 50,
	});

	const payment =
		await apiHelpers.headlessCommerceAdminPaymentApiHelper.postPayment({
			amount: order.total,
			channelId: channel.id,
			currencyCode: 'USD',
			paymentIntegrationType: 1,
			relatedItemId: order.id,
			relatedItemName: 'com.liferay.commerce.model.CommerceOrder',
		});

	await apiHelpers.headlessCommerceAdminPaymentApiHelper.patchPayment(
		{
			paymentStatus: 0,
			relatedItemId: payment.relatedItemId,
		},
		payment.id
	);

	await apiHelpers.headlessCommerceAdminOrder.patchOrder(order.id, {
		orderStatus: '10',
	});

	await apiHelpers.headlessCommerceAdminOrder.patchOrder(order.id, {
		orderStatus: '0',
	});

	await apiHelpers.headlessCommerceReturn.postCommerceReturn({
		channelId: channel.id,
		commerceReturnToCommerceReturnItems: [
			{
				amount: order.total,
				authorized: 1,
				quantity: 1,
				r_accountToCommerceReturnItems_accountEntryId: account.id,
				r_commerceOrderItemToCommerceReturnItems_commerceOrderItemId:
					order.orderItems[0].id,
				r_commerceOrderToCommerceReturns_commerceOrderId: order.id,
				received: 1,
				returnItemStatus: {
					key: 'toBeProccessed',
				},
				returnReason: {
					key: 'changeOfMind',
				},
				returnResolutionMethod: {
					key: 'refund',
				},
			},
		],
		r_accountToCommerceReturns_accountEntryId: account.id,
		r_commerceOrderToCommerceReturns_commerceOrderId: order.id,
		returnStatus: {
			key: 'processing',
		},
	});

	const refund =
		await apiHelpers.headlessCommerceAdminPaymentApiHelper.postPayment({
			amount: payment.amount,
			reasonKey: 'damaged-in-transit',
			relatedItemId: payment.id,
			relatedItemName:
				'com.liferay.commerce.payment.model.CommercePaymentEntry',
			type: 1,
		});

	await apiHelpers.headlessCommerceAdminPaymentApiHelper.patchPayment(
		{
			paymentStatus: 0,
			relatedItemId: refund.relatedItemId,
		},
		refund.id
	);

	await performLogout(page);

	await performLogin(page, user.alternateName);

	await page.goto(`/web/${site.name}/returns`);

	await page.getByLabel('View').click();

	await returnDetailsPage.returnActionsButton.click();

	await expect(
		returnDetailsPage.returnActionsViewRefundsButton
	).toBeVisible();

	await returnDetailsPage.returnActionsViewRefundsButton.click();

	await expect(returnDetailsPage.viewRefundsTitle).toBeVisible();
	await expect(
		returnDetailsPage.viewRefundsFrame.getByText(
			'Showing 1 to 1 of 1 entries.'
		)
	).toBeVisible();
});

test('LPD-41539 Returns Manager users are missing permissions to manage refunds', async ({
	apiHelpers,
	commercePaymentsPage,
	page,
}) => {
	test.setTimeout(180000);

	const siteName = 'minium-' + getRandomInt();

	const {channel, site} = await miniumSetUp(apiHelpers, siteName);

	const account = await apiHelpers.headlessAdminUser.postAccount({
		name: 'Account Business',
		type: 'business',
	});

	const user =
		await apiHelpers.headlessAdminUser.getUserAccountByEmailAddress(
			'demo.unprivileged@liferay.com'
		);

	await apiHelpers.headlessAdminUser.assignUserToAccountByEmailAddress(
		account.id,
		['demo.unprivileged@liferay.com']
	);

	const role =
		await apiHelpers.headlessAdminUser.getRoleByName('Returns Manager');

	await apiHelpers.headlessAdminUser.assignUserToRole(
		role.externalReferenceCode,
		user.id
	);

	await apiHelpers.jsonWebServicesUser.addGroupUsers(site.id, [user.id]);

	const address = await apiHelpers.headlessCommerceAdminAccount.postAddress(
		account.id
	);

	const catalogs =
		await apiHelpers.headlessCommerceAdminCatalog.getCatalogsPage(siteName);

	const product = await apiHelpers.headlessCommerceAdminCatalog.postProduct({
		catalogId: catalogs.items[0].id,
		productType: 'virtual',
	});

	const productSkus = await apiHelpers.headlessCommerceAdminCatalog
		.getProduct(product.productId)
		.then((product) => {
			return product.skus;
		});

	const sku = productSkus[0];

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
		paymentMethod: 'paypal',
		shippingAddressId: address.id,
		total: 50,
	});

	const payment =
		await apiHelpers.headlessCommerceAdminPaymentApiHelper.postPayment({
			amount: order.total,
			channelId: channel.id,
			currencyCode: 'USD',
			paymentIntegrationType: 1,
			relatedItemId: order.id,
			relatedItemName: 'com.liferay.commerce.model.CommerceOrder',
		});

	await apiHelpers.headlessCommerceAdminPaymentApiHelper.patchPayment(
		{
			paymentStatus: 0,
			relatedItemId: payment.relatedItemId,
		},
		payment.id
	);

	await apiHelpers.headlessCommerceAdminOrder.patchOrder(order.id, {
		orderStatus: '10',
	});

	await apiHelpers.headlessCommerceAdminOrder.patchOrder(order.id, {
		orderStatus: '0',
	});

	await apiHelpers.headlessCommerceReturn.postCommerceReturn({
		channelId: channel.id,
		commerceReturnToCommerceReturnItems: [
			{
				amount: order.total,
				authorized: 1,
				quantity: 1,
				r_accountToCommerceReturnItems_accountEntryId: account.id,
				r_commerceOrderItemToCommerceReturnItems_commerceOrderItemId:
					order.orderItems[0].id,
				r_commerceOrderToCommerceReturns_commerceOrderId: order.id,
				received: 1,
				returnItemStatus: {
					key: 'toBeProccessed',
				},
				returnReason: {
					key: 'changeOfMind',
				},
				returnResolutionMethod: {
					key: 'refund',
				},
			},
		],
		r_accountToCommerceReturns_accountEntryId: account.id,
		r_commerceOrderToCommerceReturns_commerceOrderId: order.id,
		returnStatus: {
			key: 'processing',
		},
	});

	await performLogout(page);

	await performLogin(page, 'demo.unprivileged');

	await commercePaymentsPage.goto(false);
	await commercePaymentsPage.makeRefundButton.click();
	await commercePaymentsPage.reasonInput.selectOption('return');
	await commercePaymentsPage.saveButton.click();

	await waitForAlert(page);
});
