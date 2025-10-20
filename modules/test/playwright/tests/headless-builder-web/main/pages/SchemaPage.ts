/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Locator, Page} from '@playwright/test';

export class SchemaPage {
	readonly page: Page;

	readonly descriptionInput: Locator;
	readonly nameInput: Locator;
	readonly propertiesTab: Locator;
	readonly successSaveMessage: Locator;

	constructor(page: Page) {
		this.page = page;

		this.descriptionInput = page.getByLabel('Description');
		this.nameInput = page.getByLabel('Name');
		this.propertiesTab = page.getByRole('tab', {name: 'Properties'});
		this.successSaveMessage = page.getByText(
			'API schema changes were saved.'
		);
	}

	async fillDescription(description: string): Promise<void> {
		await this.descriptionInput.fill(description);
	}

	async fillName(name: string): Promise<void> {
		await this.nameInput.fill(name);
	}

	async goTo(schemaName: string): Promise<void> {
		await this.page.getByRole('link', {name: schemaName}).click();
	}

	async goToPropertiesTab(): Promise<void> {
		await this.propertiesTab.click();
	}
}
