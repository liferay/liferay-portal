/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Locator, Page, expect} from '@playwright/test';

import {changeManagementToolbarView} from '../../../../utils/changeManagementToolbarView';
import {clickAndExpectToBeHidden} from '../../../../utils/clickAndExpectToBeHidden';
import {clickAndExpectToBeVisible} from '../../../../utils/clickAndExpectToBeVisible';
import fillAndClickOutside from '../../../../utils/fillAndClickOutside';
import {getRandomInt} from '../../../../utils/getRandomInt';
import getRandomString from '../../../../utils/getRandomString';
import {openFieldset} from '../../../../utils/openFieldset';
import {waitForAlert} from '../../../../utils/waitForAlert';
import {JournalPage} from './JournalPage';

export class JournalEditArticlePage {
	readonly page: Page;

	readonly changesSavedIndicator: Locator;
	readonly clearButton: Locator;
	readonly content: Locator;
	readonly defaultTemplateButton: Locator;
	readonly duplicateButton: Locator;
	readonly friendlyURLInput: Locator;
	readonly friendlyUrlToggle: Locator;
	readonly historyButton: Locator;
	readonly inputPermissionsViewRole: Locator;
	readonly journalPage: JournalPage;
	readonly previewButton: Locator;
	readonly propertiesTab: Locator;
	readonly publishDropdown: Locator;
	readonly publishButton: Locator;
	readonly redoButton: Locator;
	readonly selectButton: Locator;
	readonly titleInput: Locator;
	readonly undoButton: Locator;
	readonly alertErrorMessage: Locator;

	constructor(page: Page) {
		this.page = page;
		this.alertErrorMessage = page.locator(
			'div.article-content-content >> div.alert-danger'
		);
		this.changesSavedIndicator = page.locator(
			'#_com_liferay_journal_web_portlet_JournalPortlet_changesSavedIndicator'
		);

		this.clearButton = page.getByRole('button', {name: 'Clear'});
		this.content = page.getByText('Content', {exact: true});
		this.defaultTemplateButton = page.getByRole('button', {
			name: 'Default Template',
		});
		this.duplicateButton = page.getByLabel('Add Duplicate Field Text');
		this.friendlyURLInput = page.locator(
			'#_com_liferay_journal_web_portlet_JournalPortlet_friendlyURL'
		);
		this.friendlyUrlToggle = page.locator('a[href="#friendlyUrlContent"]');
		this.historyButton = page.getByLabel('History');
		this.inputPermissionsViewRole = page.locator(
			'#_com_liferay_journal_web_portlet_JournalPortlet_inputPermissionsViewRole'
		);
		this.journalPage = new JournalPage(page);
		this.previewButton = page.getByLabel('A draft will be saved before');
		this.propertiesTab = page.getByRole('tab', {
			name: /properties|propriétés/i,
		});
		this.publishDropdown = page.getByRole('button', {
			name: /publish options|options de publication/i,
		});
		this.publishButton = page.getByTitle(
			/publish article|submit article for workflow|publier article|envoyer article pour le flux de travail/i
		);
		this.redoButton = page.getByTitle('Redo', {exact: true});
		this.selectButton = page.getByRole('button', {
			exact: true,
			name: 'Select',
		});
		this.titleInput = page.locator(
			'#_com_liferay_journal_web_portlet_JournalPortlet_titleMapAsXML'
		);
		this.undoButton = page.getByTitle('Undo', {exact: true});
	}

	async assertPrivateContentIconInRelatedAssetPopUp() {
		await expect(
			this.page
				.getByRole('dialog')
				.getByLabel('Not Visible to Guest Users')
				.locator('use')
				.first()
		).toBeVisible({timeout: 1000});
	}

	async assertScheduledArticleDates(
		title: string,
		publishDate: string,
		{workflow} = {workflow: false},
		expirationDate?: string,
		reviewDate?: string
	) {
		await this.editArticle(title);

		await clickAndExpectToBeVisible({
			autoClick: true,
			target: this.page.getByRole('menuitem', {
				exact: true,
				name: workflow
					? 'Schedule Publication and Submit for Workflow'
					: 'Schedule Publication',
			}),
			trigger: this.publishDropdown,
		});

		if (expirationDate) {
			await expect(this.page.getByText('Expiration Date')).toHaveValue(
				expirationDate
			);
		}

		await expect(this.page.getByLabel('Date and Time')).toHaveValue(
			publishDate
		);

		if (reviewDate) {
			await expect(this.page.getByText('Review Date')).toHaveValue(
				reviewDate
			);
		}
	}

