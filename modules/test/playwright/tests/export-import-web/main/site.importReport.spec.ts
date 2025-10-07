/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {
	ObjectDefinitionAPI,
	ObjectFieldAPI,
} from '@liferay/object-admin-rest-client-js';
import {expect, mergeTests} from '@playwright/test';

import {dataApiHelpersTest} from '../../../fixtures/dataApiHelpersTest';
import {featureFlagsTest} from '../../../fixtures/featureFlagsTest';
import {loginTest} from '../../../fixtures/loginTest';
import getRandomString from '../../../utils/getRandomString';
import {exportImportPagesTest} from './fixtures/exportImportPagesTest';
import {objectDefitionRequestData} from './utils/objectDefitionRequestData';

export const test = mergeTests(
	dataApiHelpersTest,
	exportImportPagesTest,
	featureFlagsTest({
		'LPD-35914': {enabled: true},
	}),
	loginTest()
);

test('Can see error report and details', async ({
	apiHelpers,
	exportImportPage,
}) => {
	const objectDefinitionAPIClient =
		await apiHelpers.buildRestClient(ObjectDefinitionAPI);

	const {body: objectDefinition} =
		await objectDefinitionAPIClient.postObjectDefinition(
			objectDefitionRequestData({
				scope: 'site',
			})
		);

	apiHelpers.data.push({id: objectDefinition.id, type: 'objectDefinition'});

	const objectEntry = await apiHelpers.objectEntry.postObjectEntry(
		{externalReferenceCode: '', name: 'test'},
		'c/tests/scopes/Guest'
	);

	await exportImportPage.goToExport();

	const exportName = `MyExport-${getRandomString()}`;

	await exportImportPage.export(exportName, 'Tests 1 Items');

	await expect(exportImportPage.taskSuccessLabel(exportName)).toBeVisible();

	const exportFilePath =
		await exportImportPage.downloadExportProcess(exportName);

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

	await exportImportPage.goToImport();

	await exportImportPage.import(exportFilePath);

	await exportImportPage.goToImportDetails(exportName);

	await expect(
		exportImportPage.page.getByRole('cell', {
			name: objectEntry.externalReferenceCode,
		})
	).toBeVisible();

	await exportImportPage.goToImportErrorDetails(
		objectEntry.externalReferenceCode
	);

	await expect(
		exportImportPage.page.getByText(
			'No value was provided for required object field'
		)
	).toBeVisible();

	await expect(
		exportImportPage.page.getByText(objectEntry.externalReferenceCode)
	).toBeVisible();
});
