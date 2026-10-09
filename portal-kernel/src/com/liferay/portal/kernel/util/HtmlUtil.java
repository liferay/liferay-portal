/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.util;

import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;

import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Provides utility methods for escaping, replacing, and stripping
 * HTML text. This class uses XSS recommendations from <a
 * href="http://www.owasp.org/index.php/Cross_Site_Scripting#How_to_Protect_Yourself">http://www.owasp.org/index.php/Cross_Site_Scripting#How_to_Protect_Yourself</a>
 * when escaping HTML text.
 *
 * @author Brian Wing Shun Chan
 * @author Clarence Shen
 * @author Harry Mark
 * @author Samuel Kong
 * @author Connor McKay
 * @author Shuyang Zhou
 */
public class HtmlUtil {

	/**
	 * Generates a string with the data-* attributes generated from the keys and
	 * values of a map. For example, a map containing
	 * <code>{key1=value1;key2=value2}</code> is returned as the string
	 * <code>data-key1=value1 data-key2=value2</code>.
	 *
	 * @param  data the map of values to convert to data-* attributes
	 * @return a string with the data attributes, or <code>null</code> if the
	 *         map is <code>null</code>
	 */
	public static String buildData(Map<String, Object> data) {
		if ((data == null) || data.isEmpty()) {
			return StringPool.BLANK;
		}

		StringBundler sb = new StringBundler(data.size() * 5);

		for (Map.Entry<String, Object> entry : data.entrySet()) {
			sb.append("data-");
			sb.append(entry.getKey());
			sb.append("=\"");
			sb.append(escapeAttribute(String.valueOf(entry.getValue())));
			sb.append("\" ");
		}

		return sb.toString();
	}

	/**
	 * Escapes the text so that it is safe to use in an HTML context.
	 *
	 * @param  text the text to escape
	 * @return the escaped HTML text, or <code>null</code> if the text is
	 *         <code>null</code>
	 */
	public static String escape(String text) {
		if (text == null) {
			return null;
		}

		if (text.length() == 0) {
			return StringPool.BLANK;
		}

		// Escape using XSS recommendations from
		// http://www.owasp.org/index.php/Cross_Site_Scripting
		// #How_to_Protect_Yourself

		StringBundler sb = null;

		int lastReplacementIndex = 0;

		for (int i = 0; i < text.length(); i++) {
			char c = text.charAt(i);

			if ((c < 256) && ((c >= 128) || _VALID_CHARS[c])) {
				continue;
			}

			String replacement = null;

			if (c == '<') {
				replacement = "&lt;";
			}
			else if (c == '>') {
				replacement = "&gt;";
			}
			else if (c == '&') {
				replacement = "&amp;";
			}
			else if (c == '"') {
				replacement = "&#34;";
			}
			else if (c == '\'') {
				replacement = "&#39;";
			}
			else if (c == '\u00bb') {
				replacement = "&#187;";
			}
			else if (c == '\u2013') {
				replacement = "&#8211;";
			}
			else if (c == '\u2014') {
				replacement = "&#8212;";
			}
			else if (c == '\u2028') {
				replacement = "&#8232;";
			}
			else if (!_isValidXmlCharacter(c) ||
					 _isUnicodeCompatibilityCharacter(c)) {

				replacement = StringPool.SPACE;
			}
			else {
				continue;
			}

			if (sb == null) {
				sb = new StringBundler();
			}

			if (i > lastReplacementIndex) {
				sb.append(text.substring(lastReplacementIndex, i));
			}

			sb.append(replacement);

			lastReplacementIndex = i + 1;
		}

		if (sb == null) {
			return text;
		}

		if (lastReplacementIndex < text.length()) {
			sb.append(text.substring(lastReplacementIndex));
		}

		return sb.toString();
	}

