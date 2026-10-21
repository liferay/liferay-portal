/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Page, expect, mergeTests} from '@playwright/test';

import {accessibilityMenuPagesTest} from '../../../fixtures/accessibilityMenuPagesTest';
import {apiHelpersTest} from '../../../fixtures/apiHelpersTest';
import {featureFlagsTest} from '../../../fixtures/featureFlagsTest';
import {instanceSettingsPagesTest} from '../../../fixtures/instanceSettingsPagesTest';
import {isolatedSiteTest} from '../../../fixtures/isolatedSiteTest';
import {loginTest} from '../../../fixtures/loginTest';
import {siteSettingsPagesTest} from '../../../fixtures/siteSettingsPagesTest';
import {systemSettingsPageTest} from '../../../fixtures/systemSettingsPageTest';
import {virtualInstancesPagesTest} from '../../../fixtures/virtualInstancesPagesTest';
import {liferayConfig} from '../../../liferay.config';
import {AccessibilityMenuPage} from '../../../pages/accessibility-menu-web/AccessibilityMenuPage';
import {getRandomInt} from '../../../utils/getRandomInt';
import performLogin from '../../../utils/performLogin';

const test = mergeTests(
	accessibilityMenuPagesTest,
	apiHelpersTest,
	isolatedSiteTest,
	siteSettingsPagesTest,
	systemSettingsPageTest,
	featureFlagsTest({
		'LPS-178052': {enabled: true},
	}),
	instanceSettingsPagesTest,
	virtualInstancesPagesTest,
	loginTest()
);

const DEFAULT_VIRTUAL_INSTANCE_NAME = 'www.able.com';

const FIRST_SITE_NAME = `First Site ${getRandomInt()}`;
const SECOND_SITE_NAME = `Second Site ${getRandomInt()}`;

const SETTING_CATEGORY_KEY = 'Accessibility';
const SETTING_CONFIGURATION_NAME = 'Accessibility Menu';

async function expectAccessibilityMenuToBeAvailableFromUserProfileMenu(
	page: Page,
	expectedStatus: 'visible' | 'hidden' = 'visible'
) {
	const userProfileTrigger = page.getByTestId('userPersonalMenu');
	const accessibilityMenuItem = page.getByRole('menuitem', {
		name: SETTING_CONFIGURATION_NAME,
	});

	await expect(userProfileTrigger).toBeVisible();

	await userProfileTrigger.click();

	if (expectedStatus === 'visible') {
		await expect(accessibilityMenuItem).toBeVisible();

		await accessibilityMenuItem.click();

		const accessibilityModal = page
			.locator('.modal')
			.getByLabel(SETTING_CONFIGURATION_NAME);

		await accessibilityModal.waitFor();

		await expect(accessibilityModal).toBeVisible();

		await accessibilityModal.getByLabel('Close').click();

		await expect(accessibilityModal).toBeHidden();
	}
	else if (expectedStatus === 'hidden') {
		await expect(
			page.getByRole('menuitem', {name: 'Sign Out'})
		).toBeVisible();

		await expect(accessibilityMenuItem).toBeHidden();

		await page.keyboard.press('Escape');
	}
}

async function expectAccessibilityMenuToBeAvailableFromKeyboard(
	accessibilityMenuPage: AccessibilityMenuPage
) {
	await accessibilityMenuPage.openAccessibilityMenu();

	await accessibilityMenuPage.closeButton.click();
}

test.afterEach(async ({accessibilityMenuPage, systemSettingsPage}) => {
	await systemSettingsPage.goToSystemSetting(
		SETTING_CATEGORY_KEY,
		SETTING_CONFIGURATION_NAME
	);

	await expect(
		accessibilityMenuPage.enableAccessibilityMenuCheckbox
	).toBeVisible();

	if (
		await accessibilityMenuPage.enableAccessibilityMenuCheckbox.isChecked()
	) {
		await systemSettingsPage.resetToDefaultValues();
	}
});

