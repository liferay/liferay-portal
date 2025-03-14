/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';
import {exec} from 'child_process';

import {apiHelpersTest} from '../../fixtures/apiHelpersTest';
import {instanceSettingsPagesTest} from '../../fixtures/instanceSettingsPagesTest';
import {ldapConfigurationPagesTest} from '../../fixtures/ldapConfigurationPagesTest';
import {loginTest} from '../../fixtures/loginTest';
import {searchAdminPageTest} from '../../fixtures/searchAdminPageTest';
import {systemSettingsPageTest} from '../../fixtures/systemSettingsPageTest';
import {userGroupsPageTest} from '../../fixtures/userGroupsPageTest';
import {usersAndOrganizationsPagesTest} from '../../fixtures/usersAndOrganizationsPagesTest';
import {
	TLdapConfiguration,
	TLdapServer,
} from '../../helpers/LdapConfigurationHelper';
import {SystemSettingsPage} from '../../pages/configuration-admin-web/SystemSettingsPage';
import {clickAndExpectToBeVisible} from '../../utils/clickAndExpectToBeVisible';
import getRandomString from '../../utils/getRandomString';
import performLogin, {userData} from '../../utils/performLogin';
import {waitForAlert} from '../../utils/waitForAlert';

export const test = mergeTests(
	apiHelpersTest,
	loginTest(),
	instanceSettingsPagesTest,
	ldapConfigurationPagesTest,
	searchAdminPageTest,
	systemSettingsPageTest,
	usersAndOrganizationsPagesTest,
	userGroupsPageTest
);

const LDAP_ARGS = '-cx -D "cn=admin,dc=example,dc=com" -w "secret" -f';

const LDAP_GROUP_1 = 'ldapgroup1';
const LDAP_GROUP_2 = 'ldapgroup2';
const LDAP_GROUP_3 = 'ldapgroup3';
const LDAP_GROUP_3_MODIFIED = 'ldapgroup3modified';
const LDAP_GROUP_4_A = 'ldapgroup4a';
const LDAP_GROUP_4_B = 'ldapgroup4b';

const LDAP_LDIF_DIR = './tests/portal-security-ldap/dependencies/';

const LDAP_USER_1: TUserAccount = {
	alternateName: 'ldapuser1',
	emailAddress: 'ldapuser1@liferay.com',
	familyName: 'last',
	givenName: 'first',
	password: 'test',
};

const LDAP_USER_2: TUserAccount = {
	alternateName: 'ldapuser2',
	emailAddress: 'ldapuser2@liferay.com',
	familyName: 'last',
	givenName: 'first',
	password: 'test',
};

const LDAP_USER_3: TUserAccount = {
	alternateName: 'ldapuser3',
	emailAddress: 'ldapuser3@liferay.com',
	familyName: 'last',
	givenName: 'first',
	password: 'test',
};

const LDAP_USER_3_MODIFIED: TUserAccount = {
	alternateName: 'ldapuser3modified',
	emailAddress: 'ldapuser3@liferay.com',
	familyName: 'lastmodified',
	givenName: 'firstmodified',
	password: 'testmodified',
};

const LDAP_USER_4: TUserAccount = {
	alternateName: 'ldapuser4',
	emailAddress: 'ldapuser4@liferay.com',
	familyName: 'last',
	givenName: 'first',
	password: 'test',
};

test.afterAll(async ({browser}) => {
	const page = await browser.newPage();

	await performLogin(page, 'test');

	const systemSettingsPage = new SystemSettingsPage(page);

	await test.step('Reset System Settings LDAP configuration', async () => {
		await resetLdapImportSystemSettings(systemSettingsPage);
	});
});

