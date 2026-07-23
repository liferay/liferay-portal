/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {loginTest} from '../../../fixtures/loginTest';

const test = mergeTests(loginTest());

// Landing on a control panel application renders the full product menu.

const CONTROL_PANEL_URL =
	'/group/control_panel/manage?p_p_id=com_liferay_roles_admin_web_portlet_RolesAdminPortlet';

test(
	'hides administration applications that are irrelevant to the standalone CMS',
	{tag: '@LPD-94604'},
	async ({page}) => {
		await page.goto(CONTROL_PANEL_URL);

		const productMenu = page.locator('nav.menubar');

		await expect(productMenu).toBeAttached();

		// Applications kept in the standalone CMS are still reachable

		await expect(
			productMenu.getByRole('menuitem', {exact: true, name: 'Roles'})
		).toHaveCount(1);
		await expect(
			productMenu.getByRole('menuitem', {exact: true, name: 'Objects'})
		).toHaveCount(1);

		// Applications filtered out by CMSPanelAppShowFilter are gone

		await expect(
			productMenu.getByRole('menuitem', {name: 'Service Accounts'})
		).toHaveCount(0);
		await expect(
			productMenu.getByRole('menuitem', {exact: true, name: 'Sites'})
		).toHaveCount(0);
	}
);
