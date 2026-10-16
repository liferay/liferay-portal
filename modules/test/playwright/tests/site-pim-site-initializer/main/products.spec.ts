/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {dataApiHelpersTest} from '../../../fixtures/dataApiHelpersTest';
import {featureFlagsTest} from '../../../fixtures/featureFlagsTest';
import {loginTest} from '../../../fixtures/loginTest';
import {clickAndExpectToBeVisible} from '../../../utils/clickAndExpectToBeVisible';
import getRandomString from '../../../utils/getRandomString';
import {waitForAlert} from '../../../utils/waitForAlert';
import {cmsPagesTest} from '../../site-cms-site-initializer/main/fixtures/cmsPagesTest';
import {pimPagesTest} from './fixtures/pimPagesTest';

const test = mergeTests(
	cmsPagesTest,
	dataApiHelpersTest,
	featureFlagsTest({'LPD-96666': {enabled: true}}),
	loginTest(),
	pimPagesTest
);

async function addProduct({
	productPage,
	productsPage,
	productTitle = getRandomString(),
	spaceName,
}) {
	await productsPage.openNewProductEditor(spaceName);

	await productPage.sku.fill(getRandomString());
	await productPage.title.fill(productTitle);

	await productPage.save();

	await expect(productsPage.getProduct(productTitle)).toBeVisible();

	return productTitle;
}

test(
	'Create a basic product',
	{tag: ['@LPD-98441', '@LPD-99449', '@LPD-99450']},
	async ({productPage, productsPage}) => {
		const productTitle = getRandomString();

		try {
			await productsPage.goto();

			await productsPage.openNewProductEditor();

			await productPage.sku.fill(getRandomString());
			await productPage.depth.fill('10.5');
			await productPage.height.fill('20.5');
			await productPage.title.fill(productTitle);
			await productPage.virtual.setChecked(true);
			await productPage.weight.fill('30.5');
			await productPage.width.fill('40.5');

			await clickAndExpectToBeVisible({
				target: productPage.unitOfMeasureName,
				trigger: productPage.getTab('Units of Measure'),
			});

			await productPage.unitOfMeasureAllowDecimalQuantities.setChecked(
				true
			);
			await productPage.unitOfMeasureKey.fill('box');
			await productPage.unitOfMeasureName.fill('Box');
			await productPage.unitOfMeasureSymbol.fill('BX');

			await productPage.save();

			await expect(productsPage.getProduct(productTitle)).toBeVisible();

			await test.step('Verify that the unit of measure and dimension fields are persisted', async () => {
				await productsPage.openProductEditor(productTitle);

				await expect(productPage.depth).toHaveValue('10.5');
				await expect(productPage.height).toHaveValue('20.5');
				await expect(productPage.virtual).toBeChecked();
				await expect(productPage.weight).toHaveValue('30.5');
				await expect(productPage.width).toHaveValue('40.5');

				await clickAndExpectToBeVisible({
					target: productPage.unitOfMeasureName,
					trigger: productPage.getTab('Units of Measure'),
				});

				await expect(
					productPage.unitOfMeasureAllowDecimalQuantities
				).toBeChecked();
				await expect(productPage.unitOfMeasureKey).toHaveValue('box');
				await expect(productPage.unitOfMeasureName).toHaveValue('Box');
				await expect(productPage.unitOfMeasureSymbol).toHaveValue('BX');
			});
		}
		finally {
			await productsPage.goto();

			await productsPage.deleteProduct(productTitle);
		}
	}
);

test(
	'The basic product object layout renders a tab per group',
	{tag: '@LPD-99449'},
	async ({productPage, productsPage}) => {
		await productsPage.goto();

		await productsPage.openNewProductEditor();

		await expect(productPage.tabs).toHaveText([
			'Details',
			'Units of Measure',
			'Relationships',
		]);

		await test.step('The first tab is the one the form opens on', async () => {
			await expect(productPage.descriptionField).toBeVisible();
			await expect(productPage.sku).toBeVisible();
			await expect(productPage.unitOfMeasureKey).toBeHidden();
			await expect(productPage.unitOfMeasureName).toBeHidden();
			await expect(productPage.unitOfMeasureSymbol).toBeHidden();
			await expect(productPage.virtual).toBeVisible();
		});

		await test.step('The dimensions are grouped in a panel of the first tab', async () => {
			await expect(productPage.dimensions).toBeVisible();
			await expect(productPage.depth).toBeVisible();
			await expect(productPage.height).toBeVisible();
			await expect(productPage.weight).toBeVisible();
			await expect(productPage.width).toBeVisible();
		});

		await clickAndExpectToBeVisible({
			target: productPage.unitOfMeasureName,
			trigger: productPage.getTab('Units of Measure'),
		});

		await expect(productPage.depth).toBeHidden();
		await expect(productPage.descriptionField).toBeHidden();
		await expect(productPage.sku).toBeHidden();
		await expect(productPage.title).toBeVisible();
		await expect(
			productPage.unitOfMeasureAllowDecimalQuantities
		).toBeVisible();
		await expect(productPage.unitOfMeasureKey).toBeVisible();
		await expect(productPage.unitOfMeasureSymbol).toBeVisible();
	}
);

