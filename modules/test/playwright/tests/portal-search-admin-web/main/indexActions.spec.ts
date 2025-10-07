/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {loginTest} from '../../../fixtures/loginTest';
import {searchAdminPageTest} from '../../../fixtures/searchAdminPageTest';

export const test = mergeTests(loginTest(), searchAdminPageTest);

test('should have a human-readable label for every index action', async ({
	searchAdminPage,
}) => {
	await test.step('Navigate to Search Admin page', async () => {
		await searchAdminPage.goto();

		await searchAdminPage.goToIndexActionsTab();
	});

	// Assert that the first two items would reindex all indexes and dictionaries

	await expect(
		searchAdminPage.indexActionsReindexActionItems.nth(0)
	).toHaveText(/All Search Indexes/);
	await expect(
		searchAdminPage.indexActionsReindexActionItems.nth(1)
	).toHaveText(/All Spell Check Dictionaries/);

	// Assert that the rest of the items have a human-readable label
	// Regex to match the format: "Label With Words and Spaces (index.name.with.periods)"

	for (
		let i = 2;
		i < (await searchAdminPage.indexActionsReindexActionItems.count());
		i++
	) {
		await expect(
			searchAdminPage.indexActionsReindexActionItems
				.nth(i)
				.locator('.list-group-title')
		).toHaveText(/^[\w\s]+ \([\w.]+(#[\w\d]+)*\)$/);
	}
});
