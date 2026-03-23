/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Locator, expect, mergeTests} from '@playwright/test';
import path from 'path';

import {accountSettingsPagesTest} from '../../../fixtures/accountSettingsPagesTest';
import {featureFlagsTest} from '../../../fixtures/featureFlagsTest';
import {loginTest} from '../../../fixtures/loginTest';
import {systemSettingsPageTest} from '../../../fixtures/systemSettingsPageTest';
import {waitForAlert} from '../../../utils/waitForAlert';
import {
	clearConsentCookies,
	resetAllConsentManagerConfigurations,
	saveOrUpdateConfiguration,
	updateConsentManagerConfiguration,
} from './utils/consentManagerConfigurationHelper';

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
		'LPD-75027': {enabled: true},
		'LPD-75032': {enabled: true},
	}),
	loginTest(),
	systemSettingsPageTest
);

test.afterEach(async ({systemSettingsPage}) => {
	await test.step('Reset All Consent Manager Configurations', async () => {
		await resetAllConsentManagerConfigurations(systemSettingsPage);
	});

	await test.step('Clear Consent Cookies if present', async () => {
		await clearConsentCookies(systemSettingsPage);
	});
});

test.beforeEach(async ({page}) => {
	await test.step('Enable Preference Handling Cookies', async () => {
		await updateConsentManagerConfiguration(page, {
			enabled: true,
			forceReload: true,
		});
	});
});

test(
	'Escape texts in Cookie Banner to avoid XSS injections',
	{tag: '@LPD-69398'},
	async ({page, systemSettingsPage}) => {
		const script = '<script>alert("Hello world!")</script>';

		page.on('dialog', async (dialog) => {
			if (dialog.type() === 'alert') {
				throw new Error('XSS detected');
			}
		});

		await systemSettingsPage.goToSystemSetting('Privacy', 'Cookie Banner');

		await page.getByLabel('Title', {exact: true}).fill(script);
		await page.getByLabel('Content', {exact: true}).fill(script);
		await page.getByLabel('Privacy Policy Link').fill(script);
		await page.getByLabel('Link Display Text', {exact: true}).fill(script);

		await page.getByRole('button', {name: 'Save'}).dispatchEvent('click');

		await waitForAlert(page);
	}
);

test(
	'Escape texts in Cookie Panel to avoid XSS injections',
	{tag: '@LPD-69399'},
	async ({page, systemSettingsPage}) => {
		const script = '<script>alert("Hello world!")</script>';

		page.on('dialog', async (dialog) => {
			if (dialog.type() === 'alert') {
				throw new Error('XSS detected');
			}
		});

		await systemSettingsPage.goToSystemSetting('Privacy', 'Cookie Panel');

		await page.getByLabel('Title', {exact: true}).waitFor();

		await page.getByLabel('Title', {exact: true}).fill(script);
		await page.getByLabel('Description', {exact: true}).fill(script);
		await page.getByLabel('Cookie Policy Link').fill(script);
		await page.getByLabel('Link Display Text', {exact: true}).fill(script);
		await page
			.getByLabel('Strictly Necessary Cookies Description', {exact: true})
			.fill(script);
		await page
			.getByLabel('Functional Cookies Description', {exact: true})
			.fill(script);
		await page
			.getByLabel('Performance Cookies Description', {exact: true})
			.fill(script);
		await page
			.getByLabel('Personalization Cookies Description', {exact: true})
			.fill(script);

		await page.getByRole('button', {name: 'Save'}).dispatchEvent('click');

		await waitForAlert(page);

		const cookiesBanner = await page.getByRole('dialog', {
			name: 'banner cookies',
		});

		await cookiesBanner.waitFor();

		await cookiesBanner
			.getByRole('button', {name: 'Configuration'})
			.click();
	}
);

