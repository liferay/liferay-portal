/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.site.client.serdes.v1_0;

import com.liferay.headless.admin.site.client.dto.v1_0.CustomMetaTag;
import com.liferay.headless.admin.site.client.json.BaseJSONParser;

import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;

import jakarta.annotation.Generated;

/**
 * @author Rubén Pulido
 * @generated
 */
@Generated("")
public class CustomMetaTagSerDes {

	public static CustomMetaTag toDTO(String json) {
		CustomMetaTagJSONParser customMetaTagJSONParser =
			new CustomMetaTagJSONParser();

		return customMetaTagJSONParser.parseToDTO(json);
	}

	public static CustomMetaTag[] toDTOs(String json) {
		CustomMetaTagJSONParser customMetaTagJSONParser =
			new CustomMetaTagJSONParser();

		return customMetaTagJSONParser.parseToDTOs(json);
	}

	public static String toJSON(CustomMetaTag customMetaTag) {
		if (customMetaTag == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (customMetaTag.getKey() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"key\": ");

			sb.append("\"");

			sb.append(_escape(customMetaTag.getKey()));

			sb.append("\"");
		}

		if (customMetaTag.getValue_i18n() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"value_i18n\": ");

			sb.append(_toJSON(customMetaTag.getValue_i18n()));
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		CustomMetaTagJSONParser customMetaTagJSONParser =
			new CustomMetaTagJSONParser();

		return customMetaTagJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(CustomMetaTag customMetaTag) {
		if (customMetaTag == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (customMetaTag.getKey() == null) {
			map.put("key", null);
		}
		else {
			map.put("key", String.valueOf(customMetaTag.getKey()));
		}

		if (customMetaTag.getValue_i18n() == null) {
			map.put("value_i18n", null);
		}
		else {
			map.put(
				"value_i18n", String.valueOf(customMetaTag.getValue_i18n()));
		}

		return map;
	}

	public static class CustomMetaTagJSONParser
		extends BaseJSONParser<CustomMetaTag> {

		@Override
		protected CustomMetaTag createDTO() {
			return new CustomMetaTag();
		}

		@Override
		protected CustomMetaTag[] createDTOArray(int size) {
			return new CustomMetaTag[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "key")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "value_i18n")) {
				return true;
			}

			return false;
		}

		@Override
		protected void setField(
			CustomMetaTag customMetaTag, String jsonParserFieldName,
			Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "key")) {
				if (jsonParserFieldValue != null) {
					customMetaTag.setKey((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "value_i18n")) {
				if (jsonParserFieldValue != null) {
					customMetaTag.setValue_i18n(
						(Map<String, String>)jsonParserFieldValue);
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