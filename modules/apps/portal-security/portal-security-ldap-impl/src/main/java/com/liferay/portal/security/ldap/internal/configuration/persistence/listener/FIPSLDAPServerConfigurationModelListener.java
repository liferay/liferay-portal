/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.ldap.internal.configuration.persistence.listener;

import com.liferay.portal.configuration.persistence.listener.ConfigurationModelListener;
import com.liferay.portal.configuration.persistence.listener.ConfigurationModelListenerException;
import com.liferay.portal.kernel.security.fips.FIPSModeUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.security.ldap.LocalizedLDAPConfigurationException;
import com.liferay.portal.security.ldap.configuration.LDAPServerConfiguration;

import java.util.Dictionary;

import org.osgi.service.component.annotations.Component;

/**
 * Rejects saving an {@link LDAPServerConfiguration} whose
 * {@code baseProviderURL} does not use the {@code ldaps://} scheme while FIPS
 * mode is enabled.
 *
 * @author Jorge García Jiménez
 */
@Component(
	property = "model.class.name=com.liferay.portal.security.ldap.configuration.LDAPServerConfiguration",
	service = ConfigurationModelListener.class
)
public class FIPSLDAPServerConfigurationModelListener
	implements ConfigurationModelListener {

	@Override
	public void onBeforeSave(String pid, Dictionary<String, Object> properties)
		throws ConfigurationModelListenerException {

		if (!FIPSModeUtil.isEnabled()) {
			return;
		}

		String baseProviderURL = GetterUtil.getString(
			properties.get("baseProviderURL"));

		if (Validator.isNull(baseProviderURL)) {
			return;
		}

		if (!StringUtil.startsWith(baseProviderURL, "ldaps://")) {
			throw new LocalizedLDAPConfigurationException(
				"FIPS mode requires LDAP base provider URL to use the " +
					"ldaps:// scheme: " + baseProviderURL,
				"fips-mode-requires-the-ldaps-scheme-for-the-base-provider-" +
					"url-x",
				new Object[] {baseProviderURL}, LDAPServerConfiguration.class,
				getClass(), properties);
		}
	}

}