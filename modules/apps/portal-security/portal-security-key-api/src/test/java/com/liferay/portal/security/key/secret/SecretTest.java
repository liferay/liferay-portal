/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.key.secret;

import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.security.key.KeyReference;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.nio.charset.StandardCharsets;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author Tomas Polesovsky
 * @author Christopher Kian
 */
public class SecretTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Test
	public void testConstructor() {

		// Copies the input so a later mutation does not affect the secret

		byte[] bytes = RandomTestUtil.randomBytes();

		byte firstByte = bytes[0];

		Secret secret = new Secret(bytes, _createKeyReference());

		bytes[0] = (byte)~firstByte;

		Assert.assertEquals(firstByte, secret.getBytes()[0]);

		// Rejects a null key reference

		Assert.assertThrows(
			IllegalArgumentException.class,
			() -> new Secret(RandomTestUtil.randomBytes(), null));
		Assert.assertThrows(
			IllegalArgumentException.class,
			() -> new Secret(null, RandomTestUtil.randomString()));

		// Rejects characters that are not valid UTF-16

		Assert.assertThrows(
			IllegalArgumentException.class,
			() -> new Secret(
				_createKeyReference(), new String(new char[] {'\uD800'})));
	}

	@Test
	public void testDestroy() {

		// Reports destroyed and is idempotent

		Secret secret = new Secret(
			RandomTestUtil.randomBytes(), _createKeyReference());

		secret.destroy();
		secret.destroy();

		Assert.assertTrue(secret.isDestroyed());

		// Zeroes the byte buffer

		Secret byteSecret = new Secret(
			RandomTestUtil.randomBytes(), _createKeyReference());

		byte[] internalBytes = byteSecret.getBytes();

		byteSecret.destroy();

		for (byte b : internalBytes) {
			Assert.assertEquals(0, b);
		}

		// Zeroes the char buffer

		Secret charSecret = new Secret(
			_createKeyReference(), RandomTestUtil.randomString());

		char[] chars = charSecret.getChars();

		charSecret.destroy();

		for (char c : chars) {
			Assert.assertEquals('\0', c);
		}
	}

	@Test
	public void testGetBytes() {

		// Returns the same array instance on repeated calls

		Secret secret = new Secret(
			RandomTestUtil.randomBytes(), _createKeyReference());

		Assert.assertSame(secret.getBytes(), secret.getBytes());

		// Throws once the secret is destroyed

		secret.destroy();

		Assert.assertThrows(IllegalStateException.class, secret::getBytes);
	}

	@Test
	public void testGetChars() {

		// Decodes the stored bytes and caches the result

		String string = RandomTestUtil.randomString();

		Secret secret = new Secret(
			string.getBytes(StandardCharsets.UTF_8), _createKeyReference());

		Assert.assertArrayEquals(string.toCharArray(), secret.getChars());
		Assert.assertSame(secret.getChars(), secret.getChars());

		// Preserves Unicode content through the round trip

		String unicodeString = "héllo 世界";

		Secret unicodeSecret = new Secret(_createKeyReference(), unicodeString);

		Assert.assertArrayEquals(
			unicodeString.getBytes(StandardCharsets.UTF_8),
			unicodeSecret.getBytes());
		Assert.assertArrayEquals(
			unicodeString.toCharArray(), unicodeSecret.getChars());

		// Rejects stored bytes that are not valid UTF-8

		Secret invalidSecret = new Secret(
			new byte[] {(byte)0xC0, (byte)0xC0}, _createKeyReference());

		Assert.assertThrows(
			IllegalArgumentException.class, invalidSecret::getChars);

		// Throws once the secret is destroyed

		secret.destroy();

		Assert.assertThrows(IllegalStateException.class, secret::getChars);
	}

	private KeyReference _createKeyReference() {
		return new KeyReference(
			RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			KeyReference.Type.SECRET);
	}

}