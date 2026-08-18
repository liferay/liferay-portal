/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {apiHelpersTest} from '../../../../../fixtures/apiHelpersTest';
import {dataSetManagerApiHelpersTest} from '../../../../../fixtures/dataSetManagerApiHelpersTest';
import {featureFlagsTest} from '../../../../../fixtures/featureFlagsTest';
import {isolatedSiteTest} from '../../../../../fixtures/isolatedSiteTest';
import {loginTest} from '../../../../../fixtures/loginTest';
import getDataSetResourceURL from '../../../../../utils/getDataSetResourceURL';
import getRandomString from '../../../../../utils/getRandomString';
import {waitForFDS} from '../../../../../utils/waitFor';
import {fdsSamplePageTest} from '../../fixtures/fdsSamplePageTest';

const test = mergeTests(
	apiHelpersTest,
	dataSetManagerApiHelpersTest,
	fdsSamplePageTest,
	featureFlagsTest({
		'LPS-178052': {enabled: true},
	}),
	isolatedSiteTest,
	loginTest()
);

test.beforeEach(async ({fdsSamplePage, page, site}) => {
	await fdsSamplePage.setupFDSSampleWidget({
		fragmentKeys: ['advanced-search-fds-sample'],
		site,
	});

	await fdsSamplePage.selectTab('Advanced');

	await waitForFDS({page});
});

test(
	'Search behavior',
	{
		tag: ['@LPD-54150', '@LPD-56876', '@LPD-63092'],
	},
	async ({fdsSamplePage, page}) => {
		const searchInput = fdsSamplePage.managementToolbar.searchInput;

		await test.step('The total results label and search resume are displayed when a search is made', async () => {
			await test.step('Search for "Sample55"', async () => {
				await searchInput.fill('Sample55');

				await fdsSamplePage.managementToolbar.searchButton.click();
			});

			await test.step('Check that "1 Result Found for:" is displayed', async () => {
				await expect(
					page.getByText('1 Result Found for:')
				).toBeVisible();
			});

			await test.step('Check that the search resume displays "Search:Sample55"', async () => {
				await expect(
					fdsSamplePage.activeFiltersToolbar.searchResume
				).toBeVisible();
				await expect(
					fdsSamplePage.activeFiltersToolbar.searchResume
				).toContainText('Search:Sample55');
			});
		});

		await test.step('Search appropriately filters the data', async () => {
			await test.step('Check that only 1 result is found and has the title "Sample55" in the table row', async () => {
				await expect(fdsSamplePage.table.bodyRows).toHaveCount(1);
				await expect(
					fdsSamplePage.table.bodyRows
						.first()
						.getByRole('cell', {exact: true, name: 'Sample55'})
				).toBeVisible();
			});
		});

		await test.step('Clicking the "Clear Search" icon on the search resume clears the search', async () => {
			await test.step('Click on the "Clear Search" icon in the search resume', async () => {
				await fdsSamplePage.activeFiltersToolbar.clearSearchButton.click();

				await expect(
					page.getByText('75 Results Found for:')
				).toBeVisible();
			});

			await test.step('Check the search bar input is blank', async () => {
				await expect(searchInput).toBeEmpty();
			});

			await test.step('Check the search resume label is not displayed', async () => {
				await expect(
					fdsSamplePage.activeFiltersToolbar.searchResume
				).not.toBeVisible();
			});
		});

		await test.step('Applying the same search twice does not show "Requesting Results for:" indefinitely', async () => {
			await test.step('Reset the tab', async () => {
				await fdsSamplePage.selectTab('Advanced');

				await waitForFDS({
					page,
				});
			});

			await test.step('Search for "Sample" and wait for the search to finish', async () => {
				await searchInput.fill('Sample');

				await fdsSamplePage.managementToolbar.searchButton.click();

				await expect(
					page.getByText('75 Results Found for:')
				).toBeVisible();
			});

			await test.step('Click search again', async () => {
				await fdsSamplePage.managementToolbar.searchButton.click();
			});

			await test.step('Check that "Requesting Results for:" is not displayed', async () => {
				await expect(
					page.getByText('Requesting Results for:')
				).not.toBeVisible();

				await expect(
					page.getByText('75 Results Found for:')
				).toBeVisible();
			});
		});

		await test.step('Check Search clear button', async () => {
			const searchValue = getRandomString();

			await test.step('Fill search input', async () => {
				await searchInput.fill(searchValue);

				await expect(searchInput).toHaveValue(searchValue);
			});

			await test.step('Clean search text by the input clear button', async () => {
				const searchInputBox = await searchInput.boundingBox();

				await searchInput.click({
					position: {
						x: searchInputBox.width - 10,
						y: searchInputBox.height / 2,
					},
				});
			});

			await test.step('Check that input is empty', async () => {
				await expect(searchInput).toHaveValue('');
			});
		});

		await test.step('Global FDS state integration', async () => {
			const fragmentInput = page.getByPlaceholder(
				'Search in Advanced tab of'
			);
			const fragmentButton = page.getByTestId(
				'advancedSearchFDSSampleButton'
			);

			await test.step('Search in fragment results with search in FDS', async () => {
				const sampleSearchText = getRandomString();

				await fragmentInput.fill(sampleSearchText);

				await fragmentButton.click();

				await expect(searchInput).toHaveValue(sampleSearchText);
			});

			await test.step('Search in FDS UI reflects in fragment', async () => {
				const sampleSearchText = getRandomString();

				await searchInput.fill(sampleSearchText);

				await fdsSamplePage.managementToolbar.searchButton.click();

				await expect(fragmentInput).toHaveValue(sampleSearchText);
			});

			await test.step('Clear search in FDS UI reflects in fragment', async () => {
				const sampleSearchText = getRandomString();

				await searchInput.fill(sampleSearchText);

				await fdsSamplePage.managementToolbar.searchButton.click();

				await waitForFDS({
					empty: true,
					page,
				});

				await expect(fragmentInput).toHaveValue(sampleSearchText);

				await fdsSamplePage.activeFiltersToolbar.clearSearchButton.click();

				await expect(fragmentInput).toHaveValue('');
			});

			await test.step('Clear all in FDS UI reflects in fragment', async () => {
				const sampleSearchText = getRandomString();

				await searchInput.fill(sampleSearchText);

				await fdsSamplePage.managementToolbar.searchButton.click();

				await waitForFDS({
					empty: true,
					page,
				});

				await expect(fragmentInput).toHaveValue(sampleSearchText);

				await fdsSamplePage.activeFiltersToolbar.clearButton.click();

				await expect(fragmentInput).toHaveValue('');
			});

			await test.step('Search in FDS URL config reflects in fragment', async () => {
				const sampleSearchText = getRandomString();

				await searchInput.fill(sampleSearchText);

				await fdsSamplePage.managementToolbar.searchButton.click();

				await waitForFDS({
					empty: true,
					page,
				});

				// FDS URL config will be set after page reload

				await page.reload();

				await waitForFDS({
					empty: true,
					page,
				});

				await expect(fragmentInput).toHaveValue(sampleSearchText);
			});
		});
	}
);

