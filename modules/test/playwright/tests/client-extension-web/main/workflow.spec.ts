/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {loginTest} from '../../../fixtures/loginTest';

const test = mergeTests(loginTest());

test(
	`Verify that Client Extension is not available in the workflow page`,
	{tag: '@LPS-141280'},
	async ({page}) => {
		await page.goto(
			'/group/guest/~/control_panel/manage?p_p_id=com_liferay_portal_workflow_web_internal_portlet_SiteAdministrationWorkflowPortlet'
		);

		await page.getByPlaceholder('Search for').click();
		await page.getByPlaceholder('Search for').fill('Client Extension');
		await page.getByPlaceholder('Search for').press('Enter');

		await expect(page.getByText('No entries were found.')).toBeVisible();
	}
);
