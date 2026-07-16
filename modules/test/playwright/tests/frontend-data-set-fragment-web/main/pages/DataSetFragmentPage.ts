/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {FrameLocator, Locator, Page, expect} from '@playwright/test';

import {ApiHelpers} from '../../../../helpers/ApiHelpers';
import {DEFAULT_LABEL} from '../utils/constants';
import {VisualizationMode} from '../utils/types';

export class DataSetFragmentPage {
	readonly activeViewSelector: Locator;
	readonly addFilterButton: Locator;
	readonly apiHelpers: ApiHelpers;
	readonly cardsWrapper: Locator;
	readonly changeDataSetButton: Locator;
	readonly creationMenuButton: Locator;
	readonly dropdownMenu: Locator;
	readonly editPageButton: Locator;
	readonly emptyStateTitle: Locator;
	readonly filterButton: Locator;
	readonly filterConfirmButton: Locator;
	readonly filterResumeBadge: Locator;
	readonly filterResumeButton: Locator;
	readonly fragmentWidgetSearchInput: Locator;
	readonly fragmentSelectionArea: Locator;
	readonly listWrapper: Locator;
	readonly loadingIndicator: Locator;
	readonly page: Page;
	readonly paginationResults: Locator;
	readonly paginationWrapper: Locator;
	readonly publishPageButton: Locator;
	readonly removeFilterButton: Locator;
	readonly selectDataSetModal: {
		cancelButton: Locator;
		container: Locator;
		selectButton: Locator;
	};
	readonly selectDataSetButton: Locator;
	readonly selectedDataSetInput: Locator;
	readonly showResultsButton: Locator;
	readonly sidePanel: Locator;
	readonly sidePanelFrame: FrameLocator;
	readonly table: {
		bodyRows: Locator;
		container: Locator;
		headRow: Locator;
		headerCells: Locator;
		itemActionsCells: Locator;
		manageColumnsVisibilityButton: Locator;
	};
	readonly userViewsActionsButton: Locator;
	readonly userViewsDeleteAlert: Locator;
	readonly userViewsRenameModal: Locator;
	readonly userViewsSelectorButton: Locator;
	readonly userViewsSaveModal: Locator;

	// URL Token Mapping panel

	readonly tokenMappingEntityInput: Locator;
	readonly tokenMappingFieldSelect: Locator;
	readonly tokenMappingMappingSelect: Locator;
	readonly tokenMappingPanel: Locator;
	readonly tokenMappingRemoveEntityButton: Locator;
	readonly tokenMappingSelectEntityButton: Locator;
	readonly tokenMappingStatusLabel: Locator;
	readonly tokenMappingTokenSelectorTrigger: Locator;
	readonly tokenMappingTokenStatusLabel: Locator;
	readonly tokenMappingValueInput: Locator;

	// Unresolved API URL preview rendered by the fragment in edit mode

	readonly unresolvedPreview: {
		alert: Locator;
		container: Locator;
		skeletonBars: Locator;
		urlBox: Locator;
	};