test('Search Bar is shown/hidden according to FDS configuration', async ({
	dataSetManagerApiHelpers,
	fdsSamplePage,
	page,
}) => {
	const erc =
		'com_liferay_frontend_data_set_sample_web_internal_portlet_FDSSamplePortlet-advanced';

	let createdDataSet = false;

	await test.step('Check that the search bar is shown by default', async () => {
		await expect(fdsSamplePage.managementToolbar.searchInput).toBeVisible();
	});

	await test.step('If exists, update Advanced Sample DataSet to disable search bar. Otherwise, create it with disabled search bar', async () => {
		const response = await dataSetManagerApiHelpers.getResponse(
			getDataSetResourceURL({dataSetERC: erc})
		);

		if (response.ok()) {
			await dataSetManagerApiHelpers.updateDataSet({
				erc,
				showSearch: false,
			});
		}
		else {
			await dataSetManagerApiHelpers.createDataSet({
				erc,
				label: 'Advanced Sample',
				restApplication: '/c/fdssamples',
				restEndpoint: '/',
				restSchema: 'FDSSample',
				showSearch: false,
				snapshotsEnabled: true,
			});

			createdDataSet = true;
		}
	});

	await test.step('Check search bar is not shown anymore', async () => {
		await page.reload();

		await expect(
			fdsSamplePage.managementToolbar.searchInput
		).not.toBeVisible();
	});

	await test.step('Reset FDS configuration', async () => {
		if (createdDataSet) {
			await dataSetManagerApiHelpers.deleteDataSet({erc});
		}
		else {
			await dataSetManagerApiHelpers.updateDataSet({
				erc,
				showSearch: true,
			});
		}
	});
});
