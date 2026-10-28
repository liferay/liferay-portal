/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Locator, Page} from '@playwright/test';

import {clickAndExpectToBeVisible} from '../../utils/clickAndExpectToBeVisible';
import {KnowledgeBasePage} from './KnowledgeBasePage';

export class KnowledgeBaseViewArticlePage {
	readonly articleBody: Locator;
	readonly articleTitle: Locator;
	readonly infoPanel: Locator;
	readonly infoPanelButton: Locator;
	readonly page: Page;
	showActionsButton: Locator;
	deleteMenuItem: Locator;
	knowledgeBasePage: KnowledgeBasePage;

	constructor(page: Page) {
		this.articleBody = page.locator('.kb-entity-body');
		this.articleTitle = page.locator('.kb-article-title');
		this.infoPanel = page.locator('.info-panel-content');
		this.infoPanelButton = page.getByRole('link', {
			name: 'Toggle Info Panel',
		});
		this.showActionsButton = page.getByLabel('Show Actions');
		this.knowledgeBasePage = new KnowledgeBasePage(page);
		this.page = page;
	}

	async goto(siteUrl: Site['friendlyUrlPath'], articleTitle: string) {
		await this.knowledgeBasePage.goto(siteUrl);

		await this.page.getByRole('link', {name: articleTitle}).click();
		await this.page
			.locator('.kb-article-title')
			.getByText(articleTitle)
			.waitFor();
	}

	getInfoPanelValue(term: string) {
		return this.infoPanel.locator('dt', {hasText: term}).locator('+ dd');
	}

	async deleteKnowledgeBaseArticle() {
		clickAndExpectToBeVisible({
			autoClick: true,
			target: this.page.getByRole('menuitem', {name: 'Delete'}),
			trigger: this.showActionsButton,
		});
	}

	async openInfoPanel() {
		await clickAndExpectToBeVisible({
			target: this.infoPanel,
			trigger: this.infoPanelButton,
		});
	}
}
