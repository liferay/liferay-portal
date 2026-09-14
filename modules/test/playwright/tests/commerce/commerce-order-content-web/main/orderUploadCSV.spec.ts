/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';
import path from 'path';

import {commercePagesTest} from '../../../../fixtures/commercePagesTest';
import {dataApiHelpersTest} from '../../../../fixtures/dataApiHelpersTest';
import {loginTest} from '../../../../fixtures/loginTest';
import {DataApiHelpers} from '../../../../helpers/ApiHelpers';
import getRandomString from '../../../../utils/getRandomString';
import {
	performLoginViaApi,
	performUserSwitch,
} from '../../../../utils/performLogin';
import {createAccountWithBuyerUser, miniumSetUp} from '../../utils/commerce';

export const test = mergeTests(
	commercePagesTest,
	dataApiHelpersTest,
	loginTest()
);

let account: {id?: number};
let buyerScreenName: string;
let channel: {id: number; name: string};
let setupData: Array<{id: number | string; type: string}>;
let site: Site;
let uJointExternalReferenceCode: string;

test.beforeAll(async ({browser}) => {
	const page = await browser.newPage();

	await performLoginViaApi({page, screenName: 'test'});

	const apiHelpers = new DataApiHelpers(page);

	const miniumResult = await miniumSetUp(apiHelpers);

	channel = miniumResult.channel;
	site = miniumResult.site;

	const buyerResult = await createAccountWithBuyerUser(apiHelpers, site.id);

	account = buyerResult.account;
	buyerScreenName = buyerResult.buyerUser.alternateName;

	const products = await apiHelpers.headlessCommerceAdminCatalog.getProducts(
		new URLSearchParams({pageSize: '200', search: 'MIN55861'})
	);

	const uJointProduct = products.items.find(
		(product: {catalogId: number}) =>
			product.catalogId === miniumResult.catalog.id
	);

	uJointExternalReferenceCode = uJointProduct.skus[0].externalReferenceCode;

	const warehouses =
		await apiHelpers.headlessCommerceAdminInventoryApiHelper.getWarehousesPage();

	for (const warehouse of warehouses.items) {
		const warehouseItems =
			await apiHelpers.headlessCommerceAdminInventoryApiHelper.getWarehouseIdWarehouseItemsPage(
				warehouse.id
			);

		const warehouseItem = warehouseItems.items.find(
			(warehouseItem: {sku: string}) => warehouseItem.sku === 'MIN93015'
		);

		if (warehouseItem) {
			await apiHelpers.headlessCommerceAdminInventoryApiHelper.patchWarehouseItem(
				warehouseItem.id,
				{quantity: 0, sku: warehouseItem.sku}
			);
		}
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

test(
	'Valid CSV order files are previewed with their resolved prices',
	{tag: ['@COMMERCE-7699', '@LPD-105662']},
	async ({apiHelpers, commerceLayoutsPage, page, pendingOrdersPage}) => {
		await performUserSwitch(page, buyerScreenName);

		const cart = await apiHelpers.headlessCommerceDeliveryCart.postCart(
			{accountId: account.id},
			channel.id
		);

		await pendingOrdersPage.gotoOrder(site.friendlyUrlPath, cart.id);

		await pendingOrdersPage.openImportFromCSV();

		await commerceLayoutsPage.importCsvFile(
			path.join(__dirname, '/dependencies/commerce_csv_order.csv')
		);

		await commerceLayoutsPage.expectImportCsvPreviewRow('U-Joint', {
			importStatus: 'OK',
			quantity: 1,
			sku: 'MIN55861',
			totalPrice: '24.00',
			unitPrice: '24.00',
		});
		await commerceLayoutsPage.expectImportCsvPreviewRow('ABS Sensor', {
			importStatus: 'OK',
			quantity: 2,
			sku: 'MIN93015',
			totalPrice: '100.00',
			unitPrice: '50.00',
		});
		await commerceLayoutsPage.expectImportCsvPreviewRow('Mount', {
			importStatus: 'OK',
			quantity: 1,
			sku: 'MIN55857',
			totalPrice: '3.00',
			unitPrice: '3.00',
		});
	}
);

test(
	'CSV order files with unusable headers are rejected',
	{
		tag: [
			'@COMMERCE-7704',
			'@COMMERCE-7706',
			'@COMMERCE-7707',
			'@LPD-105662',
		],
	},
	async ({apiHelpers, commerceLayoutsPage, page, pendingOrdersPage}) => {
		await performUserSwitch(page, buyerScreenName);

		const cart = await apiHelpers.headlessCommerceDeliveryCart.postCart(
			{accountId: account.id},
			channel.id
		);

		await pendingOrdersPage.gotoOrder(site.friendlyUrlPath, cart.id);

		for (const fileName of [
			'commerce_missing_field.csv',
			'commerce_invalid_fields.csv',
			'commerce_invalid_stringERC.csv',
			'commerce_invalid_decimalERC.csv',
			'commerce.pdf',
			'commerce.txt',
			'commerce.jpeg',
		]) {
			await test.step(`Rejects ${fileName}`, async () => {
				await pendingOrdersPage.openImportFromCSV();

				await commerceLayoutsPage.importCsvFile(
					path.join(__dirname, '/dependencies/', fileName)
				);

				await expect(
					commerceLayoutsPage.importCsvErrorAlert
				).toContainText('The CSV could not be imported.');

				await commerceLayoutsPage.closeFrameButton.click();

				const cartItems =
					await apiHelpers.headlessCommerceDeliveryCart.getCartItems(
						cart.id
					);

				expect(cartItems.totalCount).toBe(0);
			});
		}
	}
);

test(
	'Unresolvable SKUs and invalid quantities are reported per row',
	{tag: ['@COMMERCE-7702', '@COMMERCE-7705', '@LPD-105662']},
	async ({apiHelpers, commerceLayoutsPage, page, pendingOrdersPage}) => {
		await performUserSwitch(page, buyerScreenName);

		const cart = await apiHelpers.headlessCommerceDeliveryCart.postCart(
			{accountId: account.id},
			channel.id
		);

		await pendingOrdersPage.gotoOrder(site.friendlyUrlPath, cart.id);

		for (const [fileName, productName] of [
			['commerce_invalid_skuID.csv', '123.36'],
			['commerce_invalid_stringSkuID.csv', '1,2345'],
		]) {
			await test.step(`Previews ${productName} as unavailable`, async () => {
				await pendingOrdersPage.openImportFromCSV();

				await commerceLayoutsPage.importCsvFile(
					path.join(__dirname, '/dependencies/', fileName)
				);

				await commerceLayoutsPage.expectImportCsvPreviewRow(productName, {
					importStatus: 'The product is no longer available',
				});

				await commerceLayoutsPage.closeFrameButton.click();

				const cartItems =
					await apiHelpers.headlessCommerceDeliveryCart.getCartItems(
						cart.id
					);

				expect(cartItems.totalCount).toBe(0);
			});
		}

		await test.step('Reports a negative quantity as not imported', async () => {
			await pendingOrdersPage.openImportFromCSV();

			await commerceLayoutsPage.importCsvFile(
				path.join(
					__dirname,
					'/dependencies/commerce_invalid_quantity.csv'
				)
			);

			await commerceLayoutsPage.expectImportCsvPreviewRow('U-Joint', {
				sku: 'MIN55861',
			});

			await commerceLayoutsPage.importCsvSubmitButton.click();

			await expect(page.locator('.alert-danger')).toContainText(
				'1 row was not imported.'
			);

			const cartItems =
				await apiHelpers.headlessCommerceDeliveryCart.getCartItems(
					cart.id
				);

			expect(cartItems.totalCount).toBe(0);
		});
	}
);

test(
	'SKUs are resolved by external reference code',
	{tag: ['@LPD-105662']},
	async ({apiHelpers, commerceLayoutsPage, page, pendingOrdersPage}) => {
		expect(uJointExternalReferenceCode).not.toBe('MIN55861');

		await performUserSwitch(page, buyerScreenName);

		const cart = await apiHelpers.headlessCommerceDeliveryCart.postCart(
			{accountId: account.id},
			channel.id
		);

		await pendingOrdersPage.gotoOrder(site.friendlyUrlPath, cart.id);

		await pendingOrdersPage.openImportFromCSV();

		const unknownExternalReferenceCode = getRandomString();

		await commerceLayoutsPage.importCsvFile({
			buffer: Buffer.from(
				'sku,quantity\n' +
					`${uJointExternalReferenceCode},3\n` +
					`${unknownExternalReferenceCode},1\n`
			),
			mimeType: 'text/csv',
			name: 'commerce_erc_order.csv',
		});

		await commerceLayoutsPage.expectImportCsvPreviewRow('U-Joint', {
			importStatus: 'OK',
			quantity: 3,
			sku: 'MIN55861',
			totalPrice: '72.00',
			unitPrice: '24.00',
		});
		await commerceLayoutsPage.expectImportCsvPreviewRow(
			unknownExternalReferenceCode,
			{importStatus: 'The product is no longer available'}
		);
	}
);
