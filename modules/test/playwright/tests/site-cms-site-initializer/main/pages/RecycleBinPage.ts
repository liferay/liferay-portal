/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Locator, Page, expect} from '@playwright/test';

import {PORTLET_URLS} from '../../../../utils/portletUrls';
import {DataSetPage} from './DataSetPage';

export class RecycleBinPage {
	readonly page: Page;
	readonly dataSetFragmentPage: DataSetPage;
	readonly deleteButton: Locator;
	readonly deleteItemConfirmationText: Locator;
	readonly emptyRecycleBinButton: Locator;

	constructor(page: Page) {
		this.page = page;
		this.dataSetFragmentPage = new DataSetPage(page);
		this.deleteButton = page.getByText('Delete', {exact: true});
		this.deleteItemConfirmationText = page.getByText(
			'You are about to permanently'
		);
		this.emptyRecycleBinButton = page.getByRole('menuitem', {
			name: 'Empty Recycle Bin',
		});
	}

	async execItemAction({action, filter}: {action: string; filter: string}) {
		await this.dataSetFragmentPage.execItemAction({
			action,
			filter,
		});
	}

	async goto() {
		await expect(async () => {
			await this.page.goto(PORTLET_URLS.cmsRecycleBin);

			await this.page.locator('.fds').waitFor({timeout: 3000});
		}).toPass();
	}

	getItem(filter: string) {
		return this.dataSetFragmentPage.getRow(filter);
	}

	async navigateTo(folderName: string) {
		await this.page
			.getByRole('row', {name: folderName})
			.getByRole('link')
			.click();

		await this.page.getByPlaceholder('Search').waitFor({state: 'visible'});
	}

	async selectItems(titles: string[]) {
		for (const title of titles) {
			const card = this.page
				.locator('tr', {hasText: title})
				.or(this.page.locator('.card-row', {hasText: title}));

			await card.getByRole('checkbox').check();
		}
	}
}
