/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {ldapConfigurationPagesTest} from '../../../fixtures/ldapConfigurationPagesTest';
import {loginTest} from '../../../fixtures/loginTest';
import getRandomString from '../../../utils/getRandomString';

export const test = mergeTests(loginTest(), ldapConfigurationPagesTest);

test('LPD-86301 FIPS: saving an LDAP server with a non-ldaps:// base provider URL surfaces the localized error in the UI', async ({
	ldapConfigurationPage,
	ldapServerPage,
}) => {
	const serverName = `fips-${getRandomString()}`;
	const badProviderURL = 'ldap://example.com:389';

	await test.step('Open the Add LDAP Server form', async () => {
		await ldapConfigurationPage.addLdapServer();
	});

	await test.step('Fill the form with a non-ldaps:// URL', async () => {
		await ldapServerPage.serverName.waitFor();
		await ldapServerPage.serverName.fill(serverName);
		await ldapServerPage.baseProviderUrl.fill(badProviderURL);
	});

	await test.step('Submit the form', async () => {
		await ldapServerPage.saveButton.click();
	});

	await test.step('Assert the FIPS-specific error message renders', async () => {
		const errorMessage = ldapServerPage.page.getByText(
			`FIPS mode requires the LDAP base provider URL to use the "ldaps://" scheme: "${badProviderURL}".`
		);

		await expect(errorMessage).toBeVisible();
	});

	await test.step('Assert the user is back on the LDAP server edit form so they can retry', async () => {
		await expect(ldapServerPage.serverName).toBeVisible();
		await expect(ldapServerPage.baseProviderUrl).toBeVisible();
	});
});
