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
import {liferayConfig} from '../../../../liferay.config';
import getRandomString from '../../../../utils/getRandomString';
import performLogin, {performLogout} from '../../../../utils/performLogin';
import {waitForAlert} from '../../../../utils/waitForAlert';
import getFragmentDefinition from '../../../layout-content-page-editor-web/main/utils/getFragmentDefinition';
import getPageDefinition from '../../../layout-content-page-editor-web/main/utils/getPageDefinition';
import {miniumSetUp} from '../../utils/commerce';

export const test = mergeTests(
	applicationsMenuPageTest,
	commercePagesTest,
	dataApiHelpersTest,
	featureFlagsTest({
		'LPS-178052': {enabled: true},
	}),
	loginTest()
);

test('COMMERCE-12316 Mini cart bundle with UOM', async ({
	apiHelpers,
	applicationsMenuPage,
	commerceAdminProductPage,
	commerceMiniCartPage,
	page,
}) => {
	const site = await apiHelpers.headlessSite.createSite({
		name: getRandomString(),
	});

	apiHelpers.data.push({id: site.id, type: 'site'});

	const layout = await apiHelpers.headlessDelivery.createSitePage({
		pageDefinition: getPageDefinition([
			getFragmentDefinition({
				id: getRandomString(),
				key: 'COMMERCE_CART_FRAGMENTS-mini-cart',
			}),
		]),
		siteId: site.id,
		title: getRandomString(),
	});

	await apiHelpers.headlessCommerceAdminChannel.postChannel({
		siteGroupId: site.id,
	});

	const option1 = await apiHelpers.headlessCommerceAdminCatalog.postOption(
		'select',
		'color',
		'Color',
		1
	);
	const option2 = await apiHelpers.headlessCommerceAdminCatalog.postOption(
		'select',
		'size',
		'Size',
		2
	);

	const catalog = await apiHelpers.headlessCommerceAdminCatalog.postCatalog({
		name: 'Mini Cart Catalog',
	});

	const product1 = await apiHelpers.headlessCommerceAdminCatalog.postProduct({
		catalogId: catalog.id,
		name: {en_US: 'Product1'},
	});
	const product2 = await apiHelpers.headlessCommerceAdminCatalog.postProduct({
		catalogId: catalog.id,
		name: {en_US: 'Product2'},
	});
	const productBundle =
		await apiHelpers.headlessCommerceAdminCatalog.postProduct({
			catalogId: catalog.id,
			name: {en_US: 'ProductBundle'},
			productOptions: [
				{
					fieldType: 'select',
					key: 'color',
					name: {
						en_US: 'Color',
					},
					optionId: option1.id,
					priceType: 'static',
					priority: 1,
					productOptionValues: [
						{
							deltaPrice: 10.0,
							key: 'black',
							name: {
								en_US: 'Black',
							},
							priority: 1,
							quantity: 1,
							skuId: product1.skus[0].id,
						},
						{
							deltaPrice: 20.0,
							key: 'white',
							name: {
								en_US: 'White',
							},
							priority: 2,
							quantity: 1,
						},
					],
					skuContributor: true,
				},
				{
					fieldType: 'select',
					key: 'size',
					name: {
						en_US: 'Size',
					},
					optionId: option2.id,
					priceType: 'static',
					priority: 2,
					productOptionValues: [
						{
							deltaPrice: 30.0,
							key: 'xs',
							name: {
								en_US: 'XS',
							},
							priority: 1,
							quantity: 1,
						},
						{
							deltaPrice: 40.0,
							key: 'xl',
							name: {
								en_US: 'XL',
							},
							priority: 2,
							quantity: 1,
							skuId: product2.skus[0].id,
						},
					],
					skuContributor: true,
				},
			],
		});

	await applicationsMenuPage.goToProducts();

	await commerceAdminProductPage.managementToolbarSearchInput.fill(
		'ProductBundle'
	);
	await commerceAdminProductPage.managementToolbarSearchInput.press('Enter');
	await commerceAdminProductPage
		.managementToolbarItemLink('ProductBundle')
		.click();
	await commerceAdminProductPage.generateSkus();

	await expect(page.getByText('Showing 1 to 5 of 5 entries.')).toBeVisible();

	const productBundleSkus = await apiHelpers.headlessCommerceAdminCatalog
		.getProduct(productBundle.productId)
		.then((product) => {
			return product.skus;
		});

	const sku1 = productBundleSkus.find(
		(sku) => sku.sku === 'WHITEXL' || sku.sku === 'XLWHITE'
	);
	const sku2 = productBundleSkus.find(
		(sku) => sku.sku === 'BLACKXL' || sku.sku === 'XLBLACK'
	);

	await apiHelpers.headlessCommerceAdminCatalog.postSkuUnitOfMeasure(
		sku1.id,
		{
			incrementalOrderQuantity: 2,
			name: {en_US: 'Pallet'},
			priority: 2,
			rate: 3,
		}
	);
	await apiHelpers.headlessCommerceAdminCatalog.postSkuUnitOfMeasure(
		sku2.id,
		{
			incrementalOrderQuantity: 3,
			name: {en_US: 'Box'},
			primary: true,
			priority: 1,
			rate: 1,
		}
	);

	const sku1SkuUnitOfMeasure =
		await apiHelpers.headlessCommerceAdminCatalog.postSkuUnitOfMeasure(
			sku1.id,
			{
				incrementalOrderQuantity: 3,
				name: {en_US: 'Box'},
				primary: true,
				priority: 1,
				rate: 1,
			}
		);
	const sku2SkuUnitOfMeasure =
		await apiHelpers.headlessCommerceAdminCatalog.postSkuUnitOfMeasure(
			sku2.id,
			{
				incrementalOrderQuantity: 2,
				name: {en_US: 'Package'},
				priority: 2,
				rate: 0.5,
			}
		);

	await page.goto(
		`${liferayConfig.environment.baseUrl}/web${site.friendlyUrlPath}${layout.friendlyUrlPath}`
	);

	await commerceMiniCartPage.miniCartButton.click();
	await commerceMiniCartPage.searchProductsInput.fill(sku1.sku);
	await commerceMiniCartPage
		.quickAddToCartSku(`${sku1.sku} ProductBundle`)
		.click();
	await commerceMiniCartPage.quickAddToCartButton.click();
	await commerceMiniCartPage.showOptionsButton.click();

	await expect(
		page.getByText(sku1SkuUnitOfMeasure.key, {exact: true})
	).toBeVisible();
	await expect(page.getByText('White', {exact: true})).toBeVisible();
	await expect(page.getByText('XL', {exact: true})).toBeVisible();
	await expect(
		page.getByText('$ 60.00', {exact: true}).first()
	).toBeVisible();

	await commerceMiniCartPage.cartItemActionsButton.click();
	await commerceMiniCartPage.editMenuItem.click();

	await expect(commerceMiniCartPage.editOptionsLabel).toBeVisible();
	await expect(commerceMiniCartPage.editQuantityLabel).toBeVisible();
	await expect(commerceMiniCartPage.editUnitOfMeasureLabel).toBeVisible();
	await expect(commerceMiniCartPage.unitOfMeasureTableLabel).toBeVisible();
	await expect(commerceMiniCartPage.miniCartSaveButton).toBeEnabled();

	await expect(page.getByText('Price as Configured$ 60.00')).toBeVisible();

	await expect(
		page.getByRole('cell', {exact: true, name: 'Box'})
	).toBeVisible();
	await expect(
		page.getByRole('cell', {exact: true, name: 'Pallet'})
	).toBeVisible();

	await commerceMiniCartPage.selectOption('XS', 'Size');

	await expect(page.getByText('List Price$ 50.00')).toBeVisible();

	await expect(page.getByText('Price as Configured$ 150.00')).toBeVisible();

	await commerceMiniCartPage.selectOption('Black - $ 10.00', 'Color');

	await expect(page.getByText('List Price$ 40.00')).toBeVisible();

	await expect(page.getByText('Price as Configured$ 120.00')).toBeVisible();

	await expect(commerceMiniCartPage.editUnitOfMeasureLabel).toBeHidden();
	await expect(commerceMiniCartPage.unitOfMeasureTableLabel).toBeHidden();
	await expect(commerceMiniCartPage.miniCartSaveButton).toBeEnabled();

	await commerceMiniCartPage.selectOption('XL + $ 10.00', 'Size');

	await expect(page.getByText('List Price$ 50.00')).toBeVisible();

	await expect(page.getByText('Price as Configured$ 50.00')).toBeVisible();

	await expect(
		page.getByRole('cell', {exact: true, name: 'Package'})
	).toBeVisible();

	await commerceMiniCartPage.miniCartUnitOfMeasureSelector.selectOption(
		sku2SkuUnitOfMeasure.key
	);

	await expect(commerceMiniCartPage.miniCartSaveButton).toBeDisabled();

	await commerceMiniCartPage.editQuantitySelector.fill('6');

	await expect(page.getByText('Price as Configured$ 150.00')).toBeVisible();

	await expect(commerceMiniCartPage.miniCartSaveButton).toBeEnabled();

	await commerceMiniCartPage.miniCartSaveButton.click();
	await commerceMiniCartPage.showOptionsButton.click();

	await expect(
		page.getByText(sku2SkuUnitOfMeasure.key, {exact: true})
	).toBeVisible();
	await expect(page.getByText('Black', {exact: true})).toBeVisible();
	await expect(page.getByText('XL', {exact: true})).toBeVisible();
	await expect(
		page.getByText('$ 150.00', {exact: true}).first()
	).toBeVisible();
});

