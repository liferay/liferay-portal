/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.translation.internal.tokenizer;

import com.liferay.petra.string.CharPool;
import com.liferay.portal.kernel.util.SetUtil;
import com.liferay.portal.kernel.util.StringUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import net.sf.okapi.common.HTMLCharacterEntities;

/**
 * @author Akhash Ramprakash
 */
public class HTMLInlineCodeTokenizer {

	public static List<HTMLInlineCodeToken> tokenize(String html) {
		List<HTMLInlineCodeToken> htmlInlineCodeTokens = new ArrayList<>();

		int index = 0;
		int textStart = 0;

		while (index < html.length()) {
			char c = html.charAt(index);

			if ((c != CharPool.AMPERSAND) && (c != CharPool.LESS_THAN)) {
				index++;

				continue;
			}

			Markup markup = _matchMarkup(html, index);

			if (markup == null) {
				index++;

				continue;
			}

			_addTextToken(index, html, htmlInlineCodeTokens, textStart);

			htmlInlineCodeTokens.add(
				new HTMLInlineCodeToken(
					html.substring(index, markup._end), markup._tagName,
					markup._type));

			index = markup._end;

			textStart = index;
		}

		_addTextToken(html.length(), html, htmlInlineCodeTokens, textStart);

		return htmlInlineCodeTokens;
	}

	private static void _addTextToken(
		int end, String html, List<HTMLInlineCodeToken> htmlInlineCodeTokens,
		int start) {

		if (start >= end) {
			return;
		}

		htmlInlineCodeTokens.add(
			new HTMLInlineCodeToken(
				html.substring(start, end), null,
				HTMLInlineCodeToken.Type.TEXT));
	}

	private static HTMLCharacterEntities _createHTMLCharacterEntities() {
		HTMLCharacterEntities htmlCharacterEntities =
			new HTMLCharacterEntities();

		htmlCharacterEntities.ensureInitialization(true);

		return htmlCharacterEntities;
	}

	private static int _getDigitsEndIndex(String html, int index, int radix) {
		int end = index;

		while ((end < html.length()) &&
			   (Character.digit(html.charAt(end), radix) != -1)) {

			end++;
		}

		if (end == index) {
			return -1;
		}

		return end;
	}

	private static int _getRawTextElementEndIndex(
		String html, int index, String tagName) {

		String closingTag = "</" + tagName;

		for (int i = index; i <= (html.length() - closingTag.length()); i++) {
			if (!html.regionMatches(
					true, i, closingTag, 0, closingTag.length())) {

				continue;
			}

			int boundaryIndex = i + closingTag.length();

			if (boundaryIndex < html.length()) {
				char c = html.charAt(boundaryIndex);

				if ((c != CharPool.FORWARD_SLASH) &&
					(c != CharPool.GREATER_THAN) &&
					!Character.isWhitespace(c)) {

					continue;
				}
			}

			int end = html.indexOf(CharPool.GREATER_THAN, i);

			if (end == -1) {
				return -1;
			}

			return end + 1;
		}

		return -1;
	}

	private static int _getTagEndIndex(String html, int index) {
		while (index < html.length()) {
			char c = html.charAt(index);

			if (c == CharPool.GREATER_THAN) {
				return index + 1;
			}

			if (c != CharPool.EQUAL) {
				index++;

				continue;
			}

			index++;

			while ((index < html.length()) &&
				   Character.isWhitespace(html.charAt(index))) {

				index++;
			}

			if (index >= html.length()) {
				return -1;
			}

			char valueStartChar = html.charAt(index);

			if ((valueStartChar != CharPool.APOSTROPHE) &&
				(valueStartChar != CharPool.QUOTE)) {

				continue;
			}

			int quoteEndIndex = html.indexOf(valueStartChar, index + 1);

			if (quoteEndIndex == -1) {
				return -1;
			}

			index = quoteEndIndex + 1;
		}

		return -1;
	}

	private static String _getTagName(String html, int index) {
		if ((index >= html.length()) ||
			!Character.isLetter(html.charAt(index))) {

			return null;
		}

		int end = index + 1;

		while (end < html.length()) {
			char c = html.charAt(end);

			if (Character.isLetterOrDigit(c) || (c == CharPool.COLON) ||
				(c == CharPool.DASH)) {

				end++;
			}
			else {
				break;
			}
		}

		return html.substring(index, end);
	}

	private static boolean _isSelfClosing(int end, String html, int index) {
		if (html.charAt(end - 2) != CharPool.FORWARD_SLASH) {
			return false;
		}

		while (index < (end - 2)) {
			char c = html.charAt(index);

			if (c != CharPool.EQUAL) {
				index++;

				continue;
			}

			index++;

			while (Character.isWhitespace(html.charAt(index))) {
				index++;
			}

			char valueStartChar = html.charAt(index);

			if ((valueStartChar == CharPool.APOSTROPHE) ||
				(valueStartChar == CharPool.QUOTE)) {

				int quoteEndIndex = html.indexOf(valueStartChar, index + 1);

				if ((quoteEndIndex == -1) || (quoteEndIndex >= (end - 2))) {
					return false;
				}

				index = quoteEndIndex + 1;

				continue;
			}

			while ((index < (end - 1)) &&
				   !Character.isWhitespace(html.charAt(index))) {

				if (index == (end - 2)) {
					return false;
				}

				index++;
			}
		}

		return true;
	}

