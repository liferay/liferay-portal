/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {FrameLocator, Locator, Page} from '@playwright/test';

import {CommerceLayoutsPage} from '../commerce-order-content-web/commerceLayoutsPage';

export class ProductPublisherPage {
	readonly closeButton: Locator;
	readonly configurationFilterButton: Locator;
	readonly configurationFrame: FrameLocator;
	readonly configurationMenuItem: Locator;
	readonly configurationOrderingLink: Locator;
	readonly configurationSaveButton: Locator;
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
		this.closeButton = page.getByLabel('close', {exact: true});
		this.configurationFilterButton = page
			.frameLocator(
				'iframe[title*="Product Publisher"][title*="Configuration"]'
			)
			.getByRole('button', {exact: true, name: 'Filter'});
		this.configurationFrame = page.frameLocator(
			'iframe[title*="Product Publisher"][title*="Configuration"]'
		);
		this.configurationMenuItem = page.getByRole('menuitem', {
			exact: true,
			name: 'Configuration',
		});
		this.configurationOrderingLink = this.configurationFrame.getByRole(
			'link',
			{exact: true, name: 'Ordering'}
		);
		this.configurationSaveButton = this.configurationFrame.getByRole(
			'button',
			{exact: true, name: 'Save'}
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
		this.tagsInput = this.configurationFrame.getByLabel('Tags', {
			exact: true,
		});
	}

	async addProductPublisherTagFilter(tagName: string) {
		await this.optionsButton.click();
		await this.configurationMenuItem.click();

		const expanded =
			await this.configurationFilterButton.getAttribute('aria-expanded');

		if (expanded === 'false') {
			await this.configurationFilterButton.click();
		}

		await this.configurationFrame.getByLabel('add-rule').click();
		await this.tagsInput.fill(tagName);
		await this.tagsInput.press('Enter');
		await this.closeButton.focus();
		await this.configurationSaveButton.click();
		await this.closeButton.click();
	}

	async addProductPublisherWidget() {
		await this.layoutsPage.addWidgetToPage('Product Publisher');
	}

	async removeProductPublisherTagFilter(tagName: string) {
		await this.optionsButton.click();
		await this.configurationMenuItem.click();
		await (await this.removeTagNameButton(tagName)).click();
		await this.closeButton.focus();
		await this.configurationSaveButton.click();
		await this.closeButton.click();
	}

	async goto() {
		await this.layoutsPage.goto();
	}
}
