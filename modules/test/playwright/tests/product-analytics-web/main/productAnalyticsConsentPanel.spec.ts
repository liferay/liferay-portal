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
import {ApiHelpers} from '../../../helpers/ApiHelpers';
import {AccountSettingsPage} from '../../../pages/users-admin-web/AccountSettingsPage';
import performLogin, {userData} from '../../../utils/performLogin';
import {
	clearProductAnalyticsCookies,
	expectAllCookiesAccepted,
	expectAllCookiesDeclined,
} from './utils/cookies';

export const test = mergeTests(
	accountSettingsPagesTest,
	featureFlagsTest({
		'LPD-51356': {enabled: true},
	}),
	instanceSettingsPagesTest,
	loginTest(),
	productAnalyticsPagesTest,
	siteSettingsPagesTest,
	systemSettingsPageTest
);

export enum OptionalProductAnalyticsCookieTypes {
	Functional = 'PRODUCT_ANALYTICS_CONSENT_TYPE_FUNCTIONAL',
	Performance = 'PRODUCT_ANALYTICS_CONSENT_TYPE_PERFORMANCE',
	Personalization = 'PRODUCT_ANALYTICS_CONSENT_TYPE_PERSONALIZATION',
	ProductAnalytics = 'PRODUCT_ANALYTICS_CONSENT_TYPE_PRODUCT_ANALYTICS',
}

export type ProductAnalyticsCookie = {
	name: string;
	value: boolean;
};

export enum RequiredProductAnalyticsCookieTypes {
	Necessary = 'PRODUCT_ANALYTICS_CONSENT_TYPE_NECESSARY',
}

test.afterEach(async ({page}) => {
	await test.step('Clear Product Analytics cookies if present', async () => {
		await clearProductAnalyticsCookies(page);
	});
});

test(
	'Verify Product Analytics Consent Panel buttons and order from Account Settings',
	{tag: '@LPD-67119'},
	async ({
		accountSettingsPage,
		productAnalyticsBannerPage,
		productAnalyticsConsentPanelPage,
	}) => {
		await productAnalyticsBannerPage.acceptAllButton.click();

		await test.step('Go to Product Analytics Account Settings', async () => {
			await accountSettingsPage.goToDataAndPrivacy();

			await accountSettingsPage.productAnalyticsMenuItem.waitFor();

			await accountSettingsPage.productAnalyticsMenuItem.click();
		});

		await test.step('Verify Customize button displays Consent Panel', async () => {
			await expectProductAnalyticsConsentPanelButtons(
				await productAnalyticsConsentPanelPage.consentPanelFormLocator
			);
		});
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

test(
	'Verify Product Analytics User Configuration from Account Settings',
	{tag: '@LPD-60007'},
	async ({
		accountSettingsPage,
		page,
		productAnalyticsBannerPage,
		productAnalyticsConsentPanelPage,
	}) => {
		await productAnalyticsBannerPage.acceptAllButton.click();

		await test.step('AC3: Verify Product Analytics Account Settings', async () => {
			await accountSettingsPage.goToDataAndPrivacy();

			await accountSettingsPage.productAnalyticsMenuItem.waitFor();

			await accountSettingsPage.productAnalyticsMenuItem.click();

			await productAnalyticsConsentPanelPage.consentPanelFormLocator.waitFor();
		});

		await test.step('Verify all cookie types are present and accepted', async () => {
			await expectAllCookiesAccepted(page);

			for (const optionalProductAnalyticsCookieType of Object.values(
				OptionalProductAnalyticsCookieTypes
			)) {
				const toggle =
					await productAnalyticsConsentPanelPage.getCookieTypeToggle(
						optionalProductAnalyticsCookieType,
						false
					);

				await expect(await toggle).toBeChecked();
			}
		});

		await test.step('After clearing Product Analytics cookies, verify PA banner does not display on PA configuration page', async () => {
			await clearProductAnalyticsCookies(page);

			await accountSettingsPage.page.reload();

			await accountSettingsPage.page.waitForTimeout(2000);

			await expect(
				await productAnalyticsBannerPage.bannerLocator
			).not.toBeVisible();
		});

		await test.step('Verify PA banner does display on other pages', async () => {
			await page.goto('/');

			await expect(
				await productAnalyticsBannerPage.bannerLocator
			).toBeVisible();
		});
	}
);

test(
	'Verify Product Analytics User Configuration only appears for admin users',
	{tag: '@LPD-60007'},
	async ({browser, page}) => {
		await test.step('Verify Product Analytics User Configuration does not appear for non-admin user', async () => {
			await expectProductAnalyticsAccountSettingsVisibility(
				browser,
				false,
				'demo.unprivileged'
			);
		});

		await test.step('Verify Product Analytics User Configuration appears for company admin user', async () => {
			await expectProductAnalyticsAccountSettingsVisibility(
				browser,
				true,
				'demo.company.admin'
			);
		});

		await test.step('Verify Product Analytics User Configuration appears for site admin user', async () => {
			const apiHelpers = new ApiHelpers(page);

			const user = await apiHelpers.headlessAdminUser.postUserAccount();

			userData[user.alternateName] = {
				name: user.givenName,
				password: 'test',
				surname: user.familyName,
			};

			const site =
				await apiHelpers.headlessAdminUser.getSiteByFriendlyUrlPath(
					'guest'
				);

			const siteAdminRole =
				await apiHelpers.headlessAdminUser.getRoleByName(
					'Site Administrator'
				);

			await apiHelpers.headlessAdminUser.assignUserToSite(
				siteAdminRole.id,
				site.id,
				user.id
			);

			await expectProductAnalyticsAccountSettingsVisibility(
				browser,
				true,
				user.alternateName
			);
		});
	}
);

async function expectProductAnalyticsAccountSettingsVisibility(
	browser,
	isVisible: boolean,
	screenName: string
) {
	const newPage = await browser.newPage();

	await performLogin(newPage, screenName);

	const accountSettingsPage = new AccountSettingsPage(newPage);

	await accountSettingsPage.goToDataAndPrivacy();

	await accountSettingsPage.page.waitForLoadState();

	if (isVisible) {
		await expect(
			await accountSettingsPage.productAnalyticsMenuItem
		).toBeVisible();
	}
	else {
		await expect(
			await accountSettingsPage.productAnalyticsMenuItem
		).not.toBeVisible();
	}
}

async function expectProductAnalyticsConsentPanelButtons(locator: Locator) {
	await locator.waitFor();

	const buttons = await locator.getByRole('button').all();

	expect(buttons).toHaveLength(3);
	await expect(await buttons[0]).toContainText('Use Necessary Cookies Only');
	await expect(await buttons[1]).toContainText('Accept Selected');
	await expect(await buttons[2]).toContainText('Accept All');
}
