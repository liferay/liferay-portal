/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {documentLibraryPagesTest} from '../../../fixtures/documentLibraryPages.fixtures';
import {loginTest} from '../../../fixtures/loginTest';
import {searchAdminPageTest} from '../../../fixtures/searchAdminPageTest';
import {RecycleBinPage} from '../../../pages/trash-web/RecycleBinPage';
import {viewUpgradedDocument} from '../utils/viewUpgradedDocument';

const test = mergeTests(
	documentLibraryPagesTest,
	loginTest(),
	searchAdminPageTest
);

test.describe('View DB store upgrade', () => {
	test(
		'Can view and delete upgraded DB store content',
		{tag: '@LPD-104391'},
		async ({documentLibraryPage, page, searchAdminPage}) => {
			await test.step('Reindex all search indexes', async () => {
				await searchAdminPage.goto();

				await searchAdminPage.goToIndexActionsTab();

				await searchAdminPage.reindexAllSearchIndexes();

				const reindexAllSearchIndexes =
					await searchAdminPage.getIndexActionsItem(
						'All Search Indexes'
					);

				await expect(reindexAllSearchIndexes).toBeVisible();

				const progress = reindexAllSearchIndexes.locator('.progress');

				await expect(progress).toBeVisible();

				await expect(progress).toBeHidden({timeout: 120 * 1000});
			});

			await test.step('View the document after upgrade', async () => {
				await viewUpgradedDocument({
					documentPageURL: '/web/site-name/document',
					expectedSize: 22016,
					page,
					title: 'Document1',
				});
			});

			await test.step('View the image after upgrade', async () => {
				await viewUpgradedDocument({
					documentPageURL: '/web/site-name/document',
					expectedSize: 13229,
					page,
					title: 'Image1',
				});
			});

			await test.step('Delete the document and the image', async () => {
				await documentLibraryPage.goto('/site-name');

				await documentLibraryPage.moveToRecycleBin('Document1');

				await documentLibraryPage.moveToRecycleBin('Image1');

				const recycleBinPage = new RecycleBinPage(page);

				await recycleBinPage.goto('/site-name');

				await recycleBinPage.delete('Document1');

				await recycleBinPage.delete('Image1');

				await recycleBinPage.assertEntryAbsent('Document1');

				await recycleBinPage.assertEntryAbsent('Image1');
			});
		}
	);
});
