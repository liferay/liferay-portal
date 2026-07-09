/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.fragment.entry.processor.editable.internal.parser;

import com.liferay.fragment.entry.processor.editable.parser.EditableElementParser;
import com.liferay.fragment.exception.FragmentEntryContentException;
import com.liferay.petra.string.CharPool;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.util.HtmlUtil;
import com.liferay.portal.kernel.util.ResourceBundleUtil;

import java.util.ResourceBundle;

import org.jsoup.nodes.Element;

/**
 * @author Eudaldo Alonso
 */
public abstract class BaseEditableElementParser
	implements EditableElementParser {

	@Override
	public void validate(Element element) throws FragmentEntryContentException {
		String html = element.html();

		if (html.contains("data-lfr-editable-id=\"") ||
			html.contains("<lfr-drop-zone") || html.contains("<lfr-editable") ||
			html.contains("<lfr-widget-")) {

			ResourceBundle resourceBundle = ResourceBundleUtil.getBundle(
				"content.Language", getClass());

			throw new FragmentEntryContentException(
				LanguageUtil.get(
					resourceBundle,
					"editable-fields-cannot-include-nested-editables-drop-" +
						"zones-or-widgets-in-it"));
		}
	}

	protected void replaceContent(Element element, String value) {
		if (value.indexOf(CharPool.LESS_THAN) == -1) {
			element.empty();

			element.appendText(HtmlUtil.unescape(value));
		}
		else {
			element.html(value);
		}
	}

}