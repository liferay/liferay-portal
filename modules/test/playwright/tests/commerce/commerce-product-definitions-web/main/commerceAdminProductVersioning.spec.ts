/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {apiHelpersTest} from '../../../../fixtures/apiHelpersTest';
import {commercePagesTest} from '../../../../fixtures/commercePagesTest';
import {dataApiHelpersTest} from '../../../../fixtures/dataApiHelpersTest';
import {isolatedSiteTest} from '../../../../fixtures/isolatedSiteTest';
import {loginTest} from '../../../../fixtures/loginTest';
import {pageViewModePagesTest} from '../../../../fixtures/pageViewModePagesTest';
import {CommerceInstanceSettingsPage} from '../../../../pages/commerce/commerceInstanceSettingsPage';
import getRandomString from '../../../../utils/getRandomString';
import {performLoginViaApi} from '../../../../utils/performLogin';
import {waitForAlert} from '../../../../utils/waitForAlert';

export const test = mergeTests(
	apiHelpersTest,
	commercePagesTest,
	dataApiHelpersTest,
	isolatedSiteTest,
	loginTest(),
	pageViewModePagesTest
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

test(
	'Save as Draft asks for confirmation when the product already has a draft',
	{tag: '@LPD-106110'},
	async ({
		apiHelpers,
		commerceAdminProductDetailsPage,
		commerceAdminProductPage,
		page,
	}) => {
		const catalog =
			await apiHelpers.headlessCommerceAdminCatalog.postCatalog();

		const product =
			await apiHelpers.headlessCommerceAdminCatalog.postProduct({
				catalogId: catalog.id,
				productStatus: 2,
			});

		await apiHelpers.headlessCommerceAdminCatalog.patchProduct(
			String(product.productId),
			{name: product.name, productStatus: 0}
		);

		await commerceAdminProductPage.gotoProduct(product.name['en_US']);

		const productURL = page.url();

		await commerceAdminProductDetailsPage.saveAsDraft();

		await page.goto(productURL);

		await Promise.all([
			page.waitForEvent('dialog').then(async (dialog) => {
				expect(dialog.message()).toContain(
					'There is already a draft version of this product.'
				);

				await dialog.accept();
			}),
			commerceAdminProductDetailsPage.saveAsDraft(),
		]);

		await apiHelpers.headlessCommerceAdminCatalog.deleteProductByVersion(
			product.productId,
			2
		);

		await apiHelpers.headlessCommerceAdminCatalog.deleteProductByVersion(
			product.productId,
			1
		);
	}
);

test(
	'Save as Draft is shown only when product versioning is enabled',
	{tag: '@LPD-106110'},
	async ({
		apiHelpers,
		commerceAdminProductDetailsPage,
		commerceAdminProductPage,
		commerceInstanceSettingsPage,
	}) => {
		const catalog =
			await apiHelpers.headlessCommerceAdminCatalog.postCatalog();

		const product =
			await apiHelpers.headlessCommerceAdminCatalog.postProduct({
				catalogId: catalog.id,
				productStatus: 2,
			});

		await apiHelpers.headlessCommerceAdminCatalog.patchProduct(
			String(product.productId),
			{name: product.name, productStatus: 0}
		);

		await commerceAdminProductPage.gotoProduct(product.name['en_US']);

		await expect(
			commerceAdminProductDetailsPage.saveAsDraftLink
		).toBeVisible();

		await commerceInstanceSettingsPage.toggleProductVersioning();

		await commerceAdminProductPage.gotoProduct(product.name['en_US']);

		await expect(commerceAdminProductDetailsPage.publishLink).toBeVisible();
		await expect(
			commerceAdminProductDetailsPage.saveAsDraftLink
		).toBeHidden();

		await commerceInstanceSettingsPage.toggleProductVersioning();

		await apiHelpers.headlessCommerceAdminCatalog.deleteProductByVersion(
			product.productId,
			1
		);
	}
);

test(
	'Saving a draft leaves the published version on the storefront until the draft is published',
	{tag: ['@LPD-106110', '@LPD-99202']},
	async ({
		apiHelpers,
		commerceAdminProductDetailsPage,
		commerceAdminProductPage,
		page,
		productDetailsPage,
		site,
		widgetPagePage,
	}) => {
		const product =
			await test.step('Publish a product and show it on the storefront', async () => {
				const layout = await apiHelpers.jsonWebServicesLayout.addLayout(
					{
						groupId: site.id,
						title: getRandomString(),
					}
				);

				await apiHelpers.headlessCommerceAdminChannel.postChannel({
					siteGroupId: site.id,
				});

				const catalog =
					await apiHelpers.headlessCommerceAdminCatalog.postCatalog();

				const product =
					await apiHelpers.headlessCommerceAdminCatalog.postProduct({
						catalogId: catalog.id,
						name: {en_US: getRandomString()},
						shortDescription: {en_US: 'Short description OLD'},
					});

				await page.goto(
					`/web${site.friendlyUrlPath}${layout.friendlyURL}`
				);

				await widgetPagePage.addPortlet('Product Details');

				return product;
			});

		const productURL = `/web/${site.name}/p/${product.name['en_US']}`;

		await test.step('The storefront shows the published short description', async () => {
			await page.goto(productURL);

			await expect(
				await productDetailsPage.shortDescriptionField(
					'Short description OLD'
				)
			).toBeVisible();
		});

		const draftURL =
			await test.step('Edit the short description and save it as a draft', async () => {
				await commerceAdminProductPage.gotoProduct(
					product.name['en_US']
				);

				await (
					await commerceAdminProductDetailsPage.productDetailsInput(
						'Short Description'
					)
				).fill('Short description NEW');

				await commerceAdminProductDetailsPage.saveAsDraft();

				await expect(
					commerceAdminProductDetailsPage.workflowStatusLabel('Draft')
				).toBeVisible();

				return page.url();
			});

		await test.step('The draft does not reach the storefront', async () => {
			await page.goto(productURL);

			await expect(
				await productDetailsPage.shortDescriptionField(
					'Short description OLD'
				)
			).toBeVisible();

			await expect(
				await productDetailsPage.shortDescriptionField(
					'Short description NEW'
				)
			).toBeHidden();
		});

		await test.step('Publishing the draft carries it to the storefront', async () => {
			await page.goto(draftURL);

			await commerceAdminProductDetailsPage.publishLink.click();

			await waitForAlert(page);

			await page.goto(productURL);

			await expect(
				await productDetailsPage.shortDescriptionField(
					'Short description NEW'
				)
			).toBeVisible();
		});

		await apiHelpers.headlessCommerceAdminCatalog.deleteProductByVersion(
			product.productId,
			2
		);

		await apiHelpers.headlessCommerceAdminCatalog.deleteProductByVersion(
			product.productId,
			1
		);
	}
);
