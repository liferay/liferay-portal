/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {apiHelpersTest} from '../../../fixtures/apiHelpersTest';
import {loginTest} from '../../../fixtures/loginTest';
import {sitesPageTest} from '../../../fixtures/sitesPageTest';
import {LayoutSetPrototype} from '../../../helpers/json-web-services/JSONWebServicesLayoutSetPrototypeApiHelper';
import {checkAccessibility} from '../../../utils/checkAccessibility';
import getRandomString from '../../../utils/getRandomString';
import {selectSiteInitializerPagesTest} from './fixtures/selectSiteInitializerPagesTest';
import {sitesAdminPagesTest} from './fixtures/sitesAdminPagesTest';

const test = mergeTests(
	apiHelpersTest,
	loginTest(),
	selectSiteInitializerPagesTest,
	sitesAdminPagesTest,
	sitesPageTest
);

test('Check select site initializers accessibility', async ({
	page,
	selectSiteInitializerPage,
}) => {

	// Go to site initializers selection page

	await selectSiteInitializerPage.goto();

	// Check all of them have correct label

	const cards = await page.locator('.card').all();

	for (const card of cards) {
		await expect(card.getByLabel('Select Template:')).toBeVisible();
	}

	// Check accessibility

	await checkAccessibility({
		page,
		selectors: ['.portlet-content-container'],
	});
});

test(
	'Ensure pagination works properly for Custom Site Templates tab',
	{
		tag: '@LPD-39408',
	},
	async ({apiHelpers, page, selectSiteInitializerPage, sitesPage}) => {
		const layoutSetPrototypes = [];

		for (let i = 0; i < 21; i++) {
			const layoutSetPrototype: LayoutSetPrototype =
				await apiHelpers.jsonWebServicesLayoutSetPrototype.addLayoutSetPrototypes(
					{
						name: getRandomString(),
					}
				);

			layoutSetPrototypes.push(layoutSetPrototype);
		}

		await selectSiteInitializerPage.goto();

		await sitesPage.customSiteTemplatesItem.click();

		await expect(
			page.getByText('Showing 1 to 20 of 21 entries.')
		).toBeVisible();

		await expect(
			page.getByRole('menuitem', {name: 'Provided by Liferay'})
		).not.toHaveClass(/active/);

		await expect(
			page.getByRole('menuitem', {name: 'Custom Site Templates'})
		).toHaveClass(/active/);

		for (let i = 0; i < layoutSetPrototypes.length; i++) {
			await apiHelpers.jsonWebServicesLayoutSetPrototype.deleteLayoutSetPrototypes(
				layoutSetPrototypes[i].layoutSetPrototypeId.toString()
			);
		}
	}
);

test('Ensure that the site administrator can select custom site template via keyboard during site creation', async ({
	apiHelpers,
	page,
	selectSiteInitializerPage,
	sitesPage,
}) => {
	let layoutSetPrototype: LayoutSetPrototype;

	try {
		await selectSiteInitializerPage.goto();

		await sitesPage.customSiteTemplatesItem.click();

		await expect(
			page.getByText('There are no site templates.')
		).toBeVisible();

		layoutSetPrototype =
			await apiHelpers.jsonWebServicesLayoutSetPrototype.addLayoutSetPrototypes(
				{
					name: getRandomString(),
				}
			);

		await selectSiteInitializerPage.goto();

		await page
			.getByRole('menuitem', {name: 'Provided by Liferay'})
			.press('ArrowDown');

		await expect(sitesPage.customSiteTemplatesItem).toBeFocused();

		await page.keyboard.press('Enter');

		await page.waitForTimeout(500);

		await sitesPage.customSiteTemplatesItem.press('Tab');

		await expect(
			page.getByRole('button', {
				name: layoutSetPrototype.nameCurrentValue,
			})
		).toBeFocused();

		await page.keyboard.press('Enter');

		await expect(
			page.getByRole('heading', {name: 'Add Site'})
		).toBeVisible();
	}
	finally {
		await apiHelpers.jsonWebServicesLayoutSetPrototype.deleteLayoutSetPrototypes(
			layoutSetPrototype.layoutSetPrototypeId.toString()
		);
	}
});

