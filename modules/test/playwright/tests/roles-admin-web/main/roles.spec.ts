/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {dataApiHelpersTest} from '../../../fixtures/dataApiHelpersTest';
import {featureFlagsTest} from '../../../fixtures/featureFlagsTest';
import {isolatedSiteTest} from '../../../fixtures/isolatedSiteTest';
import {loginTest} from '../../../fixtures/loginTest';
import {rolesPagesTest} from '../../../fixtures/rolesPagesTest';
import {usersAndOrganizationsPagesTest} from '../../../fixtures/usersAndOrganizationsPagesTest';
import {TRole} from '../../../helpers/HeadlessAdminUserApiHelper';
import {RolesPage} from '../../../pages/roles-admin-web/RolesPage';
import {getRandomInt} from '../../../utils/getRandomInt';
import getRandomString from '../../../utils/getRandomString';
import {nextPage, setItemsPerPage} from '../../../utils/pagination';
import {
	performLoginViaApi,
	performLogout,
	userData,
} from '../../../utils/performLogin';
import {waitForAlert} from '../../../utils/waitForAlert';
import {setupBookmark} from './utils/bookmarks';

export const test = mergeTests(
	dataApiHelpersTest,
	featureFlagsTest({
		'LPD-47858': {enabled: true},
		'LPS-178052': {enabled: true},
	}),
	isolatedSiteTest,
	loginTest(),
	rolesPagesTest,
	usersAndOrganizationsPagesTest
);

test.beforeAll(async ({browser}) => {
	const page = await browser.newPage();
	const rolesPage = new RolesPage(page);

	await performLoginViaApi({page, screenName: 'test'});

	await rolesPage.goto();

	await expect(async () => {
		await rolesPage.rolesTable.changeView('Table');

		await expect(rolesPage.rolesTable.cell('Title')).toBeVisible({
			timeout: 300,
		});
	}).toPass();

	await page.close();
});

test(
	'Can add roles with same title but different key',
	{tag: ['@LPD-50065']},
	async ({apiHelpers, rolePage, rolesPage}) => {
		const key1 = getRandomString();
		const key2 = getRandomString();
		const title = getRandomString();

		await rolesPage.goto();

		await expect(rolesPage.rolesTable.searchInput).toBeEditable();

		await expect(async () => {
			await rolesPage.rolesTable.newButton.click();

			await expect(rolePage.keyInput).toBeVisible();
		}).toPass();

		await rolePage.addRole(apiHelpers, {name: key1, title});
		await rolePage.backButton.click();

		await expect(async () => {
			await rolesPage.rolesTable.newButton.click();

			await expect(rolePage.keyInput).toBeVisible();
		}).toPass();

		await rolePage.addRole(apiHelpers, {name: key2, title});
		await rolePage.backButton.click();

		await rolesPage.rolesTable.search(key1);

		await expect(rolesPage.rolesTable.cell(title)).toHaveCount(1);

		await rolesPage.rolesTable.search(key2);

		await expect(rolesPage.rolesTable.cell(title)).toHaveCount(1);

		await rolesPage.rolesTable.search(title);

		await expect(rolesPage.roleCell(title)).toHaveCount(2);
	}
);

test(
	'Can not add roles with same key',
	{tag: ['@LPD-50065']},
	async ({apiHelpers, page, rolePage, rolesPage}) => {
		const role = await apiHelpers.headlessAdminUser.postRole({
			name: getRandomString(),
			roleType: 'regular',
		});

		await rolesPage.goto();

		await expect(rolesPage.rolesTable.searchInput).toBeEditable();

		await rolesPage.rolesTable.newButton.click();

		const title = getRandomString();

		await rolePage.keyInput.fill(role.name);
		await rolePage.titleInput.fill(title);
		await rolePage.saveButton.click();

		await waitForAlert(page, 'Your request failed to complete', {
			type: 'danger',
		});

		await expect(rolePage.uniqueNameError).toBeVisible();

		await rolePage.backButton.click();
		await rolesPage.rolesTable.search(title);

		await expect(rolesPage.rolesTable.cell(title)).toHaveCount(0);
	}
);

test(
	'Can edit a role',
	{tag: ['@LPD-50065']},
	async ({apiHelpers, page, rolePage, rolesPage}) => {
		const role = await apiHelpers.headlessAdminUser.postRole({
			name: getRandomString(),
			roleType: 'regular',
		});

		await rolesPage.goto();

		await rolesPage.rolesTable.search(role.name);

		await expect(rolesPage.rolesTable.cell(role.name)).toBeVisible();
		await (await rolesPage.rolesTable.cellLink(role.name)).click();

		const description = getRandomString();
		const name = getRandomString();
		const title = getRandomString();

		await rolePage.descriptionInput.fill(description);
		await rolePage.keyInput.fill(name);
		await rolePage.titleInput.fill(title);
		await rolePage.saveButton.click();

		await waitForAlert(page);

		await rolePage.backButton.click();

		await expect(rolesPage.rolesTable.cell(role.name)).toHaveCount(0);

		await rolesPage.rolesTable.search(title);

		await expect(
			(await rolesPage.rolesTable.row(1, title, true)).row
		).toBeVisible();
		await expect(
			(await rolesPage.rolesTable.row(2, description, true)).row
		).toBeVisible();

		await (await rolesPage.rolesTable.cellLink(title)).click();

		await expect(rolePage.descriptionInput).toHaveValue(description);
		await expect(rolePage.keyInput).toHaveValue(name);
		await expect(rolePage.titleInput).toHaveValue(title);
	}
);

test(
	'Can add a role without a title',
	{tag: ['@LPD-50065', '@LPS-104999', '@LPS-105001']},
	async ({apiHelpers, page, rolePage, rolesPage}) => {
		const key = getRandomString();

		await rolesPage.goto();

		await expect(rolesPage.rolesTable.searchInput).toBeEditable();

		await rolesPage.rolesTable.newButton.click();

		await rolePage.keyInput.fill(key);
		await rolePage.saveButton.click();

		await waitForAlert(page, `${key} was created successfully`);

		const roles = await apiHelpers.headlessAdminUser.getRoles(
			key,
			'rolePermissions'
		);

		if (roles && roles.items) {
			(roles.items as Array<TRole>).map((role) => {
				apiHelpers.data.push({
					id: role.id,
					type: 'role',
				});
			});
		}

		await rolePage.backButton.click();

		await expect(rolesPage.rolesTable.cell(key)).toBeVisible();

		await (await rolesPage.rolesTable.cellLink(key)).click();

		await expect(rolePage.keyInput).toHaveValue(key);
		await expect(rolePage.titleInput).toHaveValue('');
	}
);

