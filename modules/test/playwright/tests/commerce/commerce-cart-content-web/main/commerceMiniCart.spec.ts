/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Locator, expect, mergeTests} from '@playwright/test';

import {commercePagesTest} from '../../../../fixtures/commercePagesTest';
import {dataApiHelpersTest} from '../../../../fixtures/dataApiHelpersTest';
import {featureFlagsTest} from '../../../../fixtures/featureFlagsTest';
import {globalMenuPagesTest} from '../../../../fixtures/globalMenuPagesTest';
import {loginTest} from '../../../../fixtures/loginTest';
import {DataApiHelpers} from '../../../../helpers/ApiHelpers';
import {liferayConfig} from '../../../../liferay.config';
import {CommerceAdminProductPage} from '../../../../pages/commerce/commerce-product-definitions-web/commerceAdminProductPage';
import {CommerceThemeMiniumCatalogPage} from '../../../../pages/commerce/commerce-theme-minium/commerceThemeMiniumCatalogPage';
import {CommerceMiniCartPage} from '../../../../pages/commerce/commerceMiniCartPage';
import {clickAndExpectToBeHidden} from '../../../../utils/clickAndExpectToBeHidden';
import {clickAndExpectToBeVisible} from '../../../../utils/clickAndExpectToBeVisible';
import getRandomString from '../../../../utils/getRandomString';
import performLogin, {
	performLoginViaApi,
	performLogout,
	performUserSwitchViaApi,
} from '../../../../utils/performLogin';
import {waitForAlert} from '../../../../utils/waitForAlert';
import getFragmentDefinition from '../../../layout-content-page-editor-web/main/utils/getFragmentDefinition';
import getPageDefinition from '../../../layout-content-page-editor-web/main/utils/getPageDefinition';
import getWidgetDefinition from '../../../layout-content-page-editor-web/main/utils/getWidgetDefinition';
import {
	TProductOptionSpec,
	createAccountWithBuyerUser,
	createProductWithOptions,
	expectBrakeFluidCartItems,
	findSkuByOptionValueKeys,
	getSkusByName,
	miniumSetUp,
	setUpBrakeFluidUnitsOfMeasure,
	unitOfMeasurePriceLabel,
	zeroWarehouseStock,
} from '../../utils/commerce';

export const test = mergeTests(
	commercePagesTest,
	dataApiHelpersTest,
	featureFlagsTest({
		'LPS-178052': {enabled: true},
	}),
	globalMenuPagesTest,
	loginTest()
);

test.afterEach(async ({page}) => {
	await performLoginViaApi({page, screenName: 'test'});
});

type TBundleOptionValues = {
	deliveryDate: string;
	engraving: string;
	extras: string[];
	floors: string;
	giftWrap: boolean;
	material: string;
};

async function buildColorOptionSpecs(
	apiHelpers: DataApiHelpers,
	{
		deltaPrices = true,
		priceType = 'static',
	}: {deltaPrices?: boolean; priceType?: string} = {}
): Promise<TProductOptionSpec[]> {
	const [absSensorSku, brakeRotorsSku] = await Promise.all(
		['MIN93015', 'MIN93020'].map((skuName) =>
			apiHelpers.headlessCommerceAdminCatalog.getSkuByName(skuName)
		)
	);

	return [
		{
			fieldType: 'select',
			name: 'Color',
			priceType,
			skuContributor: true,
			values: [
				{
					...(deltaPrices && {deltaPrice: 20}),
					key: 'blue',
					name: 'Blue',
					skuId: absSensorSku.id,
				},
				{
					...(deltaPrices && {deltaPrice: 30}),
					key: 'white',
					name: 'White',
					skuId: brakeRotorsSku.id,
				},
			],
		},
	];
}

async function expectBundledCartItem(
	commerceMiniCartPage: CommerceMiniCartPage,
	commerceThemeMiniumCatalogPage: CommerceThemeMiniumCatalogPage,
	cartItem: Locator,
	{
		linkedProductName,
		linkedQuantity,
		price,
		quantity,
	}: {
		linkedProductName: string;
		linkedQuantity: number;
		price?: string;
		quantity: number;
	}
) {
	await expect(async () => {
		if (price) {
			await expect(
				commerceMiniCartPage.miniCartItemListPrice(cartItem)
			).toHaveText(price, {timeout: 5000});
		}

		await expect(
			commerceThemeMiniumCatalogPage.quantitySelector(cartItem)
		).toHaveValue(String(quantity), {timeout: 5000});

		await commerceMiniCartPage.showItemOptions(cartItem);

		await expect(
			commerceMiniCartPage
				.miniCartItemBundledItem(cartItem, linkedProductName)
				.locator('.item-sku')
		).toContainText(`${linkedQuantity} × ${linkedProductName}`, {
			timeout: 5000,
		});
	}).toPass({timeout: 30000});
}

async function setUpMiniumBundledProduct(
	apiHelpers: DataApiHelpers,
	commerceAdminProductPage: CommerceAdminProductPage,
	{
		buildOptionSpecs,
		prices,
		productConfiguration = {allowBackOrder: true},
	}: {
		buildOptionSpecs: () => Promise<TProductOptionSpec[]>;
		prices?: Array<[string, number]>;
		productConfiguration?: {[key: string]: boolean | number};
	}
) {
	const {catalog, channel, site} = await miniumSetUp(apiHelpers);

	const {account, buyerUser} = await createAccountWithBuyerUser(
		apiHelpers,
		site.id
	);

	const {product, productOptions} = await createProductWithOptions(
		apiHelpers,
		commerceAdminProductPage,
		{
			catalogId: catalog.id,
			optionSpecs: await buildOptionSpecs(),
			productConfiguration,
		}
	);

	if (prices) {
		await apiHelpers.headlessCommerceAdminPricing.postBasePriceEntries(
			catalog.id,
			prices.map(([optionValueKey, price]) => ({
				price,
				skuId: findSkuByOptionValueKeys(product, [optionValueKey]).id,
			}))
		);
	}

	return {
		account,
		buyerUser,
		catalog,
		channel,
		product,
		productOptions,
		site,
	};
}

