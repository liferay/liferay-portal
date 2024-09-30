/**
 * SPDX-FileCopyrightText: (c)2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {apiHelpersTest} from '../../fixtures/apiHelpersTest';
import {editObjectDefinitionPagesTest} from '../../fixtures/editObjectDefinitionPagesTest';
import {loginTest} from '../../fixtures/loginTest';
import {objectPagesTest} from '../../fixtures/objectPagesTest';
import {getRandomInt} from '../../utils/getRandomInt';
import getRandomString from '../../utils/getRandomString';
import {waitForAlert} from '../../utils/waitForAlert';
import {mockObjectFields} from './utils/mockObjectFields';

export const test = mergeTests(
	apiHelpersTest,
	editObjectDefinitionPagesTest,
	loginTest(),
	objectPagesTest
);

const createdEntities = {
	objectDefinitions: [],
} as {
	objectDefinitions: ObjectDefinition[];
};

test.afterEach(async ({apiHelpers}) => {
	const {objectDefinitions} = createdEntities;

	for (const objectDefinition of objectDefinitions) {
		await apiHelpers.objectAdmin.deleteObjectDefinition(
			objectDefinition.id
		);
	}

	createdEntities.objectDefinitions = [];
});

test('Can create, read, update, and delete object entries that use the client extension as a storage type', async ({
	apiHelpers,
	editObjectDetailsPage,
	modelBuilderDiagramPage,
	objectFieldsPage,
	page,
	viewObjectEntriesPage,
}) => {
	const objectDefinition = await apiHelpers.objectAdmin.postObjectDefinition({
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
		objectFieldBusinessType: businessType,
		objectFieldLabel: label['en_US'],
	});

	await editObjectDetailsPage.goToDetailsTab();

	await editObjectDetailsPage.publishButton.click();

	await page.waitForEvent('domcontentloaded');

	// Create

	await viewObjectEntriesPage.goto(objectDefinition.id);

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
	).toBeHidden();
});
