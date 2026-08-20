/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {apiHelpersTest} from '../../../../../fixtures/apiHelpersTest';
import {featureFlagsTest} from '../../../../../fixtures/featureFlagsTest';
import {isolatedSiteTest} from '../../../../../fixtures/isolatedSiteTest';
import {loginTest} from '../../../../../fixtures/loginTest';
import {clickAndExpectToBeVisible} from '../../../../../utils/clickAndExpectToBeVisible';
import {waitForFDS} from '../../../../../utils/waitFor';
import {fdsSamplePageTest} from '../../fixtures/fdsSamplePageTest';

// The Title filter is backed by an API, which the selection filter pages ten
// items at a time.

const SELECTION_FILTER_PAGE_SIZE = 10;

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
	await fdsSamplePage.setupFDSSampleWidget({
		fragmentKeys: ['advanced-filters-fds-sample'],
		site,
	});

	await fdsSamplePage.selectTab('Advanced');

	await waitForFDS({page});
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

		const creatorFilterSummaryButton = page
			.getByRole('button')
			.filter({hasText: /^Creator:/});

		const filtersFragment = {
			activeToggle: page.getByTestId('activeFiltersFDSSampleToggle'),
			excludeToggle: page.getByTestId('excludeFiltersFDSSampleToggle'),
		};

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
				await fdsSamplePage.activeFiltersToolbar.clearButton.click();

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

			await test.step('Check "Active" toggle in fragment', async () => {
				await expect(filtersFragment.activeToggle).not.toBeChecked();
			});
		});

		await test.step('Check searching the available filters in filter dropdown', async () => {
			await test.step('Open filter dropdown', async () => {
				await fdsSamplePage.managementToolbar.filterButton.click();
			});

			await test.step('Check grouped FDS filters visibility', async () => {
				await expect(
					page.locator('li.dropdown-subheader', {hasText: 'Group 1'})
				).toBeVisible();
				await expect(
					page.locator('li.dropdown-subheader', {hasText: 'Group 2'})
				).toBeVisible();
				await expect(
					page.locator('li.dropdown-subheader', {hasText: 'Group 3'})
				).toBeVisible();
				await expect(
					page.locator('li.dropdown-subheader', {
						hasText: 'Empty Group',
					})
				).not.toBeVisible();
				await expect(
					page.locator('li.dropdown-subheader', {
						hasText: 'Group With Unregistered Filter',
					})
				).not.toBeVisible();
			});

			await test.step('Check grouped FDS filters order', async () => {
				const filtersDropdownMenu = page.getByLabel('Filters');

				const groupedFilters = filtersDropdownMenu.getByRole('menu');

				await expect(
					groupedFilters.locator('li.dropdown-subheader')
				).toHaveText(['Group 1', 'Group 2', 'Group 3']);

				const group1 = groupedFilters.getByRole('group', {
					name: 'Group 1',
				});

				await expect(group1.getByRole('menuitem')).toHaveText([
					'Date Range',
					'Date Time Range',
					'Color',
				]);

				const group2 = groupedFilters.getByRole('group', {
					name: 'Group 2',
				});

				await expect(group2.getByRole('menuitem')).toHaveText([
					'Client Extension',
					'Size',
				]);

				const group3 = groupedFilters.getByRole('group', {
					name: 'Group 3',
				});

				await expect(group3.getByRole('menuitem')).toHaveText([
					'Status',
					'Title',
					'Creator',
				]);
			});

			await test.step('Enter a search term "status"', async () => {
				await fdsSamplePage.filterMenuSearchInput.fill('status');
			});

			await test.step('Check only the "status" filter appears', async () => {
				await expect(
					page.getByRole('menuitem', {name: 'Client Extension'})
				).not.toBeVisible();
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
					page.getByRole('menuitem', {
						exact: true,
						name: 'Date Range',
					})
				).toBeVisible();
				await expect(
					page.getByRole('menuitem', {name: 'Date Time Range'})
				).toBeVisible();
				await expect(
					page.getByRole('menuitem', {name: 'Client Extension'})
				).toBeVisible();
				await expect(
					page.getByRole('menuitem', {name: 'Size'})
				).toBeVisible();
				await expect(
					page.getByRole('menuitem', {name: 'Status'})
				).toBeVisible();
				await expect(
					page.getByRole('menuitem', {name: 'Creator'})
				).toBeVisible();
			});

			await test.step('Enter a search term "creator"', async () => {
				await fdsSamplePage.filterMenuSearchInput.fill('creator');
			});

			await test.step('Check only the "creator" filter appears', async () => {
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
				).not.toBeVisible();
				await expect(
					page.getByRole('menuitem', {name: 'Creator'})
				).toBeVisible();
			});
		});

		await test.step('Check selecting a filter', async () => {
			await test.step('Refresh the page', async () => {
				await fdsSamplePage.selectTab('Advanced');

				await waitForFDS({page});
			});

			await test.step('Select "Red" color in the filters dropdown', async () => {
				await fdsSamplePage.managementToolbar.container
					.getByRole('button', {name: 'Filter'})
					.click();

				await fdsSamplePage.filterMenu
					.getByRole('menuitem', {name: 'Color'})
					.click();

				await fdsSamplePage.filterMenu
					.getByRole('checkbox', {name: 'Red'})
					.check();

				await fdsSamplePage.filterShowResultsOrAddButton.click();

				await page
					.getByText('This is a description for sample 10.')
					.waitFor();
			});

			await test.step('Check the results are filtered by checking all results appear', async () => {
				expect(await blueCells.count()).toBeGreaterThan(0);
				expect(await greenCells.count()).toBeGreaterThan(0);
				expect(await redCells.count()).toBeGreaterThan(0);
				expect(await yellowCells.count()).toBeGreaterThan(0);
			});
		});

		await test.step('Check excluding a filter', async () => {
			await test.step('Refresh the page', async () => {
				await fdsSamplePage.selectTab('Advanced');

				await waitForFDS({page});
			});

			await test.step('Exclude "Blue", "Green" and "Yellow" colors', async () => {
				await fdsSamplePage.managementToolbar.filterButton.click();

				await fdsSamplePage.filterMenu
					.getByRole('menuitem', {name: 'Color'})
					.click();

				await fdsSamplePage.filterDropdownMenu
					.getByLabel('Exclude')
					.check();
			});

			await test.step('Click "Show Results"', async () => {
				await page.getByRole('button', {name: 'Show Results'}).click();

				await page
					.getByText('This is a description for sample 10.')
					.waitFor();
			});

			await test.step('Check the only Red results are displayed', async () => {
				expect(await blueCells.count()).toEqual(0);
				expect(await greenCells.count()).toEqual(0);
				expect(await redCells.count()).toBeGreaterThan(0);
				expect(await yellowCells.count()).toEqual(0);
			});

			await test.step('Check "Exclude" toggle in fragment', async () => {
				await expect(filtersFragment.excludeToggle).toBeChecked();
			});
		});

		await test.step('Check editing a filter summary box', async () => {
			await test.step('Refresh the page', async () => {
				await fdsSamplePage.selectTab('Advanced');

				await waitForFDS({page});
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

			await test.step('Check "Active" toggle in fragment', async () => {
				await expect(filtersFragment.activeToggle).not.toBeChecked();
			});
		});

		await test.step('Check filter can be removed using delete button', async () => {
			await test.step('Refresh the page', async () => {
				await fdsSamplePage.selectTab('Advanced');

				await waitForFDS({page});
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
				await fdsSamplePage.selectTab('Advanced');

				await waitForFDS({page});
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

				await fdsSamplePage.filterMenu
					.getByRole('menuitem', {name: 'Color'})
					.click();

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

		await test.step('Global FDS state integration', async () => {
			await test.step('Refresh the page', async () => {
				await fdsSamplePage.selectTab('Advanced');

				await waitForFDS({page});
			});

			await test.step('Toggle "Exclude" filter', async () => {
				await filtersFragment.excludeToggle.click();

				await expect(
					page.getByText('Color: (Exclude) Blue, Green, Yellow')
				).toBeVisible();
			});

			await test.step('Toggle "Active" filter', async () => {
				await filtersFragment.activeToggle.click();

				await expect(
					fdsSamplePage.activeFiltersToolbar.container
				).not.toBeVisible();
			});
		});

		await test.step('Check creator filter functionality', async () => {
			await test.step('Refresh the page', async () => {
				await fdsSamplePage.selectTab('Advanced');

				await waitForFDS({page});
			});

			await test.step('Select creator filter from dropdown and verify autocomplete input appears', async () => {
				await fdsSamplePage.managementToolbar.filterButton.click();

				await clickAndExpectToBeVisible({
					target: fdsSamplePage.creatorFilterSearchInput,
					trigger: fdsSamplePage.filterMenu.getByRole('menuitem', {
						name: 'Creator',
					}),
				});
			});

			await test.step('Search for and select a creator', async () => {
				await fdsSamplePage.creatorFilterSearchInput.fill('test');

				const firstCreatorCheckbox = fdsSamplePage.filterMenu
					.getByRole('checkbox')
					.first();

				await expect(firstCreatorCheckbox).toBeVisible();

				const creatorLabel = await firstCreatorCheckbox
					.locator('..')
					.textContent();

				await firstCreatorCheckbox.check();

				await fdsSamplePage.filterShowResultsOrAddButton.click();

				await expect(
					page.getByText('This is a description for sample').first()
				).toBeVisible();

				await expect(
					page.getByRole('button', {
						name: new RegExp(
							`Creator:.*${creatorLabel?.trim() ?? ''}`
						),
					})
				).toBeVisible();
			});

			await test.step('Verify creator names appear in table cells', async () => {
				await expect(page.getByRole('cell').first()).toBeVisible();
			});
		});

		await test.step('Check creator filter summary box operations', async () => {
			await test.step('Refresh the page', async () => {
				await fdsSamplePage.selectTab('Advanced');

				await waitForFDS({page});
			});

			await test.step('Apply creator filter', async () => {
				await fdsSamplePage.managementToolbar.filterButton.click();

				await fdsSamplePage.filterMenu
					.getByRole('menuitem', {name: 'Creator'})
					.click();

				await fdsSamplePage.creatorFilterSearchInput.fill('test');

				const firstCreatorCheckbox = fdsSamplePage.filterMenu
					.getByRole('checkbox')
					.first();

				await expect(firstCreatorCheckbox).toBeVisible();

				await firstCreatorCheckbox.check();

				await fdsSamplePage.filterShowResultsOrAddButton.click();

				await expect(
					page.getByText('This is a description for sample').first()
				).toBeVisible();
			});

			await test.step('Open creator filter summary box and verify it shows creator names', async () => {
				await expect(creatorFilterSummaryButton).toBeVisible();

				await creatorFilterSummaryButton.click();

				await expect(page.getByRole('checkbox').first()).toBeVisible();
			});

			await test.step('Remove creator filter using the remove button', async () => {
				const removeButton = page
					.getByRole('group')
					.getByLabel('Remove Filter');

				await expect(removeButton).toBeVisible({timeout: 10000});

				await removeButton.click();

				await expect(
					page.getByText('This is a description for sample').first()
				).toBeVisible();

				await expect(creatorFilterSummaryButton).not.toBeVisible();
			});
		});
	}
);

test(
	'Behavior of Date Time Range filter',
	{tag: ['@LPD-89563']},
	async ({fdsSamplePage, page}) => {
		await test.step('Open the Date Time Range filter from the dropdown', async () => {
			await fdsSamplePage.managementToolbar.filterButton.click();

			await fdsSamplePage.filterMenu
				.getByRole('menuitem', {name: 'Date Time Range'})
				.click();
		});

		await test.step('Check the date-time picker form renders with the From and To labels', async () => {
			const filterForm = page.locator('.fds-date-time-range');

			await expect(filterForm).toBeVisible();

			await expect(
				filterForm.locator('label', {hasText: 'From'})
			).toBeVisible();

			await expect(
				filterForm.locator('label', {hasText: 'To'})
			).toBeVisible();
		});

		await test.step('Check the Add Filter button is disabled while both fields are empty', async () => {
			await expect(
				fdsSamplePage.filterShowResultsOrAddButton
			).toBeDisabled();
		});
	}
);

test(
	'Selection filter state resets after removing filter chip',
	{
		tag: ['@LPD-90770'],
	},
	async ({fdsSamplePage, page}) => {
		await test.step('Remove the preloaded Color filter chip', async () => {
			await page.getByRole('button', {name: 'Remove Filter'}).click();

			await page
				.getByText('This is a description for sample 1.')
				.waitFor();
		});

		await test.step('Open filter dropdown and navigate to Color', async () => {
			await fdsSamplePage.managementToolbar.filterButton.click();

			await fdsSamplePage.filterMenu
				.getByRole('menuitem', {name: 'Color'})
				.click();
		});

		await test.step('Select only "Red" and add filter', async () => {
			await fdsSamplePage.filterDropdownMenu
				.getByRole('checkbox', {name: 'Red'})
				.check();

			await fdsSamplePage.filterShowResultsOrAddButton.click();

			await page
				.getByText('This is a description for sample')
				.first()
				.waitFor();
		});

		await test.step('Remove the Color: Red filter chip', async () => {
			await page.getByRole('button', {name: 'Remove Filter'}).click();

			await page
				.getByText('This is a description for sample 1.')
				.waitFor();
		});

		await test.step('Reopen filter dropdown — Color panel shows directly', async () => {
			await fdsSamplePage.managementToolbar.filterButton.click();
		});

		await test.step('Assert Red is no longer checked', async () => {
			await expect(
				fdsSamplePage.filterDropdownMenu.getByRole('checkbox', {
					name: 'Red',
				})
			).not.toBeChecked();
		});
	}
);

test(
	'Selection filter keeps a single scrolling area and a visible action button',
	{
		tag: ['@LPD-97504'],
	},
	async ({fdsSamplePage, page}) => {
		const openFilterMenu = page.locator(
			'.dropdown-menu.show:has(.data-set-filter)'
		);

		await test.step('Open the Title filter', async () => {
			await fdsSamplePage.managementToolbar.filterButton.click();

			await fdsSamplePage.filterMenu
				.getByRole('menuitem', {name: 'Title'})
				.click();

			await expect(
				openFilterMenu.getByRole('checkbox', {
					exact: true,
					name: 'Sample1',
				})
			).toBeVisible();
		});

		await test.step('Select every item of the first page, which used to overflow the dropdown', async () => {
			for (let index = 1; index <= SELECTION_FILTER_PAGE_SIZE; index++) {
				const checkbox = openFilterMenu.getByRole('checkbox', {
					exact: true,
					name: `Sample${index}`,
				});

				await checkbox.scrollIntoViewIfNeeded();

				await checkbox.check();
			}

			await expect(
				openFilterMenu.locator('.label-dismissible')
			).toHaveCount(SELECTION_FILTER_PAGE_SIZE);
		});

		await test.step('Check the item list is the only scrolling area', async () => {
			expect(
				await openFilterMenu.evaluate((menu: HTMLElement) =>
					[menu, ...menu.querySelectorAll<HTMLElement>('*')]
						.filter(
							(element) =>
								['auto', 'scroll'].includes(
									getComputedStyle(element).overflowY
								) &&
								element.scrollHeight > element.clientHeight + 1
						)
						.map((element) => element.className)
				)
			).toEqual(['filter-body']);
		});

		await test.step('Check the Add Filter button is visible', async () => {
			await expect(
				openFilterMenu.getByRole('button', {name: 'Add Filter'})
			).toBeInViewport();
		});
	}
);
