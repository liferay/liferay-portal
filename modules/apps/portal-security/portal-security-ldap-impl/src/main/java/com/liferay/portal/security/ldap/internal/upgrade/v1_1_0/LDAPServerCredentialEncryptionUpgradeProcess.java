/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.ldap.internal.upgrade.v1_1_0;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.security.ldap.configuration.LDAPServerConfiguration;
import com.liferay.portal.security.ldap.internal.configuration.persistence.listener.LDAPServerCredentialEncryptionConfigurationModelListener;

import java.util.Dictionary;

import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;

/**
 * @author Jorge García Jiménez
 */
public class LDAPServerCredentialEncryptionUpgradeProcess
	extends UpgradeProcess {

	public LDAPServerCredentialEncryptionUpgradeProcess(
		ConfigurationAdmin configurationAdmin) {

		_configurationAdmin = configurationAdmin;
	}

	@Override
	protected void doUpgrade() throws Exception {
		Configuration[] configurations = _configurationAdmin.listConfigurations(
			"(service.factoryPid=" + LDAPServerConfiguration.class.getName() +
				")");

		if (configurations == null) {
			return;
		}

		for (Configuration configuration : configurations) {
			Dictionary<String, Object> properties =
				configuration.getProperties();

			if (properties == null) {
				continue;
			}

			String securityCredential = GetterUtil.getString(
				properties.get("securityCredential"));

			if (Validator.isNull(securityCredential) ||
				securityCredential.startsWith(
					LDAPServerCredentialEncryptionConfigurationModelListener.
						ENCRYPTED_VALUE_PREFIX)) {

				continue;
			}

			try {
				configuration.update(properties);
			}
			catch (Exception exception) {
				_log.error(
					"Unable to re-save LDAP server configuration " +
						configuration.getPid() +
							" to encrypt legacy credential",
					exception);
			}
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		LDAPServerCredentialEncryptionUpgradeProcess.class);

	private final ConfigurationAdmin _configurationAdmin;

}