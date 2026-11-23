/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {accountsPagesTest} from '../../../../fixtures/accountsPagesTest';
import {commercePagesTest} from '../../../../fixtures/commercePagesTest';
import {dataApiHelpersTest} from '../../../../fixtures/dataApiHelpersTest';
import {loginTest} from '../../../../fixtures/loginTest';
import {DataApiHelpers} from '../../../../helpers/ApiHelpers';
import {clickAndExpectToBeVisible} from '../../../../utils/clickAndExpectToBeVisible';
import {getRandomInt} from '../../../../utils/getRandomInt';
import getRandomString from '../../../../utils/getRandomString';
import {
	performLoginViaApi,
	performUserSwitch,
} from '../../../../utils/performLogin';
import {waitForAlert} from '../../../../utils/waitForAlert';
import {createAccountWithBuyerUser, miniumSetUp} from '../../utils/commerce';

export const test = mergeTests(
	accountsPagesTest,
	commercePagesTest,
	dataApiHelpersTest,
	loginTest()
);

let catalog: {id: number};
let channel: {id: number; name: string};
let setupData: Array<{id: number | string; type: string}>;
let site: Site;

test.afterAll(async ({browser}) => {
	const page = await browser.newPage();

	await performLoginViaApi({page, screenName: 'test'});

	const apiHelpers = new DataApiHelpers(page);

	apiHelpers.setData(setupData);

	await apiHelpers.clearData();

	await page.close();
});

test.afterEach(async ({browser}) => {
	const page = await browser.newPage();

	await performLoginViaApi({page, screenName: 'test'});

	const apiHelpers = new DataApiHelpers(page);

	const ordersPage =
		await apiHelpers.headlessCommerceAdminOrder.getOrdersPage();

	await Promise.all(
		(ordersPage.items ?? []).map((order) =>
			apiHelpers.headlessCommerceAdminOrder.deleteOrder(order.id)
		)
	);

	await page.close();
});

test.beforeAll(async ({browser}) => {
	const page = await browser.newPage();

	await performLoginViaApi({page, screenName: 'test'});

	const apiHelpers = new DataApiHelpers(page);

	const miniumResult = await miniumSetUp(apiHelpers);

	catalog = miniumResult.catalog;
	channel = miniumResult.channel;
	site = miniumResult.site;
	setupData = [...apiHelpers.data];

	await page.close();
});

test(
	'Can set default currency per channel',
	{tag: ['@COMMERCE-9922', '@LPD-85008']},
	async ({
		accountsPage,
		apiHelpers,
		commerceChannelDefaultsPage,
		editAccountPage,
	}) => {
		const {account} = await createAccountWithBuyerUser(apiHelpers, site.id);

		await accountsPage.goto();
		await accountsPage.accountNameLink(account.name).click();
		await editAccountPage.channelDefaultsLink.click();

		await commerceChannelDefaultsPage.defaultCommerceCurrenciesButton.click();

		await expect(async () => {
			await commerceChannelDefaultsPage.editFrameCurrencySelect.selectOption(
				'Euro'
			);
			await commerceChannelDefaultsPage.editFrameSaveButton.click();

			await expect(
				commerceChannelDefaultsPage.defaultCommerceCurrencies.getByText(
					'Euro'
				)
			).toBeVisible({timeout: 500});
		}).toPass({timeout: 5000});

		await accountsPage.goto();
		await accountsPage.accountNameLink(account.name).click();
		await editAccountPage.channelDefaultsLink.click();

		await expect(
			commerceChannelDefaultsPage.defaultCommerceCurrencies.getByText(
				'Euro'
			)
		).toBeVisible();
	}
);

test(
	'Can edit default currency',
	{tag: ['@COMMERCE-9934', '@LPD-85008']},
	async ({
		accountsPage,
		apiHelpers,
		commerceChannelDefaultsPage,
		editAccountPage,
	}) => {
		const {account} = await createAccountWithBuyerUser(apiHelpers, site.id);

		await accountsPage.goto();
		await accountsPage.accountNameLink(account.name).click();
		await editAccountPage.channelDefaultsLink.click();

		await commerceChannelDefaultsPage.defaultCommerceCurrenciesButton.click();

		await expect(async () => {
			await commerceChannelDefaultsPage.editFrameCurrencySelect.selectOption(
				'Euro'
			);
			await commerceChannelDefaultsPage.editFrameSaveButton.click();

			await expect(
				commerceChannelDefaultsPage.defaultCommerceCurrenciesCell(
					'Euro'
				)
			).toBeVisible({timeout: 500});
		}).toPass({timeout: 5000});

		await commerceChannelDefaultsPage.defaultCommerceCurrenciesActions.click();
		await commerceChannelDefaultsPage.editMenuItem.click();

		await expect(async () => {
			await commerceChannelDefaultsPage.editFrameCurrencySelect.selectOption(
				'Brazilian Real'
			);
			await commerceChannelDefaultsPage.editFrameSaveButton.click();

			await expect(
				commerceChannelDefaultsPage.defaultCommerceCurrenciesCell(
					'Brazilian Real'
				)
			).toBeVisible({timeout: 500});
		}).toPass({timeout: 5000});

		await accountsPage.goto();
		await accountsPage.accountNameLink(account.name).click();
		await editAccountPage.channelDefaultsLink.click();

		await expect(
			commerceChannelDefaultsPage.defaultCommerceCurrenciesCell(
				'Brazilian Real'
			)
		).toBeVisible();
	}
);

test(
	'Can delete default currency',
	{tag: ['@COMMERCE-9931', '@COMMERCE-9937', '@LPD-85008']},
	async ({
		accountsPage,
		apiHelpers,
		commerceChannelDefaultsPage,
		commerceThemeMiniumCatalogPage,
		editAccountPage,
		page,
	}) => {
		const {account, buyerUser} = await createAccountWithBuyerUser(
			apiHelpers,
			site.id
		);

		await accountsPage.goto();
		await accountsPage.accountNameLink(account.name).click();
		await editAccountPage.channelDefaultsLink.click();

		await commerceChannelDefaultsPage.defaultCommerceCurrenciesButton.click();

		await expect(async () => {
			await commerceChannelDefaultsPage.editFrameCurrencySelect.selectOption(
				'Euro'
			);
			await commerceChannelDefaultsPage.editFrameSaveButton.click();

			await expect(
				commerceChannelDefaultsPage.defaultCommerceCurrenciesCell(
					'Euro'
				)
			).toBeVisible({timeout: 500});
		}).toPass({timeout: 5000});

		page.on('dialog', (dialog) => dialog.accept());

		await commerceChannelDefaultsPage.defaultCommerceCurrenciesActions.click();
		await commerceChannelDefaultsPage.deleteMenuItem.click();

		await expect(
			commerceChannelDefaultsPage.defaultCommerceCurrenciesCell('Euro')
		).not.toBeVisible();

		await performUserSwitch(page, buyerUser.alternateName);

		await page.goto(`/web/${site.name}/catalog`);

		await expect(
			commerceThemeMiniumCatalogPage.productCard('U-Joint')
		).toBeVisible();
		await expect(
			commerceThemeMiniumCatalogPage.productCard('U-Joint').getByText('$')
		).toBeVisible();
		await expect(
			commerceThemeMiniumCatalogPage.productCard('U-Joint').getByText('€')
		).not.toBeVisible();
	}
);

test(
	'Can link all channels to a currency',
	{tag: ['@COMMERCE-9923', '@LPD-85008']},
	async ({
		accountsPage,
		apiHelpers,
		commerceChannelDefaultsPage,
		editAccountPage,
	}) => {
		const {account} = await createAccountWithBuyerUser(apiHelpers, site.id);
		await apiHelpers.headlessCommerceAdminChannel.postChannel({
			name: 'Channel2',
			siteGroupId: site.id,
			type: 'site',
		});

		await accountsPage.goto();
		await accountsPage.accountNameLink(account.name).click();
		await editAccountPage.channelDefaultsLink.click();

		await commerceChannelDefaultsPage.defaultCommerceCurrenciesButton.click();

		await expect(async () => {
			await commerceChannelDefaultsPage.editFrameChannelSelect.selectOption(
				channel.name
			);
			await commerceChannelDefaultsPage.editFrameCurrencySelect.selectOption(
				'Euro'
			);
			await commerceChannelDefaultsPage.editFrameSaveButton.click();

			await expect(
				commerceChannelDefaultsPage.defaultCommerceCurrenciesCell(
					'Euro'
				)
			).toBeVisible({timeout: 500});
		}).toPass({timeout: 5000});

		await commerceChannelDefaultsPage.defaultCommerceCurrenciesButton.click();

		await expect(async () => {
			await commerceChannelDefaultsPage.editFrameChannelSelect.selectOption(
				'Channel2'
			);
			await commerceChannelDefaultsPage.editFrameCurrencySelect.selectOption(
				'Euro'
			);
			await commerceChannelDefaultsPage.editFrameSaveButton.click();

			await expect(
				commerceChannelDefaultsPage
					.defaultCommerceCurrenciesCell('Euro')
					.nth(1)
			).toBeVisible({timeout: 500});
		}).toPass({timeout: 5000});

		await commerceChannelDefaultsPage.defaultCommerceCurrenciesButton.click();

		await expect(async () => {
			await commerceChannelDefaultsPage.editFrameCurrencySelect.selectOption(
				'British Pound'
			);
			await commerceChannelDefaultsPage.editFrameSaveButton.click();

			await expect(
				commerceChannelDefaultsPage
					.defaultCommerceCurrenciesCell('All Other Channels')
					.first()
			).toBeVisible({timeout: 500});
		}).toPass({timeout: 5000});
	}
);

test(
	'Default currency rules can work on storefront',
	{tag: ['@COMMERCE-9925', '@LPD-85008']},
	async ({
		accountsPage,
		apiHelpers,
		commerceChannelDefaultsPage,
		commerceThemeMiniumCatalogPage,
		editAccountPage,
		page,
	}) => {
		const {account: account1, buyerUser: buyer1} =
			await createAccountWithBuyerUser(apiHelpers, site.id);

		const {buyerUser: buyer2} = await createAccountWithBuyerUser(
			apiHelpers,
			site.id
		);

		await accountsPage.goto();
		await accountsPage.accountNameLink(account1.name).click();
		await editAccountPage.channelDefaultsLink.click();

		await commerceChannelDefaultsPage.defaultCommerceCurrenciesButton.click();

		await expect(async () => {
			await commerceChannelDefaultsPage.editFrameChannelSelect.selectOption(
				channel.name
			);
			await commerceChannelDefaultsPage.editFrameCurrencySelect.selectOption(
				'Euro'
			);
			await commerceChannelDefaultsPage.editFrameSaveButton.click();

			await expect(
				commerceChannelDefaultsPage.defaultCommerceCurrenciesCell(
					'Euro'
				)
			).toBeVisible({timeout: 500});
		}).toPass({timeout: 5000});

		await performUserSwitch(page, buyer1.alternateName);

		await page.goto(`/web/${site.name}/catalog`);

		await expect(
			commerceThemeMiniumCatalogPage.productCard('U-Joint').getByText('€')
		).toBeVisible();

		await performUserSwitch(page, buyer2.alternateName);

		await page.goto(`/web/${site.name}/catalog`);

		await expect(
			commerceThemeMiniumCatalogPage.productCard('U-Joint').getByText('$')
		).toBeVisible();
		await expect(
			commerceThemeMiniumCatalogPage.productCard('U-Joint').getByText('€')
		).not.toBeVisible();
	}
);

test(
	'Can add default discount',
	{tag: ['@COMMERCE-9854', '@LPD-85008']},
	async ({
		accountsPage,
		apiHelpers,
		commerceChannelDefaultsPage,
		commerceThemeMiniumCatalogPage,
		editAccountPage,
		page,
		productDetailsPage,
	}) => {
		const {account, buyerUser} = await createAccountWithBuyerUser(
			apiHelpers,
			site.id
		);

		const products =
			await apiHelpers.headlessCommerceAdminCatalog.getProducts(
				new URLSearchParams({
					filter: `name eq 'U-Joint'`,
				})
			);

		const productId = products.items[0].productId;

		await apiHelpers.headlessCommerceAdminPricing.postDiscount({
			active: true,
			discountProducts: [{productId}],
			level: 'L1',
			percentageLevel1: 10,
			target: 'products',
			title: 'Discount 1 10% off',
			usePercentage: true,
		});

		const discount2 =
			await apiHelpers.headlessCommerceAdminPricing.postDiscount({
				active: true,
				discountProducts: [{productId}],
				level: 'L1',
				percentageLevel1: 20,
				target: 'products',
				title: 'Discount 2 20% off',
				usePercentage: true,
			});

		await accountsPage.goto();
		await accountsPage.accountNameLink(account.name).click();
		await editAccountPage.channelDefaultsLink.click();

		await commerceChannelDefaultsPage.defaultCommerceDiscountsButton.click();

		await expect(async () => {
			await commerceChannelDefaultsPage.editFrameChannelSelect.selectOption(
				channel.name
			);
			await commerceChannelDefaultsPage.editFrameDiscountSelect.selectOption(
				discount2.title
			);
			await commerceChannelDefaultsPage.editFrameSaveButton.click();

			await expect(
				commerceChannelDefaultsPage.defaultCommerceDiscountsCell(
					discount2.title
				)
			).toBeVisible({timeout: 500});
		}).toPass({timeout: 5000});

		await performUserSwitch(page, buyerUser.alternateName);

		await page.goto(`/web/${site.name}/catalog`);

		await expect(
			commerceThemeMiniumCatalogPage.productCard('U-Joint')
		).toBeVisible();

		await commerceThemeMiniumCatalogPage.productLink('U-Joint').click();

		await expect(productDetailsPage.priceContainer).toBeVisible();

		await expect(
			await productDetailsPage.priceField('$ 19.20')
		).toBeVisible();
	}
);

