/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';
import {v4 as uuidv4} from 'uuid';

import {featureFlagsTest} from '../../../../../fixtures/featureFlagsTest';
import {loginTest} from '../../../../../fixtures/loginTest';
import {clickAndExpectToBeVisible} from '../../../../../utils/clickAndExpectToBeVisible';
import getRandomString from '../../../../../utils/getRandomString';
import {waitForAlert} from '../../../../../utils/waitForAlert';
import {dataSetManagerApiHelpersTest} from '../../fixtures/dataSetManagerApiHelpersTest';
import {picklistApiHelpersTest} from '../../fixtures/picklistApiHelpersTest';
import {API_ENDPOINT_PATH} from '../../utils/constants';
import {dataSetManagerSetupTest} from './fixtures/dataSetManagerSetupTest';
import {filtersPageTest} from './fixtures/filtersPageTest';

const SELECTION_API_HEADLESS_FILTER_NAME = 'Selection API Headless filter';
const SELECTION_DISPLAY_TYPE = 'Selection Filter';
const SELECTION_PICKLIST_FILTER_NAME = 'Selection Picklist filter';
const SELECTION_PICKLIST_NO_PRESELECTED_VALUES_FILTER_NAME =
	'Selection Picklist filter without preselected values';

// @ts-ignore

const PICKLIST_VALUE_KEY = uuidv4().replaceAll('-', '');
const PICKLIST_VALUE_NAME = getRandomString();

const test = mergeTests(
	dataSetManagerApiHelpersTest,
	featureFlagsTest({
		'LPS-178052': {enabled: true},
	}),
	filtersPageTest,
	loginTest(),
	picklistApiHelpersTest,
	dataSetManagerSetupTest
);

let dataSetERC: string;
let dataSetLabel: string;
let picklistName: string;

test.beforeEach(
	async ({dataSetManagerApiHelpers, filtersPage, picklistApiHelpers}) => {
		dataSetERC = getRandomString();
		dataSetLabel = getRandomString();
		picklistName = getRandomString();

		await dataSetManagerApiHelpers.createDataSet({
			erc: dataSetERC,
			label: dataSetLabel,
		});

		await picklistApiHelpers.createPicklist({
			name: picklistName,
		});

		await picklistApiHelpers.editPicklist({
			key: PICKLIST_VALUE_KEY,
			name: picklistName,
			value: PICKLIST_VALUE_NAME,
		});

		await test.step('Navigate to the Filters tab', async () => {
			await filtersPage.goto({
				dataSetLabel,
			});
		});
	}
);

test.afterEach(async ({dataSetManagerApiHelpers, picklistApiHelpers}) => {
	await dataSetManagerApiHelpers.deleteDataSet({erc: dataSetERC});

	await picklistApiHelpers.deletePicklist(picklistName);
});