test('LPD-3496 Mini cart bundle without enough quantity', async ({
	apiHelpers,
	applicationsMenuPage,
	commerceAdminProductPage,
	commerceMiniCartPage,
	page,
}) => {
	const site = await apiHelpers.headlessSite.createSite({
		name: getRandomString(),
	});

	apiHelpers.data.push({id: site.id, type: 'site'});

	const layout = await apiHelpers.headlessDelivery.createSitePage({
		pageDefinition: getPageDefinition([
			getFragmentDefinition({
				id: getRandomString(),
				key: 'COMMERCE_CART_FRAGMENTS-mini-cart',
			}),
		]),
		siteId: site.id,
		title: getRandomString(),
	});

	await apiHelpers.headlessCommerceAdminChannel.postChannel({
		siteGroupId: site.id,
	});

	const option = await apiHelpers.headlessCommerceAdminCatalog.postOption(
		'select',
		'color',
		'Color',
		1
	);

	const catalog = await apiHelpers.headlessCommerceAdminCatalog.postCatalog();

	const product = await apiHelpers.headlessCommerceAdminCatalog.postProduct({
		catalogId: catalog.id,
		productConfiguration: {
			allowBackOrder: false,
		},
	});

	const productBundleName = 'ProductBundle';

	const productBundle =
		await apiHelpers.headlessCommerceAdminCatalog.postProduct({
			catalogId: catalog.id,
			name: {en_US: productBundleName},
			productOptions: [
				{
					fieldType: 'select',
					key: option.key,
					name: option.name,
					optionId: option.id,
					priceType: 'static',
					priority: 1,
					productOptionValues: [
						{
							deltaPrice: 10.0,
							key: 'black',
							name: {
								en_US: 'Black',
							},
							priority: 1,
							quantity: 1,
							skuId: product.skus[0].id,
						},
						{
							deltaPrice: 20.0,
							key: 'white',
							name: {
								en_US: 'White',
							},
							priority: 2,
							quantity: 1,
						},
					],
					skuContributor: true,
				},
			],
		});

	await applicationsMenuPage.goToProducts();

	await commerceAdminProductPage.managementToolbarSearchInput.fill(
		productBundleName
	);
	await commerceAdminProductPage.managementToolbarSearchInput.press('Enter');

	await page
		.getByRole('link', {exact: true, name: productBundleName})
		.click();

	await commerceAdminProductPage.generateSkus();

	await expect(page.getByText('Showing 1 to 3 of 3 entries.')).toBeVisible();

	const productBundleSkus = await apiHelpers.headlessCommerceAdminCatalog
		.getProduct(productBundle.productId)
		.then((product) => {
			return product.skus;
		});

	const sku = productBundleSkus.find((sku) => sku.sku === 'WHITE');

	await page.goto(
		`${liferayConfig.environment.baseUrl}/web${site.friendlyUrlPath}${layout.friendlyUrlPath}`
	);

	await commerceMiniCartPage.miniCartButton.click();
	await commerceMiniCartPage.searchProductsInput.fill(sku.sku);
	await commerceMiniCartPage
		.quickAddToCartSku(`${sku.sku} ${productBundleName}`)
		.click();
	await commerceMiniCartPage.quickAddToCartButton.click();
	await commerceMiniCartPage.cartItemActionsButton.click();
	await commerceMiniCartPage.editMenuItem.click();

	await expect(commerceMiniCartPage.editOptionsLabel).toBeVisible();

	await commerceMiniCartPage.selectOption('Black', 'Color');

	await expect(page.getByLabel('Color')).toBeEnabled();

	await commerceMiniCartPage.miniCartSaveButton.click();

	await expect(page.getByText(/Error.*quantity.*unavailable/)).toBeVisible();
});

