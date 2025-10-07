/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.fragment.entry.processor.styles;

import com.liferay.fragment.exception.FragmentEntryContentException;
import com.liferay.fragment.processor.FragmentEntryValidator;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.util.StringUtil;

import java.util.Locale;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Eudaldo Alonso
 */
@Component(
	property = "fragment.entry.processor.priority:Integer=7",
	service = FragmentEntryValidator.class
)
public class StylesFragmentEntryValidator implements FragmentEntryValidator {

	@Override
	public void validateFragmentEntryHTML(
			String html, JSONObject configurationJSONObject, Locale locale)
		throws PortalException {

		if (StringUtil.count(html, "data-lfr-styles") > 1) {
			throw new FragmentEntryContentException(
				_language.get(
					locale,
					"the-data-lfr-styles-attribute-can-be-used-only-once-on-" +
						"the-same-fragment"));
		}
	}

	@Reference
	private Language _language;

}