	async changeDefaultLanguage(languageId: string) {
		await this.page.getByRole('button', {name: 'Change'}).click();

		await this.page.getByRole('menuitem', {name: languageId}).click();
	}

	async changeLanguage(languageId: string) {
		await this.page
			.getByRole('combobox', {
				name: 'Select a language',
			})
			.click();

		await this.page.locator(`button[id="${languageId}"]`).click();
	}

	async clearAllCategories(vocabulary: string) {
		await this.openFieldSet('Categorization', 'categorization');

		await this.page
			.locator('.form-group')
			.filter({has: this.page.getByLabel(vocabulary, {exact: true})})
			.getByRole('button', {name: 'Clear All'})
			.click();
	}

	async createAndPublishBasicArticle(title?: string) {
		const articleTitle = title || getRandomString();

		await this.fillTitle(articleTitle);

		await this.publishArticle();
	}

	async createArticleForStructure({
		structureName,
		title,
	}: {
		structureName?: string;
		title?: string;
	} = {}) {
		await fillAndClickOutside(
			this.page,
			this.page.getByPlaceholder('Untitled ' + structureName),
			title
		);

		await this.publishArticle();

		await waitForAlert(
			this.page,
			`Success:${title} was created successfully.`
		);
	}

	async createArticleWithCustomArticleId(
		page: Page,
		site: Site,
		articleId: string,
		title?: string
	) {
		await this.goto({siteUrl: site.friendlyUrlPath});

		await this.fillTitle(title || getRandomString());

		const articleIdInput = page.locator(
			'input[name="_com_liferay_journal_web_portlet_JournalPortlet_newArticleId"]'
		);
		await articleIdInput.fill(articleId || String(getRandomInt()));

		await this.publishArticle();
	}

	async createArticleWithDuplicatedField(
		structureName: string,
		site?: Site,
		title?: string
	) {
		await this.goto({
			siteUrl: site.friendlyUrlPath,
			structureName,
		});

		await fillAndClickOutside(
			this.page,
			this.titleInput,
			title || getRandomString()
		);

		const fields = this.page.locator(
			'input[id^="_com_liferay_journal_web_portlet_JournalPortlet_ddm$$Text"]'
		);

		// The Fields panel can load collapsed and may re-collapse while it
		// finishes initializing, so re-expand it before each interaction.

		await expect(async () => {
			await openFieldset(this.page, 'Fields');

			await fields.first().fill('Text Field', {timeout: 2000});
		}).toPass();

		await this.duplicateButton.click();

		await expect(async () => {
			await openFieldset(this.page, 'Fields');

			await fields.nth(1).fill('Duplicated Text Field', {timeout: 2000});
		}).toPass();

		await this.publishArticle();
	}

	async createBasicArticleWithFriendlyURL(site, structureName?: string) {
		await this.journalPage.goto(site.friendlyUrlPath);
		await this.journalPage.goToCreateArticle(
			structureName || 'Basic Web Content'
		);

		const title = getRandomString();
		await this.fillTitle(title);
		await this.fillFriendlyURL('test');

		await this.publishArticle();
		await expect(this.page.getByTitle(title, {exact: true})).toBeVisible();
	}

	async editAndPublishExistingBasicArticle(title: string) {
		await this.editArticle(title);

		await this.fillTitle(title);

		await this.publishArticle(true);
	}

	async editArticle(title: string) {
		await this.journalPage.goToJournalArticleAction('Edit', title);

		await this.propertiesTab.waitFor();

		await this.page.locator('body').click();
	}

	async editURL(title: string, url: string) {
		await this.page
			.locator('.ck-content')
			.getByRole('link', {name: title})
			.click();
		await this.page.getByRole('button', {name: 'Edit link'}).click();
		await this.page.getByRole('textbox', {name: 'Link URL'}).fill(url);
		await this.page.getByRole('button', {name: 'Update'}).click();
	}

