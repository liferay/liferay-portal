/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';
import path from 'path';

import {accountSettingsPagesTest} from '../../../fixtures/accountSettingsPagesTest';
import {consentManagerConfigurationPageTest} from '../../../fixtures/consentManagerConfigurationPageTest';
import {isolatedSiteTest} from '../../../fixtures/isolatedSiteTest';
import {loginTest} from '../../../fixtures/loginTest';
import {siteSettingsPagesTest} from '../../../fixtures/siteSettingsPagesTest';
import {systemSettingsPageTest} from '../../../fixtures/systemSettingsPageTest';
import {ConsentManagerConfigurationPage} from '../../../pages/cookies-banner-web/ConsentManagerConfigurationPage';
import {waitForAlert} from '../../../utils/waitForAlert';
import {
	clearConsentCookies,
	resetConsentManagerConfiguration,
	saveOrUpdateConfiguration,
	updateConsentManagerConfiguration,
} from './utils/consentManagerConfigurationHelper';

export const test = mergeTests(
	accountSettingsPagesTest,
	consentManagerConfigurationPageTest,
	isolatedSiteTest,
	loginTest(),
	siteSettingsPagesTest,
	systemSettingsPageTest
);

test.afterEach(async ({systemSettingsPage}) => {
	await test.step('Reset Consent Manager Configuration', async () => {
		await resetConsentManagerConfiguration(systemSettingsPage);
	});

	await test.step('Clear Consent Cookies if present', async () => {
		await clearConsentCookies(systemSettingsPage.page);
	});
});

test(
	'Activate button stays disabled until the Enabled checkbox is saved',
	{tag: ['@LPD-86660', '@LPD-86661']},
	async ({consentManagerConfigurationPage, page}) => {
		await test.step('Ensure Consent Manager is saved as disabled', async () => {
			await updateConsentManagerConfiguration(page, {
				enabled: false,
				forceReload: true,
			});
		});

		await test.step('Verify the Activate button is initially disabled', async () => {
			await consentManagerConfigurationPage.goTo();

			await expect(
				consentManagerConfigurationPage.toggleActivateButton
			).toBeDisabled();
		});

		await test.step('Check the Enabled checkbox without saving and verify the Activate button is still disabled', async () => {
			await consentManagerConfigurationPage.enabledCheckbox.setChecked(
				true
			);

			await expect(
				consentManagerConfigurationPage.toggleActivateButton
			).toBeDisabled();
		});

		await test.step('Save and verify the Activate button becomes enabled', async () => {
			await saveOrUpdateConfiguration(page);

			await expect(
				consentManagerConfigurationPage.toggleActivateButton
			).toBeEnabled();
		});
	}
);

test(
	'Activation warning is visible on Consent Manager, Cookie Banner, and Cookie Panel pages',
	{tag: '@LPD-87281'},
	async ({page, systemSettingsPage}) => {
		const noticeText =
			/The consent management elements are not shown for end users by default/i;

		await test.step('Enable Consent Manager so the sub-tabs become available', async () => {
			await updateConsentManagerConfiguration(page, {
				active: false,
				enabled: true,
				forceReload: true,
			});
		});

		await test.step('Verify notice on Consent Manager page', async () => {
			await systemSettingsPage.goToSystemSetting(
				'Privacy',
				'Consent Manager'
			);

			await expect(page.getByText(noticeText).first()).toBeVisible();
		});

		await test.step('Verify notice on Cookie Banner page', async () => {
			await page
				.locator('#main-content')
				.getByRole('menuitem', {exact: true, name: 'Cookie Banner'})
				.click();

			await expect(page.getByText(noticeText).first()).toBeVisible();
		});

		await test.step('Verify notice on Cookie Panel page', async () => {
			await page
				.locator('#main-content')
				.getByRole('menuitem', {exact: true, name: 'Cookie Panel'})
				.click();

			await expect(page.getByText(noticeText).first()).toBeVisible();
		});
	}
);

