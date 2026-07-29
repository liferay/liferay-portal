/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.key.internal.crypto;

import com.liferay.osgi.service.tracker.collections.map.PropertyServiceReferenceMapper;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMap;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMapFactory;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.CompanyConstants;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.security.key.KeyReference;
import com.liferay.portal.security.key.ServiceIndicator;
import com.liferay.portal.security.key.crypto.CryptoKey;
import com.liferay.portal.security.key.crypto.CryptoManager;
import com.liferay.portal.security.key.crypto.CryptoServiceResult;
import com.liferay.portal.security.key.crypto.exception.CryptoException;
import com.liferay.portal.security.key.spi.ProviderStatus;
import com.liferay.portal.security.key.spi.crypto.CryptoProvider;
import com.liferay.portal.security.key.spi.profile.KeyManagerProfile;
import com.liferay.portal.security.key.spi.profile.KeyManagerProfileRegistry;

import java.security.Key;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Tomas Polesovsky
 * @author Christopher Kian
 */
@Component(service = CryptoManager.class)
public class CryptoManagerImpl implements CryptoManager {

	@Override
	public CryptoServiceResult<byte[]> decrypt(
			byte[] ciphertext, long companyId, KeyReference keyReference)
		throws CryptoException {

		if (ciphertext == null) {
			throw new IllegalArgumentException("Ciphertext is null");
		}

		if (keyReference == null) {
			throw new IllegalArgumentException("Key reference is null");
		}

		try {
			CryptoProvider cryptoProvider = _getCryptoProvider(
				companyId, keyReference.getProviderId(), ProviderRole.DEK);

			CryptoServiceResult<byte[]> cryptoServiceResult =
				cryptoProvider.decrypt(
					ciphertext, companyId, keyReference.getIdentifier());

			_logServiceIndicator(
				companyId, "decrypt",
				cryptoServiceResult.getServiceIndicator());

			return cryptoServiceResult;
		}
		catch (CryptoException cryptoException) {
			if (_log.isWarnEnabled()) {
				_log.warn("Unable to decrypt ciphertext", cryptoException);
			}

			throw cryptoException;
		}
	}

	@Override
	public void deleteKey(long companyId, KeyReference keyReference)
		throws CryptoException {

		if (keyReference == null) {
			throw new IllegalArgumentException("Key reference is null");
		}

		try {
			CryptoProvider cryptoProvider = _getCryptoProvider(
				companyId, keyReference.getProviderId(), ProviderRole.DEK);

			cryptoProvider.deleteKey(companyId, keyReference.getIdentifier());
		}
		catch (CryptoException cryptoException) {
			if (_log.isWarnEnabled()) {
				_log.warn("Unable to delete key", cryptoException);
			}

			throw cryptoException;
		}
	}

	@Override
	public CryptoServiceResult<byte[]> encrypt(
			long companyId, KeyReference keyReference, byte[] plaintext)
		throws CryptoException {

		if (keyReference == null) {
			throw new IllegalArgumentException("Key reference is null");
		}

		if (plaintext == null) {
			throw new IllegalArgumentException("Plaintext is null");
		}

		try {
			CryptoProvider cryptoProvider = _getCryptoProvider(
				companyId, keyReference.getProviderId(), ProviderRole.DEK);

			CryptoServiceResult<byte[]> cryptoServiceResult =
				cryptoProvider.encrypt(
					companyId, keyReference.getIdentifier(), plaintext);

			_logServiceIndicator(
				companyId, "encrypt",
				cryptoServiceResult.getServiceIndicator());

			return cryptoServiceResult;
		}
		catch (CryptoException cryptoException) {
			if (_log.isWarnEnabled()) {
				_log.warn("Unable to encrypt plaintext", cryptoException);
			}

			throw cryptoException;
		}
	}

