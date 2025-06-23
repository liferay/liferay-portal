/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Locator, Page} from '@playwright/test';

export class FormBuilderFieldSettingsSidePanelPage {
	readonly addOptionButton: Locator;
	readonly advancedTabButton: Locator;
	readonly allowMultipleSelectionsSettingToggle: Locator;
	readonly createListSettingSelect: Locator;
	readonly inlineToggle: Locator;
	readonly optionDisplayNameInputField: Locator;
	readonly optionReferenceInputField: Locator;
	readonly page: Page;
	readonly repeatableToggle: Locator;
	readonly requiredFieldToggle: Locator;
	readonly showAsSwitchToggle: Locator;
	readonly showLabelToggle: Locator;

	constructor(page: Page) {
		this.addOptionButton = page.getByRole('button', {name: 'Add Option'});
		this.advancedTabButton = page
			.locator('button')
			.filter({hasText: 'Advanced'});
		this.allowMultipleSelectionsSettingToggle = page.getByLabel(
			'Allow Multiple Selections'
		);
		this.createListSettingSelect = page.getByLabel('Create List');
		this.inlineToggle = page.getByText('Inline');
		this.optionDisplayNameInputField = page.getByPlaceholder('Option');
		this.optionReferenceInputField = page.getByLabel('Option Reference', {
			exact: true,
		});
		this.page = page;
		this.repeatableToggle = page.getByText('Repeatable');
		this.requiredFieldToggle = page.getByLabel('Required Field');
		this.showAsSwitchToggle = page.getByText('Show as a Switch');
		this.showLabelToggle = page.getByLabel('Show Label');
	}

	async addOptions(numberOfOptions: number, optionsSufix: string = 'Option') {
		for (let index = 0; index < numberOfOptions; index++) {
			await this.optionDisplayNameInputField
				.nth(index)
				.fill(optionsSufix + index);

			await this.optionDisplayNameInputField.nth(index).blur();

			await this.page.waitForTimeout(2000);

			if (index < numberOfOptions - 1) {
				await this.addOptionButton.click();
			}
		}
	}

	async allowMultipleSelections() {
		await this.allowMultipleSelectionsSettingToggle.check();

		await this.page.waitForTimeout(2000);
	}

	async fillMultiplePredefinedValues(values: string[]) {
		for (const value of values) {
			await this.page
				.getByRole('combobox', {name: 'Predefined Value'})
				.click();
			await this.page.getByRole('option', {name: value}).click();
		}
	}

	async selectCreateListSetting(
		option: 'From Autofill' | 'From Data Provider' | 'Manually'
	) {
		await this.createListSettingSelect.click();

		await this.page
			.getByRole('option', {name: option})
			.dispatchEvent('click');

		await this.page.waitForTimeout(2000);
	}
}
