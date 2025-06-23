/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Locator, expect, mergeTests} from '@playwright/test';

import {accountSettingsPagesTest} from '../../../fixtures/accountSettingsPagesTest';
import {apiHelpersTest} from '../../../fixtures/apiHelpersTest';
import {featureFlagsTest} from '../../../fixtures/featureFlagsTest';
import {isolatedSiteTest} from '../../../fixtures/isolatedSiteTest';
import {loginTest} from '../../../fixtures/loginTest';
import {clickAndExpectToBeVisible} from '../../../utils/clickAndExpectToBeVisible';
import getRandomString from '../../../utils/getRandomString';
import {waitForAlert} from '../../../utils/waitForAlert';
import {fdsSamplePageTest} from './fixtures/fdsSamplePageTest';

const test = mergeTests(
	apiHelpersTest,
	fdsSamplePageTest,
	featureFlagsTest({
		'LPD-41774': {enabled: true},
		'LPS-178052': {enabled: true},
	}),
	isolatedSiteTest,
	loginTest()
);

let fdsSamplePageURL: string;

test.beforeEach(async ({fdsSamplePage, page, site}) => {
	const {url} = await fdsSamplePage.setupFDSSampleWidget({site});

	fdsSamplePageURL = url;

	await fdsSamplePage.selectTab('Advanced');

	await expect(
		page.getByText('This is a description for sample 1.')
	).toBeVisible();
});

test(
	'Check behavior of filters',
	{
		tag: ['@LPS-150047'],
	},
	async ({fdsSamplePage, page}) => {
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
				const blueCells = page.getByRole('cell', {name: 'Blue'});
				const greenCells = page.getByRole('cell', {name: '🍏'});
				const redCells = page.getByRole('cell', {name: 'Red'});
				const yellowCells = page.getByRole('cell', {name: 'Yellow'});

				expect.soft(await blueCells.count()).toBeGreaterThan(0);
				expect.soft(await greenCells.count()).toBeGreaterThan(0);
				expect.soft(await redCells.count()).toEqual(0);
				expect.soft(await yellowCells.count()).toBeGreaterThan(0);
			});
		});

		await test.step('Check reset filters button', async () => {
			await test.step('Check the total amount of items is 75', async () => {
				expect
					.soft(page.getByText('Showing 1 to 20 of 75 entries.'))
					.toBeVisible();
			});

			await test.step('Click on reset filters button', async () => {
				await page.getByRole('button', {name: 'Reset Filters'}).click();

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
				await fdsSamplePage.managementToolbar
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
				await fdsSamplePage.managementToolbar
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
				await fdsSamplePage.managementToolbar
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
					.getByRole('button', {name: 'Edit Filter'})
					.click();

				await page
					.getByText('This is a description for sample 10.')
					.waitFor();
			});

			await test.step('Check the results are filtered by checking all results appear', async () => {
				const blueCells = page.getByRole('cell', {name: 'Blue'});
				const greenCells = page.getByRole('cell', {name: '🍏'});
				const redCells = page.getByRole('cell', {name: 'Red'});
				const yellowCells = page.getByRole('cell', {name: 'Yellow'});

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
				await fdsSamplePage.managementToolbar
					.getByRole('button', {name: 'Filter'})
					.click();

				await page.getByRole('menuitem', {name: 'Color'}).click();

				await page.getByLabel('Exclude').check();
			});

			await test.step('Click "Edit Filter"', async () => {
				await page.getByRole('button', {name: 'Edit Filter'}).click();

				await page
					.getByText('This is a description for sample 10.')
					.waitFor();
			});

			await test.step('Check the only Red results are displayed', async () => {
				const blueCells = page.getByRole('cell', {name: 'Blue'});
				const greenCells = page.getByRole('cell', {name: '🍏'});
				const redCells = page.getByRole('cell', {name: 'Red'});
				const yellowCells = page.getByRole('cell', {name: 'Yellow'});

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

				await page.getByRole('button', {name: 'Edit Filter'}).click();

				await page
					.getByText('This is a description for sample 1.')
					.waitFor();
			});

			await test.step('Check the results only show "Green", "Yellow", and "Red"', async () => {
				const blueCells = page.getByRole('cell', {name: 'Blue'});
				const greenCells = page.getByRole('cell', {name: '🍏'});
				const redCells = page.getByRole('cell', {name: 'Red'});
				const yellowCells = page.getByRole('cell', {name: 'Yellow'});

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
				const blueCells = page.getByRole('cell', {name: 'Blue'});
				const greenCells = page.getByRole('cell', {name: '🍏'});
				const yellowCells = page.getByRole('cell', {name: 'Yellow'});
				const redCells = page.getByRole('cell', {name: 'Red'});

				expect.soft(await blueCells.count()).toBeGreaterThan(0);
				expect.soft(await greenCells.count()).toBeGreaterThan(0);
				expect.soft(await yellowCells.count()).toBeGreaterThan(0);
				expect.soft(await redCells.count()).toBeGreaterThan(0);
			});
		});
	}
);

