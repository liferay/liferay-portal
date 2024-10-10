/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Locator, Page} from '@playwright/test';

import {waitForAlert} from '../../utils/waitForAlert';
import {ApplicationsMenuPage} from '../product-navigation-applications-menu/ApplicationsMenuPage';

export class InstanceSettingsPage {
	readonly page: Page;
	readonly applicationsMenuPage: ApplicationsMenuPage;
	readonly actionsButton: Locator;
	readonly saveButton: Locator;

	constructor(page: Page) {
		this.actionsButton = page.getByRole('button', {name: 'Actions'});
		this.page = page;
		this.applicationsMenuPage = new ApplicationsMenuPage(page);
		this.saveButton = page
			.getByRole('button', {name: 'Save'})
			.or(page.getByRole('button', {name: 'Update'}));
	}

	async goto() {
		await this.applicationsMenuPage.goToInstanceSettings();
	}

	async exportInstanceSetting() {
		await this.actionsButton.click();

		await this.page.getByRole('menuitem', {name: 'Export'}).click();
	}

	async goToInstanceSetting(categoryKey: string, configurationName: string, nthChild?: number) {
		await this.goto();

		await this.page
			.getByRole('link', {
				exact: true,
				name: categoryKey,
			})
			.click();

		const configurationMenuItem = this.page
			.getByRole('menuitem', {
				exact: true,
				name: configurationName,
			});

        if (nthChild === undefined) {
            await configurationMenuItem.first().click()
        } else {    
            await configurationMenuItem.nth(nthChild).click()
        }

	}

	async goToSSO() {
		await this.goto();
		await this.page.getByRole('link', {name: 'SSO'}).click();
	}

	async saveAndWaitForAlert(
		{
			autoClose,
            text = 'Success:Your request completed successfully.',
			type,
		}: {
			autoClose?: boolean;
            text?: string,
			type?: 'success' | 'info' | 'warning' | 'danger';
		}
	) {
		await this.saveButton.click();

		await waitForAlert(this.page, text, {autoClose, type});
	}
}
