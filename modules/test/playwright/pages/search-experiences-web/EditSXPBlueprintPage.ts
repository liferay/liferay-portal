/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Locator, Page, expect} from '@playwright/test';

export class EditSXPBlueprintPage {
	readonly addSXPElementSidebar: Locator;
	readonly cancelButton: Locator;
	readonly clauseContributorsSidebar: Locator;
	readonly configurationTab: Locator;
	readonly editTitleButton: Locator;
	readonly editDescriptionButton: Locator;
	readonly page: Page;
	readonly pageToolbar: Locator;
	readonly queryBuilderTab: Locator;
	readonly querySXPElements: Locator;
	readonly querySXPElementsMenuItem: Locator;
	readonly querySettings: Locator;
	readonly querySettingsMenuItem: Locator;
	readonly previewSidebar: Locator;
	readonly previewSidebarButton: Locator;
	readonly querySettingsRadioProperty: (label: string) => Promise<Locator>;
	readonly saveButton: Locator;
	readonly sxpBlueprintId: Locator;

	constructor(page: Page) {
		this.page = page;

		// Navigation

		this.queryBuilderTab = page.getByRole('button', {
			name: 'Query Builder',
		});
		this.configurationTab = page.getByRole('button', {
			name: 'Configuration',
		});

		this.querySXPElementsMenuItem = page.getByRole('menuitem', {
			name: 'Query Elements',
		});
		this.querySettingsMenuItem = page.getByRole('menuitem', {
			name: 'Query Settings',
		});

		// Main Components

		this.addSXPElementSidebar = page.locator('.add-element-sidebar');
		this.clauseContributorsSidebar = page.locator(
			'.clause-contributors-sidebar'
		);
		this.pageToolbar = page.getByLabel('Page Toolbar');
		this.previewSidebar = page.getByTestId('previewSidebar');
		this.querySXPElements = page.locator('.query-sxp-elements');
		this.querySettings = page.locator('.query-settings');

		// Page Toolbar

		this.editDescriptionButton =
			this.pageToolbar.getByLabel('Edit Description');
		this.editTitleButton = this.pageToolbar.getByLabel('Edit Title');
		this.cancelButton = this.pageToolbar.getByRole('button', {
			name: 'Cancel',
		});
		this.previewSidebarButton = page.getByTestId('previewSidebarButton');
		this.saveButton = this.pageToolbar.getByRole('button', {name: 'Save'});
		this.sxpBlueprintId = page.getByTestId('entityId');

		// Query Settings

		this.querySettingsRadioProperty = async (label: string) => {
			return this.querySettings.getByRole('radio', {
				name: label,
			});
		};
	}

	// Navigation & General Actions

	async cancelBlueprint() {
		await this.cancelButton.click();
	}

	async goToQueryBuilderTab() {
		await this.queryBuilderTab.click();
	}

	async goToConfigurationTab() {
		await this.configurationTab.click();
	}

	async goToQueryElementsMenuItem() {
		await this.querySXPElementsMenuItem.click();
	}

	async goToQuerySettingsMenuItem() {
		await this.querySettingsMenuItem.click();
	}

	async saveBlueprint() {
		await this.saveButton.click();
	}

	// Page Toolbar

	async getSXPBlueprintId() {
		return this.sxpBlueprintId.textContent();
	}

	// Preview

	async assertPreviewSidebarNoResults() {
		await expect(this.previewSidebar).toHaveText(/No Results Found/);
	}

	async assertPreviewSidebarSearchResult(
		title: string,
		fields?: {label: string; value: string}[],
		expandFields: boolean = false
	) {
		const previewSidebarResultListItem = this.page
			.getByTestId('previewSidebarResultListItem')
			.filter({hasText: new RegExp(title)});

		await expect(previewSidebarResultListItem).toBeVisible();

		if (expandFields) {
			await previewSidebarResultListItem.getByLabel('Expand').click();
		}

		for (const field of fields) {
			await expect(
				previewSidebarResultListItem
					.locator(
						`xpath=//div[contains(@class, "row") and contains(., "${field.label}")]/div[2]`
					)
					.nth(0)
			).toHaveText(field.value);
		}

		if (expandFields) {
			await previewSidebarResultListItem.getByLabel('Collapse').click();
		}
	}

