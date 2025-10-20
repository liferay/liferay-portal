/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {
	ObjectDefinition,
	ObjectDefinitionAPI,
} from '@liferay/object-admin-rest-client-js';
import {expect, mergeTests} from '@playwright/test';

import {dataApiHelpersTest} from '../../../fixtures/dataApiHelpersTest';
import {headlessDiscoveryPagesTest} from '../../../fixtures/headlessDiscoveryWebPagesTest';
import {loginTest} from '../../../fixtures/loginTest';
import {uiElementsPageTest} from '../../../fixtures/uiElementsTest';
import {waitForLoading} from '../../osb-faro-web/main/utils/loading';
import {headlessBuilderPagesTest} from './fixtures/headlessBuilderPagesTest';

export const testFeatureFlagsEnabled = mergeTests(
	dataApiHelpersTest,
	headlessBuilderPagesTest({
		'LPD-21414': {enabled: true},
	}),
	headlessDiscoveryPagesTest,
	loginTest(),
	uiElementsPageTest
);

export const testFeatureFlagsDisabled = mergeTests(
	dataApiHelpersTest,
	headlessBuilderPagesTest({
		'LPD-21414': {enabled: false},
	}),
	loginTest()
);

const objectDefinitionData: ObjectDefinition = {
	active: true,
	externalReferenceCode: `objectDefinition`,
	label: {
		en_US: `objectDefinition`,
	},
	name: `ObjectDefinition`,
	objectFields: [
		{
			DBType: 'String',
			businessType: 'Text',
			externalReferenceCode: 'ObjectFieldERC',
			indexed: true,
			indexedAsKeyword: false,
			indexedLanguageId: 'en_US',
			label: {
				en_US: 'Object Field',
			},
			listTypeDefinitionId: 0,
			name: 'objectField',
			required: false,
			state: false,
			system: false,
			type: 'String',
		},
	],
	pluralLabel: {
		en_US: `objectDefinitions`,
	},
	portlet: true,
	scope: 'company',
	status: {
		code: 0,
	},
};

const objectDefinition1Data: ObjectDefinition = {
	active: true,
	externalReferenceCode: `customObjectDefinition1`,
	label: {
		en_US: `objectDefinition1`,
	},
	name: `ObjectDefinition1`,
	objectFields: [
		{
			DBType: 'String',
			businessType: 'Text',
			externalReferenceCode: 'ObjectField1ERC',
			indexed: true,
			indexedAsKeyword: false,
			indexedLanguageId: 'en_US',
			label: {
				en_US: 'Object Field',
			},
			listTypeDefinitionId: 0,
			name: 'objectField',
			required: false,
			state: false,
			system: false,
			type: 'String',
		},
	],
	objectRelationships: [
		{
			deletionType: 'cascade',
			externalReferenceCode: 'modifiable-system',
			label: {
				en_US: 'Test Modifiable System Object',
			},
			name: 'testModifiableSystem',
			objectDefinitionExternalReferenceCode1: 'customObjectDefinition1',
			objectDefinitionExternalReferenceCode2: 'L_API_APPLICATION',
			objectDefinitionName2: 'APIApplication',
			parameterObjectFieldId: 0,
			parameterObjectFieldName: '',
			reverse: false,
			system: false,
			type: 'oneToMany',
		},
		{
			deletionType: 'cascade',
			externalReferenceCode: 'unmodifiable-system',
			label: {
				en_US: 'Test Unmodifiable System Object',
			},
			name: 'testUnmodifiableSystem',
			objectDefinitionExternalReferenceCode1: 'customObjectDefinition1',
			objectDefinitionExternalReferenceCode2: 'L_ORGANIZATION',
			objectDefinitionName2: 'Organization',
			parameterObjectFieldId: 0,
			parameterObjectFieldName: '',
			reverse: false,
			system: false,
			type: 'oneToMany',
		},
		{
			deletionType: 'cascade',
			externalReferenceCode: 'unmodifiable-system-allowed',
			label: {
				en_US: 'Test Unmodifiable Allowed System Object',
			},
			name: 'testUnmodifiableSystemAllowed',
			objectDefinitionExternalReferenceCode1: 'objectDefinition1',
			objectDefinitionExternalReferenceCode2: 'L_ACCOUNT',
			objectDefinitionName2: 'AccountEntry',
			parameterObjectFieldId: 0,
			parameterObjectFieldName: '',
			reverse: false,
			system: false,
			type: 'oneToMany',
		},
		{
			deletionType: 'cascade',
			externalReferenceCode: 'custom',
			label: {
				en_US: 'Test Custom Object',
			},
			name: 'testCustom',
			objectDefinitionExternalReferenceCode1: 'customObjectDefinition1',
			objectDefinitionExternalReferenceCode2: 'customObjectDefinition',
			objectDefinitionName2: 'objectDefinition',
			parameterObjectFieldId: 0,
			parameterObjectFieldName: '',
			reverse: false,
			system: false,
			type: 'oneToMany',
		},
	],
	pluralLabel: {
		en_US: `objectDefinitions1`,
	},
	portlet: true,
	scope: 'company',
	status: {
		code: 0,
	},
};