test(
	'Can search a role and view its status',
	{tag: ['@LPD-50065', '@LPD-54172']},
	async ({apiHelpers, rolesPage}) => {
		const role1 = await apiHelpers.headlessAdminUser.postRole({
			name: `A${getRandomString()}`,
			name_i18n: {'en-US': `A${getRandomString()}`},
			roleType: 'regular',
		});

		const role2 = await apiHelpers.headlessAdminUser.postRole({
			name: `A${getRandomString()}`,
			name_i18n: {'en-US': `A${getRandomString()}`},
			roleType: 'regular',
		});

		await rolesPage.goto();

		await expect(async () => {
			await rolesPage.rolesTable.search(getRandomString());

			await expect(
				rolesPage.rolesTable.cell(role1.name_i18n['en-US'])
			).toHaveCount(0);
			await expect(
				rolesPage.rolesTable.cell(role2.name_i18n['en-US'])
			).toHaveCount(0);
		}).toPass();

		await rolesPage.rolesTable.search(role1.name);

		await expect(
			rolesPage.rolesTable.cell(role1.name_i18n['en-US'])
		).toBeVisible();
		await expect(
			rolesPage.rolesTable.cell(role2.name_i18n['en-US'])
		).toHaveCount(0);
		await expect(rolesPage.rolesTable.cell('Approved')).toBeVisible();

		await rolesPage.rolesTable.changeView('List');

		await expect(
			rolesPage.rolesTable.valueLink(role1.name_i18n['en-US'])
		).toBeVisible();
		await expect(rolesPage.statusText('Approved')).toBeVisible();

		await rolesPage.rolesTable.search(role2.name);
		await rolesPage.rolesTable.changeView('Table');

		await expect(
			rolesPage.rolesTable.cell(role1.name_i18n['en-US'])
		).toHaveCount(0);
		await expect(
			rolesPage.rolesTable.cell(role2.name_i18n['en-US'])
		).toBeVisible();

		await rolesPage.rolesTable.search(role1.name_i18n['en-US']);

		await expect(
			rolesPage.rolesTable.cell(role1.name_i18n['en-US'])
		).toBeVisible();
		await expect(
			rolesPage.rolesTable.cell(role2.name_i18n['en-US'])
		).toHaveCount(0);

		await rolesPage.rolesTable.search(role2.name_i18n['en-US']);

		await expect(
			rolesPage.rolesTable.cell(role1.name_i18n['en-US'])
		).toHaveCount(0);
		await expect(
			rolesPage.rolesTable.cell(role2.name_i18n['en-US'])
		).toBeVisible();

		await rolesPage.rolesTable.search('');

		await expect(
			rolesPage.rolesTable.cell(role1.name_i18n['en-US'])
		).toBeVisible();
		await expect(
			rolesPage.rolesTable.cell(role2.name_i18n['en-US'])
		).toBeVisible();
	}
);

test(
	'Can sort the roles',
	{tag: ['@LPD-50065']},
	async ({apiHelpers, rolesPage}) => {
		const role1 = await apiHelpers.headlessAdminUser.postRole({
			name: `A${getRandomString()}`,
			roleType: 'regular',
		});

		const role2 = await apiHelpers.headlessAdminUser.postRole({
			name: `Z${getRandomString()}`,
			roleType: 'regular',
		});

		await rolesPage.goto();

		await expect(rolesPage.rolesTable.searchInput).toBeEditable();

		await expect(await rolesPage.rolesTable.firstRow()).toContainText(
			role1.name
		);

		await rolesPage.rolesTable.orderButton.click();

		await expect(await rolesPage.rolesTable.firstRow()).toContainText(
			role2.name
		);

		await rolesPage.rolesTable.orderButton.click();
	}
);

test(
	'Roles can be viewed in table view',
	{tag: ['@LPD-50065']},
	async ({rolesPage}) => {
		await rolesPage.goto();

		await rolesPage.rolesTable.changeView('Table');

		await expect(rolesPage.rolesTable.cell('Title')).toBeVisible();
		await expect(rolesPage.rolesTable.cell('Description')).toBeVisible();
		await expect(
			rolesPage.rolesTable.cell('Number of Assignees')
		).toBeVisible();
	}
);

test(
	'Regular role can be deleted',
	{tag: ['@LPD-50065']},
	async ({apiHelpers, page, rolesPage}) => {
		page.on('dialog', (dialog) => dialog.accept());

		const role = await apiHelpers.headlessAdminUser.postRole({
			name: getRandomString(),
			roleType: 'regular',
		});

		await rolesPage.goto();

		await rolesPage.rolesTable.search(role.name);

		await expect(rolesPage.rolesTable.cell(role.name)).toBeVisible();

		await expect(async () => {
			await (await rolesPage.rolesTable.rowActions(role.name)).click();

			await expect(rolesPage.deleteButton).toBeVisible({timeout: 100});
		}).toPass();

		await rolesPage.deleteButton.click();

		await expect(rolesPage.rolesTable.cell(role.name)).toHaveCount(0);

		await page.reload();

		await rolesPage.rolesTable.search(role.name);

		await expect(rolesPage.rolesTable.cell(role.name)).toHaveCount(0);
	}
);

test(
	'Organization role can be deleted',
	{tag: ['@LPD-50065']},
	async ({apiHelpers, page, rolesPage}) => {
		page.on('dialog', (dialog) => dialog.accept());

		const role = await apiHelpers.headlessAdminUser.postRole({
			name: getRandomString(),
			roleType: 'organization',
		});

		await rolesPage.goto();

		await expect(rolesPage.rolesTable.cell(role.name)).toHaveCount(0);

		await rolesPage.organizationRolesLink.click();

		await expect(rolesPage.rolesTable.cell(role.name)).toBeVisible();

		await expect(async () => {
			await (await rolesPage.rolesTable.rowActions(role.name)).click();

			await expect(rolesPage.deleteButton).toBeVisible({timeout: 100});
		}).toPass();

		await rolesPage.deleteButton.click();

		await expect(rolesPage.rolesTable.cell(role.name)).toHaveCount(0);

		await page.reload();

		await rolesPage.rolesTable.search(role.name);

		await expect(rolesPage.rolesTable.cell(role.name)).toHaveCount(0);
	}
);

test(
	'Site role can be deleted',
	{tag: ['@LPD-50065']},
	async ({apiHelpers, page, rolesPage}) => {
		page.on('dialog', (dialog) => dialog.accept());

		const role = await apiHelpers.headlessAdminUser.postRole({
			name: getRandomString(),
			roleType: 'site',
		});

		await rolesPage.goto();

		await expect(rolesPage.rolesTable.cell(role.name)).toHaveCount(0);

		await rolesPage.siteRolesLink.click();

		await expect(rolesPage.rolesTable.cell(role.name)).toBeVisible();

		await expect(async () => {
			await (await rolesPage.rolesTable.rowActions(role.name)).click();

			await expect(rolesPage.deleteButton).toBeVisible({timeout: 100});
		}).toPass();

		await rolesPage.deleteButton.click();

		await expect(rolesPage.rolesTable.cell(role.name)).toHaveCount(0);

		await page.reload();

		await rolesPage.rolesTable.search(role.name);

		await expect(rolesPage.rolesTable.cell(role.name)).toHaveCount(0);
	}
);

test(
	'User can be assigned to a regular role',
	{tag: ['@LPD-50065', '@LPS-109572']},
	async ({apiHelpers, roleAssigneesPage, rolePage, rolesPage}) => {
		const role = await apiHelpers.headlessAdminUser.postRole({
			name: getRandomString(),
			roleType: 'regular',
		});

		const user = await apiHelpers.headlessAdminUser.postUserAccount();

		await apiHelpers.headlessAdminUser.assignUserToRole(
			role.externalReferenceCode,
			user.id
		);

		await rolesPage.goto();

		await rolesPage.rolesTable.search(role.name);
		await (await rolesPage.rolesTable.cellLink(role.name)).click();
		await rolePage.assigneesLink.click();

		await expect(
			roleAssigneesPage.assigneesTable.cell(user.name)
		).toBeVisible();
	}
);

