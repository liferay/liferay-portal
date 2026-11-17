/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Locator, expect, mergeTests} from '@playwright/test';

import {apiHelpersTest} from '../../../../../fixtures/apiHelpersTest';
import {dataSetManagerApiHelpersTest} from '../../../../../fixtures/dataSetManagerApiHelpersTest';
import {featureFlagsTest} from '../../../../../fixtures/featureFlagsTest';
import {isolatedSiteTest} from '../../../../../fixtures/isolatedSiteTest';
import {loginTest} from '../../../../../fixtures/loginTest';
import {EFDSVisualizationMode, waitForFDS} from '../../../../../utils/waitFor';
import {waitForAlert} from '../../../../../utils/waitForAlert';
import {systemDataSetsPageTest} from '../../../../frontend-data-set-admin-web/main/fixtures/systemDataSetsPageTest';
import {SystemDataSetsPage} from '../../../../frontend-data-set-admin-web/main/pages/SystemDataSetsPage';
import {fdsSamplePageTest} from '../../fixtures/fdsSamplePageTest';

const test = mergeTests(
	apiHelpersTest,
	dataSetManagerApiHelpersTest,
	fdsSamplePageTest,
	featureFlagsTest({
		'LPS-178052': {enabled: true},
	}),
	isolatedSiteTest,
	loginTest(),
	systemDataSetsPageTest
);

const ADVANCED_SAMPLE_ERC =
	'com_liferay_frontend_data_set_sample_web_internal_portlet_FDSSamplePortlet-advanced';

let fdsSamplePageURL: string;

async function createSampleCustomization({
	label,
	systemDataSetsPage,
}: {
	label: string;
	systemDataSetsPage: SystemDataSetsPage;
}) {
	await systemDataSetsPage.goto();

	const creationModal = systemDataSetsPage.creationModal;

	const sampleListItem = creationModal.listItems.filter({
		hasText: label,
	});

	await systemDataSetsPage.createButton.click();

	await expect(sampleListItem).toBeVisible();

	await sampleListItem.click();

	await expect(sampleListItem).toHaveClass(/selected/);

	await creationModal.createButton.click();

	await waitForAlert(systemDataSetsPage.page);
}

