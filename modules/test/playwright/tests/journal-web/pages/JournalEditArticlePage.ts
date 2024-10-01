/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Locator, Page, expect} from '@playwright/test';

import {clickAndExpectToBeHidden} from '../../../utils/clickAndExpectToBeHidden';
import {clickAndExpectToBeVisible} from '../../../utils/clickAndExpectToBeVisible';
import fillAndClickOutside from '../../../utils/fillAndClickOutside';
import getRandomString from '../../../utils/getRandomString';
import {waitForAlert} from '../../../utils/waitForAlert';
import {JournalPage} from './JournalPage';

export class JournalEditArticlePage {
	readonly page: Page;

	readonly changesSavedIndicator: Locator;
	readonly friendlyURLInput: Locator;
	readonly friendlyUrlToggle: Locator;
	readonly historyButton: Locator;
	readonly journalPage: JournalPage;
	readonly propertiesTab: Locator;
	readonly publishButton: Locator;
	readonly redoButton: Locator;
	readonly submitForWorkflowButton: Locator;
	readonly titleInput: Locator;
	readonly undoButton: Locator;

	constructor(page: Page) {
		this.page = page;

		this.changesSavedIndicator = page.locator(
			'#_com_liferay_journal_web_portlet_JournalPortlet_changesSavedIndicator'
		);
		this.friendlyURLInput = page.locator(
			'#_com_liferay_journal_web_portlet_JournalPortlet_friendlyURL'
		);
		this.friendlyUrlToggle = page.locator('#friendlyUrlToggle');
		this.historyButton = page.getByLabel('History');
		this.journalPage = new JournalPage(page);
		this.propertiesTab = page.getByRole('tab', {name: 'Properties'});
		this.publishButton = page.locator(
			'#_com_liferay_journal_web_portlet_JournalPortlet_publishButton'
		);
		this.redoButton = page.getByTitle('Redo', {exact: true});
		this.submitForWorkflowButton = page.getByRole('button', {
			name: 'Submit for Workflow',
		});
		this.titleInput = page.locator(
			'#_com_liferay_journal_web_portlet_JournalPortlet_titleMapAsXML'
		);
		this.undoButton = page.getByTitle('Undo', {exact: true});
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

	async assertPrivateContentIconInRelatedAssetPopUp(assetType: string) {
		await expect(
			this.page
				.frameLocator(`iframe[title="Select ${assetType}"]`)
				.getByLabel('Not Visible to Guest Users')
				.locator('use')
		).toBeVisible({timeout: 1000});
	}

	async changeViewInRelatedAssetPopUp(assetType: string, viewType: string) {
		await this.page
			.frameLocator(`iframe[title="Select ${assetType}"]`)
			.getByLabel('Select View, Currently Selected: ')
			.waitFor();
		await this.page
			.frameLocator(`iframe[title="Select ${assetType}"]`)
			.getByLabel('Select View, Currently Selected: ')
			.click();
		await this.page
			.frameLocator(`iframe[title="Select ${assetType}"]`)
			.getByRole('menuitem', {name: viewType})
			.click();
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

	async createAndPublishBasicArticle(title?: string) {
		const articleTitle = title || getRandomString();

		await this.fillTitle(articleTitle);

		await this.publishArticle();
	}

	async publishArticle() {
		await clickAndExpectToBeVisible({
			autoClick: true,
			target: this.page.getByRole('menuitem', {
				name: 'Publish With Permissions',
			}),
			trigger: this.page.getByRole('button', {
				name: 'Select and Confirm Publish Settings',
			}),
		});

		await this.page
			.getByRole('button', {exact: true, name: 'Publish'})
			.click();
	}

	async editArticle(title: string) {
		await this.journalPage.goToJournalArticleAction('Edit', title);

		await this.propertiesTab.waitFor();

		await this.page.locator('body').click();
	}

	async fillContent(content: string) {
		await this.journalPage.articleContentTextBox.fill(content);
		await this.journalPage.articleContentTextBox.press('Enter');
	}

	async fillFriendlyURL(friendlyURL: string) {
		if (await this.friendlyURLInput.isHidden()) {
			await this.friendlyUrlToggle.click();
		}
		await this.friendlyURLInput.fill(friendlyURL);
	}

	async createBasicArticleWithFriendlyURL(site, page, articleTitle?: string) {
		await this.journalPage.goto(site.friendlyUrlPath);
		await this.journalPage.goToCreateArticle(
			articleTitle || 'Basic Web Content'
		);
		await this.fillFriendlyURL('test');
		const title = getRandomString();
		await this.titleInput.fill(title);
		await this.publishButton.click();
		await expect(page.getByTitle(title, {exact: true})).toBeVisible();
	}

	async createWCWithBasicPublishButton(articleTitle: string) {
		await this.titleInput.fill(articleTitle);
		await this.publishButton.waitFor();
		await this.publishButton.click();

		await waitForAlert(
			this.page,
			`Success:${articleTitle} was created successfully.`
		);
	}

	async fillTitle(title: string) {
		await this.propertiesTab.waitFor();

		await fillAndClickOutside(this.page, this.titleInput, title);
	}

	async editAndPublishExistingBasicArticle(title: string) {
		await this.editArticle(title);

		await this.fillTitle(title);

		await this.publishButton.waitFor();

		await this.publishButton.click();

		await waitForAlert(
			this.page,
			`Success:${title} was updated successfully.`
		);
	}

	async openDMItemSelectorForImages() {
		await this.page.getByLabel('Image', {exact: true}).click();
		await this.page
			.frameLocator('iframe[title="Select Item"]')
			.getByRole('link', {name: 'Documents and Media'})
			.click();
	}

	async openFieldSet(assetType: string, fieldSetId: string) {
		if (
			!(await this.page.$eval('#' + fieldSetId + 'Content', (item) =>
				item.classList.contains('show')
			))
		) {
			await this.page.getByRole('button', {name: assetType}).click();
		}
	}

	async openRelatedAsset(assetType: string) {
		await this.openFieldSet('Related Assets', 'relatedAssets');
		await this.page.getByLabel('Select Items').click();
		await this.page.getByRole('menuitem', {name: assetType}).click();
	}

	async selectSpecificDisplayPage(displayPageName: string) {
		await this.openFieldSet('Display Page', 'displayPage');
		await this.page
			.getByLabel('Select Display Page Type')
			.selectOption('Specific');
		await this.page
			.getByRole('button', {name: 'Select Display Page'})
			.click();
		const selectDisplayPageModal = await this.page.frameLocator(
			'iframe[title*="Select Display Page"]'
		);

		await this.page
			.locator('.modal-title', {
				hasText: 'Select Display Page',
			})
			.waitFor({
				state: 'visible',
			});

		await clickAndExpectToBeHidden({
			target: this.page.locator('.modal-title', {
				hasText: 'Select Display Page',
			}),
			trigger: selectDisplayPageModal.getByLabel(
				'Select ' + displayPageName
			),
		});
	}

	async scheduleArticle(
		title: string,
		publishDate: string,
		{workflow} = {workflow: false},
		expirationDate?: string,
		reviewDate?: string
	) {
		await this.fillTitle(title);

		if (!(await this.page.getByText('Never Expire').isVisible())) {
			await this.page.getByRole('link', {name: 'Schedule'}).click();
		}

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
			trigger: this.page.getByLabel(
				workflow
					? 'Select and Confirm Submit for Workflow Settings'
					: 'Select and Confirm Publish Settings',
				{
					exact: true,
				}
			),
		});

		await this.page.getByPlaceholder('YYYY-MM-DD HH:mm').fill(publishDate);

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

		const row = await this.page
			.locator('.list-group-item')
			.filter({hasText: title});

		await row
			.locator('span.label')
			.filter({hasText: workflow ? 'Pending' : 'Scheduled'})
			.waitFor();
	}

	async submitArticleForWorkflow(title: string) {
		await this.fillTitle(title);

		await this.submitForWorkflowButton.click();

		await this.page
			.locator(
				'#_com_liferay_journal_web_portlet_JournalPortlet_articlesSearchContainer .list-group-item'
			)
			.filter({hasText: title})
			.waitFor();

		const row = await this.page
			.locator('.list-group-item')
			.filter({hasText: title});

		await row.locator('span.label').filter({hasText: 'Pending'}).waitFor();
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
			trigger: this.page.getByLabel(
				workflow
					? 'Select and Confirm Submit for Workflow Settings'
					: 'Select and Confirm Publish Settings',
				{
					exact: true,
				}
			),
		});

		if (expirationDate) {
			await expect(this.page.getByText('Expiration Date')).toHaveValue(
				expirationDate
			);
		}

		await expect(
			this.page.getByPlaceholder('YYYY-MM-DD HH:mm')
		).toHaveValue(publishDate);

		if (reviewDate) {
			await expect(this.page.getByText('Review Date')).toHaveValue(
				reviewDate
			);
		}
	}
}
