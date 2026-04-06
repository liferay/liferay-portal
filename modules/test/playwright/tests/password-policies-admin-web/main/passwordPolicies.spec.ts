/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {captchaConfigPageTest} from '../../../fixtures/captchaConfigPageTest';
import {featureFlagsTest} from '../../../fixtures/featureFlagsTest';
import {loginTest} from '../../../fixtures/loginTest';
import {passwordPoliciesAdminPageTest} from '../../../fixtures/passwordPoliciesAdminConfigPageTest';
import {systemSettingsPageTest} from '../../../fixtures/systemSettingsPageTest';
import {TPasswordPolicy} from '../../../helpers/PasswordPolicyApiHelper';
import {liferayConfig} from '../../../liferay.config';
import {PasswordPoliciesAdminPage} from '../../../pages/password-policies-admin-web/PasswordPoliciesAdminPage';
import getRandomString from '../../../utils/getRandomString';
import performLoginViaApi from '../../../utils/performLogin';
import {waitForAlert} from '../../../utils/waitForAlert';

export const test = mergeTests(
	captchaConfigPageTest,
	featureFlagsTest({
		'LPD-36105': {enabled: true},
	}),
	loginTest(),
	passwordPoliciesAdminPageTest,
	systemSettingsPageTest
);

test.afterEach(
	'Reset CAPTCHA configuration',
	async ({captchaConfigPage, page, passwordPoliciesAdminConfigPage}) => {
		await page.goto('/');

		if (await page.getByRole('button', {name: 'Sign In'}).isVisible()) {
			await performLoginViaApi(page, 'test');
		}

		await captchaConfigPage.goTo();

		await captchaConfigPage.resetCaptchaConfiguration();

		await page.goto('/');

		await passwordPoliciesAdminConfigPage.goTo();

		await passwordPoliciesAdminConfigPage.resetDefaultPasswordPolicy();
	}
);

test.beforeEach(
	'Disable create account CAPTCHA',
	async ({captchaConfigPage, page}) => {
		await page.goto('/');

		if (await page.getByRole('button', {name: 'Sign In'}).isVisible()) {
			await performLoginViaApi(page, 'test');
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
	'Verify only display Eternal option when Ticket Max Age is set to 0',
	{tag: '@LPD-85143'},
	async ({page, passwordPoliciesAdminConfigPage, systemSettingsPage}) => {
		await systemSettingsPage.goToSystemSetting(
			'Users',
			'Password Policies'
		);

		await expect(
			page.getByText('Reset Ticket Max Age Durations').first()
		).toBeVisible();

		const resetTicketMaxAgeDurationEntry = await page
			.locator('div[data-field-reference="resetTicketMaxAgeDurations"]')
			.first();

		const removeExcludedPatresetTicketMaxAgeDurationButton =
			resetTicketMaxAgeDurationEntry.locator('button[title="Duplicate"]');

		await removeExcludedPatresetTicketMaxAgeDurationButton.click();

		if (await page.isVisible('button:has-text("Update")')) {
			await page.getByRole('button', {name: 'Update'}).click();
		}
		else {
			await page.getByRole('button', {name: 'Save'}).click();
		}

		await waitForAlert(
			page,
			`Success:Your request completed successfully.`
		);

		await page.goto(liferayConfig.environment.baseUrl);

		await passwordPoliciesAdminConfigPage.goTo();

		const passwordPolicy: TPasswordPolicy = {
			checkSyntaxToggle: true,
			minNumbers: 1,
		};

		await passwordPoliciesAdminConfigPage.editDefaultPasswordPolicy(
			passwordPolicy
		);

		const resetTicketMaxAgeOptions =
			await passwordPoliciesAdminConfigPage.resetTicketMaxAge
				.locator('option')
				.allInnerTexts();

		const resetTicketMaxAgeTrimmedOptions = resetTicketMaxAgeOptions.map(
			(text) => text.trim()
		);

		expect(resetTicketMaxAgeTrimmedOptions).not.toContain('0 weeks');
		expect(resetTicketMaxAgeTrimmedOptions).toContain('Eternal');
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
