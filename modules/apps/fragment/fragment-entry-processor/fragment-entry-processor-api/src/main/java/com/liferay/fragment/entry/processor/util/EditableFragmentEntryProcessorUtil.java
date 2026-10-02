/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.fragment.entry.processor.util;

import com.liferay.fragment.processor.util.FragmentEntryHtmlParserUtil;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * @author Jürgen Kappler
 */
public class EditableFragmentEntryProcessorUtil {

	public static Map<String, String> getEditableTypes(String html) {
		Map<String, String> editableTypes = new LinkedHashMap<>();

		Document document = FragmentEntryHtmlParserUtil.parse(html);

		Elements elements = document.getElementsByTag("lfr-editable");

		elements.forEach(
			element -> editableTypes.put(
				getElementId(element), getElementType(element)));

		Elements editableElements = document.getElementsByAttribute(
			"data-lfr-editable-id");

		editableElements.forEach(
			element -> editableTypes.put(
				getElementId(element), getElementType(element)));

		Elements backgroundImageElements = document.getElementsByAttribute(
			"data-lfr-background-image-id");

		backgroundImageElements.forEach(
			element -> editableTypes.put(
				element.attr("data-lfr-background-image-id"),
				"background-image"));

		return editableTypes;
	}

	public static String getElementId(Element element) {
		if (Objects.equals(element.tagName(), "lfr-editable")) {
			return element.attr("id");
		}

		return element.attr("data-lfr-editable-id");
	}

	public static String getElementType(Element element) {
		if (Objects.equals(element.tagName(), "lfr-editable")) {
			return element.attr("type");
		}

		return element.attr("data-lfr-editable-type");
	}

}