/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Locator, Page} from '@playwright/test';

import {CommerceDNDTablePage} from '../commerceDNDTablePage';

type TPinType = 'diagram' | 'external' | 'sku';

export class CommerceAdminProductDetailsDiagramPage extends CommerceDNDTablePage {
	readonly autocompleteItem: (value: string) => Locator;
	readonly diagramImage: Locator;
	readonly dragAndDropImages: Locator;
	readonly mappedProductRow: (value: string) => Locator;
	readonly mappedProductRowAt: (index: number) => Locator;
	readonly page: Page;
	readonly pinDiagramInput: Locator;
	readonly pinForm: Locator;
	readonly pinLabelInput: Locator;
	readonly pinPositionInput: Locator;
	readonly pinQuantityInput: Locator;
	readonly pinSaveButton: Locator;
	readonly pinSkuInput: Locator;
	readonly pinTypeSelect: Locator;
	readonly selectFileButton: Locator;
	readonly selectFileModal: Locator;

	constructor(page: Page) {
		super(
			page,
			'#_com_liferay_commerce_product_definitions_web_internal_portlet_CPDefinitionsPortlet_fm .fds table'
		);
		this.autocompleteItem = (value: string) =>
			page.locator('.autocomplete-items').getByText(value, {exact: true});
		this.diagramImage = page.locator('.view-wrapper g image');
		this.dragAndDropImages = page
			.frameLocator('iframe[title="Select File"]')
			.getByText('Drag & Drop Your Images or Browse to Upload');
		this.mappedProductRow = (value: string) =>
			page
				.locator('.shop-by-diagram-table tbody tr')
				.filter({hasText: value});
		this.mappedProductRowAt = (index: number) =>
			page.locator('.shop-by-diagram-table tbody tr').nth(index);
		this.page = page;
		this.pinForm = page.locator('.diagram-admin-tooltip');
		this.pinDiagramInput = this.pinForm.locator(
			'xpath=.//input[@id="productNameInput"]/following-sibling::input[1]'
		);
		this.pinLabelInput = this.pinForm.locator('#linkedProductInput');
		this.pinPositionInput = this.pinForm.locator('#sequenceInput');
		this.pinQuantityInput = this.pinForm.locator('#quantityInput');
		this.pinSaveButton = this.pinForm.getByRole('button', {
			exact: true,
			name: 'Save',
		});
		this.pinSkuInput = this.pinForm.locator(
			'xpath=.//input[@id="skuInput"]/following-sibling::input[1]'
		);
		this.pinTypeSelect = this.pinForm.locator('#typeInput');
		this.selectFileButton = page.getByRole('button', {name: 'Select File'});
		this.selectFileModal = page.locator('.modal-content');
	}

	async addPin({
		diagram,
		label,
		position,
		quantity = 1,
		sequence,
		sku,
		type,
	}: {
		diagram?: string;
		label?: string;
		position: {x: number; y: number};
		quantity?: number;
		sequence: string;
		sku?: string;
		type: TPinType;
	}) {
		await this.diagramImage.click({position});

		await this.pinPositionInput.fill(sequence);
		await this.pinTypeSelect.selectOption(type);

		if (type === 'external') {
			await this.pinLabelInput.fill(label);
		}
		else {
			const value = type === 'sku' ? sku : diagram;

			await (
				type === 'sku' ? this.pinSkuInput : this.pinDiagramInput
			).fill(value);

			await this.autocompleteItem(value).click();
		}

		if (type !== 'diagram') {
			await this.pinQuantityInput.fill(String(quantity));
		}

		await this.pinSaveButton.click();
	}

	async goToDragAndDropImages() {
		await this.selectFileButton.click();
		await this.selectFileModal.isVisible();
	}
}