	/**
	 * Escapes the attribute value so that it is safe to use as an attribute
	 * value.
	 *
	 * @param  attribute the attribute to escape
	 * @return the escaped attribute value, or <code>null</code> if the
	 *         attribute value is <code>null</code>
	 */
	public static String escapeAttribute(String attribute) {
		if (attribute == null) {
			return null;
		}

		if (attribute.length() == 0) {
			return StringPool.BLANK;
		}

		StringBuilder sb = null;
		int lastReplacementIndex = 0;

		for (int i = 0; i < attribute.length(); i++) {
			char c = attribute.charAt(i);

			if (c < _ATTRIBUTE_ESCAPES.length) {
				String replacement = _ATTRIBUTE_ESCAPES[c];

				if (replacement == null) {
					continue;
				}

				if (sb == null) {
					sb = new StringBuilder(attribute.length() + 64);
				}

				if (i > lastReplacementIndex) {
					sb.append(attribute, lastReplacementIndex, i);
				}

				sb.append(replacement);

				lastReplacementIndex = i + 1;
			}
			else if (!_isValidXmlCharacter(c) ||
					 _isUnicodeCompatibilityCharacter(c)) {

				if (sb == null) {
					sb = new StringBuilder(attribute.length() + 64);
				}

				if (i > lastReplacementIndex) {
					sb.append(attribute, lastReplacementIndex, i);
				}

				sb.append(CharPool.SPACE);

				lastReplacementIndex = i + 1;
			}
		}

		if (sb == null) {
			return attribute;
		}

		if (lastReplacementIndex < attribute.length()) {
			sb.append(attribute, lastReplacementIndex, attribute.length());
		}

		return sb.toString();
	}

	/**
	 * Escapes the CSS value so that it is safe to use in a CSS context.
	 *
	 * @param  css the CSS value to escape
	 * @return the escaped CSS value, or <code>null</code> if the CSS value is
	 *         <code>null</code>
	 */
	public static String escapeCSS(String css) {
		if (css == null) {
			return null;
		}

		if (css.length() == 0) {
			return StringPool.BLANK;
		}

		String prefix = StringPool.BACK_SLASH;

		StringBuilder sb = null;
		char[] hexBuffer = new char[4];
		int lastReplacementIndex = 0;

		for (int i = 0; i < css.length(); i++) {
			char c = css.charAt(i);

			if ((c >= _VALID_CHARS.length) || _VALID_CHARS[c]) {
				continue;
			}

			if (sb == null) {
				sb = new StringBuilder(css.length() + 64);
			}

			if (i > lastReplacementIndex) {
				sb.append(css, lastReplacementIndex, i);
			}

			sb.append(prefix);

			_appendHexChars(sb, hexBuffer, c);

			if (i < (css.length() - 1)) {
				char nextChar = css.charAt(i + 1);

				if ((nextChar >= CharPool.NUMBER_0) &&
					(nextChar <= CharPool.NUMBER_9)) {

					sb.append(CharPool.SPACE);
				}
			}

			lastReplacementIndex = i + 1;
		}

		if (sb == null) {
			return css;
		}

		if (lastReplacementIndex < css.length()) {
			sb.append(css, lastReplacementIndex, css.length());
		}

		return sb.toString();
	}

	/**
	 * Escapes the HREF attribute so that it is safe to use as an HREF
	 * attribute.
	 *
	 * @param  href the HREF attribute to escape
	 * @return the escaped HREF attribute, or <code>null</code> if the HREF
	 *         attribute is <code>null</code>
	 */
	public static String escapeHREF(String href) {
		if (href == null) {
			return null;
		}

		if (href.length() == 0) {
			return StringPool.BLANK;
		}

		char c = href.charAt(0);

		if ((c == CharPool.BACK_SLASH) || (c == CharPool.SLASH)) {
			return escapeAttribute(href);
		}

		c = Character.toLowerCase(c);

		if ((c >= CharPool.LOWER_CASE_A) && (c <= CharPool.LOWER_CASE_Z) &&
			(c != CharPool.LOWER_CASE_D) && (c != CharPool.LOWER_CASE_J)) {

			return escapeAttribute(href);
		}

		int index = href.indexOf(StringPool.COLON);

		if (index > -1) {
			href = StringUtil.replaceFirst(
				href, StringPool.COLON, "%3a", index);
		}

		return escapeAttribute(href);
	}

