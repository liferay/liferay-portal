/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.opensearch2.internal.util;

import com.liferay.petra.io.unsync.UnsyncStringReader;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.StringUtil;

import jakarta.json.spi.JsonProvider;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.opensearch.client.json.JsonpMapper;
import org.opensearch.client.opensearch._types.mapping.DynamicTemplate;
import org.opensearch.client.opensearch._types.mapping.Property;

/**
 * @author Petteri Karttunen
 */
public class IndexUtil {

	public static List<Map<String, DynamicTemplate>> getDynamicTemplatesMap(
		JSONObject mappingsJSONObject) {

		JSONArray dynamicTemplatesJSONArray = mappingsJSONObject.getJSONArray(
			"dynamic_templates");

		if (dynamicTemplatesJSONArray == null) {
			return null;
		}

		JsonpMapper jsonpMapper = JsonpUtil.getJsonpMapper();

		JsonProvider jsonProvider = jsonpMapper.jsonProvider();

		List<Map<String, DynamicTemplate>> dynamicTemplates = new ArrayList<>();

		for (int i = 0; i < dynamicTemplatesJSONArray.length(); i++) {
			JSONObject dynamicTemplateJSONObject =
				dynamicTemplatesJSONArray.getJSONObject(i);

			for (String dynamicTemplateName :
					dynamicTemplateJSONObject.keySet()) {

				JSONObject templateJSONObject =
					dynamicTemplateJSONObject.getJSONObject(
						dynamicTemplateName);

				_convertOpenSearchDynamicTemplate(templateJSONObject);

				dynamicTemplates.add(
					HashMapBuilder.<String, DynamicTemplate>put(
						dynamicTemplateName,
						DynamicTemplate._DESERIALIZER.deserialize(
							jsonProvider.createParser(
								new UnsyncStringReader(
									templateJSONObject.toString())),
							jsonpMapper)
					).build());
			}
		}

		return dynamicTemplates;
	}

	public static Map<String, Property> getPropertiesMap(
		JSONObject mappingsJSONObject) {

		JSONObject propertiesJSONObject = mappingsJSONObject.getJSONObject(
			"properties");

		if (propertiesJSONObject == null) {
			return null;
		}

		JsonpMapper jsonpMapper = JsonpUtil.getJsonpMapper();

		JsonProvider jsonProvider = jsonpMapper.jsonProvider();

		Map<String, Property> properties = new HashMap<>();

		for (String fieldName : propertiesJSONObject.keySet()) {
			properties.put(
				fieldName,
				Property._DESERIALIZER.deserialize(
					jsonProvider.createParser(
						new UnsyncStringReader(
							String.valueOf(
								propertiesJSONObject.get(fieldName)))),
					jsonpMapper));
		}

		return properties;
	}

	public static JSONArray mergeDynamicTemplates(
		JSONArray jsonArray1, JSONArray jsonArray2) {

		Map<String, JSONObject> map = new LinkedHashMap<>();

		_putAll(jsonArray1, map);
		_putAll(jsonArray2, map);

		JSONArray jsonArray3 = JSONFactoryUtil.createJSONArray();

		JSONObject defaultTemplateJSONObject = null;

		for (Map.Entry<String, JSONObject> entry : map.entrySet()) {
			String key = entry.getKey();

			if (key.equals("template_")) {
				defaultTemplateJSONObject = entry.getValue();
			}
			else {
				jsonArray3.put(entry.getValue());
			}
		}

		if (defaultTemplateJSONObject != null) {
			jsonArray3.put(defaultTemplateJSONObject);
		}

		return jsonArray3;
	}

	public static void mergeToJSONObject(
		JSONObject jsonObject, JSONObject mergeJSONObject) {

		if ((jsonObject == null) || (mergeJSONObject == null)) {
			return;
		}

		Iterator<String> iterator = mergeJSONObject.keys();

		while (iterator.hasNext()) {
			String key = iterator.next();

			Object object1 = jsonObject.get(key);
			Object object2 = mergeJSONObject.get(key);

			if ((object1 instanceof JSONObject) &&
				(object2 instanceof JSONObject)) {

				mergeToJSONObject((JSONObject)object1, (JSONObject)object2);
			}
			else if (key.equals("dynamic_templates") &&
					 (object1 instanceof JSONArray) &&
					 (object2 instanceof JSONArray)) {

				jsonObject.put(
					key,
					mergeDynamicTemplates(
						(JSONArray)object1, (JSONArray)object2));
			}
			else {
				jsonObject.put(key, mergeJSONObject.get(key));
			}
		}
	}

	private static void _convertOpenSearchDynamicTemplate(
		JSONObject templateJSONObject) {

		JSONObject mappingJSONObject = templateJSONObject.getJSONObject(
			"mapping");

		if (mappingJSONObject.has("dims")) {
			int dims = mappingJSONObject.getInt("dims");

			mappingJSONObject.remove("dims");
			mappingJSONObject.put("dimension", dims);
		}

		String type = mappingJSONObject.getString("type");

		if (StringUtil.equals(type, "dense_vector")) {
			mappingJSONObject.put("type", "knn_vector");
		}
	}

	private static void _putAll(
		JSONArray jsonArray, Map<String, JSONObject> map) {

		for (int i = 0; i < jsonArray.length(); i++) {
			JSONObject jsonObject = jsonArray.getJSONObject(i);

			JSONArray namesJSONArray = jsonObject.names();

			map.put((String)namesJSONArray.get(0), jsonObject);
		}
	}

}