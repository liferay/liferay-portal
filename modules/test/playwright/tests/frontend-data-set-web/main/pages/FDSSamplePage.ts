/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {FrameLocator, Locator, Page, expect} from '@playwright/test';

import {ApiHelpers} from '../../../../helpers/ApiHelpers';
import {liferayConfig} from '../../../../liferay.config';
import {clickAndExpectToBeVisible} from '../../../../utils/clickAndExpectToBeVisible';
import getRandomString from '../../../../utils/getRandomString';
import {EFDSVisualizationMode, waitForFDS} from '../../../../utils/waitFor';
import getFragmentDefinition from '../../../layout-content-page-editor-web/main/utils/getFragmentDefinition';
import getPageDefinition from '../../../layout-content-page-editor-web/main/utils/getPageDefinition';
import getWidgetDefinition from '../../../layout-content-page-editor-web/main/utils/getWidgetDefinition';

export class FDSSamplePage {
	readonly activeFiltersToolbar: {
		clearButton: Locator;
		clearSearchButton: Locator;
		container: Locator;
		searchResume: Locator;
	};
	private readonly apiHelpers: ApiHelpers;
	readonly bulkActions: {
		actionsDropdownButton: Locator;
		container: Locator;
	};
	readonly cards: {
		container: Locator;
		itemActionButtons: Locator;
		items: Locator;
	};
	readonly creatorFilterSearchInput: Locator;
	readonly dropdownMenu: Locator;
	readonly emptyStateContainer: Locator;
	readonly fdsWrapper: Locator;
	readonly fileDropModal: Locator;
	readonly filterDeleteButton: Locator;
	readonly filterDropdownMenu: Locator;
	readonly filterExcludeToggle: Locator;
	readonly filterMenu: Locator;
	readonly filterMenuSearchInput: Locator;
	readonly filterShowResultsOrAddButton: Locator;
	readonly infoPanel: Locator;
	readonly itemActionsButtons: Locator;
	readonly list: {
		container: Locator;
		itemActionButtons: Locator;
		items: Locator;
	};
	readonly managementToolbar: {
		container: Locator;
		filterButton: Locator;
		searchButton: Locator;
		searchInput: Locator;
	};
	readonly page: Page;
	readonly paginator: {
		itemsPerPageSelector: Locator;
	};
	readonly resubmitButton: Locator;
	readonly sidePanel: Locator;
	readonly sidePanelFrame: FrameLocator;
	readonly selectAllCheckbox: Locator;
	readonly selectionToolbar: {
		clearButton: Locator;
		container: Locator;
	};
	readonly tablist: Locator;
	readonly table: {
		bodyRows: Locator;
		container: Locator;
		firstColumnHeader: Locator;
		headerCells: Locator;
		itemActionButtons: Locator;
		manageColumnsVisibilityButton: Locator;
	};
	readonly toggleInfoPanelButton: Locator;
	readonly userViewsActionsButton: Locator;
	readonly userViewsDeleteAlert: Locator;
	readonly userViewsRenameModal: Locator;
	readonly userViewsSaveModal: Locator;
	readonly userViewsSelectorButton: Locator;
	readonly visualizationModeSelector: Locator;