test(
	'Can select all other channels for default discount',
	{tag: ['@COMMERCE-9857', '@LPD-85008']},
	async ({
		accountsPage,
		apiHelpers,
		commerceChannelDefaultsPage,
		commerceThemeMiniumCatalogPage,
		editAccountPage,
		page,
		productDetailsPage,
	}) => {
		const {account, buyerUser} = await createAccountWithBuyerUser(
			apiHelpers,
			site.id
		);

		const products =
			await apiHelpers.headlessCommerceAdminCatalog.getProducts(
				new URLSearchParams({
					filter: `name eq 'U-Joint'`,
				})
			);

		const productId = products.items[0].productId;

		const discount =
			await apiHelpers.headlessCommerceAdminPricing.postDiscount({
				active: true,
				discountProducts: [{productId}],
				level: 'L1',
				percentageLevel1: 10,
				target: 'products',
				title: 'Discount 1 10% off',
				usePercentage: true,
			});

		await accountsPage.goto();
		await accountsPage.accountNameLink(account.name).click();
		await editAccountPage.channelDefaultsLink.click();

		await commerceChannelDefaultsPage.defaultCommerceDiscountsButton.click();

		await expect(async () => {
			await commerceChannelDefaultsPage.editFrameDiscountSelect.selectOption(
				discount.title
			);
			await commerceChannelDefaultsPage.editFrameOverrideCheckbox.check();
			await commerceChannelDefaultsPage.editFrameSaveButton.click();

			await expect(
				commerceChannelDefaultsPage.defaultCommerceDiscountsCell(
					discount.title
				)
			).toBeVisible({timeout: 500});
		}).toPass({timeout: 5000});

		await expect(
			commerceChannelDefaultsPage
				.defaultCommerceDiscountsCell('All Channels')
				.first()
		).toBeVisible();

		await performUserSwitch(page, buyerUser.alternateName);

		await page.goto(`/web/${site.name}/catalog`);

		await commerceThemeMiniumCatalogPage.productLink('U-Joint').click();

		await expect(
			await productDetailsPage.priceField('$ 21.60')
		).toBeVisible();
	}
);

test(
	'Can update default discounts entry',
	{tag: ['@COMMERCE-9858', '@LPD-85008']},
	async ({
		accountsPage,
		apiHelpers,
		commerceChannelDefaultsPage,
		commerceThemeMiniumCatalogPage,
		editAccountPage,
		page,
		productDetailsPage,
	}) => {
		const {account, buyerUser} = await createAccountWithBuyerUser(
			apiHelpers,
			site.id
		);

		const products =
			await apiHelpers.headlessCommerceAdminCatalog.getProducts(
				new URLSearchParams({
					filter: `name eq 'U-Joint'`,
				})
			);

		const productId = products.items[0].productId;

		const discount1 =
			await apiHelpers.headlessCommerceAdminPricing.postDiscount({
				active: true,
				discountProducts: [{productId}],
				level: 'L1',
				percentageLevel1: 10,
				target: 'products',
				title: 'Discount 1 10% off',
				usePercentage: true,
			});
		const discount2 =
			await apiHelpers.headlessCommerceAdminPricing.postDiscount({
				active: true,
				discountProducts: [{productId}],
				level: 'L1',
				percentageLevel1: 20,
				target: 'products',
				title: 'Discount 2 20% off',
				usePercentage: true,
			});

		await accountsPage.goto();
		await accountsPage.accountNameLink(account.name).click();
		await editAccountPage.channelDefaultsLink.click();

		await commerceChannelDefaultsPage.defaultCommerceDiscountsButton.click();

		await expect(async () => {
			await commerceChannelDefaultsPage.editFrameChannelSelect.selectOption(
				channel.name
			);
			await commerceChannelDefaultsPage.editFrameDiscountSelect.selectOption(
				discount2.title
			);
			await commerceChannelDefaultsPage.editFrameSaveButton.click();

			await expect(
				commerceChannelDefaultsPage.defaultCommerceDiscountsCell(
					discount2.title
				)
			).toBeVisible({timeout: 500});
		}).toPass({timeout: 5000});

		await performUserSwitch(page, buyerUser.alternateName);

		await page.goto(`/web/${site.name}/catalog`);

		await commerceThemeMiniumCatalogPage.productLink('U-Joint').click();

		await expect(
			await productDetailsPage.priceField('$ 19.20')
		).toBeVisible();

		await performUserSwitch(page, 'test');

		await accountsPage.goto();
		await accountsPage.accountNameLink(account.name).click();
		await editAccountPage.channelDefaultsLink.click();

		await commerceChannelDefaultsPage.defaultCommerceDiscountsActions.click();
		await commerceChannelDefaultsPage.editMenuItem.click();

		await expect(async () => {
			await commerceChannelDefaultsPage.editFrameDiscountSelect.selectOption(
				discount1.title
			);
			await commerceChannelDefaultsPage.editFrameSaveButton.click();

			await expect(
				commerceChannelDefaultsPage.defaultCommerceDiscountsCell(
					discount1.title
				)
			).toBeVisible({timeout: 500});
		}).toPass({timeout: 5000});

		await performUserSwitch(page, buyerUser.alternateName);

		await page.goto(`/web/${site.name}/catalog`);

		await commerceThemeMiniumCatalogPage.productLink('U-Joint').click();

		await expect(
			await productDetailsPage.priceField('$ 21.60')
		).toBeVisible();
	}
);

test(
	'Default discount can override account eligibility',
	{tag: ['@COMMERCE-9856', '@LPD-85008']},
	async ({
		accountsPage,
		apiHelpers,
		commerceAdminDiscountDetailsPage,
		commerceAdminDiscountsPage,
		commerceChannelDefaultsPage,
		commerceThemeMiniumCatalogPage,
		editAccountPage,
		page,
		productDetailsPage,
	}) => {
		const {account: account1} = await createAccountWithBuyerUser(
			apiHelpers,
			site.id
		);

		const account2 = await apiHelpers.headlessAdminUser.postAccount({
			name: `Account ${getRandomString()}`,
			type: 'business',
		});

		const products =
			await apiHelpers.headlessCommerceAdminCatalog.getProducts(
				new URLSearchParams({
					filter: `name eq 'U-Joint'`,
				})
			);

		const productId = products.items[0].productId;

		const discount1 =
			await apiHelpers.headlessCommerceAdminPricing.postDiscount({
				active: true,
				discountProducts: [{productId}],
				level: 'L1',
				percentageLevel1: 10,
				target: 'products',
				title: 'Discount 1 10% off',
				usePercentage: true,
			});

		await commerceAdminDiscountsPage.goto();

		await commerceAdminDiscountsPage.discountLink(discount1.title).click();

		await commerceAdminDiscountDetailsPage.eligibilityTab.click();
		await commerceAdminDiscountDetailsPage.specificAccountsRadio.click();
		await commerceAdminDiscountDetailsPage.addEligibilityEntry(
			'Find an Account',
			account1.name
		);

		const discount2 =
			await apiHelpers.headlessCommerceAdminPricing.postDiscount({
				active: true,
				discountProducts: [{productId}],
				level: 'L1',
				percentageLevel1: 20,
				target: 'products',
				title: 'Discount 2 20% off',
				usePercentage: true,
			});

		await commerceAdminDiscountsPage.goto();

		await commerceAdminDiscountsPage.discountLink(discount2.title).click();
		await commerceAdminDiscountDetailsPage.eligibilityTab.click();
		await commerceAdminDiscountDetailsPage.specificChannelsRadio.click();
		await commerceAdminDiscountDetailsPage.addEligibilityEntry(
			'Find a Channel',
			channel.name
		);

		await accountsPage.goto();
		await accountsPage.accountNameLink(account2.name).click();
		await editAccountPage.channelDefaultsLink.click();

		await commerceChannelDefaultsPage.defaultCommerceDiscountsButton.click();

		await expect(async () => {
			await commerceChannelDefaultsPage.editFrameDiscountSelect.selectOption(
				discount1.title
			);
			await commerceChannelDefaultsPage.editFrameOverrideCheckbox.check();
			await commerceChannelDefaultsPage.editFrameSaveButton.click();

			await expect(
				commerceChannelDefaultsPage.defaultCommerceDiscountsCell(
					discount1.title
				)
			).toBeVisible({timeout: 500});
		}).toPass({timeout: 5000});

		await page.goto(`/web/${site.name}/catalog`);

		await commerceThemeMiniumCatalogPage.productLink('U-Joint').click();

		await expect(
			await productDetailsPage.priceField('$ 21.60')
		).toBeVisible();
	}
);

test(
	'Can edit and delete default price list',
	{tag: ['@COMMERCE-9704', '@LPD-85008']},
	async ({
		accountsPage,
		apiHelpers,
		commerceChannelDefaultsPage,
		editAccountPage,
	}) => {
		const {account} = await createAccountWithBuyerUser(apiHelpers, site.id);

		const priceList1 =
			await apiHelpers.headlessCommerceAdminPricing.postPriceList({
				catalogId: catalog.id,
				currencyCode: 'USD',
				name: 'Test Price List 1',
				type: 'price-list',
			});
		const priceList2 =
			await apiHelpers.headlessCommerceAdminPricing.postPriceList({
				catalogId: catalog.id,
				currencyCode: 'USD',
				name: 'Test Price List 2',
				type: 'price-list',
			});

		await accountsPage.goto();
		await accountsPage.accountNameLink(account.name).click();
		await editAccountPage.channelDefaultsLink.click();

		await commerceChannelDefaultsPage.defaultCommercePriceListsButton.click();

		await expect(async () => {
			await commerceChannelDefaultsPage.editFrameChannelSelect.selectOption(
				channel.name
			);
			await commerceChannelDefaultsPage.editFramePriceListSelect.selectOption(
				priceList1.name
			);
			await commerceChannelDefaultsPage.editFrameSaveButton.click();

			await expect(
				commerceChannelDefaultsPage.defaultCommercePriceListsCell(
					priceList1.name
				)
			).toBeVisible({timeout: 500});
		}).toPass({timeout: 5000});

		await commerceChannelDefaultsPage.defaultCommercePriceListsActions.click();
		await commerceChannelDefaultsPage.editMenuItem.click();

		await expect(async () => {
			await commerceChannelDefaultsPage.editFrameChannelSelect.selectOption(
				'All Other Channels'
			);
			await commerceChannelDefaultsPage.editFramePriceListSelect.selectOption(
				priceList2.name
			);
			await commerceChannelDefaultsPage.editFrameSaveButton.click();

			await expect(
				commerceChannelDefaultsPage.defaultCommercePriceListsCell(
					priceList2.name
				)
			).toBeVisible({timeout: 500});
		}).toPass({timeout: 5000});

		await expect(
			commerceChannelDefaultsPage
				.defaultCommercePriceListsCell('All Channels')
				.first()
		).toBeVisible();

		commerceChannelDefaultsPage.page.on('dialog', (dialog) =>
			dialog.accept()
		);

		await commerceChannelDefaultsPage.defaultCommercePriceListsActions.click();
		await commerceChannelDefaultsPage.deleteMenuItem.click();

		await expect(
			commerceChannelDefaultsPage.defaultCommercePriceListsCell(
				priceList2.name
			)
		).not.toBeVisible();
	}
);

