/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.translation.internal.tokenizer;

import com.liferay.petra.string.StringBundler;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.util.List;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author Akhash Ramprakash
 */
public class HTMLInlineCodeTokenizerTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Test
	public void testTokenizeApostropheInUnquotedAttributeValue() {
		List<HTMLInlineCodeToken> htmlInlineCodeTokens = _tokenize(
			"<p title=a'b>Hello x'y stop</p>");

		Assert.assertEquals(
			htmlInlineCodeTokens.toString(), 3, htmlInlineCodeTokens.size());

		_assertToken(
			htmlInlineCodeTokens.get(0), HTMLInlineCodeToken.Type.OPENING,
			"<p title=a'b>");
		_assertToken(
			htmlInlineCodeTokens.get(1), HTMLInlineCodeToken.Type.TEXT,
			"Hello x'y stop");
		_assertToken(
			htmlInlineCodeTokens.get(2), HTMLInlineCodeToken.Type.CLOSING,
			"</p>");
	}

	@Test
	public void testTokenizeBareAmpersandAndLessThan() {
		List<HTMLInlineCodeToken> htmlInlineCodeTokens = _tokenize(
			"a < b && c > d");

		Assert.assertEquals(
			htmlInlineCodeTokens.toString(), 1, htmlInlineCodeTokens.size());

		_assertToken(
			htmlInlineCodeTokens.get(0), HTMLInlineCodeToken.Type.TEXT,
			"a < b && c > d");
	}

	@Test
	public void testTokenizeCDATASection() {
		List<HTMLInlineCodeToken> htmlInlineCodeTokens = _tokenize(
			"a<![CDATA[literal <b> & text]]>b");

		Assert.assertEquals(
			htmlInlineCodeTokens.toString(), 3, htmlInlineCodeTokens.size());

		_assertToken(
			htmlInlineCodeTokens.get(1), HTMLInlineCodeToken.Type.PLACEHOLDER,
			"<![CDATA[literal <b> & text]]>");
	}

	@Test
	public void testTokenizeComment() {
		List<HTMLInlineCodeToken> htmlInlineCodeTokens = _tokenize(
			"a<!-- comment with <b> tags -->b");

		Assert.assertEquals(
			htmlInlineCodeTokens.toString(), 3, htmlInlineCodeTokens.size());

		_assertToken(
			htmlInlineCodeTokens.get(1), HTMLInlineCodeToken.Type.PLACEHOLDER,
			"<!-- comment with <b> tags -->");
	}

	@Test
	public void testTokenizeDoctype() {
		List<HTMLInlineCodeToken> htmlInlineCodeTokens = _tokenize(
			"<!DOCTYPE html><p>text</p>");

		_assertToken(
			htmlInlineCodeTokens.get(0), HTMLInlineCodeToken.Type.PLACEHOLDER,
			"<!DOCTYPE html>");
	}

	@Test
	public void testTokenizeEntities() {
		List<HTMLInlineCodeToken> htmlInlineCodeTokens = _tokenize(
			"&amp; &#160; &#x27; &notterminated and &amp");

		_assertToken(
			htmlInlineCodeTokens.get(0), HTMLInlineCodeToken.Type.PLACEHOLDER,
			"&amp;");
		_assertToken(
			htmlInlineCodeTokens.get(2), HTMLInlineCodeToken.Type.PLACEHOLDER,
			"&#160;");
		_assertToken(
			htmlInlineCodeTokens.get(4), HTMLInlineCodeToken.Type.PLACEHOLDER,
			"&#x27;");
		_assertToken(
			htmlInlineCodeTokens.get(5), HTMLInlineCodeToken.Type.TEXT,
			" &notterminated and &amp");
	}

	@Test
	public void testTokenizeNestedTags() {
		List<HTMLInlineCodeToken> htmlInlineCodeTokens = _tokenize(
			"<p>Hello <b>world</b> &amp; more</p>");

		_assertToken(
			htmlInlineCodeTokens.get(0), HTMLInlineCodeToken.Type.OPENING,
			"<p>");
		_assertToken(
			htmlInlineCodeTokens.get(1), HTMLInlineCodeToken.Type.TEXT,
			"Hello ");
		_assertToken(
			htmlInlineCodeTokens.get(2), HTMLInlineCodeToken.Type.OPENING,
			"<b>");
		_assertToken(
			htmlInlineCodeTokens.get(3), HTMLInlineCodeToken.Type.TEXT,
			"world");
		_assertToken(
			htmlInlineCodeTokens.get(4), HTMLInlineCodeToken.Type.CLOSING,
			"</b>");
		_assertToken(
			htmlInlineCodeTokens.get(6), HTMLInlineCodeToken.Type.PLACEHOLDER,
			"&amp;");
		_assertToken(
			htmlInlineCodeTokens.get(8), HTMLInlineCodeToken.Type.CLOSING,
			"</p>");
	}

	@Test
	public void testTokenizeQuotedAttributes() {
		List<HTMLInlineCodeToken> htmlInlineCodeTokens = _tokenize(
			"<a href=\"x?a>b\" title='y>z'>link</a>");

		Assert.assertEquals(
			htmlInlineCodeTokens.toString(), 3, htmlInlineCodeTokens.size());

		_assertToken(
			htmlInlineCodeTokens.get(0), HTMLInlineCodeToken.Type.OPENING,
			"<a href=\"x?a>b\" title='y>z'>");
	}

	@Test
	public void testTokenizeRawTextElementWithPrefixedClosingTag() {
		List<HTMLInlineCodeToken> htmlInlineCodeTokens = _tokenize(
			"<script>var s = \"</scriptFoo>\"; doWork();</script>");

		Assert.assertEquals(
			htmlInlineCodeTokens.toString(), 1, htmlInlineCodeTokens.size());

		_assertToken(
			htmlInlineCodeTokens.get(0), HTMLInlineCodeToken.Type.PLACEHOLDER,
			"<script>var s = \"</scriptFoo>\"; doWork();</script>");
	}

	@Test
	public void testTokenizeScriptBlock() {
		List<HTMLInlineCodeToken> htmlInlineCodeTokens = _tokenize(
			"<p>before</p><script type=\"text/javascript\">if (a < b) { " +
				"alert(\"hi\"); }</script>after");

		_assertToken(
			htmlInlineCodeTokens.get(3), HTMLInlineCodeToken.Type.PLACEHOLDER,
			"<script type=\"text/javascript\">if (a < b) { alert(\"hi\"); }" +
				"</script>");
		_assertToken(
			htmlInlineCodeTokens.get(4), HTMLInlineCodeToken.Type.TEXT,
			"after");
	}

	@Test
	public void testTokenizeStyleBlock() {
		List<HTMLInlineCodeToken> htmlInlineCodeTokens = _tokenize(
			"<style>.a > .b { color: red; }</style>text");

		_assertToken(
			htmlInlineCodeTokens.get(0), HTMLInlineCodeToken.Type.PLACEHOLDER,
			"<style>.a > .b { color: red; }</style>");
	}

	@Test
	public void testTokenizeTagNamesAreLowerCased() {
		List<HTMLInlineCodeToken> htmlInlineCodeTokens = _tokenize(
			"<P Class=\"x\">text</P><BR>");

		_assertTagName(htmlInlineCodeTokens.get(0), "p");
		_assertTagName(htmlInlineCodeTokens.get(2), "p");
		_assertTagName(htmlInlineCodeTokens.get(3), "br");
	}

	@Test
	public void testTokenizeUnknownNamedEntity() {
		List<HTMLInlineCodeToken> htmlInlineCodeTokens = _tokenize(
			"Fish &Chips; specials &eacute;");

		Assert.assertEquals(
			htmlInlineCodeTokens.toString(), 2, htmlInlineCodeTokens.size());

		_assertToken(
			htmlInlineCodeTokens.get(0), HTMLInlineCodeToken.Type.TEXT,
			"Fish &Chips; specials ");
		_assertToken(
			htmlInlineCodeTokens.get(1), HTMLInlineCodeToken.Type.PLACEHOLDER,
			"&eacute;");
	}

	@Test
	public void testTokenizeUnmatchedQuoteInSelfClosingTag() {
		List<HTMLInlineCodeToken> htmlInlineCodeTokens = _tokenize(
			"a<a href=a\"b c/>b");

		Assert.assertEquals(
			htmlInlineCodeTokens.toString(), 3, htmlInlineCodeTokens.size());

		_assertToken(
			htmlInlineCodeTokens.get(0), HTMLInlineCodeToken.Type.TEXT, "a");
		_assertToken(
			htmlInlineCodeTokens.get(1), HTMLInlineCodeToken.Type.PLACEHOLDER,
			"<a href=a\"b c/>");
		_assertToken(
			htmlInlineCodeTokens.get(2), HTMLInlineCodeToken.Type.TEXT, "b");
	}

	@Test
	public void testTokenizeUnquotedAttributeValueTrailingSlash() {
		List<HTMLInlineCodeToken> htmlInlineCodeTokens = _tokenize(
			"<a href=https://liferay.com/>link</a><input value=foo />");

		Assert.assertEquals(
			htmlInlineCodeTokens.toString(), 4, htmlInlineCodeTokens.size());

		_assertToken(
			htmlInlineCodeTokens.get(0), HTMLInlineCodeToken.Type.OPENING,
			"<a href=https://liferay.com/>");
		_assertToken(
			htmlInlineCodeTokens.get(1), HTMLInlineCodeToken.Type.TEXT, "link");
		_assertToken(
			htmlInlineCodeTokens.get(2), HTMLInlineCodeToken.Type.CLOSING,
			"</a>");
		_assertToken(
			htmlInlineCodeTokens.get(3), HTMLInlineCodeToken.Type.PLACEHOLDER,
			"<input value=foo />");
	}

	@Test
	public void testTokenizeUnterminatedRawTextElement() {
		List<HTMLInlineCodeToken> htmlInlineCodeTokens = _tokenize(
			"a<script>never closed");

		Assert.assertEquals(
			htmlInlineCodeTokens.toString(), 1, htmlInlineCodeTokens.size());

		_assertToken(
			htmlInlineCodeTokens.get(0), HTMLInlineCodeToken.Type.TEXT,
			"a<script>never closed");
	}

	@Test
	public void testTokenizeUnterminatedTag() {
		List<HTMLInlineCodeToken> htmlInlineCodeTokens = _tokenize(
			"<p>unclosed <b");

		_assertToken(
			htmlInlineCodeTokens.get(0), HTMLInlineCodeToken.Type.OPENING,
			"<p>");
		_assertToken(
			htmlInlineCodeTokens.get(1), HTMLInlineCodeToken.Type.TEXT,
			"unclosed <b");
	}

	@Test
	public void testTokenizeVoidAndSelfClosingTags() {
		List<HTMLInlineCodeToken> htmlInlineCodeTokens = _tokenize(
			"Line<br>break <img src=\"a.png\"/> done");

		_assertToken(
			htmlInlineCodeTokens.get(1), HTMLInlineCodeToken.Type.PLACEHOLDER,
			"<br>");
		_assertToken(
			htmlInlineCodeTokens.get(3), HTMLInlineCodeToken.Type.PLACEHOLDER,
			"<img src=\"a.png\"/>");
	}

	private void _assertTagName(
		HTMLInlineCodeToken htmlInlineCodeToken, String tagName) {

		Assert.assertEquals(tagName, htmlInlineCodeToken.getTagName());
	}

	private void _assertToken(
		HTMLInlineCodeToken htmlInlineCodeToken, HTMLInlineCodeToken.Type type,
		String rawText) {

		Assert.assertEquals(type, htmlInlineCodeToken.getType());
		Assert.assertEquals(rawText, htmlInlineCodeToken.getRawText());
	}

	private List<HTMLInlineCodeToken> _tokenize(String html) {
		List<HTMLInlineCodeToken> htmlInlineCodeTokens =
			HTMLInlineCodeTokenizer.tokenize(html);

		StringBundler sb = new StringBundler(htmlInlineCodeTokens.size());

		for (HTMLInlineCodeToken htmlInlineCodeToken : htmlInlineCodeTokens) {
			sb.append(htmlInlineCodeToken.getRawText());
		}

		Assert.assertEquals(html, sb.toString());

		return htmlInlineCodeTokens;
	}

}