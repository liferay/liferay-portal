/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Locator, expect, mergeTests} from '@playwright/test';

import {apiHelpersTest} from '../../../../../fixtures/apiHelpersTest';
import {featureFlagsTest} from '../../../../../fixtures/featureFlagsTest';
import {isolatedSiteTest} from '../../../../../fixtures/isolatedSiteTest';
import {loginTest} from '../../../../../fixtures/loginTest';
import {EFDSVisualizationMode, waitForFDS} from '../../../../../utils/waitFor';
import {waitForAlert} from '../../../../../utils/waitForAlert';
import {systemDataSetsPageTest} from '../../../../frontend-data-set-admin-web/main/fixtures/systemDataSetsPageTest';
import {SystemDataSetsPage} from '../../../../frontend-data-set-admin-web/main/pages/SystemDataSetsPage';
import {fdsSamplePageTest} from '../../fixtures/fdsSamplePageTest';
import {FDSSamplePage} from '../../pages/FDSSamplePage';

const test = mergeTests(
	apiHelpersTest,
	fdsSamplePageTest,
	featureFlagsTest({
		'LPS-178052': {enabled: true},
	}),
	isolatedSiteTest,
	loginTest(),
	systemDataSetsPageTest
);

let fdsSamplePageURL: string;

async function deleteAdvancedSampleCustomization({
	fdsSamplePage,
	systemDataSetsPage,
}: {
	fdsSamplePage: FDSSamplePage;
	systemDataSetsPage: SystemDataSetsPage;
}) {
	await systemDataSetsPage.goto();

	const advancedSampleRow = systemDataSetsPage.pageContainer
		.locator('.fds tr')
		.filter({hasText: 'Advanced Sample'});

	if ((await advancedSampleRow.count()) === 0) {
		return;
	}

	await advancedSampleRow.locator('.dropdown-toggle').click();

	await fdsSamplePage.dropdownMenu
		.getByRole('menuitem', {name: 'Delete'})
		.click();

	const deleteModal = systemDataSetsPage.page.getByRole('dialog');

	await deleteModal.getByRole('button', {name: 'Delete'}).click();

	await waitForAlert(systemDataSetsPage.page);

	await expect(advancedSampleRow).not.toBeAttached();
}

test.beforeEach(async ({fdsSamplePage, page, site, systemDataSetsPage}) => {
	await deleteAdvancedSampleCustomization({
		fdsSamplePage,
		systemDataSetsPage,
	});

	const {url} = await fdsSamplePage.setupFDSSampleWidget({site});

	fdsSamplePageURL = url;

	await fdsSamplePage.selectTab('Advanced');

	await waitForFDS({
		page,
		visualizationMode: EFDSVisualizationMode.TABLE,
	});
});

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
	'Columns with nested field names are shown',
	{tag: '@LPD-75783'},
	async ({fdsSamplePage, page, systemDataSetsPage}) => {
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
				await deleteAdvancedSampleCustomization({
					fdsSamplePage,
					systemDataSetsPage,
				});
			});
		}
	}
);

test(
	'Action column stays frozen during horizontal scroll',
	{tag: '@LPD-106210'},
	async ({fdsSamplePage, page}) => {
		const actionsHeaderCell = fdsSamplePage.table.headerCells.last();
		const firstRowActionsCell = fdsSamplePage.table.bodyRows
			.first()
			.locator('.cell-item-actions');
		const scrollContainer = page.locator('.fds .table-responsive');

		async function getRightEdge(locator: Locator) {
			const boundingBox = await locator.boundingBox();

			return boundingBox.x + boundingBox.width;
		}

		await test.step('Narrow the viewport to force horizontal scroll', async () => {
			await page.setViewportSize({height: 900, width: 700});

			await expect
				.poll(() =>
					scrollContainer.evaluate(
						(element) => element.scrollWidth > element.clientWidth
					)
				)
				.toBe(true);
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
