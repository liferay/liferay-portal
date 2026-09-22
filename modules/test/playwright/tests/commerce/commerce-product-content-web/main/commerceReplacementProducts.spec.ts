/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {commercePagesTest} from '../../../../fixtures/commercePagesTest';
import {dataApiHelpersTest} from '../../../../fixtures/dataApiHelpersTest';
import {featureFlagsTest} from '../../../../fixtures/featureFlagsTest';
import {isolatedSiteTest} from '../../../../fixtures/isolatedSiteTest';
import {loginTest} from '../../../../fixtures/loginTest';
import {getTableRowCells} from '../../../../pages/commerce/commerce-order-content-web/orderImportPage';
import getRandomString from '../../../../utils/getRandomString';
import {
	performLoginViaApi,
	performLogout,
	userData,
} from '../../../../utils/performLogin';
import getPageDefinition from '../../../layout-content-page-editor-web/main/utils/getPageDefinition';
import getWidgetDefinition from '../../../layout-content-page-editor-web/main/utils/getWidgetDefinition';
import {
	createAccountWithBuyerUser,
	getSkusByName,
	miniumSetUp,
	zeroWarehouseStock,
} from '../../utils/commerce';

export const test = mergeTests(
	commercePagesTest,
	dataApiHelpersTest,
	featureFlagsTest({
		'LPS-178052': {enabled: true},
	}),
	isolatedSiteTest,
	loginTest()
);

test.afterEach(async ({page}) => {
	await performLoginViaApi({page, screenName: 'test'});
});

test(
	'Can view discontinued replacement SKUs in product details',
	{tag: '@LPD-49015'},
	async ({apiHelpers, page, productDetailsPage}) => {
		test.setTimeout(120000);

		const {site} = await miniumSetUp(apiHelpers);

		const account = await apiHelpers.headlessAdminUser.postAccount({
			name: getRandomString(),
			type: 'business',
		});

		const user = await apiHelpers.headlessAdminUser.postUserAccount();

		userData[user.alternateName] = {
			name: user.givenName,
			password: 'test',
			surname: user.familyName,
		};

		await apiHelpers.headlessAdminUser.assignUserToAccountByEmailAddress(
			account.id,
			[user.emailAddress]
		);

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
			],
		});

		await apiHelpers.headlessAdminUser.postRoleUserAccountAssociation(
			role.id,
			Number(user.id)
		);

		apiHelpers.data.push({
			id: `${role.id}_${user.id}`,
			type: 'roleUserAccountAssociation',
		});

		await apiHelpers.jsonWebServicesUser.addGroupUsers(site.id, [user.id]);

		const replacementSku =
			await apiHelpers.headlessCommerceAdminCatalog.getSkuByName(
				'MIN93015'
			);

		const skuList = [
			'MIN93016A',
			'MIN93016B',
			'MIN93016C',
			'MIN93027',
			'MIN93021',
		];

		for (const skuName of skuList) {
			const sku =
				await apiHelpers.headlessCommerceAdminCatalog.getSkuByName(
					skuName
				);

			await apiHelpers.headlessCommerceAdminCatalog.patchSku(sku.id, {
				cost: sku.cost,
				discontinued: true,
				price: sku.price,
				published: true,
				purchasable: sku.purchasable,
				replacementSkuId: replacementSku.id,
				sku: sku.sku,
			});
		}

		await performLogout(page);
		await performLoginViaApi({page, screenName: user.alternateName});

		await page.goto(`/web/${site.name}/p/abs-sensor`);

		await productDetailsPage.replacementsTab.click();

		for (const skuName of skuList) {
			await expect(
				productDetailsPage.replacementsTableCell(skuName)
			).toBeVisible();
		}
		await expect(
			productDetailsPage.paginationText('Showing 1 to 5 of 5 entries.')
		).toBeVisible();

		await productDetailsPage.replacementsSearchBar.fill('Wear Sensors');
		await productDetailsPage.replacementsSearchButton.click();

		for (const skuName of skuList) {
			if (skuName === 'MIN93027') {
				await expect(
					productDetailsPage.replacementsTableCell(skuName)
				).toBeVisible();
			}
			else {
				await expect(
					productDetailsPage.replacementsTableCell(skuName)
				).not.toBeVisible();
			}
		}
	}
);

