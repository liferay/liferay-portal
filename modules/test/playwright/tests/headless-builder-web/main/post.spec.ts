/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {ObjectDefinitionAPI} from '@liferay/object-admin-rest-client-js/v1_0';
import {expect, mergeTests} from '@playwright/test';

import {dataApiHelpersTest} from '../../../fixtures/dataApiHelpersTest';
import {headlessDiscoveryPagesTest} from '../../../fixtures/headlessDiscoveryWebPagesTest';
import {loginTest} from '../../../fixtures/loginTest';
import {headlessBuilderPagesTest} from './fixtures/headlessBuilderPagesTest';

export const test = mergeTests(
	dataApiHelpersTest,
	loginTest(),
	headlessBuilderPagesTest({}),
	headlessDiscoveryPagesTest
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

const studentSubjectsApplicationData = {
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
			description: 'The student schema',
			externalReferenceCode: 'Student-schema',
			mainObjectDefinitionERC: 'student-definition',
			name: 'Student schema',
		},
		{
			apiSchemaToAPIProperties: [
				{
					description: 'Name of the subject',
					externalReferenceCode: 'subject-name-property',
					name: 'subjectName',
					objectFieldERC: 'subject-name-field',
				},
			],
			description: 'The subject schema',
			externalReferenceCode: 'Subject-schema',
			mainObjectDefinitionERC: 'subject-definition',
			name: 'Subject schema',
		},
	],
	applicationStatus: 'published',
	baseURL: 'student-subjects',
	description: 'Retrive the data from the different students/subjects.',
	externalReferenceCode: 'student-subjects-application',
	title: 'Student-Subject manager',
};

test('can create post endpoint and can not disassociate request api schema', async ({
	apiHelpers,
	applicationPage,
	headlessBuilderPage,
	page,
}) => {
	const application = await apiHelpers.objectEntry.postObjectEntry(
		applicationData,
		'headless-builder/applications'
	);

	apiHelpers.data.push({id: application.id, type: 'apiApplication'});

	await headlessBuilderPage.goto();
	await headlessBuilderPage.goToEditApplication(applicationData.title);

	await applicationPage.createEndpoint('POST', 'Company', 'student');

	await applicationPage.goToEndpointConfigurationTab();

	await page.getByLabel('Response Body Schema').click();
	await expect(
		page.getByRole('menuitem', {name: 'Not Selected'})
	).toBeVisible();

	await page.getByRole('menuitem', {name: 'Not Selected'}).click();

	await applicationPage.publishButton.click();

	await expect(
		page.getByText('Please select a request body schema.')
	).toBeVisible();
});

test('can create post endpoint and can not edit http method', async ({
	apiHelpers,
	applicationPage,
	headlessBuilderPage,
}) => {
	const application = await apiHelpers.objectEntry.postObjectEntry(
		applicationData,
		'headless-builder/applications'
	);

	apiHelpers.data.push({id: application.id, type: 'apiApplication'});

	await headlessBuilderPage.goto();
	await headlessBuilderPage.goToEditApplication(applicationData.title);

	await applicationPage.createEndpoint('POST', 'Company', 'student');

	await applicationPage.goToEndpointInfoTab();

	const isDisabled = await applicationPage.httpMethodButton.evaluate(
		(element: HTMLButtonElement) => element.disabled
	);

	await expect(isDisabled).toBeTruthy();
});

test('can create post endpoint with different request and response schema', async ({
	apiExplorerPage,
	apiHelpers,
	applicationPage,
	headlessBuilderPage,
	page,
}) => {
	const objectDefinitionAPIClient =
		await apiHelpers.buildRestClient(ObjectDefinitionAPI);

	const {body: subjectResponse} =
		await objectDefinitionAPIClient.postObjectDefinition({
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
		});

	apiHelpers.data.push({id: subjectResponse.id, type: 'objectDefinition'});

	const {body: studentResponse} =
		await objectDefinitionAPIClient.postObjectDefinition({
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
					externalReferenceCode: 'student-subjects-relationship',
					label: {
						en_US: 'Student subjects',
					},
					name: 'studentSubjects',
					objectDefinitionExternalReferenceCode1:
						'student-definition',
					objectDefinitionExternalReferenceCode2:
						'subject-definition',
					objectDefinitionModifiable2: true,
					objectDefinitionName2: 'Subject',
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
						name: 'r_studentSubjects_c_studentId',
						objectFieldSettings: [
							{
								name: 'objectDefinition1ShortName',
								value: 'Student',
							} as any,
							{
								name: 'objectRelationshipERCObjectFieldName',
								value: 'r_studentSubjects_c_studentERC',
							} as any,
						],
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
		});

	apiHelpers.data.push({id: studentResponse.id, type: 'objectDefinition'});

	apiHelpers.data.push({
		id: studentResponse.objectRelationships[0].id,
		type: 'objectRelationship',
	});

	const application = await apiHelpers.objectEntry.postObjectEntry(
		studentSubjectsApplicationData,
		'headless-builder/applications'
	);

	apiHelpers.data.push({id: application.id, type: 'apiApplication'});

	await headlessBuilderPage.goto();
	await headlessBuilderPage.goToEditApplication(
		studentSubjectsApplicationData.title
	);

	await applicationPage.createEndpoint('POST', 'Company', 'student');

	await applicationPage.goToEndpointConfigurationTab();
	await applicationPage.selectEndpointRequestSchema(
		studentSubjectsApplicationData.apiApplicationToAPISchemas[0].name
	);

	// TODO Change to:
	// await apiApplicationPage.selectEndpointResponseSchema(...)
	// when LPD-16654 is fixed

	await page.getByRole('button', {name: 'Select a Schema'}).click();
	await page
		.getByRole('menuitem', {
			name: studentSubjectsApplicationData.apiApplicationToAPISchemas[1]
				.name,
		})
		.click();

	await applicationPage.publishButton.click();

	await expect(page.getByText('API application was published')).toBeVisible();
	await expect(
		page.getByText('API endpoint changes were saved')
	).toBeVisible();

	await apiExplorerPage.goToApplication(
		`c/${studentSubjectsApplicationData.baseURL}`
	);

	await expect(apiExplorerPage.getEndpointLocator('/student')).toBeVisible();
});

test('can create post method endpoint with company scope', async ({
	apiExplorerPage,
	apiHelpers,
	applicationPage,
	headlessBuilderPage,
	page,
}) => {
	const application = await apiHelpers.objectEntry.postObjectEntry(
		applicationData,
		'headless-builder/applications'
	);

	apiHelpers.data.push({id: application.id, type: 'apiApplication'});

	await headlessBuilderPage.goto();
	await headlessBuilderPage.goToEditApplication(applicationData.title);

	await applicationPage.createEndpoint(
		'POST',
		'Company',
		'test-post-endpoint'
	);

	await applicationPage.goToEndpointConfigurationTab();
	await applicationPage.selectEndpointRequestSchema(
		applicationData.apiApplicationToAPISchemas[0].name
	);
	await applicationPage.publishButton.click();

	await expect(page.getByText('API application was published')).toBeVisible();
	await expect(
		page.getByText('API endpoint changes were saved')
	).toBeVisible();

	await apiExplorerPage.goToApplication(`c/${applicationData.baseURL}`);

	await expect(
		apiExplorerPage.getEndpointLocator('/test-post-endpoint')
	).toBeVisible();
});
