/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.site.resource.v1_0.test.util;

import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;

import java.util.Map;

/**
 * @author Lourdes Fernández Besada
 */
public class FragmentConfigurationTestUtil {

	public static String getConfiguration(
		Map<String, Map<String, Object>> fieldsMap) {

		JSONArray fieldsJSONArray = JSONFactoryUtil.createJSONArray();

		for (Map.Entry<String, Map<String, Object>> entry :
				fieldsMap.entrySet()) {

			Map<String, Object> value = entry.getValue();

			fieldsJSONArray.put(
				_getFragmentConfigurationFieldJSONObject(
					value.get("defaultValue"),
					GetterUtil.getBoolean(value.get("localizable")),
					entry.getKey(), GetterUtil.getString(value.get("type")),
					(JSONObject)value.get("typeOptions")));
		}

		return JSONUtil.put(
			"fieldSets",
			JSONUtil.put(
				JSONUtil.put(
					"fields", fieldsJSONArray
				).put(
					"label", RandomTestUtil.randomString()
				))
		).toString();
	}

	private static JSONObject _getFragmentConfigurationFieldJSONObject(
		Object defaultValue, boolean localizable, String name, String type,
		JSONObject typeOptionsJSONObject) {

		return JSONUtil.put(
			"dataType", _fragmentConfigurationFieldDataTypesMap.get(type)
		).put(
			"defaultValue", defaultValue
		).put(
			"description", RandomTestUtil.randomString()
		).put(
			"label", RandomTestUtil.randomString()
		).put(
			"localizable", localizable
		).put(
			"name", name
		).put(
			"type", type
		).put(
			"typeOptions", typeOptionsJSONObject
		);
	}

	private static final Map<String, String>
		_fragmentConfigurationFieldDataTypesMap = HashMapBuilder.put(
			"categoryTreeNodeSelector", "object"
		).put(
			"checkbox", "string"
		).put(
			"collectionSelector", "object"
		).put(
			"itemSelector", "object"
		).put(
			"length", "string"
		).put(
			"navigationMenuSelector", "object"
		).put(
			"select", "string"
		).put(
			"text", "string"
		).build();

}