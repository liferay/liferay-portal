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
import getRandomString from '../../../../utils/getRandomString';
import {waitForAlert} from '../../../../utils/waitForAlert';

export const test = mergeTests(
	apiHelpersTest,
	applicationsMenuPageTest,
	commercePagesTest,
	dataApiHelpersTest,
	loginTest()
);
test(
	'Recursive Product Window Reopens When Saving Specification Value',
	{tag: '@LPD-46276'},
	async ({
		apiHelpers,
		commerceAdminProductDetailsPage,
		commerceAdminProductPage,
		page,
	}) => {
		const catalog =
			await apiHelpers.headlessCommerceAdminCatalog.postCatalog({
				name: getRandomString(),
			});

		apiHelpers.data.push({id: catalog.id, type: 'catalog'});

		const specification =
			await apiHelpers.headlessCommerceAdminCatalog.postSpecification();

		const product =
			await apiHelpers.headlessCommerceAdminCatalog.postProduct({
				catalogId: catalog.id,
				name: {en_US: getRandomString()},
				productSpecifications: [
					{
						specificationKey: specification.key,
						value: {
							en_US: getRandomString(),
						},
					},
				],
			});

		apiHelpers.data.push({id: product.id, type: 'product'});

		await commerceAdminProductPage.gotoProduct(product.name['en_US']);

		await expect(
			await page.getByText(specification.title.en_US)
		).toBeVisible();

		await commerceAdminProductDetailsPage.ellipsisProductSpecification.click();
		await (
			await commerceAdminProductDetailsPage.dropdownProductSpecification(
				'Edit'
			)
		).click();

		const randomSpecificationValue = getRandomString();

		await commerceAdminProductDetailsPage.editFrameSpecificationProductValue.fill(
			randomSpecificationValue
		);

		await commerceAdminProductDetailsPage.editFrameSaveButton.click();

		await waitForAlert(
			commerceAdminProductDetailsPage.ellipsisFrameProductSpecification
		);

		await commerceAdminProductDetailsPage.closeEditFrame.click();

		await expect(page.getByText(randomSpecificationValue)).toBeVisible();
	}
);

test(
	'Product specification visibility is correctly saved',
	{tag: '@LPD-48103'},
	async ({
		apiHelpers,
		commerceAdminProductDetailsPage,
		commerceAdminProductPage,
	}) => {
		const picklist =
			await apiHelpers.listTypeAdmin.postRandomListTypeDefinition();

		try {
			const specification =
				await apiHelpers.headlessCommerceAdminCatalog.postSpecification(
					true,
					0,
					getRandomString(),
					null,
					false,
					[picklist.id]
				);

			await apiHelpers.listTypeAdmin.postListTypeEntry({
				key: 'item1',
				listTypeDefinitionExternalReferenceCode:
					picklist.externalReferenceCode,
				name_i18n: {en_US: 'item1'},
			});

			const catalog =
				await apiHelpers.headlessCommerceAdminCatalog.postCatalog();

			const product =
				await apiHelpers.headlessCommerceAdminCatalog.postProduct({
					catalogId: catalog.id,
				});

			await commerceAdminProductPage.gotoProduct(product.name['en_US']);

			await commerceAdminProductDetailsPage.addOrEditProductSpecification(
				'Add an Existing Specification',
				specification.title.en_US,
				'item1'
			);
			await commerceAdminProductDetailsPage.editOrDeleteProductSpecification(
				'Edit',
				'item1'
			);
			await commerceAdminProductDetailsPage.visibleToggle.check();
			await commerceAdminProductDetailsPage.editFrameSaveButton.click();

			await expect(
				commerceAdminProductDetailsPage.editSuccessMessage
			).toBeVisible();
			await expect(
				commerceAdminProductDetailsPage.visibleToggle
			).toBeChecked();
		}
		finally {
			await apiHelpers.listTypeAdmin.deleteListTypeDefinition(
				picklist.id
			);
		}
	}
);

test(
	'Product specification can be added and saved with a catalog in a different language',
	{tag: '@LPD-52730'},
	async ({
		apiHelpers,
		commerceAdminProductDetailsPage,
		commerceAdminProductPage,
		page,
	}) => {
		const catalog =
			await apiHelpers.headlessCommerceAdminCatalog.postCatalog({
				defaultLanguageId: 'de_DE',
				name: getRandomString(),
			});

		const product =
			await apiHelpers.headlessCommerceAdminCatalog.postProduct({
				catalogId: catalog.id,
				name: {de_DE: getRandomString(), en_US: getRandomString()},
			});

		const specification =
			await apiHelpers.headlessCommerceAdminCatalog.postSpecification();

		await commerceAdminProductPage.gotoProduct(product.name['en_US']);

		await commerceAdminProductDetailsPage.addExistingProductSpecification(
			'Add an Existing Specification',
			specification.title.en_US,
			'item1'
		);

		await expect(page.getByText(specification.title.en_US)).toBeVisible();

		await commerceAdminProductDetailsPage.createSpecificationProduct(
			'Create New Specification',
			'Specification-1',
			'item2'
		);

		await expect(page.getByText('Specification-1')).toBeVisible();
	}
);
