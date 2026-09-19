/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.util;

import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;

import java.util.LinkedHashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Olaf Kock
 * @author Neil Zhao Jin
 */
public class HtmlUtilTest {

	@Test
	public void testBuildData() {
		Assert.assertEquals(StringPool.BLANK, HtmlUtil.buildData(null));

		Map<String, Object> data = new LinkedHashMap<>();

		Assert.assertEquals(StringPool.BLANK, HtmlUtil.buildData(data));

		data.put("key1", "value1");

		Assert.assertEquals("data-key1=\"value1\" ", HtmlUtil.buildData(data));

		data.put("key2", "value2");

		Assert.assertEquals(
			"data-key1=\"value1\" data-key2=\"value2\" ",
			HtmlUtil.buildData(data));
	}

	@Test
	public void testEscapeBlank() {
		assertUnchangedEscape("");
	}

	@Test
	public void testEscapeCSS() {
		Assert.assertEquals("1", HtmlUtil.escapeCSS("1"));
		Assert.assertEquals("\\27", HtmlUtil.escapeCSS("'"));
		Assert.assertEquals("\\27 1", HtmlUtil.escapeCSS("'1"));
		Assert.assertEquals("\\27a", HtmlUtil.escapeCSS("'a"));
	}

	@Test
	public void testEscapeCaseSensitive() {
		assertUnchangedEscape("CAPITAL lowercase Text");
	}

	@Test
	public void testEscapeExtendedASCIICharacters() {
		StringBundler sb = new StringBundler(256);

		for (int i = 0; i < 256; i++) {
			if (Character.isLetterOrDigit(i)) {
				sb.append((char)i);
			}
		}

		String value = sb.toString();

		Assert.assertEquals(value, HtmlUtil.escape(value));

		Assert.assertEquals(value, HtmlUtil.escapeAttribute(value));
	}

	@Test
	public void testEscapeHREF() {
		Assert.assertNull(HtmlUtil.escapeHREF(null));
		Assert.assertEquals(
			StringPool.BLANK, HtmlUtil.escapeHREF(StringPool.BLANK));
		Assert.assertEquals(
			"javascript%3aalert(&#39;hello&#39;);",
			HtmlUtil.escapeHREF("javascript:alert('hello');"));
		Assert.assertEquals(
			"data%3atext/html;base64," +
				"PHNjcmlwdD5hbGVydCgndGVzdDMnKTwvc2NyaXB0Pg",
			HtmlUtil.escapeHREF(
				"data:text/html;base64," +
					"PHNjcmlwdD5hbGVydCgndGVzdDMnKTwvc2NyaXB0Pg"));

		String portalURL = "http://localhost:1234";

		Assert.assertEquals(portalURL, HtmlUtil.escapeHREF(portalURL));

		Assert.assertEquals(
			"javascript\t%3aalert(1)",
			HtmlUtil.escapeHREF("javascript\t:alert(1)"));
		Assert.assertEquals(
			"java script%3aalert(1)",
			HtmlUtil.escapeHREF("java script:alert(1)"));
		Assert.assertEquals(
			"java\nscript %3aalert(1)",
			HtmlUtil.escapeHREF("java\nscript :alert(1)"));
	}

	@Test
	public void testEscapeHtmlEncodingAmpersand() {
		Assert.assertEquals("&amp;", HtmlUtil.escape("&"));
	}

	@Test
	public void testEscapeHtmlEncodingAmpersandInBetween() {
		Assert.assertEquals("You &amp; Me", HtmlUtil.escape("You & Me"));
	}

	@Test
	public void testEscapeHtmlEncodingDoubleQuotes() {
		Assert.assertEquals(
			"&lt;span class=&#34;test&#34;&gt;Test&lt;/span&gt;",
			HtmlUtil.escape("<span class=\"test\">Test</span>"));
	}

	@Test
	public void testEscapeHtmlEncodingGreaterThan() {
		Assert.assertEquals("&gt;", HtmlUtil.escape(">"));
	}

	@Test
	public void testEscapeHtmlEncodingLessThan() {
		Assert.assertEquals("&lt;", HtmlUtil.escape("<"));
	}

	@Test
	public void testEscapeHtmlEncodingQuotes() {
		Assert.assertEquals(
			"I&#39;m quoting: &#34;this is a quote&#34;",
			HtmlUtil.escape("I'm quoting: \"this is a quote\""));
	}

	@Test
	public void testEscapeHtmlEncodingScriptTag() {
		Assert.assertEquals("&lt;script&gt;", HtmlUtil.escape("<script>"));
	}

