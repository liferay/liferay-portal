/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Locator, Page} from '@playwright/test';

export class PerformanceByAssigneePage {
	readonly page: Page;
	readonly searchBar: Locator;

	constructor(page: Page) {
		this.page = page;
		this.searchBar = page.getByPlaceholder('Search for Assignee Name');
	}
}
