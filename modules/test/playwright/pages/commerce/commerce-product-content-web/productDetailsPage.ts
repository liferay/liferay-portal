/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {FrameLocator, Locator, Page} from '@playwright/test';

import {CommerceLayoutsPage} from '../commerce-order-content-web/commerceLayoutsPage';

export class ProductDetailsPage {
	readonly addToCartButton: Locator;
	readonly attachments: Locator;
	readonly attachmentItem: (title: string) => Promise<Locator>;
	readonly attachmentItems: Locator;
	readonly diagramPin: (pinSequence: string) => Promise<Locator>;
	readonly downloadAttachmentLink: Locator;
	readonly downloadSampleField: (
		downloadSampleText: string
	) => Promise<Locator>;
	readonly fullDescriptionField: (
		fullDescription: string
	) => Promise<Locator>;
	readonly gtinField: (gtin: string) => Promise<Locator>;
	readonly layoutsPage: CommerceLayoutsPage;
	readonly mappedProductAddToCartButton: Locator;
	readonly mappedProductCheckbox: Locator;
	readonly mpnField: (mpn: string) => Promise<Locator>;
	readonly nameField: (name: string) => Promise<Locator>;
	readonly optionSelector: (optionName: string) => Locator;
	readonly page: Page;
	readonly pageTitle: Locator;
	readonly paginationText: (text: string) => Locator;
	readonly pinAddToCartButton: Locator;
	readonly priceContainer: Locator;
	readonly priceField: (
		price: string,
		container?: Locator | Page
	) => Promise<Locator>;
	readonly productNameHeading: (productName: string) => Promise<Locator>;
	readonly promoPriceField: (
		promoPrice: string,
		container?: Locator | Page
	) => Promise<Locator>;
	readonly replacementsSearchBar: Locator;
	readonly replacementsSearchButton: Locator;
	readonly replacementsTab: Locator;
	readonly replacementsTableCell: (cellValue: string) => Locator;
	readonly selectDocumentFrame: FrameLocator;
	readonly selectedDocumentLabel: Locator;
	readonly selectOption: (
		optionLabel: string,
		optionName: string
	) => Promise<string[]>;
	readonly shortDescriptionField: (
		shortDescription: string
	) => Promise<Locator>;
	readonly skuField: (sku: string) => Promise<Locator>;
	readonly uomCombobox: Locator;
	readonly uomTable: (uomTableCell: string) => Promise<Locator>;
	readonly viewButton: Locator;

	constructor(page: Page) {
		this.addToCartButton = page
			.getByRole('button', {exact: true, name: 'Add to Cart'})
			.first();
		this.attachments = page.locator(
			'#_com_liferay_commerce_product_content_web_internal_portlet_CPContentPortlet_navCPMedia'
		);
		this.attachmentItem = async (title: string) => {
			return page.getByText(title);
		};
		this.attachmentItems = this.attachments.locator('li.list-group-item');
		this.diagramPin = async (pinSequence: string) => {
			return page
				.locator("[class='pin-node-text']")
				.filter({hasText: pinSequence});
		};
		this.downloadAttachmentLink = page.getByRole('link', {
			exact: true,
			name: 'Download',
		});
		this.downloadSampleField = async (downloadSampleText: string) => {
			return page.getByRole('link', {name: downloadSampleText});
		};
		this.fullDescriptionField = async (fullDescription: string) => {
			return page.getByText(fullDescription, {exact: true});
		};
		this.gtinField = async (gtin: string) => {
			return page.getByText(gtin);
		};
		this.layoutsPage = new CommerceLayoutsPage(page);
		this.mappedProductAddToCartButton = page.getByRole('button', {
			name: 'Add Selected Product(s) to',
		});
		this.mappedProductCheckbox = page.getByLabel('Select SKU');
		this.mpnField = async (mpn: string) => {
			return page.getByText(mpn, {exact: true});
		};
		this.nameField = async (name: string) => {
			return page.getByRole('heading', {exact: true, name});
		};
		this.optionSelector = (optionName: string) => {
			return page.getByLabel(optionName);
		};
		this.page = page;
		this.paginationText = (text: string) => page.getByText(text);
		this.pinAddToCartButton = page
			.locator("[class='diagram-tooltip']")
			.getByRole('button');
		this.priceContainer = page.locator('div.price-container');
		this.priceField = async (price: string, container = this.page) => {
			return container.getByText(price, {exact: true});
		};
		this.productNameHeading = async (productName) => {
			return page.getByRole('heading', {name: productName});
		};
		this.promoPriceField = async (
			promoPrice: string,
			container = this.page
		) => {
			return container.getByText(promoPrice);
		};
		this.replacementsSearchBar = page
			.getByTestId('management-toolbar')
			.getByPlaceholder('Search');
		this.replacementsSearchButton = page.getByRole('button', {
			name: 'Search',
		});
		this.replacementsTab = page.getByRole('tab', {name: 'Replacements'});
		this.replacementsTableCell = (cellValue: string) =>
			page.getByRole('cell', {name: cellValue});
		this.selectDocumentFrame = page.frameLocator(
			'iframe[title="Select Document"]'
		);
		this.selectedDocumentLabel = page.getByLabel('File', {exact: true});
		this.selectOption = (optionLabel: string, optionName: string) =>
			page.getByLabel(optionName).selectOption({label: optionLabel});
		this.shortDescriptionField = async (shortDescription: string) => {
			return page.getByText(shortDescription);
		};
		this.skuField = async (sku: string) => {
			return page.getByText(sku, {exact: true});
		};
		this.uomCombobox = page.getByRole('combobox', {exact: true});
		this.uomTable = async (cellValue: string) => {
			return page.getByRole('cell', {name: cellValue});
		};
		this.viewButton = page.getByLabel('View');
	}

	async goto() {
		await this.layoutsPage.goto();
	}
}
