/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.fragment.entry.processor.drop.zone;

import com.liferay.fragment.exception.FragmentEntryContentException;
import com.liferay.fragment.processor.DocumentFragmentEntryValidator;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.util.Validator;

import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Set;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Eudaldo Alonso
 */
@Component(
	property = "fragment.entry.processor.priority:Integer=6",
	service = DocumentFragmentEntryValidator.class
)
public class DropZoneFragmentEntryValidator
	implements DocumentFragmentEntryValidator {

	@Override
	public void validateFragmentEntryHTML(
			Document document, JSONObject configurationJSONObject,
			Locale locale)
		throws PortalException {

		Elements elements = document.getElementsByTag("lfr-drop-zone");

		if (elements.isEmpty()) {
			return;
		}

		Set<String> elementDropZoneIds = new LinkedHashSet<>();

		for (Element element : elements) {
			String dropZoneId = element.attr("data-lfr-drop-zone-id");

			if (!Validator.isBlank(dropZoneId)) {
				elementDropZoneIds.add(dropZoneId);
			}
		}

		if (!elementDropZoneIds.isEmpty() &&
			(elementDropZoneIds.size() != elements.size())) {

			throw new FragmentEntryContentException(
				_language.get(
					locale, "you-must-define-a-unique-id-for-each-drop-zone"));
		}
	}

	@Reference
	private Language _language;

}