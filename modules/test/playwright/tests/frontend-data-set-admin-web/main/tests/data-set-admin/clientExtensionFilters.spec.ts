/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {featureFlagsTest} from '../../../../../fixtures/featureFlagsTest';
import {loginTest} from '../../../../../fixtures/loginTest';
import getRandomString from '../../../../../utils/getRandomString';
import {waitForAlert} from '../../../../../utils/waitForAlert';
import {dataSetManagerApiHelpersTest} from '../../fixtures/dataSetManagerApiHelpersTest';
import {dataSetManagerSetupTest} from './fixtures/dataSetManagerSetupTest';
import {filtersPageTest} from './fixtures/filtersPageTest';

const test = mergeTests(
	dataSetManagerApiHelpersTest,
	filtersPageTest,
	featureFlagsTest({
		'LPS-178052': {enabled: true},
	}),
	loginTest(),
	dataSetManagerSetupTest
);

let dataSetERC: string;
let dataSetLabel: string;
const clientExtensionName = 'Liferay Sample FDS Filter';
const CLIENT_EXTENSION_FILTER_DISPLAY_TYPE = 'Client Extension Filter';
const DATE_FIELD_NAME = 'dateCreated';
const NAME_FIELD_NAME = 'fieldName';

test.beforeEach(async ({dataSetManagerApiHelpers, filtersPage}) => {
	dataSetERC = getRandomString();
	dataSetLabel = getRandomString();

	await test.step('Create a data set', async () => {
		await dataSetManagerApiHelpers.createDataSet({
			erc: dataSetERC,
			label: dataSetLabel,
		});
	});

	await test.step('Navigate to Filters section', async () => {
		await filtersPage.goto({
			dataSetLabel,
		});
	});
});

test.afterEach(async ({dataSetManagerApiHelpers}) => {
	await dataSetManagerApiHelpers.deleteDataSet({erc: dataSetERC});
});

test('Can not create a Client Extension Filter in DSM', async ({
	filtersPage,
	page,
}) => {
	const filterLabel = getRandomString();

	await test.step('"No default filters were created." message appears when there are no filters', async () => {
		await expect(
			page.getByText('No default filters were created')
		).toBeVisible();
	});

	await test.step('Check that mandatory missing fields display an error message', async () => {
		await expect(filtersPage.newFilterButton).toBeVisible();

		await filtersPage.newFilterButton.click();

		const menuItem = filtersPage.page.getByRole('menuitem', {
			name: 'Client Extension',
		});

		await expect(menuItem).toBeVisible();

		await menuItem.click();

		await filtersPage.saveAddFilterForm();

		await expect(page.getByText('This field is required.')).toHaveCount(3);

		await filtersPage.newClientExtensionFilterForm.nameInput.click();
		await filtersPage.newClientExtensionFilterForm.nameInput.fill(
			filterLabel
		);
		await filtersPage.saveAddFilterForm();

		await expect(page.getByText('This field is required.')).toHaveCount(2);

		await filtersPage.newClientExtensionFilterForm.filterBySelectButton.click();

		await filtersPage.fieldSelectModalPage.selectField({
			fieldName: DATE_FIELD_NAME,
		});

		await filtersPage.fieldSelectModalPage.saveAddFieldsModal();

		await expect(page.getByText('This field is required.')).toHaveCount(1);

		await filtersPage.newClientExtensionFilterForm.clientExtensionDropdown.click();
		await page.getByRole('option', {name: clientExtensionName}).click();

		await expect(page.getByText('This field is required.')).toHaveCount(0);

		await filtersPage.cancelAddFilterForm();
	});
});

test('Can create a Client Extension Filter in DSM', async ({
	filtersPage,
	page,
}) => {
	const filterLabel = getRandomString();

	await test.step('Create a client extension filter', async () => {
		await filtersPage.createClientExtensionFilter({
			clientExtension: clientExtensionName,
			filterBy: DATE_FIELD_NAME,
			name: filterLabel,
		});

		await filtersPage.saveAddFilterForm();
	});

	await test.step('Check that the client extension filter is in the list with correct fields', async () => {
		await expect(
			page.getByRole('cell', {
				exact: true,
				name: DATE_FIELD_NAME,
			})
		).toBeVisible();

		await expect(
			page.getByRole('cell', {
				exact: true,
				name: CLIENT_EXTENSION_FILTER_DISPLAY_TYPE,
			})
		).toBeVisible();
	});

	await test.step('Fill a client extension filter form and cancel the creation', async () => {
		await filtersPage.createClientExtensionFilter({
			clientExtension: clientExtensionName,
			filterBy: NAME_FIELD_NAME,
			name: filterLabel,
		});

		await filtersPage.cancelAddFilterForm();
	});

	await test.step('Check that only one client extension filter is in the list', async () => {
		await expect(
			page.getByRole('cell', {
				exact: true,
				name: DATE_FIELD_NAME,
			})
		).toBeVisible();

		await expect(
			page.getByRole('cell', {
				exact: true,
				name: NAME_FIELD_NAME,
			})
		).not.toBeVisible();
	});
});

test(
	'Can deactivate and activate a Client Extension Filter in DSM',
	{tag: '@LPD-39965'},
	async ({filtersPage, page}) => {
		const filterLabel = getRandomString();

		await test.step('Create a client extension filter', async () => {
			await filtersPage.createClientExtensionFilter({
				clientExtension: clientExtensionName,
				filterBy: DATE_FIELD_NAME,
				name: filterLabel,
			});

			await filtersPage.saveAddFilterForm();

			await waitForAlert(page);
		});

		await test.step('Check that the client extension filter is in the list and it is "Active" by default', async () => {
			await expect(
				page.getByRole('cell', {
					exact: true,
					name: DATE_FIELD_NAME,
				})
			).toBeVisible();

			await expect(filtersPage.activeToggle.first()).toBeVisible();
		});

		await test.step('Deactivate the client extension filter', async () => {
			await filtersPage.activeToggle.first().click();

			await waitForAlert(page);

			await expect(filtersPage.inactiveToggle.first()).toBeVisible();
		});

		await test.step('Activate the client extension filter', async () => {
			await filtersPage.inactiveToggle.first().click();

			await waitForAlert(page);

			await expect(filtersPage.activeToggle.first()).toBeVisible();
		});
	}
);
