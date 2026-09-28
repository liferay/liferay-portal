/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.key.internal.company;

import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.kernel.encryptor.CompanyKeyResolver;
import com.liferay.portal.kernel.encryptor.CompanyKeyResolverUtil;
import com.liferay.portal.kernel.exception.CompanyKeyException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.ObjectValuePair;
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
import java.util.Base64;
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
	public Key unwrapKey(long companyId, String keyString) {
		Key key = _getKey(companyId, keyString);

		if (key != null) {
			return key;
		}

		byte[] keyBytes = null;

		try {
			ObjectValuePair<byte[], KeyReference> objectValuePair =
				_parseCiphertextAndKeyReference(companyId, keyString);

			CryptoServiceResult<byte[]> cryptoServiceResult =
				_cryptoManager.decrypt(
					objectValuePair.getKey(), companyId,
					objectValuePair.getValue());

			keyBytes = cryptoServiceResult.getValue();

			if (ArrayUtil.isEmpty(keyBytes)) {
				throw new CompanyKeyException(
					StringBundler.concat(
						"Decrypting the wrapped key returned no key material ",
						"for company ", companyId));
			}

			_putCompanyKeyCacheEntry(companyId, keyBytes, keyString);

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

		byte[] encodedBytes = key.getEncoded();

		if (ArrayUtil.isEmpty(encodedBytes)) {
			throw new CompanyKeyException(
				"Key has no encoded key material for company " + companyId);
		}

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
				_cryptoManager.encrypt(companyId, keyReference, encodedBytes);

			if (ArrayUtil.isEmpty(cryptoServiceResult.getValue())) {
				throw new CompanyKeyException(
					"Encrypting the key returned no ciphertext for company " +
						companyId);
			}

			return _toKeyString(cryptoServiceResult.getValue(), keyReference);
		}
		catch (CryptoException cryptoException) {
			throw new CompanyKeyException(
				"Unable to encrypt the key for company " + companyId,
				cryptoException);
		}
		finally {
			Arrays.fill(encodedBytes, (byte)0);
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
		long companyId, String keyString) {

		CompanyKeyCacheEntry companyKeyCacheEntry = _companyKeyCacheEntries.get(
			companyId);

		if (companyKeyCacheEntry == null) {
			return null;
		}

		if (!Objects.equals(companyKeyCacheEntry.getKeyString(), keyString)) {
			if (_companyKeyCacheEntries.remove(
					companyId, companyKeyCacheEntry)) {

				companyKeyCacheEntry.destroy();
			}

			return null;
		}

		return companyKeyCacheEntry;
	}

	private Key _getKey(long companyId, String keyString) {
		CompanyKeyCacheEntry companyKeyCacheEntry = _getCompanyKeyCacheEntry(
			companyId, keyString);

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

	private ObjectValuePair<byte[], KeyReference>
		_parseCiphertextAndKeyReference(long companyId, String keyString) {

		if (!CompanyKeyResolverUtil.isWrappedKey(keyString) ||
			!keyString.endsWith(StringPool.CLOSE_CURLY_BRACE)) {

			throw new CompanyKeyException(
				"Wrapped key is malformed for company " + companyId);
		}

		String body = keyString.substring(
			"${wrappedKey:".length(), keyString.length() - 1);

		int versionIndex = body.indexOf(CharPool.COLON);

		if (versionIndex <= 0) {
			throw new CompanyKeyException(
				"Wrapped key is malformed for company " + companyId);
		}

		String version = body.substring(0, versionIndex);

		if (!version.equals("v1")) {
			throw new CompanyKeyException(
				StringBundler.concat(
					"Wrapped key version ", version,
					" is not supported for company ", companyId));
		}

		body = body.substring(versionIndex + 1);

		int colonIndex = body.indexOf(CharPool.COLON);
		int pipeIndex = body.indexOf(CharPool.PIPE);

		if ((colonIndex <= 0) || (pipeIndex <= (colonIndex + 1)) ||
			(pipeIndex >= (body.length() - 1))) {

			throw new CompanyKeyException(
				"Wrapped key is malformed for company " + companyId);
		}

		byte[] ciphertext = null;

		try {
			Base64.Decoder decoder = Base64.getDecoder();

			ciphertext = decoder.decode(body.substring(pipeIndex + 1));
		}
		catch (IllegalArgumentException illegalArgumentException) {
			throw new CompanyKeyException(
				"Wrapped key ciphertext is not valid Base64 for company " +
					companyId,
				illegalArgumentException);
		}

		try {
			KeyReference keyReference = new KeyReference(
				body.substring(colonIndex + 1, pipeIndex),
				body.substring(0, colonIndex), KeyReference.Type.CRYPTO);

			_validate(keyReference);

			return new ObjectValuePair<>(ciphertext, keyReference);
		}
		catch (IllegalArgumentException illegalArgumentException) {
			throw new CompanyKeyException(
				"Wrapped key is malformed for company " + companyId,
				illegalArgumentException);
		}
	}

	private void _putCompanyKeyCacheEntry(
		long companyId, byte[] keyBytes, String keyString) {

		CompanyKeyCacheEntry companyKeyCacheEntry = _companyKeyCacheEntries.put(
			companyId, new CompanyKeyCacheEntry(keyBytes, keyString));

		if (companyKeyCacheEntry != null) {
			companyKeyCacheEntry.destroy();
		}
	}

	private String _toKeyString(byte[] ciphertext, KeyReference keyReference) {
		_validate(keyReference);

		Base64.Encoder encoder = Base64.getEncoder();

		return StringBundler.concat(
			"${wrappedKey:v1:", keyReference.getProviderId(), StringPool.COLON,
			keyReference.getIdentifier(), StringPool.PIPE,
			encoder.encodeToString(ciphertext), StringPool.CLOSE_CURLY_BRACE);
	}

	private void _validate(KeyReference keyReference) {
		String identifier = keyReference.getIdentifier();

		if ((identifier.indexOf(CharPool.CLOSE_CURLY_BRACE) != -1) ||
			(identifier.indexOf(CharPool.PIPE) != -1)) {

			throw new IllegalArgumentException(
				"Identifier contains a reserved character");
		}

		String providerId = keyReference.getProviderId();

		if (providerId.indexOf(CharPool.PIPE) != -1) {
			throw new IllegalArgumentException(
				"Provider ID contains a reserved character");
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