	private static Markup _matchClosingTag(String html, int index) {
		String tagName = _getTagName(html, index + 2);

		if (tagName == null) {
			return null;
		}

		int end = index + 2 + tagName.length();

		while ((end < html.length()) &&
			   Character.isWhitespace(html.charAt(end))) {

			end++;
		}

		if ((end >= html.length()) ||
			(html.charAt(end) != CharPool.GREATER_THAN)) {

			return null;
		}

		return new Markup(
			end + 1, StringUtil.toLowerCase(tagName),
			HTMLInlineCodeToken.Type.CLOSING);
	}

	private static Markup _matchDelimited(
		String endMarker, String html, int index) {

		int end = html.indexOf(endMarker, index);

		if (end == -1) {
			return null;
		}

		return new Markup(
			end + endMarker.length(), null,
			HTMLInlineCodeToken.Type.PLACEHOLDER);
	}

	private static Markup _matchEntity(String html, int index) {
		int end = index + 1;

		if (end >= html.length()) {
			return null;
		}

		char c = html.charAt(end);

		if (c == CharPool.POUND) {
			end++;

			int radix = 10;

			if ((end < html.length()) &&
				((html.charAt(end) == 'X') || (html.charAt(end) == 'x'))) {

				end++;

				radix = 16;
			}

			end = _getDigitsEndIndex(html, end, radix);

			if (end == -1) {
				return null;
			}
		}
		else if (Character.isLetter(c)) {
			end++;

			while ((end < html.length()) &&
				   Character.isLetterOrDigit(html.charAt(end))) {

				end++;
			}

			int codePoint = _htmlCharacterEntities.lookupName(
				html.substring(index + 1, end));

			if (codePoint == -1) {
				return null;
			}
		}
		else {
			return null;
		}

		if ((end >= html.length()) ||
			(html.charAt(end) != CharPool.SEMICOLON)) {

			return null;
		}

		return new Markup(end + 1, null, HTMLInlineCodeToken.Type.PLACEHOLDER);
	}

	private static Markup _matchMarkup(String html, int index) {
		if (html.charAt(index) == CharPool.AMPERSAND) {
			return _matchEntity(html, index);
		}

		if (html.startsWith("<!--", index)) {
			return _matchDelimited("-->", html, index);
		}

		if (html.startsWith("<![CDATA[", index)) {
			return _matchDelimited("]]>", html, index);
		}

		if (html.startsWith("<!", index)) {
			return _matchDelimited(">", html, index);
		}

		if (html.startsWith("<?", index)) {
			return _matchDelimited("?>", html, index);
		}

		if (html.startsWith("</", index)) {
			return _matchClosingTag(html, index);
		}

		return _matchTag(html, index);
	}

	private static Markup _matchTag(String html, int index) {
		String tagName = _getTagName(html, index + 1);

		if (tagName == null) {
			return null;
		}

		int end = _getTagEndIndex(html, index + 1 + tagName.length());

		if (end == -1) {
			return null;
		}

		String lowerCaseTagName = StringUtil.toLowerCase(tagName);

		boolean selfClosing = _isSelfClosing(
			end, html, index + 1 + tagName.length());

		if (_rawTextElementNames.contains(lowerCaseTagName)) {
			int rawTextElementEnd = _getRawTextElementEndIndex(
				html, end, lowerCaseTagName);

			if (rawTextElementEnd != -1) {
				return new Markup(
					rawTextElementEnd, lowerCaseTagName,
					HTMLInlineCodeToken.Type.PLACEHOLDER);
			}

			if (!selfClosing) {
				return null;
			}
		}

		if (selfClosing || _voidElementNames.contains(lowerCaseTagName)) {
			return new Markup(
				end, lowerCaseTagName, HTMLInlineCodeToken.Type.PLACEHOLDER);
		}

		return new Markup(
			end, lowerCaseTagName, HTMLInlineCodeToken.Type.OPENING);
	}

	private static final HTMLCharacterEntities _htmlCharacterEntities =
		_createHTMLCharacterEntities();
	private static final Set<String> _rawTextElementNames = SetUtil.fromArray(
		"script", "style");
	private static final Set<String> _voidElementNames = SetUtil.fromArray(
		"area", "base", "br", "col", "embed", "hr", "img", "input", "link",
		"meta", "param", "source", "track", "wbr");

	private static class Markup {

		private Markup(int end, String tagName, HTMLInlineCodeToken.Type type) {
			_end = end;
			_tagName = tagName;
			_type = type;
		}

		private final int _end;
		private final String _tagName;
		private final HTMLInlineCodeToken.Type _type;

	}

}