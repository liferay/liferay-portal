/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Locator, Page, expect} from '@playwright/test';

import {clickAndExpectToBeVisible} from '../../utils/clickAndExpectToBeVisible';
import fillAndClickOutside from '../../utils/fillAndClickOutside';
import {PORTLET_URLS} from '../../utils/portletUrls';
import {waitForAlert} from '../../utils/waitForAlert';

export type TTranslation = {
	key: string;
	values: {
		languageId: string;
		value: string;
	}[];
};

export class LanguageOverridePage {
	readonly filterButton: Locator;
	readonly newButton: Locator;
	readonly optionsButton: Locator;
	readonly page: Page;
	readonly saveButton: Locator;

	constructor(page: Page) {
		this.filterButton = page.getByRole('button', {
			exact: true,
			name: 'Filter',
		});
		this.newButton = page.getByRole('link', {name: 'Add Language Key'});
		this.optionsButton = page.getByRole('button', {name: 'Options'});
		this.page = page;
		this.saveButton = page.getByRole('button', {name: 'Save'});
	}

	async addTranslation({key, values}: TTranslation) {
		await this.newButton.click();

		await this.page.getByLabel('key required').fill(key);

		for (let i = 0; i < values.length; i++) {
			const {languageId, value} = values[i];

			await fillAndClickOutside(
				this.page,
				this.page.getByLabel(languageId),
				value,
				false
			);
		}

		await this.saveButton.click();

		await waitForAlert(this.page);
	}

	async addTranslations(languageOverrides: TTranslation[]) {
		for (const languageOverride of languageOverrides) {
			await this.addTranslation(languageOverride);
		}
	}

	async assertTranslationInListView({key, values}: TTranslation) {
		if (values.length) {
			const normalizedLanguageIds = values.map(({languageId}) =>
				languageId.replace('-', '_')
			);

			await expect(
				this.page.locator(
					`a:has-text("${key}"):has-text("Languages With Override: ${normalizedLanguageIds.join(', ')}")`
				)
			).toBeVisible();
		}
		else {
			await expect(
				this.page.getByRole('link', {name: key})
			).toBeAttached();
		}
	}

	async assertTranslationNotInListView({key}: TTranslation) {
		await expect(this.page.getByRole('link', {name: key})).toBeHidden();
	}

	async changeFilter(option: 'Any Language' | 'Selected Language') {
		await clickAndExpectToBeVisible({
			autoClick: true,
			target: this.page.getByRole('menuitem', {name: option}),
			trigger: this.filterButton,
		});

		await this.page
			.getByText('Search Results', {exact: true})
			.waitFor({state: 'visible'});
	}

	async changeLocale(currentLanguageId: string, languageId: string) {
		await clickAndExpectToBeVisible({
			autoClick: true,
			target: this.page.getByRole('menuitem', {name: languageId}),
			trigger: this.page.getByRole('button', {name: currentLanguageId}),
		});

		await this.page.waitForLoadState();
	}

	async exportOverridenTranslations() {
		await this.optionsButton.click();

		await this.page
			.getByRole('menuitem', {name: 'Export Overridden Translations'})
			.click();
	}

	async goto() {
		await this.page.goto(`/group/guest${PORTLET_URLS.languageOverride}`);
	}

	async searchTranslation(key: string) {
		await fillAndClickOutside(
			this.page,
			this.page.getByRole('searchbox'),
			key,
			false
		);

		await this.page
			.getByRole('button', {exact: true, name: 'Search for'})
			.click();

		await this.page.waitForLoadState();

		await this.page.getByText('Search Results').waitFor({state: 'visible'});
	}
}
