/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {apiHelpersTest} from '../../../../fixtures/apiHelpersTest';
import {applicationsMenuPageTest} from '../../../../fixtures/applicationsMenuPageTest';
import {commercePagesTest} from '../../../../fixtures/commercePagesTest';
import {dataApiHelpersTest} from '../../../../fixtures/dataApiHelpersTest';
import {loginTest} from '../../../../fixtures/loginTest';

export const test = mergeTests(
	apiHelpersTest,
	applicationsMenuPageTest,
	commercePagesTest,
	dataApiHelpersTest,
	loginTest()
);

test(
	'Picklist on product specifications page',
	{tag: '@LPD-22572'},
	async ({
		apiHelpers,
		commerceAdminProductDetailsPage,
		commerceAdminProductPage,
		page,
	}) => {
		const picklist =
			await apiHelpers.listTypeAdmin.postRandomListTypeDefinition();

		try {
			const catalog =
				await apiHelpers.headlessCommerceAdminCatalog.postCatalog({
					name: 'Catalog',
				});

			apiHelpers.data.push({id: catalog.id, type: 'catalog'});

			const product =
				await apiHelpers.headlessCommerceAdminCatalog.postProduct({
					catalogId: catalog.id,
					name: {en_US: 'Product1'},
				});

			apiHelpers.data.push({id: product.id, type: 'product'});

			const specification =
				await apiHelpers.headlessCommerceAdminCatalog.postSpecification();

			await apiHelpers.listTypeAdmin.postListTypeEntry({
				key: 'item1',
				listTypeDefinitionExternalReferenceCode:
					picklist.externalReferenceCode,
				name_i18n: {en_US: 'item1'},
			});

			await apiHelpers.headlessCommerceAdminCatalog.patchSpecification(
				specification.id,
				[picklist.id]
			);

			await commerceAdminProductPage.gotoProduct(product.name['en_US']);

			await commerceAdminProductDetailsPage.addOrEditProductSpecification(
				'Add an Existing Specification',
				specification.title.en_US,
				'item1'
			);

			await expect(
				page.getByText(specification.title.en_US)
			).toBeVisible();

			await apiHelpers.listTypeAdmin.postListTypeEntry({
				key: 'item2',
				listTypeDefinitionExternalReferenceCode:
					picklist.externalReferenceCode,
				name_i18n: {en_US: 'item2'},
			});

			await commerceAdminProductDetailsPage.editOrDeleteProductSpecification(
				'Edit',
				'item2'
			);

			await expect(
				commerceAdminProductDetailsPage.editSuccessMessage
			).toBeVisible();

			await commerceAdminProductDetailsPage.closeEditFrame.click();

			await expect(page.getByText('item2')).toBeVisible();

			await commerceAdminProductDetailsPage.createSpecificationProduct(
				'Create New Specification',
				'Specification-1',
				'item3'
			);

			await expect(page.getByText('item3')).toBeVisible();

			const specifications =
				await apiHelpers.headlessCommerceAdminCatalog.getSpecifications();

			for (let i = 0; i < specifications.totalCount; i++) {
				await apiHelpers.headlessCommerceAdminCatalog.deleteSpecification(
					specifications.items[i].id
				);
			}
		}
		finally {
			await apiHelpers.listTypeAdmin.deleteListTypeDefinition(
				picklist.id
			);
		}
	}
);

