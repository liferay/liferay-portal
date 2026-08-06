/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {dataApiHelpersTest} from '../../../../fixtures/dataApiHelpersTest';
import {loginTest} from '../../../../fixtures/loginTest';
import {applyFDSSelectionFilter} from '../../../../utils/applyFDSSelectionFilter';
import getRandomString from '../../../../utils/getRandomString';
import {cmsPagesTest} from '../fixtures/cmsPagesTest';

const test = mergeTests(cmsPagesTest, dataApiHelpersTest, loginTest());

test(
	'Combining Space, content type, and keyword filters in the All section narrows to only matching items',
	{tag: ['@LPD-95544', '@LPD-95544/TC-21.e']},
	async ({apiHelpers, assetsPage, page}) => {
		const applicationName = 'cms/basic-web-contents';
		const keyword = getRandomString();
		const matchingTitle = `${keyword} Match`;
		const wrongKeywordTitle = getRandomString();

		let space1Name: string;

		await test.step('Create two Spaces with content covering each filter combination', async () => {
			const space1 =
				await apiHelpers.headlessAssetLibrary.createAssetLibrary({
					name: `Space ${getRandomString()}`,
					type: 'Space',
				});

			const space2 =
				await apiHelpers.headlessAssetLibrary.createAssetLibrary({
					name: `Space ${getRandomString()}`,
					type: 'Space',
				});

			space1Name = space1.name;

			await apiHelpers.objectEntry.postObjectEntry(
				{
					objectEntryFolderExternalReferenceCode: 'L_CONTENTS',
					title: matchingTitle,
				},
				applicationName,
				space1.name
			);

			await apiHelpers.objectEntry.postObjectEntry(
				{
					objectEntryFolderExternalReferenceCode: 'L_CONTENTS',
					title: wrongKeywordTitle,
				},
				applicationName,
				space1.name
			);

			await apiHelpers.objectEntry.postObjectEntry(
				{
					file: {
						fileBase64: 'R0lGODlhAQABAAAAACw=',
						name: `${matchingTitle} File.png`,
					},
					objectEntryFolderExternalReferenceCode: 'L_FILES',
					title: `${matchingTitle} File`,
				},
				'cms/basic-documents',
				space1.name
			);

			await apiHelpers.objectEntry.postObjectEntry(
				{
					objectEntryFolderExternalReferenceCode: 'L_CONTENTS',
					title: `${keyword} Other Space`,
				},
				applicationName,
				space2.name
			);
		});

		await test.step('Apply Space and Type filters, then search by keyword', async () => {
			await assetsPage.gotoAll();

			await applyFDSSelectionFilter(page, {
				filter: 'Space',
				value: space1Name,
			});

			await applyFDSSelectionFilter(page, {
				chained: true,
				filter: 'Type',
				value: 'Basic Web Content',
			});

			const searchInput = page.getByRole('searchbox', {name: 'Search'});

			await searchInput.fill(keyword);
			await searchInput.press('Enter');
		});

		await test.step('Only the item matching all three criteria remains', async () => {
			await expect(assetsPage.getItem(matchingTitle)).toBeVisible();
			await expect(assetsPage.getItem(wrongKeywordTitle)).toBeHidden();
			await expect(
				assetsPage.getItem(`${matchingTitle} File`)
			).toBeHidden();
			await expect(
				assetsPage.getItem(`${keyword} Other Space`)
			).toBeHidden();
		});
	}
);
