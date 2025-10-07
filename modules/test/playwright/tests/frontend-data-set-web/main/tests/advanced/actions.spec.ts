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
import {waitForAlert} from '../../../../../utils/waitForAlert';
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

const TAB_NAME = {
	ADVANCED: 'Advanced',
	ITEMS_ACTIONS_GROUPS: 'Items Actions Groups',
};

const TABS = [
	{
		actionsCount: 14,
		name: TAB_NAME.ADVANCED,
	},
	{
		actionsCount: 15,
		name: TAB_NAME.ITEMS_ACTIONS_GROUPS,
	},
];

for (const tab of TABS) {
	test.describe(`Test Actions On Tab: ${tab.name}`, () => {
		test.beforeEach(async ({fdsSamplePage, page, site}) => {
			await fdsSamplePage.setupFDSSampleWidget({site});

			await fdsSamplePage.selectTab(tab.name);

			await waitForFDS({
				page,
				visualizationMode: EFDSVisualizationMode.TABLE,
			});
		});

		test('Behavior of item actions', async ({fdsSamplePage, page}) => {
			const asyncConnectionRefused = 'Async Connection Refused';
			const asyncResourceNotFound = 'Async Resource Not Found';
			const asyncSuccess = 'Async Success';
			const sampleView = 'Sample View';
			const sidePanelActionLabelWithActionTitle =
				'Side Panel With Action Title';
			const sidePanelActionLabelWithContentTitle =
				'Side Panel With Content Title';
			const sidePanelActionLabelWithActionTitleContentTitle =
				'Side Panel With Action and Content Title';
			const sidePanelActionLabelWithoutTitle = 'Side Panel With No Title';
			const sidePanelActionTitle = 'Side Panel Title Provided by Action';
			const sidePanelContentTitle = 'Side Panel Title Provided by Page';

			await test.step('Check that the Item Actions dropdown is present in table row', async () => {
				const tableItemActionButton =
					fdsSamplePage.table.itemActionButtons.first();

				await expect(tableItemActionButton).toBeVisible();

				const dropdownMenu = await fdsSamplePage.getDropdownId(
					tableItemActionButton
				);

				await expect(dropdownMenu.getByRole('menuitem')).toHaveCount(
					tab.actionsCount
				);

				await page.keyboard.press('Escape');
			});

			await test.step('Check that the Item Actions dropdown displays icons for Table, List, and Cards views', async () => {
				await test.step('Check Table view actions dropdown items has icons', async () => {
					const tableItemActionButton =
						fdsSamplePage.table.itemActionButtons.first();

					await expect(tableItemActionButton).toBeVisible();

					await fdsSamplePage.checkDropdownMenuIconsAreVisible(
						tableItemActionButton
					);
				});

				await test.step('Check List view actions dropdown items has icons', async () => {
					await fdsSamplePage.changeVisualizationMode({
						page,
						visualizationMode: EFDSVisualizationMode.LIST,
					});

					const listItemActionButton =
						fdsSamplePage.list.itemActionButtons.first();

					await expect(listItemActionButton).toBeVisible();

					await fdsSamplePage.checkDropdownMenuIconsAreVisible(
						listItemActionButton
					);
				});

				await test.step('Check Cards view action dropdown items has icons', async () => {
					await fdsSamplePage.changeVisualizationMode({
						page,
						visualizationMode: EFDSVisualizationMode.CARDS,
					});

					const cardItemActionButton =
						fdsSamplePage.cards.itemActionButtons.first();

					await expect(cardItemActionButton).toBeVisible();

					await fdsSamplePage.checkDropdownMenuIconsAreVisible(
						cardItemActionButton
					);
				});

				await test.step('Switch back to Table view', async () => {
					await fdsSamplePage.changeVisualizationMode({
						page,
						visualizationMode: EFDSVisualizationMode.TABLE,
					});
				});
			});

			await test.step('Side Panel action opens a side panel with content title', async () => {
				await fdsSamplePage.clickItemAction(
					sidePanelActionLabelWithContentTitle
				);

				await expect(fdsSamplePage.sidePanel).toBeInViewport();

				const frame = fdsSamplePage.sidePanelFrame;

				await frame.getByText(sidePanelContentTitle).waitFor();

				await expect(
					frame.getByText(sidePanelContentTitle)
				).toHaveCount(1);

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

				await expect(page.getByText(sidePanelActionTitle)).toHaveCount(
					1
				);

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

				await expect(page.getByText(sidePanelActionTitle)).toHaveCount(
					1
				);

				const frame = fdsSamplePage.sidePanelFrame;

				await expect(
					frame.locator('.side-panel-iframe-header')
				).toBeInViewport();
				await frame.getByText(sidePanelContentTitle).waitFor();

				await expect(
					frame.getByText(sidePanelContentTitle)
				).toHaveCount(1);

				await page.keyboard.press('Escape');

				await expect(fdsSamplePage.sidePanel).toHaveClass(/is-hidden/);
			});

			await test.step('Side Panel action opens a side panel without title', async () => {
				await fdsSamplePage.clickItemAction(
					sidePanelActionLabelWithoutTitle
				);

				await expect(fdsSamplePage.sidePanel).toBeInViewport();

				await expect(
					page.locator('.fds-side-panel-title')
				).toBeInViewport();
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
				let dialogMessage = '';

				page.on('dialog', async (dialog) => {
					dialogMessage = dialog.message();
					await dialog.accept();
				});

				for (const visualizationMode of Object.values(
					EFDSVisualizationMode
				)) {
					await test.step(`Sample view action receives the list of items, ${visualizationMode}`, async () => {
						await fdsSamplePage.changeVisualizationMode({
							page,
							visualizationMode,
						});

						await fdsSamplePage.clickItemAction(sampleView);
						expect(dialogMessage).toContain('Hello Sample1!');
						expect(dialogMessage).toContain('element #1');

						await fdsSamplePage.clickItemAction(sampleView, 19);
						expect(dialogMessage).toContain('Hello Sample32!');
						expect(dialogMessage).toContain('element #20');
					});
				}

				await fdsSamplePage.changeVisualizationMode({
					page,
					visualizationMode: EFDSVisualizationMode.TABLE,
				});
			});

			await test.step('Async connection refused action opens an unexpected error alert toast', async () => {
				await fdsSamplePage.clickItemAction(asyncConnectionRefused);

				await waitForAlert(
					page,
					'Error:An unexpected error occurred.',
					{
						type: 'danger',
					}
				);
			});

			await test.step('Async resource not found action opens an unexpected error alert toast', async () => {
				await fdsSamplePage.clickItemAction(asyncResourceNotFound);

				await waitForAlert(
					page,
					'Error:An unexpected error occurred.',
					{
						type: 'danger',
					}
				);
			});

			await test.step('Async success action opens a success alert toast', async () => {
				await fdsSamplePage.clickItemAction(asyncSuccess);

				await waitForAlert(page);
			});

			await test.step('Check that Sample Delete action has custom className applied', async () => {
				const tableItemActionButton =
					fdsSamplePage.table.itemActionButtons.first();

				await expect(tableItemActionButton).toBeVisible();

				const dropdownMenu = await fdsSamplePage.getDropdownId(
					tableItemActionButton
				);

				const sampleDeleteActionItem = dropdownMenu
					.getByRole('menuitem')
					.filter({hasText: 'Sample Delete'});

				await expect(sampleDeleteActionItem).toBeVisible();

				await expect(sampleDeleteActionItem).toHaveClass(/text-danger/);

				await page.keyboard.press('Escape');
			});
		});

		test(
			'Behavior of quick actions',
			{tag: '@LPS-153220'},
			async ({fdsSamplePage, page}) => {
				const firstRowItemActionButton =
					fdsSamplePage.table.itemActionButtons.first();
				const thirdRowItemActionButton =
					fdsSamplePage.table.itemActionButtons.nth(2);

				const firstRowSampleEditQuickActionLink =
					fdsSamplePage.table.bodyRows
						.first()
						.getByLabel('Sample Edit');

				const firstTableHeadCell =
					fdsSamplePage.table.headerCells.first();

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

					await expect(
						firstRowSampleEditQuickActionLink
					).toBeVisible();

					await firstTableHeadCell.hover();

					await expect(
						firstRowSampleEditQuickActionLink
					).not.toBeVisible();
				});

				await test.step('When hovering over the first line item and the quick action menu is displayed on the 1st line', async () => {
					const firstTableRow = fdsSamplePage.table.bodyRows.first();

					await firstTableRow.hover();

					await expect(
						firstRowSampleEditQuickActionLink
					).toBeVisible();
				});

				await test.step('When clicking on the ellipsis and hovering over another row, multiple quick action menus are displayed', async () => {
					await thirdRowItemActionButton.click();

					await expect(
						page.locator('.dropdown-menu.show')
					).toBeVisible();

					await fdsSamplePage.table.bodyRows.first().hover();

					await expect(
						firstRowSampleEditQuickActionLink
					).toBeVisible();

					await firstTableHeadCell.click(); // Close dropdown
				});

				await test.step('Assert quick action can be displayed on only one active row', async () => {
					await firstRowItemActionButton.hover();

					await expect(
						firstRowSampleEditQuickActionLink
					).toBeVisible();

					await thirdRowItemActionButton.hover();

					await thirdRowItemActionButton.click();

					await expect(
						firstRowSampleEditQuickActionLink
					).not.toBeVisible();

					await firstTableHeadCell.click(); // Close dropdown
				});

				await test.step('Assert that quick action icons list should be limited to three actions', async () => {
					await firstRowItemActionButton.hover();

					await expect(
						fdsSamplePage.table.bodyRows
							.first()
							.getByLabel('View Details')
					).toBeVisible();

					await expect(
						fdsSamplePage.table.bodyRows
							.first()
							.getByLabel('Sample View')
					).toBeVisible();

					await expect(
						fdsSamplePage.table.bodyRows
							.first()
							.getByLabel('Sample Edit')
					).toBeVisible();

					await expect(
						fdsSamplePage.table.bodyRows
							.first()
							.getByLabel('Sample Copy')
					).not.toBeVisible();
				});

				await test.step('Assert the quick action is not visible when the row checkbox is checked', async () => {
					await fdsSamplePage.table.bodyRows
						.first()
						.getByRole('checkbox')
						.click();

					await firstRowItemActionButton.hover();

					await expect(
						firstRowSampleEditQuickActionLink
					).not.toBeVisible();
				});
			}
		);

		if (tab.name === TAB_NAME.ITEMS_ACTIONS_GROUPS) {
			test(
				'Behavior of items actions groups',
				{tag: '@LPD-65429'},
				async ({fdsSamplePage, page}) => {
					await test.step('Assert that "Group Permission Test" does not appear in the actions dropdown menu', async () => {
						const tableItemActionButton =
							fdsSamplePage.table.itemActionButtons.first();

						await tableItemActionButton.click();

						const dropdownMenu = page.locator(
							'.dropdown-menu.show'
						);
						await dropdownMenu.waitFor();

						await expect(
							dropdownMenu.getByRole('menuitem', {
								name: 'Group Permission Test',
							})
						).not.toBeVisible();

						await page.keyboard.press('Escape');
					});

					await test.step('Assert that “Group Item” is visible and clicking on it shows an alert "You clicked on an item in a group"', async () => {
						let dialogMessage = '';

						page.on('dialog', async (dialog) => {
							dialogMessage = dialog.message();

							await dialog.accept();
						});

						await fdsSamplePage.clickItemAction('Group Item');

						expect(dialogMessage).toBe(
							'You clicked on an item in a group'
						);
					});

					await test.step('Assert that a separator is not shown in the first group', async () => {
						await fdsSamplePage.table.itemActionButtons
							.first()
							.click();

						const dropdownMenu = page.locator(
							'.dropdown-menu.show'
						);
						await dropdownMenu.waitFor();

						const firstGroup = dropdownMenu
							.locator('ul[role="group"]')
							.first();

						await expect(
							firstGroup.locator('li[role="separator"]')
						).not.toBeVisible();

						await page.keyboard.press('Escape');
					});

					await test.step('Assert that 2 separators in a row do not display', async () => {
						await fdsSamplePage.table.itemActionButtons
							.first()
							.click();

						const dropdownMenu = page.locator(
							'.dropdown-menu.show'
						);
						await dropdownMenu.waitFor();

						await expect(
							dropdownMenu.locator(
								'li[role="presentation"]:has(> ul[role="group"] > li[role="separator"]:only-child) + li[role="presentation"]:has(> ul[role="group"] > li[role="separator"]:first-child)'
							)
						).toHaveCount(0);

						await page.keyboard.press('Escape');
					});
				}
			);
		}
	});
}
