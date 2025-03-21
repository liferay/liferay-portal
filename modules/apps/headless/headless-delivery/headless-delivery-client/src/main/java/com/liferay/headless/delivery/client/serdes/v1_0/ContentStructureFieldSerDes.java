/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.delivery.client.serdes.v1_0;

import com.liferay.headless.delivery.client.dto.v1_0.ContentStructureField;
import com.liferay.headless.delivery.client.dto.v1_0.Option;
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
public class ContentStructureFieldSerDes {

	public static ContentStructureField toDTO(String json) {
		ContentStructureFieldJSONParser contentStructureFieldJSONParser =
			new ContentStructureFieldJSONParser();

		return contentStructureFieldJSONParser.parseToDTO(json);
	}

	public static ContentStructureField[] toDTOs(String json) {
		ContentStructureFieldJSONParser contentStructureFieldJSONParser =
			new ContentStructureFieldJSONParser();

		return contentStructureFieldJSONParser.parseToDTOs(json);
	}

	public static String toJSON(ContentStructureField contentStructureField) {
		if (contentStructureField == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (contentStructureField.getDataType() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"dataType\": ");

			sb.append("\"");

			sb.append(_escape(contentStructureField.getDataType()));

			sb.append("\"");
		}

		if (contentStructureField.getInputControl() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"inputControl\": ");

			sb.append("\"");

			sb.append(_escape(contentStructureField.getInputControl()));

			sb.append("\"");
		}

		if (contentStructureField.getLabel() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"label\": ");

			sb.append("\"");

			sb.append(_escape(contentStructureField.getLabel()));

