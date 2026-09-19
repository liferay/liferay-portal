/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.message.boards.parser.bbcode.internal;

import com.liferay.message.boards.util.MBUtil;
import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.ThemeConstants;
import com.liferay.portal.kernel.parsers.bbcode.BBCodeTranslator;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.HtmlUtil;
import com.liferay.portal.kernel.util.IntegerWrapper;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.osgi.service.component.annotations.Component;

/**
 * @author Iliyan Peychev
 */
@Component(service = BBCodeTranslator.class)
public class HtmlBBCodeTranslatorImpl implements BBCodeTranslator {

	public HtmlBBCodeTranslatorImpl() {
		_bbCodeCharacters = HashMapBuilder.put(
			"&", "&amp;"
		).put(
			"'", "&#039;"
		).put(
			"(", "&#40;"
		).put(
			")", "&#41;"
		).put(
			"/", "&#047;"
		).put(
			"<", "&lt;"
		).put(
			">", "&gt;"
		).put(
			"[", "&#91;"
		).put(
			"\"", "&#034;"
		).put(
			"]", "&#93;"
		).put(
			"`", "&#096;"
		).build();

		for (int i = 0; i < _EMOTICONS.length; i++) {
			String[] emoticon = _EMOTICONS[i];

			_emoticonDescriptions[i] = emoticon[2];
			_emoticonFiles[i] = emoticon[0];
			_emoticonSymbols[i] = emoticon[1];

			String image = emoticon[0];

			emoticon[0] = StringBundler.concat(
				"<img alt=\"emoticon\" src=\"",
				ThemeConstants.TOKEN_THEME_IMAGES_PATH, MBUtil.EMOTICONS, "/",
				image, "\" >");
		}

		_excludeNewLineTypes = HashMapBuilder.put(
			"*", BBCodeParser.TYPE_TAG_START_END
		).put(
			"li", BBCodeParser.TYPE_TAG_START_END
		).put(
			"table", BBCodeParser.TYPE_TAG_END
		).put(
			"td", BBCodeParser.TYPE_TAG_START_END
		).put(
			"th", BBCodeParser.TYPE_TAG_START_END
		).put(
			"tr", BBCodeParser.TYPE_TAG_START_END
		).build();

		_imageAttributes = new HashSet<>(
			Arrays.asList(
				"alt", "class", "dir", "height", "id", "lang", "longdesc",
				"style", "title", "width"));

		_orderedListStyles = HashMapBuilder.put(
			"1", "list-style: decimal outside;"
		).put(
			"a", "list-style: lower-alpha outside;"
		).put(
			"A", "list-style: upper-alpha outside;"
		).put(
			"i", "list-style: lower-roman outside;"
		).put(
			"I", "list-style: upper-roman outside;"
		).build();

		_unorderedListStyles = HashMapBuilder.put(
			"circle", "list-style: circle outside;"
		).put(
			"disc", "list-style: disc outside;"
		).put(
			"square", "list-style: square outside;"
		).build();
	}

	@Override
	public String[] getEmoticonDescriptions() {
		return _emoticonDescriptions;
	}

	@Override
	public String[] getEmoticonFiles() {
		return _emoticonFiles;
	}

	@Override
	public String[] getEmoticonSymbols() {
		return _emoticonSymbols;
	}

	@Override
	public String[][] getEmoticons() {
		return _EMOTICONS;
	}

	@Override
	public String getHTML(String bbcode) {
		try {
			bbcode = parse(bbcode);
		}
		catch (Exception exception) {
			_log.error("Unable to parse: " + bbcode, exception);

			bbcode = HtmlUtil.escape(bbcode);
		}

		return bbcode;
	}

