/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {
	ObjectDefinition,
	ObjectDefinitionAPI,
	ObjectRelationshipAPI,
} from '@liferay/object-admin-rest-client-js';
import {expect, mergeTests} from '@playwright/test';

import {dataApiHelpersTest} from '../../../fixtures/dataApiHelpersTest';
import {loginTest} from '../../../fixtures/loginTest';
import {rolesPagesTest} from '../../../fixtures/rolesPagesTest';
import {getRandomInt} from '../../../utils/getRandomInt';
import {
	createInheritanceRelationship,
	setAllowStandaloneObjectEntry,
} from './utils/objectInheritance';

export const test = mergeTests(dataApiHelpersTest, loginTest(), rolesPagesTest);

const generateRandomObjectDefinition = ({
	objectDefinitionName,
	objectRelationships = [],
	panelCategoryKey,
	portlet = true,
	rootObjectDefinitionExternalReferenceCode,
	statusCode = 0,
}: {
	objectDefinitionName: string;
	objectRelationships?: any[];
	panelCategoryKey?: string;
	portlet?: boolean;
	rootObjectDefinitionExternalReferenceCode?: string;
	statusCode?: number;
}): ObjectDefinition => {
	return {
		active: true,
		externalReferenceCode: objectDefinitionName,
		label: {
			en_US: objectDefinitionName,
		},
		name: objectDefinitionName,
		objectFields: [
			{
				DBType: 'String',
				businessType: 'Text',
				externalReferenceCode: 'textField',
				indexed: true,
				indexedAsKeyword: false,
				indexedLanguageId: '',
				label: {en_US: 'textField'},
				listTypeDefinitionId: 0,
				name: 'textField',
				required: false,
				system: false,
				type: 'String',
			},
		],
		objectRelationships,
		panelCategoryKey,
		pluralLabel: {
			en_US: objectDefinitionName,
		},
		portlet,
		rootObjectDefinitionExternalReferenceCode,
		scope: 'company',
		status: {
			code: statusCode,
		},
	};
};

test(
	'Show object in role permissions page',
	{tag: ['@LPD-26733']},
	async ({apiHelpers, roleDefinePermissionsPage, rolePage, rolesPage}) => {
		test.setTimeout(120000);

		const objectDefinitionAPIClient =
			await apiHelpers.buildRestClient(ObjectDefinitionAPI);

		const {body: objectDefinition1} =
			await objectDefinitionAPIClient.postObjectDefinition(
				generateRandomObjectDefinition({
					objectDefinitionName: `ObjectDefinition${getRandomInt()}`,
				})
			);

		apiHelpers.data.push({
			id: objectDefinition1.id,
			type: 'objectDefinition',
		});

		const {body: objectDefinition2} =
			await objectDefinitionAPIClient.postObjectDefinition(
				generateRandomObjectDefinition({
					objectDefinitionName: `ObjectDefinition${getRandomInt()}`,
					portlet: false,
				})
			);

		apiHelpers.data.push({
			id: objectDefinition2.id,
			type: 'objectDefinition',
		});

		const {body: objectDefinition3} =
			await objectDefinitionAPIClient.postObjectDefinition(
				generateRandomObjectDefinition({
					objectDefinitionName: `ObjectDefinition${getRandomInt()}`,
					panelCategoryKey: 'control_panel.users',
				})
			);

		apiHelpers.data.push({
			id: objectDefinition3.id,
			type: 'objectDefinition',
		});

		const {body: objectDefinition4} =
			await objectDefinitionAPIClient.postObjectDefinition(
				generateRandomObjectDefinition({
					objectDefinitionName: `ObjectDefinition${getRandomInt()}`,
					panelCategoryKey: 'control_panel.users',
					portlet: false,
				})
			);

		apiHelpers.data.push({
			id: objectDefinition4.id,
			type: 'objectDefinition',
		});

		await rolesPage.goto();

		await rolesPage.userLink.click();
		await rolePage.definePermissionsLink.click();
		await roleDefinePermissionsPage.searchInput.click();
		await roleDefinePermissionsPage.searchInput.fill('object');

		await expect(
			roleDefinePermissionsPage.menuItem('Objects')
		).toBeVisible();
		await expect(
			roleDefinePermissionsPage.menuItemByTestId(
				`object_${objectDefinition1.id}`
			)
		).toBeVisible();
		await expect(
			roleDefinePermissionsPage.menuItemByTestId(
				`object_${objectDefinition1.id}`
			)
		).toHaveText(objectDefinition1.name);
		await expect(
			roleDefinePermissionsPage.menuItemByTestId(
				`object_${objectDefinition2.id}`
			)
		).toBeVisible();
		await expect(
			roleDefinePermissionsPage.menuItemByTestId(
				`object_${objectDefinition2.id}`
			)
		).toHaveText(objectDefinition2.name);
		await expect(
			roleDefinePermissionsPage.menuItemByTestId(
				`object_${objectDefinition3.id}`
			)
		).toHaveCount(0);
		await expect(
			roleDefinePermissionsPage.menuItemByTestId(
				`object_${objectDefinition4.id}`
			)
		).toHaveCount(0);

		await roleDefinePermissionsPage
			.menuItemByTestId(`object_${objectDefinition1.id}`)
			.click();

		await expect(roleDefinePermissionsPage.loading).toHaveCount(0);
		await expect(roleDefinePermissionsPage.portletResourceLabel).toHaveText(
			objectDefinition1.name
		);
		await expect(
			roleDefinePermissionsPage.accessInControlPanel
		).toHaveCount(0);
		await expect(roleDefinePermissionsPage.addToPage).toBeVisible();

		await roleDefinePermissionsPage
			.menuItemByTestId(`object_${objectDefinition2.id}`)
			.click();

		await expect(roleDefinePermissionsPage.loading).toHaveCount(0);
		await expect(roleDefinePermissionsPage.portletResourceLabel).toHaveText(
			objectDefinition2.name
		);
		await expect(
			roleDefinePermissionsPage.accessInControlPanel
		).toHaveCount(0);
		await expect(roleDefinePermissionsPage.addToPage).toHaveCount(0);
	}
);

