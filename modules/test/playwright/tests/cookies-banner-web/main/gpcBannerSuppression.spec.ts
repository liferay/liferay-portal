/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {consentManagerConfigurationPageTest} from '../../../fixtures/consentManagerConfigurationPageTest';
import {loginTest} from '../../../fixtures/loginTest';
import {systemSettingsPageTest} from '../../../fixtures/systemSettingsPageTest';
import {
	clearConsentCookies,
	resetAllConsentManagerConfigurations,
	updateConsentManagerConfiguration,
} from './utils/consentManagerConfigurationHelper';

export const test = mergeTests(
	consentManagerConfigurationPageTest,
	loginTest(),
	systemSettingsPageTest
);

test.afterEach(async ({systemSettingsPage}) => {
	await test.step('Reset All Consent Manager Configurations', async () => {
		await resetAllConsentManagerConfigurations(systemSettingsPage);
	});

	await test.step('Clear Consent Cookies if present', async () => {
		await clearConsentCookies(systemSettingsPage.page);
	});
});

test.describe('with Sec-GPC: 1 header and admin honoring GPC', () => {
	test.use({extraHTTPHeaders: {'Sec-GPC': '1'}});

	test(
		'Suppresses the cookie consent banner but keeps the floating icon visible',
		{tag: '@LPD-86319'},
		async ({page}) => {
			await updateConsentManagerConfiguration(page, {
				enabled: true,
				floatingIconEnabled: true,
				forceReload: true,
				globalPrivacyControlEnabled: true,
			});

			await page.goto('/');

			await expect(
				page.getByRole('dialog', {name: 'banner cookies'})
			).toHaveCount(0);

			await expect(
				page.locator(
					'#_com_liferay_cookies_banner_web_portlet_CookiesBannerPortlet_floatingIconButton'
				)
			).toBeVisible();
		}
	);

	test(
		'Renders the cookie consent banner when admin is not honoring GPC',
		{tag: '@LPD-86319'},
		async ({page}) => {
			await updateConsentManagerConfiguration(page, {
				enabled: true,
				forceReload: true,
				globalPrivacyControlEnabled: false,
			});

			await page.goto('/');

			await expect(
				page.getByRole('dialog', {name: 'banner cookies'})
			).toBeVisible();
		}
	);
});

test.describe('without Sec-GPC header', () => {
	test(
		'Renders the cookie consent banner (regression guard)',
		{tag: '@LPD-86319'},
		async ({page}) => {
			await updateConsentManagerConfiguration(page, {
				enabled: true,
				forceReload: true,
				globalPrivacyControlEnabled: true,
			});

			await page.goto('/');

			await expect(
				page.getByRole('dialog', {name: 'banner cookies'})
			).toBeVisible();
		}
	);
});