	/**
	 * Escapes the JavaScript value so that it is safe to use in a JavaScript
	 * context.
	 *
	 * @param  js the JavaScript value to escape
	 * @return the escaped JavaScript value, or <code>null</code> if the
	 *         JavaScript value is <code>null</code>
	 */
	public static String escapeJS(String js) {
		if (js == null) {
			return null;
		}

		if (js.length() == 0) {
			return StringPool.BLANK;
		}

		String prefix = "\\x";

		StringBuilder sb = null;
		char[] hexBuffer = new char[4];
		int lastReplacementIndex = 0;

		for (int i = 0; i < js.length(); i++) {
			char c = js.charAt(i);

			if (c < _VALID_CHARS.length) {
				if (!_VALID_CHARS[c]) {
					if (sb == null) {
						sb = new StringBuilder(js.length() + 64);
					}

					if (i > lastReplacementIndex) {
						sb.append(js, lastReplacementIndex, i);
					}

					sb.append(prefix);

					_appendHexChars(sb, hexBuffer, c);

					lastReplacementIndex = i + 1;
				}
			}
			else if ((c == '\u2028') || (c == '\u2029')) {
				if (sb == null) {
					sb = new StringBuilder(js.length() + 64);
				}

				if (i > lastReplacementIndex) {
					sb.append(js, lastReplacementIndex, i);
				}

				sb.append("\\u");

				_appendHexChars(sb, hexBuffer, c);

				lastReplacementIndex = i + 1;
			}
		}

		if (sb == null) {
			return js;
		}

		if (lastReplacementIndex < js.length()) {
			sb.append(js, lastReplacementIndex, js.length());
		}

		return sb.toString();
	}

	public static String escapeJSLink(String link) {
		if (Validator.isNull(link)) {
			return StringPool.BLANK;
		}

		link = StringUtil.trim(link);

		int index = link.indexOf(StringPool.COLON);

		if (index < 10) {
			return link;
		}

		String protocol = StringUtil.toLowerCase(
			link.substring(index - 10, index));

		if (!Objects.equals(protocol, "javascript")) {
			return link;
		}

		String part1 = link.substring(0, index);
		String part2 = StringUtil.replaceFirst(
			link.substring(index), CharPool.COLON, "%3a");

		return part1 + part2;
	}

	/**
	 * Escapes the URL value so that it is safe to use as a URL.
	 *
	 * @param  url the URL value to escape
	 * @return the escaped URL value, or <code>null</code> if the URL value is
	 *         <code>null</code>
	 */
	public static String escapeURL(String url) {
		return URLCodec.encodeURL(url, true);
	}

	public static String escapeXPath(String xPath) {
		if (Validator.isNull(xPath)) {
			return xPath;
		}

		StringBundler sb = new StringBundler(xPath.length());

		for (int i = 0; i < xPath.length(); i++) {
			char c = xPath.charAt(i);

			boolean hasToken = false;

			for (char xPathToken : _XPATH_TOKENS) {
				if (c == xPathToken) {
					hasToken = true;

					break;
				}
			}

			if (hasToken) {
				sb.append(CharPool.UNDERLINE);
			}
			else {
				sb.append(c);
			}
		}

		return sb.toString();
	}

	public static String escapeXPathAttribute(String xPathAttribute) {
		boolean hasApostrophe = xPathAttribute.contains(StringPool.APOSTROPHE);
		boolean hasQuote = xPathAttribute.contains(StringPool.QUOTE);

		if (hasQuote && hasApostrophe) {
			String[] parts = xPathAttribute.split(StringPool.APOSTROPHE);

			return StringBundler.concat(
				"concat('", StringUtil.merge(parts, "', \"'\", '"), "')");
		}

		if (hasQuote) {
			return StringBundler.concat(
				StringPool.APOSTROPHE, xPathAttribute, StringPool.APOSTROPHE);
		}

		return StringBundler.concat(
			StringPool.QUOTE, xPathAttribute, StringPool.QUOTE);
	}

