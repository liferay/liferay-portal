/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Locator, Page} from '@playwright/test';

import getRandomString from '../../utils/getRandomString';
import {waitForAlert} from '../../utils/waitForAlert';

export class EditAccountRolePage {
	readonly backButton: Locator;
	readonly defineGroupScopePermissionsLink: Locator;
	readonly definePermissionsLink: Locator;
	readonly keyInput: Locator;
	readonly page: Page;
	readonly saveButton: Locator;
	readonly titleInput: Locator;

	constructor(page: Page) {
		this.backButton = page.getByRole('link', {exact: true, name: 'Back'});
		this.defineGroupScopePermissionsLink = page.getByRole('link', {
			name: 'Define Group Scope Permissions',
		});
		this.definePermissionsLink = page.getByRole('link', {
			name: 'Define Permissions',
		});
		this.keyInput = page.locator(
			'#_com_liferay_account_admin_web_internal_portlet_AccountEntriesAdminPortlet_name'
		);
		this.page = page;
		this.saveButton = page.getByRole('button', {name: 'Save'});
		this.titleInput = page.getByLabel('Title').first();
	}

	async addRole({name = getRandomString()}: {name?: string}) {
		await this.keyInput.fill(name);
		await this.titleInput.fill(name);

		await this.saveButton.click();

		await waitForAlert(this.page);
	}
}
