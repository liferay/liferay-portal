/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Locator, Page} from '@playwright/test';

export class ModalAddObjectValidationPage {
	readonly objectValidationLabelInput: Locator;
	readonly objectValidationTypeSelect: Locator;
	readonly page: Page;
	readonly saveButton: Locator;

	constructor(page: Page) {
		this.objectValidationLabelInput = page.locator('input[name="label"]');
		this.objectValidationTypeSelect = page.getByRole('combobox').filter({
			hasText: 'Expression Builder',
		});
		this.page = page;
		this.saveButton = page.getByText('Save');
	}

	async fillObjectValidationInputs(
		objectValidationLabel: string,
		objectValidationType: string
	) {
		await this.objectValidationLabelInput.fill(objectValidationLabel);

		await this.objectValidationTypeSelect.click();
		await this.page
			.getByRole('option', {name: objectValidationType})
			.dispatchEvent('click');

		await this.saveButton.click();
	}
}
