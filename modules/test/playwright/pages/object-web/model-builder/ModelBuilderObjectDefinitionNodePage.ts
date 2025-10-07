/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {ObjectRelationship} from '@liferay/object-admin-rest-client-js';

import {AddNewObjectRelationshipModalPage} from '../object-relationship/AddObjectRelationshipModalPage';

import type {Locator, Page} from '@playwright/test';

export class ModelBuilderObjectDefinitionNodePage {
	readonly addNewObjectRelationshipModalPage: AddNewObjectRelationshipModalPage;
	readonly addObjectFieldButton: Locator;
	readonly addObjectFieldOrRelationshipButton: Locator;
	readonly addObjectRelationshipButton: Locator;
	readonly deleteObjectDefinitionOption: Locator;
	readonly editObjectDefinitionExternalReferenceCodeButton: Locator;
	readonly modalAddObjectField: Locator;
	readonly modalAddObjectFieldLabelInput: Locator;
	readonly modalDeleteObjectDefinitionConfirmationButton: Locator;
	readonly modalDeleteObjectDefinitionTextField: Locator;
	readonly modalEditObjectDefinitionExternalReferenceCodeInput: Locator;
	readonly newObjectFieldSaveButton: Locator;
	readonly newObjectRelationshipSaveButton: Locator;
	readonly objectFieldBusinessTypeSelect: Locator;
	readonly objectFieldPicklistSelect: Locator;
	readonly objectRelationshipLabelInput: Locator;
	readonly objectRelationshipManyRecordsOf: Locator;
	readonly objectRelationshipTitle: Locator;
	readonly objectRelationshipTypeButton: Locator;
	readonly page: Page;

	constructor(page: Page) {
		this.addNewObjectRelationshipModalPage =
			new AddNewObjectRelationshipModalPage(page);
		this.addObjectFieldButton = page.getByRole('menuitem', {
			exact: true,
			name: 'Add Field',
		});
		this.addObjectFieldOrRelationshipButton = page.getByRole('button', {
			exact: true,
			name: 'Add Field or Relationship',
		});
		this.addObjectRelationshipButton = page.getByRole('menuitem', {
			exact: true,
			name: 'Add Relationship',
		});
		this.deleteObjectDefinitionOption = page.getByRole('menuitem', {
			name: 'Delete Object',
		});
		this.editObjectDefinitionExternalReferenceCodeButton = page
			.getByText('Edit ERC')
			.last();
		this.modalAddObjectField = page.getByLabel('New Field');
		this.modalAddObjectFieldLabelInput = this.modalAddObjectField.getByRole(
			'textbox',
			{name: 'Label'}
		);
		this.modalDeleteObjectDefinitionConfirmationButton = page
			.getByRole('dialog')
			.getByRole('button', {exact: true, name: 'Delete'});
		this.modalEditObjectDefinitionExternalReferenceCodeInput =
			page.getByLabel('External Reference Code' + 'Mandatory');
		this.modalDeleteObjectDefinitionTextField = page.getByPlaceholder(
			'Confirm Object Definition Name'
		);
		this.newObjectFieldSaveButton = page
			.getByLabel('New Field')
			.getByRole('button', {
				name: 'Save',
			});
		this.newObjectRelationshipSaveButton = page
			.getByLabel('New Relationship')
			.getByRole('button', {
				name: 'Save',
			});
		this.objectFieldBusinessTypeSelect = page
			.locator('div.form-group')
			.filter({hasText: /^TypeMandatorySelect an Option$/})
			.getByRole('combobox');
		this.objectFieldPicklistSelect = page
			.locator('div.form-group')
			.filter({hasText: /^PicklistSelect an Option$/})
			.getByRole('combobox');
		this.objectRelationshipLabelInput = page
			.locator('div.form-group')
			.filter({hasText: /^LabelMandatory$/})
			.getByRole('textbox');
		this.objectRelationshipManyRecordsOf =
			page.getByLabel('Many Records Of');
		this.objectRelationshipTitle = page.getByRole('heading', {
			name: 'New Relationship',
		});
		this.objectRelationshipTypeButton = page.getByLabel('Type');
		this.page = page;
	}

