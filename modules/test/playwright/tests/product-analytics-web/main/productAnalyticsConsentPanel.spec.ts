/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Locator, expect, mergeTests} from '@playwright/test';

import {accountSettingsPagesTest} from '../../../fixtures/accountSettingsPagesTest';
import {featureFlagsTest} from '../../../fixtures/featureFlagsTest';
import {instanceSettingsPagesTest} from '../../../fixtures/instanceSettingsPagesTest';
import {loginTest} from '../../../fixtures/loginTest';
import {productAnalyticsPagesTest} from '../../../fixtures/productAnalyticsPagesTest';
import {siteSettingsPagesTest} from '../../../fixtures/siteSettingsPagesTest';
import {systemSettingsPageTest} from '../../../fixtures/systemSettingsPageTest';
import {usersAndOrganizationsPagesTest} from '../../../fixtures/usersAndOrganizationsPagesTest';
import {clickAndExpectToBeVisible} from '../../../utils/clickAndExpectToBeVisible';
import {
	OptionalProductAnalyticsCookieTypes,
	clearProductAnalyticsCookies,
	expectAllCookiesAccepted,
	expectAllCookiesDeclined,
} from './utils/cookies';

export const disabledTest = mergeTests(
	accountSettingsPagesTest,
	featureFlagsTest({
		'LPD-51356': {enabled: false},
	}),
	loginTest(),
	systemSettingsPageTest
);

export const test = mergeTests(
	accountSettingsPagesTest,
	featureFlagsTest({
		'LPD-51356': {enabled: true},
	}),
	instanceSettingsPagesTest,
	loginTest(),
	productAnalyticsPagesTest,
	siteSettingsPagesTest,
	systemSettingsPageTest,
	usersAndOrganizationsPagesTest
);

test.afterEach(async ({page, systemSettingsPage}) => {
	const productAnalyticsHeading = await page.getByRole('heading', {
		name: 'Product Analytics',
	});

	await test.step('Reset Product Analytics System Settings if needed', async () => {
		await systemSettingsPage.goToSystemSetting(
			'Privacy',
			'Consent Manager'
		);

		if (!(await page.getByText('Product Analytics').isVisible())) {
			return;
		}

		await systemSettingsPage.goToSystemSetting(
			'Privacy',
			'Product Analytics'
		);

		await productAnalyticsHeading.waitFor();

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
	});

	await test.step('Clear Product Analytics cookies if present', async () => {
		await clearProductAnalyticsCookies(page);
	});
});

