/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.key.secret;

import com.liferay.portal.security.key.KeyReference;
import com.liferay.portal.security.key.secret.exception.SecretException;

import java.util.List;

import org.osgi.annotation.versioning.ProviderType;

/**
 * @author Tomas Polesovsky
 * @author Christopher Kian
 */
@ProviderType
public interface SecretManager {

	public void deleteSecret(long companyId, KeyReference keyReference)
		throws SecretException;

	public List<KeyReference> getKeyReferences(
			long companyId, String secretProviderId)
		throws SecretException;

	public Secret getSecret(long companyId, KeyReference keyReference)
		throws SecretException;

	public List<String> getSecretProviderIds(long companyId)
		throws SecretException;

	public KeyReference putSecret(long companyId, Secret secret)
		throws SecretException;

}