test.afterEach(
	async ({
		apiHelpers,
		ldapConfigurationPage,
		ldapServerPage,
		userGroupsPage,
	}) => {
		await test.step('Delete LDAP servers from portal', async () => {
			await ldapServerPage.deleteLdapServers();
		});

		await test.step('Reset LDAP Instance Settings', async () => {
			await ldapConfigurationPage.resetLdapConfiguration();
		});

		await test.step('Delete LDAP users from portal if present', async () => {
			for (const ldapUser of [
				LDAP_USER_1,
				LDAP_USER_2,
				LDAP_USER_3,
				LDAP_USER_3_MODIFIED,
				LDAP_USER_4,
			]) {
				const user =
					await apiHelpers.headlessAdminUser.getUserAccountByEmailAddress(
						ldapUser.emailAddress
					);

				if (user.id !== undefined) {
					await apiHelpers.headlessAdminUser.deleteUserAccount(
						Number(user.id)
					);
				}
			}
		});

		await test.step('Delete LDAP groups from portal if present', async () => {
			await userGroupsPage.goto();

			const selectAllCheckbox = userGroupsPage.page.getByLabel(
				'Select All Items on the Page'
			);

			await selectAllCheckbox.waitFor();

			await userGroupsPage.page.waitForTimeout(1000);

			if (await selectAllCheckbox.isEnabled()) {
				await selectAllCheckbox.click();

				userGroupsPage.page.once('dialog', async (dialog) => {
					dialog.accept();
				});

				await userGroupsPage.page
					.getByRole('button', {name: 'Delete'})
					.click();

				await waitForAlert(
					userGroupsPage.page,
					`Success:Your request completed successfully.`
				);
			}
		});
	}
);

test.beforeAll(async ({browser}) => {
	const page = await browser.newPage();

	await performLogin(page, 'test');

	const systemSettingsPage = new SystemSettingsPage(page);

	// The import interval at the System Settings level controls the scheduled
	// job trigger.  Set it low so we can trigger imports during tests.

	await test.step('Set LDAP Import Interval to 1 at System level', async () => {
		await resetLdapImportSystemSettings(systemSettingsPage);

		await systemSettingsPage.page.getByLabel('Import Interval').fill('1');

		await systemSettingsPage.page
			.getByRole('button', {name: 'Save'})
			.click();

		await waitForAlert(
			systemSettingsPage.page,
			`Success:Your request completed successfully.`
		);
	});

	// Add LDAP user info to userData so we can authenticate via performLogin

	for (const ldapUser of [
		LDAP_USER_1,
		LDAP_USER_2,
		LDAP_USER_3,
		LDAP_USER_4,
	]) {
		userData[ldapUser.alternateName] = {
			name: ldapUser.givenName,
			password: ldapUser.password,
			surname: ldapUser.familyName,
		};
	}

	// The modified user is a special case, because it uses the existing email
	// address, but the data and credentials are different.  We can workaround
	// the performLogin constraints by using the email as the key, and passing
	// in a blank 'domain' argument.

	userData[LDAP_USER_3_MODIFIED.emailAddress] = {
		name: LDAP_USER_3_MODIFIED.givenName,
		password: LDAP_USER_3_MODIFIED.password,
		surname: LDAP_USER_3_MODIFIED.familyName,
	};
});