	@Override
	public String parse(String text) {
		StringBundler sb = new StringBundler();

		List<BBCodeItem> bbCodeItems = _bbCodeParser.parse(text);
		Stack<String> tags = new Stack<>();
		IntegerWrapper marker = new IntegerWrapper();

		for (; marker.getValue() < bbCodeItems.size(); marker.increment()) {
			BBCodeItem bbCodeItem = bbCodeItems.get(marker.getValue());

			int type = bbCodeItem.getType();

			if (type == BBCodeParser.TYPE_DATA) {
				handleData(sb, bbCodeItems, tags, marker, bbCodeItem);
			}
			else if (type == BBCodeParser.TYPE_TAG_END) {
				handleTagEnd(sb, tags);
			}
			else if (type == BBCodeParser.TYPE_TAG_START) {
				handleTagStart(sb, bbCodeItems, tags, marker, bbCodeItem);
			}
		}

		return sb.toString();
	}

	protected void handleData(
		StringBundler sb, List<BBCodeItem> bbCodeItems, Stack<String> tags,
		IntegerWrapper marker, BBCodeItem bbCodeItem) {

		String value = HtmlUtil.escape(bbCodeItem.getValue());

		value = _handleNewLine(bbCodeItems, marker, value);

		for (String[] emoticon : _EMOTICONS) {
			value = StringUtil.replace(value, emoticon[1], emoticon[0]);
		}

		sb.append(value);
	}

	protected void handleTagEnd(StringBundler sb, Stack<String> tags) {
		sb.append(tags.pop());
	}

	protected void handleTagStart(
		StringBundler sb, List<BBCodeItem> bbCodeItems, Stack<String> tags,
		IntegerWrapper marker, BBCodeItem bbCodeItem) {

		String tag = bbCodeItem.getValue();

		if (tag.equals("b")) {
			_handleBold(sb, tags);
		}
		else if (tag.equals("center") || tag.equals("justify") ||
				 tag.equals("left") || tag.equals("right")) {

			_handleTextAlign(sb, tags, bbCodeItem);
		}
		else if (tag.equals("code")) {
			_handleCode(sb, bbCodeItems, marker);
		}
		else if (tag.equals("color") || tag.equals("colour")) {
			_handleColor(sb, tags, bbCodeItem);
		}
		else if (tag.equals("email")) {
			_handleEmail(sb, bbCodeItems, tags, marker, bbCodeItem);
		}
		else if (tag.equals("font")) {
			_handleFontFamily(sb, tags, bbCodeItem);
		}
		else if (tag.equals("i")) {
			_handleItalic(sb, tags);
		}
		else if (tag.equals("img")) {
			_handleImage(sb, tags, bbCodeItems, marker);
		}
		else if (tag.equals("li") || tag.equals("*")) {
			_handleListItem(sb, tags);
		}
		else if (tag.equals("list")) {
			_handleList(sb, tags, bbCodeItem);
		}
		else if (tag.equals("q") || tag.equals("quote")) {
			_handleQuote(sb, tags, bbCodeItem);
		}
		else if (tag.equals("s")) {
			_handleStrikeThrough(sb, tags);
		}
		else if (tag.equals("size")) {
			_handleFontSize(sb, tags, bbCodeItem);
		}
		else if (tag.equals("table")) {
			_handleTable(sb, tags);
		}
		else if (tag.equals("td")) {
			_handleTableCell(sb, tags);
		}
		else if (tag.equals("th")) {
			_handleTableHeader(sb, tags);
		}
		else if (tag.equals("tr")) {
			_handleTableRow(sb, tags);
		}
		else if (tag.equals("url")) {
			_handleURL(sb, bbCodeItems, tags, marker, bbCodeItem);
		}
		else {
			_handleSimpleTag(sb, tags, bbCodeItem);
		}
	}

