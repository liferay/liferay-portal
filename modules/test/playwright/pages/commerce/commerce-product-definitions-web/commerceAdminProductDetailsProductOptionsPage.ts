/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {FrameLocator, Locator, Page, expect} from '@playwright/test';

import {CommerceDNDTablePage} from '../commerceDNDTablePage';

export class CommerceAdminProductDetailsProductOptionsPage extends CommerceDNDTablePage {
	readonly addOptionsSearch: Locator;
	readonly createNewOptionsButton: Locator;
	readonly deleteMenuItem: Locator;
	readonly optionActionsButton: Locator;
	readonly optionLink: (optionName: string) => Locator;
	readonly optionSidePanelCancelButton: Locator;
	readonly optionSidePanelFrame: FrameLocator;
	readonly optionValueDeltaPriceInput: Locator;
	readonly optionValueLink: (optionValueName: string) => Locator;
	readonly optionValueQuantityInput: Locator;
	readonly optionValueRow: (optionValueName: string) => Locator;
	readonly optionValueSaveButton: Locator;
	readonly optionValueSidePanelCloseButton: Locator;
	readonly optionValueSidePanelFrame: FrameLocator;
	readonly optionValueSkuDropdownItem: (label: string) => Locator;
	readonly optionValueSkuDropdownItems: Locator;
	readonly optionValueSkuInput: Locator;
	readonly visibleSidePanels: Locator;

	constructor(page: Page) {
		super(
			page,
			'#_com_liferay_commerce_product_definitions_web_internal_portlet_CPDefinitionsPortlet_fm .fds table'
		);

		const sidePanel = '.fds-side-panel.is-visible';
		const sidePanelIframe = `${sidePanel} iframe`;

		this.addOptionsSearch = page.getByPlaceholder(
			'Find or create an option'
		);
		this.createNewOptionsButton = page.getByRole('button', {
			name: 'Create New',
		});
		this.deleteMenuItem = page.getByRole('menuitem', {
			exact: true,
			name: 'Delete',
		});
		this.optionActionsButton = page
			.locator('[data-testid="visualization-mode-table"]')
			.getByRole('button', {exact: true, name: 'Actions'});
		this.optionLink = (optionName: string) =>
			page.getByRole('link', {exact: true, name: optionName});
		this.optionSidePanelFrame = page.frameLocator(sidePanelIframe);
		this.optionValueSidePanelFrame =
			this.optionSidePanelFrame.frameLocator(sidePanelIframe);
		this.optionSidePanelCancelButton = this.optionSidePanelFrame.getByRole(
			'button',
			{
				exact: true,
				name: 'Cancel',
			}
		);
		this.optionValueDeltaPriceInput =
			this.optionValueSidePanelFrame.getByLabel('Delta Price');
		this.optionValueLink = (optionValueName: string) =>
			this.optionSidePanelFrame.getByRole('link', {
				exact: true,
				name: optionValueName,
			});
		this.optionValueQuantityInput = this.optionValueSidePanelFrame.locator(
			'input[name$="_quantity"]'
		);
		this.optionValueRow = (optionValueName: string) =>
			this.optionSidePanelFrame
				.getByRole('row')
				.filter({hasText: optionValueName});
		this.optionValueSaveButton = this.optionValueSidePanelFrame.getByRole(
			'button',
			{
				exact: true,
				name: 'Save',
			}
		);
		this.optionValueSidePanelCloseButton =
			this.optionSidePanelFrame.locator('.side-panel-iframe-close');
		this.optionValueSkuDropdownItem = (label: string) =>
			this.optionValueSidePanelFrame
				.locator('.autocomplete-dropdown-menu')
				.getByText(label, {exact: true});
		this.optionValueSkuDropdownItems =
			this.optionValueSidePanelFrame.locator(
				'.autocomplete-dropdown-menu li'
			);
		this.optionValueSkuInput = this.optionValueSidePanelFrame.locator(
			'#autocomplete-root input[type="text"]'
		);
		this.visibleSidePanels = page.locator(sidePanel);
	}

	async closeOption() {
		await this.optionSidePanelCancelButton.click();

		await expect(this.visibleSidePanels).toHaveCount(0);
	}

	async closeOptionValue() {
		await this.optionValueSidePanelCloseButton.click();

		await expect(this.optionValueSkuInput).toBeHidden();
	}

	async editOptionValue(
		optionValueName: string,
		{
			deltaPrice,
			quantity,
			sku,
			unitOfMeasureKey,
		}: {
			deltaPrice?: number | string;
			quantity: number | string;
			sku: string;
			unitOfMeasureKey?: string;
		}
	) {
		await this.openOptionValue(optionValueName);

		if (deltaPrice) {
			await this.optionValueDeltaPriceInput.fill(String(deltaPrice));
		}

		await this.searchSku(sku);

		const dropdownItemLabel = unitOfMeasureKey
			? `${sku} - ${unitOfMeasureKey}`
			: sku;

		await this.optionValueSkuDropdownItem(dropdownItemLabel).click();

		await expect(this.optionValueQuantityInput).toBeEnabled();

		await this.optionValueQuantityInput.fill(String(quantity));
		await this.optionValueSaveButton.click();

		await expect(this.optionValueRow(optionValueName)).toContainText(
			dropdownItemLabel
		);

		await this.closeOptionValue();
	}

	async getSkuSuggestions(sku: string) {
		await this.searchSku(sku);

		return await this.optionValueSkuDropdownItems.allInnerTexts();
	}

	async openOption(optionName: string) {
		await this.optionLink(optionName).click();

		await expect(this.optionSidePanelCancelButton).toBeVisible();
	}

	async openOptionValue(optionValueName: string) {
		await this.optionValueLink(optionValueName).click();

		await expect(this.optionValueSkuInput).toBeVisible();
	}

	async searchSku(sku: string) {
		await this.optionValueSkuInput.fill(sku);

		await expect(this.optionValueSkuDropdownItems.first()).toBeVisible();
	}
}
