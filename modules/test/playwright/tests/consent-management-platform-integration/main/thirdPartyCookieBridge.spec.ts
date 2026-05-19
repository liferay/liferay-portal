/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Page, expect, mergeTests} from '@playwright/test';

import {consentManagerConfigurationPageTest} from '../../../fixtures/consentManagerConfigurationPageTest';
import {loginTest} from '../../../fixtures/loginTest';
import {systemSettingsPageTest} from '../../../fixtures/systemSettingsPageTest';
import {SiteSettingsPage} from '../../../pages/site-admin-web/SiteSettingsPage';
import {clickAndExpectToBeHidden} from '../../../utils/clickAndExpectToBeHidden';
import performLogin, {performLogout} from '../../../utils/performLogin';
import {waitForAlert} from '../../../utils/waitForAlert';
import {
	resetConsentManagerConfiguration,
	updateConsentManagerConfiguration,
} from '../../cookies-banner-web/main/utils/consentManagerConfigurationHelper';

const test = mergeTests(
	consentManagerConfigurationPageTest,
	loginTest(),
	systemSettingsPageTest
);

test.beforeEach(async ({page}) => {
	await updateConsentManagerConfiguration(page, {
		enabled: true,
		forceReload: true,
	});

	await _acceptCookiesBanner(page);

	await _setCMPEnabled(page, true);
});

test.afterEach(async ({page, systemSettingsPage}) => {
	if (await page.getByRole('button', {name: 'Sign In'}).isVisible()) {
		await performLogin(page, 'test');
	}

	await _setCMPEnabled(page, false);

	await resetConsentManagerConfiguration(systemSettingsPage);
});

test(
	'Sign-in does not set the REMEMBER_ME cookie when functional consent is denied via the third-party CMP',
	{tag: '@LPD-88765'},
	async ({page}) => {
		await _signInWithConsentState(page, {functional: false});

		const cookies = await page.context().cookies();

		expect(
			cookies.find((cookie) => cookie.name === 'REMEMBER_ME')
		).toBeUndefined();
	}
);

test(
	'Sign-in sets the REMEMBER_ME cookie when functional consent is granted via the third-party CMP',
	{tag: '@LPD-88765'},
	async ({page}) => {
		await _signInWithConsentState(page, {functional: true});

		const cookies = await page.context().cookies();

		expect(
			cookies.find((cookie) => cookie.name === 'REMEMBER_ME')
		).toBeDefined();
	}
);

async function _acceptCookiesBanner(page: Page) {
	await clickAndExpectToBeHidden({
		target: page.locator('div[role="dialog"][aria-modal="true"]'),
		trigger: page.getByRole('button', {name: 'Accept All'}),
	});
}

async function _setCMPEnabled(page: Page, enabled: boolean) {
	const siteSettingsPage = new SiteSettingsPage(page);

	await siteSettingsPage.goToSiteSetting(
		'Privacy',
		'Third Party Consent Management Platform'
	);

	await _acceptCookiesBanner(page);

	await page
		.getByLabel('Consent Management Platform Provider Name', {exact: true})
		.fill('Playwright Mock CMP');

	await page
		.getByLabel('Script Tag', {exact: true})
		.fill('<script>/* mock CMP */</script>');

	await page.getByLabel('Enabled', {exact: true}).setChecked(enabled);

	await siteSettingsPage.saveButton.click();

	await waitForAlert(page);
}

async function _signInWithConsentState(
	page: Page,
	{functional}: {functional: boolean}
) {
	await performLogout(page);

	await page.context().clearCookies({name: 'LIFERAY_CONSENT_STATE'});

	await page.context().addCookies([
		{
			domain: new URL(page.url()).hostname,
			name: 'LIFERAY_CONSENT_STATE',
			path: '/',
			value: encodeURIComponent(
				JSON.stringify({
					functional,
					necessary: true,
					performance: false,
					personalization: false,
				})
			),
		},
	]);

	await performLogin(page, 'test', '/');
}