test(
	'Check behavior of custom views',
	{
		tag: ['@LPS-130101'],
	},
	async ({fdsSamplePage, page}) => {
		let actionsDropdown: Locator;
		let customViewsDropdown: Locator;
		let columnsVisibilityDropdown: Locator;

		const customView1Name = getRandomString();
		const customView2Name = getRandomString();

		await test.step('Get dropdown references', async () => {

			// Click on dropdown toggle button adds the aria-controls attribute

			await fdsSamplePage.customViewsActionsButton.click();

			const actionsDropdownId =
				await fdsSamplePage.customViewsActionsButton.getAttribute(
					'aria-controls'
				);

			actionsDropdown = page.locator(`#${actionsDropdownId}`);

			page.keyboard.press('Escape');

			await fdsSamplePage.customViewsSelectorButton.click();

			const customViewsDropdownId =
				await fdsSamplePage.customViewsSelectorButton.getAttribute(
					'aria-controls'
				);

			customViewsDropdown = page.locator(`#${customViewsDropdownId}`);

			page.keyboard.press('Escape');

			await fdsSamplePage.table.manageColumnsVisibilityButton.click();

			const columnsVisibilityDropdownId =
				await fdsSamplePage.table.manageColumnsVisibilityButton.getAttribute(
					'aria-controls'
				);

			columnsVisibilityDropdown = page.locator(
				`#${columnsVisibilityDropdownId}`
			);

			page.keyboard.press('Escape');
		});

		await test.step('Create a custom views and set it as the default one', async () => {
			await fdsSamplePage.customViewsActionsButton.click();

			await actionsDropdown
				.filter({has: page.getByRole('menu')})
				.waitFor();

			const menuItem = actionsDropdown.getByRole('menuitem', {
				name: 'Save View As...',
			});

			await expect(menuItem).toBeVisible();

			await menuItem.click();

			await expect(fdsSamplePage.customViewsSaveModal).toBeInViewport();

			await fdsSamplePage.customViewsSaveModal
				.getByLabel('NameRequired')
				.fill(customView1Name);

			await fdsSamplePage.customViewsSaveModal
				.getByRole('button', {name: 'Save'})
				.click();

			await fdsSamplePage.customViewsActionsButton.click();

			await actionsDropdown
				.filter({has: page.getByRole('menu')})
				.waitFor();

			await actionsDropdown
				.getByRole('menuitem', {name: 'Save View As...'})
				.click();

			await expect(fdsSamplePage.customViewsSaveModal).toBeInViewport();

			await fdsSamplePage.customViewsSaveModal
				.getByLabel('NameRequired')
				.fill(customView2Name);
			await fdsSamplePage.customViewsSaveModal
				.getByRole('button', {name: 'Save'})
				.click();

			await expect(fdsSamplePage.customViewsSelectorButton).toHaveText(
				customView2Name
			);

			await fdsSamplePage.customViewsSelectorButton.click();

			await expect(customViewsDropdown.getByRole('option')).toHaveCount(
				3
			);
		});

		await test.step('Edit custom view, by changing visibility of one column', async () => {
			await expect(fdsSamplePage.table.headerCells).toHaveCount(10);

			await fdsSamplePage.table.manageColumnsVisibilityButton.click();

			await columnsVisibilityDropdown
				.getByRole('menuitem', {name: 'Description'})
				.click();

			page.keyboard.press('Escape');

			await expect(fdsSamplePage.table.headerCells).toHaveCount(9);
		});

		await test.step('Confirm that changes in a custom view does not affect Default View', async () => {
			await expect(fdsSamplePage.customViewsSelectorButton).toHaveText(
				customView2Name
			);

			await expect(fdsSamplePage.table.headerCells).toHaveCount(9);

			await fdsSamplePage.customViewsSelectorButton.click();

			await customViewsDropdown.waitFor();

			await customViewsDropdown
				.getByRole('option', {name: 'Default View'})
				.click();

			await expect(fdsSamplePage.table.headerCells).toHaveCount(10);
		});

		await test.step('Can change a custom view name', async () => {
			await fdsSamplePage.customViewsSelectorButton.click();

			await customViewsDropdown.waitFor();

			await customViewsDropdown
				.getByRole('option', {name: customView2Name})
				.click();

			await fdsSamplePage.customViewsActionsButton.click();

			await actionsDropdown.waitFor();

			const menuItem = actionsDropdown.getByRole('menuitem', {
				name: 'Rename View',
			});

			await expect(menuItem).toBeVisible();

			await menuItem.click();

			await expect(fdsSamplePage.customViewsSaveModal).toBeInViewport();

			const newCustomViewName = getRandomString();

			await fdsSamplePage.customViewsSaveModal
				.getByLabel('NameRequired')
				.fill(newCustomViewName);

			await fdsSamplePage.customViewsSaveModal
				.getByRole('button', {name: 'Save'})
				.click();

			await expect(fdsSamplePage.customViewsSelectorButton).toHaveText(
				newCustomViewName
			);
		});

		await test.step('Delete a custom view', async () => {
			await fdsSamplePage.customViewsSelectorButton.click();

			await customViewsDropdown.waitFor();

			await customViewsDropdown
				.getByRole('option', {name: customView1Name})
				.click();

			await fdsSamplePage.customViewsActionsButton.click();

			await actionsDropdown.waitFor();

			const menuItem = actionsDropdown.getByRole('menuitem', {
				name: 'Delete View',
			});

			await expect(menuItem).toBeVisible();

			await menuItem.click();

			await expect(fdsSamplePage.customViewsDeleteAlert).toBeVisible();

			await fdsSamplePage.customViewsDeleteAlert
				.getByRole('button', {name: 'Delete'})
				.click();

			await fdsSamplePage.customViewsSelectorButton.click();

			await customViewsDropdown.waitFor();

			await expect(
				customViewsDropdown.getByRole('option', {name: customView1Name})
			).not.toBeVisible();
		});
	}
);

