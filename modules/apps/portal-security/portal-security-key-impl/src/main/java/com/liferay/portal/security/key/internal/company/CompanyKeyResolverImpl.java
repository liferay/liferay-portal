/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.key.internal.company;

import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.kernel.encryptor.CompanyKeyResolver;
import com.liferay.portal.kernel.exception.CompanyKeyException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.security.key.KeyReference;
import com.liferay.portal.security.key.crypto.CryptoManager;
import com.liferay.portal.security.key.crypto.CryptoServiceResult;
import com.liferay.portal.security.key.crypto.exception.CryptoException;
import com.liferay.portal.security.key.internal.profile.configuration.KeyManagerConfiguration;
import com.liferay.portal.security.key.spi.profile.KeyManagerProfile;
import com.liferay.portal.security.key.spi.profile.KeyManagerProfileRegistry;

import java.security.Key;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

import javax.crypto.spec.SecretKeySpec;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Christopher Kian
 */
@Component(
	configurationPid = "com.liferay.portal.security.key.internal.profile.configuration.KeyManagerConfiguration",
	service = CompanyKeyResolver.class
)
public class CompanyKeyResolverImpl implements CompanyKeyResolver {

	@Override
	public boolean isEnabled(long companyId) {
		String companyKEKIdentifier = _getCompanyKEKIdentifier();

		if (Validator.isNotNull(companyKEKIdentifier)) {
			return true;
		}

		if (_log.isWarnEnabled()) {
			if (PropsValues.FIPS_ENABLED) {
				_log.warn(
					StringBundler.concat(
						"The company key is stored in plaintext in FIPS mode ",
						"because the KEK identifier is not configured for ",
						"company ", companyId));
			}
			else if (!Validator.isBlank(companyKEKIdentifier)) {
				_log.warn(
					"Company key wrapping is inactive because the KEK " +
						"identifier is set to an unusable value for company " +
							companyId);
			}
		}

		return false;
	}

	@Override
	public Key unwrapKey(long companyId, String wrappedKey) {
		Key key = _getCachedKey(companyId, wrappedKey);

		if (key != null) {
			return key;
		}

		return _decryptKey(
			companyId, WrappedCompanyKey.parse(companyId, wrappedKey),
			wrappedKey);
	}

	@Override
	public String wrapKey(long companyId, Key key) {
		String companyKEKIdentifier = _getCompanyKEKIdentifier();

		if (Validator.isNull(companyKEKIdentifier)) {
			throw new CompanyKeyException(
				"KEK identifier is not configured for company " + companyId);
		}

		String companyKEKProviderId = _getCompanyKEKProviderId();

		if (Validator.isNull(companyKEKProviderId)) {
			throw new CompanyKeyException(
				"KEK provider is not configured for company " + companyId);
		}

		if (Objects.equals(companyKEKProviderId, StringPool.STAR)) {
			throw new CompanyKeyException(
				"KEK provider must name a single provider rather than a " +
					"wildcard for company " + companyId);
		}

		byte[] keyBytes = key.getEncoded();

		if (ArrayUtil.isEmpty(keyBytes)) {
			throw new CompanyKeyException(
				"Key has no encoded key material for company " + companyId);
		}

		byte[] plaintextKeyBytes = Arrays.copyOf(keyBytes, keyBytes.length);

		try {
			List<String> cryptoProviderIds =
				_cryptoManager.getCryptoProviderIds(companyId);

			if ((cryptoProviderIds == null) ||
				!cryptoProviderIds.contains(companyKEKProviderId)) {

				throw new CompanyKeyException(
					StringBundler.concat(
						"KEK provider ", companyKEKProviderId,
						" is not registered for company ", companyId));
			}

			KeyReference keyReference = new KeyReference(
				companyKEKIdentifier, companyKEKProviderId,
				KeyReference.Type.CRYPTO);

			CryptoServiceResult<byte[]> cryptoServiceResult =
				_cryptoManager.encrypt(
					companyId, keyReference, plaintextKeyBytes);

			byte[] ciphertext = cryptoServiceResult.getValue();

			if (ArrayUtil.isEmpty(ciphertext)) {
				throw new CompanyKeyException(
					"Encrypting the key returned no ciphertext for company " +
						companyId);
			}

			WrappedCompanyKey wrappedCompanyKey = new WrappedCompanyKey(
				ciphertext, keyReference);

			String wrappedKey = wrappedCompanyKey.toWrappedKey();

			_putCompanyKeyCacheEntry(companyId, keyBytes, wrappedKey);

			return wrappedKey;
		}
		catch (CryptoException cryptoException) {
			throw new CompanyKeyException(
				"Unable to encrypt the key for company " + companyId,
				cryptoException);
		}
		finally {
			Arrays.fill(keyBytes, (byte)0);
			Arrays.fill(plaintextKeyBytes, (byte)0);
		}
	}

	@Activate
	@Modified
	protected void activate(Map<String, Object> properties) {
		_keyManagerConfiguration = ConfigurableUtil.createConfigurable(
			KeyManagerConfiguration.class, properties);

		_clearCompanyKeyCacheEntries();
	}

	@Deactivate
	protected void deactivate() {
		_clearCompanyKeyCacheEntries();

		_keyManagerConfiguration = null;
	}

	private void _clearCompanyKeyCacheEntries() {
		for (CompanyKeyCacheEntry companyKeyCacheEntry :
				_companyKeyCacheEntries.values()) {

			companyKeyCacheEntry.destroy();
		}

		_companyKeyCacheEntries.clear();
	}

