/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {mergeTests} from '@playwright/test';

import {featureFlagsTest} from '../../../../../fixtures/featureFlagsTest';
import {isolatedSiteTest} from '../../../../../fixtures/isolatedSiteTest';
import {loginTest} from '../../../../../fixtures/loginTest';
import {checkAccessibility} from '../../../../../utils/checkAccessibility';
import getRandomString from '../../../../../utils/getRandomString';
import {EFDSVisualizationMode, waitForFDS} from '../../../../../utils/waitFor';
import {fdsSamplePageTest} from '../../fixtures/fdsSamplePageTest';

const test = mergeTests(
	fdsSamplePageTest,
	featureFlagsTest({
		'LPS-178052': {enabled: true},
	}),
	isolatedSiteTest,
	loginTest()
);

const FDS_WRAPPER_SELECTOR = '.data-set-wrapper';
const FOCUS_TRAP_HIDDEN_SELECTOR = '[data-aria-hidden="true"]';
const OPEN_DROPDOWN_SELECTOR = '.dropdown-menu.show';
const OPEN_MODAL_SELECTOR = '.modal.show';
const SELECTION_TOOLBAR_SELECTOR = '[data-qa-id="selectionToolbar"]';

test.beforeEach(async ({fdsSamplePage, page, site}) => {
	await fdsSamplePage.setupFDSSampleWidget({site});

	await fdsSamplePage.selectTab('Advanced');

	await waitForFDS({page, visualizationMode: EFDSVisualizationMode.TABLE});
});

test('Advanced FDS is accessible across visualization modes', async ({
	fdsSamplePage,
	page,
}) => {
	await test.step('Default Table view', async () => {
		await checkAccessibility({page, selectors: [FDS_WRAPPER_SELECTOR]});
	});

	await test.step('Manage Columns Visibility dropdown open', async () => {
		await fdsSamplePage.table.manageColumnsVisibilityButton.click();

		await fdsSamplePage.dropdownMenu.waitFor();

		await checkAccessibility({
			page,
			selectors: [FDS_WRAPPER_SELECTOR, OPEN_DROPDOWN_SELECTOR],
			selectorsToExclude: [FOCUS_TRAP_HIDDEN_SELECTOR],
		});

		await page.keyboard.press('Escape');
	});

	await test.step('Visualization mode picker open', async () => {
		await fdsSamplePage.visualizationModeSelector.click();

		await fdsSamplePage.dropdownMenu.waitFor();

		await checkAccessibility({
			page,
			selectors: [FDS_WRAPPER_SELECTOR, OPEN_DROPDOWN_SELECTOR],
			selectorsToExclude: [FOCUS_TRAP_HIDDEN_SELECTOR],
		});

		await page.keyboard.press('Escape');
	});

	await test.step('Switch to List visualization mode', async () => {
		await fdsSamplePage.changeVisualizationMode({
			page,
			visualizationMode: EFDSVisualizationMode.LIST,
		});

		await checkAccessibility({page, selectors: [FDS_WRAPPER_SELECTOR]});
	});

	await test.step('Switch to Cards visualization mode', async () => {
		await fdsSamplePage.changeVisualizationMode({
			page,
			visualizationMode: EFDSVisualizationMode.CARDS,
		});

		await checkAccessibility({page, selectors: [FDS_WRAPPER_SELECTOR]});
	});
});

test('Advanced FDS is accessible during search interactions', async ({
	fdsSamplePage,
	page,
}) => {
	await test.step('Search with matching results', async () => {
		await fdsSamplePage.search('Sample1');

		await fdsSamplePage.activeFiltersToolbar.searchResume.waitFor();

		await checkAccessibility({page, selectors: [FDS_WRAPPER_SELECTOR]});

		await fdsSamplePage.activeFiltersToolbar.clearSearchButton.click();

		await fdsSamplePage.activeFiltersToolbar.searchResume.waitFor({
			state: 'hidden',
		});
	});

	await test.step('Search empty state', async () => {
		await fdsSamplePage.search('no-match');

		await fdsSamplePage.emptyStateContainer.waitFor();

		await checkAccessibility({page, selectors: [FDS_WRAPPER_SELECTOR]});
	});

	await test.step('Clear search', async () => {
		await fdsSamplePage.activeFiltersToolbar.clearSearchButton.click();

		await fdsSamplePage.emptyStateContainer.waitFor({state: 'hidden'});

		await checkAccessibility({page, selectors: [FDS_WRAPPER_SELECTOR]});
	});
});

