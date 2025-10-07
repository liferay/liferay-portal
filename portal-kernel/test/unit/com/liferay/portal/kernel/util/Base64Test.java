/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.util;

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

}