/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Locator, Page} from '@playwright/test';

import {applyFDSSelectionFilter} from '../../utils/applyFDSSelectionFilter';
import {waitForFDS} from '../../utils/waitFor';

export abstract class FDSTablePage {
	readonly dataSet: Locator;
	readonly orderButton: Locator;
	readonly page: Page;
	readonly searchInput: Locator;
	readonly table: Locator;

	constructor(page: Page) {
		this.page = page;
		this.dataSet = page.locator('.fds');
		this.table = this.dataSet.locator('table');
		this.searchInput = this.dataSet.getByRole('searchbox', {
			name: 'Search',
		});
		this.orderButton = this.dataSet.getByRole('button', {
			exact: true,
			name: 'Order',
		});
	}

	async applySelectionFilter(filter: string, value: string) {
		await applyFDSSelectionFilter(this.page, {filter, value});
	}

	async clickAction(name: string, action: string) {
		await this.openActionsMenu(name);

		await this.page
			.getByRole('menuitem', {exact: true, name: action})
			.click();
	}

	columnHeader(label: string): Locator {
		return this.table.getByRole('columnheader', {name: label});
	}

	abstract goto(): Promise<void>;

	get dialog(): Locator {
		return this.page.getByRole('dialog');
	}

	async openActionsMenu(name: string) {
		await this.row(name)
			.locator('.cell-item-actions')
			.getByRole('button', {name: 'Actions'})
			.click();
	}

	row(name: string): Locator {
		return this.table.locator('tbody tr').filter({hasText: name});
	}

	get rows(): Locator {
		return this.table.locator('tbody tr');
	}

	async search(term: string) {
		await this.searchInput.fill(term);
		await this.page.keyboard.press('Enter');
	}

	sortOption(label: string): Locator {
		return this.page.getByRole('menuitem', {exact: true, name: label});
	}

	titleLink(name: string): Locator {
		return this.row(name).getByRole('link', {exact: true, name});
	}

	async waitForTable() {
		await waitForFDS({page: this.page});
	}
}
