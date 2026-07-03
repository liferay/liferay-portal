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
import getRandomString from '../../../../utils/getRandomString';
import {
	performLoginViaApi,
	performLogout,
	userData,
} from '../../../../utils/performLogin';
import getPageDefinition from '../../../layout-content-page-editor-web/main/utils/getPageDefinition';
import getWidgetDefinition from '../../../layout-content-page-editor-web/main/utils/getWidgetDefinition';
import {createAccountWithBuyerUser, miniumSetUp} from '../../utils/commerce';

export const test = mergeTests(
	commercePagesTest,
	dataApiHelpersTest,
	featureFlagsTest({
		'LPS-178052': {enabled: true},
	}),
	isolatedSiteTest,
	loginTest()
);

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