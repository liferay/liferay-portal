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
	title: string,
	productPage: ProductPage,
	productsPage: ProductsPage
) {
	await productsPage.goto();

	await productsPage.openNewProductEditor();

	await productPage.sku.fill(getRandomString());
	await productPage.title.fill(title);

	await productPage.save();

	await expect(productsPage.getProduct(title)).toBeVisible();
}

async function openRelationships(
	title: string,
	productPage: ProductPage,
	productRelationshipsPage: ProductRelationshipsPage,
	productsPage: ProductsPage
) {
	await productsPage.goto();

	await productsPage.openProductEditor(title);

	await clickAndExpectToBeVisible({
		target: productRelationshipsPage.addRelationshipButton,
		trigger: productPage.getTab(RELATIONSHIPS_TAB),
	});
}

test(
	'Relate a product to another one',
	{tag: ['@LPD-105923']},
	async ({productPage, productRelationshipsPage, productsPage}) => {
		const productTitle1 = getRandomString();
		const productTitle2 = getRandomString();

		try {
			await createProduct(productTitle1, productPage, productsPage);
			await createProduct(productTitle2, productPage, productsPage);

			await openRelationships(
				productTitle1,
				productPage,
				productRelationshipsPage,
				productsPage
			);

			await productRelationshipsPage.addRelationships([productTitle2]);

			await test.step('Both products list each other', async () => {
				await expect(productRelationshipsPage.rows).toHaveCount(1);

				await openRelationships(
					productTitle2,
					productPage,
					productRelationshipsPage,
					productsPage
				);

				await expect(productRelationshipsPage.rows).toHaveCount(1);
				await expect(
					productRelationshipsPage.getRelatedProduct(productTitle1)
				).toBeVisible();
			});
		}
		finally {
			await productsPage.goto();

			await productsPage.deleteProduct(productTitle1);
			await productsPage.deleteProduct(productTitle2);
		}
	}
);

test(
	'Relate a product to two others',
	{tag: ['@LPD-105923']},
	async ({productPage, productRelationshipsPage, productsPage}) => {
		const productTitle1 = getRandomString();
		const productTitle2 = getRandomString();
		const productTitle3 = getRandomString();

		try {
			await createProduct(productTitle1, productPage, productsPage);
			await createProduct(productTitle2, productPage, productsPage);
			await createProduct(productTitle3, productPage, productsPage);

			await openRelationships(
				productTitle1,
				productPage,
				productRelationshipsPage,
				productsPage
			);

			await productRelationshipsPage.addRelationships([
				productTitle2,
				productTitle3,
			]);

			await test.step('Every product lists the other two', async () => {
				await expect(productRelationshipsPage.rows).toHaveCount(2);

				await openRelationships(
					productTitle2,
					productPage,
					productRelationshipsPage,
					productsPage
				);

				await expect(productRelationshipsPage.rows).toHaveCount(2);
				await expect(
					productRelationshipsPage.getRelatedProduct(productTitle1)
				).toBeVisible();
				await expect(
					productRelationshipsPage.getRelatedProduct(productTitle3)
				).toBeVisible();

				await openRelationships(
					productTitle3,
					productPage,
					productRelationshipsPage,
					productsPage
				);

				await expect(productRelationshipsPage.rows).toHaveCount(2);
				await expect(
					productRelationshipsPage.getRelatedProduct(productTitle1)
				).toBeVisible();
				await expect(
					productRelationshipsPage.getRelatedProduct(productTitle2)
				).toBeVisible();
			});
		}
		finally {
			await productsPage.goto();

			await productsPage.deleteProduct(productTitle1);
			await productsPage.deleteProduct(productTitle2);
			await productsPage.deleteProduct(productTitle3);
		}
	}
);

test(
	'Remove one relationship from a group of three products',
	{tag: ['@LPD-105923']},
	async ({productPage, productRelationshipsPage, productsPage}) => {
		const productTitle1 = getRandomString();
		const productTitle2 = getRandomString();
		const productTitle3 = getRandomString();

		try {
			await createProduct(productTitle1, productPage, productsPage);
			await createProduct(productTitle2, productPage, productsPage);
			await createProduct(productTitle3, productPage, productsPage);

			await openRelationships(
				productTitle1,
				productPage,
				productRelationshipsPage,
				productsPage
			);

			await productRelationshipsPage.addRelationships([
				productTitle2,
				productTitle3,
			]);

			await productRelationshipsPage.removeRelationship(productTitle3);

			await test.step('The removed product keeps no relationship', async () => {
				await expect(productRelationshipsPage.rows).toHaveCount(1);
				await expect(
					productRelationshipsPage.getRelatedProduct(productTitle2)
				).toBeVisible();

				await openRelationships(
					productTitle2,
					productPage,
					productRelationshipsPage,
					productsPage
				);

				await expect(productRelationshipsPage.rows).toHaveCount(1);
				await expect(
					productRelationshipsPage.getRelatedProduct(productTitle1)
				).toBeVisible();

				await openRelationships(
					productTitle3,
					productPage,
					productRelationshipsPage,
					productsPage
				);

				await expect(productRelationshipsPage.rows).toHaveCount(0);
			});
		}
		finally {
			await productsPage.goto();

			await productsPage.deleteProduct(productTitle1);
			await productsPage.deleteProduct(productTitle2);
			await productsPage.deleteProduct(productTitle3);
		}
	}
);

test(
	'View a related product from the row menu',
	{tag: ['@LPD-109922']},
	async ({productPage, productRelationshipsPage, productsPage}) => {
		const productTitle1 = getRandomString();
		const productTitle2 = getRandomString();

		try {
			await createProduct(productTitle1, productPage, productsPage);
			await createProduct(productTitle2, productPage, productsPage);

			await openRelationships(
				productTitle1,
				productPage,
				productRelationshipsPage,
				productsPage
			);

			await productRelationshipsPage.addRelationships([productTitle2]);

			await productRelationshipsPage.clickItemAction(
				'View',
				productTitle2
			);

			await expect(productPage.title).toHaveValue(productTitle2);
		}
		finally {
			await productsPage.goto();

			await productsPage.deleteProduct(productTitle1);
			await productsPage.deleteProduct(productTitle2);
		}
	}
);
