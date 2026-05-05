/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Locator, Page, expect} from '@playwright/test';

import {clickAndExpectToBeVisible} from '../../../../utils/clickAndExpectToBeVisible';
import {expectToPass} from '../../../../utils/expectToPass';

export class DataSetPage {
	readonly activeViewSelector: Locator;
	readonly assetLink: (assetName: string) => Locator;
	readonly loading: Locator;
	readonly page: Page;
	readonly searchInput: Locator;
	readonly table: {
		bodyRows: Locator;
		container: Locator;
		headRow: Locator;
	};
	readonly selectAllLink: Locator;

	constructor(page: Page) {
		this.activeViewSelector = page.getByLabel(/View Selected/);
		this.assetLink = (assetName) => {
			return page.getByRole('link', {
				exact: true,
				name: assetName,
			});
		};

		const tableContainer = page.locator('.fds table');
		this.table = {
			bodyRows: tableContainer.locator('tbody tr'),
			container: tableContainer,
			headRow: tableContainer.locator('thead tr'),
		};
		this.loading = page.locator('.data-set .loading-animation');
		this.page = page;
		this.searchInput = this.page.getByPlaceholder('Search');
		this.selectAllLink = page.getByRole('button', {
			exact: true,
			name: 'Select All',
		});
	}

	getRow(filter: string) {
		return this.table.bodyRows.filter({hasText: filter});
	}

	async execBulkItemAction({action}) {
		await this.page
			.getByTestId('visualization-mode-table')
			.getByLabel('Actions')
			.click();

		const dropdownMenuItemDelete = this.page.getByRole('menuitem', {
			name: action,
		});

		await expect(dropdownMenuItemDelete).toBeVisible();

		await dropdownMenuItemDelete.click();
	}

	async execItemAction({
		action,
		filter,
		parentAction,
		timeout,
	}: {
		action: string;
		filter: string;
		parentAction?: string;
		timeout?: number;
	}) {
		const item = this.getRow(filter);

		const button = item.getByRole('button', {
			name: `${filter} Actions`,
		});

		if (parentAction) {
			await button.click();

			await this.page
				.getByRole('menuitem', {exact: true, name: parentAction})
				.hover();

			await this.page
				.getByRole('menuitem', {exact: true, name: action})
				.click();

			return;
		}

		await clickAndExpectToBeVisible({
			autoClick: true,
			target: this.page.getByRole('menuitem', {
				exact: true,
				name: action,
			}),
			timeout,
			trigger: button,
		});
	}

	async changeVisualizationMode(
		visualizationMode: 'Cards' | 'Table' | 'Gallery'
	) {
		await this.activeViewSelector.waitFor({
			state: 'visible',
		});
		await this.activeViewSelector.click();

		await this.page
			.getByRole('listbox')
			.getByRole('option', {name: visualizationMode})
			.click();
	}

	async search(value: string) {
		await expectToPass(
			async () => {
				await this.searchInput.fill(value);

				await this.searchInput.press('Enter');

				await this.page
					.locator('.search-resume-label', {
						has: this.page.locator('strong', {hasText: value}),
					})
					.waitFor();

				await this.loading.waitFor({state: 'hidden'});
			},
			{timeout: 8000}
		);
	}

	async selectAll() {
		await clickAndExpectToBeVisible({
			target: this.page.getByText('All Selected'),
			trigger: this.page.getByTitle('Select Items'),
		});
	}
}
