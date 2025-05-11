/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {
	ObjectRelationship,
	ObjectRelationshipAPI,
} from '@liferay/object-admin-rest-client-js';
import {expect, mergeTests} from '@playwright/test';

import {dataApiHelpersTest} from '../../../fixtures/dataApiHelpersTest';
import {featureFlagsTest} from '../../../fixtures/featureFlagsTest';
import {loginTest} from '../../../fixtures/loginTest';
import {objectPagesTest} from '../../../fixtures/objectPagesTest';
import {getRandomInt} from '../../../utils/getRandomInt';
import getRandomString from '../../../utils/getRandomString';
import performLogin, {
	performLogout,
	userData,
} from '../../../utils/performLogin';
import {pushToApiHelpersData} from '../../../utils/pushToApiHelpersData';

export const test = mergeTests(
	dataApiHelpersTest,
	featureFlagsTest({
		'LPD-34594': {enabled: true},
	}),
	loginTest(),
	objectPagesTest
);

test.beforeEach(({page}) => {
	page.setViewportSize({height: 1080, width: 1920});
});

test.describe('Manage root model elements through View Object Entries', () => {
	test('assert management of object entries that are account restricted', async ({
		apiHelpers,
		editObjectDetailsPage,
		page,
		viewObjectDefinitionsPage,
		viewObjectEntriesPage,
	}) => {
		const objectRelationships: ObjectRelationship[] = [];

		try {

			// Create two custom objects

			const objectDefinition1 =
				await apiHelpers.objectAdmin.postRandomObjectDefinition({
					objectFolderExternalReferenceCode: 'default',
					panelCategoryKey: 'control_panel.object',
					status: {code: 0},
				});

			const objectDefinition2 =
				await apiHelpers.objectAdmin.postRandomObjectDefinition({
					objectFolderExternalReferenceCode: 'default',
					panelCategoryKey: 'control_panel.object',
					status: {code: 0},
					titleObjectFieldName: 'textField',
				});

			apiHelpers.data.push({
				id: objectDefinition1.id,
				type: 'objectDefinition',
			});
			apiHelpers.data.push({
				id: objectDefinition2.id,
				type: 'objectDefinition',
			});

			// Create an role for users to manage object entries by control panel

			const companyId = await page.evaluate(() => {
				return Liferay.ThemeDisplay.getCompanyId();
			});

			const role = await apiHelpers.headlessAdminUser.postRole({
				name: 'Object entry manager ' + getRandomString(),
				rolePermissions: [
					{
						actionIds: ['VIEW_CONTROL_PANEL'],
						primaryKey: companyId,
						resourceName: '90',
						scope: 1,
					},
					{
						actionIds: ['ACCESS_IN_CONTROL_PANEL'],
						primaryKey: companyId,
						resourceName: `com_liferay_object_web_internal_object_definitions_portlet_ObjectDefinitionsPortlet_${objectDefinition1.className.split('#')[1]}`,
						scope: 1,
					},
					{
						actionIds: ['ADD_OBJECT_ENTRY'],
						primaryKey: companyId,
						resourceName: `com.liferay.object#${objectDefinition1.id}`,
						scope: 1,
					},
				],
			});

			apiHelpers.data.push({id: role.id, type: 'role'});

			// Create 2 accounts and 2 users

			const account1 = await apiHelpers.headlessAdminUser.postAccount();

			const account2 = await apiHelpers.headlessAdminUser.postAccount();

			const user1 = await apiHelpers.headlessAdminUser.postUserAccount();

			const user2 = await apiHelpers.headlessAdminUser.postUserAccount();

			apiHelpers.data.push({
				id: user1.id,
				type: 'userAccount',
			});

			apiHelpers.data.push({
				id: user2.id,
				type: 'userAccount',
			});

			// Assign users to respective accounts and role

			await apiHelpers.headlessAdminUser.assignUserToAccountByEmailAddress(
				account1.id,
				[user1.emailAddress]
			);

			await apiHelpers.headlessAdminUser.assignUserToAccountByEmailAddress(
				account2.id,
				[user2.emailAddress]
			);

			await apiHelpers.headlessAdminUser.assignUserToRole(
				role.externalReferenceCode,
				user1.id
			);

			await apiHelpers.headlessAdminUser.assignUserToRole(
				role.externalReferenceCode,
				user2.id
			);

			// Create an object relationship from Account object to Custom Object 1

			const objectRelationshipAPIClient =
				await apiHelpers.buildRestClient(ObjectRelationshipAPI);

			const {body: objectRelationship} =
				await objectRelationshipAPIClient.postObjectDefinitionByExternalReferenceCodeObjectRelationship(
					'L_ACCOUNT',
					{
						label: {
							en_US: 'objectRelationshipLabel' + getRandomInt(),
						},
						name:
							'objectRelationshipName' +
							Math.floor(Math.random() * 99),
						objectDefinitionExternalReferenceCode1: 'L_ACCOUNT',
						objectDefinitionExternalReferenceCode2:
							objectDefinition1.externalReferenceCode,
						objectDefinitionId1: 32183,
						objectDefinitionId2: objectDefinition1.id,
						objectDefinitionName2: objectDefinition1.name,
						type: 'oneToMany',
					}
				);

			apiHelpers.data.push({
				id: objectRelationship.id,
				type: 'objectRelationship',
			});

			// Create an inheritance relationship between Custom Object 1 and Custom Object 2

			const {body: objectRelationshipInherited} =
				await objectRelationshipAPIClient.postObjectDefinitionByExternalReferenceCodeObjectRelationship(
					objectDefinition1.externalReferenceCode,
					{
						edge: true,
						label: {
							en_US: 'objectRelationshipLabel' + getRandomInt(),
						},
						name:
							'objectRelationshipName' +
							Math.floor(Math.random() * 99),
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

			objectRelationships.push(objectRelationshipInherited);

			apiHelpers.data.push({
				id: objectRelationshipInherited.id,
				type: 'objectRelationship',
			});

			// Enable the toggle related to account restricted in custom object 1

			await viewObjectDefinitionsPage.goto();

			await viewObjectDefinitionsPage.actionsButton.first().waitFor();

			await viewObjectDefinitionsPage.clickEditObjectDefinitionLink(
				objectDefinition1.name
			);

			await editObjectDetailsPage.enableAccountRestriction(
				objectRelationship.label['en_us']
			);

			await page.getByRole('button', {name: 'Save'}).click();

			const toastAlertContainer = page.locator(
				'#ToastAlertContainer .alert-success'
			);

			await expect(toastAlertContainer).toBeVisible();

			await viewObjectEntriesPage.goto(objectDefinition1.className);

			await viewObjectEntriesPage.clickAddObjectEntry(
				objectDefinition1.label['en_US']
			);

			await viewObjectEntriesPage.selectDropdownItemWithSearch(
				account1.name
			);
			await viewObjectEntriesPage.fillObjectEntry({
				objectFieldLabel: 'textField',
				objectFieldValue: 'a1',
			});

			await viewObjectEntriesPage.saveObjectEntryButton.click();

			await expect(toastAlertContainer).toBeVisible();

			await toastAlertContainer.getByLabel('Close').click();

			await page
				.getByRole('link', {name: objectDefinition2.name})
				.click();

			await viewObjectEntriesPage.addObjectEntryButton.click();

			await page.getByRole('textbox', {name: 'textField'}).fill('b1');

			await viewObjectEntriesPage.saveObjectEntryButton.click();

			await expect(toastAlertContainer).toBeVisible();

			await viewObjectEntriesPage.goto(objectDefinition1.className);

			await viewObjectEntriesPage.addObjectEntryButton.click();

			await viewObjectEntriesPage.selectDropdownItemWithSearch(
				account2.name
			);
			await viewObjectEntriesPage.fillObjectEntry({
				objectFieldLabel: 'textField',
				objectFieldValue: 'a2',
			});

			await viewObjectEntriesPage.saveObjectEntryButton.click();

			await expect(toastAlertContainer).toBeVisible();

			await toastAlertContainer.getByLabel('Close').click();

			await page
				.getByRole('link', {name: objectDefinition2.name})
				.click();

			await viewObjectEntriesPage.addObjectEntryButton.click();

			await page.getByRole('textbox', {name: 'textField'}).fill('b2');

			await viewObjectEntriesPage.saveObjectEntryButton.click();

			await expect(toastAlertContainer).toBeVisible();

			// Log in with user 1, check for entries related to account1

			userData[user1.alternateName] = {
				name: user1.givenName,
				password: 'test',
				surname: user1.familyName,
			};

			await performLogout(page);
			await performLogin(page, user1.alternateName);

			await viewObjectEntriesPage.goto(objectDefinition1.className);

			await expect(page.getByText(account1.name)).toBeVisible();
			await expect(page.getByText(account2.name)).not.toBeVisible();

			await page.getByLabel('View').click();

			await page
				.getByRole('link', {name: objectDefinition2.name})
				.click();

			await expect(page.getByText('b1')).toBeVisible();

			// Log in with user 2, check for entries related to account2

			userData[user2.alternateName] = {
				name: user2.givenName,
				password: 'test',
				surname: user2.familyName,
			};

			await performLogout(page);
			await performLogin(page, user2.alternateName);

			await viewObjectEntriesPage.goto(objectDefinition1.className);

			await expect(page.getByText(account2.name)).toBeVisible();
			await expect(page.getByText(account1.name)).not.toBeVisible();

			await page.getByLabel('View').click();

			await page
				.getByRole('link', {name: objectDefinition2.name})
				.click();

			await expect(page.getByText('b2')).toBeVisible();
		}
		finally {
			await performLogout(page);
			await performLogin(page, 'test');

			const objectRelationshipAPIClient =
				await apiHelpers.buildRestClient(ObjectRelationshipAPI);

			for (const objectRelationship of objectRelationships) {
				await objectRelationshipAPIClient.putObjectRelationship(
					objectRelationship.id,
					{
						...objectRelationship,
						edge: false,
					}
				);
			}
		}
	});
});

test.describe('Manage root models elements through Objects Admin', () => {
	test('cannot delete an object definition with inheritance enabled on its relationship', async ({
		apiHelpers,
		page,
		viewObjectDefinitionsPage,
	}) => {
		const objectRelationships: ObjectRelationship[] = [];

		try {
			const objectDefinition1 =
				await apiHelpers.objectAdmin.postRandomObjectDefinition({
					objectFolderExternalReferenceCode: 'default',
					status: {code: 0},
				});

			const objectDefinition2 =
				await apiHelpers.objectAdmin.postRandomObjectDefinition({
					objectFolderExternalReferenceCode: 'default',
					status: {code: 0},
				});

			apiHelpers.data.push({
				id: objectDefinition1.id,
				type: 'objectDefinition',
			});
			apiHelpers.data.push({
				id: objectDefinition2.id,
				type: 'objectDefinition',
			});

			const objectRelationshipLabel =
				'objectRelationshipLabel' + getRandomInt();
			const objectRelationshipName =
				'objectRelationshipName' + Math.floor(Math.random() * 99);

			const objectRelationshipAPIClient =
				await apiHelpers.buildRestClient(ObjectRelationshipAPI);

			const {body: objectRelationship} =
				await objectRelationshipAPIClient.postObjectDefinitionByExternalReferenceCodeObjectRelationship(
					objectDefinition1.externalReferenceCode,
					{
						edge: true,
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

			objectRelationships.push(objectRelationship);

			apiHelpers.data.push({
				id: objectRelationship.id,
				type: 'objectRelationship',
			});

			await viewObjectDefinitionsPage.goto();

			await viewObjectDefinitionsPage.clickObjectDefinitionActionButton(
				objectDefinition1.label['en_US']
			);

			await viewObjectDefinitionsPage.deleteObjectDefinitionOption.click();

			await expect(page.getByText('Deletion Not Allowed')).toBeVisible();
			await expect(
				page.getByText(
					'To delete this object, you must first disable inheritance and delete its relationships.'
				)
			).toBeVisible();

			await page.getByRole('button', {name: 'Done'}).click();
		}
		finally {
			const objectRelationshipAPIClient =
				await apiHelpers.buildRestClient(ObjectRelationshipAPI);

			for (const objectRelationship of objectRelationships) {
				await objectRelationshipAPIClient.putObjectRelationship(
					objectRelationship.id,
					{
						...objectRelationship,
						edge: false,
					}
				);
			}
		}
	});

	test('cannot delete an object relationship with inheritance enabled', async ({
		apiHelpers,
		objectRelationshipsPage,
		page,
	}) => {
		const objectRelationships: ObjectRelationship[] = [];

		try {
			const objectDefinition1 =
				await apiHelpers.objectAdmin.postRandomObjectDefinition({
					objectFolderExternalReferenceCode: 'default',
					status: {code: 0},
				});

			const objectDefinition2 =
				await apiHelpers.objectAdmin.postRandomObjectDefinition({
					objectFolderExternalReferenceCode: 'default',
					status: {code: 0},
				});

			apiHelpers.data.push({
				id: objectDefinition1.id,
				type: 'objectDefinition',
			});
			apiHelpers.data.push({
				id: objectDefinition2.id,
				type: 'objectDefinition',
			});

			const objectRelationshipLabel =
				'objectRelationshipLabel' + getRandomInt();
			const objectRelationshipName =
				'objectRelationshipName' + Math.floor(Math.random() * 99);

			const objectRelationshipAPIClient =
				await apiHelpers.buildRestClient(ObjectRelationshipAPI);

			const {body: objectRelationship} =
				await objectRelationshipAPIClient.postObjectDefinitionByExternalReferenceCodeObjectRelationship(
					objectDefinition1.externalReferenceCode,
					{
						edge: true,
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

			objectRelationships.push(objectRelationship);

			apiHelpers.data.push({
				id: objectRelationship.id,
				type: 'objectRelationship',
			});

			await objectRelationshipsPage.goto(
				objectDefinition1.label['en_US']
			);

			await objectRelationshipsPage.actionsButton.click();

			await objectRelationshipsPage.deleteObjectRelationshipOption.click();

			await expect(page.getByText('Deletion Not Allowed')).toBeVisible();
			await page
				.getByText(
					'You cannot delete a relationship with inheritance enabled. Disable inheritance before deleting the relationship.'
				)
				.click();

			await page.getByRole('button', {name: 'Done'}).click();
		}
		finally {
			const objectRelationshipAPIClient =
				await apiHelpers.buildRestClient(ObjectRelationshipAPI);

			for (const objectRelationship of objectRelationships) {
				await objectRelationshipAPIClient.putObjectRelationship(
					objectRelationship.id,
					{
						...objectRelationship,
						edge: false,
					}
				);
			}
		}
	});

	test('creating multiple inheritance relationships between the same objects should trigger a warning message', async ({
		apiHelpers,
		objectRelationshipsPage,
		page,
	}) => {
		const objectRelationships: ObjectRelationship[] = [];

		try {
			const objectDefinition1 =
				await apiHelpers.objectAdmin.postRandomObjectDefinition({
					objectFolderExternalReferenceCode: 'default',
					status: {code: 1},
				});
			const objectDefinition2 =
				await apiHelpers.objectAdmin.postRandomObjectDefinition({
					objectFolderExternalReferenceCode: 'default',
					status: {code: 1},
				});

			apiHelpers.data.push({
				id: objectDefinition1.id,
				type: 'objectDefinition',
			});

			apiHelpers.data.push({
				id: objectDefinition2.id,
				type: 'objectDefinition',
			});

			const objectRelationshipAPIClient =
				await apiHelpers.buildRestClient(ObjectRelationshipAPI);

			const objectRelationshipLabel1 =
				'objectRelationshipLabel' + getRandomInt();

			const objectRelationshipName1 =
				'objectRelationshipName' + Math.floor(Math.random() * 99);

			const {body: objectRelationship1} =
				await objectRelationshipAPIClient.postObjectDefinitionByExternalReferenceCodeObjectRelationship(
					objectDefinition1.externalReferenceCode,
					{
						edge: true,
						label: {
							en_US: objectRelationshipLabel1,
						},
						name: objectRelationshipName1,
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

			objectRelationships.push(objectRelationship1);

			apiHelpers.data.push({
				id: objectRelationship1.id,
				type: 'objectRelationship',
			});

			const objectRelationshipLabel2 =
				'objectRelationshipLabel' + getRandomInt();

			const objectRelationshipName2 =
				'objectRelationshipName' + Math.floor(Math.random() * 99);

			const {body: objectRelationship2} =
				await objectRelationshipAPIClient.postObjectDefinitionByExternalReferenceCodeObjectRelationship(
					objectDefinition1.externalReferenceCode,
					{
						edge: false,
						label: {
							en_US: objectRelationshipLabel2,
						},
						name: objectRelationshipName2,
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

			objectRelationships.push(objectRelationship2);

			apiHelpers.data.push({
				id: objectRelationship2.id,
				type: 'objectRelationship',
			});

			// fail enabling inheritance in object relationship 2

			await objectRelationshipsPage.goto(
				objectDefinition1.label['en_US']
			);

			await page
				.getByRole('link', {name: objectRelationshipLabel2})
				.click();

			await objectRelationshipsPage.inheritanceCheckbox.check();

			await objectRelationshipsPage.saveObjectRelationshipButton.click();

			await expect(
				objectRelationshipsPage.inheritanceWarningMessage
			).toBeVisible();

			await objectRelationshipsPage.cancelButton.click();

			// remove inheritance from object relationship 1

			await page
				.getByRole('link', {name: objectRelationshipLabel1})
				.click();

			await objectRelationshipsPage.inheritanceCheckbox.click();

			await expect(
				objectRelationshipsPage.inheritanceModalHeader
			).toBeVisible();

			await objectRelationshipsPage.inheritanceModalDisableButton.click();

			// enable inheritance in object relationship 2

			await page
				.getByRole('link', {name: objectRelationshipLabel2})
				.click();

			await objectRelationshipsPage.inheritanceCheckbox.check();

			await objectRelationshipsPage.saveObjectRelationshipButton.click();

			await page.waitForLoadState('load');

			await page
				.getByRole('link', {name: objectRelationshipLabel2})
				.click();

			await expect(
				objectRelationshipsPage.inheritanceCheckbox
			).toBeChecked();
		}
		finally {
			const objectRelationshipAPIClient =
				await apiHelpers.buildRestClient(ObjectRelationshipAPI);

			for (const objectRelationship of objectRelationships) {
				await objectRelationshipAPIClient.putObjectRelationship(
					objectRelationship.id,
					{
						...objectRelationship,
						edge: false,
						objectField: {
							...objectRelationship.objectField,
							required: true,
						},
					}
				);
			}
		}
	});

	test('can create relationship with inheritance enabled using the add relationship modal', async ({
		addNewObjectRelationshipModalPage,
		apiHelpers,
		objectRelationshipsPage,
		page,
	}) => {
		const {objectRelationshipFormPage} = addNewObjectRelationshipModalPage;
		const objectRelationships: ObjectRelationship[] = [];

		try {
			const objectFolder =
				await apiHelpers.objectAdmin.postRandomObjectFolder();

			apiHelpers.data.push({id: objectFolder.id, type: 'objectFolder'});

			const objectDefinition1 =
				await apiHelpers.objectAdmin.postRandomObjectDefinition({
					objectFolderExternalReferenceCode:
						objectFolder.externalReferenceCode,
					status: {code: 0},
				});
			const objectDefinition2 =
				await apiHelpers.objectAdmin.postRandomObjectDefinition({
					objectFolderExternalReferenceCode:
						objectFolder.externalReferenceCode,
					status: {code: 0},
				});

			const siteScopedObjectDefinition =
				await apiHelpers.objectAdmin.postRandomObjectDefinition({
					objectFolderExternalReferenceCode:
						objectFolder.externalReferenceCode,
					scope: 'site',
					status: {code: 0},
				});

			pushToApiHelpersData(
				apiHelpers,
				[
					objectDefinition1.id,
					objectDefinition2.id,
					siteScopedObjectDefinition.id,
				],
				'objectDefinition'
			);

			await objectRelationshipsPage.goto(
				objectDefinition1.name,
				objectFolder.label['en_US']
			);

			// Add object relationship trough modal.

			await objectRelationshipsPage.addObjectRelationshipButton.click();

			const objectRelationshipLabel = 'Relationship' + getRandomInt();

			await objectRelationshipFormPage.labelInput.fill(
				objectRelationshipLabel
			);

			await objectRelationshipFormPage.selectType('One to Many');

			await objectRelationshipFormPage.selectManyRecordsOf(
				siteScopedObjectDefinition.name
			);

			await objectRelationshipFormPage.inheritanceCheckbox.check();

			// Check if info alert is displayed in the modal when the inheritance is enabled.

			await expect(
				objectRelationshipFormPage.inheritanceInfo
			).toBeVisible();

			await objectRelationshipFormPage.saveButton.click();

			// Check if error alert is displayed in the modal.

			await expect(
				page.getByText(
					'To enable inheritance, the object definitions must have the same scope.'
				)
			).toBeVisible();

			await objectRelationshipFormPage.selectManyRecordsOf(
				objectDefinition2.name
			);

			await objectRelationshipFormPage.reverseOrderButton.click();

			const responsePromise = page.waitForResponse(
				`**/${objectDefinition2.externalReferenceCode}/object-relationships`
			);

			await objectRelationshipFormPage.saveButton.click();

			const response = await responsePromise;

			objectRelationships.push(await response.json());

			// Check if success toast is displayed after creating a relationship.

			await expect(
				page.getByText('Relationship was created successfully')
			).toBeVisible();

			const viewRelationshipLink = page.getByRole('link', {
				name: 'View Relationship',
			});

			// Check if success toast includes a link to the other side of the relationship.

			await expect(viewRelationshipLink).toBeVisible();

			await viewRelationshipLink.click();

			// Check if the link works and the relationship was really created with inheritance enabled;

			await expect(
				page.locator('h3', {hasText: objectDefinition2.name})
			).toBeVisible();

			const cellClassSufix = 'relationshipInheritance';

			await expect(
				page
					.locator(`.fds td.cell-${cellClassSufix}`)
					.or(page.locator(`.fds td.cell-${cellClassSufix}`))
			).toHaveText('Inherited');
		}
		finally {
			const objectRelationshipAPIClient =
				await apiHelpers.buildRestClient(ObjectRelationshipAPI);

			for (const objectRelationship of objectRelationships) {
				await objectRelationshipAPIClient.putObjectRelationship(
					objectRelationship.id,
					{
						...objectRelationship,
						edge: false,
					}
				);
			}
		}
	});
});

test.describe('Manage root models elements through Model Builder', () => {
	test('assert inherited relationship styles on nodes and edges', async ({
		apiHelpers,
		modelBuilderDiagramPage,
		modelBuilderRightSidebarPage,
		objectRelationshipsPage,
		page,
	}) => {
		const objectFolder =
			await apiHelpers.objectAdmin.postRandomObjectFolder();

		apiHelpers.data.push({id: objectFolder.id, type: 'objectFolder'});

		const objectDefinition1 =
			await apiHelpers.objectAdmin.postRandomObjectDefinition({
				objectFolderExternalReferenceCode:
					objectFolder.externalReferenceCode,
				status: {code: 0},
			});

		const objectDefinition2 =
			await apiHelpers.objectAdmin.postRandomObjectDefinition({
				objectFolderExternalReferenceCode:
					objectFolder.externalReferenceCode,
				status: {code: 0},
			});

		apiHelpers.data.push({
			id: objectDefinition1.id,
			type: 'objectDefinition',
		});
		apiHelpers.data.push({
			id: objectDefinition2.id,
			type: 'objectDefinition',
		});

		const objectRelationshipAPIClient = await apiHelpers.buildRestClient(
			ObjectRelationshipAPI
		);

		const {body: objectRelationship} =
			await objectRelationshipAPIClient.postObjectDefinitionByExternalReferenceCodeObjectRelationship(
				objectDefinition1.externalReferenceCode,
				{
					edge: true,
					label: {
						en_US: 'objectRelationshipLabel' + getRandomInt(),
					},
					name:
						'objectRelationshipName' +
						Math.floor(Math.random() * 99),
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

		apiHelpers.data.push({
			id: objectRelationship.id,
			type: 'objectRelationship',
		});

		await modelBuilderDiagramPage.goto({
			objectFolderName: objectFolder.name,
		});

		await modelBuilderDiagramPage.clickObjectRelationshipEdge(
			objectRelationship.label['en_US']
		);

		await expect(
			modelBuilderRightSidebarPage.inheritanceCheckbox
		).toBeChecked();

		await expect(
			page
				.getByRole('button', {name: objectRelationship.label['en_US']})
				.getByRole('presentation')
		).toBeVisible();

		await expect(
			modelBuilderDiagramPage.objectDefinitionNodes
				.filter({
					hasText: objectDefinition1.label['en_US'],
				})
				.locator('.lfr-objects__model-builder-node-container')
		).toHaveClass(/treeItem/);

		await modelBuilderRightSidebarPage.inheritanceCheckbox.waitFor();

		await modelBuilderRightSidebarPage.inheritanceCheckbox.click();

		await expect(
			objectRelationshipsPage.inheritanceModalHeader
		).toBeVisible();

		await objectRelationshipsPage.inheritanceModalDisableButton.click();

		await modelBuilderRightSidebarPage.inheritanceCheckbox.waitFor();

		await expect(
			modelBuilderRightSidebarPage.inheritanceCheckbox
		).not.toBeChecked();

		await expect(
			page
				.getByRole('button', {name: objectRelationship.label['en_US']})
				.getByRole('presentation')
		).not.toBeVisible();

		await expect(
			modelBuilderDiagramPage.objectDefinitionNodes
				.filter({
					hasText: objectDefinition2.label['en_US'],
				})
				.locator('.lfr-objects__model-builder-node-container')
		).not.toHaveClass(/treeItem/);
	});

	test('can create relationship with inheritance enabled using the add relationship modal', async ({
		addNewObjectRelationshipModalPage,
		apiHelpers,
		modelBuilderDiagramPage,
		modelBuilderRightSidebarPage,
		page,
	}) => {
		const objectFolder =
			await apiHelpers.objectAdmin.postRandomObjectFolder();

		apiHelpers.data.push({id: objectFolder.id, type: 'objectFolder'});

		const objectRelationships: ObjectRelationship[] = [];

		try {
			const objectDefinition1 =
				await apiHelpers.objectAdmin.postRandomObjectDefinition({
					objectFolderExternalReferenceCode:
						objectFolder.externalReferenceCode,
					status: {code: 0},
				});

			const objectDefinition2 =
				await apiHelpers.objectAdmin.postRandomObjectDefinition({
					objectFolderExternalReferenceCode:
						objectFolder.externalReferenceCode,
					status: {code: 0},
				});

			apiHelpers.data.push({
				id: objectDefinition1.id,
				type: 'objectDefinition',
			});
			apiHelpers.data.push({
				id: objectDefinition2.id,
				type: 'objectDefinition',
			});

			await modelBuilderDiagramPage.goto({
				objectFolderName: objectFolder.name,
			});

			await modelBuilderDiagramPage.connectObjectDefinitionsNodeHandles(
				objectDefinition1.id,
				objectDefinition2.id
			);

			const objectRelationship =
				await addNewObjectRelationshipModalPage.handleForm({
					inherited: true,
					objectRelationshipLabel:
						'objectRelationship' + getRandomInt(),
					type: 'One to Many',
				});

			objectRelationships.push(objectRelationship);

			apiHelpers.data.push({
				id: objectRelationship.id,
				type: 'objectRelationship',
			});

			await expect(
				page.locator('#ToastAlertContainer .alert-success')
			).toBeVisible();

			await expect(
				modelBuilderRightSidebarPage.inheritanceCheckbox
			).toBeChecked();
		}
		finally {
			const objectRelationshipAPIClient =
				await apiHelpers.buildRestClient(ObjectRelationshipAPI);

			for (const objectRelationship of objectRelationships) {
				await objectRelationshipAPIClient.putObjectRelationship(
					objectRelationship.id,
					{
						...objectRelationship,
						edge: false,
					}
				);
			}
		}
	});

	test('cannot delete an object relationship with inheritance', async ({
		apiHelpers,
		modelBuilderDiagramPage,
		page,
	}) => {
		const objectRelationships: ObjectRelationship[] = [];

		try {
			const objectDefinition1 =
				await apiHelpers.objectAdmin.postRandomObjectDefinition({
					objectFolderExternalReferenceCode: 'default',
					status: {code: 2},
				});

			const objectDefinition2 =
				await apiHelpers.objectAdmin.postRandomObjectDefinition({
					objectFolderExternalReferenceCode: 'default',
					status: {code: 2},
				});

			apiHelpers.data.push({
				id: objectDefinition1.id,
				type: 'objectDefinition',
			});
			apiHelpers.data.push({
				id: objectDefinition2.id,
				type: 'objectDefinition',
			});

			const objectRelationshipLabel =
				'objectRelationshipLabel' + getRandomInt();
			const objectRelationshipName =
				'objectRelationshipName' + Math.floor(Math.random() * 99);

			const objectRelationshipAPIClient =
				await apiHelpers.buildRestClient(ObjectRelationshipAPI);

			const {body: objectRelationship} =
				await objectRelationshipAPIClient.postObjectDefinitionByExternalReferenceCodeObjectRelationship(
					objectDefinition1.externalReferenceCode,
					{
						edge: true,
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

			objectRelationships.push(objectRelationship);

			apiHelpers.data.push({
				id: objectRelationship.id,
				type: 'objectRelationship',
			});

			await modelBuilderDiagramPage.goto({objectFolderName: 'Default'});

			await modelBuilderDiagramPage.toggleSidebarsButton.click();

			await modelBuilderDiagramPage.openObjectDefinitionMenu(
				objectDefinition2.label['en_US']
			);

			await page
				.getByRole('menuitem')
				.filter({hasText: 'Delete Object'})
				.click();

			await expect(
				page.getByRole('dialog').locator('.modal-body span')
			).toHaveText(
				'To delete this object, you must first disable inheritance and delete its relationships.'
			);

			await page.getByRole('button', {name: 'Done'}).click();

			await expect(
				page.getByText(
					'To delete this object, you must first disable inheritance and delete its relationships.'
				)
			).not.toBeVisible();
		}
		finally {
			const objectRelationshipAPIClient =
				await apiHelpers.buildRestClient(ObjectRelationshipAPI);

			for (const objectRelationship of objectRelationships) {
				await objectRelationshipAPIClient.putObjectRelationship(
					objectRelationship.id,
					{
						...objectRelationship,
						edge: false,
					}
				);
			}
		}
	});
});
