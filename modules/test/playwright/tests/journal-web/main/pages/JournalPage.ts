/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {FrameLocator, Locator, Page, expect} from '@playwright/test';

import {changeManagementToolbarView} from '../../../../utils/changeManagementToolbarView';
import {clickAndExpectToBeVisible} from '../../../../utils/clickAndExpectToBeVisible';
import {expandSection} from '../../../../utils/expandSection';
import {PORTLET_URLS} from '../../../../utils/portletUrls';
import {waitForAlert} from '../../../../utils/waitForAlert';

export enum FilterBy {
	ALL = 'All',
	MINE = 'Mine',
	RECENT = 'Recent',
}

export class JournalPage {
	readonly page: Page;

	readonly createBasicWebContentLink: Locator;
	readonly newButton: Locator;
	readonly permissionsFrameLocator: FrameLocator;
	readonly publishButton: Locator;
	readonly tagFrameLocator: FrameLocator;
	readonly templatesLink: Locator;
	readonly articleTitleInput: Locator;
	readonly articleContentTextBox: Locator;

	constructor(page: Page) {
		this.page = page;

		this.createBasicWebContentLink = this.page.getByRole('menuitem', {
			name: 'Basic Web Content',
		});
		this.newButton = page.locator(
			'button[data-qa-id="creationMenuNewButton"].d-md-flex.d-none'
		);
		this.permissionsFrameLocator = page.frameLocator(
			'iframe[title="Permissions"]'
		);
		this.tagFrameLocator = page.frameLocator('iframe[title="Tags"]');
		this.templatesLink = page.getByRole('link', {name: 'Templates'});
		this.publishButton = page.getByRole('button', {name: 'Publish'});
		this.articleTitleInput = page.locator(
			'.article-content-title .input-group-item input'
		);
		this.articleContentTextBox = this.page
			.getByTestId('content')
			.getByRole('textbox', {name: 'Rich Text Editor'});
	}

	async goto(siteUrl?: Site['friendlyUrlPath']) {
		await this.page.goto(
			`/group${siteUrl || '/guest'}${PORTLET_URLS.journal}`
		);
	}

	async fillArticleContent(content: string) {
		await this.articleContentTextBox.click();

		await this.page.keyboard.press('Control+KeyA');
		await this.page.keyboard.press('Backspace');
		await this.page.keyboard.type(content);
	}

	async fillArticleData(title: string, content: string) {
		await this.articleTitleInput.fill(title);

		await this.fillArticleContent(content);
	}

	async fillArticleDataSiteTemplate(title: string, content: string) {
		await this.articleTitleInput.focus();
		await this.articleTitleInput.click();
		await this.page.keyboard.type(title);

		await this.fillArticleContent(content);
	}

	async goToCreateArticle(structureName?: string) {
		const target = structureName
			? this.page.getByRole('menuitem', {
					name: structureName,
				})
			: this.createBasicWebContentLink;

		await clickAndExpectToBeVisible({
			autoClick: true,
			target,
			trigger: this.newButton,
		});

		await this.page.locator('.article-content-content').waitFor();
	}

	async goToCreateFolder() {
		await clickAndExpectToBeVisible({
			autoClick: true,
			target: this.page.getByRole('menuitem', {name: 'Folder'}),
			trigger: this.newButton,
		});
	}

	async goToJournalArticleAction(action: string, title: string) {
		await this.page.getByLabel(`Actions for ${title}`).waitFor();

		await clickAndExpectToBeVisible({
			autoClick: true,
			target: this.page.getByRole('menuitem', {
				exact: true,
				name: action,
			}),
			trigger: this.page.getByLabel(`Actions for ${title}`, {
				exact: true,
			}),
		});
	}

	async goToJournalFolderAction(action: string, title: string) {
		await changeManagementToolbarView(this.page, 'cards');

		const folder = this.page.locator(
			`[data-qa-id="row"][data-title="${title}"]`
		);

		await folder.waitFor();

		await clickAndExpectToBeVisible({
			autoClick: true,
			target: this.page.getByRole('menuitem', {
				exact: true,
				name: action,
			}),
			trigger: folder.getByLabel('More actions', {
				exact: true,
			}),
		});
	}

	async assertJournalArticlePermissions(
		title: string,
		permissions: {enabled: boolean; locator: string}[]
	) {
		await this.goToJournalArticleAction('Permissions', title);

		await this.assertPermissions(permissions);
	}

	async assertJournalArticlePosition(position: number, title: string) {
		await expect(
			this.page
				.locator(
					`[id="_com_liferay_journal_web_portlet_JournalPortlet_articles_${position}"]`
				)
				.getByRole('link')
				.nth(0)
		).toHaveText(title);
	}

	async assertTitle(title: string) {
		await expect(this.page.locator(`a[title='${title}']`)).toBeVisible();
	}