test(
	'Changing the renewal period auto-checks and disables the force re-consent checkbox',
	{tag: '@LPD-87281'},
	async ({consentManagerConfigurationPage, page}) => {
		await test.step('Enable and activate Consent Manager', async () => {
			await updateConsentManagerConfiguration(page, {
				enabled: true,
				forceReload: true,
			});

			await expect(
				consentManagerConfigurationPage.toggleDeactivateButton
			).toBeVisible();
		});

		await test.step('Decline cookies banner so it stops covering the form', async () => {
			const banner = page.locator('.cookies-banner');

			await banner.waitFor({state: 'visible'});

			await banner.getByRole('button', {name: 'Decline All'}).click();

			await banner.waitFor({state: 'hidden'});
		});

		await test.step('Change renewal period and verify the checkbox is checked and disabled', async () => {
			await consentManagerConfigurationPage.consentRenewalPeriodInput.fill(
				'6'
			);

			await consentManagerConfigurationPage.updateButton.click();

			const modal = page.getByRole('alertdialog');

			await expect(modal).toContainText(
				'These changes will take effect immediately'
			);

			const checkbox = modal.getByRole('checkbox', {
				name: /force re-consent/i,
			});

			await expect(checkbox).toBeChecked();
			await expect(checkbox).toBeDisabled();

			await modal.getByRole('button', {name: 'OK'}).click();

			await waitForAlert(page);
		});

		await test.step('Verify cookies banner reappears after re-consent', async () => {
			await page.goto('/');

			await expect(page.locator('.cookies-banner')).toBeVisible();
		});
	}
);

test(
	'Clicking Activate renders the Cookies Banner and shows Data And Privacy',
	{tag: '@LPD-87281'},
	async ({
		accountSettingsPage,
		browser,
		consentManagerConfigurationPage,
		page,
	}) => {
		await test.step('Enable Consent Manager leaving active off', async () => {
			await updateConsentManagerConfiguration(page, {
				active: false,
				enabled: true,
				forceReload: true,
			});
		});

		await test.step('Click Activate and verify the button now reads Deactivate', async () => {
			await toggleActiveAndWait(consentManagerConfigurationPage);

			await expect(
				consentManagerConfigurationPage.toggleDeactivateButton
			).toBeVisible();
		});

		await test.step('Verify the Cookies Banner and floating icon render for a guest user', async () => {
			const guestPage = await browser.newPage();

			await guestPage.goto('/');

			await expect(guestPage.locator('.cookies-banner')).toBeVisible();

			await guestPage.close();
		});

		await test.step('Verify the Data And Privacy tab is visible for end users', async () => {
			await accountSettingsPage.goToAccountSettings();

			await expect(
				page.locator('.nav-link', {
					hasText: 'Data And Privacy',
				})
			).toBeVisible();
		});
	}
);

test(
	'Clicking Deactivate hides the Cookies Banner and Data And Privacy while keeping sub-tabs visible',
	{tag: '@LPD-87281'},
	async ({
		accountSettingsPage,
		browser,
		consentManagerConfigurationPage,
		page,
		systemSettingsPage,
	}) => {
		await test.step('Enable and activate Consent Manager', async () => {
			await updateConsentManagerConfiguration(page, {
				enabled: true,
				forceReload: true,
			});

			await expect(
				consentManagerConfigurationPage.toggleDeactivateButton
			).toBeVisible();
		});

		await test.step('Click Deactivate and verify the button now reads Activate', async () => {
			await toggleActiveAndWait(consentManagerConfigurationPage);

			await expect(
				consentManagerConfigurationPage.toggleActivateButton
			).toBeVisible();
		});

		await test.step('Verify the Cookies Banner no longer renders for a guest user', async () => {
			const guestPage = await browser.newPage();

			await guestPage.goto('/');

			await expect(
				guestPage.locator('.cookies-banner')
			).not.toBeVisible();

			await guestPage.close();
		});

		await test.step('Verify Cookie Banner and Cookie Panel sub-tabs remain visible', async () => {
			await systemSettingsPage.goToSystemSetting(
				'Privacy',
				'Consent Manager'
			);

			const privacyMenu =
				systemSettingsPage.page.locator('#main-content');

			await expect(
				privacyMenu.getByRole('menuitem', {
					exact: true,
					name: 'Cookie Banner',
				})
			).toBeVisible();
			await expect(
				privacyMenu.getByRole('menuitem', {
					exact: true,
					name: 'Cookie Panel',
				})
			).toBeVisible();
		});

		await test.step('Verify the Data And Privacy tab is hidden again for end users', async () => {
			await accountSettingsPage.goToAccountSettings();

			await expect(
				page.locator('.nav-link', {
					hasText: 'Data And Privacy',
				})
			).not.toBeVisible();
		});
	}
);

