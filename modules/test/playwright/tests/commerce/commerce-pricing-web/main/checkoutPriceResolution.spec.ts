/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {commercePagesTest} from '../../../../fixtures/commercePagesTest';
import {dataApiHelpersTest} from '../../../../fixtures/dataApiHelpersTest';
import {loginTest} from '../../../../fixtures/loginTest';
import {DataApiHelpers} from '../../../../helpers/ApiHelpers';
import {CheckoutPage} from '../../../../pages/commerce/commerce-checkout-web/checkoutPage';
import {
	performLoginViaApi,
	performUserSwitchViaApi,
} from '../../../../utils/performLogin';
import {createAccountWithBuyerUser, miniumSetUp} from '../../utils/commerce';

export const test = mergeTests(
	commercePagesTest,
	dataApiHelpersTest,
	loginTest()
);

type TProductFixture = {
	name: string;
	priceEntryId: number;
	skuId: number;
	skuName: string;
};

type TPriceConfiguration = {
	bulkPricing?: boolean;
	price: number;
	tiers?: TTier[];
};

type TTier = {minimumQuantity: number; price: number};

let absSensor: TProductFixture;
let basePromoPriceList: {id: number};
let channel: {id: number};
let setupData: Array<{id: number | string; type: string}>;
let site: Site;
let uJoint: TProductFixture;

