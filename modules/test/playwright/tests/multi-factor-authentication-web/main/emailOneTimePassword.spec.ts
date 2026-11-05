/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {dataApiHelpersTest} from '../../../fixtures/dataApiHelpersTest';
import {loginTest} from '../../../fixtures/loginTest';
import {multiFactorAuthenticationPagesTest} from '../../../fixtures/multiFactorAuthenticationPagesTest';
import {UserLoginPage} from '../../../pages/users-admin-web/UserLoginPage';
import {clickAndExpectToBeVisible} from '../../../utils/clickAndExpectToBeVisible';
import {getRandomInt} from '../../../utils/getRandomInt';

export const test = mergeTests(
	dataApiHelpersTest,
	loginTest(),
	multiFactorAuthenticationPagesTest
);

test(
	'maximum attempts warning is removed once the retry timeout expires',
	{tag: '@LPD-95283'},
	async ({
		apiHelpers,
		browser,
		multiFactorAuthenticationConfigurationPage,
	}) => {
		await multiFactorAuthenticationConfigurationPage.goto();

		try {
			await multiFactorAuthenticationConfigurationPage.enable({
				failedAttemptsAllowed: 1,
				retryTimeout: 10,
			});

			const user = await apiHelpers.headlessAdminUser.postUserAccount();

			const userContext = await browser.newContext();
			const userPage = await userContext.newPage();

			const userLoginPage = new UserLoginPage(userPage);

			const maximumAllowedAttemptsError = userPage.getByText(
				'You have reached the maximum allowed failed attempts'
			);
			const otpInput = userPage.getByLabel(
				'Enter the one-time password from the email'
			);
			const submitButton = userPage.getByRole('button', {name: 'Submit'});

			try {
				await userPage.goto('/');

				await clickAndExpectToBeVisible({
					target: userLoginPage.emailAddressInput,
					trigger: userLoginPage.signInNavButton,
				});

				await userLoginPage.emailAddressInput.fill(user.emailAddress);
				await userLoginPage.passwordInput.fill('test');

				await userLoginPage.signInButton.click();

				await expect(otpInput).toBeVisible({timeout: 10000});

				await otpInput.fill(String(getRandomInt()));

				await submitButton.click();

				await expect(maximumAllowedAttemptsError).toBeVisible({
					timeout: 10000,
				});

				await expect(maximumAllowedAttemptsError).toBeHidden({
					timeout: 20000,
				});

				await expect(submitButton).toBeEnabled();
			}
			finally {
				await userContext.close();
			}
		}
		finally {
			await multiFactorAuthenticationConfigurationPage.goto();

			await multiFactorAuthenticationConfigurationPage.resetConfiguration();
		}
	}
);

test(
	'verify heading is visible on the MFA challenge screen',
	{tag: '@LPP-66280'},
	async ({
		apiHelpers,
		browser,
		multiFactorAuthenticationConfigurationPage,
	}) => {
		await multiFactorAuthenticationConfigurationPage.goto();

		try {
			await multiFactorAuthenticationConfigurationPage.enable();

			const user = await apiHelpers.headlessAdminUser.postUserAccount();

			const userContext = await browser.newContext();
			const userPage = await userContext.newPage();

			const userLoginPage = new UserLoginPage(userPage);

			try {
				await userPage.goto('/');

				await clickAndExpectToBeVisible({
					target: userLoginPage.emailAddressInput,
					trigger: userLoginPage.signInNavButton,
				});

				await userLoginPage.emailAddressInput.fill(user.emailAddress);
				await userLoginPage.passwordInput.fill('test');

				await userLoginPage.signInButton.click();

				await expect(
					userPage.getByLabel(
						'Enter the one-time password from the email'
					)
				).toBeVisible({timeout: 10000});

				await expect(
					userPage.getByRole('heading', {
						exact: true,
						name: 'Multi-Factor Authentication Verify',
					})
				).toBeVisible();
			}
			finally {
				await userContext.close();
			}
		}
		finally {
			await multiFactorAuthenticationConfigurationPage.goto();

			await multiFactorAuthenticationConfigurationPage.resetConfiguration();
		}
	}
);