test.beforeEach(
	async ({dataSetManagerApiHelpers, fdsSamplePage, page, site}) => {
		await dataSetManagerApiHelpers.deleteDataSet({
			erc: ADVANCED_SAMPLE_ERC,
		});

		const {url} = await fdsSamplePage.setupFDSSampleWidget({site});

		fdsSamplePageURL = url;

		await fdsSamplePage.selectTab('Advanced');

		await waitForFDS({
			page,
			visualizationMode: EFDSVisualizationMode.TABLE,
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

		const startX = resizerBoundingBox.x + resizerBoundingBox.width / 2;
		const startY = resizerBoundingBox.y + resizerBoundingBox.height / 2;

		await page.mouse.move(startX, startY);
		await page.mouse.down();
		await page.mouse.move(startX + 50, startY, {steps: 5});
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
	async ({fdsSamplePage, page}) => {
		const firstRowItemActionButton =
			fdsSamplePage.table.itemActionButtons.first();

		const tableCells = fdsSamplePage.table.container.locator('td');

		const initialBodyCellText = await tableCells.nth(1).innerText();

		const rowAction = tableCells.locator('.component-action').first();

		await test.step('Check that row actions are present', async () => {
			await firstRowItemActionButton.hover();

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
			await expect(tableCells.nth(1)).not.toHaveText(initialBodyCellText);

			await expect(rowAction).toBeAttached();
		});
	}
);

test(
	'Search columns in the column visibility dropdown',
	{tag: '@LPD-107790'},
	async ({fdsSamplePage, page}) => {
		const menuItems = page.getByRole('menuitem');
		const searchInput = page.getByRole('textbox', {name: 'Search'});

		let menuItemsCount: number;

		await test.step('Open the column visibility dropdown', async () => {
			await fdsSamplePage.table.manageColumnsVisibilityButton.click();

			await expect(searchInput).toBeVisible();

			menuItemsCount = await menuItems.count();

			expect(menuItemsCount).toBeGreaterThan(1);
		});

		await test.step('Filter the columns by name', async () => {
			await searchInput.fill('DESC');

			await expect(menuItems).toHaveCount(1);
			await expect(menuItems.first()).toHaveText('Description');
		});

		await test.step('Show a message when no column matches', async () => {
			await searchInput.fill('nonexistent column');

			await expect(menuItems).toHaveCount(0);
			await expect(page.getByText('No Results Found')).toBeVisible();
		});

		await test.step('Clear the search to show every column again', async () => {
			await page.getByRole('button', {name: 'Clear Search'}).click();

			await expect(searchInput).toHaveValue('');
			await expect(menuItems).toHaveCount(menuItemsCount);
		});

		await test.step('Hide a column from the filtered list', async () => {
			await searchInput.fill('desc');

			await menuItems.first().getByRole('switch').click();

			await expect(
				fdsSamplePage.table.headerCells.filter({
					hasText: 'Description',
				})
			).toHaveCount(0);
		});

		await test.step('Reach the search and the columns with the keyboard', async () => {
			await page.keyboard.press('Escape');

			await expect(
				fdsSamplePage.table.manageColumnsVisibilityButton
			).toBeFocused();

			await page.keyboard.press('Enter');

			await expect(searchInput).toBeFocused();

			await page.keyboard.press('ArrowDown');

			await expect(menuItems.first().getByRole('switch')).toBeFocused();

			await page.keyboard.press('ArrowUp');

			await expect(searchInput).toBeFocused();
		});
	}
);

test(
	'Columns visibility dropdown is shown/hidden according to FDS configuration',
	{tag: '@LPD-112704'},
	async ({
		dataSetManagerApiHelpers,
		fdsSamplePage,
		page,
		systemDataSetsPage,
	}) => {
		const columnsVisibilityButton = page.getByLabel(
			'Manage Columns Visibility'
		);
		const firstBodyCell = fdsSamplePage.table.container
			.locator('td')
			.nth(1);

		let fdsSamplePageURLWithHiddenColumn: string;
		let hiddenColumnText: string;

		await test.step('Hide the first column through the columns visibility dropdown', async () => {
			hiddenColumnText = await firstBodyCell.innerText();

			await columnsVisibilityButton.click();

			await page.getByRole('menuitem').nth(0).click();

			await expect(firstBodyCell).not.toHaveText(hiddenColumnText);

			await expect.poll(() => page.url()).toContain('_fdsConfig=');

			fdsSamplePageURLWithHiddenColumn = page.url();
		});

		try {
			await test.step('Hide the columns visibility dropdown in the Data Set Manager', async () => {
				await createSampleCustomization({
					label: 'Advanced Sample',
					systemDataSetsPage,
				});

				await dataSetManagerApiHelpers.updateDataSet({
					erc: ADVANCED_SAMPLE_ERC,
					showColumnsVisibility: false,
				});
			});

			await test.step('Check the columns visibility dropdown is not shown and the item actions column is kept', async () => {
				await page.goto(fdsSamplePageURL);

				await fdsSamplePage.selectTab('Advanced');

				await waitForFDS({
					page,
					visualizationMode: EFDSVisualizationMode.TABLE,
				});

				await expect(columnsVisibilityButton).not.toBeAttached();

				await expect(firstBodyCell).toHaveText(hiddenColumnText);

				await expect(fdsSamplePage.table.headerCells).toHaveCount(
					await fdsSamplePage.table.bodyRows
						.first()
						.locator('td')
						.count()
				);

				await expect(fdsSamplePage.table.headerCells.last()).toHaveText(
					'Item Actions'
				);

				await expect(fdsSamplePage.table.headerCells.last()).toHaveCSS(
					'position',
					'sticky'
				);

				await expect(
					fdsSamplePage.table.bodyRows
						.first()
						.locator('td.cell-item-actions')
				).toBeAttached();
			});

			await test.step('Check the columns hidden through the URL are still hidden', async () => {
				await page.goto(fdsSamplePageURLWithHiddenColumn);

				await waitForFDS({
					page,
					visualizationMode: EFDSVisualizationMode.TABLE,
				});

				await expect(columnsVisibilityButton).not.toBeAttached();

				await expect(firstBodyCell).not.toHaveText(hiddenColumnText);
			});
		}
		finally {
			await test.step('Delete used system data set', async () => {
				await dataSetManagerApiHelpers.deleteDataSet({
					erc: ADVANCED_SAMPLE_ERC,
				});
			});
		}
	}
);

test(
	'Item actions column is not shown when the columns visibility dropdown is hidden and there are no item actions',
	{tag: '@LPD-112704'},
	async ({
		dataSetManagerApiHelpers,
		fdsSamplePage,
		page,
		systemDataSetsPage,
	}) => {
		const erc =
			'com_liferay_frontend_data_set_sample_web_internal_portlet_FDSSamplePortlet-delegatedFilters';

		try {
			await test.step('Hide the columns visibility dropdown of a data set without item actions', async () => {
				await createSampleCustomization({
					label: 'Delegated Filters Sample',
					systemDataSetsPage,
				});

				await dataSetManagerApiHelpers.updateDataSet({
					erc,
					showColumnsVisibility: false,
				});
			});

			await test.step('Check there is no item actions column', async () => {
				await page.goto(fdsSamplePageURL);

				await fdsSamplePage.selectTab('Delegated Filters');

				await waitForFDS({
					page,
					visualizationMode: EFDSVisualizationMode.TABLE,
				});

				await expect(
					page.getByLabel('Manage Columns Visibility')
				).not.toBeAttached();

				await expect(fdsSamplePage.table.headerCells).toHaveCount(
					await fdsSamplePage.table.bodyRows
						.first()
						.locator('td')
						.count()
				);

				await expect(
					fdsSamplePage.table.container.locator('.cell-item-actions')
				).toHaveCount(0);

				await expect(
					fdsSamplePage.table.headerCells.last()
				).not.toHaveCSS('position', 'sticky');
			});
		}
		finally {
			await test.step('Delete used system data set', async () => {
				await dataSetManagerApiHelpers.deleteDataSet({erc});
			});
		}
	}
);

test(
	'Columns with nested field names are shown',
	{tag: '@LPD-75783'},
	async ({
		dataSetManagerApiHelpers,
		fdsSamplePage,
		page,
		systemDataSetsPage,
	}) => {
		try {
			await test.step('Check author column, defined by creator.name field name, is visible', async () => {
				await expect(
					fdsSamplePage.table.container.locator(
						'[data-id="string,creator,name"]'
					)
				).toBeVisible();
			});

			await test.step('Create System Data Set', async () => {
				await systemDataSetsPage.goto();

				const creationModal = systemDataSetsPage.creationModal;

				const advancedSampleListItem = creationModal.listItems.filter({
					hasText: 'Advanced Sample',
				});

				await systemDataSetsPage.createButton.click();

				await expect(advancedSampleListItem).toBeVisible();

				await advancedSampleListItem.click();

				await expect(advancedSampleListItem).toHaveClass(/selected/);

				await creationModal.createButton.click();

				await waitForAlert(systemDataSetsPage.page);
			});

			await test.step('Check author column is still visible', async () => {
				await page.goto(fdsSamplePageURL);

				await fdsSamplePage.selectTab('Advanced');

				await waitForFDS({
					page,
					visualizationMode: EFDSVisualizationMode.TABLE,
				});

				await expect(
					fdsSamplePage.table.container.locator(
						'[data-id="string,creator.name"]'
					)
				).toBeVisible();
			});
		}
		finally {
			await test.step('Delete used system data set', async () => {
				await dataSetManagerApiHelpers.deleteDataSet({
					erc: ADVANCED_SAMPLE_ERC,
				});
			});
		}
	}
);

test(
	'Action column stays frozen during horizontal scroll',
	{tag: ['@LPD-106210', '@LPD-114095']},
	async ({fdsSamplePage, page}) => {
		const actionsHeaderCell = fdsSamplePage.table.headerCells.last();
		const firstRowActionsCell = fdsSamplePage.table.bodyRows
			.first()
			.locator('.cell-item-actions');
		const scrollContainer = page.locator('.fds .table-responsive');

		async function getBoxShadow(locator: Locator) {
			return locator.evaluate(
				(element) => getComputedStyle(element).boxShadow
			);
		}

		async function getRightEdge(locator: Locator) {
			const boundingBox = await locator.boundingBox();

			return boundingBox.x + boundingBox.width;
		}

		async function hasHorizontalScroll() {
			return scrollContainer.evaluate(
				(element) => element.scrollWidth > element.clientWidth
			);
		}

		await test.step('Check the action column has no shadow without horizontal scroll', async () => {
			await page.setViewportSize({height: 900, width: 2560});

			await expect.poll(hasHorizontalScroll).toBe(false);

			await expect
				.poll(() => getBoxShadow(actionsHeaderCell))
				.toBe('none');
			await expect
				.poll(() => getBoxShadow(firstRowActionsCell))
				.toBe('none');
		});

		await test.step('Narrow the viewport to force horizontal scroll', async () => {
			await page.setViewportSize({height: 900, width: 700});

			await expect.poll(hasHorizontalScroll).toBe(true);
		});

		await test.step('Check the action column has a shadow with horizontal scroll', async () => {
			await expect
				.poll(() => getBoxShadow(actionsHeaderCell))
				.not.toBe('none');
			await expect
				.poll(() => getBoxShadow(firstRowActionsCell))
				.not.toBe('none');
		});

		for (const scrollPosition of ['start', 'middle', 'end']) {
			await test.step(`Check the action column is pinned to the right edge at the ${scrollPosition}`, async () => {
				await scrollContainer.evaluate((element, scrollPosition) => {
					const maxScrollLeft =
						element.scrollWidth - element.clientWidth;

					element.scrollLeft =
						scrollPosition === 'start'
							? 0
							: scrollPosition === 'middle'
								? maxScrollLeft / 2
								: maxScrollLeft;
				}, scrollPosition);

				const containerRightEdge = await scrollContainer.evaluate(
					(element) =>
						element.getBoundingClientRect().left +
						element.clientLeft +
						element.clientWidth
				);

				expect(
					Math.abs(
						(await getRightEdge(actionsHeaderCell)) -
							containerRightEdge
					)
				).toBeLessThanOrEqual(2);

				expect(
					Math.abs(
						(await getRightEdge(firstRowActionsCell)) -
							containerRightEdge
					)
				).toBeLessThanOrEqual(2);
			});
		}

		await test.step('Open the row actions with the table scrolled to the middle', async () => {
			await scrollContainer.evaluate((element) => {
				element.scrollLeft =
					(element.scrollWidth - element.clientWidth) / 2;
			});

			await fdsSamplePage.table.itemActionButtons.first().click();

			await expect(fdsSamplePage.dropdownMenu).toBeVisible();
		});
	}
);
