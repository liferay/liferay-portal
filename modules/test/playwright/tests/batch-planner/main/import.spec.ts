/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {
	ObjectDefinition,
	ObjectDefinitionAPI,
} from '@liferay/object-admin-rest-client-js';
import {expect, mergeTests} from '@playwright/test';
import * as path from 'path';

import {dataApiHelpersTest} from '../../../fixtures/dataApiHelpersTest';
import {featureFlagsTest} from '../../../fixtures/featureFlagsTest';
import {loginTest} from '../../../fixtures/loginTest';
import {objectPagesTest} from '../../../fixtures/objectPagesTest';
import createTempFile from '../../../utils/createTempFile';
import getRandomString from '../../../utils/getRandomString';
import {dataMigrationCenterPagesTest} from './fixtures/dataMigrationCenterPagesTest';
import {OBJECT_ENTRY_ENTITY_TYPE} from './utils/constants';

export const test = mergeTests(
	dataApiHelpersTest,
	featureFlagsTest({
		'COMMERCE-8087': {enabled: true},
	}),
	loginTest(),
	dataMigrationCenterPagesTest,
	objectPagesTest
);

const companyObjectDefinition: ObjectDefinition = {
	active: true,
	externalReferenceCode: 'Test',
	label: {'en-US': 'Test'},
	name: 'Test',
	objectFields: [
		{
			DBType: 'String',
			businessType: 'Aggregation',
			externalReferenceCode: 'Test-AggregationField',
			indexed: false,
			indexedAsKeyword: false,
			indexedLanguageId: '',
			label: {en_US: 'testAggregationField'},
			listTypeDefinitionId: 0,
			name: 'testAggregationField',
			objectFieldSettings: [
				{
					name: 'objectRelationshipName',
					value: 'testRelationship',
				} as any,
				{name: 'function', value: 'COUNT'} as any,
			],
			required: false,
			system: false,
			type: 'String',
		},
		{
			DBType: 'String',
			businessType: 'AutoIncrement',
			externalReferenceCode: 'Test-AutoIncrementField',
			indexed: true,
			indexedAsKeyword: false,
			indexedLanguageId: '',
			label: {en_US: 'testAutoIncrementField'},
			listTypeDefinitionId: 0,
			name: 'testAutoIncrementField',
			objectFieldSettings: [
				{name: 'prefix', value: 'prefix-'} as any,
				{name: 'initialValue', value: '1'} as any,
				{name: 'suffix', value: '-suffix'} as any,
			],
			required: false,
			system: false,
			type: 'String',
		},
		{
			DBType: 'Boolean',
			businessType: 'Boolean',
			externalReferenceCode: 'Test-BooleanField',
			indexed: true,
			indexedAsKeyword: false,
			indexedLanguageId: '',
			label: {en_US: 'testBooleanField'},
			listTypeDefinitionId: 0,
			name: 'testBooleanField',
			required: false,
			system: false,
			type: 'Boolean',
		},
		{
			DBType: 'Date',
			businessType: 'Date',
			externalReferenceCode: 'Test-DateField',
			indexed: true,
			indexedAsKeyword: false,
			indexedLanguageId: '',
			label: {en_US: 'testDateField'},
			listTypeDefinitionId: 0,
			name: 'testDateField',
			required: false,
			system: false,
			type: 'Date',
		},
		{
			DBType: 'DateTime',
			businessType: 'DateTime',
			externalReferenceCode: 'Test-DateTimeField',
			indexed: true,
			indexedAsKeyword: false,
			indexedLanguageId: '',
			label: {en_US: 'testDateTimeField'},
			listTypeDefinitionId: 0,
			name: 'testDateTimeField',
			objectFieldSettings: [
				{name: 'timeStorage', value: 'convertToUTC'} as any,
			],
			required: false,
			system: false,
			type: 'DateTime',
		},
		{
			DBType: 'Double',
			businessType: 'Decimal',
			externalReferenceCode: 'Test-DecimalField',
			indexed: true,
			indexedAsKeyword: false,
			indexedLanguageId: '',
			label: {en_US: 'testDecimalFiel-d'},
			listTypeDefinitionId: 0,
			name: 'testDecimalField',
			required: false,
			system: false,
			type: 'Double',
		},
		{
			DBType: 'String',
			businessType: 'Formula',
			externalReferenceCode: 'Test-FormulaField',
			indexed: false,
			indexedAsKeyword: false,
			indexedLanguageId: '',
			label: {en_US: 'testFormulaField'},
			listTypeDefinitionId: 0,
			name: 'testFormulaField',
			objectFieldSettings: [
				{name: 'output', value: 'Integer'} as any,
				{name: 'script', value: 'id / id'} as any,
			],
			required: false,
			system: false,
			type: 'String',
		},
		{
			DBType: 'Integer',
			businessType: 'Integer',
			externalReferenceCode: 'Test-IntegerField',
			indexed: true,
			indexedAsKeyword: false,
			indexedLanguageId: '',
			label: {en_US: 'testIntegerField'},
			listTypeDefinitionId: 0,
			name: 'testIntegerField',
			required: false,
			system: false,
			type: 'Integer',
		},
		{
			DBType: 'Long',
			businessType: 'LongInteger',
			externalReferenceCode: 'Test-LongIntegerField',
			indexed: true,
			indexedAsKeyword: false,
			indexedLanguageId: '',
			label: {en_US: 'testLongInteger'},
			listTypeDefinitionId: 0,
			name: 'testLongInteger',
			required: false,
			system: false,
			type: 'Long',
		},
		{
			DBType: 'Clob',
			businessType: 'LongText',
			externalReferenceCode: 'Test-LongTextField',
			indexed: true,
			indexedAsKeyword: false,
			indexedLanguageId: '',
			label: {en_US: 'testLongTextField'},
			listTypeDefinitionId: 0,
			name: 'testLongTextField',
			required: false,
			system: false,
			type: 'Clob',
		},
		{
			DBType: 'BigDecimal',
			businessType: 'PrecisionDecimal',
			externalReferenceCode: 'Test-PrecisionDecimalField',
			indexed: true,
			indexedAsKeyword: false,
			indexedLanguageId: '',
			label: {en_US: 'testPrecisionDecimalField'},
			listTypeDefinitionId: 0,
			name: 'testPrecisionDecimalField',
			required: false,
			system: false,
			type: 'BigDecimal',
		},
		{
			DBType: 'Clob',
			businessType: 'RichText',
			externalReferenceCode: 'Test-RichTextField',
			indexed: true,
			indexedAsKeyword: false,
			indexedLanguageId: '',
			label: {en_US: 'testRichTextField'},
			listTypeDefinitionId: 0,
			name: 'testRichTextField',
			required: false,
			system: false,
			type: 'Clob',
		},
		{
			DBType: 'String',
			businessType: 'Text',
			externalReferenceCode: 'Test-TextField',
			indexed: true,
			indexedAsKeyword: false,
			indexedLanguageId: '',
			label: {en_US: 'testTextField'},
			listTypeDefinitionId: 0,
			name: 'testTextField',
			required: false,
			system: false,
			type: 'String',
		},
	],
	objectRelationships: [
		{
			deletionType: 'cascade',
			externalReferenceCode: 'test-Relationship',
			label: {
				en_US: 'Test Relationship',
			},
			name: 'testRelationship',
			objectDefinitionExternalReferenceCode1: 'Test',
			objectDefinitionExternalReferenceCode2: 'Test',
			objectDefinitionName2: 'Test',
			parameterObjectFieldId: 0,
			parameterObjectFieldName: '',
			reverse: false,
			system: false,
			type: 'oneToMany',
		},
	],
	panelCategoryKey: 'control_panel.users',
	pluralLabel: {'en-US': 'Tests'},
	portlet: true,
	scope: 'company',
	status: {code: 0},
};

