/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {collectionsPagesTest} from '../../../fixtures/collectionsPagesTest';
import {dataApiHelpersTest} from '../../../fixtures/dataApiHelpersTest';
import {featureFlagsTest} from '../../../fixtures/featureFlagsTest';
import {isolatedSiteTest} from '../../../fixtures/isolatedSiteTest';
import {loginTest} from '../../../fixtures/loginTest';
import getRandomString from '../../../utils/getRandomString';

const test = mergeTests(
	loginTest(),
	isolatedSiteTest,
	dataApiHelpersTest,
	featureFlagsTest({'LPD-39304': {enabled: true}}),
	collectionsPagesTest
);

test.describe('Manual Collection', () => {
	test('Only the selected item types are offered when adding assets', {tag: '@LPS-143093'}, async ({
		collectionsPage,
		page,
		site,
	}) => {
		const excludedTypes = ['Blogs Entry', 'Web Content Article'];
		const includedTypes = [
			'Calendar Event',
			'Translation',
			'Web Content Folder',
		];

		await test.step('Create a manual collection', async () => {
			await collectionsPage.goto(site.friendlyUrlPath);

			await collectionsPage.addNewManualCollection(getRandomString());
		});

		await test.step('Restrict the collection to every item type except Blogs Entry and Web Content Article', async () => {
			await collectionsPage.restrictManualCollectionItemTypes(
				excludedTypes
			);
		});

		await test.step('Open the Type filter in the asset entries item selector', async () => {
			const modal = await collectionsPage.openSelectItemsModal();

			await modal.getByRole('button', {name: 'Filter'}).click();

			await page.getByRole('menuitem', {name: 'Type'}).click();
		});

		await test.step('Only the selected item types are offered', async () => {
			for (const includedType of includedTypes) {
				await expect(
					page.getByRole('checkbox', {exact: true, name: includedType})
				).toBeVisible();
			}

			for (const excludedType of excludedTypes) {
				await expect(
					page.getByRole('checkbox', {exact: true, name: excludedType})
				).toHaveCount(0);
			}
		});
	});
});
