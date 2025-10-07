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
	'Unable to delete specification picklist items',
	{tag: '@LPD-46948'},
	async ({
		apiHelpers,
		applicationsMenuPage,
		commerceSpecificationsPage,
		page,
	}) => {
		const specification =
			await apiHelpers.headlessCommerceAdminCatalog.postSpecification();

		const picklist =
			await apiHelpers.listTypeAdmin.postRandomListTypeDefinition();

		const listTypeEntry = await apiHelpers.listTypeAdmin.postListTypeEntry({
			key: 'item1',
			listTypeDefinitionExternalReferenceCode:
				picklist.externalReferenceCode,
			name_i18n: {en_US: 'item1'},
		});

		await apiHelpers.headlessCommerceAdminCatalog.patchSpecification(
			specification.id,
			[picklist.id]
		);

		await applicationsMenuPage.goToCommerceSpecifications();

		await commerceSpecificationsPage
			.specificationNameLink(specification.title.en_US)
			.click();

		await expect(
			page
				.getByRole('cell', {name: picklist.externalReferenceCode})
				.nth(1)
		).toBeVisible();

		await commerceSpecificationsPage.specificationPicklistActionButton.click();
		await commerceSpecificationsPage
			.specificationPicklistDropdownMenu('Edit')
			.click();

		await expect(
			page.frameLocator('iframe').locator('tbody')
		).toContainText(listTypeEntry.externalReferenceCode);

		await commerceSpecificationsPage.specificationPicklistItemsActionButton.click();
		await commerceSpecificationsPage
			.specificationPicklistDropdownMenuItems('Delete')
			.click();

		await expect(page.getByLabel('Delete Item')).toBeVisible();

		await commerceSpecificationsPage
			.deleteModalButtonAction('Delete')
			.click();

		await waitForAlert(
			page,
			'Success:The picklist item was deleted successfully.'
		);
	}
);

test(
	'Key is not automatically generated when writing new Specifications label',
	{tag: '@LPD-28891'},
	async ({apiHelpers, applicationsMenuPage, commerceSpecificationsPage}) => {
		try {
			await applicationsMenuPage.goToCommerceSpecifications();

			await expect(
				commerceSpecificationsPage.createNewSpecificationsProduct
			).toBeVisible();

			await commerceSpecificationsPage.createNewSpecificationsProduct.click();
			await commerceSpecificationsPage.waitForKey('Specification 1');
			await commerceSpecificationsPage.addDescriptionSpecifications.fill(
				'Specification-1 Description'
			);

			await expect(
				commerceSpecificationsPage.addDescriptionSpecifications
			).toBeVisible();

			await commerceSpecificationsPage.keyContent.fill('specification-1');

			await expect(commerceSpecificationsPage.keyContent).toHaveValue(
				'specification-1'
			);

			await commerceSpecificationsPage.saveButton.click();

			await expect(
				commerceSpecificationsPage.successMessage
			).toBeVisible();

			await commerceSpecificationsPage.goBack.click();
			await commerceSpecificationsPage.goToSpecificationGroup.click();
			await commerceSpecificationsPage.createNewSpecificationsProductGroup.click();
			await commerceSpecificationsPage.addNewProductSpecificationsGroup.fill(
				'Specification group'
			);
			await commerceSpecificationsPage.addDescriptionSpecificationsGroup.fill(
				'Specification group Description'
			);

			await expect(commerceSpecificationsPage.keyContent).toHaveValue(
				'Specification group'
			);

			await commerceSpecificationsPage.saveButton.click();

			await expect(
				commerceSpecificationsPage.successMessage
			).toBeVisible();
		}
		finally {
			const specifications =
				await apiHelpers.headlessCommerceAdminCatalog.getSpecifications();

			for (let i = 0; i < specifications.totalCount; i++) {
				if (specifications.items[i].title.en_US === 'Specification 1') {
					apiHelpers.data.push({
						id: specifications.items[i].id,
						type: 'specification',
					});
				}
			}

			const optionCategory =
				await apiHelpers.headlessCommerceAdminCatalog.getOptionCategories();

			for (let i = 0; i < optionCategory.totalCount; i++) {
				if (
					optionCategory.items[i].title.en_US ===
					'Specification group'
				) {
					apiHelpers.data.push({
						id: optionCategory.items[i].id,
						type: 'optionCategory',
					});
				}
			}
		}
	}
);

test(
	'Specification visibility is correctly saved',
	{tag: '@LPD-48103'},
	async ({
		apiHelpers,
		applicationsMenuPage,
		commerceSpecificationsPage,
		page,
	}) => {
		const specification =
			await apiHelpers.headlessCommerceAdminCatalog.postSpecification(
				true,
				0,
				getRandomString(),
				null,
				false
			);

		await applicationsMenuPage.goToCommerceSpecifications();

		await commerceSpecificationsPage
			.specificationNameLink(specification.title.en_US)
			.click();
		await commerceSpecificationsPage.visibleToggle.check();
		await commerceSpecificationsPage.saveButton.click();

		await waitForAlert(page);

		await expect(commerceSpecificationsPage.visibleToggle).toBeChecked();
	}
);
