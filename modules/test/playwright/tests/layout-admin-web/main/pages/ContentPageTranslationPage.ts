/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Download, Locator, Page, expect} from '@playwright/test';

import {PagesAdminPage} from '../../../../pages/layout-admin-web/PagesAdminPage';
import {clickAndExpectToBeVisible} from '../../../../utils/clickAndExpectToBeVisible';
import fillAndClickOutside from '../../../../utils/fillAndClickOutside';
import {getTempDir} from '../../../../utils/temp';
import {waitForAlert} from '../../../../utils/waitForAlert';

export class ContentPageTranslationPage {
	private readonly page: Page;

	private readonly experienceSelector: Locator;
	private readonly exportButton: Locator;
	private readonly pagesAdminPage: PagesAdminPage;
	private readonly publishButton: Locator;
	private readonly saveAsDraftButton: Locator;
	private readonly selectFilesButton: Locator;

	constructor(page: Page) {
		this.page = page;

		this.experienceSelector = page.locator(
			'.management-bar #experience-selector'
		);

		this.exportButton = page.getByRole('button', {name: 'Export'});

		this.pagesAdminPage = new PagesAdminPage(page);

		this.publishButton = page
			.locator('.management-bar')
			.getByText('Publish');

		this.saveAsDraftButton = page
			.locator('.management-bar')
			.getByText('Save as Draft');

		this.selectFilesButton = page.getByRole('button', {
			name: 'Select Files',
		});
	}

	async goto({
		doAsUserId,
		pageName,
		siteUrl,
	}: {
		doAsUserId?: string;
		pageName: string;
		siteUrl?: Site['friendlyUrlPath'];
	}) {
		await this.pagesAdminPage.goto(siteUrl, doAsUserId);

		await this.pagesAdminPage.clickOnAction('Translate', pageName);

		await this.page
			.locator('.management-bar')
			.getByText('Translate From')
			.waitFor();
	}

	async bulkExportTranslations({
		languages,
		pageNames,
	}: {
		languages: string[];
		pageNames: string[];
	}) {
		await this.pagesAdminPage.selectPages(pageNames);

		await clickAndExpectToBeVisible({
			target: this.page.getByText('Languages to Translate To'),
			trigger: this.page.getByRole('button', {
				name: 'Export for Translations',
			}),
		});

		for (const language of languages) {
			await expect(async () => {
				await this.page.getByLabel(language).click({timeout: 1000});

				await expect(this.exportButton).toBeEnabled({timeout: 1000});

				await expect(this.page.getByLabel(language)).toBeChecked({
					timeout: 1000,
				});
			}).toPass();
		}

		let download: Download;

		await expect(async () => {
			const downloadPromise = this.page.waitForEvent('download');

			await this.exportButton.click({timeout: 1000});

			download = await downloadPromise;
		}).toPass({timeout: 3000});

		const filePath = getTempDir() + download!.suggestedFilename();

		await download!.saveAs(filePath);

		return filePath;
	}

	async exportTranslations({
		languages,
		pageName,
	}: {
		languages: string[];
		pageName: string;
	}) {
		await this.pagesAdminPage.clickOnAction(
			'Export for Translation',
			pageName
		);

		for (const language of languages) {
			await expect(async () => {
				await this.page.getByLabel(language).click({timeout: 1000});

				await expect(this.exportButton).toBeEnabled({timeout: 1000});

				await expect(this.page.getByLabel(language)).toBeChecked({
					timeout: 1000,
				});
			}).toPass();
		}

		let download: Download;

		await expect(async () => {
			const downloadPromise = this.page.waitForEvent('download');

			await this.exportButton.click({timeout: 1000});

			download = await downloadPromise;
		}).toPass({timeout: 3000});

		const filePath = getTempDir() + download!.suggestedFilename();

		await download!.saveAs(filePath);

		return filePath;
	}

	async importTranslations({
		filePath,
		pageName,
	}: {
		filePath: string;
		pageName?: string;
	}) {
		if (pageName) {
			await this.pagesAdminPage.clickOnAction(
				'Import Translation',
				pageName
			);
		}
		else {
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
		}

		await this.page
			.getByText('Please upload your translation files')
			.waitFor();

		await expect(async () => {
			const fileChooserPromise = this.page.waitForEvent('filechooser');

			await this.selectFilesButton.click({timeout: 1000});

			const fileChooser = await fileChooserPromise;

			await fileChooser.setFiles(filePath);

			await expect(this.page.getByText('Remove Files')).toBeVisible({
				timeout: 1000,
			});
		}).toPass({timeout: 5000});

		await expect(async () => {
			await this.publishButton.click({timeout: 1000});

			await expect(this.page.getByText('Saved')).toBeVisible({
				timeout: 3000,
			});

			await expect(
				this.page.getByText('could not be published')
			).not.toBeVisible({timeout: 1000});
		}).toPass({timeout: 5000});
	}

	async publish() {
		await expect(async () => {
			await this.publishButton.click({timeout: 1000});

			await waitForAlert(this.page);
		}).toPass();
	}

	async saveAsDraft() {
		await expect(async () => {
			await this.saveAsDraftButton.click({timeout: 1000});

			await waitForAlert(this.page);
		}).toPass();
	}

	async switchExperience(name: string) {
		await expect(async () => {
			await this.experienceSelector.selectOption('Default', {
				timeout: 1000,
			});

			await expect(
				this.page
					.locator('.fieldset-title', {hasText: 'Basic Information'})
					.nth(0)
			).toBeVisible({timeout: 1000});

			await this.experienceSelector.selectOption(name, {timeout: 1000});

			if (name !== 'Default') {
				await expect(
					this.page
						.locator('.fieldset-title', {
							hasText: 'Basic Information',
						})
						.nth(0)
				).not.toBeVisible({timeout: 2000});
			}
		}).toPass();
	}

	async switchLanguage({from, to}: {from?: string; to?: string}) {
		if (from) {
			await clickAndExpectToBeVisible({
				autoClick: true,
				target: this.page.getByRole('menuitem', {
					name: from,
				}),
				trigger: this.page
					.locator('.management-bar .dropdown-toggle')
					.nth(0),
			});

			await this.page.locator('.col-md-6').getByText(from).waitFor();
		}

		if (to) {
			await clickAndExpectToBeVisible({
				autoClick: true,
				target: this.page.getByRole('menuitem', {
					name: to,
				}),
				trigger: this.page
					.locator('.management-bar .dropdown-toggle')
					.nth(1),
			});
		}
	}

	async translateEditable({
		editableId,
		from = 'en-US',
		to,
		value,
	}: {
		editableId: string;
		from?: string;
		to: string;
		value: string;
	}) {

		// Select languages

		await this.switchLanguage({from, to});

		// Fill value

		const input = this.page.getByLabel(editableId);

		await expect(input).toBeEnabled();

		await fillAndClickOutside(this.page, input, value);
	}
}
