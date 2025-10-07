/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {commercePagesTest} from '../../../../fixtures/commercePagesTest';
import {dataApiHelpersTest} from '../../../../fixtures/dataApiHelpersTest';
import {featureFlagsTest} from '../../../../fixtures/featureFlagsTest';
import {loginTest} from '../../../../fixtures/loginTest';
import getRandomString from '../../../../utils/getRandomString';
import {
	performLoginViaApi,
	performLogout,
} from '../../../../utils/performLogin';
import {classicCommerceSetUp} from '../../utils/commerce';

export const test = mergeTests(
	commercePagesTest,
	dataApiHelpersTest,
	featureFlagsTest({
		'LPD-20379': {enabled: true},
	}),
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
		commerceThemeMiniumCatalogPage,
		page,
	}) => {
		test.setTimeout(90000);

		const {channel, site} = await classicCommerceSetUp(apiHelpers);

		const account = await apiHelpers.headlessAdminUser.postAccount({
			name: getRandomString(),
			type: 'business',
		});

		const user =
			await apiHelpers.headlessAdminUser.getUserAccountByEmailAddress(
				'demo.unprivileged@liferay.com'
			);

		const rolesResponse =
			await apiHelpers.headlessAdminUser.getAccountRoles(account.id);

		const accountRoleBuyer = rolesResponse?.items?.filter((role) => {
			return role.name === 'Buyer';
		});

		await apiHelpers.headlessAdminUser.assignAccountRoles(
			account.externalReferenceCode,
			accountRoleBuyer[0].id,
			user.emailAddress
		);

		const siteRole =
			await apiHelpers.headlessAdminUser.getRoleByName('Site Member');

		await apiHelpers.headlessAdminUser.assignUserToSite(
			siteRole.id,
			site.id,
			user.id
		);

		await apiHelpers.headlessAdminUser.assignUserToAccountByEmailAddress(
			account.id,
			[user.emailAddress]
		);

		await commerceAdminChannelsPage.goto();

		await (
			await commerceAdminChannelsPage.channelsTableRowLink(channel.name)
		).click();

		await commerceAdminChannelDetailsPage.goToCurrencies();

		await commerceAdminChannelDetailsCurrenciesPage.addCurrencyButton.click();

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

		await (
			await commerceAdminChannelDetailsCurrenciesPage.currencyFrameCurrency(
				currency1.name['en_US']
			)
		).check();
		await (
			await commerceAdminChannelDetailsCurrenciesPage.currencyFrameCurrency(
				currency2.name['en_US']
			)
		).check();
		await (
			await commerceAdminChannelDetailsCurrenciesPage.currencyFrameCurrency(
				currency3.name['en_US']
			)
		).check();

		await commerceAdminChannelDetailsCurrenciesPage.addCurrencyAddButton.click();

		await expect(
			(
				await commerceAdminChannelDetailsCurrenciesPage.currenciesTableRow(
					0,
					currency1.name['en_US'],
					true
				)
			).row
		).toBeVisible();
		await expect(
			(
				await commerceAdminChannelDetailsCurrenciesPage.currenciesTableRow(
					0,
					currency2.name['en_US'],
					true
				)
			).row
		).toBeVisible();
		await expect(
			(
				await commerceAdminChannelDetailsCurrenciesPage.currenciesTableRow(
					0,
					currency3.name['en_US'],
					true
				)
			).row
		).toBeVisible();

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
			await commerceThemeMiniumCatalogPage.firstCardItem.innerText()
		).toContain(`${currency3.symbol}`);

		await performLogout(page);
		await performLoginViaApi({page, screenName: 'test'});

		await commerceAdminChannelsPage.goto();

		await (
			await commerceAdminChannelsPage.channelsTableRowLink(channel.name)
		).click();

		await commerceAdminChannelDetailsPage.goToCurrencies();

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

		await performLogout(page);
		await performLoginViaApi({page, screenName: 'demo.unprivileged'});

		await page.goto(`/web/${site.name}`);

		expect(
			await commerceThemeMiniumCatalogPage.firstCardItem.innerText()
		).toContain(`${currency3.symbol}`);

		await commerceThemeClassicCatalogPage
			.currencySelectorButton(currency3.code, currency3.symbol)
			.click();

		await expect(
			commerceThemeClassicCatalogPage.currencyListItem(currency1.code)
		).toBeVisible();
	}
);

