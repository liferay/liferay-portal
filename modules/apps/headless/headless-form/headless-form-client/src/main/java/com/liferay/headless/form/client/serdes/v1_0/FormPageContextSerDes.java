/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.form.client.serdes.v1_0;

import com.liferay.headless.form.client.dto.v1_0.FormFieldContext;
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
public class FormPageContextSerDes {

	public static FormPageContext toDTO(String json) {
		FormPageContextJSONParser formPageContextJSONParser =
			new FormPageContextJSONParser();

		return formPageContextJSONParser.parseToDTO(json);
	}

	public static FormPageContext[] toDTOs(String json) {
		FormPageContextJSONParser formPageContextJSONParser =
			new FormPageContextJSONParser();

		return formPageContextJSONParser.parseToDTOs(json);
	}

	public static String toJSON(FormPageContext formPageContext) {
		if (formPageContext == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (formPageContext.getEnabled() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"enabled\": ");

			sb.append(formPageContext.getEnabled());
		}

		if (formPageContext.getFormFieldContexts() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"formFieldContexts\": ");

			sb.append("[");

			for (int i = 0; i < formPageContext.getFormFieldContexts().length;
				 i++) {

				sb.append(
					String.valueOf(formPageContext.getFormFieldContexts()[i]));

				if ((i + 1) < formPageContext.getFormFieldContexts().length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		if (formPageContext.getShowRequiredFieldsWarning() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"showRequiredFieldsWarning\": ");

			sb.append(formPageContext.getShowRequiredFieldsWarning());
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		FormPageContextJSONParser formPageContextJSONParser =
			new FormPageContextJSONParser();

		return formPageContextJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(FormPageContext formPageContext) {
		if (formPageContext == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (formPageContext.getEnabled() == null) {
			map.put("enabled", null);
		}
		else {
			map.put("enabled", String.valueOf(formPageContext.getEnabled()));
		}

		if (formPageContext.getFormFieldContexts() == null) {
			map.put("formFieldContexts", null);
		}
		else {
			map.put(
				"formFieldContexts",
				String.valueOf(formPageContext.getFormFieldContexts()));
		}

		if (formPageContext.getShowRequiredFieldsWarning() == null) {
			map.put("showRequiredFieldsWarning", null);
		}
		else {
			map.put(
				"showRequiredFieldsWarning",
				String.valueOf(formPageContext.getShowRequiredFieldsWarning()));
		}

		return map;
	}

	public static class FormPageContextJSONParser
		extends BaseJSONParser<FormPageContext> {

		@Override
		protected FormPageContext createDTO() {
			return new FormPageContext();
		}

		@Override
		protected FormPageContext[] createDTOArray(int size) {
			return new FormPageContext[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "enabled")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "formFieldContexts")) {
				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "showRequiredFieldsWarning")) {

				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			FormPageContext formPageContext, String jsonParserFieldName,
			Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "enabled")) {
				if (jsonParserFieldValue != null) {
					formPageContext.setEnabled((Boolean)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "formFieldContexts")) {
				if (jsonParserFieldValue != null) {
					Object[] jsonParserFieldValues =
						(Object[])jsonParserFieldValue;

					FormFieldContext[] formFieldContextsArray =
						new FormFieldContext[jsonParserFieldValues.length];

					for (int i = 0; i < formFieldContextsArray.length; i++) {
						formFieldContextsArray[i] =
							FormFieldContextSerDes.toDTO(
								(String)jsonParserFieldValues[i]);
					}

					formPageContext.setFormFieldContexts(
						formFieldContextsArray);
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "showRequiredFieldsWarning")) {

				if (jsonParserFieldValue != null) {
					formPageContext.setShowRequiredFieldsWarning(
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