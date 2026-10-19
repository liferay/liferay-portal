/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {apiHelpersTest} from '../../../../fixtures/apiHelpersTest';
import {commercePagesTest} from '../../../../fixtures/commercePagesTest';
import {dataApiHelpersTest} from '../../../../fixtures/dataApiHelpersTest';
import {isolatedSiteTest} from '../../../../fixtures/isolatedSiteTest';
import {loginTest} from '../../../../fixtures/loginTest';
import {clickAndExpectToBeVisible} from '../../../../utils/clickAndExpectToBeVisible';
import {waitForAlert} from '../../../../utils/waitForAlert';

export const test = mergeTests(
	apiHelpersTest,
	commercePagesTest,
	isolatedSiteTest,
	dataApiHelpersTest,
	loginTest()
);

test(
	'Can create, edit and delete UOM',
	{tag: ['@LPD-73148']},
	async ({
		apiHelpers,
		commerceAdminProductDetailsPage,
		commerceAdminProductDetailsSkusPage,
		commerceAdminProductPage,
	}) => {
		let catalog;
		let product;
		let sku;
		const uom2Name = 'UOM2';

		await test.step('Create a Catalog, a Product and a UOM', async () => {
			catalog = await apiHelpers.headlessCommerceAdminCatalog.postCatalog(
				{
					name: 'Catalog',
				}
			);

			product = await apiHelpers.headlessCommerceAdminCatalog.postProduct(
				{
					catalogId: catalog.id,
				}
			);

			const productWithSkus =
				await apiHelpers.headlessCommerceAdminCatalog.getProduct(
					product.productId
				);
			sku = productWithSkus.skus[0];

			await apiHelpers.headlessCommerceAdminCatalog.postSkuUnitOfMeasure(
				sku.id,
				{
					incrementalOrderQuantity: 0.5,
					key: 'UOM1',
					name: {en_US: 'UOM1'},
					precision: 2,
					pricingQuantity: 1.0,
					priority: 0,
				}
			);
		});

		await test.step('Navigate to product and go to Skus tab', async () => {
			await commerceAdminProductPage.gotoProduct(product.name['en_US']);

			await commerceAdminProductDetailsPage.goToProductSkus();

			await commerceAdminProductDetailsSkusPage
				.skusTableRowLink(`${sku.sku}`)
				.click();

			await commerceAdminProductDetailsSkusPage.goToSkuUOM();
		});

		await test.step('Try to add a UOM with empty fields and check the required alert in the modal', async () => {
			await commerceAdminProductDetailsSkusPage.addSkuUOMButton.click();

			await expect(
				commerceAdminProductDetailsSkusPage.skuUOMModal.getByRole(
					'heading',
					{name: 'Add Unit of Measure'}
				)
			).toBeVisible();

			await expect(
				commerceAdminProductDetailsSkusPage.skuUOMModal.getByLabel(
					'Unit of Measure Required'
				)
			).toBeVisible();

			await expect(
				commerceAdminProductDetailsSkusPage.skuUOMModal.getByLabel(
					'Set as Primary Unit of Measure'
				)
			).not.toBeChecked();

			await commerceAdminProductDetailsSkusPage.skuUOMModal
				.getByRole('button', {name: 'Add'})
				.click();

			await expect(
				commerceAdminProductDetailsSkusPage.skuUOMModal.getByText(
					'The Unit of Measure field is required.',
					{exact: true}
				)
			).toBeVisible();

			await expect(
				commerceAdminProductDetailsSkusPage.skuUOMModal.getByText(
					'The Key field is required.',
					{exact: true}
				)
			).toBeVisible();
		});

		await test.step('Try to add a UOM with same key and same name of the existing one, and check the error message', async () => {
			await commerceAdminProductDetailsSkusPage.addUOMEntry({
				baseUnitQuantity: '0.75',
				decimalAllowed: '2',
				skipModalOpening: true,
				uomKey: 'UOM1',
				uomName: 'UOM1',
			});

			await waitForAlert(
				commerceAdminProductDetailsSkusPage.skuUOMModal,
				'Error:There is another unit of measure with the same key.',
				{autoClose: false, type: 'danger'}
			);
		});

		await test.step('Add a UOM', async () => {
			await commerceAdminProductDetailsSkusPage.addUOMEntry({
				baseUnitQuantity: '0.75',
				decimalAllowed: '2',
				skipModalOpening: true,
				uomKey: uom2Name,
				uomName: uom2Name,
			});

			await expect(
				commerceAdminProductDetailsSkusPage.skuUOMModal.getByRole(
					'heading',
					{name: 'Add Unit of Measure'}
				)
			).toBeHidden();

			await expect(
				(
					await commerceAdminProductDetailsSkusPage.skuUOMTableRow(
						1,
						uom2Name,
						true
					)
				).row
			).toBeVisible();
		});

		await test.step('Edit UOM base unit quantity to invalid value and check the error message', async () => {
			await (
				await commerceAdminProductDetailsSkusPage.skuUOMTableRowAction(
					uom2Name
				)
			).click();

			await commerceAdminProductDetailsSkusPage
				.skuUOMActionMenuItem('Edit')
				.click();

			await commerceAdminProductDetailsSkusPage.sidePanelNestedFrame
				.getByLabel('Base Unit Quantity Required')
				.fill('0');

			await commerceAdminProductDetailsSkusPage.sidePanelNestedSaveButton.click();

			await waitForAlert(
				commerceAdminProductDetailsSkusPage.sidePanelNestedFrame,
				'Error:Decimals allowed cannot be less than the number of decimals in the base unit quantity',
				{autoClose: false, type: 'danger'}
			);

			await (
				await commerceAdminProductDetailsSkusPage.closeSidePanelFrame(
					true
				)
			).click();

			await expect(
				await commerceAdminProductDetailsSkusPage.closeSidePanelFrame(
					true
				)
			).toBeHidden();

			await expect(
				(
					await commerceAdminProductDetailsSkusPage.skuUOMTableRow(
						1,
						uom2Name,
						true
					)
				).row
			).toBeVisible();
		});

		await test.step('Delete UOM', async () => {
			const skuUOMTableRow = (
				await commerceAdminProductDetailsSkusPage.skuUOMTableRow(
					1,
					uom2Name,
					true
				)
			).row;

			const skuTableRowActionLocator =
				await commerceAdminProductDetailsSkusPage.skuUOMTableRowAction(
					uom2Name
				);

			await clickAndExpectToBeVisible({
				autoClick: true,
				target: commerceAdminProductDetailsSkusPage.skuUOMActionMenuItem(
					'Delete'
				),
				trigger: skuTableRowActionLocator,
			});

			await waitForAlert(
				commerceAdminProductDetailsSkusPage.sidePanelFrame
			);

			await expect(skuUOMTableRow).not.toBeAttached();
		});
	}
);