test('Can create and delete a selection filter from picklist source', async ({
	filtersPage,
	page,
}) => {
	await test.step('Can not create a selection filter without filling mandatory fields', async () => {
		await filtersPage.openNewFilterForm({
			dropdownItemLabel: 'Selection',
		});

		await filtersPage.saveAddFilterForm();

		await expect(page.getByText('This field is required.')).toHaveCount(3);

		await filtersPage.cancelAddFilterForm();
	});

	await test.step('Create a selection filter from picklist source', async () => {
		await filtersPage.createSelectionFilterPicklist({
			filterBy: 'externalReferenceCode',
			filterMode: 'Include',
			name: SELECTION_PICKLIST_FILTER_NAME,
			preselectedValues: [PICKLIST_VALUE_NAME],
			selectionType: 'Single',
			source: picklistName,
			sourceType: 'Object Picklist',
		});

		await filtersPage.saveAddFilterForm();
	});

	await test.step('Check that the selection filter is in the list', async () => {
		await expect(
			page.getByRole('cell', {
				exact: true,
				name: SELECTION_PICKLIST_FILTER_NAME,
			})
		).toBeVisible();
	});

	await test.step('Create a selection filter from picklist source without preselected values', async () => {
		await filtersPage.createSelectionFilterPicklist({
			filterBy: 'fieldName',
			name: SELECTION_PICKLIST_NO_PRESELECTED_VALUES_FILTER_NAME,
			preselectedValues: [],
			selectionType: 'Single',
			source: picklistName,
			sourceType: 'Object Picklist',
		});

		await filtersPage.saveAddFilterForm();
	});

	await test.step('Check that the selection filter is also the list', async () => {
		await expect(
			page.getByRole('cell', {
				exact: true,
				name: SELECTION_PICKLIST_NO_PRESELECTED_VALUES_FILTER_NAME,
			})
		).toBeVisible();
	});

	await test.step('Can search for a filter', async () => {
		await filtersPage.searchInput.click();
		await filtersPage.searchInput.fill(
			SELECTION_PICKLIST_NO_PRESELECTED_VALUES_FILTER_NAME
		);

		await filtersPage.searchButton.click();

		await expect(
			page.getByRole('cell', {
				exact: true,
				name: SELECTION_PICKLIST_FILTER_NAME,
			})
		).not.toBeVisible();

		await expect(
			page.getByRole('cell', {
				exact: true,
				name: SELECTION_PICKLIST_NO_PRESELECTED_VALUES_FILTER_NAME,
			})
		).toBeVisible();

		await filtersPage.searchInput.click();
		await filtersPage.searchInput.fill('');

		await filtersPage.searchButton.click();
	});

	await test.step('Delete the filter, but cancel action', async () => {
		await filtersPage
			.getRowByText(SELECTION_PICKLIST_FILTER_NAME)
			.locator('.actions-cell button')
			.click();

		const deleteButton = filtersPage.page.getByRole('menuitem', {
			name: 'Delete',
		});

		await expect(deleteButton).toBeInViewport();

		await deleteButton.click();

		const cancelDeleteButton = page.getByRole('button', {
			name: 'Cancel',
		});

		await cancelDeleteButton.waitFor();

		await cancelDeleteButton.click();
	});

	await test.step('Check that the selection filter is still in the list', async () => {
		await expect(
			page.getByRole('cell', {
				exact: true,
				name: SELECTION_PICKLIST_FILTER_NAME,
			})
		).toBeVisible();
	});

	await test.step('Delete the filter', async () => {
		await filtersPage
			.getRowByText(SELECTION_PICKLIST_FILTER_NAME)
			.locator('.actions-cell button')
			.click();

		const deleteButton = filtersPage.page.getByRole('menuitem', {
			name: 'Delete',
		});

		await expect(deleteButton).toBeInViewport();

		await deleteButton.click();

		const confirmDeleteButton = page.getByRole('button', {
			name: 'Delete',
		});

		await confirmDeleteButton.waitFor();

		await confirmDeleteButton.click();
	});

	await test.step('Check that the selection filter is no longer in the list', async () => {
		await expect(
			page.getByRole('cell', {
				exact: true,
				name: SELECTION_PICKLIST_FILTER_NAME,
			})
		).not.toBeVisible();
	});
});

test('Can create a selection filter with API Headless source', async ({
	filtersPage,
	page,
}) => {
	await test.step('Create a selection filter from API Headless source', async () => {
		await filtersPage.createSelectionFilterApiHeadless({
			filterBy: 'externalReferenceCode',
			filterMode: 'Include',
			itemKey: 'id',
			itemLabel: 'label',
			name: SELECTION_API_HEADLESS_FILTER_NAME,
			preselectedValues: [dataSetLabel],
			restApplication: `${API_ENDPOINT_PATH}`,
			restEndpoint: '/',
			restSchema: 'DataSet',
			selectionType: 'Single',
			sourceType: 'API REST Application',
		});

		await filtersPage.saveAddFilterForm();
	});

	await test.step('Check that the selection filter is in the list', async () => {
		await expect(
			page.getByRole('cell', {
				exact: true,
				name: SELECTION_API_HEADLESS_FILTER_NAME,
			})
		).toBeVisible();
	});
});

test('Preselected filter values are checked in the multiSelect', async ({
	filtersPage,
	page,
}) => {
	await test.step('Create a selection filter', async () => {
		await filtersPage.createSelectionFilterPicklist({
			filterBy: 'externalReferenceCode',
			filterMode: 'Include',
			name: 'Selection Filter',
			preselectedValues: [PICKLIST_VALUE_NAME],
			selectionType: 'Single',
			source: picklistName,
			sourceType: 'Object Picklist',
		});

		await filtersPage.saveAddFilterForm();
	});

	await test.step('Open the edit filter form', async () => {
		await filtersPage.goto({
			dataSetLabel,
		});

		const filterActionsButton = page
			.getByRole('cell', {name: 'Actions'})
			.getByRole('button');

		await expect(filterActionsButton).toBeVisible();

		await clickAndExpectToBeVisible({
			autoClick: true,
			target: page.getByRole('menuitem', {name: 'Edit'}),
			trigger: filterActionsButton,
		});

		const dialogFilterSourceSubtitle = page.getByRole('heading', {
			name: 'Filter Source',
		});

		await expect(dialogFilterSourceSubtitle).toBeVisible();

		const dialogFilterOptionsSubtitle = page.getByRole('heading', {
			name: 'Filter Options',
		});

		await expect(dialogFilterOptionsSubtitle).toBeVisible();
	});

	await test.step('Check that the preselected value is checked', async () => {
		await page.getByLabel('Preselected Values').click();

		await expect(
			page.getByLabel(PICKLIST_VALUE_NAME, {exact: true})
		).toBeChecked();
	});
});

