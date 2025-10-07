/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.saml.internal.upgrade.v1_0_0;

import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.saml.internal.constants.LegacySamlPropsKeys;

import java.util.Dictionary;
import java.util.Hashtable;

import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;

/**
 * @author Tomas Polesovsky
 */
public class SamlIdpSsoSessionMaxAgePropertyUpgradeProcess
	extends BaseUpgradeSaml {

	public SamlIdpSsoSessionMaxAgePropertyUpgradeProcess(
		ConfigurationAdmin configurationAdmin) {

		_configurationAdmin = configurationAdmin;
	}

	@Override
	public void doUpgrade() throws Exception {
		String samlIdpSsoSessionMaxAge = PropsUtil.get(
			LegacySamlPropsKeys.SAML_IDP_SSO_SESSION_MAX_AGE);

		if (Validator.isNull(samlIdpSsoSessionMaxAge)) {
			samlIdpSsoSessionMaxAge = getDefaultValue(
				LegacySamlPropsKeys.SAML_IDP_SSO_SESSION_MAX_AGE);
		}

		Configuration[] configurations = _configurationAdmin.listConfigurations(
			"(service.factoryPid=com.liferay.saml.runtime.configuration." +
				"SamlProviderConfiguration)");

		if (configurations == null) {
			return;
		}

		for (Configuration configuration : configurations) {
			Dictionary<String, Object> properties =
				configuration.getProperties();

			if (properties == null) {
				properties = new Hashtable<>();
			}

			String samlIdpSessionMaximumAgeProperty = (String)properties.get(
				LegacySamlPropsKeys.SAML_IDP_SESSION_MAXIMUM_AGE);

			long samlIdpSessionMaximumAge = GetterUtil.getLong(
				samlIdpSessionMaximumAgeProperty);

			if (samlIdpSessionMaximumAge > 0) {
				continue;
			}

			properties.put(
				LegacySamlPropsKeys.SAML_IDP_SESSION_MAXIMUM_AGE,
				samlIdpSsoSessionMaxAge);

			configuration.update(properties);
		}
	}

	private final ConfigurationAdmin _configurationAdmin;

}