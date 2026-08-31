/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.key.provider.aws.internal.crypto;

import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.kms.AWSKMS;
import com.amazonaws.services.kms.AWSKMSClientBuilder;
import com.amazonaws.services.kms.model.AliasListEntry;
import com.amazonaws.services.kms.model.CreateAliasRequest;
import com.amazonaws.services.kms.model.CreateKeyRequest;
import com.amazonaws.services.kms.model.CreateKeyResult;
import com.amazonaws.services.kms.model.DecryptRequest;
import com.amazonaws.services.kms.model.DecryptResult;
import com.amazonaws.services.kms.model.DeleteAliasRequest;
import com.amazonaws.services.kms.model.DescribeKeyRequest;
import com.amazonaws.services.kms.model.DescribeKeyResult;
import com.amazonaws.services.kms.model.EncryptRequest;
import com.amazonaws.services.kms.model.EncryptResult;
import com.amazonaws.services.kms.model.KeyMetadata;
import com.amazonaws.services.kms.model.KeySpec;
import com.amazonaws.services.kms.model.KeyUsageType;
import com.amazonaws.services.kms.model.ListAliasesRequest;
import com.amazonaws.services.kms.model.ListAliasesResult;
import com.amazonaws.services.kms.model.NotFoundException;
import com.amazonaws.services.kms.model.ScheduleKeyDeletionRequest;

import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.security.key.KeyReference;
import com.liferay.portal.security.key.ServiceIndicator;
import com.liferay.portal.security.key.crypto.CryptoKey;
import com.liferay.portal.security.key.crypto.CryptoServiceResult;
import com.liferay.portal.security.key.crypto.exception.CryptoException;
import com.liferay.portal.security.key.provider.aws.internal.fips.AWSKMSFIPSValidator;
import com.liferay.portal.security.key.provider.aws.internal.util.AWSARNUtil;
import com.liferay.portal.security.key.provider.aws.internal.util.AWSByteBufferUtil;
import com.liferay.portal.security.key.provider.aws.internal.util.AWSClientManager;
import com.liferay.portal.security.key.spi.ProviderStatus;
import com.liferay.portal.security.key.spi.crypto.CryptoProvider;

import java.nio.ByteBuffer;

import java.security.Key;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Modified;

/**
 * @author Christopher Kian
 */
public abstract class BaseAWSKMSCryptoProvider implements CryptoProvider {

	@Override
	public CryptoServiceResult<byte[]> decrypt(
			byte[] ciphertext, long companyId, String keyIdentifier)
		throws CryptoException {

		AWSKMSCryptoProviderContext awsKMSCryptoProviderContext =
			_getAWSKMSCryptoProviderContext(companyId);

		AWSClientManager<AWSKMS> awsClientManager =
			awsKMSCryptoProviderContext.getAWSClientManager();

		String keyId = _getKeyId(
			awsKMSCryptoProviderContext, companyId, keyIdentifier);

		ServiceIndicator serviceIndicator = _getServiceIndicator(
			awsKMSCryptoProviderContext, keyId, "AWS.KMS.Decrypt");

		try {
			DecryptResult decryptResult = awsClientManager.execute(
				awsKMS -> awsKMS.decrypt(
					new DecryptRequest(
					).withCiphertextBlob(
						ByteBuffer.wrap(ciphertext)
					).withKeyId(
						keyId
					)));

			return new CryptoServiceResult<>(
				serviceIndicator,
				AWSByteBufferUtil.getBytes(decryptResult.getPlaintext()));
		}
		catch (Exception exception) {
			throw new CryptoException(
				"Unable to decrypt with AWS KMS key " + keyId, exception);
		}
	}

