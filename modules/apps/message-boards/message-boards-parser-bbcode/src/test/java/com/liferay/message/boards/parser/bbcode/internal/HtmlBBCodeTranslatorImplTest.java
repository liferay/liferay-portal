/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.message.boards.parser.bbcode.internal;

import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.util.HtmlUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author Sergio González
 * @author John Zhao
 */
public class HtmlBBCodeTranslatorImplTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Test
	public void testAlign() {
		String expected = "<p style=\"text-align: center\">text</p>";
		String actual = _htmlBBCodeTranslatorImpl.parse(
			"[center]text[/center]");

		Assert.assertEquals(expected, actual);
	}

	@Test
	public void testAsterisk() {
		String expected =
			"<strong>type</strong> some <u>text</u><li>this is a test</li>";
		String actual = _htmlBBCodeTranslatorImpl.parse(
			"[b]type[/b] some [u]text[/u][*]this is a test");

		Assert.assertEquals(expected, actual);
	}

	@Test
	public void testAsteriskInBold() {
		String expected =
			"<strong>asterisk</strong><li> is inside the bold</li>";
		String actual = _htmlBBCodeTranslatorImpl.parse(
			"[b]asterisk[*][/b] is inside the bold");

		Assert.assertEquals(expected, actual);
	}

	@Test
	public void testBold() {
		String expected = "<strong>text</strong>";
		String actual = _htmlBBCodeTranslatorImpl.parse("[b]text[/b]");

		Assert.assertEquals(expected, actual);
	}

	@Test
	public void testCode() {
		StringBundler sb = new StringBundler(4);

		sb.append("<div class=\"lfr-code\"><table><tbody><tr>");
		sb.append("<td class=\"line-numbers\">1</td>");
		sb.append("<td class=\"lines\"><div class=\"line\">:)</div>");
		sb.append("</td></tr></tbody></table></div>");

		Assert.assertEquals(
			sb.toString(), _htmlBBCodeTranslatorImpl.parse("[code]:)[/code]"));
	}

	@Test
	public void testColor() {
		String expected = "<span style=\"color: #ff0000\">text</span>";
		String actual = _htmlBBCodeTranslatorImpl.parse(
			"[color=#ff0000]text[/color]");

		Assert.assertEquals(expected, actual);
	}

	@Test
	public void testEmotion() {
		String expected =
			"<img alt=\"emoticon\" " +
				"src=\"@theme_images_path@/emoticons/happy.gif\" >";
		String actual = _htmlBBCodeTranslatorImpl.parse(":)");

		Assert.assertEquals(expected, actual);
	}

	@Test
	public void testFontFamily() {
		String expected =
			"<span style=\"font-family: " +
				HtmlUtil.escapeAttribute("georgia, serif") + "\">text</span>";
		String actual = _htmlBBCodeTranslatorImpl.parse(
			"[font=georgia, serif]text[/font]");

		Assert.assertEquals(expected, actual);
	}

	@Test
	public void testFontSize() {
		String expected = "<span style=\"font-size: 18px;\">text</span>";
		String actual = _htmlBBCodeTranslatorImpl.parse("[size=5]text[/size]");

		Assert.assertEquals(expected, actual);
	}

	@Test
	public void testImgXSS() {
		String expected = "<img src=\"\" />";
		String actual = _htmlBBCodeTranslatorImpl.parse(
			"[img]asd[font= onerror=alert(/XSS/.source)//]FF[/font][/img]");

		Assert.assertEquals(expected, actual);
	}

	@Test
	public void testIncompleteTag() {
		String expected = "<strong>text</strong>";
		String actual = _htmlBBCodeTranslatorImpl.parse("[b]text");

		Assert.assertEquals(expected, actual);
	}

	@Test
	public void testInvalidTag() {
		String expected = "[x]invalidTag[/x]";

		String actual = _htmlBBCodeTranslatorImpl.parse("[x]invalidTag[/x]");

		Assert.assertEquals(expected, actual);
	}

	@Test
	public void testInvalidTagAndValidTag() {
		String expected = "[x]bbb<u>XXX</u>ddd[/x]";
		String actual = _htmlBBCodeTranslatorImpl.parse(
			"[x]bbb[u]XXX[/u]ddd[/x]");

		Assert.assertEquals(expected, actual);
	}

	@Test
	public void testItalic() {
		String expected = "<em>text</em>";
		String actual = _htmlBBCodeTranslatorImpl.parse("[i]text[/i]");

		Assert.assertEquals(expected, actual);
	}

	@Test
	public void testOrderedList() {
		String expected =
			"<ol style=\"list-style: lower-roman outside;\" start=\"2\">" +
				"<li>line1</li><li>line2</li></ol>";
		String actual = _htmlBBCodeTranslatorImpl.parse(
			"[list type=\"i\" start=\"2\"][*]line1[*]line2[/list]");

		Assert.assertEquals(expected, actual);
	}

	@Test
	public void testQuote() {
		String expected =
			"<div class=\"quote-title\">citer:</div><div class=\"quote\">" +
				"<div class=\"quote-content\">text</div></div>";
		String actual = _htmlBBCodeTranslatorImpl.parse(
			"[quote=citer]text[/quote]");

		Assert.assertEquals(expected, actual);
	}

	@Test
	public void testStrike() {
		String expected = "<strike>text</strike>";
		String actual = _htmlBBCodeTranslatorImpl.parse("[s]text[/s]");

		Assert.assertEquals(expected, actual);
	}

	@Test
	public void testURL() {
		String url = "https://msdn.microsoft.com/aa752574(VS.85).aspx";

		String expected =
			"<a href=\"" + HtmlUtil.escapeHREF(url) + "\">link</a>";
		String actual = _htmlBBCodeTranslatorImpl.parse(
			"[url=" + url + "]link[/url]");

		Assert.assertEquals(expected, actual);
	}

	@Test
	public void testURLWithAccents() {
		String urlWithAccents =
			"https://hu.wikipedia.org/wiki/Árvíztűrő_tükörfúrógép";

		String expected =
			"<a href=\"" + HtmlUtil.escapeHREF(urlWithAccents) + "\">link</a>";
		String actual = _htmlBBCodeTranslatorImpl.parse(
			"[url=" + urlWithAccents + "]link[/url]");

		Assert.assertEquals(expected, actual);
	}

	@Test
	public void testUnderline() {
		String expected = "<u>text</u>";
		String actual = _htmlBBCodeTranslatorImpl.parse("[u]text[/u]");

		Assert.assertEquals(expected, actual);
	}

	@Test
	public void testUnorderedList() {
		String expected =
			"<ul style=\"list-style: circle outside;\">" +
				"<li>line1</li><li>line2</li></ul>";
		String actual = _htmlBBCodeTranslatorImpl.parse(
			"[list type=\"circle\"][*]line1[*]line2[/list]");

		Assert.assertEquals(expected, actual);
	}

	private final HtmlBBCodeTranslatorImpl _htmlBBCodeTranslatorImpl =
		new HtmlBBCodeTranslatorImpl();

}