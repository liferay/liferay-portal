/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Locator, Page, expect} from '@playwright/test';

import {clickAndExpectToBeHidden} from '../../utils/clickAndExpectToBeHidden';
import {hoverAndExpectToBeVisible} from '../../utils/hoverAndExpectToBeVisible';

export class EditSXPBlueprintPage {
	readonly addSXPElementSidebar: Locator;
	readonly cancelButton: Locator;
	readonly clauseContributorsSidebar: Locator;
	readonly configurationTab: Locator;
	readonly editTitleButton: Locator;
	readonly editDescriptionButton: Locator;
	readonly infoSidebar: Locator;
	readonly page: Page;
	readonly pageToolbar: Locator;
	readonly queryBuilderTab: Locator;
	readonly querySXPElements: Locator;
	readonly previewSidebar: Locator;
	readonly previewSidebarButton: Locator;
	readonly saveButton: Locator;
	readonly source: Locator;
	readonly sourceRadioProperty: (label: string) => Promise<Locator>;
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

		// Main Components

		this.addSXPElementSidebar = page.locator('.add-sxp-element-sidebar');
		this.clauseContributorsSidebar = page.locator(
			'.clause-contributors-sidebar'
		);
		this.infoSidebar = page.locator('.info-sidebar');
		this.pageToolbar = page.getByLabel('Page Toolbar');
		this.previewSidebar = page.getByTestId('previewSidebar');
		this.querySXPElements = page.locator('.query-sxp-elements');
		this.source = page.locator('.source');

		// Page Toolbar

		this.editDescriptionButton =
			this.pageToolbar.getByLabel('Edit Description');
		this.editTitleButton = this.pageToolbar.getByLabel('Edit Title');
		this.cancelButton = this.pageToolbar.getByRole('link', {
			name: 'Cancel',
		});
		this.previewSidebarButton = page.getByTestId('previewSidebarButton');
		this.saveButton = this.pageToolbar.getByRole('button', {name: 'Save'});
		this.sxpBlueprintId = page.getByTestId('entityId');

		// Source