test(
	'COMMERCE-12548 A SKU with a UOM can replace a discontinued SKU',
	{tag: '@COMMERCE-12548'},
	async ({
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

		const user = await apiHelpers.headlessAdminUser.postUserAccount();

		userData[user.alternateName] = {
			name: user.givenName,
			password: 'test',
			surname: user.familyName,
		};

		await apiHelpers.headlessAdminUser.assignUserToAccountByEmailAddress(
			account.id,
			[user.emailAddress]
		);

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
			],
		});

		await apiHelpers.headlessAdminUser.postRoleUserAccountAssociation(
			role.id,
			Number(user.id)
		);

		apiHelpers.data.push({
			id: `${role.id}_${user.id}`,
			type: 'roleUserAccountAssociation',
		});

		await apiHelpers.jsonWebServicesUser.addGroupUsers(site.id, [user.id]);

		const replacementSku =
			await apiHelpers.headlessCommerceAdminCatalog.getSkuByName(
				'MIN93015'
			);

		await apiHelpers.headlessCommerceAdminCatalog.postSkuUnitOfMeasure(
			replacementSku.id,
			{
				basePrice: 20,
				incrementalOrderQuantity: 0.6,
				key: 'UOM1KEY',
				name: {en_US: 'UOM1'},
				precision: 1,
				priority: 4,
			}
		);
		await apiHelpers.headlessCommerceAdminCatalog.postSkuUnitOfMeasure(
			replacementSku.id,
			{
				basePrice: 30,
				incrementalOrderQuantity: 1.5,
				key: 'UOM2KEY',
				name: {en_US: 'UOM2'},
				precision: 1,
				priority: 1,
			}
		);

		await apiHelpers.headlessCommerceAdminCatalog.patchProduct(
			replacementSku.productId,
			{
				name: {en_US: 'ABS Sensor'},
				productConfiguration: {
					minOrderQuantity: 0.1,
					multipleOrderQuantity: 0.1,
				},
			}
		);

		const discontinuedSku =
			await apiHelpers.headlessCommerceAdminCatalog.getSkuByName(
				'MIN55861'
			);

		const warehouses =
			await apiHelpers.headlessCommerceAdminInventoryApiHelper.getWarehousesPage();

		for (const warehouse of warehouses.items) {
			const warehouseItems =
				await apiHelpers.headlessCommerceAdminInventoryApiHelper.getWarehouseIdWarehouseItemsPage(
					warehouse.id
				);

			const warehouseItem = warehouseItems.items.find(
				(warehouseItem) => warehouseItem.sku === discontinuedSku.sku
			);

			if (warehouseItem) {
				await apiHelpers.headlessCommerceAdminInventoryApiHelper.patchWarehouseItem(
					warehouseItem.id,
					{quantity: 0, sku: warehouseItem.sku}
				);
			}
		}

		await apiHelpers.headlessCommerceAdminCatalog.patchSku(
			discontinuedSku.id,
			{
				cost: discontinuedSku.cost,
				discontinued: true,
				price: discontinuedSku.price,
				published: true,
				purchasable: discontinuedSku.purchasable,
				replacementSkuId: replacementSku.id,
				sku: discontinuedSku.sku,
			}
		);

		await apiHelpers.headlessCommerceAdminCatalog.patchProduct(
			discontinuedSku.productId,
			{
				name: {en_US: 'U-Joint'},
				productConfiguration: {
					allowBackOrder: false,
				},
			}
		);

		await performLogout(page);
		await performLoginViaApi({page, screenName: user.alternateName});

		await page.goto(`/web/${site.name}/p/u-joint`);

		await expect(productDetailsPage.addToCartButton).toBeDisabled();
		await expect(
			commerceThemeMiniumCatalogPage.quantitySelector(
				page.locator('.product-detail')
			)
		).toBeDisabled();

		await expect(productDetailsPage.replacementProductButton).toBeVisible();

		await productDetailsPage.replacementProductButton.click();

		await expect(page).toHaveURL(/\/p\/abs-sensor/);
		await expect(productDetailsPage.unitOfMeasureSelect).toHaveValue(
			'UOM2KEY'
		);
		await expect(
			commerceThemeMiniumCatalogPage.quantitySelector(
				page.locator('.product-detail')
			)
		).toHaveValue('1.5');

		await commerceMiniCartPage.quickAddToCart('MIN55861');

		const cartItem = commerceMiniCartPage.miniCartItem('MIN93015');

		await expect(cartItem).toBeVisible();
		await expect(cartItem.getByText('UOM2KEY')).toBeVisible();
		await expect(
			commerceThemeMiniumCatalogPage.quantitySelector(cartItem)
		).toHaveValue('1.5');
		await expect(cartItem.getByText('$ 30.00')).toBeVisible();

		await expect(
			commerceMiniCartPage.miniCartReplacementInfoMessage
		).toBeVisible();
		await expect(
			commerceMiniCartPage.miniCartItemReplacementLabel('MIN93015')
		).toBeVisible();
	}
);

