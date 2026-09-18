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
	OrderImportSource,
	getTableRowCells,
} from '../../../../pages/commerce/commerce-order-content-web/orderImportPage';
import {PendingOrdersPage} from '../../../../pages/commerce/commerce-order-content-web/pendingOrdersPage';
import {getRandomInt} from '../../../../utils/getRandomInt';
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
	}: {
		allowBackOrder?: boolean;
		name: string;
		price: number;
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
				purchasable: true,
				sku: `SKU-${name}`,
			},
		],
	});
}

async function setUpOrderImport(
	apiHelpers: DataApiHelpers,
	page: Page,
	pendingOrdersPage: PendingOrdersPage,
	{
		beforeUserSwitch,
		sourceOrders = [],
		targetItems = [],
	}: {
		beforeUserSwitch?: () => Promise<void>;
		sourceOrders?: Array<Array<{product: Product; quantity: number}>>;
		targetItems?: Array<{product: Product; quantity: number}>;
	}
) {
	const {account, buyerUser} = await createAccountWithBuyerUser(
		apiHelpers,
		site.id
	);

	const carts = [];

	for (const sourceOrder of sourceOrders) {
		carts.push(
			await apiHelpers.headlessCommerceDeliveryCart.postCart(
				{
					accountId: account.id,
					cartItems: sourceOrder.map(({product, quantity}) => ({
						quantity,
						skuId: product.skus[0].id,
					})),
				},
				channel.id
			)
		);
	}

	const targetCart = await apiHelpers.headlessCommerceDeliveryCart.postCart(
		{
			accountId: account.id,
			cartItems: targetItems.map(({product, quantity}) => ({
				quantity,
				skuId: product.skus[0].id,
			})),
		},
		channel.id
	);

	if (beforeUserSwitch) {
		await beforeUserSwitch();
	}

	await performUserSwitch(page, buyerUser.alternateName);

	await pendingOrdersPage.gotoOrder(site.friendlyUrlPath, targetCart.id);

	return {account, buyerUser, sourceCarts: carts, targetCart};
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
	'An existing order can be imported into a new order',
	{tag: ['@LPD-106266']},
	async ({apiHelpers, orderImportPage, page, pendingOrdersPage}) => {
		const product = await createProduct(apiHelpers, {
			name: `Importable ${getRandomString()}`,
			price: 24,
		});

		const {sourceCarts} = await setUpOrderImport(
			apiHelpers,
			page,
			pendingOrdersPage,
			{sourceOrders: [[{product, quantity: 1}]]}
		);

		await orderImportPage.openImportModal('Orders');

		await expect(async () => {
			await expect(
				orderImportPage.sourceLink(String(sourceCarts[0].id), 'Orders')
			).toBeVisible({timeout: 5000});
		}).toPass({timeout: 30000});

		await orderImportPage.selectSource(String(sourceCarts[0].id), 'Orders');

		await expect(async () => {
			expect(
				await orderImportPage.previewRowCells(
					product.name.en_US,
					'Orders'
				)
			).toMatchObject({
				'IMPORT STATUS': 'OK',
				'QUANTITY': '1',
				'SKU': product.skus[0].sku,
				'TOTAL PRICE': '$ 24.00',
				'UNIT PRICE': '$ 24.00',
			});
		}).toPass({timeout: 30000});

		await orderImportPage.importButton('Orders').click();

		await expect(orderImportPage.importedRowsAlert(1)).toBeVisible();
		await expect(async () => {
			expect(
				await getTableRowCells(
					pendingOrdersPage.orderItemsTable,
					product.name.en_US
				)
			).toMatchObject({
				'LIST PRICE': '$ 24.00',
				'QUANTITY': '1',
				'SKU': product.skus[0].sku,
				'TOTAL': '$ 24.00',
			});
		}).toPass({timeout: 30000});
	}
);

