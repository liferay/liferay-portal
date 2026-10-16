/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Download, Locator, Page, expect} from '@playwright/test';
import * as fs from 'fs';
import * as path from 'path';

import {clickAndExpectToBeVisible} from '../../../../utils/clickAndExpectToBeVisible';
import {createTempDir} from '../../../../utils/createTempFile';
import {JournalPage} from '../../../journal-web/main/pages/JournalPage';

export class ExportImportTranslationPage {
	private readonly page: Page;

	readonly downloadCSVErrorReportLink: Locator;
	readonly exportButton: Locator;
	readonly exportFileFormatSelect: Locator;
	readonly exportForm: Locator;
	readonly failureTitle: Locator;
	readonly importToolbar: Locator;
	readonly journalPage: JournalPage;
	readonly replaceFilesButton: Locator;
	readonly selectFilesButton: Locator;
	readonly successTitle: Locator;
	readonly uploadAnotherFileLink: Locator;

	constructor(page: Page) {
		this.page = page;

		this.downloadCSVErrorReportLink = page.getByRole('link', {
			name: 'Download CSV Error Report',
		});
		this.exportForm = page.locator('form', {
			hasText: 'Languages to Translate To',
		});
		this.exportButton = this.exportForm.getByRole('button', {
			exact: true,
			name: 'Export',
		});
		this.exportFileFormatSelect =
			this.exportForm.getByLabel('Export File Format');
		this.failureTitle = page.locator('.h4.text-danger');
		this.importToolbar = page.locator('.management-bar');
		this.journalPage = new JournalPage(page);
		this.replaceFilesButton = page.getByRole('button', {
			name: 'Replace Files',
		});
		this.selectFilesButton = page.getByRole('button', {
			name: 'Select Files',
		});
		this.successTitle = page.locator('.h4.text-success');
		this.uploadAnotherFileLink = page.getByRole('link', {
			name: 'Upload Another File',
		});
	}

	languageCheckbox(language: string) {
		return this.exportForm.getByRole('checkbox', {
			exact: true,
			name: language,
		});
	}

	resultItem(fileName: string) {
		return this.page.locator('.list-group-item', {hasText: fileName});
	}

	async downloadCSVErrorReport() {
		const downloadPromise = this.page.waitForEvent('download');

		await this.downloadCSVErrorReportLink.click();

		return fs.readFileSync(await this.saveDownload(await downloadPromise), {
			encoding: 'utf-8',
		});
	}

	async export({format, languages}: {format?: string; languages: string[]}) {
		await this.exportForm.waitFor();

		if (format) {
			await this.exportFileFormatSelect.selectOption({label: format});
		}

		for (const language of languages) {
			await this.languageCheckbox(language).check();
		}

		await expect(this.exportButton).toBeEnabled();

		const downloadPromise = this.page.waitForEvent('download');

		await this.exportButton.click();

		return this.saveDownload(await downloadPromise);
	}

	async exportArticle(
		siteUrl: Site['friendlyUrlPath'],
		title: string,
		languages: string[],
		format?: string
	) {
		await this.gotoExport(siteUrl, title);

		return this.export({format, languages});
	}

	async gotoBulkExport(siteUrl: Site['friendlyUrlPath'], titles: string[]) {
		await this.journalPage.goto(siteUrl);

		for (const title of titles) {
			await this.journalPage.selectItemByTitle(title);
		}

		await clickAndExpectToBeVisible({
			target: this.exportForm,
			trigger: this.page.getByRole('button', {
				name: 'Export for Translations',
			}),
		});
	}

	async gotoBulkImport(siteUrl: Site['friendlyUrlPath']) {
		await this.journalPage.goto(siteUrl);

		await clickAndExpectToBeVisible({
			autoClick: true,
			target: this.page.getByRole('menuitem', {
				exact: true,
				name: 'Import Translations',
			}),
			trigger: this.page
				.locator('.control-menu-nav-item')
				.getByLabel('Options'),
		});

		await this.selectFilesButton.waitFor();
	}

	async gotoExport(siteUrl: Site['friendlyUrlPath'], title: string) {
		await this.goToArticleAction(siteUrl, 'Export for Translation', title);

		await this.exportForm.waitFor();
	}

	async gotoImport(siteUrl: Site['friendlyUrlPath'], title: string) {
		await this.goToArticleAction(siteUrl, 'Import Translation', title);

		await this.selectFilesButton.waitFor();
	}

	async submit(action: 'Publish' | 'Save as Draft' | 'Submit for Workflow') {
		const button = this.importToolbar.getByRole('button', {
			exact: true,
			name: action,
		});

		await expect(button).toBeEnabled();

		await button.click();

		await expect(
			this.successTitle.or(this.failureTitle).first()
		).toBeVisible();
	}

	async upload(filePaths: string[], trigger?: Locator) {
		await expect(async () => {
			const fileChooserPromise = this.page.waitForEvent('filechooser', {
				timeout: 2000,
			});

			await (trigger || this.selectFilesButton).click({timeout: 1000});

			const fileChooser = await fileChooserPromise;

			await fileChooser.setFiles(filePaths);

			for (const filePath of filePaths) {
				await expect(
					this.page.getByText(path.basename(filePath), {exact: true})
				).toBeVisible({timeout: 2000});
			}
		}).toPass({timeout: 15000});
	}

	async uploadAnotherFile() {
		await clickAndExpectToBeVisible({
			target: this.selectFilesButton,
			trigger: this.uploadAnotherFileLink,
		});
	}

	private async goToArticleAction(
		siteUrl: Site['friendlyUrlPath'],
		action: string,
		title: string
	) {
		await this.journalPage.goto(siteUrl);

		await clickAndExpectToBeVisible({
			autoClick: true,
			target: this.page.getByRole('menuitem', {
				exact: true,
				name: action,
			}),
			trigger: this.page
				.getByTestId('row')
				.filter({hasText: title})
				.getByRole('button', {name: /^Actions for /}),
		});
	}

	private async saveDownload(download: Download) {
		const filePath = path.join(
			createTempDir(),
			download.suggestedFilename()
		);

		await download.saveAs(filePath);

		return filePath;
	}
}
