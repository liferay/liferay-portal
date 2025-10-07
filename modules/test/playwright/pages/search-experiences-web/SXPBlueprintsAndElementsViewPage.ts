/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Locator, Page, expect} from '@playwright/test';

import {clickAndExpectToBeVisible} from '../../utils/clickAndExpectToBeVisible';
import {ApplicationsMenuPage} from '../product-navigation-applications-menu/ApplicationsMenuPage';

export class SXPBlueprintsAndElementsViewPage {
	readonly applicationsMenuPage: ApplicationsMenuPage;
	readonly page: Page;
	readonly blueprintElementSearchBar: Locator;
	readonly blueprintElementTable: Locator;
	readonly blueprintElementTableHeading: Locator;
	readonly blueprintElementTableOpenFieldsMenuButton: Locator;
	readonly blueprintsTab: Locator;
	readonly elementsTab: Locator;
	readonly addBlueprintButton: Locator;
	readonly addBlueprintElementModal: Locator;

	constructor(page: Page) {
		this.addBlueprintButton = page
			.getByTestId('managementToolbar')
			.getByLabel('New Search Blueprint');
		this.addBlueprintElementModal = page.locator('.modal-dialog');
		this.applicationsMenuPage = new ApplicationsMenuPage(page);
		this.blueprintElementTable = page.locator('.fds table');
		this.blueprintElementSearchBar = page.getByPlaceholder('Search');
		this.blueprintsTab = page.getByRole('link', {name: 'Blueprints'});
		this.elementsTab = page.getByRole('link', {name: 'Elements'});

		// Blueprint/Element Table

		this.blueprintElementTableHeading =
			this.blueprintElementTable.locator('thead');
		this.blueprintElementTableOpenFieldsMenuButton =
			this.blueprintElementTable.getByLabel('Manage Columns Visibility');

		this.page = page;
	}

	// Navigation

	async goto() {
		await this.applicationsMenuPage.goToBlueprints();
	}

	async goToBlueprintsTab() {
		await this.blueprintsTab.click();
	}

	async goToElementsTab() {
		await this.elementsTab.click();
	}

	// Blueprint/Element Table Actions

	async createBlueprint(title: string, description?: string) {
		await this.addBlueprintButton.click();

		await this.addBlueprintElementModal.getByLabel('Title').fill(title);

		if (description) {
			await this.addBlueprintElementModal
				.getByLabel('Description')
				.fill(description);
		}

		await this.addBlueprintElementModal
			.getByRole('button', {name: 'Create'})
			.click();
	}

	async selectTableLink(title: string, id?: number) {
		await expect(this.blueprintElementTable).toBeVisible();

		let itemLink = this.blueprintElementTable.getByRole('link', {
			name: title,
		});

		if ((await itemLink.count()) === 0) {

			// If items are on another page, use search bar

			await this.blueprintElementSearchBar.fill(title);

			await this.blueprintElementSearchBar.press('Enter');
		}

		if ((await itemLink.count()) > 1 && id) {

			// If there are multiple items with the same title, check id

			itemLink = this.blueprintElementTable
				.locator('tr')
				.filter({
					has: this.page.getByText(`${id}`),
				})
				.getByRole('link', {name: title});
		}

		await itemLink.click();
	}

	async selectTableMenuOption(title: string, option: string) {
		await expect(this.blueprintElementTable).toBeVisible();

		const itemRow = this.blueprintElementTable.locator('tr').filter({
			has: this.page.getByRole('link', {name: title}),
		});

		await clickAndExpectToBeVisible({
			autoClick: true,
			target: this.page
				.locator('.dropdown-menu')
				.getByRole('menuitem', {name: option}),
			trigger: itemRow.getByRole('button', {name: 'Actions'}),
		});
	}
}
