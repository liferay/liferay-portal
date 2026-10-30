/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Locator, Page, expect} from '@playwright/test';

import {clickAndExpectToBeVisible} from '../../../utils/clickAndExpectToBeVisible';
import {searchTableRowByValue} from '../commerceDNDTablePage';

export class CommerceAdminChannelDetailsCurrenciesPage {
	readonly addCurrencyButton: Locator;
	readonly addCurrencyModal: Locator;
	readonly addCurrencyModalCurrency: (currencyName: string) => Locator;
	readonly addCurrencyModalSelectButton: Locator;
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
	readonly page: Page;

	constructor(page: Page) {
		this.addCurrencyButton = page
			.getByTestId('managementToolbar')
			.locator('[data-testid="fdsCreationActionButton"]');
		this.addCurrencyModal = page.getByRole('dialog', {
			name: 'Add Currency',
		});
		this.addCurrencyModalCurrency = (currencyName: string) =>
			this.addCurrencyModal.getByRole('checkbox', {
				exact: true,
				name: `Select ${currencyName}`,
			});
		this.addCurrencyModalSelectButton = this.addCurrencyModal.getByRole(
			'button',
			{exact: true, name: 'Select'}
		);
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
		this.page = page;
	}

	async addCurrencies(currencyNames: string[]) {
		await clickAndExpectToBeVisible({
			target: this.addCurrencyModal,
			trigger: this.addCurrencyButton,
		});

		for (const currencyName of currencyNames) {
			await this.addCurrencyModalCurrency(currencyName).check();
		}

		await this.addCurrencyModalSelectButton.click();

		for (const currencyName of currencyNames) {
			await expect(
				(await this.currenciesTableRow(0, currencyName, true)).row
			).toBeVisible();
		}
	}
}
