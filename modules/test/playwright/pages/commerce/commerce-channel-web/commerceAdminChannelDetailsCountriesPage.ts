/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Locator, Page} from '@playwright/test';

import {searchTableRowByValue} from '../commerceDNDTablePage';

export class CommerceAdminChannelDetailsCountriesPage {
	readonly addCountryButton: Locator;
	readonly addCountryModal: Locator;
	readonly addCountryModalCountry: (countryName: string) => Locator;
	readonly addCountryModalSelectButton: Locator;
	readonly countriesTable: Locator;
	readonly countriesTableRow: (
		colPosition: number,
		value: number | string,
		strictEqual?: boolean
	) => Promise<{column: Locator; row: Locator}>;
	readonly countriesTableRows: () => Promise<Locator[]>;
	readonly countriesTableRowAction: (
		countryName: string,
		action: string
	) => Promise<Locator>;
	readonly page: Page;

	constructor(page: Page) {
		this.addCountryButton = page.getByLabel('Add Country');
		this.addCountryModal = page.getByRole('dialog', {name: 'Add Country'});
		this.addCountryModalCountry = (countryName: string) =>
			this.addCountryModal.getByRole('checkbox', {
				exact: true,
				name: `Select ${countryName}`,
			});
		this.addCountryModalSelectButton = this.addCountryModal.getByRole(
			'button',
			{exact: true, name: 'Select'}
		);
		this.countriesTable = page.locator(
			'#_com_liferay_commerce_channel_web_internal_portlet_CommerceChannelsPortlet_editChannelContainer .fds table'
		);
		this.countriesTableRow = async (
			colPosition: number,
			value: number | string,
			strictEqual: boolean = false
		) => {
			return await searchTableRowByValue(
				this.countriesTable,
				colPosition,
				String(value),
				strictEqual
			);
		};
		this.countriesTableRows = async () => {
			await this.countriesTable.elementHandle();

			return await this.countriesTable.locator('tbody tr').all();
		};
		this.countriesTableRowAction = async (
			countryName: string,
			action: string
		) => {
			const countriesTableRow = await this.countriesTableRow(
				0,
				countryName,
				true
			);

			if (countriesTableRow && countriesTableRow.column) {
				return countriesTableRow.row.getByRole('link', {
					name: action,
				});
			}

			throw new Error(
				`Cannot locate country row with name ${countryName}`
			);
		};
		this.page = page;
	}
}