	private String _escapeQuote(String quote) {
		StringBundler sb = new StringBundler();

		int index = 0;

		Matcher matcher = _bbCodePattern.matcher(quote);

		Collection<String> values = _bbCodeCharacters.values();

		while (matcher.find()) {
			String match = matcher.group();

			int matchStartIndex = matcher.start();

			int nextSemicolonIndex = quote.indexOf(
				StringPool.SEMICOLON, matchStartIndex);

			sb.append(quote.substring(index, matchStartIndex));

			boolean entityFound = false;

			if (nextSemicolonIndex >= 0) {
				String value = quote.substring(
					matchStartIndex, nextSemicolonIndex + 1);

				if (values.contains(value)) {
					sb.append(value);

					index = matchStartIndex + value.length();

					entityFound = true;
				}
			}

			if (!entityFound) {
				String escapedValue = _bbCodeCharacters.get(match);

				sb.append(escapedValue);

				index = matchStartIndex + match.length();
			}
		}

		if (index < quote.length()) {
			sb.append(quote.substring(index));
		}

		return sb.toString();
	}

	private String _extractData(
		List<BBCodeItem> bbCodeItems, IntegerWrapper marker, String tag,
		int type, boolean consume) {

		StringBundler sb = new StringBundler();

		int index = marker.getValue() + 1;

		BBCodeItem bbCodeItem = null;

		do {
			bbCodeItem = bbCodeItems.get(index++);

			if ((bbCodeItem.getType() & type) > 0) {
				sb.append(bbCodeItem.getValue());
			}
		}
		while ((bbCodeItem.getType() != BBCodeParser.TYPE_TAG_END) &&
			   !tag.equals(bbCodeItem.getValue()));

		if (consume) {
			marker.setValue(index - 1);
		}

		return sb.toString();
	}

	private void _handleBold(StringBundler sb, Stack<String> tags) {
		_handleSimpleTag(sb, tags, "strong");
	}

	private void _handleCode(
		StringBundler sb, List<BBCodeItem> bbCodeItems, IntegerWrapper marker) {

		sb.append("<div class=\"lfr-code\"><table><tbody>");

		String code = _extractData(
			bbCodeItems, marker, "code", BBCodeParser.TYPE_DATA, true);

		code = HtmlUtil.escape(code);
		code = StringUtil.replace(code, CharPool.TAB, StringPool.FOUR_SPACES);

		String[] lines = code.split("\r?\n");

		for (int i = 0; i < lines.length; i++) {
			sb.append("<tr><td class=\"line-numbers\">");

			String index = String.valueOf(i + 1);

			sb.append(index);

			sb.append("</td><td class=\"lines\">");

			String line = lines[i];

			line = StringUtil.replace(
				line, StringPool.THREE_SPACES, "&nbsp; &nbsp;");
			line = StringUtil.replace(line, StringPool.DOUBLE_SPACE, "&nbsp; ");

			if (Validator.isNull(line)) {
				line = "<br />";
			}

			sb.append("<div class=\"line\">");
			sb.append(line);
			sb.append("</div></td></tr>");
		}

		sb.append("</tbody></table></div>");
	}

	private void _handleColor(
		StringBundler sb, Stack<String> tags, BBCodeItem bbCodeItem) {

		sb.append("<span style=\"color: ");

		String color = bbCodeItem.getAttribute();

		if (color == null) {
			color = "inherit";
		}
		else {
			Matcher matcher = _colorPattern.matcher(color);

			if (!matcher.matches()) {
				color = "inherit";
			}
		}

		sb.append(color);

		sb.append("\">");

		tags.push("</span>");
	}

	private void _handleEmail(
		StringBundler sb, List<BBCodeItem> bbCodeItems, Stack<String> tags,
		IntegerWrapper marker, BBCodeItem bbCodeItem) {

		sb.append("<a href=\"");

		String href = bbCodeItem.getAttribute();

		if (href == null) {
			href = _extractData(
				bbCodeItems, marker, "email", BBCodeParser.TYPE_DATA, false);
		}

		if (!href.startsWith("mailto:")) {
			href = "mailto:" + href;
		}

		sb.append(HtmlUtil.escapeHREF(href));

		sb.append("\">");

		tags.push("</a>");
	}

