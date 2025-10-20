/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {apiHelpersTest} from '../../../../../fixtures/apiHelpersTest';
import {featureFlagsTest} from '../../../../../fixtures/featureFlagsTest';
import {isolatedSiteTest} from '../../../../../fixtures/isolatedSiteTest';
import {loginTest} from '../../../../../fixtures/loginTest';
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

test(
	'Check the empty state',
	{
		tag: ['@LPD-56880'],
	},
	async ({fdsSamplePage, page}) => {
		await test.step('Check the empty state with a filter applied', async () => {
			await test.step('Apply the status "Pending" filter', async () => {
				await fdsSamplePage.managementToolbar.container
					.getByRole('button', {name: 'Filter'})
					.click();

				await page
					.locator('.dropdown-menu')
					.getByRole('menuitem', {name: 'Status'})
					.click();

				await page
					.locator('.dropdown-menu')
					.getByRole('checkbox', {name: 'Pending'})
					.check();

				await page
					.locator('.dropdown-menu')
					.getByRole('button', {name: 'Add Filter'})
					.click();
			});

			await test.step('Check that the "/states/search_state.svg" image is displayed', async () => {
				await expect(
					fdsSamplePage.emptyStateContainer.locator(
						'img[src$="/states/search_state.svg"]'
					)
				).toBeVisible();
			});

			await test.step('Check that the "/states/search_state_reduced_motion.svg" image exists in the DOM', async () => {
				await expect(
					fdsSamplePage.emptyStateContainer.locator(
						'img[src$="/states/search_state_reduced_motion.svg"]'
					)
				).toBeAttached();
			});

			await test.step('Check that the text "No Results Found" is displayed', async () => {
				await expect(page.getByText('No Results Found')).toBeVisible();
			});

			await test.step('Check that the text "Review your filters and try again." is displayed', async () => {
				await expect(
					page.getByText('Review your filters and try again.')
				).toBeVisible();
			});

			await test.step('Check that the button "Clear Filters" is displayed', async () => {
				await expect(
					fdsSamplePage.emptyStateContainer.getByRole('button', {
						name: 'Clear Filters',
					})
				).toBeVisible();
			});

			await test.step('Check that the "Clear Filters" button has btn-secondary class', async () => {
				await expect(
					fdsSamplePage.emptyStateContainer.getByRole('button', {
						name: 'Clear Filters',
					})
				).toHaveClass(/btn-secondary/);
			});

			await test.step('Check that when clicking the button "Clear Filters", the filters are removed', async () => {
				await fdsSamplePage.emptyStateContainer
					.getByRole('button', {name: 'Clear Filters'})
					.click();

				await waitForFDS({
					page,
					visualizationMode: EFDSVisualizationMode.TABLE,
				});

				await expect(
					fdsSamplePage.activeFiltersToolbar
				).not.toBeVisible();
			});
		});

		await test.step('Check the empty state with a search applied and customized configuration', async () => {
			await test.step('Search using a keyword that will return no results, "no results"', async () => {
				await fdsSamplePage.managementToolbar.searchInput.fill(
					'no results'
				);

				await fdsSamplePage.managementToolbar.container
					.getByRole('button', {name: 'Search'})
					.click();
			});

			await test.step('Check that the text "Custom Title" is displayed', async () => {
				await expect(page.getByText('Custom Title')).toBeVisible();
			});

			await test.step('Check that the text "Custom Description" is displayed', async () => {
				await expect(
					page.getByText('Custom Description')
				).toBeVisible();
			});

			await test.step('Check that the "/states/empty_state.svg" image is displayed', async () => {
				await expect(
					fdsSamplePage.emptyStateContainer.locator(
						'img[src$="/states/empty_state.svg"]'
					)
				).toBeVisible();
			});

			await test.step('Check that the "/states/empty_state_reduced_motion.svg" image exists in the DOM', async () => {
				await expect(
					fdsSamplePage.emptyStateContainer.locator(
						'img[src$="/states/empty_state_reduced_motion.svg"]'
					)
				).toBeAttached();
			});

			await test.step('Check that the button "Clear Search" is displayed', async () => {
				await expect(
					fdsSamplePage.emptyStateContainer.getByRole('button', {
						name: 'Clear Search',
					})
				).toBeVisible();
			});

			await test.step('Check that the "Clear Search" button has btn-secondary class', async () => {
				await expect(
					fdsSamplePage.emptyStateContainer.getByRole('button', {
						name: 'Clear Search',
					})
				).toHaveClass(/btn-secondary/);
			});

			await test.step('Check that when clicking the button "Clear Search", the search is removed', async () => {
				await fdsSamplePage.emptyStateContainer
					.getByRole('button', {name: 'Clear Search'})
					.click();

				await waitForFDS({
					page,
					visualizationMode: EFDSVisualizationMode.TABLE,
				});

				await expect(
					fdsSamplePage.managementToolbar.searchInput
				).toBeEmpty();
			});
		});

		await test.step('Check the empty state with a search and filter applied', async () => {
			await test.step('Apply the status "Pending" filter', async () => {
				await fdsSamplePage.managementToolbar.container
					.getByRole('button', {name: 'Filter'})
					.click();

				await page
					.locator('.dropdown-menu')
					.getByRole('checkbox', {name: 'Pending'})
					.check();

				await page
					.locator('.dropdown-menu')
					.getByRole('button', {name: 'Add Filter'})
					.click();
			});

			await test.step('Search using a keyword that will return no results, "no results"', async () => {
				await fdsSamplePage.managementToolbar.searchInput.fill(
					'no results'
				);

				await fdsSamplePage.managementToolbar.container
					.getByRole('button', {name: 'Search'})
					.click();
			});

			await test.step('Check that the "/states/search_state.svg" image is displayed', async () => {
				await expect(
					fdsSamplePage.emptyStateContainer.locator(
						'img[src$="/states/search_state.svg"]'
					)
				).toBeVisible();
			});

			await test.step('Check that the "/states/search_state_reduced_motion.svg" image exists in the DOM', async () => {
				await expect(
					fdsSamplePage.emptyStateContainer.locator(
						'img[src$="/states/search_state_reduced_motion.svg"]'
					)
				).toBeAttached();
			});

			await test.step('Check that the text "No Results Found" is displayed', async () => {
				await expect(page.getByText('No Results Found')).toBeVisible();
			});

			await test.step('Check that the text "Review your filters or search and try again." is displayed', async () => {
				await expect(
					page.getByText(
						'Review your filters or search and try again.'
					)
				).toBeVisible();
			});

			await test.step('Check that the button "Clear Search And Filters" is displayed', async () => {
				await expect(
					fdsSamplePage.emptyStateContainer.getByRole('button', {
						name: 'Clear Search And Filters',
					})
				).toBeVisible();
			});

			await test.step('Check that the "Clear Search And Filters" button has btn-secondary class', async () => {
				await expect(
					fdsSamplePage.emptyStateContainer.getByRole('button', {
						name: 'Clear Search And Filters',
					})
				).toHaveClass(/btn-secondary/);
			});

			await test.step('Check that when clicking the button "Clear Search And Filters", the search and filters are removed', async () => {
				await fdsSamplePage.emptyStateContainer
					.getByRole('button', {
						name: 'Clear Search And Filters',
					})
					.click();

				await waitForFDS({
					page,
					visualizationMode: EFDSVisualizationMode.TABLE,
				});

				await expect(
					fdsSamplePage.managementToolbar.searchInput
				).toBeEmpty();

				await expect(
					fdsSamplePage.activeFiltersToolbar
				).not.toBeVisible();
			});
		});
	}
);