test('LPD-47223 AC1 TC1: Verify LDAP import via authentication imports user attributes and user groups, but only for the user being authenticated', async ({
	browser,
	editUserPage,
	ldapConfigurationPage,
	ldapServerPage,
	userGroupsPage,
	usersAndOrganizationsPage,
}) => {
	const ldapServer: TLdapServer = {
		defaultValues: 'OpenLDAP',
		principal: 'cn=admin,dc=example,dc=com',
		serverName: getRandomString(),
	};

	await test.step('Add LDAP server', async () => {
		await ldapServerPage.addLdapServer(ldapServer);
	});

	await test.step('Verify LDAP server connection tests display two users and two groups', async () => {
		await ldapServerPage.viewLdapServer(ldapServer.serverName, false);

		await ldapServerPage.testLdapUsers.click();

		await expect(
			await ldapServerPage.page.getByRole('cell', {
				exact: true,
				name: LDAP_USER_1.alternateName,
			})
		).toBeVisible();

		await expect(
			await ldapServerPage.page.getByRole('cell', {
				exact: true,
				name: LDAP_USER_2.alternateName,
			})
		).toBeVisible();

		await ldapServerPage.closeButton.click();

		await ldapServerPage.testLdapGroups.click();

		await expect(
			await ldapServerPage.page.getByRole('cell', {
				exact: true,
				name: LDAP_GROUP_1,
			})
		).toBeVisible();

		await expect(
			await ldapServerPage.page.getByRole('cell', {
				exact: true,
				name: LDAP_GROUP_2,
			})
		).toBeVisible();

		await ldapServerPage.closeButton.click();

		await ldapServerPage.cancelButton.click();
	});

	await test.step('Enable LDAP, but prevent bulk import', async () => {
		const ldapConfiguration: TLdapConfiguration = {
			enableImport: true,
			enabled: true,
			importInterval: 0,
		};

		await ldapConfigurationPage.updateLDAPConfiguration(ldapConfiguration);
	});

	await test.step(`Authenticate with ${LDAP_USER_2.alternateName}`, async () => {
		const page = await browser.newPage();

		await performLogin(page, LDAP_USER_2.alternateName);
	});

	await test.step(`Assert only ${LDAP_USER_2.alternateName} was imported`, async () => {
		await usersAndOrganizationsPage.goToUsers(false);

		await expect(
			await usersAndOrganizationsPage.usersTableCell(
				LDAP_USER_1.alternateName
			)
		).toBeHidden();

		await expect(
			await usersAndOrganizationsPage.usersTableCell(
				LDAP_USER_2.alternateName
			)
		).toBeVisible();
	});

	await test.step('Assert user data was imported correctly', async () => {
		await (
			await usersAndOrganizationsPage.usersTableRowLink(
				LDAP_USER_2.alternateName
			)
		).click();

		await expect(editUserPage.emailAddressInput).toHaveValue(
			LDAP_USER_2.emailAddress
		);

		await expect(editUserPage.firstNameInput).toHaveValue(
			LDAP_USER_2.givenName
		);

		await expect(editUserPage.lastNameInput).toHaveValue(
			LDAP_USER_2.familyName
		);

		await expect(editUserPage.screenNameInput).toHaveValue(
			LDAP_USER_2.alternateName
		);
	});

	await test.step('Assert user membership data was imported correctly', async () => {
		await editUserPage.membershipsLink.click();

		await expect(
			(
				await editUserPage.membershipsUserGroupsTableRow(
					0,
					LDAP_GROUP_2,
					true
				)
			).row
		).toBeVisible();
	});

	await test.step(`Assert only ${LDAP_GROUP_2} was imported`, async () => {
		await userGroupsPage.goto();

		await expect(
			await userGroupsPage.page.getByRole('cell', {
				exact: true,
				name: LDAP_GROUP_1,
			})
		).toBeHidden();

		await expect(
			await userGroupsPage.page.getByRole('cell', {
				exact: true,
				name: LDAP_GROUP_2,
			})
		).toBeVisible();
	});
});

