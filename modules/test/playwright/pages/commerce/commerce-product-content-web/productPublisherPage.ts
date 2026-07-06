/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {FrameLocator, Locator, Page, expect} from '@playwright/test';

import {expandSection} from '../../../utils/expandSection';
import {waitForAlert} from '../../../utils/waitForAlert';
import {CommerceLayoutsPage} from '../commerce-order-content-web/commerceLayoutsPage';

export class ProductPublisherPage {
	readonly closeButton: Locator;
	readonly configurationFilterButton: Locator;
	readonly configurationFrame: FrameLocator;
	readonly configurationMenuItem: Locator;
	readonly configurationOrderingLink: Locator;
	readonly configurationProductSelectionButton: Locator;
	readonly configurationProductSelectionTab: Locator;
	readonly configurationRenderSelectionButton: Locator;
	readonly configurationRenderSelectionTab: Locator;
	readonly configurationSaveButton: Locator;
	readonly dataSourceButton: Locator;
	readonly dataSourceInput: Locator;
	readonly displayTemplateButton: Locator;
	readonly displayTemplateSelector: Locator;
	readonly layoutsPage: CommerceLayoutsPage;
	readonly optionsButton: Locator;
	readonly page: Page;
	readonly productCard: (productName: string) => Locator;
	readonly productCardAddToCartButton: (productName: string) => Locator;
	readonly productCardPrice: (
		productName: string,
		productPrice: string
	) => Locator;
	readonly productLink: (productName: string) => Promise<Locator>;
	readonly productSku: (productSku: string) => Promise<Locator>;
	readonly removeTagNameButton: (tagName: string) => Promise<Locator>;
	readonly tagsInput: Locator;

	constructor(page: Page) {
		this.closeButton = page
			.locator('.modal')
			.getByLabel('Close', {exact: true});
		this.configurationFilterButton = page
			.frameLocator(
				'iframe[title*="Product Publisher"][title*="Configuration"]'
			)
			.getByRole('button', {exact: true, name: 'Filter'});
		this.configurationFrame = page.frameLocator(
			'iframe[title*="Configuration"]'
		);
		this.configurationMenuItem = page.getByRole('menuitem', {
			exact: true,
			name: 'Configuration',
		});
		this.configurationOrderingLink = this.configurationFrame.getByRole(
			'link',
			{exact: true, name: 'Ordering'}
		);
		this.configurationProductSelectionButton =
			this.configurationFrame.getByRole('button', {
				name: 'Product Selection',
			});
		this.configurationProductSelectionTab =
			this.configurationFrame.getByRole('tab', {
				name: 'Product Selection',
			});
		this.configurationRenderSelectionButton =
			this.configurationFrame.getByRole('button', {
				name: 'Render Selection',
			});
		this.configurationRenderSelectionTab =
			this.configurationFrame.getByRole('tab', {
				name: 'Render Selection',
			});
		this.configurationSaveButton = this.configurationFrame.getByRole(
			'button',
			{exact: true, name: 'Save'}
		);
		this.dataSourceButton = this.configurationFrame.getByRole('button', {
			name: 'Data Source',
		});
		this.dataSourceInput = this.configurationFrame.locator(
			'[id="_com_liferay_portlet_configuration_web_portlet_PortletConfigurationPortlet_dataSource"]'
		);
		this.displayTemplateButton = this.configurationFrame.getByRole(
			'button',
			{name: 'Display Template'}
		);
		this.displayTemplateSelector = this.configurationFrame.locator(
			'[id="_com_liferay_portlet_configuration_web_portlet_PortletConfigurationPortlet_displayStyle"]'
		);
		this.layoutsPage = new CommerceLayoutsPage(page);
		this.optionsButton = page
			.locator('//section[contains(@id, "CPPublisherPortlet")]')
			.getByLabel('Options');
		this.page = page;
		this.productCard = (productName: string) =>
			this.page.locator('.product-card').filter({hasText: productName});
		this.productCardAddToCartButton = (productName: string) =>
			this.productCard(productName).getByRole('button', {
				exact: true,
				name: 'Add to Cart',
			});
		this.productCardPrice = (productName, productPrice) =>
			this.productCard(productName).getByText(productPrice, {
				exact: true,
			});
		this.productLink = async (productName: string) => {
			return page.getByRole('link', {exact: true, name: productName});
		};
		this.productSku = async (productSku: string) => {
			return page.getByText(productSku);
		};
		this.removeTagNameButton = async (tagName: string) => {
			return this.configurationFrame.getByLabel(`Remove ${tagName}`);
		};
		this.tagsInput = this.configurationFrame.getByRole('combobox', {
			exact: true,
			name: 'Tags',
		});
	}

	async addProductPublisherTagFilter(tagName: string) {
		await expect(async () => {
			await this.optionsButton.click();
			await this.configurationMenuItem.click({timeout: 1000});
		}).toPass();

		const expanded =
			await this.configurationFilterButton.getAttribute('aria-expanded');

		if (expanded === 'false') {
			await this.configurationFilterButton.click();
		}

		await this.configurationFrame.getByLabel('Add Rule').click();
		await this.tagsInput.fill(tagName);
		await this.tagsInput.press('Enter');
		await this.tagsInput.press('Tab');
		await this.closeButton.focus();
		await this.configurationSaveButton.click();
		await this.closeButton.click();
	}

	async addProductPublisherProductSelectionDataSource(
		dataSource: string,
		template?: string
	) {
		await expect(async () => {
			await this.page
				.locator('#wrapper')
				.getByRole('button', {name: 'Options'})
				.click();
			await this.configurationMenuItem.click({timeout: 1000});
		}).toPass();

		await this.configurationProductSelectionTab.click();

		await expandSection(this.configurationProductSelectionButton);

		await this.configurationFrame.getByLabel('Data Source').click();

		await waitForAlert(
			this.configurationFrame,
			'Success:You have successfully updated the setup.'
		);

		await expandSection(this.dataSourceButton);

		await this.dataSourceInput.selectOption(dataSource);
		await this.configurationSaveButton.click();

		await waitForAlert(
			this.configurationFrame,
			'Success:You have successfully updated the setup.'
		);

		if (template) {
			await this.configurationRenderSelectionTab.click();

			await expandSection(this.configurationRenderSelectionButton);

			await this.configurationFrame
				.getByLabel('Use Application Display')
				.click();

			await waitForAlert(
				this.configurationFrame,
				'Success:You have successfully updated the setup.'
			);

			await expandSection(this.displayTemplateButton);

			await this.displayTemplateSelector.click();
			await this.configurationFrame
				.getByRole('option', {name: template})
				.click();
			await this.configurationSaveButton.click();

			await waitForAlert(
				this.configurationFrame,
				'Success:You have successfully updated the setup.'
			);
		}

		await this.closeButton.click();
	}

	async addProductPublisherWidget() {
		await this.layoutsPage.addWidgetToPage('Product Publisher');
	}

	async removeProductPublisherTagFilter(tagName: string) {
		await expect(async () => {
			await this.optionsButton.click();
			await this.configurationMenuItem.click({timeout: 1000});
		}).toPass();
		await (await this.removeTagNameButton(tagName)).click();
		await this.tagsInput.press('Tab');
		await this.closeButton.focus();
		await this.configurationSaveButton.click();
		await this.closeButton.click();
	}

	async goto() {
		await this.layoutsPage.goto();
	}
}
