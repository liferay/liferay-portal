/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {dataApiHelpersTest} from '../../../fixtures/dataApiHelpersTest';
import {featureFlagsTest} from '../../../fixtures/featureFlagsTest';
import {loginTest} from '../../../fixtures/loginTest';
import getRandomString from '../../../utils/getRandomString';
import {normalizeRestPath} from '../../../utils/normalizeRestPath';
import {exportImportPagesTest} from './fixtures/exportImportPagesTest';
import {importReportPagesTest} from './fixtures/importReportPagesTest';
import {forceRequiredFieldImportError} from './utils/forceRequiredFieldImportError';

export const test = mergeTests(
	dataApiHelpersTest,
	exportImportPagesTest,
	featureFlagsTest({
		'LPD-57655': {enabled: true},
	}),
	importReportPagesTest,
	loginTest()
);

test(
	'Can see error report and details',
	{tag: '@LPD-100543'},
	async ({apiHelpers, exportImportPage, importReportPage, page}) => {
		test.setTimeout(120000);

		const space = await apiHelpers.headlessAssetLibrary.createAssetLibrary({
			name: `Space ${getRandomString()}`,
			type: 'Space',
		});

		const objectDefinition =
			await apiHelpers.objectAdmin.postRandomObjectDefinition({
				objectDefinitionSettings: [
					{
						name: 'acceptedGroupExternalReferenceCodes',
						value: space.externalReferenceCode as unknown as object,
					},
				],
				scope: 'depot',
				status: {code: 0},
			});

		apiHelpers.data.push({
			id: objectDefinition.id,
			type: 'objectDefinition',
		});

		const objectEntry = await apiHelpers.objectEntry.postObjectEntry(
			{},
			normalizeRestPath(objectDefinition.restContextPath),
			space.name
		);

		await exportImportPage.goToExport(`/asset-library-${space.id}`);

		const name = `MyExport-${getRandomString()}`;

		await exportImportPage.export(name);

		const folderPath = await exportImportPage.download(name);

		await forceRequiredFieldImportError(apiHelpers, objectDefinition.id);

		await exportImportPage.goToImport(`/asset-library-${space.id}`);

		await exportImportPage.newButton.click();

		await exportImportPage.import({
			folderPath,
			name,
			taskStatus: 'completedWithErrors',
		});

		await exportImportPage.goToImportDetails(name);

		await expect(
			page.getByRole('cell', {name: objectEntry.externalReferenceCode})
		).toBeVisible();

		await importReportPage.goToEntryDetails(
			objectEntry.externalReferenceCode
		);

		await expect(
			page.getByText('No value was provided for required object field')
		).toBeVisible();

		await expect(page.getByText('ScopeAssetLibrary')).toBeVisible();

		await expect(page.getByText(`Site${space.name}`)).toBeVisible();
	}
);
