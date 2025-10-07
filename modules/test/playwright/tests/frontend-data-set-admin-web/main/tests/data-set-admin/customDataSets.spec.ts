/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {featureFlagsTest} from '../../../../../fixtures/featureFlagsTest';
import {loginTest} from '../../../../../fixtures/loginTest';
import getRandomString from '../../../../../utils/getRandomString';
import {dataSetManagerApiHelpersTest} from '../../fixtures/dataSetManagerApiHelpersTest';
import {customDataSetsPageTest} from './fixtures/customDataSetsPageTest';
import {CustomDataSetsPage} from './pages/CustomDataSetsPage';

export const test = mergeTests(
	dataSetManagerApiHelpersTest,
	customDataSetsPageTest,
	featureFlagsTest({
		'LPS-164563': {enabled: true},
	}),
	loginTest()
);

const dataSetERCs = [];

const blogPostsDataSetConfig = {
	name: 'BlogPosting',
	restApplication: '/headless-delivery/v1.0',
	restEndpoint: '/v1.0/sites/{siteId}/blog-postings',
	restSchema: 'BlogPosting',
};

const catalogsDataSetConfig = {
	name: 'Catalog',
	restApplication: '/headless-commerce-admin-catalog/v1.0',
	restEndpoint: '/v1.0/catalog',
	restSchema: 'Catalog',
};

const productsDataSetConfig = {
	name: 'Product',
	restApplication: '/headless-commerce-admin-catalog/v1.0',
	restEndpoint: '/v1.0/products',
	restSchema: 'Product',
};

const skusDataSetConfig = {
	name: 'Sku',
	restApplication: '/headless-commerce-admin-catalog/v1.0',
	restEndpoint: '/v1.0/skus',
	restSchema: 'Sku',
};

const tableSectionsDataSetConfig = {
	name: getRandomString(),
	restApplication: '/data-set-admin/table-sections',
	restEndpoint: '/',
	restSchema: 'DataSetTableSection',
};

const tableSectionsWithSpecialCharactersDataSetConfig = {
	name: 'Data Set ~!@#$%^&*(){}[].<>/? name',
	restApplication: '/data-set-admin/table-sections',
	restEndpoint: '/',
	restSchema: 'DataSetTableSection',
};

async function assertTableActionLabels(customDataSetsPage: CustomDataSetsPage) {
	await customDataSetsPage.table.bodyRows
		.locator('td.cell-item-actions')
		.first()
		.locator('.dropdown-toggle')
		.click();

	const tableItemActions = await customDataSetsPage.page
		.locator('.dropdown-menu')
		.filter({has: customDataSetsPage.page.locator('span.pr-2')})
		.first()
		.locator('.dropdown-item')
		.allInnerTexts();

	const expectedLabels = ['Edit', 'Permissions', 'Delete'];

	expect(tableItemActions).toEqual(expectedLabels);
}

async function assertTableCellContent({
	customDataSetsPage,
	dataSetConfig,
	rowIndex = 0,
}: {
	customDataSetsPage: CustomDataSetsPage;
	dataSetConfig: any;
	rowIndex?: number;
}) {
	await customDataSetsPage.table.bodyRows.first().waitFor();

	const tableRowContent = customDataSetsPage.table.bodyRows
		.nth(rowIndex)
		.locator('td');

	const expectedRowContent = [
		dataSetConfig.name,
		dataSetConfig.restApplication,
		dataSetConfig.restSchema,
		dataSetConfig.restEndpoint,
	];

	await expect(tableRowContent).toContainText(expectedRowContent);
}

async function assertTableColumnLabels(customDataSetsPage: CustomDataSetsPage) {
	const tableColumnLabels = await customDataSetsPage.table.headRow
		.locator('th')
		.allInnerTexts();

	const expectedLabels = [
		'Name',
		'REST Application',
		'REST Schema',
		'REST Endpoint',
		'Modified Date',
		'Item Actions',
	];

	expect(tableColumnLabels).toEqual(expectedLabels);
}

async function assertTableRowsCount(
	customDataSetsPage: CustomDataSetsPage,
	rowsCount: number
) {
	expect(customDataSetsPage.table.bodyRows).toHaveCount(rowsCount);
}

