/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Locator, Page} from '@playwright/test';

import {DataSetPage} from '../../../site-cms-site-initializer/main/pages/DataSetPage';

export class FieldMappingsPage {
	readonly channelField: (channelField: string) => Locator;
	readonly dataSetFragmentPage: DataSetPage;
	readonly page: Page;
	readonly status: (channelField: string) => Locator;

	constructor(page: Page) {
		this.dataSetFragmentPage = new DataSetPage(page);
		this.channelField = (channelField) =>
			this.dataSetFragmentPage
				.getRow(channelField)
				.getByRole('link', {name: channelField});
		this.page = page;
		this.status = (channelField) =>
			this.dataSetFragmentPage
				.getRow(channelField)
				.locator('.cell-status .label');
	}
}