test('Check behavior of item actions', async ({fdsSamplePage, page}) => {
	const asyncConnectionRefused = 'Async Connection Refused';
	const asyncResourceNotFound = 'Async Resource Not Found';
	const asyncSuccess = 'Async Success';
	const sampleView = 'Sample View';
	const sidePanelActionLabelWithActionTitle = 'Side Panel With Action Title';
	const sidePanelActionLabelWithContentTitle =
		'Side Panel With Content Title';
	const sidePanelActionLabelWithActionTitleContentTitle =
		'Side Panel With Action and Content Title';
	const sidePanelActionLabelWithoutTitle = 'Side Panel With No Title';
	const sidePanelActionTitle = 'Side Panel Title Provided by Action';
	const sidePanelContentTitle = 'Side Panel Title Provided by Page';

	const itemActionsCell = fdsSamplePage.table.itemActionsCells.first();

	const itemActionButton = itemActionsCell.getByRole('button', {
		exact: true,
		name: 'Actions',
	});

	await test.step('Check that the Item Actions dropdown is present in table row', async () => {
		await expect(itemActionButton).toBeVisible();

		const dropdownId = await itemActionButton.getAttribute('aria-controls');

		await itemActionButton.click();

		await page
			.locator(`#${dropdownId}`)
			.filter({has: page.getByRole('menu')})
			.waitFor();

		await expect(
			page.locator(`#${dropdownId}`).getByRole('menuitem')
		).toHaveCount(14);

		await page.keyboard.press('Escape');
	});

	await test.step('Side Panel action opens a side panel with content title', async () => {
		await fdsSamplePage.clickItemAction(
			sidePanelActionLabelWithContentTitle
		);

		await expect(fdsSamplePage.sidePanel).toBeInViewport();

		const frame = fdsSamplePage.sidePanelFrame;

		await frame.getByText(sidePanelContentTitle).waitFor();

		await expect(frame.getByText(sidePanelContentTitle)).toHaveCount(1);

		await expect(
			frame.getByText('This is a side panel with a title.')
		).toBeVisible();

		await page.keyboard.press('Escape');

		await expect(fdsSamplePage.sidePanel).toHaveClass(/is-hidden/);
	});

	await test.step('Side Panel action opens a side panel with action title', async () => {
		await fdsSamplePage.clickItemAction(
			sidePanelActionLabelWithActionTitle
		);

		await expect(fdsSamplePage.sidePanel).toBeInViewport();

		await page.getByText(sidePanelActionTitle).waitFor();

		await expect(page.getByText(sidePanelActionTitle)).toHaveCount(1);

		const frame = fdsSamplePage.sidePanelFrame;

		await expect(
			frame.locator('.side-panel-iframe-header')
		).not.toBeInViewport();

		await expect(
			frame.getByText('This is a side panel without a title.')
		).toBeVisible();

		await page.keyboard.press('Escape');

		await expect(fdsSamplePage.sidePanel).toHaveClass(/is-hidden/);
	});

	await test.step('Side Panel action opens a side panel with duplicated title', async () => {
		await fdsSamplePage.clickItemAction(
			sidePanelActionLabelWithActionTitleContentTitle
		);

		await expect(fdsSamplePage.sidePanel).toBeInViewport();

		await page.getByText(sidePanelActionTitle).waitFor();

		await expect(page.getByText(sidePanelActionTitle)).toHaveCount(1);

		const frame = fdsSamplePage.sidePanelFrame;

		await expect(
			frame.locator('.side-panel-iframe-header')
		).toBeInViewport();
		await frame.getByText(sidePanelContentTitle).waitFor();

		await expect(frame.getByText(sidePanelContentTitle)).toHaveCount(1);

		await page.keyboard.press('Escape');

		await expect(fdsSamplePage.sidePanel).toHaveClass(/is-hidden/);
	});

	await test.step('Side Panel action opens a side panel without title', async () => {
		await fdsSamplePage.clickItemAction(sidePanelActionLabelWithoutTitle);

		await expect(fdsSamplePage.sidePanel).toBeInViewport();

		await expect(page.locator('.fds-side-panel-title')).toBeInViewport();
		const panelTitle = await page
			.locator('.fds-side-panel-title')
			.allInnerTexts();

		expect(panelTitle).toEqual(['']);

		const frame = fdsSamplePage.sidePanelFrame;

		await expect(
			frame.locator('.side-panel-iframe-header')
		).not.toBeInViewport();

		await expect(
			frame.getByText('This is a side panel without a title.')
		).toBeVisible();

		await page.keyboard.press('Escape');

		await expect(fdsSamplePage.sidePanel).toHaveClass(/is-hidden/);
	});

	await test.step('Sample view action opens an alert message', async () => {
		await fdsSamplePage.clickItemAction(sampleView);

		page.on('dialog', async (dialog) => {
			await expect(dialog.message).toBe('Hello Sample1!');
		});
	});

	await test.step('Async connection refused action opens an unexpected error alert toast', async () => {
		await fdsSamplePage.clickItemAction(asyncConnectionRefused);

		await waitForAlert(page, 'Error:An unexpected error occurred.', {
			type: 'danger',
		});
	});

	await test.step('Async resource not found action opens an unexpected error alert toast', async () => {
		await fdsSamplePage.clickItemAction(asyncResourceNotFound);

		await waitForAlert(page, 'Error:An unexpected error occurred.', {
			type: 'danger',
		});
	});

	await test.step('Async success action opens a success alert toast', async () => {
		await fdsSamplePage.clickItemAction(asyncSuccess);

		await waitForAlert(page);
	});
});