const siteObjectDefinition: ObjectDefinition = {
	active: true,
	externalReferenceCode: 'Test',
	label: {'en-US': 'Test'},
	name: 'Test',
	objectFields: [
		{
			DBType: 'String',
			businessType: 'Aggregation',
			externalReferenceCode: 'Test-AggregationField',
			indexed: false,
			indexedAsKeyword: false,
			indexedLanguageId: '',
			label: {en_US: 'testAggregationField'},
			listTypeDefinitionId: 0,
			name: 'testAggregationField',
			objectFieldSettings: [
				{
					name: 'objectRelationshipName',
					value: 'testRelationship',
				} as any,
				{name: 'function', value: 'COUNT'} as any,
			],
			required: false,
			system: false,
			type: 'String',
		},
		{
			DBType: 'String',
			businessType: 'AutoIncrement',
			externalReferenceCode: 'Test-AutoIncrementField',
			indexed: true,
			indexedAsKeyword: false,
			indexedLanguageId: '',
			label: {en_US: 'testAutoIncrementField'},
			listTypeDefinitionId: 0,
			name: 'testAutoIncrementField',
			objectFieldSettings: [
				{name: 'prefix', value: 'prefix-'} as any,
				{name: 'initialValue', value: '1'} as any,
				{name: 'suffix', value: '-suffix'} as any,
			],
			required: false,
			system: false,
			type: 'String',
		},
		{
			DBType: 'Boolean',
			businessType: 'Boolean',
			externalReferenceCode: 'Test-BooleanField',
			indexed: true,
			indexedAsKeyword: false,
			indexedLanguageId: '',
			label: {en_US: 'testBooleanField'},
			listTypeDefinitionId: 0,
			name: 'testBooleanField',
			required: false,
			system: false,
			type: 'Boolean',
		},
		{
			DBType: 'Date',
			businessType: 'Date',
			externalReferenceCode: 'Test-DateField',
			indexed: true,
			indexedAsKeyword: false,
			indexedLanguageId: '',
			label: {en_US: 'testDateField'},
			listTypeDefinitionId: 0,
			name: 'testDateField',
			required: false,
			system: false,
			type: 'Date',
		},
		{
			DBType: 'DateTime',
			businessType: 'DateTime',
			externalReferenceCode: 'Test-DateTimeField',
			indexed: true,
			indexedAsKeyword: false,
			indexedLanguageId: '',
			label: {en_US: 'testDateTimeField'},
			listTypeDefinitionId: 0,
			name: 'testDateTimeField',
			objectFieldSettings: [
				{name: 'timeStorage', value: 'convertToUTC'} as any,
			],
			required: false,
			system: false,
			type: 'DateTime',
		},
		{
			DBType: 'Double',
			businessType: 'Decimal',
			externalReferenceCode: 'Test-DecimalField',
			indexed: true,
			indexedAsKeyword: false,
			indexedLanguageId: '',
			label: {en_US: 'testDecimalField'},
			listTypeDefinitionId: 0,
			name: 'testDecimalField',
			required: false,
			system: false,
			type: 'Double',
		},
		{
			DBType: 'String',
			businessType: 'Formula',
			externalReferenceCode: 'Test-FormulaField',
			indexed: false,
			indexedAsKeyword: false,
			indexedLanguageId: '',
			label: {en_US: 'testFormulaField'},
			listTypeDefinitionId: 0,
			name: 'testFormulaField',
			objectFieldSettings: [
				{name: 'output', value: 'Integer'} as any,
				{name: 'script', value: 'id / id'} as any,
			],
			required: false,
			system: false,
			type: 'String',
		},
		{
			DBType: 'Integer',
			businessType: 'Integer',
			externalReferenceCode: 'Test-IntegerField',
			indexed: true,
			indexedAsKeyword: false,
			indexedLanguageId: '',
			label: {en_US: 'testIntegerField'},
			listTypeDefinitionId: 0,
			name: 'testIntegerField',
			required: false,
			system: false,
			type: 'Integer',
		},
		{
			DBType: 'Long',
			businessType: 'LongInteger',
			externalReferenceCode: 'Test-LongIntegerField',
			indexed: true,
			indexedAsKeyword: false,
			indexedLanguageId: '',
			label: {en_US: 'testLongInteger'},
			listTypeDefinitionId: 0,
			name: 'testLongInteger',
			required: false,
			system: false,
			type: 'Long',
		},
		{
			DBType: 'Clob',
			businessType: 'LongText',
			externalReferenceCode: 'Test-LongTextField',
			indexed: true,
			indexedAsKeyword: false,
			indexedLanguageId: '',
			label: {en_US: 'testLongTextField'},
			listTypeDefinitionId: 0,
			name: 'testLongTextField',
			required: false,
			system: false,
			type: 'Clob',
		},
		{
			DBType: 'BigDecimal',
			businessType: 'PrecisionDecimal',
			externalReferenceCode: 'Test-PrecisionDecimalField',
			indexed: true,
			indexedAsKeyword: false,
			indexedLanguageId: '',
			label: {en_US: 'testPrecisionDecimalField'},
			listTypeDefinitionId: 0,
			name: 'testPrecisionDecimalField',
			required: false,
			system: false,
			type: 'BigDecimal',
		},
		{
			DBType: 'Clob',
			businessType: 'RichText',
			externalReferenceCode: 'Test-RichTextField',
			indexed: true,
			indexedAsKeyword: false,
			indexedLanguageId: '',
			label: {en_US: 'testRichTextField'},
			listTypeDefinitionId: 0,
			name: 'testRichTextField',
			required: false,
			system: false,
			type: 'Clob',
		},
		{
			DBType: 'String',
			businessType: 'Text',
			externalReferenceCode: 'Test-TextField',
			indexed: true,
			indexedAsKeyword: false,
			indexedLanguageId: '',
			label: {en_US: 'testTextField'},
			listTypeDefinitionId: 0,
			name: 'testTextField',
			required: false,
			system: false,
			type: 'String',
		},
	],
	objectRelationships: [
		{
			deletionType: 'cascade',
			externalReferenceCode: 'test-Relationship',
			label: {
				en_US: 'Test Relationship',
			},
			name: 'testRelationship',
			objectDefinitionExternalReferenceCode1: 'Test',
			objectDefinitionExternalReferenceCode2: 'Test',
			objectDefinitionName2: 'Test',
			parameterObjectFieldId: 0,
			parameterObjectFieldName: '',
			reverse: false,
			system: false,
			type: 'oneToMany',
		},
	],
	panelCategoryKey: 'site_administration.design',
	pluralLabel: {'en-US': 'Tests'},
	portlet: true,
	scope: 'site',
	status: {code: 0},
};