	public static String fromInputSafe(String text) {
		return StringUtil.replace(
			text, new String[] {"&amp;", "&quot;"}, new String[] {"&", "\""});
	}

	public static String getAUICompatibleId(String html) {
		if (Validator.isNull(html)) {
			return html;
		}

		StringBundler sb = null;

		int lastReplacementIndex = 0;

		for (int i = 0; i < html.length(); i++) {
			char c = html.charAt(i);

			if (((c <= 127) && (Validator.isChar(c) || Validator.isDigit(c))) ||
				((c > 127) && (c != CharPool.FIGURE_SPACE) &&
				 (c != CharPool.NARROW_NO_BREAK_SPACE) &&
				 (c != CharPool.NO_BREAK_SPACE))) {

				continue;
			}

			if (sb == null) {
				sb = new StringBundler();
			}

			if (i > lastReplacementIndex) {
				sb.append(html.substring(lastReplacementIndex, i));
			}

			sb.append(StringPool.UNDERLINE);

			if (c != CharPool.UNDERLINE) {
				sb.append(StringUtil.toHexString(c));
			}

			sb.append(StringPool.UNDERLINE);

			lastReplacementIndex = i + 1;
		}

		if (sb == null) {
			return html;
		}

		if (lastReplacementIndex < html.length()) {
			sb.append(html.substring(lastReplacementIndex));
		}

		return sb.toString();
	}

	/**
	 * Replaces all new lines or carriage returns with the <code><br /></code>
	 * HTML tag.
	 *
	 * @param  html the text
	 * @return the converted text, or <code>null</code> if the HTML text is
	 *         <code>null</code>
	 */
	public static String replaceNewLine(String html) {
		if (html == null) {
			return null;
		}

		html = StringUtil.replace(html, StringPool.RETURN_NEW_LINE, "<br />");

		return StringUtil.replace(html, CharPool.NEW_LINE, "<br />");
	}

	/**
	 * Strips all content delimited by the tag out of the text.
	 *
	 * <p>
	 * If the tag appears multiple times, all occurrences (including the tag)
	 * are stripped. The tag may have attributes. In order for this method to
	 * recognize the tag, it must consist of a separate opening and closing tag.
	 * Self-closing tags remain in the result.
	 * </p>
	 *
	 * @param  text the text
	 * @param  tag the tag used for delimiting, which should only be the tag's
	 *         name (e.g. no &lt;)
	 * @return the text, without the stripped tag and its contents, or
	 *         <code>null</code> if the text is <code>null</code>
	 */
	public static String stripBetween(String text, String tag) {
		return StringUtil.stripBetween(text, "<" + tag, "</" + tag + ">");
	}

	/**
	 * Strips all XML comments out of the text.
	 *
	 * @param  text the text
	 * @return the text, without the stripped XML comments, or <code>null</code>
	 *         if the text is <code>null</code>
	 */
	public static String stripComments(String text) {
		return StringUtil.stripBetween(text, "<!--", "-->");
	}

	public static String stripHtml(String text) {
		if (text == null) {
			return null;
		}

		text = stripComments(text);

		StringBundler sb = new StringBundler(text.length());

		int x = 0;
		int y = text.indexOf("<");

		while (y != -1) {
			sb.append(text.substring(x, y));

			// Look for text enclosed by <abc></abc>

			if (_isTag(_TAG_NOSCRIPT, text, y + 1)) {
				y = _stripTag(_TAG_NOSCRIPT, text, y);
			}
			else if (_isTag(_TAG_SCRIPT, text, y + 1)) {
				y = _stripTag(_TAG_SCRIPT, text, y);
			}
			else if (_isTag(_TAG_STYLE, text, y + 1)) {
				y = _stripTag(_TAG_STYLE, text, y);
			}

			x = text.indexOf(">", y);

			if (x == -1) {
				break;
			}

			x++;

			if (x < y) {

				// <b>Hello</b

				break;
			}

			y = text.indexOf("<", x);
		}

		if (y == -1) {
			sb.append(text.substring(x));
		}

		return sb.toString();
	}