test.beforeEach(async ({page, systemSettingsPage}) => {
	const productAnalyticsHeading = await page.getByRole('heading', {
		name: 'Product Analytics',
	});

	await test.step('Verify Product Analytics Instance Level Configuration', async () => {
		await systemSettingsPage.goToSystemSetting(
			'Privacy',
			'Consent Manager'
		);

		if (!(await page.getByText('Product Analytics').isVisible())) {
			return;
		}

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
});

disabledTest(
	'Data and Privacy tab is not visible',
	{tag: '@LPD-72749'},
	async ({accountSettingsPage}) => {
		await accountSettingsPage.goToAccountSettings();

		const dataAndPrivacyTab = await accountSettingsPage.page.locator(
			'.nav-link',
			{
				hasText: 'Data And Privacy',
			}
		);

		await expect(await dataAndPrivacyTab).not.toBeVisible();
	}
);

test(
	'Escape the alert parameters in Product Analytics Consent Panel to avoid XSS injections',
	{tag: '@LPD-106198'},
	async ({page}) => {
		page.on('dialog', async (dialog) => {
			if (dialog.type() === 'alert') {
				throw new Error('XSS detected');
			}
		});

		const alertDisplayType = 'info" onmouseover="alert(1)';
		const alertMessage = '<img src=x onerror=alert(1)>';
		const portletId =
			'com_liferay_product_analytics_web_portlet_ProductAnalyticsConsentPanelPortlet';

		const params = new URLSearchParams({
			[`_${portletId}_alertDisplayType`]: alertDisplayType,
			[`_${portletId}_alertMessage`]: alertMessage,
			p_p_id: portletId,
			p_p_lifecycle: '0',
			p_p_state: 'maximized',
		});

		await page.goto(`/group/control_panel/manage?${params.toString()}`);

		const alert = page
			.locator(`#_${portletId}_productAnalyticsConsentPanelForm`)
			.getByRole('alert');

		await expect(alert).not.toHaveAttribute('onmouseover');
		await expect(alert).toContainText(alertMessage);
	}
);

test(
	'Verify Consent Renewal Period field is only enabled when Product Analytics is enabled',
	{tag: '@LPD-74662'},
	async ({page, systemSettingsPage}) => {
		await systemSettingsPage.goToSystemSetting(
			'Privacy',
			'Product Analytics'
		);

		const consentRenewalPeriod = await page.getByLabel(
			'Consent Renewal Period'
		);
		const enabledButton = page.getByLabel('Enabled');

		await enabledButton.waitFor({state: 'visible'});

		const isChecked = await enabledButton.isChecked();

		if (isChecked) {
			await expect(consentRenewalPeriod).toBeEditable();

			await enabledButton.click();

			await expect(consentRenewalPeriod).not.toBeEditable();
		}
		else {
			await expect(consentRenewalPeriod).not.toBeEditable();

			await enabledButton.click();

			await expect(consentRenewalPeriod).toBeEditable();
		}
	}
);

test(
	'Verify Enable button is above Consent Renewal Period field',
	{tag: '@LPD-74662'},
	async ({page, systemSettingsPage}) => {
		await systemSettingsPage.goToSystemSetting(
			'Privacy',
			'Product Analytics'
		);

		const consentRenewalPeriod = await page
			.getByText('Consent Renewal Period')
			.boundingBox();
		const enabledButton = await page.getByLabel('Enabled').boundingBox();

		await expect(enabledButton.y).toBeLessThan(consentRenewalPeriod.y);
	}
);

test(
	'Verify Product Analytics Consent Panel buttons and order from Product Analytics Banner',
	{tag: '@LPD-67119'},
	async ({productAnalyticsBannerPage, productAnalyticsConsentPanelPage}) => {
		await test.step('Verify Customize button displays Consent Panel', async () => {
			await productAnalyticsBannerPage.customizeButton.click();

			await productAnalyticsConsentPanelPage.useNecessaryCookiesOnlyButton.waitFor();

			const productAnalyticsConsentPanelFooter =
				await productAnalyticsConsentPanelPage.page.locator(
					'[class="modal-footer"]'
				);

			await expectProductAnalyticsConsentPanelButtons(
				productAnalyticsConsentPanelFooter
			);
		});
	}
);

test(
	'Verify Product Analytics Consent Panel is present after clicking the Customize button on the Product Analytics Banner',
	{tag: '@LPD-60006'},
	async ({page, productAnalyticsBannerPage}) => {
		await test.step('AC1: Verify Customize button displays Consent Panel', async () => {
			await productAnalyticsBannerPage.customizeButton.click();

			await expect(
				await page.getByRole('heading', {
					name: 'Liferay Platform Consent Preferences',
				})
			).toBeVisible();
		});
	}
);

test(
	'Verify Product Analytics User Configuration is not available from Account Settings',
	{tag: '@LPD-80264'},
	async ({accountSettingsPage, productAnalyticsBannerPage}) => {
		await productAnalyticsBannerPage.acceptAllButton.click();

		await accountSettingsPage.page
			.getByText('Consent Manager')
			.first()
			.waitFor();

		await expect(
			accountSettingsPage.page.locator(
				'[id="_com_liferay_my_account_web_portlet_MyAccountPortlet_productAnalyticsConsentPanelForm"]'
			)
		).not.toBeVisible();
	}
);

test(
	'Verify specific Consent types can be configured from the Consent Panel',
	{tag: '@LPD-60006'},
	async ({
		page,
		productAnalyticsBannerPage,
		productAnalyticsConsentPanelPage,
	}) => {
		await productAnalyticsBannerPage.customizeButton.click();

		await test.step('AC2: Verify Consent Panel displays all cookie types', async () => {
			for (const optionalProductAnalyticsCookieType of Object.values(
				OptionalProductAnalyticsCookieTypes
			)) {
				await expect(
					await productAnalyticsConsentPanelPage.getCookieTypeToggle(
						optionalProductAnalyticsCookieType
					)
				).toBeVisible();
			}
		});

		await test.step('AC3: Verify "Accept Selected" sets toggled cookies', async () => {
			for (const optionalProductAnalyticsCookieType of Object.values(
				OptionalProductAnalyticsCookieTypes
			)) {
				const toggle =
					await productAnalyticsConsentPanelPage.getCookieTypeToggle(
						optionalProductAnalyticsCookieType
					);

				await toggle.check();
			}

			await productAnalyticsConsentPanelPage.acceptSelectedButton.click();

			await expectAllCookiesAccepted(page);

			await clearProductAnalyticsCookies(page);
		});

		await test.step('AC3: Verify "Accept Selected" does not set untoggled cookies', async () => {
			await productAnalyticsBannerPage.customizeButton.click();

			for (const optionalProductAnalyticsCookieType of Object.values(
				OptionalProductAnalyticsCookieTypes
			)) {
				const toggle =
					await productAnalyticsConsentPanelPage.getCookieTypeToggle(
						optionalProductAnalyticsCookieType
					);

				await toggle.uncheck();
			}

			await productAnalyticsConsentPanelPage.acceptSelectedButton.click();

			await expectAllCookiesDeclined(page);

			await clearProductAnalyticsCookies(page);
		});

		await test.step('AC4: Verify "Accept All" sets all cookie types', async () => {
			await productAnalyticsBannerPage.customizeButton.click();

			await productAnalyticsConsentPanelPage.acceptAllButton.click();

			await expectAllCookiesAccepted(page);

			await clearProductAnalyticsCookies(page);
		});

		await test.step('AC5: Verify "Use Necessary Cookies Only" disables all cookie types except necessary cookies', async () => {
			await productAnalyticsBannerPage.customizeButton.click();

			await productAnalyticsConsentPanelPage.useNecessaryCookiesOnlyButton.click();

			await expectAllCookiesDeclined(page);
		});
	}
);

async function expectProductAnalyticsConsentPanelButtons(locator: Locator) {
	await locator.waitFor();

	const buttons = await locator.getByRole('button').all();

	expect(buttons).toHaveLength(3);
	await expect(await buttons[0]).toContainText('Use Necessary Cookies Only');
	await expect(await buttons[1]).toContainText('Accept Selected');
	await expect(await buttons[2]).toContainText('Accept All');
}