	async fillContent(content: string) {
		const ckEditor5Content = this.journalPage.articleContentTextBox;

		if (await ckEditor5Content.count()) {
			await ckEditor5Content.fill(content);
			await ckEditor5Content.press('Enter');

			return;
		}

		// Under the autosave feature flags the content field renders as a
		// CKEditor 4 instance whose editable lives inside an iframe.

		const ckEditor4Content = this.page
			.getByRole('textbox', {exact: true, name: 'Content'})
			.frameLocator('iframe.cke_wysiwyg_frame')
			.locator('body');

		await ckEditor4Content.fill(content);
		await ckEditor4Content.press('Enter');
	}

	async fillFriendlyURL(friendlyURL: string) {
		await fillAndClickOutside(
			this.page,
			this.friendlyURLInput,
			friendlyURL
		);
	}

	async fillTitle(title: string) {
		await this.propertiesTab.waitFor();

		await fillAndClickOutside(this.page, this.titleInput, title);
	}

	async goto({
		siteUrl,
		structureName,
	}: {
		siteUrl?: Site['friendlyUrlPath'];
		structureName?: string;
	} = {}) {
		await this.journalPage.goto(siteUrl);
		await this.journalPage.goToCreateArticle(structureName);

		// Do it twice so we decrease flakiness

		await this.journalPage.goto(siteUrl);
		await this.journalPage.goToCreateArticle(structureName);

		await this.propertiesTab.waitFor();
	}

	async openDMItemSelectorForImages() {
		await this.page.getByLabel('Image', {exact: true}).click();
		await this.page
			.frameLocator('iframe[title="Select Item"]')
			.getByRole('link', {name: 'Documents and Media'})
			.click();
	}

	async openFieldSet(assetType: string, fieldSetId: string) {
		const isOpened = await this.page
			.locator(`#${fieldSetId}Content`)
			.evaluate((element) => element.classList.contains('show'));

		if (!isOpened) {
			await this.page.getByRole('button', {name: assetType}).click();
		}
	}

	async openRelatedAsset() {
		await this.openFieldSet('Related Assets', 'relatedAssets');
		await this.page.getByLabel('Select Items').click();
	}

	async publishArticle(
		existingArticle?: boolean,
		viewableBy?: 'Site Members' | 'Owner'
	) {
		if (existingArticle) {
			await this.publishButton.click();

			return;
		}

		await clickAndExpectToBeVisible({
			autoClick: true,
			target: this.page.getByRole('menuitem', {
				name: /publish with permissions|publier avec permissions/i,
			}),
			trigger: this.publishDropdown,
		});

		const viewableBySelect = this.page.getByLabel(
			/Viewable By|Visualisable avec/i
		);

		await expect(viewableBySelect).toBeVisible({
			timeout: 2000,
		});

		if (viewableBy) {
			await viewableBySelect.selectOption(viewableBy);
		}

		await this.page
			.locator('[role="dialog"]')
			.getByRole('button', {name: /publish|publier/i})
			.click();
	}

	async saveAsDraftWithPermissions(title: string) {
		await this.fillTitle(title);

		const draftButton = this.page
			.getByLabel('Save as Draft With Permissions')
			.getByRole('button', {name: 'Save as Draft'});

		await expect(async () => {
			await this.page
				.getByRole('button', {exact: true, name: 'Save as Draft'})
				.click();

			await expect(draftButton).toBeVisible();
		}).toPass();

		await draftButton.click();

		await expect(this.page.getByText('Version: 1.0 Draft')).toBeVisible();
	}

	async scheduleArticle(
		title: string,
		publishDate: string,
		{workflow} = {workflow: false},
		expirationDate?: string,
		reviewDate?: string
	) {
		await this.fillTitle(title);

		await openFieldset(this.page, 'Schedule');

		if (expirationDate) {
			await this.page.getByText('Never Expire').click();

			await this.page.getByText('Expiration Date').fill(expirationDate);
		}

		if (reviewDate) {
			await this.page.getByText('Never Review').click();

			await this.page.getByText('Review Date').fill(reviewDate);
		}

		await clickAndExpectToBeVisible({
			autoClick: true,
			target: this.page.getByRole('menuitem', {
				exact: true,
				name: workflow
					? 'Schedule Publication and Submit for Workflow'
					: 'Schedule Publication',
			}),
			trigger: this.publishDropdown,
		});

		await this.page.getByLabel('Date and Time').fill(publishDate);

		await this.page
			.locator('.modal-footer')
			.getByRole('button', {
				name: workflow ? 'Submit for Workflow' : 'Schedule',
			})
			.click();

		await waitForAlert(
			this.page,
			workflow
				? `Success:${title} has been scheduled and submitted for workflow.`
				: `Success:${title} will be published on`
		);

		await changeManagementToolbarView(this.page, 'list');

		const row = this.page
			.locator('.list-group-item')
			.filter({hasText: title});

		await row
			.locator('span.label')
			.filter({hasText: workflow ? 'Pending' : 'Scheduled'})
			.waitFor();
	}

