/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.ldap.internal.configuration.persistence.listener;

import com.liferay.petra.string.StringBundler;
import com.liferay.portal.configuration.persistence.listener.ConfigurationModelListener;
import com.liferay.portal.configuration.persistence.listener.ConfigurationModelListenerException;
import com.liferay.portal.kernel.security.fips.FIPSModeUtil;
import com.liferay.portal.kernel.security.pwd.PasswordEncryptor;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.security.ldap.LocalizedLDAPConfigurationException;
import com.liferay.portal.security.ldap.authenticator.configuration.LDAPAuthConfiguration;
import com.liferay.portal.security.ldap.constants.LDAPConstants;

import java.util.Dictionary;

import org.osgi.service.component.annotations.Component;

/**
 * Under FIPS mode, requires {@code passwordEncryptionAlgorithm} to be in the
 * FIPS-approved allowlist ({@code SHA-256}, {@code SHA-384}, {@code SHA-512},
 * {@code PBKDF2})
 * whenever {@code method} is {@code password-compare}. Rejects legacy
 * algorithms (MD2, MD5, SHA, SSHA, UFC-CRYPT, BCRYPT) and plaintext compare
 * ({@code NONE} or unset, which defaults to {@code NONE}).
 *
 * <p>
 * The check is skipped when {@code method} is not {@code password-compare},
 * because {@code passwordEncryptionAlgorithm} is only consulted by the
 * password-compare path; the bind path delegates verification to the LDAP
 * server.
 * </p>
 *
 * @author Jorge García Jiménez
 */
@Component(
	property = "model.class.name=com.liferay.portal.security.ldap.authenticator.configuration.LDAPAuthConfiguration",
	service = ConfigurationModelListener.class
)
public class FIPSLDAPAuthConfigurationModelListener
	implements ConfigurationModelListener {

	@Override
	public void onBeforeSave(String pid, Dictionary<String, Object> properties)
		throws ConfigurationModelListenerException {

		if (!FIPSModeUtil.isEnabled() ||
			!LDAPConstants.AUTH_METHOD_PASSWORD_COMPARE.equals(
				GetterUtil.getString(
					properties.get(LDAPConstants.AUTH_METHOD)))) {

			return;
		}

		String algorithm = GetterUtil.getString(
			properties.get("passwordEncryptionAlgorithm"));

		if (Validator.isNull(algorithm)) {
			algorithm = PasswordEncryptor.TYPE_NONE;
		}

		if (!FIPSModeUtil.isApprovedPasswordAlgorithm(algorithm)) {
			throw new LocalizedLDAPConfigurationException(
				StringBundler.concat(
					"FIPS mode does not permit LDAP password encryption ",
					"algorithm \"", algorithm, "\"; allowed values are ",
					"SHA-256, SHA-384, SHA-512, PBKDF2"),
				"fips-mode-does-not-permit-ldap-password-encryption-" +
					"algorithm-x",
				new Object[] {algorithm}, LDAPAuthConfiguration.class,
				getClass(), properties);
		}
	}

}