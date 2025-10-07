/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Locator, Page, expect} from '@playwright/test';

import {clickAndExpectToBeVisible} from '../../utils/clickAndExpectToBeVisible';
import {waitForAlert} from '../../utils/waitForAlert';
import {ApplicationsMenuPage} from '../product-navigation-applications-menu/ApplicationsMenuPage';

export class CaptchaConfigPage {
	readonly applicationsMenuPage: ApplicationsMenuPage;
	readonly actions: Locator;
	readonly captchaEngine: Locator;
	readonly createAccountCaptchaEnabled: Locator;
	readonly maxChallenges: Locator;
	readonly messageBoardsEditCategoryCaptchaEnabled: Locator;
	readonly messageBoardsEditMessageCaptchaEnabled: Locator;
	readonly page: Page;
	readonly reCaptchaNoScriptURL: Locator;
	readonly reCaptchaPrivateKey: Locator;
	readonly reCaptchaPublicKey: Locator;
	readonly reCaptchaScriptURL: Locator;
	readonly reCaptchaVerifyURL: Locator;
	readonly resetDefaultValues: Locator;
	readonly saveButton: Locator;
	readonly sendPasswordCaptchaEnabled: Locator;
	readonly simpleCaptchaHeight: Locator;
	readonly simpleCaptchaWidth: Locator;
	readonly updateButton: Locator;

	constructor(page: Page) {
		this.applicationsMenuPage = new ApplicationsMenuPage(page);
		this.actions = page.getByRole('button', {name: 'Actions'});
		this.captchaEngine = page.getByLabel('CAPTCHA Engine');
		this.createAccountCaptchaEnabled = page.getByText(
			'Create Account CAPTCHA Enabled'
		);
		this.maxChallenges = page.getByLabel('Maximum Challenges');
		this.messageBoardsEditCategoryCaptchaEnabled = page.getByLabel(
			'Message Boards Edit Category'
		);
		this.messageBoardsEditMessageCaptchaEnabled = page.getByLabel(
			'Message Boards Edit Message'
		);
		this.page = page;
		this.reCaptchaNoScriptURL = page.getByLabel('reCAPTCHA No Script URL');
		this.reCaptchaPrivateKey = page.getByLabel('reCAPTCHA Private Key');
		this.reCaptchaPublicKey = page.getByLabel('reCAPTCHA Public Key');
		this.reCaptchaScriptURL = page.getByLabel('reCAPTCHA Script URL');
		this.reCaptchaVerifyURL = page.getByLabel('reCAPTCHA Verify URL');
		this.resetDefaultValues = page.getByText('Reset Default Values');
		this.saveButton = page.getByRole('button', {name: 'Save'});
		this.sendPasswordCaptchaEnabled = page.getByText(
			'Send Password CAPTCHA Enabled'
		);
		this.simpleCaptchaHeight = page.getByLabel('Simple CAPTCHA Height');
		this.simpleCaptchaWidth = page.getByLabel('Simple CAPTCHA Width');
		this.updateButton = page.getByRole('button', {name: 'Update'});
	}

	async selectCaptchaEngine(captchaEngine: string) {
		await this.captchaEngine.click();

		await this.page.getByRole('option', {name: captchaEngine}).click();
	}

	async disableCreateAccountCaptcha(saveConfiguration: boolean = true) {
		await this.createAccountCaptchaEnabled.uncheck();
		await expect(this.createAccountCaptchaEnabled).not.toBeChecked();

		if (saveConfiguration) {
			await this.saveConfiguration();
		}
	}

	async disableMessageBoardsEditCategoryCaptcha(
		saveConfiguration: boolean = true
	) {
		await this.messageBoardsEditCategoryCaptchaEnabled.uncheck();
		await expect(
			this.messageBoardsEditCategoryCaptchaEnabled
		).not.toBeChecked();

		if (saveConfiguration) {
			await this.saveConfiguration();
		}
	}

	async disableMessageBoardsEditMessageCaptcha(
		saveConfiguration: boolean = true
	) {
		await this.messageBoardsEditMessageCaptchaEnabled.uncheck();
		await expect(
			this.messageBoardsEditMessageCaptchaEnabled
		).not.toBeChecked();

		if (saveConfiguration) {
			await this.saveConfiguration();
		}
	}

	async disableSendPasswordCaptcha(saveConfiguration: boolean = true) {
		await this.sendPasswordCaptchaEnabled.uncheck();
		await expect(this.sendPasswordCaptchaEnabled).not.toBeChecked();

		if (saveConfiguration) {
			await this.saveConfiguration();
		}
	}

	async enableReCaptcha(
		publicKey: string,
		privateKey: string,
		saveConfiguration: boolean = true
	) {
		await this.selectCaptchaEngine('reCAPTCHA');

		await this.reCaptchaPublicKey.fill(publicKey);

		await this.reCaptchaPrivateKey.fill(privateKey);

		if (saveConfiguration) {
			await this.saveConfiguration();
		}
	}

	async goTo() {
		await this.applicationsMenuPage.goToSystemSettings();

		await this.page.getByRole('link', {name: 'Security Tools'}).click();

		await this.page.getByRole('menuitem', {name: 'CAPTCHA'}).click();

		await this.sendPasswordCaptchaEnabled.waitFor();
	}

	async resetCaptchaConfiguration() {
		if (await this.actions.isVisible()) {
			await clickAndExpectToBeVisible({
				autoClick: true,
				target: this.resetDefaultValues,
				trigger: this.actions,
			});

			await waitForAlert(this.page);

			await this.saveConfiguration();
		}
	}

	async saveConfiguration() {
		if (await this.page.isVisible('button:has-text("Update")')) {
			await this.updateButton.click();

			await waitForAlert(
				this.page,
				`Success:Your request completed successfully.`
			);

			return;
		}

		await this.saveButton.click();

		await waitForAlert(
			this.page,
			`Success:Your request completed successfully.`
		);
	}
}
