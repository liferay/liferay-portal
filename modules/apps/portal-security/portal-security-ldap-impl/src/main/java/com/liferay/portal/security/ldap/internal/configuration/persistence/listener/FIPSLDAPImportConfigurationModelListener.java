/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.ldap.internal.configuration.persistence.listener;

import com.liferay.petra.string.StringBundler;
import com.liferay.portal.configuration.persistence.listener.ConfigurationModelListener;
import com.liferay.portal.configuration.persistence.listener.ConfigurationModelListenerException;
import com.liferay.portal.kernel.security.fips.FIPSModeUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.security.ldap.LocalizedLDAPConfigurationModelListenerException;
import com.liferay.portal.security.ldap.exportimport.configuration.LDAPImportConfiguration;

import java.util.Dictionary;

import org.osgi.service.component.annotations.Component;

/**
 * @author Jorge García Jiménez
 */
@Component(
	property = "model.class.name=com.liferay.portal.security.ldap.exportimport.configuration.LDAPImportConfiguration",
	service = ConfigurationModelListener.class
)
public class FIPSLDAPImportConfigurationModelListener
	implements ConfigurationModelListener {

	@Override
	public void onBeforeSave(String pid, Dictionary<String, Object> properties)
		throws ConfigurationModelListenerException {

		if (!FIPSModeUtil.isEnabled() ||
			!GetterUtil.getBoolean(
				properties.get("importUserPasswordEnabled"))) {

			return;
		}

		String portalAlgorithm = PropsUtil.get(
			PropsKeys.PASSWORDS_ENCRYPTION_ALGORITHM);

		if (!FIPSModeUtil.isApprovedPasswordAlgorithm(portalAlgorithm)) {
			throw new LocalizedLDAPConfigurationModelListenerException(
				StringBundler.concat(
					"FIPS mode does not permit enabling user password import ",
					"from LDAP while passwords.encryption.algorithm is \"",
					portalAlgorithm, "\"; configure a FIPS-approved algorithm ",
					"(PBKDF2, SHA-256, SHA-384, SHA-512) or disable the ",
					"import feature"),
				StringBundler.concat(
					"fips-mode-does-not-permit-enabling-ldap-user-password-",
					"import-while-the-portal-password-encryption-algorithm-is-",
					"x"),
				new Object[] {portalAlgorithm}, LDAPImportConfiguration.class,
				getClass(), properties);
		}
	}

}