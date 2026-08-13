/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {
	ObjectDefinitionAPI,
	ObjectRelationshipAPI,
} from '@liferay/object-admin-rest-client-js';
import {expect, mergeTests} from '@playwright/test';

import {dataApiHelpersTest} from '../../../fixtures/dataApiHelpersTest';
import {isolatedSiteTest} from '../../../fixtures/isolatedSiteTest';
import {loginTest} from '../../../fixtures/loginTest';
import {objectPagesTest} from '../../../fixtures/objectPagesTest';
import {getRandomInt} from '../../../utils/getRandomInt';
import {performUserSwitch, userData} from '../../../utils/performLogin';
import {waitForAlert} from '../../../utils/waitForAlert';
import {generateObjectFields} from '../utils/generateObjectFields';

const test = mergeTests(
	dataApiHelpersTest,
	isolatedSiteTest,
	loginTest(),
	objectPagesTest
);

async function getAccountDefinition(apiHelpers: any) {
	const objectDefinitionAPIClient =
		await apiHelpers.buildRestClient(ObjectDefinitionAPI);

	const {body: accountDefinition} =
		await objectDefinitionAPIClient.getObjectDefinitionByExternalReferenceCode(
			'L_ACCOUNT'
		);

	return accountDefinition;
}

test('can associate any type of account', async ({
	apiHelpers,
	page,
	viewObjectEntriesPage,
}) => {
	const objectDefinitionAPIClient =
		await apiHelpers.buildRestClient(ObjectDefinitionAPI);
	const objectRelationshipAPIClient = await apiHelpers.buildRestClient(
		ObjectRelationshipAPI
	);

	const accountDefinition = await getAccountDefinition(apiHelpers);

	const businessAccount = await apiHelpers.headlessAdminUser.postAccount({
		name: 'Business' + getRandomInt(),
		type: 'business',
	});

	const personAccount = await apiHelpers.headlessAdminUser.postAccount({
		name: 'Person' + getRandomInt(),
		type: 'person',
	});

	const guestAccount = await apiHelpers.headlessAdminUser.postAccount({
		name: 'Guest' + getRandomInt(),
		type: 'guest',
	});

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

	const relationshipName = 'relationship' + getRandomInt();

	const {body: relationship} =
		await objectRelationshipAPIClient.postObjectDefinitionByExternalReferenceCodeObjectRelationship(
			'L_ACCOUNT',
			{
				label: {en_US: 'Relationship'},
				name: relationshipName,
				objectDefinitionExternalReferenceCode2:
					objectDefinition.externalReferenceCode,
				objectDefinitionId2: objectDefinition.id,
				objectDefinitionName2: objectDefinition.name,
				type: 'oneToMany',
			}
		);

	try {
		await objectDefinitionAPIClient.patchObjectDefinition(
			accountDefinition.id,
			{titleObjectFieldName: 'type'}
		);

		const applicationName =
			'c/' + objectDefinition.name.toLowerCase() + 's';
		const fieldName = objectFields[0].name!;

		for (const account of [businessAccount, personAccount, guestAccount]) {
			await apiHelpers.objectEntry.postObjectEntry(
				{
					[fieldName]: 'Entry' + getRandomInt(),
					['r_' + relationshipName + '_accountEntryId']: account.id,
				},
				applicationName
			);
		}

		await viewObjectEntriesPage.goto(objectDefinition.className);

		for (const accountType of ['business', 'person', 'guest']) {
			await expect(
				page.locator('table').getByText(accountType)
			).toBeVisible();
		}

		await objectDefinitionAPIClient.patchObjectDefinition(
			accountDefinition.id,
			{titleObjectFieldName: 'name'}
		);
	}
	finally {
		await objectRelationshipAPIClient.deleteObjectRelationship(
			relationship.id
		);
	}
});

