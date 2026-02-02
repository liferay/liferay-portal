/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {featureFlagsTest} from '../../../fixtures/featureFlagsTest';
import {loginTest} from '../../../fixtures/loginTest';
import {productAnalyticsPagesTest} from '../../../fixtures/productAnalyticsPagesTest';
import {systemSettingsPageTest} from '../../../fixtures/systemSettingsPageTest';
import {clickAndExpectToBeVisible} from '../../../utils/clickAndExpectToBeVisible';
import {waitForAlert} from '../../../utils/waitForAlert';
import {clearProductAnalyticsCookies} from './utils/cookies';

const cookieKeys = [
	'PRODUCT_ANALYTICS_CONSENT_TYPE_FUNCTIONAL',
	'PRODUCT_ANALYTICS_CONSENT_TYPE_NECESSARY',
	'PRODUCT_ANALYTICS_CONSENT_TYPE_PERFORMANCE',
	'PRODUCT_ANALYTICS_CONSENT_TYPE_PERSONALIZATION',
	'PRODUCT_ANALYTICS_CONFIGURED',
	'PRODUCT_ANALYTICS_CONFIGURED_DATE',
];

export const test = mergeTests(
	featureFlagsTest({
		'LPD-51356': {enabled: true},
	}),
	loginTest(),
	productAnalyticsPagesTest,
	systemSettingsPageTest
);

test.afterEach(async ({page, systemSettingsPage}) => {
	await systemSettingsPage.goToSystemSetting('Privacy', 'Product Analytics');

	await page.getByRole('heading', {name: 'Product Analytics'}).waitFor();

	if (
		await systemSettingsPage.page
			.getByRole('button', {name: 'Actions'})
			.isVisible()
	) {
		page.once('dialog', async (dialogWindow) => {
			await dialogWindow.accept();
		});

		await clickAndExpectToBeVisible({
			autoClick: true,
			target: systemSettingsPage.page.getByRole('menuitem', {
				name: 'Reset Default Values',
			}),
			trigger: systemSettingsPage.page.getByRole('button', {
				name: 'Actions',
			}),
		});
	}

	await test.step('Clear Product Analytics cookies if present', async () => {
		await clearProductAnalyticsCookies(page);
	});
});

test.beforeEach(
	async ({page, productAnalyticsBannerPage, systemSettingsPage}) => {
		const productAnalyticsHeading = await page.getByRole('heading', {
			name: 'Product Analytics',
		});

		await test.step('Verify Product Analytics Instance Level Configuration', async () => {
			await systemSettingsPage.goToSystemSetting(
				'Privacy',
				'Product Analytics'
			);

			await productAnalyticsHeading.waitFor();

			const enabledButton = await page.getByLabel('Enabled');

			await enabledButton.setChecked(true);

			if (await page.getByRole('button', {name: 'Save'}).isVisible()) {
				await page
					.getByRole('button', {name: 'Save'})
					.dispatchEvent('click');
			}
			else {
				await page
					.getByRole('button', {name: 'Update'})
					.dispatchEvent('click');
			}

			await page.waitForTimeout(1000);
		});

		await test.step('Verify Product Analytics Banner appears, then Accept All cookies', async () => {
			await expect(
				await productAnalyticsBannerPage.bannerLocator
			).toBeVisible();

			await productAnalyticsBannerPage.acceptAllButton.click();
		});

		await test.step('Go to Product Analytics System Settings Configuration', async () => {
			await systemSettingsPage.goToSystemSetting(
				'Privacy',
				'Product Analytics'
			);

			await systemSettingsPage.page.waitForLoadState();
		});
	}
);

test(
	'Consent Renewal Period configuration field validation',
	{tag: '@LPD-68504'},
	async ({page}) => {
		const consentRenewalPeriodField = await page.getByLabel(
			'Consent Renewal Period'
		);

		await test.step('Validate Consent Renewal Period field', async () => {
			await test.step('Validate default value of 12 months', async () => {
				await expect(await consentRenewalPeriodField).toHaveValue('12');
			});

			await test.step('Validate value cannot be less than 1', async () => {
				await validateConsentRenewalPeriodValue('0', page, false);
			});

			await test.step('Validate value cannot be more than 12', async () => {
				await validateConsentRenewalPeriodValue('13', page, false);
			});

			await test.step('Validate value cannot be null', async () => {
				await validateConsentRenewalPeriodValue('', page, false);
			});

			await test.step('Validate value must be a number', async () => {
				await expect(await consentRenewalPeriodField).toHaveAttribute(
					'type',
					'number'
				);
			});
		});

		await test.step('Verify alert appears if changing the value', async () => {
			page.once('dialog', async (dialogWindow) => {
				await expect(dialogWindow.message()).toContain(
					'You are about to change the consent renewal period'
				);

				await dialogWindow.dismiss();
			});
		});

		await test.step('Verify dismissing the dialog does not change configuration value', async () => {
			await validateConsentRenewalPeriodValue('1', page, false);
		});

		await test.step('Verify accepting dialog updates configuration value and Product Analytics Banner appears again', async () => {
			page.once('dialog', async (dialogWindow) => {
				await dialogWindow.accept();
			});
			await validateConsentRenewalPeriodValue('1', page, true);
		});

		await test.step('Verify alert appears if resetting the configuration', async () => {
			page.once('dialog', async (dialogWindow) => {
				await expect(dialogWindow.message()).toContain(
					'You are about to change the consent renewal period'
				);

				await dialogWindow.dismiss();
			});
		});

		await test.step('Verify dismissing the dialog does not change configuration value', async () => {
			await clickAndExpectToBeVisible({
				autoClick: true,
				target: page.getByRole('menuitem', {
					name: 'Reset Default Values',
				}),
				trigger: page.getByRole('button', {
					name: 'Actions',
				}),
			});
		});

		await test.step('Verify that resetting the configuration restores the default values', async () => {
			await expect(await consentRenewalPeriodField).toHaveValue('12');
			await expect(await page.getByLabel('Enabled')).not.toBeChecked();
			await expect(
				await page.getByRole('menuitem', {
					name: 'Reset Default Values',
				})
			).not.toBeVisible();
		});
	}
);