test('Advanced FDS is accessible during filter interactions', async ({
	fdsSamplePage,
	page,
}) => {
	await test.step('Open filter dropdown', async () => {
		await fdsSamplePage.managementToolbar.filterButton.click();

		await fdsSamplePage.dropdownMenu.waitFor();

		await checkAccessibility({
			page,
			selectors: [FDS_WRAPPER_SELECTOR, OPEN_DROPDOWN_SELECTOR],
			selectorsToExclude: [FOCUS_TRAP_HIDDEN_SELECTOR],
		});

		await page.keyboard.press('Escape');
	});

	await test.step('Open Date Range filter editor', async () => {
		await fdsSamplePage.managementToolbar.filterButton.click();

		await fdsSamplePage.filterMenu
			.getByRole('menuitem', {name: 'Date Range'})
			.click();

		await fdsSamplePage.filterShowResultsOrAddButton.waitFor();

		await checkAccessibility({
			page,
			selectors: [FDS_WRAPPER_SELECTOR, OPEN_DROPDOWN_SELECTOR],
			selectorsToExclude: [FOCUS_TRAP_HIDDEN_SELECTOR],
		});

		await fdsSamplePage.filterMenu
			.getByRole('button', {name: 'Back'})
			.click();

		await page.keyboard.press('Escape');
	});

	await test.step('Open searchable Title filter editor', async () => {
		await fdsSamplePage.managementToolbar.filterButton.click();

		await fdsSamplePage.filterMenu
			.getByRole('menuitem', {name: 'Title'})
			.click();

		await fdsSamplePage.filterShowResultsOrAddButton.waitFor();

		await checkAccessibility({
			page,
			selectors: [FDS_WRAPPER_SELECTOR, OPEN_DROPDOWN_SELECTOR],
			selectorsToExclude: [FOCUS_TRAP_HIDDEN_SELECTOR],
		});

		await fdsSamplePage.filterMenu
			.getByRole('button', {name: 'Back'})
			.click();

		await page.keyboard.press('Escape');
	});

	await test.step('Apply a filter', async () => {
		await fdsSamplePage.managementToolbar.filterButton.click();

		await fdsSamplePage.filterMenu
			.getByRole('menuitem', {name: 'Status'})
			.click();

		await fdsSamplePage.filterMenu
			.getByRole('checkbox', {name: 'Approved'})
			.check();

		await fdsSamplePage.filterShowResultsOrAddButton.click();

		await fdsSamplePage.activeFiltersToolbar.container.waitFor();

		await checkAccessibility({page, selectors: [FDS_WRAPPER_SELECTOR]});
	});

	await test.step('Remove one of two active filters', async () => {
		await fdsSamplePage.activeFiltersToolbar.container
			.getByRole('button', {name: 'Remove Filter'})
			.first()
			.click();

		await checkAccessibility({page, selectors: [FDS_WRAPPER_SELECTOR]});
	});

	await test.step('Clear active filter', async () => {
		await fdsSamplePage.activeFiltersToolbar.clearButton.click();

		await fdsSamplePage.activeFiltersToolbar.container.waitFor({
			state: 'hidden',
		});

		await checkAccessibility({page, selectors: [FDS_WRAPPER_SELECTOR]});
	});
});