	constructor(page: Page) {
		const activeFiltersToolbarContainer: Locator = page.getByTestId(
			'activeFiltersToolbar'
		);

		const searchResume =
			activeFiltersToolbarContainer.locator('.search-resume');
		this.activeFiltersToolbar = {
			clearButton: activeFiltersToolbarContainer.getByRole('button', {
				exact: true,
				name: 'Clear',
			}),
			clearSearchButton: searchResume.getByRole('button', {
				exact: true,
				name: 'Clear Search',
			}),
			container: activeFiltersToolbarContainer,
			searchResume,
		};
		this.apiHelpers = new ApiHelpers(page);
		this.bulkActions = {
			actionsDropdownButton: page
				.locator('.bulk-actions')
				.getByLabel('Actions'),
			container: page.locator('.bulk-actions'),
		};

		const cardsContainer = page.locator('.cards-container');

		const cardItems = cardsContainer.locator('.card');

		this.cards = {
			container: cardsContainer,
			itemActionButtons: cardItems.getByLabel('Actions'),
			items: cardItems,
		};
		this.creatorFilterSearchInput = page
			.locator('.data-set-filter')
			.getByRole('textbox', {name: 'Search'});
		this.dropdownMenu = page.locator('.dropdown-menu.show');
		this.emptyStateContainer = page.locator('.fds .c-empty-state');
		this.fdsWrapper = page.locator('div.data-set-wrapper').first();
		this.fileDropModal = page.getByRole('dialog', {
			name: 'Custom dummy file uploader',
		});
		this.filterDropdownMenu = page.locator('.data-set-filter');
		this.filterExcludeToggle =
			this.filterDropdownMenu.getByLabel('Exclude');
		this.filterMenu = page.locator('.dropdown-menu');
		this.filterDeleteButton = this.filterMenu.getByRole('button', {
			name: 'Delete Filter',
		});
		this.filterMenuSearchInput = this.filterMenu
			.getByLabel('Search')
			.first();
		this.filterShowResultsOrAddButton = this.filterMenu
			.getByRole('button', {name: 'Show Results'})
			.or(this.filterMenu.getByRole('button', {name: 'Add Filter'}));
		this.infoPanel = page.locator('.fds-info-panel');

		this.itemActionsButtons = page.locator(
			'button.dropdown-toggle.component-action.btn-unstyled'
		);

		const listContainer = page.locator('.fds .list-sheet');

		const listItems = listContainer.locator('.list-group-item');

		this.list = {
			container: listContainer,
			itemActionButtons: listItems.getByRole('button', {
				name: 'Actions',
			}),
			items: listItems,
		};

		const managementToolbarContainer =
			page.getByTestId('managementToolbar');

		this.managementToolbar = {
			container: managementToolbarContainer,
			filterButton: managementToolbarContainer.getByRole('button', {
				name: 'Filter',
			}),
			searchButton: managementToolbarContainer.getByRole('button', {
				name: 'Search',
			}),
			searchInput: managementToolbarContainer.locator(
				'input[type="search"]'
			),
		};

		this.page = page;

		this.paginator = {
			itemsPerPageSelector: page.getByLabel('Items Per Page', {
				exact: true,
			}),
		};

		this.resubmitButton = page.getByRole('button', {name: 'Resubmit'});

		this.selectAllCheckbox = page.getByText('Select All');

		const selectionToolbarContainer = page.getByTestId('selectionToolbar');

		this.selectionToolbar = {
			clearButton: selectionToolbarContainer.getByText('Clear'),
			container: selectionToolbarContainer,
		};

		this.sidePanel = page.locator('.fds-side-panel');
		this.sidePanelFrame = this.sidePanel.frameLocator('iframe');
		this.tablist = page.getByRole('tablist');

		const tableContainer = page.locator('.fds table');

		const headerCells = tableContainer.locator('th');

		this.table = {
			bodyRows: tableContainer.locator('tbody tr'),
			container: tableContainer,
			firstColumnHeader: headerCells.nth(1),
			headerCells,
			itemActionButtons: tableContainer
				.locator('.cell-item-actions')
				.getByRole('button', {
					name: 'Actions',
				}),
			manageColumnsVisibilityButton: tableContainer.getByTitle(
				'Manage Columns Visibility'
			),
		};

		this.toggleInfoPanelButton = page
			.getByLabel('Show Info Panel')
			.or(page.getByLabel('Hide Info Panel'));

		this.userViewsActionsButton = page.getByLabel('Show View Actions', {
			exact: true,
		});
		this.userViewsDeleteAlert = page.getByRole('dialog', {
			name: 'Delete View',
		});
		this.userViewsRenameModal = page.getByRole('dialog', {
			name: 'Rename View',
		});
		this.userViewsSaveModal = page.getByRole('dialog', {
			name: 'Save New View As',
		});
		this.userViewsSelectorButton = page.getByLabel('Views', {
			exact: true,
		});

		this.visualizationModeSelector = page.getByLabel(/View Selected/);
	}

	async assignTaskToMe() {
		const assignToMeButton = this.page
			.locator('.fds-roles-tasks')
			.getByLabel('Assign to Me');

		await assignToMeButton.click();
	}

	async changeItemsPerPage({delta}: {delta: string}) {
		await this.paginator.itemsPerPageSelector.click();

		const dropdownItem = this.page.getByRole('option', {name: delta});

		await dropdownItem.waitFor({state: 'visible'});

		await dropdownItem.click();
	}

	async changePage(pageNumber: number) {
		const button = this.page.getByLabel(`Go to page, ${pageNumber}`);

		await clickAndExpectToBeVisible({
			target: this.page
				.locator('.page-item.active')
				.filter({has: button}),
			trigger: button,
		});

		await this.page.getByLabel(`Go to page, ${pageNumber}`).click();
	}

