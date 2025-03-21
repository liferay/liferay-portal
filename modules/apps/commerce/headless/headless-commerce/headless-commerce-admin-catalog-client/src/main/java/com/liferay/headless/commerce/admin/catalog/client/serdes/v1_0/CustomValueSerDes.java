/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.catalog.client.serdes.v1_0;

import com.liferay.headless.commerce.admin.catalog.client.dto.v1_0.CustomValue;
import com.liferay.headless.commerce.admin.catalog.client.json.BaseJSONParser;

import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;

import jakarta.annotation.Generated;

/**
 * @author Zoltán Takács
 * @generated
 */
@Generated("")
public class CustomValueSerDes {

	public static CustomValue toDTO(String json) {
		CustomValueJSONParser customValueJSONParser =
			new CustomValueJSONParser();

		return customValueJSONParser.parseToDTO(json);
	}

	public static CustomValue[] toDTOs(String json) {
		CustomValueJSONParser customValueJSONParser =
			new CustomValueJSONParser();

		return customValueJSONParser.parseToDTOs(json);
	}

	public static String toJSON(CustomValue customValue) {
		if (customValue == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (customValue.getData() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"data\": ");

			if (customValue.getData() instanceof String) {
				sb.append("\"");
				sb.append((String)customValue.getData());
				sb.append("\"");
			}
			else {
				sb.append(customValue.getData());
			}
		}

		if (customValue.getData_i18n() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"data_i18n\": ");

			sb.append(_toJSON(customValue.getData_i18n()));
		}

		if (customValue.getGeo() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"geo\": ");

			sb.append(String.valueOf(customValue.getGeo()));
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		CustomValueJSONParser customValueJSONParser =
			new CustomValueJSONParser();

		return customValueJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(CustomValue customValue) {
		if (customValue == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (customValue.getData() == null) {
			map.put("data", null);
		}
		else {
			map.put("data", String.valueOf(customValue.getData()));
		}

		if (customValue.getData_i18n() == null) {
			map.put("data_i18n", null);
		}
		else {
			map.put("data_i18n", String.valueOf(customValue.getData_i18n()));
		}

		if (customValue.getGeo() == null) {
			map.put("geo", null);
		}
		else {
			map.put("geo", String.valueOf(customValue.getGeo()));
		}

		return map;
	}

	public static class CustomValueJSONParser
		extends BaseJSONParser<CustomValue> {

		@Override
		protected CustomValue createDTO() {
			return new CustomValue();
		}

		@Override
		protected CustomValue[] createDTOArray(int size) {
			return new CustomValue[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "data")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "data_i18n")) {
				return true;
			}
			else if (Objects.equals(jsonParserFieldName, "geo")) {
				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			CustomValue customValue, String jsonParserFieldName,
			Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "data")) {
				if (jsonParserFieldValue != null) {
					customValue.setData((Object)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "data_i18n")) {
				if (jsonParserFieldValue != null) {
					customValue.setData_i18n(
						(Map<String, String>)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "geo")) {
				if (jsonParserFieldValue != null) {
					customValue.setGeo(
						GeoSerDes.toDTO((String)jsonParserFieldValue));
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