test('LPD-26906 Mini cart bundle quantity edit', async ({
	apiHelpers,
	applicationsMenuPage,
	commerceAdminProductPage,
	commerceMiniCartPage,
	page,
}) => {
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

	const user =
		await apiHelpers.headlessAdminUser.getUserAccountByEmailAddress(
			'demo.unprivileged@liferay.com'
		);

	await apiHelpers.headlessAdminUser.assignUserToRole(
		role.externalReferenceCode,
		user.id
	);

	const site = await apiHelpers.headlessSite.createSite({
		name: getRandomString(),
	});

	apiHelpers.data.push({id: site.id, type: 'site'});

	const layout = await apiHelpers.headlessDelivery.createSitePage({
		pageDefinition: getPageDefinition([
			getFragmentDefinition({
				id: getRandomString(),
				key: 'COMMERCE_CART_FRAGMENTS-mini-cart',
			}),
		]),
		siteId: site.id,
		title: getRandomString(),
	});

	await apiHelpers.headlessCommerceAdminChannel.postChannel({
		siteGroupId: site.id,
	});

	const optionKey = getRandomString();

	const option = await apiHelpers.headlessCommerceAdminCatalog.postOption(
		'select',
		optionKey,
		'Color',
		1
	);

	const catalog = await apiHelpers.headlessCommerceAdminCatalog.postCatalog();

	const product = await apiHelpers.headlessCommerceAdminCatalog.postProduct({
		catalogId: catalog.id,
	});

	const productBundle =
		await apiHelpers.headlessCommerceAdminCatalog.postProduct({
			catalogId: catalog.id,
			name: {en_US: getRandomString()},
			productOptions: [
				{
					fieldType: 'select',
					key: optionKey,
					name: {
						en_US: 'Color',
					},
					optionId: option.id,
					priceType: 'static',
					priority: 1,
					productOptionValues: [
						{
							deltaPrice: 10.0,
							key: 'black',
							name: {
								en_US: 'Black',
							},
							priority: 1,
							quantity: 1,
							skuId: product.skus[0].id,
						},
					],
					skuContributor: true,
				},
			],
		});

	await applicationsMenuPage.goToProducts();

	const productBundleName = productBundle.name['en_US'];

	await commerceAdminProductPage.managementToolbarSearchInput.fill(
		productBundleName
	);
	await commerceAdminProductPage.managementToolbarSearchInput.press('Enter');
	await commerceAdminProductPage
		.managementToolbarItemLink(productBundleName)
		.click();
	await commerceAdminProductPage.generateSkus();

	await expect(page.getByText('Showing 1 to 2 of 2 entries.')).toBeVisible();

	await performLogout(page);

	await performLogin(page, user.alternateName);

	await page.goto(
		`${liferayConfig.environment.baseUrl}/web${site.friendlyUrlPath}${layout.friendlyUrlPath}`
	);

	await commerceMiniCartPage.miniCartButton.click();
	await commerceMiniCartPage.searchProductsInput.fill('BLACK');
	await commerceMiniCartPage
		.quickAddToCartSku(`BLACK ${productBundleName}`)
		.click();
	await commerceMiniCartPage.quickAddToCartButton.click();
	await commerceMiniCartPage.showOptionsButton.click();

	await expect(page.getByText('Black', {exact: true})).toBeVisible();
	await expect(
		page.getByText('$ 10.00', {exact: true}).first()
	).toBeVisible();

	await commerceMiniCartPage.editQuantitySelector.fill('2');

	await expect(
		page.getByText('$ 20.00', {exact: true}).first()
	).toBeVisible();
});

