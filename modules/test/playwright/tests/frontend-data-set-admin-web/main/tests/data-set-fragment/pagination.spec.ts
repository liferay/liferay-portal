/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {featureFlagsTest} from '../../../../../fixtures/featureFlagsTest';
import {isolatedLayoutTest} from '../../../../../fixtures/isolatedLayoutTest';
import {loginTest} from '../../../../../fixtures/loginTest';
import getRandomString from '../../../../../utils/getRandomString';
import {dataSetManagerApiHelpersTest} from '../../fixtures/dataSetManagerApiHelpersTest';
import {dataSetFragmentPageTest} from './fixtures/dataSetFragmentPageTest';

export const test = mergeTests(
	dataSetManagerApiHelpersTest,
	featureFlagsTest({
		'LPS-178052': {enabled: true},
	}),
	isolatedLayoutTest({publish: false}),
	loginTest(),
	dataSetFragmentPageTest
);

let dataSetERC: string;
let dataSetLabel: string;

test.beforeEach(async ({dataSetManagerApiHelpers}) => {
	dataSetERC = getRandomString();
	dataSetLabel = getRandomString();

	await test.step('Create data set', async () => {
		await dataSetManagerApiHelpers.createDataSet({
			erc: dataSetERC,
			label: dataSetLabel,
		});
	});

	await test.step('Create table fields', async () => {
		await dataSetManagerApiHelpers.createDataSetTableSection({
			dataSetERC,
			fieldName: 'id',
			label_i18n: {en_US: 'Label'},
			type: 'string',
		});
		await dataSetManagerApiHelpers.createDataSetTableSection({
			dataSetERC,
			fieldName: 'label',
			label_i18n: {en_US: 'Id'},
			type: 'string',
		});
	});
});

test.afterEach(async ({dataSetManagerApiHelpers}) => {
	await dataSetManagerApiHelpers.deleteDataSet({erc: dataSetERC});
});

test.describe('Data Set Pagination configuration in the fragment', () => {
	const assertPaginationValues = async (
		dataSetFragmentPage,
		itemsPerPage,
		deltas
	) => {
		const paginatorWrapper =
			await dataSetFragmentPage.paginationWrapper.locator(
				'.pagination-bar'
			);

		await paginatorWrapper.scrollIntoViewIfNeeded();

		await expect(paginatorWrapper).toBeInViewport();

		const itemsPerPageButton =
			paginatorWrapper.getByLabel('Items Per Page');

		await expect(itemsPerPageButton).toContainText(itemsPerPage);

		await itemsPerPageButton.click();

		// NOTE: strange behaviour. aria-controls is added after clicking the itemsPerPageButton

		const dropdownId = await itemsPerPageButton.evaluate((node) =>
			node.getAttribute('aria-controls')
		);

		await dataSetFragmentPage.page.locator(`#${dropdownId}`).waitFor();

		await expect(
			dataSetFragmentPage.page
				.locator(`#${dropdownId}`)
				.getByRole('option')
		).toHaveCount(deltas.length);

		const paginationOptions = await dataSetFragmentPage.page
			.locator(`#${dropdownId}`)
			.getByRole('option')
			.allInnerTexts();

		expect(paginationOptions).toEqual(deltas);
	};

	const configureDataSet = async (dataSetFragmentPage, layout) => {
		await test.step('Configure Data Set in the page', async () => {
			await dataSetFragmentPage.configureDataSetFragment({
				dataSetLabel,
				layout,
			});
		});

		await test.step('Frontend Data Set Table is in the page', async () => {
			expect(
				await dataSetFragmentPage.table.headRow
					.locator('th')
					.allInnerTexts()
			).toEqual(['Label', 'Id', 'Manage Columns Visibility']);
		});
	};

	test('FDS uses default pagination configuration after creating a Data Set', async ({
		dataSetFragmentPage,
		layout,
	}) => {
		await configureDataSet(dataSetFragmentPage, layout);

		await test.step('Check that the FDS Table pagination uses default configuration values', async () => {
			await assertPaginationValues(dataSetFragmentPage, '20 Items', [
				'4 Items',
				'8 Items',
				'20 Items',
				'40 Items',
				'60 Items',
			]);
		});
	});

	test('FDS uses custom pagination configuration after creating a Data Set', async ({
		dataSetFragmentPage,
		dataSetManagerApiHelpers,
		layout,
	}) => {
		await test.step('Update Data Set pagination configuration', async () => {
			await dataSetManagerApiHelpers.updateDataSet({
				defaultItemsPerPage: 10,
				erc: dataSetERC,
				label: dataSetLabel,
				listOfItemsPerPage: '5, 10, 15',
			});
		});

		await configureDataSet(dataSetFragmentPage, layout);

		await test.step('Check that the FDS Table pagination uses custom configuration values', async () => {
			await assertPaginationValues(dataSetFragmentPage, '10 Items', [
				'5 Items',
				'10 Items',
				'15 Items',
			]);
		});
	});
});
