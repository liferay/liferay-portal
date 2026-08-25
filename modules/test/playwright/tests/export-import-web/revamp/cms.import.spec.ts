/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {mergeTests} from '@playwright/test';
import {createReadStream} from 'fs';
import path from 'path';

import {dataApiHelpersTest} from '../../../fixtures/dataApiHelpersTest';
import {featureFlagsTest} from '../../../fixtures/featureFlagsTest';
import {loginTest} from '../../../fixtures/loginTest';
import getRandomString from '../../../utils/getRandomString';
import {ObjectEntryFolderExternalReferenceCode} from '../../../utils/objectEntryFolderConstants';
import {exportImportPagesTest} from './fixtures/exportImportPagesTest';
import {assertImportWizardControls} from './utils/assertImportWizardControls';
import {exportAndDownloadLar} from './utils/exportAndDownloadLar';

export const test = mergeTests(
	dataApiHelpersTest,
	exportImportPagesTest,
	featureFlagsTest({
		'LPD-57655': {enabled: true},
	}),
	loginTest()
);

test(
	'Can see corresponding import wizard controls at asset library level',
	{tag: '@LPD-100545'},
	async ({
		apiHelpers,
		exportImportDataSelectionPage,
		exportImportPage,
		page,
	}) => {
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

		await apiHelpers.headlessDelivery.postAssetLibraryDocument(
			assetLibrary1.id,
			createReadStream(path.join(__dirname, 'dependencies/Document.jpg'))
		);

		await exportImportPage.goToExport(`/asset-library-${assetLibrary1.id}`);

		await exportImportPage.clickNew();

		const {folderPath, name} = await exportAndDownloadLar(exportImportPage);

		await exportImportPage.goToImport(`/asset-library-${assetLibrary2.id}`);

		await assertImportWizardControls({
			contentLabel: 'Basic Web Contents',
			exportImportDataSelectionPage,
			exportImportPage,
			folderPath,
			hasCommentsAndRatings: true,
			hasMirrorWithOverwriting: true,
			hasSiteBuilder: false,
			name,
			page,
		});
	}
);
