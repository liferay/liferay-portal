/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {
	ObjectActionAPI,
	ObjectDefinitionAPI,
	ObjectField,
	ObjectRelationshipAPI,
} from '@liferay/object-admin-rest-client-js/v1_0';
import {expect, mergeTests} from '@playwright/test';

import {dataApiHelpersTest} from '../../../fixtures/dataApiHelpersTest';
import {isolatedSiteTest} from '../../../fixtures/isolatedSiteTest';
import {loginTest} from '../../../fixtures/loginTest';
import {objectPagesTest} from '../../../fixtures/objectPagesTest';
import {liferayConfig} from '../../../liferay.config';
import {getRandomInt} from '../../../utils/getRandomInt';
import {waitForAlert} from '../../../utils/waitForAlert';
import {getFilePath} from '../utils/fileHelpers';
import {generateObjectEntryValues} from '../utils/generateObjectEntry';
import {generateObjectFields} from '../utils/generateObjectFields';

const test = mergeTests(
	dataApiHelpersTest,
	isolatedSiteTest,
	loginTest(),
	objectPagesTest
);

async function deleteObjectDefinitionFromData(apiHelpers, objectDefinitionId) {
	const objectDefinitionAPIClient =
		await apiHelpers.buildRestClient(ObjectDefinitionAPI);

	const dataIndex = apiHelpers.data.findIndex(
		(item) =>
			item.id === objectDefinitionId && item.type === 'objectDefinition'
	);

	if (dataIndex !== -1) {
		apiHelpers.data.splice(dataIndex, 1);
	}

	await objectDefinitionAPIClient.deleteObjectDefinition(objectDefinitionId);
}