	async assertPermissions(
		permissions: {enabled: boolean; locator: string}[]
	) {
		await this.permissionsFrameLocator
			.locator(permissions[0].locator)
			.waitFor();

		for (const permission of permissions) {
			const permissionCheckbox = this.permissionsFrameLocator.locator(
				permission.locator
			);

			if (permission.enabled) {
				await expect(permissionCheckbox).toBeChecked();
			}
			else {
				await expect(permissionCheckbox).not.toBeChecked();
			}
		}

		await this.permissionsFrameLocator
			.getByRole('button', {name: 'Cancel'})
			.click();
	}

	async assertPrivateContentIcon() {
		await expect(
			this.page.getByLabel('Not Visible to Guest Users').locator('use')
		).toBeVisible({timeout: 1000});
	}

	async changeView(viewName: string) {
		await changeManagementToolbarView(this.page, viewName);
	}

	async clearFilters() {
		await this.page.getByRole('button', {name: 'Clear'}).click();
	}

	async deleteSelection() {
		await this.page.getByRole('button', {name: 'Delete'}).click();
	}

	async moveToFolder(folderName: String) {
		await this.page.getByRole('button', {name: 'Move'}).click();

		await this.page.getByRole('button', {name: 'Select'}).click();

		await this.page
			.frameLocator('iframe[title="Select Folder"]')
			.getByRole('button')
			.click();

		await this.page
			.frameLocator('iframe[title="Select Folder"]')
			.getByText(`${folderName}`)
			.click();

		await this.page.getByRole('button', {name: 'Move'}).click();

		await expect(
			this.page.getByText('Success:Your request completed successfully.')
		).toBeVisible();
	}

	async publishArticle() {
		await this.publishButton.click();

		await waitForAlert(this.page, `was created successfully.`);
	}

	async selectItem(index: number) {
		await this.page
			.locator(
				`[id="_com_liferay_journal_web_portlet_JournalPortlet_articles_${index + 1}"]`
			)
			.locator('input[type=checkbox]')
			.click();
	}

	async selectPage(index: number) {
		await this.page
			.getByLabel('Pagination')
			.getByRole('link')
			.nth(index + 1)
			.click();

		const pageLabel = `Page ${index + 1}`;

		const pageLabelLink = this.page.getByLabel(pageLabel);

		await expect(pageLabelLink).toHaveAttribute('aria-current', 'page');
	}

	async setJournalArticlePermissions(
		articles: Locator[],
		permissionLocators: string[]
	) {
		for (const article of articles) {
			await article.getByTitle('Select').check();
		}

		await clickAndExpectToBeVisible({
			autoClick: true,
			target: this.page.getByRole('menuitem', {
				name: 'Permissions',
			}),
			trigger: this.page.getByTitle('Actions', {exact: true}),
		});

		await this.setPermissions(permissionLocators);
	}

	async setPermissions(permissionLocators: string[]) {
		await this.permissionsFrameLocator
			.locator(permissionLocators[0])
			.check({trial: true});

		for (const permissionsLocator of permissionLocators) {
			await this.permissionsFrameLocator
				.locator(permissionsLocator)
				.check({timeout: 2000});
		}

		await this.permissionsFrameLocator
			.getByRole('button', {name: 'Save'})
			.click();

		await this.permissionsFrameLocator
			.getByRole('button', {name: 'Cancel'})
			.click();
	}

	async setArticleViewableBy(value: 'Anyone' | 'Site Members' | 'Owner') {
		const permissionsGroup = this.page.getByRole('button', {
			name: 'Permissions',
		});

		await permissionsGroup.waitFor();

		await expandSection(permissionsGroup);

		await this.page.getByLabel('Viewable by').waitFor();

		await this.page.getByLabel('Viewable by').selectOption(value);
	}

	async setFilterBy(filterBy: FilterBy) {
		await this.page.getByLabel('Filter', {exact: true}).click();
		await this.page.getByRole('menuitem', {name: filterBy}).click();
	}

	async setOrderBy(orderBy: string) {
		await this.page.getByLabel('Order').click();
		await this.page.getByRole('menuitem', {name: `${orderBy}`}).click();
	}

	async selectTag(tagName: string) {
		await this.page.getByRole('button', {name: 'Select Tags'}).click();

		const tagCheckbox = this.tagFrameLocator
			.locator(`tr:has-text('${tagName}')`)
			.getByRole('checkbox');

		if (await tagCheckbox.isHidden()) {
			const tagSearchBar = this.tagFrameLocator
				.getByPlaceholder('Search for')
				.first();

			await tagSearchBar.fill(tagName);
			await tagSearchBar.press('Enter');

			await expect(tagCheckbox).toBeVisible();
		}

		await tagCheckbox.check();

		await this.page
			.locator('.modal-footer')
			.getByRole('button', {name: 'Done'})
			.click();

		await expect(tagCheckbox).toBeHidden();
	}
}