test('LPD-47223 AC1 TC2: Verify LDAP bulk import updates user information and membership', async ({
	browser,
	editUserPage,
	ldapConfigurationPage,
	ldapServerPage,
	searchAdminPage,
	usersAndOrganizationsPage,
}) => {
	const ldapServer: TLdapServer = {
		defaultValues: 'OpenLDAP',
		principal: 'cn=admin,dc=example,dc=com',
		serverName: getRandomString(),
	};

	await test.step('Add LDAP server', async () => {
		await ldapServerPage.addLdapServer(ldapServer);
	});

	await test.step(`Verify LDAP server connection tests display ${LDAP_USER_3.alternateName} but not ${LDAP_USER_3_MODIFIED.alternateName}`, async () => {
		await ldapServerPage.viewLdapServer(ldapServer.serverName, false);

		await ldapServerPage.testLdapUsers.click();

		await expect(
			await ldapServerPage.page.getByRole('cell', {
				exact: true,
				name: LDAP_USER_3.alternateName,
			})
		).toBeVisible();

		await expect(
			await ldapServerPage.page.getByRole('cell', {
				exact: true,
				name: LDAP_USER_3_MODIFIED.alternateName,
			})
		).not.toBeVisible();

		await ldapServerPage.closeButton.click();

		await ldapServerPage.cancelButton.click();
	});

	await test.step('Enable LDAP and wait for 1 minute, so import interval can be reached, triggering a bulk import', async () => {
		const ldapConfiguration: TLdapConfiguration = {
			enableImport: true,
			enabled: true,
			importInterval: 1,
		};

		await ldapConfigurationPage.updateLDAPConfiguration(ldapConfiguration);

		await ldapConfigurationPage.page.waitForTimeout(60 * 1000);
	});

	await test.step('Assert user data and membership was imported correctly', async () => {
		await usersAndOrganizationsPage.goToUsers(false);

		await (
			await usersAndOrganizationsPage.usersTableRowLink(
				LDAP_USER_3.alternateName
			)
		).click();

		await expect(editUserPage.emailAddressInput).toHaveValue(
			LDAP_USER_3.emailAddress
		);

		await expect(editUserPage.firstNameInput).toHaveValue(
			LDAP_USER_3.givenName
		);

		await expect(editUserPage.lastNameInput).toHaveValue(
			LDAP_USER_3.familyName
		);

		await expect(editUserPage.screenNameInput).toHaveValue(
			LDAP_USER_3.alternateName
		);

		await editUserPage.membershipsLink.click();

		await expect(
			await editUserPage.page.getByRole('cell', {
				exact: true,
				name: LDAP_GROUP_3,
			})
		).toBeVisible();

		await expect(
			await editUserPage.page.getByRole('cell', {
				exact: true,
				name: LDAP_GROUP_3_MODIFIED,
			})
		).not.toBeVisible();
	});

	await test.step('Change user data and memberships on LDAP server by removing the user and re-adding them with updated data.  The email will stay the same, so the lookup is the same on the portal side', async () => {
		await exec(
			`ldapdelete ${LDAP_ARGS} ${LDAP_LDIF_DIR}removeUser.ldif`,
			(error) => {
				if (error) {
					console.error(`Error during ldapdelete: ${error.message}`);
					test.fail();
				}
			}
		);

		await exec(
			`ldapadd ${LDAP_ARGS} ${LDAP_LDIF_DIR}addModifiedUser.ldif`,
			(error) => {
				if (error) {
					console.error(`Error during ldapadd: ${error.message}`);
					test.fail();
				}
			}
		);
	});

	await test.step('Wait one minute for import interval to be reached, then reindex users and user groups', async () => {
		await searchAdminPage.page.waitForTimeout(60 * 1000);
		await searchAdminPage.goto();
		await searchAdminPage.goToIndexActionsTab();
		await searchAdminPage.reindexIndexActionsItem('User');
		await searchAdminPage.reindexIndexActionsItem('User Group');
	});

	await test.step('Assert user data and membership was updated correctly', async () => {
		await usersAndOrganizationsPage.goToUsers(false);

		await expect(
			await usersAndOrganizationsPage.usersTableCell(
				LDAP_USER_3.alternateName
			)
		).toBeHidden();

		await (
			await usersAndOrganizationsPage.usersTableRowLink(
				LDAP_USER_3_MODIFIED.alternateName
			)
		).click();

		await expect(editUserPage.firstNameInput).toHaveValue(
			LDAP_USER_3_MODIFIED.givenName
		);

		await expect(editUserPage.lastNameInput).toHaveValue(
			LDAP_USER_3_MODIFIED.familyName
		);

		await expect(editUserPage.screenNameInput).toHaveValue(
			LDAP_USER_3_MODIFIED.alternateName
		);

		await editUserPage.membershipsLink.click();

		await expect(
			await editUserPage.page.getByRole('cell', {
				exact: true,
				name: LDAP_GROUP_3,
			})
		).not.toBeVisible();

		await expect(
			await editUserPage.page.getByRole('cell', {
				exact: true,
				name: LDAP_GROUP_3_MODIFIED,
			})
		).toBeVisible();
	});

	await test.step('Assert user password updated correctly', async () => {
		const page = await browser.newPage();

		await performLogin(
			page,
			LDAP_USER_3_MODIFIED.emailAddress,
			undefined,
			''
		);
	});
});