test(
	'Verify Consent Renewal Period correctly sets cookie expiration',
	{tag: '@LPD-68504'},
	async ({page}) => {
		const dateBeforeCookiesSet = new Date().getTime();

		await test.step('Set Consent Renewal Period to 1 month', async () => {
			page.once('dialog', async (dialogWindow) => {
				await dialogWindow.accept();
			});
			await validateConsentRenewalPeriodValue('1', page, true);
		});

		const cookies = await page.context().cookies();

		let productAnalyticsConfiguredDate;

		await test.step('Verify PRODUCT_ANALYTICS_CONFIGURED_DATE cookie value is now', async () => {
			const cookie = await cookies.find(
				(cookie) => cookie.name === 'PRODUCT_ANALYTICS_CONFIGURED_DATE'
			);

			await expect(cookie).toBeDefined();

			productAnalyticsConfiguredDate = Number(cookie.value);

			// Expect configured date within +/- 1 second

			await expect(productAnalyticsConfiguredDate).toBeGreaterThanOrEqual(
				dateBeforeCookiesSet - 1000
			);
			await expect(productAnalyticsConfiguredDate).toBeLessThanOrEqual(
				new Date().getTime() + 1000
			);
		});

		await test.step('Verify Consent Cookies expire in 1 month', async () => {
			const oneMonthFromNowInSeconds =
				productAnalyticsConfiguredDate / 1000 +
				60 * 60 * 24 * 365 * (1 / 12);

			for (const cookieKey of cookieKeys) {
				const cookie = await cookies.find(
					(cookie) => cookie.name === cookieKey
				);

				await expect(cookie).toBeDefined();

				// Normalize cookie.expires by removing millis

				const cookieExpiration = Number(cookie.expires.toFixed());

				// Expect expiration within +/- 3 seconds

				await expect(cookieExpiration).toBeGreaterThanOrEqual(
					oneMonthFromNowInSeconds - 3
				);
				await expect(cookieExpiration).toBeLessThanOrEqual(
					oneMonthFromNowInSeconds + 3
				);
			}
		});
	}
);

test(
	'Verify updating Consent Renewal Period removes consent cookies',
	{tag: '@LPD-68504'},
	async ({page, productAnalyticsBannerPage}) => {
		await test.step('Verify all consent cookies are set', async () => {
			const cookies = await page.context().cookies();

			for (const cookieKey of cookieKeys) {
				const cookie = await cookies.find(
					(cookie) => cookie.name === cookieKey
				);

				await expect(cookie).toBeDefined();
			}
		});

		await test.step('Update Consent Renewal Period and expect cookies banner to appear', async () => {
			page.once('dialog', async (dialogWindow) => {
				await dialogWindow.accept();
			});

			await page.getByLabel('Consent Renewal Period').fill('2');

			if (await page.getByRole('button', {name: 'Save'}).isVisible()) {
				await page.getByRole('button', {name: 'Save'}).click();
			}
			else {
				await page.getByRole('button', {name: 'Update'}).click();
			}

			await waitForAlert(page);

			await expect(
				await productAnalyticsBannerPage.bannerLocator
			).toBeVisible();
		});

		await test.step('Verify no consent cookies are set', async () => {
			await page.reload();

			await page.waitForLoadState();

			const cookies = await page.context().cookies();

			for (const cookieKey of cookieKeys) {
				const cookie = await cookies.find(
					(cookie) => cookie.name === cookieKey
				);

				await expect(cookie).toBeUndefined();
			}
		});
	}
);

async function validateConsentRenewalPeriodValue(
	newValue: string,
	page,
	saveSuccessful: boolean
) {
	const consentRenewalPeriodField = await page.getByLabel(
		'Consent Renewal Period'
	);

	let expectedValue = await consentRenewalPeriodField.getAttribute('value');

	await consentRenewalPeriodField.fill(newValue);

	if (await page.getByRole('button', {name: 'Save'}).isVisible()) {
		await page.getByRole('button', {name: 'Save'}).click();
	}
	else {
		await page.getByRole('button', {name: 'Update'}).click();
	}

	if (saveSuccessful) {
		expectedValue = newValue;

		await waitForAlert(page);

		await expect(
			await page.locator(
				'#p_p_id_com_liferay_product_analytics_web_portlet_ProductAnalyticsBannerPortlet_'
			)
		).toBeVisible();

		await page.getByRole('button', {name: 'Accept All'}).click();
	}
	else {
		await page.reload();

		await page.waitForLoadState();
	}

	await expect(await consentRenewalPeriodField).toHaveValue(expectedValue);
}