test(
	'Can see highest priority price list price in hierarchy mode',
	{tag: ['@COMMERCE-9704', '@LPD-85008']},
	async ({
		accountsPage,
		apiHelpers,
		commerceChannelDefaultsPage,
		commerceThemeMiniumCatalogPage,
		editAccountPage,
		page,
		productDetailsPage,
	}) => {
		const {account, buyerUser} = await createAccountWithBuyerUser(
			apiHelpers,
			site.id
		);

		const products =
			await apiHelpers.headlessCommerceAdminCatalog.getProducts(
				new URLSearchParams({filter: `name eq 'U-Joint'`})
			);

		const skuId = products.items[0].skus[0].id;

		const priceList1 =
			await apiHelpers.headlessCommerceAdminPricing.postPriceList({
				catalogId: catalog.id,
				currencyCode: 'USD',
				name: 'Test Price List 1',
				priority: 1,
				type: 'price-list',
			});
		const priceList2 =
			await apiHelpers.headlessCommerceAdminPricing.postPriceList({
				catalogId: catalog.id,
				currencyCode: 'USD',
				name: 'Test Price List 2',
				priority: 2,
				type: 'price-list',
			});

		await apiHelpers.headlessCommerceAdminPricing.postPriceEntry({
			price: 20,
			priceListId: priceList1.id,
			skuId,
		});
		await apiHelpers.headlessCommerceAdminPricing.postPriceEntry({
			price: 18,
			priceListId: priceList2.id,
			skuId,
		});

		await accountsPage.goto();
		await accountsPage.accountNameLink(account.name).click();
		await editAccountPage.channelDefaultsLink.click();

		await commerceChannelDefaultsPage.defaultCommercePriceListsButton.click();

		await expect(async () => {
			await commerceChannelDefaultsPage.editFrameChannelSelect.selectOption(
				channel.name
			);
			await commerceChannelDefaultsPage.editFramePriceListSelect.selectOption(
				priceList1.name
			);
			await commerceChannelDefaultsPage.editFrameSaveButton.click();

			await expect(
				commerceChannelDefaultsPage.defaultCommercePriceListsCell(
					priceList1.name
				)
			).toBeVisible({timeout: 500});
		}).toPass({timeout: 5000});

		await performUserSwitch(page, buyerUser.alternateName);

		await page.goto(`/web/${site.name}/catalog`);

		await commerceThemeMiniumCatalogPage.productLink('U-Joint').click();

		await expect(
			await productDetailsPage.priceField(
				'$ 20.00',
				productDetailsPage.priceContainer
			)
		).toBeVisible();

		await performUserSwitch(page, 'test');

		await accountsPage.goto();
		await accountsPage.accountNameLink(account.name).click();
		await editAccountPage.channelDefaultsLink.click();

		commerceChannelDefaultsPage.page.on('dialog', (dialog) =>
			dialog.accept()
		);

		await commerceChannelDefaultsPage.defaultCommercePriceListsActions.click();
		await commerceChannelDefaultsPage.deleteMenuItem.click();

		await performUserSwitch(page, buyerUser.alternateName);

		await page.goto(`/web/${site.name}/catalog`);

		await commerceThemeMiniumCatalogPage.productLink('U-Joint').click();

		await expect(
			await productDetailsPage.priceField(
				'$ 18.00',
				productDetailsPage.priceContainer
			)
		).toBeVisible();
	}
);

test(
	'Can see lowest price list price in lowest mode',
	{tag: ['@COMMERCE-9704', '@LPD-85008']},
	async ({
		accountsPage,
		apiHelpers,
		commerceChannelDefaultsPage,
		commercePricingSystemSettingsPage,
		commerceThemeMiniumCatalogPage,
		editAccountPage,
		page,
		productDetailsPage,
	}) => {
		await commercePricingSystemSettingsPage.setPriceListDiscoveryMethod(
			'lowest'
		);

		try {
			const {account, buyerUser} = await createAccountWithBuyerUser(
				apiHelpers,
				site.id
			);

			const products =
				await apiHelpers.headlessCommerceAdminCatalog.getProducts(
					new URLSearchParams({filter: `name eq 'U-Joint'`})
				);

			const skuId = products.items[0].skus[0].id;

			const priceList1 =
				await apiHelpers.headlessCommerceAdminPricing.postPriceList({
					catalogId: catalog.id,
					currencyCode: 'USD',
					name: 'Test Price List 1',
					type: 'price-list',
				});

			const priceList2 =
				await apiHelpers.headlessCommerceAdminPricing.postPriceList({
					catalogId: catalog.id,
					currencyCode: 'USD',
					name: 'Test Price List 2',
					type: 'price-list',
				});

			await apiHelpers.headlessCommerceAdminPricing.postPriceEntry({
				price: 20,
				priceListId: priceList1.id,
				skuId,
			});
			await apiHelpers.headlessCommerceAdminPricing.postPriceEntry({
				price: 18,
				priceListId: priceList2.id,
				skuId,
			});

			await accountsPage.goto();
			await accountsPage.accountNameLink(account.name).click();
			await editAccountPage.channelDefaultsLink.click();

			await commerceChannelDefaultsPage.defaultCommercePriceListsButton.click();

			await expect(async () => {
				await commerceChannelDefaultsPage.editFrameChannelSelect.selectOption(
					channel.name
				);
				await commerceChannelDefaultsPage.editFramePriceListSelect.selectOption(
					priceList1.name
				);
				await commerceChannelDefaultsPage.editFrameSaveButton.click();

				await expect(
					commerceChannelDefaultsPage.defaultCommercePriceListsCell(
						priceList1.name
					)
				).toBeVisible({timeout: 500});
			}).toPass({timeout: 5000});

			await performUserSwitch(page, buyerUser.alternateName);

			await page.goto(`/web/${site.name}/catalog`);

			await commerceThemeMiniumCatalogPage.productLink('U-Joint').click();

			await expect(
				await productDetailsPage.priceField(
					'$ 18.00',
					productDetailsPage.priceContainer
				)
			).toBeVisible();
		}
		finally {
			await performUserSwitch(page, 'test');

			await commercePricingSystemSettingsPage.setPriceListDiscoveryMethod();
		}
	}
);

test(
	'Can select only all other channels for default price list',
	{tag: ['@COMMERCE-9704', '@LPD-85008']},
	async ({
		accountsPage,
		apiHelpers,
		commerceChannelDefaultsPage,
		editAccountPage,
	}) => {
		const {account} = await createAccountWithBuyerUser(apiHelpers, site.id);

		await apiHelpers.headlessCommerceAdminChannel.postChannel({
			name: 'Test Channel 1',
			siteGroupId: site.id,
			type: 'site',
		});
		await apiHelpers.headlessCommerceAdminChannel.postChannel({
			name: 'Test Channel 2',
			siteGroupId: site.id,
			type: 'site',
		});

		const priceList1 =
			await apiHelpers.headlessCommerceAdminPricing.postPriceList({
				catalogId: catalog.id,
				currencyCode: 'USD',
				name: 'Test Price List 1',
				type: 'price-list',
			});
		const priceList2 =
			await apiHelpers.headlessCommerceAdminPricing.postPriceList({
				catalogId: catalog.id,
				currencyCode: 'USD',
				name: 'Test Price List 2',
				type: 'price-list',
			});

		await accountsPage.goto();
		await accountsPage.accountNameLink(account.name).click();
		await editAccountPage.channelDefaultsLink.click();

		for (const [channelName, priceListName] of [
			[channel.name, priceList1.name],
			['Test Channel 1', priceList2.name],
			['Test Channel 2', priceList1.name],
		]) {
			await commerceChannelDefaultsPage.defaultCommercePriceListsButton.click();

			await expect(async () => {
				await commerceChannelDefaultsPage.editFrameChannelSelect.selectOption(
					channelName
				);
				await commerceChannelDefaultsPage.editFramePriceListSelect.selectOption(
					priceListName
				);
				await commerceChannelDefaultsPage.editFrameSaveButton.click();

				await expect(
					commerceChannelDefaultsPage.defaultCommercePriceListsCell(
						priceListName
					)
				).toBeVisible({timeout: 500});
			}).toPass({timeout: 5000});
		}

		await commerceChannelDefaultsPage.defaultCommercePriceListsButton.click();

		await expect(async () => {
			await commerceChannelDefaultsPage.editFramePriceListSelect.selectOption(
				priceList2.name
			);
			await commerceChannelDefaultsPage.editFrameSaveButton.click();

			await expect(
				commerceChannelDefaultsPage
					.defaultCommercePriceListsCell('All Other Channels')
					.first()
			).toBeVisible({timeout: 500});
		}).toPass({timeout: 5000});
	}
);

test(
	'Can set default price list per channel',
	{tag: ['@COMMERCE-9704', '@LPD-85008']},
	async ({
		accountsPage,
		apiHelpers,
		commerceChannelDefaultsPage,
		commerceThemeMiniumCatalogPage,
		editAccountPage,
		page,
		productDetailsPage,
	}) => {
		const {account: account1, buyerUser: buyer1} =
			await createAccountWithBuyerUser(apiHelpers, site.id);

		const products =
			await apiHelpers.headlessCommerceAdminCatalog.getProducts(
				new URLSearchParams({filter: `name eq 'U-Joint'`})
			);

		const skuId = products.items[0].skus[0].id;

		const priceList1 =
			await apiHelpers.headlessCommerceAdminPricing.postPriceList({
				catalogId: catalog.id,
				currencyCode: 'USD',
				name: 'Test Price List 1',
				type: 'price-list',
			});
		const priceList2 =
			await apiHelpers.headlessCommerceAdminPricing.postPriceList({
				catalogId: catalog.id,
				currencyCode: 'USD',
				name: 'Test Price List 2',
				type: 'price-list',
			});

		await apiHelpers.headlessCommerceAdminPricing.postPriceEntry({
			price: 20,
			priceListId: priceList1.id,
			skuId,
		});
		await apiHelpers.headlessCommerceAdminPricing.postPriceEntry({
			price: 18,
			priceListId: priceList2.id,
			skuId,
		});

		await accountsPage.goto();
		await accountsPage.accountNameLink(account1.name).click();
		await editAccountPage.channelDefaultsLink.click();

		await commerceChannelDefaultsPage.defaultCommercePriceListsButton.click();

		await expect(async () => {
			await commerceChannelDefaultsPage.editFrameChannelSelect.selectOption(
				channel.name
			);
			await commerceChannelDefaultsPage.editFramePriceListSelect.selectOption(
				priceList1.name
			);
			await commerceChannelDefaultsPage.editFrameSaveButton.click();

			await expect(
				commerceChannelDefaultsPage.defaultCommercePriceListsCell(
					priceList1.name
				)
			).toBeVisible({timeout: 500});
		}).toPass({timeout: 5000});

		await performUserSwitch(page, buyer1.alternateName);

		await page.goto(`/web/${site.name}/catalog`);

		await commerceThemeMiniumCatalogPage.productLink('U-Joint').click();

		await expect(
			await productDetailsPage.priceField(
				'$ 20.00',
				productDetailsPage.priceContainer
			)
		).toBeVisible();
	}
);

test(
	'Default price list can override other eligibilities',
	{tag: ['@COMMERCE-9704', '@LPD-85008']},
	async ({
		accountsPage,
		apiHelpers,
		commerceAdminPriceListDetailsPage,
		commerceAdminPriceListsPage,
		commerceChannelDefaultsPage,
		commerceThemeMiniumCatalogPage,
		editAccountPage,
		page,
		productDetailsPage,
	}) => {
		const {account: account1, buyerUser: buyer1} =
			await createAccountWithBuyerUser(apiHelpers, site.id);

		const products =
			await apiHelpers.headlessCommerceAdminCatalog.getProducts(
				new URLSearchParams({filter: `name eq 'U-Joint'`})
			);

		const skuId = products.items[0].skus[0].id;

		const priceList1 =
			await apiHelpers.headlessCommerceAdminPricing.postPriceList({
				catalogId: catalog.id,
				currencyCode: 'USD',
				name: 'Test Price List 1',
				type: 'price-list',
			});
		const priceList2 =
			await apiHelpers.headlessCommerceAdminPricing.postPriceList({
				catalogId: catalog.id,
				currencyCode: 'USD',
				name: 'Test Price List 3',
				type: 'price-list',
			});

		await apiHelpers.headlessCommerceAdminPricing.postPriceEntry({
			price: 20,
			priceListId: priceList1.id,
			skuId,
		});
		await apiHelpers.headlessCommerceAdminPricing.postPriceEntry({
			price: 50,
			priceListId: priceList2.id,
			skuId,
		});

		await commerceAdminPriceListsPage.goto();
		await commerceAdminPriceListsPage
			.priceListLink(priceList1.name)
			.click();

		await commerceAdminPriceListDetailsPage.eligibilityTab.click();
		await commerceAdminPriceListDetailsPage.specificAccountsRadio.click();
		await commerceAdminPriceListDetailsPage.addEligibilityEntry(
			'Find an Account',
			account1.name
		);
		await commerceAdminPriceListDetailsPage.specificChannelsRadio.click();
		await commerceAdminPriceListDetailsPage.addEligibilityEntry(
			'Find a Channel',
			channel.name
		);

		await accountsPage.goto();
		await accountsPage.accountNameLink(account1.name).click();
		await editAccountPage.channelDefaultsLink.click();

		await commerceChannelDefaultsPage.defaultCommercePriceListsButton.click();

		await expect(async () => {
			await commerceChannelDefaultsPage.editFrameChannelSelect.selectOption(
				channel.name
			);
			await commerceChannelDefaultsPage.editFramePriceListSelect.selectOption(
				priceList2.name
			);
			await commerceChannelDefaultsPage.editFrameOverrideCheckbox.check();
			await commerceChannelDefaultsPage.editFrameSaveButton.click();

			await expect(
				commerceChannelDefaultsPage.defaultCommercePriceListsCell(
					priceList2.name
				)
			).toBeVisible({timeout: 500});
		}).toPass({timeout: 5000});

		await performUserSwitch(page, buyer1.alternateName);

		await page.goto(`/web/${site.name}/catalog`);

		await commerceThemeMiniumCatalogPage.productLink('U-Joint').click();

		await expect(
			await productDetailsPage.priceField(
				'$ 50.00',
				productDetailsPage.priceContainer
			)
		).toBeVisible();
	}
);

