/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.fips.cbom.builder;

import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.security.NoSuchAlgorithmException;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author Manuele Castro
 */
public class CryptoProviderUtilTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Test
	public void testFetchProviderName() throws Exception {
		Assert.assertEquals(
			"SUN",
			CryptoProviderUtil.fetchProviderName("SHA-256", "MessageDigest"));
		Assert.assertNull(
			CryptoProviderUtil.fetchProviderName("AES", "SecretKeySpec"));
		Assert.assertNull(
			CryptoProviderUtil.fetchProviderName(
				RandomTestUtil.randomString(), "MessageDigest"));
	}

	@Test
	public void testGetAlgorithmName() throws Exception {
		Assert.assertEquals(
			"AES/GCM/NoPadding",
			CryptoProviderUtil.getAlgorithmName("aes/GCM/NoPadding", "Cipher"));
		Assert.assertEquals(
			"SHA-1",
			CryptoProviderUtil.getAlgorithmName("SHA", "MessageDigest"));
		Assert.assertEquals(
			"SHA1withDSA",
			CryptoProviderUtil.getAlgorithmName("DSA", "Signature"));
		Assert.assertEquals(
			"TLS", CryptoProviderUtil.getAlgorithmName("TLS", "SSLContext"));

		String algorithm = RandomTestUtil.randomString();

		Assert.assertEquals(
			algorithm,
			CryptoProviderUtil.getAlgorithmName(algorithm, "MessageDigest"));
		Assert.assertEquals(
			algorithm,
			CryptoProviderUtil.getAlgorithmName(algorithm, "SecretKeySpec"));
	}

	@Test
	public void testGetProviderName() throws Exception {
		Assert.assertEquals(
			"SunJCE",
			CryptoProviderUtil.getProviderName("AES/GCM/NoPadding", "Cipher"));
		Assert.assertEquals(
			"SunEC",
			CryptoProviderUtil.getProviderName("ECDH", "KeyAgreement"));
		Assert.assertEquals(
			"SunRsaSign",
			CryptoProviderUtil.getProviderName("RSA", "KeyFactory"));
		Assert.assertEquals(
			"SunJCE",
			CryptoProviderUtil.getProviderName("AES", "KeyGenerator"));
		Assert.assertEquals(
			"SunJSSE",
			CryptoProviderUtil.getProviderName("PKIX", "KeyManagerFactory"));
		Assert.assertEquals(
			"SunEC",
			CryptoProviderUtil.getProviderName("EC", "KeyPairGenerator"));
		Assert.assertEquals(
			"SUN", CryptoProviderUtil.getProviderName("PKCS12", "KeyStore"));
		Assert.assertEquals(
			"SunJCE", CryptoProviderUtil.getProviderName("HmacSHA256", "Mac"));
		Assert.assertEquals(
			"SUN",
			CryptoProviderUtil.getProviderName("SHA-256", "MessageDigest"));
		Assert.assertEquals(
			"SunJSSE",
			CryptoProviderUtil.getProviderName("TLSv1.3", "SSLContext"));
		Assert.assertEquals(
			"SunJCE",
			CryptoProviderUtil.getProviderName(
				"PBKDF2WithHmacSHA256", "SecretKeyFactory"));
		Assert.assertEquals(
			"SUN",
			CryptoProviderUtil.getProviderName("SHA1PRNG", "SecureRandom"));
		Assert.assertEquals(
			"SunRsaSign",
			CryptoProviderUtil.getProviderName("SHA256withRSA", "Signature"));
		Assert.assertEquals(
			"SunJSSE",
			CryptoProviderUtil.getProviderName("PKIX", "TrustManagerFactory"));

		try {
			CryptoProviderUtil.getProviderName(
				RandomTestUtil.randomString(), "MessageDigest");

			Assert.fail();
		}
		catch (NoSuchAlgorithmException noSuchAlgorithmException) {
		}

		try {
			CryptoProviderUtil.getProviderName(
				RandomTestUtil.randomString(), "BCrypt");

			Assert.fail();
		}
		catch (IllegalArgumentException illegalArgumentException) {
			Assert.assertEquals(
				"Unsupported JCA class BCrypt",
				illegalArgumentException.getMessage());
		}
	}

	@Test
	public void testIsSupportedJCAClassName() {
		Assert.assertTrue(CryptoProviderUtil.isSupportedJCAClassName("Cipher"));
		Assert.assertTrue(
			CryptoProviderUtil.isSupportedJCAClassName("MessageDigest"));
		Assert.assertTrue(
			CryptoProviderUtil.isSupportedJCAClassName("SecureRandom"));
		Assert.assertFalse(
			CryptoProviderUtil.isSupportedJCAClassName("BCrypt"));
		Assert.assertFalse(CryptoProviderUtil.isSupportedJCAClassName(null));
	}

}