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
import {ListTypeDefinitionsPage} from '../../../pages/object-web/list-type/ListTypeDefinitionsPage';
import {getRandomInt} from '../../../utils/getRandomInt';
import getRandomString from '../../../utils/getRandomString';
import {performUserSwitch, userData} from '../../../utils/performLogin';
import {waitForAlert} from '../../../utils/waitForAlert';
import {generateObjectFields} from '../utils/generateObjectFields';

const test = mergeTests(
	dataApiHelpersTest,
	isolatedSiteTest,
	loginTest(),
	objectPagesTest
);

const CHILD_ENTRY_TITLE = 'Child Entry';

async function createUserWithPermissions(apiHelpers, rolePermissions) {
	const user = await apiHelpers.headlessAdminUser.postUserAccount();

	userData[user.alternateName] = {
		name: user.givenName,
		password: 'test',
		surname: user.familyName,
	};

	const company =
		await apiHelpers.jsonWebServicesCompany.getCompanyByWebId(
			'liferay.com'
		);

	const role = await apiHelpers.headlessAdminUser.postRole({
		name: 'role' + getRandomInt(),
		rolePermissions: rolePermissions.map((rolePermission) => ({
			...rolePermission,
			primaryKey: String(company.companyId),
			scope: 1,
		})),
	});

	await apiHelpers.headlessAdminUser.assignUserToRole(
		role.externalReferenceCode,
		user.id
	);

	return user;
}

async function grantPermissionsToUserRole(page, checked: boolean) {
	const permissionIframe = page.frameLocator('iframe[title="Permissions"]');

	const permissionsCheckbox = permissionIframe.locator(
		'#user_ACTION_PERMISSIONS'
	);

	await page.waitForTimeout(500);

	await permissionsCheckbox.check({trial: true});

	if (checked) {
		await permissionsCheckbox.check();
	}
	else {
		await permissionsCheckbox.uncheck();
	}

	await permissionIframe.getByRole('button', {name: 'Save'}).click();

	await waitForAlert(permissionIframe);

	await page.locator('.modal').getByLabel('Close', {exact: true}).click();
}