test(
	'Clicking Deactivate shows a confirmation dialog that can be cancelled or confirmed',
	{tag: '@LPD-87281'},
	async ({consentManagerConfigurationPage, page}) => {
		await test.step('Enable and activate Consent Manager', async () => {
			await updateConsentManagerConfiguration(page, {
				enabled: true,
				forceReload: true,
			});

			await expect(
				consentManagerConfigurationPage.toggleDeactivateButton
			).toBeVisible();
		});

		await test.step('Dismiss the deactivate confirmation and verify still active', async () => {
			page.once('dialog', async (dialogWindow) => {
				expect(dialogWindow.message()).toContain(
					'The consent manager is currently active'
				);

				await dialogWindow.dismiss();
			});

			await consentManagerConfigurationPage.toggleDeactivateButton.click();

			await page.waitForTimeout(500);

			await expect(
				consentManagerConfigurationPage.toggleDeactivateButton
			).toBeVisible();
		});

		await test.step('Accept the deactivate confirmation and verify deactivated', async () => {
			page.once('dialog', async (dialogWindow) => {
				await dialogWindow.accept();
			});

			await consentManagerConfigurationPage.toggleDeactivateButton.click();

			await waitForAlert(page);

			await expect(
				consentManagerConfigurationPage.toggleActivateButton
			).toBeVisible();
		});
	}
);

test(
	'Consent Manager menu items keep their order',
	{tag: '@LPS-156903'},
	async ({page, site, siteSettingsPage}) => {
		await siteSettingsPage.goToSiteSetting(
			'Privacy',
			'Consent Manager',
			site.friendlyUrlPath
		);

		await page
			.getByRole('checkbox', {exact: true, name: 'Enabled'})
			.check();

		await siteSettingsPage.saveConfiguration();

		await siteSettingsPage.goToSiteSetting(
			'Privacy',
			undefined,
			site.friendlyUrlPath
		);

		const menuItemNames = (
			await page
				.locator('#main-content')
				.getByRole('menuitem')
				.allInnerTexts()
		).map((menuItemText) => menuItemText.trim());

		const consentManagerIndex = menuItemNames.indexOf('Consent Manager');

		expect(consentManagerIndex).toBeGreaterThanOrEqual(0);
		expect(menuItemNames.indexOf('Cookie Banner')).toBe(
			consentManagerIndex + 1
		);
		expect(menuItemNames.indexOf('Cookie Panel')).toBe(
			consentManagerIndex + 2
		);
	}
);

test(
	'Enabling alone does not render the Cookies Banner',
	{tag: '@LPD-87281'},
	async ({
		browser,
		consentManagerConfigurationPage,
		page,
		systemSettingsPage,
	}) => {
		await test.step('Enable Consent Manager leaving active off', async () => {
			await updateConsentManagerConfiguration(page, {
				active: false,
				enabled: true,
				forceReload: true,
			});
		});

		await test.step('Verify the Cookies Banner is not rendered for a guest user', async () => {
			const guestPage = await browser.newPage();

			await guestPage.goto('/');

			await expect(
				guestPage.locator('.cookies-banner')
			).not.toBeVisible();

			await guestPage.close();
		});

		await test.step('Verify the Activate button is visible on the Consent Manager tab', async () => {
			await consentManagerConfigurationPage.goTo();

			await expect(
				consentManagerConfigurationPage.toggleActivateButton
			).toBeVisible();
		});

		await test.step('Verify Cookie Banner and Cookie Panel sub-tabs are visible under Privacy', async () => {
			await systemSettingsPage.goToSystemSetting(
				'Privacy',
				'Consent Manager'
			);

			const privacyMenu =
				systemSettingsPage.page.locator('#main-content');

			await expect(
				privacyMenu.getByRole('menuitem', {
					exact: true,
					name: 'Cookie Banner',
				})
			).toBeVisible();
			await expect(
				privacyMenu.getByRole('menuitem', {
					exact: true,
					name: 'Cookie Panel',
				})
			).toBeVisible();
		});
	}
);