test.afterEach(async ({dataSetManagerApiHelpers}) => {
	for (const erc of dataSetERCs) {
		await dataSetManagerApiHelpers.deleteDataSet({
			erc,
		});
	}

	dataSetERCs.length = 0;
});

test(
	'Create data set via UI',
	{tag: '@LPS-178858'},
	async ({customDataSetsPage}) => {
		await test.step('Navigate to Data Set page', async () => {
			await customDataSetsPage.goto();
			await expect(
				customDataSetsPage.dataSetsEmptyState.locator(
					'.c-empty-state-title'
				)
			).toContainText('No Data Sets Created');
		});

		await test.step('Create Data Set', async () => {
			await customDataSetsPage.createDataSet(tableSectionsDataSetConfig);
		});

		await assertTableColumnLabels(customDataSetsPage);

		await assertTableCellContent({
			customDataSetsPage,
			dataSetConfig: tableSectionsDataSetConfig,
		});

		await assertTableActionLabels(customDataSetsPage);

		await test.step('Delete Data Set', async () => {
			await customDataSetsPage.deleteDataSet(
				tableSectionsDataSetConfig.name
			);
		});
	}
);

test('Create parameterized data set', async ({customDataSetsPage}) => {
	await test.step('Create Data Set', async () => {
		await customDataSetsPage.goto();
		await customDataSetsPage.createDataSet(blogPostsDataSetConfig);
	});

	await assertTableColumnLabels(customDataSetsPage);

	await assertTableCellContent({
		customDataSetsPage,
		dataSetConfig: blogPostsDataSetConfig,
	});

	await assertTableActionLabels(customDataSetsPage);

	await test.step('Delete Data Set', async () => {
		await customDataSetsPage.deleteDataSet(blogPostsDataSetConfig.name);
	});
});

test(
	'Assert endpoint with resolved paramater is available as an option',
	{tag: '@LPD-31177'},
	async ({customDataSetsPage}) => {
		const cartDataSetConfig = {
			name: 'Carts',
			restApplication: '/headless-commerce-delivery-cart/v1.0',
			restEndpoint:
				'/v1.0/channels/{channelId}/account/{accountId}/carts',
			restSchema: 'Cart',
		};

		const modal = customDataSetsPage.newDataSetModal;

		await test.step('Go to Data Sets page and open "New" modal', async () => {
			await customDataSetsPage.goto();

			await customDataSetsPage.newDataSetButton.click();

			await expect(modal.nameInput).toBeVisible();
		});

		await test.step('Assert endpoint with resolved paramater is available', async () => {
			await modal.restApplicationField.click();

			await modal.restApplicationOptions
				.getByRole('option', {name: cartDataSetConfig.restApplication})
				.click();

			await expect(modal.restSchemaField).toBeVisible();

			await modal.restSchemaField.click();

			await modal.restSchemaOptions
				.getByRole('option', {
					exact: true,
					name: cartDataSetConfig.restSchema,
				})
				.click();

			await expect(modal.restEndpointField).toBeVisible();

			await modal.restEndpointField.click();

			await expect(
				modal.restEndpointOptions.getByRole('option', {
					name: cartDataSetConfig.restEndpoint,
				})
			).toBeVisible();
		});
	}
);