const applicationData1 = {
	apiApplicationToAPISchemas: [
		{
			description: 'API Application Schema',
			externalReferenceCode: 'api-application-schema',
			mainObjectDefinitionERC: 'L_API_APPLICATION',
			name: 'API Application Schema',
		},
	],
	applicationStatus: 'published',
	baseURL: 'basic-application',
	description: 'Test API Application',
	externalReferenceCode: 'basic-application',
	title: 'Basic application',
};

const applicationData2 = {
	apiApplicationToAPISchemas: [
		{
			description: 'API Application Schema',
			externalReferenceCode: 'api-application-schema',
			mainObjectDefinitionERC: 'L_API_APPLICATION',
			name: 'API Application Schema',
		},
		{
			description: 'API Application Second Schema',
			externalReferenceCode: 'api-application-second-schema',
			mainObjectDefinitionERC: 'L_API_APPLICATION',
			name: 'API Application Second Schema',
		},
	],
	applicationStatus: 'unpublished',
	baseURL: 'basic-application',
	description: 'Test API Application',
	externalReferenceCode: 'basic-application',
	title: 'Basic application',
};

testFeatureFlagsDisabled(
	'Can see all available object definitions on schema creation',
	async ({apiHelpers, applicationPage, headlessBuilderPage}) => {
		const objectDefinitions = [];

		const objectDefinitionAPIClient =
			await apiHelpers.buildRestClient(ObjectDefinitionAPI);

		for (let i = 0; i <= 21; i++) {
			const objectDefinition = (
				await objectDefinitionAPIClient.postObjectDefinition({
					active: true,
					externalReferenceCode: `objectDefinition${i}`,
					label: {
						en_US: `objectDefinition${i}`,
					},
					name: `ObjectDefinition${i}`,
					objectFields: [
						{
							DBType: 'String',
							businessType: 'Text',
							externalReferenceCode: 'ObjectFieldERC',
							indexed: true,
							indexedAsKeyword: false,
							indexedLanguageId: 'en_US',
							label: {
								en_US: 'Object Field',
							},
							listTypeDefinitionId: 0,
							name: 'objectField',
							required: false,
							state: false,
							system: false,
							type: 'String',
						},
					],
					pluralLabel: {
						en_US: `objectDefinitions${i}`,
					},
					portlet: true,
					scope: 'company',
					status: {
						code: 0,
					},
				})
			).body;

			objectDefinitions.push(objectDefinition);

			apiHelpers.data.push({
				id: objectDefinition.id,
				type: 'objectDefinition',
			});
		}

		const application = await apiHelpers.objectEntry.postObjectEntry(
			applicationData1,
			'headless-builder/applications'
		);

		apiHelpers.data.push({id: application.id, type: 'apiApplication'});

		await headlessBuilderPage.goto();
		await headlessBuilderPage.goToEditApplication(application.title);
		await applicationPage.goToSchemasTab();
		await applicationPage.addSchemaButton.click();
		await applicationPage.schemaObjectDefinitionSelector.click();

		objectDefinitions.forEach((objectDefinition) => {
			expect(
				applicationPage.page.getByRole('menuitem', {
					exact: true,
					name: objectDefinition.name,
				})
			).toBeVisible();
		});
	}
);

