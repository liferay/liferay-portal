/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.style.book.internal.util;

import com.liferay.frontend.token.definition.util.FrontendTokenDefinitionUtil;
import com.liferay.petra.string.CharPool;
import com.liferay.portal.kernel.json.JSONException;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.style.book.constants.StyleBookConstants;
import com.liferay.style.book.exception.StyleBookEntryFrontendTokensValuesException;
import com.liferay.style.book.util.FrontendTokensValuesUtil;

import java.util.List;

/**
 * @author Thiago Buarque
 */
public class StyleBookEntryFrontendTokensValuesUtil {

	public static String normalizeFrontendTokensValues(
			String customFrontendTokenDefinition, String frontendTokensValues,
			String themeId)
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

		List<String> customFrontendTokenNames =
			FrontendTokenDefinitionUtil.getFrontendTokenNames(
				customFrontendTokenDefinition);

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

				String tokenDefinitionId =
					frontendTokenValueJSONObject.getString("tokenDefinitionId");

				if (Validator.isNull(tokenDefinitionId)) {
					tokenDefinitionId = _getFrontendTokenDefinitionId(
						customFrontendTokenNames, key, themeId);
				}

				String namespacedKey =
					FrontendTokensValuesUtil.getFrontendTokensValuesKey(
						tokenDefinitionId, key);

				if (frontendTokensValuesJSONObject.has(namespacedKey)) {
					continue;
				}

				frontendTokenValueJSONObject.put(
					"tokenDefinitionId", tokenDefinitionId);

				frontendTokensValuesJSONObject.put(
					namespacedKey, frontendTokenValueJSONObject);
			}

			String name = frontendTokenValueJSONObject.getString("name");

			if (Validator.isNull(name) || _isNamespaced(name)) {
				continue;
			}

			frontendTokenValueJSONObject.put(
				"name",
				FrontendTokensValuesUtil.getFrontendTokensValuesKey(
					_getFrontendTokenDefinitionId(
						customFrontendTokenNames, name, themeId),
					name));
		}

		return frontendTokensValuesJSONObject.toString();
	}

	private static String _getFrontendTokenDefinitionId(
		List<String> customFrontendTokenNames, String frontendTokenName,
		String themeId) {

		if (customFrontendTokenNames.contains(frontendTokenName)) {
			return StyleBookConstants.FRONTEND_TOKEN_DEFINITION_ID_CUSTOM;
		}

		return themeId;
	}

	private static boolean _isNamespaced(String frontendTokenName) {
		if (frontendTokenName.indexOf(CharPool.COLON) == -1) {
			return false;
		}

		return true;
	}

}