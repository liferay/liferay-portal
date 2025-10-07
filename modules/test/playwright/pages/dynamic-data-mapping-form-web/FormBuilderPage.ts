/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Locator, Page, expect} from '@playwright/test';

import {waitForAlert} from '../../utils/waitForAlert';
import {FormsPage} from './FormsPage';

export class FormBuilderPage {
	readonly copyButton: Locator;
	readonly entriesTab: Locator;
	readonly formsPage: FormsPage;
	readonly formSettingsButton: Locator;
	readonly formSettingsDoneButton: Locator;
	readonly formTab: Locator;
	readonly formTitle: Locator;
	readonly helpText: Locator;
	readonly newFormHeading: Locator;
	readonly newPageButton: Locator;
	readonly openFormButton: Locator;
	readonly page: Page;
	readonly previewButton: Locator;
	readonly publishButton: Locator;
	readonly requireCaptchaToggle: Locator;
	readonly saveButton: Locator;
	readonly settingsAdvancedTab: Locator;
	readonly shareButton: Locator;
	readonly translationManagerButton: Locator;
	readonly unpublishButton: Locator;

	constructor(page: Page) {
		this.copyButton = page.getByLabel('Copy');
		this.entriesTab = page.getByRole('button', {name: 'Entries'});
		this.formsPage = new FormsPage(page);
		this.formSettingsButton = page.getByRole('button', {name: 'Settings'});
		this.formSettingsDoneButton = page.getByRole('button', {name: 'Done'});
		this.formTab = page.getByRole('button', {name: 'Form'});
		this.formTitle = page.getByPlaceholder('Untitled Form');
		this.helpText = page.getByLabel('Help Text');
		this.newFormHeading = page.getByRole('heading', {name: 'New Form'});
		this.newPageButton = page.getByRole('button', {name: 'New Page'});
		this.openFormButton = page.getByRole('button', {
			name: 'Open Form',
		});
		this.page = page;
		this.previewButton = page
			.getByRole('button', {name: 'Preview'})
			.and(page.getByTitle('A form draft will be saved'));
		this.publishButton = page.getByRole('button', {
			exact: true,
			name: 'Publish',
		});
		this.requireCaptchaToggle = page.getByLabel('Require CAPTCHA');
		this.saveButton = page.getByRole('button', {name: 'Save'});
		this.settingsAdvancedTab = page.getByRole('tab', {name: 'Advanced'});
		this.shareButton = page.getByRole('button', {name: 'Share'});
		this.translationManagerButton = page
			.locator('#translationManager')
			.getByRole('button');
		this.unpublishButton = page.getByRole('button', {
			exact: true,
			name: 'Unpublish',
		});
	}

	async changeFormBuilderLanguage(language: string) {
		await this.translationManagerButton.click();
		await this.page.getByRole('menuitem', {name: language}).click();
	}

	async clickPreviewButton() {
		await this.previewButton.click();
	}

	async clickPublishFormButton() {
		await this.publishButton.click();

		await waitForAlert(this.page);
	}

	async clickSaveButton() {
		await this.saveButton.click();
	}

	async fillFormTitle(title: string) {
		await this.formTitle.fill(title);
	}

	async getFormSubmissionURL() {
		await this.shareButton.click();

		await this.copyButton.click();

		const formSubmissionURL = await this.page.evaluate(() => {
			const urlInput = document.querySelector(
				'.share-form-modal-item-link input.form-control[readonly]'
			) as HTMLInputElement;

			return urlInput.value;
		});

		return formSubmissionURL;
	}

	async goToNew(siteUrl?: Site['friendlyUrlPath']) {
		await this.formsPage.goTo(siteUrl);

		await expect(this.formsPage.formsHeader).toBeVisible();

		await this.formsPage.clickManagementToolbarNewButton();
	}

	async openFieldSettings(fieldLabel: string) {
		await this.page
			.locator('.ddm-field .form-group label.ddm-label', {
				hasText: fieldLabel,
			})
			.click({force: true});
	}

	async openPreviewForm() {
		const newTabPagePromise = new Promise<Page>((resolve) =>
			this.page.once('popup', resolve)
		);

		await this.clickPreviewButton();

		const newTabPage = await newTabPagePromise;

		await newTabPage.waitForLoadState('domcontentloaded');

		return newTabPage;
	}
}