			sb.append("\"");
		}

		if (contentStructureField.getLabel_i18n() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"label_i18n\": ");

			sb.append(_toJSON(contentStructureField.getLabel_i18n()));
		}

		if (contentStructureField.getLocalizable() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"localizable\": ");

			sb.append(contentStructureField.getLocalizable());
		}

		if (contentStructureField.getMultiple() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"multiple\": ");

			sb.append(contentStructureField.getMultiple());
		}

		if (contentStructureField.getName() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"name\": ");

			sb.append("\"");

			sb.append(_escape(contentStructureField.getName()));

			sb.append("\"");
		}

		if (contentStructureField.getNestedContentStructureFields() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"nestedContentStructureFields\": ");

			sb.append("[");

			for (int i = 0;
				 i < contentStructureField.
					 getNestedContentStructureFields().length;
				 i++) {

				sb.append(
					String.valueOf(
						contentStructureField.getNestedContentStructureFields()
							[i]));

				if ((i + 1) < contentStructureField.
						getNestedContentStructureFields().length) {

					sb.append(", ");
				}
			}

			sb.append("]");
		}

		if (contentStructureField.getOptions() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"options\": ");

			sb.append("[");

			for (int i = 0; i < contentStructureField.getOptions().length;
				 i++) {

				sb.append(
					String.valueOf(contentStructureField.getOptions()[i]));

				if ((i + 1) < contentStructureField.getOptions().length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		if (contentStructureField.getPredefinedValue() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"predefinedValue\": ");

			sb.append("\"");

			sb.append(_escape(contentStructureField.getPredefinedValue()));

			sb.append("\"");
		}

		if (contentStructureField.getPredefinedValue_i18n() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"predefinedValue_i18n\": ");

			sb.append(_toJSON(contentStructureField.getPredefinedValue_i18n()));
		}

		if (contentStructureField.getRepeatable() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"repeatable\": ");

			sb.append(contentStructureField.getRepeatable());
		}

		if (contentStructureField.getRequired() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"required\": ");

			sb.append(contentStructureField.getRequired());
		}

		if (contentStructureField.getShowLabel() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"showLabel\": ");

			sb.append(contentStructureField.getShowLabel());
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		ContentStructureFieldJSONParser contentStructureFieldJSONParser =
			new ContentStructureFieldJSONParser();

		return contentStructureFieldJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(
		ContentStructureField contentStructureField) {

		if (contentStructureField == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (contentStructureField.getDataType() == null) {
			map.put("dataType", null);
		}
		else {
			map.put(
				"dataType",
				String.valueOf(contentStructureField.getDataType()));
		}

		if (contentStructureField.getInputControl() == null) {
			map.put("inputControl", null);
		}
		else {
			map.put(
				"inputControl",
				String.valueOf(contentStructureField.getInputControl()));
		}

		if (contentStructureField.getLabel() == null) {
			map.put("label", null);
		}
		else {
			map.put("label", String.valueOf(contentStructureField.getLabel()));
		}

		if (contentStructureField.getLabel_i18n() == null) {
			map.put("label_i18n", null);
		}
		else {
			map.put(
				"label_i18n",
				String.valueOf(contentStructureField.getLabel_i18n()));
		}

		if (contentStructureField.getLocalizable() == null) {
			map.put("localizable", null);
		}
		else {
			map.put(
				"localizable",
				String.valueOf(contentStructureField.getLocalizable()));
		}

		if (contentStructureField.getMultiple() == null) {
			map.put("multiple", null);
		}
		else {
			map.put(
				"multiple",
				String.valueOf(contentStructureField.getMultiple()));
		}

		if (contentStructureField.getName() == null) {
			map.put("name", null);
		}
		else {
			map.put("name", String.valueOf(contentStructureField.getName()));
		}

		if (contentStructureField.getNestedContentStructureFields() == null) {
			map.put("nestedContentStructureFields", null);
		}
		else {
			map.put(
				"nestedContentStructureFields",
				String.valueOf(
					contentStructureField.getNestedContentStructureFields()));
		}

		if (contentStructureField.getOptions() == null) {
			map.put("options", null);
		}
		else {
			map.put(
				"options", String.valueOf(contentStructureField.getOptions()));
		}

		if (contentStructureField.getPredefinedValue() == null) {
			map.put("predefinedValue", null);
		}
		else {
			map.put(
				"predefinedValue",
				String.valueOf(contentStructureField.getPredefinedValue()));
		}

		if (contentStructureField.getPredefinedValue_i18n() == null) {
			map.put("predefinedValue_i18n", null);
		}
		else {
			map.put(
				"predefinedValue_i18n",
				String.valueOf(
					contentStructureField.getPredefinedValue_i18n()));
		}

		if (contentStructureField.getRepeatable() == null) {
			map.put("repeatable", null);
		}
		else {
			map.put(
				"repeatable",
				String.valueOf(contentStructureField.getRepeatable()));
		}

		if (contentStructureField.getRequired() == null) {
			map.put("required", null);
		}
		else {
			map.put(
				"required",
				String.valueOf(contentStructureField.getRequired()));
		}

		if (contentStructureField.getShowLabel() == null) {
			map.put("showLabel", null);
		}
		else {
			map.put(
				"showLabel",
				String.valueOf(contentStructureField.getShowLabel()));
		}

		return map;
	}

	public static class ContentStructureFieldJSONParser
		extends BaseJSONParser<ContentStructureField> {

		@Override
		protected ContentStructureField createDTO() {
			return new ContentStructureField();
		}

		@Override
		protected ContentStructureField[] createDTOArray(int size) {
			return new ContentStructureField[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "dataType")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "inputControl")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "label")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "label_i18n")) {
				return true;
			}
			else if (Objects.equals(jsonParserFieldName, "localizable")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "multiple")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "name")) {
				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "nestedContentStructureFields")) {

				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "options")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "predefinedValue")) {
				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "predefinedValue_i18n")) {

				return true;
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

			return false;
		}

		@Override
		protected void setField(
			ContentStructureField contentStructureField,
			String jsonParserFieldName, Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "dataType")) {
				if (jsonParserFieldValue != null) {
					contentStructureField.setDataType(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "inputControl")) {
				if (jsonParserFieldValue != null) {
					contentStructureField.setInputControl(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "label")) {
				if (jsonParserFieldValue != null) {
					contentStructureField.setLabel(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "label_i18n")) {
				if (jsonParserFieldValue != null) {
					contentStructureField.setLabel_i18n(
						(Map<String, String>)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "localizable")) {
				if (jsonParserFieldValue != null) {
					contentStructureField.setLocalizable(
						(Boolean)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "multiple")) {
				if (jsonParserFieldValue != null) {
					contentStructureField.setMultiple(
						(Boolean)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "name")) {
				if (jsonParserFieldValue != null) {
					contentStructureField.setName((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "nestedContentStructureFields")) {

				if (jsonParserFieldValue != null) {
					Object[] jsonParserFieldValues =
						(Object[])jsonParserFieldValue;

					ContentStructureField[] nestedContentStructureFieldsArray =
						new ContentStructureField[jsonParserFieldValues.length];

					for (int i = 0;
						 i < nestedContentStructureFieldsArray.length; i++) {

						nestedContentStructureFieldsArray[i] =
							ContentStructureFieldSerDes.toDTO(
								(String)jsonParserFieldValues[i]);
					}

					contentStructureField.setNestedContentStructureFields(
						nestedContentStructureFieldsArray);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "options")) {
				if (jsonParserFieldValue != null) {
					Object[] jsonParserFieldValues =
						(Object[])jsonParserFieldValue;

					Option[] optionsArray =
						new Option[jsonParserFieldValues.length];

					for (int i = 0; i < optionsArray.length; i++) {
						optionsArray[i] = OptionSerDes.toDTO(
							(String)jsonParserFieldValues[i]);
					}

					contentStructureField.setOptions(optionsArray);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "predefinedValue")) {
				if (jsonParserFieldValue != null) {
					contentStructureField.setPredefinedValue(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "predefinedValue_i18n")) {

				if (jsonParserFieldValue != null) {
					contentStructureField.setPredefinedValue_i18n(
						(Map<String, String>)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "repeatable")) {
				if (jsonParserFieldValue != null) {
					contentStructureField.setRepeatable(
						(Boolean)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "required")) {
				if (jsonParserFieldValue != null) {
					contentStructureField.setRequired(
						(Boolean)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "showLabel")) {
				if (jsonParserFieldValue != null) {
					contentStructureField.setShowLabel(
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