/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Locator, Page, expect, mergeTests} from '@playwright/test';

import {captchaConfigPageTest} from '../../../fixtures/captchaConfigPageTest';
import {loginTest} from '../../../fixtures/loginTest';
import {passwordPoliciesAdminPageTest} from '../../../fixtures/passwordPoliciesAdminConfigPageTest';
import {systemSettingsPageTest} from '../../../fixtures/systemSettingsPageTest';
import {usersAndOrganizationsPagesTest} from '../../../fixtures/usersAndOrganizationsPagesTest';
import {TPasswordPolicy} from '../../../helpers/PasswordPolicyApiHelper';
import {liferayConfig} from '../../../liferay.config';
import {SystemSettingsPage} from '../../../pages/configuration-admin-web/SystemSettingsPage';
import {PasswordPoliciesAdminPage} from '../../../pages/password-policies-admin-web/PasswordPoliciesAdminPage';
import getRandomString from '../../../utils/getRandomString';
import performLogin, {
	performLoginViaApi,
	performLogout,
} from '../../../utils/performLogin';
import {waitForAlert} from '../../../utils/waitForAlert';

export const test = mergeTests(
	captchaConfigPageTest,
	loginTest(),
	passwordPoliciesAdminPageTest,
	systemSettingsPageTest,
	usersAndOrganizationsPagesTest
);

let passwordPoliciesConfigurationHasBeenUpdated = false;
let passwordPolicyHasBeenCreated = false;

test.afterEach(
	'Reset CAPTCHA configuration',
	async ({
		captchaConfigPage,
		page,
		passwordPoliciesAdminConfigPage,
		systemSettingsPage,
	}) => {
		await page.goto('/');

		if (await page.getByRole('button', {name: 'Sign In'}).isVisible()) {
			await performLogin(page, 'test');
		}

		await captchaConfigPage.goTo();

		await captchaConfigPage.resetCaptchaConfiguration();

		await page.goto('/');

		await passwordPoliciesAdminConfigPage.goTo();

		await passwordPoliciesAdminConfigPage.resetDefaultPasswordPolicy();

		if (passwordPolicyHasBeenCreated) {
			await passwordPoliciesAdminConfigPage.deleteAllPasswordPolicies();
		}

		if (passwordPoliciesConfigurationHasBeenUpdated) {
			await systemSettingsPage.goToSystemSetting(
				'Users',
				'Password Policies'
			);

			await systemSettingsPage.resetToDefaultValues();

			passwordPoliciesConfigurationHasBeenUpdated = false;
		}
	}
);

test.beforeEach(
	'Disable create account CAPTCHA',
	async ({captchaConfigPage, page}) => {
		await page.goto('/');

		if (await page.getByRole('button', {name: 'Sign In'}).isVisible()) {
			await performLogin(page, 'test');
		}

		await captchaConfigPage.goTo();

		await captchaConfigPage.disableCreateAccountCaptcha();
	}
);

test(
	'Edit default password policy with syntax checking and restrict dictionary words and check that it shows a Dictionary Words error',
	{tag: '@LPD-50094'},
	async ({browser, passwordPoliciesAdminConfigPage}) => {
		const passwordPolicy: TPasswordPolicy = {
			allowDictionaryWordsToggle: false,
			checkSyntaxToggle: true,
			minNumbers: 0,
			minUpperCase: 0,
		};

		await testPasswordPolicySyntaxCheck(
			browser,
			'That password uses common dictionary words',
			passwordPoliciesAdminConfigPage,
			passwordPolicy,
			'aardvark'
		);
	}
);

test(
	'Edit default password policy with syntax checking and 1 alphanumeric and check that it shows an error for Minimum Alpha Numeric error',
	{tag: '@LPD-50094'},
	async ({browser, passwordPoliciesAdminConfigPage}) => {
		const passwordPolicy: TPasswordPolicy = {
			checkSyntaxToggle: true,
			minAlphanumeric: 1,
			minLowerCase: 0,
			minNumbers: 0,
			minUpperCase: 0,
		};

		await testPasswordPolicySyntaxCheck(
			browser,
			'That password must contain at least 1 alphanumeric character',
			passwordPoliciesAdminConfigPage,
			passwordPolicy,
			'@@@@@@'
		);
	}
);