test('LPD-47223 AC3 TC3 and AC3 TC4: Verify LDAP import via authentication with multiple matching LDAP servers imports/updates only user groups for the first matching LDAP server', async ({
	browser,
	editUserPage,
	ldapConfigurationPage,
	ldapServerPage,
	usersAndOrganizationsPage,
}) => {
	const ldapServerA: TLdapServer = {
		defaultValues: 'OpenLDAP',
		importSearchFilterGroup: `(&(objectClass=groupOfUniqueNames)(cn=${LDAP_GROUP_4_A}))`,
		principal: 'cn=admin,dc=example,dc=com',
		serverName: getRandomString(),
	};

	const ldapServerB: TLdapServer = {
		defaultValues: 'OpenLDAP',
		importSearchFilterGroup: `(&(objectClass=groupOfUniqueNames)(cn=${LDAP_GROUP_4_B}))`,
		principal: 'cn=admin,dc=example,dc=com',
		serverName: getRandomString(),
	};

	await test.step('Add the same LDAP server twice, but adjust the group import search filter so each entry adds a different group', async () => {
		await test.step('Add first LDAP server', async () => {
			await ldapServerPage.addLdapServer(ldapServerA);
		});

		await test.step(`Verify first LDAP server only imports ${LDAP_GROUP_4_A}`, async () => {
			await ldapServerPage.viewLdapServer(ldapServerA.serverName, false);

			await ldapServerPage.testLdapGroups.click();

			await expect(
				await ldapServerPage.page.getByRole('cell', {
					name: LDAP_GROUP_4_A,
				})
			).toBeVisible();

			await expect(
				await ldapServerPage.page.getByRole('cell', {
					name: LDAP_GROUP_4_B,
				})
			).not.toBeVisible();

			await ldapServerPage.closeButton.click();

			await ldapServerPage.cancelButton.click();
		});

		await test.step('Add second LDAP server', async () => {
			await ldapServerPage.addLdapServer(ldapServerB, false);
		});

		await test.step(`Verify second LDAP server only imports ${LDAP_GROUP_4_B}`, async () => {
			await ldapServerPage.viewLdapServer(ldapServerB.serverName, false);

			await ldapServerPage.testLdapGroups.click();

			await expect(
				await ldapServerPage.page.getByRole('cell', {
					name: LDAP_GROUP_4_B,
				})
			).toBeVisible();

			await expect(
				await ldapServerPage.page.getByRole('cell', {
					name: LDAP_GROUP_4_A,
				})
			).not.toBeVisible();

			await ldapServerPage.closeButton.click();

			await ldapServerPage.cancelButton.click();
		});
	});

	await test.step('Enable LDAP and wait for 1 minute, so import interval can be reached, triggering a bulk import', async () => {
		const ldapConfiguration: TLdapConfiguration = {
			enableImport: true,
			enabled: true,
			importInterval: 1,
		};

		await ldapConfigurationPage.updateLDAPConfiguration(ldapConfiguration);

		await ldapConfigurationPage.page.waitForTimeout(60 * 1000);
	});

	await test.step(`Assert ${LDAP_USER_4.alternateName} was imported`, async () => {
		await usersAndOrganizationsPage.goToUsers(false);

		await expect(
			await usersAndOrganizationsPage.usersTableCell(
				LDAP_USER_4.alternateName
			)
		).toBeVisible();
	});

	await test.step(`Assert ${LDAP_USER_4.alternateName} is a member of both groups`, async () => {
		await (
			await usersAndOrganizationsPage.usersTableRowLink(
				LDAP_USER_4.alternateName
			)
		).click();

		await editUserPage.membershipsLink.click();

		await expect(
			await editUserPage.page.getByRole('cell', {
				exact: true,
				name: LDAP_GROUP_4_A,
			})
		).toBeVisible();

		await expect(
			await editUserPage.page.getByRole('cell', {
				exact: true,
				name: LDAP_GROUP_4_A,
			})
		).toBeVisible();
	});

	await test.step('Keep LDAP enabled, but disable bulk import', async () => {
		const ldapConfiguration: TLdapConfiguration = {
			enableImport: true,
			enabled: true,
			importInterval: 0,
		};

		await ldapConfigurationPage.updateLDAPConfiguration(ldapConfiguration);
	});

	await test.step(`Remove ${LDAP_USER_4.alternateName} from both groups on the LDAP server`, async () => {
		await exec(
			`ldapmodify ${LDAP_ARGS} ${LDAP_LDIF_DIR}removeUserFromGroups.ldif`,
			(error) => {
				if (error) {
					console.error(`Error during ldapmodify: ${error.message}`);
					test.fail();
				}
			}
		);
	});

	await test.step(`Authenticate with ${LDAP_USER_4.alternateName}, triggering an import from LDAP server A only`, async () => {
		const page = await browser.newPage();

		await performLogin(page, LDAP_USER_4.alternateName);
	});

	await test.step(`Assert membership was revoked only for the first server's group`, async () => {
		await usersAndOrganizationsPage.goToUsers(false);

		await (
			await usersAndOrganizationsPage.usersTableRowLink(
				LDAP_USER_4.alternateName
			)
		).click();

		await editUserPage.membershipsLink.click();

		await expect(
			await editUserPage.page.getByRole('cell', {
				exact: true,
				name: LDAP_GROUP_4_A,
			})
		).toBeHidden();

		await expect(
			await editUserPage.page.getByRole('cell', {
				exact: true,
				name: LDAP_GROUP_4_B,
			})
		).toBeVisible();
	});

	await test.step('LPD-47223 AC3 TC3 End, AC3 TC4 Start: Verify if second ldap server is the first to match during authentication, only changes from that server will be reflected', async () => {
		await test.step(`Add ${LDAP_USER_4.alternateName} back to ${LDAP_GROUP_4_A} only`, async () => {
			await exec(
				`ldapmodify ${LDAP_ARGS} ${LDAP_LDIF_DIR}addUserToGroup.ldif`,
				(error) => {
					if (error) {
						console.error(
							`Error during ldapmodify: ${error.message}`
						);
						test.fail();
					}
				}
			);
		});

		await test.step(`Update first LDAP server so it no longer imports ${LDAP_USER_4.alternateName}`, async () => {
			ldapServerA.ignoreUserSearchFilterForAuthentication = false;
			ldapServerA.importSearchFilterUser =
				'(&(objectClass=inetOrgPerson)(cn=fakeuser))';

			await ldapServerPage.editLdapServer(ldapServerA);

			await test.step(`Verify first LDAP server still only imports ${LDAP_GROUP_4_A}, but not ${LDAP_USER_4.alternateName}`, async () => {
				await ldapServerPage.viewLdapServer(
					ldapServerA.serverName,
					false
				);

				await ldapServerPage.testLdapGroups.click();

				await expect(
					await ldapServerPage.page.getByRole('cell', {
						name: LDAP_GROUP_4_A,
					})
				).toBeVisible();

				await expect(
					await ldapServerPage.page.getByRole('cell', {
						name: LDAP_GROUP_4_B,
					})
				).not.toBeVisible();

				await ldapServerPage.closeButton.click();

				await ldapServerPage.testLdapUsers.click();

				await expect(
					await ldapServerPage.page.getByRole('cell', {
						exact: true,
						name: LDAP_USER_4.alternateName,
					})
				).not.toBeVisible();

				await ldapServerPage.closeButton.click();

				await ldapServerPage.cancelButton.click();
			});

			await test.step(`Authenticate with ${LDAP_USER_4.alternateName}, triggering an import from LDAP server B only`, async () => {
				const page = await browser.newPage();

				await performLogin(page, LDAP_USER_4.alternateName);
			});

			await test.step(`Assert membership was revoked for the second server's group, but not reinstated for the first`, async () => {
				await usersAndOrganizationsPage.goToUsers(false);

				await (
					await usersAndOrganizationsPage.usersTableRowLink(
						LDAP_USER_4.alternateName
					)
				).click();

				await editUserPage.membershipsLink.click();

				await expect(
					await editUserPage.page.getByRole('cell', {
						exact: true,
						name: LDAP_GROUP_4_A,
					})
				).toBeHidden();

				await expect(
					await editUserPage.page.getByRole('cell', {
						exact: true,
						name: LDAP_GROUP_4_B,
					})
				).toBeHidden();
			});
		});
	});
});

