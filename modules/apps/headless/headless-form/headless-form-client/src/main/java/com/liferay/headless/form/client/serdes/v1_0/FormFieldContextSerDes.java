/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.form.client.serdes.v1_0;

import com.liferay.headless.form.client.dto.v1_0.FormFieldContext;
import com.liferay.headless.form.client.dto.v1_0.FormFieldOption;
import com.liferay.headless.form.client.json.BaseJSONParser;

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
public class FormFieldContextSerDes {

	public static FormFieldContext toDTO(String json) {
		FormFieldContextJSONParser formFieldContextJSONParser =
			new FormFieldContextJSONParser();

		return formFieldContextJSONParser.parseToDTO(json);
	}

	public static FormFieldContext[] toDTOs(String json) {
		FormFieldContextJSONParser formFieldContextJSONParser =
			new FormFieldContextJSONParser();

		return formFieldContextJSONParser.parseToDTOs(json);
	}

	public static String toJSON(FormFieldContext formFieldContext) {
		if (formFieldContext == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (formFieldContext.getEvaluable() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"evaluable\": ");

			sb.append(formFieldContext.getEvaluable());
		}

		if (formFieldContext.getFormFieldOptions() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"formFieldOptions\": ");

			sb.append("[");

			for (int i = 0; i < formFieldContext.getFormFieldOptions().length;
				 i++) {

				sb.append(
					String.valueOf(formFieldContext.getFormFieldOptions()[i]));

				if ((i + 1) < formFieldContext.getFormFieldOptions().length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		if (formFieldContext.getName() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"name\": ");

			sb.append("\"");

			sb.append(_escape(formFieldContext.getName()));

			sb.append("\"");
		}

		if (formFieldContext.getReadOnly() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"readOnly\": ");

			sb.append(formFieldContext.getReadOnly());
		}

		if (formFieldContext.getRequired() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"required\": ");

			sb.append(formFieldContext.getRequired());
		}

		if (formFieldContext.getValid() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"valid\": ");

			sb.append(formFieldContext.getValid());
		}

		if (formFieldContext.getValue() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"value\": ");

			sb.append("\"");

			sb.append(_escape(formFieldContext.getValue()));

			sb.append("\"");
		}

		if (formFieldContext.getValueChanged() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"valueChanged\": ");

			sb.append(formFieldContext.getValueChanged());
		}

		if (formFieldContext.getVisible() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"visible\": ");

			sb.append(formFieldContext.getVisible());
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		FormFieldContextJSONParser formFieldContextJSONParser =
			new FormFieldContextJSONParser();

		return formFieldContextJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(FormFieldContext formFieldContext) {
		if (formFieldContext == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (formFieldContext.getEvaluable() == null) {
			map.put("evaluable", null);
		}
		else {
			map.put(
				"evaluable", String.valueOf(formFieldContext.getEvaluable()));
		}

		if (formFieldContext.getFormFieldOptions() == null) {
			map.put("formFieldOptions", null);
		}
		else {
			map.put(
				"formFieldOptions",
				String.valueOf(formFieldContext.getFormFieldOptions()));
		}

		if (formFieldContext.getName() == null) {
			map.put("name", null);
		}
		else {
			map.put("name", String.valueOf(formFieldContext.getName()));
		}

		if (formFieldContext.getReadOnly() == null) {
			map.put("readOnly", null);
		}
		else {
			map.put("readOnly", String.valueOf(formFieldContext.getReadOnly()));
		}

		if (formFieldContext.getRequired() == null) {
			map.put("required", null);
		}
		else {
			map.put("required", String.valueOf(formFieldContext.getRequired()));
		}

		if (formFieldContext.getValid() == null) {
			map.put("valid", null);
		}
		else {
			map.put("valid", String.valueOf(formFieldContext.getValid()));
		}

		if (formFieldContext.getValue() == null) {
			map.put("value", null);
		}
		else {
			map.put("value", String.valueOf(formFieldContext.getValue()));
		}

		if (formFieldContext.getValueChanged() == null) {
			map.put("valueChanged", null);
		}
		else {
			map.put(
				"valueChanged",
				String.valueOf(formFieldContext.getValueChanged()));
		}

		if (formFieldContext.getVisible() == null) {
			map.put("visible", null);
		}
		else {
			map.put("visible", String.valueOf(formFieldContext.getVisible()));
		}

		return map;
	}

	public static class FormFieldContextJSONParser
		extends BaseJSONParser<FormFieldContext> {

		@Override
		protected FormFieldContext createDTO() {
			return new FormFieldContext();
		}

		@Override
		protected FormFieldContext[] createDTOArray(int size) {
			return new FormFieldContext[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "evaluable")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "formFieldOptions")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "name")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "readOnly")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "required")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "valid")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "value")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "valueChanged")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "visible")) {
				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			FormFieldContext formFieldContext, String jsonParserFieldName,
			Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "evaluable")) {
				if (jsonParserFieldValue != null) {
					formFieldContext.setEvaluable(
						(Boolean)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "formFieldOptions")) {
				if (jsonParserFieldValue != null) {
					Object[] jsonParserFieldValues =
						(Object[])jsonParserFieldValue;

					FormFieldOption[] formFieldOptionsArray =
						new FormFieldOption[jsonParserFieldValues.length];

					for (int i = 0; i < formFieldOptionsArray.length; i++) {
						formFieldOptionsArray[i] = FormFieldOptionSerDes.toDTO(
							(String)jsonParserFieldValues[i]);
					}

					formFieldContext.setFormFieldOptions(formFieldOptionsArray);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "name")) {
				if (jsonParserFieldValue != null) {
					formFieldContext.setName((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "readOnly")) {
				if (jsonParserFieldValue != null) {
					formFieldContext.setReadOnly((Boolean)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "required")) {
				if (jsonParserFieldValue != null) {
					formFieldContext.setRequired((Boolean)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "valid")) {
				if (jsonParserFieldValue != null) {
					formFieldContext.setValid((Boolean)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "value")) {
				if (jsonParserFieldValue != null) {
					formFieldContext.setValue((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "valueChanged")) {
				if (jsonParserFieldValue != null) {
					formFieldContext.setValueChanged(
						(Boolean)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "visible")) {
				if (jsonParserFieldValue != null) {
					formFieldContext.setVisible((Boolean)jsonParserFieldValue);
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