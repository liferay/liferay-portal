/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {
	ObjectDefinition,
	ObjectDefinitionApi,
	ObjectField,
	ObjectFieldApi,
	ObjectFolder,
	ObjectFolderApi,
	ObjectValidationRule,
	ObjectValidationRuleApi,
} from '@liferay/object-admin-rest-client-js';
import {Locator, Page, expect, mergeTests} from '@playwright/test';

import {apiHelpersTest} from '../../fixtures/apiHelpersTest';
import {featureFlagsTest} from '../../fixtures/featureFlagsTest';
import {loginTest} from '../../fixtures/loginTest';
import {objectPagesTest} from '../../fixtures/objectPagesTest';
import {getRandomInt} from '../../utils/getRandomInt';
import {AsyncArray} from './utils/AsyncArray';
import {createObjectFields, mockObjectFields} from './utils/mockObjectFields';

export const test = mergeTests(
	apiHelpersTest,
	featureFlagsTest({
		'LPD-32050': {enabled: true},
	}),
	loginTest(),
	objectPagesTest
);

const createdEntities = {
	listTypeDefinitionIds: [],
	objectDefinitions: [],
	objectFolders: [],
} as {
	listTypeDefinitionIds: number[];
	objectDefinitions: ObjectDefinition[];
	objectFolders: ObjectFolder[];
};

test.beforeEach(async ({apiHelpers}) => {
	const newObjectDefinition =
		await apiHelpers.objectAdmin.postRandomObjectDefinition({
			objectFolderExternalReferenceCode: 'default',
			status: {code: 0},
		});

	createdEntities.objectDefinitions.push(newObjectDefinition);
});

test.afterEach(async ({apiHelpers}) => {
	const asyncArray = new AsyncArray<
		ObjectDefinition | ObjectFolder | number,
		void
	>();

	const objectDefinitionAPIClient =
		await apiHelpers.buildRestClient(ObjectDefinitionApi);

	await asyncArray.map({
		array: createdEntities.objectDefinitions,
		predicate: async (objectDefinition: ObjectDefinition) => {
			await objectDefinitionAPIClient.deleteObjectDefinition(
				objectDefinition.id
			);
		},
	});

	createdEntities.objectDefinitions = [];

	const objectFolderApiClient =
		await apiHelpers.buildRestClient(ObjectFolderApi);

	await asyncArray.map({
		array: createdEntities.objectFolders,
		predicate: async (objectFolder: ObjectFolder) => {
			await objectFolderApiClient.deleteObjectFolder(objectFolder.id);
		},
	});

	createdEntities.objectDefinitions = [];

	await asyncArray.map({
		array: createdEntities.listTypeDefinitionIds,
		predicate: async (listTypeDefinitionId: number) => {
			await apiHelpers.listTypeAdmin.deleteListTypeDefinition(
				listTypeDefinitionId
			);
		},
	});

	createdEntities.listTypeDefinitionIds = [];
});

