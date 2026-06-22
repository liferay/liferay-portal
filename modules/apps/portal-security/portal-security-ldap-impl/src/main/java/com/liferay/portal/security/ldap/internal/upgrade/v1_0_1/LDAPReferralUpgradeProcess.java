/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.ldap.internal.upgrade.v1_0_1;

import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.security.ldap.configuration.SystemLDAPConfiguration;
import com.liferay.portal.security.ldap.constants.LDAPConstants;

import java.util.Dictionary;
import java.util.Objects;

import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;

/**
 * @author Christian Moura
 */
public class LDAPReferralUpgradeProcess extends UpgradeProcess {

	public LDAPReferralUpgradeProcess(ConfigurationAdmin configurationAdmin) {
		_configurationAdmin = configurationAdmin;
	}

	@Override
	protected void doUpgrade() throws Exception {
		Configuration[] configurations = _configurationAdmin.listConfigurations(
			"(service.factoryPid=" + SystemLDAPConfiguration.class.getName() +
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

			if (Objects.equals(
					properties.get(LDAPConstants.REFERRAL), "throws")) {

				properties.put(
					LDAPConstants.REFERRAL, LDAPConstants.REFERRAL_THROW);

				configuration.update(properties);
			}
		}
	}

	private final ConfigurationAdmin _configurationAdmin;

}