/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Locator, expect, mergeTests} from '@playwright/test';

import {featureFlagsTest} from '../../../fixtures/featureFlagsTest';
import {claySamplePageTest} from './fixtures/claySamplePageTest';
import {TabName} from './pages/ClaySamplePage';

export const test = mergeTests(
	claySamplePageTest,
	featureFlagsTest({
		'LPS-178052': {enabled: true},
	})
);

test.beforeEach('Select Management Toolbars tab', async ({claySamplePage}) => {
	await claySamplePage.selectTab(TabName.MANAGEMENT_TOOLBARS);
});

test.describe('Management Toolbar Default State', () => {
	test(
		'Assert the "New" button is displayed properly',
		{tag: '@LPS-144540'},
		async ({claySamplePage, page}) => {
			await test.step('Check that the new button text is "New"', async () => {
				await expect(
					claySamplePage.managementToolbarDefaultState
						.getByRole('button')
						.filter({hasText: 'New'})
				).toBeVisible();
			});

			await test.step('Set the window size to phone size', async () => {
				await page.setViewportSize({height: 720, width: 360});
			});

			await test.step('Check the button is an icon', async () => {
				await expect(
					claySamplePage.managementToolbarDefaultState
						.getByRole('button', {name: 'New'})
						.locator('.lexicon-icon-plus')
				).toBeVisible();
			});

			await test.step('Hover over the "New" button', async () => {
				await claySamplePage.managementToolbarDefaultState
					.getByRole('button', {name: 'New'})
					.hover();
			});

			await test.step('Check the tooltip text displays "New"', async () => {
				await expect(
					claySamplePage.tooltip.getByText('New')
				).toBeVisible();
			});
		}
	);

	test(
		'Assert tooltip messages will be displayed when hovered over the filter and order buttons in responsive mode',
		{tag: '@LPS-144536'},
		async ({claySamplePage, page}) => {
			await test.step('Set the window size to phone size', async () => {
				await page.setViewportSize({height: 720, width: 360});
			});

			await test.step('Hover over the "Filter" button', async () => {
				await claySamplePage.managementToolbarDefaultState
					.getByRole('button', {name: 'Filter'})
					.hover();
			});

			await test.step('Check the tooltip text displays "Show Filter Options"', async () => {
				await expect(
					page
						.locator('.tooltip-inner')
						.getByText('Show Filter Options')
				).toBeVisible();
			});

			await test.step('Hover over the "Order" button', async () => {
				await claySamplePage.managementToolbarDefaultState
					.getByRole('button', {name: 'Order'})
					.hover();
			});

			await test.step('Check the tooltip text displays "Show Order Options"', async () => {
				await expect(
					page
						.locator('.tooltip-inner')
						.getByText('Show Order Options')
				).toBeVisible();
			});
		}
	);

	test(
		'Assert the view button is properly displayed',
		{tag: '@LPS-144535'},
		async ({claySamplePage, page}) => {
			await test.step('Check that the double caret icon is visible', async () => {
				await expect(
					claySamplePage.managementToolbarDefaultState.locator(
						'.lexicon-icon-caret-double-l'
					)
				).toBeVisible();
			});

			await test.step('Hover over the view button', async () => {
				await claySamplePage.managementToolbarDefaultState
					.getByLabel(/Select View/)
					.hover();
			});

			await test.step('Check the tooltip text is displayed', async () => {
				await expect(page.getByText(/Select View/)).toBeVisible();
			});
		}
	);
});

test.describe('Management Toolbar With Results', () => {
	test('Clear button has a cursor of type pointer', async ({
		claySamplePage,
	}) => {
		let clearButton: Locator;

		await test.step('Get the clear button', async () => {
			clearButton =
				claySamplePage.managementToolbarWithResultsBar.getByLabel(
					'Clear'
				);

			await expect(clearButton).toBeVisible();
		});

		await test.step('Check that cursor type is a pointer', async () => {
			const cursorType = await clearButton.evaluate((element) =>
				window.getComputedStyle(element).getPropertyValue('cursor')
			);

			await expect(cursorType).toEqual('pointer');
		});
	});
});

