/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {loginTest} from '../../../fixtures/loginTest';
import getRandomString from '../../../utils/getRandomString';
import {cmsPagesTest} from '../../site-cms-site-initializer/main/fixtures/cmsPagesTest';
import {pimPagesTest} from './fixtures/pimPagesTest';

const test = mergeTests(cmsPagesTest, loginTest(), pimPagesTest);

test(
	'Create a base SKU',
	{tag: ['@LPD-98441', '@LPD-99449', '@LPD-99450']},
	async ({contentsPage, productPage, productsPage}) => {
		const baseSkuName = getRandomString();

		try {
			await productsPage.goto();

			await productsPage.openNewProductEditor();

			await productPage.code.fill(getRandomString());
			await productPage.depth.fill('10.5');
			await productPage.height.fill('20.5');
			await productPage.name.fill(baseSkuName);
			await productPage.unitOfMeasureAllowDecimalQuantities.setChecked(
				true
			);
			await productPage.unitOfMeasureKey.fill('box');
			await productPage.unitOfMeasureName.fill('Box');
			await productPage.unitOfMeasureSymbol.fill('BX');
			await productPage.virtual.setChecked(true);
			await productPage.weight.fill('30.5');
			await productPage.width.fill('40.5');

			await contentsPage.saveContent();

			await expect(productsPage.getProduct(baseSkuName)).toBeVisible();

			await test.step('Verify that the unit of measure and dimension fields are persisted', async () => {
				await productsPage.openProductEditor(baseSkuName);

				await expect(productPage.depth).toHaveValue('10.5');
				await expect(productPage.height).toHaveValue('20.5');
				await expect(
					productPage.unitOfMeasureAllowDecimalQuantities
				).toBeChecked();
				await expect(productPage.unitOfMeasureKey).toHaveValue('box');
				await expect(productPage.unitOfMeasureName).toHaveValue('Box');
				await expect(productPage.unitOfMeasureSymbol).toHaveValue('BX');
				await expect(productPage.virtual).toBeChecked();
				await expect(productPage.weight).toHaveValue('30.5');
				await expect(productPage.width).toHaveValue('40.5');
			});
		}
		finally {
			await productsPage.goto();

			await productsPage.deleteProduct(baseSkuName);
		}
	}
);
