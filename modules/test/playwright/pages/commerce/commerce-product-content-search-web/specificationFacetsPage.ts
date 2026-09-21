/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Locator, Page} from '@playwright/test';

import {CommerceLayoutsPage} from '../commerce-order-content-web/commerceLayoutsPage';

export type FacetWidget = 'Option Facet' | 'Specification Facet';

const DISPLAY_TEMPLATE_TERM_SELECTORS = {
	'Cloud Layout':
		'ul.tag-cloud span.facet-value button.facet-term[data-term-id="%s"]',
	'Compact Layout':
		'ul.list-unstyled li.facet-value button.facet-term[data-term-id="%s"]',
	'Label Layout':
		'div.label-container button.facet-term[data-term-id="%s"] span.label-item-expand',
};

export type FacetConfiguration = {
	displayFrequencies?: boolean;
	displayTemplate?: string;
	frequencyThreshold?: number;
	maxEntities?: number;
	maxTerms?: number;
};

export class SpecificationFacetsPage {
	readonly addSearchOptionsLabel: Locator;
	readonly addOptionFacetLabel: Locator;
	readonly addSpecificationFacetLabel: Locator;
	readonly addWidgetButton: Locator;
	readonly configurationMenuItem: Locator;
	readonly configurationSaveButton: Locator;
	readonly displayFrequenciesCheckbox: Locator;
	readonly displayTemplateSelect: Locator;
	readonly frequencyThresholdInput: Locator;
	readonly layoutsPage: CommerceLayoutsPage;
	readonly maxOptionsInput: Locator;
	readonly maxSpecificationsInput: Locator;
	readonly maxTermsInput: Locator;
	readonly optionFacetConfigurationEditButton: Locator;
	readonly optionFacetPortlet: Locator;
	readonly specificationFacetPortlet: Locator;
	readonly page: Page;
	readonly pageLabel: Locator;
	readonly pageTitle: Locator;
	readonly panelList: Locator;
	readonly searchFormInput: Locator;
	readonly searchOptionsAllowEmptySearchesInput: Locator;
	readonly searchOptionsConfigurationEditButton: Locator;
	readonly searchBarDestinationInput: Locator;
	readonly searchBarPortlet: Locator;
	readonly searchBarScopeSelect: Locator;
	readonly searchOptionsConfigurationSaveButton: Locator;
	readonly selectSpecificationFacetPageInput: Locator;
	readonly specificationFacetConfigurationEditButton: Locator;
	readonly specificationFacetOrderSpecificationInput: Locator;

