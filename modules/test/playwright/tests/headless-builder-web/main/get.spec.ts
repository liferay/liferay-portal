/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {
	ObjectDefinition,
	ObjectDefinitionAPI,
} from '@liferay/object-admin-rest-client-js/v1_0';
import {expect, mergeTests} from '@playwright/test';

import {apiHelpersTest} from '../../../fixtures/apiHelpersTest';
import {dataApiHelpersTest} from '../../../fixtures/dataApiHelpersTest';
import {headlessDiscoveryPagesTest} from '../../../fixtures/headlessDiscoveryWebPagesTest';
import {loginTest} from '../../../fixtures/loginTest';
import {uiElementsPageTest} from '../../../fixtures/uiElementsTest';
import getRandomString from '../../../utils/getRandomString';
import {headlessBuilderPagesTest} from './fixtures/headlessBuilderPagesTest';

export const test = mergeTests(
	apiHelpersTest,
	dataApiHelpersTest,
	loginTest(),
	headlessBuilderPagesTest({}),
	headlessDiscoveryPagesTest,
	uiElementsPageTest
);

const applicationData = {
	apiApplicationToAPISchemas: [
		{
			description: 'API Application Schema',
			externalReferenceCode: 'api-application-schema',
			mainObjectDefinitionERC: 'L_API_APPLICATION',
			name: 'API Application Schema',
		},
	],
	applicationStatus: 'unpublished',
	baseURL: 'basic-application',
	description: 'Test API Application',
	externalReferenceCode: 'basic-application',
	title: 'Basic application',
};

