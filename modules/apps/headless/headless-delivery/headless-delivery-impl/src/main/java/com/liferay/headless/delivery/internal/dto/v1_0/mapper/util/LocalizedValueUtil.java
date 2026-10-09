/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.delivery.internal.dto.v1_0.mapper.util;

import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.language.LanguageUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Eudaldo Alonso
 */
public class LocalizedValueUtil {

	public static List<String> getAvailableLanguageIds() {
		return TransformUtil.transform(
			LanguageUtil.getAvailableLocales(), LanguageUtil::getLanguageId);
	}

	public static Map<String, String> toLocalizedValues(JSONObject jsonObject) {
		return new HashMap<String, String>() {
			{
				List<String> availableLanguageIds = getAvailableLanguageIds();

				for (String key : jsonObject.keySet()) {
					if (!availableLanguageIds.contains(key)) {
						continue;
					}

					put(key, jsonObject.getString(key));
				}
			}
		};
	}

}