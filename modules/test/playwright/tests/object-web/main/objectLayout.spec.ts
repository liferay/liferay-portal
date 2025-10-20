/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {
	ObjectDefinitionAPI,
	ObjectField,
	ObjectRelationshipAPI,
} from '@liferay/object-admin-rest-client-js';
import {expect, mergeTests} from '@playwright/test';

import {apiHelpersTest} from '../../../fixtures/apiHelpersTest';
import {dataApiHelpersTest} from '../../../fixtures/dataApiHelpersTest';
import {editObjectDefinitionPagesTest} from '../../../fixtures/editObjectDefinitionPagesTest';
import {loginTest} from '../../../fixtures/loginTest';
import {objectPagesTest} from '../../../fixtures/objectPagesTest';
import {getRandomInt} from '../../../utils/getRandomInt';
import getRandomString from '../../../utils/getRandomString';
import {waitForAlert} from '../../../utils/waitForAlert';
import {generateObjectEntryValues} from './utils/generateObjectEntry';
import {generateObjectFields} from './utils/generateObjectFields';
import getRandomObjectFieldText from './utils/getRandomObjectFieldText';

export const test = mergeTests(
	apiHelpersTest,
	dataApiHelpersTest,
	editObjectDefinitionPagesTest,
	loginTest(),
	objectPagesTest
);