	constructor(page: Page) {
		this.addSearchOptionsLabel = page
			.getByTestId('addPanelTabItem')
			.filter({hasText: /^Search Options$/})
			.getByRole('button', {exact: true, name: 'Add Content'});
		this.addOptionFacetLabel = page
			.getByTestId('addPanelTabItem')
			.filter({hasText: /^Option Facet$/})
			.getByRole('button', {exact: true, name: 'Add Content'});
		this.addSpecificationFacetLabel = page
			.getByTestId('addPanelTabItem')
			.filter({hasText: /^Specification Facet$/})
			.getByRole('button', {exact: true, name: 'Add Content'});
		this.addWidgetButton = page.getByTestId('add');
		this.configurationMenuItem = page.getByRole('menuitem', {
			exact: true,
			name: 'Configuration',
		});
		this.configurationSaveButton = page
			.frameLocator('iframe[id="modalIframe"]')
			.getByRole('button', {name: 'Save'});
		this.displayFrequenciesCheckbox = page
			.frameLocator('iframe[id="modalIframe"]')
			.getByLabel('Display Frequencies');
		this.displayTemplateSelect = page
			.frameLocator('iframe[id="modalIframe"]')
			.getByLabel('Display Template');
		this.frequencyThresholdInput = page
			.frameLocator('iframe[id="modalIframe"]')
			.getByLabel('Frequency Threshold');
		this.layoutsPage = new CommerceLayoutsPage(page);
		this.maxOptionsInput = page
			.frameLocator('iframe[id="modalIframe"]')
			.getByLabel('Max Options');
		this.maxSpecificationsInput = page
			.frameLocator('iframe[id="modalIframe"]')
			.getByLabel('Max Specifications');
		this.maxTermsInput = page
			.frameLocator('iframe[id="modalIframe"]')
			.getByLabel('Max Terms');
		this.optionFacetPortlet = page.locator(
			'//section[contains(@id, "CPOptionFacetsPortlet")]'
		);
		this.specificationFacetPortlet = page.locator(
			'//section[contains(@id, "CPSpecificationOptionFacetsPortlet")]'
		);
		this.optionFacetConfigurationEditButton =
			this.optionFacetPortlet.getByLabel('Options');
		this.page = page;
		this.pageLabel = page
			.getByTestId('layoutHref')
			.getByLabel('Specification Facet Page');
		this.pageTitle = page
			.getByTestId('headerTitle')
			.filter({hasText: 'Specification Facet Page'});
		this.panelList = page
			.getByTestId('specificationFacetPanel')
			.getByRole('button');
		this.searchFormInput = page.getByRole('textbox', {
			name: 'Search Form',
		});
		this.searchOptionsAllowEmptySearchesInput = page
			.frameLocator('#modalIframe')
			.getByTestId('allowEmptySearches');
		this.searchOptionsConfigurationEditButton = page.getByRole('button', {
			name: 'Configure additional search',
		});
		this.searchOptionsConfigurationSaveButton = page
			.frameLocator('#modalIframe')
			.getByTestId('searchOptionsFooter')
			.getByRole('button', {exact: true, name: 'Save'});
		this.searchBarDestinationInput = page
			.frameLocator('iframe[id="modalIframe"]')
			.getByLabel('Destination Page');
		this.searchBarPortlet = page.locator(
			'//section[contains(@id, "SearchBarPortlet")]'
		);
		this.searchBarScopeSelect = page
			.frameLocator('iframe[id="modalIframe"]')
			.getByLabel('Scope', {exact: true});
		this.selectSpecificationFacetPageInput = page
			.getByTestId('selectLayout')
			.getByLabel('Select Specification Facet Page');
		this.specificationFacetConfigurationEditButton =
			this.specificationFacetPortlet.getByLabel('Options');
		this.specificationFacetOrderSpecificationInput = page
			.frameLocator('iframe[id="modalIframe"]')
			.getByLabel('Order Specifications By');
	}

	async addSearchOptionsWidget() {
		await this.searchFormInput.click();
		await this.searchFormInput.fill('Search Options');
		await this.addSearchOptionsLabel.click();
	}

	async addSpecificationFacetWidget() {
		await this.searchFormInput.click();
		await this.searchFormInput.fill('Specification Facet');
		await this.addSpecificationFacetLabel.click();
	}

	async addOptionFacetWidget() {
		await this.searchFormInput.click();
		await this.searchFormInput.fill('Option Facet');
		await this.addOptionFacetLabel.click();
	}

	async addRequiredFacetWidgets() {
		await this.addWidgetButton.click();
		await this.addSearchOptionsWidget();
		await this.addOptionFacetWidget();
		await this.addSpecificationFacetWidget();
	}

	async configureSearchBar({
		destination = '',
		scope = 'everything',
	}: {
		destination?: string;
		scope?: string;
	} = {}) {
		await this.searchBarPortlet.getByLabel('Options').click();
		await this.configurationMenuItem.click();
		await this.searchBarScopeSelect.waitFor({state: 'visible'});

		await this.searchBarScopeSelect.selectOption(scope);

		await this.searchBarDestinationInput.fill(destination);

		await this.saveConfiguration();

		await this.reloadPage();
	}

	async configureSearchOptions() {
		await this.searchOptionsConfigurationEditButton.click();
		await this.searchOptionsAllowEmptySearchesInput.waitFor({
			state: 'attached',
		});
		await this.searchOptionsAllowEmptySearchesInput.click();
		await this.searchOptionsConfigurationSaveButton.click();
	}

	async configureOptionFacetFrequencyThreshold(value: string) {
		await this.optionFacetConfigurationEditButton.click();
		await this.configurationMenuItem.click();
		await this.displayTemplateSelect.waitFor({
			state: 'attached',
		});
		await this.frequencyThresholdInput.fill(value);
		await this.configurationSaveButton.click();
	}

	async configureSpecificationFacetFrequencyThreshold(value: string) {
		await this.specificationFacetConfigurationEditButton.click();
		await this.configurationMenuItem.click();
		await this.displayTemplateSelect.waitFor({
			state: 'attached',
		});
		await this.frequencyThresholdInput.fill(value);
		await this.configurationSaveButton.click();
	}

