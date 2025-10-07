/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Locator, Page} from '@playwright/test';

export class AccountAccountGroupsPage {
	readonly accountGroupName: (accountGroupName: string) => Promise<Locator>;
	readonly page: Page;
	readonly searchButton: Locator;
	readonly searchInput: Locator;
	readonly statusColumn: Locator;

	constructor(page: Page) {
		this.accountGroupName = async (accountGroupName: string) => {
			return this.page.getByText(accountGroupName, {exact: true});
		};
		this.page = page;
		this.searchButton = this.page.getByLabel('Search for', {exact: true});
		this.searchInput = this.page.getByPlaceholder('Search for');
		this.statusColumn = this.page.getByText('Status');
	}
}