	private void _handleFontFamily(
		StringBundler sb, Stack<String> tags, BBCodeItem bbCodeItem) {

		sb.append("<span style=\"font-family: ");
		sb.append(HtmlUtil.escapeAttribute(bbCodeItem.getAttribute()));
		sb.append("\">");

		tags.push("</span>");
	}

	private void _handleFontSize(
		StringBundler sb, Stack<String> tags, BBCodeItem bbCodeItem) {

		sb.append("<span style=\"font-size: ");

		int size = GetterUtil.getInteger(bbCodeItem.getAttribute());

		if ((size >= 1) && (size <= _FONT_SIZES.length)) {
			sb.append(_FONT_SIZES[size - 1]);
		}
		else {
			sb.append(_FONT_SIZES[1]);
		}

		sb.append("px;\">");

		tags.push("</span>");
	}

	private void _handleImage(
		StringBundler sb, Stack<String> tags, List<BBCodeItem> bbCodeItems,
		IntegerWrapper marker) {

		sb.append("<img src=\"");

		int pos = marker.getValue();

		String src = _extractData(
			bbCodeItems, marker, "img", BBCodeParser.TYPE_DATA, true);

		Matcher matcher = _imagePattern.matcher(src);

		if (src.startsWith("data:image/") || matcher.matches()) {
			sb.append(HtmlUtil.escapeAttribute(src));
		}

		sb.append("\"");

		BBCodeItem bbCodeItem = bbCodeItems.get(pos);

		String attributes = bbCodeItem.getAttribute();

		if (Validator.isNotNull(attributes)) {
			sb.append(StringPool.SPACE);

			_handleImageAttributes(sb, attributes);
		}

		sb.append(" />");

		tags.push(StringPool.BLANK);
	}

	private void _handleImageAttributes(StringBundler sb, String attributes) {
		Matcher matcher = _attributesPattern.matcher(attributes);

		while (matcher.find()) {
			String attributeName = matcher.group(1);

			if (Validator.isNotNull(attributeName) &&
				_imageAttributes.contains(
					StringUtil.toLowerCase(attributeName))) {

				String attributeValue = matcher.group(2);

				sb.append(StringPool.SPACE);
				sb.append(attributeName);
				sb.append(StringPool.EQUAL);
				sb.append(StringPool.QUOTE);
				sb.append(HtmlUtil.escapeAttribute(attributeValue));
				sb.append(StringPool.QUOTE);
			}
		}
	}

	private void _handleItalic(StringBundler sb, Stack<String> tags) {
		_handleSimpleTag(sb, tags, "em");
	}

	private void _handleList(
		StringBundler sb, Stack<String> tags, BBCodeItem bbCodeItem) {

		String tag = "ul";
		StringBundler attributesSB = new StringBundler();

		if (Validator.isNotNull(bbCodeItem.getAttribute())) {
			Matcher matcher = _attributesPattern.matcher(
				bbCodeItem.getAttribute());

			while (matcher.find()) {
				String attributeName = matcher.group(1);
				String attributeValue = matcher.group(2);

				if (Objects.equals(attributeName, "type")) {
					String listStyle = null;

					if (_orderedListStyles.get(attributeValue) != null) {
						listStyle = _orderedListStyles.get(attributeValue);

						tag = "ol";
					}
					else {
						listStyle = _unorderedListStyles.get(attributeValue);
					}

					if (Validator.isNotNull(listStyle)) {
						attributesSB.append(" style=\"");
						attributesSB.append(listStyle);
						attributesSB.append("\"");
					}
				}
				else if (Objects.equals(attributeName, "start") &&
						 Validator.isNumber(attributeValue)) {

					attributesSB.append(" start=\"");
					attributesSB.append(attributeValue);
					attributesSB.append("\"");
				}
			}
		}

		sb.append("<");
		sb.append(tag);
		sb.append(attributesSB);
		sb.append(">");

		tags.push("</" + tag + ">");
	}

	private void _handleListItem(StringBundler sb, Stack<String> tags) {
		_handleSimpleTag(sb, tags, "li");
	}

