/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Locator, Page} from '@playwright/test';

import {PORTLET_URLS} from '../../utils/portletUrls';
import {waitForAlert} from '../../utils/waitForAlert';

export class WidgetPageTemplatesPage {
	readonly page: Page;

	readonly newButton: Locator;

	constructor(page: Page) {
		this.page = page;

		this.newButton = page.getByText('New', {exact: true});
	}

	async goto() {
		await this.page.goto(PORTLET_URLS.widgetPageTemplates);
	}

	async addGlobalWidgetPageTemplate(name: string) {
		await this.newButton.click();
		await this.page.getByPlaceholder('Name').fill(name);
		await this.page.getByRole('button', {name: 'Save'}).click();

		await waitForAlert(this.page);
	}

	async deactivateGlobalWidgetPageTemplate(name: string) {
		await this.clickMoreActions(name, 'Configure');

		await this.page.getByLabel('Active').click();

		await this.page.getByRole('button', {name: 'Save'}).click();

		await waitForAlert(this.page);
	}

	async clickMoreActions(name: string, actionName: string) {
		await this.page
			.locator('.card-page-item')
			.filter({hasText: name})
			.getByLabel('More actions')
			.click();

		await this.page
			.getByRole('menuitem', {
				exact: true,
				name: actionName,
			})
			.click();
	}

	async delete(name: string) {
		await this.clickMoreActions(name, 'Delete');

		await this.page.getByRole('button', {name: 'Delete'}).click();

		await waitForAlert(this.page);
	}

	async renameGlobalWidgetPageTemplate(newName: string, oldName: string) {
		await this.clickMoreActions(oldName, 'Configure');

		await this.page.getByPlaceholder('Name').fill(newName);
		await this.page.getByRole('button', {name: 'Save'}).click();

		await waitForAlert(this.page);
	}
}