test('can handle OnlyAddNewRecords and UpdateChangedRecordFields import strategies with duplicate ERCs', async ({
	apiHelpers,
	dataMigrationCenterPage,
	page,
}) => {
	const objectDefinitionAPIClient =
		await apiHelpers.buildRestClient(ObjectDefinitionAPI);

	const {body: objectDefinition} =
		await objectDefinitionAPIClient.postObjectDefinition(
			companyObjectDefinition
		);

	apiHelpers.data.push({id: objectDefinition.id, type: 'objectDefinition'});

	await dataMigrationCenterPage.goto();
	await dataMigrationCenterPage.goToImportFile();

	await dataMigrationCenterPage.importFile(
		OBJECT_ENTRY_ENTITY_TYPE,
		path.join(__dirname, '/dependencies/object_entries.csv'),
		'INSERT',
		'PARTIAL_UPDATE'
	);

	await expect(
		page.getByText('The import process completed successfully.')
	).toBeVisible();

	await page.getByRole('button', {exact: true, name: 'Close'}).click();

	await dataMigrationCenterPage.importFile(
		OBJECT_ENTRY_ENTITY_TYPE,
		path.join(__dirname, '/dependencies/object_entry_same_erc.csv'),
		'INSERT',
		'PARTIAL_UPDATE'
	);

	await expect(
		page.getByText(
			'com.liferay.object.exception.DuplicateObjectEntryExternalReferenceCodeException'
		)
	).toBeVisible();
});

test('can import CSV file with an unexisting field', async ({
	apiHelpers,
	dataMigrationCenterPage,
	page,
}) => {
	const objectDefinitionAPIClient =
		await apiHelpers.buildRestClient(ObjectDefinitionAPI);

	const {body: objectDefinition} =
		await objectDefinitionAPIClient.postObjectDefinition(
			companyObjectDefinition
		);

	apiHelpers.data.push({id: objectDefinition.id, type: 'objectDefinition'});

	await dataMigrationCenterPage.goto();
	await dataMigrationCenterPage.goToImportFile();

	await dataMigrationCenterPage.importFile(
		OBJECT_ENTRY_ENTITY_TYPE,
		path.join(
			__dirname,
			'/dependencies/non_existing_field_object_entries.csv'
		),
		'UPSERT',
		'UPDATE'
	);

	await expect(
		page.getByText('The import process completed successfully.')
	).toBeVisible();

	expect(
		(
			await apiHelpers.objectEntry.getObjectDefinitionObjectEntries(
				'c/tests'
			)
		).items
	).toMatchObject([
		{
			externalReferenceCode: '83b46736-f89b-9b90-188c-497d06c08271',
			testAutoIncrementField: 'prefix-1-suffix',
			testBooleanField: false,
			testDateField: '2024-01-05T00:00:00.000Z',
			testDateTimeField: '2024-01-05T15:00:00.000Z',
			testDecimalField: 10.2,
			testFormulaField: 1,
			testIntegerField: 100,
			testLongInteger: 123456789,
			testLongTextField: 'This is a long text to test testLongTextField',
			testPrecisionDecimalField: 321.123,
			testTextField: 'Test',
		},
	]);
});

test('can import CSV file with custom columns order', async ({
	apiHelpers,
	dataMigrationCenterPage,
	page,
}) => {
	const objectDefinitionAPIClient =
		await apiHelpers.buildRestClient(ObjectDefinitionAPI);

	const {body: objectDefinition} =
		await objectDefinitionAPIClient.postObjectDefinition(
			siteObjectDefinition
		);

	apiHelpers.data.push({id: objectDefinition.id, type: 'objectDefinition'});

	await dataMigrationCenterPage.goto();
	await dataMigrationCenterPage.goToImportFile();
	await dataMigrationCenterPage.importFile(
		OBJECT_ENTRY_ENTITY_TYPE,
		path.join(
			__dirname,
			'/dependencies/custom_column_order_object_entries.csv'
		),
		'UPSERT',
		'UPDATE'
	);

	await expect(
		page.getByText('The import process completed successfully.')
	).toBeVisible();

	expect(
		(
			await apiHelpers.objectEntry.getObjectDefinitionObjectEntriesByScope(
				'c/tests',
				'Guest'
			)
		).items
	).toMatchObject([
		{
			externalReferenceCode: '83b46736-f89b-9b90-188c-497d06c08271',
			scopeKey: 'Guest',
			testAutoIncrementField: 'prefix-1-suffix',
			testBooleanField: true,
			testDateField: '2024-01-05T00:00:00.000Z',
			testDateTimeField: '2024-01-05T15:00:00.000Z',
			testDecimalField: 10.2,
			testFormulaField: 1,
			testIntegerField: 100,
			testLongInteger: 123456789,
			testLongTextField: 'This is a long text to test testLongTextField',
			testPrecisionDecimalField: 321.123,
			testRichTextField:
				'<p>This is a long text <strong>with some fomatting</strong> to text\n  testRichTextField  </p>',
			testRichTextFieldRawText:
				'This is a long text with some fomatting to text testRichTextField',
			testTextField: 'Test',
		},
	]);
});

test('can import CSV file with multiple site scoped object entries', async ({
	apiHelpers,
	dataMigrationCenterPage,
	page,
}) => {
	const objectDefinitionAPIClient =
		await apiHelpers.buildRestClient(ObjectDefinitionAPI);

	const {body: objectDefinition} =
		await objectDefinitionAPIClient.postObjectDefinition(
			siteObjectDefinition
		);

	apiHelpers.data.push({id: objectDefinition.id, type: 'objectDefinition'});

	await dataMigrationCenterPage.goto();
	await dataMigrationCenterPage.goToImportFile();

	await dataMigrationCenterPage.importFile(
		OBJECT_ENTRY_ENTITY_TYPE,
		path.join(__dirname, '/dependencies/two_entries_object_entries.csv'),
		'UPSERT',
		'UPDATE'
	);

	await expect(
		page.getByText('The import process completed successfully.')
	).toBeVisible();

	expect(
		(
			await apiHelpers.objectEntry.getObjectDefinitionObjectEntriesByScope(
				'c/tests',
				'Guest'
			)
		).items
	).toMatchObject([
		{
			externalReferenceCode: '83b46736-f89b-9b90-188c-497d06c08271',
			scopeKey: 'Guest',
			testAutoIncrementField: 'prefix-1-suffix',
			testBooleanField: true,
			testDateField: '2024-01-05T00:00:00.000Z',
			testDateTimeField: '2024-01-05T15:00:00.000Z',
			testDecimalField: 10.2,
			testFormulaField: 1,
			testIntegerField: 100,
			testLongInteger: 123456789,
			testLongTextField:
				'This is a long text to test testLongTextField. The first entry',
			testPrecisionDecimalField: 321.123,
			testRichTextField:
				'<p>This is a long text <strong>with some fomatting</strong> to text\n  testRichTextField. The first entry.  </p>',
			testRichTextFieldRawText:
				'This is a long text with some fomatting to text testRichTextField. The first entry.',
			testTextField: 'Test_FirstEntry',
		},
		{
			externalReferenceCode: '83b46736-f89b-9b90-188c-497d06c08273',
			scopeKey: 'Guest',
			testAutoIncrementField: 'prefix-2-suffix',
			testBooleanField: false,
			testDateField: '2024-01-06T00:00:00.000Z',
			testDateTimeField: '2024-01-06T15:00:00.000Z',
			testDecimalField: 11.2,
			testFormulaField: 1,
			testIntegerField: 101,
			testLongInteger: 123456790,
			testLongTextField:
				'This is a long text to test testLongTextField. The second entry',
			testPrecisionDecimalField: 123.321,
			testRichTextField:
				'<p>This is a long text <strong>with some fomatting</strong> to text\n  testRichTextField. The second entry.  </p>',
			testRichTextFieldRawText:
				'This is a long text with some fomatting to text testRichTextField. The second entry.',
			testTextField: 'Test_SecondEntry',
		},
	]);
});