test('Pagination and items per page', async ({page}) => {
	const itemsSelectorCheckbox = page.locator('input[name="items-selector"]');

	await test.step('Change delta to 60 items', async () => {
		await page.getByLabel('Items Per Page').click();

		await page.getByRole('option', {name: '60 Items'}).click();

		await page
			.getByText('This is a description for sample')
			.first()
			.waitFor();

		await expect(
			page.getByText('Showing 1 to 60 of 75 entries.')
		).toBeVisible();
	});

	await test.step('Select all items in current page using the bulk actions checkbox', async () => {
		await itemsSelectorCheckbox.setChecked(true);

		await expect(page.getByText('60 of 75 Items Selected')).toBeVisible();
	});

	await test.step('Select all items', async () => {
		await page.getByLabel('Go to page, 2').click();

		await page
			.getByText('This is a description for sample')
			.first()
			.waitFor();

		for (let i = 1; i <= 15; i++) {
			await page
				.locator(
					`tbody tr:nth-child(${i}) > .cell-select-item input[type="checkbox"]`
				)
				.setChecked(true);
		}

		await expect(
			page.getByText('All Selected (75 of 75 Items)')
		).toBeVisible();
	});

	await test.step('Check that selection are preserved through page navigation', async () => {
		await page.getByLabel('Go to page, 1').click();

		await page
			.getByText('This is a description for sample')
			.first()
			.waitFor();

		await expect(
			page.getByText('All Selected (75 of 75 Items)')
		).toBeVisible();
	});

	await test.step('Unselect all items in current page using the bulk actions checkbox', async () => {
		await itemsSelectorCheckbox.setChecked(false);

		await expect(itemsSelectorCheckbox).not.toBeChecked();

		await expect(page.getByText('15 of 75 Items Selected')).toBeVisible();
	});

	await test.step('Unselect all items using clear button', async () => {
		await page.getByText('Clear').click();

		await expect(itemsSelectorCheckbox).not.toBeChecked();

		await expect(
			page.getByText('15 of 75 Items Selected')
		).not.toBeVisible();
	});
});

