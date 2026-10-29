/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {commercePagesTest} from '../../../../fixtures/commercePagesTest';
import {dataApiHelpersTest} from '../../../../fixtures/dataApiHelpersTest';
import {loginTest} from '../../../../fixtures/loginTest';
import {clickAndExpectToBeVisible} from '../../../../utils/clickAndExpectToBeVisible';
import getRandomString from '../../../../utils/getRandomString';

export const test = mergeTests(
	commercePagesTest,
	dataApiHelpersTest,
	loginTest()
);

test(
	'The grouped entries item selector offers only the products that can be grouped',
	{tag: '@LPD-110417'},
	async ({apiHelpers, commerceAdminProductPage, page}) => {
		const catalog =
			await apiHelpers.headlessCommerceAdminCatalog.postCatalog();

		const groupedProduct =
			await apiHelpers.headlessCommerceAdminCatalog.postProduct({
				catalogId: catalog.id,
				name: {en_US: getRandomString()},
				productType: 'grouped',
			});
		const otherGroupedProduct =
			await apiHelpers.headlessCommerceAdminCatalog.postProduct({
				catalogId: catalog.id,
				name: {en_US: getRandomString()},
				productType: 'grouped',
			});
		const simpleProduct1 =
			await apiHelpers.headlessCommerceAdminCatalog.postProduct({
				catalogId: catalog.id,
				name: {en_US: getRandomString()},
			});
		const simpleProduct2 =
			await apiHelpers.headlessCommerceAdminCatalog.postProduct({
				catalogId: catalog.id,
				name: {en_US: getRandomString()},
			});

		await commerceAdminProductPage.gotoProduct(groupedProduct.name.en_US);

		await page.getByRole('link', {exact: true, name: 'Grouped'}).click();

		const addGroupedEntryButton = page.getByRole('button', {
			exact: true,
			name: 'Add Grouped Entry',
		});
		const itemSelectorModal = page.getByRole('dialog', {
			exact: true,
			name: `Add New Grouped Entry to ${groupedProduct.name.en_US}`,
		});

		await test.step('Add an entry', async () => {
			await clickAndExpectToBeVisible({
				target: itemSelectorModal,
				trigger: addGroupedEntryButton,
			});

			await expect(
				itemSelectorModal.getByRole('row').filter({
					hasText: simpleProduct2.name.en_US,
				})
			).toBeVisible();

			for (const product of [groupedProduct, otherGroupedProduct]) {
				await expect(
					itemSelectorModal.getByRole('row').filter({
						hasText: product.name.en_US,
					})
				).toHaveCount(0);
			}

			await itemSelectorModal
				.getByRole('row')
				.filter({hasText: simpleProduct1.name.en_US})
				.getByRole('checkbox')
				.check();
			await itemSelectorModal
				.getByRole('button', {exact: true, name: 'Select'})
				.click();

			await expect(
				page.getByRole('cell', {
					exact: true,
					name: simpleProduct1.name.en_US,
				})
			).toBeVisible();
		});

		await test.step('Check the existing entry is excluded', async () => {
			await clickAndExpectToBeVisible({
				target: itemSelectorModal,
				trigger: addGroupedEntryButton,
			});

			await expect(
				itemSelectorModal.getByRole('row').filter({
					hasText: simpleProduct2.name.en_US,
				})
			).toBeVisible();

			await expect(
				itemSelectorModal.getByRole('row').filter({
					hasText: simpleProduct1.name.en_US,
				})
			).toHaveCount(0);
		});
	}
);
