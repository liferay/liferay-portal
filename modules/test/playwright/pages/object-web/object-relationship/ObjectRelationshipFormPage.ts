/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Locator, Page} from '@playwright/test';

export class ObjectRelationshipFormPage {
	readonly inheritanceCheckbox: Locator;
	readonly inheritanceInfo: Locator;
	readonly labelInput: Locator;
	readonly manyRecordsOfInput: Locator;
	readonly manyRecordsOfSelect: Locator;
	readonly nameInput: Locator;
	readonly oneRecordOfInput: Locator;
	readonly page: Page;
	readonly parameterSelect: Locator;
	readonly reverseOrderButton: Locator;
	readonly saveButton: Locator;
	readonly typeSelect: Locator;

	constructor(page: Page, formContainerSelector: string) {
		this.inheritanceCheckbox = page
			.locator(formContainerSelector)
			.getByLabel('Enable Inheritance');
		this.inheritanceInfo = page.getByText(
			'Info:When enabled and the relationship field is filled, permissions are inherited, all API endpoints are grouped under the parent.'
		);
		this.labelInput = page
			.locator(formContainerSelector)
			.getByLabel('LabelMandatory');
		this.manyRecordsOfInput = page
			.locator('.form-group')
			.getByLabel('Many Records OfMandatory');
		this.manyRecordsOfSelect = page
			.locator(formContainerSelector)
			.getByLabel('Many Records OfMandatory');
		this.nameInput = page
			.locator(formContainerSelector)
			.getByLabel('NameMandatory');
		this.oneRecordOfInput = page
			.locator(formContainerSelector)
			.getByLabel('One Record OfMandatory');
		this.page = page;
		this.parameterSelect = page
			.locator(formContainerSelector)
			.getByLabel('Parameter');
		this.reverseOrderButton = page
			.locator(formContainerSelector)
			.getByLabel('reverse-order');
		this.saveButton = page
			.locator(formContainerSelector)
			.getByRole('button', {name: 'Save'});
		this.typeSelect = page
			.locator(formContainerSelector)
			.getByLabel('Type')
			.or(page.getByText('Many to Many'));
	}

	async selectManyRecordsOf(option: string) {
		await this.manyRecordsOfSelect.click();

		await this.page.getByRole('option', {name: option}).click();
	}

	async selectParameter(option: string) {
		await this.parameterSelect.click();

		await this.page.getByRole('option', {name: option}).click();
	}

	async selectType(option: ObjectRelationshipType) {
		await this.typeSelect.click();

		await this.page.getByRole('option', {name: option}).click();
	}
}
