/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Locator, Page} from '@playwright/test';

import {TLdapServer} from '../../helpers/LdapConfigurationHelper';
import {waitForAlert} from '../../utils/waitForAlert';
import {LdapConfigurationPage} from './LdapConfigurationPage';

export class LdapServerPage {
	readonly authenticationSearchFilter: Locator;
	readonly baseDn: Locator;
	readonly baseProviderUrl: Locator;
	readonly cancelButton: Locator;
	readonly clockSkew: Locator;
	readonly closeButton: Locator;
	readonly credentials: Locator;
	readonly customContactMapping: Locator;
	readonly customUserMapping: Locator;
	readonly description: Locator;
	readonly emailAddress: Locator;
	readonly firstName: Locator;
	readonly fullName: Locator;
	readonly group: Locator;
	readonly groupDefaultObjectClasses: Locator;
	readonly groupName: Locator;
	readonly groupsDn: Locator;
	readonly ignoreUserSearchFilterForAuthentication: Locator;
	readonly importSearchFilterGroup: Locator;
	readonly importSearchFilterUser: Locator;
	readonly jobTitle: Locator;
	readonly lastName: Locator;
	readonly ldapConfigurationPage: LdapConfigurationPage;
	readonly middleName: Locator;
	readonly page: Page;
	readonly password: Locator;
	readonly portrait: Locator;
	readonly principal: Locator;
	readonly saveButton: Locator;
	readonly screenName: Locator;
	readonly serverName: Locator;
	readonly status: Locator;
	readonly testLdapConnection: Locator;
	readonly testLdapGroups: Locator;
	readonly testLdapUsers: Locator;
	readonly user: Locator;
	readonly userDefaultObjectClasses: Locator;
	readonly userIgnoreAttributes: Locator;
	readonly usersDn: Locator;
	readonly uuid: Locator;

	constructor(page: Page) {
		this.authenticationSearchFilter = page.getByLabel(
			'Authentication Search Filter'
		);
		this.baseDn = page.getByLabel('Base DN The LDAP Base');
		this.baseProviderUrl = page.getByLabel('Base Provider URL The LDAP');
		this.cancelButton = page.getByRole('button', {name: 'Cancel'});
		this.clockSkew = page.getByLabel('Clock Skew The system time');
		this.closeButton = page.getByLabel('Close');
		this.credentials = page.getByLabel('Credentials');
		this.customContactMapping = page.getByLabel('Custom Contact Mapping');
		this.customUserMapping = page.getByLabel('Custom User Mapping');
		this.description = page.getByLabel('Description');
		this.emailAddress = page.getByLabel('Email Address');
		this.firstName = page.getByLabel('First Name', {exact: true});
		this.fullName = page.getByLabel('Full Name');
		this.group = page.getByLabel('Group', {exact: true});
		this.groupDefaultObjectClasses = page.getByLabel(
			'Group Default Object Classes'
		);
		this.groupName = page.getByLabel('Group Name');
		this.groupsDn = page.getByLabel('Groups DN');
		this.ignoreUserSearchFilterForAuthentication = page.getByLabel(
			'Ignore User Search Filter'
		);
		this.importSearchFilterGroup = page
			.getByText('Groups Import Search Filter')
			.getByLabel('Import Search Filter');
		this.importSearchFilterUser = page
			.getByText('Users Authentication Search')
			.getByLabel('Import Search Filter');
		this.jobTitle = page.getByLabel('Job Title');
		this.lastName = page.getByLabel('Last Name', {exact: true});
		this.ldapConfigurationPage = new LdapConfigurationPage(page);
		this.middleName = page.getByLabel('Middle Name');
		this.page = page;
		this.password = page.getByLabel('Password');
		this.portrait = page.getByLabel('Portrait');
		this.principal = page.getByLabel('Principal');
		this.saveButton = page.getByRole('button', {name: 'Save'});
		this.screenName = page.getByLabel('Screen Name');
		this.serverName = page.getByLabel('Server Name');
		this.status = page.getByLabel('Status');
		this.testLdapConnection = page.getByRole('button', {
			name: 'Test LDAP Connection',
		});
		this.testLdapGroups = page.getByRole('button', {
			name: 'Test LDAP Groups',
		});
		this.testLdapUsers = page.getByRole('button', {
			name: 'Test LDAP Users',
		});
		this.user = page.getByLabel('User', {exact: true});
		this.userDefaultObjectClasses = page.getByLabel(
			'User Default Object Classes'
		);
		this.userIgnoreAttributes = page.getByLabel('User Ignore Attributes');
		this.usersDn = page.getByLabel('Users DN');
		this.uuid = page.getByLabel('UUID');
	}

	async addLdapServer(ldapServer: TLdapServer, forceReload = true) {
		await this.ldapConfigurationPage.addLdapServer(forceReload);

		await this.populateLdapServerValues(ldapServer);

		await this.saveButton.click();

		await waitForAlert(
			this.page,
			`Success:Your request completed successfully.`
		);
	}

	async deleteLdapServer(serverName: string, forceReload = true) {
		if (forceReload) {
			await this.ldapConfigurationPage.goToServersTab();
		}

		this.page.once('dialog', async (dialog) => {
			dialog.accept();
		});

		await this.ldapConfigurationPage.page
			.getByRole('row', {name: serverName})
			.locator('div')
			.getByTitle('Delete')
			.click();

		await waitForAlert(
			this.page,
			`Success:Your request completed successfully.`
		);
	}

