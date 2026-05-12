/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {apiHelpersTest} from '../../../fixtures/apiHelpersTest';
import {consentManagerConfigurationPageTest} from '../../../fixtures/consentManagerConfigurationPageTest';
import {loginTest} from '../../../fixtures/loginTest';
import {systemSettingsPageTest} from '../../../fixtures/systemSettingsPageTest';
import {waitForAlert} from '../../../utils/waitForAlert';
import {
	resetAllConsentManagerConfigurations,
	updateConsentManagerConfiguration,
} from './utils/consentManagerConfigurationHelper';

const GPC_ENDPOINT = '/.well-known/gpc.json';

const ISO_DATE_PATTERN = /^\d{4}-\d{2}-\d{2}$/;

export const test = mergeTests(
	apiHelpersTest,
	consentManagerConfigurationPageTest,
	loginTest(),
	systemSettingsPageTest
);

test.afterEach(async ({systemSettingsPage}) => {
	await resetAllConsentManagerConfigurations(systemSettingsPage);
});

test.beforeEach(async ({page}) => {
	await updateConsentManagerConfiguration(page, {
		enabled: true,
		forceReload: true,
	});
});

test(
	'GPC well-known endpoint is reachable without authentication',
	{tag: '@LPD-86313'},
	async ({browser}) => {
		const context = await browser.newContext({storageState: undefined});

		try {
			const response = await context.request.get(GPC_ENDPOINT);

			expect(response.status()).toBe(200);

			const body = await response.json();

			expect(typeof body.gpc).toBe('boolean');
		}
		finally {
			await context.close();
		}
	}
);

test(
	'GPC well-known endpoint rejects non-GET methods',
	{tag: '@LPD-86313'},
	async ({apiHelpers}) => {
		const postResponse = await apiHelpers.postResponse(GPC_ENDPOINT);

		expect(postResponse.status()).toBe(405);
		expect(postResponse.headers()['allow']).toBe('GET');

		const putResponse = await apiHelpers.putResponse(GPC_ENDPOINT);

		expect(putResponse.status()).toBe(405);
		expect(putResponse.headers()['allow']).toBe('GET');

		const deleteResponse = await apiHelpers.delete(GPC_ENDPOINT);

		expect(deleteResponse.status()).toBe(405);
		expect(deleteResponse.headers()['allow']).toBe('GET');
	}
);

test(
	'GPC well-known endpoint returns gpc:false when disabled',
	{tag: '@LPD-86313'},
	async ({apiHelpers, consentManagerConfigurationPage}) => {
		await consentManagerConfigurationPage.goTo();

		await expect(
			consentManagerConfigurationPage.globalPrivacyControlEnabledCheckbox
		).not.toBeChecked();

		const response = await apiHelpers.getResponse(GPC_ENDPOINT);

		expect(response.status()).toBe(200);
		expect(response.headers()['content-type']).toContain(
			'application/json'
		);

		const body = await response.json();

		expect(body.gpc).toBe(false);
		expect(body.lastUpdate).toBeUndefined();
	}
);

test(
	'GPC well-known endpoint returns gpc:true with lastUpdate when enabled',
	{tag: '@LPD-86313'},
	async ({apiHelpers, consentManagerConfigurationPage, page}) => {
		await consentManagerConfigurationPage.goTo();

		const acceptAllButton = consentManagerConfigurationPage.page.getByRole(
			'button',
			{name: 'Accept All'}
		);

		await acceptAllButton.click();

		await expect(acceptAllButton).not.toBeVisible();

		await consentManagerConfigurationPage.globalPrivacyControlEnabledCheckbox.check();

		await consentManagerConfigurationPage.updateButton.click();

		await waitForAlert(page);

		await expect(
			consentManagerConfigurationPage.globalPrivacyControlEnabledCheckbox
		).toBeChecked();

		const response = await apiHelpers.getResponse(GPC_ENDPOINT);

		expect(response.status()).toBe(200);
		expect(response.headers()['content-type']).toContain(
			'application/json'
		);

		const body = await response.json();

		expect(body.gpc).toBe(true);
		expect(body.lastUpdate).toMatch(ISO_DATE_PATTERN);
	}
);
