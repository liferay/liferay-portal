/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.fragment.processor.util;

import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author Alberto Chaparro
 */
public class FragmentEntryHtmlParserUtilTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Test
	@TestInfo("LPD-97574")
	public void testParse() {
		Document document = FragmentEntryHtmlParserUtil.parse(
			"<style>.a {color: red}</style>" + _BODY_HTML);

		Element headElement = document.head();

		Assert.assertEquals(
			"<style>.a {color: red}</style>", headElement.html());

		Element bodyElement = document.body();

		Assert.assertEquals(_BODY_HTML, bodyElement.html());
	}

	@Test
	@TestInfo("LPD-97574")
	public void testParseBodyFragment() {
		String html = "<style>.a {color: red}</style>" + _BODY_HTML;

		Document document = FragmentEntryHtmlParserUtil.parseBodyFragment(html);

		Element bodyElement = document.body();

		Assert.assertEquals(html, bodyElement.html());
	}

	private static final String _BODY_HTML =
		"<div><ul><li>a</li><li>b</li></ul><p>c  d</p></div>";

}