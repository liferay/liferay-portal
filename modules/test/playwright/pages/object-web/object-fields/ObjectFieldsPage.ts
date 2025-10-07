/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Locator, Page} from '@playwright/test';

import {ViewObjectDefinitionsPage} from '../ViewObjectDefinitionsPage';

export class ObjectFieldsPage {
	readonly addObjectFieldButton: Locator;
	readonly aggregationFieldDropdown: Locator;
	readonly aggregationFunctionDropdown: Locator;
	readonly agreggationRelationshipDropdown: Locator;
	readonly deleteObjectFieldOption: Locator;
	readonly editFieldSaveButton: Locator;
	readonly externalReferenceCodeField: Locator;
	readonly fieldsTabItem: Locator;
	readonly maximumFileSize: Locator;
	readonly objectFieldLabelInput: Locator;
	readonly objectFieldOptionsDropdown: Locator;
	readonly page: Page;
	readonly saveButton: Locator;
	readonly viewObjectDefinitionsPage: ViewObjectDefinitionsPage;

	constructor(page: Page) {
		this.addObjectFieldButton = page.getByLabel('Add Object Field');
		this.aggregationFieldDropdown = page.getByLabel('FieldMandatory');
		this.aggregationFunctionDropdown = page.getByLabel('FunctionMandatory');
		this.agreggationRelationshipDropdown = page.getByLabel(
			'RelationshipMandatory'
		);
		this.deleteObjectFieldOption = page.getByRole('menuitem', {
			name: 'Delete',
		});
		this.editFieldSaveButton = page
			.frameLocator('iframe')
			.getByRole('button', {name: 'Save'});
		this.externalReferenceCodeField = page
			.frameLocator('iframe')
			.locator('[name="externalReferenceCode"]');
		this.fieldsTabItem = page.locator('.nav-item .nav-link').filter({
			hasText: 'Fields',
		});
		this.maximumFileSize = page
			.frameLocator('iframe')
			.getByLabel('Maximum File Size' + 'Mandatory');
		this.objectFieldLabelInput = page.locator('input[name="label"]');
		this.objectFieldOptionsDropdown = page.getByText('Select an Option');
		this.page = page;
		this.saveButton = page.getByRole('button', {name: 'Save'});
		this.viewObjectDefinitionsPage = new ViewObjectDefinitionsPage(page);
	}

	async addObjectField({
		aggregationField,
		aggregationFieldFunction,
		aggregationFieldRelationship,
		attachmentSource,
		autoIncrementInitialValue,
		formulaFieldOutput,
		listTypeDefinitionName,
		objectFieldBusinessType,
		objectFieldLabel,
	}: CreateObjectField) {
		await this.addObjectFieldButton.waitFor();
		await this.addObjectFieldButton.click();

		await this.objectFieldLabelInput.waitFor();
		await this.objectFieldLabelInput.fill(objectFieldLabel);

		await this.objectFieldOptionsDropdown.click();

		await this.page
			.getByRole('option', {exact: true, name: objectFieldBusinessType})
			.click();

		if (objectFieldBusinessType === 'Aggregation') {
			await this.agreggationRelationshipDropdown.click();
			await this.page
				.getByRole('option', {name: aggregationFieldRelationship})
				.click();

			await this.aggregationFunctionDropdown.click();
			await this.page
				.getByRole('option', {name: aggregationFieldFunction})
				.click();

			if (aggregationField) {
				await this.aggregationFieldDropdown.click();
				await this.page
					.getByRole('option', {name: aggregationField})
					.click();
			}
		}

		if (objectFieldBusinessType === 'Attachment') {
			await this.objectFieldOptionsDropdown.click();
			await this.page
				.getByRole('option', {name: attachmentSource})
				.click();
		}

		if (objectFieldBusinessType === 'Auto Increment') {
			await this.page
				.getByRole('spinbutton')
				.fill(autoIncrementInitialValue);
		}

		if (objectFieldBusinessType === 'Formula') {
			await this.objectFieldOptionsDropdown.click();
			await this.page
				.getByRole('option', {name: formulaFieldOutput})
				.click();
		}

		if (
			objectFieldBusinessType === 'Multiselect Picklist' ||
			objectFieldBusinessType === 'Picklist'
		) {
			await this.objectFieldOptionsDropdown.click();
			await this.page
				.getByRole('option', {name: listTypeDefinitionName})
				.click();
		}

		await this.saveButton.click();
	}

	async deleteObjectField(confirmDeletion: boolean, nth: number) {
		await this.page.locator('.cell-item-actions').nth(nth).waitFor();

		await this.page
			.locator('.cell-item-actions')
			.nth(nth)
			.locator('.dropdown-toggle')
			.click();

		await this.deleteObjectFieldOption.click();

		if (confirmDeletion) {
			await this.page.getByRole('button', {name: 'Delete'}).click();
		}
	}

	async deleteObjectFieldByLabel(label: string) {
		await this.page
			.getByRole('row')
			.filter({hasText: label})
			.locator('.dropdown-toggle')
			.click();

		await this.deleteObjectFieldOption.click();

		await this.page.getByRole('button', {name: 'Delete'}).click();
	}

	getMaximumFileSizeErrorMessage({
		maximumFileSizeAllowed,
	}: {
		maximumFileSizeAllowed: string;
	}) {
		return this.page
			.frameLocator('iframe')
			.getByText(
				`File size is larger than the allowed overall maximum upload request size ${maximumFileSizeAllowed} MB.`,
				{exact: true}
			);
	}

	async goto(objectDefinitionLabel: string) {
		await this.viewObjectDefinitionsPage.goto();

		await this.viewObjectDefinitionsPage.clickEditObjectDefinitionLink(
			objectDefinitionLabel
		);

		await this.fieldsTabItem.click();
	}

	async openObjectField(fieldLabel: string) {
		await this.page
			.getByRole('cell')
			.getByRole('link')
			.filter({hasText: fieldLabel})
			.click();
	}
}
