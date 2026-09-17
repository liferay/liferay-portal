/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Page, expect, mergeTests} from '@playwright/test';

import {apiHelpersTest} from '../../../../fixtures/apiHelpersTest';
import {commercePagesTest} from '../../../../fixtures/commercePagesTest';
import {dataApiHelpersTest} from '../../../../fixtures/dataApiHelpersTest';
import {globalMenuPagesTest} from '../../../../fixtures/globalMenuPagesTest';
import {loginTest} from '../../../../fixtures/loginTest';
import {clickAndExpectToBeVisible} from '../../../../utils/clickAndExpectToBeVisible';
import getRandomString from '../../../../utils/getRandomString';
import {waitForAlert} from '../../../../utils/waitForAlert';
import {fillLocalizedInput} from '../../utils/fillLocalizedInput';

export const test = mergeTests(
	apiHelpersTest,
	globalMenuPagesTest,
	commercePagesTest,
	dataApiHelpersTest,
	loginTest()
);

function acceptNextConfirmation(page: Page) {
	const confirmation = {message: ''};

	page.once('dialog', async (dialog) => {
		confirmation.message = dialog.message();

		await dialog.accept();
	});

	return confirmation;
}

test(
	'Unable to delete specification picklist items',
	{tag: '@LPD-46948'},
	async ({apiHelpers, commerceSpecificationsPage, globalMenuPage, page}) => {
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

		await globalMenuPage.goToCommerce('Specifications');

		await commerceSpecificationsPage
			.specificationNameLink(specification.title.en_US)
			.click();

		await expect(
			page
				.getByRole('cell', {name: picklist.externalReferenceCode})
				.nth(1)
		).toBeVisible();

		await commerceSpecificationsPage
			.specificationPicklistActionButton(picklist.externalReferenceCode)
			.click();
		await commerceSpecificationsPage
			.specificationPicklistDropdownMenu('Edit')
			.click();

		await expect(
			page.frameLocator('iframe').locator('tbody')
		).toContainText(listTypeEntry.externalReferenceCode);

		await commerceSpecificationsPage
			.specificationPicklistItemsActionButton(listTypeEntry.key)
			.click();
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
	async ({apiHelpers, commerceSpecificationsPage, globalMenuPage}) => {
		try {
			await globalMenuPage.goToCommerce('Specifications');

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
			await commerceSpecificationsPage.groupTitle.fill(
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
	async ({apiHelpers, commerceSpecificationsPage, globalMenuPage, page}) => {
		const specification =
			await apiHelpers.headlessCommerceAdminCatalog.postSpecification(
				true,
				0,
				getRandomString(),
				null,
				false
			);

		await globalMenuPage.goToCommerce('Specifications');

		await commerceSpecificationsPage
			.specificationNameLink(specification.title.en_US)
			.click();
		await commerceSpecificationsPage.visibleToggle.check();
		await commerceSpecificationsPage.saveButton.click();

		await waitForAlert(page);

		await expect(commerceSpecificationsPage.visibleToggle).toBeChecked();
	}
);

test(
	'Specification groups and labels can be edited and deleted',
	{
		tag: [
			'@COMMERCE-6281',
			'@COMMERCE-6282',
			'@COMMERCE-6283',
			'@COMMERCE-6284',
			'@LPD-106079',
		],
	},
	async ({apiHelpers, commerceSpecificationsPage, globalMenuPage, page}) => {
		const optionCategory =
			await apiHelpers.headlessCommerceAdminCatalog.postOptionCategory(
				getRandomString()
			);

		const specification1 =
			await apiHelpers.headlessCommerceAdminCatalog.postSpecification(
				true,
				0,
				getRandomString(),
				optionCategory
			);
		const specification2 =
			await apiHelpers.headlessCommerceAdminCatalog.postSpecification(
				true,
				0,
				getRandomString(),
				optionCategory
			);

		const deleteConfirmationMessage =
			'Are you sure you want to delete this? It will be deleted immediately.';

		const newGroupKey = getRandomString();
		const newGroupTitle = getRandomString();
		const newSpecificationKey = getRandomString();
		const newSpecificationTitle = getRandomString();

		await test.step('Edit a specification label', async () => {
			await globalMenuPage.goToCommerce('Specifications');

			await clickAndExpectToBeVisible({
				autoClick: true,
				target: commerceSpecificationsPage.entryRowActionMenuItem(
					'Edit'
				),
				trigger: commerceSpecificationsPage.entryRowActionButton(
					specification1.title.en_US
				),
			});

			await fillLocalizedInput(
				commerceSpecificationsPage.specificationLabel,
				newSpecificationTitle
			);
			await commerceSpecificationsPage.keyContent.fill(
				newSpecificationKey
			);
			await commerceSpecificationsPage.saveButton.click();

			await waitForAlert(page);

			await expect(
				commerceSpecificationsPage.specificationLabel
			).toHaveValue(newSpecificationTitle);
			await expect(commerceSpecificationsPage.keyContent).toHaveValue(
				newSpecificationKey
			);

			await commerceSpecificationsPage.goBack.click();

			await expect(
				commerceSpecificationsPage.entryRow(newSpecificationTitle)
			).toBeVisible();
			await expect(
				commerceSpecificationsPage.defaultGroupCell(
					newSpecificationTitle
				)
			).toHaveText(optionCategory.title.en_US);
		});

		await test.step('Delete a specification label', async () => {
			const confirmation = acceptNextConfirmation(page);

			await clickAndExpectToBeVisible({
				autoClick: true,
				target: commerceSpecificationsPage.entryRowActionMenuItem(
					'Delete'
				),
				trigger: commerceSpecificationsPage.entryRowActionButton(
					specification2.title.en_US
				),
			});

			await waitForAlert(page);

			expect(confirmation.message).toBe(deleteConfirmationMessage);
			await expect(
				commerceSpecificationsPage.entryRow(specification2.title.en_US)
			).toHaveCount(0);
		});

		await test.step('Edit a specification group', async () => {
			await commerceSpecificationsPage.goToSpecificationGroup.click();

			await clickAndExpectToBeVisible({
				autoClick: true,
				target: commerceSpecificationsPage.entryRowActionMenuItem(
					'Edit'
				),
				trigger: commerceSpecificationsPage.entryRowActionButton(
					optionCategory.title.en_US
				),
			});

			await fillLocalizedInput(
				commerceSpecificationsPage.groupTitle,
				newGroupTitle
			);
			await commerceSpecificationsPage.keyContent.fill(newGroupKey);
			await commerceSpecificationsPage.saveButton.click();

			await waitForAlert(page);

			await expect(commerceSpecificationsPage.groupTitle).toHaveValue(
				newGroupTitle
			);
			await expect(commerceSpecificationsPage.keyContent).toHaveValue(
				newGroupKey
			);

			await commerceSpecificationsPage.goBack.click();

			await expect(
				commerceSpecificationsPage.entryRow(newGroupTitle)
			).toBeVisible();
		});

		await test.step('Delete a specification group holding a label', async () => {
			const confirmation = acceptNextConfirmation(page);

			await clickAndExpectToBeVisible({
				autoClick: true,
				target: commerceSpecificationsPage.entryRowActionMenuItem(
					'Delete'
				),
				trigger:
					commerceSpecificationsPage.entryRowActionButton(
						newGroupTitle
					),
			});

			await waitForAlert(page);

			expect(confirmation.message).toBe(deleteConfirmationMessage);
			await expect(
				commerceSpecificationsPage.entryRow(newGroupTitle)
			).toHaveCount(0);

			await commerceSpecificationsPage.goToSpecificationLabel.click();

			await expect(
				commerceSpecificationsPage.entryRow(newSpecificationTitle)
			).toBeVisible();
			await expect(
				commerceSpecificationsPage.defaultGroupCell(
					newSpecificationTitle
				)
			).toHaveText('');
		});
	}
);
