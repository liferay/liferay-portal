/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Locator, Page} from '@playwright/test';

import {clickAndExpectToBeVisible} from '../../../../utils/clickAndExpectToBeVisible';
import {PORTLET_URLS} from '../../../../utils/portletUrls';

export class AssetCategoriesAdminPage {
	readonly newVocabularyButton: Locator;
	readonly page: Page;

	constructor(page: Page) {
		this.newVocabularyButton = page.getByLabel('Add New Vocabulary');
		this.page = page;
	}

	async goto(siteUrl?: Site['friendlyUrlPath']) {
		await this.page.goto(
			`/group${siteUrl || '/guest'}${PORTLET_URLS.categoriesAdmin}`
		);
	}

	async gotoAction(
		action: 'Add Subcategory' | 'Delete' | 'Edit' | 'Move' | 'Permissions',
		title: string
	) {
		await clickAndExpectToBeVisible({
			autoClick: true,
			target: this.page.getByRole('menuitem', {name: action}),
			trigger: this.page
				.getByRole('row', {name: title})
				.getByLabel('Show Actions'),
		});
	}

	async gotoVocabulary(name: string) {
		await this.page.getByRole('menuitem', {name}).click();
	}

	async deleteAllCategories() {
		await this.page.getByLabel('Select All Items on the Page').check();
		await this.page.getByRole('button', {name: 'Delete'}).click();
		await this.page
			.getByLabel('Delete Categories')
			.getByRole('button', {name: 'Delete'})
			.click();
	}
}
