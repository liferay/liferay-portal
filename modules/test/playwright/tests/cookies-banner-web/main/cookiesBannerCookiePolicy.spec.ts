/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {featureFlagsTest} from '../../../fixtures/featureFlagsTest';
import {loginTest} from '../../../fixtures/loginTest';
import {systemSettingsPageTest} from '../../../fixtures/systemSettingsPageTest';
import {clickAndExpectToBeVisible} from '../../../utils/clickAndExpectToBeVisible';
import {waitForAlert} from '../../../utils/waitForAlert';

const hideableCookieTypes = [
	'Functional Cookies',
	'Performance Cookies',
	'Personalization Cookies',
];

export const test = mergeTests(
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

test('LPD-30561 Cookie Banner Cookie Policy Page', async ({
	page,
	systemSettingsPage,
}) => {
	await test.step('Enable Preference Handling Cookies', async () => {
		await systemSettingsPage.goToSystemSetting('Privacy', 'Cookie Manager');

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
	'Cookie Manager Adjustments',
	{tag: '@LPD-60002'},
	async ({browser, page, systemSettingsPage}) => {
		await test.step('Enable Preference Handling Cookies if needed', async () => {
			await systemSettingsPage.goToSystemSetting(
				'Privacy',
				'Cookie Manager'
			);

			const enabledButton = await page.getByLabel('Enabled');

			await enabledButton.waitFor();

			await enabledButton.check();

			await page.getByRole('button', {name: 'Save'}).click();

			await waitForAlert(page);
		});

		const cookiesBanner = await page.locator(
			'#p_p_id_com_liferay_cookies_banner_web_portlet_CookiesBannerPortlet_'
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

			await page.getByRole('button', {name: 'Save'}).click();

			await waitForAlert(page);

			for (const hideFromEndUserCheckbox of await hideFromEndUserCheckboxes.all()) {
				await expect(await hideFromEndUserCheckbox).toBeChecked();
			}

			await expectCookiesBannerTypes(browser);
		});

		await test.step('AC3: Disabling "Hide from end-user" will display the cookie type in the consent banner', async () => {
			for (const hideFromEndUserCheckbox of await hideFromEndUserCheckboxes.all()) {
				await hideFromEndUserCheckbox.uncheck();
			}

			await page.getByRole('button', {name: 'Update'}).click();

			await waitForAlert(page);

			for (const hideFromEndUserCheckbox of await hideFromEndUserCheckboxes.all()) {
				await expect(await hideFromEndUserCheckbox).not.toBeChecked();
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
