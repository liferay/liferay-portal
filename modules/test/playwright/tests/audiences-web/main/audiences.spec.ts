/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {apiHelpersTest} from '../../../fixtures/apiHelpersTest';
import {dataApiHelpersTest} from '../../../fixtures/dataApiHelpersTest';
import {featureFlagsTest} from '../../../fixtures/featureFlagsTest';
import {isolatedSiteTest} from '../../../fixtures/isolatedSiteTest';
import {loginTest} from '../../../fixtures/loginTest';
import {clickAndExpectToBeVisible} from '../../../utils/clickAndExpectToBeVisible';
import getRandomString from '../../../utils/getRandomString';
import {PORTLET_URLS} from '../../../utils/portletUrls';
import {waitForAlert} from '../../../utils/waitForAlert';

const test = mergeTests(
	apiHelpersTest,
	dataApiHelpersTest,
	featureFlagsTest({
		'LPD-93951': {enabled: true},
	}),
	isolatedSiteTest,
	loginTest()
);

test(
	'Can create, edit and delete an audience with a browser name condition',
	{
		tag: '@LPD-93951',
	},
	async ({page}) => {
		await page.goto(PORTLET_URLS.audiences);

		// Create a new audience

		await page.getByRole('button', {name: 'New Audience'}).first().click();

		const audienceName = 'Audience ' + getRandomString();

		await page.getByPlaceholder('New Audience').fill(audienceName);

		// Add the Browser Name condition and fill in its value

		await expect(async () => {
			await page
				.locator('.audience-builder-attribute')
				.filter({hasText: 'Browser Name'})
				.dragTo(page.locator('.audience-builder-drop-zone'));

			await expect(page.locator('.audience-builder-rule')).toBeVisible({
				timeout: 2000,
			});
		}).toPass();

		await page.getByLabel('Value').fill('Chrome');

		await page.getByRole('button', {name: 'Save'}).click();

		await waitForAlert(page);

		// The audience is listed

		await expect(
			page.locator('tr').filter({hasText: audienceName})
		).toBeVisible();

		// Reopen it and check the values were kept

		await clickAndExpectToBeVisible({
			autoClick: true,
			target: page.getByRole('menuitem', {name: 'Edit'}),
			trigger: page
				.locator('tr')
				.filter({hasText: audienceName})
				.locator('button.dropdown-toggle'),
		});

		await expect(page.getByPlaceholder('New Audience')).toHaveValue(
			audienceName
		);
		await expect(
			page.locator('.audience-builder-rule').getByText('Browser Name')
		).toBeVisible();
		await expect(page.getByLabel('Value')).toHaveValue('Chrome');

		// Go back to the list

		await page.getByRole('link', {exact: true, name: 'Back'}).click();

		// Delete the audience and check it is no longer listed

		page.once('dialog', (dialog) => dialog.accept());

		await clickAndExpectToBeVisible({
			autoClick: true,
			target: page.getByRole('menuitem', {name: 'Delete'}),
			trigger: page
				.locator('tr')
				.filter({hasText: audienceName})
				.locator('button.dropdown-toggle'),
		});

		await waitForAlert(page);

		await expect(
			page.locator('tr').filter({hasText: audienceName})
		).toHaveCount(0);
	}
);
