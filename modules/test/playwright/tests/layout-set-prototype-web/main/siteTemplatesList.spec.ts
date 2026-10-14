/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {dataApiHelpersTest} from '../../../fixtures/dataApiHelpersTest';
import {loginTest} from '../../../fixtures/loginTest';

const test = mergeTests(dataApiHelpersTest, loginTest());

test(
	'Site Templates list shows a name that matches a language key as typed',
	{tag: '@LPD-109398'},
	async ({apiHelpers, page}) => {

		// Create a Site Template named after a language key

		const siteTemplateName = 'dispatch';

		const layoutSetPrototype =
			await apiHelpers.jsonWebServicesLayoutSetPrototype.addLayoutSetPrototypes(
				{name: siteTemplateName}
			);

		apiHelpers.data.push({
			id: layoutSetPrototype.layoutSetPrototypeId,
			type: 'layoutSetPrototype',
		});

		// Every view shows the name rather than the language key's value

		for (const displayStyle of ['descriptive', 'icon', 'list']) {
			await page.goto(
				`/group/guest/~/control_panel/manage?p_p_id=com_liferay_layout_set_prototype_web_portlet_LayoutSetPrototypePortlet&_com_liferay_layout_set_prototype_web_portlet_LayoutSetPrototypePortlet_displayStyle=${displayStyle}`
			);

			await expect(
				page
					.locator('#main-content')
					.getByText(siteTemplateName, {exact: true})
					.first()
			).toBeVisible();

			await expect(
				page.locator('#main-content').getByText('Job Scheduler')
			).toBeHidden();
		}
	}
);
