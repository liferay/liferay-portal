/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.fragment.entry.processor.nullable;

import com.liferay.fragment.exception.FragmentEntryContentException;
import com.liferay.fragment.processor.FragmentEntryValidator;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.util.Validator;

import java.util.Locale;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Eudaldo Alonso
 */
@Component(
	property = "fragment.entry.processor.priority:Integer=0",
	service = FragmentEntryValidator.class
)
public class NullableFragmentEntryValidator implements FragmentEntryValidator {

	@Override
	public void validateFragmentEntryHTML(
			String html, JSONObject configurationJSONObject, Locale locale)
		throws PortalException {

		if (Validator.isNull(html)) {
			String message = _language.get(
				locale, "html-content-must-not-be-empty");

			throw new FragmentEntryContentException(message);
		}
	}

	@Reference
	private Language _language;

}