test('Can paginate created Data Sets', async ({
	customDataSetsPage,
	dataSetManagerApiHelpers,
	page,
}) => {
	const testDataSetERCs = Array.from(Array(5).keys()).map(() =>
		getRandomString()
	);

	await test.step('Create collection of Data Sets', async () => {
		for (const DATA_SET_ERC of testDataSetERCs) {
			dataSetERCs.push(DATA_SET_ERC);
			await dataSetManagerApiHelpers.createDataSet({
				...tableSectionsDataSetConfig,
				erc: DATA_SET_ERC,
				label: tableSectionsDataSetConfig.name,
			});
		}
	});

	await test.step('Navigate to Data Sets page', async () => {
		await customDataSetsPage.goto();
	});

	await assertTableRowsCount(customDataSetsPage, 5);

	await test.step('Change page size', async () => {
		const itemsPerPageButton = page.getByLabel('Items Per Page');

		await expect(itemsPerPageButton).toContainText('8 Items');

		await itemsPerPageButton.click();

		const dropdownId =
			await itemsPerPageButton.getAttribute('aria-controls');
		const dropdown = page.locator(`#${dropdownId}`);

		await dropdown.waitFor();

		await dropdown.getByRole('option', {name: '4 Items'}).click();

		await expect(itemsPerPageButton).toContainText('4 Items');
	});

	await assertTableRowsCount(customDataSetsPage, 4);

	await test.step('Navigate to Data Set page 2', async () => {
		await page.getByLabel('Go to page, 2').click();

		await page.getByText('Showing 5 to 5 of 5 entries.').isVisible();
	});

	await assertTableRowsCount(customDataSetsPage, 1);

	await test.step('Delete Data Set from current page', async () => {
		const dataSetActionsButton = page.getByRole('button', {
			name: 'Actions',
		});

		dataSetActionsButton.click();

		const actionsDropdownId =
			await dataSetActionsButton.getAttribute('aria-controls');
		const actionsDropdown = page.locator(`#${actionsDropdownId}`);

		await actionsDropdown.waitFor();

		await actionsDropdown.getByRole('menuitem', {name: 'Delete'}).click();

		await page.getByRole('dialog').waitFor({state: 'visible'});

		await page.getByRole('button', {name: 'Delete'}).click();

		await page.getByRole('dialog').waitFor({state: 'hidden'});
	});

	await assertTableRowsCount(customDataSetsPage, 4);
});

