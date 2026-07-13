/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.gradle.plugins.workspace.internal.util;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Gregory Amerson
 */
public class StringUtilTest {

	@Test
	public void testiFromCamelCaseToHyphenated() throws Exception {
		Assert.assertEquals(
			"-tt-tes-ttt", StringUtil.getDockerSafeName("-TTTesTTT"));
		Assert.assertEquals(
			"b2b-new-quote-process",
			StringUtil.getDockerSafeName("B2BNewQuoteProcess"));
		Assert.assertEquals(
			"b2b-new-quote-process",
			StringUtil.getDockerSafeName("b2BNewQuoteProcess"));
		Assert.assertEquals("t-est", StringUtil.getDockerSafeName("TEst"));
		Assert.assertEquals("test", StringUtil.getDockerSafeName("Test"));
		Assert.assertEquals("test", StringUtil.getDockerSafeName("test"));
		Assert.assertEquals(
			"test-test", StringUtil.getDockerSafeName("TestTest"));
		Assert.assertEquals(
			"test-test-test", StringUtil.getDockerSafeName("TestTestTest"));
		Assert.assertEquals(
			"tt-tes-ttt", StringUtil.getDockerSafeName("TTTesTTT"));
	}

	@Test
	public void testIsURL() {
		Assert.assertFalse(StringUtil.isUrl("http://example.com:-80"));
		Assert.assertFalse(StringUtil.isUrl("https://example.com:port"));
		Assert.assertTrue(StringUtil.isUrl("http://localhost:3000"));
		Assert.assertTrue(
			StringUtil.isUrl("https://en.wikipedia.org/wiki/Baseball"));
		Assert.assertTrue(StringUtil.isUrl("https://www.example.com"));
		Assert.assertTrue(
			StringUtil.isUrl("https://www.example.com/something-*-else"));
	}

	@Test
	public void testNormalizeFilePath() {
		Assert.assertNull(StringUtil.normalizeFilePath(null));
		Assert.assertEquals("", StringUtil.normalizeFilePath(""));
		Assert.assertEquals(
			"/opt/build/upgradeJakarta/dependencies.txt",
			StringUtil.normalizeFilePath(
				"/opt/build/upgradeJakarta/dependencies.txt"));
		Assert.assertEquals(
			"C:/build/upgradeJakarta/dependencies.txt",
			StringUtil.normalizeFilePath(
				"C:\\build\\upgradeJakarta\\dependencies.txt"));
	}

	@Test
	public void testSuffix() {
		Assert.assertEquals("foo", StringUtil.suffixIfNotBlank("foo", ""));
		Assert.assertEquals(
			"foo",
			StringUtil.suffixIfNotBlank("foo", StringUtil.FORWARD_SLASH, ""));
		Assert.assertEquals(
			"foo",
			StringUtil.suffixIfNotBlank("foo", StringUtil.FORWARD_SLASH, null));
		Assert.assertEquals("foo", StringUtil.suffixIfNotBlank("foo", null));
		Assert.assertEquals(
			"foo/bar",
			StringUtil.suffixIfNotBlank(
				"foo", StringUtil.FORWARD_SLASH, "bar"));
		Assert.assertEquals(
			"foo_bar", StringUtil.suffixIfNotBlank("foo", "bar"));
	}

}