/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Locator, Page, expect} from '@playwright/test';

import {PORTLET_URLS} from '../../../../utils/portletUrls';

export class EditVocabularyPage {
	readonly page: Page;

	private readonly assetTypeSelector: Locator;
	private readonly descriptionInput: Locator;
	private readonly nameInput: Locator;

	readonly assetTypeCheckbox: Locator;
	readonly assetTypesButton: Locator;
	readonly generalButton: Locator;
	readonly multiSelectToggle: Locator;
	readonly newButton: Locator;
	readonly saveButton: Locator;
	readonly spaceCheckbox: Locator;
	readonly spaceSelector: Locator;
	readonly visibilitySelector: Locator;

	constructor(page: Page) {
		this.page = page;

		this.assetTypesButton = this.page.getByRole('menuitem', {
			name: 'Associated Asset Types',
		});
		this.assetTypeCheckbox = this.page.getByRole('checkbox', {
			name: 'Make this vocabulary available in all asset types',
		});
		this.assetTypeSelector = this.page.getByLabel('Asset Type Selector');
		this.descriptionInput = this.page.getByLabel('Description');
		this.generalButton = this.page.getByRole('button', {name: 'General'});
		this.multiSelectToggle = this.page.getByLabel('Multi Value');
		this.nameInput = this.page.getByLabel('Name');
		this.newButton = this.page.getByRole('button', {
			name: 'New Vocabulary',
		});
		this.saveButton = this.page.getByRole('button', {name: 'Save'});
		this.spaceCheckbox = this.page.getByRole('checkbox', {
			name: 'Make this vocabulary available in all spaces',
		});
		this.spaceSelector = this.page.getByLabel('Space Selector');
		this.visibilitySelector = this.page.getByLabel('Visibility');
	}

	async goto() {
		await this.page.goto(PORTLET_URLS.cmsNewVocabulary);

		await this.page
			.getByRole('heading', {name: 'New Vocabulary'})
			.isVisible();
	}

	async changeGeneralInfo({
		description,
		name,
	}: {
		description?: string;
		name?: string;
	}) {
		await this.page.getByText('Basic Info').waitFor();

		if (description !== undefined) {
			await this.descriptionInput.fill(description);
			await this.descriptionInput.blur();
		}

		if (name !== undefined) {
			await this.nameInput.fill(name);
			await this.nameInput.blur();
		}
	}

	async changeVisibility(visibility: string) {
		await this.visibilitySelector.selectOption({label: visibility});
	}

	async selectAssetTypes(assetType: string) {
		if (this.assetTypeCheckbox.isChecked()) {
			await this.assetTypeCheckbox.click();

			await expect(this.assetTypeCheckbox).not.toBeChecked();
		}

		await this.assetTypeSelector.click();

		await this.page.getByText(assetType).click();
	}

	async selectSpaces(spaceName: string) {
		if (this.spaceCheckbox.isChecked()) {
			await this.spaceCheckbox.click();

			await expect(this.spaceCheckbox).not.toBeChecked();
		}

		await this.spaceSelector.click();

		await this.page.getByText(spaceName).click();
	}
}