test(
	'Verifies that the user can enable it by Site Settings',
	{tag: '@LPS-178192'},
	async ({accessibilityMenuPage, site, siteSettingsPage}) => {
		await test.step('When navigate to Site Settings > Accessibility And turn on Enable Accessibility Menu', async () => {
			await siteSettingsPage.goToSiteSetting(
				SETTING_CATEGORY_KEY,
				SETTING_CONFIGURATION_NAME,
				site.friendlyUrlPath
			);

			await accessibilityMenuPage.enableAccessibilityMenu();
		});

		await test.step('Then Accessibility Menu can be reached by keyboard', async () => {
			await expectAccessibilityMenuToBeAvailableFromKeyboard(
				accessibilityMenuPage
			);
		});
	}
);

test(
	'Verifies that the user can enable it by System Settings',
	{tag: '@LPS-178192'},
	async ({accessibilityMenuPage, systemSettingsPage}) => {
		await test.step('When navigate to Site Settings > Accessibility And turn on Enable Accessibility Menu', async () => {
			await systemSettingsPage.goToSystemSetting(
				SETTING_CATEGORY_KEY,
				SETTING_CONFIGURATION_NAME
			);

			await accessibilityMenuPage.enableAccessibilityMenu();
		});

		await test.step('Then Accessibility Menu can be reached by keyboard', async () => {
			await expectAccessibilityMenuToBeAvailableFromKeyboard(
				accessibilityMenuPage
			);
		});
	}
);