test(
	'Enabling Consent Manager from Site Settings saves successfully and shows the banner on the site',
	{tag: '@LPD-92656'},
	async ({page, site, siteSettingsPage}) => {
		await test.step('Navigate to Site Settings > Privacy > Consent Manager', async () => {
			await siteSettingsPage.goToSiteSetting(
				'Privacy',
				'Consent Manager',
				site.friendlyUrlPath
			);
		});

		await test.step('Enable Consent Manager with a custom floating icon', async () => {
			await page
				.getByRole('checkbox', {exact: true, name: 'Enabled'})
				.check();

			await expect(
				page.getByRole('checkbox', {
					exact: true,
					name: 'Floating Icon Enabled',
				})
			).toBeEnabled();

			await page
				.getByRole('checkbox', {
					exact: true,
					name: 'Floating Icon Enabled',
				})
				.check();

			await page.getByText('Custom', {exact: true}).click();

			const fileChooserPromise = page.waitForEvent('filechooser');

			await page
				.getByRole('button', {name: 'Change Custom Icon'})
				.click();

			const uploadImageFrame = page.frameLocator(
				'iframe[title="Upload Custom Icon"]'
			);

			await uploadImageFrame
				.getByRole('button', {name: 'Select Image'})
				.click();

			const fileChooser = await fileChooserPromise;

			await fileChooser.setFiles(
				path.join(__dirname, 'dependencies/liferay.png')
			);

			await uploadImageFrame.getByRole('button', {name: 'Done'}).click();

			await siteSettingsPage.saveConfiguration();
		});

		await test.step('Activate the Consent Manager', async () => {
			await page
				.getByRole('button', {exact: true, name: 'Activate'})
				.click();

			await waitForAlert(page);
		});

		await test.step('Verify the Consent Manager is now active', async () => {
			await expect(
				page.getByRole('button', {exact: true, name: 'Deactivate'})
			).toBeVisible();

			await expect(page.locator('.cookies-banner')).toBeVisible();
		});

		await test.step('Reload and verify the custom floating icon persists under GROUP scope', async () => {
			await page.reload();

			const floatingIconButton = page.locator(
				'#_com_liferay_cookies_banner_web_portlet_CookiesBannerPortlet_floatingIconButton'
			);

			const imageSrc = await floatingIconButton.getAttribute('src');

			expect(imageSrc.includes('/image/floating_icon?')).toBeTruthy();
		});
	}
);

test(
	'Forced Re-Consent confirmation message changes depending on the active state',
	{tag: '@LPD-87281'},
	async ({consentManagerConfigurationPage, page}) => {
		await test.step('Enable Consent Manager leaving active off', async () => {
			await updateConsentManagerConfiguration(page, {
				active: false,
				enabled: true,
				forceReload: true,
			});

			await expect(
				consentManagerConfigurationPage.toggleActivateButton
			).toBeVisible();
		});

		await test.step('Verify message references the renewal period when inactive', async () => {
			let dialogMessage = '';

			page.once('dialog', async (dialogWindow) => {
				dialogMessage = dialogWindow.message();

				await dialogWindow.dismiss();
			});

			await consentManagerConfigurationPage.forcedReconsentButton.click();

			await expect
				.poll(() => dialogMessage)
				.toContain('change the consent renewal period');
			expect(dialogMessage).toContain('when the cookie banner is active');
		});

		await test.step('Activate Consent Manager', async () => {
			await consentManagerConfigurationPage.toggleActivateButton.click();

			await waitForAlert(page);

			await expect(
				consentManagerConfigurationPage.toggleDeactivateButton
			).toBeVisible();
		});

		await test.step('Verify message references force re-consent when active', async () => {
			let dialogMessage = '';

			page.once('dialog', async (dialogWindow) => {
				dialogMessage = dialogWindow.message();

				await dialogWindow.dismiss();
			});

			await consentManagerConfigurationPage.forcedReconsentButton.click();

			await expect
				.poll(() => dialogMessage)
				.toContain('force the re-consent');
		});
	}
);

