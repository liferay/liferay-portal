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
	'Behavior of filters',
	{
		tag: ['@LPS-150047'],
	},
	async ({fdsSamplePage, page}) => {
		const blueCells = page.getByRole('cell', {name: 'Blue'});
		const greenCells = page
			.getByRole('cell', {name: 'Green'})
			.or(page.getByRole('cell', {name: '🍏'}));
		const redCells = page.getByRole('cell', {name: 'Red'});
		const yellowCells = page.getByRole('cell', {name: 'Yellow'});

		await test.step('Check filter is preloaded when entering on an FDS page for first time', async () => {
			await test.step('Check the active filters button displays with "Blue, Green, Yellow"', async () => {
				await expect
					.soft(
						page.getByRole('button', {
							name: 'Color: Blue, Green, Yellow',
						})
					)
					.toBeVisible();
			});

			await test.step('Check the results only show results with colors Blue, Green, and Yellow', async () => {
				expect.soft(await blueCells.count()).toBeGreaterThan(0);
				expect.soft(await greenCells.count()).toBeGreaterThan(0);
				expect.soft(await redCells.count()).toEqual(0);
				expect.soft(await yellowCells.count()).toBeGreaterThan(0);
			});
		});

		await test.step('Check clear filters button', async () => {
			await test.step('Check the total amount of items is 75', async () => {
				expect
					.soft(page.getByText('Showing 1 to 20 of 75 entries.'))
					.toBeVisible();
			});

			await test.step('Click on clear filters button', async () => {
				await fdsSamplePage.activeFiltersToolbar
					.getByRole('button', {name: 'Clear'})
					.click();

				await page
					.getByText('This is a description for sample 1.')
					.waitFor();
			});

			await test.step('Check the filters summary boxes are empty', async () => {
				await expect(
					page.getByRole('button', {
						name: 'Color: Blue, Green, Yellow',
					})
				).not.toBeVisible();
			});

			await test.step('Check the total amount of items is now 100', async () => {
				expect
					.soft(page.getByText('Showing 1 to 20 of 100 entries.'))
					.toBeVisible();
			});

			await test.step('Check that the results are not filtered by checking "Red" results are displayed', async () => {
				const redCells = page.getByRole('cell', {name: 'Red'});

				expect.soft(await redCells.count()).toBeGreaterThan(0);
			});
		});

		await test.step('Check searching the available filters in filter dropdown', async () => {
			await test.step('Open filter dropdown', async () => {
				await fdsSamplePage.managementToolbar.container
					.getByRole('button', {name: 'Filter'})
					.click();
			});

			await test.step('Enter a search term "status"', async () => {
				await page
					.locator('.dropdown-menu')
					.getByLabel('Search')
					.first()
					.fill('status');
			});

			await test.step('Check only the "status" filter appears', async () => {
				await expect(
					page.getByRole('menuitem', {name: 'Color'})
				).not.toBeVisible();
				await expect(
					page.getByRole('menuitem', {name: 'Date Range'})
				).not.toBeVisible();
				await expect(
					page.getByRole('menuitem', {name: 'Size'})
				).not.toBeVisible();
				await expect(
					page.getByRole('menuitem', {name: 'Status'})
				).toBeVisible();
			});

			await test.step('Clear search bar in filter dropdown', async () => {
				await page
					.locator('.dropdown-menu')
					.getByLabel('Search')
					.first()
					.clear();
			});

			await test.step('Check all items appear', async () => {
				await expect(
					page.getByRole('menuitem', {name: 'Color'})
				).toBeVisible();
				await expect(
					page.getByRole('menuitem', {name: 'Date Range'})
				).toBeVisible();
				await expect(
					page.getByRole('menuitem', {name: 'Size'})
				).toBeVisible();
				await expect(
					page.getByRole('menuitem', {name: 'Status'})
				).toBeVisible();
			});
		});

		await test.step('Check that no filters were found message is displayed', async () => {
			await test.step('Refresh the page', async () => {
				await page.reload();

				await page
					.getByText('This is a description for sample 1.')
					.waitFor();
			});

			await test.step('Open filter dropdown', async () => {
				await fdsSamplePage.managementToolbar.container
					.getByRole('button', {name: 'Filter'})
					.click();
			});

			await test.step('Enter a search term that does not exist', async () => {
				await page
					.locator('.dropdown-menu')
					.getByLabel('Search')
					.first()
					.fill('nonexistent');
			});

			await test.step('Check a message was displayed', async () => {
				await expect(
					page.getByText('No filters were found.')
				).toBeVisible();
			});
		});

		await test.step('Check selecting a filter', async () => {
			await test.step('Refresh the page', async () => {
				await page.reload();

				await page
					.getByText('This is a description for sample 1.')
					.waitFor();
			});

			await test.step('Select "Red" color in the filters dropdown', async () => {
				await fdsSamplePage.managementToolbar.container
					.getByRole('button', {name: 'Filter'})
					.click();

				await page
					.locator('.dropdown-menu')
					.getByRole('menuitem', {name: 'Color'})
					.click();

				await page
					.locator('.dropdown-menu')
					.getByRole('checkbox', {name: 'Red'})
					.check();

				await page
					.locator('.dropdown-menu')
					.getByRole('button', {name: 'Show Results'})
					.click();

				await page
					.getByText('This is a description for sample 10.')
					.waitFor();
			});

			await test.step('Check the results are filtered by checking all results appear', async () => {
				page.getByRole('cell', {name: 'Yellow'});

				expect.soft(await blueCells.count()).toBeGreaterThan(0);
				expect.soft(await greenCells.count()).toBeGreaterThan(0);
				expect.soft(await redCells.count()).toBeGreaterThan(0);
				expect.soft(await yellowCells.count()).toBeGreaterThan(0);
			});
		});

		await test.step('Check excluding a filter', async () => {
			await test.step('Refresh the page', async () => {
				await page.reload();

				await page
					.getByText('This is a description for sample 1.')
					.waitFor();
			});

			await test.step('Check exclude switch for "Blue", "Green", "Yellow" colors', async () => {
				await fdsSamplePage.managementToolbar.container
					.getByRole('button', {name: 'Filter'})
					.click();

				await page.getByRole('menuitem', {name: 'Color'}).click();

				await page.getByLabel('Exclude').check();
			});

			await test.step('Click "Show Results"', async () => {
				await page.getByRole('button', {name: 'Show Results'}).click();

				await page
					.getByText('This is a description for sample 10.')
					.waitFor();
			});

			await test.step('Check the only Red results are displayed', async () => {
				expect.soft(await blueCells.count()).toEqual(0);
				expect.soft(await greenCells.count()).toEqual(0);
				expect.soft(await redCells.count()).toBeGreaterThan(0);
				expect.soft(await yellowCells.count()).toEqual(0);
			});
		});

		await test.step('Check editing a filter summary box', async () => {
			await test.step('Refresh the page', async () => {
				await page.reload();

				await page
					.getByText('This is a description for sample 1.')
					.waitFor();
			});

			await test.step('Open the "Color" filter summary box', async () => {
				await page
					.getByRole('button', {name: 'Color: Blue, Green, Yellow'})
					.click();
			});

			await test.step('Change the selections by selecting "Red" and unselecting "Blue"', async () => {
				await page.getByRole('checkbox', {name: 'Red'}).check();
				await page.getByRole('checkbox', {name: 'Blue'}).uncheck();

				await page.getByRole('button', {name: 'Show Results'}).click();

				await page
					.getByText('This is a description for sample 1.')
					.waitFor();
			});

			await test.step('Check the results only show "Green", "Yellow", and "Red"', async () => {
				expect.soft(await blueCells.count()).toEqual(0);
				expect.soft(await greenCells.count()).toBeGreaterThan(0);
				expect.soft(await redCells.count()).toBeGreaterThan(0);
				expect.soft(await yellowCells.count()).toBeGreaterThan(0);
			});
		});

		await test.step('Check a single filter can be removed', async () => {
			await test.step('Click the remove button on the filter summary box', async () => {
				await page.getByRole('button', {name: 'Remove Filter'}).click();

				await page
					.getByText('This is a description for sample 1.')
					.waitFor();
			});

			await test.step('Check all results are shown', async () => {
				expect.soft(await blueCells.count()).toBeGreaterThan(0);
				expect.soft(await greenCells.count()).toBeGreaterThan(0);
				expect.soft(await yellowCells.count()).toBeGreaterThan(0);
				expect.soft(await redCells.count()).toBeGreaterThan(0);
			});
		});

		await test.step('Check filter can be removed using delete button', async () => {
			await test.step('Refresh the page', async () => {
				await page.reload();

				await page
					.getByText('This is a description for sample 1.')
					.waitFor();
			});

			await test.step('Open the "Color" filter summary box', async () => {
				await page
					.getByRole('button', {name: 'Color: Blue, Green, Yellow'})
					.click();
			});

			await test.step('Uncheck filter and click on delete button on the filter summary box', async () => {
				await page.getByRole('checkbox', {name: 'Blue'}).uncheck();
				await page.getByRole('checkbox', {name: 'Green'}).uncheck();
				await page.getByRole('checkbox', {name: 'Yellow'}).uncheck();

				await page.getByRole('button', {name: 'Delete Filter'}).click();

				await page
					.getByText('This is a description for sample 1.')
					.waitFor();
			});

			await test.step('Check all results are shown', async () => {
				expect.soft(await blueCells.count()).toBeGreaterThan(0);
				expect.soft(await greenCells.count()).toBeGreaterThan(0);
				expect.soft(await yellowCells.count()).toBeGreaterThan(0);
				expect.soft(await redCells.count()).toBeGreaterThan(0);
			});
		});

		await test.step('Assert the synchronization of the filters', async () => {
			await test.step('Refresh the page', async () => {
				await page.reload();

				await page
					.getByText('This is a description for sample 1.')
					.waitFor();
			});

			await test.step('Open the "Color" filter summary box', async () => {
				await page
					.getByRole('button', {name: 'Color: Blue, Green, Yellow'})
					.click();
			});

			await test.step('Uncheck the "Blue" checkbox', async () => {
				await page.getByRole('checkbox', {name: 'Blue'}).uncheck();
			});

			await test.step('Click on the "Show Results" button', async () => {
				await page.getByRole('button', {name: 'Show Results'}).click();
			});

			await test.step('Assert that the active filters button displays with "Green, Yellow" and the checkboxes are checked', async () => {
				const activeFiltersButton = page
					.getByRole('button')
					.filter({hasText: 'Filter'});

				await expect(activeFiltersButton).toBeVisible();

				await activeFiltersButton.click();

				await page.getByRole('menuitem', {name: 'Color'}).click();

				await expect(
					page.getByRole('checkbox', {name: 'Green'})
				).toBeChecked();

				await expect(
					page.getByRole('checkbox', {name: 'Yellow'})
				).toBeChecked();
			});

			await test.step('Uncheck the "Green" checkbox and assert the filter resume is updated to "Yellow"', async () => {
				await page.getByRole('checkbox', {name: 'Green'}).uncheck();

				await expect(
					page.getByRole('checkbox', {name: 'Green'})
				).not.toBeChecked();

				await page.getByRole('button', {name: 'Show Results'}).click();

				await expect(
					page.getByRole('button', {name: 'Color: Yellow'})
				).toBeVisible();
			});
		});
	}
);