test(
	'User can be assigned / unassigned from a regular role',
	{tag: ['@LPD-50065', '@LPS-109572']},
	async ({
		apiHelpers,
		page,
		roleAssigneesPage,
		rolePage,
		roleUserSelectorPage,
		rolesPage,
	}) => {
		test.setTimeout(120000);

		const companyId = await page.evaluate(() => {
			return Liferay.ThemeDisplay.getCompanyId();
		});

		const role = await apiHelpers.headlessAdminUser.postRole({
			name: getRandomString(),
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
					resourceName:
						'com_liferay_user_groups_admin_web_portlet_UserGroupsAdminPortlet',
					scope: 1,
				},
			],
			roleType: 'regular',
		});

		const user = await apiHelpers.headlessAdminUser.postUserAccount();

		userData[user.alternateName] = {
			name: user.givenName,
			password: 'test',
			surname: user.familyName,
		};

		await rolesPage.goto();

		await rolesPage.rolesTable.search(role.name);
		await (await rolesPage.rolesTable.cellLink(role.name)).click();
		await rolePage.assigneesLink.click();

		await expect(
			roleAssigneesPage.assigneesTable.cell(user.name)
		).toHaveCount(0);

		await roleAssigneesPage.assigneesTable.newButton.click();

		await expect(
			roleUserSelectorPage.usersTable.cell(user.name)
		).toBeVisible();

		await roleUserSelectorPage.assignUsers([user.name]);

		await expect(
			roleAssigneesPage.assigneesTable.cell(user.name)
		).toBeVisible();

		await performLogout(page);
		await performLoginViaApi({page, screenName: user.alternateName});

		await expect(rolesPage.applicationsMenuButton).toBeVisible();

		await performLogout(page);
		await performLoginViaApi({page, screenName: 'test'});

		await rolesPage.goto();

		await rolesPage.rolesTable.search(role.name);
		await (await rolesPage.rolesTable.cellLink(role.name)).click();
		await rolePage.assigneesLink.click();

		await expect(
			roleAssigneesPage.assigneesTable.cell(user.name)
		).toBeVisible();

		await (
			await roleAssigneesPage.assigneesTable.rowCheckbox(user.name)
		).check();
		await roleAssigneesPage.removeButton.click();

		await waitForAlert(page);

		await expect(
			roleAssigneesPage.assigneesTable.cell(user.name)
		).toHaveCount(0);

		await performLogout(page);
		await performLoginViaApi({page, screenName: user.alternateName});

		await expect(rolesPage.applicationsMenuButton).toHaveCount(0);
	}
);

test(
	'User Group can be assigned / unassigned from a regular role',
	{tag: ['@LPD-50065', '@LPS-109572']},
	async ({
		apiHelpers,
		page,
		roleAssigneesPage,
		rolePage,
		roleUserGroupSelectorPage,
		rolesPage,
	}) => {
		test.setTimeout(120000);

		const companyId = await page.evaluate(() => {
			return Liferay.ThemeDisplay.getCompanyId();
		});

		const role = await apiHelpers.headlessAdminUser.postRole({
			name: getRandomString(),
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
					resourceName:
						'com_liferay_user_groups_admin_web_portlet_UserGroupsAdminPortlet',
					scope: 1,
				},
			],
			roleType: 'regular',
		});

		const userGroup = await apiHelpers.headlessAdminUser.postUserGroup();

		const user = await apiHelpers.headlessAdminUser.postUserAccount();

		userData[user.alternateName] = {
			name: user.givenName,
			password: 'test',
			surname: user.familyName,
		};

		await apiHelpers.headlessAdminUser.assignUsersToUserGroup(
			userGroup.id,
			[user.id]
		);

		await rolesPage.goto();

		await rolesPage.rolesTable.search(role.name);
		await (await rolesPage.rolesTable.cellLink(role.name)).click();
		await rolePage.assigneesLink.click();

		await expect(
			roleAssigneesPage.assigneesTable.cell(userGroup.name)
		).toHaveCount(0);

		await roleAssigneesPage.userGroupsLink.click();

		await expect(
			roleAssigneesPage.noDataMessage('user groups')
		).toBeVisible();
		await expect(
			roleAssigneesPage.assigneesTable.cell(userGroup.name)
		).toHaveCount(0);

		await roleAssigneesPage.assigneesTable.newButton.click();

		await expect(
			roleUserGroupSelectorPage.userGroupsTable.cell(userGroup.name)
		).toBeVisible();

		await roleUserGroupSelectorPage.assignUserGroups([userGroup.name]);

		await expect(
			roleAssigneesPage.assigneesTable.cell(userGroup.name)
		).toBeVisible();

		await performLogout(page);
		await performLoginViaApi({page, screenName: user.alternateName});

		await expect(rolesPage.applicationsMenuButton).toBeVisible();

		await performLogout(page);
		await performLoginViaApi({page, screenName: 'test'});

		await rolesPage.goto();

		await rolesPage.rolesTable.search(role.name);
		await (await rolesPage.rolesTable.cellLink(role.name)).click();
		await rolePage.assigneesLink.click();
		await roleAssigneesPage.userGroupsLink.click();

		await expect(
			roleAssigneesPage.noDataMessage('user groups')
		).toHaveCount(0);
		await expect(
			roleAssigneesPage.assigneesTable.cell(userGroup.name)
		).toBeVisible();

		await (
			await roleAssigneesPage.assigneesTable.rowCheckbox(userGroup.name)
		).check();
		await roleAssigneesPage.removeButton.click();

		await waitForAlert(page);

		await expect(
			roleAssigneesPage.assigneesTable.cell(userGroup.name)
		).toHaveCount(0);

		await performLogout(page);
		await performLoginViaApi({page, screenName: user.alternateName});

		await expect(rolesPage.applicationsMenuButton).toHaveCount(0);
	}
);

test(
	'Organization can be assigned / unassigned from a regular role',
	{tag: ['@LPD-50065']},
	async ({
		apiHelpers,
		page,
		roleAssigneesPage,
		roleOrganizationSelectorPage,
		rolePage,
		rolesPage,
	}) => {
		test.setTimeout(120000);

		const companyId = await page.evaluate(() => {
			return Liferay.ThemeDisplay.getCompanyId();
		});

		const role = await apiHelpers.headlessAdminUser.postRole({
			name: getRandomString(),
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
					resourceName:
						'com_liferay_user_groups_admin_web_portlet_UserGroupsAdminPortlet',
					scope: 1,
				},
			],
			roleType: 'regular',
		});

		const organization =
			await apiHelpers.headlessAdminUser.postOrganization();

		const user = await apiHelpers.headlessAdminUser.postUserAccount();

		userData[user.alternateName] = {
			name: user.givenName,
			password: 'test',
			surname: user.familyName,
		};

		await apiHelpers.headlessAdminUser.assignUserToOrganizationByEmailAddress(
			organization.id,
			user.emailAddress
		);

		await rolesPage.goto();

		await rolesPage.rolesTable.search(role.name);
		await (await rolesPage.rolesTable.cellLink(role.name)).click();
		await rolePage.assigneesLink.click();

		await expect(
			roleAssigneesPage.assigneesTable.cell(organization.name)
		).toHaveCount(0);

		await roleAssigneesPage.organizationsLink.click();

		await expect(
			roleAssigneesPage.noDataMessage('organizations')
		).toBeVisible();
		await expect(
			roleAssigneesPage.assigneesTable.cell(organization.name)
		).toHaveCount(0);

		await roleAssigneesPage.assigneesTable.newButton.click();

		await expect(
			roleOrganizationSelectorPage.organizationsTable.cell(
				organization.name
			)
		).toBeVisible();

		await roleOrganizationSelectorPage.assignOrganizations([
			organization.name,
		]);

		await expect(
			roleAssigneesPage.assigneesTable.cell(organization.name)
		).toBeVisible();

		await performLogout(page);
		await performLoginViaApi({page, screenName: user.alternateName});

		await expect(rolesPage.applicationsMenuButton).toBeVisible();

		await performLogout(page);
		await performLoginViaApi({page, screenName: 'test'});

		await rolesPage.goto();

		await rolesPage.rolesTable.search(role.name);
		await (await rolesPage.rolesTable.cellLink(role.name)).click();
		await rolePage.assigneesLink.click();
		await roleAssigneesPage.organizationsLink.click();

		await expect(
			roleAssigneesPage.noDataMessage('organizations')
		).toHaveCount(0);
		await expect(
			roleAssigneesPage.assigneesTable.cell(organization.name)
		).toBeVisible();

		await (
			await roleAssigneesPage.assigneesTable.rowCheckbox(
				organization.name
			)
		).check();
		await roleAssigneesPage.removeButton.click();

		await waitForAlert(page);

		await expect(
			roleAssigneesPage.assigneesTable.cell(organization.name)
		).toHaveCount(0);

		await performLogout(page);
		await performLoginViaApi({page, screenName: user.alternateName});

		await expect(rolesPage.applicationsMenuButton).toHaveCount(0);
	}
);

