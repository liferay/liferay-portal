/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Locator, Page, expect} from '@playwright/test';

import {
	IClientExtensionFilter,
	IDateRangeFilter,
	ISelectionFilterApiHeadless,
	ISelectionFilterPicklist,
} from '../../../../../utils/types';
import {FieldSelectModalPage} from '../../components/FieldSelectModalPage';
import {DataSetPage} from '../DataSetPage';

interface NewFilterForm {
	cancelButton: Locator;
	closeButton: Locator;
	filterByDropdown: Locator;
	filterBySelect: Locator;
	filterBySelectButton: Locator;
	formFeedback: Locator;
	modalBody: Locator;
	nameInput: Locator;
	saveButton: Locator;
}

interface NewClientExtensionFilterForm extends NewFilterForm {
	clientExtensionDropdown: Locator;
	noClientExtensionsAvailableAlert: Locator;
}

interface NewSelectionFilterForm extends NewFilterForm {
	filterModeRadioButtons: Locator;
	itemKey: Locator;
	itemLabel: Locator;
	picklistDropdown: Locator;
	preselectedValuesMultiSelect: Locator;
	restApplicationField: Locator;
	restApplicationOptions: Locator;
	restEndpointField: Locator;
	restEndpointOptions: Locator;
	restSchemaField: Locator;
	restSchemaOptions: Locator;
	selectionRadioButtons: Locator;
	sourceTypeDropdown: Locator;
}

interface NewDateRangeFilterForm extends NewFilterForm {
	datePicker: Locator;
	fromDatePickerTrigger: Locator;
	fromInput: Locator;
	toDatePickerTrigger: Locator;
	toInput: Locator;
}

export class FiltersPage {
	readonly activeToggle: Locator;
	private readonly dataSetPage: DataSetPage;
	readonly fieldSelectModalPage: FieldSelectModalPage;

	readonly filterTable: Locator;
	readonly inactiveToggle: Locator;
	readonly newClientExtensionFilterForm: NewClientExtensionFilterForm;
	readonly newDateRangeFilterForm: NewDateRangeFilterForm;
	readonly newFilterButton: Locator;
	private readonly newFilterForm: NewFilterForm;
	readonly newSelectionFilterForm: NewSelectionFilterForm;
	readonly page: Page;
	readonly searchButton: Locator;
	readonly searchInput: Locator;

	constructor(page: Page) {
		this.activeToggle = page.getByLabel('Active', {exact: true});
		this.dataSetPage = new DataSetPage(page);
		this.fieldSelectModalPage = new FieldSelectModalPage(page);
		this.filterTable = page.getByRole('table');
		this.inactiveToggle = page.getByLabel('Inactive', {
			exact: true,
		});
		this.newFilterButton = page
			.getByRole('button', {name: 'New Filter'})
			.and(page.getByTitle('New Filter'));
		this.newFilterForm = {
			cancelButton: page.getByRole('button', {name: 'Cancel'}),
			closeButton: page.getByRole('button', {
				exact: true,
				name: 'close',
			}),
			filterByDropdown: page.locator('.fds-field-name-dropdown-menu'),
			filterBySelect: page.getByLabel('Filter By'),
			filterBySelectButton: page
				.getByRole('button', {name: 'Select'})
				.first(),
			formFeedback: page.locator('.form-feedback-item'),
			modalBody: page.locator('.modal-body'),
			nameInput: page.getByPlaceholder('Add a name'),
			saveButton: page
				.locator('.filter-form-wrapper')
				.getByRole('button', {name: 'Save'}),
		};
		this.newClientExtensionFilterForm = {
			...this.newFilterForm,
			clientExtensionDropdown: page
				.locator('label')
				.filter({hasText: 'Client ExtensionRequired'}),
			noClientExtensionsAvailableAlert: page
				.getByLabel('New Client Extension Filter')
				.locator('div')
				.filter({
					hasText:
						'InfoNo frontend data set filter client extensions are available. Add a client ex',
				})
				.first(),
		};
		this.newDateRangeFilterForm = {
			...this.newFilterForm,
			datePicker: page.getByRole('dialog', {name: 'Choose date'}),
			fromDatePickerTrigger: page
				.locator('div')
				.filter({hasText: /^From$/})
				.getByRole('button'),
			fromInput: page
				.locator('div')
				.filter({hasText: /^From$/})
				.getByPlaceholder('YYYY-MM-DD'),
			toDatePickerTrigger: page
				.locator('div')
				.filter({hasText: /^From$/})
				.getByRole('button'),
			toInput: page
				.locator('div')
				.filter({hasText: /^To$/})
				.getByPlaceholder('YYYY-MM-DD'),
		};
		this.newSelectionFilterForm = {
			...this.newFilterForm,
			filterModeRadioButtons: page.getByText('Filter ModeIncludeExclude'),
			itemKey: page.locator('.fds-filter-item-key'),
			itemLabel: page.locator('.fds-filter-item-label'),
			picklistDropdown: page
				.locator('label')
				.filter({hasText: 'PicklistRequired'}),
			preselectedValuesMultiSelect: page.getByPlaceholder(
				'Select a default value for your filter.'
			),
			restApplicationField: page.getByLabel('REST ApplicationRequired'),
			restApplicationOptions: page.locator(
				'.fds-filter-rest-application-menu'
			),
			restEndpointField: page.getByLabel('REST EndpointRequired'),
			restEndpointOptions: page.locator('.fds-filter-rest-endpoint-menu'),
			restSchemaField: page.getByLabel('REST SchemaRequired'),
			restSchemaOptions: page.locator('.fds-filter-rest-schema-menu'),
			selectionRadioButtons: page.getByText('SelectionMultipleSingle'),
			sourceTypeDropdown: page
				.locator('label')
				.filter({hasText: 'SourceRequired'}),
		};
		this.page = page;
		this.searchButton = page.getByLabel('Search');
		this.searchInput = page.getByPlaceholder('Search');
	}

