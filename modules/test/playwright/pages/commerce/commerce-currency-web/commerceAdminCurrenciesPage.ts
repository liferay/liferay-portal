/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Locator, Page} from '@playwright/test';

import {ApplicationsMenuPage} from '../../product-navigation-applications-menu/ApplicationsMenuPage';
import {CommerceDNDTablePage} from '../commerceDNDTablePage';

export class CommerceAdminCurrenciesPage extends CommerceDNDTablePage {
	readonly actionsButton: Locator;
	readonly activeMenuItem: Locator;
	readonly activeToggleMenuItem: Locator;
	readonly addCurrencyAddButton: Locator;
	readonly applicationsMenuPage: ApplicationsMenuPage;
	readonly backLink: Locator;
	readonly currencyNameLink: (currencyName: string) => Locator;
	readonly deleteMenuItem: Locator;
	readonly filterManagementToolbar: Locator;
	readonly firstRowCurrencyCellName: (currencyName: string) => Locator;
	readonly lastRowCurrencyCellName: (currencyName: string) => Locator;
	readonly noResultsFoundText: Locator;
	readonly primaryMenuItem: Locator;
	readonly priorityButton: Locator;
	readonly search: Locator;
	readonly searchButton: Locator;

	constructor(page: Page) {
		super(
			page,
			'#_com_liferay_commerce_currency_web_internal_portlet_CommerceCurrencyPortlet_fm .fds table'
		);

		this.actionsButton = page.getByRole('button', {name: 'Actions'});
		this.activeMenuItem = page.getByRole('menuitem', {name: 'Active'});
		this.activeToggleMenuItem = page.getByRole('menuitem', {
			name: 'Toggle Active',
		});
		this.addCurrencyAddButton = page.getByRole('button', {
			exact: true,
			name: 'Add Currency',
		});
		this.applicationsMenuPage = new ApplicationsMenuPage(page);
		this.backLink = page.getByRole('link', {exact: true, name: 'Back'});
		this.currencyNameLink = (currencyName) =>
			page.getByRole('link', {name: currencyName});
		this.deleteMenuItem = page.getByRole('menuitem', {name: 'Delete'});
		this.filterManagementToolbar = page
			.getByTestId('managementToolbar')
			.getByRole('button', {name: 'Filter'});
		this.firstRowCurrencyCellName = (currencyName) =>
			page.locator('tbody tr').first().filter({hasText: currencyName});
		this.lastRowCurrencyCellName = (currencyName) =>
			page.locator('tbody tr').last().filter({hasText: currencyName});
		this.noResultsFoundText = page.getByText('No Results Found');
		this.primaryMenuItem = page.getByRole('menuitem', {name: 'Primary'});
		this.priorityButton = page
			.getByRole('columnheader', {name: 'Priority'})
			.getByRole('button');
		this.search = page.getByPlaceholder('Search');
		this.searchButton = page
			.getByTestId('managementToolbar')
			.getByRole('button', {name: 'Search'});
	}

	async goto() {
		await this.applicationsMenuPage.goToCommerceCurrencies();
	}
}
