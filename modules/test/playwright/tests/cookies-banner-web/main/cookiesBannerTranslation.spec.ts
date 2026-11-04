/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {consentManagerConfigurationPageTest} from '../../../fixtures/consentManagerConfigurationPageTest';
import {loginTest} from '../../../fixtures/loginTest';
import {systemSettingsPageTest} from '../../../fixtures/systemSettingsPageTest';
import getRandomString from '../../../utils/getRandomString';
import {
	clearConsentCookies,
	resetAllConsentManagerConfigurations,
	saveOrUpdateConfiguration,
	updateConsentManagerConfiguration,
} from './utils/consentManagerConfigurationHelper';

export const test = mergeTests(
	consentManagerConfigurationPageTest,
	loginTest(),
	systemSettingsPageTest
);

test.afterEach(async ({systemSettingsPage}) => {
	await test.step('Reset All Consent Manager Configurations', async () => {
		await resetAllConsentManagerConfigurations(systemSettingsPage);
	});

	await test.step('Clear Consent Cookies if present', async () => {
		await clearConsentCookies(systemSettingsPage.page);
	});
});

for (const {menuItem, policyLinkLabel} of [
	{menuItem: 'Cookie Banner', policyLinkLabel: 'Privacy Policy Link'},
	{menuItem: 'Cookie Panel', policyLinkLabel: 'Cookie Policy Link'},
]) {
	for (const {saveLocaleId, saveLocaleName} of [
		{saveLocaleId: 'en_US', saveLocaleName: 'English'},
		{saveLocaleId: 'es_ES', saveLocaleName: 'Spanish'},
	]) {
		test(
			`Translations of the ${menuItem} title are saved from ${saveLocaleName}`,
			{tag: '@LPD-69402'},
			async ({page, systemSettingsPage}) => {
				const titleInput = page.getByLabel('Title', {exact: true});

				const selectTitleLocale = async (localeId: string) => {
					await page
						.locator('.form-group', {has: titleInput})
						.locator('[data-testid="triggerButton"]')
						.click();

					await page
						.locator(`button[name="title${localeId}"]`)
						.click();
				};

				const translation = getRandomString();

				await test.step('Enable Consent Manager', async () => {
					await updateConsentManagerConfiguration(page, {
						enabled: true,
						forceReload: true,
					});
				});

				await test.step(`Translate the title to Spanish and update from ${saveLocaleName}`, async () => {
					await systemSettingsPage.goToSystemSetting(
						'Privacy',
						menuItem
					);

					await selectTitleLocale('es_ES');

					await titleInput.fill(translation);

					await selectTitleLocale(saveLocaleId);

					await page
						.getByLabel(policyLinkLabel, {exact: true})
						.fill('http://www.liferay.com');

					await saveOrUpdateConfiguration(page);
				});

				await test.step('Verify the Spanish title is kept after a reload', async () => {
					await page.reload();

					await selectTitleLocale('es_ES');

					await expect(titleInput).toHaveValue(translation);
				});
			}
		);
	}
}
