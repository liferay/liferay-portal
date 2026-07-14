/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.ldap.internal.configuration.persistence.listener;

import com.liferay.portal.configuration.persistence.listener.ConfigurationModelListener;
import com.liferay.portal.configuration.persistence.listener.ConfigurationModelListenerException;
import com.liferay.portal.kernel.security.fips.FIPSModeValidator;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.security.ldap.LDAPConfigurationModelListenerException;
import com.liferay.portal.security.ldap.configuration.LDAPServerConfiguration;
import com.liferay.portal.security.ldap.constants.LDAPConstants;

import java.util.Dictionary;

import org.osgi.service.component.annotations.Component;

/**
 * @author Jorge García Jiménez
 */
@Component(
	property = "model.class.name=com.liferay.portal.security.ldap.configuration.LDAPServerConfiguration",
	service = ConfigurationModelListener.class
)
public class LDAPServerConfigurationModelListener
	implements ConfigurationModelListener {

	@Override
	public void onBeforeSave(String pid, Dictionary<String, Object> properties)
		throws ConfigurationModelListenerException {

		String baseProviderURL = GetterUtil.getString(
			properties.get(LDAPConstants.BASE_PROVIDER_URL));

		if (Validator.isNull(baseProviderURL)) {
			return;
		}

		try {
			FIPSModeValidator.validateURL(baseProviderURL);
		}
		catch (SecurityException securityException) {
			throw new LDAPConfigurationModelListenerException(
				securityException.getMessage(), LDAPServerConfiguration.class,
				getClass(), new Object[] {baseProviderURL, "ldaps://"},
				"the-base-provider-url-x-must-use-the-x-scheme-in-fips-mode",
				properties);
		}
	}

}