/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';
import {readFileSync} from 'fs';
import path from 'path';

import {apiHelpersTest} from '../../../../fixtures/apiHelpersTest';
import {commercePagesTest} from '../../../../fixtures/commercePagesTest';
import {dataApiHelpersTest} from '../../../../fixtures/dataApiHelpersTest';
import {displayPageTemplatesPagesTest} from '../../../../fixtures/displayPageTemplatesPagesTest';
import {featureFlagsTest} from '../../../../fixtures/featureFlagsTest';
import {loginTest} from '../../../../fixtures/loginTest';
import {pageEditorPagesTest} from '../../../../fixtures/pageEditorPagesTest';
import {DataApiHelpers} from '../../../../helpers/ApiHelpers';
import {clickAndExpectToBeHidden} from '../../../../utils/clickAndExpectToBeHidden';
import getRandomString from '../../../../utils/getRandomString';
import {
	performLoginViaApi,
	performUserSwitch,
} from '../../../../utils/performLogin';
import {waitForAlert} from '../../../../utils/waitForAlert';
import getFragmentDefinition from '../../../layout-content-page-editor-web/main/utils/getFragmentDefinition';
import getPageDefinition from '../../../layout-content-page-editor-web/main/utils/getPageDefinition';
import {
	assignBuyerUserToAccount,
	createAccountWithBuyerUser,
	deployProductFragmentsOnDefaultDPT,
	miniumSetUp,
} from '../../utils/commerce';

export const test = mergeTests(
	apiHelpersTest,
	commercePagesTest,
	dataApiHelpersTest,
	displayPageTemplatesPagesTest,
	featureFlagsTest({
		'LPS-178052': {enabled: true},
	}),
	loginTest(),
	pageEditorPagesTest
);

let account;
let buyerUser;
let catalog: {id: number};
let channel: {id: number; name: string};
let products: {[name: string]: any};
let setupData: Array<{id: number | string; type: string}>;
let site: Site;

