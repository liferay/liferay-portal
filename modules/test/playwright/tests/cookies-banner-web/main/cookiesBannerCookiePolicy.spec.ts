/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {consentManagerConfigurationPageTest} from '../../../fixtures/consentManagerConfigurationPageTest';
import {dataApiHelpersTest} from '../../../fixtures/dataApiHelpersTest';
import {featureFlagsTest} from '../../../fixtures/featureFlagsTest';
import {isolatedSiteTest} from '../../../fixtures/isolatedSiteTest';
import {loginTest} from '../../../fixtures/loginTest';
import {systemSettingsPageTest} from '../../../fixtures/systemSettingsPageTest';
import getRandomString from '../../../utils/getRandomString';
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
	dataApiHelpersTest,
	featureFlagsTest({
		'LPS-178052': {enabled: true},
	}),
	isolatedSiteTest,
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
			.locator('div[role="region"].cookies-banner')
			.waitFor({state: 'visible'});

		const cookiesBannerContainer = page.locator(
			'div[role="region"].cookies-banner'
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

		const cookiesBanner = page.locator('div[role="region"].cookies-banner');

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

test(
	'Cookie Policy Page Is Readable by a Guest',
	{tag: '@LPD-101999'},
	async ({browser, consentManagerConfigurationPage}) => {
		await test.step('Enable Consent Manager with Explicit Cookie Consent Mode', async () => {
			await updateConsentManagerConfiguration(
				consentManagerConfigurationPage.page,
				{
					enabled: true,
					explicitCookieConsentMode: true,
					forceReload: true,
				}
			);
		});

		const guestContext = await browser.newContext({
			storageState: {cookies: [], origins: []},
		});

		const guestPage = await guestContext.newPage();

		try {
			await test.step('Go to Cookie Policy page as a guest', async () => {
				await guestPage.goto('/');

				const cookiesBannerContainer = guestPage.locator(
					'div[role="region"].cookies-banner'
				);

				await expect(cookiesBannerContainer).toBeVisible();

				await cookiesBannerContainer.locator('p.mb-0 a').click();

				await expect(guestPage.getByText('Cookies List')).toBeVisible({
					timeout: 100 * 1000,
				});
			});

			await test.step('Assert the guest reads every cookie table', async () => {
				await expect(
					guestPage.getByText(
						'You do not have the roles required to access this portlet.'
					)
				).toHaveCount(0);

				const objectDefinitionPortlets = await guestPage
					.locator(
						'[id^="portlet_com_liferay_object_web_internal_object_definitions_portlet_ObjectDefinitionsPortlet_"]'
					)
					.all();

				expect(objectDefinitionPortlets.length).toBe(4);

				for (const objectDefinitionPortlet of objectDefinitionPortlets) {
					await expect(
						objectDefinitionPortlet.locator('.fds thead')
					).toBeVisible({
						timeout: 100 * 1000,
					});

					const tableRows = await objectDefinitionPortlet
						.locator('.fds tbody tr')
						.all();

					expect(tableRows.length).toBeGreaterThan(0);
				}
			});
		}
		finally {
			await guestContext.close();
		}
	}
);

test(
	'Privacy and cookie policy links can point to a page of a site',
	{tag: ['@LPS-148320', '@LPS-152844']},
	async ({apiHelpers, page, site, systemSettingsPage}) => {
		await test.step('Enable the Consent Manager', async () => {
			await updateConsentManagerConfiguration(page, {
				enabled: true,
				forceReload: true,
			});
		});

		const layout = await apiHelpers.headlessDelivery.createSitePage({
			siteId: site.id,
			title: getRandomString(),
		});

		const policyPagePath = `/web${site.friendlyUrlPath}${layout.friendlyUrlPath}`;

		await test.step('Point the Cookie Banner privacy policy link to the site page', async () => {
			await systemSettingsPage.goToSystemSetting(
				'Privacy',
				'Cookie Banner'
			);

			await page.getByLabel('Privacy Policy Link').fill(policyPagePath);

			await saveOrUpdateConfiguration(page);
		});

		const cookiesBanner = page.locator('div[role="region"].cookies-banner');

		await test.step('Follow the Cookie Banner privacy policy link', async () => {
			await page.goto('/');

			await cookiesBanner
				.getByRole('link', {
					name: 'Visit our privacy policy to learn more.',
				})
				.click();

			await expect(page).toHaveURL(
				(url) => url.pathname === policyPagePath
			);
		});

		await test.step('Point the Cookie Panel cookie policy link to the site page', async () => {
			await systemSettingsPage.goToSystemSetting(
				'Privacy',
				'Cookie Panel'
			);

			await page.getByLabel('Cookie Policy Link').fill(policyPagePath);

			await saveOrUpdateConfiguration(page);
		});

		await test.step('Follow the Cookie Panel cookie policy link', async () => {
			await page.goto('/');

			await cookiesBanner
				.getByRole('button', {name: 'Configuration'})
				.click();

			const policyPagePromise = page.context().waitForEvent('page');

			await page
				.frameLocator('#cookiesBannerConfiguration iframe')
				.locator(`a[href="${policyPagePath}"]`)
				.click();

			const policyPage = await policyPagePromise;

			await expect(policyPage).toHaveURL(
				(url) => url.pathname === policyPagePath
			);

			await policyPage.close();

			await page
				.getByRole('button', {name: 'Use Necessary Cookies Only'})
				.click();
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
