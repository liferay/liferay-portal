/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.knowledge.base.markdown.converter.internal;

import com.liferay.knowledge.base.markdown.converter.MarkdownConverter;
import com.liferay.knowledge.base.markdown.converter.factory.MarkdownConverterFactory;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author Andy Wu
 */
public class FlexmarkMarkdownConverterTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Test
	public void testMultilineHeading() throws Exception {
		String randomId = StringUtil.randomId();

		String markdown = StringBundler.concat(
			"The liferay-ui:logo-selector Tag Requires Parameter Changes [](id",
			"=", randomId, ")\n=================================");

		MarkdownConverterFactory markdownConverterFactory =
			new FlexmarkMarkdownConverterFactory();

		MarkdownConverter markdownConverter = markdownConverterFactory.create();

		String html = markdownConverter.convert(markdown);

		Assert.assertTrue(html, html.contains("id=\"" + randomId + "\""));
	}

	@Test
	public void testPrefixHeading() throws Exception {
		String randomId = StringUtil.randomId();

		String markdown = StringBundler.concat(
			"### The liferay-ui:logo-selector Tag Requires Parameter Changes ",
			"[](id=", randomId, ")");

		MarkdownConverterFactory markdownConverterFactory =
			new FlexmarkMarkdownConverterFactory();

		MarkdownConverter markdownConverter = markdownConverterFactory.create();

		String html = markdownConverter.convert(markdown);

		Assert.assertTrue(html, html.contains("id=\"" + randomId + "\""));
	}

}