/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Locator, Page, expect} from '@playwright/test';

export class FieldSelectModalPage {
	readonly addFieldsDialog: {
		cancelButton: Locator;
		fields: Locator;
		fieldsTreeview: Locator;
		saveButton: Locator;
	};
	readonly fieldSelectModalContainer: Locator;
	readonly page: Page;

	constructor(page: Page) {
		this.addFieldsDialog = {
			cancelButton: page
				.locator('.liferay-modal')
				.getByRole('button', {name: 'Cancel'}),
			fields: page.locator('.treeview-item'),
			fieldsTreeview: page.locator('.treeview'),
			saveButton: page.locator('.liferay-modal').getByRole('button', {
				exact: true,
				name: 'Save',
			}),
		};
		this.fieldSelectModalContainer = page.locator('.field-select-modal');
		this.page = page;
	}

	async cancelAddFieldsModal() {
		await this.addFieldsDialog.cancelButton.click();
	}

	async checkField({
		dataId,
		expected,
		fieldName,
	}: {
		dataId?: string;
		expected: boolean;
		fieldName: string;
	}) {
		const treeItem = this.fieldSelectModalContainer.locator(
			`.treeview-link[data-id$="${dataId ?? fieldName}"]`
		);

		await treeItem.filter({hasText: fieldName}).click();

		const checkbox = treeItem.locator('input[type="checkbox"]');

		if (expected) {
			await checkbox.check();
			await expect(checkbox).toBeChecked();
		}
		else {
			await checkbox.uncheck();
			await expect(checkbox).not.toBeChecked();
		}
	}

	getFieldCheckboxByLabel(label: string) {
		return this.fieldSelectModalContainer
			.getByRole('treeitem', {name: label})
			.locator('input[type="checkbox"]')
			.first();
	}

	async saveAddFieldsModal() {
		await this.addFieldsDialog.saveButton.click();
	}

	async searchAndSelectField(path: string) {
		const fieldSearch = this.page
			.getByRole('dialog', {name: 'Select Field'})
			.getByPlaceholder('Search');

		const FDS_NESTED_FIELD_NAME_DELIMITER = '.';

		const itemPath = path
			.replace(/\[\]/g, '.')
			.split(FDS_NESTED_FIELD_NAME_DELIMITER)
			.filter((item) => item !== '*');

		const selectedFieldName = itemPath[itemPath.length - 1];
		await fieldSearch.fill(selectedFieldName);

		const checkbox = await this.page
			.locator(`[data-id$=",${path}"]`)
			.getByRole('checkbox');

		if(await checkbox.isEnabled()) {
			await checkbox.check();
		}

		if(await checkbox.isDisabled()) {
			expect(await checkbox.isDisabled());
		}
	}

	async selectField({
		dataId,
		fieldName,
	}: {
		dataId?: string;
		fieldName: string;
	}) {
		await this.checkField({dataId, expected: true, fieldName});
	}

	async unSelectField({
		dataId,
		fieldName,
	}: {
		dataId?: string;
		fieldName: string;
	}) {
		await this.checkField({dataId, expected: false, fieldName});
	}

	async unSelectSelectedFields() {
		await this.page
			.getByRole('dialog', {name: 'Select Field'})
			.getByRole('button', {name: 'Deselect All'})
			.click();
	}
}
