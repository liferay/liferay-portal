/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {apiHelpersTest} from '../../../../fixtures/apiHelpersTest';
import {commercePagesTest} from '../../../../fixtures/commercePagesTest';
import {dataApiHelpersTest} from '../../../../fixtures/dataApiHelpersTest';
import {loginTest} from '../../../../fixtures/loginTest';
import {DataApiHelpers} from '../../../../helpers/ApiHelpers';
import {CommerceAdminProductDetailsPage} from '../../../../pages/commerce/commerce-product-definitions-web/commerceAdminProductDetailsPage';
import {clickAndExpectToBeVisible} from '../../../../utils/clickAndExpectToBeVisible';

export const test = mergeTests(
	apiHelpersTest,
	commercePagesTest,
	dataApiHelpersTest,
	loginTest()
);

const CP_DEFINITION_CLASS_NAME =
	'com.liferay.commerce.product.model.CPDefinition';

async function duplicateProduct({
	catalogName,
	commerceAdminProductDetailsPage,
	productName,
}: {
	catalogName: string;
	commerceAdminProductDetailsPage: CommerceAdminProductDetailsPage;
	productName: string;
}) {
	await commerceAdminProductDetailsPage.duplicate(catalogName);

	await expect(
		commerceAdminProductDetailsPage.productTitle(`Copy of ${productName}`)
	).toBeVisible();
}

async function postSingleApproverCatalog(apiHelpers: DataApiHelpers) {
	const catalog = await apiHelpers.headlessCommerceAdminCatalog.postCatalog();

	const company =
		await apiHelpers.jsonWebServicesCompany.getCompanyByWebId(
			'liferay.com'
		);

	const group = await apiHelpers.jsonWebServicesGroup.getGroupByKey(
		company.companyId,
		String(catalog.id)
	);

	const workflowDefinition =
		await apiHelpers.headlessAdminWorkflow.getWorkflowDefinitionByName(
			'Single Approver'
		);

	await apiHelpers.headlessAdminWorkflow.postWorkflowDefinitionLink(
		CP_DEFINITION_CLASS_NAME,
		Number(group.groupId),
		workflowDefinition.id,
		workflowDefinition.name,
		Number(workflowDefinition.version)
	);

	return catalog;
}

test(
	'A draft product in a catalog with a workflow can be duplicated but not converted to draft',
	{tag: ['@COMMERCE-9290', '@LPD-110302']},
	async ({
		apiHelpers,
		commerceAdminProductDetailsPage,
		commerceAdminProductPage,
	}) => {
		const catalog = await postSingleApproverCatalog(apiHelpers);

		const product =
			await apiHelpers.headlessCommerceAdminCatalog.postProduct({
				catalogId: catalog.id,
				productStatus: 2,
			});

		await commerceAdminProductPage.gotoProduct(product.name['en_US']);

		await expect(
			commerceAdminProductDetailsPage.saveAsDraftLink
		).toBeVisible();
		await expect(
			commerceAdminProductDetailsPage.workflowStatusLabel('Draft')
		).toBeVisible();

		await clickAndExpectToBeVisible({
			target: commerceAdminProductDetailsPage.duplicateMenuItem,
			trigger: commerceAdminProductDetailsPage.headerActionsButton,
		});

		await expect(
			commerceAdminProductDetailsPage.convertToDraftMenuItem
		).toBeHidden();

		await commerceAdminProductDetailsPage.headerActionsButton.click();

		await duplicateProduct({
			catalogName: catalog.name,
			commerceAdminProductDetailsPage,
			productName: product.name['en_US'],
		});
	}
);

test(
	'A pending product created through the API in a catalog with a workflow can be duplicated and converted to draft',
	{tag: ['@COMMERCE-9251', '@LPD-110302']},
	async ({
		apiHelpers,
		commerceAdminProductDetailsPage,
		commerceAdminProductPage,
		page,
	}) => {
		const catalog = await postSingleApproverCatalog(apiHelpers);

		const product =
			await apiHelpers.headlessCommerceAdminCatalog.postProduct({
				catalogId: catalog.id,
				productStatus: 1,
			});

		await commerceAdminProductPage.gotoProduct(product.name['en_US']);

		await expect(
			commerceAdminProductDetailsPage.saveAsDraftLink
		).toBeHidden();
		await expect(
			commerceAdminProductDetailsPage.workflowStatusLabel('Pending')
		).toBeVisible();

		const productURL = page.url();

		await duplicateProduct({
			catalogName: catalog.name,
			commerceAdminProductDetailsPage,
			productName: product.name['en_US'],
		});

		await page.goto(productURL);

		expect(await commerceAdminProductDetailsPage.convertToDraft()).toBe(
			'Converting the product status to draft will remove the product from the product catalog. Do you wish to proceed?'
		);
		await expect(
			commerceAdminProductDetailsPage.saveAsDraftLink
		).toBeVisible();
	}
);

test(
	'A product submitted for workflow is pending review until the review task is approved',
	{tag: ['@COMMERCE-9253', '@COMMERCE-9292', '@LPD-110302']},
	async ({
		apiHelpers,
		commerceAdminProductDetailsPage,
		commerceAdminProductPage,
		page,
	}) => {
		const catalog = await postSingleApproverCatalog(apiHelpers);

		const product =
			await apiHelpers.headlessCommerceAdminCatalog.postProduct({
				catalogId: catalog.id,
				productStatus: 2,
			});

		await commerceAdminProductPage.gotoProduct(product.name['en_US']);

		await commerceAdminProductDetailsPage.publish();

		await expect(
			commerceAdminProductDetailsPage.workflowStatusLabel('Pending')
		).toBeVisible();

		let workflowTask: WorkflowTaskDefinition;

		await expect(async () => {
			workflowTask =
				await apiHelpers.headlessAdminWorkflow.getWorkflowTaskByAsset(
					CP_DEFINITION_CLASS_NAME,
					String(product.id)
				);

			expect(workflowTask).toBeTruthy();
		}).toPass();

		await page.reload();

		await expect(
			commerceAdminProductDetailsPage.assigneeButton('Nobody')
		).toBeVisible();
		await expect(
			commerceAdminProductDetailsPage.workflowStatusLabel(
				'Pending (Review)'
			)
		).toBeVisible();

		const userAccount =
			await apiHelpers.headlessAdminUser.getMyUserAccount();

		await apiHelpers.headlessAdminWorkflow.postAssignTaskToUser(
			workflowTask.id,
			userAccount.id
		);

		await page.reload();

		await expect(
			commerceAdminProductDetailsPage.assigneeButton('Me')
		).toBeVisible();
		await expect(
			commerceAdminProductDetailsPage.saveAsDraftLink
		).toBeHidden();

		const productURL = page.url();

		await duplicateProduct({
			catalogName: catalog.name,
			commerceAdminProductDetailsPage,
			productName: product.name['en_US'],
		});

		await apiHelpers.headlessAdminWorkflow.postWorkflowTaskChangeTransition(
			workflowTask.id,
			'approve'
		);

		await page.goto(productURL);

		await expect(
			commerceAdminProductDetailsPage.workflowStatusLabel('Approved')
		).toBeVisible();

		expect(await commerceAdminProductDetailsPage.convertToDraft()).toBe(
			'Converting the product status to draft will remove the product from the product catalog. Do you wish to proceed?'
		);
		await expect(
			commerceAdminProductDetailsPage.saveAsDraftLink
		).toBeVisible();
	}
);
