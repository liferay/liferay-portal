/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Locator, Page} from '@playwright/test';

import {clickAndExpectToBeVisible} from '../../../../utils/clickAndExpectToBeVisible';

export class ExportImportDataSelectionPage {
	readonly collapseSectionButton: (name: string) => Locator;
	readonly expandSectionButton: (name: string) => Locator;
	readonly page: Page;
	readonly section: Locator;

	constructor(page: Page) {
		this.page = page;
		this.section = page.locator('[data-testid="data-selection-section"]');

		this.collapseSectionButton = (name) =>
			this.section.getByRole('button', {name: `Collapse ${name}`});
		this.expandSectionButton = (name) =>
			this.section.getByRole('button', {name: `Expand ${name}`});
	}

	async expandSection(name: string) {
		await clickAndExpectToBeVisible({
			target: this.collapseSectionButton(name),
			trigger: this.expandSectionButton(name),
		});
	}

	async getExportableItems() {
		const exportableItems = new Map<string, number>();

		const labels = await this.section.locator('label').all();

		for (const label of labels) {
			const countLabel = label
				.locator('xpath=..')
				.getByText(/^\d+ Items?$/);

			if ((await countLabel.count()) === 0) {
				continue;
			}

			const name = await label.textContent();
			const count = await countLabel.textContent();

			if (name && count) {
				exportableItems.set(name.trim(), parseInt(count, 10));
			}
		}

		return exportableItems;
	}

	async uncheckItem(sectionName: string, label: string) {
		await this.expandSection(sectionName);

		await this.section
			.getByRole('checkbox', {exact: true, name: label})
			.uncheck();
	}

	async selectOnlyObjectDefinition(label: string) {
		const checkboxes = await this.section.getByRole('checkbox').all();

		for (const checkbox of checkboxes) {
			await checkbox.uncheck();
		}

		await this.expandSection('Objects');

		await this.section.getByRole('checkbox', {name: label}).check();
	}
}
