/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Locator, Page} from '@playwright/test';

import {clickAndExpectToBeVisible} from '../../../../utils/clickAndExpectToBeVisible';

type VocabularyField = 'Funnel Stages' | 'Personas';

const PLACEHOLDERS: Record<VocabularyField, string> = {
	'Funnel Stages': 'Add Stages',
	'Personas': 'Add Personas',
};

export class EditProjectPage {
	readonly page: Page;
	readonly saveButton: Locator;
	readonly tagInput: Locator;
	readonly titleInput: Locator;

	constructor(page: Page) {
		this.page = page;
		this.saveButton = page.getByRole('button', {
			name: 'Save',
		});
		this.tagInput = page.getByPlaceholder('Add Tag');
		this.titleInput = page.getByPlaceholder('Untitled Project');
	}

	getCreateTagAction(tagName: string): Locator {
		return this.page.getByText(`Create New Tag: ${tagName}`);
	}

	getSelectedTag(tagName: string): Locator {
		return this.page
			.locator('.asset-tags')
			.getByText(tagName, {exact: true});
	}

	getTagOption(tagName: string): Locator {
		return this.page.getByRole('option', {exact: true, name: tagName});
	}

	getCategoryChip(field: VocabularyField, categoryName: string): Locator {
		return this.getVocabularyMultiSelect(field)
			.locator('.lfr-cmp__vocabulary-multi-select-selected')
			.getByText(categoryName, {exact: true});
	}

	getVocabularyMultiSelect(field: VocabularyField): Locator {
		return this.page
			.locator('.lfr-cmp__vocabulary-multi-select')
			.filter({has: this.page.getByText(field, {exact: true})});
	}

	async searchTags(query: string) {
		await this.tagInput.click();
		await this.tagInput.fill(query);
	}

	async removeCategory(field: VocabularyField, categoryName: string) {
		await this.getVocabularyMultiSelect(field)
			.locator('.label', {hasText: categoryName})
			.getByRole('button', {name: 'Remove'})
			.click();
	}

	async selectCategory(field: VocabularyField, categoryName: string) {
		const option = this.page.getByRole('option', {
			exact: true,
			name: categoryName,
		});

		await clickAndExpectToBeVisible({
			target: option,
			trigger: this.getVocabularyMultiSelect(field).getByPlaceholder(
				PLACEHOLDERS[field]
			),
		});

		await option.click();
	}
}
