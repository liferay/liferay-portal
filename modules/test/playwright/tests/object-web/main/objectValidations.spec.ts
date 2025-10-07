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
import {expect, mergeTests} from '@playwright/test';

import {apiHelpersTest} from '../../../fixtures/apiHelpersTest';
import {dataApiHelpersTest} from '../../../fixtures/dataApiHelpersTest';
import {featureFlagsTest} from '../../../fixtures/featureFlagsTest';
import {isolatedSiteTest} from '../../../fixtures/isolatedSiteTest';
import {loginTest} from '../../../fixtures/loginTest';
import {objectPagesTest} from '../../../fixtures/objectPagesTest';
import {pageEditorPagesTest} from '../../../fixtures/pageEditorPagesTest';
import {getRandomInt} from '../../../utils/getRandomInt';
import getRandomString from '../../../utils/getRandomString';
import {journalPagesTest} from '../../journal-web/main/fixtures/journalPagesTest';
import getFormContainerDefinition from '../../layout-content-page-editor-web/main/utils/getFormContainerDefinition';
import getPageDefinition from '../../layout-content-page-editor-web/main/utils/getPageDefinition';

export const test = mergeTests(
	apiHelpersTest,
	dataApiHelpersTest,
	featureFlagsTest({
		'LPS-178052': {enabled: true},
	}),
	isolatedSiteTest,
	loginTest(),
	objectPagesTest,
	pageEditorPagesTest,
	journalPagesTest
);

