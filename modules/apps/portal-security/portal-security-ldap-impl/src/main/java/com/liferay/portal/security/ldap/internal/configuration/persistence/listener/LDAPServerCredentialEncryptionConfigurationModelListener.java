/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.ldap.internal.configuration.persistence.listener;

import com.liferay.portal.configuration.persistence.listener.ConfigurationModelListener;
import com.liferay.portal.configuration.persistence.listener.ConfigurationModelListenerException;
import com.liferay.portal.kernel.encryptor.EncryptorException;
import com.liferay.portal.kernel.encryptor.EncryptorUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.security.ldap.configuration.LDAPServerConfiguration;

import java.util.Dictionary;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * Encrypts {@code securityCredential} (the LDAP bind password) at configuration
 * save time so it is never persisted in plain text in the OSGi configuration
 * store. Values already tagged with {@link #ENCRYPTED_VALUE_PREFIX} are left
 * untouched so repeated saves are idempotent and existing encrypted values
 * survive a round-trip.
 *
 * <p>
 * Always active; not gated on FIPS mode — encrypting credentials at rest is
 * a strict improvement over plain text in both modes.
 * </p>
 *
 * @author Jorge García Jiménez
 */
@Component(
	property = "model.class.name=com.liferay.portal.security.ldap.configuration.LDAPServerConfiguration",
	service = ConfigurationModelListener.class
)
public class LDAPServerCredentialEncryptionConfigurationModelListener
	implements ConfigurationModelListener {

	public static final String ENCRYPTED_VALUE_PREFIX = "{ENC}";

	@Override
	public void onBeforeSave(String pid, Dictionary<String, Object> properties)
		throws ConfigurationModelListenerException {

		String securityCredential = GetterUtil.getString(
			properties.get("securityCredential"));

		if (Validator.isNull(securityCredential) ||
			securityCredential.startsWith(ENCRYPTED_VALUE_PREFIX)) {

			return;
		}

		long companyId = GetterUtil.getLong(properties.get("companyId"));

		if (companyId <= 0) {
			throw new ConfigurationModelListenerException(
				"Cannot encrypt LDAP security credential: missing companyId",
				LDAPServerConfiguration.class, getClass(), properties);
		}

		try {
			Company company = _companyLocalService.getCompany(companyId);

			String encrypted = EncryptorUtil.encrypt(
				company.getKeyObj(), securityCredential);

			properties.put(
				"securityCredential", ENCRYPTED_VALUE_PREFIX + encrypted);
		}
		catch (EncryptorException | PortalException exception) {
			_log.error(
				"Unable to encrypt LDAP security credential for company " +
					companyId,
				exception);

			throw new ConfigurationModelListenerException(
				exception, LDAPServerConfiguration.class, getClass(),
				properties);
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		LDAPServerCredentialEncryptionConfigurationModelListener.class);

	@Reference
	private CompanyLocalService _companyLocalService;

}