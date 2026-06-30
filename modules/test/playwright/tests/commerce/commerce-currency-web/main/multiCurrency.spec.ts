/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {commercePagesTest} from '../../../../fixtures/commercePagesTest';
import {dataApiHelpersTest} from '../../../../fixtures/dataApiHelpersTest';
import {loginTest} from '../../../../fixtures/loginTest';
import getRandomString from '../../../../utils/getRandomString';
import {
	performLoginViaApi,
	performLogout,
} from '../../../../utils/performLogin';
import {
	classicCommerceSetUp,
	configureBuyerUserForSite,
} from '../../utils/commerce';

export const test = mergeTests(
	commercePagesTest,
	dataApiHelpersTest,
	loginTest()
);

test(
	'Buyer can change currency with currency selector and prices are converted accordingly',
	{tag: ['@LPD-48196']},
	async ({
		apiHelpers,
		commerceAdminChannelDetailsCurrenciesPage,
		commerceAdminChannelDetailsPage,
		commerceAdminChannelsPage,
		commerceThemeClassicCatalogPage,
		page,
	}) => {
		test.setTimeout(90000);

		const {channel, site} = await classicCommerceSetUp(apiHelpers);

		const account = await apiHelpers.headlessAdminUser.postAccount({
			name: getRandomString(),
			type: 'business',
		});

		await configureBuyerUserForSite(
			account,
			apiHelpers,
			site,
			'demo.unprivileged@liferay.com'
		);

		const currencies =
			await apiHelpers.headlessCommerceAdminCatalog.getCurrenciesPage('');

		const currency1 = currencies.items.find(
			(item) => item.name['en_US'] === 'US Dollar'
		);
		const currency2 = currencies.items.find(
			(item) => item.name['en_US'] === 'Australian Dollar'
		);
		const currency3 = currencies.items.find(
			(item) => item.name['en_US'] === 'Euro'
		);

		await test.step('Add three currencies to the channel as admin', async () => {
			await commerceAdminChannelsPage.goto();

			await (
				await commerceAdminChannelsPage.channelsTableRowLink(
					channel.name
				)
			).click();

			await commerceAdminChannelDetailsPage.goToCurrencies();

			await commerceAdminChannelDetailsCurrenciesPage.addCurrencies([
				currency1.name['en_US'],
				currency2.name['en_US'],
				currency3.name['en_US'],
			]);
		});

		await test.step('Switch currency from the storefront selector as the buyer', async () => {
			await performLogout(page);
			await performLoginViaApi({page, screenName: 'demo.unprivileged'});

			await page.goto(`/web/${site.name}`);

			await commerceThemeClassicCatalogPage
				.currencySelectorButton(currency1.code, currency1.symbol)
				.click();

			await expect(
				commerceThemeClassicCatalogPage.currencyListItem(currency2.code)
			).toBeVisible();
			await expect(
				commerceThemeClassicCatalogPage.currencyListItem(currency3.code)
			).toBeVisible();

			await commerceThemeClassicCatalogPage
				.currencyListItem(currency3.code)
				.click();
			await commerceThemeClassicCatalogPage
				.currencySelectorButton(currency3.code, currency3.symbol)
				.click();

			expect(
				await commerceThemeClassicCatalogPage.firstCardItem.innerText()
			).toContain(`${currency3.symbol}`);
		});

		await test.step('Remove the Australian Dollar from the channel as admin', async () => {
			await performLogout(page);
			await performLoginViaApi({page, screenName: 'test'});

			await commerceAdminChannelsPage.goto();

			await (
				await commerceAdminChannelsPage.channelsTableRowLink(
					channel.name
				)
			).click();

			await commerceAdminChannelDetailsPage.goToCurrencies();

			await expect(
				commerceAdminChannelDetailsCurrenciesPage.currenciesTable
			).toHaveCount(1);

			await (
				await commerceAdminChannelDetailsCurrenciesPage.currenciesTableRowAction(
					currency2.name['en_US'],
					'Remove'
				)
			).click();

			await page.reload();

			expect(
				await commerceAdminChannelDetailsCurrenciesPage.currenciesTableRows()
			).toHaveLength(2);
		});

		await test.step('Confirm the removed currency is no longer offered to the buyer', async () => {
			await performLogout(page);
			await performLoginViaApi({page, screenName: 'demo.unprivileged'});

			await page.goto(`/web/${site.name}`);

			expect(
				await commerceThemeClassicCatalogPage.firstCardItem.innerText()
			).toContain(`${currency3.symbol}`);

			await commerceThemeClassicCatalogPage
				.currencySelectorButton(currency3.code, currency3.symbol)
				.click();

			await expect(
				commerceThemeClassicCatalogPage.currencyListItem(currency1.code)
			).toBeVisible();
		});
	}
);

