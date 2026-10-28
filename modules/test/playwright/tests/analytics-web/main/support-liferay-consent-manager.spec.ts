/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Page, expect, mergeTests} from '@playwright/test';

import {apiHelpersTest} from '../../../fixtures/apiHelpersTest';
import {dataApiHelpersTest} from '../../../fixtures/dataApiHelpersTest';
import {globalMenuPagesTest} from '../../../fixtures/globalMenuPagesTest';
import {loginAnalyticsCloudTest} from '../../../fixtures/loginAnalyticsCloudTest';
import {loginTest} from '../../../fixtures/loginTest';
import {systemSettingsPageTest} from '../../../fixtures/systemSettingsPageTest';
import {ApiHelpers} from '../../../helpers/ApiHelpers';
import {liferayConfig} from '../../../liferay.config';
import {SystemSettingsPage} from '../../../pages/configuration-admin-web/SystemSettingsPage';
import {clickAndExpectToBeVisible} from '../../../utils/clickAndExpectToBeVisible';
import getRandomString from '../../../utils/getRandomString';
import {waitForAlert} from '../../../utils/waitForAlert';
import {
	connectToAnalyticsCloud,
	disconnectFromAnalyticsCloud,
	goNextStep,
	goToAnalyticsCloudInstanceSettings,
	syncAllContacts,
	toggleSiteSync,
} from '../../analytics-settings-web/main/utils/analytics-settings';
import {createChannel} from '../../osb-faro-web/main/utils/channel';
import {createDataSource} from '../../osb-faro-web/main/utils/data-source';

export const test = mergeTests(
	apiHelpersTest,
	dataApiHelpersTest,
	globalMenuPagesTest,
	loginAnalyticsCloudTest(),
	loginTest(),
	systemSettingsPageTest
);

async function changeCookiePreference({
	enableCookieBanner,
	enableExplicitCookieConsentMode,

	page,
	systemSettingsPage,
}: {
	enableCookieBanner?: boolean;
	enableExplicitCookieConsentMode?: boolean;
	page: Page;
	systemSettingsPage: SystemSettingsPage;
}) {
	await systemSettingsPage.goToSystemSetting('Privacy', 'Consent Manager');

	const cookieBannerCheckbox = await page.getByLabel('Enabled', {
		exact: true,
	});

	if (enableCookieBanner) {
		await cookieBannerCheckbox.check();

		const explicitCookieConsentModeCheckbox = await page.getByLabel(
			'Explicit Cookie Consent Mode'
		);

		if (enableExplicitCookieConsentMode) {
			await explicitCookieConsentModeCheckbox.check();
		}
		else {
			await explicitCookieConsentModeCheckbox.uncheck();
		}
	}
	else {
		await cookieBannerCheckbox.uncheck();
	}

	const publishButton = page
		.getByRole('button', {name: 'Save'})
		.or(page.getByRole('button', {name: 'Update'}));

	publishButton.click();

	await waitForAlert(page, `Success:Your request completed successfully.`);
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

	await toggleSiteSync({
		channelName,
		page,
	});

	await goNextStep(page);

	await syncAllContacts(page);

	await goNextStep(page);

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
	'LPD-6540 Support Liferay Consent Manager',
	{
		tag: '@LPD-6540',
	},
	() => {
		test.afterEach(async ({page, systemSettingsPage}) => {
			await systemSettingsPage.goToSystemSetting(
				'Privacy',
				'Consent Manager'
			);

			await page
				.getByRole('heading', {name: 'Consent Manager'})
				.waitFor();

			await clickAndExpectToBeVisible({
				autoClick: true,
				target: systemSettingsPage.page.getByRole('menuitem', {
					name: 'Reset Default Values',
				}),
				trigger: systemSettingsPage.page.getByRole('button', {
					name: 'Actions',
				}),
			});
		});

		test.beforeEach(async ({apiHelpers, page}) => {
			await connectACToDXP({
				apiHelpers,
				page,
			});
		});

		test('When Cookie Preference Handling and Explicit Cookie Consent Mode are both Enabled, AC tracking should be disabled by default and only be enabled as soon the user accepts the performance cookies', async ({
			page,
			systemSettingsPage,
		}) => {
			await test.step('Enable the cookie banner and explicit consent mode', async () => {
				await changeCookiePreference({
					enableCookieBanner: true,
					enableExplicitCookieConsentMode: true,
					page,
					systemSettingsPage,
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
			systemSettingsPage,
		}) => {
			await test.step('Enable the cookie banner and explicit consent mode', async () => {
				await changeCookiePreference({
					enableCookieBanner: true,
					enableExplicitCookieConsentMode: true,
					page,
					systemSettingsPage,
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
			systemSettingsPage,
		}) => {
			await test.step('Enable the cookie banner and Disabled explicit consent mode', async () => {
				await changeCookiePreference({
					enableCookieBanner: true,
					enableExplicitCookieConsentMode: false,
					page,
					systemSettingsPage,
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
			systemSettingsPage,
		}) => {
			await test.step('Enable the cookie banner and Disabled explicit consent mode', async () => {
				await changeCookiePreference({
					enableCookieBanner: true,
					enableExplicitCookieConsentMode: false,
					page,
					systemSettingsPage,
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
