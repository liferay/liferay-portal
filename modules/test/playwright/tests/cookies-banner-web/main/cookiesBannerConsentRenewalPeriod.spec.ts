/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {consentManagerConfigurationPageTest} from '../../../fixtures/consentManagerConfigurationPageTest';
import {loginTest} from '../../../fixtures/loginTest';
import {systemSettingsPageTest} from '../../../fixtures/systemSettingsPageTest';
import {ConsentManagerConfigurationPage} from '../../../pages/cookies-banner-web/ConsentManagerConfigurationPage';
import {waitForAlert} from '../../../utils/waitForAlert';
import {
	clearConsentCookies,
	resetConsentManagerConfiguration,
	updateConsentManagerConfiguration,
} from './utils/consentManagerConfigurationHelper';

const optionalCookieKeys = [
	'CONSENT_TYPE_FUNCTIONAL',
	'CONSENT_TYPE_PERFORMANCE',
	'CONSENT_TYPE_PERSONALIZATION',
];

const requiredCookieKeys = [
	'CONSENT_TYPE_NECESSARY',
	'USER_CONSENT_CONFIGURED',
	'USER_CONSENT_CONFIGURED_DATE',
];

const allCookieKeys = [...optionalCookieKeys, ...requiredCookieKeys];

export const test = mergeTests(
	consentManagerConfigurationPageTest,
	loginTest(),
	systemSettingsPageTest
);

test.afterEach(async ({systemSettingsPage}) => {
	await test.step('Reset Consent Manager Configuration', async () => {
		await resetConsentManagerConfiguration(systemSettingsPage);
	});

	await test.step('Clear Consent Cookies if present', async () => {
		await clearConsentCookies(systemSettingsPage.page);
	});
});

test.beforeEach(async ({page}) => {
	await test.step('Enable Consent Manager', async () => {
		await updateConsentManagerConfiguration(page, {
			enabled: true,
			forceReload: true,
		});
	});

	await test.step('Verify Cookies Banner appears, then Accept All cookies', async () => {
		const cookiesBanner = page.locator('div[role="region"].cookies-banner');

		await expect(cookiesBanner).toBeVisible();

		await page.getByRole('button', {name: 'Accept All'}).click();
	});
});

test(
	'Consent Renewal Period configuration field validation',
	{tag: ['@LPD-68505', '@LPD-87281']},
	async ({consentManagerConfigurationPage}) => {
		await test.step('Validate Consent Renewal Period field', async () => {
			await test.step('Validate default value of 12 months', async () => {
				await expect(
					consentManagerConfigurationPage.consentRenewalPeriodInput
				).toHaveValue('12');
			});

			await test.step('Validate value cannot be less than 1', async () => {
				await expect(
					consentManagerConfigurationPage.consentRenewalPeriodInput
				).toHaveAttribute('min', '1');
				await validateConsentRenewalPeriodValue(
					consentManagerConfigurationPage,
					false,
					'0',
					false
				);
			});

			await test.step('Validate value cannot be more than 12', async () => {
				await expect(
					consentManagerConfigurationPage.consentRenewalPeriodInput
				).toHaveAttribute('max', '12');
				await validateConsentRenewalPeriodValue(
					consentManagerConfigurationPage,
					false,
					'13',
					false
				);
			});

			await test.step('Validate value cannot be null', async () => {
				await validateConsentRenewalPeriodValue(
					consentManagerConfigurationPage,
					false,
					'',
					false
				);
			});

			await test.step('Validate value must be a number', async () => {
				await expect(
					consentManagerConfigurationPage.consentRenewalPeriodInput
				).toHaveAttribute('type', 'number');
			});
		});

		await test.step('Verify dismissing the confirmation modal does not change configuration value', async () => {
			await validateConsentRenewalPeriodValue(
				consentManagerConfigurationPage,
				false,
				'1',
				false
			);
		});

		await test.step('Verify accepting the confirmation modal updates configuration value and Cookies Banner appears again', async () => {
			await validateConsentRenewalPeriodValue(
				consentManagerConfigurationPage,
				false,
				'1',
				true
			);
		});
	}
);

test(
	'Verify confirmation modal only appears if changing the value',
	{tag: ['@LPD-79710', '@LPD-87281']},
	async ({consentManagerConfigurationPage, page}) => {
		const cookiesBanner = page.locator('.cookies-banner');
		const modal = page.getByRole('alertdialog');

		await test.step('Save without changes does not show the confirmation modal', async () => {
			await consentManagerConfigurationPage.enabledCheckbox.setChecked(
				true
			);

			await consentManagerConfigurationPage.consentRenewalPeriodInput.fill(
				'12'
			);

			await consentManagerConfigurationPage.updateButton.click();

			await expect(modal).not.toBeVisible();

			await waitForAlert(page);

			await expect(cookiesBanner).not.toBeVisible();
		});

		await test.step('Changing the value shows the confirmation modal with checkbox auto-checked and disabled', async () => {
			await consentManagerConfigurationPage.consentRenewalPeriodInput.fill(
				'11'
			);

			await consentManagerConfigurationPage.updateButton.click();

			await expect(modal).toContainText(
				'These changes will take effect immediately'
			);

			const checkbox = modal.getByRole('checkbox', {
				name: /force re-consent/i,
			});

			await expect(checkbox).toBeChecked();
			await expect(checkbox).toBeDisabled();

			await modal.getByRole('button', {name: 'OK'}).click();

			await waitForAlert(page);

			await expect(cookiesBanner).toBeVisible();
		});
	}
);

