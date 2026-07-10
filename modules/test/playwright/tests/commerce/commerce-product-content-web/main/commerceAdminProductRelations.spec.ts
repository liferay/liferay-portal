/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {apiHelpersTest} from '../../../../fixtures/apiHelpersTest';
import {commercePagesTest} from '../../../../fixtures/commercePagesTest';
import {dataApiHelpersTest} from '../../../../fixtures/dataApiHelpersTest';
import {loginTest} from '../../../../fixtures/loginTest';
import {workflowPagesTest} from '../../../../fixtures/workflowPagesTest';
import {getRandomInt} from '../../../../utils/getRandomInt';

export const test = mergeTests(
	apiHelpersTest,
	commercePagesTest,
	dataApiHelpersTest,
	loginTest(),
	workflowPagesTest
);

test('LPD-13559 Bulk actions for product relations', async ({
	apiHelpers,
	commerceAdminProductDetailsPage,
	commerceAdminProductDetailsProductRelationsPage,
	commerceAdminProductPage,
	page,
}) => {
	await page.goto('/');

	const catalog = await apiHelpers.headlessCommerceAdminCatalog.postCatalog();

	const product1 = await apiHelpers.headlessCommerceAdminCatalog.postProduct({
		catalogId: catalog.id,
	});
	const product2 = await apiHelpers.headlessCommerceAdminCatalog.postProduct({
		catalogId: catalog.id,
	});
	const product3 = await apiHelpers.headlessCommerceAdminCatalog.postProduct({
		catalogId: catalog.id,
	});

	await Promise.all([
		apiHelpers.headlessCommerceAdminCatalog.postProductRelatedProduct(
			product1.productId,
			{productId: product2.productId}
		),
		apiHelpers.headlessCommerceAdminCatalog.postProductRelatedProduct(
			product1.productId,
			{productId: product3.productId}
		),
	]);

	await commerceAdminProductPage.gotoProduct(product1.name['en_US']);

	await commerceAdminProductDetailsPage.goToProductRelations();

	await expect(
		commerceAdminProductDetailsProductRelationsPage.table
	).toBeVisible();

	await expect(
		(
			await commerceAdminProductDetailsProductRelationsPage.tableRow(
				2,
				product2.name['en_US'],
				true
			)
		).row
	).toBeVisible();
	await expect(
		(
			await commerceAdminProductDetailsProductRelationsPage.tableRow(
				2,
				product3.name['en_US'],
				true
			)
		).row
	).toBeVisible();

	await commerceAdminProductDetailsProductRelationsPage.selectItemsInput.check();

	await commerceAdminProductDetailsProductRelationsPage.bulkActionButton.click();

	await expect(
		commerceAdminProductDetailsProductRelationsPage.deleteBulkMenuItem
	).toBeVisible();

	await commerceAdminProductDetailsProductRelationsPage.deleteBulkMenuItem.click();

	await expect(
		commerceAdminProductDetailsProductRelationsPage.emptyTableMessage
	).toBeVisible();
});

test(
	'Add edit and remove product relations of all types',
	{tag: ['@LPD-55274', '@LPD-87061']},
	async ({
		apiHelpers,
		commerceAdminProductDetailsPage,
		commerceAdminProductDetailsProductRelationsPage,
		commerceAdminProductPage,
	}) => {
		const relationTypes = [
			'Up-Sell',
			'Cross-Sell',
			'Related',
			'Accessories',
			'Spare',
		];

		let catalog;
		let sourceProduct;
		let relatedProduct;

		await test.step('Create a catalog and products via API', async () => {
			catalog =
				await apiHelpers.headlessCommerceAdminCatalog.postCatalog();

			sourceProduct =
				await apiHelpers.headlessCommerceAdminCatalog.postProduct({
					catalogId: catalog.id,
					name: {en_US: 'Simple T-Shirt'},
				});
			relatedProduct =
				await apiHelpers.headlessCommerceAdminCatalog.postProduct({
					catalogId: catalog.id,
					name: {en_US: 'Shoes'},
				});
		});

		await commerceAdminProductPage.gotoProduct(sourceProduct.name.en_US);

		await commerceAdminProductDetailsPage.goToProductRelations();

		await test.step('Add a product relation', async () => {
			for (const relationType of relationTypes) {
				await commerceAdminProductDetailsProductRelationsPage.addProductRelation(
					relationType,
					relatedProduct.name.en_US
				);

				await expect(
					commerceAdminProductDetailsProductRelationsPage.productRelationRow(
						relatedProduct.name.en_US,
						relationType
					)
				).toBeVisible();
			}
		});

		await test.step('Edit a product relation', async () => {
			await commerceAdminProductDetailsProductRelationsPage.setProductRelationPriority(
				relatedProduct.name.en_US,
				'Up-Sell',
				1
			);

			await expect(
				commerceAdminProductDetailsProductRelationsPage.productRelationRow(
					relatedProduct.name.en_US,
					'Up-Sell'
				)
			).toContainText('1');
		});

		await test.step('Remove product relations, bulk delete all relations and assert the table is empty', async () => {
			await commerceAdminProductDetailsProductRelationsPage.selectItemsInput.check();

			await commerceAdminProductDetailsProductRelationsPage.bulkActionButton.click();
			await commerceAdminProductDetailsProductRelationsPage.deleteBulkMenuItem.click();

			await expect(
				commerceAdminProductDetailsProductRelationsPage.emptyTableMessage
			).toBeVisible();
		});
	}
);

