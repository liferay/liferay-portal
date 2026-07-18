/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';
import path from 'path';

import {accountSettingsPagesTest} from '../../../fixtures/accountSettingsPagesTest';
import {dataApiHelpersTest} from '../../../fixtures/dataApiHelpersTest';
import {instanceSettingsPagesTest} from '../../../fixtures/instanceSettingsPagesTest';
import {loginTest} from '../../../fixtures/loginTest';
import {usersAndOrganizationsPagesTest} from '../../../fixtures/usersAndOrganizationsPagesTest';
import {liferayConfig} from '../../../liferay.config';
import {clickAndExpectToBeVisible} from '../../../utils/clickAndExpectToBeVisible';
import {getRandomInt} from '../../../utils/getRandomInt';
import {
	performLogout,
	performUserSwitch,
	userData,
} from '../../../utils/performLogin';
import {waitForAlert} from '../../../utils/waitForAlert';

export const test = mergeTests(
	accountSettingsPagesTest,
	dataApiHelpersTest,
	instanceSettingsPagesTest,
	loginTest(),
	usersAndOrganizationsPagesTest
);

test(
	'Can view Not Found when guest access manage URL',
	{tag: ['@LPD-81993', '@LPS-164469']},
	async ({page, personalMenuPage}) => {
		await performLogout(page);

		await page.goto(`${liferayConfig.environment.baseUrl}/manage`);

		await expect(personalMenuPage.notFoundCode).toBeVisible();
		await expect(personalMenuPage.notFoundMessage).toBeVisible();
	}
);

test(
	'Check user display name of My Profile in special simulation',
	{tag: '@LPD-81993'},
	async ({apiHelpers, page, personalMenuPage}) => {
		const randomInt = getRandomInt();

		const userAccount = await apiHelpers.headlessAdminUser.postUserAccount({
			alternateName: `user${randomInt}`,
			emailAddress: `user${randomInt}@liferay.com`,
			familyName:
				'Test with a long lastname in the Liferay Profile Widget',
			givenName: 'Test',
		});

		userData[userAccount.alternateName] = {
			name: userAccount.givenName,
			password: 'test',
			surname: userAccount.familyName,
		};

		await performUserSwitch(page, userAccount.alternateName);

		await personalMenuPage.userPersonalMenuButton.click();
		await personalMenuPage.myProfileMenuItem.click();

		await page.setViewportSize({height: 667, width: 375});

		await expect(personalMenuPage.myProfileDisplayName).toHaveCSS(
			'white-space',
			'normal'
		);

		await page.reload();

		await page.setViewportSize({height: 1024, width: 768});

		await expect(personalMenuPage.myProfileDisplayName).toHaveCSS(
			'white-space',
			'normal'
		);
	}
);