testFeatureFlagsDisabled(
	'Can see allowed object definitions on schema creation',
	async ({apiHelpers, applicationPage, headlessBuilderPage}) => {
		const objectDefinitionAPIClient =
			await apiHelpers.buildRestClient(ObjectDefinitionAPI);

		const {body: objectDefinition} =
			await objectDefinitionAPIClient.postObjectDefinition(
				objectDefinitionData
			);

		apiHelpers.data.push({
			id: objectDefinition.id,
			type: 'objectDefinition',
		});

		const application = await apiHelpers.objectEntry.postObjectEntry(
			applicationData1,
			'headless-builder/applications'
		);

		apiHelpers.data.push({id: application.id, type: 'apiApplication'});

		await headlessBuilderPage.goto();
		await headlessBuilderPage.goToEditApplication(application.title);
		await applicationPage.goToSchemasTab();
		await applicationPage.addSchemaButton.click();
		await applicationPage.schemaObjectDefinitionSelector.click();

		const objectDefinitionDropdownOptions = (
			await applicationPage.page.getByRole('menu').allInnerTexts()
		)[0].split('\n');

		expect(objectDefinitionDropdownOptions).not.toContain(['Organization']);
		expect(
			objectDefinitionDropdownOptions.includes('ObjectDefinition')
		).toBeTruthy();
	}
);

testFeatureFlagsEnabled(
	'Can see allowed object definitions on schema creation with feature flag',
	async ({apiHelpers, applicationPage, headlessBuilderPage}) => {
		const objectDefinitionAPIClient =
			await apiHelpers.buildRestClient(ObjectDefinitionAPI);

		const {body: objectDefinition} =
			await objectDefinitionAPIClient.postObjectDefinition(
				objectDefinitionData
			);

		apiHelpers.data.push({
			id: objectDefinition.id,
			type: 'objectDefinition',
		});

		const application = await apiHelpers.objectEntry.postObjectEntry(
			applicationData1,
			'headless-builder/applications'
		);

		apiHelpers.data.push({id: application.id, type: 'apiApplication'});

		await headlessBuilderPage.goto();
		await headlessBuilderPage.goToEditApplication(application.title);
		await applicationPage.goToSchemasTab();
		await applicationPage.addSchemaButton.click();
		await applicationPage.schemaObjectDefinitionSelector.click();

		const objectDefinitionDropdownOptions = (
			await applicationPage.page.getByRole('menu').allInnerTexts()
		)[0].split('\n');

		expect(objectDefinitionDropdownOptions).not.toContain(['Organization']);

		for (const expectedObjectDefinition of [
			'AccountEntry',
			'ObjectDefinition',
			'User',
		]) {
			expect(
				objectDefinitionDropdownOptions.includes(
					expectedObjectDefinition
				)
			).toBeTruthy();
		}
	}
);

