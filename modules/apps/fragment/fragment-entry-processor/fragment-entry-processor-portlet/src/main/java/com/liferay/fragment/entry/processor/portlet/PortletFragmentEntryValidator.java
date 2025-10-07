/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.fragment.entry.processor.portlet;

import com.liferay.fragment.exception.FragmentEntryContentException;
import com.liferay.fragment.processor.DocumentFragmentEntryValidator;
import com.liferay.fragment.processor.PortletRegistry;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.model.ModelHintsConstants;
import com.liferay.portal.kernel.model.Portlet;
import com.liferay.portal.kernel.service.PortletLocalService;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;

import java.util.Locale;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Eudaldo Alonso
 */
@Component(
	property = "fragment.entry.processor.priority:Integer=3",
	service = DocumentFragmentEntryValidator.class
)
public class PortletFragmentEntryValidator
	implements DocumentFragmentEntryValidator {

	@Override
	public void validateFragmentEntryHTML(
			Document document, JSONObject configurationJSONObject,
			Locale locale)
		throws PortalException {

		for (Element element : document.getAllElements()) {
			String htmlTagName = element.tagName();

			if (!StringUtil.startsWith(htmlTagName, "lfr-widget-")) {
				continue;
			}

			String alias = StringUtil.removeSubstring(
				htmlTagName, "lfr-widget-");

			if (Validator.isNull(_portletRegistry.getPortletName(alias))) {
				throw new FragmentEntryContentException(
					_language.format(
						locale, "there-is-no-widget-available-for-alias-x",
						alias));
			}

			String id = element.id();

			if (Validator.isNotNull(id) && !Validator.isAlphanumericName(id)) {
				throw new FragmentEntryContentException(
					_language.format(
						locale,
						"widget-id-must-contain-only-alphanumeric-characters",
						alias));
			}

			if (Validator.isNotNull(id)) {
				Elements elements = document.select("#" + id);

				if (elements.size() > 1) {
					throw new FragmentEntryContentException(
						_language.get(locale, "widget-id-must-be-unique"));
				}

				if (id.length() > GetterUtil.getInteger(
						ModelHintsConstants.TEXT_MAX_LENGTH)) {

					throw new FragmentEntryContentException(
						_language.format(
							locale, "widget-id-cannot-exceed-x-characters",
							ModelHintsConstants.TEXT_MAX_LENGTH));
				}
			}

			Elements elements = document.getElementsByTag(htmlTagName);

			if ((elements.size() > 1) && Validator.isNull(id)) {
				throw new FragmentEntryContentException(
					_language.get(
						locale,
						"duplicate-widgets-within-the-same-fragment-must-" +
							"have-an-id"));
			}

			if (elements.size() > 1) {
				Portlet portlet = _portletLocalService.getPortletById(
					_portletRegistry.getPortletName(alias));

				if (!portlet.isInstanceable()) {
					throw new FragmentEntryContentException(
						_language.format(
							locale,
							"you-cannot-add-the-widget-x-more-than-once",
							alias));
				}
			}
		}
	}

	@Reference
	private Language _language;

	@Reference
	private PortletLocalService _portletLocalService;

	@Reference
	private PortletRegistry _portletRegistry;

}