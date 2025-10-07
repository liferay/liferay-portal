/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.fragment.entry.processor.editable.internal.parser;

import com.liferay.fragment.entry.processor.editable.parser.EditableElementParser;
import com.liferay.fragment.exception.FragmentEntryContentException;
import com.liferay.petra.string.CharPool;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.util.ResourceBundleUtil;
import com.liferay.portal.kernel.util.Validator;

import java.util.Objects;
import java.util.ResourceBundle;

import org.jsoup.nodes.Element;
import org.jsoup.nodes.TextNode;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Eudaldo Alonso
 */
@Component(property = "type=text", service = EditableElementParser.class)
public class TextEditableElementParser extends BaseEditableElementParser {

	@Override
	public String getValue(Element element) {
		String html = element.html();

		if (Validator.isNull(html.trim())) {
			ResourceBundle resourceBundle = ResourceBundleUtil.getBundle(
				"content.Language", getClass());

			return _language.get(resourceBundle, "example-text");
		}

		return html;
	}

	@Override
	public void replace(Element element, String value) {
		replace(element, value, null);
	}

	@Override
	public void replace(
		Element element, String value, JSONObject configJSONObject) {

		if (configJSONObject == null) {
			element.html(value);

			return;
		}

		String textAlignmentValue = configJSONObject.getString("textAlignment");

		if (Validator.isNotNull(textAlignmentValue)) {
			element.addClass("text-" + textAlignmentValue);
		}

		String textColorValue = configJSONObject.getString("textColor");

		if (Validator.isNotNull(textColorValue)) {
			element.addClass("text-" + textColorValue);
		}

		String textStyleValue = configJSONObject.getString("textStyle");

		if (Validator.isNotNull(textStyleValue)) {
			element.addClass(textStyleValue);
		}

		if (value.indexOf(CharPool.LESS_THAN) == -1) {
			element.empty();

			element.appendChild(new TextNode(value));
		}
		else {
			element.html(value);
		}
	}

	@Override
	public void validate(Element element) throws FragmentEntryContentException {
		for (String tag : _TAGS_BLACKLIST) {
			if (Objects.equals(element.tagName(), tag)) {
				ResourceBundle resourceBundle = ResourceBundleUtil.getBundle(
					"content.Language", getClass());

				throw new FragmentEntryContentException(
					_language.format(
						resourceBundle,
						"an-editable-of-type-x-cannot-be-used-in-a-tag-of-" +
							"type-x",
						new Object[] {getEditableElementType(), tag}, false));
			}
		}

		super.validate(element);
	}

	protected String getEditableElementType() {
		return "text";
	}

	private static final String[] _TAGS_BLACKLIST = {"img", "a"};

	@Reference
	private Language _language;

}