test(
	'Check that management toolbar is rendered when no items but filters/search are active',
	{
		tag: ['@LPD-65541'],
	},
	async ({fdsSamplePage, page}) => {
		await test.step('Check management toolbar is rendered with filter applied and no items', async () => {
			await test.step('Apply the status "Pending" filter', async () => {
				await fdsSamplePage.managementToolbar.container
					.getByRole('button', {name: 'Filter'})
					.click();

				await page
					.locator('.dropdown-menu')
					.getByRole('menuitem', {name: 'Status'})
					.click();

				await page
					.locator('.dropdown-menu')
					.getByRole('checkbox', {name: 'Pending'})
					.check();

				await page
					.locator('.dropdown-menu')
					.getByRole('button', {name: 'Add Filter'})
					.click();
			});

			await test.step('Check that the text "No Results Found" is displayed', async () => {
				await expect(page.getByText('No Results Found')).toBeVisible();
			});

			await test.step('Verify that the management toolbar is rendered with an active filter', async () => {
				await expect(
					fdsSamplePage.managementToolbar.container
				).toBeVisible();
			});
		});

		await test.step('Verify that the management toolbar is rendered with search applied and no items', async () => {
			await test.step('Clear the filter first', async () => {
				await fdsSamplePage.emptyStateContainer
					.getByRole('button', {name: 'Clear Filters'})
					.click();

				await waitForFDS({
					page,
					visualizationMode: EFDSVisualizationMode.TABLE,
				});
			});

			await test.step('Search using a keyword that will return no results', async () => {
				await fdsSamplePage.managementToolbar.searchInput.fill('Foo');

				await fdsSamplePage.managementToolbar.container
					.getByRole('button', {name: 'Search'})
					.click();
			});

			await test.step('Check that the text "Custom Title" is displayed', async () => {
				await expect(page.getByText('Custom Title')).toBeVisible();
			});

			await test.step('Verify that the management toolbar is rendered with an active search', async () => {
				await expect(
					fdsSamplePage.managementToolbar.container
				).toBeVisible();

				await expect(
					fdsSamplePage.managementToolbar.searchInput
				).toBeVisible();

				await expect(
					fdsSamplePage.managementToolbar.container.getByRole(
						'button',
						{name: 'Filter'}
					)
				).toBeVisible();
			});
		});

		await test.step('Check that the management toolbar is rendered with both filter and search applied and no items', async () => {
			await test.step('Apply the status "Pending" filter while search is active', async () => {
				await fdsSamplePage.managementToolbar.container
					.getByRole('button', {name: 'Filter'})
					.click();

				await page
					.locator('.dropdown-menu')
					.getByRole('checkbox', {name: 'Pending'})
					.check();

				await page
					.locator('.dropdown-menu')
					.getByRole('button', {name: 'Add Filter'})
					.click();
			});

			await test.step('Check that the text "No Results Found" is displayed', async () => {
				await expect(page.getByText('No Results Found')).toBeVisible();
			});

			await test.step('Verify that the management toolbar is rendered with an active filter and an active search', async () => {
				await expect(
					fdsSamplePage.managementToolbar.container
				).toBeVisible();

				await expect(
					fdsSamplePage.managementToolbar.searchInput
				).toBeVisible();

				await expect(
					fdsSamplePage.managementToolbar.container.getByRole(
						'button',
						{name: 'Filter'}
					)
				).toBeVisible();
			});
		});
	}
);
