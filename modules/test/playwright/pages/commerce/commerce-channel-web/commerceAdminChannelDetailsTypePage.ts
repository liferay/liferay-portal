/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Locator, Page} from '@playwright/test';

import {searchTableRowByValue} from '../commerceDNDTablePage';

export class CommerceAdminChannelDetailsTypePage {
	readonly page: Page;
	readonly typeLinkTab: Locator;
	readonly typeTable: Locator;
	readonly typeTableRow: (
		colPosition: number,
		value: number | string,
		strictEqual?: boolean
	) => Promise<{column: Locator; row: Locator}>;
	readonly typeTableRowAction: (pageName: string) => Promise<Locator>;
	readonly selectSiteButton: Locator;

	constructor(page: Page) {
		this.page = page;
		this.typeLinkTab = page.getByRole('link', {name: 'Type'});
		this.typeTable = page
			.frameLocator('iframe[title="Select Site"]')
			.locator(
				'#_com_liferay_item_selector_web_portlet_ItemSelectorPortlet_sitesSearchContainer table'
			);
		this.typeTableRow = async (
			colPosition: number,
			value: number | string,
			strictEqual: boolean = false
		) => {
			return await searchTableRowByValue(
				this.typeTable,
				colPosition,
				String(value),
				strictEqual
			);
		};
		this.typeTableRowAction = async (siteName: string) => {
			const pagesTableRow = await this.typeTableRow(0, siteName, true);

			if (pagesTableRow && pagesTableRow.column) {
				return pagesTableRow.row.getByRole('button');
			}

			throw new Error(`Cannot locate table row with name ${siteName}`);
		};
		this.selectSiteButton = page.getByRole('button', {name: 'Select Site'});
	}
}