test('LPD-47428: Verify a single LDAP user can belong to multiple User Groups imported from LDAP', async ({
	browser,
	editUserPage,
	ldapConfigurationPage,
	ldapServerPage,
	usersAndOrganizationsPage,
}) => {
	const ldapServer1: TLdapServer = {
		defaultValues: 'OpenLDAP',
		importSearchFilterGroup: `(&(objectClass=groupOfUniqueNames)(cn=${LDAP_GROUP_1}))`,
		principal: 'cn=admin,dc=example,dc=com',
		serverName: getRandomString(),
	};

	const ldapServer2: TLdapServer = {
		defaultValues: 'OpenLDAP',
		importSearchFilterGroup: `(&(objectClass=groupOfUniqueNames)(cn=${LDAP_GROUP_2}))`,
		principal: 'cn=admin,dc=example,dc=com',
		serverName: getRandomString(),
	};

	await test.step('Add the same LDAP server twice, but adjust the group import search filter so each entry adds a different group', async () => {
		await test.step('Add first LDAP server', async () => {
			await ldapServerPage.addLdapServer(ldapServer1);
		});

		await test.step('Verify first LDAP server it only imports the first group', async () => {
			await ldapServerPage.viewLdapServer(ldapServer1.serverName, false);

			await ldapServerPage.testLdapGroups.click();

			await expect(
				await ldapServerPage.page.getByRole('cell', {
					name: LDAP_GROUP_1,
				})
			).toBeVisible();

			await expect(
				await ldapServerPage.page.getByRole('cell', {
					name: LDAP_GROUP_2,
				})
			).not.toBeVisible();

			await ldapServerPage.closeButton.click();

			await ldapServerPage.cancelButton.click();
		});

		await test.step('Add second LDAP server', async () => {
			await ldapServerPage.addLdapServer(ldapServer2, false);
		});

		await test.step('Verify second LDAP server it only imports the second group', async () => {
			await ldapServerPage.viewLdapServer(ldapServer2.serverName, false);

			await ldapServerPage.testLdapGroups.click();

			await expect(
				await ldapServerPage.page.getByRole('cell', {
					name: LDAP_GROUP_2,
				})
			).toBeVisible();

			await expect(
				await ldapServerPage.page.getByRole('cell', {
					name: LDAP_GROUP_1,
				})
			).not.toBeVisible();

			await ldapServerPage.closeButton.click();

			await ldapServerPage.cancelButton.click();
		});
	});

	await test.step('Enable LDAP and wait for 1 minute, so import interval can be reached, triggering a bulk import', async () => {
		const ldapConfiguration: TLdapConfiguration = {
			enableImport: true,
			enabled: true,
			importInterval: 1,
			importMethod: 'Group',
		};

		await ldapConfigurationPage.updateLDAPConfiguration(ldapConfiguration);

		await ldapConfigurationPage.page.waitForTimeout(60 * 1000);
	});

	await test.step('View User Groups associated with the LDAP user, and verify they were correctly imported', async () => {
		await usersAndOrganizationsPage.goToUsers(false);

		await (
			await usersAndOrganizationsPage.usersTableRowLink(
				LDAP_USER_1.alternateName
			)
		).click();

		await editUserPage.membershipsLink.click();

		await expect(
			(
				await editUserPage.membershipsUserGroupsTableRow(
					0,
					LDAP_GROUP_1,
					true
				)
			).row
		).toBeVisible();

		await expect(
			(
				await editUserPage.membershipsUserGroupsTableRow(
					0,
					LDAP_GROUP_2,
					true
				)
			).row
		).toBeVisible();
	});

	await test.step('Attempt login with ldap user, but use incorrect password.  This reproduces the bug described in LPD-47428.', async () => {
		const page = await browser.newPage();

		await page.goto('/');

		await page.getByRole('button', {name: 'Sign In'}).last().click();

		await page.getByLabel('Email Address').fill(LDAP_USER_1.emailAddress);
		await page.getByLabel('Password').fill('badPassword');

		await page.getByRole('button', {name: 'Sign In'}).last().click();

		await waitForAlert(page, 'Error:Authentication failed', {
			autoClose: false,
			type: 'danger',
		});
	});

	await test.step('Verify both User Groups are still associated with the LDAP user.', async () => {
		await editUserPage.page.reload();

		await expect(
			(
				await editUserPage.membershipsUserGroupsTableRow(
					0,
					LDAP_GROUP_1,
					true
				)
			).row
		).toBeVisible();

		await expect(
			(
				await editUserPage.membershipsUserGroupsTableRow(
					0,
					LDAP_GROUP_2,
					true
				)
			).row
		).toBeVisible();
	});
});