	private String _handleNewLine(
		List<BBCodeItem> bbCodeItems, IntegerWrapper marker, String data) {

		if ((marker.getValue() + 1) < bbCodeItems.size()) {
			BBCodeItem bbCodeItem = null;

			if (data.matches("\\A\r?\n\\z")) {
				bbCodeItem = bbCodeItems.get(marker.getValue() + 1);

				if (bbCodeItem != null) {
					String value = bbCodeItem.getValue();

					if (_excludeNewLineTypes.containsKey(value)) {
						int type = bbCodeItem.getType();

						int excludeNewLineType = _excludeNewLineTypes.get(
							value);

						if ((type & excludeNewLineType) > 0) {
							data = StringPool.BLANK;
						}
					}
				}
			}
			else if (data.matches("(?s).*\r?\n\\z")) {
				bbCodeItem = bbCodeItems.get(marker.getValue() + 1);

				if ((bbCodeItem != null) &&
					(bbCodeItem.getType() == BBCodeParser.TYPE_TAG_END)) {

					String value = bbCodeItem.getValue();

					if (value.equals("*")) {
						data = data.substring(0, data.length() - 1);
					}
				}
			}
		}

		if (data.length() > 0) {
			data = StringUtil.replace(
				data, StringPool.RETURN_NEW_LINE, "<br />");
			data = StringUtil.replace(data, CharPool.NEW_LINE, "<br />");
		}

		return data;
	}

	private void _handleQuote(
		StringBundler sb, Stack<String> tags, BBCodeItem bbCodeItem) {

		String quote = bbCodeItem.getAttribute();

		if ((quote != null) && (quote.length() > 0)) {
			sb.append("<div class=\"quote-title\">");
			sb.append(_escapeQuote(quote));
			sb.append(":</div>");
		}

		sb.append("<div class=\"quote\"><div class=\"quote-content\">");

		tags.push("</div></div>");
	}

	private void _handleSimpleTag(
		StringBundler sb, Stack<String> tags, BBCodeItem bbCodeItem) {

		_handleSimpleTag(sb, tags, bbCodeItem.getValue());
	}

	private void _handleSimpleTag(
		StringBundler sb, Stack<String> tags, String tag) {

		sb.append("<");
		sb.append(tag);
		sb.append(">");

		tags.push("</" + tag + ">");
	}

	private void _handleStrikeThrough(StringBundler sb, Stack<String> tags) {
		_handleSimpleTag(sb, tags, "strike");
	}

	private void _handleTable(StringBundler sb, Stack<String> tags) {
		_handleSimpleTag(sb, tags, "table");
	}

	private void _handleTableCell(StringBundler sb, Stack<String> tags) {
		_handleSimpleTag(sb, tags, "td");
	}

	private void _handleTableHeader(StringBundler sb, Stack<String> tags) {
		_handleSimpleTag(sb, tags, "th");
	}

	private void _handleTableRow(StringBundler sb, Stack<String> tags) {
		_handleSimpleTag(sb, tags, "tr");
	}

	private void _handleTextAlign(
		StringBundler sb, Stack<String> tags, BBCodeItem bbCodeItem) {

		sb.append("<p style=\"text-align: ");
		sb.append(bbCodeItem.getValue());
		sb.append("\">");

		tags.push("</p>");
	}

	private void _handleURL(
		StringBundler sb, List<BBCodeItem> bbCodeItems, Stack<String> tags,
		IntegerWrapper marker, BBCodeItem bbCodeItem) {

		sb.append("<a href=\"");

		String href = bbCodeItem.getAttribute();

		if (href == null) {
			href = _extractData(
				bbCodeItems, marker, "url", BBCodeParser.TYPE_DATA, false);
		}

		Matcher matcher = _urlPattern.matcher(href);

		if (matcher.matches()) {
			sb.append(HtmlUtil.escapeHREF(href));
		}

		sb.append("\">");

		tags.push("</a>");
	}

