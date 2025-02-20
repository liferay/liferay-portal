/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Locator, Page} from '@playwright/test';

import {clickAndExpectToBeHidden} from '../../../utils/clickAndExpectToBeHidden';
import {openFieldset} from '../../../utils/openFieldset';
import {waitForAlert} from '../../../utils/waitForAlert';
import {BlogsPage} from './BlogsPage';

import type {postTaxonomyVocabularyTaxonomyCategoryProps} from '../../../helpers/HeadlessAdminTaxonomyApiHelper';

type editBlogEntryAddfriendlyUrlType = {
	categories: Pick<postTaxonomyVocabularyTaxonomyCategoryProps, 'name'>[];
	vocabularyName: string;
};

export class BlogsEditBlogEntryPage {
	readonly page: Page;

	readonly blogsPage: BlogsPage;
	readonly publishButton: Locator;
	readonly contentEditor: Locator;
	readonly submitToWorkflowButton: Locator;

	constructor(page: Page) {
		this.page = page;

		this.blogsPage = new BlogsPage(page);
		this.contentEditor = page.locator(
			'#_com_liferay_blogs_web_portlet_BlogsAdminPortlet_contentEditor.cke_editable'
		);
		this.publishButton = page.getByRole('button', {name: 'Publish'});
		this.submitToWorkflowButton = page.getByRole('button', {
			name: 'Submit for Workflow',
		});
	}

	async goto(siteUrl?: Site['friendlyUrlPath']) {
		await this.blogsPage.goto(siteUrl);
		await this.blogsPage.goToCreateBlogEntry();
	}

	private async editBlogEntryAddCategories({
		categories,
		vocabularyName,
	}: editBlogEntryAddfriendlyUrlType) {
		const fieldset = await openFieldset(this.page, 'Categorization');

		await fieldset.getByLabel(`Select ${vocabularyName}`).click();

		const categoriesSelectorIframe = await this.page.frameLocator(
			`iframe[title="Select ${vocabularyName}"]`
		);

		for (const {name} of categories) {
			await categoriesSelectorIframe
				.locator('.treeview-item', {hasText: name})
				.getByRole('checkbox')
				.check();
		}

		await this.page.getByRole('button', {name: 'Done'}).click();
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
		title,
	}: {
		content: string;
		friendlyUrl?: editBlogEntryAddfriendlyUrlType;
		publish?: boolean;
		submitToWorkflow?: boolean;
		title: string;
	}) {
		await this.page.getByPlaceholder('Title *').fill(title);

		await this.contentEditor.fill(content);

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

	async publishBlogEntry() {
		await this.publishButton.click();
		await waitForAlert(this.page);
	}

	async selectCoverImage(coverImageTitle) {
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
