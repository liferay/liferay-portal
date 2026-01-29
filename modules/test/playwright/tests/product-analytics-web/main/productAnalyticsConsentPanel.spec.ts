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
import {ApiHelpers} from '../../../helpers/ApiHelpers';
import {AccountSettingsPage} from '../../../pages/users-admin-web/AccountSettingsPage';
import {clickAndExpectToBeVisible} from '../../../utils/clickAndExpectToBeVisible';
import performLogin, {userData} from '../../../utils/performLogin';
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
		await systemSettingsPage.goToSystemSetting('Privacy', 'Cookie Manager');

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
		await systemSettingsPage.goToSystemSetting('Privacy', 'Cookie Manager');

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
	'Data and Privacy tab is visible',
	{tag: '@LPD-72749'},
	async ({accountSettingsPage}) => {
		await accountSettingsPage.goToAccountSettings();

		const dataAndPrivacyTab = await accountSettingsPage.page.locator(
			'.nav-link',
			{
				hasText: 'Data And Privacy',
			}
		);

		await expect(await dataAndPrivacyTab).toBeVisible();
	}
);

test(
	'Verify back button is showing on the top left of the Data and Privacy screen',
	{tag: '@LPD-73820'},
	async ({page, usersAndOrganizationsPage}) => {
		await usersAndOrganizationsPage.goToUsers();

		await usersAndOrganizationsPage.goToUser('Test Test');

		await usersAndOrganizationsPage.page
			.locator('.nav-link', {
				hasText: 'Data And Privacy',
			})
			.click();

		await clickAndExpectToBeVisible({
			target: page.getByText('Edit User Test Test', {
				exact: true,
			}),
			trigger: usersAndOrganizationsPage.page.locator('.nav-link', {
				hasText: 'Data And Privacy',
			}),
		});
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
		const enabledButton = await page.getByLabel('Enabled');

		const isChecked = await enabledButton.isChecked();

		if (isChecked) {
			await expect(consentRenewalPeriod).toBeEditable();

			await enabledButton.click();

			if (
				await systemSettingsPage.page
					.getByRole('button', {name: 'Update'})
					.isVisible()
			) {
				await systemSettingsPage.page
					.getByRole('button', {name: 'Update'})
					.click();
			}
			else {
				await systemSettingsPage.page
					.getByRole('button', {name: 'Save'})
					.click();
			}

			await expect(consentRenewalPeriod).not.toBeEditable();
		}
		else {
			await expect(consentRenewalPeriod).not.toBeEditable();

			await enabledButton.click();

			if (
				await systemSettingsPage.page
					.getByRole('button', {name: 'Update'})
					.isVisible()
			) {
				await systemSettingsPage.page
					.getByRole('button', {name: 'Update'})
					.click();
			}
			else {
				await systemSettingsPage.page
					.getByRole('button', {name: 'Save'})
					.click();
			}

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
	'Verify Product Analytics Consent Panel buttons and order from Account Settings',
	{tag: '@LPD-67119'},
	async ({
		accountSettingsPage,
		productAnalyticsBannerPage,
		productAnalyticsConsentPanelPage,
	}) => {
		await productAnalyticsBannerPage.acceptAllButton.click();

		await accountSettingsPage.goToDataAndPrivacy();

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

	await accountSettingsPage.goToAccountSettings();

	const dataAndPrivacyTab = await accountSettingsPage.page.locator(
		'.nav-link',
		{
			hasText: 'Data And Privacy',
		}
	);

	if (isVisible) {
		await expect(await dataAndPrivacyTab).toBeVisible();
	}
	else {
		await expect(await dataAndPrivacyTab).not.toBeVisible();
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