test(
	'Replacement product row redirects to the discontinued product details page',
	{tag: '@LPD-97008'},
	async ({
		apiHelpers,
		commerceAdminChannelsPage,
		page,
		productDetailsPage,
		site,
	}) => {
		const channel =
			await apiHelpers.headlessCommerceAdminChannel.postChannel({
				siteGroupId: site.id,
			});

		await commerceAdminChannelsPage.changeCommerceChannelSiteType(
			channel.name,
			'B2B'
		);

		const catalog =
			await apiHelpers.headlessCommerceAdminCatalog.postCatalog();

		const {buyerUser} = await createAccountWithBuyerUser(
			apiHelpers,
			site.id
		);

		const suffix = getRandomString();

		const discontinuedProductName = `Test Simple Product ${suffix}`;
		const discontinuedSku = `SKU1002-${suffix}`;
		const replacementProductName = `Test Simple Product Replacement ${suffix}`;
		const replacementSku = `SKU1001-${suffix}`;

		const replacementProduct =
			await apiHelpers.headlessCommerceAdminCatalog.postProduct({
				catalogId: catalog.id,
				name: {en_US: replacementProductName},
				productType: 'simple',
				skus: [
					{
						cost: 0,
						price: 0,
						published: true,
						purchasable: true,
						sku: replacementSku,
					},
				],
			});

		await apiHelpers.headlessCommerceAdminCatalog.postProduct({
			catalogId: catalog.id,
			name: {en_US: discontinuedProductName},
			productType: 'simple',
			skus: [
				{
					cost: 0,
					discontinued: true,
					price: 0,
					published: true,
					purchasable: true,
					replacementSkuId: replacementProduct.skus[0].id,
					sku: discontinuedSku,
				},
			],
		});

		await apiHelpers.headlessDelivery.createSitePage({
			pageDefinition: getPageDefinition([
				getWidgetDefinition({
					id: getRandomString(),
					widgetName:
						'com_liferay_commerce_product_content_web_internal_portlet_CPContentPortlet',
				}),
			]),
			siteId: site.id,
			title: getRandomString(),
		});

		await performLogout(page);
		await performLoginViaApi({page, screenName: buyerUser.alternateName});

		await page.goto(
			`/web/${site.name}/p/${replacementProductName
				.toLowerCase()
				.replace(/ /g, '-')}`,
			{waitUntil: 'networkidle'}
		);

		await productDetailsPage.replacementsTab.click();

		await expect(
			productDetailsPage.replacementsTableCell(discontinuedSku)
		).toBeVisible();

		await page
			.getByRole('row', {name: discontinuedSku})
			.getByLabel('View')
			.click();

		await expect(
			await productDetailsPage.nameField(discontinuedProductName)
		).toBeVisible();
		await expect(
			await productDetailsPage.skuField(discontinuedSku)
		).toBeVisible();
	}
);

