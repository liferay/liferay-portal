/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Locator, Page} from '@playwright/test';

import {DataApiHelpers} from '../../helpers/ApiHelpers';
import {TRole} from '../../helpers/HeadlessAdminUserApiHelper';
import getRandomString from '../../utils/getRandomString';
import {waitForAlert} from '../../utils/waitForAlert';

export class RolePage {
	readonly assigneesLink: Locator;
	readonly backButton: Locator;
	readonly definePermissionsLink: Locator;
	readonly descriptionInput: Locator;
	readonly keyInput: Locator;
	readonly page: Page;
	readonly saveButton: Locator;
	readonly titleInput: Locator;
	readonly uniqueNameError: Locator;

	constructor(page: Page) {
		this.assigneesLink = page.getByRole('link', {
			name: 'Assignees',
		});
		this.backButton = page.getByRole('link', {name: 'Go to Roles'});
		this.definePermissionsLink = page.getByRole('link', {
			name: 'Define Permissions',
		});
		this.descriptionInput = page.getByLabel('Description');
		this.keyInput = page.locator(
			'#_com_liferay_roles_admin_web_portlet_RolesAdminPortlet_name'
		);
		this.page = page;
		this.saveButton = page.getByRole('button', {name: 'Save'});
		this.titleInput = page.getByLabel('Title');
		this.uniqueNameError = page.getByText('Please enter a unique name');
	}

	async addRole(
		apiHelpers: DataApiHelpers,
		{
			description = '',
			name = getRandomString(),
			title = '',
		}: {description?: string; name?: string; title?: string}
	) {
		await this.keyInput.fill(name);
		await this.descriptionInput.fill(description);
		await this.titleInput.fill(title || name);

		await this.saveButton.click();

		await waitForAlert(this.page, `${name} was created successfully`);

		const roles = await apiHelpers.headlessAdminUser.getRoles(
			name,
			'rolePermissions'
		);

		if (roles && roles.items) {
			(roles.items as Array<TRole>).map((role) => {
				apiHelpers.data.push({
					id: role.id,
					type: 'role',
				});
			});
		}
	}
}