	@Test
	public void testEscapeJS() {
		Assert.assertEquals("\\u2028", HtmlUtil.escapeJS("\u2028"));
		Assert.assertEquals("\\u2029", HtmlUtil.escapeJS("\u2029"));
		Assert.assertEquals("\\x0a", HtmlUtil.escapeJS("\n"));
		Assert.assertEquals("\\x0d", HtmlUtil.escapeJS("\r"));
		Assert.assertEquals("\\x22", HtmlUtil.escapeJS("\""));
		Assert.assertEquals("\\x27", HtmlUtil.escapeJS("'"));
		Assert.assertEquals("\\x5c", HtmlUtil.escapeJS("\\"));
	}

	@Test
	public void testEscapeJSLink() {
		Assert.assertEquals(
			"%00javascript%3a//localhost:800/123%0aalert(document.domain)",
			HtmlUtil.escapeJSLink(
				"%00javascript://localhost:800/123%0aalert(document.domain)"));

		String portalURL = "http://localhost:1234";

		Assert.assertEquals(portalURL, HtmlUtil.escapeJSLink(portalURL));
		Assert.assertEquals(
			portalURL + "/test?scope='javascript:;'",
			HtmlUtil.escapeJSLink(portalURL + "/test?scope='javascript:;'"));

		Assert.assertEquals(
			"javascript%3a//localhost:800/123%0aalert(document.domain)",
			HtmlUtil.escapeJSLink(
				"\tjavascript://localhost:800/123%0aalert(document.domain)"));
		Assert.assertEquals(
			"javascript%3aalert('hello');",
			HtmlUtil.escapeJSLink("javascript:alert('hello');"));
	}

	@Test
	public void testEscapeNoTrimmingPerformed() {
		assertUnchangedEscape("  no trimming performed ");
	}

	@Test
	public void testEscapeNull() {
		Assert.assertNull(HtmlUtil.escape(null));
	}

	@Test
	public void testEscapeNullChar() {
		Assert.assertEquals(
			StringPool.SPACE, HtmlUtil.escape(StringPool.NULL_CHAR));
	}

	@Test
	public void testEscapeSemiColon() {
		assertUnchangedEscape(";");
	}

	@Test
	public void testEscapeText() {
		assertUnchangedEscape("text");
	}

	@Test
	public void testEscapeUTF8SupplementaryCharacter() {
		assertUnchangedEscape("\uD83D\uDC31");
	}

	@Test
	public void testEscapeWhitespace() {
		assertUnchangedEscape(" ");
	}

	@Test
	public void testGetAUICompatibleId() {
		Assert.assertNull(HtmlUtil.getAUICompatibleId(null));
		Assert.assertEquals(
			StringPool.BLANK, HtmlUtil.getAUICompatibleId(StringPool.BLANK));
		Assert.assertEquals(
			"hello_20_world", HtmlUtil.getAUICompatibleId("hello world"));
		Assert.assertEquals(
			"hello__world", HtmlUtil.getAUICompatibleId("hello_world"));

		StringBundler actualSB = new StringBundler(53);

		for (int i = 0; i <= 47; i++) {
			actualSB.append(StringPool.ASCII_TABLE[i]);
		}

		actualSB.append(":;<=>?@[\\]^_`{|}~");
		actualSB.append(CharPool.DELETE);
		actualSB.append(CharPool.NO_BREAK_SPACE);
		actualSB.append(CharPool.FIGURE_SPACE);
		actualSB.append(CharPool.NARROW_NO_BREAK_SPACE);

		Assert.assertEquals(
			StringBundler.concat(
				"_0__1__2__3__4__5__6__7__8__9__a__b__c__d__e__f_",
				"_10__11__12__13__14__15__16__17__18__19__1a__1b_",
				"_1c__1d__1e__1f__20__21__22__23__24__25__26__27_",
				"_28__29__2a__2b__2c__2d__2e__2f__3a__3b__3c__3d_",
				"_3e__3f__40__5b__5c__5d__5e____60__7b__7c__7d__7e_",
				"_7f__a0__2007__202f_"),
			HtmlUtil.getAUICompatibleId(actualSB.toString()));
	}

	@Test
	public void testNewLineConversion() {
		Assert.assertEquals(
			"one<br />two<br />three<br /><br />five",
			HtmlUtil.replaceNewLine("one\ntwo\r\nthree\n\nfive"));
	}

	@Test
	public void testStripBetween() {
		Assert.assertEquals(
			"test-test-test", HtmlUtil.stripBetween("test-test-test", "test"));
	}

	@Test
	public void testStripBetweenHtmlElement() {
		Assert.assertEquals(
			"test--test",
			HtmlUtil.stripBetween(
				"test-<honk>thiswillbestripped</honk>-test", "honk"));
	}

	@Test
	public void testStripBetweenHtmlElementAcrossLines() {
		Assert.assertEquals(
			"works across  lines",
			HtmlUtil.stripBetween(
				"works across <honk>\r\n a number of </honk> lines", "honk"));
	}