	@Override
	public CryptoServiceResult<Key> exportKey(
			long companyId, KeyReference keyReference)
		throws CryptoException {

		if (keyReference == null) {
			throw new IllegalArgumentException("Key reference is null");
		}

		try {
			CryptoProvider cryptoProvider = _getCryptoProvider(
				companyId, keyReference.getProviderId(), ProviderRole.DEK);

			CryptoServiceResult<Key> cryptoServiceResult =
				cryptoProvider.exportKey(
					companyId, keyReference.getIdentifier());

			_logServiceIndicator(
				companyId, "exportKey",
				cryptoServiceResult.getServiceIndicator());

			return cryptoServiceResult;
		}
		catch (CryptoException cryptoException) {
			if (_log.isWarnEnabled()) {
				_log.warn("Unable to export key", cryptoException);
			}

			throw cryptoException;
		}
	}

	@Override
	public CryptoServiceResult<KeyReference> generateAsymmetricKeyReference(
			String algorithm, long companyId, KeyReference keyReference)
		throws CryptoException {

		if (Validator.isNull(algorithm)) {
			throw new IllegalArgumentException("Algorithm is null");
		}

		if (keyReference == null) {
			throw new IllegalArgumentException("Key reference is null");
		}

		try {
			return _generateKeyReference(
				algorithm, companyId,
				CryptoProvider::generateAsymmetricKeyIdentifier, keyReference,
				"generateAsymmetricKeyReference");
		}
		catch (CryptoException cryptoException) {
			if (_log.isWarnEnabled()) {
				_log.warn(
					"Unable to generate asymmetric key reference",
					cryptoException);
			}

			throw cryptoException;
		}
	}

	@Override
	public CryptoServiceResult<KeyReference> generateSecretKeyReference(
			String algorithm, long companyId, KeyReference keyReference)
		throws CryptoException {

		if (Validator.isNull(algorithm)) {
			throw new IllegalArgumentException("Algorithm is null");
		}

		if (keyReference == null) {
			throw new IllegalArgumentException("Key reference is null");
		}

		try {
			return _generateKeyReference(
				algorithm, companyId,
				CryptoProvider::generateSecretKeyIdentifier, keyReference,
				"generateSecretKeyReference");
		}
		catch (CryptoException cryptoException) {
			if (_log.isWarnEnabled()) {
				_log.warn(
					"Unable to generate secret key reference", cryptoException);
			}

			throw cryptoException;
		}
	}

	@Override
	public CryptoKey getCryptoKey(long companyId, KeyReference keyReference)
		throws CryptoException {

		if (keyReference == null) {
			throw new IllegalArgumentException("Key reference is null");
		}

		try {
			CryptoProvider cryptoProvider = _getCryptoProvider(
				companyId, keyReference.getProviderId(), ProviderRole.DEK);

			return cryptoProvider.getCryptoKey(
				companyId, keyReference.getIdentifier());
		}
		catch (CryptoException cryptoException) {
			if (_log.isWarnEnabled()) {
				_log.warn("Unable to get crypto key", cryptoException);
			}

			throw cryptoException;
		}
	}

	@Override
	public List<String> getCryptoProviderIds(long companyId) {
		List<String> cryptoProviderIds = new ArrayList<>();

		ServiceTrackerMap<String, List<CryptoProvider>> serviceTrackerMap =
			_serviceTrackerMap;

		if (serviceTrackerMap == null) {
			return cryptoProviderIds;
		}

		for (String cryptoProviderId : serviceTrackerMap.keySet()) {
			List<CryptoProvider> cryptoProviders = serviceTrackerMap.getService(
				cryptoProviderId);

			if (cryptoProviders == null) {
				continue;
			}

			for (CryptoProvider cryptoProvider : cryptoProviders) {
				if (cryptoProvider.isAllowedCompany(companyId)) {
					cryptoProviderIds.add(cryptoProviderId);

					break;
				}
			}
		}

		return cryptoProviderIds;
	}

