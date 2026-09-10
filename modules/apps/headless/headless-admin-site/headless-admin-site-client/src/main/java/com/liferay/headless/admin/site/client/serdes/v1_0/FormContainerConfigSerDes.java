/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.site.client.serdes.v1_0;

import com.liferay.headless.admin.site.client.dto.v1_0.FormContainerConfig;
import com.liferay.headless.admin.site.client.json.BaseJSONParser;

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
public class FormContainerConfigSerDes {

	public static FormContainerConfig toDTO(String json) {
		FormContainerConfigJSONParser formContainerConfigJSONParser =
			new FormContainerConfigJSONParser();

		return formContainerConfigJSONParser.parseToDTO(json);
	}

	public static FormContainerConfig[] toDTOs(String json) {
		FormContainerConfigJSONParser formContainerConfigJSONParser =
			new FormContainerConfigJSONParser();

		return formContainerConfigJSONParser.parseToDTOs(json);
	}

	public static String toJSON(FormContainerConfig formContainerConfig) {
		if (formContainerConfig == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (formContainerConfig.getFormContainerReference() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"formContainerReference\": ");

			sb.append(
				String.valueOf(
					formContainerConfig.getFormContainerReference()));
		}

		if (formContainerConfig.getFormContainerType() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"formContainerType\": ");

			sb.append("\"");
			sb.append(formContainerConfig.getFormContainerType());
			sb.append("\"");
		}

		if (formContainerConfig.getLocalizationConfig() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"localizationConfig\": ");

			sb.append(
				String.valueOf(formContainerConfig.getLocalizationConfig()));
		}

		if (formContainerConfig.getNumberOfSteps() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"numberOfSteps\": ");

			sb.append(formContainerConfig.getNumberOfSteps());
		}

		if (formContainerConfig.getSuccessFormContainerSubmissionResult() !=
				null) {

			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"successFormContainerSubmissionResult\": ");

			sb.append(
				String.valueOf(
					formContainerConfig.
						getSuccessFormContainerSubmissionResult()));
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		FormContainerConfigJSONParser formContainerConfigJSONParser =
			new FormContainerConfigJSONParser();

		return formContainerConfigJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(
		FormContainerConfig formContainerConfig) {

		if (formContainerConfig == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (formContainerConfig.getFormContainerReference() == null) {
			map.put("formContainerReference", null);
		}
		else {
			map.put(
				"formContainerReference",
				String.valueOf(
					formContainerConfig.getFormContainerReference()));
		}

		if (formContainerConfig.getFormContainerType() == null) {
			map.put("formContainerType", null);
		}
		else {
			map.put(
				"formContainerType",
				String.valueOf(formContainerConfig.getFormContainerType()));
		}

		if (formContainerConfig.getLocalizationConfig() == null) {
			map.put("localizationConfig", null);
		}
		else {
			map.put(
				"localizationConfig",
				String.valueOf(formContainerConfig.getLocalizationConfig()));
		}

		if (formContainerConfig.getNumberOfSteps() == null) {
			map.put("numberOfSteps", null);
		}
		else {
			map.put(
				"numberOfSteps",
				String.valueOf(formContainerConfig.getNumberOfSteps()));
		}

		if (formContainerConfig.getSuccessFormContainerSubmissionResult() ==
				null) {

			map.put("successFormContainerSubmissionResult", null);
		}
		else {
			map.put(
				"successFormContainerSubmissionResult",
				String.valueOf(
					formContainerConfig.
						getSuccessFormContainerSubmissionResult()));
		}

		return map;
	}

	public static class FormContainerConfigJSONParser
		extends BaseJSONParser<FormContainerConfig> {

		@Override
		protected FormContainerConfig createDTO() {
			return new FormContainerConfig();
		}

		@Override
		protected FormContainerConfig[] createDTOArray(int size) {
			return new FormContainerConfig[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "formContainerReference")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "formContainerType")) {
				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "localizationConfig")) {

				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "numberOfSteps")) {
				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName,
						"successFormContainerSubmissionResult")) {

				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			FormContainerConfig formContainerConfig, String jsonParserFieldName,
			Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "formContainerReference")) {
				if (jsonParserFieldValue != null) {
					formContainerConfig.setFormContainerReference(
						FormContainerReferenceSerDes.toDTO(
							(String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "formContainerType")) {
				if (jsonParserFieldValue != null) {
					formContainerConfig.setFormContainerType(
						FormContainerConfig.FormContainerType.create(
							(String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "localizationConfig")) {

				if (jsonParserFieldValue != null) {
					formContainerConfig.setLocalizationConfig(
						LocalizationConfigSerDes.toDTO(
							(String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "numberOfSteps")) {
				if (jsonParserFieldValue != null) {
					formContainerConfig.setNumberOfSteps(
						Integer.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(
						jsonParserFieldName,
						"successFormContainerSubmissionResult")) {

				if (jsonParserFieldValue != null) {
					formContainerConfig.setSuccessFormContainerSubmissionResult(
						SuccessFormContainerSubmissionResultSerDes.toDTO(
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
// LIFERAY-REST-BUILDER-HASH:329910256