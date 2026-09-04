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

test(
	'Recent searches',
	{
		tag: ['@LPD-89792'],
	},
	async ({fdsSamplePage, page}) => {
		const searchInput = fdsSamplePage.managementToolbar.searchInput;

		await test.step('The dropdown is not shown when nothing has been searched yet', async () => {
			await searchInput.click();

			await expect(fdsSamplePage.recentSearches.menu).toBeHidden();
		});

		await test.step('A query that returned results is listed when the empty input is focused', async () => {
			await fdsSamplePage.search('Sample55');

			await expect(page.getByText('1 Result Found for:')).toBeVisible();

			await searchInput.clear();

			await searchInput.click();

			await expect(
				fdsSamplePage.recentSearchEntry('Sample55')
			).toBeVisible();
		});

		await test.step('A query that returned no results is not listed', async () => {
			await fdsSamplePage.search(getRandomString());

			await fdsSamplePage.emptyStateContainer.waitFor();

			await searchInput.clear();

			await searchInput.click();

			await expect(fdsSamplePage.recentSearches.entries).toHaveText([
				'Sample55',
			]);
		});

		await test.step('The most recent query is listed first', async () => {
			await fdsSamplePage.search('Sample12');

			await expect(page.getByText('1 Result Found for:')).toBeVisible();

			await searchInput.clear();

			await searchInput.click();

			await expect(fdsSamplePage.recentSearches.entries).toHaveText([
				'Sample12',
				'Sample55',
			]);
		});

		await test.step('Typing keeps only the queries matching the input and emphasizes the match', async () => {
			await searchInput.fill('sample5');

			await expect(fdsSamplePage.recentSearches.entries).toHaveText([
				'Sample55',
			]);

			await expect(
				fdsSamplePage.recentSearchEntry('Sample55').locator('strong')
			).toHaveText('Sample5');
		});

		await test.step('The dropdown is not shown when no stored query matches the input', async () => {
			await searchInput.fill(getRandomString());

			await expect(fdsSamplePage.recentSearches.menu).toBeHidden();
		});

		await test.step('Clicking a query fills the input and searches for it', async () => {
			await searchInput.clear();

			await searchInput.click();

			await fdsSamplePage.recentSearchEntry('Sample55').click();

			await expect(searchInput).toHaveValue('Sample55');
			await expect(page.getByText('1 Result Found for:')).toBeVisible();
			await expect(fdsSamplePage.table.bodyRows).toHaveCount(1);
			await expect(fdsSamplePage.recentSearches.menu).toBeHidden();
		});

		await test.step('Removing a query leaves the rest of the list open', async () => {
			await searchInput.clear();

			await searchInput.click();

			await fdsSamplePage.recentSearchEntry('Sample55').hover();

			await fdsSamplePage.recentSearchRemoveButton('Sample55').click();

			await expect(fdsSamplePage.recentSearches.entries).toHaveText([
				'Sample12',
			]);
		});

		await test.step('The dropdown closes when the user clicks outside the search bar', async () => {
			await fdsSamplePage.table.container.click();

			await expect(fdsSamplePage.recentSearches.menu).toBeHidden();
		});

		await test.step('The queries survive a page reload', async () => {
			await fdsSamplePage.activeFiltersToolbar.clearSearchButton.click();

			await page.reload();

			await waitForFDS({page});

			await searchInput.click();

			await expect(fdsSamplePage.recentSearches.entries).toHaveText([
				'Sample12',
			]);
		});

		await test.step('Clearing all removes every query', async () => {
			await fdsSamplePage.recentSearches.clearAllButton.click();

			await expect(fdsSamplePage.recentSearches.menu).toBeHidden();

			await searchInput.click();

			await expect(fdsSamplePage.recentSearches.menu).toBeHidden();
		});
	}
);

test(
	'Recent searches follow the search box',
	{
		tag: ['@LPD-89792'],
	},
	async ({fdsSamplePage, page}) => {
		const magnifierButton = page
			.locator('.navbar-breakpoint-d-none button')
			.first();
		const searchInput = fdsSamplePage.managementToolbar.searchInput;

		await test.step('Remember a query for the dropdown to list', async () => {
			await fdsSamplePage.search('Sample55');

			await expect(page.getByText('1 Result Found for:')).toBeVisible();

			await searchInput.clear();
		});

		await test.step('The dropdown opens as wide as the search box', async () => {
			await searchInput.click();

			await expect(fdsSamplePage.recentSearches.menu).toBeVisible();

			const menuBoundingBox =
				await fdsSamplePage.recentSearches.menu.boundingBox();
			const searchBoxBoundingBox = await searchInput.boundingBox();

			expect(menuBoundingBox!.width).toBeGreaterThanOrEqual(
				searchBoxBoundingBox!.width
			);
		});

		await test.step('The dropdown narrows with the search box', async () => {
			const widthBefore =
				(await fdsSamplePage.recentSearches.menu.boundingBox())!.width;

			await page.setViewportSize({height: 800, width: 960});

			await expect(async () => {
				const menuBoundingBox =
					await fdsSamplePage.recentSearches.menu.boundingBox();
				const searchBoxBoundingBox = await searchInput.boundingBox();

				expect(menuBoundingBox!.width).toBeLessThan(widthBefore);
				expect(menuBoundingBox!.width).toBeGreaterThanOrEqual(
					searchBoxBoundingBox!.width
				);
			}).toPass();
		});

		await test.step('The dropdown closes when the management bar puts the search box behind a button', async () => {
			await page.setViewportSize({height: 800, width: 700});

			await expect(searchInput).toBeHidden();

			await expect(fdsSamplePage.recentSearches.menu).toBeHidden();
		});

		await test.step('The dropdown opens on the search box that button reveals', async () => {
			await magnifierButton.click();

			await expect(searchInput).toBeVisible();

			await searchInput.click();

			await expect(fdsSamplePage.recentSearches.menu).toBeVisible();
		});

		await test.step('The dropdown closes when the revealed search box is put away', async () => {
			await searchInput.fill('Sample');

			await searchInput.clear();

			await expect(searchInput).toBeHidden();

			await expect(fdsSamplePage.recentSearches.menu).toBeHidden();
		});
	}
);