test(
	'Buyer can change currency with active order',
	{tag: ['@LPD-48196']},
	async ({
		apiHelpers,
		commerceAdminChannelDetailsCurrenciesPage,
		commerceAdminChannelDetailsPage,
		commerceAdminChannelsPage,
		commerceThemeClassicCatalogPage,
		commerceThemeMiniumCatalogPage,
		page,
	}) => {
		test.setTimeout(90000);

		const {channel, site} = await classicCommerceSetUp(apiHelpers);

		const account = await apiHelpers.headlessAdminUser.postAccount({
			name: getRandomString(),
			type: 'business',
		});

		const user =
			await apiHelpers.headlessAdminUser.getUserAccountByEmailAddress(
				'demo.unprivileged@liferay.com'
			);

		const rolesResponse =
			await apiHelpers.headlessAdminUser.getAccountRoles(account.id);

		const accountRoleBuyer = rolesResponse?.items?.filter((role) => {
			return role.name === 'Buyer';
		});

		await apiHelpers.headlessAdminUser.assignAccountRoles(
			account.externalReferenceCode,
			accountRoleBuyer[0].id,
			user.emailAddress
		);

		const siteRole =
			await apiHelpers.headlessAdminUser.getRoleByName('Site Member');

		await apiHelpers.headlessAdminUser.assignUserToSite(
			siteRole.id,
			site.id,
			user.id
		);

		await apiHelpers.headlessAdminUser.assignUserToAccountByEmailAddress(
			account.id,
			[user.emailAddress]
		);

		await commerceAdminChannelsPage.goto();

		await (
			await commerceAdminChannelsPage.channelsTableRowLink(channel.name)
		).click();

		await commerceAdminChannelDetailsPage.goToCurrencies();

		await commerceAdminChannelDetailsCurrenciesPage.addCurrencyButton.click();

		const currencies =
			await apiHelpers.headlessCommerceAdminCatalog.getCurrenciesPage();

		const currency1 = currencies.items.find(
			(item) => item.name['en_US'] === 'US Dollar'
		);
		const currency2 = currencies.items.find(
			(item) => item.name['en_US'] === 'Australian Dollar'
		);
		const currency3 = currencies.items.find(
			(item) => item.name['en_US'] === 'Euro'
		);

		await (
			await commerceAdminChannelDetailsCurrenciesPage.currencyFrameCurrency(
				currency1.name['en_US']
			)
		).check();
		await (
			await commerceAdminChannelDetailsCurrenciesPage.currencyFrameCurrency(
				currency2.name['en_US']
			)
		).check();
		await (
			await commerceAdminChannelDetailsCurrenciesPage.currencyFrameCurrency(
				currency3.name['en_US']
			)
		).check();

		await commerceAdminChannelDetailsCurrenciesPage.addCurrencyAddButton.click();

		await expect(
			(
				await commerceAdminChannelDetailsCurrenciesPage.currenciesTableRow(
					0,
					currency1.name['en_US'],
					true
				)
			).row
		).toBeVisible();
		await expect(
			(
				await commerceAdminChannelDetailsCurrenciesPage.currenciesTableRow(
					0,
					currency2.name['en_US'],
					true
				)
			).row
		).toBeVisible();
		await expect(
			(
				await commerceAdminChannelDetailsCurrenciesPage.currenciesTableRow(
					0,
					currency3.name['en_US'],
					true
				)
			).row
		).toBeVisible();

		const product = (
			await apiHelpers.headlessCommerceAdminCatalog.getProducts(
				new URLSearchParams({
					filter: `name eq 'U-Joint'`,
					nestedFields: `skus`,
				})
			)
		).items[0];

		await apiHelpers.headlessCommerceDeliveryCart.postCart(
			{
				accountId: account.id,
				cartItems: [
					{
						quantity: 1,
						skuId: product.skus[0].id,
					},
				],
			},
			channel.id
		);

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

		try {
			await expect(
				commerceThemeClassicCatalogPage.changeCurrencyModalHeading
			).toBeVisible();

			await expect(async () => {
				await commerceThemeClassicCatalogPage.changeCurrencyModalProceedButton.click();

				await page.waitForURL('**/order/**');
			}).toPass();

			await page.goto(`/web/${site.name}`);

			expect(
				await commerceThemeMiniumCatalogPage.firstCardItem.innerText()
			).toContain(`${currency3.symbol}`);
		}
		finally {
			const orders =
				await apiHelpers.headlessCommerceAdminOrder.getOrdersPage();

			apiHelpers.data.push({id: orders.items[0].id, type: 'order'});
		}
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

		const user =
			await apiHelpers.headlessAdminUser.getUserAccountByEmailAddress(
				'demo.unprivileged@liferay.com'
			);

		const rolesResponse =
			await apiHelpers.headlessAdminUser.getAccountRoles(account.id);

		const accountRoleBuyer = rolesResponse?.items?.filter((role) => {
			return role.name === 'Buyer';
		});

		await apiHelpers.headlessAdminUser.assignAccountRoles(
			account.externalReferenceCode,
			accountRoleBuyer[0].id,
			user.emailAddress
		);

		const siteRole =
			await apiHelpers.headlessAdminUser.getRoleByName('Site Member');

		await apiHelpers.headlessAdminUser.assignUserToSite(
			siteRole.id,
			site.id,
			user.id
		);

		await apiHelpers.headlessAdminUser.assignUserToAccountByEmailAddress(
			account.id,
			[user.emailAddress]
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

		const productSku = (
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

		await commerceAdminChannelsPage.goto();

		await (
			await commerceAdminChannelsPage.channelsTableRowLink(channel.name)
		).click();

		await commerceAdminChannelDetailsPage.goToCurrencies();

		await commerceAdminChannelDetailsCurrenciesPage.addCurrencyButton.click();

		await (
			await commerceAdminChannelDetailsCurrenciesPage.currencyFrameCurrency(
				currency1.name['en_US']
			)
		).check();
		await (
			await commerceAdminChannelDetailsCurrenciesPage.currencyFrameCurrency(
				currency2.name['en_US']
			)
		).check();
		await (
			await commerceAdminChannelDetailsCurrenciesPage.currencyFrameCurrency(
				currency3.name['en_US']
			)
		).check();

		await commerceAdminChannelDetailsCurrenciesPage.addCurrencyAddButton.click();

		await expect(
			(
				await commerceAdminChannelDetailsCurrenciesPage.currenciesTableRow(
					0,
					currency1.name['en_US'],
					true
				)
			).row
		).toBeVisible();
		await expect(
			(
				await commerceAdminChannelDetailsCurrenciesPage.currenciesTableRow(
					0,
					currency2.name['en_US'],
					true
				)
			).row
		).toBeVisible();
		await expect(
			(
				await commerceAdminChannelDetailsCurrenciesPage.currenciesTableRow(
					0,
					currency3.name['en_US'],
					true
				)
			).row
		).toBeVisible();

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

		try {
			await expect(
				commerceThemeClassicCatalogPage.changeCurrencyModalHeading
			).toBeVisible();

			await expect(async () => {
				await commerceThemeClassicCatalogPage.changeCurrencyModalProceedButton.click();

				await page.waitForURL('**/order/**');
			}).toPass();

			await page.goto(`/web/${site.name}`);

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

			await page.goto(`/web/${site.name}/p/` + product.name['en_US']);

			await expect(
				await productDetailsPage.priceField(
					priceEntry.priceFormatted,
					productDetailsPage.priceContainer
				)
			).toBeVisible();

			await productDetailsPage.addToCartButton.click();

			await expect(commerceMiniCartPage.miniCartButton).toHaveClass(
				'has-badge mini-cart-opener'
			);

			await commerceMiniCartPage.miniCartButton.click();

			await expect(
				commerceMiniCartPage.miniCartItem(product.name['en_US'])
			).toBeVisible();
			await expect(commerceMiniCartPage.miniCartTotalPrice).toHaveText(
				priceEntry.priceFormatted
			);

			await commerceThemeClassicCatalogPage.ordersTab.click();

			await (
				await commerceThemeClassicOrdersPage.tableRowLink({
					colIndex: 1,
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
		}
		finally {
			const orders =
				await apiHelpers.headlessCommerceAdminOrder.getOrdersPage();

			apiHelpers.data.push({id: orders.items[0].id, type: 'order'});
		}
	}
);
