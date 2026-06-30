/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Locator, Page} from '@playwright/test';

export class FileSizeMimetypesForm {
	readonly addOptionButton: Locator;
	readonly page: Page;

	constructor(page: Page) {
		this.addOptionButton = page.getByRole('button', {name: 'Add Option'});
		this.page = page;
	}

	async addRow() {
		await this.addOptionButton.click();
	}

	async fillRow(index: number, mimeType: string, size: string) {
		await this.mimeTypeInput(index).fill(mimeType);
		await this.sizeInput(index).fill(size);
	}

	mimeTypeInput(index: number): Locator {
		return this.page.locator(`input[name$="mimeType_${index}"]`);
	}

	async removeRow(index: number) {
		await this.page
			.locator('.file-size-mimetypes-item')
			.nth(index)
			.getByRole('button', {name: 'Remove'})
			.click();
	}

	sizeInput(index: number): Locator {
		return this.page.locator(`input[name$="size_${index}"]`);
	}
}
