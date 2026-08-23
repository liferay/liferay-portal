/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Locator, Page} from '@playwright/test';

export class FormSettingsModalPage {
	readonly doneButton: Locator;
	readonly objectSelect: Locator;
	readonly page: Page;
	readonly storageTypeSelect: Locator;

	constructor(page: Page) {
		this.doneButton = page.getByRole('button', {name: 'Done'});
		this.objectSelect = page.getByLabel('Select Object');
		this.page = page;
		this.storageTypeSelect = page.getByLabel('Select a Storage Type');
	}

	async clickDoneButton() {
		await this.doneButton.click();
	}

	async clickDoneButtonAndWaitForObjectFields(objectDefinitionId: number) {

		// Closing the settings is what asks for the selected object's fields,
		// and the publish validation reads the fields this ask populates. A
		// publish clicked before the answer lands reads no fields and lets an
		// unmapped form through, so the close waits for the answer it caused.

		const objectFieldsResponsePromise = this.page.waitForResponse(
			(response) =>
				response.ok() &&
				response.request().method() === 'GET' &&
				response
					.url()
					.includes(
						`/o/object-admin/v1.0/object-definitions/${objectDefinitionId}`
					)
		);

		await this.doneButton.click();

		await objectFieldsResponsePromise;
	}

	async selectObject(objectLabel: string) {
		await this.objectSelect.click();

		const option = this.getSelectOptionLocator(objectLabel);
		await option.click();
	}

	async selectStorageType(storageTypeLabel: string) {
		await this.storageTypeSelect.click();

		const option = this.getSelectOptionLocator(storageTypeLabel);
		await option.click();
	}

	getSelectOptionLocator = (optionLabel: string) => {
		return this.page.getByRole('option', {name: optionLabel});
	};
}