test(
	'Configure look and feel current site and My Dashboard',
	{tag: '@LPD-81993'},
	async ({page, personalMenuInstanceSettingsPage, personalMenuPage}) => {
		await test.step('Set to Current Site and verify apps open in current site', async () => {
			await personalMenuInstanceSettingsPage.goToPersonalMenuSettings();

			await personalMenuInstanceSettingsPage.selectPersonalApplicationsLookAndFeel(
				'Current Site'
			);

			await personalMenuInstanceSettingsPage.save();

			await page.goto('/');
			await page.waitForLoadState('networkidle');

			await clickAndExpectToBeVisible({
				autoClick: true,
				target: personalMenuPage.notificationsMenuItem,
				trigger: personalMenuPage.userPersonalMenuButton,
			});

			await expect(page).not.toHaveURL(/\/user\//);
		});

		await test.step('Set to My Dashboard and verify apps open in user dashboard', async () => {
			await personalMenuInstanceSettingsPage.goToPersonalMenuSettings();

			await personalMenuInstanceSettingsPage.selectPersonalApplicationsLookAndFeel(
				'My Dashboard'
			);

			await personalMenuInstanceSettingsPage.save();

			await page.goto('/');
			await page.waitForLoadState('networkidle');

			await clickAndExpectToBeVisible({
				autoClick: true,
				target: personalMenuPage.notificationsMenuItem,
				trigger: personalMenuPage.userPersonalMenuButton,
			});

			await expect(page).toHaveURL(/\/user\//);
		});
	}
);

test(
	'Configure show in control menu',
	{tag: '@LPD-81993'},
	async ({page, personalMenuInstanceSettingsPage, personalMenuPage}) => {
		await page.goto(liferayConfig.environment.baseUrl);

		await expect(personalMenuPage.controlMenuAvatar).not.toBeVisible();

		await expect(personalMenuPage.personalBarAvatar).toBeVisible();

		await personalMenuInstanceSettingsPage.goToPersonalMenuSettings();

		await personalMenuInstanceSettingsPage.showInControlMenuCheckbox.check();
		await personalMenuInstanceSettingsPage.save();

		try {
			await page.goto(liferayConfig.environment.baseUrl);

			await expect(personalMenuPage.controlMenuAvatar).toBeVisible();
			await expect(personalMenuPage.personalBarAvatar).toBeVisible();
		}
		finally {
			await personalMenuInstanceSettingsPage.goToPersonalMenuSettings();

			await personalMenuInstanceSettingsPage.showInControlMenuCheckbox.uncheck();
			await personalMenuInstanceSettingsPage.save();
		}
	}
);

test(
	'Navigate to My Sites',
	{tag: '@LPD-81993'},
	async ({personalMenuPage}) => {
		await personalMenuPage.userPersonalMenuButton.click();

		await personalMenuPage.mySitesMenuItem.click();

		await expect(personalMenuPage.selectSiteHeading).toBeVisible();
		await expect(personalMenuPage.siteIframeCard).toBeVisible();
	}
);

test(
	'Render apps with current theme',
	{tag: '@LPD-81993'},
	async ({page, personalMenuPage}) => {
		const renderNotifications = async () => {
			await clickAndExpectToBeVisible({
				autoClick: true,
				target: personalMenuPage.notificationsMenuItem,
				trigger: personalMenuPage.userPersonalMenuButton,
			});

			await expect(personalMenuPage.notificationsHeading).toBeVisible();

			await page.reload();

			await expect(personalMenuPage.notificationsHeading).toBeVisible();
		};

		await test.step('Render in the current site theme', async () => {
			await page.goto(liferayConfig.environment.baseUrl);

			await renderNotifications();
		});

		await test.step('Render in the control panel theme', async () => {
			await page.goto(
				`${liferayConfig.environment.baseUrl}/group/control_panel/manage/-/com_liferay_users_admin_web_portlet_UsersAdminPortlet`
			);

			await renderNotifications();
		});
	}
);

test(
	'Update address from user profile',
	{tag: ['@LPD-81993', '@LPS-106534']},
	async ({accountSettingsPage, apiHelpers, page, personalMenuPage}) => {
		const randomInt = getRandomInt();

		const userAccount = await apiHelpers.headlessAdminUser.postUserAccount({
			alternateName: `user${randomInt}`,
			emailAddress: `user${randomInt}@liferay.com`,
			familyName: 'userln',
			givenName: 'userfn',
		});

		userData[userAccount.alternateName] = {
			name: userAccount.givenName,
			password: 'test',
			surname: userAccount.familyName,
		};

		await performUserSwitch(page, userAccount.alternateName);

		await accountSettingsPage.goToAccountSettings();

		await personalMenuPage.contactLink.click();
		await personalMenuPage.contactAddButton.click();

		await personalMenuPage.street1Input.fill('1400 Montefino Ave');
		await personalMenuPage.cityInput.fill('Diamond Bar');
		await personalMenuPage.postalCodeInput.fill('91765');

		await personalMenuPage.countrySelect.selectOption('United States');
		await personalMenuPage.regionSelect.selectOption('California');

		await accountSettingsPage.saveButton.click();

		await waitForAlert(page);

		await personalMenuPage.userPersonalMenuButton.click();
		await personalMenuPage.myProfileMenuItem.click();

		await expect(page.getByText('1400 Montefino Ave')).toBeVisible();
	}
);

test(
	'Update user information',
	{tag: ['@LPD-81993', '@LPS-120825']},
	async ({accountSettingsPage, apiHelpers, page, personalMenuPage}) => {
		const randomInt = getRandomInt();

		const userAccount = await apiHelpers.headlessAdminUser.postUserAccount({
			alternateName: `user${randomInt}`,
			emailAddress: `user${randomInt}@liferay.com`,
			familyName: 'userln',
			givenName: 'userfn',
		});

		userData[userAccount.alternateName] = {
			name: userAccount.givenName,
			password: 'test',
			surname: userAccount.familyName,
		};

		await performUserSwitch(page, userAccount.alternateName);

		await accountSettingsPage.goToAccountSettings();

		const newEmail = `user${getRandomInt()}@liferay.com`;
		const newScreenName = `user${getRandomInt()}`;

		await personalMenuPage.emailAddressInput.fill(newEmail);
		await personalMenuPage.screenNameInput.fill(newScreenName);

		await accountSettingsPage.saveButton.click();

		const confirmFrame = page.frameLocator(
			'iframe[title="Confirm Password"]'
		);

		await confirmFrame.getByLabel('Your Password').fill('test');
		await confirmFrame.getByRole('button', {name: 'Confirm'}).click();

		await waitForAlert(page);

		await expect(personalMenuPage.emailAddressInput).toHaveValue(newEmail);
		await expect(personalMenuPage.screenNameInput).toHaveValue(
			newScreenName
		);
		await expect(personalMenuPage.firstNameInput).toHaveValue('userfn');
		await expect(personalMenuPage.lastNameInput).toHaveValue('userln');
	}
);

test(
	'Update user portrait',
	{tag: ['@LPD-81993', '@LPS-105387']},
	async ({accountSettingsPage, apiHelpers, page, personalMenuPage}) => {
		const randomInt = getRandomInt();

		const userAccount = await apiHelpers.headlessAdminUser.postUserAccount({
			alternateName: `user${randomInt}`,
			emailAddress: `user${randomInt}@liferay.com`,
			familyName: 'userln',
			givenName: 'userfn',
		});

		userData[userAccount.alternateName] = {
			name: userAccount.givenName,
			password: 'test',
			surname: userAccount.familyName,
		};

		await performUserSwitch(page, userAccount.alternateName);

		await expect(personalMenuPage.userDefaultIcon).toBeVisible();

		await accountSettingsPage.goToAccountSettings();

		const fileChooserPromise = page.waitForEvent('filechooser');

		await personalMenuPage.changeImageButton.click();
		await personalMenuPage.uploadImageSelectImageButton.click();

		const fileChooser = await fileChooserPromise;

		await fileChooser.setFiles(
			path.join(__dirname, '/dependencies/liferay.png')
		);

		await personalMenuPage.uploadImageDoneButton.click();
		await accountSettingsPage.saveButton.click();

		await waitForAlert(page);

		await expect(personalMenuPage.userPortrait).toBeVisible();
	}
);

test(
	'Verify back navigation link of personal menu pages',
	{tag: ['@LPD-81993', '@LPS-182444']},
	async ({page, personalMenuPage}) => {
		const menuItems = [
			'Shared Content',
			'My Submissions',
			'My Workflow Tasks',
			'Account Settings',
			'My Connected Applications',
			'My Organizations',
		];

		for (const menuItemName of menuItems) {
			await page.goto(liferayConfig.environment.baseUrl);

			await personalMenuPage.userPersonalMenuButton.click();

			await personalMenuPage.menuItem(menuItemName).click();

			await personalMenuPage.backLink.click();

			if (menuItemName === 'Account Settings') {
				await expect(
					personalMenuPage.usersAndOrganizationsHeading
				).toBeVisible();
			}
			else {
				await expect(personalMenuPage.welcomeText).toBeVisible();
			}
		}
	}
);

test(
	'View My Account user',
	{tag: '@LPD-81993'},
	async ({accountSettingsPage, apiHelpers, page}) => {
		const randomInt = getRandomInt();

		const userAccount = await apiHelpers.headlessAdminUser.postUserAccount({
			alternateName: `user${randomInt}`,
			emailAddress: `user${randomInt}@liferay.com`,
			familyName: 'userln',
			givenName: 'userfn',
		});

		userData[userAccount.alternateName] = {
			name: userAccount.givenName,
			password: 'test',
			surname: userAccount.familyName,
		};

		await performUserSwitch(page, userAccount.alternateName);

		await page.goto(liferayConfig.environment.baseUrl);

		await accountSettingsPage.goToAccountSettings();

		await expect(accountSettingsPage.userDisplayData).toBeVisible();
	}
);

test(
	'View My Account user via URL',
	{tag: ['@LPD-81993', '@LPS-76106']},
	async ({apiHelpers, page, personalMenuPage}) => {
		const randomInt = getRandomInt();

		const userAccount = await apiHelpers.headlessAdminUser.postUserAccount({
			alternateName: `user${randomInt}`,
			emailAddress: `user${randomInt}@liferay.com`,
			familyName: 'userln',
			givenName: 'userfn',
		});

		userData[userAccount.alternateName] = {
			name: userAccount.givenName,
			password: 'test',
			surname: userAccount.familyName,
		};

		await performUserSwitch(page, userAccount.alternateName);

		await page.goto(
			`${liferayConfig.environment.baseUrl}/group/control_panel/manage?p_p_id=com_liferay_my_account_web_portlet_MyAccountPortlet&p_p_lifecycle=1`
		);

		await expect(personalMenuPage.userDisplayDataText).toBeVisible();
	}
);

test(
	'View My Account via Sign In portlet',
	{tag: '@LPD-81993'},
	async ({apiHelpers, page, personalMenuPage}) => {
		const site =
			await apiHelpers.headlessAdminUser.getSiteByFriendlyUrlPath(
				'/guest'
			);

		const layout = await apiHelpers.jsonWebServicesLayout.addLayout({
			groupId: site.id,
			title: `SignInPage${getRandomInt()}`,
		});

		await page.goto(`/web/guest${layout.friendlyURL}`);

		await personalMenuPage.addWidgetButton.click();

		await personalMenuPage.widgetSearchInput.fill('Sign In');
		await page.keyboard.press('Enter');
		await personalMenuPage.addContentButton('Sign In').click();

		await waitForAlert(
			page,
			'Success:The application was added to the page.'
		);

		await personalMenuPage.portletContentLink('Test Test').click();

		await expect(personalMenuPage.userDisplayDataText).toBeVisible();
	}
);

test(
	'View My Organizations',
	{tag: '@LPD-81993'},
	async ({apiHelpers, page, personalMenuPage}) => {
		const organization1Name = `Organization${getRandomInt()} Name`;
		const organization2Name = `Organization${getRandomInt()} Name`;

		const randomInt = getRandomInt();

		const userAccount = await apiHelpers.headlessAdminUser.postUserAccount({
			alternateName: `user${randomInt}`,
			emailAddress: `user${randomInt}@liferay.com`,
			familyName: 'user1',
			givenName: 'user1',
		});

		userData[userAccount.alternateName] = {
			name: userAccount.givenName,
			password: 'test',
			surname: userAccount.familyName,
		};

		const organization1 =
			await apiHelpers.headlessAdminUser.postOrganization({
				name: organization1Name,
			});

		await apiHelpers.headlessAdminUser.assignUserToOrganizationByEmailAddress(
			organization1.id,
			userAccount.emailAddress
		);

		await apiHelpers.headlessAdminUser.postOrganization({
			name: organization2Name,
		});

		await performUserSwitch(page, userAccount.alternateName);

		await personalMenuPage.userPersonalMenuButton.click();
		await personalMenuPage.myOrganizationsMenuItem.click();

		await expect(personalMenuPage.myOrganizationsHeading).toBeVisible();

		await expect(page.getByText(organization1Name)).toBeVisible();
		await expect(page.getByText(organization2Name)).not.toBeVisible();
	}
);

test(
	'View no My Dashboard My Profile link',
	{tag: '@LPD-81993'},
	async ({apiHelpers, page, personalMenuPage}) => {
		const randomInt = getRandomInt();

		const userAccount = await apiHelpers.headlessAdminUser.postUserAccount({
			alternateName: `user${randomInt}`,
			emailAddress: `user${randomInt}@liferay.com`,
			familyName: 'user1',
			givenName: 'user1',
		});

		userData[userAccount.alternateName] = {
			name: userAccount.givenName,
			password: 'test',
			surname: userAccount.familyName,
		};

		await performUserSwitch(page, userAccount.alternateName);

		await personalMenuPage.userPersonalMenuButton.click();

		await expect(personalMenuPage.myDashboardMenuItem).not.toBeVisible();
		await expect(personalMenuPage.myProfileMenuItem).not.toBeVisible();
	}
);
