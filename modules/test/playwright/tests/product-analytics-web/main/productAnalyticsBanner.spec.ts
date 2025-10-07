/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {featureFlagsTest} from '../../../fixtures/featureFlagsTest';
import {instanceSettingsPagesTest} from '../../../fixtures/instanceSettingsPagesTest';
import {loginTest} from '../../../fixtures/loginTest';
import {productAnalyticsPagesTest} from '../../../fixtures/productAnalyticsPagesTest';
import {siteSettingsPagesTest} from '../../../fixtures/siteSettingsPagesTest';
import {systemSettingsPageTest} from '../../../fixtures/systemSettingsPageTest';
import {clickAndExpectToBeVisible} from '../../../utils/clickAndExpectToBeVisible';
import {waitForAlert} from '../../../utils/waitForAlert';

export const test = mergeTests(
	featureFlagsTest({
		'LPD-51356': {enabled: true},
	}),
	instanceSettingsPagesTest,
	loginTest(),
	productAnalyticsPagesTest,
	siteSettingsPagesTest,
	systemSettingsPageTest
);

test.afterEach(async ({systemSettingsPage}) => {
	await test.step('Reset Product Analytics System Settings if needed', async () => {
		await systemSettingsPage.goToSystemSetting(
			'Privacy',
			'Product Analytics'
		);

		await systemSettingsPage.page
			.getByRole('heading', {
				name: 'Product Analytics',
			})
			.waitFor();

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
	});

	await test.step('Reset Cookies System Settings if needed', async () => {
		await systemSettingsPage.goToSystemSetting('Privacy', 'Cookie Manager');

		await systemSettingsPage.page
			.getByRole('heading', {
				name: 'Cookie Manager',
			})
			.waitFor();

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
	});
});

test(
	'AC1: Verify "Product Analytics" Configuration is present and enabled by default',
	{tag: '@LPD-60003'},
	async ({
		instanceSettingsPage,
		page,
		siteSettingsPage,
		systemSettingsPage,
	}) => {
		const enabledButton = await page.getByLabel('Enabled');
		const productAnalyticsHeading = await page.getByRole('heading', {
			name: 'Product Analytics',
		});

		await test.step('Verify Product Analytics System Level Configuration', async () => {
			await systemSettingsPage.goToSystemSetting(
				'Privacy',
				'Product Analytics'
			);

			await productAnalyticsHeading.waitFor();

			await expect(enabledButton).toBeChecked();
		});

		await test.step('Verify Product Analytics Instance Level Configuration', async () => {
			await instanceSettingsPage.goToInstanceSetting(
				'Privacy',
				'Product Analytics',
				false
			);

			await productAnalyticsHeading.waitFor();

			await expect(enabledButton).toBeChecked();
		});

		await test.step('Verify Product Analytics Site Level Configuration', async () => {
			await siteSettingsPage.goToSiteSetting(
				'Privacy',
				'Product Analytics'
			);

			await productAnalyticsHeading.waitFor();

			await expect(enabledButton).toBeChecked();
		});
	}
);

test(
	'AC2 and AC3: Verify Product Analytics Banner is only present when enabled',
	{tag: '@LPD-60003'},
	async ({page, productAnalyticsBannerPage, systemSettingsPage}) => {
		await test.step('Verify Product Analytics Banner is present', async () => {
			await expect(
				await productAnalyticsBannerPage.bannerLocator
			).toBeVisible();
		});

		await test.step('Disable Product Analytics', async () => {
			await systemSettingsPage.goToSystemSetting(
				'Privacy',
				'Product Analytics'
			);

			await page
				.getByRole('heading', {
					name: 'Product Analytics',
				})
				.waitFor();

			const enabledButton = await page.getByLabel('Enabled');

			await enabledButton.setChecked(false);

			await page
				.getByRole('button', {name: 'Save'})
				.dispatchEvent('click');

			await waitForAlert(page);

			await expect(enabledButton).not.toBeChecked();
		});

		await test.step('Verify Product Analytics Banner is no longer present', async () => {
			await expect(
				await productAnalyticsBannerPage.bannerLocator
			).not.toBeVisible();
		});
	}
);

test(
	'AC1: After opting-in in Product Analytics Banner, cookies banner shows up when cookie config is enabled',
	{tag: '@LPD-60005'},
	async ({page, productAnalyticsBannerPage, systemSettingsPage}) => {
		await test.step('Enable Preference Handling Cookies', async () => {
			await systemSettingsPage.goToSystemSetting(
				'Privacy',
				'Cookie Manager'
			);

			await systemSettingsPage.page.waitForTimeout(1000);

			const enabledButton = page.getByLabel('Enabled');
			const isChecked = await enabledButton.isChecked();

			if (!isChecked) {
				await enabledButton.click();
			}

			await page
				.getByRole('button', {name: 'Save'})
				.dispatchEvent('click');

			await page.waitForTimeout(1000);
			await page.reload();

			await expect(enabledButton).toBeChecked();
		});

		await page.goto('/');

		await test.step('Verify Product Analytics Banner is present', async () => {
			await expect(
				await productAnalyticsBannerPage.bannerLocator
			).toBeVisible();
		});

		const acceptAll = page.getByRole('button', {name: 'Accept All'});

		await acceptAll.waitFor({state: 'visible'});

		await acceptAll.click();

		await test.step('Verify Product Analytics Banner is no longer present', async () => {
			await expect(
				await productAnalyticsBannerPage.bannerLocator
			).not.toBeVisible();
		});

		await test.step('Verify Cookies Banner shows up', async () => {
			await expect(
				await page.locator(
					'#p_p_id_com_liferay_cookies_banner_web_portlet_CookiesBannerPortlet_'
				)
			).toBeVisible();
		});
	}
);

test(
	'AC2: After opting-out in Product Analytics Banner, cookies banner shows up when cookie config is enabled',
	{tag: '@LPD-60005'},
	async ({page, productAnalyticsBannerPage, systemSettingsPage}) => {
		await test.step('Enable Preference Handling Cookies', async () => {
			await systemSettingsPage.goToSystemSetting(
				'Privacy',
				'Cookie Manager'
			);

			await systemSettingsPage.page.waitForTimeout(1000);

			const enabledButton = page.getByLabel('Enabled');
			const isChecked = await enabledButton.isChecked();

			if (!isChecked) {
				await enabledButton.click();
			}

			const updateButton = page.getByRole('button', {
				name: 'Update',
			});

			const saveButton = page.getByRole('button', {
				name: 'Save',
			});

			if (await saveButton.isVisible()) {
				await saveButton.dispatchEvent('click');
			}
			else if (await updateButton.isVisible()) {
				await updateButton.dispatchEvent('click');
			}

			await waitForAlert(page);

			await expect(enabledButton).toBeChecked();
		});

		await page.goto('/');

		await test.step('Verify Product Analytics Banner is present', async () => {
			await expect(
				await productAnalyticsBannerPage.bannerLocator
			).toBeVisible();
		});

		const acceptAll = page.getByRole('button', {name: 'Decline All'});

		await acceptAll.waitFor({state: 'visible'});

		await acceptAll.click();

		await test.step('Verify Product Analytics Banner is no longer present', async () => {
			await expect(
				await productAnalyticsBannerPage.bannerLocator
			).not.toBeVisible();
		});

		await test.step('Verify Cookies Banner shows up', async () => {
			await expect(
				await page.locator(
					'#p_p_id_com_liferay_cookies_banner_web_portlet_CookiesBannerPortlet_'
				)
			).toBeVisible();
		});
	}
);