test.describe('Manage object fields through Model Builder', () => {
	test.beforeEach(({page}) => {
		page.setViewportSize({height: 1080, width: 1920});
	});

	test('all picklist definitions are listed during object field creation', async ({
		apiHelpers,
		modelBuilderDiagramPage,
		modelBuilderLeftSidebarPage,
		modelBuilderObjectDefinitionNodePage,
	}) => {
		const {listTypeDefinitionIds, objectDefinitions} = createdEntities;

		const [objectDefinition] = objectDefinitions;

		const existingListTypeDefinitions = (
			await apiHelpers.listTypeAdmin.getListTypeDefinitions()
		).items;

		const allListTypeDefinitions = existingListTypeDefinitions.concat(
			await Promise.all(
				Array(22)
					.fill(null)
					.map(
						async () =>
							await apiHelpers.listTypeAdmin.postRandomListTypeDefinition()
					)
			)
		);

		allListTypeDefinitions.forEach(({id}) =>
			listTypeDefinitionIds.push(id)
		);

		await modelBuilderDiagramPage.goto({objectFolderName: 'Default'});

		await modelBuilderLeftSidebarPage.sidebarItems
			.filter({hasText: objectDefinition.name})
			.click();

		await modelBuilderObjectDefinitionNodePage.openAddNewObjectFieldOrRelationshipModal(
			objectDefinition.name,
			modelBuilderDiagramPage.objectDefinitionNodes,
			modelBuilderObjectDefinitionNodePage.addObjectFieldButton
		);

		await modelBuilderObjectDefinitionNodePage.fillObjectFieldLabelInput(
			'objectFieldLabel' + getRandomInt()
		);

		await modelBuilderObjectDefinitionNodePage.selectNewObjectFieldBusinessTypeOption(
			'Picklist'
		);

		await modelBuilderObjectDefinitionNodePage.objectFieldPicklistSelect.click();

		const listTypeDefinitionBox =
			modelBuilderDiagramPage.page.getByRole('listbox');

		await expect(listTypeDefinitionBox).toBeVisible();

		await expect(listTypeDefinitionBox.getByRole('option')).toHaveCount(
			allListTypeDefinitions.length
		);
	});

	test('can add picklist object field to object definition node', async ({
		apiHelpers,
		modelBuilderDiagramPage,
		modelBuilderLeftSidebarPage,
		modelBuilderObjectDefinitionNodePage,
		page,
		viewObjectDefinitionsPage,
	}) => {
		const {listTypeDefinitionIds, objectDefinitions} = createdEntities;

		const [objectDefinition] = objectDefinitions;

		await page.goto('/');

		const listTypeDefinition =
			await apiHelpers.listTypeAdmin.postRandomListTypeDefinition();

		listTypeDefinitionIds.push(listTypeDefinition.id);

		await viewObjectDefinitionsPage.goto();

		await viewObjectDefinitionsPage.openObjectFolder('default');

		await viewObjectDefinitionsPage.viewInModelBuilderButton.click();

		await modelBuilderLeftSidebarPage.sidebarItems
			.filter({hasText: objectDefinition.name})
			.click();

		const objectFieldLabel = 'objectFieldLabel' + getRandomInt();

		await modelBuilderObjectDefinitionNodePage.createObjectField({
			listTypeDefinitionName: listTypeDefinition.name,
			mandatory: false,
			objectDefinitionLabel: objectDefinition.label['en_US'],
			objectDefinitionNodes:
				modelBuilderDiagramPage.objectDefinitionNodes,
			objectFieldBusinessType: 'Picklist',
			objectFieldLabel,
		});

		await expect(
			modelBuilderDiagramPage.objectDefinitionNodes
				.filter({hasText: objectDefinition.label['en_US']})
				.getByText(objectFieldLabel)
		).toBeVisible();
	});

	test('can delete object field', async ({
		apiHelpers,
		modelBuilderDiagramPage,
		modelBuilderLeftSidebarPage,
		modelBuilderObjectDefinitionNodePage,
		modelBuilderRightSidebarPage,
	}) => {
		const [objectDefinition] = createdEntities.objectDefinitions;

		const objectFieldApiClient =
			await apiHelpers.buildRestClient(ObjectFieldApi);

		await objectFieldApiClient.postObjectDefinitionByExternalReferenceCodeObjectField(
			objectDefinition.externalReferenceCode,
			{
				DBType: ObjectField.DBTypeEnum.Integer,
				label: {
					en_US: 'intField',
				},

				listTypeDefinitionId: 0,
				localized: false,
				name: 'intField',
				objectFieldSettings: [],
				readOnly: ObjectField.ReadOnlyEnum.False,
				readOnlyConditionExpression: '',
				required: false,
				state: false,
				system: false,
			}
		);

		await modelBuilderDiagramPage.goto({objectFolderName: 'Default'});

		await modelBuilderLeftSidebarPage.sidebarItems
			.filter({hasText: objectDefinition.name})
			.click();

		await modelBuilderObjectDefinitionNodePage.clickShowAllFieldsButton(
			objectDefinition.label['en_US'],
			modelBuilderDiagramPage.objectDefinitionNodes
		);

		await modelBuilderDiagramPage.objectDefinitionNodes
			.filter({hasText: objectDefinition.name})
			.getByText('Integer', {exact: true})
			.click();

		await modelBuilderRightSidebarPage.deleteTrashButton.click();

		await modelBuilderObjectDefinitionNodePage.modalDeleteObjectDefinitionConfirmationButton.click();

		await expect(
			modelBuilderDiagramPage.objectDefinitionNodes
				.filter({hasText: objectDefinition.name})
				.getByText('intField')
		).toBeHidden();
	});

	test('can edit picklist object field from draft object definition', async ({
		apiHelpers,
		modelBuilderDiagramPage,
		modelBuilderLeftSidebarPage,
		modelBuilderObjectDefinitionNodePage,
		page,
	}) => {
		const {listTypeDefinitionIds} = createdEntities;

		const draftObjectDefinition =
			await apiHelpers.objectAdmin.postRandomObjectDefinition({
				objectFolderExternalReferenceCode: 'default',
				status: {code: 2},
			});

		createdEntities.objectDefinitions.push(draftObjectDefinition);

		const listTypeDefinition =
			await apiHelpers.listTypeAdmin.postRandomListTypeDefinition();

		listTypeDefinitionIds.push(listTypeDefinition.id);

		let picklistFieldName = 'picklistField' + getRandomInt();

		const objectFieldApiClient =
			await apiHelpers.buildRestClient(ObjectFieldApi);

		await objectFieldApiClient.postObjectDefinitionByExternalReferenceCodeObjectField(
			draftObjectDefinition.externalReferenceCode,
			{
				DBType: ObjectField.DBTypeEnum.String,
				businessType: ObjectField.BusinessTypeEnum.Picklist,
				externalReferenceCode: picklistFieldName,
				indexed: true,
				indexedAsKeyword: false,
				indexedLanguageId: '',
				label: {en_US: picklistFieldName},
				listTypeDefinitionExternalReferenceCode:
					listTypeDefinition.externalReferenceCode,
				listTypeDefinitionId: listTypeDefinition.id,
				localized: false,
				name: picklistFieldName,
				readOnly: ObjectField.ReadOnlyEnum.False,
				required: false,
				state: false,
				system: false,
			}
		);

		await modelBuilderDiagramPage.goto({objectFolderName: 'Default'});

		await modelBuilderLeftSidebarPage.clickSideBarItem(
			draftObjectDefinition.label['en_US']
		);

		await modelBuilderObjectDefinitionNodePage.clickShowAllFieldsButton(
			draftObjectDefinition.label['en_US'],
			modelBuilderDiagramPage.objectDefinitionNodes
		);

		await page.getByText(picklistFieldName).click();

		picklistFieldName = 'picklistField' + getRandomInt();

		await page
			.getByPlaceholder('Text to translate...')
			.fill(picklistFieldName);

		await modelBuilderLeftSidebarPage.clickSideBarItem(
			draftObjectDefinition.label['en_US']
		);

		await expect(page.getByText(picklistFieldName)).toBeVisible();
	});

	test('can navigate to picklist portlet through manage picklist button', async ({
		apiHelpers,
		modelBuilderDiagramPage,
		modelBuilderLeftSidebarPage,
		modelBuilderObjectDefinitionNodePage,
		modelBuilderRightSidebarPage,
		page,
	}) => {
		const {listTypeDefinitionIds, objectDefinitions} = createdEntities;

		const [objectDefinition] = objectDefinitions;

		const listTypeDefinition =
			await apiHelpers.listTypeAdmin.postRandomListTypeDefinition();

		listTypeDefinitionIds.push(listTypeDefinition.id);

		const objectFieldApiClient =
			await apiHelpers.buildRestClient(ObjectFieldApi);

		await objectFieldApiClient.postObjectDefinitionByExternalReferenceCodeObjectField(
			objectDefinition.externalReferenceCode,
			createObjectFields(
				'picklist',
				[{label: 'picklistField', name: 'picklistField'}],
				{
					listTypeDefinitionExternalReferenceCode:
						listTypeDefinition.externalReferenceCode,
					listTypeDefinitionId: listTypeDefinition.id,
				}
			)[0]
		);

		await modelBuilderDiagramPage.goto({objectFolderName: 'Default'});

		await modelBuilderLeftSidebarPage.sidebarItems
			.filter({hasText: objectDefinition.name})
			.click();

		await modelBuilderObjectDefinitionNodePage.clickShowAllFieldsButton(
			objectDefinition.name,
			modelBuilderDiagramPage.objectDefinitionNodes
		);

		await page.getByText('picklistField').click();

		const newTabPagePromise = new Promise<Page>((resolve) =>
			page.once('popup', resolve)
		);

		await modelBuilderRightSidebarPage.managePicklistsButton.click();

		const newTabPage = await newTabPagePromise;

		await expect(
			newTabPage.getByRole('heading', {level: 1, name: 'Picklists'})
		).toBeVisible();
	});

	test('can see the translation of the object fields businesses types in object definition node', async ({
		apiHelpers,
		modelBuilderDiagramPage,
		page,
	}) => {
		const {listTypeDefinitionIds, objectDefinitions, objectFolders} =
			createdEntities;
		const objectFolder =
			await apiHelpers.objectAdmin.postRandomObjectFolder();

		objectFolders.push(objectFolder);

		const {listTypeDefinition, objectFields} = await mockObjectFields({
			apiHelpers,
			objectFieldBusinessTypes: [
				'attachment',
				'boolean',
				'date',
				'decimal',
				'integer',
				'longInteger',
				'longText',
				'picklist',
				'precisionDecimal',
				'richText',
				'text',
			],
		});

		listTypeDefinitionIds.push(listTypeDefinition.id);

		const objectDefinition =
			await apiHelpers.objectAdmin.postRandomObjectDefinition({
				objectFields,
				objectFolderExternalReferenceCode:
					objectFolder.externalReferenceCode,
				status: {code: 1},
			});

		objectDefinitions.push(objectDefinition);

		await apiHelpers.objectAdmin.postObjectDefinitionObjectFieldBatch(
			objectDefinition.id,
			objectFields
		);

		await page.goto('pt');

		await page.waitForLoadState('networkidle');

		await modelBuilderDiagramPage.goto({
			objectFolderName: objectFolder.name,
		});

		await modelBuilderDiagramPage.objectDefinitionNodes
			.filter({hasText: objectDefinition.label['en_US']})
			.getByRole('button', {name: 'Exibir tudo campo'})
			.click();

		const objectDefinitionNodeObjectFields = await page
			.locator('.lfr-objects__model-builder-node-field')
			.all();

		const {objectFields: objectDefinitionObjectFields} = objectDefinition;

		expect(objectDefinitionNodeObjectFields).toHaveLength(
			objectDefinitionObjectFields.length
		);

		const objectFieldBusinessTypeNameLabel = {
			Attachment: 'Anexo',
			Boolean: 'Boolean',
			Date: 'Data',
			Decimal: 'Decimal',
			Integer: 'Inteiro',
			LongInteger: 'Número inteiro longo',
			LongText: 'Texto longo',
			Picklist: 'Lista de seleção',
			PrecisionDecimal: 'Casa decimal',
			RichText: 'Rich Text',
			Text: 'Texto',
		};

		const asyncArray = new AsyncArray<Locator, boolean>();

		for (let i = 0; i < objectDefinitionObjectFields.length; i++) {
			const objectFieldRow = await asyncArray.find({
				array: objectDefinitionNodeObjectFields,
				predicate: async (objectFieldTableRow: Locator) => {
					return (await objectFieldTableRow.textContent()).includes(
						objectDefinitionObjectFields[i].label['en_US']
					);
				},
			});

			expect(objectFieldRow).toBeVisible();
			expect(
				objectFieldRow.getByText(
					objectFieldBusinessTypeNameLabel[
						objectDefinitionObjectFields[i].businessType
					],
					{exact: true}
				)
			).toBeVisible();
		}

		await page.goto('en');
	});

	test('can show and hide object fields in the object definition node', async ({
		apiHelpers,
		modelBuilderDiagramPage,
		modelBuilderLeftSidebarPage,
		modelBuilderObjectDefinitionNodePage,
		page,
	}) => {
		const [objectDefinition] = createdEntities.objectDefinitions;

		const dateFieldName = 'dateField' + getRandomInt();
		const integerFieldName = 'integerField' + getRandomInt();

		const objectFieldApiClient =
			await apiHelpers.buildRestClient(ObjectFieldApi);

		await objectFieldApiClient.postObjectDefinitionByExternalReferenceCodeObjectField(
			objectDefinition.externalReferenceCode,
			{
				DBType: ObjectField.DBTypeEnum.Integer,
				businessType: ObjectField.BusinessTypeEnum.Integer,
				externalReferenceCode: integerFieldName,
				indexed: true,
				indexedAsKeyword: false,
				indexedLanguageId: '',
				label: {en_US: integerFieldName},
				listTypeDefinitionId: 0,
				localized: false,
				name: integerFieldName,
				readOnly: ObjectField.ReadOnlyEnum.False,
				required: false,
				state: false,
				system: false,
			}
		);

		await objectFieldApiClient.postObjectDefinitionByExternalReferenceCodeObjectField(
			objectDefinition.externalReferenceCode,
			{
				DBType: ObjectField.DBTypeEnum.Date,
				businessType: ObjectField.BusinessTypeEnum.Date,
				externalReferenceCode: dateFieldName,
				indexed: true,
				indexedAsKeyword: false,
				indexedLanguageId: '',
				label: {en_US: dateFieldName},
				listTypeDefinitionId: 0,
				localized: false,
				name: dateFieldName,
				readOnly: ObjectField.ReadOnlyEnum.False,
				required: false,
				state: false,
				system: false,
			}
		);

		await modelBuilderDiagramPage.goto({objectFolderName: 'Default'});

		await modelBuilderLeftSidebarPage.clickSideBarItem(
			objectDefinition.label['en_US']
		);

		await expect(page.getByText(integerFieldName)).not.toBeVisible();
		await expect(page.getByText(dateFieldName)).not.toBeVisible();

		await modelBuilderObjectDefinitionNodePage.clickShowAllFieldsButton(
			objectDefinition.label['en_US'],
			modelBuilderDiagramPage.objectDefinitionNodes
		);

		await expect(page.getByText(integerFieldName)).toBeVisible();
		await expect(page.getByText(dateFieldName)).toBeVisible();

		await modelBuilderObjectDefinitionNodePage.clickHideFieldsButton(
			objectDefinition.label['en_US'],
			modelBuilderDiagramPage.objectDefinitionNodes
		);

		await expect(page.getByText(integerFieldName)).not.toBeVisible();
		await expect(page.getByText(dateFieldName)).not.toBeVisible();
	});

	test('cannot delete an objectField that belongs to a unique composite key validation through Model Builder', async ({
		apiHelpers,
		modelBuilderDiagramPage,
		modelBuilderLeftSidebarPage,
		modelBuilderObjectDefinitionNodePage,
		modelBuilderRightSidebarPage,
		page,
	}) => {
		const [objectDefinition] = createdEntities.objectDefinitions;

		const integerFieldName = 'integerField' + getRandomInt();

		const objectFieldApiClient =
			await apiHelpers.buildRestClient(ObjectFieldApi);

		await objectFieldApiClient.postObjectDefinitionByExternalReferenceCodeObjectField(
			objectDefinition.externalReferenceCode,
			{
				DBType: ObjectField.DBTypeEnum.Integer,
				businessType: ObjectField.BusinessTypeEnum.Integer,
				externalReferenceCode: integerFieldName,
				indexed: true,
				indexedAsKeyword: false,
				indexedLanguageId: '',
				label: {en_US: integerFieldName},
				listTypeDefinitionId: 0,
				localized: false,
				name: integerFieldName,
				readOnly: ObjectField.ReadOnlyEnum.False,
				required: false,
				state: false,
				system: false,
			}
		);

		const objectValidationName =
			'Unique Composite Key Object Validation' + getRandomInt();

		const objectValidationRuleApiClient = await apiHelpers.buildRestClient(
			ObjectValidationRuleApi
		);

		await objectValidationRuleApiClient.postObjectDefinitionByExternalReferenceCodeObjectValidationRule(
			objectDefinition.externalReferenceCode,
			{
				active: true,
				engine: 'compositeKey',
				engineLabel: 'Composite Key',
				errorLabel: {
					en_US: 'Unique composite key object validation error',
				},
				name: {
					en_US: objectValidationName,
				},
				objectValidationRuleSettings: [
					{
						name: 'compositeKeyObjectFieldExternalReferenceCode',
						value: 'textField',
					} as any,
					{
						name: 'compositeKeyObjectFieldExternalReferenceCode',
						value: integerFieldName,
					} as any,
				],
				outputType: ObjectValidationRule.OutputTypeEnum.FullValidation,
				script: '',
				system: false,
			}
		);

		await modelBuilderDiagramPage.goto({objectFolderName: 'Default'});

		await modelBuilderLeftSidebarPage.sidebarItems
			.filter({hasText: objectDefinition.name})
			.click();

		await modelBuilderObjectDefinitionNodePage.clickShowAllFieldsButton(
			objectDefinition.name,
			modelBuilderDiagramPage.objectDefinitionNodes
		);

		await page.getByText(integerFieldName).click();

		await modelBuilderRightSidebarPage.deleteButton.click();

		await expect(page.getByText('Deletion Not Allowed')).toBeVisible();
		await expect(
			page.getByText(
				`The object field "${integerFieldName}" cannot be deleted because it is used in a unique composite key validation. To remove this object field, you must first delete the associated unique composite key validation.`
			)
		).toBeVisible();
	});

	test('cannot delete only custom object field of an published object definition', async ({
		modelBuilderDiagramPage,
		modelBuilderLeftSidebarPage,
		modelBuilderObjectDefinitionNodePage,
		modelBuilderRightSidebarPage,
		page,
	}) => {
		const [objectDefinition] = createdEntities.objectDefinitions;

		await modelBuilderDiagramPage.goto({objectFolderName: 'Default'});

		await modelBuilderLeftSidebarPage.sidebarItems
			.filter({hasText: objectDefinition.name})
			.click();

		await modelBuilderObjectDefinitionNodePage.clickShowAllFieldsButton(
			objectDefinition.name,
			modelBuilderDiagramPage.objectDefinitionNodes
		);

		await page.getByText('textField').click();

		await modelBuilderRightSidebarPage.deleteButton.click();

		await expect(page.getByText('Deletion Not Allowed')).toBeVisible();
		await expect(
			page.getByText(
				`The object field "textField" cannot be deleted because it is the only custom object field of the published object definition.`
			)
		).toBeVisible();
	});
});