	/**
	 * Encodes the text so that it's safe to use as an HTML input field value.
	 *
	 * <p>
	 * For example, the <code>&</code> character is replaced by
	 * <code>&amp;amp;</code>.
	 * </p>
	 *
	 * @param  text the text
	 * @return the encoded text that is safe to use as an HTML input field
	 *         value, or <code>null</code> if the text is <code>null</code>
	 */
	public static String toInputSafe(String text) {
		return StringUtil.replace(
			text, new char[] {'&', '\"'}, new String[] {"&amp;", "&quot;"});
	}

	public static String unescape(String text) {
		return StringUtil.replace(text, "&", ";", _unescapeMap);
	}

	public static String unescapeCDATA(String text) {
		if (text == null) {
			return null;
		}

		if (text.length() == 0) {
			return StringPool.BLANK;
		}

		text = StringUtil.replace(text, "&lt;![CDATA[", "<![CDATA[");
		text = StringUtil.replace(text, "]]&gt;", "]]>");

		return text;
	}

	public static String wordBreak(String text, int columns) {
		StringBundler sb = new StringBundler();

		int length = 0;
		int lastWrite = 0;
		int pos = 0;

		Matcher matcher = _pattern.matcher(text);

		while (matcher.find()) {
			if (matcher.start() < pos) {
				continue;
			}

			while ((length + matcher.start() - pos) >= columns) {
				pos += columns - length;

				sb.append(text.substring(lastWrite, pos));
				sb.append("<wbr/>&shy;");

				length = 0;
				lastWrite = pos;
			}

			length += matcher.start() - pos;

			String group = matcher.group();

			if (group.equals(StringPool.AMPERSAND)) {
				int x = text.indexOf(StringPool.SEMICOLON, matcher.start());

				if (x != -1) {
					length++;
					pos = x + 1;
				}

				continue;
			}

			if (group.equals(StringPool.LESS_THAN)) {
				int x = text.indexOf(StringPool.GREATER_THAN, matcher.start());

				if (x != -1) {
					pos = x + 1;
				}

				continue;
			}

			if (!group.equals(StringPool.SPACE) &&
				!group.equals(StringPool.NEW_LINE)) {

				continue;
			}

			length = 0;
			pos = matcher.start() + 1;
		}

		sb.append(text.substring(lastWrite));

		return sb.toString();
	}

	private static void _appendHexChars(
		StringBuilder sb, char[] buffer, char c) {

		int index = buffer.length;

		do {
			buffer[--index] = _HEX_DIGITS[c & 15];

			c >>>= 4;
		}
		while (c != 0);

		if (index == (buffer.length - 1)) {
			sb.append(CharPool.NUMBER_0);
			sb.append(buffer[index]);

			return;
		}

		sb.append(buffer, index, buffer.length - index);
	}

	private static boolean _isTag(char[] tag, String text, int pos) {
		if ((pos + tag.length + 1) <= text.length()) {
			char item = '\0';

			for (char c : tag) {
				item = text.charAt(pos++);

				if (Character.toLowerCase(item) != c) {
					return false;
				}
			}

			item = text.charAt(pos);

			// Check that char after tag is not a letter (i.e. another tag)

			return !Character.isLetter(item);
		}

		return false;
	}

	private static boolean _isUnicodeCompatibilityCharacter(char c) {
		if (((c >= '\u007f') && (c <= '\u0084')) ||
			((c >= '\u0086') && (c <= '\u009f')) ||
			((c >= '\ufdd0') && (c <= '\ufdef'))) {

			return true;
		}

		return false;
	}