test('Sort data sets by different columns', async ({
	customDataSetsPage,
	dataSetManagerApiHelpers,
	page,
}) => {
	const productsDataSetERC = getRandomString();

	await test.step('Create collection of Data Sets', async () => {
		const blogPostDataSetERC = getRandomString();
		dataSetERCs.push(blogPostDataSetERC);

		await dataSetManagerApiHelpers.createDataSet({
			...blogPostsDataSetConfig,
			erc: blogPostDataSetERC,
			label: blogPostsDataSetConfig.name,
		});

		const catalogsDataSetERC = getRandomString();
		dataSetERCs.push(catalogsDataSetERC);

		await dataSetManagerApiHelpers.createDataSet({
			...catalogsDataSetConfig,
			erc: catalogsDataSetERC,
			label: catalogsDataSetConfig.name,
		});

		dataSetERCs.push(productsDataSetERC);

		await dataSetManagerApiHelpers.createDataSet({
			...productsDataSetConfig,
			erc: productsDataSetERC,
			label: productsDataSetConfig.name,
		});

		const skuDataSetERC = getRandomString();
		dataSetERCs.push(skuDataSetERC);

		await dataSetManagerApiHelpers.createDataSet({
			...skusDataSetConfig,
			erc: skuDataSetERC,
			label: skusDataSetConfig.name,
		});
	});

	await test.step('Go to Data Sets', async () => {
		await customDataSetsPage.goto();
	});

	await assertTableRowsCount(customDataSetsPage, 4);

	await test.step('Check data sets default sort is by creation date, in descending order', async () => {
		await assertTableCellContent({
			customDataSetsPage,
			dataSetConfig: skusDataSetConfig,
			rowIndex: 0,
		});
		await assertTableCellContent({
			customDataSetsPage,
			dataSetConfig: productsDataSetConfig,
			rowIndex: 1,
		});
		await assertTableCellContent({
			customDataSetsPage,
			dataSetConfig: catalogsDataSetConfig,
			rowIndex: 2,
		});
		await assertTableCellContent({
			customDataSetsPage,
			dataSetConfig: blogPostsDataSetConfig,
			rowIndex: 3,
		});
	});

	await test.step('Sort data sets by "Name" column', async () => {
		await customDataSetsPage.sortBy('Name');

		await assertTableCellContent({
			customDataSetsPage,
			dataSetConfig: blogPostsDataSetConfig,
			rowIndex: 0,
		});
		await assertTableCellContent({
			customDataSetsPage,
			dataSetConfig: catalogsDataSetConfig,
			rowIndex: 1,
		});
		await assertTableCellContent({
			customDataSetsPage,
			dataSetConfig: productsDataSetConfig,
			rowIndex: 2,
		});
		await assertTableCellContent({
			customDataSetsPage,
			dataSetConfig: skusDataSetConfig,
			rowIndex: 3,
		});

		await customDataSetsPage.sortBy('Name');

		await assertTableCellContent({
			customDataSetsPage,
			dataSetConfig: skusDataSetConfig,
			rowIndex: 0,
		});
		await assertTableCellContent({
			customDataSetsPage,
			dataSetConfig: productsDataSetConfig,
			rowIndex: 1,
		});
		await assertTableCellContent({
			customDataSetsPage,
			dataSetConfig: catalogsDataSetConfig,
			rowIndex: 2,
		});
		await assertTableCellContent({
			customDataSetsPage,
			dataSetConfig: blogPostsDataSetConfig,
			rowIndex: 3,
		});
	});

	await test.step('Sort data sets by "REST Endpoint" column', async () => {

		// Reload to start with default sort

		await page.reload();

		await customDataSetsPage.sortBy('REST Endpoint');

		await assertTableCellContent({
			customDataSetsPage,
			dataSetConfig: catalogsDataSetConfig,
			rowIndex: 0,
		});
		await assertTableCellContent({
			customDataSetsPage,
			dataSetConfig: productsDataSetConfig,
			rowIndex: 1,
		});
		await assertTableCellContent({
			customDataSetsPage,
			dataSetConfig: blogPostsDataSetConfig,
			rowIndex: 2,
		});
		await assertTableCellContent({
			customDataSetsPage,
			dataSetConfig: skusDataSetConfig,
			rowIndex: 3,
		});

		await customDataSetsPage.sortBy('REST Endpoint');

		await assertTableCellContent({
			customDataSetsPage,
			dataSetConfig: skusDataSetConfig,
			rowIndex: 0,
		});
		await assertTableCellContent({
			customDataSetsPage,
			dataSetConfig: blogPostsDataSetConfig,
			rowIndex: 1,
		});
		await assertTableCellContent({
			customDataSetsPage,
			dataSetConfig: productsDataSetConfig,
			rowIndex: 2,
		});
		await assertTableCellContent({
			customDataSetsPage,
			dataSetConfig: catalogsDataSetConfig,
			rowIndex: 3,
		});
	});

	await test.step('Sort data sets by "REST Schema" column', async () => {

		// Reload to start with default sort

		await page.reload();

		await customDataSetsPage.sortBy('REST Schema');

		await assertTableCellContent({
			customDataSetsPage,
			dataSetConfig: blogPostsDataSetConfig,
			rowIndex: 0,
		});
		await assertTableCellContent({
			customDataSetsPage,
			dataSetConfig: catalogsDataSetConfig,
			rowIndex: 1,
		});
		await assertTableCellContent({
			customDataSetsPage,
			dataSetConfig: productsDataSetConfig,
			rowIndex: 2,
		});
		await assertTableCellContent({
			customDataSetsPage,
			dataSetConfig: skusDataSetConfig,
			rowIndex: 3,
		});

		await customDataSetsPage.sortBy('REST Schema');

		await assertTableCellContent({
			customDataSetsPage,
			dataSetConfig: skusDataSetConfig,
			rowIndex: 0,
		});
		await assertTableCellContent({
			customDataSetsPage,
			dataSetConfig: productsDataSetConfig,
			rowIndex: 1,
		});
		await assertTableCellContent({
			customDataSetsPage,
			dataSetConfig: catalogsDataSetConfig,
			rowIndex: 2,
		});
		await assertTableCellContent({
			customDataSetsPage,
			dataSetConfig: blogPostsDataSetConfig,
			rowIndex: 3,
		});
	});

	await test.step('Sort data sets by "Modified Date" column', async () => {

		// Reload to start with default sort

		await page.reload();

		await customDataSetsPage.sortBy('Modified Date');

		await assertTableCellContent({
			customDataSetsPage,
			dataSetConfig: blogPostsDataSetConfig,
			rowIndex: 0,
		});
		await assertTableCellContent({
			customDataSetsPage,
			dataSetConfig: catalogsDataSetConfig,
			rowIndex: 1,
		});
		await assertTableCellContent({
			customDataSetsPage,
			dataSetConfig: productsDataSetConfig,
			rowIndex: 2,
		});
		await assertTableCellContent({
			customDataSetsPage,
			dataSetConfig: skusDataSetConfig,
			rowIndex: 3,
		});

		await dataSetManagerApiHelpers.updateDataSet({
			defaultItemsPerPage: 8,
			erc: productsDataSetERC,
		});

		await page.reload();

		await customDataSetsPage.sortBy('Modified Date');

		await assertTableCellContent({
			customDataSetsPage,
			dataSetConfig: blogPostsDataSetConfig,
			rowIndex: 0,
		});
		await assertTableCellContent({
			customDataSetsPage,
			dataSetConfig: catalogsDataSetConfig,
			rowIndex: 1,
		});
		await assertTableCellContent({
			customDataSetsPage,
			dataSetConfig: skusDataSetConfig,
			rowIndex: 2,
		});
		await assertTableCellContent({
			customDataSetsPage,
			dataSetConfig: productsDataSetConfig,
			rowIndex: 3,
		});
	});
});