	private Key _createKey(byte[] keyBytes) {
		return new SecretKeySpec(keyBytes, _getKeyAlgorithm());
	}

	private Key _decryptKey(
		long companyId, WrappedCompanyKey wrappedCompanyKey,
		String wrappedKey) {

		byte[] keyBytes = null;

		try {
			CryptoServiceResult<byte[]> cryptoServiceResult =
				_cryptoManager.decrypt(
					wrappedCompanyKey.getCiphertext(), companyId,
					wrappedCompanyKey.getKeyReference());

			keyBytes = cryptoServiceResult.getValue();

			if (ArrayUtil.isEmpty(keyBytes)) {
				throw new CompanyKeyException(
					StringBundler.concat(
						"Decrypting the wrapped key returned no key material ",
						"for company ", companyId));
			}

			_putCompanyKeyCacheEntry(companyId, keyBytes, wrappedKey);

			return _createKey(keyBytes);
		}
		catch (CryptoException cryptoException) {
			throw new CompanyKeyException(
				"Unable to decrypt the wrapped key for company " + companyId,
				cryptoException);
		}
		finally {
			if (keyBytes != null) {
				Arrays.fill(keyBytes, (byte)0);
			}
		}
	}

	private void _destroyExpiredCompanyKeyCacheEntries() {
		long time = System.currentTimeMillis();

		_companyKeyCacheEntries.forEach(
			(companyId, companyKeyCacheEntry) -> {
				if (companyKeyCacheEntry.isExpired(time) &&
					_companyKeyCacheEntries.remove(
						companyId, companyKeyCacheEntry)) {

					companyKeyCacheEntry.destroy();
				}
			});
	}

	private long _getCacheTTLMillis() {
		KeyManagerConfiguration keyManagerConfiguration =
			_keyManagerConfiguration;

		if (keyManagerConfiguration == null) {
			return 0;
		}

		long cacheTTLSeconds = keyManagerConfiguration.companyKeyCacheTTL();

		return cacheTTLSeconds * 1000;
	}

	private Key _getCachedKey(long companyId, String wrappedKey) {
		CompanyKeyCacheEntry companyKeyCacheEntry = _getCompanyKeyCacheEntry(
			companyId, wrappedKey);

		if (companyKeyCacheEntry == null) {
			return null;
		}

		byte[] keyBytes = companyKeyCacheEntry.getKeyBytes();

		if (keyBytes == null) {
			return null;
		}

		try {
			return _createKey(keyBytes);
		}
		finally {
			Arrays.fill(keyBytes, (byte)0);
		}
	}

	private String _getCompanyKEKIdentifier() {
		KeyManagerConfiguration keyManagerConfiguration =
			_keyManagerConfiguration;

		if (keyManagerConfiguration == null) {
			return null;
		}

		return keyManagerConfiguration.companyKEKIdentifier();
	}

	private String _getCompanyKEKProviderId() {
		KeyManagerProfile keyManagerProfile =
			_keyManagerProfileRegistry.getActiveKeyManagerProfile();

		if (keyManagerProfile == null) {
			return null;
		}

		return keyManagerProfile.getCompanyKEKProviderId();
	}

	private CompanyKeyCacheEntry _getCompanyKeyCacheEntry(
		long companyId, String wrappedKey) {

		CompanyKeyCacheEntry companyKeyCacheEntry = _companyKeyCacheEntries.get(
			companyId);

		if (companyKeyCacheEntry == null) {
			return null;
		}

		if (companyKeyCacheEntry.isExpired(System.currentTimeMillis()) ||
			!Objects.equals(companyKeyCacheEntry.getWrappedKey(), wrappedKey)) {

			if (_companyKeyCacheEntries.remove(
					companyId, companyKeyCacheEntry)) {

				companyKeyCacheEntry.destroy();
			}

			return null;
		}

		return companyKeyCacheEntry;
	}

	private String _getKeyAlgorithm() {
		String keyAlgorithm = _keyAlgorithm;

		if (keyAlgorithm == null) {
			keyAlgorithm = StringUtil.toUpperCase(
				GetterUtil.getString(
					PropsUtil.get(PropsKeys.COMPANY_ENCRYPTION_ALGORITHM)));

			_keyAlgorithm = keyAlgorithm;
		}

		return keyAlgorithm;
	}

	private void _putCompanyKeyCacheEntry(
		long companyId, byte[] keyBytes, String wrappedKey) {

		long cacheTTLMillis = _getCacheTTLMillis();

		if (cacheTTLMillis <= 0) {
			return;
		}

		_destroyExpiredCompanyKeyCacheEntries();

		CompanyKeyCacheEntry companyKeyCacheEntry = _companyKeyCacheEntries.put(
			companyId,
			new CompanyKeyCacheEntry(
				System.currentTimeMillis() + cacheTTLMillis, keyBytes,
				wrappedKey));

		if (companyKeyCacheEntry != null) {
			companyKeyCacheEntry.destroy();
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		CompanyKeyResolverImpl.class);

	private final Map<Long, CompanyKeyCacheEntry> _companyKeyCacheEntries =
		new ConcurrentHashMap<>();

	@Reference
	private CryptoManager _cryptoManager;

	private volatile String _keyAlgorithm;
	private volatile KeyManagerConfiguration _keyManagerConfiguration;

	@Reference
	private KeyManagerProfileRegistry _keyManagerProfileRegistry;

}