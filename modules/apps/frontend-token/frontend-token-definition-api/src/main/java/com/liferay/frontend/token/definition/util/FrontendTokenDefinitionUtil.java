/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.frontend.token.definition.util;

import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONException;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.Validator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Gabriel Lima
 */
public class FrontendTokenDefinitionUtil {

	public static List<String> getFrontendTokenNames(
		String frontendTokenDefinition) {

		JSONObject frontendTokenDefinitionJSONObject = _parse(
			frontendTokenDefinition);

		if (frontendTokenDefinitionJSONObject == null) {
			return Collections.emptyList();
		}

		JSONArray frontendTokenCategoriesJSONArray =
			frontendTokenDefinitionJSONObject.getJSONArray(
				"frontendTokenCategories");

		if (frontendTokenCategoriesJSONArray == null) {
			return Collections.emptyList();
		}

		List<String> frontendTokenNames = new ArrayList<>();

		for (int i = 0; i < frontendTokenCategoriesJSONArray.length(); i++) {
			JSONObject frontendTokenCategoryJSONObject =
				frontendTokenCategoriesJSONArray.getJSONObject(i);

			if (frontendTokenCategoryJSONObject == null) {
				continue;
			}

			_collectFrontendTokenNames(
				frontendTokenCategoryJSONObject, frontendTokenNames);
		}

		return frontendTokenNames;
	}

	private static void _collectFrontendTokenNames(
		JSONObject frontendTokenCategoryJSONObject,
		List<String> frontendTokenNames) {

		JSONArray frontendTokenSetsJSONArray =
			frontendTokenCategoryJSONObject.getJSONArray("frontendTokenSets");

		if (frontendTokenSetsJSONArray == null) {
			return;
		}

		for (int i = 0; i < frontendTokenSetsJSONArray.length(); i++) {
			JSONObject frontendTokenSetJSONObject =
				frontendTokenSetsJSONArray.getJSONObject(i);

			if (frontendTokenSetJSONObject == null) {
				continue;
			}

			JSONArray frontendTokensJSONArray =
				frontendTokenSetJSONObject.getJSONArray("frontendTokens");

			if (frontendTokensJSONArray == null) {
				continue;
			}

			for (int j = 0; j < frontendTokensJSONArray.length(); j++) {
				JSONObject frontendTokenJSONObject =
					frontendTokensJSONArray.getJSONObject(j);

				if (frontendTokenJSONObject == null) {
					continue;
				}

				frontendTokenNames.add(
					frontendTokenJSONObject.getString("name"));
			}
		}
	}

	private static JSONObject _parse(String frontendTokenDefinition) {
		if (Validator.isNull(frontendTokenDefinition)) {
			return null;
		}

		try {
			return JSONFactoryUtil.createJSONObject(frontendTokenDefinition);
		}
		catch (JSONException jsonException) {
			if (_log.isWarnEnabled()) {
				_log.warn(
					"Unable to parse frontend token definition", jsonException);
			}

			return null;
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		FrontendTokenDefinitionUtil.class);

}