test(
	'Sort columns and assert visibility',
	{tag: '@LPS-193005'},
	async ({page}) => {
		await test.step('Sorting ID and Title column in ascending order', async () => {
			const idColumnHeader = page
				.getByRole('columnheader')
				.getByText('ID');

			await expect(idColumnHeader).toBeInViewport();

			await Promise.all([
				idColumnHeader.click(),
				page.waitForResponse(
					(response: any) => response.status() === 200
				),
			]);

			let cells = await page.locator('td').allInnerTexts();

			await expect(page.locator('td').nth(1)).toHaveText(cells[1]);
			await expect(page.locator('td').nth(11)).toHaveText(cells[11]);
			await expect(page.locator('td').nth(21)).toHaveText(cells[21]);
			await expect(page.locator('td').nth(31)).toHaveText(cells[31]);

			const ascendingIDCells = [
				cells[1],
				cells[11],
				cells[21],
				cells[31],
				cells[41],
				cells[51],
				cells[61],
				cells[71],
				cells[81],
				cells[91],
			].sort();

			await expect(page.locator('td').nth(1)).toHaveText(
				ascendingIDCells[0]
			);
			await expect(page.locator('td').nth(11)).toHaveText(
				ascendingIDCells[1]
			);
			await expect(page.locator('td').nth(21)).toHaveText(
				ascendingIDCells[2]
			);
			await expect(page.locator('td').nth(31)).toHaveText(
				ascendingIDCells[3]
			);

			const titleColumnHeader = page
				.getByRole('columnheader')
				.getByText('Title');

			await Promise.all([
				titleColumnHeader.click(),
				page.waitForResponse(
					(response: any) => response.status() === 200
				),
			]);

			cells = await page.locator('td').allInnerTexts();

			const ascendingTitleCells = [
				cells[2],
				cells[12],
				cells[22],
				cells[32],
				cells[42],
				cells[52],
				cells[62],
				cells[72],
				cells[82],
				cells[92],
			].sort((a, b) => new Intl.Collator('en').compare(a, b));

			await expect(page.locator('td').nth(2)).toHaveText(
				ascendingTitleCells[0]
			);
			await expect(page.locator('td').nth(12)).toHaveText(
				ascendingTitleCells[1]
			);
			await expect(page.locator('td').nth(22)).toHaveText(
				ascendingTitleCells[2]
			);
			await expect(page.locator('td').nth(32)).toHaveText(
				ascendingTitleCells[3]
			);
		});

		await test.step('Hide the Title column', async () => {
			const titleColumnHeader = page
				.getByRole('columnheader')
				.getByText('Title');

			await expect(titleColumnHeader).toBeAttached();

			const button = page.getByLabel('Manage Columns Visibility');

			await expect(button).toBeAttached();

			await button.click();

			const titleMenuItem = page.getByRole('menuitem').nth(1);

			await titleMenuItem.click();

			await expect(
				page.getByRole('columnheader').getByText('Title')
			).toBeHidden();
		});
	}
);

test('Resize columns', {tag: '@LPD-54497'}, async ({fdsSamplePage, page}) => {
	const firstColumnHeader = fdsSamplePage.table.firstColumnHeader;
	let initialWidth: number;

	await test.step('Get the initial width of a column', async () => {
		initialWidth = await firstColumnHeader.evaluate(
			(element) => element.getBoundingClientRect().width
		);

		await expect(initialWidth).toBeGreaterThan(0);
	});

	await test.step('Drag resizer element to make column wider', async () => {
		const resizer = firstColumnHeader.locator('.dnd-th-resizer');
		const resizerBoundingBox = await resizer.boundingBox();

		await page.mouse.move(resizerBoundingBox.x, resizerBoundingBox.y);
		await page.mouse.down();
		await page.mouse.move(resizerBoundingBox.x + 50, resizerBoundingBox.y);
		await page.mouse.up();
	});

	await test.step('Check that final widht is greater than initial one', async () => {
		const finalWidth = await firstColumnHeader.evaluate(
			(element) => element.getBoundingClientRect().width
		);

		await expect(finalWidth).toBeGreaterThan(initialWidth);
	});
});

test(
	'Hide column and assert correct visibility of columns',
	{tag: '@LPD-45051'},
	async ({page}) => {
		const initialBodyCellText = await page.locator('td').nth(1).innerText();

		const rowAction = page.locator('td .component-action').first();

		await test.step('Check that row actions are present', async () => {
			await expect(rowAction).toBeAttached();
		});

		await test.step('Hide the first column', async () => {
			const button = page.getByLabel('Manage Columns Visibility');

			await expect(button).toBeAttached();

			await button.click();

			const menuItem = page.getByRole('menuitem').nth(0);

			await menuItem.click();
		});

		await test.step('Check that the first column is hidden and the row actions are still present', async () => {
			await expect(page.locator('td').nth(1)).not.toHaveText(
				initialBodyCellText
			);

			await expect(rowAction).toBeAttached();
		});
	}
);