test('LPD-45736 Order items are split on the mini cart with quick add to cart when order splitting is enabled', async ({
	apiHelpers,
	commerceAdminChannelsPage,
	commerceMiniCartPage,
	page,
}) => {
	const account = await apiHelpers.headlessAdminUser.postAccount({
		name: getRandomString(),
		type: 'business',
	});

	const {channel, site} = await miniumSetUp(apiHelpers);

	await apiHelpers.headlessCommerceAdminAccount.postAddress(account.id, {
		phoneNumber: '12345',
		regionISOCode: 'LA',
	});

	const product = await apiHelpers.headlessCommerceAdminCatalog.getProducts(
		new URLSearchParams({
			filter: `name eq 'ABS Sensor'`,
		})
	);

	const productName = product.items[0].name['en_US'];

	await page.goto(`/web/${site.name}`);

	await commerceMiniCartPage.miniCartButton.click();
	await commerceMiniCartPage.searchProductsInput.fill(productName);
	await commerceMiniCartPage.quickAddToCartSku(productName).click();
	await commerceMiniCartPage.quickAddToCartButton.click();
	await commerceMiniCartPage.searchProductsInput.fill(productName);
	await commerceMiniCartPage.quickAddToCartSku(productName).click();
	await commerceMiniCartPage.quickAddToCartButton.click();

	await expect(
		page.getByText('$ 100.00', {exact: true}).first()
	).toBeVisible();
	await expect(commerceMiniCartPage.miniCartItem(productName)).toHaveCount(1);

	await commerceAdminChannelsPage.goto();
	await (
		await commerceAdminChannelsPage.channelsTableRowLink(channel.name)
	).click();
	await commerceAdminChannelsPage
		.ordersTabToggle('Show Separate Order Items')
		.click();
	await commerceAdminChannelsPage.headerActionsSaveButton.click();

	await waitForAlert(page);

	await page.goto(`/web/${site.name}`);

	await commerceMiniCartPage.miniCartButton.click();
	await commerceMiniCartPage.searchProductsInput.fill(productName);
	await commerceMiniCartPage.quickAddToCartSku(productName).click();
	await commerceMiniCartPage.quickAddToCartButton.click();

	await expect(
		page.getByText('$ 150.00', {exact: true}).first()
	).toBeVisible();
	await expect(commerceMiniCartPage.miniCartItem(productName)).toHaveCount(2);
});

