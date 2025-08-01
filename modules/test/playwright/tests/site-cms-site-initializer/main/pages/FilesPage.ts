/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Locator, Page} from '@playwright/test';

import {clickAndExpectToBeVisible} from '../../../../utils/clickAndExpectToBeVisible';
import {PORTLET_URLS} from '../../../../utils/portletUrls';
import {DataSetPage} from './DataSetPage';

export class FilesPage {
	readonly page: Page;

	readonly dataSetFragmentPage: DataSetPage;
	readonly newButton: Locator;

	constructor(page: Page) {
		this.page = page;

		this.dataSetFragmentPage = new DataSetPage(page);
		this.newButton = page.getByLabel('New');
	}

	async goto() {
		await this.page.goto(PORTLET_URLS.cmsFiles);
		await this.page.getByRole('heading', {name: 'Files'}).waitFor();
	}

	async createContent(type: string) {
		await clickAndExpectToBeVisible({
			autoClick: true,
			target: this.page.getByRole('menuitem', {name: type}),
			trigger: this.newButton,
		});
	}

	getItem(filter: string) {
		return this.dataSetFragmentPage.getRow(filter);
	}

	async execItemAction({
		action,
		filter,
	}: {
		action: 'Download' | 'View';
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
