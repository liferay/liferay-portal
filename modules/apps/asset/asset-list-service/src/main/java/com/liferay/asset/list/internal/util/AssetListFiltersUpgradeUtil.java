/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.asset.list.internal.util;

import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.UnicodeProperties;
import com.liferay.portal.kernel.util.UnicodePropertiesBuilder;
import com.liferay.portal.kernel.util.Validator;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @author Joshua Cords
 */
public class AssetListFiltersUpgradeUtil {

	public static String toUpgradedTypeSettings(String typeSettings) {
		if (Validator.isNull(typeSettings)) {
			return null;
		}

		UnicodeProperties unicodeProperties = UnicodePropertiesBuilder.fastLoad(
			typeSettings
		).build();

		String[] keys = ArrayUtil.toStringArray(unicodeProperties.keySet());

		if (!ArrayUtil.exists(
				keys, key -> StringUtil.startsWith(key, "queryName"))) {

			return null;
		}

		Map<String, JSONObject> filterJSONObjects = new LinkedHashMap<>();

		for (int i = 0; true; i++) {
			String[] queryValues = StringUtil.split(
				unicodeProperties.getProperty("queryValues" + i, null));

			if (ArrayUtil.isEmpty(queryValues)) {
				break;
			}

			String propertyName = unicodeProperties.getProperty(
				"queryName" + i, StringPool.BLANK);

			if (!Objects.equals(propertyName, "assetCategories") &&
				!Objects.equals(propertyName, "keywords")) {

				propertyName = "assetTags";
			}

			boolean queryAndOperator = GetterUtil.getBoolean(
				unicodeProperties.getProperty(
					"queryAndOperator" + i, StringPool.BLANK));
			boolean queryContains = GetterUtil.getBoolean(
				unicodeProperties.getProperty(
					"queryContains" + i, StringPool.BLANK));

			filterJSONObjects.put(
				StringBundler.concat(
					propertyName, StringPool.POUND, queryContains,
					StringPool.POUND, queryAndOperator),
				JSONUtil.put(
					"operatorName", queryContains ? "contains" : "not-contains"
				).put(
					"propertyName", propertyName
				).put(
					"quantifier", queryAndOperator ? "all" : "any"
				).put(
					"value", _toValue(propertyName, queryValues)
				));
		}

		JSONArray filtersJSONArray = JSONFactoryUtil.createJSONArray();

		String filtersJSON = unicodeProperties.getProperty("filters");

		if (Validator.isNotNull(filtersJSON)) {
			try {
				filtersJSONArray = JSONFactoryUtil.createJSONArray(filtersJSON);
			}
			catch (Exception exception) {
				if (_log.isDebugEnabled()) {
					_log.debug(exception);
				}
			}
		}

		for (JSONObject filterJSONObject : filterJSONObjects.values()) {
			filtersJSONArray.put(filterJSONObject);
		}

		unicodeProperties.setProperty("filters", filtersJSONArray.toString());

		for (String key : keys) {
			if (!StringUtil.startsWith(key, "queryAndOperator") &&
				!StringUtil.startsWith(key, "queryContains") &&
				!StringUtil.startsWith(key, "queryName") &&
				!StringUtil.startsWith(key, "queryValues")) {

				continue;
			}

			unicodeProperties.remove(key);
		}

		return unicodeProperties.toString();
	}

	private static Object _toValue(String propertyName, String[] queryValues) {
		if (Objects.equals(propertyName, "keywords")) {
			String[] keywords = new String[queryValues.length];

			for (int i = 0; i < queryValues.length; i++) {
				String keyword = queryValues[i];

				if (keyword.contains(StringPool.SPACE)) {
					keyword = StringUtil.quote(keyword, CharPool.QUOTE);
				}

				keywords[i] = keyword;
			}

			return StringUtil.merge(keywords, StringPool.SPACE);
		}

		return JSONUtil.toJSONArray(
			queryValues, queryValue -> JSONUtil.put("value", queryValue), _log);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		AssetListFiltersUpgradeUtil.class);

}