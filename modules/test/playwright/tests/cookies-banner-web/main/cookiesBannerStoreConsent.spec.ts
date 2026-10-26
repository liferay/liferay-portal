/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {accountSettingsPagesTest} from '../../../fixtures/accountSettingsPagesTest';
import {apiHelpersTest} from '../../../fixtures/apiHelpersTest';
import {consentManagerConfigurationPageTest} from '../../../fixtures/consentManagerConfigurationPageTest';
import {loginTest} from '../../../fixtures/loginTest';
import {systemSettingsPageTest} from '../../../fixtures/systemSettingsPageTest';
import {usersAndOrganizationsPagesTest} from '../../../fixtures/usersAndOrganizationsPagesTest';
import {getRandomInt} from '../../../utils/getRandomInt';
import performLogin, {
	performLogout,
	userData,
} from '../../../utils/performLogin';
import {
	clearConsentCookies,
	resetConsentManagerConfiguration,
	updateConsentManagerConfiguration,
} from './utils/consentManagerConfigurationHelper';

const cookieKeys = [
	'CONSENT_TYPE_FUNCTIONAL',
	'CONSENT_TYPE_NECESSARY',
	'CONSENT_TYPE_PERFORMANCE',
	'CONSENT_TYPE_PERSONALIZATION',
	'USER_CONSENT_CONFIGURED',
];

export const test = mergeTests(
	accountSettingsPagesTest,
	apiHelpersTest,
	consentManagerConfigurationPageTest,
	loginTest(),
	systemSettingsPageTest,
	usersAndOrganizationsPagesTest
);

let userAccount = undefined;

test.afterEach(async ({apiHelpers, page, systemSettingsPage}) => {
	if (await page.getByRole('button', {name: 'Sign In'}).isVisible()) {
		await performLogin(page, 'test');
	}

	if (userAccount !== undefined) {
		await apiHelpers.headlessAdminUser.deleteUserAccount(userAccount.id);

		userAccount = undefined;
	}

	await test.step('Reset Consent Manager Configuration', async () => {
		await resetConsentManagerConfiguration(systemSettingsPage);
	});

	await test.step('Clear Consent Cookies if present', async () => {
		await clearConsentCookies(systemSettingsPage.page);
	});
});

test.beforeEach(async ({page}) => {
	await test.step('Enable Consent Manager', async () => {
		await updateConsentManagerConfiguration(page, {
			enabled: true,
			forceReload: true,
		});
	});

	await test.step('Verify Cookies Banner appears, then Accept All cookies', async () => {
		const cookiesBanner = page.locator('div[role="region"].cookies-banner');

		await expect(cookiesBanner).toBeVisible();

		await page.getByRole('button', {name: 'Accept All'}).click();
	});
});

test(
	'Store Consent configuration field validation',
	{tag: '@LPD-78076'},
	async ({consentManagerConfigurationPage}) => {
		await test.step('Validate Store Consent field is not enabled by default', async () => {
			await expect(
				consentManagerConfigurationPage.storeConsentCheckbox
			).not.toBeChecked();
		});

		await test.step('Verify Store Consent field can be saved', async () => {
			await updateConsentManagerConfiguration(
				consentManagerConfigurationPage.page,
				{
					enabled: true,
					storeConsent: true,
				}
			);

			await expect(
				consentManagerConfigurationPage.storeConsentCheckbox
			).toBeChecked();
		});
	}
);

