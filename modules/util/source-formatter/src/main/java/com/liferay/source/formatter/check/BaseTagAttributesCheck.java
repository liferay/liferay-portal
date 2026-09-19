/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.source.formatter.check;

import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.NaturalOrderStringComparator;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.source.formatter.check.util.SourceUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Hugo Huijser
 */
public abstract class BaseTagAttributesCheck extends BaseFileCheck {

	protected Tag doFormatLineBreaks(Tag tag, String absolutePath) {
		return tag;
	}

	protected String formatIncorrectLineBreak(String fileName, String content) {
		Matcher matcher = _incorrectLineBreakPattern.matcher(content);

		while (matcher.find()) {
			String match = StringUtil.trimLeading(matcher.group());

			if (fileName.endsWith(".svg") && match.startsWith("<image")) {
				continue;
			}

			String s = SourceUtil.stripQuotes(matcher.group(3));

			if (s.contains(">")) {
				continue;
			}

			if (getLevel(match, "<", ">") != 0) {
				addMessage(
					fileName,
					"There should be a line break after \"" + matcher.group(2) +
						"\"",
					getLineNumber(content, matcher.start(2)));

				continue;
			}

			return StringUtil.replaceFirst(
				content, StringPool.SPACE, "\n\t" + matcher.group(1),
				matcher.start());
		}

		return content;
	}

	protected Tag formatLineBreaks(
		Tag tag, String absolutePath, boolean forceSingleLine) {

		if (forceSingleLine) {
			tag.setMultiLine(false);

			return tag;
		}

		Map<String, String> attributesMap = tag.getAttributesMap();

		for (Map.Entry<String, String> entry : attributesMap.entrySet()) {
			String attributeValue = entry.getValue();

			if (attributeValue.contains(StringPool.NEW_LINE)) {
				tag.setMultiLine(true);

				return tag;
			}
		}

		return doFormatLineBreaks(tag, absolutePath);
	}

	protected String formatMultiLinesTagAttributes(
			String absolutePath, String content, boolean escapeQuotes)
		throws Exception {

		Matcher matcher = _multilineTagPattern.matcher(content);

		while (matcher.find()) {
			if (matcher.start() != 0) {
				char c = content.charAt(matcher.start() - 1);

				if (c != CharPool.NEW_LINE) {
					continue;
				}
			}

			String tag = matcher.group(1);

			String lastLine = StringUtil.trim(
				getLine(content, getLineNumber(content, matcher.end(1))));

			if (lastLine.matches("></[-\\w:]+>")) {
				String newTag = StringUtil.replaceLast(tag, lastLine, "/>");

				return StringUtil.replace(content, tag, newTag);
			}

			if (getLevel(_getStrippedTag(tag, "\"", "'"), "<", ">") != 0) {
				continue;
			}

			String beforeClosingTagChar = matcher.group(3);

			if (!beforeClosingTagChar.equals(StringPool.NEW_LINE) &&
				!beforeClosingTagChar.equals(StringPool.TAB)) {

				String closingTag = matcher.group(4);

				String whitespace = matcher.group(2);

				String indent = StringUtil.removeChar(
					whitespace, CharPool.SPACE);

				return StringUtil.replaceFirst(
					content, closingTag, "\n" + indent + closingTag,
					matcher.start(3));
			}

			String newTag = formatTagAttributes(
				absolutePath, tag, escapeQuotes, false);

			if (!tag.equals(newTag)) {
				return StringUtil.replace(content, tag, newTag);
			}
		}

		return content;
	}

	protected String formatTagAttributes(
			String absolutePath, String s, boolean escapeQuotes,
			boolean forceSingleLine)
		throws Exception {

		Tag tag = parseTag(s, escapeQuotes);

		if (tag == null) {
			return s;
		}

		tag = formatTagAttributeType(absolutePath, tag);

		tag = sortHTMLTagAttributes(tag);

		if (isPortalSource() || isSubrepository()) {
			tag = formatLineBreaks(tag, absolutePath, forceSingleLine);
		}

		return tag.toString();
	}

	protected Tag formatTagAttributeType(String absolutePath, Tag tag)
		throws Exception {

		return tag;
	}

	protected List<String> getJSPTags(String line) {
		List<String> jspTags = new ArrayList<>();

		Matcher matcher = _jspTaglibPattern.matcher(line);

		while (matcher.find()) {
			String tag = getTag(line, matcher.start());

			if (tag == null) {
				return jspTags;
			}

			jspTags.add(tag);
		}

		return jspTags;
	}

