/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Locator, Page, expect} from '@playwright/test';

import {clickAndExpectToBeVisible} from '../../utils/clickAndExpectToBeVisible';
import {PORTLET_URLS} from '../../utils/portletUrls';
import {GlobalMenuPage} from '../product-navigation-applications-menu/GlobalMenuPage';
import {DataTablePage} from './DataTablePage';

export const searchTableRowByValue = async function (
	tableLocator: Locator,
	colPosition: number,
	value: string,
	strictEqual: boolean = false
) {
	await tableLocator.elementHandle();

	const rows = await tableLocator.getByRole('row').all();

	for await (const row of rows) {
		const column = row.getByRole('cell').nth(colPosition).first();

		const colValue = (await column.allInnerTexts()).join('');

		if (
			(strictEqual && colValue === value) ||
			(!strictEqual &&
				colValue.toLowerCase().indexOf(value.toLowerCase()) >= 0)
		) {
			return {column, row};
		}
	}

	throw new Error(`Cannot locate table row with value ${value}`);
};

export class AccountsPage {
	readonly accountGroupsTab: Locator;
	readonly accountNameLink: (name: string) => Locator;
	readonly accountRolesTab: Locator;
	readonly accountsTable: DataTablePage;
	readonly activateButton: Locator;
	readonly globalMenuPage: GlobalMenuPage;
	readonly channelDefaultsTab: Locator;
	readonly deactivateButton: Locator;
	readonly deleteButton: Locator;
	readonly detailsTab: Locator;
	readonly filterStatus: (status: string) => Locator;
	readonly manageUsersButton: Locator;
	readonly noAccountsMessage: Locator;
	readonly organizationsTab: Locator;
	readonly page: Page;
	readonly pageTitle: Locator;
	readonly usersTab: Locator;

	constructor(page: Page) {
		this.accountGroupsTab = page.getByRole('link', {
			name: 'Account Groups',
		});
		this.accountNameLink = (name) =>
			page.getByRole('link', {
				exact: true,
				name,
			});
		this.accountRolesTab = page.getByRole('link', {
			name: 'Roles',
		});
		this.accountsTable = new DataTablePage(
			page,
			page.locator(
				'#_com_liferay_account_admin_web_internal_portlet_AccountEntriesAdminPortlet_accountEntriesSearchContainer'
			)
		);
		this.activateButton = page
			.getByRole('button', {name: 'Activate'})
			.or(page.getByRole('link', {name: 'Activate'}));
		this.globalMenuPage = new GlobalMenuPage(page);
		this.channelDefaultsTab = page.getByRole('link', {
			name: 'Channel Defaults',
		});
		this.deactivateButton = page
			.getByRole('button', {name: 'Deactivate'})
			.or(page.getByRole('link', {name: 'Deactivate'}));
		this.deleteButton = page
			.getByRole('button', {name: 'Delete'})
			.or(page.getByRole('link', {name: 'Delete'}));
		this.detailsTab = page.getByRole('link', {
			name: 'Details',
		});
		this.filterStatus = (status: string) => {
			return page.getByText('Status: ' + status);
		};
		this.manageUsersButton = page
			.getByRole('menuitem', {
				name: 'Manage Users',
			})
			.or(
				page.getByRole('menuitem', {
					name: 'View Users',
				})
			);
		this.noAccountsMessage = page.getByText('No accounts were found.');
		this.organizationsTab = page.getByRole('link', {
			name: 'Organizations',
		});
		this.page = page;
		this.pageTitle = page.getByTestId('headerTitle');
		this.usersTab = page.getByRole('link', {
			exact: true,
			name: 'Users',
		});
	}

	async changeFilter(option: string) {
		await clickAndExpectToBeVisible({
			autoClick: true,
			target: this.accountsTable.filterMenuItem(option),
			trigger: this.accountsTable.filterButton,
		});

		if (option === 'Active') {
			await expect(this.accountsTable.clearButton).not.toBeVisible();
		}
		else {
			await this.filterStatus(option).waitFor({state: 'visible'});
		}
	}

	async goto(forceReload = true) {
		if (forceReload) {
			await this.globalMenuPage.goToHome();
		}

		await this.globalMenuPage.goToControlPanel('Accounts');
	}

	async gotoAccountAdmin(doAsUserId?: string) {
		await this.page.goto(
			doAsUserId
				? `${PORTLET_URLS.accountAdmin}&doAsUserId=${doAsUserId}`
				: PORTLET_URLS.accountAdmin
		);
	}

	async gotoAccountChannelDefaults(accountName: string) {
		await this.gotoAccountAdmin();
		await this.accountNameLink(accountName).click();
		await this.channelDefaultsTab.click();
	}

	async organizationName(name: string): Promise<Locator> {
		return this.page.getByText(name, {exact: true});
	}
}
