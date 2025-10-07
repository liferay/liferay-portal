/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Locator, Page, expect} from '@playwright/test';

import {clickAndExpectToBeVisible} from '../../../../utils/clickAndExpectToBeVisible';
import {getRandomInt} from '../../../../utils/getRandomInt';
import {PORTLET_URLS} from '../../../../utils/portletUrls';
import {DataSetPage} from './DataSetPage';

export class TagsPage {
	readonly page: Page;
	readonly dataSetFragmentPage: DataSetPage;
	readonly newTagButton: Locator;
	readonly saveAndAddAnotherButton: Locator;
	readonly saveButton: Locator;
	readonly spaceCheckbox: Locator;

	constructor(page: Page) {
		this.page = page;
		this.dataSetFragmentPage = new DataSetPage(page);
		this.newTagButton = this.page.getByLabel('New');
		this.saveAndAddAnotherButton = this.page.getByText(
			'Save and Add Another'
		);
		this.saveButton = this.page.getByText('Save').last();
		this.spaceCheckbox = page.getByLabel(
			'Make this tag available in all spaces, including those yet to be created.'
		);
	}

	async goto() {
		await this.page.goto(PORTLET_URLS.cmsTags);
		await this.page.getByRole('heading', {name: 'Tags'}).waitFor();
	}

	async createTag() {
		await this.goto();

		const tagName = `Tag${getRandomInt()}`;

		await this.newTagButton.click();

		await this.page.getByLabel('NameRequired').fill(tagName);

		await clickAndExpectToBeVisible({
			target: this.page.getByText(
				`Success:${tagName} was created successfully.`
			),
			trigger: this.saveButton,
		});

		return tagName;
	}

	async deleteTag(name: string) {
		await this.execItemAction({
			action: 'Delete',
			filter: name,
		});

		await expect(
			this.page.getByRole('heading', {name: `Delete Tag`})
		).toBeVisible();

		await clickAndExpectToBeVisible({
			target: this.page.getByText(
				`Success:${name} was deleted successfully.`
			),
			trigger: this.page.getByRole('button', {name: 'Delete'}),
		});

		await expect(this.getItem(name)).not.toBeVisible();
	}

	getItem(filter: string) {
		return this.dataSetFragmentPage.getRow(filter);
	}

	async execItemAction({action, filter}: {action: string; filter: string}) {
		await this.dataSetFragmentPage.execItemAction({
			action,
			filter,
		});
	}
}
