/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {FrameLocator, Locator, Page, expect, test} from '@playwright/test';

import {clickAndExpectToBeVisible} from '../../../utils/clickAndExpectToBeVisible';
import {waitForPageToBeLoaded} from '../../../utils/waitForPageToBeLoaded';
import {ViewObjectDefinitionsPage} from '../ViewObjectDefinitionsPage';

type TAggregationFilter =
	| {
			filterBy: string;
			filterType: 'Is Equal To' | 'Is Not Equal To';
			value: string;
	  }
	| {
			endDate: string;
			filterBy: string;
			filterType: 'Range';
			startDate: string;
	  };

export class ObjectFieldsPage {
	readonly iframeLocator: FrameLocator;
	readonly addObjectFieldButton: Locator;
	readonly advancedTab: Locator;
	readonly aggregationFieldDropdown: Locator;
	readonly aggregationFunctionDropdown: Locator;
	readonly agreggationRelationshipDropdown: Locator;
	readonly deleteObjectFieldOption: Locator;
	readonly editFieldSaveButton: Locator;
	readonly externalReferenceCodeField: Locator;
	readonly fieldsTabItem: Locator;
	readonly filterByDropdown: Locator;
	readonly filterEndDate: Locator;
	readonly filterModal: Locator;
	readonly filterStartDate: Locator;
	readonly filterTypeDropdown: Locator;
	readonly filterValue: Locator;
	readonly limitCharactersToggle: Locator;
	readonly maximumFileSize: Locator;
	readonly maximumNumberOfCharacters: Locator;
	readonly newFilterButton: Locator;
	readonly objectFieldLabelInput: Locator;
	readonly objectFieldNameInput: Locator;
	readonly objectFieldOptionsDropdown: Locator;
	readonly countryPicker: Locator;
	readonly countrySourceDropdown: Locator;
	readonly page: Page;
	readonly saveButton: Locator;
	readonly selectOptionButton: Locator;
	readonly storageFolder: Locator;
	readonly useDefaultValueToggle: Locator;
	readonly viewObjectDefinitionsPage: ViewObjectDefinitionsPage;

	constructor(page: Page) {
		this.iframeLocator = page.frameLocator('iframe');
		this.addObjectFieldButton = page.getByLabel('Add Object Field');
		this.advancedTab = this.iframeLocator.getByRole('tab', {
			name: 'Advanced',
		});
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
		this.fieldsTabItem = page
			.locator('#main-content .nav-item .nav-link')
			.filter({
				hasText: 'Fields',
			});
		this.filterModal = this.iframeLocator.getByRole('dialog', {
			exact: true,
			name: 'Filter',
		});
		this.filterByDropdown = this.filterModal.getByLabel(
			'Filter By' + 'Mandatory'
		);
		this.filterEndDate = this.filterModal.getByLabel('End' + 'Mandatory');
		this.filterStartDate = this.filterModal.getByLabel(
			'Start' + 'Mandatory'
		);
		this.filterTypeDropdown = this.filterModal.getByLabel(
			'Filter Type' + 'Mandatory'
		);
		this.filterValue = this.filterModal.getByLabel('Value' + 'Mandatory');
		this.limitCharactersToggle = this.iframeLocator.getByRole('switch', {
			name: 'Limit Characters',
		});
		this.maximumFileSize = page
			.frameLocator('iframe')
			.getByLabel('Maximum File Size' + 'Mandatory');
		this.maximumNumberOfCharacters = this.iframeLocator.getByLabel(
			'Maximum Number of Characters' + 'Mandatory'
		);
		this.newFilterButton = this.iframeLocator.getByRole('button', {
			name: 'New Filter',
		});
		this.objectFieldLabelInput = page.locator('input[name="label"]');
		this.objectFieldNameInput = page.locator('input[name="name"]');
		this.objectFieldOptionsDropdown = page.getByText('Select an Option');
		this.page = page;
		this.countryPicker = this.iframeLocator.getByRole('combobox', {
			exact: true,
			name: 'Country',
		});
		this.countrySourceDropdown = this.iframeLocator.getByRole('combobox', {
			exact: true,
			name: 'Country Source',
		});
		this.saveButton = page.getByRole('button', {name: 'Save'});
		this.selectOptionButton = this.iframeLocator.getByRole('combobox');
		this.storageFolder = this.iframeLocator.getByLabel(
			'Storage Folder' + 'Mandatory'
		);
		this.useDefaultValueToggle = this.iframeLocator.getByRole('switch', {
			name: 'Use Default Value',
		});
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

		const navigation = this.page.waitForNavigation({
			timeout: 10000,
			waitUntil: 'load',
		});

		await this.saveButton.click();

		await navigation;
	}