	@Override
	public List<KeyReference> getKeyReferences(
			long companyId, String cryptoProviderId)
		throws CryptoException {

		if (Validator.isNull(cryptoProviderId)) {
			throw new IllegalArgumentException("Crypto provider ID is null");
		}

		try {
			cryptoProviderId = _getCryptoProviderId(
				companyId, cryptoProviderId, ProviderRole.DEK);

			CryptoProvider cryptoProvider = _getCryptoProvider(
				companyId, cryptoProviderId);

			return _toKeyReferences(
				cryptoProviderId, cryptoProvider.getKeyIdentifiers(companyId));
		}
		catch (CryptoException cryptoException) {
			if (_log.isWarnEnabled()) {
				_log.warn("Unable to get key identifiers", cryptoException);
			}

			throw cryptoException;
		}
	}

	@Override
	public CryptoServiceResult<KeyReference> importSecretKey(
			String algorithm, long companyId, byte[] keyBytes,
			KeyReference keyReference)
		throws CryptoException {

		if (Validator.isNull(algorithm)) {
			throw new IllegalArgumentException("Algorithm is null");
		}

		if (keyBytes == null) {
			throw new IllegalArgumentException("Key bytes are null");
		}

		if (keyReference == null) {
			throw new IllegalArgumentException("Key reference is null");
		}

		try {
			String cryptoProviderId = _getCryptoProviderId(
				companyId, keyReference.getProviderId(), ProviderRole.DEK);

			CryptoProvider cryptoProvider = _getCryptoProvider(
				companyId, cryptoProviderId);

			CryptoServiceResult<String> cryptoServiceResult =
				cryptoProvider.importSecretKey(
					algorithm, companyId, keyBytes,
					keyReference.getIdentifier());

			_logServiceIndicator(
				companyId, "importSecretKey",
				cryptoServiceResult.getServiceIndicator());

			return new CryptoServiceResult<>(
				cryptoServiceResult.getServiceIndicator(),
				new KeyReference(
					cryptoServiceResult.getValue(), cryptoProviderId,
					KeyReference.Type.CRYPTO));
		}
		catch (CryptoException cryptoException) {
			if (_log.isWarnEnabled()) {
				_log.warn("Unable to import secret key", cryptoException);
			}

			throw cryptoException;
		}
		finally {
			Arrays.fill(keyBytes, (byte)0);
		}
	}

	@Override
	public CryptoServiceResult<KeyReference> unwrap(
			long companyId, KeyReference keyReference,
			KeyReference masterKeyReference, String wrappedKeyAlgorithm,
			byte[] wrappedKeyBytes, int wrappedKeyCipherType)
		throws CryptoException {

		if (keyReference == null) {
			throw new IllegalArgumentException("Key reference is null");
		}

		if (masterKeyReference == null) {
			throw new IllegalArgumentException("Master key reference is null");
		}

		if (Validator.isNull(wrappedKeyAlgorithm)) {
			throw new IllegalArgumentException("Wrapped key algorithm is null");
		}

		if (wrappedKeyBytes == null) {
			throw new IllegalArgumentException("Wrapped key bytes are null");
		}

		try {
			String cryptoProviderId = _getCryptoProviderId(
				companyId, keyReference, masterKeyReference);

			CryptoProvider cryptoProvider = _getCryptoProvider(
				companyId, cryptoProviderId);

			CryptoServiceResult<String> cryptoServiceResult =
				cryptoProvider.unwrap(
					companyId, keyReference.getIdentifier(),
					masterKeyReference.getIdentifier(), wrappedKeyAlgorithm,
					wrappedKeyBytes, wrappedKeyCipherType);

			_logServiceIndicator(
				companyId, "unwrap", cryptoServiceResult.getServiceIndicator());

			return new CryptoServiceResult<>(
				cryptoServiceResult.getServiceIndicator(),
				new KeyReference(
					cryptoServiceResult.getValue(), cryptoProviderId,
					KeyReference.Type.CRYPTO));
		}
		catch (CryptoException cryptoException) {
			if (_log.isWarnEnabled()) {
				_log.warn("Unable to unwrap key", cryptoException);
			}

			throw cryptoException;
		}
	}