	@Test
	public void testStripBetweenHtmlElementWithAttribute() {
		Assert.assertEquals(
			"test--test",
			HtmlUtil.stripBetween(
				"test-<honk attribute=\"value\">thiswillbestripped</honk>-test",
				"honk"));
	}

	@Test
	public void testStripBetweenMultipleOcurrencesOfHtmlElement() {
		Assert.assertEquals(
			"multiple occurrences, multiple indeed",
			HtmlUtil.stripBetween(
				"multiple <a>many</a>occurrences, multiple <a>HONK</a>indeed",
				"a"));
	}

	@Test
	public void testStripBetweenNull() {
		Assert.assertNull(HtmlUtil.stripBetween(null, "test"));
	}

	@Test
	public void testStripBetweenSelfClosedHtmlElement() {
		Assert.assertEquals(
			"self-closing <test/> is unhandled",
			HtmlUtil.stripBetween("self-closing <test/> is unhandled", "test"));
	}

	@Test
	public void testStripBetweenSelfClosedHtmlElementWithWhitespaceEnding() {
		Assert.assertEquals(
			"self-closing <test /> is unhandled",
			HtmlUtil.stripBetween(
				"self-closing <test /> is unhandled", "test"));
	}

	@Test
	public void testStripComments() {
		Assert.assertEquals("", HtmlUtil.stripComments("<!-- bla -->"));
	}

	@Test
	public void testStripCommentsAccrossLines() {
		Assert.assertEquals("test", HtmlUtil.stripComments("te<!-- \n -->st"));
	}

	@Test
	public void testStripCommentsAfter() {
		Assert.assertEquals(
			"test", HtmlUtil.stripComments("test<!--  bla -->"));
	}

	@Test
	public void testStripCommentsBefore() {
		Assert.assertEquals(
			"test", HtmlUtil.stripComments("<!--  bla -->test"));
	}

	@Test
	public void testStripEmptyComments() {
		Assert.assertEquals("", HtmlUtil.stripComments("<!---->"));
	}

	@Test
	public void testStripHtml() {
		Assert.assertEquals(
			"Hello World!",
			HtmlUtil.stripHtml(
				"<html><body><h1>Hello World!</h1></body></html>"));
	}

	@Test
	public void testStripHtmlWithNoscriptTag() {
		Assert.assertEquals(
			"Hello World!",
			HtmlUtil.stripHtml(
				"<body>Hello<noscript>No JavaScript</noscript> World!</body>"));
	}

	@Test
	public void testStripHtmlWithScriptTag() {
		Assert.assertEquals(
			"Hello World!",
			HtmlUtil.stripHtml(
				"<body>Hello<script>alert('xss');</script> World!</body>"));
	}

	@Test
	public void testStripHtmlWithStyleTag() {
		Assert.assertEquals(
			"Hello World!",
			HtmlUtil.stripHtml(
				"<body>Hello<style>p{color:#000000}</style> World!</body>"));
	}

	@Test
	public void testStripMultipleComments() {
		Assert.assertEquals(
			"test",
			HtmlUtil.stripComments("te<!--  bla -->s<!-- bla bla -->t"));
	}

	@Test
	public void testStripMultipleEmptyComments() {
		Assert.assertEquals(
			"test", HtmlUtil.stripComments("te<!-- --><!-- -->st"));
	}

	@Test
	public void testStripNullComments() {
		Assert.assertNull(HtmlUtil.stripComments(null));
	}

	@Test
	public void testUnescapeDoubleHtmlEncoding() {
		Assert.assertEquals(
			"&#034;", HtmlUtil.unescape(HtmlUtil.escape("&#034;")));
	}

	@Test
	public void testUnescapeHtmlEncodingAmpersand() {
		Assert.assertEquals("&", HtmlUtil.unescape("&amp;"));
	}

	@Test
	public void testUnescapeHtmlEncodingAmpersandInBetween() {
		Assert.assertEquals("You & Me", HtmlUtil.unescape("You &amp; Me"));
	}

	@Test
	public void testUnescapeHtmlEncodingEmDash() {
		Assert.assertEquals("\u2014", HtmlUtil.unescape("&#8212;"));
	}

	@Test
	public void testUnescapeHtmlEncodingEnDash() {
		Assert.assertEquals("\u2013", HtmlUtil.unescape("&#8211;"));
	}

	@Test
	public void testUnescapeHtmlEncodingRightSingleQuote() {
		Assert.assertEquals("\u2019", HtmlUtil.unescape("&rsquo;"));
	}

	protected void assertUnchangedEscape(String input) {
		Assert.assertEquals(input, HtmlUtil.escape(input));
	}

}