	protected String getTag(String s, int fromIndex) {
		int x = fromIndex;

		while (true) {
			x = s.indexOf(">", x + 1);

			if (x == -1) {
				return null;
			}

			String part = s.substring(fromIndex, x + 1);

			if (getLevel(part, "<", ">") == 0) {
				return part;
			}
		}
	}

	protected Tag parseTag(String s, boolean escapeQuotes) {
		String indent = SourceUtil.getIndent(s);

		s = StringUtil.trim(s);

		boolean multiLine = false;

		int x = -1;

		if (s.contains(StringPool.NEW_LINE)) {
			multiLine = true;

			x = s.indexOf(CharPool.NEW_LINE);
		}
		else {
			x = s.indexOf(CharPool.SPACE);
		}

		if (x == -1) {
			return null;
		}

		String tagName = s.substring(1, x);

		Tag tag = new Tag(tagName, indent, multiLine, escapeQuotes);

		s = s.substring(x + 1);

		if (s.equals(">") || s.equals("/>") ||
			(tagName.matches("[-\\w:]+") &&
			 s.matches(">\\s*</" + tagName + "\\s*>"))) {

			tag.setClosingTag(
				StringUtil.removeChars(
					s, CharPool.NEW_LINE, CharPool.SPACE, CharPool.TAB));

			return tag;
		}

		boolean withoutValueAttribute = false;

		while (true) {
			x = s.indexOf(CharPool.EQUAL);

			if (x == -1) {
				x = s.indexOf(CharPool.SPACE);

				if (x == -1) {
					return null;
				}

				withoutValueAttribute = true;
			}

			String attributeName = StringUtil.trim(s.substring(0, x));

			if (!_isValidAttributeName(attributeName)) {
				return null;
			}

			s = StringUtil.trimLeading(s.substring(x + 1));

			if (withoutValueAttribute) {
				tag.putAttribute(attributeName, "_WITHOUT_VALUE_");

				if (s.equals(">") || s.equals("/>") ||
					(tagName.matches("[-\\w:]+") &&
					 s.matches(">\\s*</" + tagName + "\\s*>"))) {

					tag.setClosingTag(
						StringUtil.removeChars(
							s, CharPool.NEW_LINE, CharPool.SPACE,
							CharPool.TAB));

					return tag;
				}
			}

			char delimiter = s.charAt(0);

			if ((delimiter != CharPool.APOSTROPHE) &&
				(delimiter != CharPool.QUOTE)) {

				return null;
			}

			s = s.substring(1);

			x = -1;

			while (true) {
				x = s.indexOf(delimiter, x + 1);

				if (x == -1) {
					return null;
				}

				String attributeValue = s.substring(0, x);

				if (attributeName.equals("class")) {
					attributeValue = StringUtil.trim(attributeValue);
				}

				if ((attributeValue.startsWith("<%") &&
					 (getLevel(attributeValue, "<%", "%>") == 0)) ||
					(!attributeValue.startsWith("<%") &&
					 (getLevel(attributeValue, "<", ">") == 0))) {

					tag.putAttribute(attributeName, attributeValue);

					s = StringUtil.trim(s.substring(x + 1));

					if (s.equals(">") || s.equals("/>") ||
						(tagName.matches("[-\\w:]+") &&
						 s.matches(">\\s*</" + tagName + "\\s*>"))) {

						tag.setClosingTag(
							StringUtil.removeChars(
								s, CharPool.NEW_LINE, CharPool.SPACE,
								CharPool.TAB));

						return tag;
					}

					break;
				}
			}
		}
	}

	protected Tag sortHTMLTagAttributes(Tag tag) {
		String tagFullName = tag.getFullName();

		if (tagFullName.equals("liferay-ui:tabs")) {
			return tag;
		}

		Map<String, String> attributesMap = tag.getAttributesMap();

		for (Map.Entry<String, String> entry : attributesMap.entrySet()) {
			String attributeName = entry.getKey();

			if (attributeName.startsWith("v-") ||
				(tagFullName.equals("svg") &&
				 attributeName.equals("viewBox"))) {

				continue;
			}

			String attributeValue = entry.getValue();

			if (attributeValue.matches("([-a-z0-9]+ )+[-a-z0-9]+")) {
				List<String> htmlAttributes = ListUtil.fromArray(
					StringUtil.split(attributeValue, StringPool.SPACE));

				Collections.sort(htmlAttributes);

				tag.putAttribute(
					attributeName,
					StringUtil.merge(htmlAttributes, StringPool.SPACE));
			}
			else if (attributeValue.matches("([-a-z0-9]+,)+[-a-z0-9]+")) {
				if (!tagFullName.equals("aui:script") ||
					!attributeName.equals("use")) {

					continue;
				}

				List<String> htmlAttributes = ListUtil.fromArray(
					StringUtil.split(attributeValue, StringPool.COMMA));

				Collections.sort(htmlAttributes);

				tag.putAttribute(
					attributeName,
					StringUtil.merge(htmlAttributes, StringPool.COMMA));
			}
		}

		return tag;
	}