test(
	'Buyer can change currency with active order and product price with price list are converted accordingly',
	{tag: ['@LPD-48196']},
	async ({
		apiHelpers,
		commerceAdminChannelDetailsCurrenciesPage,
		commerceAdminChannelDetailsPage,
		commerceAdminChannelsPage,
		commerceMiniCartPage,
		commerceThemeClassicCatalogPage,
		commerceThemeClassicOrdersPage,
		page,
		productDetailsPage,
	}) => {
		test.setTimeout(90000);

		const {catalog, channel, site} = await classicCommerceSetUp(apiHelpers);

		const account = await apiHelpers.headlessAdminUser.postAccount({
			name: getRandomString(),
			type: 'business',
		});

		await configureBuyerUserForSite(
			account,
			apiHelpers,
			site,
			'demo.unprivileged@liferay.com'
		);

		const currencies =
			await apiHelpers.headlessCommerceAdminCatalog.getCurrenciesPage('');

		const currency1 = currencies.items.find(
			(item) => item.name['en_US'] === 'US Dollar'
		);
		const currency2 = currencies.items.find(
			(item) => item.name['en_US'] === 'Australian Dollar'
		);
		const currency3 = currencies.items.find(
			(item) => item.name['en_US'] === 'Euro'
		);

		const product = (
			await apiHelpers.headlessCommerceAdminCatalog.getProducts(
				new URLSearchParams({
					filter: `name eq 'Wear Sensors'`,
					nestedFields: `skus`,
				})
			)
		).items[0];

		const productSku = await (
			await apiHelpers.headlessCommerceDeliveryCatalog.getChannelProductSkusPage(
				channel.id,
				product.productId,
				new URLSearchParams({
					nestedFields: `price`,
				})
			)
		).items[0];

		const priceList =
			await apiHelpers.headlessCommerceAdminPricing.postPriceList({
				catalogId: catalog.id,
				currencyCode: currency3.code,
				name: getRandomString(),
				type: 'price-list',
			});

		const priceEntry =
			await apiHelpers.headlessCommerceAdminPricing.postPriceEntry({
				price: 100,
				priceListId: priceList.id,
				skuId: productSku.id,
			});

		const cart = await apiHelpers.headlessCommerceDeliveryCart.postCart(
			{
				accountId: account.id,
				cartItems: [
					{
						quantity: 1,
						skuId: productSku.id,
					},
				],
			},
			channel.id
		);

		await test.step('Add three currencies to the channel as admin', async () => {
			await commerceAdminChannelsPage.goto();

			await (
				await commerceAdminChannelsPage.channelsTableRowLink(
					channel.name
				)
			).click();

			await commerceAdminChannelDetailsPage.goToCurrencies();

			await commerceAdminChannelDetailsCurrenciesPage.addCurrencies([
				currency1.name['en_US'],
				currency2.name['en_US'],
				currency3.name['en_US'],
			]);
		});

		await test.step('Select Euro from the storefront currency selector as the buyer', async () => {
			await performLogout(page);
			await performLoginViaApi({page, screenName: 'demo.unprivileged'});

			await page.goto(`/web/${site.name}`);

			await commerceThemeClassicCatalogPage
				.currencySelectorButton(currency1.code, currency1.symbol)
				.click();

			await expect(
				commerceThemeClassicCatalogPage.currencyListItem(currency2.code)
			).toBeVisible();
			await expect(
				commerceThemeClassicCatalogPage.currencyListItem(currency3.code)
			).toBeVisible();

			await commerceThemeClassicCatalogPage
				.currencyListItem(currency3.code)
				.click();
		});

		try {
			await test.step('Confirm the currency change with the active order and verify base prices are converted', async () => {
				await expect(
					commerceThemeClassicCatalogPage.changeCurrencyModalHeading
				).toBeVisible();

				await expect(async () => {
					await commerceThemeClassicCatalogPage.changeCurrencyModalProceedButton.click();

					await page.waitForURL('**/order/**');
				}).toPass();

				await page.goto(`/web/${site.name}`);

				expect(
					await commerceThemeClassicCatalogPage.firstCardItem.innerText()
				).toContain(`${currency3.symbol}`);
			});

			await test.step('Verify the converted price-list price on the catalog', async () => {
				expect(
					await commerceThemeClassicCatalogPage
						.productCard(product.name['en_US'])
						.innerText()
				).toContain(`${currency3.symbol}`);
				await expect(
					commerceThemeClassicCatalogPage.productCardPrice(
						product.name['en_US'],
						priceEntry.priceFormatted
					)
				).toBeVisible();
			});

			await test.step('Verify the converted price on the product details page', async () => {
				await page.goto(
					`/web/${site.name}/p/` + product.name['en_US'],
					{
						waitUntil: 'networkidle',
					}
				);

				await expect(
					await productDetailsPage.priceField(
						priceEntry.priceFormatted,
						productDetailsPage.priceContainer
					)
				).toBeVisible();
			});

			await test.step('Add the product to the cart and verify the mini cart total', async () => {
				await productDetailsPage.addToCartButton.click();

				await expect(commerceMiniCartPage.miniCartButton).toHaveClass(
					'has-badge mini-cart-opener'
				);

				await commerceMiniCartPage.miniCartButton.click();

				await expect(
					commerceMiniCartPage.miniCartItem(product.name['en_US'])
				).toBeVisible();
				await expect(
					commerceMiniCartPage.miniCartTotalPrice
				).toHaveText(priceEntry.priceFormatted);

				await commerceMiniCartPage.miniCartButtonClose.click();
			});

			await test.step('Verify the converted price on the pending order', async () => {
				await commerceThemeClassicCatalogPage.goToOrderPages(
					'Pending Orders'
				);

				await (
					await commerceThemeClassicOrdersPage.tableRowButton({
						rowValue: cart.id,
					})
				).click();
				await expect(
					(
						await commerceThemeClassicOrdersPage.orderItemsTableRow(
							10,
							productSku.price.priceFormatted,
							true
						)
					).column.getByText(productSku.price.priceFormatted)
				).toBeVisible();
			});
		}
		finally {
			const orders =
				await apiHelpers.headlessCommerceAdminOrder.getOrdersPage();

			apiHelpers.data.push({id: orders.items[0].id, type: 'order'});
		}
	}
);
