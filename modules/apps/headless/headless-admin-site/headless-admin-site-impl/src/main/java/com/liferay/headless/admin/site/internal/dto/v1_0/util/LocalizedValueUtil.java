/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.site.internal.dto.v1_0.util;

import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.language.LanguageUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Mikel Lorza
 */
public class LocalizedValueUtil {

	public static List<String> getAvailableLanguageIds() {
		return TransformUtil.transform(
			LanguageUtil.getAvailableLocales(), LanguageUtil::getLanguageId);
	}

	public static JSONObject toJSONObject(Map<String, String> localizedValues) {
		JSONObject jsonObject = JSONFactoryUtil.createJSONObject();

		for (Map.Entry<String, String> entry : localizedValues.entrySet()) {
			jsonObject.put(entry.getKey(), entry.getValue());
		}

		return jsonObject;
	}

	public static Map<String, String> toLocalizedValues(JSONObject jsonObject) {
		return new HashMap<String, String>() {
			{
				List<String> availableLanguageIds = getAvailableLanguageIds();

				for (String key : jsonObject.keySet()) {
					if (availableLanguageIds.contains(key)) {
						put(key, jsonObject.getString(key));
					}
				}
			}
		};
	}

}