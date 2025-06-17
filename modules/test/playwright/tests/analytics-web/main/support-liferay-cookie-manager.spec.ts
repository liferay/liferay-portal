/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Page, expect, mergeTests} from '@playwright/test';

import {apiHelpersTest} from '../../../fixtures/apiHelpersTest';
import {dataApiHelpersTest} from '../../../fixtures/dataApiHelpersTest';
import {loginAnalyticsCloudTest} from '../../../fixtures/loginAnalyticsCloudTest';
import {loginTest} from '../../../fixtures/loginTest';
import {ApiHelpers} from '../../../helpers/ApiHelpers';
import {liferayConfig} from '../../../liferay.config';
import getRandomString from '../../../utils/getRandomString';
import {
	connectToAnalyticsCloud,
	disconnectFromAnalyticsCloud,
	goNextStep,
	goToAnalyticsCloudInstanceSettings,
	syncAllContacts,
	syncSite,
} from '../../analytics-settings-web/main/utils/analytics-settings';
import {createChannel} from '../../osb-faro-web/main/utils/channel';
import {createDataSource} from '../../osb-faro-web/main/utils/data-source';

export const test = mergeTests(
	apiHelpersTest,
	dataApiHelpersTest,
	loginAnalyticsCloudTest(),
	loginTest()
);

async function changeCookiePreference({
	enableCookieBanner,
	enableExplicitCookieConsentMode,
	page,
}: {
	enableCookieBanner?: boolean;
	enableExplicitCookieConsentMode?: boolean;
	page: Page;
}) {
	await page.getByLabel('Open Applications MenuCtrl+Alt+A').click();

	await page.getByRole('tab', {name: 'Control Panel'}).click();

	await page.getByRole('menuitem', {name: 'Instance Settings'}).click();

	await page.getByRole('link', {name: 'Cookies'}).click();

	const cookieBannerCheckbox = await page.getByLabel('Enabled');

	await page.waitForTimeout(3000);

	if (enableCookieBanner) {
		await cookieBannerCheckbox.check();
	}
	else {
		await cookieBannerCheckbox.uncheck();
	}

	const explicitCookieConsentModeCheckbox = await page.getByLabel(
		'Explicit Cookie Consent Mode'
	);

	if (enableCookieBanner) {
		if (enableExplicitCookieConsentMode) {
			await explicitCookieConsentModeCheckbox.check();
		}
		else {
			await explicitCookieConsentModeCheckbox.uncheck();
		}
	}

	const submitButton = await page.$(
		'button[data-qa-id="submitConfiguration"]'
	);

	await submitButton.click();

	await page.waitForTimeout(3000);
}

async function connectACToDXP({
	apiHelpers,
	page,
}: {
	apiHelpers: ApiHelpers;
	page: Page;
}) {
	const channelName = 'My Property - ' + getRandomString();

	await createChannel({
		apiHelpers,
		channelName,
	});

	const {token} = await createDataSource(page);

	await goToAnalyticsCloudInstanceSettings(page);

	const cookieBannerElement = await page.$('div.portlet-cookies-banner');

	if (cookieBannerElement) {
		await cookieBannerElement.evaluate((div) => {
			div.style.display = 'none';
		});
	}

	await disconnectFromAnalyticsCloud(page);

	await connectToAnalyticsCloud(page, {token});

	await syncSite({
		channelName,
		page,
	});

	await goNextStep(page);

	await syncAllContacts(page);

	await goNextStep(page);

	await page.getByRole('button', {name: 'Finish'}).click();
}

async function checkAnalyticsInstance(page: Page) {
	return await page.evaluate(() => {

		// @ts-ignore

		return !!window.Analytics && !window.Analytics._disposed;
	});
}

test.describe(
	'LPD-6540 Support Liferay Cookie Manager',
	{
		tag: '@LPD-6540',
	},
	() => {
		test.beforeEach(async ({apiHelpers, page}) => {
			await connectACToDXP({
				apiHelpers,
				page,
			});
		});

		test('When Cookie Preference Handling and Explicit Cookie Consent Mode are both Enabled, AC tracking should be disabled by default and only be enabled as soon the user accepts the performance cookies', async ({
			page,
		}) => {
			await test.step('Enable the cookie banner and explicit consent mode', async () => {
				await changeCookiePreference({
					enableCookieBanner: true,
					enableExplicitCookieConsentMode: true,
					page,
				});
			});

			await test.step('Go to DXP > Check that events are not being sent', async () => {
				await page.goto(liferayConfig.environment.baseUrl);

				expect(await checkAnalyticsInstance(page)).toBeFalsy();
			});

			await test.step('Accept all cookies > Check that events start to be sent', async () => {
				await page.getByRole('button', {name: 'Accept All'}).click();

				await page.waitForTimeout(3000);

				expect(await checkAnalyticsInstance(page)).toBeTruthy();
			});
		});

		test('When Cookie Preference Handling and Explicit Cookie Consent Mode are both Enabled, AC tracking should be disabled by default and remain disabled if end user did not accept the perfomance cookies', async ({
			page,
		}) => {
			await test.step('Enable the cookie banner and explicit consent mode', async () => {
				await changeCookiePreference({
					enableCookieBanner: true,
					enableExplicitCookieConsentMode: true,
					page,
				});
			});

			await test.step('Go to DXP > Check that events are not being sent', async () => {
				await page.goto(liferayConfig.environment.baseUrl);

				expect(await checkAnalyticsInstance(page)).toBeFalsy();
			});

			await test.step('Decline all cookies > Check that events are not sent', async () => {
				await page.getByRole('button', {name: 'Decline All'}).click();

				await page.waitForTimeout(3000);

				expect(await checkAnalyticsInstance(page)).toBeFalsy();
			});
		});

		test('When Cookie Preference Handling is Enabled and Explicit Cookie Consent Mode is not Enabled, AC tracking should be enabled by default until the user rejects the performance cookies', async ({
			page,
		}) => {
			await test.step('Enable the cookie banner and Disabled explicit consent mode', async () => {
				await changeCookiePreference({
					enableCookieBanner: true,
					enableExplicitCookieConsentMode: false,
					page,
				});
			});

			await test.step('Go to DXP > Check that events are sent', async () => {
				await page.goto(liferayConfig.environment.baseUrl);

				expect(await checkAnalyticsInstance(page)).toBeTruthy();
			});

			await test.step('Decline all cookies > Check that events are not sent', async () => {
				await page.getByRole('button', {name: 'Decline All'}).click();

				await page.waitForTimeout(3000);

				expect(await checkAnalyticsInstance(page)).toBeFalsy();
			});
		});

		test('When Cookie Preference Handling is Enabled and Explicit Cookie Consent Mode is not Enabled, AC tracking should be enabled by default and remain enabled if end user accepts the perfomance cookies', async ({
			page,
		}) => {
			await test.step('Enable the cookie banner and Disabled explicit consent mode', async () => {
				await changeCookiePreference({
					enableCookieBanner: true,
					enableExplicitCookieConsentMode: false,
					page,
				});
			});

			await test.step('Go to DXP > Check that events are sent', async () => {
				await page.goto(liferayConfig.environment.baseUrl);

				expect(await checkAnalyticsInstance(page)).toBeTruthy();
			});

			await test.step('Accept all cookies > Check that events are sent', async () => {
				await page.getByRole('button', {name: 'Accept All'}).click();

				await page.waitForTimeout(3000);

				expect(await checkAnalyticsInstance(page)).toBeTruthy();
			});
		});
	}
);
