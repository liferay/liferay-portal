/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {applicationsMenuPageTest} from '../../../../fixtures/applicationsMenuPageTest';
import {commercePagesTest} from '../../../../fixtures/commercePagesTest';
import {dataApiHelpersTest} from '../../../../fixtures/dataApiHelpersTest';
import {loginTest} from '../../../../fixtures/loginTest';
import {getRandomInt} from '../../../../utils/getRandomInt';
import getRandomString from '../../../../utils/getRandomString';
import {miniumSetUp} from '../../utils/commerce';

export const test = mergeTests(
	applicationsMenuPageTest,
	commercePagesTest,
	dataApiHelpersTest,
	loginTest()
);

test('LPD-29583 Check discount on products with UOM', async ({
	apiHelpers,
	commerceMiniCartPage,
	page,
	productDetailsPage,
}) => {
	test.setTimeout(180000);

	const account = await apiHelpers.headlessAdminUser.postAccount({
		name: getRandomString(),
		type: 'business',
	});

	await apiHelpers.headlessAdminUser.assignUserToAccountByEmailAddress(
		account.id,
		['test@liferay.com']
	);

	const user =
		await apiHelpers.headlessAdminUser.getUserAccountByEmailAddress(
			'test@liferay.com'
		);

	const roles = await apiHelpers.headlessAdminUser.getRoles('Sales Agent');

	await apiHelpers.headlessAdminUser.postRoleUserAccountAssociation(
		roles.items[0].id,
		user.id
	);

	apiHelpers.data.push({
		id: `${roles.items[0].id}_${user.id}`,
		type: 'roleUserAccountAssociation',
	});

	const {catalog, site} = await miniumSetUp(apiHelpers);

	const product = await apiHelpers.headlessCommerceAdminCatalog.postProduct({
		catalogId: catalog.id,
		name: {en_US: `product-${getRandomInt()}`},
		skus: [
			{
				cost: 50,
				price: 50,
				published: true,
				purchasable: true,
				sku: 'Sku' + getRandomInt(),
			},
		],
	});

	await page.goto(`/web${site.friendlyUrlPath}/p/${product.name['en_US']}`);

	await expect(
		await productDetailsPage.priceField(
			'Discount',
			productDetailsPage.priceContainer
		)
	).toHaveCount(0);
	await expect(
		await productDetailsPage.priceField(
			'Net Price',
			productDetailsPage.priceContainer
		)
	).toHaveCount(0);
	await expect(
		await productDetailsPage.priceField(
			'$ 50.00',
			productDetailsPage.priceContainer
		)
	).toBeVisible();

	await apiHelpers.headlessCommerceAdminPricing.postDiscount({
		discountProducts: [
			{
				productId: product.productId,
			},
		],
		percentageLevel1: 10,
		usePercentage: true,
	});

	await apiHelpers.headlessCommerceAdminCatalog.postSkuUnitOfMeasure(
		product.skus[0].id,
		{
			basePrice: 50,
			incrementalOrderQuantity: 5,
			key: getRandomString(),
			name: {en_US: `Pallet`},
			primary: true,
			priority: 2,
			rate: 1,
		}
	);

	await page.reload();

	await expect(
		await productDetailsPage.priceField(
			'$ 50.00',
			productDetailsPage.priceContainer
		)
	).toBeVisible();
	await expect(
		await productDetailsPage.priceField(
			'–10%',
			productDetailsPage.priceContainer
		)
	).toBeVisible();
	await expect(
		await productDetailsPage.priceField(
			'$ 45.00',
			productDetailsPage.priceContainer
		)
	).toBeVisible();

	await commerceMiniCartPage.miniCartButton.click();
	await commerceMiniCartPage.searchProductsInput.fill(product.skus[0].sku);
	await commerceMiniCartPage.quickAddToCartSku(product.skus[0].sku).click();
	await commerceMiniCartPage.quickAddToCartButton.click();

	await expect(
		await commerceMiniCartPage.priceField(
			'$ 50.00',
			commerceMiniCartPage.miniCartItemsContainer
		)
	).toBeVisible();
	await expect(
		await commerceMiniCartPage.priceField(
			'–10.00%',
			commerceMiniCartPage.miniCartItemsContainer
		)
	).toBeVisible();
	await expect(
		await commerceMiniCartPage.priceField(
			'$ 45.00',
			commerceMiniCartPage.miniCartItemsContainer
		)
	).toBeVisible();

	await apiHelpers.headlessCommerceAdminCatalog.postSkuUnitOfMeasure(
		product.skus[0].id,
		{
			basePrice: 10,
			incrementalOrderQuantity: 3,
			key: getRandomString(),
			name: {en_US: `Box`},
			primary: true,
			priority: 1,
			rate: 1,
		}
	);

	await page.reload();

	await expect(
		await productDetailsPage.priceField(
			'$ 10.00',
			productDetailsPage.priceContainer
		)
	).toBeVisible();
	await expect(
		await productDetailsPage.priceField(
			'–10%',
			productDetailsPage.priceContainer
		)
	).toBeVisible();
	await expect(
		await productDetailsPage.priceField(
			'$ 9.00',
			productDetailsPage.priceContainer
		)
	).toBeVisible();
});

