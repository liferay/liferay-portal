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

		const gotoEligibilityTab = async () => {
			await commerceAdminPromotionsPage.goto();

			await (
				await commerceAdminPromotionsPage.tableRowLink({
					colIndex: 0,
					rowValue: promotionName,
				})
			).click();

			await commerceAdminPriceListDetailsPage.eligibilityTab.click();
		};

		await gotoEligibilityTab();

		const eligibilities = [
			{
				entryName: channel.name,
				errorMessage: 'The channel relation already exists.',
				label: 'Channel',
				placeholder: 'Find a Channel',
				radio: commerceAdminPriceListDetailsPage.specificChannelsRadio,
				searchTerm: channel.name,
			},
			{
				entryName: accountName,
				label: 'Account',
				placeholder: 'Find an Account',
				radio: commerceAdminPriceListDetailsPage.specificAccountsRadio,
				searchTerm: `Run${randomString}`,
			},
			{
				entryName: orderType.name.en_US,
				errorMessage: 'The order type relation already exists.',
				label: 'Order type',
				placeholder: 'Find an Order Type',
				radio: commerceAdminPriceListDetailsPage.specificOrderTypesRadio,
				searchTerm: `Run${randomString}`,
			},
		];

		const searchEligibility = async (
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
		};

		for (const eligibility of eligibilities) {
			await test.step(`${eligibility.label} cannot be selected twice in one session`, async () => {
				await eligibility.radio.check();

				await searchEligibility(
					eligibility.placeholder,
					eligibility.searchTerm,
					eligibility.entryName
				);

				await commerceAdminPriceListDetailsPage
					.eligibilityRowSelectButton(eligibility.entryName)
					.click();

				await commerceAdminPriceListDetailsPage
					.eligibilityFindInput(eligibility.placeholder)
					.fill(eligibility.searchTerm);

				await expect(
					commerceAdminPriceListDetailsPage.eligibilityRowSelectButton(
						eligibility.entryName
					)
				).toBeDisabled();

				await page.keyboard.press('Escape');
			});
		}

		const guardedEligibilities = eligibilities.filter(
			({errorMessage}) => errorMessage
		);

		for (const eligibility of guardedEligibilities) {
			await test.step(`Server rejects a duplicate ${eligibility.label.toLowerCase()} once the selection is cleared`, async () => {
				await gotoEligibilityTab();

				await eligibility.radio.check();

				await searchEligibility(
					eligibility.placeholder,
					eligibility.searchTerm,
					eligibility.entryName
				);

				await commerceAdminPriceListDetailsPage
					.eligibilityRowSelectButton(eligibility.entryName)
					.click();

				await expect(
					commerceAdminPriceListDetailsPage.errorAlert(
						eligibility.errorMessage
					)
				).toBeVisible();
			});
		}
	}
);
