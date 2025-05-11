/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Locator, Page} from '@playwright/test';

export class DataSetPage {
	readonly activeViewSelector: Locator;
	readonly page: Page;
	readonly table: {
		bodyRows: Locator;
		container: Locator;
		headRow: Locator;
	};

	constructor(page: Page) {
		this.activeViewSelector = page.getByLabel('Show View Options');

		const tableContainer = page.locator('.fds table');
		this.table = {
			bodyRows: tableContainer.locator('tbody tr'),
			container: tableContainer,
			headRow: tableContainer.locator('thead tr'),
		};

		this.page = page;
	}

	getRow(filter: string) {
		return this.table.bodyRows.filter({hasText: filter});
	}

	async execItemAction({action, filter}: {action: string; filter: string}) {
		const item = this.getRow(filter);
		const button = item.getByRole('button', {
			exact: true,
			name: 'Actions',
		});
		const dropdownId = await button.getAttribute('aria-controls');
		await button.click();

		const dropdownMenu = this.page
			.locator(`#${dropdownId}`)
			.filter({has: this.page.getByRole('menu')});
		await dropdownMenu.waitFor();

		const dropdownMenuActionItem = dropdownMenu.getByRole('menuitem', {
			name: action,
		});
		await dropdownMenuActionItem.waitFor();
		await dropdownMenuActionItem.click();
	}

	async changeVisualizationMode(visualizationMode: 'Cards' | 'Table') {
		await this.activeViewSelector.waitFor({
			state: 'visible',
		});
		await this.activeViewSelector.click();

		await this.page
			.getByRole('listbox')
			.getByRole('option', {name: visualizationMode})
			.click();
	}
}