test(
	'A discontinued product shows its end of life date and offers no replacements list',
	{tag: ['@COMMERCE-9347', '@LPD-106905']},
	async ({
		apiHelpers,
		commerceAdminChannelsPage,
		page,
		productDetailsPage,
		site,
	}) => {
		const channel =
			await apiHelpers.headlessCommerceAdminChannel.postChannel({
				siteGroupId: site.id,
			});

		await commerceAdminChannelsPage.changeCommerceChannelSiteType(
			channel.name,
			'B2B'
		);

		const catalog =
			await apiHelpers.headlessCommerceAdminCatalog.postCatalog();

		const {buyerUser} = await createAccountWithBuyerUser(
			apiHelpers,
			site.id
		);

		const suffix = getRandomString();

		const discontinuedProductName = `Test Simple Product ${suffix}`;
		const discontinuedSku = `SKU1002-${suffix}`;
		const replacementProductName = `Test Simple Product Replacement ${suffix}`;
		const replacementSku = `SKU1001-${suffix}`;

		const replacementProduct =
			await apiHelpers.headlessCommerceAdminCatalog.postProduct({
				catalogId: catalog.id,
				name: {en_US: replacementProductName},
				productType: 'simple',
				skus: [
					{
						cost: 0,
						price: 0,
						published: true,
						purchasable: true,
						sku: replacementSku,
					},
				],
			});

		await apiHelpers.headlessCommerceAdminCatalog.postProduct({
			catalogId: catalog.id,
			name: {en_US: discontinuedProductName},
			productType: 'simple',
			skus: [
				{
					cost: 0,
					discontinued: true,
					discontinuedDate: new Date().toISOString(),
					price: 0,
					published: true,
					purchasable: true,
					replacementSkuId: replacementProduct.skus[0].id,
					sku: discontinuedSku,
				},
			],
		});

		await apiHelpers.headlessDelivery.createSitePage({
			pageDefinition: getPageDefinition([
				getWidgetDefinition({
					id: getRandomString(),
					widgetName:
						'com_liferay_commerce_product_content_web_internal_portlet_CPContentPortlet',
				}),
			]),
			siteId: site.id,
			title: getRandomString(),
		});

		await performLogout(page);
		await performLoginViaApi({page, screenName: buyerUser.alternateName});

		await page.goto(
			`/web/${site.name}/p/${discontinuedProductName
				.toLowerCase()
				.replace(/ /g, '-')}`,
			{waitUntil: 'networkidle'}
		);

		await expect(
			await productDetailsPage.nameField(discontinuedProductName)
		).toBeVisible();

		await test.step('The product is flagged as discontinued and dated', async () => {
			await expect(productDetailsPage.inStockQuantity).toContainText(
				'Discontinued'
			);

			const endOfLifeDate = await productDetailsPage
				.productDetailValue('End of Life')
				.innerText();

			expect(new Date(endOfLifeDate).toDateString()).toEqual(
				new Date().toDateString()
			);
		});

		await test.step('The discontinued product lists no replacements of its own', async () => {
			await expect(productDetailsPage.replacementsTab).toHaveCount(0);
			await expect(
				page.getByText('Replacements', {exact: true})
			).toHaveCount(0);
			await expect(
				page.getByText(replacementSku, {exact: true})
			).toHaveCount(0);
		});
	}
);

test(
	'A discontinued SKU without stock puts the first available replacement of its chain in the cart',
	{tag: ['@COMMERCE-12026', '@LPD-106905']},
	async ({apiHelpers, commerceMiniCartPage, page}) => {
		test.setTimeout(300000);

		const {site} = await miniumSetUp(apiHelpers);

		const {buyerUser} = await createAccountWithBuyerUser(
			apiHelpers,
			site.id
		);

		const skuByName = await getSkusByName(apiHelpers, [
			'MIN55861',
			'MIN93015',
			'MIN93017',
		]);

		await test.step('Discontinue the SKU and its replacement, and leave both without stock or back orders', async () => {
			for (const [skuName, replacementSkuName] of [
				['MIN55861', 'MIN93015'],
				['MIN93015', 'MIN93017'],
			]) {
				const sku = skuByName[skuName];

				await apiHelpers.headlessCommerceAdminCatalog.patchSku(
					String(sku.id),
					{
						discontinued: true,
						published: true,
						purchasable: true,
						replacementSkuId: skuByName[replacementSkuName].id,
						sku: sku.sku,
					}
				);
			}

			await zeroWarehouseStock(apiHelpers, ['MIN55861', 'MIN93015']);

			await Promise.all(
				['ABS Sensor', 'U-Joint'].map(async (productName) => {
					const product =
						await apiHelpers.headlessCommerceAdminCatalog.getProductByName(
							productName
						);

					await apiHelpers.headlessCommerceAdminCatalog.patchProduct(
						String(product.productId),
						{
							name: product.name,
							productConfiguration: {allowBackOrder: false},
						}
					);
				})
			);
		});

		await performLogout(page);
		await performLoginViaApi({page, screenName: buyerUser.alternateName});

		await page.goto(`/web/${site.name}/catalog`, {
			waitUntil: 'networkidle',
		});

		await commerceMiniCartPage.quickAddToCart('MIN55861');

		await expect(
			commerceMiniCartPage.miniCartSku('MIN93017')
		).toBeVisible();
		await expect(
			commerceMiniCartPage.miniCartItemReplacementLabel(
				'Premium Brake Fluid'
			)
		).toBeVisible();
		await expect(
			commerceMiniCartPage.miniCartReplacementInfoMessage
		).toBeVisible();

		for (const skuName of ['MIN55861', 'MIN93015']) {
			await expect(commerceMiniCartPage.miniCartSku(skuName)).toHaveCount(
				0
			);
		}
	}
);

