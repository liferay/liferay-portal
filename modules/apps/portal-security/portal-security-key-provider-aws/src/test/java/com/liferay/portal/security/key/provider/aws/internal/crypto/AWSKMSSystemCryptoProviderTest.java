/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.key.provider.aws.internal.crypto;

import com.amazonaws.services.kms.AWSKMS;
import com.amazonaws.services.kms.model.AliasListEntry;
import com.amazonaws.services.kms.model.CreateKeyRequest;
import com.amazonaws.services.kms.model.CreateKeyResult;
import com.amazonaws.services.kms.model.DecryptRequest;
import com.amazonaws.services.kms.model.DecryptResult;
import com.amazonaws.services.kms.model.DescribeKeyRequest;
import com.amazonaws.services.kms.model.DescribeKeyResult;
import com.amazonaws.services.kms.model.EncryptRequest;
import com.amazonaws.services.kms.model.EncryptResult;
import com.amazonaws.services.kms.model.KeyMetadata;
import com.amazonaws.services.kms.model.ListAliasesRequest;
import com.amazonaws.services.kms.model.ListAliasesResult;
import com.amazonaws.services.kms.model.ScheduleKeyDeletionRequest;

import com.liferay.portal.kernel.model.CompanyConstants;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.security.key.KeyReference;
import com.liferay.portal.security.key.ServiceIndicator;
import com.liferay.portal.security.key.crypto.CryptoKey;
import com.liferay.portal.security.key.crypto.CryptoServiceResult;
import com.liferay.portal.security.key.crypto.exception.CryptoException;
import com.liferay.portal.security.key.provider.aws.internal.fips.AWSKMSFIPSValidator;
import com.liferay.portal.security.key.provider.aws.internal.util.AWSClientManager;
import com.liferay.portal.security.key.spi.ProviderStatus;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.nio.ByteBuffer;

import java.util.Date;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.Mockito;

/**
 * @author Christopher Kian
 */
public class AWSKMSSystemCryptoProviderTest {

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
				AWSClientManager.AWSOperation<AWSKMS, ?> awsOperation =
					invocation.getArgument(0);

