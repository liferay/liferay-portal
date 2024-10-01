/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {featureFlagsTest} from '../../../../fixtures/featureFlagsTest';
import {loginTest} from '../../../../fixtures/loginTest';
import getRandomString from '../../../../utils/getRandomString';
import {waitForAlert} from '../../../../utils/waitForAlert';
import {dataSetManagerApiHelpersTest} from '../../fixtures/dataSetManagerApiHelpersTest';
import saveFromModal from '../../utils/saveFromModal';
import {dataSetsPageTest} from './fixtures/dataSetsPageTest';
import {sortingPageTest} from './fixtures/sortingPageTest';

export const test = mergeTests(
	dataSetManagerApiHelpersTest,
	dataSetsPageTest,
	featureFlagsTest({
		'LPS-164563': true,
		'LPS-178052': true,
	}),
	sortingPageTest,
	loginTest()
);

let dataSetERC: string;
let dataSetLabel: string;

test.beforeEach(async ({dataSetManagerApiHelpers}) => {
	dataSetERC = getRandomString();
	dataSetLabel = getRandomString();

	await dataSetManagerApiHelpers.createDataSet({
		erc: dataSetERC,
		label: dataSetLabel,
	});
});

test.afterEach(async ({dataSetManagerApiHelpers}) => {
	await dataSetManagerApiHelpers.deleteDataSet({erc: dataSetERC});
});