test(
	'A discontinued product is replaced when an order that holds it is imported',
	{tag: ['@COMMERCE-9352', '@LPD-106905']},
	async ({apiHelpers, orderImportPage, page, pendingOrdersPage}) => {
		test.setTimeout(300000);

		const {channel, site} = await miniumSetUp(apiHelpers);

		const {account, buyerUser} = await createAccountWithBuyerUser(
			apiHelpers,
			site.id
		);

		const {MIN55861: discontinuedSku, MIN93015: replacementSku} =
			await getSkusByName(apiHelpers, ['MIN55861', 'MIN93015']);

		const sourceCart =
			await apiHelpers.headlessCommerceDeliveryCart.postCart(
				{
					accountId: account.id,
					cartItems: [{quantity: 1, skuId: discontinuedSku.id}],
				},
				channel.id
			);

		const targetCart =
			await apiHelpers.headlessCommerceDeliveryCart.postCart(
				{accountId: account.id, cartItems: []},
				channel.id
			);

		await test.step('Discontinue the ordered SKU, empty its stock and point it at a replacement', async () => {
			await zeroWarehouseStock(apiHelpers, [discontinuedSku.sku]);

			await apiHelpers.headlessCommerceAdminCatalog.patchSku(
				String(discontinuedSku.id),
				{
					discontinued: true,
					published: true,
					purchasable: true,
					replacementSkuId: replacementSku.id,
					sku: discontinuedSku.sku,
				}
			);
		});

		await performLogout(page);
		await performLoginViaApi({page, screenName: buyerUser.alternateName});

		await pendingOrdersPage.gotoOrder(site.friendlyUrlPath, targetCart.id);

		await orderImportPage.openImportModal('Orders');

		await expect(async () => {
			await expect(
				orderImportPage.sourceLink(String(sourceCart.id), 'Orders')
			).toBeVisible({timeout: 5000});
		}).toPass({timeout: 30000});

		await orderImportPage.selectSource(String(sourceCart.id), 'Orders');

		await expect(async () => {
			expect(
				await orderImportPage.previewRowCells('ABS Sensor', 'Orders')
			).toMatchObject({
				'IMPORT STATUS': 'OK',
				'QUANTITY': '1',
				'SKU': replacementSku.sku,
				'TOTAL PRICE': '$ 50.00',
				'UNIT PRICE': '$ 50.00',
			});
		}).toPass({timeout: 30000});

		await orderImportPage.importButton('Orders').click();

		await expect(orderImportPage.importedRowsAlert(1)).toBeVisible();

		await expect(async () => {
			expect(
				await getTableRowCells(
					pendingOrdersPage.orderItemsTable,
					'ABS Sensor'
				)
			).toMatchObject({
				'LIST PRICE': '$ 50.00',
				'QUANTITY': '1',
				'SKU': replacementSku.sku,
				'TOTAL': '$ 50.00',
			});
		}).toPass({timeout: 30000});

		await expect(
			pendingOrdersPage.orderItemsTable.getByText('U-Joint')
		).toHaveCount(0);
	}
);