	async assertFiltersTableRowCount(rowCount: number) {
		await expect(
			this.filterTable.locator('tbody').locator('tr')
		).toHaveCount(rowCount);
	}

	async assertValidationError(text: string) {
		const visualFeedback = this.newFilterForm.formFeedback
			.filter({has: this.page.locator('.form-feedback-indicator')})
			.first();

		await expect(visualFeedback).toBeVisible();

		await expect(visualFeedback).toContainText(text);
	}

	async assertTableCellContent({filterData, page, rowIndex = 0}) {
		await page
			.locator('.orderable-table > tbody > .orderable-table-row')
			.first()
			.waitFor();

		const tableRowContent = await page
			.locator('.orderable-table-row')
			.nth(rowIndex)
			.locator('td');

		const expectedRowContent = [
			filterData.name,
			filterData.fieldName,
			filterData.type,
			filterData.status,
		];

		await expect(tableRowContent).toContainText(expectedRowContent);

		if (!Object.keys(filterData).includes('actionsDropdown')) {
			return;
		}

		if (filterData.actionsDropdown) {
			await expect(tableRowContent.locator('.dropdown')).toBeAttached();
		}
		else {
			await expect(
				tableRowContent.locator('.dropdown')
			).not.toBeAttached();
		}
	}

	async cancelAddFilterForm() {
		await this.newFilterForm.cancelButton.click();
	}

	async closeAddFilterForm() {
		await this.newFilterForm.closeButton.click();
	}

	async createClientExtensionFilter({
		clientExtension,
		filterBy,
		name,
	}: IClientExtensionFilter) {
		await this.openNewFilterForm({
			dropdownItemLabel: 'Client Extension',
		});

		await this.newClientExtensionFilterForm.nameInput.click();
		await this.newClientExtensionFilterForm.nameInput.fill(name);

		await this.newClientExtensionFilterForm.filterBySelectButton
			.first()
			.click();
		await this.fieldSelectModalPage.selectField({
			fieldName: filterBy,
		});

		await this.fieldSelectModalPage.saveAddFieldsModal();

		await this.newClientExtensionFilterForm.clientExtensionDropdown.click();
		await this.page.getByRole('option', {name: clientExtension}).click();
	}

	async createDateRangeFilter({filterBy, from, name, to}: IDateRangeFilter) {
		await this.openNewFilterForm({
			dropdownItemLabel: 'Date Range',
		});

		await this.newDateRangeFilterForm.nameInput.click();
		await this.newDateRangeFilterForm.nameInput.fill(name);

		await this.newDateRangeFilterForm.filterBySelectButton.click();

		const notDateField = 'label';
		await expect(
			this.fieldSelectModalPage.getFieldCheckboxByLabel(notDateField)
		).toBeDisabled();
		await expect(
			this.fieldSelectModalPage.getFieldCheckboxByLabel(filterBy)
		).toBeEnabled();

		await this.fieldSelectModalPage.selectField({
			fieldName: filterBy,
		});

		await this.fieldSelectModalPage.saveAddFieldsModal();

		if (from) {
			await this.newDateRangeFilterForm.fromInput.fill(from);
		}

		if (to) {
			await this.newDateRangeFilterForm.toInput.fill(to);
		}
	}