	@Override
	public void deleteKey(long companyId, String keyIdentifier)
		throws CryptoException {

		AWSKMSCryptoProviderContext awsKMSCryptoProviderContext =
			_getAWSKMSCryptoProviderContext(companyId);

		AWSClientManager<AWSKMS> awsClientManager =
			awsKMSCryptoProviderContext.getAWSClientManager();
		String keyId = _getKeyId(
			awsKMSCryptoProviderContext, companyId, keyIdentifier);
		int pendingWindowInDays =
			awsKMSCryptoProviderContext.getPendingWindowInDays();

		String aliasName = _getAliasName(keyId);

		try {
			awsClientManager.execute(
				awsKMS -> {
					awsKMS.scheduleKeyDeletion(
						new ScheduleKeyDeletionRequest(
						).withKeyId(
							keyId
						).withPendingWindowInDays(
							pendingWindowInDays
						));

					if (aliasName != null) {
						try {
							awsKMS.deleteAlias(
								new DeleteAliasRequest(
								).withAliasName(
									aliasName
								));
						}
						catch (NotFoundException notFoundException) {
							if (_log.isDebugEnabled()) {
								_log.debug(
									"Alias " + aliasName +
										" was already removed",
									notFoundException);
							}
						}
					}

					return null;
				});
		}
		catch (Exception exception) {
			throw new CryptoException(
				"Unable to schedule deletion for AWS KMS key " + keyId,
				exception);
		}
	}

	@Override
	public CryptoServiceResult<byte[]> encrypt(
			long companyId, String keyIdentifier, byte[] plaintext)
		throws CryptoException {

		AWSKMSCryptoProviderContext awsKMSCryptoProviderContext =
			_getAWSKMSCryptoProviderContext(companyId);

		AWSKMSFIPSValidator awsKMSFIPSValidator =
			awsKMSCryptoProviderContext.getAWSKMSFIPSValidator();

		awsKMSFIPSValidator.validateCipherMode();

		AWSClientManager<AWSKMS> awsClientManager =
			awsKMSCryptoProviderContext.getAWSClientManager();

		String keyId = _getKeyId(
			awsKMSCryptoProviderContext, companyId, keyIdentifier);

		ServiceIndicator serviceIndicator = _getServiceIndicator(
			awsKMSCryptoProviderContext, keyId, "AWS.KMS.Encrypt");

		try {
			EncryptResult encryptResult = awsClientManager.execute(
				awsKMS -> awsKMS.encrypt(
					new EncryptRequest(
					).withKeyId(
						keyId
					).withPlaintext(
						ByteBuffer.wrap(plaintext)
					)));

			return new CryptoServiceResult<>(
				serviceIndicator,
				AWSByteBufferUtil.getBytes(encryptResult.getCiphertextBlob()));
		}
		catch (Exception exception) {
			throw new CryptoException(
				"Unable to encrypt with AWS KMS key " + keyId, exception);
		}
	}

	@Override
	public CryptoServiceResult<Key> exportKey(
			long companyId, String keyIdentifier)
		throws CryptoException {

		throw new CryptoException("Exporting key material is not supported");
	}

	@Override
	public CryptoServiceResult<String> generateAsymmetricKeyIdentifier(
			String algorithm, long companyId, String keyIdentifier)
		throws CryptoException {

		AWSKMSCryptoProviderContext awsKMSCryptoProviderContext =
			_getAWSKMSCryptoProviderContext(companyId);

		AWSKMSFIPSValidator awsKMSFIPSValidator =
			awsKMSCryptoProviderContext.getAWSKMSFIPSValidator();
		String identifier = _createKey(
			awsKMSCryptoProviderContext, companyId, keyIdentifier,
			_getKeySpec(algorithm), KeyUsageType.ENCRYPT_DECRYPT);

		return new CryptoServiceResult<>(
			awsKMSFIPSValidator.toServiceIndicator(
				awsKMSCryptoProviderContext.isUseFIPSEndpoint(),
				"AWS.KMS.CreateKey.Asymmetric"),
			identifier);
	}