test(
	'Verify consent preferences set as a guest user are not used after authentication if the user has previously stored consent preferences AC3/TC2',
	{tag: '@LPD-76011'},
	async ({apiHelpers, browser, page}) => {
		await test.step('Enable Store Consent', async () => {
			await updateConsentManagerConfiguration(page, {
				enabled: true,
				storeConsent: true,
			});
		});

		const userId = getRandomInt();

		await test.step('Create new user', async () => {
			userAccount = await apiHelpers.headlessAdminUser.postUserAccount(
				undefined,
				userId
			);

			// Add user info to userData const so we can authenticate via performLogin

			userData[userAccount.alternateName] = {
				name: userAccount.givenName,
				password: 'test',
				surname: userAccount.familyName,
			};
		});

		let newUserPage = await browser.newPage();

		await test.step('Sign in as new user, store and accept consent preferences', async () => {
			await performLogin(newUserPage, userAccount.alternateName);

			await newUserPage
				.locator('div[role="region"].cookies-banner')
				.waitFor();

			await newUserPage.getByLabel('Store Consent').setChecked(true);

			await newUserPage.getByRole('button', {name: 'Accept All'}).click();
		});

		await test.step('As a guest user, decline all consent preference cookies', async () => {
			newUserPage = await browser.newPage();

			await newUserPage.goto('/');

			await expect(
				newUserPage.getByRole('button', {name: 'Sign In'})
			).toBeVisible();

			await expect(
				newUserPage.locator('div[role="region"].cookies-banner')
			).toBeVisible();

			await newUserPage
				.getByRole('button', {name: 'Decline All'})
				.click();
		});

		await test.step('In the same browser window, sign in as the new user, verify cookies banner does not appear and previously stored consent preference cookies are present', async () => {
			await performLogin(newUserPage, userAccount.alternateName);

			await expect(
				newUserPage.locator('div[role="region"].cookies-banner')
			).not.toBeVisible();

			await newUserPage.reload();

			await newUserPage.waitForTimeout(1000);

			const cookies = await newUserPage.context().cookies();

			for (const cookieKey of cookieKeys) {
				const cookie = cookies.find(
					(cookie) => cookie.name === cookieKey
				);

				expect(cookie).toBeDefined();
				expect(cookie.value).toBe('true');
			}
		});
	}
);

test(
	'Verify consent preferences set as a guest user are used after authentication if the user has no previously stored consent preferences AC3/TC1',
	{tag: '@LPD-76011'},
	async ({apiHelpers, browser, page}) => {
		await test.step('Enable Store Consent', async () => {
			await updateConsentManagerConfiguration(page, {
				enabled: true,
				storeConsent: true,
			});
		});

		const userId = getRandomInt();

		await test.step('Create new user', async () => {
			userAccount = await apiHelpers.headlessAdminUser.postUserAccount(
				undefined,
				userId
			);

			// Add user info to userData const so we can authenticate via performLogin

			userData[userAccount.alternateName] = {
				name: userAccount.givenName,
				password: 'test',
				surname: userAccount.familyName,
			};
		});

		let newUserPage = await browser.newPage();

		await test.step('As a guest user, accept all consent preference cookies', async () => {
			newUserPage = await browser.newPage();

			await newUserPage.goto('/');

			await expect(
				newUserPage.getByRole('button', {name: 'Sign In'})
			).toBeVisible();

			await expect(
				newUserPage.locator('div[role="region"].cookies-banner')
			).toBeVisible();

			await newUserPage.getByRole('button', {name: 'Accept All'}).click();
		});

		await test.step('Sign in as new user, verify cookies banner does not appear and cookies are present', async () => {
			await performLogin(newUserPage, userAccount.alternateName);

			await expect(
				newUserPage.locator('div[role="region"].cookies-banner')
			).not.toBeVisible();

			const cookies = await newUserPage.context().cookies();

			for (const cookieKey of cookieKeys) {
				const cookie = cookies.find(
					(cookie) => cookie.name === cookieKey
				);

				expect(cookie).toBeDefined();
			}
		});
	}
);

test(
	'Verify consent preferences set as an authenticated user are removed from the browser after logout AC5/TC1',
	{tag: '@LPD-76011'},
	async ({page}) => {
		await test.step('Verify all consent cookies are set', async () => {
			const cookies = await page.context().cookies();

			for (const cookieKey of cookieKeys) {
				const cookie = cookies.find(
					(cookie) => cookie.name === cookieKey
				);

				expect(cookie).toBeDefined();
			}
		});

		await performLogout(page);

		await test.step('Verify no consent cookies are set', async () => {
			await page.reload();

			await page.waitForLoadState();

			const cookies = await page.context().cookies();

			for (const cookieKey of cookieKeys) {
				const cookie = cookies.find(
					(cookie) => cookie.name === cookieKey
				);

				expect(cookie).toBeUndefined();
			}
		});
	}
);

