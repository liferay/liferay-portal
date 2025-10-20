/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {apiHelpersTest} from '../../../fixtures/apiHelpersTest';
import {applicationsMenuPageTest} from '../../../fixtures/applicationsMenuPageTest';
import {isolatedSiteTest} from '../../../fixtures/isolatedSiteTest';
import {loginTest} from '../../../fixtures/loginTest';
import {pageViewModePagesTest} from '../../../fixtures/pageViewModePagesTest';
import {productMenuPageTest} from '../../../fixtures/productMenuPageTest';
import {stagingPageTest} from '../../export-import-web/main/fixtures/stagingPageTest';
import {navigationMenusPagesTest} from '../../site-navigation-admin-web/main/fixtures/navigationMenusPagesTest';
import {stagingConfigurationPageTest} from '../../staging-configuration-web/main/fixtures/stagingConfigurationPageTest';

const test = mergeTests(
	apiHelpersTest,
	applicationsMenuPageTest,
	isolatedSiteTest,
	loginTest(),
	navigationMenusPagesTest,
	pageViewModePagesTest,
	productMenuPageTest,
	stagingConfigurationPageTest,
	stagingPageTest
);

let site;
const siteName = 'Test Site Name';

test.beforeEach(async ({apiHelpers}) => {
	site = await apiHelpers.headlessSite.createSite({
		name: siteName,
	});

	await apiHelpers.jsonWebServicesStaging.enableLocalStaging({
		groupId: site.id,
	});
});

test.afterEach(async ({apiHelpers}) => {
	await test.step('Delete site on de DXP side', async () => {
		await apiHelpers.headlessSite.deleteSite(site.id);
	});
});

test(
	'Cannot edit the navigation menu in the Live site',
	{
		tag: '@LPD-67281',
	},
	async ({navigationMenusPage, page, productMenuPage, stagingPage}) => {
		const navigationMenuName = 'Navigation Menu Name';

		await navigationMenusPage.goto('/test-site-name-staging');

		await navigationMenusPage.createNavigationMenu(navigationMenuName);

		await productMenuPage.openProductMenuIfClosed();

		await page.getByRole('menuitem', {name: 'Navigation Menus'}).click();

		await expect(
			page.getByRole('link', {name: navigationMenuName})
		).toBeVisible();

		await page.getByLabel('Show Actions').click();

		await expect(page.getByRole('menuitem', {name: 'Edit'})).toBeVisible();

		await stagingPage.goto('test-site-name-staging');

		await stagingPage.publish();

		await navigationMenusPage.goto('/test-site-name');

		await expect(
			page.locator(
				`td.table-list-title:has-text('${navigationMenuName}')`
			)
		).toBeVisible();

		const linkLocator = page.locator(`a:has-text('${navigationMenuName}')`);

		await expect(linkLocator).not.toBeVisible();

		await page.getByLabel('Show Actions').click();

		await expect(
			page.getByRole('menuitem', {name: 'Edit'})
		).not.toBeVisible();
	}
);
