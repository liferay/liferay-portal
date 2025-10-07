/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {apiHelpersTest} from '../../../../fixtures/apiHelpersTest';
import {applicationsMenuPageTest} from '../../../../fixtures/applicationsMenuPageTest';
import {commercePagesTest} from '../../../../fixtures/commercePagesTest';
import {dataApiHelpersTest} from '../../../../fixtures/dataApiHelpersTest';
import {isolatedSiteTest} from '../../../../fixtures/isolatedSiteTest';
import {loginTest} from '../../../../fixtures/loginTest';
import {getRandomInt} from '../../../../utils/getRandomInt';
import getRandomString from '../../../../utils/getRandomString';
import performLogin, {performLogout} from '../../../../utils/performLogin';
import {waitForAlert} from '../../../../utils/waitForAlert';
import {miniumSetUp} from '../../utils/commerce';

export const test = mergeTests(
	apiHelpersTest,
	applicationsMenuPageTest,
	commercePagesTest,
	isolatedSiteTest,
	dataApiHelpersTest,
	loginTest()
);

test('LPD-33466 User can update pricing quantity of UOM', async ({
	apiHelpers,
	applicationsMenuPage,
	commerceAdminProductDetailsPage,
	commerceAdminProductDetailsSkusPage,
	commerceAdminProductPage,
}) => {
	const catalog = await apiHelpers.headlessCommerceAdminCatalog.postCatalog({
		name: 'Catalog',
	});

	const product = await apiHelpers.headlessCommerceAdminCatalog.postProduct({
		catalogId: catalog.id,
		name: {
			en_US: 'Product',
		},
	});

	const productSkus = await apiHelpers.headlessCommerceAdminCatalog
		.getProduct(product.productId)
		.then((product) => {
			return product.skus;
		});

	const sku = productSkus[0];

	await apiHelpers.headlessCommerceAdminCatalog.postSkuUnitOfMeasure(sku.id, {
		incrementalOrderQuantity: 1.22,
		name: {en_US: 'UOM'},
		precision: 2,
		pricingQuantity: 1.0,
		priority: 0,
	});

	await applicationsMenuPage.goToProducts();

	await commerceAdminProductPage.managementToolbarSearchInput.fill('Product');
	await commerceAdminProductPage.managementToolbarSearchInput.press('Enter');

	await commerceAdminProductPage.productsTableRowLink('Product').click();

	await commerceAdminProductDetailsPage.goToProductSkus();

	await commerceAdminProductDetailsSkusPage
		.skusTableRowLink(`${sku.sku}`)
		.click();

	await commerceAdminProductDetailsSkusPage.goToSkuUOM();

	await commerceAdminProductDetailsSkusPage.uomTableRowLink('UOM').click();

	await expect(
		commerceAdminProductDetailsSkusPage.pricinQuantity
	).toHaveValue('1.0');

	await commerceAdminProductDetailsSkusPage.pricinQuantity.fill('2');

	await commerceAdminProductDetailsSkusPage.sidePanelNestedSaveButton.click();

	await waitForAlert(
		commerceAdminProductDetailsSkusPage.skuUOMFrame.frameLocator('iframe')
	);

	await commerceAdminProductDetailsSkusPage.skuUOMFrameCancelButton.click();

	await commerceAdminProductDetailsSkusPage.uomTableRowLink('UOM').click();

	await expect(
		commerceAdminProductDetailsSkusPage.pricinQuantity
	).toHaveValue('2.0');
});

test('LPD-36797 Quantity selector starting quantity in catalog page and minicart is correct when UOM is set with decimal base unit quantity and decimal multiple order quantity', async ({
	apiHelpers,
	commerceMiniCartPage,
	commerceThemeMiniumCatalogPage,
	page,
}) => {
	test.setTimeout(120000);

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

	const product1 = (
		await apiHelpers.headlessCommerceAdminCatalog.getProducts(
			new URLSearchParams({
				filter: `name eq 'U-Joint'`,
			})
		)
	).items[0];

	const productName1 = product1.name['en_US'];

	await apiHelpers.headlessCommerceAdminCatalog.patchProduct(
		product1.productId,
		{
			name: {en_US: productName1},
			productConfiguration: {
				multipleOrderQuantity: 0.1,
			},
		}
	);

	const patchedProduct1 = (
		await apiHelpers.headlessCommerceAdminCatalog.getProducts(
			new URLSearchParams({
				filter: `name eq 'U-Joint'`,
				nestedFields: `skus,productConfiguration`,
			})
		)
	).items[0];

	const skuUOM1 =
		await apiHelpers.headlessCommerceAdminCatalog.postSkuUnitOfMeasure(
			patchedProduct1.skus[0].id,
			{
				basePrice: patchedProduct1.price,
				incrementalOrderQuantity: 0.3,
				name: {en_US: 'UOM'},
				precision: 1,
				priority: 0,
			}
		);

	const patchedProduct2 = (
		await apiHelpers.headlessCommerceAdminCatalog.getProducts(
			new URLSearchParams({
				filter: `name eq 'Transmission Cooler Line Assembly'`,
				nestedFields: `skus,productConfiguration`,
			})
		)
	).items[0];

	const productName2 = patchedProduct2.name['en_US'];

	const skuUOM2 =
		await apiHelpers.headlessCommerceAdminCatalog.postSkuUnitOfMeasure(
			patchedProduct2.skus[0].id,
			{
				basePrice: patchedProduct2.price,
				incrementalOrderQuantity: 0.3,
				name: {en_US: 'UOM'},
				precision: 1,
				priority: 0,
			}
		);

	await performLogout(page);
	await performLogin(page, 'demo.unprivileged');

	const multipleQuantity1 =
		commerceThemeMiniumCatalogPage.getMultipleQuantity(
			skuUOM1.incrementalOrderQuantity,
			patchedProduct1.productConfiguration.multipleOrderQuantity,
			skuUOM1.precision
		);
	const minQuantity1 = commerceThemeMiniumCatalogPage.getProductMinQuantity(
		patchedProduct1.productConfiguration.minOrderQuantity,
		multipleQuantity1,
		skuUOM1.precision
	);
	const maxQuantity1 = commerceThemeMiniumCatalogPage.getProductMaxQuantity(
		patchedProduct1.productConfiguration.maxOrderQuantity,
		multipleQuantity1,
		skuUOM1.precision
	);

	await page.goto(`/web/${site.name}`);

	await expect(
		commerceThemeMiniumCatalogPage.quantitySelector(
			commerceThemeMiniumCatalogPage.productCard(productName1)
		)
	).toHaveValue(`${minQuantity1}`);

	await commerceThemeMiniumCatalogPage
		.quantitySelector(
			commerceThemeMiniumCatalogPage.productCard(productName1)
		)
		.focus();

	await expect(
		commerceThemeMiniumCatalogPage.quantitySelector(
			commerceThemeMiniumCatalogPage.productCard(productName1)
		)
	).toBeFocused();

	await commerceThemeMiniumCatalogPage.checkQuantitiesInPopOverMessages(
		maxQuantity1,
		minQuantity1,
		multipleQuantity1
	);

	const multipleQuantity2 =
		commerceThemeMiniumCatalogPage.getMultipleQuantity(
			skuUOM2.incrementalOrderQuantity,
			patchedProduct2.productConfiguration.multipleOrderQuantity,
			skuUOM2.precision
		);
	const minQuantity2 = commerceThemeMiniumCatalogPage.getProductMinQuantity(
		patchedProduct2.productConfiguration.minOrderQuantity,
		multipleQuantity2,
		skuUOM2.precision
	);
	const maxQuantity2 = commerceThemeMiniumCatalogPage.getProductMaxQuantity(
		patchedProduct2.productConfiguration.maxOrderQuantity,
		multipleQuantity2,
		skuUOM2.precision
	);

	await expect(
		commerceThemeMiniumCatalogPage.quantitySelector(
			commerceThemeMiniumCatalogPage.productCard(productName2)
		)
	).toHaveValue(`${minQuantity2}`);

	await commerceThemeMiniumCatalogPage
		.quantitySelector(
			commerceThemeMiniumCatalogPage.productCard(productName2)
		)
		.focus();

	await expect(
		commerceThemeMiniumCatalogPage.quantitySelector(
			commerceThemeMiniumCatalogPage.productCard(productName2)
		)
	).toBeFocused();

	await commerceThemeMiniumCatalogPage.checkQuantitiesInPopOverMessages(
		maxQuantity2,
		minQuantity2,
		multipleQuantity2
	);

	try {
		await commerceThemeMiniumCatalogPage.addToCart(productName1);

		await commerceMiniCartPage.miniCartButton.click();

		await expect(
			commerceThemeMiniumCatalogPage.quantitySelector(
				commerceMiniCartPage.miniCartItem(productName1)
			)
		).toHaveValue(`${minQuantity1}`);

		await commerceMiniCartPage.miniCartButtonClose.click();

		await expect(
			commerceThemeMiniumCatalogPage.productCardAddToCartButton(
				productName2
			)
		).toBeVisible();

		await commerceThemeMiniumCatalogPage.addToCart(productName2);

		await commerceMiniCartPage.miniCartButton.click();

		await expect(
			commerceThemeMiniumCatalogPage.quantitySelector(
				commerceMiniCartPage.miniCartItem(productName2)
			)
		).toHaveValue(`${minQuantity2}`);

		await commerceThemeMiniumCatalogPage
			.quantitySelector(commerceMiniCartPage.miniCartItem(productName1))
			.focus();
		await commerceThemeMiniumCatalogPage.checkQuantitiesInPopOverMessages(
			maxQuantity1,
			minQuantity1,
			multipleQuantity1
		);

		await expect(commerceMiniCartPage.miniCartTotalPrice).toHaveText(
			'$ 246.00'
		);

		await commerceThemeMiniumCatalogPage
			.quantitySelector(commerceMiniCartPage.miniCartItem(productName2))
			.focus();
		await commerceThemeMiniumCatalogPage.checkQuantitiesInPopOverMessages(
			maxQuantity2,
			minQuantity2,
			multipleQuantity2
		);

		await expect(commerceMiniCartPage.miniCartTotalPrice).toHaveText(
			'$ 246.00'
		);

		await commerceMiniCartPage.miniCartButtonClose.click();

		await commerceThemeMiniumCatalogPage
			.productCard(productName1)
			.getByRole('link')
			.first()
			.click();

		await expect(
			commerceThemeMiniumCatalogPage.quantitySelector(
				page.locator('.product-detail')
			)
		).toHaveValue(`${minQuantity1}`);

		await commerceThemeMiniumCatalogPage
			.quantitySelector(page.locator('.product-detail'))
			.focus();
		await commerceThemeMiniumCatalogPage.checkQuantitiesInPopOverMessages(
			maxQuantity1,
			minQuantity1,
			multipleQuantity1
		);

		await page.goto(`/web/${site.name}`);

		await commerceThemeMiniumCatalogPage
			.productCard(productName2)
			.getByRole('link')
			.first()
			.click();

		await expect(
			commerceThemeMiniumCatalogPage.quantitySelector(
				page.locator('.product-detail')
			)
		).toHaveValue(`${minQuantity2}`);

		await commerceThemeMiniumCatalogPage
			.quantitySelector(page.locator('.product-detail'))
			.focus();
		await commerceThemeMiniumCatalogPage.checkQuantitiesInPopOverMessages(
			maxQuantity2,
			minQuantity2,
			multipleQuantity2
		);
	}
	finally {
		const orders =
			await apiHelpers.headlessCommerceAdminOrder.getOrdersPage();

		apiHelpers.data.push({id: orders.items[0].id, type: 'order'});
	}
});

