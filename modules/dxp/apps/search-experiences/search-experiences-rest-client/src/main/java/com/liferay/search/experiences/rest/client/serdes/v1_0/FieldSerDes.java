/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.search.experiences.rest.client.serdes.v1_0;

import com.liferay.search.experiences.rest.client.dto.v1_0.Field;
import com.liferay.search.experiences.rest.client.dto.v1_0.FieldMapping;
import com.liferay.search.experiences.rest.client.json.BaseJSONParser;

import jakarta.annotation.Generated;

import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;

/**
 * @author Brian Wing Shun Chan
 * @generated
 */
@Generated("")
public class FieldSerDes {

	public static Field toDTO(String json) {
		FieldJSONParser fieldJSONParser = new FieldJSONParser();

		return fieldJSONParser.parseToDTO(json);
	}

	public static Field[] toDTOs(String json) {
		FieldJSONParser fieldJSONParser = new FieldJSONParser();

		return fieldJSONParser.parseToDTOs(json);
	}

	public static String toJSON(Field field) {
		if (field == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (field.getDefaultValue() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"defaultValue\": ");

			if (field.getDefaultValue() instanceof String) {
				sb.append("\"");
				sb.append((String)field.getDefaultValue());
				sb.append("\"");
			}
			else {
				sb.append(field.getDefaultValue());
			}
		}

		if (field.getFieldMappings() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"fieldMappings\": ");

			sb.append("[");

			for (int i = 0; i < field.getFieldMappings().length; i++) {
				sb.append(String.valueOf(field.getFieldMappings()[i]));

				if ((i + 1) < field.getFieldMappings().length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		if (field.getHelpText() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"helpText\": ");

			sb.append("\"");

			sb.append(_escape(field.getHelpText()));

			sb.append("\"");
		}

		if (field.getHelpTextLocalized() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"helpTextLocalized\": ");

			sb.append("\"");

			sb.append(_escape(field.getHelpTextLocalized()));

			sb.append("\"");
		}

		if (field.getLabel() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"label\": ");

			sb.append("\"");

			sb.append(_escape(field.getLabel()));

			sb.append("\"");
		}

		if (field.getLabelLocalized() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"labelLocalized\": ");

			sb.append("\"");

			sb.append(_escape(field.getLabelLocalized()));

			sb.append("\"");
		}

		if (field.getName() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"name\": ");

			sb.append("\"");

			sb.append(_escape(field.getName()));

			sb.append("\"");
		}

		if (field.getType() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"type\": ");

			sb.append("\"");

			sb.append(_escape(field.getType()));

			sb.append("\"");
		}

		if (field.getTypeOptions() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"typeOptions\": ");

			sb.append(String.valueOf(field.getTypeOptions()));
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		FieldJSONParser fieldJSONParser = new FieldJSONParser();

		return fieldJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(Field field) {
		if (field == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (field.getDefaultValue() == null) {
			map.put("defaultValue", null);
		}
		else {
			map.put("defaultValue", String.valueOf(field.getDefaultValue()));
		}

		if (field.getFieldMappings() == null) {
			map.put("fieldMappings", null);
		}
		else {
			map.put("fieldMappings", String.valueOf(field.getFieldMappings()));
		}

		if (field.getHelpText() == null) {
			map.put("helpText", null);
		}
		else {
			map.put("helpText", String.valueOf(field.getHelpText()));
		}

		if (field.getHelpTextLocalized() == null) {
			map.put("helpTextLocalized", null);
		}
		else {
			map.put(
				"helpTextLocalized",
				String.valueOf(field.getHelpTextLocalized()));
		}

		if (field.getLabel() == null) {
			map.put("label", null);
		}
		else {
			map.put("label", String.valueOf(field.getLabel()));
		}

		if (field.getLabelLocalized() == null) {
			map.put("labelLocalized", null);
		}
		else {
			map.put(
				"labelLocalized", String.valueOf(field.getLabelLocalized()));
		}

		if (field.getName() == null) {
			map.put("name", null);
		}
		else {
			map.put("name", String.valueOf(field.getName()));
		}

		if (field.getType() == null) {
			map.put("type", null);
		}
		else {
			map.put("type", String.valueOf(field.getType()));
		}

		if (field.getTypeOptions() == null) {
			map.put("typeOptions", null);
		}
		else {
			map.put("typeOptions", String.valueOf(field.getTypeOptions()));
		}

		return map;
	}

	public static class FieldJSONParser extends BaseJSONParser<Field> {

		@Override
		protected Field createDTO() {
			return new Field();
		}

		@Override
		protected Field[] createDTOArray(int size) {
			return new Field[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "defaultValue")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "fieldMappings")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "helpText")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "helpTextLocalized")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "label")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "labelLocalized")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "name")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "type")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "typeOptions")) {
				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			Field field, String jsonParserFieldName,
			Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "defaultValue")) {
				if (jsonParserFieldValue != null) {
					field.setDefaultValue((Object)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "fieldMappings")) {
				if (jsonParserFieldValue != null) {
					Object[] jsonParserFieldValues =
						(Object[])jsonParserFieldValue;

					FieldMapping[] fieldMappingsArray =
						new FieldMapping[jsonParserFieldValues.length];

					for (int i = 0; i < fieldMappingsArray.length; i++) {
						fieldMappingsArray[i] = FieldMappingSerDes.toDTO(
							(String)jsonParserFieldValues[i]);
					}

					field.setFieldMappings(fieldMappingsArray);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "helpText")) {
				if (jsonParserFieldValue != null) {
					field.setHelpText((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "helpTextLocalized")) {
				if (jsonParserFieldValue != null) {
					field.setHelpTextLocalized((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "label")) {
				if (jsonParserFieldValue != null) {
					field.setLabel((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "labelLocalized")) {
				if (jsonParserFieldValue != null) {
					field.setLabelLocalized((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "name")) {
				if (jsonParserFieldValue != null) {
					field.setName((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "type")) {
				if (jsonParserFieldValue != null) {
					field.setType((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "typeOptions")) {
				if (jsonParserFieldValue != null) {
					field.setTypeOptions(
						TypeOptionsSerDes.toDTO((String)jsonParserFieldValue));
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