	@Override
	public CryptoServiceResult<String> generateSecretKeyIdentifier(
			String algorithm, long companyId, String keyIdentifier)
		throws CryptoException {

		if ((algorithm != null) && !algorithm.equals("AES_256_GCM") &&
			!algorithm.equals("SYMMETRIC_DEFAULT")) {

			throw new CryptoException(
				"AWS KMS supports only AES_256_GCM secret keys, not \"" +
					algorithm + "\"");
		}

		AWSKMSCryptoProviderContext awsKMSCryptoProviderContext =
			_getAWSKMSCryptoProviderContext(companyId);

		AWSKMSFIPSValidator awsKMSFIPSValidator =
			awsKMSCryptoProviderContext.getAWSKMSFIPSValidator();

		awsKMSFIPSValidator.validateCipherMode();

		String identifier = _createKey(
			awsKMSCryptoProviderContext, companyId, keyIdentifier,
			KeySpec.SYMMETRIC_DEFAULT, KeyUsageType.ENCRYPT_DECRYPT);

		return new CryptoServiceResult<>(
			awsKMSFIPSValidator.toServiceIndicator(
				awsKMSCryptoProviderContext.isUseFIPSEndpoint(),
				"AWS.KMS.CreateKey.Symmetric"),
			identifier);
	}

	@Override
	public CryptoKey getCryptoKey(long companyId, String keyIdentifier)
		throws CryptoException {

		AWSKMSCryptoProviderContext awsKMSCryptoProviderContext =
			_getAWSKMSCryptoProviderContext(companyId);

		String keyId = _getKeyId(
			awsKMSCryptoProviderContext, companyId, keyIdentifier);

		try {
			KeyMetadata keyMetadata = _getKeyMetadata(
				awsKMSCryptoProviderContext, keyId);

			String algorithm = GetterUtil.getString(
				keyMetadata.getKeySpec(), "SYMMETRIC_DEFAULT");
			String cipherSpec = GetterUtil.getString(
				keyMetadata.getKeyUsage(), "ENCRYPT_DECRYPT");
			Date creationDate = keyMetadata.getCreationDate();

			long createTime = 0;

			if (creationDate != null) {
				createTime = creationDate.getTime();
			}

			return new CryptoKey(
				algorithm, cipherSpec, createTime,
				new KeyReference(
					keyId, getProviderId(), KeyReference.Type.CRYPTO));
		}
		catch (Exception exception) {
			throw new CryptoException(
				"Unable to describe AWS KMS key " + keyId, exception);
		}
	}

	@Override
	public List<String> getKeyIdentifiers(long companyId)
		throws CryptoException {

		AWSKMSCryptoProviderContext awsKMSCryptoProviderContext =
			_getAWSKMSCryptoProviderContext(companyId);

		String aliasPrefix = _resolveAliasPrefix(
			awsKMSCryptoProviderContext, companyId);

		if (Validator.isNull(aliasPrefix)) {
			return new ArrayList<>();
		}

		AWSClientManager<AWSKMS> awsClientManager =
			awsKMSCryptoProviderContext.getAWSClientManager();

		try {
			return awsClientManager.execute(
				awsKMS -> {
					List<String> keyIdentifiers = new ArrayList<>();

					ListAliasesRequest listAliasesRequest =
						new ListAliasesRequest(
						).withLimit(
							100
						);

					while (true) {
						ListAliasesResult listAliasesResult =
							awsKMS.listAliases(listAliasesRequest);

						for (AliasListEntry aliasListEntry :
								listAliasesResult.getAliases()) {

							String aliasName = aliasListEntry.getAliasName();

							if ((aliasName != null) &&
								aliasName.startsWith(aliasPrefix)) {

								keyIdentifiers.add(
									aliasName.substring(aliasPrefix.length()));
							}
						}

						String marker = listAliasesResult.getNextMarker();

						if (marker == null) {
							break;
						}

						listAliasesRequest.setMarker(marker);
					}

					return keyIdentifiers;
				});
		}
		catch (Exception exception) {
			throw new CryptoException(
				"Unable to list AWS KMS aliases under " + aliasPrefix,
				exception);
		}
	}

