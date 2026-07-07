/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {FrameLocator, Locator, Page} from '@playwright/test';

import {MessageBoardsPage} from './MessageBoardsPage';

export class MessageBoardsEditThreadPage {
	readonly allFilesReadyToBeSavedMessage: Locator;
	readonly attachmentCollapse: Locator;
	readonly bodyFrameLocator: FrameLocator;
	readonly bodyImageButton: Locator;
	readonly bodyLinkButton: Locator;
	readonly bodyTextBox: Locator;
	readonly browseServerButton: Locator;
	readonly fileSelector: Locator;
	readonly itemSelectorFrame: FrameLocator;
	readonly linkDialogOKButton: Locator;
	readonly linkDisplayTextSelector: Locator;
	readonly messageBoardsPage: MessageBoardsPage;
	readonly page: Page;
	readonly publishButton: Locator;
	readonly subjectSelector: Locator;
	readonly submitForWorkflowButton: Locator;

	constructor(page: Page) {
		this.allFilesReadyToBeSavedMessage = page.getByText(
			'All files ready to be saved.'
		);
		this.attachmentCollapse = page.getByRole('button', {
			name: 'Attachments',
		});
		this.bodyFrameLocator = page.frameLocator('iframe');
		this.bodyImageButton = page.getByRole('button', {
			exact: true,
			name: 'Image',
		});
		this.bodyLinkButton = page.getByRole('button', {
			exact: true,
			name: 'Link',
		});
		this.bodyTextBox = this.bodyFrameLocator.getByRole('textbox');
		this.browseServerButton = page.getByRole('button', {
			name: 'Browse Server',
		});
		this.fileSelector = page.getByRole('button', {name: 'Select File'});
		this.itemSelectorFrame = page.frameLocator(
			'iframe[title="Select Item"]'
		);
		this.linkDialogOKButton = page.getByRole('button', {
			exact: true,
			name: 'OK',
		});
		this.linkDisplayTextSelector = page.getByRole('textbox', {
			name: 'Display Text',
		});
		this.messageBoardsPage = new MessageBoardsPage(page);
		this.page = page;
		this.publishButton = page.getByRole('button', {
			exact: true,
			name: 'Publish',
		});
		this.subjectSelector = page.getByLabel('Subject');
		this.submitForWorkflowButton = page.getByRole('button', {
			exact: true,
			name: 'Submit for Workflow',
		});
	}

	async insertBodyImage(documentTitle: string) {
		await this.bodyImageButton.click();

		await this._selectItemSelectorDocument(documentTitle);
	}

	async insertBodyLinkToDocument(displayText: string, documentTitle: string) {
		await this.bodyLinkButton.click();

		await this.linkDisplayTextSelector.fill(displayText);

		await this.browseServerButton.click();

		await this._selectItemSelectorDocument(documentTitle);

		await this.linkDialogOKButton.click();
	}

	async insertBodyLinkToPage(displayText: string, pageName: string) {
		await this.bodyLinkButton.click();

		await this.linkDisplayTextSelector.fill(displayText);

		await this.browseServerButton.click();

		await this.itemSelectorFrame.getByRole('link', {name: 'Pages'}).click();

		await this.itemSelectorFrame
			.getByRole('treeitem', {name: pageName})
			.click();

		await this.linkDialogOKButton.click();
	}

	async goto(siteUrl?: Site['friendlyUrlPath']) {
		await this.messageBoardsPage.goto(siteUrl);

		await this.messageBoardsPage.goToCreateNewThread();
	}

	async gotoAndPublishNewBasicThread(
		subject: string,
		body: string,
		siteUrl?: Site['friendlyUrlPath'],
		filePath?: string
	) {
		await this.goto(siteUrl);
		await this.publishNewBasicThread(subject, body, filePath);
	}

	async publishNewBasicThread(
		subject: string,
		body: string,
		filePath?: string
	) {
		await this.subjectSelector.fill(subject);
		await this.bodyTextBox.fill(body);

		if (filePath) {
			await this.selectFile(filePath);
		}

		await this.publishButton.click();
	}

	async publishReply(body: string) {
		await this.page.getByRole('button', {name: 'Reply'}).click();
		await this.bodyTextBox.fill(body);
		await this.publishButton.click();
	}

	async publishNewThreadForWorkflow(subject: string, body: string) {
		await this.messageBoardsPage.goToCreateNewThread();
		await this.subjectSelector.fill(subject);
		await this.bodyTextBox.fill(body);
		await this.submitForWorkflowButton.click();
	}

	async selectFile(filePath: string) {
		const isExpanded = await this.attachmentCollapse.evaluate(
			(element) => element.getAttribute('aria-expanded') === 'true'
		);
		if (!isExpanded) {
			await this.attachmentCollapse.click();
		}
		const fileChooserPromise = this.page.waitForEvent('filechooser');
		await this.fileSelector.click();
		const fileChooser = await fileChooserPromise;
		await fileChooser.setFiles(filePath);
		await this.allFilesReadyToBeSavedMessage.waitFor();
	}

	async _selectItemSelectorDocument(documentTitle: string) {
		const searchBox = this.itemSelectorFrame.getByRole('searchbox', {
			name: 'Search for:',
		});

		await searchBox.fill(documentTitle);
		await searchBox.press('Enter');

		await this.itemSelectorFrame
			.getByText(documentTitle, {exact: true})
			.click();
	}
}
