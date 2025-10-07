/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {
	ObjectDefinitionAPI,
	ObjectRelationshipAPI,
	ObjectViewAPI,
} from '@liferay/object-admin-rest-client-js';
import {expect, mergeTests} from '@playwright/test';

import {dataApiHelpersTest} from '../../../fixtures/dataApiHelpersTest';
import {loginTest} from '../../../fixtures/loginTest';
import {objectPagesTest} from '../../../fixtures/objectPagesTest';
import {getRandomInt} from '../../../utils/getRandomInt';
import getRandomString from '../../../utils/getRandomString';
import {generateObjectEntryValues} from './utils/generateObjectEntry';
import {generateObjectFields} from './utils/generateObjectFields';

export const test = mergeTests(
	dataApiHelpersTest,
	loginTest(),
	objectPagesTest
);

test('can add and remove new object fields from object view while maintaining correct logic order', async ({
	apiHelpers,
	editObjectViewPage,
	objectViewPage,
	page,
}) => {
	const listTypeDefinition =
		await apiHelpers.listTypeAdmin.postRandomListTypeDefinition();

	apiHelpers.data.push({
		id: listTypeDefinition.id,
		type: 'listTypeDefinition',
	});

	const objectDefinition =
		await apiHelpers.objectAdmin.postRandomObjectDefinition({
			objectFields: [
				{
					DBType: 'String',
					businessType: 'MultiselectPicklist',
					externalReferenceCode: 'customPicklist',
					indexed: true,
					indexedAsKeyword: false,
					indexedLanguageId: 'en_US',
					label: {
						en_US: 'customPicklist',
					},
					listTypeDefinitionExternalReferenceCode:
						listTypeDefinition.externalReferenceCode,
					name: 'customPicklist',
					required: false,
					state: false,
				},
			],
			status: {code: 0},
		});

	apiHelpers.data.push({id: objectDefinition.id, type: 'objectDefinition'});

	const objectViewName = getRandomString();

	await objectViewPage.goto(objectDefinition.label['en_US']);

	await objectViewPage.createObjectView(objectViewName);

	await page.getByRole('link', {name: objectViewName}).click();

	await editObjectViewPage.selectObjectFields(['Status', 'Create Date']);

	const objectFields = editObjectViewPage.sidePanel.locator(
		'li[draggable="true"]'
	);

	await expect(objectFields).toHaveCount(2);

	await expect(objectFields.nth(0)).toContainText('Status');

	await expect(objectFields.nth(1)).toContainText('Create Date');

	await editObjectViewPage.selectObjectFields(['ID', 'customPicklist']);

	await editObjectViewPage.unselectObjectFields(['Status', 'ID']);

	await editObjectViewPage.selectObjectFields(['External Reference Code']);

	await expect(objectFields).toHaveCount(3);

	await expect(objectFields.nth(0)).toContainText('Create Date');

	await expect(objectFields.nth(1)).toContainText('customPicklist');

	await expect(objectFields.nth(2)).toContainText('External Reference Code');
});

