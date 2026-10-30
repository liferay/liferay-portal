/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Locator, Page, expect} from '@playwright/test';

import {clickAndExpectToBeHidden} from '../../../../utils/clickAndExpectToBeHidden';
import {openFieldset} from '../../../../utils/openFieldset';
import {waitForAlert} from '../../../../utils/waitForAlert';
import {BlogsPage} from './BlogsPage';

import type {postTaxonomyVocabularyTaxonomyCategoryProps} from '../../../../helpers/HeadlessAdminTaxonomyApiHelper';

type editBlogEntryAddfriendlyUrlType = {
	categories: Pick<postTaxonomyVocabularyTaxonomyCategoryProps, 'name'>[];
	vocabularyName: string;
};

export class BlogsEditBlogEntryPage {
	readonly page: Page;

	readonly abstractDescription: Locator;
	readonly blogsPage: BlogsPage;
	readonly contentEditor: Locator;
	readonly customAbstractRadio: Locator;
	readonly customUrlInput: Locator;
	readonly publishButton: Locator;
	readonly saveDraftStatus: Locator;
	readonly submitToWorkflowButton: Locator;
	readonly subtitleInput: Locator;
	readonly titleInput: Locator;

	constructor(page: Page) {
		this.page = page;

		this.abstractDescription = page.locator(
			'#_com_liferay_blogs_web_portlet_BlogsAdminPortlet_description'
		);
		this.blogsPage = new BlogsPage(page);
		this.contentEditor = page.locator(
			'#_com_liferay_blogs_web_portlet_BlogsAdminPortlet_contentEditor.cke_editable'
		);
		this.customAbstractRadio = page.getByLabel('Custom Abstract');
		this.customUrlInput = page.locator(
			'#_com_liferay_blogs_web_portlet_BlogsAdminPortlet_urlTitle'
		);
		this.publishButton = page.getByRole('button', {name: 'Publish'});
		this.saveDraftStatus = page.locator(
			'#_com_liferay_blogs_web_portlet_BlogsAdminPortlet_saveStatus'
		);
		this.submitToWorkflowButton = page.getByRole('button', {
			name: 'Submit for Workflow',
		});
		this.subtitleInput = page.getByPlaceholder('Subtitle');
		this.titleInput = page.getByPlaceholder('Title *');
	}

	async goto(siteUrl?: Site['friendlyUrlPath']) {
		await this.blogsPage.goto(siteUrl);
		await this.blogsPage.goToCreateBlogEntry();
	}

	async expandPanel(
		name:
			| 'Categorization'
			| 'Configuration'
			| 'Display Page'
			| 'Friendly URL'
	): Promise<Locator> {
		return openFieldset(this.page, name);
	}

	async setCustomAbstract(value: string) {
		await this.expandPanel('Configuration');

		await this.customAbstractRadio.click();

		await expect(this.abstractDescription).toBeEnabled();

		await this.abstractDescription.fill(value);
	}

	async setCustomUrl(value: string) {
		const fieldset = await this.expandPanel('Friendly URL');

		await fieldset.getByText('Use a Customized URL').click();

		await this.customUrlInput.fill(value);
	}

	async saveAsDraft() {
		await this.saveDraftStatus.getByText('Draft Saved').waitFor({
			timeout: 40_000,
		});
	}

	async fillTitle(title: string) {
		await this.titleInput.fill(title);
	}

	async fillSubtitle(subtitle: string) {
		await this.subtitleInput.fill(subtitle);
	}

	async fillContent(content: string) {
		await this.contentEditor.fill(content);
	}

	private async editBlogEntryAddCategories({
		categories,
		vocabularyName,
	}: editBlogEntryAddfriendlyUrlType) {
		const fieldset = await openFieldset(this.page, 'Categorization');

		await fieldset.getByLabel(`Select ${vocabularyName}`).click();

		const categoriesSelectorModal = this.page.getByRole('dialog', {
			name: `Select ${vocabularyName}`,
		});

		for (const {name} of categories) {
			await categoriesSelectorModal
				.locator('.treeview-item', {hasText: name})
				.getByRole('checkbox')
				.check();
		}

		await categoriesSelectorModal
			.getByRole('button', {name: 'Done'})
			.click();
	}