test(
	'Orders can be found by ID in the import picker',
	{tag: ['@LPD-106266']},
	async ({apiHelpers, orderImportPage, page, pendingOrdersPage}) => {
		const [firstProduct, secondProduct] = await Promise.all(
			[24, 34].map((price) =>
				createProduct(apiHelpers, {
					name: `Importable ${getRandomString()}`,
					price,
				})
			)
		);

		const {sourceCarts} = await setUpOrderImport(
			apiHelpers,
			page,
			pendingOrdersPage,
			{
				sourceOrders: [
					[{product: firstProduct, quantity: 1}],
					[{product: secondProduct, quantity: 1}],
				],
			}
		);

		await orderImportPage.openImportModal('Orders');

		await expect(async () => {
			await expect(orderImportPage.sourceRows('Orders')).toHaveCount(2, {
				timeout: 5000,
			});
		}).toPass({timeout: 30000});

		await orderImportPage.search(String(sourceCarts[0].id), 'Orders');

		await expect(async () => {
			await expect(orderImportPage.sourceRows('Orders')).toHaveCount(1, {
				timeout: 5000,
			});
			await expect(
				orderImportPage.sourceLink(String(sourceCarts[0].id), 'Orders')
			).toBeVisible({timeout: 5000});
			await expect(
				orderImportPage.sourceLink(String(sourceCarts[1].id), 'Orders')
			).toHaveCount(0, {timeout: 5000});
		}).toPass({timeout: 30000});
	}
);

test(
	'Every order of the account except the current one is listed in the import picker',
	{tag: ['@LPD-106266']},
	async ({apiHelpers, orderImportPage, page, pendingOrdersPage}) => {
		const products = await Promise.all(
			[24, 3, 34].map((price) =>
				createProduct(apiHelpers, {
					name: `Importable ${getRandomString()}`,
					price,
				})
			)
		);

		const {sourceCarts, targetCart} = await setUpOrderImport(
			apiHelpers,
			page,
			pendingOrdersPage,
			{
				sourceOrders: products.map((product) => [
					{product, quantity: 1},
				]),
			}
		);

		await orderImportPage.openImportModal('Orders');

		await expect(async () => {
			await expect(orderImportPage.sourceRows('Orders')).toHaveCount(3, {
				timeout: 5000,
			});

			for (const sourceCart of sourceCarts) {
				expect(
					await getTableRowCells(
						orderImportPage.sourceTable('Orders'),
						String(sourceCart.id)
					)
				).toMatchObject({'ORDER ID': String(sourceCart.id)});
			}

			await expect(
				orderImportPage.sourceLink(String(targetCart.id), 'Orders')
			).toHaveCount(0, {timeout: 5000});
		}).toPass({timeout: 30000});
	}
);

test(
	'An empty state is shown when there is no order to import',
	{tag: ['@LPD-106266']},
	async ({apiHelpers, orderImportPage, page, pendingOrdersPage}) => {
		await setUpOrderImport(apiHelpers, page, pendingOrdersPage, {});

		await expect(orderImportPage.sourceMenuItem('Orders')).toHaveCount(0);

		await orderImportPage.orderActionsButton.click();

		await expect(orderImportPage.sourceMenuItem('Orders')).toBeVisible();

		await orderImportPage.sourceMenuItem('Orders').click();

		await expect(async () => {
			await expect(orderImportPage.emptyState('Orders')).toBeVisible({
				timeout: 5000,
			});
			await expect(orderImportPage.sourceRows('Orders')).toHaveCount(0, {
				timeout: 5000,
			});
		}).toPass({timeout: 30000});
	}
);