test('can import CSV file with new and existing site scoped object entries', async ({
	apiHelpers,
	dataMigrationCenterPage,
	page,
}) => {
	const objectDefinitionAPIClient =
		await apiHelpers.buildRestClient(ObjectDefinitionAPI);

	const {body: objectDefinition} =
		await objectDefinitionAPIClient.postObjectDefinition(
			siteObjectDefinition
		);

	apiHelpers.data.push({id: objectDefinition.id, type: 'objectDefinition'});

	await dataMigrationCenterPage.goto();
	await dataMigrationCenterPage.goToImportFile();

	await dataMigrationCenterPage.importFile(
		OBJECT_ENTRY_ENTITY_TYPE,
		path.join(__dirname, '/dependencies/object_entries.csv'),
		'UPSERT',
		'UPDATE'
	);

	await page.getByRole('button', {exact: true, name: 'Close'}).click();

	await dataMigrationCenterPage.importFile(
		OBJECT_ENTRY_ENTITY_TYPE,
		path.join(
			__dirname,
			'/dependencies/two_entries_existing_nonmodified_object_entries.csv'
		),
		'UPSERT',
		'UPDATE'
	);

	await expect(
		page.getByText('The import process completed successfully.')
	).toBeVisible();

	expect(
		(
			await apiHelpers.objectEntry.getObjectDefinitionObjectEntriesByScope(
				'c/tests',
				'Guest'
			)
		).items
	).toMatchObject([
		{
			externalReferenceCode: '83b46736-f89b-9b90-188c-497d06c08271',
			scopeKey: 'Guest',
			testAutoIncrementField: 'prefix-1-suffix',
			testBooleanField: false,
			testDateField: '2024-01-05T00:00:00.000Z',
			testDateTimeField: '2024-01-05T15:00:00.000Z',
			testDecimalField: 10.2,
			testFormulaField: 1,
			testIntegerField: 100,
			testLongInteger: 123456789,
			testLongTextField:
				'This is a long text to test testLongTextField. The first entry',
			testPrecisionDecimalField: 321.123,
			testRichTextField:
				'<p>This is a long text <strong>with some fomatting</strong> to text\n  testRichTextField.  </p>',
			testRichTextFieldRawText:
				'This is a long text with some fomatting to text testRichTextField.',
			testTextField: 'Test',
		},
		{
			externalReferenceCode: '83b46736-f89b-9b90-188c-497d06c08273',
			scopeKey: 'Guest',
			testAutoIncrementField: 'prefix-2-suffix',
			testBooleanField: true,
			testDateField: '2024-01-06T00:00:00.000Z',
			testDateTimeField: '2024-01-06T15:00:00.000Z',
			testDecimalField: 11.2,
			testFormulaField: 1,
			testIntegerField: 101,
			testLongInteger: 123456790,
			testLongTextField:
				'This is a long text to test testLongTextField. The second entry',
			testPrecisionDecimalField: 123.321,
			testRichTextField:
				'<p>This is a long text <strong>with some fomatting</strong> to text\n  testRichTextField. New entry.  </p>',
			testRichTextFieldRawText:
				'This is a long text with some fomatting to text testRichTextField. New entry.',
			testTextField: 'Test_SecondEntry',
		},
	]);
});

test('can import CSV file with new and modified existing company scoped object entries', async ({
	apiHelpers,
	dataMigrationCenterPage,
	page,
}) => {
	const objectDefinitionAPIClient =
		await apiHelpers.buildRestClient(ObjectDefinitionAPI);

	const {body: objectDefinition} =
		await objectDefinitionAPIClient.postObjectDefinition(
			companyObjectDefinition
		);

	apiHelpers.data.push({id: objectDefinition.id, type: 'objectDefinition'});

	await dataMigrationCenterPage.goto();
	await dataMigrationCenterPage.goToImportFile();

	await dataMigrationCenterPage.importFile(
		OBJECT_ENTRY_ENTITY_TYPE,
		path.join(__dirname, '/dependencies/object_entries.csv'),
		'UPSERT',
		'UPDATE'
	);

	await page.getByRole('button', {exact: true, name: 'Close'}).click();

	await dataMigrationCenterPage.importFile(
		OBJECT_ENTRY_ENTITY_TYPE,
		path.join(
			__dirname,
			'/dependencies/two_entries_existing_modified_object_entries.csv'
		),
		'UPSERT',
		'UPDATE'
	);

	await expect(
		page.getByText('The import process completed successfully.')
	).toBeVisible();

	expect(
		(
			await apiHelpers.objectEntry.getObjectDefinitionObjectEntries(
				'c/tests'
			)
		).items
	).toMatchObject([
		{
			externalReferenceCode: '83b46736-f89b-9b90-188c-497d06c08271',
			testAutoIncrementField: 'prefix-1-suffix',
			testBooleanField: true,
			testDateField: '2024-01-05T00:00:00.000Z',
			testDateTimeField: '2024-01-05T15:00:00.000Z',
			testDecimalField: 10.2,
			testFormulaField: 1,
			testIntegerField: 100,
			testLongInteger: 123456789,
			testLongTextField:
				'This is a long text to test testLongTextField. The first entry',
			testPrecisionDecimalField: 321.123,
			testRichTextField:
				'<p>This is a long text <strong>with some fomatting</strong> to text\n  testRichTextField. The modified entry.  </p>',
			testRichTextFieldRawText:
				'This is a long text with some fomatting to text testRichTextField. The modified entry.',
			testTextField: 'Test_Modified',
		},
		{
			externalReferenceCode: '83b46736-f89b-9b90-188c-497d06c08273',
			testAutoIncrementField: 'prefix-2-suffix',
			testBooleanField: false,
			testDateField: '2024-01-06T00:00:00.000Z',
			testDateTimeField: '2024-01-06T15:00:00.000Z',
			testDecimalField: 11.2,
			testFormulaField: 1,
			testIntegerField: 101,
			testLongInteger: 123456790,
			testLongTextField:
				'This is a long text to test testLongTextField. The second entry',
			testPrecisionDecimalField: 123.321,
			testRichTextField:
				'<p>This is a long text <strong>with some fomatting</strong> to text\n  testRichTextField. The new entry.  </p>',
			testRichTextFieldRawText:
				'This is a long text with some fomatting to text testRichTextField. The new entry.',
			testTextField: 'Test_NewEntry',
		},
	]);
});