	@Override
	public CryptoServiceResult<byte[]> wrap(
			long companyId, KeyReference keyReference,
			KeyReference masterKeyReference)
		throws CryptoException {

		if (keyReference == null) {
			throw new IllegalArgumentException("Key reference is null");
		}

		if (masterKeyReference == null) {
			throw new IllegalArgumentException("Master key reference is null");
		}

		try {
			CryptoProvider cryptoProvider = _getCryptoProvider(
				companyId,
				_getCryptoProviderId(
					companyId, keyReference, masterKeyReference));

			CryptoServiceResult<byte[]> cryptoServiceResult =
				cryptoProvider.wrap(
					companyId, keyReference.getIdentifier(),
					masterKeyReference.getIdentifier());

			_logServiceIndicator(
				companyId, "wrap", cryptoServiceResult.getServiceIndicator());

			return cryptoServiceResult;
		}
		catch (CryptoException cryptoException) {
			if (_log.isWarnEnabled()) {
				_log.warn("Unable to wrap key", cryptoException);
			}

			throw cryptoException;
		}
	}

	@Activate
	protected void activate(BundleContext bundleContext) {
		_serviceTrackerMap = ServiceTrackerMapFactory.openMultiValueMap(
			bundleContext, CryptoProvider.class, "(crypto.provider.id=*)",
			new PropertyServiceReferenceMapper<>("crypto.provider.id"));
	}

	@Deactivate
	protected void deactivate() {
		if (_serviceTrackerMap != null) {
			_serviceTrackerMap.close();

			_serviceTrackerMap = null;
		}
	}

	private CryptoServiceResult<KeyReference> _generateKeyReference(
			String algorithm, long companyId, KeyGenerator keyGenerator,
			KeyReference keyReference, String operation)
		throws CryptoException {

		String cryptoProviderId = _getCryptoProviderId(
			companyId, keyReference.getProviderId(), ProviderRole.DEK);

		CryptoServiceResult<String> cryptoServiceResult = keyGenerator.generate(
			_getCryptoProvider(companyId, cryptoProviderId), algorithm,
			companyId, keyReference.getIdentifier());

		_logServiceIndicator(
			companyId, operation, cryptoServiceResult.getServiceIndicator());

		return new CryptoServiceResult<>(
			cryptoServiceResult.getServiceIndicator(),
			new KeyReference(
				cryptoServiceResult.getValue(), cryptoProviderId,
				KeyReference.Type.CRYPTO));
	}

	private CryptoProvider _getCryptoProvider(
			long companyId, String cryptoProviderId)
		throws CryptoException {

		List<CryptoProvider> cryptoProviders = _serviceTrackerMap.getService(
			cryptoProviderId);

		if (cryptoProviders != null) {
			for (CryptoProvider cryptoProvider : cryptoProviders) {
				if (!cryptoProvider.isAllowedCompany(companyId)) {
					continue;
				}

				if (cryptoProvider.getProviderStatus() ==
						ProviderStatus.ERROR) {

					throw new CryptoException(
						StringBundler.concat(
							"Crypto provider ", cryptoProviderId,
							" is in an error state for company ID ",
							companyId));
				}

				return cryptoProvider;
			}
		}

		throw new CryptoException(
			StringBundler.concat(
				"No crypto provider found for ID ", cryptoProviderId,
				" and company ID ", companyId));
	}

	private CryptoProvider _getCryptoProvider(
			long companyId, String cryptoProviderId, ProviderRole providerRole)
		throws CryptoException {

		return _getCryptoProvider(
			companyId,
			_getCryptoProviderId(companyId, cryptoProviderId, providerRole));
	}

