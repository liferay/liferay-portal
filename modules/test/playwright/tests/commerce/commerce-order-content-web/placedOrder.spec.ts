/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {applicationsMenuPageTest} from '../../../fixtures/applicationsMenuPageTest';
import {commercePagesTest} from '../../../fixtures/commercePagesTest';
import {dataApiHelpersTest} from '../../../fixtures/dataApiHelpersTest';
import {featureFlagsTest} from '../../../fixtures/featureFlagsTest';
import {loginTest} from '../../../fixtures/loginTest';
import {usersAndOrganizationsPagesTest} from '../../../fixtures/usersAndOrganizationsPagesTest';
import getRandomString from '../../../utils/getRandomString';
import performLogin, {
	performLogout,
	userData,
} from '../../../utils/performLogin';
import {waitForAlert} from '../../../utils/waitForAlert';
import {miniumSetUp} from '../utils/commerce';

export const test = mergeTests(
	applicationsMenuPageTest,
	commercePagesTest,
	dataApiHelpersTest,
	featureFlagsTest({
		'LPS-178052': true,
	}),
	loginTest(),
	usersAndOrganizationsPagesTest
);

test('LPD-25831 Placed orders widget configuration to display full addresses and phone number', async ({
	apiHelpers,
	applicationsMenuPage,
	commerceLayoutsPage,
	page,
	placedOrdersPage,
}) => {
	const site = await apiHelpers.headlessSite.createSite({
		name: getRandomString(),
	});

	apiHelpers.data.push({id: site.id, type: 'site'});

	const channel = await apiHelpers.headlessCommerceAdminChannel.postChannel({
		name: getRandomString(),
		siteGroupId: site.id,
	});

	const catalog = await apiHelpers.headlessCommerceAdminCatalog.postCatalog();

	const product = await apiHelpers.headlessCommerceAdminCatalog.postProduct({
		catalogId: catalog.id,
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

	const phoneNumber = '12345';

	const address = await apiHelpers.headlessCommerceAdminAccount.postAddress(
		account.id,
		{phoneNumber, regionISOCode: 'AL'}
	);

	await apiHelpers.headlessCommerceAdminOrder.postOrder({
		accountId: account.id,
		billingAddressId: address.id,
		channelId: channel.id,
		orderItems: [
			{
				decimalQuantity: 10,
				quantity: 2,
				skuId: sku.id,
			},
		],
		orderStatus: '0',
		paymentStatus: '0',
		shippingAddressId: address.id,
	});

	await applicationsMenuPage.goToSite(site.name);

	await commerceLayoutsPage.goToPages(false);
	await commerceLayoutsPage.createWidgetPage('Placed Orders Page');

	await page.goto(`/web/${site.name}`);

	await placedOrdersPage.addPlacedOrdersWidget();

	await placedOrdersPage.viewButton.click();

	await expect(placedOrdersPage.commerceBillingAddress).not.toContainText(
		'United States'
	);
	await expect(placedOrdersPage.commerceBillingAddress).not.toContainText(
		'Alabama'
	);
	await expect(placedOrdersPage.commerceBillingAddress).not.toContainText(
		phoneNumber
	);
	await expect(placedOrdersPage.commerceShippingAddress).not.toContainText(
		'United States'
	);
	await expect(placedOrdersPage.commerceShippingAddress).not.toContainText(
		'Alabama'
	);
	await expect(placedOrdersPage.commerceShippingAddress).not.toContainText(
		phoneNumber
	);

	await page.goto(`/web/${site.name}`);

	await placedOrdersPage.optionsButton.click();

	await placedOrdersPage.configurationMenuItem.click();
	await placedOrdersPage.configurationIFrameShowFullAddressToggle.check();
	await placedOrdersPage.configurationIFrameShowPhoneNumberToggle.check();
	await placedOrdersPage.configurationIFrameSaveButton.click();
	await waitForAlert(placedOrdersPage.configurationIFrame);
	await page.reload();

	await placedOrdersPage.viewButton.click();

	await expect(placedOrdersPage.commerceBillingAddress).toContainText(
		'United States'
	);
	await expect(placedOrdersPage.commerceBillingAddress).toContainText(
		'Alabama'
	);
	await expect(placedOrdersPage.commerceBillingAddress).toContainText(
		phoneNumber
	);
	await expect(placedOrdersPage.commerceShippingAddress).toContainText(
		'United States'
	);
	await expect(placedOrdersPage.commerceShippingAddress).toContainText(
		'Alabama'
	);
	await expect(placedOrdersPage.commerceShippingAddress).toContainText(
		phoneNumber
	);
});

test('LPD-26643 Reorder from placed orders details page', async ({
	apiHelpers,
	checkoutPage,
	commerceAdminOrderDetailsPage,
	commerceMiniCartPage,
	page,
}) => {
	const account = await apiHelpers.headlessAdminUser.postAccount({
		name: 'admin',
		type: 'business',
	});

	apiHelpers.data.push({id: account.id, type: 'account'});

	const {channel, site} = await miniumSetUp(apiHelpers);

	await apiHelpers.headlessAdminUser.assignUserToAccountByEmailAddress(
		account.id,
		['demo.unprivileged@liferay.com']
	);
	const user =
		await apiHelpers.headlessAdminUser.getUserAccountByEmailAddress(
			'demo.unprivileged@liferay.com'
		);
	const rolesResponse = await apiHelpers.headlessAdminUser.getAccountRoles(
		account.id
	);

	const buyerAccountRole = rolesResponse?.items?.filter((role) => {
		return role.name === 'Buyer';
	});

	await apiHelpers.headlessAdminUser.assignAccountRoles(
		account.externalReferenceCode,
		buyerAccountRole[0].id,
		user.emailAddress
	);

	await apiHelpers.headlessCommerceAdminAccount.postAddress(account.id, {
		phoneNumber: '12345',
		regionISOCode: 'LA',
	});

	const product = await apiHelpers.headlessCommerceAdminCatalog.getProducts(
		new URLSearchParams({
			filter: `name eq 'U-Joint'`,
		})
	);

	const productId = product.items[0].productId;

	const productSkus = await apiHelpers.headlessCommerceAdminCatalog
		.getProduct(productId)
		.then((product) => {
			return product.skus;
		});

	const sku = productSkus[0];

	await apiHelpers.headlessCommerceDeliveryCart.postCart(
		{
			accountId: account.id,
			cartItems: [
				{
					quantity: 1,
					skuId: sku.id,
				},
			],
		},
		channel.id
	);

	const siteRole =
		await apiHelpers.headlessAdminUser.getRoleByName('Site Member');

	await apiHelpers.headlessAdminUser.assignUserToSite(
		siteRole.id,
		site.id,
		user.id
	);

	await performLogout(page);

	await performLogin(page, user.alternateName);

	await page.goto(`/web/${site.name}`);

	await commerceMiniCartPage.submitCart();

	await expect(page.getByText('U-joint')).toBeVisible();

	await checkoutPage.chooseShippingAddress({index: 1});

	await expect(page.getByText('Standard Delivery (+$ 15.00)')).toBeVisible();

	await checkoutPage.continueButton.click();

	await expect(page.getByText('U-joint')).toBeVisible();

	await checkoutPage.continueButton.click();

	await expect(checkoutPage.orderSuccessMessage).toBeVisible();

	await checkoutPage.goToOrderDetailsButton.click();

	await expect(page.getByText('U-joint')).toBeVisible();

	await commerceAdminOrderDetailsPage.reorder();

	await expect(page.getByText('U-joint')).toBeVisible();

	await checkoutPage.chooseShippingAddress({index: 1});

	await expect(page.getByText('Standard Delivery (+$ 15.00)')).toBeVisible();

	await checkoutPage.continueButton.click();

	await expect(page.getByText('U-joint')).toBeVisible();

	await checkoutPage.continueButton.click();

	await expect(checkoutPage.orderSuccessMessage).toBeVisible();
});

test('LPD-32095 A user can search orders by account name', async ({
	apiHelpers,
	applicationsMenuPage,
	commerceAdminChannelsPage,
	commerceLayoutsPage,
	page,
	placedOrdersPage,
}) => {
	const userAccount = await apiHelpers.headlessAdminUser.postUserAccount();

	userData[userAccount.alternateName] = {
		name: userAccount.givenName,
		password: 'test',
		surname: userAccount.familyName,
	};

	const site = await apiHelpers.headlessSite.createSite({
		name: getRandomString(),
	});

	apiHelpers.data.push({id: site.id, type: 'site'});

	const channel = await apiHelpers.headlessCommerceAdminChannel.postChannel({
		name: getRandomString(),
		siteGroupId: site.id,
	});

	await commerceAdminChannelsPage.changeCommerceChannelSiteType(
		channel.name,
		'B2B'
	);

	const catalog = await apiHelpers.headlessCommerceAdminCatalog.postCatalog();

	const product = await apiHelpers.headlessCommerceAdminCatalog.postProduct({
		catalogId: catalog.id,
	});

	const account1 = await apiHelpers.headlessAdminUser.postAccount({
		name: getRandomString(),
		type: 'business',
	});

	apiHelpers.data.push({id: account1.id, type: 'account'});

	await apiHelpers.headlessAdminUser.assignUserToAccountByEmailAddress(
		account1.id,
		[userAccount.emailAddress]
	);

	const rolesResponse1 = await apiHelpers.headlessAdminUser.getAccountRoles(
		account1.id
	);

	const accountRoleBuyer1 = rolesResponse1?.items?.filter((role) => {
		return role.name === 'Buyer';
	});

	await apiHelpers.headlessAdminUser.assignAccountRoles(
		account1.externalReferenceCode,
		accountRoleBuyer1[0].id,
		userAccount.emailAddress
	);

	const account2 = await apiHelpers.headlessAdminUser.postAccount({
		name: getRandomString(),
		type: 'business',
	});

	apiHelpers.data.push({id: account2.id, type: 'account'});

	apiHelpers.headlessAdminUser.assignUserToAccountByEmailAddress(
		account2.id,
		[userAccount.emailAddress]
	);

	await applicationsMenuPage.goToSite(site.name);

	await commerceLayoutsPage.goToPages(false);
	await commerceLayoutsPage.createWidgetPage('Placed Orders Page');

	await page.goto(`/web/${site.name}`);

	await placedOrdersPage.addPlacedOrdersWidget();

	const productSkus = await apiHelpers.headlessCommerceAdminCatalog
		.getProduct(product.productId)
		.then((product) => {
			return product.skus;
		});

	const sku = productSkus[0];

	const phoneNumber = '12345';

	const address1 = await apiHelpers.headlessCommerceAdminAccount.postAddress(
		account1.id,
		{phoneNumber, regionISOCode: 'AL'}
	);

	await apiHelpers.headlessCommerceAdminOrder.postOrder({
		accountId: account1.id,
		billingAddressId: address1.id,
		channelId: channel.id,
		orderItems: [
			{
				decimalQuantity: 10,
				quantity: 2,
				skuId: sku.id,
			},
		],
		orderStatus: '0',
		paymentMethod: 'paypal',
		paymentStatus: '0',
		shippingAddressId: address1.id,
	});

	const address2 = await apiHelpers.headlessCommerceAdminAccount.postAddress(
		account2.id,
		{phoneNumber, regionISOCode: 'AL'}
	);

	await apiHelpers.headlessCommerceAdminOrder.postOrder({
		accountId: account2.id,
		billingAddressId: address2.id,
		channelId: channel.id,
		orderItems: [
			{
				decimalQuantity: 10,
				quantity: 2,
				skuId: sku.id,
			},
		],
		orderStatus: '0',
		paymentMethod: 'paypal',
		paymentStatus: '0',
		shippingAddressId: address2.id,
	});

	await performLogout(page);
	await performLogin(page, userAccount.alternateName);

	await page.goto(`/web/${site.name}`);

	await placedOrdersPage.searchInput.fill(account2.name);
	await placedOrdersPage.searchButton.click();

	await expect(placedOrdersPage.orderAccountName(account1.name)).toHaveCount(
		0
	);
	await expect(placedOrdersPage.orderAccountName(account2.name)).toHaveCount(
		1
	);
});

test('LPD-33783 Placed orders table displays correct fields', async ({
	apiHelpers,
	applicationsMenuPage,
	commerceLayoutsPage,
	page,
	placedOrdersPage,
}) => {
	const site = await apiHelpers.headlessSite.createSite({
		name: 'Placed order',
	});

	apiHelpers.data.push({id: site.id, type: 'site'});

	const channel = await apiHelpers.headlessCommerceAdminChannel.postChannel({
		name: 'Placed order Channel',
		siteGroupId: site.id,
	});

	const account = await apiHelpers.headlessAdminUser.postAccount({
		name: getRandomString(),
		type: 'person',
	});

	apiHelpers.data.push({id: account.id, type: 'account'});

	await apiHelpers.headlessCommerceAdminOrder.postOrder({
		accountId: account.id,
		channelId: channel.id,
		name: 'order1',
		orderStatus: '0',
	});

	await applicationsMenuPage.goToSite(site.name);

	await commerceLayoutsPage.goToPages(false);
	await commerceLayoutsPage.createWidgetPage('Placed Orders Page');

	await page.goto(`/web/${site.name}`);

	await placedOrdersPage.addPlacedOrdersWidget();

	await expect(placedOrdersPage.table).toBeVisible();

	const tableHeaderLabels = [
		'Order ID',
		'Name',
		'Order Type',
		'ERC',
		'Purchase Order Number',
		'Order Date',
		'Account',
		'Submitted By',
		'Status',
		'Amount',
	];

	await expect(await placedOrdersPage.tableHeaders.innerText()).toEqual(
		tableHeaderLabels.join('\n')
	);
});
