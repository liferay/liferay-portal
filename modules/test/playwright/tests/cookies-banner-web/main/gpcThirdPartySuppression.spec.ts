/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Page, expect, mergeTests} from '@playwright/test';

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

const CONSENT_TYPES = [
	'CONSENT_TYPE_FUNCTIONAL',
	'CONSENT_TYPE_PERFORMANCE',
	'CONSENT_TYPE_PERSONALIZATION',
];

test.afterEach(async ({systemSettingsPage}) => {
	await test.step('Reset All Consent Manager Configurations', async () => {
		await resetAllConsentManagerConfigurations(systemSettingsPage);
	});

	await test.step('Clear Consent Cookies if present', async () => {
		await clearConsentCookies(systemSettingsPage.page);
	});
});

async function findEmittedThirdPartyCookiesScript(page: Page) {
	const moduleScripts = await page
		.locator('script[type="module"]')
		.allTextContents();

	return moduleScripts.find(
		(content) =>
			content.includes('runThirdPartyCookiesInterval(') ||
			content.includes('suppressThirdPartyCookies(')
	);
}

async function preSeedConsentCookies(page: Page) {
	await page.context().addCookies(
		CONSENT_TYPES.map((name) => ({
			domain: 'localhost',
			name,
			path: '/',
			value: 'true',
		}))
	);
}

test.describe('with Sec-GPC: 1 header and admin honoring GPC', () => {
	test.use({extraHTTPHeaders: {'Sec-GPC': '1'}});

	test(
		'Emits suppressThirdPartyCookies and declines consent cookies',
		{tag: '@LPD-86317'},
		async ({page}) => {
			await updateConsentManagerConfiguration(page, {
				enabled: true,
				forceReload: true,
				globalPrivacyControlEnabled: true,
			});

			await preSeedConsentCookies(page);

			await page.goto('/');

			const emittedScript =
				await findEmittedThirdPartyCookiesScript(page);

			expect(emittedScript).not.toContain(
				'runThirdPartyCookiesInterval('
			);
			expect(emittedScript).toContain('suppressThirdPartyCookies(');

			await expect
				.poll(
					async () => {
						const cookies = await page.context().cookies();

						return cookies
							.filter((cookie) =>
								CONSENT_TYPES.includes(cookie.name)
							)
							.map((cookie) => `${cookie.name}=${cookie.value}`)
							.sort();
					},
					{timeout: 5000}
				)
				.toEqual([
					'CONSENT_TYPE_FUNCTIONAL=false',
					'CONSENT_TYPE_PERFORMANCE=false',
					'CONSENT_TYPE_PERSONALIZATION=false',
				]);
		}
	);
});

test.describe('without admin honoring GPC', () => {
	test(
		'Emits runThirdPartyCookiesInterval (regression guard)',
		{tag: '@LPD-86317'},
		async ({page}) => {
			await updateConsentManagerConfiguration(page, {
				enabled: true,
				forceReload: true,
				globalPrivacyControlEnabled: false,
			});

			await page.goto('/');

			const emittedScript =
				await findEmittedThirdPartyCookiesScript(page);

			expect(emittedScript).toContain('runThirdPartyCookiesInterval(');
			expect(emittedScript).not.toContain('suppressThirdPartyCookies(');
		}
	);
});
