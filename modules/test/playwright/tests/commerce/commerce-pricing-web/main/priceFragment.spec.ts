/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Page, expect, mergeTests} from '@playwright/test';

import {apiHelpersTest} from '../../../../fixtures/apiHelpersTest';
import {commercePagesTest} from '../../../../fixtures/commercePagesTest';
import {dataApiHelpersTest} from '../../../../fixtures/dataApiHelpersTest';
import {displayPageTemplatesPagesTest} from '../../../../fixtures/displayPageTemplatesPagesTest';
import {loginTest} from '../../../../fixtures/loginTest';
import {pageEditorPagesTest} from '../../../../fixtures/pageEditorPagesTest';
import {DataApiHelpers} from '../../../../helpers/ApiHelpers';
import {ProductDetailsPage} from '../../../../pages/commerce/commerce-product-content-web/productDetailsPage';
import {PageEditorPage} from '../../../../pages/layout-content-page-editor-web/PageEditorPage';
import {DisplayPageTemplatesPage} from '../../../../pages/layout-page-template-admin-web/DisplayPageTemplatesPage';
import getRandomString from '../../../../utils/getRandomString';
import {
	performLoginViaApi,
	performUserSwitch,
} from '../../../../utils/performLogin';
import {
	createAccountWithBuyerUser,
	deployProductFragmentsOnDefaultDPT,
	miniumSetUp,
} from '../../utils/commerce';

export const test = mergeTests(
	apiHelpersTest,
	commercePagesTest,
	dataApiHelpersTest,
	displayPageTemplatesPagesTest,
	loginTest(),
	pageEditorPagesTest
);

let basePriceListId: number;
let brakeSystemCategoryId: number;
let buyerUser;
let catalog: {id: number};
let hoses;
let setupData: Array<{id: number | string; type: string}>;
let site: Site;