test.describe('Manage export/import object definitions through UI', () => {
	test('Can cancel importing an object definition', async ({
		page,
		viewObjectDefinitionsPage,
	}) => {
		await viewObjectDefinitionsPage.goto();

		await viewObjectDefinitionsPage.objectFolderActions.click();

		await viewObjectDefinitionsPage.importObjectDefinitionOption.click();

		await page.getByLabel('Name').fill('CancelledImport' + getRandomInt());

		await viewObjectDefinitionsPage.hiddenFileInput.setInputFiles(
			getFilePath('ImportedSimpleObject.json')
		);

		await page.getByRole('button', {name: 'Cancel'}).click();

		await expect(page.locator('.modal-body')).toBeHidden();

		await viewObjectDefinitionsPage.goto();

		const importedObjectName = 'Imported Simple Object';

		await viewObjectDefinitionsPage.searchInput.fill(importedObjectName);

		await page.keyboard.press('Enter');

		await expect(
			page.getByRole('link', {
				exact: true,
				name: importedObjectName,
			})
		).toBeHidden();
	});

	test('Can clear the JSON file on the import dialog', async ({
		page,
		viewObjectDefinitionsPage,
	}) => {
		await viewObjectDefinitionsPage.goto();

		await viewObjectDefinitionsPage.objectFolderActions.click();

		await viewObjectDefinitionsPage.importObjectDefinitionOption.click();

		await viewObjectDefinitionsPage.hiddenFileInput.setInputFiles(
			getFilePath('ImportedSimpleObject.json')
		);

		await page.getByRole('button', {name: 'Clear'}).click();

		await expect(
			page.getByText('ImportedSimpleObject.json', {
				exact: true,
			})
		).not.toBeVisible();
	});

	test('can export data structure from a custom object', async ({
		apiHelpers,
		page,
		viewObjectDefinitionsPage,
	}) => {
		const objectDefinition =
			await apiHelpers.objectAdmin.postRandomObjectDefinition({
				status: {code: 0},
			});

		apiHelpers.data.push({
			id: objectDefinition.id,
			type: 'objectDefinition',
		});

		await viewObjectDefinitionsPage.goto();

		const downloadPromise = page.waitForEvent('download');

		const row = page.getByRole('row', {
			name: objectDefinition.label['en_US'],
		});

		await row.getByRole('button', {name: 'Actions'}).click();

		await viewObjectDefinitionsPage.exportObjectDefinitionOption.click();

		const download = await downloadPromise;

		expect(download.suggestedFilename()).toContain(
			objectDefinition.externalReferenceCode
		);
	});

	test('Can export and import an object definition with Actions', async ({
		apiHelpers,
		page,
		viewObjectDefinitionsPage,
	}) => {
		const objectDefinition =
			await apiHelpers.objectAdmin.postRandomObjectDefinition({
				status: {code: 0},
			});

		apiHelpers.data.push({
			id: objectDefinition.id,
			type: 'objectDefinition',
		});

		const actionLabel = 'ImportedAction' + getRandomInt();

		const objectActionAPIClient =
			await apiHelpers.buildRestClient(ObjectActionAPI);

		await objectActionAPIClient.postObjectDefinitionByExternalReferenceCodeObjectAction(
			objectDefinition.externalReferenceCode,
			{
				active: true,
				label: {
					en_US: actionLabel,
				},
				name: 'actionName' + getRandomInt(),
				objectActionExecutorKey: 'webhook',
				objectActionTriggerKey: 'onAfterAdd',
				parameters: {
					secret: '',
					url: liferayConfig.environment.baseUrl,
				},
			}
		);

		const {filePath, jsonContent} =
			await viewObjectDefinitionsPage.exportObjectDefinition(
				objectDefinition.label['en_US']
			);

		expect(jsonContent).toBeTruthy();
		expect(jsonContent.objectActions).toBeTruthy();
		expect(jsonContent.objectActions.length).toBeGreaterThanOrEqual(1);

		await deleteObjectDefinitionFromData(apiHelpers, objectDefinition.id);

		const importedObjectName = 'ImportedWithAction' + getRandomInt();

		const importedObjectId =
			await viewObjectDefinitionsPage.importObjectDefinition(
				filePath,
				importedObjectName
			);

		if (importedObjectId) {
			apiHelpers.data.push({
				id: importedObjectId,
				type: 'objectDefinition',
			});
		}

		await viewObjectDefinitionsPage.clickEditObjectDefinitionLink(
			objectDefinition.label['en_US']
		);

		await page.getByRole('link', {name: 'Actions'}).click();

		await expect(
			page.getByRole('cell').getByText(actionLabel, {exact: true})
		).toBeVisible();

		await expect(page.getByText('Yes')).toBeVisible();
	});

	test('Can export an object definition with Aggregation field', async ({
		apiHelpers,
		objectFieldsPage,
		viewObjectDefinitionsPage,
	}) => {
		const objectDefinition =
			await apiHelpers.objectAdmin.postRandomObjectDefinition({
				status: {code: 2},
			});

		apiHelpers.data.push({
			id: objectDefinition.id,
			type: 'objectDefinition',
		});

		const objectRelationshipAPIClient = await apiHelpers.buildRestClient(
			ObjectRelationshipAPI
		);

		const relationshipName = 'relationship' + getRandomInt();

		await objectRelationshipAPIClient.postObjectDefinitionByExternalReferenceCodeObjectRelationship(
			objectDefinition.externalReferenceCode,
			{
				label: {
					en_US: 'Relationship',
				},
				name: relationshipName,
				objectDefinitionExternalReferenceCode1:
					objectDefinition.externalReferenceCode,
				objectDefinitionExternalReferenceCode2:
					objectDefinition.externalReferenceCode,
				objectDefinitionId1: objectDefinition.id,
				objectDefinitionId2: objectDefinition.id,
				objectDefinitionName2: objectDefinition.name,
				type: 'oneToMany',
			}
		);

		const objectDefinitionAPIClient =
			await apiHelpers.buildRestClient(ObjectDefinitionAPI);

		await objectDefinitionAPIClient.postObjectDefinitionPublish(
			objectDefinition.id
		);

		await objectFieldsPage.goto(objectDefinition.label['en_US']);

		await objectFieldsPage.addObjectField({
			aggregationField: 'ID',
			aggregationFieldFunction: 'Max',
			aggregationFieldRelationship: 'Relationship',
			objectFieldBusinessType: 'Aggregation',
			objectFieldLabel: 'Custom Aggregation',
		});

		const {jsonContent} =
			await viewObjectDefinitionsPage.exportObjectDefinition(
				objectDefinition.label['en_US']
			);

		expect(jsonContent).toBeTruthy();

		const aggregationField = jsonContent.objectFields?.find(
			(field) => field.businessType === 'Aggregation'
		);

		expect(aggregationField).toBeTruthy();
	});

	test('Can import and export Custom Views structure', async ({
		apiHelpers,
		page,
		viewObjectDefinitionsPage,
	}) => {
		const importedObjectName = 'Imported Object With Custom Views';

		const importedObjectId =
			await viewObjectDefinitionsPage.importObjectDefinition(
				getFilePath('ImportedObjectWithCustomViews.json'),
				importedObjectName
			);

		if (importedObjectId) {
			apiHelpers.data.push({
				id: importedObjectId,
				type: 'objectDefinition',
			});
		}

		await viewObjectDefinitionsPage.goto();
		await viewObjectDefinitionsPage.searchInput.fill(importedObjectName);
		await page.keyboard.press('Enter');

		await expect(
			page.getByRole('link', {name: importedObjectName})
		).toBeVisible();

		const {jsonContent} =
			await viewObjectDefinitionsPage.exportObjectDefinition(
				importedObjectName
			);

		expect(jsonContent).toBeTruthy();
		expect(jsonContent.objectViews).toBeTruthy();
		expect(jsonContent.objectViews.length).toBeGreaterThanOrEqual(1);
	});

	test('Can import and export State Manager structure', async ({
		apiHelpers,
		page,
		viewObjectDefinitionsPage,
	}) => {
		const importedObjectName = 'Imported Object With State';

		const importedObjectId =
			await viewObjectDefinitionsPage.importObjectDefinition(
				getFilePath('ImportedObjectWithState.json'),
				importedObjectName
			);

		if (importedObjectId) {
			apiHelpers.data.push({
				id: importedObjectId,
				type: 'objectDefinition',
			});
		}

		await viewObjectDefinitionsPage.goto();
		await viewObjectDefinitionsPage.searchInput.fill(importedObjectName);
		await page.keyboard.press('Enter');

		await expect(
			page.getByRole('link', {name: importedObjectName})
		).toBeVisible();

		const {jsonContent} =
			await viewObjectDefinitionsPage.exportObjectDefinition(
				importedObjectName
			);

		expect(jsonContent).toBeTruthy();

		const stateManagerField = jsonContent.objectFields?.find(
			(field) => field.state === true
		);

		expect(stateManagerField).toBeTruthy();
	});

	test('Can import and export Validation structure', async ({
		apiHelpers,
		page,
		viewObjectDefinitionsPage,
	}) => {
		const importedObjectName = 'Imported Object With Validation';

		const importedObjectId =
			await viewObjectDefinitionsPage.importObjectDefinition(
				getFilePath('ImportedObjectWithValidation.json'),
				importedObjectName
			);

		if (importedObjectId) {
			apiHelpers.data.push({
				id: importedObjectId,
				type: 'objectDefinition',
			});
		}

		await viewObjectDefinitionsPage.goto();
		await viewObjectDefinitionsPage.searchInput.fill(importedObjectName);
		await page.keyboard.press('Enter');

		await expect(
			page.getByRole('link', {name: importedObjectName})
		).toBeVisible();

		const {jsonContent} =
			await viewObjectDefinitionsPage.exportObjectDefinition(
				importedObjectName
			);

		expect(jsonContent).toBeTruthy();
		expect(jsonContent.objectValidationRules).toBeTruthy();
		expect(jsonContent.objectValidationRules.length).toBeGreaterThanOrEqual(
			1
		);
	});

	test('Can import and export metadata fields', async ({
		apiHelpers,
		page,
		viewObjectDefinitionsPage,
	}) => {
		const objectFields = generateObjectFields({
			objectFieldBusinessTypes: ['Text'],
		});

		const objectDefinition =
			await apiHelpers.objectAdmin.postRandomObjectDefinition({
				objectFields,
				status: {code: 0},
			});

		apiHelpers.data.push({
			id: objectDefinition.id,
			type: 'objectDefinition',
		});

		const {filePath} =
			await viewObjectDefinitionsPage.exportObjectDefinition(
				objectDefinition.label['en_US']
			);

		await deleteObjectDefinitionFromData(apiHelpers, objectDefinition.id);

		const importedObjectName = 'ImportedWithMetadata' + getRandomInt();

		const importedObjectId =
			await viewObjectDefinitionsPage.importObjectDefinition(
				filePath,
				importedObjectName
			);

		if (importedObjectId) {
			apiHelpers.data.push({
				id: importedObjectId,
				type: 'objectDefinition',
			});
		}

		await viewObjectDefinitionsPage.clickEditObjectDefinitionLink(
			objectDefinition.label['en_US']
		);

		await page.getByRole('link', {name: 'Fields'}).click();

		for (const metadataField of [
			'Author',
			'Create Date',
			'External Reference Code',
			'ID',
			'Modified Date',
			'Status',
		]) {
			await expect(
				page.getByRole('cell').getByText(metadataField, {exact: true})
			).toBeVisible();
		}
	});

	test('Can import and maintain Layouts after importing an Object', async ({
		apiHelpers,
		page,
		viewObjectDefinitionsPage,
	}) => {
		const importedObjectName = 'Imported Object With Layout';

		const importedObjectId =
			await viewObjectDefinitionsPage.importObjectDefinition(
				getFilePath('ImportedObjectWithLayout.json'),
				importedObjectName
			);

		if (importedObjectId) {
			apiHelpers.data.push({
				id: importedObjectId,
				type: 'objectDefinition',
			});
		}

		await viewObjectDefinitionsPage.clickEditObjectDefinitionLink(
			importedObjectName
		);

		await page.getByRole('link', {name: 'Layouts'}).click();

		await expect(
			page.getByRole('link', {exact: true, name: 'Layout'})
		).toBeVisible();
	});

	test('Can import and maintain Scope after importing an Object', async ({
		apiHelpers,
		page,
		viewObjectDefinitionsPage,
	}) => {
		const importedObjectName = 'Imported Object With Scope';

		const importedObjectId =
			await viewObjectDefinitionsPage.importObjectDefinition(
				getFilePath('ImportedObjectWithScope.json'),
				importedObjectName
			);

		if (importedObjectId) {
			apiHelpers.data.push({
				id: importedObjectId,
				type: 'objectDefinition',
			});
		}

		await viewObjectDefinitionsPage.goto();
		await viewObjectDefinitionsPage.searchInput.fill(importedObjectName);
		await page.keyboard.press('Enter');

		await expect(
			page.getByRole('link', {name: importedObjectName})
		).toBeVisible();

		await viewObjectDefinitionsPage.clickEditObjectDefinitionLink(
			importedObjectName
		);

		await page.getByRole('link', {name: 'Details'}).click();

		await expect(page.getByText('Company', {exact: true})).toBeVisible();
	});

	test('Can import the same object more than once', async ({
		apiHelpers,
		page,
		viewObjectDefinitionsPage,
	}) => {
		const importedObjectIdA =
			await viewObjectDefinitionsPage.importObjectDefinition(
				getFilePath('ImportedSimpleObject.json'),
				'ImportedSimpleObjectA'
			);

		if (importedObjectIdA) {
			apiHelpers.data.push({
				id: importedObjectIdA,
				type: 'objectDefinition',
			});
		}

		const importedObjectIdB =
			await viewObjectDefinitionsPage.importObjectDefinition(
				getFilePath('ImportedSimpleObject.json'),
				'ImportedSimpleObjectB'
			);

		if (importedObjectIdB) {
			apiHelpers.data.push({
				id: importedObjectIdB,
				type: 'objectDefinition',
			});
		}

		await viewObjectDefinitionsPage.goto();

		await viewObjectDefinitionsPage.searchInput.fill(
			'Imported Simple Object'
		);

		await page.keyboard.press('Enter');

		await expect(
			page.getByRole('link', {
				exact: true,
				name: 'Imported Simple Object',
			})
		).toHaveCount(2);
	});

	test('Verify that an imported custom object is created with Draft status', async ({
		apiHelpers,
		page,
		viewObjectDefinitionsPage,
	}) => {
		const importedObjectName = 'Imported Simple Object';

		const importedObjectId =
			await viewObjectDefinitionsPage.importObjectDefinition(
				getFilePath('ImportedSimpleObject.json'),
				importedObjectName
			);

		if (importedObjectId) {
			apiHelpers.data.push({
				id: importedObjectId,
				type: 'objectDefinition',
			});
		}

		await viewObjectDefinitionsPage.goto();
		await viewObjectDefinitionsPage.searchInput.fill(importedObjectName);
		await page.keyboard.press('Enter');

		await expect(
			page.getByRole('link', {name: importedObjectName})
		).toBeVisible();

		const row = page.getByRole('row', {
			name: importedObjectName,
		});

		await expect(row.getByText('Draft')).toBeVisible();
	});
});

