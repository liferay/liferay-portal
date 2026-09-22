/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect} from '@playwright/test';

import {SearchAdminPage} from '../../../pages/portal-search-admin-web/SearchAdminPage';

/**
 * Reindexes every search index and waits for the job to finish. SearchAdminPage
 * already asserts that the progress bar appears, so this waits only for it to
 * clear.
 */
export async function reindexAllSearchIndexes({
	searchAdminPage,
}: {
	searchAdminPage: SearchAdminPage;
}) {
	await searchAdminPage.goto();

	await searchAdminPage.goToIndexActionsTab();

	await searchAdminPage.reindexAllSearchIndexes();

	const indexActionsItem =
		await searchAdminPage.getIndexActionsItem('All Search Indexes');

	await expect(indexActionsItem).toBeVisible();

	await expect(indexActionsItem.locator('.progress')).toBeHidden({
		timeout: 120 * 1000,
	});
}