	private static boolean _isValidXmlCharacter(char c) {
		if (((c >= CharPool.SPACE) && (c <= '\ud7ff')) ||
			((c >= '\ue000') && (c <= '\ufffd')) || Character.isSurrogate(c) ||
			(c == CharPool.TAB) || (c == CharPool.NEW_LINE) ||
			(c == CharPool.RETURN)) {

			return true;
		}

		return false;
	}

	private static int _stripTag(char[] tag, String text, int pos) {
		int x = pos + tag.length;

		// Find end of the tag

		x = text.indexOf(">", x);

		if (x < 0) {
			return pos;
		}

		// Check if preceding character is / (i.e. is this instance of <abc/>)

		if (text.charAt(x - 1) == '/') {
			return pos;
		}

		// Search for the ending </abc> tag

		while (true) {
			x = text.indexOf("</", x);

			if (x < 0) {
				break;
			}

			if (_isTag(tag, text, x + 2)) {
				pos = x;

				break;
			}

			// Skip past "</"

			x += 2;
		}

		return pos;
	}

	private static final String[] _ATTRIBUTE_ESCAPES = new String[256];

	private static final char[] _HEX_DIGITS = {
		'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd',
		'e', 'f'
	};

	private static final char[] _TAG_NOSCRIPT = {
		'n', 'o', 's', 'c', 'r', 'i', 'p', 't'
	};

	private static final char[] _TAG_SCRIPT = {'s', 'c', 'r', 'i', 'p', 't'};

	private static final char[] _TAG_STYLE = {'s', 't', 'y', 'l', 'e'};

	// See http://www.w3.org/TR/xpath20/#lexical-structure

	private static final boolean[] _VALID_CHARS = new boolean[256];

	private static final char[] _XPATH_TOKENS = {
		'(', ')', '[', ']', '.', '@', ',', ':', '/', '|', '+', '-', '=', '!',
		'<', '>', '*', '$', '"', '"', ' ', 9, 10, 13, 133, 8232
	};

	private static final Pattern _pattern = Pattern.compile("([\\s<&]|$)");
	private static final Map<String, String> _unescapeMap = HashMapBuilder.put(
		"#34", "\""
	).put(
		"#35", "#"
	).put(
		"#37", "%"
	).put(
		"#39", "'"
	).put(
		"#40", "("
	).put(
		"#41", ")"
	).put(
		"#43", "+"
	).put(
		"#44", ","
	).put(
		"#45", "-"
	).put(
		"#59", ";"
	).put(
		"#61", "="
	).put(
		"#8211", "\u2013"
	).put(
		"#8212", "\u2014"
	).put(
		"amp", "&"
	).put(
		"gt", ">"
	).put(
		"lt", "<"
	).put(
		"nbsp", " "
	).put(
		"rsquo", "\u2019"
	).build();

	static {
		for (int i = 0; i < 256; i++) {
			char c = (char)i;

			if (!_isValidXmlCharacter(c)) {
				_ATTRIBUTE_ESCAPES[i] = StringPool.SPACE;
			}

			_ATTRIBUTE_ESCAPES[CharPool.AMPERSAND] =
				StringPool.AMPERSAND_ENCODED;
			_ATTRIBUTE_ESCAPES[CharPool.APOSTROPHE] = "&#39;";
			_ATTRIBUTE_ESCAPES[CharPool.GREATER_THAN] = "&gt;";
			_ATTRIBUTE_ESCAPES[CharPool.LESS_THAN] = "&lt;";
			_ATTRIBUTE_ESCAPES[CharPool.QUOTE] = "&quot;";

			if (!Character.isLetterOrDigit(c)) {
				continue;
			}

			_VALID_CHARS[i] = true;
		}

		_VALID_CHARS['-'] = true;
		_VALID_CHARS['_'] = true;
	}

}