test('cannot delete relationship when deletion type is disassociate', async ({
	apiHelpers,
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

	const objectRelationshipAPIClient = await apiHelpers.buildRestClient(
		ObjectRelationshipAPI
	);

	const relationshipName = 'relationship' + getRandomInt();

	const {body: relationship} =
		await objectRelationshipAPIClient.postObjectDefinitionByExternalReferenceCodeObjectRelationship(
			'L_ACCOUNT',
			{
				deletionType: 'disassociate',
				label: {en_US: 'DisassociateRel'},
				name: relationshipName,
				objectDefinitionExternalReferenceCode2:
					objectDefinition.externalReferenceCode,
				objectDefinitionId2: objectDefinition.id,
				objectDefinitionName2: objectDefinition.name,
				type: 'oneToMany',
			}
		);

	try {
		const accountName = 'Account' + getRandomInt();

		const account = await apiHelpers.headlessAdminUser.postAccount({
			name: accountName,
			type: 'business',
		});

		apiHelpers.data.push({id: account.id, type: 'account'});

		const applicationName =
			'c/' + objectDefinition.name.toLowerCase() + 's';
		const fieldName = objectFields[0].name!;

		await apiHelpers.objectEntry.postObjectEntry(
			{
				[fieldName]: 'ChildEntry1',
				['r_' + relationshipName + '_accountEntryId']: account.id,
			},
			applicationName
		);

		await apiHelpers.headlessAdminUser.deleteAccount(account.id);

		apiHelpers.data.splice(
			apiHelpers.data.findIndex(
				(item) => item.id === account.id && item.type === 'account'
			),
			1
		);

		const entries =
			await apiHelpers.objectEntry.getObjectDefinitionObjectEntries(
				applicationName
			);

		expect(entries.totalCount).toBe(1);

		expect(
			entries.items[0]['r_' + relationshipName + '_accountEntryId']
		).toBeFalsy();
	}
	finally {
		await objectRelationshipAPIClient.deleteObjectRelationship(
			relationship.id
		);
	}
});

test('cannot edit system fields', async ({
	apiHelpers,
	objectFieldsPage,
	page,
}) => {
	const accountDefinition = await getAccountDefinition(apiHelpers);

	await objectFieldsPage.goto(accountDefinition.label['en_US']);

	await page.getByRole('listitem').filter({hasText: 'Fields'}).click();

	const sidePanel = page.frameLocator('iframe');

	for (const fieldLabel of ['Name', 'Description', 'Type']) {
		await page
			.locator('table tbody')
			.getByRole('link', {name: fieldLabel})
			.click();

		await expect(
			sidePanel.locator('input[name="name"]')
		).not.toBeEditable();

		await page.frameLocator('iframe').getByLabel('Cancel').click();
	}
});

test('can view description on field entry', async ({
	apiHelpers,
	page,
	viewObjectEntriesPage,
}) => {
	const objectDefinitionAPIClient =
		await apiHelpers.buildRestClient(ObjectDefinitionAPI);
	const objectRelationshipAPIClient = await apiHelpers.buildRestClient(
		ObjectRelationshipAPI
	);

	const accountDefinition =
		objectDefinitionAPIClient.getObjectDefinitionByExternalReferenceCode(
			'L_ACCOUNT'
		);

	const accountDescription = 'Description' + getRandomInt();

	await apiHelpers.headlessAdminUser.postAccount({
		description: accountDescription,
		name: 'Account' + getRandomInt(),
		type: 'business',
	});

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

	const relationshipName = 'relationship' + getRandomInt();

	const {body: relationship} =
		await objectRelationshipAPIClient.postObjectDefinitionByExternalReferenceCodeObjectRelationship(
			'L_ACCOUNT',
			{
				label: {en_US: 'Relationship'},
				name: relationshipName,
				objectDefinitionExternalReferenceCode2:
					objectDefinition.externalReferenceCode,
				objectDefinitionId2: objectDefinition.id,
				objectDefinitionName2: objectDefinition.name,
				type: 'oneToMany',
			}
		);

	try {
		await objectDefinitionAPIClient.patchObjectDefinition(
			(await accountDefinition).body.id,
			{titleObjectFieldName: 'description'}
		);

		await viewObjectEntriesPage.goto(objectDefinition.className);

		await viewObjectEntriesPage.clickAddObjectEntry(
			objectDefinition.label['en_US']
		);

		await viewObjectEntriesPage.selectDropdownItemWithSearch(
			accountDescription
		);

		await viewObjectEntriesPage.saveObjectEntryButton.click();

		await expect(viewObjectEntriesPage.successMessage).toBeVisible();

		await viewObjectEntriesPage.backButton.click();

		await expect(
			page.locator('table').getByText(accountDescription)
		).toBeVisible();
	}
	finally {
		await objectDefinitionAPIClient.patchObjectDefinition(
			(await accountDefinition).body.id,
			{titleObjectFieldName: 'name'}
		);

		await objectRelationshipAPIClient.deleteObjectRelationship(
			relationship.id
		);
	}
});

test('can view fields label by default', async ({
	apiHelpers,
	objectFieldsPage,
	page,
}) => {
	const accountDefinition = await getAccountDefinition(apiHelpers);

	await objectFieldsPage.goto(accountDefinition.label['en_US']);

	await page.getByRole('listitem').filter({hasText: 'Fields'}).click();

	const tbody = page.locator('table tbody');

	for (const fieldLabel of ['Name', 'Description', 'Type']) {
		const fieldRow = tbody
			.getByRole('row')
			.filter({hasText: fieldLabel})
			.first();

		await expect(fieldRow).toBeVisible();

		await expect(fieldRow.getByText('Text')).toBeVisible();
	}
});

test('can view name and type as mandatory fields', async ({
	apiHelpers,
	objectFieldsPage,
	page,
}) => {
	const accountDefinition = await getAccountDefinition(apiHelpers);

	await objectFieldsPage.goto(accountDefinition.label['en_US']);

	await page.getByRole('listitem').filter({hasText: 'Fields'}).click();

	const tbody = page.locator('table tbody');

	for (const fieldLabel of ['Name', 'Type']) {
		const fieldRow = tbody
			.getByRole('row')
			.filter({hasText: fieldLabel})
			.first();

		await expect(fieldRow.getByText('Yes')).toBeVisible();
	}
});

test('can view name on field entry', async ({
	apiHelpers,
	page,
	viewObjectEntriesPage,
}) => {
	const objectRelationshipAPIClient = await apiHelpers.buildRestClient(
		ObjectRelationshipAPI
	);

	const accountName = 'TestAccount' + getRandomInt();

	await apiHelpers.headlessAdminUser.postAccount({
		name: accountName,
		type: 'business',
	});

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

	const relationshipName = 'relationship' + getRandomInt();

	const {body: relationship} =
		await objectRelationshipAPIClient.postObjectDefinitionByExternalReferenceCodeObjectRelationship(
			'L_ACCOUNT',
			{
				label: {en_US: 'Relationship'},
				name: relationshipName,
				objectDefinitionExternalReferenceCode2:
					objectDefinition.externalReferenceCode,
				objectDefinitionId2: objectDefinition.id,
				objectDefinitionName2: objectDefinition.name,
				type: 'oneToMany',
			}
		);

	try {
		await viewObjectEntriesPage.goto(objectDefinition.className);

		await viewObjectEntriesPage.clickAddObjectEntry(
			objectDefinition.label['en_US']
		);

		await viewObjectEntriesPage.selectDropdownItemWithSearch(accountName);

		await viewObjectEntriesPage.saveObjectEntryButton.click();

		await expect(viewObjectEntriesPage.successMessage).toBeVisible();

		await viewObjectEntriesPage.backButton.click();

		await expect(
			page.locator('table td').getByText(accountName).first()
		).toBeVisible();
	}
	finally {
		await objectRelationshipAPIClient.deleteObjectRelationship(
			relationship.id
		);
	}
});

test('can view relationship on custom object entries with custom view', async ({
	apiHelpers,
	editObjectViewPage,
	objectFieldsPage,
	objectViewPage,
	page,
	viewObjectEntriesPage,
}) => {
	const objectDefinitionAPIClient =
		await apiHelpers.buildRestClient(ObjectDefinitionAPI);

	const accountDefinition = await getAccountDefinition(apiHelpers);

	await objectDefinitionAPIClient.patchObjectDefinition(
		accountDefinition.id,
		{
			titleObjectFieldName: 'name',
		}
	);

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

	const objectRelationshipAPIClient = await apiHelpers.buildRestClient(
		ObjectRelationshipAPI
	);

	const relationshipName = 'relationship' + getRandomInt();

	const {body: relationship} =
		await objectRelationshipAPIClient.postObjectDefinitionByExternalReferenceCodeObjectRelationship(
			'L_ACCOUNT',
			{
				label: {en_US: 'AccountRel'},
				name: relationshipName,
				objectDefinitionExternalReferenceCode2:
					objectDefinition.externalReferenceCode,
				objectDefinitionId2: objectDefinition.id,
				objectDefinitionName2: objectDefinition.name,
				type: 'oneToMany',
			}
		);

	try {
		const accountName = 'Account' + getRandomInt();

		const account = await apiHelpers.headlessAdminUser.postAccount({
			name: accountName,
			type: 'business',
		});

		const applicationName =
			'c/' + objectDefinition.name.toLowerCase() + 's';
		const fieldName = objectFields[0].name!;

		await apiHelpers.objectEntry.postObjectEntry(
			{
				[fieldName]: 'EntryValue',
				['r_' + relationshipName + '_accountEntryId']: account.id,
			},
			applicationName
		);

		await objectFieldsPage.goto(objectDefinition.label['en_US']);

		await page.getByRole('listitem').filter({hasText: 'Views'}).click();

		const viewName = 'CustomView' + getRandomInt();

		await objectViewPage.createObjectView(viewName);

		await expect(page.getByRole('link', {name: viewName})).toBeVisible();

		await page.getByRole('link', {name: viewName}).click();

		await editObjectViewPage.selectObjectFields(['AccountRel']);

		await page
			.frameLocator('iframe')
			.getByRole('tab', {name: 'Basic Info'})
			.click();

		await editObjectViewPage.markAsDefaultButton.check();

		await editObjectViewPage.saveButton.click();

		await page.waitForLoadState('networkidle');

		await viewObjectEntriesPage.goto(objectDefinition.className);

		await expect(
			page.locator('table td').getByText(accountName).first()
		).toBeVisible();
	}
	finally {
		await objectRelationshipAPIClient.deleteObjectRelationship(
			relationship.id
		);
	}
});

test('can user view system account when allowed', async ({
	apiHelpers,
	page,
	viewObjectDefinitionsPage,
}) => {
	const company =
		await apiHelpers.jsonWebServicesCompany.getCompanyByWebId(
			'liferay.com'
		);

	const role = await apiHelpers.headlessAdminUser.postRole({
		name: 'ObjViewRole' + getRandomInt(),
		rolePermissions: [
			{
				actionIds: ['ACCESS_IN_CONTROL_PANEL', 'VIEW'],
				primaryKey: String(company.companyId),
				resourceName:
					'com_liferay_object_web_internal_object_definitions_portlet_ObjectDefinitionsPortlet',
				scope: 1,
			},
			{
				actionIds: ['VIEW'],
				primaryKey: String(company.companyId),
				resourceName: 'com.liferay.object.model.ObjectDefinition',
				scope: 1,
			},
			{
				actionIds: ['VIEW_CONTROL_PANEL'],
				primaryKey: String(company.companyId),
				resourceName: '90',
				scope: 1,
			},
		],
	});

	apiHelpers.data.push({id: role.id, type: 'role'});

	const user = await apiHelpers.headlessAdminUser.postUserAccount();

	apiHelpers.data.push({id: user.id, type: 'userAccount'});

	userData[user.alternateName] = {
		name: user.givenName,
		password: 'test',
		surname: user.familyName,
	};

	await apiHelpers.headlessAdminUser.assignUserToRole(
		role.externalReferenceCode,
		user.id
	);

	await performUserSwitch(page, user.alternateName);

	await viewObjectDefinitionsPage.goto();

	await expect(
		page.getByRole('link', {exact: true, name: 'Account'})
	).toBeVisible();
});

test('can view type on field entry', async ({
	apiHelpers,
	page,
	viewObjectEntriesPage,
}) => {
	const objectDefinitionAPIClient =
		await apiHelpers.buildRestClient(ObjectDefinitionAPI);
	const objectRelationshipAPIClient = await apiHelpers.buildRestClient(
		ObjectRelationshipAPI
	);

	const accountDefinition = await getAccountDefinition(apiHelpers);

	const account = await apiHelpers.headlessAdminUser.postAccount({
		name: 'Account' + getRandomInt(),
		type: 'business',
	});

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

	const relationshipName = 'relationship' + getRandomInt();

	const {body: relationship} =
		await objectRelationshipAPIClient.postObjectDefinitionByExternalReferenceCodeObjectRelationship(
			'L_ACCOUNT',
			{
				label: {en_US: 'Relationship'},
				name: relationshipName,
				objectDefinitionExternalReferenceCode2:
					objectDefinition.externalReferenceCode,
				objectDefinitionId2: objectDefinition.id,
				objectDefinitionName2: objectDefinition.name,
				type: 'oneToMany',
			}
		);

	try {
		await objectDefinitionAPIClient.patchObjectDefinition(
			accountDefinition.id,
			{titleObjectFieldName: 'type'}
		);

		const applicationName =
			'c/' + objectDefinition.name.toLowerCase() + 's';
		const fieldName = objectFields[0].name!;

		await apiHelpers.objectEntry.postObjectEntry(
			{
				[fieldName]: 'Entry' + getRandomInt(),
				['r_' + relationshipName + '_accountEntryId']: account.id,
			},
			applicationName
		);

		await viewObjectEntriesPage.goto(objectDefinition.className);

		await expect(page.locator('table').getByText('business')).toBeVisible();
	}
	finally {
		await objectDefinitionAPIClient.patchObjectDefinition(
			accountDefinition.id,
			{titleObjectFieldName: 'name'}
		);

		await objectRelationshipAPIClient.deleteObjectRelationship(
			relationship.id
		);
	}
});

test('edit title field on system account', async ({
	apiHelpers,
	objectDetailsPage,
	page,
}) => {
	const accountDefinition = await getAccountDefinition(apiHelpers);
	const objectDefinitionAPIClient =
		await apiHelpers.buildRestClient(ObjectDefinitionAPI);

	await objectDetailsPage.goto(accountDefinition.label['en_US']);

	await expect(page.getByText('ERC:L_ACCOUNT')).toBeVisible();

	const titleFieldGroup = page
		.locator('.form-group')
		.filter({hasText: 'Entry Title Field'});

	try {
		await titleFieldGroup.getByRole('combobox').click();

		await page.getByRole('option', {name: 'Type'}).click();

		await page.getByRole('button', {name: 'Save'}).click();

		await waitForAlert(page, 'The object was saved successfully.');

		await expect(titleFieldGroup.getByRole('combobox')).toHaveText(/Type/);

		await titleFieldGroup.getByRole('combobox').click();

		await page.getByRole('option', {name: 'Name'}).click();

		await page.getByRole('button', {name: 'Save'}).click();

		await waitForAlert(page, 'The object was saved successfully.');
	}
	finally {
		await objectDefinitionAPIClient.patchObjectDefinition(
			accountDefinition.id,
			{titleObjectFieldName: 'name'}
		);
	}
});
