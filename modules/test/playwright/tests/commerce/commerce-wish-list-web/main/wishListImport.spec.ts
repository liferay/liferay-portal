/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Page, expect, mergeTests} from '@playwright/test';

import {commercePagesTest} from '../../../../fixtures/commercePagesTest';
import {dataApiHelpersTest} from '../../../../fixtures/dataApiHelpersTest';
import {loginTest} from '../../../../fixtures/loginTest';
import {DataApiHelpers} from '../../../../helpers/ApiHelpers';
import {
	OrderImportPage,
	getTableRowCells,
} from '../../../../pages/commerce/commerce-order-content-web/orderImportPage';
import {PendingOrdersPage} from '../../../../pages/commerce/commerce-order-content-web/pendingOrdersPage';
import getRandomString from '../../../../utils/getRandomString';
import {
	performLoginViaApi,
	performUserSwitch,
} from '../../../../utils/performLogin';
import {createAccountWithBuyerUser, miniumSetUp} from '../../utils/commerce';

export const test = mergeTests(
	commercePagesTest,
	dataApiHelpersTest,
	loginTest()
);

let catalogId: number;
let channel: {id: number; name: string; siteGroupId: number};
let setupData: Array<{id: number | string; type: string}>;
let site: Site;

type Product = {
	name?: {[key: string]: string};
	productId?: number;
	skus?: Array<{id?: number; price?: number; sku?: string}>;
};

async function createProduct(
	apiHelpers: DataApiHelpers,
	{
		allowBackOrder = true,
		name,
		price,
		purchasable = true,
		subscription = false,
	}: {
		allowBackOrder?: boolean;
		name: string;
		price: number;
		purchasable?: boolean;
		subscription?: boolean;
	}
): Promise<Product> {
	return apiHelpers.headlessCommerceAdminCatalog.postProduct({
		catalogId,
		name: {en_US: name},
		productConfiguration: {allowBackOrder},
		skus: [
			{
				cost: price,
				price,
				published: true,
				purchasable,
				sku: `SKU-${name}`,
			},
		],
		...(subscription && {
			subscriptionConfiguration: {
				enable: true,
				length: 1,
				subscriptionType: 'daily',
			},
		}),
	});
}

async function setUpWishListImport(
	apiHelpers: DataApiHelpers,
	orderImportPage: OrderImportPage,
	page: Page,
	pendingOrdersPage: PendingOrdersPage,
	{
		cartItems = [],
		wishListProducts,
	}: {
		cartItems?: Array<{product: Product; quantity: number}>;
		wishListProducts: Product[];
	}
) {
	const {account, buyerUser} = await createAccountWithBuyerUser(
		apiHelpers,
		site.id
	);

	const cart = await apiHelpers.headlessCommerceDeliveryCart.postCart(
		{
			accountId: account.id,
			cartItems: cartItems.map(({product, quantity}) => ({
				quantity,
				skuId: product.skus[0].id,
			})),
		},
		channel.id
	);

	await performUserSwitch(page, buyerUser.alternateName);

	const wishListName = `Wish List ${getRandomString()}`;

	const wishList =
		await apiHelpers.headlessCommerceDeliveryCatalog.postWishList(
			{name: wishListName},
			channel.id,
			account.id
		);

	for (const product of [...wishListProducts].reverse()) {
		await apiHelpers.headlessCommerceDeliveryCatalog.postWishListItem(
			wishList.id,
			{productId: product.productId, skuId: product.skus[0].id},
			account.id
		);
	}

	await page.goto(`/web${site.friendlyUrlPath}/pending-orders`);

	await pendingOrdersPage.orderRowLink(cart.id).click();

	await orderImportPage.openImportModal();

	await orderImportPage.selectSource(wishListName);

	return {account, buyerUser, cart};
}

async function expectPreviewStatuses(
	orderImportPage: OrderImportPage,
	statuses: Array<[Product, string]>
) {
	await expect(async () => {
		await expect(orderImportPage.previewRows()).toHaveCount(
			statuses.length,
			{timeout: 5000}
		);

		for (const [product, status] of statuses) {
			expect(
				await orderImportPage.previewRowCells(product.name.en_US)
			).toMatchObject({
				'IMPORT STATUS': status,
				'SKU': product.skus[0].sku,
			});
		}
	}).toPass({timeout: 30000});
}