test(
	'Segment can be assigned / unassigned from a regular role',
	{tag: ['@LPD-50065']},
	async ({
		apiHelpers,
		page,
		roleAssigneesPage,
		rolePage,
		roleSegmentSelectorPage,
		rolesPage,
	}) => {
		test.setTimeout(120000);

		const companyId = await page.evaluate(() => {
			return Liferay.ThemeDisplay.getCompanyId();
		});

		const globalSite = await apiHelpers.jsonWebServicesGroup.getGroupByKey(
			companyId,
			companyId
		);

		const role = await apiHelpers.headlessAdminUser.postRole({
			name: getRandomString(),
			roleType: 'regular',
		});

		const segmentsEntry =
			await apiHelpers.jsonWebServicesSegmentsEntry.addSegmentsEntry({
				criteria: {
					criteria: {
						user: {
							conjunction: 'and',
							filterString: `(emailAddress eq 'test@liferay.com')`,
							typeValue: 'model',
						},
					},
					filterString: {
						model: `(emailAddress eq 'test@liferay.com')`,
					},
				},
				groupId: globalSite.groupId,
				name: getRandomString(),
			});

		await rolesPage.goto();

		await rolesPage.rolesTable.search(role.name);
		await (await rolesPage.rolesTable.cellLink(role.name)).click();
		await rolePage.assigneesLink.click();

		await expect(
			roleAssigneesPage.assigneesTable.cell(
				segmentsEntry.nameCurrentValue
			)
		).toHaveCount(0);

		await roleAssigneesPage.segmentsLink.click();

		await expect(roleAssigneesPage.noDataMessage('segments')).toBeVisible();
		await expect(
			roleAssigneesPage.assigneesTable.cell(
				segmentsEntry.nameCurrentValue
			)
		).toHaveCount(0);

		await roleAssigneesPage.assigneesTable.newButton.click();

		await expect(
			roleSegmentSelectorPage.segmentsTable.cell(
				segmentsEntry.nameCurrentValue
			)
		).toBeVisible();
		await expect(
			roleSegmentSelectorPage.segmentsTable.newButton
		).toBeVisible();

		await roleSegmentSelectorPage.assignSegments([
			segmentsEntry.nameCurrentValue,
		]);

		await expect(
			roleAssigneesPage.assigneesTable.cell(
				segmentsEntry.nameCurrentValue
			)
		).toBeVisible();

		await rolesPage.goto();

		await rolesPage.rolesTable.search(role.name);
		await (await rolesPage.rolesTable.cellLink(role.name)).click();
		await rolePage.assigneesLink.click();
		await roleAssigneesPage.segmentsLink.click();

		await expect(roleAssigneesPage.noDataMessage('segments')).toHaveCount(
			0
		);
		await expect(
			roleAssigneesPage.assigneesTable.cell(
				segmentsEntry.nameCurrentValue
			)
		).toBeVisible();

		await (
			await roleAssigneesPage.assigneesTable.rowCheckbox(
				segmentsEntry.nameCurrentValue
			)
		).check();
		await roleAssigneesPage.removeButton.click();

		await waitForAlert(page);

		await expect(
			roleAssigneesPage.assigneesTable.cell(
				segmentsEntry.nameCurrentValue
			)
		).toHaveCount(0);
	}
);

test(
	'Site can be assigned / unassigned from a regular role',
	{tag: ['@LPD-50065']},
	async ({
		apiHelpers,
		page,
		roleAssigneesPage,
		rolePage,
		roleSiteSelectorPage,
		rolesPage,
		site,
	}) => {
		test.setTimeout(120000);

		const companyId = await page.evaluate(() => {
			return Liferay.ThemeDisplay.getCompanyId();
		});

		const role = await apiHelpers.headlessAdminUser.postRole({
			name: getRandomString(),
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
					resourceName:
						'com_liferay_user_groups_admin_web_portlet_UserGroupsAdminPortlet',
					scope: 1,
				},
			],
			roleType: 'regular',
		});

		const user = await apiHelpers.headlessAdminUser.postUserAccount();

		userData[user.alternateName] = {
			name: user.givenName,
			password: 'test',
			surname: user.familyName,
		};

		await apiHelpers.jsonWebServicesUser.addGroupUsers(site.id, [user.id]);

		await rolesPage.goto();

		await rolesPage.rolesTable.search(role.name);
		await (await rolesPage.rolesTable.cellLink(role.name)).click();
		await rolePage.assigneesLink.click();

		await expect(
			roleAssigneesPage.assigneesTable.cell(site.name)
		).toHaveCount(0);

		await roleAssigneesPage.sitesLink.click();

		await expect(roleAssigneesPage.noDataMessage('sites')).toBeVisible();
		await expect(
			roleAssigneesPage.assigneesTable.cell(site.name)
		).toHaveCount(0);

		await roleAssigneesPage.assigneesTable.newButton.click();

		await expect(
			roleSiteSelectorPage.sitesTable.cell(site.name)
		).toBeVisible();

		await roleSiteSelectorPage.assignSite([site.name]);

		await expect(
			roleAssigneesPage.assigneesTable.cell(site.name)
		).toBeVisible();

		await performLogout(page);
		await performLoginViaApi({page, screenName: user.alternateName});

		await expect(rolesPage.applicationsMenuButton).toBeVisible();

		await performLogout(page);
		await performLoginViaApi({page, screenName: 'test'});

		await rolesPage.goto();

		await rolesPage.rolesTable.search(role.name);
		await (await rolesPage.rolesTable.cellLink(role.name)).click();
		await rolePage.assigneesLink.click();
		await roleAssigneesPage.sitesLink.click();

		await expect(roleAssigneesPage.noDataMessage('sites')).toHaveCount(0);
		await expect(
			roleAssigneesPage.assigneesTable.cell(site.name)
		).toBeVisible();

		await (
			await roleAssigneesPage.assigneesTable.rowCheckbox(site.name)
		).check();
		await roleAssigneesPage.removeButton.click();

		await waitForAlert(page);

		await expect(
			roleAssigneesPage.assigneesTable.cell(site.name)
		).toHaveCount(0);

		await performLogout(page);
		await performLoginViaApi({page, screenName: user.alternateName});

		await expect(rolesPage.applicationsMenuButton).toHaveCount(0);
	}
);

