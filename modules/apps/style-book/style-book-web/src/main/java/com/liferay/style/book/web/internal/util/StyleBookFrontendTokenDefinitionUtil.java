/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.style.book.web.internal.util;

import com.liferay.frontend.token.definition.constants.FrontendTokenDefinitionConstants;
import com.liferay.frontend.token.definition.util.FrontendTokenDefinitionUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.style.book.constants.StyleBookConstants;
import com.liferay.style.book.model.StyleBookEntry;

import java.util.Locale;

/**
 * @author Thiago Buarque
 */
public class StyleBookFrontendTokenDefinitionUtil {

	public static JSONObject getCustomFrontendTokenDefinitionJSONObject(
		Locale locale, StyleBookEntry styleBookEntry) {

		JSONObject customFrontendTokenDefinitionJSONObject =
			FrontendTokenDefinitionUtil.parseFrontendTokenDefinitionJSONObject(
				styleBookEntry.getFrontendTokenDefinition());

		if (customFrontendTokenDefinitionJSONObject == null) {
			return null;
		}

		return customFrontendTokenDefinitionJSONObject.put(
			"id", StyleBookConstants.CUSTOM_FRONTEND_TOKEN_DEFINITION_ID
		).put(
			"name", LanguageUtil.get(locale, "custom")
		).put(
			"priority", FrontendTokenDefinitionConstants.PRIORITY_CUSTOM
		);
	}

}