/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.translation.internal.util;

import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.xml.Document;
import com.liferay.portal.kernel.xml.DocumentException;
import com.liferay.portal.kernel.xml.Element;
import com.liferay.portal.kernel.xml.Node;
import com.liferay.portal.kernel.xml.SAXReaderUtil;
import com.liferay.portal.kernel.xml.XMLUtil;
import com.liferay.translation.internal.tokenizer.HTMLInlineCodeToken;
import com.liferay.translation.internal.tokenizer.HTMLInlineCodeTokenizer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import net.sf.okapi.common.filterwriter.XLIFFContent;
import net.sf.okapi.common.resource.Code;
import net.sf.okapi.common.resource.TextFragment;
import net.sf.okapi.lib.xliff2.core.Fragment;
import net.sf.okapi.lib.xliff2.core.TagType;

/**
 * @author Akhash Ramprakash
 */
public class XLIFFInlineCodeUtil {

	public static void addXLIFF12InlineCodes(
			Element element, String namespaceURI, TextFragment... textFragments)
		throws DocumentException {

		StringBundler sb = new StringBundler(textFragments.length);

		for (TextFragment textFragment : textFragments) {
			XLIFFContent xliffContent = new XLIFFContent();

			xliffContent.setContent(textFragment);

			sb.append(xliffContent.toString());
		}

		String content = sb.toString();

		if (content.isEmpty()) {
			element.addText(StringPool.BLANK);

			return;
		}

		Document document = SAXReaderUtil.read(
			StringBundler.concat(
				"<inline-codes xmlns=\"", namespaceURI, "\">", content,
				"</inline-codes>"));

		Element rootElement = document.getRootElement();

		for (Node node : new ArrayList<>(rootElement.content())) {
			rootElement.remove(node);

			element.add(node);
		}
	}

	public static void appendXLIFF20InlineCodes(
		Fragment fragment, TextFragment textFragment) {

		String codedText = textFragment.getCodedText();

		for (int i = 0; i < codedText.length(); i++) {
			char c = codedText.charAt(i);

			if (!TextFragment.isMarker(c)) {
				fragment.append(c);

				continue;
			}

			Code code = textFragment.getCode(codedText.charAt(++i));

			String id = String.valueOf(code.getId());

			if (c == TextFragment.MARKER_OPENING) {
				fragment.openCodeSpan(id, code.getData());
			}
			else if (c == TextFragment.MARKER_CLOSING) {
				fragment.closeCodeSpan(id, code.getData());
			}
			else if (code.getTagType() == TextFragment.TagType.OPENING) {
				fragment.append(TagType.OPENING, id, code.getData(), true);
			}
			else if (code.getTagType() == TextFragment.TagType.CLOSING) {
				fragment.append(TagType.CLOSING, id, code.getData(), true);
			}
			else {
				fragment.appendCode(id, code.getData());
			}
		}
	}

	public static int renumberCodes(
		int startId, TextFragment sourceTextFragment,
		TextFragment targetTextFragment) {

		int lastId = sourceTextFragment.renumberCodes(startId);

		Map<String, Queue<Integer>> sourceIdsMap = new HashMap<>();

		Map<Integer, List<Code>> sourceCodesMap = _getCodesMap(
			sourceTextFragment);

		for (Map.Entry<Integer, List<Code>> entry : sourceCodesMap.entrySet()) {
			Queue<Integer> sourceIds = sourceIdsMap.computeIfAbsent(
				_getKey(entry.getValue()), key -> new LinkedList<>());

			sourceIds.add(entry.getKey());
		}

		Map<Integer, List<Code>> targetCodesMap = _getCodesMap(
			targetTextFragment);

		for (List<Code> codes : targetCodesMap.values()) {
			Queue<Integer> sourceIds = sourceIdsMap.get(_getKey(codes));

			int id = 0;

			if ((sourceIds != null) && !sourceIds.isEmpty()) {
				id = sourceIds.poll();
			}
			else {
				id = ++lastId;
			}

			for (Code code : codes) {
				code.setId(id);
			}
		}

		return lastId + 1;
	}

	public static TextFragment toTextFragment(String html) {
		TextFragment textFragment = new TextFragment();

		if (html == null) {
			return textFragment;
		}

		List<HTMLInlineCodeToken> htmlInlineCodeTokens =
			HTMLInlineCodeTokenizer.tokenize(XMLUtil.stripInvalidChars(html));

		for (HTMLInlineCodeToken htmlInlineCodeToken : htmlInlineCodeTokens) {
			HTMLInlineCodeToken.Type type = htmlInlineCodeToken.getType();

			if (type == HTMLInlineCodeToken.Type.TEXT) {
				textFragment.append(htmlInlineCodeToken.getRawText());

				continue;
			}

			String tagName = htmlInlineCodeToken.getTagName();

			textFragment.append(
				_getTagType(type),
				(tagName == null) ? StringPool.BLANK : tagName,
				htmlInlineCodeToken.getRawText());
		}

		return textFragment;
	}

	private static Map<Integer, List<Code>> _getCodesMap(
		TextFragment textFragment) {

		Map<Integer, List<Code>> codesMap = new LinkedHashMap<>();

		for (Code code : textFragment.getCodes()) {
			List<Code> codes = codesMap.computeIfAbsent(
				code.getId(), id -> new ArrayList<>());

			codes.add(code);
		}

		return codesMap;
	}

	private static String _getKey(List<Code> codes) {
		StringBundler sb = new StringBundler(codes.size() * 2);

		for (Code code : codes) {
			sb.append(String.valueOf(code.getTagType()));
			sb.append(code.getData());
		}

		return sb.toString();
	}

	private static TextFragment.TagType _getTagType(
		HTMLInlineCodeToken.Type type) {

		if (type == HTMLInlineCodeToken.Type.OPENING) {
			return TextFragment.TagType.OPENING;
		}

		if (type == HTMLInlineCodeToken.Type.CLOSING) {
			return TextFragment.TagType.CLOSING;
		}

		return TextFragment.TagType.PLACEHOLDER;
	}

}