test.describe('Object Unique Composite Key Validation', () => {
	let objectDefinition1: ObjectDefinition;
	let objectDefinition2: ObjectDefinition;

	test.beforeEach(async ({apiHelpers}) => {
		const newObjectDefinition1 =
			await apiHelpers.objectAdmin.postRandomObjectDefinition({
				status: {code: 0},
			});

		const newObjectDefinition2 =
			await apiHelpers.objectAdmin.postRandomObjectDefinition({
				status: {code: 0},
			});

		objectDefinition1 = newObjectDefinition1;
		objectDefinition2 = newObjectDefinition2;
	});

	test.afterEach(async ({apiHelpers}) => {
		const objectDefinitionAPIClient =
			await apiHelpers.buildRestClient(ObjectDefinitionAPI);

		await objectDefinitionAPIClient.deleteObjectDefinition(
			objectDefinition1.id
		);
		await objectDefinitionAPIClient.deleteObjectDefinition(
			objectDefinition2.id
		);
	});

	test('can create an object unique composite key validation', async ({
		apiHelpers,
		editObjectValidationPage,
		modalAddObjectValidationPage,
		objectValidationsPage,
		page,
	}) => {
		const objectFieldAPIClient =
			await apiHelpers.buildRestClient(ObjectFieldAPI);

		await objectFieldAPIClient.postObjectDefinitionByExternalReferenceCodeObjectField(
			objectDefinition1.externalReferenceCode,
			{
				DBType: 'Integer',
				businessType: 'Integer',
				externalReferenceCode: 'integerField',
				indexed: true,
				indexedAsKeyword: false,
				indexedLanguageId: '',
				label: {en_US: 'integerField'},
				listTypeDefinitionId: 0,
				localized: false,
				name: 'integerField',
				readOnly: 'false',
				required: false,
				state: false,
				system: false,
			}
		);

		objectValidationsPage.goto(objectDefinition1.label['en_US']);

		await objectValidationsPage.addObjectValidationButton.click();

		const objectValidationLabel =
			'UniqueCompositeKeyValidation' + getRandomInt();

		await modalAddObjectValidationPage.fillObjectValidationInputs(
			objectValidationLabel,
			'Composite Key'
		);

		const newValidationLink = page.getByText(objectValidationLabel);

		await newValidationLink.click();

		await editObjectValidationPage.uniqueCompositeKeyTab.click();

		await editObjectValidationPage.addObjectFieldsButton.click();

		await editObjectValidationPage.clickSelectAllFields();

		await editObjectValidationPage.saveObjectValidationButton.click();

		await expect(
			page.getByText('The object validation was updated successfully.')
		).toBeVisible();
	});

	test('can use an object unique composite key validation', async ({
		apiHelpers,
		viewObjectEntriesPage,
	}) => {
		const integerFieldName = 'integerField' + getRandomInt();

		const objectFieldAPIClient =
			await apiHelpers.buildRestClient(ObjectFieldAPI);

		await objectFieldAPIClient.postObjectDefinitionByExternalReferenceCodeObjectField(
			objectDefinition1.externalReferenceCode,
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
			objectDefinition1.externalReferenceCode,
			{
				active: true,
				engine: 'compositeKey',
				engineLabel: 'Composite Key',
				errorLabel: {
					en_US: 'The field values are already in use. Please choose unique values.',
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

		const applicationName =
			'c/' + objectDefinition1.name.toLowerCase() + 's';

		const textObjectEntry = {
			textField: 'entry',
		};

		await apiHelpers.objectEntry.postObjectEntry(
			textObjectEntry,
			applicationName
		);

		await viewObjectEntriesPage.goto(objectDefinition1.className);

		await viewObjectEntriesPage.clickAddObjectEntry(
			objectDefinition1.label['en_US']
		);

		await viewObjectEntriesPage.fillObjectEntry({
			objectFieldLabel: integerFieldName,
			objectFieldValue: '0',
		});

		await viewObjectEntriesPage.fillObjectEntry({
			objectFieldLabel: 'textField',
			objectFieldValue: 'entry',
		});

		await viewObjectEntriesPage.saveObjectEntryButton.click();
		await viewObjectEntriesPage.assertErrorWithDuplicateEntryValue();

		await viewObjectEntriesPage.backButton.click();

		await viewObjectEntriesPage.clickAddObjectEntry(
			objectDefinition1.label['en_US']
		);

		await viewObjectEntriesPage.fillObjectEntry({
			objectFieldLabel: integerFieldName,
			objectFieldValue: '123',
		});

		await viewObjectEntriesPage.fillObjectEntry({
			objectFieldLabel: 'textField',
			objectFieldValue: 'entry 2',
		});

		await viewObjectEntriesPage.saveObjectEntryButton.click();
		await expect(viewObjectEntriesPage.successMessage).toBeVisible();
	});

	test('check if only specific object field business types (AutoIncrement, Integer, Picklist, Relationship, Text) will be accepted in unique composite key validation', async ({
		apiHelpers,
		editObjectValidationPage,
		modalAddObjectValidationPage,
		objectValidationsPage,
		page,
	}) => {
		const autoIncrementFieldName = 'autoIncrementField' + getRandomInt();
		const dateFieldName = 'dateField' + getRandomInt();
		const integerFieldName = 'integerField' + getRandomInt();
		const objectRelationshipLabel =
			'objectRelationshipLabel' + getRandomInt();
		const objectRelationshipName =
			'objectRelationshipName' + Math.floor(Math.random() * 99);
		const picklistFieldName = 'picklistField' + getRandomInt();

		const objectFieldAPIClient =
			await apiHelpers.buildRestClient(ObjectFieldAPI);

		await objectFieldAPIClient.postObjectDefinitionByExternalReferenceCodeObjectField(
			objectDefinition1.externalReferenceCode,
			{
				DBType: 'String',
				businessType: 'AutoIncrement',
				externalReferenceCode: autoIncrementFieldName,
				indexed: true,
				indexedAsKeyword: false,
				indexedLanguageId: '',
				label: {en_US: autoIncrementFieldName},
				listTypeDefinitionId: 0,
				localized: false,
				name: autoIncrementFieldName,
				objectFieldSettings: [
					{
						name: 'initialValue',
						value: '1234',
					} as any,
				],
				readOnly: 'false',
				required: false,
				state: false,
				system: false,
			}
		);

		await objectFieldAPIClient.postObjectDefinitionByExternalReferenceCodeObjectField(
			objectDefinition1.externalReferenceCode,
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

		await objectFieldAPIClient.postObjectDefinitionByExternalReferenceCodeObjectField(
			objectDefinition1.externalReferenceCode,
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

		const objectRelationshipAPIClient = await apiHelpers.buildRestClient(
			ObjectRelationshipAPI
		);

		await objectRelationshipAPIClient.postObjectDefinitionByExternalReferenceCodeObjectRelationship(
			objectDefinition2.externalReferenceCode,
			{
				label: {
					en_US: objectRelationshipLabel,
				},
				name: objectRelationshipName,
				objectDefinitionExternalReferenceCode1:
					objectDefinition2.externalReferenceCode,
				objectDefinitionExternalReferenceCode2:
					objectDefinition1.externalReferenceCode,
				objectDefinitionId1: objectDefinition2.id,
				objectDefinitionId2: objectDefinition1.id,
				objectDefinitionName2: objectDefinition1.name,
				type: 'oneToMany',
			}
		);

		const listTypeDefinition =
			await apiHelpers.listTypeAdmin.postRandomListTypeDefinition();

		await objectFieldAPIClient.postObjectDefinitionByExternalReferenceCodeObjectField(
			objectDefinition1.externalReferenceCode,
			{
				DBType: 'String',
				businessType: 'Picklist',
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
				readOnly: 'false',
				required: false,
				state: false,
				system: false,
			}
		);

		objectValidationsPage.goto(objectDefinition1.label['en_US']);

		await objectValidationsPage.addObjectValidationButton.click();

		const objectValidationLabel =
			'UniqueCompositeKeyValidation' + getRandomInt();

		await modalAddObjectValidationPage.fillObjectValidationInputs(
			objectValidationLabel,
			'Composite Key'
		);

		const newValidationLink = page.getByText(objectValidationLabel);

		await expect(newValidationLink).toBeVisible();

		await newValidationLink.click();

		await editObjectValidationPage.uniqueCompositeKeyTab.click();

		await editObjectValidationPage.addObjectFieldsButton.click();

		await expect(page.getByText(autoIncrementFieldName)).toBeVisible();
		await expect(page.getByText(dateFieldName)).not.toBeVisible();
		await expect(page.getByText(integerFieldName)).toBeVisible();
		await expect(page.getByText(objectRelationshipLabel)).toBeVisible();
		await expect(page.getByText(picklistFieldName)).toBeVisible();

		// Clean Up

		await apiHelpers.listTypeAdmin.deleteListTypeDefinition(
			listTypeDefinition.id
		);
	});

	test('cannot select a object field that already has an entry in a new composite key validation', async ({
		apiHelpers,
		editObjectValidationPage,
		modalAddObjectValidationPage,
		objectValidationsPage,
		page,
	}) => {
		const objectFieldAPIClient =
			await apiHelpers.buildRestClient(ObjectFieldAPI);

		await objectFieldAPIClient.postObjectDefinitionByExternalReferenceCodeObjectField(
			objectDefinition1.externalReferenceCode,
			{
				DBType: 'Integer',
				businessType: 'Integer',
				externalReferenceCode: 'integerField',
				indexed: true,
				indexedAsKeyword: false,
				indexedLanguageId: '',
				label: {en_US: 'integerField'},
				listTypeDefinitionId: 0,
				localized: false,
				name: 'integerField',
				readOnly: 'false',
				required: false,
				state: false,
				system: false,
			}
		);

		const applicationName =
			'c/' + objectDefinition1.name.toLowerCase() + 's';

		const textObjectEntry = {
			textField: 'entry',
		};

		await apiHelpers.objectEntry.postObjectEntry(
			textObjectEntry,
			applicationName
		);

		objectValidationsPage.goto(objectDefinition1.label['en_US']);

		await objectValidationsPage.addObjectValidationButton.click();

		const objectValidationLabel =
			'UniqueCompositeKeyValidation' + getRandomInt();

		await modalAddObjectValidationPage.fillObjectValidationInputs(
			objectValidationLabel,
			'Composite Key'
		);

		const newValidationLink = page.getByText(objectValidationLabel);

		await expect(newValidationLink).toBeVisible();

		await newValidationLink.click();

		await editObjectValidationPage.uniqueCompositeKeyTab.click();

		await editObjectValidationPage.addObjectFieldsButton.click();

		await editObjectValidationPage.clickSelectAllFields();

		await editObjectValidationPage.saveObjectValidationButton.click();

		await expect(
			editObjectValidationPage.getObjectFieldAlreadyHasEntryErrorLocator(
				'textField, integerField'
			)
		).toBeVisible();
	});

	test('cannot add unique composite key validation with just one field', async ({
		editObjectValidationPage,
		modalAddObjectValidationPage,
		objectValidationsPage,
		page,
	}) => {
		objectValidationsPage.goto(objectDefinition1.label['en_US']);

		await objectValidationsPage.addObjectValidationButton.click();

		const validationLabel = 'UniqueCompositeKeyValidation' + getRandomInt();

		await modalAddObjectValidationPage.fillObjectValidationInputs(
			validationLabel,
			'Composite Key'
		);

		const newValidationLink = page.getByText(validationLabel);

		await expect(newValidationLink).toBeVisible();

		await newValidationLink.click();

		await editObjectValidationPage.uniqueCompositeKeyTab.click();

		await editObjectValidationPage.addObjectFieldsButton.click();

		await editObjectValidationPage.clickSelectAllFields();

		await editObjectValidationPage.saveObjectValidationButton.click();

		await expect(
			editObjectValidationPage.addTwoObjectFieldsErrorMessage
		).toBeVisible();
	});
});

test(
	'can display error messages under object fields in a mapped form container when submitting invalid entries',
	{tag: ['@LPD-44500']},
	async ({apiHelpers, page, pageEditorPage, site}) => {
		let objectDefinition;

		await test.step('Create the object definition', async () => {
			const objectDefinitionAPIClient =
				await apiHelpers.buildRestClient(ObjectDefinitionAPI);

			const {body} = await objectDefinitionAPIClient.postObjectDefinition(
				{
					active: true,
					externalReferenceCode:
						'objectDefinitionERC' + getRandomInt(),
					label: {
						en_US: 'objectDefinition',
					},
					name: 'ObjectDefinition' + getRandomInt(),
					objectFields: [
						{
							DBType: 'Integer',
							businessType: 'Integer',
							externalReferenceCode: 'intERC',
							indexed: true,
							label: {
								en_US: 'objectFieldLabelInt',
							},
							name: `fieldInt${getRandomInt()}`,
							required: false,
						},
						{
							DBType: 'Double',
							businessType: 'Decimal',
							externalReferenceCode: 'doubleERC',
							indexed: true,
							label: {
								en_US: 'objectFieldLabelDouble',
							},
							name: `fieldDouble${getRandomInt()}`,
							required: false,
						},
					],
					pluralLabel: {
						en_US: 'objectDefinitions',
					},
					portlet: true,
					scope: 'company',
					status: {
						code: 0,
					},
				}
			);

			objectDefinition = body;

			apiHelpers.data.push({
				id: objectDefinition.id,
				type: 'objectDefinition',
			});
		});

		const objectValidationErrorLabelInt = 'Should be greater than 5.';

		const objectValidationErrorLabelDouble = 'Should be less than 6.5.';

		await test.step('Create object field validations', async () => {
			const allObjectDefinitionFields =
				await apiHelpers.objectAdmin.getAllObjectDefinitionsFields(
					objectDefinition.id
				);

			const objectDefinitionValidation = await apiHelpers.buildRestClient(
				ObjectValidationRuleAPI
			);

			const intField = allObjectDefinitionFields.items.find(
				(item: ObjectField) =>
					item.label?.en_US === 'objectFieldLabelInt'
			);

			await objectDefinitionValidation.postObjectDefinitionObjectValidationRule(
				objectDefinition.id,
				{
					active: true,
					engine: 'ddm',
					engineLabel: 'Expression Builder',
					errorLabel: {en_US: objectValidationErrorLabelInt},
					name: {
						en_US: `validation${getRandomInt()}`,
					},
					objectDefinitionExternalReferenceCode:
						objectDefinition.externalReferenceCode,
					objectValidationRuleSettings: [
						{
							name: 'outputObjectFieldExternalReferenceCode',
							value: intField.externalReferenceCode,
						} as any,
					],
					outputType: 'partialValidation',
					script: `${intField.name} > 5`,
					system: false,
				}
			);

			const doubleField = allObjectDefinitionFields.items.find(
				(item: ObjectField) =>
					item.label?.en_US === 'objectFieldLabelDouble'
			);

			await objectDefinitionValidation.postObjectDefinitionObjectValidationRule(
				objectDefinition.id,
				{
					active: true,
					engine: 'ddm',
					engineLabel: 'Expression Builder',
					errorLabel: {en_US: objectValidationErrorLabelDouble},
					name: {
						en_US: `validation${getRandomInt()}`,
					},
					objectDefinitionExternalReferenceCode:
						objectDefinition.externalReferenceCode,
					objectValidationRuleSettings: [
						{
							name: 'outputObjectFieldExternalReferenceCode',
							value: doubleField.externalReferenceCode,
						} as any,
					],
					outputType: 'partialValidation',
					script: `${doubleField.name} < 6.5`,
					system: false,
				}
			);
		});

		let formId;
		let layout;

		await test.step('Create a content page with a form container and go to edit mode', async () => {
			formId = getRandomString();

			const formDefinition = getFormContainerDefinition({
				id: formId,
			});

			const pageName = getRandomString();

			layout = await apiHelpers.headlessDelivery.createSitePage({
				pageDefinition: getPageDefinition([formDefinition]),
				siteId: site.id,
				title: pageName,
			});

			await pageEditorPage.goto(layout, site.friendlyUrlPath);
		});

		await test.step('Map the form container to the object and post page', async () => {
			await pageEditorPage.mapFormFragment(
				formId,
				`${objectDefinition.label.en_US}`
			);

			await pageEditorPage.publishPage();
		});

		await test.step('Go to the page, fill the form and check the validations', async () => {
			await page.goto(
				`/web${site.friendlyUrlPath}${layout.friendlyUrlPath}`
			);

			await page
				.getByRole('spinbutton', {name: 'objectFieldLabelDouble'})
				.fill('7.1');

			await page
				.getByRole('spinbutton', {name: 'objectFieldLabelInt'})
				.fill('4');

			await page.getByText('Submit').click();

			await expect(
				page.getByText(objectValidationErrorLabelDouble)
			).toBeVisible();

			await page
				.getByRole('spinbutton', {name: 'objectFieldLabelDouble'})
				.fill('5.2');

			await page.getByText('Submit').click();

			await expect(
				page.getByText(objectValidationErrorLabelInt)
			).toBeVisible();

			await page
				.getByRole('spinbutton', {name: 'objectFieldLabelInt'})
				.fill('6');

			await page.getByText('Submit').click();

			await expect(
				page.getByText('Thank you. Your information')
			).toBeVisible();
		});
	}
);