	private async editBlogEntryAddfriendlyUrl({
		categories,
		vocabularyName,
	}: editBlogEntryAddfriendlyUrlType) {
		await this.editBlogEntryAddCategories({
			categories,
			vocabularyName,
		});

		const fieldset = await openFieldset(this.page, 'Friendly URL');

		await fieldset.getByText('Use a Customized URL').click();

		await this.page
			.getByLabel('Available')
			.selectOption(categories.map(({name}) => ({label: name})));

		await this.page.getByLabel('Transfer Item Left to Right').click();
	}

	async editBlogEntry({
		content,
		friendlyUrl,
		publish = true,
		submitToWorkflow,
		subtitle,
		title,
	}: {
		content: string;
		friendlyUrl?: editBlogEntryAddfriendlyUrlType;
		publish?: boolean;
		submitToWorkflow?: boolean;
		subtitle?: string;
		title: string;
	}) {
		await this.fillTitle(title);

		if (subtitle !== undefined) {
			await this.fillSubtitle(subtitle);
		}

		await this.fillContent(content);

		if (friendlyUrl) {
			const {categories, vocabularyName} = friendlyUrl;

			await this.editBlogEntryAddfriendlyUrl({
				categories,
				vocabularyName,
			});
		}

		if (submitToWorkflow) {
			await this.submitBlogEntryToWorkflow();
		}
		else if (publish) {
			await this.publishBlogEntry();
		}
	}

	async openVideoItemSelector() {
		const itemSelectorIframe = this.page.locator(
			'iframe[title="Select Item"]'
		);

		await expect(async () => {
			if (!(await itemSelectorIframe.isVisible())) {
				await this.contentEditor.click();

				await this.page
					.getByTitle('Add', {exact: true})
					.click({timeout: 1000});

				await this.page
					.getByTitle('Insert Video')
					.click({timeout: 1000});
			}

			await expect(itemSelectorIframe).toBeVisible({timeout: 2000});
		}).toPass();

		const itemSelector = itemSelectorIframe.contentFrame();

		await itemSelector
			.getByRole('link', {exact: true, name: 'Documents and Media'})
			.click();

		return itemSelector;
	}

	async publishBlogEntry() {
		await this.publishButton.click();
		await waitForAlert(this.page, undefined, {timeout: 60_000});
	}

	async publishBlogEntryExpectError(partialText: string) {
		await this.publishButton.click();

		await expect(this.page.getByText(partialText).first()).toBeVisible();
	}

	async selectCoverImage(coverImageTitle: string) {
		await this.page
			.getByRole('button', {name: 'Select File'})
			.first()
			.click();

		const itemSelectorDialog = await this.page.frameLocator(
			'iframe[title="Select File"]'
		);

		await itemSelectorDialog
			.getByRole('link', {name: 'Documents and Media'})
			.click();

		await itemSelectorDialog.getByText('Sites and Libraries').waitFor();

		await itemSelectorDialog.getByText(coverImageTitle).click();
	}

	async selectSpecificDisplayPage(displayPageName: string) {
		const displayPageFieldSet = await openFieldset(
			this.page,
			'Display Page'
		);

		await displayPageFieldSet
			.getByLabel('Display Page Template')
			.selectOption('Specific');
		await displayPageFieldSet.getByRole('button', {name: 'Select'}).click();
		const selectDisplayPageModal = this.page.frameLocator(
			'iframe[title*="Select Page"]'
		);

		await selectDisplayPageModal
			.locator('.card-type-asset')
			.filter({hasText: displayPageName})
			.click({trial: true});

		await clickAndExpectToBeHidden({
			target: this.page.locator('.modal-title', {
				hasText: 'Select Page',
			}),
			trigger: selectDisplayPageModal
				.locator('.card-type-asset')
				.filter({hasText: displayPageName}),
		});
	}

	async submitBlogEntryToWorkflow() {
		await this.submitToWorkflowButton.click();
		await waitForAlert(this.page);
	}
}