	@Override
	public ProviderStatus getProviderStatus() {
		AWSKMSCryptoProviderContext awsKMSCryptoProviderContext =
			_awsKMSCryptoProviderContext;

		if ((awsKMSCryptoProviderContext == null) ||
			!awsKMSCryptoProviderContext.isEnabled() ||
			Validator.isNull(awsKMSCryptoProviderContext.getAWSRegion())) {

			return ProviderStatus.DEGRADED;
		}

		return ProviderStatus.OPERATIONAL;
	}

	@Override
	public CryptoServiceResult<String> importSecretKey(
			String algorithm, long companyId, byte[] keyBytes,
			String keyIdentifier)
		throws CryptoException {

		throw new CryptoException("Importing key material is not supported");
	}

	@Override
	public CryptoServiceResult<String> unwrap(
			long companyId, String keyIdentifier, String masterKeyIdentifier,
			String wrappedKeyAlgorithm, byte[] wrappedKeyBytes,
			int wrappedKeyCipherType)
		throws CryptoException {

		throw new CryptoException("Unwrapping keys is not supported");
	}

	@Override
	public CryptoServiceResult<byte[]> wrap(
			long companyId, String keyIdentifier, String masterKeyIdentifier)
		throws CryptoException {

		throw new CryptoException("Wrapping keys is not supported");
	}

	@Activate
	@Modified
	protected void activate(Map<String, Object> properties) {
		String awsAccountId = GetterUtil.getString(
			properties.get("awsAccountId"));
		String awsRegion = GetterUtil.getString(properties.get("awsRegion"));
		String cipherMode = GetterUtil.getString(
			properties.get("cipherMode"), "AES_256_GCM");
		boolean enabled = GetterUtil.getBoolean(properties.get("enabled"));
		boolean fipsEnforced = GetterUtil.getBoolean(
			properties.get("fipsEnforced"));
		String keyARNTemplate = GetterUtil.getString(
			properties.get("keyARNTemplate"));
		int pendingWindowInDays = GetterUtil.getInteger(
			properties.get("pendingWindowInDays"), 30);
		boolean useFIPSEndpoint = GetterUtil.getBoolean(
			properties.get("useFIPSEndpoint"));

		AWSClientManager<AWSKMS> awsClientManager = null;
		AWSKMSCryptoProviderContext awsKMSCryptoProviderContext =
			_awsKMSCryptoProviderContext;

		if (awsKMSCryptoProviderContext != null) {
			awsClientManager =
				awsKMSCryptoProviderContext.getAWSClientManager();
		}

		if (awsClientManager == null) {
			awsClientManager = new AWSClientManager<>(
				awsRegion, BaseAWSKMSCryptoProvider::_buildAWSKMS,
				"kms-fips.{region}.amazonaws.com", useFIPSEndpoint);
		}
		else {
			awsClientManager.updateConfiguration(awsRegion, useFIPSEndpoint);
		}

		awsRegion = awsClientManager.getAWSRegion();

		_awsKMSCryptoProviderContext = new AWSKMSCryptoProviderContext(
			awsAccountId, awsClientManager,
			new AWSKMSFIPSValidator(cipherMode, fipsEnforced), awsRegion,
			enabled, keyARNTemplate, pendingWindowInDays, useFIPSEndpoint);

		if (_log.isInfoEnabled()) {
			_log.info(
				StringBundler.concat(
					"Activated ", getProviderId(), " in AWS region ",
					awsRegion));
		}
	}

	@Deactivate
	protected void deactivate() {
		AWSKMSCryptoProviderContext awsKMSCryptoProviderContext =
			_awsKMSCryptoProviderContext;
		_awsKMSCryptoProviderContext = null;

		if (awsKMSCryptoProviderContext != null) {
			AWSClientManager<AWSKMS> awsClientManager =
				awsKMSCryptoProviderContext.getAWSClientManager();

			awsClientManager.close();
		}
	}

