/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {dataApiHelpersTest} from '../../../../fixtures/dataApiHelpersTest';
import {featureFlagsTest} from '../../../../fixtures/featureFlagsTest';
import {loginTest} from '../../../../fixtures/loginTest';
import {applyFDSSelectionFilter} from '../../../../utils/applyFDSSelectionFilter';
import getRandomString from '../../../../utils/getRandomString';
import {cmsPagesTest} from '../fixtures/cmsPagesTest';

const test = mergeTests(
	cmsPagesTest,
	dataApiHelpersTest,
	featureFlagsTest({
		'LPD-17564': {enabled: true},
	}),
	loginTest()
);

test(
	'Filtering the Files section by an image extension excludes non-image files',
	{tag: ['@LPD-95544', '@LPD-95544/TC-21.d']},
	async ({apiHelpers, assetsPage, page}) => {
		const applicationName = 'cms/basic-documents';
		const imageTitle = `${getRandomString()}.png`;
		const textTitle = `${getRandomString()}.txt`;

		await test.step('Upload a PNG image and a plain text file into a fresh Space', async () => {
			const space =
				await apiHelpers.headlessAssetLibrary.createAssetLibrary({
					name: `Space ${getRandomString()}`,
					type: 'Space',
				});

			await apiHelpers.objectEntry.postObjectEntry(
				{
					file: {
						fileBase64: 'R0lGODlhAQABAAAAACw=',
						name: imageTitle,
					},
					objectEntryFolderExternalReferenceCode: 'L_FILES',
					title: imageTitle,
				},
				applicationName,
				space.name
			);

			await apiHelpers.objectEntry.postObjectEntry(
				{
					file: {
						fileBase64:
							Buffer.from('plain text content').toString(
								'base64'
							),
						name: textTitle,
					},
					objectEntryFolderExternalReferenceCode: 'L_FILES',
					title: textTitle,
				},
				applicationName,
				space.name
			);
		});

		await test.step('Both files are visible before filtering', async () => {
			await assetsPage.gotoFiles();

			await assetsPage.changeVisualizationMode('Table');

			await expect(assetsPage.getItem(imageTitle)).toBeVisible();
			await expect(assetsPage.getItem(textTitle)).toBeVisible();
		});

		await test.step('Filtering by Extension png shows only the image file', async () => {
			await applyFDSSelectionFilter(page, {
				filter: 'Extension',
				value: 'png',
			});

			await expect(assetsPage.getItem(imageTitle)).toBeVisible();
			await expect(assetsPage.getItem(textTitle)).toBeHidden();
		});
	}
);
