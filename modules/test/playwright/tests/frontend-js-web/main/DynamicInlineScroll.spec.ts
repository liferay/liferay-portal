/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {dataApiHelpersTest} from '../../../fixtures/dataApiHelpersTest';
import {isolatedSiteTest} from '../../../fixtures/isolatedSiteTest';
import {loginTest} from '../../../fixtures/loginTest';
import {usersAndOrganizationsPagesTest} from '../../../fixtures/usersAndOrganizationsPagesTest';

export const test = mergeTests(
	dataApiHelpersTest,
	isolatedSiteTest,
	loginTest(),
	usersAndOrganizationsPagesTest
);

test(
	'Check user pagination url working correctly',
	{tag: '@LPD-61908'},
	async ({apiHelpers, page, usersAndOrganizationsPage}) => {
		test.setTimeout(180000);

		await test.step('Create 22 users', async () => {
			for (let i = 0; i < 22; i++) {
				await usersAndOrganizationsPage.createUser(apiHelpers);
			}
		});

		await test.step('Check pagination 20 URL works', async () => {
			await usersAndOrganizationsPage.goto();

			await page.getByLabel('Items per Page').click();

			await page.getByRole('option', {name: '1'}).click();

			await page
				.getByRole('button', {name: 'Intermediate Pages'})
				.click();

			const page4 = page.getByLabel('Page 4');

			await page4.hover();

			await page.mouse.wheel(0, 2000);

			await page.getByLabel('Page 20').click();

			await expect(page.getByLabel('Page 20')).toBeVisible();
		});
	}
);
