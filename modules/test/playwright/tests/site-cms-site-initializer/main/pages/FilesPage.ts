/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Page} from '@playwright/test';

import {PORTLET_URLS} from '../../../../utils/portletUrls';
import {DataSetPage} from './DataSetPage';

export class FilesPage {
	readonly page: Page;
	readonly dataSetFragmentPage: DataSetPage;

	constructor(page: Page) {
		this.page = page;
		this.dataSetFragmentPage = new DataSetPage(page);
	}

	async goto() {
		await this.page.goto(PORTLET_URLS.cmsFiles);
		await this.page.getByRole('heading', {name: 'Files'}).waitFor();
	}

	getItem(filter: string) {
		return this.dataSetFragmentPage.getRow(filter);
	}

	async execItemAction({
		action,
		filter,
	}: {
		action: 'Download';
		filter: string;
	}) {
		await this.dataSetFragmentPage.execItemAction({
			action,
			filter,
		});
	}

	async changeVisualizationMode(
		...args: Parameters<DataSetPage['changeVisualizationMode']>
	) {
		await this.dataSetFragmentPage.changeVisualizationMode(...args);
	}
}