	async changeVisualizationMode({
		page,
		visualizationMode,
	}: {
		page: Page;
		visualizationMode: EFDSVisualizationMode;
	}) {
		await this.visualizationModeSelector.waitFor({
			state: 'visible',
		});

		await this.visualizationModeSelector.click();

		await this.page
			.getByRole('listbox')
			.getByRole('option', {name: visualizationMode})
			.click();

		await waitForFDS({page, visualizationMode});
	}

	async checkDropdownMenuIconsAreVisible() {
		const menuItems = this.dropdownMenu.getByRole('menuitem');

		for (const menuItem of await menuItems.all()) {
			await expect
				.soft(menuItem.locator('.lexicon-icon').first())
				.toBeVisible();
		}

		await this.page.keyboard.press('Escape');
	}

	async clickItemAction(action: string, item: number = 0) {
		await this.itemActionsButtons.nth(item).click();

		await this.dropdownMenu
			.getByRole('menuitem', {
				exact: true,
				name: action,
			})
			.click();
	}

	async fillAndSaveWorkflowModal({
		comment,
		name,
	}: {
		comment: string;
		name: string;
	}) {
		const workflowModal = this.page.getByRole('dialog', {name});

		await workflowModal.waitFor({state: 'visible'});

		await this.page.getByRole('textbox', {name: 'Comment'}).fill(comment);

		await workflowModal.getByRole('button', {name: 'Save'}).click();

		await workflowModal.waitFor({state: 'hidden'});
	}

	getFilterItemCheckbox(label: string) {
		return this.filterDropdownMenu.getByRole('checkbox', {name: label});
	}

	getFilterRemoveButton(label: string) {
		return this.activeFiltersToolbar.container
			.getByRole('group')
			.filter({hasText: `${label}:`})
			.getByRole('button', {name: 'Remove Filter'});
	}

	getFilterSummaryButton(label: string) {
		return this.activeFiltersToolbar.container
			.getByRole('button')
			.filter({hasText: new RegExp(`^${label}:`)});
	}

	async search(value: string) {
		await this.managementToolbar.searchInput.fill(value);

		await this.managementToolbar.searchInput.press('Enter');
	}

	selectItemActionsByRow(text: string) {
		return this.table.bodyRows
			.filter({
				hasText: text,
			})
			.locator('.cell-item-actions')
			.getByRole('button', {
				name: 'Actions',
			});
	}

	async selectByRowAndCell({
		cell = 0,
		filter,
		row = 0,
	}: {
		cell?: number;
		filter?: string;
		row?: number;
	}) {
		if (filter) {
			await this.table.bodyRows
				.nth(row)
				.locator('td')
				.filter({hasText: filter})
				.click();
		}
		else {
			await this.table.bodyRows.nth(row).locator('td').nth(cell).click();
		}
	}

	async selectByRowAndRole({
		filter,
		role = 'checkbox',
		row = 0,
	}: {
		filter?: string;
		role?: any;
		row?: number;
	} = {}) {
		if (filter) {
			await this.table.bodyRows
				.nth(row)
				.locator('td')
				.getByRole(role)
				.filter({hasText: filter})
				.click();
		}
		else {
			await this.table.bodyRows
				.nth(row)
				.locator('td')
				.getByRole(role)
				.first()
				.click();
		}
	}

	async selectTab(label: string) {
		const navLink = this.page.locator('.nav-link').filter({hasText: label});

		await navLink.click();

		await expect(navLink).toHaveClass(/active/);
	}

	async setupFDSSampleWidget({
		fragmentKeys = [],
		locale = 'en',
		site,
	}: {
		fragmentKeys?: Array<string>;
		locale?: string;
		site: Site;
	}) {
		const layout = await this.apiHelpers.headlessDelivery.createSitePage({
			pageDefinition: getPageDefinition([
				...fragmentKeys.map((fragmentKey) =>
					getFragmentDefinition({
						id: getRandomString(),
						key: fragmentKey,
					})
				),
				getWidgetDefinition({
					id: getRandomString(),
					widgetName:
						'com_liferay_frontend_data_set_sample_web_internal_portlet_FDSSamplePortlet',
				}),
			]),
			siteId: site.id,
			title: getRandomString(),
		});

		const url = `${liferayConfig.environment.baseUrl}/${locale}/web${site.friendlyUrlPath}${layout.friendlyUrlPath}`;

		await this.page.goto(url);

		return {layout, url};
	}
}
