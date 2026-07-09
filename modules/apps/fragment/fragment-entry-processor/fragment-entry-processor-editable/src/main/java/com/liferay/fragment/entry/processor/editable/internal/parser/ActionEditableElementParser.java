/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.fragment.entry.processor.editable.internal.parser;

import com.liferay.fragment.entry.processor.editable.parser.EditableElementParser;
import com.liferay.fragment.exception.FragmentEntryContentException;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.util.ResourceBundleUtil;
import com.liferay.portal.kernel.util.Validator;

import java.util.Objects;
import java.util.ResourceBundle;

import org.jsoup.nodes.Element;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Rubén Pulido
 */
@Component(property = "type=action", service = EditableElementParser.class)
public class ActionEditableElementParser extends BaseEditableElementParser {

	@Override
	public String getValue(Element element) {
		String html = element.html();

		if (Validator.isNull(html.trim())) {
			ResourceBundle resourceBundle = ResourceBundleUtil.getBundle(
				"content.Language", getClass());

			return _language.get(resourceBundle, "example-action");
		}

		return html;
	}

	@Override
	public void replace(Element element, String value) {
		replaceContent(element, value);
	}

	@Override
	public void validate(Element element) throws FragmentEntryContentException {
		if (!Objects.equals(element.tagName(), "button")) {
			ResourceBundle resourceBundle = ResourceBundleUtil.getBundle(
				"content.Language", getClass());

			throw new FragmentEntryContentException(
				_language.format(
					resourceBundle,
					"an-editable-of-type-x-can-only-be-used-in-a-tag-of-type-x",
					new Object[] {"action", "button"}, false));
		}

		super.validate(element);
	}

	@Reference
	private Language _language;

}