testFeatureFlagsDisabled(
	'Check related objects enablement without feature flag',
	async ({apiHelpers, applicationPage, headlessBuilderPage, schemaPage}) => {
		const objectDefinitionAPIClient =
			await apiHelpers.buildRestClient(ObjectDefinitionAPI);

		const {body: objectDefinition} =
			await objectDefinitionAPIClient.postObjectDefinition(
				objectDefinitionData
			);

		apiHelpers.data.push({
			id: objectDefinition.id,
			type: 'objectDefinition',
		});

		const {body: objectDefinition1} =
			await objectDefinitionAPIClient.postObjectDefinition(
				objectDefinition1Data
			);

		apiHelpers.data.push({
			id: objectDefinition1.id,
			type: 'objectDefinition',
		});

		const application = await apiHelpers.objectEntry.postObjectEntry(
			{
				apiApplicationToAPISchemas: [
					{
						description: 'objectDefinition1 Schema',
						externalReferenceCode: 'api-application-schema',
						mainObjectDefinitionERC:
							objectDefinition1.externalReferenceCode,
						name: 'ObjectDefinition1 Schema',
					},
				],
				applicationStatus: 'published',
				baseURL: 'basic-application',
				description: 'Test API Application',
				externalReferenceCode: 'basic-application',
				title: 'Basic application',
			},
			'headless-builder/applications'
		);

		apiHelpers.data.push({id: application.id, type: 'apiApplication'});

		const {body: customObjectDefinition} =
			await objectDefinitionAPIClient.getObjectDefinitionByExternalReferenceCode(
				'customObjectDefinition'
			);
		apiHelpers.data.push({
			id: customObjectDefinition.id,
			type: 'objectDefinition',
		});

		await headlessBuilderPage.goto();
		await headlessBuilderPage.goToEditApplication(application.title);
		await applicationPage.goToSchemasTab();
		await schemaPage.goTo('ObjectDefinition1 Schema');
		await schemaPage.goToPropertiesTab();

		// Assert that principal object properties are enabled

		waitForLoading;

		expect(
			await schemaPage.page.getByLabel('Add Author Property')
		).toBeEnabled();

		await schemaPage.page
			.getByRole('button', {name: 'View Related Objects'})
			.click();

		// Assert that unmodifiable system object properties are disabled

		waitForLoading;

		await schemaPage.page
			.getByRole('button', {name: 'Organization'})
			.click();

		const systemObjectPropertiesFF = await schemaPage.page
			.getByRole('button', {name: 'Organization'})
			.locator('..')
			.getByLabel('Test Unmodifiable System Object')
			.getByLabel('Add Author Property')
			.getByText('Author')
			.all();

		for (const property of systemObjectPropertiesFF) {
			await property.waitFor({state: 'attached'});
			await expect(property).toHaveClass(/disabled/);
		}

		// Assert that unmodifiable allowed system object properties are disabled without FF

		await schemaPage.page.getByRole('button', {name: 'Account'}).click();

		const systemObjectProperties = await schemaPage.page
			.getByRole('button', {name: 'Account'})
			.locator('..')
			.getByLabel('Test Unmodifiable Allowed System Object')
			.getByLabel('Add Author Property')
			.getByText('Author')
			.all();

		for (const element of systemObjectProperties) {
			await element.waitFor({state: 'attached'});
			await expect(element).toHaveClass(/disabled/);
		}

		// Assert that modifiable system object properties are enabled

		await schemaPage.page
			.getByRole('button', {name: 'API Application'})
			.click();

		const modifiableSystemProperties = await schemaPage.page
			.getByRole('button', {name: 'API Application'})
			.locator('..')
			.getByLabel('Test Modifiable System Object')
			.getByLabel('Add Author Property')
			.getByText('Author')
			.all();

		for (const property of modifiableSystemProperties) {
			await property.waitFor({state: 'attached'});
			await expect(property).not.toHaveClass(/disabled/);
		}

		// Assert that custom object properties are enabled

		await schemaPage.page
			.getByRole('button', {name: 'ObjectDefinition'})
			.click();

		const customObjectProperties = await schemaPage.page
			.getByRole('button', {name: 'ObjectDefinition'})
			.locator('..')
			.getByLabel('Test Custom Object')
			.getByLabel('Add Author Property')
			.getByText('Author')
			.all();

		for (const property of customObjectProperties) {
			await property.waitFor({state: 'attached'});
			await expect(property).not.toHaveClass(/disabled/);
		}
	}
);

