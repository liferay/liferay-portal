/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Page, expect, mergeTests} from '@playwright/test';

import {dataApiHelpersTest} from '../../../fixtures/dataApiHelpersTest';
import {loginTest} from '../../../fixtures/loginTest';
import {ApiHelpers} from '../../../helpers/ApiHelpers';
import getRandomString from '../../../utils/getRandomString';
import {performLoginViaApi} from '../../../utils/performLogin';
import {AssetsPage} from '../main/pages/AssetsPage';
import {DataSetPage} from '../main/pages/DataSetPage';
import {cmsPagesTest} from './fixtures/cmsPagesTest';

const test = mergeTests(cmsPagesTest, dataApiHelpersTest, loginTest());

const APPLICATION_NAME = 'cms/basic-documents';
const FILE_COUNT = 2;

const SPACE_NAME = 'Default';

const VALID_IMAGE_FILE_BASE64 =
	'iVBORw0KGgoAAAANSUhEUgAAAAEAAAABAQMAAAAl21bKAAAAA1BMVEUAAACnej3aAAAAAXRSTlMAQObYZgAAAApJREFUCNdjYAAAAAIAAeIhvDMAAAAASUVORK5CYII=';

let createdEntryIds: number[];
let singleTokenPrefix: string;
let multiTokenPrefix: string;

async function createFile(apiHelpers: ApiHelpers, title: string) {
	return apiHelpers.objectEntry.postObjectEntry(
		{
			file: {
				fileBase64: VALID_IMAGE_FILE_BASE64,
				name: `${title}.png`,
			},
			objectEntryFolderExternalReferenceCode: 'L_FILES',
			title,
		},
		APPLICATION_NAME,
		SPACE_NAME
	);
}

async function expectBulkActionVisible(page: Page, action: string) {
	await expect(async () => {
		await openBulkActionMenu(page);

		await expect(
			page.getByRole('menuitem', {exact: true, name: action})
		).toBeVisible({timeout: 1000});

		await page.keyboard.press('Escape');
	}).toPass({timeout: 5000});
}

async function openBulkActionMenu(page: Page) {
	await page
		.getByTestId('visualization-mode-table')
		.getByLabel('Actions')
		.click();
}

async function searchSelectAndExpectDelete(
	assetsPage: AssetsPage,
	page: Page,
	search: string,
	titles: string[]
) {
	await assetsPage.gotoFiles();
	await assetsPage.changeVisualizationMode('Table');

	const dataSetPage = new DataSetPage(page);

	await dataSetPage.search(search);

	for (const title of titles) {
		await selectRow(page, title);
	}

	await expectBulkActionVisible(page, 'Delete');
}

async function selectRow(page: Page, title: string) {
	await page.getByLabel(`Select ${title}`, {exact: true}).check();
}

test.describe(
	'transformFDSBulkActions wires the bulk action menu in the Files view',
	{tag: '@LPD-86106'},
	() => {
		test.beforeAll(async ({browser}) => {
			const page = await browser.newPage();

			await performLoginViaApi({page, screenName: 'test'});

			const setupApiHelpers = new ApiHelpers(page);

			singleTokenPrefix = `singleToken_${getRandomString().replace(/-/g, '')}`;
			multiTokenPrefix = `multiToken_${getRandomString().replace(/-/g, '')}`;

			const createdEntries: ObjectEntry[] = [];

			for (let i = 0; i < FILE_COUNT; i++) {
				createdEntries.push(
					await createFile(
						setupApiHelpers,
						`${singleTokenPrefix}_${i}`
					)
				);
				createdEntries.push(
					await createFile(
						setupApiHelpers,
						`${multiTokenPrefix} ${i}`
					)
				);
			}

			createdEntryIds = createdEntries.map((entry) => entry.id);

			await page.close();
		});

		test.afterAll(async ({browser}) => {
			const page = await browser.newPage();

			await performLoginViaApi({page, screenName: 'test'});

			const teardownApiHelpers = new ApiHelpers(page);

			if (createdEntryIds?.length) {
				for (const entryId of createdEntryIds) {
					await teardownApiHelpers.objectEntry
						.deleteObjectEntry(APPLICATION_NAME, String(entryId))
						.catch(() => {});
				}
			}

			await page.close();
		});

		test('shows Delete in the bulk menu when a single item is selected', async ({
			assetsPage,
			page,
		}) => {
			await searchSelectAndExpectDelete(
				assetsPage,
				page,
				`${singleTokenPrefix}_0`,
				[`${singleTokenPrefix}_0`]
			);
		});

		test(
			'shows Delete in the bulk menu when multiple items are found by a ' +
				'partial title',
			{tag: '@LPD-96225'},
			async ({assetsPage, page}) => {
				await searchSelectAndExpectDelete(
					assetsPage,
					page,
					singleTokenPrefix,
					[`${singleTokenPrefix}_0`, `${singleTokenPrefix}_1`]
				);
			}
		);

		test(
			'shows Delete in the bulk menu when multiple items are found by a ' +
				'whole word in a multi-word title',
			{tag: '@LPD-96225'},
			async ({assetsPage, page}) => {
				await searchSelectAndExpectDelete(
					assetsPage,
					page,
					multiTokenPrefix,
					[`${multiTokenPrefix} 0`, `${multiTokenPrefix} 1`]
				);
			}
		);
	}
);