test(
	'Verify clicking Forced Re-Consent button retriggers the Cookies Banner',
	{tag: ['@LPD-86096', '@LPD-87281']},
	async ({consentManagerConfigurationPage, page}) => {
		const cookiesBanner = page.locator('div[role="region"].cookies-banner');

		await test.step('Initial state: Verify Cookies Banner is not visible after acceptance', async () => {
			await expect(cookiesBanner).not.toBeVisible();
		});

		await test.step('Click Forced Re-Consent and accept the confirmation dialog', async () => {
			page.once('dialog', async (dialogWindow) => {
				expect(dialogWindow.message()).toContain(
					'You are about to force the re-consent of consent preference cookies. By performing this action, every user will have to give their consent again. Are you sure you want to continue'
				);

				await dialogWindow.accept();
			});

			await consentManagerConfigurationPage.forcedReconsentButton.click();
		});

		await test.step('Verify success alert and reappearance of Cookies Banner', async () => {
			await expect(cookiesBanner).toBeVisible();
		});

		await test.step('Verify consent cookies were cleared', async () => {
			const cookies = await page.context().cookies();

			for (const cookieKey of optionalCookieKeys) {
				const cookie = cookies.find(
					(cookie) => cookie.name === cookieKey
				);

				expect(cookie).toBeUndefined();
			}
		});
	}
);

test(
	'Verify Consent Manager can be saved with Enabled set to false',
	{tag: '@LPD-78627'},
	async ({consentManagerConfigurationPage, page}) => {
		await test.step('Disable Consent Manager and save configuration', async () => {
			await updateConsentManagerConfiguration(page, {
				enabled: false,
			});

			await expect(
				consentManagerConfigurationPage.enabledCheckbox
			).not.toBeChecked();
		});
	}
);

test(
	'Verify Consent Renewal Period can be changed immediately after checking Enabled',
	{tag: '@LPD-79710'},
	async ({consentManagerConfigurationPage}) => {
		await consentManagerConfigurationPage.enabledCheckbox.setChecked(false);

		await expect(
			consentManagerConfigurationPage.consentRenewalPeriodInput
		).not.toBeEnabled();

		await consentManagerConfigurationPage.enabledCheckbox.setChecked(true);

		await expect(
			consentManagerConfigurationPage.consentRenewalPeriodInput
		).toBeEnabled();
	}
);

test(
	'Verify Consent Renewal Period correctly sets cookie expiration',
	{tag: '@LPD-68505'},
	async ({consentManagerConfigurationPage}) => {
		await validateConsentRenewalPeriodCookieExpiration(
			consentManagerConfigurationPage,
			false
		);
	}
);

test(
	'Verify Consent Renewal Period for Dissent correctly sets cookie expiration',
	{tag: '@LPD-80057'},
	async ({consentManagerConfigurationPage}) => {
		await validateConsentRenewalPeriodCookieExpiration(
			consentManagerConfigurationPage,
			true
		);
	}
);

test(
	'Verify Consent Renewal Period for Dissent correctly sets cookie expiration after 1 day',
	{tag: '@LPD-84142'},
	async ({consentManagerConfigurationPage}) => {
		await validateConsentRenewalPeriodCookieExpiration(
			consentManagerConfigurationPage,
			true,
			'1',
			'days'
		);
	}
);

test(
	'Verify Consent Renewal Period for Dissent correctly sets cookie expiration after 1 week',
	{tag: '@LPD-84142'},
	async ({consentManagerConfigurationPage}) => {
		await validateConsentRenewalPeriodCookieExpiration(
			consentManagerConfigurationPage,
			true,
			'1',
			'weeks'
		);
	}
);

test(
	'Verify updating Consent Renewal Period removes consent cookies',
	{tag: ['@LPD-68505', '@LPD-87281']},
	async ({page}) => {
		await test.step('Verify all consent cookies are set', async () => {
			const cookies = await page.context().cookies();

			for (const cookieKey of allCookieKeys) {
				const cookie = cookies.find(
					(cookie) => cookie.name === cookieKey
				);

				expect(cookie).toBeDefined();
			}
		});

		await test.step('Update Consent Renewal Period and expect cookies banner to appear', async () => {
			await updateConsentManagerConfiguration(page, {
				consentRenewalPeriod: '2',
			});

			await page
				.locator('div[role="region"].cookies-banner')
				.waitFor({state: 'visible'});
		});

		await test.step('Verify no consent cookies are set', async () => {
			await page.reload();

			await page.waitForLoadState();

			const cookies = await page.context().cookies();

			for (const cookieKey of allCookieKeys) {
				const cookie = cookies.find(
					(cookie) => cookie.name === cookieKey
				);

				expect(cookie).toBeUndefined();
			}
		});
	}
);