test('COMMERCE-12399 Verify that the maximum order quantity is applied correctly with decimal numbers and UOM in the minicart', async ({
	apiHelpers,
	commerceMiniCartPage,
	commerceThemeMiniumCatalogPage,
	page,
	productDetailsPage,
}) => {
	test.setTimeout(120000);

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

	const product1 = (
		await apiHelpers.headlessCommerceAdminCatalog.getProducts(
			new URLSearchParams({
				filter: `name eq 'U-Joint'`,
			})
		)
	).items[0];

	const productName1 = product1.name['en_US'];

	await apiHelpers.headlessCommerceAdminCatalog.patchProduct(
		product1.productId,
		{
			name: {en_US: productName1},
			productConfiguration: {
				maxOrderQuantity: 1.5,
				minOrderQuantity: 0.0001,
				multipleOrderQuantity: 0.0001,
			},
		}
	);

	const patchedProduct1 = (
		await apiHelpers.headlessCommerceAdminCatalog.getProducts(
			new URLSearchParams({
				filter: `name eq 'U-Joint'`,
				nestedFields: `skus,productConfiguration`,
			})
		)
	).items[0];

	const skuUOM1 =
		await apiHelpers.headlessCommerceAdminCatalog.postSkuUnitOfMeasure(
			patchedProduct1.skus[0].id,
			{
				basePrice: 10,
				incrementalOrderQuantity: 0.6,
				name: {en_US: 'UOM1'},
				precision: 1,
				priority: 0,
			}
		);

	const product2 = (
		await apiHelpers.headlessCommerceAdminCatalog.getProducts(
			new URLSearchParams({
				filter: `name eq 'Abs Sensor'`,
			})
		)
	).items[0];

	const productName2 = product2.name['en_US'];

	await apiHelpers.headlessCommerceAdminCatalog.patchProduct(
		product2.productId,
		{
			name: {en_US: productName2},
			productConfiguration: {
				maxOrderQuantity: 0.5,
				minOrderQuantity: 0.0001,
				multipleOrderQuantity: 0.0001,
			},
		}
	);

	const patchedProduct2 = (
		await apiHelpers.headlessCommerceAdminCatalog.getProducts(
			new URLSearchParams({
				filter: `name eq 'Abs Sensor'`,
				nestedFields: `skus,productConfiguration`,
			})
		)
	).items[0];

	const skuUOM2 =
		await apiHelpers.headlessCommerceAdminCatalog.postSkuUnitOfMeasure(
			patchedProduct2.skus[0].id,
			{
				basePrice: 10,
				incrementalOrderQuantity: 0.6,
				name: {en_US: 'UOM2'},
				precision: 1,
				priority: 0,
			}
		);

	const multipleQuantity1 =
		commerceThemeMiniumCatalogPage.getMultipleQuantity(
			skuUOM1.incrementalOrderQuantity,
			patchedProduct1.productConfiguration.multipleOrderQuantity,
			skuUOM1.precision
		);
	const minQuantity1 = commerceThemeMiniumCatalogPage.getProductMinQuantity(
		patchedProduct1.productConfiguration.minOrderQuantity,
		multipleQuantity1,
		skuUOM1.precision
	);
	const maxQuantity1 = commerceThemeMiniumCatalogPage.getProductMaxQuantity(
		patchedProduct1.productConfiguration.maxOrderQuantity,
		multipleQuantity1,
		skuUOM1.precision
	);

	const multipleQuantity2 =
		commerceThemeMiniumCatalogPage.getMultipleQuantity(
			skuUOM2.incrementalOrderQuantity,
			patchedProduct2.productConfiguration.multipleOrderQuantity,
			skuUOM2.precision
		);
	const minQuantity2 = commerceThemeMiniumCatalogPage.getProductMinQuantity(
		patchedProduct2.productConfiguration.minOrderQuantity,
		multipleQuantity2,
		skuUOM2.precision
	);
	const maxQuantity2 = commerceThemeMiniumCatalogPage.getProductMaxQuantity(
		patchedProduct2.productConfiguration.maxOrderQuantity,
		multipleQuantity2,
		skuUOM2.precision
	);

	await performLogout(page);
	await performLogin(page, 'demo.unprivileged');

	await page.goto(`/web/${site.name}/p/` + productName1);

	let minQuantityNotSatisfied;
	let multipleQuantityNotSatisfied;
	let maxQuantityNotSatisfied;

	try {
		await productDetailsPage.addToCartButton.click();

		await commerceMiniCartPage.miniCartButton.click();

		for (const quantitySelectorActualQuantity of [1.8, 1.2]) {
			await commerceThemeMiniumCatalogPage
				.quantitySelector(
					commerceMiniCartPage.miniCartItem(productName1)
				)
				.fill(`${quantitySelectorActualQuantity}`);

			maxQuantityNotSatisfied =
				quantitySelectorActualQuantity > maxQuantity1;
			minQuantityNotSatisfied =
				quantitySelectorActualQuantity < minQuantity1;
			multipleQuantityNotSatisfied = !Number.isInteger(
				quantitySelectorActualQuantity / multipleQuantity1
			);

			const isInvalid = quantitySelectorActualQuantity === 1.8;

			if (isInvalid) {
				await expect(
					commerceMiniCartPage.miniCartInvalidQuantityMessage
				).toBeVisible();
				await expect(
					commerceMiniCartPage.reviewOrderButton
				).toBeVisible();
			}

			await commerceThemeMiniumCatalogPage.checkQuantitiesInPopOverMessages(
				maxQuantity1,
				minQuantity1,
				multipleQuantity1,
				maxQuantityNotSatisfied,
				minQuantityNotSatisfied,
				multipleQuantityNotSatisfied
			);
		}

		await page.goto(`/web/${site.name}/p/` + productName2);

		await commerceThemeMiniumCatalogPage
			.quantitySelector(page.locator('.product-detail'))
			.focus();

		await commerceThemeMiniumCatalogPage.checkQuantitiesInPopOverMessages(
			maxQuantity2,
			minQuantity2,
			multipleQuantity2
		);
	}
	finally {
		const orders =
			await apiHelpers.headlessCommerceAdminOrder.getOrdersPage();

		apiHelpers.data.push({id: orders.items[0].id, type: 'order'});
	}
});