test.describe('Sorting in Data Set Manager', () => {
	test('Save and cancel buttons are not present @LPD-9468', async ({
		page,
		sortingPage,
	}) => {
		await test.step('Navigate to Sorting section', async () => {
			await sortingPage.goto({
				dataSetLabel,
			});
		});

		await test.step('Check that save and cancel buttons are not present', async () => {
			await expect(
				page.getByRole('button', {name: 'Save'})
			).not.toBeVisible();
			await expect(
				page.getByRole('button', {name: 'Cancel'})
			).not.toBeVisible();
		});
	});

	test('Delete confirmation message is displayed @LPD-12725', async ({
		dataSetManagerApiHelpers,
		page,
		sortingPage,
	}) => {
		await test.step('Create sorting options', async () => {
			await dataSetManagerApiHelpers.createDataSetSort({
				dataSetERC,
				defaultValue: true,
				fieldName: 'id',
				label_i18n: {en_US: 'ID'},
				orderType: 'asc',
			});
		});

		await test.step('Navigate to Sorting section', async () => {
			await sortingPage.goto({
				dataSetLabel,
			});
		});

		await test.step('Click on the delete action', async () => {
			const tableRow = sortingPage.sortingTable.locator('tr', {
				has: page.locator('text="ID"'),
			});

			await tableRow
				.getByRole('cell', {name: 'Actions'})
				.getByRole('button')
				.click();

			await page.getByRole('menuitem', {name: 'Delete'}).click();
		});

		await test.step('Check that the delete message is displayed', async () => {
			await expect(page.getByText('Delete Sorting')).toBeVisible();

			await expect(
				page.getByText(
					'Are you sure you want to delete this sorting? It will be removed immediately. Fragments using it will be affected. This action cannot be undone.'
				)
			).toBeVisible();
		});

		await test.step('Click on delete button to check that delete button exists and is clickable', async () => {
			await page.getByRole('button', {name: 'Delete'}).click();
		});

		await test.step('Wait for success message to be displayed', async () => {
			await waitForAlert(page);
		});
	});

	test('Edit action can be cancelled @LPD-12725', async ({
		dataSetManagerApiHelpers,
		page,
		sortingPage,
	}) => {
		await test.step('Create ID sorting', async () => {
			await dataSetManagerApiHelpers.createDataSetSort({
				dataSetERC,
				defaultValue: true,
				fieldName: 'id',
				label_i18n: {en_US: 'ID'},
				orderType: 'asc',
			});
		});

		await test.step('Navigate to Sorting section', async () => {
			await sortingPage.goto({
				dataSetLabel,
			});
		});

		await test.step('Click on the edit action', async () => {
			const tableRow = sortingPage.sortingTable.locator('tr', {
				has: page.locator('text="ID"'),
			});

			await tableRow
				.getByRole('cell', {name: 'Actions'})
				.getByRole('button')
				.click();

			await page.getByRole('menuitem', {name: 'Edit'}).click();
		});

		await test.step('Change "Label" and "Sort By" inputs to "dateCreated"', async () => {
			await page.getByLabel('Label').fill('dateCreated');
			await page.getByLabel('Sort By').selectOption('dateCreated');
		});

		await test.step('Click cancel', async () => {
			await page.getByRole('button', {name: 'Cancel'}).click();
		});

		await test.step('Check that changes are not applied', async () => {
			const tableRow = sortingPage.sortingTable.locator('tr', {
				has: page.locator('text="dateCreated"'),
			});

			expect(tableRow).not.toBeVisible();
		});
	});

	test('Delete action can be cancelled @LPD-12725', async ({
		dataSetManagerApiHelpers,
		page,
		sortingPage,
	}) => {
		await test.step('Create ID sorting', async () => {
			await dataSetManagerApiHelpers.createDataSetSort({
				dataSetERC,
				defaultValue: true,
				fieldName: 'id',
				label_i18n: {en_US: 'ID'},
				orderType: 'asc',
			});
		});

		await test.step('Navigate to Sorting section', async () => {
			await sortingPage.goto({
				dataSetLabel,
			});
		});

		await test.step('Click on the delete action', async () => {
			const tableRow = sortingPage.sortingTable.locator('tr', {
				has: page.locator('text="ID"'),
			});

			await tableRow
				.getByRole('cell', {name: 'Actions'})
				.getByRole('button')
				.click();

			await page.getByRole('menuitem', {name: 'Delete'}).click();
		});

		await test.step('Click cancel', async () => {
			await page.getByRole('button', {name: 'Cancel'}).click();
		});

		await test.step('Check that ID still exists', async () => {
			const tableRow = sortingPage.sortingTable.locator('tr', {
				has: page.locator('text="ID"'),
			});

			expect(tableRow).toBeVisible();
		});
	});

	test('Sorting options can be reordered and changes are persisted @LPD-9468', async ({
		dataSetManagerApiHelpers,
		sortingPage,
	}) => {
		await test.step('Create sorting options', async () => {
			await dataSetManagerApiHelpers.createDataSetSort({
				dataSetERC,
				defaultValue: true,
				fieldName: 'id',
				label_i18n: {en_US: 'ID'},
				orderType: 'asc',
			});

			await dataSetManagerApiHelpers.createDataSetSort({
				dataSetERC,
				defaultValue: false,
				fieldName: 'name',
				label_i18n: {en_US: 'Name'},
			});

			await dataSetManagerApiHelpers.createDataSetSort({
				dataSetERC,
				defaultValue: false,
				fieldName: 'dateCreated',
				label_i18n: {en_US: 'Date Created'},
			});
		});

		await test.step('Navigate to Sorting section', async () => {
			await sortingPage.goto({
				dataSetLabel,
			});
		});

		await test.step('Check that "Date Created" is below "Name"', async () => {
			const tableLabelCellTexts =
				await sortingPage.getTableColumnInnerTexts(2);

			expect(tableLabelCellTexts).toEqual(['ID', 'Name', 'Date Created']);
		});

		await test.step('Move the "Date Created" option above "Name"', async () => {
			const dateCreatedRow = sortingPage.sortingTable.getByRole('row', {
				name: 'Date Created',
			});

			const nameRow = sortingPage.sortingTable.getByRole('row', {
				name: 'Name',
			});

			await dateCreatedRow.dragTo(nameRow);
		});

		await test.step('Check that "Date Created" is above "Name"', async () => {
			const tableLabelCellTexts =
				await sortingPage.getTableColumnInnerTexts(2);

			expect(tableLabelCellTexts).toEqual(['ID', 'Date Created', 'Name']);
		});

		await test.step('Navigate to the "Details" tab and back to "Sorting" tab', async () => {
			await sortingPage.selectTab('Details');
			await sortingPage.selectTab('Sorting');
		});

		await test.step('Check that the order is still the same', async () => {
			const tableLabelCellTexts =
				await sortingPage.getTableColumnInnerTexts(2);

			expect(tableLabelCellTexts).toEqual(['ID', 'Date Created', 'Name']);
		});
	});

	test('The search bar filters the results @LPD-9468', async ({
		dataSetManagerApiHelpers,
		page,
		sortingPage,
	}) => {
		await test.step('Create sorting options', async () => {
			await dataSetManagerApiHelpers.createDataSetSort({
				dataSetERC,
				defaultValue: true,
				fieldName: 'id',
				label_i18n: {en_US: 'ID'},
				orderType: 'asc',
			});

			await dataSetManagerApiHelpers.createDataSetSort({
				dataSetERC,
				defaultValue: false,
				fieldName: 'name',
				label_i18n: {en_US: 'Name'},
			});
		});

		await test.step('Navigate to Sorting section', async () => {
			await sortingPage.goto({
				dataSetLabel,
			});
		});

		await test.step('Enter in a search term that does not exist', async () => {
			await page.getByPlaceholder('Search').fill('nothing');
		});

		await test.step('Check that "No Results Found" is displayed', async () => {
			await expect(page.getByText('No Results Found')).toBeVisible();
		});

		await test.step('Enter in a search term to only show ID', async () => {
			await page.getByPlaceholder('Search').fill('ID');
		});

		await test.step('Check that only "ID" appears in the table', async () => {
			const tableLabelCellTexts =
				await sortingPage.getTableColumnInnerTexts(2);

			expect(tableLabelCellTexts).toEqual(['ID']);
		});
	});

	test('In the New Sort modal, the Label and Sort By fields are required', async ({
		page,
		sortingPage,
	}) => {
		await test.step('Navigate to Sorting section', async () => {
			await sortingPage.goto({
				dataSetLabel,
			});
		});

		await test.step('Open new sort modal', async () => {
			await sortingPage.openAddSortingModal();
		});

		await test.step('Check that save button is disabled when "Sort By" is not selected', async () => {
			await page.getByLabel('Label').fill('ID');
			await page.getByLabel('Sort By').selectOption('');

			await expect(
				page.locator('.liferay-modal').getByRole('button', {
					exact: true,
					name: 'Save',
				})
			).toBeDisabled();
		});

		await test.step('Check that save button is disabled when "Label" is empty', async () => {
			await page.getByLabel('Label').fill('');
			await page.getByLabel('Sort By').selectOption('id');

			await expect(
				page.locator('.liferay-modal').getByRole('button', {
					exact: true,
					name: 'Save',
				})
			).toBeDisabled();
		});
	});

	test('In the New Sort modal, the Order Type input only appears when default is checked @LPD-19465', async ({
		page,
		sortingPage,
	}) => {
		await test.step('Navigate to Sorting section', async () => {
			await sortingPage.goto({
				dataSetLabel,
			});
		});

		await test.step('Open new sort modal', async () => {
			await sortingPage.openAddSortingModal();
		});

		await test.step('Order Type input only appears when default is checked', async () => {
			await expect(page.getByLabel('Order Type')).not.toBeVisible();

			await page.getByLabel('Use as Default Sorting').check();

			await expect(page.getByLabel('Order Type')).toBeVisible();
		});

		await test.step('Check the options of the Order Type input are "Ascending" and "Descending"', async () => {
			const orderTypeInput = await page
				.getByLabel('Order Type')
				.textContent();

			expect(orderTypeInput).toEqual('AscendingDescending');
		});
	});

	test('Sorting can be created, edited, and deleted @LPD-19465', async ({
		page,
		sortingPage,
	}) => {
		await test.step('Navigate to Sorting section', async () => {
			await sortingPage.goto({
				dataSetLabel,
			});
		});

		await test.step('Open new sort modal', async () => {
			await sortingPage.openAddSortingModal();
		});

		await test.step('Input values', async () => {
			await page.getByLabel('Label').fill('Date Modified');
			await page.getByLabel('Sort By').selectOption('dateModified');
			await page.getByLabel('Use as Default Sorting').check();
		});

		await test.step('Save changes', async () => {
			await saveFromModal({
				page,
			});
		});

		await test.step('New sort is displayed on the table', async () => {
			await expect(page.getByText('Date Modified').first()).toBeVisible();
			await expect(page.getByText('dateModified').first()).toBeVisible();
			await expect(page.getByText('Yes').first()).toBeVisible();
		});

		await test.step('Open edit sort modal', async () => {
			const tableRow = sortingPage.sortingTable.locator('tr', {
				has: page.locator('text="Date Modified"'),
			});

			await tableRow
				.getByRole('cell', {name: 'Actions'})
				.getByRole('button')
				.click();

			await page.getByRole('menuitem', {name: 'Edit'}).click();
		});

		await test.step('Change label and sort by values', async () => {
			await page.getByLabel('Label').fill('Date Created');
			await page.getByLabel('Sort By').selectOption('dateCreated');
			await page.getByLabel('Use as Default Sorting').setChecked(false);
		});

		await test.step('Save changes', async () => {
			await saveFromModal({
				page,
			});
		});

		await test.step('Edited sort is updated on the table', async () => {
			await expect(page.getByText('Date Created').first()).toBeVisible();
			await expect(page.getByText('dateCreated').first()).toBeVisible();
			await expect(page.getByText('No').first()).toBeVisible();
		});

		await test.step('Delete sort', async () => {
			const tableRow = sortingPage.sortingTable.locator('tr', {
				has: page.locator('text="Date Created"'),
			});

			await tableRow
				.getByRole('cell', {name: 'Actions'})
				.getByRole('button')
				.click();

			await page.getByRole('menuitem', {name: 'Delete'}).click();

			await page.getByRole('button', {name: 'Delete'}).click();

			await expect(
				page.getByText('Date Created').first()
			).not.toBeVisible();
		});
	});

	test('Unmark default sorting when a new one is marked and saved @LPD-25392', async ({
		page,
		sortingPage,
	}) => {
		await test.step('Navigate to Sorting section', async () => {
			await sortingPage.goto({
				dataSetLabel,
			});
		});

		await test.step('Create new sorting Date Modified as default', async () => {
			await sortingPage.openAddSortingModal();

			await page.getByLabel('Label').fill('Date Modified');
			await page.getByLabel('Sort By').selectOption('dateModified');
			await page.getByLabel('Use as Default Sorting').check();

			await saveFromModal({page});
		});

		await test.step('Create new sorting Date Created as default', async () => {
			await sortingPage.openAddSortingModal();

			await page.getByLabel('Label').fill('Date Created');
			await page.getByLabel('Sort By').selectOption('dateCreated');
			await page.getByLabel('Use as Default Sorting').check();

			await saveFromModal({page});
		});

		await test.step('Check that Date Created is marked as default and Date Modified is not', async () => {
			await expect(
				page
					.getByRole('row', {name: 'Date Created'})
					.getByRole('cell', {name: 'Yes'})
			).toBeVisible();

			await expect(
				page
					.getByRole('row', {name: 'Date Modified'})
					.getByRole('cell', {name: 'No'})
			).toBeVisible();
		});
	});

	test('Sorting label in the table is not blank after changing default site language @LPD-25464', async ({
		page,
		sortingPage,
	}) => {
		let spanishLanguage = false;

		try {
			await test.step('Navigate to Instance Settings Localization', async () => {
				await page
					.getByLabel('Open Applications MenuCtrl+Alt+A')
					.click();

				await page.getByRole('tab', {name: 'Control Panel'}).click();

				await page
					.getByRole('menuitem', {name: 'Instance Settings'})
					.click();

				await page.getByRole('link', {name: 'Localization'}).click();
			});

			await test.step('Change default site language to Spanish', async () => {
				await page.getByLabel('Default Language').selectOption('es_ES');

				await page.getByRole('button', {name: 'Save'}).click();

				await waitForAlert(page);

				spanishLanguage = true;

				// Reload page for language changes to take affect.
				// Otherwise the Label language selector will still default to
				// en_US.

				await page.reload();
			});

			await test.step('Navigate to Sorting section', async () => {
				await sortingPage.goto({
					dataSetLabel,
				});
			});

			await test.step('Open new sort modal', async () => {
				await sortingPage.openAddSortingModal();
			});

			await test.step('Input values', async () => {
				await page.getByLabel('Label').fill('Nombre');
				await page.getByLabel('Sort By').selectOption('name');
			});

			await test.step('Save changes', async () => {
				await saveFromModal({
					page,
				});
			});

			await test.step('Label in the table is not blank', async () => {
				await expect(page.getByText('Nombre').first()).toBeVisible();
			});
		}
		finally {
			if (spanishLanguage) {
				await test.step('Navigate to Instance Settings Localization', async () => {
					await page
						.getByLabel('Open Applications MenuCtrl+Alt+A')
						.click();

					await page
						.getByRole('tab', {name: 'Control Panel'})
						.click();

					await page
						.getByRole('menuitem', {name: 'Instance Settings'})
						.click();

					await page
						.getByRole('link', {name: 'Localization'})
						.click();
				});

				await test.step('Change default site language back to English', async () => {
					await page
						.getByLabel('Default Language')
						.selectOption('en_US');

					await page.getByRole('button', {name: 'Save'}).click();

					await waitForAlert(page);
				});
			}
		}
	});
});

export const applicationPageTest = mergeTests(
	dataSetManagerApiHelpersTest,
	dataSetsPageTest,
	featureFlagsTest({
		'LPS-164563': true,
		'LPS-178052': true,
	}),
	loginTest()
);

applicationPageTest.describe(
	'Sorting Dropdown in Data Set Application Page',
	() => {
		applicationPageTest(
			'When sorting configuration has no labels defined, the order dropdown is not displayed @LPD-19503',
			async ({dataSetsPage, page}) => {
				await dataSetsPage.goto();

				await expect(
					page.getByRole('button', {name: 'Order'})
				).not.toBeVisible();
			}
		);
	}
);
