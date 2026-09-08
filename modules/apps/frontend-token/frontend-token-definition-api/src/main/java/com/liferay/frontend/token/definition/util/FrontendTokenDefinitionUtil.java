/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.frontend.token.definition.util;

import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONException;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.Validator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Gabriel Lima
 * @author Thiago Buarque
 */
public class FrontendTokenDefinitionUtil {

	public static List<String> getFrontendTokenNames(
		String frontendTokenDefinition) {

		JSONObject frontendTokenDefinitionJSONObject =
			parseFrontendTokenDefinitionJSONObject(frontendTokenDefinition);

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

	public static JSONObject mergeFrontendTokenDefinitionJSONObject(
		JSONObject frontendTokenDefinitionJSONObject,
		JSONObject overrideFrontendTokenDefinitionJSONObject) {

		JSONArray overrideFrontendTokenCategoriesJSONArray =
			_getFrontendTokenCategoriesJSONArray(
				overrideFrontendTokenDefinitionJSONObject);

		if (JSONUtil.isEmpty(overrideFrontendTokenCategoriesJSONArray)) {
			return frontendTokenDefinitionJSONObject;
		}

		JSONObject mergedFrontendTokenDefinitionJSONObject = _clone(
			frontendTokenDefinitionJSONObject);

		JSONArray frontendTokenCategoriesJSONArray =
			_getFrontendTokenCategoriesJSONArray(
				mergedFrontendTokenDefinitionJSONObject);

		if (frontendTokenCategoriesJSONArray == null) {
			frontendTokenCategoriesJSONArray =
				JSONFactoryUtil.createJSONArray();
		}

		JSONArray mergedFrontendTokenCategoriesJSONArray =
			_mergeJSONArraysByName(
				frontendTokenCategoriesJSONArray,
				overrideFrontendTokenCategoriesJSONArray, "frontendTokenSets",
				"frontendTokens");

		mergedFrontendTokenDefinitionJSONObject.put(
			"frontendTokenCategories", mergedFrontendTokenCategoriesJSONArray);

		return mergedFrontendTokenDefinitionJSONObject;
	}

	public static JSONObject parseFrontendTokenDefinitionJSONObject(
		String frontendTokenDefinition) {

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

	private static JSONObject _clone(JSONObject jsonObject) {
		if (jsonObject == null) {
			return JSONFactoryUtil.createJSONObject();
		}

		return JSONFactoryUtil.createJSONObject(jsonObject.toMap());
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

	private static JSONArray _getFrontendTokenCategoriesJSONArray(
		JSONObject frontendTokenDefinitionJSONObject) {

		if (frontendTokenDefinitionJSONObject == null) {
			return null;
		}

		return frontendTokenDefinitionJSONObject.getJSONArray(
			"frontendTokenCategories");
	}

	private static void _mergeChildJSONArraysByName(
		JSONObject jsonObject, JSONObject overrideJSONObject,
		String... childArrayKeys) {

		String childArrayKey = childArrayKeys[0];

		JSONArray overrideChildJSONArray = overrideJSONObject.getJSONArray(
			childArrayKey);

		if (overrideChildJSONArray == null) {
			return;
		}

		JSONArray childJSONArray = jsonObject.getJSONArray(childArrayKey);

		if (childJSONArray == null) {
			childJSONArray = JSONFactoryUtil.createJSONArray();
		}

		jsonObject.put(
			childArrayKey,
			_mergeJSONArraysByName(
				childJSONArray, overrideChildJSONArray,
				Arrays.copyOfRange(childArrayKeys, 1, childArrayKeys.length)));
	}

	private static JSONArray _mergeJSONArraysByName(
		JSONArray jsonArray, JSONArray overrideJSONArray,
		String... childArrayKeys) {

		Map<String, JSONObject> jsonObjectsByName = new HashMap<>();

		for (int i = 0; i < jsonArray.length(); i++) {
			JSONObject jsonObject = jsonArray.getJSONObject(i);

			if (jsonObject != null) {
				jsonObjectsByName.put(jsonObject.getString("name"), jsonObject);
			}
		}

		for (int i = 0; i < overrideJSONArray.length(); i++) {
			JSONObject overrideJSONObject = overrideJSONArray.getJSONObject(i);

			if (overrideJSONObject == null) {
				continue;
			}

			JSONObject jsonObject = jsonObjectsByName.get(
				overrideJSONObject.getString("name"));

			if (jsonObject == null) {
				jsonArray.put(_clone(overrideJSONObject));
			}
			else if (childArrayKeys.length == 0) {
				jsonArray = JSONUtil.replace(
					jsonArray, "name", _clone(overrideJSONObject));
			}
			else {
				_mergeChildJSONArraysByName(
					jsonObject, overrideJSONObject, childArrayKeys);
			}
		}

		return jsonArray;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		FrontendTokenDefinitionUtil.class);

}