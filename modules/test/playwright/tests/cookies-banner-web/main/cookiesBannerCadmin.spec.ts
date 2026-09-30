/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {isolatedLayoutTest} from '../../../fixtures/isolatedLayoutTest';
import {loginTest} from '../../../fixtures/loginTest';
import {systemSettingsPageTest} from '../../../fixtures/systemSettingsPageTest';
import {
	clearConsentCookies,
	resetConsentManagerConfiguration,
	updateConsentManagerConfiguration,
} from './utils/consentManagerConfigurationHelper';

export const test = mergeTests(
	isolatedLayoutTest(),
	loginTest(),
	systemSettingsPageTest
);

test.afterEach(async ({systemSettingsPage}) => {
	await test.step('Deactivate Consent Manager', async () => {
		await updateConsentManagerConfiguration(systemSettingsPage.page, {
			active: false,
			forceReload: true,
		});
	});

	await test.step('Reset Consent Manager Configuration', async () => {
		await resetConsentManagerConfiguration(systemSettingsPage);
	});

	await test.step('Clear Consent Cookies if present', async () => {
		await clearConsentCookies(systemSettingsPage.page);
	});
});

test('LPD-25440 Cookie Banner Cadmin', async ({page}) => {
	await test.step('Enable Third Party Cookies', async () => {
		await updateConsentManagerConfiguration(page, {
			enabled: true,
			forceReload: true,
		});
	});

	await test.step('Open Configuration', async () => {
		await page.goto('/');

		await page
			.locator('div[role="dialog"][aria-modal="true"]')
			.waitFor({state: 'visible'});

		const configuration = page.getByRole('button', {name: 'Configuration'});

		await configuration.waitFor({state: 'visible'});

		await configuration.click();
	});

	await test.step('Check cadmin is not applied', async () => {
		const modalBody = page
			.frameLocator('#cookiesBannerConfiguration_iframe_')
			.locator('.dialog-iframe-popup');

		await expect(modalBody).not.toHaveClass(/cadmin/);
	});
});

test(
	'Cookie Banner renders with a solid background and drop shadow',
	{tag: '@LPD-94502'},
	async ({page}) => {
		await test.step('Enable Third Party Cookies', async () => {
			await updateConsentManagerConfiguration(page, {
				enabled: true,
				forceReload: true,
			});
		});

		await test.step('Render the banner on the configuration page', async () => {
			await page.reload();

			await page.locator('.cookies-banner').waitFor({state: 'visible'});
		});

		const banner = page.locator('.cookies-banner');

		let lightBackgroundColor: string;
		let lightColor: string;

		await test.step('Verify the banner card is not transparent and has a shadow', async () => {
			await expect(banner).not.toHaveCSS(
				'background-color',
				'rgba(0, 0, 0, 0)'
			);

			await expect(banner).not.toHaveCSS('box-shadow', 'none');

			[lightBackgroundColor, lightColor] = await banner.evaluate(
				(element) => {
					const {backgroundColor, color} = getComputedStyle(element);

					return [backgroundColor, color];
				}
			);
		});

		await test.step('Verify the banner card adapts to dark mode', async () => {
			await page.evaluate(() => {
				document.documentElement.dataset.colorScheme = 'dark';
			});

			await expect(banner).not.toHaveCSS(
				'background-color',
				'rgba(0, 0, 0, 0)'
			);

			await expect(banner).not.toHaveCSS(
				'background-color',
				lightBackgroundColor
			);

			await expect(banner).not.toHaveCSS('color', lightColor);
		});
	}
);