test.beforeAll(async ({browser}) => {
	const page = await browser.newPage();

	await performLoginViaApi({page, screenName: 'test'});

	const apiHelpers = new DataApiHelpers(page);

	const miniumResult = await miniumSetUp(apiHelpers);

	catalog = miniumResult.catalog;
	channel = miniumResult.channel;
	site = miniumResult.site;

	const accountResult = await createAccountWithBuyerUser(
		apiHelpers,
		site.id,
		{accountName: `Commerce Account ${site.name}`}
	);

	account = accountResult.account;
	buyerUser = accountResult.buyerUser;

	products = {};

	for (const name of [
		'ABS Sensor',
		'Mount',
		'Torque Converters',
		'Transmission Cooler Line Assembly',
		'U-Joint',
	]) {
		products[name] =
			await apiHelpers.headlessCommerceAdminCatalog.getProductByName(
				name,
				{
					catalogId: catalog.id,
					nestedFields: 'productConfiguration,skus',
				}
			);
	}

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

test.beforeEach(async ({apiHelpers}) => {
	await setSiteTheme(apiHelpers, 'minium_WAR_miniumtheme');
});

test.afterEach(async ({apiHelpers, page}) => {
	await performLoginViaApi({page, screenName: 'test'});

	const trackedIds = new Set(apiHelpers.data.map((entry) => entry.id));

	const orders = await apiHelpers.headlessCommerceAdminOrder.getOrdersPage();

	for (const order of orders.items ?? []) {
		if (!trackedIds.has(order.id)) {
			apiHelpers.data.push({id: order.id, type: 'order'});
		}
	}
});

async function setSiteTheme(apiHelpers: DataApiHelpers, themeId: string) {
	return apiHelpers.jsonWebServicesLayoutSet.updateLookAndFeel({
		groupId: String(site.id),
		themeId,
	});
}

test(
	'Can view the product card fragment fields, its promotion and discount prices, and add the product to the comparison bar and the wish list',
	{tag: ['@COMMERCE-11197', '@LPD-106457']},
	async ({
		apiHelpers,
		commerceThemeMiniumCatalogPage,
		commerceWishListPage,
		displayPageTemplatesPage,
		page,
		pageEditorPage,
		productComparisonPage,
	}) => {
		test.setTimeout(300000);

		const absSensor = products['ABS Sensor'];
		const uJoint = products['U-Joint'];
		const uJointImageId = uJoint.thumbnail.match(/images\/(\d+)/)[1];

		await test.step('Deploy the Product Card fragment and the Product Comparison Bar widget on the default product display page template', async () => {
			await deployProductFragmentsOnDefaultDPT(apiHelpers, {
				displayPageTemplatesPage,
				fragmentNames: ['Product Card'],
				pageEditorPage,
				site,
				widgets: [
					{category: 'Commerce', name: 'Product Comparison Bar'},
				],
			});
		});

		await test.step('Discount ABS Sensor by 20%', async () => {
			await apiHelpers.headlessCommerceAdminPricing.postDiscount({
				active: true,
				discountProducts: [{productId: absSensor.productId}],
				level: 'L1',
				percentageLevel1: 20,
				target: 'products',
				usePercentage: true,
			});
		});

		await performUserSwitch(page, buyerUser.alternateName);

		await test.step('Every product card field renders for U-Joint', async () => {
			await page.goto(`/web${site.friendlyUrlPath}/p/u-joint`);

			const productCard =
				commerceThemeMiniumCatalogPage.productCardFragment;

			await expect(productCard).toBeVisible();
			await expect(
				commerceThemeMiniumCatalogPage.productCardFragmentName(
					productCard,
					'U-Joint'
				)
			).toBeVisible();
			await expect(
				commerceThemeMiniumCatalogPage.productCardFragmentSku(
					productCard,
					'MIN55861'
				)
			).toBeVisible();
			await expect(
				commerceThemeMiniumCatalogPage.productCardFragmentPrice(
					productCard,
					'$ 24.00'
				)
			).toBeVisible();
			await expect(
				commerceThemeMiniumCatalogPage.productCardFragmentAvailabilityLabel(
					productCard
				)
			).toHaveText('Available');
			await expect(
				commerceThemeMiniumCatalogPage.productCardFragmentImage(
					productCard
				)
			).toBeVisible();
			await expect(
				commerceThemeMiniumCatalogPage.productCardFragmentAddToCartButton(
					productCard
				)
			).toBeVisible();
			await expect(
				commerceThemeMiniumCatalogPage.quantitySelector(productCard)
			).toBeVisible();
			await expect(
				commerceThemeMiniumCatalogPage.productCardFragmentCompareCheckbox(
					productCard
				)
			).toBeVisible();
			await expect(
				commerceThemeMiniumCatalogPage.productCardFragmentAddToWishListButton(
					productCard
				)
			).toBeVisible();
		});

		await test.step('The promotion price renders for Brake Pads', async () => {
			await page.goto(`/web${site.friendlyUrlPath}/p/brake-pads`);

			const productCard =
				commerceThemeMiniumCatalogPage.productCardFragment;

			await expect(
				commerceThemeMiniumCatalogPage.productCardFragmentInactivePrice(
					productCard
				)
			).toHaveText('$ 21.00');
			await expect(
				commerceThemeMiniumCatalogPage.productCardFragmentPromoPrice(
					productCard
				)
			).toHaveText('$ 18.90');
		});

		await test.step('The discounted price renders for ABS Sensor', async () => {
			await page.goto(`/web${site.friendlyUrlPath}/p/abs-sensor`);

			const productCard =
				commerceThemeMiniumCatalogPage.productCardFragment;

			await expect(
				commerceThemeMiniumCatalogPage.productCardFragmentInactivePrice(
					productCard
				)
			).toHaveText('$ 50.00');
			await expect(
				commerceThemeMiniumCatalogPage.productCardFragmentNetPrice(
					productCard
				)
			).toHaveText('$ 40.00');
		});

		await test.step('Checking Compare adds U-Joint to the comparison bar', async () => {
			await page.goto(`/web${site.friendlyUrlPath}/p/u-joint`);

			await commerceThemeMiniumCatalogPage
				.productCardFragmentCompareCheckbox(
					commerceThemeMiniumCatalogPage.productCardFragment
				)
				.getByRole('checkbox')
				.check();

			await expect(
				productComparisonPage.compareBarItemThumbnail(uJointImageId)
			).toBeVisible();
		});

		await test.step('Adding U-Joint to the wish list fills the heart and lists the product', async () => {
			await page.goto(`/web${site.friendlyUrlPath}/p/u-joint`);

			const productCard =
				commerceThemeMiniumCatalogPage.productCardFragment;

			if (
				await commerceThemeMiniumCatalogPage
					.productCardFragmentWishListFullIcon(productCard)
					.isVisible()
			) {
				await commerceThemeMiniumCatalogPage
					.productCardFragmentWishListToggle(productCard)
					.click();

				await page.reload();

				await expect(
					commerceThemeMiniumCatalogPage.productCardFragmentWishListFullIcon(
						productCard
					)
				).toHaveCount(0);
			}

			await commerceThemeMiniumCatalogPage
				.productCardFragmentWishListToggle(productCard)
				.click();

			await expect(
				commerceThemeMiniumCatalogPage.productCardFragmentWishListFullIcon(
					productCard
				)
			).toBeVisible();

			await page.goto(`/web${site.friendlyUrlPath}/lists`);

			await commerceWishListPage.wishListLink('Default').click();

			await expect(
				commerceWishListPage.wishListContentPortlet
			).toContainText('U-Joint');
		});
	}
);

test(
	'Can add products with minimum, multiple and allowed order quantities to the cart from the product card fragment and check out',
	{tag: ['@COMMERCE-11199', '@LPD-106457']},
	async ({
		apiHelpers,
		checkoutPage,
		commerceMiniCartPage,
		commerceThemeMiniumCatalogPage,
		displayPageTemplatesPage,
		page,
		pageEditorPage,
	}) => {
		test.setTimeout(300000);

		const absSensor = products['ABS Sensor'];
		const mount = products['Mount'];
		const torqueConverters = products['Torque Converters'];
		const uJoint = products['U-Joint'];

		await test.step('Deploy the Product Card fragment on the default product display page template', async () => {
			await deployProductFragmentsOnDefaultDPT(apiHelpers, {
				displayPageTemplatesPage,
				fragmentNames: ['Product Card'],
				pageEditorPage,
				site,
			});
		});

		await test.step('Constrain the order quantities of three products', async () => {
			await apiHelpers.headlessCommerceAdminCatalog.patchProduct(
				String(absSensor.productId),
				{
					name: absSensor.name,
					productConfiguration: {minOrderQuantity: 3},
				}
			);
			await apiHelpers.headlessCommerceAdminCatalog.patchProduct(
				String(mount.productId),
				{
					name: mount.name,
					productConfiguration: {multipleOrderQuantity: 4},
				}
			);
			await apiHelpers.headlessCommerceAdminCatalog.patchProduct(
				String(torqueConverters.productId),
				{
					name: torqueConverters.name,
					productConfiguration: {allowedOrderQuantities: [1, 3, 5]},
				}
			);
		});

		try {
			await performUserSwitch(page, buyerUser.alternateName);

			const productCard =
				commerceThemeMiniumCatalogPage.productCardFragment;

			const addToCart = async (itemsCount: number) => {
				await commerceThemeMiniumCatalogPage
					.productCardFragmentAddToCartButton(productCard)
					.click();

				await expect(
					commerceMiniCartPage.miniCartButton
				).toHaveAttribute('data-badge-count', String(itemsCount));
			};

			await test.step('An unconstrained product is added with the preselected quantity', async () => {
				await page.goto(`/web${site.friendlyUrlPath}/p/u-joint`);

				await expect(
					commerceThemeMiniumCatalogPage.quantitySelector(productCard)
				).toHaveValue('1');

				await addToCart(1);
			});

			await test.step('A quantity below the minimum is rejected and the minimum is accepted', async () => {
				await page.goto(`/web${site.friendlyUrlPath}/p/abs-sensor`);

				await expect(
					commerceThemeMiniumCatalogPage.quantitySelector(productCard)
				).toHaveValue('3');

				await commerceThemeMiniumCatalogPage
					.quantitySelector(productCard)
					.fill('2');

				await expect(
					commerceThemeMiniumCatalogPage.quantitySelectorErrorContainer(
						productCard
					)
				).toHaveClass(/has-error/);

				await commerceThemeMiniumCatalogPage.checkQuantitiesInPopOverMessages(
					absSensor.productConfiguration.maxOrderQuantity,
					3,
					1,
					false,
					true,
					false
				);

				await commerceThemeMiniumCatalogPage
					.quantitySelector(productCard)
					.fill('4');

				await expect(
					commerceThemeMiniumCatalogPage.quantitySelectorErrorContainer(
						productCard
					)
				).not.toHaveClass(/has-error/);

				await addToCart(2);
			});

			await test.step('A quantity that is not a multiple is rejected and a multiple is accepted', async () => {
				await page.goto(`/web${site.friendlyUrlPath}/p/mount`);

				await commerceThemeMiniumCatalogPage
					.quantitySelector(productCard)
					.fill('3');

				await expect(
					commerceThemeMiniumCatalogPage.quantitySelectorErrorContainer(
						productCard
					)
				).toHaveClass(/has-error/);

				await commerceThemeMiniumCatalogPage.checkQuantitiesInPopOverMessages(
					mount.productConfiguration.maxOrderQuantity,
					4,
					4,
					false,
					true,
					true
				);

				await commerceThemeMiniumCatalogPage
					.quantitySelector(productCard)
					.fill('8');

				await expect(
					commerceThemeMiniumCatalogPage.quantitySelectorErrorContainer(
						productCard
					)
				).not.toHaveClass(/has-error/);

				await addToCart(3);
			});

			await test.step('Only the allowed quantities are offered', async () => {
				await page.goto(
					`/web${site.friendlyUrlPath}/p/torque-converters`
				);

				await expect(
					commerceThemeMiniumCatalogPage
						.quantitySelectorList(productCard)
						.locator('option')
				).toHaveText(['1', '3', '5']);

				await commerceThemeMiniumCatalogPage
					.quantitySelectorList(productCard)
					.selectOption('5');

				await addToCart(4);
			});

			await test.step('The mini cart holds every added item', async () => {
				await commerceMiniCartPage.miniCartButton.click();

				for (const [product, quantity, quantityFromList] of [
					[uJoint, '1', false],
					[absSensor, '4', false],
					[mount, '8', false],
					[torqueConverters, '5', true],
				] as Array<[typeof uJoint, string, boolean]>) {
					const productName = product.name['en_US'];
					const miniCartItem =
						commerceMiniCartPage.miniCartItem(productName);

					await expect(miniCartItem).toBeVisible();
					await expect(
						commerceMiniCartPage.miniCartSku(product.skus[0].sku)
					).toBeVisible();
					await expect(
						commerceMiniCartPage.miniCartItemListPrice(productName)
					).toHaveText(`$ ${product.skus[0].price.toFixed(2)}`);
					await expect(
						quantityFromList
							? commerceThemeMiniumCatalogPage.quantitySelectorList(
									miniCartItem
								)
							: commerceThemeMiniumCatalogPage.quantitySelector(
									miniCartItem
								)
					).toHaveValue(quantity);
				}

				await expect(
					commerceMiniCartPage.miniCartSummaryItem('Quantity')
				).toHaveText('18');
				await expect(
					commerceMiniCartPage.miniCartSummaryItem('Subtotal')
				).toHaveText('$ 418.00');
				await expect(
					commerceMiniCartPage.miniCartTotalPrice
				).toHaveText('$ 418.00');
			});

			await test.step('The order can be checked out', async () => {
				await commerceMiniCartPage.submitButton.click();

				await checkoutPage.performCheckout({
					shippingAddress: {
						city: 'Test City',
						countryLabel: 'United States',
						name: 'Address Name',
						regionLabel: 'Florida',
						street: 'Test Address',
						zip: '12345',
					},
				});
			});
		}
		finally {
			await performLoginViaApi({page, screenName: 'test'});

			for (const product of [absSensor, mount, torqueConverters]) {
				await apiHelpers.headlessCommerceAdminCatalog.patchProduct(
					String(product.productId),
					{
						name: product.name,
						productConfiguration: {
							allowedOrderQuantities: [],
							minOrderQuantity: 1,
							multipleOrderQuantity: 1,
						},
					}
				);
			}
		}
	}
);

test(
	'The product card fragment renders a diagram card with the image, the short description and a View button for a diagram type product',
	{tag: ['@COMMERCE-11195', '@LPD-106457']},
	async ({
		apiHelpers,
		commerceThemeMiniumCatalogPage,
		displayPageTemplatesPage,
		page,
		pageEditorPage,
	}) => {
		test.setTimeout(300000);

		const productName = `Diagram T-Shirt ${getRandomString()}`;

		const diagramProduct =
			await apiHelpers.headlessCommerceAdminCatalog.postProduct({
				catalogId: catalog.id,
				images: [
					{
						attachment: readFileSync(
							path.join(__dirname, '/dependencies/liferay.png')
						).toString('base64'),
						title: {en_US: 'Black'},
					},
				],
				name: {en_US: productName},
				productType: 'diagram',
				shortDescription: {en_US: 'This is a short description.'},
				skus: [],
			});

		await test.step('Deploy the Product Card fragment on the default product display page template', async () => {
			await deployProductFragmentsOnDefaultDPT(apiHelpers, {
				displayPageTemplatesPage,
				fragmentNames: ['Product Card'],
				pageEditorPage,
				site,
			});
		});

		await performUserSwitch(page, buyerUser.alternateName);

		await page.goto(
			`/web${site.friendlyUrlPath}/p/${diagramProduct.urls['en_US']}`
		);

		const productCard = commerceThemeMiniumCatalogPage.productCardFragment;

		await expect(
			commerceThemeMiniumCatalogPage.productCardFragmentName(
				productCard,
				productName
			)
		).toBeVisible();
		await expect(
			commerceThemeMiniumCatalogPage.productCardFragmentImage(productCard)
		).toBeVisible();
		await expect(
			commerceThemeMiniumCatalogPage.productCardFragmentDescription(
				productCard
			)
		).toHaveText('This is a short description.');
		await expect(
			commerceThemeMiniumCatalogPage.productCardFragmentViewButton(
				productCard
			)
		).toBeVisible();
		await expect(
			commerceThemeMiniumCatalogPage.quantitySelector(productCard)
		).toHaveCount(0);
	}
);

test(
	'The product card fragment shows the Price on Application label for a SKU priced on application',
	{tag: ['@COMMERCE-11636', '@LPD-106457']},
	async ({
		apiHelpers,
		commerceThemeMiniumCatalogPage,
		displayPageTemplatesPage,
		page,
		pageEditorPage,
	}) => {
		test.setTimeout(300000);

		const uJoint = products['U-Joint'];

		const basePriceList = (
			await apiHelpers.headlessCommerceAdminPricing.getBasePriceListId(
				catalog.id
			)
		).items[0];

		const priceEntry = (
			await apiHelpers.headlessCommerceAdminPricing.getPriceListEntries(
				basePriceList.id
			)
		).items.find(
			(entry: {skuId: number}) => entry.skuId === uJoint.skus[0].id
		);

		await test.step('Deploy the Product Card fragment on the default product display page template', async () => {
			await deployProductFragmentsOnDefaultDPT(apiHelpers, {
				displayPageTemplatesPage,
				fragmentNames: ['Product Card'],
				pageEditorPage,
				site,
			});
		});

		try {
			await apiHelpers.headlessCommerceAdminPricing.patchPriceEntry(
				priceEntry.priceEntryId,
				{priceOnApplication: true}
			);

			await performUserSwitch(page, buyerUser.alternateName);

			await page.goto(
				`/web${site.friendlyUrlPath}/p/${uJoint.urls['en_US']}`
			);

			await expect(
				commerceThemeMiniumCatalogPage.productCardFragmentPriceOnApplicationLabel(
					commerceThemeMiniumCatalogPage.productCardFragment
				)
			).toBeVisible();
		}
		finally {
			await performLoginViaApi({page, screenName: 'test'});

			await apiHelpers.headlessCommerceAdminPricing.patchPriceEntry(
				priceEntry.priceEntryId,
				{priceOnApplication: false}
			);
		}
	}
);

test(
	'The Add to Cart fragment shows a placeholder in the page editor and renders an enabled add to cart form for every product on the storefront',
	{tag: ['@COMMERCE-9382', '@LPD-106457']},
	async ({
		apiHelpers,
		commerceThemeMiniumCatalogPage,
		displayPageTemplatesPage,
		page,
		pageEditorPage,
	}) => {
		test.setTimeout(300000);

		const absSensor = products['ABS Sensor'];
		const uJoint = products['U-Joint'];

		await test.step('Deploy the Add to Cart fragment on the default product display page template', async () => {
			await deployProductFragmentsOnDefaultDPT(apiHelpers, {
				displayPageTemplatesPage,
				fragmentNames: ['Add to Cart'],
				onFragmentsAdded: async () => {
					await expect(
						page.getByText(
							'The add to cart component will be shown here.',
							{exact: true}
						)
					).toBeVisible();
				},
				pageEditorPage,
				site,
			});
		});

		await performUserSwitch(page, buyerUser.alternateName);

		for (const product of [absSensor, uJoint]) {
			await page.goto(
				`/web${site.friendlyUrlPath}/p/${product.urls['en_US']}`
			);

			await expect(
				commerceThemeMiniumCatalogPage.quantitySelector(
					commerceThemeMiniumCatalogPage.addToCartFragment
				)
			).toHaveValue('1');
			await expect(
				commerceThemeMiniumCatalogPage.addToCartFragmentButton
			).toBeEnabled();
		}
	}
);

test(
	'Can add a product to the cart from the Add to Cart fragment and check out',
	{tag: ['@COMMERCE-9382', '@LPD-106457']},
	async ({
		apiHelpers,
		checkoutPage,
		commerceMiniCartPage,
		commerceThemeMiniumCatalogPage,
		displayPageTemplatesPage,
		page,
		pageEditorPage,
	}) => {
		test.setTimeout(300000);

		const uJoint = products['U-Joint'];
		const productName = uJoint.name['en_US'];

		await test.step('Deploy the Add to Cart fragment on the default product display page template', async () => {
			await deployProductFragmentsOnDefaultDPT(apiHelpers, {
				displayPageTemplatesPage,
				fragmentNames: ['Add to Cart'],
				pageEditorPage,
				site,
			});
		});

		await performUserSwitch(page, buyerUser.alternateName);

		await page.goto(
			`/web${site.friendlyUrlPath}/p/${uJoint.urls['en_US']}`
		);

		await expect(
			commerceThemeMiniumCatalogPage.quantitySelector(
				commerceThemeMiniumCatalogPage.addToCartFragment
			)
		).toHaveValue('1');

		await commerceThemeMiniumCatalogPage.addToCartFragmentButton.click();

		await expect(commerceMiniCartPage.miniCartButton).toHaveAttribute(
			'data-badge-count',
			'1'
		);

		await commerceMiniCartPage.miniCartButton.click();

		await commerceMiniCartPage.submitButton.click();

		await checkoutPage.performCheckout(
			{
				shippingAddress: {
					city: 'Test City',
					countryLabel: 'United States',
					name: 'Address Name',
					regionLabel: 'Florida',
					street: 'Test Address',
					zip: '12345',
				},
			},
			async (activeStep) => {
				if (activeStep.includes('Order Summary')) {
					await expect(
						checkoutPage.orderSummaryItemCell(
							productName,
							'quantity'
						)
					).toHaveText('1');
					await expect(
						checkoutPage.orderSummaryItemListPrice(productName)
					).toHaveText(`$ ${uJoint.skus[0].price.toFixed(2)}`);
				}
			}
		);
	}
);

test(
	'The Add to Cart fragment preselects the minimum order quantity and rejects a lower one',
	{tag: ['@COMMERCE-9382', '@LPD-106457']},
	async ({
		apiHelpers,
		commerceThemeMiniumCatalogPage,
		displayPageTemplatesPage,
		page,
		pageEditorPage,
	}) => {
		test.setTimeout(300000);

		const transmissionCoolerLineAssembly =
			products['Transmission Cooler Line Assembly'];
		const uJoint = products['U-Joint'];

		await apiHelpers.headlessCommerceAdminCatalog.patchProduct(
			String(transmissionCoolerLineAssembly.productId),
			{
				name: transmissionCoolerLineAssembly.name,
				productConfiguration: {minOrderQuantity: 5},
			}
		);

		await test.step('Deploy the Add to Cart fragment on the default product display page template', async () => {
			await deployProductFragmentsOnDefaultDPT(apiHelpers, {
				displayPageTemplatesPage,
				fragmentNames: ['Add to Cart'],
				pageEditorPage,
				site,
			});
		});

		try {
			await performUserSwitch(page, buyerUser.alternateName);

			const addToCartFragment =
				commerceThemeMiniumCatalogPage.addToCartFragment;
			const maxOrderQuantity =
				transmissionCoolerLineAssembly.productConfiguration
					.maxOrderQuantity;

			await test.step('An unconstrained product preselects one', async () => {
				await page.goto(
					`/web${site.friendlyUrlPath}/p/${uJoint.urls['en_US']}`
				);

				await expect(
					commerceThemeMiniumCatalogPage.quantitySelector(
						addToCartFragment
					)
				).toHaveValue('1');
				await expect(
					commerceThemeMiniumCatalogPage.addToCartFragmentButton
				).toBeEnabled();
			});

			await test.step('A product with a minimum order quantity preselects it', async () => {
				await page.goto(
					`/web${site.friendlyUrlPath}/p/${transmissionCoolerLineAssembly.urls['en_US']}`
				);

				await expect(
					commerceThemeMiniumCatalogPage.quantitySelector(
						addToCartFragment
					)
				).toHaveValue('5');
				await expect(
					commerceThemeMiniumCatalogPage.addToCartFragmentButton
				).toBeEnabled();
			});

			await test.step('A quantity below the minimum turns the selector red and flags the rule', async () => {
				await commerceThemeMiniumCatalogPage
					.quantitySelector(addToCartFragment)
					.fill('3');

				await expect(
					commerceThemeMiniumCatalogPage.quantitySelectorErrorContainer(
						addToCartFragment
					)
				).toHaveClass(/has-error/);

				await commerceThemeMiniumCatalogPage.checkQuantitiesInPopOverMessages(
					maxOrderQuantity,
					5,
					1,
					false,
					true,
					false
				);
			});

			await test.step('A quantity above the minimum is accepted', async () => {
				await commerceThemeMiniumCatalogPage
					.quantitySelector(addToCartFragment)
					.fill('6');

				await expect(
					commerceThemeMiniumCatalogPage.quantitySelectorErrorContainer(
						addToCartFragment
					)
				).not.toHaveClass(/has-error/);

				await commerceThemeMiniumCatalogPage.checkQuantitiesInPopOverMessages(
					maxOrderQuantity,
					5,
					1
				);
			});
		}
		finally {
			await performLoginViaApi({page, screenName: 'test'});

			await apiHelpers.headlessCommerceAdminCatalog.patchProduct(
				String(transmissionCoolerLineAssembly.productId),
				{
					name: transmissionCoolerLineAssembly.name,
					productConfiguration: {minOrderQuantity: 1},
				}
			);
		}
	}
);

test(
	'The Add to Cart fragment is disabled once the account is deactivated',
	{tag: ['@COMMERCE-9382', '@LPD-106457']},
	async ({
		apiHelpers,
		commerceThemeMiniumCatalogPage,
		displayPageTemplatesPage,
		page,
		pageEditorPage,
	}) => {
		test.setTimeout(300000);

		const uJoint = products['U-Joint'];

		const {
			account: deactivatedAccount,
			buyerUser: deactivatedAccountBuyer,
		} = await createAccountWithBuyerUser(apiHelpers, site.id);

		await test.step('Deploy the Add to Cart fragment on the default product display page template', async () => {
			await deployProductFragmentsOnDefaultDPT(apiHelpers, {
				displayPageTemplatesPage,
				fragmentNames: ['Add to Cart'],
				pageEditorPage,
				site,
			});
		});

		const goToProductAsBuyer = async () => {
			await performUserSwitch(
				page,
				deactivatedAccountBuyer.alternateName
			);

			await page.goto(
				`/web${site.friendlyUrlPath}/p/${uJoint.urls['en_US']}`
			);
		};

		await test.step('The add to cart form is enabled for an active account', async () => {
			await goToProductAsBuyer();

			await expect(
				commerceThemeMiniumCatalogPage.quantitySelector(
					commerceThemeMiniumCatalogPage.addToCartFragment
				)
			).toBeEnabled();
			await expect(
				commerceThemeMiniumCatalogPage.addToCartFragmentButton
			).toBeEnabled();
		});

		await test.step('The add to cart form is disabled once the account is deactivated', async () => {
			await performLoginViaApi({page, screenName: 'test'});

			await apiHelpers.headlessAdminUser.patchAccount(
				deactivatedAccount.id,
				{status: 5}
			);

			await goToProductAsBuyer();

			await expect(
				commerceThemeMiniumCatalogPage.quantitySelector(
					commerceThemeMiniumCatalogPage.addToCartFragment
				)
			).toBeDisabled();
			await expect(
				commerceThemeMiniumCatalogPage.addToCartFragmentButton
			).toBeDisabled();
		});
	}
);

test(
	'Can review a cart and check out from the Mini Cart fragment on a content page',
	{tag: ['@COMMERCE-10778', '@LPD-106457']},
	async ({apiHelpers, checkoutPage, commerceMiniCartPage, page}) => {
		test.setTimeout(300000);

		const uJoint = products['U-Joint'];
		const productName = uJoint.name['en_US'];

		await setSiteTheme(apiHelpers, 'classic_WAR_classictheme');

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

		await apiHelpers.headlessCommerceDeliveryCart.postCart(
			{
				accountId: account.id,
				cartItems: [
					{
						options: '[]',
						quantity: 1,
						replacedSkuId: 0,
						skuId: uJoint.skus[0].id,
					},
				],
			},
			channel.id
		);

		await performUserSwitch(page, buyerUser.alternateName);

		await page.goto(`/web${site.friendlyUrlPath}${layout.friendlyUrlPath}`);

		await commerceMiniCartPage.miniCartButton.click();

		await expect(commerceMiniCartPage.miniCartResume).toHaveText(
			'1 Product'
		);
		await expect(
			commerceMiniCartPage.miniCartItem(productName)
		).toBeVisible();
		await expect(
			commerceMiniCartPage.miniCartSku(uJoint.skus[0].sku)
		).toBeVisible();
		await expect(
			commerceMiniCartPage.miniCartItemListPrice(productName)
		).toHaveText(`$ ${uJoint.skus[0].price.toFixed(2)}`);

		await commerceMiniCartPage.submitButton.click();

		await checkoutPage.performCheckout({
			shippingAddress: {
				city: 'Test City',
				countryLabel: 'United States',
				name: 'Address Name',
				regionLabel: 'Florida',
				street: 'Test Address',
				zip: '12345',
			},
		});
	}
);

test(
	'The Mini Cart fragment offers the Request a Quote action and carries it over to the order details page',
	{tag: ['@COMMERCE-11030', '@LPD-106457']},
	async ({
		apiHelpers,
		commerceAdminChannelDetailsPage,
		commerceAdminChannelsPage,
		commerceMiniCartPage,
		orderDetailsPage,
		page,
	}) => {
		test.setTimeout(300000);

		const uJoint = products['U-Joint'];

		await setSiteTheme(apiHelpers, 'classic_WAR_classictheme');

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

		await apiHelpers.headlessCommerceDeliveryCart.postCart(
			{
				accountId: account.id,
				cartItems: [
					{
						options: '[]',
						quantity: 1,
						replacedSkuId: 0,
						skuId: uJoint.skus[0].id,
					},
				],
			},
			channel.id
		);

		const setAllowRequestAQuote = async (checked: boolean) => {
			await commerceAdminChannelsPage.goto();

			await (
				await commerceAdminChannelsPage.channelsTableRowLink(
					channel.name
				)
			).click();

			await commerceAdminChannelDetailsPage.allowRequestAQuote.setChecked(
				checked
			);

			await commerceAdminChannelDetailsPage.saveButton.click();

			await waitForAlert(page, 'success');
		};

		try {
			await setAllowRequestAQuote(true);

			await performUserSwitch(page, buyerUser.alternateName);

			await page.goto(
				`/web${site.friendlyUrlPath}${layout.friendlyUrlPath}`
			);

			await commerceMiniCartPage.miniCartButton.click();

			await commerceMiniCartPage.requestAQuoteButton.click();

			await expect(orderDetailsPage.requestAQuoteButton).toBeVisible();
		}
		finally {
			await performLoginViaApi({page, screenName: 'test'});

			await setAllowRequestAQuote(false);
		}
	}
);

test(
	'The Mini Cart fragment flags a price on application item, warns about it and blocks the submission',
	{tag: ['@COMMERCE-11634', '@LPD-106457']},
	async ({apiHelpers, commerceMiniCartPage, page}) => {
		test.setTimeout(300000);

		const uJoint = products['U-Joint'];
		const productName = uJoint.name['en_US'];

		await setSiteTheme(apiHelpers, 'classic_WAR_classictheme');

		const basePriceList = (
			await apiHelpers.headlessCommerceAdminPricing.getBasePriceListId(
				catalog.id
			)
		).items[0];

		const priceEntry = (
			await apiHelpers.headlessCommerceAdminPricing.getPriceListEntries(
				basePriceList.id
			)
		).items.find(
			(entry: {skuId: number}) => entry.skuId === uJoint.skus[0].id
		);

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

		try {
			await apiHelpers.headlessCommerceAdminPricing.patchPriceEntry(
				priceEntry.priceEntryId,
				{priceOnApplication: true}
			);

			await apiHelpers.headlessCommerceDeliveryCart.postCart(
				{
					accountId: account.id,
					cartItems: [
						{
							options: '[]',
							quantity: 1,
							replacedSkuId: 0,
							skuId: uJoint.skus[0].id,
						},
					],
				},
				channel.id
			);

			await performUserSwitch(page, buyerUser.alternateName);

			await page.goto(
				`/web${site.friendlyUrlPath}${layout.friendlyUrlPath}`
			);

			await commerceMiniCartPage.miniCartButton.click();

			await expect(
				commerceMiniCartPage.miniCartItemPriceOnApplication(productName)
			).toBeVisible();
			await expect(
				commerceMiniCartPage.miniCartPriceOnApplicationInfoMessage
			).toBeVisible();
			await expect(commerceMiniCartPage.submitButton).toBeDisabled();
		}
		finally {
			await performLoginViaApi({page, screenName: 'test'});

			await apiHelpers.headlessCommerceAdminPricing.patchPriceEntry(
				priceEntry.priceEntryId,
				{priceOnApplication: false}
			);
		}
	}
);

test(
	'Can switch account and create an order from the Account Selector fragment on a content page',
	{tag: ['@COMMERCE-10946', '@LPD-106457']},
	async ({apiHelpers, commerceThemeMiniumCatalogPage, page}) => {
		test.setTimeout(300000);

		await setSiteTheme(apiHelpers, 'classic_WAR_classictheme');

		const secondAccount = await apiHelpers.headlessAdminUser.postAccount({
			name: `Commerce Account ${getRandomString()}`,
			type: 'business',
		});

		await assignBuyerUserToAccount(secondAccount, apiHelpers, buyerUser);

		const layout = await apiHelpers.headlessDelivery.createSitePage({
			pageDefinition: getPageDefinition([
				getFragmentDefinition({
					id: getRandomString(),
					key: 'COMMERCE_ACCOUNT_FRAGMENTS-account-selector',
				}),
			]),
			siteId: site.id,
			title: getRandomString(),
		});

		const accountSelectorPageURL = `/web${site.friendlyUrlPath}${layout.friendlyUrlPath}`;

		await performUserSwitch(page, buyerUser.alternateName);

		await page.goto(accountSelectorPageURL);

		await test.step('The dropdown lists both accounts and the buyer can switch between them', async () => {
			await commerceThemeMiniumCatalogPage.openAccountSelectorDropdown();

			await expect(
				commerceThemeMiniumCatalogPage.accountSelectorAccount(
					account.name
				)
			).toBeVisible();
			await expect(
				commerceThemeMiniumCatalogPage.accountSelectorAccount(
					secondAccount.name
				)
			).toBeVisible();

			await commerceThemeMiniumCatalogPage
				.accountSelectorAccount(account.name)
				.click();

			await expect(
				commerceThemeMiniumCatalogPage.accountSelectorSelectedAccount
			).toHaveText(account.name);

			await commerceThemeMiniumCatalogPage.openAccountSelectorDropdown();

			await commerceThemeMiniumCatalogPage
				.accountSelectorAccount(secondAccount.name)
				.click();

			await expect(
				commerceThemeMiniumCatalogPage.accountSelectorSelectedAccount
			).toHaveText(secondAccount.name);
		});

		await test.step('The buyer can create an order from the dropdown and it becomes the active one', async () => {
			await commerceThemeMiniumCatalogPage.accountSelectorButton.click();

			const [cartResponse] = await Promise.all([
				page.waitForResponse(
					(response) =>
						response
							.url()
							.includes('headless-commerce-delivery-cart') &&
						response.request().method() === 'POST'
				),
				commerceThemeMiniumCatalogPage.createNewOrderButton.click(),
			]);

			const createdOrderId = (await cartResponse.json()).id;

			apiHelpers.data.push({id: createdOrderId, type: 'order'});

			await page.goto(accountSelectorPageURL);

			await expect(
				commerceThemeMiniumCatalogPage.accountSelectorOrderId
			).toHaveText(String(createdOrderId));
		});
	}
);

test(
	'The Mini Cart fragment follows the account and the order picked in the Account Selector fragment',
	{tag: ['@COMMERCE-10947', '@LPD-106457']},
	async ({
		apiHelpers,
		commerceMiniCartPage,
		commerceThemeMiniumCatalogPage,
		page,
	}) => {
		test.setTimeout(300000);

		await setSiteTheme(apiHelpers, 'classic_WAR_classictheme');

		const secondAccount = await apiHelpers.headlessAdminUser.postAccount({
			name: `Commerce Account ${getRandomString()}`,
			type: 'business',
		});

		await assignBuyerUserToAccount(secondAccount, apiHelpers, buyerUser);

		const absSensor = products['ABS Sensor'];
		const mount = products['Mount'];
		const uJoint = products['U-Joint'];

		const postCart = async (
			accountId: number,
			product: typeof uJoint,
			quantity: number
		) =>
			apiHelpers.headlessCommerceDeliveryCart.postCart(
				{
					accountId,
					cartItems: [
						{
							options: '[]',
							quantity,
							replacedSkuId: 0,
							skuId: product.skus[0].id,
						},
					],
				},
				channel.id
			);

		const firstAccountCart = await postCart(account.id, uJoint, 3);
		const secondAccountCarts = [
			await postCart(secondAccount.id, absSensor, 2),
			await postCart(secondAccount.id, mount, 5),
		];

		const cartContents = {
			[firstAccountCart.id]: {
				productName: uJoint.name['en_US'],
				quantity: '3',
			},
			[secondAccountCarts[0].id]: {
				productName: absSensor.name['en_US'],
				quantity: '2',
			},
			[secondAccountCarts[1].id]: {
				productName: mount.name['en_US'],
				quantity: '5',
			},
		};

		const layout = await apiHelpers.headlessDelivery.createSitePage({
			pageDefinition: getPageDefinition([
				getFragmentDefinition({
					id: getRandomString(),
					key: 'COMMERCE_ACCOUNT_FRAGMENTS-account-selector',
				}),
				getFragmentDefinition({
					id: getRandomString(),
					key: 'COMMERCE_CART_FRAGMENTS-mini-cart',
				}),
			]),
			siteId: site.id,
			title: getRandomString(),
		});

		const expectMiniCartToHold = async (orderId: number) => {
			const {productName, quantity} = cartContents[orderId];

			await expect(
				commerceThemeMiniumCatalogPage.accountSelectorDropdownMenu
			).toBeHidden();

			await commerceMiniCartPage.miniCartButton.click();

			await expect(
				commerceMiniCartPage.miniCartItem(productName)
			).toBeVisible();
			await expect(
				commerceThemeMiniumCatalogPage.quantitySelector(
					commerceMiniCartPage.miniCartItem(productName)
				)
			).toHaveValue(quantity);

			await clickAndExpectToBeHidden({
				target: page.locator('.mini-cart.is-open'),
				trigger: commerceMiniCartPage.miniCartButtonClose,
			});
		};

		const fragmentsPageURL = `/web${site.friendlyUrlPath}${layout.friendlyUrlPath}`;

		await performUserSwitch(page, buyerUser.alternateName);

		await page.goto(fragmentsPageURL);

		await test.step("The mini cart holds the first account's order", async () => {
			await commerceThemeMiniumCatalogPage.openAccountSelectorDropdown();

			await commerceThemeMiniumCatalogPage
				.accountSelectorAccount(account.name)
				.click();

			await expect(
				commerceThemeMiniumCatalogPage.accountSelectorOrderId
			).toHaveText(String(firstAccountCart.id));

			await expectMiniCartToHold(firstAccountCart.id);
		});

		let activeOrderId: number;

		await test.step("Switching account swaps the mini cart to that account's active order", async () => {
			await commerceThemeMiniumCatalogPage.openAccountSelectorDropdown();

			await commerceThemeMiniumCatalogPage
				.accountSelectorAccount(secondAccount.name)
				.click();

			await expect(
				commerceThemeMiniumCatalogPage.accountSelectorSelectedAccount
			).toHaveText(secondAccount.name);

			activeOrderId = Number(
				await commerceThemeMiniumCatalogPage.accountSelectorOrderId.textContent()
			);

			expect(secondAccountCarts.map((cart) => cart.id)).toContain(
				activeOrderId
			);

			await expectMiniCartToHold(activeOrderId);
		});

		await test.step('Switching the active order swaps the mini cart again', async () => {
			const otherOrderId = secondAccountCarts
				.map((cart) => cart.id)
				.find((id) => id !== activeOrderId);

			await commerceThemeMiniumCatalogPage.accountSelectorButton.click();

			await commerceThemeMiniumCatalogPage
				.accountSelectorOrderLink(String(otherOrderId))
				.click();

			await page.waitForLoadState('networkidle');

			await page.goto(fragmentsPageURL);

			await expect(
				commerceThemeMiniumCatalogPage.accountSelectorOrderId
			).toHaveText(String(otherOrderId));

			await expectMiniCartToHold(otherOrderId);
		});
	}
);