test('can import json file with attachment field', async ({
	apiHelpers,
	dataMigrationCenterPage,
	page,
}) => {
	const studentObjectDefinitionWithAttachment: ObjectDefinition = {
		active: true,
		externalReferenceCode: 'student-def',
		label: {
			en_US: 'Student',
		},
		name: 'Student',
		objectFields: [
			{
				DBType: 'String',
				businessType: 'Text',
				externalReferenceCode: 'studentName',
				indexed: true,
				indexedAsKeyword: false,
				indexedLanguageId: 'en_US',
				label: {
					en_US: 'Student name',
				},
				listTypeDefinitionId: 0,
				name: 'name',
				required: true,
				state: false,
				system: false,
				type: 'String',
			},
			{
				DBType: 'Long',
				businessType: 'Attachment',
				indexed: true,
				indexedAsKeyword: false,
				label: {
					en_US: 'customAttachment',
				},
				name: 'diploma',
				objectFieldSettings: [
					{
						name: 'acceptedFileExtensions',
						value: 'jpeg, jpg, pdf, png',
					} as any,
					{
						name: 'fileSource',
						value: 'documentsAndMedia',
					} as any,
					{
						name: 'maximumFileSize',
						value: '100',
					} as any,
				],
				required: false,
				type: 'Long',
			},
		],
		panelCategoryKey: 'control_panel.object',
		pluralLabel: {
			en_US: 'Students',
		},
		portlet: true,
		restContextPath: '/o/c/students',
		scope: 'company',
		status: {
			code: 0,
		},
	};
	const objectDefinitionAPIClient =
		await apiHelpers.buildRestClient(ObjectDefinitionAPI);

	const {body: studentResponse} =
		await objectDefinitionAPIClient.postObjectDefinition(
			studentObjectDefinitionWithAttachment
		);

	apiHelpers.data.push({id: studentResponse.id, type: 'objectDefinition'});

	const subjectObjectDefinition: ObjectDefinition = {
		active: true,
		externalReferenceCode: 'subject-def',
		label: {
			en_US: 'Subject',
		},
		name: 'Subject',
		objectFields: [
			{
				DBType: 'String',
				businessType: 'Text',
				externalReferenceCode: 'subject-name-field',
				indexed: true,
				indexedAsKeyword: false,
				indexedLanguageId: 'en_US',
				label: {
					en_US: 'name',
				},
				listTypeDefinitionId: 0,
				name: 'name',
				required: false,
				state: false,
				system: false,
				type: 'String',
			},
		],
		objectRelationships: [
			{
				deletionType: 'cascade',
				externalReferenceCode: 'student-subjects-relationship',
				label: {
					en_US: 'Student subjects',
				},
				name: 'subjectStudents',
				objectDefinitionExternalReferenceCode1: 'subject-def',
				objectDefinitionExternalReferenceCode2: 'student-def',
				objectDefinitionModifiable2: true,
				objectDefinitionName2: 'Student',
				objectDefinitionSystem2: false,
				objectField: {
					DBType: 'Long',
					businessType: 'Relationship',
					externalReferenceCode:
						'student-subjects-relationship-field',
					indexed: true,
					indexedAsKeyword: false,
					indexedLanguageId: '',
					label: {
						en_US: 'Student subjects',
					},
					name: 'r_subjectStudents_c_subjectId',
					readOnly: 'false',
					relationshipType: 'oneToMany',
					state: false,
					system: false,
					type: 'Long',
					unique: false,
				},
				parameterObjectFieldId: 0,
				parameterObjectFieldName: '',
				reverse: false,
				system: false,
				type: 'oneToMany',
			},
		],
		panelCategoryKey: 'control_panel.object',
		pluralLabel: {
			en_US: 'Subjects',
		},
		portlet: true,
		restContextPath: '/c/subjects',
		scope: 'company',
		status: {
			code: 0,
		},
	};

	const {body: subjectResponse} =
		await objectDefinitionAPIClient.postObjectDefinition(
			subjectObjectDefinition
		);

	apiHelpers.data.push({
		id: subjectResponse.id,
		type: 'objectDefinition',
	});

	await apiHelpers.objectEntry.postObjectEntry(
		{
			externalReferenceCode: 'Math',
			name: 'Math',
		},
		'c/subjects'
	);

	const objectEntry = await apiHelpers.objectEntry.postObjectEntry(
		{
			diploma: {
				fileBase64: 'R0lGODlhAQABAAAAACw=',
				name: 'diploma.png',
			},
			externalReferenceCode: 'studentERC',
			name: 'Jane',
			r_subjectStudents_c_subjectERC: 'Math',
		},
		'c/students'
	);

	apiHelpers.data.push({
		id: objectEntry.diploma.id,
		type: 'document',
	});

	const filePath = createTempFile(
		getRandomString() + '.json',
		`[{"diploma": {
			"id":${objectEntry.diploma.id},
			"link":
				{
					"href": "${objectEntry.diploma.link.href}",
					"label": "${objectEntry.diploma.link.label}"
				},
				"name": "${objectEntry.diploma.name}"
			},
			"name": "John",
			"r_subjectStudents_c_subjectERC": "Math"
		}]`
	);

	await dataMigrationCenterPage.goto();
	await dataMigrationCenterPage.goToImportFile();

	await dataMigrationCenterPage.importFile(
		'com.liferay.object.rest.dto.v1_0.ObjectEntry#C_Student',
		filePath,
		'INSERT',
		'PARTIAL_UPDATE'
	);

	await expect(
		page.getByText('The import process completed successfully.')
	).toBeVisible();
	expect(
		(
			await apiHelpers.objectEntry.getObjectDefinitionObjectEntries(
				'c/students'
			)
		).items
	).toEqual(
		expect.arrayContaining([
			expect.objectContaining({
				diploma: expect.objectContaining({
					link: expect.objectContaining({label: 'diploma.png'}),
				}),
				name: 'Jane',
				r_subjectStudents_c_subjectERC: 'Math',
			}),
			expect.objectContaining({
				diploma: expect.objectContaining({
					link: expect.objectContaining({label: 'diploma.png'}),
				}),
				name: 'John',
				r_subjectStudents_c_subjectERC: 'Math',
			}),
		])
	);
});

test('can map all imported fields', async ({
	apiHelpers,
	dataMigrationCenterPage,
	page,
}) => {
	const objectDefinitionAPIClient =
		await apiHelpers.buildRestClient(ObjectDefinitionAPI);
	const {body: objectDefinition} =
		await objectDefinitionAPIClient.postObjectDefinition(
			siteObjectDefinition
		);
	apiHelpers.data.push({id: objectDefinition.id, type: 'objectDefinition'});

	await dataMigrationCenterPage.goto();
	await dataMigrationCenterPage.goToImportFile();

	await dataMigrationCenterPage.selectEntityType(OBJECT_ENTRY_ENTITY_TYPE);

	await expect(
		page.getByText('externalReferenceCode', {exact: true})
	).toBeVisible();
	await expect(page.getByText('keywords', {exact: true})).toBeVisible();
	await expect(page.getByText('taxonomyCategoryIds')).toBeVisible();
	await expect(page.getByText('testAutoIncrementField')).toBeVisible();
	await expect(page.getByText('testBooleanField')).toBeVisible();
	await expect(page.getByText('testDateField')).toBeVisible();
	await expect(page.getByText('testDecimalField')).toBeVisible();
	await expect(page.getByText('testIntegerField')).toBeVisible();
	await expect(page.getByText('testLongInteger')).toBeVisible();
	await expect(page.getByText('testLongTextField')).toBeVisible();
	await expect(page.getByText('testPrecisionDecimalField')).toBeVisible();
	await expect(page.getByText('testRichTextField')).toBeVisible();
	await expect(page.getByText('testTextField')).toBeVisible();
});

