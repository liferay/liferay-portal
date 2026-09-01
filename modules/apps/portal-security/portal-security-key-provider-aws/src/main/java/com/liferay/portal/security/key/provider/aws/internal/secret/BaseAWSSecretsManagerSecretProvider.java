/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.key.provider.aws.internal.secret;

import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.secretsmanager.AWSSecretsManager;
import com.amazonaws.services.secretsmanager.AWSSecretsManagerClientBuilder;
import com.amazonaws.services.secretsmanager.model.CreateSecretRequest;
import com.amazonaws.services.secretsmanager.model.DeleteSecretRequest;
import com.amazonaws.services.secretsmanager.model.GetSecretValueRequest;
import com.amazonaws.services.secretsmanager.model.GetSecretValueResult;
import com.amazonaws.services.secretsmanager.model.ListSecretsRequest;
import com.amazonaws.services.secretsmanager.model.ListSecretsResult;
import com.amazonaws.services.secretsmanager.model.PutSecretValueRequest;
import com.amazonaws.services.secretsmanager.model.ResourceNotFoundException;
import com.amazonaws.services.secretsmanager.model.SecretListEntry;

import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.security.key.KeyReference;
import com.liferay.portal.security.key.provider.aws.internal.fips.AWSSecretsManagerFIPSValidator;
import com.liferay.portal.security.key.provider.aws.internal.util.AWSARNUtil;
import com.liferay.portal.security.key.provider.aws.internal.util.AWSByteBufferUtil;
import com.liferay.portal.security.key.provider.aws.internal.util.AWSClientManager;
import com.liferay.portal.security.key.secret.Secret;
import com.liferay.portal.security.key.secret.exception.SecretException;
import com.liferay.portal.security.key.spi.ProviderStatus;
import com.liferay.portal.security.key.spi.secret.SecretProvider;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Modified;

/**
 * @author Christopher Kian
 */
