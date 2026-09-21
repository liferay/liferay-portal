/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.key.provider.aws.internal.secret;

import com.amazonaws.services.secretsmanager.AWSSecretsManager;
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
import com.liferay.portal.kernel.model.CompanyConstants;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.security.key.KeyReference;
import com.liferay.portal.security.key.provider.aws.internal.fips.AWSSecretsManagerFIPSValidator;
import com.liferay.portal.security.key.provider.aws.internal.util.AWSClientManager;
import com.liferay.portal.security.key.secret.Secret;
import com.liferay.portal.security.key.secret.exception.SecretException;
import com.liferay.portal.security.key.spi.ProviderStatus;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.nio.ByteBuffer;

import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

/**
 * @author Christopher Kian
 */
public class AWSSecretsManagerSystemSecretProviderTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() throws Exception {
		Mockito.when(
			_awsClientManager.execute(Mockito.any())
		).thenAnswer(
			invocation -> {
				AWSClientManager.AWSOperation<AWSSecretsManager, ?>
					awsOperation = invocation.getArgument(0);

				return awsOperation.apply(_awsSecretsManager);
			}
		);
	}

	@Test
	public void testDeleteSecret() throws Exception {
		_setAWSSecretsManagerSecretProviderContext(true, false, false);

		_awsSecretsManagerSystemSecretProvider.deleteSecret(
			CompanyConstants.SYSTEM, RandomTestUtil.randomString());

		ArgumentCaptor<DeleteSecretRequest> argumentCaptor =
			ArgumentCaptor.forClass(DeleteSecretRequest.class);

		Mockito.verify(
			_awsSecretsManager
		).deleteSecret(
			argumentCaptor.capture()
		);

		DeleteSecretRequest deleteSecretRequest = argumentCaptor.getValue();

		Assert.assertEquals(
			Long.valueOf(_recoveryWindowInDays),
			deleteSecretRequest.getRecoveryWindowInDays());
	}

	@Test
	public void testGetProviderStatus() {
		_setAWSSecretsManagerSecretProviderContext(true, false, false);

		Assert.assertEquals(
			ProviderStatus.OPERATIONAL,
			_awsSecretsManagerSystemSecretProvider.getProviderStatus());
	}

	@Test
	public void testGetSecret() throws Exception {
		_setAWSSecretsManagerSecretProviderContext(true, false, false);

		byte[] expectedBytes = RandomTestUtil.randomBytes();

		Mockito.when(
			_awsSecretsManager.getSecretValue(
				Mockito.any(GetSecretValueRequest.class))
		).thenReturn(
			new GetSecretValueResult(
			).withSecretBinary(
				ByteBuffer.wrap(expectedBytes.clone())
			)
		);

		Secret secret = _awsSecretsManagerSystemSecretProvider.getSecret(
			CompanyConstants.SYSTEM, RandomTestUtil.randomString());

		Assert.assertArrayEquals(expectedBytes, secret.getBytes());
	}

	@Test
	public void testGetSecretIdentifiers() throws Exception {
		_setAWSSecretsManagerSecretProviderContext(true, false, false);

		String secretIdentifier1 = RandomTestUtil.randomString();
		String secretIdentifier2 = RandomTestUtil.randomString();
		String secretNamePrefix = "liferay/" + CompanyConstants.SYSTEM + "/";

		Mockito.when(
			_awsSecretsManager.listSecrets(
				Mockito.any(ListSecretsRequest.class))
		).thenReturn(
			new ListSecretsResult(
			).withSecretList(
				new SecretListEntry(
				).withName(
					secretNamePrefix + secretIdentifier1
				),
				new SecretListEntry(
				).withName(
					secretNamePrefix + secretIdentifier2
				)
			)
		);

		List<String> secretIdentifiers =
			_awsSecretsManagerSystemSecretProvider.getSecretIdentifiers(
				CompanyConstants.SYSTEM);

		Assert.assertTrue(secretIdentifiers.contains(secretIdentifier1));
		Assert.assertTrue(secretIdentifiers.contains(secretIdentifier2));
		Assert.assertEquals(
			secretIdentifiers.toString(), 2, secretIdentifiers.size());
	}

	@Test(expected = SecretException.class)
	public void testGetSecretRejectsMissingValue() throws Exception {
		_setAWSSecretsManagerSecretProviderContext(true, false, false);

		Mockito.when(
			_awsSecretsManager.getSecretValue(
				Mockito.any(GetSecretValueRequest.class))
		).thenReturn(
			new GetSecretValueResult()
		);

		_awsSecretsManagerSystemSecretProvider.getSecret(
			CompanyConstants.SYSTEM, RandomTestUtil.randomString());
	}

	@Test(expected = SecretException.class)
	public void testGetSecretRejectsUnderFIPSEnforcementWithoutFIPSEndpoint()
		throws Exception {

		_setAWSSecretsManagerSecretProviderContext(true, true, false);

		_awsSecretsManagerSystemSecretProvider.getSecret(
			CompanyConstants.SYSTEM, RandomTestUtil.randomString());
	}

	@Test(expected = SecretException.class)
	public void testGetSecretWhenDisabled() throws Exception {
		_setAWSSecretsManagerSecretProviderContext(false, false, false);

		_awsSecretsManagerSystemSecretProvider.getSecret(
			CompanyConstants.SYSTEM, RandomTestUtil.randomString());
	}

	@Test
	public void testIsAllowedCompany() {
		Assert.assertFalse(
			_awsSecretsManagerSystemSecretProvider.isAllowedCompany(
				RandomTestUtil.randomLong()));
		Assert.assertTrue(
			_awsSecretsManagerSystemSecretProvider.isAllowedCompany(
				CompanyConstants.SYSTEM));
	}

	@Test
	public void testPutSecret() throws Exception {
		_setAWSSecretsManagerSecretProviderContext(true, false, false);

		byte[] bytes = RandomTestUtil.randomBytes();

		_awsSecretsManagerSystemSecretProvider.putSecret(
			CompanyConstants.SYSTEM,
			new Secret(
				bytes,
				new KeyReference(
					RandomTestUtil.randomString(),
					RandomTestUtil.randomString(), KeyReference.Type.SECRET)));

		ArgumentCaptor<PutSecretValueRequest> argumentCaptor =
			ArgumentCaptor.forClass(PutSecretValueRequest.class);

		Mockito.verify(
			_awsSecretsManager
		).putSecretValue(
			argumentCaptor.capture()
		);

		Mockito.verify(
			_awsSecretsManager, Mockito.never()
		).createSecret(
			Mockito.any(CreateSecretRequest.class)
		);

		PutSecretValueRequest putSecretValueRequest = argumentCaptor.getValue();

		Assert.assertEquals(
			ByteBuffer.wrap(bytes), putSecretValueRequest.getSecretBinary());
	}

	@Test
	public void testPutSecretCreatesSecretWithName() throws Exception {
		_setAWSSecretsManagerSecretProviderContext(true, false, false);

		Mockito.when(
			_awsSecretsManager.putSecretValue(
				Mockito.any(PutSecretValueRequest.class))
		).thenThrow(
			new ResourceNotFoundException(RandomTestUtil.randomString())
		);

		String secretIdentifier = RandomTestUtil.randomString();

		_awsSecretsManagerSystemSecretProvider.putSecret(
			CompanyConstants.SYSTEM,
			new Secret(
				RandomTestUtil.randomBytes(),
				new KeyReference(
					secretIdentifier, RandomTestUtil.randomString(),
					KeyReference.Type.SECRET)));

		ArgumentCaptor<CreateSecretRequest> argumentCaptor =
			ArgumentCaptor.forClass(CreateSecretRequest.class);

		Mockito.verify(
			_awsSecretsManager
		).createSecret(
			argumentCaptor.capture()
		);

		CreateSecretRequest createSecretRequest = argumentCaptor.getValue();

		Assert.assertEquals(
			StringBundler.concat(
				"liferay/", CompanyConstants.SYSTEM, "/", secretIdentifier),
			createSecretRequest.getName());
	}

	private void _setAWSSecretsManagerSecretProviderContext(
		boolean enabled, boolean fipsEnforced, boolean useFIPSEndpoint) {

		_recoveryWindowInDays = RandomTestUtil.randomLong();

		ReflectionTestUtil.setFieldValue(
			_awsSecretsManagerSystemSecretProvider,
			"_awsSecretsManagerSecretProviderContext",
			new AWSSecretsManagerSecretProviderContext(
				null, _awsClientManager, RandomTestUtil.randomString(),
				new AWSSecretsManagerFIPSValidator(
					fipsEnforced, useFIPSEndpoint),
				enabled, _recoveryWindowInDays, _SECRET_ARN_TEMPLATE));
	}

	private static final String _SECRET_ARN_TEMPLATE =
		"arn:aws:secretsmanager:us-east-1:123456789012:secret:liferay" +
			"/{companyId}/{identifier}";

	private final AWSClientManager<AWSSecretsManager> _awsClientManager =
		Mockito.mock(AWSClientManager.class);
	private final AWSSecretsManager _awsSecretsManager = Mockito.mock(
		AWSSecretsManager.class);
	private final AWSSecretsManagerSystemSecretProvider
		_awsSecretsManagerSystemSecretProvider =
			new AWSSecretsManagerSystemSecretProvider();
	private long _recoveryWindowInDays;

}