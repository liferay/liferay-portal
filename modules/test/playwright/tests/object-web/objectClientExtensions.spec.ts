/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {
	ObjectDefinition,
	ObjectDefinitionApi,
} from '@liferay/object-admin-rest-client-js';
import {expect, mergeTests} from '@playwright/test';

import {apiHelpersTest} from '../../fixtures/apiHelpersTest';
import {editObjectDefinitionPagesTest} from '../../fixtures/editObjectDefinitionPagesTest';
import {featureFlagsTest} from '../../fixtures/featureFlagsTest';
import {loginTest} from '../../fixtures/loginTest';
import {objectPagesTest} from '../../fixtures/objectPagesTest';
import {getRandomInt} from '../../utils/getRandomInt';
import getRandomString from '../../utils/getRandomString';
import {waitForAlert} from '../../utils/waitForAlert';
import {createObjectFields, mockObjectFields} from './utils/mockObjectFields';

export const test = mergeTests(
	apiHelpersTest,
	editObjectDefinitionPagesTest,
	featureFlagsTest({
		'LPS-135430': {enabled: true},
	}),
	loginTest(),
	objectPagesTest
);

const createdEntities = {
	objectDefinitions: [],
} as {
	objectDefinitions: ObjectDefinition[];
};

test.afterEach(async ({apiHelpers}) => {
	const objectDefinitionAPIClient =
		await apiHelpers.buildRestClient(ObjectDefinitionApi);

	const {objectDefinitions} = createdEntities;

	for (const objectDefinition of objectDefinitions) {
		await objectDefinitionAPIClient.deleteObjectDefinition(
			objectDefinition.id
		);
	}

	createdEntities.objectDefinitions = [];
});

test.beforeEach(async ({apiHelpers}) => {
	const objectFields = createObjectFields('text', [
		{
			label: 'Name',
			name: 'name',
		},
	]);

	const objectDefinitionAPIClient =
		await apiHelpers.buildRestClient(ObjectDefinitionApi);

	const {body: objectDefinition} =
		await objectDefinitionAPIClient.postObjectDefinition({
			active: true,
			externalReferenceCode: getRandomString(),
			label: {
				en_US: 'Employee',
			},
			name: 'Employee',
			objectFields,
			objectFolderExternalReferenceCode: 'default',
			panelCategoryKey: 'control_panel.object',
			pluralLabel: {
				en_US: 'Employees',
			},
			portlet: true,
			scope: 'company',
			status: {
				code: 0,
			},
			storageType: 'default',
		});

	createdEntities.objectDefinitions.push(objectDefinition);
});

test('Can create, read, update, and delete object entries that use the client extension as a storage type', async ({
	apiHelpers,
	editObjectDetailsPage,
	modelBuilderDiagramPage,
	objectFieldsPage,
	page,
	viewObjectEntriesPage,
}) => {
	const objectDefinitionAPIClient =
		await apiHelpers.buildRestClient(ObjectDefinitionApi);

	const {body: objectDefinition} =
		await objectDefinitionAPIClient.postObjectDefinition({
			active: true,
			externalReferenceCode: getRandomString(),
			label: {
				en_US: getRandomString(),
			},
			name: 'Name' + getRandomInt(),
			objectFields: [],
			objectFolderExternalReferenceCode: 'default',
			panelCategoryKey: 'control_panel.object',
			pluralLabel: {
				en_US: getRandomString(),
			},
			portlet: true,
			scope: 'company',
			status: {
				code: 1,
			},
			storageType:
				'function#liferay-sample-etc-spring-boot-object-entry-manager-1',
		});

	createdEntities.objectDefinitions.push(objectDefinition);

	await objectFieldsPage.goto(objectDefinition.label['en_US']);

	const {objectEntry, objectFields} = await mockObjectFields({
		apiHelpers,
		objectEntryReturn: {format: 'UI'},
		objectFieldBusinessTypes: ['text'],
	});

	const [{businessType, label, name}] = objectFields;

	await objectFieldsPage.addObjectField({
		objectDefinitionNodes: modelBuilderDiagramPage.objectDefinitionNodes,
		objectFieldBusinessType: String(businessType),
		objectFieldLabel: label['en_US'],
	});

	await editObjectDetailsPage.goToDetailsTab();

	await editObjectDetailsPage.publishButton.click();

	await page.waitForEvent('domcontentloaded');

	// Create

	await viewObjectEntriesPage.goto(objectDefinition.className);

	await viewObjectEntriesPage.clickAddObjectEntry(
		objectDefinition.label['en_US']
	);

	await viewObjectEntriesPage.fillObjectEntry({
		objectFieldBusinessType: businessType,
		objectFieldLabel: label['en_US'],
		objectFieldValue: objectEntry[name].toString(),
	});

	await viewObjectEntriesPage.saveObjectEntryButton.click();

	await waitForAlert(page);

	await viewObjectEntriesPage.backButton.click();

	// Read

	await expect(
		page
			.locator(`.cell-${label['en_US']}`)
			.nth(1)
			.getByText(objectEntry[name].toString())
	).toBeVisible();

	// Update

	await page.getByRole('button', {name: 'Actions'}).click();

	await page.getByRole('menuitem', {name: 'View'}).click();

	const objectEntryUpdatedValue = getRandomString();

	await viewObjectEntriesPage.fillObjectEntry({
		objectFieldBusinessType: businessType,
		objectFieldLabel: label['en_US'],
		objectFieldValue: objectEntryUpdatedValue,
	});

	await viewObjectEntriesPage.saveObjectEntryButton.click();

	await expect(viewObjectEntriesPage.successMessage).toBeVisible();

	await viewObjectEntriesPage.backButton.click();

	await expect(
		page
			.locator(`.cell-${label['en_US']}`)
			.nth(1)
			.getByText(objectEntryUpdatedValue)
	).toBeVisible();

	// Delete

	await viewObjectEntriesPage.frontendDatasetActions.click();

	await viewObjectEntriesPage.frontendDatasetDeleteAction.click();

	await viewObjectEntriesPage.deletionConfirmationModal
		.getByRole('button', {
			name: 'Delete',
		})
		.click();

	await expect(
		page
			.locator(`.cell-${label['en_US']}`)
			.nth(1)
			.getByText(objectEntryUpdatedValue, {exact: true})
	).toBeAttached({attached: false});
});

