/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import type {Locator, Page} from '@playwright/test';

export class ModelBuilderRightSidebarPage {
	readonly advancedTab: Locator;
	readonly allowStandaloneEntriesToggle: Locator;
	readonly deleteButton: Locator;
	readonly deleteObjectRelationshipButton: Locator;
	readonly deleteTrashButton: Locator;
	readonly inheritanceCheckbox: Locator;
	readonly managePicklistsButton: Locator;
	readonly modalDeleteObjectRelationshipTextField: Locator;
	readonly objectDefinitionActivateObject: Locator;
	readonly objectDefinitionEntryTitleField: Locator;
	readonly objectDefinitionLabelLocalizationButton: Locator;
	readonly objectDefinitionPanelLink: Locator;
	readonly objectDefinitionPluralLabel: Locator;
	readonly objectDefinitionPluralLabelLocalizationButton: Locator;
	readonly objectDefinitionScope: Locator;
	readonly objectDefinitionSeo: Locator;
	readonly objectRelationshipDeletionType: Locator;
	readonly objectRelationshipManyRecordsOf: Locator;
	readonly objectRelationshipOneRecordOf: Locator;
	readonly objectRelationshipType: Locator;
	readonly page: Page;
	readonly selectOptionButton: Locator;
	readonly sidebarDescriptionInput: Locator;
	readonly sidebarLabelInput: Locator;
	readonly sidebarName: Locator;
	readonly useDefaultValueToggle: Locator;

	constructor(page: Page) {
		this.advancedTab = page.getByRole('tab', {
			name: 'Advanced',
		});
		this.allowStandaloneEntriesToggle = page.getByRole('switch', {
			name: 'Allow Standalone Entries',
		});
		this.deleteButton = page.getByRole('button', {
			exact: true,
			name: 'Delete',
		});
		this.deleteObjectRelationshipButton = page.getByLabel(
			'Delete Relationship'
		);
		this.deleteTrashButton = page
			.getByRole('tabpanel')
			.getByTitle('Delete');
		this.inheritanceCheckbox = page.getByRole('checkbox');
		this.managePicklistsButton = page.getByRole('button', {
			name: 'Manage Picklists',
		});
		this.modalDeleteObjectRelationshipTextField = page.getByPlaceholder(
			'Confirm Relationship Name'
		);
		this.objectDefinitionActivateObject =
			page.getByLabel('Activate Object');
		this.objectDefinitionEntryTitleField =
			page.getByLabel('Entry Title Field');
		this.sidebarDescriptionInput = page.getByRole('textbox', {
			exact: true,
			name: 'Description',
		});
		this.sidebarLabelInput = page.getByLabel('Label' + 'Mandatory', {
			exact: true,
		});
		this.sidebarName = page.getByLabel('Name' + 'Mandatory', {
			exact: true,
		});
		this.objectDefinitionLabelLocalizationButton = page
			.locator('[aria-label="Open Localizations"]')
			.first();
		this.objectDefinitionPanelLink = page.getByLabel('Panel Link');
		this.objectDefinitionPluralLabel = page.getByLabel('Plural Label');
		this.objectDefinitionPluralLabelLocalizationButton = page
			.locator('[aria-label="Open Localizations"]')
			.last();
		this.objectDefinitionScope = page.getByLabel('Scope');
		this.objectDefinitionSeo = page.getByLabel(
			"Allow Overriding an Entry's Friendly URL"
		);
		this.objectRelationshipDeletionType = page.getByLabel('Deletion Type');
		this.objectRelationshipManyRecordsOf =
			page.getByLabel('Many Records Of');
		this.objectRelationshipOneRecordOf = page.getByLabel('One Record Of');
		this.objectRelationshipType = page.getByLabel('Type');
		this.page = page;
		this.selectOptionButton = page.getByRole('combobox');
		this.useDefaultValueToggle = page.getByRole('switch', {
			name: 'Use Default Value',
		});
	}

	async deleteObjectRelationship(objectRelationshipName: string) {
		await this.deleteObjectRelationshipButton.click();
		await this.modalDeleteObjectRelationshipTextField.click();
		await this.modalDeleteObjectRelationshipTextField.fill(
			objectRelationshipName
		);
		await this.deleteButton.click();
	}

	async selectDefaultValue(value: string) {
		await this.selectOptionButton.click();

		const selectOptionLocator = this.page.getByRole('option', {
			exact: true,
			name: value,
		});

		await selectOptionLocator.click();
	}

	async setDefaultValue(objectFieldBusinessType: string, value: string) {
		await this.advancedTab.click();

		await this.useDefaultValueToggle.check({timeout: 1000});

		if (
			objectFieldBusinessType === 'Boolean' ||
			objectFieldBusinessType === 'Picklist'
		) {
			await this.selectDefaultValue(value);
		}

		if (
			objectFieldBusinessType === 'LongText' ||
			objectFieldBusinessType === 'Text'
		) {
			await this.page
				.getByPlaceholder('Enter a default value.')
				.pressSequentially(value);
		}
	}

	getRightSidebarLocator(createNewObjectDefinitionButton: Locator) {
		return this.page.getByRole('tabpanel').filter({
			hasNot: createNewObjectDefinitionButton,
		});
	}
}
