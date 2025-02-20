/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {apiHelpersTest} from '../../fixtures/apiHelpersTest';
import {loginTest} from '../../fixtures/loginTest';
import {sitesPageTest} from '../../fixtures/sitesPageTest';
import {LayoutSetPrototype} from '../../helpers/json-web-services/JSONWebServicesLayoutSetPrototypeApiHelper';
import {checkAccessibility} from '../../utils/checkAccessibility';
import getRandomString from '../../utils/getRandomString';
import {selectSiteInitializerPagesTest} from './fixtures/selectSiteInitializerPagesTest';

const test = mergeTests(
	apiHelpersTest,
	loginTest(),
	selectSiteInitializerPagesTest,
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

		for (let i = 0; i < 5; i++) {
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

		await page.getByLabel('Items per Page').click();

		await page.getByRole('option', {name: '4  Entries per Page'}).click();

		await expect(
			page.getByText('Showing 1 to 4 of 5 entries.')
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