test(
	'Saving Cookie Banner configuration while active shows the modal with an optional unchecked checkbox',
	{tag: '@LPD-87281'},
	async ({consentManagerConfigurationPage, page}) => {
		await test.step('Enable and activate Consent Manager', async () => {
			await updateConsentManagerConfiguration(page, {
				enabled: true,
				forceReload: true,
			});

			await expect(
				consentManagerConfigurationPage.toggleDeactivateButton
			).toBeVisible();
		});

		await test.step('Decline cookies banner so it stops covering the form', async () => {
			const banner = page.locator('.cookies-banner');

			await banner.waitFor({state: 'visible'});

			await banner.getByRole('button', {name: 'Decline All'}).click();

			await banner.waitFor({state: 'hidden'});
		});

		await test.step('Navigate to Cookie Banner configuration and change a value', async () => {
			await page
				.locator('#main-content')
				.getByRole('menuitem', {exact: true, name: 'Cookie Banner'})
				.click();

			await page
				.getByLabel('Privacy Policy Link')
				.fill('http://example.com');

			await page.getByLabel('Include "Decline All" Button').uncheck();

			const saveButton = page.getByRole('button', {name: 'Save'});
			const updateButton = page.getByRole('button', {name: 'Update'});

			if (await saveButton.isVisible()) {
				await saveButton.click();
			}
			else {
				await updateButton.click();
			}
		});

		await test.step('Verify modal shows with the checkbox unchecked and clickable', async () => {
			const modal = page.getByRole('alertdialog');

			await expect(modal).toContainText(
				'These changes will take effect immediately'
			);

			const checkbox = modal.getByRole('checkbox', {
				name: /force re-consent/i,
			});

			await expect(checkbox).not.toBeChecked();
			await expect(checkbox).toBeEnabled();

			await checkbox.check();
			await expect(checkbox).toBeChecked();

			await checkbox.uncheck();
			await expect(checkbox).not.toBeChecked();

			await modal.getByRole('button', {name: 'Cancel'}).click();

			await modal.waitFor({state: 'hidden'});
		});
	}
);

test(
	'Saving Cookie Panel configuration while active shows the modal with an optional unchecked checkbox',
	{tag: '@LPD-87281'},
	async ({consentManagerConfigurationPage, page}) => {
		await test.step('Enable and activate Consent Manager', async () => {
			await updateConsentManagerConfiguration(page, {
				enabled: true,
				forceReload: true,
			});

			await expect(
				consentManagerConfigurationPage.toggleDeactivateButton
			).toBeVisible();
		});

		await test.step('Decline cookies banner so it stops covering the form', async () => {
			const banner = page.locator('.cookies-banner');

			await banner.waitFor({state: 'visible'});

			await banner.getByRole('button', {name: 'Decline All'}).click();

			await banner.waitFor({state: 'hidden'});
		});

		await test.step('Navigate to Cookie Panel configuration and change a value', async () => {
			await page
				.locator('#main-content')
				.getByRole('menuitem', {exact: true, name: 'Cookie Panel'})
				.click();

			await page
				.getByLabel('Cookie Policy Link')
				.fill('http://example.com');

			await page.getByLabel('Hide from end-user').first().check();

			const saveButton = page.getByRole('button', {name: 'Save'});
			const updateButton = page.getByRole('button', {name: 'Update'});

			if (await saveButton.isVisible()) {
				await saveButton.click();
			}
			else {
				await updateButton.click();
			}
		});

		await test.step('Verify modal shows with the checkbox unchecked and clickable', async () => {
			const modal = page.getByRole('alertdialog');

			await expect(modal).toContainText(
				'These changes will take effect immediately'
			);

			const checkbox = modal.getByRole('checkbox', {
				name: /force re-consent/i,
			});

			await expect(checkbox).not.toBeChecked();
			await expect(checkbox).toBeEnabled();

			await checkbox.check();
			await expect(checkbox).toBeChecked();

			await checkbox.uncheck();
			await expect(checkbox).not.toBeChecked();

			await modal.getByRole('button', {name: 'Cancel'}).click();

			await modal.waitFor({state: 'hidden'});
		});
	}
);

