/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {consentManagerConfigurationPageTest} from '../../../fixtures/consentManagerConfigurationPageTest';
import {loginTest} from '../../../fixtures/loginTest';
import {systemSettingsPageTest} from '../../../fixtures/systemSettingsPageTest';
import {
	clearConsentCookies,
	resetAllConsentManagerConfigurations,
	saveOrUpdateConfiguration,
	updateConsentManagerConfiguration,
} from './utils/consentManagerConfigurationHelper';

const hideableCookieTypes = [
	'Functional Cookies',
	'Performance Cookies',
	'Personalization Cookies',
];

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

test('LPD-30561 Cookie Banner Cookie Policy Page', async ({
	consentManagerConfigurationPage,
	page,
}) => {
	await test.step('Enable Consent Manager with Explicit Cookie Consent Mode', async () => {
		await updateConsentManagerConfiguration(
			consentManagerConfigurationPage.page,
			{
				enabled: true,
				explicitCookieConsentMode: true,
				forceReload: true,
			}
		);

		await expect(
			consentManagerConfigurationPage.explicitCookieConsentModeCheckbox
		).toBeChecked();
	});

	await test.step('Go to Cookie Policy page', async () => {
		await page.goto('/');

		await page
			.locator('div[role="dialog"][aria-modal="true"]')
			.waitFor({state: 'visible'});

		const cookiesBannerContainer = page.locator(
			'div[role="dialog"][aria-modal="true"]'
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
				objectDefinitionPortlet.locator('.fds thead')
			).toBeVisible({
				timeout: 100 * 1000,
			});

			const tableRows = await objectDefinitionPortlet
				.locator('.fds tr')
				.all();

			expect(tableRows.length).toBeGreaterThan(0);
		}
	});
});

test(
	'Consent Manager Adjustments',
	{tag: '@LPD-60002'},
	async ({browser, page, systemSettingsPage}) => {
		await test.step('Enable Preference Handling Cookies', async () => {
			await updateConsentManagerConfiguration(page, {
				enabled: true,
				explicitCookieConsentMode: true,
				forceReload: true,
			});
		});

		const cookiesBanner = page.locator(
			'div[role="dialog"][aria-modal="true"]'
		);

		// Accept All cookies so the Cookies Banner doesn't break the test

		if (await cookiesBanner.isVisible()) {
			await page.getByRole('button', {name: 'Accept All'}).click();
		}

		const cookiePolicyLink = page.getByLabel('Cookie Policy Link');

		const hideFromEndUserCheckboxes = page.getByLabel('Hide from end-user');

		await test.step('AC1: Verify each non-strictly necessary cookie type has a "Hide from end-user" configuration entry', async () => {
			await systemSettingsPage.goToSystemSetting(
				'Privacy',
				'Cookie Panel'
			);

			await cookiePolicyLink.waitFor();

			expect(await hideFromEndUserCheckboxes.count()).toEqual(
				hideableCookieTypes.length
			);
		});

		await test.step('AC2: Enabling "Hide from end-user" will hide the cookie type from the consent banner', async () => {
			for (const hideFromEndUserCheckbox of await hideFromEndUserCheckboxes.all()) {
				await hideFromEndUserCheckbox.check();
			}

			// Configuration won't save without a URL in the Cookie Policy Link

			await cookiePolicyLink.fill('http://www.liferay.com');

			await saveOrUpdateConfiguration(page);

			for (const hideFromEndUserCheckbox of await hideFromEndUserCheckboxes.all()) {
				await expect(hideFromEndUserCheckbox).toBeChecked();
			}

			await expectCookiesBannerTypes(browser);
		});

		await test.step('AC3: Disabling "Hide from end-user" will display the cookie type in the consent banner', async () => {
			for (const hideFromEndUserCheckbox of await hideFromEndUserCheckboxes.all()) {
				await hideFromEndUserCheckbox.uncheck();
			}

			await saveOrUpdateConfiguration(page);

			for (const hideFromEndUserCheckbox of await hideFromEndUserCheckboxes.all()) {
				await expect(hideFromEndUserCheckbox).not.toBeChecked();
			}

			await expectCookiesBannerTypes(browser, hideableCookieTypes, false);
		});
	}
);

async function expectCookiesBannerTypes(
	browser,
	cookieTypes: string[] = hideableCookieTypes,
	hidden: boolean = true
) {
	const newPage = await browser.newPage();

	await newPage.goto('/');

	await newPage.getByRole('button', {name: 'Configuration'}).waitFor();

	await newPage.getByRole('button', {name: 'Configuration'}).click();

	const cookieConfigurationIFrame = await newPage.frameLocator(
		'iframe[title="Cookie Configuration"]'
	);

	await cookieConfigurationIFrame
		.getByRole('heading', {name: 'Strictly Necessary Cookies'})
		.waitFor();

	for (const cookieType of cookieTypes) {
		const cookieTypeHeading = cookieConfigurationIFrame.getByRole(
			'heading',
			{name: cookieType}
		);

		if (hidden) {
			await expect(await cookieTypeHeading).not.toBeVisible();
		}
		else {
			await expect(await cookieTypeHeading).toBeVisible();
		}
	}

	await newPage.close();
}
