/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.data.engine.rest.client.serdes.v2_0;

import com.liferay.data.engine.rest.client.dto.v2_0.DataLayoutRenderingContext;
import com.liferay.data.engine.rest.client.json.BaseJSONParser;

import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;

import jakarta.annotation.Generated;

/**
 * @author Jeyvison Nascimento
 * @generated
 */
@Generated("")
public class DataLayoutRenderingContextSerDes {

	public static DataLayoutRenderingContext toDTO(String json) {
		DataLayoutRenderingContextJSONParser
			dataLayoutRenderingContextJSONParser =
				new DataLayoutRenderingContextJSONParser();

		return dataLayoutRenderingContextJSONParser.parseToDTO(json);
	}

	public static DataLayoutRenderingContext[] toDTOs(String json) {
		DataLayoutRenderingContextJSONParser
			dataLayoutRenderingContextJSONParser =
				new DataLayoutRenderingContextJSONParser();

		return dataLayoutRenderingContextJSONParser.parseToDTOs(json);
	}

	public static String toJSON(
		DataLayoutRenderingContext dataLayoutRenderingContext) {

		if (dataLayoutRenderingContext == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (dataLayoutRenderingContext.getContainerId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"containerId\": ");

			sb.append("\"");

			sb.append(_escape(dataLayoutRenderingContext.getContainerId()));

			sb.append("\"");
		}

		if (dataLayoutRenderingContext.getDataRecordValues() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"dataRecordValues\": ");

			sb.append(
				_toJSON(dataLayoutRenderingContext.getDataRecordValues()));
		}

		if (dataLayoutRenderingContext.getNamespace() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"namespace\": ");

			sb.append("\"");

			sb.append(_escape(dataLayoutRenderingContext.getNamespace()));

