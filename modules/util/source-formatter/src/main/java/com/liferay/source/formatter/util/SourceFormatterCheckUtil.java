/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.source.formatter.util;

import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.json.JSONArrayImpl;
import com.liferay.portal.json.JSONObjectImpl;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.source.formatter.check.util.SourceUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * @author Hugo Huijser
 */
public class SourceFormatterCheckUtil {

	public static final String CONFIGURATION_FILE_LOCATION =
		"configuration_file_location";

	public static final String ENABLED_KEY = "enabled";

	public static JSONObject addPropertiesAttributes(
		JSONObject attributesJSONObject, Map<String, Properties> propertiesMap,
		CheckType checkType, String checkName) {

		String[] keyPrefixes = _getKeyPrefixes(checkType, checkName);

		for (Map.Entry<String, Properties> entry : propertiesMap.entrySet()) {
			JSONObject propertiesAttributesJSONObject =
				attributesJSONObject.getJSONObject(entry.getKey());

			if (propertiesAttributesJSONObject == null) {
				propertiesAttributesJSONObject = new JSONObjectImpl();
			}

			Properties properties = entry.getValue();

			for (Object object : properties.keySet()) {
				String key = (String)object;

				String attributeName = null;

				for (String keyPrefix : keyPrefixes) {
					if (!key.startsWith(keyPrefix)) {
						continue;
					}

					attributeName = StringUtil.replaceFirst(
						key, keyPrefix, StringPool.BLANK);
				}

				if (attributeName == null) {
					continue;
				}

				JSONArray jsonArray = new JSONArrayImpl();

				for (String value :
						StringUtil.split(
							properties.getProperty(key), StringPool.COMMA)) {

					jsonArray.put(value);
				}

				propertiesAttributesJSONObject.put(attributeName, jsonArray);
			}

			if (propertiesAttributesJSONObject.length() == 0) {
				continue;
			}

			attributesJSONObject.put(
				entry.getKey(), propertiesAttributesJSONObject);
		}

		return attributesJSONObject;
	}

	public static JSONObject addPropertiesAttributes(
		JSONObject attributesJSONObject, Map<String, Properties> propertiesMap,
		String... keys) {

		for (Map.Entry<String, Properties> entry : propertiesMap.entrySet()) {
			JSONObject propertiesAttributesJSONObject =
				attributesJSONObject.getJSONObject(entry.getKey());

			if (propertiesAttributesJSONObject == null) {
				propertiesAttributesJSONObject = new JSONObjectImpl();
			}

			Properties properties = entry.getValue();

			for (Object object : properties.keySet()) {
				String key = (String)object;

				if (!ArrayUtil.contains(keys, key)) {
					continue;
				}

				JSONArray jsonArray = new JSONArrayImpl();

				for (String value :
						StringUtil.split(
							properties.getProperty(key), StringPool.COMMA)) {

					jsonArray.put(value);
				}

				propertiesAttributesJSONObject.put(key, jsonArray);
			}

			if (propertiesAttributesJSONObject.length() == 0) {
				continue;
			}

			attributesJSONObject.put(
				entry.getKey(), propertiesAttributesJSONObject);
		}

		return attributesJSONObject;
	}

	public static JSONObject getExcludesJSONObject(
		Map<String, Properties> propertiesMap) {

		JSONObject excludesJSONObject = new JSONObjectImpl();

		for (Map.Entry<String, Properties> entry : propertiesMap.entrySet()) {
			JSONObject propertiesExcludesJSONObject = new JSONObjectImpl();

			Properties properties = entry.getValue();

			for (Object object : properties.keySet()) {
				String key = (String)object;

				if (!key.endsWith(".excludes")) {
					continue;
				}

				JSONArray jsonArray = new JSONArrayImpl();

				for (String value :
						StringUtil.split(
							properties.getProperty(key), StringPool.COMMA)) {

					jsonArray.put(value);
				}

				propertiesExcludesJSONObject.put(key, jsonArray);
			}

			if (propertiesExcludesJSONObject.length() == 0) {
				continue;
			}

			excludesJSONObject.put(
				entry.getKey(), propertiesExcludesJSONObject);
		}

		return excludesJSONObject;
	}

	public static String getJSONObjectValue(
		JSONObject jsonObject, Map<String, String> cachedValuesMap, String key,
		String defaultValue, String absolutePath, String baseDirName) {

		return getJSONObjectValue(
			jsonObject, cachedValuesMap, key, defaultValue, absolutePath,
			baseDirName, false);
	}