test(
	'Mini cart bundle with UOM',
	{tag: '@COMMERCE-12316'},
	async ({
		apiHelpers,
		commerceAdminProductPage,
		commerceMiniCartPage,
		globalMenuPage,
		page,
	}) => {
		const site = await apiHelpers.headlessAdminSite.postSite({
			name: getRandomString(),
		});

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

		const option1 =
			await apiHelpers.headlessCommerceAdminCatalog.postOption(
				'select',
				'color',
				'Color',
				1
			);
		const option2 =
			await apiHelpers.headlessCommerceAdminCatalog.postOption(
				'select',
				'size',
				'Size',
				2
			);

		const catalog =
			await apiHelpers.headlessCommerceAdminCatalog.postCatalog({
				name: 'Mini Cart Catalog',
			});

		const product1 =
			await apiHelpers.headlessCommerceAdminCatalog.postProduct({
				catalogId: catalog.id,
				name: {en_US: 'Product1'},
			});
		const product2 =
			await apiHelpers.headlessCommerceAdminCatalog.postProduct({
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

		await globalMenuPage.goToCommerce('Products');

		await commerceAdminProductPage.managementToolbarSearchInput.fill(
			'ProductBundle'
		);
		await commerceAdminProductPage.managementToolbarSearchInput.press(
			'Enter'
		);
		await commerceAdminProductPage
			.managementToolbarItemLink('ProductBundle')
			.click();
		await commerceAdminProductPage.generateSkus();

		await expect(
			page.getByText('Showing 1 to 5 of 5 entries.')
		).toBeVisible();

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

		await expect(async () => {
			await page.goto(
				`${liferayConfig.environment.baseUrl}/web${site.friendlyUrlPath}${layout.friendlyUrlPath}`
			);

			await expect(commerceMiniCartPage.miniCartButton).toBeVisible({
				timeout: 500,
			});
		}).toPass({timeout: 5000});

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
		await expect(
			commerceMiniCartPage.unitOfMeasureTableLabel
		).toBeVisible();
		await expect(commerceMiniCartPage.miniCartSaveButton).toBeEnabled();

		await expect(
			page.getByText('Price as Configured$ 60.00')
		).toBeVisible();

		await expect(
			page.getByRole('cell', {exact: true, name: 'Box'})
		).toBeVisible();
		await expect(
			page.getByRole('cell', {exact: true, name: 'Pallet'})
		).toBeVisible();

		await commerceMiniCartPage.selectOption('XS', 'Size');

		await expect(page.getByText('List Price$ 50.00')).toBeVisible();

		await expect(
			page.getByText('Price as Configured$ 150.00')
		).toBeVisible();

		await commerceMiniCartPage.selectOption('Black - $ 10.00', 'Color');

		await expect(page.getByText('List Price$ 40.00')).toBeVisible();

		await expect(
			page.getByText('Price as Configured$ 120.00')
		).toBeVisible();

		await expect(commerceMiniCartPage.editUnitOfMeasureLabel).toBeHidden();
		await expect(commerceMiniCartPage.unitOfMeasureTableLabel).toBeHidden();
		await expect(commerceMiniCartPage.miniCartSaveButton).toBeEnabled();

		await commerceMiniCartPage.selectOption('XL + $ 10.00', 'Size');

		await expect(page.getByText('List Price$ 50.00')).toBeVisible();

		await expect(
			page.getByText('Price as Configured$ 50.00')
		).toBeVisible();

		await expect(
			page.getByRole('cell', {exact: true, name: 'Package'})
		).toBeVisible();

		await commerceMiniCartPage.miniCartUnitOfMeasureSelector.selectOption(
			sku2SkuUnitOfMeasure.key
		);

		await expect(commerceMiniCartPage.miniCartSaveButton).toBeDisabled();

		await commerceMiniCartPage.editQuantitySelector.fill('6');

		await expect(
			page.getByText('Price as Configured$ 150.00')
		).toBeVisible();

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
	}
);

test(
	'Mini cart bundle without enough quantity',
	{tag: '@LPD-3496'},
	async ({
		apiHelpers,
		commerceAdminProductPage,
		commerceMiniCartPage,
		globalMenuPage,
		page,
	}) => {
		const site = await apiHelpers.headlessAdminSite.postSite({
			name: getRandomString(),
		});

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

		const catalog =
			await apiHelpers.headlessCommerceAdminCatalog.postCatalog();

		const product =
			await apiHelpers.headlessCommerceAdminCatalog.postProduct({
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

		await globalMenuPage.goToCommerce('Products');

		await commerceAdminProductPage.managementToolbarSearchInput.fill(
			productBundleName
		);
		await commerceAdminProductPage.managementToolbarSearchInput.press(
			'Enter'
		);

		await page
			.getByRole('link', {exact: true, name: productBundleName})
			.click();

		await commerceAdminProductPage.generateSkus();

		await expect(
			page.getByText('Showing 1 to 3 of 3 entries.')
		).toBeVisible();

		const productBundleSkus = await apiHelpers.headlessCommerceAdminCatalog
			.getProduct(productBundle.productId)
			.then((product) => {
				return product.skus;
			});

		const sku = productBundleSkus.find((sku) => sku.sku === 'WHITE');

		await expect(async () => {
			await page.goto(
				`${liferayConfig.environment.baseUrl}/web${site.friendlyUrlPath}${layout.friendlyUrlPath}`
			);

			await expect(commerceMiniCartPage.miniCartButton).toBeVisible({
				timeout: 500,
			});
		}).toPass({timeout: 5000});

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

		await expect(
			page.getByText(/Error.*quantity.*unavailable/)
		).toBeVisible();
	}
);

test(
	'Mini cart bundle quantity edit',
	{tag: '@LPD-26906'},
	async ({
		apiHelpers,
		commerceAdminProductPage,
		commerceMiniCartPage,
		globalMenuPage,
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

		const user =
			await apiHelpers.headlessAdminUser.getUserAccountByEmailAddress(
				'demo.unprivileged@liferay.com'
			);

		await apiHelpers.headlessAdminUser.assignUserToRole(
			role.externalReferenceCode,
			user.id
		);

		const site = await apiHelpers.headlessAdminSite.postSite({
			name: getRandomString(),
		});

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

		const catalog =
			await apiHelpers.headlessCommerceAdminCatalog.postCatalog();

		const product =
			await apiHelpers.headlessCommerceAdminCatalog.postProduct({
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

		await globalMenuPage.goToCommerce('Products');

		const productBundleName = productBundle.name['en_US'];

		await commerceAdminProductPage.managementToolbarSearchInput.fill(
			productBundleName
		);
		await commerceAdminProductPage.managementToolbarSearchInput.press(
			'Enter'
		);
		await commerceAdminProductPage
			.managementToolbarItemLink(productBundleName)
			.click();
		await commerceAdminProductPage.generateSkus();

		await expect(
			page.getByText('Showing 1 to 2 of 2 entries.')
		).toBeVisible();

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
	}
);

test(
	'Order items are split on the mini cart with quick add to cart when order splitting is enabled',
	{tag: '@LPD-45736'},
	async ({
		apiHelpers,
		commerceAdminChannelsPage,
		commerceMiniCartPage,
		page,
	}) => {
		const {channel, site} = await miniumSetUp(apiHelpers);

		const {account, buyerUser} = await createAccountWithBuyerUser(
			apiHelpers,
			site.id
		);

		await apiHelpers.headlessCommerceAdminAccount.postAddress(account.id, {
			phoneNumber: '12345',
			regionISOCode: 'LA',
		});

		const product =
			await apiHelpers.headlessCommerceAdminCatalog.getProducts(
				new URLSearchParams({
					filter: `name eq 'ABS Sensor'`,
				})
			);

		const productName = product.items[0].name['en_US'];

		await performUserSwitchViaApi(page, buyerUser.alternateName);

		await expect(async () => {
			await page.goto(`/web/${site.name}`);

			await expect(commerceMiniCartPage.miniCartButton).toBeVisible({
				timeout: 500,
			});
		}).toPass({timeout: 5000});

		await commerceMiniCartPage.miniCartButton.click();
		await commerceMiniCartPage.searchProductsInput.fill(productName);
		await commerceMiniCartPage.quickAddToCartSku(productName).click();
		await commerceMiniCartPage.quickAddToCartButton.click();

		await expect(commerceMiniCartPage.miniCartTotalPrice).toHaveText(
			'$ 50.00'
		);

		await commerceMiniCartPage.searchProductsInput.fill(productName);
		await commerceMiniCartPage.quickAddToCartSku(productName).click();
		await commerceMiniCartPage.quickAddToCartButton.click();

		await expect(
			page.getByText('$ 100.00', {exact: true}).first()
		).toBeVisible();
		await expect(
			commerceMiniCartPage.miniCartItem(productName)
		).toHaveCount(1);

		await performUserSwitchViaApi(page, 'test');

		await commerceAdminChannelsPage.goto();
		await (
			await commerceAdminChannelsPage.channelsTableRowLink(channel.name)
		).click();
		await commerceAdminChannelsPage
			.ordersTabToggle('Show Separate Order Items')
			.click();
		await commerceAdminChannelsPage.headerActionsSaveButton.click();

		await waitForAlert(page);

		await performUserSwitchViaApi(page, buyerUser.alternateName);

		await expect(async () => {
			await page.goto(`/web/${site.name}`);

			await expect(commerceMiniCartPage.miniCartButton).toBeVisible({
				timeout: 500,
			});
		}).toPass({timeout: 5000});

		await commerceMiniCartPage.miniCartButton.click();
		await commerceMiniCartPage.searchProductsInput.fill(productName);
		await commerceMiniCartPage.quickAddToCartSku(productName).click();
		await commerceMiniCartPage.quickAddToCartButton.click();

		await expect(
			page.getByText('$ 150.00', {exact: true}).first()
		).toBeVisible();
		await expect(
			commerceMiniCartPage.miniCartItem(productName)
		).toHaveCount(2);

		await performUserSwitchViaApi(page, 'test');

		await apiHelpers.headlessCommerceAdminOrder.deleteOrdersByAccountId(
			account.id
		);
	}
);

test(
	'As a buyer, I want the first selectable quantity of a cart item to be the minimum multiple quantity if Minimum Order Quantity is higher than Multiple Order Quantity',
	{tag: '@COMMERCE-6348'},
	async ({
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

		const multipleQuantity =
			commerceThemeMiniumCatalogPage.getMultipleQuantity(
				0,
				patchedProduct.productConfiguration.multipleOrderQuantity
			);
		const minQuantity =
			commerceThemeMiniumCatalogPage.getProductMinQuantity(
				patchedProduct.productConfiguration.minOrderQuantity,
				multipleQuantity
			);
		const maxQuantity =
			commerceThemeMiniumCatalogPage.getProductMaxQuantity(
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
	}
);

test(
	'As a buyer I can add to cart a SKU with single UOM',
	{tag: '@COMMERCE-12370'},
	async ({
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

		const multipleQuantity =
			commerceThemeMiniumCatalogPage.getMultipleQuantity(
				skuUOM2.incrementalOrderQuantity,
				product.productConfiguration.multipleOrderQuantity,
				skuUOM2.precision
			);
		const maxQuantity =
			commerceThemeMiniumCatalogPage.getProductMaxQuantity(
				product.productConfiguration.maxOrderQuantity,
				multipleQuantity,
				skuUOM2.precision
			);
		const minQuantity =
			commerceThemeMiniumCatalogPage.getProductMinQuantity(
				product.productConfiguration.minOrderQuantity,
				multipleQuantity,
				skuUOM2.precision
			);

		await performLogout(page);
		await performLogin(page, 'demo.unprivileged');

		await page.goto(`/web/${site.name}`, {waitUntil: 'networkidle'});

		await commerceThemeMiniumCatalogPage.selectSorting('Name Ascending');

		await expect(
			commerceThemeMiniumCatalogPage.productCardAddToCartButton(
				productName
			)
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

			await expect(
				page.getByText(skuUOM2.key, {exact: true})
			).toBeVisible();

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

			await expect(
				page.getByText(skuUOM2.key, {exact: true})
			).toBeVisible();

			await expect(commerceMiniCartPage.miniCartTotalPrice).toHaveText(
				'$ 250.00'
			);
		}
		finally {
			const orders =
				await apiHelpers.headlessCommerceAdminOrder.getOrdersPage();

			apiHelpers.data.push({id: orders.items[0].id, type: 'order'});
		}
	}
);

test(
	'As a buyer I can quick add to cart the purchasable SKU UOM with the highest priority',
	{tag: ['@COMMERCE-12368', '@LPD-105593']},
	async ({
		apiHelpers,
		commerceMiniCartPage,
		commerceThemeMiniumCatalogPage,
		page,
	}) => {
		const {site} = await miniumSetUp(apiHelpers);

		const {buyerUser} = await createAccountWithBuyerUser(
			apiHelpers,
			site.id
		);

		const product = (
			await apiHelpers.headlessCommerceAdminCatalog.getProducts(
				new URLSearchParams({
					filter: `name eq 'Abs Sensor'`,
					nestedFields: 'skus',
				})
			)
		).items[0];

		const productName = product.name['en_US'];
		const sku = product.skus[0];

		for (const [index, active] of [false, true, true].entries()) {
			await apiHelpers.headlessCommerceAdminCatalog.postSkuUnitOfMeasure(
				sku.id,
				{
					active,
					basePrice: 25,
					key: `uom${index + 1}`,
					name: {en_US: `UOM${index + 1}`},
					priority: index + 1,
				}
			);
		}

		await performLogout(page);
		await performLogin(page, buyerUser.alternateName);

		await page.goto(`/web/${site.name}`, {waitUntil: 'networkidle'});

		try {
			await commerceMiniCartPage.quickAddToCart(sku.sku);

			await expect(
				commerceMiniCartPage.miniCartItem(productName)
			).toBeVisible();
			await expect(
				commerceMiniCartPage.miniCartSku(sku.sku)
			).toBeVisible();
			await expect(
				commerceMiniCartPage.miniCartItemUnitOfMeasure(productName)
			).toHaveText('uom2');
			await expect(
				commerceThemeMiniumCatalogPage.quantitySelector(
					commerceMiniCartPage.miniCartItem(productName)
				)
			).toHaveValue('1');
			await expect(
				commerceMiniCartPage.miniCartItemPrice(/25\.00/, productName)
			).toBeVisible();
			await expect(
				commerceMiniCartPage.miniCartSummaryItem('Quantity')
			).toHaveText('1');
			await expect(
				commerceMiniCartPage.miniCartSummaryItem('Subtotal')
			).toHaveText('$ 25.00');
			await expect(commerceMiniCartPage.miniCartTotalPrice).toHaveText(
				'$ 25.00'
			);
		}
		finally {
			const orders =
				await apiHelpers.headlessCommerceAdminOrder.getOrdersPage();

			apiHelpers.data.push({id: orders.items[0].id, type: 'order'});
		}
	}
);

test(
	'Mini cart shows the Price on Application labels for a SKU with a UOM marked as price on application',
	{tag: ['@LPD-92604']},
	async ({
		apiHelpers,
		commerceAdminChannelsPage,
		commerceMiniCartPage,
		page,
	}) => {
		const site = await apiHelpers.headlessAdminSite.postSite({
			name: getRandomString(),
		});

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

		const channel =
			await apiHelpers.headlessCommerceAdminChannel.postChannel({
				siteGroupId: site.id,
			});

		await commerceAdminChannelsPage.changeCommerceChannelSiteType(
			channel.name,
			'B2B'
		);

		await waitForAlert(page);

		const catalog =
			await apiHelpers.headlessCommerceAdminCatalog.postCatalog({
				name: getRandomString(),
			});

		const product =
			await apiHelpers.headlessCommerceAdminCatalog.postProduct({
				catalogId: catalog.id,
				name: {en_US: getRandomString()},
			});

		const sku = product.skus[0];

		const skuUnitOfMeasure =
			await apiHelpers.headlessCommerceAdminCatalog.postSkuUnitOfMeasure(
				sku.id,
				{
					name: {en_US: 'UOM1'},
					priority: 0,
				}
			);

		const basePriceList =
			await apiHelpers.headlessCommerceAdminPricing.getBasePriceListId(
				catalog.id
			);

		await apiHelpers.headlessCommerceAdminPricing.postPriceEntry({
			price: 10,
			priceListId: basePriceList.items[0].id,
			priceOnApplication: true,
			skuId: sku.id,
			unitOfMeasureKey: skuUnitOfMeasure.key,
		});

		const {account, buyerUser} = await createAccountWithBuyerUser(
			apiHelpers,
			site.id
		);

		await apiHelpers.headlessCommerceDeliveryCart.postCart(
			{
				accountId: account.id,
				cartItems: [
					{
						options: '[]',
						quantity: 1,
						replacedSkuId: 0,
						skuId: sku.id,
						skuUnitOfMeasure: {key: skuUnitOfMeasure.key},
					},
				],
			},
			channel.id
		);

		await performLogout(page);
		await performLoginViaApi({page, screenName: buyerUser.alternateName});

		await page.goto(`/web${site.friendlyUrlPath}${layout.friendlyUrlPath}`);

		await test.step('Mini cart shows the Request a Quote button, the info message and the Price on Application label for a SKU UOM marked as price on application', async () => {
			await commerceMiniCartPage.miniCartButton.click();

			await expect(
				commerceMiniCartPage.requestAQuoteButton
			).toBeVisible();
			await expect(
				commerceMiniCartPage.miniCartPriceOnApplicationInfoMessage
			).toBeVisible();
			await expect(
				commerceMiniCartPage
					.miniCartItem(product.name.en_US)
					.getByText('Price on Application', {exact: true})
			).toBeVisible();
		});
	}
);

test(
	'Decimal unit of measure prices are converted for a bundled product in the mini cart Edit panel',
	{tag: '@COMMERCE-12628'},
	async ({
		apiHelpers,
		commerceAdminChannelsPage,
		commerceAdminProductDetailsPage,
		commerceAdminProductDetailsProductOptionsPage,
		commerceAdminProductPage,
		commerceMiniCartPage,
		page,
		productDetailsPage,
	}) => {
		const site = await apiHelpers.headlessAdminSite.postSite({
			name: getRandomString(),
		});

		await apiHelpers.headlessDelivery.createSitePage({
			pageDefinition: getPageDefinition([
				getWidgetDefinition({
					id: getRandomString(),
					widgetName:
						'com_liferay_commerce_product_content_web_internal_portlet_CPContentPortlet',
				}),
				getFragmentDefinition({
					id: getRandomString(),
					key: 'COMMERCE_CART_FRAGMENTS-mini-cart',
				}),
			]),
			siteId: site.id,
			title: getRandomString(),
		});

		const channel =
			await apiHelpers.headlessCommerceAdminChannel.postChannel({
				siteGroupId: site.id,
			});

		await commerceAdminChannelsPage.changeCommerceChannelSiteType(
			channel.name,
			'B2B'
		);

		await waitForAlert(page);

		const catalog =
			await apiHelpers.headlessCommerceAdminCatalog.postCatalog({
				name: getRandomString(),
			});

		const {account, buyerUser} = await createAccountWithBuyerUser(
			apiHelpers,
			site.id
		);

		await apiHelpers.headlessCommerceAdminAccount.postAddress(account.id, {
			phoneNumber: '12345',
			regionISOCode: 'LA',
		});

		const product =
			await apiHelpers.headlessCommerceAdminCatalog.postProduct({
				catalogId: catalog.id,
				name: {en_US: getRandomString()},
				productConfiguration: {
					minOrderQuantity: 0.1,
					multipleOrderQuantity: 0.1,
				},
			});

		const sku = product.skus[0];

		const unitOfMeasure1 =
			await apiHelpers.headlessCommerceAdminCatalog.postSkuUnitOfMeasure(
				sku.id,
				{
					basePrice: 30,
					incrementalOrderQuantity: 0.6,
					name: {en_US: 'UOM1'},
					precision: 1,
					primary: true,
					priority: 1,
					rate: 1,
				}
			);

		const unitOfMeasure2 =
			await apiHelpers.headlessCommerceAdminCatalog.postSkuUnitOfMeasure(
				sku.id,
				{
					basePrice: 50,
					incrementalOrderQuantity: 1.5,
					name: {en_US: 'UOM2'},
					precision: 1,
					priority: 2,
					promoPrice: 40,
					rate: 1,
				}
			);

		const optionKey = getRandomString();

		const option = await apiHelpers.headlessCommerceAdminCatalog.postOption(
			'select',
			optionKey,
			'Color',
			1
		);

		const bundleProduct =
			await apiHelpers.headlessCommerceAdminCatalog.postProduct({
				catalogId: catalog.id,
				name: {en_US: getRandomString()},
				productOptions: [
					{
						fieldType: 'select',
						key: optionKey,
						name: {en_US: 'Color'},
						optionId: option.id,
						priceType: 'dynamic',
						priority: 1,
						productOptionValues: [
							{
								key: 'blue',
								name: {en_US: 'Blue'},
								priority: 1,
							},
							{
								key: 'white',
								name: {en_US: 'White'},
								priority: 2,
							},
						],
						required: true,
						skuContributor: true,
					},
				],
			});

		await test.step('Link each option value to the same SKU at a different unit of measure', async () => {
			await commerceAdminProductPage.gotoProduct(
				bundleProduct.name['en_US']
			);

			await commerceAdminProductDetailsPage.goToProductOptions();

			await commerceAdminProductDetailsProductOptionsPage.openOption(
				'Color'
			);
			await commerceAdminProductDetailsProductOptionsPage.editOptionValue(
				'Blue',
				{
					quantity: '0.6',
					sku: sku.sku,
					unitOfMeasureKey: unitOfMeasure1.key,
				}
			);
			await commerceAdminProductDetailsProductOptionsPage.editOptionValue(
				'White',
				{
					quantity: '1.5',
					sku: sku.sku,
					unitOfMeasureKey: unitOfMeasure2.key,
				}
			);
			await commerceAdminProductDetailsProductOptionsPage.closeOption();

			await commerceAdminProductPage.generateSkus();

			await expect(
				page.getByText('Showing 1 to 3 of 3 entries.')
			).toBeVisible();
		});

		await performLogout(page);
		await performLoginViaApi({page, screenName: buyerUser.alternateName});

		try {
			await test.step('The product details page converts each unit of measure price to a single bundle quantity', async () => {
				await page.goto(
					`/web/${site.name}/p/${bundleProduct.name['en_US']}`,
					{
						waitUntil: 'networkidle',
					}
				);

				await productDetailsPage.selectOption('Blue', 'Color');

				await expect(
					await productDetailsPage.skuField('BLUE')
				).toBeVisible();
				await expect(
					await productDetailsPage.priceField(
						'$ 30.00',
						productDetailsPage.priceContainer
					)
				).toBeVisible();

				await productDetailsPage.addToCartButton.click();

				await page.waitForLoadState('networkidle');

				await commerceMiniCartPage.miniCartButton.click();

				await expect(
					commerceMiniCartPage.miniCartSku('BLUE')
				).toBeVisible();

				await clickAndExpectToBeHidden({
					target: commerceMiniCartPage.miniCartItemsContainer,
					trigger: commerceMiniCartPage.miniCartButtonClose,
				});

				await productDetailsPage.selectOption(
					'White + $ 10.00',
					'Color'
				);

				await expect(
					await productDetailsPage.skuField('WHITE')
				).toBeVisible();
				await expect(
					await productDetailsPage.priceField(
						'$ 50.00',
						productDetailsPage.priceContainer
					)
				).toBeVisible();
				await expect(
					await productDetailsPage.promoPriceField(
						'$ 40.00',
						productDetailsPage.priceContainer
					)
				).toBeVisible();

				await productDetailsPage.addToCartButton.click();

				await page.waitForLoadState('networkidle');
			});

			await test.step('The mini cart shows the converted price for both bundled order items', async () => {
				await commerceMiniCartPage.miniCartButton.click();

				for (const [skuName, bundledItem, price] of [
					[
						'BLUE',
						`Blue(0.6 × ${product.name['en_US']} ${unitOfMeasure1.key})`,
						'$ 30.00',
					],
					[
						'WHITE',
						`White(1.5 × ${product.name['en_US']} ${unitOfMeasure2.key})`,
						'$ 40.00',
					],
				]) {
					await clickAndExpectToBeVisible({
						target: commerceMiniCartPage
							.miniCartItem(skuName)
							.getByText(bundledItem),
						trigger:
							commerceMiniCartPage.miniCartItemShowOptionsButton(
								skuName
							),
					});

					await expect(
						commerceMiniCartPage
							.miniCartItem(skuName)
							.getByText(price)
							.first()
					).toBeVisible();
				}
			});

			await test.step('Switching to the promotionally priced unit of measure converts both prices in the Edit panel', async () => {
				await commerceMiniCartPage
					.miniCartItem('BLUE')
					.getByTestId('cartItemActions')
					.click();
				await commerceMiniCartPage.editMenuItem.click();

				await expect(
					commerceMiniCartPage.editOptionsLabel
				).toBeVisible();

				await commerceMiniCartPage.selectEditItemOption(
					'White',
					'Color'
				);

				await expect(
					commerceMiniCartPage.miniCartEditItemPrice('List Price')
				).toHaveText('$ 50.00');
				await expect(
					commerceMiniCartPage.miniCartEditItemPrice(
						'Price as Configured'
					)
				).toHaveText('$ 40.00');
			});

			await test.step('Editing the White order item back to Blue merges it into the Blue order item', async () => {
				await page.goto(
					`/web/${site.name}/p/${bundleProduct.name['en_US']}`,
					{
						waitUntil: 'networkidle',
					}
				);

				await commerceMiniCartPage.miniCartButton.click();

				await expect(
					commerceMiniCartPage.miniCartSku('WHITE')
				).toBeVisible();

				await commerceMiniCartPage
					.miniCartItem('WHITE')
					.getByTestId('cartItemActions')
					.click();
				await commerceMiniCartPage.editMenuItem.click();

				await expect(
					commerceMiniCartPage.editOptionsLabel
				).toBeVisible();

				await commerceMiniCartPage.selectEditItemOption(
					'Blue',
					'Color'
				);

				await expect(
					commerceMiniCartPage.miniCartEditItemPrice('List Price')
				).toHaveText('$ 30.00');
				await expect(
					commerceMiniCartPage.miniCartEditItemPrice(
						'Price as Configured'
					)
				).toHaveText('$ 30.00');

				await commerceMiniCartPage.miniCartSaveButton.click();

				await expect(
					commerceMiniCartPage.miniCartSku('WHITE')
				).toBeHidden();
				await expect(
					commerceMiniCartPage.miniCartSku('BLUE')
				).toBeVisible();
			});
		}
		finally {
			const orders =
				await apiHelpers.headlessCommerceAdminOrder.getOrdersPage();

			if (orders.items[0]) {
				apiHelpers.data.push({id: orders.items[0].id, type: 'order'});
			}
		}
	}
);

test(
	'A multi SKU product carries every unit of measure of every SKU into the cart from the product details page',
	{tag: ['@COMMERCE-12396', '@LPD-106905']},
	async ({
		apiHelpers,
		commerceMiniCartPage,
		commerceThemeMiniumCatalogPage,
		page,
		productDetailsPage,
	}) => {
		test.setTimeout(300000);

		const optionName = 'Package Quantity';
		const productName = 'Brake Fluid';

		const {catalog, site} = await miniumSetUp(apiHelpers);

		const {buyerUser} = await createAccountWithBuyerUser(
			apiHelpers,
			site.id
		);

		const {
			brakeFluid,
			firstUnitOfMeasure,
			secondUnitOfMeasure,
			thirdUnitOfMeasure,
		} = await setUpBrakeFluidUnitsOfMeasure(apiHelpers, catalog.id);

		await performLogout(page);
		await performLoginViaApi({page, screenName: buyerUser.alternateName});

		await page.goto(
			`/web${site.friendlyUrlPath}/p/${brakeFluid.urls['en_US']}`,
			{waitUntil: 'networkidle'}
		);

		await test.step('The preselected SKU offers its only unit of measure and cannot switch away from it', async () => {
			await expect(productDetailsPage.unitOfMeasureSelect).toHaveValue(
				firstUnitOfMeasure.key
			);
			await expect(productDetailsPage.unitOfMeasureSelect).toBeDisabled();
			await expect(
				productDetailsPage.unitOfMeasureSelect.locator('option')
			).toHaveText([firstUnitOfMeasure.name['en_US']]);
			await expect(
				await productDetailsPage.nameField(productName)
			).toBeVisible();
			await expect(
				await productDetailsPage.skuField('MIN93016A')
			).toBeVisible();
			await expect(productDetailsPage.inStockQuantity).toHaveText(
				'240 in Stock'
			);
			await expect(
				productDetailsPage.productDetailAvailabilityLabel
			).toHaveText('Available');
			await expect(
				await productDetailsPage.priceField(
					unitOfMeasurePriceLabel(
						firstUnitOfMeasure,
						firstUnitOfMeasure.basePrice
					),
					productDetailsPage.priceContainer
				)
			).toBeVisible();

			await productDetailsPage.productDetailQuantitySelector.fill('1.2');
			await productDetailsPage.productDetailAddToCartButton.click();

			await expect(commerceMiniCartPage.miniCartButton).toHaveAttribute(
				'data-badge-count',
				'1'
			);
		});

		await test.step('A SKU without a unit of measure hides the selector', async () => {
			await productDetailsPage.selectOption('48', optionName);

			await expect(productDetailsPage.unitOfMeasureSelect).toHaveCount(0);

			await productDetailsPage.productDetailQuantitySelector.fill('1');
			await productDetailsPage.productDetailAddToCartButton.click();

			await expect(commerceMiniCartPage.miniCartButton).toHaveAttribute(
				'data-badge-count',
				'2'
			);
		});

		await test.step('A SKU with several units of measure lists only the active ones and preselects the first', async () => {
			await productDetailsPage.selectOption('112', optionName);

			await expect(
				productDetailsPage.unitOfMeasureSelect.locator('option')
			).toHaveText([
				secondUnitOfMeasure.name['en_US'],
				thirdUnitOfMeasure.name['en_US'],
			]);
			await expect(productDetailsPage.unitOfMeasureSelect).toHaveValue(
				secondUnitOfMeasure.key
			);
			await expect(
				await productDetailsPage.skuField('MIN93016C')
			).toBeVisible();
			await expect(productDetailsPage.inStockQuantity).toHaveText(
				'240 in Stock'
			);
			await expect(
				productDetailsPage.productDetailAvailabilityLabel
			).toHaveText('Available');
			await expect(
				await productDetailsPage.priceField(
					unitOfMeasurePriceLabel(
						secondUnitOfMeasure,
						secondUnitOfMeasure.basePrice
					),
					productDetailsPage.priceContainer
				)
			).toBeVisible();

			await productDetailsPage.productDetailQuantitySelector.fill('1');
			await productDetailsPage.productDetailAddToCartButton.click();

			await expect(commerceMiniCartPage.miniCartButton).toHaveAttribute(
				'data-badge-count',
				'3'
			);
		});

		await test.step('Switching the unit of measure switches the price, the availability and the stock quantity', async () => {
			await productDetailsPage.unitOfMeasureSelect.selectOption(
				thirdUnitOfMeasure.key
			);

			await expect(productDetailsPage.inStockQuantity).toHaveText(
				'0 in Stock'
			);
			await expect(
				productDetailsPage.productDetailAvailabilityLabel
			).toHaveText('Unavailable');
			await expect(
				await productDetailsPage.promoPriceField(
					`$ ${thirdUnitOfMeasure.promoPrice.toFixed(2)}`,
					productDetailsPage.priceContainer
				)
			).toBeVisible();

			await productDetailsPage.productDetailQuantitySelector.fill('1');
			await productDetailsPage.productDetailAddToCartButton.click();

			await expect(commerceMiniCartPage.miniCartButton).toHaveAttribute(
				'data-badge-count',
				'4'
			);
		});

		await test.step('Every SKU and unit of measure combination reaches the mini cart with its own quantity and price', async () => {
			await commerceMiniCartPage.miniCartButton.click();

			await expectBrakeFluidCartItems(
				commerceMiniCartPage,
				commerceThemeMiniumCatalogPage,
				{firstUnitOfMeasure, secondUnitOfMeasure, thirdUnitOfMeasure}
			);
		});
	}
);

test(
	'The mini cart lists the linked SKUs of a bundled order item and drops its edit panel when the cart is closed',
	{tag: ['@COMMERCE-12605', '@LPD-106905']},
	async ({
		apiHelpers,
		commerceAdminProductPage,
		commerceMiniCartPage,
		commerceThemeMiniumCatalogPage,
		page,
		productDetailsPage,
	}) => {
		test.setTimeout(300000);

		const {buyerUser, product, site} = await setUpMiniumBundledProduct(
			apiHelpers,
			commerceAdminProductPage,
			{
				buildOptionSpecs: () => buildColorOptionSpecs(apiHelpers),
				prices: [
					['blue', 10],
					['white', 20],
				],
			}
		);

		const blueSku = findSkuByOptionValueKeys(product, ['blue']);
		const whiteSku = findSkuByOptionValueKeys(product, ['white']);

		await performLogout(page);
		await performLoginViaApi({page, screenName: buyerUser.alternateName});

		await test.step('The buyer adds both bundle variants to the cart', async () => {
			for (const [index, optionLabel] of ['Blue', 'White'].entries()) {
				await page.goto(
					`/web${site.friendlyUrlPath}/p/${product.urls['en_US']}`,
					{waitUntil: 'networkidle'}
				);

				await productDetailsPage.selectOptionContaining(
					optionLabel,
					'Color'
				);

				await productDetailsPage.productDetailAddToCartButton.click();

				await expect(
					commerceMiniCartPage.miniCartButton
				).toHaveAttribute('data-badge-count', String(index + 1));
			}

			await commerceMiniCartPage.open();
		});

		await test.step('Each bundled order item lists its linked SKU and offers the edit action', async () => {
			for (const [sku, price, optionValueName, linkedProductName] of [
				[blueSku, '$ 30.00', 'Blue', 'ABS Sensor'],
				[whiteSku, '$ 50.00', 'White', 'Brake Rotors'],
			] as Array<[{sku: string}, string, string, string]>) {
				const cartItem = commerceMiniCartPage.miniCartItemForSku(
					sku.sku
				);

				await expect(
					commerceMiniCartPage.miniCartItemListPrice(cartItem)
				).toHaveText(price);
				await expect(
					commerceThemeMiniumCatalogPage.quantitySelector(cartItem)
				).toHaveValue('1');
				await expect(
					commerceMiniCartPage.miniCartItemActionsButton(cartItem)
				).toBeVisible();
				await commerceMiniCartPage.showItemOptions(cartItem);

				const bundledItem =
					commerceMiniCartPage.miniCartItemBundledItem(
						cartItem,
						linkedProductName
					);

				await expect(bundledItem).toBeVisible();
				await expect(bundledItem.locator('.item-name')).toHaveText(
					'Color'
				);
				await expect(bundledItem.locator('.item-sku')).toContainText(
					optionValueName
				);
				await expect(bundledItem.locator('.item-sku')).toContainText(
					`1 \u00D7 ${linkedProductName}`
				);
			}
		});

		await test.step('A non bundled order item keeps its options collapsed and lists no linked SKU', async () => {
			await commerceMiniCartPage.close();

			await page.goto(`/web${site.friendlyUrlPath}/p/brake-fluid`, {
				waitUntil: 'networkidle',
			});

			await productDetailsPage.selectOption('12', 'Package Quantity');

			await productDetailsPage.productDetailAddToCartButton.click();

			await page.waitForLoadState('networkidle');

			await commerceMiniCartPage.open();

			const cartItem =
				commerceMiniCartPage.miniCartItemForSku('MIN93016A');

			await expect(
				commerceMiniCartPage.miniCartItemShowOptionsButton(cartItem)
			).toBeVisible();
			await expect(
				commerceMiniCartPage.miniCartItemActionsButton(cartItem)
			).toBeVisible();

			await commerceMiniCartPage.showItemOptions(cartItem);

			const optionInfo = commerceMiniCartPage.miniCartItemBundledItem(
				cartItem,
				'Package Quantity'
			);

			await expect(optionInfo.locator('.item-name')).toHaveText(
				'Package Quantity'
			);
			await expect(optionInfo.locator('.item-sku')).toHaveText('12');
		});

		await test.step('Closing the mini cart drops the edit panel, whether by the overlay or by the close button', async () => {
			const cartItem = commerceMiniCartPage.miniCartItemForSku(
				blueSku.sku
			);

			for (const dismiss of [
				commerceMiniCartPage.miniCartOverlay,
				commerceMiniCartPage.miniCartButtonClose,
			]) {
				await commerceMiniCartPage.open();

				await commerceMiniCartPage
					.miniCartItemActionsButton(cartItem)
					.click();

				await commerceMiniCartPage.editMenuItem.click();

				await expect(
					commerceMiniCartPage.miniCartEditItemPanel
				).toBeVisible();

				await clickAndExpectToBeHidden({
					target: commerceMiniCartPage.miniCartOpenDrawer,
					trigger: dismiss,
				});

				await commerceMiniCartPage.open();

				await expect(
					commerceMiniCartPage.miniCartEditItemPanel
				).toHaveCount(0);
				await expect(commerceMiniCartPage.miniCartResume).toBeVisible();
			}
		});
	}
);

test(
	'A bundled option value whose linked SKU is out of stock cannot be added to the cart',
	{tag: ['@COMMERCE-12608', '@LPD-106905']},
	async ({
		apiHelpers,
		commerceAdminProductPage,
		commerceMiniCartPage,
		page,
		productDetailsPage,
	}) => {
		test.setTimeout(300000);

		const {buyerUser, product, site} = await setUpMiniumBundledProduct(
			apiHelpers,
			commerceAdminProductPage,
			{
				buildOptionSpecs: () => buildColorOptionSpecs(apiHelpers),
				prices: [
					['blue', 10],
					['white', 20],
				],
			}
		);

		const blueSku = findSkuByOptionValueKeys(product, ['blue']);
		const whiteSku = findSkuByOptionValueKeys(product, ['white']);

		await test.step('The linked SKU of the Blue option value runs out of stock and refuses back orders', async () => {
			const absSensor =
				await apiHelpers.headlessCommerceAdminCatalog.getProductByName(
					'ABS Sensor'
				);

			await apiHelpers.headlessCommerceAdminCatalog.patchProduct(
				String(absSensor.productId),
				{
					name: absSensor.name,
					productConfiguration: {allowBackOrder: false},
				}
			);

			await zeroWarehouseStock(apiHelpers, ['MIN93015']);
		});

		await performLogout(page);
		await performLoginViaApi({page, screenName: buyerUser.alternateName});

		await page.goto(
			`/web${site.friendlyUrlPath}/p/${product.urls['en_US']}`,
			{waitUntil: 'networkidle'}
		);

		await test.step('Adding the unavailable option value is refused', async () => {
			await productDetailsPage.selectOptionContaining('Blue', 'Color');

			await productDetailsPage.productDetailAddToCartButton.click();

			await waitForAlert(page, 'The specified quantity is unavailable.', {
				type: 'danger',
			});

			await expect(commerceMiniCartPage.miniCartButton).toHaveAttribute(
				'data-badge-count',
				'0'
			);
		});

		await test.step('The available option value is ordered instead', async () => {
			await productDetailsPage.selectOptionContaining('White', 'Color');

			await productDetailsPage.productDetailAddToCartButton.click();

			await expect(commerceMiniCartPage.miniCartButton).toHaveAttribute(
				'data-badge-count',
				'1'
			);
		});

		await test.step('The edit panel still offers the unavailable option value', async () => {
			await commerceMiniCartPage.open();

			await expect(
				commerceMiniCartPage.miniCartSku(whiteSku.sku)
			).toBeVisible();
			await expect(
				commerceMiniCartPage.miniCartSku(blueSku.sku)
			).toHaveCount(0);

			await commerceMiniCartPage
				.miniCartItemActionsButton(
					commerceMiniCartPage.miniCartItemForSku(whiteSku.sku)
				)
				.click();

			await commerceMiniCartPage.editMenuItem.click();

			await expect(
				commerceMiniCartPage.miniCartEditItemPanel
			).toBeVisible();
			await expect(
				commerceMiniCartPage
					.miniCartEditItemOptionValues('Color')
					.filter({hasText: 'Blue'})
			).toHaveCount(1);
		});
	}
);

test(
	'A bundled product with a decimal unit of measure quantity keeps its linked SKU quantity in step',
	{tag: ['@COMMERCE-12611', '@LPD-106905']},
	async ({
		apiHelpers,
		commerceAdminProductPage,
		commerceMiniCartPage,
		commerceThemeMiniumCatalogPage,
		page,
		productDetailsPage,
	}) => {
		test.setTimeout(300000);

		const {buyerUser, product, site} = await setUpMiniumBundledProduct(
			apiHelpers,
			commerceAdminProductPage,
			{
				buildOptionSpecs: () =>
					buildColorOptionSpecs(apiHelpers, {
						deltaPrices: false,
						priceType: 'dynamic',
					}),
				productConfiguration: {
					allowBackOrder: true,
					minOrderQuantity: 0.1,
					multipleOrderQuantity: 0.5,
				},
			}
		);

		const blueSku = findSkuByOptionValueKeys(product, ['blue']);

		const unitOfMeasure =
			await apiHelpers.headlessCommerceAdminCatalog.postSkuUnitOfMeasure(
				blueSku.id,
				{
					active: true,
					basePrice: 50,
					incrementalOrderQuantity: 0.5,
					key: 'UOM1KEY',
					name: {en_US: 'UOM1'},
					precision: 1,
					priority: 1,
				}
			);

		await performLogout(page);
		await performLoginViaApi({page, screenName: buyerUser.alternateName});

		await page.goto(
			`/web${site.friendlyUrlPath}/p/${product.urls['en_US']}`,
			{waitUntil: 'networkidle'}
		);

		await test.step('The product details page preselects the incremental order quantity', async () => {
			await expect(
				productDetailsPage.productDetailQuantitySelector
			).toHaveValue(String(unitOfMeasure.incrementalOrderQuantity));

			await productDetailsPage.productDetailAddToCartButton.click();

			await expect(commerceMiniCartPage.miniCartButton).toHaveAttribute(
				'data-badge-count',
				'1'
			);
		});

		await commerceMiniCartPage.open();

		const cartItem = commerceMiniCartPage.miniCartItemForSku(blueSku.sku);

		const expectQuantities = (quantity: number, linkedQuantity: number) =>
			expectBundledCartItem(
				commerceMiniCartPage,
				commerceThemeMiniumCatalogPage,
				cartItem,
				{linkedProductName: 'ABS Sensor', linkedQuantity, quantity}
			);

		await test.step('The order item carries the decimal quantity and its linked SKU quantity', async () => {
			await expect(
				commerceMiniCartPage.miniCartItemListPrice(cartItem)
			).toHaveText(
				`$ ${(
					unitOfMeasure.basePrice /
					unitOfMeasure.incrementalOrderQuantity
				).toFixed(2)}`
			);

			await expectQuantities(unitOfMeasure.incrementalOrderQuantity, 1);
		});

		await test.step('Stepping the quantity up and down keeps the linked SKU quantity in step', async () => {
			for (const [key, quantity, linkedQuantity] of [
				['ArrowUp', 1, 2],
				['ArrowDown', 0.5, 1],
			] as Array<[string, number, number]>) {
				await commerceThemeMiniumCatalogPage
					.quantitySelector(cartItem)
					.press(key);

				await expectQuantities(quantity, linkedQuantity);
			}
		});
	}
);

test(
	'Editing a bundled order item to another option value repositions it against the basic price',
	{tag: ['@COMMERCE-12607', '@LPD-106905']},
	async ({
		apiHelpers,
		commerceAdminProductPage,
		commerceMiniCartPage,
		commerceThemeMiniumCatalogPage,
		page,
		productDetailsPage,
	}) => {
		test.setTimeout(300000);

		const {buyerUser, product, site} = await setUpMiniumBundledProduct(
			apiHelpers,
			commerceAdminProductPage,
			{
				buildOptionSpecs: () => buildColorOptionSpecs(apiHelpers),
				prices: [
					['blue', 10],
					['white', 20],
				],
			}
		);

		const blueSku = findSkuByOptionValueKeys(product, ['blue']);
		const whiteSku = findSkuByOptionValueKeys(product, ['white']);

		const productURL = `/web${site.friendlyUrlPath}/p/${product.urls['en_US']}`;

		const expectCartItem = (
			sku: {sku: string},
			expected: {
				linkedProductName: string;
				linkedQuantity: number;
				price: string;
				quantity: number;
			}
		) =>
			expectBundledCartItem(
				commerceMiniCartPage,
				commerceThemeMiniumCatalogPage,
				commerceMiniCartPage.miniCartItemForSku(sku.sku),
				expected
			);

		const expectCartSummary = async (quantity: number, total: string) => {
			await expect(async () => {
				await expect(
					commerceMiniCartPage.miniCartSummaryItem('Quantity')
				).toHaveText(String(quantity), {timeout: 5000});
				await expect(
					commerceMiniCartPage.miniCartSummaryItem('Subtotal')
				).toHaveText(total, {timeout: 5000});
				await expect(
					commerceMiniCartPage.miniCartTotalPrice
				).toHaveText(total, {timeout: 5000});
			}).toPass({timeout: 30000});
		};

		const editOptionValue = async (
			sku: {sku: string},
			optionLabel: string
		) => {
			await commerceMiniCartPage.open();

			await commerceMiniCartPage
				.miniCartItemActionsButton(
					commerceMiniCartPage.miniCartItemForSku(sku.sku)
				)
				.click();

			await commerceMiniCartPage.editMenuItem.click();

			await expect(
				commerceMiniCartPage.miniCartEditItemPanel
			).toBeVisible();

			await commerceMiniCartPage.selectEditItemOption(
				optionLabel,
				'Color'
			);

			await commerceMiniCartPage.miniCartSaveButton.click();

			await expect(
				commerceMiniCartPage.miniCartEditItemPanel
			).toHaveCount(0);
		};

		await performLogout(page);
		await performLoginViaApi({page, screenName: buyerUser.alternateName});

		await test.step('Both bundle variants reach the cart with their own price', async () => {
			for (const [index, optionLabel] of ['Blue', 'White'].entries()) {
				await page.goto(productURL, {waitUntil: 'networkidle'});

				await productDetailsPage.selectOptionContaining(
					optionLabel,
					'Color'
				);

				await productDetailsPage.productDetailAddToCartButton.click();

				await expect(
					commerceMiniCartPage.miniCartButton
				).toHaveAttribute('data-badge-count', String(index + 1));
			}

			await commerceMiniCartPage.open();

			await expectCartItem(blueSku, {
				linkedProductName: 'ABS Sensor',
				linkedQuantity: 1,
				price: '$ 30.00',
				quantity: 1,
			});
			await expectCartItem(whiteSku, {
				linkedProductName: 'Brake Rotors',
				linkedQuantity: 1,
				price: '$ 50.00',
				quantity: 1,
			});
			await expectCartSummary(2, '$ 80.00');
		});

		await test.step('Saving the option value it already carries leaves the order item untouched', async () => {
			await editOptionValue(blueSku, 'Blue');

			await commerceMiniCartPage.open();

			await expect(commerceMiniCartPage.miniCartResume).toBeVisible();

			await expectCartItem(blueSku, {
				linkedProductName: 'ABS Sensor',
				linkedQuantity: 1,
				price: '$ 30.00',
				quantity: 1,
			});
			await expectCartItem(whiteSku, {
				linkedProductName: 'Brake Rotors',
				linkedQuantity: 1,
				price: '$ 50.00',
				quantity: 1,
			});
			await expectCartSummary(2, '$ 80.00');
		});

		await test.step('Adding the same variant again raises its quantity', async () => {
			await page.goto(productURL, {waitUntil: 'networkidle'});

			await productDetailsPage.selectOptionContaining('Blue', 'Color');

			await productDetailsPage.productDetailAddToCartButton.click();

			await commerceMiniCartPage.open();

			await expectCartItem(blueSku, {
				linkedProductName: 'ABS Sensor',
				linkedQuantity: 2,
				price: '$ 30.00',
				quantity: 2,
			});
			await expectCartSummary(3, '$ 110.00');
		});

		await test.step('Editing the option value merges the order item into the one that already carries it', async () => {
			await editOptionValue(blueSku, 'White');

			await commerceMiniCartPage.open();

			await expectCartItem(whiteSku, {
				linkedProductName: 'Brake Rotors',
				linkedQuantity: 3,
				price: '$ 50.00',
				quantity: 3,
			});
			await expect(
				commerceMiniCartPage.miniCartSku(blueSku.sku)
			).toHaveCount(0);
			await expectCartSummary(3, '$ 150.00');
		});

		await test.step('Quick add to cart keeps the merged order items in step', async () => {
			for (const sku of [blueSku, whiteSku]) {
				await commerceMiniCartPage.quickAddToCart(sku.sku);
			}

			await expectCartItem(whiteSku, {
				linkedProductName: 'Brake Rotors',
				linkedQuantity: 4,
				price: '$ 50.00',
				quantity: 4,
			});
			await expectCartItem(blueSku, {
				linkedProductName: 'ABS Sensor',
				linkedQuantity: 1,
				price: '$ 30.00',
				quantity: 1,
			});
			await expectCartSummary(5, '$ 230.00');
		});

		await test.step('The edit panel prices the single unit and the configured quantity apart', async () => {
			await commerceMiniCartPage
				.miniCartItemActionsButton(
					commerceMiniCartPage.miniCartItemForSku(whiteSku.sku)
				)
				.click();

			await commerceMiniCartPage.editMenuItem.click();

			await expect(
				commerceMiniCartPage.miniCartEditItemPrice('List Price')
			).toHaveText('$ 50.00');
			await expect(
				commerceMiniCartPage.miniCartEditItemPrice(
					'Price as Configured'
				)
			).toHaveText('$ 200.00');
		});
	}
);

test(
	'Every type of product option of a bundled product survives the mini cart edit panel',
	{tag: ['@COMMERCE-12610', '@LPD-106905']},
	async ({
		apiHelpers,
		commerceAdminProductPage,
		commerceMiniCartPage,
		page,
		pendingOrdersPage,
		productDetailsPage,
	}) => {
		test.setTimeout(300000);

		const {buyerUser, product, productOptions, site} =
			await setUpMiniumBundledProduct(
				apiHelpers,
				commerceAdminProductPage,
				{
					buildOptionSpecs: async () => [
						...(await buildColorOptionSpecs(apiHelpers)),
						{fieldType: 'checkbox', name: 'Gift Wrap'},
						{fieldType: 'date', name: 'Delivery Date'},
						{fieldType: 'numeric', name: 'Floors'},
						{fieldType: 'text', name: 'Engraving'},
						{
							fieldType: 'checkbox_multiple',
							name: 'Extras',
							values: [
								{key: 'value1', name: 'Value1'},
								{key: 'value2', name: 'Value2'},
							],
						},
						{
							fieldType: 'radio',
							name: 'Material',
							values: [
								{key: 'value3', name: 'Value3'},
								{key: 'value4', name: 'Value4'},
							],
						},
					],
				}
			);

		const blueSku = findSkuByOptionValueKeys(product, ['blue']);

		const firstValues: TBundleOptionValues = {
			deliveryDate: '2023-10-23',
			engraving: 'First engraving',
			extras: ['Value1', 'Value2'],
			floors: '10',
			giftWrap: true,
			material: 'Value4',
		};
		const secondValues: TBundleOptionValues = {
			deliveryDate: '2023-10-20',
			engraving: 'Second engraving',
			extras: ['Value1'],
			floors: '20',
			giftWrap: false,
			material: 'Value3',
		};

		const fillOptions = async (
			container: Locator,
			values: TBundleOptionValues
		) => {
			await productDetailsPage
				.optionField('Gift Wrap', container)
				.setChecked(values.giftWrap);

			for (const optionValueName of ['Value1', 'Value2']) {
				await productDetailsPage
					.optionValueCheckbox(optionValueName, container)
					.setChecked(values.extras.includes(optionValueName));
			}

			await productDetailsPage
				.optionRadio(values.material, container)
				.check();
			await productDetailsPage
				.optionField('Delivery Date', container)
				.fill(values.deliveryDate);
			await productDetailsPage
				.optionField('Floors', container)
				.fill(values.floors);
			await productDetailsPage
				.optionField('Engraving', container)
				.fill(values.engraving);
		};

		const expectOptionInputs = async (
			container: Locator,
			values: TBundleOptionValues
		) => {
			await expect(
				productDetailsPage.optionField('Gift Wrap', container)
			).toBeChecked({checked: values.giftWrap});

			for (const optionValueName of ['Value1', 'Value2']) {
				await expect(
					productDetailsPage.optionValueCheckbox(
						optionValueName,
						container
					)
				).toBeChecked({
					checked: values.extras.includes(optionValueName),
				});
			}

			await expect(
				productDetailsPage.optionRadio(values.material, container)
			).toBeChecked();

			await expect(
				productDetailsPage.optionField('Delivery Date', container)
			).toHaveValue(values.deliveryDate);
			await expect(
				productDetailsPage.optionField('Floors', container)
			).toHaveValue(values.floors);
			await expect(
				productDetailsPage.optionField('Engraving', container)
			).toHaveValue(values.engraving);
		};

		const expectCartItemOptions = async (
			cartItem: Locator,
			values: TBundleOptionValues
		) => {
			await expect(async () => {
				for (const [optionName, optionValue] of [
					['Color', 'Blue'],
					['Delivery Date', values.deliveryDate],
					['Engraving', values.engraving],
					['Extras', values.extras.join(', ')],
					['Floors', values.floors],
					['Material', values.material],
				]) {
					await expect(
						commerceMiniCartPage.miniCartItemOption(
							cartItem,
							optionName
						)
					).toContainText(optionValue, {timeout: 5000});
				}
			}).toPass({timeout: 30000});
		};

		const productURL = `/web${site.friendlyUrlPath}/p/${product.urls['en_US']}`;

		await performLogout(page);
		await performLoginViaApi({page, screenName: buyerUser.alternateName});

		await page.goto(productURL, {waitUntil: 'networkidle'});

		await test.step('The product details page renders every option type', async () => {
			for (const {name} of productOptions) {
				await expect(
					productDetailsPage.productDetail.getByText(name['en_US'], {
						exact: true,
					})
				).toBeVisible();
			}

			for (const optionValueName of ['Value1', 'Value2']) {
				await expect(
					productDetailsPage.productDetail.getByRole('checkbox', {
						name: optionValueName,
					})
				).not.toBeChecked();
			}

			for (const optionValueName of ['Value3', 'Value4']) {
				await expect(
					productDetailsPage.productDetail.getByRole('radio', {
						name: optionValueName,
					})
				).toBeVisible();
			}
		});

		await test.step('The filled option values reach the mini cart', async () => {
			await productDetailsPage.selectOptionContaining('Blue', 'Color');

			await fillOptions(productDetailsPage.productDetail, firstValues);

			await productDetailsPage.productDetailAddToCartButton.click();

			await expect(commerceMiniCartPage.miniCartButton).toHaveAttribute(
				'data-badge-count',
				'1'
			);

			await commerceMiniCartPage.open();

			const cartItem = commerceMiniCartPage.miniCartItemForSku(
				blueSku.sku
			);

			await expect(
				commerceMiniCartPage.miniCartItemListPrice(cartItem)
			).toHaveText('$ 20.00');

			await expectCartItemOptions(cartItem, firstValues);
		});

		await test.step('The edit panel shows every option value and takes a new one for each', async () => {
			await commerceMiniCartPage
				.miniCartItemActionsButton(
					commerceMiniCartPage.miniCartItemForSku(blueSku.sku)
				)
				.click();

			await commerceMiniCartPage.editMenuItem.click();

			await expect(
				commerceMiniCartPage.miniCartEditItemPanel
			).toBeVisible();

			await expectOptionInputs(
				commerceMiniCartPage.miniCartEditItemPanel,
				firstValues
			);

			await expect(
				commerceMiniCartPage.miniCartEditItemPrice('List Price')
			).toHaveText('$ 20.00');
			await expect(
				commerceMiniCartPage.miniCartEditItemPrice(
					'Price as Configured'
				)
			).toHaveText('$ 20.00');

			await fillOptions(
				commerceMiniCartPage.miniCartEditItemPanel,
				secondValues
			);

			await commerceMiniCartPage.miniCartSaveButton.click();

			await expect(
				commerceMiniCartPage.miniCartEditItemPanel
			).toHaveCount(0);

			await expectCartItemOptions(
				commerceMiniCartPage.miniCartItemForSku(blueSku.sku),
				secondValues
			);
		});

		await test.step('The same SKU with different option values becomes a separate order item', async () => {
			await page.goto(productURL, {waitUntil: 'networkidle'});

			await productDetailsPage.selectOptionContaining('Blue', 'Color');

			await fillOptions(productDetailsPage.productDetail, firstValues);

			await productDetailsPage.productDetailAddToCartButton.click();

			await expect(commerceMiniCartPage.miniCartButton).toHaveAttribute(
				'data-badge-count',
				'2'
			);

			await commerceMiniCartPage.open();

			await expect(
				commerceMiniCartPage.miniCartItemForSku(blueSku.sku)
			).toHaveCount(2);

			for (const values of [firstValues, secondValues]) {
				await expectCartItemOptions(
					commerceMiniCartPage
						.miniCartItemForSku(blueSku.sku)
						.filter({hasText: values.engraving}),
					values
				);
			}
		});

		await test.step('Both order items are listed on the pending order with their own option values', async () => {
			await commerceMiniCartPage.viewDetailsButton.click();

			await expect(pendingOrdersPage.orderItemsTableRows).toHaveCount(2);

			for (const values of [firstValues, secondValues]) {
				await expect(
					pendingOrdersPage.orderItemsTableRowWith(values.engraving)
				).toHaveCount(1);
			}
		});
	}
);

test(
	'Every pricing type of a linked SKU resolves in the mini cart edit panel',
	{tag: ['@COMMERCE-12609', '@LPD-106905']},
	async ({
		apiHelpers,
		commerceAdminProductPage,
		commerceMiniCartPage,
		commercePricingSystemSettingsPage,
		page,
		productDetailsPage,
	}) => {
		test.setTimeout(300000);

		const linkedSkuNames = {
			black: 'MIN93020',
			blue: 'MIN93015',
			green: 'MIN93027',
			red: 'MIN93021',
			white: 'MIN93017',
		};

		const {buyerUser, catalog, channel, product, site} =
			await setUpMiniumBundledProduct(
				apiHelpers,
				commerceAdminProductPage,
				{
					buildOptionSpecs: async () => {
						const skuByName = await getSkusByName(
							apiHelpers,
							Object.values(linkedSkuNames)
						);

						return [
							{
								fieldType: 'select',
								name: 'Color',
								priceType: 'dynamic',
								skuContributor: true,
								values: Object.entries(linkedSkuNames).map(
									([key, skuName]) => ({
										key,
										name:
											key[0].toUpperCase() + key.slice(1),
										skuId: skuByName[skuName].id,
									})
								),
							},
						];
					},
				}
			);

		const skuFor = (optionValueKey: string) =>
			findSkuByOptionValueKeys(product, [optionValueKey]);

		await test.step('Price the Black SKU on application, discount the Red one and give the Green one its own price list', async () => {
			const basePriceLists =
				await apiHelpers.headlessCommerceAdminPricing.getBasePriceListId(
					catalog.id
				);

			await apiHelpers.headlessCommerceAdminPricing.postPriceEntry({
				price: 0,
				priceListId: basePriceLists.items[0].id,
				priceOnApplication: true,
				skuId: skuFor('black').id,
			});

			const discount =
				await apiHelpers.headlessCommerceAdminPricing.postDiscount({
					level: 'L1',
					percentageLevel1: 50,
					target: 'skus',
					usePercentage: true,
				});

			await apiHelpers.headlessCommerceAdminPricing.postDiscountSku(
				discount.id,
				{skuId: skuFor('red').id}
			);

			const priceList =
				await apiHelpers.headlessCommerceAdminPricing.postPriceList({
					catalogId: catalog.id,
					currencyCode: 'USD',
					name: getRandomString(),
					type: 'price-list',
				});

			await apiHelpers.headlessCommerceAdminPricing.postPriceListChannel(
				priceList.id,
				channel.id
			);

			await apiHelpers.headlessCommerceAdminPricing.postPriceEntry({
				discountDiscovery: false,
				discountLevel1: 10,
				discountLevel2: 20,
				discountLevel3: 0,
				discountLevel4: 0,
				price: 100,
				priceListId: priceList.id,
				skuId: skuFor('green').id,
			});
		});

		await commercePricingSystemSettingsPage.setDisplayDiscountLevels(true);

		try {
			await performLogout(page);
			await performLoginViaApi({
				page,
				screenName: buyerUser.alternateName,
			});

			await page.goto(
				`/web${site.friendlyUrlPath}/p/${product.urls['en_US']}`,
				{waitUntil: 'networkidle'}
			);

			await test.step('The product details page prices the Black option value on application', async () => {
				await productDetailsPage.selectOptionContaining(
					'Black',
					'Color'
				);

				await expect(productDetailsPage.priceContainer).toContainText(
					'Price on Application'
				);
			});

			await test.step('The Blue option value reaches the cart at its linked SKU price', async () => {
				await productDetailsPage.selectOptionContaining(
					'Blue',
					'Color'
				);

				await productDetailsPage.productDetailAddToCartButton.click();

				await commerceMiniCartPage.open();

				await expect(
					commerceMiniCartPage.miniCartItemListPrice(
						commerceMiniCartPage.miniCartItemForSku(
							skuFor('blue').sku
						)
					)
				).toHaveText('$ 50.00');
			});

			const editOptionValue = async (
				fromOptionValueKey: string,
				toOptionValueName: string
			) => {
				await commerceMiniCartPage.open();

				await commerceMiniCartPage
					.miniCartItemActionsButton(
						commerceMiniCartPage.miniCartItemForSku(
							skuFor(fromOptionValueKey).sku
						)
					)
					.click();

				await commerceMiniCartPage.editMenuItem.click();

				await expect(
					commerceMiniCartPage.miniCartEditItemPanel
				).toBeVisible();

				await commerceMiniCartPage.selectEditItemOption(
					toOptionValueName,
					'Color'
				);
			};

			await test.step('A promotion price is shown in the edit panel and carried to the order item', async () => {
				await editOptionValue('blue', 'White');

				await expect(
					commerceMiniCartPage.miniCartEditItemPrice(
						'Price as Configured'
					)
				).toHaveText('$ 90.00');
				await expect(
					commerceMiniCartPage.miniCartEditItemInactivePrice
				).toHaveText('$ 100.00');
				await expect(
					commerceMiniCartPage.miniCartEditItemPrice('Promo Price')
				).toHaveText('$ 90.00');

				await commerceMiniCartPage.miniCartSaveButton.click();

				await expect(
					commerceMiniCartPage.miniCartItemPromoPrice(
						commerceMiniCartPage.miniCartItemForSku(
							skuFor('white').sku
						)
					)
				).toHaveText('$ 90.00');
			});

			await test.step('A price on application is shown in the edit panel and blocks the order item', async () => {
				await editOptionValue('white', 'Black');

				await expect(
					commerceMiniCartPage.miniCartEditItemPanel
				).toContainText('Price on Application');

				await commerceMiniCartPage.miniCartSaveButton.click();

				await expect(
					commerceMiniCartPage.miniCartItemPriceOnApplication(
						commerceMiniCartPage.miniCartItemForSku(
							skuFor('black').sku
						)
					)
				).toHaveText('Price on Application');
				await expect(
					commerceMiniCartPage.requestAQuoteButton
				).toBeVisible();
			});

			await test.step('A discount is shown in the edit panel', async () => {
				await editOptionValue('black', 'Red');

				await expect(
					commerceMiniCartPage.miniCartEditItemPrice('Discount')
				).toHaveText('-50%');
				await expect(
					commerceMiniCartPage.miniCartEditItemPrice(
						'Price as Configured'
					)
				).toHaveText('$ 45.00');
				await expect(
					commerceMiniCartPage.miniCartEditItemInactivePrice
				).toHaveText('$ 90.00');

				await commerceMiniCartPage.miniCartSaveButton.click();

				await expect(
					commerceMiniCartPage.miniCartItemListPrice(
						commerceMiniCartPage.miniCartItemForSku(
							skuFor('red').sku
						)
					)
				).toHaveText('$ 90.00');
			});

			await test.step('A price list with discount levels resolves them on the order item', async () => {
				await editOptionValue('red', 'Green');

				await commerceMiniCartPage.miniCartSaveButton.click();

				const cartItem = commerceMiniCartPage.miniCartItemForSku(
					skuFor('green').sku
				);

				await expect(
					commerceMiniCartPage.miniCartItemInactivePrice(cartItem)
				).toHaveText('$ 160.00');
				await expect(
					commerceMiniCartPage.miniCartItemDiscountLevels(cartItem)
				).toHaveText(['10', '20', '0', '0']);
				await expect(
					commerceMiniCartPage.miniCartItemNetPrice(cartItem)
				).toHaveText('$ 132.00');
			});
		}
		finally {
			await performLoginViaApi({page, screenName: 'test'});

			await commercePricingSystemSettingsPage.setDisplayDiscountLevels(
				false
			);
		}
	}
);