test(
	'Delete a product moves it to the Recycle Bin',
	{tag: '@LPD-109721'},
	async ({apiHelpers, page, productPage, productsPage, recycleBinPage}) => {
		const spaceName = getRandomString();

		await apiHelpers.headlessAssetLibrary.createSpace({
			name: spaceName,
			settings: {trashEnabled: true},
		});

		await productsPage.goto();

		const productTitle = await addProduct({
			productPage,
			productsPage,
			spaceName,
		});

		await test.step('Delete the product without a confirmation and undo it from the toast', async () => {
			await productsPage.dataSetFragmentPage.execItemAction({
				action: 'Delete',
				filter: productTitle,
			});

			await waitForAlert(page, `Success:${productTitle} was moved`, {
				autoClose: false,
			});

			await productsPage.undoButton.click();

			await waitForAlert(page, `Success:${productTitle} was restored`, {
				autoClose: false,
			});

			await expect(productsPage.getProduct(productTitle)).toBeVisible();
		});

		await test.step('Delete the product again and open the Recycle Bin from the toast', async () => {
			await productsPage.dataSetFragmentPage.execItemAction({
				action: 'Delete',
				filter: productTitle,
			});

			await waitForAlert(page, `Success:${productTitle} was moved`, {
				autoClose: false,
			});

			await productsPage.recycleBinLink.click();

			await expect(recycleBinPage.getItem(productTitle)).toBeVisible();
		});

		await test.step('Restore the product from the Recycle Bin', async () => {
			await recycleBinPage.execItemAction({
				action: 'Restore',
				filter: productTitle,
			});

			await waitForAlert(page, `Success:${productTitle} was restored`, {
				autoClose: false,
			});

			await productsPage.goto();

			await expect(productsPage.getProduct(productTitle)).toBeVisible();
		});
	}
);

test(
	'Bulk delete products moves them to the Recycle Bin',
	{tag: '@LPD-109721'},
	async ({apiHelpers, page, productPage, productsPage, recycleBinPage}) => {
		const spaceName = getRandomString();

		await apiHelpers.headlessAssetLibrary.createSpace({
			name: spaceName,
			settings: {trashEnabled: true},
		});

		await productsPage.goto();

		const productTitle1 = await addProduct({
			productPage,
			productsPage,
			spaceName,
		});
		const productTitle2 = await addProduct({
			productPage,
			productsPage,
			spaceName,
		});

		await test.step('Delete both products without a confirmation', async () => {
			await productsPage.selectProducts([productTitle1, productTitle2]);

			await productsPage.dataSetFragmentPage.execBulkItemAction({
				action: 'Delete',
			});

			await waitForAlert(
				page,
				'Info:Delete action started for 2 assets.',
				{type: 'info'}
			);

			await expect(productsPage.getProduct(productTitle1)).toBeHidden();
			await expect(productsPage.getProduct(productTitle2)).toBeHidden();
		});

		await test.step('Both products are in the Recycle Bin', async () => {
			await recycleBinPage.goto();

			await expect(recycleBinPage.getItem(productTitle1)).toBeVisible();
			await expect(recycleBinPage.getItem(productTitle2)).toBeVisible();
		});
	}
);

test(
	'Delete products from the All page moves them to the Recycle Bin',
	{tag: '@LPD-109721'},
	async ({
		apiHelpers,
		assetsPage,
		page,
		productPage,
		productsPage,
		recycleBinPage,
	}) => {
		const spaceName = getRandomString();

		await apiHelpers.headlessAssetLibrary.createSpace({
			name: spaceName,
			settings: {trashEnabled: true},
		});

		await productsPage.goto();

		const productTitlePrefix = getRandomString();

		const productTitle1 = await addProduct({
			productPage,
			productTitle: `${productTitlePrefix}1`,
			productsPage,
			spaceName,
		});
		const productTitle2 = await addProduct({
			productPage,
			productTitle: `${productTitlePrefix}2`,
			productsPage,
			spaceName,
		});
		const productTitle3 = await addProduct({
			productPage,
			productTitle: `${productTitlePrefix}3`,
			productsPage,
			spaceName,
		});

		await assetsPage.gotoAll();

		await productsPage.search(productTitlePrefix);

		await test.step('Delete a product from its row actions', async () => {
			await assetsPage.execItemAction({
				action: 'Delete',
				filter: productTitle1,
			});

			await waitForAlert(page, `Success:${productTitle1} was moved`, {
				autoClose: false,
			});

			await expect(assetsPage.getItem(productTitle1)).toBeHidden();
		});

		await test.step('Bulk delete the other products without a confirmation', async () => {
			await assetsPage.selectItems([productTitle2, productTitle3]);

			await assetsPage.execBulkItemAction('Delete');

			await waitForAlert(
				page,
				'Info:Delete action started for 2 assets.',
				{type: 'info'}
			);

			await expect(assetsPage.getItem(productTitle2)).toBeHidden();
			await expect(assetsPage.getItem(productTitle3)).toBeHidden();
		});

		await test.step('The products are in the Recycle Bin', async () => {
			await recycleBinPage.goto();

			await expect(recycleBinPage.getItem(productTitle1)).toBeVisible();
			await expect(recycleBinPage.getItem(productTitle2)).toBeVisible();
			await expect(recycleBinPage.getItem(productTitle3)).toBeVisible();
		});
	}
);
