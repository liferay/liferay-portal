/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {apiHelpersTest} from '../../../fixtures/apiHelpersTest';
import {isolatedSiteTest} from '../../../fixtures/isolatedSiteTest';
import {loginTest} from '../../../fixtures/loginTest';
import getRandomString from '../../../utils/getRandomString';
import {waitForAlert} from '../../../utils/waitForAlert';
import {navigationMenusPagesTest} from '../../site-navigation-admin-web/main/fixtures/navigationMenusPagesTest';

const test = mergeTests(
	apiHelpersTest,
	isolatedSiteTest,
	loginTest(),
	navigationMenusPagesTest
);

test(
	'Change the selected page of a layout Navigation Menu Item',
	{tag: '@LPS-105188'},
	async ({apiHelpers, navigationMenusPage, page, site}) => {

		// Create two pages and a navigation menu whose item links to the first

		const page1 = await apiHelpers.jsonWebServicesLayout.addLayout({
			groupId: site.id,
			title: getRandomString(),
		});

		const page2 = await apiHelpers.jsonWebServicesLayout.addLayout({
			groupId: site.id,
			title: getRandomString(),
		});

		const navigationMenuName = getRandomString();

		await apiHelpers.headlessAdminSite.postSiteNavigationMenu(
			site.externalReferenceCode,
			{
				name: navigationMenuName,
				navigationMenuItems: [
					{
						navigationMenuItemSettings: {
							externalReferenceCode: page1.externalReferenceCode,
							privatePage: false,
							title: page1.nameCurrentValue,
						},
						type: 'layout',
					},
				],
			}
		);

		// Open the navigation menu and re-point the item to the second page

		await navigationMenusPage.goto(site.friendlyUrlPath);

		await page.getByRole('link', {name: navigationMenuName}).click();

		const selectPageModal = page.frameLocator(
			'iframe[title="Select Page"]'
		);

		await page.getByText(page1.nameCurrentValue, {exact: true}).click();

		await page.getByLabel('Change Item').click();

		await selectPageModal
			.getByText(page2.nameCurrentValue, {exact: true})
			.click();

		await page.getByRole('button', {exact: true, name: 'Select'}).click();

		await page.getByRole('button', {name: 'Save'}).click();

		await waitForAlert(
			page,
			'Success:Your request completed successfully.'
		);

		await expect(
			await navigationMenusPage.getMenuItemCard(page2.nameCurrentValue)
		).toBeVisible();

		await expect(
			await navigationMenusPage.getMenuItemCard(page1.nameCurrentValue)
		).not.toBeVisible();
	}
);
