/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {dataApiHelpersTest} from '../../../fixtures/dataApiHelpersTest';
import {featureFlagsTest} from '../../../fixtures/featureFlagsTest';
import {instanceSettingsPagesTest} from '../../../fixtures/instanceSettingsPagesTest';
import {loginTest} from '../../../fixtures/loginTest';
import {smtpPagesTest} from '../../../fixtures/smtpPagesTest';
import {usersAndOrganizationsPagesTest} from '../../../fixtures/usersAndOrganizationsPagesTest';
import {liferayConfig} from '../../../liferay.config';
import {UserRegistrationPage} from '../../../pages/users-admin-web/UserRegistrationPage';
import {SiteSettingsPage} from '../../../pages/users-admin-web/site-admin-web/SiteSettingsPage';
import getRandomString from '../../../utils/getRandomString';
import {
	performLoginViaApi,
	performLogout,
	userData,
} from '../../../utils/performLogin';

const test = mergeTests(
	dataApiHelpersTest,
	featureFlagsTest({
		'LPS-178052': {enabled: true},
	}),
	instanceSettingsPagesTest,
	loginTest(),
	smtpPagesTest,
	usersAndOrganizationsPagesTest
);

test(
	'Can view email verification notification with user first name',
	{tag: ['@LPD-81993', '@LPS-176074']},
	async ({
		apiHelpers,
		emailInstanceSettingsPage,
		instanceSettingsPage,
		mockMockPage,
		page,
		userLoginPage,
	}) => {
		await instanceSettingsPage.goToInstanceSetting(
			'User Authentication',
			'General'
		);

		const strangersVerifyCheckbox = page.getByLabel(
			'Require strangers to verify their email address'
		);

		await expect(strangersVerifyCheckbox).toBeVisible();

		await strangersVerifyCheckbox.check();

		await instanceSettingsPage.saveAndWaitForAlert();

		await emailInstanceSettingsPage.goToEmailSender();

		await emailInstanceSettingsPage.senderAddressInput.fill(
			'joe@liferay.com'
		);
		await emailInstanceSettingsPage.senderNameInput.fill('Joe Bloggs');

		await instanceSettingsPage.saveAndWaitForAlert();

		await page.waitForLoadState('networkidle');

		await mockMockPage.deleteAll();

		const user = await apiHelpers.headlessAdminUser.postUserAccount();

		try {
			await performLogout(page);

			await userLoginPage.goto();

			await userLoginPage.emailAddressInput.fill(user.emailAddress);
			await userLoginPage.passwordInput.fill(userData['test'].password);
			await userLoginPage.signInButton.click();

			await page.waitForLoadState('networkidle');

			await expect(
				userLoginPage.sendNewVerificationCodeButton
			).toBeVisible({timeout: 10000});

			await userLoginPage.sendNewVerificationCodeButton.click();

			await page.waitForLoadState('networkidle');

			await mockMockPage.assertEmail({
				subject: 'Email Address Verification',
				to: user.givenName,
			});
		}
		finally {
			await page.context().clearCookies();
			await performLoginViaApi({page, screenName: 'test'});

			await emailInstanceSettingsPage.goToEmailSender();

			await emailInstanceSettingsPage.senderAddressInput.fill(
				'test@liferay.com'
			);
			await emailInstanceSettingsPage.senderNameInput.fill('Test Test');

			await instanceSettingsPage.saveAndWaitForAlert();

			await instanceSettingsPage.goToInstanceSetting(
				'User Authentication',
				'General'
			);

			await page
				.getByLabel('Require strangers to verify their email address')
				.uncheck();

			await instanceSettingsPage.saveAndWaitForAlert();
		}
	}
);

test(
	'Welcome email contains virtual host URL',
	{tag: ['@LPD-81993', '@LPS-111136']},
	async ({
		apiHelpers,
		emailInstanceSettingsPage,
		instanceSettingsPage,
		mockMockPage,
		page,
	}) => {
		await instanceSettingsPage.goToInstanceSetting(
			'User Authentication',
			'General'
		);

		await page
			.getByLabel('Require strangers to verify their email address')
			.uncheck();

		await instanceSettingsPage.saveAndWaitForAlert();

		await emailInstanceSettingsPage.goToEmailSender();

		await emailInstanceSettingsPage.senderAddressInput.fill(
			'joe@liferay.com'
		);
		await emailInstanceSettingsPage.senderNameInput.fill('Joe Bloggs');

		await instanceSettingsPage.saveAndWaitForAlert();

		await page.waitForLoadState('networkidle');

		await mockMockPage.deleteAll();

		const site = await apiHelpers.headlessAdminSite.postSite({
			name: `Site${getRandomString()}`,
		});

		await apiHelpers.headlessDelivery.createSitePage({
			siteId: site.id,
			title: 'Home',
		});

		const siteSettingsPage = new SiteSettingsPage(page);
		const siteFriendlyUrl = site.friendlyUrlPath || '';

		try {
			await siteSettingsPage.setVirtualHost(
				siteFriendlyUrl,
				'www.able.com'
			);

			const registrationContext = await page
				.context()
				.browser()!
				.newContext();

			const registrationPage = await registrationContext.newPage();
			const userRegistrationPage = new UserRegistrationPage(
				registrationPage
			);

			try {
				await registrationPage.goto(
					`http://www.able.com:${liferayConfig.environment.port}`
				);

				await userRegistrationPage.signInButton.click();
				await userRegistrationPage.createAccountLink.click();

				const userName = `user${getRandomString()}`;

				await userRegistrationPage.emailAddressInput.fill(
					`${userName}@liferay.com`
				);
				await userRegistrationPage.firstNameInput.fill(userName);
				await userRegistrationPage.lastNameInput.fill(userName);
				await userRegistrationPage.passwordInput.fill('Password1');
				await userRegistrationPage.reenterPasswordInput.fill(
					'Password1'
				);
				await userRegistrationPage.screenNameInput.fill(userName);

				await userRegistrationPage.saveButton.click();

				await registrationPage.waitForLoadState('networkidle');

				// Wait for the email to be sent asynchronously

				await registrationPage.waitForTimeout(3000);
			}
			finally {
				await registrationContext.close();
			}

			await mockMockPage.assertEmail({
				body: 'www.able.com',
				subject: 'www.able.com',
			});
		}
		finally {
			await emailInstanceSettingsPage.goToEmailSender();

			await emailInstanceSettingsPage.senderAddressInput.fill(
				'test@liferay.com'
			);
			await emailInstanceSettingsPage.senderNameInput.fill('Test Test');

			await instanceSettingsPage.saveAndWaitForAlert();
		}
	}
);
