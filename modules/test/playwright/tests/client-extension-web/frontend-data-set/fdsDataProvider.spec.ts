/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {dataApiHelpersTest} from '../../../fixtures/dataApiHelpersTest';
import {isolatedSiteTest} from '../../../fixtures/isolatedSiteTest';
import {loginTest} from '../../../fixtures/loginTest';
import {pagesAdminPagesTest} from '../../../fixtures/pagesAdminPagesTest';
import {siteSettingsPagesTest} from '../../../fixtures/siteSettingsPagesTest';
import {usersAndOrganizationsPagesTest} from '../../../fixtures/usersAndOrganizationsPagesTest';
import {clientExtensionsPageTest} from '../fixtures/clientExtensionsPageTest';

const test = mergeTests(
	clientExtensionsPageTest,
	dataApiHelpersTest,
	isolatedSiteTest,
	loginTest({screenName: 'demo.unprivileged'}),
	pagesAdminPagesTest,
	siteSettingsPagesTest,
	usersAndOrganizationsPagesTest
);

test('Check users without permission cannot fetch client extension data', async ({
	page,
	request,
}) => {
	const groupId = await page.evaluate(() =>
		Liferay.ThemeDisplay.getScopeGroupId()
	);

	const response = await request.get(
		`/o/frontend-data-set-taglib/app/data-set/com_liferay_client_extension_web_internal_portlet_ClientExtensionAdminPortlet-clientExtensionTypes/com_liferay_client_extension_web_internal_portlet_ClientExtensionAdminPortlet-clientExtensionTypes?groupId=${groupId}`
	);

	expect(response.status()).toBe(403);
});
