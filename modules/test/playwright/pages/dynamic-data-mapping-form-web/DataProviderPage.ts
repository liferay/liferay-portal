/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Locator, Page} from '@playwright/test';

export class DataProviderPage {
	readonly addNewDataProviderLink: Locator;
	readonly inputLabelField: Locator;
	readonly inputParameterField: Locator;
	readonly inputTypeSelect: Locator;
	readonly nameInputField: Locator;
	readonly outputLabelField: Locator;
	readonly outputPathField: Locator;
	readonly outputTypeSelect: Locator;
	readonly page: Page;
	readonly passwordInputField: Locator;
	readonly saveButton: Locator;
	readonly timeoutInputField: Locator;
	readonly urlInputField: Locator;
	readonly userNameInputField: Locator;

	constructor(page: Page) {
		this.addNewDataProviderLink = page
			.getByRole('link', {
				name: 'REST Data Provider',
			})
			.first();
		this.inputLabelField = page
			.getByLabel('inputs')
			.getByPlaceholder('Enter a label.');
		this.inputParameterField = page.getByPlaceholder(
			'Enter the parameter.'
		);
		this.inputTypeSelect = page
			.getByLabel('inputs')
			.getByRole('combobox')
			.and(page.getByLabel('Type'));
		this.nameInputField = page.getByPlaceholder(
			"Enter the data provider's"
		);
		this.outputLabelField = page
			.getByLabel('outputs')
			.getByPlaceholder('Enter a label.');
		this.outputPathField = page.getByPlaceholder('Enter the path.');
		this.outputTypeSelect = page
			.getByLabel('outputs')
			.getByRole('combobox')
			.and(page.getByLabel('Type'));
		this.page = page;
		this.passwordInputField = page.getByPlaceholder('Enter a password.');
		this.saveButton = page.getByRole('button', {name: 'Save'});
		this.timeoutInputField = page.getByPlaceholder(
			'Enter time in milliseconds.'
		);
		this.urlInputField = page.getByPlaceholder(
			'Enter the REST service URL.'
		);
		this.userNameInputField = page.getByPlaceholder('Enter a user name.');
	}

	async selectInputType(type: string) {
		await this.inputTypeSelect.click();
		await this.page.getByRole('option', {name: type}).click();
	}

	async selectOutputType(type: string) {
		await this.outputTypeSelect.click();
		await this.page.getByRole('option', {name: type}).click();
	}
}