testFeatureFlagsEnabled(
	'Check related objects enablement with feature flag',
	async ({apiHelpers, applicationPage, headlessBuilderPage, schemaPage}) => {
		const objectDefinitionAPIClient =
			await apiHelpers.buildRestClient(ObjectDefinitionAPI);

		const {body: objectDefinition} =
			await objectDefinitionAPIClient.postObjectDefinition(
				objectDefinitionData
			);

		apiHelpers.data.push({
			id: objectDefinition.id,
			type: 'objectDefinition',
		});

		const {body: objectDefinition1} =
			await objectDefinitionAPIClient.postObjectDefinition(
				objectDefinition1Data
			);

		apiHelpers.data.push({
			id: objectDefinition1.id,
			type: 'objectDefinition',
		});

		const application = await apiHelpers.objectEntry.postObjectEntry(
			{
				apiApplicationToAPISchemas: [
					{
						description: 'objectDefinition1 Schema',
						externalReferenceCode: 'api-application-schema',
						mainObjectDefinitionERC:
							objectDefinition1.externalReferenceCode,
						name: 'ObjectDefinition1 Schema',
					},
				],
				applicationStatus: 'published',
				baseURL: 'basic-application',
				description: 'Test API Application',
				externalReferenceCode: 'basic-application',
				title: 'Basic application',
			},
			'headless-builder/applications'
		);

		apiHelpers.data.push({id: application.id, type: 'apiApplication'});

		const {body: customObjectDefinition} =
			await objectDefinitionAPIClient.getObjectDefinitionByExternalReferenceCode(
				'customObjectDefinition'
			);
		apiHelpers.data.push({
			id: customObjectDefinition.id,
			type: 'objectDefinition',
		});

		await headlessBuilderPage.goto();
		await headlessBuilderPage.goToEditApplication(application.title);
		await applicationPage.goToSchemasTab();
		await schemaPage.goTo('ObjectDefinition1 Schema');
		await schemaPage.goToPropertiesTab();

		// Assert that principal object properties are enabled

		waitForLoading;

		await expect(
			await schemaPage.page.getByLabel('Add Author Property')
		).toBeEnabled();

		await schemaPage.page
			.getByRole('button', {name: 'View Related Objects'})
			.click();

		// Assert that unmodifiable system object properties are disabled

		waitForLoading;

		await schemaPage.page
			.getByRole('button', {name: 'Organization'})
			.waitFor({state: 'visible'});

		await schemaPage.page
			.getByRole('button', {name: 'Organization'})
			.click();

		const unmodifiableSystemProperties = await schemaPage.page
			.getByRole('button', {name: 'Organization'})
			.locator('..')
			.getByLabel('Test Unmodifiable System Object')
			.getByLabel('Add Author Property')
			.getByText('Author')
			.all();

		for (const property of unmodifiableSystemProperties) {
			await property.waitFor({state: 'attached'});
			await expect(property).toHaveClass(/disabled/);
		}

		// Assert that unmodifiable allowed system object properties are enabled with FF

		await schemaPage.page.getByRole('button', {name: 'Account'}).click();

		const unmodifiableAllowedSystemProperties = await schemaPage.page
			.getByRole('button', {name: 'Account'})
			.locator('..')
			.getByLabel('Test Unmodifiable Allowed System Object')
			.getByLabel('Add Author Property')
			.getByText('Author')
			.all();

		for (const property of unmodifiableAllowedSystemProperties) {
			await property.waitFor({state: 'attached'});
			await expect(property).not.toHaveClass(/disabled/);
		}

		// Assert that modifiable system object properties are enabled

		await schemaPage.page
			.getByRole('button', {name: 'API Application'})
			.click();

		const modifiableSystemProperties = await schemaPage.page
			.getByRole('button', {name: 'API Application'})
			.locator('..')
			.getByLabel('Test Modifiable System Object')
			.getByLabel('Add Author Property')
			.getByText('Author')
			.all();

		for (const property of modifiableSystemProperties) {
			await property.waitFor({state: 'attached'});
			await expect(property).not.toHaveClass(/disabled/);
		}

		// Assert that custom object properties are enabled

		await schemaPage.page
			.getByRole('button', {name: 'ObjectDefinition'})
			.click();

		const customObjectProperties = await schemaPage.page
			.getByRole('button', {name: 'ObjectDefinition'})
			.locator('..')
			.getByLabel('Test Custom Object')
			.getByLabel('Add Author Property')
			.getByText('Author')
			.all();

		for (const property of customObjectProperties) {
			await property.waitFor({state: 'attached'});
			await expect(property).not.toHaveClass(/disabled/);
		}
	}
);

