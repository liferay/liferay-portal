/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {ObjectRelationshipAPI} from '@liferay/object-admin-rest-client-js';
import {expect, mergeTests} from '@playwright/test';

import {dataApiHelpersTest} from '../../../fixtures/dataApiHelpersTest';
import {isolatedSiteTest} from '../../../fixtures/isolatedSiteTest';
import {loginTest} from '../../../fixtures/loginTest';
import {objectPagesTest} from '../../../fixtures/objectPagesTest';
import {usersAndOrganizationsPagesTest} from '../../../fixtures/usersAndOrganizationsPagesTest';
import {getRandomInt} from '../../../utils/getRandomInt';
import {performUserSwitch, userData} from '../../../utils/performLogin';
import {waitForAlert} from '../../../utils/waitForAlert';
import {generateObjectFields} from '../utils/generateObjectFields';

const test = mergeTests(
	dataApiHelpersTest,
	isolatedSiteTest,
	loginTest(),
	objectPagesTest,
	usersAndOrganizationsPagesTest
);

test('can anonymize object entries', async ({
	apiHelpers,
	page,
	personalDataErasurePage,
	usersAndOrganizationsPage,
	viewObjectEntriesPage,
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

	const applicationName = 'c/' + objectDefinition.name.toLowerCase() + 's';
	const textFieldName = objectFields[0].name;

	await apiHelpers.objectEntry.postObjectEntry(
		{[textFieldName]: 'Entry A'},
		applicationName
	);

	const userAccount = await apiHelpers.headlessAdminUser.postUserAccount();

	apiHelpers.data.push({id: userAccount.id, type: 'userAccount'});

	userData[userAccount.alternateName] = {
		name: userAccount.givenName,
		password: 'test',
		surname: userAccount.familyName,
	};

	const role =
		await apiHelpers.headlessAdminUser.getRoleByName('Administrator');

	await apiHelpers.headlessAdminUser.postRoleByExternalReferenceCodeUserAccountAssociation(
		role.externalReferenceCode,
		userAccount.id
	);

	await performUserSwitch(page, userAccount.alternateName);

	for (const entryName of ['Entry B', 'Entry C', 'Entry D', 'Entry E']) {
		await apiHelpers.objectEntry.postObjectEntry(
			{[textFieldName]: entryName},
			applicationName
		);
	}

	await performUserSwitch(page, 'test');

	await usersAndOrganizationsPage.goToUsers(false);

	page.on('dialog', (dialog) => {
		dialog.accept().catch(() => {});
	});

	await expect(async () => {
		await (
			await usersAndOrganizationsPage.usersTableRowActions(
				userAccount.alternateName
			)
		).click();

		await expect(
			usersAndOrganizationsPage.deletePersonalDataMenuItem
		).toBeVisible({timeout: 500});
	}).toPass({timeout: 5000});

	await usersAndOrganizationsPage.deletePersonalDataMenuItem.click();

	await personalDataErasurePage.selectAllItemsOnPageCheckbox.check();
	await personalDataErasurePage.allSelectedButton.click();

	await personalDataErasurePage.anonymizeMenuItem.click();

	await personalDataErasurePage.anonymizeButton.click();

	await waitForAlert(page, undefined, {first: true});

	await usersAndOrganizationsPage.goToUsers();

	await usersAndOrganizationsPage.filterUsers('Inactive');

	await viewObjectEntriesPage.goto(objectDefinition.className);

	for (let i = 0; i < 4; i++) {
		await expect(
			page.getByRole('cell', {name: 'Anonymous Anonymous'}).nth(i)
		).toBeVisible();
	}
});

test('can delete object entries via personal data management', async ({
	apiHelpers,
	page,
	personalDataErasurePage,
	usersAndOrganizationsPage,
	viewObjectEntriesPage,
}) => {
	const objectFields = generateObjectFields({
		objectFieldBusinessTypes: [
			{
				businessType: 'Text',
				label: {en_US: 'Custom Field'},
				name: 'customField',
			},
		],
	});

	const objectDefinition =
		await apiHelpers.objectAdmin.postRandomObjectDefinition({
			objectFields,
			status: {code: 0},
			titleObjectFieldName: 'customField',
		});

	apiHelpers.data.push({
		id: objectDefinition.id,
		type: 'objectDefinition',
	});

	const objectRelationshipAPIClient = await apiHelpers.buildRestClient(
		ObjectRelationshipAPI
	);

	const objectRelationshipName = 'relationship' + getRandomInt();

	await objectRelationshipAPIClient.postObjectDefinitionByExternalReferenceCodeObjectRelationship(
		objectDefinition.externalReferenceCode,
		{
			label: {en_US: 'Relationship'},
			name: objectRelationshipName,
			objectDefinitionExternalReferenceCode1:
				objectDefinition.externalReferenceCode,
			objectDefinitionExternalReferenceCode2:
				objectDefinition.externalReferenceCode,
			objectDefinitionId1: objectDefinition.id,
			objectDefinitionId2: objectDefinition.id,
			objectDefinitionName2: objectDefinition.name,
			type: 'manyToMany',
		}
	);

	const applicationName = 'c/' + objectDefinition.name.toLowerCase() + 's';

	const entryA = await apiHelpers.objectEntry.postObjectEntry(
		{customField: 'Entry A'},
		applicationName
	);

	const userAccount = await apiHelpers.headlessAdminUser.postUserAccount();

	apiHelpers.data.push({id: userAccount.id, type: 'userAccount'});

	userData[userAccount.alternateName] = {
		name: userAccount.givenName,
		password: 'test',
		surname: userAccount.familyName,
	};

	const role =
		await apiHelpers.headlessAdminUser.getRoleByName('Administrator');

	await apiHelpers.headlessAdminUser.postRoleByExternalReferenceCodeUserAccountAssociation(
		role.externalReferenceCode,
		userAccount.id
	);

	await performUserSwitch(page, userAccount.alternateName);

	const entryB = await apiHelpers.objectEntry.postObjectEntry(
		{customField: 'Entry B'},
		applicationName
	);

	const entryC = await apiHelpers.objectEntry.postObjectEntry(
		{customField: 'Entry C'},
		applicationName
	);

	const entryD = await apiHelpers.objectEntry.postObjectEntry(
		{customField: 'Entry D'},
		applicationName
	);

	await apiHelpers.objectEntry.postObjectEntry(
		{customField: 'Entry E'},
		applicationName
	);

	await performUserSwitch(page, 'test');

	await apiHelpers.objectEntry.putByExternalReferenceCodeCurrentExternalReferenceCodeObjectRelationshipNameRelatedExternalReferenceCode(
		{
			applicationName,
			currentExternalReferenceCode: entryA.externalReferenceCode,
			objectRelationshipName,
			relatedExternalReferenceCode: entryB.externalReferenceCode,
		}
	);

	await apiHelpers.objectEntry.putByExternalReferenceCodeCurrentExternalReferenceCodeObjectRelationshipNameRelatedExternalReferenceCode(
		{
			applicationName,
			currentExternalReferenceCode: entryC.externalReferenceCode,
			objectRelationshipName,
			relatedExternalReferenceCode: entryD.externalReferenceCode,
		}
	);

	await usersAndOrganizationsPage.goToUsers(false);

	page.on('dialog', (dialog) => {
		dialog.accept().catch(() => {});
	});

	await expect(async () => {
		await (
			await usersAndOrganizationsPage.usersTableRowActions(
				userAccount.alternateName
			)
		).click();

		await expect(
			usersAndOrganizationsPage.deletePersonalDataMenuItem
		).toBeVisible({timeout: 500});
	}).toPass({timeout: 5000});

	await usersAndOrganizationsPage.deletePersonalDataMenuItem.click();

	await personalDataErasurePage.selectAllItemsOnPageCheckbox.check();
	await personalDataErasurePage.allSelectedButton.click();

	await personalDataErasurePage.deleteMenuItem.click();

	await waitForAlert(page);

	await usersAndOrganizationsPage.goToUsers(true);

	await usersAndOrganizationsPage.filterUsers('Inactive');

	await usersAndOrganizationsPage.activateUsers([userAccount.name]);

	await viewObjectEntriesPage.goto(objectDefinition.className);

	for (const entryName of ['Entry B', 'Entry C', 'Entry D', 'Entry E']) {
		await expect(
			page.locator('table').getByRole('cell', {name: entryName})
		).not.toBeVisible();
	}

	await expect(
		page.locator('table').getByRole('cell', {name: 'Entry A'})
	).toBeVisible();
});

test('can export object entries via personal data management', async ({
	apiHelpers,
	exportUserDataPage,
	page,
	usersAndOrganizationsPage,
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

	const applicationName = 'c/' + objectDefinition.name.toLowerCase() + 's';
	const textFieldName = objectFields[0].name;

	const userAccount = await apiHelpers.headlessAdminUser.postUserAccount();

	apiHelpers.data.push({id: userAccount.id, type: 'userAccount'});

	userData[userAccount.alternateName] = {
		name: userAccount.givenName,
		password: 'test',
		surname: userAccount.familyName,
	};

	const role =
		await apiHelpers.headlessAdminUser.getRoleByName('Administrator');

	await apiHelpers.headlessAdminUser.postRoleByExternalReferenceCodeUserAccountAssociation(
		role.externalReferenceCode,
		userAccount.id
	);

	await performUserSwitch(page, userAccount.alternateName);

	for (const entryNumber of ['1', '2', '3']) {
		await apiHelpers.objectEntry.postObjectEntry(
			{[textFieldName]: `Entry ${entryNumber}`},
			applicationName
		);
	}

	await performUserSwitch(page, 'test');

	await usersAndOrganizationsPage.goToUsers(false);

	await expect(async () => {
		await (
			await usersAndOrganizationsPage.usersTableRowActions(
				userAccount.alternateName
			)
		).click();

		await expect(
			usersAndOrganizationsPage.exportPersonalDataItem
		).toBeVisible({timeout: 500});
	}).toPass({timeout: 5000});

	await usersAndOrganizationsPage.exportPersonalDataItem.click();

	await exportUserDataPage.addExportProcessesButton.click();

	await page.getByLabel('Objects').check();

	await exportUserDataPage.exportButton.click();

	await expect(
		exportUserDataPage.statusText('Successful').first()
	).toBeVisible();

	const downloadPromise = page.waitForEvent('download');

	await exportUserDataPage.actionsButton.click();

	await page.getByRole('menuitem', {name: /Download/}).click();

	const download = await downloadPromise;

	expect(download.suggestedFilename()).toMatch(/UAD_.*com\.liferay\.object/);
});
