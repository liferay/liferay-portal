/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Locator, Page, expect} from '@playwright/test';

import {waitForAlert} from '../../../../utils/waitForAlert';
import {JournalPage} from '../../../journal-web/main/pages/JournalPage';

type TranslationFields = {
	content?: string;
	description?: string;
	title?: string;
};

export class WebContentTranslationPage {
	readonly page: Page;

	readonly baseLocaleToggle: Locator;
	readonly contentEditor: Locator;
	readonly descriptionEditor: Locator;
	readonly journalPage: JournalPage;
	readonly publishButton: Locator;
	readonly saveAsDraftButton: Locator;
	readonly submitForWorkflowButton: Locator;
	readonly targetLocaleToggle: Locator;
	readonly titleInput: Locator;

	constructor(page: Page) {
		this.page = page;

		this.baseLocaleToggle = page
			.locator('button.dropdown-toggle')
			.filter({hasText: /^[a-z]{2}-[A-Z]{2}$/})
			.first();
		this.contentEditor = page
			.locator('.lfr-ck')
			.nth(1)
			.locator('.ck-content');
		this.descriptionEditor = page
			.locator('.lfr-ck')
			.nth(0)
			.locator('.ck-content');
		this.journalPage = new JournalPage(page);
		this.publishButton = page.getByRole('button', {name: 'Publish'});
		this.saveAsDraftButton = page.getByRole('button', {
			name: 'Save as Draft',
		});
		this.submitForWorkflowButton = page.getByRole('button', {
			name: 'Submit for Workflow',
		});
		this.targetLocaleToggle = page
			.locator('button.dropdown-toggle')
			.filter({hasText: /^[a-z]{2}-[A-Z]{2}$/})
			.last();
		this.titleInput = page.locator(
			'#_com_liferay_translation_web_internal_portlet_TranslationPortlet_infoField--JournalArticle_title--0'
		);
	}

	async assertCustomFieldValue(fieldName: string, value: string) {
		await expect(this.customField(fieldName)).toHaveValue(value);
	}

	async assertRichTextValue(index: number, value: string) {
		await expect(this.richTextEditor(index)).toContainText(value);
	}

	async assertTargetFields({content, description, title}: TranslationFields) {
		if (title !== undefined) {
			await expect(this.titleInput).toHaveValue(title);
		}

		if (description !== undefined) {
			await expect(this.descriptionEditor).toContainText(description);
		}

		if (content !== undefined) {
			await expect(this.contentEditor).toContainText(content);
		}
	}

	customField(fieldName: string): Locator {
		return this.page.locator(
			`#_com_liferay_translation_web_internal_portlet_TranslationPortlet_infoField--DDMStructure_${fieldName}--0`
		);
	}

	async fillCustomField(fieldName: string, value: string) {
		await this.customField(fieldName).fill(value);
	}

	async fillRichText(index: number, value: string) {
		await this.fillEditor(this.richTextEditor(index), value);
	}

	richTextEditor(index: number): Locator {
		return this.page.locator('.lfr-ck').nth(index).locator('.ck-content');
	}

	async changeBaseLocale(locale: string) {
		await this.baseLocaleToggle.click();

		await this.page
			.locator('.dropdown-menu.show button.dropdown-item')
			.filter({hasText: new RegExp(`^${locale}$`)})
			.click();

		await expect(this.baseLocaleToggle).toHaveText(locale);
	}

	async changeTargetLocale(locale: string) {
		await this.targetLocaleToggle.click();

		await this.page
			.locator('.dropdown-menu.show button.dropdown-item')
			.filter({hasText: new RegExp(`^${locale}$`)})
			.click();

		await expect(this.targetLocaleToggle).toHaveText(locale);
	}

	async open(site: Site, title: string) {
		await this.journalPage.goto(site.friendlyUrlPath);

		await this.journalPage.goToJournalArticleAction('Translate', title);
	}

	async publish() {
		await this.publishButton.click();

		await waitForAlert(this.page);
	}

	async saveAsDraft() {
		await this.saveAsDraftButton.click();

		await waitForAlert(this.page);
	}

	async submitForWorkflow() {
		await this.submitForWorkflowButton.click();

		await waitForAlert(this.page);
	}

	async translateFields({content, description, title}: TranslationFields) {
		if (title !== undefined) {
			await this.titleInput.fill(title);
		}

		if (description !== undefined) {
			await this.fillEditor(this.descriptionEditor, description);
		}

		if (content !== undefined) {
			await this.fillEditor(this.contentEditor, content);
		}
	}

	private async fillEditor(editor: Locator, value: string) {
		await editor.click();

		await this.page.keyboard.press('Control+KeyA');
		await this.page.keyboard.press('Backspace');
		await this.page.keyboard.type(value);
	}
}
