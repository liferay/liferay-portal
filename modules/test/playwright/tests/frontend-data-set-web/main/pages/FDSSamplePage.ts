/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {FrameLocator, Locator, Page, expect} from '@playwright/test';

import {ApiHelpers} from '../../../../helpers/ApiHelpers';
import {liferayConfig} from '../../../../liferay.config';
import getRandomString from '../../../../utils/getRandomString';
import {EFDSVisualizationMode, waitForFDS} from '../../../../utils/waitFor';
import getPageDefinition from '../../../layout-content-page-editor-web/main/utils/getPageDefinition';
import getWidgetDefinition from '../../../layout-content-page-editor-web/main/utils/getWidgetDefinition';

export class FDSSamplePage {
	readonly activeFiltersToolbar: Locator;
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
	readonly customViewsActionsButton: Locator;
	readonly customViewsDeleteAlert: Locator;
	readonly customViewsSaveModal: Locator;
	readonly customViewsSelectorButton: Locator;
	readonly emptyStateContainer: Locator;
	readonly fdsWrapper: Locator;
	readonly fileDropModal: Locator;
	readonly infoPanel: Locator;
	readonly itemActionButton: Locator;
	readonly itemActionsButtons: Locator;
	readonly list: {
		container: Locator;
		itemActionButtons: Locator;
		items: Locator;
	};
	readonly managementToolbar: {
		container: Locator;
		searchButton: Locator;
		searchInput: Locator;
	};
	readonly page: Page;
	readonly paginator: {
		itemsPerPageSelector: Locator;
	};
	readonly showViewOptionsButton: Locator;
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
	readonly visualizationModeSelector: Locator;

	constructor(page: Page) {
		this.activeFiltersToolbar = page.getByTestId('activeFiltersToolbar');
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
			itemActionButtons: cardItems.getByLabel('More actions'),
			items: cardItems,
		};
		this.customViewsActionsButton = page.getByLabel('Show View Actions', {
			exact: true,
		});
		this.customViewsDeleteAlert = page.getByRole('dialog', {
			name: 'Delete View',
		});
		this.customViewsSaveModal = page.getByRole('dialog', {
			name: 'Save New View As',
		});
		this.customViewsSelectorButton = page.getByLabel('Views', {
			exact: true,
		});
		this.emptyStateContainer = page.locator('.fds .c-empty-state');
		this.fdsWrapper = page.locator('div.data-set-wrapper').first();
		this.fileDropModal = page.getByRole('dialog', {
			name: 'Custom dummy file uploader',
		});
		this.infoPanel = page.locator('.fds-info-panel');

		this.itemActionsButtons = page.locator(
			'button.dropdown-toggle.component-action.btn-unstyled'
		);

		const listContainer = page.locator('.fds .list-sheet');

		const listItems = listContainer.locator('.list-group-item');

		this.list = {
			container: listContainer,
			itemActionButtons: listItems.getByRole('button', {
				exact: true,
				name: 'Actions',
			}),
			items: listItems,
		};

		const managementToolbarContainer =
			page.getByTestId('managementToolbar');

		this.managementToolbar = {
			container: managementToolbarContainer,
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

		this.selectAllCheckbox = page.getByText('Select All');

		const selectionToolbarContainer = page.getByTestId('selectionToolbar');

		this.selectionToolbar = {
			clearButton: selectionToolbarContainer.getByText('Clear'),
			container: selectionToolbarContainer,
		};

		this.showViewOptionsButton = page.getByLabel('Show View Options', {
			exact: true,
		});
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
					exact: true,
					name: 'Actions',
				}),
			manageColumnsVisibilityButton: tableContainer.getByTitle(
				'Manage Columns Visibility'
			),
		};

		this.toggleInfoPanelButton = page.getByLabel('Toggle Info Panel');

		this.visualizationModeSelector = page.getByLabel('Show View Options');
	}

	async changeItemsPerPage({delta}: {delta: string}) {
		await this.paginator.itemsPerPageSelector.click();

		const dropdownItem = this.page.getByRole('option', {name: delta});

		await dropdownItem.waitFor({state: 'visible'});

		await dropdownItem.click();
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

	async checkDropdownMenuIconsAreVisible(itemActionButton: Locator) {
		const dropdownMenu = await this.getDropdownId(itemActionButton);

		const menuItems = dropdownMenu.getByRole('menuitem');

		for (const menuItem of await menuItems.all()) {
			await expect.soft(menuItem.locator('.lexicon-icon')).toBeVisible();
		}

		await this.page.keyboard.press('Escape');
	}

	async clickItemAction(action: string, item: number = 0) {
		const dropdownId = await this.itemActionsButtons
			.nth(item)
			.getAttribute('aria-controls');

		await this.itemActionsButtons.nth(item).click();

		await this.page
			.locator(`#${dropdownId}`)
			.filter({has: this.page.getByRole('menu')})
			.waitFor();

		await this.page
			.locator(`#${dropdownId}`)
			.getByRole('menuitem', {
				exact: true,
				name: action,
			})
			.click();
	}

	async getDropdownId(itemActionButton: Locator) {
		await itemActionButton.click();

		const dropdownId = await itemActionButton.getAttribute('aria-controls');

		const dropdownMenu = this.page.locator(`#${dropdownId}`);

		await dropdownMenu.filter({has: this.page.getByRole('menu')}).waitFor();

		return dropdownMenu;
	}

	selectItemActionsByRow(text: string) {
		return this.table.bodyRows
			.filter({
				hasText: text,
			})
			.locator('.cell-item-actions')
			.getByRole('button', {
				exact: true,
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

	async setupFDSSampleWidget({locale = 'en', site}) {
		const widgetDefinition = getWidgetDefinition({
			id: getRandomString(),
			widgetName:
				'com_liferay_frontend_data_set_sample_web_internal_portlet_FDSSamplePortlet',
		});

		const layout = await this.apiHelpers.headlessDelivery.createSitePage({
			pageDefinition: getPageDefinition([widgetDefinition]),
			siteId: site.id,
			title: getRandomString(),
		});

		const url = `${liferayConfig.environment.baseUrl}/${locale}/web${site.friendlyUrlPath}${layout.friendlyUrlPath}`;

		await this.page.goto(url);

		return {layout, url};
	}
}
