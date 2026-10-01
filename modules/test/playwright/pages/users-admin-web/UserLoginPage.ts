/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Locator, Page, expect} from '@playwright/test';

export class UserLoginPage {
	readonly authenticationFailedAlert: Locator;
	readonly createAccountLink: Locator;
	readonly emailAddressInput: Locator;
	readonly forgotPasswordLink: Locator;
	readonly iAgreeButton: Locator;
	readonly idInput: Locator;
	readonly loginInput: Locator;
	readonly page: Page;
	readonly passwordInput: Locator;
	readonly rememberMeCheckbox: Locator;
	readonly sendNewPasswordButton: Locator;
	readonly sendNewVerificationCodeButton: Locator;
	readonly signInButton: Locator;
	readonly signInNavButton: Locator;
	readonly userProfileMenuButton: Locator;

	constructor(page: Page) {
		this.authenticationFailedAlert = page
			.getByRole('alert')
			.getByText('Authentication failed');
		this.createAccountLink = page
			.getByRole('link', {name: 'Create Account'})
			.or(page.getByRole('menuitem', {name: 'Create Account'}));
		this.emailAddressInput = page.getByLabel('Email Address');
		this.forgotPasswordLink = page
			.getByRole('link', {
				name: 'Forgot Password',
			})
			.or(page.getByRole('menuitem', {name: 'Forgot Password'}));
		this.iAgreeButton = page.getByRole('button', {name: 'I Agree'});
		this.idInput = page.getByLabel('ID');
		this.loginInput = page.locator(
			'input[id="_com_liferay_login_web_portlet_LoginPortlet_login"]'
		);
		this.page = page;
		this.passwordInput = page.getByLabel('Password');
		this.rememberMeCheckbox = page.getByLabel('Remember Me');
		this.sendNewPasswordButton = page.getByRole('button', {
			name: 'Send New Password',
		});
		this.sendNewVerificationCodeButton = page.getByRole('button', {
			name: 'Send New Verification Code',
		});
		this.signInButton = page.getByRole('button', {name: 'Sign In'}).last();
		this.signInNavButton = page
			.getByRole('button', {name: 'Sign In'})
			.first();
		this.userProfileMenuButton = page.getByTitle('User Profile Menu');
	}

	async agreeToTermsOfUseIfRequired() {
		await expect(async () => {
			if (await this.iAgreeButton.isVisible()) {
				await this.iAgreeButton.click();
			}

			await expect(this.userProfileMenuButton).toBeVisible({
				timeout: 3000,
			});
		}).toPass({timeout: 30000});
	}

	async goto() {
		await this.page.goto('/');

		await this.signInNavButton.click();
	}
}
