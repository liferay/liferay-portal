/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Locator, Page} from '@playwright/test';

const DATA_MASKS_URL =
	'/group/guest/~/control_panel/manage?p_p_id=com_liferay_mcp_server_web_internal_portlet_MCPServerWebPortlet';

export class DataMasksPage {
	readonly addFilterButton: Locator;
	readonly dataSet: Locator;
	readonly filterButton: Locator;
	readonly newDataMaskButton: Locator;
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
		this.newDataMaskButton = page.getByRole('button', {
			name: 'New Data Mask',
		});
		this.filterButton = page.getByRole('button', {
			exact: true,
			name: 'Filter',
		});
		this.addFilterButton = page.getByRole('button', {
			exact: true,
			name: 'Add Filter',
		});
		this.orderButton = page.getByRole('button', {
			exact: true,
			name: 'Order',
		});
	}

	async goto() {
		await this.page.goto(DATA_MASKS_URL, {waitUntil: 'load'});

		await this.table.waitFor({state: 'visible'});
	}

	async filterByType(value: string) {
		await this.filterButton.click();

		await this.page
			.getByRole('menuitem', {exact: true, name: 'Type'})
			.click();

		await this.page.getByLabel(value, {exact: true}).check();

		await this.addFilterButton.click();
	}

	async search(name: string) {
		await this.searchInput.fill(name);
		await this.page.keyboard.press('Enter');
	}

	row(name: string): Locator {
		return this.table
			.locator('tbody tr')
			.filter({has: this.page.getByRole('link', {exact: true, name})});
	}

	titleLink(name: string): Locator {
		return this.row(name).getByRole('link', {exact: true, name});
	}

	async clickAction(name: string, action: string) {
		await this.row(name).getByRole('button', {name: 'Actions'}).click();

		await this.page
			.getByRole('menuitem', {exact: true, name: action})
			.click();
	}

	get formHeading(): Locator {
		return this.page.locator('.control-menu-level-1-heading');
	}

	get nameInput(): Locator {
		return this.page.locator('#dataMaskName');
	}

	get descriptionInput(): Locator {
		return this.page.locator('#dataMaskDescription');
	}

	get matchPatternInput(): Locator {
		return this.page.locator('#dataMaskMatchPattern');
	}

	get regexPatternInput(): Locator {
		return this.page.locator('#dataMaskRegexPattern');
	}

	get replacementInput(): Locator {
		return this.page.locator('#dataMaskReplacement');
	}

	get saveButton(): Locator {
		return this.page.getByRole('button', {name: 'Save'});
	}

	get sampleInput(): Locator {
		return this.page.getByPlaceholder('Enter a sample value');
	}

	get outputInput(): Locator {
		return this.page.getByLabel('Output', {exact: true});
	}

	get testButton(): Locator {
		return this.page.getByRole('button', {exact: true, name: 'Test'});
	}

	get dialog(): Locator {
		return this.page.getByRole('dialog');
	}
}
