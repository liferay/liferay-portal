/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Locator, Page, expect} from '@playwright/test';

import {waitForAlert} from '../../utils/waitForAlert';
import {GlobalMenuPage} from './GlobalMenuPage';

const MOCK_API_KEY = 'VALID_API_KEY';
const STR_BLANK = '';

export class AICreatorInstanceSettingsPage {
	readonly apiKeyInput: Locator;
	readonly chatGPTCheckbox: Locator;
	readonly dalleCheckbox: Locator;
	readonly globalMenuPage: GlobalMenuPage;
	readonly page: Page;
	readonly saveButton: Locator;

	constructor(page: Page) {
		this.page = page;

		this.apiKeyInput = this.page.getByLabel('API Key');
		this.chatGPTCheckbox = this.page.getByLabel(
			'Enable ChatGPT to Create Content'
		);
		this.dalleCheckbox = this.page.getByLabel(
			'Enable DALL-E to Create Images'
		);
		this.globalMenuPage = new GlobalMenuPage(page);
		this.saveButton = this.page.getByRole('button', {name: 'Save'});
	}

	async goto() {
		await this.globalMenuPage.goToControlPanel(
			'Instance Settings',
			'AI Creator'
		);
	}

	async disableChatGPTCreateContent() {
		await this.goto();

		await this.chatGPTCheckbox.uncheck();

		await expect(this.chatGPTCheckbox).not.toBeChecked();

		await this.saveButton.click();

		await waitForAlert(this.page);
	}

	async enableChatGPTCreateContent() {
		await this.goto();

		await this.chatGPTCheckbox.check();

		await expect(this.chatGPTCheckbox).toBeChecked();

		await this.saveButton.click();

		await waitForAlert(this.page);
	}

	async enableDalleCreateImages() {
		await this.goto();

		await this.dalleCheckbox.check();

		await expect(this.dalleCheckbox).toBeChecked();

		await this.saveButton.click();

		await waitForAlert(this.page);
	}

	async disableDalleCreateImages() {
		await this.goto();

		await this.dalleCheckbox.uncheck();

		await expect(this.dalleCheckbox).not.toBeChecked();

		await this.saveButton.click();

		await waitForAlert(this.page);
	}

	async addApiKey() {
		await this.setAPIKey(MOCK_API_KEY);
	}

	async removeApiKey() {
		await this.setAPIKey(STR_BLANK);
	}

	async setAPIKey(apikey) {
		await this.goto();

		await this.apiKeyInput.fill(apikey);
		await this.saveButton.click();
		await waitForAlert(this.page);
	}
}
