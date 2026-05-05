/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.ldap.internal.configuration.persistence.listener;

import com.liferay.portal.configuration.persistence.listener.ConfigurationModelListener;
import com.liferay.portal.configuration.persistence.listener.ConfigurationModelListenerException;
import com.liferay.portal.kernel.security.fips.FIPSModeUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.security.ldap.exportimport.configuration.LDAPImportConfiguration;

import java.util.Dictionary;

import org.osgi.service.component.annotations.Component;

/**
 * Rejects enabling {@code importUserPasswordEnabled} while FIPS mode is on if
 * the portal-wide {@code passwords.encryption.algorithm} is not on the
 * FIPS-approved allowlist. When the portal algorithm is FIPS-approved (default
 * PBKDF2), the imported password is hashed compliantly on storage, so the
 * feature remains available.
 *
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
			throw new ConfigurationModelListenerException(
				null, LDAPImportConfiguration.class, getClass(), properties);
		}
	}

}