test(
	'Can manage related object entries without control panel access to the related object definition',
	{tag: '@LPD-102111'},
	async ({
		apiHelpers,
		editObjectDetailsPage,
		objectLayoutsPage,
		page,
		viewObjectEntriesPage,
	}) => {
		const {
			childObjectDefinition,
			objectRelationship,
			parentObjectDefinition,
		} = await test.step('Relate two object definitions', async () => {
			const parentObjectDefinition =
				await apiHelpers.objectAdmin.postRandomObjectDefinition({
					objectFields: generateObjectFields({
						objectFieldBusinessTypes: [
							{
								businessType: 'Text',
								label: {en_US: 'parentField'},
								name: 'parentField',
							},
						],
					}),
					status: {code: 0},
					titleObjectFieldName: 'parentField',
				});

			apiHelpers.data.push({
				id: parentObjectDefinition.id,
				type: 'objectDefinition',
			});

			const childObjectDefinition =
				await apiHelpers.objectAdmin.postRandomObjectDefinition({
					objectFields: generateObjectFields({
						objectFieldBusinessTypes: [
							{
								businessType: 'Text',
								label: {en_US: 'childField'},
								name: 'childField',
							},
						],
					}),
					status: {code: 0},
					titleObjectFieldName: 'childField',
				});

			apiHelpers.data.push({
				id: childObjectDefinition.id,
				type: 'objectDefinition',
			});

			const objectRelationshipAPIClient =
				await apiHelpers.buildRestClient(ObjectRelationshipAPI);

			const {body: objectRelationship} =
				await objectRelationshipAPIClient.postObjectDefinitionByExternalReferenceCodeObjectRelationship(
					parentObjectDefinition.externalReferenceCode,
					{
						deletionType: 'disassociate',
						label: {en_US: 'Relationship' + getRandomInt()},
						name: 'relationship' + getRandomInt(),
						objectDefinitionExternalReferenceCode1:
							parentObjectDefinition.externalReferenceCode,
						objectDefinitionExternalReferenceCode2:
							childObjectDefinition.externalReferenceCode,
						objectDefinitionId1: parentObjectDefinition.id,
						objectDefinitionId2: childObjectDefinition.id,
						type: 'oneToMany',
					}
				);

			apiHelpers.data.push({
				id: objectRelationship.id,
				type: 'objectRelationship',
			});

			return {
				childObjectDefinition,
				objectRelationship,
				parentObjectDefinition,
			};
		});

		const parentObjectEntry =
			await test.step('Relate one entry of each object definition', async () => {
				const parentApplicationName =
					'c/' + parentObjectDefinition.name.toLowerCase() + 's';

				const parentObjectEntry =
					await apiHelpers.objectEntry.postObjectEntry(
						{parentField: getRandomString()},
						parentApplicationName
					);

				const childObjectEntry =
					await apiHelpers.objectEntry.postObjectEntry(
						{childField: CHILD_ENTRY_TITLE},
						'c/' + childObjectDefinition.name.toLowerCase() + 's'
					);

				await apiHelpers.objectEntry.putByExternalReferenceCodeCurrentExternalReferenceCodeObjectRelationshipNameRelatedExternalReferenceCode(
					{
						applicationName: parentApplicationName,
						currentExternalReferenceCode:
							parentObjectEntry.externalReferenceCode,
						objectRelationshipName: objectRelationship.name,
						relatedExternalReferenceCode:
							childObjectEntry.externalReferenceCode,
					}
				);

				return parentObjectEntry;
			});

		await test.step('Add a relationship tab to the parent default layout', async () => {
			const objectLayoutName = 'Layout' + getRandomInt();

			await objectLayoutsPage.goto(parentObjectDefinition.name);

			await objectLayoutsPage.createObjectLayout(objectLayoutName);

			await page.getByRole('link', {name: objectLayoutName}).click();

			await objectLayoutsPage.markAsDefaultButton.check();

			await objectLayoutsPage.createObjectLayoutContent({
				objectFieldNames: ['parentField'],
				objectLayoutName,
				objectLayoutRegularBlockName: 'Block 1',
				objectLayoutTabName: 'Field Tab',
			});

			const {reload} =
				await objectLayoutsPage.createObjectRelationshipTab(
					objectLayoutName,
					'Relationship Tab',
					objectRelationship.label['en_US']
				);

			await reload;

			await editObjectDetailsPage.goto(parentObjectDefinition.name);

			const {reload: detailsReload} =
				await editObjectDetailsPage.saveObjectDefinitionReturningReload();

			await waitForAlert(
				page,
				'Success:The object was saved successfully.'
			);

			await detailsReload;
		});

		const gotoRelationshipTab = async () => {
			await viewObjectEntriesPage.goto(parentObjectDefinition.className);

			await page
				.getByRole('link', {name: String(parentObjectEntry.id)})
				.click();

			await page.getByRole('link', {name: 'Relationship Tab'}).click();
		};

		await test.step('Switch to a user without control panel access to the related object definition', async () => {
			const user = await createUserWithPermissions(apiHelpers, [
				{actionIds: ['VIEW_CONTROL_PANEL'], resourceName: '90'},
				{
					actionIds: ['ACCESS_IN_CONTROL_PANEL'],
					resourceName: `com_liferay_object_web_internal_object_definitions_portlet_ObjectDefinitionsPortlet_${parentObjectDefinition.className.split('#')[1]}`,
				},
				{
					actionIds: ['ADD_OBJECT_ENTRY'],
					resourceName: `com.liferay.object#${parentObjectDefinition.id}`,
				},
				{
					actionIds: ['UPDATE', 'VIEW'],
					resourceName: parentObjectDefinition.className,
				},
				{
					actionIds: ['ADD_OBJECT_ENTRY'],
					resourceName: `com.liferay.object#${childObjectDefinition.id}`,
				},
				{
					actionIds: ['UPDATE', 'VIEW'],
					resourceName: childObjectDefinition.className,
				},
			]);

			await performUserSwitch(page, user.alternateName);

			await gotoRelationshipTab();

			await expect(
				page.getByRole('cell', {name: CHILD_ENTRY_TITLE})
			).toBeVisible();
		});

		await test.step('Create a related object entry', async () => {
			await page.getByRole('button', {name: 'New'}).first().click();

			await page.getByRole('menuitem', {name: 'Create New'}).click();

			await expect(
				viewObjectEntriesPage.noPermissionMessage
			).toBeHidden();

			await expect(page.getByLabel('childField')).toBeVisible();
		});

		await test.step('Edit a related object entry', async () => {
			await gotoRelationshipTab();

			await viewObjectEntriesPage.frontendDatasetActions.first().click();

			await viewObjectEntriesPage.frontendDatasetViewAction.click();

			await expect(
				viewObjectEntriesPage.noPermissionMessage
			).toBeHidden();

			await expect(page.getByLabel('childField')).toHaveValue(
				CHILD_ENTRY_TITLE
			);
		});

		await test.step('Delete a related object entry', async () => {
			await gotoRelationshipTab();

			await viewObjectEntriesPage.frontendDatasetActions.first().click();

			await viewObjectEntriesPage.frontendDatasetDeleteAction.click();

			await expect(
				viewObjectEntriesPage.noPermissionMessage
			).toBeHidden();

			await expect(
				page.getByRole('cell', {name: CHILD_ENTRY_TITLE})
			).toBeHidden();
		});
	}
);

