/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';
import {createReadStream} from 'fs';
import path from 'path';

import {dataApiHelpersTest} from '../../../fixtures/dataApiHelpersTest';
import {featureFlagsTest} from '../../../fixtures/featureFlagsTest';
import {loginTest} from '../../../fixtures/loginTest';
import getRandomString from '../../../utils/getRandomString';
import {exportImportPagesTest} from './fixtures/exportImportPagesTest';
import {exportAndDownloadLar} from './utils/exportAndDownloadLar';

const test = mergeTests(
	dataApiHelpersTest,
	exportImportPagesTest,
	featureFlagsTest({
		'LPD-57655': {enabled: true},
	}),
	loginTest()
);

test(
	'Can import an asset library scoped lar file into another asset library',
	{tag: '@LPD-104250'},
	async ({apiHelpers, exportImportPage}) => {
		const {document, sourceAssetLibrary, targetAssetLibrary} =
			await test.step('Add a document to an asset library', async () => {
				const sourceAssetLibrary =
					await apiHelpers.headlessAssetLibrary.createAssetLibrary({
						name: getRandomString(),
						type: 'AssetLibrary',
					});

				const targetAssetLibrary =
					await apiHelpers.headlessAssetLibrary.createAssetLibrary({
						name: getRandomString(),
						type: 'AssetLibrary',
					});

				const document =
					await apiHelpers.headlessDelivery.postAssetLibraryDocument(
						sourceAssetLibrary.id,
						createReadStream(
							path.join(__dirname, 'dependencies/Document.jpg')
						),
						{
							fileName: `${getRandomString()}.jpg`,
							title: getRandomString(),
						}
					);

				return {document, sourceAssetLibrary, targetAssetLibrary};
			});

		const {folderPath, name} =
			await test.step('Export the asset library', async () => {
				await exportImportPage.goToExport(
					sourceAssetLibrary.friendlyURL
				);

				await exportImportPage.clickNew();

				return await exportAndDownloadLar(exportImportPage);
			});

		await test.step('Import the lar file into the other asset library', async () => {
			await exportImportPage.goToImport(targetAssetLibrary.friendlyURL);

			await exportImportPage.newButton.click();

			await exportImportPage.import({folderPath, name});
		});

		await test.step('Verify the document is in the other asset library', async () => {
			await expect(async () => {
				const {items} =
					await apiHelpers.headlessDelivery.getSiteDocumentsPage(
						targetAssetLibrary.siteId
					);

				expect(
					items.map((item: {title: string}) => item.title)
				).toContain(document.title);
			}).toPass();
		});
	}
);
