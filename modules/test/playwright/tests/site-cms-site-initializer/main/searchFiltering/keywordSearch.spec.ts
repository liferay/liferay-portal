/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {dataApiHelpersTest} from '../../../../fixtures/dataApiHelpersTest';
import {loginTest} from '../../../../fixtures/loginTest';
import getRandomString from '../../../../utils/getRandomString';
import {cmsPagesTest} from '../fixtures/cmsPagesTest';

const test = mergeTests(cmsPagesTest, dataApiHelpersTest, loginTest());

test(
	'Searching a keyword in the All section returns matching content and files from all Spaces',
	{tag: ['@LPD-95544', '@LPD-95544/TC-21.a']},
	async ({apiHelpers, assetsPage}) => {
		const keyword = getRandomString();
		const matchingContentTitle = `${keyword} Content`;
		const matchingFileTitle = `${keyword} File`;
		const unrelatedTitle = getRandomString();

		await test.step('Create a matching content and a matching file in different Spaces, and an unrelated content', async () => {
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

			await apiHelpers.objectEntry.postObjectEntry(
				{
					objectEntryFolderExternalReferenceCode: 'L_CONTENTS',
					title: matchingContentTitle,
				},
				'cms/basic-web-contents',
				space1.name
			);

			await apiHelpers.objectEntry.postObjectEntry(
				{
					file: {
						fileBase64: 'R0lGODlhAQABAAAAACw=',
						name: `${matchingFileTitle}.png`,
					},
					objectEntryFolderExternalReferenceCode: 'L_FILES',
					title: matchingFileTitle,
				},
				'cms/basic-documents',
				space2.name
			);

			await apiHelpers.objectEntry.postObjectEntry(
				{
					objectEntryFolderExternalReferenceCode: 'L_CONTENTS',
					title: unrelatedTitle,
				},
				'cms/basic-web-contents',
				space1.name
			);
		});

		await test.step('All entries are visible before searching', async () => {
			await assetsPage.gotoAll();

			await expect(
				assetsPage.getItem(matchingContentTitle)
			).toBeVisible();
			await expect(assetsPage.getItem(matchingFileTitle)).toBeVisible();
			await expect(assetsPage.getItem(unrelatedTitle)).toBeVisible();
		});

		await test.step('Searching the keyword returns only the matching content and file', async () => {
			await assetsPage.dataSetFragmentPage.search(keyword);

			await expect(
				assetsPage.getItem(matchingContentTitle)
			).toBeVisible();
			await expect(assetsPage.getItem(matchingFileTitle)).toBeVisible();
			await expect(assetsPage.getItem(unrelatedTitle)).toBeHidden();
		});
	}
);
