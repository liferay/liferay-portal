/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {dataApiHelpersTest} from '../../../fixtures/dataApiHelpersTest';
import {featureFlagsTest} from '../../../fixtures/featureFlagsTest';
import {isolatedSiteTest} from '../../../fixtures/isolatedSiteTest';
import {loginTest} from '../../../fixtures/loginTest';
import {checkAccessibility} from '../../../utils/checkAccessibility';
import getRandomString from '../../../utils/getRandomString';
import {normalizeRestPath} from '../../../utils/normalizeRestPath';
import {getTempFile} from '../../../utils/temp';
import {readFileFromZip} from '../../../utils/zip';
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

		await forceRequiredFieldImportError(apiHelpers, objectDefinition.id);

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

test(
	'Can download export report entries CSV',
	{tag: ['@LPD-65208', '@LPD-99982']},
	async ({apiHelpers, exportImportPage, page, site}) => {
		test.setTimeout(120000);

		const name = `MyExport-${getRandomString()}`;

		await test.step('Setup', async () => {
			const objectDefinition =
				await apiHelpers.objectAdmin.postRandomObjectDefinition({
					scope: 'site',
					status: {code: 0},
				});

			apiHelpers.data.push({
				id: objectDefinition.id,
				type: 'objectDefinition',
			});

			await apiHelpers.objectEntry.postObjectEntry(
				{},
				`${normalizeRestPath(objectDefinition.restContextPath)}/scopes/${site.name}`
			);

			await exportImportPage.goToExport(site.friendlyUrlPath);

			await exportImportPage.export(name);

			const folderPath = await exportImportPage.download(name);

			await forceRequiredFieldImportError(
				apiHelpers,
				objectDefinition.id
			);

			await exportImportPage.goToImport(site.friendlyUrlPath);

			await exportImportPage.newButton.click();

			await exportImportPage.import({
				folderPath,
				name,
				taskStatus: 'completedWithErrors',
			});
		});

		await test.step('Open Export Report Entries modal', async () => {
			await exportImportPage.openExportReportEntriesModal(name);

			await page.keyboard.press('Escape');

			await expect(
				exportImportPage.exportReportEntriesModal
			).toBeVisible();

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
				new RegExp(`^${name}_report_entries\\.zip$`)
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
