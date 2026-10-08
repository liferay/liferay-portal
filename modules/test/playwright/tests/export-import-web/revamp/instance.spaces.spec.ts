/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {dataApiHelpersTest} from '../../../fixtures/dataApiHelpersTest';
import {featureFlagsTest} from '../../../fixtures/featureFlagsTest';
import {globalMenuPagesTest} from '../../../fixtures/globalMenuPagesTest';
import {loginTest} from '../../../fixtures/loginTest';
import getRandomString from '../../../utils/getRandomString';
import {exportImportPagesTest} from './fixtures/exportImportPagesTest';
import {exportAndDownloadLar} from './utils/exportAndDownloadLar';

export const test = mergeTests(
	dataApiHelpersTest,
	exportImportPagesTest,
	featureFlagsTest({
		'LPD-41307': {enabled: true},
		'LPD-57655': {enabled: true},
		'LPD-85946': {enabled: true},
	}),
	globalMenuPagesTest,
	loginTest()
);

test(
	'Can import a space with an object entry folder from an instance export',
	{tag: '@LPD-75352'},
	async ({
		apiHelpers,
		exportImportDataSelectionPage,
		exportImportPage,
		globalMenuPage,
		page,
	}) => {
		const space = await apiHelpers.headlessAssetLibrary.createAssetLibrary({
			name: getRandomString(),
		});

		const objectEntryFolder =
			await apiHelpers.objectFolder.createObjectEntryFolder({
				parentObjectEntryFolderExternalReferenceCode: 'L_CONTENTS',
				scopeKey: space.name,
				title: getRandomString(),
			});

		const lar = await test.step('Export the space', async () => {
			await globalMenuPage.goToApplications('Export');

			await exportImportPage.clickNew();

			await exportImportDataSelectionPage.uncheckAllItems();

			await exportImportDataSelectionPage.selectSpace(space.name);

			return await exportAndDownloadLar(exportImportPage);
		});

		await apiHelpers.objectFolder.deleteObjectEntryFolder(
			objectEntryFolder.id
		);

		await test.step('Import the space', async () => {
			await globalMenuPage.goToApplications('Import');

			await exportImportPage.clickNew();

			await exportImportPage.import({
				folderPath: lar.folderPath,
				name: getRandomString(),
				selectData: async () => {
					await expect(
						page.getByText('No spaces are selected.')
					).toBeVisible();

					await exportImportDataSelectionPage.selectSpace(space.name);
				},
			});
		});

		expect(
			await apiHelpers.objectFolder.getObjectEntryFolderByExternalReferenceCode(
				{
					externalReferenceCode:
						objectEntryFolder.externalReferenceCode,
					scopeKey: space.name,
				}
			)
		).toEqual(
			expect.objectContaining({
				externalReferenceCode: objectEntryFolder.externalReferenceCode,
				title: objectEntryFolder.title,
			})
		);
	}
);
