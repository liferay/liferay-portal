/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {commercePagesTest} from '../../../../fixtures/commercePagesTest';
import {dataApiHelpersTest} from '../../../../fixtures/dataApiHelpersTest';
import {loginTest} from '../../../../fixtures/loginTest';
import {DataApiHelpers} from '../../../../helpers/ApiHelpers';
import {CommerceAdminPriceListDetailsPage} from '../../../../pages/commerce/commerce-pricing-web/commerceAdminPriceListDetailsPage';
import {CommerceAdminPriceListsPage} from '../../../../pages/commerce/commerce-pricing-web/commerceAdminPriceListsPage';
import {performLoginViaApi} from '../../../../utils/performLogin';
import {waitForAlert} from '../../../../utils/waitForAlert';
import {miniumSetUp} from '../../utils/commerce';

export const test = mergeTests(
	commercePagesTest,
	dataApiHelpersTest,
	loginTest()
);

let basePriceList: {id: number; name: string};
let setupData: Array<{id: number | string; type: string}>;
let site: Site;

test.beforeAll(async ({browser}) => {
	const page = await browser.newPage();

	await performLoginViaApi({page, screenName: 'test'});

	const apiHelpers = new DataApiHelpers(page);

	const miniumResult = await miniumSetUp(apiHelpers);

	site = miniumResult.site;

	basePriceList = (
		await apiHelpers.headlessCommerceAdminPricing.getBasePriceList(
			miniumResult.catalog.id
		)
	).items[0];

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

async function getPriceEntry(
	apiHelpers: DataApiHelpers,
	skuId: number,
	unitOfMeasureKey?: string
) {
	const priceEntries =
		await apiHelpers.headlessCommerceAdminPricing.getPriceListEntries(
			basePriceList.id
		);

	return priceEntries.items.find(
		(priceEntry: {skuId: number; unitOfMeasureKey: string}) =>
			priceEntry.skuId === skuId &&
			(unitOfMeasureKey
				? priceEntry.unitOfMeasureKey === unitOfMeasureKey
				: !priceEntry.unitOfMeasureKey)
	);
}

async function goToPriceEntry(
	commerceAdminPriceListDetailsPage: CommerceAdminPriceListDetailsPage,
	commerceAdminPriceListsPage: CommerceAdminPriceListsPage,
	unitOfMeasureKey: string
) {
	await commerceAdminPriceListsPage.goto();

	await commerceAdminPriceListsPage.priceListLink(basePriceList.name).click();

	await commerceAdminPriceListDetailsPage.entriesTab.click();

	await commerceAdminPriceListDetailsPage.searchInput.fill('MIN93015');
	await commerceAdminPriceListDetailsPage.searchInput.press('Enter');

	await commerceAdminPriceListDetailsPage
		.priceEntryRowLink('MIN93015', unitOfMeasureKey)
		.click();
}

test(
	'Price table lists the unit of measure name, key, quantity and net price of every active unit of measure',
	{tag: ['@COMMERCE-12464', '@LPD-105705']},
	async ({apiHelpers, page, productDetailsPage}) => {
		const sku =
			await apiHelpers.headlessCommerceAdminCatalog.getSkuByName(
				'MIN93015'
			);

		for (const unitOfMeasure of [
			{
				active: false,
				basePrice: 3,
				key: 'Lt',
				name: {en_US: 'Liter'},
				priority: 1,
			},
			{basePrice: 10, key: 'Bt', name: {en_US: 'Bottle'}, priority: 2},
			{basePrice: 55, key: 'Cr', name: {en_US: 'Carrier'}, priority: 3},
		]) {
			await apiHelpers.headlessCommerceAdminCatalog.postSkuUnitOfMeasure(
				sku.id,
				{
					incrementalOrderQuantity: 1,
					precision: 1,
					primary: false,
					rate: 1,
					...unitOfMeasure,
				}
			);
		}

		await page.goto(`/web${site.friendlyUrlPath}/p/abs-sensor`);

		for (const columnName of ['Unit', 'Key', 'Quantity', 'Net Price']) {
			await expect(
				productDetailsPage.uomPriceTableColumnHeader(columnName)
			).toBeVisible();
		}

		await expect(productDetailsPage.uomPriceTableRows).toHaveCount(2);

		await expect(productDetailsPage.uomPriceTableRowCells(0)).toHaveText([
			'Bottle',
			'Bt',
			'1',
			'$ 10.00',
		]);
		await expect(productDetailsPage.uomPriceTableRowCells(1)).toHaveText([
			'Carrier',
			'Cr',
			'1',
			'$ 55.00',
		]);
		await expect(
			productDetailsPage.uomPriceTable.getByText('Liter')
		).toBeHidden();
	}
);

test(
	'Price table is not rendered for a SKU with a single unit of measure and no tier price',
	{tag: ['@COMMERCE-12465', '@LPD-105705']},
	async ({apiHelpers, page, productDetailsPage}) => {
		const sku =
			await apiHelpers.headlessCommerceAdminCatalog.getSkuByName(
				'MIN93015'
			);

		await apiHelpers.headlessCommerceAdminCatalog.postSkuUnitOfMeasure(
			sku.id,
			{
				basePrice: 10,
				incrementalOrderQuantity: 1,
				key: 'Bt',
				name: {en_US: 'Bottle'},
				precision: 1,
				primary: false,
				priority: 1,
				rate: 1,
			}
		);

		await page.goto(`/web${site.friendlyUrlPath}/p/abs-sensor`);

		await expect(
			await productDetailsPage.productNameHeading('ABS Sensor')
		).toBeVisible();
		await expect(productDetailsPage.uomPriceTable).toHaveCount(0);
		await expect(productDetailsPage.unitOfMeasureSelectedOption).toHaveText(
			'Bottle'
		);
		await expect(
			await productDetailsPage.skuField('MIN93015')
		).toBeVisible();
		await expect(
			await productDetailsPage.priceField(
				'$ 10.00 / Bottle',
				productDetailsPage.priceContainer
			)
		).toBeVisible();
	}
);

test(
	'Price table shows only quantity and net price for tier and bulk prices on a SKU without a unit of measure',
	{tag: ['@COMMERCE-12466', '@LPD-105705']},
	async ({apiHelpers, page, productDetailsPage}) => {
		const sku =
			await apiHelpers.headlessCommerceAdminCatalog.getSkuByName(
				'MIN93015'
			);

		const priceEntry = await getPriceEntry(apiHelpers, sku.id);

		await apiHelpers.headlessCommerceAdminPricing.patchPriceEntry(
			priceEntry.priceEntryId,
			{bulkPricing: false, price: 50}
		);

		for (const tierPrice of [
			{minimumQuantity: 5, price: 240},
			{minimumQuantity: 10, price: 450},
		]) {
			await apiHelpers.headlessCommerceAdminPricing.postTierPrice(
				priceEntry.priceEntryId,
				tierPrice
			);
		}

		await apiHelpers.headlessCommerceAdminPricing.patchPriceEntry(
			priceEntry.priceEntryId,
			{bulkPricing: true}
		);

		await apiHelpers.headlessCommerceAdminPricing.postTierPrice(
			priceEntry.priceEntryId,
			{minimumQuantity: 20, price: 900}
		);

		await page.goto(`/web${site.friendlyUrlPath}/p/abs-sensor`);

		for (const columnName of ['Quantity', 'Net Price']) {
			await expect(
				productDetailsPage.uomPriceTableColumnHeader(columnName)
			).toBeVisible();
		}

		for (const columnName of ['Unit', 'Key']) {
			await expect(
				productDetailsPage.uomPriceTableColumnHeader(columnName)
			).toHaveCount(0);
		}

		await expect(productDetailsPage.uomPriceTableRows).toHaveCount(4);

		for (const [rowIndex, cells] of [
			['1', '$ 50.00'],
			['5', '$ 240.00'],
			['10', '$ 450.00'],
			['20', '$ 900.00'],
		].entries()) {
			await expect(
				productDetailsPage.uomPriceTableRowCells(rowIndex)
			).toHaveText(cells);
		}
	}
);

test(
	'Price table reflects the unit of measure and tier price combination of each SKU of a multiple SKU product',
	{tag: ['@COMMERCE-12467', '@LPD-105705']},
	async ({apiHelpers, page, productDetailsPage}) => {
		const sku1 =
			await apiHelpers.headlessCommerceAdminCatalog.getSkuByName(
				'MIN93016A'
			);
		const sku2 =
			await apiHelpers.headlessCommerceAdminCatalog.getSkuByName(
				'MIN93016C'
			);

		for (const unitOfMeasure of [
			{basePrice: 3, key: 'Lt', name: {en_US: 'Liter'}, priority: 1},
			{basePrice: 10, key: 'Bt', name: {en_US: 'Bottle'}, priority: 2},
		]) {
			await apiHelpers.headlessCommerceAdminCatalog.postSkuUnitOfMeasure(
				sku1.id,
				{
					incrementalOrderQuantity: 1,
					precision: 1,
					primary: false,
					rate: 1,
					...unitOfMeasure,
				}
			);
		}

		const bottlePriceEntry = await getPriceEntry(apiHelpers, sku1.id, 'Bt');

		for (const tierPrice of [
			{minimumQuantity: 5, price: 45},
			{minimumQuantity: 10, price: 80},
		]) {
			await apiHelpers.headlessCommerceAdminPricing.postTierPrice(
				bottlePriceEntry.priceEntryId,
				{...tierPrice, unitOfMeasureKey: 'Bt'}
			);
		}

		const literPriceEntry = await getPriceEntry(apiHelpers, sku1.id, 'Lt');

		await apiHelpers.headlessCommerceAdminPricing.patchPriceEntry(
			literPriceEntry.priceEntryId,
			{priceOnApplication: true}
		);

		const priceEntry = await getPriceEntry(apiHelpers, sku2.id);

		for (const tierPrice of [
			{minimumQuantity: 15, price: 200},
			{minimumQuantity: 25, price: 320},
			{minimumQuantity: 50, price: 600},
		]) {
			await apiHelpers.headlessCommerceAdminPricing.postTierPrice(
				priceEntry.priceEntryId,
				tierPrice
			);
		}

		await page.goto(`/web${site.friendlyUrlPath}/p/brake-fluid`);

		await test.step('The SKU with two units of measure lists every unit of measure and tier price combination', async () => {
			await expect(
				await productDetailsPage.skuField('MIN93016A')
			).toBeVisible();
			await expect(productDetailsPage.uomPriceTableRows).toHaveCount(4);

			for (const [rowIndex, cells] of [
				['Liter', 'Lt', '1', 'Price on Application'],
				['Bottle', 'Bt', '1', '$ 10.00'],
				['Bottle', 'Bt', '5', '$ 45.00'],
				['Bottle', 'Bt', '10', '$ 80.00'],
			].entries()) {
				await expect(
					productDetailsPage.uomPriceTableRowCells(rowIndex)
				).toHaveText(cells);
			}

			await expect(
				productDetailsPage.uomPriceTableRowCells(0).last()
			).toHaveClass(/price-on-application/);
		});

		await test.step('The SKU without a unit of measure or tier price renders no table, only its list and promotion price', async () => {
			await productDetailsPage
				.optionSelector('Package Quantity')
				.selectOption({label: '48'});

			await expect(
				await productDetailsPage.skuField('MIN93016B')
			).toBeVisible();
			await expect(productDetailsPage.uomPriceTable).toHaveCount(0);
			await expect(
				await productDetailsPage.priceField(
					'$ 80.00',
					productDetailsPage.priceContainer
				)
			).toHaveClass(/price-value-inactive/);
			await expect(
				await productDetailsPage.promoPriceField(
					'$ 72.00',
					productDetailsPage.priceContainer
				)
			).toHaveClass(/price-value-promo/);
		});

		await test.step('The SKU with tier prices and no unit of measure lists its quantity breaks', async () => {
			await productDetailsPage
				.optionSelector('Package Quantity')
				.selectOption({label: '112'});

			await expect(
				await productDetailsPage.skuField('MIN93016C')
			).toBeVisible();
			await expect(productDetailsPage.uomPriceTableRows).toHaveCount(4);

			for (const [rowIndex, cells] of [
				['1', '$ 80.00'],
				['15', '$ 200.00'],
				['25', '$ 320.00'],
				['50', '$ 600.00'],
			].entries()) {
				await expect(
					productDetailsPage.uomPriceTableRowCells(rowIndex)
				).toHaveText(cells);
			}
		});
	}
);

test(
	'Price table is updated when a unit of measure is deleted and a tier price is edited',
	{tag: ['@COMMERCE-12468', '@LPD-105705']},
	async ({
		apiHelpers,
		commerceAdminPriceListDetailsPage,
		commerceAdminPriceListsPage,
		page,
		productDetailsPage,
	}) => {
		const sku =
			await apiHelpers.headlessCommerceAdminCatalog.getSkuByName(
				'MIN93015'
			);

		const unitsOfMeasure = [];

		for (const unitOfMeasure of [
			{
				basePrice: 3,
				incrementalOrderQuantity: 3.5,
				key: 'Lt',
				name: {en_US: 'Liter'},
				priority: 1,
			},
			{
				basePrice: 10,
				incrementalOrderQuantity: 1,
				key: 'Bt',
				name: {en_US: 'Bottle'},
				priority: 2,
			},
		]) {
			unitsOfMeasure.push(
				await apiHelpers.headlessCommerceAdminCatalog.postSkuUnitOfMeasure(
					sku.id,
					{precision: 1, primary: false, rate: 1, ...unitOfMeasure}
				)
			);
		}

		const bottlePriceEntry = await getPriceEntry(apiHelpers, sku.id, 'Bt');

		for (const tierPrice of [
			{minimumQuantity: 5, price: 45},
			{minimumQuantity: 10, price: 80},
		]) {
			await apiHelpers.headlessCommerceAdminPricing.postTierPrice(
				bottlePriceEntry.priceEntryId,
				{...tierPrice, unitOfMeasureKey: 'Bt'}
			);
		}

		await goToPriceEntry(
			commerceAdminPriceListDetailsPage,
			commerceAdminPriceListsPage,
			'Bt'
		);

		await test.step('A bulk price can be added to the unit of measure price entry', async () => {
			await commerceAdminPriceListDetailsPage.bulkPricingRadio.click();

			await commerceAdminPriceListDetailsPage.sidePanelSaveButton.click();

			await waitForAlert(
				commerceAdminPriceListDetailsPage.sidePanelFrame
			);

			await expect(
				commerceAdminPriceListDetailsPage.bulkPricingRadio
			).toBeChecked();

			await commerceAdminPriceListDetailsPage.addTierPriceButton.click();

			await commerceAdminPriceListDetailsPage.addTierPriceEntryQuantity.fill(
				'40'
			);
			await commerceAdminPriceListDetailsPage.addTierPriceEntryPrice.fill(
				'700'
			);

			await commerceAdminPriceListDetailsPage.addTierPriceEntrySaveButton.click();

			await expect(
				commerceAdminPriceListDetailsPage.skuLink('$ 700.00')
			).toBeVisible();

			const bulkTierPrice = (
				await apiHelpers.headlessCommerceAdminPricing.getTierPrices(
					bottlePriceEntry.priceEntryId
				)
			).items.find(
				(tierPrice: {minimumQuantity: number}) =>
					tierPrice.minimumQuantity === 40
			);

			apiHelpers.data.push({id: bulkTierPrice.id, type: 'tierPrice'});
		});

		await test.step('The price table lists every unit of measure, tier price and bulk price', async () => {
			await page.goto(`/web${site.friendlyUrlPath}/p/abs-sensor`);

			await expect(productDetailsPage.uomPriceTableRows).toHaveCount(5);

			for (const [rowIndex, cells] of [
				['Liter', 'Lt', '3.5', '$ 3.00'],
				['Bottle', 'Bt', '1', '$ 10.00'],
				['Bottle', 'Bt', '5', '$ 45.00'],
				['Bottle', 'Bt', '10', '$ 80.00'],
				['Bottle', 'Bt', '40', '$ 700.00'],
			].entries()) {
				await expect(
					productDetailsPage.uomPriceTableRowCells(rowIndex)
				).toHaveText(cells);
			}
		});

		await apiHelpers.headlessCommerceAdminCatalog.deleteSkuUnitOfMeasure(
			unitsOfMeasure[0].id
		);

		await goToPriceEntry(
			commerceAdminPriceListDetailsPage,
			commerceAdminPriceListsPage,
			'Bt'
		);

		await test.step('A tier price quantity that is not a multiple of the unit of measure order quantity is rejected', async () => {
			await commerceAdminPriceListDetailsPage.skuLink('$ 45.00').click();

			await commerceAdminPriceListDetailsPage.editPriceTierQuantity.fill(
				'2.5'
			);
			await commerceAdminPriceListDetailsPage.editPriceTierPrice.fill(
				'210'
			);

			await commerceAdminPriceListDetailsPage.editPriceTierSaveButton.click();

			await expect(
				commerceAdminPriceListDetailsPage.editPriceTierQuantityNotAllowedError
			).toBeVisible();
		});

		await test.step('The tier price is saved with an allowed quantity', async () => {
			await commerceAdminPriceListDetailsPage.editPriceTierQuantity.fill(
				'25'
			);
			await commerceAdminPriceListDetailsPage.editPriceTierPrice.fill(
				'210'
			);

			await commerceAdminPriceListDetailsPage.editPriceTierSaveButton.click();

			await expect(
				commerceAdminPriceListDetailsPage.skuLink('$ 210.00')
			).toBeVisible();
		});

		await test.step('The price table drops the deleted unit of measure and shows the edited tier price', async () => {
			await page.goto(`/web${site.friendlyUrlPath}/p/abs-sensor`);

			await expect(productDetailsPage.uomPriceTableRows).toHaveCount(4);

			for (const [rowIndex, cells] of [
				['Bottle', 'Bt', '1', '$ 10.00'],
				['Bottle', 'Bt', '10', '$ 80.00'],
				['Bottle', 'Bt', '25', '$ 210.00'],
				['Bottle', 'Bt', '40', '$ 700.00'],
			].entries()) {
				await expect(
					productDetailsPage.uomPriceTableRowCells(rowIndex)
				).toHaveText(cells);
			}

			await expect(
				productDetailsPage.uomPriceTable.getByText('Liter')
			).toBeHidden();
		});
	}
);
