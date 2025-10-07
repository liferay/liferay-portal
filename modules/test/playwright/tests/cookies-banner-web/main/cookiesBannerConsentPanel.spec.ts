/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Locator, expect, mergeTests} from '@playwright/test';

import {accountSettingsPagesTest} from '../../../fixtures/accountSettingsPagesTest';
import {featureFlagsTest} from '../../../fixtures/featureFlagsTest';
import {loginTest} from '../../../fixtures/loginTest';
import {systemSettingsPageTest} from '../../../fixtures/systemSettingsPageTest';
import {clickAndExpectToBeVisible} from '../../../utils/clickAndExpectToBeVisible';
import {waitForAlert} from '../../../utils/waitForAlert';

const cookieHeadingNames = [
	'Functional Cookies',
	'Performance Cookies',
	'Personalization Cookies',
];

const cookieKeys = [
	'CONSENT_TYPE_FUNCTIONAL',
	'CONSENT_TYPE_PERFORMANCE',
	'CONSENT_TYPE_PERSONALIZATION',
];

export const test = mergeTests(
	accountSettingsPagesTest,
	featureFlagsTest({
		'LPD-51356': {enabled: true},
	}),
	loginTest(),
	systemSettingsPageTest
);

test.afterEach(async ({systemSettingsPage}) => {
	await systemSettingsPage.goToSystemSetting('Privacy', 'Cookie Manager');

	const menuItems = await systemSettingsPage.page.getByRole('menuitem').all();

	await test.step('In reverse order, reset each configuration if previously set. We use reverse order since the latter entries will be hidden if the first "Preference Handling" entry is reset.', async () => {
		for (const menuItem of menuItems.reverse()) {
			if (await menuItem.getByText('Product Analytics').isVisible()) {
				continue;
			}

			await menuItem.click();

			await systemSettingsPage.page.waitForTimeout(1000);

			await systemSettingsPage.page.waitForLoadState();

			if (
				await systemSettingsPage.page
					.getByRole('button', {name: 'Actions'})
					.isVisible()
			) {
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
		}
	});

	await test.step('Clear Consent Cookies if present', async () => {
		await systemSettingsPage.page
			.context()
			.clearCookies({name: /^CONSENT_TYPE_/});
		await systemSettingsPage.page
			.context()
			.clearCookies({name: 'USER_CONSENT_CONFIGURED'});
	});
});

test.beforeEach(async ({systemSettingsPage}) => {
	await test.step('Enable Preference Handling Cookies', async () => {
		await systemSettingsPage.goToSystemSetting('Privacy', 'Cookie Manager');

		const enabledButton = systemSettingsPage.page.getByLabel('Enabled');

		await enabledButton.waitFor({state: 'visible'});

		await systemSettingsPage.page.waitForTimeout(3000);

		const isChecked = await enabledButton.isChecked();

		if (!isChecked) {
			await enabledButton.click();

			await systemSettingsPage.page
				.getByRole('button', {name: 'Save'})
				.click();

			await waitForAlert(systemSettingsPage.page);
		}

		await expect(enabledButton).toBeChecked();
	});
});

test(
	'Verify Cookie Banner Consent Panel buttons',
	{tag: '@LPD-67119'},
	async ({page}) => {
		await test.step('Open the Cookie Banner Consent Panel', async () => {
			await page.goto('/');

			const cookiesBanner = await page.locator(
				'#p_p_id_com_liferay_cookies_banner_web_portlet_CookiesBannerPortlet_'
			);

			await cookiesBanner.waitFor();

			await cookiesBanner
				.getByRole('button', {name: 'Configuration'})
				.click();
		});

		await test.step('Verify all button names and ordering', async () => {
			const consentPanelFooter = await page.locator(
				'[class="modal-footer"]'
			);

			await expectCookieConsentPanelButtons(await consentPanelFooter);
		});
	}
);

test(
	'Verify Cookie Manager buttons',
	{tag: '@LPD-67119'},
	async ({accountSettingsPage}) => {
		await test.step('Go to Cookie Manager Account Settings page', async () => {
			await accountSettingsPage.goToDataAndPrivacy();

			await accountSettingsPage.page
				.getByText('Cookie Manager')
				.first()
				.waitFor();

			if (await accountSettingsPage.cookieManagerMenuItem.isVisible()) {
				await accountSettingsPage.cookieManagerMenuItem.click();
			}
		});

		await test.step('Verify all button names and ordering', async () => {
			const cookiesManagerConsentPanel =
				await accountSettingsPage.page.locator(
					'[id="_com_liferay_my_account_web_portlet_MyAccountPortlet_cookiesBannerConfigurationForm"]'
				);

			await expectCookieConsentPanelButtons(
				await cookiesManagerConsentPanel
			);
		});
	}
);

test(
	'Verify Cookie Manager can be accessed from the Data And Privacy Account Settings tab',
	{tag: '@LPD-60007'},
	async ({accountSettingsPage, page}) => {
		await test.step('AC1: Verify Data And Privacy tab exists within Account Settings', async () => {
			await accountSettingsPage.goToAccountSettings();

			const dataAndPrivacyTab = await accountSettingsPage.page.locator(
				'.nav-link',
				{
					hasText: 'Data And Privacy',
				}
			);

			await expect(await dataAndPrivacyTab).toBeVisible();
		});

		await test.step('AC2: Verify Cookie Manager panel is visible from Data and Privacy tab', async () => {
			await accountSettingsPage.goToDataAndPrivacy();

			await accountSettingsPage.page
				.getByText('Cookie Manager')
				.first()
				.waitFor();

			if (await accountSettingsPage.cookieManagerMenuItem.isVisible()) {
				await accountSettingsPage.cookieManagerMenuItem.click();
			}

			for (const cookieHeadingName of cookieHeadingNames) {
				const cookieHeading = await page.getByRole('heading', {
					name: cookieHeadingName,
				});

				await expect(await cookieHeading).toBeVisible();
			}
		});
	}
);

test(
	'Verify Cookie Preferences can be saved from the new Cookie Manager page',
	{tag: '@LPD-60007'},
	async ({accountSettingsPage, page}) => {
		await test.step('Enable all cookie types', async () => {
			await accountSettingsPage.goToDataAndPrivacy();

			await accountSettingsPage.page
				.getByText('Cookie Manager')
				.first()
				.waitFor();

			if (await accountSettingsPage.cookieManagerMenuItem.isVisible()) {
				await accountSettingsPage.cookieManagerMenuItem.click();
			}

			await accountSettingsPage.page
				.getByRole('button', {name: 'Accept All'})
				.click();

			await accountSettingsPage.page.waitForTimeout(1000);
		});

		await test.step('Verify all cookie types have been enabled', async () => {
			const actualCookies = await page.context().cookies();

			for (const cookieKey of cookieKeys) {
				const cookieKeyToggle = await page.locator(
					`[data-cookie-key="${cookieKey}"]`
				);

				await expect(await cookieKeyToggle).toBeChecked();

				const actualCookie = await actualCookies.find(
					(actualCookie) => actualCookie.name === cookieKey
				);

				await expect(actualCookie).toBeDefined();
				await expect(actualCookie.value).toEqual('true');
			}
		});
	}
);

async function expectCookieConsentPanelButtons(locator: Locator) {
	await locator.waitFor();

	const buttons = await locator.getByRole('button').all();

	expect(buttons).toHaveLength(3);
	await expect(await buttons[0]).toContainText('Use Necessary Cookies Only');
	await expect(await buttons[1]).toContainText('Accept Selected');
	await expect(await buttons[2]).toContainText('Accept All');
}