async function validateConsentRenewalPeriodCookieExpiration(
	consentManagerConfigurationPage: ConsentManagerConfigurationPage,
	dissent: boolean,
	newValue = '1',
	unit: 'days' | 'weeks' | 'months' = 'months'
) {
	const dateBeforeCookiesSet = new Date().getTime();

	await test.step(`Set Consent Renewal Period to ${newValue} ${unit}`, async () => {
		await validateConsentRenewalPeriodValue(
			consentManagerConfigurationPage,
			dissent,
			newValue,
			true,
			unit
		);
	});

	const cookies = await consentManagerConfigurationPage.page
		.context()
		.cookies();

	let userConsentConfiguredDate;

	await test.step('Verify USER_CONSENT_CONFIGURED_DATE cookie value is now', async () => {
		const cookie = cookies.find(
			(cookie) => cookie.name === 'USER_CONSENT_CONFIGURED_DATE'
		);

		expect(cookie).toBeDefined();

		userConsentConfiguredDate = Number(cookie.value);

		expect(userConsentConfiguredDate).toBeGreaterThanOrEqual(
			dateBeforeCookiesSet
		);
		expect(userConsentConfiguredDate).toBeLessThanOrEqual(
			new Date().getTime()
		);
	});

	await test.step(`Verify Consent Cookies expire in ${newValue} ${unit}`, async () => {
		const period = Number(newValue);
		const secondsInDay = 60 * 60 * 24;

		let expirationOffsetInSeconds = secondsInDay * 365 * (period / 12);

		if (unit === 'days') {
			expirationOffsetInSeconds = secondsInDay * period;
		}
		else if (unit === 'weeks') {
			expirationOffsetInSeconds = secondsInDay * 7 * period;
		}

		let expectedExpiration = Math.floor(
			userConsentConfiguredDate / 1000 + expirationOffsetInSeconds
		);

		if (dissent) {
			await validateCookiesExpiration(
				cookies,
				optionalCookieKeys,
				expectedExpiration
			);

			expectedExpiration = Math.floor(
				userConsentConfiguredDate / 1000 + secondsInDay * 365
			);

			await validateCookiesExpiration(
				cookies,
				requiredCookieKeys,
				expectedExpiration
			);
		}
		else {
			await validateCookiesExpiration(
				cookies,
				allCookieKeys,
				expectedExpiration
			);
		}
	});
}

async function validateConsentRenewalPeriodValue(
	consentManagerConfigurationPage: ConsentManagerConfigurationPage,
	dissent: boolean,
	newValue: string,
	saveSuccessful: boolean,
	unit: 'days' | 'weeks' | 'months' = 'months'
) {
	let consentRenewalPeriodField =
		consentManagerConfigurationPage.consentRenewalPeriodInput;

	let expectedValue = await consentRenewalPeriodField.getAttribute('value');

	if (dissent) {
		consentRenewalPeriodField =
			consentManagerConfigurationPage.consentRenewalPeriodForDissentInput;

		await consentManagerConfigurationPage.dissentRenewalPeriodUnitSelect.selectOption(
			unit
		);
	}

	await consentRenewalPeriodField.fill(newValue);

	await consentManagerConfigurationPage.updateButton.click();

	const page = consentManagerConfigurationPage.page;
	const modal = page.getByRole('alertdialog');

	let modalAppeared = false;

	try {
		await modal.waitFor({state: 'visible', timeout: 2000});

		modalAppeared = true;
	}
	catch {

		// HTML5 validation prevented submit, or value matched current and no
		// modal was triggered.

	}

	if (modalAppeared) {
		if (saveSuccessful) {
			await modal.getByRole('button', {name: 'OK'}).click();

			expectedValue = newValue;

			await waitForAlert(page);

			const cookiesBanner = page.locator('.cookies-banner');

			await cookiesBanner.waitFor({state: 'visible'});

			if (dissent) {
				await cookiesBanner
					.getByRole('button', {name: 'Decline All'})
					.click();
			}
			else {
				await cookiesBanner
					.getByRole('button', {name: 'Accept All'})
					.click();
			}
		}
		else {
			await modal.getByRole('button', {name: 'Cancel'}).click();

			await modal.waitFor({state: 'hidden'});

			await page.reload();

			await page.waitForLoadState();
		}
	}
	else {
		await page.reload();

		await page.waitForLoadState();
	}

	await expect(consentRenewalPeriodField).toHaveValue(expectedValue);
}

async function validateCookiesExpiration(
	cookies,
	cookieKeys,
	expectedExpiration
) {
	for (const cookieKey of cookieKeys) {
		const cookie = cookies.find((cookie) => cookie.name === cookieKey);

		expect(cookie).toBeDefined();

		// Normalize cookie.expires by removing millis

		const cookieExpiration = Number(cookie.expires.toFixed());

		// Expect expiration within +/- 1 second

		expect(cookieExpiration).toBeGreaterThanOrEqual(expectedExpiration - 1);
		expect(cookieExpiration).toBeLessThanOrEqual(expectedExpiration + 1);
	}
}
