/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {FrameLocator, Locator, Page} from '@playwright/test';

import {CommerceDNDTablePage} from '../commerceDNDTablePage';

type TPinType = 'diagram' | 'external' | 'sku';

type TPinFields = {
	diagram?: string;
	label?: string;
	quantity?: number;
	sequence?: string;
	sku?: string;
	type?: TPinType;
};

export class CommerceAdminProductDetailsDiagramPage extends CommerceDNDTablePage {
	readonly autocompleteItem: (value: string) => Locator;
	readonly changeImageButton: Locator;
	readonly compressButton: Locator;
	readonly diagram: Locator;
	readonly diagramFile: (name: string) => Locator;
	readonly diagramImage: Locator;
	readonly diagramTypeSelect: Locator;
	readonly diagramImageNamed: (name: string) => Locator;
	readonly dragAndDropImages: Locator;
	readonly expandButton: Locator;
	readonly mappedProductRow: (value: string) => Locator;
	readonly mappedProductRowAt: (index: number) => Locator;
	readonly page: Page;
	readonly pin: (sequence: string) => Locator;
	readonly pinMarker: (sequence: string) => Locator;
	readonly pinRadiusHandler: (sequence: string) => Locator;
	readonly missingFileError: Locator;
	readonly pinCancelButton: Locator;
	readonly pinDeleteButton: Locator;
	readonly pinDiagramInput: Locator;
	readonly pinForm: Locator;
	readonly pinLabelInput: Locator;
	readonly pinPositionInput: Locator;
	readonly pinQuantityInput: Locator;
	readonly pinRadius: (size: string) => Locator;
	readonly pinSaveButton: Locator;
	readonly pinSizeButton: Locator;
	readonly pinSkuInput: Locator;
	readonly pinTypeSelect: Locator;
	readonly pinUpdateButton: Locator;
	readonly removeImageButton: Locator;
	readonly selectFileButton: Locator;
	readonly svgPin: (sequence: string) => Locator;
	readonly selectFileFrame: FrameLocator;
	readonly selectFileModal: Locator;
	readonly zoomHandler: Locator;
	readonly zoomSelect: Locator;

	constructor(page: Page) {
		super(
			page,
			'#_com_liferay_commerce_product_definitions_web_internal_portlet_CPDefinitionsPortlet_fm .fds table'
		);
		this.autocompleteItem = (value: string) =>
			page.locator('.autocomplete-items').getByText(value, {exact: true});
		this.changeImageButton = page.locator('.browse-image');
		this.compressButton = page.getByRole('button', {name: 'Compress'});
		this.diagram = page.locator('.shop-by-diagram');
		this.diagramFile = (name: string) =>
			page.locator(`.image-wrapper img[src*="${name}"]`);
		this.diagramImage = page.locator('.view-wrapper g image');
		this.diagramTypeSelect = page.locator('select[id$="type"]');
		this.diagramImageNamed = (name: string) =>
			page.locator(`.view-wrapper g image[href*="${name}"]`);
		this.expandButton = page.getByRole('button', {name: 'Expand'});
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
		this.pin = (sequence: string) =>
			page.locator("[class='pin-node-text']").filter({hasText: sequence});
		this.missingFileError = page.getByText(
			'Please select an existing file.'
		);
		this.pinForm = page.locator('.diagram-admin-tooltip');
		this.pinMarker = (sequence: string) =>
			this.pin(sequence).locator('../..');
		this.pinRadiusHandler = (sequence: string) =>
			this.pin(sequence).locator('..');
		this.pinRadius = (size: string) =>
			page.getByRole('menuitem', {exact: true, name: size});
		this.pinSizeButton = this.diagram
			.locator('nav')
			.first()
			.getByRole('button')
			.first();
		this.pinCancelButton = this.pinForm.getByRole('button', {
			exact: true,
			name: 'Cancel',
		});
		this.pinDeleteButton = this.pinForm.getByRole('button', {
			exact: true,
			name: 'Delete',
		});
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
		this.pinUpdateButton = this.pinForm.getByRole('button', {
			exact: true,
			name: 'Update',
		});
		this.removeImageButton = page.getByTitle('Remove Image');
		this.selectFileButton = page.getByRole('button', {name: 'Select File'});
		this.selectFileFrame = page.frameLocator('iframe[title="Select File"]');
		this.selectFileModal = page.locator('.modal-content');

		this.svgPin = (sequence: string) =>
			this.diagram
				.locator('text.pin')
				.filter({hasText: new RegExp(`^${sequence}$`)});
		this.zoomHandler = this.diagram.locator('g.zoom-handler');
		this.zoomSelect = this.diagram.locator('select');
	}

