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
	{tag: ['@LPD-98441']},
	async ({contentsPage, productsPage}) => {
		const baseSkuName = getRandomString();

		try {
			await productsPage.goto();

			await productsPage.openNewProductEditor();

			await contentsPage.fillData([
				{label: 'Code', value: getRandomString()},
				{label: 'Name', value: baseSkuName},
			]);

			await contentsPage.saveContent();

			await expect(productsPage.getProduct(baseSkuName)).toBeVisible();
		}
		finally {
			await productsPage.goto();

			await productsPage.deleteProduct(baseSkuName);
		}
	}
);
