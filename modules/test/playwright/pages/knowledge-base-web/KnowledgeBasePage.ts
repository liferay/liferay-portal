/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Locator, Page, expect} from '@playwright/test';

import {clickAndExpectToBeVisible} from '../../utils/clickAndExpectToBeVisible';
import {PORTLET_URLS} from '../../utils/portletUrls';

export class KnowledgeBasePage {
	readonly basicArticleMenuItem: Locator;
	readonly emptyStateTitle: Locator;
	readonly foldersAndArticlesButton: Locator;
	readonly foldersAndArticlesPanel: Locator;
	readonly foldersAndArticlesTab: Locator;
	readonly newButton: Locator;
	readonly page: Page;
	readonly recycleBinAlert: Locator;
	readonly selectAllCheckBox: Locator;

	constructor(page: Page) {
		this.basicArticleMenuItem = page.getByRole('menuitem', {
			name: 'Basic Article',
		});
		this.emptyStateTitle = page.getByText('Knowledge base is empty.', {
			exact: true,
		});
		this.foldersAndArticlesButton = page.getByLabel('Folders and Articles');
		this.foldersAndArticlesPanel = page.getByRole('tabpanel', {
			name: 'Folders and Articles',
		});
		this.foldersAndArticlesTab = page.getByRole('tab', {
			name: 'Folders and Articles',
		});
		this.newButton = page
			.locator('.management-bar')
			.getByText('New', {exact: true});
		this.page = page;
		this.recycleBinAlert = page.locator(
			'[id="_com_liferay_knowledge_base_web_portlet_AdminPortlet_recycleBinAlert"]'
		);
		this.selectAllCheckBox = page.getByLabel(
			'Select All Items on the Page'
		);
	}

	getRow(title: string) {
		return this.page.locator('[data-qa-id=row]').filter({
			has: this.page.getByRole('heading', {exact: true, name: title}),
		});
	}

	getTreeItem(name: string) {
		return this.foldersAndArticlesPanel.getByRole('treeitem', {
			exact: true,
			name,
		});
	}

	async clickRowAction(title: string, menuItem: string, index = 0) {
		await clickAndExpectToBeVisible({
			autoClick: true,
			target: this.page.getByRole('menuitem', {
				exact: true,
				name: menuItem,
			}),
			trigger: this.getRow(title).nth(index).getByLabel('Show Actions'),
		});
	}

	async clickTreeItemAction(name: string, menuItem: string) {
		const treeItem = this.getTreeItem(name);

		await treeItem.hover();

		await clickAndExpectToBeVisible({
			autoClick: true,
			target: this.page.getByRole('menuitem', {
				exact: true,
				name: menuItem,
			}),
			trigger: treeItem.getByRole('button', {name: 'Actions'}).first(),
		});
	}

	async selectMoveTargetAndSave(title: string, targetName: string) {
		const moveDialog = this.page.getByRole('dialog', {
			name: `Move "${title}" To`,
		});

		const moveFrame = moveDialog.frameLocator('iframe');

		const targetTreeItem = moveFrame.getByRole('treeitem', {
			name: targetName,
		});

		await expect(async () => {
			await targetTreeItem.click({timeout: 1000});

			await expect(targetTreeItem).toHaveClass(/(^|\s)active(\s|$)/, {
				timeout: 1000,
			});
		}).toPass();

		await moveDialog.getByRole('button', {name: 'Save'}).click();
	}

	async openFoldersAndArticlesPanel() {
		await expect(async () => {
			if (!(await this.foldersAndArticlesPanel.isVisible())) {
				await this.foldersAndArticlesTab.click({timeout: 1000});
			}

			await expect(this.foldersAndArticlesPanel).toBeVisible({
				timeout: 1000,
			});
		}).toPass();
	}

	async goto(siteUrl?: Site['friendlyUrlPath']) {
		await this.page.goto(
			`/group${siteUrl || '/guest'}${PORTLET_URLS.knowledgeBase}`
		);
	}

	async goToCreateNewArticle() {
		await this.goToFoldersAndArticles();
		await clickAndExpectToBeVisible({
			autoClick: true,
			target: this.basicArticleMenuItem,
			trigger: this.newButton,
		});
	}

	private async goToFoldersAndArticles() {
		await this.foldersAndArticlesButton.click();
	}

	async deleteKnowledgeBaseArticle(title: string) {
		const kbArticle = await this.page
			.locator(
				'#_com_liferay_knowledge_base_web_portlet_AdminPortlet_kbObjectsSearchContainer .list-group-item'
			)
			.filter({hasText: title});

		this.page.once('dialog', (dialog) => {
			dialog.accept().catch(() => {});
		});
		await clickAndExpectToBeVisible({
			autoClick: true,
			target: this.page.getByRole('menuitem', {name: 'Delete'}),
			trigger: kbArticle.getByLabel('Show Actions'),
		});
	}

	async deleteAll(recycleBin: boolean) {
		if (await this.selectAllCheckBox.isDisabled()) {
			return;
		}

		if (!recycleBin) {
			this.page.once('dialog', (dialog) => {
				dialog.accept().catch(() => {});
			});
		}

		await clickAndExpectToBeVisible({
			autoClick: true,
			target: this.page.getByRole('button', {
				name: 'Delete',
			}),
			trigger: this.selectAllCheckBox,
		});
	}
}