public abstract class BaseAWSSecretsManagerSecretProvider
	implements SecretProvider {

	@Override
	public void deleteSecret(long companyId, String secretIdentifier)
		throws SecretException {

		AWSSecretsManagerSecretProviderContext
			awsSecretsManagerSecretProviderContext =
				_getAWSSecretsManagerSecretProviderContext(companyId);

		AWSClientManager<AWSSecretsManager> awsClientManager =
			awsSecretsManagerSecretProviderContext.getAWSClientManager();
		long recoveryWindowInDays =
			awsSecretsManagerSecretProviderContext.getRecoveryWindowInDays();
		String secretARN = _resolveSecretARN(
			awsSecretsManagerSecretProviderContext, companyId,
			secretIdentifier);

		try {
			awsClientManager.execute(
				awsSecretsManager -> awsSecretsManager.deleteSecret(
					new DeleteSecretRequest(
					).withRecoveryWindowInDays(
						recoveryWindowInDays
					).withSecretId(
						secretARN
					)));
		}
		catch (Exception exception) {
			throw new SecretException(
				"Unable to delete AWS secret " + secretARN, exception);
		}
	}

	@Override
	public ProviderStatus getProviderStatus() {
		AWSSecretsManagerSecretProviderContext
			awsSecretsManagerSecretProviderContext =
				_awsSecretsManagerSecretProviderContext;

		if ((awsSecretsManagerSecretProviderContext == null) ||
			!awsSecretsManagerSecretProviderContext.isEnabled() ||
			Validator.isNull(
				awsSecretsManagerSecretProviderContext.getAWSRegion())) {

			return ProviderStatus.DEGRADED;
		}

		return ProviderStatus.OPERATIONAL;
	}

	@Override
	public Secret getSecret(long companyId, String secretIdentifier)
		throws SecretException {

		AWSSecretsManagerSecretProviderContext
			awsSecretsManagerSecretProviderContext =
				_getAWSSecretsManagerSecretProviderContext(companyId);

		AWSClientManager<AWSSecretsManager> awsClientManager =
			awsSecretsManagerSecretProviderContext.getAWSClientManager();

		byte[] bytes = null;

		String secretARN = _resolveSecretARN(
			awsSecretsManagerSecretProviderContext, companyId,
			secretIdentifier);

		try {
			GetSecretValueResult getSecretValueResult =
				awsClientManager.execute(
					awsSecretsManager -> awsSecretsManager.getSecretValue(
						new GetSecretValueRequest(
						).withSecretId(
							secretARN
						)));

			bytes = _getBytes(getSecretValueResult, secretARN);

			return new Secret(
				bytes,
				new KeyReference(
					secretARN, getProviderId(), KeyReference.Type.SECRET));
		}
		catch (SecretException secretException) {
			throw secretException;
		}
		catch (Exception exception) {
			throw new SecretException(
				"Unable to read AWS secret " + secretARN, exception);
		}
		finally {
			if (bytes != null) {
				Arrays.fill(bytes, (byte)0);
			}
		}
	}

	@Override
	public List<String> getSecretIdentifiers(long companyId)
		throws SecretException {

		AWSSecretsManagerSecretProviderContext
			awsSecretsManagerSecretProviderContext =
				_getAWSSecretsManagerSecretProviderContext(companyId);

		String secretNamePrefix = _resolveSecretNamePrefix(
			awsSecretsManagerSecretProviderContext, companyId);

		if (Validator.isNull(secretNamePrefix)) {
			return new ArrayList<>();
		}

		AWSClientManager<AWSSecretsManager> awsClientManager =
			awsSecretsManagerSecretProviderContext.getAWSClientManager();

		try {
			return awsClientManager.execute(
				awsSecretsManager -> {
					List<String> secretIdentifiers = new ArrayList<>();

					ListSecretsRequest listSecretsRequest =
						new ListSecretsRequest(
						).withMaxResults(
							100
						);

					while (true) {
						ListSecretsResult listSecretsResult =
							awsSecretsManager.listSecrets(listSecretsRequest);

						for (SecretListEntry secretListEntry :
								listSecretsResult.getSecretList()) {

							String name = secretListEntry.getName();

							if ((name != null) &&
								name.startsWith(secretNamePrefix)) {

								secretIdentifiers.add(
									name.substring(secretNamePrefix.length()));
							}
						}

						String nextToken = listSecretsResult.getNextToken();

						if (nextToken == null) {
							break;
						}

						listSecretsRequest.setNextToken(nextToken);
					}

					return secretIdentifiers;
				});
		}
		catch (Exception exception) {
			throw new SecretException(
				"Unable to list AWS secrets under " + secretNamePrefix,
				exception);
		}
	}

	@Override
	public void putSecret(long companyId, Secret secret)
		throws SecretException {

		AWSSecretsManagerSecretProviderContext
			awsSecretsManagerSecretProviderContext =
				_getAWSSecretsManagerSecretProviderContext(companyId);

		KeyReference keyReference = secret.getKeyReference();

		String secretARN = _resolveSecretARN(
			awsSecretsManagerSecretProviderContext, companyId,
			keyReference.getIdentifier());

		String secretName = _getSecretName(secretARN);

		AWSClientManager<AWSSecretsManager> awsClientManager =
			awsSecretsManagerSecretProviderContext.getAWSClientManager();

		try {
			awsClientManager.execute(
				awsSecretsManager -> {
					try {
						awsSecretsManager.putSecretValue(
							new PutSecretValueRequest(
							).withSecretBinary(
								ByteBuffer.wrap(secret.getBytes())
							).withSecretId(
								secretARN
							));
					}
					catch (ResourceNotFoundException
								resourceNotFoundException) {

						if (_log.isDebugEnabled()) {
							_log.debug(
								"Creating AWS secret " + secretName,
								resourceNotFoundException);
						}

						awsSecretsManager.createSecret(
							new CreateSecretRequest(
							).withName(
								secretName
							).withSecretBinary(
								ByteBuffer.wrap(secret.getBytes())
							));
					}

					return null;
				});
		}
		catch (Exception exception) {
			throw new SecretException(
				"Unable to write AWS secret " + secretARN, exception);
		}
	}

	@Activate
	@Modified
	protected void activate(Map<String, Object> properties) {
		String awsAccountId = GetterUtil.getString(
			properties.get("awsAccountId"));
		String awsRegion = GetterUtil.getString(properties.get("awsRegion"));
		boolean enabled = GetterUtil.getBoolean(properties.get("enabled"));
		boolean fipsEnforced = GetterUtil.getBoolean(
			properties.get("fipsEnforced"));
		long recoveryWindowInDays = GetterUtil.getLong(
			properties.get("recoveryWindowInDays"), 30);
		String secretARNTemplate = GetterUtil.getString(
			properties.get("arnTemplate"));
		boolean useFIPSEndpoint = GetterUtil.getBoolean(
			properties.get("useFIPSEndpoint"));

		AWSClientManager<AWSSecretsManager> awsClientManager = null;

		AWSSecretsManagerSecretProviderContext
			awsSecretsManagerSecretProviderContext =
				_awsSecretsManagerSecretProviderContext;

		if (awsSecretsManagerSecretProviderContext != null) {
			awsClientManager =
				awsSecretsManagerSecretProviderContext.getAWSClientManager();
		}

		if (awsClientManager == null) {
			awsClientManager = new AWSClientManager<>(
				awsRegion,
				BaseAWSSecretsManagerSecretProvider::_buildAWSSecretsManager,
				"secretsmanager-fips.{region}.amazonaws.com", useFIPSEndpoint);
		}
		else {
			awsClientManager.updateConfiguration(awsRegion, useFIPSEndpoint);
		}

		awsRegion = awsClientManager.getAWSRegion();

		_awsSecretsManagerSecretProviderContext =
			new AWSSecretsManagerSecretProviderContext(
				awsAccountId, awsClientManager, awsRegion,
				new AWSSecretsManagerFIPSValidator(
					fipsEnforced, useFIPSEndpoint),
				enabled, recoveryWindowInDays, secretARNTemplate);

		if (_log.isInfoEnabled()) {
			_log.info(
				StringBundler.concat(
					"Activated ", getProviderId(), " in AWS region ",
					awsRegion));
		}
	}

	@Deactivate
	protected void deactivate() {
		AWSSecretsManagerSecretProviderContext
			awsSecretsManagerSecretProviderContext =
				_awsSecretsManagerSecretProviderContext;

		_awsSecretsManagerSecretProviderContext = null;

		if (awsSecretsManagerSecretProviderContext != null) {
			AWSClientManager<AWSSecretsManager> awsClientManager =
				awsSecretsManagerSecretProviderContext.getAWSClientManager();

			awsClientManager.close();
		}
	}

	protected abstract String getProviderId();

	private static AWSSecretsManager _buildAWSSecretsManager(
		AWSCredentialsProvider awsCredentialsProvider, String awsRegion,
		AwsClientBuilder.EndpointConfiguration endpointConfiguration) {

		AWSSecretsManagerClientBuilder awsSecretsManagerClientBuilder =
			AWSSecretsManagerClientBuilder.standard(
			).withCredentials(
				awsCredentialsProvider
			);

		if (endpointConfiguration != null) {
			awsSecretsManagerClientBuilder.withEndpointConfiguration(
				endpointConfiguration);
		}
		else if (Validator.isNotNull(awsRegion)) {
			awsSecretsManagerClientBuilder.withRegion(awsRegion);
		}

		return awsSecretsManagerClientBuilder.build();
	}

	private AWSSecretsManagerSecretProviderContext
			_getAWSSecretsManagerSecretProviderContext(long companyId)
		throws SecretException {

		AWSSecretsManagerSecretProviderContext
			awsSecretsManagerSecretProviderContext =
				_awsSecretsManagerSecretProviderContext;

		if ((awsSecretsManagerSecretProviderContext == null) ||
			!awsSecretsManagerSecretProviderContext.isEnabled()) {

			throw new SecretException(
				"Provider " + getProviderId() + " is not enabled");
		}

		if (!isAllowedCompany(companyId)) {
			throw new SecretException(
				StringBundler.concat(
					"Provider ", getProviderId(),
					" does not handle company ID ", companyId));
		}

		AWSSecretsManagerFIPSValidator awsSecretsManagerFIPSValidator =
			awsSecretsManagerSecretProviderContext.
				getAWSSecretsManagerFIPSValidator();

		awsSecretsManagerFIPSValidator.validateEndpoint();

		return awsSecretsManagerSecretProviderContext;
	}

	private byte[] _getBytes(
			GetSecretValueResult getSecretValueResult, String secretARN)
		throws SecretException {

		ByteBuffer secretBinary = getSecretValueResult.getSecretBinary();

		if (secretBinary != null) {
			return AWSByteBufferUtil.getBytes(secretBinary);
		}

		String secretString = getSecretValueResult.getSecretString();

		if (secretString != null) {
			return secretString.getBytes(StandardCharsets.UTF_8);
		}

		throw new SecretException(
			"AWS secret " + secretARN + " has no binary or string value");
	}

	private String _getSecretName(String secretARN) {
		int index = secretARN.indexOf(":secret:");

		if (index >= 0) {
			return secretARN.substring(index + ":secret:".length());
		}

		return secretARN;
	}

	private String _resolveSecretARN(
		AWSSecretsManagerSecretProviderContext
			awsSecretsManagerSecretProviderContext,
		long companyId, String identifier) {

		return AWSARNUtil.resolve(
			awsSecretsManagerSecretProviderContext.getSecretARNTemplate(),
			awsSecretsManagerSecretProviderContext.getAWSAccountId(),
			awsSecretsManagerSecretProviderContext.getAWSRegion(), companyId,
			identifier);
	}

	private String _resolveSecretNamePrefix(
		AWSSecretsManagerSecretProviderContext
			awsSecretsManagerSecretProviderContext,
		long companyId) {

		if (Validator.isNull(
				awsSecretsManagerSecretProviderContext.
					getSecretARNTemplate())) {

			return null;
		}

		String arn = AWSARNUtil.resolve(
			awsSecretsManagerSecretProviderContext.getSecretARNTemplate(),
			awsSecretsManagerSecretProviderContext.getAWSAccountId(),
			awsSecretsManagerSecretProviderContext.getAWSRegion(), companyId,
			StringPool.BLANK);

		return _getSecretName(arn);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		BaseAWSSecretsManagerSecretProvider.class);

	private volatile AWSSecretsManagerSecretProviderContext
		_awsSecretsManagerSecretProviderContext;

}