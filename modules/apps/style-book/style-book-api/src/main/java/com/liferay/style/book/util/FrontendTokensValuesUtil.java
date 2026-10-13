/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.style.book.util;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.util.SetUtil;
import com.liferay.portal.kernel.util.Validator;

import java.util.Map;
import java.util.Set;

/**
 * @author Thiago Buarque
 */
public class FrontendTokensValuesUtil {

	public static String getFrontendTokenValue(
		Map<String, String> defaultFrontendTokenValues,
		JSONObject frontendTokensValuesJSONObject, String key) {

		if (frontendTokensValuesJSONObject == null) {
			return _getDefaultFrontendTokenValue(
				defaultFrontendTokenValues, key);
		}

		JSONObject frontendTokenValueJSONObject =
			frontendTokensValuesJSONObject.getJSONObject(key);

		if (frontendTokenValueJSONObject == null) {
			return _getDefaultFrontendTokenValue(
				defaultFrontendTokenValues, key);
		}

		Set<String> visitedKeys = SetUtil.fromArray(key);

		while (true) {
			String name = frontendTokenValueJSONObject.getString("name");

			if (Validator.isNull(name) || !visitedKeys.add(name)) {
				break;
			}

			JSONObject linkedFrontendTokenValueJSONObject =
				frontendTokensValuesJSONObject.getJSONObject(name);

			if (linkedFrontendTokenValueJSONObject == null) {
				String defaultFrontendTokenValue =
					_getDefaultFrontendTokenValue(
						defaultFrontendTokenValues, name);

				if (defaultFrontendTokenValue != null) {
					return defaultFrontendTokenValue;
				}

				break;
			}

			frontendTokenValueJSONObject = linkedFrontendTokenValueJSONObject;
		}

		return frontendTokenValueJSONObject.getString("value");
	}

	public static String getFrontendTokensValuesKey(
		String frontendTokenDefinitionId, String frontendTokenName) {

		return frontendTokenDefinitionId + StringPool.COLON + frontendTokenName;
	}

	private static String _getDefaultFrontendTokenValue(
		Map<String, String> defaultFrontendTokenValues, String key) {

		if (defaultFrontendTokenValues == null) {
			return null;
		}

		return defaultFrontendTokenValues.get(key);
	}

}