/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.fragment.client.serdes.v1_0;

import com.liferay.headless.admin.fragment.client.dto.v1_0.Dependency;
import com.liferay.headless.admin.fragment.client.dto.v1_0.TextTypeOptions;
import com.liferay.headless.admin.fragment.client.json.BaseJSONParser;

import jakarta.annotation.Generated;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;

/**
 * @author Rubén Pulido
 * @generated
 */
@Generated("")
public class TextTypeOptionsSerDes {

	public static TextTypeOptions toDTO(String json) {
		TextTypeOptionsJSONParser textTypeOptionsJSONParser =
			new TextTypeOptionsJSONParser();

		return textTypeOptionsJSONParser.parseToDTO(json);
	}

	public static TextTypeOptions[] toDTOs(String json) {
		TextTypeOptionsJSONParser textTypeOptionsJSONParser =
			new TextTypeOptionsJSONParser();

		return textTypeOptionsJSONParser.parseToDTOs(json);
	}

	public static String toJSON(TextTypeOptions textTypeOptions) {
		if (textTypeOptions == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (textTypeOptions.getDependency() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"dependency\": ");

			sb.append(_toJSON(textTypeOptions.getDependency()));
		}

		if (textTypeOptions.getPlaceholder() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"placeholder\": ");

			sb.append("\"");

			sb.append(_escape(textTypeOptions.getPlaceholder()));

			sb.append("\"");
		}

		if (textTypeOptions.getValidation() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"validation\": ");

			sb.append(String.valueOf(textTypeOptions.getValidation()));
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		TextTypeOptionsJSONParser textTypeOptionsJSONParser =
			new TextTypeOptionsJSONParser();

		return textTypeOptionsJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(TextTypeOptions textTypeOptions) {
		if (textTypeOptions == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (textTypeOptions.getDependency() == null) {
			map.put("dependency", null);
		}
		else {
			map.put(
				"dependency", String.valueOf(textTypeOptions.getDependency()));
		}

		if (textTypeOptions.getPlaceholder() == null) {
			map.put("placeholder", null);
		}
		else {
			map.put(
				"placeholder",
				String.valueOf(textTypeOptions.getPlaceholder()));
		}

		if (textTypeOptions.getValidation() == null) {
			map.put("validation", null);
		}
		else {
			map.put(
				"validation", String.valueOf(textTypeOptions.getValidation()));
		}

		return map;
	}

	public static class TextTypeOptionsJSONParser
		extends BaseJSONParser<TextTypeOptions> {

		@Override
		protected TextTypeOptions createDTO() {
			return new TextTypeOptions();
		}

		@Override
		protected TextTypeOptions[] createDTOArray(int size) {
			return new TextTypeOptions[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "dependency")) {
				return true;
			}
			else if (Objects.equals(jsonParserFieldName, "placeholder")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "validation")) {
				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			TextTypeOptions textTypeOptions, String jsonParserFieldName,
			Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "dependency")) {
				if (jsonParserFieldValue != null) {
					textTypeOptions.setDependency(
						(Map<String, Dependency>)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "placeholder")) {
				if (jsonParserFieldValue != null) {
					textTypeOptions.setPlaceholder(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "validation")) {
				if (jsonParserFieldValue != null) {
					textTypeOptions.setValidation(
						ValidationSerDes.toDTO((String)jsonParserFieldValue));
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

		if (value instanceof Collection) {
			Collection<?> collection = (Collection<?>)value;

			return _toJSON(collection.toArray());
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
// LIFERAY-REST-BUILDER-HASH:-1441174481