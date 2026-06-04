/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.crypto.hash.provider.bcrypt.internal;

import com.liferay.portal.crypto.hash.exception.CryptoHashException;
import com.liferay.portal.crypto.hash.spi.CryptoHashProvider;
import com.liferay.portal.crypto.hash.spi.CryptoHashProviderFactory;
import com.liferay.portal.crypto.hash.spi.CryptoHashProviderResponse;
import com.liferay.portal.kernel.util.MapUtil;

import java.nio.charset.StandardCharsets;

import java.util.Collections;
import java.util.Map;

import jodd.util.BCrypt;

import org.osgi.service.component.annotations.Component;

/**
 * @author Arthur Chan
 */
@Component(
	enabled = false,
	property = "configuration.pid=com.liferay.portal.crypto.hash.provider.bcrypt.internal.configuration.BCryptCryptoHashProviderConfiguration",
	service = CryptoHashProviderFactory.class
)
public class BCryptCryptoHashProviderFactory
	implements CryptoHashProviderFactory {

	@Override
	public CryptoHashProvider create(
			Map<String, ?> cryptoHashProviderProperties)
		throws CryptoHashException {

		if ((cryptoHashProviderProperties == null) ||
			cryptoHashProviderProperties.isEmpty()) {

			return new BCryptCryptoHashProvider(Collections.emptyMap());
		}

		return new BCryptCryptoHashProvider(cryptoHashProviderProperties);
	}

	@Override
	public String getCryptoHashProviderFactoryName() {
		return _CRYPTO_HASH_PROVIDER_FACTORY_NAME;
	}

	private static final String _CRYPTO_HASH_PROVIDER_FACTORY_NAME = "BCrypt";

	private static class BCryptCryptoHashProvider
		implements CryptoHashProvider {

		public BCryptCryptoHashProvider(
			Map<String, ?> cryptoHashProviderProperties) {

			_cryptoHashProviderProperties = cryptoHashProviderProperties;

			_rounds = MapUtil.getInteger(
				cryptoHashProviderProperties, "bcrypt.rounds", 10);
		}

		@Override
		public CryptoHashProviderResponse generate(byte[] salt, byte[] input) {
			String hashedPassword = BCrypt.hashpw(
				new String(input, StandardCharsets.US_ASCII),
				new String(salt, StandardCharsets.US_ASCII));

			return new CryptoHashProviderResponse(
				_CRYPTO_HASH_PROVIDER_FACTORY_NAME,
				Collections.unmodifiableMap(_cryptoHashProviderProperties),
				hashedPassword.getBytes(StandardCharsets.US_ASCII));
		}

		@Override
		public byte[] generateSalt() {
			String salt = BCrypt.gensalt(_rounds);

			return salt.getBytes(StandardCharsets.US_ASCII);
		}

		private final Map<String, ?> _cryptoHashProviderProperties;
		private final int _rounds;

	}

}