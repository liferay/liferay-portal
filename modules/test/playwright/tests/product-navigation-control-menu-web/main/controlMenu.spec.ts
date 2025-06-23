/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {dataApiHelpersTest} from '../../../fixtures/dataApiHelpersTest';
import {isolatedSiteTest} from '../../../fixtures/isolatedSiteTest';
import {loginTest} from '../../../fixtures/loginTest';
import {siteSettingsPagesTest} from '../../../fixtures/siteSettingsPagesTest';
import {TRole} from '../../../helpers/HeadlessAdminUserApiHelper';
import {liferayConfig} from '../../../liferay.config';
import getRandomString from '../../../utils/getRandomString';
import {
	performLoginViaApi,
	performLogout,
	userData,
} from '../../../utils/performLogin';

const test = mergeTests(
	dataApiHelpersTest,
	isolatedSiteTest,
	loginTest(),
	siteSettingsPagesTest
);

test(
	'Verify role-based Control Menu access and direct admin page visibility',
	{tag: '@LPD-55260'},
	async ({apiHelpers, page, site, siteSettingsPage}) => {
		let viewSiteAdminRole: TRole;
		let user: TUserAccount;

		const companyId = await page.evaluate(() => {
			return Liferay.ThemeDisplay.getCompanyId();
		});

		await test.step('Create role that can view the site administration and assign it to a new user', async () => {
			viewSiteAdminRole = await apiHelpers.headlessAdminUser.postRole({
				name: getRandomString(),
				rolePermissions: [
					{
						actionIds: ['VIEW_SITE_ADMINISTRATION'],
						primaryKey: companyId,
						resourceName: 'com.liferay.portal.kernel.model.Group',
						scope: 1,
					},
				],
				roleType: 'regular',
			});

			user = await apiHelpers.headlessAdminUser.postUserAccount();

			userData[user.alternateName] = {
				name: user.givenName,
				password: 'test',
				surname: user.familyName,
			};

			await apiHelpers.headlessAdminUser.assignUserToRole(
				viewSiteAdminRole.externalReferenceCode,
				user.id
			);
		});

		await test.step('Add user to site', async () => {
			const siteRole =
				await apiHelpers.headlessAdminUser.getRoleByName('Site Member');

			await apiHelpers.headlessAdminUser.assignUserToSite(
				siteRole.id,
				site.id,
				user.id
			);
		});

		await test.step('Enable Menu Access configuration', async () => {
			await siteSettingsPage.goToSiteSetting(
				'Site Configuration',
				'Menu Access',
				site.friendlyUrlPath
			);

			await page
				.getByRole('checkbox', {name: 'Show Control Menu by Role'})
				.check();

			await siteSettingsPage.saveConfiguration();
		});

		await test.step('Assert that there are no remove buttons for the Administrator and Site Administrator roles', async () => {
			await expect(
				page.getByText('Administrator', {exact: true})
			).toBeVisible();

			await expect(
				page.getByText('Site Administrator', {exact: true})
			).toBeVisible();

			await expect(page.getByLabel('Remove')).toBeHidden();
		});

		await test.step('Select the new role to allow accessing the control menu', async () => {
			await page.getByRole('button', {name: 'Select'}).click();

			const frameLocator = page.frameLocator(
				'[id="_com_liferay_site_admin_web_portlet_SiteSettingsPortlet_selectRole>_iframe_"]'
			);

			await frameLocator
				.locator(
					`input[type="checkbox"][value="${viewSiteAdminRole.id}"]`
				)
				.check();

			await page.getByRole('button', {exact: true, name: 'Add'}).click();

			await siteSettingsPage.saveConfiguration();
		});

		await test.step('Asser that the new user can see the control menu for the associated site', async () => {
			await performLogout(page);

			await performLoginViaApi({page, screenName: user.alternateName});

			await page.goto(
				`${liferayConfig.environment.baseUrl}/web${site.friendlyUrlPath}`
			);

			await expect(page.getByLabel('Control Menu')).toBeVisible();
		});

		await test.step('Remove the control menu access role from the new user', async () => {
			await performLogout(page);

			await performLoginViaApi({page, screenName: 'test'});

			await apiHelpers.headlessAdminUser.deleteUserRole(
				viewSiteAdminRole.externalReferenceCode,
				user.id
			);
		});

		await test.step('Create a new role that has access to a control panel page', async () => {
			const viewControlPanelRole =
				await apiHelpers.headlessAdminUser.postRole({
					name: getRandomString(),
					rolePermissions: [
						{
							actionIds: ['ACCESS_IN_CONTROL_PANEL'],
							primaryKey: companyId,
							resourceName:
								'com_liferay_journal_web_portlet_JournalPortlet',
							scope: 1,
						},
					],
					roleType: 'regular',
				});

			await apiHelpers.headlessAdminUser.assignUserToRole(
				viewControlPanelRole.externalReferenceCode,
				user.id
			);
		});

		await test.step('Assert that the new user no longer see the control menu', async () => {
			await performLogout(page);

			await performLoginViaApi({page, screenName: user.alternateName});

			await page.goto(
				`${liferayConfig.environment.baseUrl}/web${site.friendlyUrlPath}`
			);

			await expect(page.getByLabel('Control Menu')).toBeHidden();
		});

		await test.step('Assert that the new user can see the control menu when in a control panel page', async () => {
			await page.goto(
				`/group${site.friendlyUrlPath}/~/control_panel/manage?p_p_id=com_liferay_journal_web_portlet_JournalPortlet`
			);

			await expect(page.getByLabel('Control Menu')).toBeVisible();
		});
	}
);