testFeatureFlagsEnabled(
	'Cannot edit object field of a related schema in unpublished application',
	{tag: ['@LPD-67357']},
	async ({
		apiHelpers,
		applicationPage,
		headlessBuilderPage,
		page,
		schemaPage,
	}) => {
		const application = await apiHelpers.objectEntry.postObjectEntry(
			{...applicationData1, applicationStatus: 'unpublished'},
			'headless-builder/applications'
		);

		apiHelpers.data.push({id: application.id, type: 'apiApplication'});

		await headlessBuilderPage.openApplicationAndEdit(application.title);
		await applicationPage.goToSchemasTab();
		await schemaPage.goTo('API Application Schema');

		await expect(
			page.getByLabel('Object Definition APIApplication is Selected')
		).toBeDisabled();
	}
);

testFeatureFlagsEnabled(
	'Can edit schema name and description in an unpublished application',
	{tag: ['@LPD-67357']},
	async ({
		apiHelpers,
		applicationPage,
		headlessBuilderPage,
		schemaPage,
		uiElementsPage,
	}) => {
		const application = await apiHelpers.objectEntry.postObjectEntry(
			{...applicationData1, applicationStatus: 'unpublished'},
			'headless-builder/applications'
		);

		apiHelpers.data.push({id: application.id, type: 'apiApplication'});

		await headlessBuilderPage.openApplicationAndEdit(application.title);
		await applicationPage.goToSchemasTab();
		await schemaPage.goTo(
			applicationData1.apiApplicationToAPISchemas[0].name
		);

		const newDescription = 'Description edited';
		const newName = 'API Application Schema edited';

		await schemaPage.fillDescription(newDescription);
		await schemaPage.fillName(newName);
		await uiElementsPage.saveButton.click();

		await expect(schemaPage.successSaveMessage).toBeVisible();
		await expect(schemaPage.descriptionInput).toHaveValue(newDescription);
		await expect(schemaPage.nameInput).toHaveValue(newName);
	}
);

testFeatureFlagsEnabled(
	'Can relate the same schema with multiple endpoints',
	{tag: ['@LPD-67357']},
	async ({
		apiHelpers,
		applicationPage,
		headlessBuilderPage,
		uiElementsPage,
	}) => {
		const application = await apiHelpers.objectEntry.postObjectEntry(
			{
				...applicationData2,
				apiApplicationToAPIEndpoints: [
					{
						description: 'Test API Endpoint',
						externalReferenceCode: 'basic-endpoint',
						httpMethod: 'get',
						name: 'Basic API Endpoint',
						path: '/endpoint/',
						r_responseAPISchemaToAPIEndpoints_l_apiSchemaERC:
							'api-application-schema',
						retrieveType: 'collection',
						scope: 'company',
					},
				],
			},
			'headless-builder/applications'
		);

		apiHelpers.data.push({id: application.id, type: 'apiApplication'});

		await apiHelpers.objectEntry.postObjectEntry(
			{
				description: 'Test API Endpoint',
				externalReferenceCode: 'second-endpoint',
				httpMethod: 'get',
				name: 'Second API Endpoint',
				path: '/second-endpoint/',
				r_apiApplicationToAPIEndpoints_l_apiApplicationId:
					application.id,
				r_responseAPISchemaToAPIEndpoints_l_apiSchemaERC:
					'api-application-second-schema',
				retrieveType: 'collection',
				scope: 'company',
			},
			'headless-builder/endpoints'
		);

		await headlessBuilderPage.openApplicationAndEdit(application.title);
		await applicationPage.goToEndpointsTab();
		await applicationPage.goToEditEndpoint('/endpoint/');
		await applicationPage.goToEndpointConfigurationTab();
		await applicationPage.configureSchemaOfEndpoint(
			'API Application Second Schema'
		);
		await uiElementsPage.saveButton.click();

		const response =
			await apiHelpers.objectEntry.getObjectDefinitionObjectEntries(
				'headless-builder/endpoints'
			);

		const schemaERCs = response.items.map(
			(endpoint: any) =>
				endpoint?.r_responseAPISchemaToAPIEndpoints_l_apiSchemaERC
		);

		expect(schemaERCs.sort().join(',')).toEqual(
			'api-application-second-schema,api-application-second-schema'
		);
	}
);
