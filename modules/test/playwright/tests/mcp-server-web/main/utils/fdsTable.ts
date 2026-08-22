/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Page, expect} from '@playwright/test';

import {FDSTablePage} from '../../../../pages/mcp-server-web/FDSTablePage';
import getRandomString from '../../../../utils/getRandomString';

export async function expectFDSTableColumns(
	fdsTablePage: FDSTablePage,
	columns: string[]
) {
	await fdsTablePage.goto();

	for (const column of columns) {
		await expect(fdsTablePage.columnHeader(column)).toBeVisible();
	}
}

export async function expectFDSTableSortOptions(
	fdsTablePage: FDSTablePage,
	sortOptions: string[]
) {
	await fdsTablePage.goto();

	await fdsTablePage.orderButton.click();

	for (const sortOption of sortOptions) {
		await expect(fdsTablePage.sortOption(sortOption)).toBeVisible();
	}
}

export async function expectFDSTableSearchFindsItem(
	createFDSItem: () => Promise<string>,
	fdsTablePage: FDSTablePage,
	seededItemName: string
) {
	await createFDSItem();

	await fdsTablePage.goto();

	await fdsTablePage.search(seededItemName);

	await expect(fdsTablePage.rows).toHaveCount(1);
	await expect(fdsTablePage.row(seededItemName)).toBeVisible();
}

export async function expectFDSTableSearchEmptyResult(
	fdsTablePage: FDSTablePage
) {
	await fdsTablePage.goto();

	await fdsTablePage.search(`missing-${getRandomString()}`);

	await expect(fdsTablePage.rows).toHaveCount(0);
}

export async function expectFDSTableRowActions(
	fdsTablePage: FDSTablePage,
	page: Page,
	seededItemName: string,
	rowActions: string[]
) {
	await fdsTablePage.goto();

	await fdsTablePage.search(seededItemName);
	await fdsTablePage.openActionsMenu(seededItemName);

	for (const rowAction of rowActions) {
		await expect(
			page.getByRole('menuitem', {
				exact: true,
				name: rowAction,
			})
		).toBeVisible();
	}
}
