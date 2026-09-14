/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.key.test.util;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.security.key.KeyReference;
import com.liferay.portal.security.key.secret.Secret;
import com.liferay.portal.security.key.secret.exception.SecretException;
import com.liferay.portal.security.key.spi.ProviderStatus;
import com.liferay.portal.security.key.spi.secret.SecretProvider;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Pedro Victor Silvestre
 */
public class TestSecretProvider implements SecretProvider {

	public TestSecretProvider(String secretProviderId) {
		_secretProviderId = secretProviderId;
	}

	@Override
	public void deleteSecret(long companyId, String secretIdentifier) {
		_secrets.remove(_getKey(companyId, secretIdentifier));
	}

	@Override
	public ProviderStatus getProviderStatus() {
		return ProviderStatus.OPERATIONAL;
	}

	@Override
	public Secret getSecret(long companyId, String secretIdentifier)
		throws SecretException {

		String value = _secrets.get(_getKey(companyId, secretIdentifier));

		if (value == null) {
			throw new SecretException(
				"No secret was found for identifier \"" + secretIdentifier +
					"\"");
		}

		return new Secret(
			new KeyReference(
				secretIdentifier, _secretProviderId, KeyReference.Type.SECRET),
			value);
	}

	@Override
	public List<String> getSecretIdentifiers(long companyId) {
		List<String> secretIdentifiers = new ArrayList<>();

		String prefix = companyId + StringPool.SLASH;

		for (String key : _secrets.keySet()) {
			if (key.startsWith(prefix)) {
				secretIdentifiers.add(key.substring(prefix.length()));
			}
		}

		return secretIdentifiers;
	}

	@Override
	public boolean isAllowedCompany(long companyId) {
		return true;
	}

	@Override
	public void putSecret(long companyId, Secret secret) {
		KeyReference keyReference = secret.getKeyReference();

		_secrets.put(
			_getKey(companyId, keyReference.getIdentifier()),
			new String(secret.getChars()));
	}

	private String _getKey(long companyId, String secretIdentifier) {
		return companyId + StringPool.SLASH + secretIdentifier;
	}

	private final String _secretProviderId;
	private final Map<String, String> _secrets = new ConcurrentHashMap<>();

}