test(
	'Can add default delivery term with checkout',
	{tag: ['@COMMERCE-9723', '@LPD-85008']},
	async ({
		accountsPage,
		apiHelpers,
		checkoutPage,
		commerceAdminChannelDetailsPage,
		commerceAdminChannelsPage,
		commerceChannelDefaultsPage,
		commerceMiniCartPage,
		editAccountPage,
		page,
	}) => {
		const {account, buyerUser} = await createAccountWithBuyerUser(
			apiHelpers,
			site.id
		);

		await apiHelpers.headlessCommerceAdminOrder.postTerm({
			type: 'delivery-terms',
		});
		const term = await apiHelpers.headlessCommerceAdminOrder.postTerm({
			type: 'delivery-terms',
		});

		await commerceAdminChannelsPage.goto();

		await (
			await commerceAdminChannelsPage.channelsTableRowLink(channel.name)
		).click();
		await commerceAdminChannelDetailsPage.activateChannelConfiguration(
			'Flat Rate',
			'Shipping Methods'
		);
		await (
			await commerceAdminChannelDetailsPage.generalCommerceAdminChannelTableLink(
				'Flat Rate'
			)
		).click();
		await commerceAdminChannelDetailsPage.setEntryEligibility(
			'Specific Delivery Terms',
			term.label['en_US'],
			'Shipping Methods',
			'Standard Delivery'
		);

		await accountsPage.gotoAccountAdmin();

		await accountsPage.accountsTable.search(account.name);
		await accountsPage.accountNameLink(account.name).click();
		await editAccountPage.channelDefaultsLink.click();
		await commerceChannelDefaultsPage.defaultDeliveryCommerceTermEntriesButton.click();

		await expect(async () => {
			await commerceChannelDefaultsPage.editFrameChannelSelect.selectOption(
				channel.name
			);
			await commerceChannelDefaultsPage.editFrameTermSelect.selectOption(
				String(term.id)
			);
			await commerceChannelDefaultsPage.editFrameSaveButton.click();

			await expect(
				commerceChannelDefaultsPage.defaultDeliveryCommerceTermEntriesCell(
					term.label['en_US']
				)
			).toBeVisible({timeout: 500});
		}).toPass({timeout: 5000});

		await performUserSwitch(page, buyerUser.alternateName);

		await page.goto(`/web/${site.name}/catalog`);

		await commerceMiniCartPage.quickAddToCart('MIN55861');
		await commerceMiniCartPage.submitButton.click();

		await checkoutPage.addAddress({
			city: 'Test City',
			countryLabel: 'United States',
			name: 'Test Name',
			regionLabel: 'Florida',
			street: 'Test Street',
			zip: '12345',
		});
		await checkoutPage.continueButton.click();

		await page.waitForURL((url) => url.href.includes('shipping-method'));

		await checkoutPage.continueButton.click();

		await page.waitForURL((url) => url.href.includes('order-summary'));

		await expect(
			checkoutPage.deliveryTermLink(term.label['en_US'])
		).toBeVisible();

		await checkoutPage.continueButton.click();

		await expect(checkoutPage.orderSuccessMessage).toBeVisible();
	}
);

test(
	'Can have all channels linked to a delivery term',
	{tag: ['@COMMERCE-9729', '@LPD-85008']},
	async ({
		accountsPage,
		apiHelpers,
		commerceChannelDefaultsPage,
		editAccountPage,
		page,
	}) => {
		const {account} = await createAccountWithBuyerUser(apiHelpers, site.id);

		const term1 = await apiHelpers.headlessCommerceAdminOrder.postTerm({
			type: 'delivery-terms',
		});
		const term2 = await apiHelpers.headlessCommerceAdminOrder.postTerm({
			type: 'delivery-terms',
		});

		await accountsPage.gotoAccountAdmin();

		await accountsPage.accountsTable.search(account.name);
		await accountsPage.accountNameLink(account.name).click();
		await editAccountPage.channelDefaultsLink.click();
		await commerceChannelDefaultsPage.defaultDeliveryCommerceTermEntriesButton.click();

		await expect(
			commerceChannelDefaultsPage.editFrameChannelSelect
		).toBeVisible();

		const optionsBefore = (
			await commerceChannelDefaultsPage.editFrameChannelOptions.allTextContents()
		).map((s) => s.trim());

		expect(optionsBefore).toContain('All Channels');
		expect(optionsBefore).toContain(channel.name);
		expect(optionsBefore).not.toContain('All Other Channels');

		await page.keyboard.press('Escape');

		await expect(
			commerceChannelDefaultsPage.editFrameTermSelect
		).toBeHidden();

		await clickAndExpectToBeVisible({
			target: commerceChannelDefaultsPage.editFrameTermSelect,
			trigger:
				commerceChannelDefaultsPage.defaultDeliveryCommerceTermEntriesButton,
		});

		await commerceChannelDefaultsPage.editFrameChannelSelect.selectOption(
			channel.name
		);
		await commerceChannelDefaultsPage.editFrameTermSelect.selectOption(
			String(term1.id)
		);
		await commerceChannelDefaultsPage.editFrameSaveButton.click();

		await expect(
			commerceChannelDefaultsPage.editFrameTermSelect
		).toBeHidden();

		await expect(
			commerceChannelDefaultsPage.defaultDeliveryCommerceTermEntriesCell(
				term1.label['en_US']
			)
		).toBeVisible();

		await commerceChannelDefaultsPage.defaultDeliveryCommerceTermEntriesButton.click();

		await expect(
			commerceChannelDefaultsPage.editFrameChannelSelect
		).toBeVisible();

		const optionsAfter = (
			await commerceChannelDefaultsPage.editFrameChannelOptions.allTextContents()
		).map((s) => s.trim());

		expect(optionsAfter).toContain('All Other Channels');
		expect(optionsAfter).not.toContain('All Channels');

		await page.keyboard.press('Escape');

		await expect(
			commerceChannelDefaultsPage.editFrameTermSelect
		).toBeHidden();

		page.on('dialog', (dialog) => dialog.accept());

		await commerceChannelDefaultsPage.defaultDeliveryCommerceTermEntriesActionsButton.click();
		await commerceChannelDefaultsPage.deleteMenuItem.click();

		await clickAndExpectToBeVisible({
			target: commerceChannelDefaultsPage.editFrameTermSelect,
			trigger:
				commerceChannelDefaultsPage.defaultDeliveryCommerceTermEntriesButton,
		});

		await commerceChannelDefaultsPage.editFrameTermSelect.selectOption(
			String(term1.id)
		);
		await commerceChannelDefaultsPage.editFrameSaveButton.click();

		await expect(
			commerceChannelDefaultsPage.editFrameTermSelect
		).toBeHidden();

		await expect(
			commerceChannelDefaultsPage.defaultDeliveryCommerceTermEntriesCell(
				'All Channels',
				true
			)
		).toBeVisible();

		await clickAndExpectToBeVisible({
			target: commerceChannelDefaultsPage.editFrameTermSelect,
			trigger:
				commerceChannelDefaultsPage.defaultDeliveryCommerceTermEntriesButton,
		});

		await commerceChannelDefaultsPage.editFrameChannelSelect.selectOption(
			channel.name
		);
		await commerceChannelDefaultsPage.editFrameTermSelect.selectOption(
			String(term2.id)
		);
		await commerceChannelDefaultsPage.editFrameSaveButton.click();

		await expect(
			commerceChannelDefaultsPage.editFrameTermSelect
		).toBeHidden();

		await expect(
			commerceChannelDefaultsPage.defaultDeliveryCommerceTermEntriesCell(
				term2.label['en_US']
			)
		).toBeVisible();

		await expect(
			commerceChannelDefaultsPage.defaultDeliveryCommerceTermEntriesCell(
				'All Other Channels',
				true
			)
		).toBeVisible();
	}
);

test(
	'Cannot use disabled default delivery term',
	{tag: ['@COMMERCE-9725', '@LPD-85008']},
	async ({
		accountsPage,
		apiHelpers,
		checkoutPage,
		commerceAdminChannelDetailsPage,
		commerceAdminChannelsPage,
		commerceChannelDefaultsPage,
		commerceMiniCartPage,
		editAccountPage,
		page,
	}) => {
		const {account, buyerUser} = await createAccountWithBuyerUser(
			apiHelpers,
			site.id
		);

		const term1 = await apiHelpers.headlessCommerceAdminOrder.postTerm({
			type: 'delivery-terms',
		});
		const term2 = await apiHelpers.headlessCommerceAdminOrder.postTerm({
			type: 'delivery-terms',
		});
		await apiHelpers.headlessCommerceAdminOrder.postTerm({
			type: 'delivery-terms',
		});

		await commerceAdminChannelsPage.goto();

		await (
			await commerceAdminChannelsPage.channelsTableRowLink(channel.name)
		).click();
		await commerceAdminChannelDetailsPage.activateChannelConfiguration(
			'Flat Rate',
			'Shipping Methods'
		);
		await (
			await commerceAdminChannelDetailsPage.generalCommerceAdminChannelTableLink(
				'Flat Rate'
			)
		).click();
		await commerceAdminChannelDetailsPage.setEntryEligibility(
			'Specific Delivery Terms',
			term1.label['en_US'],
			'Shipping Methods',
			'Standard Delivery'
		);

		await commerceAdminChannelsPage.addFlatRateDeliveryTermEligibility(
			commerceAdminChannelDetailsPage,
			channel.name,
			term2.label['en_US']
		);

		await accountsPage.gotoAccountAdmin();

		await accountsPage.accountsTable.search(account.name);
		await accountsPage.accountNameLink(account.name).click();
		await editAccountPage.channelDefaultsLink.click();
		await commerceChannelDefaultsPage.defaultDeliveryCommerceTermEntriesButton.click();

		await expect(async () => {
			await commerceChannelDefaultsPage.editFrameChannelSelect.selectOption(
				channel.name
			);
			await commerceChannelDefaultsPage.editFrameTermSelect.selectOption(
				String(term2.id)
			);
			await commerceChannelDefaultsPage.editFrameSaveButton.click();

			await expect(
				commerceChannelDefaultsPage.defaultDeliveryCommerceTermEntriesCell(
					term2.label['en_US']
				)
			).toBeVisible({timeout: 500});
		}).toPass({timeout: 5000});

		await apiHelpers.headlessCommerceAdminOrder.patchTerm(term2.id, {
			active: false,
		});

		await performUserSwitch(page, buyerUser.alternateName);

		await page.goto(`/web/${site.name}/catalog`);

		await commerceMiniCartPage.quickAddToCart('MIN55861');
		await commerceMiniCartPage.submitButton.click();

		await checkoutPage.addAddress({
			city: 'Test City',
			countryLabel: 'United States',
			name: 'Test Name',
			regionLabel: 'Florida',
			street: 'Test Street',
			zip: '12345',
		});
		await checkoutPage.continueButton.click();

		await page.waitForURL((url) => url.href.includes('shipping-method'));

		await checkoutPage.continueButton.click();

		await page.waitForURL((url) => url.href.includes('order-summary'));

		await expect(
			checkoutPage.deliveryTermLink(term1.label['en_US'])
		).toBeVisible();

		await checkoutPage.continueButton.click();

		await expect(checkoutPage.orderSuccessMessage).toBeVisible();
	}
);

test(
	'Default delivery term can override other eligibilities',
	{tag: ['@COMMERCE-9782', '@LPD-85008']},
	async ({
		accountsPage,
		apiHelpers,
		checkoutPage,
		commerceAdminChannelDetailsPage,
		commerceAdminChannelsPage,
		commerceChannelDefaultsPage,
		commerceMiniCartPage,
		editAccountPage,
		page,
	}) => {
		const {account, buyerUser} = await createAccountWithBuyerUser(
			apiHelpers,
			site.id
		);

		const term1 = await apiHelpers.headlessCommerceAdminOrder.postTerm({
			type: 'delivery-terms',
		});
		const term2 = await apiHelpers.headlessCommerceAdminOrder.postTerm({
			type: 'delivery-terms',
		});
		await apiHelpers.headlessCommerceAdminOrder.postTerm({
			active: false,
			type: 'delivery-terms',
		});

		await commerceAdminChannelsPage.goto();

		await (
			await commerceAdminChannelsPage.channelsTableRowLink(channel.name)
		).click();
		await commerceAdminChannelDetailsPage.activateChannelConfiguration(
			'Flat Rate',
			'Shipping Methods'
		);
		await (
			await commerceAdminChannelDetailsPage.generalCommerceAdminChannelTableLink(
				'Flat Rate'
			)
		).click();
		await commerceAdminChannelDetailsPage.setEntryEligibility(
			'Specific Delivery Terms',
			term1.label['en_US'],
			'Shipping Methods',
			'Standard Delivery'
		);

		await commerceAdminChannelsPage.addFlatRateDeliveryTermEligibility(
			commerceAdminChannelDetailsPage,
			channel.name,
			term2.label['en_US']
		);

		await accountsPage.gotoAccountAdmin();

		await accountsPage.accountsTable.search(account.name);
		await accountsPage.accountNameLink(account.name).click();
		await editAccountPage.channelDefaultsLink.click();
		await commerceChannelDefaultsPage.defaultDeliveryCommerceTermEntriesButton.click();

		await expect(async () => {
			await commerceChannelDefaultsPage.editFrameChannelSelect.selectOption(
				channel.name
			);
			await commerceChannelDefaultsPage.editFrameTermSelect.selectOption(
				String(term2.id)
			);
			await commerceChannelDefaultsPage.editFrameOverrideCheckbox.check();
			await commerceChannelDefaultsPage.editFrameSaveButton.click();

			await expect(
				commerceChannelDefaultsPage.defaultDeliveryCommerceTermEntriesCell(
					term2.label['en_US']
				)
			).toBeVisible({timeout: 500});
		}).toPass({timeout: 5000});

		await performUserSwitch(page, buyerUser.alternateName);

		await page.goto(`/web/${site.name}/catalog`);

		await commerceMiniCartPage.quickAddToCart('MIN55861');
		await commerceMiniCartPage.submitButton.click();

		await checkoutPage.addAddress({
			city: 'Test City',
			countryLabel: 'United States',
			name: 'Test Name',
			regionLabel: 'Florida',
			street: 'Test Street',
			zip: '12345',
		});
		await checkoutPage.continueButton.click();

		await page.waitForURL((url) => url.href.includes('shipping-method'));

		await checkoutPage.continueButton.click();

		await page.waitForURL((url) => url.href.includes('order-summary'));

		await expect(
			checkoutPage.deliveryTermLink(term2.label['en_US'])
		).toBeVisible();

		await checkoutPage.continueButton.click();

		await expect(checkoutPage.orderSuccessMessage).toBeVisible();
	}
);