test(
	'Segments assignee tab is not available for Administrator role',
	{tag: ['@LPD-50065', '@LPS-202614']},
	async ({roleAssigneesPage, rolePage, rolesPage}) => {
		await rolesPage.goto();

		await rolesPage.rolesTable.search('Administrator');
		await (await rolesPage.rolesTable.cellLink('Administrator')).click();
		await rolePage.assigneesLink.click();

		await expect(roleAssigneesPage.noDataMessage('users')).toHaveCount(0);
		await expect(roleAssigneesPage.organizationsLink).toBeVisible();
		await expect(roleAssigneesPage.segmentsLink).toHaveCount(0);
		await expect(roleAssigneesPage.sitesLink).toBeVisible();
		await expect(roleAssigneesPage.userGroupsLink).toBeVisible();
		await expect(roleAssigneesPage.usersLink).toBeVisible();
	}
);

test(
	'Can add / remove the site scope of a role permission',
	{tag: ['@LPD-50065', '@LPS-116055']},
	async ({
		apiHelpers,
		page,
		roleDefinePermissionsPage,
		rolePage,
		rolesPage,
	}) => {
		const role = await apiHelpers.headlessAdminUser.postRole({
			name: getRandomString(),
			roleType: 'regular',
		});

		await rolesPage.goto();

		await expect(async () => {
			await rolesPage.rolesTable.search(role.name);

			await expect(rolesPage.rolesTable.cell(role.name)).toBeVisible();
		}).toPass();

		await (await rolesPage.rolesTable.cellLink(role.name)).click();

		await rolePage.definePermissionsLink.click();

		const menuItemName = 'Documents and Media';
		const permissionName =
			'Access in Site and Asset Library Administration';
		const siteName = 'Liferay DXP';

		await roleDefinePermissionsPage.searchInput.click();
		await roleDefinePermissionsPage.searchInput.fill(menuItemName);

		await expect(
			roleDefinePermissionsPage.menuItem(menuItemName)
		).toBeVisible();

		await roleDefinePermissionsPage.menuItem(menuItemName).click();
		await page.waitForLoadState('domcontentloaded');

		await expect(
			roleDefinePermissionsPage.permissionScopeLabel(permissionName)
		).toBeVisible();

		await roleDefinePermissionsPage
			.permissionScopeChangeButton(permissionName)
			.click();

		await expect(async () => {
			await roleDefinePermissionsPage
				.siteSelectorSiteCard(siteName)
				.click();

			await expect(
				roleDefinePermissionsPage.permissionScopeSiteLabel(
					permissionName,
					siteName
				)
			).toBeVisible({timeout: 3000});
		}).toPass();

		await roleDefinePermissionsPage
			.permissionCheckbox(permissionName)
			.check();
		await roleDefinePermissionsPage.saveButton.click();

		await waitForAlert(page, 'Success:The role permissions were updated.');

		await expect(
			roleDefinePermissionsPage.summaryPermissionCell(permissionName)
		).toBeVisible();
		await expect(
			roleDefinePermissionsPage.summaryPermissionScopeCell(
				permissionName,
				siteName
			)
		).toBeVisible();

		await page.reload();

		await expect(
			roleDefinePermissionsPage.summaryPermissionCell(permissionName)
		).toBeVisible();
		await expect(
			roleDefinePermissionsPage.summaryPermissionScopeCell(
				permissionName,
				siteName
			)
		).toBeVisible();
		await expect(
			roleDefinePermissionsPage.summaryPermissionScopeCell(
				permissionName,
				'All Sites and Asset Libraries'
			)
		).toHaveCount(0);

		await roleDefinePermissionsPage.searchInput.click();
		await roleDefinePermissionsPage.searchInput.fill(menuItemName);

		await expect(
			roleDefinePermissionsPage.menuItem(menuItemName)
		).toBeVisible();

		await roleDefinePermissionsPage.menuItem(menuItemName).click();
		await page.waitForLoadState('domcontentloaded');

		await expect(
			roleDefinePermissionsPage.permissionScopeLabel(permissionName)
		).toHaveCount(0);
		await expect(
			roleDefinePermissionsPage.permissionScopes(permissionName)
		).toContainText(siteName);

		await roleDefinePermissionsPage
			.permissionScopeRemoveButton(permissionName)
			.click();

		await expect(
			roleDefinePermissionsPage.permissionScopeLabel(permissionName)
		).toBeVisible();
		await expect(
			roleDefinePermissionsPage.permissionScopes(permissionName)
		).toHaveCount(0);

		await roleDefinePermissionsPage.saveButton.click();

		await waitForAlert(page, 'Success:The role permissions were updated.');

		await expect(
			roleDefinePermissionsPage.summaryPermissionCell(permissionName)
		).toBeVisible();
		await expect(
			roleDefinePermissionsPage.summaryPermissionScopeCell(
				permissionName,
				siteName
			)
		).toHaveCount(0);
		await expect(
			roleDefinePermissionsPage.summaryPermissionScopeCell(
				permissionName,
				'All Sites and Asset Libraries'
			)
		).toBeVisible();
	}
);

test(
	'All subdirectories in Define Permissions should display',
	{tag: ['@LPD-50065', '@LPS-97321']},
	async ({roleDefinePermissionsPage, rolePage, rolesPage}) => {
		await rolesPage.goto();

		await expect(async () => {
			await rolesPage.rolesTable.search('User');

			await expect(rolesPage.rolesTable.cell('User')).toBeVisible();
		}).toPass();

		await (await rolesPage.rolesTable.cellLink('User')).click();

		await rolePage.definePermissionsLink.click();

		const menuItems = {
			'Applications Menu': ['Communication', 'Content', 'Workflow'],
			'Control Panel': [
				'Accounts',
				'Configuration',
				'General Permissions',
				'Marketplace',
				'Security',
				'Sites',
				'System',
				'Users',
			],
			'Site and Asset Library Administration': [
				'Applications',
				'Categorization',
				'Configuration',
				'Content & Data',
				'People',
				'Publishing',
				'Recycle Bin',
				'Site Builder',
			],
			'User': [
				'Account Settings',
				'My Organizations',
				'My Submissions',
				'My Workflow Tasks',
				'Notifications',
			],
		};

		for (const menuItem of Object.keys(menuItems)) {
			const subMenuItems = menuItems[menuItem];

			if (
				['Site and Asset Library Administration', 'User'].includes(
					menuItem
				)
			) {
				await roleDefinePermissionsPage
					.menuItem(menuItem, true)
					.click();
			}

			for (const subMenuItem of subMenuItems) {
				await expect(
					roleDefinePermissionsPage.subMenuItem(menuItem, subMenuItem)
				).toBeVisible();
			}
		}
	}
);

test(
	'Select All checkbox is visible in Define Permissions',
	{tag: ['@LPD-50065', '@LPS-132482']},
	async ({page, roleDefinePermissionsPage, rolePage, rolesPage}) => {
		await rolesPage.goto();

		await expect(async () => {
			await rolesPage.rolesTable.search('User');

			await expect(rolesPage.rolesTable.cell('User')).toBeVisible();
		}).toPass();

		await (await rolesPage.rolesTable.cellLink('User')).click();

		await rolePage.definePermissionsLink.click();

		const menuItemName = 'Users and Organizations';

		await roleDefinePermissionsPage.searchInput.click();
		await roleDefinePermissionsPage.searchInput.fill(menuItemName);

		await expect(
			roleDefinePermissionsPage.menuItem(menuItemName)
		).toBeVisible();

		await roleDefinePermissionsPage.menuItem(menuItemName).click();
		await page.waitForLoadState('domcontentloaded');
		await roleDefinePermissionsPage
			.selectAllCheckbox('Application Permissions')
			.click();

		await expect(
			roleDefinePermissionsPage.permissionCheckbox(
				'Access in Control Panel'
			)
		).toBeChecked();
	}
);