test(
	'Can create and edit a selection filter with API Headless source using composed/complex fields',
	{tag: '@LPD-25905'},
	async ({filtersPage, page}) => {
		const composedFieldName = 'dataSetToDataSetTableSections.description';

		await test.step('Create a selection filter from API Headless source', async () => {
			await filtersPage.createSelectionFilterApiHeadless({
				filterBy: 'externalReferenceCode',
				filterMode: 'Include',
				itemKey: 'id',
				itemLabel: 'label',
				name: SELECTION_API_HEADLESS_FILTER_NAME,
				preselectedValues: [dataSetLabel],
				restApplication: `${API_ENDPOINT_PATH}`,
				restEndpoint: '/',
				restSchema: 'DataSet',
				selectionType: 'Single',
				sourceType: 'API REST Application',
			});

			await filtersPage.newSelectionFilterForm.filterBySelectButton.click();
			await filtersPage.fieldSelectModalPage.searchAndSelectField(
				composedFieldName
			);
			await filtersPage.fieldSelectModalPage.saveAddFieldsModal();
			await filtersPage.saveAddFilterForm();
		});

		await test.step('Check that the selection filter is in the list', async () => {
			await expect(
				page.getByRole('cell', {
					exact: true,
					name: SELECTION_API_HEADLESS_FILTER_NAME,
				})
			).toBeVisible();
		});

		await test.step('Open the edit filter form', async () => {
			const filterActionsButton = page
				.getByRole('cell', {name: 'Actions'})
				.getByRole('button');

			await expect(filterActionsButton).toBeVisible();

			await clickAndExpectToBeVisible({
				autoClick: true,
				target: page.getByRole('menuitem', {name: 'Edit'}),
				trigger: filterActionsButton,
			});

			const dialogFilterSourceSubtitle = page.getByRole('heading', {
				name: 'Filter Source',
			});

			await expect(dialogFilterSourceSubtitle).toBeVisible();

			const dialogFilterOptionsSubtitle = page.getByRole('heading', {
				name: 'Filter Options',
			});

			await expect(dialogFilterOptionsSubtitle).toBeVisible();
		});

		await test.step('Change the filter name', async () => {
			await filtersPage.newSelectionFilterForm.nameInput.clear();

			await filtersPage.saveAddFilterForm();
		});

		await test.step('Confirm that a "This field is required." message appears in the form', async () => {
			await filtersPage.page
				.getByText('This field is required.')
				.isVisible();
		});

		await test.step('Save filter form without errors', async () => {
			await filtersPage.newSelectionFilterForm.nameInput.fill(
				SELECTION_API_HEADLESS_FILTER_NAME
			);

			await filtersPage.saveAddFilterForm();
		});

		await test.step('Check that the selection filter is in the list', async () => {
			await expect(
				page.getByRole('cell', {
					exact: true,
					name: SELECTION_API_HEADLESS_FILTER_NAME,
				})
			).toBeVisible();

			await expect(
				page.getByRole('cell', {
					exact: true,
					name: SELECTION_DISPLAY_TYPE,
				})
			).toBeVisible();
		});
	}
);