test(
	'Saving while active shows a confirmation modal that can be cancelled or confirmed',
	{tag: '@LPD-87281'},
	async ({consentManagerConfigurationPage, page}) => {
		await test.step('Enable and activate Consent Manager', async () => {
			await updateConsentManagerConfiguration(page, {
				enabled: true,
				forceReload: true,
				storeConsent: false,
			});

			await expect(
				consentManagerConfigurationPage.toggleDeactivateButton
			).toBeVisible();
		});

		await test.step('Decline cookies banner so it stops covering the form', async () => {
			const banner = page.locator('.cookies-banner');

			await banner.waitFor({state: 'visible'});

			await banner.getByRole('button', {name: 'Decline All'}).click();

			await banner.waitFor({state: 'hidden'});
		});

		await test.step('Cancel the confirmation modal and verify Store Consent is not persisted', async () => {
			await consentManagerConfigurationPage.storeConsentCheckbox.setChecked(
				true
			);

			await consentManagerConfigurationPage.updateButton.click();

			const modal = page.getByRole('alertdialog');

			await expect(modal).toContainText(
				'These changes will take effect immediately'
			);

			await modal.getByRole('button', {name: 'Cancel'}).click();

			await modal.waitFor({state: 'hidden'});

			await consentManagerConfigurationPage.goTo();

			await expect(
				consentManagerConfigurationPage.storeConsentCheckbox
			).not.toBeChecked();
		});

		await test.step('Confirm the modal with the force re-consent checkbox marked and verify Store Consent is persisted', async () => {
			await consentManagerConfigurationPage.storeConsentCheckbox.setChecked(
				true
			);

			await consentManagerConfigurationPage.updateButton.click();

			const modal = page.getByRole('alertdialog');

			await modal
				.getByRole('checkbox', {name: /force re-consent/i})
				.check();

			await modal.getByRole('button', {name: 'OK'}).click();

			await waitForAlert(page);

			await consentManagerConfigurationPage.goTo();

			await expect(
				consentManagerConfigurationPage.storeConsentCheckbox
			).toBeChecked();
		});

		await test.step('Verify the cookies banner is shown again after force re-consent', async () => {
			await page.goto('/');

			await expect(page.locator('.cookies-banner')).toBeVisible();
		});
	}
);

test(
	'Saving without changes does not show the confirmation modal',
	{tag: '@LPD-87281'},
	async ({consentManagerConfigurationPage, page}) => {
		await test.step('Enable and activate Consent Manager', async () => {
			await updateConsentManagerConfiguration(page, {
				enabled: true,
				forceReload: true,
			});

			await expect(
				consentManagerConfigurationPage.toggleDeactivateButton
			).toBeVisible();
		});

		await test.step('Decline cookies banner so it stops covering the form', async () => {
			const banner = page.locator('.cookies-banner');

			await banner.waitFor({state: 'visible'});

			await banner.getByRole('button', {name: 'Decline All'}).click();

			await banner.waitFor({state: 'hidden'});
		});

		await test.step('Click Update without changes and verify no modal appears', async () => {
			await consentManagerConfigurationPage.updateButton.click();

			const modal = page.getByRole('alertdialog');

			await expect(modal).not.toBeVisible();

			await waitForAlert(page);
		});
	}
);

async function toggleActiveAndWait(
	consentManagerConfigurationPage: ConsentManagerConfigurationPage
) {
	const {page, toggleActivateButton, toggleDeactivateButton} =
		consentManagerConfigurationPage;

	if (await toggleDeactivateButton.isVisible()) {
		page.once('dialog', async (dialogWindow) => {
			await dialogWindow.accept();
		});

		await toggleDeactivateButton.click();
	}
	else {
		await toggleActivateButton.click();
	}

	await waitForAlert(page);
}
