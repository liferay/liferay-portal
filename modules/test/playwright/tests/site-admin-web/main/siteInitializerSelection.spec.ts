/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {dataApiHelpersTest} from '../../../fixtures/dataApiHelpersTest';
import {loginTest} from '../../../fixtures/loginTest';
import {sitesPageTest} from '../../../fixtures/sitesPageTest';
import {LayoutSetPrototype} from '../../../helpers/json-web-services/JSONWebServicesLayoutSetPrototypeApiHelper';
import {checkAccessibility} from '../../../utils/checkAccessibility';
import getRandomString from '../../../utils/getRandomString';
import {getResultsTotal, nextPage} from '../../../utils/pagination';
import {selectSiteInitializerPagesTest} from './fixtures/selectSiteInitializerPagesTest';
import {sitesAdminPagesTest} from './fixtures/sitesAdminPagesTest';

const test = mergeTests(
	dataApiHelpersTest,
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

		const total = await getResultsTotal(page);

		expect(total).toBeGreaterThan(20);

		await expect(
			page.getByText(`Showing 1 to 20 of ${total} entries.`)
		).toBeVisible();

		await nextPage(page);

		await expect(
			page.getByText(
				`Showing 21 to ${Math.min(40, total)} of ${total} entries.`
			)
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
	const layoutSetPrototype: LayoutSetPrototype =
		await apiHelpers.jsonWebServicesLayoutSetPrototype.addLayoutSetPrototypes(
			{
				name: getRandomString(),
			}
		);

	try {
		await selectSiteInitializerPage.goto();

		await sitesPage.customSiteTemplatesItem.click();

		await expect(
			page.getByRole('button', {
				name: layoutSetPrototype.nameCurrentValue,
			})
		).toBeVisible();

		await selectSiteInitializerPage.goto();

		await page
			.getByRole('menuitem', {name: 'Provided by Liferay'})
			.press('ArrowDown');

		await expect(sitesPage.customSiteTemplatesItem).toBeFocused();

		await page.keyboard.press('Enter');

		await page.waitForTimeout(500);

		await sitesPage.customSiteTemplatesItem.press('Tab');

		await expect(
			page.locator('.add-site-action-card').first()
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

	await page.getByRole('menuitem', {name: 'Provided by Liferay'}).focus();

	await page
		.getByRole('menuitem', {name: 'Provided by Liferay'})
		.press('Tab');

	await expect(
		page.getByRole('button', {name: 'Select Template: Blank Site'})
	).toBeFocused();

	await page.keyboard.press('Enter');

	await expect(page.getByRole('heading', {name: 'Add Site'})).toBeVisible();
});
