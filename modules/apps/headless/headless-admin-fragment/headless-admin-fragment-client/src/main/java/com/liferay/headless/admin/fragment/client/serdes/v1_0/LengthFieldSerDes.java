/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.fragment.client.serdes.v1_0;

import com.liferay.headless.admin.fragment.client.dto.v1_0.LengthField;
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
public class LengthFieldSerDes {

	public static LengthField toDTO(String json) {
		LengthFieldJSONParser lengthFieldJSONParser =
			new LengthFieldJSONParser();

		return lengthFieldJSONParser.parseToDTO(json);
	}

	public static LengthField[] toDTOs(String json) {
		LengthFieldJSONParser lengthFieldJSONParser =
			new LengthFieldJSONParser();

		return lengthFieldJSONParser.parseToDTOs(json);
	}

	public static String toJSON(LengthField lengthField) {
		if (lengthField == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (lengthField.getDefaultValue() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"defaultValue\": ");

			sb.append(String.valueOf(lengthField.getDefaultValue()));
		}

		if (lengthField.getTypeOptions() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"typeOptions\": ");

			sb.append(String.valueOf(lengthField.getTypeOptions()));
		}

		if (lengthField.getDataType() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"dataType\": ");

			sb.append("\"");
			sb.append(lengthField.getDataType());
			sb.append("\"");
		}

		if (lengthField.getDescription() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"description\": ");

			sb.append("\"");

			sb.append(_escape(lengthField.getDescription()));

			sb.append("\"");
		}

		if (lengthField.getLabel() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"label\": ");

			sb.append("\"");

			sb.append(_escape(lengthField.getLabel()));

			sb.append("\"");
		}

		if (lengthField.getLocalizable() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"localizable\": ");

			sb.append(lengthField.getLocalizable());
		}

		if (lengthField.getName() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"name\": ");

			sb.append("\"");

			sb.append(_escape(lengthField.getName()));

			sb.append("\"");
		}

		if (lengthField.getType() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"type\": ");

			sb.append("\"");
			sb.append(lengthField.getType());
			sb.append("\"");
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		LengthFieldJSONParser lengthFieldJSONParser =
			new LengthFieldJSONParser();

		return lengthFieldJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(LengthField lengthField) {
		if (lengthField == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (lengthField.getDefaultValue() == null) {
			map.put("defaultValue", null);
		}
		else {
			map.put(
				"defaultValue", String.valueOf(lengthField.getDefaultValue()));
		}

		if (lengthField.getTypeOptions() == null) {
			map.put("typeOptions", null);
		}
		else {
			map.put(
				"typeOptions", String.valueOf(lengthField.getTypeOptions()));
		}

		if (lengthField.getDataType() == null) {
			map.put("dataType", null);
		}
		else {
			map.put("dataType", String.valueOf(lengthField.getDataType()));
		}

		if (lengthField.getDescription() == null) {
			map.put("description", null);
		}
		else {
			map.put(
				"description", String.valueOf(lengthField.getDescription()));
		}

		if (lengthField.getLabel() == null) {
			map.put("label", null);
		}
		else {
			map.put("label", String.valueOf(lengthField.getLabel()));
		}

		if (lengthField.getLocalizable() == null) {
			map.put("localizable", null);
		}
		else {
			map.put(
				"localizable", String.valueOf(lengthField.getLocalizable()));
		}

		if (lengthField.getName() == null) {
			map.put("name", null);
		}
		else {
			map.put("name", String.valueOf(lengthField.getName()));
		}

		if (lengthField.getType() == null) {
			map.put("type", null);
		}
		else {
			map.put("type", String.valueOf(lengthField.getType()));
		}

		return map;
	}

	public static class LengthFieldJSONParser
		extends BaseJSONParser<LengthField> {

		@Override
		protected LengthField createDTO() {
			return new LengthField();
		}

		@Override
		protected LengthField[] createDTOArray(int size) {
			return new LengthField[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "defaultValue")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "typeOptions")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "dataType")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "description")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "label")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "localizable")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "name")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "type")) {
				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			LengthField lengthField, String jsonParserFieldName,
			Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "defaultValue")) {
				if (jsonParserFieldValue != null) {
					lengthField.setDefaultValue(
						LengthFragmentConfigurationFieldDefaultValueSerDes.
							toDTO((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "typeOptions")) {
				if (jsonParserFieldValue != null) {
					lengthField.setTypeOptions(
						TypeOptionsSerDes.toDTO((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "dataType")) {
				if (jsonParserFieldValue != null) {
					lengthField.setDataType(
						LengthField.DataType.create(
							(String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "description")) {
				if (jsonParserFieldValue != null) {
					lengthField.setDescription((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "label")) {
				if (jsonParserFieldValue != null) {
					lengthField.setLabel((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "localizable")) {
				if (jsonParserFieldValue != null) {
					lengthField.setLocalizable((Boolean)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "name")) {
				if (jsonParserFieldValue != null) {
					lengthField.setName((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "type")) {
				if (jsonParserFieldValue != null) {
					lengthField.setType(
						LengthField.Type.create((String)jsonParserFieldValue));
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
// LIFERAY-REST-BUILDER-HASH:1536850441