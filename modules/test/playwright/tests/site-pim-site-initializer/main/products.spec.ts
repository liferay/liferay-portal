/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {featureFlagsTest} from '../../../fixtures/featureFlagsTest';
import {loginTest} from '../../../fixtures/loginTest';
import {clickAndExpectToBeVisible} from '../../../utils/clickAndExpectToBeVisible';
import getRandomString from '../../../utils/getRandomString';
import {cmsPagesTest} from '../../site-cms-site-initializer/main/fixtures/cmsPagesTest';
import {pimPagesTest} from './fixtures/pimPagesTest';

const test = mergeTests(
	cmsPagesTest,
	featureFlagsTest({'LPD-96666': {enabled: true}}),
	loginTest(),
	pimPagesTest
);

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

			await contentsPage.saveContent();

			await expect(productsPage.getProduct(baseSkuName)).toBeVisible();

			await test.step('Verify that the unit of measure and dimension fields are persisted', async () => {
				await productsPage.openProductEditor(baseSkuName);

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

			await productsPage.deleteProduct(baseSkuName);
		}
	}
);

test(
	'The base SKU object layout renders a tab per group',
	{tag: '@LPD-99449'},
	async ({productPage, productsPage}) => {
		await productsPage.goto();

		await productsPage.openNewProductEditor();

		await expect(productPage.tabs).toHaveText([
			'Details',
			'Units of Measure',
		]);

		await test.step('The first tab is the one the form opens on', async () => {
			await expect(productPage.code).toBeVisible();
			await expect(productPage.descriptionField).toBeVisible();
			await expect(productPage.name).toBeVisible();
			await expect(productPage.virtual).toBeVisible();
			await expect(productPage.unitOfMeasureKey).toBeHidden();
			await expect(productPage.unitOfMeasureName).toBeHidden();
			await expect(productPage.unitOfMeasureSymbol).toBeHidden();
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

		await expect(
			productPage.unitOfMeasureAllowDecimalQuantities
		).toBeVisible();
		await expect(productPage.unitOfMeasureKey).toBeVisible();
		await expect(productPage.unitOfMeasureSymbol).toBeVisible();
		await expect(productPage.code).toBeHidden();
		await expect(productPage.depth).toBeHidden();
		await expect(productPage.descriptionField).toBeHidden();
		await expect(productPage.name).toBeHidden();
	}
);
