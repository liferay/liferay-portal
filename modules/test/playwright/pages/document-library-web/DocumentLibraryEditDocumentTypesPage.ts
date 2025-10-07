/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Locator, Page} from '@playwright/test';

import fillAndClickOutside from '../../utils/fillAndClickOutside';
import {DocumentLibraryPage} from './DocumentLibraryPage';

export class DocumentLibraryEditDocumentTypesPage {
	readonly documentLibraryPage: DocumentLibraryPage;
	readonly page: Page;
	readonly saveButton: Locator;
	readonly titleSelector: Locator;

	constructor(page: Page) {
		this.documentLibraryPage = new DocumentLibraryPage(page);
		this.page = page;
		this.saveButton = page.getByRole('button', {exact: true, name: 'Save'});
		this.titleSelector = page.getByPlaceholder('Untitled');
	}

	async goto(siteUrl?: Site['friendlyUrlPath']) {
		await this.documentLibraryPage.goto(siteUrl);
		await this.documentLibraryPage.changeTab('Document Types');
		await this.documentLibraryPage.openNewDLTypeButton();
	}

	async addField(type: string, siteUrl?: Site['friendlyUrlPath']) {
		await this.goto(siteUrl);
		await this.page.getByTitle(type, {exact: true}).dblclick();
	}

	async createNewDLTypeWithNumericField(
		title: string,
		siteUrl?: Site['friendlyUrlPath']
	) {
		await this.goto(siteUrl);
		await this.addField('Numeric', siteUrl);
		await fillAndClickOutside(this.page, this.titleSelector, title);
		await this.saveButton.click();
	}

	async createNewDLTypeWithTextFieldRequiredNonLocalizable(
		title: string,
		siteUrl?: Site['friendlyUrlPath']
	) {
		await this.goto(siteUrl);
		await this.addField('Text', siteUrl);
		await this.page.getByRole('tab', {name: 'Basic'}).click();
		await this.page.getByLabel('Required Field').check();
		await this.page.getByRole('tab', {name: 'Advanced'}).click();
		await this.page.getByLabel('Localizable').uncheck();
		await fillAndClickOutside(this.page, this.titleSelector, title);
		await this.saveButton.click();
	}

	async createNewDLTypeWithUploadField(
		title: string,
		siteUrl?: Site['friendlyUrlPath']
	) {
		await this.goto(siteUrl);
		await this.addField('Upload', siteUrl);
		await fillAndClickOutside(this.page, this.titleSelector, title);
		await this.saveButton.click();
	}

	async createNewDLTypeWithTextField(
		title: string,
		isRequired: boolean,
		siteUrl?: Site['friendlyUrlPath']
	) {
		await this.goto(siteUrl);
		await this.addField('Text', siteUrl);
		await this.page.getByRole('tab', {name: 'Basic'}).click();
		await fillAndClickOutside(this.page, this.titleSelector, title);
		if (isRequired) {
			await this.page.getByLabel('Required Field').check();
		}
		await this.saveButton.click();
		await this.documentLibraryPage.waitForSuccessAlert();
	}

	async updateDLTypeTextField(
		title: string,
		isRequired: boolean,
		siteUrl?: Site['friendlyUrlPath']
	) {
		await this.goto(siteUrl);
		await this.documentLibraryPage.changeTab('Document Types');
		await this.page.locator('a', {hasText: title}).click();
		await this.page.locator('.ddm-drag').first().click();
		await this.page.getByRole('tab', {name: 'Basic'}).click();
		if (isRequired) {
			await this.page.getByLabel('Required Field').check();
		}
		await this.saveButton.click();
	}
}
