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
import getRandomString from '../../../../utils/getRandomString';
import performLogin, {performLogout} from '../../../../utils/performLogin';
import {miniumSetUp} from '../../utils/commerce';

export const test = mergeTests(
	apiHelpersTest,
	applicationsMenuPageTest,
	commercePagesTest,
	dataApiHelpersTest,
	loginTest()
);

test('LPD-29997 Search for products by typing different specification values in global search', async ({
	apiHelpers,
	applicationsMenuPage,
	commerceThemeMiniumCatalogPage,
}) => {
	const {site} = await miniumSetUp(apiHelpers);

	await applicationsMenuPage.goToSite(site.name);

	await commerceThemeMiniumCatalogPage.focusGlobalSearchBarInput();
	await commerceThemeMiniumCatalogPage.search('Plastic');

	await expect(
		commerceThemeMiniumCatalogPage.globalSearchBarCommerceItemLink(
			'Timing Chain Tensioner'
		)
	).toBeVisible();

	await commerceThemeMiniumCatalogPage.clearSearchButton.click();
	await commerceThemeMiniumCatalogPage.search('Plastic, Ceramic');

	await expect(
		commerceThemeMiniumCatalogPage.globalSearchBarCommerceItemLink(
			'Timing Chain Tensioner'
		)
	).toBeVisible();
	await expect(
		commerceThemeMiniumCatalogPage.globalSearchBarCommerceItemLink(
			'Premium Brake Pads'
		)
	).toBeVisible();
});

test('LPD-30191 Search for products by typing different SKUs in global search', async ({
	apiHelpers,
	applicationsMenuPage,
	commerceThemeMiniumCatalogPage,
}) => {
	const {site} = await miniumSetUp(apiHelpers);

	await applicationsMenuPage.goToSite(site.name);

	await commerceThemeMiniumCatalogPage.focusGlobalSearchBarInput();
	await commerceThemeMiniumCatalogPage.search('MIN93015');

	await expect(
		commerceThemeMiniumCatalogPage.globalSearchBarCommerceItemLink(
			'ABS Sensor Product designed'
		)
	).toBeVisible();

	await commerceThemeMiniumCatalogPage.clearSearchButton.click();
	await commerceThemeMiniumCatalogPage.search('MIN93015 MIN55861');

	await expect(
		commerceThemeMiniumCatalogPage.globalSearchBarCommerceItemLink(
			'ABS Sensor Product designed'
		)
	).toBeVisible();
	await expect(
		commerceThemeMiniumCatalogPage.globalSearchBarCommerceItemLink(
			'U-Joint Product designed'
		)
	).toBeVisible();
});

test('LPD-30370 Search for all orders by typing user email in global search', async ({
	apiHelpers,
	applicationsMenuPage,
	commerceThemeMiniumCatalogPage,
}) => {
	const {channel, site} = await miniumSetUp(apiHelpers);

	const account = await apiHelpers.headlessAdminUser.postAccount({
		name: getRandomString(),
		type: 'business',
	});

	await apiHelpers.headlessAdminUser.assignUserToAccountByEmailAddress(
		account.id,
		['test@liferay.com']
	);

	const openOrder = await apiHelpers.headlessCommerceDeliveryCart.postCart(
		{
			accountId: account.id,
		},
		channel.id
	);

	const product = (
		await apiHelpers.headlessCommerceAdminCatalog.getProducts(
			new URLSearchParams({
				filter: `name eq 'ABS Sensor'`,
			})
		)
	).items[0];

	const productSkus = await apiHelpers.headlessCommerceAdminCatalog
		.getProduct(product.productId)
		.then((product) => {
			return product.skus;
		});

	const sku = productSkus[0];

	const address = await apiHelpers.headlessCommerceAdminAccount.postAddress(
		account.id,
		{phoneNumber: '12345', regionISOCode: 'AL'}
	);

	const placedOrder = await apiHelpers.headlessCommerceAdminOrder.postOrder({
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
		paymentMethod: 'paypal',
		paymentStatus: '0',
		shippingAddressId: address.id,
	});

	await applicationsMenuPage.goToSite(site.name);

	await commerceThemeMiniumCatalogPage.focusGlobalSearchBarInput();
	await commerceThemeMiniumCatalogPage.search('test@liferay.com');

	await expect(
		commerceThemeMiniumCatalogPage.globalSearchBarCommerceItemLink(
			String(openOrder.id)
		)
	).toBeVisible();
	await expect(
		commerceThemeMiniumCatalogPage.globalSearchBarCommerceItemLink(
			String(placedOrder.id)
		)
	).toBeVisible();
});

