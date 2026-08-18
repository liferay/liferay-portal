/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Page, TestType, expect} from '@playwright/test';

import {FDSTablePage} from '../../../../pages/mcp-server-web/FDSTablePage';
import getRandomString from '../../../../utils/getRandomString';

export interface FDSTableOptions {
	columns: string[];
	name: string;
	rowActions: string[];
	sortOptions: string[];
	tag: string | string[];
}

/**
 * Generates the generic FDS table test cases for a screen. The consuming
 * spec must provide two fixtures: `fdsTablePage` (the screen's FDSTablePage
 * subclass) and `createFDSItem` (creates one item via API and returns its
 * name).
 */
export function createFDSTableTests<
	TArgs extends {
		createFDSItem: () => Promise<string>;
		fdsTablePage: FDSTablePage;
		page: Page;
	},
	TWorkerArgs,
>(test: TestType<TArgs, TWorkerArgs>, options: FDSTableOptions) {
	const {columns, name, rowActions, sortOptions, tag} = options;

	test.describe(`${name} - FDS Table`, () => {
		let seededItemName: string;

		// Seed one item so the FDS renders the table instead of the empty state

		test.beforeEach(async ({createFDSItem}) => {
			seededItemName = await createFDSItem();
		});

		test('Shows the table columns', {tag}, async ({fdsTablePage}) => {
			await fdsTablePage.goto();

			for (const column of columns) {
				await expect(fdsTablePage.columnHeader(column)).toBeVisible();
			}
		});

		test('Offers the sort options', {tag}, async ({fdsTablePage}) => {
			await fdsTablePage.goto();

			await fdsTablePage.orderButton.click();

			for (const sortOption of sortOptions) {
				await expect(fdsTablePage.sortOption(sortOption)).toBeVisible();
			}
		});

		test(
			'Searches the table by item name',
			{tag},
			async ({createFDSItem, fdsTablePage}) => {
				await createFDSItem();

				await fdsTablePage.goto();

				await fdsTablePage.search(seededItemName);

				await expect(fdsTablePage.rows).toHaveCount(1);
				await expect(fdsTablePage.row(seededItemName)).toBeVisible();
			}
		);

		test(
			'Shows an empty result when searching for a missing item',
			{tag},
			async ({fdsTablePage}) => {
				await fdsTablePage.goto();

				await fdsTablePage.search(`missing-${getRandomString()}`);

				await expect(fdsTablePage.rows).toHaveCount(0);
			}
		);

		test('Offers the row actions', {tag}, async ({fdsTablePage, page}) => {
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
		});
	});
}
