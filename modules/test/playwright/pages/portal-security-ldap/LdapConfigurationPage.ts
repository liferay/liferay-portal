/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Locator, Page} from '@playwright/test';

import {
	DEFAULT_LDAP_CONFIGURATION_VALUES,
	TLdapConfiguration,
} from '../../helpers/LdapConfigurationHelper';
import {waitForAlert} from '../../utils/waitForAlert';
import {InstanceSettingsPage} from '../configuration-admin-web/InstanceSettingsPage';

export class LdapConfigurationPage {
	readonly addLdapServerButton: Locator;
	readonly autogenerateUserPasswordOnImport: Locator;
	readonly createRolePerGroupOnImport: Locator;
	readonly defaultUserPassword: Locator;
	readonly enableExport: Locator;
	readonly enableGroupCacheOnImport: Locator;
	readonly enableGroupExport: Locator;
	readonly enableImport: Locator;
	readonly enableImportOnStartup: Locator;
	readonly enableUserPasswordOnImport: Locator;
	readonly enabled: Locator;
	readonly importInterval: Locator;
	readonly importMethod: Locator;
	readonly importUserSyncStrategy: Locator;
	readonly instanceSettingsPage: InstanceSettingsPage;
	readonly lockExpirationTime: Locator;
	readonly method: Locator;
	readonly page: Page;
	readonly passwordEncryptionAlgorithm: Locator;
	readonly required: Locator;
	readonly resetDefaultValues: Locator;
	readonly saveButton: Locator;
	readonly useLdapPasswordPolicy: Locator;

	constructor(page: Page) {
		this.addLdapServerButton = page.getByRole('button', {name: 'Add'});
		this.autogenerateUserPasswordOnImport = page.getByText(
			'Autogenerate User Password on Import'
		);
		this.createRolePerGroupOnImport = page.getByText(
			'Create Role per Group on Import'
		);
		this.defaultUserPassword = page.getByLabel('Default User Password');
		this.enableExport = page.getByText('Enable Export');
		this.enableGroupCacheOnImport = page.getByText(
			'Enable Group Cache on Import'
		);
		this.enableGroupExport = page.getByText('Enable Group Export');
		this.enableImport = page.getByText('Enable Import').first();
		this.enableImportOnStartup = page.getByText('Enable Import on Startup');
		this.enableUserPasswordOnImport = page.getByText(
			'Enable User Password on Import'
		);

		this.enabled = page.getByText('Enabled', {exact: true});
		this.importInterval = page.getByLabel('Import Interval');
		this.importMethod = page.getByLabel('Import Method');
		this.importUserSyncStrategy = page.getByLabel(
			'Import User Sync Strategy'
		);
		this.instanceSettingsPage = new InstanceSettingsPage(page);
		this.lockExpirationTime = page.getByLabel('Lock Expiration Time');
		this.method = page.getByLabel('Method');
		this.page = page;
		this.passwordEncryptionAlgorithm = page.getByLabel(
			'Password Encryption Algorithm'
		);
		this.required = page.getByText('Required');
		this.resetDefaultValues = page.getByText('Reset Default Values');
		this.saveButton = page.getByRole('button', {name: 'Save'});
		this.useLdapPasswordPolicy = page.getByText('Use LDAP Password Policy');
	}

	async addLdapServer(forceReload = true) {
		await this.goToServersTab(forceReload);

		await this.addLdapServerButton.click();
	}

	async goTo() {
		await this.instanceSettingsPage.goToInstanceSetting(
			'LDAP',
			'General',
			false
		);

		await this.enabled.waitFor();
	}

	async goToServersTab(forceReload = true) {
		if (forceReload) {
			await this.goTo();
		}

		await this.page.getByRole('menuitem', {name: 'Servers'}).click();

		await this.addLdapServerButton.waitFor();
	}

	async resetLdapConfiguration() {
		await this.goTo();

		const defaultLdapConfiguration: TLdapConfiguration = {
			...DEFAULT_LDAP_CONFIGURATION_VALUES,
		};

		await this.updateLDAPGeneralConfiguration(defaultLdapConfiguration);

		await this.updateLDAPExportConfiguration(defaultLdapConfiguration);

		await this.updateLDAPImportConfiguration(defaultLdapConfiguration);
	}