test('COMMERCE-12397 Verify that the minimum order quantity is applied correctly with decimal numbers and UOM in the minicart', async ({
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

	const product1 = (
		await apiHelpers.headlessCommerceAdminCatalog.getProducts(
			new URLSearchParams({
				filter: `name eq 'U-Joint'`,
			})
		)
	).items[0];

	const productName1 = product1.name['en_US'];

	await apiHelpers.headlessCommerceAdminCatalog.patchProduct(
		product1.productId,
		{
			name: {en_US: productName1},
			productConfiguration: {
				minOrderQuantity: 0.5,
				multipleOrderQuantity: 0.0001,
			},
		}
	);

	const patchedProduct1 = (
		await apiHelpers.headlessCommerceAdminCatalog.getProducts(
			new URLSearchParams({
				filter: `name eq 'U-Joint'`,
				nestedFields: `skus,productConfiguration`,
			})
		)
	).items[0];

	const skuUOM1 =
		await apiHelpers.headlessCommerceAdminCatalog.postSkuUnitOfMeasure(
			patchedProduct1.skus[0].id,
			{
				basePrice: 10,
				incrementalOrderQuantity: 0.4,
				name: {en_US: 'UOM1'},
				precision: 1,
				priority: 0,
			}
		);

	const product2 = (
		await apiHelpers.headlessCommerceAdminCatalog.getProducts(
			new URLSearchParams({
				filter: `name eq 'Abs Sensor'`,
			})
		)
	).items[0];

	const productName2 = product2.name['en_US'];

	await apiHelpers.headlessCommerceAdminCatalog.patchProduct(
		product2.productId,
		{
			name: {en_US: productName2},
			productConfiguration: {
				minOrderQuantity: 0.5,
				multipleOrderQuantity: 0.0001,
			},
		}
	);

	const patchedProduct2 = (
		await apiHelpers.headlessCommerceAdminCatalog.getProducts(
			new URLSearchParams({
				filter: `name eq 'Abs Sensor'`,
				nestedFields: `skus,productConfiguration`,
			})
		)
	).items[0];

	const skuUOM2 =
		await apiHelpers.headlessCommerceAdminCatalog.postSkuUnitOfMeasure(
			patchedProduct2.skus[0].id,
			{
				basePrice: 10,
				incrementalOrderQuantity: 0.6,
				name: {en_US: 'UOM2'},
				precision: 1,
				priority: 0,
			}
		);

	const multipleQuantity1 =
		commerceThemeMiniumCatalogPage.getMultipleQuantity(
			skuUOM1.incrementalOrderQuantity,
			patchedProduct1.productConfiguration.multipleOrderQuantity,
			skuUOM1.precision
		);
	const minQuantity1 = commerceThemeMiniumCatalogPage.getProductMinQuantity(
		patchedProduct1.productConfiguration.minOrderQuantity,
		multipleQuantity1,
		skuUOM1.precision
	);
	const maxQuantity1 = commerceThemeMiniumCatalogPage.getProductMaxQuantity(
		patchedProduct1.productConfiguration.maxOrderQuantity,
		multipleQuantity1,
		skuUOM1.precision
	);

	const multipleQuantity2 =
		commerceThemeMiniumCatalogPage.getMultipleQuantity(
			skuUOM2.incrementalOrderQuantity,
			patchedProduct2.productConfiguration.multipleOrderQuantity,
			skuUOM2.precision
		);
	const minQuantity2 = commerceThemeMiniumCatalogPage.getProductMinQuantity(
		patchedProduct2.productConfiguration.minOrderQuantity,
		multipleQuantity2,
		skuUOM2.precision
	);
	const maxQuantity2 = commerceThemeMiniumCatalogPage.getProductMaxQuantity(
		patchedProduct2.productConfiguration.maxOrderQuantity,
		multipleQuantity2,
		skuUOM2.precision
	);

	await performLogout(page);
	await performLogin(page, 'demo.unprivileged');

	await page.goto(`/web/${site.name}/p/` + productName1);

	try {
		await productDetailsPage.addToCartButton.click();

		await commerceMiniCartPage.miniCartButton.click();

		let minQuantityNotSatisfied;
		let multipleQuantityNotSatisfied;
		let maxQuantityNotSatisfied;

		for (const quantitySelectorActualQuantity of [0.4, 0.6, 0.8]) {
			await commerceThemeMiniumCatalogPage
				.quantitySelector(
					commerceMiniCartPage.miniCartItem(productName1)
				)
				.fill(`${quantitySelectorActualQuantity}`);

			await expect(
				commerceThemeMiniumCatalogPage.quantitySelector(
					commerceMiniCartPage.miniCartItem(productName1)
				)
			).toHaveValue(`${quantitySelectorActualQuantity}`);

			maxQuantityNotSatisfied =
				quantitySelectorActualQuantity > maxQuantity1;
			minQuantityNotSatisfied =
				quantitySelectorActualQuantity < minQuantity1;
			multipleQuantityNotSatisfied = !Number.isInteger(
				quantitySelectorActualQuantity / multipleQuantity1
			);

			if (
				maxQuantityNotSatisfied ||
				minQuantityNotSatisfied ||
				multipleQuantityNotSatisfied
			) {
				await expect(
					commerceMiniCartPage.miniCartInvalidQuantityMessage
				).toBeVisible();
				await expect(
					commerceMiniCartPage.reviewOrderButton
				).toBeVisible();
			}

			await commerceThemeMiniumCatalogPage.checkQuantitiesInPopOverMessages(
				maxQuantity1,
				minQuantity1,
				multipleQuantity1,
				maxQuantityNotSatisfied,
				minQuantityNotSatisfied,
				multipleQuantityNotSatisfied
			);
		}

		await page.goto(`/web/${site.name}/p/` + productName2);

		await productDetailsPage.addToCartButton.click();

		await commerceMiniCartPage.miniCartButton.click();

		for (const quantitySelectorActualQuantity of [0.3, 0.8, 0.6]) {
			await commerceThemeMiniumCatalogPage
				.quantitySelector(
					commerceMiniCartPage.miniCartItem(productName2)
				)
				.fill(`${quantitySelectorActualQuantity}`);

			await expect(
				commerceThemeMiniumCatalogPage.quantitySelector(
					commerceMiniCartPage.miniCartItem(productName2)
				)
			).toHaveValue(`${quantitySelectorActualQuantity}`);

			maxQuantityNotSatisfied =
				quantitySelectorActualQuantity > maxQuantity2;
			minQuantityNotSatisfied =
				quantitySelectorActualQuantity < minQuantity2;
			multipleQuantityNotSatisfied = !Number.isInteger(
				quantitySelectorActualQuantity / multipleQuantity2
			);

			if (
				maxQuantityNotSatisfied ||
				minQuantityNotSatisfied ||
				multipleQuantityNotSatisfied
			) {
				await expect(
					commerceMiniCartPage.miniCartInvalidQuantityMessage
				).toBeVisible();
				await expect(
					commerceMiniCartPage.reviewOrderButton
				).toBeVisible();
			}

			await commerceThemeMiniumCatalogPage.checkQuantitiesInPopOverMessages(
				maxQuantity2,
				minQuantity2,
				multipleQuantity2,
				maxQuantityNotSatisfied,
				minQuantityNotSatisfied,
				multipleQuantityNotSatisfied
			);
		}
	}
	finally {
		const orders =
			await apiHelpers.headlessCommerceAdminOrder.getOrdersPage();

		apiHelpers.data.push({id: orders.items[0].id, type: 'order'});
	}
});

test('COMMERCE-12398 Verify that the multiple order quantity is applied correctly with decimal numbers and UOM in the minicart', async ({
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
				minOrderQuantity: 0.0001,
				multipleOrderQuantity: 0.5,
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

	const skuUOM =
		await apiHelpers.headlessCommerceAdminCatalog.postSkuUnitOfMeasure(
			patchedProduct.skus[0].id,
			{
				basePrice: 10,
				incrementalOrderQuantity: 0.3,
				name: {en_US: 'UOM1'},
				precision: 1,
				priority: 0,
			}
		);

	const multipleQuantity = commerceThemeMiniumCatalogPage.getMultipleQuantity(
		skuUOM.incrementalOrderQuantity,
		patchedProduct.productConfiguration.multipleOrderQuantity,
		skuUOM.precision
	);
	const minQuantity = commerceThemeMiniumCatalogPage.getProductMinQuantity(
		patchedProduct.productConfiguration.minOrderQuantity,
		multipleQuantity,
		skuUOM.precision
	);
	const maxQuantity = commerceThemeMiniumCatalogPage.getProductMaxQuantity(
		patchedProduct.productConfiguration.maxOrderQuantity,
		multipleQuantity,
		skuUOM.precision
	);

	await performLogout(page);
	await performLogin(page, 'demo.unprivileged');

	await page.goto(`/web/${site.name}/p/` + productName);

	await expect(
		commerceThemeMiniumCatalogPage.quantitySelector(
			page.locator('.product-detail')
		)
	).toHaveValue(`${minQuantity}`);

	try {
		await productDetailsPage.addToCartButton.click();

		await commerceMiniCartPage.miniCartButton.click();

		let minQuantityNotSatisfied;
		let multipleQuantityNotSatisfied;
		let maxQuantityNotSatisfied;

		for (const quantitySelectorActualQuantity of [0.3, 1.1, 1.5]) {
			await commerceThemeMiniumCatalogPage
				.quantitySelector(
					commerceMiniCartPage.miniCartItem(productName)
				)
				.fill(`${quantitySelectorActualQuantity}`);

			maxQuantityNotSatisfied =
				quantitySelectorActualQuantity > maxQuantity;
			minQuantityNotSatisfied =
				quantitySelectorActualQuantity < minQuantity;
			multipleQuantityNotSatisfied = !Number.isInteger(
				quantitySelectorActualQuantity / multipleQuantity
			);

			await expect(
				commerceThemeMiniumCatalogPage.quantitySelector(
					commerceMiniCartPage.miniCartItem(productName)
				)
			).toHaveValue(`${quantitySelectorActualQuantity}`);

			if (
				maxQuantityNotSatisfied ||
				minQuantityNotSatisfied ||
				multipleQuantityNotSatisfied
			) {
				await expect(
					commerceMiniCartPage.miniCartInvalidQuantityMessage
				).toBeVisible();
				await expect(
					commerceMiniCartPage.reviewOrderButton
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
	}
	finally {
		const orders =
			await apiHelpers.headlessCommerceAdminOrder.getOrdersPage();

		apiHelpers.data.push({id: orders.items[0].id, type: 'order'});
	}
});

test('COMMERCE-12399 Verify that the maximum order quantity is applied correctly with decimal numbers and UOM in the product card', async ({
	apiHelpers,
	commerceThemeMiniumCatalogPage,
	page,
}) => {
	test.setTimeout(120000);

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

	const product1 = (
		await apiHelpers.headlessCommerceAdminCatalog.getProducts(
			new URLSearchParams({
				filter: `name eq 'U-Joint'`,
			})
		)
	).items[0];

	const productName1 = product1.name['en_US'];

	await apiHelpers.headlessCommerceAdminCatalog.patchProduct(
		product1.productId,
		{
			name: {en_US: productName1},
			productConfiguration: {
				maxOrderQuantity: 1.5,
				minOrderQuantity: 0.0001,
				multipleOrderQuantity: 0.0001,
			},
		}
	);

	const patchedProduct1 = (
		await apiHelpers.headlessCommerceAdminCatalog.getProducts(
			new URLSearchParams({
				filter: `name eq 'U-Joint'`,
				nestedFields: `skus,productConfiguration`,
			})
		)
	).items[0];

	const skuUOM1 =
		await apiHelpers.headlessCommerceAdminCatalog.postSkuUnitOfMeasure(
			patchedProduct1.skus[0].id,
			{
				basePrice: 10,
				incrementalOrderQuantity: 0.6,
				name: {en_US: 'UOM1'},
				precision: 1,
				priority: 0,
			}
		);

	const product2 = (
		await apiHelpers.headlessCommerceAdminCatalog.getProducts(
			new URLSearchParams({
				filter: `name eq 'Abs Sensor'`,
			})
		)
	).items[0];

	const productName2 = product2.name['en_US'];

	await apiHelpers.headlessCommerceAdminCatalog.patchProduct(
		product2.productId,
		{
			name: {en_US: productName2},
			productConfiguration: {
				maxOrderQuantity: 0.5,
				minOrderQuantity: 0.0001,
				multipleOrderQuantity: 0.0001,
			},
		}
	);

	const patchedProduct2 = (
		await apiHelpers.headlessCommerceAdminCatalog.getProducts(
			new URLSearchParams({
				filter: `name eq 'Abs Sensor'`,
				nestedFields: `skus,productConfiguration`,
			})
		)
	).items[0];

	const skuUOM2 =
		await apiHelpers.headlessCommerceAdminCatalog.postSkuUnitOfMeasure(
			patchedProduct2.skus[0].id,
			{
				basePrice: 10,
				incrementalOrderQuantity: 0.6,
				name: {en_US: 'UOM2'},
				precision: 1,
				priority: 0,
			}
		);

	const multipleQuantity1 =
		commerceThemeMiniumCatalogPage.getMultipleQuantity(
			skuUOM1.incrementalOrderQuantity,
			patchedProduct1.productConfiguration.multipleOrderQuantity,
			skuUOM1.precision
		);
	const minQuantity1 = commerceThemeMiniumCatalogPage.getProductMinQuantity(
		patchedProduct1.productConfiguration.minOrderQuantity,
		multipleQuantity1,
		skuUOM1.precision
	);
	const maxQuantity1 = commerceThemeMiniumCatalogPage.getProductMaxQuantity(
		patchedProduct1.productConfiguration.maxOrderQuantity,
		multipleQuantity1,
		skuUOM1.precision
	);

	const multipleQuantity2 =
		commerceThemeMiniumCatalogPage.getMultipleQuantity(
			skuUOM2.incrementalOrderQuantity,
			patchedProduct2.productConfiguration.multipleOrderQuantity,
			skuUOM2.precision
		);
	const minQuantity2 = commerceThemeMiniumCatalogPage.getProductMinQuantity(
		patchedProduct2.productConfiguration.minOrderQuantity,
		multipleQuantity2,
		skuUOM2.precision
	);
	const maxQuantity2 = commerceThemeMiniumCatalogPage.getProductMaxQuantity(
		patchedProduct2.productConfiguration.maxOrderQuantity,
		multipleQuantity2,
		skuUOM2.precision
	);

	await performLogout(page);
	await performLogin(page, 'demo.unprivileged');

	await page.goto(`/web/${site.name}`);

	let minQuantityNotSatisfied;
	let multipleQuantityNotSatisfied;
	let maxQuantityNotSatisfied;

	for (const quantitySelectorActualQuantity of [1.8, 1.2]) {
		await commerceThemeMiniumCatalogPage
			.quantitySelector(
				commerceThemeMiniumCatalogPage.productCard(productName1)
			)
			.fill(`${quantitySelectorActualQuantity}`);

		maxQuantityNotSatisfied = quantitySelectorActualQuantity > maxQuantity1;
		minQuantityNotSatisfied = quantitySelectorActualQuantity < minQuantity1;
		multipleQuantityNotSatisfied = !Number.isInteger(
			quantitySelectorActualQuantity / multipleQuantity1
		);

		await expect(
			commerceThemeMiniumCatalogPage.quantitySelector(
				commerceThemeMiniumCatalogPage.productCard(productName1)
			)
		).toHaveValue(`${quantitySelectorActualQuantity}`);

		if (
			maxQuantityNotSatisfied ||
			minQuantityNotSatisfied ||
			multipleQuantityNotSatisfied
		) {
			await expect(
				commerceThemeMiniumCatalogPage.quantitySelectorErrorContainer(
					commerceThemeMiniumCatalogPage.productCard(productName1)
				)
			).toHaveClass(/has-error/);
			await expect(
				commerceThemeMiniumCatalogPage.productCardAddToCartButton(
					productName1
				)
			).toHaveClass(/not-allowed/);
		}

		await commerceThemeMiniumCatalogPage.checkQuantitiesInPopOverMessages(
			maxQuantity1,
			minQuantity1,
			multipleQuantity1,
			maxQuantityNotSatisfied,
			minQuantityNotSatisfied,
			multipleQuantityNotSatisfied
		);
	}

	for (const quantitySelectorActualQuantity of [0.5, 0.6, 0.7]) {
		await commerceThemeMiniumCatalogPage
			.quantitySelector(
				commerceThemeMiniumCatalogPage.productCard(productName2)
			)
			.fill(`${quantitySelectorActualQuantity}`);

		maxQuantityNotSatisfied = quantitySelectorActualQuantity > maxQuantity2;
		minQuantityNotSatisfied = quantitySelectorActualQuantity < minQuantity2;
		multipleQuantityNotSatisfied = !Number.isInteger(
			quantitySelectorActualQuantity / multipleQuantity2
		);

		await expect(
			commerceThemeMiniumCatalogPage.quantitySelector(
				commerceThemeMiniumCatalogPage.productCard(productName2)
			)
		).toHaveValue(`${quantitySelectorActualQuantity}`);

		await expect(
			commerceThemeMiniumCatalogPage.productCardAddToCartButton(
				productName2
			)
		).toHaveClass(/not-allowed/);

		await expect(
			commerceThemeMiniumCatalogPage.quantitySelectorErrorContainer(
				commerceThemeMiniumCatalogPage.productCard(productName2)
			)
		).toHaveClass(/has-error/);

		await commerceThemeMiniumCatalogPage.checkQuantitiesInPopOverMessages(
			maxQuantity2,
			minQuantity2,
			multipleQuantity2,
			maxQuantityNotSatisfied,
			minQuantityNotSatisfied,
			multipleQuantityNotSatisfied
		);
	}
});

test('COMMERCE-12397 Verify that the minimum order quantity is applied correctly with decimal numbers and UOM in the product card', async ({
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

	const product1 = (
		await apiHelpers.headlessCommerceAdminCatalog.getProducts(
			new URLSearchParams({
				filter: `name eq 'U-Joint'`,
			})
		)
	).items[0];

	const productName1 = product1.name['en_US'];

	await apiHelpers.headlessCommerceAdminCatalog.patchProduct(
		product1.productId,
		{
			name: {en_US: productName1},
			productConfiguration: {
				minOrderQuantity: 0.5,
				multipleOrderQuantity: 0.0001,
			},
		}
	);

	const patchedProduct1 = (
		await apiHelpers.headlessCommerceAdminCatalog.getProducts(
			new URLSearchParams({
				filter: `name eq 'U-Joint'`,
				nestedFields: `skus,productConfiguration`,
			})
		)
	).items[0];

	const skuUOM1 =
		await apiHelpers.headlessCommerceAdminCatalog.postSkuUnitOfMeasure(
			patchedProduct1.skus[0].id,
			{
				basePrice: 10,
				incrementalOrderQuantity: 0.4,
				name: {en_US: 'UOM1'},
				precision: 1,
				priority: 0,
			}
		);

	const product2 = (
		await apiHelpers.headlessCommerceAdminCatalog.getProducts(
			new URLSearchParams({
				filter: `name eq 'Abs Sensor'`,
			})
		)
	).items[0];

	const productName2 = product2.name['en_US'];

	await apiHelpers.headlessCommerceAdminCatalog.patchProduct(
		product2.productId,
		{
			name: {en_US: productName2},
			productConfiguration: {
				minOrderQuantity: 0.5,
				multipleOrderQuantity: 0.0001,
			},
		}
	);

	const patchedProduct2 = (
		await apiHelpers.headlessCommerceAdminCatalog.getProducts(
			new URLSearchParams({
				filter: `name eq 'Abs Sensor'`,
				nestedFields: `skus,productConfiguration`,
			})
		)
	).items[0];

	const skuUOM2 =
		await apiHelpers.headlessCommerceAdminCatalog.postSkuUnitOfMeasure(
			patchedProduct2.skus[0].id,
			{
				basePrice: 10,
				incrementalOrderQuantity: 0.6,
				name: {en_US: 'UOM2'},
				precision: 1,
				priority: 0,
			}
		);

	const multipleQuantity1 =
		commerceThemeMiniumCatalogPage.getMultipleQuantity(
			skuUOM1.incrementalOrderQuantity,
			patchedProduct1.productConfiguration.multipleOrderQuantity,
			skuUOM1.precision
		);
	const minQuantity1 = commerceThemeMiniumCatalogPage.getProductMinQuantity(
		patchedProduct1.productConfiguration.minOrderQuantity,
		multipleQuantity1,
		skuUOM1.precision
	);
	const maxQuantity1 = commerceThemeMiniumCatalogPage.getProductMaxQuantity(
		patchedProduct1.productConfiguration.maxOrderQuantity,
		multipleQuantity1,
		skuUOM1.precision
	);

	const multipleQuantity2 =
		commerceThemeMiniumCatalogPage.getMultipleQuantity(
			skuUOM2.incrementalOrderQuantity,
			patchedProduct2.productConfiguration.multipleOrderQuantity,
			skuUOM2.precision
		);
	const minQuantity2 = commerceThemeMiniumCatalogPage.getProductMinQuantity(
		patchedProduct2.productConfiguration.minOrderQuantity,
		multipleQuantity2,
		skuUOM2.precision
	);
	const maxQuantity2 = commerceThemeMiniumCatalogPage.getProductMaxQuantity(
		patchedProduct2.productConfiguration.maxOrderQuantity,
		multipleQuantity2,
		skuUOM2.precision
	);

	await performLogout(page);
	await performLogin(page, 'demo.unprivileged');

	await page.goto(`/web/${site.name}`);

	let minQuantityNotSatisfied;
	let multipleQuantityNotSatisfied;
	let maxQuantityNotSatisfied;

	for (const quantitySelectorActualQuantity of [0.4, 0.6, 0.8]) {
		await commerceThemeMiniumCatalogPage
			.quantitySelector(
				commerceThemeMiniumCatalogPage.productCard(productName1)
			)
			.fill(`${quantitySelectorActualQuantity}`);

		maxQuantityNotSatisfied = quantitySelectorActualQuantity > maxQuantity1;
		minQuantityNotSatisfied = quantitySelectorActualQuantity < minQuantity1;
		multipleQuantityNotSatisfied = !Number.isInteger(
			quantitySelectorActualQuantity / multipleQuantity1
		);

		await expect(
			commerceThemeMiniumCatalogPage.quantitySelector(
				commerceThemeMiniumCatalogPage.productCard(productName1)
			)
		).toHaveValue(`${quantitySelectorActualQuantity}`);

		if (
			maxQuantityNotSatisfied ||
			minQuantityNotSatisfied ||
			multipleQuantityNotSatisfied
		) {
			await expect(
				commerceThemeMiniumCatalogPage.quantitySelectorErrorContainer(
					commerceThemeMiniumCatalogPage.productCard(productName1)
				)
			).toHaveClass(/has-error/);
		}
		else {
			await expect(
				commerceThemeMiniumCatalogPage.quantitySelectorErrorContainer(
					commerceThemeMiniumCatalogPage.productCard(productName1)
				)
			).not.toHaveClass(/has-error/);
		}

		await commerceThemeMiniumCatalogPage.checkQuantitiesInPopOverMessages(
			maxQuantity1,
			minQuantity1,
			multipleQuantity1,
			maxQuantityNotSatisfied,
			minQuantityNotSatisfied,
			multipleQuantityNotSatisfied
		);
	}

	for (const quantitySelectorActualQuantity of [0.3, 0.8, 0.6]) {
		await commerceThemeMiniumCatalogPage
			.quantitySelector(
				commerceThemeMiniumCatalogPage.productCard(productName2)
			)
			.fill(`${quantitySelectorActualQuantity}`);

		maxQuantityNotSatisfied = quantitySelectorActualQuantity > maxQuantity2;
		minQuantityNotSatisfied = quantitySelectorActualQuantity < minQuantity2;
		multipleQuantityNotSatisfied = !Number.isInteger(
			quantitySelectorActualQuantity / multipleQuantity2
		);

		await expect(
			commerceThemeMiniumCatalogPage.quantitySelector(
				commerceThemeMiniumCatalogPage.productCard(productName2)
			)
		).toHaveValue(`${quantitySelectorActualQuantity}`);

		if (
			maxQuantityNotSatisfied ||
			minQuantityNotSatisfied ||
			multipleQuantityNotSatisfied
		) {
			await expect(
				commerceThemeMiniumCatalogPage.quantitySelectorErrorContainer(
					commerceThemeMiniumCatalogPage.productCard(productName2)
				)
			).toHaveClass(/has-error/);
		}
		else {
			await expect(
				commerceThemeMiniumCatalogPage.quantitySelectorErrorContainer(
					commerceThemeMiniumCatalogPage.productCard(productName2)
				)
			).not.toHaveClass(/has-error/);
		}

		await commerceThemeMiniumCatalogPage.checkQuantitiesInPopOverMessages(
			maxQuantity2,
			minQuantity2,
			multipleQuantity2,
			maxQuantityNotSatisfied,
			minQuantityNotSatisfied,
			multipleQuantityNotSatisfied
		);
	}
});

test('COMMERCE-12398 Verify that the multiple order quantity is applied correctly with decimal numbers and UOM in the product card', async ({
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
				minOrderQuantity: 0.0001,
				multipleOrderQuantity: 0.5,
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

	const skuUOM =
		await apiHelpers.headlessCommerceAdminCatalog.postSkuUnitOfMeasure(
			patchedProduct.skus[0].id,
			{
				basePrice: 10,
				incrementalOrderQuantity: 0.3,
				name: {en_US: 'UOM1'},
				precision: 1,
				priority: 0,
			}
		);

	const multipleQuantity = commerceThemeMiniumCatalogPage.getMultipleQuantity(
		skuUOM.incrementalOrderQuantity,
		patchedProduct.productConfiguration.multipleOrderQuantity,
		skuUOM.precision
	);
	const minQuantity = commerceThemeMiniumCatalogPage.getProductMinQuantity(
		patchedProduct.productConfiguration.minOrderQuantity,
		multipleQuantity,
		skuUOM.precision
	);
	const maxQuantity = commerceThemeMiniumCatalogPage.getProductMaxQuantity(
		patchedProduct.productConfiguration.maxOrderQuantity,
		multipleQuantity,
		skuUOM.precision
	);

	await performLogout(page);
	await performLogin(page, 'demo.unprivileged');

	await page.goto(`/web/${site.name}`);

	await expect(
		commerceThemeMiniumCatalogPage.quantitySelector(
			commerceThemeMiniumCatalogPage.productCard(productName)
		)
	).toHaveValue(`${minQuantity}`);

	let maxQuantityNotSatisfied;
	let minQuantityNotSatisfied;
	let multipleQuantityNotSatisfied;

	for (const quantitySelectorActualQuantity of [0.6, 1.1, 1.5]) {
		await commerceThemeMiniumCatalogPage
			.quantitySelector(
				commerceThemeMiniumCatalogPage.productCard(productName)
			)
			.fill(`${quantitySelectorActualQuantity}`);

		maxQuantityNotSatisfied = quantitySelectorActualQuantity > maxQuantity;
		minQuantityNotSatisfied = quantitySelectorActualQuantity < minQuantity;
		multipleQuantityNotSatisfied = !Number.isInteger(
			quantitySelectorActualQuantity / multipleQuantity
		);

		await expect(
			commerceThemeMiniumCatalogPage.quantitySelector(
				commerceThemeMiniumCatalogPage.productCard(productName)
			)
		).toHaveValue(`${quantitySelectorActualQuantity}`);

		if (
			maxQuantityNotSatisfied ||
			minQuantityNotSatisfied ||
			multipleQuantityNotSatisfied
		) {
			await expect(
				commerceThemeMiniumCatalogPage.quantitySelectorErrorContainer(
					commerceThemeMiniumCatalogPage.productCard(productName)
				)
			).toHaveClass(/has-error/);
			await expect(
				commerceThemeMiniumCatalogPage.productCardAddToCartButton(
					productName
				)
			).toHaveClass(/not-allowed/);
		}
		else {
			await expect(
				commerceThemeMiniumCatalogPage.quantitySelectorErrorContainer(
					commerceThemeMiniumCatalogPage.productCard(productName)
				)
			).not.toHaveClass(/has-error/);
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

test('COMMERCE-12399 Verify that the maximum order quantity is applied correctly with decimal numbers and UOM in the product details', async ({
	apiHelpers,
	commerceThemeMiniumCatalogPage,
	page,
	productDetailsPage,
}) => {
	test.setTimeout(120000);

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

	const product1 = (
		await apiHelpers.headlessCommerceAdminCatalog.getProducts(
			new URLSearchParams({
				filter: `name eq 'U-Joint'`,
			})
		)
	).items[0];

	const productName1 = product1.name['en_US'];

	await apiHelpers.headlessCommerceAdminCatalog.patchProduct(
		product1.productId,
		{
			name: {en_US: productName1},
			productConfiguration: {
				maxOrderQuantity: 1.5,
				minOrderQuantity: 0.0001,
				multipleOrderQuantity: 0.0001,
			},
		}
	);

	const patchedProduct1 = (
		await apiHelpers.headlessCommerceAdminCatalog.getProducts(
			new URLSearchParams({
				filter: `name eq 'U-Joint'`,
				nestedFields: `skus,productConfiguration`,
			})
		)
	).items[0];

	const skuUOM1 =
		await apiHelpers.headlessCommerceAdminCatalog.postSkuUnitOfMeasure(
			patchedProduct1.skus[0].id,
			{
				basePrice: 10,
				incrementalOrderQuantity: 0.6,
				name: {en_US: 'UOM1'},
				precision: 1,
				priority: 0,
			}
		);

	const product2 = (
		await apiHelpers.headlessCommerceAdminCatalog.getProducts(
			new URLSearchParams({
				filter: `name eq 'Abs Sensor'`,
			})
		)
	).items[0];

	const productName2 = product2.name['en_US'];

	await apiHelpers.headlessCommerceAdminCatalog.patchProduct(
		product2.productId,
		{
			name: {en_US: productName2},
			productConfiguration: {
				maxOrderQuantity: 0.5,
				minOrderQuantity: 0.0001,
				multipleOrderQuantity: 0.0001,
			},
		}
	);

	const patchedProduct2 = (
		await apiHelpers.headlessCommerceAdminCatalog.getProducts(
			new URLSearchParams({
				filter: `name eq 'Abs Sensor'`,
				nestedFields: `skus,productConfiguration`,
			})
		)
	).items[0];

	const skuUOM2 =
		await apiHelpers.headlessCommerceAdminCatalog.postSkuUnitOfMeasure(
			patchedProduct2.skus[0].id,
			{
				basePrice: 10,
				incrementalOrderQuantity: 0.6,
				name: {en_US: 'UOM2'},
				precision: 1,
				priority: 0,
			}
		);

	const multipleQuantity1 =
		commerceThemeMiniumCatalogPage.getMultipleQuantity(
			skuUOM1.incrementalOrderQuantity,
			patchedProduct1.productConfiguration.multipleOrderQuantity,
			skuUOM1.precision
		);
	const minQuantity1 = commerceThemeMiniumCatalogPage.getProductMinQuantity(
		patchedProduct1.productConfiguration.minOrderQuantity,
		multipleQuantity1,
		skuUOM1.precision
	);
	const maxQuantity1 = commerceThemeMiniumCatalogPage.getProductMaxQuantity(
		patchedProduct1.productConfiguration.maxOrderQuantity,
		multipleQuantity1,
		skuUOM1.precision
	);

	const multipleQuantity2 =
		commerceThemeMiniumCatalogPage.getMultipleQuantity(
			skuUOM2.incrementalOrderQuantity,
			patchedProduct2.productConfiguration.multipleOrderQuantity,
			skuUOM2.precision
		);
	const minQuantity2 = commerceThemeMiniumCatalogPage.getProductMinQuantity(
		patchedProduct2.productConfiguration.minOrderQuantity,
		multipleQuantity2,
		skuUOM2.precision
	);
	const maxQuantity2 = commerceThemeMiniumCatalogPage.getProductMaxQuantity(
		patchedProduct2.productConfiguration.maxOrderQuantity,
		multipleQuantity2,
		skuUOM2.precision
	);

	await performLogout(page);
	await performLogin(page, 'demo.unprivileged');

	await page.goto(`/web/${site.name}/p/` + productName1);

	let maxQuantityNotSatisfied;
	let minQuantityNotSatisfied;
	let multipleQuantityNotSatisfied;

	for (const quantitySelectorActualQuantity of [1.8, 1.2]) {
		await expect(async () => {
			await commerceThemeMiniumCatalogPage
				.quantitySelector(page.locator('.product-detail'))
				.focus();
			await commerceThemeMiniumCatalogPage
				.quantitySelector(page.locator('.product-detail'))
				.fill(`${quantitySelectorActualQuantity}`);

			maxQuantityNotSatisfied =
				quantitySelectorActualQuantity > maxQuantity1;
			minQuantityNotSatisfied =
				quantitySelectorActualQuantity < minQuantity1;
			multipleQuantityNotSatisfied = !Number.isInteger(
				quantitySelectorActualQuantity / multipleQuantity1
			);

			await expect(
				commerceThemeMiniumCatalogPage.quantitySelector(
					page.locator('.product-detail')
				)
			).toHaveValue(`${quantitySelectorActualQuantity}`);
		}).toPass();

		if (
			maxQuantityNotSatisfied ||
			minQuantityNotSatisfied ||
			multipleQuantityNotSatisfied
		) {
			await expect(
				commerceThemeMiniumCatalogPage.quantitySelectorErrorContainer(
					page.locator('.product-detail')
				)
			).toHaveClass(/has-error/);
			await expect(productDetailsPage.addToCartButton).toHaveClass(
				/not-allowed/
			);
		}
		else {
			await expect(
				commerceThemeMiniumCatalogPage.quantitySelectorErrorContainer(
					page.locator('.product-detail')
				)
			).not.toHaveClass(/has-error/);
		}

		await commerceThemeMiniumCatalogPage.checkQuantitiesInPopOverMessages(
			maxQuantity1,
			minQuantity1,
			multipleQuantity1,
			maxQuantityNotSatisfied,
			minQuantityNotSatisfied,
			multipleQuantityNotSatisfied
		);
	}

	await page.goto(`/web/${site.name}/p/` + productName2);

	for (const quantitySelectorActualQuantity of [0.5, 0.6, 0.7]) {
		await expect(async () => {
			await commerceThemeMiniumCatalogPage
				.quantitySelector(page.locator('.product-detail'))
				.focus();
			await commerceThemeMiniumCatalogPage
				.quantitySelector(page.locator('.product-detail'))
				.fill(`${quantitySelectorActualQuantity}`);

			await expect(
				commerceThemeMiniumCatalogPage.quantitySelector(
					page.locator('.product-detail')
				)
			).toHaveValue(`${quantitySelectorActualQuantity}`);
		}).toPass();

		await expect(productDetailsPage.addToCartButton).toHaveClass(
			/not-allowed/
		);

		await expect(
			commerceThemeMiniumCatalogPage.quantitySelectorErrorContainer(
				page.locator('.product-detail')
			)
		).toHaveClass(/has-error/);

		await commerceThemeMiniumCatalogPage.checkQuantitiesInPopOverMessages(
			maxQuantity2,
			minQuantity2,
			multipleQuantity2
		);
	}
});

test('COMMERCE-12398 Verify that the multiple order quantity is applied correctly with decimal numbers and UOM in the product details', async ({
	apiHelpers,
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
				minOrderQuantity: 0.0001,
				multipleOrderQuantity: 0.5,
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

	const skuUOM =
		await apiHelpers.headlessCommerceAdminCatalog.postSkuUnitOfMeasure(
			patchedProduct.skus[0].id,
			{
				basePrice: 10,
				incrementalOrderQuantity: 0.3,
				name: {en_US: 'UOM1'},
				precision: 1,
				priority: 0,
			}
		);

	const multipleQuantity = commerceThemeMiniumCatalogPage.getMultipleQuantity(
		skuUOM.incrementalOrderQuantity,
		patchedProduct.productConfiguration.multipleOrderQuantity,
		skuUOM.precision
	);
	const minQuantity = commerceThemeMiniumCatalogPage.getProductMinQuantity(
		patchedProduct.productConfiguration.minOrderQuantity,
		multipleQuantity,
		skuUOM.precision
	);
	const maxQuantity = commerceThemeMiniumCatalogPage.getProductMaxQuantity(
		patchedProduct.productConfiguration.maxOrderQuantity,
		multipleQuantity,
		skuUOM.precision
	);

	await performLogout(page);
	await performLogin(page, 'demo.unprivileged');

	await page.goto(`/web/${site.name}/p/` + productName);

	await expect(
		commerceThemeMiniumCatalogPage.quantitySelector(
			page.locator('.product-detail')
		)
	).toHaveValue(`${minQuantity}`);

	let maxQuantityNotSatisfied;
	let minQuantityNotSatisfied;
	let multipleQuantityNotSatisfied;

	for (const quantitySelectorActualQuantity of [0.6, 1.1, 1.5]) {
		await commerceThemeMiniumCatalogPage
			.quantitySelector(page.locator('.product-detail'))
			.focus();
		await commerceThemeMiniumCatalogPage
			.quantitySelector(page.locator('.product-detail'))
			.fill(`${quantitySelectorActualQuantity}`);

		maxQuantityNotSatisfied = quantitySelectorActualQuantity > maxQuantity;
		minQuantityNotSatisfied = quantitySelectorActualQuantity < minQuantity;
		multipleQuantityNotSatisfied = !Number.isInteger(
			quantitySelectorActualQuantity / multipleQuantity
		);

		await expect(
			commerceThemeMiniumCatalogPage.quantitySelector(
				page.locator('.product-detail')
			)
		).toHaveValue(`${quantitySelectorActualQuantity}`);

		if (
			maxQuantityNotSatisfied ||
			minQuantityNotSatisfied ||
			multipleQuantityNotSatisfied
		) {
			await expect(
				commerceThemeMiniumCatalogPage.quantitySelectorErrorContainer(
					page.locator('.product-detail')
				)
			).toHaveClass(/has-error/);
			await expect(productDetailsPage.addToCartButton).toHaveClass(
				/not-allowed/
			);
		}
		else {
			await expect(
				commerceThemeMiniumCatalogPage.quantitySelectorErrorContainer(
					page.locator('.product-detail')
				)
			).not.toHaveClass(/has-error/);
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

test(
	'Order manager can view information about UOM and quantity for each order item',
	{tag: ['@LPD-57002']},
	async ({
		apiHelpers,
		applicationsMenuPage,
		commerceAdminOrdersPage,
		commerceAdminProductPage,
		commerceAdminShipmentsPage,
		page,
		site,
	}) => {
		test.setTimeout(180000);

		let account;
		let catalog;
		let channel;
		let checkoutCart;
		let option;
		let postCart;
		let product;
		let productSkus;
		let skuUOM1;
		let skuUOM2;
		let skuUOM3;
		let warehouse;

		await test.step('Create an Account and a Catalog', async () => {
			account = await apiHelpers.headlessAdminUser.postAccount({
				name: getRandomString(),
				type: 'business',
			});

			catalog =
				await apiHelpers.headlessCommerceAdminCatalog.postCatalog();

			channel = await apiHelpers.headlessCommerceAdminChannel.postChannel(
				{
					siteGroupId: site.id,
				}
			);
		});

		await test.step('Add 2 UOMs to BLACK and 1 UOM to WHITE', async () => {
			option = await apiHelpers.headlessCommerceAdminCatalog.postOption(
				'select',
				'color',
				'Color',
				1
			);

			product = await apiHelpers.headlessCommerceAdminCatalog.postProduct(
				{
					catalogId: catalog.id,
					name: {en_US: getRandomString()},
					productConfiguration: {
						maxOrderQuantity: 10000,
						minOrderQuantity: 1,
						minStockQuantity: 5,
						multipleOrderQuantity: 0.1,
					},
					productOptions: [
						{
							fieldType: 'select',
							key: 'color',
							name: {
								en_US: 'Color',
							},
							optionId: option.id,
							priority: 1,
							productOptionValues: [
								{
									key: 'black',
									name: {
										en_US: 'Black',
									},
									priority: 1,
									quantity: 1,
								},
								{
									key: 'white',
									name: {
										en_US: 'White',
									},
									priority: 2,
									quantity: 1,
								},
								{
									key: 'yellow',
									name: {
										en_US: 'Yellow',
									},
									priority: 2,
									quantity: 1,
								},
							],
							required: true,
							skuContributor: true,
						},
					],
				}
			);

			await applicationsMenuPage.goToProducts();

			await commerceAdminProductPage
				.managementToolbarItemLink(product.name['en_US'])
				.click();
			await commerceAdminProductPage.generateSkus();

			product = (
				await apiHelpers.headlessCommerceAdminCatalog.getProducts(
					new URLSearchParams({
						filter: `name eq '${product.name['en_US']}'`,
						nestedFields: `skus`,
					})
				)
			).items[0];

			productSkus = product.skus.filter((sku) => {
				return ['YELLOW', 'WHITE', 'BLACK'].includes(sku.sku);
			});

			warehouse =
				await apiHelpers.headlessCommerceAdminInventoryApiHelper.postWarehouses(
					{
						active: true,
						latitude: getRandomInt(),
						longitude: getRandomInt(),
						warehouseItems: [
							{
								quantity: 100,
								sku: productSkus[0].sku,
							},
							{
								quantity: 100,
								sku: productSkus[1].sku,
							},
							{
								quantity: 100,
								sku: productSkus[2].sku,
							},
						],
					}
				);

			await apiHelpers.headlessCommerceAdminInventoryApiHelper.postWarehousesChannels(
				warehouse.id,
				channel.id
			);

			skuUOM1 =
				await apiHelpers.headlessCommerceAdminCatalog.postSkuUnitOfMeasure(
					productSkus[2].id,
					{
						basePrice: 10,
						incrementalOrderQuantity: 0.2,
						name: {en_US: 'UOM1'},
						precision: 1,
						priority: 0,
					}
				);

			skuUOM2 =
				await apiHelpers.headlessCommerceAdminCatalog.postSkuUnitOfMeasure(
					productSkus[2].id,
					{
						basePrice: 10,
						incrementalOrderQuantity: 0.7,
						name: {en_US: 'UOM2'},
						precision: 1,
						priority: 0,
					}
				);

			skuUOM3 =
				await apiHelpers.headlessCommerceAdminCatalog.postSkuUnitOfMeasure(
					productSkus[1].id,
					{
						basePrice: 10,
						incrementalOrderQuantity: 1.3,
						name: {en_US: 'UOM3'},
						precision: 1,
						priority: 0,
					}
				);

			const warehouseItems =
				await apiHelpers.headlessCommerceAdminInventoryApiHelper.getWarehouseIdWarehouseItemsPage(
					warehouse.id
				);

			const warehouseItem = warehouseItems.items.find(
				(warehouseItem) =>
					warehouseItem.sku === productSkus[2].sku &&
					warehouseItem.unitOfMeasureKey === skuUOM2.key
			);

			await apiHelpers.headlessCommerceAdminInventoryApiHelper.patchWarehouseItem(
				warehouseItem.id,
				{
					quantity: 10,
					sku: warehouseItem.sku,
					unitOfMeasureKey: warehouseItem.unitOfMeasureKey,
				}
			);
		});

		await test.step('Create a processing order', async () => {
			await apiHelpers.headlessCommerceAdminAccount.postAddress(
				account.id,
				{phoneNumber: '1234567890', regionISOCode: 'AL'}
			);

			postCart = await apiHelpers.headlessCommerceDeliveryCart.postCart(
				{
					accountId: account.id,
					cartItems: [
						{
							options: `[{key: ${option.key}, value: 'yellow'}]`,
							quantity: 2,
							skuId: productSkus[0].id,
						},
						{
							options: `[{key: ${option.key}, value: 'white'}]`,
							quantity: 2.6,
							skuId: productSkus[1].id,
							skuUnitOfMeasure: {key: skuUOM3.key},
						},
						{
							options: `[{key: ${option.key}, value: 'black'}]`,
							quantity: 1.2,
							skuId: productSkus[2].id,
							skuUnitOfMeasure: {key: skuUOM1.key},
						},
						{
							options: `[{key: ${option.key}, value: 'black'}]`,
							quantity: 1.4,
							skuId: productSkus[2].id,
							skuUnitOfMeasure: {key: skuUOM2.key},
						},
					],
					currencyCode: 'USD',
				},
				channel.id
			);

			checkoutCart =
				await apiHelpers.headlessCommerceDeliveryCart.checkoutCart(
					postCart.id
				);

			await apiHelpers.headlessCommerceAdminOrder.patchOrder(
				checkoutCart.id,
				{
					orderStatus: '10',
				}
			);
		});

		await test.step('Go to Orders and create a shipment and add order items to it', async () => {
			await commerceAdminOrdersPage.goto();
			await commerceAdminOrdersPage
				.menuActionButton(account.name)
				.click();
			await commerceAdminOrdersPage.menuItemAction('View').click();
			await commerceAdminOrdersPage
				.orderStatusLink('Create Shipment')
				.click();

			await expect(
				page.getByText('Processing', {exact: true})
			).toBeVisible();

			await commerceAdminShipmentsPage.addProductsToShipment.click();

			await expect(
				(
					await commerceAdminShipmentsPage.shipmentItemsTableRow(
						1,
						productSkus[0].sku,
						true
					)
				).row
			).toContainText('2');

			await expect(
				(
					await commerceAdminShipmentsPage.shipmentItemsTableRow(
						4,
						skuUOM3.key,
						true
					)
				).row
			).toContainText('2.6');
			await expect(
				(
					await commerceAdminShipmentsPage.shipmentItemsTableRow(
						4,
						skuUOM3.key,
						true
					)
				).row
			).toContainText(productSkus[1].sku);
			await expect(
				(
					await commerceAdminShipmentsPage.shipmentItemsTableRow(
						4,
						skuUOM1.key,
						true
					)
				).row
			).toContainText('1.2');
			await expect(
				(
					await commerceAdminShipmentsPage.shipmentItemsTableRow(
						4,
						skuUOM1.key,
						true
					)
				).row
			).toContainText(productSkus[2].sku);

			await expect(
				(
					await commerceAdminShipmentsPage.shipmentItemsTableRow(
						4,
						skuUOM2.key,
						true
					)
				).row
			).toContainText('1.4');
			await expect(
				(
					await commerceAdminShipmentsPage.shipmentItemsTableRow(
						4,
						skuUOM2.key,
						true
					)
				).row
			).toContainText(productSkus[2].sku);

			await (
				await commerceAdminShipmentsPage.shipmentItemsTableRowAction(
					1,
					productSkus[0].sku
				)
			).check();
			await (
				await commerceAdminShipmentsPage.shipmentItemsTableRowAction(
					4,
					skuUOM3.key
				)
			).check();
			await (
				await commerceAdminShipmentsPage.shipmentItemsTableRowAction(
					4,
					skuUOM2.key
				)
			).check();
			await (
				await commerceAdminShipmentsPage.shipmentItemsTableRowAction(
					4,
					skuUOM1.key
				)
			).check();

			await commerceAdminShipmentsPage.shipmentsItemSubmitButton.click();
		});

		await test.step('Edit each order item and set the quantity in the shipment', async () => {
			await (
				await commerceAdminShipmentsPage.editProductTableRow(
					6,
					skuUOM1.key,
					true
				)
			).row
				.getByRole('link')
				.click();

			await expect(
				await commerceAdminShipmentsPage.editProductTableRowQuantitySelector(
					{
						colIndex: 0,
						rowValue: warehouse.name['en_US'],
					}
				)
			).toBeVisible();

			await (
				await commerceAdminShipmentsPage.editProductTableRowQuantitySelector(
					{
						colIndex: 0,
						rowValue: warehouse.name['en_US'],
					}
				)
			).focus();

			for (let i = 0; i < 6; i++) {
				await (
					await commerceAdminShipmentsPage.editProductTableRowQuantitySelector(
						{
							colIndex: 0,
							rowValue: warehouse.name['en_US'],
						}
					)
				).press('ArrowUp');
			}

			await expect(
				await commerceAdminShipmentsPage.editProductTableRowQuantitySelector(
					{
						colIndex: 0,
						rowValue: warehouse.name['en_US'],
					}
				)
			).toHaveValue('1.2');
			await commerceAdminShipmentsPage.editProductSaveButton.click();

			await waitForAlert(page.frameLocator('iframe'));

			await commerceAdminShipmentsPage.editProductCloseButton.click();

			await (
				await commerceAdminShipmentsPage.editProductTableRow(
					6,
					skuUOM2.key,
					true
				)
			).row
				.getByRole('link')
				.click();

			await expect(
				await commerceAdminShipmentsPage.editProductTableRowQuantitySelector(
					{
						colIndex: 0,
						rowValue: warehouse.name['en_US'],
					}
				)
			).toBeVisible();

			await (
				await commerceAdminShipmentsPage.editProductTableRowQuantitySelector(
					{
						colIndex: 0,
						rowValue: warehouse.name['en_US'],
					}
				)
			).fill('1.4');
			await commerceAdminShipmentsPage.editProductSaveButton.click();

			await waitForAlert(page.frameLocator('iframe'));

			await expect(
				await commerceAdminShipmentsPage.editProductTableRowQuantitySelector(
					{
						colIndex: 0,
						rowValue: warehouse.name['en_US'],
					}
				)
			).toHaveValue('1.4');

			await commerceAdminShipmentsPage.editProductCloseButton.click();

			await (
				await commerceAdminShipmentsPage.editProductTableRow(
					6,
					skuUOM3.key,
					true
				)
			).row
				.getByRole('link')
				.click();

			await expect(
				await commerceAdminShipmentsPage.editProductTableRowQuantitySelector(
					{
						colIndex: 0,
						rowValue: warehouse.name['en_US'],
					}
				)
			).toBeVisible();

			await (
				await commerceAdminShipmentsPage.editProductTableRowQuantitySelector(
					{
						colIndex: 0,
						rowValue: warehouse.name['en_US'],
					}
				)
			).fill('2.6');
			await commerceAdminShipmentsPage.editProductSaveButton.click();

			await waitForAlert(page.frameLocator('iframe'));

			await expect(
				await commerceAdminShipmentsPage.editProductTableRowQuantitySelector(
					{
						colIndex: 0,
						rowValue: warehouse.name['en_US'],
					}
				)
			).toHaveValue('2.6');

			await commerceAdminShipmentsPage.editProductCloseButton.click();

			await (
				await commerceAdminShipmentsPage.editProductTableRow(
					0,
					productSkus[0].sku,
					true
				)
			).row
				.getByRole('link')
				.click();
			await (
				await commerceAdminShipmentsPage.editProductTableRowQuantitySelector(
					{
						colIndex: 0,
						rowValue: warehouse.name['en_US'],
					}
				)
			).fill('2');
			await commerceAdminShipmentsPage.editProductSaveButton.click();

			await waitForAlert(page.frameLocator('iframe'));

			await expect(
				await commerceAdminShipmentsPage.editProductTableRowQuantitySelector(
					{
						colIndex: 0,
						rowValue: warehouse.name['en_US'],
					}
				)
			).toHaveValue('2');

			await commerceAdminShipmentsPage.editProductCloseButton.click();
		});

		await test.step('Assert that the outstanding quantity column displays 0 for each order item', async () => {
			await expect(
				(
					await commerceAdminShipmentsPage.editProductTableRow(
						6,
						skuUOM1.key,
						true
					)
				).row
			).toContainText('0');
			await expect(
				(
					await commerceAdminShipmentsPage.editProductTableRow(
						6,
						skuUOM2.key,
						true
					)
				).row
			).toContainText('0');
			await expect(
				(
					await commerceAdminShipmentsPage.editProductTableRow(
						6,
						skuUOM3.key,
						true
					)
				).row
			).toContainText('0');
			await expect(
				(
					await commerceAdminShipmentsPage.editProductTableRow(
						0,
						productSkus[0].sku,
						true
					)
				).row
			).toContainText('0');
		});

		await test.step('Assert that after click on Finish Processing button, the table keeps showing the right quantities and UOMs', async () => {
			await commerceAdminShipmentsPage
				.shipmentStatusLink('Finish Processing')
				.click();

			await waitForAlert(page);

			await expect(
				(
					await commerceAdminShipmentsPage.editProductTableRow(
						6,
						skuUOM1.key,
						true
					)
				).row
			).toContainText('1.2');
			await expect(
				(
					await commerceAdminShipmentsPage.editProductTableRow(
						6,
						skuUOM2.key,
						true
					)
				).row
			).toContainText('1.4');
			await expect(
				(
					await commerceAdminShipmentsPage.editProductTableRow(
						6,
						skuUOM3.key,
						true
					)
				).row
			).toContainText('2.6');
			await expect(
				(
					await commerceAdminShipmentsPage.editProductTableRow(
						0,
						productSkus[0].sku,
						true
					)
				).row
			).toContainText('2');
		});

		await test.step('Assert that after click on Ship and Deliver button, the table keeps showing the right quantities and UOMs', async () => {
			const shipmentStatuses = ['Ship', 'Deliver'];

			for (const shipmentStatus of shipmentStatuses) {
				await commerceAdminShipmentsPage
					.shipmentStatusLink(shipmentStatus)
					.click();

				await waitForAlert(page);

				await expect(
					(
						await commerceAdminShipmentsPage.editProductTableRow(
							5,
							skuUOM1.key,
							true
						)
					).row
				).toContainText('1.2');
				await expect(
					(
						await commerceAdminShipmentsPage.editProductTableRow(
							5,
							skuUOM2.key,
							true
						)
					).row
				).toContainText('1.4');
				await expect(
					(
						await commerceAdminShipmentsPage.editProductTableRow(
							5,
							skuUOM3.key,
							true
						)
					).row
				).toContainText('2.6');
				await expect(
					(
						await commerceAdminShipmentsPage.editProductTableRow(
							0,
							productSkus[0].sku,
							true
						)
					).row
				).toContainText('2');
			}
		});
	}
);

test(
	'UOM incremental order quantity and pricing quantity display double value',
	{tag: ['@LPD-62187']},
	async ({
		apiHelpers,
		applicationsMenuPage,
		commerceAdminProductDetailsPage,
		commerceAdminProductDetailsSkusPage,
		commerceAdminProductPage,
	}) => {
		const catalog =
			await apiHelpers.headlessCommerceAdminCatalog.postCatalog({
				name: 'Catalog',
			});

		const product =
			await apiHelpers.headlessCommerceAdminCatalog.postProduct({
				catalogId: catalog.id,
				name: {
					en_US: 'Product',
				},
			});

		const productSkus = await apiHelpers.headlessCommerceAdminCatalog
			.getProduct(product.productId)
			.then((product) => {
				return product.skus;
			});

		const sku = productSkus[0];

		await apiHelpers.headlessCommerceAdminCatalog.postSkuUnitOfMeasure(
			sku.id,
			{
				incrementalOrderQuantity: 1.5,
				name: {en_US: 'UOM'},
				precision: 1,
				pricingQuantity: 1.5,
				priority: 0,
			}
		);

		await applicationsMenuPage.goToProducts();

		await commerceAdminProductPage.managementToolbarSearchInput.fill(
			'Product'
		);
		await commerceAdminProductPage.managementToolbarSearchInput.press(
			'Enter'
		);

		await commerceAdminProductPage.productsTableRowLink('Product').click();

		await commerceAdminProductDetailsPage.goToProductSkus();

		await commerceAdminProductDetailsSkusPage
			.skusTableRowLink(`${sku.sku}`)
			.click();

		await commerceAdminProductDetailsSkusPage.goToSkuUOM();

		await commerceAdminProductDetailsSkusPage
			.uomTableRowLink('UOM')
			.click();

		await expect(
			commerceAdminProductDetailsSkusPage.incrementalOrderQuantity
		).toHaveValue('1.5');

		await expect(
			commerceAdminProductDetailsSkusPage.pricinQuantity
		).toHaveValue('1.5');
	}
);
