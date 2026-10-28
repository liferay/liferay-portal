/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {apiHelpersTest} from '../../../../fixtures/apiHelpersTest';
import {commercePagesTest} from '../../../../fixtures/commercePagesTest';
import {dataApiHelpersTest} from '../../../../fixtures/dataApiHelpersTest';
import {globalMenuPagesTest} from '../../../../fixtures/globalMenuPagesTest';
import {loginTest} from '../../../../fixtures/loginTest';
import {waitForAlert} from '../../../../utils/waitForAlert';

export const test = mergeTests(
	apiHelpersTest,
	globalMenuPagesTest,
	commercePagesTest,
	dataApiHelpersTest,
	loginTest()
);

test('LPD-21637 Virtual item details section visible for product and sku', async ({
	apiHelpers,
	commerceAdminProductPage,
	globalMenuPage,
	page,
}) => {
	const catalog = await apiHelpers.headlessCommerceAdminCatalog.postCatalog();

	const virtualProduct =
		await apiHelpers.headlessCommerceAdminCatalog.postProduct({
			catalogId: catalog.id,
			productType: 'virtual',
			skus: [
				{
					cost: 0,
					price: 0,
					published: true,
					purchasable: true,
					sku: 'VirtualSku',
				},
			],
		});

	await globalMenuPage.goToCommerce('Products');

	await commerceAdminProductPage.managementToolbarSearchInput.fill(
		virtualProduct.name.en_US
	);
	await commerceAdminProductPage.managementToolbarSearchInput.press('Enter');
	await commerceAdminProductPage
		.productsTableRowLink(virtualProduct.name.en_US)
		.click();
	await commerceAdminProductPage.productVirtualLink.click();
	await commerceAdminProductPage.addVirtualProductFileEntryButton.click();

	await expect(
		commerceAdminProductPage.productVirtualFileEntrySaveAndPropagateButton
	).toBeVisible();

	const productSkuVirtualFileEntryURL =
		'https://8.8.8.8/test-virtual-product-sku-details-section';
	const productVirtualFileEntryURL =
		'https://8.8.8.8/test-virtual-product-details-section';

	await commerceAdminProductPage.productVirtualFileEntryURLInput.fill(
		productVirtualFileEntryURL
	);

	await commerceAdminProductPage.productVirtualFileEntrySaveButton.click();
	await commerceAdminProductPage.productVirtualFileEntryCancelButton.click();

	await expect(page.getByText(productVirtualFileEntryURL)).toBeVisible();

	await commerceAdminProductPage.productSkusLink.click();
	await commerceAdminProductPage.productSkuTableRowLink('VirtualSku').click();
	await commerceAdminProductPage.virtualSettingsOverrideLink.click();
	await commerceAdminProductPage.productSkuVirtualOverrideToggle.check();
	await commerceAdminProductPage.addVirtualSkuFileEntryButton.click();
	await commerceAdminProductPage.productSkuVirtualFileEntryURLInput.fill(
		productSkuVirtualFileEntryURL
	);
	await commerceAdminProductPage.productSkuVirtualFileEntrySaveButton.click();

	await waitForAlert(page.frameLocator('iframe').frameLocator('iframe'));

	await commerceAdminProductPage.productSkuVirtualFileEntryCancelButton.click();

	await expect(
		page.frameLocator('iframe').getByText(productSkuVirtualFileEntryURL)
	).toBeVisible();
});