	async createAggregationFilter(aggregationFilter: TAggregationFilter) {
		await this.openAggregationFilterTypeOptions(aggregationFilter.filterBy);

		await this.selectAggregationFilterType(aggregationFilter.filterType);

		if (aggregationFilter.filterType === 'Range') {
			await this.filterStartDate.fill(aggregationFilter.startDate);

			await this.filterEndDate.fill(aggregationFilter.endDate);
		}
		else {
			await this.filterValue.fill(aggregationFilter.value);
		}

		await this.saveAggregationFilter();
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

	async disableDefaultValue(objectFieldName: string) {
		await test.step(`Disable default value for '${objectFieldName}'`, async () => {
			await this.openObjectField(objectFieldName);

			await this.advancedTab.click();

			await this.useDefaultValueToggle.uncheck();

			await this.saveObjectField();
		});
	}

	async goto(objectDefinitionLabel: string) {
		await this.viewObjectDefinitionsPage.goto();

		await this.viewObjectDefinitionsPage.clickEditObjectDefinitionLink(
			objectDefinitionLabel
		);

		await this.fieldsTabItem.click();
	}

	async openAggregationFilterTypeOptions(filterBy: string) {
		await this.newFilterButton.click();

		await this.filterModal.waitFor();

		await this.filterByDropdown.click();

		await this.iframeLocator
			.getByRole('option', {exact: true, name: filterBy})
			.click();

		await this.filterTypeDropdown.click();

		return this.iframeLocator.getByRole('option');
	}

	async openObjectField(fieldLabel: string) {
		await test.step(`Open object field '${fieldLabel}'`, async () => {
			await expect(async () => {
				const trigger = this.page
					.getByRole('cell')
					.getByRole('link')
					.filter({hasText: fieldLabel});

				// Click on trigger again here because clickAndExpectToBeVisible
				// won't click again if the side panel is already open.

				await trigger.click();

				// Check that the side panel is opened after clicking.

				await clickAndExpectToBeVisible({
					target: this.page.locator('.fds-side-panel.is-visible'),
					trigger,
				});

				// Check that the correct field was opened.

				await expect(
					this.iframeLocator.locator('#objectFieldLabelInput')
				).toHaveValue(fieldLabel);
			}).toPass();

			await this.page.waitForLoadState('networkidle');
		});
	}

	async closeObjectFieldSidePanel() {
		const cancelButton = this.iframeLocator.getByLabel('Cancel');

		await cancelButton.click();

		await cancelButton.waitFor({state: 'hidden'});
	}

	async saveAggregationFilter() {
		await this.filterModal.getByRole('button', {name: 'Save'}).click();

		await this.filterModal.waitFor({state: 'hidden'});
	}

	async saveObjectField() {
		await this.editFieldSaveButton.click();

		await this.page.locator('.fds-side-panel').waitFor({state: 'hidden'});

		await waitForPageToBeLoaded(this.page);
	}

	async saveObjectFieldReturningNavigation() {
		const navigation = this.page.waitForNavigation({
			timeout: 10000,
			waitUntil: 'load',
		});

		await this.editFieldSaveButton.click();

		return {navigation};
	}

	async selectAggregationFilterType(
		filterType: TAggregationFilter['filterType']
	) {
		await this.iframeLocator
			.getByRole('option', {exact: true, name: filterType})
			.click();
	}

	async selectDefaultValue(value: string) {
		await this.selectOptionButton.click();

		const selectOptionLocator = this.iframeLocator.getByRole('option', {
			exact: true,
			name: value,
		});

		await selectOptionLocator.click();
	}

	async setDefaultValue({
		defaultValue,
		objectFieldBusinessType,
		objectFieldName,
	}: {
		defaultValue: string;
		objectFieldBusinessType: string;
		objectFieldName: string;
	}) {
		await test.step(`Set default value '${defaultValue}' for '${objectFieldName}' (${objectFieldBusinessType})`, async () => {
			await this.openObjectField(objectFieldName);

			await this.advancedTab.click();

			await this.useDefaultValueToggle.check({timeout: 1000});

			if (
				objectFieldBusinessType === 'Boolean' ||
				objectFieldBusinessType === 'Picklist'
			) {
				await this.selectDefaultValue(defaultValue);
			}
			if (objectFieldBusinessType === 'Date') {
				await this.iframeLocator
					.getByPlaceholder('__/__/____')
					.fill(defaultValue);
			}

			if (objectFieldBusinessType === 'DateTime') {
				await this.iframeLocator
					.getByPlaceholder('__/__/____ __:__ _')
					.fill(defaultValue);
			}

			if (
				objectFieldBusinessType === 'Decimal' ||
				objectFieldBusinessType === 'Integer' ||
				objectFieldBusinessType === 'LongInteger' ||
				objectFieldBusinessType === 'PrecisionDecimal'
			) {
				await this.iframeLocator
					.getByPlaceholder('Enter a default value.')
					.fill(defaultValue);
			}

			if (
				objectFieldBusinessType === 'LongText' ||
				objectFieldBusinessType === 'Text'
			) {
				await this.iframeLocator
					.getByLabel('Default ValueMandatory')
					.fill(defaultValue);
			}

			if (objectFieldBusinessType === 'RichText') {
				await this.iframeLocator
					.getByLabel('Rich Text Editor')
					.nth(1)
					.fill(defaultValue);
			}

			await this.saveObjectField();
		});
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
}