	async createSelectionFilterApiHeadless({
		filterBy,
		filterMode,
		itemKey,
		itemLabel,
		name,
		preselectedValues,
		restApplication,
		restEndpoint,
		restSchema,
		selectionType,
		sourceType,
	}: ISelectionFilterApiHeadless) {
		await this.openNewFilterForm({
			dropdownItemLabel: 'Selection',
		});

		await this.newSelectionFilterForm.nameInput.click();
		await this.newSelectionFilterForm.nameInput.fill(name);

		await this.newSelectionFilterForm.filterBySelectButton.click();

		const notSelectionFilterField = 'permissions';
		await expect(
			this.fieldSelectModalPage.getFieldCheckboxByLabel(
				notSelectionFilterField
			)
		).toBeDisabled();
		await expect(
			this.fieldSelectModalPage.getFieldCheckboxByLabel(filterBy)
		).toBeEnabled();

		await this.fieldSelectModalPage.selectField({
			fieldName: filterBy,
		});

		await this.fieldSelectModalPage.saveAddFieldsModal();

		await this.newSelectionFilterForm.sourceTypeDropdown.selectOption(
			sourceType
		);

		await this.newSelectionFilterForm.restApplicationField.click();
		await this.newSelectionFilterForm.restApplicationOptions.waitFor();
		await this.newSelectionFilterForm.restApplicationOptions
			.getByRole('option', {exact: true, name: restApplication})
			.click();

		await this.newSelectionFilterForm.restSchemaField.waitFor();
		await this.newSelectionFilterForm.restSchemaField.click();

		await this.newSelectionFilterForm.restSchemaOptions
			.getByRole('option', {exact: true, name: restSchema})
			.click();
		await this.newSelectionFilterForm.restSchemaField.click();

		await this.newSelectionFilterForm.restEndpointField.waitFor();
		await this.newSelectionFilterForm.restEndpointField.click();

		await this.newSelectionFilterForm.restEndpointOptions
			.getByRole('option', {exact: true, name: restEndpoint})
			.click();
		await this.newSelectionFilterForm.restEndpointField.click();

		await this.newSelectionFilterForm.itemKey.click();
		await this.page
			.getByRole('option', {exact: true, name: itemKey})
			.click();
		await this.newSelectionFilterForm.itemKey.click();

		await this.newSelectionFilterForm.itemLabel.click();
		await this.page
			.getByRole('option', {exact: true, name: itemLabel})
			.click();
		await this.newSelectionFilterForm.itemLabel.click();

		await this.newSelectionFilterForm.preselectedValuesMultiSelect.click();
		await this.page
			.getByRole('option', {name: preselectedValues[0]})
			.click();

		await this.page.getByText(selectionType).click();
		await this.page.locator('label').filter({hasText: filterMode}).click();
	}

	async createSelectionFilterPicklist({
		filterBy,
		filterMode,
		name,
		preselectedValues,
		selectionType,
		source,
		sourceType,
	}: ISelectionFilterPicklist) {
		await this.openNewFilterForm({
			dropdownItemLabel: 'Selection',
		});

		await this.newSelectionFilterForm.nameInput.click();
		await this.newSelectionFilterForm.nameInput.fill(name);

		await this.newSelectionFilterForm.filterBySelectButton.click();

		await this.fieldSelectModalPage.selectField({
			fieldName: filterBy,
		});
		await this.fieldSelectModalPage.saveAddFieldsModal();

		await this.newSelectionFilterForm.sourceTypeDropdown.click();
		await this.newSelectionFilterForm.sourceTypeDropdown.selectOption(
			sourceType
		);
		await this.newSelectionFilterForm.picklistDropdown.click();
		await this.newSelectionFilterForm.picklistDropdown.selectOption(source);
		await this.newSelectionFilterForm.preselectedValuesMultiSelect.click();

		if (preselectedValues.length) {
			await this.page
				.getByRole('option', {name: preselectedValues[0]})
				.waitFor();

			await this.page
				.getByRole('option', {name: preselectedValues[0]})
				.click();

			await this.page
				.locator('label')
				.filter({hasText: filterMode})
				.click();
		}

		await this.page.getByText(selectionType).click();
	}

	getRowByText(text: string) {
		return this.filterTable
			.locator('tbody')
			.locator('tr')
			.filter({
				has: this.page.getByText(text, {exact: true}).first(),
			});
	}

	async goto({dataSetLabel}: {dataSetLabel: string}) {
		await this.dataSetPage.goto({
			dataSetLabel,
		});

		await this.dataSetPage.selectTab('Filters');
	}

	async open({dataSetLabel}: {dataSetLabel: string}) {
		await this.dataSetPage.open({
			dataSetLabel,
		});

		await this.dataSetPage.selectTab('Filters');
	}

	async openNewFilterForm({
		dropdownItemLabel,
		expectSaveHidden = false,
	}: {
		dropdownItemLabel: string;
		expectSaveHidden?: boolean;
	}) {
		await expect(this.newFilterButton).toBeVisible();

		await this.newFilterButton.click();

		const menuItem = this.page.getByRole('menuitem', {
			name: dropdownItemLabel,
		});

		await expect(menuItem).toBeVisible();

		await menuItem.click();

		if (expectSaveHidden) {
			await expect(this.newFilterForm.saveButton).toBeHidden();
		}
		else {
			await expect(this.newFilterForm.saveButton).toBeVisible();
		}
	}

	async saveAddFilterForm() {
		await this.newFilterForm.saveButton.click();
	}

	async selectTab(tabLabel: string) {
		await this.dataSetPage.selectTab(tabLabel);
	}
}
