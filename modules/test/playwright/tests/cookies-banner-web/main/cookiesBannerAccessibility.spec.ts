/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {loginTest} from '../../../fixtures/loginTest';
import {systemSettingsPageTest} from '../../../fixtures/systemSettingsPageTest';
import {checkAccessibility} from '../../../utils/checkAccessibility';
import {clickAndExpectToBeHidden} from '../../../utils/clickAndExpectToBeHidden';
import {
	clearConsentCookies,
	resetConsentManagerConfiguration,
	updateConsentManagerConfiguration,
} from './utils/consentManagerConfigurationHelper';

export const test = mergeTests(loginTest(), systemSettingsPageTest);

test.afterEach(async ({systemSettingsPage}) => {
	await test.step('Reset Consent Manager Configuration', async () => {
		await resetConsentManagerConfiguration(systemSettingsPage);
	});

	await test.step('Clear Consent Cookies if present', async () => {
		await clearConsentCookies(systemSettingsPage.page);
	});
});

test(
	'LPD-30822 Cookie Banner Accessibility',
	{tag: '@LPD-111751'},
	async ({page}) => {
		await test.step('Enable Third Party Cookies', async () => {
			await updateConsentManagerConfiguration(page, {
				enabled: true,
				forceReload: true,
			});
		});

		const cookiesBannerContainer = page.locator(
			'div[role="region"].cookies-banner'
		);

		await test.step('Check role, aria-labelledby, heading and paragraph', async () => {
			await page.goto('/');

			await cookiesBannerContainer.waitFor({state: 'visible'});

			await expect(cookiesBannerContainer).toBeVisible();

			await expect(cookiesBannerContainer).not.toHaveAttribute(
				'aria-label'
			);
			await expect(cookiesBannerContainer).toHaveAttribute(
				'aria-labelledby'
			);
			await expect(cookiesBannerContainer).not.toHaveAttribute(
				'aria-modal'
			);

			await expect(cookiesBannerContainer.locator('h2')).toBeVisible();
			await expect(
				cookiesBannerContainer.locator('p.mb-0')
			).toBeVisible();
		});

		await test.step('Run axe accessibility check on banner', async () => {
			await checkAccessibility({
				page,
				selectors: ['div[role="region"].cookies-banner'],
				soft: false,
			});
		});

		await test.step('Check the banner follows the skip link in the focus order', async () => {
			await page.keyboard.press('Tab');

			await expect(
				page.getByRole('link', {name: 'Skip to Main Content'})
			).toBeFocused();

			await page.keyboard.press('Tab');

			await expect(
				cookiesBannerContainer.getByRole('link')
			).toBeFocused();
		});

		await test.step('Check the banner reserves its height as scroll padding', async () => {
			const bannerHeight = await cookiesBannerContainer.evaluate(
				(element: HTMLElement) => element.offsetHeight
			);

			await expect(page.locator('html')).toHaveCSS(
				'scroll-padding-bottom',
				`${bannerHeight}px`
			);

			await clickAndExpectToBeHidden({
				target: cookiesBannerContainer,
				trigger: cookiesBannerContainer.getByRole('button', {
					name: 'Accept All',
				}),
			});

			await expect(page.locator('html')).toHaveCSS(
				'scroll-padding-bottom',
				'auto'
			);
		});
	}
);

test('LPD-67788 Cookie Panel toggle switches have aria-labels', async ({
	page,
}) => {
	await test.step('Enable Third Party Cookies', async () => {
		await updateConsentManagerConfiguration(page, {
			enabled: true,
			forceReload: true,
		});
	});

	await test.step('Open Configuration panel', async () => {
		await page.goto('/');

		await page
			.locator('div[role="region"].cookies-banner')
			.waitFor({state: 'visible'});

		const configButton = page.getByRole('button', {name: 'Configuration'});

		await expect(configButton).toHaveAttribute(
			'aria-label',
			/configuration/i
		);

		await configButton.click();
	});

	await test.step('Verify toggle switches have non-empty aria-labels', async () => {
		await page
			.locator('[id="cookiesBannerConfiguration"]')
			.waitFor({state: 'visible'});

		const cookiesPanel = page.frameLocator(
			'#cookiesBannerConfiguration iframe'
		);

		await cookiesPanel
			.locator('.toggle-switch-check')
			.first()
			.waitFor({state: 'attached'});

		const toggles = await cookiesPanel
			.locator('.toggle-switch-check')
			.all();

		expect(toggles.length).toBeGreaterThan(0);

		for (const toggle of toggles) {
			const ariaLabel = await toggle.getAttribute('aria-label');

			expect(ariaLabel).toBeTruthy();
		}
	});

	await test.step('Run axe accessibility check on cookie panel', async () => {
		await checkAccessibility({
			page,
			selectors: ['[id="cookiesBannerConfiguration"]'],
			soft: false,
		});
	});
});