test('COMMERCE-6348. As a buyer, I want the first selectable quantity of a cart item to be the minimum multiple quantity if Minimum Order Quantity is higher than Multiple Order Quantity', async ({
	apiHelpers,
	commerceMiniCartPage,
	commerceThemeMiniumCatalogPage,
	page,
}) => {
	const {channel, site} = await miniumSetUp(apiHelpers);

	const account = await apiHelpers.headlessAdminUser.postAccount({
		name: getRandomString(),
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

	const product = (
		await apiHelpers.headlessCommerceAdminCatalog.getProducts(
			new URLSearchParams({
				filter: `name eq 'U-Joint'`,
			})
		)
	).items[0];

	const productName = product.name['en_US'];

	await apiHelpers.headlessCommerceAdminCatalog.patchProduct(
		product.productId,
		{
			name: {en_US: productName},
			productConfiguration: {
				minOrderQuantity: 6,
				multipleOrderQuantity: 5,
			},
		}
	);

	const patchedProduct = (
		await apiHelpers.headlessCommerceAdminCatalog.getProducts(
			new URLSearchParams({
				filter: `name eq 'U-Joint'`,
				nestedFields: `skus,productConfiguration`,
			})
		)
	).items[0];

	const cart = await apiHelpers.headlessCommerceDeliveryCart.postCart(
		{
			accountId: account.id,
			cartItems: [
				{
					quantity: 10,
					skuId: patchedProduct.skus[0].id,
				},
			],
			currencyCode: 'USD',
		},
		channel.id
	);

	const multipleQuantity = commerceThemeMiniumCatalogPage.getMultipleQuantity(
		0,
		patchedProduct.productConfiguration.multipleOrderQuantity
	);
	const minQuantity = commerceThemeMiniumCatalogPage.getProductMinQuantity(
		patchedProduct.productConfiguration.minOrderQuantity,
		multipleQuantity
	);
	const maxQuantity = commerceThemeMiniumCatalogPage.getProductMaxQuantity(
		patchedProduct.productConfiguration.maxOrderQuantity,
		multipleQuantity
	);

	await performLogout(page);
	await performLogin(page, user.alternateName);

	await page.goto(`/web/${site.name}`);

	await commerceMiniCartPage.miniCartButton.click();

	await expect(
		commerceThemeMiniumCatalogPage.quantitySelector(
			commerceMiniCartPage.miniCartItem(productName)
		)
	).toHaveValue(cart.cartItems[0].quantity.toString());

	let minQuantityNotSatisfied;
	let multipleQuantityNotSatisfied;
	let maxQuantityNotSatisfied;

	for (const quantitySelectorActualQuantity of [5, 20]) {
		await commerceThemeMiniumCatalogPage
			.quantitySelector(commerceMiniCartPage.miniCartItem(productName))
			.fill(`${quantitySelectorActualQuantity}`);

		maxQuantityNotSatisfied = quantitySelectorActualQuantity > maxQuantity;
		minQuantityNotSatisfied = quantitySelectorActualQuantity < minQuantity;
		multipleQuantityNotSatisfied = !Number.isInteger(
			quantitySelectorActualQuantity / multipleQuantity
		);

		if (quantitySelectorActualQuantity === 5) {
			await expect(
				commerceMiniCartPage.miniCartInvalidQuantityMessage
			).toBeVisible();
		}

		await commerceThemeMiniumCatalogPage.checkQuantitiesInPopOverMessages(
			maxQuantity,
			minQuantity,
			multipleQuantity,
			maxQuantityNotSatisfied,
			minQuantityNotSatisfied,
			multipleQuantityNotSatisfied
		);
	}
});

test('COMMERCE-12370. As a buyer I can add to cart a SKU with single UOM', async ({
	apiHelpers,
	commerceMiniCartPage,
	commerceThemeMiniumCatalogPage,
	page,
	productDetailsPage,
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

	const product = (
		await apiHelpers.headlessCommerceAdminCatalog.getProducts(
			new URLSearchParams({
				filter: `name eq 'Abs Sensor'`,
				nestedFields: `skus,productConfiguration`,
			})
		)
	).items[0];

	const productName = product.name['en_US'];

	await apiHelpers.headlessCommerceAdminCatalog.postSkuUnitOfMeasure(
		product.skus[0].id,
		{
			active: false,
			name: {en_US: 'UOM1'},
			priority: 0,
		}
	);

	const skuUOM2 =
		await apiHelpers.headlessCommerceAdminCatalog.postSkuUnitOfMeasure(
			product.skus[0].id,
			{
				basePrice: 25,
				incrementalOrderQuantity: 0.6,
				name: {en_US: 'UOM2'},
				precision: 1,
				priority: 0,
			}
		);

	const multipleQuantity = commerceThemeMiniumCatalogPage.getMultipleQuantity(
		skuUOM2.incrementalOrderQuantity,
		product.productConfiguration.multipleOrderQuantity,
		skuUOM2.precision
	);
	const maxQuantity = commerceThemeMiniumCatalogPage.getProductMaxQuantity(
		product.productConfiguration.maxOrderQuantity,
		multipleQuantity,
		skuUOM2.precision
	);
	const minQuantity = commerceThemeMiniumCatalogPage.getProductMinQuantity(
		product.productConfiguration.minOrderQuantity,
		multipleQuantity,
		skuUOM2.precision
	);

	await performLogout(page);
	await performLogin(page, 'demo.unprivileged');

	await page.goto(`/web/${site.name}`);

	await commerceThemeMiniumCatalogPage.selectSorting('Name Ascending');

	await expect(
		commerceThemeMiniumCatalogPage.productCardAddToCartButton(productName)
	).not.toHaveClass(/not-allowed/);

	await expect(
		commerceThemeMiniumCatalogPage.quantitySelector(
			commerceThemeMiniumCatalogPage.productCard(productName)
		)
	).toHaveValue(`${minQuantity}`);

	await expect(
		commerceThemeMiniumCatalogPage.quantitySelectorErrorContainer(
			commerceThemeMiniumCatalogPage.productCard(productName)
		)
	).not.toHaveClass(/has-error/);

	await commerceThemeMiniumCatalogPage
		.quantitySelector(
			commerceThemeMiniumCatalogPage.productCard(productName)
		)
		.focus();

	await commerceThemeMiniumCatalogPage.checkQuantitiesInPopOverMessages(
		maxQuantity,
		minQuantity,
		multipleQuantity
	);

	try {
		await commerceThemeMiniumCatalogPage.addToCart(productName);

		await commerceMiniCartPage.miniCartButton.click();

		await expect(
			commerceMiniCartPage.miniCartItem(productName)
		).toBeVisible();

		await expect(page.getByText(skuUOM2.key, {exact: true})).toBeVisible();

		await expect(commerceMiniCartPage.miniCartTotalPrice).toHaveText(
			'$ 125.00'
		);

		await page.goto(`/web/${site.name}/p/` + productName);

		await expect(page.locator('select')).toHaveAttribute('disabled');

		await commerceThemeMiniumCatalogPage
			.quantitySelector(page.locator('.product-detail'))
			.fill('1.2');

		await expect(
			commerceThemeMiniumCatalogPage.quantitySelectorErrorContainer(
				page.locator('.product-detail')
			)
		).toHaveClass(/has-error/);
		await expect(productDetailsPage.addToCartButton).toHaveClass(
			/not-allowed/
		);

		const maxQuantityNotSatisfied = false;
		const minQuantityNotSatisfied = true;
		const multipleQuantityNotSatisfied = false;

		await commerceThemeMiniumCatalogPage.checkQuantitiesInPopOverMessages(
			maxQuantity,
			minQuantity,
			multipleQuantity,
			maxQuantityNotSatisfied,
			minQuantityNotSatisfied,
			multipleQuantityNotSatisfied
		);

		await commerceThemeMiniumCatalogPage
			.quantitySelector(page.locator('.product-detail'))
			.fill(`${minQuantity}`);

		await expect(
			commerceThemeMiniumCatalogPage.quantitySelector(
				page.locator('.product-detail')
			)
		).toHaveValue(`${minQuantity}`);

		await commerceThemeMiniumCatalogPage.checkQuantitiesInPopOverMessages(
			maxQuantity,
			minQuantity,
			multipleQuantity
		);

		await productDetailsPage.addToCartButton.click();

		await commerceMiniCartPage.miniCartButton.click();

		await expect(
			commerceMiniCartPage.miniCartItem(productName)
		).toBeVisible();

		await expect(
			commerceThemeMiniumCatalogPage.quantitySelector(
				commerceMiniCartPage.miniCartItem(productName)
			)
		).toHaveValue(`${minQuantity * 2}`);

		await expect(page.getByText(skuUOM2.key, {exact: true})).toBeVisible();

		await expect(commerceMiniCartPage.miniCartTotalPrice).toHaveText(
			'$ 250.00'
		);
	}
	finally {
		const orders =
			await apiHelpers.headlessCommerceAdminOrder.getOrdersPage();

		apiHelpers.data.push({id: orders.items[0].id, type: 'order'});
	}
});