test('LPD-35633 Check discount on products with minOrderQuantity greater than 1', async ({
	apiHelpers,
	page,
	productDetailsPage,
}) => {
	test.setTimeout(180000);

	const {catalog, site} = await miniumSetUp(apiHelpers);

	const account = await apiHelpers.headlessAdminUser.postAccount({
		name: getRandomString(),
		type: 'business',
	});

	await apiHelpers.headlessAdminUser.assignUserToAccountByEmailAddress(
		account.id,
		['test@liferay.com']
	);

	const user =
		await apiHelpers.headlessAdminUser.getUserAccountByEmailAddress(
			'test@liferay.com'
		);

	const roles = await apiHelpers.headlessAdminUser.getRoles('Sales Agent');

	await apiHelpers.headlessAdminUser.postRoleUserAccountAssociation(
		roles.items[0].id,
		user.id
	);

	apiHelpers.data.push({
		id: `${roles.items[0].id}_${user.id}`,
		type: 'roleUserAccountAssociation',
	});

	const product = await apiHelpers.headlessCommerceAdminCatalog.postProduct({
		catalogId: catalog.id,
		name: {en_US: `product-${getRandomInt()}`},
		productConfiguration: {
			minOrderQuantity: 10,
			multipleOrderQuantity: 10,
		},
		skus: [
			{
				cost: 50,
				price: 50,
				published: true,
				purchasable: true,
				sku: 'Sku' + getRandomInt(),
			},
		],
	});

	await apiHelpers.headlessCommerceAdminPricing.postDiscount({
		discountProducts: [
			{
				productId: product.productId,
			},
		],
		percentageLevel1: 10,
		usePercentage: true,
	});

	await page.goto(`/web${site.friendlyUrlPath}/p/${product.name['en_US']}`);

	await expect(
		await productDetailsPage.priceField(
			'$ 50.00',
			productDetailsPage.priceContainer
		)
	).toBeVisible();
	await expect(
		await productDetailsPage.priceField(
			'–10%',
			productDetailsPage.priceContainer
		)
	).toBeVisible();
	await expect(
		await productDetailsPage.priceField(
			'$ 45.00',
			productDetailsPage.priceContainer
		)
	).toBeVisible();

	await apiHelpers.headlessCommerceAdminCatalog.postSkuUnitOfMeasure(
		product.skus[0].id,
		{
			basePrice: 10,
			incrementalOrderQuantity: 0.1,
			key: getRandomString(),
			name: {en_US: `Box`},
			precision: 1,
			primary: true,
			priority: 1,
			rate: 1,
		}
	);

	await page.reload();

	await expect(
		await productDetailsPage.priceField(
			'$ 10.00',
			productDetailsPage.priceContainer
		)
	).toBeVisible();
	await expect(
		await productDetailsPage.priceField(
			'–10%',
			productDetailsPage.priceContainer
		)
	).toBeVisible();
	await expect(
		await productDetailsPage.priceField(
			'$ 9.00',
			productDetailsPage.priceContainer
		)
	).toBeVisible();
});
