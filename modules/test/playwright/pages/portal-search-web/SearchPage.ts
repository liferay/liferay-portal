/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {FrameLocator, Locator, Page, expect} from '@playwright/test';

import {clickAndExpectToBeVisible} from '../../utils/clickAndExpectToBeVisible';
import {waitForAlert} from '../../utils/waitForAlert';

export class SearchPage {
	readonly page: Page;
	readonly modalIFrame: FrameLocator;
	readonly addPanelBody: Locator;
	readonly configurationMenuItem: Locator;
	readonly controlMenuAddButton: Locator;
	readonly searchBarInputInMainContent: Locator;
	readonly searchBarInputInNavBar: Locator;
	readonly searchBarPortletInMainContent: Locator;
	readonly searchBarPortletInNavBar: Locator;
	readonly searchOptionsConfigurationLink: Locator;
	readonly searchResults: Locator;
	readonly searchResultsItems: Locator;
	readonly searchResultsPaginationBar: Locator;
	readonly searchResultsPaginationDescription: Locator;
	readonly searchResultsPaginationItemsPerPageToggle: Locator;
	readonly searchResultsPaginationItemsPerPageDropdown: Locator;
	readonly searchResultsTotalLabel: Locator;

	constructor(page: Page) {
		this.page = page;

		this.addPanelBody = page.locator('.add-content-menu');
		this.controlMenuAddButton = page
			.locator('.control-menu-nav-item')
			.getByRole('button', {
				exact: true,
				name: 'Add',
			});
		this.configurationMenuItem = page.getByRole('menuitem', {
			exact: true,
			name: 'Configuration',
		});
		this.modalIFrame = page.frameLocator('iframe[id="modalIframe"]');
		this.searchBarPortletInMainContent = page.locator(
			'#main-content .portlet-search-bar'
		);
		this.searchBarPortletInNavBar = page.locator(
			'.navbar .portlet-search-bar'
		);
		this.searchOptionsConfigurationLink = page.getByText(
			'Configure additional search options in this page'
		);
		this.searchResults = page.locator('.portlet-search-results');

		// Search Bar Elements

		this.searchBarInputInMainContent =
			this.searchBarPortletInMainContent.getByPlaceholder('Search...');
		this.searchBarInputInNavBar =
			this.searchBarPortletInNavBar.getByPlaceholder('Search...');

		// Search Results Elements

		this.searchResultsItems =
			this.searchResults.locator('.list-group-item');
		this.searchResultsPaginationBar =
			this.searchResults.locator('.pagination');
		this.searchResultsPaginationDescription = this.searchResults.locator(
			'.pagination-results'
		);
		this.searchResultsPaginationItemsPerPageToggle =
			this.searchResults.locator('.pagination-items-per-page button');
		this.searchResultsPaginationItemsPerPageDropdown =
			this.searchResults.locator(
				'.pagination-items-per-page .dropdown-menu'
			);
		this.searchResultsTotalLabel = this.searchResults.locator(
			'.search-total-label'
		);
	}

	async addPortlet(portletName: string, category: string) {
		if (!(await this.addPanelBody.isVisible())) {
			await this.controlMenuAddButton.click();
		}

		await this.addPanelBody
			.getByRole('textbox', {name: 'Search Form'})
			.fill(portletName);

		const categoryPanel = this.addPanelBody.locator('.panel').filter({
			has: this.page.locator(
				`xpath=//span[@class='panel-title' and contains(.,'${category}')]`
			),
		});

		await categoryPanel.getByText(portletName).click();

		await categoryPanel.getByRole('button', {name: 'Add Content'}).click();
	}

	async getSearchFacetCheckbox(
		searchFacetTerm: string | RegExp,
		type: string
	): Promise<Locator> {
		return this.page
			.locator(
				`xpath=//div[contains(.,'${type}') and contains(@class, 'panel-group')]`
			)
			.getByRole('checkbox', {name: searchFacetTerm});
	}