	async clickHideFieldsButton(
		objectDefinitionName: string,
		objectDefinitionNodes: Locator
	) {
		await objectDefinitionNodes
			.filter({hasText: objectDefinitionName})
			.getByRole('button', {name: 'Hide Fields'})
			.click();
	}

	async clickObjectDefinitionActionsButton(
		objectDefinitionLabel: string,
		objectDefinitionNodes: Locator
	) {
		await objectDefinitionNodes
			.filter({hasText: objectDefinitionLabel})
			.getByLabel('Show Actions')
			.click();
	}

	async clickShowAllFieldsButton(
		objectDefinitionName: string,
		objectDefinitionNodes: Locator
	) {
		await objectDefinitionNodes
			.filter({hasText: objectDefinitionName})
			.getByRole('button', {name: 'Show All Fields'})
			.click();
	}

	async createObjectField({
		listTypeDefinitionName,
		mandatory,
		objectDefinitionLabel,
		objectDefinitionNodes,
		objectFieldBusinessType,
		objectFieldLabel,
	}: CreateObjectField & {
		mandatory?: boolean;
		objectDefinitionLabel?: string;
		objectDefinitionNodes?: unknown;
	}) {
		await this.openAddNewObjectFieldOrRelationshipModal(
			objectDefinitionLabel,
			objectDefinitionNodes,
			this.addObjectFieldButton
		);

		await this.fillObjectFieldLabelInput(objectFieldLabel);

		await this.selectNewObjectFieldBusinessTypeOption(
			objectFieldBusinessType
		);

		if (objectFieldBusinessType === 'Picklist') {
			await this.objectFieldPicklistSelect.click();
			await this.page
				.getByRole('option', {
					exact: true,
					name: listTypeDefinitionName,
				})
				.click();
		}

		if (mandatory) {
			await this.page.getByLabel('Mandatory', {exact: true}).check();
		}

		await this.newObjectFieldSaveButton.click();
	}

	async createObjectRelationship({
		manyRecordsOf,
		objectDefinitionLabel,
		objectDefinitionNodes,
		objectRelationshipLabel,
		objectRelationshipType,
	}: {
		manyRecordsOf: string;
		objectDefinitionLabel: string;
		objectDefinitionNodes: unknown;
		objectRelationshipLabel: string;
		objectRelationshipType: ObjectRelationshipType;
	}): Promise<ObjectRelationship> {
		await this.openAddNewObjectFieldOrRelationshipModal(
			objectDefinitionLabel,
			objectDefinitionNodes,
			this.addObjectRelationshipButton
		);

		const objectRelationship =
			await this.addNewObjectRelationshipModalPage.handleForm({
				manyRecordsOf,
				objectRelationshipLabel,
				type: objectRelationshipType,
			});

		return objectRelationship;
	}

	async deleteObjectDefinition(objectDefinitionName: string) {
		await this.deleteObjectDefinitionOption.click();
		await this.modalDeleteObjectDefinitionTextField.click();
		await this.modalDeleteObjectDefinitionTextField.fill(
			objectDefinitionName
		);
		await this.modalDeleteObjectDefinitionConfirmationButton.click();
	}

	async fillObjectFieldLabelInput(objectFieldLabel: string) {
		await this.modalAddObjectFieldLabelInput.fill(objectFieldLabel);
	}

	getLinkedObjectDefinitionIconLocator(
		objectDefinitionLabel: string,
		objectDefinitionNodes: Locator
	) {
		return objectDefinitionNodes
			.filter({
				hasText: objectDefinitionLabel,
			})
			.locator('svg.lexicon-icon-link');
	}

	async openAddNewObjectFieldOrRelationshipModal(
		objectDefinitionLabel: string,
		objectDefinitionNodes: unknown,
		openModalButton: Locator
	) {
		await (objectDefinitionNodes as Locator)
			.filter({hasText: objectDefinitionLabel})
			.getByRole('button', {name: 'Add Field or Relationship'})
			.click();

		await openModalButton.click();
	}

	async selectNewObjectFieldBusinessTypeOption(
		objectFieldBusinessType: string
	) {
		await this.objectFieldBusinessTypeSelect.click();
		await this.page
			.getByRole('option', {
				exact: true,
				name: String(objectFieldBusinessType),
			})
			.click();
	}
}