test(
	'Organization Role table is empty and select button does not display if user is not a member of an organization',
	{tag: ['@LPD-50065']},
	async ({apiHelpers, editUserPage, page, usersAndOrganizationsPage}) => {
		const user = await apiHelpers.headlessAdminUser.postUserAccount();

		await usersAndOrganizationsPage.goToUsers();

		await (
			await usersAndOrganizationsPage.usersTableRowLink(
				user.alternateName
			)
		).click();
		await editUserPage.rolesLink.click();

		await expect(editUserPage.selectOrganizationRolesButton).toHaveCount(0);

		const organization =
			await apiHelpers.headlessAdminUser.postOrganization();

		await apiHelpers.headlessAdminUser.assignUserToOrganizationByEmailAddress(
			organization.id,
			user.emailAddress
		);

		await page.reload();

		await expect(editUserPage.selectOrganizationRolesButton).toBeVisible();
	}
);

test(
	'Site Role table is empty and select button does not display if user is not a member of a site',
	{tag: ['@LPD-50065']},
	async ({
		apiHelpers,
		editUserPage,
		page,
		site,
		usersAndOrganizationsPage,
	}) => {
		const user = await apiHelpers.headlessAdminUser.postUserAccount();

		await usersAndOrganizationsPage.goToUsers();

		await (
			await usersAndOrganizationsPage.usersTableRowLink(
				user.alternateName
			)
		).click();
		await editUserPage.rolesLink.click();

		await expect(editUserPage.selectSiteRolesButton).toHaveCount(0);

		await apiHelpers.jsonWebServicesUser.addGroupUsers(site.id, [user.id]);

		await page.reload();

		await expect(editUserPage.selectSiteRolesButton).toBeVisible();
	}
);

test(
	'Deactivated user is not counted in Site Roles',
	{tag: ['@LPD-50065', '@LPS-141903']},
	async ({
		apiHelpers,
		page,
		rolesPage,
		site,
		siteMembershipsPage,
		usersAndOrganizationsPage,
	}) => {
		page.on('dialog', (dialog) => dialog.accept());

		const roleName = 'Site Content Reviewer';

		const user1 = await apiHelpers.headlessAdminUser.postUserAccount();
		const user2 = await apiHelpers.headlessAdminUser.postUserAccount();

		await apiHelpers.jsonWebServicesUser.addGroupUsers(site.id, [
			user1.id,
			user2.id,
		]);

		const role = await apiHelpers.headlessAdminUser.getRoleByName(roleName);

		await apiHelpers.headlessAdminUser.assignUserToSite(
			role.id,
			site.id,
			user1.id
		);
		await apiHelpers.headlessAdminUser.assignUserToSite(
			role.id,
			site.id,
			user2.id
		);

		await rolesPage.goto();

		await rolesPage.siteRolesLink.click();

		await expect(rolesPage.rolesTable.cell(roleName)).toBeVisible();
		await expect(
			await rolesPage.numberAssigneesCell(roleName, '2')
		).toBeVisible();

		await siteMembershipsPage.goto(site.friendlyUrlPath);

		await expect(async () => {
			await siteMembershipsPage.usersTable.changeView('Table');

			await expect(
				siteMembershipsPage.usersTable.cell(user1.name)
			).toBeVisible();
			await expect(
				siteMembershipsPage.usersTable.cell(user2.name)
			).toBeVisible();
		}).toPass();

		await usersAndOrganizationsPage.goToUsers();

		await usersAndOrganizationsPage.deActivateUsers([user2.name]);

		await rolesPage.goto();

		await rolesPage.siteRolesLink.click();

		await expect(rolesPage.rolesTable.cell(roleName)).toBeVisible();
		await expect(
			await rolesPage.numberAssigneesCell(roleName, '1')
		).toBeVisible();

		await siteMembershipsPage.goto(site.friendlyUrlPath);

		await expect(async () => {
			await siteMembershipsPage.usersTable.changeView('Table');

			await expect(
				siteMembershipsPage.usersTable.cell(user1.name)
			).toBeVisible();
			await expect(
				siteMembershipsPage.usersTable.cell(user2.name)
			).toHaveCount(0);
		}).toPass();
	}
);

test(
	'Can add Site Administration permissions for specific site',
	{tag: ['@LPD-50835', '@LPS-133818']},
	async ({
		apiHelpers,
		page,
		roleDefinePermissionsPage,
		rolePage,
		rolesPage,
		site,
	}) => {
		const role = await apiHelpers.headlessAdminUser.postRole({
			name: getRandomString(),
			roleType: 'regular',
		});

		await rolesPage.goto();

		const menuItemName = 'Documents and Media';
		const permissionName =
			'Access in Site and Asset Library Administration';

		await (await rolesPage.rolesTable.cellLink(role.name)).click();
		await rolePage.definePermissionsLink.click();
		await roleDefinePermissionsPage.searchInput.click();
		await roleDefinePermissionsPage.searchInput.fill(menuItemName);

		await expect(
			roleDefinePermissionsPage.menuItem(menuItemName)
		).toBeVisible();

		await roleDefinePermissionsPage.menuItem(menuItemName).click();
		await page.waitForLoadState('domcontentloaded');

		await expect(
			roleDefinePermissionsPage.permissionScopeLabel(permissionName)
		).toBeVisible();

		await roleDefinePermissionsPage
			.permissionScopeChangeButton(permissionName)
			.click();

		await expect(async () => {
			await roleDefinePermissionsPage.siteSelectorMySitesLink.click();
			await roleDefinePermissionsPage
				.siteSelectorSiteCard(site.name)
				.click();

			await expect(
				roleDefinePermissionsPage.permissionScopeSiteLabel(
					permissionName,
					site.name
				)
			).toBeVisible({timeout: 3000});
		}).toPass();

		await roleDefinePermissionsPage
			.permissionCheckbox(permissionName)
			.check();
		await roleDefinePermissionsPage.saveButton.click();

		await waitForAlert(page, 'Success:The role permissions were updated.');

		await expect(
			roleDefinePermissionsPage.summaryPermissionCell(
				`Documents and Media: ${permissionName}`
			)
		).toBeVisible();
		await expect(
			roleDefinePermissionsPage.summaryPermissionScopeCell(
				`Documents and Media: ${permissionName}`,
				site.name
			)
		).toBeVisible();
		await expect(
			roleDefinePermissionsPage.summaryPermissionCell(
				'Documents and Media > Documents: View'
			)
		).toBeVisible();
		await expect(
			roleDefinePermissionsPage.summaryPermissionScopeCell(
				'Documents and Media > Documents: View',
				site.name
			)
		).toBeVisible();

		await expect(
			roleDefinePermissionsPage.summaryPermissionCell(
				'Site Settings > Site: View Site and Asset Library Administration Menu'
			)
		).toBeVisible();
		await expect(
			roleDefinePermissionsPage.summaryPermissionScopeCell(
				'Site Settings > Site: View Site and Asset Library Administration Menu',
				site.name
			)
		).toBeVisible();
	}
);