test(
	'The import preview reports the items that cannot be imported',
	{tag: ['@LPD-106266']},
	async ({apiHelpers, orderImportPage, page, pendingOrdersPage}) => {
		const [firstProduct, secondProduct] = await Promise.all(
			[15, 34].map((price) =>
				createProduct(apiHelpers, {
					name: `Importable ${getRandomString()}`,
					price,
				})
			)
		);

		const outOfStockProduct = await createProduct(apiHelpers, {
			allowBackOrder: false,
			name: `Out Of Stock ${getRandomString()}`,
			price: 24,
		});

		const warehouse =
			await apiHelpers.headlessCommerceAdminInventoryApiHelper.postWarehouses(
				{
					active: true,
					latitude: getRandomInt(),
					longitude: getRandomInt(),
				}
			);

		await apiHelpers.headlessCommerceAdminInventoryApiHelper.postWarehousesChannels(
			warehouse.id,
			channel.id
		);

		const warehouseItem =
			await apiHelpers.headlessCommerceAdminInventoryApiHelper.postWarehousesWarehouseItems(
				warehouse.id,
				{quantity: 1, sku: outOfStockProduct.skus[0].sku}
			);

		const {sourceCarts} = await setUpOrderImport(
			apiHelpers,
			page,
			pendingOrdersPage,
			{
				beforeUserSwitch: async () => {
					await apiHelpers.headlessCommerceAdminInventoryApiHelper.patchWarehouseItem(
						warehouseItem.id,
						{quantity: 0, sku: outOfStockProduct.skus[0].sku}
					);
				},
				sourceOrders: [
					[
						{product: outOfStockProduct, quantity: 1},
						{product: firstProduct, quantity: 1},
						{product: secondProduct, quantity: 1},
					],
				],
			}
		);

		await orderImportPage.openImportModal('Orders');

		await expect(async () => {
			await expect(
				orderImportPage.sourceLink(String(sourceCarts[0].id), 'Orders')
			).toBeVisible({timeout: 5000});
		}).toPass({timeout: 30000});

		await orderImportPage.selectSource(String(sourceCarts[0].id), 'Orders');

		await expect(async () => {
			await expect(orderImportPage.previewRows('Orders')).toHaveCount(3, {
				timeout: 5000,
			});

			expect(
				await orderImportPage.previewRowCells(
					outOfStockProduct.name.en_US,
					'Orders'
				)
			).toMatchObject({
				'IMPORT STATUS': 'The specified quantity is unavailable.',
				'QUANTITY': '1',
				'SKU': outOfStockProduct.skus[0].sku,
				'TOTAL PRICE': '',
				'UNIT PRICE': '',
			});

			for (const product of [firstProduct, secondProduct]) {
				expect(
					await orderImportPage.previewRowCells(
						product.name.en_US,
						'Orders'
					)
				).toMatchObject({
					'IMPORT STATUS': 'OK',
					'QUANTITY': '1',
					'SKU': product.skus[0].sku,
					'TOTAL PRICE': `$ ${product.skus[0].price.toFixed(2)}`,
					'UNIT PRICE': `$ ${product.skus[0].price.toFixed(2)}`,
				});
			}
		}).toPass({timeout: 30000});

		await orderImportPage.importButton('Orders').click();

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
				outOfStockProduct.name.en_US
			)
		).toHaveCount(0);
	}
);

test(
	'An import can be aborted before and from the preview',
	{tag: ['@LPD-106266']},
	async ({apiHelpers, orderImportPage, page, pendingOrdersPage}) => {
		const [orderedProduct, sourceProduct] = await Promise.all(
			[15, 24].map((price) =>
				createProduct(apiHelpers, {
					name: `Importable ${getRandomString()}`,
					price,
				})
			)
		);

		const {sourceCarts, targetCart} = await setUpOrderImport(
			apiHelpers,
			page,
			pendingOrdersPage,
			{
				sourceOrders: [[{product: sourceProduct, quantity: 1}]],
				targetItems: [{product: orderedProduct, quantity: 1}],
			}
		);

		const expectOrderIsUnchanged = async (source: OrderImportSource) => {
			await expect(orderImportPage.modalFrame(source)).toHaveCount(0);
			await expect(page).toHaveURL(/pending-orders/);

			const cartItems =
				await apiHelpers.headlessCommerceDeliveryCart.getCartItems(
					targetCart.id
				);

			expect(cartItems.totalCount).toBe(1);
			expect(cartItems.items[0].skuId).toBe(orderedProduct.skus[0].id);
		};

		await test.step('Abort before choosing a source', async () => {
			await orderImportPage.openImportModal('CSV');

			await expect(orderImportPage.cancelButton('CSV')).toBeVisible();

			await orderImportPage.cancelButton('CSV').click();

			await expectOrderIsUnchanged('CSV');

			for (const source of ['Orders', 'Wish Lists'] as const) {
				await orderImportPage.openImportModal(source);

				await expect(orderImportPage.modalFrame(source)).toBeVisible();

				await orderImportPage.closeModalButton.click();

				await expectOrderIsUnchanged(source);
			}
		});

		await test.step('Abort from the preview', async () => {
			for (const abort of ['cancel', 'close'] as const) {
				await orderImportPage.openImportModal('Orders');

				await expect(async () => {
					await expect(
						orderImportPage.sourceLink(
							String(sourceCarts[0].id),
							'Orders'
						)
					).toBeVisible({timeout: 5000});
				}).toPass({timeout: 30000});

				await orderImportPage.selectSource(
					String(sourceCarts[0].id),
					'Orders'
				);

				await expect(
					orderImportPage.importButton('Orders')
				).toBeVisible();

				if (abort === 'cancel') {
					await orderImportPage.cancelButton('Orders').click();
				}
				else {
					await orderImportPage.closeModalButton.click();
				}

				await expectOrderIsUnchanged('Orders');
			}
		});
	}
);

