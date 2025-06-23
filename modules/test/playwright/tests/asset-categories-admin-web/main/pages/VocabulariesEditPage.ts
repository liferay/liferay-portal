/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Locator, Page} from '@playwright/test';

import {clickAndExpectToBeVisible} from '../../../../utils/clickAndExpectToBeVisible';
import {waitForAlert} from '../../../../utils/waitForAlert';

export class VocabulariesEditPage {
	readonly addRowButton: Locator;
	readonly assetTypeSelect: Locator;
	readonly deleteButton: Locator;
	readonly descriptionInput: Locator;
	readonly nameInput: Locator;
	readonly page: Page;
	readonly removeRowButton: Locator;
	readonly saveButton: Locator;

	constructor(page: Page) {
		this.addRowButton = page.getByRole('button', {
			name: 'Add',
		});
		this.assetTypeSelect = page.locator('.vocabulary-asset-type-select');
		this.deleteButton = page.getByRole('button', {name: 'Delete'});
		this.descriptionInput = page.getByPlaceholder('Description');
		this.nameInput = page.getByPlaceholder('Name');
		this.page = page;
		this.removeRowButton = page.getByRole('button', {
			name: 'Remove',
		});
		this.saveButton = page.getByRole('button', {
			name: 'Save',
		});
	}

	async add({
		assetTypes,
		description,
		name,
	}: {
		name: string;
		description?: string;
		assetTypes?: string[];
	}) {
		await this.fillName(name);

		if (description) {
			await this.descriptionInput.fill(description);
		}

		if (assetTypes) {
			for (const [index, assetType] of assetTypes.entries()) {
				await this.addAssociatedAssetType(assetType, index);

				if (assetTypes.length !== index + 1) {
					await this.addRowButton.nth(index).click();
				}
			}
		}

		await this.page.on('dialog', (dialog) => dialog.accept());
		await this.saveButton.click();
		await waitForAlert(this.page);
	}

	async addAssociatedAssetType(assetType: string, index: number) {
		await this.assetTypeSelect.nth(index).selectOption(assetType);
	}

	async removeLastAssociatedAssetType() {
		await this.removeRowButton.last().click();
	}

	async delete(name: string) {
		await clickAndExpectToBeVisible({
			autoClick: true,
			target: this.page.getByRole('menuitem', {name: 'Delete'}),
			trigger: this.page
				.getByRole('heading', {name})
				.getByLabel('Show Actions'),
		});

		await this.deleteButton.click();
	}

	async fillName(name: string) {
		await this.nameInput.fill(name);
	}

	async goto(name: string) {
		await clickAndExpectToBeVisible({
			autoClick: true,
			target: this.page.getByRole('menuitem', {name: 'Edit'}),
			trigger: this.page
				.getByRole('heading', {name})
				.getByLabel('Show Actions'),
		});
	}
}
