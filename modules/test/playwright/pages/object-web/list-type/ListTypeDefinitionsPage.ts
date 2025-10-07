/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {FrameLocator, Locator, Page} from '@playwright/test';

import {ApplicationsMenuPage} from '../../product-navigation-applications-menu/ApplicationsMenuPage';

export class ListTypeDefinitionsPage {
	readonly addPicklistButton: Locator;
	readonly addPicklistItemButton: Locator;
	readonly applicationsMenuPage: ApplicationsMenuPage;
	readonly basicInfoHeading: Locator;
	readonly deleteActionMenuOption: Locator;
	readonly deleteButton: Locator;
	readonly frameLocator: FrameLocator;
	readonly modalNameInput: Locator;
	readonly modalSaveButton: Locator;
	readonly page: Page;
	readonly picklistItemKey: Locator;
	readonly picklistItemActionsButton: Locator;
	readonly picklistItemTranslationButton: Locator;
	readonly picklistTranslationButton: Locator;
	readonly sidebarNameInput: Locator;
	readonly sidebarSaveButton: Locator;

	constructor(page: Page) {
		this.addPicklistButton = page
			.getByRole('button', {
				name: 'Add Picklist',
			})
			.first();
		this.addPicklistItemButton = page
			.frameLocator('iframe')
			.getByLabel('Add Item');
		this.applicationsMenuPage = new ApplicationsMenuPage(page);
		this.basicInfoHeading = page
			.frameLocator('iframe')
			.getByRole('heading', {name: 'Basic Info'});
		this.deleteActionMenuOption = page
			.frameLocator('iframe')
			.getByRole('menuitem', {name: 'Delete'});
		this.deleteButton = page.getByRole('button', {name: 'Delete'});
		this.frameLocator = page.frameLocator('iframe');
		this.modalNameInput = page.getByLabel('Name');
		this.modalSaveButton = page.getByRole('button', {
			name: 'Save',
		});
		this.page = page;
		this.picklistItemActionsButton = page
			.frameLocator('iframe')
			.getByRole('button', {name: 'Actions'});
		this.picklistItemKey = page.getByLabel('Key');
		this.picklistItemTranslationButton = page.getByTitle('en_US');
		this.picklistTranslationButton = page
			.frameLocator('iframe')
			.getByTitle('en_US');
		this.sidebarNameInput = page.frameLocator('iframe').getByLabel('Name');
		this.sidebarSaveButton = page
			.frameLocator('iframe')
			.getByRole('button', {name: 'Save'});
	}

	async addPicklistItem(
		picklistName: string,
		picklistNameEntry: string,
		picklistKeyEntry?: string
	) {
		await this.page.getByRole('link', {name: picklistName}).click();
		await this.addPicklistItemButton.click();
		await this.modalNameInput.fill(picklistNameEntry);

		if (picklistKeyEntry) {
			await this.picklistItemKey.fill(picklistKeyEntry);
		}

		await this.modalSaveButton.click();
	}

	async createPicklist(picklistName: string) {
		await this.addPicklistButton.click();
		await this.modalNameInput.click();
		await this.modalNameInput.fill(picklistName);
		await this.modalSaveButton.click();
	}

	async deletePicklistItem() {
		await this.picklistItemActionsButton.click();
		await this.deleteActionMenuOption.click();
		await this.deleteButton.click();
	}

	async goto() {
		await this.applicationsMenuPage.goToPicklists();
	}

	async translatePicklist(
		listTypeDefinitionName: string,
		locationCode: string
	) {
		await this.getPicklistLinkLocator(listTypeDefinitionName).click();

		await this.picklistTranslationButton.click();

		await this.page
			.frameLocator('iframe')
			.getByRole('option', {
				name: `${locationCode} language: Untranslated`,
			})
			.click();

		await this.sidebarNameInput.click();

		await this.sidebarNameInput.fill(
			listTypeDefinitionName + ' translated'
		);

		await this.sidebarSaveButton.click();
	}

	async translatePicklistItem(
		listTypeDefinitionName: string,
		listTypeItemName: string,
		locationCode: string
	) {
		await this.getPicklistLinkLocator(listTypeDefinitionName).click();

		await this.getPicklistItemLinkLocator(listTypeItemName).click();

		await this.modalSaveButton.waitFor({state: 'visible'});

		await this.picklistItemTranslationButton.click();

		await this.page
			.getByRole('option', {
				name: `${locationCode} language: Untranslated`,
			})
			.click();

		await this.modalNameInput.click();

		await this.modalNameInput.fill(listTypeItemName + ' translated');

		await this.modalSaveButton.click();
	}

	getPicklistItemLinkLocator(listTypeItemName: string) {
		return this.page
			.frameLocator('iframe')
			.getByRole('link', {name: listTypeItemName});
	}

	getPicklistLinkLocator(listTypeDefinitionName: string) {
		return this.page.getByRole('link', {name: listTypeDefinitionName});
	}
}