	public static String getJSONObjectValue(
		JSONObject jsonObject, Map<String, String> cachedValuesMap, String key,
		String defaultValue, String absolutePath, String baseDirName,
		boolean checkConfigurationOnly) {

		if (jsonObject == null) {
			return defaultValue;
		}

		String value = cachedValuesMap.get(key);

		if (value != null) {
			return value;
		}

		boolean cacheValue = true;
		String closestPropertiesFileLocation = null;

		Iterator<String> iterator = jsonObject.keys();

		while (iterator.hasNext()) {
			String fileLocation = iterator.next();

			String curValue = _getJSONObjectValue(
				jsonObject.getJSONObject(fileLocation), key);

			if (curValue == null) {
				continue;
			}

			if (fileLocation.equals(CONFIGURATION_FILE_LOCATION)) {
				if (value == null) {
					value = curValue;
				}

				continue;
			}

			if (checkConfigurationOnly) {
				continue;
			}

			String baseDirNameAbsolutePath = SourceUtil.getAbsolutePath(
				baseDirName);

			if (fileLocation.length() > baseDirNameAbsolutePath.length()) {
				cacheValue = false;
			}

			if (!absolutePath.startsWith(fileLocation) &&
				!fileLocation.equals(baseDirNameAbsolutePath)) {

				continue;
			}

			if ((closestPropertiesFileLocation != null) &&
				(closestPropertiesFileLocation.length() >=
					fileLocation.length())) {

				continue;
			}

			value = curValue;

			closestPropertiesFileLocation = fileLocation;
		}

		if (value == null) {
			value = defaultValue;
		}

		if (cacheValue) {
			cachedValuesMap.put(key, value);
		}

		return value;
	}

	public static List<String> getJSONObjectValues(
		JSONObject jsonObject, String key,
		Map<String, List<String>> cachedValuesMap, String absolutePath,
		String baseDirName) {

		if (jsonObject == null) {
			return Collections.emptyList();
		}

		List<String> values = cachedValuesMap.get(key);

		if (values != null) {
			return values;
		}

		values = new ArrayList<>();

		boolean cacheValues = true;

		Iterator<String> iterator = jsonObject.keys();

		while (iterator.hasNext()) {
			String fileLocation = iterator.next();

			List<String> curValues = _getJSONObjectValues(
				jsonObject.getJSONObject(fileLocation), key);

			if (curValues.isEmpty()) {
				continue;
			}

			if (!fileLocation.equals(CONFIGURATION_FILE_LOCATION)) {
				String baseDirNameAbsolutePath = SourceUtil.getAbsolutePath(
					baseDirName);

				if (fileLocation.length() > baseDirNameAbsolutePath.length()) {
					cacheValues = false;
				}

				if (!absolutePath.startsWith(fileLocation) &&
					!absolutePath.contains(
						SourceFormatterUtil.SOURCE_FORMATTER_TEST_PATH)) {

					continue;
				}
			}

			values.addAll(curValues);
		}

		if (cacheValues) {
			cachedValuesMap.put(key, values);
		}

		return values;
	}

	public static boolean isExcludedPath(
		JSONObject jsonObject, Map<String, List<String>> cachedExcludesMap,
		String key, String path, int lineNumber, String parameter,
		String baseDirName) {

		List<String> excludes = getJSONObjectValues(
			jsonObject, key, cachedExcludesMap, path, baseDirName);

		if (ListUtil.isEmpty(excludes)) {
			return false;
		}

		String pathWithParameter = null;

		if (Validator.isNotNull(parameter)) {
			pathWithParameter = path + StringPool.AT + parameter;
		}

		String pathWithLineNumber = null;

		if (lineNumber > 0) {
			pathWithLineNumber = path + StringPool.AT + lineNumber;
		}

		for (String exclude : excludes) {
			if (Validator.isNull(exclude)) {
				continue;
			}

			if (exclude.startsWith("**")) {
				exclude = exclude.substring(2);
			}

			if (exclude.endsWith("**")) {
				exclude = exclude.substring(0, exclude.length() - 2);

				if (path.contains(exclude)) {
					return true;
				}

				continue;
			}

			if (path.endsWith(exclude) ||
				((pathWithParameter != null) &&
				 pathWithParameter.endsWith(exclude)) ||
				((pathWithLineNumber != null) &&
				 pathWithLineNumber.endsWith(exclude))) {

				return true;
			}
		}

		return false;
	}

	private static String _getJSONObjectValue(
		JSONObject jsonObject, String key) {

		JSONArray jsonArray = jsonObject.getJSONArray(key);

		if ((jsonArray == null) || (jsonArray.length() != 1)) {
			return null;
		}

		return jsonArray.getString(0);
	}

	private static List<String> _getJSONObjectValues(
		JSONObject jsonObject, String key) {

		List<String> values = new ArrayList<>();

		JSONArray jsonArray = jsonObject.getJSONArray(key);

		if (jsonArray != null) {
			for (int i = 0; i < jsonArray.length(); i++) {
				values.add(jsonArray.getString(i));
			}
		}

		return values;
	}

	private static String[] _getKeyPrefixes(
		CheckType checkType, String checkName) {

		String checkTypeName = checkType.getValue();

		checkTypeName = checkTypeName.replaceAll("([a-z])([A-Z])", "$1.$2");

		checkTypeName = checkTypeName.replaceAll(
			"([A-Z])([A-Z][a-z])", "$1.$2");

		checkTypeName = StringUtil.toLowerCase(checkTypeName);

		String formattedCheckName = checkName.replaceAll(
			"([a-z])([A-Z])", "$1.$2");

		formattedCheckName = StringUtil.toLowerCase(
			formattedCheckName.replaceAll("([A-Z])([A-Z][a-z])", "$1.$2"));

		return new String[] {
			StringBundler.concat(checkTypeName, ".", checkName, "."),
			StringBundler.concat(checkTypeName, ".", formattedCheckName, ".")
		};
	}

}