	private String _getCryptoProviderId(
			long companyId, KeyReference keyReference,
			KeyReference masterKeyReference)
		throws CryptoException {

		String cryptoProviderId = _getCryptoProviderId(
			companyId, keyReference.getProviderId(), ProviderRole.DEK);

		String masterCryptoProviderId = _getCryptoProviderId(
			companyId, masterKeyReference.getProviderId(), ProviderRole.KEK);

		if (!Objects.equals(cryptoProviderId, masterCryptoProviderId)) {
			throw new CryptoException(
				StringBundler.concat(
					"Key provider ", cryptoProviderId,
					" does not match master key provider ",
					masterCryptoProviderId));
		}

		return cryptoProviderId;
	}

	private String _getCryptoProviderId(
			long companyId, String cryptoProviderId, ProviderRole providerRole)
		throws CryptoException {

		if (Validator.isNull(cryptoProviderId)) {
			throw new IllegalArgumentException("Crypto provider ID is null");
		}

		if (!Objects.equals(cryptoProviderId, StringPool.STAR)) {
			return cryptoProviderId;
		}

		KeyManagerProfile activeKeyManagerProfile =
			_keyManagerProfileRegistry.getActiveKeyManagerProfile();

		if (activeKeyManagerProfile == null) {
			throw new CryptoException(
				StringBundler.concat(
					"No active key manager profile found to resolve the ",
					"provider wildcard for company ID ", companyId));
		}

		if (companyId == CompanyConstants.SYSTEM) {
			if (providerRole == ProviderRole.DEK) {
				cryptoProviderId =
					activeKeyManagerProfile.getSystemDEKProviderId();
			}
			else {
				cryptoProviderId =
					activeKeyManagerProfile.getSystemKEKProviderId();
			}
		}
		else if (providerRole == ProviderRole.DEK) {
			cryptoProviderId =
				activeKeyManagerProfile.getCompanyDEKProviderId();
		}
		else {
			cryptoProviderId =
				activeKeyManagerProfile.getCompanyKEKProviderId();
		}

		if (Validator.isNull(cryptoProviderId)) {
			throw new CryptoException(
				StringBundler.concat(
					"The active key manager profile does not configure a ",
					"crypto provider ID for provider role ", providerRole,
					" and company ID ", companyId));
		}

		return cryptoProviderId;
	}

	private void _logServiceIndicator(
		long companyId, String operation, ServiceIndicator serviceIndicator) {

		if (serviceIndicator == null) {
			if (_log.isWarnEnabled()) {
				_log.warn(
					StringBundler.concat(
						"Operation ", operation, " for company ID ", companyId,
						" returned a null service indicator"));
			}

			return;
		}

		if (_log.isInfoEnabled()) {
			String approval = "a nonapproved";

			if (serviceIndicator.isApproved()) {
				approval = "an approved";
			}

			_log.info(
				StringBundler.concat(
					"Operation ", operation, " for company ID ", companyId,
					" used ", approval, " security function ",
					serviceIndicator.getSecurityFunctionName()));
		}
	}

	private List<KeyReference> _toKeyReferences(
		String cryptoProviderId, List<String> keyIdentifiers) {

		return TransformUtil.transform(
			keyIdentifiers,
			keyIdentifier -> new KeyReference(
				keyIdentifier, cryptoProviderId, KeyReference.Type.CRYPTO));
	}

	private static final Log _log = LogFactoryUtil.getLog(
		CryptoManagerImpl.class);

	@Reference
	private KeyManagerProfileRegistry _keyManagerProfileRegistry;

	private ServiceTrackerMap<String, List<CryptoProvider>> _serviceTrackerMap;

	@FunctionalInterface
	private interface KeyGenerator {

		public CryptoServiceResult<String> generate(
				CryptoProvider cryptoProvider, String algorithm, long companyId,
				String keyIdentifier)
			throws CryptoException;

	}

	private enum ProviderRole {

		DEK, KEK

	}

}