test('LPD-3185 Search a catalog entry using global search, click on a suggested entry and get redirected to that product details page', async ({
	apiHelpers,
	applicationsMenuPage,
	commerceThemeMiniumCatalogPage,
	productDetailsPage,
}) => {
	const {site} = await miniumSetUp(apiHelpers);

	await applicationsMenuPage.goToSite(site.name);

	await commerceThemeMiniumCatalogPage.focusGlobalSearchBarInput();
	await commerceThemeMiniumCatalogPage.search('A');

	await expect(
		commerceThemeMiniumCatalogPage.globalSearchBarCommerceItemLink(
			'A Product designed'
		)
	).toHaveCount(0);

	await commerceThemeMiniumCatalogPage.search('ABS Sensor');

	await expect(
		commerceThemeMiniumCatalogPage.globalSearchBarCommerceItemLink(
			'ABS Sensor Product designed'
		)
	).toBeVisible();
	await expect(
		commerceThemeMiniumCatalogPage.globalSearchBarCommerceItemLink(
			'Wear Sensors Product designed'
		)
	).toBeVisible();

	await commerceThemeMiniumCatalogPage.clearSearchButton.click();
	await commerceThemeMiniumCatalogPage.search(`"ABS Sensor"`);

	await expect(
		commerceThemeMiniumCatalogPage.globalSearchBarCommerceItemLink(
			'ABS Sensor Product designed'
		)
	).toHaveCount(1);

	await commerceThemeMiniumCatalogPage
		.globalSearchBarCommerceItemLink('ABS Sensor Product designed')
		.click();

	await expect(
		await productDetailsPage.productNameHeading('ABS Sensor')
	).toBeVisible();
});

test('COMMERCE-6322 As a buyer, I want to be able to search an entry in Catalog using Global Search and I want the results to be visible in Search Results widget', async ({
	apiHelpers,
	commerceThemeMiniumCatalogPage,
	page,
}) => {
	const {site} = await miniumSetUp(apiHelpers);

	const account = await apiHelpers.headlessAdminUser.postAccount({
		name: getRandomString(),
		type: 'business',
	});

	const user =
		await apiHelpers.headlessAdminUser.getUserAccountByEmailAddress(
			'demo.unprivileged@liferay.com'
		);
	const rolesResponse = await apiHelpers.headlessAdminUser.getAccountRoles(
		account.id
	);

	const accountRoleBuyer = rolesResponse?.items?.filter((role) => {
		return role.name === 'Buyer';
	});

	await apiHelpers.headlessAdminUser.assignAccountRoles(
		account.externalReferenceCode,
		accountRoleBuyer[0].id,
		user.emailAddress
	);
	await apiHelpers.headlessAdminUser.assignUserToAccountByEmailAddress(
		account.id,
		['demo.unprivileged@liferay.com']
	);

	const siteRole =
		await apiHelpers.headlessAdminUser.getRoleByName('Site Member');

	await apiHelpers.headlessAdminUser.assignUserToSite(
		siteRole.id,
		site.id,
		user.id
	);

	await performLogout(page);
	await performLogin(page, 'demo.unprivileged');

	await page.goto(`/web/${site.name}`);

	await commerceThemeMiniumCatalogPage.catalogSearch.click();
	await commerceThemeMiniumCatalogPage.catalogSearch.fill('U-Joint');
	await commerceThemeMiniumCatalogPage.catalogSearch.press('Enter');

	await expect(
		commerceThemeMiniumCatalogPage.productLink('U-Joint')
	).toBeVisible();
	await expect(
		commerceThemeMiniumCatalogPage.productLink('Ball Joints')
	).toBeVisible();
});

