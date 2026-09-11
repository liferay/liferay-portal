/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Locator, Page} from '@playwright/test';

import {DataTablePage} from '../account-admin-web/DataTablePage';
import {GlobalMenuPage} from '../product-navigation-applications-menu/GlobalMenuPage';

export class CountriesManagementPage {
	readonly activateButton: Locator;
	readonly globalMenuPage: GlobalMenuPage;
	readonly countriesTable: DataTablePage;
	readonly deactivateButton: Locator;
	readonly deleteButton: Locator;
	readonly editButton: Locator;
	readonly noCountriesMessage: Locator;
	readonly noRegionsMessage: Locator;
	readonly page: Page;
	readonly regionsLink: Locator;
	readonly regionsTable: DataTablePage;

	constructor(page: Page) {
		this.activateButton = page
			.getByRole('button', {name: 'Activate'})
			.or(page.getByRole('link', {name: 'Activate'}))
			.or(page.getByRole('menuitem', {name: 'Activate'}));
		this.globalMenuPage = new GlobalMenuPage(page);
		this.countriesTable = new DataTablePage(
			page,
			page.locator(
				'#_com_liferay_address_web_internal_portlet_CountriesManagementAdminPortlet_countrySearchContainer'
			)
		);
		this.deactivateButton = page
			.getByRole('button', {name: 'Deactivate'})
			.or(page.getByRole('link', {name: 'Deactivate'}))
			.or(page.getByRole('menuitem', {name: 'Deactivate'}));
		this.deleteButton = page
			.getByRole('button', {name: 'Delete'})
			.or(page.getByRole('link', {name: 'Delete'}))
			.or(page.getByRole('menuitem', {name: 'Delete'}));
		this.editButton = page.getByRole('menuitem', {name: 'Edit'});
		this.noCountriesMessage = page.getByText('There are no countries.');
		this.noRegionsMessage = page
			.getByText('There are no regions.')
			.or(page.getByText('There are no active regions.'))
			.or(page.getByText('There are no inactive regions.'));
		this.page = page;
		this.regionsLink = page.getByRole('link', {
			name: 'Regions',
		});
		this.regionsTable = new DataTablePage(
			page,
			page.locator(
				'#_com_liferay_address_web_internal_portlet_CountriesManagementAdminPortlet_regionSearchContainer'
			)
		);
	}

	async goto() {
		await this.globalMenuPage.goToControlPanel('Countries');
	}
}
