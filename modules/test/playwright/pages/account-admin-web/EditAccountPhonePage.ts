/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Locator, Page} from '@playwright/test';

export class EditAccountPhonePage {
	readonly extensionLabel: Locator;
	readonly numberInput: Locator;
	readonly numberLabel: Locator;
	readonly page: Page;
	readonly saveButton: Locator;

	constructor(page: Page) {
		this.extensionLabel = page.getByLabel('Phone Extension');
		this.numberInput = page.getByRole('textbox', {name: 'Number'});
		this.numberLabel = page.getByLabel('Phone Number');
		this.page = page;
		this.saveButton = page.getByRole('button', {name: 'Save'});
	}

	async updatePhoneNumber(numberInput: string) {
		await this.numberInput.fill(numberInput);
		await this.saveButton.click();
	}
}
