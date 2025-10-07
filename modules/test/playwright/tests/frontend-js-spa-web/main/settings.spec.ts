/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {isolatedLayoutTest} from '../../../fixtures/isolatedLayoutTest';
import {loginTest} from '../../../fixtures/loginTest';
import {systemSettingsPageTest} from '../../../fixtures/systemSettingsPageTest';
import isSPAEnabled from '../../../utils/isSPAEnabled';
import {waitForAlert} from '../../../utils/waitForAlert';

export const test = mergeTests(
	isolatedLayoutTest({publish: false}),
	loginTest(),
	systemSettingsPageTest
);

test(
	'Exclude path in SPA Settings',
	{
		tag: '@LPS-108376',
	},
	async ({layout, page, systemSettingsPage}) => {
		await test.step('Navigate to an isolated page', async () => {
			await page.goto(`/web/guest/${layout.friendlyURL}`);
		});

		await test.step('Check if SPA is enabled', async () => {
			expect(await isSPAEnabled({page})).toBeTruthy();
		});

		await test.step('Exclude path in SPA Settings', async () => {
			await systemSettingsPage.goToSystemSetting(
				'Infrastructure',
				'Frontend SPA Infrastructure'
			);

			const customExcludedPathsInput = page.getByRole('textbox', {
				name: 'Custom Excluded Paths',
			});

			await customExcludedPathsInput.fill(
				`/web/guest${layout.friendlyURL}`
			);

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

		await test.step('Go back to isolated page and check SPA', async () => {
			await page.goto(`/web/guest${layout.friendlyURL}`);

			expect(await isSPAEnabled({page})).toBeFalsy();
		});
	}
);

test(
	'Show notification when SPA request times out',
	{tag: '@LPS-67072'},
	async ({page, systemSettingsPage}) => {
		await test.step('Navigate to SPA Settings page', async () => {
			await systemSettingsPage.goToSystemSetting(
				'Infrastructure',
				'Frontend SPA Infrastructure'
			);
		});

		const userNotificationTimeoutInput = page.getByRole('textbox', {
			name: 'User Notification Timeout',
		});

		const saveButton = page.locator('button', {
			hasText: /Save|Update/,
		});

		await test.step('Change the default timeout from 30000ms to 1ms', async () => {
			await userNotificationTimeoutInput.fill('1');

			await saveButton.click();

			await waitForAlert(page);
		});

		await test.step('Reload SPA Settings page, navigate and check that the User Notificacion appears in the page', async () => {
			await page.reload();

			await userNotificationTimeoutInput.waitFor({state: 'visible'});

			let warningWasDisplayed = false;

			page.on('request', () => {
				if (
					page.getByText(
						'It looks like this is taking longer than expected.'
					)
				) {
					warningWasDisplayed = true;
				}
			});

			await saveButton.click();

			expect(warningWasDisplayed).toBe(true);
		});

		await test.step('Change the timeout back to 30000ms', async () => {
			await userNotificationTimeoutInput.fill('30000');

			await saveButton.click();

			await waitForAlert(page);
		});
	}
);
