/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {FrameLocator, Locator, Page} from '@playwright/test';

import {expandSection} from '../../../utils/expandSection';
import {waitForAlert} from '../../../utils/waitForAlert';

export class SearchResultsPage {
	readonly configurationCloseButton: Locator;
	readonly configurationFrame: FrameLocator;
	readonly configurationPaginationTab: Locator;
	readonly configurationRenderSelectionTab: Locator;
	readonly configurationSaveButton: Locator;
	readonly displayTemplateButton: Locator;
	readonly displayTemplateOption: (displayTemplateName: string) => Locator;
	readonly displayTemplateSelector: Locator;
	readonly numberOfItemsToDisplayInput: Locator;
	readonly page: Page;
	readonly productListRendererButton: Locator;
	readonly productListRendererSelectedOption: Locator;
	readonly useApplicationDisplayTemplateRadio: Locator;

	constructor(page: Page) {
		this.configurationCloseButton = page
			.locator('.modal-header')
			.getByLabel('Close', {exact: true});
		this.configurationFrame = page.frameLocator(
			'iframe[title*="Configuration"]'
		);
		this.configurationPaginationTab = this.configurationFrame.getByRole(
			'tab',
			{exact: true, name: 'Pagination'}
		);
		this.configurationRenderSelectionTab =
			this.configurationFrame.getByRole('tab', {
				exact: true,
				name: 'Render Selection',
			});
		this.configurationSaveButton = this.configurationFrame.getByRole(
			'button',
			{exact: true, name: 'Save'}
		);
		this.displayTemplateButton = this.configurationFrame.getByRole(
			'button',
			{exact: true, name: 'Display Template'}
		);
		this.displayTemplateOption = (displayTemplateName: string) =>
			this.configurationFrame.getByRole('option', {
				exact: true,
				name: displayTemplateName,
			});
		this.displayTemplateSelector = this.configurationFrame.getByRole(
			'combobox',
			{exact: true, name: 'Display Template'}
		);
		this.numberOfItemsToDisplayInput = this.configurationFrame.getByRole(
			'spinbutton',
			{name: 'Number of Items to Display'}
		);
		this.page = page;
		this.productListRendererButton = this.configurationFrame.getByRole(
			'button',
			{exact: true, name: 'Product List Renderer'}
		);
		this.productListRendererSelectedOption = this.configurationFrame
			.getByRole('combobox', {exact: true, name: 'Product List Renderer'})
			.getByRole('option', {selected: true});
		this.useApplicationDisplayTemplateRadio =
			this.configurationFrame.getByRole('radio', {
				exact: true,
				name: 'Use Application Display Template',
			});
	}

	async openRenderSelection() {
		await this.configurationRenderSelectionTab.click();

		await expandSection(this.productListRendererButton);
	}

	async saveConfiguration() {
		await this.configurationSaveButton.click();

		await waitForAlert(
			this.configurationFrame,
			'Success:You have successfully updated the setup.'
		);
	}

	async selectDisplayTemplate(displayTemplateName: string) {
		await this.useApplicationDisplayTemplateRadio.click();

		await waitForAlert(
			this.configurationFrame,
			'Success:You have successfully updated the setup.'
		);

		await expandSection(this.displayTemplateButton);

		await this.displayTemplateSelector.click();
		await this.displayTemplateOption(displayTemplateName).click();
	}

	async setNumberOfItemsToDisplay(numberOfItemsToDisplay: number) {
		await this.configurationPaginationTab.click();

		await this.numberOfItemsToDisplayInput.fill(
			String(numberOfItemsToDisplay)
		);
	}
}
