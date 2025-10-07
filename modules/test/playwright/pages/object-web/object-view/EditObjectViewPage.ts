/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {FrameLocator, Locator, Page} from '@playwright/test';

export class EditObjectViewPage {
	readonly addButton: Locator;
	readonly addColumnButton: Locator;
	readonly addColumnsModal: Locator;
	readonly filterBy: Locator;
	readonly filterType: Locator;
	readonly filterValue: Locator;
	readonly filtersTab: Locator;
	readonly markAsDefaultButton: Locator;
	readonly newFilterButton: Locator;
	readonly saveButton: Locator;
	readonly saveFilter: Locator;
	readonly sidePanel: FrameLocator;
	readonly page: Page;
	readonly viewBuilderTab: Locator;

	constructor(page: Page) {
		this.sidePanel = page.frameLocator('iframe');

		this.addButton = this.sidePanel.getByLabel('Add');
		this.addColumnButton = this.sidePanel.getByRole('button', {
			name: 'Add Column',
		});
		this.addColumnsModal = page.getByRole('dialog', {name: 'Add Columns'});
		this.filterBy = this.sidePanel.getByLabel('Filter By' + 'Mandatory');
		this.filterType = this.sidePanel.getByText('Select an Option');
		this.filtersTab = this.sidePanel.getByRole('tab', {name: 'Filters'});
		this.filterValue = this.sidePanel
			.getByLabel('New Filter')
			.locator('input[type="text"]');
		this.markAsDefaultButton = this.sidePanel.getByLabel('Mark as Default');
		this.newFilterButton = this.sidePanel.getByRole('button', {
			name: 'New Filter',
		});
		this.saveButton = this.sidePanel.getByRole('button', {
			name: 'Save',
		});
		this.saveFilter = this.sidePanel
			.getByLabel('New Filter')
			.getByText('Save');
		this.viewBuilderTab = this.sidePanel.getByRole('tab', {
			name: 'View Builder',
		});
	}

	async createFilter(
		filterBy: string,
		filterType: 'Includes' | 'Excludes',
		objectEntryId?: string
	) {
		await this.filtersTab.click();

		await this.newFilterButton.click();

		await this.filterBy.click();

		await this.sidePanel.getByRole('option', {name: filterBy}).click();

		await this.filterType.click();

		await this.sidePanel.getByRole('option', {name: filterType}).click();

		if (objectEntryId) {
			await this.filterValue.click();

			await this.sidePanel.getByLabel(objectEntryId).check();
		}

		await this.saveFilter.click();
	}

	async selectObjectFields(objectFieldNames: string[]) {
		await this.viewBuilderTab.click();

		const addButton = this.addColumnButton.or(this.addButton);

		await addButton.click();

		for (const objectFieldName of objectFieldNames) {
			await this.addColumnsModal.getByText(objectFieldName).check();
		}

		await this.addColumnsModal
			.getByRole('button', {
				name: 'Save',
			})
			.click();
	}

	async unselectObjectFields(objectFieldNames: string[]) {
		await this.viewBuilderTab.click();

		const addButton = this.addColumnButton.or(this.addButton);

		await addButton.click();

		for (const objectFieldName of objectFieldNames) {
			await this.addColumnsModal.getByText(objectFieldName).uncheck();
		}

		await this.addColumnsModal
			.getByRole('button', {
				name: 'Save',
			})
			.click();
	}
}