test(
	'Verify cookies banner does not appear after login if user has previously stored consent preferences AC2/TC2',
	{tag: '@LPD-76011'},
	async ({apiHelpers, browser, page}) => {
		await test.step('Enable Store Consent', async () => {
			await updateConsentManagerConfiguration(page, {
				enabled: true,
				storeConsent: true,
			});
		});

		const userId = getRandomInt();

		await test.step('Create new user', async () => {
			userAccount = await apiHelpers.headlessAdminUser.postUserAccount(
				undefined,
				userId
			);

			// Add user info to userData const so we can authenticate via performLogin

			userData[userAccount.alternateName] = {
				name: userAccount.givenName,
				password: 'test',
				surname: userAccount.familyName,
			};
		});

		let newUserPage = await browser.newPage();

		await test.step('Sign in as new user, store and accept consent preferences', async () => {
			await performLogin(newUserPage, userAccount.alternateName);

			await newUserPage
				.locator('div[role="region"].cookies-banner')
				.waitFor();

			await newUserPage.getByLabel('Store Consent').setChecked(true);

			await newUserPage.getByRole('button', {name: 'Accept All'}).click();
		});

		await test.step('In a new window as guest user, verify cookies banner appears and no cookies are set', async () => {
			newUserPage = await browser.newPage();

			await newUserPage.goto('/');

			await expect(
				newUserPage.getByRole('button', {name: 'Sign In'})
			).toBeVisible();

			await expect(
				newUserPage.locator('div[role="region"].cookies-banner')
			).toBeVisible();

			const cookies = await newUserPage.context().cookies();

			for (const cookieKey of cookieKeys) {
				const cookie = cookies.find(
					(cookie) => cookie.name === cookieKey
				);

				expect(cookie).toBeUndefined();
			}
		});

		await test.step('Sign in as new user, verify cookies banner does not appear and cookies are present', async () => {
			await performLogin(newUserPage, userAccount.alternateName);

			await expect(
				newUserPage.locator('div[role="region"].cookies-banner')
			).not.toBeVisible();

			const cookies = await newUserPage.context().cookies();

			for (const cookieKey of cookieKeys) {
				const cookie = cookies.find(
					(cookie) => cookie.name === cookieKey
				);

				expect(cookie).toBeDefined();
			}
		});
	}
);

test(
	'Verify enabling Store Consent configuration shows Store Consent option across all consent manager pages',
	{tag: '@LPD-76011'},
	async ({accountSettingsPage, page}) => {
		await test.step('Enable Store Consent', async () => {
			await updateConsentManagerConfiguration(page, {
				enabled: true,
				storeConsent: true,
			});
		});

		await test.step('Clear consent cookies so banner appears', async () => {
			await clearConsentCookies(page);
		});

		await test.step('Verify Store Consent option is present in cookies banner', async () => {
			await page.goto('/');

			const cookiesBanner = page.locator(
				'div[role="region"].cookies-banner'
			);

			await cookiesBanner.waitFor();

			await expect(
				cookiesBanner.getByLabel('Store Consent')
			).toBeVisible();
		});

		await test.step('Verify Store Consent option is present in cookies banner configuration', async () => {
			await page.getByRole('button', {name: 'Configuration'}).click();

			const cookieConfigurationIFrame = page.frameLocator(
				'iframe[title="Cookie Configuration"]'
			);

			const storeConsentHeading = cookieConfigurationIFrame.getByRole(
				'heading',
				{
					name: 'Store Consent',
				}
			);

			await expect(storeConsentHeading).toBeVisible();

			await page
				.getByRole('dialog', {name: 'Cookie Configuration'})
				.getByLabel('Close')
				.click();
		});

		await test.step('Verify Store Consent option is present in Data and Privacy tab', async () => {
			await accountSettingsPage.goToDataAndPrivacy();

			await accountSettingsPage.page
				.getByText('Consent Manager')
				.first()
				.waitFor();

			const storeConsentHeading = accountSettingsPage.page.getByRole(
				'heading',
				{
					name: 'Store Consent',
				}
			);

			await expect(storeConsentHeading).toBeVisible();
		});
	}
);

test(
	'Verify when the user does not Store Consent, their consent preferences are lost when clearing cookies AC1/TC2',
	{tag: '@LPD-76011'},
	async ({page}) => {
		await test.step('Enable Store Consent', async () => {
			await updateConsentManagerConfiguration(page, {
				enabled: true,
				storeConsent: true,
			});
		});

		await test.step('Clear consent cookies so banner appears', async () => {
			await clearConsentCookies(page);
		});

		const cookiesBanner = page.locator('div[role="region"].cookies-banner');

		await test.step('Accept all consent preference cookies without storing consent', async () => {
			await page.goto('/');

			await cookiesBanner.waitFor();

			await page.getByRole('button', {name: 'Accept All'}).click();
		});

		await test.step('Verify clearing cookies and refreshing the page makes the cookies banner appear again', async () => {
			await clearConsentCookies(page);

			await page.goto('/');

			await expect(cookiesBanner).toBeVisible();
		});
	}
);

