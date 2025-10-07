/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {
	ObjectDefinition,
	ObjectDefinitionAPI,
	ObjectField,
	ObjectFieldAPI,
	ObjectRelationshipAPI,
	ObjectValidationRuleAPI,
} from '@liferay/object-admin-rest-client-js';
import {Locator, Page, expect, mergeTests} from '@playwright/test';

import {apiHelpersTest} from '../../../fixtures/apiHelpersTest';
import {dataApiHelpersTest} from '../../../fixtures/dataApiHelpersTest';
import {featureFlagsTest} from '../../../fixtures/featureFlagsTest';
import {loginTest} from '../../../fixtures/loginTest';
import {objectPagesTest} from '../../../fixtures/objectPagesTest';
import {getRandomInt} from '../../../utils/getRandomInt';
import getRandomString from '../../../utils/getRandomString';
import {waitForAlert} from '../../../utils/waitForAlert';
import {AsyncArray} from './utils/AsyncArray';
import {generateObjectFields} from './utils/generateObjectFields';
import {postListTypeDefinitionListTypeEntries} from './utils/postListTypeDefinitionListTypeEntries';

export const test = mergeTests(
	apiHelpersTest,
	dataApiHelpersTest,
	featureFlagsTest({
		'LPD-32050': {enabled: true},
	}),
	loginTest(),
	objectPagesTest
);

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
		const objectDefinition =
			await apiHelpers.objectAdmin.postRandomObjectDefinition({
				status: {code: 0},
			});

		apiHelpers.data.push({
			id: objectDefinition.id,
			type: 'objectDefinition',
		});

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
			apiHelpers.data.push({
				id,
				type: 'listTypeDefinition',
			})
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

	test('assert that field entry translation is disabled by default', async ({
		apiHelpers,
		modelBuilderDiagramPage,
		modelBuilderLeftSidebarPage,
		modelBuilderObjectDefinitionNodePage,
		page,
	}) => {
		const objectDefinition =
			await apiHelpers.objectAdmin.postRandomObjectDefinition({
				status: {code: 0},
			});

		apiHelpers.data.push({
			id: objectDefinition.id,
			type: 'objectDefinition',
		});

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
			'Decimal'
		);

		await expect(
			page.getByRole('switch', {name: 'Enable Entry Translation'})
		).not.toBeChecked();
	});

	test('can add picklist object field to object definition node', async ({
		apiHelpers,
		modelBuilderDiagramPage,
		modelBuilderLeftSidebarPage,
		modelBuilderObjectDefinitionNodePage,
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

		const {listTypeDefinition} =
			await postListTypeDefinitionListTypeEntries({
				apiHelpers,
			});

		apiHelpers.data.push({
			id: listTypeDefinition.id,
			type: 'listTypeDefinition',
		});

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
		const objectFields = generateObjectFields({
			objectFieldBusinessTypes: ['Integer', 'Text'],
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
				.getByText('Integer', {exact: true})
		).toBeHidden();
	});

	test('can edit picklist object field from draft object definition', async ({
		apiHelpers,
		modelBuilderDiagramPage,
		modelBuilderLeftSidebarPage,
		modelBuilderObjectDefinitionNodePage,
		page,
	}) => {
		const {listTypeDefinition} =
			await postListTypeDefinitionListTypeEntries({
				apiHelpers,
			});

		const objectFields: Partial<ObjectField>[] = generateObjectFields({
			listTypeDefinitionExternalReferenceCode:
				listTypeDefinition.externalReferenceCode,
			objectFieldBusinessTypes: ['Picklist'],
		});

		apiHelpers.data.push({
			id: listTypeDefinition.id,
			type: 'listTypeDefinition',
		});

		const draftObjectDefinition =
			await apiHelpers.objectAdmin.postRandomObjectDefinition({
				objectFields,
				status: {code: 2},
			});

		apiHelpers.data.push({
			id: draftObjectDefinition.id,
			type: 'objectDefinition',
		});

		await modelBuilderDiagramPage.goto({objectFolderName: 'Default'});

		await modelBuilderLeftSidebarPage.clickSideBarItem(
			draftObjectDefinition.label['en_US']
		);

		await modelBuilderObjectDefinitionNodePage.clickShowAllFieldsButton(
			draftObjectDefinition.label['en_US'],
			modelBuilderDiagramPage.objectDefinitionNodes
		);

		await page.getByText('Picklist', {exact: true}).click();

		const picklistFieldName = 'picklistField' + getRandomInt();

		await page
			.getByPlaceholder('Text to translate...')
			.fill(picklistFieldName);

		await modelBuilderLeftSidebarPage.clickSideBarItem(
			draftObjectDefinition.label['en_US']
		);

		await expect(
			page.getByText(picklistFieldName, {exact: true})
		).toBeVisible();
	});

	test('can navigate to picklist portlet through manage picklist button', async ({
		apiHelpers,
		modelBuilderDiagramPage,
		modelBuilderLeftSidebarPage,
		modelBuilderObjectDefinitionNodePage,
		modelBuilderRightSidebarPage,
		page,
	}) => {
		const {listTypeDefinition} =
			await postListTypeDefinitionListTypeEntries({
				apiHelpers,
			});

		const objectFields: Partial<ObjectField>[] = generateObjectFields({
			listTypeDefinitionExternalReferenceCode:
				listTypeDefinition.externalReferenceCode,
			objectFieldBusinessTypes: ['Picklist'],
		});

		apiHelpers.data.push({
			id: listTypeDefinition.id,
			type: 'listTypeDefinition',
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

		await modelBuilderDiagramPage.goto({objectFolderName: 'Default'});

		await modelBuilderLeftSidebarPage.sidebarItems
			.filter({hasText: objectDefinition.name})
			.click();

		await modelBuilderObjectDefinitionNodePage.clickShowAllFieldsButton(
			objectDefinition.name,
			modelBuilderDiagramPage.objectDefinitionNodes
		);

		await page.getByText('Picklist', {exact: true}).click();

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
		const objectFolder =
			await apiHelpers.objectAdmin.postRandomObjectFolder();

		apiHelpers.data.push({id: objectFolder.id, type: 'objectFolder'});

		const {listTypeDefinition} =
			await postListTypeDefinitionListTypeEntries({
				apiHelpers,
			});

		const objectFields = generateObjectFields({
			listTypeDefinitionExternalReferenceCode:
				listTypeDefinition.externalReferenceCode,
			objectFieldBusinessTypes: [
				'Attachment',
				'Boolean',
				'Date',
				'Decimal',
				'Integer',
				'LongInteger',
				'LongText',
				'Picklist',
				'PrecisionDecimal',
				'RichText',
				'Text',
			],
		});

		apiHelpers.data.push({
			id: listTypeDefinition.id,
			type: 'listTypeDefinition',
		});

		const objectDefinition =
			await apiHelpers.objectAdmin.postRandomObjectDefinition({
				objectFields,
				objectFolderExternalReferenceCode:
					objectFolder.externalReferenceCode,
				status: {code: 1},
			});

		apiHelpers.data.push({
			id: objectDefinition.id,
			type: 'objectDefinition',
		});

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
			DateTime: 'Data/Hora',
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
		const objectDefinition =
			await apiHelpers.objectAdmin.postRandomObjectDefinition({
				status: {code: 0},
			});

		apiHelpers.data.push({
			id: objectDefinition.id,
			type: 'objectDefinition',
		});

		const dateFieldName = 'dateField' + getRandomInt();
		const integerFieldName = 'integerField' + getRandomInt();

		const objectFieldAPIClient =
			await apiHelpers.buildRestClient(ObjectFieldAPI);

		await objectFieldAPIClient.postObjectDefinitionByExternalReferenceCodeObjectField(
			objectDefinition.externalReferenceCode,
			{
				DBType: 'Integer',
				businessType: 'Integer',
				externalReferenceCode: integerFieldName,
				indexed: true,
				indexedAsKeyword: false,
				indexedLanguageId: '',
				label: {en_US: integerFieldName},
				listTypeDefinitionId: 0,
				localized: false,
				name: integerFieldName,
				readOnly: 'false',
				required: false,
				state: false,
				system: false,
			}
		);

		await objectFieldAPIClient.postObjectDefinitionByExternalReferenceCodeObjectField(
			objectDefinition.externalReferenceCode,
			{
				DBType: 'Date',
				businessType: 'Date',
				externalReferenceCode: dateFieldName,
				indexed: true,
				indexedAsKeyword: false,
				indexedLanguageId: '',
				label: {en_US: dateFieldName},
				listTypeDefinitionId: 0,
				localized: false,
				name: dateFieldName,
				readOnly: 'false',
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
		const objectDefinition =
			await apiHelpers.objectAdmin.postRandomObjectDefinition({
				status: {code: 0},
			});

		apiHelpers.data.push({
			id: objectDefinition.id,
			type: 'objectDefinition',
		});

		const integerFieldName = 'integerField' + getRandomInt();

		const objectFieldAPIClient =
			await apiHelpers.buildRestClient(ObjectFieldAPI);

		await objectFieldAPIClient.postObjectDefinitionByExternalReferenceCodeObjectField(
			objectDefinition.externalReferenceCode,
			{
				DBType: 'Integer',
				businessType: 'Integer',
				externalReferenceCode: integerFieldName,
				indexed: true,
				indexedAsKeyword: false,
				indexedLanguageId: '',
				label: {en_US: integerFieldName},
				listTypeDefinitionId: 0,
				localized: false,
				name: integerFieldName,
				readOnly: 'false',
				required: false,
				state: false,
				system: false,
			}
		);

		const objectValidationName =
			'Unique Composite Key Object Validation' + getRandomInt();

		const objectValidationRuleAPIClient = await apiHelpers.buildRestClient(
			ObjectValidationRuleAPI
		);

		await objectValidationRuleAPIClient.postObjectDefinitionByExternalReferenceCodeObjectValidationRule(
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
				outputType: 'fullValidation',
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
		apiHelpers,
		modelBuilderDiagramPage,
		modelBuilderLeftSidebarPage,
		modelBuilderObjectDefinitionNodePage,
		modelBuilderRightSidebarPage,
		page,
	}) => {
		const objectDefinition =
			await apiHelpers.objectAdmin.postRandomObjectDefinition({
				status: {code: 0},
			});

		apiHelpers.data.push({
			id: objectDefinition.id,
			type: 'objectDefinition',
		});

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

	test('navigates to documentation from the "unsupported translations" alert link', async ({
		apiHelpers,
		modelBuilderDiagramPage,
		modelBuilderLeftSidebarPage,
		modelBuilderObjectDefinitionNodePage,
		page,
	}) => {
		const objectFields = generateObjectFields({
			objectFieldBusinessTypes: ['Encrypted'],
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

		await modelBuilderDiagramPage.goto({objectFolderName: 'Default'});

		await modelBuilderLeftSidebarPage.sidebarItems
			.filter({hasText: objectDefinition.label['en_US']})
			.click();

		await modelBuilderObjectDefinitionNodePage.clickShowAllFieldsButton(
			objectDefinition.label['en_US'],
			modelBuilderDiagramPage.objectDefinitionNodes
		);

		await page.getByText('Encrypted', {exact: true}).click();

		const pagePromise = page.waitForEvent('popup');

		await page
			.getByRole('link', {name: 'Learn more. (Opens a new window)'})
			.click();

		const newPage = await pagePromise;

		await expect(
			newPage.getByRole('heading', {
				name: 'Localizing Object Definitions',
			})
		).toBeVisible();
	});
});

test.describe('Manage objectFields through Objects Admin UI', () => {
	test('can update custom object field in a system object', async ({
		apiHelpers,
		objectFieldsPage,
		page,
	}) => {
		const {items} = await apiHelpers.objectAdmin.getAllObjectDefinitions();

		const systemObjectDefinition = items.find((item: ObjectDefinition) => {
			return item.system === true;
		});

		const objectFieldAPIClient =
			await apiHelpers.buildRestClient(ObjectFieldAPI);

		const objectFieldLabel = 'objectFieldLabel';

		await objectFieldAPIClient.postObjectDefinitionObjectField(
			systemObjectDefinition.id,
			{
				DBType: 'String',
				businessType: 'Text',
				label: {en_US: objectFieldLabel},
				name: 'customField' + getRandomInt(),
				required: false,
			}
		);

		await objectFieldsPage.goto(systemObjectDefinition.label.en_US);

		await objectFieldsPage.openObjectField(objectFieldLabel);

		const newObjectFieldLabel = 'newObjectFieldLabel';

		await page
			.frameLocator('iframe')
			.getByLabel('Label')
			.fill(newObjectFieldLabel);

		await page
			.frameLocator('iframe')
			.getByRole('button', {name: 'save'})
			.click();

		await expect(
			page.getByRole('row').filter({hasText: newObjectFieldLabel})
		).toBeVisible();

		await objectFieldsPage.deleteObjectFieldByLabel(newObjectFieldLabel);

		await expect(
			page.getByRole('row').filter({hasText: newObjectFieldLabel})
		).toBeHidden();
	});

	test('can create custom object field in a system object definition', async ({
		objectFieldsPage,
		page,
	}) => {
		await objectFieldsPage.goto('Account');

		const objectFieldLabel = `formula${getRandomInt()}`;

		await objectFieldsPage.addObjectField({
			formulaFieldOutput: 'Integer',
			objectFieldBusinessType: 'Formula',
			objectFieldLabel,
		});

		await expect(page.getByText(objectFieldLabel)).toBeVisible();

		await objectFieldsPage.deleteObjectField(true, -1);
	});

	test('can create object fields of all types', async ({
		apiHelpers,
		objectFieldsPage,
		page,
	}) => {
		const objectDefinition1 =
			await apiHelpers.objectAdmin.postRandomObjectDefinition({
				objectFields: [],
				status: {code: 1},
			});

		const objectDefinition2 =
			await apiHelpers.objectAdmin.postRandomObjectDefinition({
				status: {code: 1},
			});

		apiHelpers.data.push({
			id: objectDefinition1.id,
			type: 'objectDefinition',
		});

		apiHelpers.data.push({
			id: objectDefinition2.id,
			type: 'objectDefinition',
		});

		const objectRelationshipAPIClient = await apiHelpers.buildRestClient(
			ObjectRelationshipAPI
		);

		const {body: objectRelationship} =
			await objectRelationshipAPIClient.postObjectDefinitionObjectRelationship(
				objectDefinition1.id,
				{
					label: {
						en_US: 'objectRelationshipLabel' + getRandomInt(),
					},
					name:
						'objectRelationshipName' +
						Math.floor(Math.random() * 99),
					objectDefinitionExternalReferenceCode1:
						objectDefinition1.externalReferenceCode,
					objectDefinitionExternalReferenceCode2:
						objectDefinition2.externalReferenceCode,
					type: 'oneToMany',
				}
			);

		apiHelpers.data.push({
			id: objectRelationship.id,
			type: 'objectRelationship',
		});

		const listTypeDefinition =
			await apiHelpers.listTypeAdmin.postRandomListTypeDefinition();

		apiHelpers.data.push({
			id: listTypeDefinition.id,
			type: 'listTypeDefinition',
		});

		await objectFieldsPage.goto(objectDefinition1.label['en_US']);

		const objectFieldsMock = [
			{
				objectFieldBusinessType: 'Aggregation',
				objectFieldLabel: `aggregation${getRandomInt()}`,
			},
			{
				objectFieldBusinessType: 'Attachment',
				objectFieldLabel: `attachment${getRandomInt()}`,
			},
			{
				objectFieldBusinessType: 'Auto Increment',
				objectFieldLabel: `autoIncrement${getRandomInt()}`,
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
				objectFieldBusinessType: 'Date Time',
				objectFieldLabel: `dateTime${getRandomInt()}`,
			},
			{
				objectFieldBusinessType: 'Decimal',
				objectFieldLabel: `decimal${getRandomInt()}`,
			},
			{
				objectFieldBusinessType: 'Encrypted',
				objectFieldLabel: `encrypted${getRandomInt()}`,
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

			if (objectFieldBusinessType === 'Aggregation') {
				await objectFieldsPage.addObjectField({
					aggregationFieldFunction: 'count',
					aggregationFieldRelationship:
						objectRelationship.label['en_US'],
					objectFieldBusinessType,
					objectFieldLabel,
				});

				continue;
			}

			if (objectFieldBusinessType === 'Attachment') {
				await objectFieldsPage.addObjectField({
					attachmentSource: 'Upload Directly from the User',
					objectFieldBusinessType,
					objectFieldLabel,
				});

				continue;
			}

			if (objectFieldBusinessType === 'Auto Increment') {
				await objectFieldsPage.addObjectField({
					autoIncrementInitialValue: '1',
					objectFieldBusinessType,
					objectFieldLabel,
				});

				continue;
			}

			if (objectFieldBusinessType === 'Date Time') {
				await objectFieldsPage.addObjectField({
					objectFieldBusinessType: 'Date and Time',
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
					objectFieldBusinessType,
					objectFieldLabel,
				});

				continue;
			}

			await objectFieldsPage.addObjectField({
				objectFieldBusinessType,
				objectFieldLabel,
			});
		}

		await page.getByLabel('Items Per Page').click();
		await page.getByRole('option', {name: '40 Items'}).click();

		while (
			(await page.locator('tbody > tr').all()).length !==
			objectDefinition1.objectFields.length + objectFieldsMock.length
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

	test('cannot delete a custom object relationship field from a system object', async ({
		apiHelpers,
		objectFieldsPage,
		page,
	}) => {
		const objectDefinition1 =
			await apiHelpers.objectAdmin.postRandomObjectDefinition({
				status: {code: 0},
			});

		apiHelpers.data.push({
			id: objectDefinition1.id,
			type: 'objectDefinition',
		});

		const objectDefinitionAPIClient =
			await apiHelpers.buildRestClient(ObjectDefinitionAPI);

		const {body: objectDefinition2} =
			await objectDefinitionAPIClient.getObjectDefinitionByExternalReferenceCode(
				'L_USER'
			);

		const objectRelationshipAPIClient = await apiHelpers.buildRestClient(
			ObjectRelationshipAPI
		);

		const {body: objectRelationship} =
			await objectRelationshipAPIClient.postObjectDefinitionByExternalReferenceCodeObjectRelationship(
				objectDefinition1.externalReferenceCode,
				{
					label: {
						en_US: 'objectRelationshipLabel' + getRandomInt(),
					},
					name: 'objectRelationshipName' + getRandomInt(),
					objectDefinitionExternalReferenceCode1:
						objectDefinition1.externalReferenceCode,
					objectDefinitionExternalReferenceCode2:
						objectDefinition2.externalReferenceCode,
					objectDefinitionId1: objectDefinition1.id,
					objectDefinitionId2: objectDefinition2.id,
					objectDefinitionName2: objectDefinition2.name,
					type: 'oneToMany',
				}
			);

		apiHelpers.data.push({
			id: objectRelationship.id,
			type: 'objectRelationship',
		});

		await objectFieldsPage.goto('User');

		await expect(
			page
				.getByRole('row', {name: objectRelationship.label.en_US})
				.getByRole('button', {name: 'Actions'})
		).toBeHidden();
	});

	test('cannot delete an objectField that belongs to a unique composite key validation through Objects Admin UI', async ({
		apiHelpers,
		objectFieldsPage,
		page,
	}) => {
		const objectDefinition =
			await apiHelpers.objectAdmin.postRandomObjectDefinition({
				status: {code: 0},
			});

		apiHelpers.data.push({
			id: objectDefinition.id,
			type: 'objectDefinition',
		});

		const integerFieldName = 'integerField' + getRandomInt();

		const objectFieldAPIClient =
			await apiHelpers.buildRestClient(ObjectFieldAPI);

		await objectFieldAPIClient.postObjectDefinitionByExternalReferenceCodeObjectField(
			objectDefinition.externalReferenceCode,
			{
				DBType: 'Integer',
				businessType: 'Integer',
				externalReferenceCode: integerFieldName,
				indexed: true,
				indexedAsKeyword: false,
				indexedLanguageId: '',
				label: {en_US: integerFieldName},
				listTypeDefinitionId: 0,
				localized: false,
				name: integerFieldName,
				readOnly: 'false',
				required: false,
				state: false,
				system: false,
			}
		);

		const objectValidationName =
			'Unique Composite Key Object Validation' + getRandomInt();

		const objectValidationRuleAPIClient = await apiHelpers.buildRestClient(
			ObjectValidationRuleAPI
		);

		await objectValidationRuleAPIClient.postObjectDefinitionByExternalReferenceCodeObjectValidationRule(
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
				outputType: 'fullValidation',
				script: '',
				system: false,
			}
		);

		await objectFieldsPage.goto(objectDefinition.label['en_US']);

		await objectFieldsPage.deleteObjectField(false, -1);

		await expect(page.getByText('Deletion Not Allowed')).toBeVisible();
		await expect(
			page.getByText(
				`The object field "${integerFieldName}" cannot be deleted because it is used in a unique composite key validation. To remove this object field, you must first delete the associated unique composite key validation.`
			)
		).toBeVisible();
	});

	test('cannot delete system fields of system objects', async ({
		apiHelpers,
		objectFieldsPage,
		page,
	}) => {
		await objectFieldsPage.goto('User');

		const objectDefinitionAPIClient =
			await apiHelpers.buildRestClient(ObjectDefinitionAPI);

		const {body: objectDefinition} =
			await objectDefinitionAPIClient.getObjectDefinitionByExternalReferenceCode(
				'L_USER'
			);

		const systemFields = objectDefinition.objectFields.filter((item) => {
			if (item.system) {
				return item;
			}
		});

		for (const item of systemFields) {
			await expect(
				page
					.getByRole('row', {name: item.label.en_US})
					.getByRole('button', {name: 'Actions'})
			).toBeHidden();
		}
	});

	test('can only edit external reference code of custom fields through the UI', async ({
		apiHelpers,
		objectFieldsPage,
		page,
	}) => {
		const objectDefinition =
			await apiHelpers.objectAdmin.postRandomObjectDefinition({
				status: {code: 0},
			});

		apiHelpers.data.push({
			id: objectDefinition.id,
			type: 'objectDefinition',
		});

		await objectFieldsPage.goto(objectDefinition.label['en_US']);

		await objectFieldsPage.openObjectField(
			objectDefinition.objectFields[0].label['en_US']
		);

		await expect(
			objectFieldsPage.externalReferenceCodeField
		).toBeDisabled();

		const field = objectDefinition.objectFields.find((item) => {
			return !item.system;
		});

		await objectFieldsPage.openObjectField(field.label['en_US']);

		await objectFieldsPage.externalReferenceCodeField.click();

		const ERCValue = getRandomString();

		await objectFieldsPage.externalReferenceCodeField.fill(ERCValue);

		await objectFieldsPage.editFieldSaveButton.click();

		await waitForAlert(
			page,
			'Success:The object field was updated successfully.'
		);

		await objectFieldsPage.openObjectField(field.label['en_US']);

		await page
			.frameLocator('iframe')
			.getByText('Field')
			.first()
			.waitFor({state: 'visible'});

		expect(objectFieldsPage.externalReferenceCodeField).toHaveValue(
			ERCValue
		);
	});

	test(
		'can delete created custom fields in a System Object',
		{tag: ['@LPD-53450']},
		async ({apiHelpers, objectFieldsPage, page}) => {
			const objectDefinitionField =
				await apiHelpers.buildRestClient(ObjectFieldAPI);

			const fieldName = 'Custom Field';

			const {items} =
				await apiHelpers.objectAdmin.getAllObjectDefinitions();

			const systemObjectDefinition = items.find(
				(item: ObjectDefinition) => {
					return item.system === true;
				}
			);

			await objectDefinitionField.postObjectDefinitionObjectField(
				systemObjectDefinition.id,
				{
					DBType: 'String',
					businessType: 'Text',
					indexed: true,
					label: {en_US: fieldName},
					localized: false,
					name: 'customField',
					readOnly: 'false',
					required: false,
					state: false,
				}
			);

			await objectFieldsPage.goto(systemObjectDefinition.label.en_US);

			await page
				.getByRole('row')
				.filter({hasText: fieldName})
				.getByRole('button', {name: 'Actions'})
				.click();

			await objectFieldsPage.deleteObjectFieldOption.click();

			await page.getByRole('button', {name: 'Delete'}).click();

			await expect(page.locator('.alert-success')).toBeVisible();

			await expect(
				page.getByRole('row').filter({hasText: fieldName})
			).toHaveCount(0);
		}
	);

	test('navigates to documentation from the "unsupported translations" alert link', async ({
		apiHelpers,
		objectFieldsPage,
		page,
	}) => {
		const objectFields = await generateObjectFields({
			objectFieldBusinessTypes: ['Encrypted'],
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

		await objectFieldsPage.goto(objectDefinition.label['en_US']);

		await objectFieldsPage.openObjectField(objectFields[0].label['en_US']);

		const pagePromise = page.waitForEvent('popup');

		await page
			.frameLocator('iframe')
			.getByRole('link', {name: 'Learn more. (Opens a new window)'})
			.click();

		const newPage = await pagePromise;

		await expect(
			newPage.getByRole('heading', {
				name: 'Localizing Object Definitions',
			})
		).toBeVisible();
	});
});
