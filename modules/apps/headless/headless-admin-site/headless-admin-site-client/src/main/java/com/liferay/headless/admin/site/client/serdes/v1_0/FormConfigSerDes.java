/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.site.client.serdes.v1_0;

import com.liferay.headless.admin.site.client.dto.v1_0.FormConfig;
import com.liferay.headless.admin.site.client.json.BaseJSONParser;

import jakarta.annotation.Generated;

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
public class FormConfigSerDes {

	public static FormConfig toDTO(String json) {
		FormConfigJSONParser formConfigJSONParser = new FormConfigJSONParser();

		return formConfigJSONParser.parseToDTO(json);
	}

	public static FormConfig[] toDTOs(String json) {
		FormConfigJSONParser formConfigJSONParser = new FormConfigJSONParser();

		return formConfigJSONParser.parseToDTOs(json);
	}

	public static String toJSON(FormConfig formConfig) {
		if (formConfig == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (formConfig.getFormReference() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"formReference\": ");

			if (formConfig.getFormReference() instanceof String) {
				sb.append("\"");
				sb.append((String)formConfig.getFormReference());
				sb.append("\"");
			}
			else {
				sb.append(formConfig.getFormReference());
			}
		}

		if (formConfig.getFormType() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"formType\": ");

			sb.append("\"");
			sb.append(formConfig.getFormType());
			sb.append("\"");
		}

		if (formConfig.getNumberOfSteps() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"numberOfSteps\": ");

			sb.append(formConfig.getNumberOfSteps());
		}

		if (formConfig.getSuccessFormSubmissionResult() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"successFormSubmissionResult\": ");

			if (formConfig.getSuccessFormSubmissionResult() instanceof String) {
				sb.append("\"");
				sb.append((String)formConfig.getSuccessFormSubmissionResult());
				sb.append("\"");
			}
			else {
				sb.append(formConfig.getSuccessFormSubmissionResult());
			}
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		FormConfigJSONParser formConfigJSONParser = new FormConfigJSONParser();

		return formConfigJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(FormConfig formConfig) {
		if (formConfig == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (formConfig.getFormReference() == null) {
			map.put("formReference", null);
		}
		else {
			map.put(
				"formReference", String.valueOf(formConfig.getFormReference()));
		}

		if (formConfig.getFormType() == null) {
			map.put("formType", null);
		}
		else {
			map.put("formType", String.valueOf(formConfig.getFormType()));
		}

		if (formConfig.getNumberOfSteps() == null) {
			map.put("numberOfSteps", null);
		}
		else {
			map.put(
				"numberOfSteps", String.valueOf(formConfig.getNumberOfSteps()));
		}

		if (formConfig.getSuccessFormSubmissionResult() == null) {
			map.put("successFormSubmissionResult", null);
		}
		else {
			map.put(
				"successFormSubmissionResult",
				String.valueOf(formConfig.getSuccessFormSubmissionResult()));
		}

		return map;
	}

	public static class FormConfigJSONParser
		extends BaseJSONParser<FormConfig> {

		@Override
		protected FormConfig createDTO() {
			return new FormConfig();
		}

		@Override
		protected FormConfig[] createDTOArray(int size) {
			return new FormConfig[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "formReference")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "formType")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "numberOfSteps")) {
				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "successFormSubmissionResult")) {

				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			FormConfig formConfig, String jsonParserFieldName,
			Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "formReference")) {
				if (jsonParserFieldValue != null) {
					formConfig.setFormReference((Object)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "formType")) {
				if (jsonParserFieldValue != null) {
					formConfig.setFormType(
						FormConfig.FormType.create(
							(String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "numberOfSteps")) {
				if (jsonParserFieldValue != null) {
					formConfig.setNumberOfSteps(
						Integer.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "successFormSubmissionResult")) {

				if (jsonParserFieldValue != null) {
					formConfig.setSuccessFormSubmissionResult(
						(Object)jsonParserFieldValue);
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