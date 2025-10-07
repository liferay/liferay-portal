/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {apiHelpersTest} from '../../../fixtures/apiHelpersTest';
import {depotAdminPageTest} from '../../../fixtures/depotAdminPageTest';
import {featureFlagsTest} from '../../../fixtures/featureFlagsTest';
import {isolatedSiteTest} from '../../../fixtures/isolatedSiteTest';
import {loginTest} from '../../../fixtures/loginTest';
import {pageSelectorPagesTest} from '../../../fixtures/pageSelectorPagesTest';
import {pagesAdminPagesTest} from '../../../fixtures/pagesAdminPagesTest';
import getRandomString from '../../../utils/getRandomString';
import {pagesPagesTest} from '../../layout-admin-web/main/fixtures/pagesPagesTest';
import {navigationMenusPagesTest} from './fixtures/navigationMenusPagesTest';

const test = mergeTests(
	apiHelpersTest,
	depotAdminPageTest,
	featureFlagsTest({
		'LPS-178052': {enabled: true},
	}),
	isolatedSiteTest,
	loginTest(),
	navigationMenusPagesTest,
	pagesAdminPagesTest,
	pagesPagesTest,
	pageSelectorPagesTest
);

test(
	'Delete multiple Navigation Menus',
	{
		tag: '@LPD-58226',
	},
	async ({apiHelpers, navigationMenusPage, page, site}) => {
		const pageName = getRandomString();

		await apiHelpers.headlessDelivery.createSitePage({
			siteId: site.id,
			title: pageName,
		});

		await navigationMenusPage.goto(site.friendlyUrlPath);

		const navigationMenuName1 = getRandomString();

		const navigationMenuName2 = getRandomString();

		await navigationMenusPage.createNavigationMenu(navigationMenuName1);

		await navigationMenusPage.goto(site.friendlyUrlPath);

		await navigationMenusPage.createNavigationMenu(navigationMenuName2);

		await navigationMenusPage.goto(site.friendlyUrlPath);

		await page.getByLabel('Select All Items on the Page').check();

		await page.getByRole('button', {name: 'Delete'}).click();

		await page
			.getByLabel('Delete Navigation Menus')
			.getByRole('button', {name: 'Delete'})
			.click();

		await expect(
			page.getByRole('link', {name: navigationMenuName1})
		).not.toBeVisible();

		await expect(
			page.getByRole('link', {name: navigationMenuName2})
		).not.toBeVisible();
	}
);

test(
	'Delete Navigation Menu',
	{
		tag: '@LPD-58226',
	},
	async ({apiHelpers, navigationMenusPage, page, site}) => {
		const pageName = getRandomString();

		await apiHelpers.headlessDelivery.createSitePage({
			siteId: site.id,
			title: pageName,
		});

		await navigationMenusPage.goto(site.friendlyUrlPath);

		const navigationMenuName1 = getRandomString();

		await navigationMenusPage.createNavigationMenu(navigationMenuName1);

		await navigationMenusPage.goto(site.friendlyUrlPath);

		await page.waitForTimeout(300);

		await (
			await navigationMenusPage.getNavigationMenuActionMenu(
				navigationMenuName1
			)
		).click();

		await (await navigationMenusPage.getMenuItem('Delete')).click();

		await page.getByRole('button', {name: 'Delete'}).click();

		await expect(
			page.getByRole('link', {name: navigationMenuName1})
		).not.toBeVisible();
	}
);

test(
	'Mark Navigation Menu as Primary Navigation',
	{
		tag: '@LPD-58226',
	},
	async ({apiHelpers, navigationMenusPage, page, site}) => {
		const pageName = getRandomString();

		await apiHelpers.headlessDelivery.createSitePage({
			siteId: site.id,
			title: pageName,
		});

		await navigationMenusPage.goto(site.friendlyUrlPath);

		const navigationMenuName1 = getRandomString();

		const navigationMenuName2 = getRandomString();

		await navigationMenusPage.createNavigationMenu(navigationMenuName1);

		await navigationMenusPage.goto(site.friendlyUrlPath);

		await navigationMenusPage.createNavigationMenu(navigationMenuName2);

		await navigationMenusPage.goto(site.friendlyUrlPath);

		await page.waitForTimeout(300);

		await (
			await navigationMenusPage.getNavigationMenuActionMenu(
				navigationMenuName1
			)
		).click();

		await (
			await navigationMenusPage.getMenuItem('Primary Navigation')
		).click();

		const firstNavigationMenu =
			await navigationMenusPage.getNavigationMenuRow(1);

		const secondNavigationMenu =
			await navigationMenusPage.getNavigationMenuRow(2);

		await expect(
			await navigationMenusPage.getNavigationMenuCell(
				'Primary Navigation',
				firstNavigationMenu
			)
		).toBeVisible();

		await expect(
			await navigationMenusPage.getNavigationMenuCell(
				'Primary Navigation',
				secondNavigationMenu
			)
		).not.toBeVisible();

		await (
			await navigationMenusPage.getNavigationMenuActionMenu(
				navigationMenuName2
			)
		).click();

		page.once('dialog', async (dialog) => {
			await dialog.accept();
		});

		await (
			await navigationMenusPage.getMenuItem('Primary Navigation')
		).click();

		await expect(
			await navigationMenusPage.getNavigationMenuCell(
				'Primary Navigation',
				secondNavigationMenu
			)
		).toBeVisible();

		await expect(
			await navigationMenusPage.getNavigationMenuCell(
				'Primary Navigation',
				firstNavigationMenu
			)
		).not.toBeVisible();
	}
);

