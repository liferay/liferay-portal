/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Locator, Page} from '@playwright/test';

import {waitForAlert} from '../../../../utils/waitForAlert';

export class EditConnectorPage {
	readonly apiSchemaInput: Locator;
	readonly connectorSelect: Locator;
	readonly nameInput: Locator;
	readonly page: Page;
	readonly referenceMark: (field: Locator) => Locator;
	readonly saveButton: Locator;

	constructor(page: Page) {
		this.apiSchemaInput = page.getByRole('textbox', {name: 'API Schema'});
		this.connectorSelect = page.getByRole('combobox', {name: 'Connector'});
		this.nameInput = page.getByRole('textbox', {name: 'Name'});
		this.page = page;
		this.referenceMark = (field) =>
			page
				.locator('.form-group', {has: field})
				.locator('.reference-mark');
		this.saveButton = page.getByRole('button', {name: 'Save'});
	}

	async createConnector({
		connector,
		name,
	}: {
		connector: string;
		name: string;
	}) {
		await this.nameInput.fill(name);

		await this.connectorSelect.selectOption({label: connector});

		await this.saveButton.click();

		await waitForAlert(
			this.page,
			`Success:${name} was published successfully.`
		);
	}
}