test.describe('Accessibility Menu Configuration Override and Inheritance', () => {
	test.slow();

	let firstSite: Site;
	let secondSite: Site;
	let virtualInstancePage: Page;

	test.beforeEach(async ({apiHelpers, browser, virtualInstancesPage}) => {
		await test.step('Create new virtual instance, new first site and new second site', async () => {
			await virtualInstancesPage.addNewVirtualInstance(
				DEFAULT_VIRTUAL_INSTANCE_NAME
			);

			virtualInstancePage = await browser.newPage({
				baseURL: `http://${DEFAULT_VIRTUAL_INSTANCE_NAME}:${liferayConfig.environment.port}`,
			});

			await performLogin(
				virtualInstancePage,
				'test',
				'',
				`@${DEFAULT_VIRTUAL_INSTANCE_NAME}.com`
			);

			[firstSite, secondSite] = await Promise.all(
				[FIRST_SITE_NAME, SECOND_SITE_NAME].map(
					async (name) =>
						await apiHelpers.headlessAdminSite.postSite({name})
				)
			);
		});
	});

	test.afterEach(
		async ({
			apiHelpers,
			instanceSettingsPage,
			systemSettingsPage,
			virtualInstancesPage,
		}) => {
			test.setTimeout(180000);

			await systemSettingsPage.goToSystemSetting(
				SETTING_CATEGORY_KEY,
				SETTING_CONFIGURATION_NAME
			);
			await systemSettingsPage.resetToDefaultValues();

			await instanceSettingsPage.goToInstanceSetting(
				SETTING_CATEGORY_KEY,
				SETTING_CONFIGURATION_NAME
			);
			await instanceSettingsPage.resetInstanceSetting();

			await Promise.all(
				[firstSite, secondSite].map((site) =>
					apiHelpers.headlessAdminSite.deleteSite(
						site.externalReferenceCode
					)
				)
			);

			if (virtualInstancePage) {
				await virtualInstancePage.close();
			}

			await virtualInstancesPage.deleteVirtualInstance(
				DEFAULT_VIRTUAL_INSTANCE_NAME
			);
		}
	);

	test(
		'Verifies that settings can be overridden',
		{tag: '@LPS-178192'},
		async ({
			accessibilityMenuPage,
			instanceSettingsPage,
			page,
			siteSettingsPage,
			systemSettingsPage,
		}) => {
			await test.step('When enable Accessibility Menu on first site settings', async () => {
				await siteSettingsPage.goToSiteSetting(
					SETTING_CATEGORY_KEY,
					SETTING_CONFIGURATION_NAME,
					firstSite.friendlyUrlPath
				);

				await accessibilityMenuPage.enableAccessibilityMenu();
			});

			await test.step('Then Accessibility Menu is available on first site', async () => {
				await expectAccessibilityMenuToBeAvailableFromUserProfileMenu(
					page
				);
			});

			await test.step('And Accessibility Menu is not available on second site', async () => {
				await siteSettingsPage.goto(secondSite.friendlyUrlPath);

				await expectAccessibilityMenuToBeAvailableFromUserProfileMenu(
					page,
					'hidden'
				);
			});

			await test.step('When enable Accessibility Menu on instance ', async () => {
				await instanceSettingsPage.goToInstanceSetting(
					SETTING_CATEGORY_KEY,
					SETTING_CONFIGURATION_NAME
				);

				await accessibilityMenuPage.enableAccessibilityMenu();
			});

			await test.step('Then Accessibility Menu is available on first and second site', async () => {
				for (const site of [firstSite, secondSite]) {
					await siteSettingsPage.goto(site.friendlyUrlPath);

					await expectAccessibilityMenuToBeAvailableFromUserProfileMenu(
						page
					);
				}
			});

			await test.step('And Accessibility Menu is not available in the new virtual instance', async () => {
				await expectAccessibilityMenuToBeAvailableFromUserProfileMenu(
					virtualInstancePage,
					'hidden'
				);
			});

			await test.step('When disable Accessibility Menu on first site settings', async () => {
				await siteSettingsPage.goToSiteSetting(
					SETTING_CATEGORY_KEY,
					SETTING_CONFIGURATION_NAME,
					firstSite.friendlyUrlPath
				);

				await accessibilityMenuPage.disableAccessibilityMenu();
			});

			await test.step('Then Accessibility Menu is not available on first site', async () => {
				await expectAccessibilityMenuToBeAvailableFromUserProfileMenu(
					page,
					'hidden'
				);
			});

			await test.step('And Accessibility Menu is available on second site', async () => {
				await siteSettingsPage.goto(secondSite.friendlyUrlPath);

				await expectAccessibilityMenuToBeAvailableFromUserProfileMenu(
					page
				);
			});

			await test.step('When enable Accessibility Menu on System Settings', async () => {
				await systemSettingsPage.goToSystemSetting(
					SETTING_CATEGORY_KEY,
					SETTING_CONFIGURATION_NAME
				);

				await accessibilityMenuPage.enableAccessibilityMenu();
			});

			await test.step('And disable Accessibility Menu on Instance Settings', async () => {
				await instanceSettingsPage.goToInstanceSetting(
					SETTING_CATEGORY_KEY,
					SETTING_CONFIGURATION_NAME
				);

				await accessibilityMenuPage.disableAccessibilityMenu();
			});

			await test.step('Then Accessibility Menu is not available on first site', async () => {
				await siteSettingsPage.goto(firstSite.friendlyUrlPath);

				await expectAccessibilityMenuToBeAvailableFromUserProfileMenu(
					page,
					'hidden'
				);
			});

			await test.step('And Accessibility Menu is available in the new virtual instance', async () => {
				await virtualInstancePage.reload();
				await virtualInstancePage.waitForLoadState('domcontentloaded');

				await expectAccessibilityMenuToBeAvailableFromUserProfileMenu(
					virtualInstancePage
				);
			});

			await test.step('When enable Accessibility Menu on Instance Settings', async () => {
				await instanceSettingsPage.goToInstanceSetting(
					SETTING_CATEGORY_KEY,
					SETTING_CONFIGURATION_NAME
				);

				await accessibilityMenuPage.enableAccessibilityMenu();
			});

			await test.step('Then Accessibility Menu is not available on first site', async () => {
				await siteSettingsPage.goto(firstSite.friendlyUrlPath);

				await expectAccessibilityMenuToBeAvailableFromUserProfileMenu(
					page,
					'hidden'
				);
			});
		}
	);
});
