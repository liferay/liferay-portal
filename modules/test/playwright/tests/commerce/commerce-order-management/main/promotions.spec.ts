/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {commercePagesTest} from '../../../../fixtures/commercePagesTest';
import {dataApiHelpersTest} from '../../../../fixtures/dataApiHelpersTest';
import {loginTest} from '../../../../fixtures/loginTest';
import {DataApiHelpers} from '../../../../helpers/ApiHelpers';
import getRandomString from '../../../../utils/getRandomString';
import {performLoginViaApi} from '../../../../utils/performLogin';
import {miniumSetUp} from '../../utils/commerce';

export const test = mergeTests(
	commercePagesTest,
	dataApiHelpersTest,
	loginTest()
);

let catalog: {id: number; name: string};
let channel: {id: number; name: string};
let setupData: Array<{id: number | string; type: string}>;
let sku: {id: number; sku: string};

test.beforeAll(async ({browser}) => {
	const page = await browser.newPage();

	await performLoginViaApi({page, screenName: 'test'});

	const apiHelpers = new DataApiHelpers(page);

	const miniumResult = await miniumSetUp(apiHelpers);

	catalog = miniumResult.catalog;
	channel = miniumResult.channel;
	setupData = [...apiHelpers.data];

	const products =
		await apiHelpers.headlessCommerceAdminCatalog.getProductsPage(
			100,
			'ABS Sensor'
		);

	const product = products.items.find(
		(item: {catalogId: number; name: {en_US: string}}) =>
			item.catalogId === catalog.id && item.name.en_US === 'ABS Sensor'
	);

	sku = (
		await apiHelpers.headlessCommerceAdminCatalog.getProduct(
			product.productId
		)
	).skus[0];

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

test(
	'Item finder lists one entry per unit of measure of a SKU in a promotion',
	{tag: ['@COMMERCE-12302', '@LPD-105579']},
	async ({
		apiHelpers,
		commerceAdminPriceListDetailsPage,
		commerceAdminPromotionsPage,
	}) => {
		const unitOfMeasures = [
			{basePrice: 10, key: 'Liter'},
			{basePrice: 20, key: 'Gallon'},
		];

		for (const [index, unitOfMeasure] of unitOfMeasures.entries()) {
			await apiHelpers.headlessCommerceAdminCatalog.postSkuUnitOfMeasure(
				sku.id,
				{
					basePrice: unitOfMeasure.basePrice,
					key: unitOfMeasure.key,
					name: {en_US: unitOfMeasure.key},
					primary: index === 0,
					priority: index + 1,
				}
			);
		}

		await commerceAdminPromotionsPage.goto();

		await (
			await commerceAdminPromotionsPage.tableRowLink({
				colIndex: 0,
				rowValue: `${catalog.name} Base Promotion`,
			})
		).click();

		await commerceAdminPriceListDetailsPage.entriesTab.click();

		await expect(async () => {
			await commerceAdminPriceListDetailsPage.findSkuInput.fill('');
			await commerceAdminPriceListDetailsPage.findSkuInput.fill(sku.sku);

			await expect(
				commerceAdminPriceListDetailsPage.itemFinderRows
			).toHaveCount(unitOfMeasures.length, {timeout: 2000});
		}).toPass({timeout: 30000});

		for (const [index, unitOfMeasure] of unitOfMeasures.entries()) {
			await expect(
				commerceAdminPriceListDetailsPage.itemFinderRows.nth(index)
			).toContainText(unitOfMeasure.key);
		}
	}
);

test(
	'Cannot link the same eligibility entry twice to a promotion',
	{tag: ['@COMMERCE-10053', '@LPD-105579']},
	async ({
		apiHelpers,
		commerceAdminPriceListDetailsPage,
		commerceAdminPromotionsPage,
		page,
	}) => {
		const randomString = getRandomString().slice(0, 8);
		const accountName = `Test Account Run${randomString}`;
		const promotionName = `Test Promotion Run${randomString}`;

		await apiHelpers.headlessAdminUser.postAccount({
			name: accountName,
			type: 'business',
		});

		const orderType =
			await apiHelpers.headlessCommerceAdminOrder.postOrderType({
				active: true,
				name: {en_US: `Test Order Type Run${randomString}`},
			});

		await apiHelpers.headlessCommerceAdminPricing.postPriceList({
			catalogId: catalog.id,
			currencyCode: 'USD',
			name: promotionName,
			type: 'promotion',
		});

		await commerceAdminPromotionsPage.goto();

		await (
			await commerceAdminPromotionsPage.tableRowLink({
				colIndex: 0,
				rowValue: promotionName,
			})
		).click();

		await commerceAdminPriceListDetailsPage.eligibilityTab.click();

		const addThenAssertDisabled = async (
			placeholder: string,
			searchTerm: string,
			entryName: string
		) => {
			await expect(async () => {
				await commerceAdminPriceListDetailsPage
					.eligibilityFindInput(placeholder)
					.fill(searchTerm);

				await expect(
					commerceAdminPriceListDetailsPage.eligibilityRowSelectButton(
						entryName
					)
				).toBeEnabled({timeout: 2000});
			}).toPass({timeout: 30000});

			await commerceAdminPriceListDetailsPage
				.eligibilityRowSelectButton(entryName)
				.click();

			await commerceAdminPriceListDetailsPage
				.eligibilityFindInput(placeholder)
				.fill(searchTerm);

			await expect(
				commerceAdminPriceListDetailsPage.eligibilityRowSelectButton(
					entryName
				)
			).toBeDisabled();

			await page.keyboard.press('Escape');
		};

		await test.step('Channel cannot be linked twice', async () => {
			await commerceAdminPriceListDetailsPage.specificChannelsRadio.check();

			await addThenAssertDisabled(
				'Find a Channel',
				channel.name,
				channel.name
			);
		});

		await test.step('Account cannot be linked twice', async () => {
			await commerceAdminPriceListDetailsPage.specificAccountsRadio.check();

			await addThenAssertDisabled(
				'Find an Account',
				`Run${randomString}`,
				accountName
			);
		});

		await test.step('Order type cannot be linked twice', async () => {
			await commerceAdminPriceListDetailsPage.specificOrderTypesRadio.check();

			await addThenAssertDisabled(
				'Find an Order Type',
				`Run${randomString}`,
				orderType.name.en_US
			);
		});
	}
);
