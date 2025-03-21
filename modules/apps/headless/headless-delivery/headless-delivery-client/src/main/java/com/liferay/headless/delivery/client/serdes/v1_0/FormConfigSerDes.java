/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.delivery.client.serdes.v1_0;

import com.liferay.headless.delivery.client.dto.v1_0.FormConfig;
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

		if (formConfig.getFormSuccessSubmissionResult() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"formSuccessSubmissionResult\": ");

			if (formConfig.getFormSuccessSubmissionResult() instanceof String) {
				sb.append("\"");
				sb.append((String)formConfig.getFormSuccessSubmissionResult());
				sb.append("\"");
			}
			else {
				sb.append(formConfig.getFormSuccessSubmissionResult());
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

		if (formConfig.getLocalizationConfig() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"localizationConfig\": ");

			sb.append(String.valueOf(formConfig.getLocalizationConfig()));
		}

		if (formConfig.getNumberOfSteps() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"numberOfSteps\": ");

			sb.append(formConfig.getNumberOfSteps());
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

		if (formConfig.getFormSuccessSubmissionResult() == null) {
			map.put("formSuccessSubmissionResult", null);
		}
		else {
			map.put(
				"formSuccessSubmissionResult",
				String.valueOf(formConfig.getFormSuccessSubmissionResult()));
		}

		if (formConfig.getFormType() == null) {
			map.put("formType", null);
		}
		else {
			map.put("formType", String.valueOf(formConfig.getFormType()));
		}

		if (formConfig.getLocalizationConfig() == null) {
			map.put("localizationConfig", null);
		}
		else {
			map.put(
				"localizationConfig",
				String.valueOf(formConfig.getLocalizationConfig()));
		}

		if (formConfig.getNumberOfSteps() == null) {
			map.put("numberOfSteps", null);
		}
		else {
			map.put(
				"numberOfSteps", String.valueOf(formConfig.getNumberOfSteps()));
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
			else if (Objects.equals(
						jsonParserFieldName, "formSuccessSubmissionResult")) {

				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "formType")) {
				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "localizationConfig")) {

				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "numberOfSteps")) {
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
			else if (Objects.equals(
						jsonParserFieldName, "formSuccessSubmissionResult")) {

				if (jsonParserFieldValue != null) {
					formConfig.setFormSuccessSubmissionResult(
						(Object)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "formType")) {
				if (jsonParserFieldValue != null) {
					formConfig.setFormType(
						FormConfig.FormType.create(
							(String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "localizationConfig")) {

				if (jsonParserFieldValue != null) {
					formConfig.setLocalizationConfig(
						LocalizationConfigSerDes.toDTO(
							(String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "numberOfSteps")) {
				if (jsonParserFieldValue != null) {
					formConfig.setNumberOfSteps(
						Integer.valueOf((String)jsonParserFieldValue));
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