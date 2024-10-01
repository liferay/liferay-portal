/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Locator, Page} from '@playwright/test';

import {waitForAlert} from '../../utils/waitForAlert';
import {ApplicationsMenuPage} from './ApplicationsMenuPage';

const MOCK_API_KEY = 'VALID_API_KEY';
const STR_BLANK = '';

export class AICreatorInstanceSettingsPage {
	readonly applicationsMenuPage: ApplicationsMenuPage;
	readonly apiKeyInput: Locator;
	readonly dalleCheckbox: Locator;
	readonly page: Page;
	readonly saveButton: Locator;

	constructor(page: Page) {
		this.page = page;

		this.applicationsMenuPage = new ApplicationsMenuPage(page);
		this.apiKeyInput = this.page.getByLabel('API Key');
		this.dalleCheckbox = this.page.getByText(
			'Enable DALL-E to Create Images'
		);
		this.saveButton = this.page.getByRole('button', {name: 'Save'});
	}

	async goto() {
		await this.applicationsMenuPage.goToAICreator();
	}

	async enableDalleCreateImages() {
		await this.goto();

		await this.dalleCheckbox.check();
		await this.saveButton.click();

		await waitForAlert(this.page);
	}

	async disableDalleCreateImages() {
		await this.goto();

		await this.dalleCheckbox.uncheck();
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