test(
	'Delivery eligibility rules can persist at checkout',
	{tag: ['@COMMERCE-9785']},
	async ({
		accountsPage,
		apiHelpers,
		checkoutPage,
		commerceAdminChannelDetailsPage,
		commerceAdminChannelsPage,
		commerceChannelDefaultsPage,
		commerceMiniCartPage,
		editAccountPage,
		page,
	}) => {
		const {account, buyerUser} = await createAccountWithBuyerUser(
			apiHelpers,
			site.id
		);

		const term1 = await apiHelpers.headlessCommerceAdminOrder.postTerm({
			type: 'delivery-terms',
		});
		const term2 = await apiHelpers.headlessCommerceAdminOrder.postTerm({
			type: 'delivery-terms',
		});
		const term3 = await apiHelpers.headlessCommerceAdminOrder.postTerm({
			active: false,
			type: 'delivery-terms',
		});

		await commerceAdminChannelsPage.goto();

		await (
			await commerceAdminChannelsPage.channelsTableRowLink(channel.name)
		).click();
		await commerceAdminChannelDetailsPage.activateChannelConfiguration(
			'Flat Rate',
			'Shipping Methods'
		);
		await (
			await commerceAdminChannelDetailsPage.generalCommerceAdminChannelTableLink(
				'Flat Rate'
			)
		).click();
		await commerceAdminChannelDetailsPage.setEntryEligibility(
			'Specific Delivery Terms',
			term1.label['en_US'],
			'Shipping Methods',
			'Standard Delivery'
		);

		await commerceAdminChannelsPage.addFlatRateDeliveryTermEligibility(
			commerceAdminChannelDetailsPage,
			channel.name,
			term2.label['en_US']
		);

		await accountsPage.gotoAccountAdmin();

		await accountsPage.accountsTable.search(account.name);
		await accountsPage.accountNameLink(account.name).click();
		await editAccountPage.channelDefaultsLink.click();
		await commerceChannelDefaultsPage.defaultDeliveryCommerceTermEntriesButton.click();

		await expect(async () => {
			await commerceChannelDefaultsPage.editFrameChannelSelect.selectOption(
				channel.name
			);
			await commerceChannelDefaultsPage.editFrameTermSelect.selectOption(
				String(term1.id)
			);
			await commerceChannelDefaultsPage.editFrameSaveButton.click();

			await expect(
				commerceChannelDefaultsPage.defaultDeliveryCommerceTermEntriesCell(
					term1.label['en_US']
				)
			).toBeVisible({timeout: 500});
		}).toPass({timeout: 5000});

		await performUserSwitch(page, buyerUser.alternateName);

		await page.goto(`/web/${site.name}/catalog`);

		await commerceMiniCartPage.quickAddToCart('MIN55861');
		await commerceMiniCartPage.submitButton.click();

		await checkoutPage.addAddress({
			city: 'Test City',
			countryLabel: 'United States',
			name: 'Test Name',
			regionLabel: 'Florida',
			street: 'Test Street',
			zip: '12345',
		});
		await checkoutPage.continueButton.click();

		await page.waitForURL((url) => url.href.includes('shipping-method'));

		await checkoutPage.continueButton.click();

		await page.waitForURL((url) => url.href.includes('delivery-terms'));

		await expect(
			checkoutPage.deliveryTermOption(term1.label['en_US'])
		).toBeChecked();
		await expect(
			checkoutPage.deliveryTermOption(term2.label['en_US'])
		).toBeVisible();
		await expect(
			checkoutPage.deliveryTermOption(term3.label['en_US'])
		).not.toBeVisible();

		await checkoutPage.continueButton.click();

		await page.waitForURL((url) => url.href.includes('order-summary'));

		await checkoutPage.continueButton.click();

		await expect(checkoutPage.orderSuccessMessage).toBeVisible();
	}
);

test(
	'Can add default payment term with checkout',
	{tag: ['@COMMERCE-9724', '@LPD-85008']},
	async ({
		accountsPage,
		apiHelpers,
		checkoutPage,
		commerceAdminChannelDetailsPage,
		commerceAdminChannelsPage,
		commerceChannelDefaultsPage,
		commerceMiniCartPage,
		editAccountPage,
		page,
	}) => {
		const {account, buyerUser} = await createAccountWithBuyerUser(
			apiHelpers,
			site.id
		);

		await apiHelpers.headlessCommerceAdminOrder.postTerm({
			type: 'payment-terms',
		});
		const term = await apiHelpers.headlessCommerceAdminOrder.postTerm({
			type: 'payment-terms',
		});

		await commerceAdminChannelsPage.goto();

		await (
			await commerceAdminChannelsPage.channelsTableRowLink(channel.name)
		).click();
		await commerceAdminChannelDetailsPage.activateChannelConfiguration(
			'Money Order',
			'Payment Methods'
		);

		await (
			await commerceAdminChannelDetailsPage.generalCommerceAdminChannelTableLink(
				'Money Order'
			)
		).click();
		await commerceAdminChannelDetailsPage.setEntryEligibility(
			'Specific Payment Terms',
			term.label['en_US'],
			'Payment Methods',
			'Money Order'
		);

		await accountsPage.gotoAccountAdmin();

		await accountsPage.accountsTable.search(account.name);
		await accountsPage.accountNameLink(account.name).click();
		await editAccountPage.channelDefaultsLink.click();
		await commerceChannelDefaultsPage.defaultPaymentCommerceTermEntriesButton.click();

		await expect(
			commerceChannelDefaultsPage.editFrameChannelSelect
		).toBeVisible();

		await expect(async () => {
			await commerceChannelDefaultsPage.editFrameChannelSelect.selectOption(
				channel.name
			);
			await commerceChannelDefaultsPage.editFrameTermSelect.selectOption(
				String(term.id)
			);
			await commerceChannelDefaultsPage.editFrameSaveButton.click();

			await expect(
				commerceChannelDefaultsPage.defaultPaymentCommerceTermEntriesCell(
					term.label['en_US']
				)
			).toBeVisible({timeout: 5000});
		}).toPass({timeout: 20000});

		await performUserSwitch(page, buyerUser.alternateName);

		await page.goto(`/web${site.friendlyUrlPath}/catalog`);

		await commerceMiniCartPage.quickAddToCart('MIN55861');
		await commerceMiniCartPage.submitButton.click();

		await checkoutPage.addAddress({
			city: 'Test City',
			countryLabel: 'United States',
			name: 'Test Name',
			regionLabel: 'Florida',
			street: 'Test Street',
			zip: '12345',
		});
		await checkoutPage.continueButton.click();

		await page.waitForURL((url) => url.href.includes('shipping-method'));

		await checkoutPage.continueButton.click();

		await page.waitForURL((url) => url.href.includes('order-summary'));

		await expect(
			checkoutPage.paymentTermLink(term.label['en_US'])
		).toBeVisible();

		await checkoutPage.continueButton.click();

		await expect(checkoutPage.goToOrderDetailsButton).toBeVisible();
	}
);

test(
	'Can have all channels linked to a payment term',
	{tag: ['@COMMERCE-9730', '@LPD-85008']},
	async ({
		accountsPage,
		apiHelpers,
		commerceChannelDefaultsPage,
		editAccountPage,
		page,
	}) => {
		const {account} = await createAccountWithBuyerUser(apiHelpers, site.id);

		const term1 = await apiHelpers.headlessCommerceAdminOrder.postTerm({
			type: 'payment-terms',
		});
		const term2 = await apiHelpers.headlessCommerceAdminOrder.postTerm({
			type: 'payment-terms',
		});

		await accountsPage.gotoAccountAdmin();

		await accountsPage.accountsTable.search(account.name);
		await accountsPage.accountNameLink(account.name).click();
		await editAccountPage.channelDefaultsLink.click();
		await commerceChannelDefaultsPage.defaultPaymentCommerceTermEntriesButton.click();

		await expect(
			commerceChannelDefaultsPage.editFrameChannelSelect
		).toBeVisible();

		const optionsBefore = (
			await commerceChannelDefaultsPage.editFrameChannelOptions.allTextContents()
		).map((s) => s.trim());

		expect(optionsBefore).toContain('All Channels');
		expect(optionsBefore).toContain(channel.name);
		expect(optionsBefore).not.toContain('All Other Channels');

		await page.keyboard.press('Escape');

		await expect(
			commerceChannelDefaultsPage.editFrameTermSelect
		).toBeHidden();

		await clickAndExpectToBeVisible({
			target: commerceChannelDefaultsPage.editFrameTermSelect,
			trigger:
				commerceChannelDefaultsPage.defaultPaymentCommerceTermEntriesButton,
		});

		await commerceChannelDefaultsPage.editFrameChannelSelect.selectOption(
			channel.name
		);
		await commerceChannelDefaultsPage.editFrameTermSelect.selectOption(
			String(term1.id)
		);
		await commerceChannelDefaultsPage.editFrameSaveButton.click();

		await expect(
			commerceChannelDefaultsPage.editFrameTermSelect
		).toBeHidden();

		await expect(
			commerceChannelDefaultsPage.defaultPaymentCommerceTermEntriesCell(
				term1.label['en_US']
			)
		).toBeVisible();

		await commerceChannelDefaultsPage.defaultPaymentCommerceTermEntriesButton.click();

		await expect(
			commerceChannelDefaultsPage.editFrameChannelSelect
		).toBeVisible();

		const optionsAfter = (
			await commerceChannelDefaultsPage.editFrameChannelOptions.allTextContents()
		).map((s) => s.trim());

		expect(optionsAfter).toContain('All Other Channels');
		expect(optionsAfter).not.toContain('All Channels');

		await page.keyboard.press('Escape');

		await expect(
			commerceChannelDefaultsPage.editFrameTermSelect
		).toBeHidden();

		page.on('dialog', (dialog) => dialog.accept());

		await commerceChannelDefaultsPage.defaultPaymentCommerceTermEntriesActionsButton.click();
		await commerceChannelDefaultsPage.deleteMenuItem.click();

		await clickAndExpectToBeVisible({
			target: commerceChannelDefaultsPage.editFrameTermSelect,
			trigger:
				commerceChannelDefaultsPage.defaultPaymentCommerceTermEntriesButton,
		});

		await commerceChannelDefaultsPage.editFrameTermSelect.selectOption(
			String(term1.id)
		);
		await commerceChannelDefaultsPage.editFrameSaveButton.click();

		await expect(
			commerceChannelDefaultsPage.editFrameTermSelect
		).toBeHidden();

		await expect(
			commerceChannelDefaultsPage.defaultPaymentCommerceTermEntriesCell(
				'All Channels',
				true
			)
		).toBeVisible();

		await clickAndExpectToBeVisible({
			target: commerceChannelDefaultsPage.editFrameTermSelect,
			trigger:
				commerceChannelDefaultsPage.defaultPaymentCommerceTermEntriesButton,
		});

		await commerceChannelDefaultsPage.editFrameChannelSelect.selectOption(
			channel.name
		);
		await commerceChannelDefaultsPage.editFrameTermSelect.selectOption(
			String(term2.id)
		);
		await commerceChannelDefaultsPage.editFrameSaveButton.click();

		await expect(
			commerceChannelDefaultsPage.editFrameTermSelect
		).toBeHidden();

		await expect(
			commerceChannelDefaultsPage.defaultPaymentCommerceTermEntriesCell(
				term2.label['en_US']
			)
		).toBeVisible();

		await expect(
			commerceChannelDefaultsPage.defaultPaymentCommerceTermEntriesCell(
				'All Other Channels',
				true
			)
		).toBeVisible();
	}
);

