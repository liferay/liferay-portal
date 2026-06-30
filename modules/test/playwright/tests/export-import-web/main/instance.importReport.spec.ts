/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {
	ObjectFieldAPI,
	ObjectRelationshipAPI,
} from '@liferay/object-admin-rest-client-js';
import {expect, mergeTests} from '@playwright/test';

import {dataApiHelpersTest} from '../../../fixtures/dataApiHelpersTest';
import {featureFlagsTest} from '../../../fixtures/featureFlagsTest';
import {globalMenuPagesTest} from '../../../fixtures/globalMenuPagesTest';
import {loginTest} from '../../../fixtures/loginTest';
import {DataApiHelpers} from '../../../helpers/ApiHelpers';
import {clickAndExpectToBeVisible} from '../../../utils/clickAndExpectToBeVisible';
import {getRandomInt} from '../../../utils/getRandomInt';
import {normalizeRestPath} from '../../../utils/normalizeRestPath';
import {companyExportImportPageTest} from './fixtures/companyExportImportPagesTest';
import {exportImportPagesTest} from './fixtures/exportImportPagesTest';

export const test = mergeTests(
	dataApiHelpersTest,
	exportImportPagesTest,
	companyExportImportPageTest,
	featureFlagsTest({
		'LPD-57655': {enabled: false},
	}),
	globalMenuPagesTest,
	loginTest()
);

async function setupImportReportScenario(apiHelpers: DataApiHelpers) {
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
		`${normalizeRestPath(objectDefinition1.restContextPath)}/scopes/Guest`
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
		`${normalizeRestPath(objectDefinition2.restContextPath)}/scopes/Guest`
	);

	return {
		objectDefinition1,
		objectDefinition2,
		objectDefinition3,
		objectEntry,
	};
}

test(
	'Can filter, search and sort errors report entries',
	{tag: '@LPD-68461'},
	async ({apiHelpers, exportImportPage}) => {
		const {
			objectDefinition1,
			objectDefinition2,
			objectDefinition3,
			objectEntry,
		} = await setupImportReportScenario(apiHelpers);

		await exportImportPage.goToExport();

		const exportFilePath = await exportImportPage.export({
			includePageSettings: false,
			portletLabels: [
				`${objectDefinition1.name} 1 Items`,
				`${objectDefinition2.name} 1 Items`,
			],
		});

		const objectFieldAPIClient =
			await apiHelpers.buildRestClient(ObjectFieldAPI);

		await objectFieldAPIClient.postObjectDefinitionObjectField(
			objectDefinition1.id,
			{
				DBType: 'String',
				businessType: 'Text',
				label: {en_US: 'mandatoryField'},
				name: 'mandatoryField',
				required: true,
			}
		);

		await apiHelpers.objectEntry.deleteObjectEntry(
			normalizeRestPath(objectDefinition3.restContextPath),
			String(objectEntry.id)
		);

		const site = await apiHelpers.headlessAdminSite.postSite({
			name: 'TestSite' + getRandomInt(),
		});

		await exportImportPage.goToImport(site.friendlyUrlPath);

		await exportImportPage.import({
			filePath: exportFilePath,
			taskStatus: 'completedWithErrors',
		});

		const exportName = exportFilePath.slice(
			exportFilePath.lastIndexOf('/') + 1
		);

		await exportImportPage.goToImportDetails(exportName);

		// Sort by Entity Type

		await exportImportPage.sortReportBy('Entity Type');
		let values =
			await exportImportPage.getReportColumnValues('Entity Type');
		expect(values).toEqual([...values].sort((a, b) => a.localeCompare(b)));

		await exportImportPage.sortReportBy('Entity Type');
		values = await exportImportPage.getReportColumnValues('Entity Type');
		expect(values).toEqual([...values].sort((a, b) => b.localeCompare(a)));

		// Sort by External Reference Code

		await exportImportPage.sortReportBy('External Reference Code');
		values = await exportImportPage.getReportColumnValues(
			'External Reference Code'
		);
		expect(values).toEqual([...values].sort((a, b) => a.localeCompare(b)));

		await exportImportPage.sortReportBy('External Reference Code');
		values = await exportImportPage.getReportColumnValues(
			'External Reference Code'
		);
		expect(values).toEqual([...values].sort((a, b) => b.localeCompare(a)));

		// Search by Entity Type name

		await exportImportPage.searchReportEntries(objectDefinition1.name);
		values = await exportImportPage.getReportColumnValues('Entity Type');
		expect(values).toEqual([objectDefinition1.name]);

		await exportImportPage.clearReportSearch();

		// Search by External Reference Code

		await exportImportPage.searchReportEntries(
			objectEntry.externalReferenceCode
		);
		values = await exportImportPage.getReportColumnValues(
			'External Reference Code'
		);
		expect(values).toEqual([objectEntry.externalReferenceCode]);

		await exportImportPage.clearReportSearch();

		// Filter by Entity Type

		await exportImportPage.filterReportBy(
			'Entity Type',
			objectDefinition1.name
		);
		values = await exportImportPage.getReportColumnValues('Entity Type');
		expect(values).toEqual([objectDefinition1.name]);

		await exportImportPage.removeReportFilter();

		// Filter by External Reference Code

		await exportImportPage.filterReportBy(
			'External Reference Code',
			objectEntry.externalReferenceCode
		);
		values = await exportImportPage.getReportColumnValues(
			'External Reference Code'
		);
		expect(values).toEqual([objectEntry.externalReferenceCode]);

		await exportImportPage.removeReportFilter();

		// Filter by Type

		await exportImportPage.filterReportBy('Type', 'Empty');
		values = await exportImportPage.getReportColumnValues('Type');
		expect(values).toEqual(['Empty']);

		await exportImportPage.excludeReportFilter();
		values = await exportImportPage.getReportColumnValues('Type');
		expect(values).toEqual(['Error']);

		await exportImportPage.removeReportFilter();
	}
);