test('Can trigger object action as a client extension', async ({
	editObjectActionPage,
	page,
	viewObjectActionsPage,
	viewObjectEntriesPage,
}) => {
	const [objectDefinition] = createdEntities.objectDefinitions;

	const objectField = objectDefinition.objectFields.find(
		({system}) => !system
	);

	await viewObjectActionsPage.goto(objectDefinition.label['en_US']);

	await editObjectActionPage.addNewAction(
		'object-action-executor[function#liferay-sample-etc-spring-boot-object-action-1]',
		'On After Add'
	);

	viewObjectEntriesPage.goto(objectDefinition.className);

	await viewObjectEntriesPage.clickAddObjectEntry(
		objectDefinition.label['en_US']
	);

	await viewObjectEntriesPage.fillObjectEntry({
		objectFieldBusinessType: objectField.businessType,
		objectFieldLabel: objectField.label['en_US'],
		objectFieldValue: getRandomString(),
	});

	await viewObjectEntriesPage.saveObjectEntryButton.click();

	await waitForAlert(page);

	await viewObjectActionsPage.goto(objectDefinition.label['en_US']);

	await expect(viewObjectActionsPage.lastExecutionCell.nth(1)).toContainText(
		'Success'
	);
});

test('Can trigger object validation as a client extension', async ({
	editObjectValidationPage,
	modalAddObjectValidationPage,
	objectValidationsPage,
	page,
	viewObjectEntriesPage,
}) => {
	const [objectDefinition] = createdEntities.objectDefinitions;

	const objectField = objectDefinition.objectFields.find(
		({system}) => !system
	);

	await objectValidationsPage.goto(objectDefinition.label['en_US']);

	await objectValidationsPage.addObjectValidationButton.click();

	const objectValidationLabel = 'ClientExtensionValidation' + getRandomInt();

	await modalAddObjectValidationPage.fillObjectValidationInputs(
		objectValidationLabel,
		'Liferay Sample Etc Spring Boot Spring Boot Object Validation Rule 1'
	);

	await page.getByText(objectValidationLabel).click();

	await objectValidationsPage.activeValitionToggle.check();

	await objectValidationsPage.errorMessageInput.fill(
		'This entry is not possible.'
	);

	await editObjectValidationPage.saveObjectValidationButton.click();

	await page.waitForEvent('domcontentloaded');

	await viewObjectEntriesPage.goto(objectDefinition.className);

	await viewObjectEntriesPage.clickAddObjectEntry(
		objectDefinition.label['en_US']
	);

	await viewObjectEntriesPage.fillObjectEntry({
		objectFieldBusinessType: objectField.businessType,
		objectFieldLabel: objectField.label['en_US'],
		objectFieldValue: 'Invalid Name',
	});

	await viewObjectEntriesPage.saveObjectEntryButton.click();

	await waitForAlert(page, 'This entry is not possible.', {type: 'danger'});

	await viewObjectEntriesPage.backButton.click();

	await expect(
		page.locator('.cell-name').getByText('Invalid Name')
	).toBeAttached({attached: false});

	await viewObjectEntriesPage.clickAddObjectEntry(
		objectDefinition.label['en_US']
	);

	await viewObjectEntriesPage.fillObjectEntry({
		objectFieldBusinessType: objectField.businessType,
		objectFieldLabel: objectField.label['en_US'],
		objectFieldValue: 'Valid Name',
	});

	await viewObjectEntriesPage.saveObjectEntryButton.click();

	await waitForAlert(page);

	await viewObjectEntriesPage.backButton.click();

	await expect(
		page.locator('.cell-name').getByText('Valid Name')
	).toBeVisible();
});