test.describe('Manage export/import object definitions with object entries', () => {
	test(
		'Can create an object entry of an object definition with 100 fields of all types after export and import',
		{
			annotation: {
				description:
					'this test requires an encryption algorithm and key to your portal properties',
				type: 'info',
			},
		},
		async ({
			apiHelpers,
			page,
			viewObjectDefinitionsPage,
			viewObjectEntriesPage,
		}) => {
			const listTypeDefinition =
				await apiHelpers.listTypeAdmin.postRandomListTypeDefinition();

			apiHelpers.data.push({
				id: listTypeDefinition.id,
				type: 'listTypeDefinition',
			});

			const listTypeEntryNames = ['open', 'review', 'closed'];

			for (const statusName of listTypeEntryNames) {
				await apiHelpers.listTypeAdmin.postListTypeEntry({
					key: statusName,
					listTypeDefinitionExternalReferenceCode:
						listTypeDefinition.externalReferenceCode,
					name_i18n: {en_US: statusName},
				});
			}

			const objectFields = generateObjectFields({
				listTypeDefinitionExternalReferenceCode:
					listTypeDefinition.externalReferenceCode,
				objectFieldBusinessTypes: [
					'Assignee',
					'Attachment',
					'AutoIncrement',
					'Boolean',
					'Date',
					'DateTime',
					'Decimal',
					'Encrypted',
					'Integer',
					'LongInteger',
					'LongText',
					'MultiselectPicklist',
					'Picklist',
					'PrecisionDecimal',
					'RichText',
					'Text',
				],
			});

			const allObjectFields = [
				...objectFields,
				...generateObjectFields({
					listTypeDefinitionExternalReferenceCode:
						listTypeDefinition.externalReferenceCode,
					objectFieldBusinessTypes: [
						...Array(7).fill('AutoIncrement'),
						...Array(7).fill('Boolean'),
						...Array(7).fill('Date'),
						...Array(7).fill('DateTime'),
						...Array(7).fill('Decimal'),
						...Array(7).fill('Encrypted'),
						...Array(7).fill('Integer'),
						...Array(7).fill('LongInteger'),
						...Array(7).fill('LongText'),
						...Array(7).fill('PrecisionDecimal'),
						...Array(7).fill('RichText'),
						...Array(7).fill('Text'),
					] as ObjectField['businessType'][],
				}),
			];

			const objectDefinition =
				await apiHelpers.objectAdmin.postRandomObjectDefinition({
					objectFields: allObjectFields,
					status: {code: 0},
				});

			apiHelpers.data.push({
				id: objectDefinition.id,
				type: 'objectDefinition',
			});

			const {filePath} =
				await viewObjectDefinitionsPage.exportObjectDefinition(
					objectDefinition.label['en_US']
				);

			await deleteObjectDefinitionFromData(
				apiHelpers,
				objectDefinition.id
			);

			const importedObjectName = 'ImportedObject' + getRandomInt();

			const importedObjectId =
				await viewObjectDefinitionsPage.importObjectDefinition(
					filePath,
					importedObjectName
				);

			if (importedObjectId) {
				apiHelpers.data.push({
					id: importedObjectId,
					type: 'objectDefinition',
				});
			}

			await viewObjectDefinitionsPage.goto();

			await viewObjectDefinitionsPage.searchInput.fill(
				objectDefinition.label['en_US']
			);

			await page.keyboard.press('Enter');

			await expect(
				page.getByRole('link', {name: objectDefinition.label['en_US']})
			).toBeVisible();

			const objectDefinitionAPIClient =
				await apiHelpers.buildRestClient(ObjectDefinitionAPI);

			const importedObjectDefinition =
				await objectDefinitionAPIClient.getObjectDefinition(
					importedObjectId
				);

			await viewObjectEntriesPage.goto(
				importedObjectDefinition.body.className
			);

			await viewObjectEntriesPage.clickAddObjectEntry(
				objectDefinition.label['en_US']
			);

			const {objectEntry} = await generateObjectEntryValues({
				listTypeEntries: listTypeEntryNames,
				objectEntryFormat: 'UI',
				objectFields: objectFields as ObjectField[],
				role: 'Test Test',
			});

			await viewObjectEntriesPage.fillObjectFields({
				objectEntry,
				objectFields,
			});

			await viewObjectEntriesPage.saveObjectEntryButton.click();

			await waitForAlert(page);

			for (const objectField of objectFields) {
				const label = objectField.label['en_US'];
				const value = objectEntry[objectField.name];

				if (objectField.businessType === 'Attachment') {
					await expect(
						page.getByRole('button', {name: 'astronaut.png'})
					).toBeVisible();
				}
				else if (objectField.businessType === 'AutoIncrement') {
					await expect(page.getByLabel(label)).toHaveValue('1');
				}
				else if (objectField.businessType === 'Boolean') {
					if (value) {
						await expect(page.getByLabel(label)).toBeChecked();
					}
					else {
						await expect(page.getByLabel(label)).not.toBeChecked();
					}
				}
				else if (objectField.businessType === 'MultiselectPicklist') {
					for (const selected of value as string[]) {
						await expect(
							page.getByRole('gridcell', {
								exact: true,
								name: selected,
							})
						).toBeVisible();
					}
				}
				else if (objectField.businessType === 'Picklist') {
					await expect(
						page.getByRole('combobox', {name: label})
					).toHaveText((value as {key: string}).key);
				}
				else if (objectField.businessType === 'RichText') {
					await expect(
						page
							.locator(`[data-qa-id="${objectField.name}"]`)
							.locator('.ck-editor__editable')
					).toHaveText(value as string);
				}
				else {
					await expect(page.getByLabel(label)).toHaveValue(
						value as string
					);
				}
			}
		}
	);
});
