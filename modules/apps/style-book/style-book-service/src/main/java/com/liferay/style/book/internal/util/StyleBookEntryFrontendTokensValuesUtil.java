/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.style.book.internal.util;

import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.json.JSONException;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.style.book.exception.StyleBookEntryFrontendTokensValuesException;

/**
 * @author Thiago Buarque
 */
public class StyleBookEntryFrontendTokensValuesUtil {

	public static String normalizeFrontendTokensValues(
			String frontendTokensValues, String themeId)
		throws StyleBookEntryFrontendTokensValuesException {

		if (Validator.isBlank(frontendTokensValues) ||
			Validator.isBlank(themeId)) {

			return frontendTokensValues;
		}

		JSONObject frontendTokensValuesJSONObject = null;

		try {
			frontendTokensValuesJSONObject = JSONFactoryUtil.createJSONObject(
				frontendTokensValues);
		}
		catch (JSONException jsonException) {
			throw new StyleBookEntryFrontendTokensValuesException.
				MustBeValidJSON(jsonException);
		}

		for (String key :
				ListUtil.fromCollection(
					frontendTokensValuesJSONObject.keySet())) {

			JSONObject frontendTokenValueJSONObject =
				frontendTokensValuesJSONObject.getJSONObject(key);

			if (frontendTokenValueJSONObject == null) {
				continue;
			}

			if (!_isNamespaced(key)) {
				frontendTokensValuesJSONObject.remove(key);

				String namespacedKey = themeId + StringPool.COLON + key;

				if (frontendTokensValuesJSONObject.has(namespacedKey)) {
					continue;
				}

				if (!frontendTokenValueJSONObject.has("tokenDefinitionId")) {
					frontendTokenValueJSONObject.put(
						"tokenDefinitionId", themeId);
				}

				frontendTokensValuesJSONObject.put(
					namespacedKey, frontendTokenValueJSONObject);
			}

			String name = frontendTokenValueJSONObject.getString("name");

			if (Validator.isNotNull(name) && !_isNamespaced(name)) {
				frontendTokenValueJSONObject.put(
					"name", themeId + StringPool.COLON + name);
			}
		}

		return frontendTokensValuesJSONObject.toString();
	}

	private static boolean _isNamespaced(String frontendTokenName) {
		if (frontendTokenName.indexOf(CharPool.COLON) == -1) {
			return false;
		}

		return true;
	}

}