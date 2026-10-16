/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Locator, Page, expect} from '@playwright/test';

import {waitForAlert} from '../../utils/waitForAlert';
import {waitForSPAToBeLoaded} from '../../utils/waitForSPAToBeLoaded';
import {GlobalMenuPage} from './GlobalMenuPage';

const MOCK_API_KEY = 'VALID_API_KEY';
const STR_BLANK = '';

export class AICreatorInstanceSettingsPage {
	readonly apiKeyInput: Locator;
	readonly chatGPTCheckbox: Locator;
	readonly globalMenuPage: GlobalMenuPage;
	readonly openAIImageGenerationCheckbox: Locator;
	readonly imageModelInput: Locator;
	readonly imageSizesInput: Locator;
	readonly page: Page;
	readonly saveButton: Locator;

	constructor(page: Page) {
		this.page = page;

		this.apiKeyInput = this.page.getByLabel('API Key');
		this.chatGPTCheckbox = this.page.getByLabel(
			'Enable ChatGPT to Create Content'
		);
		this.globalMenuPage = new GlobalMenuPage(page);
		this.openAIImageGenerationCheckbox = this.page.getByLabel(
			'Enable OpenAI Image Generation'
		);
		this.imageModelInput = this.page.getByLabel('Image Model');
		this.imageSizesInput = this.page.getByLabel('Image Sizes');
		this.saveButton = this.page.getByRole('button', {name: 'Save'});
	}

	async goto() {
		await this.globalMenuPage.goToControlPanel(
			'Instance Settings',
			'AI Creator'
		);

		await this.page.waitForURL(
			/configurationScreenKey=ai-creator-openai-company-configuration/
		);

		await waitForSPAToBeLoaded(this.page);
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

	async enableOpenAIImageGeneration() {
		await this.goto();

		await this.openAIImageGenerationCheckbox.check();

		await expect(this.openAIImageGenerationCheckbox).toBeChecked();

		await this.saveButton.click();

		await waitForAlert(this.page);
	}

	async disableOpenAIImageGeneration() {
		await this.goto();

		await this.openAIImageGenerationCheckbox.uncheck();

		await expect(this.openAIImageGenerationCheckbox).not.toBeChecked();

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
