/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {ObjectRelationshipAPI} from '@liferay/object-admin-rest-client-js';
import {expect, mergeTests} from '@playwright/test';

import {dataApiHelpersTest} from '../../../fixtures/dataApiHelpersTest';
import {featureFlagsTest} from '../../../fixtures/featureFlagsTest';
import {globalMenuPagesTest} from '../../../fixtures/globalMenuPagesTest';
import {isolatedSiteTest} from '../../../fixtures/isolatedSiteTest';
import {loginTest} from '../../../fixtures/loginTest';
import {DataApiHelpers} from '../../../helpers/ApiHelpers';
import {clickAndExpectToBeVisible} from '../../../utils/clickAndExpectToBeVisible';
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
	globalMenuPagesTest,
	importReportPagesTest,
	isolatedSiteTest,
	loginTest()
);

async function setupImportReportScenario(
	apiHelpers: DataApiHelpers,
	siteName: string
) {
	const [objectDefinition1, objectDefinition2] = await Promise.all([
		apiHelpers.objectAdmin.postRandomObjectDefinition({
			scope: 'site',
			status: {code: 0},
		}),
		apiHelpers.objectAdmin.postRandomObjectDefinition({
			scope: 'site',
			status: {code: 0},
		}),
	]);

	const objectDefinition3 =
		await apiHelpers.objectAdmin.postRandomObjectDefinition({
			status: {code: 0},
		});

	for (const objectDefinition of [
		objectDefinition1,
		objectDefinition2,
		objectDefinition3,
	]) {
		apiHelpers.data.push({
			id: objectDefinition.id,
			type: 'objectDefinition',
		});
	}

	const objectRelationshipAPIClient = await apiHelpers.buildRestClient(
		ObjectRelationshipAPI
	);

	const relationshipName = 'relationship';

	await objectRelationshipAPIClient.postObjectDefinitionByExternalReferenceCodeObjectRelationship(
		objectDefinition3.externalReferenceCode,
		{
			deletionType: 'disassociate',
			label: {en_US: 'Relationship'},
			name: relationshipName,
			objectDefinitionExternalReferenceCode1:
				objectDefinition3.externalReferenceCode,
			objectDefinitionExternalReferenceCode2:
				objectDefinition2.externalReferenceCode,
			objectDefinitionId1: objectDefinition3.id,
			objectDefinitionId2: objectDefinition2.id,
			objectDefinitionName2: objectDefinition2.name,
			type: 'oneToMany',
		}
	);

	await apiHelpers.objectEntry.postObjectEntry(
		{},
		`${normalizeRestPath(objectDefinition1.restContextPath)}/scopes/${siteName}`
	);

	const objectEntry = await apiHelpers.objectEntry.postObjectEntry(
		{},
		normalizeRestPath(objectDefinition3.restContextPath)
	);

	await apiHelpers.objectEntry.postObjectEntry(
		{
			[`r_${relationshipName}_c_${objectDefinition3.name[0].toLowerCase() + objectDefinition3.name.substring(1)}Id`]:
				objectEntry.id,
		},
		`${normalizeRestPath(objectDefinition2.restContextPath)}/scopes/${siteName}`
	);

	return {
		objectDefinition1,
		objectDefinition2,
		objectDefinition3,
		objectEntry,
	};
}

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

		await forceRequiredFieldImportError(apiHelpers, objectDefinition.id);

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

test(
	'Can filter, search and sort errors report entries',
	{tag: '@LPD-100543'},
	async ({apiHelpers, exportImportPage, site}) => {
		test.setTimeout(120000);

		const {objectDefinition1, objectDefinition3, objectEntry} =
			await setupImportReportScenario(apiHelpers, site.name);

		await exportImportPage.goToExport(site.friendlyUrlPath);

		const name = `MyExport-${getRandomString()}`;

		await exportImportPage.export(name);

		const folderPath = await exportImportPage.download(name);

		await forceRequiredFieldImportError(apiHelpers, objectDefinition1.id);

		await apiHelpers.objectEntry.deleteObjectEntry(
			normalizeRestPath(objectDefinition3.restContextPath),
			String(objectEntry.id)
		);

		await exportImportPage.goToImport(site.friendlyUrlPath);

		await exportImportPage.newButton.click();

		await exportImportPage.import({
			folderPath,
			name,
			taskStatus: 'completedWithErrors',
		});

		await exportImportPage.goToImportDetails(name);

		// Sort by Entity Type

		await exportImportPage.sortBy('Entity Type');
		let values = await exportImportPage.getColumnValues('Entity Type');
		expect(values).toEqual([...values].sort((a, b) => a.localeCompare(b)));

		await exportImportPage.sortBy('Entity Type');
		values = await exportImportPage.getColumnValues('Entity Type');
		expect(values).toEqual([...values].sort((a, b) => b.localeCompare(a)));

		// Sort by External Reference Code

		await exportImportPage.sortBy('External Reference Code');
		values = await exportImportPage.getColumnValues(
			'External Reference Code'
		);
		expect(values).toEqual([...values].sort((a, b) => a.localeCompare(b)));

		await exportImportPage.sortBy('External Reference Code');
		values = await exportImportPage.getColumnValues(
			'External Reference Code'
		);
		expect(values).toEqual([...values].sort((a, b) => b.localeCompare(a)));

		// Search by Entity Type name

		await exportImportPage.searchReportEntries(objectDefinition1.name);
		values = await exportImportPage.getColumnValues('Entity Type');
		expect(values).toEqual([objectDefinition1.name]);

		await exportImportPage.clearReportSearch();

		// Search by External Reference Code

		await exportImportPage.searchReportEntries(
			objectEntry.externalReferenceCode
		);
		values = await exportImportPage.getColumnValues(
			'External Reference Code'
		);
		expect(values).toEqual([objectEntry.externalReferenceCode]);

		await exportImportPage.clearReportSearch();

		// Filter by Entity Type

		await exportImportPage.filterReportBy(
			'Entity Type',
			objectDefinition1.name
		);
		values = await exportImportPage.getColumnValues('Entity Type');
		expect(values).toEqual([objectDefinition1.name]);

		await exportImportPage.removeReportFilter();

		// Filter by External Reference Code

		await exportImportPage.filterReportBy(
			'External Reference Code',
			objectEntry.externalReferenceCode
		);
		values = await exportImportPage.getColumnValues(
			'External Reference Code'
		);
		expect(values).toEqual([objectEntry.externalReferenceCode]);

		await exportImportPage.removeReportFilter();

		// Filter by Type

		await exportImportPage.filterReportBy('Type', 'Empty');
		values = await exportImportPage.getColumnValues('Type');
		expect(values).toEqual(['Empty']);

		await exportImportPage.excludeReportFilter();
		values = await exportImportPage.getColumnValues('Type');
		expect(values).toEqual(['Error']);

		await exportImportPage.removeReportFilter();
	}
);
