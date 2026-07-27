/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Locator, Page} from '@playwright/test';

import {clickAndExpectToBeVisible} from '../../../../utils/clickAndExpectToBeVisible';
import {waitForAlert} from '../../../../utils/waitForAlert';
import {AssetCategoriesAdminPage} from './AssetCategoriesAdminPage';

export class AssetCategoriesEditPage {
	readonly addButton: Locator;
	readonly assetCategoriesAdminPage: AssetCategoriesAdminPage;
	readonly cancelButton: Locator;
	readonly deleteButton: Locator;
	readonly descriptionField: Locator;
	readonly externalReferenceCodeInput: Locator;
	readonly friendlyURLInput: Locator;
	readonly friendlyURLTab: Locator;
	readonly nameInput: Locator;
	readonly page: Page;
	readonly propertiesTab: Locator;
	readonly saveButton: Locator;

	constructor(page: Page) {
		this.assetCategoriesAdminPage = new AssetCategoriesAdminPage(page);
		this.cancelButton = page.getByRole('button', {name: 'Cancel'});
		this.deleteButton = page.getByRole('button', {name: 'Delete'});
		this.descriptionField = page
			.frameLocator('iframe[title="editor"]')
			.getByRole('textbox');
		this.externalReferenceCodeInput = page.getByPlaceholder(
			'External Reference Code'
		);
		this.friendlyURLInput = page.getByRole('textbox', {
			name: 'Friendly URL',
		});
		this.friendlyURLTab = page.getByRole('link', {name: 'Friendly URL'});
		this.nameInput = page.getByPlaceholder('Name');
		this.page = page;
		this.propertiesTab = page.getByRole('link', {name: 'properties'});
		this.saveButton = page.getByRole('button', {exact: true, name: 'Save'});
	}

	async addProperties(
		properties: {[key: string]: string},
		{save = true} = {}
	) {
		await this.propertiesTab.click();

		const keyInputs = this.page.getByLabel('key');

		for (const [key, value] of Object.entries(properties)) {
			if (await keyInputs.last().inputValue()) {
				const count = await keyInputs.count();

				await this.page
					.getByRole('button', {name: 'Add'})
					.last()
					.click();
				await keyInputs.nth(count).waitFor();
			}

			const keyInput = keyInputs.last();
			const valueInput = this.page.getByLabel('value').last();

			await keyInput.fill(key);
			await valueInput.fill(value);
		}

		if (save) {
			await this.save();
		}
	}

	async fillExternalReferenceCode(externalReferenceCode: string) {
		await this.descriptionField.waitFor();
		await this.externalReferenceCodeInput.fill(externalReferenceCode);
	}

	async fillFriendlyURL(friendlyURL: string) {
		await this.friendlyURLInput.fill(friendlyURL);
	}

	async fillName(name: string) {
		await this.descriptionField.waitFor();
		await this.nameInput.fill(name);
	}

	async goto(title: string) {
		await this.assetCategoriesAdminPage.gotoAction('Edit', title);
	}

	async goToFriendlyURLTab(title: string) {
		await this.goto(title);
		await this.friendlyURLTab.click();
	}

	async goToPropertiesTab(title: string) {
		await this.goto(title);
		await this.propertiesTab.click();
	}

	async moveCategory({
		categoryName,
		expandNames = [],
		targetName,
	}: {
		categoryName: string;
		expandNames?: string[];
		targetName: string;
	}) {
		const moveIframe = this.page.frameLocator(
			`iframe[title="Move ${categoryName}"]`
		);

		for (const expandName of expandNames) {
			await moveIframe.getByLabel(expandName).getByRole('button').click();
		}

		await moveIframe.getByText(targetName).click();
		await this.page
			.getByLabel(`Move ${categoryName}`)
			.getByRole('button', {name: 'Add'})
			.click();

		await waitForAlert(this.page);
	}

	async selectLanguage(languageId: string) {
		await clickAndExpectToBeVisible({
			autoClick: true,
			target: this.page.locator('.palette-item', {hasText: languageId}),
			trigger: this.page.locator('.input-localized-trigger'),
		});
	}

	async save(successMessage?: string) {
		await this.saveButton.click();
		await waitForAlert(this.page, successMessage);
	}
}
