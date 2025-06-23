/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {FrameLocator, Locator, Page, expect} from '@playwright/test';

import {ApiHelpers} from '../../../../helpers/ApiHelpers';
import {liferayConfig} from '../../../../liferay.config';
import getRandomString from '../../../../utils/getRandomString';
import getPageDefinition from '../../../layout-content-page-editor-web/main/utils/getPageDefinition';
import getWidgetDefinition from '../../../layout-content-page-editor-web/main/utils/getWidgetDefinition';

export class FDSSamplePage {
	private readonly apiHelpers: ApiHelpers;
	readonly bulkActions: {
		actionsDropdownButton: Locator;
		container: Locator;
	};
	readonly customViewsActionsButton: Locator;
	readonly customViewsDeleteAlert: Locator;
	readonly customViewsSaveModal: Locator;
	readonly customViewsSelectorButton: Locator;
	readonly infoPanel: Locator;
	readonly list: {
		container: Locator;
		items: Locator;
	};
	readonly managementToolbar: Locator;
	readonly page: Page;
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
		itemActionsCells: Locator;
		manageColumnsVisibilityButton: Locator;
	};
	readonly toggleInfoPanelButton: Locator;
	readonly visualizationModeSelector: Locator;

	constructor(page: Page) {
		this.apiHelpers = new ApiHelpers(page);
		this.bulkActions = {
			actionsDropdownButton: page
				.locator('.bulk-actions')
				.getByLabel('Actions'),
			container: page.locator('.bulk-actions'),
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
		this.infoPanel = page.locator('.fds-info-panel');

		const listContainer = page.locator('.fds .list-sheet');

		this.list = {
			container: listContainer,
			items: listContainer.locator('.list-group-item'),
		};

		this.managementToolbar = page.getByTestId('managementToolbar');
		this.page = page;
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
			itemActionsCells: tableContainer.locator('.cell-item-actions'),
			manageColumnsVisibilityButton: tableContainer.getByTitle(
				'Manage Columns Visibility'
			),
		};

		this.toggleInfoPanelButton = page.getByLabel('Toggle Info Panel');

		this.visualizationModeSelector = page.getByLabel('Show View Options');
	}

	async changeVisualizationMode(
		visualizationMode: 'Cards' | 'List' | 'Table'
	) {
		await this.visualizationModeSelector.waitFor({
			state: 'visible',
		});

		await this.visualizationModeSelector.click();

		await this.page
			.getByRole('listbox')
			.getByRole('option', {name: visualizationMode})
			.click();
	}

	async clickItemAction(itemAction: string) {
		const firstItemActionsCell = this.table.itemActionsCells.first();

		const firstItemActionButton = firstItemActionsCell.getByRole('button', {
			exact: true,
			name: 'Actions',
		});

		const dropdownId =
			await firstItemActionButton.getAttribute('aria-controls');

		await firstItemActionButton.click();

		await this.page
			.locator(`#${dropdownId}`)
			.filter({has: this.page.getByRole('menu')})
			.waitFor();

		await this.page
			.locator(`#${dropdownId}`)
			.getByRole('menuitem', {
				exact: true,
				name: itemAction,
			})
			.click();
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