test.beforeAll(async ({browser}) => {
	const page = await browser.newPage();

	await performLoginViaApi({page, screenName: 'test'});

	const apiHelpers = new DataApiHelpers(page);

	const miniumResult = await miniumSetUp(apiHelpers);

	channel = miniumResult.channel;
	site = miniumResult.site;

	const [basePriceLists, basePromoPriceLists] = await Promise.all([
		apiHelpers.headlessCommerceAdminPricing.getBasePriceList(
			miniumResult.catalog.id
		),
		apiHelpers.headlessCommerceAdminPricing.getBasePromoPriceList(
			miniumResult.catalog.id
		),
	]);

	basePromoPriceList = basePromoPriceLists.items[0];

	const priceEntries =
		await apiHelpers.headlessCommerceAdminPricing.getPriceListEntries(
			basePriceLists.items[0].id
		);

	async function getProductFixture(productName: string) {
		const sku = (
			await apiHelpers.headlessCommerceAdminCatalog.getProductByName(
				productName
			)
		).skus[0];

		const priceEntry = priceEntries.items.find(
			(priceEntry: {skuId: number; unitOfMeasureKey: string}) =>
				priceEntry.skuId === sku.id && !priceEntry.unitOfMeasureKey
		);

		return {
			name: productName,
			priceEntryId: priceEntry.priceEntryId,
			skuId: sku.id,
			skuName: sku.sku,
		};
	}

	[absSensor, uJoint] = await Promise.all([
		getProductFixture('ABS Sensor'),
		getProductFixture('U-Joint'),
	]);

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

async function checkoutAndAssertPrice(
	checkoutPage: CheckoutPage,
	{
		absentUnitPrice,
		buyerName,
		listUnitPrice,
		productName,
		quantity,
		unitPrice,
	}: {
		absentUnitPrice?: number;
		buyerName: string;
		listUnitPrice?: number;
		productName: string;
		quantity: number;
		unitPrice: number;
	}
) {
	const subtotal = unitPrice * quantity;

	await checkoutPage.addAddress({
		city: 'Test City',
		countryLabel: 'United States',
		name: buyerName,
		regionLabel: 'Florida',
		street: 'Test Address',
		zip: '12345',
	});

	await checkoutPage.continueButton.click();

	await checkoutPage.shippingMethodRadio('Standard').check();

	await checkoutPage.continueButton.click();

	await expect(checkoutPage.activeCheckoutStep).toHaveText('Order Summary');
	await expect(
		checkoutPage.orderSummaryItemCell(productName, 'quantity')
	).toContainText(String(quantity));
	await expect(
		checkoutPage.orderSummaryItemListPrice(productName)
	).toHaveText(formatPrice(listUnitPrice ?? unitPrice));
	await expect(
		checkoutPage.orderSummaryItemCell(productName, 'total')
	).toHaveText(formatPrice(subtotal));

	if (listUnitPrice === undefined) {
		await expect(
			checkoutPage.orderSummaryItemPromoPrice(productName)
		).toBeHidden();
	}
	else {
		await expect(
			checkoutPage.orderSummaryItemPromoPrice(productName)
		).toHaveText(formatPrice(unitPrice));
	}

	if (absentUnitPrice !== undefined) {
		await expect(checkoutPage.orderItemsTableLocator).not.toContainText(
			formatPrice(absentUnitPrice)
		);
	}

	await expect(checkoutPage.orderSummarySubtotal).toHaveText(
		formatPrice(subtotal)
	);
	await expect(checkoutPage.orderSummaryDelivery).toHaveText(formatPrice(15));
	await expect(checkoutPage.orderSummaryTotal).toHaveText(
		formatPrice(subtotal + 15)
	);

	for (const address of [
		checkoutPage.commerceBillingAddress,
		checkoutPage.commerceShippingAddress,
	]) {
		for (const line of [
			buyerName,
			'Test Address',
			'Test City',
			'United States',
		]) {
			await expect(address).toContainText(line);
		}
	}

	await checkoutPage.continueButton.click();

	await expect(checkoutPage.orderSuccessMessage).toBeVisible();
}

async function configurePriceEntry(
	apiHelpers: DataApiHelpers,
	priceEntryId: number,
	{bulkPricing = false, price, tiers = []}: TPriceConfiguration
) {
	await apiHelpers.headlessCommerceAdminPricing.patchPriceEntry(
		priceEntryId,
		{bulkPricing, price}
	);

	for (const tier of tiers) {
		await apiHelpers.headlessCommerceAdminPricing.postTierPrice(
			priceEntryId,
			tier
		);
	}
}

async function configurePromoPriceEntry(
	apiHelpers: DataApiHelpers,
	product: TProductFixture,
	priceConfiguration: TPriceConfiguration
) {
	const promoPriceEntry =
		await apiHelpers.headlessCommerceAdminPricing.postPriceEntry({
			price: priceConfiguration.price,
			priceListId: basePromoPriceList.id,
			skuId: product.skuId,
		});

	await configurePriceEntry(
		apiHelpers,
		promoPriceEntry.priceEntryId,
		priceConfiguration
	);
}

function formatPrice(price: number) {
	return `$ ${price.toFixed(2)}`;
}

function resolveUnitPrice(
	{bulkPricing = false, price, tiers = []}: TPriceConfiguration,
	quantity: number
) {
	const applicableTiers = tiers
		.filter((tier) => tier.minimumQuantity <= quantity)
		.sort((a, b) => a.minimumQuantity - b.minimumQuantity);

	if (!applicableTiers.length) {
		return price;
	}

	if (bulkPricing) {
		return applicableTiers[applicableTiers.length - 1].price;
	}

	let coveredQuantity = 0;
	let currentPrice = price;
	let total = 0;

	for (const tier of applicableTiers) {
		total += currentPrice * (tier.minimumQuantity - coveredQuantity - 1);
		coveredQuantity = tier.minimumQuantity - 1;
		currentPrice = tier.price;
	}

	return (total + currentPrice * (quantity - coveredQuantity)) / quantity;
}

test(
	'List price applies at checkout when the promotion price is higher',
	{tag: ['@COMMERCE-10279', '@LPD-106099']},
	async ({
		apiHelpers,
		checkoutPage,
		commerceMiniCartPage,
		commerceThemeMiniumCatalogPage,
		page,
	}) => {
		await configurePriceEntry(apiHelpers, uJoint.priceEntryId, {price: 24});

		await apiHelpers.headlessCommerceAdminPricing.postPriceEntry({
			price: 40,
			priceListId: basePromoPriceList.id,
			skuId: uJoint.skuId,
		});

		const {account, buyerUser} = await createAccountWithBuyerUser(
			apiHelpers,
			site.id
		);

		await apiHelpers.headlessCommerceDeliveryCart.postCart(
			{
				accountId: account.id,
				cartItems: [{options: '[]', quantity: 3, skuId: uJoint.skuId}],
			},
			channel.id
		);

		await performUserSwitchViaApi(page, buyerUser.alternateName);

		await page.goto(`/web${site.friendlyUrlPath}`);

		await expect(
			commerceThemeMiniumCatalogPage.productCardPrice(
				uJoint.name,
				formatPrice(24)
			)
		).toBeVisible();

		await commerceMiniCartPage.miniCartButton.click();
		await commerceMiniCartPage.submitButton.click();

		await checkoutAndAssertPrice(checkoutPage, {
			absentUnitPrice: 40,
			buyerName: buyerUser.alternateName,
			productName: uJoint.name,
			quantity: 3,
			unitPrice: 24,
		});
	}
);

test(
	'Promotion price applies at checkout when it is lower than the list price',
	{tag: ['@COMMERCE-10280', '@LPD-106099']},
	async ({apiHelpers, checkoutPage, commerceMiniCartPage, page}) => {
		await configurePriceEntry(apiHelpers, uJoint.priceEntryId, {price: 24});

		await apiHelpers.headlessCommerceAdminPricing.postPriceEntry({
			price: 14,
			priceListId: basePromoPriceList.id,
			skuId: uJoint.skuId,
		});

		const {account, buyerUser} = await createAccountWithBuyerUser(
			apiHelpers,
			site.id
		);

		await apiHelpers.headlessCommerceDeliveryCart.postCart(
			{
				accountId: account.id,
				cartItems: [{options: '[]', quantity: 2, skuId: uJoint.skuId}],
			},
			channel.id
		);

		await performUserSwitchViaApi(page, buyerUser.alternateName);

		await page.goto(`/web${site.friendlyUrlPath}`);

		await commerceMiniCartPage.miniCartButton.click();

		await expect(
			commerceMiniCartPage.miniCartSku(uJoint.skuName)
		).toBeVisible();
		await expect(
			commerceMiniCartPage.miniCartItemListPrice(uJoint.name)
		).toHaveText(formatPrice(24));
		await expect(
			commerceMiniCartPage.miniCartItemPromoPrice(uJoint.name)
		).toHaveText(formatPrice(14));

		await commerceMiniCartPage.submitButton.click();

		await checkoutAndAssertPrice(checkoutPage, {
			buyerName: buyerUser.alternateName,
			listUnitPrice: 24,
			productName: uJoint.name,
			quantity: 2,
			unitPrice: 14,
		});
	}
);

test(
	'Bulk price applies at checkout',
	{tag: ['@COMMERCE-10245', '@LPD-106099']},
	async ({apiHelpers, checkoutPage, commerceMiniCartPage, page}) => {
		await configurePriceEntry(apiHelpers, uJoint.priceEntryId, {
			bulkPricing: true,
			price: 24,
			tiers: [{minimumQuantity: 7, price: 50}],
		});

		const {account, buyerUser} = await createAccountWithBuyerUser(
			apiHelpers,
			site.id
		);

		await apiHelpers.headlessCommerceDeliveryCart.postCart(
			{
				accountId: account.id,
				cartItems: [{options: '[]', quantity: 7, skuId: uJoint.skuId}],
			},
			channel.id
		);

		await performUserSwitchViaApi(page, buyerUser.alternateName);

		await page.goto(`/web${site.friendlyUrlPath}`);

		await commerceMiniCartPage.miniCartButton.click();
		await commerceMiniCartPage.submitButton.click();

		await checkoutAndAssertPrice(checkoutPage, {
			buyerName: buyerUser.alternateName,
			productName: uJoint.name,
			quantity: 7,
			unitPrice: 50,
		});
	}
);

test(
	'Tiered price applies at checkout',
	{tag: ['@COMMERCE-12443', '@LPD-106099']},
	async ({apiHelpers, checkoutPage, commerceMiniCartPage, page}) => {
		await configurePriceEntry(apiHelpers, absSensor.priceEntryId, {
			price: 50,
			tiers: [{minimumQuantity: 5, price: 20}],
		});

		const {account, buyerUser} = await createAccountWithBuyerUser(
			apiHelpers,
			site.id
		);

		await apiHelpers.headlessCommerceDeliveryCart.postCart(
			{
				accountId: account.id,
				cartItems: [
					{options: '[]', quantity: 5, skuId: absSensor.skuId},
				],
			},
			channel.id
		);

		await performUserSwitchViaApi(page, buyerUser.alternateName);

		await page.goto(`/web${site.friendlyUrlPath}`);

		await commerceMiniCartPage.miniCartButton.click();
		await commerceMiniCartPage.submitButton.click();

		await checkoutAndAssertPrice(checkoutPage, {
			buyerName: buyerUser.alternateName,
			productName: absSensor.name,
			quantity: 5,
			unitPrice: (4 * 50 + 20) / 5,
		});
	}
);

for (const {bulkPricing, initialUnitPrice, title, unitPrice} of [
	{
		bulkPricing: true,
		initialUnitPrice: 50,
		title: 'The closest bulk tier applies as the quantity changes in the mini cart',
		unitPrice: 5,
	},
	{
		bulkPricing: false,
		initialUnitPrice: (4 * 24 + 50) / 5,
		title: 'Tiered prices accumulate across tiers as the quantity changes in the mini cart',
		unitPrice: (4 * 24 + 5 * 50 + 5) / 10,
	},
]) {
	test(
		title,
		{tag: ['@COMMERCE-12402', '@LPD-106099']},
		async ({
			apiHelpers,
			checkoutPage,
			commerceMiniCartPage,
			commerceThemeMiniumCatalogPage,
			page,
		}) => {
			await configurePriceEntry(apiHelpers, uJoint.priceEntryId, {
				bulkPricing,
				price: 24,
				tiers: [
					{minimumQuantity: 5, price: 50},
					{minimumQuantity: 10, price: 5},
				],
			});

			const {account, buyerUser} = await createAccountWithBuyerUser(
				apiHelpers,
				site.id
			);

			await apiHelpers.headlessCommerceDeliveryCart.postCart(
				{
					accountId: account.id,
					cartItems: [
						{options: '[]', quantity: 5, skuId: uJoint.skuId},
					],
				},
				channel.id
			);

			await performUserSwitchViaApi(page, buyerUser.alternateName);

			await page.goto(`/web${site.friendlyUrlPath}`);

			await commerceMiniCartPage.miniCartButton.click();

			const quantitySelector =
				commerceThemeMiniumCatalogPage.quantitySelector(
					commerceMiniCartPage.miniCartItem(uJoint.name)
				);

			await expect(
				commerceMiniCartPage.miniCartSku(uJoint.skuName)
			).toBeVisible();
			await expect(quantitySelector).toHaveValue('5');
			await expect(
				commerceMiniCartPage.miniCartItemListPrice(uJoint.name)
			).toHaveText(formatPrice(initialUnitPrice));
			await expect(
				commerceMiniCartPage.miniCartSummaryItem('Subtotal')
			).toHaveText(formatPrice(initialUnitPrice * 5));

			await quantitySelector.fill('10');

			await expect(async () => {
				await expect(quantitySelector).toHaveValue('10', {
					timeout: 2000,
				});
				await expect(
					commerceMiniCartPage.miniCartItemListPrice(uJoint.name)
				).toHaveText(formatPrice(unitPrice), {timeout: 2000});
				await expect(
					commerceMiniCartPage.miniCartSummaryItem('Subtotal')
				).toHaveText(formatPrice(unitPrice * 10), {timeout: 2000});
			}).toPass({timeout: 30000});

			await commerceMiniCartPage.submitButton.click();

			await checkoutAndAssertPrice(checkoutPage, {
				buyerName: buyerUser.alternateName,
				productName: uJoint.name,
				quantity: 10,
				unitPrice,
			});
		}
	);
}

for (const {
	listPriceConfiguration,
	promoPriceConfiguration,
	quantity,
	tag,
	title,
} of [
	{
		listPriceConfiguration: {
			bulkPricing: true,
			price: 24,
			tiers: [{minimumQuantity: 1, price: 40}],
		},
		promoPriceConfiguration: {
			price: 45,
			tiers: [{minimumQuantity: 4, price: 10}],
		},
		quantity: 4,
		tag: '@COMMERCE-10290',
		title: 'Tiered promotion price applies at checkout over a bulk price list price',
	},
	{
		listPriceConfiguration: {price: 24},
		promoPriceConfiguration: {
			bulkPricing: true,
			price: 30,
			tiers: [{minimumQuantity: 5, price: 15}],
		},
		quantity: 5,
		tag: '@COMMERCE-10282',
		title: 'Bulk promotion price applies at checkout when only the promotion has tiers',
	},
	{
		listPriceConfiguration: {
			bulkPricing: true,
			price: 24,
			tiers: [{minimumQuantity: 1, price: 50}],
		},
		promoPriceConfiguration: {
			bulkPricing: true,
			price: 55,
			tiers: [{minimumQuantity: 5, price: 30}],
		},
		quantity: 5,
		tag: '@COMMERCE-10307',
		title: 'Bulk promotion price applies at checkout over a bulk price list price',
	},
	{
		listPriceConfiguration: {
			price: 50,
			tiers: [{minimumQuantity: 5, price: 40}],
		},
		promoPriceConfiguration: {
			bulkPricing: true,
			price: 55,
			tiers: [{minimumQuantity: 5, price: 30}],
		},
		quantity: 5,
		tag: '@COMMERCE-10306',
		title: 'Bulk promotion price applies at checkout over a tiered price list price',
	},
	{
		listPriceConfiguration: {
			price: 50,
			tiers: [{minimumQuantity: 5, price: 40}],
		},
		promoPriceConfiguration: {
			price: 55,
			tiers: [{minimumQuantity: 5, price: 10}],
		},
		quantity: 5,
		tag: '@COMMERCE-10289',
		title: 'Tiered promotion price applies at checkout over a tiered price list price',
	},
	{
		listPriceConfiguration: {price: 24},
		promoPriceConfiguration: {
			price: 25,
			tiers: [{minimumQuantity: 5, price: 10}],
		},
		quantity: 5,
		tag: '@COMMERCE-10281',
		title: 'Tiered promotion price applies at checkout when only the promotion has tiers',
	},
]) {
	test(
		title,
		{tag: [tag, '@LPD-106247']},
		async ({
			apiHelpers,
			checkoutPage,
			commerceMiniCartPage,
			commerceThemeMiniumCatalogPage,
			page,
		}) => {
			await configurePriceEntry(
				apiHelpers,
				uJoint.priceEntryId,
				listPriceConfiguration
			);
			await configurePromoPriceEntry(
				apiHelpers,
				uJoint,
				promoPriceConfiguration
			);

			const {account, buyerUser} = await createAccountWithBuyerUser(
				apiHelpers,
				site.id
			);

			await apiHelpers.headlessCommerceDeliveryCart.postCart(
				{
					accountId: account.id,
					cartItems: [{options: '[]', quantity, skuId: uJoint.skuId}],
				},
				channel.id
			);

			await performUserSwitchViaApi(page, buyerUser.alternateName);

			await page.goto(`/web${site.friendlyUrlPath}`);

			await expect(
				commerceThemeMiniumCatalogPage.productCardPrice(
					uJoint.name,
					formatPrice(resolveUnitPrice(listPriceConfiguration, 1))
				)
			).toBeVisible();

			await commerceMiniCartPage.miniCartButton.click();
			await commerceMiniCartPage.submitButton.click();

			await checkoutAndAssertPrice(checkoutPage, {
				buyerName: buyerUser.alternateName,
				listUnitPrice: resolveUnitPrice(
					listPriceConfiguration,
					quantity
				),
				productName: uJoint.name,
				quantity,
				unitPrice: resolveUnitPrice(promoPriceConfiguration, quantity),
			});
		}
	);
}
