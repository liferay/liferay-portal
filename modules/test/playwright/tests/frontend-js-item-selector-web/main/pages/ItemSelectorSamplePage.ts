/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Locator, Page} from '@playwright/test';

import {EFDSVisualizationMode, waitForFDS} from '../../../../utils/waitFor';

export class ItemSelectorSamplePage {
	readonly fdsContentContainer: Locator;
	readonly fragmentWidgetSearchInput: Locator;
	readonly inputGroup: (label: string) => Locator;
	readonly modal: {
		cancelButton: Locator;
		selectButton: Locator;
	};
	readonly multiselectGridItem: (name: string) => Locator;
	readonly page: Page;
	readonly filtersButton: Locator;
	readonly jsUtilityButton: Locator;
	readonly publishPageButton: Locator;
	readonly samplePageHeader: Locator;
	readonly selectCMSFileButton: Locator;
	readonly selectCMSFileModalHeader: Locator;
	readonly selectDocumentButton: Locator;
	readonly selectDocumentModalHeader: Locator;
	readonly selectUserButton: Locator;
	readonly selectUserModalHeader: Locator;
	readonly table: {
		bodyRows: Locator;
		container: Locator;
	};
	readonly visualizationModeSelector: Locator;

	constructor(page: Page) {
		this.fdsContentContainer = page.locator(
			'.fds .data-set-content-wrapper'
		);
		this.fragmentWidgetSearchInput = page.getByLabel(
			'Search Fragments and Widgets'
		);
		this.inputGroup = (label: string) =>
			page.getByText(label).locator('..');
		this.modal = {
			cancelButton: page.getByRole('button', {
				exact: true,
				name: 'Cancel',
			}),
			selectButton: page.getByRole('button', {
				exact: true,
				name: 'Select',
			}),
		};
		this.multiselectGridItem = (name: string) =>
			page.getByRole('gridcell', {
				exact: true,
				name,
			});
		this.page = page;
		this.filtersButton = page.getByRole('button', {
			name: 'Filter',
		});
		this.jsUtilityButton = page.getByRole('button', {
			exact: true,
			name: 'Open Modal With JS Utility',
		});
		this.publishPageButton = page.getByRole('button', {
			name: 'Publish',
		});
		this.samplePageHeader = page.getByRole('heading', {
			exact: true,
			name: 'Item Selector Samples',
		});
		this.selectCMSFileButton = page.getByRole('button', {
			exact: true,
			name: 'Select CMS Files',
		});
		this.selectCMSFileModalHeader = page.getByRole('heading', {
			exact: true,
			name: 'Select Files',
		});
		this.selectDocumentButton = page.getByRole('button', {
			exact: true,
			name: 'Select Documents',
		});
		this.selectDocumentModalHeader = page.getByRole('heading', {
			exact: true,
			name: 'Select Documents',
		});
		this.selectUserButton = page.getByRole('button', {
			exact: true,
			name: 'Select User',
		});
		this.selectUserModalHeader = page.getByRole('heading', {
			exact: true,
			name: 'Select User',
		});

		const tableContainer = page.locator('.fds table');

		this.table = {
			bodyRows: tableContainer.locator('tbody tr'),
			container: tableContainer,
		};

		this.visualizationModeSelector = page.getByLabel('Show View Options');
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

	async goToPage({layout, site}: {layout: Layout; site: Site}) {
		await this.page.goto(
			`/web${site.friendlyUrlPath}${layout.friendlyUrlPath || layout.friendlyURL}`
		);
	}

	async publishPage() {
		await this.publishPageButton.click();

		await this.publishPageButton.isEnabled();
	}

	async selectByRowAndRole({
		filter,
		role = 'radio',
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
}