test('Use client extensions', async ({fdsSamplePage, page}) => {
	await test.step('Assert that the cell renderer is invoked and the apple emoji is visible', async () => {
		const firstColorCell = fdsSamplePage.table.container
			.locator('td.cell-color')
			.first();

		await expect(firstColorCell).toContainText('🍏');
	});

	await test.step('Assert that the cell renderer has access to data from other cells', async () => {
		const firstColorCell = fdsSamplePage.table.container
			.locator('td.cell-color')
			.nth(1);

		await expect(firstColorCell).toContainText('Sample100 is Blue');
	});

	await test.step('Assert that the filter client extension is working', async () => {
		const clientExtensionMenuItem = page.getByRole('menuitem', {
			name: 'Client Extension',
		});

		const filterButton = page
			.locator('.filters-dropdown')
			.getByText('Filter');

		await expect(filterButton).toBeInViewport();

		await clickAndExpectToBeVisible({
			autoClick: true,
			target: clientExtensionMenuItem,
			trigger: filterButton,
		});

		const filterInput = page.getByPlaceholder('Search with Odata');

		await expect(filterInput).toBeInViewport();

		await filterInput.fill("title eq 'Sample97'");

		await expect(filterInput).toHaveValue("title eq 'Sample97'");

		const submitButton = page.getByRole('button', {name: 'Submit'});

		await expect(submitButton).toBeInViewport();

		await submitButton.click();

		await expect(page.getByText('Sample97', {exact: true})).toBeVisible();

		const bodyRows = fdsSamplePage.table.container.locator('tbody tr');

		expect(await bodyRows.count()).toEqual(1);
	});
});

test(
	'Item selection and bulk actions',
	{tag: ['@LPD-52063', '@LPD-41774']},
	async ({fdsSamplePage, page}) => {
		const firstItemRow = fdsSamplePage.table.bodyRows.first();

		const firstItemCheckbox = firstItemRow
			.locator('.cell-select-item')
			.getByRole('checkbox');

		await test.step('Select the first item in the table', async () => {
			await firstItemCheckbox.check();

			await expect(firstItemRow).toHaveClass('table-active');
		});

		await test.step('Check the highlighted bulk action "Label" is visible', async () => {
			await expect(
				fdsSamplePage.bulkActions.container.getByRole('button', {
					name: 'Label',
				})
			).toHaveText('Label');
		});

		await test.step('Open ellipsis actions menu', async () => {
			await fdsSamplePage.bulkActions.actionsDropdownButton.click();
		});

		await test.step('Check the bulk actions are listed', async () => {
			await expect(
				page.locator('.dropdown-menu.show').getByRole('menuitem')
			).toHaveCount(3);
			await expect(
				page.locator('.dropdown-menu.show').getByRole('menuitem')
			).toHaveText(['Label', 'Delete', 'Test']);
		});

		await test.step('Close ellipsis actions menu', async () => {
			await fdsSamplePage.bulkActions.actionsDropdownButton.click();

			await expect(page.locator('.dropdown-menu.show')).toBeHidden();
		});

		await test.step('Check in medium-width windows the bulk actions text is hidden', async () => {
			await page.setViewportSize({height: 1024, width: 800});

			const visibleLabelButton = fdsSamplePage.bulkActions.container
				.locator('button')
				.filter({
					hasText: 'Label',
				});

			await expect(visibleLabelButton).toBeVisible();

			await expect(
				fdsSamplePage.bulkActions.container.getByLabel('Label')
			).not.toBeVisible();
		});

		await test.step('Check in small-width windows the text and icon are hidden', async () => {
			await page.setViewportSize({height: 720, width: 360});

			await expect(
				fdsSamplePage.bulkActions.container.getByRole('button', {
					name: 'Label',
				})
			).toBeHidden();
		});

		await test.step('Reset the window size', async () => {
			await page.setViewportSize({height: 720, width: 1280});
		});

		await test.step('Deselect the item to reset to original state', async () => {
			await firstItemCheckbox.uncheck();
		});

		const itemsSelectorCheckbox = page.locator(
			'input[name="items-selector"]'
		);
		let sentItems: Array<number>;
		let sentKeyValues: Array<number>;
		let sentSelectAll: boolean;

		await page.route('/o/c/fdssamples/', async (route, request) => {
			if (request.method() === 'POST') {
				const postData = request.postDataJSON();

				sentItems = postData.items;
				sentKeyValues = postData.keyValues;
				sentSelectAll = postData.selectAll;
			}

			await route.continue();
		});

		await test.step('Check Select All buton selects all elements', async () => {
			await itemsSelectorCheckbox.click();

			await expect(
				page.getByText('20 of 75 Items Selected')
			).toBeVisible();

			await fdsSamplePage.selectAllCheckbox.click();

			await expect(
				page.getByText('All Selected (75 of 75 Items)')
			).toBeVisible();
		});

		await test.step('Deselect an element disables Select All flag', async () => {
			await fdsSamplePage.table.container
				.locator('tbody .cell-select-item')
				.first()
				.getByRole('checkbox')
				.uncheck();

			await expect(
				page.getByText('19 of 75 Items Selected')
			).toBeVisible();

			await expect(fdsSamplePage.selectAllCheckbox).not.toBeVisible();
		});

		await test.step('Without Select All flag active, requests sent actual item selection to bulk actions', async () => {
			await fdsSamplePage.bulkActions.actionsDropdownButton.click();

			await page
				.locator('.dropdown-menu.show')
				.getByRole('menuitem', {name: 'test'})
				.click();

			expect(sentItems).toHaveLength(19);
			expect(sentKeyValues).toHaveLength(19);
			expect(sentSelectAll).toBe(false);

			expect(
				await fdsSamplePage.bulkActions.actionsDropdownButton.getAttribute(
					'aria-expanded'
				)
			).toBe('false');
		});

		await test.step('With Select All flag active, requests sent the flag instead of selected items', async () => {
			await itemsSelectorCheckbox.click();

			await fdsSamplePage.selectAllCheckbox.click();

			await fdsSamplePage.bulkActions.actionsDropdownButton.click();

			await page
				.locator('.dropdown-menu.show')
				.getByRole('menuitem', {name: 'test'})
				.click();

			expect(sentItems).toEqual([]);
			expect(sentKeyValues).toEqual([]);
			expect(sentSelectAll).toBe(true);

			expect(
				await fdsSamplePage.bulkActions.actionsDropdownButton.getAttribute(
					'aria-expanded'
				)
			).toBe('false');

			await fdsSamplePage.selectionToolbar.clearButton.click();
		});

		const firstListItem = fdsSamplePage.list.items.first();

		const firstListItemCheckbox = firstListItem.getByRole('checkbox');

		await test.step('Open list visualization mode', async () => {
			await fdsSamplePage.changeVisualizationMode('List');
		});

		await test.step('Select the first item in the list', async () => {
			await firstListItemCheckbox.check();

			await expect(firstListItem).toHaveClass(/active/);
		});
	}
);

