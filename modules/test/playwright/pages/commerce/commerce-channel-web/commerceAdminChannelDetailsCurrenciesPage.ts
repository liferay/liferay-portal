/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {FrameLocator, Locator, Page} from '@playwright/test';

import {ApplicationsMenuPage} from '../../product-navigation-applications-menu/ApplicationsMenuPage';
import {searchTableRowByValue} from '../commerceDNDTablePage';

export class CommerceAdminChannelDetailsCurrenciesPage {
	readonly addCurrencyAddButton: Locator;
	readonly addCurrencyButton: Locator;
	readonly addCurrencyFrame: FrameLocator;
	readonly applicationsMenuPage: ApplicationsMenuPage;
	readonly currenciesTable: Locator;
	readonly currenciesTableRow: (
		colPosition: number,
		value: number | string,
		strictEqual?: boolean
	) => Promise<{column: Locator; row: Locator}>;
	readonly currenciesTableRows: () => Promise<Locator[]>;
	readonly currenciesTableRowAction: (
		currencyName: string,
		action: string
	) => Promise<Locator>;
	readonly currencyFrameCurrency: (currencyName: string) => Promise<Locator>;
	readonly page: Page;

	constructor(page: Page) {
		this.addCurrencyButton = page
			.getByTestId('managementToolbar')
			.locator('[data-testid="fdsCreationActionButton"]');
		this.addCurrencyAddButton = page.getByRole('button', {
			exact: true,
			name: 'Add',
		});
		this.addCurrencyFrame = page.frameLocator(
			'iframe[title="Add Currency"]'
		);
		this.applicationsMenuPage = new ApplicationsMenuPage(page);
		this.currenciesTable = page.locator(
			'#_com_liferay_commerce_channel_web_internal_portlet_CommerceChannelsPortlet_editChannelContainer .fds table'
		);
		this.currenciesTableRow = async (
			colPosition: number,
			value: number | string,
			strictEqual: boolean = false
		) => {
			return await searchTableRowByValue(
				this.currenciesTable,
				colPosition,
				String(value),
				strictEqual
			);
		};
		this.currenciesTableRows = async () => {
			await this.currenciesTable.elementHandle();

			return await this.currenciesTable.locator('tbody tr').all();
		};
		this.currenciesTableRowAction = async (
			currencyName: string,
			action: string
		) => {
			const currenciesTableRow = await this.currenciesTableRow(
				0,
				currencyName,
				true
			);

			if (currenciesTableRow && currenciesTableRow.column) {
				return currenciesTableRow.row.getByRole('link', {
					name: action,
				});
			}

			throw new Error(
				`Cannot locate currency row with name ${currencyName}`
			);
		};
		this.currencyFrameCurrency = async (currencyName: string) => {
			return this.addCurrencyFrame.getByLabel(currencyName);
		};
		this.page = page;
	}
}
