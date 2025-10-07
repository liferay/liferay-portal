/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {apiHelpersTest} from '../../../../../fixtures/apiHelpersTest';
import {featureFlagsTest} from '../../../../../fixtures/featureFlagsTest';
import {isolatedSiteTest} from '../../../../../fixtures/isolatedSiteTest';
import {loginTest} from '../../../../../fixtures/loginTest';
import {EFDSVisualizationMode} from '../../../../../utils/waitFor';
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

	await expect(
		page.getByText('This is a description for sample 1.')
	).toBeVisible();
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

			await expect(firstItemRow).toHaveClass(/table-active/);
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
		let sentFilters: Array<object>;
		let sentItems: Array<number>;
		let sentKeyValues: Array<number>;
		let sentSearchQuery: string;
		let sentSelectAll: boolean;

		await page.route('/o/c/fdssamples/', async (route, request) => {
			if (request.method() === 'POST') {
				const postData = request.postDataJSON();

				sentFilters = postData.filters;
				sentItems = postData.items;
				sentKeyValues = postData.keyValues;
				sentSearchQuery = postData.searchQuery;
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

			expect(sentFilters).toBeUndefined();
			expect(sentItems).toHaveLength(19);
			expect(sentKeyValues).toHaveLength(19);
			expect(sentSearchQuery).toBeUndefined();
			expect(sentSelectAll).toBe(false);

			expect(
				await fdsSamplePage.bulkActions.actionsDropdownButton.getAttribute(
					'aria-expanded'
				)
			).toBe('false');
		});

		await test.step('With Select All flag active, requests sent the flag instead of selected items', async () => {
			await test.step('Enter a search term', async () => {
				await fdsSamplePage.selectionToolbar.clearButton.click();

				await fdsSamplePage.managementToolbar.searchInput.fill(
					'Sample'
				);

				await fdsSamplePage.managementToolbar.container
					.getByRole('button', {name: 'Search'})
					.click();
			});

			await itemsSelectorCheckbox.click();

			await fdsSamplePage.selectAllCheckbox.click();

			await fdsSamplePage.bulkActions.actionsDropdownButton.click();

			await page
				.locator('.dropdown-menu.show')
				.getByRole('menuitem', {name: 'test'})
				.click();

			expect(sentFilters).toEqual([
				{
					id: 'color',
					multiple: true,
					odataFilterString: "color in ('Blue', 'Green', 'Yellow')",
					selectedItemsLabel: 'Blue, Green, Yellow',
				},
			]);
			expect(sentItems).toEqual([]);
			expect(sentKeyValues).toEqual([]);
			expect(sentSearchQuery).toEqual('Sample');
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
			await fdsSamplePage.changeVisualizationMode({
				page,
				visualizationMode: EFDSVisualizationMode.LIST,
			});
		});

		await test.step('Select the first item in the list', async () => {
			await firstListItemCheckbox.check();

			await expect(firstListItem).toHaveClass(/active/);
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

	await test.step('Can open Info Panel when using an infoPanel type item action', async () => {
		await fdsSamplePage.selectionToolbar.clearButton.click();

		await fdsSamplePage.clickItemAction('View Details');

		expect(fdsSamplePage.infoPanel).toBeInViewport();

		expect(
			fdsSamplePage.infoPanel.getByText(
				'This is a description for sample 1.'
			)
		).toBeVisible();
	});
});

test(
	'Check selection style behavior',
	{tag: '@LPD-49159'},
	async ({fdsSamplePage, page}) => {
		await test.step('Change visualization mode to List', async () => {
			await fdsSamplePage.changeVisualizationMode({
				page,
				visualizationMode: EFDSVisualizationMode.LIST,
			});

			const listItem = fdsSamplePage.list.container
				.locator('.list-group-item')
				.first();

			await listItem.click();

			await expect(listItem).toHaveClass(/active/);

			await listItem.click();

			await expect(listItem).not.toHaveClass(/active/);

			await fdsSamplePage.clickItemAction('View Details');

			await expect(listItem).toHaveClass(/active/);
		});

		await test.step('Change visualization mode to Cards', async () => {
			await fdsSamplePage.selectionToolbar.clearButton.click();

			await fdsSamplePage.changeVisualizationMode({
				page,
				visualizationMode: EFDSVisualizationMode.CARDS,
			});

			const cardItem = fdsSamplePage.cards.container
				.locator('.form-check-card')
				.first();

			await cardItem.click();

			await expect(cardItem).toHaveClass(/active/);

			await cardItem.click();

			await expect(cardItem).not.toHaveClass(/active/);

			await fdsSamplePage.clickItemAction('View Details');

			await expect(cardItem).toHaveClass(/active/);
		});

		await test.step('Change visualization mode to Table', async () => {
			await fdsSamplePage.selectionToolbar.clearButton.click();

			await fdsSamplePage.changeVisualizationMode({
				page,
				visualizationMode: EFDSVisualizationMode.TABLE,
			});

			const tableItem = fdsSamplePage.table.bodyRows.nth(0);

			await tableItem.click();

			await expect(tableItem).toHaveClass(/active/);

			await tableItem.click();

			await expect(tableItem).not.toHaveClass(/active/);

			await fdsSamplePage.clickItemAction('View Details');

			await expect(tableItem).toHaveClass(/active/);
		});
	}
);

test(
	'Check multiple and single selection behavior',
	{tag: '@LPD-49159'},
	async ({fdsSamplePage, page}) => {
		await test.step('Can select multiple items using the checkboxes', async () => {
			fdsSamplePage.selectByRowAndRole();

			await expect(
				page.getByText('1 of 75 Items Selected')
			).toBeVisible();

			fdsSamplePage.selectByRowAndRole({row: 1});

			await expect(
				page.getByText('2 of 75 Items Selected')
			).toBeVisible();
		});

		await test.step('Can select only one items when clicking in a simple table cell', async () => {
			await fdsSamplePage.selectionToolbar.clearButton.click();

			fdsSamplePage.selectByRowAndCell({
				filter: 'This is a description',
			});

			await expect(
				page.getByText('1 of 75 Items Selected')
			).toBeVisible();

			fdsSamplePage.selectByRowAndCell({
				filter: 'This is a description',
				row: 3,
			});

			await expect(
				page.getByText('1 of 75 Items Selected')
			).toBeVisible();
		});

		await test.step('Can deselect an item when clicking in a simple table cell', async () => {
			await fdsSamplePage.selectionToolbar.clearButton.click();

			fdsSamplePage.selectByRowAndCell({
				filter: 'This is a description',
				row: 0,
			});

			await expect(
				page.getByText('1 of 75 Items Selected')
			).toBeVisible();

			fdsSamplePage.selectByRowAndCell({
				filter: 'This is a description',
				row: 0,
			});

			await expect(
				page.getByText('1 of 75 Items Selected')
			).not.toBeVisible();
		});

		await test.step('Can not select when clicking in a table cell with custom cell renderer', async () => {
			fdsSamplePage.selectByRowAndRole({role: 'link'});

			await expect(
				page.getByText('1 of 75 Items Selected')
			).not.toBeVisible();
		});
	}
);

test('Pagination and items per page', async ({fdsSamplePage, page}) => {
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
		await fdsSamplePage.selectionToolbar.clearButton.click();

		await expect(itemsSelectorCheckbox).not.toBeChecked();

		await expect(
			page.getByText('15 of 75 Items Selected')
		).not.toBeVisible();
	});
});
