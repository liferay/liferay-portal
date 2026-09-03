/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {FrameLocator, Page} from '@playwright/test';

export class PageSelectorPage {
	readonly page: Page;

	readonly modal: FrameLocator;

	constructor(page: Page) {
		this.page = page;

		this.modal = page.frameLocator('iframe[title*="Select Page"]');
	}

	async getModal(): Promise<FrameLocator> {
		return this.modal;
	}

	async loadMore() {
		const loadMoreButton = this.modal.locator('.load-more-btn');

		await loadMoreButton.click();

		await loadMoreButton.locator('.loading-animation').waitFor();
		await loadMoreButton
			.locator('.loading-animation')
			.waitFor({state: 'hidden'});
	}

	async search(query: string) {
		await this.modal.getByPlaceholder('Search').fill(query);

		await this.modal.locator('.loading-animation').waitFor();
		await this.modal
			.locator('.loading-animation')
			.waitFor({state: 'hidden'});
	}

	async selectSearchResult(pageName: string) {
		const searchResult = this.modal.locator('.search-result').filter({
			has: this.modal.getByText(pageName, {exact: true}),
		});

		await searchResult.getByRole('checkbox').check();
	}
}