	private static final String[][] _EMOTICONS = {
		{"happy.gif", ":)", "happy"}, {"smile.gif", ":D", "smile"},
		{"cool.gif", "B)", "cool"}, {"sad.gif", ":(", "sad"},
		{"tongue.gif", ":P", "tongue"}, {"laugh.gif", ":lol:", "laugh"},
		{"kiss.gif", ":#", "kiss"}, {"blush.gif", ":*)", "blush"},
		{"bashful.gif", ":bashful:", "bashful"}, {"smug.gif", ":smug:", "smug"},
		{"blink.gif", ":blink:", "blink"}, {"huh.gif", ":huh:", "huh"},
		{"mellow.gif", ":mellow:", "mellow"},
		{"unsure.gif", ":unsure:", "unsure"}, {"mad.gif", ":mad:", "mad"},
		{"oh_my.gif", ":O", "oh-my-goodness"},
		{"roll_eyes.gif", ":rolleyes:", "roll-eyes"},
		{"angry.gif", ":angry:", "angry"},
		{"suspicious.gif", "8o", "suspicious"},
		{"big_grin.gif", ":grin:", "grin"},
		{"in_love.gif", ":love:", "in-love"}, {"bored.gif", ":bored:", "bored"},
		{"closed_eyes.gif", "-_-", "closed-eyes"},
		{"cold.gif", ":cold:", "cold"}, {"sleep.gif", ":sleep:", "sleep"},
		{"glare.gif", ":glare:", "glare"},
		{"darth_vader.gif", ":vader:", "darth-vader"},
		{"dry.gif", ":dry:", "dry"}, {"exclamation.gif", ":what:", "what"},
		{"girl.gif", ":girl:", "girl"},
		{"karate_kid.gif", ":kid:", "karate-kid"},
		{"ninja.gif", ":ph34r:", "ninja"}, {"pac_man.gif", ":V", "pac-man"},
		{"wacko.gif", ":wacko:", "wacko"}, {"wink.gif", ":wink:", "wink"},
		{"wub.gif", ":wub:", "wub"}
	};

	private static final int[] _FONT_SIZES = {10, 12, 14, 16, 18, 24, 32, 48};

	private static final Log _log = LogFactoryUtil.getLog(
		HtmlBBCodeTranslatorImpl.class);

	private static final Pattern _attributesPattern = Pattern.compile(
		"\\s*([^=]+)\\s*=\\s*\"([^\"]+)\"\\s*");
	private static final Pattern _bbCodePattern = Pattern.compile(
		"[]&<>'\"`\\[()]");
	private static final Pattern _colorPattern = Pattern.compile(
		"^(:?aqua|black|blue|fuchsia|gray|green|lime|maroon|navy|olive|purple" +
			"|red|silver|teal|white|yellow|#(?:[0-9a-f]{3})?[0-9a-f]{3})$",
		Pattern.CASE_INSENSITIVE);
	private static final Pattern _imagePattern = Pattern.compile(
		"^(?:https?://|/)[-;/?:@&=+$,_.!~*'()%0-9a-z]{1,2048}$",
		Pattern.CASE_INSENSITIVE);
	private static final Pattern _urlPattern = Pattern.compile(
		"^[-;/?:@&=+$,_.!~*'()%\\p{Digit}\\p{L}#]{1,2048}$",
		Pattern.CASE_INSENSITIVE);

	private final Map<String, String> _bbCodeCharacters;
	private final BBCodeParser _bbCodeParser = new BBCodeParser();
	private final String[] _emoticonDescriptions =
		new String[_EMOTICONS.length];
	private final String[] _emoticonFiles = new String[_EMOTICONS.length];
	private final String[] _emoticonSymbols = new String[_EMOTICONS.length];
	private final Map<String, Integer> _excludeNewLineTypes;
	private final Set<String> _imageAttributes;
	private final Map<String, String> _orderedListStyles;
	private final Map<String, String> _unorderedListStyles;

}