test(
	'Group scope permission check',
	{tag: ['@LPD-50835']},
	async ({
		apiHelpers,
		bookmarksPage,
		page,
		roleDefinePermissionsPage,
		rolePage,
		rolesPage,
		site: site1,
	}) => {
		const role = await apiHelpers.headlessAdminUser.postRole({
			name: getRandomString(),
			roleType: 'regular',
		});

		await rolesPage.goto();

		const menuItemName = 'Bookmarks';
		const permissionName = 'View';

		await (await rolesPage.rolesTable.cellLink(role.name)).click();
		await rolePage.definePermissionsLink.click();
		await roleDefinePermissionsPage.searchInput.click();
		await roleDefinePermissionsPage.searchInput.fill(menuItemName);

		await expect(
			roleDefinePermissionsPage.menuItem(menuItemName)
		).toBeVisible();

		await roleDefinePermissionsPage.menuItem(menuItemName).click();
		await page.waitForLoadState('domcontentloaded');

		await expect(
			roleDefinePermissionsPage.permissionScopeLabel(permissionName, true)
		).toBeVisible();

		await roleDefinePermissionsPage
			.permissionScopeChangeButton(permissionName, true)
			.click();

		await expect(async () => {
			await roleDefinePermissionsPage.siteSelectorMySitesLink.click();
			await roleDefinePermissionsPage
				.siteSelectorSiteCard(site1.name)
				.click();

			await expect(
				roleDefinePermissionsPage.permissionScopeSiteLabel(
					permissionName,
					site1.name
				)
			).toBeVisible({timeout: 3000});
		}).toPass();

		await roleDefinePermissionsPage
			.permissionCheckbox(permissionName, true)
			.check();
		await roleDefinePermissionsPage.saveButton.click();

		await waitForAlert(page, 'Success:The role permissions were updated.');

		const bookmarkName1 = getRandomString();

		const {layout: layout1} = await setupBookmark(
			apiHelpers,
			bookmarkName1,
			bookmarksPage,
			page,
			site1
		);

		const site2 = await apiHelpers.headlessSite.createSite({
			name: getRandomString(),
		});

		apiHelpers.data.push({id: site2.id, type: 'site'});

		const bookmarkName2 = getRandomString();

		const {layout: layout2} = await setupBookmark(
			apiHelpers,
			bookmarkName2,
			bookmarksPage,
			page,
			site2
		);

		const user = await apiHelpers.headlessAdminUser.postUserAccount();

		userData[user.alternateName] = {
			name: user.givenName,
			password: 'test',
			surname: user.familyName,
		};

		await apiHelpers.jsonWebServicesUser.assignUsersToSite(
			site1.id,
			user.id
		);
		await apiHelpers.jsonWebServicesUser.assignUsersToSite(
			site2.id,
			user.id
		);

		await performLogout(page);
		await performLoginViaApi({page, screenName: user.alternateName});

		await page.goto(`/web/${site1.name}/${layout1.friendlyUrlPath}`);

		await expect(bookmarksPage.bookmarkItem(bookmarkName1)).toHaveCount(0);

		await page.goto(`/web/${site2.name}/${layout2.friendlyUrlPath}`);

		await expect(bookmarksPage.bookmarkItem(bookmarkName2)).toHaveCount(0);

		await performLogout(page);
		await performLoginViaApi({page, screenName: 'test'});

		await apiHelpers.headlessAdminUser.assignUserToRole(
			role.externalReferenceCode,
			user.id
		);

		await performLogout(page);
		await performLoginViaApi({page, screenName: user.alternateName});

		await page.goto(`/web/${site1.name}/${layout1.friendlyUrlPath}`);

		await expect(bookmarksPage.bookmarkItem(bookmarkName1)).toBeVisible();

		await page.goto(`/web/${site2.name}/${layout2.friendlyUrlPath}`);

		await expect(bookmarksPage.bookmarkItem(bookmarkName2)).toHaveCount(0);
	}
);

test(
	'View Documents And Media Add Repository permission in site member role',
	{tag: ['@LPD-50835']},
	async ({
		apiHelpers,
		page,
		roleDefinePermissionsPage,
		rolePage,
		rolesPage,
	}) => {
		const companyId = await page.evaluate(() => {
			return Liferay.ThemeDisplay.getCompanyId();
		});

		const siteMemberRole = await apiHelpers.headlessAdminUser.getRoleByName(
			'Site Member',
			'rolePermissions'
		);

		try {
			await apiHelpers.jsonWebServicesResourcePermissionApiHelper.addResourcePermission(
				'ADD_REPOSITORY',
				companyId,
				'0',
				'com.liferay.document.library',
				'0',
				String(siteMemberRole.id),
				'3'
			);

			await rolesPage.goto();
			await rolesPage.siteRolesLink.click();

			await expect(
				rolesPage.rolesTable.cell(siteMemberRole.name)
			).toBeVisible();

			await (
				await rolesPage.rolesTable.cellLink(siteMemberRole.name)
			).click();
			await rolePage.definePermissionsLink.click();

			const permissionName = 'Add Repository';

			await expect(
				roleDefinePermissionsPage.summaryPermissionCell(permissionName)
			).toBeVisible();

			await roleDefinePermissionsPage.changePermission(
				'Documents and Media',
				permissionName,
				false
			);

			await expect(
				roleDefinePermissionsPage.summaryPermissionCell(permissionName)
			).toHaveCount(0);

			await page.reload();

			await expect(
				roleDefinePermissionsPage.summaryPermissionCell(permissionName)
			).toHaveCount(0);
		}
		finally {
			await apiHelpers.jsonWebServicesResourcePermissionApiHelper.removeResourcePermission(
				'ADD_REPOSITORY',
				companyId,
				'0',
				'com.liferay.document.library',
				'0',
				String(siteMemberRole.id),
				'3'
			);
		}
	}
);

test(
	'View automatic assigned permissions',
	{tag: ['@LPD-50835']},
	async ({apiHelpers, roleDefinePermissionsPage, rolePage, rolesPage}) => {
		const role = await apiHelpers.headlessAdminUser.postRole({
			name: getRandomString(),
			roleType: 'regular',
		});

		await rolesPage.goto();

		await rolesPage.rolesTable.search(role.name);

		await expect(rolesPage.rolesTable.cell(role.name)).toBeVisible();
		await (await rolesPage.rolesTable.cellLink(role.name)).click();
		await rolePage.definePermissionsLink.click();

		await roleDefinePermissionsPage.changePermission(
			'Users and Organizations',
			'Access in Control Panel',
			true
		);

		await expect(
			roleDefinePermissionsPage.summaryPermissionCell(
				'Portal: View Control Panel Menu'
			)
		).toBeVisible();
		await expect(
			roleDefinePermissionsPage.summaryPermissionCell(
				'Users and Organizations: Access in Control Panel'
			)
		).toBeVisible();

		await roleDefinePermissionsPage.changePermission(
			'Pages',
			'Access in Site and Asset Library Administration',
			true
		);

		await expect(
			roleDefinePermissionsPage.summaryPermissionCell(
				'Portal: View Control Panel Menu'
			)
		).toBeVisible();
		await expect(
			roleDefinePermissionsPage.summaryPermissionCell(
				'Users and Organizations: Access in Control Panel'
			)
		).toBeVisible();
		await expect(
			roleDefinePermissionsPage.summaryPermissionCell(
				'Asset Library Settings > Asset Library Entry: View Site and Asset Library Administration Menu'
			)
		).toBeVisible();
		await expect(
			roleDefinePermissionsPage.summaryPermissionCell(
				'Pages: Access in Site and Asset Library Administration'
			)
		).toBeVisible();
		await expect(
			roleDefinePermissionsPage.summaryPermissionCell(
				'Pages > Page SEO: View'
			)
		).toBeVisible();
		await expect(
			roleDefinePermissionsPage.summaryPermissionCell(
				'Site Settings > Site: View Site and Asset Library Administration Menu'
			)
		).toBeVisible();
	}
);

