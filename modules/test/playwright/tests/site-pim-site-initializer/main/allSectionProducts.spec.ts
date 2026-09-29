/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {loginTest} from '../../../fixtures/loginTest';
import {applyFDSSelectionFilter} from '../../../utils/applyFDSSelectionFilter';
import getRandomString from '../../../utils/getRandomString';
import {cmsPagesTest} from '../../site-cms-site-initializer/main/fixtures/cmsPagesTest';
import {pimPagesTest} from './fixtures/pimPagesTest';

const PRODUCT_STRUCTURE_LABEL = 'PIM Base SKU';

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
		const productName = getRandomString();
		const webContentTitle = getRandomString();

		try {
			await test.step(
				'Create a product of the type the New menu offers',
				async () => {
					await assetsPage.gotoAll();

					await assetsPage.createContent(PRODUCT_STRUCTURE_LABEL);

					await spaceSelectorPage.selectSpace(SPACE_NAME);

					await productPage.code.fill(getRandomString());
					await productPage.name.fill(productName);

					await contentsPage.saveContent();
				}
			);

			await test.step(
				'Create a web content so the filter has something to exclude',
				async () => {
					await assetsPage.gotoAll();

					await assetsPage.createContent('Basic Web Content');

					await spaceSelectorPage.selectSpace(SPACE_NAME);

					await contentsPage.fillData([
						{label: 'Title', value: webContentTitle},
					]);

					await contentsPage.saveContent();
				}
			);

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

			await productsPage.deleteProduct(productName);
		}
	}
);