test('can associate and disassociate schema', async ({
	apiHelpers,
	applicationPage,
	headlessBuilderPage,
	page,
}) => {
	const apiApplication = await apiHelpers.objectEntry.postObjectEntry(
		{
			...applicationData,
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

	apiHelpers.data.push({id: apiApplication.id, type: 'apiApplication'});

	await headlessBuilderPage.goto();
	await headlessBuilderPage.goToEditApplication(applicationData.title);
	await applicationPage.goToEndpointsTab();
	await applicationPage.goToEditEndpoint('/endpoint/');
	await applicationPage.goToEndpointConfigurationTab();

	await page.getByLabel('Response Body Schema').click();
	await expect(
		page.getByRole('menuitem', {name: 'Not Selected'})
	).toBeVisible();

	await page.getByRole('menuitem', {name: 'Not Selected'}).click();

	await applicationPage.publishButton.click();

	await page.waitForTimeout(1500);

	await applicationPage.goToDetailsTab();
	await applicationPage.goToEndpointsTab();
	await applicationPage.goToEditEndpoint('/endpoint/');
	await applicationPage.goToEndpointConfigurationTab();

	const responseBodySchemaContent = await page
		.getByLabel('Response Body Schema')
		.textContent();

	await expect(responseBodySchemaContent).toEqual('Select a Schema');
});

test('can see available path parameter properties of a singleElement endpoint', async ({
	apiHelpers,
	applicationPage,
	headlessBuilderPage,
	page,
}) => {
	const objectDefinition =
		(await apiHelpers.objectAdmin.postRandomObjectDefinition({
			objectFolderExternalReferenceCode: 'default',
			status: {code: 0},
		})) as ObjectDefinition;

	apiHelpers.data.push({id: objectDefinition.id, type: 'objectDefinition'});

	await headlessBuilderPage.goto();

	const apiApplicationTitle = getRandomString();

	await headlessBuilderPage.createApplication(apiApplicationTitle);

	const {
		items: [apiApplication],
	} = await apiHelpers.objectEntry.getObjectDefinitionObjectEntries(
		'headless-builder/applications',
		new URLSearchParams({filter: `title eq '${apiApplicationTitle}'`})
	);

	apiHelpers.data.push({id: apiApplication.id, type: 'apiApplication'});

	await applicationPage.goToSchemasTab();
	await applicationPage.addSchemaButton.click();
	await applicationPage.schemaNameTextBox.fill('API Application schema');
	await applicationPage.setSchemaMainObjectDefinition('objectDefinition');
	await applicationPage.createButton.click();

	await applicationPage.createSingleElementEndpoint(
		'Company',
		'gettest',
		'entryid'
	);
	await applicationPage.goToEndpointConfigurationTab();
	await applicationPage.selectEndpointResponseSchema(
		'API Application schema'
	);

	await page.getByRole('button', {name: 'Select an Option'}).click();
	await expect(
		page.getByRole('menuitem', {name: 'External Reference Code'})
	).toBeVisible();
	await expect(page.getByRole('menuitem', {name: 'ID'})).toBeVisible();
});

test('can see path parameter property with map details', async ({
	apiHelpers,
	applicationPage,
	headlessBuilderPage,
	page,
}) => {
	const objectDefinition =
		(await apiHelpers.objectAdmin.postRandomObjectDefinition({
			objectFolderExternalReferenceCode: 'default',
			status: {code: 0},
		})) as ObjectDefinition;

	apiHelpers.data.push({id: objectDefinition.id, type: 'objectDefinition'});

	await headlessBuilderPage.goto();

	const apiApplicationTitle = getRandomString();

	await headlessBuilderPage.createApplication(apiApplicationTitle);

	const {
		items: [apiApplication],
	} = await apiHelpers.objectEntry.getObjectDefinitionObjectEntries(
		'headless-builder/applications',
		new URLSearchParams({filter: `title eq '${apiApplicationTitle}'`})
	);

	apiHelpers.data.push({id: apiApplication.id, type: 'apiApplication'});

	await applicationPage.goToSchemasTab();
	await applicationPage.addSchemaButton.click();
	await applicationPage.schemaNameTextBox.fill('API Application schema');
	await applicationPage.setSchemaMainObjectDefinition('objectDefinition');
	await applicationPage.createButton.click();

	await applicationPage.createSingleElementEndpoint(
		'Company',
		'gettest',
		'entryid'
	);
	await applicationPage.goToEndpointConfigurationTab();
	await applicationPage.selectEndpointResponseSchema(
		'API Application schema'
	);

	await expect(
		page.getByRole('button', {name: 'Select an Option'})
	).toBeVisible();
	await expect(
		page.getByPlaceholder('Add a description here.')
	).toBeVisible();
	await expect(
		page.getByText(
			'This property from the schema will be mapped to path Parameter: {entryid}.'
		)
	).toBeVisible();
});

test('can see schema unique fields as path parameter properties', async ({
	apiHelpers,
	applicationPage,
	headlessBuilderPage,
	page,
}) => {
	const apiApplication = await apiHelpers.objectEntry.postObjectEntry(
		{
			...applicationData,
			apiApplicationToAPIEndpoints: [
				{
					description: 'Test API Endpoint',
					externalReferenceCode: 'basic-endpoint',
					httpMethod: 'get',
					name: 'Basic API Endpoint',
					path: '/endpoint/{pathParam}',
					r_responseAPISchemaToAPIEndpoints_l_apiSchemaERC:
						'api-application-schema',
					retrieveType: 'singleElement',
					scope: 'company',
				},
			],
		},
		'headless-builder/applications'
	);

	apiHelpers.data.push({id: apiApplication.id, type: 'apiApplication'});

	await headlessBuilderPage.goto();
	await headlessBuilderPage.goToEditApplication(applicationData.title);
	await applicationPage.goToEndpointsTab();
	await applicationPage.goToEditEndpoint('/endpoint/{pathParam}');
	await applicationPage.goToEndpointConfigurationTab();

	await page.getByRole('button', {name: 'Select an Option'}).click();
	await expect(page.getByRole('menuitem', {name: 'Base URL'})).toBeVisible();
	await expect(
		page.getByRole('menuitem', {name: 'External Reference Code'})
	).toBeVisible();
	await expect(page.getByRole('menuitem', {name: 'ID'})).toBeVisible();
	await expect(page.getByRole('menuitem', {name: 'Title'})).toBeVisible();
});

test('can list site scoped endpoint', async ({
	apiHelpers,
	applicationPage,
	headlessBuilderPage,
	page,
}) => {
	const objectDefinitionAPIClient =
		await apiHelpers.buildRestClient(ObjectDefinitionAPI);

	const {body: studentSiteDefinition} =
		await objectDefinitionAPIClient.postObjectDefinition({
			active: true,
			externalReferenceCode: 'site-student-definition',
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
			pluralLabel: {
				en_US: 'Students',
			},
			portlet: true,
			restContextPath: '/o/c/students',
			scope: 'site',
			status: {
				code: 0,
			},
		});

	const studentApplication = await apiHelpers.objectEntry.postObjectEntry(
		{
			apiApplicationToAPISchemas: [
				{
					apiSchemaToAPIProperties: [
						{
							description: 'Name of the student',
							externalReferenceCode: 'student-name-property',
							name: 'studentName',
							objectFieldERC: 'student-name-field',
						},
					],
					description: 'Test site-schema',
					externalReferenceCode: 'testSchema',
					mainObjectDefinitionERC: 'site-student-definition',
					name: 'testSchema',
				},
			],
			applicationStatus: 'unpublished',
			baseURL: 'my-app',
			description: 'Retrive the data from the different students.',
			externalReferenceCode: 'my-application',
			title: 'My-app',
		},
		'headless-builder/applications'
	);

	apiHelpers.data.push({
		id: studentSiteDefinition.id,
		type: 'objectDefinition',
	});

	apiHelpers.data.push({id: studentApplication.id, type: 'apiApplication'});

	await headlessBuilderPage.goto();
	await headlessBuilderPage.goToEditApplication(studentApplication.title);
	await applicationPage.createSingleElementEndpoint(
		'Site',
		'gettest',
		'entryerc'
	);

	await applicationPage.goToEndpointConfigurationTab();
	await page.getByLabel('Response Body Schema').click();
	await page
		.getByRole('menuitem', {
			name: studentApplication.apiApplicationToAPISchemas[0].name,
		})
		.click();
	await page.getByRole('button', {name: 'Select an Option'}).click();
	await page.getByRole('menuitem', {name: 'External Reference Code'}).click();
	await applicationPage.publishButton.click();

	await headlessBuilderPage.goto();
	await headlessBuilderPage.goToEditApplication(studentApplication.title);
	await applicationPage.goToEndpointsTab();
	await applicationPage.goToEditEndpoint('/gettest/{entryerc}/');
});

test(
	'can create an endpoint with a valid path',
	{tag: '@LPD-106934'},
	async ({
		apiHelpers,
		applicationPage,
		headlessBuilderPage,
		page,
		uiElementsPage,
	}) => {
		const application = await apiHelpers.objectEntry.postObjectEntry(
			{
				applicationStatus: 'unpublished',
				baseURL: `test-${getRandomString()}`,
				title: getRandomString(),
			},
			'headless-builder/applications'
		);

		apiHelpers.data.push({id: application.id, type: 'apiApplication'});

		await headlessBuilderPage.goto();
		await headlessBuilderPage.goToEditApplication(application.title);
		await applicationPage.createCollectionEndpoint(
			'Company',
			'/testendpoint'
		);

		await expect(uiElementsPage.anySuccessAlert).toBeVisible();

		await headlessBuilderPage.goto();
		await headlessBuilderPage.goToEditApplication(application.title);
		await applicationPage.goToEndpointsTab();

		await expect(
			page.locator('.fds tbody tr', {hasText: '/testendpoint/'})
		).toBeVisible();
	}
);

test(
	'can save a filter and a sort in the endpoint configuration',
	{tag: '@LPD-109889'},
	async ({apiHelpers, applicationPage, headlessBuilderPage, page}) => {
		const endpointExternalReferenceCode = getRandomString();

		const apiApplication = await apiHelpers.objectEntry.postObjectEntry(
			{
				...applicationData,
				apiApplicationToAPIEndpoints: [
					{
						externalReferenceCode: endpointExternalReferenceCode,
						httpMethod: 'get',
						name: getRandomString(),
						path: '/endpoint/',
						r_responseAPISchemaToAPIEndpoints_l_apiSchemaERC:
							'api-application-schema',
						retrieveType: 'collection',
						scope: 'company',
					},
				],
				baseURL: `test-${getRandomString()}`,
				externalReferenceCode: getRandomString(),
				title: getRandomString(),
			},
			'headless-builder/applications'
		);

		apiHelpers.data.push({id: apiApplication.id, type: 'apiApplication'});

		await headlessBuilderPage.openApplicationAndEdit(apiApplication.title);
		await applicationPage.goToEndpointsTab();
		await applicationPage.goToEditEndpoint('/endpoint/');
		await applicationPage.goToEndpointConfigurationTab();

		await page
			.getByLabel('Add a filter using OData.')
			.fill("title eq 'abc'");
		await page.getByLabel('Add a sort using OData.').fill('title:desc');

		await applicationPage.publishButton.click();

		await expect(async () => {
			expect(
				await apiHelpers.objectEntry.getObjectEntryByExternalReferenceCode(
					{
						applicationName: 'headless-builder/endpoints',
						externalReferenceCode: endpointExternalReferenceCode,
						nestedField:
							'apiEndpointToAPIFilters,apiEndpointToAPISorts',
					}
				)
			).toMatchObject({
				apiEndpointToAPIFilters: [{oDataFilter: "title eq 'abc'"}],
				apiEndpointToAPISorts: [{oDataSort: 'title:desc'}],
			});
		}).toPass({timeout: 15000});
	}
);
