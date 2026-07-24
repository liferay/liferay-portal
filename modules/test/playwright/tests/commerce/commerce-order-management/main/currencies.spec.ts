/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {commercePagesTest} from '../../../../fixtures/commercePagesTest';
import {dataApiHelpersTest} from '../../../../fixtures/dataApiHelpersTest';
import {loginTest} from '../../../../fixtures/loginTest';
import {DataApiHelpers} from '../../../../helpers/ApiHelpers';
import {getRandomInt} from '../../../../utils/getRandomInt';
import getRandomString from '../../../../utils/getRandomString';
import {
	performLoginViaApi,
	performUserSwitchViaApi,
} from '../../../../utils/performLogin';
import {waitForAlert} from '../../../../utils/waitForAlert';
import {createAccountWithBuyerUser, miniumSetUp} from '../../utils/commerce';

export const test = mergeTests(
	commercePagesTest,
	dataApiHelpersTest,
	loginTest()
);

const INDIAN_FORMAT_PATTERN = '₹ ##,##,##0.00';
const INDIAN_PRICE_REGEX = /₹\s\d{1,2}(?:,\d{2})*,\d{3}\.\d{2}/;

let catalog: {id: number};
let channel: {id: number; name: string};
let originalFormatPattern: string;
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
	setupData = [...apiHelpers.data];

	const currencies =
		await apiHelpers.headlessCommerceAdminCatalog.getCurrenciesPage(
			'Indian'
		);
	const indianRupee = currencies.items.find(
		(item: {code: string}) => item.code === 'INR'
	);

	originalFormatPattern = indianRupee.formatPattern;

	await page.close();
});

test.afterAll(async ({browser}) => {
	const page = await browser.newPage();

	await performLoginViaApi({page, screenName: 'test'});

	const apiHelpers = new DataApiHelpers(page);

	const currencies =
		await apiHelpers.headlessCommerceAdminCatalog.getCurrenciesPage(
			'Indian'
		);
	const indianRupee = currencies.items.find(
		(item: {code: string}) => item.code === 'INR'
	);

	await apiHelpers.headlessCommerceAdminCatalog.patchCurrency(
		indianRupee.id,
		{
			formatPattern: originalFormatPattern,
		} as any
	);

	apiHelpers.setData(setupData);

	await apiHelpers.clearData();

	await page.close();
});

test(
	'Update INR currency format and verify it applies to storefront prices',
	{tag: ['@COMMERCE-11393', '@LPD-85008']},
	async ({
		apiHelpers,
		commerceAdminChannelDetailsPage,
		commerceAdminChannelsPage,
		commerceAdminCurrenciesPage,
		commerceAdminCurrencyDetailsPage,
		commerceMiniCartPage,
		page,
		productDetailsPage,
	}) => {
		await test.step('Update Indian Rupee format pattern', async () => {
			await commerceAdminCurrenciesPage.goto();

			await commerceAdminCurrenciesPage
				.currencyNameLink('Indian Rupee')
				.click();

			await commerceAdminCurrencyDetailsPage.formatPatternInput.fill(
				INDIAN_FORMAT_PATTERN
			);
			await commerceAdminCurrencyDetailsPage.saveButton.click();

			await waitForAlert(page);
		});

		const productName = `Currency Test Product ${getRandomString()}`;

		await test.step('Create a product with a high base price', async () => {
			await apiHelpers.headlessCommerceAdminCatalog.postProduct({
				catalogId: catalog.id,
				name: {en_US: productName},
				skus: [
					{
						cost: 0,
						price: 1000000,
						published: true,
						purchasable: true,
						sku: `CT${getRandomInt()}`,
					},
				],
			});
		});

		await test.step('Set channel currency to Indian Rupee', async () => {
			await commerceAdminChannelsPage.goto();

			await (
				await commerceAdminChannelsPage.channelsTableRowLink(
					channel.name
				)
			).click();

			await commerceAdminChannelDetailsPage.changeChannelDefaultCurrency(
				'INR'
			);
		});

		const {buyerUser} = await createAccountWithBuyerUser(
			apiHelpers,
			site.id
		);

		await performUserSwitchViaApi(page, buyerUser.alternateName);

		await test.step('Product details page price is in Indian format', async () => {
			await page.goto(`/web${site.friendlyUrlPath}/p/${productName}`);

			await expect(productDetailsPage.priceContainer).toHaveText(
				INDIAN_PRICE_REGEX
			);
		});

		await test.step('Mini cart price is in Indian format', async () => {
			await productDetailsPage.addToCartButton.click();

			await commerceMiniCartPage.miniCartButton.click();

			await expect(
				commerceMiniCartPage.miniCartItem(productName)
			).toBeVisible();
			await expect(commerceMiniCartPage.miniCartTotalPrice).toHaveText(
				INDIAN_PRICE_REGEX
			);
		});
	}
);
