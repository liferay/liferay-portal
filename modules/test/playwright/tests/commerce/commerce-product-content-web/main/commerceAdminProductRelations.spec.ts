/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {apiHelpersTest} from '../../../../fixtures/apiHelpersTest';
import {commercePagesTest} from '../../../../fixtures/commercePagesTest';
import {dataApiHelpersTest} from '../../../../fixtures/dataApiHelpersTest';
import {loginTest} from '../../../../fixtures/loginTest';

export const test = mergeTests(
	apiHelpersTest,
	commercePagesTest,
	dataApiHelpersTest,
	loginTest()
);

test('LPD-13559 Bulk actions for product relations', async ({
	apiHelpers,
	commerceAdminProductDetailsPage,
	commerceAdminProductDetailsProductRelationsPage,
	commerceAdminProductPage,
	page,
}) => {
	await page.goto('/');

	const catalog = await apiHelpers.headlessCommerceAdminCatalog.postCatalog();

	const product1 = await apiHelpers.headlessCommerceAdminCatalog.postProduct({
		catalogId: catalog.id,
	});
	const product2 = await apiHelpers.headlessCommerceAdminCatalog.postProduct({
		catalogId: catalog.id,
	});
	const product3 = await apiHelpers.headlessCommerceAdminCatalog.postProduct({
		catalogId: catalog.id,
	});

	await Promise.all([
		apiHelpers.headlessCommerceAdminCatalog.postProductRelatedProduct(
			product1.productId,
			{productId: product2.productId}
		),
		apiHelpers.headlessCommerceAdminCatalog.postProductRelatedProduct(
			product1.productId,
			{productId: product3.productId}
		),
	]);

	await commerceAdminProductPage.gotoProduct(product1.name['en_US']);

	await commerceAdminProductDetailsPage.goToProductRelations();

	await expect(
		(
			await commerceAdminProductDetailsProductRelationsPage.tableRow(
				2,
				product2.name['en_US'],
				true
			)
		).row
	).toBeVisible();
	await expect(
		(
			await commerceAdminProductDetailsProductRelationsPage.tableRow(
				2,
				product3.name['en_US'],
				true
			)
		).row
	).toBeVisible();

	await commerceAdminProductDetailsProductRelationsPage.selectItemsInput.check();

	await expect(
		commerceAdminProductDetailsProductRelationsPage.deleteBulkButton
	).toBeVisible();

	await commerceAdminProductDetailsProductRelationsPage.deleteBulkButton.click();

	await expect(
		commerceAdminProductDetailsProductRelationsPage.emptyTableMessage
	).toBeVisible();
});

test('CanDeleteProductWithRelations', async ({
	apiHelpers,
	commerceAdminProductDetailsProductRelationsPage,
	commerceAdminProductPage,
}) => {
	const catalog = await apiHelpers.headlessCommerceAdminCatalog.postCatalog();

	const product1 = await apiHelpers.headlessCommerceAdminCatalog.postProduct({
		catalogId: catalog.id,
	});
	const product2 = await apiHelpers.headlessCommerceAdminCatalog.postProduct({
		catalogId: catalog.id,
	});

	await commerceAdminProductPage.gotoProduct(product1.name.en_US);

	await commerceAdminProductDetailsProductRelationsPage.addSpareProductRelation();

	await expect(
		await commerceAdminProductDetailsProductRelationsPage.addProductRelationHeading(
			product1.name.en_US
		)
	).toBeVisible();

	await apiHelpers.headlessCommerceAdminCatalog.deleteProduct(
		product2.productId
	);

	await commerceAdminProductPage.gotoProduct(product1.name.en_US);

	await expect(
		await commerceAdminProductDetailsProductRelationsPage.addProductRelationHeading(
			product2.name.en_US
		)
	).not.toBeVisible();
});
