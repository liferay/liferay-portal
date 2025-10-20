/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Locator, Page} from '@playwright/test';

import {ApplicationsMenuPage} from '../../../../pages/product-navigation-applications-menu/ApplicationsMenuPage';

export class HeadlessBuilderPage {
	readonly addNewApplicationButton: Locator;
	readonly applicationsMenuPage: ApplicationsMenuPage;
	readonly createApplicationButton: Locator;
	readonly newApplicationTitleBox: Locator;
	readonly page: Page;

	constructor(page: Page) {
		this.addNewApplicationButton = page.getByLabel(
			'Add New API Application'
		);
		this.applicationsMenuPage = new ApplicationsMenuPage(page);
		this.createApplicationButton = page.getByRole('button', {
			name: 'Create',
		});
		this.newApplicationTitleBox = page.getByPlaceholder('Enter title.');
		this.page = page;
	}

	async openApplicationActions(title: string) {
		await this.page
			.locator('.fds tbody tr', {hasText: title})
			.getByRole('button')
			.click();
	}

	async openApplicationAndEdit(title: string) {
		await this.goto();
		await this.openApplicationActions(title);
		await this.page.getByRole('menuitem', {name: 'Edit'}).click();
	}

	async deleteApplication(title: string) {
		await this.openApplicationActions(title);
		await this.page.getByRole('menuitem', {name: 'Delete'}).click();
		await this.page
			.getByLabel('Delete API Application')
			.getByRole('textbox')
			.fill('My-app');
		await this.page.getByRole('button', {name: 'Delete'}).click();
	}

	async goto() {
		await this.applicationsMenuPage.goToAPIBuilder();
	}

	async goToEditApplication(name: string) {
		await this.page.getByRole('link', {name}).click();
	}
}
