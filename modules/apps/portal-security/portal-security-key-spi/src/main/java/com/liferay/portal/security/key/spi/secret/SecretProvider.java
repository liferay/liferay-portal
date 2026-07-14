/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.key.spi.secret;

import com.liferay.portal.security.key.secret.Secret;
import com.liferay.portal.security.key.secret.exception.SecretException;
import com.liferay.portal.security.key.spi.ProviderStatus;

import java.util.List;

/**
 * @author Tomas Polesovsky
 * @author Christopher Kian
 */
public interface SecretProvider {

	public void deleteSecret(long companyId, String identifier)
		throws SecretException;

	public ProviderStatus getProviderStatus();

	public Secret getSecret(long companyId, String identifier)
		throws SecretException;

	public List<String> getSecretIdentifiers(long companyId)
		throws SecretException;

	public boolean isAllowedCompany(long companyId);

	public void putSecret(long companyId, Secret secret) throws SecretException;

}