/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.message.boards.parser.bbcode.internal;

import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.IntegerWrapper;
import com.liferay.portal.kernel.util.SetUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Iliyan Peychev
 */
public class BBCodeParser {

	public static final int TYPE_DATA = 0x04;

	public static final int TYPE_TAG_END = 0x02;

	public static final int TYPE_TAG_START = 0x01;

	public static final int TYPE_TAG_START_END = TYPE_TAG_START | TYPE_TAG_END;

	public BBCodeParser() {
		_blockElements = SetUtil.fromArray(
			"*", "center", "code", "justify", "left", "li", "list", "q",
			"quote", "right", "table", "td", "th", "tr");

		_inlineElements = SetUtil.fromArray(
			"b", "color", "font", "i", "img", "s", "size", "u", "url");

		_selfCloseElements = SetUtil.fromArray("*");
	}

	public List<BBCodeItem> parse(String text) {
		List<BBCodeItem> bbCodeItems = new ArrayList<>();

		BBCodeLexer bbCodeLexer = new BBCodeLexer(text);
		Stack<String> tags = new Stack<>();
		IntegerWrapper marker = new IntegerWrapper();

		BBCodeToken bbCodeToken = null;

		while ((bbCodeToken = bbCodeLexer.getNextBBCodeToken()) != null) {
			handleData(bbCodeItems, bbCodeLexer, marker, bbCodeToken, text);

			if (bbCodeToken.getStartTag() == null) {
				handleTagEnd(bbCodeItems, tags, bbCodeToken);

				continue;
			}

			handleTagStart(bbCodeItems, tags, bbCodeToken);

			String startTag = bbCodeToken.getStartTag();

			if (!startTag.equals("code")) {
				continue;
			}

			while (true) {
				bbCodeToken = bbCodeLexer.getNextBBCodeToken();

				if (bbCodeToken == null) {
					break;
				}

				String endTag = GetterUtil.getString(bbCodeToken.getEndTag());

				if (endTag.equals("code")) {
					break;
				}
			}

			handleData(bbCodeItems, bbCodeLexer, marker, bbCodeToken, text);

			if (bbCodeToken == null) {
				break;
			}

			handleTagEnd(bbCodeItems, tags, bbCodeToken);
		}

		handleData(bbCodeItems, bbCodeLexer, marker, null, text);
		handleTagEnd(bbCodeItems, tags, null);

		return bbCodeItems;
	}

	protected void handleData(
		List<BBCodeItem> bbCodeItems, BBCodeLexer bbCodeLexer,
		IntegerWrapper marker, BBCodeToken bbCodeToken, String data) {

		int length = data.length();

		int lastIndex = length;

		if (bbCodeToken != null) {
			lastIndex = bbCodeLexer.getLastIndex();

			length = lastIndex;

			String tag = bbCodeToken.getStartTag();

			if (tag == null) {
				tag = bbCodeToken.getEndTag();
			}

			if (_isValidTag(tag)) {
				length = bbCodeToken.getStart();
			}
		}

		if (length > marker.getValue()) {
			BBCodeItem bbCodeItem = new BBCodeItem(
				TYPE_DATA, null, data.substring(marker.getValue(), length));

			bbCodeItems.add(bbCodeItem);
		}

		marker.setValue(lastIndex);
	}

	protected void handleTagEnd(
		List<BBCodeItem> bbCodeItems, Stack<String> tags,
		BBCodeToken bbCodeToken) {

		int size = 0;

		if (bbCodeToken != null) {
			String endTag = bbCodeToken.getEndTag();

			for (size = tags.size() - 1; size >= 0; size--) {
				if (endTag.equals(tags.elementAt(size))) {
					break;
				}
			}
		}

		if (size >= 0) {
			for (int i = tags.size() - 1; i >= size; i--) {
				BBCodeItem bbCodeItem = new BBCodeItem(
					TYPE_TAG_END, null, tags.elementAt(i));

				bbCodeItems.add(bbCodeItem);
			}

			tags.setSize(size);
		}
	}

	protected void handleTagStart(
		List<BBCodeItem> bbCodeItems, Stack<String> tags,
		BBCodeToken bbCodeToken) {

		String startTag = bbCodeToken.getStartTag();

		if (!_isValidTag(startTag)) {
			return;
		}

		if (!tags.isEmpty()) {
			if (_blockElements.contains(startTag)) {
				String currentTag = null;

				while (!tags.isEmpty() &&
					   ((currentTag = tags.lastElement()) != null) &&
					   _inlineElements.contains(currentTag)) {

					BBCodeToken currentTagBBCodeToken = new BBCodeToken(
						currentTag);

					handleTagEnd(bbCodeItems, tags, currentTagBBCodeToken);
				}
			}

			if (!tags.isEmpty() && _selfCloseElements.contains(startTag) &&
				startTag.equals(tags.lastElement())) {

				BBCodeToken tagBBCodeToken = new BBCodeToken(startTag);

				handleTagEnd(bbCodeItems, tags, tagBBCodeToken);
			}
		}

		tags.push(startTag);

		BBCodeItem bbCodeItem = new BBCodeItem(
			TYPE_TAG_START, bbCodeToken.getAttribute(), startTag);

		bbCodeItems.add(bbCodeItem);
	}

	private boolean _isValidTag(String tag) {
		if ((tag != null) && (tag.length() > 0)) {
			Matcher matcher = _tagPattern.matcher(tag);

			return matcher.matches();
		}

		return false;
	}

	private static final Pattern _tagPattern = Pattern.compile(
		"^/?(?:b|center|code|colou?r|email|font|i|img|justify|left|li|list" +
			"|pre|q|quote|right|s|size|table|td|th|tr|u|url|\\*)$",
		Pattern.CASE_INSENSITIVE);

	private final Set<String> _blockElements;
	private final Set<String> _inlineElements;
	private final Set<String> _selfCloseElements;

}