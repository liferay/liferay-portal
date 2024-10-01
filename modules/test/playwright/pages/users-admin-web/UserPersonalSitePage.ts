/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Locator, Page} from '@playwright/test';

import {waitForAlert} from '../../utils/waitForAlert';

export class UserPersonalSitePage {
	readonly addWidgetButton: Locator;
	readonly languageSelectorButton: Locator;
	readonly languageSelectorMenuItem: Locator;
	readonly languageSelectorOptionsMenu: Locator;
	readonly myDashboardMenuItem: Locator;
	readonly page: Page;
	readonly removeMenuItem: Locator;
	readonly searchBar: Locator;
	readonly userPersonalMenuButton: Locator;

	constructor(page: Page) {
		this.addWidgetButton = page.getByTestId('add');
		this.languageSelectorButton = page.getByTitle('Select a Language');
		this.languageSelectorMenuItem = page
			.getByTestId('addPanelTabItem')
			.filter({hasText: 'Language Selector'})
			.getByRole('button', {exact: true, name: 'Add Content'});
		this.myDashboardMenuItem = page.getByRole('menuitem', {
			name: 'My Dashboard',
		});
		this.page = page;
		this.searchBar = page
			.getByRole('tabpanel')
			.getByPlaceholder('Search...');
		this.userPersonalMenuButton = page.getByTestId('userPersonalMenu');
	}

	async addLanguageSelectorToPage() {
		if (!(await this.languageSelectorButton.isVisible())) {
			await this.addWidgetButton.click();
			await this.searchBar.click();
			await this.searchBar.fill('language');
			await this.page.keyboard.press('Enter');
			await this.languageSelectorMenuItem.waitFor({state: 'visible'});
			await this.languageSelectorMenuItem.click();
			await waitForAlert(
				this.page,
				'Success:The application was added to the page.'
			);
		}
	}

	async goToMyDashboard() {
		await this.userPersonalMenuButton.click();
		await this.myDashboardMenuItem.click();
	}

	async switchLanguages(
		targetLanguage: string,
		localizedButtonName: string = 'Select a Language'
	) {
		await this.page.getByTitle(localizedButtonName).click();
		await this.page.getByRole('menuitem', {name: targetLanguage}).click();
	}
}
