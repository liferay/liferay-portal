/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.source.formatter.check;

import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.json.JSONArrayImpl;
import com.liferay.portal.json.JSONObjectImpl;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.source.formatter.check.util.JSONSourceUtil;
import com.liferay.source.formatter.util.FileUtil;

import java.io.File;

import java.util.Comparator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Qi Zhang
 */
public class JSONBatchEngineDataFileCheck extends BaseFileCheck {

	@Override
	public boolean isLiferaySourceCheck() {
		return true;
	}

	@Override
	protected String doProcess(
			String fileName, String absolutePath, String content)
		throws Exception {

		if (!absolutePath.endsWith(".batch-engine-data.json")) {
			return content;
		}

		JSONObject jsonObject = new JSONObjectImpl(content);

		_formatItems(fileName, absolutePath, jsonObject);
		_removeConfigurationDefaultValues(absolutePath, jsonObject);
		_removeRootDefaultValues(absolutePath, jsonObject);

		return JSONUtil.toString(jsonObject);
	}

	private void _checkClassName(
			String fileName, String absolutePath, JSONObject jsonObject)
		throws Exception {

		String className = jsonObject.getString("className");

		if (!className.startsWith(
				"com.liferay.object.model.ObjectDefinition#")) {

			return;
		}

		List<String> allowedUnmatchedClassNameFileNames = getAttributeValues(
			_ALLOWED_UNMATCHED_CLASS_NAME_FILE_NAMES_KEY, absolutePath);

		for (String allowedUnmatchedClassNameFileName :
				allowedUnmatchedClassNameFileNames) {

			if (absolutePath.endsWith(allowedUnmatchedClassNameFileName)) {
				return;
			}
		}

		String classNameRegex = _getClassNameRegex();

		if ((classNameRegex == null) || className.matches(classNameRegex)) {
			return;
		}

		addMessage(
			fileName,
			"\"className\" does not match the pattern specified by \"_class" +
				"NamePattern\" in \"ObjectDefinitionClassNameProcessorImpl\"");
	}

	private void _formatItems(
			String fileName, String absolutePath, JSONObject jsonObject)
		throws Exception {

		JSONArray itemsJSONArray = jsonObject.getJSONArray("items");

		if (itemsJSONArray == null) {
			return;
		}

		List<Object> objects = JSONUtil.toObjectList(itemsJSONArray);

		itemsJSONArray = new JSONArrayImpl();

		List<String> allItemsDefaultValues = getAttributeValues(
			_ALL_ITEMS_DEFAULT_VALUES_KEY, absolutePath);
		List<String> objectDefinitionItemsDefaultValues = getAttributeValues(
			_OBJECT_DEFINITION_ITEMS_DEFAULT_VALUES_KEY, absolutePath);

		boolean objectDefinition = _isObjectDefinition(jsonObject);

		for (Object object : objects) {
			JSONObject itemJSONObject = (JSONObject)object;

			_removeDefaultValues(allItemsDefaultValues, itemJSONObject);

			if (objectDefinition) {
				_checkClassName(fileName, absolutePath, itemJSONObject);
				_removeDefaultValues(
					objectDefinitionItemsDefaultValues, itemJSONObject);
				_sortObjectFields(itemJSONObject);
			}

			itemsJSONArray.put(itemJSONObject);
		}

		jsonObject.put("items", itemsJSONArray);
	}

	private synchronized String _getClassNameRegex() throws Exception {
		if (_classNameRegex != null) {
			return _classNameRegex;
		}

		File portalDir = getPortalDir();

		if (portalDir == null) {
			return null;
		}

		File file = new File(
			portalDir,
			"/modules/apps/object/object-service/src/main/java/com/liferay" +
				"/object/internal/definition/processor" +
					"/ObjectDefinitionClassNameProcessorImpl.java");

		if (!file.exists()) {
			return null;
		}

		Matcher matcher = _classNamePatternDefinitionPattern.matcher(
			FileUtil.read(file));

		if (!matcher.find()) {
			return null;
		}

		StringBundler sb = new StringBundler();

		matcher = _stringLiteralPattern.matcher(matcher.group(1));

		while (matcher.find()) {
			sb.append(matcher.group(1));
		}

		_classNameRegex = StringUtil.replace(sb.toString(), "\\\\", "\\");

		return _classNameRegex;
	}