test(
	'Check behavior of quick actions',
	{tag: '@LPS-153220'},
	async ({fdsSamplePage, page}) => {
		const firstRowItemActionButton = fdsSamplePage.table.itemActionsCells
			.first()
			.getByRole('button', {
				exact: true,
				name: 'Actions',
			});

		const thirdRowItemActionButton = fdsSamplePage.table.itemActionsCells
			.nth(2)
			.getByRole('button', {
				exact: true,
				name: 'Actions',
			});

		const firstRowSampleEditQuickActionLink = fdsSamplePage.table.bodyRows
			.first()
			.getByLabel('Sample Edit');

		const firstTableHeadCell = fdsSamplePage.table.headerCells.first();

		await test.step('Assert that "#test-pencil" is appended to browser URL after clicking', async () => {
			await firstTableHeadCell.hover();

			await firstTableHeadCell.click();

			await firstRowItemActionButton.hover();

			await firstRowSampleEditQuickActionLink.click();

			expect(page.url()).toContain('#test-pencil');
		});

		await test.step('Assert that clicking quick action is equivalent to clicking the ellipsis dropdown menu', async () => {
			await firstRowItemActionButton.hover();

			await firstRowSampleEditQuickActionLink.click();

			const pageURLAfterQuickAction = page.url();

			expect(pageURLAfterQuickAction).toContain('#test-pencil');

			await firstRowItemActionButton.click();

			await page
				.getByRole('menuitem', {
					name: 'Sample Edit',
				})
				.click();

			expect(page.url()).toEqual(pageURLAfterQuickAction);
		});

		await test.step('Assert that hover over mouse off of the table body quick action menu is not visible', async () => {
			await firstRowItemActionButton.hover();

			await expect(firstRowSampleEditQuickActionLink).toBeVisible();

			await firstTableHeadCell.hover();

			await expect(firstRowSampleEditQuickActionLink).not.toBeVisible();
		});

		await test.step('When hovering over the first line item and the quick action menu is displayed on the 1st line', async () => {
			const firstTableRow = fdsSamplePage.table.bodyRows.first();

			await firstTableRow.hover();

			await expect(firstRowSampleEditQuickActionLink).toBeVisible();
		});

		await test.step('When clicking on the ellipsis and hovering over another row, multiple quick action menus are displayed', async () => {
			await thirdRowItemActionButton.click();

			await expect(page.locator('.dropdown-menu.show')).toBeVisible();

			await fdsSamplePage.table.bodyRows.first().hover();

			await expect(firstRowSampleEditQuickActionLink).toBeVisible();

			await firstTableHeadCell.click(); // Close dropdown
		});

		await test.step('Assert quick action can be displayed on only one active row', async () => {
			await firstRowItemActionButton.hover();

			await expect(firstRowSampleEditQuickActionLink).toBeVisible();

			await thirdRowItemActionButton.hover();

			await thirdRowItemActionButton.click();

			await expect(firstRowSampleEditQuickActionLink).not.toBeVisible();

			await firstTableHeadCell.click(); // Close dropdown
		});

		await test.step('Assert that quick action icons list should be limited to three actions', async () => {
			await firstRowItemActionButton.hover();

			await expect(
				fdsSamplePage.table.bodyRows.first().getByLabel('View Details')
			).toBeVisible();

			await expect(
				fdsSamplePage.table.bodyRows.first().getByLabel('Sample View')
			).toBeVisible();

			await expect(
				fdsSamplePage.table.bodyRows.first().getByLabel('Sample Edit')
			).toBeVisible();

			await expect(
				fdsSamplePage.table.bodyRows.first().getByLabel('Sample Copy')
			).not.toBeVisible();
		});

		await test.step('Assert the quick action is not visible when the row checkbox is checked', async () => {
			await fdsSamplePage.table.bodyRows
				.first()
				.getByRole('checkbox')
				.click();

			await firstRowItemActionButton.hover();

			await expect(firstRowSampleEditQuickActionLink).not.toBeVisible();
		});
	}
);