test.describe('Manage objectFields through Objects Admin UI', () => {
	test('can create object fields of multiple types (except AutoIncrement, Date and Time, Encrypted and Aggregation)', async ({
		apiHelpers,
		modelBuilderDiagramPage,
		objectFieldsPage,
		page,
	}) => {
		const {listTypeDefinitionIds, objectDefinitions} = createdEntities;

		const objectDefinition =
			await apiHelpers.objectAdmin.postRandomObjectDefinition({
				objectFields: [],
				objectFolderExternalReferenceCode: 'default',
				status: {code: 1},
			});

		objectDefinitions.push(objectDefinition);

		const listTypeDefinition =
			await apiHelpers.listTypeAdmin.postRandomListTypeDefinition();

		listTypeDefinitionIds.push(listTypeDefinition.id);

		await objectFieldsPage.goto(objectDefinition.label['en_US']);

		const objectFieldsMock = [
			{
				objectFieldBusinessType: 'Attachment',
				objectFieldLabel: `attachment${getRandomInt()}`,
			},
			{
				objectFieldBusinessType: 'Boolean',
				objectFieldLabel: `boolean${getRandomInt()}`,
			},
			{
				objectFieldBusinessType: 'Date',
				objectFieldLabel: `date${getRandomInt()}`,
			},
			{
				objectFieldBusinessType: 'Decimal',
				objectFieldLabel: `decimal${getRandomInt()}`,
			},
			{
				objectFieldBusinessType: 'Integer',
				objectFieldLabel: `integer${getRandomInt()}`,
			},
			{
				objectFieldBusinessType: 'Long Integer',
				objectFieldLabel: `longInteger${getRandomInt()}`,
			},
			{
				objectFieldBusinessType: 'Long Text',
				objectFieldLabel: `longText${getRandomInt()}`,
			},
			{
				objectFieldBusinessType: 'Multiselect Picklist',
				objectFieldLabel: `multiselectPicklist${getRandomInt()}`,
			},
			{
				objectFieldBusinessType: 'Picklist',
				objectFieldLabel: `picklist${getRandomInt()}`,
			},
			{
				objectFieldBusinessType: 'Precision Decimal',
				objectFieldLabel: `precisionDecimal${getRandomInt()}`,
			},
			{
				objectFieldBusinessType: 'Rich Text',
				objectFieldLabel: `richText${getRandomInt()}`,
			},
			{
				objectFieldBusinessType: 'Text',
				objectFieldLabel: `text${getRandomInt()}`,
			},
		] as {
			objectFieldBusinessType: string;
			objectFieldLabel: string;
		}[];

		for (const objectField of objectFieldsMock) {
			const {objectFieldBusinessType, objectFieldLabel} = objectField;

			if (objectFieldBusinessType === 'Attachment') {
				await objectFieldsPage.addObjectField({
					attachmentSource: 'Upload Directly from the User',
					objectDefinitionNodes:
						modelBuilderDiagramPage.objectDefinitionNodes,
					objectFieldBusinessType,
					objectFieldLabel,
				});

				continue;
			}

			if (
				objectFieldBusinessType === 'Picklist' ||
				objectFieldBusinessType === `Multiselect Picklist`
			) {
				await objectFieldsPage.addObjectField({
					listTypeDefinitionName: listTypeDefinition.name,
					objectDefinitionNodes:
						modelBuilderDiagramPage.objectDefinitionNodes,
					objectFieldBusinessType,
					objectFieldLabel,
				});

				continue;
			}

			await objectFieldsPage.addObjectField({
				objectDefinitionNodes:
					modelBuilderDiagramPage.objectDefinitionNodes,
				objectFieldBusinessType,
				objectFieldLabel,
			});
		}

		while (
			(await page.locator('tbody > tr').all()).length !==
			objectDefinition.objectFields.length + objectFieldsMock.length
		) {
			await page.waitForTimeout(1000);
		}

		const objectFieldTableRows = await page.locator('tbody > tr').all();

		const asyncArray = new AsyncArray<Locator, boolean>();

		const objectFieldTableCustomRows = await asyncArray.filter({
			array: objectFieldTableRows,
			predicate: async (objectFieldTableRow: Locator) => {
				return (await objectFieldTableRow.textContent()).includes(
					'Custom'
				);
			},
		});

		for (let i = 0; i < objectFieldsMock.length; i++) {
			const {objectFieldBusinessType, objectFieldLabel} =
				objectFieldsMock[i];

			await expect(
				objectFieldTableCustomRows[i].getByText(objectFieldLabel, {
					exact: true,
				})
			).toBeVisible();

			await expect(
				objectFieldTableCustomRows[i].getByText(
					String(objectFieldBusinessType),
					{exact: true}
				)
			).toBeVisible();
		}
	});

	test('cannot create localized object fields in unmodifiable system object definition', async ({
		objectFieldsPage,
	}) => {
		await objectFieldsPage.goto('Account');

		await objectFieldsPage.addObjectFieldButton.waitFor();

		await objectFieldsPage.addObjectFieldButton.click();

		await objectFieldsPage.objectFieldOptionsDropdown.click();

		await objectFieldsPage.page
			.getByRole('option', {exact: true, name: 'Text'})
			.click();

		expect(
			objectFieldsPage.page.getByText('Enable Entry Translation')
		).toBeDisabled();
	});

	test('cannot delete an objectField that belongs to a unique composite key validation through Objects Admin UI', async ({
		apiHelpers,
		objectFieldsPage,
		page,
	}) => {
		const [objectDefinition] = createdEntities.objectDefinitions;
		const integerFieldName = 'integerField' + getRandomInt();

		const objectFieldApiClient =
			await apiHelpers.buildRestClient(ObjectFieldApi);

		await objectFieldApiClient.postObjectDefinitionByExternalReferenceCodeObjectField(
			objectDefinition.externalReferenceCode,
			{
				DBType: ObjectField.DBTypeEnum.Integer,
				businessType: ObjectField.BusinessTypeEnum.Integer,
				externalReferenceCode: integerFieldName,
				indexed: true,
				indexedAsKeyword: false,
				indexedLanguageId: '',
				label: {en_US: integerFieldName},
				listTypeDefinitionId: 0,
				localized: false,
				name: integerFieldName,
				readOnly: ObjectField.ReadOnlyEnum.False,
				required: false,
				state: false,
				system: false,
			}
		);

		const objectValidationName =
			'Unique Composite Key Object Validation' + getRandomInt();

		const objectValidationRuleApiClient = await apiHelpers.buildRestClient(
			ObjectValidationRuleApi
		);

		await objectValidationRuleApiClient.postObjectDefinitionByExternalReferenceCodeObjectValidationRule(
			objectDefinition.externalReferenceCode,
			{
				active: true,
				engine: 'compositeKey',
				engineLabel: 'Composite Key',
				errorLabel: {
					en_US: 'Unique composite key object validation error',
				},
				name: {
					en_US: objectValidationName,
				},
				objectValidationRuleSettings: [
					{
						name: 'compositeKeyObjectFieldExternalReferenceCode',
						value: 'textField',
					} as any,
					{
						name: 'compositeKeyObjectFieldExternalReferenceCode',
						value: integerFieldName,
					} as any,
				],
				outputType: ObjectValidationRule.OutputTypeEnum.FullValidation,
				script: '',
				system: false,
			}
		);

		await objectFieldsPage.goto(objectDefinition.label['en_US']);

		await objectFieldsPage.deleteObjectField(-1);

		await expect(page.getByText('Deletion Not Allowed')).toBeVisible();
		await expect(
			page.getByText(
				`The object field "${integerFieldName}" cannot be deleted because it is used in a unique composite key validation. To remove this object field, you must first delete the associated unique composite key validation.`
			)
		).toBeVisible();
	});
});