	facetPortlet(widget: FacetWidget) {
		return widget === 'Option Facet'
			? this.optionFacetPortlet
			: this.specificationFacetPortlet;
	}

	facetPanelTitles(widget: FacetWidget) {
		return this.facetPortlet(widget).locator('.panel-title');
	}

	facetPanel(widget: FacetWidget, facetName: string) {
		return this.facetPortlet(widget).locator('.panel-title', {
			hasText: facetName,
		});
	}

	facetTerms(widget: FacetWidget, facetName: string) {
		return this.facetPortlet(widget)
			.getByTestId(facetName)
			.locator('li.facet-value');
	}

	facetTermFrequencies(widget: FacetWidget) {
		return this.facetPortlet(widget).locator('.term-count');
	}

	facetTermsByDisplayTemplate(
		widget: FacetWidget,
		displayTemplate: string,
		term: string
	) {
		return this.facetPortlet(widget).locator(
			DISPLAY_TEMPLATE_TERM_SELECTORS[displayTemplate].replace('%s', term)
		);
	}

	configurationErrorMessage(message: string) {
		return this.page
			.frameLocator('iframe[id="modalIframe"]')
			.getByText(message);
	}

	maxEntitiesInput(widget: FacetWidget) {
		return widget === 'Option Facet'
			? this.maxOptionsInput
			: this.maxSpecificationsInput;
	}

	async openFacetConfiguration(widget: FacetWidget) {
		await this.facetPortlet(widget).getByLabel('Options').click();
		await this.configurationMenuItem.click();
		await this.displayTemplateSelect.waitFor({state: 'visible'});
	}

	async updateFacetConfiguration(
		widget: FacetWidget,
		configuration: FacetConfiguration
	) {
		await this.openFacetConfiguration(widget);

		const {
			displayFrequencies,
			displayTemplate,
			frequencyThreshold,
			maxEntities,
			maxTerms,
		} = configuration;

		if (displayTemplate !== undefined) {
			await this.displayTemplateSelect.click();
			await this.page
				.frameLocator('iframe[id="modalIframe"]')
				.getByRole('option', {name: displayTemplate})
				.click();
		}

		if (maxEntities !== undefined) {
			await this.maxEntitiesInput(widget).fill(String(maxEntities));
		}

		if (maxTerms !== undefined) {
			await this.maxTermsInput.fill(String(maxTerms));
		}

		if (frequencyThreshold !== undefined) {
			await this.frequencyThresholdInput.fill(String(frequencyThreshold));
		}

		if (displayFrequencies === false) {
			await this.displayFrequenciesCheckbox.uncheck();
		}

		await this.saveConfiguration();
	}

	async saveConfiguration() {
		await Promise.all([
			this.page.waitForResponse((response) =>
				response.url().includes('PortletConfigurationPortlet')
			),
			this.configurationSaveButton.click(),
		]);
	}

	async closeFacetConfiguration() {
		await this.page.reload();
	}

	async configureSpecificationFacetOrdering(value: string) {
		await this.specificationFacetConfigurationEditButton.click();
		await this.configurationMenuItem.click();
		await this.specificationFacetOrderSpecificationInput.selectOption(
			value
		);
		await this.configurationSaveButton.click();
		await this.reloadPage();
	}

	async deleteSpecificationPage() {
		await this.selectSpecificationFacetPageInput.click();
		await this.layoutsPage.deletePageButton.click();
		await this.layoutsPage.deleteLayoutModal.waitFor({
			state: 'attached',
		});
		await Promise.all([
			this.layoutsPage.deleteLayoutModal.click(),
			this.page.waitForResponse(
				(resp) =>
					resp.status() === 200 &&
					resp
						.url()
						.includes(
							'p_p_id=com_liferay_layout_admin_web_portlet_GroupPagesPortlet'
						)
			),
		]);
	}

	async goto() {
		await this.layoutsPage.goto();
	}

	async goToPage() {
		await this.layoutsPage.goToPages();
		await Promise.all([
			this.pageLabel.click(),
			this.page.waitForResponse(
				(resp) =>
					resp.status() === 200 &&
					resp.url().includes('specification-facet-page')
			),
		]);
	}

	async reloadPage() {
		await this.page.reload();
	}
}
