/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {ldapConfigurationPagesTest} from '../../../fixtures/ldapConfigurationPagesTest';
import {loginTest} from '../../../fixtures/loginTest';
import {clickAndExpectToBeVisible} from '../../../utils/clickAndExpectToBeVisible';
import getRandomString from '../../../utils/getRandomString';
import {waitForAlert} from '../../../utils/waitForAlert';

export const test = mergeTests(loginTest(), ldapConfigurationPagesTest);

const NOT_ALLOWED_ALGORITHMS = [
	'BCRYPT',
	'MD2',
	'MD5',
	'SHA',
	'SSHA',
	'UFC-CRYPT',
];

const PRESETS = [
	['Apache Directory Server', 'ldaps://localhost:10636'],
	['Fedora Directory Server', 'ldaps://localhost:19636'],
	['Microsoft Active Directory Server', 'ldaps://localhost:636'],
	['Novell eDirectory', 'ldaps://localhost:636'],
	['OpenLDAP', 'ldaps://localhost:636'],
] as const;

test(
	'New LDAP server form defaults the base provider URL to the ldaps:// scheme',
	{tag: '@LPD-86301'},
	async ({ldapConfigurationPage, ldapServerPage}) => {
		await test.step('Open the Add LDAP Server form', async () => {
			await ldapConfigurationPage.addLdapServer();
		});

		await test.step('Assert the base provider URL defaults to the ldaps:// scheme', async () => {
			await ldapServerPage.serverName.waitFor();

			await expect(ldapServerPage.baseProviderUrl).toHaveValue(
				'ldaps://localhost:10636'
			);
		});
	}
);

test(
	'Selecting a preset applies the ldaps:// scheme and the secure port to the base provider URL',
	{tag: '@LPD-86301'},
	async ({ldapConfigurationPage, ldapServerPage}) => {
		await test.step('Open the Add LDAP Server form', async () => {
			await ldapConfigurationPage.addLdapServer();

			await ldapServerPage.serverName.waitFor();
		});

		for (const [preset, baseProviderURL] of PRESETS) {
			await test.step(`Assert the ${preset} preset uses ${baseProviderURL}`, async () => {
				await ldapServerPage.page
					.getByText(preset)
					.getByRole('radio')
					.check();

				await expect(ldapServerPage.baseProviderUrl).toHaveValue(
					baseProviderURL
				);
			});
		}
	}
);

test(
	'Saving an LDAP server with an insecure ldap:// base provider URL shows the FIPS validation error',
	{tag: '@LPD-86301'},
	async ({ldapConfigurationPage, ldapServerPage}) => {
		const baseProviderURL = `ldap://${getRandomString()}`;

		await test.step('Open the Add LDAP Server form', async () => {
			await ldapConfigurationPage.addLdapServer();
		});

		await test.step('Fill the form with an insecure ldap:// URL', async () => {
			await ldapServerPage.serverName.waitFor();

			await ldapServerPage.serverName.fill(`fips-${getRandomString()}`);
			await ldapServerPage.baseProviderUrl.fill(baseProviderURL);
		});

		await test.step('Submit the form and assert the FIPS validation error names the URL and the required scheme', async () => {
			await clickAndExpectToBeVisible({
				target: ldapServerPage.page.getByText(
					`The base provider URL "${baseProviderURL}" must use the "ldaps://" scheme in FIPS mode.`
				),
				trigger: ldapServerPage.saveButton,
			});
		});
	}
);

test(
	'The LDAP password encryption algorithm offers only FIPS approved algorithms',
	{tag: '@LPD-86301'},
	async ({ldapConfigurationPage}) => {
		await test.step('Open the LDAP General settings', async () => {
			await ldapConfigurationPage.goTo();

			await ldapConfigurationPage.passwordEncryptionAlgorithm.waitFor();
		});

		await test.step('Assert the FIPS approved algorithms are offered', async () => {
			for (const algorithm of ['SHA-256', 'SHA-384']) {
				await expect(
					ldapConfigurationPage.passwordEncryptionAlgorithm.getByRole(
						'option',
						{name: algorithm}
					)
				).toHaveCount(1);
			}
		});

		await test.step('Assert the algorithms that are not FIPS approved are withheld', async () => {
			for (const algorithm of NOT_ALLOWED_ALGORITHMS) {
				await expect(
					ldapConfigurationPage.passwordEncryptionAlgorithm.getByRole(
						'option',
						{exact: true, name: algorithm}
					)
				).toHaveCount(0);
			}
		});
	}
);

test(
	'The LDAP test pages refuse an insecure ldap:// base provider URL',
	{tag: '@LPD-86301'},
	async ({ldapConfigurationPage, ldapServerPage}) => {
		const baseProviderURL = `ldap://${getRandomString()}:389`;
		const serverName = `fips-${getRandomString()}`;

		await test.step('Add an LDAP server with a compliant URL', async () => {
			await ldapConfigurationPage.addLdapServer();

			await ldapServerPage.serverName.waitFor();

			await ldapServerPage.serverName.fill(serverName);
			await ldapServerPage.baseProviderUrl.fill('ldaps://localhost:636');

			await ldapServerPage.saveButton.click();

			await waitForAlert(
				ldapServerPage.page,
				'Success:Your request completed successfully.'
			);
		});

		await test.step('Reopen it and enter an insecure URL', async () => {
			await ldapServerPage.viewLdapServer(serverName);

			await ldapServerPage.baseProviderUrl.fill(baseProviderURL);
		});

		for (const [name, button] of [
			['Connection', ldapServerPage.testLdapConnection],
			['Groups', ldapServerPage.testLdapGroups],
			['Users', ldapServerPage.testLdapUsers],
		] as const) {
			await test.step(`Assert Test LDAP ${name} reports the FIPS scheme error`, async () => {
				await clickAndExpectToBeVisible({
					target: ldapServerPage.page.getByText(
						`The base provider URL "${baseProviderURL}" must use the "ldaps://" scheme in FIPS mode.`
					),
					trigger: button,
				});

				await ldapServerPage.page.keyboard.press('Escape');
			});
		}

		await ldapServerPage.deleteLdapServer(serverName);
	}
);
