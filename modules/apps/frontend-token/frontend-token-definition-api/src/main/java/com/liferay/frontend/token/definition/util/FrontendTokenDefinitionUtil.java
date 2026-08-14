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
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

	public static JSONObject getMergedFrontendTokenDefinitionJSONObject(
		JSONObject frontendTokenDefinitionJSONObject,
		JSONObject overrideFrontendTokenDefinitionJSONObject) {

		JSONArray overrideFrontendTokenCategoriesJSONArray =
			_getFrontendTokenCategoriesJSONArray(
				_clone(overrideFrontendTokenDefinitionJSONObject));

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

			mergedFrontendTokenDefinitionJSONObject.put(
				"frontendTokenCategories", frontendTokenCategoriesJSONArray);
		}

		_mergeNamedJSONObjects(
			frontendTokenCategoriesJSONArray,
			overrideFrontendTokenCategoriesJSONArray, 0);

		return mergedFrontendTokenDefinitionJSONObject;
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

	private static JSONArray _mergeNamedJSONObject(
		JSONArray jsonArray, JSONObject overrideJSONObject,
		JSONObject jsonObject, int depth) {

		if (depth == _CHILD_ARRAY_KEYS.length) {
			return JSONUtil.replace(jsonArray, "name", overrideJSONObject);
		}

		String childArrayKey = _CHILD_ARRAY_KEYS[depth];

		JSONArray overrideChildJSONArray = overrideJSONObject.getJSONArray(
			childArrayKey);

		if (overrideChildJSONArray == null) {
			return jsonArray;
		}

		JSONArray childJSONArray = jsonObject.getJSONArray(childArrayKey);

		if (childJSONArray == null) {
			childJSONArray = JSONFactoryUtil.createJSONArray();

			jsonObject.put(childArrayKey, childJSONArray);
		}

		jsonObject.put(
			childArrayKey,
			_mergeNamedJSONObjects(
				childJSONArray, overrideChildJSONArray, depth + 1));

		return jsonArray;
	}

	private static JSONArray _mergeNamedJSONObjects(
		JSONArray jsonArray, JSONArray overrideJSONArray, int depth) {

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
				jsonArray.put(overrideJSONObject);

				continue;
			}

			jsonArray = _mergeNamedJSONObject(
				jsonArray, overrideJSONObject, jsonObject, depth);
		}

		return jsonArray;
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

	private static final String[] _CHILD_ARRAY_KEYS = {
		"frontendTokenSets", "frontendTokens"
	};

	private static final Log _log = LogFactoryUtil.getLog(
		FrontendTokenDefinitionUtil.class);

}