test.describe('manage Object Layouts through the Object Layout tab', () => {
	test('can view all fields of an object when creating its layout', async ({
		apiHelpers,
		objectLayoutsPage,
		page,
	}) => {
		const objectFields = getRandomObjectFieldText({
			objectFieldsQuantity: 20,
		});

		const objectDefinition =
			await apiHelpers.objectAdmin.postRandomObjectDefinition({
				objectFields,
				status: {code: 0},
			});

		const objectLayoutName = getRandomString();

		await objectLayoutsPage.goto(objectDefinition.name);

		await objectLayoutsPage.createObjectLayout(objectLayoutName);

		await objectLayoutsPage.createObjectLayoutContent({
			objectLayoutBlockName: getRandomString(),
			objectLayoutName,
			objectLayoutTabName: getRandomString(),
		});

		objectFields.forEach(({label}) => {
			expect(
				page
					.frameLocator('iframe')
					.getByRole('option', {name: label.en_US})
			).toBeVisible();
		});

		// Clean up

		const objectDefinitionAPIClient =
			await apiHelpers.buildRestClient(ObjectDefinitionAPI);

		await objectDefinitionAPIClient.deleteObjectDefinition(
			objectDefinition.id
		);
	});

	test('can view Entries with Custom Layout Created', async ({
		apiHelpers,
		objectLayoutsPage,
		page,
		viewObjectEntriesPage,
	}) => {
		const objectDefinition =
			await apiHelpers.objectAdmin.postRandomObjectDefinition({
				status: {code: 0},
				titleObjectFieldName: 'textField',
			});

		apiHelpers.data.push({
			id: objectDefinition.id,
			type: 'objectDefinition',
		});

		const iframe = page.frameLocator('iframe');

		const blockName = getRandomString();

		await test.step('create layout and set it as default', async () => {
			await objectLayoutsPage.goto(objectDefinition.name);

			const objectLayoutName = getRandomString();

			await objectLayoutsPage.createObjectLayout(objectLayoutName);

			await objectLayoutsPage.createObjectLayoutContent({
				objectLayoutBlockName: blockName,
				objectLayoutName,
				objectLayoutTabName: getRandomString(),
			});

			await objectLayoutsPage.addObjectLayoutObjectField('textField');

			await iframe.getByRole('button', {name: 'Save'}).first().click();
		});

		await test.step('add object entry and assert that blockname is visible', async () => {
			await viewObjectEntriesPage.goto(objectDefinition.className);

			await viewObjectEntriesPage.clickAddObjectEntry(
				objectDefinition.label['en_US']
			);

			await viewObjectEntriesPage.fillObjectEntry({
				objectFieldBusinessType: 'Text',
				objectFieldLabel: 'textField',
				objectFieldValue: 'Entry A',
			});

			await viewObjectEntriesPage.saveObjectEntryButton.click();

			await waitForAlert(page);

			await viewObjectEntriesPage.backButton.click();

			await page.waitForLoadState('networkidle');

			await page
				.getByRole('row', {name: 'Entry A'})
				.getByRole('link')
				.click();

			await expect(
				page.locator('label').filter({hasText: blockName})
			).toBeVisible();

			await expect(page.getByLabel('textField')).toBeVisible();
		});
	});

	test('can view relationship child entry when selected', async ({
		apiHelpers,
		objectLayoutsPage,
		page,
		viewObjectEntriesPage,
	}) => {
		const objectDefinitionLabel1 = 'ObjectDefinitionLabel' + getRandomInt();
		const objectDefinitionName1 = 'ObjectDefinitionName' + getRandomInt();

		const objectDefinitionLabel2 = 'ObjectDefinitionLabel' + getRandomInt();
		const objectDefinitionName2 = 'ObjectDefinitionName' + getRandomInt();

		const objectFields1: Partial<ObjectField>[] = generateObjectFields({
			objectFieldBusinessTypes: ['Text'],
		});

		const objectEntry1 = await generateObjectEntryValues({
			objectEntryFormat: 'UI',
			objectFields: objectFields1,
		});

		const objectDefinitionAPIClient =
			await apiHelpers.buildRestClient(ObjectDefinitionAPI);

		const {body: objectDefinition} =
			await objectDefinitionAPIClient.postObjectDefinition({
				active: true,
				label: {
					en_US: objectDefinitionLabel1,
				},
				name: objectDefinitionName1,
				objectFields: objectFields1,
				pluralLabel: {
					en_US: objectDefinitionLabel1,
				},
				portlet: true,
				scope: 'company',
				status: {
					code: 0,
				},
			});

		apiHelpers.data.push({
			id: objectDefinition.id,
			type: 'objectDefinition',
		});

		const applicationName =
			'c/' + objectDefinition.name.toLowerCase() + 's';

		const {id: objectEntryId} =
			await apiHelpers.objectEntry.postObjectEntry(
				objectEntry1,
				applicationName
			);

		const objectFields2: Partial<ObjectField>[] = generateObjectFields({
			objectFieldBusinessTypes: ['Text'],
		});

		const objectEntry2 = await generateObjectEntryValues({
			objectEntryFormat: 'UI',
			objectFields: objectFields2,
		});

		const {body: objectDefinition2} =
			await objectDefinitionAPIClient.postObjectDefinition({
				active: true,
				label: {
					en_US: objectDefinitionLabel2,
				},
				name: objectDefinitionName2,
				objectFields: objectFields2,
				pluralLabel: {
					en_US: objectDefinitionLabel2,
				},
				portlet: true,
				scope: 'company',
				status: {
					code: 0,
				},
			});

		apiHelpers.data.push({
			id: objectDefinition2.id,
			type: 'objectDefinition',
		});

		const applicationName2 =
			'c/' + objectDefinition2.name.toLowerCase() + 's';

		await apiHelpers.objectEntry.postObjectEntry(
			objectEntry2,
			applicationName2
		);

		const objectRelationshipLabel =
			'objectRelationshipLabel' + getRandomInt();
		const objectRelationshipName =
			'objectRelationshipName' + Math.floor(Math.random() * 99);

		const objectRelationshipApiClient = await apiHelpers.buildRestClient(
			ObjectRelationshipAPI
		);

		const {body: objectRelationship} =
			await objectRelationshipApiClient.postObjectDefinitionByExternalReferenceCodeObjectRelationship(
				objectDefinition.externalReferenceCode,
				{
					label: {
						en_US: objectRelationshipLabel,
					},
					name: objectRelationshipName,
					objectDefinitionExternalReferenceCode1:
						objectDefinition.externalReferenceCode,
					objectDefinitionExternalReferenceCode2:
						objectDefinition2.externalReferenceCode,
					objectDefinitionId1: objectDefinition.id,
					objectDefinitionId2: objectDefinition2.id,
					objectDefinitionName2: objectDefinition2.name,
					type: 'oneToMany',
				}
			);

		apiHelpers.data.push({
			id: objectRelationship.id,
			type: 'objectRelationship',
		});

		await objectLayoutsPage.goto(objectDefinitionLabel1);

		const objectLayoutName = getRandomString();

		await objectLayoutsPage.createObjectLayout(objectLayoutName);

		await objectLayoutsPage.createObjectLayoutContent({
			objectLayoutBlockName: getRandomString(),
			objectLayoutName,
			objectLayoutTabName: getRandomString(),
		});

		await objectLayoutsPage.addObjectLayoutObjectField(
			objectFields1[0].label.en_US
		);

		const objectLayoutRelTabName = getRandomString();

		await objectLayoutsPage.createObjectRelationshipTab(
			objectLayoutName,
			objectLayoutRelTabName,
			objectRelationshipLabel
		);

		await waitForAlert(
			page,
			'Success:The object layout was updated successfully'
		);

		await viewObjectEntriesPage.goto(objectDefinition.className);

		await page.getByRole('link', {name: objectEntryId.toString()}).click();

		await page
			.getByRole('link')
			.filter({hasText: objectLayoutRelTabName})
			.click();

		await page.getByRole('button', {name: 'New'}).first().click();

		await page.getByRole('menuitem', {name: 'Create New'}).click();

		const objectChildEnty = 'ChildEntry' + getRandomInt();

		await page
			.getByLabel(objectFields2[0].label.en_US)
			.fill(objectChildEnty);

		await page.getByRole('button', {name: 'Save'}).click();

		await waitForAlert(page);

		await viewObjectEntriesPage.backButton.click();

		await page.getByRole('cell').getByRole('link').click();

		await page
			.getByLabel(objectFields2[0].label.en_US)
			.waitFor({state: 'visible'});

		await expect(page.getByRole('textbox').last()).toHaveValue(
			objectChildEnty
		);
	});
});
