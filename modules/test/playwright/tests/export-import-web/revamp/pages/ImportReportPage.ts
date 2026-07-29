/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Page} from '@playwright/test';

export class ImportReportPage {
	readonly page: Page;

	constructor(page: Page) {
		this.page = page;
	}

	async goToEntryDetails(externalReferenceCode: string) {
		await this.page
			.getByRole('row', {name: externalReferenceCode})
			.getByLabel('view')
			.click();
	}
}