	async addPin({
		position,
		...fields
	}: TPinFields & {
		position: {x: number; y: number};
		sequence: string;
		type: TPinType;
	}) {
		await this.diagramImage.click({position});

		await this.fillPinForm(fields);

		await this.pinSaveButton.click();
	}

	async deletePin(sequence: string) {
		await this.pin(sequence).click();

		await this.pinDeleteButton.click();
	}

	async editPin(sequence: string, fields: TPinFields) {
		await this.pin(sequence).click();

		await this.fillPinForm(fields);

		await this.pinUpdateButton.click();
	}

	async fillPinForm({
		diagram,
		label,
		quantity,
		sequence,
		sku,
		type,
	}: TPinFields) {
		if (sequence !== undefined) {
			await this.pinPositionInput.fill(sequence);
		}

		if (type !== undefined) {
			await this.pinTypeSelect.selectOption(type);
		}

		if (label !== undefined) {
			await this.pinLabelInput.fill(label);
		}

		if (quantity !== undefined) {
			await this.pinQuantityInput.fill(String(quantity));
		}

		const value = sku ?? diagram;

		if (value !== undefined) {
			await (
				sku !== undefined ? this.pinSkuInput : this.pinDiagramInput
			).fill(value);

			await this.autocompleteItem(value).click();
		}
	}

	async setPinSize(size: 'Small' | 'Medium' | 'Large') {
		if (!(await this.pinRadius(size).isVisible())) {
			await this.pinSizeButton.click();
		}

		await this.pinRadius(size).click();

		await this.pinSizeButton.click();
	}

	async toggleExpanded() {
		const button = (await this.expandButton.isVisible())
			? this.expandButton
			: this.compressButton;

		await button.click();
	}

	async removeDiagramImage() {
		await this.removeImageButton.click();
	}

	async uploadDiagramImage(filePath: string, replace = false) {
		await (
			replace ? this.changeImageButton : this.selectFileButton
		).click();

		await this.selectFileFrame
			.locator('input[type="file"]')
			.setInputFiles(filePath);

		await this.selectFileFrame
			.getByRole('button', {exact: true, name: 'Add'})
			.click();

		await this.diagramImage.waitFor({state: 'attached'});
	}

	async dragFrom(locator: Locator, delta: {x: number; y: number}) {
		await locator.hover();

		const box = await locator.boundingBox();

		const fromX = box.x + box.width / 2;
		const fromY = box.y + box.height / 2;

		await this.page.mouse.down();
		await this.page.mouse.move(fromX + delta.x, fromY + delta.y, {
			steps: 10,
		});
		await this.page.mouse.up();
	}

	async mapSvgPin(sequence: string, fields: TPinFields) {
		await this.svgPin(sequence).first().click();

		await this.fillPinForm(fields);

		if (await this.pinSaveButton.isVisible()) {
			await this.pinSaveButton.click();
		}
		else {
			await this.pinUpdateButton.click();
		}
	}

	async setDiagramType(type: 'default' | 'svg') {
		const patched = this.page.waitForResponse(
			(response) =>
				response.url().includes('/diagrams/') &&
				response.request().method() === 'PATCH'
		);

		await this.diagramTypeSelect.selectOption(`diagram.type.${type}`);

		const response = await patched;

		if (!response.ok()) {
			throw new Error(
				`Diagram type PATCH failed with ${response.status()} at ${response.url()}`
			);
		}

		await this.page.reload();
	}

	async goToDragAndDropImages() {
		await this.selectFileButton.click();
		await this.selectFileModal.isVisible();
	}
}