	async deleteLdapServers(forceReload = true) {
		if (forceReload) {
			await this.ldapConfigurationPage.goToServersTab();
		}

		const ldapServerDeleteButton = await this.ldapConfigurationPage.page
			.getByRole('link', {name: 'Delete'})
			.first();

		while (await ldapServerDeleteButton.isVisible()) {
			this.page.once('dialog', async (dialog) => {
				dialog.accept();
			});

			await ldapServerDeleteButton.click();

			await waitForAlert(
				this.page,
				`Success:Your request completed successfully.`
			);
		}
	}

	async editLdapServer(
		ldapServer: TLdapServer,
		existingServerName = ldapServer.serverName,
		forceReload = true
	) {
		await this.viewLdapServer(existingServerName, forceReload);

		await this.populateLdapServerValues(ldapServer);

		await this.saveButton.click();

		await waitForAlert(
			this.page,
			`Success:Your request completed successfully.`
		);
	}

	async populateLdapServerValues(ldapServer: TLdapServer) {
		await this.serverName.waitFor();

		if (ldapServer.defaultValues) {
			await this.page
				.getByText(ldapServer.defaultValues)
				.getByRole('radio')
				.check();
		}
		if (ldapServer.authenticationSearchFilter) {
			await this.authenticationSearchFilter.fill(
				ldapServer.authenticationSearchFilter
			);
		}
		if (ldapServer.baseDn) {
			await this.baseDn.fill(ldapServer.baseDn);
		}
		if (ldapServer.baseProviderUrl) {
			await this.baseProviderUrl.fill(ldapServer.baseProviderUrl);
		}
		if (ldapServer.clockSkew) {
			await this.clockSkew.fill(ldapServer.clockSkew.toString());
		}
		if (ldapServer.credentials) {
			await this.credentials.fill(ldapServer.credentials);
		}
		if (ldapServer.customContactMapping) {
			await this.customContactMapping.fill(
				ldapServer.customContactMapping
			);
		}
		if (ldapServer.customUserMapping) {
			await this.customUserMapping.fill(ldapServer.customUserMapping);
		}
		if (ldapServer.description) {
			await this.description.fill(ldapServer.description);
		}
		if (ldapServer.emailAddress) {
			await this.emailAddress.fill(ldapServer.emailAddress);
		}
		if (ldapServer.firstName) {
			await this.firstName.fill(ldapServer.firstName);
		}
		if (ldapServer.fullName) {
			await this.fullName.fill(ldapServer.fullName);
		}
		if (ldapServer.group) {
			await this.group.fill(ldapServer.group);
		}
		if (ldapServer.groupDefaultObjectClasses) {
			await this.groupDefaultObjectClasses.fill(
				ldapServer.groupDefaultObjectClasses
			);
		}
		if (ldapServer.groupName) {
			await this.groupName.fill(ldapServer.groupName);
		}
		if (ldapServer.groupsDn) {
			await this.groupsDn.fill(ldapServer.groupsDn);
		}
		if (ldapServer.ignoreUserSearchFilterForAuthentication !== undefined) {
			await this.ignoreUserSearchFilterForAuthentication.setChecked(
				ldapServer.ignoreUserSearchFilterForAuthentication
			);
		}
		if (ldapServer.importSearchFilterGroup) {
			await this.importSearchFilterGroup.fill(
				ldapServer.importSearchFilterGroup
			);
		}
		if (ldapServer.importSearchFilterUser) {
			await this.importSearchFilterUser.fill(
				ldapServer.importSearchFilterUser
			);
		}
		if (ldapServer.jobTitle) {
			await this.jobTitle.fill(ldapServer.jobTitle);
		}
		if (ldapServer.lastName) {
			await this.lastName.fill(ldapServer.lastName);
		}
		if (ldapServer.middleName) {
			await this.middleName.fill(ldapServer.middleName);
		}
		if (ldapServer.password) {
			await this.password.fill(ldapServer.password);
		}
		if (ldapServer.portrait) {
			await this.portrait.fill(ldapServer.portrait);
		}
		if (ldapServer.principal) {
			await this.principal.fill(ldapServer.principal);
		}
		if (ldapServer.screenName) {
			await this.screenName.fill(ldapServer.screenName);
		}

		await this.serverName.fill(ldapServer.serverName);

		if (ldapServer.status) {
			await this.status.fill(ldapServer.status);
		}
		if (ldapServer.user) {
			await this.user.fill(ldapServer.user);
		}
		if (ldapServer.userDefaultObjectClasses) {
			await this.userDefaultObjectClasses.fill(
				ldapServer.userDefaultObjectClasses
			);
		}
		if (ldapServer.userIgnoreAttributes) {
			await this.userIgnoreAttributes.fill(
				ldapServer.userIgnoreAttributes
			);
		}
		if (ldapServer.usersDn) {
			await this.usersDn.fill(ldapServer.usersDn);
		}
		if (ldapServer.uuid) {
			await this.uuid.fill(ldapServer.uuid);
		}
	}

	async viewLdapServer(serverName: string, forceReload = true) {
		if (forceReload) {
			await this.ldapConfigurationPage.goToServersTab();
		}

		await this.ldapConfigurationPage.page
			.getByRole('row', {name: serverName})
			.locator('div')
			.getByTitle('Edit')
			.click();

		await this.serverName.waitFor();
	}
}