test('COMMERCE-6326 As a buyer, I want to be able to search an entry in All Content using Global Search and the results should be visible on Search page', async ({
	apiHelpers,
	commerceThemeMiniumCatalogPage,
	page,
}) => {
	const {site} = await miniumSetUp(apiHelpers);

	const account = await apiHelpers.headlessAdminUser.postAccount({
		name: getRandomString(),
		type: 'business',
	});

	const user =
		await apiHelpers.headlessAdminUser.getUserAccountByEmailAddress(
			'demo.unprivileged@liferay.com'
		);
	const rolesResponse = await apiHelpers.headlessAdminUser.getAccountRoles(
		account.id
	);

	const accountRoleBuyer = rolesResponse?.items?.filter((role) => {
		return role.name === 'Buyer';
	});

	await apiHelpers.headlessAdminUser.assignAccountRoles(
		account.externalReferenceCode,
		accountRoleBuyer[0].id,
		user.emailAddress
	);
	await apiHelpers.headlessAdminUser.assignUserToAccountByEmailAddress(
		account.id,
		['demo.unprivileged@liferay.com']
	);

	const siteRole =
		await apiHelpers.headlessAdminUser.getRoleByName('Site Member');

	await apiHelpers.headlessAdminUser.assignUserToSite(
		siteRole.id,
		site.id,
		user.id
	);

	await performLogout(page);
	await performLogin(page, 'demo.unprivileged');

	await page.goto(`/web/${site.name}`);

	await commerceThemeMiniumCatalogPage.focusGlobalSearchBarInput();
	await commerceThemeMiniumCatalogPage.search('U-Joint');

	await expect(
		commerceThemeMiniumCatalogPage.globalSearchBarCommerceItemLink(
			'Search U-Joint in All Content'
		)
	).toBeVisible();

	await commerceThemeMiniumCatalogPage
		.globalSearchBarCommerceItemLink('Search U-Joint in All Content')
		.click();

	await expect(
		commerceThemeMiniumCatalogPage.productLink('U-Joint')
	).toBeVisible();
	await expect(
		commerceThemeMiniumCatalogPage.productLink('Ball Joints')
	).toBeVisible();
});

test('COMMERCE-6321 As a buyer, I want to be able to search an Orders entry using Global Search and I want to be able to click on a suggested entry and get redirected to that order details page', async ({
	apiHelpers,
	commerceThemeMiniumCatalogPage,
	page,
}) => {
	const account = await apiHelpers.headlessAdminUser.postAccount({
		name: getRandomString(),
		type: 'business',
	});

	const user =
		await apiHelpers.headlessAdminUser.getUserAccountByEmailAddress(
			'demo.unprivileged@liferay.com'
		);
	const rolesResponse = await apiHelpers.headlessAdminUser.getAccountRoles(
		account.id
	);

	const accountRoleBuyer = rolesResponse?.items?.filter((role) => {
		return role.name === 'Buyer';
	});

	await apiHelpers.headlessAdminUser.assignAccountRoles(
		account.externalReferenceCode,
		accountRoleBuyer[0].id,
		user.emailAddress
	);
	await apiHelpers.headlessAdminUser.assignUserToAccountByEmailAddress(
		account.id,
		['demo.unprivileged@liferay.com']
	);

	const siteRole =
		await apiHelpers.headlessAdminUser.getRoleByName('Site Member');

	const {site} = await miniumSetUp(apiHelpers);

	await apiHelpers.headlessAdminUser.assignUserToSite(
		siteRole.id,
		site.id,
		user.id
	);

	const channel = (
		await apiHelpers.headlessCommerceAdminChannel.getChannelsPage(site.name)
	).items[0];

	const order = await apiHelpers.headlessCommerceAdminOrder.postOrder({
		accountId: account.id,
		channelId: channel.id,
		name: 'order1',
		orderStatus: '1',
	});

	await performLogout(page);
	await performLogin(page, 'demo.unprivileged');

	await page.goto(`/web/${site.name}`);

	await commerceThemeMiniumCatalogPage.focusGlobalSearchBarInput();
	await commerceThemeMiniumCatalogPage.search(`${order.id}`);

	await expect(
		commerceThemeMiniumCatalogPage.globalSearchBarCommerceOrderLink(
			`${order.id}`,
			`${account.name}`
		)
	).toBeVisible();

	await commerceThemeMiniumCatalogPage.clearSearchButton.click();
	await commerceThemeMiniumCatalogPage.search(`${user.emailAddress}`);

	await expect(
		commerceThemeMiniumCatalogPage.globalSearchBarCommerceOrderLink(
			`${order.id}`,
			`${account.name}`
		)
	).toBeVisible();

	await commerceThemeMiniumCatalogPage
		.globalSearchBarCommerceOrderLink(`${order.id}`, `${account.name}`)
		.click();

	await expect(page.getByText(`Order Id ${order.id}`)).toBeVisible();
});

