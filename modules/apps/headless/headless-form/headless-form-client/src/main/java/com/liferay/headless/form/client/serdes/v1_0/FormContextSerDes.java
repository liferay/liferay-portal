/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.form.client.serdes.v1_0;

import com.liferay.headless.form.client.dto.v1_0.FormContext;
import com.liferay.headless.form.client.dto.v1_0.FormFieldValue;
import com.liferay.headless.form.client.dto.v1_0.FormPageContext;
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
public class FormContextSerDes {

	public static FormContext toDTO(String json) {
		FormContextJSONParser formContextJSONParser =
			new FormContextJSONParser();

		return formContextJSONParser.parseToDTO(json);
	}

	public static FormContext[] toDTOs(String json) {
		FormContextJSONParser formContextJSONParser =
			new FormContextJSONParser();

		return formContextJSONParser.parseToDTOs(json);
	}

	public static String toJSON(FormContext formContext) {
		if (formContext == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (formContext.getFormFieldValues() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"formFieldValues\": ");

			sb.append("[");

			for (int i = 0; i < formContext.getFormFieldValues().length; i++) {
				sb.append(String.valueOf(formContext.getFormFieldValues()[i]));

				if ((i + 1) < formContext.getFormFieldValues().length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		if (formContext.getFormPageContexts() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"formPageContexts\": ");

			sb.append("[");

			for (int i = 0; i < formContext.getFormPageContexts().length; i++) {
				sb.append(String.valueOf(formContext.getFormPageContexts()[i]));

				if ((i + 1) < formContext.getFormPageContexts().length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		if (formContext.getReadOnly() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"readOnly\": ");

			sb.append(formContext.getReadOnly());
		}

		if (formContext.getShowRequiredFieldsWarning() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"showRequiredFieldsWarning\": ");

			sb.append(formContext.getShowRequiredFieldsWarning());
		}

		if (formContext.getShowSubmitButton() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"showSubmitButton\": ");

			sb.append(formContext.getShowSubmitButton());
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		FormContextJSONParser formContextJSONParser =
			new FormContextJSONParser();

		return formContextJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(FormContext formContext) {
		if (formContext == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (formContext.getFormFieldValues() == null) {
			map.put("formFieldValues", null);
		}
		else {
			map.put(
				"formFieldValues",
				String.valueOf(formContext.getFormFieldValues()));
		}

		if (formContext.getFormPageContexts() == null) {
			map.put("formPageContexts", null);
		}
		else {
			map.put(
				"formPageContexts",
				String.valueOf(formContext.getFormPageContexts()));
		}

		if (formContext.getReadOnly() == null) {
			map.put("readOnly", null);
		}
		else {
			map.put("readOnly", String.valueOf(formContext.getReadOnly()));
		}

		if (formContext.getShowRequiredFieldsWarning() == null) {
			map.put("showRequiredFieldsWarning", null);
		}
		else {
			map.put(
				"showRequiredFieldsWarning",
				String.valueOf(formContext.getShowRequiredFieldsWarning()));
		}

		if (formContext.getShowSubmitButton() == null) {
			map.put("showSubmitButton", null);
		}
		else {
			map.put(
				"showSubmitButton",
				String.valueOf(formContext.getShowSubmitButton()));
		}

		return map;
	}

	public static class FormContextJSONParser
		extends BaseJSONParser<FormContext> {

		@Override
		protected FormContext createDTO() {
			return new FormContext();
		}

		@Override
		protected FormContext[] createDTOArray(int size) {
			return new FormContext[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "formFieldValues")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "formPageContexts")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "readOnly")) {
				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "showRequiredFieldsWarning")) {

				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "showSubmitButton")) {
				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			FormContext formContext, String jsonParserFieldName,
			Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "formFieldValues")) {
				if (jsonParserFieldValue != null) {
					Object[] jsonParserFieldValues =
						(Object[])jsonParserFieldValue;

					FormFieldValue[] formFieldValuesArray =
						new FormFieldValue[jsonParserFieldValues.length];

					for (int i = 0; i < formFieldValuesArray.length; i++) {
						formFieldValuesArray[i] = FormFieldValueSerDes.toDTO(
							(String)jsonParserFieldValues[i]);
					}

					formContext.setFormFieldValues(formFieldValuesArray);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "formPageContexts")) {
				if (jsonParserFieldValue != null) {
					Object[] jsonParserFieldValues =
						(Object[])jsonParserFieldValue;

					FormPageContext[] formPageContextsArray =
						new FormPageContext[jsonParserFieldValues.length];

					for (int i = 0; i < formPageContextsArray.length; i++) {
						formPageContextsArray[i] = FormPageContextSerDes.toDTO(
							(String)jsonParserFieldValues[i]);
					}

					formContext.setFormPageContexts(formPageContextsArray);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "readOnly")) {
				if (jsonParserFieldValue != null) {
					formContext.setReadOnly((Boolean)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "showRequiredFieldsWarning")) {

				if (jsonParserFieldValue != null) {
					formContext.setShowRequiredFieldsWarning(
						(Boolean)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "showSubmitButton")) {
				if (jsonParserFieldValue != null) {
					formContext.setShowSubmitButton(
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