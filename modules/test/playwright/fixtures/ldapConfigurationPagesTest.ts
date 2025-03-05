/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import test from '@playwright/test';

import {LdapConfigurationPage} from '../pages/portal-security-ldap/LdapConfigurationPage';
import {LdapServerPage} from '../pages/portal-security-ldap/LdapServerPage';

const ldapConfigurationPagesTest = test.extend<{
	ldapConfigurationPage: LdapConfigurationPage;
	ldapServerPage: LdapServerPage;
}>({
	ldapConfigurationPage: async ({page}, use) => {
		await use(new LdapConfigurationPage(page));
	},
	ldapServerPage: async ({page}, use) => {
		await use(new LdapServerPage(page));
	},
});

export {ldapConfigurationPagesTest};