test('COMMERCE-6329 As a buyer, I want to search for products in Catalog by typing different Categories in Global Search and I want to see the products with that categories in the suggestions even with multiple categories', async ({
	apiHelpers,
	commerceThemeMiniumCatalogPage,
	page,
}) => {
	const {site} = await miniumSetUp(apiHelpers);

	const account = await apiHelpers.headlessAdminUser.postAccount({
		name: getRandomString(),
		type: 'business',
	});

	const user =
		await apiHelpers.headlessAdminUser.getUserAccountByEmailAddress(
			'demo.unprivileged@liferay.com'
		);
	const rolesResponse = await apiHelpers.headlessAdminUser.getAccountRoles(
		account.id
	);

	const accountRoleBuyer = rolesResponse?.items?.filter((role) => {
		return role.name === 'Buyer';
	});

	await apiHelpers.headlessAdminUser.assignAccountRoles(
		account.externalReferenceCode,
		accountRoleBuyer[0].id,
		user.emailAddress
	);
	await apiHelpers.headlessAdminUser.assignUserToAccountByEmailAddress(
		account.id,
		['demo.unprivileged@liferay.com']
	);

	const siteRole =
		await apiHelpers.headlessAdminUser.getRoleByName('Site Member');

	await apiHelpers.headlessAdminUser.assignUserToSite(
		siteRole.id,
		site.id,
		user.id
	);

	const exhaustSystemList = [
		'Lift Support Product designed',
		'Muffler/Resonators Product designed',
		'Exhaust Clamps Product designed',
		'Catalytic Converters Product designed',
	];

	await performLogout(page);
	await performLogin(page, 'demo.unprivileged');

	await page.goto(`/web/${site.name}`);

	await commerceThemeMiniumCatalogPage.focusGlobalSearchBarInput();
	await commerceThemeMiniumCatalogPage.search('Exhaust System');

	for (let i = 0; i < exhaustSystemList.length; i++) {
		await expect(
			commerceThemeMiniumCatalogPage.globalSearchBarCommerceItemLink(
				exhaustSystemList[i]
			)
		).toBeVisible();
	}

	await commerceThemeMiniumCatalogPage.clearSearchButton.click();
	await commerceThemeMiniumCatalogPage.search('Exhaust System, Engine');

	for (let i = 0; i < exhaustSystemList.length; i++) {
		await expect(
			commerceThemeMiniumCatalogPage.globalSearchBarCommerceItemLink(
				exhaustSystemList[i]
			)
		).toBeVisible();
	}

	await expect(
		commerceThemeMiniumCatalogPage.globalSearchBarCommerceItemLink(
			'Engine Mount Product designed'
		)
	).toBeVisible();
});