test(
	'Verify Consent Manager buttons',
	{tag: '@LPD-67119'},
	async ({accountSettingsPage}) => {
		await test.step('Go to Consent Manager Account Settings page', async () => {
			await accountSettingsPage.goToDataAndPrivacy();

			await accountSettingsPage.page
				.getByText('Consent Manager')
				.first()
				.waitFor();

			if (await accountSettingsPage.consentManagerMenuItem.isVisible()) {
				await accountSettingsPage.consentManagerMenuItem.click();
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
	'Verify Consent Manager can be accessed from the Data And Privacy Account Settings tab',
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

		await test.step('AC2: Verify Consent Manager panel is visible from Data and Privacy tab', async () => {
			await accountSettingsPage.goToDataAndPrivacy();

			await accountSettingsPage.page
				.getByText('Consent Manager')
				.first()
				.waitFor();

			if (await accountSettingsPage.consentManagerMenuItem.isVisible()) {
				await accountSettingsPage.consentManagerMenuItem.click();
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
	'Verify Cookie Banner Consent Panel buttons',
	{tag: '@LPD-67119'},
	async ({page}) => {
		await test.step('Open the Cookie Banner Consent Panel', async () => {
			await page.goto('/');

			const cookiesBanner = await page.getByRole('dialog', {
				name: 'banner cookies',
			});

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
	'Verify Cookie Preferences can be saved from the new Consent Manager page',
	{tag: '@LPD-60007'},
	async ({accountSettingsPage, page}) => {
		await test.step('Enable all cookie types', async () => {
			await accountSettingsPage.goToDataAndPrivacy();

			await accountSettingsPage.page
				.getByText('Consent Manager')
				.first()
				.waitFor();

			if (await accountSettingsPage.consentManagerMenuItem.isVisible()) {
				await accountSettingsPage.consentManagerMenuItem.click();
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

test(
	'Verify Custom Floating Icon can be selected',
	{tag: '@LPD-81552'},
	async ({page, systemSettingsPage}) => {
		await systemSettingsPage.goToSystemSetting(
			'Privacy',
			'Consent Manager'
		);

		const acceptAllButton = systemSettingsPage.page.getByRole('button', {
			name: 'Accept All',
		});

		await acceptAllButton.click();

		await expect(acceptAllButton).not.toBeVisible();
		await systemSettingsPage.page
			.getByText('Custom', {exact: true})
			.click();

		const fileChooserPromise = page.waitForEvent('filechooser');

		await systemSettingsPage.page
			.getByRole('button', {name: 'Change Custom Icon'})
			.click();

		const uploadImageFrame = await systemSettingsPage.page.frameLocator(
			'iframe[title="Upload Custom Icon"]'
		);

		await uploadImageFrame
			.getByRole('button', {name: 'Select Image'})
			.click();

		const fileChooser = await fileChooserPromise;

		await fileChooser.setFiles(
			path.join(__dirname, '/dependencies/liferay.png')
		);

		await uploadImageFrame
			.getByRole('button', {
				name: 'Done',
			})
			.click();

		await systemSettingsPage.page
			.getByRole('button', {name: 'Update'})
			.click();

		await waitForAlert(
			systemSettingsPage.page,
			`Success:Your request completed successfully.`
		);
		const imageButton = await systemSettingsPage.page.locator(
			'#_com_liferay_cookies_banner_web_portlet_CookiesBannerPortlet_floatingIconButton'
		);

		await expect(imageButton).toBeVisible();

		const imageSrc = await imageButton.getAttribute('src');

		await expect(imageSrc.includes('/image/floating_icon?')).toBeTruthy();
	}
);

test(
	'Verify Floating Icon can be selected',
	{tag: '@LPD-78592'},
	async ({page, systemSettingsPage}) => {
		await systemSettingsPage.goToSystemSetting(
			'Privacy',
			'Consent Manager'
		);

		const controlPanelIcon = page.locator(
			'label:has(svg.lexicon-icon-control-panel)'
		);

		await expect(controlPanelIcon).toBeVisible();

		const acceptAllButton = systemSettingsPage.page.getByRole('button', {
			name: 'Accept All',
		});

		await acceptAllButton.click();

		await expect(acceptAllButton).not.toBeVisible();

		await controlPanelIcon.check();

		await systemSettingsPage.page
			.getByRole('button', {name: 'Update'})
			.click();

		await expect(controlPanelIcon).toBeChecked();
	}
);

test(
	'Verify Floating Icon Enabled is visible and can be disabled',
	{tag: '@LPD-78592'},
	async ({systemSettingsPage}) => {
		await systemSettingsPage.goToSystemSetting(
			'Privacy',
			'Consent Manager'
		);

		const floatingIconButton = systemSettingsPage.page.getByLabel(
			'Floating Icon Enabled'
		);

		await floatingIconButton.waitFor({state: 'visible'});

		await expect(floatingIconButton).toBeChecked();

		const acceptAllButton = systemSettingsPage.page.getByRole('button', {
			name: 'Accept All',
		});

		await acceptAllButton.click();

		await expect(acceptAllButton).not.toBeVisible();

		await floatingIconButton.uncheck();

		await systemSettingsPage.page
			.getByRole('button', {name: 'Update'})
			.click();

		await expect(floatingIconButton).not.toBeChecked();
	}
);

test(
	'Verify Floating Icon use',
	{tag: '@LPD-78593'},
	async ({page, systemSettingsPage}) => {
		await systemSettingsPage.goToSystemSetting(
			'Privacy',
			'Consent Manager'
		);

		const acceptAllButton = systemSettingsPage.page.getByRole('button', {
			name: 'Accept All',
		});

		await acceptAllButton.click();

		await expect(acceptAllButton).not.toBeVisible();

		const floatingIconButton = page.locator(
			'#_com_liferay_cookies_banner_web_portlet_CookiesBannerPortlet_floatingIconButton'
		);

		await expect(floatingIconButton).toBeVisible();

		await floatingIconButton.click();

		const dialog = page.getByRole('dialog', {name: 'Cookie Configuration'});

		await expect(dialog).toBeVisible();

		await page.getByRole('button', {name: 'Accept Selected'}).click();

		await expect(dialog).not.toBeVisible();
	}
);

test(
	'Verify selected Floating Icon appears in button',
	{tag: '@LPD-78593'},
	async ({page, systemSettingsPage}) => {
		await systemSettingsPage.goToSystemSetting(
			'Privacy',
			'Consent Manager'
		);

		const controlPanelIcon = page.locator('label[for$="control-panel"]');

		await expect(controlPanelIcon).toBeVisible();

		const acceptAllButton = systemSettingsPage.page.getByRole('button', {
			name: 'Accept All',
		});

		await acceptAllButton.click();

		await expect(acceptAllButton).not.toBeVisible();

		await controlPanelIcon.click();

		await saveOrUpdateConfiguration(true, systemSettingsPage.page);

		await expect(controlPanelIcon).toBeChecked();

		const floatingIconButton = page.locator(
			'#_com_liferay_cookies_banner_web_portlet_CookiesBannerPortlet_floatingIconButton svg'
		);

		await expect(floatingIconButton).toHaveClass(
			/lexicon-icon-control-panel/
		);
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
