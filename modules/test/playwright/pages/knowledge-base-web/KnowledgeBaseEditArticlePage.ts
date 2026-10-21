/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {FrameLocator, Locator, Page} from '@playwright/test';

import {clickAndExpectToBeVisible} from '../../utils/clickAndExpectToBeVisible';
import {expandSection} from '../../utils/expandSection';
import {waitForAlert} from '../../utils/waitForAlert';
import {KnowledgeBasePage} from './KnowledgeBasePage';

export class KnowledgeBaseEditArticlePage {
	readonly cancelButton: Locator;
	readonly contentFrameLocator: FrameLocator;
	readonly contentTextBox: Locator;
	readonly page: Page;
	readonly publishButton: Locator;
	readonly publishMenuItem: Locator;
	readonly saveAsDraftButton: Locator;
	readonly scheduleButton: Locator;
	readonly scheduleDatePlaceholder: Locator;
	readonly scheduleMenuItem: Locator;
	readonly titlePlaceholder: Locator;

	knowledgeBasePage: KnowledgeBasePage;

	constructor(page: Page) {
		this.cancelButton = page.getByRole('link', {name: 'Cancel'});
		this.contentFrameLocator = page.frameLocator(
			'iframe.cke_wysiwyg_frame'
		);
		this.contentTextBox = this.contentFrameLocator.getByRole('textbox');
		this.knowledgeBasePage = new KnowledgeBasePage(page);
		this.publishButton = page.getByRole('button', {name: 'Publish'});
		this.publishMenuItem = page.getByRole('menuitem', {
			exact: true,
			name: 'Publish',
		});
		this.saveAsDraftButton = page.getByRole('button', {
			name: 'Save as Draft',
		});
		this.scheduleButton = page.getByRole('button', {name: 'Schedule'});
		this.scheduleDatePlaceholder =
			page.getByPlaceholder('yyyy-MM-dd HH:mm');
		this.scheduleMenuItem = page.getByRole('menuitem', {
			name: 'Schedule Publication',
		});
		this.titlePlaceholder = page.getByPlaceholder('Untitled Article');
		this.page = page;
	}

	async goto(siteUrl: Site['friendlyUrlPath']) {
		await this.knowledgeBasePage.goto(siteUrl);

		await this.knowledgeBasePage.goToCreateNewArticle();
	}

	async editKBArticle(title: string, content: string) {
		await this.titlePlaceholder.fill(title);
		await this.contentTextBox.fill(content);

		await clickAndExpectToBeVisible({
			autoClick: true,
			target: this.publishMenuItem,
			trigger: this.publishButton,
		});

		await waitForAlert(
			this.page,
			`Success:${title} was successfully published.`
		);
	}

	async expandPanel(name: string) {
		await expandSection(
			this.page
				.getByRole('group', {exact: true, name})
				.getByRole('button', {exact: true, name})
		);
	}

	async publish() {
		await clickAndExpectToBeVisible({
			autoClick: true,
			target: this.publishMenuItem,
			trigger: this.publishButton,
		});
	}

	async cancel() {
		await this.cancelButton.click();
	}

	async publishNewKnowledgeBaseArticle(content: string, title: string) {
		await this.titlePlaceholder.fill(title);
		await this.contentTextBox.fill(content);
		await clickAndExpectToBeVisible({
			autoClick: true,
			target: this.publishMenuItem,
			trigger: this.publishButton,
		});
	}

	async scheduleNewKnowledgeBaseArticle(
		content: string,
		scheduleDate: string,
		title: string
	) {
		await this.titlePlaceholder.fill(title);
		await this.contentTextBox.fill(content);
		await clickAndExpectToBeVisible({
			autoClick: true,
			target: this.scheduleMenuItem,
			trigger: this.publishButton,
		});
		await this.scheduleDatePlaceholder.click();
		await this.scheduleDatePlaceholder.fill(scheduleDate);
		await this.scheduleButton.click();
	}
}
