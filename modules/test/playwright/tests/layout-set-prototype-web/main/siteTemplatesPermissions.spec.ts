/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {dataApiHelpersTest} from '../../../fixtures/dataApiHelpersTest';
import {loginTest} from '../../../fixtures/loginTest';
import {portletConfigurationPermissionsPageTest} from '../../../fixtures/portletConfigurationPermissionsPagesTest';
import {clickAndExpectToBeVisible} from '../../../utils/clickAndExpectToBeVisible';
import getRandomString from '../../../utils/getRandomString';
import {layoutSetPrototypePageTest} from './fixtures/layoutSetPrototypePageTest';

const test = mergeTests(
	dataApiHelpersTest,
	layoutSetPrototypePageTest,
	loginTest(),
	portletConfigurationPermissionsPageTest
);

test(
	'Permissions show the Site Template name as typed',
	{tag: '@LPD-109398'},
	async ({
		apiHelpers,
		layoutSetPrototypePage,
		page,
		portletConfigurationPermissionsPage,
	}) => {

		// Create a Site Template whose name contains markup

		const siteTemplateName = `R&D <i>SiteTemplate-${getRandomString()}</i>`;

		const layoutSetPrototype =
			await apiHelpers.jsonWebServicesLayoutSetPrototype.addLayoutSetPrototypes(
				{name: siteTemplateName}
			);

		apiHelpers.data.push({
			id: layoutSetPrototype.layoutSetPrototypeId,
			type: 'layoutSetPrototype',
		});

		// Create a role that can view every Site Template, so its permission
		// is preselected and explained with the Site Template name

		const companyId = await page.evaluate(() => {
			return Liferay.ThemeDisplay.getCompanyId();
		});

		const roleName = 'Role-' + getRandomString();

		await apiHelpers.headlessAdminUser.postRole({
			name: roleName,
			rolePermissions: [
				{
					actionIds: ['VIEW'],
					primaryKey: companyId,
					resourceName:
						'com.liferay.portal.kernel.model.LayoutSetPrototype',
					scope: 1,
				},
			],
		});

		// Open the Site Template permissions from the Site Templates list

		await page.goto(
			'/group/guest/~/control_panel/manage?p_p_id=com_liferay_layout_set_prototype_web_portlet_LayoutSetPrototypePortlet&_com_liferay_layout_set_prototype_web_portlet_LayoutSetPrototypePortlet_displayStyle=list'
		);

		await clickAndExpectToBeVisible({
			target: portletConfigurationPermissionsPage.permissionsMenuItem,
			trigger: layoutSetPrototypePage.rowActions(siteTemplateName),
		});

		await portletConfigurationPermissionsPage.permissionsMenuItem.click();

		// The preselected permission explains itself with the name as typed

		await expect(
			portletConfigurationPermissionsPage.permissionsFrame.getByRole(
				'checkbox',
				{
					name: `Give View permission to users with the ${roleName} role.`,
				}
			)
		).toHaveAttribute(
			'title',
			`${roleName} is allowed to do action "View" in all items of type ${siteTemplateName} in this portal instance. Use the roles administration tool if you want to change this.`
		);
	}
);