test('Ensure that the site administrator can select template provided by liferay via keyboard during site creation', async ({
	page,
	selectSiteInitializerPage,
}) => {
	await selectSiteInitializerPage.goto();

	await page
		.getByRole('menuitem', {name: 'Provided by Liferay'})
		.press('Tab');

	await expect(
		page.getByRole('button', {name: 'Select Template: Blank Site'})
	).toBeFocused();

	await page.keyboard.press('Enter');

	await expect(page.getByRole('heading', {name: 'Add Site'})).toBeVisible();
});

test('Ensure that, using Keyboard Navigation, it is possible to access the back button of Sites and sites templates and edit mode and the tooltip is Go to Sites', async ({
	apiHelpers,
	page,
	sitesAdminPage,
}) => {
	let site: Site;

	try {
		await sitesAdminPage.goto();

		await page.getByRole('link', {name: 'Add Site'}).click();

		await page.waitForTimeout(300);

		await page
			.getByRole('link', {name: 'Skip to Main Content'})
			.press('Tab');

		await expect(
			page.getByRole('link', {name: 'Go to Sites'})
		).toBeFocused();

		await expect(
			page.getByRole('tooltip').getByText('Go to Sites')
		).toBeVisible();

		site = await apiHelpers.headlessSite.createSite({
			name: getRandomString(),
		});

		await page.getByRole('link', {name: 'Go to Sites'}).click();

		await page.getByRole('link', {name: site.name}).click();

		await page.waitForTimeout(300);

		await page
			.getByRole('link', {name: 'Skip to Main Content'})
			.press('Tab');

		await expect(
			page.getByRole('link', {name: 'Go to Sites'})
		).toBeFocused();

		await expect(
			page.getByRole('tooltip').getByText('Go to Sites')
		).toBeVisible();
	}
	finally {
		await apiHelpers.headlessSite.deleteSite(site.id);
	}
});

test('Ensure that, using Keyboard Navigation, it is possible to access the back button of a Site child and the tooltip is Go to Site Name', async ({
	apiHelpers,
	page,
	sitesAdminPage,
}) => {
	const site: Site = await apiHelpers.headlessSite.createSite({
		name: getRandomString(),
	});

	const childSite: Site = await apiHelpers.headlessSite.createSite({
		name: getRandomString(),
		parentSiteKey: site.name,
	});

	const grandChildSite: Site = await apiHelpers.headlessSite.createSite({
		name: getRandomString(),
		parentSiteKey: childSite.name,
	});

	try {
		await sitesAdminPage.goto();

		await page.getByRole('link', {name: site.name}).click();

		await page.getByRole('link', {name: childSite.name}).click();

		await page.waitForTimeout(300);

		await page
			.getByRole('link', {name: 'Skip to Main Content'})
			.press('Tab');

		await expect(
			page.getByRole('link', {name: `Go to ${site.name}`})
		).toBeFocused();

		await expect(
			page.getByRole('tooltip').getByText(`Go to ${site.name}`)
		).toBeVisible();

		await page.getByRole('link', {name: grandChildSite.name}).click();

		await page.waitForTimeout(300);

		await page
			.getByRole('link', {name: 'Skip to Main Content'})
			.press('Tab');

		await expect(
			page.getByRole('link', {name: `Go to ${childSite.name}`})
		).toBeFocused();

		await expect(
			page.getByRole('tooltip').getByText(`Go to ${childSite.name}`)
		).toBeVisible();
	}
	finally {
		await apiHelpers.headlessSite.deleteSite(grandChildSite.id);

		await apiHelpers.headlessSite.deleteSite(childSite.id);

		await apiHelpers.headlessSite.deleteSite(site.id);
	}
});