	async selectFileFromDocumentsAndMedia(fileName: string) {
		await this.page.getByLabel('File', {exact: true}).click();

		const selectDocumentIframe = this.page.frameLocator(
			'iframe[id$="selectDocumentLibrary_iframe_"]'
		);

		await selectDocumentIframe.locator('.breadcrumb-link').first().click();

		await selectDocumentIframe
			.getByRole('link', {name: /^Liferay DXP( Site)?$/})
			.click();

		const searchBox = selectDocumentIframe.getByRole('searchbox');

		await searchBox.fill(fileName);
		await searchBox.press('Enter');

		await selectDocumentIframe
			.getByTestId('row')
			.getByText(fileName)
			.dblclick();
	}

	async selectCategories(vocabulary: string, categories: string[]) {
		await this.openFieldSet('Categorization', 'categorization');

		await this.page
			.getByRole('button', {name: `Select ${vocabulary}`})
			.click();

		const selectVocabularyModal = this.page.getByRole('dialog', {
			name: `Select ${vocabulary}`,
		});

		for (const category of categories) {
			await expect(async () => {
				await selectVocabularyModal
					.locator('li')
					.filter({hasText: category})
					.getByRole('checkbox')
					.check({timeout: 2000});
			}).toPass({timeout: 10000});
		}

		await selectVocabularyModal.getByRole('button', {name: 'Done'}).click();
	}

	async selectSpecificDisplayPage(displayPageName: string) {
		await this.openFieldSet('Display Page', 'displayPage');
		await this.page
			.getByLabel('Select Display Page Type')
			.selectOption('Specific');
		await this.page
			.getByRole('button', {name: 'Select Display Page'})
			.click();
		const selectDisplayPageModal = this.page.frameLocator(
			'iframe[title*="Select Display Page"]'
		);

		await selectDisplayPageModal
			.locator('.card-type-asset')
			.filter({hasText: displayPageName})
			.click({trial: true});

		await clickAndExpectToBeHidden({
			target: this.page.locator('.modal-title', {
				hasText: 'Select Display Page',
			}),
			trigger: selectDisplayPageModal
				.locator('.card-type-asset')
				.filter({hasText: displayPageName}),
		});
	}

	async submitArticleForWorkflow(title: string) {
		await this.fillTitle(title);

		await expect(async () => {
			await clickAndExpectToBeVisible({
				autoClick: true,
				target: this.page.getByRole('menuitem', {
					name: /submit for workflow with permissions/i,
				}),
				trigger: this.publishDropdown,
			});

			await expect(this.page.getByLabel('Viewable By')).toBeVisible({
				timeout: 2000,
			});
		}).toPass();

		await this.page
			.locator('[role="dialog"]')
			.getByRole('button', {name: /submit for workflow/i})
			.click();

		await this.page
			.locator(
				'#_com_liferay_journal_web_portlet_JournalPortlet_articlesSearchContainer .list-group-item'
			)
			.filter({hasText: title})
			.waitFor();

		const row = this.page
			.locator('.list-group-item')
			.filter({hasText: title});

		await row.locator('span.label').filter({hasText: 'Pending'}).waitFor();
	}

	async uploadImageFromWebContentImages(filePath: string) {
		await this.page.getByLabel('Image', {exact: true}).click();

		const itemSelectorIframe = this.page.frameLocator(
			'iframe[id$="selectDocumentLibrary_iframe_"]'
		);

		await itemSelectorIframe
			.getByRole('link', {name: 'Web Content Images'})
			.click();

		const addButton = itemSelectorIframe.getByRole('button', {
			exact: true,
			name: 'Add',
		});

		await expect(async () => {
			await itemSelectorIframe
				.locator('input[type="file"]')
				.setInputFiles(filePath, {timeout: 5000});

			await expect(addButton).toBeVisible({timeout: 5000});
		}).toPass();

		await clickAndExpectToBeHidden({
			target: this.page.locator(
				'iframe[id$="selectDocumentLibrary_iframe_"]'
			),
			trigger: addButton,
		});
	}
}
