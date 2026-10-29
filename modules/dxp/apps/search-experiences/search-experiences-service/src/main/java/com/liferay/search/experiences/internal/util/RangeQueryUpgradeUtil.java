/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.search.experiences.internal.util;

import com.liferay.petra.string.CharPool;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONException;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;

/**
 * @author Rodrigo Guedes de Souza
 */
public class RangeQueryUpgradeUtil {

	public static boolean upgradeRangeQueries(JSONArray jsonArray) {
		boolean upgraded = false;

		for (int i = 0; i < jsonArray.length(); i++) {
			if (!_upgradeNestedRangeQueries(jsonArray.get(i))) {
				continue;
			}

			upgraded = true;
		}

		return upgraded;
	}

	public static boolean upgradeRangeQueries(JSONObject jsonObject) {
		boolean upgraded = false;

		for (String key : jsonObject.keySet()) {
			Object value = jsonObject.get(key);

			if (key.equals("range") && (value instanceof JSONObject)) {
				if (_upgradeRangeQuery((JSONObject)value)) {
					upgraded = true;
				}
			}
			else if (value instanceof String) {
				String json = _upgradeJSON((String)value);

				if (json == null) {
					continue;
				}

				jsonObject.put(key, json);

				upgraded = true;
			}
			else if (_upgradeNestedRangeQueries(value)) {
				upgraded = true;
			}
		}

		return upgraded;
	}

	public static String upgradeRangeQueries(String json) {
		String upgradedJSON = _upgradeJSON(json);

		if (upgradedJSON == null) {
			return json;
		}

		return upgradedJSON;
	}

	private static String _upgradeJSON(String json) {
		if (Validator.isNull(json)) {
			return null;
		}

		try {
			String trimmedJSON = json.trim();

			if (StringUtil.startsWith(trimmedJSON, CharPool.OPEN_BRACKET)) {
				JSONArray jsonArray = JSONFactoryUtil.createJSONArray(json);

				if (upgradeRangeQueries(jsonArray)) {
					return jsonArray.toString();
				}
			}
			else if (StringUtil.startsWith(
						trimmedJSON, CharPool.OPEN_CURLY_BRACE)) {

				JSONObject jsonObject = JSONFactoryUtil.createJSONObject(json);

				if (upgradeRangeQueries(jsonObject)) {
					return jsonObject.toString();
				}
			}
		}
		catch (JSONException jsonException) {
			if (_log.isDebugEnabled()) {
				_log.debug(jsonException);
			}
		}

		return null;
	}

	private static boolean _upgradeNestedRangeQueries(Object value) {
		if (value instanceof JSONArray) {
			return upgradeRangeQueries((JSONArray)value);
		}

		if (value instanceof JSONObject) {
			return upgradeRangeQueries((JSONObject)value);
		}

		return false;
	}

	private static boolean _upgradeRangeQuery(JSONObject rangeJSONObject) {
		boolean upgraded = false;

		for (String key : rangeJSONObject.keySet()) {
			JSONObject fieldJSONObject = rangeJSONObject.getJSONObject(key);

			if ((fieldJSONObject == null) ||
				(!fieldJSONObject.has("from") &&
				 !fieldJSONObject.has("include_lower") &&
				 !fieldJSONObject.has("include_upper") &&
				 !fieldJSONObject.has("to"))) {

				continue;
			}

			_upgradeRangeQueryBound(
				"gt", fieldJSONObject,
				fieldJSONObject.getBoolean("include_lower", true), "gte",
				"from");
			_upgradeRangeQueryBound(
				"lt", fieldJSONObject,
				fieldJSONObject.getBoolean("include_upper", true), "lte", "to");

			fieldJSONObject.remove("include_lower");
			fieldJSONObject.remove("include_upper");

			upgraded = true;
		}

		return upgraded;
	}

	private static void _upgradeRangeQueryBound(
		String exclusiveKey, JSONObject fieldJSONObject, boolean inclusive,
		String inclusiveKey, String key) {

		if (!fieldJSONObject.has(key)) {
			return;
		}

		Object value = null;

		if (!fieldJSONObject.isNull(key)) {
			value = fieldJSONObject.get(key);
		}

		fieldJSONObject.remove(key);

		if ((value == null) || fieldJSONObject.has(exclusiveKey) ||
			fieldJSONObject.has(inclusiveKey)) {

			return;
		}

		if (inclusive) {
			fieldJSONObject.put(inclusiveKey, value);
		}
		else {
			fieldJSONObject.put(exclusiveKey, value);
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		RangeQueryUpgradeUtil.class);

}