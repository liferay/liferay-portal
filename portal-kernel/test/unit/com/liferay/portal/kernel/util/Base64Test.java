/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.util;

import com.liferay.petra.string.CharPool;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Kevin Lee
 */
public class Base64Test {

	@Test
	public void testDecodeFromURLWithOptionalPadding() {

		// Double padding

		Assert.assertArrayEquals(
			"life".getBytes(), Base64.decodeFromURL("bGlmZQ"));
		Assert.assertArrayEquals(
			"life".getBytes(), Base64.decodeFromURL("bGlmZQ**"));

		// Single padding

		Assert.assertArrayEquals(
			"liferay".getBytes(), Base64.decodeFromURL("bGlmZXJheQ"));
		Assert.assertArrayEquals(
			"liferay".getBytes(), Base64.decodeFromURL("bGlmZXJheQ*"));
	}

	@Test
	public void testDecodeWithOptionalPadding() {

		// Double padding

		Assert.assertArrayEquals("life".getBytes(), Base64.decode("bGlmZQ"));
		Assert.assertArrayEquals("life".getBytes(), Base64.decode("bGlmZQ=="));

		// Single padding

		Assert.assertArrayEquals(
			"liferay".getBytes(), Base64.decode("bGlmZXJheQ"));
		Assert.assertArrayEquals(
			"liferay".getBytes(), Base64.decode("bGlmZXJheQ="));
	}

	@Test
	public void testEncode() {
		java.util.Base64.Encoder encoder = java.util.Base64.getEncoder();

		for (int length : _LENGTHS) {
			byte[] bytes = _getBytes(length);

			Assert.assertEquals(
				encoder.encodeToString(bytes), Base64.encode(bytes));
		}
	}

	@Test
	public void testEncodeToURL() {
		java.util.Base64.Encoder encoder = java.util.Base64.getUrlEncoder();

		for (int length : _LENGTHS) {
			byte[] bytes = _getBytes(length);

			Assert.assertEquals(
				StringUtil.replace(
					encoder.encodeToString(bytes), CharPool.EQUAL,
					CharPool.STAR),
				Base64.encodeToURL(bytes));
		}
	}

	private byte[] _getBytes(int length) {
		byte[] bytes = new byte[length];

		for (int i = 0; i < length; i++) {
			bytes[i] = (byte)((i * 31) + length);
		}

		return bytes;
	}

	private static final int[] _LENGTHS = {0, 1, 2, 3, 1000};

}