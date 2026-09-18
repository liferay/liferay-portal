/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Download, Locator, Page, expect} from '@playwright/test';

export class ImageEditor {
	readonly fileInput: Locator;
	readonly layerList: Locator;
	readonly page: Page;
	readonly ratioSelect: Locator;
	readonly root: Locator;
	readonly saveButton: Locator;
	readonly sidebar: Locator;
	readonly workspace: Locator;

	constructor(page: Page) {
		this.page = page;
		this.root = page.locator('.image-editor');

		this.fileInput = this.root.locator(
			'.editor-annotate-actions input[type="file"]'
		);
		this.layerList = this.root.locator('.editor-layer-list');
		this.ratioSelect = this.root.getByRole('combobox', {name: 'Ratio'});
		this.saveButton = this.root.getByRole('button', {name: 'Save'});
		this.sidebar = this.root.getByRole('complementary', {
			name: 'Edit Controls',
		});
		this.workspace = this.root.getByRole('region', {
			name: 'Image Workspace',
		});
	}

	async addAnnotation(toolName: string) {
		await this.sidebar
			.getByRole('button', {exact: true, name: toolName})
			.click();
	}

	async addImage(fileName: string, source: Buffer) {
		await this.fileInput.setInputFiles({
			buffer: source,
			mimeType: 'image/png',
			name: fileName,
		});
	}

	async addShape(shapeName: string) {
		await this.addAnnotation('Add Shape');

		await this.page
			.getByRole('grid', {name: 'Add Shape'})
			.getByRole('button', {name: shapeName})
			.click();
	}

	layer(layerName: string): Locator {
		return this.layerList.getByRole('button', {
			exact: true,
			name: layerName,
		});
	}

	async save(): Promise<Download> {
		const download = this.page.waitForEvent('download');

		await this.saveButton.click();

		return download;
	}

	async selectFilter(filter: string) {
		const input = this.root.locator(`[id$="-filter-${filter}"]`);

		const label = this.sidebar.locator(
			`label[for="${await input.getAttribute('id')}"]`
		);

		await label.scrollIntoViewIfNeeded();

		await label.click();

		await expect(input).toBeChecked();
	}

	async selectRatio(ratio: string) {
		await this.ratioSelect.selectOption({label: ratio});
	}
}
