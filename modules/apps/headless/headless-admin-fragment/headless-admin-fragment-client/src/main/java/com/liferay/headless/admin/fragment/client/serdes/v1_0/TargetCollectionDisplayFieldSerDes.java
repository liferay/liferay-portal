/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.fragment.client.serdes.v1_0;

import com.liferay.headless.admin.fragment.client.dto.v1_0.TargetCollectionDisplayField;
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
public class TargetCollectionDisplayFieldSerDes {

	public static TargetCollectionDisplayField toDTO(String json) {
		TargetCollectionDisplayFieldJSONParser
			targetCollectionDisplayFieldJSONParser =
				new TargetCollectionDisplayFieldJSONParser();

		return targetCollectionDisplayFieldJSONParser.parseToDTO(json);
	}

	public static TargetCollectionDisplayField[] toDTOs(String json) {
		TargetCollectionDisplayFieldJSONParser
			targetCollectionDisplayFieldJSONParser =
				new TargetCollectionDisplayFieldJSONParser();

		return targetCollectionDisplayFieldJSONParser.parseToDTOs(json);
	}

	public static String toJSON(
		TargetCollectionDisplayField targetCollectionDisplayField) {

		if (targetCollectionDisplayField == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (targetCollectionDisplayField.getDefaultValue() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"defaultValue\": ");

			sb.append(
				String.valueOf(targetCollectionDisplayField.getDefaultValue()));
		}

		if (targetCollectionDisplayField.getTypeOptions() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"typeOptions\": ");

			sb.append(
				String.valueOf(targetCollectionDisplayField.getTypeOptions()));
		}

		if (targetCollectionDisplayField.getDataType() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"dataType\": ");

			sb.append("\"");
			sb.append(targetCollectionDisplayField.getDataType());
			sb.append("\"");
		}

		if (targetCollectionDisplayField.getDescription() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"description\": ");

			sb.append("\"");

			sb.append(_escape(targetCollectionDisplayField.getDescription()));

			sb.append("\"");
		}

		if (targetCollectionDisplayField.getLabel() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"label\": ");

			sb.append("\"");

			sb.append(_escape(targetCollectionDisplayField.getLabel()));

			sb.append("\"");
		}

		if (targetCollectionDisplayField.getLocalizable() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"localizable\": ");

			sb.append(targetCollectionDisplayField.getLocalizable());
		}

		if (targetCollectionDisplayField.getName() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"name\": ");

			sb.append("\"");

			sb.append(_escape(targetCollectionDisplayField.getName()));

			sb.append("\"");
		}

		if (targetCollectionDisplayField.getType() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"type\": ");

			sb.append("\"");
			sb.append(targetCollectionDisplayField.getType());
			sb.append("\"");
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		TargetCollectionDisplayFieldJSONParser
			targetCollectionDisplayFieldJSONParser =
				new TargetCollectionDisplayFieldJSONParser();

		return targetCollectionDisplayFieldJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(
		TargetCollectionDisplayField targetCollectionDisplayField) {

		if (targetCollectionDisplayField == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (targetCollectionDisplayField.getDefaultValue() == null) {
			map.put("defaultValue", null);
		}
		else {
			map.put(
				"defaultValue",
				String.valueOf(targetCollectionDisplayField.getDefaultValue()));
		}

		if (targetCollectionDisplayField.getTypeOptions() == null) {
			map.put("typeOptions", null);
		}
		else {
			map.put(
				"typeOptions",
				String.valueOf(targetCollectionDisplayField.getTypeOptions()));
		}

		if (targetCollectionDisplayField.getDataType() == null) {
			map.put("dataType", null);
		}
		else {
			map.put(
				"dataType",
				String.valueOf(targetCollectionDisplayField.getDataType()));
		}

		if (targetCollectionDisplayField.getDescription() == null) {
			map.put("description", null);
		}
		else {
			map.put(
				"description",
				String.valueOf(targetCollectionDisplayField.getDescription()));
		}

		if (targetCollectionDisplayField.getLabel() == null) {
			map.put("label", null);
		}
		else {
			map.put(
				"label",
				String.valueOf(targetCollectionDisplayField.getLabel()));
		}

		if (targetCollectionDisplayField.getLocalizable() == null) {
			map.put("localizable", null);
		}
		else {
			map.put(
				"localizable",
				String.valueOf(targetCollectionDisplayField.getLocalizable()));
		}

		if (targetCollectionDisplayField.getName() == null) {
			map.put("name", null);
		}
		else {
			map.put(
				"name", String.valueOf(targetCollectionDisplayField.getName()));
		}

		if (targetCollectionDisplayField.getType() == null) {
			map.put("type", null);
		}
		else {
			map.put(
				"type", String.valueOf(targetCollectionDisplayField.getType()));
		}

		return map;
	}

	public static class TargetCollectionDisplayFieldJSONParser
		extends BaseJSONParser<TargetCollectionDisplayField> {

		@Override
		protected TargetCollectionDisplayField createDTO() {
			return new TargetCollectionDisplayField();
		}

		@Override
		protected TargetCollectionDisplayField[] createDTOArray(int size) {
			return new TargetCollectionDisplayField[size];
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
			TargetCollectionDisplayField targetCollectionDisplayField,
			String jsonParserFieldName, Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "defaultValue")) {
				if (jsonParserFieldValue != null) {
					targetCollectionDisplayField.setDefaultValue(
						TargetCollectionDisplayFragmentConfigurationFieldDefaultValueSerDes.
							toDTO((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "typeOptions")) {
				if (jsonParserFieldValue != null) {
					targetCollectionDisplayField.setTypeOptions(
						TargetCollectionDisplayTypeOptionsSerDes.toDTO(
							(String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "dataType")) {
				if (jsonParserFieldValue != null) {
					targetCollectionDisplayField.setDataType(
						TargetCollectionDisplayField.DataType.create(
							(String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "description")) {
				if (jsonParserFieldValue != null) {
					targetCollectionDisplayField.setDescription(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "label")) {
				if (jsonParserFieldValue != null) {
					targetCollectionDisplayField.setLabel(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "localizable")) {
				if (jsonParserFieldValue != null) {
					targetCollectionDisplayField.setLocalizable(
						(Boolean)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "name")) {
				if (jsonParserFieldValue != null) {
					targetCollectionDisplayField.setName(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "type")) {
				if (jsonParserFieldValue != null) {
					targetCollectionDisplayField.setType(
						TargetCollectionDisplayField.Type.create(
							(String)jsonParserFieldValue));
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
// LIFERAY-REST-BUILDER-HASH:1285725495