test.beforeAll(async ({browser}) => {
	const page = await browser.newPage();

	await performLoginViaApi({page, screenName: 'test'});

	const apiHelpers = new DataApiHelpers(page);

	const miniumResult = await miniumSetUp(apiHelpers);

	channel = miniumResult.channel;
	site = miniumResult.site;

	await expect(async () => {
		const miniumProduct =
			await apiHelpers.headlessCommerceAdminCatalog.getProductByName(
				'U-Joint'
			);

		catalogId = miniumProduct.catalogId;

		expect(catalogId).toBeTruthy();
	}).toPass({timeout: 30000});

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

test(
	'Wish list import preview reports the status of every wish list item',
	{tag: ['@LPD-105666']},
	async ({apiHelpers, orderImportPage, page, pendingOrdersPage}) => {
		const importableProducts = [];

		for (const price of [24, 50, 34]) {
			importableProducts.push(
				await createProduct(apiHelpers, {
					name: `Importable ${getRandomString()}`,
					price,
				})
			);
		}

		const outOfStockProduct = await createProduct(apiHelpers, {
			allowBackOrder: false,
			name: `Out Of Stock ${getRandomString()}`,
			price: 15,
		});

		const discontinuedProduct = await createProduct(apiHelpers, {
			name: `Discontinued ${getRandomString()}`,
			price: 10,
			purchasable: false,
		});

		await setUpWishListImport(
			apiHelpers,
			orderImportPage,
			page,
			pendingOrdersPage,
			{
				wishListProducts: [
					...importableProducts,
					outOfStockProduct,
					discontinuedProduct,
				],
			}
		);

		await expect(async () => {
			await expect(orderImportPage.previewRows()).toHaveCount(5, {
				timeout: 5000,
			});

			for (const product of importableProducts) {
				expect(
					await orderImportPage.previewRowCells(product.name.en_US)
				).toMatchObject({
					'IMPORT STATUS': 'OK',
					'QUANTITY': '1',
					'SKU': product.skus[0].sku,
					'TOTAL PRICE': `$ ${product.skus[0].price.toFixed(2)}`,
					'UNIT PRICE': `$ ${product.skus[0].price.toFixed(2)}`,
				});
			}

			expect(
				await orderImportPage.previewRowCells(
					outOfStockProduct.name.en_US
				)
			).toMatchObject({
				'IMPORT STATUS': 'The specified quantity is unavailable.',
				'QUANTITY': '1',
				'SKU': outOfStockProduct.skus[0].sku,
				'TOTAL PRICE': '',
				'UNIT PRICE': '',
			});
			expect(
				await orderImportPage.previewRowCells(
					discontinuedProduct.name.en_US
				)
			).toMatchObject({
				'IMPORT STATUS': 'The product is no longer available.',
				'QUANTITY': '1',
				'SKU': discontinuedProduct.skus[0].sku,
				'TOTAL PRICE': '',
				'UNIT PRICE': '',
			});
		}).toPass({timeout: 30000});
	}
);

test(
	'Importing a wish list adds its items to the order and merges quantities',
	{tag: ['@LPD-105666']},
	async ({apiHelpers, orderImportPage, page, pendingOrdersPage}) => {
		const [orderedProduct, increasedProduct, addedProduct] =
			await Promise.all(
				[24, 50, 34].map((price) =>
					createProduct(apiHelpers, {
						name: `Importable ${getRandomString()}`,
						price,
					})
				)
			);

		const untouchedProduct = await createProduct(apiHelpers, {
			name: `Untouched ${getRandomString()}`,
			price: 15,
		});

		const outOfStockProduct = await createProduct(apiHelpers, {
			allowBackOrder: false,
			name: `Out Of Stock ${getRandomString()}`,
			price: 99,
		});

		await setUpWishListImport(
			apiHelpers,
			orderImportPage,
			page,
			pendingOrdersPage,
			{
				cartItems: [
					{product: orderedProduct, quantity: 1},
					{product: increasedProduct, quantity: 3},
					{product: untouchedProduct, quantity: 1},
				],
				wishListProducts: [
					orderedProduct,
					increasedProduct,
					addedProduct,
					outOfStockProduct,
				],
			}
		);

		await expect(orderImportPage.importButton()).toBeVisible();

		await orderImportPage.importButton().click();

		await expect(orderImportPage.importedRowsAlert(3)).toBeVisible();
		await expect(orderImportPage.notImportedRowsAlert(1)).toBeVisible();

		await expect(async () => {
			expect(
				await getTableRowCells(
					pendingOrdersPage.orderItemsTable,
					orderedProduct.name.en_US
				)
			).toMatchObject({
				QUANTITY: '2',
				SKU: orderedProduct.skus[0].sku,
				TOTAL: '$ 48.00',
			});
			expect(
				await getTableRowCells(
					pendingOrdersPage.orderItemsTable,
					increasedProduct.name.en_US
				)
			).toMatchObject({
				QUANTITY: '4',
				SKU: increasedProduct.skus[0].sku,
				TOTAL: '$ 200.00',
			});
			expect(
				await getTableRowCells(
					pendingOrdersPage.orderItemsTable,
					addedProduct.name.en_US
				)
			).toMatchObject({
				QUANTITY: '1',
				SKU: addedProduct.skus[0].sku,
				TOTAL: '$ 34.00',
			});
			expect(
				await getTableRowCells(
					pendingOrdersPage.orderItemsTable,
					untouchedProduct.name.en_US
				)
			).toMatchObject({
				QUANTITY: '1',
				SKU: untouchedProduct.skus[0].sku,
				TOTAL: '$ 15.00',
			});
		}).toPass({timeout: 30000});

		await expect(
			pendingOrdersPage.orderItemsTable.getByText(
				outOfStockProduct.name.en_US
			)
		).toHaveCount(0);
	}
);

test(
	'Only one wish list can be chosen for an import',
	{tag: ['@LPD-105666']},
	async ({apiHelpers, orderImportPage, page, pendingOrdersPage}) => {
		const {account, buyerUser} = await createAccountWithBuyerUser(
			apiHelpers,
			site.id
		);

		const cart = await apiHelpers.headlessCommerceDeliveryCart.postCart(
			{accountId: account.id},
			channel.id
		);

		const products = await Promise.all(
			[1, 2, 3].map((index) =>
				createProduct(apiHelpers, {
					name: `Importable ${index} ${getRandomString()}`,
					price: 10 * index,
				})
			)
		);

		await performUserSwitch(page, buyerUser.alternateName);

		const wishListNames = [];

		for (const product of products) {
			const wishListName = `Wish List ${getRandomString()}`;

			const wishList =
				await apiHelpers.headlessCommerceDeliveryCatalog.postWishList(
					{defaultWishList: false, name: wishListName},
					channel.id,
					account.id
				);

			await apiHelpers.headlessCommerceDeliveryCatalog.postWishListItem(
				wishList.id,
				{productId: product.productId, skuId: product.skus[0].id},
				account.id
			);

			wishListNames.push(wishListName);
		}

		await page.goto(`/web${site.friendlyUrlPath}/pending-orders`);

		await pendingOrdersPage.orderRowLink(cart.id).click();

		await orderImportPage.openImportModal();

		await expect(async () => {
			for (const wishListName of wishListNames) {
				expect(
					await getTableRowCells(
						orderImportPage.sourceTable(),
						wishListName
					)
				).toMatchObject({
					'NUMBER OF ITEMS': '1',
					'TITLE': wishListName,
				});
			}
		}).toPass({timeout: 30000});

		await orderImportPage.selectSource(wishListNames[2]);

		await expect(async () => {
			expect(
				await orderImportPage.previewRowCells(products[2].name.en_US)
			).toMatchObject({
				'IMPORT STATUS': 'OK',
				'QUANTITY': '1',
				'SKU': products[2].skus[0].sku,
			});
		}).toPass({timeout: 30000});

		for (const wishListName of wishListNames) {
			await expect(orderImportPage.sourceLink(wishListName)).toHaveCount(
				0
			);
		}
	}
);

test(
	'Only the first subscription product is imported into an empty order',
	{tag: ['@COMMERCE-7790', '@LPD-105666']},
	async ({apiHelpers, orderImportPage, page, pendingOrdersPage}) => {
		const [firstSubscriptionProduct, secondSubscriptionProduct] =
			await Promise.all(
				[50, 24].map((price) =>
					createProduct(apiHelpers, {
						name: `Subscription ${getRandomString()}`,
						price,
						subscription: true,
					})
				)
			);

		const product = await createProduct(apiHelpers, {
			name: `Importable ${getRandomString()}`,
			price: 34,
		});

		await setUpWishListImport(
			apiHelpers,
			orderImportPage,
			page,
			pendingOrdersPage,
			{
				wishListProducts: [
					firstSubscriptionProduct,
					product,
					secondSubscriptionProduct,
				],
			}
		);

		await expectPreviewStatuses(orderImportPage, [
			[firstSubscriptionProduct, 'OK'],
			[
				product,
				'Cart cannot contain both subscription and non-subscription products.',
			],
			[
				secondSubscriptionProduct,
				'Your cart can contain only one subscription product.',
			],
		]);

		await orderImportPage.importButton().click();

		await expect(orderImportPage.importedRowsAlert(1)).toBeVisible();
		await expect(orderImportPage.notImportedRowsAlert(2)).toBeVisible();

		await expect(async () => {
			expect(
				await getTableRowCells(
					pendingOrdersPage.orderItemsTable,
					firstSubscriptionProduct.name.en_US
				)
			).toMatchObject({
				QUANTITY: '1',
				SKU: firstSubscriptionProduct.skus[0].sku,
				TOTAL: '$ 50.00',
			});
		}).toPass({timeout: 30000});

		for (const notImportedProduct of [product, secondSubscriptionProduct]) {
			await expect(
				pendingOrdersPage.orderItemsTable.getByText(
					notImportedProduct.name.en_US
				)
			).toHaveCount(0);
		}
	}
);

test(
	'Non subscription products are imported when the subscription product is not the first one',
	{tag: ['@COMMERCE-7791', '@LPD-105666']},
	async ({apiHelpers, orderImportPage, page, pendingOrdersPage}) => {
		const [firstProduct, secondProduct] = await Promise.all(
			[24, 34].map((price) =>
				createProduct(apiHelpers, {
					name: `Importable ${getRandomString()}`,
					price,
				})
			)
		);

		const subscriptionProduct = await createProduct(apiHelpers, {
			name: `Subscription ${getRandomString()}`,
			price: 50,
			subscription: true,
		});

		await setUpWishListImport(
			apiHelpers,
			orderImportPage,
			page,
			pendingOrdersPage,
			{
				wishListProducts: [
					firstProduct,
					subscriptionProduct,
					secondProduct,
				],
			}
		);

		await expectPreviewStatuses(orderImportPage, [
			[firstProduct, 'OK'],
			[
				subscriptionProduct,
				'Cart cannot contain both subscription and non-subscription products.',
			],
			[secondProduct, 'OK'],
		]);

		await orderImportPage.importButton().click();

		await expect(orderImportPage.importedRowsAlert(2)).toBeVisible();
		await expect(orderImportPage.notImportedRowsAlert(1)).toBeVisible();

		await expect(async () => {
			for (const product of [firstProduct, secondProduct]) {
				expect(
					await getTableRowCells(
						pendingOrdersPage.orderItemsTable,
						product.name.en_US
					)
				).toMatchObject({
					QUANTITY: '1',
					SKU: product.skus[0].sku,
					TOTAL: `$ ${product.skus[0].price.toFixed(2)}`,
				});
			}
		}).toPass({timeout: 30000});

		await expect(
			pendingOrdersPage.orderItemsTable.getByText(
				subscriptionProduct.name.en_US
			)
		).toHaveCount(0);
	}
);

test(
	'Nothing is imported when the order already contains a subscription product',
	{tag: ['@COMMERCE-7792', '@LPD-105666']},
	async ({apiHelpers, orderImportPage, page, pendingOrdersPage}) => {
		const [orderedSubscriptionProduct, subscriptionProduct] =
			await Promise.all(
				[120, 50].map((price) =>
					createProduct(apiHelpers, {
						name: `Subscription ${getRandomString()}`,
						price,
						subscription: true,
					})
				)
			);

		const [firstProduct, secondProduct] = await Promise.all(
			[24, 34].map((price) =>
				createProduct(apiHelpers, {
					name: `Importable ${getRandomString()}`,
					price,
				})
			)
		);

		await setUpWishListImport(
			apiHelpers,
			orderImportPage,
			page,
			pendingOrdersPage,
			{
				cartItems: [{product: orderedSubscriptionProduct, quantity: 1}],
				wishListProducts: [
					subscriptionProduct,
					firstProduct,
					secondProduct,
				],
			}
		);

		await expectPreviewStatuses(orderImportPage, [
			[
				subscriptionProduct,
				'Your cart can contain only one subscription product.',
			],
			[
				firstProduct,
				'Cart cannot contain both subscription and non-subscription products.',
			],
			[
				secondProduct,
				'Cart cannot contain both subscription and non-subscription products.',
			],
		]);

		await orderImportPage.importButton().click();

		await expect(orderImportPage.notImportedRowsAlert(3)).toBeVisible();

		await expect(async () => {
			expect(
				await getTableRowCells(
					pendingOrdersPage.orderItemsTable,
					orderedSubscriptionProduct.name.en_US
				)
			).toMatchObject({
				QUANTITY: '1',
				SKU: orderedSubscriptionProduct.skus[0].sku,
				TOTAL: '$ 120.00',
			});
		}).toPass({timeout: 30000});

		await expect(
			pendingOrdersPage.orderItemsTable.locator('tbody tr')
		).toHaveCount(1);
	}
);

test(
	'A subscription product is not imported into an order of non subscription products',
	{tag: ['@COMMERCE-7793', '@LPD-105666']},
	async ({apiHelpers, orderImportPage, page, pendingOrdersPage}) => {
		const [orderedProduct, untouchedProduct, addedProduct] =
			await Promise.all(
				[24, 120, 34].map((price) =>
					createProduct(apiHelpers, {
						name: `Importable ${getRandomString()}`,
						price,
					})
				)
			);

		const subscriptionProduct = await createProduct(apiHelpers, {
			name: `Subscription ${getRandomString()}`,
			price: 50,
			subscription: true,
		});

		await setUpWishListImport(
			apiHelpers,
			orderImportPage,
			page,
			pendingOrdersPage,
			{
				cartItems: [
					{product: orderedProduct, quantity: 1},
					{product: untouchedProduct, quantity: 1},
				],
				wishListProducts: [
					orderedProduct,
					subscriptionProduct,
					addedProduct,
				],
			}
		);

		await expectPreviewStatuses(orderImportPage, [
			[orderedProduct, 'OK'],
			[
				subscriptionProduct,
				'Cart cannot contain both subscription and non-subscription products.',
			],
			[addedProduct, 'OK'],
		]);

		await orderImportPage.importButton().click();

		await expect(orderImportPage.importedRowsAlert(2)).toBeVisible();
		await expect(orderImportPage.notImportedRowsAlert(1)).toBeVisible();

		await expect(async () => {
			expect(
				await getTableRowCells(
					pendingOrdersPage.orderItemsTable,
					orderedProduct.name.en_US
				)
			).toMatchObject({
				QUANTITY: '2',
				SKU: orderedProduct.skus[0].sku,
				TOTAL: '$ 48.00',
			});
			expect(
				await getTableRowCells(
					pendingOrdersPage.orderItemsTable,
					untouchedProduct.name.en_US
				)
			).toMatchObject({
				QUANTITY: '1',
				SKU: untouchedProduct.skus[0].sku,
				TOTAL: '$ 120.00',
			});
			expect(
				await getTableRowCells(
					pendingOrdersPage.orderItemsTable,
					addedProduct.name.en_US
				)
			).toMatchObject({
				QUANTITY: '1',
				SKU: addedProduct.skus[0].sku,
				TOTAL: '$ 34.00',
			});
		}).toPass({timeout: 30000});

		await expect(
			pendingOrdersPage.orderItemsTable.getByText(
				subscriptionProduct.name.en_US
			)
		).toHaveCount(0);
	}
);

test(
	'A discounted product keeps its price variation when imported from a wish list',
	{tag: ['@COMMERCE-7721', '@LPD-105666']},
	async ({apiHelpers, orderImportPage, page, pendingOrdersPage}) => {
		const discountedProduct = await createProduct(apiHelpers, {
			name: `Discounted ${getRandomString()}`,
			price: 50,
		});

		const product = await createProduct(apiHelpers, {
			name: `Importable ${getRandomString()}`,
			price: 24,
		});

		await apiHelpers.headlessCommerceAdminPricing.postDiscount({
			discountProducts: [{productId: discountedProduct.productId}],
			percentageLevel1: 20,
			usePercentage: true,
		});

		await setUpWishListImport(
			apiHelpers,
			orderImportPage,
			page,
			pendingOrdersPage,
			{
				cartItems: [
					{product: discountedProduct, quantity: 1},
					{product, quantity: 1},
				],
				wishListProducts: [discountedProduct, product],
			}
		);

		await expectPreviewStatuses(orderImportPage, [
			[discountedProduct, 'OK'],
			[product, 'OK'],
		]);

		await orderImportPage.importButton().click();

		await expect(orderImportPage.importedRowsAlert(2)).toBeVisible();

		await expect(async () => {
			expect(
				await getTableRowCells(
					pendingOrdersPage.orderItemsTable,
					discountedProduct.name.en_US
				)
			).toMatchObject({
				'DISCOUNT': '$ 20.00',
				'LIST PRICE': '$ 50.00',
				'QUANTITY': '2',
				'SKU': discountedProduct.skus[0].sku,
				'TOTAL': '$ 80.00',
			});
			expect(
				await getTableRowCells(
					pendingOrdersPage.orderItemsTable,
					product.name.en_US
				)
			).toMatchObject({
				'DISCOUNT': '$ 0.00',
				'LIST PRICE': '$ 24.00',
				'QUANTITY': '2',
				'SKU': product.skus[0].sku,
				'TOTAL': '$ 48.00',
			});
		}).toPass({timeout: 30000});
	}
);

test(
	'An order imported from a wish list can be checked out',
	{tag: ['@LPD-105666']},
	async ({
		apiHelpers,
		checkoutPage,
		orderDetailsPage,
		orderImportPage,
		page,
		pendingOrdersPage,
		placedOrderPage,
		placedOrdersPage,
	}) => {
		const products = await Promise.all(
			[24, 50, 34].map((price) =>
				createProduct(apiHelpers, {
					name: `Importable ${getRandomString()}`,
					price,
				})
			)
		);

		const {buyerUser, cart} = await setUpWishListImport(
			apiHelpers,
			orderImportPage,
			page,
			pendingOrdersPage,
			{
				cartItems: products.map((product) => ({product, quantity: 1})),
				wishListProducts: products,
			}
		);

		await orderImportPage.importButton().click();

		await expect(orderImportPage.importedRowsAlert(3)).toBeVisible();

		await expect(async () => {
			for (const product of products) {
				expect(
					await getTableRowCells(
						pendingOrdersPage.orderItemsTable,
						product.name.en_US
					)
				).toMatchObject({
					QUANTITY: '2',
					SKU: product.skus[0].sku,
					TOTAL: `$ ${(product.skus[0].price * 2).toFixed(2)}`,
				});
			}
		}).toPass({timeout: 30000});

		await orderDetailsPage.checkoutButton.click();

		await checkoutPage.addAddress({
			city: 'Test City',
			countryLabel: 'United States',
			name: buyerUser.name,
			regionLabel: 'Florida',
			street: 'Test Address',
			zip: '12345',
		});

		await checkoutPage.continueButton.click();

		await page.waitForURL((url) => url.href.includes('shipping-method'));

		await checkoutPage.continueButton.click();

		await page.waitForURL((url) => url.href.includes('order-summary'));

		await checkoutPage.continueButton.click();

		await expect(checkoutPage.orderSuccessMessage).toBeVisible();

		await page.goto(`/web${site.friendlyUrlPath}/placed-orders`);

		await placedOrdersPage.orderRowLink(cart.id).click();

		await expect(async () => {
			for (const product of products) {
				expect(
					await getTableRowCells(
						placedOrdersPage.table,
						product.name.en_US
					)
				).toMatchObject({
					QUANTITY: '2',
					SKU: product.skus[0].sku,
					TOTAL: `$ ${(product.skus[0].price * 2).toFixed(2)}`,
				});
			}

			expect(await placedOrderPage.getOrderPrices()).toMatchObject({
				DELIVERY: '$ 15.00',
				SUBTOTAL: '$ 216.00',
				TAX: '$ 0.00',
				TOTAL: '$ 231.00',
			});
		}).toPass({timeout: 30000});
	}
);

test(
	'A wish list item that does not resolve to a SKU cannot be imported',
	{tag: ['@LPD-105666']},
	async ({
		apiHelpers,
		commerceThemeMiniumCatalogPage,
		orderImportPage,
		page,
		pendingOrdersPage,
	}) => {
		const productName = `multi-sku-${getRandomString()}`;

		const option = await apiHelpers.headlessCommerceAdminCatalog.postOption(
			'select',
			`size-${getRandomString()}`
		);

		const product =
			await apiHelpers.headlessCommerceAdminCatalog.postProduct({
				catalogId,
				name: {en_US: productName},
				productOptions: [
					{
						fieldType: 'select',
						key: option.key,
						name: {en_US: 'Size'},
						optionId: option.id,
						priceType: 'static',
						priority: 1,
						productOptionValues: [
							{key: 'small', name: {en_US: 'Small'}, priority: 1},
							{key: 'large', name: {en_US: 'Large'}, priority: 2},
						],
						skuContributor: true,
					},
				],
				skus: ['small', 'large'].map((value) => ({
					cost: 24,
					price: 24,
					published: true,
					purchasable: true,
					sku: `SKU-${value}-${productName}`,
					skuOptions: [{key: option.key, value}],
				})),
			});

		const {account, buyerUser} = await createAccountWithBuyerUser(
			apiHelpers,
			site.id
		);

		const cart = await apiHelpers.headlessCommerceDeliveryCart.postCart(
			{accountId: account.id},
			channel.id
		);

		await performUserSwitch(page, buyerUser.alternateName);

		await page.goto(`/web${site.friendlyUrlPath}/catalog`);

		await expect(async () => {
			await commerceThemeMiniumCatalogPage.catalogSearch.fill(
				productName
			);

			await commerceThemeMiniumCatalogPage.catalogSearch.press('Enter');

			await expect(
				commerceThemeMiniumCatalogPage.productCardAddToWishListButton(
					productName
				)
			).toBeVisible({timeout: 5000});
		}).toPass({timeout: 30000});

		const wishListItemResponse = page.waitForResponse((response) =>
			response.url().includes('/o/commerce-ui/wish-list-item')
		);

		await commerceThemeMiniumCatalogPage
			.productCardAddToWishListButton(productName)
			.click();

		await wishListItemResponse;

		await page.goto(`/web${site.friendlyUrlPath}/pending-orders`);

		await pendingOrdersPage.orderRowLink(cart.id).click();

		await orderImportPage.openImportModal();

		await orderImportPage.selectSource('Default');

		await expect(async () => {
			expect(
				await orderImportPage.previewRowCells(product.name.en_US)
			).toMatchObject({
				'IMPORT STATUS': 'The product is no longer available.',
				'QUANTITY': '1',
				'SKU': '',
			});
		}).toPass({timeout: 30000});

		await orderImportPage.importButton().click();

		await expect(orderImportPage.notImportedRowsAlert(1)).toBeVisible();
		await expect(
			pendingOrdersPage.orderItemsTable.getByText(product.name.en_US)
		).toHaveCount(0);
	}
);
