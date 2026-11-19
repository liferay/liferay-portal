/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';
import path from 'path';

import {loginTest} from '../../../fixtures/loginTest';
import {applyFDSSelectionFilter} from '../../../utils/applyFDSSelectionFilter';
import getRandomString from '../../../utils/getRandomString';
import {cmsPagesTest} from '../../site-cms-site-initializer/main/fixtures/cmsPagesTest';
import {pimPagesTest} from './fixtures/pimPagesTest';

const PRODUCT_STRUCTURE_LABEL = 'PIM Basic Product';

const SPACE_NAME = 'Default';

const test = mergeTests(cmsPagesTest, loginTest(), pimPagesTest);

test(
	'Create a product from the All section and filter by its type',
	{tag: ['@LPD-105923']},
	async ({
		assetsPage,
		contentsPage,
		page,
		productPage,
		productsPage,
		spaceSelectorPage,
	}) => {
		const productTitle = getRandomString();
		const webContentTitle = getRandomString();

		try {
			await test.step('Create a product of the type the New menu offers', async () => {
				await assetsPage.gotoAll();

				await assetsPage.createContent(PRODUCT_STRUCTURE_LABEL);

				await spaceSelectorPage.selectSpace(SPACE_NAME);

				await productPage.sku.fill(getRandomString());
				await productPage.title.fill(productTitle);

				await productPage.save();
			});

			await test.step('Create a web content so the filter has something to exclude', async () => {
				await assetsPage.gotoAll();

				await assetsPage.createContent('Basic Web Content');

				await spaceSelectorPage.selectSpace(SPACE_NAME);

				await contentsPage.fillData([
					{label: 'Title', value: webContentTitle},
				]);

				await contentsPage.saveContent();
			});

			await test.step('Filter the All section by the product type', async () => {
				await assetsPage.gotoAll();

				await expect(
					assetsPage.getItem(PRODUCT_STRUCTURE_LABEL).first()
				).toBeVisible();
				await expect(assetsPage.getItem(webContentTitle)).toBeVisible();

				await applyFDSSelectionFilter(page, {
					filter: 'Type',
					value: PRODUCT_STRUCTURE_LABEL,
				});

				await expect(
					assetsPage.getItem(PRODUCT_STRUCTURE_LABEL).first()
				).toBeVisible();
				await expect(assetsPage.getItem(webContentTitle)).toBeHidden();
			});
		}
		finally {
			await productsPage.goto();

			await productsPage.deleteProduct(productTitle);
		}
	}
);

test(
	'Show the first image of a product as its card thumbnail in the All section',
	{tag: ['@LPD-114149']},
	async ({assetsPage, productPage, productsPage}) => {
		const imageProductTitle = getRandomString();
		const productTitle = getRandomString();

		try {
			await test.step('Add a product with two images and one without images', async () => {
				const dependenciesPath = path.join(
					__dirname,
					'../../site-cms-site-initializer/main/dependencies'
				);
				const images = productPage.getRepeatableGroupEntries('Images');

				await productsPage.goto();

				await productsPage.openNewProductEditor(SPACE_NAME);

				await productPage.sku.fill(getRandomString());
				await productPage.title.fill(imageProductTitle);

				await images
					.first()
					.locator('input[type="file"]')
					.setInputFiles(
						path.join(dependenciesPath, 'file_upload_image_1.jpg')
					);

				await productPage
					.getRepeatableGroup('Images')
					.getByRole('button', {name: 'Add New'})
					.click();

				await expect(images).toHaveCount(2);

				await images
					.last()
					.locator('input[type="file"]')
					.setInputFiles(
						path.join(
							dependenciesPath,
							'sample_small_wide_400x300.jpg'
						)
					);

				await productPage.save();

				await expect(
					productsPage.getProduct(imageProductTitle)
				).toBeVisible();

				await productsPage.openNewProductEditor(SPACE_NAME);

				await productPage.sku.fill(getRandomString());
				await productPage.title.fill(productTitle);

				await productPage.save();

				await expect(
					productsPage.getProduct(productTitle)
				).toBeVisible();
			});

			await test.step('The All section cards show the first image', async () => {
				await assetsPage.gotoAll();

				await assetsPage.changeVisualizationMode('Cards');

				await expect(
					assetsPage.getCardItem(imageProductTitle).locator('img')
				).toHaveAttribute('src', /file_upload_image_1\.jpg/);
				await expect(
					assetsPage.getCardItem(productTitle).locator('img')
				).toHaveCount(0);
			});
		}
		finally {
			await assetsPage.gotoAll();

			await assetsPage.changeVisualizationMode('Table');

			await productsPage.goto();

			await productsPage.deleteProduct(imageProductTitle);
			await productsPage.deleteProduct(productTitle);
		}
	}
);
