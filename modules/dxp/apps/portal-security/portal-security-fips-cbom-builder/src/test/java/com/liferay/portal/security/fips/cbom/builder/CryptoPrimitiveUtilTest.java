/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.fips.cbom.builder;

import com.liferay.portal.test.rule.LiferayUnitTestRule;

import org.cyclonedx.model.component.crypto.enums.Primitive;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author Manuele Castro
 */
public class CryptoPrimitiveUtilTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Test
	public void testGetPrimitive() {
		_testGetPrimitive(Primitive.AE, "AES/GCM/NoPadding", "Cipher");
		_testGetPrimitive(Primitive.BLOCK_CIPHER, "AES", "SecretKeySpec");
		_testGetPrimitive(
			Primitive.BLOCK_CIPHER, "AES/CBC/PKCS5Padding", "Cipher");
		_testGetPrimitive(Primitive.HASH, null, "MessageDigest");
		_testGetPrimitive(
			Primitive.KDF, "PBKDF2WithHmacSHA256", "SecretKeyFactory");
		_testGetPrimitive(Primitive.KEY_AGREE, "X25519", "KeyPairGenerator");
		_testGetPrimitive(Primitive.MAC, "HmacSHA256", "KeyGenerator");
		_testGetPrimitive(Primitive.OTHER, "PKCS12", "KeyStore");
		_testGetPrimitive(
			Primitive.PKE, "RSA/ECB/OAEPWithSHA-256AndMGF1Padding", "Cipher");
		_testGetPrimitive(Primitive.SIGNATURE, "Ed25519", "KeyPairGenerator");
		_testGetPrimitive(Primitive.SIGNATURE, "SHA256withRSA", "Signature");
		_testGetPrimitive(Primitive.STREAM_CIPHER, "ChaCha20", "Cipher");
		_testGetPrimitive(Primitive.UNKNOWN, "RSA", "KeyPairGenerator");
		_testGetPrimitive(Primitive.UNKNOWN, null, "Cipher");
	}

	private void _testGetPrimitive(
		Primitive expectedPrimitive, String algorithm, String jcaClassName) {

		Assert.assertEquals(
			expectedPrimitive,
			CryptoPrimitiveUtil.getPrimitive(algorithm, jcaClassName));
	}

}