test.beforeAll(async ({browser}) => {
	const page = await browser.newPage();

	await performLoginViaApi({page, screenName: 'test'});

	const apiHelpers = new DataApiHelpers(page);

	const miniumResult = await miniumSetUp(apiHelpers);

	catalog = miniumResult.catalog;
	site = miniumResult.site;

	buyerUser = (
		await createAccountWithBuyerUser(apiHelpers, site.id, {
			accountName: `Commerce Account ${site.name}`,
		})
	).buyerUser;

	hoses = await apiHelpers.headlessCommerceAdminCatalog.getProductByName(
		'Hoses',
		{catalogId: catalog.id, nestedFields: 'productOptions,skus'}
	);

	basePriceListId = (
		await apiHelpers.headlessCommerceAdminPricing.getBasePriceListId(
			catalog.id
		)
	).items[0].id;

	const priceEntries =
		await apiHelpers.headlessCommerceAdminPricing.getPriceListEntries(
			basePriceListId
		);

	for (const {price, sku} of [
		{price: 25, sku: 'MIN93022A'},
		{price: 50, sku: 'MIN93022B'},
		{price: 100, sku: 'MIN93022C'},
	]) {
		const skuId = hoses.skus.find(
			(hosesSku: {sku: string}) => hosesSku.sku === sku
		).id;

		const priceEntry = priceEntries.items.find(
			(entry: {skuId: number}) => entry.skuId === skuId
		);

		await apiHelpers.headlessCommerceAdminPricing.patchPriceEntry(
			priceEntry.priceEntryId,
			{price}
		);
	}

	const companyGroup = await apiHelpers.jsonWebServicesGroup.getCompanyGroup(
		await page.evaluate(() => Liferay.ThemeDisplay.getCompanyId())
	);

	const taxonomyVocabularies =
		await apiHelpers.headlessAdminTaxonomy.getTaxonomyVocabularyBySiteId(
			String(companyGroup.groupId)
		);

	const taxonomyCategories =
		await apiHelpers.headlessAdminTaxonomy.getTaxonomyCategoryByVocabularyId(
			taxonomyVocabularies.items.find(
				(vocabulary: {name: string}) => vocabulary.name === site.name
			).id
		);

	brakeSystemCategoryId = taxonomyCategories.items.find(
		(category: {name: string}) => category.name === 'Brake System'
	).id;

	await deployProductFragmentsOnDefaultDPT(apiHelpers, {
		displayPageTemplatesPage: new DisplayPageTemplatesPage(page),
		fragmentNames: ['Price', 'Option Selector'],
		pageEditorPage: new PageEditorPage(page),
		site,
	});

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

test.afterEach(async ({page}) => {
	await performLoginViaApi({page, screenName: 'test'});
});

function hosesSkuId(sku: string) {
	return hoses.skus.find((hosesSku: {sku: string}) => hosesSku.sku === sku)
		.id;
}

async function expectProductPrices(
	page: Page,
	productDetailsPage: ProductDetailsPage,
	product: {urls: {[key: string]: string}},
	optionName: string,
	rows: Array<[string, string, string]>,
	discountedPriceType: 'net' | 'promo' = 'net'
) {
	await page.goto(`/web${site.friendlyUrlPath}/p/${product.urls['en_US']}`);

	const discountedPrice =
		discountedPriceType === 'promo'
			? productDetailsPage.priceFragmentPromoPrice
			: productDetailsPage.priceFragmentNetPrice;

	for (const [optionValue, listPrice, discountedPriceText] of rows) {
		await productDetailsPage.selectOption(optionValue, optionName);

		if (discountedPriceText) {
			await expect(
				productDetailsPage.priceFragmentInactivePrice
			).toHaveText(listPrice);
			await expect(discountedPrice).toHaveText(discountedPriceText);
		}
		else {
			await expect(productDetailsPage.priceFragmentListPrice).toHaveText(
				listPrice
			);
			await expect(discountedPrice).toHaveCount(0);
		}
	}
}

test(
	'The price fragment renders the discounted price, and a SKU targeted discount wins over a product targeted one',
	{tag: ['@COMMERCE-7617', '@LPD-106621']},
	async ({apiHelpers, page, productDetailsPage}) => {
		test.setTimeout(300000);

		await apiHelpers.headlessCommerceAdminPricing.postDiscount({
			active: true,
			discountProducts: [{productId: hoses.productId}],
			level: 'L1',
			percentageLevel1: 50,
			target: 'products',
			title: `Discount ${getRandomString()}`,
			usePercentage: true,
		});

		const skuDiscount =
			await apiHelpers.headlessCommerceAdminPricing.postDiscount({
				active: true,
				level: 'L1',
				percentageLevel1: 90,
				target: 'skus',
				title: `Discount ${getRandomString()}`,
				usePercentage: false,
			});

		await apiHelpers.headlessCommerceAdminPricing.postDiscountSku(
			skuDiscount.id,
			{
				skuId: hoses.skus.find(
					(hosesSku: {sku: string}) => hosesSku.sku === 'MIN93022C'
				).id,
			}
		);

		await performUserSwitch(page, buyerUser.alternateName);

		await page.goto(`/web${site.friendlyUrlPath}/p/${hoses.urls['en_US']}`);

		for (const [optionValue, listPrice, netPrice] of [
			['6', '$ 25.00', '$ 12.50'],
			['24', '$ 50.00', '$ 25.00'],
			['48', '$ 100.00', '$ 10.00'],
		]) {
			await productDetailsPage.selectOption(
				optionValue,
				'Package Quantity'
			);

			await expect(
				productDetailsPage.priceFragmentInactivePrice
			).toHaveText(listPrice);
			await expect(productDetailsPage.priceFragmentNetPrice).toHaveText(
				netPrice
			);
		}
	}
);

test(
	'The price fragment renders a category targeted discount, as a percentage and as a fixed amount',
	{tag: ['@COMMERCE-11137', '@LPD-106621']},
	async ({apiHelpers, page, productDetailsPage}) => {
		test.setTimeout(300000);

		const discountFields = {
			active: true,
			level: 'L1',
			limitationType: 'unlimited',
			maximumDiscountAmount: 0,
			neverExpire: true,
			target: 'categories',
			title: `Discount ${getRandomString()}`,
		};

		const discount =
			await apiHelpers.headlessCommerceAdminPricing.postDiscount({
				...discountFields,
				percentageLevel1: 50,
				usePercentage: true,
			});

		await apiHelpers.headlessCommerceAdminPricing.postDiscountCategory(
			discount.id,
			brakeSystemCategoryId
		);

		await performUserSwitch(page, buyerUser.alternateName);

		const hosesURL = `/web${site.friendlyUrlPath}/p/${hoses.urls['en_US']}`;

		await test.step('A percentage discount takes half off every SKU', async () => {
			await page.goto(hosesURL);

			for (const [optionValue, listPrice, netPrice] of [
				['6', '$ 25.00', '$ 12.50'],
				['24', '$ 50.00', '$ 25.00'],
				['48', '$ 100.00', '$ 50.00'],
			]) {
				await productDetailsPage.selectOption(
					optionValue,
					'Package Quantity'
				);

				await expect(
					productDetailsPage.priceFragmentInactivePrice
				).toHaveText(listPrice);
				await expect(
					productDetailsPage.priceFragmentNetPrice
				).toHaveText(netPrice);
			}
		});

		await test.step('Switching the discount to a fixed amount takes the same amount off every SKU', async () => {
			await performLoginViaApi({page, screenName: 'test'});

			await apiHelpers.headlessCommerceAdminPricing.patchDiscount(
				discount.id,
				{
					...discountFields,
					percentageLevel1: 20,
					usePercentage: false,
				}
			);

			await performUserSwitch(page, buyerUser.alternateName);

			await page.goto(hosesURL);

			for (const [optionValue, listPrice, netPrice] of [
				['6', '$ 25.00', '$ 5.00'],
				['24', '$ 50.00', '$ 30.00'],
				['48', '$ 100.00', '$ 80.00'],
			]) {
				await productDetailsPage.selectOption(
					optionValue,
					'Package Quantity'
				);

				await expect(
					productDetailsPage.priceFragmentInactivePrice
				).toHaveText(listPrice);
				await expect(
					productDetailsPage.priceFragmentNetPrice
				).toHaveText(netPrice);
			}
		});
	}
);

test(
	'The price fragment renders a product group targeted discount, as a percentage and as a fixed amount',
	{tag: ['@COMMERCE-11137', '@LPD-106621']},
	async ({apiHelpers, page, productDetailsPage}) => {
		test.setTimeout(300000);

		const productGroup =
			await apiHelpers.headlessCommerceAdminCatalog.postProductGroup({
				products: [{productId: hoses.productId}],
				title: `PG ${getRandomString()}`,
			});

		const discountFields = {
			active: true,
			level: 'L1',
			limitationType: 'unlimited',
			maximumDiscountAmount: 0,
			neverExpire: true,
			target: 'product-groups',
			title: `Discount ${getRandomString()}`,
		};

		const discount =
			await apiHelpers.headlessCommerceAdminPricing.postDiscount({
				...discountFields,
				percentageLevel1: 50,
				usePercentage: true,
			});

		await apiHelpers.headlessCommerceAdminPricing.postDiscountProductGroup(
			discount.id,
			productGroup.id
		);

		await performUserSwitch(page, buyerUser.alternateName);

		await expectProductPrices(
			page,
			productDetailsPage,
			hoses,
			'Package Quantity',
			[
				['6', '$ 25.00', '$ 12.50'],
				['24', '$ 50.00', '$ 25.00'],
				['48', '$ 100.00', '$ 50.00'],
			]
		);

		await performLoginViaApi({page, screenName: 'test'});

		await apiHelpers.headlessCommerceAdminPricing.patchDiscount(
			discount.id,
			{...discountFields, percentageLevel1: 20, usePercentage: false}
		);

		await performUserSwitch(page, buyerUser.alternateName);

		await expectProductPrices(
			page,
			productDetailsPage,
			hoses,
			'Package Quantity',
			[
				['6', '$ 25.00', '$ 5.00'],
				['24', '$ 50.00', '$ 30.00'],
				['48', '$ 100.00', '$ 80.00'],
			]
		);
	}
);

test(
	'The price fragment renders a product targeted discount, as a percentage and as a fixed amount',
	{tag: ['@COMMERCE-11137', '@LPD-106621']},
	async ({apiHelpers, page, productDetailsPage}) => {
		test.setTimeout(300000);

		const discountFields = {
			active: true,
			discountProducts: [{productId: hoses.productId}],
			level: 'L1',
			limitationType: 'unlimited',
			maximumDiscountAmount: 0,
			neverExpire: true,
			target: 'products',
			title: `Discount ${getRandomString()}`,
		};

		const discount =
			await apiHelpers.headlessCommerceAdminPricing.postDiscount({
				...discountFields,
				percentageLevel1: 50,
				usePercentage: true,
			});

		await performUserSwitch(page, buyerUser.alternateName);

		await expectProductPrices(
			page,
			productDetailsPage,
			hoses,
			'Package Quantity',
			[
				['6', '$ 25.00', '$ 12.50'],
				['24', '$ 50.00', '$ 25.00'],
				['48', '$ 100.00', '$ 50.00'],
			]
		);

		await performLoginViaApi({page, screenName: 'test'});

		await apiHelpers.headlessCommerceAdminPricing.patchDiscount(
			discount.id,
			{...discountFields, percentageLevel1: 20, usePercentage: false}
		);

		await performUserSwitch(page, buyerUser.alternateName);

		await expectProductPrices(
			page,
			productDetailsPage,
			hoses,
			'Package Quantity',
			[
				['6', '$ 25.00', '$ 5.00'],
				['24', '$ 50.00', '$ 30.00'],
				['48', '$ 100.00', '$ 80.00'],
			]
		);
	}
);

test(
	'A lower maximum discount amount caps a percentage discount in the price fragment',
	{tag: ['@COMMERCE-11137', '@LPD-106621']},
	async ({apiHelpers, page, productDetailsPage}) => {
		test.setTimeout(300000);

		await apiHelpers.headlessCommerceAdminPricing.postDiscount({
			active: true,
			discountProducts: [{productId: hoses.productId}],
			level: 'L1',
			limitationType: 'unlimited',
			maximumDiscountAmount: 1,
			neverExpire: true,
			percentageLevel1: 20,
			target: 'products',
			title: `Discount ${getRandomString()}`,
			usePercentage: true,
		});

		await performUserSwitch(page, buyerUser.alternateName);

		await expectProductPrices(
			page,
			productDetailsPage,
			hoses,
			'Package Quantity',
			[
				['6', '$ 25.00', '$ 24.00'],
				['24', '$ 50.00', '$ 49.00'],
				['48', '$ 100.00', '$ 99.00'],
			]
		);
	}
);

test(
	'The price fragment renders a SKU targeted discount on that SKU alone, as a percentage and as a fixed amount',
	{tag: ['@COMMERCE-11137', '@LPD-106621']},
	async ({apiHelpers, page, productDetailsPage}) => {
		test.setTimeout(300000);

		const discountFields = {
			active: true,
			level: 'L1',
			limitationType: 'unlimited',
			maximumDiscountAmount: 0,
			neverExpire: true,
			target: 'skus',
			title: `Discount ${getRandomString()}`,
		};

		const discount =
			await apiHelpers.headlessCommerceAdminPricing.postDiscount({
				...discountFields,
				percentageLevel1: 50,
				usePercentage: true,
			});

		await apiHelpers.headlessCommerceAdminPricing.postDiscountSku(
			discount.id,
			{
				skuId: hoses.skus.find(
					(hosesSku: {sku: string}) => hosesSku.sku === 'MIN93022C'
				).id,
			}
		);

		await performUserSwitch(page, buyerUser.alternateName);

		await expectProductPrices(
			page,
			productDetailsPage,
			hoses,
			'Package Quantity',
			[
				['6', '$ 25.00', null],
				['24', '$ 50.00', null],
				['48', '$ 100.00', '$ 50.00'],
			]
		);

		await performLoginViaApi({page, screenName: 'test'});

		await apiHelpers.headlessCommerceAdminPricing.patchDiscount(
			discount.id,
			{...discountFields, percentageLevel1: 20, usePercentage: false}
		);

		await performUserSwitch(page, buyerUser.alternateName);

		await expectProductPrices(
			page,
			productDetailsPage,
			hoses,
			'Package Quantity',
			[
				['6', '$ 25.00', null],
				['24', '$ 50.00', null],
				['48', '$ 100.00', '$ 80.00'],
			]
		);
	}
);

test(
	'The price fragment renders a price list price, its product targeted modifier and the discount level overriding both',
	{tag: ['@COMMERCE-7617', '@LPD-106621']},
	async ({
		apiHelpers,
		commercePricingSystemSettingsPage,
		page,
		productDetailsPage,
	}) => {
		test.setTimeout(300000);

		const priceList =
			await apiHelpers.headlessCommerceAdminPricing.postPriceList({
				catalogId: catalog.id,
				currencyCode: 'USD',
				name: `Price List ${getRandomString()}`,
				type: 'price-list',
			});

		await apiHelpers.headlessCommerceAdminPricing.postPriceEntry({
			discountDiscovery: false,
			discountLevel1: 90,
			price: 100,
			priceListId: priceList.id,
			skuId: hosesSkuId('MIN93022C'),
		});

		await commercePricingSystemSettingsPage.setDisplayDiscountLevels(true);

		const priceModifier =
			await apiHelpers.headlessCommerceAdminPricing.postPriceModifier(
				priceList.id,
				{
					modifierAmount: -50,
					modifierType: 'percentage',
					target: 'products',
					title: `Price Modifier ${getRandomString()}`,
				}
			);

		await apiHelpers.headlessCommerceAdminPricing.postPriceModifierProduct(
			priceModifier.id,
			hoses.productId
		);

		await performUserSwitch(page, buyerUser.alternateName);

		await expectProductPrices(
			page,
			productDetailsPage,
			hoses,
			'Package Quantity',
			[
				['6', '$ 12.50', null],
				['24', '$ 25.00', null],
				['48', '$ 100.00', '$ 10.00'],
			]
		);

		await expect(
			productDetailsPage.priceFragmentDiscountLevels
		).toHaveCount(4);
		await expect(
			productDetailsPage.priceFragmentDiscountLevels.first()
		).toHaveText('90');

		await performLoginViaApi({page, screenName: 'test'});

		await commercePricingSystemSettingsPage.setDisplayDiscountLevels(false);
	}
);

test(
	'The price fragment compounds the four discount levels set on a price list entry',
	{tag: ['@COMMERCE-11141', '@LPD-106621']},
	async ({
		apiHelpers,
		commercePricingSystemSettingsPage,
		page,
		productDetailsPage,
	}) => {
		test.setTimeout(300000);

		const priceList =
			await apiHelpers.headlessCommerceAdminPricing.postPriceList({
				catalogId: catalog.id,
				currencyCode: 'USD',
				name: `Price List ${getRandomString()}`,
				type: 'price-list',
			});

		await apiHelpers.headlessCommerceAdminPricing.postPriceEntry({
			discountDiscovery: false,
			discountLevel1: 10,
			discountLevel2: 20,
			discountLevel3: 30,
			discountLevel4: 40,
			price: 100,
			priceListId: priceList.id,
			skuId: hosesSkuId('MIN93022C'),
		});

		await commercePricingSystemSettingsPage.setDisplayDiscountLevels(true);

		await performUserSwitch(page, buyerUser.alternateName);

		await expectProductPrices(
			page,
			productDetailsPage,
			hoses,
			'Package Quantity',
			[
				['6', '$ 25.00', null],
				['24', '$ 50.00', null],
				['48', '$ 100.00', '$ 30.24'],
			]
		);

		await expect(productDetailsPage.priceFragmentDiscountLevels).toHaveText(
			['10', '20', '30', '40']
		);

		await performLoginViaApi({page, screenName: 'test'});

		await commercePricingSystemSettingsPage.setDisplayDiscountLevels(false);
	}
);

for (const {bindPriceModifier, target, targetLabel} of [
	{
		bindPriceModifier: async () => {},
		target: 'catalog',
		targetLabel: 'catalog',
	},
	{
		bindPriceModifier: async (
			apiHelpers: DataApiHelpers,
			modifierId: number
		) =>
			apiHelpers.headlessCommerceAdminPricing.postPriceModifierCategory(
				modifierId,
				brakeSystemCategoryId
			),
		target: 'categories',
		targetLabel: 'category',
	},
	{
		bindPriceModifier: async (
			apiHelpers: DataApiHelpers,
			modifierId: number
		) => {
			const productGroup =
				await apiHelpers.headlessCommerceAdminCatalog.postProductGroup({
					products: [{productId: hoses.productId}],
					title: `PG ${getRandomString()}`,
				});

			await apiHelpers.headlessCommerceAdminPricing.postPriceModifierProductGroup(
				modifierId,
				productGroup.id
			);
		},
		target: 'product-groups',
		targetLabel: 'product group',
	},
	{
		bindPriceModifier: async (
			apiHelpers: DataApiHelpers,
			modifierId: number
		) =>
			apiHelpers.headlessCommerceAdminPricing.postPriceModifierProduct(
				modifierId,
				hoses.productId
			),
		target: 'products',
		targetLabel: 'product',
	},
]) {
	test(
		`The price fragment follows a price list ${targetLabel} targeted modifier through percentage, replace and fixed amount`,
		{tag: ['@COMMERCE-11141', '@LPD-106621']},
		async ({apiHelpers, page, productDetailsPage}) => {
			test.setTimeout(300000);

			const priceList =
				await apiHelpers.headlessCommerceAdminPricing.postPriceList({
					catalogId: catalog.id,
					currencyCode: 'USD',
					name: `Price List ${getRandomString()}`,
					type: 'price-list',
				});

			const modifierFields = {
				active: true,
				priceListId: priceList.id,
				priority: 0,
				target,
				title: `Price Modifier ${getRandomString()}`,
			};

			const priceModifier =
				await apiHelpers.headlessCommerceAdminPricing.postPriceModifier(
					priceList.id,
					{
						...modifierFields,
						modifierAmount: 50,
						modifierType: 'percentage',
					}
				);

			await bindPriceModifier(apiHelpers, priceModifier.id);

			await performUserSwitch(page, buyerUser.alternateName);

			await expectProductPrices(
				page,
				productDetailsPage,
				hoses,
				'Package Quantity',
				[
					['6', '$ 37.50', null],
					['24', '$ 75.00', null],
					['48', '$ 150.00', null],
				]
			);

			for (const [modifierType, prices] of [
				['replace', ['$ 50.00', '$ 50.00', '$ 50.00']],
				['fixed-amount', ['$ 75.00', '$ 100.00', '$ 150.00']],
			] as Array<[string, string[]]>) {
				await performLoginViaApi({page, screenName: 'test'});

				await apiHelpers.headlessCommerceAdminPricing.patchPriceModifier(
					priceModifier.id,
					{...modifierFields, modifierAmount: 50, modifierType}
				);

				await performUserSwitch(page, buyerUser.alternateName);

				await expectProductPrices(
					page,
					productDetailsPage,
					hoses,
					'Package Quantity',
					[
						['6', prices[0], null],
						['24', prices[1], null],
						['48', prices[2], null],
					]
				);
			}
		}
	);
}

for (const {bindPriceModifier, target, targetLabel, tickets} of [
	{
		bindPriceModifier: async (
			apiHelpers: DataApiHelpers,
			modifierId: number
		) =>
			apiHelpers.headlessCommerceAdminPricing.postPriceModifierProduct(
				modifierId,
				hoses.productId
			),
		target: 'products',
		targetLabel: 'product',

		tickets: ['@COMMERCE-7617', '@COMMERCE-11142'],
	},
	{
		bindPriceModifier: async () => {},
		target: 'catalog',
		targetLabel: 'catalog',
		tickets: ['@COMMERCE-11142'],
	},
	{
		bindPriceModifier: async (
			apiHelpers: DataApiHelpers,
			modifierId: number
		) =>
			apiHelpers.headlessCommerceAdminPricing.postPriceModifierCategory(
				modifierId,
				brakeSystemCategoryId
			),
		target: 'categories',
		targetLabel: 'category',
		tickets: ['@COMMERCE-11142'],
	},
	{
		bindPriceModifier: async (
			apiHelpers: DataApiHelpers,
			modifierId: number
		) => {
			const productGroup =
				await apiHelpers.headlessCommerceAdminCatalog.postProductGroup({
					products: [{productId: hoses.productId}],
					title: `PG ${getRandomString()}`,
				});

			await apiHelpers.headlessCommerceAdminPricing.postPriceModifierProductGroup(
				modifierId,
				productGroup.id
			);
		},
		target: 'product-groups',
		targetLabel: 'product group',
		tickets: ['@COMMERCE-11142'],
	},
]) {
	test(
		`The price fragment follows a promotion ${targetLabel} targeted modifier through percentage, replace and fixed amount`,
		{tag: [...tickets, '@LPD-106621']},
		async ({apiHelpers, page, productDetailsPage}) => {
			test.setTimeout(300000);

			const promotion =
				await apiHelpers.headlessCommerceAdminPricing.postPriceList({
					catalogId: catalog.id,
					currencyCode: 'USD',
					name: `Promotion ${getRandomString()}`,
					type: 'promotion',
				});

			const modifierFields = {
				active: true,
				priceListId: promotion.id,
				priority: 0,
				target,
				title: `Price Modifier ${getRandomString()}`,
			};

			const priceModifier =
				await apiHelpers.headlessCommerceAdminPricing.postPriceModifier(
					promotion.id,
					{
						...modifierFields,
						modifierAmount: -50,
						modifierType: 'percentage',
					}
				);

			await bindPriceModifier(apiHelpers, priceModifier.id);

			await performUserSwitch(page, buyerUser.alternateName);

			await expectProductPrices(
				page,
				productDetailsPage,
				hoses,
				'Package Quantity',
				[
					['6', '$ 25.00', '$ 12.50'],
					['24', '$ 50.00', '$ 25.00'],
					['48', '$ 100.00', '$ 50.00'],
				],
				'promo'
			);

			for (const [modifierAmount, modifierType, promoPrices] of [
				[20, 'replace', ['$ 20.00', '$ 20.00', '$ 20.00']],
				[-20, 'fixed-amount', ['$ 5.00', '$ 30.00', '$ 80.00']],
			] as Array<[number, string, string[]]>) {
				await performLoginViaApi({page, screenName: 'test'});

				await apiHelpers.headlessCommerceAdminPricing.patchPriceModifier(
					priceModifier.id,
					{...modifierFields, modifierAmount, modifierType}
				);

				await performUserSwitch(page, buyerUser.alternateName);

				await expectProductPrices(
					page,
					productDetailsPage,
					hoses,
					'Package Quantity',
					[
						['6', '$ 25.00', promoPrices[0]],
						['24', '$ 50.00', promoPrices[1]],
						['48', '$ 100.00', promoPrices[2]],
					],
					'promo'
				);
			}
		}
	);
}

test(
	'The price fragment compounds the four discount levels set on a promotion entry',
	{tag: ['@COMMERCE-11142', '@LPD-106621']},
	async ({
		apiHelpers,
		commercePricingSystemSettingsPage,
		page,
		productDetailsPage,
	}) => {
		test.setTimeout(300000);

		const promotion =
			await apiHelpers.headlessCommerceAdminPricing.postPriceList({
				catalogId: catalog.id,
				currencyCode: 'USD',
				name: `Promotion ${getRandomString()}`,
				type: 'promotion',
			});

		await apiHelpers.headlessCommerceAdminPricing.postPriceEntry({
			discountDiscovery: false,
			discountLevel1: 10,
			discountLevel2: 20,
			discountLevel3: 30,
			discountLevel4: 40,
			price: 90,
			priceListId: promotion.id,
			skuId: hosesSkuId('MIN93022C'),
		});

		await commercePricingSystemSettingsPage.setDisplayDiscountLevels(true);

		await performUserSwitch(page, buyerUser.alternateName);

		await page.goto(`/web${site.friendlyUrlPath}/p/${hoses.urls['en_US']}`);

		for (const optionValue of ['6', '24']) {
			await productDetailsPage.selectOption(
				optionValue,
				'Package Quantity'
			);

			await expect(productDetailsPage.priceFragmentNetPrice).toHaveCount(
				0
			);
		}

		await productDetailsPage.selectOption('48', 'Package Quantity');

		await expect(productDetailsPage.priceFragmentInactivePrice).toHaveText(
			'$ 100.00'
		);
		await expect(
			productDetailsPage.priceFragmentPromoInactivePrice
		).toHaveText('$ 90.00');
		await expect(productDetailsPage.priceFragmentNetPrice).toHaveText(
			'$ 27.22'
		);
		await expect(productDetailsPage.priceFragmentDiscountLevels).toHaveText(
			['10', '20', '30', '40']
		);

		await performLoginViaApi({page, screenName: 'test'});

		await commercePricingSystemSettingsPage.setDisplayDiscountLevels(false);
	}
);

test(
	'The price fragment shows the Price on Application label for a SKU priced on application',
	{tag: ['@COMMERCE-11635', '@LPD-106621']},
	async ({apiHelpers, page, productDetailsPage}) => {
		test.setTimeout(300000);

		const uJoint =
			await apiHelpers.headlessCommerceAdminCatalog.getProductByName(
				'U-Joint',
				{catalogId: catalog.id, nestedFields: 'skus'}
			);

		const priceEntry = (
			await apiHelpers.headlessCommerceAdminPricing.getPriceListEntries(
				basePriceListId
			)
		).items.find(
			(entry: {skuId: number}) => entry.skuId === uJoint.skus[0].id
		);

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
				productDetailsPage.priceFragmentPriceOnApplicationLabel
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