test(
	'Check cancel in Data Set',
	{tag: ['@LPS-175990', '@LPS-172398']},
	async ({customDataSetsPage, page}) => {
		await test.step('Navigate to Data Set page', async () => {
			await customDataSetsPage.goto();
		});

		await test.step('Cannot create a Data Set without a name', async () => {
			await customDataSetsPage.newDataSetButton.click();
			await customDataSetsPage.newDataSetModal.nameInput.waitFor();

			await customDataSetsPage.newDataSetModal.nameInput.fill('');
			await customDataSetsPage.newDataSetModal.saveButton.click();

			await expect(
				page.getByText('This field is required.', {exact: true})
			).toBeVisible();

			await customDataSetsPage.newDataSetModal.cancel.click();

			await expect(
				customDataSetsPage.dataSetsEmptyState.locator(
					'.c-empty-state-title'
				)
			).toContainText('No Data Sets Created');
		});

		await test.step('Can create a Data Set using special characters', async () => {
			await customDataSetsPage.createDataSet(
				tableSectionsWithSpecialCharactersDataSetConfig
			);
		});

		await assertTableCellContent({
			customDataSetsPage,
			dataSetConfig: tableSectionsWithSpecialCharactersDataSetConfig,
		});

		await test.step('Select the Delete Data Set action, then click Cancel button', async () => {
			const datasetTestRow = customDataSetsPage.table.bodyRows.filter({
				hasText: tableSectionsWithSpecialCharactersDataSetConfig.name,
			});

			await datasetTestRow
				.first()
				.getByRole('button', {name: 'Actions'})
				.click();

			await page.getByRole('menuitem', {name: 'Delete'}).click();

			const deleteModal = page.getByRole('dialog');

			await deleteModal.getByRole('button', {name: 'Cancel'}).click();

			await assertTableCellContent({
				customDataSetsPage,
				dataSetConfig: tableSectionsWithSpecialCharactersDataSetConfig,
			});
		});

		await test.step('Select the Delete Data Set action, then click X button', async () => {
			const datasetTestRow = customDataSetsPage.table.bodyRows.filter({
				hasText: tableSectionsWithSpecialCharactersDataSetConfig.name,
			});

			await datasetTestRow
				.first()
				.getByRole('button', {name: 'Actions'})
				.click();

			await page.getByRole('menuitem', {name: 'Delete'}).click();

			const deleteModal = page.getByRole('dialog');

			await deleteModal.getByRole('button', {name: 'Close'}).click();

			await assertTableCellContent({
				customDataSetsPage,
				dataSetConfig: tableSectionsWithSpecialCharactersDataSetConfig,
			});
		});

		await test.step('Delete Data Set', async () => {
			await customDataSetsPage.deleteDataSet(
				tableSectionsWithSpecialCharactersDataSetConfig.name
			);
		});
	}
);
