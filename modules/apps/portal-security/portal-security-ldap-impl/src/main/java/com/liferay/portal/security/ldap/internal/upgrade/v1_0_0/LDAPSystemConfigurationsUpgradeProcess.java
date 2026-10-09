/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.ldap.internal.upgrade.v1_0_0;

import com.liferay.portal.configuration.module.configuration.ConfigurationProvider;
import com.liferay.portal.kernel.model.CompanyConstants;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.security.ldap.authenticator.configuration.LDAPAuthConfiguration;
import com.liferay.portal.security.ldap.configuration.SystemLDAPConfiguration;
import com.liferay.portal.security.ldap.exportimport.configuration.LDAPExportConfiguration;
import com.liferay.portal.security.ldap.exportimport.configuration.LDAPImportConfiguration;

import java.util.Dictionary;

import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;

/**
 * @author Marta Medio
 */
public class LDAPSystemConfigurationsUpgradeProcess extends UpgradeProcess {

	public LDAPSystemConfigurationsUpgradeProcess(
		ConfigurationAdmin configurationAdmin,
		ConfigurationProvider configurationProvider) {

		_configurationAdmin = configurationAdmin;
		_configurationProvider = configurationProvider;
	}

	@Override
	protected void doUpgrade() throws Exception {
		_upgradeConfiguration(LDAPAuthConfiguration.class.getName());
		_upgradeConfiguration(LDAPExportConfiguration.class.getName());
		_upgradeConfiguration(LDAPImportConfiguration.class.getName());
		_upgradeConfiguration(SystemLDAPConfiguration.class.getName());
	}

	private void _upgradeConfiguration(String className) throws Exception {
		Configuration[] configurations = _configurationAdmin.listConfigurations(
			"(service.factoryPid=" + className + ")");

		if (configurations == null) {
			return;
		}

		for (Configuration configuration : configurations) {
			Dictionary<String, Object> configurationProperties =
				configuration.getProperties();

			long companyId = GetterUtil.getLong(
				configurationProperties.get("companyId"));

			if (companyId != CompanyConstants.SYSTEM) {
				continue;
			}

			_configurationProvider.saveSystemConfiguration(
				Class.forName(className), configurationProperties);

			configuration.delete();
		}
	}

	private final ConfigurationAdmin _configurationAdmin;
	private final ConfigurationProvider _configurationProvider;

}