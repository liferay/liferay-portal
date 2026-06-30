/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Locator, Page, expect} from '@playwright/test';

import {getRandomInt} from '../../../../utils/getRandomInt';
import {PORTLET_URLS} from '../../../../utils/portletUrls';

export class EditVocabularyPage {
	readonly page: Page;

	readonly assetTypeCheckbox: Locator;
	readonly assetTypeSelector: Locator;
	readonly assetTypeToggle: Locator;
	readonly assetTypesButton: Locator;
	readonly descriptionInput: Locator;
	readonly externalReferenceCodeInput: Locator;
	readonly generalButton: Locator;
	readonly multiSelectToggle: Locator;
	readonly nameInput: Locator;
	readonly newButton: Locator;
	readonly projectCheckbox: Locator;
	readonly projectSelector: Locator;
	readonly saveButton: Locator;
	readonly spaceCheckbox: Locator;
	readonly spaceSelector: Locator;
	readonly visibilitySelector: Locator;

	constructor(page: Page) {
		this.page = page;

		this.assetTypeCheckbox = this.page.getByRole('checkbox', {
			name: 'Make this vocabulary available in all asset types',
		});
		this.assetTypeSelector = this.page.getByLabel('Asset Type Selector');
		this.assetTypeToggle = this.page.getByLabel('toggle-asset-type');
		this.assetTypesButton = this.page.getByRole('menuitem', {
			name: 'Associated Asset Types',
		});
		this.descriptionInput = this.page.getByLabel('Description');
		this.externalReferenceCodeInput = this.page.getByLabel(
			'External Reference Code'
		);
		this.generalButton = this.page.getByRole('button', {name: 'General'});
		this.multiSelectToggle = this.page.getByLabel('Multi Value');
		this.nameInput = this.page.getByLabel('Name');
		this.newButton = this.page.getByRole('button', {
			name: 'New Vocabulary',
		});
		this.projectCheckbox = this.page.getByRole('checkbox', {
			name: 'Make this vocabulary available in all projects',
		});
		this.projectSelector = this.page.getByLabel('Project Selector');
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
		externalReferenceCode,
		name,
	}: {
		description?: string;
		externalReferenceCode?: string;
		name?: string;
	}) {
		await this.page.getByText('Basic Info').waitFor();

		if (description !== undefined) {
			await this.descriptionInput.fill(description);
			await this.descriptionInput.blur();
		}

		if (externalReferenceCode !== undefined) {
			await this.externalReferenceCodeInput.fill(externalReferenceCode);
			await this.externalReferenceCodeInput.blur();
		}

		if (name !== undefined) {
			await this.nameInput.fill(name);
			await this.nameInput.blur();
		}
	}

	async changeVisibility(visibility: string) {
		await this.visibilitySelector.selectOption({label: visibility});
	}

	async createVocabulary() {
		await this.goto();

		const name = `Vocabulary${getRandomInt()}`;

		await this.changeGeneralInfo({name});

		await this.saveButton.click();

		return name;
	}

	async fillName(name: string) {
		await this.nameInput.waitFor();
		await this.nameInput.fill(name);
	}

	async selectAssetTypes(assetType: string) {
		if (await this.assetTypeCheckbox.isChecked()) {
			await this.assetTypeCheckbox.click();

			await expect(this.assetTypeCheckbox).not.toBeChecked();
		}

		await this.assetTypeSelector.click();

		await this.page.getByText(assetType).click();
	}

	async selectProjects(projectName: string) {
		if (await this.projectCheckbox.isChecked()) {
			await this.projectCheckbox.click();

			await expect(this.projectCheckbox).not.toBeChecked();
		}

		await this.projectSelector.click();

		const option = this.page
			.getByRole('option')
			.filter({hasText: projectName});

		await option.scrollIntoViewIfNeeded();
		await option.click();
	}

	async selectSpaces(spaceName: string) {
		if (await this.spaceCheckbox.isChecked()) {
			await this.spaceCheckbox.click();

			await expect(this.spaceCheckbox).not.toBeChecked();
		}

		await this.spaceSelector.click();

		const option = this.page
			.getByRole('option')
			.filter({hasText: spaceName});

		await option.scrollIntoViewIfNeeded();
		await option.click();
	}
}
