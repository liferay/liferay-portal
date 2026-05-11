/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.ldap.internal.configuration;

import com.liferay.portal.kernel.encryptor.EncryptorException;
import com.liferay.portal.kernel.encryptor.EncryptorUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.security.ldap.internal.configuration.persistence.listener.LDAPServerCredentialEncryptionConfigurationModelListener;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Jorge García Jiménez
 */
@Component(service = {})
public class LDAPCredentialCipher {

	public String resolve(long companyId, String value) {
		if (!StringUtil.startsWith(
				value,
				LDAPServerCredentialEncryptionConfigurationModelListener.
					ENCRYPTED_VALUE_PREFIX)) {

			return value;
		}

		try {
			Company company = _companyLocalService.getCompany(companyId);

			return EncryptorUtil.decrypt(
				company.getKeyObj(),
				value.substring(
					LDAPServerCredentialEncryptionConfigurationModelListener.
						ENCRYPTED_VALUE_PREFIX.length()));
		}
		catch (EncryptorException | PortalException exception) {
			_log.error(
				"Unable to decrypt LDAP security credential for company " +
					companyId,
				exception);

			return value;
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		LDAPCredentialCipher.class);

	@Reference
	private CompanyLocalService _companyLocalService;

}