test('can preview CSV file', async ({
	apiHelpers,
	dataMigrationCenterPage,
	page,
}) => {
	const objectDefinitionAPIClient =
		await apiHelpers.buildRestClient(ObjectDefinitionAPI);

	const {body: objectDefinition} =
		await objectDefinitionAPIClient.postObjectDefinition(
			siteObjectDefinition
		);

	apiHelpers.data.push({id: objectDefinition.id, type: 'objectDefinition'});

	await dataMigrationCenterPage.goto();
	await dataMigrationCenterPage.goToImportFile();

	await dataMigrationCenterPage.selectFile(
		path.join(__dirname, '/dependencies/object_entries.csv')
	);

	await dataMigrationCenterPage.selectEntityType(OBJECT_ENTRY_ENTITY_TYPE);

	await page.waitForTimeout(2000);

	await page.getByRole('button', {name: 'Next'}).click();

	await expect(
		page
			.getByLabel('Preview')
			.getByRole('cell', {name: 'externalReferenceCode'})
	).toBeVisible();
	await expect(
		page
			.getByLabel('Preview')
			.getByRole('cell', {exact: true, name: 'testAutoIncrementField'})
	).toBeVisible();
	await expect(
		page
			.getByLabel('Preview')
			.getByRole('cell', {exact: true, name: 'testBooleanField'})
	).toBeVisible();
	await expect(
		page.getByLabel('Preview').getByRole('cell', {name: 'testDateField'})
	).toBeVisible();
	await expect(
		page
			.getByLabel('Preview')
			.getByRole('cell', {name: 'testDateTimeField'})
	).toBeVisible();
	await expect(
		page.getByLabel('Preview').getByRole('cell', {name: 'testDecimalField'})
	).toBeVisible();
	await expect(
		page.getByLabel('Preview').getByRole('cell', {name: 'testIntegerField'})
	).toBeVisible();
	await expect(
		page.getByLabel('Preview').getByRole('cell', {name: 'testLongInteger'})
	).toBeVisible();
	await expect(
		page
			.getByLabel('Preview')
			.getByRole('cell', {exact: true, name: 'testLongTextField'})
	).toBeVisible();
	await expect(
		page
			.getByLabel('Preview')
			.getByRole('cell', {name: 'testPrecisionDecimalField'})
	).toBeVisible();
	await expect(
		page
			.getByLabel('Preview')
			.getByRole('cell', {exact: true, name: 'testRichTextField'})
	).toBeVisible();
	await expect(
		page
			.getByLabel('Preview')
			.getByRole('cell', {exact: true, name: 'testTextField'})
	).toBeVisible();
});

test('can show duplicate error message with CSV import existing entry and only add new record fields', async ({
	apiHelpers,
	dataMigrationCenterPage,
	page,
}) => {
	const objectDefinitionAPIClient =
		await apiHelpers.buildRestClient(ObjectDefinitionAPI);

	const {body: objectDefinition} =
		await objectDefinitionAPIClient.postObjectDefinition(
			companyObjectDefinition
		);

	apiHelpers.data.push({id: objectDefinition.id, type: 'objectDefinition'});

	await dataMigrationCenterPage.goto();
	await dataMigrationCenterPage.goToImportFile();

	await dataMigrationCenterPage.importFile(
		OBJECT_ENTRY_ENTITY_TYPE,
		path.join(__dirname, '/dependencies/object_entries.csv'),
		'UPSERT',
		'UPDATE'
	);

	await page.getByRole('button', {exact: true, name: 'Close'}).click();

	await dataMigrationCenterPage.importFile(
		OBJECT_ENTRY_ENTITY_TYPE,
		path.join(__dirname, '/dependencies/object_entry_same_erc.csv'),
		'INSERT',
		'UPDATE'
	);

	await expect(
		page.getByText(
			'com.liferay.object.exception.DuplicateObjectEntryExternalReferenceCodeException'
		)
	).toBeVisible();
});

test('can show unique contraint error message with CSV import existing entry and only add new record fields', async ({
	apiHelpers,
	dataMigrationCenterPage,
	page,
}) => {
	const objectDefinitionAPIClient =
		await apiHelpers.buildRestClient(ObjectDefinitionAPI);

	const {body: objectDefinition} =
		await objectDefinitionAPIClient.postObjectDefinition(
			companyObjectDefinition
		);

	apiHelpers.data.push({id: objectDefinition.id, type: 'objectDefinition'});

	await dataMigrationCenterPage.goto();
	await dataMigrationCenterPage.goToImportFile();

	await dataMigrationCenterPage.importFile(
		OBJECT_ENTRY_ENTITY_TYPE,
		path.join(__dirname, '/dependencies/object_entries.csv'),
		'UPSERT',
		'UPDATE'
	);

	await page.getByRole('button', {exact: true, name: 'Close'}).click();

	await dataMigrationCenterPage.importFile(
		OBJECT_ENTRY_ENTITY_TYPE,
		path.join(__dirname, '/dependencies/object_entries.csv'),
		'INSERT',
		'UPDATE'
	);

	await expect(
		page.getByText(
			'com.liferay.object.exception.ObjectEntryValuesException$UniqueValueConstraintViolation'
		)
	).toBeVisible();
});

test('cannot import CSV file without headers row', async ({
	dataMigrationCenterPage,
	page,
}) => {
	await dataMigrationCenterPage.goto();
	await dataMigrationCenterPage.goToImportFile();

	await dataMigrationCenterPage.selectFile(
		path.join(__dirname, '/dependencies/no_headers_object_entries.csv')
	);

	await page.getByRole('button', {name: 'Next'}).click();

	await expect(page.getByText('Unexpected Error')).toBeVisible();
	await expect(
		page.getByText(
			'Error:Please upload a file and select the required columns before continuing.'
		)
	).toBeVisible();
});

test('cannot import CSV file with empty headers row', async ({
	apiHelpers,
	dataMigrationCenterPage,
	page,
}) => {
	const objectDefinitionAPIClient =
		await apiHelpers.buildRestClient(ObjectDefinitionAPI);

	const {body: objectDefinition} =
		await objectDefinitionAPIClient.postObjectDefinition(
			siteObjectDefinition
		);

	apiHelpers.data.push({id: objectDefinition.id, type: 'objectDefinition'});

	await dataMigrationCenterPage.goto();
	await dataMigrationCenterPage.goToImportFile();

	await dataMigrationCenterPage.selectFile(
		path.join(
			__dirname,
			'/dependencies/empty_header_values_object_entries.csv'
		)
	);

	await dataMigrationCenterPage.selectEntityType(OBJECT_ENTRY_ENTITY_TYPE);

	await page.waitForTimeout(2000);

	await page.getByRole('button', {name: 'Next'}).click();

	await expect(
		page.getByText(
			'Error:You must map at least one field and all required fields before continuing.'
		)
	).toBeVisible();
});

test('cannot import CSV file with object entry with UPSERT strategy', async ({
	apiHelpers,
	dataMigrationCenterPage,
	page,
}) => {
	const objectDefinitionAPIClient =
		await apiHelpers.buildRestClient(ObjectDefinitionAPI);

	const {body: objectDefinition} =
		await objectDefinitionAPIClient.postObjectDefinition(
			companyObjectDefinition
		);

	apiHelpers.data.push({id: objectDefinition.id, type: 'objectDefinition'});

	await dataMigrationCenterPage.goto();
	await dataMigrationCenterPage.goToImportFile();

	await dataMigrationCenterPage.importFile(
		OBJECT_ENTRY_ENTITY_TYPE,
		path.join(__dirname, '/dependencies/object_entries.csv'),
		'UPSERT',
		'PARTIAL_UPDATE'
	);

	await expect(
		page.getByText(
			'jakarta.ws.rs.NotSupportedException: Create strategy "UPSERT" is not supported for'
		)
	).toBeVisible();
});