test(
	'Cannot use disabled default payment terms',
	{tag: ['@COMMERCE-9726', '@LPD-85008']},
	async ({
		accountsPage,
		apiHelpers,
		checkoutPage,
		commerceAdminChannelDetailsPage,
		commerceAdminChannelsPage,
		commerceChannelDefaultsPage,
		commerceMiniCartPage,
		editAccountPage,
		page,
	}) => {
		const {account, buyerUser} = await createAccountWithBuyerUser(
			apiHelpers,
			site.id
		);

		const term1 = await apiHelpers.headlessCommerceAdminOrder.postTerm({
			type: 'payment-terms',
		});
		const term2 = await apiHelpers.headlessCommerceAdminOrder.postTerm({
			type: 'payment-terms',
		});
		await apiHelpers.headlessCommerceAdminOrder.postTerm({
			type: 'payment-terms',
		});

		await commerceAdminChannelsPage.goto();

		await (
			await commerceAdminChannelsPage.channelsTableRowLink(channel.name)
		).click();
		await commerceAdminChannelDetailsPage.activateChannelConfiguration(
			'Money Order',
			'Payment Methods'
		);
		await (
			await commerceAdminChannelDetailsPage.generalCommerceAdminChannelTableLink(
				'Money Order'
			)
		).click();
		await commerceAdminChannelDetailsPage.setEntryEligibility(
			'Specific Payment Terms',
			term1.label['en_US'],
			'Payment Methods',
			'Money Order'
		);

		await commerceAdminChannelsPage.addMoneyOrderPaymentTermEligibility(
			commerceAdminChannelDetailsPage,
			channel.name,
			term2.label['en_US']
		);

		await accountsPage.gotoAccountAdmin();

		await accountsPage.accountsTable.search(account.name);
		await accountsPage.accountNameLink(account.name).click();
		await editAccountPage.channelDefaultsLink.click();
		await commerceChannelDefaultsPage.defaultPaymentCommerceTermEntriesButton.click();

		await expect(async () => {
			await commerceChannelDefaultsPage.editFrameChannelSelect.selectOption(
				channel.name
			);
			await commerceChannelDefaultsPage.editFrameTermSelect.selectOption(
				String(term2.id)
			);
			await commerceChannelDefaultsPage.editFrameSaveButton.click();

			await expect(
				commerceChannelDefaultsPage.defaultPaymentCommerceTermEntriesCell(
					term2.label['en_US']
				)
			).toBeVisible({timeout: 500});
		}).toPass({timeout: 5000});

		await apiHelpers.headlessCommerceAdminOrder.patchTerm(term2.id, {
			active: false,
		});

		await performUserSwitch(page, buyerUser.alternateName);

		await page.goto(`/web${site.friendlyUrlPath}/catalog`);

		await commerceMiniCartPage.quickAddToCart('MIN55861');
		await commerceMiniCartPage.submitButton.click();

		await checkoutPage.addAddress({
			city: 'Test City',
			countryLabel: 'United States',
			name: 'Test Name',
			regionLabel: 'Florida',
			street: 'Test Street',
			zip: '12345',
		});
		await checkoutPage.continueButton.click();

		await page.waitForURL((url) => url.href.includes('shipping-method'));

		await checkoutPage.continueButton.click();

		await page.waitForURL((url) => url.href.includes('order-summary'));

		await expect(
			checkoutPage.paymentTermLink(term1.label['en_US'])
		).toBeVisible();

		await checkoutPage.continueButton.click();

		await expect(checkoutPage.goToOrderDetailsButton).toBeVisible();
	}
);

test(
	'Default payment term can override other eligibilities',
	{tag: ['@COMMERCE-9782', '@LPD-85008']},
	async ({
		accountsPage,
		apiHelpers,
		checkoutPage,
		commerceAdminChannelDetailsPage,
		commerceAdminChannelsPage,
		commerceChannelDefaultsPage,
		commerceMiniCartPage,
		editAccountPage,
		page,
	}) => {
		const {account, buyerUser} = await createAccountWithBuyerUser(
			apiHelpers,
			site.id
		);

		const term1 = await apiHelpers.headlessCommerceAdminOrder.postTerm({
			type: 'payment-terms',
		});
		const term2 = await apiHelpers.headlessCommerceAdminOrder.postTerm({
			type: 'payment-terms',
		});
		await apiHelpers.headlessCommerceAdminOrder.postTerm({
			type: 'payment-terms',
		});
		await apiHelpers.headlessCommerceAdminOrder.postTerm({
			active: false,
			type: 'payment-terms',
		});

		await commerceAdminChannelsPage.goto();

		await (
			await commerceAdminChannelsPage.channelsTableRowLink(channel.name)
		).click();
		await commerceAdminChannelDetailsPage.activateChannelConfiguration(
			'Money Order',
			'Payment Methods'
		);
		await (
			await commerceAdminChannelDetailsPage.generalCommerceAdminChannelTableLink(
				'Money Order'
			)
		).click();
		await commerceAdminChannelDetailsPage.setEntryEligibility(
			'Specific Payment Terms',
			term1.label['en_US'],
			'Payment Methods',
			'Money Order'
		);

		await commerceAdminChannelsPage.addMoneyOrderPaymentTermEligibility(
			commerceAdminChannelDetailsPage,
			channel.name,
			term2.label['en_US']
		);

		await accountsPage.gotoAccountAdmin();

		await accountsPage.accountsTable.search(account.name);
		await accountsPage.accountNameLink(account.name).click();
		await editAccountPage.channelDefaultsLink.click();
		await commerceChannelDefaultsPage.defaultPaymentCommerceTermEntriesButton.click();

		await expect(async () => {
			await commerceChannelDefaultsPage.editFrameChannelSelect.selectOption(
				channel.name
			);
			await commerceChannelDefaultsPage.editFrameTermSelect.selectOption(
				String(term2.id)
			);
			await commerceChannelDefaultsPage.editFrameOverrideCheckbox.check();
			await commerceChannelDefaultsPage.editFrameSaveButton.click();

			await expect(
				commerceChannelDefaultsPage.defaultPaymentCommerceTermEntriesCell(
					term2.label['en_US']
				)
			).toBeVisible({timeout: 500});
		}).toPass({timeout: 5000});

		await performUserSwitch(page, buyerUser.alternateName);

		await page.goto(`/web${site.friendlyUrlPath}/catalog`);

		await commerceMiniCartPage.quickAddToCart('MIN55861');
		await commerceMiniCartPage.submitButton.click();

		await checkoutPage.addAddress({
			city: 'Test City',
			countryLabel: 'United States',
			name: 'Test Name',
			regionLabel: 'Florida',
			street: 'Test Street',
			zip: '12345',
		});
		await checkoutPage.continueButton.click();

		await page.waitForURL((url) => url.href.includes('shipping-method'));

		await checkoutPage.continueButton.click();

		await page.waitForURL((url) => url.href.includes('order-summary'));

		await expect(
			checkoutPage.paymentTermLink(term2.label['en_US'])
		).toBeVisible();

		await checkoutPage.continueButton.click();

		await expect(checkoutPage.goToOrderDetailsButton).toBeVisible();
	}
);

test(
	'Payment eligibility rules can persist at checkout',
	{tag: ['@COMMERCE-9785', '@LPD-85008']},
	async ({
		accountsPage,
		apiHelpers,
		checkoutPage,
		commerceAdminChannelDetailsPage,
		commerceAdminChannelsPage,
		commerceChannelDefaultsPage,
		commerceMiniCartPage,
		editAccountPage,
		page,
	}) => {
		const {account, buyerUser} = await createAccountWithBuyerUser(
			apiHelpers,
			site.id
		);

		const term1 = await apiHelpers.headlessCommerceAdminOrder.postTerm({
			type: 'payment-terms',
		});
		const term2 = await apiHelpers.headlessCommerceAdminOrder.postTerm({
			type: 'payment-terms',
		});
		await apiHelpers.headlessCommerceAdminOrder.postTerm({
			type: 'payment-terms',
		});
		const term4 = await apiHelpers.headlessCommerceAdminOrder.postTerm({
			active: false,
			type: 'payment-terms',
		});

		await commerceAdminChannelsPage.goto();

		await (
			await commerceAdminChannelsPage.channelsTableRowLink(channel.name)
		).click();
		await commerceAdminChannelDetailsPage.activateChannelConfiguration(
			'Money Order',
			'Payment Methods'
		);
		await (
			await commerceAdminChannelDetailsPage.generalCommerceAdminChannelTableLink(
				'Money Order'
			)
		).click();
		await commerceAdminChannelDetailsPage.setEntryEligibility(
			'Specific Payment Terms',
			term1.label['en_US'],
			'Payment Methods',
			'Money Order'
		);

		await commerceAdminChannelsPage.addMoneyOrderPaymentTermEligibility(
			commerceAdminChannelDetailsPage,
			channel.name,
			term2.label['en_US']
		);

		await accountsPage.gotoAccountAdmin();

		await accountsPage.accountsTable.search(account.name);
		await accountsPage.accountNameLink(account.name).click();
		await editAccountPage.channelDefaultsLink.click();
		await commerceChannelDefaultsPage.defaultPaymentCommerceTermEntriesButton.click();

		await expect(async () => {
			await commerceChannelDefaultsPage.editFrameChannelSelect.selectOption(
				channel.name
			);
			await commerceChannelDefaultsPage.editFrameTermSelect.selectOption(
				String(term2.id)
			);
			await commerceChannelDefaultsPage.editFrameSaveButton.click();

			await expect(
				commerceChannelDefaultsPage.defaultPaymentCommerceTermEntriesCell(
					term2.label['en_US']
				)
			).toBeVisible({timeout: 500});
		}).toPass({timeout: 5000});

		await performUserSwitch(page, buyerUser.alternateName);

		await page.goto(`/web${site.friendlyUrlPath}/catalog`);

		await commerceMiniCartPage.quickAddToCart('MIN55861');
		await commerceMiniCartPage.submitButton.click();

		await checkoutPage.addAddress({
			city: 'Test City',
			countryLabel: 'United States',
			name: 'Test Name',
			regionLabel: 'Florida',
			street: 'Test Street',
			zip: '12345',
		});
		await checkoutPage.continueButton.click();

		await page.waitForURL((url) => url.href.includes('shipping-method'));

		await checkoutPage.continueButton.click();

		await page.waitForURL((url) => url.href.includes('payment-terms'));

		await expect(
			checkoutPage.paymentTermOption(term2.label['en_US'])
		).toBeChecked();
		await expect(
			checkoutPage.paymentTermOption(term1.label['en_US'])
		).toBeVisible();
		await expect(
			checkoutPage.paymentTermOption(term4.label['en_US'])
		).not.toBeVisible();

		await checkoutPage.continueButton.click();

		await page.waitForURL((url) => url.href.includes('order-summary'));

		await checkoutPage.continueButton.click();

		await expect(checkoutPage.goToOrderDetailsButton).toBeVisible();
	}
);

test(
	'Can set default billing address per channel',
	{tag: ['@COMMERCE-9733', '@LPD-85008']},
	async ({
		accountsPage,
		apiHelpers,
		checkoutPage,
		commerceMiniCartPage,
		editAccountChannelDefaultsPage,
		editAccountPage,
		page,
	}) => {
		const {account, buyerUser} = await createAccountWithBuyerUser(
			apiHelpers,
			site.id
		);

		const billingAddress =
			await apiHelpers.headlessCommerceAdminAccount.postAddress(
				account.id,
				{
					defaultBilling: false,
					defaultShipping: false,
					name: 'Billing Address Entry ' + getRandomInt(),
					type: 1,
				}
			);

		apiHelpers.data.push({id: billingAddress.id, type: 'address'});

		const shippingAddress =
			await apiHelpers.headlessCommerceAdminAccount.postAddress(
				account.id,
				{
					defaultBilling: false,
					defaultShipping: false,
					name: 'Shipping Address Entry ' + getRandomInt(),
					type: 3,
				}
			);

		apiHelpers.data.push({id: shippingAddress.id, type: 'address'});

		const billingAndShippingAddress =
			await apiHelpers.headlessCommerceAdminAccount.postAddress(
				account.id,
				{
					defaultBilling: false,
					defaultShipping: false,
					name:
						'Billing and Shipping Address Entry ' + getRandomInt(),
					type: 2,
				}
			);

		apiHelpers.data.push({
			id: billingAndShippingAddress.id,
			type: 'address',
		});

		const testChannel1 =
			await apiHelpers.headlessCommerceAdminChannel.postChannel({
				name: 'Test Channel 1 ' + getRandomInt(),
			});
		const testChannel2 =
			await apiHelpers.headlessCommerceAdminChannel.postChannel({
				name: 'Test Channel 2 ' + getRandomInt(),
			});

		await accountsPage.gotoAccountAdmin();

		await accountsPage.accountsTable.search(account.name);
		await accountsPage.accountNameLink(account.name).click();
		await editAccountPage.channelDefaultsLink.click();
		await editAccountChannelDefaultsPage.addDefaultBillingAddressButton.click();

		await expect(
			editAccountChannelDefaultsPage.setDefaultAddressFrameChannelDropdownMenu
		).toBeVisible();

		const channelOptions = (
			await editAccountChannelDefaultsPage.setDefaultAddressFrameChannelDropdownOptions.allTextContents()
		).map((s) => s.trim());

		expect(channelOptions).toContain('All Channels');
		expect(channelOptions).toContain(channel.name);
		expect(channelOptions).toContain(testChannel1.name);
		expect(channelOptions).toContain(testChannel2.name);

		const addressOptions = (
			await editAccountChannelDefaultsPage.setDefaultBillingAddressFrameBillingAddressDropdownOptions.allTextContents()
		).map((s) => s.trim());

		expect(addressOptions).toContain(billingAddress.name);
		expect(addressOptions).toContain(billingAndShippingAddress.name);
		expect(addressOptions).not.toContain(shippingAddress.name);

		await page.keyboard.press('Escape');

		await expect(async () => {
			await editAccountChannelDefaultsPage.addDefaultBillingAddressButton.click();
			await editAccountChannelDefaultsPage.setDefaultAddressFrameChannelDropdownMenu.selectOption(
				channel.name
			);
			await editAccountChannelDefaultsPage.setDefaultBillingAddressFrameBillingAddressDropdownMenu.selectOption(
				billingAddress.name
			);
			await editAccountChannelDefaultsPage.modalSaveButton.click();

			await expect(
				await editAccountChannelDefaultsPage.addressTableRowColumn(
					1,
					'Billing',
					billingAddress.name
				)
			).toBeVisible({timeout: 5000});
		}).toPass({timeout: 20000});

		await editAccountChannelDefaultsPage.addDefaultBillingAddressButton.click();

		await expect(
			editAccountChannelDefaultsPage.setDefaultAddressFrameChannelDropdownMenu
		).toBeVisible();

		const remainingOptions = (
			await editAccountChannelDefaultsPage.setDefaultAddressFrameChannelDropdownOptions.allTextContents()
		).map((s) => s.trim());

		expect(remainingOptions).toContain('All Other Channels');
		expect(remainingOptions).toContain(testChannel1.name);
		expect(remainingOptions).toContain(testChannel2.name);
		expect(remainingOptions).not.toContain('All Channels');

		await page.keyboard.press('Escape');

		await performUserSwitch(page, buyerUser.alternateName);

		await page.goto(`/web${site.friendlyUrlPath}/catalog`);

		await commerceMiniCartPage.quickAddToCart('MIN55861');
		await commerceMiniCartPage.submitButton.click();

		await page.waitForURL((url) => url.href.includes('shipping-address'));

		await expect(async () => {
			await checkoutPage.shippingAddressSelect.selectOption(
				{
					label: shippingAddress.name,
				},
				{timeout: 5000}
			);
			await checkoutPage.useAsBillingCheckbox.setChecked(false, {
				timeout: 5000,
			});
			await checkoutPage.continueButton.click({timeout: 5000});

			await page.waitForURL(
				(url) => url.href.includes('shipping-method'),
				{timeout: 5000}
			);
		}).toPass({timeout: 30000});

		await checkoutPage.continueButton.click();

		await page.waitForURL((url) => url.href.includes('billing-address'));

		await expect(checkoutPage.nameInput).toBeDisabled();
		await expect(checkoutPage.nameInput).toHaveValue(billingAddress.name);
		await expect(checkoutPage.addressInput).toBeDisabled();
		await expect(checkoutPage.zipInput).toBeDisabled();
		await expect(checkoutPage.cityInput).toBeDisabled();

		await checkoutPage.continueButton.click();

		await page.waitForURL((url) => url.href.includes('order-summary'));

		await expect(checkoutPage.commerceBillingAddress).toContainText(
			billingAddress.name
		);
		await expect(checkoutPage.commerceShippingAddress).toContainText(
			shippingAddress.name
		);

		await checkoutPage.continueButton.click();

		await expect(checkoutPage.orderSuccessMessage).toBeVisible();
	}
);