test('can create an object custom view using object relationship entry', async ({
	apiHelpers,
	editObjectViewPage,
	objectViewPage,
	page,
}) => {
	const objectDefinition1 =
		await apiHelpers.objectAdmin.postRandomObjectDefinition({
			status: {code: 0},
		});

	const objectDefinition2 =
		await apiHelpers.objectAdmin.postRandomObjectDefinition({
			status: {code: 0},
		});
	apiHelpers.data.push({id: objectDefinition1.id, type: 'objectDefinition'});

	apiHelpers.data.push({id: objectDefinition2.id, type: 'objectDefinition'});

	const objectRelationshipLabel = 'objectRelationshipLabel' + getRandomInt();
	const objectRelationshipName =
		'objectRelationshipName' + Math.floor(Math.random() * 99);

	const objectRelationshipAPIClient = await apiHelpers.buildRestClient(
		ObjectRelationshipAPI
	);

	await objectRelationshipAPIClient.postObjectDefinitionByExternalReferenceCodeObjectRelationship(
		objectDefinition1.externalReferenceCode,
		{
			label: {
				en_US: objectRelationshipLabel,
			},
			name: objectRelationshipName,
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

	const applicationName = 'c/' + objectDefinition1.name.toLowerCase() + 's';

	const textObjectEntry = {
		textField: 'entry',
	};

	const objectEntryResponse = await apiHelpers.objectEntry.postObjectEntry(
		textObjectEntry,
		applicationName
	);

	const objectViewName = getRandomString();

	await objectViewPage.goto(objectDefinition2.label['en_US']);

	await objectViewPage.createObjectView(objectViewName);

	await page.getByRole('link', {name: objectViewName}).click();

	editObjectViewPage.createFilter(
		objectRelationshipLabel,
		'Includes',
		`${objectEntryResponse.id}`
	);

	await expect(
		editObjectViewPage.sidePanel.getByText(`${objectRelationshipLabel}`)
	).toBeVisible();

	await expect(
		editObjectViewPage.sidePanel.getByText('Relationship', {exact: true})
	).toBeVisible();

	await expect(
		editObjectViewPage.sidePanel.getByText(`${objectEntryResponse.id}`)
	).toBeVisible();
});

test('cannot create an object custom view using empty multiselectpicklist entry', async ({
	apiHelpers,
	editObjectViewPage,
	objectViewPage,
	page,
}) => {
	const listTypeDefinition =
		await apiHelpers.listTypeAdmin.postRandomListTypeDefinition();

	apiHelpers.data.push({
		id: listTypeDefinition.id,
		type: 'listTypeDefinition',
	});

	const objectDefinition =
		await apiHelpers.objectAdmin.postRandomObjectDefinition({
			objectFields: [
				{
					DBType: 'String',
					businessType: 'MultiselectPicklist',
					externalReferenceCode: 'customPicklist',
					indexed: true,
					indexedAsKeyword: false,
					indexedLanguageId: 'en_US',
					label: {
						en_US: 'customPicklist',
					},
					listTypeDefinitionExternalReferenceCode:
						listTypeDefinition.externalReferenceCode,
					name: 'customPicklist',
					required: false,
					state: false,
				},
			],
			status: {code: 0},
		});

	apiHelpers.data.push({id: objectDefinition.id, type: 'objectDefinition'});

	const objectViewName = getRandomString();

	await objectViewPage.goto(objectDefinition.label['en_US']);

	await objectViewPage.createObjectView(objectViewName);

	await page.getByRole('link', {name: objectViewName}).click();

	await editObjectViewPage.createFilter('customPicklist', 'Includes');

	await expect(
		page.frameLocator('iframe').getByText('Required')
	).toBeVisible();
});

test('assert that the user is able to use the ERC field in Sort, on the Custom Views tab', async ({
	apiHelpers,
	page,
	viewObjectEntriesPage,
}) => {
	const objectDefinitionLabel = 'ObjectDefinitionLabel' + getRandomInt();
	const objectDefinitionName = 'ObjectDefinitionName' + getRandomInt();

	const objectFields = generateObjectFields({
		objectFieldBusinessTypes: ['Text'],
	});

	const objectDefinitionAPIClient =
		await apiHelpers.buildRestClient(ObjectDefinitionAPI);

	const {body: objectDefinition} =
		await objectDefinitionAPIClient.postObjectDefinition({
			active: true,
			enableLocalization: true,
			label: {
				en_US: objectDefinitionLabel,
			},
			name: objectDefinitionName,
			objectFields,
			pluralLabel: {
				en_US: objectDefinitionLabel,
			},
			portlet: true,
			scope: 'company',
			status: {
				code: 0,
			},
			titleObjectFieldName: objectFields[0].name,
		});

	apiHelpers.data.push({
		id: objectDefinition.id,
		type: 'objectDefinition',
	});

	const objectViewAPIClient = await apiHelpers.buildRestClient(ObjectViewAPI);

	await objectViewAPIClient.postObjectDefinitionObjectView(
		objectDefinition.id,
		{
			defaultObjectView: true,
			name: {en_US: getRandomString()},
			objectViewColumns: [
				{
					objectFieldName: objectFields[0].name,
					priority: 0,
				},
				{
					objectFieldName: 'externalReferenceCode',
					priority: 1,
				},
			],
			objectViewSortColumns: [
				{
					objectFieldName: 'externalReferenceCode',
					priority: 0,
					sortOrder: 'asc',
				},
			],
		}
	);

	const {objectEntry} = await generateObjectEntryValues({
		objectEntryFormat: 'API',
		objectFields,
	});

	const applicationName = 'c/' + objectDefinition.name.toLowerCase() + 's';
	const entry1 = 'Entry A';
	const entry2 = 'Entry B';

	await apiHelpers.objectEntry.postObjectEntry(
		{...objectEntry, externalReferenceCode: entry1},
		applicationName
	);

	await apiHelpers.objectEntry.postObjectEntry(
		{...objectEntry, externalReferenceCode: entry2},
		applicationName
	);

	await viewObjectEntriesPage.goto(objectDefinition.className);

	await expect(page.getByRole('cell').nth(2)).toHaveText(entry1);

	await page.getByTitle('Sortable Column').dblclick();

	await expect(page.getByRole('cell').nth(2)).toHaveText(entry2);
});
