/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.encryptor;

import com.liferay.petra.lang.SafeCloseable;
import com.liferay.portal.kernel.encryptor.Encryptor;
import com.liferay.portal.kernel.test.util.PropsValuesTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.security.Key;

import javax.crypto.spec.SecretKeySpec;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author Mika Koivisto
 */
public class EncryptorImplTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Test
	public void testDecrypt() throws Exception {
		Encryptor encryptor = new EncryptorImpl();

		Key key = encryptor.generateKey();

		String encryptedString = encryptor.encrypt(key, _PLAIN_TEXT);

		try (SafeCloseable safeCloseable =
				PropsValuesTestUtil.swapWithSafeCloseable(
					"FIPS_ENABLED", true)) {

			Assert.assertEquals(
				_PLAIN_TEXT, encryptor.decrypt(key, encryptedString));
		}
	}

	@Test
	public void testEncrypt() throws Exception {
		try (SafeCloseable safeCloseable =
				PropsValuesTestUtil.swapWithSafeCloseable(
					"FIPS_ENABLED", true)) {

			Encryptor encryptor = new EncryptorImpl();

			Key key = encryptor.generateKey();

			String encryptedString1 = encryptor.encrypt(key, _PLAIN_TEXT);
			String encryptedString2 = encryptor.encrypt(key, _PLAIN_TEXT);

			Assert.assertNotEquals(encryptedString1, encryptedString2);

			Assert.assertEquals(
				_PLAIN_TEXT, encryptor.decrypt(key, encryptedString1));
			Assert.assertEquals(
				_PLAIN_TEXT, encryptor.decrypt(key, encryptedString2));

			Assert.assertThrows(
				SecurityException.class,
				() -> encryptor.encryptUnencoded(
					new SecretKeySpec(new byte[8], "AES"),
					_PLAIN_TEXT.getBytes()));

			Assert.assertThrows(
				SecurityException.class,
				() -> encryptor.decryptUnencodedAsBytes(
					new SecretKeySpec(new byte[8], "AES"),
					_PLAIN_TEXT.getBytes()));
		}
	}

	@Test
	public void testSerializeKey() throws Exception {
		Encryptor encryptor = new EncryptorImpl();

		Key key = encryptor.generateKey();

		String encryptedString = encryptor.encrypt(key, _PLAIN_TEXT);

		String serializedKey = encryptor.serializeKey(key);

		key = encryptor.deserializeKey(serializedKey);

		Assert.assertEquals(
			_PLAIN_TEXT, encryptor.decrypt(key, encryptedString));
	}

	private static final String _PLAIN_TEXT = RandomTestUtil.randomString();

}