			sb.append("\"");
		}

		if (dataLayoutRenderingContext.getPathThemeImages() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"pathThemeImages\": ");

			sb.append("\"");

			sb.append(_escape(dataLayoutRenderingContext.getPathThemeImages()));

			sb.append("\"");
		}

		if (dataLayoutRenderingContext.getReadOnly() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"readOnly\": ");

			sb.append(dataLayoutRenderingContext.getReadOnly());
		}

		if (dataLayoutRenderingContext.getScopeGroupId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"scopeGroupId\": ");

			sb.append(dataLayoutRenderingContext.getScopeGroupId());
		}

		if (dataLayoutRenderingContext.getSiteGroupId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"siteGroupId\": ");

			sb.append(dataLayoutRenderingContext.getSiteGroupId());
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		DataLayoutRenderingContextJSONParser
			dataLayoutRenderingContextJSONParser =
				new DataLayoutRenderingContextJSONParser();

		return dataLayoutRenderingContextJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(
		DataLayoutRenderingContext dataLayoutRenderingContext) {

		if (dataLayoutRenderingContext == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (dataLayoutRenderingContext.getContainerId() == null) {
			map.put("containerId", null);
		}
		else {
			map.put(
				"containerId",
				String.valueOf(dataLayoutRenderingContext.getContainerId()));
		}

		if (dataLayoutRenderingContext.getDataRecordValues() == null) {
			map.put("dataRecordValues", null);
		}
		else {
			map.put(
				"dataRecordValues",
				String.valueOf(
					dataLayoutRenderingContext.getDataRecordValues()));
		}

		if (dataLayoutRenderingContext.getNamespace() == null) {
			map.put("namespace", null);
		}
		else {
			map.put(
				"namespace",
				String.valueOf(dataLayoutRenderingContext.getNamespace()));
		}

		if (dataLayoutRenderingContext.getPathThemeImages() == null) {
			map.put("pathThemeImages", null);
		}
		else {
			map.put(
				"pathThemeImages",
				String.valueOf(
					dataLayoutRenderingContext.getPathThemeImages()));
		}

		if (dataLayoutRenderingContext.getReadOnly() == null) {
			map.put("readOnly", null);
		}
		else {
			map.put(
				"readOnly",
				String.valueOf(dataLayoutRenderingContext.getReadOnly()));
		}

		if (dataLayoutRenderingContext.getScopeGroupId() == null) {
			map.put("scopeGroupId", null);
		}
		else {
			map.put(
				"scopeGroupId",
				String.valueOf(dataLayoutRenderingContext.getScopeGroupId()));
		}

		if (dataLayoutRenderingContext.getSiteGroupId() == null) {
			map.put("siteGroupId", null);
		}
		else {
			map.put(
				"siteGroupId",
				String.valueOf(dataLayoutRenderingContext.getSiteGroupId()));
		}

		return map;
	}

	public static class DataLayoutRenderingContextJSONParser
		extends BaseJSONParser<DataLayoutRenderingContext> {

		@Override
		protected DataLayoutRenderingContext createDTO() {
			return new DataLayoutRenderingContext();
		}

		@Override
		protected DataLayoutRenderingContext[] createDTOArray(int size) {
			return new DataLayoutRenderingContext[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "containerId")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "dataRecordValues")) {
				return true;
			}
			else if (Objects.equals(jsonParserFieldName, "namespace")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "pathThemeImages")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "readOnly")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "scopeGroupId")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "siteGroupId")) {
				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			DataLayoutRenderingContext dataLayoutRenderingContext,
			String jsonParserFieldName, Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "containerId")) {
				if (jsonParserFieldValue != null) {
					dataLayoutRenderingContext.setContainerId(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "dataRecordValues")) {
				if (jsonParserFieldValue != null) {
					dataLayoutRenderingContext.setDataRecordValues(
						(Map<String, Object>)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "namespace")) {
				if (jsonParserFieldValue != null) {
					dataLayoutRenderingContext.setNamespace(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "pathThemeImages")) {
				if (jsonParserFieldValue != null) {
					dataLayoutRenderingContext.setPathThemeImages(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "readOnly")) {
				if (jsonParserFieldValue != null) {
					dataLayoutRenderingContext.setReadOnly(
						(Boolean)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "scopeGroupId")) {
				if (jsonParserFieldValue != null) {
					dataLayoutRenderingContext.setScopeGroupId(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "siteGroupId")) {
				if (jsonParserFieldValue != null) {
					dataLayoutRenderingContext.setSiteGroupId(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
		}

	}

	private static String _escape(Object object) {
		String string = String.valueOf(object);

		for (String[] strings : BaseJSONParser.JSON_ESCAPE_STRINGS) {
			string = string.replace(strings[0], strings[1]);
		}

		return string;
	}

	private static String _toJSON(Map<String, ?> map) {
		StringBuilder sb = new StringBuilder("{");

		@SuppressWarnings("unchecked")
		Set set = map.entrySet();

		@SuppressWarnings("unchecked")
		Iterator<Map.Entry<String, ?>> iterator = set.iterator();

		while (iterator.hasNext()) {
			Map.Entry<String, ?> entry = iterator.next();

			sb.append("\"");
			sb.append(entry.getKey());
			sb.append("\": ");

			Object value = entry.getValue();

			sb.append(_toJSON(value));

			if (iterator.hasNext()) {
				sb.append(", ");
			}
		}

		sb.append("}");

		return sb.toString();
	}

	private static String _toJSON(Object value) {
		if (value == null) {
			return "null";
		}

		if (value instanceof Map) {
			return _toJSON((Map)value);
		}

		Class<?> clazz = value.getClass();

		if (clazz.isArray()) {
			StringBuilder sb = new StringBuilder("[");

			Object[] values = (Object[])value;

			for (int i = 0; i < values.length; i++) {
				sb.append(_toJSON(values[i]));

				if ((i + 1) < values.length) {
					sb.append(", ");
				}
			}

			sb.append("]");

			return sb.toString();
		}

		if (value instanceof String) {
			return "\"" + _escape(value) + "\"";
		}

		return String.valueOf(value);
	}

}