test('InfoPanel behavior', async ({fdsSamplePage, page}) => {
	const firstItemCheckbox = fdsSamplePage.table.container
		.locator('tbody .cell-select-item')
		.first()
		.getByRole('checkbox');

	const secondItemCheckbox = fdsSamplePage.table.container
		.locator('tbody .cell-select-item')
		.nth(1)
		.getByRole('checkbox');

	await test.step('Can open Info Panel when no item is selected', async () => {
		expect(fdsSamplePage.toggleInfoPanelButton).toBeVisible();

		await fdsSamplePage.toggleInfoPanelButton.click();

		expect(fdsSamplePage.infoPanel).toBeInViewport();

		expect(
			page.getByText('Content from propsTransformer: No items selected')
		).toBeVisible();

		await fdsSamplePage.toggleInfoPanelButton.click();

		expect(fdsSamplePage.infoPanel).not.toBeInViewport();
	});

	await test.step('Can open Info Panel when there is one item selected', async () => {
		await firstItemCheckbox.check();

		expect(fdsSamplePage.toggleInfoPanelButton).toBeVisible();

		await fdsSamplePage.toggleInfoPanelButton.click();

		expect(fdsSamplePage.infoPanel).toBeInViewport();

		expect(
			fdsSamplePage.infoPanel.getByText(
				'This is a description for sample 1.'
			)
		).toBeVisible();

		await fdsSamplePage.toggleInfoPanelButton.click();

		expect(fdsSamplePage.infoPanel).not.toBeInViewport();
	});

	await test.step('Can open Info Panel when there are more than one items selected', async () => {
		await firstItemCheckbox.check();

		await secondItemCheckbox.check();

		await fdsSamplePage.toggleInfoPanelButton.click();

		expect(fdsSamplePage.infoPanel).toBeInViewport();

		expect(
			fdsSamplePage.infoPanel.getByText(
				'Content from propsTransformer. Items selected: 2'
			)
		).toBeVisible();

		await fdsSamplePage.toggleInfoPanelButton.click();

		expect(fdsSamplePage.infoPanel).not.toBeInViewport();
	});
});

const accountSettingsTest = mergeTests(test, accountSettingsPagesTest);

accountSettingsTest(
	'Set time zone from theme display in a datetime renderer',
	{
		tag: ['@LPD-37756'],
	},
	async ({accountSettingsPage, fdsSamplePage, page}) => {
		await test.step('Check date in UTC time zone', async () => {
			await accountSettingsPage.goToDisplaySettings();

			await accountSettingsPage.setTimeZone('UTC');

			await page.goto(fdsSamplePageURL);

			await fdsSamplePage.selectTab('Advanced');

			await expect(
				page.getByText('Jan 1, 2020, 12:00:00 AM')
			).toBeVisible();
		});

		await test.step('Check date in a different time zone', async () => {
			await accountSettingsPage.goToDisplaySettings();

			await accountSettingsPage.setTimeZone('Europe/Paris');

			await page.goto(fdsSamplePageURL);

			await fdsSamplePage.selectTab('Advanced');

			await expect(
				page.getByText('Jan 1, 2020, 1:00:00 AM')
			).toBeVisible();
		});

		await test.step('Revert to default UTC time zone', async () => {
			await accountSettingsPage.goToDisplaySettings();

			await accountSettingsPage.setTimeZone('UTC');
		});
	}
);
