/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Locator, Page, expect} from '@playwright/test';

import {clickAndExpectToBeVisible} from '../../../../utils/clickAndExpectToBeVisible';

export class ExportImportDataSelectionPage {
	readonly collapseSectionButton: (name: string) => Locator;
	readonly expandSectionButton: (name: string) => Locator;
	readonly page: Page;
	readonly section: Locator;
	readonly selectSitesButton: Locator;
	readonly siteSelectorDialog: Locator;

	constructor(page: Page) {
		this.page = page;
		this.section = page.locator('[data-testid="data-selection-section"]');
		this.selectSitesButton = page.getByRole('button', {
			name: 'Select Sites',
		});
		this.siteSelectorDialog = page.getByRole('dialog', {
			name: 'Select Sites',
		});

		this.collapseSectionButton = (name) =>
			page.getByRole('button', {exact: true, name: `Collapse ${name}`});
		this.expandSectionButton = (name) =>
			page.getByRole('button', {exact: true, name: `Expand ${name}`});
	}

	async expandSection(name: string) {
		await clickAndExpectToBeVisible({
			target: this.collapseSectionButton(name),
			trigger: this.expandSectionButton(name),
		});
	}

	async getExportableItems() {
		await this.waitForContent();

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

	async uncheckAllItems() {
		await this.waitForContent();

		const checkboxes = await this.section.getByRole('checkbox').all();

		for (const checkbox of checkboxes) {
			await checkbox.uncheck();
		}
	}

	async uncheckItem(sectionName: string, label: string) {
		await this.expandSection(sectionName);

		await this.section
			.getByRole('checkbox', {exact: true, name: label})
			.uncheck();
	}

	async selectGroup(groupName: string) {
		await this.selectSitesButton.click();

		const searchbox = this.siteSelectorDialog.getByRole('searchbox', {
			name: 'Search',
		});

		await searchbox.fill(groupName);
		await searchbox.press('Enter');

		await this.siteSelectorDialog
			.getByRole('row', {name: groupName})
			.getByRole('checkbox')
			.check();

		await this.siteSelectorDialog
			.getByRole('button', {exact: true, name: 'Select'})
			.click();

		await expect(this.siteSelectorDialog).toBeHidden();

		await expect(
			this.page.getByText(`Selected ${groupName}`)
		).toBeVisible();
	}

	async selectOnlyObjectDefinition(label: string) {
		await this.uncheckAllItems();

		await this.expandSection('Objects');

		await this.section.getByRole('checkbox', {name: label}).check();
	}

	async waitForContent() {
		await this.section.getByRole('checkbox').first().waitFor();
	}
}
