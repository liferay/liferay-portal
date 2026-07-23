/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {dataApiHelpersTest} from '../../../fixtures/dataApiHelpersTest';
import {featureFlagsTest} from '../../../fixtures/featureFlagsTest';
import {loginTest} from '../../../fixtures/loginTest';
import getRandomString from '../../../utils/getRandomString';
import {ObjectEntryFolderExternalReferenceCode} from '../../../utils/objectEntryFolderConstants';
import {exportImportPagesTest} from './fixtures/exportImportPagesTest';

const test = mergeTests(
	dataApiHelpersTest,
	exportImportPagesTest,
	featureFlagsTest({
		'LPD-11235': {enabled: false},
		'LPD-57655': {enabled: false},
	}),
	loginTest()
);

test('Basic Web Content checkbox is displayed when importing LAR with Basic Web Content', async ({
	apiHelpers,
	exportImportPage,
}) => {
	const {assetLibraryId1, assetLibraryId2} =
		await test.step('Create two CMS spaces', async () => {
			const assetLibrary1 =
				await apiHelpers.headlessAssetLibrary.createAssetLibrary({
					name: getRandomString(),
					settings: {},
					type: 'Space',
				});

			const assetLibrary2 =
				await apiHelpers.headlessAssetLibrary.createAssetLibrary({
					name: getRandomString(),
					settings: {},
					type: 'Space',
				});

			await apiHelpers.objectEntry.postObjectEntry(
				{
					objectEntryFolderExternalReferenceCode:
						ObjectEntryFolderExternalReferenceCode.CONTENTS,
					title: getRandomString(),
				},
				'cms/basic-web-contents',
				assetLibrary1.name
			);

			return {
				assetLibraryId1: assetLibrary1.id,
				assetLibraryId2: assetLibrary2.id,
			};
		});

	const exportFilePath = await test.step('Export the space 1', async () => {
		await exportImportPage.goToExport(`/asset-library-${assetLibraryId1}`);

		const exportFilePath = await exportImportPage.export({
			taskName: getRandomString(),
		});
		expect(exportFilePath).toBeTruthy();

		return exportFilePath;
	});

	await test.step('Verify Basic Web Content checkbox appears in import UI', async () => {
		await exportImportPage.goToImport(`/asset-library-${assetLibraryId2}`);
		await exportImportPage.selectImportFile({filePath: exportFilePath});

		const basicWebContentCheckbox = exportImportPage.page.getByText(
			'Basic Web Contents1'
		);
		await expect(basicWebContentCheckbox).toBeVisible();
	});
});
