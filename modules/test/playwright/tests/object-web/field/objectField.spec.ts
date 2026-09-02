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
import {ObjectFieldsPage} from '../../../pages/object-web/object-fields/ObjectFieldsPage';
import {getRandomInt} from '../../../utils/getRandomInt';
import getRandomString from '../../../utils/getRandomString';
import {waitForAlert} from '../../../utils/waitForAlert';
import {waitForSearchToBeReady} from '../../../utils/waitForSearchToBeReady';
import {AsyncArray} from '../utils/AsyncArray';
import {generateObjectFields} from '../utils/generateObjectFields';
import {getFreshObjectRelationshipName} from '../utils/getFreshObjectRelationshipName';
import {postListTypeDefinitionListTypeEntries} from '../utils/postListTypeDefinitionListTypeEntries';

const test = mergeTests(
	apiHelpersTest,
	dataApiHelpersTest,
	loginTest(),
	objectPagesTest
);

const cmsTest = mergeTests(
	test,
	featureFlagsTest({
		'LPD-11235': {enabled: false},
	})
);

cmsTest.describe('Manage object field attachment storage locations', () => {
	cmsTest(
		'can create field with CMS storage types',
		async ({apiHelpers, objectFieldsPage, page}) => {
			const spaceName = `Space ${getRandomString()}`;

			await test.step('Create a new Space', async () => {
				await apiHelpers.headlessAssetLibrary.createAssetLibrary({
					name: spaceName,
					settings: {},
					type: 'Space',
				});
			});

			const objectDefinition =
				await apiHelpers.objectAdmin.postRandomObjectDefinition({
					status: {code: 0},
				});

			apiHelpers.data.push({
				id: objectDefinition.id,
				type: 'objectDefinition',
			});

			await objectFieldsPage.goto(objectDefinition.label.en_US);

			const CMSFilesLabel = 'cmsFiles' + getRandomString();
			const userToCMSLabel = 'userToCMS' + getRandomString();

			await objectFieldsPage.addObjectField({
				attachmentSource:
					"Upload Directly from the User's Computer (CMS Files)",
				objectFieldBusinessType: 'Attachment',
				objectFieldLabel: userToCMSLabel,
			});

			await objectFieldsPage.addObjectField({
				attachmentSource: 'Upload or Select from CMS Files',
				objectFieldBusinessType: 'Attachment',
				objectFieldLabel: CMSFilesLabel,
			});

			await expect(
				page.getByRole('link', {name: userToCMSLabel})
			).toBeVisible();

			await expect(
				page.getByRole('link', {name: CMSFilesLabel})
			).toBeVisible();
		}
	);
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

		const createdListTypeDefinitions = await Promise.all(
			Array(22)
				.fill(null)
				.map(
					async () =>
						await apiHelpers.listTypeAdmin.postRandomListTypeDefinition()
				)
		);

		createdListTypeDefinitions.forEach(({id}) =>
			apiHelpers.data.push({
				id,
				type: 'listTypeDefinition',
			})
		);

		const allListTypeDefinitions = existingListTypeDefinitions.concat(
			createdListTypeDefinitions
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

		await page
			.getByText(objectFields[0].label['en_US'], {exact: true})
			.click();

		const picklistFieldName = 'picklistField' + getRandomInt();

		await expect(page.getByPlaceholder('Text to translate...')).toHaveValue(
			objectFields[0].label['en_US']
		);

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

		await page
			.getByText(objectFields[0].label['en_US'], {exact: true})
			.click();

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

	test(
		'can update custom object field',
		{tag: '@LPD-16778'},
		async ({
			apiHelpers,
			modelBuilderDiagramPage,
			modelBuilderLeftSidebarPage,
			modelBuilderObjectDefinitionNodePage,
			page,
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

			await test.step('Show all fields in the object definition node', async () => {
				await modelBuilderDiagramPage.goto({
					objectFolderName: 'Default',
				});

				await modelBuilderLeftSidebarPage.clickSideBarItem(
					objectDefinition.label['en_US']
				);

				await modelBuilderObjectDefinitionNodePage.clickShowAllFieldsButton(
					objectDefinition.label['en_US'],
					modelBuilderDiagramPage.objectDefinitionNodes
				);
			});

			const objectFieldLabel = objectFields[0].label['en_US'];

			const updatedFieldLabel = 'UpdatedField' + getRandomInt();

			await test.step('Update the field label', async () => {
				await modelBuilderDiagramPage.objectDefinitionNodes
					.filter({hasText: objectDefinition.label['en_US']})
					.getByText(objectFieldLabel, {exact: true})
					.click();

				await expect(page.getByText('LabelMandatory')).toHaveValue(
					objectFieldLabel
				);

				await page.getByText('LabelMandatory').fill(updatedFieldLabel);

				await modelBuilderLeftSidebarPage.clickSideBarItem(
					objectDefinition.label['en_US']
				);
			});

			await test.step('Verify the updated label is visible in the node and the old label is not', async () => {
				await expect(
					modelBuilderDiagramPage.objectDefinitionNodes
						.filter({hasText: objectDefinition.label['en_US']})
						.getByText(updatedFieldLabel, {exact: true})
				).toBeVisible();

				await expect(
					modelBuilderDiagramPage.objectDefinitionNodes
						.filter({hasText: objectDefinition.label['en_US']})
						.getByText(objectFieldLabel, {exact: true})
				).toBeHidden();
			});
		}
	);

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

		await expect(
			page.getByRole('link', {name: 'Learn more.'})
		).toHaveAttribute('href', /localizing-object-definitions-and-entries/);
	});

	test('read only configuration is displayed in the fields advanced tab', async ({
		apiHelpers,
		modelBuilderDiagramPage,
		modelBuilderLeftSidebarPage,
		modelBuilderObjectDefinitionNodePage,
		modelBuilderRightSidebarPage,
		page,
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

		await modelBuilderDiagramPage.goto({objectFolderName: 'Default'});

		await modelBuilderLeftSidebarPage.sidebarItems
			.filter({hasText: objectDefinition.label['en_US']})
			.click();

		await modelBuilderObjectDefinitionNodePage.clickShowAllFieldsButton(
			objectDefinition.label['en_US'],
			modelBuilderDiagramPage.objectDefinitionNodes
		);

		await page
			.getByText(objectFields[0].label.en_US, {exact: true})
			.click();

		await modelBuilderRightSidebarPage.advancedTab.click();

		await expect(
			page.getByRole('button', {name: 'Read Only'})
		).toBeVisible();
	});
});

test.describe('Manage objectFields through Objects Admin UI', () => {
	test(
		'can add blocked domains on email address field',
		{tag: ['@LPD-70673']},
		async ({apiHelpers, objectFieldsPage}) => {
			const objectFieldLabel = `emailAddressField${getRandomInt()}`;
			const blockedDomain = '@gmail.com';

			const objectDefinition =
				await apiHelpers.objectAdmin.postRandomObjectDefinition({
					status: {code: 0},
				});

			apiHelpers.data.push({
				id: objectDefinition.id,
				type: 'objectDefinition',
			});

			await test.step('Navigate and add an Email Address field', async () => {
				await objectFieldsPage.goto(objectDefinition.label.en_US);

				await objectFieldsPage.addObjectField({
					objectFieldBusinessType: 'Email Address',
					objectFieldLabel,
				});
			});

			await test.step('Configure blocked domains in the Advanced tab', async () => {
				await objectFieldsPage.openObjectField(objectFieldLabel);

				await objectFieldsPage.advancedTab.click();

				const input =
					objectFieldsPage.iframeLocator.getByPlaceholder(
						'@example.com'
					);

				await input.fill(blockedDomain);

				await input.press('Enter');

				await objectFieldsPage.saveObjectField();
			});

			await test.step('Verify the blocked domain is saved', async () => {
				await objectFieldsPage.openObjectField(objectFieldLabel);

				await objectFieldsPage.advancedTab.click();

				await expect(
					objectFieldsPage.iframeLocator.getByText(blockedDomain)
				).toBeVisible();
			});
		}
	);

	test('can create and edit a formula field on a custom object', async ({
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

		await objectFieldsPage.addObjectField({
			formulaFieldOutput: 'Decimal',
			objectFieldBusinessType: 'Formula',
			objectFieldLabel: 'Custom Formula Field',
		});

		await expect(
			page.getByRole('link', {name: 'Custom Formula Field'})
		).toBeVisible();

		const objectFieldLabel = 'Custom Formula Field';

		await objectFieldsPage.openObjectField(objectFieldLabel);

		await objectFieldsPage.iframeLocator
			.getByLabel('LabelMandatory')
			.fill(`${objectFieldLabel} Updated`);

		await objectFieldsPage.iframeLocator
			.getByLabel('Expand input area.')
			.click();

		test.step('can see all operations', async () => {
			await expect(
				page.getByRole('button', {name: 'Divided By'})
			).toBeVisible();
			await expect(
				page.getByRole('button', {name: 'Minus'})
			).toBeVisible();
			await expect(
				page.getByRole('button', {name: 'Plus'})
			).toBeVisible();
			await expect(
				page.getByRole('button', {name: 'Times'})
			).toBeVisible();

			await expect(
				page.getByRole('button', {exact: true, name: 'And'})
			).toBeHidden();
			await expect(
				page.getByRole('button', {exact: true, name: 'Or'})
			).toBeHidden();
		});

		await page.getByRole('button', {exact: true, name: 'ID'}).click();

		await page.getByRole('button', {name: 'Plus'}).click();

		await page.getByRole('button', {name: 'Done'}).click();

		await page.waitForTimeout(500);

		await objectFieldsPage.editFieldSaveButton.click();

		await expect(
			page.getByRole('link', {name: `${objectFieldLabel} Updated`})
		).toBeVisible();

		await objectFieldsPage.openObjectField(`${objectFieldLabel} Updated`);

		await expect(
			objectFieldsPage.iframeLocator.getByPlaceholder(
				'Create an expression.'
			)
		).toHaveValue('idfield_name1 + field_name2');
	});

	test('can create a formula field on a custom object', async ({
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

		await objectFieldsPage.addObjectField({
			formulaFieldOutput: 'Decimal',
			objectFieldBusinessType: 'Formula',
			objectFieldLabel: 'Custom Formula Field',
		});

		await expect(
			page.getByRole('link', {name: 'Custom Formula Field'})
		).toBeVisible();

		const fieldRow = page
			.getByRole('row')
			.filter({hasText: 'Custom Formula Field'});

		await expect(
			fieldRow.getByText('Formula', {exact: true})
		).toBeVisible();
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

		await expect(
			page.getByRole('link', {name: objectFieldLabel})
		).toBeVisible();

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
					name: await getFreshObjectRelationshipName(apiHelpers, [
						objectDefinition1.externalReferenceCode!,
						objectDefinition2.externalReferenceCode!,
					]),
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
				objectFieldBusinessType: 'Email Address',
				objectFieldLabel: `emailAddress${getRandomInt()}`,
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
				objectFieldBusinessType: 'Phone Number',
				objectFieldLabel: `phoneNumber${getRandomInt()}`,
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
					attachmentSource:
						"Upload Directly from the User's Computer (Documents and Media)",
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

	test('can delete an aggregation field', async ({
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

		const objectRelationshipAPIClient = await apiHelpers.buildRestClient(
			ObjectRelationshipAPI
		);

		const objectRelationshipName = await getFreshObjectRelationshipName(
			apiHelpers,
			[objectDefinition.externalReferenceCode!]
		);

		await objectRelationshipAPIClient.postObjectDefinitionByExternalReferenceCodeObjectRelationship(
			objectDefinition.externalReferenceCode!,
			{
				label: {en_US: 'Relationship'},
				name: objectRelationshipName,
				objectDefinitionExternalReferenceCode2:
					objectDefinition.externalReferenceCode,
				objectDefinitionId2: objectDefinition.id,
				objectDefinitionName2: objectDefinition.name,
				type: 'oneToMany',
			}
		);

		await objectFieldsPage.goto(objectDefinition.label['en_US']);

		const objectFieldLabel = 'Custom Aggregation';

		await objectFieldsPage.addObjectField({
			aggregationFieldFunction: 'Count',
			aggregationFieldRelationship: 'Relationship',
			objectFieldBusinessType: 'Aggregation',
			objectFieldLabel,
		});

		await expect(
			page.getByRole('link', {name: objectFieldLabel})
		).toBeVisible();

		await objectFieldsPage.deleteObjectFieldByLabel(objectFieldLabel);

		await waitForAlert(
			page,
			`Success:${objectFieldLabel} was deleted successfully.`
		);

		await expect(
			page.getByRole('link', {name: objectFieldLabel})
		).toBeHidden();
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

			const accountObjectDefinition = items.find(
				(item: ObjectDefinition) => {
					return item.externalReferenceCode === 'L_ACCOUNT';
				}
			);

			await objectDefinitionField.postObjectDefinitionObjectField(
				accountObjectDefinition.id,
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

			await objectFieldsPage.goto(accountObjectDefinition.label.en_US);

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

	test('can edit an aggregation field', async ({
		apiHelpers,
		objectFieldsPage,
		page,
	}) => {
		const objectDefinition =
			await apiHelpers.objectAdmin.postRandomObjectDefinition({
				status: {code: 0},
			});

		const objectDefinition2 =
			await apiHelpers.objectAdmin.postRandomObjectDefinition({
				status: {code: 0},
			});

		apiHelpers.data.push({
			id: objectDefinition.id,
			type: 'objectDefinition',
		});

		const objectRelationshipAPIClient = await apiHelpers.buildRestClient(
			ObjectRelationshipAPI
		);

		const objectRelationship1 = await getFreshObjectRelationshipName(
			apiHelpers,
			[objectDefinition.externalReferenceCode!],
			'objectRelationship'
		);

		await objectRelationshipAPIClient.postObjectDefinitionByExternalReferenceCodeObjectRelationship(
			objectDefinition.externalReferenceCode!,
			{
				label: {en_US: objectRelationship1},
				name: objectRelationship1,
				objectDefinitionExternalReferenceCode2:
					objectDefinition.externalReferenceCode,
				objectDefinitionId2: objectDefinition.id,
				objectDefinitionName2: objectDefinition.name,
				type: 'oneToMany',
			}
		);

		const objectRelationship2 = await getFreshObjectRelationshipName(
			apiHelpers,
			[
				objectDefinition.externalReferenceCode!,
				objectDefinition2.externalReferenceCode!,
			],
			'objectRelationship'
		);

		await objectRelationshipAPIClient.postObjectDefinitionByExternalReferenceCodeObjectRelationship(
			objectDefinition.externalReferenceCode!,
			{
				label: {en_US: objectRelationship2},
				name: objectRelationship2,
				objectDefinitionExternalReferenceCode2:
					objectDefinition2.externalReferenceCode,
				objectDefinitionId2: objectDefinition2.id,
				objectDefinitionName2: objectDefinition2.name,
				type: 'oneToMany',
			}
		);

		await objectFieldsPage.goto(objectDefinition.label['en_US']);

		const objectFieldLabel = 'Custom Aggregation';

		await objectFieldsPage.addObjectField({
			aggregationField: 'ID',
			aggregationFieldFunction: 'Max',
			aggregationFieldRelationship: objectRelationship1,
			objectFieldBusinessType: 'Aggregation',
			objectFieldLabel,
		});

		await objectFieldsPage.openObjectField(objectFieldLabel);

		await objectFieldsPage.iframeLocator
			.getByLabel('LabelMandatory')
			.fill(`${objectFieldLabel} Updated`);

		await objectFieldsPage.iframeLocator
			.getByLabel('RelationshipMandatory')
			.click();

		await objectFieldsPage.iframeLocator
			.getByRole('option', {name: objectRelationship2})
			.click();

		await objectFieldsPage.iframeLocator
			.getByLabel('FunctionMandatory')
			.click();

		await objectFieldsPage.iframeLocator
			.getByRole('option', {name: 'Min'})
			.click();

		await objectFieldsPage.iframeLocator
			.getByLabel('FieldMandatory')
			.click();

		await objectFieldsPage.iframeLocator
			.getByRole('option', {name: 'ID'})
			.click();

		await objectFieldsPage.editFieldSaveButton.click();

		await expect(
			page.getByRole('link', {name: `${objectFieldLabel} Updated`})
		).toBeVisible();
	});

	test(
		'can edit the country source and country for a phone number field',
		{tag: ['@LPD-83570']},
		async ({apiHelpers, objectFieldsPage}) => {
			let objectDefinition: ObjectDefinition;
			let selectedCountry: string;
			let selectedCountrySource: string;

			const objectFieldLabel = `phoneNumber${getRandomInt()}`;

			await test.step('Create required definitions', async () => {
				const listTypeDefinition =
					await apiHelpers.listTypeAdmin.postRandomListTypeDefinition();

				apiHelpers.data.push({
					id: listTypeDefinition.id,
					type: 'listTypeDefinition',
				});

				objectDefinition =
					await apiHelpers.objectAdmin.postRandomObjectDefinition({
						status: {code: 0},
					});

				apiHelpers.data.push({
					id: objectDefinition.id,
					type: 'objectDefinition',
				});
			});

			await test.step('Navigate to the object definition and add a phone number field', async () => {
				await objectFieldsPage.goto(objectDefinition.label!['en_US']);

				await objectFieldsPage.addObjectField({
					objectFieldBusinessType: 'Phone Number',
					objectFieldLabel,
				});
			});

			await test.step('Edit the country source and country for the phone number field', async () => {
				await objectFieldsPage.openObjectField(objectFieldLabel);

				await objectFieldsPage.countrySourceDropdown.click();

				const countrySourceOption =
					objectFieldsPage.iframeLocator.getByRole('option', {
						exact: true,
						name: 'Fixed',
					});

				selectedCountrySource = await countrySourceOption.innerText();

				await countrySourceOption.click();

				await objectFieldsPage.countryPicker.click();

				const countryOption = objectFieldsPage.iframeLocator
					.getByRole('option')
					.nth(1);

				await countryOption.click();

				selectedCountry =
					await objectFieldsPage.countryPicker.innerText();

				await objectFieldsPage.saveObjectField();
			});

			await test.step('Verify the updated country source and country are saved', async () => {
				await objectFieldsPage.openObjectField(objectFieldLabel);

				await expect(objectFieldsPage.countrySourceDropdown).toHaveText(
					selectedCountrySource
				);

				await expect(objectFieldsPage.countryPicker).toHaveText(
					selectedCountry
				);
			});
		}
	);

	test(
		'can enable autocomplete and add domains on an email address field',
		{tag: ['@LPD-70673']},
		async ({apiHelpers, objectFieldsPage}) => {
			const objectFieldLabel = `emailAddressField${getRandomInt()}`;
			const autocompleteDomain = '@liferay.com';

			const objectDefinition =
				await apiHelpers.objectAdmin.postRandomObjectDefinition({
					status: {code: 0},
				});

			apiHelpers.data.push({
				id: objectDefinition.id,
				type: 'objectDefinition',
			});

			await test.step('Navigate and add an Email Address field', async () => {
				await objectFieldsPage.goto(objectDefinition.label.en_US);

				await objectFieldsPage.addObjectField({
					objectFieldBusinessType: 'Email Address',
					objectFieldLabel,
				});
			});

			await test.step('Enable autocomplete and add a domain in the Advanced tab', async () => {
				await objectFieldsPage.openObjectField(objectFieldLabel);

				await objectFieldsPage.advancedTab.click();

				await objectFieldsPage.iframeLocator
					.getByRole('switch', {name: 'Enable Autocomplete'})
					.check();

				const input = objectFieldsPage.iframeLocator
					.getByPlaceholder('@liferay.com')
					.last();

				await input.fill(autocompleteDomain);

				await input.press('Enter');

				await objectFieldsPage.saveObjectField();
			});

			await test.step('Verify autocomplete is enabled and the domain is saved', async () => {
				await objectFieldsPage.openObjectField(objectFieldLabel);

				await objectFieldsPage.advancedTab.click();

				await expect(
					objectFieldsPage.iframeLocator.getByRole('switch', {
						name: 'Enable Autocomplete',
					})
				).toBeChecked();

				await expect(
					objectFieldsPage.iframeLocator.getByText(autocompleteDomain)
				).toBeVisible();
			});
		}
	);

	test('can only edit external reference code of custom fields through the UI', async ({
		apiHelpers,
		objectFieldsPage,
	}) => {
		let objectDefinition: ObjectDefinition;

		await test.step('Create an object definition', async () => {
			objectDefinition =
				await apiHelpers.objectAdmin.postRandomObjectDefinition({
					status: {code: 0},
				});

			apiHelpers.data.push({
				id: objectDefinition.id,
				type: 'objectDefinition',
			});
		});

		await test.step('Go to the object definition fields page', async () => {
			await objectFieldsPage.goto(objectDefinition.label['en_US']);
		});

		const systemField = objectDefinition.objectFields.find((item) => {
			return item.system;
		});

		await test.step(`Check that the ERC input is disabled for a system field (${systemField.label['en_US']}) `, async () => {
			await objectFieldsPage.openObjectField(systemField.label['en_US']);

			await expect(
				objectFieldsPage.externalReferenceCodeField
			).toBeDisabled();
		});

		const customField = objectDefinition.objectFields.find((item) => {
			return !item.system;
		});

		const newERCValue = getRandomString();

		await test.step(`Edit the ERC of a custom field (${customField.label['en_US']})`, async () => {
			await objectFieldsPage.openObjectField(customField.label['en_US']);

			await objectFieldsPage.externalReferenceCodeField.click();

			await objectFieldsPage.externalReferenceCodeField.fill(newERCValue);

			await objectFieldsPage.saveObjectField();
		});

		await test.step('Verify that the ERC was updated', async () => {
			await objectFieldsPage.openObjectField(customField.label['en_US']);

			expect(objectFieldsPage.externalReferenceCodeField).toHaveValue(
				newERCValue
			);
		});
	});

	test('can update custom object field in a system object', async ({
		apiHelpers,
		objectFieldsPage,
		page,
	}) => {
		const {items} = await apiHelpers.objectAdmin.getAllObjectDefinitions();

		const accountObjectDefinition = items.find((item: ObjectDefinition) => {
			return item.externalReferenceCode === 'L_ACCOUNT';
		});

		const objectFieldAPIClient =
			await apiHelpers.buildRestClient(ObjectFieldAPI);

		const objectFieldLabel = 'objectFieldLabel';

		await objectFieldAPIClient.postObjectDefinitionObjectField(
			accountObjectDefinition.id,
			{
				DBType: 'String',
				businessType: 'Text',
				label: {en_US: objectFieldLabel},
				name: 'customField' + getRandomInt(),
				required: false,
			}
		);

		await objectFieldsPage.goto(accountObjectDefinition.label.en_US);

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

	test(
		'can update relationship field label on system object',
		{tag: '@LPS-135406'},
		async ({apiHelpers, objectFieldsPage, page}) => {
			const objectDefinition =
				await apiHelpers.objectAdmin.postRandomObjectDefinition({
					status: {code: 0},
				});

			apiHelpers.data.push({
				id: objectDefinition.id,
				type: 'objectDefinition',
			});

			const objectRelationshipAPIClient =
				await apiHelpers.buildRestClient(ObjectRelationshipAPI);

			const relationshipLabel = 'Relationship' + getRandomInt();

			const {body: objectRelationship} =
				await objectRelationshipAPIClient.postObjectDefinitionByExternalReferenceCodeObjectRelationship(
					'L_USER',
					{
						label: {en_US: relationshipLabel},
						name: await getFreshObjectRelationshipName(
							apiHelpers,
							['L_USER', objectDefinition.externalReferenceCode!],
							'relationship'
						),
						objectDefinitionExternalReferenceCode2:
							objectDefinition.externalReferenceCode,
						objectDefinitionId2: objectDefinition.id,
						objectDefinitionName2: objectDefinition.name,
						type: 'oneToMany',
					}
				);

			apiHelpers.data.push({
				id: objectRelationship.id,
				type: 'objectRelationship',
			});

			await objectFieldsPage.goto(objectDefinition.label['en_US']);

			await objectFieldsPage.openObjectField(relationshipLabel);

			const iframeLocator = page.frameLocator('iframe');
			const newLabel = 'New Relationship' + getRandomInt();

			await iframeLocator.getByLabel('LabelMandatory').clear();
			await iframeLocator.getByLabel('LabelMandatory').fill(newLabel);

			await objectFieldsPage.editFieldSaveButton.click();

			await waitForAlert(
				page,
				'The object field was updated successfully'
			);

			await page.reload();

			await expect(page.getByText(newLabel)).toBeVisible();
		}
	);

	test(
		'can view description for each field business type',
		{tag: '@LPD-93353'},
		async ({apiHelpers, objectFieldsPage, page}) => {
			const objectDefinition =
				await apiHelpers.objectAdmin.postRandomObjectDefinition({
					status: {code: 0},
				});

			apiHelpers.data.push({
				id: objectDefinition.id,
				type: 'objectDefinition',
			});

			await objectFieldsPage.goto(objectDefinition.label.en_US);

			await objectFieldsPage.addObjectFieldButton.click();

			await objectFieldsPage.objectFieldLabelInput.waitFor();

			await objectFieldsPage.objectFieldOptionsDropdown.click();

			const fieldTypeDescriptions = [
				{
					description: 'Assign the entry to a user or role.',
					type: 'Assignee',
				},
				{
					description:
						'Upload files or select from Documents and Media.',
					type: 'Attachment',
				},
				{
					description:
						'Automatically generate a unique value when a new entry is added.',
					type: 'Auto Increment',
				},
				{
					description: 'Select between true or false.',
					type: 'Boolean',
				},
				{description: 'Add a date.', type: 'Date'},
				{
					description: 'Add date and time values.',
					type: 'Date and Time',
				},
				{
					description:
						'Add a decimal number that supports fractional portions.',
					type: 'Decimal',
				},
				{
					description: 'Store content as encrypted text.',
					type: 'Encrypted',
				},
				{
					description:
						'Add an algorithm that derives its value from other fields.',
					type: 'Formula',
				},
				{
					description: 'Add an integer up to nine digits.',
					type: 'Integer',
				},
				{
					description: 'Add a long integer up to 16 digits.',
					type: 'Long Integer',
				},
				{
					description: 'Add text up to 65,000 characters.',
					type: 'Long Text',
				},
				{
					description: 'Choose from a picklist.',
					type: 'Picklist',
				},
				{
					description:
						'Add a high precision decimal number without rounding.',
					type: 'Precision Decimal',
				},
				{
					description: 'Create rich text content.',
					type: 'Rich Text',
				},
				{
					description: 'Add text up to 280 characters.',
					type: 'Text',
				},
			];

			for (const {description, type} of fieldTypeDescriptions) {
				await expect(
					page.getByRole('option', {exact: true, name: type})
				).toContainText(description);
			}
		}
	);

	test('can view more than 20 picklists in the picklist drop-down', async ({
		apiHelpers,
		objectFieldsPage,
		page,
	}) => {
		const listTypeDefinitions = await Promise.all(
			Array(21)
				.fill(null)
				.map(async () => {
					const listTypeDef =
						await apiHelpers.listTypeAdmin.postRandomListTypeDefinition();

					apiHelpers.data.push({
						id: listTypeDef.id,
						type: 'listTypeDefinition',
					});

					return listTypeDef;
				})
		);

		const objectDefinition =
			await apiHelpers.objectAdmin.postRandomObjectDefinition({
				status: {code: 0},
			});

		apiHelpers.data.push({
			id: objectDefinition.id,
			type: 'objectDefinition',
		});

		await objectFieldsPage.goto(objectDefinition.label['en_US']);

		await objectFieldsPage.addObjectFieldButton.click();

		await objectFieldsPage.objectFieldLabelInput.fill('Field Picklist');

		await objectFieldsPage.objectFieldOptionsDropdown.click();

		await page.getByRole('option', {exact: true, name: 'Picklist'}).click();

		await objectFieldsPage.objectFieldOptionsDropdown.click();

		for (const listTypeDef of listTypeDefinitions) {
			await expect(
				page.getByRole('option', {name: listTypeDef.name})
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
					name: await getFreshObjectRelationshipName(apiHelpers, [
						objectDefinition1.externalReferenceCode!,
						objectDefinition2.externalReferenceCode!,
					]),
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

	test('cannot edit external reference code from system fields', async ({
		apiHelpers,
		objectFieldsPage,
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

		await objectFieldsPage.openObjectField('External Reference Code');

		await expect(
			objectFieldsPage.iframeLocator.locator('input[name="name"]')
		).toBeDisabled();

		await expect(
			objectFieldsPage.iframeLocator.getByRole('combobox', {
				name: 'Type Mandatory',
			})
		).toBeDisabled();
	});

	test('navigates to documentation from the "unsupported translations" alert link', async ({
		apiHelpers,
		objectFieldsPage,
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

		await objectFieldsPage.goto(objectDefinition.label['en_US']);

		await objectFieldsPage.openObjectField(objectFields[0].label['en_US']);

		await expect(
			page.frameLocator('iframe').getByRole('link', {name: 'Learn more.'})
		).toHaveAttribute('href', /localizing-object-definitions-and-entries/);
	});
});

test.describe('Create Object Fields', () => {
	let createdObjectDefinition: ObjectDefinition;
	let createdObjectField: ObjectField;

	test.beforeEach(async ({apiHelpers, objectFieldsPage}) => {
		const objectFields = generateObjectFields({
			objectFieldBusinessTypes: ['Text'],
		});

		const objectDefinition =
			await apiHelpers.objectAdmin.postRandomObjectDefinition({
				objectFields,
				status: {code: 2},
			});

		apiHelpers.data.push({
			id: objectDefinition.id,
			type: 'objectDefinition',
		});

		createdObjectDefinition = objectDefinition;
		createdObjectField = objectFields[0];

		await objectFieldsPage.goto(objectDefinition.label['en_US']);
	});

	async function assertSearchableProperties(
		objectFieldsPage: ObjectFieldsPage,
		visible: boolean
	) {
		await expect(
			objectFieldsPage.iframeLocator.getByRole('radio', {
				name: 'Keyword',
			})
		).toBeVisible({visible});
		await expect(
			objectFieldsPage.iframeLocator.getByText('Language')
		).toBeVisible({visible});
		await expect(
			objectFieldsPage.iframeLocator.getByRole('radio', {
				name: 'Text',
			})
		).toBeVisible({visible});
	}

	test('Verify it is not possible to add a custom object field when required properties are missing', async ({
		objectFieldsPage,
		page,
	}) => {
		await objectFieldsPage.addObjectFieldButton.click();

		await test.step('Verify required error is shown three times when Label, Name and Type are blank', async () => {
			await objectFieldsPage.saveButton.click();

			await expect(page.getByText('Required')).toHaveCount(3);
		});

		await test.step('Verify required error is shown two times after filling the Name', async () => {
			await objectFieldsPage.objectFieldNameInput.fill('testField');

			await objectFieldsPage.saveButton.click();

			await expect(page.getByText('Required')).toHaveCount(2);
		});

		await test.step('Verify required error is shown one time after filling the Label', async () => {
			await objectFieldsPage.objectFieldLabelInput.fill('Test Field');

			await objectFieldsPage.saveButton.click();

			await expect(page.getByText('Required')).toHaveCount(1);
		});

		await test.step('Verify required error is shown for the picklist when the Type is changed to Picklist', async () => {
			await objectFieldsPage.objectFieldOptionsDropdown.click();
			await page
				.getByRole('option', {exact: true, name: 'Picklist'})
				.click();

			await objectFieldsPage.saveButton.click();

			await expect(page.getByText('Required')).toHaveCount(1);
		});
	});

	test('Verify it is not possible to add custom object field with invalid name', async ({
		objectFieldsPage,
		page,
	}) => {
		await test.step('Verify that Name is autofilled when Label is filled', async () => {
			await objectFieldsPage.addObjectFieldButton.click();

			await objectFieldsPage.objectFieldLabelInput.fill('Test Field');

			await expect(objectFieldsPage.objectFieldNameInput).toHaveValue(
				'testField'
			);
		});

		await test.step('Verify it is not possible to save Name with special characters', async () => {
			await objectFieldsPage.objectFieldNameInput.fill('Field@Special!');

			await objectFieldsPage.objectFieldOptionsDropdown.click();
			await page.getByRole('option', {exact: true, name: 'Text'}).click();

			await objectFieldsPage.saveButton.click();

			await expect(
				page.getByText('Name must only contain letters and digits.')
			).toBeVisible();
		});

		await test.step('Verify it is not possible to save Name that begin with uppercase letter', async () => {
			await objectFieldsPage.objectFieldNameInput.fill('FieldUpperCase');

			await objectFieldsPage.saveButton.click();

			await expect(
				page.getByText(
					'The first character of a name must be a lowercase letter.'
				)
			).toBeVisible();
		});

		await test.step('Verify it is not possible to save Duplicated name', async () => {
			await objectFieldsPage.objectFieldNameInput.fill(
				createdObjectField.name
			);

			await objectFieldsPage.saveButton.click();

			await expect(
				page.getByText('This name is already in use. Try another one.')
			).toBeVisible();
		});
	});

	test('Verify it is possible to cancel the creation of a custom object field', async ({
		objectFieldsPage,
		page,
	}) => {
		await objectFieldsPage.addObjectFieldButton.click();

		await objectFieldsPage.objectFieldLabelInput.fill('Cancel Field');

		await page.getByRole('button', {name: 'Cancel'}).click();

		await expect(objectFieldsPage.objectFieldLabelInput).toBeHidden();

		const searchInput = page
			.getByRole('search')
			.getByRole('searchbox', {name: 'Search'});

		await searchInput.fill('Cancel Field');

		await waitForSearchToBeReady(page);

		await searchInput.press('Enter');

		await expect(page.getByText('No Results Found')).toBeVisible();
	});

	test('Verify it is possible to delete a custom object field depending on the object state', async ({
		apiHelpers,
		objectFieldsPage,
		page,
	}) => {
		await test.step('Verify it is possible to delete the only custom object field before the object is published', async () => {
			await objectFieldsPage.deleteObjectField(false, -1);
		});

		await test.step('Verify it is not possible to delete the only custom object field after the object is published', async () => {
			await objectFieldsPage.addObjectField({
				objectFieldBusinessType: 'Text',
				objectFieldLabel: 'Text1',
			});

			await apiHelpers.objectAdmin.postObjectDefinitionPublish({
				objectDefinitionId: createdObjectDefinition.id,
			});

			await objectFieldsPage.deleteObjectField(false, -1);

			await expect(page.getByText('Deletion Not Allowed')).toBeVisible();
			await expect(
				page.getByText(
					`The object field "Text1" cannot be deleted because it is the only custom object field of the published object definition.`
				)
			).toBeVisible();

			await page.getByRole('button', {name: 'Done'}).click();
		});

		await test.step('Verify it is possible to delete a custom object field when it is not the only one after the object is published', async () => {
			await objectFieldsPage.addObjectField({
				objectFieldBusinessType: 'Text',
				objectFieldLabel: 'Text2',
			});

			await objectFieldsPage.deleteObjectField(true, -1);

			await expect(
				page.getByRole('row').filter({hasText: 'Text1'})
			).toBeVisible();
			await expect(
				page.getByRole('row').filter({hasText: 'Text2'})
			).toBeHidden();
		});
	});

	test('Verify it is possible to update field properties depending on the object state', async ({
		apiHelpers,
		objectFieldsPage,
		page,
	}) => {
		await test.step('Before the object is published', async () => {
			await test.step('Update Label with translation', async () => {
				await objectFieldsPage.openObjectField(
					createdObjectField.label!['en_US']
				);

				await objectFieldsPage.iframeLocator
					.getByLabel('Label')
					.fill('Updated Label');

				await objectFieldsPage.iframeLocator
					.getByTitle('en_US')
					.click();
				await objectFieldsPage.iframeLocator
					.getByRole('option', {name: 'pt_BR'})
					.click();

				await objectFieldsPage.iframeLocator
					.getByLabel('Label')
					.fill('Rótulo Atualizado');
			});

			await test.step('Verify that Keyword, Language, and Text fields are not visible for non-searchable Text field', async () => {
				await assertSearchableProperties(objectFieldsPage, false);
			});

			await test.step('Update Mandatory, Name and Searchable', async () => {
				await objectFieldsPage.iframeLocator
					.getByRole('switch', {name: 'Mandatory'})
					.check();
				await objectFieldsPage.iframeLocator
					.locator('input[name="name"]')
					.fill('updatedName');
				await objectFieldsPage.iframeLocator
					.getByRole('switch', {name: 'Searchable'})
					.check();
			});

			await test.step('Verify that Keyword, Language, and Text fields are visible for searchable Text field', async () => {
				await assertSearchableProperties(objectFieldsPage, true);
			});

			await test.step('Update type to Integer', async () => {
				await objectFieldsPage.iframeLocator
					.getByRole('combobox', {name: 'Type'})
					.click();
				await objectFieldsPage.iframeLocator
					.getByRole('option', {exact: true, name: 'Integer'})
					.click();
			});

			await test.step('Verify that Keyword, Language, and Text fields are not visible for searchable Integer field', async () => {
				await assertSearchableProperties(objectFieldsPage, false);
			});

			await objectFieldsPage.editFieldSaveButton.click();

			await waitForAlert(
				page,
				'The object field was updated successfully'
			);

			await test.step('Verify that Label and Type columns are displayed for the updated field', async () => {
				await page
					.getByRole('search')
					.getByRole('searchbox', {name: 'Search'})
					.fill('Updated Label');
				await page.keyboard.press('Enter');

				await expect(
					page
						.getByRole('row')
						.filter({hasText: 'Updated Label'})
						.getByText('Integer')
				).toBeVisible();
			});

			await test.step('Verify that translated Label is updated', async () => {
				await objectFieldsPage.openObjectField('Updated Label');

				await objectFieldsPage.iframeLocator
					.getByTitle('en_US')
					.click();
				await objectFieldsPage.iframeLocator
					.getByRole('option', {name: 'pt_BR'})
					.click();

				await expect(
					objectFieldsPage.iframeLocator.getByLabel('Label')
				).toHaveValue('Rótulo Atualizado');
			});

			await test.step('Verify that Mandatory, Name, Searchable and Type are updated', async () => {
				await expect(
					objectFieldsPage.iframeLocator.getByRole('switch', {
						name: 'Mandatory',
					})
				).toBeChecked();
				await expect(
					objectFieldsPage.iframeLocator.locator('input[name="name"]')
				).toHaveValue('updatedName');
				await expect(
					objectFieldsPage.iframeLocator.getByRole('switch', {
						name: 'Searchable',
					})
				).toBeChecked();
				await expect(
					objectFieldsPage.iframeLocator.getByRole('combobox', {
						name: 'Type',
					})
				).toHaveText('Integer');
			});
		});

		await test.step('After the object is published', async () => {
			await apiHelpers.objectAdmin.postObjectDefinitionPublish({
				objectDefinitionId: createdObjectDefinition.id,
			});

			await page.reload();

			await test.step('Update Label with translation', async () => {
				await objectFieldsPage.openObjectField('Updated Label');

				await objectFieldsPage.iframeLocator
					.getByLabel('Label')
					.fill('New Updated Label');

				await objectFieldsPage.iframeLocator
					.getByTitle('en_US')
					.click();
				await objectFieldsPage.iframeLocator
					.getByRole('option', {name: 'pt_BR'})
					.click();

				await objectFieldsPage.iframeLocator
					.getByLabel('Label')
					.fill('Novo Rótulo Atualizado');
			});

			await test.step('Uncheck Mandatory and Searchable fields', async () => {
				await objectFieldsPage.iframeLocator
					.getByRole('switch', {name: 'Mandatory'})
					.uncheck();
				await objectFieldsPage.iframeLocator
					.getByRole('switch', {name: 'Searchable'})
					.uncheck();
			});

			await test.step('Verify it is not possible to update Name and Type fields', async () => {
				await expect(
					objectFieldsPage.iframeLocator.locator('input[name="name"]')
				).toBeDisabled();
				await expect(
					objectFieldsPage.iframeLocator.getByRole('combobox', {
						name: 'Type',
					})
				).toBeDisabled();
			});

			await objectFieldsPage.editFieldSaveButton.click();

			await waitForAlert(
				page,
				'The object field was updated successfully'
			);

			await test.step('Verify that translated Label is updated', async () => {
				await objectFieldsPage.openObjectField('New Updated Label');

				await objectFieldsPage.iframeLocator
					.getByTitle('en_US')
					.click();
				await objectFieldsPage.iframeLocator
					.getByRole('option', {name: 'pt_BR'})
					.click();

				await expect(
					objectFieldsPage.iframeLocator.getByLabel('Label')
				).toHaveValue('Novo Rótulo Atualizado');
			});

			await test.step('Verify that Mandatory is unchecked and disabled', async () => {
				await expect(
					objectFieldsPage.iframeLocator.getByRole('switch', {
						name: 'Mandatory',
					})
				).not.toBeChecked();
				await expect(
					objectFieldsPage.iframeLocator.getByRole('switch', {
						name: 'Mandatory',
					})
				).toBeDisabled();
			});

			await test.step('Verify that Searchable is unchecked', async () => {
				await expect(
					objectFieldsPage.iframeLocator.getByRole('switch', {
						name: 'Searchable',
					})
				).not.toBeChecked();
				await expect(
					objectFieldsPage.iframeLocator.getByRole('switch', {
						name: 'Searchable',
					})
				).toBeEnabled();
			});
		});
	});
});

test.describe('Manage object fields default value properties', () => {
	test(
		'can create, update, and delete default value for boolean field through Model Builder',
		{tag: ['@LPD-70980']},
		async ({
			apiHelpers,
			modelBuilderDiagramPage,
			modelBuilderObjectDefinitionNodePage,
			modelBuilderRightSidebarPage,
			page,
			viewObjectEntriesPage,
		}) => {
			let booleanFieldName: string;

			let objectClassName: string;

			let objectName: string;

			let objectFolderName: string;

			await test.step('create object with boolean field', async () => {
				const objectFields = generateObjectFields({
					objectFieldBusinessTypes: ['Boolean'],
				});

				booleanFieldName = objectFields[0].label['en_US'];

				const objectFolder =
					await apiHelpers.objectAdmin.postRandomObjectFolder();

				apiHelpers.data.push({
					id: objectFolder.id,
					type: 'objectFolder',
				});

				objectFolderName = objectFolder.name;

				const objectDefinition =
					await apiHelpers.objectAdmin.postRandomObjectDefinition({
						objectFields,
						objectFolderExternalReferenceCode:
							objectFolder.externalReferenceCode,
						status: {code: 0},
					});

				objectClassName = objectDefinition.className;

				objectName = objectDefinition.name;

				apiHelpers.data.push({
					id: objectDefinition.id,
					type: 'objectDefinition',
				});
			});

			await test.step('set default value to false for boolean field and check in object entry', async () => {
				await modelBuilderDiagramPage.goto({
					objectFolderName,
				});

				await modelBuilderObjectDefinitionNodePage.clickShowAllFieldsButton(
					objectName,
					modelBuilderDiagramPage.objectDefinitionNodes
				);

				await modelBuilderDiagramPage.objectDefinitionNodes
					.filter({hasText: objectName})
					.getByText('Boolean', {exact: true})
					.dispatchEvent('click');

				await modelBuilderRightSidebarPage.setDefaultValue(
					'Boolean',
					'False'
				);

				await viewObjectEntriesPage.goto(objectClassName);

				await viewObjectEntriesPage.clickAddObjectEntry(objectName);

				await expect(
					page.getByLabel(booleanFieldName)
				).not.toBeChecked();
			});

			await test.step('set default value to true for boolean field and check in object entry', async () => {
				await modelBuilderDiagramPage.goto({
					objectFolderName,
				});

				await modelBuilderObjectDefinitionNodePage.clickShowAllFieldsButton(
					objectName,
					modelBuilderDiagramPage.objectDefinitionNodes
				);

				await modelBuilderDiagramPage.objectDefinitionNodes
					.filter({hasText: objectName})
					.getByText('Boolean', {exact: true})
					.dispatchEvent('click');

				await modelBuilderRightSidebarPage.setDefaultValue(
					'Boolean',
					'True'
				);

				await viewObjectEntriesPage.goto(objectClassName);

				await viewObjectEntriesPage.clickAddObjectEntry(objectName);

				await expect(page.getByLabel(booleanFieldName)).toBeChecked();
			});

			await test.step('untoggle default value for boolean field and check in object entry', async () => {
				await modelBuilderDiagramPage.goto({
					objectFolderName,
				});

				await modelBuilderObjectDefinitionNodePage.clickShowAllFieldsButton(
					objectName,
					modelBuilderDiagramPage.objectDefinitionNodes
				);

				await modelBuilderDiagramPage.objectDefinitionNodes
					.filter({hasText: objectName})
					.getByText('Boolean', {exact: true})
					.dispatchEvent('click');

				await modelBuilderRightSidebarPage.advancedTab.click();

				await modelBuilderRightSidebarPage.useDefaultValueToggle.uncheck();

				await viewObjectEntriesPage.goto(objectClassName);

				await viewObjectEntriesPage.clickAddObjectEntry(objectName);

				await expect(
					page.getByLabel(booleanFieldName)
				).not.toBeChecked();
			});
		}
	);

	test(
		'can create, update, and delete default value for boolean field through Object Admin',
		{tag: ['@LPD-49587']},
		async ({apiHelpers, objectFieldsPage, page, viewObjectEntriesPage}) => {
			let objectDefinition: Partial<ObjectDefinition>;

			let booleanField: Partial<ObjectField>[];

			let booleanFieldName: string;

			let objectClassName: string;

			await test.step('create object with boolean field', async () => {
				booleanField = generateObjectFields({
					objectFieldBusinessTypes: ['Boolean'],
				});

				booleanFieldName = booleanField[0].label['en_US'];

				objectDefinition =
					await apiHelpers.objectAdmin.postRandomObjectDefinition({
						objectFields: booleanField,
						status: {code: 0},
					});

				objectClassName = objectDefinition.className;

				apiHelpers.data.push({
					id: objectDefinition.id,
					type: 'objectDefinition',
				});
			});

			await test.step('set default value to false for boolean field and check in object entry', async () => {
				await objectFieldsPage.goto(objectDefinition.label['en_US']);

				await objectFieldsPage.setDefaultValue({
					defaultValue: 'False',
					objectFieldBusinessType: 'Boolean',
					objectFieldName: booleanFieldName,
				});

				await viewObjectEntriesPage.goto(objectClassName);

				await viewObjectEntriesPage.clickAddObjectEntry(
					objectDefinition.name
				);

				await expect(
					page.getByLabel(booleanFieldName)
				).not.toBeChecked();
			});

			await test.step('set default value to true for boolean field and check in object entry', async () => {
				await objectFieldsPage.goto(objectDefinition.label['en_US']);

				await objectFieldsPage.setDefaultValue({
					defaultValue: 'True',
					objectFieldBusinessType: 'Boolean',
					objectFieldName: booleanFieldName,
				});

				await viewObjectEntriesPage.goto(objectClassName);

				await viewObjectEntriesPage.clickAddObjectEntry(
					objectDefinition.name
				);

				await expect(page.getByLabel(booleanFieldName)).toBeChecked();
			});

			await test.step('untoggle default value for boolean field and check in object entry', async () => {
				await objectFieldsPage.goto(objectDefinition.name);

				await objectFieldsPage.disableDefaultValue(booleanFieldName);

				await viewObjectEntriesPage.goto(objectClassName);

				await viewObjectEntriesPage.clickAddObjectEntry(
					objectDefinition.name
				);

				await expect(
					page.getByLabel(booleanFieldName)
				).not.toBeChecked();
			});
		}
	);

	test(
		'can create, read, update and delete the default value of date and dateTime fields',
		{tag: ['@LPD-48612']},
		async ({apiHelpers, objectFieldsPage, page, viewObjectEntriesPage}) => {
			const FIELDS: Array<{
				businessType: 'Date' | 'DateTime';
				editedValue: string;
				initialValue: string;
				initialValueUI: string;
				label?: string;
			}> = [
				{
					businessType: 'Date',
					editedValue: '12/10/2030',
					initialValue: '2025-12-10',
					initialValueUI: '12/10/2025',
				},
				{
					businessType: 'DateTime',
					editedValue: '12/10/2030 03:00 PM',
					initialValue: '2025-12-10 15:00',
					initialValueUI: '12/10/2025 03:00 PM',
				},
			];

			let objectDefinition: ObjectDefinition;
			let objectFields: Partial<ObjectField>[];

			await test.step('Create an object definition with default values', async () => {
				objectFields = generateObjectFields({
					objectFieldBusinessTypes: FIELDS.map(
						({businessType, initialValue}) => ({
							businessType,
							objectFieldSettings: [
								{
									name: 'defaultValueType',
									value: 'inputAsValue',
								},
								{name: 'defaultValue', value: initialValue},
							],
						})
					),
				});

				objectDefinition =
					await apiHelpers.objectAdmin.postRandomObjectDefinition({
						objectFields,
						status: {code: 0},
					});

				apiHelpers.data.push({
					id: objectDefinition.id,
					type: 'objectDefinition',
				});

				FIELDS.forEach((field, index) => {
					field.label = objectFields[index].label['en_US'];
				});
			});

			await test.step('Check that initial default values are set in the object entry creation', async () => {
				await viewObjectEntriesPage.goto(objectDefinition.className);

				await viewObjectEntriesPage.clickAddObjectEntry(
					objectDefinition.label['en_US']
				);

				for (const {initialValueUI, label} of FIELDS) {
					await expect(page.getByLabel(label)).toHaveValue(
						initialValueUI
					);
				}
			});

			await test.step('Update default values', async () => {
				await objectFieldsPage.goto(objectDefinition.label['en_US']);

				for (const {businessType, editedValue, label} of FIELDS) {
					await objectFieldsPage.setDefaultValue({
						defaultValue: editedValue,
						objectFieldBusinessType: businessType,
						objectFieldName: label,
					});
				}
			});

			await test.step('Check default values are updated in object entry creation', async () => {
				await viewObjectEntriesPage.goto(objectDefinition.className);

				await viewObjectEntriesPage.clickAddObjectEntry(
					objectDefinition.label['en_US']
				);

				for (const {editedValue, label} of FIELDS) {
					await expect(page.getByLabel(label)).toHaveValue(
						editedValue
					);
				}
			});

			await test.step('Disable default values', async () => {
				await objectFieldsPage.goto(objectDefinition.label['en_US']);

				for (const {label} of FIELDS) {
					await objectFieldsPage.disableDefaultValue(label);
				}
			});

			await test.step('Check default values are cleared in object entry creation', async () => {
				await viewObjectEntriesPage.goto(objectDefinition.className);

				await viewObjectEntriesPage.clickAddObjectEntry(
					objectDefinition.label['en_US']
				);

				for (const {label} of FIELDS) {
					await expect(page.getByLabel(label)).toHaveValue('');
				}
			});
		}
	);

	test(
		'can create, read, update and delete the default value of decimal, integer, longInteger, and precisionDecimal fields',
		{tag: ['@LPD-49589']},
		async ({apiHelpers, objectFieldsPage, page, viewObjectEntriesPage}) => {
			const FIELDS: Array<{
				businessType:
					| 'Decimal'
					| 'Integer'
					| 'LongInteger'
					| 'PrecisionDecimal';
				editedValue: string;
				initialValue: string;
				label?: string;
			}> = [
				{
					businessType: 'Decimal',
					editedValue: '456.456',
					initialValue: '123.123',
				},
				{
					businessType: 'Integer',
					editedValue: '456',
					initialValue: '123',
				},
				{
					businessType: 'LongInteger',
					editedValue: '456',
					initialValue: '123',
				},
				{
					businessType: 'PrecisionDecimal',
					editedValue: '456.456',
					initialValue: '123.123',
				},
			];

			let objectDefinition: ObjectDefinition;
			let objectFields: Partial<ObjectField>[];

			await test.step('Create object definition with default values', async () => {
				objectFields = generateObjectFields({
					objectFieldBusinessTypes: FIELDS.map(
						({businessType, initialValue}) => ({
							businessType,
							objectFieldSettings: [
								{
									name: 'defaultValueType',
									value: 'inputAsValue',
								},
								{name: 'defaultValue', value: initialValue},
							],
						})
					),
				});

				objectDefinition =
					await apiHelpers.objectAdmin.postRandomObjectDefinition({
						objectFields,
						status: {code: 0},
					});

				apiHelpers.data.push({
					id: objectDefinition.id,
					type: 'objectDefinition',
				});

				FIELDS.forEach((field, index) => {
					field.label = objectFields[index].label['en_US'];
				});
			});

			await test.step('Check initial default values in object entry creation', async () => {
				await viewObjectEntriesPage.goto(objectDefinition.className);

				await viewObjectEntriesPage.clickAddObjectEntry(
					objectDefinition.label['en_US']
				);

				for (const {initialValue, label} of FIELDS) {
					await expect(page.getByLabel(label)).toHaveValue(
						initialValue
					);
				}
			});

			await test.step('Update default values', async () => {
				await objectFieldsPage.goto(objectDefinition.label['en_US']);

				for (const {businessType, editedValue, label} of FIELDS) {
					await objectFieldsPage.setDefaultValue({
						defaultValue: editedValue,
						objectFieldBusinessType: businessType,
						objectFieldName: label,
					});
				}
			});

			await test.step('Check updated default values in object entry creation', async () => {
				await viewObjectEntriesPage.goto(objectDefinition.className);

				await viewObjectEntriesPage.clickAddObjectEntry(
					objectDefinition.label['en_US']
				);

				for (const {editedValue, label} of FIELDS) {
					await expect(page.getByLabel(label)).toHaveValue(
						editedValue
					);
				}
			});

			await test.step('Disable default values', async () => {
				await objectFieldsPage.goto(objectDefinition.label['en_US']);

				for (const {label} of FIELDS) {
					await objectFieldsPage.disableDefaultValue(label);
				}
			});

			await test.step('Check default values are cleared in object entry creation', async () => {
				await viewObjectEntriesPage.goto(objectDefinition.className);

				await viewObjectEntriesPage.clickAddObjectEntry(
					objectDefinition.label['en_US']
				);

				for (const {label} of FIELDS) {
					await expect(page.getByLabel(label)).toHaveValue('');
				}
			});
		}
	);

	test(
		'can create, read, update and delete the default value of long text and text fields',
		{tag: ['@LPD-48612']},
		async ({apiHelpers, objectFieldsPage, page, viewObjectEntriesPage}) => {
			const FIELDS: Array<{
				businessType: 'LongText' | 'Text';
				editedValue: string;
				initialValue: string;
				label?: string;
			}> = [
				{
					businessType: 'LongText',
					editedValue: 'defaultValueLongTextEdited',
					initialValue: 'defaultValueLongText',
				},
				{
					businessType: 'Text',
					editedValue: 'defaultValueTextEdited',
					initialValue: 'defaultValueText',
				},
			];

			const objectFields = generateObjectFields({
				objectFieldBusinessTypes: FIELDS.map(
					({businessType, initialValue}) => ({
						businessType,
						objectFieldSettings: [
							{
								name: 'defaultValueType',
								value: 'inputAsValue',
							},
							{name: 'defaultValue', value: initialValue},
						],
					})
				),
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

			FIELDS.forEach((field, index) => {
				field.label = objectFields[index].label['en_US'];
			});

			await viewObjectEntriesPage.goto(objectDefinition.className);
			await viewObjectEntriesPage.clickAddObjectEntry(
				objectDefinition.label['en_US']
			);

			for (const {initialValue, label} of FIELDS) {
				await expect(page.getByLabel(label)).toHaveValue(initialValue);
			}

			await objectFieldsPage.goto(objectDefinition.label['en_US']);

			for (const {businessType, editedValue, label} of FIELDS) {
				await objectFieldsPage.setDefaultValue({
					defaultValue: editedValue,
					objectFieldBusinessType: businessType,
					objectFieldName: label,
				});
			}

			await viewObjectEntriesPage.goto(objectDefinition.className);
			await viewObjectEntriesPage.clickAddObjectEntry(
				objectDefinition.label['en_US']
			);

			for (const {editedValue, label} of FIELDS) {
				await expect(page.getByLabel(label)).toHaveValue(editedValue);
			}

			await objectFieldsPage.goto(objectDefinition.label['en_US']);

			for (const {label} of FIELDS) {
				await objectFieldsPage.disableDefaultValue(label);
			}

			await viewObjectEntriesPage.goto(objectDefinition.className);
			await viewObjectEntriesPage.clickAddObjectEntry(
				objectDefinition.label['en_US']
			);

			for (const {label} of FIELDS) {
				await expect(page.getByLabel(label)).toHaveValue('');
			}
		}
	);

	test(
		'can create, read, update and delete the default value of a richText field',
		{tag: ['@LPD-48612']},
		async ({apiHelpers, objectFieldsPage, page, viewObjectEntriesPage}) => {
			const objectFields = generateObjectFields({
				objectFieldBusinessTypes: [
					{
						businessType: 'RichText',
						objectFieldSettings: [
							{
								name: 'defaultValueType',
								value: 'inputAsValue',
							},
							{
								name: 'defaultValue',
								value: '<p>defaultValueRichText</p>',
							},
						],
					},
				],
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

			await viewObjectEntriesPage.goto(objectDefinition.className);
			await viewObjectEntriesPage.clickAddObjectEntry(
				objectDefinition.label['en_US']
			);

			const richTextEditor = page.getByLabel('Rich Text Editor').nth(1);

			await expect(richTextEditor.getByRole('paragraph')).toHaveText(
				'defaultValueRichText'
			);

			await objectFieldsPage.goto(objectDefinition.label['en_US']);

			const richTextLabel = objectFields[0].label['en_US'];

			await objectFieldsPage.setDefaultValue({
				defaultValue: 'defaultValueRichTextEdited',
				objectFieldBusinessType: 'RichText',
				objectFieldName: richTextLabel,
			});

			await viewObjectEntriesPage.goto(objectDefinition.className);
			await viewObjectEntriesPage.clickAddObjectEntry(
				objectDefinition.label['en_US']
			);

			await expect(richTextEditor.getByRole('paragraph')).toHaveText(
				'defaultValueRichTextEdited'
			);

			await objectFieldsPage.goto(objectDefinition.label['en_US']);

			await objectFieldsPage.disableDefaultValue(richTextLabel);

			await viewObjectEntriesPage.goto(objectDefinition.className);
			await viewObjectEntriesPage.clickAddObjectEntry(
				objectDefinition.label['en_US']
			);

			await expect(richTextEditor.getByRole('paragraph')).toHaveText('');
		}
	);

	test(
		'can edit a default value input through Model Builder without throwing errors',
		{tag: ['@LPD-70980']},
		async ({
			apiHelpers,
			modelBuilderDiagramPage,
			modelBuilderObjectDefinitionNodePage,
			modelBuilderRightSidebarPage,
			page,
		}) => {
			const objectFields = generateObjectFields({
				objectFieldBusinessTypes: ['Text'],
			});

			const objectFolder =
				await apiHelpers.objectAdmin.postRandomObjectFolder();

			apiHelpers.data.push({
				id: objectFolder.id,
				type: 'objectFolder',
			});

			const objectDefinition =
				await apiHelpers.objectAdmin.postRandomObjectDefinition({
					objectFields,
					objectFolderExternalReferenceCode:
						objectFolder.externalReferenceCode,
					status: {code: 0},
				});

			apiHelpers.data.push({
				id: objectDefinition.id,
				type: 'objectDefinition',
			});

			await modelBuilderDiagramPage.goto({
				objectFolderName: objectFolder.name,
			});

			await modelBuilderObjectDefinitionNodePage.clickShowAllFieldsButton(
				objectDefinition.name,
				modelBuilderDiagramPage.objectDefinitionNodes
			);

			await modelBuilderDiagramPage.objectDefinitionNodes
				.filter({hasText: objectDefinition.name})
				.getByText(objectFields[0].label.en_US, {exact: true})
				.dispatchEvent('click');

			await modelBuilderRightSidebarPage.setDefaultValue(
				'Text',
				'this is a text default value on model builder'
			);

			await expect(page.getByText('Error')).toHaveCount(0);
		}
	);

	test(
		'default value fields are required',
		{tag: ['@LPD-48612']},
		async ({apiHelpers, objectFieldsPage, page}) => {
			const objectFields = generateObjectFields({
				objectFieldBusinessTypes: [
					'Boolean',
					'Date',
					'DateTime',
					'Decimal',
					'Integer',
					'LongInteger',
					'LongText',
					'PrecisionDecimal',
					'RichText',
					'Text',
				],
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

			for (const {label} of objectFields) {
				const fieldLabel = label.en_US;

				await objectFieldsPage.openObjectField(fieldLabel);

				await objectFieldsPage.advancedTab.click();

				await objectFieldsPage.useDefaultValueToggle.check();

				await objectFieldsPage.editFieldSaveButton.click();

				await expect(page.getByText('required')).toBeVisible();

				await waitForAlert(
					page,
					'Error:Please fill out all required fields.',
					{type: 'danger'}
				);

				await objectFieldsPage.closeObjectFieldSidePanel();
			}
		}
	);

	test(
		'model builder rightSidebar width only increases if configuration is enabled',
		{tag: ['@LPD-70980']},
		async ({
			apiHelpers,
			modelBuilderDiagramPage,
			modelBuilderObjectDefinitionNodePage,
			modelBuilderRightSidebarPage,
			page,
		}) => {
			const objectFields = generateObjectFields({
				objectFieldBusinessTypes: ['Text'],
			});

			const objectFolder =
				await apiHelpers.objectAdmin.postRandomObjectFolder();

			apiHelpers.data.push({
				id: objectFolder.id,
				type: 'objectFolder',
			});

			const objectDefinition =
				await apiHelpers.objectAdmin.postRandomObjectDefinition({
					objectFields,
					objectFolderExternalReferenceCode:
						objectFolder.externalReferenceCode,
					status: {code: 0},
				});

			apiHelpers.data.push({
				id: objectDefinition.id,
				type: 'objectDefinition',
			});

			await modelBuilderDiagramPage.goto({
				objectFolderName: objectFolder.name,
			});

			await modelBuilderObjectDefinitionNodePage.clickShowAllFieldsButton(
				objectDefinition.name,
				modelBuilderDiagramPage.objectDefinitionNodes
			);

			await modelBuilderDiagramPage.objectDefinitionNodes
				.filter({hasText: objectDefinition.name})
				.getByText(objectFields[0].label.en_US, {exact: true})
				.dispatchEvent('click');

			const rightSidebar = page.locator(
				'.lfr__objects-custom-vertical-bar-content > .sidebar[id*="ModelBuilderRightSidebar"]'
			);

			await expect(rightSidebar).toHaveCSS('width', '320px');

			await modelBuilderRightSidebarPage.advancedTab.click();

			await modelBuilderRightSidebarPage.useDefaultValueToggle.check();

			await expect(rightSidebar).toHaveCSS('width', '500px');

			await modelBuilderRightSidebarPage.useDefaultValueToggle.uncheck();

			await expect(rightSidebar).toHaveCSS('width', '320px');
		}
	);

	test(
		'normalizes email address default value to lowercase',
		{tag: ['@LPD-70673']},
		async ({apiHelpers, objectFieldsPage}) => {
			const objectFieldLabel = `emailAddressField${getRandomInt()}`;

			const objectDefinition =
				await apiHelpers.objectAdmin.postRandomObjectDefinition({
					status: {code: 0},
				});

			apiHelpers.data.push({
				id: objectDefinition.id,
				type: 'objectDefinition',
			});

			await test.step('Add an Email Address field and set a mixed-case default value', async () => {
				await objectFieldsPage.goto(objectDefinition.label.en_US);

				await objectFieldsPage.addObjectField({
					objectFieldBusinessType: 'Email Address',
					objectFieldLabel,
				});

				await objectFieldsPage.openObjectField(objectFieldLabel);

				await objectFieldsPage.advancedTab.click();

				await objectFieldsPage.useDefaultValueToggle.check();

				await objectFieldsPage.iframeLocator
					.getByPlaceholder('Enter a default value.')
					.fill('User@Example.com');

				await objectFieldsPage.saveObjectField();
			});

			await test.step('Verify the stored default value is lowercase', async () => {
				await objectFieldsPage.openObjectField(objectFieldLabel);

				await objectFieldsPage.advancedTab.click();

				await expect(
					objectFieldsPage.iframeLocator.getByPlaceholder(
						'Enter a default value.'
					)
				).toHaveValue('user@example.com');
			});
		}
	);

	test(
		'shows error for email address field default value when value is invalid',
		{tag: ['@LPD-70673']},
		async ({apiHelpers, objectFieldsPage, page}) => {
			const objectFieldLabel = `emailAddressField${getRandomInt()}`;

			const objectDefinition =
				await apiHelpers.objectAdmin.postRandomObjectDefinition({
					status: {code: 0},
				});

			apiHelpers.data.push({
				id: objectDefinition.id,
				type: 'objectDefinition',
			});

			const invalidEmailAddress = 'notanemail';

			await test.step('Add an Email Address field and enter an invalid default value', async () => {
				await objectFieldsPage.goto(objectDefinition.label.en_US);

				await objectFieldsPage.addObjectField({
					objectFieldBusinessType: 'Email Address',
					objectFieldLabel,
				});

				await objectFieldsPage.openObjectField(objectFieldLabel);

				await objectFieldsPage.advancedTab.click();

				await objectFieldsPage.useDefaultValueToggle.check();

				await objectFieldsPage.iframeLocator
					.getByPlaceholder('Enter a default value.')
					.fill(invalidEmailAddress);

				await objectFieldsPage.editFieldSaveButton.click();
			});

			await test.step('Verify the save is rejected and the panel stays open', async () => {
				await waitForAlert(
					page,
					`The value ${invalidEmailAddress} of setting "defaultValue" is invalid for object field "${objectFieldLabel}"`,
					{type: 'danger'}
				);

				await expect(
					objectFieldsPage.editFieldSaveButton
				).toBeVisible();
			});
		}
	);
});