test(
	'Can set default shipping address per channel',
	{tag: ['@COMMERCE-9732', '@LPD-85008']},
	async ({
		accountsPage,
		apiHelpers,
		checkoutPage,
		commerceMiniCartPage,
		editAccountChannelDefaultsPage,
		editAccountPage,
		page,
	}) => {
		const {account, buyerUser} = await createAccountWithBuyerUser(
			apiHelpers,
			site.id
		);

		const billingAddress =
			await apiHelpers.headlessCommerceAdminAccount.postAddress(
				account.id,
				{
					defaultBilling: false,
					defaultShipping: false,
					name: 'Billing Address Entry ' + getRandomInt(),
					type: 1,
				}
			);

		apiHelpers.data.push({id: billingAddress.id, type: 'address'});

		const shippingAddress =
			await apiHelpers.headlessCommerceAdminAccount.postAddress(
				account.id,
				{
					defaultBilling: false,
					defaultShipping: false,
					name: 'Shipping Address Entry ' + getRandomInt(),
					type: 3,
				}
			);

		apiHelpers.data.push({id: shippingAddress.id, type: 'address'});

		const billingAndShippingAddress =
			await apiHelpers.headlessCommerceAdminAccount.postAddress(
				account.id,
				{
					defaultBilling: false,
					defaultShipping: false,
					name:
						'Billing and Shipping Address Entry ' + getRandomInt(),
					type: 2,
				}
			);

		apiHelpers.data.push({
			id: billingAndShippingAddress.id,
			type: 'address',
		});

		const testChannel1 =
			await apiHelpers.headlessCommerceAdminChannel.postChannel({
				name: 'Test Channel 1 ' + getRandomInt(),
			});
		const testChannel2 =
			await apiHelpers.headlessCommerceAdminChannel.postChannel({
				name: 'Test Channel 2 ' + getRandomInt(),
			});

		await accountsPage.gotoAccountAdmin();

		await accountsPage.accountsTable.search(account.name);
		await accountsPage.accountNameLink(account.name).click();
		await editAccountPage.channelDefaultsLink.click();
		await editAccountChannelDefaultsPage.addDefaultShippingAddressButton.click();

		await expect(
			editAccountChannelDefaultsPage.setDefaultAddressFrameChannelDropdownMenu
		).toBeVisible();

		const channelOptions = (
			await editAccountChannelDefaultsPage.setDefaultAddressFrameChannelDropdownOptions.allTextContents()
		).map((s) => s.trim());

		expect(channelOptions).toContain('All Channels');
		expect(channelOptions).toContain(channel.name);
		expect(channelOptions).toContain(testChannel1.name);
		expect(channelOptions).toContain(testChannel2.name);

		const addressOptions = (
			await editAccountChannelDefaultsPage.setDefaultShippingAddressFrameBillingAddressDropdownOptions.allTextContents()
		).map((s) => s.trim());

		expect(addressOptions).toContain(shippingAddress.name);
		expect(addressOptions).toContain(billingAndShippingAddress.name);
		expect(addressOptions).not.toContain(billingAddress.name);

		await page.keyboard.press('Escape');

		await expect(async () => {
			await editAccountChannelDefaultsPage.addDefaultShippingAddressButton.click();
			await editAccountChannelDefaultsPage.setDefaultAddressFrameChannelDropdownMenu.selectOption(
				channel.name
			);
			await editAccountChannelDefaultsPage.setDefaultShippingAddressFrameBillingAddressDropdownMenu.selectOption(
				shippingAddress.name
			);
			await editAccountChannelDefaultsPage.modalSaveButton.click();

			await expect(
				await editAccountChannelDefaultsPage.addressTableRowColumn(
					1,
					'Shipping',
					shippingAddress.name
				)
			).toBeVisible({timeout: 5000});
		}).toPass({timeout: 20000});

		await editAccountChannelDefaultsPage.addDefaultShippingAddressButton.click();

		await expect(
			editAccountChannelDefaultsPage.setDefaultAddressFrameChannelDropdownMenu
		).toBeVisible();

		const remainingOptions = (
			await editAccountChannelDefaultsPage.setDefaultAddressFrameChannelDropdownOptions.allTextContents()
		).map((s) => s.trim());

		expect(remainingOptions).toContain('All Other Channels');
		expect(remainingOptions).toContain(testChannel1.name);
		expect(remainingOptions).toContain(testChannel2.name);
		expect(remainingOptions).not.toContain('All Channels');

		await page.keyboard.press('Escape');

		await performUserSwitch(page, buyerUser.alternateName);

		await page.goto(`/web${site.friendlyUrlPath}/catalog`);

		await commerceMiniCartPage.quickAddToCart('MIN55861');
		await commerceMiniCartPage.submitButton.click();

		await expect(checkoutPage.nameInput).toBeDisabled();
		await expect(checkoutPage.nameInput).toHaveValue(shippingAddress.name);
		await expect(checkoutPage.addressInput).toBeDisabled();
		await expect(checkoutPage.zipInput).toBeDisabled();
		await expect(checkoutPage.cityInput).toBeDisabled();

		await checkoutPage.useAsBillingCheckbox.setChecked(true);
		await checkoutPage.continueButton.click();

		await page.waitForURL((url) => url.href.includes('shipping-method'));

		await checkoutPage.continueButton.click();

		await page.waitForURL((url) => url.href.includes('order-summary'));

		await expect(checkoutPage.commerceShippingAddress).toContainText(
			shippingAddress.name
		);

		await checkoutPage.continueButton.click();

		await expect(checkoutPage.orderSuccessMessage).toBeVisible();
	}
);

test(
	'Can select only All Other Channels for default billing address',
	{tag: ['@COMMERCE-9829', '@LPD-85008']},
	async ({
		accountsPage,
		apiHelpers,
		editAccountChannelDefaultsPage,
		editAccountPage,
		page,
	}) => {
		const {account} = await createAccountWithBuyerUser(apiHelpers, site.id);

		const billingAddress =
			await apiHelpers.headlessCommerceAdminAccount.postAddress(
				account.id,
				{
					defaultBilling: false,
					defaultShipping: false,
					name: 'Billing Address Entry ' + getRandomInt(),
					type: 1,
				}
			);

		apiHelpers.data.push({id: billingAddress.id, type: 'address'});

		const testChannel1 =
			await apiHelpers.headlessCommerceAdminChannel.postChannel({
				name: 'Test Channel 1 ' + getRandomInt(),
			});
		const testChannel2 =
			await apiHelpers.headlessCommerceAdminChannel.postChannel({
				name: 'Test Channel 2 ' + getRandomInt(),
			});

		await accountsPage.gotoAccountAdmin();

		await accountsPage.accountsTable.search(account.name);
		await accountsPage.accountNameLink(account.name).click();
		await editAccountPage.channelDefaultsLink.click();

		for (const channelName of [
			channel.name,
			testChannel1.name,
			testChannel2.name,
		]) {
			await expect(async () => {
				await editAccountChannelDefaultsPage.addDefaultBillingAddressButton.click();
				await editAccountChannelDefaultsPage.setDefaultAddressFrameChannelDropdownMenu.selectOption(
					channelName
				);
				await editAccountChannelDefaultsPage.setDefaultBillingAddressFrameBillingAddressDropdownMenu.selectOption(
					billingAddress.name
				);
				await editAccountChannelDefaultsPage.modalSaveButton.click();

				await expect(
					await editAccountChannelDefaultsPage.addressTableRowColumn(
						0,
						'Billing',
						channelName
					)
				).toBeVisible({timeout: 5000});
			}).toPass({timeout: 20000});
		}

		await editAccountChannelDefaultsPage.addDefaultBillingAddressButton.click();

		await expect(
			editAccountChannelDefaultsPage.setDefaultAddressFrameChannelDropdownMenu
		).toBeVisible();

		const options = (
			await editAccountChannelDefaultsPage.setDefaultAddressFrameChannelDropdownOptions.allTextContents()
		)
			.map((s) => s.trim())
			.filter(Boolean);

		expect(options).toContain('All Other Channels');
		expect(options).not.toContain('All Channels');

		await page.keyboard.press('Escape');
	}
);

test(
	'Can select only All Other Channels for default shipping address',
	{tag: ['@COMMERCE-9829', '@LPD-85008']},
	async ({
		accountsPage,
		apiHelpers,
		editAccountChannelDefaultsPage,
		editAccountPage,
		page,
	}) => {
		const {account} = await createAccountWithBuyerUser(apiHelpers, site.id);

		const shippingAddress =
			await apiHelpers.headlessCommerceAdminAccount.postAddress(
				account.id,
				{
					defaultBilling: false,
					defaultShipping: false,
					name: 'Shipping Address Entry ' + getRandomInt(),
					type: 3,
				}
			);

		apiHelpers.data.push({id: shippingAddress.id, type: 'address'});

		const testChannel1 =
			await apiHelpers.headlessCommerceAdminChannel.postChannel({
				name: 'Test Channel 1 ' + getRandomInt(),
			});
		const testChannel2 =
			await apiHelpers.headlessCommerceAdminChannel.postChannel({
				name: 'Test Channel 2 ' + getRandomInt(),
			});

		await accountsPage.gotoAccountAdmin();

		await accountsPage.accountsTable.search(account.name);
		await accountsPage.accountNameLink(account.name).click();
		await editAccountPage.channelDefaultsLink.click();

		for (const channelName of [
			channel.name,
			testChannel1.name,
			testChannel2.name,
		]) {
			await expect(async () => {
				await editAccountChannelDefaultsPage.addDefaultShippingAddressButton.click();
				await editAccountChannelDefaultsPage.setDefaultAddressFrameChannelDropdownMenu.selectOption(
					channelName
				);
				await editAccountChannelDefaultsPage.setDefaultShippingAddressFrameBillingAddressDropdownMenu.selectOption(
					shippingAddress.name
				);
				await editAccountChannelDefaultsPage.modalSaveButton.click();

				await expect(
					await editAccountChannelDefaultsPage.addressTableRowColumn(
						0,
						'Shipping',
						channelName
					)
				).toBeVisible({timeout: 5000});
			}).toPass({timeout: 20000});
		}

		await editAccountChannelDefaultsPage.addDefaultShippingAddressButton.click();

		await expect(
			editAccountChannelDefaultsPage.setDefaultAddressFrameChannelDropdownMenu
		).toBeVisible();

		const options = (
			await editAccountChannelDefaultsPage.setDefaultAddressFrameChannelDropdownOptions.allTextContents()
		)
			.map((s) => s.trim())
			.filter(Boolean);

		expect(options).toContain('All Other Channels');
		expect(options).not.toContain('All Channels');

		await page.keyboard.press('Escape');
	}
);

