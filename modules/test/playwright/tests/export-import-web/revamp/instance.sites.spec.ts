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
import {normalizeRestPath} from '../../../utils/normalizeRestPath';
import {exportImportPagesTest} from './fixtures/exportImportPagesTest';
import {exportAndDownloadLar} from './utils/exportAndDownloadLar';

export const test = mergeTests(
	dataApiHelpersTest,
	exportImportPagesTest,
	featureFlagsTest({
		'LPD-57655': {enabled: true},
		'LPD-85946': {enabled: true},
	}),
	globalMenuPagesTest,
	loginTest()
);

test(
	'Can import a site with object entries from an instance export',
	{tag: '@LPD-101408'},
	async ({
		apiHelpers,
		exportImportDataSelectionPage,
		exportImportPage,
		globalMenuPage,
		page,
	}) => {
		const group = await apiHelpers.headlessAdminSite.postSite({
			name: getRandomString(),
		});

		const objectDefinition =
			await apiHelpers.objectAdmin.postRandomObjectDefinition({
				status: {code: 0},
			});

		apiHelpers.data.push({
			id: objectDefinition.id,
			type: 'objectDefinition',
		});

		const applicationName = normalizeRestPath(
			objectDefinition.restContextPath
		);

		const objectEntry = await apiHelpers.objectEntry.postObjectEntry(
			{externalReferenceCode: '', textField: getRandomString()},
			applicationName
		);

		const lar = await test.step('Export the site', async () => {
			await globalMenuPage.goToApplications('Export');

			await exportImportPage.clickNew();

			await exportImportDataSelectionPage.selectOnlyObjectDefinition(
				objectDefinition.name
			);

			await exportImportDataSelectionPage.selectGroup(group.name);

			return await exportAndDownloadLar(exportImportPage);
		});

		await apiHelpers.objectEntry.deleteObjectEntry(
			applicationName,
			String(objectEntry.id)
		);

		await test.step('Import the site', async () => {
			await globalMenuPage.goToApplications('Import');

			await exportImportPage.clickNew();

			await exportImportPage.import({
				folderPath: lar.folderPath,
				name: `MyImport-${getRandomString()}`,
				selectData: async () => {
					await expect(
						page.getByText('No sites are selected.')
					).toBeVisible();

					await exportImportDataSelectionPage.selectGroup(group.name);
				},
			});
		});

		expect(
			await apiHelpers.objectEntry.getObjectEntryByExternalReferenceCode({
				applicationName,
				externalReferenceCode: objectEntry.externalReferenceCode,
			})
		).toEqual(
			expect.objectContaining({
				externalReferenceCode: objectEntry.externalReferenceCode,
				textField: objectEntry.textField,
			})
		);
	}
);
