/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {ObjectFieldAPI} from '@liferay/object-admin-rest-client-js';
import {expect, mergeTests} from '@playwright/test';

import {dataApiHelpersTest} from '../../../fixtures/dataApiHelpersTest';
import {featureFlagsTest} from '../../../fixtures/featureFlagsTest';
import {isolatedSiteTest} from '../../../fixtures/isolatedSiteTest';
import {loginTest} from '../../../fixtures/loginTest';
import getRandomString from '../../../utils/getRandomString';
import {normalizeRestPath} from '../../../utils/normalizeRestPath';
import {exportImportPagesTest} from './fixtures/exportImportPagesTest';
import {importReportPagesTest} from './fixtures/importReportPagesTest';

export const test = mergeTests(
	dataApiHelpersTest,
	exportImportPagesTest,
	featureFlagsTest({
		'LPD-57655': {enabled: true},
	}),
	importReportPagesTest,
	isolatedSiteTest,
	loginTest()
);

test(
	'Can see error report and details',
	{tag: '@LPD-99382'},
	async ({apiHelpers, exportImportPage, importReportPage, page, site}) => {
		test.setTimeout(120000);

		const objectDefinition =
			await apiHelpers.objectAdmin.postRandomObjectDefinition({
				scope: 'site',
				status: {code: 0},
			});

		apiHelpers.data.push({
			id: objectDefinition.id,
			type: 'objectDefinition',
		});

		const objectEntry = await apiHelpers.objectEntry.postObjectEntry(
			{},
			`${normalizeRestPath(objectDefinition.restContextPath)}/scopes/${site.name}`
		);

		await exportImportPage.goToExport(site.friendlyUrlPath);

		const name = `MyExport-${getRandomString()}`;

		await exportImportPage.export(name);

		const folderPath = await exportImportPage.download(name);

		const objectFieldAPIClient =
			await apiHelpers.buildRestClient(ObjectFieldAPI);

		await objectFieldAPIClient.postObjectDefinitionObjectField(
			objectDefinition.id,
			{
				DBType: 'String',
				businessType: 'Text',
				label: {en_US: 'mandatoryField'},
				name: 'mandatoryField',
				required: true,
			}
		);

		await exportImportPage.goToImport(site.friendlyUrlPath);

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

		await expect(page.getByText('ScopeSite')).toBeVisible();

		await expect(page.getByText(`Site${site.name}`)).toBeVisible();
	}
);
