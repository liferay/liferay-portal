/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.content.marketing.platform.web.internal.util;

import com.liferay.portal.test.rule.LiferayUnitTestRule;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author Larissa Ribeiro
 */
public class CMPRichTextUtilTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Test
	public void testToHTML() {
		Assert.assertEquals("", CMPRichTextUtil.toHTML(""));
		Assert.assertEquals("", CMPRichTextUtil.toHTML("  "));
		Assert.assertEquals(
			"<p>a<br>b</p><p>c</p>", CMPRichTextUtil.toHTML("a\r\nb\r\n\r\nc"));
		Assert.assertEquals(
			"<p>a &lt; b &amp; c &gt; d</p>",
			CMPRichTextUtil.toHTML("a < b & c > d"));
		Assert.assertEquals(
			"<p>a</p><p>b</p>", CMPRichTextUtil.toHTML("a\n\n\n\nb"));
		Assert.assertEquals(
			"<p>a</p><p>b</p>", CMPRichTextUtil.toHTML("a\n\n\nb"));
		Assert.assertEquals(
			"<p>first</p><p>second</p>",
			CMPRichTextUtil.toHTML("first\n\nsecond"));
		Assert.assertEquals(
			"<p>line one<br>line two</p>",
			CMPRichTextUtil.toHTML("line one\nline two"));
		Assert.assertNull(CMPRichTextUtil.toHTML(null));
	}

}