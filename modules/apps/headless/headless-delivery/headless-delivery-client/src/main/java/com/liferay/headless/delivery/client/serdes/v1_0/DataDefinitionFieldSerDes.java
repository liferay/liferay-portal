/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.delivery.client.serdes.v1_0;

import com.liferay.headless.delivery.client.dto.v1_0.DataDefinitionField;
import com.liferay.headless.delivery.client.json.BaseJSONParser;

import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;

import jakarta.annotation.Generated;

/**
 * @author Javier Gamarra
 * @generated
 */
@Generated("")
public class DataDefinitionFieldSerDes {

	public static DataDefinitionField toDTO(String json) {
		DataDefinitionFieldJSONParser dataDefinitionFieldJSONParser =
			new DataDefinitionFieldJSONParser();

		return dataDefinitionFieldJSONParser.parseToDTO(json);
	}

	public static DataDefinitionField[] toDTOs(String json) {
		DataDefinitionFieldJSONParser dataDefinitionFieldJSONParser =
			new DataDefinitionFieldJSONParser();

		return dataDefinitionFieldJSONParser.parseToDTOs(json);
	}

	public static String toJSON(DataDefinitionField dataDefinitionField) {
		if (dataDefinitionField == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (dataDefinitionField.getCustomProperties() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"customProperties\": ");

			sb.append(_toJSON(dataDefinitionField.getCustomProperties()));
		}

		if (dataDefinitionField.getDefaultValue() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"defaultValue\": ");

			sb.append(_toJSON(dataDefinitionField.getDefaultValue()));
		}

		if (dataDefinitionField.getFieldType() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"fieldType\": ");

			sb.append("\"");

			sb.append(_escape(dataDefinitionField.getFieldType()));