test(
	'Role assignments are correct when role count assigned exceeds items per page value',
	{tag: ['@LPD-56472']},
	async ({apiHelpers, editUserPage, usersAndOrganizationsPage}) => {
		const user = await apiHelpers.headlessAdminUser.postUserAccount();

		for (let i = 0; i < 22; i++) {
			const role = await apiHelpers.headlessAdminUser.postRole({
				name: 'role' + i,
				roleType: 'regular',
			});

			if (i > 0) {
				await apiHelpers.headlessAdminUser.assignUserToRole(
					role.externalReferenceCode,
					user.id
				);
			}
		}

		await usersAndOrganizationsPage.goToUsers();

		await (
			await usersAndOrganizationsPage.usersTableRowLink(
				user.alternateName
			)
		).click();
		await editUserPage.rolesLink.click();
		await editUserPage.selectRegularRolesButton.click();

		await setItemsPerPage(editUserPage.selectRegularRolesFrame, 20);

		await expect(
			editUserPage.selectRegularRolesChooseButton('role0')
		).toBeEnabled();
		await expect(
			editUserPage.selectRegularRolesChooseButton('role1').first()
		).toBeDisabled();

		await nextPage(editUserPage.selectRegularRolesFrame);

		await expect(
			editUserPage.selectRegularRolesChooseButton('role21')
		).toBeDisabled();
	}
);

test(
	'Can duplicate a role',
	{tag: ['@LPD-58202']},
	async ({
		apiHelpers,
		page,
		roleDefinePermissionsPage,
		rolePage,
		rolesPage,
	}) => {
		page.on('dialog', (dialog) => dialog.accept());

		const user = await apiHelpers.headlessAdminUser.postUserAccount();

		userData[user.alternateName] = {
			name: user.givenName,
			password: 'test',
			surname: user.familyName,
		};

		const companyId = await page.evaluate(() => {
			return Liferay.ThemeDisplay.getCompanyId();
		});

		const role1 = await apiHelpers.headlessAdminUser.postRole({
			name: getRandomString(),
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
					resourceName:
						'com_liferay_roles_admin_web_portlet_RolesAdminPortlet',
					scope: 1,
				},
				{
					actionIds: ['VIEW'],
					primaryKey: companyId,
					resourceName: 'com.liferay.portal.kernel.model.Role',
					scope: 1,
				},
				{
					actionIds: ['UPDATE'],
					primaryKey: companyId,
					resourceName: 'com.liferay.portal.kernel.model.Role',
					scope: 1,
				},
				{
					actionIds: ['PERMISSIONS'],
					primaryKey: companyId,
					resourceName: 'com.liferay.portal.kernel.model.Role',
					scope: 1,
				},
				{
					actionIds: ['DEFINE_PERMISSIONS'],
					primaryKey: companyId,
					resourceName: 'com.liferay.portal.kernel.model.Role',
					scope: 1,
				},
			],
			roleType: 'regular',
		});

		await apiHelpers.headlessAdminUser.assignUserToRole(
			role1.externalReferenceCode,
			user.id
		);

		await performLogout(page);
		await performLoginViaApi({page, screenName: user.alternateName});

		await rolesPage.goto(false);

		await rolesPage.rolesTable.changeView('Table');

		const guestRoleName = 'Guest';

		await rolesPage.rolesTable.search(guestRoleName);

		await expect(rolesPage.rolesTable.cell(guestRoleName)).toBeVisible();

		await (await rolesPage.rolesTable.rowActions(guestRoleName)).click();

		await expect(rolesPage.duplicateMenuItem).not.toBeVisible();

		await performLogout(page);
		await performLoginViaApi({page, screenName: 'test'});

		const role2 = await apiHelpers.headlessAdminUser.postRole({
			name: getRandomString(),
			rolePermissions: [
				{
					actionIds: ['ADD_ROLE'],
					primaryKey: companyId,
					resourceName: '90',
					scope: 1,
				},
			],
			roleType: 'regular',
		});

		await apiHelpers.headlessAdminUser.assignUserToRole(
			role2.externalReferenceCode,
			user.id
		);

		await performLogout(page);
		await performLoginViaApi({page, screenName: user.alternateName});

		await rolesPage.goto(false);

		await rolesPage.rolesTable.search(guestRoleName);

		await expect(rolesPage.rolesTable.cell(guestRoleName)).toBeVisible();

		await (await rolesPage.rolesTable.rowActions(guestRoleName)).click();
		await rolesPage.duplicateMenuItem.click();

		await expect(rolesPage.copyFrameNewRoleNameInput).toBeVisible();

		await rolesPage.copyFrameNewRoleNameInput.fill(guestRoleName);
		await rolesPage.copyFrameSaveButton.click();

		await expect(rolesPage.copyFrameErrorMessage).toBeVisible();

		await rolesPage.copyFrameNewRoleNameInput.clear();
		await rolesPage.copyFrameSaveButton.click();

		await expect(rolesPage.copyFrameEmptyErrorMessage).toBeVisible();

		const duplicateRoleName = 'role' + getRandomInt();

		await rolesPage.copyFrameNewRoleNameInput.fill(duplicateRoleName);
		await rolesPage.copyFrameSaveButton.click();

		try {
			await expect(rolePage.keyInput).toHaveValue(duplicateRoleName);

			await rolePage.definePermissionsLink.click();

			await expect(roleDefinePermissionsPage.searchInput).toBeVisible();
			await expect(
				roleDefinePermissionsPage.noRoleMessage
			).not.toBeVisible();
		}
		finally {
			await performLogout(page);
			await performLoginViaApi({page, screenName: 'test'});

			await rolesPage.goto();

			await rolesPage.rolesTable.search(duplicateRoleName);

			await expect(
				rolesPage.rolesTable.cell(duplicateRoleName)
			).toBeVisible();

			await (
				await rolesPage.rolesTable.rowActions(duplicateRoleName)
			).click();
			await rolesPage.deleteButton.click();
		}
	}
);

test(
	'Can duplicate all types of roles',
	{tag: ['@LPD-58202']},
	async ({page, rolePage, rolesPage}) => {
		page.on('dialog', (dialog) => dialog.accept());

		const copyRole = async (roleName: string, roleType: string) => {
			await expect(async () => {
				await rolesPage.rolesLink(roleType).click();

				await expect(rolesPage.rolesTable.searchInput).toBeEnabled({
					timeout: 1000,
				});

				await (await rolesPage.rolesTable.rowActions(roleName)).click();
			}).toPass();

			await rolesPage.duplicateMenuItem.click();

			await expect(rolesPage.copyFrameNewRoleNameInput).toBeEnabled();

			const duplicateRoleName = `${roleType}${getRandomInt()}`;

			await expect(async () => {
				await rolesPage.copyFrameNewRoleNameInput.fill(
					duplicateRoleName
				);

				await expect(rolesPage.copyFrameNewRoleNameInput).toHaveValue(
					duplicateRoleName,
					{timeout: 500}
				);
			}).toPass();

			await rolesPage.copyFrameSaveButton.click();

			await expect(rolePage.keyInput).toHaveValue(duplicateRoleName);

			await rolePage.backButton.click();

			await expect(
				rolesPage.rolesTable.cell(duplicateRoleName)
			).toBeVisible();

			await (
				await rolesPage.rolesTable.rowActions(duplicateRoleName)
			).click();
			await rolesPage.deleteButton.click();
		};

		await rolesPage.goto();

		await rolesPage.rolesTable.changeView('Table');

		await copyRole('Account Administrator', 'Account');
		await copyRole('Asset Library Member', 'Asset Library');
		await copyRole('Guest', 'Regular');
		await copyRole('Organization Administrator', 'Organization');
		await copyRole('Site Member', 'Site');
	}
);