	constructor(page: Page) {
		this.activeViewSelector = page.getByLabel(/View Selected/);
		this.addFilterButton = page.getByRole('button', {
			exact: true,
			name: 'Add Filter',
		});
		this.apiHelpers = new ApiHelpers(page);
		this.cardsWrapper = page.locator('.cards-container');
		this.changeDataSetButton = page.getByRole('button', {
			name: 'Change Data Set',
		});
		this.creationMenuButton = page.getByRole('button', {name: 'New'});
		this.dropdownMenu = page.locator('.dropdown-menu.show');
		this.emptyStateTitle = page.getByText('No Results Found');

		this.filterButton = page.getByRole('button', {
			exact: true,
			name: 'Filter',
		});
		this.filterConfirmButton = page.getByRole('button', {
			name: /add filter|show results|delete filter/i,
		});
		this.filterResumeBadge = page.locator('.filter-resume-badge');
		this.filterResumeButton = page.locator('.filter-resume');
		this.fragmentSelectionArea = page.getByText('Select a data set');
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
		this.removeFilterButton = page.getByRole('button', {
			exact: true,
			name: 'Remove Filter',
		});

		const selectDataSetModalContainer = page
			.locator('.modal')
			.filter({hasText: 'Select Data Set'});

		this.selectDataSetModal = {
			cancelButton: selectDataSetModalContainer.getByRole('button', {
				name: 'Cancel',
			}),
			container: selectDataSetModalContainer,
			selectButton: selectDataSetModalContainer.getByRole('button', {
				name: 'Select',
			}),
		};

		this.selectDataSetButton = page.getByRole('button', {
			name: 'Select Data Set',
		});
		this.selectedDataSetInput = page.getByPlaceholder(
			'No Data Set Selected'
		);

		this.showResultsButton = page.getByRole('button', {
			exact: true,
			name: 'Show Results',
		});
		this.sidePanel = page.locator('.fds-side-panel');
		this.sidePanelFrame = this.sidePanel.frameLocator('iframe');

		this.tokenMappingPanel = page
			.locator('.panel.w-100')
			.filter({hasText: 'URL Token Mapping'});

		this.tokenMappingEntityInput =
			this.tokenMappingPanel.getByPlaceholder('No Entity Selected');
		this.tokenMappingFieldSelect = this.tokenMappingPanel.getByLabel(
			'Field',
			{exact: true}
		);
		this.tokenMappingMappingSelect = this.tokenMappingPanel.getByLabel(
			'Mapping',
			{exact: true}
		);
		this.tokenMappingRemoveEntityButton = this.tokenMappingPanel.getByRole(
			'button',
			{
				name: 'Remove Entity',
			}
		);
		this.tokenMappingSelectEntityButton = this.tokenMappingPanel.getByRole(
			'button',
			{name: /Select Entity|Change Entity/}
		);
		this.tokenMappingStatusLabel = this.tokenMappingPanel
			.locator('.form-group')
			.filter({hasText: 'Status'})
			.locator('.label');
		this.tokenMappingTokenSelectorTrigger =
			this.tokenMappingPanel.getByRole('button', {
				exact: true,
				name: 'Token',
			});
		this.tokenMappingTokenStatusLabel =
			this.tokenMappingTokenSelectorTrigger.locator('.label');
		this.tokenMappingValueInput = this.tokenMappingPanel.getByLabel(
			'Token Value',
			{exact: true}
		);

		const unresolvedPreviewContainer = page.locator(
			'.unresolved-data-set-preview'
		);

		this.unresolvedPreview = {
			alert: unresolvedPreviewContainer.locator('.alert-info'),
			container: unresolvedPreviewContainer,
			skeletonBars: unresolvedPreviewContainer.locator(
				'.data-set-skeleton-bar'
			),
			urlBox: unresolvedPreviewContainer.locator('.text-break'),
		};

		const tableContainer = page.locator('.fds table');

		this.table = {
			bodyRows: tableContainer.locator('tbody tr'),
			container: tableContainer,
			headRow: tableContainer.locator('thead tr'),
			headerCells: tableContainer.locator('thead th'),
			itemActionsCells: tableContainer.locator('td.cell-item-actions'),
			manageColumnsVisibilityButton: tableContainer.getByTitle(
				'Manage Columns Visibility'
			),
		};

		this.userViewsActionsButton = page.getByLabel('Show View Actions');
		this.userViewsDeleteAlert = page.getByRole('dialog', {
			name: 'Delete View',
		});
		this.userViewsRenameModal = page.getByRole('dialog', {
			name: 'Rename View',
		});
		this.userViewsSelectorButton = page.getByLabel('Views');
		this.userViewsSaveModal = page.getByRole('dialog', {
			name: 'Save New View As',
		});
	}

	async goto() {
		await this.page.goto('/');
	}

	async selectDataSet(label: string) {
		await this.page.getByRole('dialog').isVisible();

		await this.page.getByRole('heading', {name: 'Select'}).isVisible();

		await this.selectDataSetModal.container
			.locator('li')
			.filter({hasText: label})
			.locator('input')
			.click();

		await this.selectDataSetModal.selectButton.click();

		await expect(this.selectedDataSetInput).toHaveValue(label);
	}

	async selectFilter(filterLabel: string) {
		await this.filterButton.waitFor({state: 'visible'});
		await this.filterButton.click();

		await this.dropdownMenu
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

		await this.selectDataSetModal.container
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

		await expect(this.fragmentSelectionArea).toBeVisible();

		await this.fragmentSelectionArea.click();
	}

	async fillTokenValue(value: string) {
		await this.tokenMappingValueInput.fill(value);

		await this.tokenMappingValueInput.blur();
	}

	async selectEntity(entryTitle: string) {
		await this.tokenMappingSelectEntityButton.click();

		const iframe = this.page.frameLocator(
			'iframe[title="Select an Entity"]'
		);

		await iframe
			.getByRole('menuitem', {exact: true, name: 'Web Content'})
			.click();

		await iframe.getByText(entryTitle).first().click();

		await expect(this.tokenMappingEntityInput).toHaveValue(entryTitle);
	}

	async selectMappingMode(label: string) {
		await this.tokenMappingMappingSelect.selectOption({label});
	}

	async selectToken(tokenName: string) {
		await this.tokenMappingTokenSelectorTrigger.click();

		await this.page.getByRole('menuitem', {name: `{${tokenName}}`}).click();

		await expect(this.tokenMappingMappingSelect).toBeVisible();
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
