/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {dataApiHelpersTest} from '../../../fixtures/dataApiHelpersTest';
import {featureFlagsTest} from '../../../fixtures/featureFlagsTest';
import {loginTest} from '../../../fixtures/loginTest';
import {rolesPagesTest} from '../../../fixtures/rolesPagesTest';
import {getRandomInt} from '../../../utils/getRandomInt';
import {cmsPagesTest} from './fixtures/cmsPagesTest';
import {
	clickMenuItem,
	createSpace,
	deleteSpace,
	goToAllSpaces,
} from './utils/permissions';

const test = mergeTests(
	cmsPagesTest,
	dataApiHelpersTest,
	featureFlagsTest({
		'LPD-11235': {enabled: false},
		'LPD-17564': {enabled: true},
		'LPD-58677': {enabled: true},
		'LPD-96750': {enabled: true},
	}),
	loginTest(),
	rolesPagesTest
);

test(
	'Individual Permissions modal lists only roles matching the depot domain',
	{tag: ['@LPD-83689']},
	async ({apiHelpers, page, permissionsPage, spaceSummaryPage}) => {
		test.setTimeout(90000);

		const role1 = await apiHelpers.headlessAdminUser.postRole({
			name: 'RoleNoSubtype' + getRandomInt(),
			roleType: 'depot',
		});
		const role2 = await apiHelpers.headlessAdminUser.postRole({
			name: 'RoleProject' + getRandomInt(),
			roleType: 'depot',
			subtype: 'project',
		});
		const role3 = await apiHelpers.headlessAdminUser.postRole({
			name: 'RoleSpace' + getRandomInt(),
			roleType: 'depot',
			subtype: 'space',
		});

		await goToAllSpaces(page);

		const spaceName = 'Space' + getRandomInt();

		await createSpace(page, spaceName);

		try {
			await spaceSummaryPage.goto(spaceName);

			const folderName = 'Folder' + getRandomInt();

			await spaceSummaryPage.createContentFolder(folderName);

			await spaceSummaryPage.viewAllContentLink.click();

			await expect(async () => {
				await clickMenuItem('Permissions', page, folderName);

				await expect(
					permissionsPage.permissionsModalCloseButton
				).toBeVisible({
					timeout: 2000,
				});
			}).toPass({timeout: 20000});

			await expect(
				permissionsPage.roleCell(role1.name).first()
			).toBeVisible();
			await expect(permissionsPage.roleCell(role2.name)).toHaveCount(0);
			await expect(
				permissionsPage.roleCell(role3.name).first()
			).toBeVisible();

			await permissionsPage.permissionsModalCloseButton.click();
		}
		finally {
			await goToAllSpaces(page);

			await deleteSpace(page, spaceName);
		}
	}
);

test(
	'All Members modal role dropdown filters by the space subtype',
	{tag: '@LPD-88817'},
	async ({apiHelpers, page, spaceSummaryPage}) => {
		const role1 = await apiHelpers.headlessAdminUser.postRole({
			name: 'RoleNoSubtype' + getRandomInt(),
			roleType: 'depot',
		});
		const role2 = await apiHelpers.headlessAdminUser.postRole({
			name: 'RoleProject' + getRandomInt(),
			roleType: 'depot',
			subtype: 'project',
		});
		const role3 = await apiHelpers.headlessAdminUser.postRole({
			name: 'RoleSpace' + getRandomInt(),
			roleType: 'depot',
			subtype: 'space',
		});

		const spaceName = 'Space' + getRandomInt();

		await apiHelpers.headlessAssetLibrary.createAssetLibrary({
			name: spaceName,
			settings: {},
			type: 'Space',
		});

		const user = await apiHelpers.headlessAdminUser.postUserAccount();
		const userFullName = `${user.givenName} ${user.familyName}`;

		await spaceSummaryPage.goto(spaceName);

		await spaceSummaryPage.addUserOrUserGroup(userFullName, 'users');

		await spaceSummaryPage.viewAllMembersLink.click();

		const userRow = page
			.getByRole('dialog')
			.getByRole('listitem')
			.filter({hasText: userFullName});

		await userRow
			.locator('button:has(.permission-select-trigger-text)')
			.click();

		await expect(
			page.getByRole('checkbox', {exact: true, name: role1.name})
		).toBeVisible();
		await expect(
			page.getByRole('checkbox', {exact: true, name: role2.name})
		).toHaveCount(0);
		await expect(
			page.getByRole('checkbox', {exact: true, name: role3.name})
		).toBeVisible();
	}
);

test(
	'Define Permissions tree filters object definitions by depot role subtype',
	{tag: '@LPD-88820'},
	async ({apiHelpers, page, rolePage, rolesPage}) => {
		const objectMenuItem = (id: number) =>
			page.locator(`[data-qa-id="object_${id}"]`);

		const projectObjectDefinition =
			await apiHelpers.objectAdmin.postRandomObjectDefinition({
				objectDefinitionSettings: [
					{name: 'domain', value: 'project' as unknown as object},
				],
				scope: 'depot',
				status: {code: 0},
			});

		apiHelpers.data.push({
			id: projectObjectDefinition.id,
			type: 'objectDefinition',
		});

		const spaceObjectDefinition =
			await apiHelpers.objectAdmin.postRandomObjectDefinition({
				objectDefinitionSettings: [
					{name: 'domain', value: 'space' as unknown as object},
				],
				scope: 'depot',
				status: {code: 0},
			});

		apiHelpers.data.push({
			id: spaceObjectDefinition.id,
			type: 'objectDefinition',
		});

		const projectRole = await apiHelpers.headlessAdminUser.postRole({
			name: 'ProjectRole' + getRandomInt(),
			roleType: 'depot',
			subtype: 'project',
		});
		const spaceRole = await apiHelpers.headlessAdminUser.postRole({
			name: 'SpaceRole' + getRandomInt(),
			roleType: 'depot',
			subtype: 'space',
		});

		await rolesPage.goto();
		await rolesPage.rolesLink('Space').click();
		await rolesPage.selectRole(spaceRole.name);

		await rolePage.definePermissionsLink.click();

		await expect(objectMenuItem(projectObjectDefinition.id!)).toHaveCount(
			0
		);
		await expect(objectMenuItem(spaceObjectDefinition.id!)).toBeVisible();

		await rolesPage.goto();
		await rolesPage.rolesLink('Space').click();
		await rolesPage.selectRole(projectRole.name);

		await rolePage.definePermissionsLink.click();

		await expect(objectMenuItem(projectObjectDefinition.id!)).toBeVisible();
		await expect(objectMenuItem(spaceObjectDefinition.id!)).toHaveCount(0);
	}
);