test('Can only update Object Definition permissions when PERMISSIONS permission is granted', async ({
	apiHelpers,
	page,
	viewObjectDefinitionsPage,
}) => {
	const objectDefinition =
		await apiHelpers.objectAdmin.postRandomObjectDefinition({
			status: {code: 0},
		});

	apiHelpers.data.push({
		id: objectDefinition.id,
		type: 'objectDefinition',
	});

	await test.step('As admin, grant PERMISSIONS on the object definition to the User role', async () => {
		await viewObjectDefinitionsPage.goto();

		await viewObjectDefinitionsPage.clickObjectDefinitionActionButton(
			objectDefinition.label['en_US']
		);

		await page.getByRole('menuitem', {name: 'Permissions'}).click();

		await grantPermissionsToUserRole(page, true);
	});

	await test.step('Switch to limited user and verify Permissions is available', async () => {
		const user = await createUserWithPermissions(apiHelpers, [
			{
				actionIds: ['ACCESS_IN_CONTROL_PANEL'],
				resourceName:
					'com_liferay_object_web_internal_object_definitions_portlet_ObjectDefinitionsPortlet',
			},
			{
				actionIds: ['VIEW'],
				resourceName: 'com.liferay.object.model.ObjectDefinition',
			},
		]);

		await performUserSwitch(page, user.alternateName);

		await viewObjectDefinitionsPage.goto();

		await viewObjectDefinitionsPage.clickObjectDefinitionActionButton(
			objectDefinition.label['en_US']
		);

		await page.getByRole('menuitem', {name: 'Permissions'}).click();
	});

	await test.step('Remove PERMISSIONS from User role', async () => {
		await grantPermissionsToUserRole(page, false);
	});

	await test.step('Verify Permissions menu item is no longer available', async () => {
		await page.reload();

		await viewObjectDefinitionsPage.clickObjectDefinitionActionButton(
			objectDefinition.label['en_US']
		);

		await expect(
			page.getByRole('menuitem', {name: 'Permissions'})
		).toBeHidden();
	});
});

test('Can only update Object Entry permissions when PERMISSIONS permission is granted', async ({
	apiHelpers,
	page,
	viewObjectEntriesPage,
}) => {
	const objectDefinition =
		await apiHelpers.objectAdmin.postRandomObjectDefinition({
			status: {code: 0},
		});

	apiHelpers.data.push({
		id: objectDefinition.id,
		type: 'objectDefinition',
	});

	await apiHelpers.objectEntry.postObjectEntry(
		{},
		'c/' + objectDefinition.name.toLowerCase() + 's'
	);

	await test.step('As admin, grant PERMISSIONS on the object entry to the User role', async () => {
		await viewObjectEntriesPage.goto(objectDefinition.className);

		await viewObjectEntriesPage.frontendDatasetActions.click();

		await viewObjectEntriesPage.frontendDatasetPermissionsAction.click();

		await grantPermissionsToUserRole(page, true);
	});

	await test.step('Switch to limited user and verify Permissions is available', async () => {
		const user = await createUserWithPermissions(apiHelpers, [
			{
				actionIds: ['ACCESS_IN_CONTROL_PANEL'],
				resourceName: `com_liferay_object_web_internal_object_definitions_portlet_ObjectDefinitionsPortlet_${objectDefinition.className.split('#')[1]}`,
			},
			{
				actionIds: ['VIEW'],
				resourceName: objectDefinition.className,
			},
		]);

		await performUserSwitch(page, user.alternateName);

		await viewObjectEntriesPage.goto(objectDefinition.className);

		await viewObjectEntriesPage.frontendDatasetActions.click();

		await viewObjectEntriesPage.frontendDatasetPermissionsAction.click();
	});

	await test.step('Remove PERMISSIONS from User role', async () => {
		await grantPermissionsToUserRole(page, false);
	});

	await test.step('Verify actions dropdown is no longer available', async () => {
		await page.reload();

		await expect(viewObjectEntriesPage.frontendDatasetActions).toBeHidden();
	});
});