test('Can see error report and details', async ({
	apiHelpers,
	companyExportImportPage,
	exportImportPage,
	globalMenuPage,
}) => {
	const {objectDefinition3, objectEntry} =
		await setupImportReportScenario(apiHelpers);

	await globalMenuPage.goToApplications('Export');

	const exportFilePath = await exportImportPage.export({
		portletLabels: [`${objectDefinition3.name} 1 Items`],
	});

	const objectFieldAPIClient =
		await apiHelpers.buildRestClient(ObjectFieldAPI);

	await objectFieldAPIClient.postObjectDefinitionObjectField(
		objectDefinition3.id,
		{
			DBType: 'String',
			businessType: 'Text',
			label: {en_US: 'mandatoryField'},
			name: 'mandatoryField',
			required: true,
		}
	);

	await companyExportImportPage.import({
		filePath: exportFilePath,
		includePermissions: true,
		taskStatus: 'completedWithErrors',
	});

	const exportName = exportFilePath.slice(
		exportFilePath.lastIndexOf('/') + 1
	);

	await clickAndExpectToBeVisible({
		target: exportImportPage.clearMenuItem,
		trigger: exportImportPage.taskActionsMenu(exportName),
	});

	await expect(exportImportPage.viewReportEntriesMenuItem).toBeVisible();

	await expect(exportImportPage.exportReportEntriesMenuItem).toBeVisible();

	await exportImportPage.goToImportDetails(exportName);

	await expect(
		exportImportPage.page.getByRole('cell', {
			name: objectEntry.externalReferenceCode,
		})
	).toBeVisible();

	await exportImportPage.goToImportReportEntryDetails(
		objectEntry.externalReferenceCode
	);

	await expect(
		exportImportPage.page.getByText(
			'No value was provided for required object field'
		)
	).toBeVisible();

	await expect(exportImportPage.page.getByText('ScopeCompany')).toBeVisible();

	await expect(
		exportImportPage.page.getByText('SiteLiferay DXP')
	).not.toBeVisible();

	await expect(
		exportImportPage.page.getByText(objectEntry.externalReferenceCode)
	).toBeVisible();
});

test('Report entries actions are not visible for a successful import', async ({
	apiHelpers,
	exportImportPage,
}) => {
	const {objectDefinition1} = await setupImportReportScenario(apiHelpers);

	await exportImportPage.goToExport();

	const exportFilePath = await exportImportPage.export({
		portletLabels: [`${objectDefinition1.name} 1 Items`],
	});

	await exportImportPage.goToImport();

	await exportImportPage.import({
		filePath: exportFilePath,
	});

	const exportName = exportFilePath.slice(
		exportFilePath.lastIndexOf('/') + 1
	);

	await clickAndExpectToBeVisible({
		target: exportImportPage.clearMenuItem,
		trigger: exportImportPage.taskActionsMenu(exportName),
	});

	await expect(exportImportPage.viewReportEntriesMenuItem).not.toBeVisible();

	await expect(
		exportImportPage.exportReportEntriesMenuItem
	).not.toBeVisible();
});