test(
	'Edit default password policy with syntax checking and 10 length and check that it shows an error for Minimum Length',
	{tag: '@LPD-50094'},
	async ({browser, passwordPoliciesAdminConfigPage}) => {
		const passwordPolicy: TPasswordPolicy = {
			checkSyntaxToggle: true,
			minLength: 10,
		};

		await testPasswordPolicySyntaxCheck(
			browser,
			'That password is too short',
			passwordPoliciesAdminConfigPage,
			passwordPolicy,
			'ABcd12#$'
		);
	}
);

test(
	'Edit default password policy with syntax checking and 1 lowercase and check that it shows an error for Minimum Lower Case error',
	{tag: '@LPD-48268'},
	async ({browser, passwordPoliciesAdminConfigPage}) => {
		const passwordPolicy: TPasswordPolicy = {
			checkSyntaxToggle: true,
			minLowerCase: 1,
		};

		await testPasswordPolicySyntaxCheck(
			browser,
			'That password must contain at least 1 lowercase character',
			passwordPoliciesAdminConfigPage,
			passwordPolicy,
			'ABC123'
		);
	}
);

test(
	'Edit default password policy with syntax checking and 1 number and check that it shows an error for Minimum Numbers error',
	{tag: '@LPD-50094'},
	async ({browser, passwordPoliciesAdminConfigPage}) => {
		const passwordPolicy: TPasswordPolicy = {
			checkSyntaxToggle: true,
			minNumbers: 1,
		};

		await testPasswordPolicySyntaxCheck(
			browser,
			'That password must contain at least 1 number',
			passwordPoliciesAdminConfigPage,
			passwordPolicy,
			'ABCdef'
		);
	}
);

test(
	'Edit default password policy with syntax checking and 1 symbol and check that it shows an error for Minimum Symbols',
	{tag: '@LPD-50094'},
	async ({browser, passwordPoliciesAdminConfigPage}) => {
		const passwordPolicy: TPasswordPolicy = {
			checkSyntaxToggle: true,
			minSymbols: 1,
		};

		await testPasswordPolicySyntaxCheck(
			browser,
			'That password must contain at least 1 symbol',
			passwordPoliciesAdminConfigPage,
			passwordPolicy,
			'abCD123'
		);
	}
);

test(
	'Edit default password policy with syntax checking and 1 uppercase and check that it shows an error for Minimum Upper Case',
	{tag: '@LPD-50094'},
	async ({browser, passwordPoliciesAdminConfigPage}) => {
		const passwordPolicy: TPasswordPolicy = {
			checkSyntaxToggle: true,
			minUpperCase: 1,
		};

		await testPasswordPolicySyntaxCheck(
			browser,
			'That password must contain at least 1 uppercase character',
			passwordPoliciesAdminConfigPage,
			passwordPolicy,
			'abc123'
		);
	}
);

test(
	`Verify display 'Eternal' option is visible and 0 weeks is not when Ticket Max Age is set to 0`,
	{tag: '@LPD-85143'},
	async ({page, passwordPoliciesAdminConfigPage, systemSettingsPage}) => {
		await testDefaultMessageCanBeSaved(
			'Eternal',
			page,
			passwordPoliciesAdminConfigPage,
			systemSettingsPage,
			passwordPoliciesAdminConfigPage.resetTicketMaxAge
		);
	}
);

test(
	`Verify display 'None' option is visible and 0 weeks is not when Minimum Age is set to 0`,
	{tag: '@LPD-85143'},
	async ({page, passwordPoliciesAdminConfigPage, systemSettingsPage}) => {
		await testDefaultMessageCanBeSaved(
			'None',
			page,
			passwordPoliciesAdminConfigPage,
			systemSettingsPage,
			passwordPoliciesAdminConfigPage.minimumAge
		);
	}
);

test(
	`Verify display 'Until unlocked' by an administrator option is visible and 0 weeks is not when Lockout Duration is set to 0`,
	{tag: '@LPD-85143'},
	async ({page, passwordPoliciesAdminConfigPage, systemSettingsPage}) => {
		await testDefaultMessageCanBeSaved(
			'Until unlocked by an administrator',
			page,
			passwordPoliciesAdminConfigPage,
			systemSettingsPage,
			passwordPoliciesAdminConfigPage.lockoutDuration
		);
	}
);

