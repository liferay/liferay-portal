/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {documentLibraryPagesTest} from '../../../fixtures/documentLibraryPages.fixtures';
import {loginTest} from '../../../fixtures/loginTest';
import {searchAdminPageTest} from '../../../fixtures/searchAdminPageTest';
import {RecycleBinPage} from '../../../pages/trash-web/RecycleBinPage';
import {reindexAllSearchIndexes} from '../utils/reindexAllSearchIndexes';
import {viewUpgradedDocument} from '../utils/viewUpgradedDocument';

const test = mergeTests(
	documentLibraryPagesTest,
	loginTest(),
	searchAdminPageTest
);

test.describe.serial('View AFS store upgrade', () => {
	test(
		'Can view the upgraded document library',
		{tag: '@LPD-104390'},
		async ({page, searchAdminPage}) => {
			await test.step('Reindex all search indexes', async () => {
				await reindexAllSearchIndexes({searchAdminPage});
			});

			for (const [title, expectedSize] of [
				['Document1', 22016],
				['Image1', 5176],
			] as const) {
				await test.step(`View ${title} after upgrade`, async () => {
					await viewUpgradedDocument({
						documentPageURL: '/web/site-name/document',
						expectedSize,
						page,
						title,
					});
				});
			}
		}
	);

	test(
		'Can delete the upgraded document library entries',
		{tag: '@LPD-104390'},
		async ({documentLibraryPage, page}) => {
			await documentLibraryPage.goto('/site-name');

			await documentLibraryPage.changeView('cards');

			for (const title of ['Document1', 'Image1']) {
				const card = page.locator(`.card-body:has-text('${title}')`);

				await expect(card).toBeVisible();

				await documentLibraryPage.moveToRecycleBin(title);

				await expect(card).toBeHidden();
			}

			const recycleBinPage = new RecycleBinPage(page);

			await recycleBinPage.goto('/site-name');

			for (const title of ['Document1', 'Image1']) {
				await recycleBinPage.delete(title);

				await recycleBinPage.assertEntryAbsent(title);
			}
		}
	);
});