			sb.append("\"");
		}

		if (dataDefinitionField.getId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"id\": ");

			sb.append(dataDefinitionField.getId());
		}

		if (dataDefinitionField.getIndexType() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"indexType\": ");

			sb.append("\"");

			sb.append(dataDefinitionField.getIndexType());

			sb.append("\"");
		}

		if (dataDefinitionField.getIndexable() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"indexable\": ");

			sb.append(dataDefinitionField.getIndexable());
		}

		if (dataDefinitionField.getLabel() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"label\": ");

			sb.append(_toJSON(dataDefinitionField.getLabel()));
		}

		if (dataDefinitionField.getLocalizable() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"localizable\": ");

			sb.append(dataDefinitionField.getLocalizable());
		}

		if (dataDefinitionField.getName() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"name\": ");

			sb.append("\"");

			sb.append(_escape(dataDefinitionField.getName()));

			sb.append("\"");
		}

		if (dataDefinitionField.getNestedDataDefinitionFields() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"nestedDataDefinitionFields\": ");

			sb.append("[");

			for (int i = 0;
				 i < dataDefinitionField.getNestedDataDefinitionFields().length;
				 i++) {

				sb.append(
					dataDefinitionField.getNestedDataDefinitionFields()[i]);

				if ((i + 1) < dataDefinitionField.
						getNestedDataDefinitionFields().length) {

					sb.append(", ");
				}
			}

			sb.append("]");
		}

		if (dataDefinitionField.getReadOnly() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"readOnly\": ");

			sb.append(dataDefinitionField.getReadOnly());
		}

		if (dataDefinitionField.getRepeatable() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"repeatable\": ");

			sb.append(dataDefinitionField.getRepeatable());
		}

		if (dataDefinitionField.getRequired() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"required\": ");

			sb.append(dataDefinitionField.getRequired());
		}

		if (dataDefinitionField.getShowLabel() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"showLabel\": ");

			sb.append(dataDefinitionField.getShowLabel());
		}

		if (dataDefinitionField.getTip() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"tip\": ");

			sb.append(_toJSON(dataDefinitionField.getTip()));
		}

		if (dataDefinitionField.getVisible() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"visible\": ");

			sb.append(dataDefinitionField.getVisible());
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		DataDefinitionFieldJSONParser dataDefinitionFieldJSONParser =
			new DataDefinitionFieldJSONParser();

		return dataDefinitionFieldJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(
		DataDefinitionField dataDefinitionField) {

		if (dataDefinitionField == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (dataDefinitionField.getCustomProperties() == null) {
			map.put("customProperties", null);
		}
		else {
			map.put(
				"customProperties",
				String.valueOf(dataDefinitionField.getCustomProperties()));
		}

		if (dataDefinitionField.getDefaultValue() == null) {
			map.put("defaultValue", null);
		}
		else {
			map.put(
				"defaultValue",
				String.valueOf(dataDefinitionField.getDefaultValue()));
		}

		if (dataDefinitionField.getFieldType() == null) {
			map.put("fieldType", null);
		}
		else {
			map.put(
				"fieldType",
				String.valueOf(dataDefinitionField.getFieldType()));
		}

		if (dataDefinitionField.getId() == null) {
			map.put("id", null);
		}
		else {
			map.put("id", String.valueOf(dataDefinitionField.getId()));
		}

		if (dataDefinitionField.getIndexType() == null) {
			map.put("indexType", null);
		}
		else {
			map.put(
				"indexType",
				String.valueOf(dataDefinitionField.getIndexType()));
		}

		if (dataDefinitionField.getIndexable() == null) {
			map.put("indexable", null);
		}
		else {
			map.put(
				"indexable",
				String.valueOf(dataDefinitionField.getIndexable()));
		}

		if (dataDefinitionField.getLabel() == null) {
			map.put("label", null);
		}
		else {
			map.put("label", String.valueOf(dataDefinitionField.getLabel()));
		}

		if (dataDefinitionField.getLocalizable() == null) {
			map.put("localizable", null);
		}
		else {
			map.put(
				"localizable",
				String.valueOf(dataDefinitionField.getLocalizable()));
		}

		if (dataDefinitionField.getName() == null) {
			map.put("name", null);
		}
		else {
			map.put("name", String.valueOf(dataDefinitionField.getName()));
		}

		if (dataDefinitionField.getNestedDataDefinitionFields() == null) {
			map.put("nestedDataDefinitionFields", null);
		}
		else {
			map.put(
				"nestedDataDefinitionFields",
				String.valueOf(
					dataDefinitionField.getNestedDataDefinitionFields()));
		}

		if (dataDefinitionField.getReadOnly() == null) {
			map.put("readOnly", null);
		}
		else {
			map.put(
				"readOnly", String.valueOf(dataDefinitionField.getReadOnly()));
		}

		if (dataDefinitionField.getRepeatable() == null) {
			map.put("repeatable", null);
		}
		else {
			map.put(
				"repeatable",
				String.valueOf(dataDefinitionField.getRepeatable()));
		}

		if (dataDefinitionField.getRequired() == null) {
			map.put("required", null);
		}
		else {
			map.put(
				"required", String.valueOf(dataDefinitionField.getRequired()));
		}

		if (dataDefinitionField.getShowLabel() == null) {
			map.put("showLabel", null);
		}
		else {
			map.put(
				"showLabel",
				String.valueOf(dataDefinitionField.getShowLabel()));
		}

		if (dataDefinitionField.getTip() == null) {
			map.put("tip", null);
		}
		else {
			map.put("tip", String.valueOf(dataDefinitionField.getTip()));
		}

		if (dataDefinitionField.getVisible() == null) {
			map.put("visible", null);
		}
		else {
			map.put(
				"visible", String.valueOf(dataDefinitionField.getVisible()));
		}

		return map;
	}

	public static class DataDefinitionFieldJSONParser
		extends BaseJSONParser<DataDefinitionField> {

		@Override
		protected DataDefinitionField createDTO() {
			return new DataDefinitionField();
		}

		@Override
		protected DataDefinitionField[] createDTOArray(int size) {
			return new DataDefinitionField[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "customProperties")) {
				return true;
			}
			else if (Objects.equals(jsonParserFieldName, "defaultValue")) {
				return true;
			}
			else if (Objects.equals(jsonParserFieldName, "fieldType")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "id")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "indexType")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "indexable")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "label")) {
				return true;
			}
			else if (Objects.equals(jsonParserFieldName, "localizable")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "name")) {
				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "nestedDataDefinitionFields")) {

				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "readOnly")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "repeatable")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "required")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "showLabel")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "tip")) {
				return true;
			}
			else if (Objects.equals(jsonParserFieldName, "visible")) {
				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			DataDefinitionField dataDefinitionField, String jsonParserFieldName,
			Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "customProperties")) {
				if (jsonParserFieldValue != null) {
					dataDefinitionField.setCustomProperties(
						(Map<String, Object>)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "defaultValue")) {
				if (jsonParserFieldValue != null) {
					dataDefinitionField.setDefaultValue(
						(Map<String, Object>)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "fieldType")) {
				if (jsonParserFieldValue != null) {
					dataDefinitionField.setFieldType(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "id")) {
				if (jsonParserFieldValue != null) {
					dataDefinitionField.setId(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "indexType")) {
				if (jsonParserFieldValue != null) {
					dataDefinitionField.setIndexType(
						DataDefinitionField.IndexType.create(
							(String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "indexable")) {
				if (jsonParserFieldValue != null) {
					dataDefinitionField.setIndexable(
						(Boolean)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "label")) {
				if (jsonParserFieldValue != null) {
					dataDefinitionField.setLabel(
						(Map<String, Object>)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "localizable")) {
				if (jsonParserFieldValue != null) {
					dataDefinitionField.setLocalizable(
						(Boolean)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "name")) {
				if (jsonParserFieldValue != null) {
					dataDefinitionField.setName((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "nestedDataDefinitionFields")) {

				if (jsonParserFieldValue != null) {
					Object[] jsonParserFieldValues =
						(Object[])jsonParserFieldValue;

					DataDefinitionField[] nestedDataDefinitionFieldsArray =
						new DataDefinitionField[jsonParserFieldValues.length];

					for (int i = 0; i < nestedDataDefinitionFieldsArray.length;
						 i++) {

						nestedDataDefinitionFieldsArray[i] =
							DataDefinitionFieldSerDes.toDTO(
								(String)jsonParserFieldValues[i]);
					}

					dataDefinitionField.setNestedDataDefinitionFields(
						nestedDataDefinitionFieldsArray);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "readOnly")) {
				if (jsonParserFieldValue != null) {
					dataDefinitionField.setReadOnly(
						(Boolean)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "repeatable")) {
				if (jsonParserFieldValue != null) {
					dataDefinitionField.setRepeatable(
						(Boolean)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "required")) {
				if (jsonParserFieldValue != null) {
					dataDefinitionField.setRequired(
						(Boolean)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "showLabel")) {
				if (jsonParserFieldValue != null) {
					dataDefinitionField.setShowLabel(
						(Boolean)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "tip")) {
				if (jsonParserFieldValue != null) {
					dataDefinitionField.setTip(
						(Map<String, Object>)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "visible")) {
				if (jsonParserFieldValue != null) {
					dataDefinitionField.setVisible(
						(Boolean)jsonParserFieldValue);
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