test(
	'Multiple picklist on product specifications page',
	{tag: '@LPD-29336'},
	async ({
		apiHelpers,
		commerceAdminProductDetailsPage,
		commerceAdminProductPage,
		page,
	}) => {
		const picklist1 =
			await apiHelpers.listTypeAdmin.postRandomListTypeDefinition();
		const picklist2 =
			await apiHelpers.listTypeAdmin.postRandomListTypeDefinition();

		try {
			const catalog =
				await apiHelpers.headlessCommerceAdminCatalog.postCatalog({
					name: 'Catalog',
				});

			apiHelpers.data.push({id: catalog.id, type: 'catalog'});

			const product =
				await apiHelpers.headlessCommerceAdminCatalog.postProduct({
					catalogId: catalog.id,
					name: {en_US: 'Product1'},
				});

			apiHelpers.data.push({id: product.id, type: 'product'});

			const specification =
				await apiHelpers.headlessCommerceAdminCatalog.postSpecification();

			await apiHelpers.listTypeAdmin.postListTypeEntry({
				key: 'item1',
				listTypeDefinitionExternalReferenceCode:
					picklist1.externalReferenceCode,
				name_i18n: {en_US: 'item1'},
			});

			await apiHelpers.listTypeAdmin.postListTypeEntry({
				key: 'item2',
				listTypeDefinitionExternalReferenceCode:
					picklist2.externalReferenceCode,
				name_i18n: {en_US: 'item2'},
			});

			await apiHelpers.headlessCommerceAdminCatalog.patchSpecification(
				specification.id,
				[picklist1.id, picklist2.id]
			);

			await commerceAdminProductPage.gotoProduct(product.name['en_US']);

			await commerceAdminProductDetailsPage.addOrEditProductSpecification(
				'Add an Existing Specification',
				specification.title.en_US,
				'item1'
			);

			await expect(page.getByText('item1')).toBeVisible();

			await commerceAdminProductDetailsPage.addOrEditProductSpecification(
				'Add an Existing Specification',
				specification.title.en_US,
				'item2'
			);

			await expect(page.getByText('item2')).toBeVisible();

			const specifications =
				await apiHelpers.headlessCommerceAdminCatalog.getSpecifications();

			for (let i = 0; i < specifications.totalCount; i++) {
				await apiHelpers.headlessCommerceAdminCatalog.deleteSpecification(
					specifications.items[i].id
				);
			}
		}
		finally {
			await apiHelpers.listTypeAdmin.deleteListTypeDefinition(
				picklist1.id
			);
			await apiHelpers.listTypeAdmin.deleteListTypeDefinition(
				picklist2.id
			);
		}
	}
);

test(
	'Missing error handling for missing Specifications values',
	{tag: '@LPD-45255'},
	async ({
		apiHelpers,
		commerceAdminProductDetailsPage,
		commerceAdminProductPage,
		page,
	}) => {
		const picklist =
			await apiHelpers.listTypeAdmin.postRandomListTypeDefinition();

		try {
			const catalog =
				await apiHelpers.headlessCommerceAdminCatalog.postCatalog({
					name: 'Catalog',
				});

			apiHelpers.data.push({id: catalog.id, type: 'catalog'});

			const product =
				await apiHelpers.headlessCommerceAdminCatalog.postProduct({
					catalogId: catalog.id,
					name: {en_US: 'Product1'},
				});

			apiHelpers.data.push({id: product.id, type: 'product'});

			const specification =
				await apiHelpers.headlessCommerceAdminCatalog.postSpecification();

			await apiHelpers.listTypeAdmin.postListTypeEntry({
				key: 'item1',
				listTypeDefinitionExternalReferenceCode:
					picklist.externalReferenceCode,
				name_i18n: {en_US: 'item1'},
			});

			await apiHelpers.headlessCommerceAdminCatalog.patchSpecification(
				specification.id,
				[picklist.id]
			);

			await commerceAdminProductPage.gotoProduct(product.name['en_US']);

			await commerceAdminProductDetailsPage.addOrEditProductSpecification(
				'Add an Existing Specification',
				specification.title.en_US
			);

			const selectSpecificationValueIframe = page
				.frameLocator('iframe')
				.nth(2)
				.locator('select[name="listTypeEntriesSelect"]');

			await expect(selectSpecificationValueIframe).toHaveAttribute(
				'required'
			);

			await commerceAdminProductDetailsPage.frameChooseSpecificationValue(
				'item1'
			);
			await commerceAdminProductDetailsPage.frameSubmitSpecification.click();

			await expect(page.getByText('item1')).toBeVisible();

			await commerceAdminProductDetailsPage.createSpecificationProduct(
				'Create New Specification',
				'Specification-1'
			);

			const inputSpecificationValueIframe = page
				.frameLocator('iframe')
				.nth(2)
				.getByRole('textbox')
				.nth(1);

			await expect(inputSpecificationValueIframe).toHaveAttribute(
				'required'
			);

			await commerceAdminProductDetailsPage.createNewValueSpecificationProduct.fill(
				'item-2'
			);
			await commerceAdminProductDetailsPage.frameSubmitSpecification.click();

			await expect(page.getByText('item-2')).toBeVisible();

			const specifications =
				await apiHelpers.headlessCommerceAdminCatalog.getSpecifications();

			for (let i = 0; i < specifications.totalCount; i++) {
				await apiHelpers.headlessCommerceAdminCatalog.deleteSpecification(
					specifications.items[i].id
				);
			}
		}
		finally {
			await apiHelpers.listTypeAdmin.deleteListTypeDefinition(
				picklist.id
			);
		}
	}
);