test(
	'Cannot assign default addresses to the same channel',
	{tag: ['@COMMERCE-9885', '@LPD-85008']},
	async ({
		accountsPage,
		apiHelpers,
		editAccountChannelDefaultsPage,
		editAccountPage,
	}) => {
		const {account} = await createAccountWithBuyerUser(apiHelpers, site.id);

		const billingAddress =
			await apiHelpers.headlessCommerceAdminAccount.postAddress(
				account.id,
				{
					defaultBilling: false,
					defaultShipping: false,
					name: 'Test Billing Address ' + getRandomInt(),
					type: 1,
				}
			);

		apiHelpers.data.push({id: billingAddress.id, type: 'address'});

		const billingAndShippingAddress =
			await apiHelpers.headlessCommerceAdminAccount.postAddress(
				account.id,
				{
					defaultBilling: false,
					defaultShipping: false,
					name: 'Test Billing and Shipping Address ' + getRandomInt(),
					type: 2,
				}
			);

		apiHelpers.data.push({
			id: billingAndShippingAddress.id,
			type: 'address',
		});

		await apiHelpers.headlessCommerceAdminChannel.postChannel({
			name: 'Test Channel 1 ' + getRandomInt(),
		});
		await apiHelpers.headlessCommerceAdminChannel.postChannel({
			name: 'Test Channel 2 ' + getRandomInt(),
		});

		await accountsPage.gotoAccountAdmin();

		await accountsPage.accountsTable.search(account.name);
		await accountsPage.accountNameLink(account.name).click();
		await editAccountPage.channelDefaultsLink.click();

		await expect(async () => {
			await editAccountChannelDefaultsPage.addDefaultBillingAddressButton.click();
			await editAccountChannelDefaultsPage.setDefaultAddressFrameChannelDropdownMenu.selectOption(
				channel.name
			);
			await editAccountChannelDefaultsPage.setDefaultBillingAddressFrameBillingAddressDropdownMenu.selectOption(
				billingAddress.name
			);
			await editAccountChannelDefaultsPage.modalSaveButton.click();

			await expect(
				await editAccountChannelDefaultsPage.addressTableRowColumn(
					0,
					'Billing',
					channel.name
				)
			).toBeVisible({timeout: 5000});
		}).toPass({timeout: 20000});

		await expect(async () => {
			await editAccountChannelDefaultsPage.addDefaultBillingAddressButton.click();
			await editAccountChannelDefaultsPage.setDefaultAddressFrameChannelDropdownMenu.selectOption(
				'All Other Channels'
			);
			await editAccountChannelDefaultsPage.setDefaultBillingAddressFrameBillingAddressDropdownMenu.selectOption(
				billingAndShippingAddress.name
			);
			await editAccountChannelDefaultsPage.modalSaveButton.click();

			await expect(
				editAccountChannelDefaultsPage.billingAddressAllOtherChannelsText
			).toBeVisible({timeout: 5000});
		}).toPass({timeout: 20000});

		await editAccountChannelDefaultsPage.addDefaultBillingAddressButton.click();
		await editAccountChannelDefaultsPage.setDefaultAddressFrameChannelDropdownMenu.selectOption(
			'All Other Channels'
		);
		await editAccountChannelDefaultsPage.setDefaultBillingAddressFrameBillingAddressDropdownMenu.selectOption(
			billingAddress.name
		);
		await editAccountChannelDefaultsPage.modalSaveButton.click();

		await expect(
			editAccountChannelDefaultsPage.modalContainer.getByText(
				'There is already an address defined for the selected channel.'
			)
		).toBeVisible();
	}
);

test(
	'Can remove default addresses',
	{tag: ['@COMMERCE-9876', '@LPD-85008']},
	async ({
		accountsPage,
		apiHelpers,
		checkoutPage,
		commerceMiniCartPage,
		editAccountChannelDefaultsPage,
		editAccountPage,
		page,
	}) => {
		const {account, buyerUser} = await createAccountWithBuyerUser(
			apiHelpers,
			site.id
		);

		const billingAddress =
			await apiHelpers.headlessCommerceAdminAccount.postAddress(
				account.id,
				{
					defaultBilling: false,
					defaultShipping: false,
					name: 'Billing Address Entry ' + getRandomInt(),
					type: 1,
				}
			);

		apiHelpers.data.push({id: billingAddress.id, type: 'address'});

		const shippingAddress =
			await apiHelpers.headlessCommerceAdminAccount.postAddress(
				account.id,
				{
					defaultBilling: false,
					defaultShipping: false,
					name: 'Shipping Address Entry ' + getRandomInt(),
					type: 3,
				}
			);

		apiHelpers.data.push({id: shippingAddress.id, type: 'address'});

		const testChannel1 =
			await apiHelpers.headlessCommerceAdminChannel.postChannel({
				name: 'Test Channel 1 ' + getRandomInt(),
			});
		const testChannel2 =
			await apiHelpers.headlessCommerceAdminChannel.postChannel({
				name: 'Test Channel 2 ' + getRandomInt(),
			});

		await accountsPage.gotoAccountAdmin();

		await accountsPage.accountsTable.search(account.name);
		await accountsPage.accountNameLink(account.name).click();
		await editAccountPage.channelDefaultsLink.click();

		await expect(async () => {
			await editAccountChannelDefaultsPage.addDefaultBillingAddressButton.click();
			await editAccountChannelDefaultsPage.setDefaultAddressFrameChannelDropdownMenu.selectOption(
				testChannel1.name
			);
			await editAccountChannelDefaultsPage.setDefaultBillingAddressFrameBillingAddressDropdownMenu.selectOption(
				billingAddress.name
			);
			await editAccountChannelDefaultsPage.modalSaveButton.click();

			await expect(
				await editAccountChannelDefaultsPage.addressTableRowColumn(
					0,
					'Billing',
					testChannel1.name
				)
			).toBeVisible({timeout: 5000});
		}).toPass({timeout: 20000});

		await expect(async () => {
			await editAccountChannelDefaultsPage.addDefaultShippingAddressButton.click();
			await editAccountChannelDefaultsPage.setDefaultAddressFrameChannelDropdownMenu.selectOption(
				testChannel2.name
			);
			await editAccountChannelDefaultsPage.setDefaultShippingAddressFrameBillingAddressDropdownMenu.selectOption(
				shippingAddress.name
			);
			await editAccountChannelDefaultsPage.modalSaveButton.click();

			await expect(
				await editAccountChannelDefaultsPage.addressTableRowColumn(
					0,
					'Shipping',
					testChannel2.name
				)
			).toBeVisible({timeout: 5000});
		}).toPass({timeout: 20000});

		await (
			await editAccountChannelDefaultsPage.addressTableRowColumn(
				3,
				'Billing',
				billingAddress.name
			)
		).click();
		await editAccountChannelDefaultsPage.deleteMenuItem.click();

		await waitForAlert(page);

		await (
			await editAccountChannelDefaultsPage.addressTableRowColumn(
				3,
				'Shipping',
				shippingAddress.name
			)
		).click();
		await editAccountChannelDefaultsPage.deleteMenuItem.click();

		await waitForAlert(page);

		await performUserSwitch(page, buyerUser.alternateName);

		await page.goto(`/web${site.friendlyUrlPath}/catalog`);

		await commerceMiniCartPage.quickAddToCart('MIN55861');
		await commerceMiniCartPage.submitButton.click();

		await expect(checkoutPage.nameInput).not.toBeDisabled();
		await expect(checkoutPage.addressInput).not.toBeDisabled();
		await expect(checkoutPage.zipInput).not.toBeDisabled();
		await expect(checkoutPage.cityInput).not.toBeDisabled();

		const newAddressName = 'New Address ' + getRandomInt();

		await checkoutPage.addAddress({
			city: 'Test City',
			countryLabel: 'United States',
			name: newAddressName,
			regionLabel: 'Florida',
			street: 'Test Address',
			zip: '12345',
		});
		await checkoutPage.continueButton.click();

		await page.waitForURL((url) => url.href.includes('shipping-method'));

		await checkoutPage.continueButton.click();

		await page.waitForURL((url) => url.href.includes('order-summary'));

		await expect(checkoutPage.commerceShippingAddress).toContainText(
			newAddressName
		);

		await checkoutPage.continueButton.click();

		await expect(checkoutPage.orderSuccessMessage).toBeVisible();
	}
);

test(
	'Cannot use default addresses in a different channel',
	{tag: ['@COMMERCE-9734', '@LPD-85008']},
	async ({
		accountsPage,
		apiHelpers,
		checkoutPage,
		commerceMiniCartPage,
		editAccountChannelDefaultsPage,
		editAccountPage,
		page,
	}) => {
		const otherChannel =
			await apiHelpers.headlessCommerceAdminChannel.postChannel({
				name: 'Other Channel ' + getRandomInt(),
			});

		const {account, buyerUser} = await createAccountWithBuyerUser(
			apiHelpers,
			site.id
		);

		const billingAddress =
			await apiHelpers.headlessCommerceAdminAccount.postAddress(
				account.id,
				{
					defaultBilling: false,
					defaultShipping: false,
					name: 'Billing Address Entry ' + getRandomInt(),
					type: 1,
				}
			);

		apiHelpers.data.push({id: billingAddress.id, type: 'address'});

		const shippingAddress =
			await apiHelpers.headlessCommerceAdminAccount.postAddress(
				account.id,
				{
					defaultBilling: false,
					defaultShipping: false,
					name: 'Shipping Address Entry ' + getRandomInt(),
					type: 3,
				}
			);

		apiHelpers.data.push({id: shippingAddress.id, type: 'address'});

		await accountsPage.gotoAccountAdmin();

		await accountsPage.accountsTable.search(account.name);
		await accountsPage.accountNameLink(account.name).click();
		await editAccountPage.channelDefaultsLink.click();

		await expect(async () => {
			await editAccountChannelDefaultsPage.addDefaultBillingAddressButton.click();
			await editAccountChannelDefaultsPage.setDefaultAddressFrameChannelDropdownMenu.selectOption(
				otherChannel.name
			);
			await editAccountChannelDefaultsPage.setDefaultBillingAddressFrameBillingAddressDropdownMenu.selectOption(
				billingAddress.name
			);
			await editAccountChannelDefaultsPage.modalSaveButton.click();

			await expect(
				await editAccountChannelDefaultsPage.addressTableRowColumn(
					1,
					'Billing',
					billingAddress.name
				)
			).toBeVisible({timeout: 5000});
		}).toPass({timeout: 20000});

		await expect(async () => {
			await editAccountChannelDefaultsPage.addDefaultShippingAddressButton.click();
			await editAccountChannelDefaultsPage.setDefaultAddressFrameChannelDropdownMenu.selectOption(
				otherChannel.name
			);
			await editAccountChannelDefaultsPage.setDefaultShippingAddressFrameBillingAddressDropdownMenu.selectOption(
				shippingAddress.name
			);
			await editAccountChannelDefaultsPage.modalSaveButton.click();

			await expect(
				await editAccountChannelDefaultsPage.addressTableRowColumn(
					1,
					'Shipping',
					shippingAddress.name
				)
			).toBeVisible({timeout: 5000});
		}).toPass({timeout: 20000});

		await performUserSwitch(page, buyerUser.alternateName);

		await page.goto(`/web${site.friendlyUrlPath}/catalog`);

		await commerceMiniCartPage.quickAddToCart('MIN55861');
		await commerceMiniCartPage.submitButton.click();

		await expect(checkoutPage.nameInput).not.toBeDisabled();
		await expect(checkoutPage.addressInput).not.toBeDisabled();
		await expect(checkoutPage.zipInput).not.toBeDisabled();
		await expect(checkoutPage.cityInput).not.toBeDisabled();

		const newShippingName = 'New Shipping Address ' + getRandomInt();
		const newBillingName = 'New Billing Address ' + getRandomInt();

		await checkoutPage.addAddress({
			city: 'Test Shipping City',
			countryLabel: 'United States',
			name: newShippingName,
			regionLabel: 'Florida',
			street: 'Test Shipping Address',
			useAsBilling: false,
			zip: '12345',
		});
		await checkoutPage.continueButton.click();

		await page.waitForURL((url) => url.href.includes('shipping-method'));

		await checkoutPage.continueButton.click();

		await page.waitForURL((url) => url.href.includes('billing-address'));

		await checkoutPage.cityInput.fill('Test Billing City');
		await checkoutPage.countryInput.selectOption({label: 'United States'});
		await checkoutPage.nameInput.fill(newBillingName);
		await checkoutPage.regionInput.selectOption({label: 'Florida'});
		await checkoutPage.addressInput.fill('Test Billing Address');
		await checkoutPage.zipInput.fill('67890');

		await checkoutPage.continueButton.click();

		await page.waitForURL((url) => url.href.includes('order-summary'));

		await expect(checkoutPage.commerceShippingAddress).toContainText(
			newShippingName
		);
		await expect(checkoutPage.commerceBillingAddress).toContainText(
			newBillingName
		);

		await checkoutPage.continueButton.click();

		await expect(checkoutPage.orderSuccessMessage).toBeVisible();
	}
);
