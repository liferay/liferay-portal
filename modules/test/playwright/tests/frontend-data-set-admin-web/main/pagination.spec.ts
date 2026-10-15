/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {dataSetManagerApiHelpersTest} from '../../../fixtures/dataSetManagerApiHelpersTest';
import {featureFlagsTest} from '../../../fixtures/featureFlagsTest';
import {loginTest} from '../../../fixtures/loginTest';
import getRandomString from '../../../utils/getRandomString';
import {dataSetManagerSetupTest} from './fixtures/dataSetManagerSetupTest';
import {paginationPageTest} from './fixtures/paginationPageTest';

export const test = mergeTests(
	dataSetManagerApiHelpersTest,
	featureFlagsTest({
		'LPS-164563': {enabled: true},
	}),
	loginTest(),
	dataSetManagerSetupTest,
	paginationPageTest
);

let dataSetERC: string;
let dataSetLabel: string;

test.beforeEach(async ({dataSetManagerApiHelpers, paginationPage}) => {
	dataSetERC = getRandomString();
	dataSetLabel = getRandomString();

	await test.step('Create data set', async () => {
		await dataSetManagerApiHelpers.createDataSet({
			erc: dataSetERC,
			label: dataSetLabel,
		});
	});

	await test.step('Navigate to Pagination section', async () => {
		await paginationPage.goto({
			dataSetLabel,
		});

		await expect(paginationPage.header).toBeInViewport();
	});

	await test.step('Check list items per page has default values', async () => {
		await expect(paginationPage.listOfItemsPerPageTextarea).toHaveValue(
			'4, 8, 20, 40, 60'
		);
	});

	await test.step('Check default items per page has a default value', async () => {
		await expect(paginationPage.defaultItemsPerPageInput).toHaveValue('20');
	});
});

test.afterEach(async ({dataSetManagerApiHelpers}) => {
	await dataSetManagerApiHelpers.deleteDataSet({erc: dataSetERC});
});

test('Default pagination configuration', async ({paginationPage}) => {
	await test.step('Check default values are valid', async () => {
		await expect(paginationPage.saveButton).toBeEnabled();
	});

	await test.step('Save default pagination configuration', async () => {
		await paginationPage.saveButton.click();

		await paginationPage.toastContainer.isVisible();

		await paginationPage.page
			.getByText('Success:Your request completed successfully.')
			.waitFor();

		await paginationPage.toastContainer
			.getByRole('button', {
				name: 'Close',
			})
			.click();

		await paginationPage.toastContainer.isHidden();
	});
});

test('Update pagination configuration', async ({paginationPage}) => {
	await test.step('Update items per page', async () => {
		await paginationPage.listOfItemsPerPageTextarea.clear();
		await paginationPage.listOfItemsPerPageTextarea.fill('5, 10, 15');
	});

	await test.step('Update default items per page', async () => {
		await paginationPage.defaultItemsPerPageInput.clear();
		await paginationPage.defaultItemsPerPageInput.fill('10');
		await paginationPage.defaultItemsPerPageInput.blur();
	});

	await test.step('Check default values are valid', async () => {
		await expect(paginationPage.saveButton).toBeEnabled();
	});

	await test.step('Save updated pagination configuration', async () => {
		await paginationPage.saveButton.click();

		await paginationPage.toastContainer.isVisible();

		await paginationPage.page
			.getByText('Success:Your request completed successfully.')
			.waitFor();

		await paginationPage.toastContainer
			.getByRole('button', {
				name: 'Close',
			})
			.click();

		await paginationPage.toastContainer.isHidden();
	});

	await test.step('Reload and check that updated values are present', async () => {
		await paginationPage.page.reload();

		await paginationPage.goto({
			dataSetLabel,
		});

		await expect(paginationPage.listOfItemsPerPageTextarea).toHaveValue(
			'5, 10, 15'
		);

		await expect(paginationPage.defaultItemsPerPageInput).toHaveValue('10');
	});
});

test('Pagination configuration limits', async ({paginationPage}) => {
	await test.step('Check list items per page cannot be empty', async () => {
		await paginationPage.listOfItemsPerPageTextarea.clear();
		await paginationPage.listOfItemsPerPageTextarea.blur();

		await expect(paginationPage.fieldRequiredError).toBeVisible();
	});

	await test.step('Check list items per page cannot have more than 1000 items option', async () => {
		await paginationPage.listOfItemsPerPageTextarea.clear();
		await paginationPage.listOfItemsPerPageTextarea.fill('5, 1005');
		await paginationPage.listOfItemsPerPageTextarea.blur();

		await expect(paginationPage.fieldItemsLimitError).toBeVisible();

		await expect(paginationPage.saveButton).toBeDisabled();
	});

	await test.step('Check list items per page cannot have more than 25 elemtns', async () => {
		await paginationPage.listOfItemsPerPageTextarea.clear();
		await paginationPage.listOfItemsPerPageTextarea.fill(
			'1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25,26'
		);
		await paginationPage.listOfItemsPerPageTextarea.blur();

		await expect(paginationPage.fieldItemsNumberError).toBeVisible();

		await expect(paginationPage.saveButton).toBeDisabled();
	});

	await test.step('Check default items per page has a default value', async () => {
		await expect(paginationPage.defaultItemsPerPageInput).toHaveValue('20');
	});

	await test.step('Check default items per page cannot be empty', async () => {
		await paginationPage.defaultItemsPerPageInput.clear();
		await paginationPage.defaultItemsPerPageInput.blur();

		await expect(paginationPage.fieldRequiredError).toBeVisible();
	});

	await test.step('Check default items per page must match one value from List items per page field', async () => {
		await paginationPage.listOfItemsPerPageTextarea.clear();
		await paginationPage.listOfItemsPerPageTextarea.fill('5, 10, 15');
		await paginationPage.listOfItemsPerPageTextarea.blur();

		await paginationPage.defaultItemsPerPageInput.clear();
		await paginationPage.defaultItemsPerPageInput.blur();
		await paginationPage.defaultItemsPerPageInput.fill('20');

		await expect(paginationPage.defaultItemsPerPageInput).toBeVisible();

		await expect(paginationPage.saveButton).toBeDisabled();
	});
});