	async openPreviewSidebar() {
		if ((await this.page.locator('.preview-sidebar.open').count()) < 1) {
			await this.previewSidebarButton.click();
		}
	}

	async searchInPreviewSidebar(keyword: string) {
		const searchInput = this.previewSidebar.getByPlaceholder('Search');

		await searchInput.fill(keyword);

		await searchInput.press('Enter');

		await expect(this.previewSidebar).toHaveText(/Result/);
	}

	// Query Settings - Clause Contributor Functions

	async assertClauseContributorSelection(option: {
		labels: string[];
		value: boolean;
	}) {

		// If labels is ['*'], then all contributors are selected

		const contributorsList =
			option.labels[0] === '*'
				? this.page
						.getByTestId('clauseContributorsSidebarListItem')
						.all()
				: option.labels.map((label) =>
						this.page
							.getByTestId('clauseContributorsSidebarListItem')
							.filter({
								has: this.page.getByText(label).first(),
							})
					);

		for (const contributor of await contributorsList) {
			const toggle = contributor.locator('.toggle-switch-check');

			if (option.value) {
				await expect(toggle).toBeChecked();
			}
			else {
				await expect(toggle).not.toBeChecked();
			}
		}
	}

	async assertQuerySettingsRadioPropertySelection(
		label: string,
		value: boolean = true
	) {
		const selectElement = await this.querySettingsRadioProperty(label);

		if (value) {
			await expect(selectElement).toBeChecked();
		}
		else {
			await expect(selectElement).not.toBeChecked();
		}
	}

	async openClauseContributorsSidebar() {
		await this.querySettings
			.getByRole('button', {name: 'Customize Contributors'})
			.click();
	}

	async selectQuerySettingsRadioProperty(label: string) {
		const selectElement = await this.querySettingsRadioProperty(label);

		await selectElement.check();
	}

	async selectAssetTypes(types: string[]) {
		await this.selectQuerySettingsRadioProperty('Selected Types');

		await this.querySettings
			.getByRole('button', {name: 'Select Asset Types'})
			.click();

		await expect(
			this.page.getByRole('dialog').getByText('Select Types')
		).toBeVisible();

		for (const type of types) {
			await this.page
				.getByRole('checkbox', {
					exact: true,
					name: `Select ${type}`,
				})
				.check();
		}

		await this.page
			.getByRole('dialog')
			.getByRole('button', {name: 'Done'})
			.click();

		for (const type of types) {
			await expect(
				this.querySettings.locator('li').getByText(type, {exact: true})
			).toBeVisible();
		}
	}

	async selectAssetSubtypes(subtypes: string[], type: string) {
		const assetListItem = this.querySettings
			.locator('li')
			.filter({hasText: type});

		await assetListItem.getByLabel('Select Subtypes').click();

		await expect(
			this.page.getByRole('dialog').getByText('Select Subtypes')
		).toBeVisible();

		for (const subtype of subtypes) {
			await this.page
				.getByRole('checkbox', {exact: true, name: `Select ${subtype}`})
				.check();
		}

		await this.page
			.getByRole('dialog')
			.getByRole('button', {name: 'Done'})
			.click();

		for (const subtype of subtypes) {
			await expect(assetListItem).toHaveText(new RegExp(subtype));
		}
	}

	async selectClauseContributors(option: {labels: string[]; value: boolean}) {

		// If labels is ['*'], then all contributors are selected

		if (option.labels[0] === '*') {
			const navbar = this.clauseContributorsSidebar.locator(
				'.clause-contributors-management-bar'
			);

			await navbar.getByRole('checkbox').click();

			if (option.value) {
				await navbar.getByText('Turn on').click();
			}
			else {
				await navbar.getByText('Turn off').click();
			}

			await navbar.getByRole('checkbox').click();
		}
		else {
			for (const label of option.labels) {
				const toggle = this.page
					.getByTestId('clauseContributorsSidebarListItem')
					.filter({
						has: this.page.getByText(label),
					})
					.locator('.toggle-switch-check');

				if (option.value) {
					await toggle.check();
				}
				else {
					await toggle.uncheck();
				}
			}
		}
	}
}
