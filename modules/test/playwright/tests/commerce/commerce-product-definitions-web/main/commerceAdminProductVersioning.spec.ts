/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {apiHelpersTest} from '../../../../fixtures/apiHelpersTest';
import {commercePagesTest} from '../../../../fixtures/commercePagesTest';
import {dataApiHelpersTest} from '../../../../fixtures/dataApiHelpersTest';
import {loginTest} from '../../../../fixtures/loginTest';
import {CommerceInstanceSettingsPage} from '../../../../pages/commerce/commerceInstanceSettingsPage';
import {performLoginViaApi} from '../../../../utils/performLogin';
import {waitForAlert} from '../../../../utils/waitForAlert';

export const test = mergeTests(
	apiHelpersTest,
	commercePagesTest,
	dataApiHelpersTest,
	loginTest()
);

test.beforeAll(async ({browser}) => {
	const page = await browser.newPage();

	await performLoginViaApi({page, screenName: 'test'});

	await new CommerceInstanceSettingsPage(page).toggleProductVersioning();

	await page.close();
});

test.afterAll(async ({browser}) => {
	const page = await browser.newPage();

	await performLoginViaApi({page, screenName: 'test'});

	await new CommerceInstanceSettingsPage(page).toggleProductVersioning();

	await page.close();
});

test('LPD-3272 Enable product versioning and verify a new product version is created after updating the sku', async ({
	apiHelpers,
	commerceAdminProductDetailsPage,
	commerceAdminProductDetailsSkusPage,
	commerceAdminProductPage,
}) => {
	const catalog = await apiHelpers.headlessCommerceAdminCatalog.postCatalog();

	const product1 = await apiHelpers.headlessCommerceAdminCatalog.postProduct({
		catalogId: catalog.id,
		productStatus: 2,
	});

	await apiHelpers.headlessCommerceAdminCatalog.patchProduct(
		String(product1.productId),
		{name: product1.name, productStatus: 0}
	);

	const product1Skus = await apiHelpers.headlessCommerceAdminCatalog
		.getProduct(product1.productId)
		.then((product) => {
			return product.skus;
		});

	const product1Sku = product1Skus[0];

	await commerceAdminProductPage.gotoProduct(product1.name['en_US']);

	await commerceAdminProductDetailsPage.goToProductSkus();

	await commerceAdminProductDetailsSkusPage
		.skusTableRowLink(product1Sku.sku)
		.click();

	await commerceAdminProductDetailsSkusPage.sidePanelDetailsSkuFieldName.fill(
		'updatedSku'
	);

	await commerceAdminProductDetailsSkusPage.sidePanelDetailsSkuPublishButton.click();

	await expect(
		commerceAdminProductDetailsSkusPage.sidePanelFrame.getByText(
			'Success:Your request completed successfully.'
		)
	).toBeVisible();

	const product2 =
		await apiHelpers.headlessCommerceAdminCatalog.getProductByVersion(
			product1.productId,
			2
		);

	expect(product2.skuFormatted).not.toEqual(product1Sku.sku);
	expect(product2.skuFormatted).toEqual('updatedSku');

	await apiHelpers.headlessCommerceAdminCatalog.deleteProductByVersion(
		product2.productId,
		2
	);

	await apiHelpers.headlessCommerceAdminCatalog.deleteProductByVersion(
		product1.productId,
		1
	);
});

test('LPD-84993 Editing the Configuration tab and clicking Publish carries the change into the new published version', async ({
	apiHelpers,
	commerceAdminProductDetailsConfigurationPage,
	commerceAdminProductDetailsPage,
	commerceAdminProductPage,
	page,
}) => {
	const catalog = await apiHelpers.headlessCommerceAdminCatalog.postCatalog();

	const product = await apiHelpers.headlessCommerceAdminCatalog.postProduct({
		catalogId: catalog.id,
		productStatus: 2,
	});

	await apiHelpers.headlessCommerceAdminCatalog.patchProduct(
		String(product.productId),
		{name: product.name, productStatus: 0}
	);

	await commerceAdminProductPage.gotoProduct(product.name['en_US']);

	await commerceAdminProductDetailsPage.goToProductConfiguration();

	await expect(
		commerceAdminProductDetailsConfigurationPage.purchasableInput
	).toBeVisible();
	await expect(
		commerceAdminProductDetailsConfigurationPage.purchasableInput
	).toBeChecked();

	await commerceAdminProductDetailsConfigurationPage.purchasableInput.click();

	await commerceAdminProductDetailsConfigurationPage.publishLink.click();

	await waitForAlert(page);

	await page.reload();

	await expect(
		commerceAdminProductDetailsConfigurationPage.purchasableInput
	).not.toBeChecked();
});
