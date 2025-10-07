/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {apiHelpersTest} from '../../../../../fixtures/apiHelpersTest';
import {featureFlagsTest} from '../../../../../fixtures/featureFlagsTest';
import {isolatedSiteTest} from '../../../../../fixtures/isolatedSiteTest';
import {loginTest} from '../../../../../fixtures/loginTest';
import getRandomString from '../../../../../utils/getRandomString';
import {EFDSVisualizationMode, waitForFDS} from '../../../../../utils/waitFor';
import {fdsSamplePageTest} from '../../fixtures/fdsSamplePageTest';

const test = mergeTests(
	apiHelpersTest,
	fdsSamplePageTest,
	featureFlagsTest({
		'LPS-178052': {enabled: true},
	}),
	isolatedSiteTest,
	loginTest()
);

test.beforeEach(async ({fdsSamplePage, page, site}) => {
	await fdsSamplePage.setupFDSSampleWidget({site});

	await fdsSamplePage.selectTab('Advanced');

	await waitForFDS({page, visualizationMode: EFDSVisualizationMode.TABLE});
});

test('Check Search clear button', async ({fdsSamplePage}) => {
	const searchInput = fdsSamplePage.managementToolbar.searchInput;
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

test(
	'Check behavior of search',
	{
		tag: ['@LPD-56876', '@LPD-63092'],
	},
	async ({fdsSamplePage, page}) => {
		await test.step('The total results label and search resume are displayed when a search is made', async () => {
			await test.step('Search for "Sample55"', async () => {
				await fdsSamplePage.managementToolbar.searchInput.fill(
					'Sample55'
				);

				await fdsSamplePage.managementToolbar.searchButton.click();
			});

			await test.step('Check that "1 Result Found for:" is displayed', async () => {
				await expect(
					page.getByText('1 Result Found for:')
				).toBeVisible();
			});

			await test.step('Check that the search resume displays "Search:Sample55"', async () => {
				const searchResume =
					fdsSamplePage.activeFiltersToolbar.locator(
						'.search-resume'
					);

				await expect(searchResume).toBeVisible();
				await expect(searchResume).toContainText('Search:Sample55');
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
				await fdsSamplePage.activeFiltersToolbar
					.locator('.search-resume')
					.getByRole('button', {name: 'Clear Search'})
					.click();

				await expect(
					page.getByText('75 Results Found for:')
				).toBeVisible();
			});

			await test.step('Check the search bar input is blank', async () => {
				await expect(
					fdsSamplePage.managementToolbar.searchInput
				).toBeEmpty();
			});

			await test.step('Check the search resume label is not displayed', async () => {
				await expect(
					fdsSamplePage.activeFiltersToolbar.locator('.search-resume')
				).not.toBeVisible();
			});
		});

		await test.step('Applying the same search twice does not show "Requesting Results for:" indefinitely', async () => {
			await test.step('Reset the tab', async () => {
				await fdsSamplePage.selectTab('Advanced');

				await waitForFDS({
					page,
					visualizationMode: EFDSVisualizationMode.TABLE,
				});
			});

			await test.step('Search for "Sample" and wait for the search to finish', async () => {
				await fdsSamplePage.managementToolbar.searchInput.fill(
					'Sample'
				);

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
	}
);
