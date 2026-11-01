/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Locator, Page} from '@playwright/test';

import getRandomString from '../../../../utils/getRandomString';
import {waitForAlert} from '../../../../utils/waitForAlert';

export class EditConnectorPage {
	readonly activeToggle: Locator;
	readonly clientIdInput: Locator;
	readonly clientSecretInput: Locator;
	readonly connectorSelect: Locator;
	readonly nameInput: Locator;
	readonly page: Page;
	readonly referenceMark: (field: Locator) => Locator;
	readonly saveButton: Locator;
	readonly urlInput: Locator;

	constructor(page: Page) {
		this.activeToggle = page.getByRole('switch', {name: 'Active'});
		this.clientIdInput = page.getByLabel('Client ID');
		this.clientSecretInput = page.getByLabel('Client Secret');
		this.connectorSelect = page.getByRole('combobox', {name: 'Connector'});
		this.nameInput = page.getByRole('textbox', {name: 'Name'});
		this.page = page;
		this.referenceMark = (field) =>
			page
				.locator('.form-group', {has: field})
				.locator('.reference-mark');
		this.saveButton = page.getByRole('button', {name: 'Save'});
		this.urlInput = page.getByLabel('URL');
	}

	async createConnector({
		active = false,
		connector,
		name,
	}: {
		active?: boolean;
		connector: string;
		name: string;
	}) {
		await this.nameInput.fill(name);

		await this.connectorSelect.selectOption({label: connector});

		await this.clientIdInput.fill(getRandomString());
		await this.clientSecretInput.fill(getRandomString());
		await this.urlInput.fill(getRandomString());

		if (active) {
			await this.activeToggle.click();
		}

		await this.saveButton.click();

		await waitForAlert(
			this.page,
			`Success:${name} was published successfully.`
		);
	}

	async updateConnector({name}: {name: string}) {
		await this.saveButton.click();

		await waitForAlert(
			this.page,
			`Success:${name} was updated successfully.`
		);
	}
}
