/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {featureFlagsTest} from '../../fixtures/featureFlagsTest';
import {loginTest} from '../../fixtures/loginTest';
import {systemSettingsPageTest} from '../../fixtures/systemSettingsPageTest';
import {waitForAlert} from '../../utils/waitForAlert';

export const test = mergeTests(
	featureFlagsTest({
		'LPD-10588': true,
	}),
	loginTest(),
	systemSettingsPageTest
);

test('LPD-30561 Cookie Banner Cookie Policy Page', async ({
	page,
	systemSettingsPage,
}) => {
	await test.step('Enable Preference Handling Cookies', async () => {
		await systemSettingsPage.goToSystemSetting(
			'Cookies',
			'Preference Handling'
		);

		const enabledButton = page.getByLabel('Enabled');

		await enabledButton.waitFor({state: 'visible'});

		await page.waitForTimeout(3000);

		const isChecked = await enabledButton.isChecked();

		if (!isChecked) {
			await enabledButton.click();
		}

		await expect(enabledButton).toBeChecked();
	});

	await test.step('Enable Explicit Cookie Consent Mode', async () => {
		const explicitCookieConsentModeButton = page.getByLabel(
			'Explicit Cookie Consent Mode'
		);

		await explicitCookieConsentModeButton.waitFor({state: 'visible'});

		const isChecked = await explicitCookieConsentModeButton.isChecked();

		if (!isChecked) {
			await explicitCookieConsentModeButton.click();
		}

		await expect(explicitCookieConsentModeButton).toBeChecked();
	});

	await test.step('Update Preference Handling', async () => {
		const updateButton = page.getByRole('button', {
			name: 'Update',
		});

		const saveButton = page.getByRole('button', {
			name: 'Save',
		});

		if (await saveButton.isVisible()) {
			await saveButton.click();
		}
		else if (await updateButton.isVisible()) {
			await updateButton.click();
		}

		await waitForAlert(page);
	});

	await test.step('Go to Cookie Policy page', async () => {
		await page.goto('/');

		await page
			.locator(
				'#p_p_id_com_liferay_cookies_banner_web_portlet_CookiesBannerPortlet_'
			)
			.waitFor({state: 'visible'});

		const cookiesBannerContainer = page.locator(
			'//div[@role="dialog"][@aria-label="banner cookies"]'
		);

		await expect(cookiesBannerContainer).toBeVisible();

		const paragraph = cookiesBannerContainer.locator('p.mb-0');

		await expect(paragraph).toBeVisible();

		const cookiePolicyURL = paragraph.locator('a');

		await cookiePolicyURL.click();

		await expect(page.getByText('Cookies List')).toBeVisible({
			timeout: 100 * 1000,
		});

		const objectDefinitionPortlets = await page
			.locator(
				'[id^="portlet_com_liferay_object_web_internal_object_definitions_portlet_ObjectDefinitionsPortlet_"]'
			)
			.all();

		expect(objectDefinitionPortlets.length).toBe(4);

		for (const objectDefinitionPortletIndex in objectDefinitionPortlets) {
			const objectDefinitionPortlet =
				objectDefinitionPortlets[objectDefinitionPortletIndex];

			await expect(
				objectDefinitionPortlet.locator('.dnd-thead')
			).toBeVisible({
				timeout: 100 * 1000,
			});

			const tableRows = await objectDefinitionPortlet
				.locator('.dnd-tr')
				.all();

			expect(tableRows.length).toBeGreaterThan(0);
		}
	});
});
