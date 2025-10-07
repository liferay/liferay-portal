/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {captchaConfigPageTest} from '../../../fixtures/captchaConfigPageTest';
import {instanceSettingsPagesTest} from '../../../fixtures/instanceSettingsPagesTest';
import {loginTest} from '../../../fixtures/loginTest';
import {liferayConfig} from '../../../liferay.config';
import getRandomString from '../../../utils/getRandomString';
import performLogin, {performLogout} from '../../../utils/performLogin';

let captchaConfigurationResetRequired: boolean = false;

export const test = mergeTests(
	captchaConfigPageTest,
	instanceSettingsPagesTest,
	loginTest()
);

test.beforeEach(
	'Disable create account CAPTCHA',
	async ({captchaConfigPage, page}) => {
		await page.goto(liferayConfig.environment.baseUrl);

		if (await page.getByRole('button', {name: 'Sign In'}).isVisible()) {
			await performLogin(page, 'test');
		}

		await captchaConfigPage.goTo();

		await captchaConfigPage.disableCreateAccountCaptcha();

		captchaConfigurationResetRequired = true;
	}
);

test.afterEach(
	'Reset CAPTCHA configuration',
	async ({captchaConfigPage, page}) => {
		if (captchaConfigurationResetRequired) {
			await page.goto(liferayConfig.environment.baseUrl);

			if (await page.getByRole('button', {name: 'Sign In'}).isVisible()) {
				await performLogin(page, 'test');
			}

			await captchaConfigPage.goTo();

			await captchaConfigPage.resetCaptchaConfiguration();

			await page.goto(liferayConfig.environment.baseUrl);
		}
	}
);

test('LPD-44960 Create account using duplicate email address', async ({
	page,
}) => {
	await performLogout(page);

	await page.goto(liferayConfig.environment.baseUrl);

	await page.getByRole('button', {name: 'Sign In'}).click();

	await page.getByText('Create Account').click();

	await page.getByLabel('Screen Name').fill(getRandomString());

	await page.getByLabel('Email Address').fill('test@liferay.com');

	await page.getByLabel('First Name').fill(getRandomString());

	await page.getByLabel('Last Name').fill(getRandomString());

	const password = getRandomString();

	await page.getByLabel('Password Required', {exact: true}).fill(password);

	await page.getByLabel('Reenter Password Required').fill(password);

	await page.getByRole('button', {name: 'Save'}).click();

	await expect(
		page.getByText(
			'Thank you for creating an account. Use your password to log in.'
		)
	).toBeVisible();

	await expect(
		page.getByText('Error:Your request failed to complete.')
	).toBeHidden();
});

test('LPD-44960 Create account using duplicate email address with email address verification', async ({
	instanceSettingsPage,
	page,
}) => {
	await instanceSettingsPage.goToInstanceSetting(
		'User Authentication',
		'General'
	);

	const strangersVerify = page.getByText(
		'Require strangers to verify their email address?'
	);
	await strangersVerify.check();
	await expect(strangersVerify).toBeChecked();

	await instanceSettingsPage.saveAndWaitForAlert();

	await performLogout(page);

	await page.goto(liferayConfig.environment.baseUrl);

	await page.getByRole('button', {name: 'Sign In'}).click();

	await page.getByText('Create Account').click();

	await page.getByLabel('Screen Name').fill(getRandomString());

	await page.getByLabel('Email Address').fill('test@liferay.com');

	await page.getByLabel('First Name').fill(getRandomString());

	await page.getByLabel('Last Name').fill(getRandomString());

	const password = getRandomString();

	await page.getByLabel('Password Required', {exact: true}).fill(password);

	await page.getByLabel('Reenter Password Required').fill(password);

	await page.getByRole('button', {name: 'Save'}).click();

	await expect(
		page.getByText(
			'Thank you for creating an account. Your email verification code was sent to test@liferay.com. Use your password to log in.'
		)
	).toBeVisible();

	await expect(
		page.getByText('Error:Your request failed to complete.')
	).toBeHidden();

	await performLogin(page, 'test');

	await instanceSettingsPage.goToInstanceSetting(
		'User Authentication',
		'General'
	);

	await strangersVerify.uncheck();
	await expect(strangersVerify).not.toBeChecked();

	await instanceSettingsPage.saveAndWaitForAlert();
});

test('LPD-52901 Check CAPTCHA section title', async ({
	captchaConfigPage,
	page,
}) => {
	await performLogout(page);

	await page.goto(liferayConfig.environment.baseUrl);

	await page.getByRole('button', {name: 'Sign In'}).click();

	await page.getByText('Create Account').click();

	await expect(page.getByLabel('Screen Name')).toBeVisible();

	await expect(
		page.getByRole('heading', {name: 'Verification'})
	).not.toBeVisible();

	await expect(page.getByText('Refresh CAPTCHA Text')).not.toBeVisible();

	await page.goto(liferayConfig.environment.baseUrl);

	await performLogin(page, 'test');

	await captchaConfigPage.goTo();

	await captchaConfigPage.resetCaptchaConfiguration();

	captchaConfigurationResetRequired = false;

	await performLogout(page);

	await page.goto(liferayConfig.environment.baseUrl);

	await page.getByRole('button', {name: 'Sign In'}).click();

	await page.getByText('Create Account').click();

	await expect(page.getByLabel('Screen Name')).toBeVisible();

	await expect(
		page.getByRole('heading', {name: 'Verification'})
	).toBeVisible();

	await expect(page.getByText('Refresh CAPTCHA Text')).toBeVisible();
});
