/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {dataApiHelpersTest} from '../../../fixtures/dataApiHelpersTest';
import {featureFlagsTest} from '../../../fixtures/featureFlagsTest';
import {globalMenuPagesTest} from '../../../fixtures/globalMenuPagesTest';
import {isolatedSiteTest} from '../../../fixtures/isolatedSiteTest';
import {loginTest} from '../../../fixtures/loginTest';
import getRandomString from '../../../utils/getRandomString';
import {normalizeRestPath} from '../../../utils/normalizeRestPath';
import {exportImportPagesTest} from './fixtures/exportImportPagesTest';

export const test = mergeTests(
	dataApiHelpersTest,
	exportImportPagesTest,
	featureFlagsTest({
		'LPD-57655': {enabled: true},
	}),
	globalMenuPagesTest,
	isolatedSiteTest,
	loginTest()
);

test(
	'Cannot import an instance scoped lar file',
	{tag: '@LPD-99382'},
	async ({
		apiHelpers,
		exportImportDataSelectionPage,
		exportImportPage,
		globalMenuPage,
		site,
	}) => {
		const objectDefinition =
			await apiHelpers.objectAdmin.postRandomObjectDefinition({
				status: {code: 0},
			});

		apiHelpers.data.push({
			id: objectDefinition.id,
			type: 'objectDefinition',
		});

		await apiHelpers.objectEntry.postObjectEntry(
			{},
			normalizeRestPath(objectDefinition.restContextPath)
		);

		await globalMenuPage.goToApplications('Export');

		const name = `MyExport-${getRandomString()}`;

		await exportImportPage.clickNew();

		await exportImportDataSelectionPage.selectOnlyObjectDefinition(
			objectDefinition.name
		);

		await exportImportPage.nameInput.fill(name);

		await exportImportPage.exportButton.click();

		await expect(exportImportPage.taskStatusLabel(name)).toBeVisible();

		const folderPath = await exportImportPage.download(name);

		await exportImportPage.goToImport(site.friendlyUrlPath);

		await exportImportPage.newButton.click();

		await exportImportPage.expectUploadError(
			folderPath,
			'The LAR file contains one or more entities with a different scope.'
		);
	}
);