				return awsOperation.apply(_awsKMS);
			}
		);

		_setAWSKMSCryptoProviderContext(true, false);
	}

	@Test
	public void testDecrypt() throws Exception {
		byte[] plaintext = RandomTestUtil.randomBytes();

		Mockito.when(
			_awsKMS.decrypt(Mockito.any(DecryptRequest.class))
		).thenReturn(
			new DecryptResult(
			).withPlaintext(
				ByteBuffer.wrap(plaintext.clone())
			)
		);

		CryptoServiceResult<byte[]> cryptoServiceResult =
			_awsKMSSystemCryptoProvider.decrypt(
				RandomTestUtil.randomBytes(), CompanyConstants.SYSTEM,
				RandomTestUtil.randomString());

		Assert.assertArrayEquals(plaintext, cryptoServiceResult.getValue());
		Assert.assertNotNull(cryptoServiceResult.getServiceIndicator());
	}

	@Test(expected = CryptoException.class)
	public void testDecryptRejectsUnapprovedOriginUnderEnforcement()
		throws Exception {

		_setAWSKMSCryptoProviderContext(true, true);

		Mockito.when(
			_awsKMS.describeKey(Mockito.any(DescribeKeyRequest.class))
		).thenReturn(
			new DescribeKeyResult(
			).withKeyMetadata(
				new KeyMetadata(
				).withOrigin(
					"EXTERNAL"
				)
			)
		);

		_awsKMSSystemCryptoProvider.decrypt(
			RandomTestUtil.randomBytes(), CompanyConstants.SYSTEM,
			RandomTestUtil.randomString());
	}

	@Test
	public void testDecryptUnderEnforcementWithApprovedOrigin()
		throws Exception {

		_setAWSKMSCryptoProviderContext(true, true);

		byte[] plaintext = RandomTestUtil.randomBytes();

		Mockito.when(
			_awsKMS.decrypt(Mockito.any(DecryptRequest.class))
		).thenReturn(
			new DecryptResult(
			).withPlaintext(
				ByteBuffer.wrap(plaintext.clone())
			)
		);

		Mockito.when(
			_awsKMS.describeKey(Mockito.any(DescribeKeyRequest.class))
		).thenReturn(
			new DescribeKeyResult(
			).withKeyMetadata(
				new KeyMetadata(
				).withOrigin(
					"AWS_KMS"
				)
			)
		);

		CryptoServiceResult<byte[]> cryptoServiceResult =
			_awsKMSSystemCryptoProvider.decrypt(
				RandomTestUtil.randomBytes(), CompanyConstants.SYSTEM,
				RandomTestUtil.randomString());

		Assert.assertArrayEquals(plaintext, cryptoServiceResult.getValue());

		ServiceIndicator serviceIndicator =
			cryptoServiceResult.getServiceIndicator();

		Assert.assertTrue(serviceIndicator.isApproved());
	}

	@Test(expected = CryptoException.class)
	public void testDecryptWhenDisabled() throws Exception {
		_setAWSKMSCryptoProviderContext(false, false);

		_awsKMSSystemCryptoProvider.decrypt(
			RandomTestUtil.randomBytes(), CompanyConstants.SYSTEM,
			RandomTestUtil.randomString());
	}

	@Test
	public void testDeleteKey() throws Exception {
		_awsKMSSystemCryptoProvider.deleteKey(
			CompanyConstants.SYSTEM, RandomTestUtil.randomString());

		Mockito.verify(
			_awsKMS
		).scheduleKeyDeletion(
			Mockito.any(ScheduleKeyDeletionRequest.class)
		);
	}

	@Test
	public void testEncrypt() throws Exception {
		byte[] ciphertext = RandomTestUtil.randomBytes();

		Mockito.when(
			_awsKMS.encrypt(Mockito.any(EncryptRequest.class))
		).thenReturn(
			new EncryptResult(
			).withCiphertextBlob(
				ByteBuffer.wrap(ciphertext.clone())
			)
		);

		CryptoServiceResult<byte[]> cryptoServiceResult =
			_awsKMSSystemCryptoProvider.encrypt(
				CompanyConstants.SYSTEM, RandomTestUtil.randomString(),
				RandomTestUtil.randomBytes());

		Assert.assertArrayEquals(ciphertext, cryptoServiceResult.getValue());
		Assert.assertNotNull(cryptoServiceResult.getServiceIndicator());
	}

	@Test(expected = CryptoException.class)
	public void testExportKey() throws Exception {
		_awsKMSSystemCryptoProvider.exportKey(
			CompanyConstants.SYSTEM, RandomTestUtil.randomString());
	}

	@Test
	public void testGenerateAsymmetricKeyIdentifier() throws Exception {
		String keyARN = RandomTestUtil.randomString();

		Mockito.when(
			_awsKMS.createKey(Mockito.any(CreateKeyRequest.class))
		).thenReturn(
			new CreateKeyResult(
			).withKeyMetadata(
				new KeyMetadata(
				).withArn(
					keyARN
				)
			)
		);

		CryptoServiceResult<String> cryptoServiceResult =
			_awsKMSSystemCryptoProvider.generateAsymmetricKeyIdentifier(
				"RSA_2048", CompanyConstants.SYSTEM,
				RandomTestUtil.randomString());

		Assert.assertEquals(keyARN, cryptoServiceResult.getValue());

		Assert.assertThrows(
			CryptoException.class,
			() -> _awsKMSSystemCryptoProvider.generateAsymmetricKeyIdentifier(
				"ECC_NIST_P256", CompanyConstants.SYSTEM,
				RandomTestUtil.randomString()));
	}

	@Test
	public void testGenerateSecretKeyIdentifier() throws Exception {
		String keyARN = RandomTestUtil.randomString();

		Mockito.when(
			_awsKMS.createKey(Mockito.any(CreateKeyRequest.class))
		).thenReturn(
			new CreateKeyResult(
			).withKeyMetadata(
				new KeyMetadata(
				).withArn(
					keyARN
				)
			)
		);

		CryptoServiceResult<String> cryptoServiceResult =
			_awsKMSSystemCryptoProvider.generateSecretKeyIdentifier(
				"AES_256_GCM", CompanyConstants.SYSTEM,
				RandomTestUtil.randomString());

		Assert.assertEquals(keyARN, cryptoServiceResult.getValue());

		Assert.assertThrows(
			CryptoException.class,
			() -> _awsKMSSystemCryptoProvider.generateSecretKeyIdentifier(
				RandomTestUtil.randomString(), CompanyConstants.SYSTEM,
				RandomTestUtil.randomString()));
	}

	@Test
	public void testGetCryptoKey() throws Exception {
		String algorithm = RandomTestUtil.randomString();
		String cipherSpec = RandomTestUtil.randomString();

		Mockito.when(
			_awsKMS.describeKey(Mockito.any(DescribeKeyRequest.class))
		).thenReturn(
			new DescribeKeyResult(
			).withKeyMetadata(
				new KeyMetadata(
				).withCreationDate(
					new Date(RandomTestUtil.randomLong())
				).withKeySpec(
					algorithm
				).withKeyUsage(
					cipherSpec
				)
			)
		);

		CryptoKey cryptoKey = _awsKMSSystemCryptoProvider.getCryptoKey(
			CompanyConstants.SYSTEM, RandomTestUtil.randomString());

		Assert.assertEquals(algorithm, cryptoKey.getAlgorithm());
		Assert.assertEquals(cipherSpec, cryptoKey.getCipherSpec());

		KeyReference keyReference = cryptoKey.getKeyReference();

		Assert.assertEquals("aws-system-crypto", keyReference.getProviderId());
		Assert.assertEquals(KeyReference.Type.CRYPTO, keyReference.getType());
	}

	@Test
	public void testGetKeyIdentifiers() throws Exception {
		String aliasPrefix = "alias/liferay/";

		_setAWSKMSCryptoProviderContext(
			true, false, aliasPrefix + "{companyId}/");

		String keyIdentifier1 = RandomTestUtil.randomString();
		String keyIdentifier2 = RandomTestUtil.randomString();
		String resolvedAliasPrefix =
			aliasPrefix + CompanyConstants.SYSTEM + "/";

		Mockito.when(
			_awsKMS.listAliases(Mockito.any(ListAliasesRequest.class))
		).thenReturn(
			new ListAliasesResult(
			).withAliases(
				new AliasListEntry(
				).withAliasName(
					resolvedAliasPrefix + keyIdentifier1
				),
				new AliasListEntry(
				).withAliasName(
					resolvedAliasPrefix + keyIdentifier2
				),
				new AliasListEntry(
				).withAliasName(
					"alias/other/" + RandomTestUtil.randomString()
				)
			)
		);

		List<String> keyIdentifiers =
			_awsKMSSystemCryptoProvider.getKeyIdentifiers(
				CompanyConstants.SYSTEM);

		Assert.assertEquals(
			keyIdentifiers.toString(), 2, keyIdentifiers.size());
		Assert.assertTrue(keyIdentifiers.contains(keyIdentifier1));
		Assert.assertTrue(keyIdentifiers.contains(keyIdentifier2));
	}

	@Test
	public void testGetProviderStatus() {
		Assert.assertEquals(
			ProviderStatus.OPERATIONAL,
			_awsKMSSystemCryptoProvider.getProviderStatus());

		_setAWSKMSCryptoProviderContext(false, false);

		Assert.assertEquals(
			ProviderStatus.DEGRADED,
			_awsKMSSystemCryptoProvider.getProviderStatus());
	}

	@Test(expected = CryptoException.class)
	public void testImportSecretKey() throws Exception {
		_awsKMSSystemCryptoProvider.importSecretKey(
			RandomTestUtil.randomString(), CompanyConstants.SYSTEM,
			RandomTestUtil.randomBytes(), RandomTestUtil.randomString());
	}

	@Test
	public void testIsAllowedCompany() {
		Assert.assertFalse(
			_awsKMSSystemCryptoProvider.isAllowedCompany(
				RandomTestUtil.randomLong()));
		Assert.assertTrue(
			_awsKMSSystemCryptoProvider.isAllowedCompany(
				CompanyConstants.SYSTEM));
	}

	@Test(expected = CryptoException.class)
	public void testUnwrap() throws Exception {
		_awsKMSSystemCryptoProvider.unwrap(
			CompanyConstants.SYSTEM, RandomTestUtil.randomString(),
			RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			RandomTestUtil.randomBytes(), RandomTestUtil.randomInt());
	}

	@Test(expected = CryptoException.class)
	public void testWrap() throws Exception {
		_awsKMSSystemCryptoProvider.wrap(
			CompanyConstants.SYSTEM, RandomTestUtil.randomString(),
			RandomTestUtil.randomString());
	}

	private void _setAWSKMSCryptoProviderContext(
		boolean enabled, boolean fipsEnforced) {

		_setAWSKMSCryptoProviderContext(enabled, fipsEnforced, null);
	}

	private void _setAWSKMSCryptoProviderContext(
		boolean enabled, boolean fipsEnforced, String keyARNTemplate) {

		ReflectionTestUtil.setFieldValue(
			_awsKMSSystemCryptoProvider, "_awsKMSCryptoProviderContext",
			new AWSKMSCryptoProviderContext(
				null, _awsClientManager,
				new AWSKMSFIPSValidator("AES_256_GCM", fipsEnforced),
				RandomTestUtil.randomString(), enabled, keyARNTemplate,
				RandomTestUtil.randomInt(), false));
	}

	private final AWSClientManager<AWSKMS> _awsClientManager = Mockito.mock(
		AWSClientManager.class);
	private final AWSKMS _awsKMS = Mockito.mock(AWSKMS.class);
	private final AWSKMSSystemCryptoProvider _awsKMSSystemCryptoProvider =
		new AWSKMSSystemCryptoProvider();

}