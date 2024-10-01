/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {isolatedLayoutTest} from '../../fixtures/isolatedLayoutTest';
import {loginTest} from '../../fixtures/loginTest';
import {systemSettingsPageTest} from '../../fixtures/systemSettingsPageTest';
import {waitForAlert} from '../../utils/waitForAlert';

export const test = mergeTests(
	isolatedLayoutTest(),
	loginTest(),
	systemSettingsPageTest
);

test('@LPD-26354 Custom Exclude Path', async ({
	layout,
	page,
	systemSettingsPage,
}) => {
	await test.step('Check SPA is enabled', async () => {
		await page.goto(layout.friendlyURL);

		// @ts-ignore

		const liferaySPAOutput = await page.evaluate(() => Liferay.SPA);

		expect(liferaySPAOutput).not.toEqual(undefined);
	});

	await test.step('Exclude path in SPA Settings', async () => {
		await systemSettingsPage.goToSystemSetting(
			'Infrastructure',
			'Frontend SPA Infrastructure'
		);

		const customExcludedPathsInput = page.getByLabel(
			'Custom Excluded Paths',
			{
				exact: true,
			}
		);

		await customExcludedPathsInput.waitFor({state: 'visible'});
		await customExcludedPathsInput.click();
		await customExcludedPathsInput.fill(layout.friendlyURL);

		const updateButton = page.getByRole('button', {
			name: 'Update',
		});

		const saveButton = page.getByRole('button', {
			name: 'Save',
		});

		if (await saveButton.isVisible()) {
			await saveButton.click();
		}
		else if (await updateButton.isVisible()) {
			await updateButton.click();
		}

		await waitForAlert(page);
	});

	await test.step('Go to page and check SPA', async () => {
		await page.goto(layout.friendlyURL);

		await page.reload();

		await page.locator('#content').waitFor({state: 'visible'});

		// @ts-ignore

		const liferaySPAOutput = await page.evaluate(() => Liferay.SPA);

		expect(liferaySPAOutput).toEqual(undefined);
	});
});