test(
	'Can deactivate and activate Selection filters',
	{tag: '@LPD-39965'},
	async ({filtersPage, page}) => {
		await test.step('Create a selection filter from API Headless source', async () => {
			await filtersPage.createSelectionFilterApiHeadless({
				filterBy: 'externalReferenceCode',
				filterMode: 'Include',
				itemKey: 'id',
				itemLabel: 'label',
				name: SELECTION_API_HEADLESS_FILTER_NAME,
				preselectedValues: [dataSetLabel],
				restApplication: `${API_ENDPOINT_PATH}`,
				restEndpoint: '/',
				restSchema: 'DataSet',
				selectionType: 'Single',
				sourceType: 'API REST Application',
			});

			await filtersPage.saveAddFilterForm();

			await waitForAlert(page);
		});

		await test.step('Check that the selection filter is in the list and is "Active" by default', async () => {
			await expect(
				page.getByRole('cell', {
					exact: true,
					name: SELECTION_API_HEADLESS_FILTER_NAME,
				})
			).toBeVisible();

			await expect(filtersPage.activeToggle.first()).toBeVisible();
		});

		await test.step('Deactivate selection filter', async () => {
			await filtersPage.activeToggle.first().click();

			await waitForAlert(page);

			await expect(filtersPage.inactiveToggle.first()).toBeVisible();
		});

		await test.step('Activate selection filter', async () => {
			await filtersPage.inactiveToggle.first().click();

			await waitForAlert(page);

			await expect(filtersPage.activeToggle.first()).toBeVisible();
		});
	}
);

test(
	'Keywords should be able to select',
	{tag: '@LPD-55983'},
	async ({filtersPage, page}) => {
		await test.step('Create a selection filter from API Headless source', async () => {
			await filtersPage.createSelectionFilterApiHeadless({
				filterBy: 'keywords',
				filterMode: 'Include',
				itemKey: 'id',
				itemLabel: 'label',
				name: SELECTION_API_HEADLESS_FILTER_NAME,
				preselectedValues: [dataSetLabel],
				restApplication: `${API_ENDPOINT_PATH}`,
				restEndpoint: '/',
				restSchema: 'DataSet',
				selectionType: 'Single',
				sourceType: 'API REST Application',
			});

			await filtersPage.saveAddFilterForm();

			await waitForAlert(page);
		});

		await test.step('Check that keywords was selected correctly', async () => {
			await expect(
				page.getByRole('cell', {
					exact: true,
					name: 'keywords',
				})
			).toBeVisible();
		});

		await test.step('Check that the selection filter is in the list and is "Active" by default', async () => {
			await expect(
				page.getByRole('cell', {
					exact: true,
					name: SELECTION_API_HEADLESS_FILTER_NAME,
				})
			).toBeVisible();

			await expect(filtersPage.activeToggle.first()).toBeVisible();
		});

	}
);

test(
	'dataSetToDataSetTableSections.description should be able to select',
	{tag: '@LPD-55983'},
	async ({filtersPage, page}) => {

		const composedFieldName = 'dataSetToDataSetTableSections.description';

		await test.step('Create a selection filter from API Headless source', async () => {
			await filtersPage.createSelectionFilterApiHeadless({
				filterBy: 'externalReferenceCode',
				filterMode: 'Include',
				itemKey: 'id',
				itemLabel: 'label',
				name: SELECTION_API_HEADLESS_FILTER_NAME,
				preselectedValues: [dataSetLabel],
				restApplication: `${API_ENDPOINT_PATH}`,
				restEndpoint: '/',
				restSchema: 'DataSet',
				selectionType: 'Single',
				sourceType: 'API REST Application',
			});

			await filtersPage.newSelectionFilterForm.filterBySelectButton.click();
			await filtersPage.fieldSelectModalPage.searchAndSelectField(
				composedFieldName
			);
			await filtersPage.fieldSelectModalPage.saveAddFieldsModal();
		});

	}
);

test(
	'dataSetToDataSetTableSections.friendlyUrlPath should not be able to select',
	{tag: '@LPD-55983'},
	async ({filtersPage, page}) => {

		const composedFieldName = 'dataSetToDataSetTableSections.friendlyUrlPath';

		await test.step('Create a selection filter from API Headless source', async () => {
			await filtersPage.createSelectionFilterApiHeadless({
				filterBy: 'externalReferenceCode',
				filterMode: 'Include',
				itemKey: 'id',
				itemLabel: 'label',
				name: SELECTION_API_HEADLESS_FILTER_NAME,
				preselectedValues: [dataSetLabel],
				restApplication: `${API_ENDPOINT_PATH}`,
				restEndpoint: '/',
				restSchema: 'DataSet',
				selectionType: 'Single',
				sourceType: 'API REST Application',
			});

			await filtersPage.newSelectionFilterForm.filterBySelectButton.click();
			await filtersPage.fieldSelectModalPage.searchAndSelectField(
				composedFieldName
			);
			await filtersPage.fieldSelectModalPage.saveAddFieldsModal();
		});

	}
);