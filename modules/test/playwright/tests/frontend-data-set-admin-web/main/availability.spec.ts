/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {loginTest} from '../../../fixtures/loginTest';
import {customDataSetsPageTest} from './fixtures/customDataSetsPageTest';

export const test = mergeTests(customDataSetsPageTest, loginTest());

test.describe('Data Set Manager availability', () => {
	test(
		'Data Sets is in the Object category of the application menu',
		{tag: ['@LPD-107564', '@LPS-188590']},
		async ({customDataSetsPage, page}) => {
			await test.step('Open the application menu and go to the Control Panel tab', async () => {
				await customDataSetsPage.globalMenuPage.goToControlPanel();
			});

			await test.step('Check that "Data Sets" is in the "Object" category', async () => {
				const objectHeading = page.locator('.nav-link.collapse-icon', {
					hasText: 'Object',
				});

				await objectHeading.waitFor();

				await expect(objectHeading.locator('..')).toContainText(
					'Data Sets'
				);
			});
		}
	);

	test(
		'Frontend Data Set Cell Renderer can be added in Client Extensions',
		{tag: ['@LPD-107564', '@LPS-188590']},
		async ({customDataSetsPage, page}) => {
			await test.step('Navigate to Client Extensions page', async () => {
				await customDataSetsPage.globalMenuPage.goToApplications(
					'Client Extensions'
				);
			});

			await test.step('Open add menu', async () => {
				await page.getByRole('button', {name: 'New'}).first().click();
			});

			await test.step('Check that Frontend Data Set Cell Renderer is displayed', async () => {
				await expect(
					page.getByRole('menuitem', {
						exact: true,
						name: 'Add Frontend Data Set Cell Renderer',
					})
				).toBeVisible();
			});
		}
	);

	test(
		'Data Set fragment is available in the page editor',
		{tag: ['@LPD-107564', '@LPS-188590']},
		async ({page}) => {
			await test.step('Go to home edit page', async () => {
				await page.goto(`/web/guest/home?p_l_mode=edit`);
			});

			await test.step('Check that "Data Set" is displayed as a fragment', async () => {
				await page
					.getByLabel('Search Fragments and Widgets')
					.fill('Data Set');

				await expect(
					page.getByRole('menuitem', {
						exact: true,
						name: 'Data Set Add Data Set Mark Data Set as Favorite',
					})
				).toBeVisible();
			});
		}
	);
});
