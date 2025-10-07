/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {FrameLocator, Locator, Page, expect} from '@playwright/test';

import {ApiHelpers} from '../../../../../../helpers/ApiHelpers';
import {DEFAULT_LABEL} from '../../../utils/constants';
import {VisualizationMode} from '../../../utils/types';

export class DataSetFragmentPage {
	readonly activeViewSelector: Locator;
	readonly addFilterButton: Locator;
	readonly apiHelpers: ApiHelpers;
	readonly cardsWrapper: Locator;
	readonly changeDataSetButton: Locator;
	readonly creationMenuButton: Locator;
	readonly editPageButton: Locator;
	readonly emptyStateTitle: Locator;
	readonly filterButton: Locator;
	filterItem: Locator;
	readonly filterConfirmButton: Locator;
	readonly filterResumeButton: Locator;
	readonly fragmentWidgetSearchInput: Locator;
	readonly listWrapper: Locator;
	readonly loadingIndicator: Locator;
	readonly page: Page;
	readonly paginationResults: Locator;
	readonly paginationWrapper: Locator;
	readonly publishPageButton: Locator;
	readonly resetFilterButton: Locator;
	readonly selectDataSetModalFrame: FrameLocator;
	readonly selectDataSetButton: Locator;
	readonly selectedDataSetInput: Locator;
	readonly selectionListContainer: Locator;
	readonly sidePanel: Locator;
	readonly sidePanelFrame: FrameLocator;
	readonly table: {
		bodyRows: Locator;
		container: Locator;
		headRow: Locator;
		itemActionsCells: Locator;
	};

	constructor(page: Page) {
		this.activeViewSelector = page.getByLabel('Show View Options');
		this.addFilterButton = page.getByRole('button', {
			exact: true,
			name: 'Add Filter',
		});
		this.apiHelpers = new ApiHelpers(page);
		this.cardsWrapper = page.locator('.cards-container');
		this.changeDataSetButton = page.getByRole('button', {
			name: 'Change Data Set View',
		});
		this.creationMenuButton = page.getByRole('button', {name: 'New'});
		this.emptyStateTitle = page.getByText('No Results Found');

		this.filterButton = page.getByRole('button', {
			exact: true,
			name: 'Filter',
		});
		this.filterConfirmButton = page.getByRole('button', {
			name: /add filter|show results|delete filter/i,
		});
		this.filterResumeButton = page.locator('.filter-resume');
		this.fragmentWidgetSearchInput = page.getByLabel(
			'Search Fragments and Widgets'
		);
		this.listWrapper = page.locator('.list-sheet');
		this.loadingIndicator = page.locator('.fds .loading-animation');
		this.page = page;
		this.paginationResults = page.locator('.pagination-results');
		this.paginationWrapper = page.locator('.data-set-pagination-wrapper');
		this.publishPageButton = page.getByRole('button', {
			name: 'Publish',
		});
		this.resetFilterButton = page.getByRole('button', {
			exact: true,
			name: 'Reset Filters',
		});
		this.selectDataSetModalFrame = page.frameLocator(
			'iframe[title="Select"]'
		);
		this.selectDataSetButton = page.getByRole('button', {
			name: 'Select Data Set View',
		});
		this.selectedDataSetInput = page
			.getByLabel('Configuration Panel')
			.getByLabel('Data Set View', {exact: true});

		this.selectionListContainer = this.selectDataSetModalFrame.locator(
			'.fds-admin-item-selector'
		);

		this.sidePanel = page.locator('.fds-side-panel');
		this.sidePanelFrame = this.sidePanel.frameLocator('iframe');

		const tableContainer = page.locator('.fds table');

		this.table = {
			bodyRows: tableContainer.locator('tbody tr'),
			container: tableContainer,
			headRow: tableContainer.locator('thead tr'),
			itemActionsCells: tableContainer.locator('td.cell-item-actions'),
		};
	}

	async goto() {
		await this.page.goto('/');
	}

	async selectDataSet(label: string) {
		await this.page.getByRole('dialog').isVisible();

		await this.page.getByRole('heading', {name: 'Select'}).isVisible();

		await this.selectDataSetModalFrame
			.locator('.fds-admin-item-selector')
			.waitFor({state: 'visible'});

		await this.selectDataSetModalFrame
			.locator('li')
			.filter({hasText: label})
			.first()
			.click();

		await this.selectDataSetModalFrame
			.getByRole('button', {name: 'Save'})
			.click();

		await expect(this.selectedDataSetInput).toHaveValue(label);
	}

	async selectFilter(filterLabel: string) {
		await this.filterButton.waitFor({state: 'visible'});
		const filterDropdownId =
			await this.filterButton.getAttribute('aria-controls');
		await this.filterButton.click();
		await this.page
			.locator(`#${filterDropdownId}`)
			.waitFor({state: 'visible'});
		this.filterItem = this.page.locator(`#${filterDropdownId}`);
		this.filterItem
			.getByRole('menuitem', {
				name: filterLabel,
			})
			.click();
	}

	async changeVisualizationMode(visualizationMode: VisualizationMode) {
		await this.activeViewSelector.waitFor({
			state: 'visible',
		});
		await this.activeViewSelector.click();

		await this.page
			.getByRole('listbox')
			.getByRole('option', {name: visualizationMode})
			.click();
	}

	async configureDataSetFragment({
		dataSetLabel = DEFAULT_LABEL.DATA_SET,
		layout,
	}: {
		dataSetLabel?: string;
		layout: Layout;
	}) {
		await this.addDataSetFragment(layout);

		await this.selectDataSetButton.click();

		await this.selectDataSet(dataSetLabel);

		await this.publishPage();

		await this.goToPage({layout});

		await this.page
			.locator('.data-set-content-wrapper')
			.waitFor({state: 'visible'});
	}

	async configureEmptyDataSetFragment({layout}: {layout: Layout}) {
		await this.addDataSetFragment(layout);

		await this.selectDataSetModalFrame
			.locator('.c-empty-state-title')
			.waitFor({state: 'visible'});
	}

	async editPage({layout}: {layout: Layout}) {
		await this.page.goto(`/web/guest${layout.friendlyURL}?p_l_mode=edit`);
	}

	async goToPage({layout}: {layout: Layout}) {
		await this.page.goto(`/web/guest${layout.friendlyURL}`);
	}

	async publishPage() {
		await this.publishPageButton.click();

		await this.publishPageButton.isEnabled();
	}

	async searchFragmentOrWidget(itemName: string) {
		await this.fragmentWidgetSearchInput.fill(itemName);
	}

	async addDataSetFragment(layout: Layout) {
		await this.editPage({layout});

		await this.searchFragmentOrWidget('Data Set');

		const dataSetMenuItem = this.page.getByRole('menuitem', {
			exact: true,
			name: 'Data Set Add Data Set Mark Data Set as Favorite',
		});

		await dataSetMenuItem.dragTo(
			this.page.getByText('Drag and drop fragments or widgets here.')
		);

		const fragmentSelectionArea = this.page.getByText(
			'Select a data set view'
		);

		await expect(fragmentSelectionArea).toBeVisible();

		await fragmentSelectionArea.click();
	}

	async sortBy(columnName: string) {
		await Promise.all([
			this.table.headRow.locator('th', {hasText: columnName}).click(),

			this.page.waitForResponse(
				(response: any) =>
					response.status() === 200 &&
					response.url().includes('/data-set-admin/')
			),
		]);
	}
}