	async updateLDAPConfiguration(
		ldapConfiguration: TLdapConfiguration,
		forceReload = true
	) {
		if (forceReload) {
			await this.goTo();
		}

		await this.updateLDAPGeneralConfiguration(ldapConfiguration);

		await this.updateLDAPExportConfiguration(ldapConfiguration);

		await this.updateLDAPImportConfiguration(ldapConfiguration);
	}

	async updateLDAPExportConfiguration(ldapConfiguration: TLdapConfiguration) {
		await this.page.getByRole('menuitem', {name: 'Export'}).click();

		await this.enableExport.waitFor();

		if (ldapConfiguration.enableExport !== undefined) {
			await this.enableExport.setChecked(ldapConfiguration.enableExport);
		}

		if (ldapConfiguration.enableGroupExport !== undefined) {
			await this.enableGroupExport.setChecked(
				ldapConfiguration.enableGroupExport
			);
		}

		await this.saveButton.click();

		await waitForAlert(
			this.page,
			`Success:Your request completed successfully.`
		);
	}

	async updateLDAPGeneralConfiguration(
		ldapConfiguration: TLdapConfiguration
	) {
		if (await this.enabled.isHidden()) {
			await this.page.getByRole('menuitem', {name: 'General'}).click();
		}

		await this.enabled.waitFor();

		if (ldapConfiguration.enabled !== undefined) {
			await this.enabled.setChecked(ldapConfiguration.enabled);
		}

		if (ldapConfiguration.method) {
			await this.method.selectOption(ldapConfiguration.method);
		}

		if (ldapConfiguration.passwordEncryptionAlgorithm) {
			await this.passwordEncryptionAlgorithm.selectOption(
				ldapConfiguration.passwordEncryptionAlgorithm
			);
		}

		if (ldapConfiguration.required !== undefined) {
			await this.required.setChecked(ldapConfiguration.required);
		}

		if (ldapConfiguration.userLdapPasswordPolicy !== undefined) {
			await this.useLdapPasswordPolicy.setChecked(
				ldapConfiguration.userLdapPasswordPolicy
			);
		}

		await this.saveButton.click();

		await waitForAlert(
			this.page,
			`Success:Your request completed successfully.`
		);
	}

	async updateLDAPImportConfiguration(ldapConfiguration: TLdapConfiguration) {
		await this.page.getByRole('menuitem', {name: 'Import'}).click();

		await this.enableImport.waitFor();

		if (ldapConfiguration.autogenerateUserPasswordOnImport !== undefined) {
			await this.autogenerateUserPasswordOnImport.setChecked(
				ldapConfiguration.autogenerateUserPasswordOnImport
			);
		}

		if (ldapConfiguration.createRolePerGroupOnImport !== undefined) {
			await this.createRolePerGroupOnImport.setChecked(
				ldapConfiguration.createRolePerGroupOnImport
			);
		}

		if (ldapConfiguration.defaultUserPassword) {
			await this.defaultUserPassword.fill(
				ldapConfiguration.defaultUserPassword
			);
		}

		if (ldapConfiguration.enableGroupCacheOnImport !== undefined) {
			await this.enableGroupCacheOnImport.setChecked(
				ldapConfiguration.enableGroupCacheOnImport
			);
		}

		if (ldapConfiguration.enableImport !== undefined) {
			await this.enableImport.setChecked(ldapConfiguration.enableImport);
		}

		if (ldapConfiguration.enableImportOnStartup !== undefined) {
			await this.enableImportOnStartup.setChecked(
				ldapConfiguration.enableImportOnStartup
			);
		}

		if (ldapConfiguration.enableUserPasswordOnImport !== undefined) {
			await this.enableUserPasswordOnImport.setChecked(
				ldapConfiguration.enableUserPasswordOnImport
			);
		}

		if (ldapConfiguration.importInterval) {
			await this.importInterval.fill(
				ldapConfiguration.importInterval.toString()
			);
		}

		if (ldapConfiguration.importMethod) {
			await this.importMethod.selectOption(
				ldapConfiguration.importMethod
			);
		}

		if (ldapConfiguration.importUserSyncStrategy) {
			await this.importUserSyncStrategy.selectOption(
				ldapConfiguration.importUserSyncStrategy
			);
		}

		if (ldapConfiguration.lockExpirationTime) {
			await this.lockExpirationTime.fill(
				ldapConfiguration.lockExpirationTime.toString()
			);
		}

		await this.saveButton.click();

		await waitForAlert(
			this.page,
			`Success:Your request completed successfully.`
		);
	}
}