test('cannot import empty CSV file', async ({
	apiHelpers,
	dataMigrationCenterPage,
	page,
}) => {
	const objectDefinitionAPIClient =
		await apiHelpers.buildRestClient(ObjectDefinitionAPI);

	const {body: objectDefinition} =
		await objectDefinitionAPIClient.postObjectDefinition(
			companyObjectDefinition
		);

	apiHelpers.data.push({id: objectDefinition.id, type: 'objectDefinition'});

	await dataMigrationCenterPage.goto();
	await dataMigrationCenterPage.goToImportFile();

	await dataMigrationCenterPage.selectFile(
		path.join(__dirname, '/dependencies/empty_object_entries.csv')
	);

	await dataMigrationCenterPage.selectEntityType(OBJECT_ENTRY_ENTITY_TYPE);

	await page.waitForTimeout(2000);

	await page.getByRole('button', {name: 'Next'}).click();

	await expect(page.getByText('Error:Please upload a file.')).toBeVisible();
});

test('can see correct custom object name in dropdown', async ({
	apiHelpers,
	dataMigrationCenterPage,
}) => {
	const objectDefinitionAPIClient =
		await apiHelpers.buildRestClient(ObjectDefinitionAPI);

	const {body: objectDefinition} =
		await objectDefinitionAPIClient.postObjectDefinition({
			active: true,
			externalReferenceCode: 'stockERC',
			label: {
				en_US: 'stock',
			},
			name: 'Stock',
			objectFields: [
				{
					DBType: 'String',
					businessType: 'Text',
					externalReferenceCode: 'nameERC',
					indexed: true,
					indexedAsKeyword: true,
					label: {
						en_US: 'name',
					},
					name: 'name',
					required: true,
				},
			],
			pluralLabel: {
				en_US: 'stocks',
			},
			portlet: true,
			scope: 'company',
			status: {
				code: 0,
			},
		});

	apiHelpers.data.push({id: objectDefinition.id, type: 'objectDefinition'});

	await apiHelpers.objectEntry.postObjectEntry(
		{
			externalReferenceCode: 'nameERC',
			name: 'Stock Entry',
		},
		'c/stocks'
	);

	await dataMigrationCenterPage.goto();
	await dataMigrationCenterPage.goToImportFile();

	expect(
		await dataMigrationCenterPage.page
			.getByLabel('Entity Type')
			.textContent()
	).toContain('Stock (v1.0 - Liferay Object REST)');
});

test('can see ObjectDefinition entity type in dropdown', async ({
	dataMigrationCenterPage,
}) => {
	await dataMigrationCenterPage.goto();
	await dataMigrationCenterPage.goToImportFile();

	expect(
		await dataMigrationCenterPage.page
			.getByLabel('Entity Type')
			.textContent()
	).toContain('ObjectDefinition (v1.0 - Liferay Object Admin REST)');
});

test('cannot see relationship nested field', async ({
	apiHelpers,
	dataMigrationCenterPage,
	page,
}) => {
	const objectDefinitionAPIClient =
		await apiHelpers.buildRestClient(ObjectDefinitionAPI);

	const {body: objectDefinition} =
		await objectDefinitionAPIClient.postObjectDefinition(
			companyObjectDefinition
		);

	apiHelpers.data.push({id: objectDefinition.id, type: 'objectDefinition'});

	await dataMigrationCenterPage.goto();
	await dataMigrationCenterPage.goToImportFile();

	await dataMigrationCenterPage.selectEntityType(OBJECT_ENTRY_ENTITY_TYPE);

	await expect(page.getByText('testRelationship')).not.toBeVisible();
});

