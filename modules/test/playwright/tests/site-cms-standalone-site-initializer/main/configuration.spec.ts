/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {loginTest} from '../../../fixtures/loginTest';

const test = mergeTests(loginTest());

// The categories the standalone CMS keeps visible (CMSConfigurationCategoryShowFilter).

const VISIBLE_CATEGORY_NAMES = new Set([
	'AI Creator',
	'Accessibility',
	'Batch Engine',
	'Comments',
	'Community Tools',
	'Default Permissions',
	'Documents and Media',
	'Email',
	'Feature Flags',
	'Instance Configuration',
	'LDAP',
	'Localization',
	'Login',
	'Marketplace',
	'OAuth 2',
	'Object',
	'SCIM',
	'Security Tools',
	'SSO',
	'Translation',
	'User Authentication',
	'Web API',
]);

const SETTINGS_URLS = {
	'instance settings':
		'/group/control_panel/manage?p_p_id=com_liferay_configuration_admin_web_portlet_InstanceSettingsPortlet',
	'system settings':
		'/group/control_panel/~/control_panel/manage?p_p_id=com_liferay_configuration_admin_web_portlet_SystemSettingsPortlet',
};

for (const [label, url] of Object.entries(SETTINGS_URLS)) {
	test(
		`only shows allowlisted configuration categories in ${label}`,
		{tag: '@LPD-94604'},
		async ({page}) => {
			await page.goto(url);

			const categoryLinks = page.locator('.list-group-card-item-text');

			await expect(categoryLinks.first()).toBeVisible();

			const categoryNames = (await categoryLinks.allInnerTexts()).map(
				(name) => name.trim()
			);

			expect(categoryNames.length).toBeGreaterThan(0);

			// No category outside the standalone CMS allowlist leaks through

			for (const categoryName of categoryNames) {
				expect(VISIBLE_CATEGORY_NAMES).toContain(categoryName);
			}

			// A few representative allowlisted categories are present

			expect(categoryNames).toContain('Login');
			expect(categoryNames).toContain('Web API');
			expect(categoryNames).toContain('Documents and Media');
		}
	);
}
