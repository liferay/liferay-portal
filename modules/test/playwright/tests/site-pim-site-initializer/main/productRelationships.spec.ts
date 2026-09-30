/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {loginTest} from '../../../fixtures/loginTest';
import {clickAndExpectToBeVisible} from '../../../utils/clickAndExpectToBeVisible';
import getRandomString from '../../../utils/getRandomString';
import {cmsPagesTest} from '../../site-cms-site-initializer/main/fixtures/cmsPagesTest';
import {pimPagesTest} from './fixtures/pimPagesTest';
import {ProductPage} from './pages/ProductPage';
import {ProductRelationshipsPage} from './pages/ProductRelationshipsPage';
import {ProductsPage} from './pages/ProductsPage';

const RELATIONSHIPS_TAB = 'Relationships';

const test = mergeTests(cmsPagesTest, loginTest(), pimPagesTest);

async function createProduct(
	name: string,
	productPage: ProductPage,
	productsPage: ProductsPage
) {
	await productsPage.goto();

	await productsPage.openNewProductEditor();

	await productPage.code.fill(getRandomString());
	await productPage.name.fill(name);

	await productPage.save();

	await expect(productsPage.getProduct(name)).toBeVisible();
}

async function openRelationships(
	name: string,
	productPage: ProductPage,
	productRelationshipsPage: ProductRelationshipsPage,
	productsPage: ProductsPage
) {
	await productsPage.goto();

	await productsPage.openProductEditor(name);

	await clickAndExpectToBeVisible({
		target: productRelationshipsPage.addRelationshipButton,
		trigger: productPage.getTab(RELATIONSHIPS_TAB),
	});
}

test(
	'Relate a product to another one',
	{tag: ['@LPD-105923']},
	async ({productPage, productRelationshipsPage, productsPage}) => {
		const productName1 = getRandomString();
		const productName2 = getRandomString();

		try {
			await createProduct(productName1, productPage, productsPage);
			await createProduct(productName2, productPage, productsPage);

			await openRelationships(
				productName1,
				productPage,
				productRelationshipsPage,
				productsPage
			);

			await productRelationshipsPage.addRelationships([productName2]);

			await test.step('Both products list each other', async () => {
				await expect(productRelationshipsPage.rows).toHaveCount(1);

				await openRelationships(
					productName2,
					productPage,
					productRelationshipsPage,
					productsPage
				);

				await expect(productRelationshipsPage.rows).toHaveCount(1);
				await expect(
					productRelationshipsPage.getRelatedProduct(productName1)
				).toBeVisible();
			});
		}
		finally {
			await productsPage.goto();

			await productsPage.deleteProduct(productName1);
			await productsPage.deleteProduct(productName2);
		}
	}
);

test(
	'Relate a product to two others',
	{tag: ['@LPD-105923']},
	async ({productPage, productRelationshipsPage, productsPage}) => {
		const productName1 = getRandomString();
		const productName2 = getRandomString();
		const productName3 = getRandomString();

		try {
			await createProduct(productName1, productPage, productsPage);
			await createProduct(productName2, productPage, productsPage);
			await createProduct(productName3, productPage, productsPage);

			await openRelationships(
				productName1,
				productPage,
				productRelationshipsPage,
				productsPage
			);

			await productRelationshipsPage.addRelationships([
				productName2,
				productName3,
			]);

			await test.step('Every product lists the other two', async () => {
				await expect(productRelationshipsPage.rows).toHaveCount(2);

				await openRelationships(
					productName2,
					productPage,
					productRelationshipsPage,
					productsPage
				);

				await expect(productRelationshipsPage.rows).toHaveCount(2);
				await expect(
					productRelationshipsPage.getRelatedProduct(productName1)
				).toBeVisible();
				await expect(
					productRelationshipsPage.getRelatedProduct(productName3)
				).toBeVisible();

				await openRelationships(
					productName3,
					productPage,
					productRelationshipsPage,
					productsPage
				);

				await expect(productRelationshipsPage.rows).toHaveCount(2);
				await expect(
					productRelationshipsPage.getRelatedProduct(productName1)
				).toBeVisible();
				await expect(
					productRelationshipsPage.getRelatedProduct(productName2)
				).toBeVisible();
			});
		}
		finally {
			await productsPage.goto();

			await productsPage.deleteProduct(productName1);
			await productsPage.deleteProduct(productName2);
			await productsPage.deleteProduct(productName3);
		}
	}
);

test(
	'Remove one relationship from a group of three products',
	{tag: ['@LPD-105923']},
	async ({productPage, productRelationshipsPage, productsPage}) => {
		const productName1 = getRandomString();
		const productName2 = getRandomString();
		const productName3 = getRandomString();

		try {
			await createProduct(productName1, productPage, productsPage);
			await createProduct(productName2, productPage, productsPage);
			await createProduct(productName3, productPage, productsPage);

			await openRelationships(
				productName1,
				productPage,
				productRelationshipsPage,
				productsPage
			);

			await productRelationshipsPage.addRelationships([
				productName2,
				productName3,
			]);

			await productRelationshipsPage.deleteRelationship(productName3);

			await test.step('The removed product keeps no relationship', async () => {
				await expect(productRelationshipsPage.rows).toHaveCount(1);
				await expect(
					productRelationshipsPage.getRelatedProduct(productName2)
				).toBeVisible();

				await openRelationships(
					productName2,
					productPage,
					productRelationshipsPage,
					productsPage
				);

				await expect(productRelationshipsPage.rows).toHaveCount(1);
				await expect(
					productRelationshipsPage.getRelatedProduct(productName1)
				).toBeVisible();

				await openRelationships(
					productName3,
					productPage,
					productRelationshipsPage,
					productsPage
				);

				await expect(productRelationshipsPage.rows).toHaveCount(0);
			});
		}
		finally {
			await productsPage.goto();

			await productsPage.deleteProduct(productName1);
			await productsPage.deleteProduct(productName2);
			await productsPage.deleteProduct(productName3);
		}
	}
);
