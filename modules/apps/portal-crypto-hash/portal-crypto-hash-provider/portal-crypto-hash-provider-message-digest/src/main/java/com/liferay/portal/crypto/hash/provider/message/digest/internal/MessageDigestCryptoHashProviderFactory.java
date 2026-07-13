/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.crypto.hash.provider.message.digest.internal;

import com.liferay.portal.crypto.hash.exception.CryptoHashException;
import com.liferay.portal.crypto.hash.spi.CryptoHashProvider;
import com.liferay.portal.crypto.hash.spi.CryptoHashProviderFactory;
import com.liferay.portal.crypto.hash.spi.CryptoHashProviderResponse;
import com.liferay.portal.kernel.security.SecureRandomUtil;
import com.liferay.portal.kernel.security.fips.FIPSModeValidator;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.DigesterUtil;
import com.liferay.portal.kernel.util.MapUtil;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import java.util.Collections;
import java.util.Map;

import org.osgi.service.component.annotations.Component;

/**
 * @author Arthur Chan
 */
@Component(
	property = "configuration.pid=com.liferay.portal.crypto.hash.provider.message.digest.internal.configuration.MessageDigestCryptoHashProviderConfiguration",
	service = CryptoHashProviderFactory.class
)
public class MessageDigestCryptoHashProviderFactory
	implements CryptoHashProviderFactory {

	@Override
	public CryptoHashProvider create(
			Map<String, ?> cryptoHashProviderProperties)
		throws CryptoHashException {

		try {
			if (cryptoHashProviderProperties == null) {
				return new MessageDigestCryptoHashProvider(
					Collections.emptyMap());
			}

			return new MessageDigestCryptoHashProvider(
				cryptoHashProviderProperties);
		}
		catch (NoSuchAlgorithmException noSuchAlgorithmException) {
			throw new CryptoHashException(noSuchAlgorithmException);
		}
	}

	@Override
	public String getCryptoHashProviderFactoryName() {
		return _CRYPTO_HASH_PROVIDER_FACTORY_NAME;
	}

	private static final String _CRYPTO_HASH_PROVIDER_FACTORY_NAME =
		"MessageDigest";

	private static class MessageDigestCryptoHashProvider
		implements CryptoHashProvider {

		public MessageDigestCryptoHashProvider(
				Map<String, ?> cryptoHashProviderProperties)
			throws NoSuchAlgorithmException {

			_cryptoHashProviderProperties = cryptoHashProviderProperties;

			String algorithm = MapUtil.getString(
				cryptoHashProviderProperties, "message.digest.algorithm",
				DigesterUtil.SHA_256);

			FIPSModeValidator.validateAlgorithm(algorithm);

			_messageDigest = MessageDigest.getInstance(algorithm);

			_saltSize = MapUtil.getInteger(
				cryptoHashProviderProperties, "message.digest.salt.size", 32);
		}

		@Override
		public CryptoHashProviderResponse generate(byte[] salt, byte[] input) {
			return new CryptoHashProviderResponse(
				_CRYPTO_HASH_PROVIDER_FACTORY_NAME,
				Collections.unmodifiableMap(_cryptoHashProviderProperties),
				_messageDigest.digest(ArrayUtil.append(salt, input)));
		}

		@Override
		public byte[] generateSalt() {
			byte[] salt = new byte[_saltSize];

			for (int i = 0; i < salt.length; ++i) {
				salt[i] = SecureRandomUtil.nextByte();
			}

			return salt;
		}

		private final Map<String, ?> _cryptoHashProviderProperties;
		private final MessageDigest _messageDigest;
		private final int _saltSize;

	}

}