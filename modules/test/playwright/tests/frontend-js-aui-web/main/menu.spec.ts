/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {loginTest} from '../../../fixtures/loginTest';

export const test = mergeTests(loginTest());

test('Check dropdown menu has show', {tag: '@LPD-64072'}, async ({page}) => {
	await page.getByLabel('Open Applications MenuCtrl+').click();

	await page.getByRole('tab', {name: 'Control Panel'}).click();

	await page.getByRole('menuitem', {name: 'Countries Management'}).click();

	const countryEllipses = page.locator(
		'[id="_com_liferay_address_web_internal_portlet_CountriesManagementAdminPortlet_countrySearchContainer_1_menu"]'
	);

	await countryEllipses.waitFor({state: 'visible'});

	await countryEllipses.click();

	await expect(
		page.locator('div.open.show.lfr-icon-menu-open')
	).toBeVisible();

	await expect(page.locator('.dropdown.lfr-icon-menu.open')).toBeVisible();
});