	protected abstract String getProviderId();

	private static AWSKMS _buildAWSKMS(
		AWSCredentialsProvider awsCredentialsProvider, String awsRegion,
		AwsClientBuilder.EndpointConfiguration endpointConfiguration) {

		AWSKMSClientBuilder awsKMSClientBuilder = AWSKMSClientBuilder.standard(
		).withCredentials(
			awsCredentialsProvider
		);

		if (endpointConfiguration != null) {
			awsKMSClientBuilder.withEndpointConfiguration(
				endpointConfiguration);
		}
		else if (Validator.isNotNull(awsRegion)) {
			awsKMSClientBuilder.withRegion(awsRegion);
		}

		return awsKMSClientBuilder.build();
	}

	private String _createKey(
			AWSKMSCryptoProviderContext awsKMSCryptoProviderContext,
			long companyId, String keyIdentifier, KeySpec keySpec,
			KeyUsageType keyUsageType)
		throws CryptoException {

		String aliasName = _getAliasName(
			_getKeyId(awsKMSCryptoProviderContext, companyId, keyIdentifier));
		AWSClientManager<AWSKMS> awsClientManager =
			awsKMSCryptoProviderContext.getAWSClientManager();

		try {
			return awsClientManager.execute(
				awsKMS -> {
					CreateKeyResult createKeyResult = awsKMS.createKey(
						new CreateKeyRequest(
						).withKeySpec(
							keySpec
						).withKeyUsage(
							keyUsageType
						));

					KeyMetadata keyMetadata = createKeyResult.getKeyMetadata();

					if (aliasName != null) {
						awsKMS.createAlias(
							new CreateAliasRequest(
							).withAliasName(
								aliasName
							).withTargetKeyId(
								keyMetadata.getKeyId()
							));

						return keyIdentifier;
					}

					return keyMetadata.getArn();
				});
		}
		catch (Exception exception) {
			throw new CryptoException(
				"Unable to create AWS KMS key", exception);
		}
	}

	private String _getAliasName(String keyARNOrAlias) {
		if (keyARNOrAlias == null) {
			return null;
		}

		if (keyARNOrAlias.startsWith("alias/")) {
			return keyARNOrAlias;
		}

		int index = keyARNOrAlias.indexOf(":alias/");

		if (index < 0) {
			return null;
		}

		return keyARNOrAlias.substring(index + 1);
	}

	private AWSKMSCryptoProviderContext _getAWSKMSCryptoProviderContext(
			long companyId)
		throws CryptoException {

		AWSKMSCryptoProviderContext awsKMSCryptoProviderContext =
			_awsKMSCryptoProviderContext;

		if ((awsKMSCryptoProviderContext == null) ||
			!awsKMSCryptoProviderContext.isEnabled()) {

			throw new CryptoException(
				"Provider " + getProviderId() + " is not enabled");
		}

		if (!isAllowedCompany(companyId)) {
			throw new CryptoException(
				StringBundler.concat(
					"Provider ", getProviderId(),
					" does not handle company ID ", companyId));
		}

		return awsKMSCryptoProviderContext;
	}

	private String _getKeyId(
			AWSKMSCryptoProviderContext awsKMSCryptoProviderContext,
			long companyId, String keyIdentifier)
		throws CryptoException {

		try {
			return AWSARNUtil.resolve(
				awsKMSCryptoProviderContext.getKeyARNTemplate(),
				awsKMSCryptoProviderContext.getAWSAccountId(),
				awsKMSCryptoProviderContext.getAWSRegion(), companyId,
				keyIdentifier);
		}
		catch (IllegalArgumentException illegalArgumentException) {
			throw new CryptoException(
				illegalArgumentException.getMessage(),
				illegalArgumentException);
		}
	}

