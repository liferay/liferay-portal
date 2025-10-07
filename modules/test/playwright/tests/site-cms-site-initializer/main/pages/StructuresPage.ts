/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Page, expect} from '@playwright/test';

import {PORTLET_URLS} from '../../../../utils/portletUrls';
import {DataSetPage} from './DataSetPage';

export class StructuresPage {
	readonly page: Page;
	readonly dataSetFragmentPage: DataSetPage;

	constructor(page: Page) {
		this.page = page;
		this.dataSetFragmentPage = new DataSetPage(page);
	}

	async goto() {
		await expect(async () => {
			await this.page.goto(PORTLET_URLS.cmsStructures);

			await this.page.locator('.fds').waitFor({timeout: 3000});
		}).toPass();
	}

	getItem(filter: string) {
		return this.dataSetFragmentPage.getRow(filter);
	}

	async execItemAction({action, filter}: {action: 'Delete'; filter: string}) {
		await this.dataSetFragmentPage.execItemAction({
			action,
			filter,
		});
	}
}