	async getSearchFacetLink(
		searchFacetTerm: string,
		type: string
	): Promise<Locator> {
		return this.page.locator(
			`xpath=//div[contains(.,'${type}') and contains(@class, 'panel-group')]//*[contains(.,'${searchFacetTerm}') and contains(@class, 'term-name')]`
		);
	}

	async goto() {
		await this.page.goto('/search');
	}

	async openSearchPortletConfiguration(
		portletName: string,
		index: number = 0
	) {
		const portletTopper = this.page
			.locator('.portlet-topper', {hasText: portletName})
			.nth(index);

		await this.page
			.locator('.portlet', {
				hasText: portletName,
			})
			.nth(index)
			.hover();

		await expect(portletTopper).toBeVisible();

		await portletTopper.getByLabel('Options').click();

		await this.configurationMenuItem.click();

		await expect(this.page.locator('#modalIframe')).toBeVisible();
	}

	async savePortletConfiguration() {
		await this.modalIFrame.getByRole('button', {name: 'Save'}).click();

		await waitForAlert(
			this.modalIFrame,
			'Success:You have successfully updated the setup.'
		);

		await this.modalIFrame.getByRole('button', {name: 'Cancel'}).click();
	}

	async searchKeywordInMainContent(searchText: string) {
		await this.searchBarInputInMainContent.fill(searchText);

		await this.searchBarInputInMainContent.press('Enter');
	}

	async searchKeywordInNavBar(searchText: string) {
		await this.searchBarInputInNavBar.fill(searchText);

		await this.searchBarInputInNavBar.press('Enter');
	}

	async selectPaginationItemsPerPage(delta: number) {
		await clickAndExpectToBeVisible({
			autoClick: true,
			target: this.searchResultsPaginationItemsPerPageDropdown.locator(
				`xpath=//*[@id='${delta}']`
			),
			trigger: this.searchResultsPaginationItemsPerPageToggle,
		});

		await expect(this.searchResultsPaginationItemsPerPageToggle).toHaveText(
			new RegExp(`${delta.toString()} Entries`)
		);
	}

	async selectPaginationPageNumber(pageNumber: number) {
		await this.searchResultsPaginationBar
			.getByText(pageNumber.toString())
			.first()
			.click();

		await expect(
			this.searchResultsPaginationBar
				.getByText(pageNumber.toString())
				.first()
		).toHaveAttribute('aria-current', 'page');
	}

	async selectPortletConfigurationsCheckbox(
		options: {label: string; value: boolean}[]
	) {
		for (const option of options) {
			const configurationCheckbox = this.modalIFrame.locator(
				`xpath=//*[text()[contains(.,'${option.label}')]]//input`
			);

			await this.selectSearchFacetCheckbox(
				configurationCheckbox,
				option.value
			);
		}
	}

	async selectPortletConfigurationsSelect(
		options: {label: string; value: string}[]
	) {
		for (const option of options) {
			const configurationSelect = this.modalIFrame.getByLabel(
				option.label,
				{exact: true}
			);

			await configurationSelect.selectOption({label: option.value});
		}
	}

	async selectSearchFacetCheckbox(
		searchFacetCheckbox: Locator,
		value: boolean = true
	) {
		if (value) {
			await searchFacetCheckbox.check();

			await expect(searchFacetCheckbox).toBeChecked();
		}
		else {
			await searchFacetCheckbox.uncheck();

			await expect(searchFacetCheckbox).not.toBeChecked();
		}
	}

	async selectSearchFacetLink(
		searchFacetLink: Locator,
		value: boolean = true
	) {
		await searchFacetLink.click();

		await expect(searchFacetLink).not.toBeDisabled();

		if (value) {
			await expect(searchFacetLink).toHaveClass(/facet-term-selected/);
		}
		else {
			await expect(searchFacetLink).not.toHaveClass(
				/facet-term-selected/
			);
		}
	}
}