test('smoke: Add LDAP server, verify connection, users, and groups are mapped properly, edit LDAP server, then delete LDAP server', async ({
	ldapConfigurationPage,
	ldapServerPage,
}) => {
	const serverName = getRandomString();

	const ldapServer: TLdapServer = {
		defaultValues: 'OpenLDAP',
		principal: 'cn=admin,dc=example,dc=com',
		serverName,
	};

	await test.step('Add LDAP Server', async () => {
		await ldapServerPage.addLdapServer(ldapServer);
	});

	await test.step('Test LDAP Server connections', async () => {
		await ldapServerPage.viewLdapServer(serverName, false);

		await ldapServerPage.testLdapConnection.click();

		await expect(
			await ldapServerPage.page.getByText(
				'Liferay has successfully connected to the LDAP server'
			)
		).toBeVisible();

		await ldapServerPage.closeButton.click();

		await ldapServerPage.testLdapUsers.click();

		await expect(
			await ldapServerPage.page.getByText(
				'A subset of users has been displayed for you to review'
			)
		).toBeVisible();

		await ldapServerPage.closeButton.click();

		await ldapServerPage.testLdapGroups.click();

		await expect(
			await ldapServerPage.page.getByText(
				'A subset of groups has been displayed for you to review'
			)
		).toBeVisible();

		await ldapServerPage.closeButton.click();

		await ldapServerPage.cancelButton.click();
	});

	await test.step('Edit LDAP Server by changing server name', async () => {
		ldapServer.serverName = 'newServerName';

		await ldapServerPage.editLdapServer(ldapServer, serverName, false);

		await expect(
			await ldapConfigurationPage.page.getByRole('row', {
				name: 'newServerName',
			})
		).toBeVisible();
	});

	await test.step('Delete LDAP server', async () => {
		await ldapServerPage.deleteLdapServer('newServerName', false);

		await expect(
			await ldapConfigurationPage.page.getByRole('row', {
				name: 'newServerName',
			})
		).toBeHidden();
	});
});

async function resetLdapImportSystemSettings(
	systemSettingsPage: SystemSettingsPage
) {
	await systemSettingsPage.goToSystemSetting('LDAP', 'Import');

	await systemSettingsPage.page.getByLabel('Import Interval').waitFor();

	if (
		await systemSettingsPage.page
			.getByRole('button', {name: 'Actions'})
			.isVisible()
	) {
		await clickAndExpectToBeVisible({
			autoClick: true,
			target: systemSettingsPage.page.getByRole('menuitem', {
				name: 'Reset Default Values',
			}),
			trigger: systemSettingsPage.page.getByRole('button', {
				name: 'Actions',
			}),
		});

		await waitForAlert(systemSettingsPage.page);
	}
}
