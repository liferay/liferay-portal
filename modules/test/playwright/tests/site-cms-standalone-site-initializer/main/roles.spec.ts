/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {loginTest} from '../../../fixtures/loginTest';

const test = mergeTests(loginTest());

const ROLES_ADMIN_URL =
	'/group/control_panel/manage?p_p_id=com_liferay_roles_admin_web_portlet_RolesAdminPortlet';

test(
	'hides account and site role types from the roles administration',
	{tag: '@LPD-94207'},
	async ({page}) => {
		await page.goto(ROLES_ADMIN_URL);

		const roleTypeNavigation = page.locator('.navigation-bar');

		// The remaining role types are still offered

		await expect(
			roleTypeNavigation.getByRole('link', {name: 'Regular Roles'})
		).toBeVisible();
		await expect(
			roleTypeNavigation.getByRole('link', {name: 'Organization Roles'})
		).toBeVisible();

		// Account and site role types are hidden in the standalone CMS

		await expect(
			roleTypeNavigation.getByRole('link', {name: 'Account Roles'})
		).toHaveCount(0);
		await expect(
			roleTypeNavigation.getByRole('link', {name: 'Site Roles'})
		).toHaveCount(0);
	}
);