test(
	'An imported order item can be deleted',
	{tag: ['@LPD-106266']},
	async ({apiHelpers, orderImportPage, page, pendingOrdersPage}) => {
		const product = await createProduct(apiHelpers, {
			name: `Importable ${getRandomString()}`,
			price: 24,
		});

		const {sourceCarts, targetCart} = await setUpOrderImport(
			apiHelpers,
			page,
			pendingOrdersPage,
			{sourceOrders: [[{product, quantity: 1}]]}
		);

		await orderImportPage.openImportModal('Orders');

		await expect(async () => {
			await expect(
				orderImportPage.sourceLink(String(sourceCarts[0].id), 'Orders')
			).toBeVisible({timeout: 5000});
		}).toPass({timeout: 30000});

		await orderImportPage.selectSource(String(sourceCarts[0].id), 'Orders');

		await expect(orderImportPage.importButton('Orders')).toBeVisible();

		await orderImportPage.importButton('Orders').click();

		await expect(orderImportPage.importedRowsAlert(1)).toBeVisible();
		await expect(async () => {
			expect(
				await getTableRowCells(
					pendingOrdersPage.orderItemsTable,
					product.name.en_US
				)
			).toMatchObject({
				QUANTITY: '1',
				SKU: product.skus[0].sku,
			});
		}).toPass({timeout: 30000});

		await (
			await pendingOrdersPage.orderItemsTableRowLink(product.name.en_US)
		).click();

		await pendingOrdersPage.deleteMenuItem.click();

		await expect(async () => {
			await expect(
				pendingOrdersPage.orderItemsTable.getByText(product.name.en_US)
			).toHaveCount(0, {timeout: 5000});
		}).toPass({timeout: 30000});

		const cartItems =
			await apiHelpers.headlessCommerceDeliveryCart.getCartItems(
				targetCart.id
			);

		expect(cartItems.totalCount).toBe(0);
	}
);

test(
	'An imported order item is merged when its product is added to the order again',
	{tag: ['@LPD-106266']},
	async ({
		apiHelpers,
		orderImportPage,
		page,
		pendingOrdersPage,
		productDetailsPage,
	}) => {
		const product = await createProduct(apiHelpers, {
			name: `Importable ${getRandomString()}`,
			price: 24,
		});

		const {sourceCarts, targetCart} = await setUpOrderImport(
			apiHelpers,
			page,
			pendingOrdersPage,
			{sourceOrders: [[{product, quantity: 1}]]}
		);

		await page.goto(`/web${site.friendlyUrlPath}/pending-orders`);

		await pendingOrdersPage.orderRowLink(targetCart.id).click();

		await orderImportPage.openImportModal('Orders');

		await expect(async () => {
			await expect(
				orderImportPage.sourceLink(String(sourceCarts[0].id), 'Orders')
			).toBeVisible({timeout: 5000});
		}).toPass({timeout: 30000});

		await orderImportPage.selectSource(String(sourceCarts[0].id), 'Orders');

		await expect(orderImportPage.importButton('Orders')).toBeVisible();

		await orderImportPage.importButton('Orders').click();

		await expect(orderImportPage.importedRowsAlert(1)).toBeVisible();
		await expect(async () => {
			expect(
				await getTableRowCells(
					pendingOrdersPage.orderItemsTable,
					product.name.en_US
				)
			).toMatchObject({
				QUANTITY: '1',
				SKU: product.skus[0].sku,
				TOTAL: '$ 24.00',
			});
		}).toPass({timeout: 30000});

		await (
			await pendingOrdersPage.orderItemsTableRowLink(product.name.en_US)
		).click();

		await pendingOrdersPage.viewMenuItem.click();

		await expect(productDetailsPage.addToCartButton).toBeVisible();

		await page.waitForLoadState('networkidle');

		await expect(async () => {
			const cartItemResponse = page.waitForResponse(
				(response) =>
					response.request().method() === 'POST' &&
					response.url().includes(`/carts/${targetCart.id}/items`),
				{timeout: 5000}
			);

			await productDetailsPage.addToCartButton.click();

			expect((await cartItemResponse).ok()).toBe(true);
		}).toPass({timeout: 30000});

		await expect(async () => {
			await pendingOrdersPage.gotoOrder(
				site.friendlyUrlPath,
				targetCart.id
			);

			expect(
				await getTableRowCells(
					pendingOrdersPage.orderItemsTable,
					product.name.en_US
				)
			).toMatchObject({
				QUANTITY: '2',
				SKU: product.skus[0].sku,
				TOTAL: '$ 48.00',
			});
		}).toPass({timeout: 30000});
	}
);
