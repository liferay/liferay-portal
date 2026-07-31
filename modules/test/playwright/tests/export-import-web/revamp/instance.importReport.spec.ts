/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {ObjectFieldAPI} from '@liferay/object-admin-rest-client-js';
import {expect, mergeTests} from '@playwright/test';

import {dataApiHelpersTest} from '../../../fixtures/dataApiHelpersTest';
import {featureFlagsTest} from '../../../fixtures/featureFlagsTest';
import {globalMenuPagesTest} from '../../../fixtures/globalMenuPagesTest';
import {loginTest} from '../../../fixtures/loginTest';
import {clickAndExpectToBeVisible} from '../../../utils/clickAndExpectToBeVisible';
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
	globalMenuPagesTest,
	importReportPagesTest,
	loginTest()
);

test(
	'Can see error report and details',
	{tag: ['@LPD-99382', '@LPD-99982']},
	async ({
		apiHelpers,
		exportImportDataSelectionPage,
		exportImportPage,
		globalMenuPage,
		importReportPage,
		page,
	}) => {
		test.setTimeout(120000);

		const objectDefinition =
			await apiHelpers.objectAdmin.postRandomObjectDefinition({
				status: {code: 0},
			});

		apiHelpers.data.push({
			id: objectDefinition.id,
			type: 'objectDefinition',
		});

		const objectEntry = await apiHelpers.objectEntry.postObjectEntry(
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

		await globalMenuPage.goToApplications('Import');

		await exportImportPage.newButton.click();

		await exportImportPage.import({
			folderPath,
			name,
			taskStatus: 'completedWithErrors',
		});

		await clickAndExpectToBeVisible({
			target: exportImportPage.viewReportEntriesMenuItem,
			trigger: exportImportPage.actionsButton(name),
		});

		await expect(
			exportImportPage.exportReportEntriesMenuItem
		).toBeVisible();

		await exportImportPage.viewReportEntriesMenuItem.click();

		await expect(
			page.getByRole('cell', {name: objectEntry.externalReferenceCode})
		).toBeVisible();

		await importReportPage.goToEntryDetails(
			objectEntry.externalReferenceCode
		);

		await expect(
			page.getByText('No value was provided for required object field')
		).toBeVisible();

		await expect(page.getByText('ScopeCompany')).toBeVisible();

		await expect(page.getByText('SiteLiferay DXP')).not.toBeVisible();
	}
);

test(
	'Report entries actions are not visible for a successful import',
	{tag: ['@LPD-99382', '@LPD-99982']},
	async ({
		apiHelpers,
		exportImportDataSelectionPage,
		exportImportPage,
		globalMenuPage,
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

		await globalMenuPage.goToApplications('Import');

		await exportImportPage.newButton.click();

		await exportImportPage.import({folderPath, name});

		await clickAndExpectToBeVisible({
			target: exportImportPage.clearMenuItem,
			trigger: exportImportPage.actionsButton(name),
		});

		await expect(
			exportImportPage.exportReportEntriesMenuItem
		).not.toBeVisible();

		await expect(
			exportImportPage.viewReportEntriesMenuItem
		).not.toBeVisible();
	}
);