async function testDefaultMessageCanBeSaved(
	defaultMessage: string,
	page: Page,
	passwordPoliciesAdminConfigPage: PasswordPoliciesAdminPage,
	systemSettingsPage: SystemSettingsPage,
	targetLocator: Locator
) {
	await systemSettingsPage.goToSystemSetting('Users', 'Password Policies');

	await expect(targetLocator.first()).toBeVisible();

	await targetLocator
		.locator('xpath=./ancestor::div[contains(@class, "form-group")]')
		.locator('button[title="Duplicate"]')
		.first()
		.click();

	if (await passwordPoliciesAdminConfigPage.updateButton.isVisible()) {
		await passwordPoliciesAdminConfigPage.updateButton.click();
	}
	else {
		await passwordPoliciesAdminConfigPage.saveButton.click();
	}

	await expect(passwordPoliciesAdminConfigPage.successMessage).toBeVisible();

	passwordPoliciesConfigurationHasBeenUpdated = true;

	await page.goto(liferayConfig.environment.baseUrl);

	await passwordPoliciesAdminConfigPage.goTo();

	await passwordPoliciesAdminConfigPage.editDefaultPasswordPolicy({
		checkSyntaxToggle: true,
		minNumbers: 1,
	});

	await waitForAlert(page);

	await expect(targetLocator.last()).toContainText(defaultMessage);
	await expect(targetLocator.last()).not.toContainText('0 weeks');

	await targetLocator
		.last()
		.selectOption({label: defaultMessage}, {force: true});

	if (await passwordPoliciesAdminConfigPage.updateButton.isVisible()) {
		await passwordPoliciesAdminConfigPage.updateButton.click();
	}
	else {
		await passwordPoliciesAdminConfigPage.saveButton.click();
	}

	await expect(passwordPoliciesAdminConfigPage.successMessage).toBeVisible();

	await expect(targetLocator.last().locator('option:checked')).toHaveText(
		defaultMessage
	);
}

test(
	'Create password policy and assign to a recently logged-in user and verify it applied the Edit Password page',
	{tag: '@LPP-63539'},
	async ({
		editUserPage,
		page,
		passwordPoliciesAdminConfigPage,
		usersAndOrganizationsPage,
	}) => {
		const screenName = 'demo.unprivileged';

		await performLogout(page);

		await performLoginViaApi({page, rememberMe: false, screenName});

		const passwordPolicy: TPasswordPolicy = {
			changeRequiredToggle: true,
			changeableToggle: true,
			name: getRandomString(),
		};

		await performLoginViaApi({page, screenName: 'test'});

		await passwordPoliciesAdminConfigPage.goTo();

		await passwordPoliciesAdminConfigPage.createPasswordPolicy(
			passwordPolicy
		);

		passwordPolicyHasBeenCreated = true;

		await passwordPoliciesAdminConfigPage.goTo();

		await passwordPoliciesAdminConfigPage.assignUser(
			passwordPolicy.name,
			screenName
		);
		await usersAndOrganizationsPage.goToUsers();

		await usersAndOrganizationsPage.goToUser(screenName);

		await editUserPage.passwordLink.click();

		const requiredPasswordResetCheckbox =
			editUserPage.requiredPasswordResetCheckbox;

		await expect(editUserPage.passwordInput).toBeEnabled();

		await expect(requiredPasswordResetCheckbox).toBeDisabled();

		await expect(requiredPasswordResetCheckbox).toBeChecked();
	}
);

async function testPasswordPolicySyntaxCheck(
	browser,
	expectedMessage: String,
	passwordPoliciesAdminConfigPage: PasswordPoliciesAdminPage,
	passwordPolicy: TPasswordPolicy,
	password: String
) {
	await passwordPoliciesAdminConfigPage.goTo();

	await passwordPoliciesAdminConfigPage.editDefaultPasswordPolicy(
		passwordPolicy
	);

	const page = await browser.newPage();

	await page.goto('/');

	await page.getByRole('button', {name: 'Sign In'}).click();

	await page.getByText('Create Account').click();

	await page.getByLabel('Screen Name').fill(getRandomString());

	await page
		.getByLabel('Email Address')
		.fill(getRandomString() + '@liferay.com');

	await page.getByLabel('First Name').fill(getRandomString());

	await page.getByLabel('Last Name').fill(getRandomString());

	await page.getByLabel('Password Required', {exact: true}).fill(password);

	await page.getByLabel('Reenter Password Required').fill(password);

	await page.getByRole('button', {name: 'Save'}).click();

	await expect(page.getByText(expectedMessage)).toBeVisible();
}