test('Advanced FDS is accessible during sorting interactions', async ({
	fdsSamplePage,
	page,
}) => {
	await test.step('Sort by ID column ascending', async () => {
		const idColumnHeader = page.getByRole('columnheader').getByText('ID');

		await Promise.all([
			idColumnHeader.click(),
			page.waitForResponse((response) => response.status() === 200),
		]);

		await checkAccessibility({page, selectors: [FDS_WRAPPER_SELECTOR]});
	});

	await test.step('Sort by ID column descending', async () => {
		const idColumnHeader = page.getByRole('columnheader').getByText('ID');

		await Promise.all([
			idColumnHeader.click(),
			page.waitForResponse((response) => response.status() === 200),
		]);

		await checkAccessibility({page, selectors: [FDS_WRAPPER_SELECTOR]});
	});

	await test.step('Open Order dropdown', async () => {
		await page.getByRole('button', {exact: true, name: 'Order'}).click();

		await fdsSamplePage.dropdownMenu.waitFor();

		await checkAccessibility({
			page,
			selectors: [FDS_WRAPPER_SELECTOR, OPEN_DROPDOWN_SELECTOR],
			selectorsToExclude: [FOCUS_TRAP_HIDDEN_SELECTOR],
		});

		await page.keyboard.press('Escape');
	});

	await test.step('Sort via Order dropdown', async () => {
		await page.getByRole('button', {exact: true, name: 'Order'}).click();

		await Promise.all([
			fdsSamplePage.dropdownMenu
				.getByRole('menuitem', {name: 'By Color'})
				.click(),
			page.waitForResponse((response) => response.status() === 200),
		]);

		await checkAccessibility({page, selectors: [FDS_WRAPPER_SELECTOR]});
	});
});

test('Advanced FDS is accessible across user views', async ({
	fdsSamplePage,
	page,
}) => {
	const userViewName = getRandomString();

	await test.step('Open user views menu', async () => {
		await fdsSamplePage.userViewsSelectorButton.click();

		await fdsSamplePage.dropdownMenu.waitFor();

		await checkAccessibility({
			page,
			selectors: [FDS_WRAPPER_SELECTOR, OPEN_DROPDOWN_SELECTOR],
			selectorsToExclude: [FOCUS_TRAP_HIDDEN_SELECTOR],
		});

		await page.keyboard.press('Escape');
	});

	await test.step('Open View Actions menu (Default View)', async () => {
		await fdsSamplePage.userViewsActionsButton.click();

		await fdsSamplePage.dropdownMenu.waitFor();

		await checkAccessibility({
			page,
			selectors: [FDS_WRAPPER_SELECTOR, OPEN_DROPDOWN_SELECTOR],
			selectorsToExclude: [FOCUS_TRAP_HIDDEN_SELECTOR],
		});
	});

	await test.step('Save and apply a user view', async () => {
		await fdsSamplePage.dropdownMenu
			.getByRole('menuitem', {name: 'Save View As...'})
			.click();

		await fdsSamplePage.userViewsSaveModal.waitFor();

		await checkAccessibility({
			page,
			selectors: [OPEN_MODAL_SELECTOR],
			selectorsToExclude: [FOCUS_TRAP_HIDDEN_SELECTOR],
		});

		await fdsSamplePage.userViewsSaveModal
			.getByLabel('NameRequired')
			.fill(userViewName);

		await fdsSamplePage.userViewsSaveModal
			.getByRole('button', {name: 'Save'})
			.click();

		await fdsSamplePage.userViewsSaveModal.waitFor({state: 'hidden'});

		await checkAccessibility({page, selectors: [FDS_WRAPPER_SELECTOR]});
	});

	await test.step('Open View Actions menu on a custom view', async () => {
		await fdsSamplePage.userViewsActionsButton.click();

		await fdsSamplePage.dropdownMenu.waitFor();

		await checkAccessibility({
			page,
			selectors: [FDS_WRAPPER_SELECTOR, OPEN_DROPDOWN_SELECTOR],
			selectorsToExclude: [FOCUS_TRAP_HIDDEN_SELECTOR],
		});

		await page.keyboard.press('Escape');
	});

	await test.step('Open user views menu with a saved view', async () => {
		await fdsSamplePage.userViewsSelectorButton.click();

		await fdsSamplePage.dropdownMenu.waitFor();

		await checkAccessibility({
			page,
			selectors: [FDS_WRAPPER_SELECTOR, OPEN_DROPDOWN_SELECTOR],
			selectorsToExclude: [FOCUS_TRAP_HIDDEN_SELECTOR],
		});

		await page.keyboard.press('Escape');
	});

	// Delete the saved view so it does not linger for the next run. User
	// views are persisted per user and are not removed with the isolated
	// site.

	await test.step('Delete the saved view', async () => {
		await fdsSamplePage.userViewsActionsButton.click();

		await fdsSamplePage.dropdownMenu
			.getByRole('menuitem', {name: 'Delete View'})
			.click();

		await fdsSamplePage.userViewsDeleteAlert
			.getByRole('button', {name: 'Delete'})
			.click();

		await fdsSamplePage.userViewsDeleteAlert.waitFor({state: 'hidden'});
	});
});