		this.sourceRadioProperty = async (label: string) => {
			return this.source.getByRole('radio', {
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

	async expandPanel(title: string) {
		await this.closeSidebars();

		const panelButtonLocator = this.page.getByRole('button', {name: title});

		await expect(async () => {
			if (
				await panelButtonLocator.evaluate((element) =>
					element.classList.contains('collapsed')
				)
			) {
				await panelButtonLocator.dispatchEvent('click');
			}

			await expect(panelButtonLocator).not.toHaveClass(/collapsed/, {
				timeout: 5000,
			});
		}).toPass({timeout: 30000});
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
		fields: {label: string; value: string}[],
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

	// Preview Sidebar

	async openPreviewSidebar() {
		if (await this.page.locator('.preview-sidebar.open').isHidden()) {
			await this.previewSidebarButton.click();
		}
	}

	async closeSidebars() {
		const openSidebar = this.page.locator('.sidebar.open').first();

		await clickAndExpectToBeHidden({
			target: openSidebar,
			trigger: openSidebar.getByRole('button', {name: 'Close'}),
		});
	}

	async addPreviewAttributes(attributes: {key: string; value: string}[]) {
		await this.page.getByLabel('Search Context Attributes').click();

		for (let i = 0; i < attributes.length; i++) {
			await this.page.getByLabel('Add Field').click();

			await this.page
				.locator(`.modal-dialog #key-${i}`)
				.fill(attributes[i].key);
			await this.page
				.locator(`.modal-dialog #value-${i}`)
				.fill(attributes[i].value);
		}

		await this.page.getByRole('button', {name: 'Done'}).click();
	}

	async searchInPreviewSidebar(keyword: string) {
		const searchInput = this.previewSidebar.getByPlaceholder('Search');

		await searchInput.fill(keyword);

		await searchInput.press('Enter');

		await expect(this.previewSidebar).toHaveText(/Result/);
	}

	// Query Elements

	async addQueryElement(elementName: string) {
		if (!this.addSXPElementSidebar.isVisible()) {
			await this.page.getByLabel('Add Query Element').click();
		}

		await this.addSXPElementSidebar
			.getByPlaceholder('Search')
			.fill(elementName);

		await hoverAndExpectToBeVisible({
			autoClick: true,
			target: this.addSXPElementSidebar
				.locator('li')
				.filter({
					hasText: elementName,
				})
				.nth(0)
				.getByLabel('Add'),
			trigger: this.addSXPElementSidebar
				.locator('li')
				.filter({
					hasText: elementName,
				})
				.nth(0),
		});

		await expect(
			this.querySXPElements.getByText(elementName, {
				exact: true,
			})
		).toBeVisible();
	}

	// Source - Clause Contributor Functions

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

	async assertSourceRadioPropertySelection(
		label: string,
		value: boolean = true
	) {
		const selectElement = await this.sourceRadioProperty(label);

		if (value) {
			await expect(selectElement).toBeChecked();
		}
		else {
			await expect(selectElement).not.toBeChecked();
		}
	}

	async openClauseContributorsSidebar() {
		await this.source
			.getByRole('button', {name: 'Customize Contributors'})
			.click();
	}

	async selectSourceRadioProperty(label: string) {
		const selectElement = await this.sourceRadioProperty(label);

		await selectElement.check();
	}

	async selectAssetTypes(types: string[]) {
		await this.assertSourceRadioPropertySelection('Selected Types');

		await this.source
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
				this.source.locator('li').getByText(type, {exact: true})
			).toBeVisible();
		}
	}

	async selectAssetSubtypes(subtypes: string[], type: string) {
		const assetListItem = this.source.locator('li').filter({hasText: type});

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

	// Source - Clause Contributor Filter, Search, and Sort

	async assertClauseContributorCount(count: number) {
		await expect(
			this.page.getByTestId('clauseContributorsSidebarListItem')
		).toHaveCount(count);
	}

	async assertClauseContributorNotPresent(className: string) {
		await expect(
			this.page
				.getByTestId('clauseContributorsSidebarListItem')
				.filter({hasText: className})
		).toHaveCount(0);
	}

	async assertClauseContributorPresent(className: string) {
		await expect(
			this.page
				.getByTestId('clauseContributorsSidebarListItem')
				.filter({hasText: className})
		).toBeVisible();
	}

	async clearClauseContributorFilter(label: string) {
		await this.clauseContributorsSidebar
			.locator('.tbar-label')
			.filter({hasText: label})
			.getByRole('button')
			.click();
	}

	async clearClauseContributorFilters() {
		await this.clauseContributorsSidebar
			.getByRole('button', {exact: true, name: 'Clear'})
			.click();
	}

	async filterClauseContributors(label: string) {
		await this.clauseContributorsSidebar
			.getByRole('button', {name: 'Filter'})
			.click();

		await this.page
			.getByRole('menuitem', {exact: true, name: label})
			.click();
	}

	async reverseClauseContributorSort() {
		await this.clauseContributorsSidebar
			.getByTitle('Reverse Sort Direction')
			.click();
	}

	async searchClauseContributors(keyword: string) {
		const searchInput = this.clauseContributorsSidebar.getByRole(
			'textbox',
			{
				name: 'Search',
			}
		);

		await searchInput.fill(keyword);

		await searchInput.press('Enter');
	}

	async selectScope({
		label,
		tab,
	}: {
		label: string;
		tab?: 'Recent' | 'My Sites' | 'Asset Libraries' | 'Spaces';
	}) {
		const scopeSelector = this.page.locator('.scope-selector');

		await scopeSelector.getByRole('button', {name: 'Select Scope'}).click();

		const scopeModal = this.page.frameLocator(
			'iframe[title="Select Scope"]'
		);

		if (tab) {
			await scopeModal
				.locator('.navbar-nav')
				.getByRole('link', {name: tab})
				.click();
		}

		await clickAndExpectToBeHidden({
			target: this.page.locator('.modal-dialog'),
			trigger: scopeModal.getByRole('link', {exact: true, name: label}),
		});

		await expect(
			scopeSelector
				.locator('tr')
				.filter({has: this.page.getByRole('cell', {name: label})})
		).toBeVisible();
	}

	async removeScope({label}: {label: string}) {
		const scopeSelector = this.page.locator('.scope-selector');

		await clickAndExpectToBeHidden({
			target: scopeSelector.getByRole('cell', {name: label}),
			trigger: scopeSelector
				.getByRole('row', {name: label})
				.getByLabel('Remove'),
		});
	}
}