test('Can only update Picklist permissions when PERMISSIONS permission is granted', async ({
	apiHelpers,
	page,
}) => {
	const picklist =
		await apiHelpers.listTypeAdmin.postRandomListTypeDefinition();

	apiHelpers.data.push({
		id: picklist.id,
		type: 'listTypeDefinition',
	});

	const listTypeDefinitionsPage = new ListTypeDefinitionsPage(page);

	await test.step('As admin, grant PERMISSIONS on the picklist to the User role', async () => {
		await listTypeDefinitionsPage.goto();

		await page
			.getByRole('row', {name: picklist.name})
			.getByRole('button')
			.click();

		await page.getByRole('menuitem', {name: 'Permissions'}).click();

		await grantPermissionsToUserRole(page, true);
	});

	await test.step('Switch to limited user and verify Permissions is available', async () => {
		const user = await createUserWithPermissions(apiHelpers, [
			{
				actionIds: ['ACCESS_IN_CONTROL_PANEL'],
				resourceName:
					'com_liferay_object_web_internal_list_type_portlet_portlet_ListTypeDefinitionsPortlet',
			},
			{
				actionIds: ['VIEW'],
				resourceName: 'com.liferay.list.type.model.ListTypeDefinition',
			},
		]);

		await performUserSwitch(page, user.alternateName);

		await listTypeDefinitionsPage.goto();

		await page
			.getByRole('row', {name: picklist.name})
			.getByRole('button')
			.click();

		await page.getByRole('menuitem', {name: 'Permissions'}).click();
	});

	await test.step('Remove PERMISSIONS from User role', async () => {
		await grantPermissionsToUserRole(page, false);
	});

	await test.step('Verify Permissions menu item is no longer available', async () => {
		await page.reload();

		await page
			.getByRole('row', {name: picklist.name})
			.getByRole('button')
			.click();

		await expect(
			page.getByRole('menuitem', {name: 'Permissions'})
		).toBeHidden();
	});
});

test('Can restrict site-scoped object portlet to the site where the role permission is granted', async ({
	apiHelpers,
	page,
	viewObjectEntriesPage,
}) => {
	const objectDefinition =
		await apiHelpers.objectAdmin.postRandomObjectDefinition({
			panelCategoryKey: 'site_administration.content',
			scope: 'site',
			status: {code: 0},
		});

	apiHelpers.data.push({
		id: objectDefinition.id,
		type: 'objectDefinition',
	});

	const company =
		await apiHelpers.jsonWebServicesCompany.getCompanyByWebId(
			'liferay.com'
		);

	const siteA = await apiHelpers.headlessAdminSite.postSite({
		name: 'Site A ' + getRandomInt(),
	});

	const siteB = await apiHelpers.headlessAdminSite.postSite({
		name: 'Site B ' + getRandomInt(),
	});

	const role = await apiHelpers.headlessAdminUser.postRole({
		name: 'role' + getRandomInt(),
		rolePermissions: [
			{
				actionIds: ['ACCESS_IN_CONTROL_PANEL', 'VIEW'],
				primaryKey: siteA.id,
				resourceName: `com_liferay_object_web_internal_object_definitions_portlet_ObjectDefinitionsPortlet_${objectDefinition.className.split('#')[1]}`,
				scope: 2,
			},
			{
				actionIds: ['VIEW', 'VIEW_SITE_ADMINISTRATION'],
				primaryKey: company.companyId,
				resourceName: 'com.liferay.portal.kernel.model.Group',
				scope: 1,
			},
			{
				actionIds: ['VIEW_SITE_ADMINISTRATION'],
				primaryKey: company.companyId,
				resourceName: 'com.liferay.depot.model.DepotEntry',
				scope: 1,
			},
			{
				actionIds: ['VIEW_SITE_ADMINISTRATION'],
				primaryKey: siteA.id,
				resourceName: 'com.liferay.portal.kernel.model.Group',
				scope: 2,
			},
		],
	});

	const user = await apiHelpers.headlessAdminUser.postUserAccount();

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

	await test.step('custom object portlet is reachable by site A', async () => {
		await viewObjectEntriesPage.goto(
			objectDefinition.className,
			undefined,
			siteA.friendlyUrlPath
		);

		await expect(
			page.getByRole('heading', {
				name: objectDefinition.label['en_US'],
			})
		).toBeVisible();
	});

	await test.step('custom object portlet is not reachable by site B', async () => {
		await viewObjectEntriesPage.goto(
			objectDefinition.className,
			undefined,
			siteB.friendlyUrlPath
		);

		await expect(viewObjectEntriesPage.noPermissionMessage).toBeVisible();
	});
});