test('Advanced FDS is accessible during bulk selection', async ({
	fdsSamplePage,
	page,
}) => {
	const firstRowCheckbox = fdsSamplePage.table.bodyRows
		.first()
		.locator('.cell-select-item')
		.getByRole('checkbox');

	await test.step('Select a single row', async () => {
		await firstRowCheckbox.check();

		await fdsSamplePage.selectionToolbar.container.waitFor();

		await checkAccessibility({
			page,
			selectors: [FDS_WRAPPER_SELECTOR, SELECTION_TOOLBAR_SELECTOR],
		});

		await firstRowCheckbox.uncheck();
	});

	await test.step('Select all rows on the page', async () => {
		await page.locator('input[name="items-selector"]').click();

		await checkAccessibility({
			page,
			selectors: [FDS_WRAPPER_SELECTOR, SELECTION_TOOLBAR_SELECTOR],
		});
	});

	await test.step('Select all entries across pages', async () => {
		await fdsSamplePage.selectAllCheckbox.click();

		await checkAccessibility({
			page,
			selectors: [FDS_WRAPPER_SELECTOR, SELECTION_TOOLBAR_SELECTOR],
		});
	});

	await test.step('Open bulk Actions menu', async () => {
		await fdsSamplePage.bulkActions.actionsDropdownButton.click();

		await fdsSamplePage.dropdownMenu.waitFor();

		await checkAccessibility({
			page,
			selectors: [
				FDS_WRAPPER_SELECTOR,
				SELECTION_TOOLBAR_SELECTOR,
				OPEN_DROPDOWN_SELECTOR,
			],
			selectorsToExclude: [FOCUS_TRAP_HIDDEN_SELECTOR],
		});

		await page.keyboard.press('Escape');
	});

	await test.step('Clear selection from toolbar', async () => {
		await fdsSamplePage.selectionToolbar.clearButton.click();

		await fdsSamplePage.selectionToolbar.container.waitFor({
			state: 'hidden',
		});

		await checkAccessibility({page, selectors: [FDS_WRAPPER_SELECTOR]});
	});
});

test('Advanced FDS is accessible in row item actions', async ({
	fdsSamplePage,
	page,
}) => {
	await test.step('Open row item actions dropdown', async () => {
		await fdsSamplePage.itemActionsButtons.first().click();

		await fdsSamplePage.dropdownMenu.waitFor();

		await checkAccessibility({
			page,
			selectors: [FDS_WRAPPER_SELECTOR, OPEN_DROPDOWN_SELECTOR],
			selectorsToExclude: [FOCUS_TRAP_HIDDEN_SELECTOR],
		});
	});

	await test.step('Open contextual submenu', async () => {
		await fdsSamplePage.dropdownMenu
			.getByRole('menuitem', {name: 'Contextual Item'})
			.click();

		await fdsSamplePage.dropdownMenu.nth(1).waitFor();

		await checkAccessibility({
			page,
			selectors: [
				FDS_WRAPPER_SELECTOR,
				`${OPEN_DROPDOWN_SELECTOR}:not([id])`,
			],
			selectorsToExclude: [FOCUS_TRAP_HIDDEN_SELECTOR],
		});

		await page.keyboard.press('Escape');
	});
});