test(
	'Product relations created under the Single Approver workflow show as pending and become approved once approved',
	{tag: '@COMMERCE-11569'},
	async ({
		apiHelpers,
		commerceAdminProductDetailsPage,
		commerceAdminProductDetailsProductRelationsPage,
		commerceAdminProductPage,
		configurationTabPage,
		page,
	}) => {
		await configurationTabPage.goTo();

		await configurationTabPage.assignWorkflowToAssetType(
			'Single Approver',
			'Product Link'
		);

		try {
			const catalog =
				await apiHelpers.headlessCommerceAdminCatalog.postCatalog();

			const sourceProduct =
				await apiHelpers.headlessCommerceAdminCatalog.postProduct({
					catalogId: catalog.id,
					name: {en_US: 'ABS Sensor ' + getRandomInt()},
				});
			const relatedProduct =
				await apiHelpers.headlessCommerceAdminCatalog.postProduct({
					catalogId: catalog.id,
					name: {en_US: 'Hoses ' + getRandomInt()},
				});

			const relationLinkIds = new Set<number>();

			const relationTypes = [
				{label: 'Up-Sell', type: 'up-sell'},
				{label: 'Cross-Sell', type: 'cross-sell'},
				{label: 'Related', type: 'related'},
				{label: 'Accessories', type: 'accessories'},
				{label: 'Spare', type: 'spare'},
			];

			for (const {type} of relationTypes) {
				const relation =
					await apiHelpers.headlessCommerceAdminCatalog.postProductRelatedProduct(
						sourceProduct.productId,
						{productId: relatedProduct.productId, type}
					);

				relationLinkIds.add(relation.id);
			}

			await commerceAdminProductPage.gotoProduct(
				sourceProduct.name.en_US
			);

			await commerceAdminProductDetailsPage.goToProductRelations();

			await expect(
				commerceAdminProductDetailsProductRelationsPage.table
			).toBeVisible();

			for (const {label} of relationTypes) {
				await expect(
					commerceAdminProductDetailsProductRelationsPage.productRelationStatusLabel(
						relatedProduct.name.en_US,
						label
					)
				).toHaveText('Pending');
			}

			const userAccount =
				await apiHelpers.headlessAdminUser.getMyUserAccount();

			let workflowTasks: any[] = [];

			await expect(async () => {
				const {items} =
					await apiHelpers.headlessAdminWorkflow.getWorkflowTasksBySubmittingUser(
						userAccount.id,
						-1
					);

				workflowTasks = items.filter((item) =>
					relationLinkIds.has(item.objectReviewed?.id)
				);

				expect(workflowTasks).toHaveLength(relationTypes.length);
			}).toPass();

			for (const workflowTask of workflowTasks) {
				await apiHelpers.headlessAdminWorkflow.postAssignTaskToUser(
					workflowTask.id,
					userAccount.id
				);

				await apiHelpers.headlessAdminWorkflow.postWorkflowTaskChangeTransition(
					workflowTask.id,
					'approve'
				);
			}

			await page.reload();

			await commerceAdminProductDetailsPage.goToProductRelations();

			for (const {label} of relationTypes) {
				await expect(
					commerceAdminProductDetailsProductRelationsPage.productRelationStatusLabel(
						relatedProduct.name.en_US,
						label
					)
				).toHaveText('Approved');
			}
		}
		finally {
			await configurationTabPage.goTo();

			await configurationTabPage.unassignWorkflowFromAssetType(
				'Product Link'
			);
		}
	}
);