test.describe('can rely on anyOf form validation', () => {
	const studentObjectDefinition: ObjectDefinition = {
		active: true,
		externalReferenceCode: 'student-definition',
		label: {
			en_US: 'Student',
		},
		name: 'Student',
		objectFields: [
			{
				DBType: 'String',
				businessType: 'Text',
				externalReferenceCode: 'student-name-field',
				indexed: true,
				indexedAsKeyword: false,
				indexedLanguageId: 'en_US',
				label: {
					en_US: 'Student name',
				},
				listTypeDefinitionId: 0,
				name: 'studentName',
				required: true,
				state: false,
				system: false,
				type: 'String',
			},
		],
		objectRelationships: [
			{
				deletionType: 'cascade',
				externalReferenceCode: 'student-subjects-relationship-1',
				label: {
					en_US: 'Student subjects 1',
				},
				name: 'studentSubjects1',
				objectDefinitionExternalReferenceCode1: 'student-definition',
				objectDefinitionExternalReferenceCode2: 'subject-definition',
				objectDefinitionModifiable2: true,
				objectDefinitionName2: 'Subject',
				objectDefinitionSystem2: false,
				objectField: {
					DBType: 'Long',
					businessType: 'Relationship',
					externalReferenceCode:
						'student-subjects-relationship-field-1',
					indexed: true,
					indexedAsKeyword: false,
					indexedLanguageId: '',
					label: {
						en_US: 'Student subjects 1',
					},
					name: 'r_studentSubjects1_c_studentId',
					readOnly: 'false',
					relationshipType: 'oneToMany',
					required: true,
					state: false,
					system: false,
					type: 'Long',
					unique: false,
				},
				parameterObjectFieldId: 0,
				parameterObjectFieldName: '',
				reverse: false,
				system: false,
				type: 'oneToMany',
			},
			{
				deletionType: 'cascade',
				externalReferenceCode: 'student-subjects-relationship-2',
				label: {
					en_US: 'Student subjects 2',
				},
				name: 'studentSubjects2',
				objectDefinitionExternalReferenceCode1: 'student-definition',
				objectDefinitionExternalReferenceCode2: 'subject-definition',
				objectDefinitionModifiable2: true,
				objectDefinitionName2: 'Subject',
				objectDefinitionSystem2: false,
				objectField: {
					DBType: 'Long',
					businessType: 'Relationship',
					externalReferenceCode:
						'student-subjects-relationship-field-2',
					indexed: true,
					indexedAsKeyword: false,
					indexedLanguageId: '',
					label: {
						en_US: 'Student subjects 2',
					},
					name: 'r_studentSubjects2_c_studentId',
					readOnly: 'false',
					relationshipType: 'oneToMany',
					required: true,
					state: false,
					system: false,
					type: 'Long',
					unique: false,
				},
				parameterObjectFieldId: 0,
				parameterObjectFieldName: '',
				reverse: false,
				system: false,
				type: 'oneToMany',
			},
			{
				deletionType: 'cascade',
				externalReferenceCode: 'student-subjects-relationship-3',
				label: {
					en_US: 'Student subjects 3',
				},
				name: 'studentSubjects3',
				objectDefinitionExternalReferenceCode1: 'student-definition',
				objectDefinitionExternalReferenceCode2: 'subject-definition',
				objectDefinitionModifiable2: true,
				objectDefinitionName2: 'Subject',
				objectDefinitionSystem2: false,
				objectField: {
					DBType: 'Long',
					businessType: 'Relationship',
					externalReferenceCode:
						'student-subjects-relationship-field-3',
					indexed: true,
					indexedAsKeyword: false,
					indexedLanguageId: '',
					label: {
						en_US: 'Student subjects 3',
					},
					name: 'r_studentSubjects3_c_studentId',
					readOnly: 'false',
					relationshipType: 'oneToMany',
					required: false,
					state: false,
					system: false,
					type: 'Long',
					unique: false,
				},
				parameterObjectFieldId: 0,
				parameterObjectFieldName: '',
				reverse: false,
				system: false,
				type: 'oneToMany',
			},
		],
		panelCategoryKey: 'control_panel.object',
		pluralLabel: {
			en_US: 'Students',
		},
		portlet: true,
		restContextPath: '/o/c/students',
		scope: 'company',
		status: {
			code: 0,
		},
	};

	const subjectObjectDefinition: ObjectDefinition = {
		active: true,
		externalReferenceCode: 'subject-definition',
		label: {
			en_US: 'Subject',
		},
		name: 'Subject',
		objectFields: [
			{
				DBType: 'String',
				businessType: 'Text',
				externalReferenceCode: 'subject-name-field',
				indexed: true,
				indexedAsKeyword: false,
				indexedLanguageId: 'en_US',
				label: {
					en_US: 'Subject name',
				},
				listTypeDefinitionId: 0,
				name: 'subjectName',
				required: false,
				state: false,
				system: false,
				type: 'String',
			},
		],
		panelCategoryKey: 'control_panel.object',
		pluralLabel: {
			en_US: 'Subjects',
		},
		portlet: true,
		restContextPath: '/o/c/subjects',
		scope: 'company',
		status: {
			code: 0,
		},
	};

	test('cannot preview fields with no required anyOf fields selected', async ({
		apiHelpers,
		dataMigrationCenterPage,
		page,
	}) => {
		const objectDefinitionAPIClient =
			await apiHelpers.buildRestClient(ObjectDefinitionAPI);

		const {body: subjectResponse} =
			await objectDefinitionAPIClient.postObjectDefinition(
				subjectObjectDefinition
			);

		apiHelpers.data.push({
			id: subjectResponse.id,
			type: 'objectDefinition',
		});

		const {body: studentResponse} =
			await objectDefinitionAPIClient.postObjectDefinition(
				studentObjectDefinition
			);

		apiHelpers.data.push({
			id: studentResponse.id,
			type: 'objectDefinition',
		});

		await dataMigrationCenterPage.goto();
		await dataMigrationCenterPage.goToImportFile();

		await dataMigrationCenterPage.selectEntityType(
			'com.liferay.object.rest.dto.v1_0.ObjectEntry#C_Subject'
		);

		await expect(
			page.getByLabel('r_studentSubjects1_c_studentERC', {exact: true})
		).toBeEmpty();
		await expect(
			page.getByLabel('r_studentSubjects1_c_studentId', {exact: true})
		).toBeEmpty();
		await expect(
			page.getByLabel('r_studentSubjects2_c_studentERC', {exact: true})
		).toBeEmpty();
		await expect(
			page.getByLabel('r_studentSubjects2_c_studentId', {exact: true})
		).toBeEmpty();
		await expect(
			page.getByLabel('r_studentSubjects3_c_studentERC', {exact: true})
		).toBeEmpty();
		await expect(
			page.getByLabel('r_studentSubjects3_c_studentId', {exact: true})
		).toBeEmpty();

		await dataMigrationCenterPage.selectFile(
			path.join(__dirname, '/dependencies/any_of_object_entries.csv')
		);
		await page.getByRole('button', {name: 'Next'}).click();
		await expect(
			page.getByText(
				'Error:You must map at least one field and all required fields before continuing.'
			)
		).toBeVisible();
	});

	test('cannot preview fields with one required anyOf field missing', async ({
		apiHelpers,
		dataMigrationCenterPage,
		page,
	}) => {
		const objectDefinitionAPIClient =
			await apiHelpers.buildRestClient(ObjectDefinitionAPI);

		const {body: subjectResponse} =
			await objectDefinitionAPIClient.postObjectDefinition(
				subjectObjectDefinition
			);

		apiHelpers.data.push({
			id: subjectResponse.id,
			type: 'objectDefinition',
		});

		const {body: studentResponse} =
			await objectDefinitionAPIClient.postObjectDefinition(
				studentObjectDefinition
			);

		apiHelpers.data.push({
			id: studentResponse.id,
			type: 'objectDefinition',
		});

		await dataMigrationCenterPage.goto();
		await dataMigrationCenterPage.goToImportFile();

		await dataMigrationCenterPage.selectEntityType(
			'com.liferay.object.rest.dto.v1_0.ObjectEntry#C_Subject'
		);

		await expect(
			page.getByLabel('r_studentSubjects1_c_studentERC', {exact: true})
		).toBeEmpty();
		await expect(
			page.getByLabel('r_studentSubjects1_c_studentId', {exact: true})
		).toBeEmpty();
		await expect(
			page.getByLabel('r_studentSubjects2_c_studentERC', {exact: true})
		).toBeEmpty();
		await expect(
			page.getByLabel('r_studentSubjects2_c_studentId', {exact: true})
		).toBeEmpty();
		await expect(
			page.getByLabel('r_studentSubjects3_c_studentERC', {exact: true})
		).toBeEmpty();
		await expect(
			page.getByLabel('r_studentSubjects3_c_studentId', {exact: true})
		).toBeEmpty();

		await dataMigrationCenterPage.selectFile(
			path.join(__dirname, '/dependencies/any_of_object_entries.csv')
		);

		await page
			.getByLabel('r_studentSubjects1_c_studentERC')
			.selectOption('studentSubjects1_ERC');

		await page.getByRole('button', {name: 'Next'}).click();

		await expect(
			page
				.getByText(
					'Error:You must map at least one field and all required fields before continuing.'
				)
				.first()
		).toBeVisible();
	});

	test('can preview import with all required anyOf fields selected', async ({
		apiHelpers,
		dataMigrationCenterPage,
		page,
	}) => {
		const objectDefinitionAPIClient =
			await apiHelpers.buildRestClient(ObjectDefinitionAPI);

		const {body: subjectResponse} =
			await objectDefinitionAPIClient.postObjectDefinition(
				subjectObjectDefinition
			);

		apiHelpers.data.push({
			id: subjectResponse.id,
			type: 'objectDefinition',
		});

		const {body: studentResponse} =
			await objectDefinitionAPIClient.postObjectDefinition(
				studentObjectDefinition
			);

		apiHelpers.data.push({
			id: studentResponse.id,
			type: 'objectDefinition',
		});

		await dataMigrationCenterPage.goto();
		await dataMigrationCenterPage.goToImportFile();

		await dataMigrationCenterPage.selectEntityType(
			'com.liferay.object.rest.dto.v1_0.ObjectEntry#C_Subject'
		);

		await expect(
			page.getByLabel('r_studentSubjects1_c_studentERC', {exact: true})
		).toBeEmpty();
		await expect(
			page.getByLabel('r_studentSubjects1_c_studentId', {exact: true})
		).toBeEmpty();
		await expect(
			page.getByLabel('r_studentSubjects2_c_studentERC', {exact: true})
		).toBeEmpty();
		await expect(
			page.getByLabel('r_studentSubjects2_c_studentId', {exact: true})
		).toBeEmpty();
		await expect(
			page.getByLabel('r_studentSubjects3_c_studentERC', {exact: true})
		).toBeEmpty();
		await expect(
			page.getByLabel('r_studentSubjects3_c_studentId', {exact: true})
		).toBeEmpty();

		await dataMigrationCenterPage.selectFile(
			path.join(__dirname, '/dependencies/any_of_object_entries.csv')
		);

		await page
			.getByLabel('r_studentSubjects1_c_studentERC')
			.selectOption('studentSubjects1_ERC');
		await page
			.getByLabel('r_studentSubjects2_c_studentERC')
			.selectOption('studentSubjects2_ERC');

		await page.getByRole('button', {name: 'Next'}).click();

		await expect(
			page
				.getByLabel('Preview')
				.getByRole('cell', {name: 'r_studentSubjects1_c_studentERC'})
		).toBeVisible();
		await expect(
			page
				.getByLabel('Preview')
				.getByRole('cell', {name: 'r_studentSubjects2_c_studentERC'})
		).toBeVisible();
	});
});