	protected class Tag {

		public Tag(
			String fullName, String indent, boolean multiLine,
			boolean escapeQuotes) {

			_fullName = fullName;
			_indent = indent;
			_multiLine = multiLine;
			_escapeQuotes = escapeQuotes;

			int x = _fullName.indexOf(":");

			if (x != -1) {
				_name = _fullName.substring(x + 1);
				_taglibName = _fullName.substring(0, x);
			}
			else {
				_name = _fullName;
				_taglibName = null;
			}
		}

		public Map<String, String> getAttributesMap() {
			return _attributesMap;
		}

		public String getFullName() {
			return _fullName;
		}

		public String getName() {
			return _name;
		}

		public String getTaglibName() {
			return _taglibName;
		}

		public void putAttribute(String attributeName, String attributeValue) {
			_attributesMap.put(attributeName, attributeValue);
		}

		public void setClosingTag(String closingTag) {
			_closingTag = closingTag;
		}

		public void setMultiLine(boolean multiLine) {
			_multiLine = multiLine;
		}

		@Override
		public String toString() {
			StringBundler sb = new StringBundler();

			sb.append(_indent);
			sb.append(StringPool.LESS_THAN);
			sb.append(_fullName);

			for (Map.Entry<String, String> entry : _attributesMap.entrySet()) {
				if (_multiLine) {
					sb.append(StringPool.NEW_LINE);
					sb.append(_indent);
					sb.append(StringPool.TAB);
				}
				else {
					sb.append(StringPool.SPACE);
				}

				sb.append(entry.getKey());

				String attributeValue = entry.getValue();

				if (attributeValue.equals("_WITHOUT_VALUE_")) {
					continue;
				}

				sb.append(StringPool.EQUAL);

				String delimiter = null;

				if (_escapeQuotes ||
					!attributeValue.contains(StringPool.QUOTE) ||
					!_fullName.contains(StringPool.COLON)) {

					delimiter = StringPool.QUOTE;
				}
				else {
					delimiter = StringPool.APOSTROPHE;
				}

				sb.append(delimiter);

				if (!_escapeQuotes) {
					sb.append(attributeValue);
				}
				else {
					sb.append(
						StringUtil.replace(
							attributeValue, CharPool.QUOTE, "&quot;"));
				}

				sb.append(delimiter);
			}

			if (_multiLine) {
				sb.append(StringPool.NEW_LINE);
				sb.append(_indent);
			}
			else if (_closingTag.equals("/>")) {
				sb.append(StringPool.SPACE);
			}

			sb.append(_closingTag);

			return sb.toString();
		}

		private Map<String, String> _attributesMap = new TreeMap<>(
			new NaturalOrderStringComparator());
		private String _closingTag;
		private final boolean _escapeQuotes;
		private final String _fullName;
		private final String _indent;
		private boolean _multiLine;
		private final String _name;
		private final String _taglibName;

	}

	private String _getStrippedTag(String tag, String... quotes) {
		for (String quote : quotes) {
			while (true) {
				int x = tag.indexOf(quote + "<%=");

				if (x == -1) {
					break;
				}

				int y = tag.indexOf("%>" + quote, x);

				if (y == -1) {
					return tag;
				}

				tag = tag.substring(0, x) + tag.substring(y + 3);
			}
		}

		return tag;
	}

	private boolean _isValidAttributeName(String attributeName) {
		if (Validator.isNull(attributeName)) {
			return false;
		}

		Matcher matcher = _attributeNamePattern.matcher(attributeName);

		return matcher.matches();
	}

	private static final Pattern _attributeNamePattern = Pattern.compile(
		"[a-zA-Z][-\\.:\\w]*");
	private static final Pattern _incorrectLineBreakPattern = Pattern.compile(
		"\n(\t*)(<\\w[-_:\\w]*) (.*)([\"']|%=)\n[\\s\\S]*?>\n");
	private static final Pattern _jspTaglibPattern = Pattern.compile(
		"\t*<[-\\w]+:[-\\w]+ .");
	private static final Pattern _multilineTagPattern = Pattern.compile(
		"(([ \t]*)<[-\\w:]+\n.*?([^%])(/?>))(\n|$)", Pattern.DOTALL);

}