	private KeyMetadata _getKeyMetadata(
			AWSKMSCryptoProviderContext awsKMSCryptoProviderContext,
			String keyId)
		throws Exception {

		AWSClientManager<AWSKMS> awsClientManager =
			awsKMSCryptoProviderContext.getAWSClientManager();

		DescribeKeyResult describeKeyResult = awsClientManager.execute(
			awsKMS -> awsKMS.describeKey(
				new DescribeKeyRequest(
				).withKeyId(
					keyId
				)));

		return describeKeyResult.getKeyMetadata();
	}

	private String _getKeyOrigin(
			AWSKMSCryptoProviderContext awsKMSCryptoProviderContext,
			String keyId)
		throws CryptoException {

		String keyOrigin = _keyOrigins.get(keyId);

		if (keyOrigin != null) {
			return keyOrigin;
		}

		try {
			KeyMetadata keyMetadata = _getKeyMetadata(
				awsKMSCryptoProviderContext, keyId);

			keyOrigin = keyMetadata.getOrigin();
		}
		catch (Exception exception) {
			throw new CryptoException(
				"Unable to describe AWS KMS key " + keyId +
					" for FIPS validation",
				exception);
		}

		if (keyId.contains(":key/")) {
			_keyOrigins.put(keyId, keyOrigin);
		}

		return keyOrigin;
	}

	private KeySpec _getKeySpec(String algorithm) throws CryptoException {
		if (Validator.isNull(algorithm)) {
			throw new CryptoException("AWS KMS key spec is null");
		}

		KeySpec keySpec = null;

		try {
			keySpec = KeySpec.fromValue(algorithm);
		}
		catch (IllegalArgumentException illegalArgumentException) {
			throw new CryptoException(
				"Unknown AWS KMS key spec \"" + algorithm + "\"",
				illegalArgumentException);
		}

		String keySpecString = keySpec.toString();

		if (!keySpecString.startsWith("RSA")) {
			throw new CryptoException(
				StringBundler.concat(
					"AWS KMS asymmetric encryption supports only RSA key ",
					"specs, not \"", algorithm, "\""));
		}

		return keySpec;
	}

	private ServiceIndicator _getServiceIndicator(
			AWSKMSCryptoProviderContext awsKMSCryptoProviderContext,
			String keyId, String securityFunctionName)
		throws CryptoException {

		AWSKMSFIPSValidator awsKMSFIPSValidator =
			awsKMSCryptoProviderContext.getAWSKMSFIPSValidator();

		if (!awsKMSFIPSValidator.isFIPSEnforced()) {
			return awsKMSFIPSValidator.toServiceIndicator(
				false, securityFunctionName);
		}

		String keyOrigin = _getKeyOrigin(awsKMSCryptoProviderContext, keyId);

		awsKMSFIPSValidator.validateKeyOrigin(keyOrigin);

		return awsKMSFIPSValidator.toServiceIndicator(
			keyOrigin, securityFunctionName);
	}

	private String _resolveAliasPrefix(
		AWSKMSCryptoProviderContext awsKMSCryptoProviderContext,
		long companyId) {

		if (Validator.isNull(awsKMSCryptoProviderContext.getKeyARNTemplate())) {
			return null;
		}

		return _getAliasName(
			AWSARNUtil.resolve(
				awsKMSCryptoProviderContext.getKeyARNTemplate(),
				awsKMSCryptoProviderContext.getAWSAccountId(),
				awsKMSCryptoProviderContext.getAWSRegion(), companyId,
				StringPool.BLANK));
	}

	private static final Log _log = LogFactoryUtil.getLog(
		BaseAWSKMSCryptoProvider.class);

	private volatile AWSKMSCryptoProviderContext _awsKMSCryptoProviderContext;
	private final Map<String, String> _keyOrigins = new ConcurrentHashMap<>();

}