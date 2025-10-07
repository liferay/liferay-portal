/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.fragment.entry.processor.background.image;

import com.liferay.fragment.exception.FragmentEntryContentException;
import com.liferay.fragment.processor.DocumentFragmentEntryValidator;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.language.Language;

import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Eudaldo Alonso
 */
@Component(
	property = "fragment.entry.processor.priority:Integer=5",
	service = DocumentFragmentEntryValidator.class
)
public class BackgroundImageFragmentEntryValidator
	implements DocumentFragmentEntryValidator {

	@Override
	public void validateFragmentEntryHTML(
			Document document, JSONObject configurationJSONObject,
			Locale locale)
		throws PortalException {

		Set<String> ids = new HashSet<>();

		for (Element element :
				document.getElementsByAttribute(
					"data-lfr-background-image-id")) {

			if (ids.add(element.attr("data-lfr-background-image-id"))) {
				continue;
			}

			throw new FragmentEntryContentException(
				_language.get(
					locale,
					"you-must-define-a-unique-id-for-each-background-image-" +
						"element"));
		}
	}

	@Reference
	private Language _language;

}