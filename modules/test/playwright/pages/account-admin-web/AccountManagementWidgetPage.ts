/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Locator, Page} from '@playwright/test';

import {AccountsPage} from './AccountsPage';
import {DataTablePage} from './DataTablePage';

export class AccountManagementWidgetPage extends AccountsPage {
	readonly accountCell: (accountName: string) => Locator;
	readonly editButton: Locator;
	readonly manageOrganizationsButton: Locator;
	readonly page: Page;
	readonly searchInput: Locator;
	readonly selectAccountButton: Locator;

	constructor(page: Page) {
		super(page);
		this.accountCell = (accountName: string) => {
			return this.page.getByRole('cell', {
				exact: true,
				name: `${accountName}`,
			});
		};
		this.editButton = page
			.getByRole('button', {name: 'Edit'})
			.or(page.getByRole('menuitem', {name: 'Edit'}));
		this.manageOrganizationsButton = page
			.getByRole('button', {name: 'Manage Organizations'})
			.or(page.getByRole('menuitem', {name: 'Manage Organizations'}));

		// @ts-ignore

		this.accountsTable = new DataTablePage(
			page,
			page.locator(
				'#_com_liferay_account_admin_web_internal_portlet_AccountEntriesManagementPortlet_accountEntriesSearchContainer'
			)
		);
		this.page = page;
		this.searchInput = page.locator('input[placeholder="Search for"]');
		this.selectAccountButton = page
			.getByRole('link', {name: 'Select Account'})
			.or(page.getByRole('menuitem', {name: 'Select Account'}));
	}
}