test(
	'Show or hide inactive permissions banner based on standalone entries setting',
	{tag: ['@LPD-88002']},
	async ({
		apiHelpers,
		page,
		roleDefinePermissionsPage,
		rolePage,
		rolesPage,
	}) => {
		const parentObjectDefinition =
			await apiHelpers.objectAdmin.postRandomObjectDefinition({
				status: {code: 0},
			});

		const standaloneDisabledChild =
			await apiHelpers.objectAdmin.postRandomObjectDefinition({
				status: {code: 0},
			});

		const standaloneEnabledChild =
			await apiHelpers.objectAdmin.postRandomObjectDefinition({
				status: {code: 0},
			});

		// The push order matters: cleanup iterates apiHelpers.data.reverse(),
		// so the parent must be cleaned up first to disable edge=true
		// relationships and cascade-delete them before the children are
		// deleted.

		apiHelpers.data.push({
			id: standaloneDisabledChild.id,
			type: 'objectDefinition',
		});
		apiHelpers.data.push({
			id: standaloneEnabledChild.id,
			type: 'objectDefinition',
		});
		apiHelpers.data.push({
			id: parentObjectDefinition.id,
			type: 'objectDefinition',
		});

		const objectRelationshipAPIClient = await apiHelpers.buildRestClient(
			ObjectRelationshipAPI
		);

		await createInheritanceRelationship(
			objectRelationshipAPIClient,
			parentObjectDefinition,
			standaloneDisabledChild,
			'inheritedDisabled'
		);
		await createInheritanceRelationship(
			objectRelationshipAPIClient,
			parentObjectDefinition,
			standaloneEnabledChild,
			'inheritedEnabled'
		);

		const objectDefinitionAPIClient =
			await apiHelpers.buildRestClient(ObjectDefinitionAPI);

		await setAllowStandaloneObjectEntry(
			objectDefinitionAPIClient,
			standaloneDisabledChild,
			'false'
		);
		await setAllowStandaloneObjectEntry(
			objectDefinitionAPIClient,
			standaloneEnabledChild,
			'true'
		);

		const banner = page.getByText(
			"Standalone entries are disabled. Some of these permissions are inactive and will take effect when standalone entries are enabled in the object's settings."
		);

		await rolesPage.goto();

		await rolesPage.userLink.click();

		await rolePage.definePermissionsLink.click();

		await roleDefinePermissionsPage.searchInput.click();

		await roleDefinePermissionsPage.searchInput.fill('object');

		await test.step('Banner is visible for child with standalone entries disabled', async () => {
			await roleDefinePermissionsPage
				.menuItemByTestId(`object_${standaloneDisabledChild.id}`)
				.click();

			await expect(roleDefinePermissionsPage.loading).toHaveCount(0);
			await expect(
				roleDefinePermissionsPage.portletResourceLabel
			).toHaveText(standaloneDisabledChild.name);
			await expect(banner).toBeVisible();
		});

		await test.step('Banner is hidden for child with standalone entries enabled', async () => {
			await roleDefinePermissionsPage
				.menuItemByTestId(`object_${standaloneEnabledChild.id}`)
				.click();

			await expect(roleDefinePermissionsPage.loading).toHaveCount(0);
			await expect(
				roleDefinePermissionsPage.portletResourceLabel
			).toHaveText(standaloneEnabledChild.name);
			await expect(banner).toBeHidden();
		});

		await test.step('Banner is hidden for root object', async () => {
			await roleDefinePermissionsPage
				.menuItemByTestId(`object_${parentObjectDefinition.id}`)
				.click();

			await expect(roleDefinePermissionsPage.loading).toHaveCount(0);
			await expect(
				roleDefinePermissionsPage.portletResourceLabel
			).toHaveText(parentObjectDefinition.name);
			await expect(banner).toBeHidden();
		});
	}
);