test.describe('Management Toolbar Active State', () => {
	test(
		'Assert the items in the actions ellipsis are displayed',
		{tag: '@LPS-144538'},
		async ({claySamplePage, page}) => {
			let actionsButton: Locator;

			await test.step('Check that the ellipsis actions button is visible', async () => {
				actionsButton = claySamplePage.managementToolbarActiveState
					.getByRole('button')
					.nth(3);

				await expect(actionsButton).toBeVisible();
			});

			await test.step('Click on the actions button', async () => {
				await actionsButton.click();
			});

			await test.step('Check that the "Edit" button visible', async () => {
				await expect(
					page.getByRole('menuitem', {name: 'Edit'})
				).toBeVisible();
			});

			await test.step('Check that the "Download" button and icon is visible', async () => {
				await expect(
					page.getByRole('menuitem', {name: 'Download'})
				).toBeVisible();

				await expect(
					page
						.getByRole('menuitem', {name: 'Download'})
						.locator('.lexicon-icon-download')
				).toBeVisible();
			});

			await test.step('Check that the "Delete" button and icon is visible', async () => {
				await expect(
					page.getByRole('menuitem', {name: 'Delete'})
				).toBeVisible();

				await expect(
					page
						.getByRole('menuitem', {name: 'Delete'})
						.locator('.lexicon-icon-trash')
				).toBeVisible();
			});
		}
	);

	test(
		'Assert the action buttons are displayed properly in responsive mode',
		{tag: '@LPS-144538'},
		async ({claySamplePage, page}) => {
			await test.step('Set the window size to tablet size', async () => {
				await page.setViewportSize({height: 1024, width: 800});
			});

			await test.step('Check that the text is not visible', async () => {
				await expect(
					claySamplePage.managementToolbarActiveState.getByText(
						'Download'
					)
				).not.toBeVisible();

				await expect(
					claySamplePage.managementToolbarActiveState.getByText(
						'Delete'
					)
				).not.toBeVisible();
			});

			await test.step('Check that the icon is visible', async () => {

				// This counts the icons because for responsiveness there is one
				// button with an icon only and another button with icon and text
				// where one should be visible at a time.

				const downloadIconsLocator =
					claySamplePage.managementToolbarActiveState.locator(
						'.lexicon-icon-download'
					);

				const downloadIcons = await downloadIconsLocator.all();

				let downloadIconVisibleCount = 0;

				for (const downloadIcon of downloadIcons) {
					if (await downloadIcon.isVisible()) {
						downloadIconVisibleCount++;
					}
				}

				expect(downloadIconVisibleCount).toEqual(1);

				const trashIconsLocator =
					claySamplePage.managementToolbarActiveState.locator(
						'.lexicon-icon-trash'
					);

				const trashIcons = await trashIconsLocator.all();

				let trashIconVisibleCount = 0;

				for (const trashIcon of trashIcons) {
					if (await trashIcon.isVisible()) {
						trashIconVisibleCount++;
					}
				}

				expect(trashIconVisibleCount).toEqual(1);
			});

			await test.step('Hover over the download button', async () => {
				await claySamplePage.managementToolbarActiveState
					.getByRole('button', {name: 'Download'})
					.hover();
			});

			await test.step('Check the tooltip text is displayed', async () => {
				await expect(
					claySamplePage.tooltip.getByText('Download')
				).toBeVisible();
			});

			await test.step('Hover over the delete button', async () => {
				await claySamplePage.managementToolbarActiveState
					.getByRole('link', {name: 'Delete'})
					.hover();
			});

			await test.step('Check the tooltip text is displayed', async () => {
				await expect(
					claySamplePage.tooltip.getByText('Delete')
				).toBeVisible();
			});
		}
	);

	test(
		'Assert the clear button will be displayed properly in responsive mode',
		{tag: '@LPS-144539'},
		async ({claySamplePage, page}) => {
			await test.step('Set the window size to phone size', async () => {
				await page.setViewportSize({height: 720, width: 360});
			});

			await test.step('Check that the times circle icon is visible', async () => {
				await expect(
					claySamplePage.managementToolbarActiveState.locator(
						'.lexicon-icon-times-circle'
					)
				).toBeVisible();
			});

			await test.step('Hover over the clear button', async () => {
				await claySamplePage.managementToolbarActiveState
					.getByRole('button', {name: 'Clear'})
					.hover();
			});

			await test.step('Check the tooltip text is displayed', async () => {
				await expect(
					claySamplePage.tooltip.getByText('Clear')
				).toBeVisible();
			});
		}
	);
});

test.describe('Management Toolbar Using Display Context', () => {
	test(
		'Assert the order button can display the correct icons',
		{tag: '@LPS-144536'},
		async ({claySamplePage, page}) => {
			await test.step('Open the order dropdown', async () => {
				await claySamplePage.managementToolbarUsingDisplayContext
					.getByRole('button', {name: 'Order'})
					.click();
			});

			await test.step('Check that ascending order is selected', async () => {
				await expect(
					page
						.getByRole('menuitem', {name: 'Ascending'})
						.locator('.lexicon-icon-check')
				).toBeVisible();
			});

			await test.step('Check that the icon is order-list-up', async () => {
				expect(
					page
						.getByRole('button', {name: 'Order'})
						.locator('.lexicon-icon-order-list-up')
						.first()
				).toBeVisible();
			});

			await test.step('Click on descending', async () => {
				await page.getByRole('menuitem', {name: 'Descending'}).click();
			});

			await test.step('Navigate to management toolbar tab', async () => {
				await claySamplePage.selectTab(TabName.MANAGEMENT_TOOLBARS);
			});

			await test.step('Open the order dropdown', async () => {
				await claySamplePage.managementToolbarUsingDisplayContext
					.getByRole('button', {name: 'Order'})
					.click();
			});

			await test.step('Check that descending order is selected', async () => {
				await expect(
					page
						.getByRole('menuitem', {name: 'Descending'})
						.locator('.lexicon-icon-check')
				).toBeVisible();
			});

			await test.step('Check that the icon is order-list-down', async () => {
				expect(
					page
						.getByRole('button', {name: 'Order'})
						.locator('.lexicon-icon-order-list-down')
						.first()
				).toBeVisible();
			});
		}
	);
});
