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
import {checkAccessibility} from '../../../utils/checkAccessibility';
import getRandomString from '../../../utils/getRandomString';
import {getTempFile} from '../../../utils/temp';
import {readFileFromZip} from '../../../utils/zip';
import {exportImportPagesTest} from './fixtures/exportImportPagesTest';
import {objectDefitionRequestData} from './utils/objectDefitionRequestData';

export const test = mergeTests(
	dataApiHelpersTest,
	exportImportPagesTest,
	featureFlagsTest({
		'LPD-57655': {enabled: false},
	}),
	loginTest()
);

test('Can see error report and details', async ({
	apiHelpers,
	exportImportPage,
	page,
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

	const taskName = `MyExport-${getRandomString()}`;

	const exportFilePath = await exportImportPage.export({
		portletLabels: ['Tests 1 Items'],
		taskName,
	});

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

	await exportImportPage.import({
		filePath: exportFilePath,
		taskStatus: 'completedWithErrors',
	});

	await exportImportPage.goToImportDetails(taskName);

	await expect(
		page.getByRole('cell', {
			name: objectEntry.externalReferenceCode,
		})
	).toBeVisible();

	await exportImportPage.goToImportReportEntryDetails(
		objectEntry.externalReferenceCode
	);

	await expect(
		page.getByText('No value was provided for required object field')
	).toBeVisible();

	await expect(exportImportPage.page.getByText('ScopeSite')).toBeVisible();

	await expect(
		exportImportPage.page.getByText('SiteLiferay DXP')
	).toBeVisible();

	await expect(
		page.getByText(objectEntry.externalReferenceCode)
	).toBeVisible();
});

test(
	'Can download export report entries CSV',
	{tag: '@LPD-65208'},
	async ({apiHelpers, exportImportPage, page}) => {
		const taskName = `MyExport-${getRandomString()}`;

		await test.step('Setup', async () => {
			const objectDefinitionAPIClient =
				await apiHelpers.buildRestClient(ObjectDefinitionAPI);

			const {body: objectDefinition} =
				await objectDefinitionAPIClient.postObjectDefinition(
					objectDefitionRequestData({
						scope: 'site',
					})
				);

			apiHelpers.data.push({
				id: objectDefinition.id,
				type: 'objectDefinition',
			});

			await apiHelpers.objectEntry.postObjectEntry(
				{externalReferenceCode: '', name: 'test'},
				'c/tests/scopes/Guest'
			);

			await exportImportPage.goToExport();

			const exportFilePath = await exportImportPage.export({
				portletLabels: ['Tests 1 Items'],
				taskName,
			});

			// Add a mandatory field to Object Definition to generate report issues on import

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

			await exportImportPage.import({
				filePath: exportFilePath,
				taskStatus: 'completedWithErrors',
			});
		});

		await test.step('Open Export Report Entries modal', async () => {
			await exportImportPage.openExportReportEntriesModal(taskName);

			await checkAccessibility({
				page,
				selectors: ['.modal'],
				selectorsToExclude: ['[role="progressbar"]'],
			});

			await expect(
				exportImportPage.exportReportEntriesModalProgressbar
			).toHaveAttribute('aria-valuenow', '100');
		});

		await test.step('Download report entries ZIP', async () => {
			const downloadPromise = page.waitForEvent('download');
			await exportImportPage.exportReportEntriesModalDownloadButton.click();

			const download = await downloadPromise;
			const suggestedFilename = download.suggestedFilename();

			expect(suggestedFilename).toMatch(
				new RegExp(`^${taskName}_report_entries\\.zip$`)
			);

			const filePath = getTempFile(suggestedFilename);
			await download.saveAs(filePath);

			const content = await readFileFromZip('export.csv', filePath);

			await expect(content).toMatch(
				/^classExternalReferenceCode,errorMessage,modelName,status.code,status.extendedProperties,status.label,status.xClassName,type.code,type.extendedProperties,type.label,type.xClassName\r?\n/
			);

			await expect(content).toContain(
				'"No value was provided for required object field ""mandatoryField"""'
			);
		});
	}
);
