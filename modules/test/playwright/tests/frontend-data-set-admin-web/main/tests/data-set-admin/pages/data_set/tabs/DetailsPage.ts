/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Locator, Page} from '@playwright/test';

import {DataSetPage} from '../DataSetPage';

export class DetailsPage {
	readonly cancelButton: Locator;
	readonly dataSetPage: DataSetPage;
	readonly page: Page;
	readonly parametersInput: Locator;
	readonly saveButton: Locator;
	readonly urlPreviewInput: Locator;

	constructor(page: Page) {
		this.cancelButton = page.getByRole('button', {name: 'Cancel'});
		this.dataSetPage = new DataSetPage(page);
		this.page = page;
		this.parametersInput = page.getByLabel('Parameters', {exact: true});
		this.saveButton = page.getByRole('button', {name: 'Save'});
		this.urlPreviewInput = page.getByLabel('URL Preview');
	}

	async goto({dataSetLabel}: {dataSetLabel: string}) {
		await this.dataSetPage.goto({
			dataSetLabel,
		});

		await this.dataSetPage.selectTab('Details');
	}
}