test(
	'Verify when the user does Store Consent, their consent preferences are reloaded after clearing cookies AC1/TC1',
	{tag: '@LPD-76011'},
	async ({page}) => {
		await test.step('Enable Store Consent', async () => {
			await updateConsentManagerConfiguration(page, {
				enabled: true,
				storeConsent: true,
			});
		});

		await test.step('Clear consent cookies so banner appears', async () => {
			await clearConsentCookies(page);
		});

		const cookiesBanner = page.locator('div[role="region"].cookies-banner');

		await test.step('Accept all consent preference cookies and store consent', async () => {
			await page.goto('/');

			await cookiesBanner.waitFor();

			await page.getByLabel('Store Consent').setChecked(true);

			await page.getByRole('button', {name: 'Accept All'}).click();
		});

		await test.step('Verify clearing cookies and refreshing the page does not make the cookies banner appear again', async () => {
			await page.context().clearCookies({name: /^CONSENT_TYPE_/});
			await page
				.context()
				.clearCookies({name: /^USER_CONSENT_CONFIGURED/});

			await page.goto('/');

			await expect(cookiesBanner).not.toBeVisible();
		});
	}
);

test(
	'Verify when the user does Store Consent, their consent preferences can be anonymized via Personal Data Erasure AC2/TC1, AC4/TC1',
	{tag: '@LPD-76011'},
	async ({apiHelpers, browser, page, usersAndOrganizationsPage}) => {
		await test.step('Enable Store Consent', async () => {
			await updateConsentManagerConfiguration(page, {
				enabled: true,
				storeConsent: true,
			});
		});

		const userId = getRandomInt();

		await test.step('Create new user', async () => {
			userAccount = await apiHelpers.headlessAdminUser.postUserAccount(
				undefined,
				userId
			);

			// Add user info to userData const so we can authenticate via performLogin

			userData[userAccount.alternateName] = {
				name: userAccount.givenName,
				password: 'test',
				surname: userAccount.familyName,
			};
		});

		await test.step('Sign in as new user, store and accept consent preferences', async () => {
			const newUserPage = await browser.newPage();

			await performLogin(newUserPage, userAccount.alternateName);

			await newUserPage
				.locator('div[role="region"].cookies-banner')
				.waitFor();

			await newUserPage.getByLabel('Store Consent').setChecked(true);

			await newUserPage.getByRole('button', {name: 'Accept All'}).click();
		});

		await test.step('As admin, delete new user personal data', async () => {
			await usersAndOrganizationsPage.goToUsers();

			usersAndOrganizationsPage.page.once('dialog', async (dialog) => {
				await dialog.accept();
			});

			await (
				await usersAndOrganizationsPage.usersTableRowActions(
					userAccount.alternateName
				)
			).click();

			await usersAndOrganizationsPage.deletePersonalDataMenuItem.click();
		});

		await test.step('Verify consent preferences appear in personal data erasure page', async () => {
			await expect(
				usersAndOrganizationsPage.page.getByRole('cell', {
					name: 'Consent Preferences',
				})
			).toBeVisible();
		});
	}
);

test(
	'Verify when the user has previously stored consent preferences, they are removed when unchecking the Store Consent toggle AC1/TC3',
	{tag: '@LPD-76011'},
	async ({accountSettingsPage, page}) => {
		await test.step('Enable Store Consent', async () => {
			await updateConsentManagerConfiguration(page, {
				enabled: true,
				storeConsent: true,
			});
		});

		await test.step('Clear consent cookies so banner appears', async () => {
			await clearConsentCookies(page);
		});

		const cookiesBanner = page.locator('div[role="region"].cookies-banner');

		await test.step('Accept all consent preference cookies and store consent', async () => {
			await page.goto('/');

			await cookiesBanner.waitFor();

			await page.getByLabel('Store Consent').setChecked(true);

			await page.getByRole('button', {name: 'Accept All'}).click();
		});

		await test.step('Uncheck the Store Consent option toggle in Data and Privacy tab', async () => {
			await accountSettingsPage.goToDataAndPrivacy();

			await accountSettingsPage.page
				.getByText('Consent Manager')
				.first()
				.waitFor();

			const storeConsentToggle = accountSettingsPage.page.locator(
				'[id="_com_liferay_my_account_web_portlet_MyAccountPortlet_storeConsent"]'
			);

			await expect(storeConsentToggle).toBeChecked();

			await storeConsentToggle.setChecked(false);
		});

		await test.step('Verify clearing cookies and refreshing the page makes the cookies banner appear again', async () => {
			await clearConsentCookies(page);

			await page.goto('/');

			await expect(cookiesBanner).toBeVisible();
		});
	}
);