test(
	'Mark Navigation Menu as Secondary Navigation',
	{
		tag: '@LPD-58226',
	},
	async ({apiHelpers, navigationMenusPage, page, site}) => {
		const pageName = getRandomString();

		await apiHelpers.headlessDelivery.createSitePage({
			siteId: site.id,
			title: pageName,
		});

		await navigationMenusPage.goto(site.friendlyUrlPath);

		const navigationMenuName1 = getRandomString();

		const navigationMenuName2 = getRandomString();

		await navigationMenusPage.createNavigationMenu(navigationMenuName1);

		await navigationMenusPage.goto(site.friendlyUrlPath);

		await navigationMenusPage.createNavigationMenu(navigationMenuName2);

		await navigationMenusPage.goto(site.friendlyUrlPath);

		await page.waitForTimeout(300);

		await (
			await navigationMenusPage.getNavigationMenuActionMenu(
				navigationMenuName1
			)
		).click();

		await (
			await navigationMenusPage.getMenuItem('Secondary Navigation')
		).click();

		const firstNavigationMenu =
			await navigationMenusPage.getNavigationMenuRow(1);

		const secondNavigationMenu =
			await navigationMenusPage.getNavigationMenuRow(2);

		await expect(
			await navigationMenusPage.getNavigationMenuCell(
				'Secondary Navigation',
				firstNavigationMenu
			)
		).toBeVisible();

		await expect(
			await navigationMenusPage.getNavigationMenuCell(
				'Secondary Navigation',
				secondNavigationMenu
			)
		).not.toBeVisible();

		await (
			await navigationMenusPage.getNavigationMenuActionMenu(
				navigationMenuName2
			)
		).click();

		await (
			await navigationMenusPage.getMenuItem('Secondary Navigation')
		).click();

		await expect(
			await navigationMenusPage.getNavigationMenuCell(
				'Secondary Navigation',
				secondNavigationMenu
			)
		).toBeVisible();

		await expect(
			await navigationMenusPage.getNavigationMenuCell(
				'Secondary Navigation',
				firstNavigationMenu
			)
		).not.toBeVisible();
	}
);

test(
	'Mark Navigation Menu as Social Navigation',
	{
		tag: '@LPD-58226',
	},
	async ({apiHelpers, navigationMenusPage, page, site}) => {
		await apiHelpers.headlessDelivery.createSitePage({
			siteId: site.id,
			title: getRandomString(),
		});

		await navigationMenusPage.goto(site.friendlyUrlPath);

		const navigationMenuName1 = getRandomString();

		const navigationMenuName2 = getRandomString();

		await navigationMenusPage.createNavigationMenu(navigationMenuName1);

		await navigationMenusPage.goto(site.friendlyUrlPath);

		await navigationMenusPage.createNavigationMenu(navigationMenuName2);

		await navigationMenusPage.goto(site.friendlyUrlPath);

		await page.waitForTimeout(300);

		await (
			await navigationMenusPage.getNavigationMenuActionMenu(
				navigationMenuName1
			)
		).click();

		await (
			await navigationMenusPage.getMenuItem('Social Navigation')
		).click();

		const firstNavigationMenu =
			await navigationMenusPage.getNavigationMenuRow(1);

		const secondNavigationMenu =
			await navigationMenusPage.getNavigationMenuRow(2);

		await expect(
			await navigationMenusPage.getNavigationMenuCell(
				'Social Navigation',
				firstNavigationMenu
			)
		).toBeVisible();

		await expect(
			await navigationMenusPage.getNavigationMenuCell(
				'Social Navigation',
				secondNavigationMenu
			)
		).not.toBeVisible();

		await (
			await navigationMenusPage.getNavigationMenuActionMenu(
				navigationMenuName2
			)
		).click();

		await (
			await navigationMenusPage.getMenuItem('Social Navigation')
		).click();

		await expect(
			await navigationMenusPage.getNavigationMenuCell(
				'Social Navigation',
				secondNavigationMenu
			)
		).toBeVisible();

		await expect(
			await navigationMenusPage.getNavigationMenuCell(
				'Social Navigation',
				firstNavigationMenu
			)
		).not.toBeVisible();
	}
);

test(
	'Rename Navigation Menu',
	{
		tag: '@LPD-58226',
	},
	async ({apiHelpers, navigationMenusPage, page, site}) => {
		await apiHelpers.headlessDelivery.createSitePage({
			siteId: site.id,
			title: getRandomString(),
		});

		await navigationMenusPage.goto(site.friendlyUrlPath);

		const navigationMenuName1 = getRandomString();

		const navigationMenuName2 = getRandomString();

		await navigationMenusPage.createNavigationMenu(navigationMenuName1);

		await navigationMenusPage.goto(site.friendlyUrlPath);

		await page.waitForTimeout(300);

		await (
			await navigationMenusPage.getNavigationMenuActionMenu(
				navigationMenuName1
			)
		).click();

		await (await navigationMenusPage.getMenuItem('Rename')).click();

		await page.getByPlaceholder('Name').fill(navigationMenuName2);

		await page.getByRole('button', {name: 'Save'}).click();

		await expect(
			page.getByRole('link', {name: navigationMenuName2})
		).toBeVisible();
	}
);