	private boolean _isObjectDefinition(JSONObject jsonObject) {
		JSONObject configurationJSONObject = jsonObject.getJSONObject(
			"configuration");

		if ((configurationJSONObject != null) &&
			StringUtil.equals(
				configurationJSONObject.getString("className"),
				"com.liferay.object.admin.rest.dto.v1_0.ObjectDefinition")) {

			return true;
		}

		return false;
	}

	private void _removeConfigurationDefaultValues(
		String absolutePath, JSONObject jsonObject) {

		JSONObject configurationJSONObject = jsonObject.getJSONObject(
			"configuration");

		if (configurationJSONObject == null) {
			return;
		}

		List<String> configurationDefaultValues = getAttributeValues(
			_CONFIGURATION_DEFAULT_VALUES_KEY, absolutePath);

		_removeDefaultValues(
			configurationDefaultValues, configurationJSONObject);

		jsonObject.put("configuration", configurationJSONObject);
	}

	private void _removeDefaultValues(
		List<String> defaultValues, JSONObject jsonObject) {

		for (String defaultValue : defaultValues) {
			String[] parts = StringUtil.split(defaultValue, CharPool.COLON);

			if (parts.length != 2) {
				continue;
			}

			String key = parts[0];

			if (!jsonObject.has(key)) {
				continue;
			}

			String value = parts[1];

			if (value.equals(StringPool.STAR) ||
				value.equals(String.valueOf(jsonObject.get(key)))) {

				jsonObject.remove(key);
			}
		}
	}

	private void _removeRootDefaultValues(
		String absolutePath, JSONObject jsonObject) {

		List<String> rootDefaultValues = getAttributeValues(
			_ROOT_DEFAULT_VALUES_KEY, absolutePath);

		_removeDefaultValues(rootDefaultValues, jsonObject);
	}

	private void _sortObjectFields(JSONObject jsonObject) {
		JSONArray objectFieldsJSONArray = jsonObject.getJSONArray(
			"objectFields");

		if (objectFieldsJSONArray == null) {
			return;
		}

		jsonObject.put(
			"objectFields",
			JSONSourceUtil.sortJSONArray(
				objectFieldsJSONArray, new ObjectFieldComparator()));
	}

	private static final String _ALL_ITEMS_DEFAULT_VALUES_KEY =
		"allItemsDefaultValues";

	private static final String _ALLOWED_UNMATCHED_CLASS_NAME_FILE_NAMES_KEY =
		"allowedUnmatchedClassNameFileNames";

	private static final String _CONFIGURATION_DEFAULT_VALUES_KEY =
		"configurationDefaultValues";

	private static final String _OBJECT_DEFINITION_ITEMS_DEFAULT_VALUES_KEY =
		"objectDefinitionItemsDefaultValues";

	private static final String _ROOT_DEFAULT_VALUES_KEY = "rootDefaultValues";

	private static final Pattern _classNamePatternDefinitionPattern =
		Pattern.compile(
			"_classNamePattern\\s*=\\s*Pattern\\.compile\\(([^;]*)\\);");
	private static final Pattern _stringLiteralPattern = Pattern.compile(
		"\"((?:\\\\.|[^\"\\\\])*)\"");

	private String _classNameRegex;

	private static class ObjectFieldComparator implements Comparator<Object> {

		@Override
		public int compare(Object object1, Object object2) {
			JSONObject jsonObject1 = (JSONObject)object1;
			JSONObject jsonObject2 = (JSONObject)object2;

			boolean relationship1 = _isRelationship(jsonObject1);
			boolean relationship2 = _isRelationship(jsonObject2);

			if (relationship1 != relationship2) {
				if (relationship1) {
					return 1;
				}

				return -1;
			}

			String name1 = jsonObject1.getString("name");
			String name2 = jsonObject2.getString("name");

			return name1.compareTo(name2);
		}

		private boolean _isRelationship(JSONObject jsonObject) {
			String businessType = jsonObject.getString("businessType");

			if (businessType.equals("Relationship")) {
				return true;
			}

			String name = jsonObject.getString("name");

			if (Validator.isNotNull(name) && name.startsWith("r_")) {
				return true;
			}

			return false;
		}

	}

}